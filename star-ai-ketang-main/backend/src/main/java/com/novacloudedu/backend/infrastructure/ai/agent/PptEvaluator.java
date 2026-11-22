package com.novacloudedu.backend.infrastructure.ai.agent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.novacloudedu.backend.config.PptAgentProperties;
import com.novacloudedu.backend.infrastructure.ai.ChatModelFactory;
import com.novacloudedu.backend.infrastructure.ai.LangchainChatService;
import com.novacloudedu.backend.infrastructure.ai.agent.PptAgentInterfaces.EvaluatorAgent;
import com.novacloudedu.backend.infrastructure.ai.agent.PptAgentOrchestrator.EvaluationResult;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * PPT 质量评估器（Stage 3）
 *
 * 负责对生成的幻灯片进行质量评估，包括：
 * - 整体质量评估（文本评估）
 * - 多模态视觉评估（基于渲染后的预览图）
 * - 逐页评分与反馈生成
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PptEvaluator {

    private final ChatModelFactory chatModelFactory;
    private final LangchainChatService langchainChatService;
    private final PptAgentProperties agentProperties;
    private final ObjectMapper objectMapper;

    /**
     * Agent 驱动的 PPT 质量评估
     *
     * @param slides  所有幻灯片配置
     * @param outline 原始大纲
     * @return 评估结果 JSON
     */
    public EvaluationResult evaluatePresentation(List<Map<String, Object>> slides, String outline) {
        log.info("Stage 3 - EvaluatorAgent 开始质量评估: {} 页", slides.size());

        EvaluatorAgent evaluator = buildEvaluatorAgent();

        String input = buildEvaluationInput(slides, outline);

        try {
            String result = evaluator.evaluate(input);
            return PptAgentJsonUtils.parseEvaluationResult(result);
        } catch (Exception e) {
            log.error("质量评估失败", e);
            return EvaluationResult.defaultResult();
        }
    }

    /**
     * 多模态视觉评估 — 通过视觉模型查看渲染后的幻灯片 PNG 图片进行审查
     *
     * @param slides          所有幻灯片配置
     * @param outline         原始大纲
     * @param previewImageUrls 每页渲染后的预览图 URL 列表（与 slides 索引对应）
     * @return 包含视觉审查的评估结果
     */
    public EvaluationResult evaluatePresentationWithVision(
            List<Map<String, Object>> slides, String outline, List<String> previewImageUrls) {

        log.info("Stage 3 - 多模态视觉评估: {} 页, {} 张预览图",
                slides.size(), previewImageUrls != null ? previewImageUrls.stream().filter(u -> u != null && !u.isBlank()).count() : 0);

        // 收集有效的预览图 URL
        List<String> validImageUrls = new ArrayList<>();
        if (previewImageUrls != null) {
            for (String url : previewImageUrls) {
                if (url != null && !url.isBlank()) {
                    validImageUrls.add(url);
                }
            }
        }

        // 如果没有预览图，降级为纯文本评估
        if (validImageUrls.isEmpty()) {
            log.warn("无预览图可用，降级为纯文本评估");
            return evaluatePresentation(slides, outline);
        }

        String systemPrompt = """
                You are a professional presentation quality reviewer, expert at evaluating slides from both visual and content perspectives.
                You will receive rendered screenshots and content configurations for each slide. Examine every image carefully and evaluate:

                1. **Content Quality** (content_score): information accuracy, professional depth, data support, text conciseness
                2. **Visual Design** (design_score): layout aesthetics, color harmony, image-text ratio, whitespace balance, font readability
                3. **Structural Coherence** (coherence_score): logical flow between pages, topic consistency, information progression

                For each slide, provide an individual score and specific feedback. Pay special attention to:
                - Template placeholder text remnants (e.g., "Click to add title", "Type here") or overlap with AI-generated content
                - Text overflowing or being clipped by text boxes
                - Images that are blurry, distorted, or mismatched with content
                - Layout that is too crowded or too sparse
                - Color scheme harmony

                If template placeholder text remnants or content overlap are found, that slide's score MUST be below 50.

                Output pure JSON:
                {
                    "overall_score": 0-100,
                    "content_score": 0-100,
                    "design_score": 0-100,
                    "coherence_score": 0-100,
                    "strengths": ["strength 1", "strength 2"],
                    "weaknesses": ["weakness 1", "weakness 2"],
                    "improvement_suggestions": ["suggestion 1", "suggestion 2"],
                    "slide_level_feedback": [
                        {"slide_index": 0, "score": 75, "feedback": "specific issue description"},
                        {"slide_index": 1, "score": 60, "feedback": "specific issue description"}
                    ]
                }
                """;

        // Build user message: text description + all preview images
        StringBuilder userMsg = new StringBuilder();
        userMsg.append("Below are all rendered slide screenshots and their content configurations. Review each page and provide evaluation.\n\n");
        userMsg.append("## Original Outline\n").append(outline).append("\n\n");
        userMsg.append("## Slide Content (").append(slides.size()).append(" slides)\n\n");

        for (int i = 0; i < slides.size(); i++) {
            userMsg.append("### Slide ").append(i + 1);
            if (i < validImageUrls.size() && validImageUrls.get(i) != null) {
                userMsg.append(" (see corresponding image)");
            }
            userMsg.append("\n");
            try {
                userMsg.append(objectMapper.writeValueAsString(slides.get(i)));
            } catch (Exception e) {
                userMsg.append("(serialization failed)");
            }
            userMsg.append("\n\n");
        }

        userMsg.append("Carefully examine all slide screenshots. Evaluate the visual quality and content of each page. Output pure JSON.");

        try {
            String result = langchainChatService.chatWithImages(
                    agentProperties.designModel(), systemPrompt, userMsg.toString(), validImageUrls);
            log.info("多模态视觉评估完成, 结果长度={}", result.length());
            return PptAgentJsonUtils.parseEvaluationResult(result, objectMapper);
        } catch (Exception e) {
            log.error("多模态视觉评估失败，降级为纯文本评估", e);
            return evaluatePresentation(slides, outline);
        }
    }

    /**
     * 构建 EvaluatorAgent 实例
     */
    private EvaluatorAgent buildEvaluatorAgent() {
        String modelId = agentProperties.evaluatorModel() != null
            ? agentProperties.evaluatorModel()
            : chatModelFactory.getDefaultModelId();
        ChatModel model = chatModelFactory.createChatModelWithParams(
                modelId, 0.3, 0.8, 4096);
        return AiServices.builder(EvaluatorAgent.class)
                .chatModel(model)
                .build();
    }

    /**
     * 构建评估输入文本
     */
    private String buildEvaluationInput(List<Map<String, Object>> slides, String outline) {
        StringBuilder sb = new StringBuilder();
        sb.append("## Original Outline\n").append(outline).append("\n\n");
        sb.append("## Generated Slide Content (").append(slides.size()).append(" slides)\n\n");

        for (int i = 0; i < slides.size(); i++) {
            try {
                sb.append("### Slide ").append(i + 1).append("\n");
                sb.append(objectMapper.writeValueAsString(slides.get(i)));
                sb.append("\n\n");
            } catch (Exception e) {
                sb.append("(parse failed)\n\n");
            }
        }

        sb.append("Please perform a comprehensive quality evaluation of this presentation. Output pure JSON.");
        return sb.toString();
    }
}
