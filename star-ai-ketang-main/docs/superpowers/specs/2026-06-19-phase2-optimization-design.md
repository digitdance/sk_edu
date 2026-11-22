# Phase 2 Optimization Design — NovaCloudEdu

- **Date**: 2026-06-19
- **Status**: Draft (awaiting user review)
- **Scope**: 可维护性 + 性能与资源 + 开发效率（"快速见效"节奏）
- **Strategy**: 方案 A（启动优化 + 巨型类拆分）+ 配置治理

## 背景

经过对项目启动日志、代码结构、配置的全面梳理，识别出 5 个高杠杆优化点：

1. **后端启动 168 秒**（Python sandbox image pull 占 3 分钟，WebApplicationContext bean 创建占 60 秒）
2. **PPT Agent 巨型类**：`PptAgentOrchestrator.java` 2450 行
3. **工作流双巨型类**：`DefaultWorkflowEngine.java` 1587 行 + `WorkflowApplicationService.java` 1374 行
4. **前端 NodeConfigPanel 单文件**：3671 行堆砌 18 种节点配置
5. **配置治理缺失**：`application.yml` 587 行、docker-compose 51 个 env 透传、AI provider 重复配置

每项独立可交付，互不阻塞。总工期预估 5-7 天。

## 执行顺序

**5 → 1 → 3 → 2 → 4**（配置治理 + 启动优化先做，体感收益最快；巨型类拆分按风险从低到高）

---

## Section 1. 启动优化：168s → 30-45s

### 瓶颈分析（来自启动日志）

| 阶段 | 耗时 | 根因 |
|------|------|------|
| Spring repo scanning | 0-17s | 正常 |
| **WebApplicationContext bean 创建** | **17-77s (60s)** | 所有 Bean  eagerly 初始化 |
| HikariPool + Flyway | 77-79s | 正常 |
| BaiduOcr + Neo4j + DashScope clients | 79-100s (21s) | 多个外部客户端同步构造 |
| ES index + Python sandbox start | 100-108s | Python sandbox 同步启动 |
| **Python image pull + Tomcat** | **108-168s (60s)** | Python sandbox 拉取镜像超时 |

### 6 项措施

**A. Python sandbox 异步镜像预加载**
- 启动时立即触发 `docker pull python:3.11-slim` 异步任务，不阻塞主启动
- 首次使用时 lazy wait
- 预计节省 30-60s

**B. 非关键 Bean `@Lazy`**
- `BaiduOcrClient`（OCR 不是核心路径）
- `DashScopeConfig` 多模态客户端（图像/视频生成）
- `SeleniumWebScraperService`
- `NlsSpeechRecognitionService`
- 预计节省 15-25s

**C. Spring Context Indexer**
- `pom.xml` 添加 `spring-context-indexer` 依赖
- 编译期生成 `META-INF/spring.components`，跳过运行时 classpath 扫描
- 预计节省 5-10s

**D. DashScope 多模态客户端懒初始化**
- `DashScopeImageModel` / `DashScopeVideoModel` 首次调用才构建客户端
- 避免启动时加载 2+ 个 SDK 客户端

**E. 减少 Spring Data repo 冗余扫描**
- 检查 `@EnableJpaRepositories` / `@EnableNeo4jRepositories` 等注解的 `basePackages`
- 明确指定包路径，避免全盘扫描

**F. Docker healthcheck start-period 调整**
- 当前 backend `start_period` 可能过短导致反复 unhealthy
- 与 A-E 配合后 start_period 调到 60s 即可

### 验证标准
- 冷启动 < 45s（热启动 < 20s）
- 所有现有功能回归正常
- 启动日志无 BeanCreation 异常

---

## Section 2. PPT Agent 巨型类拆分

### 现状

`PptAgentOrchestrator.java` 2450 行，承担 6 类职责：

| 职责 | 行数 | 关键方法 |
|------|------|---------|
| 生命周期 + 配置 | ~30 | `init()`、25 个 `@Value` 字段 |
| 大纲规划（Stage 1） | ~120 | `planOutline`、`buildPlannerAgent`、`parseOutlineResult` |
| 幻灯片生成（Stage 2，含 HTML） | ~850 | `generateSlidesParallel`、`generateHtmlSlidesParallel`、`enrichSlideWithDesign`、模板推断 |
| 质量评估（Stage 3） | ~250 | `evaluatePresentation*`、`parseEvaluationResult` |
| 反思修复（Stage 3b） | ~550 | `generateWithReflection*`、`repairWeakSlides`、`inspectAndRepairSlide` |
| JSON / 工具方法 | ~200 | `parseJsonSafe`、`parseSlideConfigSafe`、`extractPreviewImageUrls` |
| 公共 record | ~40 | `OutlineResult`、`EvaluationResult`、`SlideFeedback`、`GenerationWithEvalResult` |

> Agent 接口已在 `PptAgentInterfaces.*` 独立，问题仅在于 Orchestrator 自身太大。

### 拆分方案（5 + 1 门面）

按 **Agent 角色 / 阶段** 拆分：

```
infrastructure/ai/agent/
├── PptAgentInterfaces.java         (不动)
├── PptAgentOrchestrator.java       (门面 + 配置 + init，~350 行)
├── PptOutlinePlanner.java          (Stage 1 大纲规划，~180 行)
├── PptSlideFactory.java            (Stage 2 幻灯片生成 + 设计富化 + 模板工具，~850 行)
├── PptEvaluator.java               (Stage 3 评估，~280 行)
├── PptReflector.java               (Stage 3b 反思修复，~600 行)
└── PptAgentJsonUtils.java          (JSON / 通用工具，~180 行)
```

### 关键设计点

- `PptAgentOrchestrator` 退化为门面，保留所有 public 方法签名（不破坏调用方），实现委托到 5 个 agent 类
- `PptSlideFactory` 同时处理 regular 与 HTML 两种 slide，通过方法名区分（`generateRegular*` / `generateHtml*`）
- `PptReflector` 合并两条修复流水线，共享 `inspectAndRepairSlide` 逻辑
- `@Value` 配置按职责分组注入到对应类（Planner 用 `plannerModelId`、Evaluator 用 `evaluatorModelId` 等）
- 所有 record 保留在 `PptAgentOrchestrator`（或搬到 `PptAgentInterfaces`），因为它们跨阶段共享

### 验证标准
- 没有单个文件 > 900 行
- `PptAgentOrchestrator` 所有原 public API 不变 → 调用方零改动
- 启动时所有 Bean 构造时间总和 ≤ 当前值
- PPT 生成端到端回归测试（用同一 topic 生成前后对比）

---

## Section 3. 工作流引擎拆分

### 现状

工作流模块有 2 个巨型类，共 2961 行：

| 文件 | 行数 | 实际职责 |
|------|------|---------|
| `DefaultWorkflowEngine.java` | 1587 | 引擎生命周期 + 执行调度 + 容器节点（loop/parallel）+ 内置节点（变量 set/get）+ 条件表达式解析 + 错误处理 |
| `WorkflowApplicationService.java` | 1374 | 工作流 CRUD + 执行控制 + 图编辑（nodes/edges）+ 变量管理 + 设置 + 校验 + 全部 DTO 转换 + 内部 VO 类 |

### 拆分方案

#### A. `DefaultWorkflowEngine` 拆 5 个类

```
infrastructure/workflow/
├── DefaultWorkflowEngine.java          (门面 + 生命周期 + 执行调度，~400 行)
├── execution/
│   ├── WorkflowExecutionRunner.java    (executeFromNode + retry + 错误处理，~350 行)
│   ├── WorkflowContainerExecutor.java  (loop/parallel 容器 + 收敛节点计算，~450 行)
│   ├── WorkflowBuiltinNodeHandler.java (变量 set/get 等内置节点，~200 行)
│   └── WorkflowConditionEvaluator.java (条件/表达式求值 + 正则，~250 行)
```

**关键点**：
- `DefaultWorkflowEngine` 保留 `WorkflowEngine` 接口实现，作为 Spring Bean 入口，其他方法委托
- `WorkflowConditionEvaluator` 独立后**可单测**（当前嵌在引擎里难覆盖）
- `WorkflowContainerExecutor` 独立后 `computeParallelConvergenceNodes` 等纯算法可独立验证

#### B. `WorkflowApplicationService` 拆 6 个类

```
application/service/workflow/
├── WorkflowApplicationService.java     (门面 + 协调，~150 行)
├── WorkflowCrudService.java            (create/update/publish/archive/copy/list，~200 行)
├── WorkflowExecutionService.java       (execute*/getStatus/logs/statistics，~180 行)
├── WorkflowGraphEditor.java            (nodes + edges CRUD，~300 行)
├── WorkflowVariableService.java        (variables + settings，~150 行)
├── WorkflowValidationService.java      (validate + 拓扑校验，~200 行)
└── dto/
    ├── WorkflowVO.java                 (独立 static class → 顶级类)
    ├── ExecutionResultVO.java
    ├── WorkflowNodeVO.java
    ├── WorkflowEdgeVO.java
    ├── WorkflowVariableVO.java
    └── WorkflowDtoMapper.java          (聚合所有 toVO 方法)
```

**关键点**：
- 现有 9 个内嵌 VO 类抽到 `dto/` 目录成顶级类 → Controller 可直接复用
- `WorkflowApplicationService` 退化为门面，保留所有原 public 方法签名 → **Controller 零改动**
- `getNodeCategory` 这种"节点类型元数据"搬到 `NodeType` 枚举自身（领域驱动）

### 验证标准

- 没有单个文件 > 500 行
- `WorkflowEngine`、`WorkflowApplicationService` 原 public API 不变 → 调用方零改动
- `WorkflowConditionEvaluator`、`WorkflowContainerExecutor` 有独立单元测试（迁移 + 增强现有 `WorkflowEngineTest`）
- 现有 `WorkflowApplicationServiceTest` 仍然全部通过（门面委托透明）

---

## Section 4. 前端 NodeConfigPanel 拆分

### 现状

`NodeConfigPanel.tsx` 3671 行，堆砌 18 个子组件：

| 节点类别 | 子组件 | 行数 |
|---------|-------|------|
| LLM / AI | `LLMConfig`、`IntentRecognitionConfig`、`EntityExtractionConfig` | ~720 |
| 流程控制 | `ConditionConfig`、`SwitchConfig`、`LoopConfig`、`ParallelConfig`、`MergeConfig` | ~800 |
| 数据 / 变量 | `VariableSetConfig`、`VariableGetConfig`、`JsonParseConfig`、`TemplateConfig` | ~430 |
| IO / 集成 | `StartConfig`、`EndConfig`、`HttpRequestConfig`、`DatabaseQueryConfig`、`FileReadConfig` | ~710 |
| 代码 | `CodeConfig`（含 JS/PY 模板 + 变量检测） | ~260 |
| 共享 | `ConfigField`、`ConfigCheckbox`、`useUpstreamVars`、`OPERATORS`、CSS classes | ~250 |
| 外壳 | `NodeConfigPanel` + `renderNodeConfig` 分发器 | ~120 |

### 拆分方案

按节点类别拆到 `node-configs/` 子目录：

```
web/src/pages/admin/workflow/components/
├── NodeConfigPanel.tsx              (外壳 + renderNodeConfig 分发，~120 行)
└── node-configs/
    ├── index.ts                     (统一 re-export)
    ├── shared.tsx                   (ConfigField / ConfigCheckbox / CSS classes / useUpstreamVars / OPERATORS / NODE_OUTPUT_VARS)
    ├── StartEndConfig.tsx           (Start + End，~200 行)
    ├── LLMConfigs.tsx               (LLM + IntentRecognition + EntityExtraction + AI_CAPABILITIES，~740 行)
    ├── FlowControlConfigs.tsx       (Condition + Switch + Loop + Parallel + Merge，~820 行)
    ├── DataTransformConfigs.tsx     (VariableSet + VariableGet + JsonParse + Template，~450 行)
    ├── IoConfigs.tsx                (HttpRequest + DatabaseQuery + FileRead，~520 行)
    └── CodeConfig.tsx               (Code + JS/PY 模板 + detectJsVars/detectPyVars，~280 行)
```

### 关键设计点

- 每个子组件保持原接口：`(config, onUpdate) => JSX`，外壳只负责按 `nodeType` 分发
- `LLMConfigs.tsx`（740 行）仍可接受，因为 LLM/Intent/Entity 三个组件共享 `ModelInfo`、`KbInfo`、`InputMapping` 接口，物理共置减少跨文件耦合
- `shared.tsx` 把通用 hook、常量、UI 原子统一收口 → 所有节点配置类都从这里 import
- `renderNodeConfig` 改为**按类别查表**：
  ```ts
  const REGISTRY: Record<NodeType, React.FC<Props>> = {
    start: StartConfig, end: EndConfig,
    llm: LLMConfig, intent_recognition: IntentRecognitionConfig, ...
  }
  ```
  添加新节点类型只需注册一行
- 动态 `import()` 懒加载每个类别（可选，按需）：
  ```ts
  const LLMConfigs = lazy(() => import('./node-configs/LLMConfigs'))
  ```
  进一步降低主 bundle 体积

### 验证标准

- 没有单个文件 > 850 行
- 现有 workflow 画布编辑流程（添加/修改 18 种节点）回归正常
- `tsc --noEmit` 无报错
- 每个子组件可独立写 storybook（后续演进）

---

## Section 5. 配置治理

### 现状

| 文件 | 行数 | 问题 |
|------|------|------|
| `application.yml` | 587 | AI providers 段 8 个共 ~150 行高度重复；主文件过大 |
| `docker-compose.yml` backend | 51 行 env | 每加一个配置项要改 3 处（application.yml + docker/.env + docker-compose.yml）|
| `docker/.env` | 144 行 | 默认值跟 application.yml 耦合，易错位 |
| `application.yml` ppt.agent 段 | 7 个模型槽位 | 全部写死 `openrouter/claude-opus-4-8`，没复用 default |

### 5 项改造

#### A. YAML 锚点 + 合并键（立省 ~80 行）

每个 AI provider 重复 `api-key / base-url / enabled / models.{X}.{type,temperature,max-tokens,top-p}`。利用 YAML `&anchor` + `<<: *merge`：

```yaml
ai:
  chat-models:
    provider-defaults: &provider-defaults
      temperature: &temp-default 0.7
      max-tokens: &tokens-default 4000
      top-p: 0.8
    providers:
      dashscope:
        enabled: true
        api-key: ${DASHSCOPE_API_KEY:}
        <<: *provider-defaults
        models:
          qwen-max: { type: text }     # 省略字段 → 继承默认
          qwen-turbo: { type: text, max-tokens: 2000 }  # 仅覆盖差异
```

→ 模型配置从 ~150 行压到 ~60 行，新增模型只需一行。

#### B. docker-compose env 透传自动化

现状：
```yaml
backend:
  environment:
    - DB_NAME=${DB_NAME:-novacloudedu}
    - DB_USERNAME=${DB_USERNAME:-nova}
    - DB_PASSWORD=${DB_PASSWORD}
    - ... 51 行 ...
```

改造：用 `env_file` + Spring relaxed binding，application.yml 的 `${VAR:default}` 天然读环境变量：
```yaml
backend:
  env_file:
    - path: .env
      required: true
  environment:
    - SPRING_PROFILES_ACTIVE=docker
    # 只保留需要覆盖/显式声明的 3-5 个关键变量
    - DOCKER_SOCK_PATH=${DOCKER_SOCK_PATH:-/var/run/docker.sock}
```

→ docker-compose.yml 从 51 行环境配置压到 ~5 行。

**前提**：`.env` 的变量名必须跟 Spring `${VAR}` 一致（已一致）。需要验证 `.env` 中 `NEO4J_AUTH=neo4j/...` 这类 Spring 不读的变量另外处理。

#### C. PPT Agent 模型槽位继承 default

现状 7 个槽位全部显式写 `openrouter/claude-opus-4-8`：

```yaml
ppt.agent:
  planner-model: ${PPT_AGENT_PLANNER_MODEL:openrouter/claude-opus-4-8}
  content-model: ${PPT_AGENT_CONTENT_MODEL:openrouter/claude-opus-4-8}
  ... (重复 7 次)
```

改造：**代码层引入 fallback**：
```java
String modelId = Optional.ofNullable(plannerModel)
    .filter(StringUtils::hasText)
    .orElse(chatModelsProperties.getDefaultModel());
```

配置简化：
```yaml
ppt:
  agent:
    # 默认全部继承 ai.chat-models.default-model（即 claude-opus-4-8）
    # 仅需要差异化时才覆盖
    evaluator-model: ${PPT_AGENT_EVALUATOR_MODEL:}   # 空 → 用 default
    layout-selector-model: ${PPT_AGENT_LAYOUT_SELECTOR_MODEL:}
```

→ docker/.env 删 7 行，application.yml 删 7 个重复默认值。未来切换基座模型只改一处。

#### D. Profile 拆分（主配置瘦身）

把 AI providers 段搬到 `application-ai.yml`：
```
backend/src/main/resources/
├── application.yml              (587 → ~300 行，只留通用 + DB/Redis/MQ/邮件等)
├── application-ai.yml           (~200 行，AI providers + PPT Agent + RAG)
├── application-media.yml        (~30 行，SRS/LiveKit/coturn)
└── application-docker.yml       (已存在，容器连接细节)
```

Spring 自动按 `spring.profiles.active` 合并，本地 IDE 开发 `dev,ai`，Docker 跑 `docker,ai,media`。

**收益**：
- 改 AI 模型配置不用滚动 500 行找位置
- 可以按环境选择性加载（如测试环境关掉 AI）

#### E. `@ConfigurationProperties` 替代散落 `@Value`

现状 `PptAgentOrchestrator` 有 14 个 `@Value` 字段。改造：
```java
@ConfigurationProperties(prefix = "ppt.agent")
@Validated
public record PptAgentProperties(
    @NotBlank String plannerModel,
    @Min(1) int concurrency,
    @Range(min = 0, max = 100) int qualityThreshold,
    ...
) {}
```

**收益**：
- 启动时校验（避免运行时才发现 qualityThreshold 配成 -1）
- 配置项有类型 + 文档 = 自描述
- 单元测试可直接 new 一个 Properties 对象，无需启动 Spring

→ 先对 PPT Agent 做示范（14 个字段全收敛），其他巨型类后续迁移。

### 验证标准

- `application.yml` 主文件 < 350 行
- docker-compose.yml backend env 段 < 10 行
- 新增一个 AI provider 仅需改 1 个文件（`application-ai.yml`）
- 配置错误（如 `qualityThreshold: abc`）启动即 fail-fast
- 当前所有 PPT 生成、AI 对话流程回归正常

---

## 风险与缓解

| 风险 | 缓解 |
|------|------|
| 拆分时引入 Bean 循环依赖 | 先用 `@Lazy` 临时打破，后续重构解决根因；IDE 启动时检查 |
| 门面委托层性能开销 | 仅方法调用，JIT 内联后为 0 |
| 配置自动化后环境变量未正确传递 | 每项改造后先在 docker 环境 smoke test |
| PPT Agent 拆分后调用方未更新 | 严格保留原 public API，调用方零改动 |
| YAML 锚点语法 IDE 不识别 | IntelliJ / VSCode 均支持，必要时加 schema 提示 |

## 非目标（显式不做）

- 不做数据库 schema 重构
- 不做前端路由/状态管理重构
- 不引入新中间件
- 不做微服务拆分
- 不做 CI/CD 流程改造

## 成功标准（整体）

- 启动时间 < 45s（冷）/ < 20s（热）
- 没有单个 Java 文件 > 900 行、没有单个 TSX 文件 > 850 行
- `application.yml` < 350 行，docker-compose backend env < 10 行
- 所有现有功能回归测试通过（PPT 生成、AI 对话、工作流执行）
- 单元测试覆盖率不降（拆分后关键模块新增测试）
