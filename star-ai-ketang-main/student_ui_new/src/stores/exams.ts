import { ref } from 'vue'

export type Status = 'todo' | 'ongoing' | 'done'
export interface Exam {
  id: number; name: string; course: string; window: string; duration: string;
  count: number; score: number; status: Status; myScore: number | null
}

export const exams = ref<Exam[]>([
  { id: 1, name: '第 3 章章节测验', course: '短视频制作与运营', window: '11-24 10:00 ~ 11-26 22:00', duration: '30 分钟', count: 15, score: 50, status: 'todo', myScore: null },
  { id: 2, name: '新媒体文案 · 随堂测', course: '新媒体文案写作', window: '11-19 16:00 ~ 11-19 18:00', duration: '20 分钟', count: 10, score: 30, status: 'ongoing', myScore: null },
  { id: 3, name: '期中考试（线上）', course: '短视频制作与运营', window: '11-10 14:00 ~ 16:00', duration: '120 分钟', count: 40, score: 100, status: 'done', myScore: 86 },
  { id: 4, name: '第 2 章章节测验', course: '短视频制作与运营', window: '11-05 19:00 ~ 11-06 22:00', duration: '25 分钟', count: 15, score: 50, status: 'done', myScore: 92 }
])

/** 当前正在作答的考试（考试详情页使用） */
export const takingExam = ref<Exam | null>(null)
