package com.novacloudedu.backend.infrastructure.ai.agent;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.novacloudedu.backend.infrastructure.ai.agent.PptAgentOrchestrator.EvaluationResult;
import com.novacloudedu.backend.infrastructure.ai.agent.PptAgentOrchestrator.OutlineResult;
import com.novacloudedu.backend.infrastructure.ai.agent.PptAgentOrchestrator.SlideFeedback;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PPT Agent JSON 解析工具类
 *
 * 提供 JSON 解析、清理、提取等工具方法，用于处理 Agent 输出。
 */
@Slf4j
@UtilityClass
public class PptAgentJsonUtils {

    private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 从文本中提取 JSON（支持 markdown code fence 包裹）
     *
     * @param text 包含 JSON 的文本
     * @return 提取的 JSON 字符串（已清理），如果无法提取则返回原文本
     */
    public String extractJsonFromText(String text) {
        if (text == null || text.isBlank()) {
            return text;
        }

        String cleaned = text.trim();

        // 清理 markdown code fence
        if (cleaned.startsWith("```")) {
            int firstNewLine = cleaned.indexOf('\n');
            if (firstNewLine > 0) {
                cleaned = cleaned.substring(firstNewLine + 1);
            }
            if (cleaned.endsWith("```")) {
                cleaned = cleaned.substring(0, cleaned.length() - 3);
            }
            cleaned = cleaned.trim();
        }

        // 尝试提取 JSON 对象（从第一个 { 到最后一个 }）
        int start = cleaned.indexOf('{');
        int end = cleaned.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return cleaned.substring(start, end + 1);
        }

        return cleaned;
    }

    /**
     * 解析大纲结果
     *
     * 从 PlannerAgent 的输出中分离研究摘要和大纲内容。
     *
     * @param rawResult PlannerAgent 的原始输出
     * @return OutlineResult 包含大纲和研究摘要
     */
    public OutlineResult parseOutlineResult(String rawResult) {
        if (rawResult == null || rawResult.isBlank()) {
            log.warn("parseOutlineResult: rawResult is null or blank");
            return new OutlineResult("", "");
        }

        // 分离研究摘要和大纲
        String researchSummary = "";
        String outlineMarkdown = rawResult;

        // Try English markers first (matching PlannerAgent output format)
        int outlineIdx = rawResult.indexOf("## Presentation Outline");
        if (outlineIdx == -1) {
            outlineIdx = rawResult.indexOf("## PPT大纲");
        }
        if (outlineIdx == -1) {
            outlineIdx = rawResult.indexOf("## PPT 大纲");
        }

        if (outlineIdx > 0) {
            researchSummary = rawResult.substring(0, outlineIdx).trim();
            // Remove the heading line itself
            String headingLine = rawResult.substring(outlineIdx).split("\n", 2)[0];
            outlineMarkdown = rawResult.substring(outlineIdx + headingLine.length()).trim();
            // Remove research summary heading prefix
            if (researchSummary.startsWith("## Research Summary")) {
                researchSummary = researchSummary.substring("## Research Summary".length()).trim();
            } else if (researchSummary.startsWith("## 研究摘要")) {
                researchSummary = researchSummary.substring("## 研究摘要".length()).trim();
            }
        }

        return new OutlineResult(outlineMarkdown, researchSummary);
    }

    /**
     * 安全解析幻灯片配置 JSON
     *
     * 支持清理 markdown code fence 和提取 JSON 对象。
     *
     * @param jsonStr JSON 字符串
     * @return 幻灯片配置 Map，解析失败返回空 Map
     */
    public Map<String, Object> parseSlideConfigSafe(String jsonStr) {
        return parseSlideConfigSafe(jsonStr, OBJECT_MAPPER);
    }

    /**
     * 安全解析幻灯片配置 JSON
     *
     * 支持清理 markdown code fence 和提取 JSON 对象。
     *
     * @param jsonStr      JSON 字符串
     * @param objectMapper Jackson ObjectMapper
     * @return 幻灯片配置 Map，解析失败返回空 Map
     */
    public Map<String, Object> parseSlideConfigSafe(String jsonStr, ObjectMapper objectMapper) {
        if (jsonStr == null || jsonStr.isBlank()) {
            log.warn("parseSlideConfigSafe: jsonStr is null or blank");
            return new HashMap<>();
        }

        try {
            String cleaned = extractJsonFromText(jsonStr);
            return objectMapper.readValue(cleaned, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.warn("Agent输出JSON解析失败，尝试提取: {}", e.getMessage());
            // 尝试找到 JSON 对象
            int start = jsonStr.indexOf('{');
            int end = jsonStr.lastIndexOf('}');
            if (start >= 0 && end > start) {
                try {
                    return objectMapper.readValue(
                            jsonStr.substring(start, end + 1), new TypeReference<Map<String, Object>>() {});
                } catch (Exception ex) {
                    log.error("JSON提取也失败", ex);
                }
            }
            return new HashMap<>();
        }
    }

    /**
     * 安全解析 JSON 为 JsonNode
     *
     * 支持清理 markdown code fence 和提取 JSON 对象。
     *
     * @param jsonStr JSON 字符串
     * @return JsonNode，解析失败返回 null
     */
    public JsonNode parseJsonSafe(String jsonStr) {
        return parseJsonSafe(jsonStr, OBJECT_MAPPER);
    }

    /**
     * 安全解析 JSON 为 JsonNode
     *
     * 支持清理 markdown code fence 和提取 JSON 对象。
     *
     * @param jsonStr      JSON 字符串
     * @param objectMapper Jackson ObjectMapper
     * @return JsonNode，解析失败返回 null
     */
    public JsonNode parseJsonSafe(String jsonStr, ObjectMapper objectMapper) {
        if (jsonStr == null || jsonStr.isBlank()) {
            log.warn("parseJsonSafe: jsonStr is null or blank");
            return null;
        }

        try {
            String cleaned = extractJsonFromText(jsonStr);
            return objectMapper.readTree(cleaned);
        } catch (Exception e) {
            log.warn("JSON解析失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 解析评估结果
     *
     * 从 EvaluatorAgent 的输出中解析质量评估结果。
     *
     * @param rawResult EvaluatorAgent 的原始输出
     * @return EvaluationResult 评估结果，解析失败返回默认结果
     */
    public EvaluationResult parseEvaluationResult(String rawResult) {
        return parseEvaluationResult(rawResult, OBJECT_MAPPER);
    }

    /**
     * 解析评估结果
     *
     * 从 EvaluatorAgent 的输出中解析质量评估结果。
     *
     * @param rawResult    EvaluatorAgent 的原始输出
     * @param objectMapper Jackson ObjectMapper
     * @return EvaluationResult 评估结果，解析失败返回默认结果
     */
    public EvaluationResult parseEvaluationResult(String rawResult, ObjectMapper objectMapper) {
        JsonNode json = parseJsonSafe(rawResult, objectMapper);
        if (json == null) {
            return EvaluationResult.defaultResult();
        }

        try {
            int overall = json.path("overall_score").asInt(70);
            int content = json.path("content_score").asInt(70);
            int design = json.path("design_score").asInt(70);
            int coherence = json.path("coherence_score").asInt(70);

            List<String> strengths = new ArrayList<>();
            json.path("strengths").forEach(n -> strengths.add(n.asText()));

            List<String> weaknesses = new ArrayList<>();
            json.path("weaknesses").forEach(n -> weaknesses.add(n.asText()));

            List<String> suggestions = new ArrayList<>();
            json.path("improvement_suggestions").forEach(n -> suggestions.add(n.asText()));

            // 解析每页评分
            List<SlideFeedback> slideFeedbacks = new ArrayList<>();
            JsonNode slideLevelNode = json.path("slide_level_feedback");
            if (slideLevelNode.isArray()) {
                for (JsonNode sfNode : slideLevelNode) {
                    slideFeedbacks.add(new SlideFeedback(
                            sfNode.path("slide_index").asInt(0),
                            sfNode.path("score").asInt(70),
                            sfNode.path("feedback").asText("")));
                }
            }

            return new EvaluationResult(overall, content, design, coherence,
                    strengths, weaknesses, suggestions, slideFeedbacks);
        } catch (Exception e) {
            log.warn("评估结果解析失败", e);
            return EvaluationResult.defaultResult();
        }
    }

    /**
     * 从幻灯片配置列表中提取预览图 URL
     *
     * @param slides 幻灯片配置列表
     * @return 预览图 URL 列表，无预览图的位置为 null
     */
    public List<String> extractPreviewImageUrls(List<Map<String, Object>> slides) {
        List<String> urls = new ArrayList<>();
        for (Map<String, Object> slide : slides) {
            Object url = slide.get("previewImageUrl");
            urls.add(url instanceof String s && !s.isBlank() ? s : null);
        }
        return urls;
    }
}
