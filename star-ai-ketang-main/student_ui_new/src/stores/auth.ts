import { reactive } from 'vue'
import { apiRequest, setAccessToken } from '../api/client'

export interface StudentProfile {
  name: string
  account: string
  no: string
  className: string
  college: string
  gender: string
  phone: string
  email: string
  bio: string
}

interface AuthUser {
  id: number
  userAccount: string
  userName: string
  userProfile?: string
  userRole: string
  userGender?: number
  userPhone?: string
  userEmail?: string
  token?: string
  refreshToken?: string
}

interface AuthState {
  loggedIn: boolean
  sessionReady: boolean
  profile: StudentProfile
  notify: { site: boolean; mail: boolean; wechat: boolean }
  privacy: { showPhone: boolean; showEmail: boolean }
}

const KEY = 'student-auth-v1'
const defaults: AuthState = {
  loggedIn: false,
  sessionReady: false,
  profile: { name: '', account: '', no: '', className: '', college: '', gender: '', phone: '', email: '', bio: '' },
  notify: { site: true, mail: false, wechat: true },
  privacy: { showPhone: false, showEmail: false },
}

function load(): AuthState {
  try {
    const raw = localStorage.getItem(KEY)
    const saved = raw ? JSON.parse(raw) as Partial<AuthState> : {}
    return {
      ...defaults,
      ...saved,
      loggedIn: Boolean(localStorage.getItem('student-access-token')),
      sessionReady: false,
      profile: { ...defaults.profile, ...(saved.profile ?? {}) },
      notify: { ...defaults.notify, ...(saved.notify ?? {}) },
      privacy: { ...defaults.privacy, ...(saved.privacy ?? {}) },
    }
  } catch {
    return { ...defaults, profile: { ...defaults.profile }, notify: { ...defaults.notify }, privacy: { ...defaults.privacy } }
  }
}

export const auth = reactive<AuthState>(load())

function persist() {
  localStorage.setItem(KEY, JSON.stringify({
    loggedIn: auth.loggedIn,
    profile: auth.profile,
    notify: auth.notify,
    privacy: auth.privacy,
  }))
}

function applyUser(user: AuthUser) {
  auth.profile = {
    ...auth.profile,
    name: user.userName || user.userAccount,
    account: user.userAccount,
    gender: user.userGender === 1 ? '男' : user.userGender === 2 ? '女' : '',
    phone: user.userPhone || '',
    email: user.userEmail || '',
    bio: user.userProfile || '',
  }
  auth.loggedIn = true
  auth.sessionReady = true
  persist()
}

export async function login(account: string, password: string): Promise<string | null> {
  try {
    const user = await apiRequest<AuthUser>('/api/auth/login', {
      method: 'POST',
      body: { userAccount: account.trim(), userPassword: password },
    }, true)
    if (user.userRole !== 'student') return '该账号没有学生端访问权限'
    if (!user.token) return '登录响应缺少访问令牌，请联系管理员'
    setAccessToken(user.token)
    if (user.refreshToken) localStorage.setItem('student-refresh-token', user.refreshToken)
    applyUser(user)
    return null
  } catch (error) {
    auth.sessionReady = true
    return error instanceof Error ? error.message : '登录失败，请稍后重试'
  }
}

export async function restoreSession() {
  const token = localStorage.getItem('student-access-token')
  if (!token) {
    auth.loggedIn = false
    auth.sessionReady = true
    return
  }
  try {
    const user = await apiRequest<AuthUser>('/api/auth/current')
    if (user.userRole !== 'student') throw new Error('账号没有学生端访问权限')
    applyUser(user)
  } catch {
    setAccessToken(null)
    localStorage.removeItem('student-refresh-token')
    auth.loggedIn = false
    persist()
  } finally {
    auth.sessionReady = true
  }
}

export function logout() {
  setAccessToken(null)
  localStorage.removeItem('student-refresh-token')
  auth.loggedIn = false
  auth.sessionReady = true
  persist()
}

export function saveProfile(profile: StudentProfile) {
  auth.profile = { ...profile }
  persist()
}

export async function changePassword(oldPassword: string, newPassword: string): Promise<string | null> {
  try {
    await apiRequest('/api/user/password', {
      method: 'POST',
      body: { oldPassword, newPassword, confirmPassword: newPassword },
    })
    return null
  } catch (error) {
    return error instanceof Error ? error.message : '密码修改失败，请重试'
  }
}

export function saveSettings() {
  persist()
}

if (typeof window !== 'undefined') {
  window.addEventListener('student-session-expired', () => {
    auth.loggedIn = false
    auth.sessionReady = true
    persist()
  })
}
