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
          <span class="chip gray">今日问答 36</span>
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
          <div v-for="g in kbGroups" :key="g.id" class="kb">
            <div class="kb-t"><AiIcon :name="g.icon" :size="15" /><b>{{ g.type }}</b><span class="muted">{{ g.items.length }} 项</span></div>
            <label v-for="k in g.items" :key="k.name" class="kb-i" :class="{ on: k.bound }">
              <input type="checkbox" v-model="k.bound" />
              <span>{{ k.name }}</span>
              <small class="muted">{{ k.info }} · {{ k.state }}</small>
            </label>
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
import { computed, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import AiIcon from '../components/AiIcon.vue'
import { defaultBoundary, kbIndex, kbSources, qaRecords, qaSamples, type QaRecord } from '../data/aiMock'

interface ChatMsg {
  role: 'u' | 'b'
  text: string
  src?: string
}
type Rec = QaRecord & { checked: boolean }

const kbGroups = reactive([
  { id: 'courseware', type: '课程课件', icon: 'slides', items: kbSources.courseware.map((k) => ({ ...k, bound: true })) },
  { id: 'bank', type: '课程题库', icon: 'db', items: kbSources.bank.map((k) => ({ ...k, bound: true })) },
  { id: 'video', type: '教学视频', icon: 'video', items: kbSources.video.map((k) => ({ ...k, bound: true })) },
])
const totalKb = computed(() => kbGroups.reduce((n, g) => n + g.items.length, 0))
const boundCount = computed(() => kbGroups.reduce((n, g) => n + g.items.filter((k) => k.bound).length, 0))

const qaOn = ref(true)
const boundary = reactive({ ...defaultBoundary, scope: { ...defaultBoundary.scope } })
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

const recFilter = ref('all')
const recFilters = [
  { id: 'all', name: '全部' },
  { id: 'answered', name: '已解答' },
  { id: 'out', name: '超范围' },
  { id: 'blocked', name: '已拦截' },
  { id: 'corrected', name: '已纠错' },
]
const records = reactive<Rec[]>(qaRecords.map((r) => ({ ...r, src: [...r.src], checked: false })))
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
function ask(q: string) {
  const text = q.trim()
  if (!text) return
  if (!qaOn.value) {
    ElMessage.warning('学生端问答已关闭，请先开启开关')
    return
  }
  chat.value.push({ role: 'u', text })
  const r = judge(text)
  chat.value.push({ role: 'b', text: r.a, src: r.src[0] })
  records.unshift({
    id: Date.now(),
    student: '预览·教师',
    cls: '—',
    time: new Date().toLocaleString('zh-CN', { hour12: false }).replaceAll('/', '-'),
    q: text,
    a: r.a,
    src: r.src,
    conf: r.conf,
    status: r.status,
    fwd: r.status === 'out' && boundary.outAction === 'forward',
    teacher: '',
    note: r.status === 'blocked' ? '触发拦截策略' : '',
    checked: false,
  })
  draft.value = ''
}

function bindAll() {
  kbGroups.forEach((g) => g.items.forEach((k) => (k.bound = true)))
}
function bindNone() {
  kbGroups.forEach((g) => g.items.forEach((k) => (k.bound = false)))
}
function addWord() {
  const w = newWord.value.trim()
  if (w && !words.value.includes(w)) words.value.push(w)
  newWord.value = ''
}
function saveBoundary() {
  ElMessage.success('边界设置已保存，并即时同步到学生端')
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
  if (!stdDraft.value.trim()) {
    ElMessage.warning('请先填写标准答案')
    return
  }
  if (detail.value) {
    detail.value.teacher = stdDraft.value
    detail.value.a = stdDraft.value
    detail.value.status = 'corrected'
    detail.value.conf = 98
    detail.value.fwd = false
  }
  ElMessage.success('已保存为标准答案，后续相同问题将优先引用')
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
  ElMessage.success('问答记录已导出（CSV）')
}
</script>
