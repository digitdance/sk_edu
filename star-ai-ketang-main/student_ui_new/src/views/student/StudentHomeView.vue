<template>
  <div>
    <!-- 欢迎横幅 -->
    <div class="hero panel">
      <div>
        <h2>你好，刘洋</h2>
        <p>今天也要继续加油 · 你有 2 项作业和 1 场考试即将截止</p>
      </div>
      <el-button color="#fff" style="color:#2f6bff" @click="$emit('navigate', 's-homework')">去完成待办</el-button>
    </div>

    <!-- 统计 -->
    <div class="grid-4" style="margin-bottom:16px">
      <div class="panel stat-mini" style="margin:0"><span class="num mono">2</span><span class="lbl">在学课程</span></div>
      <div class="panel stat-mini" style="margin:0"><span class="num mono" style="color:#e6a23c">2</span><span class="lbl">待完成作业</span></div>
      <div class="panel stat-mini" style="margin:0"><span class="num mono" style="color:#f56c6c">1</span><span class="lbl">待参加考试</span></div>
      <div class="panel stat-mini" style="margin:0"><span class="num mono" style="color:#2f6bff">91.5</span><span class="lbl">平均总评</span></div>
    </div>

    <div class="home-grid">
      <!-- 我的课程 -->
      <div class="panel" style="margin:0">
        <div class="panel-title">
          <h3>我的课程</h3>
          <el-button link type="primary" @click="$emit('navigate', 's-courses')">全部课程</el-button>
        </div>
        <div class="grid-2">
          <div v-for="c in courses" :key="c.name" class="course-card" :style="{ background: c.gradient }" @click="$emit('navigate', 's-learn')">
            <div>
              <h4>{{ c.name }}</h4>
              <div class="meta">{{ c.teacher }} · {{ c.progress }}% 已完成</div>
            </div>
            <div class="foot">
              <el-progress :percentage="c.progress" :stroke-width="6" color="rgba(255,255,255,.9)" :show-text="false" style="flex:1" />
              <span class="pct">继续学习</span>
            </div>
          </div>
        </div>

        <!-- 公告 -->
        <div class="panel-title" style="margin-top:20px"><h3>课程公告</h3></div>
        <div class="notice-list">
          <div v-for="n in notices" :key="n.id" class="notice-item" @click="openNotice(n.id)">
            <el-tag size="small" effect="light" :type="n.type">{{ n.course }}</el-tag>
            <div class="n-body">
              <strong>{{ n.title }}</strong>
              <span class="muted">{{ n.time }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 成绩 + 待办 -->
      <div>
        <div class="panel score-panel">
          <div class="panel-title"><h3>我的成绩</h3><el-button link type="primary" @click="$emit('navigate', 's-grades')">详情</el-button></div>
          <div class="score-main">
            <span class="big-score mono">91.5</span>
            <div class="score-sub">
              <span>本学期平均总评</span>
              <el-tag size="small" type="success" effect="light">班级前 15%</el-tag>
            </div>
          </div>
          <div v-for="s in scoreItems" :key="s.label" class="score-row">
            <span class="s-label">{{ s.label }}</span>
            <el-progress :percentage="s.value" :stroke-width="8" :color="s.color" :show-text="false" style="flex:1" />
            <span class="s-val mono">{{ s.value }}</span>
          </div>
        </div>

        <div class="panel">
          <div class="panel-title"><h3>待办事项</h3></div>
          <div class="todo-list">
            <div v-for="t in todos" :key="t.title" class="todo-item" @click="$emit('navigate', t.to)">
              <div class="t-icon" :style="{ background: t.bg, color: t.color }">
                <component :is="t.icon" :size="15" />
              </div>
              <div class="t-body">
                <strong>{{ t.title }}</strong>
                <span class="muted">{{ t.desc }}</span>
              </div>
              <el-tag size="small" effect="plain" :type="t.urgent ? 'danger' : 'info'">{{ t.deadline }}</el-tag>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ClipboardList, FileCheck2, PlayCircle } from 'lucide-vue-next'
import { activeNoticeId, notices } from '../../stores/notices'

const emit = defineEmits<{ navigate: [id: string] }>()

function openNotice(id: number) {
  activeNoticeId.value = id
  emit('navigate', 's-notice')
}

const courses = [
  { name: '短视频制作与运营', teacher: '王琳', progress: 68, gradient: 'linear-gradient(135deg, #2f6bff, #6b4dff)' },
  { name: '新媒体文案写作', teacher: '李思颖', progress: 42, gradient: 'linear-gradient(135deg, #12b5a5, #0d8a7d)' }
]

const scoreItems = [
  { label: '短视频制作与运营', value: 92, color: '#2f6bff' },
  { label: '新媒体文案写作', value: 88, color: '#12b5a5' }
]

const todos = [
  { title: '第 2 章作业 · 视频剪辑基础', desc: '短视频制作与运营', deadline: '后天 22:00', urgent: true, icon: ClipboardList, bg: '#fff3e2', color: '#d97b00', to: 's-homework' },
  { title: '第 3 章章节测验', desc: '开放后 48 小时内完成', deadline: '11-24 开放', urgent: false, icon: FileCheck2, bg: '#eaf0ff', color: '#2f6bff', to: 's-exams' },
  { title: '继续学习 2.2 剪辑软件快速上手', desc: '上次观看到 06:12', deadline: '任务点', urgent: false, icon: PlayCircle, bg: '#e7f6ec', color: '#1a9e50', to: 's-learn' }
]
</script>

<style scoped>
.hero {
  display: flex; align-items: center; justify-content: space-between; gap: 16px;
  background: linear-gradient(120deg, #2f6bff 0%, #6b4dff 100%); color: #fff; border: none;
}
.hero h2 { margin: 0 0 6px; font-size: 20px; }
.hero p { margin: 0; font-size: 13px; opacity: .88; }
.home-grid { display: grid; grid-template-columns: 1.6fr 1fr; gap: 16px; align-items: start; }
.notice-list { display: flex; flex-direction: column; gap: 4px; }
.notice-item { display: flex; align-items: center; gap: 10px; padding: 9px 0; border-bottom: 1px solid #f2f4f9; }
.notice-item:last-child { border-bottom: none; }
.n-body { display: flex; flex-direction: column; }
.n-body strong { font-size: 13px; }
.score-main { display: flex; align-items: center; gap: 14px; margin-bottom: 14px; }
.big-score { font-size: 38px; font-weight: 800; color: var(--el-color-primary); }
.score-sub { display: flex; flex-direction: column; gap: 5px; font-size: 12px; color: var(--app-ink-3); }
.score-row { display: flex; align-items: center; gap: 10px; margin-bottom: 10px; font-size: 12.5px; }
.s-label { width: 118px; flex-shrink: 0; color: var(--app-ink-2); }
.s-val { width: 26px; text-align: right; font-weight: 700; }
.todo-list { display: flex; flex-direction: column; gap: 4px; }
.todo-item { display: flex; align-items: center; gap: 10px; padding: 9px 6px; border-radius: 8px; cursor: pointer; }
.todo-item:hover { background: #f7f9ff; }
.t-icon { width: 32px; height: 32px; border-radius: 8px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.t-body { flex: 1; display: flex; flex-direction: column; min-width: 0; }
.t-body strong { font-size: 13px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
@media (max-width: 960px) {
  .home-grid { grid-template-columns: 1fr; }
}
</style>
