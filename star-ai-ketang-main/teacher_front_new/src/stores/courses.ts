import { ref } from 'vue'
import { apiRequest } from '../api/client'
import { Clapperboard, PenLine, Camera, Radio, BookOpen } from 'lucide-vue-next'

export type SectionType = 'video' | 'doc' | 'quiz'
export interface Section { id: number; title: string; type: SectionType; taskPoint: boolean; quiz: boolean; duration: string }
export interface Chapter { id: number; title: string; points: number; sections: Section[] }
export interface Course {
  id: number; name: string; code: string; status: '已发布' | '建设中' | '草稿'
  level: string; hours: number; classes: number; students: number
  semester: string; updated: string; progress: number; cover: string; icon: unknown
  intro: string; chapters: Chapter[]
}

/** 当前登录教师（王琳）承担的全部课程 */
export const courses = ref<Course[]>([
  {
    id: 1, name: '短视频制作与运营', code: 'CSD-2024-0312', status: '已发布',
    level: '高职', hours: 48, classes: 3, students: 118, semester: '2026-2027-1',
    updated: '2026-11-12', progress: 92, icon: Clapperboard,
    cover: 'linear-gradient(135deg,#2f6bff,#6b4dff)',
    intro: '本课程面向数字媒体与电子商务专业，系统讲授短视频策划、拍摄、剪辑、发布与账号运营全流程。通过「学、练、评、赛」一体化设计，帮助学生掌握从 0 到 1 搭建账号的能力。',
    chapters: [
      {
        id: 1, title: '第 1 章 短视频认知与平台生态', points: 8,
        sections: [
          { id: 11, title: '1.1 课程导学', type: 'video', taskPoint: true, quiz: true, duration: '08:24' },
          { id: 12, title: '1.2 平台生态与发展趋势', type: 'video', taskPoint: true, quiz: false, duration: '12:10' },
          { id: 13, title: '1.3 阅读：主流平台规则对比', type: 'doc', taskPoint: false, quiz: false, duration: 'PDF · 12 页' }
        ]
      },
      {
        id: 2, title: '第 2 章 拍摄与剪辑基础', points: 12,
        sections: [
          { id: 21, title: '2.1 构图与运镜', type: 'video', taskPoint: true, quiz: true, duration: '15:42' },
          { id: 22, title: '2.2 剪辑软件快速上手', type: 'video', taskPoint: true, quiz: false, duration: '18:05' },
          { id: 23, title: '2.3 章节测验', type: 'quiz', taskPoint: true, quiz: true, duration: '10 题' }
        ]
      },
      {
        id: 3, title: '第 3 章 平台推荐机制与账号定位', points: 10,
        sections: [
          { id: 31, title: '3.1 推荐算法拆解', type: 'video', taskPoint: true, quiz: false, duration: '14:36' },
          { id: 32, title: '3.2 账号定位与人设打造', type: 'video', taskPoint: true, quiz: true, duration: '11:52' }
        ]
      },
      {
        id: 4, title: '第 4 章 内容策划与脚本写作', points: 7,
        sections: [
          { id: 41, title: '4.1 选题方法与热点追踪', type: 'doc', taskPoint: false, quiz: false, duration: 'PDF · 8 页' },
          { id: 42, title: '4.2 脚本模板实操', type: 'video', taskPoint: true, quiz: false, duration: '09:48' }
        ]
      },
      {
        id: 5, title: '第 5 章 账号运营与数据分析', points: 5,
        sections: [
          { id: 51, title: '5.1 数据指标解读', type: 'video', taskPoint: true, quiz: false, duration: '13:20' }
        ]
      }
    ]
  },
  {
    id: 2, name: '新媒体文案写作', code: 'CSD-2024-0287', status: '已发布',
    level: '高职', hours: 32, classes: 2, students: 86, semester: '2026-2027-1',
    updated: '2026-11-08', progress: 100, icon: PenLine,
    cover: 'linear-gradient(135deg,#0ea5a4,#22c55e)',
    intro: '本课程聚焦新媒体场景下的文案创作，覆盖标题打磨、种草文案、品牌故事与活动推文四大模块，强调「写得出、改得准、传得开」的实战能力。',
    chapters: [
      {
        id: 1, title: '第 1 章 新媒体文案思维', points: 6,
        sections: [
          { id: 11, title: '1.1 文案的底层逻辑', type: 'video', taskPoint: true, quiz: false, duration: '10:18' },
          { id: 12, title: '1.2 爆款标题公式', type: 'video', taskPoint: true, quiz: true, duration: '12:44' }
        ]
      },
      {
        id: 2, title: '第 2 章 种草与带货文案', points: 8,
        sections: [
          { id: 21, title: '2.1 种草文案结构拆解', type: 'video', taskPoint: true, quiz: false, duration: '14:02' },
          { id: 22, title: '2.2 实操：一篇推文的诞生', type: 'doc', taskPoint: true, quiz: false, duration: 'DOCX · 模板' }
        ]
      },
      {
        id: 3, title: '第 3 章 品牌故事写作', points: 6,
        sections: [
          { id: 31, title: '3.1 品牌叙事框架', type: 'video', taskPoint: true, quiz: true, duration: '11:26' }
        ]
      },
      {
        id: 4, title: '第 4 章 期末文案工作坊', points: 4,
        sections: [
          { id: 41, title: '4.1 作品互评与修改', type: 'quiz', taskPoint: true, quiz: true, duration: '任务' }
        ]
      }
    ]
  },
  {
    id: 3, name: '摄影摄像基础', code: 'CSD-2025-0105', status: '建设中',
    level: '高职', hours: 40, classes: 1, students: 42, semester: '2026-2027-1',
    updated: '2026-10-29', progress: 45, icon: Camera,
    cover: 'linear-gradient(135deg,#f59e0b,#ef4444)',
    intro: '本课程为数字媒体专业基础课，讲授相机操作、曝光三要素、用光与构图基础，为后续短视频与影视课程打底。',
    chapters: [
      {
        id: 1, title: '第 1 章 认识你的相机', points: 4,
        sections: [
          { id: 11, title: '1.1 相机结构与参数', type: 'video', taskPoint: true, quiz: false, duration: '09:36' }
        ]
      },
      {
        id: 2, title: '第 2 章 曝光与用光', points: 6,
        sections: [
          { id: 21, title: '2.1 曝光三要素', type: 'video', taskPoint: true, quiz: true, duration: '13:12' }
        ]
      }
    ]
  },
  {
    id: 4, name: '直播电商运营实务', code: 'CSD-2026-0033', status: '草稿',
    level: '高职', hours: 32, classes: 0, students: 0, semester: '2026-2027-2',
    updated: '2026-09-18', progress: 20, icon: Radio,
    cover: 'linear-gradient(135deg,#8b5cf6,#ec4899)',
    intro: '面向电子商务专业的实训课程，覆盖直播间搭建、话术设计、流量投放与复盘分析，计划于下学期开课。',
    chapters: [
      {
        id: 1, title: '第 1 章 直播电商概览', points: 2,
        sections: [
          { id: 11, title: '1.1 行业格局与岗位能力', type: 'doc', taskPoint: false, quiz: false, duration: 'PDF · 6 页' }
        ]
      }
    ]
  }
])


interface ApiCourse {
  id: number; title: string; subtitle?: string; description?: string; status?: number; statusDesc?: string;
  teacherId?: number; totalDuration?: number; totalChapters?: number; totalSections?: number; studentCount?: number;
  coverImage?: string; updateTime?: string
}
interface ApiSection { id: number; title: string; videoUrl?: string; resourceUrl?: string; duration?: number; accessible?: boolean }
interface ApiChapter { id: number; title: string; sections?: ApiSection[] }
interface ApiStructure { chapters: ApiChapter[] }

export async function loadTeacherCourses(teacherId: number) {
  const items = await apiRequest<ApiCourse[]>(`/api/course/teacher/${teacherId}?page=1&size=100`)
  const loaded = await Promise.all(items.map(async (item) => {
    let chapters: Chapter[] = []
    try {
      const structure = await apiRequest<ApiStructure>(`/api/course/${item.id}/structure`)
      chapters = (structure.chapters ?? []).map((chapter) => {
        const sections = (chapter.sections ?? []).map((section) => ({
          id: section.id, title: section.title, type: section.resourceUrl && !section.videoUrl ? 'doc' as const : 'video' as const,
          taskPoint: true, quiz: false, duration: section.duration ? `${Math.floor(section.duration / 60)}:${String(section.duration % 60).padStart(2, '0')}` : '00:00'
        }))
        return { id: chapter.id, title: chapter.title, points: sections.length, sections }
      })
    } catch {
      chapters = []
    }
    return {
      id: item.id, name: item.title, code: `COURSE-${item.id}`,
      status: (item.statusDesc ?? (item.status === 1 ? '已发布' : '建设中')) as Course['status'],
      level: '', hours: Math.round((item.totalDuration ?? 0) / 60), classes: 0, students: item.studentCount ?? 0,
      semester: '', updated: item.updateTime?.slice(0, 10) ?? '', progress: 0,
      icon: BookOpen, cover: 'linear-gradient(135deg,#2f6bff,#6b4dff)', intro: item.description ?? item.subtitle ?? '', chapters
    } satisfies Course
  }))
  courses.value = loaded
}


export const chapterPoints = (c: Course) => c.chapters.reduce((s, ch) => s + ch.points, 0)

/** 当前正在门户预览/编辑的课程 */
export const portalCourseId = ref<number | null>(null)
