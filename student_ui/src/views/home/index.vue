<template>
  <div>
    <common-header></common-header>
    <div >
      <section class="bg-gradient text-white py-16 md:py-20">
            <div class="max-w-7xl mx-auto px-4">
                <div class="flex flex-col md:flex-row items-center justify-between gap-12">
                    <div class="max-w-2xl animate-fade-in">
                        <h1 class="text-[clamp(2.5rem,6vw,4rem)] font-bold leading-tight mb-6">
                            数智营销产业学院<br>
                            <span class="text-white/90">教育平台</span>
                        </h1>
                        <p class="text-white/80 text-lg mb-8 max-w-xl">
                            TradeNest 数智营销产业学院教育平台，专注于电商、营销、数据分析等核心领域的职业技能培养，为商科教学提供扎实的数字化支撑，助力学生精准掌握职业核心技能，让技能培养更具针对性与实效性。
                        </p>
                    </div>
                    <!-- 现代科技图形元素 -->
                    <div class="w-full md:w-1/2 animate-slide-up">
                        <div class="relative">
                            <!-- 主图形 -->
                            <div class="absolute inset-0 bg-white/10 backdrop-blur-md rounded-2xl transform rotate-3"></div>
                            <div class="absolute inset-0 bg-white/5 backdrop-blur-md rounded-2xl transform -rotate-2"></div>
                            <div class="relative bg-white rounded-2xl p-8 shadow-lg">
                                <div class="flex flex-col gap-6">
                                    <div class="flex items-center gap-4">
                                        <div class="w-12 h-12 rounded-lg bg-secondary/10 flex items-center justify-center text-secondary">
                                            <i class="fa-solid fa-bullseye text-xl"></i>
                                        </div>
                                        <div>
                                            <h3 class="font-semibold">领域精准聚焦</h3>
                                            <p class="text-sm text-gray">深耕电商、营销、数据分析，精准锚定数字商科人才培养方向</p>
                                        </div>
                                    </div>
                                    <div class="flex items-center gap-4">
                                        <div class="w-12 h-12 rounded-lg bg-success/10 flex items-center justify-center text-success">
                                            <i class="fa-solid fa-robot text-xl"></i>
                                        </div>
                                        <div>
                                            <h3 class="font-semibold">AI 智能实训</h3>
                                            <p class="text-sm text-gray">搭载智能课程工坊，AI 赋能直播与营销文案全流程实训创作</p>
                                        </div>
                                    </div>
                                    <div class="flex items-center gap-4">
                                        <div class="w-12 h-12 rounded-lg bg-accent/10 flex items-center justify-center text-accent">
                                            <i class="fa-solid fa-check-circle text-xl"></i>
                                        </div>
                                        <div>
                                            <h3 class="font-semibold">技能实效培育</h3>
                                            <p class="text-sm text-gray">依托数字化教学支撑，高效打造贴合岗位需求的核心职业能力</p>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <!-- 核心功能：锚点 #features -->
        <section id="features" class="section">
            <div class="text-center mb-16">
                <h2 class="text-[clamp(1.75rem,4vw,2.5rem)] font-bold mb-4">核心功能</h2>
                <p class="text-gray max-w-4xl mx-auto text-lg">专为数智营销产业培训打造的资源管理平台，聚焦核心技能素养培育与教学数字化建设。</p>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
                <!-- 功能卡片1：多形态课程交付 -->
                <div class="card-gradient rounded-2xl p-8 shadow-soft card-hover">
                    <div class="w-16 h-16 rounded-xl bg-secondary/10 flex items-center justify-center text-secondary mb-6">
                        <i class="fa-solid fa-bullseye text-2xl"></i>
                    </div>
                    <h3 class="text-xl font-semibold mb-4">1. 领域聚焦精准化</h3>
                    <p class="text-gray">深耕电商运营、新媒体营销、数据分析等数字商科核心赛道，紧密对接行业岗位需求与职业能力标准，精准锚定商科人才技能培养核心方向，构建贴合产业发展的专业化培养体系。</p>
                </div>

                <!-- 功能卡片2：AI 智能辅助答疑 -->
                <div class="card-gradient rounded-2xl p-8 shadow-soft card-hover">
                    <div class="w-16 h-16 rounded-xl bg-success/10 flex items-center justify-center text-success mb-6">
                        <i class="fa-solid fa-robot text-2xl"></i>
                    </div>
                    <h3 class="text-xl font-semibold mb-4">3. 实训工具智能化</h3>
                    <p class="text-gray">搭载自研TradeNest智能课程工坊，深度整合产品手册专业知识体系，依托AI技术赋能内容创作全流程，一站式辅助学生完成直播话术、营销推广文案等实战创作实训，全面提升商科学生新媒体内容创作实操能力。</p>
                </div>

                <!-- 功能卡片3：学习数据追踪 -->
                <div class="card-gradient rounded-2xl p-8 shadow-soft card-hover">
                    <div class="w-16 h-16 rounded-xl bg-accent/10 flex items-center justify-center text-accent mb-6">
                        <i class="fa-solid fa-check-circle text-2xl"></i>
                    </div>
                    <h3 class="text-xl font-semibold mb-4">2. 技能培养实效化</h3>
                    <p class="text-gray">聚焦数字商科职业核心技能打磨，依托平台完备的数字化教学支撑体系，实现教学内容与岗位实操需求精准匹配，让商科技能培养更具针对性、落地性与实效性，助力学生高效掌握职场核心竞争力。</p>
                </div>
            </div>
        </section>

        <!-- 热门课程：锚点 #courses -->
        <section id="courses" class="section bg-gray/5">
            <div class="flex flex-col md:flex-row justify-between items-start md:items-center mb-12 gap-4">
                <div>
                    <h2 class="text-[clamp(1.75rem,4vw,2.5rem)] font-bold mb-3">热门数字商科课程</h2>
                    <p class="text-gray text-lg">精选优质职业技能培训课程，覆盖电商、营销、数据分析等核心领域</p>
                </div>
                <a href="课程广场.html" class="text-secondary font-medium hover:underline flex items-center gap-2">
                    <span>查看全部</span>
                    <i class="fa-solid fa-arrow-right"></i>
                </a>
            </div>

            <!-- 热门课程列表 - 由后端动态生成，固定显示4门课程 -->
            <!-- 展示规则：技术可根据以下规则选择展示的课程：
                 1. 课程热度（浏览量、学习人数）
                 2. 最新上线时间
                 3. 推荐算法（综合考虑用户兴趣、课程评分等）
                 4. 手动置顶推荐
                 无论采用哪种规则，此处固定只显示4门课程 -->
            <div id="hot-courses-list" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-8">
                <!-- 课程卡片示例 - 实际数据由后端返回 -->
                <div style="width: 320px;" class="bg-white rounded-2xl overflow-hidden shadow-soft card-hover" :key="index" v-for="(course,index) in courseList">
                    <div class="w-[320px] h-[400px] mx-auto overflow-hidden aaa">
                        <!-- 课程图片：从后台获取 -->
                        <img :src="course.logo" alt="短视频制作与运营" class="w-full h-full object-cover">
                    </div>
                    <div class="p-6">
                        <!-- 课程标题：从后台获取 -->
                        <h3 class="text-lg font-semibold mb-3">{{ course.courseName }}</h3>
                        <!-- 课程描述：从后台获取 -->
                        <p class="text-gray text-sm mb-6 line-clamp-2">{{ course.title }}</p>
                        <div class="flex justify-end items-center">
                            <!-- 课程详情链接：从后台获取课程ID拼接 -->
                            <router-link target="_blank" :to="{path:'/course/courseInfo/'+course.id}" :title="course.courseName" class="text-secondary font-medium hover:underline text-sm">
                                查看详情
                          </router-link>

                          
                        </div>
                    </div>
                </div>

                

                
            </div>
        </section>
    </div>
    <common-footer></common-footer>
  </div>
</template>

<script>
  import left from '@/views/home/left'
  import right from '@/views/home/right'
  import commonHeader from "@/views/common/header";
  import commonFooter from "@/views/common/footer";
  import mainContent from "./mainContent.vue";
  import {course} from "@/api";
  export default {
    name: 'index',
    data () {
      return {
        subjectId:"0",
        courseList:[],
        size:4,
        orderBy:""
      }
    },
    components:{
      commonHeader,
      commonFooter,
      mainContent,
      left,
      right
    },
    mounted:function() {
      this.selectCouseList();
    },
    methods: {
      clickSubjectId(subjectId){
        this.subjectId = subjectId+""
      },
      selectCouseList(){
        let subjectId = "";
        let level = "";
        let sellType = "1";
        let courseName = ""
        course(this.current,this.size,sellType,level,subjectId,courseName,'',this.orderBy).then(res =>{
          this.courseList = res.data.data.records;
        })
      },
    },

  }
</script>

<style scoped>

body{
  background-color: #f9f9f9;
}
.section{
    max-width: 100rem!important;
}
</style>
