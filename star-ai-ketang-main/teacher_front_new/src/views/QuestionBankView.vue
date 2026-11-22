<template>
  <div>
    <div class="panel">
      <el-tabs v-model="tab">
        <!-- ================= 题库 ================= -->
        <el-tab-pane label="题库" name="questions">
          <div class="toolbar">
            <el-select v-model="qType" placeholder="题型" style="width:110px">
              <el-option label="全部题型" value="all" />
              <el-option v-for="t in questionTypes" :key="t" :label="t" :value="t" />
            </el-select>
            <el-select v-model="qDiff" placeholder="难度" style="width:110px">
              <el-option label="全部难度" value="all" />
              <el-option label="简单" value="简单" />
              <el-option label="中等" value="中等" />
              <el-option label="困难" value="困难" />
            </el-select>
            <el-select v-model="qChapter" placeholder="章节" style="width:170px">
              <el-option label="全部章节" value="all" />
              <el-option v-for="c in chaptersList" :key="c" :label="c" :value="c" />
            </el-select>
            <el-input v-model="qKeyword" placeholder="搜索题干关键词" :prefix-icon="SearchIcon" style="width:200px" clearable />
            <span style="flex:1" />
            <el-button @click="importDialog = true">模板导入</el-button>
            <el-button @click="exportTemplate">模板导出</el-button>
            <el-button type="primary" @click="openAddQuestion">新增题目</el-button>
          </div>

          <el-table ref="qTableRef" :data="filteredQuestions" border stripe @selection-change="s => selectedQuestions = s">
            <el-table-column type="selection" width="42" />
            <el-table-column label="题干" min-width="320" show-overflow-tooltip>
              <template #default="{ row }">
                <span class="q-stem">{{ row.stem }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="type" label="题型" width="90">
              <template #default="{ row }">
                <el-tag size="small" effect="plain">{{ row.type }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="difficulty" label="难度" width="80">
              <template #default="{ row }">
                <el-tag size="small" effect="light" :type="row.difficulty === '简单' ? 'success' : row.difficulty === '中等' ? 'warning' : 'danger'">
                  {{ row.difficulty }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="chapter" label="所属章节" width="150" show-overflow-tooltip />
            <el-table-column prop="used" label="使用次数" width="90" align="center" />
            <el-table-column label="操作" width="130" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" size="small" @click="editQuestion(row)">编辑</el-button>
                <el-button link type="danger" size="small" @click="deleteQuestion(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="table-foot">
            <div class="sel-info">
              <template v-if="selectedQuestions.length">
                已选 <strong>{{ selectedQuestions.length }}</strong> 题 ·
                合计 {{ selectedScore }} 分
                <el-button size="small" type="primary" style="margin-left:10px" @click="openRandomPaper">生成试卷</el-button>
                <el-button size="small" link @click="qTableRef?.clearSelection()">清空</el-button>
              </template>
              <span v-else class="muted">勾选题目后可快速组卷</span>
            </div>
            <el-pagination layout="total, prev, pager, next" :total="filteredQuestions.length" :page-size="10" />
          </div>
        </el-tab-pane>

        <!-- ================= 作业库 ================= -->
        <el-tab-pane label="作业库" name="homeworkLib">
          <div class="toolbar">
            <el-input placeholder="搜索作业" :prefix-icon="SearchIcon" style="width:220px" />
            <span style="flex:1" />
            <el-button type="primary" @click="goHomework">新建作业</el-button>
          </div>
          <el-table :data="homeworkLib" border stripe>
            <el-table-column prop="name" label="作业名称" min-width="220" />
            <el-table-column prop="chapter" label="所属章节" width="170" />
            <el-table-column prop="count" label="题目数" width="80" align="center" />
            <el-table-column prop="score" label="总分" width="80" align="center" />
            <el-table-column prop="used" label="引用次数" width="90" align="center" />
            <el-table-column label="操作" width="180" fixed="right">
              <template #default>
                <el-button link type="primary" size="small">引用到作业</el-button>
                <el-button link size="small">编辑</el-button>
                <el-button link type="danger" size="small">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- ================= 试卷库 ================= -->
        <el-tab-pane label="试卷库" name="papers">
          <div class="toolbar">
            <el-input placeholder="搜索试卷" :prefix-icon="SearchIcon" style="width:220px" />
            <span style="flex:1" />
            <el-button @click="openRandomPaper">随机组卷</el-button>
            <el-button type="primary" @click="openManualPaper">手动组卷</el-button>
          </div>
          <el-table :data="papers" border stripe>
            <el-table-column prop="name" label="试卷名称" min-width="220" />
            <el-table-column prop="count" label="题数" width="70" align="center" />
            <el-table-column prop="score" label="总分" width="70" align="center" />
            <el-table-column prop="source" label="组卷方式" width="110">
              <template #default="{ row }">
                <el-tag size="small" effect="light" :type="row.source === '随机组卷' ? 'warning' : 'primary'">{{ row.source }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="used" label="使用次数" width="90" align="center" />
            <el-table-column prop="time" label="创建时间" width="110" />
            <el-table-column label="操作" width="170" fixed="right">
              <template #default>
                <el-button link type="primary" size="small" @click="previewPaper(row)">预览</el-button>
                <el-button link size="small">复制</el-button>
                <el-button link type="danger" size="small">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 试卷预览 -->
    <el-dialog :model-value="!!paperPreview" :title="paperPreview?.name ?? '试卷预览'" width="560px" @close="paperPreview = null">
      <template v-if="paperPreview">
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="组卷方式">{{ paperPreview.source }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ paperPreview.time }}</el-descriptions-item>
          <el-descriptions-item label="题目数量">{{ paperPreview.count }} 题</el-descriptions-item>
          <el-descriptions-item label="总分">{{ paperPreview.score }} 分</el-descriptions-item>
          <el-descriptions-item label="使用次数">{{ paperPreview.used }} 次</el-descriptions-item>
          <el-descriptions-item label="答案页">含答案与解析</el-descriptions-item>
        </el-descriptions>
        <div class="paper-structure">
          <div class="ps-title">题型结构</div>
          <div class="ps-row" v-for="row in paperStructure" :key="row.type">
            <span>{{ row.type }}</span>
            <el-progress :percentage="row.pct" :stroke-width="8" style="flex:1" />
            <b>{{ row.count }} 题</b>
          </div>
        </div>
      </template>
      <template #footer>
        <el-button @click="paperPreview = null">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 新增/编辑题目 -->
    <el-dialog v-model="questionDialog" :title="editingQuestion ? '编辑题目' : '新增题目'" width="620px">
      <el-form label-position="top">
        <div style="display:flex; gap:16px; flex-wrap:wrap">
          <el-form-item label="题型" style="min-width:200px">
            <el-radio-group v-model="questionForm.type">
              <el-radio-button v-for="t in questionTypes" :key="t" :value="t">{{ t }}</el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="难度">
            <el-rate v-model="questionForm.diff" :max="3" show-text :texts="['简单', '中等', '困难']" />
          </el-form-item>
        </div>
        <el-form-item label="题干">
          <el-input v-model="questionForm.stem" type="textarea" :rows="3" placeholder="输入题目内容，支持插入图片与公式" />
          <el-button link type="primary" size="small" style="margin-top:4px" @click="formulaDialog = true">
            ∑ 插入数学 / 化学公式
          </el-button>
        </el-form-item>
        <el-form-item v-if="questionForm.type !== '简答题'" label="选项（每行一个，判断题填 正确/错误）">
          <el-input v-model="questionForm.options" type="textarea" :rows="4" placeholder="A. 选项一&#10;B. 选项二&#10;C. 选项三&#10;D. 选项四" />
        </el-form-item>
        <div style="display:flex; gap:16px; flex-wrap:wrap">
          <el-form-item label="正确答案" style="flex:1; min-width:180px">
            <el-input v-model="questionForm.answer" placeholder="如：B / ABC / 正确 / 参考答案要点" />
          </el-form-item>
          <el-form-item label="分值">
            <el-input-number v-model="questionForm.score" :min="1" :max="50" />
          </el-form-item>
          <el-form-item label="所属章节" style="min-width:180px">
            <el-select v-model="questionForm.chapter">
              <el-option v-for="c in chaptersList" :key="c" :label="c" :value="c" />
            </el-select>
          </el-form-item>
        </div>
        <el-form-item label="答案解析">
          <el-input v-model="questionForm.analysis" type="textarea" :rows="2" placeholder="可选，学生提交后可见" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="questionDialog = false">取消</el-button>
        <el-button type="primary" @click="saveQuestion">保存题目</el-button>
      </template>
    </el-dialog>

    <!-- 模板导入 -->
    <el-dialog v-model="importDialog" title="模板导入题目" width="520px">
      <el-steps :active="1" align-center style="margin-bottom:20px">
        <el-step title="下载模板" />
        <el-step title="上传文件" />
        <el-step title="校验入库" />
      </el-steps>
      <el-upload drag :auto-upload="false" :show-file-list="false" :on-change="onImportPick">
        <div style="padding:16px 0 6px; text-align:center">
          <FileUp :size="36" style="color: var(--el-color-primary)" />
          <p style="margin:8px 0 0; font-size:13px">拖拽 Excel 文件到此处，或 <em>点击上传</em></p>
          <p class="muted">单次最多导入 500 题，字段：题型 / 题干 / 选项 / 答案 / 解析 / 分值</p>
        </div>
      </el-upload>
      <el-alert v-if="importFile" type="success" :closable="false" style="margin-top:12px"
        :title="`已选择「${importFile}」，校验通过：识别 32 题，无格式错误。`" />
      <template #footer>
        <el-button link type="primary" @click="downloadTemplate">下载导入模板</el-button>
        <span style="flex:1" />
        <el-button @click="importDialog = false">取消</el-button>
        <el-button type="primary" :disabled="!importFile" @click="confirmImport">开始导入</el-button>
      </template>
    </el-dialog>

    <!-- 随机组卷 -->
    <el-dialog v-model="randomDialog" title="随机组卷" width="620px">
      <el-form label-position="top">
        <el-form-item label="试卷名称">
          <el-input v-model="paperForm.name" placeholder="例如：第 3 章章节测验（A/B 卷）" />
        </el-form-item>
        <el-form-item label="抽题范围">
          <el-checkbox-group v-model="paperForm.chapters">
            <el-checkbox v-for="c in chaptersList" :key="c" :value="c">{{ c.replace('短视', '') }}</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="题型与数量">
          <table class="qp-table">
            <thead>
              <tr><th>题型</th><th>抽题数</th><th>每题分值</th><th>小计</th></tr>
            </thead>
            <tbody>
              <tr v-for="row in paperForm.items" :key="row.type">
                <td>{{ row.type }}</td>
                <td><el-input-number v-model="row.count" :min="0" :max="30" size="small" /></td>
                <td><el-input-number v-model="row.score" :min="1" :max="20" size="small" /></td>
                <td class="mono">{{ row.count * row.score }} 分</td>
              </tr>
            </tbody>
          </table>
        </el-form-item>
        <el-form-item :label="`难度分布（困难题占比 ${paperForm.hardPct}%）`">
          <el-slider v-model="paperForm.hardPct" :min="0" :max="60" :format-tooltip="v => v + '%'" />
        </el-form-item>
        <div class="paper-summary">
          <span>总题数 <strong class="mono">{{ totalCount }}</strong></span>
          <span>总分 <strong class="mono">{{ totalScore }}</strong></span>
          <span>预计用时 <strong class="mono">{{ Math.max(10, totalCount * 2) }} 分钟</strong></span>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="randomDialog = false">取消</el-button>
        <el-button @click="generatePaper(false)">仅保存试卷</el-button>
        <el-button type="primary" @click="generatePaper(true)">生成并发布为测验</el-button>
      </template>
    </el-dialog>

    <!-- ============ 手动组卷 · 组卷管理 ============ -->
    <el-dialog v-model="manualDialog" title="组卷管理" width="1140px" top="3vh" :close-on-click-modal="false" class="manual-dialog">
      <div class="manual-wrap">
        <!-- 左侧：大题型管理 -->
        <aside class="ms-side">
          <div v-for="(sec, si) in manualSections" :key="sec.type" class="ms-sec">
            <div class="ms-sec-head">
              <strong>{{ sec.type }}</strong>
              <span class="ms-tools">
                <el-button link :icon="ArrowUp" :disabled="si === 0" @click="moveSection(si, -1)" />
                <el-button link :icon="ArrowDown" :disabled="si === manualSections.length - 1" @click="moveSection(si, 1)" />
                <el-button link :icon="Trash2" @click="removeSection(si)" />
              </span>
            </div>
            <p class="muted">共{{ sec.questions.length }}题，共{{ sectionScore(sec) }}分数</p>
            <div class="ms-each">
              每题 <el-input-number v-model="sec.each" :min="0.5" :max="50" :step="0.5" size="small" style="width:92px" @change="applyEachScore(sec)" /> 分
            </div>
          </div>
          <div class="ms-total">
            <span>总题数：<strong class="mono">{{ manualTotalCount }}</strong>题</span>
            <span>当前总分：<strong class="mono">{{ manualTotalScore }}</strong>分</span>
          </div>
        </aside>

        <!-- 右侧：试卷内容 -->
        <div class="ms-main">
          <el-input v-model="manualName" class="ms-title" placeholder="请输入试卷名称，如：项目一·任务二 课后习题" />
          <div v-for="(sec, si) in manualSections" :key="sec.type" class="ms-block">
            <div class="ms-block-head">
              <el-select :model-value="sec.type" size="small" style="width:106px"
                @update:model-value="v => changeSectionType(sec, v as string)">
                <el-option v-for="t in addableTypes" :key="t" :label="t" :value="t"
                  :disabled="t !== sec.type && manualSections.some(s => s.type === t)" />
              </el-select>
              <strong class="ms-block-type">{{ sec.type }}</strong>
              <el-button type="primary" size="small" @click="openPicker(si)">添加试题</el-button>
            </div>

            <div v-for="(q, qi) in sec.questions" :key="q.id" class="ms-q">
              <div class="ms-q-main">
                <p class="ms-q-stem">{{ qi + 1 }}.{{ q.stem }}</p>
                <div v-if="q.options?.length" class="ms-q-opts">
                  <div v-for="(o, oi) in q.options" :key="oi" class="ms-q-opt">
                    <span class="opt-letter" :class="{ right: q.answer?.includes(letter(oi)) }">{{ letter(oi) }}</span>
                    <span :class="{ 'opt-right-text': q.answer?.includes(letter(oi)) }">{{ o }}</span>
                  </div>
                </div>
                <p class="ms-q-ans">答案：{{ q.answer || '—' }}</p>
                <p class="ms-q-ana">解析：{{ q.analysis || '—' }}</p>
              </div>
              <div class="ms-q-tools">
                <el-input-number v-model="q.pscore" :min="0.5" :max="50" :step="0.5" size="small" style="width:86px" />
                <span class="muted" style="font-size:12px">分</span>
                <el-button link :icon="ArrowUp" :disabled="qi === 0" @click="moveQuestion(sec, qi, -1)" />
                <el-button link :icon="ArrowDown" :disabled="qi === sec.questions.length - 1" @click="moveQuestion(sec, qi, 1)" />
                <el-button link :icon="Trash2" @click="removeQuestion(sec, qi)" />
              </div>
            </div>

            <div v-if="!sec.questions.length" class="ms-empty muted">
              此大题还未添加试题，请点击"<el-button link type="primary" @click="openPicker(si)">添加试题</el-button>"按钮选择试题
            </div>
          </div>

          <div class="ms-addbar">
            <el-button v-for="t in addableTypes" :key="t" plain size="small"
              :disabled="manualSections.some(s => s.type === t)" @click="addSection(t)">{{ t }}</el-button>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="manualDialog = false">取消</el-button>
        <el-button type="primary" :disabled="!manualTotalCount" @click="saveManualPaper">保存试卷</el-button>
      </template>
    </el-dialog>

    <!-- 从题库选择试题 -->
    <el-dialog v-model="pickerDialog" :title="`添加试题 · ${manualSections[pickerSection]?.type ?? ''}`" width="660px" append-to-body>
      <el-input v-model="pickerKeyword" placeholder="搜索题干关键词" :prefix-icon="SearchIcon" clearable style="margin-bottom:12px" />
      <div class="pk-list">
        <el-checkbox-group v-model="pickerSelected">
          <div v-for="q in pickerCandidates" :key="q.id" class="pk-item">
            <el-checkbox :value="q.id">
              <div class="pk-body">
                <p>{{ q.stem }}</p>
                <span class="muted">{{ q.chapter }} · {{ q.difficulty }} · 建议 {{ q.score }} 分 · 已用 {{ q.used }} 次</span>
              </div>
            </el-checkbox>
          </div>
        </el-checkbox-group>
        <el-empty v-if="!pickerCandidates.length" description="题库中该题型已全部选用" :image-size="60" />
      </div>
      <template #footer>
        <el-button @click="pickerDialog = false">取消</el-button>
        <el-button type="primary" :disabled="!pickerSelected.length" @click="confirmPick">添加 {{ pickerSelected.length }} 题</el-button>
      </template>
    </el-dialog>

    <!-- 公式编辑器（数学 / 化学） -->
    <FormulaEditorDialog v-model="formulaDialog" @insert="onFormulaInsert" />
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { downloadCsv } from '../utils/download'
import type { UploadFile } from 'element-plus'
import { Search as SearchIcon, FileUp, ArrowUp, ArrowDown, Trash2 } from 'lucide-vue-next'
import FormulaEditorDialog from '../components/FormulaEditorDialog.vue'

/* 公式插入：题干中以 $...$ 包裹 LaTeX，学生端渲染 */
const formulaDialog = ref(false)
function onFormulaInsert(latex: string) {
  questionForm.value.stem = `${questionForm.value.stem} $${latex}$`.trim()
  ElMessage.success('公式已插入题干，以 $...$ 标记保存')
}

const emit = defineEmits<{ navigate: [id: string] }>()
function goHomework() { emit('navigate', 'homework') }

const tab = ref('questions')
const qType = ref('all')
const qDiff = ref('all')
const qChapter = ref('all')
const qKeyword = ref('')
const questionDialog = ref(false)
const importDialog = ref(false)
const randomDialog = ref(false)
const importFile = ref('')
const editingQuestion = ref<Question | null>(null)
const qTableRef = ref()

const questionTypes = ['单选题', '多选题', '判断题', '填空题', '简答题']
const chaptersList = ['第 1 章 短视频认知', '第 2 章 拍摄与剪辑', '第 3 章 平台算法', '第 4 章 内容策划', '第 5 章 账号运营']

interface Question {
  id: number; stem: string; type: string; difficulty: string; chapter: string; used: number; score: number
  options?: string[]; answer?: string; analysis?: string
}

const questions = ref<Question[]>([
  { id: 1, stem: '短视频平台推荐算法中，对冷启动流量池影响最大的指标是？', type: '单选题', difficulty: '中等', chapter: '第 3 章 平台算法', used: 12, score: 2, options: ['点赞量', '完播率', '评论量', '转发量'], answer: 'B', analysis: '完播率直接反映内容质量，是冷启动流量池晋级的核心指标。' },
  { id: 2, stem: '下列哪些属于平台判定的有效互动行为？（多选）', type: '多选题', difficulty: '中等', chapter: '第 3 章 平台算法', used: 9, score: 3, options: ['完整看完视频', '评论区互动', '转发给好友', '快速划过'], answer: 'ABC', analysis: '快速划过属于负反馈行为，不计入有效互动。' },
  { id: 3, stem: '「黄金三秒」原则指的是短视频开头需要快速抛出核心看点。', type: '判断题', difficulty: '简单', chapter: '第 1 章 短视频认知', used: 15, score: 1, answer: '正确', analysis: '黄金三秒强调开头即给出冲突 / 悬念 / 利益点之一。' },
  { id: 4, stem: '推拉摇移跟，是五种最基本的____方式。', type: '填空题', difficulty: '简单', chapter: '第 2 章 拍摄与剪辑', used: 6, score: 2, answer: '运镜', analysis: '推、拉、摇、移、跟为五种基本运镜方式。' },
  { id: 5, stem: '结合本课程案例，阐述账号垂直定位对涨粉效率的影响机制。', type: '简答题', difficulty: '困难', chapter: '第 5 章 账号运营', used: 4, score: 10, answer: '要点：降低算法识别成本、粉丝画像集中、完播互动率提升、商业转化效率高', analysis: '从算法匹配与粉丝资产两个维度作答即可得分。' },
  { id: 6, stem: '剪辑中「J-cut」指的是声音先于画面进入的转场技巧。', type: '判断题', difficulty: '中等', chapter: '第 2 章 拍摄与剪辑', used: 8, score: 1, answer: '正确', analysis: 'J-cut = 声音先进，画面后切，常用于访谈与场景转场。' },
  { id: 7, stem: '一份完整短视频脚本通常包含下列哪些要素？（多选）', type: '多选题', difficulty: '简单', chapter: '第 4 章 内容策划', used: 7, score: 3, options: ['场景设定', '台词文案', '镜头时长', '账号登录密码'], answer: 'ABC', analysis: '脚本包含场景、台词、镜头信息，不涉及账号密码。' },
  { id: 8, stem: '平台完播率的计算公式是____。', type: '填空题', difficulty: '困难', chapter: '第 3 章 平台算法', used: 5, score: 2, answer: '完整播放次数 ÷ 播放次数 × 100%', analysis: '完播率 = 完整播放次数 / 播放次数 × 100%。' }
])

const homeworkLib = [
  { name: '第 1 章课后练习 · 平台认知', chapter: '第 1 章 短视频认知', count: 8, score: 20, used: 3 },
  { name: '剪辑实操作业 · 30s 混剪', chapter: '第 2 章 拍摄与剪辑', count: 3, score: 30, used: 2 },
  { name: '算法理解专项训练', chapter: '第 3 章 平台算法', count: 12, score: 40, used: 1 }
]

const papers = ref([
  { id: 1, name: '第 2 章章节测验卷', count: 15, score: 50, source: '随机组卷', used: 2, time: '2026-11-06' },
  { id: 2, name: '期中模拟卷（A 卷）', count: 30, score: 100, source: '手动组卷', used: 1, time: '2026-10-30' },
  { id: 3, name: '期末考试模拟卷', count: 40, score: 100, source: '随机组卷', used: 0, time: '2026-10-18' }
])

const filteredQuestions = computed(() =>
  questions.value.filter(q =>
    (qType.value === 'all' || q.type === qType.value) &&
    (qDiff.value === 'all' || q.difficulty === qDiff.value) &&
    (qChapter.value === 'all' || q.chapter === qChapter.value) &&
    q.stem.includes(qKeyword.value)
  )
)

const selectedQuestions = ref<Question[]>([])
const selectedScore = computed(() => selectedQuestions.value.reduce((s, q) => s + q.score, 0))

const questionForm = ref({
  type: '单选题', diff: 1, stem: '', options: '', answer: '', score: 2, chapter: chaptersList[0], analysis: ''
})

function openAddQuestion() {
  editingQuestion.value = null
  questionForm.value = { type: '单选题', diff: 1, stem: '', options: '', answer: '', score: 2, chapter: chaptersList[0], analysis: '' }
  questionDialog.value = true
}
function editQuestion(row: Question) {
  editingQuestion.value = row
  questionForm.value = {
    type: row.type, diff: row.difficulty === '简单' ? 1 : row.difficulty === '中等' ? 2 : 3,
    stem: row.stem, options: '', answer: '', score: row.score, chapter: row.chapter, analysis: ''
  }
  questionDialog.value = true
}
function saveQuestion() {
  if (!questionForm.value.stem.trim()) { ElMessage.warning('请输入题干'); return }
  const diffText = questionForm.value.diff === 1 ? '简单' : questionForm.value.diff === 2 ? '中等' : '困难'
  if (editingQuestion.value) {
    Object.assign(editingQuestion.value, {
      stem: questionForm.value.stem, type: questionForm.value.type, difficulty: diffText,
      chapter: questionForm.value.chapter, score: questionForm.value.score
    })
    ElMessage.success('题目已更新')
  } else {
    questions.value.unshift({
      id: Date.now(), stem: questionForm.value.stem, type: questionForm.value.type,
      difficulty: diffText, chapter: questionForm.value.chapter, used: 0, score: questionForm.value.score
    })
    ElMessage.success('题目已入库')
  }
  questionDialog.value = false
}
function deleteQuestion(row: Question) {
  ElMessageBox.confirm('确认删除该题目？已被试卷引用的题目将保留历史数据。', '删除题目', { type: 'warning' })
    .then(() => {
      questions.value = questions.value.filter(q => q.id !== row.id)
      ElMessage.success('已删除')
    })
    .catch(() => undefined)
}

function onImportPick(file: UploadFile) { importFile.value = file.name }
function downloadTemplate() {
  downloadCsv('题库导入模板.csv',
    ['题型', '章节', '难度', '题干', '选项A', '选项B', '选项C', '选项D', '答案', '解析'],
    [['单选题', '第 3 章 平台算法', '中', '示例：冷启动阶段最看重的指标是？', '点赞量', '完播率', '评论量', '转发量', 'B', '完播率直接反映内容质量']])
  ElMessage.success('导入模板已开始下载')
}
function exportTemplate() {
  downloadCsv('题库导出.csv',
    ['题型', '章节', '难度', '题干', '答案'],
    filteredQuestions.value.map(q => [q.type, q.chapter, q.difficulty, q.stem, q.answer ?? '']))
  ElMessage.success(`已导出 ${filteredQuestions.value.length} 道题目`)
}
function confirmImport() {
  importDialog.value = false
  importFile.value = ''
  ElMessage.success('成功导入 32 题，已自动按章节归类')
}

/* 随机组卷 */
const paperForm = ref({
  name: '',
  chapters: [...chaptersList],
  hardPct: 20,
  items: [
    { type: '单选题', count: 10, score: 2 },
    { type: '多选题', count: 5, score: 3 },
    { type: '判断题', count: 5, score: 1 },
    { type: '填空题', count: 3, score: 2 },
    { type: '简答题', count: 2, score: 10 }
  ]
})
const totalCount = computed(() => paperForm.value.items.reduce((s, i) => s + i.count, 0))
const totalScore = computed(() => paperForm.value.items.reduce((s, i) => s + i.count * i.score, 0))

function openRandomPaper() { randomDialog.value = true }
function generatePaper(publish: boolean) {
  if (!paperForm.value.name.trim()) { ElMessage.warning('请输入试卷名称'); return }
  papers.value.unshift({
    id: Date.now(), name: paperForm.value.name.trim(), count: totalCount.value,
    score: totalScore.value, source: '随机组卷', used: publish ? 1 : 0,
    time: new Date().toISOString().slice(0, 10)
  })
  randomDialog.value = false
  if (publish) {
    ElMessage.success('试卷已生成，即将跳转到「测验考试」完成发布')
    setTimeout(() => emit('navigate', 'exam'), 600)
  } else {
    ElMessage.success('试卷已保存到试卷库')
  }
}
const paperStructure = computed(() => {
  if (!paperPreview.value) return []
  const total = paperPreview.value.count
  const rows = [
    { type: '单选题', ratio: 0.4 }, { type: '多选题', ratio: 0.2 },
    { type: '判断题', ratio: 0.2 }, { type: '简答题', ratio: 0.2 }
  ]
  return rows.map(r => ({
    type: r.type,
    count: Math.max(1, Math.round(total * r.ratio)),
    pct: Math.round(r.ratio * 100)
  }))
})

interface PaperMeta { id: number; name: string; count: number; score: number; source: string; used: number; time: string }
const paperPreview = ref<PaperMeta | null>(null)
function previewPaper(row: PaperMeta) { paperPreview.value = row }

/* ---------- 手动组卷（组卷管理） ---------- */
interface PaperQ extends Question { pscore: number }
interface PaperSection { type: string; each: number; questions: PaperQ[] }

const manualDialog = ref(false)
const manualName = ref('')
const manualSections = ref<PaperSection[]>([])
const pickerDialog = ref(false)
const pickerSection = ref(-1)
const pickerSelected = ref<number[]>([])
const pickerKeyword = ref('')

const addableTypes = ['单选题', '多选题', '判断题', '填空题', '简答题', '实训题']
const TYPE_DEFAULT_SCORE: Record<string, number> = { 单选题: 1, 多选题: 2, 判断题: 1, 填空题: 1, 简答题: 5, 实训题: 10 }
const letter = (i: number) => String.fromCharCode(65 + i)

function openManualPaper() {
  manualName.value = ''
  manualSections.value = [{ type: '单选题', each: TYPE_DEFAULT_SCORE['单选题'], questions: [] }]
  manualDialog.value = true
}
function addSection(type: string) {
  if (manualSections.value.some(s => s.type === type)) return
  manualSections.value.push({ type, each: TYPE_DEFAULT_SCORE[type] ?? 1, questions: [] })
  ElMessage.success(`已添加「${type}」大题，可从题库选择试题`)
}
function removeSection(si: number) {
  const sec = manualSections.value[si]
  const doRemove = () => manualSections.value.splice(si, 1)
  if (sec.questions.length) {
    ElMessageBox.confirm(`「${sec.type}」下已有 ${sec.questions.length} 道题，删除大题将一并移除，确认？`, '删除大题', { type: 'warning' })
      .then(doRemove).catch(() => undefined)
  } else doRemove()
}
function moveSection(si: number, dir: -1 | 1) {
  const arr = manualSections.value
  const j = si + dir
  if (j < 0 || j >= arr.length) return
  ;[arr[si], arr[j]] = [arr[j], arr[si]]
}
function changeSectionType(sec: PaperSection, type: string) {
  if (type === sec.type) return
  sec.type = type
  sec.each = TYPE_DEFAULT_SCORE[type] ?? 1
  sec.questions = [] // 换题型后原题不再适用
  ElMessage.info('已更换题型，原大题下的题目已清空，请重新选题')
}
/** 左侧「每题 n 分」统一设置该大题下所有题目分值 */
function applyEachScore(sec: PaperSection) {
  sec.questions.forEach(q => { q.pscore = sec.each })
}
function sectionScore(sec: PaperSection) {
  return sec.questions.reduce((n, q) => n + q.pscore, 0)
}
const manualTotalCount = computed(() => manualSections.value.reduce((n, s) => n + s.questions.length, 0))
const manualTotalScore = computed(() => manualSections.value.reduce((n, s) => n + sectionScore(s), 0))

/* 题库选题 */
function openPicker(si: number) {
  pickerSection.value = si
  pickerSelected.value = []
  pickerKeyword.value = ''
  pickerDialog.value = true
}
const pickerCandidates = computed(() => {
  const sec = manualSections.value[pickerSection.value]
  if (!sec) return []
  return questions.value.filter(q =>
    q.type === sec.type &&
    !sec.questions.some(p => p.id === q.id) &&
    q.stem.includes(pickerKeyword.value)
  )
})
function confirmPick() {
  const sec = manualSections.value[pickerSection.value]
  if (!sec) return
  const picked = questions.value.filter(q => pickerSelected.value.includes(q.id))
  picked.forEach(q => sec.questions.push({ ...q, pscore: sec.each }))
  pickerDialog.value = false
  ElMessage.success(`已添加 ${picked.length} 道${sec.type}`)
}

function moveQuestion(sec: PaperSection, qi: number, dir: -1 | 1) {
  const j = qi + dir
  if (j < 0 || j >= sec.questions.length) return
  ;[sec.questions[qi], sec.questions[j]] = [sec.questions[j], sec.questions[qi]]
}
function removeQuestion(sec: PaperSection, qi: number) {
  sec.questions.splice(qi, 1)
}
function saveManualPaper() {
  if (!manualName.value.trim()) { ElMessage.warning('请输入试卷名称'); return }
  if (!manualTotalCount.value) { ElMessage.warning('请先为大题添加试题'); return }
  papers.value.unshift({
    id: Date.now(), name: manualName.value.trim(), count: manualTotalCount.value,
    score: manualTotalScore.value, source: '手动组卷', used: 0,
    time: new Date().toISOString().slice(0, 10)
  })
  manualDialog.value = false
  ElMessage.success(`试卷已保存：共 ${manualTotalCount.value} 题 / ${manualTotalScore.value} 分`)
}
</script>

<style scoped>
.paper-structure { margin-top: 16px; }
.ps-title { font-size: 13px; font-weight: 600; margin-bottom: 10px; }
.ps-row { display: flex; align-items: center; gap: 12px; margin-bottom: 8px; font-size: 12.5px; }
.ps-row span { width: 52px; }
.ps-row b { width: 52px; text-align: right; font-weight: 600; }
.toolbar { display: flex; gap: 10px; align-items: center; margin-bottom: 14px; flex-wrap: wrap; }
.q-stem { font-size: 13px; }
.table-foot { display: flex; align-items: center; justify-content: space-between; margin-top: 14px; flex-wrap: wrap; gap: 10px; }
.sel-info { font-size: 13px; color: var(--app-ink-2); }
.qp-table { width: 100%; border-collapse: collapse; }
.qp-table th, .qp-table td { padding: 8px 10px; border: 1px solid var(--app-line); font-size: 13px; text-align: left; }
.qp-table th { background: #fafbfd; color: var(--app-ink-3); font-weight: 600; }
.paper-summary {
  display: flex; gap: 24px; padding: 12px 16px; border-radius: 8px; font-size: 13px;
  background: var(--el-color-primary-light-9); color: var(--app-ink-2);
}
.paper-summary strong { color: var(--el-color-primary); font-size: 15px; }

/* ---------- 手动组卷 ---------- */
.manual-wrap { display: grid; grid-template-columns: 240px 1fr; gap: 18px; min-height: 480px; }
.ms-side { border: 1px solid var(--app-line); border-radius: 10px; padding: 6px 14px 14px; align-self: start; }
.ms-sec { padding: 12px 0; border-bottom: 1px solid #eef1f6; }
.ms-sec-head { display: flex; align-items: center; justify-content: space-between; }
.ms-sec-head strong { font-size: 14px; }
.ms-tools { display: inline-flex; }
.ms-tools .el-button + .el-button { margin-left: 2px; }
.ms-sec > p { font-size: 12.5px; margin: 8px 0; }
.ms-each { display: flex; align-items: center; gap: 6px; font-size: 12.5px; color: var(--app-ink-2); }
.ms-total { display: flex; flex-direction: column; gap: 4px; padding-top: 12px; font-size: 13px; }
.ms-total strong { color: var(--el-color-primary); font-size: 15px; }
.ms-main { min-width: 0; }
.ms-title { margin-bottom: 16px; }
.ms-title :deep(.el-input__wrapper) { justify-content: center; font-size: 16px; font-weight: 600; }
.ms-title :deep(.el-input__inner) { text-align: center; }
.ms-block { border: 1px solid var(--app-line); border-radius: 10px; margin-bottom: 14px; overflow: hidden; }
.ms-block-head { display: flex; align-items: center; gap: 12px; padding: 10px 14px; background: #f7f8fb; border-bottom: 1px solid var(--app-line); }
.ms-block-type { font-size: 14.5px; flex: 1; }
.ms-q { display: flex; gap: 10px; padding: 14px 16px; border-bottom: 1px solid #f0f2f6; }
.ms-q:last-of-type { border-bottom: none; }
.ms-q-main { flex: 1; min-width: 0; }
.ms-q-stem { font-size: 13.5px; font-weight: 600; margin: 0 0 8px; line-height: 1.6; }
.ms-q-opts { display: flex; flex-direction: column; gap: 4px; margin-bottom: 8px; }
.ms-q-opt { display: flex; align-items: center; gap: 8px; font-size: 13px; color: var(--app-ink-2); }
.opt-letter {
  display: inline-flex; align-items: center; justify-content: center;
  width: 18px; height: 18px; border: 1px solid #c9d2e0; border-radius: 50%;
  font-size: 11px; color: var(--app-ink-3); flex-shrink: 0;
}
.opt-letter.right { background: var(--el-color-primary); border-color: var(--el-color-primary); color: #fff; }
.opt-right-text { color: var(--el-color-primary); }
.ms-q-ans, .ms-q-ana { font-size: 12.5px; color: var(--app-ink-2); margin: 2px 0; }
.ms-q-tools { display: flex; align-items: center; gap: 4px; flex-shrink: 0; }
.ms-empty { text-align: center; padding: 34px 0; font-size: 13px; }
.ms-addbar { display: flex; justify-content: center; gap: 10px; padding: 16px 0 4px; }
.pk-list { max-height: 46vh; overflow-y: auto; }
.pk-item { padding: 10px 8px; border-bottom: 1px solid #f2f4f9; }
.pk-item :deep(.el-checkbox) { align-items: flex-start; height: auto; width: 100%; }
.pk-item :deep(.el-checkbox__label) { flex: 1; line-height: 1.6; }
.pk-body p { font-size: 13.5px; margin: 0 0 4px; font-weight: 600; }
.pk-body .muted { font-size: 12px; }
</style>
