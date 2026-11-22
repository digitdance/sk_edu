import { ref } from 'vue'
import {
  FileText, ListChecks, Megaphone, FolderOpen, ClipboardList, Link2, Users
} from 'lucide-vue-next'
import type { Course } from './courses'

export type ModuleType = 'intro' | 'requirement' | 'notice' | 'material' | 'task' | 'link' | 'team'

export interface PortalModule {
  id: number
  type: ModuleType
  title: string
  visible: boolean          // 是否公开显示（学生端可见）
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  data: any
}

export interface ModuleDef { type: ModuleType; label: string; icon: unknown; desc: string }

export const moduleDefs: ModuleDef[] = [
  { type: 'intro', label: '课程介绍', icon: FileText, desc: '课程定位、教学目标等富文本内容' },
  { type: 'requirement', label: '教学要求', icon: ListChecks, desc: '先修要求、学习纪律与考核说明' },
  { type: 'notice', label: '课程公告', icon: Megaphone, desc: '面向学生的通告与教学安排' },
  { type: 'material', label: '课程资料', icon: FolderOpen, desc: '课件、文档等可下载资料' },
  { type: 'task', label: '学习任务', icon: ClipboardList, desc: '任务点、作业与测验安排' },
  { type: 'link', label: '资源链接', icon: Link2, desc: '外部教学资源链接导航' },
  { type: 'team', label: '教师团队', icon: Users, desc: '主讲教师、助教与师资简介' }
]

/** 各课程的门户模块（按课程 id 持久保存编辑结果） */
export const portalModules = ref<Record<number, PortalModule[]>>({})

let seed = 1
const nid = () => Date.now() * 100 + (seed++ % 100)

/** 新课程 / 首次预览时的默认门户结构 */
export function ensurePortalModules(course: Course): PortalModule[] {
  if (!portalModules.value[course.id]) {
    portalModules.value[course.id] = [
      {
        id: nid(), type: 'intro', title: '课程介绍', visible: true,
        data: { text: course.intro }
      },
      {
        id: nid(), type: 'requirement', title: '教学要求', visible: true,
        data: {
          text: '1. 按时完成各章节任务点，视频任务点需完整观看；\n2. 章节测验截止后不再补做，请合理安排学习进度；\n3. 积极参与讨论区互动，优质回帖计入平时成绩；\n4. 期末考试前需完成全部必修任务点。'
        }
      },
      {
        id: nid(), type: 'notice', title: '课程公告', visible: true,
        data: {
          items: [
            { id: nid(), tag: '教学安排', title: '第 3 章将于 11 月 24 日 08:00 定时开放', time: '2026-11-19 10:20' },
            { id: nid(), tag: '考试', title: '期末考核改为线上考试，请提前测试设备', time: '2026-11-18 16:00' }
          ]
        }
      },
      {
        id: nid(), type: 'material', title: '课程资料', visible: true,
        data: {
          items: [
            { id: nid(), name: '课程导学课件.pptx', meta: 'PPT · 8.6 MB · 更新 2026-11-10' },
            { id: nid(), name: '优秀作业案例集.pdf', meta: 'PDF · 4.2 MB · 更新 2026-11-05' }
          ]
        }
      },
      {
        id: nid(), type: 'task', title: '学习任务', visible: true,
        data: {
          items: [
            { id: nid(), title: '观看 1.1 课程导学', kind: '视频任务点', deadline: '本周五 22:00' },
            { id: nid(), title: '第 2 章作业 · 视频剪辑基础', kind: '作业', deadline: '后天 22:00' },
            { id: nid(), title: '第 3 章章节测验', kind: '测验', deadline: '11-24 开放' }
          ]
        }
      },
      {
        id: nid(), type: 'link', title: '资源链接', visible: true,
        data: {
          items: [
            { id: nid(), title: '中国大学 MOOC · 新媒体运营', url: 'https://www.icourse163.org', desc: '推荐拓展学习课程' },
            { id: nid(), title: '剪映官方教程中心', url: 'https://www.capcut.cn', desc: '剪辑软件官方文档' }
          ]
        }
      },
      {
        id: nid(), type: 'team', title: '教师团队', visible: true,
        data: {
          members: [
            { id: nid(), name: '王琳', role: '主讲教师', dept: '信息工程学院', bio: '数字媒体方向副教授，主讲短视频制作与新媒体运营课程 8 年，主持省级在线精品课程 2 门。' },
            { id: nid(), name: '李思颖', role: '助教', dept: '数字媒体教研室', bio: '负责作业批阅与答疑，每周三 14:00-16:00 在线值班。' }
          ]
        }
      }
    ]
  }
  return portalModules.value[course.id]
}

/** 添加模块时的默认数据 */
export function createModule(type: ModuleType): PortalModule {
  const def = moduleDefs.find(d => d.type === type)!
  const base = { id: nid(), type, title: def.label, visible: true }
  switch (type) {
    case 'intro': return { ...base, data: { text: '（点击文字直接编辑课程介绍…）' } }
    case 'requirement': return { ...base, data: { text: '（点击文字直接编辑教学要求…）' } }
    case 'notice': return { ...base, data: { items: [] } }
    case 'material': return { ...base, data: { items: [] } }
    case 'task': return { ...base, data: { items: [] } }
    case 'link': return { ...base, data: { items: [] } }
    case 'team': return { ...base, data: { members: [] } }
  }
}
