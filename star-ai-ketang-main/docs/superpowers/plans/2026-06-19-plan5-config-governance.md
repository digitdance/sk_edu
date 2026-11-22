# Plan 5: Configuration Governance Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Reduce configuration complexity — application.yml 587→~300 lines, docker-compose backend env 51→~5 lines, eliminate AI provider repetition, enable fail-fast validation

**Architecture:** YAML anchors for AI provider defaults, env_file for docker-compose automation, @ConfigurationProperties for type-safe PPT Agent config, profile split for AI/media sections

**Tech Stack:** Spring Boot 3.5.8 YAML, Docker Compose env_file, @ConfigurationProperties + @Validated

**Estimated Time:** 1 day (4-6 hours active work)

**Risk:** Low — each task independently testable, no behavior change, only config structure

---

## File Structure

**Created:**
- `backend/src/main/resources/application-ai.yml` (~200 lines)
- `backend/src/main/resources/application-media.yml` (~30 lines)
- `backend/src/main/java/com/novacloudedu/backend/config/PptAgentProperties.java` (~50 lines)

**Modified:**
- `backend/src/main/resources/application.yml` (587→~300 lines)
- `docker/docker-compose.yml` (backend env: 51→~5 lines)
- `backend/src/main/java/com/novacloudedu/backend/infrastructure/ai/agent/PptAgentOrchestrator.java` (remove 14 @Value, inject Properties)
- `backend/pom.xml` (add spring-boot-configuration-processor)

---

## Task 1: Add Configuration Processor Dependency

**Files:**
- Modify: `backend/pom.xml:124-130` (after lombok dependency)

- [ ] **Step 1: Add spring-boot-configuration-processor dependency**

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <optional>true</optional>
        </dependency>
```

Insert after the lombok dependency block (around line 128).

- [ ] **Step 2: Verify Maven resolves dependency**

```bash
cd backend
./mvnw dependency:tree | grep configuration-processor
```

Expected output: Contains `spring-boot-configuration-processor` artifact

- [ ] **Step 3: Commit**

```bash
git add backend/pom.xml
git commit -m "build: add spring-boot-configuration-processor for @ConfigurationProperties"
```

---

## Task 2: Create PptAgentProperties

**Files:**
- Create: `backend/src/main/java/com/novacloudedu/backend/config/PptAgentProperties.java`

- [ ] **Step 1: Write PptAgentProperties record**

```java
package com.novacloudedu.backend.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * PPT Agent 多模型编排配置
 */
@ConfigurationProperties(prefix = "ppt.agent")
@Validated
@Builder
public record PptAgentProperties(
    String plannerModel,
    String contentModel,
    String designModel,
    String designToolModel,
    String evaluatorModel,
    String layoutSelectorModel,
    String htmlSlideModel,
    
    boolean enableContentValidation,
    boolean enableLayoutSelection,
    boolean enablePerSlideInspection,
    
    @Min(1) @Max(10) int concurrency,
    @Min(0) @Max(100) int qualityThreshold,
    @Min(1) @Max(5) int maxRepairRounds,
    @Min(0) @Max(100) int slideScoreThreshold
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
    }
}
```

- [ ] **Step 2: Enable @ConfigurationProperties scanning**

Add to `backend/src/main/java/com/novacloudedu/backend/BackendApplication.java`:

```java
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan  // Add this
public class BackendApplication {
```

- [ ] **Step 3: Verify compilation**

```bash
cd backend
./mvnw compile -DskipTests
```

Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add backend/src/main/java/com/novacloudedu/backend/config/PptAgentProperties.java \
         backend/src/main/java/com/novacloudedu/backend/BackendApplication.java
git commit -m "feat(config): add PptAgentProperties with validation"
```

---

## Task 3: Update PptAgentOrchestrator to Use Properties

**Files:**
- Modify: `backend/src/main/java/com/novacloudedu/backend/infrastructure/ai/agent/PptAgentOrchestrator.java:37-90`

- [ ] **Step 1: Replace @Value fields with PptAgentProperties injection**

Remove lines 45-84 (14 @Value annotations + fields), replace with:

```java
    private final PptAgentProperties agentProperties;
    private final ChatModelFactory chatModelFactory;
    private final LangchainChatService langchainChatService;
    // ... other final fields unchanged
```

Update constructor (line ~89):

```java
    public PptAgentOrchestrator(
            PptAgentProperties agentProperties,
            ChatModelFactory chatModelFactory,
            LangchainChatService langchainChatService,
            WebSearchTool webSearchTool,
            ImageGenerationTool imageGenerationTool,
            ContentResearchTool contentResearchTool,
            com.novacloudedu.backend.infrastructure.ppt.PptServiceClient pptServiceClient,
            ObjectMapper objectMapper) {
        this.agentProperties = agentProperties;
        this.chatModelFactory = chatModelFactory;
        // ... rest unchanged
    }
```

- [ ] **Step 2: Update model ID references with fallback logic**

In methods like `buildPlannerAgent()` (line ~911), replace:

```java
private PlannerAgent buildPlannerAgent() {
    String modelId = agentProperties.plannerModel() != null 
        ? agentProperties.plannerModel() 
        : chatModelFactory.getDefaultModelId();
    ChatModel chatModel = chatModelFactory.create(modelId);
    return AiServices.create(PlannerAgent.class, chatModel);
}
```

Repeat for: `buildContentAgent()`, `buildDesignAgent()`, `buildEvaluatorAgent()`, `buildLayoutSelectorAgent()`, `buildHtmlSlideAgent()`

- [ ] **Step 3: Update config value references**

Replace all:
- `qualityThreshold` → `agentProperties.qualityThreshold()`
- `maxRepairRounds` → `agentProperties.maxRepairRounds()`
- `slideScoreThreshold` → `agentProperties.slideScoreThreshold()`
- `enableContentValidation` → `agentProperties.enableContentValidation()`
- `enableLayoutSelection` → `agentProperties.enableLayoutSelection()`
- `enablePerSlideInspection` → `agentProperties.enablePerSlideInspection()`

- [ ] **Step 4: Verify compilation**

```bash
cd backend
./mvnw compile -DskipTests
```

Expected: BUILD SUCCESS

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/com/novacloudedu/backend/infrastructure/ai/agent/PptAgentOrchestrator.java
git commit -m "refactor(ppt-agent): use PptAgentProperties instead of @Value"
```


## Task 4: Add ChatModelFactory Default Model Getter

**Files:**
- Modify: `backend/src/main/java/com/novacloudedu/backend/infrastructure/ai/ChatModelFactory.java`

- [ ] **Step 1: Add getDefaultModelId() method**

```java
    /**
     * 获取默认模型 ID（用于 Properties fallback）
     */
    public String getDefaultModelId() {
        return chatModelsProperties.getDefaultModel();
    }
```

Add after existing public methods.

- [ ] **Step 2: Verify compilation**

```bash
cd backend
./mvnw compile -DskipTests
```

Expected: BUILD SUCCESS

- [ ] **Step 3: Commit**

```bash
git add backend/src/main/java/com/novacloudedu/backend/infrastructure/ai/ChatModelFactory.java
git commit -m "feat(ai): add getDefaultModelId to ChatModelFactory"
```

---

## Task 5: Split AI Config to application-ai.yml

**Files:**
- Create: `backend/src/main/resources/application-ai.yml`
- Modify: `backend/src/main/resources/application.yml:173-515`

Due to size, split into 3 steps:

### Step 1: Create application-ai.yml Part 1 (AI providers with YAML anchors)

- [ ] **Create application-ai.yml with chat-models section**

```yaml
# =================================================================
# AI 配置 (Profile: ai)
# 使用 YAML 锚点减少重复配置
# =================================================================
ai:
  # ---- 对话模型路由 ----
  chat-models:
    default-model: ${AI_DEFAULT_MODEL:deepseek/deepseek-v4-pro}
    default-vision-model: ${AI_DEFAULT_VISION_MODEL:openrouter/google/gemini-3-flash-preview}
    
    # YAML 锚点：定义通用默认值
    provider-defaults: &provider-defaults
      temperature: 0.7
      max-tokens: 4000
      top-p: 0.8
    
    providers:
      dashscope:
        enabled: true
        api-key: ${DASHSCOPE_API_KEY:}
        base-url: ${DASHSCOPE_BASE_URL:https://dashscope.aliyuncs.com/api/v1}
        models:
          qwen-max: { <<: *provider-defaults, type: text, max-tokens: 2000 }
          qwen-plus: { <<: *provider-defaults, type: text, max-tokens: 2000 }
          qwen-turbo: { <<: *provider-defaults, type: text, max-tokens: 2000 }
          qwen-long: { <<: *provider-defaults, type: text, max-tokens: 6000 }
          qwen-vl-max: { <<: *provider-defaults, type: vision, max-tokens: 2000 }
          qwen-vl-plus: { <<: *provider-defaults, type: vision, max-tokens: 2000 }
          qwen3-235b-a22b: { <<: *provider-defaults, type: text, max-tokens: 8000 }
      
      openai:
        enabled: ${OPENAI_ENABLED:false}
        api-key: ${OPENAI_API_KEY:}
        base-url: ${OPENAI_BASE_URL:https://api.openai.com/v1}
        models:
          gpt-4o: { <<: *provider-defaults, type: vision }
          gpt-4o-mini: { <<: *provider-defaults, type: vision, max-tokens: 2000 }
      
      deepseek:
        enabled: ${DEEPSEEK_ENABLED:true}
        api-key: ${DEEPSEEK_API_KEY:}
        base-url: ${DEEPSEEK_BASE_URL:https://api.deepseek.com/v1}
        models:
          deepseek-chat: { <<: *provider-defaults, type: text }
          deepseek-reasoner: { <<: *provider-defaults, type: text, temperature: 0.6, max-tokens: 8000 }
          deepseek-v4-pro: { <<: *provider-defaults, type: text, max-tokens: 128000 }
      
      moonshot:
        enabled: ${MOONSHOT_ENABLED:false}
        api-key: ${MOONSHOT_API_KEY:}
        base-url: ${MOONSHOT_BASE_URL:https://api.moonshot.cn/v1}
        models:
          moonshot-v1-128k: { <<: *provider-defaults, type: text }
          moonshot-v1-32k: { <<: *provider-defaults, type: text }
      
      zhipu:
        enabled: ${ZHIPU_ENABLED:false}
        api-key: ${ZHIPU_API_KEY:}
        models:
          glm-4: { <<: *provider-defaults, type: text }
          glm-4v: { <<: *provider-defaults, type: vision, max-tokens: 2000 }
      
      siliconflow:
        enabled: ${SILICONFLOW_ENABLED:false}
        api-key: ${SILICONFLOW_API_KEY:}
        base-url: ${SILICONFLOW_BASE_URL:https://api.siliconflow.cn/v1}
        models:
          deepseek-ai/DeepSeek-V3: { <<: *provider-defaults, type: text }
          Qwen/Qwen2.5-72B-Instruct: { <<: *provider-defaults, type: text }
      
      ollama:
        enabled: ${OLLAMA_ENABLED:false}
        base-url: ${OLLAMA_BASE_URL:http://localhost:11434}
        models:
          llama3: { <<: *provider-defaults, type: text }
          llava: { <<: *provider-defaults, type: vision }
      
      openrouter:
        enabled: ${OPENROUTER_ENABLED:false}
        api-key: ${OPENROUTER_API_KEY:}
        base-url: ${OPENROUTER_BASE_URL:https://openrouter.ai/api/v1}
        models:
          claude-opus-4-8: { <<: *provider-defaults, type: text, max-tokens: 16000 }
          openai/gpt-5.2: { <<: *provider-defaults, type: text, max-tokens: 8000 }
          openai/gpt-5-nano: { <<: *provider-defaults, type: text }
          google/gemini-3-flash-preview: { <<: *provider-defaults, type: vision, max-tokens: 8000 }
          google/gemini-2.5-flash-preview: { <<: *provider-defaults, type: vision, max-tokens: 8000 }
          anthropic/claude-sonnet-4: { <<: *provider-defaults, type: text, max-tokens: 8000 }
          openai/gpt-4o: { <<: *provider-defaults, type: vision }
          openai/gpt-4.1-mini: { <<: *provider-defaults, type: text }
          deepseek/deepseek-r1: { <<: *provider-defaults, type: text, temperature: 0.6, max-tokens: 8000 }
          qwen/qwen3-235b-a22b: { <<: *provider-defaults, type: text, max-tokens: 8000 }
```

### Step 2: Append rest of AI config to application-ai.yml

- [ ] **Append image/video models, DashScope, and feature configs**

```yaml
  # ---- 文生图 ----
  image-models:
    enabled: true
    default-model: dashscope/wan2.6-t2i
    upload-to-oss: true
    providers:
      dashscope:
        enabled: true
        api-key: ${DASHSCOPE_API_KEY:}
        type: dashscope
        models:
          wan2.6-t2i: { size: "1280*1280", n: 1, prompt-extend: true }
          wanx2.1-t2i-plus: { size: "1024*1024", n: 1 }
      siliconflow:
        enabled: ${SILICONFLOW_ENABLED:false}
        api-key: ${SILICONFLOW_API_KEY:}
        base-url: ${SILICONFLOW_BASE_URL:https://api.siliconflow.cn/v1}
        type: openai
        models:
          black-forest-labs/FLUX.1-schnell: { size: "1024*1024", steps: 4, n: 1 }

  # ---- 文生视频 ----
  video-models:
    enabled: true
    default-model: dashscope/wan2.6-t2v
    upload-to-oss: true
    providers:
      dashscope:
        enabled: true
        api-key: ${DASHSCOPE_API_KEY:}
        type: dashscope
        models:
          wan2.6-t2v: { size: "1280*720", duration: 5, prompt-extend: true }
          wan2.2-t2v-plus: { size: "832*480", duration: 5, prompt-extend: true }

  # ---- DashScope 基础（向量/重排） ----
  dashscope:
    api-key: ${DASHSCOPE_API_KEY:}
    base-url: ${DASHSCOPE_BASE_URL:https://dashscope.aliyuncs.com/api/v1}
    llm:
      model-name: ${DASHSCOPE_LLM_MODEL:qwen-max}
      vision-model-name: ${DASHSCOPE_VISION_MODEL:qwen-vl-max}
      temperature: 0.7
      max-tokens: 2000
      top-p: 0.8
      enable-search: false
    embedding:
      model-name: ${DASHSCOPE_EMBEDDING_MODEL:text-embedding-v2}
      dimension: 1536
      text-type: document
    rerank:
      model-name: ${DASHSCOPE_RERANK_MODEL:gte-rerank}
      enabled: true

  # ---- 对话记忆 ----
  conversation:
    max-history: 10
    context-window: 4000
    window-size: 20
    summarize-threshold: 30

  # ---- RAG ----
  rag:
    enabled: ${RAG_ENABLED:true}
    top-k: 5
    similarity-threshold: 0.7
    chunk-size: 500
    chunk-overlap: 50
    max-context-length: 3000

  # ---- AI 总结 ----
  summary:
    cache-enabled: true
    cache-ttl: 7d
    default-type: detailed

  # ---- 知识点提取 ----
  knowledge:
    max-points: 20
    min-confidence: 0.6

  # ---- 测试题生成 ----
  quiz:
    default-count: 5
    difficulty-distribution:
      easy: 0.3
      medium: 0.5
      hard: 0.2

# =================================================================
# Tavily Search API（PPT Agent 联网搜索）
# =================================================================
tavily:
  api-key: "${TAVILY_API_KEY:}"

# =================================================================
# PPT Agent 多智能体
# =================================================================
ppt:
  agent:
    # 模型 ID 留空表示继承 ai.chat-models.default-model
    planner-model: ${PPT_AGENT_PLANNER_MODEL:}
    content-model: ${PPT_AGENT_CONTENT_MODEL:}
    design-model: ${PPT_AGENT_DESIGN_MODEL:}
    design-tool-model: ${PPT_AGENT_DESIGN_TOOL_MODEL:}
    evaluator-model: ${PPT_AGENT_EVALUATOR_MODEL:}
    layout-selector-model: ${PPT_AGENT_LAYOUT_SELECTOR_MODEL:}
    html-slide-model: ${PPT_AGENT_HTML_SLIDE_MODEL:}
    enable-content-validation: ${PPT_AGENT_CONTENT_VALIDATION:true}
    enable-layout-selection: ${PPT_AGENT_LAYOUT_SELECTION:true}
    enable-per-slide-inspection: ${PPT_AGENT_PER_SLIDE_INSPECTION:true}
    concurrency: ${PPT_AGENT_CONCURRENCY:3}
    quality-threshold: ${PPT_AGENT_QUALITY_THRESHOLD:65}
    max-repair-rounds: ${PPT_AGENT_MAX_REPAIR_ROUNDS:2}
    slide-score-threshold: ${PPT_AGENT_SLIDE_SCORE_THRESHOLD:60}
    image-search:
      enabled: ${PPT_AGENT_IMAGE_SEARCH_ENABLED:false}
      provider: ${PPT_AGENT_IMAGE_SEARCH_PROVIDER:bing}
      api-key: "${PPT_AGENT_IMAGE_SEARCH_API_KEY:}"
```

### Step 3: Delete AI section from application.yml and verify

- [ ] **Delete lines 173-515 from application.yml**

Remove entire `ai:` block (dashscope, chat-models, image-models, video-models, conversation, rag, summary, knowledge, quiz), `tavily:` block (lines 464-467), and `ppt.agent:` block (lines 477-498).

- [ ] **Verify YAML syntax**

```bash
cd backend/src/main/resources
python3 -c "import yaml; yaml.safe_load(open('application-ai.yml'))"
python3 -c "import yaml; yaml.safe_load(open('application.yml'))"
```

Expected: No output (valid YAML for both)

- [ ] **Commit**

```bash
git add backend/src/main/resources/application.yml backend/src/main/resources/application-ai.yml
git commit -m "refactor(config): split AI config to application-ai.yml with YAML anchors

- 使用 YAML 锚点 &provider-defaults 消除 AI provider 重复配置
- PPT Agent 模型槽位留空表示继承 default-model
- application.yml 从 587 行减至约 300 行"
```

---

## Task 6: Update docker/.env - Remove PPT Agent Model Overrides

**Files:**
- Modify: `docker/.env:61-75`

- [ ] **Step 1: Remove explicit PPT Agent model overrides**

Delete lines 61-62 (AI_DEFAULT_MODEL/AI_DEFAULT_VISION_MODEL specific values already set), and lines 90-94 (PPT_AGENT_* model overrides).

Keep only lines that differ from application-ai.yml defaults:
```
# docker/.env lines 90-94 DELETE these:
PPT_AGENT_PLANNER_MODEL=openrouter/claude-opus-4-8
PPT_AGENT_CONTENT_MODEL=openrouter/claude-opus-4-8
PPT_AGENT_DESIGN_MODEL=openrouter/claude-opus-4-8
...
```

Result: PPT Agent models now inherit from AI_DEFAULT_MODEL (openrouter/claude-opus-4-8).

- [ ] **Step 2: Commit**

```bash
git add docker/.env
git commit -m "chore(config): remove PPT Agent model overrides, inherit from default"
```

---

## Task 7: Docker Compose env_file Automation

**Files:**
- Modify: `docker/docker-compose.yml:180-279` (backend service)

- [ ] **Step 1: Replace backend environment block with env_file**

Replace lines 188-256 (51 environment variables) with:

```yaml
  backend:
    build:
      context: ../backend
      dockerfile: Dockerfile
    container_name: nova-backend
    ports:
      - "${BACKEND_PORT:-8081}:8080"
    env_file:
      - path: .env
        required: true
    environment:
      # ---- Spring Profile ----
      - SPRING_PROFILES_ACTIVE=docker,ai
      # ---- Docker Socket (容器特有) ----
      - DOCKER_SOCK_PATH=${DOCKER_SOCK_PATH:-/var/run/docker.sock}
      # ---- SRS 内部地址（容器特有） ----
      - SRS_HTTP_BASE=http://srs:8080
    volumes:
      - ${DOCKER_SOCK_PATH:-/var/run/docker.sock}:/var/run/docker.sock:rw
      - python-sandbox-venvs:/opt/novacloudedu/venvs
      - python-sandbox-tmp:/tmp/novacloudedu-sandbox
    depends_on:
      # ... unchanged
```

Key changes:
- Add `env_file: - path: .env` → Spring reads all vars from .env automatically
- Keep only 3 explicit env vars: SPRING_PROFILES_ACTIVE (add `ai` profile), DOCKER_SOCK_PATH, SRS_HTTP_BASE
- All other 48 vars (DB_*, REDIS_*, AI_*, etc.) inherited from .env via Spring relaxed binding

- [ ] **Step 2: Verify .env var names match Spring ${VAR} syntax**

Check that .env uses Spring-compatible names:
```bash
grep -E "^[A-Z_]+=" docker/.env | head -20
```

Expected: All vars like `DB_NAME=`, `REDIS_PASSWORD=`, etc. (no neo4j/password split format issues)

- [ ] **Step 3: Commit**

```bash
git add docker/docker-compose.yml
git commit -m "refactor(docker): use env_file for backend, reduce 51→3 explicit env vars"
```

---

## Task 8: Integration Test - Verify Config Changes

**Files:**
- Test: Backend startup + PPT generation smoke test

- [ ] **Step 1: Rebuild backend with new config**

```bash
cd docker
docker compose build backend
```

Expected: BUILD SUCCESS

- [ ] **Step 2: Start all services**

```bash
docker compose up -d
```

- [ ] **Step 3: Monitor backend logs for validation errors**

```bash
docker compose logs -f backend | grep -E "Validation|ConfigurationProperties|PptAgentProperties"
```

Expected: No validation errors, PptAgentProperties loaded successfully

- [ ] **Step 4: Verify PPT Agent inherits default model**

```bash
docker compose exec backend curl -s http://localhost:8080/actuator/configprops | grep -A 30 "pptAgentProperties"
```

Expected: Model IDs show as `openrouter/claude-opus-4-8` (inherited from default)

- [ ] **Step 5: Smoke test PPT generation**

Use existing test account to trigger PPT generation, verify no errors.

```bash
# Via web UI or API call to /api/ppt/generate
curl -X POST http://localhost:8081/api/ppt/generate \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"topic": "测试主题", "requirements": "5页PPT"}'
```

Expected: HTTP 200, PPT generation starts (may take time, just verify no immediate error)

- [ ] **Step 6: Commit verification note**

```bash
git commit --allow-empty -m "test: verify config governance changes - PPT Agent + env_file OK"
```

---

## Verification Checklist

After completing all tasks:

- [ ] `application.yml` < 350 lines
- [ ] `application-ai.yml` created (~250 lines)
- [ ] docker-compose.yml backend env < 10 lines
- [ ] Backend starts without validation errors
- [ ] PptAgentProperties loads with correct defaults
- [ ] PPT generation smoke test passes
- [ ] No regression in AI chat endpoints
- [ ] `mvn clean package` passes

---

## Rollback Plan

If config changes cause startup failures:

1. Revert commits: `git revert HEAD~8..HEAD`
2. Rebuild: `docker compose build backend`
3. Restart: `docker compose up -d backend`

Or restore from specific commit:
```bash
git checkout b04c18c  # Before Plan 5 started
docker compose build backend
docker compose up -d
```
