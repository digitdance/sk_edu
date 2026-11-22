<template>
  <div>
    <!-- 总评卡 -->
    <div class="panel total-panel">
      <div class="tp-left">
        <span class="big mono">91.5</span>
        <div class="tp-sub">
          <strong>本学期平均总评</strong>
          <el-tag size="small" type="success" effect="light">班级排名 6 / 40</el-tag>
        </div>
      </div>
      <div class="tp-weights">
        <div v-for="w in weights" :key="w.label" class="tw-item">
          <span class="tw-dot" :style="{ background: w.color }" />
          <span class="tw-label">{{ w.label }}（{{ w.pct }}%）</span>
          <strong class="mono">{{ w.score }}</strong>
        </div>
      </div>
    </div>

    <div class="grid-2">
      <!-- 课程成绩表 -->
      <div class="panel" style="margin:0">
        <div class="panel-title"><h3>各科成绩</h3><span class="extra">按教师设置的权重计算</span></div>
        <el-table :data="courses" border size="small">
          <el-table-column prop="name" label="课程" min-width="150" />
          <el-table-column prop="homework" label="作业" width="70" align="center" />
          <el-table-column prop="quiz" label="测验" width="70" align="center" />
          <el-table-column prop="exam" label="考试" width="70" align="center" />
          <el-table-column prop="offline" label="线下" width="70" align="center">
            <template #default="{ row }"><span :class="{ muted: !row.offline }">{{ row.offline ?? '—' }}</span></template>
          </el-table-column>
          <el-table-column prop="total" label="总评" width="80" align="center">
            <template #default="{ row }"><strong class="mono" style="color:var(--el-color-primary)">{{ row.total }}</strong></template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 趋势 -->
      <div class="panel" style="margin:0">
        <div class="panel-title"><h3>近 6 次测验成绩趋势</h3></div>
        <svg viewBox="0 0 300 170" class="line-chart" role="img" aria-label="成绩趋势">
          <g stroke="#eef1f7"><line v-for="y in 4" :key="y" x1="10" :y1="y * 40" x2="290" :y2="y * 40" /></g>
          <polyline :points="linePoints" fill="none" stroke="#2f6bff" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" />
          <circle v-for="(p, i) in lineDots" :key="i" :cx="p[0]" :cy="p[1]" r="3.5" fill="#fff" stroke="#2f6bff" stroke-width="2" />
        </svg>
        <el-alert type="success" :closable="false" show-icon title="趋势向好：近 3 次测验均高于班级平均分。" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const weights = [
  { label: '作业', pct: 30, score: 88, color: '#2f6bff' },
  { label: '章节测验', pct: 20, score: 84, color: '#2fbf71' },
  { label: '期末考试', pct: 40, score: 82, color: '#e6a23c' },
  { label: '线下成绩', pct: 10, score: 80, color: '#7a3ff2' }
]

const courses = [
  { name: '短视频制作与运营', homework: 88, quiz: 84, exam: 82, offline: 80, total: 91.5 },
  { name: '新媒体文案写作', homework: 92, quiz: 88, exam: 86, offline: null, total: 88.6 }
]

const trend = [76, 82, 78, 88, 90, 92]
const lineDots = computed(() => trend.map((v, i) => [20 + i * 52, 150 - (v - 60) * 4] as [number, number]))
const linePoints = computed(() => lineDots.value.map(p => p.join(',')).join(' '))
</script>

<style scoped>
.total-panel { display: flex; align-items: center; gap: 40px; flex-wrap: wrap; }
.tp-left { display: flex; align-items: center; gap: 14px; }
.big { font-size: 44px; font-weight: 800; color: var(--el-color-primary); }
.tp-sub { display: flex; flex-direction: column; gap: 6px; font-size: 13px; }
.tp-weights { display: flex; gap: 26px; flex-wrap: wrap; }
.tw-item { display: flex; align-items: center; gap: 8px; font-size: 13px; }
.tw-dot { width: 10px; height: 10px; border-radius: 3px; }
.tw-label { color: var(--app-ink-2); }
.line-chart { width: 100%; height: 170px; margin-bottom: 10px; }
</style>
