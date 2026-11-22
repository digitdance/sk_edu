<template>
  <div class="profile-page">
    <div class="p-card p-side">
      <div class="p-avatar">{{ form.name.charAt(0) }}</div>
      <h3>{{ form.name }}</h3>
      <p class="p-title">{{ auth.profile.no }} · {{ auth.profile.className }}</p>
      <el-tag size="small" effect="light" type="primary">在读学生</el-tag>
      <div class="p-meta">
        <div><School :size="14" /> {{ auth.profile.college }}</div>
        <div><Phone :size="14" /> {{ form.phone }}</div>
        <div><Mail :size="14" /> {{ form.email }}</div>
      </div>
      <div class="p-stats">
        <div><b>2</b><span>在学课程</span></div>
        <div><b>91.5</b><span>平均总评</span></div>
        <div><b>12</b><span>笔记</span></div>
      </div>
    </div>

    <div class="p-card p-main">
      <div class="p-head">
        <h3>个人信息</h3>
        <span class="p-hint">学籍信息由教务系统同步，不可修改；联系方式可随时更新</span>
      </div>

      <div class="p-readonly">
        <div class="ro-item"><span>姓名</span><b>{{ auth.profile.name }}</b></div>
        <div class="ro-item"><span>学号</span><b>{{ auth.profile.no }}</b></div>
        <div class="ro-item"><span>班级</span><b>{{ auth.profile.className }}</b></div>
        <div class="ro-item"><span>学院</span><b>{{ auth.profile.college }}</b></div>
      </div>

      <el-form label-position="top" class="p-form">
        <div class="p-grid">
          <el-form-item label="性别">
            <el-radio-group v-model="form.gender">
              <el-radio value="男">男</el-radio>
              <el-radio value="女">女</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="form.phone" maxlength="11" placeholder="用于接收平台通知" />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="form.email" placeholder="用于接收成绩与作业提醒" />
          </el-form-item>
        </div>
        <el-form-item label="个人签名">
          <el-input v-model="form.bio" type="textarea" :rows="3" maxlength="100" show-word-limit placeholder="学习目标、兴趣方向…" />
        </el-form-item>
        <div class="p-actions">
          <el-button @click="reset">恢复默认</el-button>
          <el-button type="primary" :disabled="!dirty" @click="save">保存修改</el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { Mail, Phone, School } from 'lucide-vue-next'
import { auth, saveProfile, type StudentProfile } from '../../stores/auth'

const form = reactive<StudentProfile>({ ...auth.profile })
const dirty = computed(() => JSON.stringify(form) !== JSON.stringify(auth.profile))

function save() {
  if (form.phone && !form.phone.includes('*') && !/^1\d{10}$/.test(form.phone)) {
    ElMessage.warning('手机号格式不正确')
    return
  }
  if (form.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
    ElMessage.warning('邮箱格式不正确')
    return
  }
  saveProfile({ ...form })
  ElMessage.success('个人信息已保存')
}
function reset() {
  Object.assign(form, auth.profile)
}
</script>

<style scoped>
.profile-page { display: grid; grid-template-columns: 320px minmax(0, 1fr); gap: 18px; align-items: start; max-width: 1080px; margin: 0 auto; padding: 22px 24px; }
.p-card { background: #fff; border: 1px solid var(--app-line); border-radius: 14px; padding: 24px; }
.p-side { display: flex; flex-direction: column; align-items: center; text-align: center; gap: 8px; }
.p-avatar {
  width: 84px; height: 84px; border-radius: 50%; display: grid; place-items: center;
  font-size: 32px; font-weight: 700; color: #fff; margin-bottom: 6px;
  background: linear-gradient(135deg, #16a6c9, #2f6bff); box-shadow: 0 10px 24px rgba(22, 166, 201, .35);
}
.p-side h3 { margin: 0; font-size: 18px; }
.p-title { margin: 0 0 4px; font-size: 12.5px; color: var(--app-ink-3); }
.p-meta { margin-top: 16px; width: 100%; display: grid; gap: 10px; text-align: left; }
.p-meta div { display: flex; align-items: center; gap: 8px; font-size: 13px; color: var(--app-ink-2); }
.p-meta svg { color: #16a6c9; flex-shrink: 0; }
.p-stats {
  margin-top: 20px; width: 100%; display: grid; grid-template-columns: repeat(3, 1fr);
  border-top: 1px solid var(--app-line); padding-top: 16px;
}
.p-stats b { display: block; font-size: 17px; }
.p-stats span { font-size: 11.5px; color: var(--app-ink-3); }
.p-head { display: flex; align-items: baseline; gap: 12px; margin-bottom: 18px; flex-wrap: wrap; }
.p-head h3 { margin: 0; font-size: 16px; }
.p-hint { font-size: 12px; color: var(--app-ink-3); }
.p-readonly {
  display: grid; grid-template-columns: repeat(4, 1fr); gap: 12px; margin-bottom: 20px;
}
.ro-item { background: #f7f9fd; border: 1px solid var(--app-line); border-radius: 10px; padding: 10px 14px; }
.ro-item span { display: block; font-size: 11.5px; color: var(--app-ink-3); margin-bottom: 3px; }
.ro-item b { font-size: 13.5px; }
.p-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 0 20px; }
.p-actions { display: flex; justify-content: flex-end; gap: 10px; }
@media (max-width: 900px) {
  .profile-page { grid-template-columns: minmax(0, 1fr); }
  .p-grid { grid-template-columns: 1fr; }
  .p-readonly { grid-template-columns: repeat(2, 1fr); }
}
</style>
