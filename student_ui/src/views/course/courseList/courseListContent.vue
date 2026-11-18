<template>
  <div class="section">
     <!-- 课程广场主区域 -->
    
        <!-- 页面标题 -->
        <div class="text-center mb-16">
            <h1 class="text-[clamp(2rem,5vw,3rem)] font-bold mb-4">课程广场</h1>
            <p class="text-gray max-w-2xl mx-auto text-lg">发现适合您的数智营销产业培训课程，开启学习之旅</p>
        </div>

        <!-- 课程列表：由后端动态生成，支持分页和筛选 -->
        <!-- 注意：课程数量不固定，可能为20门、30门或更多，当前布局为响应式网格布局：
             - 移动端：1列
             - 平板：2列
             - 桌面：3列
             该布局可根据课程数量自动扩展，无需手动调整 -->
        <div id="courses-list" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-8">
            <!-- 课程卡片模板：实际数据由后端返回，这里只展示一个示例 -->
            <!-- 后端需根据实际课程数量动态生成多个此类卡片 -->
            <div class="course-card animate-fade-in"  v-for="(course,index) in courseList" :key="index">
                <div class="course-image group">
                    <div class="pt-6 h-full">
                        <!-- 课程图片：从后台获取 -->
                        <img :src="course.logo" alt="短视频制作与运营" class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105">
                    </div>
                    <!-- 科技感覆盖层 -->
                    <div class="absolute inset-0 bg-gradient-to-t to-transparent opacity-70"></div>
                    <!-- 科技感边框 -->
                    <div class="absolute top-6 inset-x-0 bottom-0 border border-secondary/30"></div>

                    <!-- 科技感图标 -->
                    <div class="absolute bottom-3 right-3 bg-primary/80 text-secondary w-8 h-8 rounded-full flex items-center justify-center backdrop-blur-sm">
                        <i class="fa-solid fa-play"></i>
                    </div>
                </div>
                <div class="p-6">
                    <div class="flex items-center gap-2 mb-3">
                        <div class="w-1 h-6 bg-secondary rounded-full"></div>
                        <!-- 课程标题：从后台获取 -->
                        <h3 class="font-bold text-xl text-gray-900">{{ course.courseName }}</h3>
                    </div>
                    <!-- 课程描述：从后台获取 -->
                    <p class="text-gray mb-6 line-clamp-2 text-sm">掌握短视频拍摄、剪辑、运营全流程，适配数智营销产业培训体系。</p>
                    <div class="flex justify-between items-center">
                        <div class="flex items-center gap-4 text-sm text-gray">
                            <span class="flex items-center gap-1">
                                <i class="fa-solid fa-eye"></i>
                                <!-- 课程浏览量：从后台获取 -->
                                <span>{{ course.pageViewcount }}</span>
                            </span>
                            <span class="flex items-center gap-1">
                                <i class="fa-solid fa-user-graduate"></i>
                                <!-- 课程学习人数：从后台获取 -->
                                <span>{{ course.pageBuycount }}</span>
                            </span>
                        </div>
                        <!-- 开始学习按钮：链接到课程详情或学习页面 -->
                        <router-link target="_blank" :to="{path:'/course/courseInfo/'+course.id}" :title="course.courseName" class="btn-learn">
                          开始学习
                          </router-link>
                        <!-- <button class="btn-learn">
                            开始学习
                        </button> -->
                    </div>
                </div>
            </div>

        </div>

        <!-- 分页控件：由后端根据课程总数动态生成 -->
        <page-component id="pagination-container" class="flex justify-center mt-12" :page="page" @goto="goto" v-show="courseList.length > 0" ></page-component>

        <!-- <div id="pagination-container" class="flex justify-center mt-12">
            <div class="flex items-center gap-3">
              
                <button id="prev-page-btn" class="w-10 h-10 rounded-lg bg-white border border-gray/20 flex items-center justify-center text-gray hover:border-secondary hover:text-secondary transition-all">
                    <i class="fa-solid fa-angle-left"></i>
                </button>
             
                <button class="w-10 h-10 rounded-lg bg-secondary text-white flex items-center justify-center shadow-glow">1</button>
                <button class="w-10 h-10 rounded-lg bg-white border border-gray/20 flex items-center justify-center text-gray hover:border-secondary hover:text-secondary transition-all">2</button>
                <button class="w-10 h-10 rounded-lg bg-white border border-gray/20 flex items-center justify-center text-gray hover:border-secondary hover:text-secondary transition-all">3</button>
             
                <button id="next-page-btn" class="w-10 h-10 rounded-lg bg-white border border-gray/20 flex items-center justify-center text-gray hover:border-secondary hover:text-secondary transition-all">
                    <i class="fa-solid fa-angle-right"></i>
                </button>
            </div>
        </div> -->
 
  </div>
</template>

<script>
  import {course} from "@/api";
  import pageComponent from '@/views/common/pageComponent'

  export default {
    name: 'courseListContent',
    data() {
      return {
        courseList:[],
        current:1,
        size:8,
        sellType:"",
        level:"",
        subjectId:"",
        subject1Id:"",
        pageList:[],
        pages:0,
        page:{},
        levelFlag:false,
        orderBy:"default",
      }
    },
    components: {
      pageComponent
    },
    methods: {
      handler (component) {
        console.log('this component is showing')
      },
      goto(i){
        this.current = i
        this.selectCouseList();
      },
      toOrderBy(orderBy1){//排序
          this.orderBy = orderBy1;
          this.selectCouseList();
      },
      selectCouseList(){
        let subjectId = "";
        let level = "";
        let sellType = "";
        let courseName = this.$route.query.searchCourseName;
        let typeActive = this.$route.query.courseType;
        let subLevel1Active = this.$route.query.subLevel1;
        let subLevel2Active = this.$route.query.subLevel2;
        if (typeof(subLevel1Active) == "undefined"){
          subLevel1Active = 0
        }
        if (typeof(subLevel2Active) == "undefined"){
          subLevel2Active = 0
        }

        if(typeActive != 0){
          sellType = typeActive
        }
        if(subLevel2Active == 0&&subLevel1Active == 0){
          level = "";
          subjectId = "";
        }else if(subLevel2Active == 0&&subLevel1Active != 0){
          level = "1";
          subjectId = subLevel1Active;
        }else{
          level = "2";
          subjectId = subLevel2Active;
        }
        course(this.current,this.size,sellType,level,subjectId,courseName,'',this.orderBy).then(res =>{
          this.courseList = res.data.data.records;
          this.page = res.data.data
          console.log(this.page);
          this.pages = res.data.data.pages
          console.log(this.pages);
          this.computePage(res.data.data.pages)
        })
      },
      dateFormat(fmt, date){
        let ret;
        let getDate = new Date(date);
        const opt = {
          "Y+": getDate.getFullYear().toString(),        // 年
          "m+": (getDate.getMonth() + 1).toString(),     // 月
          "d+": getDate.getDate().toString(),            // 日
          "H+": getDate.getHours().toString(),           // 时
          "M+": getDate.getMinutes().toString(),         // 分
          "S+": getDate.getSeconds().toString()          // 秒
          // 有其他格式化字符需求可以继续添加，必须转化成字符串
        };
        for (let k in opt) {
          ret = new RegExp("(" + k + ")").exec(fmt);
          if (ret) {
            fmt = fmt.replace(ret[1], (ret[1].length == 1) ? (opt[k]) : (opt[k].padStart(ret[1].length, "0")))
          }
        }
        return fmt;
      },
      dateCompareToDate(date){
        let getDate = new Date(date);
        return getDate;
      },
      computePage(pages){
        this.pageList = []
        if(pages<=7){
          for (let i = 1; i <= pages; i++) {
            this.pageList.push(i)
          }
        }else{
          if(this.current <= 4){
            this.pageList = [1,2,3,4,5,6,7];
          }else if(this.current+3<=pages) {
            let n = this.current
            this.pageList = [n-3,n-2,n-1,n,n+1,n+2,n+3];
          }else{
            this.pageList = [pages-6,pages-5,pages-4,pages-3,pages-2,pages-1,pages];
          }
        }
      }
    },
    mounted:function() {
      this.selectCouseList();
    },
  }
</script>

<style scoped>
 
</style>
