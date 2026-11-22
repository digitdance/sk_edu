<template>
  <div>
    <div class="panel toolbar">
      <el-select v-model="courseFilter" style="width:210px">
        <el-option label="全部课程" value="all" />
        <el-option v-for="c in courseOptions" :key="c" :label="c" :value="c" />
      </el-select>
      <span style="flex:1" />
      <span class="muted">共 {{ filteredExams.length }} 场考试</span>
    </div>

    <div class="grid-2">
      <div v-for="e in filteredExams" :key="e.id" class="panel exam-card">
        <div class="ec-head">
          <div>
            <strong class="ec-name">{{ e.name }}</strong>
            <div class="muted">{{ e.course }}</div>
          </div>
          <el-tag :type="e.status === 'ongoing' ? 'success' : e.status === 'todo' ? 'warning' : 'info'" effect="light">
            {{ statusText[e.status] }}
          </el-tag>
        </div>
        <div class="ec-meta">
          <div><span class="muted">考试窗口</span><span class="mono">{{ e.window }}</span></div>
          <div><span class="muted">时长</span><span>{{ e.duration }}</span></div>
          <div><span class="muted">题量 / 总分</span><span>{{ e.count }} 题 / {{ e.score }} 分</span></div>
          <div><span class="muted">我的成绩</span>
            <strong v-if="e.status === 'done'" class="mono" :style="{ color: (e.myScore ?? 0) >= 60 ? '#2fbf71' : '#f56c6c' }">{{ e.myScore }} 分</strong>
            <span v-else>—</span>
          </div>
        </div>
        <div class="ec-actions">
          <template v-if="e.status === 'todo'">
            <el-button disabled>未开放</el-button>
            <el-button @click="ElMessage.success('已开启开考提醒，开放后通过站内信通知')">开考提醒</el-button>
          </template>
          <template v-else-if="e.status === 'ongoing'">
            <el-button type="primary" @click="enterExam(e)">进入考试</el-button>
            <el-button @click="showRules">考试须知</el-button>
          </template>
          <template v-else>
            <el-button type="primary" plain @click="viewPaper(e)">查看试卷</el-button>
            <el-button @click="ElMessage.success('错题已加入个人错题本')">错题回顾</el-button>
          </template>
        </div>
      </div>
    </div>
    <el-empty v-if="!filteredExams.length" description="该课程暂无考试安排" :image-size="80" />
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { exams, takingExam, type Exam, type Status } from '../../stores/exams'
import { startProctoring, maxViolations } from '../../utils/proctor'

const emit = defineEmits<{ navigate: [id: string] }>()

const statusText: Record<Status, string> = { todo: '未开始', ongoing: '进行中', done: '已结束' }

/* ---------- 课程筛选 ---------- */
const courseFilter = ref('all')
const courseOptions = computed(() => [...new Set(exams.value.map(e => e.course))])
const filteredExams = computed(() =>
  exams.value.filter(e => courseFilter.value === 'all' || e.course === courseFilter.value)
)

const rulesHtml = `考试须知：<br>
1. 进入考试后系统将进入<strong>全屏</strong>并开始倒计时，时间到自动交卷；<br>
2. 考试期间禁止切换标签页、切出窗口或最小化浏览器，累计切屏 ${maxViolations} 次将强制交卷；<br>
3. 请使用电脑端 Chrome / Edge 浏览器，提前关闭无关软件。`

function showRules() {
  ElMessageBox.alert(rulesHtml, '考试须知', { dangerouslyUseHTMLString: true, confirmButtonText: '知道了' })
}

function enterExam(e: Exam) {
  ElMessageBox.confirm(`${rulesHtml}<br>确认进入「${e.name}」？`, '考试确认', {
    type: 'warning', dangerouslyUseHTMLString: true, confirmButtonText: '进入考试', cancelButtonText: '再想想'
  })
    .then(() => {
      takingExam.value = e
      startProctoring() // 全屏请求必须在用户手势中发起
      emit('navigate', 's-exam-taking')
    })
    .catch(() => undefined)
}

function viewPaper(e: Exam) { ElMessage.info(`「${e.name}」试卷回顾：含答案解析与教师讲评`) }
</script>

<style scoped>
.toolbar { display: flex; align-items: center; gap: 10px; padding: 14px 18px; }
.exam-card { display: flex; flex-direction: column; gap: 12px; }
.ec-head { display: flex; align-items: flex-start; justify-content: space-between; gap: 10px; }
.ec-name { font-size: 15px; }
.ec-meta { display: grid; grid-template-columns: repeat(2, 1fr); gap: 10px 16px; font-size: 12.5px; }
.ec-meta > div { display: flex; justify-content: space-between; border-bottom: 1px dashed #eef1f6; padding-bottom: 6px; }
.ec-actions { display: flex; gap: 10px; }
</style>
