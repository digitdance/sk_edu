<template>
  <div v-if="!auth.sessionReady" class="session-loading" aria-busy="true">Loading session...</div>
  <LoginView v-else-if="!auth.loggedIn" />
  <div v-else class="student-shell">
    <TopNav v-if="activeView !== 's-exam-taking'" :active="activeView" @navigate="navigate" />
    <main class="app-content" :class="{ bare: activeView === 's-exam-taking' }">
      <component :is="meta.component" :key="activeView" @navigate="navigate" />
    </main>
    <footer v-if="activeView !== 's-exam-taking'" class="student-foot">
      教学资源平台 · 学生学习中心 © 2026 重庆数字职业学院
    </footer>
    <AiChatWidget v-if="activeView !== 's-exam-taking'" />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import TopNav from './components/TopNav.vue'
import AiChatWidget from './components/AiChatWidget.vue'

import StudentHomeView from './views/student/StudentHomeView.vue'
import StudentCoursesView from './views/student/StudentCoursesView.vue'
import StudentClassView from './views/student/StudentClassView.vue'
import StudentLearnView from './views/student/StudentLearnView.vue'
import StudentHomeworkView from './views/student/StudentHomeworkView.vue'
import StudentExamsView from './views/student/StudentExamsView.vue'
import StudentGradesView from './views/student/StudentGradesView.vue'
import StudentDiscussView from './views/student/StudentDiscussView.vue'
import StudentNotesView from './views/student/StudentNotesView.vue'
import StudentNoticeView from './views/student/StudentNoticeView.vue'
import StudentExamTakeView from './views/student/StudentExamTakeView.vue'
import StudentProfileView from './views/student/StudentProfileView.vue'
import StudentSettingsView from './views/student/StudentSettingsView.vue'
import LoginView from './views/LoginView.vue'
import { auth, restoreSession } from './stores/auth'

export type ViewId =
  | 's-home' | 's-courses' | 's-class' | 's-learn' | 's-homework'
  | 's-exams' | 's-grades' | 's-discuss' | 's-notes'
  | 's-notice' | 's-exam-taking' | 's-profile' | 's-settings'

const viewMeta: Record<ViewId, { component: unknown }> = {
  's-home': { component: StudentHomeView },
  's-courses': { component: StudentCoursesView },
  's-class': { component: StudentClassView },
  's-learn': { component: StudentLearnView },
  's-homework': { component: StudentHomeworkView },
  's-exams': { component: StudentExamsView },
  's-grades': { component: StudentGradesView },
  's-discuss': { component: StudentDiscussView },
  's-notes': { component: StudentNotesView },
  's-notice': { component: StudentNoticeView },
  's-exam-taking': { component: StudentExamTakeView },
  's-profile': { component: StudentProfileView },
  's-settings': { component: StudentSettingsView }
}

const activeView = ref<ViewId>('s-home')
const meta = computed(() => viewMeta[activeView.value])
onMounted(() => { void restoreSession() })

function navigate(id: string) {
  if (id in viewMeta) {
    activeView.value = id as ViewId
    window.scrollTo({ top: 0 })
  }
}
</script>

<style scoped>
.student-shell { min-height: 100vh; display: flex; flex-direction: column; }
.app-content { padding-top: 76px; }
.app-content.bare { padding-top: 0; }
.student-foot {
  margin-top: auto; padding: 18px 24px 22px; text-align: center;
  font-size: 12px; color: var(--app-ink-3);
}
</style>
