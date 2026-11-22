<template>
  <div v-if="!auth.sessionReady" class="session-loading" aria-busy="true">Loading session...</div>
  <LoginView v-else-if="!auth.loggedIn" />
  <div v-else class="app-shell">
    <SidebarNav :active="activeView" :open="sidebarOpen" @navigate="navigate" @close="sidebarOpen = false" />
    <div class="app-main">
      <TopBar
        :title="meta.title"
        :subtitle="meta.subtitle"
        @toggle-sidebar="sidebarOpen = !sidebarOpen"
        @navigate="navigate"
      />
      <main class="app-content">
        <component :is="meta.component" :key="activeView" @navigate="navigate" />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import SidebarNav from './components/SidebarNav.vue'
import TopBar from './components/TopBar.vue'

import CourseBuilderView from './views/CourseBuilderView.vue'
import CoursePortalEditorView from './views/CoursePortalEditorView.vue'
import ResourceLibraryView from './views/ResourceLibraryView.vue'
import LearningControlView from './views/LearningControlView.vue'
import QuestionBankView from './views/QuestionBankView.vue'
import HomeworkManageView from './views/HomeworkManageView.vue'
import HomeworkGradingView from './views/HomeworkGradingView.vue'
import HomeworkLibraryView from './views/HomeworkLibraryView.vue'
import ExamView from './views/ExamView.vue'
import GradeStatsView from './views/GradeStatsView.vue'
import AiLessonPrepView from './views/AiLessonPrepView.vue'
import AiLearningAnalysisView from './views/AiLearningAnalysisView.vue'
import AiQAAssistantView from './views/AiQAAssistantView.vue'
import UserPermissionView from './views/UserPermissionView.vue'
import LoginView from './views/LoginView.vue'
import ProfileView from './views/ProfileView.vue'
import AccountSettingsView from './views/AccountSettingsView.vue'
import { auth, restoreSession } from './stores/auth'
import { loadTeacherCourses } from './stores/courses'

export type ViewId =
  | 'course' | 'portal' | 'resources' | 'control' | 'bank' | 'homework'
  | 'grading' | 'homework-library' | 'exam' | 'grades' | 'ai-prep' | 'ai-analysis' | 'ai-qa' | 'users'
  | 'profile' | 'settings'

const viewMeta: Record<ViewId, { title: string; subtitle: string; component: unknown }> = {
  course: { title: '课程建设', subtitle: '我的全部课程 · 内容编辑、章节任务点与课程设置', component: CourseBuilderView },
  portal: { title: '课程门户预览', subtitle: '学生端上课页面 · 教师所见即所得编辑', component: CoursePortalEditorView },
  resources: { title: '资源库', subtitle: '个人云盘、课程资源、转码与共享管理', component: ResourceLibraryView },
  control: { title: '学习控制', subtitle: '章节开放模式、任务点矩阵与视频播放管控', component: LearningControlView },
  bank: { title: '题库与试卷', subtitle: '题库管理、模板导入导出与随机组卷', component: QuestionBankView },
  homework: { title: '作业管理', subtitle: '作业创建、题目来源与评分规则设置', component: HomeworkManageView },
  'homework-library': { title: 'Homework library', subtitle: 'Saved assignment drafts for reuse', component: HomeworkLibraryView },
  grading: { title: '作业批阅', subtitle: '客观题自动判分、主观题批阅与 AI 参考评分', component: HomeworkGradingView },
  exam: { title: '测验考试', subtitle: '考试安排、随机多套试卷与试卷分析', component: ExamView },
  grades: { title: '成绩统计', subtitle: '成绩权重、分数分布、排名与线下成绩录入', component: GradeStatsView },
  'ai-prep': { title: '智能备课设计', subtitle: '教案、随堂练习、PPT 大纲与实训任务一键生成', component: AiLessonPrepView },
  'ai-analysis': { title: '学情分析与建议', subtitle: '成绩趋势、AI 批改校准、错题与薄弱点分析', component: AiLearningAnalysisView },
  'ai-qa': { title: '智能问答助手', subtitle: '课程知识库绑定、问答边界与问答记录维护', component: AiQAAssistantView },
  users: { title: '用户与权限', subtitle: '组织机构、用户角色与功能权限矩阵', component: UserPermissionView },
  profile: { title: '个人中心', subtitle: '个人信息与教学名片管理', component: ProfileView },
  settings: { title: '账号设置', subtitle: '密码安全、消息通知与隐私设置', component: AccountSettingsView }
}

const activeView = ref<ViewId>('course')
const sidebarOpen = ref(false)
const meta = computed(() => viewMeta[activeView.value])
onMounted(() => { void restoreSession() })
watch(() => auth.sessionReady && auth.loggedIn, (active) => { if (active) void loadTeacherCourses(auth.userId).catch(() => undefined) }, { immediate: true })

function navigate(id: string) {
  if (id in viewMeta) {
    activeView.value = id as ViewId
    sidebarOpen.value = false
  }
}
</script>
