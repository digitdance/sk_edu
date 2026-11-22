package com.novacloudedu.backend.infrastructure.ai.agent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.novacloudedu.backend.config.PptAgentProperties;
import com.novacloudedu.backend.infrastructure.ai.ChatModelFactory;
import com.novacloudedu.backend.infrastructure.ai.LangchainChatService;
import com.novacloudedu.backend.infrastructure.ai.agent.PptAgentInterfaces.*;
import com.novacloudedu.backend.infrastructure.ai.agent.PptReflector.SlideStatusCallback;
import com.novacloudedu.backend.infrastructure.ppt.PptServiceClient;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * PPT 幻灯片生成工厂 — Stage 2: 幻灯片生成与设计富化
 *
 * 负责并发生成幻灯片内容（模板模式、HTML模式），包括：
 * - ContentAgent 驱动的内容生成
 * - DesignAgent 驱动的视觉优化（配图生成）
 * - 内容校验与即时修复
 * - HTML 模式的内容扩写与生成
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PptSlideFactory {

    private final ChatModelFactory chatModelFactory;
    private final LangchainChatService langchainChatService;
    private final ImageGenerationTool imageGenerationTool;
    private final WebSearchTool webSearchTool;
    private final ContentResearchTool contentResearchTool;
    private final PptServiceClient pptServiceClient;
    private final ObjectMapper objectMapper;
    private final PptAgentProperties agentProperties;
    @Qualifier("pptSlideExecutor")
    private final ExecutorService slideExecutor;

    // ==================== Public API: Stage 2 并发生成 ====================

    /**
     * Agent 驱动的并发幻灯片生成
     */
    public List<Map<String, Object>> generateSlidesParallel(
            List<String> sections,
            String templateInfo,
            BiConsumer<Integer, Map<String, Object>> progressCallback) {
        return generateSlidesParallel(sections, templateInfo, progressCallback, null, null);
    }

    /**
     * Agent 驱动的并发幻灯片生成（带任务跟踪 + 状态回调）
     */
    public List<Map<String, Object>> generateSlidesParallel(
            List<String> sections,
            String templateInfo,
            BiConsumer<Integer, Map<String, Object>> progressCallback,
            AgentTaskTracker tracker) {
        return generateSlidesParallel(sections, templateInfo, progressCallback, tracker, null);
    }

    /**
     * Agent 驱动的并发幻灯片生成（带任务跟踪 + 状态回调）
     */
    public List<Map<String, Object>> generateSlidesParallel(
            List<String> sections,
            String templateInfo,
            BiConsumer<Integer, Map<String, Object>> progressCallback,
            AgentTaskTracker tracker,
            SlideStatusCallback slideStatusCallback) {
        return generateSlidesParallel(sections, templateInfo, null,
                progressCallback, tracker, slideStatusCallback);
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
            SlideStatusCallback slideStatusCallback) {

        log.info("Stage 2 - 并发生成 {} 页幻灯片, concurrency={}, validation={}, layoutSelection={}",
                sections.size(), agentProperties.concurrency(),
                agentProperties.enableContentValidation(), agentProperties.enableLayoutSelection());

        // 预构建 Agent 实例，所有并发任务复用（AiServices 实例是线程安全的）
        ContentAgent sharedContentAgent = buildContentAgent();
        DesignAgent sharedDesignAgent = buildDesignAgent();
        LayoutSelectorAgent sharedLayoutSelector = agentProperties.enableLayoutSelection() ? buildLayoutSelectorAgent() : null;

        int totalSlides = sections.size();
        Map<Integer, Map<String, Object>> resultsMap = new ConcurrentHashMap<>();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        // 为每页预创建任务（content + design）
        Map<Integer, String> contentTaskIds = new ConcurrentHashMap<>();
        Map<Integer, String> designTaskIds = new ConcurrentHashMap<>();

        if (tracker != null) {
            for (int i = 0; i < totalSlides; i++) {
                contentTaskIds.put(i, tracker.addTask(AgentTaskTracker.AgentRole.CONTENT,
                        String.format("生成第 %d/%d 页内容", i + 1, totalSlides), i));
                designTaskIds.put(i, tracker.addTask(AgentTaskTracker.AgentRole.DESIGN,
                        String.format("第 %d/%d 页视觉优化", i + 1, totalSlides), i));
            }
            tracker.pushFullList();
        }

        for (int i = 0; i < totalSlides; i++) {
            final int slideIndex = i;
            final String section = sections.get(i);

            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    log.info("开始生成第 {}/{} 页", slideIndex + 1, totalSlides);

                    // 推送状态：生成中
                    if (slideStatusCallback != null) {
                        slideStatusCallback.onStatusChange(slideIndex, "generating", "AI 生成内容...");
                    }

                    // ContentAgent 生成内容
                    if (tracker != null) {
                        tracker.startTask(contentTaskIds.get(slideIndex), "ContentAgent 正在生成...");
                    }

                    Map<String, Object> slideConfig = generateSlideWithAgent(
                            section, templateInfo, slideIndex, totalSlides,
                            sharedContentAgent, templateUrl, sharedLayoutSelector);

                    if (tracker != null) {
                        tracker.completeTask(contentTaskIds.get(slideIndex), "内容生成完成");
                    }

                    // 推送状态：设计优化中
                    if (slideStatusCallback != null) {
                        slideStatusCallback.onStatusChange(slideIndex, "designing", "视觉优化...");
                    }

                    // DesignAgent 视觉优化（检查是否需要配图）
                    if (tracker != null) {
                        tracker.startTask(designTaskIds.get(slideIndex), "DesignAgent 检查配图需求...");
                    }

                    enrichSlideWithDesign(slideConfig, section, slideIndex, sharedDesignAgent);

                    if (tracker != null) {
                        boolean hasImage = slideConfig.containsKey("generated_image_url");
                        tracker.completeTask(designTaskIds.get(slideIndex),
                                hasImage ? "已生成配图" : "无需配图，跳过");
                    }

                    // Per-Slide Visual Inspection（P1）：逐页即时视觉审查
                    if (agentProperties.enablePerSlideInspection() && templateUrl != null) {
                        if (slideStatusCallback != null) {
                            slideStatusCallback.onStatusChange(slideIndex, "inspecting", "视觉审查...");
                        }
                        slideConfig = inspectAndRepairSlide(
                                slideConfig, section, slideIndex, totalSlides,
                                templateUrl, sharedContentAgent, sharedDesignAgent);
                    }

                    // 推送状态：渲染预览中
                    if (slideStatusCallback != null) {
                        slideStatusCallback.onStatusChange(slideIndex, "rendering", "渲染预览...");
                    }

                    resultsMap.put(slideIndex, slideConfig);

                    if (progressCallback != null) {
                        progressCallback.accept(slideIndex, slideConfig);
                    }

                    // 推送状态：完成
                    if (slideStatusCallback != null) {
                        slideStatusCallback.onStatusChange(slideIndex, "done", "完成");
                    }

                    log.info("第 {}/{} 页生成完成", slideIndex + 1, totalSlides);

                } catch (Exception e) {
                    log.error("第 {}/{} 页生成失败", slideIndex + 1, totalSlides, e);
                    if (slideStatusCallback != null) {
                        slideStatusCallback.onStatusChange(slideIndex, "failed", "生成失败");
                    }
                    if (tracker != null) {
                        tracker.failTask(contentTaskIds.get(slideIndex), "生成失败: " + e.getMessage());
                        tracker.skipTask(designTaskIds.get(slideIndex), "内容生成失败，跳过视觉优化");
                    }
                    Map<String, Object> fallback = createFallbackSlideConfig(section, slideIndex, templateInfo);
                    resultsMap.put(slideIndex, fallback);
                    if (progressCallback != null) {
                        progressCallback.accept(slideIndex, fallback);
                    }
                }
            }, slideExecutor)
            .orTimeout(120, TimeUnit.SECONDS)
            .exceptionally(ex -> {
                if (ex instanceof java.util.concurrent.TimeoutException || (ex.getCause() instanceof java.util.concurrent.TimeoutException)) {
                    log.error("第 {}/{} 页生成超时（120s）", slideIndex + 1, totalSlides);
                    if (slideStatusCallback != null) {
                        slideStatusCallback.onStatusChange(slideIndex, "failed", "生成超时");
                    }
                    Map<String, Object> fallback = createFallbackSlideConfig(section, slideIndex, templateInfo);
                    resultsMap.put(slideIndex, fallback);
                    if (progressCallback != null) {
                        progressCallback.accept(slideIndex, fallback);
                    }
                }
                return null;
            });

            futures.add(future);
        }

        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .get(300, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("并发生成超时或异常", e);
        }

        List<Map<String, Object>> orderedResults = new ArrayList<>();
        for (int i = 0; i < totalSlides; i++) {
            Map<String, Object> config = resultsMap.get(i);
            if (config != null) {
                orderedResults.add(config);
            }
        }

        log.info("Stage 2 完成: 成功生成 {}/{} 页", orderedResults.size(), totalSlides);
        return orderedResults;
    }

    /**
     * HTML 模式：并发生成所有 HTML 幻灯片
     */
    public List<Map<String, Object>> generateHtmlSlidesParallel(
            List<String> sections,
            String userStyleHint,
            BiConsumer<Integer, Map<String, Object>> progressCallback,
            AgentTaskTracker tracker,
            SlideStatusCallback slideStatusCallback) {

        log.info("HTML 模式 Stage 2 - 并发生成  页 HTML 幻灯片, concurrency={}",
                sections.size(), agentProperties.concurrency());

        HtmlSlideAgent sharedHtmlAgent = buildHtmlSlideAgent();
        DesignAgent sharedDesignAgent = buildDesignAgent();

        int totalSlides = sections.size();
        Map<Integer, Map<String, Object>> resultsMap = new ConcurrentHashMap<>();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        // 全局设计指令
        String globalColorScheme = buildGlobalColorScheme(userStyleHint);

        Map<Integer, String> contentTaskIds = new ConcurrentHashMap<>();
        Map<Integer, String> designTaskIds = new ConcurrentHashMap<>();

        if (tracker != null) {
            for (int i = 0; i < totalSlides; i++) {
                contentTaskIds.put(i, tracker.addTask(AgentTaskTracker.AgentRole.CONTENT,
                        String.format("生成第 %d/%d 页 HTML 内容", i + 1, totalSlides), i));
                designTaskIds.put(i, tracker.addTask(AgentTaskTracker.AgentRole.DESIGN,
                        String.format("第 %d/%d 页视觉优化", i + 1, totalSlides), i));
            }
            tracker.pushFullList();
        }

        for (int i = 0; i < totalSlides; i++) {
            final int slideIndex = i;
            final String section = sections.get(i);

            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    log.info("HTML 模式：开始生成第 {}/{} 页", slideIndex + 1, totalSlides);

                    if (slideStatusCallback != null) {
                        slideStatusCallback.onStatusChange(slideIndex, "expanding", "扩写内容...");
                    }
                    if (tracker != null) {
                        tracker.startTask(contentTaskIds.get(slideIndex), "内容扩写中...");
                    }

                    String expandedSection = expandSectionForHtml(section, slideIndex, totalSlides);

                    if (slideStatusCallback != null) {
                        slideStatusCallback.onStatusChange(slideIndex, "generating", "AI 生成 HTML 内容...");
                    }
                    if (tracker != null) {
                        tracker.updateDetail(contentTaskIds.get(slideIndex), "HtmlSlideAgent 正在生成...");
                    }

                    Map<String, Object> slideConfig = generateHtmlSlideWithAgent(
                            expandedSection, slideIndex, totalSlides, globalColorScheme, sharedHtmlAgent);

                    if (tracker != null) {
                        tracker.completeTask(contentTaskIds.get(slideIndex), "HTML 内容生成完成");
                    }

                    if (slideStatusCallback != null) {
                        slideStatusCallback.onStatusChange(slideIndex, "designing", "视觉优化...");
                    }
                    if (tracker != null) {
                        tracker.startTask(designTaskIds.get(slideIndex), "DesignAgent 检查配图需求...");
                    }

                    enrichHtmlSlideWithDesign(slideConfig, section, slideIndex, sharedDesignAgent);

                    if (tracker != null) {
                        boolean hasImage = slideConfig.containsKey("generated_image_url");
                        tracker.completeTask(designTaskIds.get(slideIndex),
                                hasImage ? "已生成配图" : "无需配图，跳过");
                    }

                    if (slideStatusCallback != null) {
                        slideStatusCallback.onStatusChange(slideIndex, "rendering", "渲染预览...");
                    }

                    resultsMap.put(slideIndex, slideConfig);

                    if (progressCallback != null) {
                        progressCallback.accept(slideIndex, slideConfig);
                    }

                    if (slideStatusCallback != null) {
                        slideStatusCallback.onStatusChange(slideIndex, "done", "完成");
                    }

                    log.info("HTML 模式：第 {}/{} 页生成完成", slideIndex + 1, totalSlides);

                } catch (Exception e) {
                    log.error("HTML 模式：第 {}/{} 页生成失败", slideIndex + 1, totalSlides, e);
                    if (slideStatusCallback != null) {
                        slideStatusCallback.onStatusChange(slideIndex, "failed", "生成失败");
                    }
                    if (tracker != null) {
                        tracker.failTask(contentTaskIds.get(slideIndex), "生成失败: " + e.getMessage());
                        tracker.skipTask(designTaskIds.get(slideIndex), "内容生成失败，跳过视觉优化");
                    }
                    Map<String, Object> fallback = createFallbackHtmlSlideConfig(section, slideIndex, totalSlides);
                    resultsMap.put(slideIndex, fallback);
                    if (progressCallback != null) {
                        progressCallback.accept(slideIndex, fallback);
                    }
                }
            }, slideExecutor)
            .orTimeout(120, TimeUnit.SECONDS)
            .exceptionally(ex -> {
                if (ex instanceof java.util.concurrent.TimeoutException
                        || (ex.getCause() instanceof java.util.concurrent.TimeoutException)) {
                    log.error("HTML 模式：第 {}/{} 页生成超时（120s）", slideIndex + 1, totalSlides);
                    if (slideStatusCallback != null) {
                        slideStatusCallback.onStatusChange(slideIndex, "failed", "生成超时");
                    }
                    Map<String, Object> fallback = createFallbackHtmlSlideConfig(section, slideIndex, totalSlides);
                    resultsMap.put(slideIndex, fallback);
                    if (progressCallback != null) {
                        progressCallback.accept(slideIndex, fallback);
                    }
                }
                return null;
            });

            futures.add(future);
        }

        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .get(300, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("HTML 模式：并发生成超时或异常", e);
        }

        List<Map<String, Object>> orderedResults = new ArrayList<>();
        for (int i = 0; i < totalSlides; i++) {
            Map<String, Object> config = resultsMap.get(i);
            if (config != null) {
                orderedResults.add(config);
            }
        }

        log.info("HTML 模式 Stage 2 完成: 成功生成 {}/{} 页", orderedResults.size(), totalSlides);
        return orderedResults;
    }

    // ==================== Design Enrichment ====================

    public void enrichSlideWithDesign(Map<String, Object> slideConfig, String section, int slideIndex,
                                       DesignAgent designAgent) {
        try {
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
                JsonNode designJson = PptAgentJsonUtils.parseJsonSafe(designResult);

                if (designJson != null && designJson.has("image_url")) {
                    String imageUrl = designJson.path("image_url").asText(null);
                    if (imageUrl != null && !imageUrl.isBlank() && !imageUrl.equals("null")) {
                        slideConfig.put("generated_image_url", imageUrl);
                        log.info("第{}页配图生成成功: {}", slideIndex + 1, imageUrl);

                        injectImageIntoFills(slideConfig, section, imageUrl);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("第页视觉优化跳过: {}", slideIndex + 1, e.getMessage());
        }
    }

    private void enrichHtmlSlideWithDesign(Map<String, Object> slideConfig, String section,
                                           int slideIndex, DesignAgent designAgent) {
        try {
            Object suggestions = slideConfig.get("image_suggestions");
            if (suggestions instanceof List<?> list && !list.isEmpty()) {
                String input = String.format("""
                        Provide a visual optimization plan for the following HTML slide.

                        Slide content: %s
                        Image suggestions: %s

                        Image sourcing strategy:
                        - If the topic is a REAL-WORLD entity, use searchWebImage first.
                        - If the topic is ABSTRACT or CONCEPTUAL, use generateSlideImage directly.
                        - Not every slide needs an image.

                        Output pure JSON with "image_url" field.
                        """, section, list);

                String designResult = designAgent.designSlide(input);
                JsonNode designJson = PptAgentJsonUtils.parseJsonSafe(designResult);

                if (designJson != null && designJson.has("image_url")) {
                    String imageUrl = designJson.path("image_url").asText(null);
                    if (imageUrl != null && !imageUrl.isBlank() && !imageUrl.equals("null")) {
                        slideConfig.put("generated_image_url", imageUrl);
                        log.info("HTML 模式第{}页配图生成成功: {}", slideIndex + 1, imageUrl);

                        injectImageIntoHtml(slideConfig, imageUrl);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("HTML 模式第{}页视觉优化跳过: {}", slideIndex + 1, e.getMessage());
        }
    }

    // ==================== Agent Builders ====================

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

    private LayoutSelectorAgent buildLayoutSelectorAgent() {
        String modelId = agentProperties.layoutSelectorModel() != null
            ? agentProperties.layoutSelectorModel()
            : chatModelFactory.getDefaultModelId();
        ChatModel model = chatModelFactory.createChatModelWithParams(
                modelId, 0.3, 0.85, 1024);
        return AiServices.builder(LayoutSelectorAgent.class)
                .chatModel(model)
                .build();
    }

    private HtmlSlideAgent buildHtmlSlideAgent() {
        String modelId = agentProperties.htmlSlideModel() != null
            ? agentProperties.htmlSlideModel()
            : chatModelFactory.getDefaultModelId();
        ChatModel model = chatModelFactory.createChatModelWithParams(
                modelId, 0.7, 0.9, 8192);
        return AiServices.builder(HtmlSlideAgent.class)
                .chatModel(model)
                .tools(webSearchTool, contentResearchTool)
                .build();
    }

    // ==================== Helper Methods ====================

    @SuppressWarnings("unchecked")
    private Map<String, Object> generateSlideWithAgent(
            String section, String templateInfo, int slideIndex, int totalSlides,
            ContentAgent contentAgent, String templateUrl,
            LayoutSelectorAgent sharedLayoutSelector) {

        int suggestedIndex = parseTemplateSlideIndex(section, slideIndex, totalSlides);
        String slideType = inferSlideType(slideIndex, totalSlides, section);

        // LayoutSelectorAgent: 对 content 类型页面，用 AI 选择最佳版式
        if (sharedLayoutSelector != null && "content".equals(slideType)) {
            try {
                String layoutInput = String.format("""
                        ## Slide Content (Slide %d/%d)
                        %s

                        ## Available Template Layouts
                        %s

                        Select the best-matching template layout for the content above.
                        """,
                        slideIndex + 1, totalSlides,
                        cleanSectionText(section),
                        templateInfo);
                String layoutResult = sharedLayoutSelector.selectLayout(layoutInput);
                JsonNode layoutJson = PptAgentJsonUtils.parseJsonSafe(layoutResult);
                if (layoutJson != null && layoutJson.has("selected_template_slide_index")) {
                    int selectedIdx = layoutJson.path("selected_template_slide_index").asInt(-1);
                    if (selectedIdx >= 0) {
                        log.info("第{}页 LayoutSelector 选择模板页 {} (原推荐: {}), 理由: {}",
                                slideIndex + 1, selectedIdx, suggestedIndex,
                                layoutJson.path("reasoning").asText(""));
                        suggestedIndex = selectedIdx;
                    }
                }
            } catch (Exception e) {
                log.warn("第{}页版式选择失败，使用默认: {}", slideIndex + 1, e.getMessage());
            }
        }

        String pageInfo = extractTemplatePageInfo(section, suggestedIndex);

        String input = String.format("""
                Generate fill content for the following slide.

                ## Current Position
                Slide %d/%d, page type: %s

                ## Template Page to Use (template_slide_index=%d)
                %s

                ## Outline Content for This Slide
                %s

                ## Filling Guidelines
                1. template_slide_index MUST be set to %d
                2. fills may only use shape_ids listed above
                3. **You MUST provide a fill for every fillable=YES shape** — omissions cause template placeholder text to remain
                4. For unused fillable=YES shapes, still output {"shape_id": N, "text": ""}
                5. role=title shapes get the page title; role=body+fillable=YES shapes get bullet points
                6. fillable=NO shapes do NOT need filling
                7. Control text volume based on shape dimensions

                Output strictly pure JSON without ```json markers or any other text.
                """,
                slideIndex + 1, totalSlides, slideType,
                suggestedIndex,
                pageInfo,
                cleanSectionText(section),
                suggestedIndex);

        String result = contentAgent.generateSlideContent(input);
        Map<String, Object> slideConfig = PptAgentJsonUtils.parseSlideConfigSafe(result);

        // 内容长度校验 + 带反馈重试
        if (agentProperties.enableContentValidation() && templateUrl != null && !templateUrl.isBlank()) {
            try {
                Object fillsObj = slideConfig.get("fills");
                if (fillsObj instanceof List<?> fillsList && !fillsList.isEmpty()) {
                    List<Map<String, Object>> fills = (List<Map<String, Object>>) fillsList;
                    int tsi = slideConfig.containsKey("template_slide_index")
                            ? ((Number) slideConfig.get("template_slide_index")).intValue()
                            : suggestedIndex;

                    var validation = pptServiceClient.validateSlide(
                            templateUrl, slideIndex, tsi, fills);

                    if (!validation.isValid()) {
                        log.info("第{}页内容校验不通过，带反馈重新生成: {}",
                                slideIndex + 1, validation.feedbackText());

                        String retryInput = input + "\n\n## Content Validation Feedback (please fix the following issues)\n"
                                + validation.feedbackText();
                        String retryResult = contentAgent.generateSlideContent(retryInput);
                        slideConfig = PptAgentJsonUtils.parseSlideConfigSafe(retryResult);
                    }
                }
            } catch (Exception e) {
                log.warn("第{}页内容校验异常，使用原始结果: {}", slideIndex + 1, e.getMessage());
            }
        }

        return slideConfig;
    }

    private Map<String, Object> generateHtmlSlideWithAgent(
            String section, int slideIndex, int totalSlides,
            String globalColorScheme, HtmlSlideAgent htmlAgent) {

        String slideType = inferHtmlSlideType(slideIndex, totalSlides, section);

        String designDirective = globalColorScheme != null ? globalColorScheme :
                "Use a harmonious modern color palette with gradients. " +
                "Keep the same visual language across all slides.";

        String layoutHint = switch (slideType) {
            case "cover" -> "LAYOUT: Full-bleed gradient background. Large title (72-80px) centered. " +
                    "Add 2-3 decorative geometric shapes (large semi-transparent circles, diagonal bars). " +
                    "Subtitle below title. Author/date at bottom.";
            case "section" -> "LAYOUT: Bold gradient or dark background. Large chapter number (120px+, semi-transparent). " +
                    "Chapter title (56-64px, light text). Accent line. Corner decorative elements.";
            case "ending" -> "LAYOUT: Gradient background matching cover style. Large centered closing text. " +
                    "Decorative shapes echoing cover design. Key takeaway or contact info below.";
            default -> "LAYOUT: Colored title bar at top (full-width, 80px height). " +
                    "Content area: arrange points as CARD GRID (2×2 or 3×1), each card with: " +
                    "rounded corners (16px), box-shadow, left color accent border (4-6px), " +
                    "emoji icon circle on left, text on right. " +
                    "Add decorative footer bar or page indicator at bottom. " +
                    "NEVER use plain bullet text lists — always use card-based layouts!";
        };

        String input = String.format("""
                Create a visually STUNNING HTML slide. It must look like a premium keynote design, NOT a plain document.

                ## Slide Position
                Slide %d of %d — Type: **%s**

                ## %s

                ## Global Design Directive
                %s

                ## Content for This Slide
                %s

                ## CRITICAL VISUAL REQUIREMENTS
                ⚠ Plain white backgrounds with simple text are UNACCEPTABLE. You MUST include:
                - Gradient background (linear-gradient or radial-gradient)
                - At least 2-3 decorative div elements (colored circles, bars, accent shapes)
                - Card-based layouts with box-shadow (0 8px 32px rgba(0,0,0,0.12)) and border-radius (16px)
                - Unicode emoji symbols (📊💡🎯⚡🔬📈🏆✅🌐🚀) as visual icon anchors in colored circles
                - Multi-column flexbox or grid layout (NOT single-column plain text)
                - Colored accent bars, header strips, or sidebar panels
                - Strong visual hierarchy: title (≥48px bold), subtitle, card content (≥24px)

                ## Technical Rules
                - Single root `<div>`: width:1920px; height:1080px; overflow:hidden
                - ALL styles inline (style="..."). NO <style> tags, NO external CSS/JS
                - Font: Arial, 'Helvetica Neue', sans-serif. NO external fonts
                - Text language MUST match the content above (Chinese → Chinese)

                ## Output — ONLY pure JSON:
                {"slide_html": "...", "slide_type": "...", "speaker_notes": "...", "image_suggestions": [...]}
                """,
                slideIndex + 1, totalSlides, slideType,
                layoutHint,
                designDirective,
                cleanSectionText(section));

        String result = htmlAgent.generateHtmlSlide(input);
        Map<String, Object> slideConfig = PptAgentJsonUtils.parseSlideConfigSafe(result);

        if (!slideConfig.containsKey("slide_html")) {
            log.warn("第{}页 HTML 生成缺少 slide_html 字段，使用 fallback", slideIndex + 1);
            return createFallbackHtmlSlideConfig(section, slideIndex, totalSlides);
        }

        slideConfig.put("mode", "html");
        return slideConfig;
    }

    private Map<String, Object> inspectAndRepairSlide(
            Map<String, Object> slideConfig, String section,
            int slideIndex, int totalSlides, String templateUrl,
            ContentAgent contentAgent, DesignAgent designAgent) {

        if (!agentProperties.enablePerSlideInspection() || templateUrl == null || templateUrl.isBlank()) {
            return slideConfig;
        }

        try {
            var previewResult = pptServiceClient.generateSlidePreview(templateUrl, slideConfig);
            String previewUrl = previewResult.imageUrl();
            if (previewUrl == null || previewUrl.isBlank()) {
                log.debug("第{}页预览渲染无结果，跳过视觉审查", slideIndex + 1);
                return slideConfig;
            }

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

            JsonNode inspectJson = PptAgentJsonUtils.parseJsonSafe(inspectResult);
            if (inspectJson == null || inspectJson.path("pass").asBoolean(true)) {
                log.info("第{}页视觉审查通过", slideIndex + 1);
                slideConfig.put("previewImageUrl", previewUrl);
                return slideConfig;
            }

            String severity = inspectJson.path("severity").asText("minor");
            StringBuilder issueText = new StringBuilder();
            JsonNode issuesArray = inspectJson.path("issues");
            if (issuesArray.isArray()) {
                for (JsonNode issue : issuesArray) {
                    issueText.append("- ").append(issue.asText()).append("\n");
                }
            }

            log.info("第{}页视觉审查发现{}问题: {}", slideIndex + 1, severity, issueText);

            if ("minor".equals(severity)) {
                slideConfig.put("previewImageUrl", previewUrl);
                slideConfig.put("inspection_notes", issueText.toString());
                return slideConfig;
            }

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
            Map<String, Object> repairedConfig = PptAgentJsonUtils.parseSlideConfigSafe(repairedResult);

            if (designAgent != null) {
                enrichSlideWithDesign(repairedConfig, section, slideIndex, designAgent);
            }

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

    @SuppressWarnings("unchecked")
    private void injectImageIntoFills(Map<String, Object> slideConfig, String section, String imageUrl) {
        try {
            Matcher imgMatcher = Pattern
                    .compile("Image slots:\\s*\\n\\s*shape_id=(\\d+)")
                    .matcher(section != null ? section : "");

            if (!imgMatcher.find()) {
                log.debug("No image slot shape_id found, skipping image injection");
                return;
            }
            final int imageShapeId = Integer.parseInt(imgMatcher.group(1));

            Object fillsObj = slideConfig.get("fills");
            List<Map<String, Object>> fills;
            if (fillsObj instanceof List<?>) {
                fills = (List<Map<String, Object>>) fillsObj;
            } else {
                fills = new ArrayList<>();
                slideConfig.put("fills", fills);
            }

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

    private void injectImageIntoHtml(Map<String, Object> slideConfig, String imageUrl) {
        String html = (String) slideConfig.get("slide_html");
        if (html == null) {
            return;
        }

        try {
            String replaced = html;

            if (html.contains("image-placeholder")) {
                replaced = html.replaceFirst(
                        "(?s)<div[^>]*class=\"image-placeholder\"[^>]*>.*?</div>",
                        String.format(
                                "<div style=\"width:100%%;height:100%%;overflow:hidden;border-radius:16px;"
                                + "box-shadow:0 4px 20px rgba(0,0,0,0.1);\">"
                                + "<img src=\"%s\" style=\"width:100%%;height:100%%;object-fit:cover;\" />"
                                + "</div>",
                                imageUrl));
            }

            if (replaced.equals(html)) {
                String imgOverlay = String.format(
                        "<div style=\"position:absolute;top:0;right:0;width:45%%;height:100%%;"
                        + "overflow:hidden;opacity:0.15;z-index:0;\">"
                        + "<img src=\"%s\" style=\"width:100%%;height:100%%;object-fit:cover;\" />"
                        + "</div>",
                        imageUrl);

                int firstClose = replaced.indexOf('>');
                if (firstClose > 0) {
                    if (!replaced.substring(0, firstClose).contains("position:")) {
                        replaced = replaced.substring(0, firstClose).replace("style=\"",
                                "style=\"position:relative;") + replaced.substring(firstClose);
                        firstClose = replaced.indexOf('>');
                    }
                    replaced = replaced.substring(0, firstClose + 1) + imgOverlay
                            + replaced.substring(firstClose + 1);
                    log.info("图片已作为背景层注入 HTML: {}", imageUrl);
                }
            } else {
                log.info("图片已替换 placeholder 注入 HTML: {}", imageUrl);
            }

            slideConfig.put("slide_html", replaced);
        } catch (Exception e) {
            log.warn("图片注入 HTML 失败: {}", e.getMessage());
        }
    }

    private String expandSectionForHtml(String section, int slideIndex, int totalSlides) {
        String slideType = inferHtmlSlideType(slideIndex, totalSlides, section);

        if ("cover".equals(slideType) || "ending".equals(slideType) || "section".equals(slideType)) {
            return section;
        }

        try {
            String prompt = String.format("""
                    你是一位专业的演示文稿内容编辑。请将以下简短大纲扩写为适合演示文稿展示的详细内容。

                    ## 原始大纲
                    %s

                    ## 扩写要求
                    1. 保留原标题不变
                    2. 将每个要点扩展为 1-2 句话的详细描述（包含具体数据、案例或解释）
                    3. 如果要点少于 4 个，补充相关要点到 4-6 个
                    4. 每个要点应独立成段，适合放在单独的卡片中展示
                    5. 如果涉及数据或统计，添加具体数字（可以合理估算）
                    6. 保持专业但易懂的语言风格
                    7. 使用与原文相同的语言（中文大纲 → 中文扩写）

                    ## 输出格式
                    直接输出扩写后的 Markdown 内容（保留标题层级），不要输出其他解释文字。
                    """, section);

            String expanded = langchainChatService.chat(agentProperties.contentModel(),
                    "你是专业的演示文稿内容编辑，擅长将简短大纲扩写为详细、有深度的演示内容。",
                    prompt);

            if (expanded != null && !expanded.isBlank() && expanded.length() > section.length()) {
                log.info("第{}页内容扩写完成: {}字 → {}字", slideIndex + 1, section.length(), expanded.length());
                return expanded.trim();
            }
        } catch (Exception e) {
            log.warn("第{}页内容扩写失败，使用原始大纲: {}", slideIndex + 1, e.getMessage());
        }

        return section;
    }

    private Map<String, Object> createFallbackSlideConfig(String section, int slideIndex, String templateInfo) {
        Map<String, Object> config = new HashMap<>();
        int suggestedIndex = parseTemplateSlideIndex(section, slideIndex, Integer.MAX_VALUE);
        config.put("template_slide_index", suggestedIndex);

        String cleaned = cleanSectionText(section);
        String[] lines = cleaned.split("\n");
        List<Map<String, Object>> fills = new ArrayList<>();

        String title = lines.length > 0 ? lines[0].replaceAll("^#+\\s*", "") : "Slide " + (slideIndex + 1);
        fills.add(Map.of("shape_id", 0, "text", title));

        if (lines.length > 1) {
            List<String> items = new ArrayList<>();
            for (int i = 1; i < lines.length; i++) {
                String line = lines[i].replaceAll("^[-*#\\s]+", "").trim();
                if (!line.isEmpty()) {
                    items.add(line);
                }
            }
            if (!items.isEmpty()) {
                fills.add(Map.of("shape_id", 1, "items", items));
            }
        }

        config.put("fills", fills);
        return config;
    }

    private Map<String, Object> createFallbackHtmlSlideConfig(String section, int slideIndex, int totalSlides) {
        String cleaned = cleanSectionText(section);
        String[] lines = cleaned.split("\n");
        String title = lines.length > 0 ? lines[0].replaceAll("^#+\\s*", "") : "Slide " + (slideIndex + 1);

        StringBuilder bodyHtml = new StringBuilder();
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].replaceAll("^[-*#\\s]+", "").trim();
            if (!line.isEmpty()) {
                bodyHtml.append(String.format(
                        "<div style=\"font-size:28px;color:#334155;margin:16px 0;"
                        + "padding:12px 20px;background:#F1F5F9;border-radius:8px;"
                        + "border-left:4px solid #2563EB;\">%s</div>", line));
            }
        }

        boolean isDarkSlide = slideIndex == 0 || slideIndex == totalSlides - 1;
        String bg = isDarkSlide
                ? "linear-gradient(135deg, #1E293B, #0F172A)"
                : "#FFFFFF";
        String titleColor = isDarkSlide ? "#F1F5F9" : "#1E293B";
        String titleSize = slideIndex == 0 ? "64px" : "48px";
        String layout = isDarkSlide
                ? "display:flex;align-items:center;justify-content:center;flex-direction:column;"
                : "padding:80px 100px;";
        String accentBar = isDarkSlide
                ? String.format("<div style=\"width:120px;height:4px;background:#2563EB;"
                        + "margin:%s;border-radius:2px;\"></div>",
                        slideIndex == 0 ? "32px auto 0" : "32px auto 0")
                : "";

        String html = String.format(
                "<div style=\"width:1920px;height:1080px;background:%s;%s"
                + "overflow:hidden;font-family:Arial,'Helvetica Neue',sans-serif;\">"
                + "<div style=\"font-size:%s;font-weight:700;color:%s;"
                + "letter-spacing:-0.5px;line-height:1.2;\">%s</div>"
                + "%s%s</div>",
                bg, layout, titleSize, titleColor, title, accentBar, bodyHtml);

        Map<String, Object> config = new HashMap<>();
        config.put("slide_html", html);
        config.put("slide_type", inferHtmlSlideType(slideIndex, totalSlides, section));
        config.put("speaker_notes", "");
        config.put("image_suggestions", List.of());
        config.put("mode", "html");
        return config;
    }

    private String buildGlobalColorScheme(String userStyleHint) {
        if (userStyleHint != null && !userStyleHint.isBlank()) {
            return "USER STYLE DIRECTIVE (highest priority):\n" +
                    userStyleHint + "\n\n" +
                    "ADDITIONAL RULES:\n" +
                    "- Maintain visual consistency across ALL slides.\n" +
                    "- EVERY slide must use gradient backgrounds, card layouts with shadows, decorative shapes.\n" +
                    "- Use the user's described style as the primary design language.\n" +
                    "- Include emoji icon circles, colored accent bars, multi-column grids.\n" +
                    "- NEVER produce a plain white slide with simple text — that is a FAILURE.";
        } else {
            return "DEFAULT DESIGN RULES — apply to EVERY slide:\n" +
                    "- Use a deep blue-to-purple gradient palette (cover/ending: dark gradient, content: light gradient).\n" +
                    "- Primary accent: vibrant blue (#2563EB). Secondary: purple (#7C3AED).\n" +
                    "- ALL slides must have gradient backgrounds (linear-gradient), NOT plain solid white.\n" +
                    "- Content slides: light gradient (e.g. linear-gradient(135deg, #F8FAFC, #EEF2FF)).\n" +
                    "- Cover/ending: dark gradient (e.g. linear-gradient(135deg, #1E293B, #312E81)).\n" +
                    "- Use card-based layouts with box-shadow and border-radius for all content.\n" +
                    "- Include decorative elements: colored circles, accent bars, geometric shapes.\n" +
                    "- Emoji icons in colored circles as visual anchors on each card.\n" +
                    "- NEVER produce a plain white slide with simple text — that is a FAILURE.";
        }
    }

    private String inferSlideType(int slideIndex, int totalSlides, String section) {
        if (section != null) {
            Matcher roleMatcher = Pattern.compile("\\[role=(\\w+)").matcher(section);
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
        if (slideIndex == 0) return "cover（封面页：显示PPT标题和副标题）";
        if (slideIndex == totalSlides - 1) return "ending（结束页：感谢/总结/联系方式）";
        String cleaned = section != null ? cleanSectionText(section).trim() : "";
        if (cleaned.startsWith("## ") && !cleaned.contains("### "))
            return "section（章节过渡页：仅显示章节标题）";
        return "content（正文页：标题 + 要点列表或详细内容）";
    }

    private String inferHtmlSlideType(int slideIndex, int totalSlides, String section) {
        if (slideIndex == 0) return "cover";
        if (slideIndex == totalSlides - 1) return "ending";
        String cleaned = section != null ? cleanSectionText(section).trim() : "";
        if (cleaned.startsWith("## ") && !cleaned.contains("### ")) return "section";
        return "content";
    }

    private int parseTemplateSlideIndex(String section, int slideIndex, int totalSlides) {
        if (section == null) return inferDefaultTemplateIndex(slideIndex, totalSlides);

        Matcher tpMatcher = Pattern
                .compile("\\[TEMPLATE_PAGE]\\s*\\ntemplate_slide_index\\s*=\\s*(\\d+)")
                .matcher(section);
        if (tpMatcher.find()) {
            try {
                return Integer.parseInt(tpMatcher.group(1));
            } catch (NumberFormatException e) { /* ignore */ }
        }

        Matcher matcher = Pattern
                .compile("<!--\\s*template_slide_index\\s*=\\s*(\\d+)\\s*-->")
                .matcher(section);
        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException e) { /* ignore */ }
        }

        return inferDefaultTemplateIndex(slideIndex, totalSlides);
    }

    private int inferDefaultTemplateIndex(int slideIndex, int totalSlides) {
        if (slideIndex == 0) return 0;
        if (slideIndex == totalSlides - 1) return Math.max(1, slideIndex);
        return Math.min(slideIndex, 1);
    }

    private String extractTemplatePageInfo(String templateInfo, int pageIndex) {
        if (templateInfo == null || templateInfo.isBlank()) {
            return "无模板页信息（使用默认布局）";
        }

        int tpStart = templateInfo.indexOf("[TEMPLATE_PAGE]");
        int tpEnd = templateInfo.indexOf("[/TEMPLATE_PAGE]");
        if (tpStart >= 0 && tpEnd > tpStart) {
            return templateInfo.substring(tpStart + "[TEMPLATE_PAGE]".length(), tpEnd).trim();
        }

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

    private String extractTemplateRules(String templateInfo) {
        int rulesIdx = templateInfo.indexOf("=== 使用规则 ===");
        if (rulesIdx >= 0) {
            return templateInfo.substring(rulesIdx);
        }
        return "";
    }

    private String cleanSectionText(String section) {
        if (section == null) return "";
        String cleaned = section.replaceAll("(?s)\\[TEMPLATE_PAGE].*?\\[/TEMPLATE_PAGE]", "");
        cleaned = cleaned.replaceAll("<!--\\s*template_slide_index\\s*=\\s*\\d+\\s*-->", "");
        return cleaned.trim();
    }
}
