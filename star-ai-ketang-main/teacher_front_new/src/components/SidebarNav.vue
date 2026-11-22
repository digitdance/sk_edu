<template>
  <div v-if="open" class="nav-mask" @click="$emit('close')" />
  <aside class="sidebar" :class="{ open }">
    <div class="brand">
      <div class="logo">
        <GraduationCap :size="20" :stroke-width="2.2" />
      </div>
      <div class="brand-text">
        <strong>教学资源平台</strong>
        <span>教师工作台</span>
      </div>
    </div>

    <nav class="nav-scroll">
      <template v-for="group in groups" :key="group.label">
        <p class="group-label">{{ group.label }}</p>
        <button
          v-for="item in group.items"
          :key="item.id"
          class="nav-item"
          :class="{ active: item.id === active }"
          @click="$emit('navigate', item.id)"
        >
          <component :is="item.icon" :size="16" :stroke-width="2" />
          <span>{{ item.label }}</span>
        </button>
      </template>
    </nav>

    <div class="side-foot">
      <div class="user-chip">
        <div class="avatar">王</div>
        <div class="u-meta">
          <strong>王琳 · 教师</strong>
          <span>信息工程学院</span>
        </div>
      </div>
    </div>
  </aside>
</template>

<script setup lang="ts">
import type { FunctionalComponent } from 'vue'
import {
  GraduationCap, BookOpen, FolderOpen, SlidersHorizontal, Library,
  ClipboardList, PenSquare, FileCheck2, BarChart3, Users,
  NotebookPen, ChartLine, MessageSquareText
} from 'lucide-vue-next'
import type { ViewId } from '../App.vue'

interface NavItem { id: ViewId; label: string; icon: FunctionalComponent }
interface NavGroup { label: string; items: NavItem[] }

defineProps<{ active: ViewId; open: boolean }>()
defineEmits<{ navigate: [id: string]; close: [] }>()

const groups: NavGroup[] = [
  {
    label: '教学',
    items: [
      { id: 'course', label: '课程建设', icon: BookOpen },
      { id: 'resources', label: '资源库', icon: FolderOpen },
      { id: 'control', label: '学习控制', icon: SlidersHorizontal }
    ]
  },
  {
    label: '评测',
    items: [
      { id: 'bank', label: '题库与试卷', icon: Library },
      { id: 'homework', label: '作业管理', icon: ClipboardList },
      { id: 'grading', label: '作业批阅', icon: PenSquare },
      { id: 'homework-library', label: 'Homework library', icon: Library },
      { id: 'exam', label: '测验考试', icon: FileCheck2 },
      { id: 'grades', label: '成绩统计', icon: BarChart3 }
    ]
  },
  {
    label: 'AI 教辅',
    items: [
      { id: 'ai-prep', label: '智能备课设计', icon: NotebookPen },
      { id: 'ai-analysis', label: '学情分析与建议', icon: ChartLine },
      { id: 'ai-qa', label: '智能问答助手', icon: MessageSquareText }
    ]
  },
  {
    label: '辅助',
    items: [
      { id: 'users', label: '用户与权限', icon: Users }
    ]
  }
]
</script>

<style scoped>
.sidebar {
  width: 224px; flex-shrink: 0; height: 100vh; display: flex; flex-direction: column;
  background: var(--app-sidebar-bg); color: #aeb9d4; z-index: 40;
}
.brand { display: flex; align-items: center; gap: 10px; padding: 16px 16px 14px; }
.logo {
  width: 34px; height: 34px; border-radius: 9px; flex-shrink: 0;
  background: linear-gradient(135deg, #2f6bff, #6b4dff);
  display: flex; align-items: center; justify-content: center; color: #fff;
}
.brand-text { display: flex; flex-direction: column; line-height: 1.3; }
.brand-text strong { color: #fff; font-size: 14px; }
.brand-text span { font-size: 11px; color: #6f7ea3; }

.nav-scroll { flex: 1; overflow-y: auto; padding: 4px 10px 12px; }
.nav-scroll::-webkit-scrollbar { width: 0; }
.group-label { margin: 14px 8px 6px; font-size: 11px; color: #5a6890; letter-spacing: .08em; }
.nav-item {
  width: 100%; display: flex; align-items: center; gap: 10px; border: none; cursor: pointer;
  background: transparent; color: #aeb9d4; padding: 9px 12px; border-radius: 8px;
  font-size: 13px; margin-bottom: 2px; transition: background .15s ease, color .15s ease; text-align: left;
}
.nav-item:hover { background: rgba(255, 255, 255, .06); color: #e6ebfa; }
.nav-item.active { background: var(--app-sidebar-active); color: #fff; font-weight: 600; }

.side-foot { padding: 12px; border-top: 1px solid rgba(255, 255, 255, .07); }
.user-chip { display: flex; align-items: center; gap: 10px; padding: 6px 8px; }
.avatar {
  width: 32px; height: 32px; border-radius: 50%; flex-shrink: 0;
  background: linear-gradient(135deg, #2fbf71, #1a9e50); color: #fff;
  display: flex; align-items: center; justify-content: center; font-size: 13px; font-weight: 700;
}
.u-meta { display: flex; flex-direction: column; line-height: 1.35; min-width: 0; }
.u-meta strong { color: #e6ebfa; font-size: 12.5px; }
.u-meta span { font-size: 11px; color: #6f7ea3; }

.nav-mask { display: none; }
@media (max-width: 960px) {
  .sidebar {
    position: fixed; left: 0; top: 0; transform: translateX(-100%);
    transition: transform .22s ease; box-shadow: 12px 0 32px rgba(6, 12, 30, .35);
  }
  .sidebar.open { transform: translateX(0); }
  .nav-mask { display: block; position: fixed; inset: 0; background: rgba(10, 16, 34, .45); z-index: 39; }
}
</style>
