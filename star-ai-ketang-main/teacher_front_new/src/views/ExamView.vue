<template>
  <div>
    <!-- 统计卡片 -->
    <div class="grid-4 stats-row">
      <div class="panel stat-mini"><span class="num mono">38</span><span class="lbl">考试总场次</span></div>
      <div class="panel stat-mini"><span class="num mono" style="color:#2f6bff">82.4</span><span class="lbl">全校平均分</span></div>
      <div class="panel stat-mini"><span class="num mono" style="color:#2fbf71">98</span><span class="lbl">本学期参考人次</span></div>
      <div class="panel stat-mini"><span class="num mono" style="color:#e6a23c">91%</span><span class="lbl">及格率</span></div>
    </div>

    <div class="panel">
      <div class="toolbar">
        <el-input v-model="keyword" placeholder="搜索考试名称" :prefix-icon="SearchIcon" style="width:220px" clearable />
        <el-select v-model="courseFilter" style="width:170px"><el-option label="All courses" value="all" /><el-option v-for="course in courseOptions" :key="course" :label="course" :value="course" /></el-select>
        <el-select v-model="statusFilter" style="width:120px">
          <el-option label="全部状态" value="all" />
          <el-option label="未开始" value="未开始" />
          <el-option label="进行中" value="进行中" />
          <el-option label="已结束" value="已结束" />
        </el-select>
        <span style="flex:1" />
        <el-button @click="ElMessage.info('章节测验随堂发起，不占考试场次')">发起章节测验</el-button>
        <el-button type="primary" @click="openCreate">新建考试</el-button>
      </div>

      <el-table :data="filteredExams" border stripe>
        <el-table-column prop="name" label="考试名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="course" label="所属课程" width="150" show-overflow-tooltip />
        <el-table-column label="考试时间" width="200">
          <template #default="{ row }">
            <span class="mono" style="font-size:12.5px">{{ row.start }} ~ {{ row.end }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="时长" width="80" align="center" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag size="small" effect="light" :type="row.status === '进行中' ? 'success' : row.status === '未开始' ? 'info' : 'warning'">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="attend" label="参考人数" width="90" align="center" />
        <el-table-column prop="avg" label="平均分" width="90" align="center">
          <template #default="{ row }">
            <span class="mono">{{ row.avg || '—' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" :disabled="row.status === '未开始'" @click="openAnalysis(row)">分析</el-button>
            <el-button link size="small" @click="editExam(row)">编辑</el-button>
            <el-button link size="small" @click="monitorExam(row)">监考</el-button>
            <el-button link type="danger" size="small" @click="deleteExam(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 新建考试 -->
    <el-dialog v-model="createDialog" :title="editingExam ? '编辑考试' : '新建考试'" width="640px">
      <el-form label-position="top">
        <div class="grid-2">
          <el-form-item label="考试名称">
            <el-input v-model="examForm.name" placeholder="例如：期末考试（线上）" />
          </el-form-item>
          <el-form-item label="Course"><el-select v-model="examForm.courseId"><el-option v-for="c in courses" :key="c.id" :label="c.title" :value="c.id" /></el-select></el-form-item>
          <el-form-item label="Classes"><el-select v-model="examForm.classes" multiple><el-option v-for="c in classList" :key="c.id" :label="c.className" :value="c.id" /></el-select></el-form-item>
          <el-form-item label="考试时长（分钟）">
            <el-input-number v-model="examForm.duration" :min="15" :max="300" style="width:100%" />
          </el-form-item>
          <el-form-item label="考试时间窗口">
            <el-date-picker v-model="examForm.range" type="datetimerange" range-separator="至"
              start-placeholder="开始时间" end-placeholder="结束时间" style="width:100%" />
          </el-form-item>
          <el-form-item label="Paper"><el-select v-model="examForm.paper" clearable><el-option v-for="p in papers" :key="p.id" :label="p.title" :value="p.id" /></el-select></el-form-item>
        </div>
        <div class="exam-switches">
          <div class="es-row">
            <div><div class="es-label">随机多套试卷</div><div class="muted">同场考试自动生成 A/B/C 卷，题目乱序</div></div>
            <el-switch v-model="examForm.multiPaper" />
          </div>
          <div class="es-row">
            <div><div class="es-label">自动开考 / 自动收卷</div><div class="muted">按时间窗口自动开放与强制交卷</div></div>
            <el-switch v-model="examForm.autoSwitch" />
          </div>
          <div class="es-row">
            <div><div class="es-label">防切屏监考</div><div class="muted">切屏 3 次警告，超过 5 次自动交卷</div></div>
            <el-switch v-model="examForm.antiSwitch" />
          </div>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="createDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmCreate">{{ editingExam ? '保存修改' : '发布考试' }}</el-button>
      </template>
    </el-dialog>

    <!-- 试卷分析抽屉 -->
    <el-drawer v-model="analysisDrawer" :title="`试卷分析 · ${analysisExam?.name || ''}`" size="620px">
      <template v-if="analysisExam">
        <div class="grid-4" style="gap:10px">
          <div class="panel stat-mini" style="margin:0; padding:14px"><span class="num mono" style="font-size:22px">{{ analysisExam.avg }}</span><span class="lbl">平均分</span></div>
          <div class="panel stat-mini" style="margin:0; padding:14px"><span class="num mono" style="font-size:22px">98</span><span class="lbl">最高分</span></div>
          <div class="panel stat-mini" style="margin:0; padding:14px"><span class="num mono" style="font-size:22px">51</span><span class="lbl">最低分</span></div>
          <div class="panel stat-mini" style="margin:0; padding:14px"><span class="num mono" style="font-size:22px">120<span style="font-size:12px"> 分钟</span></span><span class="lbl">平均用时</span></div>
        </div>

        <div class="panel-title" style="margin-top:18px"><h3>分数段分布</h3></div>
        <div class="bars">
          <div v-for="b in scoreDist" :key="b.label" class="bar-col">
            <div class="bar" :style="{ height: b.pct + '%' }" />
            <span class="bar-lbl">{{ b.label }}</span>
          </div>
        </div>

        <div class="panel-title" style="margin-top:18px"><h3>题目分析</h3><span class="extra">按正确率升序</span></div>
        <el-table :data="questionAnalysis" border size="small">
          <el-table-column prop="no" label="题号" width="60" align="center" />
          <el-table-column prop="stem" label="题干" min-width="200" show-overflow-tooltip />
          <el-table-column prop="type" label="题型" width="80" />
          <el-table-column label="正确率" width="130">
            <template #default="{ row }">
              <el-progress :percentage="row.rate" :stroke-width="6" :color="row.rate >= 80 ? '#2fbf71' : row.rate >= 60 ? '#e6a23c' : '#f56c6c'" />
            </template>
          </el-table-column>
          <el-table-column prop="diff" label="区分度" width="80" align="center" />
        </el-table>
      </template>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search as SearchIcon } from 'lucide-vue-next'
import { apiRequest } from '../api/client'
import { auth } from '../stores/auth'

interface CourseOption { id: number; title: string }
interface ClassOption { id: string; className: string }
interface PaperOption { id: number; title: string; totalScore?: number; durationMin?: number }
interface PaperPage { records: PaperOption[] }
interface Exam { id: number; name: string; course: string; courseId: number; paperId?: number; classIds?: number[]; startIso: string; endIso: string; start: string; end: string; duration: string; status: string; attend: number; avg: number }
const keyword = ref('')
const courseFilter = ref('all')
const statusFilter = ref('all')
const createDialog = ref(false)
const analysisDrawer = ref(false)
const analysisExam = ref<Exam | null>(null)
const exams = ref<Exam[]>([])
const courses = ref<CourseOption[]>([])
const classList = ref<ClassOption[]>([])
const papers = ref<PaperOption[]>([])
const editingExam = ref<Exam | null>(null)
const examForm = ref({ name: '', courseId: 0, classes: [] as string[], duration: 120, range: null as [Date, Date] | null, paper: 0, multiPaper: false, autoSwitch: true, antiSwitch: true })
const courseOptions = computed(() => courses.value.map(c => c.title))
const filteredExams = computed(() => exams.value.filter(e => (courseFilter.value === 'all' || e.course === courseFilter.value) && (statusFilter.value === 'all' || e.status === statusFilter.value) && e.name.includes(keyword.value)))
const scoreDist = [{ label: '<60', pct: 0 }, { label: '60-70', pct: 0 }, { label: '70-80', pct: 0 }, { label: '80-90', pct: 0 }, { label: '90+', pct: 0 }]
const questionAnalysis = ref<{ no: number; stem: string; type: string; rate: number; diff: number }[]>([])

function displayTime(value: string) { return value ? new Date(value).toLocaleString('zh-CN', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit', hour12: false }) : '—' }
function localDateTime(value: Date) { const pad = (n: number) => String(n).padStart(2, '0'); return `${value.getFullYear()}-${pad(value.getMonth() + 1)}-${pad(value.getDate())}T${pad(value.getHours())}:${pad(value.getMinutes())}:${pad(value.getSeconds())}` }
function mapExam(row: Record<string, unknown>): Exam {
  const start = String(row.start ?? ''), end = String(row.end ?? '')
  const now = Date.now(), startAt = new Date(start).getTime(), endAt = new Date(end).getTime()
  let classIds: number[] = []; try { classIds = JSON.parse(String(row.classIds ?? '[]')).map(Number) } catch { /* retain empty selection */ }
  return { id: Number(row.id), name: String(row.name), course: String(row.course), courseId: Number(row.courseId), paperId: Number(row.paperId) || undefined, classIds,
    startIso: start, endIso: end,
    start: displayTime(start), end: displayTime(end), duration: `${row.duration} 分钟`, status: now < startAt ? '未开始' : now <= endAt ? '进行中' : '已结束', attend: Number(row.attend ?? 0), avg: Number(row.avg ?? 0) }
}
async function load() {
  try {
    const [examRows, courseRows, classPage, paperRows] = await Promise.all([
      apiRequest<Record<string, unknown>[]>('/api/exams'),
      apiRequest<CourseOption[]>(`/api/course/teacher/${auth.userId}?page=1&size=100`),
      apiRequest<{ list: ClassOption[] }>('/api/classes/list?pageNum=1&pageSize=100'),
      apiRequest<PaperPage>('/api/exam-papers?pageNum=1&pageSize=100')
    ])
    exams.value = examRows.map(mapExam); courses.value = courseRows; classList.value = classPage.list ?? []; papers.value = paperRows.records ?? []
    if (!examForm.value.courseId) examForm.value.courseId = courses.value[0]?.id ?? 0
    if (!examForm.value.paper) examForm.value.paper = papers.value[0]?.id ?? 0
  } catch (error) { ElMessage.error(error instanceof Error ? error.message : '考试数据加载失败') }
}
function openCreate() { editingExam.value = null; examForm.value = { name: '', courseId: courses.value[0]?.id ?? 0, classes: [], duration: 120, range: null, paper: papers.value[0]?.id ?? 0, multiPaper: false, autoSwitch: true, antiSwitch: true }; createDialog.value = true }
async function confirmCreate() {
  if (!examForm.value.name.trim() || !examForm.value.courseId || !examForm.value.range || !examForm.value.classes.length) { ElMessage.warning('请完整填写考试名称、课程、班级和时间'); return }
  try {
    const [startsAt, endsAt] = examForm.value.range
    const body = { title: examForm.value.name.trim(), courseId: examForm.value.courseId, paperId: examForm.value.paper || null, startsAt: localDateTime(startsAt), endsAt: localDateTime(endsAt), durationMinutes: examForm.value.duration, classIds: examForm.value.classes.map(Number), settings: { multiPaper: examForm.value.multiPaper, autoSwitch: examForm.value.autoSwitch, antiSwitch: examForm.value.antiSwitch } }
    if (editingExam.value) await apiRequest(`/api/exams/${editingExam.value.id}`, { method: 'PUT', body })
    else await apiRequest('/api/exams', { method: 'POST', body })
    createDialog.value = false; await load(); ElMessage.success('考试安排已保存')
  } catch (error) { ElMessage.error(error instanceof Error ? error.message : '保存考试失败') }
}
function editExam(row: Exam) { editingExam.value = row; examForm.value = { ...examForm.value, name: row.name, courseId: row.courseId, paper: row.paperId ?? 0, classes: (row.classIds ?? []).map(String), duration: parseInt(row.duration) || 120, range: [new Date(row.startIso), new Date(row.endIso)] }; createDialog.value = true }
function monitorExam(row: Exam) { ElMessage.info(`「${row.name}」已参考 ${row.attend} 人；切屏事件统计将在监考接口接入后显示。`) }
async function deleteExam(row: Exam) {
  try { await ElMessageBox.confirm(`确认删除「${row.name}」？`, '删除考试', { type: 'warning' }); await apiRequest(`/api/exams/${row.id}`, { method: 'DELETE' }); await load(); ElMessage.success('考试已删除') }
  catch (error) { if (error instanceof Error) ElMessage.error(error.message) }
}
function openAnalysis(row: Exam) { analysisExam.value = row; analysisDrawer.value = true }
onMounted(() => { void load() })
</script>






<style scoped>
.stats-row .panel { margin-bottom: 0; }
.stats-row { margin-bottom: 16px; }
.toolbar { display: flex; gap: 10px; align-items: center; margin-bottom: 14px; flex-wrap: wrap; }
.exam-switches { border: 1px solid var(--app-line); border-radius: 8px; padding: 4px 14px; }
.es-row { display: flex; align-items: center; justify-content: space-between; gap: 16px; padding: 11px 0; border-bottom: 1px solid #f2f4f9; }
.es-row:last-child { border-bottom: none; }
.es-label { font-size: 14px; font-weight: 600; }
</style>
