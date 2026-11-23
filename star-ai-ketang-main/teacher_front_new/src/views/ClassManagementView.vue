<template>
  <section class="panel">
    <header class="panel-title">
      <h3>班级管理</h3>
      <el-button type="primary" @click="openCreate">新建班级</el-button>
    </header>
    <el-table :data="classes" v-loading="loading" stripe>
      <el-table-column prop="className" label="班级名称" min-width="180" />
      <el-table-column prop="description" label="描述" min-width="220" />
      <el-table-column prop="memberCount" label="人数" width="90" />
      <el-table-column label="操作" width="260" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openMembers(row)">成员</el-button>
          <el-button link @click="openEdit(row)">编辑</el-button>
          <el-button link @click="openCourse(row)">关联课程</el-button>
          <el-button link type="danger" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
      <template #empty><el-empty description="暂无班级" /></template>
    </el-table>

    <el-dialog v-model="classDialog" :title="editing ? '编辑班级' : '新建班级'" width="480px">
      <el-form label-position="top">
        <el-form-item label="班级名称"><el-input v-model="form.className" maxlength="100" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" maxlength="500" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="classDialog = false">取消</el-button><el-button type="primary" @click="saveClass">保存</el-button></template>
    </el-dialog>

    <el-dialog v-model="memberDialog" :title="`${selected?.className ?? ''} · 成员`" width="600px">
      <div class="toolbar"><el-input-number v-model="newMemberId" :min="1" placeholder="学生用户 ID" /><el-select v-model="newMemberRole"><el-option label="学生" value="student" /><el-option label="教师" value="teacher" /></el-select><el-button type="primary" @click="addMember">添加成员</el-button></div>
      <el-table :data="members" v-loading="membersLoading">
        <el-table-column prop="userId" label="用户 ID" />
        <el-table-column prop="userName" label="姓名" />
        <el-table-column prop="role" label="角色" />
        <el-table-column label="操作" width="100"><template #default="{ row }"><el-button link type="danger" @click="removeMember(row)">移除</el-button></template></el-table-column>
      </el-table>
    </el-dialog>

    <el-dialog v-model="courseDialog" :title="`${selected?.className ?? ''} · 关联课程`" width="480px">
      <el-select v-model="courseId" filterable style="width:100%" placeholder="选择课程"><el-option v-for="course in courses" :key="course.id" :label="course.title" :value="course.id" /></el-select>
      <template #footer><el-button @click="courseDialog = false">取消</el-button><el-button type="primary" @click="addCourse">关联</el-button></template>
    </el-dialog>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { apiRequest } from '../api/client'

interface ClassRow { id: string; className: string; description?: string; memberCount?: number }
interface Page<T> { list: T[]; total: number }
interface MemberRow { userId: string; userName?: string; role: string }
interface CourseRow { id: number; title: string }
const classes = ref<ClassRow[]>([])
const courses = ref<CourseRow[]>([])
const members = ref<MemberRow[]>([])
const selected = ref<ClassRow | null>(null)
const loading = ref(false)
const membersLoading = ref(false)
const classDialog = ref(false)
const memberDialog = ref(false)
const courseDialog = ref(false)
const editing = ref(false)
const form = ref({ className: '', description: '' })
const newMemberId = ref<number | undefined>()
const newMemberRole = ref('student')
const courseId = ref<number | undefined>()

async function load() {
  loading.value = true
  try {
    const page = await apiRequest<Page<ClassRow>>('/api/classes/list?pageNum=1&pageSize=100')
    classes.value = page.list ?? []
    await Promise.all(classes.value.map(async row => {
      try { const detail = await apiRequest<Page<MemberRow>>(`/api/classes/${row.id}/members?pageNum=1&pageSize=1`); row.memberCount = detail.total }
      catch { row.memberCount = 0 }
    }))
  } catch (error) { ElMessage.error(error instanceof Error ? error.message : '班级加载失败') }
  finally { loading.value = false }
}
function openCreate() { editing.value = false; form.value = { className: '', description: '' }; classDialog.value = true }
function openEdit(row: ClassRow) { selected.value = row; editing.value = true; form.value = { className: row.className, description: row.description ?? '' }; classDialog.value = true }
async function saveClass() {
  if (!form.value.className.trim()) { ElMessage.warning('请输入班级名称'); return }
  try {
    if (editing.value && selected.value) await apiRequest(`/api/classes/${selected.value.id}`, { method: 'PUT', body: form.value })
    else await apiRequest('/api/classes', { method: 'POST', body: form.value })
    classDialog.value = false; await load(); ElMessage.success('班级已保存')
  } catch (error) { ElMessage.error(error instanceof Error ? error.message : '保存失败') }
}
async function remove(row: ClassRow) {
  try { await ElMessageBox.confirm(`确认删除班级“${row.className}”？`, '删除班级', { type: 'warning' }); await apiRequest(`/api/classes/${row.id}`, { method: 'DELETE' }); await load() }
  catch (error) { if (error instanceof Error) ElMessage.error(error.message) }
}
async function openMembers(row: ClassRow) {
  selected.value = row; memberDialog.value = true; membersLoading.value = true
  try { const page = await apiRequest<Page<MemberRow>>(`/api/classes/${row.id}/members?pageNum=1&pageSize=100`); members.value = page.list ?? [] }
  catch (error) { ElMessage.error(error instanceof Error ? error.message : '成员加载失败') }
  finally { membersLoading.value = false }
}
async function addMember() {
  if (!selected.value || !newMemberId.value) return
  try { await apiRequest(`/api/classes/${selected.value.id}/members`, { method: 'POST', body: { userId: newMemberId.value, role: newMemberRole.value } }); newMemberId.value = undefined; await openMembers(selected.value); await load() }
  catch (error) { ElMessage.error(error instanceof Error ? error.message : '添加成员失败') }
}
async function removeMember(row: MemberRow) {
  if (!selected.value) return
  try { await apiRequest(`/api/classes/${selected.value.id}/members/${row.userId}`, { method: 'DELETE' }); await openMembers(selected.value); await load() }
  catch (error) { ElMessage.error(error instanceof Error ? error.message : '移除成员失败') }
}
async function openCourse(row: ClassRow) {
  selected.value = row; courseId.value = undefined; courseDialog.value = true
  try { courses.value = await apiRequest<CourseRow[]>('/api/course/list?page=1&size=100') }
  catch (error) { ElMessage.error(error instanceof Error ? error.message : '课程加载失败') }
}
async function addCourse() {
  if (!selected.value || !courseId.value) return
  try { await apiRequest(`/api/classes/${selected.value.id}/courses`, { method: 'POST', body: { courseId: courseId.value } }); courseDialog.value = false; ElMessage.success('课程已关联到班级') }
  catch (error) { ElMessage.error(error instanceof Error ? error.message : '关联课程失败') }
}
onMounted(() => { void load() })
</script>
