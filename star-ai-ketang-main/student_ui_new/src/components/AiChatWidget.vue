<template>
  <div class="aiw">
    <transition name="aiw-pop">
      <section v-if="open" class="aiw-panel" role="dialog" aria-label="课程智能问答助手">
        <header class="aiw-head">
          <div class="aiw-title">
            <Sparkles :size="18" />
            <div>
              <b>课程智能问答助手</b>
              <small>{{ online ? '已接入《短视频制作与运营》知识库' : '服务已停用' }}</small>
            </div>
          </div>
          <button class="aiw-x" @click="open = false" aria-label="关闭"><X :size="16" /></button>
        </header>

        <div ref="chatBox" class="aiw-chat">
          <div v-if="!msgs.length" class="aiw-hello">
            <p>同学你好！我已学习本课程的课件、题库与教学视频，可以回答课程相关问题。</p>
            <div class="aiw-samples">
              <button v-for="s in samples" :key="s" @click="send(s)">{{ s }}</button>
            </div>
          </div>
          <div v-for="(m, i) in msgs" :key="i" class="aiw-msg" :class="m.role">
            <div class="aiw-bub">
              {{ m.text }}
              <small v-if="m.src" class="aiw-src">出处：{{ m.src }}</small>
            </div>
          </div>
          <div v-if="thinking" class="aiw-msg b"><div class="aiw-bub aiw-typing"><i></i><i></i><i></i></div></div>
        </div>

        <footer class="aiw-ask">
          <input v-model="draft" placeholder="输入课程相关问题…" @keyup.enter="send(draft)" />
          <button class="aiw-send" @click="send(draft)" aria-label="发送"><SendHorizontal :size="16" /></button>
        </footer>
      </section>
    </transition>

    <button class="aiw-fab" :class="{ on: open }" @click="open = !open" aria-label="智能问答助手">
      <Sparkles v-if="!open" :size="24" />
      <ChevronDown v-else :size="24" />
    </button>
  </div>
</template>

<script setup lang="ts">
import { nextTick, ref } from 'vue'
import { ChevronDown, SendHorizontal, Sparkles, X } from 'lucide-vue-next'

interface Msg {
  role: 'u' | 'b'
  text: string
  src?: string
}
interface KbEntry {
  kw: string[]
  a: string
  src: string
}

const online = ref(true)
const open = ref(false)
const draft = ref('')
const thinking = ref(false)
const msgs = ref<Msg[]>([])
const chatBox = ref<HTMLElement | null>(null)

const threshold = 70
const fallback = '这个问题超出了本课程资料的范围，我已转交任课老师，老师回复后会通知你。'
const samples = [
  '完播率是怎么计算的？',
  '流量池是怎么晋级的？',
  '黄金三秒是什么意思？',
  '帮我写这次作业的答案',
]
const kb: KbEntry[] = [
  { kw: ['完播率', '计算', '公式'], a: '完播率 = 完整播放次数 ÷ 播放次数 × 100%。它直接反映内容质量，是冷启动流量池晋级的核心指标，通常 30% 以上为健康水平。', src: '第3章 平台算法与流量池.pptx · 第12页' },
  { kw: ['流量池', '晋级', '冷启动', '推荐'], a: '新视频先进入 300-500 曝光的冷启动流量池，系统依据完播率、互动率等数据表现决定是否晋级到更大流量池，一般有四轮晋级机会。', src: '第3章 平台算法与流量池.pptx · 第15页' },
  { kw: ['黄金三秒', '开头', '前三秒'], a: '黄金三秒指视频开头 3 秒内必须抛出核心看点（冲突、悬念或利益点），否则用户划走会拉低 5 秒留存与完播率。', src: '第2章 拍摄与剪辑基础.pptx · 第21页' },
  { kw: ['垂直定位', '账号定位', '涨粉'], a: '垂直定位能降低算法的识别成本，让内容精准触达目标人群；粉丝画像集中后完播与互动率更高，涨粉效率显著提升。', src: '短视频运营题库 · 第5章 第1题' },
  { kw: ['j-cut', 'jcut', '转场', '声音先'], a: 'J-cut 指声音先于画面进入的转场技巧，常用于访谈与场景切换，能让转场更自然流畅。', src: '剪辑节奏卡点示范.mp4 · 05:40' },
  { kw: ['运镜', '推拉摇移'], a: '推、拉、摇、移、跟是五种基本运镜：推近强调主体，拉远交代环境，摇摄展示空间，移动跟随主体，跟拍营造临场感。', src: '剪辑节奏卡点示范.mp4 · 03:12' },
  { kw: ['发布', '时段', '几点发'], a: '校园类账号建议在 12:00-13:00、18:00-20:00、21:00-23:00 三个活跃时段发布，发布后 1 小时内积极回复评论可提升初始互动。', src: '短视频运营题库 · 第4章 第6题' },
]

function norm(s: string) {
  return s.replace(/[\s？?。！!，,.、：:；;]/g, '')
}
function judge(q: string): Msg {
  const n = norm(q)
  if (/(代写|作业.*答案|答案.*作业|考试.*答案|泄题|帮我(做|写)作业|答案直接发)/.test(n)) {
    return { role: 'b', text: '检测到疑似索要作业/考试答案的请求，已被课程问答边界拦截。建议描述你的解题思路，我可以帮你分析卡在哪一步。' }
  }
  let best: { conf: number; a: string; src: string } | null = null
  for (const k of kb) {
    const hits = k.kw.filter((kw) => n.toLowerCase().includes(norm(kw).toLowerCase())).length
    if (!hits) continue
    const conf = hits >= 2 ? 92 : n.length >= 8 ? 76 : 62
    if (!best || conf > best.conf) best = { conf, a: k.a, src: k.src }
  }
  if (best && best.conf >= threshold) {
    return { role: 'b', text: best.a, src: best.src }
  }
  return { role: 'b', text: fallback }
}
function send(q: string) {
  const text = q.trim()
  if (!text || thinking.value) return
  msgs.value.push({ role: 'u', text })
  draft.value = ''
  thinking.value = true
  scroll()
  window.setTimeout(() => {
    msgs.value.push(judge(text))
    thinking.value = false
    scroll()
  }, 600)
}
function scroll() {
  nextTick(() => {
    if (chatBox.value) chatBox.value.scrollTop = chatBox.value.scrollHeight
  })
}
</script>

<style scoped>
.aiw-fab {
  position: fixed; right: 26px; bottom: 26px; z-index: 900;
  width: 56px; height: 56px; border-radius: 50%; border: 0; cursor: pointer;
  background: linear-gradient(135deg, #2f6bff, #6b4dff); color: #fff;
  display: grid; place-items: center;
  box-shadow: 0 10px 26px rgba(47, 107, 255, .4);
  transition: transform .18s ease, box-shadow .18s ease;
}
.aiw-fab:hover { transform: translateY(-2px) scale(1.05); box-shadow: 0 14px 30px rgba(47, 107, 255, .5); }
.aiw-fab.on { background: #3b4a6b; }

.aiw-panel {
  position: fixed; right: 26px; bottom: 94px; z-index: 900;
  width: 380px; max-width: calc(100vw - 32px); height: 560px; max-height: calc(100vh - 130px);
  background: #fff; border-radius: 16px; overflow: hidden;
  box-shadow: 0 18px 50px rgba(15, 23, 42, .25);
  display: flex; flex-direction: column;
  border: 1px solid #e6e9f2;
}
.aiw-pop-enter-active, .aiw-pop-leave-active { transition: opacity .18s ease, transform .18s ease; }
.aiw-pop-enter-from, .aiw-pop-leave-to { opacity: 0; transform: translateY(14px) scale(.97); }

.aiw-head {
  display: flex; align-items: center; justify-content: space-between;
  padding: 12px 14px; color: #fff;
  background: linear-gradient(135deg, #2f6bff, #6b4dff);
}
.aiw-title { display: flex; align-items: center; gap: 10px; }
.aiw-title b { display: block; font-size: 14px; }
.aiw-title small { display: block; font-size: 11px; opacity: .85; margin-top: 1px; }
.aiw-x { border: 0; background: rgba(255, 255, 255, .18); color: #fff; width: 26px; height: 26px; border-radius: 8px; cursor: pointer; display: grid; place-items: center; }
.aiw-x:hover { background: rgba(255, 255, 255, .3); }

.aiw-chat { flex: 1; overflow-y: auto; padding: 14px; background: #f5f7fc; display: flex; flex-direction: column; gap: 10px; }
.aiw-hello { background: #fff; border: 1px solid #e6e9f2; border-radius: 12px; padding: 12px; font-size: 13px; color: #3b4763; }
.aiw-hello p { margin: 0 0 10px; line-height: 1.6; }
.aiw-samples { display: flex; flex-wrap: wrap; gap: 8px; }
.aiw-samples button {
  border: 1px solid #d6e0ff; background: #eef3ff; color: #2f6bff;
  border-radius: 999px; padding: 5px 12px; font-size: 12px; cursor: pointer;
}
.aiw-samples button:hover { background: #dfe9ff; }

.aiw-msg { display: flex; }
.aiw-msg.u { justify-content: flex-end; }
.aiw-bub {
  max-width: 82%; padding: 9px 12px; border-radius: 12px; font-size: 13px; line-height: 1.65;
  background: #fff; border: 1px solid #e6e9f2; color: #1f2a44;
  border-bottom-left-radius: 4px; white-space: pre-wrap; word-break: break-word;
}
.aiw-msg.u .aiw-bub {
  background: #2f6bff; border-color: #2f6bff; color: #fff;
  border-radius: 12px; border-bottom-right-radius: 4px;
}
.aiw-src { display: block; margin-top: 6px; font-size: 11px; color: #7a87a6; }
.aiw-typing { display: flex; gap: 5px; align-items: center; min-height: 18px; }
.aiw-typing i { width: 6px; height: 6px; border-radius: 50%; background: #b6c2dd; animation: aiw-dot 1s infinite; }
.aiw-typing i:nth-child(2) { animation-delay: .15s; }
.aiw-typing i:nth-child(3) { animation-delay: .3s; }
@keyframes aiw-dot { 0%, 60%, 100% { transform: translateY(0); opacity: .5; } 30% { transform: translateY(-4px); opacity: 1; } }

.aiw-ask { display: flex; gap: 8px; padding: 10px 12px; border-top: 1px solid #e6e9f2; background: #fff; }
.aiw-ask input {
  flex: 1; height: 38px; border: 1px solid #dde3f0; border-radius: 10px;
  padding: 0 12px; font-size: 13px; outline: none;
}
.aiw-ask input:focus { border-color: #2f6bff; box-shadow: 0 0 0 3px rgba(47, 107, 255, .12); }
.aiw-send {
  width: 38px; height: 38px; border-radius: 10px; border: 0; cursor: pointer;
  background: #2f6bff; color: #fff; display: grid; place-items: center;
}
.aiw-send:hover { background: #2458d6; }

@media (max-width: 640px) {
  .aiw-fab { right: 16px; bottom: 16px; }
  .aiw-panel { right: 12px; bottom: 82px; }
}
</style>
