<template>
  <div>
    <div class="panel">
      <div class="toolbar">
        <el-radio-group v-model="filter">
          <el-radio-button value="all">全部课程</el-radio-button>
          <el-radio-button value="learning">学习中</el-radio-button>
          <el-radio-button value="done">已结课</el-radio-button>
        </el-radio-group>
        <span style="flex:1" />
        <el-input v-model="keyword" placeholder="搜索课程" :prefix-icon="SearchIcon" style="width:200px" clearable />
      </div>
      <div class="grid-3">
        <div v-for="c in filteredCourses" :key="c.name" class="course-card tall" :style="{ background: c.gradient }" @click="$emit('navigate', 's-learn')">
          <div>
            <div class="c-top">
              <el-tag size="small" effect="dark" :color="'rgba(255,255,255,.2)'" style="border:none">{{ c.term }}</el-tag>
                              <span v-if="c.progress === 100" class="done-badge">已结课</span>
            </div>
            <h4>{{ c.name }}</h4>
            <div class="meta">{{ c.teacher }} · {{ c.credit }} 学分 · {{ c.hours }} 学时</div>
          </div>
          <div>
            <div class="task-info muted-light">
              <span>任务点 {{ c.doneTasks }}/{{ c.tasks }}</span>
              <span>下一节：{{ c.next }}</span>
            </div>
            <div class="foot">
              <el-progress :percentage="c.progress" :stroke-width="6" color="rgba(255,255,255,.9)" :show-text="false" style="flex:1" />
              <span class="pct">{{ c.progress }}%</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { Search as SearchIcon } from 'lucide-vue-next'
import { globalSearch } from '../../stores/search'

defineEmits<{ navigate: [id: string] }>()

const filter = ref('all')
const keyword = ref(globalSearch.keyword)
globalSearch.keyword = ''

const courses = [
  { name: '短视频制作与运营', teacher: '王琳', credit: 3, hours: 48, progress: 68, tasks: 42, doneTasks: 29, next: '2.2 剪辑软件快速上手', term: '2026 秋', gradient: 'linear-gradient(135deg, #2f6bff, #6b4dff)' },
  { name: '新媒体文案写作', teacher: '李思颖', credit: 2, hours: 32, progress: 42, tasks: 30, doneTasks: 13, next: '3.1 标题写作技巧', term: '2026 秋', gradient: 'linear-gradient(135deg, #12b5a5, #0d8a7d)' },
  { name: '摄影摄像基础', teacher: '周明远', credit: 2, hours: 32, progress: 100, tasks: 36, doneTasks: 36, next: '—', term: '2026 春', gradient: 'linear-gradient(135deg, #e6872f, #d9534f)' },
  { name: '数字媒体概论', teacher: '陈嘉', credit: 2, hours: 32, progress: 100, tasks: 28, doneTasks: 28, next: '—', term: '2026 春', gradient: 'linear-gradient(135deg, #7a3ff2, #4f1bb8)' }
]

const filteredCourses = computed(() =>
  courses.filter(c =>
    (filter.value === 'all' || (filter.value === 'learning' ? c.progress < 100 : c.progress === 100)) &&
    c.name.includes(keyword.value)
  )
)
</script>

<style scoped>
.toolbar { display: flex; gap: 10px; align-items: center; margin-bottom: 16px; flex-wrap: wrap; }
.tall { min-height: 168px; }
.c-top { display: flex; align-items: center; justify-content: space-between; margin-bottom: 8px; }
.done-badge { font-size: 11px; background: rgba(255,255,255,.22); padding: 2px 8px; border-radius: 99px; }
.task-info { display: flex; flex-direction: column; gap: 2px; font-size: 11.5px; color: rgba(255,255,255,.82); margin-bottom: 4px; }
</style>
