<template>
  <div v-if="course" class="portal-wrap" :class="{ 'preview-mode': previewMode }">
    <!-- ======== 教师编辑工具栏 ======== -->
    <div class="edit-bar panel">
      <el-tag type="warning" effect="dark" size="small">教师编辑模式 · 所见即所得</el-tag>
      <span class="muted eb-hint">正在编辑「{{ course.name }}」的学生端上课页面，点击任意文字直接修改</span>
      <span style="flex:1" />
      <el-checkbox v-model="previewMode" size="small">预览学生视角</el-checkbox>
      <el-dropdown trigger="click" @command="addModule">
        <el-button :icon="Plus">添加模块</el-button>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item v-for="d in moduleDefs" :key="d.type" :command="d.type">
              <div class="md-item">
                <component :is="d.icon" :size="15" />
                <div>
                  <div>{{ d.label }}</div>
                  <div class="md-desc">{{ d.desc }}</div>
                </div>
              </div>
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
      <el-button @click="savePortal">保存草稿</el-button>
      <el-button type="primary" @click="publishPortal">发布更新</el-button>
      <el-button @click="emit('navigate', 'course')">退出预览</el-button>
    </div>

    <!-- ======== 课程横幅（学生端样式） ======== -->
    <div class="portal-banner" :style="{ background: course.cover }">
      <div class="pb-icon"><component :is="course.icon" :size="34" /></div>
      <div class="pb-info">
        <h1 class="pb-title">
          <EditableText v-model="course.name" hint="点击修改课程名称" />
        </h1>
        <p class="pb-meta">{{ course.code }} · {{ course.level }} · {{ course.hours }} 学时 · {{ course.semester }} 学期</p>
        <p class="pb-stats">{{ course.classes }} 个班级 · {{ course.students }} 名学生在学 · {{ course.chapters.length }} 章节 / {{ chapterPoints(course) }} 任务点</p>
      </div>
      <el-popover trigger="click" width="272" placement="bottom-end">
        <template #reference>
          <el-button size="small" class="cover-btn">更换封面</el-button>
        </template>
        <p class="cp-title">选择封面配色</p>
        <div class="cover-picker">
          <button v-for="(g, i) in coverPresets" :key="i" type="button" class="cp-item"
            :class="{ on: course.cover === g }" :style="{ background: g }" @click="course.cover = g" />
        </div>
      </el-popover>
    </div>

    <!-- ======== 门户模块（可拖拽排序） ======== -->
    <div class="module-list">
      <section v-for="(m, mi) in modules" :key="m.id" class="module panel"
        :class="{ off: !m.visible, 'drop-before': isDrop(mi, 'before'), 'drop-after': isDrop(mi, 'after'), dragging: dragIndex === mi }"
        @dragover.prevent="onModuleDragOver($event, mi)" @drop.prevent="commitModuleDrop">
        <header class="mod-head">
          <span class="grip" title="拖拽调整模块位置" draggable="true"
            @dragstart="onModuleDragStart($event, mi)" @dragend="onModuleDragEnd">
            <GripVertical :size="15" />
          </span>
          <component :is="defOf(m).icon" :size="16" class="mod-icon" />
          <h3 class="mod-title"><EditableText v-model="m.title" hint="点击修改模块名称" /></h3>
          <el-tag v-if="!m.visible" size="small" type="info" effect="plain" class="off-tag">学生端不可见</el-tag>
          <span style="flex:1" />
          <span class="vis-label muted">公开显示</span>
          <el-switch v-model="m.visible" size="small" />
          <el-button link type="danger" size="small" @click="removeModule(mi)">删除</el-button>
        </header>

        <div class="mod-body">
          <!-- 课程介绍 / 教学要求：富文本直接编辑 -->
          <template v-if="m.type === 'intro' || m.type === 'requirement'">
            <EditableText v-model="m.data.text" multiline class="richtext" hint="点击直接编辑正文" />
          </template>

          <!-- 课程公告 -->
          <template v-else-if="m.type === 'notice'">
            <div v-for="(n, ni) in itemsOf(m)" :key="n.id" class="row-item">
              <el-tag size="small" effect="light" :type="n.tag === '考试' ? 'danger' : 'primary'">{{ n.tag }}</el-tag>
              <span class="grow"><EditableText v-model="n.title" /></span>
              <span class="muted">{{ n.time }}</span>
              <X :size="14" class="row-del" @click="itemsOf(m).splice(ni, 1)" />
            </div>
            <el-empty v-if="!itemsOf(m).length" description="暂无公告" :image-size="48" />
            <el-button size="small" :icon="Plus" class="add-btn" @click="openAddItem(m, 'notice')">发布公告</el-button>
          </template>

          <!-- 课程资料 -->
          <template v-else-if="m.type === 'material'">
            <div v-for="(f, fi) in itemsOf(m)" :key="f.id" class="row-item">
              <FileText :size="15" class="muted" />
              <span class="grow"><EditableText v-model="f.name" /></span>
              <span class="muted">{{ f.meta }}</span>
              <el-button link type="primary" size="small" @click="previewMaterial(f.name)">预览</el-button>
              <X :size="14" class="row-del" @click="itemsOf(m).splice(fi, 1)" />
            </div>
            <el-empty v-if="!itemsOf(m).length" description="暂无资料" :image-size="48" />
            <el-button size="small" :icon="Plus" class="add-btn" @click="openAddItem(m, 'material')">添加资料</el-button>
          </template>

          <!-- 学习任务 -->
          <template v-else-if="m.type === 'task'">
            <div v-for="(t, ti) in itemsOf(m)" :key="t.id" class="row-item">
              <el-tag size="small" effect="light"
                :type="t.kind === '作业' ? 'warning' : t.kind === '测验' ? 'danger' : 'success'">{{ t.kind }}</el-tag>
              <span class="grow"><EditableText v-model="t.title" /></span>
              <span class="muted">截止 {{ t.deadline }}</span>
              <X :size="14" class="row-del" @click="itemsOf(m).splice(ti, 1)" />
            </div>
            <el-empty v-if="!itemsOf(m).length" description="暂无任务" :image-size="48" />
            <el-button size="small" :icon="Plus" class="add-btn" @click="openAddItem(m, 'task')">添加任务</el-button>
          </template>

          <!-- 资源链接 -->
          <template v-else-if="m.type === 'link'">
            <div v-for="(l, li) in itemsOf(m)" :key="l.id" class="row-item link-item">
              <Link2 :size="15" class="muted" />
              <div class="grow">
                <div><EditableText v-model="l.title" /></div>
                <div class="muted link-url">{{ l.url }}<template v-if="l.desc"> · {{ l.desc }}</template></div>
              </div>
              <X :size="14" class="row-del" @click="itemsOf(m).splice(li, 1)" />
            </div>
            <el-empty v-if="!itemsOf(m).length" description="暂无链接" :image-size="48" />
            <el-button size="small" :icon="Plus" class="add-btn" @click="openAddItem(m, 'link')">添加链接</el-button>
          </template>

          <!-- 教师团队 -->
          <template v-else-if="m.type === 'team'">
            <div class="team-grid">
              <div v-for="(mb, mbi) in membersOf(m)" :key="mb.id" class="teacher-card">
                <X :size="13" class="row-del tc-del" @click="membersOf(m).splice(mbi, 1)" />
                <div class="tc-avatar">{{ mb.name[0] }}</div>
                <div class="tc-name"><EditableText v-model="mb.name" /></div>
                <div class="tc-role">
                  <el-tag size="small" effect="light" :type="mb.role === '主讲教师' ? 'primary' : 'success'">
                    <EditableText v-model="mb.role" />
                  </el-tag>
                </div>
                <div class="muted tc-dept"><EditableText v-model="mb.dept" /></div>
                <p class="tc-bio"><EditableText v-model="mb.bio" multiline hint="点击编辑教师简介" /></p>
              </div>
              <button type="button" class="teacher-card tc-add" @click="openAddItem(m, 'team')">
                <Plus :size="20" />
                <span>添加成员</span>
              </button>
            </div>
          </template>
        </div>
      </section>

      <el-empty v-if="!modules.length" description="门户还没有模块，点击右上角「添加模块」开始搭建" :image-size="72" class="panel" />
    </div>

    <!-- ======== 添加内容弹窗（公告/资料/任务/链接/成员 共用） ======== -->
    <el-dialog v-model="addDialogVisible" :title="addDialogTitle" width="480px">
      <el-form label-position="top">
        <template v-if="addTarget?.type === 'notice'">
          <el-form-item label="公告类型">
            <el-radio-group v-model="addForm.tag">
              <el-radio-button v-for="t in ['教学安排', '考试', '作业', '通知']" :key="t" :value="t">{{ t }}</el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="公告内容" required>
            <el-input v-model="addForm.title" type="textarea" :rows="2" placeholder="例如：第 3 章将于 11 月 24 日 08:00 定时开放" />
          </el-form-item>
        </template>
        <template v-else-if="addTarget?.type === 'material'">
          <el-form-item label="资料名称" required>
            <el-input v-model="addForm.name" placeholder="例如：第 3 章讲义.pdf" />
          </el-form-item>
          <el-form-item label="资料类型">
            <el-radio-group v-model="addForm.kind">
              <el-radio-button v-for="t in ['PDF', 'PPT', '视频', '文档', '压缩包']" :key="t" :value="t">{{ t }}</el-radio-button>
            </el-radio-group>
          </el-form-item>
        </template>
        <template v-else-if="addTarget?.type === 'task'">
          <el-form-item label="任务名称" required>
            <el-input v-model="addForm.title" placeholder="例如：观看 3.1 推荐算法拆解" />
          </el-form-item>
          <el-form-item label="任务类型">
            <el-radio-group v-model="addForm.kind">
              <el-radio-button v-for="t in ['视频任务点', '作业', '测验']" :key="t" :value="t">{{ t }}</el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="截止时间">
            <el-input v-model="addForm.deadline" placeholder="例如：11-30 22:00" />
          </el-form-item>
        </template>
        <template v-else-if="addTarget?.type === 'link'">
          <el-form-item label="链接标题" required>
            <el-input v-model="addForm.title" placeholder="例如：剪映官方教程中心" />
          </el-form-item>
          <el-form-item label="链接地址" required>
            <el-input v-model="addForm.url" placeholder="https://" />
          </el-form-item>
          <el-form-item label="备注说明">
            <el-input v-model="addForm.desc" placeholder="可选" />
          </el-form-item>
        </template>
        <template v-else-if="addTarget?.type === 'team'">
          <el-form-item label="姓名" required>
            <el-input v-model="addForm.name" placeholder="教师姓名" />
          </el-form-item>
          <el-form-item label="角色">
            <el-radio-group v-model="addForm.role">
              <el-radio-button v-for="r in ['主讲教师', '助教', '共建者']" :key="r" :value="r">{{ r }}</el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="所属教研室">
            <el-input v-model="addForm.dept" placeholder="例如：数字媒体教研室" />
          </el-form-item>
          <el-form-item label="教师简介">
            <el-input v-model="addForm.bio" type="textarea" :rows="3" placeholder="研究方向、教学经历等" />
          </el-form-item>
        </template>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAddItem">{{ addConfirmText }}</el-button>
      </template>
    </el-dialog>
  </div>

  <!-- 未选择课程的兜底 -->
  <div v-else class="panel empty-box">
    <p class="muted">请先在「课程建设」中选择一门课程，再点击「预览课程」进入门户编辑</p>
    <el-button type="primary" @click="emit('navigate', 'course')">前往课程建设</el-button>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { GripVertical, Plus, X, FileText, Link2 } from 'lucide-vue-next'
import EditableText from '../components/EditableText.vue'
import { courses, chapterPoints, portalCourseId } from '../stores/courses'
import { previewDocument } from '../utils/kkPreview'

/** 课程资料在线预览：走 kkFileView 文档服务 */
function previewMaterial(name: string) {
  if (previewDocument(name)) ElMessage.success(`正在通过 kkFileView 打开「${name}」在线预览`)
  else ElMessage.info('该类型暂不支持在线预览，请下载后查看')
}
import {
  portalModules, moduleDefs, ensurePortalModules, createModule,
  type PortalModule, type ModuleType
} from '../stores/portal'

const emit = defineEmits<{ navigate: [id: string] }>()

const course = computed(() => courses.value.find(c => c.id === portalCourseId.value) ?? null)
const modules = computed<PortalModule[]>(() => {
  const c = course.value
  return c ? ensurePortalModules(c) : []
})
const defOf = (m: PortalModule) => moduleDefs.find(d => d.type === m.type)!

const coverPresets = [
  'linear-gradient(135deg,#2f6bff,#6b4dff)',
  'linear-gradient(135deg,#0ea5a4,#22c55e)',
  'linear-gradient(135deg,#f59e0b,#ef4444)',
  'linear-gradient(135deg,#8b5cf6,#ec4899)',
  'linear-gradient(135deg,#2563eb,#06b6d4)',
  'linear-gradient(135deg,#0f766e,#14b8a6)'
]

const previewMode = ref(false)

/* ---------- 模块增删 / 公开显示 ---------- */
function addModule(type: ModuleType) {
  const m = createModule(type)
  modules.value.push(m)
  nextTick(() => {
    document.querySelectorAll('.module-list .module')[modules.value.length - 1]
      ?.scrollIntoView({ behavior: 'smooth', block: 'center' })
  })
  ElMessage.success(`已添加「${m.title}」模块，点击文字即可编辑`)
}
function removeModule(mi: number) {
  const [removed] = modules.value.splice(mi, 1)
  ElMessage.success(`已删除「${removed.title}」模块（学生端同步移除）`)
}

/* ---------- 模块拖拽排序 ---------- */
const dragIndex = ref<number | null>(null)
const dropIndex = ref<{ index: number; pos: 'before' | 'after' } | null>(null)

function onModuleDragStart(e: DragEvent, index: number) {
  dragIndex.value = index
  e.dataTransfer?.setData('text/plain', 'module')
  if (e.dataTransfer) e.dataTransfer.effectAllowed = 'move'
}
function onModuleDragEnd() {
  dragIndex.value = null
  dropIndex.value = null
}
function onModuleDragOver(e: DragEvent, mi: number) {
  if (dragIndex.value === null) return
  const rect = (e.currentTarget as HTMLElement).getBoundingClientRect()
  const pos = e.clientY < rect.top + rect.height / 2 ? 'before' : 'after'
  dropIndex.value = { index: mi, pos }
}
function isDrop(mi: number, pos: 'before' | 'after') {
  return dropIndex.value?.index === mi && dropIndex.value.pos === pos
}
function commitModuleDrop() {
  const from = dragIndex.value
  const t = dropIndex.value
  const list = modules.value
  if (from === null || !t) { onModuleDragEnd(); return }
  let to = t.index + (t.pos === 'after' ? 1 : 0)
  if (to !== from && to !== from + 1) {
    const [moved] = list.splice(from, 1)
    if (from < to) to--
    list.splice(to, 0, moved)
    ElMessage.success('模块位置已调整')
  }
  onModuleDragEnd()
}

/* ---------- 模块内容条目 ---------- */
const itemsOf = (m: PortalModule): any[] => (m.data.items ??= [])
const membersOf = (m: PortalModule): any[] => (m.data.members ??= [])

/* ---------- 添加内容弹窗 ---------- */
const addDialogVisible = ref(false)
const addTarget = ref<PortalModule | null>(null)
const addForm = ref({ tag: '教学安排', title: '', name: '', kind: 'PDF', url: '', desc: '', deadline: '', role: '助教', dept: '', bio: '' })

const addMeta: Record<string, { title: string; confirm: string }> = {
  notice: { title: '发布公告', confirm: '发布' },
  material: { title: '添加课程资料', confirm: '添加' },
  task: { title: '添加学习任务', confirm: '添加' },
  link: { title: '添加资源链接', confirm: '添加' },
  team: { title: '添加教师成员', confirm: '添加' }
}
const addDialogTitle = computed(() => addTarget.value ? addMeta[addTarget.value.type].title : '')
const addConfirmText = computed(() => addTarget.value ? addMeta[addTarget.value.type].confirm : '确定')

function openAddItem(m: PortalModule, _type: ModuleType) {
  addTarget.value = m
  addForm.value = { tag: '教学安排', title: '', name: '', kind: 'PDF', url: '', desc: '', deadline: '', role: '助教', dept: '', bio: '' }
  addDialogVisible.value = true
}
function nowText() {
  const d = new Date()
  const p = (n: number) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())} ${p(d.getHours())}:${p(d.getMinutes())}`
}
function confirmAddItem() {
  const m = addTarget.value
  if (!m) return
  const f = addForm.value
  const id = Date.now()
  switch (m.type) {
    case 'notice':
      if (!f.title.trim()) { ElMessage.warning('请输入公告内容'); return }
      itemsOf(m).unshift({ id, tag: f.tag, title: f.title.trim(), time: nowText() })
      ElMessage.success('公告已发布，学生端实时可见')
      break
    case 'material':
      if (!f.name.trim()) { ElMessage.warning('请输入资料名称'); return }
      itemsOf(m).unshift({ id, name: f.name.trim(), meta: `${f.kind} · 刚刚上传` })
      ElMessage.success('资料已添加')
      break
    case 'task':
      if (!f.title.trim()) { ElMessage.warning('请输入任务名称'); return }
      itemsOf(m).push({ id, title: f.title.trim(), kind: f.kind, deadline: f.deadline.trim() || '不限时' })
      ElMessage.success('任务已添加')
      break
    case 'link':
      if (!f.title.trim() || !f.url.trim()) { ElMessage.warning('请填写链接标题与地址'); return }
      itemsOf(m).push({ id, title: f.title.trim(), url: f.url.trim(), desc: f.desc.trim() })
      ElMessage.success('链接已添加')
      break
    case 'team':
      if (!f.name.trim()) { ElMessage.warning('请输入教师姓名'); return }
      membersOf(m).push({ id, name: f.name.trim(), role: f.role, dept: f.dept.trim() || '信息工程学院', bio: f.bio.trim() || '（点击编辑教师简介）' })
      ElMessage.success('成员已加入教师团队')
      break
  }
  addDialogVisible.value = false
}

/* ---------- 保存 / 发布 ---------- */
function savePortal() {
  ElMessage.success('门户草稿已保存 · ' + new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }))
}
function publishPortal() {
  if (course.value) {
    course.value.status = '已发布'
    course.value.updated = new Date().toISOString().slice(0, 10)
  }
  ElMessage.success(`「${course.value?.name}」门户页面已发布，学生端同步更新`)
}
</script>

<style scoped>
.portal-wrap { max-width: 980px; margin: 0 auto; }
.empty-box { display: flex; flex-direction: column; align-items: center; gap: 12px; padding: 60px 0; }

/* 编辑工具栏 */
.edit-bar { display: flex; align-items: center; gap: 12px; flex-wrap: wrap; padding: 10px 16px; position: sticky; top: 0; z-index: 20; }
.eb-hint { font-size: 12.5px; }
.md-item { display: flex; align-items: flex-start; gap: 8px; padding: 2px 0; }
.md-desc { font-size: 11px; color: var(--app-ink-3); }

/* 课程横幅 */
.portal-banner {
  border-radius: 14px; color: #fff; padding: 30px 34px; display: flex; align-items: center; gap: 18px;
  position: relative; margin-bottom: 16px;
}
.pb-icon {
  width: 68px; height: 68px; border-radius: 14px; background: rgba(255, 255, 255, .18);
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}
.pb-info { flex: 1; min-width: 0; }
.pb-title { margin: 0; font-size: 24px; font-weight: 700; }
.pb-title :deep(.editable:hover) { background: rgba(255, 255, 255, .14); }
.pb-meta { margin: 6px 0 0; font-size: 13px; opacity: .88; }
.pb-stats { margin: 4px 0 0; font-size: 12.5px; opacity: .72; }
.cover-btn { background: rgba(255, 255, 255, .16); border-color: rgba(255, 255, 255, .3); color: #fff; }
.cover-btn:hover { background: rgba(255, 255, 255, .26); color: #fff; }
.cp-title { margin: 0 0 8px; font-size: 12.5px; color: var(--app-ink-3); }
.cover-picker { display: flex; flex-wrap: wrap; gap: 8px; }
.cp-item { width: 56px; height: 36px; border-radius: 8px; border: 2px solid transparent; cursor: pointer; }
.cp-item.on { border-color: var(--el-color-primary); }

/* 模块 */
.module-list { display: flex; flex-direction: column; gap: 14px; }
.module { padding: 0; overflow: hidden; transition: box-shadow .15s ease; }
.module.drop-before { box-shadow: inset 0 3px 0 var(--el-color-primary); }
.module.drop-after { box-shadow: inset 0 -3px 0 var(--el-color-primary); }
.module.dragging { opacity: .45; }
.module.off .mod-body { opacity: .45; filter: grayscale(.4); }
.mod-head {
  display: flex; align-items: center; gap: 8px; padding: 12px 16px;
  border-bottom: 1px solid var(--app-line); background: #fbfcff;
}
.mod-icon { color: var(--el-color-primary); }
.mod-title { margin: 0; font-size: 15px; font-weight: 700; }
.off-tag { margin-left: 6px; }
.vis-label { font-size: 12px; }
.mod-body { padding: 14px 16px; }
.richtext { display: block; white-space: pre-wrap; line-height: 1.8; font-size: 14px; color: var(--app-ink-2); }

/* 列表条目 */
.row-item { display: flex; align-items: center; gap: 10px; padding: 9px 6px; border-radius: 8px; font-size: 13.5px; }
.row-item:hover { background: #f7f9ff; }
.row-item .grow { flex: 1; min-width: 0; }
.row-del { color: var(--app-ink-3); cursor: pointer; flex-shrink: 0; opacity: 0; }
.row-item:hover .row-del, .teacher-card:hover .row-del { opacity: 1; }
.row-del:hover { color: var(--el-color-danger); }
.link-url { font-size: 12px; }
.add-btn { margin-top: 8px; border-style: dashed; width: 100%; }

/* 教师团队 */
.team-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(220px, 1fr)); gap: 12px; }
.teacher-card { position: relative; border: 1px solid var(--app-line); border-radius: 10px; padding: 16px 14px; text-align: center; }
.tc-avatar {
  width: 52px; height: 52px; border-radius: 50%; margin: 0 auto 8px; font-size: 20px; font-weight: 700;
  background: var(--el-color-primary-light-8); color: var(--el-color-primary);
  display: flex; align-items: center; justify-content: center;
}
.tc-name { font-weight: 700; font-size: 14.5px; }
.tc-role { margin: 5px 0 3px; }
.tc-dept { font-size: 12px; }
.tc-bio { margin: 8px 0 0; font-size: 12.5px; color: var(--app-ink-2); line-height: 1.7; text-align: left; }
.tc-del { position: absolute; top: 8px; right: 8px; }
.tc-add {
  border: 1.5px dashed var(--app-line); border-radius: 10px; background: none; cursor: pointer;
  display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 6px;
  color: var(--app-ink-3); font-size: 13px; min-height: 150px;
}
.tc-add:hover { border-color: var(--el-color-primary); color: var(--el-color-primary); }

/* 学生视角预览：隐藏编辑痕迹与不可见模块 */
.preview-mode .edit-bar .el-dropdown,
.preview-mode .edit-bar .el-button:not(.keep) { display: none; }
.preview-mode .grip,
.preview-mode .vis-label,
.preview-mode .mod-head .el-switch,
.preview-mode .mod-head .el-button,
.preview-mode .off-tag,
.preview-mode .row-del,
.preview-mode .add-btn,
.preview-mode .tc-add,
.preview-mode .cover-btn { display: none; }
.preview-mode .module.off { display: none; }
.preview-mode :deep(.editable:hover) { background: none; outline: none; cursor: default; }
.preview-mode :deep(.editable .pen) { display: none; }
.preview-mode :deep(.editable) { pointer-events: none; }
</style>
