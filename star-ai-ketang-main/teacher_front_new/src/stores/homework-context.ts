import { ref } from 'vue'

export interface HomeworkContext {
  courseId: number
  chapterId: number
  sectionId: number
  sectionTitle: string
  description?: string
}

export const homeworkContext = ref<HomeworkContext | null>(null)

export function openHomeworkForSection(context: HomeworkContext) {
  homeworkContext.value = context
}
