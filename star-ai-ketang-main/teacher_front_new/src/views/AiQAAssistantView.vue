<template>
  <div class="ai-scope">
    <div class="qa">
      <div class="page-head">
        <div>
          <h2>智能问答助手</h2>
          <div class="sub">绑定课程知识库 · 限定问答范围 · 越界拦截与兜底 · 问答记录与标准答案库维护</div>
        </div>
        <div class="filters">
          <span class="chip ok">知识库 {{ boundCount }}/{{ totalKb }}</span>
          <span class="chip gray">今日问答 {{ records.length }}</span>
          <span class="chip warn">待纠错 {{ correctedCount }}</span>
        </div>
      </div>

      <section class="card card-pad">
        <div class="viz-h">
          <h3 class="h-title">课程知识库绑定</h3>
          <div style="display:flex;gap:8px">
            <button class="btn-sm btn" @click="bindAll">全选</button>
            <button class="btn-sm btn" @click="bindNone">清空</button>
          </div>
        </div>
        <div class="kb-lists">
          <div class="kb">
            <div class="kb-t"><b>Available knowledge bases</b><button class="btn-sm btn" @click="loadKnowledgeBases">Refresh</button><button class="btn-sm btn" :disabled="kbBusy" @click="createKnowledgeBase">Create</button></div>
            <p v-if="!knowledgeBases.length" class="muted">Create a knowledge base and upload course materials to enable grounded answers.</p>
            <label v-for="kb in knowledgeBases" :key="kb.id" class="kb-i" :class="{ on: selectedKbIds.includes(kb.id) }">
              <input type="checkbox" :value="kb.id" v-model="selectedKbIds" />
              <span>{{ kb.name }}</span><small class="muted">{{ kb.documentCount ?? 0 }} documents 路 {{ kb.status ?? 'ready' }}</small>
            </label>
            <label class="lab" for="kb-upload">Add a course document</label>
            <input id="kb-upload" type="file" :disabled="kbBusy || !selectedKbIds.length" @change="uploadKnowledgeDocument" />
            <small class="muted">Text and Markdown files are indexed with their content; other formats are passed to the document processor.</small>
          </div>
        </div>
      </section>

      <section class="card card-pad">
        <div class="viz-h">
          <h3 class="h-title">学生端问答开关</h3>
          <button class="toggle" :class="{ on: qaOn }" @click="qaOn = !qaOn"><i class="knob"></i>{{ qaOn ? '已开启' : '已关闭' }}</button>
        </div>
        <p class="muted" style="margin:0 0 12px">关闭后学生端浮动入口将隐藏；开启时请配置下方问答边界，防止 AI 超范围作答。</p>

        <div class="bd" :class="{ dim: !qaOn }">
          <div class="bd-c">
            <b><AiIcon name="shield" :size="15" /> 允许问答范围</b>
            <label v-for="s in scopeOptions" :key="s.id" class="kb-i" :class="{ on: boundary.scope[s.id] }">
              <input type="checkbox" v-model="boundary.scope[s.id]" />
              <span>{{ s.name }}</span>
            </label>
            <div class="th">
              <div class="row"><span>答案置信度阈值</span><b>{{ boundary.threshold }}</b></div>
              <input type="range" min="40" max="95" step="5" v-model.number="boundary.threshold" />
              <small class="muted">低于阈值的问题将不直接作答，按中间列策略处理</small>
            </div>
          </div>
          <div class="bd-c">
            <b><AiIcon name="alert" :size="15" /> 超出范围处理</b>
            <label class="kb-i" :class="{ on: boundary.outAction === 'refuse' }">
              <input type="radio" value="refuse" v-model="boundary.outAction" />
              <span>礼貌拒答</span><small class="muted">提示不在课程范围内</small>
            </label>
            <label class="kb-i" :class="{ on: boundary.outAction === 'forward' }">
              <input type="radio" value="forward" v-model="boundary.outAction" />
              <span>转交教师</span><small class="muted">记录问题并提醒教师补充</small>
            </label>
            <label class="kb-i" :class="{ on: boundary.outAction === 'open' }">
              <input type="radio" value="open" v-model="boundary.outAction" />
              <span>通用回答</span><small class="muted">标注「非课程内容，仅供参考」</small>
            </label>
            <div class="th">
              <label class="kb-i" :class="{ on: boundary.blockCheat }">
                <input type="checkbox" v-model="boundary.blockCheat" />
                <span>拦截代写 / 求答案行为</span>
              </label>
              <label class="kb-i" :class="{ on: boundary.needSource }">
                <input type="checkbox" v-model="boundary.needSource" />
                <span>回答必须附知识库出处</span>
              </label>
            </div>
          </div>
          <div class="bd-c">
            <b><AiIcon name="edit" :size="15" /> 敏感词与兜底话术</b>
            <div class="words">
              <span v-for="(w, i) in words" :key="w" class="chip danger">{{ w }}<a @click="words.splice(i, 1)">×</a></span>
              <input class="input" v-model="newWord" placeholder="添加敏感词后回车" @keyup.enter="addWord" />
            </div>
            <textarea class="input" rows="3" v-model="boundary.fallback"></textarea>
            <div style="text-align:right;margin-top:8px"><button class="btn-primary btn-sm btn" @click="saveBoundary">保存边界设置</button></div>
          </div>
        </div>
      </section>

      <section class="card card-pad stu">
        <div class="viz-h"><h3 class="h-title">学生端效果预览</h3><span class="chip" :class="qaOn ? 'ok' : 'gray'">{{ qaOn ? '服务中' : '已停用' }}</span></div>
        <div class="chat">
          <div v-for="(m, i) in chat" :key="i" class="msg" :class="m.role">
            <div class="bub">{{ m.text }}</div>
            <small v-if="m.src" class="muted">出处：{{ m.src }}</small>
          </div>
          <div v-if="!chat.length" class="samples">
            <p class="muted">试试这些问题：</p>
            <button v-for="s in qaSamples" :key="s" class="chip gray" @click="ask(s)">{{ s }}</button>
          </div>
        </div>
        <div class="ask">
          <input class="input" v-model="draft" placeholder="输入学生问题…" @keyup.enter="ask(draft)" />
          <button class="btn-primary btn" @click="ask(draft)">发送</button>
        </div>
      </section>

      <div class="rec-wrap" :class="{ 'no-det': !detail }">
        <section class="card card-pad">
          <div class="rec-h">
            <h3 class="h-title">问答记录</h3>
            <div class="seg mini">
              <button v-for="f in recFilters" :key="f.id" :class="{ on: recFilter === f.id }" @click="recFilter = f.id">{{ f.name }}</button>
            </div>
          </div>
          <table class="tbl">
            <thead>
              <tr><th><input type="checkbox" :checked="allChecked" @change="toggleAll" /></th><th>学生</th><th>问题</th><th>状态</th><th>时间</th><th>操作</th></tr>
            </thead>
            <tbody>
              <tr v-for="r in filteredRecords" :key="r.id">
                <td><input type="checkbox" v-model="r.checked" /></td>
                <td>{{ r.student }} <small class="muted">{{ r.cls }}</small></td>
                <td class="qq">{{ r.q }}</td>
                <td><span class="chip" :class="statusChip(r.status)">{{ statusName(r.status) }}</span></td>
                <td class="muted">{{ r.time }}</td>
                <td><button class="lnk" @click="openDetail(r)">详情</button></td>
              </tr>
            </tbody>
          </table>
          <div class="export">
            <span class="muted">已选 {{ checkedCount }} 条</span>
            <div>
              <button class="btn" @click="batchToStd">加入标准答案库</button>
              <button class="btn" @click="exportRecords">导出记录</button>
            </div>
          </div>
        </section>

        <aside v-if="detail" class="card card-pad det">
          <div class="viz-h"><h3 class="h-title">记录详情</h3><button class="lnk" @click="detail = null">关闭</button></div>
          <div class="bub q">{{ detail.q }}</div>
          <div class="bub a">{{ detail.a }}</div>
          <div v-if="detail.src.length" class="srcs muted">出处：{{ detail.src.join('；') }}</div>
          <div class="note" :class="detail.status === 'blocked' ? 'danger' : detail.status === 'out' ? 'warn' : 'ok'">
            {{ statusName(detail.status) }} · 置信度 {{ detail.conf }}<template v-if="detail.note"> · {{ detail.note }}</template>
          </div>
          <template v-if="detail.status === 'out' || detail.status === 'corrected'">
            <b style="font-size:13px">教师补充标准答案</b>
            <textarea class="input" rows="3" v-model="stdDraft" placeholder="写下标准答案，保存后进入标准答案库"></textarea>
            <div class="det-f">
              <button class="btn-sm btn" @click="flagError">标记纠错</button>
              <button class="btn-primary btn-sm btn" @click="saveStd">保存为标准答案</button>
            </div>
          </template>
        </aside>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import AiIcon from '../components/AiIcon.vue'
import { defaultBoundary, kbIndex, qaSamples, type QaRecord } from '../data/aiMock'
import { apiRequest } from '../api/client'
import { auth } from '../stores/auth'

interface ChatMsg {
  role: 'u' | 'b'
  text: string
  src?: string
}
type Rec = QaRecord & { checked: boolean }

interface KnowledgeBase { id: number; name: string; description?: string; documentCount?: number; status?: string }
interface RecallChunk { documentName?: string; content?: string; score?: number }
interface RecallResponse { chunks?: RecallChunk[]; totalResults?: number }
const knowledgeBases = ref<KnowledgeBase[]>([])
const selectedKbIds = ref<number[]>([])
const kbBusy = ref(false)
const totalKb = computed(() => knowledgeBases.value.length)
const boundCount = computed(() => selectedKbIds.value.length)
const settingsKey = () => `teacher-ai-qa-settings:${auth.userId}`
const sessionKey = () => `teacher-ai-qa-session:${auth.userId}`
const standardsKey = () => `teacher-ai-qa-standards:${auth.userId}`

const qaOn = ref(true)
const boundary = reactive({ ...defaultBoundary, scope: { ...defaultBoundary.scope } })
const standards = ref<Record<string, string>>({})
const scopeOptions = [
  { id: 'courseware', name: '课程课件内容' },
  { id: 'bank', name: '课程题库知识' },
  { id: 'video', name: '教学视频字幕' },
  { id: 'std', name: '教师标准答案库' },
]
const words = ref(['代考', '刷分', '外挂'])
const newWord = ref('')

const chat = ref<ChatMsg[]>([])
const draft = ref('')
const aiSessionId = ref<number | null>(null)

const recFilter = ref('all')
const recFilters = [
  { id: 'all', name: '全部' },
  { id: 'answered', name: '已解答' },
  { id: 'out', name: '超范围' },
  { id: 'blocked', name: '已拦截' },
  { id: 'corrected', name: '已纠错' },
]
function loadSavedRecords(): Rec[] {
  try { return JSON.parse(localStorage.getItem(`teacher-ai-qa-records:${auth.userId}`) || '[]') as Rec[] }
  catch { return [] }
}
function persistRecords() { localStorage.setItem(`teacher-ai-qa-records:${auth.userId}`, JSON.stringify(records)) }
const records = reactive<Rec[]>(loadSavedRecords())
const detail = ref<Rec | null>(null)
const stdDraft = ref('')

const filteredRecords = computed(() => (recFilter.value === 'all' ? records : records.filter((r) => r.status === recFilter.value)))
const checkedCount = computed(() => records.filter((r) => r.checked).length)
const correctedCount = computed(() => records.filter((r) => r.status === 'out').length)
const allChecked = computed(() => filteredRecords.value.length > 0 && filteredRecords.value.every((r) => r.checked))

function norm(s: string) {
  return s.replace(/[\s？?。！!，,.、：:；;]/g, '')
}
function judge(q: string): { status: string; conf: number; a: string; src: string[] } {
  const n = norm(q)
  if (boundary.blockCheat && /(代写|作业.*答案|答案.*作业|考试.*答案|泄题|帮我(做|写)作业|答案直接发)/.test(n)) {
    return { status: 'blocked', conf: 0, a: '检测到疑似索要作业/考试答案的请求，已按课程设置拦截。请描述你的解题思路，我可以帮你分析卡点。', src: [] }
  }
  if (words.value.some((w) => n.includes(norm(w)))) {
    return { status: 'blocked', conf: 0, a: '该问题包含敏感内容，无法作答。', src: [] }
  }
  let best: { conf: number; a: string; src: string } | null = null
  for (const k of kbIndex) {
    const hits = k.kw.filter((kw) => n.toLowerCase().includes(norm(kw).toLowerCase())).length
    if (!hits) continue
    const conf = hits >= 2 ? 92 : n.length >= 8 ? 76 : 62
    if (!best || conf > best.conf) best = { conf, a: k.a, src: k.src }
  }
  if (best && best.conf >= boundary.threshold) {
    return { status: 'answered', conf: best.conf, a: best.a, src: boundary.needSource ? [best.src] : [] }
  }
  if (boundary.outAction === 'refuse') {
    return { status: 'out', conf: best?.conf ?? 15, a: '这个问题超出当前课程知识库范围，我暂时无法可靠作答。', src: [] }
  }
  if (boundary.outAction === 'open') {
    return { status: 'out', conf: best?.conf ?? 15, a: '以下内容非课程范围，仅供参考：建议咨询任课教师或查阅课程群公告获取权威解答。', src: [] }
  }
  return { status: 'out', conf: best?.conf ?? 15, a: boundary.fallback, src: [] }
}
async function ask(q: string) {
  const text = q.trim()
  if (!text) return
  if (!qaOn.value) { ElMessage.warning('Student Q&A is disabled'); return }
  chat.value.push({ role: 'u', text })
  draft.value = ''
  const local = judge(text)
  let answer = local.a
  let status = local.status
  let sources = local.src
  let confidence = local.conf
  const standard = standards.value[norm(text)]
  if (standard && local.status !== 'blocked') {
    answer = standard
    confidence = 100
    status = 'corrected'
    sources = ['Teacher standard answer']
  } else if (local.status !== 'blocked') {
    try {
      const recallResults = await Promise.all(selectedKbIds.value.map((id) => apiRequest<RecallResponse>(`/api/ai/knowledge-bases/${id}/recall-test`, { method: 'POST', body: { query: text } })))
      const chunks = recallResults.flatMap((result) => result.chunks ?? []).filter((chunk) => (chunk.score ?? 0) >= 0.3)
      confidence = chunks.length ? Math.round(Math.max(...chunks.map((chunk) => chunk.score ?? 0)) * 100) : 0
      const context = chunks.map((chunk) => `${chunk.documentName ?? 'Course document'}: ${chunk.content ?? ''}`).join('\n\n')
      sources = [...new Set(chunks.map((chunk) => chunk.documentName).filter((name): name is string => Boolean(name)))]
      if (!context) {
        status = 'out'
        answer = boundary.outAction === 'forward' ? boundary.fallback : boundary.outAction === 'open' ? 'No matching course source was found. Please check with your instructor.' : 'No matching course source was found, so I cannot answer reliably.'
      } else {
        if (!aiSessionId.value) {
          const session = await apiRequest<{ sessionId: number }>('/api/ai/chat/sessions', { method: 'POST', body: {} })
          aiSessionId.value = session.sessionId
          localStorage.setItem(sessionKey(), String(session.sessionId))
        }
        const base = (import.meta.env.VITE_API_BASE_URL || '').replace(/\/$/, '')
        const token = localStorage.getItem('teacher-access-token')
        const response = await fetch(`${base}/api/ai/chat/sessions/${aiSessionId.value}/stream`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json', Accept: 'text/event-stream', ...(token ? { Authorization: `Bearer ${token}` } : {}) },
          body: JSON.stringify({ message: text, documentUrls: [], systemPrompt: `Use only the supplied course excerpts. If they do not support an answer, say so. Do not provide direct answers to active exams or graded homework. ${boundary.needSource ? 'Cite source document names.' : ''}\n\nAllowed scope: ${Object.entries(boundary.scope).filter(([, allowed]) => allowed).map(([name]) => name).join(', ')}\n\nCourse excerpts:\n${context}` })
        })
        if (!response.ok || !response.body) throw new Error(`AI service returned ${response.status}`)
        const reader = response.body.getReader(); const decoder = new TextDecoder(); let buffer = ''; let output = ''
        while (true) {
          const { value, done } = await reader.read(); if (done) break
          buffer += decoder.decode(value, { stream: true })
          const lines = buffer.split(/\r?\n/); buffer = lines.pop() ?? ''
          for (const line of lines) if (line.startsWith('data:')) {
            const data = line.slice(5).trim(); if (!data || data === '[DONE]') continue
            try { const parsed = JSON.parse(data); output += typeof parsed === 'string' ? parsed : typeof parsed === 'number' ? data : String(parsed.content ?? parsed.text ?? parsed.delta ?? '') } catch { output += data }
          }
        }
        if (output.trim()) { answer = output.trim(); status = 'answered' }
      }
    } catch (error) {
      answer = boundary.fallback
      status = 'out'
      ElMessage.warning(error instanceof Error ? `Knowledge lookup failed: ${error.message}` : 'Knowledge lookup failed')
    }
  }
  chat.value.push({ role: 'b', text: answer, src: sources[0] })
  records.unshift({
    id: Date.now(), student: 'Teacher preview', cls: '',
    time: new Date().toLocaleString('zh-CN', { hour12: false }).replaceAll('/', '-'),
    q: text, a: answer, src: sources, conf: confidence, status,
    fwd: status === 'out' && boundary.outAction === 'forward', teacher: '',
    note: status === 'blocked' ? 'Blocked by configured policy' : '', checked: false
  })
}

async function loadKnowledgeBases() {
  if (!auth.userId) return
  kbBusy.value = true
  try {
    knowledgeBases.value = await apiRequest<KnowledgeBase[]>(`/api/ai/knowledge-bases?userId=${auth.userId}&page=0&size=100`)
    const saved = JSON.parse(localStorage.getItem(`${sessionKey()}:kbids`) || '[]') as number[]
    selectedKbIds.value = saved.filter((id) => knowledgeBases.value.some((kb) => kb.id === id))
    if (!localStorage.getItem(`${sessionKey()}:kbids`)) selectedKbIds.value = knowledgeBases.value.map((kb) => kb.id)
  } catch (error) { ElMessage.error(error instanceof Error ? error.message : 'Could not load knowledge bases') }
  finally { kbBusy.value = false }
}
async function createKnowledgeBase() {
  if (!auth.userId) { ElMessage.warning('Sign in before creating a knowledge base'); return }
  kbBusy.value = true
  try {
    const kb = await apiRequest<KnowledgeBase>(`/api/ai/knowledge-bases?userId=${auth.userId}`, { method: 'POST', body: {
      name: `${auth.profile.name || 'Teacher'} Course Assistant`, description: 'Course materials for grounded student Q&A',
      retrievalMode: 'HYBRID_RERANK', defaultTopK: 5, useDynamicTopK: false,
    } })
    await loadKnowledgeBases()
    selectedKbIds.value = [...new Set([...selectedKbIds.value, kb.id])]
    ElMessage.success('Knowledge base created')
  } catch (error) { ElMessage.error(error instanceof Error ? error.message : 'Could not create knowledge base') }
  finally { kbBusy.value = false }
}
async function uploadKnowledgeDocument(event: Event) {
  const input = event.target as HTMLInputElement
  const file = input.files?.[0]
  const kbId = selectedKbIds.value[0]
  if (selectedKbIds.value.length !== 1) { ElMessage.warning('Select exactly one KB before adding a document'); input.value = ''; return }
  if (!file || !kbId || !auth.userId) return
  kbBusy.value = true
  try {
    const form = new FormData(); form.append('file', file)
    const uploaded = await apiRequest<{ fileUrl: string }>('/api/file/upload/general', { method: 'POST', body: form })
    const content = /\.(txt|md)$/i.test(file.name) ? await file.text() : null
    const doc = await apiRequest<{ id: number }>(`/api/ai/knowledge-bases/${kbId}/documents?userId=${auth.userId}`, { method: 'POST', body: {
      name: file.name, fileType: file.name.split('.').pop() || 'file', fileUrl: uploaded.fileUrl, fileSize: file.size, content,
    } })
    await apiRequest(`/api/ai/knowledge-bases/${kbId}/documents/${doc.id}/embed`, { method: 'POST' })
    await loadKnowledgeBases()
    ElMessage.success('Document uploaded and indexing requested')
  } catch (error) { ElMessage.error(error instanceof Error ? error.message : 'Document upload failed') }
  finally { kbBusy.value = false; input.value = '' }
}
function bindAll() { selectedKbIds.value = knowledgeBases.value.map((kb) => kb.id) }
function bindNone() { selectedKbIds.value = [] }
async function addWord() {
  const w = newWord.value.trim()
  if (w && !words.value.includes(w)) words.value.push(w)
  newWord.value = ''
}
function saveBoundary() {
  localStorage.setItem(settingsKey(), JSON.stringify({ boundary, words: words.value, qaOn: qaOn.value }))
  localStorage.setItem(`${sessionKey()}:kbids`, JSON.stringify(selectedKbIds.value))
  ElMessage.success('Settings saved for this teacher account')
}
function statusName(s: string) {
  return ({ answered: '已解答', out: '超范围', blocked: '已拦截', corrected: '已纠错' } as Record<string, string>)[s] ?? s
}
function statusChip(s: string) {
  return ({ answered: 'ok', out: 'warn', blocked: 'danger', corrected: 'gray' } as Record<string, string>)[s] ?? 'gray'
}
function toggleAll() {
  const v = !allChecked.value
  filteredRecords.value.forEach((r) => (r.checked = v))
}
function openDetail(r: Rec) {
  detail.value = r
  stdDraft.value = r.teacher || ''
}
function saveStd() {
  if (!stdDraft.value.trim()) { ElMessage.warning('Enter a standard answer first'); return }
  if (detail.value) {
    detail.value.teacher = stdDraft.value.trim()
    detail.value.a = stdDraft.value.trim()
    detail.value.status = 'corrected'
    detail.value.conf = 98
    detail.value.fwd = false
    standards.value[norm(detail.value.q)] = stdDraft.value.trim()
    localStorage.setItem(standardsKey(), JSON.stringify(standards.value))
  }
  ElMessage.success('Standard answer saved and will be reused')
}
function flagError() {
  if (detail.value) {
    detail.value.status = 'corrected'
    detail.value.note = '教师标记纠错样本'
  }
  ElMessage.success('已标记为纠错样本，将用于优化问答模型')
}
function batchToStd() {
  const n = checkedCount.value
  if (!n) {
    ElMessage.warning('请先勾选记录')
    return
  }
  records.forEach((r) => {
    if (r.checked && r.status === 'answered') r.conf = 98
  })
  ElMessage.success(`已将 ${n} 条优质问答加入标准答案库`)
}
function exportRecords() {
  if (!records.length) { ElMessage.warning('There are no Q&A records to export'); return }
  const csv = ['Question,Answer,Status,Sources,Time', ...records.map((record) => [record.q, record.a, record.status, record.src.join('; '), record.time].map((value) => `"${String(value).replaceAll('"', '""')}"`).join(','))].join('\r\n')
  const url = URL.createObjectURL(new Blob([csv], { type: 'text/csv;charset=utf-8' }))
  const link = document.createElement('a'); link.href = url; link.download = 'course-qa-records.csv'; link.click()
  URL.revokeObjectURL(url)
}
async function restoreAiState() {
  try {
    const saved = JSON.parse(localStorage.getItem(settingsKey()) || 'null')
    if (saved) {
      Object.assign(boundary, saved.boundary ?? {})
      Object.assign(boundary.scope, saved.boundary?.scope ?? {})
      words.value = saved.words ?? words.value
      qaOn.value = saved.qaOn ?? qaOn.value
      selectedKbIds.value = saved.selectedKbIds ?? []
    }
    standards.value = JSON.parse(localStorage.getItem(standardsKey()) || '{}')
    await loadKnowledgeBases()
    const savedSessionId = Number(localStorage.getItem(sessionKey()))
    if (savedSessionId) {
      const detail = await apiRequest<{ messages: Array<{ role: string; content: string }> }>(`/api/ai/chat/sessions/${savedSessionId}`)
      aiSessionId.value = savedSessionId
      chat.value = (detail.messages ?? []).map((message) => ({ role: message.role === 'user' ? 'u' : 'b', text: message.content }))
    }
  } catch {
    aiSessionId.value = null
    localStorage.removeItem(sessionKey())
  }
}
onMounted(() => { void restoreAiState() })
</script>
