<template>
  <svg class="lc" :viewBox="`0 0 ${W} ${H}`" role="img" :aria-label="label" preserveAspectRatio="xMidYMid meet">
    <defs>
      <linearGradient :id="gid" x1="0" y1="0" x2="0" y2="1">
        <stop offset="0%" stop-color="var(--brand)" stop-opacity=".28" />
        <stop offset="100%" stop-color="var(--brand)" stop-opacity="0" />
      </linearGradient>
    </defs>
    <g v-for="t in ticks" :key="t.v">
      <line :x1="L" :x2="W - R" :y1="t.y" :y2="t.y" stroke="var(--line)" stroke-dasharray="3 4" />
      <text :x="L - 8" :y="t.y + 4" text-anchor="end" class="lc-t">{{ t.v }}</text>
    </g>
    <text v-for="(l, i) in labels" :key="l" :x="px(i)" :y="H - 8" text-anchor="middle" class="lc-t">{{ l }}</text>
    <path :d="area" :fill="`url(#${gid})`" />
    <path :d="line(series[1])" fill="none" stroke="var(--fg-3)" stroke-width="2" stroke-dasharray="5 4" stroke-linejoin="round" />
    <path :d="line(series[0])" fill="none" stroke="var(--brand)" stroke-width="2.6" stroke-linejoin="round" stroke-linecap="round" />
    <circle v-for="(v, i) in series[0]" :key="i" :cx="px(i)" :cy="py(v)" :r="i === series[0].length - 1 ? 5 : 3.2"
      fill="var(--surface)" stroke="var(--brand)" stroke-width="2" />
    <g>
      <rect :x="px(series[0].length - 1) - 62" :y="py(series[0][series[0].length - 1]) - 34" width="54" height="22" rx="6" fill="var(--brand)" />
      <text :x="px(series[0].length - 1) - 35" :y="py(series[0][series[0].length - 1]) - 19" text-anchor="middle" class="lc-tip">{{ series[0][series[0].length - 1] }}{{ unit }}</text>
    </g>
  </svg>
</template>

<script setup lang="ts">
import { computed } from 'vue'

let uid = 0
const props = withDefaults(defineProps<{
  labels: string[]; series: number[][]; min?: number; max?: number; unit?: string; label?: string
}>(), { min: 0, max: 100, unit: '', label: '趋势图' })

const W = 640, H = 280, L = 40, R = 16, T = 30, B = 30
const gid = 'lcg' + (++uid)

function px(i: number) { const n = props.labels.length - 1; return L + ((W - L - R) * i) / n }
function py(v: number) { return T + (H - T - B) * (1 - (v - props.min) / (props.max - props.min)) }
function line(s: number[]) { return s.map((v, i) => (i ? 'L' : 'M') + px(i) + ',' + py(v)).join(' ') }

const ticks = computed(() => {
  const out: { v: number; y: number }[] = []
  const n = 5
  for (let i = 0; i <= n; i++) {
    const v = Math.round(props.min + ((props.max - props.min) * i) / n)
    out.push({ v, y: py(v) })
  }
  return out
})
const area = computed(() => {
  const s = props.series[0]
  const base = py(props.min)
  return line(s) + ' L' + px(s.length - 1) + ',' + base + ' L' + px(0) + ',' + base + ' Z'
})
</script>

<style scoped>
.lc { width: 100%; height: auto; display: block; }
.lc-t { font-size: 11px; fill: var(--fg-3); font-variant-numeric: tabular-nums; }
.lc-tip { font-size: 12px; fill: #fff; font-weight: 600; font-variant-numeric: tabular-nums; }
</style>
