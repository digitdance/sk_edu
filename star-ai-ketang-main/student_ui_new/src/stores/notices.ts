import { ref } from 'vue'

export interface Notice {
  id: number; course: string; type: 'primary' | 'success' | 'warning'
  title: string; time: string; publisher: string; readCount: number; content: string[]
}

export const notices = ref<Notice[]>([
  {
    id: 1, course: '短视频', type: 'primary', title: '第 3 章将于 11 月 24 日 08:00 定时开放',
    time: '11-19 10:20', publisher: '王琳', readCount: 96,
    content: [
      '各位同学：',
      '《短视频制作与运营》第 3 章「平台推荐机制与账号定位」将于 11 月 24 日（周一）08:00 定时开放，请大家合理安排学习进度。',
      '本章共 2 个小节、10 个任务点，包含 1 次章节测验。进入第 3 章前，请确保已完成第 2 章全部必修任务点（闯关模式）。',
      '本章配套的实操任务需要用到第 2 章下载的素材包，尚未下载的同学请提前到「课程资料」中准备。',
      '如有疑问，欢迎在讨论区留言或在线值班时间（每周三 14:00-16:00）咨询。'
    ]
  },
  {
    id: 2, course: '文案', type: 'success', title: '期末考核改为线上考试，请提前测试设备',
    time: '11-18 16:00', publisher: '李思颖', readCount: 112,
    content: [
      '各位同学：',
      '《新媒体文案写作》期末考核由原定的线下笔试调整为线上考试，时间为 12 月 20 日 19:00-21:00。',
      '考试期间将开启全屏防作弊监考，切屏、最小化浏览器累计 3 次将被强制交卷，请务必使用电脑端 Chrome / Edge 浏览器参加。',
      '请于考前完成以下准备：1）测试摄像头与网络；2）关闭无关软件与浏览器标签页；3）提前 15 分钟进入考试页面候考。',
      '模拟测试通道将于 12 月 18 日开放，建议大家提前熟悉考试系统操作。'
    ]
  },
  {
    id: 3, course: '短视频', type: 'primary', title: '第 2 章作业优秀案例已公布',
    time: '11-17 09:45', publisher: '王琳', readCount: 88,
    content: [
      '各位同学：',
      '第 2 章「视频剪辑基础」作业已批改完毕，本次作业优秀率 23%，整体完成质量较高。',
      '优秀作业案例集已上传到「课程资料」（优秀作业案例集.pdf），供大家学习参考。案例分析将于本周五直播课讲解。',
      '作业未通过的同学请在 11 月 26 日前修改后重新提交，逾期将影响平时成绩。'
    ]
  }
])

/** 当前查看的公告（公告详情页使用） */
export const activeNoticeId = ref<number | null>(null)
