package com.novacloudedu.backend.infrastructure.ai.agent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.novacloudedu.backend.config.PptAgentProperties;
import com.novacloudedu.backend.infrastructure.ai.ChatModelFactory;
import com.novacloudedu.backend.infrastructure.ai.agent.PptAgentInterfaces.PlannerAgent;
import com.novacloudedu.backend.infrastructure.ai.agent.PptAgentOrchestrator.OutlineResult;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * PPT 大纲规划器（Stage 1: 分析与规划）
 *
 * 负责 PPT 多 Agent 编排流程的第一阶段：
 * - 联网搜索收集资料
 * - 基于调研结果规划结构化大纲
 *
 * 从 PptAgentOrchestrator 中提取，作为独立组件便于维护和测试。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PptOutlinePlanner {

    private final ChatModelFactory chatModelFactory;
    private final WebSearchTool webSearchTool;
    private final ContentResearchTool contentResearchTool;
    private final PptAgentProperties agentProperties;
    private final ObjectMapper objectMapper;

    /**
     * Agent 驱动的大纲规划
     *
     * @param topic        PPT 主题
     * @param requirements 额外要求
     * @param templateInfo 模板结构描述（可选）
     * @return 结构化的大纲结果
     */
    public OutlineResult planOutline(String topic, String requirements, String templateInfo) {
        return planOutline(topic, requirements, templateInfo, null);
    }

    /**
     * Agent 驱动的大纲规划（带任务跟踪）
     *
     * @param topic        PPT 主题
     * @param requirements 额外要求
     * @param templateInfo 模板结构描述（可选）
     * @param tracker      任务跟踪器（可为 null）
     * @return 结构化的大纲结果
     */
    public OutlineResult planOutline(String topic, String requirements,
                                      String templateInfo, AgentTaskTracker tracker) {
        log.info("Stage 1 - PlannerAgent 开始规划大纲: topic={}", topic);

        String searchTaskId = null;
        String planTaskId = null;

        if (tracker != null) {
            searchTaskId = tracker.startNewTask(AgentTaskTracker.AgentRole.RESEARCHER,
                    "联网搜索「" + topic + "」相关资料", "正在搜索...");
        }

        PlannerAgent planner = buildPlannerAgent();
        String input = buildPlannerInput(topic, requirements, templateInfo);

        try {
            String result = planner.planOutline(input);
            log.info("PlannerAgent 大纲规划完成, 结果长度={}", result.length());

            if (tracker != null) {
                tracker.completeTask(searchTaskId, "搜索完成，已收集相关资料");
                planTaskId = tracker.startNewTask(AgentTaskTracker.AgentRole.PLANNER,
                        "基于调研结果规划大纲结构", "正在规划...");
            }

            OutlineResult outlineResult = PptAgentJsonUtils.parseOutlineResult(result);

            if (tracker != null) {
                tracker.completeTask(planTaskId, "大纲规划完成，共生成结构化大纲");
            }

            return outlineResult;
        } catch (Exception e) {
            log.error("PlannerAgent 大纲规划失败: topic={}", topic, e);
            if (tracker != null) {
                if (searchTaskId != null) tracker.failTask(searchTaskId, e.getMessage());
                if (planTaskId != null) tracker.failTask(planTaskId, e.getMessage());
            }
            throw new RuntimeException("Agent大纲规划失败: " + e.getMessage(), e);
        }
    }

    /**
     * 构建 PlannerAgent 实例
     */
    private PlannerAgent buildPlannerAgent() {
        String modelId = agentProperties.plannerModel() != null
            ? agentProperties.plannerModel()
            : chatModelFactory.getDefaultModelId();
        ChatModel model = chatModelFactory.createChatModelWithParams(
                modelId, 0.7, 0.9, 4096);
        return AiServices.builder(PlannerAgent.class)
                .chatModel(model)
                .tools(webSearchTool)
                .build();
    }

    /**
     * 构建 PlannerAgent 输入提示词
     */
    private String buildPlannerInput(String topic, String requirements, String templateInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append("Presentation topic: ").append(topic).append("\n\n");

        if (requirements != null && !requirements.isBlank()) {
            sb.append("Additional requirements: ").append(requirements).append("\n\n");
        }
        if (templateInfo != null && !templateInfo.isBlank()) {
            sb.append("## Template Structure (you MUST strictly follow the \"Outline Structure Requirements\" within)\n\n");
            sb.append(templateInfo).append("\n\n");
            sb.append("IMPORTANT:\n");
            sb.append("- Strictly follow the Outline Structure Requirements to control ## (chapter) and ### (content page) counts\n");
            sb.append("- Each ### corresponds to one PPT slide; total must match the template's content page count\n");
            sb.append("- Do NOT annotate template_slide_index; the system assigns it automatically\n");
            sb.append("- If Audience and Tone are specified above, match the language register and formality accordingly\n\n");
        }

        sb.append("First use web search tools to gather the latest information on this topic, then create the outline.");
        return sb.toString();
    }
}
