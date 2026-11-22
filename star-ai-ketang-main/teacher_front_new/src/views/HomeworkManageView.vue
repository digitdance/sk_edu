<template>
  <div class="hw-layout">
    <!-- 作业列表 -->
    <div class="panel list-col">
      <div class="panel-title">
        <h3>作业列表</h3>
        <el-button size="small" type="primary" @click="createDialog = true">创建作业</el-button>
      </div>
      <el-select v-model="courseFilter" size="small" style="width:100%; margin-bottom:10px">
        <el-option label="全部课程" value="all" />
        <el-option v-for="c in courseOptions" :key="c" :label="c" :value="c" />
      </el-select>
      <div class="hw-list">
        <div
          v-for="h in filteredHomework" :key="h.id"
          class="hw-item" :class="{ active: h.id === currentId }"
          @click="selectHomework(h.id)"
        >
          <div class="h-row">
            <strong>{{ h.name }}</strong>
            <el-tag size="small" effect="light" :type="h.status === '进行中' ? 'success' : h.status === '未发布' ? 'info' : 'warning'">
              {{ h.status }}
            </el-tag>
          </div>
          <div class="muted">{{ h.course }} · {{ h.chapter }} · {{ h.count }} 题 · {{ h.score }} 分</div>
          <div class="muted">截止 {{ h.deadline }} · 已交 {{ h.submitted }}/{{ h.total }}</div>
        </div>
        <el-empty v-if="!filteredHomework.length" description="该课程暂无作业" :image-size="60" />
      </div>
    </div>

    <!-- 作业设置 -->
    <div class="panel setting-col">
      <div class="panel-title">
        <h3>作业设置</h3>
        <div style="display:flex; gap:10px">
          <el-button @click="saveSettings(false)">保存设置</el-button>
          <el-button type="primary" @click="saveSettings(true)">保存并发布</el-button>
        </div>
      </div>

      <el-form label-position="top" class="setting-form">
        <div class="grid-2">
          <el-form-item label="作业名称">
            <el-input v-model="form.name" />
          </el-form-item>
          <el-form-item label="Course">
            <el-select v-model="selectedCourseId" style="width:100%" @change="loadCourseStructure">
              <el-option v-for="c in availableCourses" :key="c.id" :label="c.title" :value="c.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="所属章节">
            <el-select v-model="selectedChapterId">
              <el-option v-for="c in chaptersList" :key="c.id" :label="c.title" :value="c.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="Section"><el-select v-model="selectedSectionId" clearable><el-option v-for="section in sectionsList" :key="section.id" :label="section.title" :value="section.id" /></el-select></el-form-item><el-form-item label="发布班级">
            <el-select v-model="form.classes" multiple>
              <el-option v-for="c in classList" :key="c.id" :label="c.className" :value="c.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="截止时间">
            <el-date-picker v-model="form.deadline" type="datetime" style="width:100%" />
          </el-form-item>
        </div>

        <el-divider content-position="left">题目来源</el-divider>
        <el-form-item label="Assignment prompt"><el-input v-model="description" type="textarea" :rows="3" maxlength="2000" show-word-limit /></el-form-item>
        <el-radio-group v-model="form.source">
          <el-radio value="pick">从题库选题（当前已选 12 题 · 40 分）</el-radio>
          <el-radio value="random">随机抽题（每位学生题目不同）</el-radio>
        </el-radio-group>
        <div v-if="form.source === 'random'" class="random-box">
          <div v-for="row in form.randomItems" :key="row.type" class="random-row">
            <span>{{ row.type }}</span>
            <el-input-number v-model="row.count" :min="0" :max="20" size="small" />
            <span class="muted">题</span>
            <el-input-number v-model="row.score" :min="1" :max="20" size="small" />
            <span class="muted">分/题</span>
          </div>
        </div>
        <div class="grid-2" style="margin-top:14px">
          <el-form-item label="总分">
            <el-input-number v-model="form.score" :min="1" :max="200" />
          </el-form-item>
          <el-form-item label="及格分">
            <el-input-number v-model="form.passScore" :min="0" :max="form.score" />
          </el-form-item>
        </div>

        <el-divider content-position="left">提交与评分规则</el-divider>
        <div class="rule-grid">
          <div class="rule-row">
            <div>
              <div class="r-label">允许迟交</div>
              <div class="muted">迟交作业标记为「迟交」，可正常批阅</div>
            </div>
            <el-switch v-model="form.allowLate" />
          </div>
          <div class="rule-row">
            <div>
              <div class="r-label">允许重做</div>
              <div class="muted">取最高一次成绩计入总评</div>
            </div>
            <div style="display:flex; align-items:center; gap:8px">
              <el-input-number v-if="form.allowRetake" v-model="form.retakeCount" :min="1" :max="5" size="small" />
              <el-switch v-model="form.allowRetake" />
            </div>
          </div>
          <div class="rule-row">
            <div>
              <div class="r-label">客观题自动判分</div>
              <div class="muted">选择/判断/填空提交后立即出分</div>
            </div>
            <el-switch v-model="form.autoGrade" />
          </div>
          <div class="rule-row">
            <div>
              <div class="r-label">题目与选项乱序</div>
              <div class="muted">降低互相对答案的可能性</div>
            </div>
            <el-switch v-model="form.shuffle" />
          </div>
          <div class="rule-row">
            <div>
              <div class="r-label">禁止粘贴答案</div>
              <div class="muted">输入框禁用粘贴，需手动录入</div>
            </div>
            <el-switch v-model="form.noPaste" />
          </div>
          <div class="rule-row">
            <div>
              <div class="r-label">提交后显示答案与解析</div>
              <div class="muted">关闭则仅在教师批阅后可见</div>
            </div>
            <el-switch v-model="form.showAnswer" />
          </div>
          <div class="rule-row">
            <div>
              <div class="r-label">提交后立即显示分数</div>
              <div class="muted">主观题部分待批阅后更新</div>
            </div>
            <el-switch v-model="form.showScore" />
          </div>
          <div class="rule-row">
            <div>
              <div class="r-label">简答题答案长度限制</div>
              <div class="muted">防止大段复制无关内容</div>
            </div>
            <div style="display:flex; align-items:center; gap:8px">
              <el-input-number v-if="form.limitLength" v-model="form.maxLength" :min="100" :max="5000" :step="100" size="small" />
              <el-switch v-model="form.limitLength" />
            </div>
          </div>
        </div>

        <el-form-item label="允许的提交格式" style="margin-top:6px">
          <el-checkbox-group v-model="form.formats">
            <el-checkbox value="文本">在线文本</el-checkbox>
            <el-checkbox value="附件">附件上传</el-checkbox>
            <el-checkbox value="图片">图片</el-checkbox>
            <el-checkbox value="视频">视频作品</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
    </div>

    <!-- 创建作业 -->
    <el-dialog v-model="createDialog" title="创建作业" width="480px">
      <el-form label-position="top">
        <el-form-item label="作业名称">
          <el-input v-model="newName" placeholder="例如：第 3 章课后作业 · 算法理解" />
        </el-form-item>
        <el-form-item label="创建方式">
          <el-radio-group v-model="createMode">
            <el-radio value="blank">空白作业</el-radio>
            <el-radio value="lib">从作业库引用</el-radio>
            <el-radio value="ai">AI 智能出题</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-alert v-if="createMode === 'ai'" type="info" :closable="false" show-icon
          title="AI 将依据章节知识点自动生成题目草稿，可在题库中二次编辑。" />
      </el-form>
      <template #footer>
        <el-button @click="createDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmCreate">创建并配置</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { apiRequest } from '../api/client'
import { auth } from '../stores/auth'
import { homeworkContext } from '../stores/homework-context'
import { createAssignment, listTeacherAssignments, publishAssignment, updateAssignment, type AssignmentPayload, type HomeworkAssignment } from '../api/homework-api'

const emit = defineEmits<{ navigate: [id: string] }>()
interface CourseOption { id: number; title: string }
interface ClassOption { id: number; className: string }
interface CourseChapter { id: number; title: string; sections: { id: number; title: string }[] }
interface HomeworkItem { id: number; name: string; course: string; courseId: number; chapter: string; chapterId?: number; sectionId?: number; classIds: number[]; settings: Record<string, unknown>; description: string; count: number; score: number; deadline: string; submitted: number; total: number; status: string }

const availableCourses = ref<CourseOption[]>([])
const classList = ref<ClassOption[]>([])
const courseChapters = ref<CourseChapter[]>([])
const selectedCourseId = ref<number | null>(null)
const selectedChapterId = ref<number | null>(null)
const selectedSectionId = ref<number | null>(null)
const chaptersList = computed(() => courseChapters.value)
const sectionsList = computed(() => courseChapters.value.find(ch => ch.id === selectedChapterId.value)?.sections ?? [])
const homeworkList = ref<HomeworkItem[]>([])
const courseFilter = ref('all')
const courseOptions = computed(() => [...new Set(homeworkList.value.map(h => h.course))])
const filteredHomework = computed(() => homeworkList.value.filter(h => courseFilter.value === 'all' || h.course === courseFilter.value))
const currentId = ref(0)
const createDialog = ref(false)
const createMode = ref('blank')
const newName = ref('')
const description = ref('')
const isNew = ref(false)
const form = ref({
  name: '', chapter: '', classes: [] as number[], deadline: null as Date | null, source: 'pick',
  randomItems: [{ type: '单选题', count: 0, score: 4 }, { type: '多选题', count: 0, score: 8 }, { type: '简答题', count: 0, score: 10 }],
  score: 100, passScore: 60, allowLate: true, allowRetake: true, retakeCount: 2,
  autoGrade: true, shuffle: false, noPaste: false, showAnswer: false, showScore: true,
  limitLength: false, maxLength: 2000, formats: ['文本', '附件', '图片']
})

function parseIdList(value: unknown): number[] {
  if (Array.isArray(value)) return value.map(Number)
  if (typeof value === 'string') { try { const parsed = JSON.parse(value); return Array.isArray(parsed) ? parsed.map(Number) : [] } catch { return [] } }
  return []
}
function parseSettings(value: unknown): Record<string, unknown> {
  if (value && typeof value === 'object') return value as Record<string, unknown>
  if (typeof value === 'string') { try { return JSON.parse(value) as Record<string, unknown> } catch { return {} } }
  return {}
}
function toItem(item: HomeworkAssignment): HomeworkItem {
  return {
    id: item.id, name: item.title, course: item.courseName, courseId: item.courseId,
    chapter: courseChapters.value.find(ch => ch.id === item.chapterId)?.title ?? '', chapterId: item.chapterId,
    sectionId: item.sectionId, classIds: parseIdList(item.classIds), settings: parseSettings(item.settings), description: item.description ?? '',
    count: 0, score: item.totalScore, deadline: item.deadline ?? '未设置', submitted: item.submissionCount ?? 0,
    total: 0, status: item.status === 'PUBLISHED' ? '进行中' : '未发布'
  }
}
async function loadResources() {
  const [courseData, classData] = await Promise.all([
    apiRequest<Array<{ id: number; title: string }>>(`/api/course/teacher/${auth.userId}?page=1&size=100`),
    apiRequest<{ list: ClassOption[] }>('/api/classes/list?pageNum=1&pageSize=100')
  ])
  availableCourses.value = courseData.map(c => ({ id: c.id, title: c.title }))
  classList.value = classData.list ?? []
  if (!selectedCourseId.value && availableCourses.value.length) {
    selectedCourseId.value = availableCourses.value[0].id
    await loadCourseStructure()
  }
}
async function loadCourseStructure() {
  if (!selectedCourseId.value) { courseChapters.value = []; return }
  const structure = await apiRequest<{ chapters: CourseChapter[] }>(`/api/course/${selectedCourseId.value}/structure`)
  courseChapters.value = structure.chapters ?? []
  selectedChapterId.value = courseChapters.value[0]?.id ?? null
  form.value.chapter = courseChapters.value[0]?.title ?? ''
  selectedSectionId.value = null
}
async function loadAssignments() {
  const items = await listTeacherAssignments()
  homeworkList.value = items.map(toItem)
  if (!currentId.value && homeworkList.value.length) selectHomework(homeworkList.value[0].id)
}
function selectHomework(id: number) {
  currentId.value = id
  isNew.value = false
  const item = homeworkList.value.find(x => x.id === id)
  if (!item) return
  form.value.name = item.name
  form.value.score = item.score || 100
  form.value.classes = [...item.classIds]
  description.value = item.description
  form.value.deadline = item.deadline ? new Date(item.deadline) : null
  const settings = item.settings
  form.value.allowLate = settings.allowLate === true
  form.value.allowRetake = settings.allowRetake === true
  form.value.retakeCount = Number(settings.retakeCount ?? 2)
  form.value.autoGrade = settings.autoGrade !== false
  form.value.shuffle = settings.shuffle === true
  form.value.noPaste = settings.noPaste === true
  form.value.showAnswer = settings.showAnswer === true
  form.value.showScore = settings.showScore !== false
  form.value.limitLength = settings.limitLength === true
  form.value.maxLength = Number(settings.maxLength ?? 2000)
  if (Array.isArray(settings.formats)) form.value.formats = settings.formats as string[]
  selectedCourseId.value = item.courseId
  selectedChapterId.value = item.chapterId ?? null
  selectedSectionId.value = item.sectionId ?? null
  void loadCourseStructure().then(() => {
    selectedChapterId.value = item.chapterId ?? null
    form.value.chapter = courseChapters.value.find(ch => ch.id === item.chapterId)?.title ?? ''
    selectedSectionId.value = item.sectionId ?? null
  })
}
function makePayload(): AssignmentPayload {
  const deadline = form.value.deadline ? form.value.deadline.toLocaleString('sv-SE').replace(' ', 'T') : null
  return {
    courseId: selectedCourseId.value!, chapterId: selectedChapterId.value, sectionId: selectedSectionId.value,
    title: form.value.name.trim(), description: description.value.trim(), totalScore: form.value.score,
    passScore: form.value.passScore, deadline, classIds: form.value.classes,
    settings: { allowLate: form.value.allowLate, allowRetake: form.value.allowRetake, retakeCount: form.value.retakeCount,
      autoGrade: form.value.autoGrade, shuffle: form.value.shuffle, noPaste: form.value.noPaste,
      showAnswer: form.value.showAnswer, showScore: form.value.showScore, limitLength: form.value.limitLength,
      maxLength: form.value.maxLength, formats: form.value.formats }
  }
}
async function saveSettings(publish: boolean) {
  if (!selectedCourseId.value) { ElMessage.warning('请先选择课程'); return }
  if (!form.value.classes.length) { ElMessage.warning('请至少选择一个班级'); return }
  if (!form.value.name.trim()) { ElMessage.warning('请输入作业名称'); return }
  try {
    const payload = makePayload()
    if (isNew.value) {
      currentId.value = await createAssignment(payload)
      isNew.value = false
    } else await updateAssignment(currentId.value, payload)
    if (publish) await publishAssignment(currentId.value)
    await loadAssignments()
    ElMessage.success(publish ? '作业已发布，学生端已同步' : '作业草稿已保存')
  } catch (error) { ElMessage.error(error instanceof Error ? error.message : '保存作业失败') }
}
function confirmCreate() {
  if (!newName.value.trim()) { ElMessage.warning('请输入作业名称'); return }
  if (createMode.value !== 'blank') { ElMessage.info('作业库和 AI 出题接入即将补齐'); return }
  isNew.value = true
  currentId.value = 0
  form.value.name = newName.value.trim()
  form.value.classes = []
  description.value = ''
  createDialog.value = false
  newName.value = ''
  ElMessage.success('已创建作业草稿，请配置内容和班级后保存')
}
onMounted(async () => {
  try {
    await loadResources()
    await loadAssignments()
    const target = homeworkContext.value
    if (target) {
      homeworkContext.value = null
      selectedCourseId.value = target.courseId
      await loadCourseStructure()
      selectedChapterId.value = target.chapterId
      selectedSectionId.value = target.sectionId
      form.value.chapter = courseChapters.value.find(ch => ch.id === target.chapterId)?.title ?? ''
      form.value.name = `${target.sectionTitle} 作业`
      form.value.classes = []
      description.value = target.description ?? ''
      currentId.value = 0
      isNew.value = true
    }
  }
  catch (error) { ElMessage.error(error instanceof Error ? error.message : '加载课程或班级失败') }
})
</script>







<style scoped>
.hw-layout { display: grid; grid-template-columns: 320px 1fr; gap: 16px; align-items: start; }
.list-col { position: sticky; top: 0; }
.hw-list { display: flex; flex-direction: column; gap: 8px; }
.hw-item { border: 1px solid var(--app-line); border-radius: 8px; padding: 12px; cursor: pointer; transition: border-color .15s ease, background .15s ease; }
.hw-item:hover { border-color: var(--el-color-primary-light-5); }
.hw-item.active { border-color: var(--el-color-primary); background: var(--el-color-primary-light-9); }
.h-row { display: flex; align-items: center; justify-content: space-between; gap: 8px; margin-bottom: 5px; }
.h-row strong { font-size: 13.5px; }
.hw-item .muted { display: block; margin-top: 2px; }
.setting-form :deep(.el-divider__text) { font-size: 13px; font-weight: 700; color: var(--app-ink); }
.random-box { border: 1px dashed var(--app-line); border-radius: 8px; padding: 12px 14px; margin-top: 12px; display: flex; flex-direction: column; gap: 10px; }
.random-row { display: flex; align-items: center; gap: 10px; font-size: 13px; }
.rule-grid { display: flex; flex-direction: column; }
.rule-row { display: flex; align-items: center; justify-content: space-between; gap: 16px; padding: 11px 0; border-bottom: 1px solid #f2f4f9; }
.rule-row:last-child { border-bottom: none; }
.r-label { font-size: 14px; font-weight: 600; }
@media (max-width: 960px) {
  .hw-layout { grid-template-columns: 1fr; }
  .list-col { position: static; }
}
</style>
