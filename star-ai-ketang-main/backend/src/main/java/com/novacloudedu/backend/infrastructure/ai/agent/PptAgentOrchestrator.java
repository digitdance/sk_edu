package com.novacloudedu.backend.infrastructure.ai.agent;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.novacloudedu.backend.config.PptAgentProperties;
import com.novacloudedu.backend.infrastructure.ai.ChatModelFactory;
import com.novacloudedu.backend.infrastructure.ai.LangchainChatService;
import com.novacloudedu.backend.infrastructure.ai.agent.PptAgentInterfaces.*;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.BiConsumer;

/**
 * PPT 多 Agent 编排器
 *
 * 协调 PlannerAgent、ContentAgent、DesignAgent、EvaluatorAgent 完成 PPT 生成流程。
 * 支持并发幻灯片生成和质量评估。
 *
 * 架构参考 PPTAgent 项目的两阶段方法：
 * Stage 1: 分析与规划（Planner + Research）
 * Stage 2: 内容生成与设计优化（Content + Design，并发执行）
 * Stage 3: 质量评估与反思（Evaluator）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PptAgentOrchestrator {

    private final ChatModelFactory chatModelFactory;
    private final LangchainChatService langchainChatService;
    private final WebSearchTool webSearchTool;
    private final ImageGenerationTool imageGenerationTool;
    private final ContentResearchTool contentResearchTool;
    private final com.novacloudedu.backend.infrastructure.ppt.PptServiceClient pptServiceClient;
    private final ObjectMapper objectMapper;
    private final PptAgentProperties agentProperties;
    private final PptOutlinePlanner pptOutlinePlanner;
    private final PptSlideFactory pptSlideFactory;
    private final PptEvaluator pptEvaluator;
    private final PptReflector pptReflector;

    /** 并发生成用线程池 */
    private ExecutorService slideExecutor;

    @PostConstruct
    public void init() {
        slideExecutor = Executors.newFixedThreadPool(
                agentProperties.concurrency(), Thread.ofVirtual().name("ppt-agent-", 0).factory());
        // 将线程池注入到 PptReflector（用于并发修复）
        pptReflector.setSlideExecutor(slideExecutor);
        log.info("PPT Agent Orchestrator 初始化: plannerModel={}, contentModel={}, designVisionModel={}, designToolModel={}, concurrency={}",
                agentProperties.plannerModel(), agentProperties.contentModel(),
                agentProperties.designModel(), agentProperties.designToolModel(),
                agentProperties.concurrency());
    }

    // ==================== Stage 1: 规划大纲（带联网搜索） ====================

    /**
     * Agent 驱动的大纲规划
     *
     * @param topic        PPT 主题
     * @param requirements 额外要求
     * @param templateInfo 模板结构描述（可选）
     * @return 结构化的大纲结果
     */
    public OutlineResult planOutline(String topic, String requirements, String templateInfo) {
        return pptOutlinePlanner.planOutline(topic, requirements, templateInfo);
    }

    /**
     * Agent 驱动的大纲规划（带任务跟踪）
     */
    public OutlineResult planOutline(String topic, String requirements,
                                      String templateInfo, AgentTaskTracker tracker) {
        return pptOutlinePlanner.planOutline(topic, requirements, templateInfo, tracker);
    }

    // ==================== Stage 2: 并发生成幻灯片内容 ====================

    /**
     * Agent 驱动的并发幻灯片生成
     */

    public List<Map<String, Object>> generateSlidesParallel(
            List<String> sections,
            String templateInfo,
            BiConsumer<Integer, Map<String, Object>> progressCallback) {
        return pptSlideFactory.generateSlidesParallel(sections, templateInfo, progressCallback);
    }

    /**
     * Agent 驱动的并发幻灯片生成（带任务跟踪 + 状态回调）
     */
    public List<Map<String, Object>> generateSlidesParallel(
            List<String> sections,
            String templateInfo,
            BiConsumer<Integer, Map<String, Object>> progressCallback,
            AgentTaskTracker tracker) {
        return pptSlideFactory.generateSlidesParallel(sections, templateInfo, progressCallback, tracker);
    }

    /**
     * Agent 驱动的并发幻灯片生成（带任务跟踪 + 状态回调）
     */
    public List<Map<String, Object>> generateSlidesParallel(
            List<String> sections,
            String templateInfo,
            BiConsumer<Integer, Map<String, Object>> progressCallback,
            AgentTaskTracker tracker,
            PptReflector.SlideStatusCallback slideStatusCallback) {
        return pptSlideFactory.generateSlidesParallel(sections, templateInfo, progressCallback, tracker, slideStatusCallback);
    }

    /**
     * Agent 驱动的并发幻灯片生成（完整参数版，含 templateUrl 用于内容校验）
     */
    public List<Map<String, Object>> generateSlidesParallel(
            List<String> sections,
            String templateInfo,
            String templateUrl,
            BiConsumer<Integer, Map<String, Object>> progressCallback,
            AgentTaskTracker tracker,
            PptReflector.SlideStatusCallback slideStatusCallback) {
        return pptSlideFactory.generateSlidesParallel(sections, templateInfo, templateUrl, progressCallback, tracker, slideStatusCallback);
    }


    // ==================== Stage 3: 质量评估 ====================

    /**
     * Agent 驱动的 PPT 质量评估
     *
     * @param slides  所有幻灯片配置
     * @param outline 原始大纲
     * @return 评估结果 JSON
     */
    public EvaluationResult evaluatePresentation(List<Map<String, Object>> slides, String outline) {
        return pptEvaluator.evaluatePresentation(slides, outline);
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
        return pptEvaluator.evaluatePresentationWithVision(slides, outline, previewImageUrls);
    }

    // ==================== 反思修复循环 ====================

    /**
     * 带反思修复循环的完整幻灯片生成
     *
     * 流程：
     * 1. 并发生成所有幻灯片（Stage 2）
     * 2. 质量评估（Stage 3）
     * 3. 如果整体评分 < qualityThreshold 或存在低分页面：
     *    a. 识别低分页面（slide_score < slideScoreThreshold）
     *    b. 仅对低分页面重新生成（带上评估反馈）
     *    c. 重新评估
     *    d. 重复直到达标或达到最大修复轮次
     *
     * @param sections         大纲章节列表
     * @param templateInfo     模板结构描述
     * @param outline          完整大纲（用于评估）
     * @param progressCallback 生成进度回调 (slideIndex, slideConfig)
     * @param repairCallback   修复进度回调（可为null）
     * @return 包含最终幻灯片列表和评估结果的 GenerationWithEvalResult
     */

    public GenerationWithEvalResult generateWithReflection(
            List<String> sections,
            String templateInfo,
            String outline,
            BiConsumer<Integer, Map<String, Object>> progressCallback,
            PptReflector.RepairProgressCallback repairCallback) {
        return pptReflector.generateWithReflection(sections, templateInfo, outline, progressCallback, repairCallback);
    }

    /**
     * 带反思修复循环的完整幻灯片生成（带任务跟踪 + 状态回调）
     */
    public GenerationWithEvalResult generateWithReflection(
            List<String> sections,
            String templateInfo,
            String outline,
            BiConsumer<Integer, Map<String, Object>> progressCallback,
            PptReflector.RepairProgressCallback repairCallback,
            AgentTaskTracker tracker) {
        return pptReflector.generateWithReflection(sections, templateInfo, outline, progressCallback, repairCallback, tracker);
    }

    /**
     * 带反思修复循环的完整幻灯片生成（完整参数版）
     */
    public GenerationWithEvalResult generateWithReflection(
            List<String> sections,
            String templateInfo,
            String outline,
            BiConsumer<Integer, Map<String, Object>> progressCallback,
            PptReflector.RepairProgressCallback repairCallback,
            AgentTaskTracker tracker,
            PptReflector.SlideStatusCallback slideStatusCallback) {
        return pptReflector.generateWithReflection(sections, templateInfo, outline, progressCallback, repairCallback, tracker, slideStatusCallback);
    }

    /**
     * 带反思修复循环的完整幻灯片生成（含 templateUrl 用于内容校验）
     */
    public GenerationWithEvalResult generateWithReflection(
            List<String> sections,
            String templateInfo,
            String outline,
            String templateUrl,
            BiConsumer<Integer, Map<String, Object>> progressCallback,
            PptReflector.RepairProgressCallback repairCallback,
            AgentTaskTracker tracker,
            PptReflector.SlideStatusCallback slideStatusCallback) {
        return pptReflector.generateWithReflection(sections, templateInfo, outline, templateUrl, progressCallback, repairCallback, tracker, slideStatusCallback);
    }



    // ==================== HTML 模式生成 ====================

    /**
     * HTML 模式：并发生成 HTML 幻灯片
     */
    public List<Map<String, Object>> generateHtmlSlidesParallel(
            List<String> sections,
            String userStyleHint,
            BiConsumer<Integer, Map<String, Object>> progressCallback,
            AgentTaskTracker tracker,
            PptReflector.SlideStatusCallback slideStatusCallback) {
        return pptSlideFactory.generateHtmlSlidesParallel(sections, userStyleHint, progressCallback, tracker, slideStatusCallback);
    }

    /**
     * HTML 模式：带反思修复的幻灯片生成
     */
    public GenerationWithEvalResult generateWithReflectionHtml(
            List<String> sections,
            String outline,
            String userStyleHint,
            BiConsumer<Integer, Map<String, Object>> progressCallback,
            PptReflector.RepairProgressCallback repairCallback,
            AgentTaskTracker tracker,
            PptReflector.SlideStatusCallback slideStatusCallback) {
        return pptReflector.generateWithReflectionHtml(sections, outline, userStyleHint, progressCallback, repairCallback, tracker, slideStatusCallback);
    }


    // ==================== 数据类型 ====================

    public record OutlineResult(
            String outlineMarkdown,
            String researchSummary
    ) {}

    public record SlideFeedback(
            int slideIndex,
            int score,
            String feedback
    ) {}

    public record EvaluationResult(
            int overallScore,
            int contentScore,
            int designScore,
            int coherenceScore,
            List<String> strengths,
            List<String> weaknesses,
            List<String> suggestions,
            List<SlideFeedback> slideFeedbacks
    ) {
        public static EvaluationResult defaultResult() {
            return new EvaluationResult(
                    70, 70, 70, 70,
                    List.of("内容完整"), List.of("待评估"), List.of("待优化"),
                    List.of());
        }
    }

    /**
     * 带反思修复循环的完整生成结果
     */
    public record GenerationWithEvalResult(
            List<Map<String, Object>> slides,
            EvaluationResult evaluation,
            int repairRounds
    ) {}
}
