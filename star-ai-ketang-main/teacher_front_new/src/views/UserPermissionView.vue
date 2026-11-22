<template>
  <div>
    <el-tabs v-model="tab" class="panel tabs-panel">
      <!-- ================= 用户管理 ================= -->
      <el-tab-pane label="用户管理" name="users">
        <div class="users-layout">
          <!-- 组织树 -->
          <div class="tree-col">
            <div class="panel-title"><h3>组织机构</h3></div>
            <el-tree
              :data="orgTree"
              :props="{ label: 'name', children: 'children' }"
              default-expand-all
              highlight-current
              node-key="id"
              :current-node-key="currentOrg"
              @node-click="(n: OrgNode) => currentOrg = n.id"
            >
              <template #default="{ data }">
                <span class="tree-node">
                  <Building2 v-if="data.depth === 0" :size="14" />
                  <School v-else :size="14" />
                  <span>{{ data.name }}</span>
                  <span class="muted">({{ data.count }})</span>
                </span>
              </template>
            </el-tree>
          </div>

          <!-- 用户表 -->
          <div class="table-col">
            <div class="toolbar">
              <el-input v-model="keyword" placeholder="搜索姓名 / 账号" :prefix-icon="SearchIcon" style="width:200px" clearable />
              <el-select v-model="roleFilter" placeholder="角色" style="width:120px">
                <el-option label="全部角色" value="all" />
                <el-option v-for="r in roleNames" :key="r" :label="r" :value="r" />
              </el-select>
              <span style="flex:1" />
              <el-button @click="importDialog = true">批量导入</el-button>
              <el-button type="primary" @click="openAddUser">新增用户</el-button>
            </div>

            <el-table :data="filteredUsers" border stripe>
              <el-table-column label="用户" min-width="170">
                <template #default="{ row }">
                  <div class="u-cell">
                    <div class="u-avatar" :style="{ background: avatarBg(row.roles) }">{{ row.name[0] }}</div>
                    <div>
                      <div style="font-weight:600">{{ row.name }}</div>
                      <div class="muted mono">{{ row.account }}</div>
                    </div>
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="角色" width="150">
                <template #default="{ row }">
                  <el-tag v-for="r in row.roles" :key="r" size="small" effect="light"
                    :type="r === '超级管理员' ? 'danger' : r === '管理员' ? 'warning' : r === '教师' ? 'primary' : 'success'"
                    style="margin-right:4px">{{ r }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="org" label="所属机构" width="160" show-overflow-tooltip />
              <el-table-column label="状态" width="80">
                <template #default="{ row }">
                  <el-tag size="small" :type="row.status === '正常' ? 'success' : 'info'" effect="plain">{{ row.status }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="lastLogin" label="最近登录" width="150" />
              <el-table-column label="操作" width="190" fixed="right">
                <template #default="{ row }">
                  <el-button link type="primary" size="small" @click="editUser(row)">编辑</el-button>
                  <el-button link size="small" @click="resetPwd(row)">重置密码</el-button>
                  <el-button link type="danger" size="small" @click="deleteUser(row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>

            <div class="pager">
              <el-pagination layout="total, prev, pager, next" :total="filteredUsers.length" :page-size="10" />
            </div>
          </div>
        </div>
      </el-tab-pane>

      <!-- ================= 角色管理 ================= -->
      <el-tab-pane label="角色管理" name="roles">
        <div class="grid-4" style="margin-bottom:16px">
          <div
            v-for="r in roles" :key="r.name"
            class="role-card" :class="{ active: r.name === currentRole }"
            @click="currentRole = r.name"
          >
            <div class="rc-head">
              <div class="rc-icon" :style="{ background: r.bg, color: r.color }">
                <component :is="r.icon" :size="18" />
              </div>
              <strong>{{ r.name }}</strong>
            </div>
            <p class="muted">{{ r.desc }}</p>
            <span class="rc-count">{{ r.count }} 人</span>
          </div>
        </div>

        <div class="panel-title">
          <h3>「{{ currentRole }}」功能权限矩阵</h3>
          <el-button size="small" type="primary" @click="savePerms">保存权限</el-button>
        </div>
        <el-table :data="permMatrix" border>
          <el-table-column prop="module" label="功能模块" width="180" />
          <el-table-column v-for="p in permTypes" :key="p" :label="p" align="center">
            <template #default="{ row }">
              <el-checkbox :model-value="row.perms[p]" @change="v => row.perms[p] = Boolean(v)" />
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- ================= 组织机构 ================= -->
      <el-tab-pane label="组织机构" name="org">
        <div class="toolbar">
          <el-button type="primary" @click="orgDialog = true">新增机构</el-button>
          <span class="muted">支持多级院系 / 专业 / 班级结构，用户按机构批量授权</span>
        </div>
        <div class="org-grid">
          <div v-for="org in orgList" :key="org.name" class="org-card">
            <div class="oc-head">
              <strong>{{ org.name }}</strong>
              <el-tag size="small" effect="plain">{{ org.type }}</el-tag>
            </div>
            <p class="muted">负责人：{{ org.leader }} · 成员 {{ org.count }} 人</p>
            <p class="muted">下级：{{ org.children }}</p>
            <div class="oc-actions">
              <el-button link type="primary" size="small">编辑</el-button>
              <el-button link size="small">添加下级</el-button>
              <el-button link type="danger" size="small">删除</el-button>
            </div>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 新增/编辑用户 -->
    <el-dialog v-model="userDialog" :title="editingUser ? '编辑用户' : '新增用户'" width="480px">
      <el-form label-position="top">
        <div class="grid-2">
          <el-form-item label="姓名"><el-input v-model="userForm.name" /></el-form-item>
          <el-form-item :label="userForm.role === '学生' ? '学号' : '工号'"><el-input v-model="userForm.account" /></el-form-item>
        </div>
        <el-form-item label="角色">
          <el-checkbox-group v-model="userForm.roles">
            <el-checkbox v-for="r in roleNames" :key="r" :value="r">{{ r }}</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="所属机构">
          <el-cascader v-model="userForm.orgPath" :options="orgCascader" placeholder="选择院系 / 班级" style="width:100%" />
        </el-form-item>
        <div class="grid-2">
          <el-form-item label="手机号"><el-input v-model="userForm.phone" /></el-form-item>
          <el-form-item label="邮箱"><el-input v-model="userForm.email" /></el-form-item>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="userDialog = false">取消</el-button>
        <el-button type="primary" @click="saveUser">保存</el-button>
      </template>
    </el-dialog>

    <!-- 批量导入 -->
    <el-dialog v-model="importDialog" title="批量导入用户" width="520px">
      <el-steps :active="1" align-center style="margin-bottom:20px">
        <el-step title="下载模板" />
        <el-step title="上传文件" />
        <el-step title="完成导入" />
      </el-steps>
      <el-upload drag :auto-upload="false" :show-file-list="false" :on-change="f => importFile = f.name">
        <div style="padding:16px 0 6px; text-align:center">
          <FileSpreadsheet :size="36" style="color: var(--el-color-primary)" />
          <p style="margin:8px 0 0; font-size:13px">拖拽 Excel 到此处，或 <em>点击上传</em></p>
          <p class="muted">字段：姓名 / 账号 / 角色 / 机构 / 手机号，单次最多 2000 条</p>
        </div>
      </el-upload>
      <el-alert v-if="importFile" type="success" :closable="false" style="margin-top:12px"
        :title="`已选择「${importFile}」，校验通过：新增 118 人，更新 6 人，无异常。`" />
      <template #footer>
        <el-button link type="primary" @click="downloadTemplate">下载模板</el-button>
        <span style="flex:1" />
        <el-button @click="importDialog = false">取消</el-button>
        <el-button type="primary" :disabled="!importFile" @click="confirmImport">开始导入</el-button>
      </template>
    </el-dialog>

    <!-- 新增机构 -->
    <el-dialog v-model="orgDialog" title="新增机构" width="440px">
      <el-form label-position="top">
        <el-form-item label="上级机构">
          <el-cascader :options="orgCascader" placeholder="留空则为顶级机构" style="width:100%" clearable />
        </el-form-item>
        <el-form-item label="机构名称">
          <el-input v-model="newOrgName" placeholder="例如：信息工程学院 / 2024级软件3班" />
        </el-form-item>
        <el-form-item label="负责人">
          <el-input placeholder="选填" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="orgDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmAddOrg">创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { downloadCsv } from '../utils/download'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { UploadFile } from 'element-plus'
import {
  Search as SearchIcon, Building2, School, FileSpreadsheet,
  GraduationCap, Presentation, ShieldCheck, Crown
} from 'lucide-vue-next'

interface OrgNode { id: number; name: string; count: number; depth: number; children?: OrgNode[] }
interface User {
  id: number; name: string; account: string; roles: string[]; org: string;
  status: string; lastLogin: string
}

const tab = ref('users')
const keyword = ref('')
const roleFilter = ref('all')
const currentOrg = ref(1)
const userDialog = ref(false)
const importDialog = ref(false)
const orgDialog = ref(false)
const importFile = ref('')
const newOrgName = ref('')
const editingUser = ref<User | null>(null)
const currentRole = ref('教师')

const roleNames = ['学生', '教师', '管理员', '超级管理员']

const orgTree: OrgNode[] = [
  {
    id: 1, name: '重庆数字职业学院', count: 3186, depth: 0,
    children: [
      {
        id: 2, name: '信息工程学院', count: 1240, depth: 1,
        children: [
          { id: 5, name: '2024级软件1班', count: 40, depth: 2 },
          { id: 6, name: '2024级软件2班', count: 40, depth: 2 },
          { id: 7, name: '2023级数媒1班', count: 38, depth: 2 }
        ]
      },
      { id: 3, name: '电子商务学院', count: 986, depth: 1 },
      { id: 4, name: '数字媒体学院', count: 960, depth: 1 }
    ]
  }
]

const orgCascader = [
  {
    value: '信息工程学院', label: '信息工程学院',
    children: [
      { value: '2024级软件1班', label: '2024级软件1班' },
      { value: '2024级软件2班', label: '2024级软件2班' },
      { value: '2023级数媒1班', label: '2023级数媒1班' }
    ]
  },
  { value: '电子商务学院', label: '电子商务学院' },
  { value: '数字媒体学院', label: '数字媒体学院' }
]

const users = ref<User[]>([
  { id: 1, name: '王琳', account: 'T2019012', roles: ['教师'], org: '信息工程学院', status: '正常', lastLogin: '2026-11-19 08:42' },
  { id: 2, name: '李思颖', account: 'T2021035', roles: ['教师'], org: '数字媒体学院', status: '正常', lastLogin: '2026-11-18 17:20' },
  { id: 3, name: '刘洋', account: '20240115', roles: ['学生'], org: '2024级软件1班', status: '正常', lastLogin: '2026-11-19 07:58' },
  { id: 4, name: '陈思雨', account: '20240116', roles: ['学生'], org: '2024级软件1班', status: '正常', lastLogin: '2026-11-18 22:11' },
  { id: 5, name: '张建国', account: 'A1002', roles: ['管理员'], org: '教务处', status: '正常', lastLogin: '2026-11-19 09:03' },
  { id: 6, name: '系统超管', account: 'root', roles: ['超级管理员'], org: '信息中心', status: '正常', lastLogin: '2026-11-17 14:36' },
  { id: 7, name: '赵可欣', account: '20240120', roles: ['学生'], org: '2024级软件1班', status: '停用', lastLogin: '2026-10-30 11:12' }
])

const roles = [
  { name: '学生', desc: '课程学习、作业考试、讨论笔记', count: 2958, icon: GraduationCap, bg: '#e7f6ec', color: '#1a9e50' },
  { name: '教师', desc: '建课、资源、作业批阅与学情', count: 204, icon: Presentation, bg: '#eaf0ff', color: '#2f6bff' },
  { name: '管理员', desc: '教务管理、考试与数据报表', count: 22, icon: ShieldCheck, bg: '#fff3e2', color: '#d97b00' },
  { name: '超级管理员', desc: '系统配置、权限与机构管理', count: 2, icon: Crown, bg: '#ffe9e9', color: '#d4380d' }
]

const permTypes = ['访问', '创建', '编辑', '删除', '管理']
const permMatrix = ref([
  { module: '课程建设', perms: { 访问: true, 创建: true, 编辑: true, 删除: false, 管理: false } as Record<string, boolean> },
  { module: '资源库', perms: { 访问: true, 创建: true, 编辑: true, 删除: true, 管理: false } as Record<string, boolean> },
  { module: '题库与试卷', perms: { 访问: true, 创建: true, 编辑: true, 删除: false, 管理: false } as Record<string, boolean> },
  { module: '作业与批阅', perms: { 访问: true, 创建: true, 编辑: true, 删除: false, 管理: false } as Record<string, boolean> },
  { module: '测验考试', perms: { 访问: true, 创建: true, 编辑: false, 删除: false, 管理: false } as Record<string, boolean> },
  { module: '成绩统计', perms: { 访问: true, 创建: false, 编辑: true, 删除: false, 管理: false } as Record<string, boolean> },
  { module: '用户与权限', perms: { 访问: false, 创建: false, 编辑: false, 删除: false, 管理: false } as Record<string, boolean> }
])

const orgList = [
  { name: '信息工程学院', type: '学院', leader: '周正华', count: 1240, children: '软件技术 / 大数据 / 人工智能（12 个班级）' },
  { name: '电子商务学院', type: '学院', leader: '林晓芸', count: 986, children: '电子商务 / 跨境电商（9 个班级）' },
  { name: '数字媒体学院', type: '学院', leader: '吴一凡', count: 960, children: '数字媒体 / 影视制作（8 个班级）' },
  { name: '2024级软件1班', type: '班级', leader: '王琳（辅导员）', count: 40, children: '—' }
]

const filteredUsers = computed(() =>
  users.value.filter(u =>
    (roleFilter.value === 'all' || u.roles.includes(roleFilter.value)) &&
    (u.name.includes(keyword.value) || u.account.includes(keyword.value))
  )
)

const userForm = ref({
  name: '', account: '', role: '学生', roles: ['学生'] as string[],
  orgPath: [] as string[], phone: '', email: ''
})

function avatarBg(roleList: string[]) {
  const r = roleList[0]
  return r === '超级管理员' ? '#ffe9e9' : r === '管理员' ? '#fff3e2' : r === '教师' ? '#eaf0ff' : '#e7f6ec'
}
function openAddUser() {
  editingUser.value = null
  userForm.value = { name: '', account: '', role: '学生', roles: ['学生'], orgPath: [], phone: '', email: '' }
  userDialog.value = true
}
function editUser(row: User) {
  editingUser.value = row
  userForm.value = { name: row.name, account: row.account, role: row.roles[0], roles: [...row.roles], orgPath: [], phone: '', email: '' }
  userDialog.value = true
}
function downloadTemplate() {
  downloadCsv('用户导入模板.csv', ['姓名', '账号', '角色', '组织机构'], [['张三', 'zhangsan', '学生', '信息工程学院/2024级/软件1班']])
  ElMessage.success('导入模板已开始下载')
}
function saveUser() {
  if (!userForm.value.name.trim() || !userForm.value.account.trim()) { ElMessage.warning('请填写姓名与账号'); return }
  if (editingUser.value) {
    Object.assign(editingUser.value, { name: userForm.value.name, account: userForm.value.account, roles: [...userForm.value.roles] })
    ElMessage.success('用户信息已更新')
  } else {
    users.value.unshift({
      id: Date.now(), name: userForm.value.name.trim(), account: userForm.value.account.trim(),
      roles: [...userForm.value.roles], org: userForm.value.orgPath.join(' / ') || '未分配',
      status: '正常', lastLogin: '—'
    })
    ElMessage.success('用户已创建，初始密码已通过短信发送')
  }
  userDialog.value = false
}
function resetPwd(row: User) {
  ElMessageBox.confirm(`确认重置「${row.name}」的登录密码？新密码将通过短信发送。`, '重置密码', { type: 'warning' })
    .then(() => ElMessage.success('密码已重置并发送'))
    .catch(() => undefined)
}
function deleteUser(row: User) {
  ElMessageBox.confirm(`确认删除用户「${row.name}」？其学习数据将保留但不可登录。`, '删除用户', { type: 'warning' })
    .then(() => {
      users.value = users.value.filter(u => u.id !== row.id)
      ElMessage.success('已删除')
    })
    .catch(() => undefined)
}
function confirmImport() {
  importDialog.value = false
  importFile.value = ''
  ElMessage.success('导入完成：新增 118 人，更新 6 人')
}
function confirmAddOrg() {
  if (!newOrgName.value.trim()) { ElMessage.warning('请输入机构名称'); return }
  orgDialog.value = false
  newOrgName.value = ''
  ElMessage.success('机构已创建')
}
function savePerms() { ElMessage.success(`「${currentRole.value}」权限已更新，即时生效`) }
</script>

<style scoped>
.tabs-panel :deep(.el-tabs__header) { margin-bottom: 18px; }
.users-layout { display: grid; grid-template-columns: 260px 1fr; gap: 18px; }
.tree-col { border-right: 1px solid #f2f4f9; padding-right: 16px; }
.tree-node { display: inline-flex; align-items: center; gap: 6px; font-size: 13px; }
.toolbar { display: flex; gap: 10px; align-items: center; margin-bottom: 14px; flex-wrap: wrap; }
.u-cell { display: flex; align-items: center; gap: 10px; }
.u-avatar { width: 32px; height: 32px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-weight: 700; font-size: 13px; color: var(--app-ink); flex-shrink: 0; }
.pager { display: flex; justify-content: flex-end; margin-top: 14px; }
.role-card { border: 1px solid var(--app-line); border-radius: 10px; padding: 16px; cursor: pointer; transition: border-color .15s ease, transform .15s ease; }
.role-card:hover { transform: translateY(-2px); }
.role-card.active { border-color: var(--el-color-primary); box-shadow: 0 0 0 3px var(--el-color-primary-light-9); }
.rc-head { display: flex; align-items: center; gap: 10px; margin-bottom: 8px; }
.rc-icon { width: 36px; height: 36px; border-radius: 9px; display: flex; align-items: center; justify-content: center; }
.rc-count { font-size: 12px; color: var(--el-color-primary); font-weight: 700; }
.org-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 14px; }
.org-card { border: 1px solid var(--app-line); border-radius: 10px; padding: 16px; }
.oc-head { display: flex; align-items: center; justify-content: space-between; margin-bottom: 8px; }
.oc-actions { margin-top: 10px; }
@media (max-width: 960px) {
  .users-layout { grid-template-columns: 1fr; }
  .tree-col { border-right: none; padding-right: 0; }
  .org-grid { grid-template-columns: 1fr; }
}
</style>
