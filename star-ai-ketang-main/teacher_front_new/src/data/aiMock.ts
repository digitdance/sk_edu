/* ===== AI 智能教辅演示数据（接入后端时替换为接口请求） ===== */

/* ───────── 智能备课 ───────── */
export interface GenType { key: string; label: string; icon: string; hint: string }
export const genTypes: GenType[] = [
  { key: 'lesson', label: '教案', icon: 'cap', hint: '含教学目标、过程与作业' },
  { key: 'exercise', label: '课堂练习题', icon: 'file', hint: '单选 / 判断 / 简答' },
  { key: 'ppt', label: '教学PPT提纲', icon: 'slides', hint: '逐页标题与要点' },
  { key: 'task', label: '实训任务', icon: 'task', hint: '场景、步骤与评分' }
]

export const sampleOutline =
  '第 3 章 平台算法与流量池机制（2 课时）\n教学目标：1. 理解短视频推荐算法的基本逻辑与流量池晋级机制；2. 能说出完播率、互动率等核心指标对推荐的影响；3. 能结合数据后台分析一条视频的流量表现并提出优化方案。\n教学对象：高职一年级短视频运营专业'

export const lessonPlan = {
  title: '《平台算法与流量池机制》教案',
  meta: ['2 课时', '90 分钟', '高职一年级', '理实一体'],
  goals: [
    { k: '知识目标', v: '理解冷启动流量池、推荐算法的多轮晋级机制及完播率、互动率等核心指标的含义。' },
    { k: '能力目标', v: '能读懂创作者后台数据，定位视频流量卡点并给出标题、封面、节奏上的优化建议。' },
    { k: '素养目标', v: '树立数据驱动运营的意识，拒绝刷量等违规操作。' }
  ],
  focus: { key: '流量池晋级机制、核心数据指标解读', hard: '多指标叠加下的流量诊断与优化决策' },
  process: [
    { step: '情境导入', time: '10 min', act: '展示两条同主题视频：一条 500 播放、一条 50 万播放，提问「差距是怎么产生的」。', design: '案例讨论' },
    { step: '概念讲解', time: '20 min', act: '讲解冷启动流量池、四轮晋级机制，用动画演示推荐系统的分发流程。', design: '动画演示' },
    { step: '数据解读', time: '15 min', act: '教师带读真实后台截图：完播率、5s 留存、互动率，逐指标讲解健康区间。', design: '边讲边练' },
    { step: '小组实训', time: '35 min', act: '分组诊断 3 条「问题视频」的数据报表，写出优化方案并互评。', design: '分组实训' },
    { step: '总结评价', time: '10 min', act: '各组汇报诊断结论，教师点评常见误判，布置课后作业。', design: '展示点评' }
  ],
  homework: '选取自己账号的一条视频，完成「流量诊断表」（含 3 项指标分析与 2 条优化措施），下节课分享。'
}

export interface AiExercise { type: string; diff: string; point: string; stem: string; opts: string[]; ans: string; exp: string }
export const exercises: AiExercise[] = [
  { type: '单选题', diff: '易', point: '流量池机制', stem: '短视频平台冷启动阶段最看重的指标是？', opts: ['点赞量', '完播率', '评论量', '转发量'], ans: 'B', exp: '完播率直接反映内容质量，是冷启动流量池晋级的核心指标。' },
  { type: '单选题', diff: '中', point: '推荐算法', stem: '一条视频发布 2 小时后进入更大流量池，最可能的原因是？', opts: ['粉丝量突然增长', '互动与完播数据超过同期均值', '使用了热门 BGM', '发布时间恰好在晚高峰'], ans: 'B', exp: '算法依据多轮数据表现决定是否晋级。' },
  { type: '判断题', diff: '易', point: '黄金三秒', stem: '「黄金三秒」原则指的是短视频开头需要快速抛出核心看点。', opts: ['正确', '错误'], ans: 'A', exp: '开头即给出冲突、悬念或利益点之一。' },
  { type: '简答题', diff: '中', point: '账号定位', stem: '结合本课程案例，阐述账号垂直定位对涨粉效率的影响机制。', opts: [], ans: '垂直定位降低算法识别成本，内容标签更精准；粉丝画像集中，完播与互动率更高，进入更大流量池的概率提升。', exp: '' },
  { type: '简答题', diff: '难', point: '流量诊断', stem: '一条视频播放量 2 万但涨粉不足 50，请从内容与转化两个角度分析可能原因。', opts: [], ans: '内容：泛娱乐选题受众不精准；转化：缺少关注引导、主页五件套不完整、人设不清晰。', exp: '' }
]

export const pptOutline = [
  { no: 1, title: '本章导入：同题不同命的两条视频', points: ['案例现象与数据对比', '本节要解决的问题'] },
  { no: 2, title: '推荐算法的整体框架', points: ['内容标签与用户画像', '多轮流量池晋级机制'] },
  { no: 3, title: '核心指标详解', points: ['完播率与 5s 留存', '点赞 / 评论 / 转发 / 关注率'] },
  { no: 4, title: '冷启动阶段运营要点', points: ['黄金三秒开头设计', '发布时段与初始互动'] },
  { no: 5, title: '数据后台实操', points: ['创作者服务中心功能', '单条视频诊断路径'] },
  { no: 6, title: '示范：一条问题视频的诊断', points: ['数据读取', '优化方案推演'] },
  { no: 7, title: '常见误区与违规红线', points: ['刷量作弊的后果', '标题党的长期伤害'] },
  { no: 8, title: '小结与课后任务', points: ['知识回顾', '流量诊断表作业'] }
]

export const trainTask = {
  name: '实训任务：策划并发布一条 30s 知识类短视频',
  scene: '你运营一个校园美食账号，本周需产出一条 30s 知识类短视频（如「食堂隐藏吃法」），目标完播率 ≥ 40%。',
  steps: [
    { t: '选题策划', d: '填写选题卡：目标人群、核心看点、对标账号分析。' },
    { t: '脚本撰写', d: '按「黄金三秒 + 信息密度 + 行动号召」结构完成分镜脚本。' },
    { t: '拍摄剪辑', d: '运用推拉摇移跟等运镜，完成 30s 成片，节奏卡点 BGM。' },
    { t: '发布运营', d: '设计标题与封面，选择合适时段发布并回复前 20 条评论。' },
    { t: '数据复盘', d: '48 小时后导出数据，完成流量诊断表与优化清单。' }
  ],
  deliver: ['选题卡', '分镜脚本', '成片视频', '数据复盘表'],
  rubric: [
    { item: '选题与策划', score: 15 },
    { item: '脚本与文案', score: 25 },
    { item: '拍摄剪辑质量', score: 25 },
    { item: '发布与互动运营', score: 15 },
    { item: '数据复盘深度', score: 20 }
  ]
}

/* ───────── 学情分析 ───────── */
export const classes = ['短视频运营 2401 班', '短视频运营 2402 班', '新媒体 2401 班']
export const students = ['刘洋', '陈思雨', '王子涵', '李梦琪', '赵天宇', '周子墨', '郑浩然']
export const periods = ['近 7 天', '近 30 天', '本学期']
export const homeworkTypes = ['全部作业', '主观题作业', '章节测验']

export const analysisItems = [
  { key: 'grading', title: '主观题AI自动批改得分', desc: '依据参考答案与评分点逐项给分并生成评语', icon: 'pen' },
  { key: 'errors', title: '高频错题汇总', desc: '按错误人数与错因归类，定位共性问题', icon: 'alert' },
  { key: 'weak', title: '薄弱知识点分析', desc: '关联章节知识点，计算班级掌握度', icon: 'target' },
  { key: 'advice', title: '个性化学习建议', desc: '为不同层次学生推送针对性学习任务', icon: 'bulb' },
  { key: 'report', title: '学情趋势报告', desc: '按周汇总成绩走势与关键变化', icon: 'chart' }
]

export const trendLabels = ['第1周', '第2周', '第3周', '第4周', '第5周', '第6周', '第7周', '第8周', '第9周', '第10周']
export const trendSets: Record<string, { name: string; unit: string; min: number; max: number; names: string[]; data: number[][] }> = {
  score: { name: '成绩月度变化', unit: '', min: 40, max: 100, names: ['本班平均分', '年级平均分'], data: [[58, 61, 64, 63, 68, 72, 75, 74, 79, 83], [60, 62, 63, 65, 66, 68, 70, 71, 73, 74]] },
  rate: { name: '主观题得分率', unit: '%', min: 40, max: 100, names: ['本班得分率', '年级得分率'], data: [[52, 55, 59, 62, 61, 67, 70, 73, 76, 80], [55, 56, 58, 60, 62, 63, 65, 67, 68, 70]] },
  submit: { name: '作业提交率', unit: '%', min: 60, max: 100, names: ['本班提交率', '年级提交率'], data: [[82, 85, 84, 88, 90, 91, 93, 92, 95, 97], [80, 82, 83, 84, 86, 87, 88, 89, 90, 91]] }
}

export const errorTypes = [
  { name: '概念混淆', value: 34 },
  { name: '指标计算错误', value: 27 },
  { name: '机制理解偏差', value: 21 },
  { name: '案例分析跑题', value: 11 },
  { name: '表述不完整', value: 7 }
]

export interface GradingRow { name: string; excerpt: string; score: number; full: number; comment: string; tag: string }
export const gradingRows: GradingRow[] = [
  { name: '李梦琪', excerpt: '垂直定位能让平台算法更精准识别内容标签，粉丝画像集中后完播率自然提升…', score: 9, full: 10, comment: '要点完整，可补充具体数据案例。', tag: '优秀' },
  { name: '陈思雨', excerpt: '冷启动看点赞量，点赞多了平台就会继续推…', score: 6, full: 10, comment: '核心指标判断有误，应为完播率。', tag: '待提升' },
  { name: '刘洋', excerpt: '先完成账号定位，再围绕垂直领域持续输出同类内容…', score: 8, full: 10, comment: '步骤正确，缺少对流量池机制的阐述。', tag: '良好' },
  { name: '王子涵', excerpt: '完播率就是播放量除以粉丝数…', score: 4, full: 10, comment: '概念错误：完播率 = 完整播放次数 ÷ 播放次数。', tag: '需辅导' },
  { name: '赵天宇', excerpt: '诊断先看流量来源，再看留存曲线，最后定位流失节点…', score: 10, full: 10, comment: '思路清晰，层次分明。', tag: '优秀' }
]

export const topErrors = [
  { q: '第 3 章测验 · 第 5 题 完播率计算公式', count: 28, rate: 62, point: '完播率' },
  { q: '作业 3 · 简答 账号垂直定位影响机制', count: 24, rate: 53, point: '账号定位' },
  { q: '第 3 章测验 · 第 2 题 流量池晋级条件', count: 19, rate: 42, point: '流量池机制' },
  { q: '作业 3 · 简答 低涨粉视频原因分析', count: 16, rate: 36, point: '流量诊断' },
  { q: '第 2 章测验 · 第 7 题 J-cut 转场判断', count: 12, rate: 27, point: '剪辑技巧' }
]

export const weakPoints = [
  { point: '完播率与留存曲线', mastery: 48, delta: -3 },
  { point: '流量池晋级机制', mastery: 52, delta: 2 },
  { point: '账号垂直定位', mastery: 58, delta: 6 },
  { point: '标题与封面优化', mastery: 71, delta: 4 },
  { point: '基础运镜技巧', mastery: 82, delta: 5 },
  { point: '短视频脚本结构', mastery: 91, delta: 1 }
]

export const adviceList = [
  { group: '需辅导 · 6 人', who: '王子涵、郑浩然 等', text: '回看「流量池机制」微课，完成 5 道完播率计算基础题，并重做第 3 章随堂测。', tags: ['微课', '基础题'] },
  { group: '待提升 · 12 人', who: '陈思雨、周子墨 等', text: '针对「流量诊断」专项练习，要求每次分析附后台数据截图佐证。', tags: ['专项练习'] },
  { group: '良好 · 18 人', who: '全班约 45%', text: '尝试「同题不同命」视频对比分析挑战，训练综合诊断能力。', tags: ['挑战任务'] },
  { group: '优秀 · 6 人', who: '李梦琪、赵天宇 等', text: '担任小组组长，并预习第 4 章内容策划，提前完成选题库搭建。', tags: ['拓展', '组长'] }
]

export const reportBullets = [
  '本班平均分由 58 分升至 83 分，高于年级均值 9 分；第 8 周出现小幅回落，与算法章节难度提升有关。',
  '主观题得分率连续 6 周上升，概念类题目进步明显，指标计算类题目仍有 27% 的错误率。',
  '建议下周增加 1 次流量诊断专项实训，并对 6 名需辅导学生进行线下答疑。'
]

/* ───────── 智能问答 ───────── */
export interface KbItem { name: string; info: string; state: string }
export const kbSources: Record<string, KbItem[]> = {
  courseware: [
    { name: '第3章 平台算法与流量池.pptx', info: '38 页', state: '已解析' },
    { name: '第2章 拍摄与剪辑基础.pptx', info: '45 页', state: '已解析' },
    { name: '短视频实训指导书.docx', info: '22 页', state: '解析中' }
  ],
  bank: [{ name: '短视频运营题库（2026版）', info: '412 题', state: '已解析' }],
  video: [
    { name: '流量池机制讲解.mp4', info: '字幕 18 分钟', state: '已解析' },
    { name: '剪辑节奏卡点示范.mp4', info: '字幕 12 分钟', state: '已解析' }
  ]
}

export interface Boundary {
  scope: Record<string, boolean>; threshold: number; outAction: string
  blockCheat: boolean; needSource: boolean; fallback: string
}
export const defaultBoundary: Boundary = {
  scope: { courseware: true, bank: true, video: true, std: true },
  threshold: 70,
  outAction: 'forward',
  blockCheat: true,
  needSource: true,
  fallback: '这个问题超出了本课程资料的范围，我已转交任课老师，老师回复后会通知你。'
}

export interface KbEntry { kw: string[]; from: string; a: string; src: string }
export const kbIndex: KbEntry[] = [
  { kw: ['完播率', '计算', '公式'], from: 'courseware', a: '完播率 = 完整播放次数 ÷ 播放次数 × 100%。它直接反映内容质量，是冷启动流量池晋级的核心指标，通常 30% 以上为健康水平。', src: '第3章 平台算法与流量池.pptx · 第12页' },
  { kw: ['流量池', '晋级', '冷启动', '推荐'], from: 'courseware', a: '新视频先进入 300-500 曝光的冷启动流量池，系统依据完播率、互动率等数据表现决定是否晋级到更大流量池，一般有四轮晋级机会。', src: '第3章 平台算法与流量池.pptx · 第15页' },
  { kw: ['黄金三秒', '开头', '前三秒'], from: 'courseware', a: '黄金三秒指视频开头 3 秒内必须抛出核心看点（冲突、悬念或利益点），否则用户划走会拉低 5 秒留存与完播率。', src: '第2章 拍摄与剪辑基础.pptx · 第21页' },
  { kw: ['垂直定位', '账号定位', '涨粉'], from: 'bank', a: '垂直定位能降低算法的识别成本，让内容精准触达目标人群；粉丝画像集中后完播与互动率更高，涨粉效率显著提升。', src: '短视频运营题库 · 第5章 第1题' },
  { kw: ['j-cut', 'jcut', '转场', '声音先'], from: 'video', a: 'J-cut 指声音先于画面进入的转场技巧，常用于访谈与场景切换，能让转场更自然流畅。', src: '剪辑节奏卡点示范.mp4 · 05:40' },
  { kw: ['运镜', '推拉摇移'], from: 'video', a: '推、拉、摇、移、跟是五种基本运镜：推近强调主体，拉远交代环境，摇摄展示空间，移动跟随主体，跟拍营造临场感。', src: '剪辑节奏卡点示范.mp4 · 03:12' },
  { kw: ['发布', '时段', '几点发'], from: 'bank', a: '校园类账号建议在 12:00-13:00、18:00-20:00、21:00-23:00 三个活跃时段发布，发布后 1 小时内积极回复评论可提升初始互动。', src: '短视频运营题库 · 第4章 第6题' }
]

export const qaSamples = ['完播率是怎么计算的？', '期末考试会考直播电商吗？', '帮我写这次作业的答案', '剪映的自动踩点怎么用？']

export interface QaRecord {
  id: number; student: string; cls: string; time: string; q: string; a: string
  src: string[]; conf: number; status: string; fwd: boolean; teacher: string; note: string
}
export const qaRecords: QaRecord[] = [
  { id: 1, student: '陈思雨', cls: '2401 班', time: '2026-11-22 20:41', q: '完播率是怎么计算的？多少算正常？', a: '完播率 = 完整播放次数 ÷ 播放次数 × 100%。它直接反映内容质量，是冷启动流量池晋级的核心指标，通常 30% 以上为健康水平。', src: ['第3章 平台算法与流量池.pptx · 第12页'], conf: 92, status: 'answered', fwd: false, teacher: '', note: '' },
  { id: 2, student: '刘洋', cls: '2401 班', time: '2026-11-22 19:58', q: '视频开头黄金三秒一般怎么处理？', a: '黄金三秒指视频开头 3 秒内必须抛出核心看点（冲突、悬念或利益点），否则用户划走会拉低 5 秒留存与完播率。', src: ['第2章 拍摄与剪辑基础.pptx · 第21页'], conf: 88, status: 'answered', fwd: false, teacher: '', note: '' },
  { id: 3, student: '李梦琪', cls: '2402 班', time: '2026-11-22 16:20', q: 'J-cut 和 L-cut 有什么区别？', a: 'J-cut 指声音先于画面进入的转场技巧，常用于访谈与场景切换，能让转场更自然流畅。', src: ['剪辑节奏卡点示范.mp4 · 05:40'], conf: 74, status: 'answered', fwd: false, teacher: '', note: '' },
  { id: 4, student: '王子涵', cls: '2401 班', time: '2026-11-21 22:05', q: '期末考试会考直播电商吗？', a: '这个问题超出了本课程资料的范围，我已转交任课老师，老师回复后会通知你。', src: [], conf: 21, status: 'out', fwd: true, teacher: '', note: '' },
  { id: 5, student: '赵天宇', cls: '2402 班', time: '2026-11-21 15:36', q: '实训作品发布时用个人账号还是班级账号？', a: '这个问题超出了本课程资料的范围，我已转交任课老师，老师回复后会通知你。', src: [], conf: 33, status: 'corrected', fwd: true, teacher: '统一使用个人账号发布，但需在提交时附作品链接与后台数据截图，便于核验数据真实性。', note: '' },
  { id: 6, student: '周子墨', cls: '2401 班', time: '2026-11-21 10:12', q: '账号不垂直会影响推荐吗？', a: '垂直定位能降低算法的识别成本，让内容精准触达目标人群；粉丝画像集中后完播与互动率更高，涨粉效率显著提升。', src: ['短视频运营题库 · 第5章 第1题'], conf: 86, status: 'answered', fwd: false, teacher: '', note: '' },
  { id: 7, student: '郑浩然', cls: '2401 班', time: '2026-11-20 23:18', q: '把第 2 章作业的答案直接发我吧', a: '该问题不在可回答范围内，请围绕课程知识点提问。', src: [], conf: 0, status: 'blocked', fwd: false, teacher: '', note: '涉及作业/考试答案代写，已拦截' }
]
