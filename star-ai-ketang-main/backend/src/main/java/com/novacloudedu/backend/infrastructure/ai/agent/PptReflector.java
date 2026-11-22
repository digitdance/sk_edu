package com.novacloudedu.backend.infrastructure.ai.agent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.novacloudedu.backend.config.PptAgentProperties;
import com.novacloudedu.backend.infrastructure.ai.ChatModelFactory;
import com.novacloudedu.backend.infrastructure.ai.LangchainChatService;
import com.novacloudedu.backend.infrastructure.ai.agent.PptAgentInterfaces.*;
import com.novacloudedu.backend.infrastructure.ai.agent.PptAgentOrchestrator.EvaluationResult;
import com.novacloudedu.backend.infrastructure.ai.agent.PptAgentOrchestrator.GenerationWithEvalResult;
import com.novacloudedu.backend.infrastructure.ai.agent.PptAgentOrchestrator.SlideFeedback;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.BiConsumer;

/**
 * PPT Agent Stage 3b: 反思与修复器
 *
 * 负责根据评估反馈进行反思性修复，提升低质量幻灯片。
 * 借鉴 PPTAgent V2 (DeepPresenter) 的 reflection-repair loop 设计。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PptReflector {

    private final ChatModelFactory chatModelFactory;
    private final LangchainChatService langchainChatService;
    private final ImageGenerationTool imageGenerationTool;
    private final WebSearchTool webSearchTool;
    private final ContentResearchTool contentResearchTool;
    private final com.novacloudedu.backend.infrastructure.ppt.PptServiceClient pptServiceClient;
    private final ObjectMapper objectMapper;
    private final PptAgentProperties agentProperties;

    /** 并发生成用线程池（需从 orchestrator 传入或共享） */
    private ExecutorService slideExecutor;

    /**
     * 设置线程池（从 orchestrator 注入）
     */
    public void setSlideExecutor(ExecutorService executor) {
        this.slideExecutor = executor;
    }

    // ==================== Stage 3b: 反思修复循环 ====================

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
            RepairProgressCallback repairCallback) {
        return generateWithReflection(sections, templateInfo, outline,
                progressCallback, repairCallback, null, null);
    }

    /**
     * 带反思修复循环的完整幻灯片生成（带任务跟踪 + 状态回调）
     */
    public GenerationWithEvalResult generateWithReflection(
            List<String> sections,
            String templateInfo,
            String outline,
            BiConsumer<Integer, Map<String, Object>> progressCallback,
            RepairProgressCallback repairCallback,
            AgentTaskTracker tracker) {
        return generateWithReflection(sections, templateInfo, outline,
                progressCallback, repairCallback, tracker, null);
    }

    /**
     * 带反思修复循环的完整幻灯片生成（完整参数版）
     */
    public GenerationWithEvalResult generateWithReflection(
            List<String> sections,
            String templateInfo,
            String outline,
            BiConsumer<Integer, Map<String, Object>> progressCallback,
            RepairProgressCallback repairCallback,
            AgentTaskTracker tracker,
            SlideStatusCallback slideStatusCallback) {
        return generateWithReflection(sections, templateInfo, outline, null,
                progressCallback, repairCallback, tracker, slideStatusCallback);
    }

    /**
     * 带反思修复循环的完整幻灯片生成（含 templateUrl 用于内容校验）
     *
     * NOTE: 此方法当前依赖 PptAgentOrchestrator 的 generateSlidesParallel 和 evaluatePresentationWithVision
     * 待 Task 3 (PptSlideFactory) 和 Task 4 (PptEvaluator) 完成后，将改为注入这些组件
     */
    public GenerationWithEvalResult generateWithReflection(
            List<String> sections,
            String templateInfo,
            String outline,
            String templateUrl,
            BiConsumer<Integer, Map<String, Object>> progressCallback,
            RepairProgressCallback repairCallback,
            AgentTaskTracker tracker,
            SlideStatusCallback slideStatusCallback) {

        throw new UnsupportedOperationException(
                "generateWithReflection requires PptSlideFactory and PptEvaluator dependencies. " +
                "This will be implemented after Task 3 and Task 4 are completed.");
    }

    /**
     * HTML 模式：带反思修复循环的幻灯片生成
     * TODO: Implement HTML mode reflection generation
     */
    public GenerationWithEvalResult generateWithReflectionHtml(
            List<String> sections,
            String outline,
            String userStyleHint,
            BiConsumer<Integer, Map<String, Object>> progressCallback,
            RepairProgressCallback repairCallback,
            AgentTaskTracker tracker,
            SlideStatusCallback slideStatusCallback) {

        throw new UnsupportedOperationException(
                "generateWithReflectionHtml is not yet implemented. " +
                "HTML mode with reflection requires additional implementation.");
    }

    /**
     * 并发修复低分幻灯片
     *
     * @param allSlides         所有幻灯片配置（会被原地修改）
     * @param sections          大纲章节列表
     * @param templateInfo      模板结构描述
     * @param templateUrl       模板 URL（用于内容校验）
     * @param weakIndexes       需要修复的页面索引列表
     * @param prevEval          上一轮评估结果
     * @param repairRound       当前修复轮次
     * @param progressCallback  进度回调
     * @param repairCallback    修复进度回调
     * @param tracker           任务跟踪器
     * @param slideStatusCallback 状态回调
     */
    public void repairWeakSlides(
            List<Map<String, Object>> allSlides,
            List<String> sections,
            String templateInfo,
            String templateUrl,
            List<Integer> weakIndexes,
            EvaluationResult prevEval,
            int repairRound,
            BiConsumer<Integer, Map<String, Object>> progressCallback,
            RepairProgressCallback repairCallback,
            AgentTaskTracker tracker,
            SlideStatusCallback slideStatusCallback) {

        int totalSlides = sections.size();
        Map<Integer, Map<String, Object>> repairedMap = new ConcurrentHashMap<>();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        String feedbackSummary = buildRepairFeedback(prevEval, weakIndexes);

        // 预构建 Agent 实例，修复任务复用
        ContentAgent repairContentAgent = buildContentAgent();
        DesignAgent repairDesignAgent = buildDesignAgent();

        // 查找已创建的修复任务ID（按 slideIndex 匹配）
        Map<Integer, String> repairTaskIds = new ConcurrentHashMap<>();
        if (tracker != null) {
            List<Map<String, Object>> snapshot = tracker.getTaskListSnapshot();
            for (Map<String, Object> t : snapshot) {
                if ("REPAIRER".equals(t.get("agentRole"))
                        && "PENDING".equals(t.get("status"))
                        && t.containsKey("slideIndex")) {
                    int si = (int) t.get("slideIndex");
                    if (weakIndexes.contains(si)) {
                        repairTaskIds.put(si, (String) t.get("id"));
                    }
                }
            }
        }

        for (int idx : weakIndexes) {
            if (idx >= sections.size()) continue;

            final int slideIndex = idx;
            final String section = sections.get(slideIndex);

            if (repairCallback != null) {
                repairCallback.onProgress(repairRound, slideIndex,
                        String.format("正在修复第 %d 页...", slideIndex + 1));
            }

            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                String taskId = repairTaskIds.get(slideIndex);
                try {
                    log.info("修复第 {}/{} 页 (round={})", slideIndex + 1, totalSlides, repairRound);

                    if (tracker != null && taskId != null) {
                        tracker.startTask(taskId, "带评估反馈重新生成内容...");
                    }

                    Map<String, Object> repairedConfig = regenerateSlideWithFeedback(
                            section, templateInfo, slideIndex, totalSlides, feedbackSummary, repairContentAgent);

                    enrichSlideWithDesign(repairedConfig, section, slideIndex, repairDesignAgent);

                    repairedMap.put(slideIndex, repairedConfig);

                    if (progressCallback != null) {
                        progressCallback.accept(slideIndex, repairedConfig);
                    }

                    if (tracker != null && taskId != null) {
                        tracker.completeTask(taskId, "修复完成");
                    }

                    // 推送状态：修复完成
                    if (slideStatusCallback != null) {
                        slideStatusCallback.onStatusChange(slideIndex, "done", "修复完成");
                    }

                    log.info("第 {}/{} 页修复完成", slideIndex + 1, totalSlides);
                } catch (Exception e) {
                    log.error("第 {}/{} 页修复失败", slideIndex + 1, totalSlides, e);
                    if (slideStatusCallback != null) {
                        slideStatusCallback.onStatusChange(slideIndex, "failed", "修复失败");
                    }
                    if (tracker != null && taskId != null) {
                        tracker.failTask(taskId, "修复失败: " + e.getMessage());
                    }
                }
            }, slideExecutor);

            futures.add(future);
        }

        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .get(180, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("修复超时或异常", e);
        }

        // 替换修复后的页面
        for (Map.Entry<Integer, Map<String, Object>> entry : repairedMap.entrySet()) {
            allSlides.set(entry.getKey(), entry.getValue());
        }

        log.info("第 {} 轮修复完成: 成功修复 {}/{} 页",
                repairRound, repairedMap.size(), weakIndexes.size());
    }

    /**
     * 逐页即时视觉审查 — 借鉴 PPTAgent V2 (DeepPresenter) 的 inspect_slide 设计。
     * 在每页生成后立即渲染预览图，用视觉模型快速审查，发现问题立即修复。
     * 相比现有的"全部生成后批量评估"，这种方式能更早地捕获和修复问题。
     *
     * @param slideConfig     当前页的配置 JSON
     * @param section         该页大纲内容
     * @param slideIndex      页码索引
     * @param totalSlides     总页数
     * @param templateUrl     模板 URL（用于渲染预览）
     * @param contentAgent    ContentAgent（用于修复重生）
     * @param designAgent     DesignAgent（用于修复后重新配图）
     * @return 修复后的 slideConfig（如果需要修复），或原始 slideConfig
     */
    public Map<String, Object> inspectAndRepairSlide(
            Map<String, Object> slideConfig, String section,
            int slideIndex, int totalSlides, String templateUrl,
            ContentAgent contentAgent, DesignAgent designAgent) {

        if (!agentProperties.enablePerSlideInspection() || templateUrl == null || templateUrl.isBlank()) {
            return slideConfig;
        }

        try {
            // Step 1: 渲染当前页为预览图
            var previewResult = pptServiceClient.generateSlidePreview(templateUrl, slideConfig);
            String previewUrl = previewResult.imageUrl();
            if (previewUrl == null || previewUrl.isBlank()) {
                log.debug("第{}页预览渲染无结果，跳过视觉审查", slideIndex + 1);
                return slideConfig;
            }

            // Step 2: 视觉模型快速审查（轻量 prompt，控制延迟）
            String inspectionPrompt = String.format("""
                    Quickly review the visual quality of this presentation slide (slide %d/%d).

                    Check ONLY for the following critical issues:
                    1. Text overflowing or being clipped outside text boxes
                    2. Obvious layout corruption (overlapping elements, misalignment)
                    3. Too little text causing empty/hollow appearance
                    4. Missing or broken images

                    Output pure JSON:
                    {
                      "pass": true/false,
                      "issues": ["issue description 1", "issue description 2"],
                      "severity": "none|minor|major"
                    }

                    If no critical issues are found, output {"pass": true, "issues": [], "severity": "none"}
                    """, slideIndex + 1, totalSlides);

            String inspectResult = langchainChatService.chatWithImages(
                    agentProperties.designModel(),
                    "You are a presentation visual quality inspector. Focus only on critical visual defects and ignore minor stylistic differences. Output pure JSON.",
                    inspectionPrompt,
                    List.of(previewUrl));

            com.fasterxml.jackson.databind.JsonNode inspectJson = PptAgentJsonUtils.parseJsonSafe(inspectResult);
            if (inspectJson == null || inspectJson.path("pass").asBoolean(true)) {
                log.info("第{}页视觉审查通过", slideIndex + 1);
                // 将预览图 URL 存入 slideConfig 供后续使用
                slideConfig.put("previewImageUrl", previewUrl);
                return slideConfig;
            }

            // Step 3: 发现问题 → 带视觉反馈修复
            String severity = inspectJson.path("severity").asText("minor");
            StringBuilder issueText = new StringBuilder();
            com.fasterxml.jackson.databind.JsonNode issuesArray = inspectJson.path("issues");
            if (issuesArray.isArray()) {
                for (com.fasterxml.jackson.databind.JsonNode issue : issuesArray) {
                    issueText.append("- ").append(issue.asText()).append("\n");
                }
            }

            log.info("第{}页视觉审查发现{}问题: {}", slideIndex + 1, severity, issueText);

            if ("minor".equals(severity)) {
                // 轻微问题：记录但不修复，避免额外延迟
                slideConfig.put("previewImageUrl", previewUrl);
                slideConfig.put("inspection_notes", issueText.toString());
                return slideConfig;
            }

            // major 问题：立即修复
            log.info("第{}页发现严重视觉问题，立即修复重生", slideIndex + 1);

            int suggestedIndex = parseTemplateSlideIndex(section, slideIndex, totalSlides);
            String pageInfo = extractTemplatePageInfo(section, suggestedIndex);
            String slideType = inferSlideType(slideIndex, totalSlides, section);

            String repairInput = String.format("""
                    Regenerate the fill content for slide %d/%d. The previous content has visual issues that need repair.

                    ## Page Type: %s
                    ## Template Page (template_slide_index=%d)
                    %s

                    ## Outline Content
                    %s

                    ## Visual Inspection Issues (MUST be fixed)
                    %s

                    ## Repair Requirements
                    - template_slide_index MUST be set to %d
                    - Strictly control text volume based on shape dimensions to avoid overflow
                    - Ensure ALL fillable=YES shapes have fills

                    Output pure JSON.
                    """,
                    slideIndex + 1, totalSlides, slideType,
                    suggestedIndex, pageInfo,
                    cleanSectionText(section),
                    issueText,
                    suggestedIndex);

            String repairedResult = contentAgent.generateSlideContent(repairInput);
            Map<String, Object> repairedConfig = PptAgentJsonUtils.parseSlideConfigSafe(repairedResult, objectMapper);

            // 修复后重新执行 DesignAgent 视觉优化（确保配图不丢失）
            if (designAgent != null) {
                enrichSlideWithDesign(repairedConfig, section, slideIndex, designAgent);
            }

            // 重新渲染预览
            try {
                var repairedPreview = pptServiceClient.generateSlidePreview(templateUrl, repairedConfig);
                if (repairedPreview.imageUrl() != null && !repairedPreview.imageUrl().isBlank()) {
                    repairedConfig.put("previewImageUrl", repairedPreview.imageUrl());
                }
            } catch (Exception e) {
                log.warn("第{}页修复后预览渲染失败: {}", slideIndex + 1, e.getMessage());
            }

            repairedConfig.put("inspection_repaired", true);
            log.info("第{}页视觉修复完成", slideIndex + 1);
            return repairedConfig;

        } catch (Exception e) {
            log.warn("第{}页视觉审查异常，使用原始结果: {}", slideIndex + 1, e.getMessage());
            return slideConfig;
        }
    }

    // ==================== 内部辅助方法 ====================

    /**
     * 带评估反馈的幻灯片重新生成
     */
    private Map<String, Object> regenerateSlideWithFeedback(
            String section, String templateInfo, int slideIndex, int totalSlides,
            String feedbackSummary, ContentAgent contentAgent) {

        int suggestedIndex = parseTemplateSlideIndex(section, slideIndex, totalSlides);
        // 优先从 section 内嵌的 [TEMPLATE_PAGE] 块提取，其次从全局 templateInfo
        String pageInfo = extractTemplatePageInfo(section, suggestedIndex);
        String slideType = inferSlideType(slideIndex, totalSlides, section);

        String input = String.format("""
                Regenerate the fill content for the following slide. This is a repair round — pay close attention to the previous evaluation feedback.

                ## Current Position
                Slide %d/%d, page type: %s

                ## Template Page to Use (template_slide_index=%d)
                %s

                ## Outline Content for This Slide
                %s

                ## Previous Evaluation Feedback (focus on improvements)
                %s

                ## Filling Guidelines
                1. template_slide_index MUST be set to %d
                2. fills may only use shape_ids listed above
                3. **You MUST provide a fill for every fillable=YES shape** — omissions cause template placeholder text to remain
                4. For unused fillable=YES shapes, still output {"shape_id": N, "text": ""}
                5. Content should be more professional and specific, backed by data
                6. Bullet points must be concise (no more than 20 characters each)
                7. Use search tools to supplement with the latest information when necessary

                Output strictly pure JSON without ```json markers or any other text.
                """,
                slideIndex + 1, totalSlides, slideType,
                suggestedIndex,
                pageInfo,
                cleanSectionText(section),
                feedbackSummary,
                suggestedIndex);

        String result = contentAgent.generateSlideContent(input);
        return PptAgentJsonUtils.parseSlideConfigSafe(result, objectMapper);
    }

    /**
     * 构建修复反馈文本
     */
    private String buildRepairFeedback(EvaluationResult eval, List<Integer> weakIndexes) {
        StringBuilder sb = new StringBuilder();
        sb.append("Overall score: ").append(eval.overallScore()).append("/100\n");
        sb.append("Content score: ").append(eval.contentScore()).append("/100\n");
        sb.append("Design score: ").append(eval.designScore()).append("/100\n");
        sb.append("Coherence score: ").append(eval.coherenceScore()).append("/100\n\n");

        if (!eval.weaknesses().isEmpty()) {
            sb.append("Key weaknesses:\n");
            for (String w : eval.weaknesses()) {
                sb.append("- ").append(w).append("\n");
            }
        }

        if (!eval.suggestions().isEmpty()) {
            sb.append("\nImprovement suggestions:\n");
            for (String s : eval.suggestions()) {
                sb.append("- ").append(s).append("\n");
            }
        }

        // Add per-slide feedback
        if (eval.slideFeedbacks() != null) {
            sb.append("\nLow-scoring slide feedback:\n");
            for (SlideFeedback fb : eval.slideFeedbacks()) {
                if (weakIndexes.contains(fb.slideIndex())) {
                    sb.append(String.format("- Slide %d (score %d): %s\n",
                            fb.slideIndex() + 1, fb.score(), fb.feedback()));
                }
            }
        }

        return sb.toString();
    }

    /**
     * 为单张幻灯片添加设计元素（配图）
     */
    @SuppressWarnings("unchecked")
    private void enrichSlideWithDesign(Map<String, Object> slideConfig, String section, int slideIndex,
                                       DesignAgent designAgent) {
        try {
            // 检查是否有 image_suggestions
            Object suggestions = slideConfig.get("image_suggestions");
            if (suggestions instanceof List<?> list && !list.isEmpty()) {

                String input = String.format("""
                        Provide a visual optimization plan for the following slide.

                        Slide content: %s
                        Image suggestions: %s

                        Image sourcing strategy:
                        - If the topic is a REAL-WORLD entity (product, company, person, place, device),
                          use searchWebImage first. Fall back to generateSlideImage only if search fails.
                        - If the topic is ABSTRACT or CONCEPTUAL, use generateSlideImage directly.
                        - Not every slide needs an image — skip if the slide is text-heavy.

                        Output pure JSON.
                        """, section, list);

                String designResult = designAgent.designSlide(input);
                com.fasterxml.jackson.databind.JsonNode designJson = PptAgentJsonUtils.parseJsonSafe(designResult);

                if (designJson != null && designJson.has("image_url")) {
                    String imageUrl = designJson.path("image_url").asText(null);
                    if (imageUrl != null && !imageUrl.isBlank() && !imageUrl.equals("null")) {
                        slideConfig.put("generated_image_url", imageUrl);
                        log.info("第{}页配图生成成功: {}", slideIndex + 1, imageUrl);

                        // 将生成的图片 URL 写入 fills 数组的图片槽位，确保 ppt-service 能插入到 PPTX
                        injectImageIntoFills(slideConfig, section, imageUrl);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("第{}页视觉优化跳过: {}", slideIndex + 1, e.getMessage());
        }
    }

    /**
     * 将生成的图片 URL 注入到 fills 数组中对应的图片槽位。
     * 从 [TEMPLATE_PAGE] 块中解析图片槽位 shape_id，然后添加到 fills。
     */
    @SuppressWarnings("unchecked")
    private void injectImageIntoFills(Map<String, Object> slideConfig, String section, String imageUrl) {
        try {
            // Parse image slot shape_id from [TEMPLATE_PAGE] block
            // Format: "Image slots:\n  shape_id=18, name=...\n"
            java.util.regex.Matcher imgMatcher = java.util.regex.Pattern
                    .compile("Image slots:\\s*\\n\\s*shape_id=(\\d+)")
                    .matcher(section != null ? section : "");

            if (!imgMatcher.find()) {
                log.debug("No image slot shape_id found, skipping image injection");
                return;
            }
            final int imageShapeId = Integer.parseInt(imgMatcher.group(1));

            // 获取或创建 fills 列表
            Object fillsObj = slideConfig.get("fills");
            List<Map<String, Object>> fills;
            if (fillsObj instanceof List<?>) {
                fills = (List<Map<String, Object>>) fillsObj;
            } else {
                fills = new ArrayList<>();
                slideConfig.put("fills", fills);
            }

            // 检查是否已有该 shape_id 的 fill（避免重复）
            boolean exists = fills.stream().anyMatch(f ->
                    f.containsKey("shape_id") && Objects.equals(f.get("shape_id"), imageShapeId));

            if (!exists) {
                Map<String, Object> imageFill = new HashMap<>();
                imageFill.put("shape_id", imageShapeId);
                imageFill.put("image_url", imageUrl);
                fills.add(imageFill);
                log.info("图片已注入 fills: shape_id={}, url={}", imageShapeId, imageUrl);
            }
        } catch (Exception e) {
            log.warn("图片注入 fills 失败: {}", e.getMessage());
        }
    }

    // ==================== Agent 构建方法 ====================

    private ContentAgent buildContentAgent() {
        String modelId = agentProperties.contentModel() != null
            ? agentProperties.contentModel()
            : chatModelFactory.getDefaultModelId();
        ChatModel model = chatModelFactory.createChatModelWithParams(
                modelId, 0.5, 0.85, 4096);
        return AiServices.builder(ContentAgent.class)
                .chatModel(model)
                .tools(webSearchTool, contentResearchTool)
                .build();
    }

    private DesignAgent buildDesignAgent() {
        // 使用支持 function calling 的文本模型（非视觉模型），因为需要调用 imageGenerationTool
        String modelId = agentProperties.designToolModel() != null
            ? agentProperties.designToolModel()
            : chatModelFactory.getDefaultModelId();
        ChatModel model = chatModelFactory.createChatModelWithParams(
                modelId, 0.6, 0.85, 2048);
        return AiServices.builder(DesignAgent.class)
                .chatModel(model)
                .tools(imageGenerationTool)
                .build();
    }

    // ==================== 工具方法（从 PptAgentOrchestrator 复制，待重构） ====================

    private String inferSlideType(int slideIndex, int totalSlides, String section) {
        // 优先从 [TEMPLATE_PAGE] 块中提取 role
        if (section != null) {
            java.util.regex.Matcher roleMatcher = java.util.regex.Pattern
                    .compile("\\[role=(\\w+)")
                    .matcher(section);
            if (roleMatcher.find()) {
                String role = roleMatcher.group(1);
                return switch (role) {
                    case "cover" -> "cover（封面页：显示PPT标题和副标题）";
                    case "toc" -> "toc（目录页：展示PPT结构）";
                    case "section" -> "section（章节过渡页：仅显示章节标题）";
                    case "ending" -> "ending（结束页：感谢/总结/联系方式）";
                    case "credits" -> "credits（版权页：致谢/版权信息）";
                    default -> "content（正文页：标题 + 要点列表或详细内容）";
                };
            }
        }
        // 兜底：基于位置推断
        if (slideIndex == 0) return "cover（封面页：显示PPT标题和副标题）";
        if (slideIndex == totalSlides - 1) return "ending（结束页：感谢/总结/联系方式）";
        String cleaned = section != null ? cleanSectionText(section).trim() : "";
        if (cleaned.startsWith("## ") && !cleaned.contains("### "))
            return "section（章节过渡页：仅显示章节标题）";
        return "content（正文页：标题 + 要点列表或详细内容）";
    }

    /**
     * 从大纲章节文本中解析 PlannerAgent 标注的 template_slide_index
     * 格式: <!-- template_slide_index=N -->
     */
    private int parseTemplateSlideIndex(String section, int slideIndex, int totalSlides) {
        if (section == null) return inferDefaultTemplateIndex(slideIndex, totalSlides);

        // 优先从 [TEMPLATE_PAGE] 块解析（enriched sections 格式）
        java.util.regex.Matcher tpMatcher = java.util.regex.Pattern
                .compile("\\[TEMPLATE_PAGE]\\s*\\ntemplate_slide_index\\s*=\\s*(\\d+)")
                .matcher(section);
        if (tpMatcher.find()) {
            try {
                return Integer.parseInt(tpMatcher.group(1));
            } catch (NumberFormatException e) { /* ignore */ }
        }

        // 兼容旧格式 <!-- template_slide_index=N -->
        java.util.regex.Matcher matcher = java.util.regex.Pattern
                .compile("<!--\\s*template_slide_index\\s*=\\s*(\\d+)\\s*-->")
                .matcher(section);
        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException e) { /* ignore */ }
        }

        return inferDefaultTemplateIndex(slideIndex, totalSlides);
    }

    /**
     * 默认模板页索引推断：封面→0, 结尾→最后一页（假设存在ending页）, 其他→轮转content页
     */
    private int inferDefaultTemplateIndex(int slideIndex, int totalSlides) {
        if (slideIndex == 0) return 0;
        if (slideIndex == totalSlides - 1) return Math.max(1, slideIndex);
        // 默认使用索引1（通常是第一个content页），如果模板有多个content页可以轮转
        return Math.min(slideIndex, 1);
    }

    /**
     * 从 section 文本或全局模板描述中提取该页的模板 shape 信息。
     * 优先从 enriched section 的 [TEMPLATE_PAGE]...[/TEMPLATE_PAGE] 块提取，
     * 其次从全局 templateInfo 中按 "┌── 模板第N页" 标记搜索。
     */
    private String extractTemplatePageInfo(String templateInfo, int pageIndex) {
        if (templateInfo == null || templateInfo.isBlank()) {
            return "无模板页信息（使用默认布局）";
        }

        // 优先：从 [TEMPLATE_PAGE]...[/TEMPLATE_PAGE] 块提取（enriched section 内嵌）
        int tpStart = templateInfo.indexOf("[TEMPLATE_PAGE]");
        int tpEnd = templateInfo.indexOf("[/TEMPLATE_PAGE]");
        if (tpStart >= 0 && tpEnd > tpStart) {
            return templateInfo.substring(tpStart + "[TEMPLATE_PAGE]".length(), tpEnd).trim();
        }

        // 兼容旧格式：从全局模板描述中按 "┌── 模板第N页" 标记搜索
        String startMarker = String.format("┌── 模板第%d页", pageIndex);
        String endMarker = String.format("└── 模板第%d页结束", pageIndex);

        int startIdx = templateInfo.indexOf(startMarker);
        int endIdx = templateInfo.indexOf(endMarker);

        if (startIdx >= 0 && endIdx > startIdx) {
            String pageSection = templateInfo.substring(startIdx, endIdx + endMarker.length());
            return pageSection + "\n\n" + extractTemplateRules(templateInfo);
        }

        log.debug("未找到模板第{}页的详细信息，返回完整模板", pageIndex);
        return templateInfo;
    }

    /**
     * 提取模板使用规则部分
     */
    private String extractTemplateRules(String templateInfo) {
        int rulesIdx = templateInfo.indexOf("=== 使用规则 ===");
        if (rulesIdx >= 0) {
            return templateInfo.substring(rulesIdx);
        }
        return "";
    }

    /**
     * 清理章节文本：移除 template_slide_index 标注等元信息
     */
    private String cleanSectionText(String section) {
        if (section == null) return "";
        // 移除 [TEMPLATE_PAGE]...[/TEMPLATE_PAGE] 块
        String cleaned = section.replaceAll("(?s)\\[TEMPLATE_PAGE].*?\\[/TEMPLATE_PAGE]", "");
        // 兼容旧格式
        cleaned = cleaned.replaceAll("<!--\\s*template_slide_index\\s*=\\s*\\d+\\s*-->", "");
        return cleaned.trim();
    }

    // ==================== 回调接口定义（从 PptAgentOrchestrator 复制） ====================

    /**
     * 回调接口：单页幻灯片状态变化通知（用于前端实时显示并行状态）
     */
    @FunctionalInterface
    public interface SlideStatusCallback {
        /**
         * @param slideIndex  幻灯片索引
         * @param status      状态: pending/generating/designing/rendering/done/evaluating/repairing/failed
         * @param statusLabel 中文状态描述
         */
        void onStatusChange(int slideIndex, String status, String statusLabel);
    }

    /**
     * 回调接口：反思修复进度通知
     */
    @FunctionalInterface
    public interface RepairProgressCallback {
        /**
         * @param round      当前修复轮次（1-based）
         * @param slideIndex 正在修复的幻灯片索引（-1 表示整体评估阶段）
         * @param message    进度描述
         */
        void onProgress(int round, int slideIndex, String message);
    }
}

