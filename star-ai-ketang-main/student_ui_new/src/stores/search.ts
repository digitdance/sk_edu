import { reactive } from 'vue'

/** 顶栏全局搜索关键词，目标列表页消费后清空 */
export const globalSearch = reactive({ keyword: '' })
