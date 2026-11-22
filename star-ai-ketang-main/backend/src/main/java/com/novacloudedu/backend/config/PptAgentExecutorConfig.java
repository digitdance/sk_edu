package com.novacloudedu.backend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * PPT Agent 并发执行器配置
 */
@Slf4j
@Configuration
public class PptAgentExecutorConfig {

    @Bean(name = "pptSlideExecutor", destroyMethod = "shutdown")
    public ExecutorService pptSlideExecutor(PptAgentProperties agentProperties) {
        ExecutorService executor = Executors.newFixedThreadPool(
                agentProperties.concurrency(),
                Thread.ofVirtual().name("ppt-agent-", 0).factory());
        log.info("PPT Slide ExecutorService 初始化: concurrency={}", agentProperties.concurrency());
        return executor;
    }
}
