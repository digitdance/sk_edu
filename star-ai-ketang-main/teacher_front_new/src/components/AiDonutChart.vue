<template>
  <div class="dn">
    <svg viewBox="0 0 120 120" class="dn-svg" role="img" aria-label="占比环形图">
      <circle cx="60" cy="60" r="42" fill="none" stroke="var(--surface-2)" stroke-width="16" />
      <circle v-for="s in segs" :key="s.name" cx="60" cy="60" r="42" fill="none" :stroke="s.color" stroke-width="16"
        :stroke-dasharray="`${s.len} ${C - s.len}`" :stroke-dashoffset="-s.off" transform="rotate(-90 60 60)" />
      <text x="60" y="58" text-anchor="middle" class="dn-n">{{ total }}</text>
      <text x="60" y="74" text-anchor="middle" class="dn-l">{{ totalLabel }}</text>
    </svg>
    <ul class="dn-leg">
      <li v-for="s in segs" :key="s.name">
        <i :style="{ background: s.color }"></i>
        <span>{{ s.name }}</span>
        <b>{{ s.value }}%</b>
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const COLORS = ['#2f6bff', '#16a6c9', '#7a8cff', '#f0a43a', '#9aa6c0']
const props = withDefaults(defineProps<{
  items: { name: string; value: number }[]; totalLabel?: string; total?: number | string
}>(), { totalLabel: '错题', total: 0 })

const C = 2 * Math.PI * 42
const segs = computed(() => {
  const sum = props.items.reduce((a, b) => a + b.value, 0)
  let off = 0
  return props.items.map((it, i) => {
    const len = (it.value / sum) * C
    const seg = { name: it.name, value: it.value, color: COLORS[i % COLORS.length], len: Math.max(len - 2, 0), off }
    off += len
    return seg
  })
})
</script>

<style scoped>
.dn { display: flex; align-items: center; gap: 22px; flex-wrap: wrap; }
.dn-svg { width: 132px; height: 132px; flex: none; }
.dn-n { font-size: 20px; font-weight: 700; fill: var(--fg); font-variant-numeric: tabular-nums; }
.dn-l { font-size: 10px; fill: var(--fg-3); }
.dn-leg { list-style: none; margin: 0; padding: 0; flex: 1; min-width: 170px; display: grid; gap: 8px; }
.dn-leg li { display: flex; align-items: center; gap: 8px; font-size: 13px; }
.dn-leg i { width: 9px; height: 9px; border-radius: 3px; flex: none; }
.dn-leg span { flex: 1; color: var(--fg-2); }
.dn-leg b { font-variant-numeric: tabular-nums; }
</style>
