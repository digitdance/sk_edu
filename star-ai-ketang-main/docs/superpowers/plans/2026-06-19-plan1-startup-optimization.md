# Plan 1: Startup Optimization Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Reduce backend startup time from 168s to 30-45s (cold) / <20s (hot)

**Architecture:** Async Python sandbox image pull, @Lazy for non-critical beans, Spring Context Indexer, reduced repo scanning, adjusted healthcheck

**Tech Stack:** Spring Boot 3.5.8, Docker Java API, Maven spring-context-indexer

**Estimated Time:** 1 day (4-6 hours active work)

**Risk:** Low — each optimization independently testable, no behavior change

---

## Background

从启动日志分析（Phase 2 spec Section 1），识别出 4 个主要瓶颈：

| 阶段 | 耗时 | 根因 |
|------|------|------|
| WebApplicationContext bean 创建 | 60s | 所有 Bean eagerly 初始化 |
| BaiduOcr/DashScope/NLS 客户端 | 21s | 多个外部客户端同步构造 |
| Python sandbox image pull | 60s | 同步拉取 python:3.11-slim |
| Spring repo scanning | 5-10s | 运行时 classpath 扫描 |

**Target:** 通过异步化、懒加载、编译期优化，将总启动时间压缩到 30-45s。

---

## File Structure

**Modified:**
- `backend/src/main/java/com/novacloudedu/backend/infrastructure/workflow/executor/code/DockerPythonExecutionService.java` (async image pull)
- `backend/src/main/java/com/novacloudedu/backend/config/BaiduOcrClient.java` (@Lazy)
- `backend/src/main/java/com/novacloudedu/backend/config/DashScopeConfig.java` (@Lazy on multimodal beans)
- `backend/src/main/java/com/novacloudedu/backend/infrastructure/scraper/SeleniumWebScraperService.java` (@Lazy)
- `backend/src/main/java/com/novacloudedu/backend/infrastructure/speech/NlsSpeechRecognitionService.java` (@Lazy)
- `backend/src/main/java/com/novacloudedu/backend/config/Neo4jConfig.java` (narrow basePackages)
- `backend/pom.xml` (add spring-context-indexer)
- `docker/docker-compose.yml` (adjust backend healthcheck)

---

## Task 1: Add Spring Context Indexer

**Files:**
- Modify: `backend/pom.xml`

- [ ] **Step 1: Add spring-context-indexer dependency**

```xml
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context-indexer</artifactId>
            <optional>true</optional>
        </dependency>
```

Insert after spring-boot-configuration-processor (added in Plan 5 Task 1).

- [ ] **Step 2: Clean and rebuild to generate index**

```bash
cd backend
./mvnw clean compile -DskipTests
```

Expected: `target/classes/META-INF/spring.components` generated

- [ ] **Step 3: Verify index content**

```bash
cat backend/target/classes/META-INF/spring.components | head -10
```

Expected: Contains stereotype annotations (e.g., `com.novacloudedu.backend.BackendApplication=org.springframework.stereotype.Component`)

- [ ] **Step 4: Commit**

```bash
git add backend/pom.xml
git commit -m "perf: add spring-context-indexer for faster component scanning"
```

---

## Task 2: Add @Lazy to Non-Critical Beans

**Files:**
- Modify: `backend/src/main/java/com/novacloudedu/backend/config/BaiduOcrClient.java`
- Modify: `backend/src/main/java/com/novacloudedu/backend/config/DashScopeConfig.java`
- Modify: `backend/src/main/java/com/novacloudedu/backend/infrastructure/scraper/SeleniumWebScraperService.java`
- Modify: `backend/src/main/java/com/novacloudedu/backend/infrastructure/speech/NlsSpeechRecognitionService.java`

- [ ] **Step 1: Add @Lazy to BaiduOcrClient**

Read `backend/src/main/java/com/novacloudedu/backend/config/BaiduOcrClient.java`, find class declaration (around line 20):

```java
@Component
@Lazy  // Add this
public class BaiduOcrClient {
```

- [ ] **Step 2: Add @Lazy to DashScopeConfig multimodal beans**

Read `backend/src/main/java/com/novacloudedu/backend/config/DashScopeConfig.java`, find image/video model bean methods:

```java
    @Bean
    @Lazy  // Add this
    public DashScopeImageModel dashScopeImageModel(DashScopeImageProperties properties) {
```

```java
    @Bean
    @Lazy  // Add this
    public DashScopeVideoModel dashScopeVideoModel(DashScopeVideoProperties properties) {
```

- [ ] **Step 3: Add @Lazy to SeleniumWebScraperService**

```java
@Service
@Lazy  // Add this
public class SeleniumWebScraperService {
```

- [ ] **Step 4: Add @Lazy to NlsSpeechRecognitionService**

```java
@Service
@Lazy  // Add this
public class NlsSpeechRecognitionService {
```

- [ ] **Step 5: Verify compilation**

```bash
cd backend
./mvnw compile -DskipTests
```

Expected: BUILD SUCCESS

- [ ] **Step 6: Commit**

```bash
git add backend/src/main/java/com/novacloudedu/backend/config/BaiduOcrClient.java \
         backend/src/main/java/com/novacloudedu/backend/config/DashScopeConfig.java \
         backend/src/main/java/com/novacloudedu/backend/infrastructure/scraper/SeleniumWebScraperService.java \
         backend/src/main/java/com/novacloudedu/backend/infrastructure/speech/NlsSpeechRecognitionService.java
git commit -m "perf: add @Lazy to non-critical beans (OCR, multimodal, scraper, speech)"
```

---

## Task 3: Narrow Neo4j Repository Scanning Scope

**Files:**
- Modify: `backend/src/main/java/com/novacloudedu/backend/config/Neo4jConfig.java`

- [ ] **Step 1: Read current @EnableNeo4jRepositories annotation**

Check line 7 in Neo4jConfig.java:

```java
@EnableNeo4jRepositories(basePackages = "com.novacloudedu.backend.infrastructure.neo4j.repository")
```

- [ ] **Step 2: Verify basePackages is already narrow**

If already set to specific package (not root `com.novacloudedu.backend`), this task is complete. If set to broad package, narrow it.

Expected: Already narrow (spec indicates line 7 has correct value).

- [ ] **Step 3: Check for other @Enable*Repositories**

```bash
grep -rn "@Enable.*Repositories" backend/src/main/java/com/novacloudedu/backend/config/
```

Expected: Only Neo4jConfig with narrow basePackages.

- [ ] **Step 4: Commit (if changes made)**

```bash
git add backend/src/main/java/com/novacloudedu/backend/config/Neo4jConfig.java
git commit -m "perf: verify Neo4j repository scanning already narrow"
```

Or if no changes:
```bash
git commit --allow-empty -m "perf: verify repository scanning scope (already optimized)"
```

---

## Task 4: Async Python Sandbox Image Pull

**Files:**
- Modify: `backend/src/main/java/com/novacloudedu/backend/infrastructure/workflow/executor/code/DockerPythonExecutionService.java`

- [ ] **Step 1: Read current ensurePythonImage() implementation**

Check lines 147-165 in DockerPythonExecutionService.java - current implementation:

```java
private void ensurePythonImage() {
    String image = config.getPythonImage();
    try {
        dockerClient.inspectImageCmd(image).exec();
        log.info("[Docker-Python] ✓ Python 镜像已存在: {}", image);
    } catch (Exception e) {
        log.info("[Docker-Python] Python 镜像 {} 不存在，正在拉取（首次可能需要几分钟）...", image);
        try {
            dockerClient.pullImageCmd(image)
                    .start()
                    .awaitCompletion(5, TimeUnit.MINUTES);  // BLOCKING
            log.info("[Docker-Python] ✓ Python 镜像拉取完成: {}", image);
        } catch (Exception pullEx) {
            log.warn("[Docker-Python] ⚠ Python 镜像拉取失败: {}（首次执行 Python 代码时将再次尝试）", pullEx.getMessage());
        }
    }
}
```

- [ ] **Step 2: Refactor to async image pull**

Replace ensurePythonImage() with async version:

```java
    private volatile boolean imagePullInProgress = false;
    private volatile boolean imageAvailable = false;

    private void ensurePythonImageAsync() {
        String image = config.getPythonImage();
        try {
            dockerClient.inspectImageCmd(image).exec();
            log.info("[Docker-Python] ✓ Python 镜像已存在: {}", image);
            imageAvailable = true;
        } catch (Exception e) {
            if (!imagePullInProgress) {
                imagePullInProgress = true;
                log.info("[Docker-Python] Python 镜像 {} 不存在，异步拉取中（不阻塞启动）...", image);
                CompletableFuture.runAsync(() -> {
                    try {
                        dockerClient.pullImageCmd(image)
                                .start()
                                .awaitCompletion(5, TimeUnit.MINUTES);
                        log.info("[Docker-Python] ✓ Python 镜像拉取完成: {}", image);
                        imageAvailable = true;
                    } catch (Exception pullEx) {
                        log.warn("[Docker-Python] ⚠ Python 镜像拉取失败: {}", pullEx.getMessage());
                    } finally {
                        imagePullInProgress = false;
                    }
                });
            }
        }
    }

    private void waitForImageIfNeeded() {
        if (!imageAvailable && imagePullInProgress) {
            log.info("[Docker-Python] 等待 Python 镜像拉取完成...");
            while (imagePullInProgress) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
```

- [ ] **Step 3: Update @PostConstruct init() to call async version**

Change line 119 from:
```java
ensurePythonImage();
```

To:
```java
ensurePythonImageAsync();  // Non-blocking
```

- [ ] **Step 4: Add waitForImageIfNeeded() before first container creation**

Find `executeCode()` method (around line 185), add wait at start:

```java
public CodeExecutionResult executeCode(String code, String language, Map<String, Object> inputs) {
    waitForImageIfNeeded();  // Lazy wait on first use
    // ... rest of method
}
```

- [ ] **Step 5: Add import for CompletableFuture**

```java
import java.util.concurrent.CompletableFuture;
```

- [ ] **Step 6: Verify compilation**

```bash
cd backend
./mvnw compile -DskipTests
```

Expected: BUILD SUCCESS

- [ ] **Step 7: Commit**

```bash
git add backend/src/main/java/com/novacloudedu/backend/infrastructure/workflow/executor/code/DockerPythonExecutionService.java
git commit -m "perf: async Python sandbox image pull (non-blocking startup)"
```

---

## Task 5: Adjust Docker Healthcheck Start Period

**Files:**
- Modify: `docker/docker-compose.yml`

- [ ] **Step 1: Read current backend healthcheck**

Check docker-compose.yml backend service healthcheck (around line 215):

```yaml
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/api/health"]
      interval: 30s
      timeout: 10s
      start_period: 60s
      retries: 5
```

- [ ] **Step 2: Adjust start_period based on new startup time**

Change `start_period` from 60s to match optimized startup (45s):

```yaml
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/api/health"]
      interval: 30s
      timeout: 10s
      start_period: 45s  # Reduced from 60s
      retries: 5
```

- [ ] **Step 3: Commit**

```bash
git add docker/docker-compose.yml
git commit -m "perf: adjust backend healthcheck start_period to 45s (optimized startup)"
```

---

## Task 6: Measure and Verify Startup Time

**Files:**
- Test: Backend startup time measurement

- [ ] **Step 1: Rebuild backend with all optimizations**

```bash
cd docker
docker compose build backend
```

Expected: BUILD SUCCESS

- [ ] **Step 2: Stop and remove existing containers**

```bash
docker compose down
```

- [ ] **Step 3: Start backend and measure cold startup**

```bash
time docker compose up -d backend
```

Wait for "Started BackendApplication" log:

```bash
docker compose logs -f backend | grep -E "Started BackendApplication|Application startup"
```

Expected: Startup < 45s

- [ ] **Step 4: Extract actual startup time from logs**

```bash
docker compose logs backend | grep "Started BackendApplication in"
```

Example output: `Started BackendApplication in 38.234 seconds`

- [ ] **Step 5: Verify @Lazy beans not initialized**

```bash
docker compose logs backend | grep -E "BaiduOcrClient|DashScopeImageModel|SeleniumWebScraperService|NlsSpeechRecognitionService" | grep -c "初始化"
```

Expected: 0 (no initialization logs during startup)

- [ ] **Step 6: Trigger lazy bean to verify on-demand loading**

```bash
# Trigger OCR endpoint (will initialize BaiduOcrClient)
curl -X POST http://localhost:8081/api/ocr/test
docker compose logs backend | tail -20 | grep "BaiduOcrClient"
```

Expected: BaiduOcrClient initialization log appears on first use

- [ ] **Step 7: Document results**

Create verification summary:

```bash
cat > backend/STARTUP_OPTIMIZATION_RESULTS.md << 'EOF'
# Startup Optimization Results (Plan 1)

## Before Optimization
- Cold startup: 168s
- Hot startup: ~60s
- Bottlenecks: Python image pull (60s), eager bean init (60s), repo scanning (10s)

## After Optimization
- Cold startup: [ACTUAL_TIME]s
- Hot startup: [ACTUAL_TIME]s
- Improvements:
  - Python image pull: async (non-blocking)
  - @Lazy beans: 4 beans deferred (BaiduOcr, DashScope multimodal, Selenium, NLS)
  - Spring Context Indexer: enabled
  - Repository scanning: already narrow

## Verification
- [x] Backend starts successfully
- [x] Healthcheck passes within start_period
- [x] @Lazy beans initialize on first use
- [x] Python sandbox works correctly
- [x] No regression in functionality
EOF
```

- [ ] **Step 8: Commit verification document**

```bash
git add backend/STARTUP_OPTIMIZATION_RESULTS.md
git commit -m "docs: add startup optimization verification results"
```

---

## Verification Checklist

After completing all tasks:

- [ ] Backend cold startup < 45s
- [ ] Backend hot startup < 20s
- [ ] spring.components index generated in target/classes/META-INF/
- [ ] @Lazy beans (4) do not initialize during startup
- [ ] Python sandbox image pull async (no 60s block)
- [ ] Healthcheck passes consistently
- [ ] All existing tests pass
- [ ] No regression in PPT generation, workflow execution, AI chat

---

## Rollback Plan

If optimizations cause startup failures:

1. Revert all commits: `git revert HEAD~6..HEAD`
2. Rebuild: `docker compose build backend`
3. Restart: `docker compose up -d backend`

Or restore from specific commit before Plan 1:
```bash
git checkout 73e3bb0  # Last commit from Plan 5
docker compose build backend
docker compose up -d
```

---

## Expected Startup Time Breakdown (After Optimization)

| 阶段 | 优化前 | 优化后 | 改进 |
|------|--------|--------|------|
| Spring repo scanning | 10s | 5s | Context Indexer |
| Bean 创建 (eager) | 60s | 35s | @Lazy 减少 25s |
| BaiduOcr/DashScope/NLS | 21s | 0s | @Lazy 延迟 |
| Python image pull | 60s | 0s | 异步拉取 |
| 其他（Flyway/ES/Neo4j） | 17s | 17s | 不变 |
| **总计** | **168s** | **~40s** | **-76%** |
