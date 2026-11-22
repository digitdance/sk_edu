package com.novacloudedu.backend.domain.ai.valueobject;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 模型配置值对象
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ModelConfig {

    private String modelName;
    private BigDecimal temperature;
    private BigDecimal topP;
    private Integer maxTokens;
    /** 智慧体独立 API Key，为空时使用系统全局配置 */
    private String apiKey;
    /** 智慧体独立模型 API Base URL */
    private String modelBaseUrl;

    private ModelConfig(String modelName, BigDecimal temperature, BigDecimal topP, Integer maxTokens,
                        String apiKey, String modelBaseUrl) {
        this.modelName = modelName;
        this.temperature = temperature;
        this.topP = topP;
        this.maxTokens = maxTokens;
        this.apiKey = apiKey;
        this.modelBaseUrl = modelBaseUrl;
    }

    public static ModelConfig create(String modelName, BigDecimal temperature, BigDecimal topP, Integer maxTokens) {
        return create(modelName, temperature, topP, maxTokens, null, null);
    }

    public static ModelConfig create(String modelName, BigDecimal temperature, BigDecimal topP,
                                     Integer maxTokens, String apiKey) {
        return create(modelName, temperature, topP, maxTokens, apiKey, null);
    }

    public static ModelConfig create(String modelName, BigDecimal temperature, BigDecimal topP,
                                     Integer maxTokens, String apiKey, String modelBaseUrl) {
        if (modelName == null || modelName.trim().isEmpty()) {
            modelName = "dashscope/qwen-plus";
        } else if (!modelName.contains("/")) {
            modelName = "dashscope/" + modelName.trim();
        }
        if (temperature == null) {
            temperature = new BigDecimal("0.7");
        }
        if (topP == null) {
            topP = new BigDecimal("0.8");
        }
        if (maxTokens == null || maxTokens <= 0) {
            maxTokens = 2000;
        }

        if (temperature.compareTo(BigDecimal.ZERO) < 0 || temperature.compareTo(BigDecimal.valueOf(2)) > 0) {
            throw new IllegalArgumentException("temperature必须在0-2之间");
        }
        if (topP.compareTo(BigDecimal.ZERO) < 0 || topP.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("topP必须在0-1之间");
        }
        if (maxTokens > 8000) {
            throw new IllegalArgumentException("maxTokens不能超过8000");
        }

        String normalizedApiKey = apiKey != null && !apiKey.isBlank() ? apiKey.trim() : null;
        String normalizedBaseUrl = modelBaseUrl != null && !modelBaseUrl.isBlank() ? modelBaseUrl.trim() : null;
        return new ModelConfig(modelName.trim(), temperature, topP, maxTokens, normalizedApiKey, normalizedBaseUrl);
    }

    public static ModelConfig defaultConfig() {
        return create("dashscope/qwen-plus", new BigDecimal("0.7"), new BigDecimal("0.8"), 2000);
    }

    public boolean hasApiKey() {
        return apiKey != null && !apiKey.isBlank();
    }

    public boolean hasModelBaseUrl() {
        return modelBaseUrl != null && !modelBaseUrl.isBlank();
    }
}
