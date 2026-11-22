<template>
  <header class="topnav">
    <div class="nav-inner">
      <div class="brand" @click="$emit('navigate', 's-home')">
        <div class="logo">
          <GraduationCap :size="19" :stroke-width="2.2" />
        </div>
        <div class="brand-text">
          <strong>教学资源平台</strong>
          <span class="s-tag">学生端</span>
        </div>
      </div>

      <nav class="h-menu">
        <button
          v-for="item in menuItems"
          :key="item.id"
          class="m-item"
          :class="{ active: item.id === active || (item.id === 's-courses' && active === 's-learn') }"
          @click="go(item.id)"
        >
          {{ item.label }}
        </button>
      </nav>

      <div class="right">
        <el-input
          v-model="keyword"
          class="search"
          placeholder="搜索课程、资料…"
          :prefix-icon="SearchIcon"
          clearable
          @keyup.enter="doSearch"
        />
        <el-badge :value="2" :max="99">
          <button class="icon-btn" aria-label="通知" @click="noticeVisible = true">
            <Bell :size="17" />
          </button>
        </el-badge>
        <el-dropdown trigger="click" @command="onUserMenu">
          <div class="user-entry">
            <div class="s-avatar">{{ auth.profile.name.charAt(0) }}</div>
            <div class="u-meta">
              <strong>{{ auth.profile.name }}</strong>
              <span>{{ auth.profile.className }}</span>
            </div>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="s-profile">个人中心</el-dropdown-item>
              <el-dropdown-item command="s-settings">账号设置</el-dropdown-item>
              <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>

        <button class="icon-btn burger" aria-label="菜单" @click="mobileOpen = !mobileOpen">
          <Menu :size="18" />
        </button>
      </div>
    </div>

    <!-- 移动端菜单 -->
    <transition name="el-zoom-in-top">
      <div v-if="mobileOpen" class="mobile-menu">
        <button
          v-for="item in menuItems"
          :key="item.id"
          class="mm-item"
          :class="{ active: item.id === active }"
          @click="go(item.id)"
        >
          <component :is="item.icon" :size="15" />
          <span>{{ item.label }}</span>
        </button>
      </div>
    </transition>

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
import { auth, logout } from '../stores/auth'
import { globalSearch } from '../stores/search'
import {
  GraduationCap, Bell, Menu, Search as SearchIcon,
  Home, LayoutGrid, ClipboardList, FileCheck2, BarChart3,
  MessageSquareMore, NotebookPen, AlarmClock, Megaphone, Users
} from 'lucide-vue-next'
import type { ViewId } from '../App.vue'

interface MenuItem { id: ViewId; label: string; icon: unknown }

defineProps<{ active: ViewId }>()
const emit = defineEmits<{ navigate: [id: string] }>()

const keyword = ref('')
const noticeVisible = ref(false)
const mobileOpen = ref(false)

const menuItems: MenuItem[] = [
  { id: 's-home', label: '首页', icon: Home },
  { id: 's-courses', label: '我的课程', icon: LayoutGrid },
  { id: 's-class', label: '我的班级', icon: Users },
  { id: 's-homework', label: '作业', icon: ClipboardList },
  { id: 's-exams', label: '测验考试', icon: FileCheck2 },
  { id: 's-grades', label: '成绩', icon: BarChart3 },
  { id: 's-discuss', label: '讨论', icon: MessageSquareMore },
  { id: 's-notes', label: '笔记', icon: NotebookPen }
]

const notices = [
  { id: 1, title: '作业截止提醒', desc: '「第 2 章作业 · 视频剪辑基础」将于 11-21 22:00 截止，请尽快提交。', time: '30 分钟前', icon: AlarmClock, bg: '#fff3e2' },
  { id: 2, title: '测验开放预告', desc: '「第 3 章章节测验」将于 11 月 24 日 10:00 开放。', time: '2 小时前', icon: FileCheck2, bg: '#eaf0ff' },
  { id: 3, title: '课程公告', desc: '期末考核改为线上考试，请提前测试设备与网络。', time: '昨天 16:00', icon: Megaphone, bg: '#e7f6ec' }
]

function go(id: string) {
  mobileOpen.value = false
  emit('navigate', id)
}
function doSearch() {
  const kw = keyword.value.trim()
  if (!kw) return
  globalSearch.keyword = kw
  keyword.value = ''
  emit('navigate', 's-courses')
}
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
.topnav {
  position: fixed; top: 0; left: 0; right: 0; z-index: 50;
  background: #fff; border-bottom: 1px solid var(--app-line);
}
.nav-inner {
  max-width: 1360px; margin: 0 auto; height: 58px; padding: 0 24px;
  display: flex; align-items: center; gap: 28px;
}
.brand { display: flex; align-items: center; gap: 10px; cursor: pointer; flex-shrink: 0; }
.logo {
  width: 32px; height: 32px; border-radius: 9px;
  background: linear-gradient(135deg, #2f6bff, #6b4dff);
  display: flex; align-items: center; justify-content: center; color: #fff;
}
.brand-text { display: flex; align-items: center; gap: 7px; }
.brand-text strong { font-size: 15px; }
.s-tag {
  font-size: 10.5px; color: var(--el-color-primary); background: var(--el-color-primary-light-9);
  padding: 2px 7px; border-radius: 99px; font-weight: 600;
}

.h-menu { display: flex; align-items: stretch; gap: 4px; height: 100%; flex: 1; }
.m-item {
  position: relative; border: none; background: transparent; cursor: pointer;
  padding: 0 14px; font-size: 14px; color: var(--app-ink-2); height: 58px;
  transition: color .15s ease;
}
.m-item:hover { color: var(--el-color-primary); }
.m-item.active { color: var(--el-color-primary); font-weight: 600; }
.m-item.active::after {
  content: ''; position: absolute; left: 12px; right: 12px; bottom: 0;
  height: 3px; border-radius: 3px 3px 0 0; background: var(--el-color-primary);
}

.right { display: flex; align-items: center; gap: 10px; flex-shrink: 0; }
.search { width: 200px; }
.icon-btn {
  width: 34px; height: 34px; border-radius: 8px; border: 1px solid var(--app-line); background: #fff;
  color: var(--app-ink-2); display: inline-flex; align-items: center; justify-content: center; cursor: pointer;
  transition: border-color .15s ease, color .15s ease;
}
.icon-btn:hover { border-color: var(--el-color-primary); color: var(--el-color-primary); }
.user-entry { display: flex; align-items: center; gap: 9px; cursor: pointer; padding: 4px 6px; border-radius: 8px; }
.user-entry:hover { background: #f5f7fc; }
.s-avatar {
  width: 32px; height: 32px; border-radius: 50%; flex-shrink: 0;
  background: linear-gradient(135deg, #2fbf71, #1a9e50); color: #fff;
  display: flex; align-items: center; justify-content: center; font-size: 13px; font-weight: 700;
}
.u-meta { display: flex; flex-direction: column; line-height: 1.3; }
.u-meta strong { font-size: 13px; }
.u-meta span { font-size: 11px; color: var(--app-ink-3); }
.burger { display: none; }

.mobile-menu {
  display: none; flex-direction: column; padding: 8px 16px 12px;
  border-top: 1px solid var(--app-line); background: #fff;
}
.mm-item {
  display: flex; align-items: center; gap: 10px; border: none; background: transparent;
  padding: 11px 12px; font-size: 14px; color: var(--app-ink-2); cursor: pointer;
  border-radius: 8px; text-align: left;
}
.mm-item.active { background: var(--el-color-primary-light-9); color: var(--el-color-primary); font-weight: 600; }

.notice-list { display: flex; flex-direction: column; gap: 4px; }
.notice-item { display: flex; gap: 12px; padding: 12px 6px; border-bottom: 1px solid #f0f2f7; }
.n-icon { width: 32px; height: 32px; border-radius: 8px; display: flex; align-items: center; justify-content: center; color: var(--el-color-primary); flex-shrink: 0; }
.n-body strong { font-size: 13px; }
.n-body p { margin: 3px 0 4px; font-size: 12px; color: var(--app-ink-2); line-height: 1.55; }
.n-body .time { font-size: 11px; color: var(--app-ink-3); }

@media (max-width: 1060px) {
  .search { display: none; }
}
@media (max-width: 900px) {
  .nav-inner { gap: 14px; padding: 0 16px; }
  .h-menu { display: none; }
  .burger { display: inline-flex; }
  .mobile-menu { display: flex; }
  .u-meta { display: none; }
}
</style>
