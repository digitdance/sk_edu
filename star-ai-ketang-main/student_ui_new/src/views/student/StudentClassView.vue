<template>
  <div>
    <!-- 班级信息卡 -->
    <div class="panel class-hero">
      <div class="ch-left">
        <div class="ch-badge">短</div>
        <div class="ch-info">
          <h2>
            短视频运营 2401 班
            <el-tag size="small" effect="light" type="primary">2024 级</el-tag>
            <el-tag size="small" effect="plain" type="success">在籍</el-tag>
          </h2>
          <p class="muted">信息工程学院 · 数字媒体系 · 班主任：王琳</p>
          <p class="muted">班号 ZS2401 · 2024-09-01 加入 · 成员 40 人</p>
        </div>
      </div>
      <div class="ch-stats">
        <div class="ch-stat"><strong class="mono">68%</strong><span>班级平均进度</span></div>
        <el-divider direction="vertical" />
        <div class="ch-stat"><strong class="mono">4 门</strong><span>在学课程</span></div>
        <el-divider direction="vertical" />
        <div class="ch-stat"><strong class="mono" style="color:#e6a23c">第 5 名</strong><span>我的班级排名</span></div>
      </div>
    </div>

    <div class="class-layout">
      <!-- 左：班级成员 -->
      <div class="panel members-col">
        <div class="panel-title">
          <h3>班级成员（{{ filteredMembers.length }} / {{ members.length }}）</h3>
          <el-input v-model="keyword" placeholder="搜索姓名 / 学号" :prefix-icon="SearchIcon" clearable style="width:220px" />
        </div>
        <el-table :data="filteredMembers" stripe>
          <el-table-column label="姓名" min-width="140">
            <template #default="{ row }">
              <div class="m-name">
                <span class="m-avatar" :style="{ background: row.self ? 'linear-gradient(135deg,#2fbf71,#1a9e50)' : avatarBg(row.no) }">{{ row.name[0] }}</span>
                <strong>{{ row.name }}</strong>
                <el-tag v-if="row.self" size="small" type="success" effect="plain">我</el-tag>
                <el-tag v-if="row.role" size="small" type="warning" effect="plain">{{ row.role }}</el-tag>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="no" label="学号" width="120">
            <template #default="{ row }"><span class="mono">{{ row.no }}</span></template>
          </el-table-column>
          <el-table-column label="学习进度" min-width="170">
            <template #default="{ row }">
              <el-progress :percentage="row.progress" :stroke-width="7"
                :color="row.progress >= 80 ? '#2fbf71' : row.progress >= 60 ? '#2f6bff' : '#e6a23c'" />
            </template>
          </el-table-column>
          <el-table-column prop="active" label="最近活跃" width="110">
            <template #default="{ row }"><span class="muted">{{ row.active }}</span></template>
          </el-table-column>
        </el-table>
        <el-empty v-if="!filteredMembers.length" description="无匹配成员" :image-size="60" />
      </div>

      <!-- 右：任课教师 + 学习之星 -->
      <div class="side-col">
        <div class="panel">
          <div class="panel-title"><h3>任课教师</h3><span class="extra">4 位</span></div>
          <div v-for="t in teachers" :key="t.name" class="t-item">
            <span class="t-avatar" :style="{ background: t.bg }">{{ t.name[0] }}</span>
            <div class="grow">
              <strong>{{ t.name }}</strong>
              <p class="muted">{{ t.course }}{{ t.head ? ' · 班主任' : '' }}</p>
            </div>
            <el-button link type="primary" size="small" @click="contactTeacher(t.name)">私信</el-button>
          </div>
        </div>

        <div class="panel">
          <div class="panel-title"><h3>学习之星</h3><span class="extra">按本周学习时长</span></div>
          <div v-for="(s, i) in stars" :key="s.name" class="star-item">
            <span class="rank" :class="`r${i + 1}`">{{ i + 1 }}</span>
            <span class="grow">{{ s.name }}</span>
            <span class="muted mono">{{ s.hours }} 小时</span>
          </div>
        </div>

        <div class="panel notice-mini">
          <div class="panel-title"><h3>班级公告</h3></div>
          <div v-for="n in classNotices" :key="n" class="cn-item">
            <Megaphone :size="13" style="color:var(--el-color-primary); flex-shrink:0" />
            <span>{{ n }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Search as SearchIcon, Megaphone } from 'lucide-vue-next'

interface Member { name: string; no: string; progress: number; active: string; self?: boolean; role?: string }

const keyword = ref('')

const members: Member[] = [
  { name: '陈思雨', no: '20240116', progress: 96, active: '10 分钟前', role: '学习委员' },
  { name: '李梦琪', no: '20240118', progress: 92, active: '1 小时前' },
  { name: '王子涵', no: '20240117', progress: 84, active: '刚刚', role: '班长' },
  { name: '刘洋', no: '20240115', progress: 78, active: '在线', self: true },
  { name: '赵天宇', no: '20240121', progress: 74, active: '3 小时前' },
  { name: '孙佳怡', no: '20240123', progress: 69, active: '今天 08:40' },
  { name: '周子墨', no: '20240125', progress: 63, active: '昨天 21:12' },
  { name: '吴欣然', no: '20240126', progress: 57, active: '昨天 18:05' },
  { name: '郑浩然', no: '20240129', progress: 45, active: '2 天前' },
  { name: '林小雨', no: '20240132', progress: 38, active: '3 天前' }
]

const filteredMembers = computed(() =>
  members.filter(m => m.name.includes(keyword.value) || m.no.includes(keyword.value))
)

const palette = ['#2f6bff', '#6b4dff', '#e6a23c', '#f56c6c', '#13c2c2', '#2fbf71']
function avatarBg(no: string) { return palette[Number(no.slice(-2)) % palette.length] }

const teachers = [
  { name: '王琳', course: '短视频制作与运营', head: true, bg: 'linear-gradient(135deg,#2f6bff,#6b4dff)' },
  { name: '李思颖', course: '新媒体文案写作', head: false, bg: 'linear-gradient(135deg,#f56c6c,#ff9f43)' },
  { name: '周航', course: '摄影摄像基础', head: false, bg: 'linear-gradient(135deg,#13c2c2,#2fbf71)' },
  { name: '赵敏', course: '直播电商实务', head: false, bg: 'linear-gradient(135deg,#e6a23c,#d97b00)' }
]

const stars = [
  { name: '陈思雨', hours: 14.5 },
  { name: '李梦琪', hours: 12.8 },
  { name: '王子涵', hours: 11.2 },
  { name: '赵天宇', hours: 9.6 },
  { name: '刘洋', hours: 8.9 }
]

const classNotices = [
  '本周五 19:00 班级作品互评会，请提前上传混剪作品',
  '第 3 章章节测验 11-24 10:00 开放，请注意考试须知'
]

function contactTeacher(name: string) { ElMessage.success(`已打开与「${name}」老师的私信会话`) }
</script>

<style scoped>
.class-hero { display: flex; align-items: center; justify-content: space-between; gap: 24px; flex-wrap: wrap; }
.ch-left { display: flex; align-items: center; gap: 16px; }
.ch-badge {
  width: 58px; height: 58px; border-radius: 14px; flex-shrink: 0;
  background: linear-gradient(135deg, #2f6bff, #6b4dff); color: #fff;
  display: flex; align-items: center; justify-content: center; font-size: 26px; font-weight: 700;
}
.ch-info h2 { margin: 0 0 6px; font-size: 19px; display: flex; align-items: center; gap: 8px; }
.ch-info p { margin: 2px 0; font-size: 12.5px; }
.ch-stats { display: flex; align-items: center; gap: 22px; }
.ch-stat { display: flex; flex-direction: column; align-items: center; gap: 3px; }
.ch-stat strong { font-size: 20px; }
.ch-stat span { font-size: 12px; color: var(--app-ink-3); }

.class-layout { display: grid; grid-template-columns: 1fr 330px; gap: 16px; align-items: start; }
.side-col { display: flex; flex-direction: column; gap: 16px; }

.m-name { display: flex; align-items: center; gap: 9px; }
.m-avatar {
  width: 28px; height: 28px; border-radius: 50%; color: #fff; font-size: 12px; font-weight: 700;
  display: inline-flex; align-items: center; justify-content: center; flex-shrink: 0;
}

.t-item { display: flex; align-items: center; gap: 11px; padding: 9px 0; border-bottom: 1px solid #f2f4f9; }
.t-item:last-child { border-bottom: none; }
.t-avatar {
  width: 34px; height: 34px; border-radius: 10px; color: #fff; font-size: 13px; font-weight: 700;
  display: inline-flex; align-items: center; justify-content: center; flex-shrink: 0;
}
.t-item .grow { flex: 1; min-width: 0; }
.t-item strong { font-size: 13.5px; }
.t-item p { margin: 1px 0 0; font-size: 12px; }

.star-item { display: flex; align-items: center; gap: 10px; padding: 9px 0; border-bottom: 1px solid #f2f4f9; font-size: 13.5px; }
.star-item:last-child { border-bottom: none; }
.star-item .grow { flex: 1; }
.rank {
  width: 22px; height: 22px; border-radius: 6px; background: #f2f4f9; color: var(--app-ink-2);
  display: inline-flex; align-items: center; justify-content: center; font-size: 12px; font-weight: 700;
}
.rank.r1 { background: #ffe9c7; color: #b45309; }
.rank.r2 { background: #eef1f6; color: #5b6b85; }
.rank.r3 { background: #ffe3d3; color: #c2410c; }

.cn-item { display: flex; align-items: flex-start; gap: 8px; font-size: 12.5px; line-height: 1.6; padding: 7px 0; border-bottom: 1px dashed #f0f2f7; }
.cn-item:last-child { border-bottom: none; }

@media (max-width: 960px) {
  .class-layout { grid-template-columns: 1fr; }
  .ch-stats { width: 100%; justify-content: space-around; }
}
</style>
