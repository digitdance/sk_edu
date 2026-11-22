<template>
  <header class="topbar">
    <div class="left">
      <button class="icon-btn burger" aria-label="菜单" @click="$emit('toggleSidebar')">
        <Menu :size="18" />
      </button>
      <div class="title-wrap">
        <h1>{{ title }}</h1>
        <p>{{ subtitle }}</p>
      </div>
    </div>

    <div class="right">
      <el-input
        v-model="keyword"
        class="search"
        placeholder="搜索课程、资源、学生…"
        :prefix-icon="SearchIcon"
        clearable
        @keyup.enter="doSearch"
      />
      <el-dropdown trigger="click" @command="onQuickCreate">
        <button class="icon-btn" aria-label="快捷新建">
          <Plus :size="18" />
        </button>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="course">新建课程</el-dropdown-item>
            <el-dropdown-item command="resources">上传资源</el-dropdown-item>
            <el-dropdown-item command="homework">创建作业</el-dropdown-item>
            <el-dropdown-item command="exam">新建考试</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
      <el-badge :value="3" :max="99" class="bell-badge">
        <button class="icon-btn" aria-label="通知" @click="noticeVisible = true">
          <Bell :size="18" />
        </button>
      </el-badge>

      <el-dropdown trigger="click" @command="onUserMenu">
        <div class="user-entry">
          <div class="t-avatar">{{ auth.profile.name.charAt(0) }}</div>
          <span class="t-name">{{ auth.profile.name }}</span>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">个人中心</el-dropdown-item>
            <el-dropdown-item command="settings">账号设置</el-dropdown-item>
            <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>

    <el-drawer v-model="noticeVisible" title="通知中心" size="360px">
      <div class="notice-list">
        <div v-for="n in notices" :key="n.id" class="notice-item">
          <div class="n-icon" :style="{ background: n.bg }">
            <component :is="n.icon" :size="15" />
          </div>
          <div class="n-body">
            <strong>{{ n.title }}</strong>
            <p>{{ n.desc }}</p>
            <span class="time">{{ n.time }}</span>
          </div>
        </div>
      </div>
    </el-drawer>
  </header>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Menu, Plus, Bell, Search as SearchIcon, ClipboardList, FileCheck2, Users } from 'lucide-vue-next'
import { auth, logout } from '../stores/auth'
import { globalSearch } from '../stores/search'

defineProps<{ title: string; subtitle: string }>()
const emit = defineEmits<{ toggleSidebar: []; navigate: [id: string] }>()

const keyword = ref('')
const noticeVisible = ref(false)

const notices = [
  { id: 1, title: '作业提交提醒', desc: '「第2章 · 视频剪辑基础」已有 36 人提交，截止今晚 22:00。', time: '10 分钟前', icon: ClipboardList, bg: '#eaf0ff' },
  { id: 2, title: '考试安排已发布', desc: '期末线上考试 6 月 20 日 14:00 开始，时长 120 分钟。', time: '2 小时前', icon: FileCheck2, bg: '#e7f6ec' },
  { id: 3, title: '3 名学生申请加入课程', desc: '请在「用户与权限」中审核转班申请。', time: '昨天 18:24', icon: Users, bg: '#fff3e2' }
]

function doSearch() {
  const kw = keyword.value.trim()
  if (!kw) return
  globalSearch.keyword = kw
  keyword.value = ''
  emit('navigate', 'resources')
}
function onQuickCreate(cmd: string) { emit('navigate', cmd) }
function onUserMenu(cmd: string) {
  if (cmd === 'logout') {
    logout()
    ElMessage.success('已退出登录')
    return
  }
  emit('navigate', cmd)
}
</script>

<style scoped>
.topbar {
  height: 58px; flex-shrink: 0; display: flex; align-items: center; justify-content: space-between;
  gap: 12px; padding: 0 20px; background: #fff; border-bottom: 1px solid var(--app-line); z-index: 30;
}
.left { display: flex; align-items: center; gap: 12px; min-width: 0; }
.title-wrap h1 { margin: 0; font-size: 16px; font-weight: 700; }
.title-wrap p { margin: 1px 0 0; font-size: 12px; color: var(--app-ink-3); white-space: nowrap; }
.icon-btn {
  width: 34px; height: 34px; border-radius: 8px; border: 1px solid var(--app-line); background: #fff;
  color: var(--app-ink-2); display: inline-flex; align-items: center; justify-content: center; cursor: pointer;
  transition: border-color .15s ease, color .15s ease;
}
.icon-btn:hover { border-color: var(--el-color-primary); color: var(--el-color-primary); }
.right { display: flex; align-items: center; gap: 10px; }
.search { width: 240px; }
.burger { display: none; }
.user-entry { display: flex; align-items: center; gap: 8px; cursor: pointer; padding: 4px 6px; border-radius: 8px; }
.user-entry:hover { background: #f5f7fc; }
.t-avatar {
  width: 30px; height: 30px; border-radius: 50%; background: linear-gradient(135deg, #2fbf71, #1a9e50);
  color: #fff; display: flex; align-items: center; justify-content: center; font-size: 12.5px; font-weight: 700;
}
.t-name { font-size: 13px; color: var(--app-ink); }
.notice-list { display: flex; flex-direction: column; gap: 4px; }
.notice-item { display: flex; gap: 12px; padding: 12px 6px; border-bottom: 1px solid #f0f2f7; }
.n-icon { width: 32px; height: 32px; border-radius: 8px; display: flex; align-items: center; justify-content: center; color: var(--el-color-primary); flex-shrink: 0; }
.n-body strong { font-size: 13px; }
.n-body p { margin: 3px 0 4px; font-size: 12px; color: var(--app-ink-2); line-height: 1.55; }
.n-body .time { font-size: 11px; color: var(--app-ink-3); }
@media (max-width: 960px) {
  .burger { display: inline-flex; }
  .search, .t-name { display: none; }
}
</style>
