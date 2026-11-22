<template>
  <div>
    <div class="panel">
      <el-tabs v-model="tab">
        <el-tab-pane label="个人云盘" name="mine" />
        <el-tab-pane label="课程资源" name="course" />
        <el-tab-pane label="共享给我" name="shared" />
        <el-tab-pane label="资源分类" name="category" />
      </el-tabs>

      <template v-if="tab !== 'category'">
        <!-- 工具栏 -->
        <div class="toolbar">
          <el-input v-model="keyword" placeholder="搜索资源名称" :prefix-icon="SearchIcon" style="width:220px" clearable />
          <el-select v-model="typeFilter" placeholder="类型" style="width:120px">
            <el-option label="全部类型" value="all" />
            <el-option label="视频" value="video" />
            <el-option label="文档" value="doc" />
            <el-option label="图片" value="img" />
            <el-option label="压缩包" value="zip" />
          </el-select>
          <el-select v-model="transcodeFilter" placeholder="转码状态" style="width:130px">
            <el-option label="全部状态" value="all" />
            <el-option label="已转码" value="done" />
            <el-option label="转码中" value="doing" />
            <el-option label="转码失败" value="fail" />
          </el-select>
          <span style="flex:1" />
          <el-button :icon="Upload" @click="uploadDialog = true">上传文件</el-button>
          <el-button type="primary" :icon="FolderPlus" @click="uploadDialog = true">批量上传</el-button>
        </div>

        <!-- 批量操作条 -->
        <transition name="el-fade-in">
          <div v-if="selected.length" class="batch-bar">
            <span>已选 <strong>{{ selected.length }}</strong> 项</span>
            <el-button size="small" type="primary" plain @click="batchShare">批量分享</el-button>
            <el-button size="small" plain @click="batchImport">导入课程</el-button>
            <el-button size="small" plain @click="batchDownload">批量下载</el-button>
            <el-button size="small" type="danger" plain @click="batchDelete">删除</el-button>
            <el-button size="small" link @click="clearSelection">取消选择</el-button>
          </div>
        </transition>

        <el-table ref="tableRef" :data="filteredResources" border stripe @selection-change="s => selected = s">
          <el-table-column type="selection" width="42" />
          <el-table-column label="资源名称" min-width="240">
            <template #default="{ row }">
              <div class="name-cell" @click="openPreview(row)">
                <div class="f-icon" :style="{ background: typeMeta[row.type].bg, color: typeMeta[row.type].color }">
                  <component :is="typeMeta[row.type].icon" :size="16" />
                </div>
                <div class="n-meta">
                  <span class="n">{{ row.name }}</span>
                  <span class="muted">{{ row.folder }}</span>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="size" label="大小" width="90" />
          <el-table-column label="转码状态" width="110">
            <template #default="{ row }">
              <el-tag v-if="row.type === 'video' || row.transcode !== 'done'" size="small"
                :type="row.transcode === 'done' ? 'success' : row.transcode === 'doing' ? 'warning' : 'danger'" effect="light">
                {{ row.transcode === 'done' ? '已转码' : row.transcode === 'doing' ? '转码中' : '转码失败' }}
              </el-tag>
              <span v-else class="muted">—</span>
            </template>
          </el-table-column>
          <el-table-column label="共享范围" width="110">
            <template #default="{ row }">
              <el-tag size="small" effect="plain" :type="row.scope === '全校共享' ? 'warning' : row.scope === '课程内' ? 'primary' : 'info'">
                {{ row.scope }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="expiry" label="使用期限" width="110" />
          <el-table-column prop="time" label="上传时间" width="115" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" size="small" @click="openPreview(row)">预览</el-button>
              <el-button link size="small" @click="shareOne(row)">分享</el-button>
              <el-button link size="small" @click="downloadOne(row)">下载</el-button>
              <el-button link type="danger" size="small" @click="deleteOne(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pager">
          <el-pagination layout="total, prev, pager, next" :total="filteredResources.length" :page-size="10" />
        </div>
      </template>

      <!-- 资源分类 -->
      <template v-else>
        <div class="grid-4">
          <div v-for="c in categories" :key="c.name" class="cat-card" @click="filterByCategory(c)">
            <div class="c-icon" :style="{ background: c.bg, color: c.color }">
              <component :is="c.icon" :size="22" />
            </div>
            <strong>{{ c.name }}</strong>
            <span class="muted">{{ c.count }} 个文件 · {{ c.size }}</span>
          </div>
        </div>
      </template>
    </div>

    <!-- 上传对话框 -->
    <el-dialog v-model="uploadDialog" title="上传资源" width="560px">
      <el-upload drag multiple :auto-upload="false" :on-change="onFilePick" :show-file-list="false">
        <div class="up-area">
          <UploadCloud :size="40" style="color: var(--el-color-primary)" />
          <p>将文件拖到此处，或 <em>点击选择文件</em></p>
          <p class="muted">支持视频 / 文档 / 图片 / 压缩包，视频上传后自动转码为自适应码流</p>
        </div>
      </el-upload>
      <div v-if="uploads.length" class="up-list">
        <div v-for="u in uploads" :key="u.id" class="up-item">
          <FileIcon :size="15" />
          <div class="u-meta">
            <div class="u-row">
              <span class="u-name">{{ u.name }}</span>
              <span class="muted">{{ u.progress }}%</span>
            </div>
            <el-progress :percentage="u.progress" :stroke-width="6" :status="u.progress === 100 ? 'success' : undefined" />
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="uploadDialog = false">关闭</el-button>
        <el-button type="primary" :disabled="!uploads.length" @click="finishUpload">完成并转码</el-button>
      </template>
    </el-dialog>

    <!-- 分享对话框 -->
    <el-dialog v-model="shareDialog" title="设置共享" width="440px">
      <el-form label-position="top">
        <el-form-item label="共享范围">
          <el-radio-group v-model="shareScope">
            <el-radio value="私有">私有</el-radio>
            <el-radio value="课程内">课程内</el-radio>
            <el-radio value="全校共享">全校共享</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="使用期限">
          <el-select v-model="shareExpiry" style="width:100%">
            <el-option label="永久有效" value="永久" />
            <el-option label="本学期内" value="本学期" />
            <el-option label="自定义截止日期" value="自定义" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="shareExpiry === '自定义'" label="截止日期">
          <el-date-picker type="date" style="width:100%" placeholder="选择日期" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="shareDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmShare">确认共享</el-button>
      </template>
    </el-dialog>

    <!-- 预览 / 视频内测抽屉 -->
    <el-drawer v-model="previewDrawer" :title="current?.name || '资源预览'" size="520px">
      <template v-if="current">
        <div v-if="current.type === 'video'" class="player-box" style="aspect-ratio:16/8.4">
          <button class="play-btn" @click="ElMessage.info('视频已转码，可在课程章节中在线播放')"><Play :size="24" /></button>
          <div class="hud"><div class="hud-row"><span>00:00</span><div class="track"><div class="fill" style="width:0%" /></div><span>{{ current.duration }}</span></div></div>
        </div>
        <div v-else class="doc-preview">
          <component :is="typeMeta[current.type].icon" :size="42" style="color: var(--el-color-primary)" />
          <p>{{ current.type === 'doc' ? '文档已在线解析，支持逐页预览' : '图片预览' }}</p>
        </div>

        <el-descriptions :column="2" border size="small" style="margin-top:14px">
          <el-descriptions-item label="大小">{{ current.size }}</el-descriptions-item>
          <el-descriptions-item label="时长" v-if="current.duration">{{ current.duration }}</el-descriptions-item>
          <el-descriptions-item label="转码">
            <el-tag size="small" :type="current.transcode === 'done' ? 'success' : 'warning'" effect="light">
              {{ current.transcode === 'done' ? '已转码 · 自适应码流' : '排队转码中' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="共享范围">{{ current.scope }}</el-descriptions-item>
        </el-descriptions>

        <!-- 视频内测验锚点 -->
        <template v-if="current.type === 'video'">
          <div class="panel-title" style="margin-top:18px">
            <h3>视频内测验</h3>
            <el-button size="small" type="primary" plain @click="anchorDialog = true">添加锚点</el-button>
          </div>
          <div class="timeline-editor">
            <div class="played" :style="{ width: playedPct + '%' }" />
            <span v-for="a in anchors" :key="a.id" class="tick" :style="{ left: a.pct + '%' }" :title="a.time" @click="editAnchor(a)" />
          </div>
          <div class="anchor-list">
            <div v-for="a in anchors" :key="a.id" class="anchor-item">
              <el-tag size="small" effect="plain" class="mono">{{ a.time }}</el-tag>
              <div class="a-body">
                <strong>{{ a.question }}</strong>
                <span class="muted">{{ a.type }} · {{ a.score }} 分 · {{ a.required ? '必答' : '选答' }}</span>
              </div>
              <el-button link type="primary" size="small" @click="editAnchor(a)">编辑</el-button>
            </div>
            <el-empty v-if="!anchors.length" description="尚未添加测验锚点" :image-size="60" />
          </div>
        </template>
      </template>
    </el-drawer>

    <!-- 添加/编辑锚点 -->
    <el-dialog v-model="anchorDialog" :title="editingAnchor ? '编辑测验锚点' : '添加测验锚点'" width="520px">
      <el-form label-position="top">
        <el-form-item label="出现时间点">
          <el-input v-model="anchorForm.time" placeholder="mm:ss，例如 05:40" style="width:180px" />
        </el-form-item>
        <el-form-item label="题型">
          <el-radio-group v-model="anchorForm.type">
            <el-radio value="单选题">单选题</el-radio>
            <el-radio value="多选题">多选题</el-radio>
            <el-radio value="判断题">判断题</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="题干">
          <el-input v-model="anchorForm.question" type="textarea" :rows="2" placeholder="输入题目内容" />
        </el-form-item>
        <el-form-item label="选项（每行一个）">
          <el-input v-model="anchorForm.options" type="textarea" :rows="4" placeholder="A. 选项一&#10;B. 选项二&#10;C. 选项三" />
        </el-form-item>
        <div style="display:flex; gap:16px">
          <el-form-item label="分值" style="flex:1">
            <el-input-number v-model="anchorForm.score" :min="1" :max="20" />
          </el-form-item>
          <el-form-item label="必答">
            <el-switch v-model="anchorForm.required" />
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="anchorDialog = false">取消</el-button>
        <el-button type="primary" @click="saveAnchor">保存锚点</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { UploadFile } from 'element-plus'
import { previewDocument } from '../utils/kkPreview'
import { globalSearch } from '../stores/search'
import { downloadText } from '../utils/download'
import {
  Search as SearchIcon, Upload, FolderPlus, UploadCloud, Play,
  FileVideo2, FileText, Image as ImageIcon, FileArchive, File as FileIcon
} from 'lucide-vue-next'

type RType = 'video' | 'doc' | 'img' | 'zip'
interface Resource {
  id: number; name: string; type: RType; size: string; folder: string;
  transcode: 'done' | 'doing' | 'fail'; scope: string; expiry: string; time: string; duration?: string
}

const tab = ref('mine')
const keyword = ref(globalSearch.keyword)
globalSearch.keyword = ''
const typeFilter = ref('all')
const transcodeFilter = ref('all')
const uploadDialog = ref(false)
const shareDialog = ref(false)
const previewDrawer = ref(false)
const anchorDialog = ref(false)
const shareScope = ref('课程内')
const shareExpiry = ref('本学期')
const selected = ref<Resource[]>([])
const current = ref<Resource | null>(null)
const tableRef = ref()
const playedPct = ref(34)

const typeMeta: Record<RType, { icon: unknown; bg: string; color: string; label: string }> = {
  video: { icon: FileVideo2, bg: '#eaf0ff', color: '#2f6bff', label: '视频' },
  doc: { icon: FileText, bg: '#e7f6ec', color: '#1a9e50', label: '文档' },
  img: { icon: ImageIcon, bg: '#fff3e2', color: '#d97b00', label: '图片' },
  zip: { icon: FileArchive, bg: '#f3eaff', color: '#7a3ff2', label: '压缩包' }
}

const resources = ref<Resource[]>([
  { id: 1, name: '1.1 平台发展历程回顾.mp4', type: 'video', size: '86.4 MB', folder: '第 1 章', transcode: 'done', scope: '课程内', expiry: '本学期', time: '2026-11-08', duration: '08:24' },
  { id: 2, name: '1.2 短视频运营入门.pdf', type: 'doc', size: '4.8 MB', folder: '第 1 章', transcode: 'done', scope: '私有', expiry: '永久', time: '2026-11-08' },
  { id: 3, name: '2.1 构图与运镜示范.mp4', type: 'video', size: '412 MB', folder: '第 2 章', transcode: 'doing', scope: '私有', expiry: '永久', time: '2026-11-10', duration: '15:42' },
  { id: 4, name: '优秀账号拆解案例.xlsx', type: 'doc', size: '268 KB', folder: '第 3 章', transcode: 'done', scope: '全校共享', expiry: '永久', time: '2026-10-28' },
  { id: 5, name: '3.2 平台推荐机制详解.mp4', type: 'video', size: '196 MB', folder: '第 3 章', transcode: 'done', scope: '课程内', expiry: '本学期', time: '2026-10-25', duration: '14:36' },
  { id: 6, name: '封面图模板合集.zip', type: 'zip', size: '58 MB', folder: '素材库', transcode: 'done', scope: '私有', expiry: '永久', time: '2026-10-20' },
  { id: 7, name: '课堂讨论引导图.png', type: 'img', size: '1.2 MB', folder: '素材库', transcode: 'done', scope: '课程内', expiry: '本学期', time: '2026-10-18' }
])

const categories = [
  { name: '教学视频', count: 42, size: '6.8 GB', icon: FileVideo2, bg: '#eaf0ff', color: '#2f6bff' },
  { name: '教学文档', count: 67, size: '486 MB', icon: FileText, bg: '#e7f6ec', color: '#1a9e50' },
  { name: '图片素材', count: 128, size: '820 MB', icon: ImageIcon, bg: '#fff3e2', color: '#d97b00' },
  { name: '压缩包 / 其他', count: 15, size: '1.2 GB', icon: FileArchive, bg: '#f3eaff', color: '#7a3ff2' }
]

const filteredResources = computed(() =>
  resources.value.filter(r =>
    (typeFilter.value === 'all' || r.type === typeFilter.value) &&
    (transcodeFilter.value === 'all' || r.transcode === transcodeFilter.value) &&
    r.name.includes(keyword.value)
  )
)

interface UploadItem { id: number; name: string; progress: number; timer?: number }
const uploads = ref<UploadItem[]>([])

function onFilePick(file: UploadFile) {
  const item: UploadItem = { id: Date.now() + Math.random(), name: file.name, progress: 0 }
  uploads.value.push(item)
  item.timer = window.setInterval(() => {
    if (item.progress >= 100) { window.clearInterval(item.timer); return }
    item.progress = Math.min(100, item.progress + 5 + Math.floor(Math.random() * 12))
  }, 220)
}

function finishUpload() {
  const videoCount = uploads.value.filter(u => /\.(mp4|mov|avi|mkv)$/i.test(u.name)).length
  uploads.value.forEach(u => {
    resources.value.unshift({
      id: u.id, name: u.name, type: /\.(mp4|mov|avi|mkv)$/i.test(u.name) ? 'video' : 'doc',
      size: '—', folder: '未分类', transcode: 'doing', scope: '私有', expiry: '永久',
      time: new Date().toISOString().slice(0, 10), duration: '00:00'
    })
  })
  uploadDialog.value = false
  uploads.value = []
  ElMessage.success(videoCount ? `上传完成，${videoCount} 个视频已进入转码队列` : '上传完成')
}

function openPreview(row: Resource) {
  // 文档类资源走 kkFileView 在线预览服务（新窗口打开）
  if (row.type === 'doc' && previewDocument(row.name)) {
    ElMessage.success(`正在通过 kkFileView 打开「${row.name}」在线预览`)
    return
  }
  current.value = row
  if (row.type === 'video') loadAnchors(row.id)
  previewDrawer.value = true
}
function shareOne(row: Resource) { current.value = row; shareDialog.value = true }
function downloadOne(row: Resource) {
  downloadText(row.name, `课程资源：${row.name}\n（接入对象存储后替换为真实文件流）`)
  ElMessage.success(`开始下载「${row.name}」`)
}
function deleteOne(row: Resource) {
  ElMessageBox.confirm(`确认删除「${row.name}」？删除后不可恢复。`, '删除资源', { type: 'warning' })
    .then(() => {
      resources.value = resources.value.filter(r => r.id !== row.id)
      ElMessage.success('已删除')
    })
    .catch(() => undefined)
}
function batchShare() { shareDialog.value = true }
function batchImport() { ElMessage.success(`已将 ${selected.value.length} 个资源导入当前课程`) }
function batchDownload() { ElMessage.success(`已创建打包下载任务（${selected.value.length} 个资源），完成后自动下载`) }
function batchDelete() {
  ElMessageBox.confirm(`确认删除选中的 ${selected.value.length} 个资源？`, '批量删除', { type: 'warning' })
    .then(() => {
      const ids = new Set(selected.value.map(s => s.id))
      resources.value = resources.value.filter(r => !ids.has(r.id))
      clearSelection()
      ElMessage.success('已批量删除')
    })
    .catch(() => undefined)
}
function clearSelection() { tableRef.value?.clearSelection() }
function confirmShare() {
  shareDialog.value = false
  ElMessage.success(`共享设置已更新：${shareScope.value} · ${shareExpiry.value}`)
}
function filterByCategory(c: { name: string }) {
  tab.value = 'mine'
  typeFilter.value = c.name.includes('视频') ? 'video' : c.name.includes('文档') ? 'doc' : c.name.includes('图片') ? 'img' : 'zip'
}

/* ===== 视频内测锚点 ===== */
interface Anchor { id: number; time: string; pct: number; question: string; type: string; score: number; required: boolean }
const anchors = ref<Anchor[]>([])
const editingAnchor = ref<Anchor | null>(null)
const anchorForm = ref({ time: '02:10', type: '单选题', question: '', options: '', score: 5, required: true })

function loadAnchors(videoId: number) {
  anchors.value = videoId === 1 || videoId === 5
    ? [
        { id: 1, time: '02:10', pct: 26, question: '平台算法最看重下列哪项指标？', type: '单选题', score: 5, required: true },
        { id: 2, time: '05:40', pct: 62, question: '下列哪些属于有效互动行为？', type: '多选题', score: 5, required: true },
        { id: 3, time: '09:15', pct: 88, question: '完播率越高推荐量一定越大。', type: '判断题', score: 5, required: false }
      ]
    : []
}
function editAnchor(a: Anchor) {
  editingAnchor.value = a
  anchorForm.value = { time: a.time, type: a.type, question: a.question, options: '', score: a.score, required: a.required }
  anchorDialog.value = true
}
function saveAnchor() {
  if (!anchorForm.value.question.trim()) { ElMessage.warning('请输入题干'); return }
  const pct = Math.min(96, Math.max(4, Math.random() * 90))
  if (editingAnchor.value) {
    Object.assign(editingAnchor.value, { time: anchorForm.value.time, type: anchorForm.value.type, question: anchorForm.value.question, score: anchorForm.value.score, required: anchorForm.value.required })
    ElMessage.success('锚点已更新')
  } else {
    anchors.value.push({ id: Date.now(), time: anchorForm.value.time, pct, question: anchorForm.value.question, type: anchorForm.value.type, score: anchorForm.value.score, required: anchorForm.value.required })
    ElMessage.success(`已在 ${anchorForm.value.time} 添加测验锚点`)
  }
  anchorDialog.value = false
  editingAnchor.value = null
  anchorForm.value = { time: '02:10', type: '单选题', question: '', options: '', score: 5, required: true }
}
</script>

<style scoped>
.toolbar { display: flex; gap: 10px; align-items: center; margin-bottom: 14px; flex-wrap: wrap; }
.batch-bar {
  display: flex; align-items: center; gap: 10px; padding: 9px 14px; margin-bottom: 12px;
  background: var(--el-color-primary-light-9); border: 1px solid var(--el-color-primary-light-7);
  border-radius: 8px; font-size: 13px; flex-wrap: wrap;
}
.name-cell { display: flex; align-items: center; gap: 10px; cursor: pointer; }
.name-cell:hover .n { color: var(--el-color-primary); }
.f-icon { width: 32px; height: 32px; border-radius: 8px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.n-meta { display: flex; flex-direction: column; min-width: 0; }
.n { font-size: 13px; font-weight: 600; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.pager { display: flex; justify-content: flex-end; margin-top: 14px; }
.cat-card {
  border: 1px solid var(--app-line); border-radius: 10px; padding: 18px; cursor: pointer;
  display: flex; flex-direction: column; gap: 6px; align-items: flex-start; transition: border-color .15s ease, transform .15s ease;
}
.cat-card:hover { border-color: var(--el-color-primary); transform: translateY(-2px); }
.c-icon { width: 44px; height: 44px; border-radius: 10px; display: flex; align-items: center; justify-content: center; margin-bottom: 4px; }
.up-area { padding: 20px 0 8px; text-align: center; }
.up-area p { margin: 8px 0 0; font-size: 13px; }
.up-list { margin-top: 14px; display: flex; flex-direction: column; gap: 10px; max-height: 220px; overflow-y: auto; }
.up-item { display: flex; gap: 10px; align-items: center; }
.u-meta { flex: 1; min-width: 0; }
.u-row { display: flex; justify-content: space-between; font-size: 12.5px; margin-bottom: 3px; }
.u-name { overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.doc-preview {
  border: 1px dashed var(--app-line); border-radius: 10px; padding: 40px 20px; text-align: center;
  color: var(--app-ink-3); font-size: 13px; display: flex; flex-direction: column; align-items: center; gap: 10px;
}
.anchor-list { display: flex; flex-direction: column; gap: 8px; margin-top: 10px; }
.anchor-item { display: flex; align-items: center; gap: 10px; border: 1px solid var(--app-line); border-radius: 8px; padding: 9px 12px; }
.a-body { flex: 1; display: flex; flex-direction: column; min-width: 0; }
.a-body strong { font-size: 13px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
</style>
