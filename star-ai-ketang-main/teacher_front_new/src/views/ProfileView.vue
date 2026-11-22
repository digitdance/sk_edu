<template>
  <div class="profile-page">
    <div class="p-card p-side">
      <div class="p-avatar">{{ form.name.charAt(0) }}</div>
      <h3>{{ form.name }}</h3>
      <p class="p-title">{{ form.title }} · {{ auth.profile.account }}</p>
      <el-tag size="small" effect="light" type="success">在职教师</el-tag>
      <div class="p-meta">
        <div><Building2 :size="14" /> {{ form.college }}</div>
        <div><Phone :size="14" /> {{ form.phone }}</div>
        <div><Mail :size="14" /> {{ form.email }}</div>
      </div>
      <div class="p-stats">
        <div><b>2</b><span>在授课程</span></div>
        <div><b>78</b><span>学生</span></div>
        <div><b>6 年</b><span>教龄</span></div>
      </div>
    </div>

    <div class="p-card p-main">
      <div class="p-head">
        <h3>个人信息</h3>
        <span class="p-hint">保存后即时生效，并同步到平台各端展示</span>
      </div>
      <el-form label-position="top" class="p-form">
        <div class="p-grid">
          <el-form-item label="姓名">
            <el-input v-model="form.name" maxlength="10" placeholder="请输入姓名" />
          </el-form-item>
          <el-form-item label="性别">
            <el-radio-group v-model="form.gender">
              <el-radio value="女">女</el-radio>
              <el-radio value="男">男</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="职称">
            <el-select v-model="form.title">
              <el-option v-for="t in titles" :key="t" :label="t" :value="t" />
            </el-select>
          </el-form-item>
          <el-form-item label="所属教研室">
            <el-select v-model="form.college">
              <el-option v-for="c in colleges" :key="c" :label="c" :value="c" />
            </el-select>
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="form.phone" maxlength="11" placeholder="用于接收平台通知" />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="form.email" placeholder="用于接收成绩与批阅提醒" />
          </el-form-item>
        </div>
        <el-form-item label="个人简介">
          <el-input v-model="form.bio" type="textarea" :rows="4" maxlength="200" show-word-limit placeholder="主讲课程、研究方向…" />
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
import { Building2, Mail, Phone } from 'lucide-vue-next'
import { auth, saveProfile, type TeacherProfile } from '../stores/auth'

const titles = ['助教', '讲师', '副教授', '教授']
const colleges = [
  '信息工程学院 · 数字媒体教研室',
  '信息工程学院 · 软件技术教研室',
  '传媒与设计学院 · 新媒体教研室',
  '通识教育中心',
]

const form = reactive<TeacherProfile>({ ...auth.profile })
const dirty = computed(() => JSON.stringify(form) !== JSON.stringify(auth.profile))

function save() {
  if (!form.name.trim()) {
    ElMessage.warning('姓名不能为空')
    return
  }
  if (form.phone && !/^1\d{10}$/.test(form.phone.replace(/\*/g, '0')) && !form.phone.includes('*')) {
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
.profile-page { display: grid; grid-template-columns: 320px minmax(0, 1fr); gap: 18px; align-items: start; max-width: 1080px; }
.p-card { background: #fff; border: 1px solid var(--app-line); border-radius: 14px; padding: 24px; }
.p-side { display: flex; flex-direction: column; align-items: center; text-align: center; gap: 8px; }
.p-avatar {
  width: 84px; height: 84px; border-radius: 50%; display: grid; place-items: center;
  font-size: 32px; font-weight: 700; color: #fff; margin-bottom: 6px;
  background: linear-gradient(135deg, #2fbf71, #1a9e50); box-shadow: 0 10px 24px rgba(47, 191, 113, .35);
}
.p-side h3 { margin: 0; font-size: 18px; }
.p-title { margin: 0 0 4px; font-size: 12.5px; color: var(--app-ink-3); }
.p-meta { margin-top: 16px; width: 100%; display: grid; gap: 10px; text-align: left; }
.p-meta div { display: flex; align-items: center; gap: 8px; font-size: 13px; color: var(--app-ink-2); }
.p-meta svg { color: var(--el-color-primary); flex-shrink: 0; }
.p-stats {
  margin-top: 20px; width: 100%; display: grid; grid-template-columns: repeat(3, 1fr);
  border-top: 1px solid var(--app-line); padding-top: 16px;
}
.p-stats b { display: block; font-size: 17px; }
.p-stats span { font-size: 11.5px; color: var(--app-ink-3); }
.p-head { display: flex; align-items: baseline; gap: 12px; margin-bottom: 18px; }
.p-head h3 { margin: 0; font-size: 16px; }
.p-hint { font-size: 12px; color: var(--app-ink-3); }
.p-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 0 20px; }
.p-actions { display: flex; justify-content: flex-end; gap: 10px; }
@media (max-width: 900px) {
  .profile-page { grid-template-columns: minmax(0, 1fr); }
  .p-grid { grid-template-columns: 1fr; }
}
</style>
