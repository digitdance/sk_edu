<template>
  <div>
    <!-- 顶部返回条 -->
    <div class="panel learn-head">
      <el-button link @click="$emit('navigate', 's-courses')">
        <ArrowLeft :size="16" style="vertical-align:-3px" /> 返回课程
      </el-button>
      <div class="lh-info">
        <strong>短视频制作与运营</strong>
        <span class="muted">第 2 章 · 2.2 剪辑软件快速上手</span>
      </div>
      <div class="lh-progress">
        <span class="muted">本章进度</span>
        <el-progress :percentage="64" :stroke-width="8" style="width:140px" />
      </div>
    </div>

    <div class="learn-layout">
      <!-- 章节目录 -->
      <div class="panel catalog-col">
        <div class="panel-title"><h3>课程目录</h3></div>
        <div class="catalog">
          <div v-for="ch in catalog" :key="ch.title" class="cat-ch">
            <div class="cat-ch-title" :class="{ locked: ch.locked }">
              <Lock v-if="ch.locked" :size="13" />
              <CheckCircle2 v-else-if="ch.done" :size="13" style="color:#2fbf71" />
              <PlayCircle v-else :size="13" style="color:var(--el-color-primary)" />
              <span>{{ ch.title }}</span>
            </div>
            <div
              v-for="sec in ch.sections" :key="sec.title"
              class="cat-sec" :class="{ active: sec.active, locked: ch.locked }"
              @click="selectSection(sec, ch.locked)"
            >
              <span class="grow">{{ sec.title }}</span>
              <CheckCircle2 v-if="sec.done" :size="13" style="color:#2fbf71" />
              <span v-else class="muted">{{ sec.duration }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 播放区 -->
      <div>
        <div class="player-box" ref="playerBoxRef">
          <button class="play-btn" @click="togglePlay">
            <Pause v-if="playing" :size="26" />
            <Play v-else :size="26" />
          </button>
          <div class="hud">
            <div class="hud-row">
              <span class="mono">{{ formatTime(currentTime) }}</span>
              <div class="track" @click="seekClick">
                <div class="fill" :style="{ width: progress + '%' }" />
                <span
                  v-for="q in quizMarkers" :key="q.time"
                  class="quiz-marker" :style="{ left: (q.time / duration * 100) + '%' }"
                  :title="`随堂测验 ${formatTime(q.time)}`"
                  @click.stop="openQuiz(q)"
                />
              </div>
              <span class="mono">{{ formatTime(duration) }}</span>
            </div>
            <div class="hud-row" style="margin-top:6px; justify-content:flex-end; gap:14px">
              <span class="hud-tip">已开启防拖拽 · 防切屏</span>
              <el-select v-model="speed" size="small" style="width:86px" @change="onSpeedChange">
                <el-option v-for="s in ['1.0', '1.25', '1.5']" :key="s" :label="s + 'x'" :value="s" />
              </el-select>
            </div>
          </div>
        </div>
        <p class="player-note">2.2 剪辑软件快速上手 · 任务点 · 完整观看后自动标记完成 · 含 {{ quizMarkers.length }} 个随堂测验</p>

        <!-- 讨论 / 笔记 / 资料 -->
        <div class="panel" style="margin-top:14px">
          <el-tabs v-model="tab">
            <el-tab-pane label="讨论区（24）" name="discuss">
              <div class="discuss-list">
                <div v-for="d in discussions" :key="d.id" class="discuss-item">
                  <div class="d-avatar">{{ d.author[0] }}</div>
                  <div class="d-body">
                    <div class="d-head">
                      <strong>{{ d.author }}</strong>
                      <el-tag v-if="d.isTeacher" size="small" type="primary" effect="light">教师</el-tag>
                      <span class="muted">{{ d.time }}</span>
                    </div>
                    <p>{{ d.content }}</p>
                    <div class="d-actions">
                      <el-button link size="small" @click="d.liked = !d.liked; d.likes += d.liked ? 1 : -1">
                        <ThumbsUp :size="13" :style="{ color: d.liked ? 'var(--el-color-primary)' : '' }" /> {{ d.likes }}
                      </el-button>
                      <el-button link size="small" @click="replyTo = d.author">回复</el-button>
                    </div>
                  </div>
                </div>
              </div>
              <div class="d-editor">
                <el-input v-model="discussInput" type="textarea" :rows="2"
                  :placeholder="replyTo ? `回复 ${replyTo}…` : '发表你的问题或看法…'" />
                <div style="display:flex; justify-content:space-between; margin-top:8px">
                  <span v-if="replyTo" class="muted">正在回复 {{ replyTo }} <el-button link size="small" @click="replyTo = ''">取消</el-button></span>
                  <span v-else />
                  <el-button type="primary" size="small" @click="postDiscuss">发布</el-button>
                </div>
              </div>
            </el-tab-pane>

            <el-tab-pane :label="`我的笔记（${notes.length}）`" name="notes">
              <div class="note-list">
                <div v-for="n in notes" :key="n.id" class="note-item" @click="jumpTo(n.time)">
                  <el-tag size="small" effect="plain" class="mono">{{ formatTime(n.time) }}</el-tag>
                  <span class="grow">{{ n.content }}</span>
                  <el-button link type="danger" size="small" @click.stop="deleteNote(n.id)">删除</el-button>
                </div>
                <el-empty v-if="!notes.length" description="暂无笔记，播放时随时记录" :image-size="60" />
              </div>
              <div class="d-editor">
                <el-input v-model="noteInput" type="textarea" :rows="2" :placeholder="`在当前时间点 ${formatTime(currentTime)} 记录笔记…`" />
                <div style="display:flex; justify-content:flex-end; margin-top:8px">
                  <el-button type="primary" size="small" @click="addNote">保存笔记</el-button>
                </div>
              </div>
            </el-tab-pane>

            <el-tab-pane label="课程资料（3）" name="files">
              <div class="file-list">
                <div v-for="f in courseFiles" :key="f.name" class="file-item">
                  <FileText :size="16" style="color:var(--el-color-primary)" />
                  <span class="grow">{{ f.name }}</span>
                  <span class="muted">{{ f.size }}</span>
                  <el-button link type="primary" size="small" @click="previewFile(f.name)">预览</el-button>
                  <el-button link type="primary" size="small" @click="downloadRes(f.name)">下载</el-button>
                </div>
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
    </div>

    <!-- 随堂测验弹窗 -->
    <el-dialog v-model="quizVisible" title="随堂测验" width="480px" :close-on-click-modal="false" @closed="afterQuizClosed">
      <template v-if="activeQuiz">
        <div class="quiz-meta">
          <el-tag size="small" effect="plain" class="mono">{{ formatTime(activeQuiz.time) }}</el-tag>
          <el-tag size="small" type="warning" effect="light">{{ activeQuiz.type }}</el-tag>
          <span class="muted">必答 · {{ activeQuiz.score }} 分</span>
        </div>
        <p class="quiz-stem">{{ activeQuiz.question }}</p>
        <el-radio-group v-model="quizAnswer" class="quiz-options">
          <el-radio v-for="opt in activeQuiz.options" :key="opt" :value="opt" class="q-opt">{{ opt }}</el-radio>
        </el-radio-group>
        <el-alert v-if="quizResult" :type="quizResult === 'right' ? 'success' : 'error'" :closable="false" show-icon
          :title="quizResult === 'right' ? '回答正确，继续学习！' : `回答错误，正确答案：${activeQuiz.answer}`" />
      </template>
      <template #footer>
        <el-button v-if="!quizResult" type="primary" :disabled="!quizAnswer" @click="submitQuiz">提交答案</el-button>
        <template v-else>
          <el-button v-if="quizResult === 'wrong'" @click="quizResult = ''; quizAnswer = ''">再试一次</el-button>
          <el-button type="primary" @click="continueLearning">继续学习</el-button>
        </template>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  ArrowLeft, Play, Pause, Lock, CheckCircle2, PlayCircle, ThumbsUp, FileText
} from 'lucide-vue-next'
import { previewDocument } from '../../utils/kkPreview'
import { downloadText } from '../../utils/download'

/** 课程资料在线预览：走 kkFileView 文档服务 */
function previewFile(name: string) {
  if (previewDocument(name)) ElMessage.success(`正在打开「${name}」在线预览`)
  else ElMessage.info('该类型暂不支持在线预览，请下载后查看')
}
function downloadRes(name: string) {
  downloadText(name, `课程资料：${name}\n（接入对象存储后替换为真实文件流）`)
  ElMessage.success(`开始下载「${name}」`)
}

defineEmits<{ navigate: [id: string] }>()

const tab = ref('discuss')
const playing = ref(false)
const currentTime = ref(372) // 上次观看到 06:12
const duration = 552 // 总长 09:12
const speed = ref('1.0')

interface Quiz { time: number; type: string; question: string; options: string[]; answer: string; score: number; done: boolean }
const quizMarkers = ref<Quiz[]>([
  { time: 430, type: '单选题', question: '「黄金三秒」原则强调短视频开头应当？', options: ['铺陈背景', '快速抛出核心看点', '展示制作名单', '放缓节奏'], answer: '快速抛出核心看点', score: 5, done: false },
  { time: 510, type: '单选题', question: 'J-cut 转场技巧指的是？', options: ['画面先于声音进入', '声音先于画面进入', '声画同时切换', '画面叠化'], answer: '声音先于画面进入', score: 5, done: false }
])

const quizVisible = ref(false)
const activeQuiz = ref<Quiz | null>(null)
const quizAnswer = ref('')
const quizResult = ref<'right' | 'wrong' | ''>('')
let timer: number | undefined

const progress = computed(() => Math.min(100, (currentTime.value / duration) * 100))

const catalog = [
  {
    title: '第 1 章 短视频认知与平台生态', done: true, locked: false,
    sections: [
      { title: '1.1 课程导学', duration: '08:24', done: true, active: false },
      { title: '1.2 平台生态与发展趋势', duration: '12:10', done: true, active: false }
    ]
  },
  {
    title: '第 2 章 拍摄与剪辑基础', done: false, locked: false,
    sections: [
      { title: '2.1 构图与运镜', duration: '15:42', done: true, active: false },
      { title: '2.2 剪辑软件快速上手', duration: '06:12', done: false, active: true },
      { title: '2.3 章节测验', duration: '10 题', done: false, active: false }
    ]
  },
  {
    title: '第 3 章 平台推荐机制与账号定位', done: false, locked: true,
    sections: [{ title: '3.1 推荐算法拆解', duration: '14:36', done: false, active: false }]
  }
]

const discussions = ref([
  { id: 1, author: '王琳', isTeacher: true, time: '11-18 16:20', content: '2.2 节操作演示用的素材包已上传到「课程资料」，大家课后跟着做一遍。', likes: 18, liked: false },
  { id: 2, author: '陈思雨', isTeacher: false, time: '11-19 09:12', content: '老师，关键帧动画那部分 0.5 倍速看还是有点快，能出一期慢速跟练吗？', likes: 6, liked: false },
  { id: 3, author: '王子涵', isTeacher: false, time: '11-19 10:45', content: '同求！另外发现手机剪辑 App 的界面和课件不太一样。', likes: 3, liked: false }
])
const discussInput = ref('')
const replyTo = ref('')

const notes = ref([
  { id: 1, time: 96, content: '黄金三秒：开头必须给出冲突 / 悬念 / 利益点之一' },
  { id: 2, time: 215, content: 'J-cut = 声音先进，画面后切，常用于访谈转场' }
])
const noteInput = ref('')

const courseFiles = [
  { name: '2.2 剪辑实操素材包.zip', size: '58 MB' },
  { name: '剪辑快捷键速查表.pdf', size: '320 KB' },
  { name: '章节讲义 · 拍摄与剪辑.pdf', size: '4.8 MB' }
]

function formatTime(sec: number) {
  const s = Math.max(0, Math.floor(sec))
  return `${String(Math.floor(s / 60)).padStart(2, '0')}:${String(s % 60).padStart(2, '0')}`
}

function togglePlay() {
  playing.value = !playing.value
  if (playing.value) startTimer()
  else stopTimer()
}
function startTimer() {
  stopTimer()
  timer = window.setInterval(() => {
    currentTime.value = Math.min(duration, currentTime.value + 1)
    const hit = quizMarkers.value.find(q => !q.done && Math.abs(currentTime.value - q.time) < 0.6)
    if (hit) openQuiz(hit)
    if (currentTime.value >= duration) {
      togglePlay()
      ElMessage.success('本小节学习完成，任务点已自动标记')
    }
  }, 1000 / Number(speed.value))
}
function stopTimer() { if (timer) { window.clearInterval(timer); timer = undefined } }

function seekClick(e: MouseEvent) {
  const track = e.currentTarget as HTMLElement
  const rect = track.getBoundingClientRect()
  const ratio = Math.min(1, Math.max(0, (e.clientX - rect.left) / rect.width))
  const target = ratio * duration
  if (target > currentTime.value + 5) {
    ElMessage.warning('防拖拽已开启：不能跳过未观看的内容')
    return
  }
  currentTime.value = target
}
function onSpeedChange() { if (playing.value) startTimer() }

function openQuiz(q: Quiz) {
  if (q.done) return
  activeQuiz.value = q
  quizAnswer.value = ''
  quizResult.value = ''
  quizVisible.value = true
  if (playing.value) togglePlay()
}
function submitQuiz() {
  if (!activeQuiz.value) return
  quizResult.value = quizAnswer.value === activeQuiz.value.answer ? 'right' : 'wrong'
}
function continueLearning() {
  if (activeQuiz.value && quizResult.value === 'right') activeQuiz.value.done = true
  quizVisible.value = false
}
function afterQuizClosed() {
  if (activeQuiz.value?.done && !playing.value) togglePlay()
  activeQuiz.value = null
}

function selectSection(sec: { title: string; active: boolean }, locked: boolean) {
  if (locked) { ElMessage.warning('该章节未开放：定时开放 / 需完成上一章任务点'); return }
  catalog.forEach(ch => ch.sections.forEach(s => (s.active = false)))
  sec.active = true
  ElMessage.info(`已切换到「${sec.title}」`)
}
function postDiscuss() {
  const text = discussInput.value.trim()
  if (!text) return
  discussions.value.push({
    id: Date.now(), author: '刘洋', isTeacher: false, time: '刚刚',
    content: replyTo.value ? `回复 ${replyTo.value}：${text}` : text, likes: 0, liked: false
  })
  discussInput.value = ''
  replyTo.value = ''
  ElMessage.success('已发布到讨论区')
}
function addNote() {
  const text = noteInput.value.trim()
  if (!text) return
  notes.value.unshift({ id: Date.now(), time: Math.floor(currentTime.value), content: text })
  noteInput.value = ''
  ElMessage.success('笔记已保存，并与视频时间点绑定')
}
function deleteNote(id: number) { notes.value = notes.value.filter(n => n.id !== id) }
function jumpTo(time: number) {
  currentTime.value = time
  ElMessage.info(`已跳转到 ${formatTime(time)}`)
}

/* ---------- 视频学习防切屏：切走（含最小化浏览器）立即暂停并记录 ---------- */
const switchCount = ref(0)
function onLearnVisibility() {
  if (document.hidden && playing.value) {
    playing.value = false
    stopTimer()
    switchCount.value += 1
    ElMessage.warning(`检测到切屏 / 最小化，视频已暂停（第 ${switchCount.value} 次，已记录到学习行为）`)
  }
}
function onLearnBlur() {
  if (!document.hidden && playing.value) {
    playing.value = false
    stopTimer()
    switchCount.value += 1
    ElMessage.warning(`考试/学习窗口失去焦点，视频已暂停（第 ${switchCount.value} 次，已记录）`)
  }
}

onMounted(() => {
  document.addEventListener('visibilitychange', onLearnVisibility)
  window.addEventListener('blur', onLearnBlur)
})
onBeforeUnmount(() => {
  stopTimer()
  document.removeEventListener('visibilitychange', onLearnVisibility)
  window.removeEventListener('blur', onLearnBlur)
})
</script>

<style scoped>
.learn-head { display: flex; align-items: center; gap: 16px; flex-wrap: wrap; }
.lh-info { display: flex; flex-direction: column; flex: 1; }
.lh-progress { display: flex; align-items: center; gap: 10px; font-size: 12px; }
.learn-layout { display: grid; grid-template-columns: 280px 1fr; gap: 16px; align-items: start; }
.catalog-col { max-height: 720px; overflow-y: auto; }
.catalog { display: flex; flex-direction: column; gap: 2px; }
.cat-ch-title { display: flex; align-items: center; gap: 7px; padding: 9px 6px; font-size: 13px; font-weight: 700; }
.cat-ch-title.locked { color: var(--app-ink-3); }
.cat-sec { display: flex; align-items: center; gap: 8px; padding: 8px 6px 8px 24px; font-size: 12.5px; color: var(--app-ink-2); border-radius: 6px; cursor: pointer; }
.cat-sec:hover { background: #f7f9ff; }
.cat-sec.active { background: var(--el-color-primary-light-9); color: var(--el-color-primary); font-weight: 600; }
.cat-sec.locked { opacity: .5; cursor: not-allowed; }
.grow { flex: 1; min-width: 0; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.player-note { margin: 10px 4px 0; font-size: 12.5px; color: var(--app-ink-3); }
.hud-tip { font-size: 11px; color: rgba(255,255,255,.6); }
.quiz-meta { display: flex; align-items: center; gap: 8px; margin-bottom: 10px; }
.quiz-stem { font-weight: 700; font-size: 15px; margin: 6px 0 14px; line-height: 1.6; }
.quiz-options { display: flex; flex-direction: column; gap: 4px; }
.q-opt { height: auto; padding: 9px 10px; border-radius: 8px; margin-right: 0 !important; }
.q-opt:hover { background: #f7f9ff; }
.discuss-list { display: flex; flex-direction: column; gap: 4px; margin-bottom: 14px; }
.discuss-item { display: flex; gap: 12px; padding: 12px 4px; border-bottom: 1px solid #f2f4f9; }
.d-avatar { width: 34px; height: 34px; border-radius: 50%; background: var(--el-color-primary-light-9); color: var(--el-color-primary); display: flex; align-items: center; justify-content: center; font-weight: 700; flex-shrink: 0; }
.d-body { flex: 1; min-width: 0; }
.d-head { display: flex; align-items: center; gap: 8px; }
.d-body p { margin: 6px 0; font-size: 13.5px; line-height: 1.7; color: var(--app-ink-2); }
.d-actions { display: flex; gap: 14px; }
.note-list { display: flex; flex-direction: column; gap: 8px; margin-bottom: 14px; }
.note-item { display: flex; align-items: center; gap: 10px; border: 1px solid var(--app-line); border-radius: 8px; padding: 10px 12px; cursor: pointer; font-size: 13px; }
.note-item:hover { border-color: var(--el-color-primary); }
.file-list { display: flex; flex-direction: column; gap: 8px; }
.file-item { display: flex; align-items: center; gap: 10px; border: 1px solid var(--app-line); border-radius: 8px; padding: 11px 14px; font-size: 13px; }
@media (max-width: 960px) {
  .learn-layout { grid-template-columns: 1fr; }
  .catalog-col { max-height: none; }
}
</style>
