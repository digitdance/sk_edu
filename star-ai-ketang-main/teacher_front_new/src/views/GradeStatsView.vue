<template>
  <div>
    <!-- 权重设置 -->
    <div class="panel">
      <div class="panel-title">
        <h3>成绩权重设置</h3>
        <el-button size="small" type="primary" @click="saveWeights">保存权重</el-button>
      </div>
      <div class="weight-bar">
        <div v-for="w in weightList" :key="w.key" :style="{ width: w.value + '%', background: w.color }">
          {{ w.label }} {{ w.value }}%
        </div>
      </div>
      <div class="weight-inputs">
        <div v-for="w in weightList" :key="w.key" class="w-item">
          <span class="w-dot" :style="{ background: w.color }" />
          <span class="w-label">{{ w.label }}</span>
          <el-input-number v-model="w.value" :min="0" :max="100" size="small" @change="normalize" />
          <span class="muted">%</span>
        </div>
      </div>
      <div class="vis-row">
        <span class="r-label">学生可见：</span>
        <el-checkbox v-model="visible.total">总评成绩</el-checkbox>
        <el-checkbox v-model="visible.rank">班级排名</el-checkbox>
        <el-checkbox v-model="visible.detail">各项明细</el-checkbox>
        <span style="flex:1" />
        <el-button :icon="Download" @click="exportScores">导出成绩表</el-button>
        <el-button type="primary" plain @click="offlineDialog = true">录入线下成绩</el-button>
      </div>
    </div>

    <!-- 概览统计 -->
    <div class="grid-4" style="margin-bottom:16px">
      <div class="panel stat-mini" style="margin:0"><span class="num mono">118</span><span class="lbl">修读学生</span></div>
      <div class="panel stat-mini" style="margin:0"><span class="num mono" style="color:#2fbf71">5</span><span class="lbl">成绩优秀（≥90）</span></div>
      <div class="panel stat-mini" style="margin:0"><span class="num mono" style="color:#f56c6c">12</span><span class="lbl">预警（&lt;60）</span></div>
      <div class="panel stat-mini" style="margin:0"><span class="num mono" style="color:#2f6bff">82.4</span><span class="lbl">班级平均分</span></div>
    </div>

    <!-- 图表区 -->
    <div class="grid-3" style="margin-bottom:16px">
      <div class="panel" style="margin:0">
        <div class="panel-title"><h3>分数段分布</h3></div>
        <div class="bars">
          <div v-for="b in scoreDist" :key="b.label" class="bar-col">
            <div class="bar" :style="{ height: b.pct + '%' }" />
            <span class="bar-lbl">{{ b.label }}</span>
          </div>
        </div>
      </div>
      <div class="panel" style="margin:0">
        <div class="panel-title"><h3>总评构成达成度</h3></div>
        <div class="donut-wrap">
          <div class="donut" :style="{ background: donutStyle }">
            <div class="hole"><span class="big mono">86%</span><span class="muted">总评合格率</span></div>
          </div>
          <div class="legend">
            <span v-for="l in donutLegend" :key="l.label"><i class="dot" :style="{ background: l.color }" />{{ l.label }} {{ l.value }}</span>
          </div>
        </div>
      </div>
      <div class="panel" style="margin:0">
        <div class="panel-title"><h3>近 6 次测验平均分趋势</h3></div>
        <svg viewBox="0 0 300 170" class="line-chart" role="img" aria-label="平均分趋势">
          <g stroke="#eef1f7"><line v-for="y in 4" :key="y" x1="10" :y1="y * 40" x2="290" :y2="y * 40" /></g>
          <polyline :points="linePoints" fill="none" stroke="#2f6bff" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" />
          <circle v-for="(p, i) in lineDots" :key="i" :cx="p[0]" :cy="p[1]" r="3.5" fill="#fff" stroke="#2f6bff" stroke-width="2" />
        </svg>
      </div>
    </div>

    <!-- 排名表 -->
    <div class="panel">
      <div class="panel-title">
        <h3>学生成绩排名</h3>
        <el-input v-model="keyword" placeholder="搜索姓名 / 学号" :prefix-icon="SearchIcon" style="width:200px" clearable size="small" />
      </div>
      <el-table :data="filteredRanking" border stripe :default-sort="{ prop: 'total', order: 'descending' }">
        <el-table-column label="排名" width="80" align="center">
          <template #default="{ $index }">
            <span class="rank-badge" :class="{ top: $index < 3 }">{{ $index + 1 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="姓名" width="110" />
        <el-table-column prop="homework" label="作业 30%" width="110" sortable align="center" />
        <el-table-column prop="quiz" label="测验 20%" width="110" sortable align="center" />
        <el-table-column prop="exam" label="期末 40%" width="110" sortable align="center" />
        <el-table-column prop="offline" label="线下 10%" width="110" sortable align="center">
          <template #default="{ row }">
            <span :class="{ muted: row.offline === null }">{{ row.offline ?? '未录入' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="total" label="总评" width="110" sortable align="center">
          <template #default="{ row }">
            <strong class="mono" :style="{ color: row.total >= 90 ? '#2fbf71' : row.total < 60 ? '#f56c6c' : '#1f2733' }">{{ row.total }}</strong>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="90" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 线下成绩录入 -->
    <el-dialog v-model="offlineDialog" title="录入线下成绩（占总评 10%）" width="560px">
      <el-alert type="info" :closable="false" show-icon style="margin-bottom:14px"
        title="线下成绩包括课堂表现、实训作品答辩等，录入后立即重新计算总评。" />
      <el-table :data="ranking" border size="small" max-height="360">
        <el-table-column prop="name" label="姓名" width="110" />
        <el-table-column prop="no" label="学号" width="120" />
        <el-table-column label="线下成绩">
          <template #default="{ row }">
            <el-input-number v-model="row.offline" :min="0" :max="100" size="small" @change="recalc(row)" />
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button @click="offlineDialog = false">取消</el-button>
        <el-button type="primary" @click="saveOffline">保存并重新计算</el-button>
      </template>
    </el-dialog>

    <!-- 学生成绩详情 -->
    <el-drawer v-model="detailDrawer" :title="`成绩详情 · ${detailStudent?.name || ''}`" size="440px">
      <template v-if="detailStudent">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="作业（30%）">{{ detailStudent.homework }} 分 · 8/8 次已交</el-descriptions-item>
          <el-descriptions-item label="章节测验（20%）">{{ detailStudent.quiz }} 分 · 5 次测验</el-descriptions-item>
          <el-descriptions-item label="期末考试（40%）">{{ detailStudent.exam }} 分</el-descriptions-item>
          <el-descriptions-item label="线下成绩（10%）">{{ detailStudent.offline ?? '未录入' }}</el-descriptions-item>
          <el-descriptions-item label="总评"><strong style="font-size:17px">{{ detailStudent.total }} 分</strong></el-descriptions-item>
        </el-descriptions>
        <el-alert style="margin-top:14px" :closable="false" show-icon
          :type="detailStudent.total >= 60 ? 'success' : 'error'"
          :title="detailStudent.total >= 60 ? '学习状态良好，章节任务点完成率 92%。' : '存在学习预警：第 3 章任务点未完成，建议教师介入辅导。'" />
      </template>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { downloadCsv } from '../utils/download'
import { ElMessage } from 'element-plus'
import { Search as SearchIcon, Download } from 'lucide-vue-next'

interface StudentScore {
  id: number; name: string; no: string; homework: number; quiz: number;
  exam: number; offline: number | null; total: number
}

const keyword = ref('')
const offlineDialog = ref(false)
const detailDrawer = ref(false)
const detailStudent = ref<StudentScore | null>(null)
const visible = ref({ total: true, rank: true, detail: false })

const weightList = ref([
  { key: 'homework', label: '作业', value: 30, color: '#2f6bff' },
  { key: 'quiz', label: '章节测验', value: 20, color: '#2fbf71' },
  { key: 'exam', label: '期末考试', value: 40, color: '#e6a23c' },
  { key: 'offline', label: '线下成绩', value: 10, color: '#7a3ff2' }
])

const scoreDist = [
  { label: '<60', pct: 20 }, { label: '60-70', pct: 32 }, { label: '70-80', pct: 58 },
  { label: '80-90', pct: 92 }, { label: '90+', pct: 44 }
]

const donutLegend = [
  { label: '优秀', value: '12%', color: '#2fbf71' },
  { label: '良好', value: '38%', color: '#2f6bff' },
  { label: '合格', value: '36%', color: '#e6a23c' },
  { label: '不合格', value: '14%', color: '#f56c6c' }
]
const donutStyle = 'conic-gradient(#2fbf71 0 12%, #2f6bff 12% 50%, #e6a23c 50% 86%, #f56c6c 86% 100%)'

const trendData = [78, 80, 76, 83, 85, 82.4]
const lineDots = computed(() => trendData.map((v, i) => [20 + i * 52, 150 - (v - 60) * 4] as [number, number]))
const linePoints = computed(() => lineDots.value.map(p => p.join(',')).join(' '))

function calcTotal(s: Omit<StudentScore, 'total'>) {
  const w = Object.fromEntries(weightList.value.map(x => [x.key, x.value]))
  return Math.round(
    (s.homework * w.homework + s.quiz * w.quiz + s.exam * w.exam + (s.offline ?? 0) * w.offline) / 100 * 10
  ) / 10
}

const base: Omit<StudentScore, 'total'>[] = [
  { id: 1, name: '李梦琪', no: '20240118', homework: 96, quiz: 92, exam: 94, offline: 90 },
  { id: 2, name: '陈思雨', no: '20240116', homework: 90, quiz: 88, exam: 86, offline: 85 },
  { id: 3, name: '刘洋', no: '20240115', homework: 88, quiz: 84, exam: 82, offline: 80 },
  { id: 4, name: '王子涵', no: '20240117', homework: 82, quiz: 78, exam: 80, offline: 78 },
  { id: 5, name: '赵可欣', no: '20240120', homework: 76, quiz: 72, exam: 70, offline: null },
  { id: 6, name: '张浩然', no: '20240119', homework: 58, quiz: 62, exam: 55, offline: 60 },
  { id: 7, name: '孙艺涵', no: '20240121', homework: 49, quiz: 55, exam: 52, offline: null }
]
const ranking = ref<StudentScore[]>(base.map(s => ({ ...s, total: calcTotal(s) })))

const filteredRanking = computed(() =>
  [...ranking.value]
    .filter(s => s.name.includes(keyword.value) || s.no.includes(keyword.value))
    .sort((a, b) => b.total - a.total)
)

function normalize() {
  const sum = weightList.value.reduce((s, w) => s + w.value, 0)
  if (sum !== 100) ElMessage.warning(`当前权重合计 ${sum}%，建议调整为 100%`)
}
function saveWeights() {
  ranking.value.forEach(s => (s.total = calcTotal(s)))
  ElMessage.success('权重已保存，全部学生总评已重新计算')
}
function recalc(row: StudentScore) { row.total = calcTotal(row) }
function saveOffline() {
  offlineDialog.value = false
  ElMessage.success('线下成绩已保存，总评排名已更新')
}
function exportScores() {
  downloadCsv('成绩表.csv',
    ['姓名', '学号', '作业', '测验', '考试', '线下', '总评'],
    ranking.value.map(s => [s.name, s.no, s.homework, s.quiz, s.exam, s.offline ?? '—', s.total]))
  ElMessage.success('成绩表已导出（含各项明细与总评）')
}
function openDetail(row: StudentScore) {
  detailStudent.value = row
  detailDrawer.value = true
}
</script>

<style scoped>
.weight-inputs { display: flex; gap: 22px; margin-top: 14px; flex-wrap: wrap; }
.w-item { display: flex; align-items: center; gap: 8px; font-size: 13px; }
.w-dot { width: 10px; height: 10px; border-radius: 3px; }
.w-label { font-weight: 600; }
.vis-row { display: flex; align-items: center; gap: 16px; margin-top: 16px; padding-top: 14px; border-top: 1px solid #f2f4f9; flex-wrap: wrap; }
.r-label { font-size: 13px; font-weight: 600; }
.line-chart { width: 100%; height: 180px; }
.rank-badge {
  display: inline-flex; width: 24px; height: 24px; border-radius: 50%; align-items: center; justify-content: center;
  background: #eef1f7; color: var(--app-ink-2); font-size: 12px; font-weight: 700;
}
.rank-badge.top { background: #fff1d6; color: #b97a00; }
</style>
