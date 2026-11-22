<template>
  <svg class="ico" :width="size" :height="size" viewBox="0 0 24 24" fill="none" stroke="currentColor"
    stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
    <path v-for="(d, i) in paths" :key="i" :d="d" />
  </svg>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const MAP: Record<string, string> = {
  book: 'M4 19.5A2.5 2.5 0 0 1 6.5 17H20V3H6.5A2.5 2.5 0 0 0 4 5.5z|M20 17v4H6.5A2.5 2.5 0 0 1 4 18.5',
  sparkles: 'M12 3l1.8 5.2L19 10l-5.2 1.8L12 17l-1.8-5.2L5 10l5.2-1.8z|M19 15l.7 2 2 .7-2 .7-.7 2-.7-2-2-.7 2-.7z',
  upload: 'M12 15V4|M7 9l5-5 5 5|M4 20h16',
  download: 'M12 4v11|M7 10l5 5 5-5|M4 20h16',
  search: 'M11 4a7 7 0 1 0 0 14a7 7 0 0 0 0-14z|M20 20l-4-4',
  file: 'M6 3h8l5 5v13H6z|M14 3v5h5',
  cap: 'M2 9l10-5 10 5-10 5z|M6 11v5c3 2.5 9 2.5 12 0v-5',
  slides: 'M3 4h18v12H3z|M8 20h8|M12 16v4',
  task: 'M4 5h16v14H4z|M8 10l3 2-3 2|M13 15h3',
  refresh: 'M20 11a8 8 0 0 0-14-4L4 9|M4 4v5h5|M4 13a8 8 0 0 0 14 4l2-2|M20 20v-5h-5',
  cloud: 'M7 18a4 4 0 0 1-.5-8A6 6 0 0 1 18 9a4.5 4.5 0 0 1-.5 9z',
  check: 'M5 12l5 5 9-10',
  chart: 'M4 20V4|M4 20h16|M8 16v-5|M12 16V8|M16 16v-8',
  pen: 'M4 20l4-1 11-11-3-3L5 16z',
  alert: 'M12 4l9 16H3z|M12 10v4|M12 17v.5',
  target: 'M12 3a9 9 0 1 0 0 18a9 9 0 0 0 0-18z|M12 8a4 4 0 1 0 0 8a4 4 0 0 0 0-8z',
  bulb: 'M9 18h6|M10 21h4|M12 3a6 6 0 0 0-4 10c1 1 1 2 1 3h6c0-1 0-2 1-3a6 6 0 0 0-4-10z',
  message: 'M4 5h16v11H9l-5 4z',
  shield: 'M12 3l8 3v6c0 5-3.5 8-8 9-4.5-1-8-4-8-9V6z',
  link: 'M10 14a4 4 0 0 0 5.7 0l3-3a4 4 0 0 0-5.7-5.7l-1 1|M14 10a4 4 0 0 0-5.7 0l-3 3a4 4 0 0 0 5.7 5.7l1-1',
  video: 'M3 6h13v12H3z|M16 10l5-3v10l-5-3',
  db: 'M5 5h14v4H5z|M5 11h14v4H5z|M5 17h14v3H5z',
  x: 'M6 6l12 12|M18 6L6 18',
  plus: 'M12 5v14|M5 12h14',
  users: 'M9 11a3 3 0 1 0 0-6 3 3 0 0 0 0 6z|M3 20c0-3 3-5 6-5s6 2 6 5|M16 5a3 3 0 0 1 0 6|M18 15c2 .5 3 2 3 5',
  chevron: 'M9 6l6 6-6 6',
  folder: 'M3 6h6l2 2h10v11H3z',
  clock: 'M12 3a9 9 0 1 0 0 18a9 9 0 0 0 0-18z|M12 7v5l3 2',
  edit: 'M4 20h4L19 9l-4-4L4 16z|M13 7l4 4'
}

const props = withDefaults(defineProps<{ name: string; size?: number }>(), { size: 18 })
const paths = computed(() => (MAP[props.name] || '').split('|'))
</script>
