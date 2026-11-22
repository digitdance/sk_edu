<template>
  <div class="notice-page">
    <el-button text :icon="ArrowLeft" @click="emit('navigate', 's-home')">返回首页</el-button>

    <div v-if="notice" class="notice-layout">
      <!-- 公告正文 -->
      <article class="panel notice-card">
        <div class="n-head">
          <el-tag size="small" effect="light" :type="notice.type">{{ notice.course }}</el-tag>
          <h1>{{ notice.title }}</h1>
          <div class="n-meta muted">
            <span><User :size="13" /> {{ notice.publisher }}</span>
            <span><Clock3 :size="13" /> {{ notice.time }}</span>
            <span><Eye :size="13" /> {{ notice.readCount }} 人已读</span>
          </div>
        </div>
        <div class="n-content">
          <p v-for="(p, i) in notice.content" :key="i">{{ p }}</p>
        </div>
        <div class="n-foot muted">
          <CheckCircle2 :size="14" style="color:var(--el-color-success)" /> 已读回执已自动提交给任课教师
        </div>
      </article>

      <!-- 更多公告 -->
      <aside class="panel more-card">
        <div class="panel-title"><h3>更多公告</h3></div>
        <div
          v-for="n in otherNotices" :key="n.id"
          class="more-item" @click="openNotice(n.id)"
        >
          <el-tag size="small" effect="plain" :type="n.type">{{ n.course }}</el-tag>
          <div class="m-body">
            <strong>{{ n.title }}</strong>
            <span class="muted">{{ n.time }} · {{ n.publisher }}</span>
          </div>
          <ChevronRight :size="14" class="muted" />
        </div>
      </aside>
    </div>

    <el-empty v-else description="公告不存在或已撤回">
      <el-button type="primary" @click="emit('navigate', 's-home')">返回首页</el-button>
    </el-empty>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { ArrowLeft, User, Clock3, Eye, CheckCircle2, ChevronRight } from 'lucide-vue-next'
import { activeNoticeId, notices } from '../../stores/notices'

const emit = defineEmits<{ navigate: [id: string] }>()

const notice = computed(() => notices.value.find(n => n.id === activeNoticeId.value) ?? notices.value[0] ?? null)
const otherNotices = computed(() => notices.value.filter(n => n.id !== notice.value?.id))

function openNotice(id: number) {
  activeNoticeId.value = id
  window.scrollTo({ top: 0 })
}
</script>

<style scoped>
.notice-page { max-width: 1080px; margin: 0 auto; padding: 4px 24px 40px; }
.notice-layout { display: grid; grid-template-columns: 1fr 300px; gap: 18px; margin-top: 12px; align-items: start; }
.notice-card { padding: 28px 34px; }
.n-head h1 { font-size: 21px; margin: 12px 0 10px; line-height: 1.5; }
.n-meta { display: flex; gap: 20px; font-size: 12.5px; padding-bottom: 16px; border-bottom: 1px solid var(--app-line); }
.n-meta span { display: inline-flex; align-items: center; gap: 5px; }
.n-content { padding: 18px 0; }
.n-content p { font-size: 14.5px; line-height: 2; color: var(--app-ink-2); margin: 0 0 10px; text-indent: 2em; }
.n-content p:first-child { text-indent: 0; }
.n-foot { display: flex; align-items: center; gap: 6px; font-size: 12.5px; padding-top: 14px; border-top: 1px dashed var(--app-line); }
.more-card { padding: 16px; }
.more-item { display: flex; align-items: flex-start; gap: 10px; padding: 12px 6px; border-bottom: 1px solid #f2f4f8; cursor: pointer; border-radius: 6px; }
.more-item:hover { background: #f7f9ff; }
.more-item:last-child { border-bottom: none; }
.m-body { flex: 1; display: flex; flex-direction: column; gap: 4px; min-width: 0; }
.m-body strong { font-size: 13px; line-height: 1.5; }
.m-body .muted { font-size: 12px; }
@media (max-width: 900px) { .notice-layout { grid-template-columns: 1fr; } }
</style>
