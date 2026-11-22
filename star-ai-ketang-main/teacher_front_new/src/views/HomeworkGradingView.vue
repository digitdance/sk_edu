<template>
  <div>
    <!-- 顶部统计与筛选 -->
    <div class="panel">
      <div class="grade-head">
        <div class="stat-row">
          <div class="stat-mini"><span class="num mono">38</span><span class="lbl">已提交</span></div>
          <el-divider direction="vertical" />
          <div class="stat-mini"><span class="num mono" style="color:#e6a23c">12</span><span class="lbl">待批阅</span></div>
          <el-divider direction="vertical" />
          <div class="stat-mini"><span class="num mono" style="color:#2fbf71">26</span><span class="lbl">已批阅</span></div>
        </div>
        <div class="filter-row">
          <el-select v-model="homeworkFilter" style="width:230px">
            <el-option label="第 2 章作业 · 视频剪辑基础" value="hw2" />
            <el-option label="第 1 章作业 · 平台认知测验" value="hw1" />
          </el-select>
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
            <el-button link type="primary" size="small" @click="previewAttachment(f.name)">预览</el-button>
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
              <el-input-number v-model="subjectiveScore" :min="0" :max="10" :step="0.5" />
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
import { computed, ref } from 'vue'
import { downloadText } from '../utils/download'
import { ElMessage } from 'element-plus'
import { Search as SearchIcon, BellRing, Sparkles, Paperclip } from 'lucide-vue-next'
import { previewDocument } from '../utils/kkPreview'

/** 预览学生上传的附件：文档走 kkFileView 在线预览，其它类型提示下载查看 */
function previewAttachment(name: string) {
  if (previewDocument(name)) ElMessage.success(`正在通过 kkFileView 打开「${name}」在线预览`)
  else ElMessage.info('该类型暂不支持在线预览，请下载后查看')
}

interface Attachment { name: string; size: string }
interface Student {
  id: number; name: string; no: string; status: 'pending' | 'graded' | 'missing';
  score: number; objective: number; submitTime: string; aiScore: number; aiComment: string; answer: string
  attachments?: Attachment[]
}

const homeworkFilter = ref('hw2')
const statusFilter = ref('all')
const keyword = ref('')
const currentId = ref(1)
const subjectiveScore = ref(8)
const comment = ref('')

const students = ref<Student[]>([
  { id: 1, name: '刘洋', no: '20240115', status: 'pending', score: 0, objective: 52, submitTime: '11-19 20:14', aiScore: 8, aiComment: '结构完整，覆盖「垂直定位→内容匹配→粉丝转化」链路；建议补充具体数据案例。', answer: '账号垂直定位能让平台算法更精准地识别内容标签，从而把视频推荐给真正感兴趣的人群。垂直账号的粉丝画像集中，完播率和互动率更高，进入更大流量池的概率也随之提升……', attachments: [{ name: '30s混剪作品_刘洋.mp4', size: '86.2 MB' }, { name: '制作说明_刘洋.docx', size: '18 KB' }] },
  { id: 2, name: '陈思雨', no: '20240116', status: 'graded', score: 88, objective: 56, submitTime: '11-19 18:02', aiScore: 9, aiComment: '论述充分，案例翔实。', answer: '（已批阅）垂直定位的本质是降低算法理解成本……', attachments: [{ name: '混剪作品_陈思雨.mp4', size: '64.5 MB' }] },
  { id: 3, name: '王子涵', no: '20240117', status: 'pending', score: 0, objective: 48, submitTime: '11-19 21:40', aiScore: 7, aiComment: '要点齐全但缺少个人分析。', answer: '垂直账号更容易被算法打上明确标签……', attachments: [{ name: '剪辑作业_王子涵.mp4', size: '71.8 MB' }, { name: '分镜脚本_王子涵.pdf', size: '320 KB' }] },
  { id: 4, name: '李梦琪', no: '20240118', status: 'graded', score: 95, objective: 60, submitTime: '11-18 16:33', aiScore: 10, aiComment: '结构、论据、数据俱佳。', answer: '（已批阅，范文）从流量池机制看……', attachments: [{ name: '混剪作品_李梦琪.mp4', size: '92.4 MB' }] },
  { id: 5, name: '张浩然', no: '20240119', status: 'missing', score: 0, objective: 0, submitTime: '—', aiScore: 0, aiComment: '', answer: '' },
  { id: 6, name: '赵可欣', no: '20240120', status: 'pending', score: 0, objective: 44, submitTime: '11-19 22:01', aiScore: 6, aiComment: '观点正确但论述较浅，建议举例。', answer: '我觉得垂直定位很重要……', attachments: [{ name: '剪辑作业_赵可欣.mp4', size: '55.1 MB' }] }
])

const optionStats = [
  { key: 'A', text: '点赞量', pct: 12, right: false },
  { key: 'B', text: '完播率', pct: 63, right: true },
  { key: 'C', text: '评论量', pct: 18, right: false },
  { key: 'D', text: '转发量', pct: 7, right: false }
]

const filteredStudents = computed(() =>
  students.value.filter(s =>
    (statusFilter.value === 'all' || s.status === statusFilter.value) &&
    (s.name.includes(keyword.value) || s.no.includes(keyword.value))
  )
)
const current = computed(() => filteredStudents.value.find(s => s.id === currentId.value) || filteredStudents.value[0] || null)
const currentIndex = computed(() => (current.value ? filteredStudents.value.findIndex(s => s.id === current.value!.id) : 0))

function selectStudent(s: Student) {
  currentId.value = s.id
  subjectiveScore.value = s.status === 'graded' ? s.score - s.objective : s.aiScore || 0
  comment.value = ''
}
function go(step: number) {
  const next = filteredStudents.value[currentIndex.value + step]
  if (next) selectStudent(next)
}
function adoptAi() {
  subjectiveScore.value = current.value?.aiScore ?? 0
  comment.value = current.value?.aiComment ?? ''
  ElMessage.success('已采用 AI 参考评分，可继续微调')
}
function saveGrade(next: boolean) {
  if (!current.value) return
  current.value.status = 'graded'
  current.value.score = current.value.objective + subjectiveScore.value
  ElMessage.success(`已保存 ${current.value.name} 的批阅结果：${current.value.score} 分`)
  if (next) go(1)
}
function downloadFile(f: { name: string }) {
  downloadText(f.name, `学生作业附件：${f.name}\n（接入对象存储后替换为真实文件流）`)
  ElMessage.success(`开始下载「${f.name}」`)
}
function remindMissing() { ElMessage.success('已向 2 名未提交学生推送提醒') }
function autoGradeAll() { ElMessage.success('客观题已全部自动判分：38 份，平均 51.2 分') }
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
