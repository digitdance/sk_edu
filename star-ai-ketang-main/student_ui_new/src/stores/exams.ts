import { ref } from 'vue'

export type Status = 'todo' | 'ongoing' | 'done'
export interface Exam {
  id: number; name: string; course: string; window: string; duration: string;
  count: number; score: number; status: Status; myScore: number | null; paperId?: number; paperId?: number; paperId?: number; paperId?: number; paperId?: number
}

export const exams = ref<Exam[]>([])

/** 当前正在作答的考试（考试详情页使用） */
export const takingExam = ref<Exam | null>(null)
