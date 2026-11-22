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
        <el-select v-model="courseFilter" style="width:170px">
          <el-option label="全部课程" value="all" />
          <el-option label="短视频制作与运营" value="短视频制作与运营" />
          <el-option label="新媒体文案写作" value="新媒体文案写作" />
        </el-select>
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
          <el-form-item label="所属课程">
            <el-select v-model="examForm.course">
              <el-option label="短视频制作与运营" value="短视频制作与运营" />
              <el-option label="新媒体文案写作" value="新媒体文案写作" />
            </el-select>
          </el-form-item>
          <el-form-item label="参考班级">
            <el-select v-model="examForm.classes" multiple>
              <el-option v-for="c in classList" :key="c" :label="c" :value="c" />
            </el-select>
          </el-form-item>
          <el-form-item label="考试时长（分钟）">
            <el-input-number v-model="examForm.duration" :min="15" :max="300" style="width:100%" />
          </el-form-item>
          <el-form-item label="考试时间窗口">
            <el-date-picker v-model="examForm.range" type="datetimerange" range-separator="至"
              start-placeholder="开始时间" end-placeholder="结束时间" style="width:100%" />
          </el-form-item>
          <el-form-item label="试卷">
            <el-select v-model="examForm.paper">
              <el-option label="期末考试模拟卷（40 题 / 100 分）" value="p1" />
              <el-option label="期中模拟卷 A 卷（30 题 / 100 分）" value="p2" />
              <el-option label="＋ 随机组卷" value="new" />
            </el-select>
          </el-form-item>
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
import { computed, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search as SearchIcon } from 'lucide-vue-next'

interface Exam {
  id: number; name: string; course: string; start: string; end: string;
  duration: string; status: string; attend: number; avg: number
}

const keyword = ref('')
const courseFilter = ref('all')
const statusFilter = ref('all')
const createDialog = ref(false)
const analysisDrawer = ref(false)
const analysisExam = ref<Exam | null>(null)
const classList = ['2024级软件1班', '2024级软件2班', '2023级数媒1班']

const exams = ref<Exam[]>([
  { id: 1, name: '第 3 章章节测验', course: '短视频制作与运营', start: '11-24 10:00', end: '11-24 22:00', duration: '30 分钟', status: '未开始', attend: 0, avg: 0 },
  { id: 2, name: '期中考试（线上）', course: '短视频制作与运营', start: '11-10 14:00', end: '11-10 16:00', duration: '120 分钟', status: '已结束', attend: 78, avg: 82.4 },
  { id: 3, name: '第 2 章章节测验', course: '短视频制作与运营', start: '11-05 19:00', end: '11-06 22:00', duration: '25 分钟', status: '已结束', attend: 80, avg: 86.1 },
  { id: 4, name: '新媒体文案 · 随堂测', course: '新媒体文案写作', start: '11-19 16:00', end: '11-19 18:00', duration: '20 分钟', status: '进行中', attend: 36, avg: 0 }
])

const examForm = ref({
  name: '', course: '短视频制作与运营', classes: [] as string[],
  duration: 120, range: null as [Date, Date] | null, paper: 'p1',
  multiPaper: true, autoSwitch: true, antiSwitch: true
})

const filteredExams = computed(() =>
  exams.value.filter(e =>
    (courseFilter.value === 'all' || e.course === courseFilter.value) &&
    (statusFilter.value === 'all' || e.status === statusFilter.value) && e.name.includes(keyword.value)
  )
)

const scoreDist = [
  { label: '<60', pct: 14 }, { label: '60-70', pct: 22 }, { label: '70-80', pct: 38 },
  { label: '80-90', pct: 56 }, { label: '90+', pct: 30 }
]
const questionAnalysis = [
  { no: 12, stem: '平台完播率的计算公式', type: '填空', rate: 42, diff: 0.61 },
  { no: 27, stem: 'J-cut 转场技巧判断', type: '判断', rate: 58, diff: 0.45 },
  { no: 8, stem: '有效互动行为识别', type: '多选', rate: 66, diff: 0.52 },
  { no: 3, stem: '黄金三秒原则', type: '判断', rate: 91, diff: 0.28 },
  { no: 1, stem: '冷启动流量池核心指标', type: '单选', rate: 94, diff: 0.22 }
]

function openCreate() { createDialog.value = true }
const editingExam = ref<Exam | null>(null)
function confirmCreate() {
  if (!examForm.value.name.trim()) { ElMessage.warning('请输入考试名称'); return }
  if (editingExam.value) {
    Object.assign(editingExam.value, {
      name: examForm.value.name.trim(), course: examForm.value.course,
      duration: examForm.value.duration + ' 分钟'
    })
    createDialog.value = false
    editingExam.value = null
    ElMessage.success('考试信息已保存，学生端同步更新')
    return
  }
  exams.value.unshift({
    id: Date.now(), name: examForm.value.name.trim(), course: examForm.value.course,
    start: '待定', end: '待定', duration: examForm.value.duration + ' 分钟',
    status: '未开始', attend: 0, avg: 0
  })
  createDialog.value = false
  ElMessage.success(`考试已发布${examForm.value.multiPaper ? '，已启用随机 A/B 卷' : ''}，学生端同步可见`)
}
function editExam(row: Exam) {
  editingExam.value = row
  examForm.value = {
    ...examForm.value,
    name: row.name, course: row.course,
    duration: parseInt(row.duration) || 120
  }
  createDialog.value = true
}
function monitorExam(row: Exam) {
  ElMessage.info(`「${row.name}」实时监考台：在线 36 人，切屏告警 2 人`)
}
function deleteExam(row: Exam) {
  ElMessageBox.confirm(`确认删除「${row.name}」？已产生的成绩数据将保留。`, '删除考试', { type: 'warning' })
    .then(() => {
      exams.value = exams.value.filter(e => e.id !== row.id)
      ElMessage.success('已删除')
    })
    .catch(() => undefined)
}
function openAnalysis(row: Exam) {
  analysisExam.value = row
  analysisDrawer.value = true
}
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
