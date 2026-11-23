<template>
  <div class="ai-scope">
    <div class="la">
      <div class="page-head">
        <div>
          <h2>学情分析与建议</h2>
          <div class="sub">按班级 / 学生筛选 · 成绩趋势 · AI 批改校准 · 错题与薄弱知识点 · 分层教学建议</div>
        </div>
        <div class="filters">
          <select v-model="cls" class="select"><option v-for="c in classOptions" :key="c.id" :value="c.id">{{ c.className }}</option></select>
          <select v-model="stu" class="select"><option>全班</option><option v-for="s in students" :key="s">{{ s }}</option></select>
          <select v-model="period" class="select"><option v-for="p in periods" :key="p">{{ p }}</option></select>
          <select v-model="hw" class="select"><option v-for="h in homeworkTypes" :key="h">{{ h }}</option></select>
          <button class="btn" @click="resetFilters">重置</button>
        </div>
      </div>

      <div class="kpis">
        <div class="card card-pad"><div class="muted">学生数</div><div class="kpi-v">{{ overview.memberCount }}</div><div class="sub">Class members</div></div>
        <div class="card card-pad"><div class="muted">作业提交率</div><div class="kpi-v">{{ overview.totalActivities }}</div><div class="sub">Recorded learning activities</div></div>
        <div class="card card-pad"><div class="muted">平均分</div><div class="kpi-v">{{ Number(overview.avgScoreRate || 0).toFixed(1) }}%</div><div class="sub">Class average score rate</div></div>
        <div class="card card-pad"><div class="muted">主观题得分率</div><div class="kpi-v">{{ overview.avgDurationText }}</div><div class="sub">Average study duration</div></div>
      </div>

      <div class="cols">
        <aside class="card list">
          <button v-for="it in analysisItems" :key="it.key" class="li" :class="{ on: item === it.key }" @click="item = it.key">
            <span class="ic"><AiIcon :name="it.icon" :size="16" /></span>
            <span><b>{{ it.title }}</b><br /><small class="muted">{{ it.desc }}</small></span>
          </button>
        </aside>

        <section class="card detail">
          <template v-if="item === 'grading'">
            <div class="viz-h">
              <div>
                <h3 class="h-title">{{ trend.name }}</h3>
                <div class="legend">
                  <span><i class="dot" style="background:#2f6bff"></i>{{ trend.names[0] }}</span>
                  <span><i class="dot" style="background:#16a6c9"></i>{{ trend.names[1] }}</span>
                </div>
              </div>
              <div class="seg mini">
                <button :class="{ on: metric === 'score' }" @click="metric = 'score'">分数</button>
                <button :class="{ on: metric === 'rate' }" @click="metric = 'rate'">得分率</button>
                <button :class="{ on: metric === 'submit' }" @click="metric = 'submit'">提交率</button>
              </div>
            </div>
            <AiLineChart :labels="trendLabels" :series="trend.data" :min="trend.min" :max="trend.max" :unit="trend.unit" :label="trend.name" />
            <h3 class="h-title" style="margin-top:14px">AI 批改结果复核 <span class="muted" style="font-weight:500">（作业 3 · 账号定位与流量诊断简答）</span></h3>
            <div class="seg" style="margin:8px 0 12px">
              <button v-for="m in modes" :key="m.id" :class="{ on: mode === m.id }" @click="applyMode(m.id)">{{ m.name }}</button>
            </div>
            <table class="tbl">
              <thead><tr><th>学生</th><th>答案摘要</th><th>AI 初评</th><th>AI 评语</th><th>教师校准</th><th>操作</th></tr></thead>
              <tbody>
                <tr v-for="g in grading" :key="g.name">
                  <td>{{ g.name }}</td>
                  <td class="muted">{{ g.excerpt }}</td>
                  <td>
                    <b :style="{ color: g.score >= 8 ? 'var(--ok)' : g.score >= 6 ? 'var(--warn)' : 'var(--danger)' }">{{ g.score }}</b> / {{ g.full }}
                    <span class="chip" :class="g.score >= 8 ? 'ok' : g.score >= 6 ? 'warn' : 'danger'" style="margin-left:6px">{{ g.tag }}</span>
                  </td>
                  <td class="muted">{{ g.comment }}</td>
                  <td>
                    <div class="sc-in">
                      <button class="btn-sm" @click="g.teacher > 0 && g.teacher--">−</button>
                      <input class="input" v-model.number="g.teacher" />
                      <button class="btn-sm" @click="g.teacher < g.full && g.teacher++">＋</button>
                    </div>
                  </td>
                  <td>
                    <span v-if="g.done" class="chip ok">已确认</span>
                    <button v-else class="lnk" @click="g.done = true">确认</button>
                  </td>
                </tr>
              </tbody>
            </table>
            <div class="export">
              <span class="muted">{{ modeName }}模式 · 已确认 {{ confirmedCount }}/{{ grading.length }}</span>
              <div>
                <button class="btn" @click="confirmAll">全部确认</button>
                <button class="btn-primary" @click="syncGrade">同步到成绩册</button>
              </div>
            </div>
          </template>

          <template v-else-if="item === 'errors'">
            <div class="viz-h"><h3 class="h-title">高频错题 TOP 5</h3><span class="muted">作业 3 与第 3 章测验合并统计</span></div>
            <div class="split">
              <div class="list" style="border:0">
                <div v-for="e in topErrors" :key="e.q" class="li" style="cursor:default">
                  <b style="font-weight:500">{{ e.q }}</b>
                  <span>
                    <span class="chip gray" style="margin-right:6px">正确率 {{ e.rate }}%</span>
                    <span class="chip danger">{{ e.count }} 人错</span>
                  </span>
                </div>
              </div>
              <div>
                <AiDonutChart :items="errorTypes" total-label="错误" :total="132" />
                <div class="note warn"><AiIcon name="alert" :size="15" /><span>「算法与流量池」章节客观题正确率仅 61%，建议安排 1 课时专题讲评。</span></div>
              </div>
            </div>
          </template>

          <template v-else-if="item === 'weak'">
            <div class="viz-h"><h3 class="h-title">薄弱知识点掌握度</h3><span class="chip danger">{{ weakLow.length }} 项低于 60%</span></div>
            <div v-for="w in weakPoints" :key="w.point" class="weak">
              <div class="row">
                <span>{{ w.point }}</span>
                <b :style="{ color: w.mastery < 60 ? 'var(--danger)' : 'inherit' }">
                  {{ w.mastery }}%
                  <small class="muted" style="font-weight:400">（{{ w.delta > 0 ? '+' : '' }}{{ w.delta }}）</small>
                </b>
              </div>
              <div class="bar big" :class="{ low: w.mastery < 60 }"><i :style="{ width: w.mastery + '%' }"></i></div>
            </div>
            <div class="note danger"><AiIcon name="alert" :size="15" /><span>{{ weakLow.map((x) => x.point).join('、') }} 掌握度不足 60%，建议生成专项练习。</span></div>
            <div class="export">
              <span></span>
              <button class="btn-primary weak-f" @click="genPractice"><AiIcon name="task" :size="15" /> 一键生成专项练习</button>
            </div>
          </template>

          <template v-else-if="item === 'advice'">
            <div class="viz-h"><h3 class="h-title">分层教学建议</h3><span class="muted">按最近三次作业综合表现分层</span></div>
            <div class="adv">
              <div v-for="(a, i) in adviceList" :key="a.group" class="ad">
                <b>{{ a.group }} <span class="chip" :class="['danger', 'warn', 'gray', 'ok'][i]">{{ a.who }}</span></b>
                <p>{{ a.text }}</p>
                <div>
                  <span v-for="t in a.tags" :key="t" class="chip gray" style="margin-right:6px">{{ t }}</span>
                  <button class="lnk" @click="push(a.group)">推送建议 ›</button>
                </div>
              </div>
            </div>
            <div class="note ok"><AiIcon name="check" :size="15" /><span>分层建议将随下次作业布置自动附带到对应学生的任务说明中。</span></div>
          </template>

          <template v-else>
            <div class="viz-h"><h3 class="h-title">班级学情趋势报告</h3><span class="chip ok">AI 自动生成</span></div>
            <div class="rep">
              <b>本期结论（{{ period }}）</b>
              <ul>
                <li v-for="b in reportBullets" :key="b">{{ b }}</li>
              </ul>
            </div>
            <AiLineChart :labels="trendLabels" :series="trendSets.score.data" :min="trendSets.score.min" :max="trendSets.score.max" :label="trendSets.score.name" />
            <div class="export">
              <span class="muted">报告可随家长会 / 教学检查导出存档</span>
              <button class="btn-primary" @click="exportReport"><AiIcon name="download" :size="15" /> 导出学情报告 PDF</button>
            </div>
          </template>
        </section>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { apiRequest } from '../api/client'
import { ElMessage } from 'element-plus'
import AiIcon from '../components/AiIcon.vue'
import AiLineChart from '../components/AiLineChart.vue'
import AiDonutChart from '../components/AiDonutChart.vue'
import { aiHandoff } from '../stores/aiHandoff'
import {
  adviceList,
  analysisItems,
  errorTypes,
  gradingRows,
  homeworkTypes,
  periods,
  reportBullets,
  students,
  topErrors,
  trendLabels,
  trendSets,
  weakPoints,
} from '../data/aiMock'

const emit = defineEmits<{ navigate: [view: string] }>()

type Metric = 'score' | 'rate' | 'submit'

const cls = ref('')
const classOptions = ref<Array<{ id: string; className: string }>>([])
const overview = ref({ memberCount: 0, totalActivities: 0, avgScoreRate: 0, avgDurationText: '-' })
const stu = ref('全班')
const period = ref(periods[0])
const hw = ref(homeworkTypes[0])
const item = ref('grading')
const metric = ref<Metric>('score')
const mode = ref('standard')
const grading = reactive(
  gradingRows.map((r) => ({ ...r, teacher: r.score, done: false })),
)

const modes = [
  { id: 'loose', name: '宽松', delta: 1 },
  { id: 'standard', name: '标准', delta: 0 },
  { id: 'strict', name: '严格', delta: -1 },
]
const modeName = computed(() => modes.find((m) => m.id === mode.value)?.name ?? '')
const trend = computed(() => trendSets[metric.value])
watch(cls, async (id) => {
  if (!id) return
  try { overview.value = await apiRequest<typeof overview.value>(`/api/analytics/class/${id}/overview`) }
  catch (error) { ElMessage.error(error instanceof Error ? error.message : 'Class analytics could not be loaded') }
})
const weakLow = computed(() => weakPoints.filter((w) => w.mastery < 60))
const confirmedCount = computed(() => grading.filter((g) => g.done).length)

onMounted(async () => {
  try {
    const page = await apiRequest<{ list: Array<{ id: string; className: string }> }>('/api/classes/list?pageNum=1&pageSize=100')
    classOptions.value = page.list ?? []
    if (!cls.value) cls.value = classOptions.value[0]?.id ?? ''
    if (cls.value) overview.value = await apiRequest<typeof overview.value>(`/api/analytics/class/${cls.value}/overview`)
  } catch (error) { ElMessage.error(error instanceof Error ? error.message : 'Class analytics could not be loaded') }
})
function applyMode(id: string) {
  mode.value = id
  const delta = modes.find((m) => m.id === id)?.delta ?? 0
  grading.forEach((g, i) => {
    g.teacher = Math.max(0, Math.min(g.full, gradingRows[i].score + delta))
  })
}
function resetFilters() {
  cls.value = classOptions.value[0]?.id ?? ''
  stu.value = '全班'
  period.value = periods[0]
  hw.value = homeworkTypes[0]
  ElMessage.success('筛选已重置')
}
function confirmAll() {
  grading.forEach((g) => (g.done = true))
  ElMessage.success('已全部确认')
}
function syncGrade() {
  ElMessage.success(`已按${modeName.value}模式将 ${grading.length} 条成绩同步到成绩册`)
}
function push(g: string) {
  ElMessage.success(`已向「${g}」学生推送学习建议`)
}
function exportReport() {
  ElMessage.success('学情报告 PDF 已生成，可在下载中心查看')
}
function genPractice() {
  aiHandoff.outline = '针对薄弱知识点生成专项练习：' + weakLow.value.map((x) => x.point).join('、')
  aiHandoff.type = 'exercise'
  ElMessage.success('已生成专项练习需求，正在跳转到智能备课…')
  emit('navigate', 'ai-prep')
}
</script>
