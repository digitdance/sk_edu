<template>
  <div class="ai-scope">
    <div class="lp">
      <div class="page-head">
        <div>
          <h1>智能备课设计</h1>
          <p>基于课程大纲或章节教学目标，生成可直接使用的备课素材，生成内容仅供参考，请教师审核后使用。</p>
        </div>
        <span class="chip gray"><AiIcon name="clock" :size="13" />{{ stamp }}</span>
      </div>

      <section class="card card-pad src">
        <div class="src-bar" :class="{ focus: focused, bad: invalid }">
          <AiIcon name="upload" :size="18" />
          <textarea id="outline" class="src-ta" rows="3" v-model="outline" @focus="focused = true" @blur="focused = false"
            placeholder="上传课程大纲 / 粘贴章节教学目标"></textarea>
        </div>
        <div class="src-row">
          <div class="src-l">
            <label class="btn btn-sm" for="ai-file"><AiIcon name="upload" :size="14" />上传课程大纲</label>
            <input id="ai-file" type="file" hidden accept=".doc,.docx,.pdf,.txt,.md" @change="onFile" />
            <span v-if="fileName" class="chip"><AiIcon name="file" :size="12" />{{ fileName }}</span>
            <label class="f"><span>课时</span>
              <select class="select" v-model="hours"><option>1 课时</option><option>2 课时</option><option>4 课时</option></select>
            </label>
            <label class="f"><span>教学对象</span>
              <select class="select" v-model="level"><option>高职一年级</option><option>高职二年级</option><option>中职</option></select>
            </label>
          </div>
          <button class="btn btn-primary btn-lg" :disabled="busy" @click="generate(false)">
            <AiIcon name="search" :size="16" />分析大纲并生成
          </button>
        </div>
        <p v-if="invalid" class="warn-t"><AiIcon name="alert" :size="14" />请先上传课程大纲，或粘贴章节教学目标（至少 10 个字）。</p>
      </section>

      <section class="card card-pad gen">
        <h2 class="h-title">AI生成</h2>
        <div class="types" role="radiogroup" aria-label="生成类型">
          <button v-for="t in genTypes" :key="t.key" class="type" :class="{ on: type === t.key }" role="radio"
            :aria-checked="type === t.key" @click="type = t.key">
            <i class="dot"></i>
            <span class="ic"><AiIcon :name="t.icon" :size="24" /></span>
            <b>{{ t.label }}</b>
            <small>{{ t.hint }}</small>
          </button>
        </div>
      </section>

      <section class="card result" :class="{ loading: busy }">
        <div class="res-head">
          <h2 class="h-title">{{ currentLabel }}预览</h2>
          <div class="rh-r">
            <span v-if="busy" class="chip"><AiIcon name="refresh" :size="13" class="spin" />AI 正在生成…</span>
            <template v-else>
              <span class="chip ok"><AiIcon name="check" :size="13" />已生成 v{{ version }}</span>
              <span v-if="dirty" class="chip warn">已编辑，未保存</span>
              <button class="btn btn-sm" @click="toggleEdit"><AiIcon name="edit" :size="14" />{{ editing ? '完成编辑' : '审核编辑' }}</button>
            </template>
          </div>
        </div>
        <div class="basis"><span class="muted">生成依据</span><span class="chip gray">{{ fileName || '粘贴的教学目标' }}</span><span class="chip gray">{{ hours }}</span><span class="chip gray">{{ level }}</span><span class="chip gray">课程：{{ course }}</span></div>

        <div v-if="busy" class="skel"><i v-for="n in 7" :key="n" :style="{ width: (92 - n * 7) + '%' }"></i></div>

        <div v-else class="res-body" :class="{ editing }" :contenteditable="editing" @input="onResultEdit"><pre v-if="generatedContent" class="ai-output">{{ generatedContent }}</pre><template v-else>
          <!-- 教案 -->
          <template v-if="type === 'lesson'">
            <h3 class="doc-t">{{ lessonPlan.title }}</h3>
            <div class="metas"><span class="chip" v-for="m in lessonPlan.meta" :key="m">{{ m }}</span></div>
            <div class="grid2">
              <div class="blk">
                <h4>教学目标</h4>
                <dl><template v-for="g in lessonPlan.goals" :key="g.k"><dt>{{ g.k }}</dt><dd>{{ g.v }}</dd></template></dl>
              </div>
              <div class="blk">
                <h4>教学重难点</h4>
                <dl><dt>重点</dt><dd>{{ lessonPlan.focus.key }}</dd><dt>难点</dt><dd>{{ lessonPlan.focus.hard }}</dd></dl>
                <h4 class="mt">课后作业</h4>
                <p class="sub">{{ lessonPlan.homework }}</p>
              </div>
            </div>
            <h4>教学过程</h4>
            <div class="tbl-wrap">
              <table class="tbl plain">
                <thead><tr><th>环节</th><th>时长</th><th>教学活动</th><th>设计意图</th></tr></thead>
                <tbody>
                  <tr v-for="p in lessonPlan.process" :key="p.step"><td><b>{{ p.step }}</b></td><td>{{ p.time }}</td><td>{{ p.act }}</td><td><span class="chip gray">{{ p.design }}</span></td></tr>
                </tbody>
              </table>
            </div>
          </template>

          <!-- 练习题 -->
          <template v-else-if="type === 'exercise'">
            <h3 class="doc-t">《平台算法与流量池机制》课堂练习题</h3>
            <div class="metas"><span class="chip">共 {{ exercises.length }} 题</span><span class="chip gray">可一键存入题库</span></div>
            <ol class="qs">
              <li v-for="(e, i) in exercises" :key="i" class="q">
                <div class="q-h"><b>{{ i + 1 }}.</b><span class="chip">{{ e.type }}</span><span class="chip gray">难度 {{ e.diff }}</span><span class="chip gray">{{ e.point }}</span></div>
                <p class="q-s">{{ e.stem }}</p>
                <ul v-if="e.opts.length" class="opts"><li v-for="(o, k) in e.opts" :key="k"><i>{{ 'ABCD'[k] }}</i>{{ o }}</li></ul>
                <p class="ans"><b>参考答案：</b>{{ e.ans }}<span v-if="e.exp" class="muted"> · {{ e.exp }}</span></p>
              </li>
            </ol>
          </template>

          <!-- PPT -->
          <template v-else-if="type === 'ppt'">
            <h3 class="doc-t">《平台算法与流量池机制》教学PPT提纲</h3>
            <div class="metas"><span class="chip">共 {{ pptOutline.length }} 页</span><span class="chip gray">建议时长 45 分钟</span></div>
            <div class="slides">
              <article v-for="s in pptOutline" :key="s.no" class="sl">
                <span class="sl-n">{{ s.no }}</span>
                <h5>{{ s.title }}</h5>
                <ul><li v-for="p in s.points" :key="p">{{ p }}</li></ul>
              </article>
            </div>
          </template>

          <!-- 实训任务 -->
          <template v-else>
            <h3 class="doc-t">{{ trainTask.name }}</h3>
            <p class="sub scene"><b>任务场景：</b>{{ trainTask.scene }}</p>
            <div class="grid2">
              <div class="blk">
                <h4>实施步骤</h4>
                <ol class="steps"><li v-for="s in trainTask.steps" :key="s.t"><b>{{ s.t }}</b><span>{{ s.d }}</span></li></ol>
              </div>
              <div class="blk">
                <h4>提交成果</h4>
                <div class="metas"><span class="chip gray" v-for="d in trainTask.deliver" :key="d">{{ d }}</span></div>
                <h4 class="mt">评分标准（满分 100）</h4>
                <ul class="rub"><li v-for="r in trainTask.rubric" :key="r.item"><span>{{ r.item }}</span><i><u :style="{ width: r.score * 3 + '%' }"></u></i><b>{{ r.score }}</b></li></ul>
              </div>
            </div>
          </template></template></template></template></template>
        </div>

        <div class="actions">
          <button class="btn" :disabled="busy" @click="dlg = true"><AiIcon name="download" :size="16" />一键导入课程编辑器</button>
          <button class="btn btn-primary" :disabled="busy" @click="generate(true)"><AiIcon name="refresh" :size="16" />重新生成</button>
          <button class="btn" :disabled="busy" @click="saveCloud"><AiIcon name="cloud" :size="16" />保存素材至云盘</button>
        </div>
      </section>

      <div v-if="dlg" class="mask" @click.self="dlg = false">
        <div class="dlg card" role="dialog" aria-modal="true" aria-label="导入课程编辑器">
          <h3>导入课程编辑器</h3>
          <p class="sub">将“{{ currentLabel }}”导入到课程《{{ course }}》，导入后可继续在富媒体编辑器中修改。</p>
          <label class="lab" for="chap">目标章节</label>
          <select id="chap" class="select" v-model="chapter"><option v-for="c in chapters" :key="c">{{ c }}</option></select>
          <label class="lab" for="pos">插入方式</label>
          <select id="pos" class="select" v-model="pos"><option>追加到章节末尾</option><option>新建学习单元</option><option>替换该章节同类内容</option></select>
          <label v-if="type === 'exercise'" class="ck"><input type="checkbox" v-model="toBank" />同时存入课程题库（单选、判断、简答）</label>
          <div class="dlg-f">
            <button class="btn" @click="dlg = false">取消</button>
            <button class="btn btn-primary" @click="doImport">确认导入</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import AiIcon from '../components/AiIcon.vue'
import { aiHandoff } from '../stores/aiHandoff'
import { genTypes, sampleOutline, lessonPlan, exercises, pptOutline, trainTask } from '../data/aiMock'
import { apiRequest } from '../api/client'

const type = ref('lesson')
const outline = ref(sampleOutline)
const focused = ref(false)
const fileName = ref('')
const fileUrl = ref('')
const hours = ref('2 课时')
const level = ref('高职一年级')
const busy = ref(false)
const version = ref(0)
const stamp = ref('尚未生成')
const editing = ref(false)
const dirty = ref(false)
const invalid = ref(false)
const dlg = ref(false)
const course = '当前教师课程'
const chapters = ['请在课程建设中选择目标章节']
const chapter = ref(chapters[0])
const pos = ref('追加到章节末尾')
const toBank = ref(true)
const generatedContent = ref('')
const currentLabel = computed(() => genTypes.find(item => item.key === type.value)?.label ?? '教学内容')
function now() { return new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }) }

async function onFile(event: Event) {
  const file = (event.target as HTMLInputElement).files?.[0]
  if (!file) return
  fileName.value = file.name
  try {
    if (/\.(txt|md)$/i.test(file.name)) outline.value = await file.text()
    const form = new FormData(); form.append('file', file)
    const upload = await apiRequest<{ fileUrl: string }>('/api/file/upload/general', { method: 'POST', body: form })
    fileUrl.value = upload.fileUrl
    ElMessage.success('大纲已上传，可用于生成备课内容')
  } catch (error) { ElMessage.error(error instanceof Error ? error.message : '大纲上传失败') }
}
function toggleEdit() { editing.value = !editing.value; if (!editing.value && dirty.value) ElMessage.info('修改已保留在当前预览') }
function onResultEdit(event: Event) { generatedContent.value = (event.currentTarget as HTMLElement).innerText; dirty.value = true }
async function saveCloud() {
  if (!generatedContent.value.trim()) { ElMessage.warning('请先生成备课内容'); return }
  try {
    const file = new File([generatedContent.value], `ai-${type.value}-${Date.now()}.md`, { type: 'text/markdown' })
    const form = new FormData(); form.append('file', file)
    await apiRequest('/api/file/upload/general', { method: 'POST', body: form })
    dirty.value = false; ElMessage.success('备课素材已保存到个人文件库')
  } catch (error) { ElMessage.error(error instanceof Error ? error.message : '素材保存失败') }
}
function doImport() { ElMessage.warning('请选择具体课程与章节后，再从课程建设页面导入本次生成内容'); dlg.value = false }

async function streamPrompt(prompt: string): Promise<string> {
  const session = await apiRequest<{ sessionId: number }>('/api/ai/chat/sessions', { method: 'POST', body: {} })
  const base = (import.meta.env.VITE_API_BASE_URL || '').replace(/\/$/, '')
  const token = localStorage.getItem('teacher-access-token')
  const response = await fetch(`${base}/api/ai/chat/sessions/${session.sessionId}/stream`, {
    method: 'POST', headers: { 'Content-Type': 'application/json', Accept: 'text/event-stream', ...(token ? { Authorization: `Bearer ${token}` } : {}) },
    body: JSON.stringify({ message: prompt, documentUrls: fileUrl.value ? [fileUrl.value] : [], systemPrompt: '你是职业院校教学设计助手。依据用户给出的课程大纲，输出可直接审核的中文教学材料。按所选类型输出具体内容；不得编造课程数据。' + (fileUrl.value ? ` 可参考上传文档：${fileUrl.value}` : '') })
  })
  if (!response.ok || !response.body) throw new Error(`AI 服务请求失败（${response.status}）`)
  const reader = response.body.getReader(); const decoder = new TextDecoder(); let buffer = ''; let output = ''
  while (true) {
    const { value, done } = await reader.read(); if (done) break
    buffer += decoder.decode(value, { stream: true })
    const lines = buffer.split(/\r?\n/); buffer = lines.pop() ?? ''
    for (const line of lines) if (line.startsWith('data:')) {
      const data = line.slice(5).trim(); if (!data || data === '[DONE]') continue
      try { const parsed = JSON.parse(data); output += String(parsed.content ?? parsed.text ?? parsed.delta ?? '') }
      catch { output += data }
    }
  }
  return output.trim()
}
async function generate(again: boolean) {
  invalid.value = outline.value.trim().length < 10 && !fileUrl.value
  if (invalid.value) return
  editing.value = false; dirty.value = false; busy.value = true
  try {
    const prompt = `生成类型：${currentLabel.value}\n课时：${hours.value}\n教学对象：${level.value}\n课程：${course}\n课程大纲或教学目标：\n${outline.value}`
    const result = await streamPrompt(prompt)
    if (!result) throw new Error('AI 未返回可展示的内容')
    generatedContent.value = result; version.value += 1; stamp.value = '上次生成 ' + now()
    ElMessage.success(again ? '已重新生成备课内容' : '已根据大纲生成备课内容')
  } catch (error) { ElMessage.error(error instanceof Error ? error.message : 'AI 生成失败') }
  finally { busy.value = false }
}
onMounted(() => {
  if (aiHandoff.outline) { outline.value = aiHandoff.outline; if (aiHandoff.type) type.value = aiHandoff.type; aiHandoff.outline = ''; aiHandoff.type = ''; void generate(false) }
})
</script>
