<template>
    <div v-wechat-title="title">
      <common-header :headNav="headNav"></common-header>
        <!-- AI助教横幅 -->
    <section class="bg-gradient text-white py-12">
        <div class="max-w-7xl mx-auto px-4">
            <div class="text-center">
                <h1 class="text-[clamp(1.75rem,4vw,2.5rem)] font-bold mb-4">智能备课助手</h1>
                <p class="text-white/80 text-lg max-w-2xl mx-auto">
                  以课程大纲为底座，AI 辅助生成教学内容与设计参考，做教师身边的 AI 备课伙伴。
                </p>
            </div>
        </div>
    </section>
    
    <!-- AI助教主区域 -->
    <section class="section" v-if="userInfo">
        <iframe :src="iframeUrl" style="width: 100%;height: 660px;"></iframe>
    </section>
    <section class="section" v-else>
            
            <router-link :to="{path:'/user/login'}" ><a href="javascript:void(0)" id="js-signin-btn">请先点登录</a></router-link>
    </section>
      <common-footer></common-footer>
    </div>
  </template>
  
  <script>
  import { mapGetters } from "vuex";
  import {courseInfo,courseInfoSimple} from "@/api";
  import commonHeader from '@/views/common/header'
  import commonFooter from '@/views/common/footer'
  import courseInfoheader from '@/views/course/courseInfo/courseInfoheader'
  import courseInfoContent from '@/views/course/courseInfo/courseInfoContent'
  import courseInfoTaocanContent from '@/views/course/courseInfo/courseInfoTaocanContent'
  import Store from "../../store";
  export default {
    name: 'courseList',
    data () {
      return {
        title:"",
        iframeUrl:"",
        courseInfo:{},
        loading:false,
        headNav:"",
        headFlag:false
      }
    },components:{
      commonHeader,commonFooter,courseInfoheader,courseInfoContent,courseInfoTaocanContent
    },
    computed: {
    ...mapGetters(["website", "bottomLink", "friendLink","userInfo"]),
  },
    methods: {
      init(){
        let that = this
        let id = this.$route.params.id;
        this.loading=true
        //查询课程信息
        courseInfoSimple(id).then(res =>{
          that.courseInfo = res.data.data;
          //把课程名字动态赋值给title
          let title = "";
          if (Store.getters.website.title) {
            title = res.data.data.courseName+"-"+Store.getters.website.title;
          }
          this.title = title;
          that.headFlag = true
          if(that.courseInfo.sellType == "1"){
            that.headNav = "/course/courseList?courseType=1"
          }
          if(that.courseInfo.sellType == "2"){
            that.headNav = "/course/courseList?courseType=2"
          }
          if(that.courseInfo.sellType == "3"){
            that.headNav = "/course/courseList?courseType=3"
          }
          this.loading=false
        })
      }
    },
    created(){
      
    },
    mounted:function() {
        if(this.userInfo){
            this.iframeUrl = 'http://cqedu.digitdance.cn/login?examId=2089526983247728641&inAccount=student&inPassword=123456&direct=2&content='+this.$route.query.content
        }
        this.init()
    },
  }
  </script>
  
  <style scoped>
  
  </style>
  