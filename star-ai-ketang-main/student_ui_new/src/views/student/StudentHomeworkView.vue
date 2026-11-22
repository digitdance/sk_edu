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
            <el-button v-else-if="row.status === 'submitted'" link size="small" @click="retake(row)">申请重做</el-button>
            <el-button v-else link type="primary" size="small" @click="viewResult(row)">查看批改</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 做作业 -->
    <el-dialog v-model="doDialog" :title="current?.name" width="640px" :close-on-click-modal="false">
      <el-alert type="warning" :closable="false" show-icon style="margin-bottom:14px"
        title="禁止粘贴 · 题目乱序 · 提交后立即显示客观题分数" />
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
        <p class="comment-box">结构完整，案例选取恰当；第 3 题注意完播率的计算口径，建议回看 3.1 节 05:40 处讲解。—— 王琳</p>
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
import { computed, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { UploadFile } from 'element-plus'
import { UploadCloud, FileText, X } from 'lucide-vue-next'

type Status = 'todo' | 'submitted' | 'graded'
interface Homework {
  id: number; name: string; course: string; deadline: string; urgent: boolean;
  status: Status; score: number | null; objScore: number | null; retakeLeft: number
  requireAttachment?: boolean; attachments?: string[]
}

const filter = ref('all')
const doDialog = ref(false)
const resultDrawer = ref(false)
const current = ref<Homework | null>(null)

const statusText: Record<Status, string> = { todo: '待提交', submitted: '已提交', graded: '已批改' }

const homework = ref<Homework[]>([
  { id: 1, name: '第 2 章作业 · 视频剪辑基础', course: '短视频制作与运营', deadline: '11-21 22:00', urgent: true, status: 'todo', score: null, objScore: null, retakeLeft: 2, requireAttachment: true },
  { id: 2, name: '3.1 课后思考 · 算法理解', course: '短视频制作与运营', deadline: '11-26 22:00', urgent: false, status: 'todo', score: null, objScore: null, retakeLeft: 2 },
  { id: 3, name: '第 1 章作业 · 平台认知测验', course: '短视频制作与运营', deadline: '11-14 22:00', urgent: false, status: 'graded', score: 88, objScore: 56, retakeLeft: 1 },
  { id: 4, name: '文案标题专项练习', course: '新媒体文案写作', deadline: '11-12 22:00', urgent: false, status: 'graded', score: 92, objScore: 60, retakeLeft: 0 },
  { id: 5, name: '2.2 剪辑实操作品提交', course: '短视频制作与运营', deadline: '11-18 22:00', urgent: false, status: 'submitted', score: null, objScore: null, retakeLeft: 2, requireAttachment: true, attachments: ['30s混剪作品_刘洋.mp4', '制作说明.pdf'] }
])

/* ---------- 作业附件上传 ---------- */
interface PickedFile { name: string; size: string }
const files = ref<PickedFile[]>([])
function formatSize(bytes: number) {
  if (bytes >= 1024 * 1024) return `${(bytes / 1024 / 1024).toFixed(1)} MB`
  return `${Math.max(1, Math.round(bytes / 1024))} KB`
}
function onPickFile(file: UploadFile) {
  if (!file.raw) return
  files.value.push({ name: file.name, size: formatSize(file.raw.size) })
  ElMessage.success(`「${file.name}」已加入待提交附件`)
}

const filtered = computed(() => homework.value.filter(h =>
  (filter.value === 'all' || h.status === filter.value) &&
  (courseFilter.value === 'all' || h.course === courseFilter.value)
))

/* ---------- 课程筛选 ---------- */
const courseFilter = ref('all')
const courseOptions = computed(() => [...new Set(homework.value.map(h => h.course))])

const questions = ref([
  { type: 'single', stem: '短视频平台冷启动阶段最看重的指标是？', options: ['点赞量', '完播率', '评论量', '转发量'], score: 4, my: '' },
  { type: 'single', stem: '「黄金三秒」原则指的是？', options: ['开头快速抛出核心看点', '前三秒必须出现人物', '前三秒留白', '前三秒展示 logo'], score: 4, my: '' },
  { type: 'text', stem: '结合案例，阐述账号垂直定位对涨粉效率的影响机制。', score: 10, my: '' }
])

const resultQuestions = [
  { stem: '冷启动阶段最看重的指标', right: true, got: 4, score: 4 },
  { stem: '「黄金三秒」原则', right: true, got: 4, score: 4 },
  { stem: '有效互动行为识别（多选）', right: false, got: 0, score: 3 },
  { stem: '简答：垂直定位影响机制', right: true, got: 8, score: 10 }
]

function openDo(row: Homework) {
  current.value = row
  questions.value.forEach(q => (q.my = ''))
  files.value = []
  doDialog.value = true
}
function submitHomework() {
  if (current.value?.requireAttachment && !files.value.length) {
    ElMessage.warning('该作业要求上传附件，请先上传后再提交')
    return
  }
  const undone = questions.value.filter(q => !q.my).length
  const doSubmit = () => {
    if (current.value) {
      current.value.status = 'submitted'
      if (files.value.length) current.value.attachments = files.value.map(f => f.name)
    }
    doDialog.value = false
    ElMessage.success(files.value.length
      ? `提交成功！已上传 ${files.value.length} 个附件，教师批阅后可查看成绩`
      : '提交成功！客观题得分 52 / 60，主观题待教师批阅')
  }
  if (undone) {
    ElMessageBox.confirm(`还有 ${undone} 题未作答，确认提交？`, '提交确认', { type: 'warning' })
      .then(doSubmit).catch(() => undefined)
  } else doSubmit()
}
function retake(row: Homework) {
  if (!row.retakeLeft) { ElMessage.warning('重做次数已用完'); return }
  ElMessageBox.confirm(`剩余重做次数 ${row.retakeLeft} 次，确认申请重做？`, '申请重做', { type: 'info' })
    .then(() => { row.retakeLeft -= 1; openDo(row) })
    .catch(() => undefined)
}
function viewResult(row: Homework) {
  current.value = row
  resultDrawer.value = true
}
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
