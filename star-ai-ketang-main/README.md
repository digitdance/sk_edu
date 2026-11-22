# 项目介绍

## 1. 项目背景与目标
- 背景概述  
  随着在线教育和智慧教学的广泛应用，教育平台需要在内容生产、自动批改、学习路径推荐与教学质量评估上做到数据驱动和工程化落地。siren 是一个以 AI 为核心驱动的智慧教育云平台，旨在把教学内容、学习行为与评估结果闭环起来，提升教学效率与内容生产效率。
- 首页（仪表盘）：  
  ![首页](docs/images/1超级首页.png)

- 典型行业痛点（与平台目标的对应）
  - 数据分散、样本难以统一管理与版本追踪，影响教学资源复用与模型训练复现；
  - 标注与评估规范不一致，导致自动批改/评分模型精度不稳定；
  - 模型训练、验证、部署链路割裂，整体落地周期长、迭代慢；
  - 业务流程缺少可追溯的审计与日志，问题排查成本高；
  - 模型上线后缺少持续回流机制（用户反馈与验证样本），难以快速优化效果。

- 建设目标
  - 建立工程化的教育数据治理体系（版本化数据集、标签规范）；
  - 提供标准化标注与批改工具，保证训练数据质量与一致性；
  - 实现端到端 ML 生命周期（训练 → 验证 → 部署 → 监控 → 回流）；
  - 提供可审计的操作链路与日志，便于故障定位与合规审计；
  - 快速构建上线模型的持续改进闭环，实现业务与模型的协同演进。

## 2. 项目应用场景
- 在线试题自动批改：OCR + NLP/视觉模型实现客观题与主观题自动评分与反馈。
- 个性化学习推荐：根据学习历史与测评结果，推荐学习资源与练习路径。
- 教师辅助内容生成：基于 AI 自动生成试题、讲义、PPT 大纲与教学计划。
- 题库管理与版本化：统一题目、答案、解析与标签的管理与版本控制。
- 课堂与作业分析：统计学生表现、发现薄弱点并生成班级/学生报表。
- 接入第三方 LMS/教学系统：通过标准 API 对接学校或企业内部系统。

## 3. 核心功能概述
- 数据管理：统一存储题库、作答记录、标注与训练集版本，支持导入/导出。
- 标注与审核：提供图像/文本标注工具，支持标注流程、权限与质量检查。
- 模型训练管理：可配置训练任务、监控训练日志与指标，支持分布式训练。
- 在线推理与服务化部署：一键部署推理服务（容器化 / REST/gRPC），支持版本路由。
- 自动批改引擎：集成 OCR、文本理解与评分策略，支持可配置的评分规则。
- 反馈回流：将验证与真实业务中的错误样本回流至数据集，支撑模型再训练。
- 仪表盘与报表：用户与教师行为、模型绩效、系统健康状态可视化。
- 权限与审计：基于角色的访问控制（RBAC）与操作日志审计。
- API 与二次开发：对外提供标准 REST/GraphQL API 与 SDK，便于集成与扩展。
- 部署与运维：提供 Docker / Kubernetes 部署脚本、健康检查与自动伸缩配置建议。

## 4. 主要技术栈
- 编程语言：Java（后端主服务）、TypeScript（前端）、Python（数据与模型）、Dart/Flutter（移动端，可选）。
- 后端框架：Spring Boot（REST、SSE）、Spring Security（认证授权）、MyBatis / JPA（数据访问）。
- 前端框架：React + TypeScript（Web 管理端与用户端），Vite 构建工具。
- 数据库：PostgreSQL（业务数据）、Elasticsearch（全文检索/日志索引）。
- 缓存/消息：Redis（缓存、会话）、Kafka / RabbitMQ（事件与任务队列）。
- 存储：对象存储（如 OSS / S3）用于附件、试卷与媒体文件。
- ML / 算法：Python（scikit-learn、XGBoost、PyTorch、TensorFlow）、模型版本管理（MLflow/自研）。
- 容器与编排：Docker、Docker Compose（开发）、Kubernetes（生产）。
- CI/CD 与运维：GitHub Actions / GitLab CI / Jenkins、Prometheus + Grafana（监控）、ELK 或 EFK（日志）。
- 安全：OAuth2 / JWT（认证）、HTTPS、审计日志与流量限流策略。

## 技术栈

### 前端技术

| 技术 | 版本 | 用途 |
|:---|:---:|:---|
| **React** | 19.2 | Web 用户端 + 管理后台 |
| **TypeScript** | 5.9 | 类型安全 |
| **Vite** | 7.2 | 构建工具 (SWC) |
| **TailwindCSS** | 4.1 | 原子化 CSS |
| **Flutter** | 3.10+ | 移动端 (Android/iOS) |
| **Dart** | 3.10+ | Flutter 语言 |

```
┌─────────────────────────────────────────────────────────────┐
│                         前端层                               │
├─────────────────────┬───────────────────────────────────────┤
│  Web 前端           │  移动端 (Flutter)                      │
├─────────────────────┼───────────────────────────────────────┤
│  React 19.2         │  Flutter 3.10+ / Dart 3.10+           │
│  TypeScript 5.9     │  TDesign Flutter 组件库                │
│  Vite 7.2 (SWC)    │  Dio + Token 自动刷新拦截器             │
│  TailwindCSS 4.1    │  WebSocket + STOMP (实时聊天)          │
│  Zustand 状态管理   │  sqflite 本地数据库 (离线缓存)        │
│  React Flow 工作流   │  flutter_secure_storage 安全存储       │
│  json-bigint 大数   │  SSE 流式 AI 对话                      │
└─────────────────────┴───────────────────────────────────────┘
```

### 后端技术

| 技术 | 版本 | 用途 |
|:---|:---:|:---|
| **Java** | 21 | 后端语言 |
| **Spring Boot** | 3.5.8 | 应用框架 |
| **Spring Security** | 6.2 | 安全认证 |
| **MyBatis Plus** | 3.5.5 | ORM 框架 |
| **Langchain4j** | 0.36.2 | AI 集成 |

```
┌─────────────────────────────────────────────────────────────┐
│                       后端服务层                             │
├─────────────────────────────────────────────────────────────┤
│  核心:       Java 21 + Spring Boot 3.5.8 (DDD 四层架构)     │
│  持久化:     MyBatis Plus 3.5.5 (80+ PO / 80+ Mapper)      │
│  安全:       Spring Security + JWT (jjwt 0.12.6)           │
│  AI:         Langchain4j 0.36.2 + DashScope SDK 2.16.7     │
│  工作流:     自研 DAG 引擎 (18 种节点 + 代码沙箱)           │
│  微服务:     Python-PPTX (PPT生成) + Typst (试卷排版)      │
├─────────────────────────────────────────────────────────────┤
│  数据存储    │  PostgreSQL 15+ (主数据库, 31 张表)           │
│              │  Redis 7+ (缓存/会话/离线消息)                │
│              │  Elasticsearch 8+ (全文检索)                  │
│              │  Neo4j 5+ (知识图谱/推荐)                     │
├─────────────────────────────────────────────────────────────┤
│  外部服务    │  阿里云 OSS (文件存储)                        │
│              │  阿里云 NLS (语音 ASR/TTS)                    │
│              │  WebSocket + STOMP (实时通信)                 │
│              │  Docker Java + GraalJS (代码沙箱)             │
└─────────────────────────────────────────────────────────────┘
```



## 5. 系统界面预览
- 仪表盘：  
  ![仪表盘](docs/images/0仪表盘.png)

- 圈子：  
  ![圈子](docs/images/2圈子.png)

- 聊天：  
  ![聊天](docs/images/3.1聊天.png)

- 智能体中心：  
  ![智能体中心](docs/images/3.2智能体中心.png)

- 智能体配置：  
  ![智能体配置](docs/images/3.3智能体配置.png)

- 书架：  
  ![书架](docs/images/4.1书架.png)

- 课程：  
  ![课程](docs/images/4课程.png)

- 智能批改：  
  ![智能批改](docs/images/5智能批改.png)

- 课程管理：  
  ![课程管理](docs/images/6课程管理.png)

- 班级管理：  
  ![班级管理](docs/images/7班级管理.png)

- 用户管理：  
  ![用户管理](docs/images/8用户管理.png)

- 教师管理：  
  ![教师管理](docs/images/9教师管理.png)

- 电子书管理：  
  ![电子书管理](docs/images/10电子书管理.png)

- 题库管理：  
  ![题库管理](docs/images/11题库管理.png)

- 试卷管理：  
  ![试卷管理](docs/images/12试卷管理.png)

- 知识库管理：  
  ![知识库管理](docs/images/13知识库管理.png)

- 公告管理：  
  ![公告管理](docs/images/14公告管理.png)

- 群聊管理：  
  ![群聊管理](docs/images/15群聊管理.png)

- 每日单词：  
  ![每日单词](docs/images/16每日单词.png)

- MCP服务器：  
  ![MCP服务器](docs/images/17MCP服务器.png)


## 6. 项目使用指南

### 6.1 环境要求
- 操作系统：Linux（生产推荐）或 Windows（开发可用）。
- 推荐硬件（开发/小规模测试）：4 CPU、8 GB RAM、SSD。生产按负载规划（CPU/GPU、内存、存储）。
- 运行时要求与依赖（示例占位）：
  - Java JDK 11+ 或 17+（后端）
  - Maven（或使用 mvnw）
  - Node.js 16+（前端）
  - Python 3.8+（数据处理与模型训练）
  - PostgreSQL 12/13+、Redis、Docker、Docker Compose
  - 可选：NVIDIA GPU、Kubernetes、Elasticsearch、Kafka

### 6.2 启动步骤
以下为典型的本地开发 / 测试启动流程（示例命令以 PowerShell 为例）。请将 `<REPO_URL>`、占位配置替换为实际值。

- 1) 克隆项目
```powershell
git clone <REPO_URL> siren
cd siren
```
- 2) 准备依赖服务（推荐使用 Docker Compose）
```bash
cd docker
Copy-Item .env.example .env
# 编辑 .env，替换占位配置（DB/Redis/OSS/AI KEY 等）
docker-compose up -d

```

- 3) 启动后端（Maven）
```bash
cd ..\backend
.\mvnw spring-boot:run
# 或构建并运行 jar
.\mvnw clean package -DskipTests
java -jar target\*.jar


```


- 4) 启动前端（React）
```bash
cd ..\web
npm install
npm run dev


```


- 5) 启动模型/数据服务（可选）
```bash
cd ..\ml
python -m venv .venv
.\.venv\Scripts\Activate.ps1
pip install -r requirements.txt
python serve_inference.py --port <INFERENCE_PORT>



```
## 6.3 访问地址
- 前端 UI（占位）：http://localhost:<FRONTEND_PORT>/
- 后端 API（占位）：http://localhost:<BACKEND_PORT>/api/
- Swagger（占位）：http://localhost:<BACKEND_PORT>/swagger-ui.html

---

## 6.4 默认账号说明
- 默认管理员账号（占位，请上线前修改）：
  - 用户名：admin 
  - 密码：123456
- 安全建议：首次部署后立即修改默认管理员密码，并使用密钥管理服务保存凭证。

---

# 7. 典型使用流程
1. 登录系统（管理员/教师账号）
2. 导入题库与样本数据（CSV/API）
3. 标注与质检，生成训练集版本
4. 触发训练并监控训练指标
5. 验证模型并一键部署推理服务
6. 前端接入后对学生作答进行自动批改
7. 收集低置信度/错误样本回流用于下一轮训练

# 8. 演示视频
- 通过网盘分享的文件：AI课堂.mp4
  链接: https://pan.baidu.com/s/1Q74AaZirgY08MFQvmcuVzA 提取码: bn37
