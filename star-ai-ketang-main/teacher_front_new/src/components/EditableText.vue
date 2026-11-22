<template>
  <span v-if="!editing" class="editable" :title="hint" @click.stop="startEdit">
    <slot>{{ modelValue }}</slot>
    <Pencil :size="11" class="pen" />
  </span>
  <span v-else ref="editEl" class="editable editing" :class="{ multi }" contenteditable="true"
    @blur="finish" @keydown="onKeydown" @click.stop>{{ modelValue }}</span>
</template>

<script setup lang="ts">
import { nextTick, ref } from 'vue'
import { Pencil } from 'lucide-vue-next'

const props = withDefaults(defineProps<{
  modelValue: string
  multiline?: boolean
  hint?: string
}>(), { multiline: false, hint: '点击编辑' })

const emit = defineEmits<{ 'update:modelValue': [v: string] }>()

const editing = ref(false)
const editEl = ref<HTMLElement>()

function startEdit() {
  editing.value = true
  nextTick(() => {
    const el = editEl.value
    if (!el) return
    el.focus()
    const range = document.createRange()
    range.selectNodeContents(el)
    range.collapse(false)
    const sel = window.getSelection()
    sel?.removeAllRanges()
    sel?.addRange(range)
  })
}
function finish() {
  if (!editing.value) return
  editing.value = false
  const text = (editEl.value?.innerText ?? '').trim()
  if (text && text !== props.modelValue) emit('update:modelValue', text)
}
function onKeydown(e: KeyboardEvent) {
  // 单行模式 Enter 完成；多行模式 Ctrl/⌘+Enter 完成
  if (e.key === 'Enter' && (!props.multiline || e.ctrlKey || e.metaKey)) {
    e.preventDefault()
    ;(e.target as HTMLElement).blur()
  }
}
</script>

<style scoped>
.editable { position: relative; cursor: text; border-radius: 4px; transition: background .12s ease; }
.editable .pen { opacity: 0; margin-left: 4px; vertical-align: baseline; color: var(--el-color-primary); }
.editable:hover { background: var(--el-color-primary-light-9); outline: 1px dashed var(--el-color-primary-light-5); outline-offset: 2px; }
.editable:hover .pen { opacity: 1; }
.editable.editing {
  display: inline-block; min-width: 80px; outline: 1.5px solid var(--el-color-primary) !important;
  outline-offset: 2px; background: #fff; cursor: text;
}
.editable.editing.multi { display: block; white-space: pre-wrap; }
</style>
