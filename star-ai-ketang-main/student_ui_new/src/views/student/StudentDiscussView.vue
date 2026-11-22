<template>
  <div class="discuss-layout">
    <!-- 帖子流 -->
    <div class="panel" style="margin:0">
      <div class="toolbar">
        <el-radio-group v-model="filter">
          <el-radio-button value="all">全部讨论</el-radio-button>
          <el-radio-button value="mine">我发布的</el-radio-button>
          <el-radio-button value="teacher">教师回复</el-radio-button>
          <el-radio-button value="top">精华</el-radio-button>
        </el-radio-group>
        <span style="flex:1" />
        <el-button type="primary" @click="postDialog = true">发起讨论</el-button>
      </div>

      <div class="post-list">
        <div v-for="p in filteredPosts" :key="p.id" class="post-item" @click="openPost(p)">
          <div class="p-avatar">{{ p.author[0] }}</div>
          <div class="p-body">
            <div class="p-head">
              <strong>{{ p.title }}</strong>
              <el-tag v-if="p.top" size="small" type="warning" effect="light">精华</el-tag>
              <el-tag v-if="p.hasTeacher" size="small" type="success" effect="light">教师已回复</el-tag>
            </div>
            <p class="p-preview">{{ p.preview }}</p>
            <div class="p-meta">
              <span>{{ p.author }} · {{ p.course }}</span>
              <span class="muted">{{ p.time }}</span>
              <span class="muted"><MessageSquare :size="12" style="vertical-align:-2px" /> {{ p.replies }}</span>
              <span class="muted"><ThumbsUp :size="12" style="vertical-align:-2px" /> {{ p.likes }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 侧栏：课程筛选 + 热议 -->
    <div>
      <div class="panel">
        <div class="panel-title"><h3>按课程筛选</h3></div>
        <div class="course-filter">
          <el-check-tag v-for="c in courseTags" :key="c" :checked="course === c" @change="course = c" style="margin:0 8px 8px 0">{{ c }}</el-check-tag>
        </div>
      </div>
      <div class="panel">
        <div class="panel-title"><h3>本周热议</h3></div>
        <div class="hot-list">
          <div v-for="(h, i) in hotPosts" :key="h" class="hot-item">
            <span class="hot-rank" :class="{ top: i < 3 }">{{ i + 1 }}</span>
            <span>{{ h }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 发帖 -->
    <el-dialog v-model="postDialog" title="发起讨论" width="560px">
      <el-form label-position="top">
        <el-form-item label="所属课程">
          <el-select v-model="postCourse" style="width:100%">
            <el-option label="短视频制作与运营" value="短视频制作与运营" />
            <el-option label="新媒体文案写作" value="新媒体文案写作" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题">
          <el-input v-model="postTitle" placeholder="一句话说明你的问题或观点" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="postContent" type="textarea" :rows="4" placeholder="补充细节，可描述已尝试的方法" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="postDialog = false">取消</el-button>
        <el-button type="primary" @click="submitPost">发布</el-button>
      </template>
    </el-dialog>

    <!-- 帖子详情 -->
    <el-drawer v-model="detailDrawer" :title="current?.title" size="520px">
      <template v-if="current">
        <div class="detail-post">
          <div class="p-meta" style="margin-bottom:10px">
            <span>{{ current.author }} · {{ current.course }}</span>
            <span class="muted">{{ current.time }}</span>
          </div>
          <p style="line-height:1.8; font-size:14px">{{ current.preview }} 已经尝试了调整开头和封面，但数据没有明显变化，想听听大家的建议。</p>
        </div>
        <el-divider content-position="left">全部回复（{{ current.replies }}）</el-divider>
        <div v-for="r in replies" :key="r.author" class="reply-item">
          <div class="p-avatar" :style="r.isTeacher ? 'background:#eaf0ff;color:#2f6bff' : ''">{{ r.author[0] }}</div>
          <div style="flex:1">
            <div class="p-head">
              <strong style="font-size:13px">{{ r.author }}</strong>
              <el-tag v-if="r.isTeacher" size="small" type="primary" effect="light">教师</el-tag>
              <span class="muted" style="font-size:11px">{{ r.time }}</span>
            </div>
            <p style="margin:5px 0 0; font-size:13px; line-height:1.7; color:var(--app-ink-2)">{{ r.content }}</p>
          </div>
        </div>
        <div class="reply-editor">
          <el-input v-model="replyInput" type="textarea" :rows="2" placeholder="写下你的回复…" />
          <el-button type="primary" size="small" style="margin-top:8px; float:right" @click="submitReply">回复</el-button>
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { MessageSquare, ThumbsUp } from 'lucide-vue-next'

interface Post {
  id: number; title: string; preview: string; author: string; course: string;
  time: string; replies: number; likes: number; top: boolean; hasTeacher: boolean; mine: boolean
}

const filter = ref('all')
const course = ref('全部课程')
const postDialog = ref(false)
const detailDrawer = ref(false)
const current = ref<Post | null>(null)
const postCourse = ref('短视频制作与运营')
const postTitle = ref('')
const postContent = ref('')
const replyInput = ref('')

const courseTags = ['全部课程', '短视频制作与运营', '新媒体文案写作']

const posts = ref<Post[]>([
  { id: 1, title: '为什么同样的内容，小号流量比大号好？', preview: '我用同样的脚本发了两个账号，新号反而进了更大的流量池……', author: '陈思雨', course: '短视频制作与运营', time: '11-19 09:12', replies: 18, likes: 32, top: true, hasTeacher: true, mine: false },
  { id: 2, title: '求剪辑作业互评搭子', preview: '第 2 章实操想找人互相点评，评论区扣 1。', author: '王子涵', course: '短视频制作与运营', time: '11-18 21:40', replies: 9, likes: 12, top: false, hasTeacher: false, mine: false },
  { id: 3, title: '标题里数字放在前面还是后面效果更好？', preview: 'A/B 测了 6 条视频，把数据分享给大家……', author: '刘洋', course: '新媒体文案写作', time: '11-18 15:30', replies: 14, likes: 26, top: true, hasTeacher: true, mine: true },
  { id: 4, title: '手机剪辑导出水印怎么去除？', preview: '课程里说的那个设置项我在最新版找不到了……', author: '赵可欣', course: '短视频制作与运营', time: '11-17 19:05', replies: 5, likes: 7, top: false, hasTeacher: false, mine: false }
])

const replies = [
  { author: '王琳', isTeacher: true, time: '11-19 10:02', content: '新号的「账号历史权重」更低，平台更愿意给测试流量。注意这不代表内容策略可以照搬，建议对照 3.1 节的流量池模型分析。' },
  { author: '刘洋', isTeacher: false, time: '11-19 10:15', content: '补充一个数据：我对比了 12 条视频，新号前 500 播放的完播率阈值明显更低。' }
]

const hotPosts = ['为什么同样的内容，小号流量比大号好？', '标题数字位置 A/B 测试结果', '第 2 章剪辑作业素材包分享', '完播率到底怎么算的？', '求推荐适合新手的补光灯']

const filteredPosts = computed(() =>
  posts.value.filter(p =>
    (course.value === '全部课程' || p.course === course.value) &&
    (filter.value === 'all' ||
      (filter.value === 'mine' && p.mine) ||
      (filter.value === 'teacher' && p.hasTeacher) ||
      (filter.value === 'top' && p.top))
  )
)

function openPost(p: Post) { current.value = p; detailDrawer.value = true }
function submitPost() {
  if (!postTitle.value.trim()) { ElMessage.warning('请输入标题'); return }
  posts.value.unshift({
    id: Date.now(), title: postTitle.value.trim(), preview: postContent.value || '（无正文）',
    author: '刘洋', course: postCourse.value, time: '刚刚', replies: 0, likes: 0, top: false, hasTeacher: false, mine: true
  })
  postDialog.value = false
  postTitle.value = ''
  postContent.value = ''
  ElMessage.success('讨论已发布')
}
function submitReply() {
  if (!replyInput.value.trim()) return
  if (current.value) current.value.replies += 1
  replyInput.value = ''
  ElMessage.success('回复成功')
}
</script>

<style scoped>
.discuss-layout { display: grid; grid-template-columns: 1fr 300px; gap: 16px; align-items: start; }
.toolbar { display: flex; align-items: center; gap: 10px; margin-bottom: 14px; flex-wrap: wrap; }
.post-list { display: flex; flex-direction: column; }
.post-item { display: flex; gap: 12px; padding: 14px 4px; border-bottom: 1px solid #f2f4f9; cursor: pointer; border-radius: 6px; }
.post-item:hover { background: #f7f9ff; }
.p-avatar { width: 36px; height: 36px; border-radius: 50%; background: #e7f6ec; color: #1a9e50; display: flex; align-items: center; justify-content: center; font-weight: 700; flex-shrink: 0; }
.p-body { flex: 1; min-width: 0; }
.p-head { display: flex; align-items: center; gap: 8px; flex-wrap: wrap; }
.p-head strong { font-size: 14px; }
.p-preview { margin: 5px 0; font-size: 12.5px; color: var(--app-ink-2); overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.p-meta { display: flex; gap: 14px; font-size: 12px; color: var(--app-ink-3); flex-wrap: wrap; }
.course-filter { display: flex; flex-wrap: wrap; }
.hot-list { display: flex; flex-direction: column; gap: 10px; }
.hot-item { display: flex; gap: 10px; font-size: 13px; color: var(--app-ink-2); }
.hot-rank { width: 18px; height: 18px; border-radius: 5px; background: #eef1f7; color: var(--app-ink-3); font-size: 11px; font-weight: 700; display: inline-flex; align-items: center; justify-content: center; flex-shrink: 0; }
.hot-rank.top { background: #ffe9e0; color: #e25c1b; }
.reply-item { display: flex; gap: 10px; padding: 12px 0; border-bottom: 1px solid #f2f4f9; }
.reply-editor { margin-top: 14px; overflow: hidden; }
@media (max-width: 960px) {
  .discuss-layout { grid-template-columns: 1fr; }
}
</style>
