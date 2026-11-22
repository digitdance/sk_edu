# Phase 2 Optimization - Implementation Plans Index

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement these plans task-by-task.

**Based on Spec:** [2026-06-19-phase2-optimization-design.md](../specs/2026-06-19-phase2-optimization-design.md)

**Total Estimated Time:** 5-7 days

**Execution Order:** Plan 5 → Plan 1 → Plan 3 → Plan 2 → Plan 4

---

## Overview

5 个独立可交付的优化计划，互不阻塞，按风险从低到高排列：

| # | Plan | Goal | Time | Risk | Status |
|---|------|------|------|------|--------|
| 5 | [Configuration Governance](./2026-06-19-plan5-config-governance.md) | application.yml 587→300 lines, docker-compose 51→5 env | 1 day | Low | ✅ Ready |
| 1 | [Startup Optimization](./2026-06-19-plan1-startup-optimization.md) | 168s → 30-45s | 1 day | Low | 📝 Draft |
| 3 | [Workflow Engine Split](./2026-06-19-plan3-workflow-split.md) | 2 files 2961 lines → 11 focused classes | 2 days | Medium | 📝 Draft |
| 2 | [PPT Agent Split](./2026-06-19-plan2-ppt-agent-split.md) | 2450 lines → 6 classes (5 + facade) | 1.5 days | Medium | 📝 Draft |
| 4 | [Frontend NodeConfigPanel Split](./2026-06-19-plan4-frontend-split.md) | 3671 lines → 8 category files | 1 day | Low | 📝 Draft |

---

## Plan 5: Configuration Governance ✅

**File:** [2026-06-19-plan5-config-governance.md](./2026-06-19-plan5-config-governance.md)

**Goal:** Reduce config complexity, enable fail-fast validation

**Key Changes:**
- YAML anchors for AI provider defaults (150→60 lines)
- `@ConfigurationProperties` for PPT Agent (type-safe + validation)
- docker-compose `env_file` automation (51→5 explicit env vars)
- Profile split: `application-ai.yml` + `application-media.yml`

**Tasks:** 8 tasks, 32 steps

**Deliverables:**
- `backend/src/main/resources/application-ai.yml` (~250 lines)
- `backend/src/main/java/com/novacloudedu/backend/config/PptAgentProperties.java`
- `application.yml` reduced to ~300 lines
- docker-compose.yml backend env block < 10 lines

---

## Plan 1: Startup Optimization (Draft)

**Goal:** 168s → 30-45s cold start

**Key Changes:**
- Python sandbox async image preload
- `@Lazy` for non-critical beans (BaiduOcr, SeleniumScraper, NLS, DashScope multimodal)
- Spring Context Indexer
- Reduce Spring Data repo scanning scope
- Docker healthcheck adjustment

**Estimated Tasks:** 6 tasks, ~25 steps

**Status:** Plan document to be written. Use spec Section 1 as blueprint.

---

## Plan 3: Workflow Engine Split (Draft)

**Goal:** 2 giant classes (1587 + 1374 lines) → 11 focused classes

**Key Changes:**

**A. DefaultWorkflowEngine → 5 classes:**
- `DefaultWorkflowEngine` (facade, ~400 lines)
- `WorkflowExecutionRunner` (~350 lines)
- `WorkflowContainerExecutor` (loop/parallel, ~450 lines)
- `WorkflowBuiltinNodeHandler` (variable set/get, ~200 lines)
- `WorkflowConditionEvaluator` (expression parsing, ~250 lines)

**B. WorkflowApplicationService → 6 classes + DTO package:**
- `WorkflowApplicationService` (facade, ~150 lines)
- `WorkflowCrudService` (~200 lines)
- `WorkflowExecutionService` (~180 lines)
- `WorkflowGraphEditor` (nodes/edges, ~300 lines)
- `WorkflowVariableService` (~150 lines)
- `WorkflowValidationService` (~200 lines)
- `dto/` package (9 VO classes + DtoMapper)

**Estimated Tasks:** 12 tasks (6 for engine, 6 for service), ~50 steps

**Status:** Plan document to be written. Use spec Section 3 as blueprint.

---

## Plan 2: PPT Agent Split (Draft)

**Goal:** 2450 lines → 6 classes by agent stage

**Key Changes:**
- `PptAgentOrchestrator` → facade (~350 lines)
- `PptOutlinePlanner` (Stage 1, ~180 lines)
- `PptSlideFactory` (Stage 2 regular + HTML, ~850 lines)
- `PptEvaluator` (Stage 3, ~280 lines)
- `PptReflector` (Stage 3b repair, ~600 lines)
- `PptAgentJsonUtils` (~180 lines)

**Estimated Tasks:** 7 tasks (6 classes + integration test), ~30 steps

**Status:** Plan document to be written. Use spec Section 2 as blueprint.

---

## Plan 4: Frontend NodeConfigPanel Split (Draft)

**Goal:** 3671 lines → 8 category files

**Key Changes:**
```
web/src/pages/admin/workflow/components/
├── NodeConfigPanel.tsx (~120 lines, dispatch only)
└── node-configs/
    ├── shared.tsx (hooks + constants + CSS)
    ├── StartEndConfig.tsx (~200 lines)
    ├── LLMConfigs.tsx (~740 lines)
    ├── FlowControlConfigs.tsx (~820 lines)
    ├── DataTransformConfigs.tsx (~450 lines)
    ├── IoConfigs.tsx (~520 lines)
    └── CodeConfig.tsx (~280 lines)
```

**Estimated Tasks:** 9 tasks (8 files + registry rewrite + verification), ~35 steps

**Status:** Plan document to be written. Use spec Section 4 as blueprint.

---

## Execution Notes

### For Subagent-Driven Development

Start with Plan 5, complete all 8 tasks sequentially:
```bash
# Dispatch to subagent-driven-development skill with:
/loop Plan 5 Task 1 → Task 2 → ... → Task 8 → Verify
```

After Plan 5 verified ✅, proceed to Plan 1, etc.

### For Inline Execution

Use executing-plans skill with checkpoint reviews:
```bash
# Load plan, execute tasks 1-4, checkpoint review
# Continue tasks 5-8, final review
```

### Parallel Execution (Advanced)

Plans 5 and 1 are independent and can run in parallel:
- Worker A: Plan 5 (config governance)
- Worker B: Plan 1 (startup optimization)

Plans 2-4 should wait until Plan 1 complete (startup metrics needed for baseline).

---

## Success Criteria (All Plans)

- [ ] Backend cold start < 45s (Plan 1)
- [ ] No single Java file > 900 lines (Plans 2, 3)
- [ ] No single TSX file > 850 lines (Plan 4)
- [ ] application.yml < 350 lines (Plan 5)
- [ ] docker-compose backend env < 10 lines (Plan 5)
- [ ] All existing tests pass
- [ ] PPT generation end-to-end test passes
- [ ] Workflow execution end-to-end test passes
- [ ] No regression in AI chat/assistant features

---

## Next Steps

1. ✅ **Plan 5 is ready** - can start immediately
2. 📝 Write detailed plans for 1, 2, 3, 4 (using spec as blueprint)
3. Execute in order: 5 → 1 → 3 → 2 → 4
4. Each plan independently testable with its own verification checklist

**Would you like me to:**
- A. Write all 4 remaining plans now (will be very long, ~2000 lines total)
- B. Execute Plan 5 first, then write remaining plans
- C. Write one more plan (which one: 1/2/3/4?) then pause
