<template>
  <div>
    <div class="panel">
      <div class="toolbar">
        <el-select v-model="courseFilter" style="width:200px">
          <el-option label="全部课程" value="all" />
          <el-option label="短视频制作与运营" value="短视频制作与运营" />
          <el-option label="新媒体文案写作" value="新媒体文案写作" />
        </el-select>
        <el-input v-model="keyword" placeholder="搜索笔记内容" :prefix-icon="SearchIcon" style="width:220px" clearable />
        <span style="flex:1" />
        <span class="muted">共 {{ filtered.length }} 条笔记 · 点击时间点可跳转视频</span>
      </div>

      <div v-for="group in groupedNotes" :key="group.course" class="note-group">
        <div class="group-title">
          <BookOpen :size="15" style="color:var(--el-color-primary)" />
          <strong>{{ group.course }}</strong>
          <span class="muted">{{ group.items.length }} 条</span>
        </div>
        <div class="note-grid">
          <div v-for="n in group.items" :key="n.id" class="note-card" @click="jump(n)">
            <div class="n-top">
              <el-tag size="small" effect="plain" class="mono">{{ n.section }} · {{ n.time }}</el-tag>
              <el-button link type="danger" size="small" @click.stop="remove(n.id)">删除</el-button>
            </div>
            <p class="n-content">{{ n.content }}</p>
            <div class="n-foot">
              <span class="muted">{{ n.created }}</span>
              <span class="jump">跳转视频 <ArrowRight :size="12" style="vertical-align:-1px" /></span>
            </div>
          </div>
        </div>
      </div>
      <el-empty v-if="!filtered.length" description="暂无笔记，去课程学习页记录吧" :image-size="80" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Search as SearchIcon, BookOpen, ArrowRight } from 'lucide-vue-next'

const emit = defineEmits<{ navigate: [id: string] }>()

const courseFilter = ref('all')
const keyword = ref('')

interface Note { id: number; course: string; section: string; time: string; content: string; created: string }

const notes = ref<Note[]>([
  { id: 1, course: '短视频制作与运营', section: '2.2 剪辑软件快速上手', time: '01:36', content: '黄金三秒：开头必须给出冲突 / 悬念 / 利益点之一，直接决定 5 秒留存。', created: '11-19 08:42' },
  { id: 2, course: '短视频制作与运营', section: '2.2 剪辑软件快速上手', time: '03:35', content: 'J-cut = 声音先进画面后切，访谈转场常用；L-cut 相反。', created: '11-19 08:50' },
  { id: 3, course: '短视频制作与运营', section: '3.1 推荐算法拆解', time: '05:40', content: '冷启动看「小流量池完播率」，约 500 播放；进入叠加推荐看互动率。', created: '11-17 21:14' },
  { id: 4, course: '新媒体文案写作', section: '3.1 标题写作技巧', time: '02:18', content: '数字法标题：数字要具体、有对比（3 个方法 / 提升 2 倍）。', created: '11-16 19:33' },
  { id: 5, course: '新媒体文案写作', section: '2.4 用户视角写作', time: '04:02', content: '把「我们的产品很好」改写成「你能得到什么」。', created: '11-15 20:26' }
])

const filtered = computed(() =>
  notes.value.filter(n =>
    (courseFilter.value === 'all' || n.course === courseFilter.value) &&
    n.content.includes(keyword.value)
  )
)

const groupedNotes = computed(() => {
  const map = new Map<string, Note[]>()
  filtered.value.forEach(n => {
    if (!map.has(n.course)) map.set(n.course, [])
    map.get(n.course)!.push(n)
  })
  return [...map.entries()].map(([course, items]) => ({ course, items }))
})

function jump(n: Note) {
  ElMessage.info(`即将跳转到「${n.section}」${n.time} 处`)
  emit('navigate', 's-learn')
}
function remove(id: number) {
  notes.value = notes.value.filter(n => n.id !== id)
  ElMessage.success('笔记已删除')
}
</script>

<style scoped>
.toolbar { display: flex; align-items: center; gap: 10px; margin-bottom: 18px; flex-wrap: wrap; }
.note-group { margin-bottom: 22px; }
.group-title { display: flex; align-items: center; gap: 8px; margin-bottom: 12px; font-size: 14px; }
.note-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 12px; }
.note-card { border: 1px solid var(--app-line); border-radius: 10px; padding: 13px 15px; cursor: pointer; transition: border-color .15s ease, transform .15s ease; }
.note-card:hover { border-color: var(--el-color-primary); transform: translateY(-2px); }
.n-top { display: flex; align-items: center; justify-content: space-between; margin-bottom: 8px; }
.n-content { margin: 0 0 10px; font-size: 13px; line-height: 1.7; color: var(--app-ink-2); }
.n-foot { display: flex; align-items: center; justify-content: space-between; font-size: 11.5px; }
.jump { color: var(--el-color-primary); }
@media (max-width: 860px) {
  .note-grid { grid-template-columns: 1fr; }
}
</style>
