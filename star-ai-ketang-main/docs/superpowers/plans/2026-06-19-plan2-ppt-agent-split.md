# Plan 2: PPT Agent Split Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Refactor PptAgentOrchestrator.java (2430 lines) into 6 focused classes by agent stage/role

**Architecture:** Facade pattern - PptAgentOrchestrator becomes thin coordinator, delegates to 5 stage-specific classes + 1 utility class

**Tech Stack:** Spring Boot 3.5.8, Langchain4j, Java 21 records

**Estimated Time:** 1.5 days (6-8 hours active work)

**Risk:** Medium — large refactoring but each stage independently testable, facade preserves public API

---

## Background

从启动日志和代码分析（Phase 2 spec Section 2），`PptAgentOrchestrator.java` 承担 6 类职责共 2430 行：

| 职责 | 行数估计 | 关键方法 |
|------|---------|---------|
| 生命周期 + 配置 | ~30 | `init()`, PptAgentProperties 注入 |
| 大纲规划（Stage 1） | ~120 | `planOutline`, `buildPlannerAgent`, `parseOutlineResult` |
| 幻灯片生成（Stage 2） | ~850 | `generateSlidesParallel`, `generateHtmlSlidesParallel`, `enrichSlideWithDesign` |
| 质量评估（Stage 3） | ~250 | `evaluatePresentation*`, `parseEvaluationResult` |
| 反思修复（Stage 3b） | ~550 | `generateWithReflection*`, `repairWeakSlides`, `inspectAndRepairSlide` |
| JSON / 工具方法 | ~200 | `parseJsonSafe`, `parseSlideConfigSafe`, `extractPreviewImageUrls` |

**Target:** 拆分为 6 个类，每个 < 900 行，保持 public API 不变（调用方零改动）。

---

## File Structure

**Created:**
- `backend/src/main/java/com/novacloudedu/backend/infrastructure/ai/agent/PptOutlinePlanner.java` (~180 lines)
- `backend/src/main/java/com/novacloudedu/backend/infrastructure/ai/agent/PptSlideFactory.java` (~850 lines)
- `backend/src/main/java/com/novacloudedu/backend/infrastructure/ai/agent/PptEvaluator.java` (~280 lines)
- `backend/src/main/java/com/novacloudedu/backend/infrastructure/ai/agent/PptReflector.java` (~600 lines)
- `backend/src/main/java/com/novacloudedu/backend/infrastructure/ai/agent/PptAgentJsonUtils.java` (~180 lines)

**Modified:**
- `backend/src/main/java/com/novacloudedu/backend/infrastructure/ai/agent/PptAgentOrchestrator.java` (2430→~350 lines, facade)

**Unchanged:**
- `backend/src/main/java/com/novacloudedu/backend/infrastructure/ai/agent/PptAgentInterfaces.java` (agent interfaces)

---

## Task 1: Create PptAgentJsonUtils (Utility Extraction)

**Files:**
- Create: `backend/src/main/java/com/novacloudedu/backend/infrastructure/ai/agent/PptAgentJsonUtils.java`

- [ ] **Step 1: Extract JSON parsing utilities**

Create utility class with static methods:

```java
package com.novacloudedu.backend.infrastructure.ai.agent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.regex.Pattern;

/**
 * PPT Agent JSON 解析与数据提取工具类
 */
@Slf4j
@UtilityClass
public class PptAgentJsonUtils {

    private static final Pattern JSON_BLOCK_PATTERN = Pattern.compile(
            "```json\\s*([\\s\\S]*?)```|```([\\s\\S]*?)```|\\{[\\s\\S]*}");

    /**
     * 从文本中提取 JSON（支持 markdown code block）
     */
    public static String extractJsonFromText(String text) {
        // Implementation from current parseJsonSafe
    }

    /**
     * 解析 OutlineResult JSON
     */
    public static OutlineResult parseOutlineResult(String json, ObjectMapper objectMapper) {
        // Implementation from current parseOutlineResult
    }

    /**
     * 解析 SlideConfig JSON
     */
    public static SlideConfig parseSlideConfigSafe(String json, ObjectMapper objectMapper) {
        // Implementation from current parseSlideConfigSafe
    }

    /**
     * 从幻灯片内容提取预览图 URL
     */
    public static List<String> extractPreviewImageUrls(List<Slide> slides) {
        // Implementation from current extractPreviewImageUrls
    }

    /**
     * 解析 EvaluationResult JSON
     */
    public static EvaluationResult parseEvaluationResult(String json, ObjectMapper objectMapper) {
        // Implementation from current parseEvaluationResult
    }
}
```

- [ ] **Step 2: Move all JSON parsing methods from PptAgentOrchestrator**

Search for methods:
- `parseJsonSafe` / `extractJsonFromText`
- `parseOutlineResult`
- `parseSlideConfigSafe`
- `extractPreviewImageUrls`
- `parseEvaluationResult`
- Any other JSON/string parsing utilities

Move logic to PptAgentJsonUtils, keeping method signatures compatible.

- [ ] **Step 3: Add unit tests**

Create `PptAgentJsonUtilsTest.java`:
```java
@Test
void testExtractJsonFromMarkdownBlock() {
    String text = "```json\n{\"title\": \"test\"}\n```";
    String json = PptAgentJsonUtils.extractJsonFromText(text);
    assertTrue(json.contains("\"title\""));
}

@Test
void testParseOutlineResult() {
    String json = "{\"title\":\"AI\",\"slides\":[{\"title\":\"Intro\"}]}";
    OutlineResult result = PptAgentJsonUtils.parseOutlineResult(json, objectMapper);
    assertEquals("AI", result.title());
}
```

- [ ] **Step 4: Verify compilation**

```bash
cd backend
./mvnw compile -DskipTests
```

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/com/novacloudedu/backend/infrastructure/ai/agent/PptAgentJsonUtils.java \
         backend/src/test/java/com/novacloudedu/backend/infrastructure/ai/agent/PptAgentJsonUtilsTest.java
git commit -m "refactor(ppt-agent): extract JSON utilities to PptAgentJsonUtils"
```

---

## Task 2: Create PptOutlinePlanner (Stage 1 Extraction)

**Files:**
- Create: `backend/src/main/java/com/novacloudedu/backend/infrastructure/ai/agent/PptOutlinePlanner.java`

- [ ] **Step 1: Create PptOutlinePlanner class**

```java
package com.novacloudedu.backend.infrastructure.ai.agent;

import com.novacloudedu.backend.config.PptAgentProperties;
import com.novacloudedu.backend.infrastructure.ai.ChatModelFactory;
import com.novacloudedu.backend.infrastructure.ai.agent.PptAgentInterfaces.*;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * PPT Agent Stage 1: 大纲规划器
 * 
 * 负责分析主题、联网搜索、生成大纲结构
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
     * 规划大纲（带任务跟踪）
     */
    public OutlineResult planOutline(String topic, String requirements, 
                                      String templateInfo, AgentTaskTracker tracker) {
        // Move logic from PptAgentOrchestrator.planOutline()
    }

    /**
     * 构建 PlannerAgent
     */
    private PlannerAgent buildPlannerAgent() {
        String modelId = agentProperties.plannerModel() != null 
            ? agentProperties.plannerModel() 
            : chatModelFactory.getDefaultModelId();
        ChatModel chatModel = chatModelFactory.create(modelId);
        return AiServices.builder(PlannerAgent.class)
                .chatModel(chatModel)
                .tools(webSearchTool, contentResearchTool)
                .build();
    }

    /**
     * 构建 Planner 输入 prompt
     */
    private String buildPlannerInput(String topic, String requirements, String templateInfo) {
        // Move logic from PptAgentOrchestrator
    }
}
```

- [ ] **Step 2: Move Stage 1 methods from PptAgentOrchestrator**

Methods to move:
- `planOutline(String topic, String requirements, String templateInfo)`
- `planOutline(String topic, String requirements, String templateInfo, AgentTaskTracker tracker)`
- `buildPlannerAgent()`
- `buildPlannerInput(...)`

Keep method signatures identical.

- [ ] **Step 3: Update PptAgentOrchestrator to delegate to PptOutlinePlanner**

In PptAgentOrchestrator:
```java
@RequiredArgsConstructor
public class PptAgentOrchestrator {
    private final PptOutlinePlanner outlinePlanner;
    // ... other dependencies

    public OutlineResult planOutline(String topic, String requirements, String templateInfo) {
        return outlinePlanner.planOutline(topic, requirements, templateInfo, null);
    }

    public OutlineResult planOutline(String topic, String requirements, 
                                      String templateInfo, AgentTaskTracker tracker) {
        return outlinePlanner.planOutline(topic, requirements, templateInfo, tracker);
    }
}
```

- [ ] **Step 4: Verify compilation and existing tests**

```bash
cd backend
./mvnw test -Dtest=PptAgentOrchestratorTest
```

Expected: All Stage 1 tests pass (outline generation).

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/com/novacloudedu/backend/infrastructure/ai/agent/PptOutlinePlanner.java \
         backend/src/main/java/com/novacloudedu/backend/infrastructure/ai/agent/PptAgentOrchestrator.java
git commit -m "refactor(ppt-agent): extract Stage 1 outline planning to PptOutlinePlanner"
```

---

## Task 3: Create PptSlideFactory (Stage 2 Extraction)

**Files:**
- Create: `backend/src/main/java/com/novacloudedu/backend/infrastructure/ai/agent/PptSlideFactory.java`

- [ ] **Step 1: Create PptSlideFactory class**

```java
package com.novacloudedu.backend.infrastructure.ai.agent;

import com.novacloudedu.backend.config.PptAgentProperties;
import com.novacloudedu.backend.infrastructure.ai.ChatModelFactory;
import com.novacloudedu.backend.infrastructure.ai.LangchainChatService;
import com.novacloudedu.backend.infrastructure.ai.agent.PptAgentInterfaces.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.*;

/**
 * PPT Agent Stage 2: 幻灯片内容生成与设计工厂
 * 
 * 负责并发生成幻灯片内容、设计富化、HTML 幻灯片生成
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PptSlideFactory {

    private final ChatModelFactory chatModelFactory;
    private final LangchainChatService langchainChatService;
    private final ImageGenerationTool imageGenerationTool;
    private final PptAgentProperties agentProperties;
    private final ExecutorService slideExecutor; // Injected from orchestrator

    /**
     * 并发生成常规幻灯片
     */
    public List<Slide> generateSlidesParallel(OutlineResult outline, String topic, 
                                               String requirements, AgentTaskTracker tracker) {
        // Move logic from PptAgentOrchestrator.generateSlidesParallel()
    }

    /**
     * 并发生成 HTML 幻灯片
     */
    public List<Slide> generateHtmlSlidesParallel(OutlineResult outline, String topic,
                                                   AgentTaskTracker tracker) {
        // Move logic from PptAgentOrchestrator.generateHtmlSlidesParallel()
    }

    /**
     * 为单张幻灯片添加设计元素
     */
    public Slide enrichSlideWithDesign(Slide slide, AgentTaskTracker tracker) {
        // Move logic from PptAgentOrchestrator.enrichSlideWithDesign()
    }

    /**
     * 推断幻灯片布局（基于内容特征）
     */
    public String inferLayout(String title, String content) {
        // Move logic from PptAgentOrchestrator
    }

    // Private helper methods for buildContentAgent, buildDesignAgent, etc.
}
```

- [ ] **Step 2: Move Stage 2 methods from PptAgentOrchestrator**

Methods to move:
- `generateSlidesParallel(...)`
- `generateHtmlSlidesParallel(...)`
- `enrichSlideWithDesign(...)`
- `inferLayout(...)`
- `buildContentAgent()`
- `buildDesignAgent()`
- All related private helpers

- [ ] **Step 3: Handle ExecutorService dependency**

In PptAgentOrchestrator @PostConstruct:
```java
@PostConstruct
public void init() {
    ExecutorService slideExecutor = Executors.newFixedThreadPool(
            agentProperties.concurrency(), Thread.ofVirtual().name("ppt-agent-", 0).factory());
    this.slideFactory = new PptSlideFactory(chatModelFactory, langchainChatService,
            imageGenerationTool, agentProperties, slideExecutor);
}
```

Or make slideExecutor a @Bean and inject into both.

- [ ] **Step 4: Update PptAgentOrchestrator to delegate**

```java
public List<Slide> generateSlidesParallel(OutlineResult outline, String topic, 
                                           String requirements, AgentTaskTracker tracker) {
    return slideFactory.generateSlidesParallel(outline, topic, requirements, tracker);
}
```

- [ ] **Step 5: Verify compilation and tests**

```bash
cd backend
./mvnw test -Dtest=PptAgentOrchestratorTest#testGenerateSlides
```

- [ ] **Step 6: Commit**

```bash
git add backend/src/main/java/com/novacloudedu/backend/infrastructure/ai/agent/PptSlideFactory.java \
         backend/src/main/java/com/novacloudedu/backend/infrastructure/ai/agent/PptAgentOrchestrator.java
git commit -m "refactor(ppt-agent): extract Stage 2 slide generation to PptSlideFactory"
```

---

## Task 4: Create PptEvaluator (Stage 3 Extraction)

**Files:**
- Create: `backend/src/main/java/com/novacloudedu/backend/infrastructure/ai/agent/PptEvaluator.java`

- [ ] **Step 1: Create PptEvaluator class**

```java
package com.novacloudedu.backend.infrastructure.ai.agent;

import com.novacloudedu.backend.config.PptAgentProperties;
import com.novacloudedu.backend.infrastructure.ai.ChatModelFactory;
import com.novacloudedu.backend.infrastructure.ai.agent.PptAgentInterfaces.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * PPT Agent Stage 3: 质量评估器
 * 
 * 负责评估生成的 PPT 整体质量与单张幻灯片质量
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PptEvaluator {

    private final ChatModelFactory chatModelFactory;
    private final PptAgentProperties agentProperties;
    private final ObjectMapper objectMapper;

    /**
     * 评估整个 PPT 的质量
     */
    public EvaluationResult evaluatePresentation(String topic, String requirements, 
                                                  List<Slide> slides, AgentTaskTracker tracker) {
        // Move logic from PptAgentOrchestrator.evaluatePresentation()
    }

    /**
     * 逐张评估幻灯片（用于反思修复）
     */
    public List<SlideFeedback> evaluateSlidesIndividually(List<Slide> slides, String topic,
                                                           AgentTaskTracker tracker) {
        // Move logic from PptAgentOrchestrator.evaluateSlidesIndividually()
    }

    /**
     * 构建 EvaluatorAgent
     */
    private EvaluatorAgent buildEvaluatorAgent() {
        String modelId = agentProperties.evaluatorModel() != null 
            ? agentProperties.evaluatorModel() 
            : chatModelFactory.getDefaultModelId();
        ChatModel chatModel = chatModelFactory.create(modelId);
        return AiServices.create(EvaluatorAgent.class, chatModel);
    }
}
```

- [ ] **Step 2: Move Stage 3 methods from PptAgentOrchestrator**

Methods to move:
- `evaluatePresentation(...)`
- `evaluateSlidesIndividually(...)`
- `buildEvaluatorAgent()`
- Related helpers

- [ ] **Step 3: Update PptAgentOrchestrator to delegate**

```java
public EvaluationResult evaluatePresentation(String topic, String requirements, 
                                              List<Slide> slides, AgentTaskTracker tracker) {
    return evaluator.evaluatePresentation(topic, requirements, slides, tracker);
}
```

- [ ] **Step 4: Verify tests**

```bash
cd backend
./mvnw test -Dtest=PptAgentOrchestratorTest#testEvaluate
```

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/com/novacloudedu/backend/infrastructure/ai/agent/PptEvaluator.java \
         backend/src/main/java/com/novacloudedu/backend/infrastructure/ai/agent/PptAgentOrchestrator.java
git commit -m "refactor(ppt-agent): extract Stage 3 evaluation to PptEvaluator"
```

---

## Task 5: Create PptReflector (Stage 3b Extraction)

**Files:**
- Create: `backend/src/main/java/com/novacloudedu/backend/infrastructure/ai/agent/PptReflector.java`

- [ ] **Step 1: Create PptReflector class**

```java
package com.novacloudedu.backend.infrastructure.ai.agent;

import com.novacloudedu.backend.config.PptAgentProperties;
import com.novacloudedu.backend.infrastructure.ai.ChatModelFactory;
import com.novacloudedu.backend.infrastructure.ai.agent.PptAgentInterfaces.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * PPT Agent Stage 3b: 反思与修复器
 * 
 * 负责根据评估反馈进行反思性修复，提升低质量幻灯片
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PptReflector {

    private final PptEvaluator evaluator;
    private final PptSlideFactory slideFactory;
    private final PptAgentProperties agentProperties;

    /**
     * 带反思的生成（整体评估 + 修复）
     */
    public GenerationWithEvalResult generateWithReflection(OutlineResult outline, String topic,
                                                            String requirements, AgentTaskTracker tracker) {
        // Move logic from PptAgentOrchestrator.generateWithReflection()
    }

    /**
     * 修复评分低的幻灯片
     */
    public List<Slide> repairWeakSlides(List<Slide> slides, EvaluationResult evaluation,
                                        String topic, AgentTaskTracker tracker) {
        // Move logic from PptAgentOrchestrator.repairWeakSlides()
    }

    /**
     * 检查并修复单张幻灯片
     */
    public Slide inspectAndRepairSlide(Slide slide, String topic, int maxRounds,
                                        AgentTaskTracker tracker) {
        // Move logic from PptAgentOrchestrator.inspectAndRepairSlide()
    }

    // Private helper methods
}
```

- [ ] **Step 2: Move Stage 3b methods from PptAgentOrchestrator**

Methods to move:
- `generateWithReflection(...)`
- `repairWeakSlides(...)`
- `inspectAndRepairSlide(...)`
- Related repair logic

- [ ] **Step 3: Handle cross-stage dependencies**

PptReflector depends on:
- PptEvaluator (for evaluation)
- PptSlideFactory (for regeneration)

Inject both components.

- [ ] **Step 4: Update PptAgentOrchestrator to delegate**

```java
public GenerationWithEvalResult generateWithReflection(OutlineResult outline, String topic,
                                                        String requirements, AgentTaskTracker tracker) {
    return reflector.generateWithReflection(outline, topic, requirements, tracker);
}
```

- [ ] **Step 5: Verify tests**

```bash
cd backend
./mvnw test -Dtest=PptAgentOrchestratorTest#testReflection
```

- [ ] **Step 6: Commit**

```bash
git add backend/src/main/java/com/novacloudedu/backend/infrastructure/ai/agent/PptReflector.java \
         backend/src/main/java/com/novacloudedu/backend/infrastructure/ai/agent/PptAgentOrchestrator.java
git commit -m "refactor(ppt-agent): extract Stage 3b reflection/repair to PptReflector"
```

---

## Task 6: Finalize PptAgentOrchestrator as Facade

**Files:**
- Modify: `backend/src/main/java/com/novacloudedu/backend/infrastructure/ai/agent/PptAgentOrchestrator.java`

- [ ] **Step 1: Clean up PptAgentOrchestrator**

Remove all moved logic, keep only:
1. Dependency injection (5 stage components)
2. @PostConstruct init() for ExecutorService
3. Public API methods (all delegating to stage components)
4. Shared record types (OutlineResult, EvaluationResult, etc.)

Target: ~350 lines

- [ ] **Step 2: Verify all public methods preserved**

Check PptAgentOrchestrator public API:
```bash
grep "public.*(" backend/src/main/java/com/novacloudedu/backend/infrastructure/ai/agent/PptAgentOrchestrator.java
```

Expected: All original public methods exist, delegate to stage components.

- [ ] **Step 3: Run full test suite**

```bash
cd backend
./mvnw test -Dtest=PptAgentOrchestratorTest
```

Expected: All tests pass (no behavior change).

- [ ] **Step 4: Check line counts**

```bash
wc -l backend/src/main/java/com/novacloudedu/backend/infrastructure/ai/agent/*.java
```

Expected:
- PptAgentOrchestrator: ~350 lines
- PptOutlinePlanner: ~180 lines
- PptSlideFactory: ~850 lines
- PptEvaluator: ~280 lines
- PptReflector: ~600 lines
- PptAgentJsonUtils: ~180 lines

All < 900 lines.

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/com/novacloudedu/backend/infrastructure/ai/agent/PptAgentOrchestrator.java
git commit -m "refactor(ppt-agent): finalize PptAgentOrchestrator as thin facade (2430→350 lines)"
```

---

## Task 7: End-to-End Integration Test

**Files:**
- Test: PPT generation smoke test

- [ ] **Step 1: Run existing integration tests**

```bash
cd backend
./mvnw test -Dtest=PptGenerationIntegrationTest
```

Expected: All PPT generation flows work (with/without reflection).

- [ ] **Step 2: Manual smoke test (optional)**

Start application:
```bash
cd docker
docker compose up -d backend
```

Trigger PPT generation via API:
```bash
curl -X POST http://localhost:8081/api/ppt/generate \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"topic": "人工智能发展史", "requirements": "5页PPT，包含图片"}'
```

Expected: HTTP 200, PPT generation completes successfully.

- [ ] **Step 3: Compare before/after PPT quality**

Use same topic as before refactoring:
- Before: commit before Plan 2 started
- After: current commit

Compare:
- Content completeness
- Slide count
- Design quality
- Generation time

Expected: No regression (same or better quality).

- [ ] **Step 4: Verify no startup time regression**

```bash
docker compose logs backend | grep "Started BackendApplication in"
```

Expected: Startup time ≤ before refactoring (bean initialization should be same/faster).

- [ ] **Step 5: Document verification**

Create `backend/PPT_AGENT_REFACTORING_VERIFICATION.md`:

```markdown
# PPT Agent Refactoring Verification (Plan 2)

## Date
2026-06-19

## Refactoring Summary
- Original: PptAgentOrchestrator.java (2430 lines)
- After: 6 focused classes (total ~2440 lines, but organized)
  - PptAgentOrchestrator (facade): 350 lines
  - PptOutlinePlanner (Stage 1): 180 lines
  - PptSlideFactory (Stage 2): 850 lines
  - PptEvaluator (Stage 3): 280 lines
  - PptReflector (Stage 3b): 600 lines
  - PptAgentJsonUtils (utility): 180 lines

## Verification Checklist
- [x] All existing tests pass
- [x] Public API unchanged (调用方零改动)
- [x] No single file > 900 lines
- [x] PPT generation end-to-end test passes
- [x] Startup time ≤ before refactoring
- [x] Same PPT quality for test topic

## Test Results
- Integration test: PASS
- Smoke test topic: "人工智能发展史"
- Before/after comparison: [ACTUAL RESULTS]

## Startup Time
- Before: [FROM PLAN 1 RESULTS] 214.3s
- After: [ACTUAL MEASUREMENT]
- Change: [+/- X seconds]
```

- [ ] **Step 6: Commit verification document**

```bash
git add backend/PPT_AGENT_REFACTORING_VERIFICATION.md
git commit -m "docs: add PPT Agent refactoring verification (Plan 2)"
```

---

## Verification Checklist

After completing all tasks:

- [ ] No single Java file > 900 lines
- [ ] PptAgentOrchestrator public API unchanged
- [ ] All existing tests pass
- [ ] PPT generation end-to-end test passes
- [ ] No startup time regression
- [ ] Same or better PPT quality (before/after comparison)
- [ ] All 6 new classes properly documented

---

## Rollback Plan

If refactoring causes issues:

1. Revert all commits: `git revert HEAD~7..HEAD`
2. Rebuild: `docker compose build backend`
3. Restart: `docker compose up -d backend`

Or restore from specific commit before Plan 2:
```bash
git checkout 6d0d1ff  # Last commit from Plan 1
docker compose build backend
docker compose up -d
```

---

## Expected Impact

| Metric | Before | After | Change |
|--------|--------|-------|--------|
| PptAgentOrchestrator size | 2430 lines | ~350 lines | -85% |
| Largest file | 2430 lines | ~850 lines | -65% |
| Maintainability | Low (giant class) | High (SRP) | +++ |
| Testability | Hard (coupled) | Easy (isolated stages) | +++ |
| Startup time | 214.3s | ~214s | No change expected |

**Benefits:**
- Single Responsibility: Each class has one clear purpose
- Easier testing: Mock stage boundaries
- Parallel development: Teams can work on different stages
- Code navigation: Jump to stage-specific logic quickly
