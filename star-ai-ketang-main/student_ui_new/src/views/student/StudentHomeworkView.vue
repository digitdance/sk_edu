<template>
  <div>
    <div class="panel">
      <div class="toolbar">
        <el-radio-group v-model="filter">
          <el-radio-button value="all">全部</el-radio-button>
          <el-radio-button value="todo">待提交</el-radio-button>
          <el-radio-button value="submitted">已提交</el-radio-button>
          <el-radio-button value="graded">已批改</el-radio-button>
        </el-radio-group>
        <el-select v-model="courseFilter" style="width:190px">
          <el-option label="全部课程" value="all" />
          <el-option v-for="c in courseOptions" :key="c" :label="c" :value="c" />
        </el-select>
        <span style="flex:1" />
        <span class="muted">迟交将按教师规则标记，重做取最高分</span>
      </div>

      <el-table :data="filtered" border stripe>
        <el-table-column label="作业名称" min-width="220" show-overflow-tooltip>
          <template #default="{ row }">
            <span>{{ row.name }}</span>
            <el-tag v-if="row.requireAttachment" size="small" type="warning" effect="plain" style="margin-left:6px">需上传附件</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="course" label="所属课程" width="160" />
        <el-table-column prop="deadline" label="截止时间" width="150">
          <template #default="{ row }">
            <span :style="{ color: row.urgent && row.status === 'todo' ? '#f56c6c' : 'inherit' }" class="mono">{{ row.deadline }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag size="small" effect="light"
              :type="row.status === 'todo' ? 'danger' : row.status === 'submitted' ? 'warning' : 'success'">
              {{ statusText[row.status] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="成绩" width="90" align="center">
          <template #default="{ row }">
            <strong v-if="row.status === 'graded'" class="mono">{{ row.score }}</strong>
            <span v-else class="muted">—</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 'todo'" link type="primary" size="small" @click="openDo(row)">去完成作业</el-button>
            <el-button v-else-if="row.retakeLeft > 0" link size="small" @click="retake(row)">申请重做</el-button>
            <el-button v-else link type="primary" size="small" @click="viewResult(row)">查看批改</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 做作业 -->
    <el-dialog v-model="doDialog" :title="current?.name" width="640px" :close-on-click-modal="false">
      <el-alert type="warning" :closable="false" show-icon style="margin-bottom:14px"
        title="禁止粘贴 · 题目乱序 · 提交后立即显示客观题分数" />
      <p v-if="current?.description" class="q-stem">{{ current.description }}</p><el-input v-model="answerText" type="textarea" :rows="6" maxlength="5000" show-word-limit placeholder="Write your response" />
      <div v-for="(q, i) in questions" :key="i" class="q-block">
        <p class="q-stem">{{ i + 1 }}. {{ q.stem }}（{{ q.score }} 分）</p>
        <el-radio-group v-if="q.type === 'single'" v-model="q.my" class="q-opts">
          <el-radio v-for="o in q.options" :key="o" :value="o" class="q-opt">{{ o }}</el-radio>
        </el-radio-group>
        <el-input v-else v-model="q.my" type="textarea" :rows="4" placeholder="请输入答案（已禁用粘贴）" @paste.prevent />
      </div>

      <!-- 作业附件上传 -->
      <div v-if="current?.requireAttachment" class="attach-block">
        <div class="attach-title">
          作业附件
          <el-tag size="small" type="danger" effect="plain">必传</el-tag>
          <span class="muted">支持文档 / 图片 / 视频 / 压缩包，单个不超过 500MB</span>
        </div>
        <el-upload drag :auto-upload="false" :show-file-list="false" :on-change="onPickFile" multiple>
          <div class="upload-inner">
            <UploadCloud :size="30" style="color:var(--el-color-primary)" />
            <p>拖拽文件到此处，或 <em>点击上传</em></p>
          </div>
        </el-upload>
        <div v-for="(f, i) in files" :key="f.name + i" class="file-row">
          <FileText :size="14" style="color:var(--el-color-primary)" />
          <span class="grow">{{ f.name }}</span>
          <span class="muted">{{ f.size }}</span>
          <X :size="14" class="file-del" @click="files.splice(i, 1)" />
        </div>
      </div>
      <template #footer>
        <el-button @click="doDialog = false">暂存退出</el-button>
        <el-button type="primary" @click="submitHomework">提交作业</el-button>
      </template>
    </el-dialog>

    <!-- 批改结果 -->
    <el-drawer v-model="resultDrawer" title="批改详情" size="480px">
      <template v-if="current">
        <div class="result-head">
          <span class="big mono">{{ current.score }}</span>
          <div>
            <div style="font-weight:700">{{ current.name }}</div>
            <div class="muted">客观题 {{ current.objScore }} + 主观题 {{ (current.score ?? 0) - (current.objScore ?? 0) }}</div>
          </div>
        </div>
        <template v-if="current.attachments?.length">
          <el-divider content-position="left">已提交附件（{{ current.attachments.length }}）</el-divider>
          <div v-for="f in current.attachments" :key="f" class="file-row" style="margin-bottom:8px">
            <FileText :size="14" style="color:var(--el-color-primary)" />
            <span class="grow">{{ f }}</span>
          </div>
        </template>
        <el-divider content-position="left">教师评语</el-divider>
        <p v-if="current.answer" class="answer-box">{{ current.answer }}</p><p v-if="current.answer" class="answer-box">{{ current.answer }}</p><p class="comment-box">{{ current.teacherComment || "No teacher feedback yet" }}</p>
        <el-divider content-position="left">答题情况</el-divider>
        <div v-for="(q, i) in resultQuestions" :key="i" class="rq-row">
          <el-tag size="small" :type="q.right ? 'success' : 'danger'" effect="light">{{ q.right ? '正确' : '错误' }}</el-tag>
          <span class="grow">{{ q.stem }}</span>
          <span class="muted mono">{{ q.got }}/{{ q.score }}</span>
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { UploadFile } from 'element-plus'
import { UploadCloud, FileText, X } from 'lucide-vue-next'
import { listMyAssignments, submitAssignment, uploadHomeworkAttachment, type HomeworkAssignment } from '../../api/homework-api'

type Status = 'todo' | 'submitted' | 'graded'
interface Homework {
  id: number; name: string; course: string; description: string; deadline: string; urgent: boolean;
  status: Status; score: number | null; objScore: number | null; retakeLeft: number;
  submissionId?: number; teacherComment?: string; requireAttachment: boolean; attachments: string[]
  answer: string
}
interface PickedFile { name: string; size: string; url: string }
const filter = ref('all')
const courseFilter = ref('all')
const doDialog = ref(false)
const resultDrawer = ref(false)
const currentId = ref(0)
const files = ref<PickedFile[]>([])
const answerText = ref('')
const homework = ref<Homework[]>([])
const current = computed(() => homework.value.find(h => h.id === currentId.value) ?? null)
const statusText: Record<Status, string> = { todo: '待提交', submitted: '已提交', graded: '已批改' }
const filtered = computed(() => homework.value.filter(h =>
  (filter.value === 'all' || h.status === filter.value) &&
  (courseFilter.value === 'all' || h.course === courseFilter.value)))
const courseOptions = computed(() => [...new Set(homework.value.map(h => h.course))])
const questions: { type: string; stem: string; options: string[]; score: number; my: string }[] = []
const resultQuestions: { stem: string; right: boolean; got: number; score: number }[] = []

function parseSettings(value: unknown): Record<string, unknown> {
  if (value && typeof value === 'object') return value as Record<string, unknown>
  if (typeof value === 'string') { try { return JSON.parse(value) as Record<string, unknown> } catch { return {} } }
  return {}
}
function formatDeadline(value?: string) {
  if (!value) return '未设置'
  const date = new Date(value)
  return Number.isNaN(date.getTime()) ? value : date.toLocaleString()
}
function attachmentNames(value: unknown): string[] {
  let list = value
  if (typeof value === 'string') { try { list = JSON.parse(value) } catch { list = [] } }
  return Array.isArray(list) ? list.filter((url): url is string => typeof url === 'string').map(url => url.split('/').pop() || url) : []
}
function mapAssignment(item: HomeworkAssignment): Homework {
  const settings = parseSettings(item.settings)
  const status: Status = item.submissionStatus === 'GRADED' ? 'graded' : item.submissionStatus === 'SUBMITTED' ? 'submitted' : 'todo'
  return {
    id: item.id, name: item.title, course: item.courseName, description: item.description ?? '',
    deadline: formatDeadline(item.deadline), urgent: false, status, score: item.score ?? null, objScore: null,
    submissionId: item.submissionId, teacherComment: item.teacherComment,
    answer: String(parseSettings(item.answers).answer ?? ''),
    retakeLeft: settings.allowRetake === true ? Number(settings.retakeCount ?? 2) : 0,
    requireAttachment: Array.isArray(settings.formats) && settings.formats.includes('附件'), attachments: attachmentNames(item.attachments)
  }
}
async function loadHomework() {
  const assignments = await listMyAssignments()
  homework.value = assignments.map(mapAssignment)
  if (!homework.value.some(h => h.id === currentId.value)) currentId.value = homework.value[0]?.id ?? 0
}
function formatSize(bytes: number) {
  if (bytes >= 1024 * 1024) return `${(bytes / 1024 / 1024).toFixed(1)} MB`
  return `${Math.max(1, Math.round(bytes / 1024))} KB`
}
async function onPickFile(file: UploadFile) {
  if (!file.raw) return
  try {
    const uploaded = await uploadHomeworkAttachment(file.raw)
    files.value.push({ name: uploaded.originalName || file.name, size: formatSize(file.raw.size), url: uploaded.fileUrl })
    ElMessage.success('附件上传完成')
  } catch (error) { ElMessage.error(error instanceof Error ? error.message : '附件上传失败') }
}
function openDo(row: Homework) {
  currentId.value = row.id
  answerText.value = ''
  files.value = []
  doDialog.value = true
}
async function submitHomework() {
  if (!current.value) return
  if (current.value.requireAttachment && !files.value.length) { ElMessage.warning('请先上传作业附件'); return }
  try {
    await submitAssignment(current.value.id, { answer: answerText.value }, files.value.map(file => file.url))
    doDialog.value = false
    ElMessage.success('作业已提交')
    await loadHomework()
  } catch (error) { ElMessage.error(error instanceof Error ? error.message : '提交作业失败') }
}
function retake(row: Homework) {
  if (!row.retakeLeft) { ElMessage.warning('已达到重做次数上限'); return }
  ElMessageBox.confirm(`剩余重做次数 ${row.retakeLeft} 次，确认申请重做？`, '申请重做', { type: 'info' })
    .then(() => openDo(row)).catch(() => undefined)
}
function viewResult(row: Homework) { currentId.value = row.id; resultDrawer.value = true }
function previewAttachment(name: string) { ElMessage.info(`附件：${name}`) }
onMounted(() => { void loadHomework().catch(error => ElMessage.error(error instanceof Error ? error.message : '加载作业失败')) })
</script>





<style scoped>
.toolbar { display: flex; align-items: center; gap: 10px; margin-bottom: 14px; flex-wrap: wrap; }
.q-block { border: 1px solid var(--app-line); border-radius: 8px; padding: 14px 16px; margin-bottom: 12px; }
.q-stem { font-weight: 600; font-size: 14px; margin: 0 0 10px; line-height: 1.6; }
.q-opts { display: flex; flex-direction: column; gap: 2px; }
.q-opt { height: auto; padding: 7px 8px; border-radius: 6px; margin-right: 0 !important; }
.q-opt:hover { background: #f7f9ff; }
.result-head { display: flex; align-items: center; gap: 14px; }
.result-head .big { font-size: 36px; font-weight: 800; color: var(--el-color-primary); }
.comment-box { font-size: 13px; line-height: 1.8; color: var(--app-ink-2); background: #fafbfd; border-radius: 8px; padding: 12px 14px; }
.rq-row { display: flex; align-items: center; gap: 10px; padding: 9px 0; border-bottom: 1px solid #f2f4f9; font-size: 13px; }
.grow { flex: 1; min-width: 0; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.attach-block { border: 1px dashed var(--app-line); border-radius: 10px; padding: 14px 16px; margin-top: 4px; }
.attach-title { display: flex; align-items: center; gap: 8px; font-weight: 700; font-size: 14px; margin-bottom: 10px; }
.attach-title .muted { font-weight: 400; font-size: 12px; }
.upload-inner { padding: 10px 0 4px; text-align: center; }
.upload-inner p { margin: 6px 0 0; font-size: 13px; }
.file-row { display: flex; align-items: center; gap: 8px; padding: 8px 10px; border: 1px solid #eef1f6; border-radius: 8px; margin-top: 8px; font-size: 13px; }
.file-del { cursor: pointer; color: var(--app-ink-3); }
.file-del:hover { color: #f56c6c; }
</style>
