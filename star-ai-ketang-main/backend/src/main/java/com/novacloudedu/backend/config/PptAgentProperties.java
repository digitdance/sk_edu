package com.novacloudedu.backend.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * PPT Agent 多模型编排配置
 *
 * <p>用于配置 PPT 生成多智能体的模型选择、并发度、质量阈值等参数。
 * 模型 ID 字段留空（null）时，将自动继承 {@code ai.chat-models.default-model}。
 *
 * <p>示例配置：
 * <pre>
 * ppt:
 *   agent:
 *     planner-model: openrouter/claude-opus-4-8
 *     concurrency: 3
 *     quality-threshold: 65
 * </pre>
 */
@ConfigurationProperties(prefix = "ppt.agent")
@Validated
public record PptAgentProperties(
    String plannerModel,
    String contentModel,
    String designModel,
    String designToolModel,
    String evaluatorModel,
    String layoutSelectorModel,
    String htmlSlideModel,

    Boolean enableContentValidation,
    Boolean enableLayoutSelection,
    Boolean enablePerSlideInspection,

    @Min(1) @Max(10) Integer concurrency,
    @Min(0) @Max(100) Integer qualityThreshold,
    @Min(1) @Max(5) Integer maxRepairRounds,
    @Min(0) @Max(100) Integer slideScoreThreshold
) {
    public PptAgentProperties {
        // Apply defaults for null model IDs (will fallback to ai.chat-models.default-model)
        plannerModel = plannerModel != null && !plannerModel.isBlank() ? plannerModel : null;
        contentModel = contentModel != null && !contentModel.isBlank() ? contentModel : null;
        designModel = designModel != null && !designModel.isBlank() ? designModel : null;
        designToolModel = designToolModel != null && !designToolModel.isBlank() ? designToolModel : null;
        evaluatorModel = evaluatorModel != null && !evaluatorModel.isBlank() ? evaluatorModel : null;
        layoutSelectorModel = layoutSelectorModel != null && !layoutSelectorModel.isBlank() ? layoutSelectorModel : null;
        htmlSlideModel = htmlSlideModel != null && !htmlSlideModel.isBlank() ? htmlSlideModel : null;

        // Apply defaults for configuration fields
        enableContentValidation = enableContentValidation != null ? enableContentValidation : true;
        enableLayoutSelection = enableLayoutSelection != null ? enableLayoutSelection : true;
        enablePerSlideInspection = enablePerSlideInspection != null ? enablePerSlideInspection : true;
        concurrency = concurrency != null ? concurrency : 3;
        qualityThreshold = qualityThreshold != null ? qualityThreshold : 65;
        maxRepairRounds = maxRepairRounds != null ? maxRepairRounds : 2;
        slideScoreThreshold = slideScoreThreshold != null ? slideScoreThreshold : 60;
    }
}
