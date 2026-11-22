import { apiRequest } from './client'

export interface HomeworkAssignment {
  id: number
  title: string
  description?: string
  courseId: number
  courseName: string
  chapterId?: number
  sectionId?: number
  totalScore: number
  passScore: number
  deadline?: string
  status?: string
  settings?: string | Record<string, unknown>
  submissionId?: number
  submissionStatus?: 'SUBMITTED' | 'GRADED'
  score?: number
  teacherComment?: string
  submittedAt?: string
  answers?: string | Record<string, unknown>
  attachments?: string | string[]
  classCount?: number
  classIds?: string | number[]
  submissionCount?: number
}

export interface HomeworkSubmission {
  id: number
  studentId: number
  studentName: string
  studentAccount: string
  attemptNo: number
  answers: string | Record<string, unknown>
  attachments: string | string[]
  status: 'SUBMITTED' | 'GRADED'
  score?: number
  teacherComment?: string
  submittedAt: string
}

export interface AssignmentPayload {
  courseId: number
  chapterId?: number | null
  sectionId?: number | null
  title: string
  description?: string
  totalScore: number
  passScore: number
  deadline?: string | null
  classIds: number[]
  settings: Record<string, unknown>
}

export const listTeacherAssignments = () => apiRequest<HomeworkAssignment[]>('/api/homework/assignments')
export const createAssignment = (payload: AssignmentPayload) => apiRequest<number>('/api/homework/assignments', { method: 'POST', body: payload })
export const updateAssignment = (id: number, payload: AssignmentPayload) => apiRequest<boolean>(`/api/homework/assignments/${id}`, { method: 'PUT', body: payload })
export const publishAssignment = (id: number) => apiRequest<boolean>(`/api/homework/assignments/${id}/publish`, { method: 'POST' })
export const listMyAssignments = () => apiRequest<HomeworkAssignment[]>('/api/homework/my-assignments')
export const submitAssignment = (id: number, answers: Record<string, unknown>, attachmentUrls: string[]) =>
  apiRequest<number>(`/api/homework/assignments/${id}/submissions`, { method: 'POST', body: { answers, attachmentUrls } })
export const listAssignmentSubmissions = (id: number) => apiRequest<HomeworkSubmission[]>(`/api/homework/assignments/${id}/submissions`)
export const gradeAssignmentSubmission = (id: number, score: number, comment: string) =>
  apiRequest<boolean>(`/api/homework/submissions/${id}/grade`, { method: 'POST', body: { score, comment } })
export const uploadHomeworkAttachment = (file: File) => {
  const body = new FormData()
  body.append('file', file)
  return apiRequest<{ fileUrl: string; originalName: string }>('/api/file/upload/grading/homework', { method: 'POST', body })
}
