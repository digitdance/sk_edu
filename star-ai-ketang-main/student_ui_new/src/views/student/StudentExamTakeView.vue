<template>
  <div class="exam-page">
    <h1 class="exam-title">{{ exam?.name ?? '在线考试' }}</h1>
    <p class="exam-sub muted">{{ exam?.course }} · 考试窗口 {{ exam?.window }} · 时长 {{ exam?.duration }}</p>

    <div class="exam-layout">
      <!-- ============ 答题区 ============ -->
      <div class="exam-main">
        <div v-for="(sec, si) in sections" :key="sec.type" class="q-section">
          <div class="sec-head">
            <span class="sec-bar" />
            <strong class="sec-type">{{ sec.type }}</strong>
            <span class="sec-desc">（{{ sec.desc }}）</span>
          </div>

          <div v-for="(q, i) in sec.questions" :key="i" :id="`q-${gid(si, i)}`" class="q-item">
            <div class="q-head">
              <span class="q-no">{{ gid(si, i) + 1 }}</span>
              <div class="q-acts">
                <span class="act" :class="{ on: q.fav }" @click="q.fav = !q.fav">
                  <Heart :size="14" :fill="q.fav ? 'currentColor' : 'none'" /> 收藏
                </span>
                <span class="act" :class="{ on: q.marked }" @click="q.marked = !q.marked">
                  <Flag :size="14" :fill="q.marked ? 'currentColor' : 'none'" /> 标记
                </span>
              </div>
            </div>
            <div class="q-score">（{{ q.score }}分）</div>
            <p class="q-stem">{{ q.stem }}</p>
            <!-- 单选 / 判断 -->
            <el-radio-group v-if="q.kind === 'single' || q.kind === 'judge'" v-model="q.my" class="q-opts">
              <el-radio v-for="o in q.options" :key="o" :value="o" class="q-opt">{{ o }}</el-radio>
            </el-radio-group>
            <!-- 多选 -->
            <el-checkbox-group v-else-if="q.kind === 'multi'" v-model="q.mys" class="q-opts">
              <el-checkbox v-for="o in q.options" :key="o" :value="o" class="q-opt">{{ o }}</el-checkbox>
            </el-checkbox-group>
            <!-- 填空 -->
            <el-input v-else-if="q.kind === 'blank'" v-model="q.my" class="q-blank" placeholder="请输入答案" @paste.prevent />
            <!-- 简答 -->
            <el-input v-else v-model="q.my" type="textarea" :rows="4" placeholder="请输入答案（已禁用粘贴）" @paste.prevent />
          </div>
        </div>
      </div>

      <!-- ============ 右侧：进度 / 时间 / 答题卡 ============ -->
      <aside class="exam-side">
        <div class="side-block">
          <div class="side-label-row">
            <span class="side-label">进度</span>
            <span class="side-progress-num">{{ answeredCount }}/{{ totalCount }}</span>
          </div>
          <el-progress :percentage="progressPct" :stroke-width="8" :show-text="false" />
        </div>

        <div class="side-block center-block">
          <div class="side-label-row"><Clock3 :size="15" class="muted" /><span class="side-label">答题时间</span></div>
          <div class="time-left mono">{{ timeLeftText }}</div>
          <el-button type="primary" size="large" class="submit-btn" @click="submitExam(false)">交卷</el-button>
        </div>

        <div class="side-block">
          <div class="side-label-row">
            <LayoutGrid :size="15" style="color:var(--el-color-primary)" />
            <span class="side-label">答题卡</span>
            <span class="muted" style="font-size:12px">（试卷总分{{ totalScore }}分）</span>
          </div>
          <div class="legend">
            <span class="lg-item"><span class="lg done" />已做</span>
            <span class="lg-item"><span class="lg" />未做</span>
            <span class="lg-item"><span class="lg marked"><Flag :size="10" /></span>标记</span>
          </div>
          <div v-for="(sec, si) in sections" :key="sec.type" class="card-sec">
            <div class="card-sec-title">【{{ sec.type }}】（共 {{ sec.questions.length }} 题，一共{{ secScore(sec) }}分）</div>
            <div class="card-grid">
              <button
                v-for="(q, i) in sec.questions" :key="i"
                class="card-btn" :class="{ done: isAnswered(q), marked: q.marked }"
                @click="scrollToQuestion(gid(si, i))"
              >{{ gid(si, i) + 1 }}</button>
            </div>
          </div>
        </div>

        <div class="side-block anti-block">
          <ShieldAlert :size="14" />
          <span>防作弊监考中 · 全屏锁定 · 切屏 {{ violationCount }} / {{ maxViolations }}</span>
        </div>
      </aside>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Heart, Flag, Clock3, LayoutGrid, ShieldAlert } from 'lucide-vue-next'
import { takingExam } from '../../stores/exams'
import { maxViolations, violationCount, setForceSubmitHandler, stopProctoring } from '../../utils/proctor'

const emit = defineEmits<{ navigate: [id: string] }>()

const exam = takingExam

type Kind = 'single' | 'multi' | 'judge' | 'blank' | 'essay'
interface Question {
  kind: Kind; stem: string; options?: string[]; score: number
  my: string; mys: string[]; fav: boolean; marked: boolean
}
interface Section { type: string; desc: string; questions: Question[] }

const mk = (kind: Kind, stem: string, score: number, options?: string[]): Question =>
  ({ kind, stem, options, score, my: '', mys: [], fav: false, marked: false })

const sections = ref<Section[]>([
  {
    type: '单选题',
    desc: '每题备选答案中，只有一个为正确答案。错选、不选均不得分',
    questions: [
      mk('single', '爆款标题中「数字法」的典型应用是？', 2, ['震惊！不看后悔', '3 个方法让你的视频完播率翻倍', '你知道吗', '快来看看']),
      mk('single', '文案的「用户视角」指的是？', 2, ['多用专业术语', '从用户利益出发表达', '使用官方口吻', '罗列产品参数']),
      mk('single', '下列哪种开头更适合知识类短视频文案？', 2, ['悬念提问式', '无意义寒暄', '长篇自我介绍', '直接硬广'])
    ]
  },
  {
    type: '多选题',
    desc: '每题有两个或两个以上正确答案，多选、少选、错选均不得分',
    questions: [
      mk('multi', '下列哪些属于平台判定的有效互动行为？', 4, ['完整看完视频', '评论区互动', '转发给好友', '快速划过']),
      mk('multi', '一篇高转化带货文案通常包含哪些要素？', 4, ['痛点场景引入', '产品核心卖点', '明确的行动号召', '冗长的企业介绍'])
    ]
  },
  {
    type: '判断题',
    desc: '请判断题干表述是否正确',
    questions: [
      mk('judge', '「黄金三秒」原则指的是短视频开头需要快速抛出核心看点。', 2, ['正确', '错误']),
      mk('judge', '标题党虽然能提升点击量，但会伤害账号的长期权重。', 2, ['正确', '错误'])
    ]
  },
  {
    type: '填空题',
    desc: '请在横线处填入恰当内容，表述准确即可得分',
    questions: [
      mk('blank', '推拉摇移跟，是五种最基本的____方式。', 3),
      mk('blank', '平台完播率的计算公式是____。', 3)
    ]
  },
  {
    type: '简答题',
    desc: '结合作品案例作答，按要点给分',
    questions: [
      mk('essay', '结合本课程案例，阐述账号垂直定位对涨粉效率的影响机制。', 5),
      mk('essay', '请为「校园咖啡角」写一条 15 字以内的短视频标题，并说明理由。', 5)
    ]
  }
])

/** 每大题起始题号（全局连续编号） */
const secOffsets = computed(() => {
  const arr: number[] = []
  let n = 0
  for (const s of sections.value) { arr.push(n); n += s.questions.length }
  return arr
})
const gid = (si: number, i: number) => secOffsets.value[si] + i

function isAnswered(q: Question) {
  return q.kind === 'multi' ? q.mys.length > 0 : !!q.my.trim()
}

const totalCount = computed(() => sections.value.reduce((n, s) => n + s.questions.length, 0))
const answeredCount = computed(() => sections.value.reduce((n, s) => n + s.questions.filter(isAnswered).length, 0))
const totalScore = computed(() => sections.value.reduce((n, s) => n + secScore(s), 0))
const progressPct = computed(() => (totalCount.value ? Math.round((answeredCount.value / totalCount.value) * 100) : 0))
function secScore(sec: Section) { return sec.questions.reduce((n, q) => n + q.score, 0) }

/* ---------- 答题时间倒计时，时间到自动交卷 ---------- */
const leftSeconds = ref(20 * 60)
let timer: number | undefined
const timeLeftText = computed(() => {
  const s = Math.max(0, leftSeconds.value)
  const h = Math.floor(s / 3600), m = Math.floor((s % 3600) / 60), ss = s % 60
  return [h, m, ss].map(v => String(v).padStart(2, '0')).join(':')
})
function startTimer() {
  stopTimer()
  timer = window.setInterval(() => {
    leftSeconds.value -= 1
    if (leftSeconds.value <= 0) {
      ElMessage.warning('答题时间已到，系统自动交卷')
      doSubmit('auto')
    }
  }, 1000)
}
function stopTimer() { if (timer) { window.clearInterval(timer); timer = undefined } }

/* ---------- 答题卡跳转 ---------- */
function scrollToQuestion(i: number) {
  document.getElementById(`q-${i}`)?.scrollIntoView({ behavior: 'smooth', block: 'center' })
}

/* ---------- 交卷 ---------- */
let submitted = false
function submitExam(auto: boolean) {
  if (submitted) return
  if (auto) { doSubmit('auto'); return }
  ElMessageBox.confirm(`已作答 ${answeredCount.value} / ${totalCount.value} 题，确认交卷？`, '交卷确认', { type: 'warning' })
    .then(() => doSubmit('manual'))
    .catch(() => undefined)
}
function doSubmit(how: 'manual' | 'auto' | 'forced') {
  if (submitted) return
  submitted = true
  stopTimer()
  stopProctoring()
  if (exam.value) { exam.value.status = 'done'; exam.value.myScore = 20 }
  const scoreText = '客观题得分 20 / 24，简答题待教师批阅'
  if (how === 'forced') ElMessage.error(`因多次切屏被强制交卷，切屏记录（${violationCount.value} 次）已上报教师端。${scoreText}`)
  else ElMessage.success(how === 'auto' ? `时间到，系统已自动收卷。${scoreText}` : `交卷成功！${scoreText}`)
  emit('navigate', 's-exams')
}

onMounted(() => {
  setForceSubmitHandler(() => doSubmit('forced'))
  startTimer()
  window.scrollTo({ top: 0 })
})
onBeforeUnmount(() => {
  stopTimer()
  setForceSubmitHandler(null)
  if (!submitted) stopProctoring()
})
</script>

<style scoped>
.exam-page { max-width: 1280px; margin: 0 auto; padding: 8px 24px 48px; }
.exam-title { text-align: center; font-size: 19px; font-weight: 600; margin: 18px 0 4px; }
.exam-sub { text-align: center; font-size: 12.5px; margin-bottom: 22px; }

.exam-layout { display: grid; grid-template-columns: 1fr 300px; gap: 28px; align-items: start; }

/* 答题区 */
.exam-main { min-width: 0; }
.sec-head { display: flex; align-items: baseline; gap: 8px; padding: 6px 0 14px; border-bottom: 1px solid #f0f2f6; }
.sec-bar { width: 3px; height: 16px; background: var(--el-color-primary); border-radius: 2px; align-self: center; }
.sec-type { font-size: 16px; }
.sec-desc { font-size: 12.5px; color: var(--app-ink-3); }
.q-item { padding: 20px 0 22px; border-bottom: 1px solid #f0f2f6; }
.q-head { display: flex; align-items: center; justify-content: space-between; }
.q-no {
  display: inline-flex; align-items: center; justify-content: center;
  width: 24px; height: 28px; color: #fff; font-size: 13px; font-weight: 600;
  background: var(--el-color-primary);
  clip-path: polygon(0 0, 100% 0, 100% 100%, 50% 82%, 0 100%);
}
.q-acts { display: flex; gap: 22px; }
.act { display: inline-flex; align-items: center; gap: 5px; font-size: 13px; color: var(--app-ink-3); cursor: pointer; user-select: none; }
.act:hover { color: var(--el-color-primary); }
.act.on { color: #f56c6c; }
.act.on:last-child { color: #e6a23c; }
.q-score { font-size: 13px; color: var(--el-color-primary); margin: 6px 0 8px; }
.q-stem { font-size: 14.5px; font-weight: 600; line-height: 1.7; margin: 0 0 12px; }
.q-opts { display: flex; flex-direction: column; gap: 2px; align-items: flex-start; }
.q-opt { height: auto; padding: 7px 10px; border-radius: 6px; margin-right: 0 !important; justify-content: flex-start; }
.q-opt:hover { background: #f7f9ff; }
.q-blank { max-width: 380px; }

/* 右侧栏 */
.exam-side { position: sticky; top: 92px; display: flex; flex-direction: column; gap: 20px; }
.side-block { background: #fff; border: 1px solid #eef1f6; border-radius: 12px; padding: 18px 20px; }
.side-label-row { display: flex; align-items: center; gap: 6px; margin-bottom: 12px; }
.side-label { font-size: 14px; font-weight: 600; }
.side-progress-num { margin-left: auto; font-size: 13px; color: var(--el-color-primary); font-weight: 600; }
.center-block { text-align: center; }
.center-block .side-label-row { justify-content: center; }
.time-left { font-size: 34px; font-weight: 700; color: var(--el-color-primary); letter-spacing: 1px; margin: 6px 0 16px; }
.submit-btn { width: 100%; }
.legend { display: flex; align-items: center; gap: 16px; font-size: 12.5px; color: var(--app-ink-2); margin-bottom: 12px; }
.lg-item { display: inline-flex; align-items: center; gap: 5px; }
.lg { display: inline-flex; align-items: center; justify-content: center; width: 14px; height: 14px; border: 1px solid #c9d2e0; border-radius: 3px; }
.lg.done { background: var(--el-color-primary); border-color: var(--el-color-primary); }
.lg.marked { color: #f56c6c; border-color: #f56c6c; }
.card-sec { margin-top: 8px; }
.card-sec-title { font-size: 13px; font-weight: 600; margin-bottom: 8px; }
.card-grid { display: flex; flex-wrap: wrap; gap: 8px; }
.card-btn {
  width: 32px; height: 32px; border: 1px solid #c9d2e0; border-radius: 6px; background: #fff;
  font-size: 13px; cursor: pointer; color: var(--app-ink-2); transition: all .12s;
}
.card-btn:hover { border-color: var(--el-color-primary); color: var(--el-color-primary); }
.card-btn.done { background: var(--el-color-primary); border-color: var(--el-color-primary); color: #fff; }
.card-btn.marked { position: relative; box-shadow: inset 0 -3px 0 #f56c6c; }
.anti-block { display: flex; align-items: center; gap: 8px; font-size: 12.5px; color: #b45309; background: #fffbeb; border-color: #fde4b8; }

@media (max-width: 960px) {
  .exam-layout { grid-template-columns: 1fr; }
  .exam-side { position: static; }
}
</style>
