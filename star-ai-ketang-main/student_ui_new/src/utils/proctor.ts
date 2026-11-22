import { ref } from 'vue'
import { ElMessageBox } from 'element-plus'

/**
 * 考试防作弊监考：全屏锁定 + 切屏 / 失焦 / 最小化 / 退出全屏检测
 * 浏览器不允许网页真正阻止切屏或最小化，因此采用检测 + 警告 + 强制交卷方案。
 */
export const maxViolations = 3
export const violationCount = ref(0)
export const proctoring = ref(false)

let lastViolationAt = 0
let forceSubmitHandler: (() => void) | null = null

/** 全屏是防切屏的基础：需在用户手势中调用 */
function requestExamFullscreen() {
  document.documentElement.requestFullscreen?.().catch(() => undefined)
}

function recordViolation(reason: string) {
  if (!proctoring.value) return
  const now = Date.now()
  if (now - lastViolationAt < 1500) return // 切屏会同时触发 blur + hidden，去重
  lastViolationAt = now
  violationCount.value += 1
  if (violationCount.value >= maxViolations) {
    ElMessageBox.alert(`检测到第 ${violationCount.value} 次${reason}，已达上限，系统将强制交卷。`, '监考警告', {
      type: 'error', confirmButtonText: '知道了', showClose: false
    }).then(() => forceSubmitHandler?.()).catch(() => forceSubmitHandler?.())
  } else {
    ElMessageBox.alert(
      `检测到${reason}（第 ${violationCount.value} 次）。考试期间禁止切换页面、切出窗口或最小化浏览器，累计 ${maxViolations} 次将强制交卷。`,
      '切屏警告',
      { type: 'warning', confirmButtonText: '返回考试', showClose: false }
    ).then(() => { window.focus(); requestExamFullscreen() }).catch(() => { window.focus(); requestExamFullscreen() })
  }
}

function onVisibilityChange() {
  // 切换标签页、最小化浏览器、锁屏都会使页面变为 hidden
  if (document.hidden) recordViolation('页面被隐藏（切屏 / 最小化）')
}
function onWindowBlur() {
  // 切到其它应用窗口（页面可能仍然可见）
  if (!document.hidden) recordViolation('考试窗口失去焦点')
}
function onFullscreenChange() {
  // Esc 或被系统强制退出全屏，视为一次违规行为并拉回全屏
  if (proctoring.value && !document.fullscreenElement) recordViolation('退出全屏考试环境')
}
function onBeforeUnload(e: BeforeUnloadEvent) {
  e.preventDefault() // 考试进行中关闭 / 刷新页面时浏览器原生确认
}

/** 在「进入考试」的点击 handler 中调用（全屏请求需要用户手势） */
export function startProctoring() {
  violationCount.value = 0
  lastViolationAt = 0
  proctoring.value = true
  requestExamFullscreen()
  document.addEventListener('visibilitychange', onVisibilityChange)
  window.addEventListener('blur', onWindowBlur)
  document.addEventListener('fullscreenchange', onFullscreenChange)
  window.addEventListener('beforeunload', onBeforeUnload)
}

/** 考试页挂载后注册强制交卷回调 */
export function setForceSubmitHandler(fn: (() => void) | null) {
  forceSubmitHandler = fn
}

export function stopProctoring() {
  proctoring.value = false
  forceSubmitHandler = null
  document.removeEventListener('visibilitychange', onVisibilityChange)
  window.removeEventListener('blur', onWindowBlur)
  document.removeEventListener('fullscreenchange', onFullscreenChange)
  window.removeEventListener('beforeunload', onBeforeUnload)
  if (document.fullscreenElement) document.exitFullscreen().catch(() => undefined)
}
