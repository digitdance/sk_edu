<template>
  <div>
    <!-- 顶部统计与筛选 -->
    <div class="panel">
      <div class="grade-head">
        <div class="stat-row">
          <div class="stat-mini"><span class="num mono">{{ submittedCount }}</span><span class="lbl">已提交</span></div>
          <el-divider direction="vertical" />
          <div class="stat-mini"><span class="num mono" style="color:#e6a23c">{{ pendingCount }}</span><span class="lbl">待批阅</span></div>
          <el-divider direction="vertical" />
          <div class="stat-mini"><span class="num mono" style="color:#2fbf71">{{ gradedCount }}</span><span class="lbl">已批阅</span></div>
        </div>
        <div class="filter-row">
          <el-select v-model="homeworkFilter" style="width:230px"><el-option v-for="assignment in homeworkOptions" :key="assignment.id" :label="assignment.title" :value="assignment.id" /></el-select>
          <el-select v-model="statusFilter" style="width:120px">
            <el-option label="全部状态" value="all" />
            <el-option label="待批阅" value="pending" />
            <el-option label="已批阅" value="graded" />
            <el-option label="未提交" value="missing" />
          </el-select>
          <el-button :icon="BellRing" @click="remindMissing">提醒未交（2）</el-button>
          <el-button type="primary" plain @click="autoGradeAll">客观题自动判分</el-button>
        </div>
      </div>
    </div>

    <div class="grading-layout">
      <!-- 学生列表 -->
      <div class="panel stu-col">
        <el-input v-model="keyword" placeholder="搜索姓名 / 学号" :prefix-icon="SearchIcon" clearable style="margin-bottom:12px" />
        <div class="stu-list">
          <div
            v-for="s in filteredStudents" :key="s.id"
            class="stu-item" :class="{ active: s.id === currentId }"
            @click="selectStudent(s)"
          >
            <div class="s-main">
              <strong>{{ s.name }}</strong>
              <span class="muted mono">{{ s.no }}</span>
            </div>
            <div class="s-sub">
              <el-tag v-if="s.status === 'graded'" size="small" type="success" effect="light">{{ s.score }} 分</el-tag>
              <el-tag v-else-if="s.status === 'pending'" size="small" type="warning" effect="light">待批阅</el-tag>
              <el-tag v-else size="small" type="danger" effect="light">未提交</el-tag>
              <span class="muted">{{ s.submitTime }}</span>
            </div>
          </div>
          <el-empty v-if="!filteredStudents.length" description="无匹配学生" :image-size="60" />
        </div>
      </div>

      <!-- 批阅工作区 -->
      <div class="panel work-col" v-if="current">
        <div class="panel-title">
          <h3>{{ current.name }} · {{ current.no }}</h3>
          <span class="extra">第 {{ currentIndex + 1 }} / {{ filteredStudents.length }} 份</span>
        </div>

        <!-- 客观题得分 -->
        <div class="obj-score">
          <div class="o-item"><span class="muted">客观题（已自动判分）</span><strong class="mono">{{ current.objective }} / 60</strong></div>
          <div class="o-item"><span class="muted">主观题（{{ current.status === 'graded' ? '已批阅' : '待批阅' }}）</span><strong class="mono">{{ current.status === 'graded' ? (current.score - current.objective) + ' / 40' : '— / 40' }}</strong></div>
          <div class="o-item total"><span class="muted">总分</span><strong class="mono">{{ current.status === 'graded' ? current.score : '—' }}</strong></div>
        </div>

        <!-- 学生提交附件 -->
        <div v-if="current.attachments?.length" class="attach-box">
          <div class="q-title">学生提交附件（{{ current.attachments.length }}）</div>
          <div v-for="f in current.attachments" :key="f.name" class="att-row">
            <Paperclip :size="14" style="color:var(--el-color-primary)" />
            <span class="att-name">{{ f.name }}</span>
            <span class="muted">{{ f.size }}</span>
            <el-button link type="primary" size="small" @click="previewAttachment(f)">预览</el-button>
            <el-button link size="small" @click="downloadFile(f)">下载</el-button>
          </div>
        </div>

        <!-- 主观题 -->
        <div class="subj-box">
          <div class="q-title">简答题（10 分）：结合本课程案例，阐述账号垂直定位对涨粉效率的影响机制。</div>
          <div class="answer-box">
            <p>{{ current.answer }}</p>
          </div>

          <div class="ai-hint">
            <Sparkles :size="15" />
            <div>
              <strong>AI 参考评分：{{ current.aiScore }} 分</strong>
              <p>{{ current.aiComment }}</p>
            </div>
            <el-button size="small" plain @click="adoptAi">采用</el-button>
          </div>

          <div class="grade-inputs">
            <el-form-item label="本题得分">
              <el-input-number v-model="subjectiveScore" :min="0" :max="homeworkOptions.find(item => item.id === homeworkFilter)?.totalScore ?? 100" :step="0.5" />
            </el-form-item>
            <el-form-item label="评语" style="flex:1">
              <el-input v-model="comment" type="textarea" :rows="2" placeholder="输入评语，学生端批阅后可见" />
            </el-form-item>
          </div>
        </div>

        <div class="work-actions">
          <el-button :disabled="currentIndex === 0" @click="go(-1)">上一份</el-button>
          <span style="flex:1" />
          <el-button @click="saveGrade(false)">保存</el-button>
          <el-button type="primary" @click="saveGrade(true)">保存并下一份</el-button>
        </div>

        <!-- 选项统计 -->
        <el-divider content-position="left">单选第 3 题 · 选项分布（38 人作答）</el-divider>
        <div class="opt-stats">
          <div v-for="o in optionStats" :key="o.key" class="opt-stat">
            <span class="tag" :class="{ right: o.right }">{{ o.key }}</span>
            <span class="opt-text">{{ o.text }}</span>
            <div class="bar-wrap"><div class="bar-fill" :class="{ right: o.right }" :style="{ width: o.pct + '%' }" /></div>
            <span class="pct mono">{{ o.pct }}%</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Search as SearchIcon, BellRing, Sparkles, Paperclip } from 'lucide-vue-next'
import { previewDocument } from '../utils/kkPreview'
import { listTeacherAssignments, listAssignmentSubmissions, gradeAssignmentSubmission, type HomeworkAssignment, type HomeworkSubmission } from '../api/homework-api'

interface Attachment { name: string; size: string; url: string }
interface Student {
  id: number; submissionId: number; name: string; no: string; status: 'pending' | 'graded'; score: number;
  objective: number; submitTime: string; aiScore: number; aiComment: string; answer: string; attachments: Attachment[]
}
const homeworkOptions = ref<HomeworkAssignment[]>([])
const homeworkFilter = ref<number | null>(null)
const statusFilter = ref('all')
const keyword = ref('')
const currentId = ref(0)
const subjectiveScore = ref(0)
const comment = ref('')
const students = ref<Student[]>([])
const optionStats: { key: string; text: string; pct: number; right: boolean }[] = []
const filteredStudents = computed(() => students.value.filter(s =>
  (statusFilter.value === 'all' || s.status === statusFilter.value) &&
  (s.name.includes(keyword.value) || s.no.includes(keyword.value))))
const current = computed(() => filteredStudents.value.find(s => s.id === currentId.value) ?? filteredStudents.value[0] ?? null)
const currentIndex = computed(() => current.value ? filteredStudents.value.findIndex(s => s.id === current.value!.id) : 0)
const submittedCount = computed(() => students.value.length)
const gradedCount = computed(() => students.value.filter(s => s.status === 'graded').length)
const pendingCount = computed(() => submittedCount.value - gradedCount.value)
function parseObject(value: unknown): Record<string, unknown> {
  if (value && typeof value === 'object') return value as Record<string, unknown>
  if (typeof value === 'string') { try { return JSON.parse(value) as Record<string, unknown> } catch { return {} } }
  return {}
}
function parseAttachments(value: unknown): Attachment[] {
  let list = value
  if (typeof value === 'string') { try { list = JSON.parse(value) } catch { list = [] } }
  if (!Array.isArray(list)) return []
  return list.filter((item): item is string => typeof item === 'string').map(url => ({ name: url.split('/').pop() || url, size: '', url }))
}
async function loadAssignments() {
  homeworkOptions.value = await listTeacherAssignments()
  if (!homeworkFilter.value && homeworkOptions.value.length) homeworkFilter.value = homeworkOptions.value[0].id
  if (!homeworkOptions.value.length) students.value = []
}
async function loadSubmissions() {
  if (!homeworkFilter.value) { students.value = []; return }
  const records = await listAssignmentSubmissions(homeworkFilter.value)
  students.value = records.map((record: HomeworkSubmission) => {
    const answers = parseObject(record.answers)
    return {
      id: record.id, submissionId: record.id, name: record.studentName || record.studentAccount,
      no: record.studentAccount, status: record.status === 'GRADED' ? 'graded' : 'pending', score: Number(record.score ?? 0),
      objective: 0, submitTime: record.submittedAt ? new Date(record.submittedAt).toLocaleString() : '',
      aiScore: 0, aiComment: '', answer: String(answers.answer ?? answers.text ?? Object.values(answers).join('\n')),
      attachments: parseAttachments(record.attachments)
    }
  })
  currentId.value = students.value[0]?.id ?? 0
  if (students.value[0]) selectStudent(students.value[0])
}
function selectStudent(student: Student) {
  currentId.value = student.id
  subjectiveScore.value = student.score
  comment.value = ''
}
function go(step: number) {
  const next = filteredStudents.value[currentIndex.value + step]
  if (next) selectStudent(next)
}
function adoptAi() { ElMessage.info('当前作业采用教师手动评分') }
async function saveGrade(next: boolean) {
  if (!current.value) return
  try {
    await gradeAssignmentSubmission(current.value.submissionId, subjectiveScore.value, comment.value)
    current.value.status = 'graded'
    current.value.score = subjectiveScore.value
    ElMessage.success('评分已保存，学生端可以查看')
    if (next) go(1)
  } catch (error) { ElMessage.error(error instanceof Error ? error.message : '保存评分失败') }
}
function previewAttachment(file: Attachment) {
  if (previewDocument(file.url)) return
  window.open(file.url, '_blank', 'noopener,noreferrer')
}
function downloadFile(file: Attachment) { window.open(file.url, '_blank', 'noopener,noreferrer') }
function remindMissing() { ElMessage.info('未提交提醒接口尚未接入') }
function autoGradeAll() { ElMessage.info('作业自动评分接口尚未接入') }
watch(homeworkFilter, () => { void loadSubmissions().catch(showError) })
function showError(error: unknown) { ElMessage.error(error instanceof Error ? error.message : '加载作业提交失败') }
onMounted(() => { void loadAssignments().then(loadSubmissions).catch(showError) })
</script>





<style scoped>
.grade-head { display: flex; align-items: center; justify-content: space-between; gap: 16px; flex-wrap: wrap; }
.stat-row { display: flex; align-items: center; gap: 18px; }
.filter-row { display: flex; gap: 10px; flex-wrap: wrap; }
.grading-layout { display: grid; grid-template-columns: 300px 1fr; gap: 16px; align-items: start; }
.stu-col { max-height: 720px; overflow-y: auto; }
.stu-list { display: flex; flex-direction: column; gap: 6px; }
.stu-item { border: 1px solid var(--app-line); border-radius: 8px; padding: 10px 12px; cursor: pointer; transition: border-color .15s ease, background .15s ease; }
.stu-item:hover { border-color: var(--el-color-primary-light-5); }
.stu-item.active { border-color: var(--el-color-primary); background: var(--el-color-primary-light-9); }
.s-main { display: flex; align-items: center; justify-content: space-between; }
.s-main strong { font-size: 13.5px; }
.s-sub { display: flex; align-items: center; justify-content: space-between; margin-top: 6px; }
.obj-score { display: flex; gap: 12px; margin-bottom: 16px; flex-wrap: wrap; }
.o-item { flex: 1; min-width: 150px; border: 1px solid var(--app-line); border-radius: 8px; padding: 12px 14px; display: flex; flex-direction: column; gap: 4px; }
.o-item strong { font-size: 20px; }
.o-item.total { background: var(--el-color-primary-light-9); border-color: var(--el-color-primary-light-7); }
.o-item.total strong { color: var(--el-color-primary); }
.attach-box { border: 1px solid var(--app-line); border-radius: 10px; padding: 14px 16px; margin-bottom: 14px; }
.attach-box .q-title { margin-bottom: 8px; }
.att-row { display: flex; align-items: center; gap: 8px; padding: 8px 10px; border: 1px solid #eef1f6; border-radius: 8px; margin-top: 8px; font-size: 13px; }
.att-name { flex: 1; min-width: 0; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.subj-box { border: 1px solid var(--app-line); border-radius: 10px; padding: 16px; }
.q-title { font-weight: 700; font-size: 14px; margin-bottom: 10px; }
.answer-box { background: #fafbfd; border-radius: 8px; padding: 14px; font-size: 13.5px; line-height: 1.8; color: var(--app-ink-2); margin-bottom: 12px; }
.answer-box p { margin: 0; }
.ai-hint {
  display: flex; align-items: flex-start; gap: 10px; padding: 12px 14px; border-radius: 8px;
  background: #f5f0ff; border: 1px solid #e3d8fb; margin-bottom: 14px; font-size: 12.5px; color: #4a3b7a;
}
.ai-hint p { margin: 3px 0 0; }
.ai-hint > div { flex: 1; }
.grade-inputs { display: flex; gap: 16px; align-items: flex-start; flex-wrap: wrap; }
.grade-inputs :deep(.el-form-item) { margin-bottom: 0; }
.work-actions { display: flex; gap: 10px; margin-top: 16px; }
.opt-text { width: 90px; font-size: 12.5px; color: var(--app-ink-2); flex-shrink: 0; }
@media (max-width: 960px) {
  .grading-layout { grid-template-columns: 1fr; }
  .stu-col { max-height: none; }
}
</style>
