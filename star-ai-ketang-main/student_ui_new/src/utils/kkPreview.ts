/**
 * kkFileView 在线文档预览服务集成
 * 调用方式：GET {BASE}/onlinePreview?url={Base64(文件URL)}
 */
export const KKFILEVIEW_BASE = 'http://139.9.162.79:8012'

/**
 * 演示环境：平台内示例文档映射到可公开访问的样例文件，
 * 生产环境应替换为资源实际上传后的真实文件地址。
 */
const DEMO_FILE_URLS: Record<string, string> = {
  pdf: 'https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf',
  doc: 'https://calibre-ebook.com/downloads/demos/demo.docx',
  docx: 'https://calibre-ebook.com/downloads/demos/demo.docx',
  ppt: 'https://raw.githubusercontent.com/apache/poi/trunk/test-data/slideshow/SampleShow.pptx',
  pptx: 'https://raw.githubusercontent.com/apache/poi/trunk/test-data/slideshow/SampleShow.pptx',
  xls: 'https://oss.sheetjs.com/test_files/formula_stress_test.xlsx',
  xlsx: 'https://oss.sheetjs.com/test_files/formula_stress_test.xlsx'
}

/** 按文件扩展名取文件地址（演示映射），不支持的类型返回 null */
export function fileUrlOf(name: string): string | null {
  const ext = name.split('.').pop()?.toLowerCase() ?? ''
  return DEMO_FILE_URLS[ext] ?? null
}

/** 构造 kkFileView 预览地址（url 参数需 Base64 编码，兼容中文文件名） */
export function kkPreviewUrl(fileUrl: string): string {
  const b64 = window.btoa(unescape(encodeURIComponent(fileUrl)))
  return `${KKFILEVIEW_BASE}/onlinePreview?url=${encodeURIComponent(b64)}`
}

/** 新窗口打开 kkFileView 文档预览；不支持的类型返回 false */
export function previewDocument(name: string, fileUrl?: string): boolean {
  const url = fileUrl ?? fileUrlOf(name)
  if (!url) return false
  window.open(kkPreviewUrl(url), '_blank')
  return true
}
