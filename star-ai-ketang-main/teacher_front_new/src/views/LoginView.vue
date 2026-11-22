<template>
  <div class="login-wrap">
    <div class="login-brand">
      <div class="b-inner">
        <div class="b-logo"><GraduationCap :size="30" :stroke-width="2" /></div>
        <h1>教学资源平台</h1>
        <p class="b-sub">教师工作台 · 课程建设 / 作业批阅 / 测验考试 / AI 教辅</p>
        <ul class="b-points">
          <li><Check :size="15" /> 章节任务点与学习进度全程掌控</li>
          <li><Check :size="15" /> 手动 / 随机组卷，客观题自动判分</li>
          <li><Check :size="15" /> AI 智能备课、学情分析与课程问答</li>
        </ul>
        <div class="b-foot">重庆数字职业学院 © 2026</div>
      </div>
    </div>

    <div class="login-side">
      <form class="login-card" @submit.prevent="submit">
        <h2>教师登录</h2>
        <p class="l-sub">请使用教师工号登录工作台</p>

        <label class="l-label">账号</label>
        <div class="l-field" :class="{ bad: !!error && !form.account }">
          <User :size="16" />
          <input v-model="form.account" placeholder="请输入教师工号" autocomplete="username" />
        </div>

        <label class="l-label">密码</label>
        <div class="l-field" :class="{ bad: !!error }">
          <Lock :size="16" />
          <input v-model="form.password" :type="showPwd ? 'text' : 'password'" placeholder="请输入密码" autocomplete="current-password" />
          <button type="button" class="eye" @click="showPwd = !showPwd">
            <Eye v-if="!showPwd" :size="15" /><EyeOff v-else :size="15" />
          </button>
        </div>

        <transition name="shake">
          <div v-if="error" class="l-error"><CircleAlert :size="14" /> {{ error }}</div>
        </transition>

        <div class="l-opts">
          <el-checkbox v-model="remember">7 天内自动登录</el-checkbox>
          <button type="button" class="l-link" @click="forgotVisible = true">忘记密码？</button>
        </div>

        <button class="l-submit" type="submit" :disabled="loading">
          <span v-if="!loading">登 录</span>
          <span v-else class="loading-dot"><i></i><i></i><i></i></span>
        </button>
      </form>
    </div>

    <el-dialog v-model="forgotVisible" title="找回密码" width="420px">
      <p class="fp-text">请联系所在学院教务管理员重置密码，或携带工牌到信息中心前台办理。重置后初始密码将通过短信发送至绑定手机 {{ auth.profile.phone }}。</p>
      <template #footer>
        <el-button type="primary" @click="forgotVisible = false">我知道了</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { Check, CircleAlert, Eye, EyeOff, GraduationCap, Lock, User } from 'lucide-vue-next'
import { auth, login } from '../stores/auth'

const form = reactive({ account: '', password: '' })
const showPwd = ref(false)
const remember = ref(true)
const loading = ref(false)
const error = ref('')
const forgotVisible = ref(false)

async function submit() {
  error.value = ''
  if (!form.account.trim()) {
    error.value = '请输入教师工号'
    return
  }
  if (!form.password) {
    error.value = '请输入密码'
    return
  }
  loading.value = true
  const message = await login(form.account, form.password)
  loading.value = false
  if (message) error.value = message
}
</script>

<style scoped>
.login-wrap { position: fixed; inset: 0; display: flex; background: #f2f5fb; z-index: 100; }
.login-brand {
  flex: 1.15; min-width: 0; display: flex; align-items: center; justify-content: center;
  background: linear-gradient(150deg, #16204a 0%, #22346e 55%, #2f6bff 130%);
  color: #fff; position: relative; overflow: hidden;
}
.login-brand::after {
  content: ''; position: absolute; right: -140px; bottom: -140px; width: 420px; height: 420px;
  border-radius: 50%; background: rgba(255, 255, 255, .06);
}
.login-brand::before {
  content: ''; position: absolute; left: -100px; top: -100px; width: 320px; height: 320px;
  border-radius: 50%; background: rgba(255, 255, 255, .05);
}
.b-inner { position: relative; z-index: 1; max-width: 420px; padding: 40px; }
.b-logo {
  width: 58px; height: 58px; border-radius: 15px; display: grid; place-items: center;
  background: linear-gradient(135deg, #2f6bff, #6b4dff); box-shadow: 0 12px 30px rgba(47, 107, 255, .45);
  margin-bottom: 22px;
}
.b-inner h1 { margin: 0 0 8px; font-size: 30px; letter-spacing: 1px; }
.b-sub { margin: 0 0 30px; font-size: 14px; color: #b9c6ef; }
.b-points { list-style: none; margin: 0 0 40px; padding: 0; display: grid; gap: 14px; }
.b-points li { display: flex; align-items: center; gap: 10px; font-size: 14px; color: #dbe4ff; }
.b-points li svg { color: #57e0a0; flex-shrink: 0; }
.b-foot { font-size: 12px; color: #8fa0d4; }

.login-side { flex: 1; display: flex; align-items: center; justify-content: center; padding: 32px; }
.login-card {
  width: 400px; max-width: 100%; background: #fff; border-radius: 18px; padding: 38px 36px 30px;
  box-shadow: 0 20px 60px rgba(20, 33, 61, .1); border: 1px solid #e8ecf5;
}
.login-card h2 { margin: 0 0 6px; font-size: 22px; }
.l-sub { margin: 0 0 26px; font-size: 13px; color: #7a87a6; }
.l-label { display: block; font-size: 13px; font-weight: 600; color: #3b4763; margin: 16px 0 7px; }
.l-field {
  display: flex; align-items: center; gap: 9px; height: 44px; padding: 0 13px;
  border: 1px solid #dde3f0; border-radius: 11px; color: #8a97b8; transition: all .15s ease;
}
.l-field:focus-within { border-color: #2f6bff; box-shadow: 0 0 0 3px rgba(47, 107, 255, .12); color: #2f6bff; }
.l-field.bad { border-color: #e5564b; }
.l-field input { flex: 1; border: 0; outline: none; font-size: 14px; color: #1f2a44; background: transparent; }
.eye { border: 0; background: none; cursor: pointer; color: #8a97b8; display: grid; place-items: center; padding: 2px; }
.eye:hover { color: #2f6bff; }
.l-error {
  display: flex; align-items: center; gap: 6px; margin-top: 12px; padding: 9px 12px;
  background: #fef0ef; color: #d0453a; border: 1px solid #fad6d2; border-radius: 9px; font-size: 12.5px;
}
.shake-enter-active { animation: shake .35s ease; }
@keyframes shake { 0%, 100% { transform: translateX(0); } 25% { transform: translateX(-5px); } 75% { transform: translateX(5px); } }
.l-opts { display: flex; align-items: center; justify-content: space-between; margin: 16px 0 20px; }
.l-link { border: 0; background: none; color: #2f6bff; font-size: 13px; cursor: pointer; }
.l-link:hover { text-decoration: underline; }
.l-submit {
  width: 100%; height: 46px; border: 0; border-radius: 11px; cursor: pointer; font-size: 15px; font-weight: 600;
  color: #fff; background: linear-gradient(135deg, #2f6bff, #5685ff); letter-spacing: 6px;
  box-shadow: 0 8px 20px rgba(47, 107, 255, .3); transition: all .15s ease;
}
.l-submit:hover:not(:disabled) { transform: translateY(-1px); box-shadow: 0 12px 26px rgba(47, 107, 255, .38); }
.l-submit:disabled { opacity: .75; cursor: default; letter-spacing: 0; }
.loading-dot { display: inline-flex; gap: 6px; }
.loading-dot i { width: 7px; height: 7px; border-radius: 50%; background: #fff; animation: ld .9s infinite; }
.loading-dot i:nth-child(2) { animation-delay: .15s; }
.loading-dot i:nth-child(3) { animation-delay: .3s; }
@keyframes ld { 0%, 60%, 100% { opacity: .4; transform: translateY(0); } 30% { opacity: 1; transform: translateY(-4px); } }
.l-tip { margin-top: 18px; text-align: center; font-size: 12px; color: #9aa6c0; }
.fp-text { margin: 0; font-size: 13px; line-height: 1.8; color: #3b4763; }

@media (max-width: 860px) {
  .login-brand { display: none; }
}
</style>
