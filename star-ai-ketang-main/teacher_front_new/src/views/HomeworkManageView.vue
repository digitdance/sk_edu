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
          <el-form-item label="所属章节">
            <el-select v-model="form.chapter">
              <el-option v-for="c in chaptersList" :key="c" :label="c" :value="c" />
            </el-select>
          </el-form-item>
          <el-form-item label="发布班级">
            <el-select v-model="form.classes" multiple>
              <el-option v-for="c in classList" :key="c" :label="c" :value="c" />
            </el-select>
          </el-form-item>
          <el-form-item label="截止时间">
            <el-date-picker v-model="form.deadline" type="datetime" style="width:100%" />
          </el-form-item>
        </div>

        <el-divider content-position="left">题目来源</el-divider>
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
import { computed, ref } from 'vue'
import { ElMessage } from 'element-plus'

const emit = defineEmits<{ navigate: [id: string] }>()

const chaptersList = ['第 1 章 短视频认知', '第 2 章 拍摄与剪辑', '第 3 章 平台算法', '第 4 章 内容策划', '第 5 章 账号运营']
const classList = ['2024级软件1班', '2024级软件2班', '2023级数媒1班']

const homeworkList = ref([
  { id: 1, name: '第 2 章作业 · 视频剪辑基础', course: '短视频制作与运营', chapter: '第 2 章', count: 8, score: 100, deadline: '11-21 22:00', submitted: 36, total: 40, status: '进行中' },
  { id: 2, name: '第 1 章作业 · 平台认知测验', course: '短视频制作与运营', chapter: '第 1 章', count: 10, score: 100, deadline: '11-14 22:00', submitted: 40, total: 40, status: '已截止' },
  { id: 4, name: '文案标题专项练习', course: '新媒体文案写作', chapter: '第 2 章', count: 6, score: 60, deadline: '11-12 22:00', submitted: 38, total: 40, status: '已截止' },
  { id: 3, name: '第 3 章作业 · 算法理解（草稿）', course: '短视频制作与运营', chapter: '第 3 章', count: 0, score: 0, deadline: '未设置', submitted: 0, total: 40, status: '未发布' }
])

/* ---------- 课程筛选 ---------- */
const courseFilter = ref('all')
const courseOptions = computed(() => [...new Set(homeworkList.value.map(h => h.course))])
const filteredHomework = computed(() =>
  homeworkList.value.filter(h => courseFilter.value === 'all' || h.course === courseFilter.value)
)
const currentId = ref(1)
const createDialog = ref(false)
const createMode = ref('blank')
const newName = ref('')

const form = ref({
  name: '第 2 章作业 · 视频剪辑基础',
  chapter: '第 2 章 拍摄与剪辑',
  classes: ['2024级软件1班', '2024级软件2班'],
  deadline: new Date('2026-11-21T22:00:00'),
  source: 'pick',
  randomItems: [
    { type: '单选题', count: 5, score: 4 },
    { type: '多选题', count: 2, score: 8 },
    { type: '简答题', count: 2, score: 22 }
  ],
  score: 100,
  passScore: 60,
  allowLate: true,
  allowRetake: true,
  retakeCount: 2,
  autoGrade: true,
  shuffle: true,
  noPaste: true,
  showAnswer: false,
  showScore: true,
  limitLength: true,
  maxLength: 2000,
  formats: ['文本', '附件', '图片']
})

function selectHomework(id: number) {
  currentId.value = id
  const h = homeworkList.value.find(x => x.id === id)
  if (h) {
    form.value.name = h.name
    form.value.score = h.score || 100
  }
}
function saveSettings(publish: boolean) {
  if (publish) {
    ElMessage.success('作业已发布，学生端已同步，系统将在截止前 24 小时自动提醒未交学生')
    setTimeout(() => emit('navigate', 'grading'), 700)
  } else {
    ElMessage.success('作业设置已保存')
  }
}
function confirmCreate() {
  if (!newName.value.trim()) { ElMessage.warning('请输入作业名称'); return }
  const id = Date.now()
  homeworkList.value.unshift({
    id, name: newName.value.trim(), course: '短视频制作与运营', chapter: '第 1 章', count: 0, score: 100,
    deadline: '未设置', submitted: 0, total: 40, status: '未发布'
  })
  currentId.value = id
  form.value.name = newName.value.trim()
  createDialog.value = false
  newName.value = ''
  ElMessage.success('作业已创建，请完成设置后发布')
}
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
