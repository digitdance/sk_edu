<template>
  <div class="settings-page">
    <div class="s-card">
      <div class="s-head"><KeyRound :size="17" /><h3>登录密码</h3></div>
      <p class="s-desc">定期更换密码可提升账号安全性，新密码至少 6 位。</p>
      <el-form label-width="90px" class="s-form">
        <el-form-item label="原密码">
          <el-input v-model="pwd.old" type="password" show-password placeholder="请输入原密码" style="max-width:320px" />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="pwd.next" type="password" show-password placeholder="至少 6 位，建议字母 + 数字" style="max-width:320px" />
        </el-form-item>
        <el-form-item label="确认新密码">
          <el-input v-model="pwd.confirm" type="password" show-password placeholder="再次输入新密码" style="max-width:320px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitPwd">更新密码</el-button>
        </el-form-item>
      </el-form>
      <transition name="el-fade-in">
        <div v-if="pwdMsg.text" class="s-msg" :class="pwdMsg.ok ? 'ok' : 'err'">{{ pwdMsg.text }}</div>
      </transition>
    </div>

    <div class="s-card">
      <div class="s-head"><BellRing :size="17" /><h3>消息通知</h3></div>
      <p class="s-desc">选择接收作业提交、考试安排与批阅提醒的渠道。</p>
      <div class="s-row">
        <div><b>站内信</b><span>平台内通知中心推送</span></div>
        <el-switch v-model="auth.notify.site" @change="onSettingsChange" />
      </div>
      <div class="s-row">
        <div><b>邮件</b><span>发送至 {{ auth.profile.email }}</span></div>
        <el-switch v-model="auth.notify.mail" @change="onSettingsChange" />
      </div>
      <div class="s-row">
        <div><b>微信服务号</b><span>绑定后接收截止与成绩提醒</span></div>
        <el-switch v-model="auth.notify.wechat" @change="onSettingsChange" />
      </div>
    </div>

    <div class="s-card">
      <div class="s-head"><ShieldCheck :size="17" /><h3>隐私设置</h3></div>
      <p class="s-desc">控制学生在课程页面中可以看到哪些联系方式。</p>
      <div class="s-row">
        <div><b>对学生展示手机号</b><span>关闭后学生仅可通过站内信联系</span></div>
        <el-switch v-model="auth.privacy.showPhone" @change="onSettingsChange" />
      </div>
      <div class="s-row">
        <div><b>对学生展示邮箱</b><span>展示在课程门户的教师信息区</span></div>
        <el-switch v-model="auth.privacy.showEmail" @change="onSettingsChange" />
      </div>
    </div>

    <div class="s-card danger-zone">
      <div class="s-head"><LogOut :size="17" /><h3>账号操作</h3></div>
      <div class="s-row">
        <div><b>退出当前账号</b><span>退出后需重新输入账号密码登录</span></div>
        <el-button type="danger" plain @click="doLogout">退出登录</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { BellRing, KeyRound, LogOut, ShieldCheck } from 'lucide-vue-next'
import { auth, changePassword, logout, saveSettings } from '../stores/auth'

const pwd = reactive({ old: '', next: '', confirm: '' })
const pwdMsg = reactive({ text: '', ok: false })

async async async function submitPwd() {
  pwdMsg.text = ''
  if (!pwd.old || !pwd.next || !pwd.confirm) {
    pwdMsg.text = '请完整填写原密码与新密码'
    pwdMsg.ok = false
    return
  }
  if (pwd.next !== pwd.confirm) {
    pwdMsg.text = '两次输入的新密码不一致'
    pwdMsg.ok = false
    return
  }
  const err = await changePassword(pwd.old, pwd.next)
  if (err) {
    pwdMsg.text = err
    pwdMsg.ok = false
    return
  }
  pwd.old = pwd.next = pwd.confirm = ''
  pwdMsg.text = '密码已更新，下次登录请使用新密码'
  pwdMsg.ok = true
}
function onSettingsChange() {
  saveSettings()
  ElMessage.success('设置已保存')
}
function doLogout() {
  logout()
  ElMessage.success('已退出登录')
}
</script>

<style scoped>
.settings-page { display: grid; gap: 16px; max-width: 760px; }
.s-card { background: #fff; border: 1px solid var(--app-line); border-radius: 14px; padding: 20px 24px; }
.s-head { display: flex; align-items: center; gap: 8px; margin-bottom: 4px; }
.s-head svg { color: var(--el-color-primary); }
.s-head h3 { margin: 0; font-size: 15px; }
.s-desc { margin: 0 0 16px; font-size: 12.5px; color: var(--app-ink-3); }
.s-form :deep(.el-form-item) { margin-bottom: 14px; }
.s-msg { margin-top: 4px; padding: 9px 12px; border-radius: 9px; font-size: 12.5px; }
.s-msg.ok { background: #eafaf1; color: #1a9e50; border: 1px solid #c4ecd4; }
.s-msg.err { background: #fef0ef; color: #d0453a; border: 1px solid #fad6d2; }
.s-row {
  display: flex; align-items: center; justify-content: space-between; gap: 16px;
  padding: 13px 0; border-bottom: 1px solid #f0f2f7;
}
.s-row:last-child { border-bottom: 0; }
.s-row b { display: block; font-size: 13.5px; font-weight: 600; }
.s-row span { display: block; margin-top: 2px; font-size: 12px; color: var(--app-ink-3); }
.danger-zone .s-head svg { color: #d0453a; }
</style>
