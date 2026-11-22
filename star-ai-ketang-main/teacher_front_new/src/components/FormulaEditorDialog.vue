<template>
  <el-dialog :model-value="modelValue" title="插入公式" width="640px"
    @update:model-value="v => emit('update:modelValue', v)" @opened="onOpened">
    <el-tabs v-model="formulaTab">
      <!-- ============ 数学公式 ============ -->
      <el-tab-pane label="数学公式" name="math">
        <div class="preset-bar">
          <button v-for="p in mathPresets" :key="p.label" type="button" class="preset-btn"
            @click="insertMath(p.latex)">{{ p.label }}</button>
          <button type="button" class="preset-btn kb" @click="toggleKeyboard">
            <Keyboard :size="13" /> 虚拟键盘
          </button>
        </div>
        <math-field ref="mathFieldEl" class="math-field"
          virtual-keyboard-mode="manual"
          @input="onMathInput"></math-field>
      </el-tab-pane>

      <!-- ============ 化学方程式 ============ -->
      <el-tab-pane label="化学方程式" name="chem">
        <div class="preset-bar">
          <button v-for="p in chemPresets" :key="p.label" type="button" class="preset-btn"
            @click="insertChem(p.latex)">{{ p.label }}</button>
        </div>
        <el-input ref="chemInputEl" v-model="chemLatex" type="textarea" :rows="3"
          placeholder='使用 \ce{} 语法书写化学方程式，或点击上方示例快速插入' class="chem-input" />
        <p class="muted" style="margin:6px 2px 0">
          支持 mhchem 语法：\ce{H2O} 自动下标、-&gt; 反应箭头、&lt;=&gt; 可逆号、^ 气体、v 沉淀、-&gt;[\Delta] 反应条件
        </p>
      </el-tab-pane>
    </el-tabs>

    <!-- 实时预览 -->
    <div class="fx-preview">
      <span class="muted pv-label">预览</span>
      <div class="pv-body" v-html="previewHtml" />
    </div>
    <div class="fx-latex">
      <span class="muted pv-label">LaTeX</span>
      <code class="latex-src">{{ currentLatex || '（空）' }}</code>
    </div>

    <template #footer>
      <el-button @click="emit('update:modelValue', false)">取消</el-button>
      <el-button type="primary" :disabled="!currentLatex.trim()" @click="confirmInsert">插入到编辑器</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, nextTick, ref } from 'vue'
import katex from 'katex'
import 'katex/contrib/mhchem'
import 'mathlive'
import 'mathlive/fonts.css'
import { Keyboard } from 'lucide-vue-next'

const props = withDefaults(defineProps<{ modelValue: boolean; initialLatex?: string }>(), { initialLatex: '' })
const emit = defineEmits<{
  'update:modelValue': [v: boolean]
  insert: [latex: string]
}>()

const formulaTab = ref<'math' | 'chem'>('math')
const mathFieldEl = ref<HTMLElement>()
const chemInputEl = ref()
const mathLatex = ref('')
const chemLatex = ref('')
const keyboardOn = ref(false)

/* ---------- 预设片段 ---------- */
const mathPresets = [
  { label: '分式 a/b', latex: '\\frac{#?}{#?}' },
  { label: '根号 √', latex: '\\sqrt{#?}' },
  { label: '上标 x²', latex: '^{#?}' },
  { label: '下标 aᵢ', latex: '_{#?}' },
  { label: '求和 Σ', latex: '\\sum_{i=1}^{n}' },
  { label: '积分 ∫', latex: '\\int_{a}^{b}' },
  { label: '极限 lim', latex: '\\lim_{x \\to 0}' },
  { label: 'π', latex: '\\pi' },
  { label: 'α', latex: '\\alpha' },
  { label: 'θ', latex: '\\theta' },
  { label: '±', latex: '\\pm' },
  { label: '≤', latex: '\\le' },
  { label: '≠', latex: '\\ne' },
  { label: '向量', latex: '\\vec{#?}' }
]
const chemPresets = [
  { label: '水分子', latex: '\\ce{H2O}' },
  { label: '配平反应', latex: '\\ce{2H2 + O2 -> 2H2O}' },
  { label: '可逆反应', latex: '\\ce{N2 + 3H2 <=> 2NH3}' },
  { label: '加热条件', latex: '\\ce{CaCO3 ->[\\Delta] CaO + CO2 ^}' },
  { label: '沉淀', latex: '\\ce{Ba^2+ + SO4^2- -> BaSO4 v}' },
  { label: '气体逸出', latex: '\\ce{Zn + 2HCl -> ZnCl2 + H2 ^}' },
  { label: '离子', latex: '\\ce{Na+ + Cl-}' },
  { label: '同位素', latex: '\\ce{^{14}C}' }
]

/* ---------- 数学：MathLive 可视化编辑 ---------- */
function onMathInput() {
  mathLatex.value = (mathFieldEl.value as unknown as { value: string })?.value ?? ''
}
function insertMath(latex: string) {
  const mf = mathFieldEl.value as unknown as { insert: (s: string, o?: object) => void; focus: () => void }
  mf?.focus()
  mf?.insert(latex, { insertionMode: 'replaceSelection' })
  onMathInput()
}
function toggleKeyboard() {
  const mf = mathFieldEl.value as unknown as { showVirtualKeyboard: () => void; hideVirtualKeyboard: () => void }
  keyboardOn.value ? mf?.hideVirtualKeyboard() : mf?.showVirtualKeyboard()
  keyboardOn.value = !keyboardOn.value
}

/* ---------- 化学：mhchem 语法辅助输入 ---------- */
function insertChem(latex: string) {
  const el = chemInputEl.value?.textarea as HTMLTextAreaElement | undefined
  if (!el) { chemLatex.value += latex; return }
  const start = el.selectionStart ?? chemLatex.value.length
  chemLatex.value = chemLatex.value.slice(0, start) + latex + chemLatex.value.slice(el.selectionEnd ?? start)
  nextTick(() => { el.focus(); el.selectionStart = el.selectionEnd = start + latex.length })
}

/* ---------- 预览与插入 ---------- */
const currentLatex = computed(() => formulaTab.value === 'math' ? mathLatex.value : chemLatex.value)
const previewHtml = computed(() => {
  const src = currentLatex.value.trim()
  if (!src) return '<span style="color:#98a2b3;font-size:13px">输入公式后此处实时预览</span>'
  try {
    return katex.renderToString(src, { throwOnError: false, displayMode: true, strict: false, trust: true })
  } catch {
    return '<span style="color:#f56c6c;font-size:13px">公式语法有误，请检查</span>'
  }
})

function onOpened() {
  if (props.initialLatex) {
    if (props.initialLatex.includes('\\ce{')) { formulaTab.value = 'chem'; chemLatex.value = props.initialLatex }
    else {
      formulaTab.value = 'math'
      nextTick(() => {
        const mf = mathFieldEl.value as unknown as { setValue: (s: string) => void }
        mf?.setValue(props.initialLatex)
        onMathInput()
      })
    }
  }
}
function confirmInsert() {
  emit('insert', currentLatex.value.trim())
  emit('update:modelValue', false)
}
</script>

<style scoped>
.preset-bar { display: flex; flex-wrap: wrap; gap: 6px; margin-bottom: 10px; }
.preset-btn {
  border: 1px solid var(--app-line); background: #fff; border-radius: 6px; padding: 4px 10px;
  font-size: 12.5px; cursor: pointer; color: var(--app-ink-2); display: inline-flex; align-items: center; gap: 4px;
}
.preset-btn:hover { border-color: var(--el-color-primary); color: var(--el-color-primary); }
.math-field {
  display: block; width: 100%; min-height: 74px; padding: 10px 12px; font-size: 20px;
  border: 1px solid var(--el-border-color); border-radius: 8px; outline: none;
}
.math-field:focus-within { border-color: var(--el-color-primary); box-shadow: 0 0 0 2px var(--el-color-primary-light-8); }
.chem-input :deep(textarea) { font-family: 'JetBrains Mono', Consolas, monospace; font-size: 13px; }
.fx-preview {
  margin-top: 14px; border: 1px dashed var(--app-line); border-radius: 8px; padding: 10px 14px;
  display: flex; gap: 12px; align-items: center; min-height: 64px; background: #fafbff;
}
.pv-label { flex-shrink: 0; font-size: 12px; }
.pv-body { flex: 1; overflow-x: auto; padding: 4px 0; }
.fx-latex { display: flex; gap: 12px; align-items: flex-start; margin-top: 8px; }
.latex-src { flex: 1; font-size: 12px; color: var(--app-ink-2); background: #f5f7fb; border-radius: 6px; padding: 6px 10px; word-break: break-all; }
</style>
