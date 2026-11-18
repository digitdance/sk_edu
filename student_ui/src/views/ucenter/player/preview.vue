<template>
  <div style="height: 100%;background: #1c1f21;">
   
    <player-content v-if="courseKpoint.videoType == 'aliyunvod'||courseKpoint.videoType == 'aliyunlive'||courseKpoint.videoType == 'aliyunReplay'" :courseKpoint="courseKpoint" :courseInfo="courseInfo" :materialInfo="materialInfo" :times="times"></player-content>
    <baijiayun-player-content v-if="courseKpoint.videoType == 'baijiayunvod'" :courseKpoint="courseKpoint" :courseInfo="courseInfo" :materialInfo="materialInfo" :times="times"></baijiayun-player-content>
    <local-player-content-preview  :courseKpoint="courseKpoint" :courseInfo="courseInfo" :materialInfo="materialInfo" :times="times"></local-player-content-preview>
    <polyv-player-content v-if="courseKpoint.videoType == 'polyvvod'||courseKpoint.videoType == 'polyvReplay'" :courseKpoint="courseKpoint" :courseInfo="courseInfo" :times="times"></polyv-player-content>
    <player-ifream v-if="courseKpoint.videoType == 'othervod'||courseKpoint.videoType == 'polyvlive'||courseKpoint.videoType == 'baijiayunlive'||courseKpoint.videoType == 'baijiayunReplay'
||courseKpoint.videoType == 'otherlive'||courseKpoint.videoType == 'iframelive'"  :courseKpoint="courseKpoint" :courseInfo="courseInfo" :times="times"></player-ifream>
  </div>
</template>

<script>
const playerHeader = ()=>import("@/views/ucenter/player/playerHeader.vue");
const playerContent = ()=>import("@/views/ucenter/player/playerContent.vue");
const baijiayunPlayerContent = ()=>import("@/views/ucenter/player/baijiayunPlayerContent.vue");
const localPlayerContentPreview = ()=>import("@/views/ucenter/player/localPlayerContentPreview.vue");
const polyvPlayerContent = ()=>import("@/views/ucenter/player/polyvPlayerContent.vue");
const playerIfream = ()=>import("@/views/ucenter/player/playerIfream.vue");
import {courseInfo , materialInfo , kpointDetail} from "@/api";
// import {checkCourseV2,checkCourse,getTrxorderdetail} from "@/api/user";
import { mapGetters } from "vuex";

export default {
  name: 'player',
  oneminthInter:{},
  cookieTime:{},
  fiveminthInter:{},
  data () {
    return {
      id:"",
      times:0,
      courseIdsc:0,
      kpointIds:0,
      kpointCourseIds:0,
      courseTypes:0,
      courseKpoint:{},
      courseInfo:{},
      materialInfo:[]
    }
  },components:{
    playerHeader,playerContent,baijiayunPlayerContent,localPlayerContentPreview,playerIfream,polyvPlayerContent,
    // eslint-disable-next-line vue/no-unused-components
    'remote-css': {
      render(createElement) {
        return createElement('link', { attrs: { rel: 'stylesheet', href: this.href }});
      },
      props: {
        href: { type: String, required: true },
      },
    },
    // eslint-disable-next-line vue/no-unused-components
    'remote-js': {
      render(createElement) {
        return createElement('script', { attrs: { type: 'text/javascript', src: this.src }});
      },
      props: {
        src: { type: String, required: true },
      },
    },
  },
  methods: {
    init(){
      //判断是否登录如果没有登录则去登录
      
    
      let id = this.$route.params.id;
      let taocanId = this.$route.query.taocanId;
      let courseType=1;
      let courseIds=0;
      let kpointCourseId=0;
      this.id = id;
      let that = this
      if(taocanId!=undefined && taocanId!='' &&taocanId!=0){
          courseType=2;
        kpointCourseId=taocanId;
      }else {
        kpointCourseId=0;
      }
      kpointDetail(id,kpointCourseId,"preview").then(res =>{
        let courseKpoint = res.data.data;
        courseIds= courseKpoint.courseId;
          that.courseIdsc=courseIds,
            that.kpointIds=id,
            that.kpointCourseIds=kpointCourseId,
            that.courseTypes=courseType,

            
        courseInfo(courseKpoint.courseId,taocanId).then(res =>{
          let courseInfo = res.data.data;
          //判断用户是否拥有此课程  如果没有登录则不验证
          that.courseInfo = courseInfo
          that.courseKpoint = courseKpoint
          let param = {
            courseId:courseInfo.id,
          }
          //判断如果为班级打开的详情页面则验证班级id
          if(typeof(taocanId) != "undefined"){
            param.courseId = taocanId
          }

          that.courseInfo = courseInfo
          that.courseKpoint = courseKpoint

          
        })
        //查询课程资料
      materialInfo(id).then(res=>{
        this.materialInfo = res.data.data
      })
      
      },error =>{
        this.$layer.alert(
          error.message,
          {
            shade: true,
            title: "提示"
          },
          laeryid => {
            this.$layer.close(laeryid);
          }
        );
        console.log(error.message)
      })
    }
  },
    mounted:function() {
      // 通过$once来监听定时器，在beforeDestroy钩子可以被清除。
      // this.$once('hook:beforeDestroy', () => {
      //   clearInterval(this.oneminthInter)
      //   clearInterval(this.fiveminthInter)
      // })
      this.init()
     // window.addEventListener('beforeunload',this.beforeUnloadHandler,true);
    },
  computed: {
    ...mapGetters(["studentInfo","isLogin"]),
  },
}
</script>

<style scoped>
</style>
