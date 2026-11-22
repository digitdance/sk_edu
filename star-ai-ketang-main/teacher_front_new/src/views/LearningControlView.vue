<template>
  <div>
    <div class="panel">
      <div class="panel-title">
        <h3>班级</h3>
        <span class="extra">学习控制以班级为单位生效</span>
      </div>
      <el-tabs v-model="classTab">
        <el-tab-pane v-for="c in classes" :key="c" :label="c" :name="c" />
      </el-tabs>

      <!-- 章节开放控制 -->
      <div class="panel-title">
        <h3>章节开放控制</h3>
        <el-button size="small" type="primary" @click="saveAccess">保存设置</el-button>
      </div>
      <el-table :data="chapterAccess" border stripe>
        <el-table-column prop="name" label="章节" min-width="220" />
        <el-table-column label="开放模式" width="150">
          <template #default="{ row }">
            <el-select v-model="row.mode" size="small">
              <el-option v-for="m in accessModes" :key="m" :label="m" :value="m" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="开放时间" min-width="200">
          <template #default="{ row }">
            <el-date-picker
              v-if="row.mode === '定时开放'"
              v-model="row.openAt"
              type="datetime"
              size="small"
              placeholder="选择开放时间"
              style="width:100%"
            />
            <span v-else class="muted">{{ row.mode === '闯关模式' ? '完成上一章后自动解锁' : row.mode === '关闭' ? '学生不可见' : '已开放' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="points" label="任务点" width="90" align="center" />
        <el-table-column label="学生完成情况" width="180">
          <template #default="{ row }">
            <el-progress :percentage="row.finish" :stroke-width="8" :color="row.finish >= 80 ? '#2fbf71' : row.finish >= 50 ? '#2f6bff' : '#f56c6c'" />
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="grid-2">
      <!-- 视频播放控制 -->
      <div class="panel">
        <div class="panel-title"><h3>视频播放控制</h3><span class="extra">同步到全部视频任务点</span></div>
        <div class="ctl-list">
          <div v-for="s in videoControls" :key="s.key" class="ctl-row">
            <div>
              <div class="ctl-label">{{ s.label }}</div>
              <div class="muted">{{ s.hint }}</div>
            </div>
            <el-switch v-model="s.on" />
          </div>
        </div>
        <el-form label-position="top" style="margin-top:6px">
          <el-form-item label="允许倍速">
            <el-checkbox-group v-model="speeds">
              <el-checkbox value="1.0">1.0x</el-checkbox>
              <el-checkbox value="1.25">1.25x</el-checkbox>
              <el-checkbox value="1.5">1.5x</el-checkbox>
              <el-checkbox value="2.0">2.0x</el-checkbox>
            </el-checkbox-group>
          </el-form-item>
          <el-form-item label="切屏警告次数上限（超过记为一次违规）">
            <el-input-number v-model="switchLimit" :min="1" :max="10" />
          </el-form-item>
        </el-form>
        <div style="display:flex; justify-content:flex-end">
          <el-button type="primary" @click="saveVideoCtl">保存设置</el-button>
        </div>
      </div>

      <!-- 任务驱动矩阵 -->
      <div class="panel">
        <div class="panel-title">
          <h3>任务驱动式学习</h3>
          <span class="extra">勾选后该内容计入任务点</span>
        </div>
        <table class="matrix">
          <thead>
            <tr>
              <th>章节</th>
              <th v-for="t in taskTypes" :key="t">{{ t }}</th>
              <th>任务点</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in taskMatrix" :key="row.name">
              <td class="m-name">{{ row.name }}</td>
              <td v-for="t in taskTypes" :key="t" class="m-cell">
                <el-checkbox :model-value="row.tasks[t]" @change="v => row.tasks[t] = Boolean(v)" />
              </td>
              <td class="m-num mono">{{ row.points }}</td>
            </tr>
          </tbody>
        </table>
        <div style="display:flex; justify-content:space-between; align-items:center; margin-top:12px">
          <span class="muted">总任务点 {{ totalPoints }} · 结课需完成 ≥ 80%</span>
          <el-button type="primary" @click="saveMatrix">保存矩阵</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { ElMessage } from 'element-plus'

const classes = ['2024级软件1班', '2024级软件2班', '2023级数媒1班']
const classTab = ref(classes[0])
const accessModes = ['开放', '定时开放', '闯关模式', '关闭']
const speeds = ref(['1.0', '1.25', '1.5'])
const switchLimit = ref(3)

const chapterAccess = ref([
  { name: '第 1 章 短视频认知与平台生态', mode: '开放', openAt: null as Date | null, points: 8, finish: 96 },
  { name: '第 2 章 拍摄与剪辑基础', mode: '开放', openAt: null as Date | null, points: 12, finish: 84 },
  { name: '第 3 章 平台推荐机制与账号定位', mode: '定时开放', openAt: new Date('2026-11-24T08:00:00'), points: 10, finish: 12 },
  { name: '第 4 章 内容策划与脚本写作', mode: '闯关模式', openAt: null as Date | null, points: 7, finish: 0 },
  { name: '第 5 章 账号运营与数据分析', mode: '关闭', openAt: null as Date | null, points: 5, finish: 0 }
])

const videoControls = ref([
  { key: 'antiDrag', label: '防拖拽', hint: '未观看部分禁止拖动进度条', on: true },
  { key: 'antiSwitch', label: '防切屏', hint: '切换窗口自动暂停并记录', on: true },
  { key: 'watchAll', label: '看完才计任务点', hint: '完整观看后自动标记完成', on: true },
  { key: 'quizGate', label: '随堂测验必答', hint: '答对后才可继续播放', on: false }
])

const taskTypes = ['视频', '文档', '测验', '作业', '讨论']
const taskMatrix = ref([
  { name: '第 1 章', points: 8, tasks: { 视频: true, 文档: true, 测验: true, 作业: false, 讨论: true } as Record<string, boolean> },
  { name: '第 2 章', points: 12, tasks: { 视频: true, 文档: true, 测验: true, 作业: true, 讨论: false } as Record<string, boolean> },
  { name: '第 3 章', points: 10, tasks: { 视频: true, 文档: false, 测验: true, 作业: true, 讨论: true } as Record<string, boolean> },
  { name: '第 4 章', points: 7, tasks: { 视频: true, 文档: true, 测验: false, 作业: true, 讨论: false } as Record<string, boolean> },
  { name: '第 5 章', points: 5, tasks: { 视频: true, 文档: false, 测验: true, 作业: false, 讨论: false } as Record<string, boolean> }
])

const totalPoints = computed(() => taskMatrix.value.reduce((s, r) => s + r.points, 0))

function saveAccess() { ElMessage.success(`「${classTab.value}」章节开放设置已保存`) }
function saveVideoCtl() { ElMessage.success('视频播放控制已同步到全部任务点') }
function saveMatrix() { ElMessage.success('任务点矩阵已保存，学生端进度将重新计算') }
</script>

<style scoped>
.ctl-list { display: flex; flex-direction: column; gap: 4px; }
.ctl-row { display: flex; align-items: center; justify-content: space-between; gap: 16px; padding: 10px 0; border-bottom: 1px solid #f2f4f9; }
.ctl-row:last-child { border-bottom: none; }
.ctl-label { font-size: 14px; font-weight: 600; }
.matrix { width: 100%; border-collapse: collapse; font-size: 13px; }
.matrix th, .matrix td { padding: 9px 8px; border-bottom: 1px solid #f2f4f9; text-align: center; }
.matrix th { color: var(--app-ink-3); font-weight: 600; font-size: 12px; }
.matrix .m-name { text-align: left; font-weight: 600; }
.matrix .m-num { font-weight: 700; }
</style>
