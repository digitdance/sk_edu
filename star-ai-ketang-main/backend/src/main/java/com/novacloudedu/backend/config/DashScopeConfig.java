package com.novacloudedu.backend.config;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.embeddings.TextEmbedding;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * 阿里云灵积平台 (DashScope) 配置类
 */
@Slf4j
@Configuration
public class DashScopeConfig {

    @Value("${ai.dashscope.api-key}")
    private String apiKey;

    @Value("${ai.dashscope.llm.model-name}")
    private String llmModelName;

    @Value("${ai.dashscope.embedding.model-name}")
    private String embeddingModelName;

    @Value("${ai.dashscope.llm.vision-model-name:qwen-vl-max}")
    private String visionModelName;

    /**
     * 配置 DashScope Generation 客户端
     * 延迟加载：仅在需要时初始化，减少启动时间
     */
    @Bean
    @Lazy
    public Generation generation() {
        log.info("初始化 DashScope Generation 客户端，模型: {}", llmModelName);
        // DashScope SDK 会自动从环境变量或配置中读取 API Key
        System.setProperty("DASHSCOPE_API_KEY", apiKey);
        return new Generation();
    }

    /**
     * 配置 DashScope MultiModalConversation 客户端（视觉多模态）
     * <p>
     * 延迟加载：此bean仅在需要多模态视觉识别时才初始化，节省启动时间。
     * API Key 已在 generation() bean 中设置，创建 MultiModalConversation 对象无副作用。
     */
    @Bean
    @Lazy
    public MultiModalConversation multiModalConversation() {
        log.info("初始化 DashScope MultiModalConversation 客户端（延迟加载），模型: {}", visionModelName);
        return new MultiModalConversation();
    }

    /**
     * 配置 DashScope TextEmbedding 客户端
     * 延迟加载：仅在需要嵌入向量时初始化，减少启动时间
     */
    @Bean
    @Lazy
    public TextEmbedding textEmbedding() {
        log.info("初始化 DashScope TextEmbedding 客户端，模型: {}", embeddingModelName);
        System.setProperty("DASHSCOPE_API_KEY", apiKey);
        return new TextEmbedding();
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getLlmModelName() {
        return llmModelName;
    }

    public String getEmbeddingModelName() {
        return embeddingModelName;
    }
}
