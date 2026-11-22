<template>
  <section class="panel">
    <header class="panel-title">
      <h3>Homework library</h3>
      <el-button type="primary" @click="emit('navigate', 'homework')">Create assignment</el-button>
    </header>
    <el-table :data="drafts" v-loading="loading" stripe>
      <el-table-column prop="title" label="Assignment" min-width="220" />
      <el-table-column prop="courseName" label="Course" min-width="180" />
      <el-table-column prop="totalScore" label="Score" width="90" />
      <el-table-column prop="classCount" label="Classes" width="100" />
      <el-table-column label="Actions" width="140" fixed="right">
        <template #default="{ row }"><el-button link type="primary" @click="reuse(row)">Reuse</el-button></template>
      </el-table-column>
      <template #empty><el-empty description="No saved drafts" /></template>
    </el-table>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { listTeacherAssignments, type HomeworkAssignment } from '../api/homework-api'
import { openHomeworkForSection } from '../stores/homework-context'

const emit = defineEmits<{ navigate: [id: string] }>()
const loading = ref(false)
const assignments = ref<HomeworkAssignment[]>([])
const drafts = computed(() => assignments.value.filter(item => item.status === 'DRAFT'))

async function load() {
  loading.value = true
  try { assignments.value = await listTeacherAssignments() }
  catch (error) { ElMessage.error(error instanceof Error ? error.message : 'Could not load homework library') }
  finally { loading.value = false }
}
function reuse(item: HomeworkAssignment) {
  openHomeworkForSection({
    courseId: item.courseId, chapterId: item.chapterId ?? 0, sectionId: item.sectionId ?? 0,
    sectionTitle: item.title, description: item.description ?? ''
  })
  emit('navigate', 'homework')
}
onMounted(() => { void load() })
</script>
