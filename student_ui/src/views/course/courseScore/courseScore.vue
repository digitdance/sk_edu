<template>
  <div>
    <div class="main">
      <div class="container">
        <el-table
              :data="scoreList"
            >
              <el-table-column prop="title" label="标题"></el-table-column>
              <el-table-column
                prop="score"
                label="得分"
                min-width="25px"
              >
              </el-table-column>
              <el-table-column
                prop="createTime"
                label="打分日期"
                :formatter="formatDate"
                min-width="25px"
              >
              </el-table-column>
            </el-table>
        <div class="clear"></div>
      </div>
    </div>
 
  </div>

</template>

<script>
import commonHeader from '@/views/common/header'
import commonFooter from '@/views/common/footer'
import {exampaperRecordById} from "@/api";
import {mapGetters} from "vuex";

import {courseScoreList} from '@/api'




export default {
  name: 'courseList',
  data() {
    return {
      headNav: "/exam/examIndex",
      exSuspendShow: true,
      examList:[],
      scoreList:[],
      examInfo:{
        paperName:' ',
        testTime:0,
        correctNum:0,
        qstCount:1
      }
    }
  }, 
  components: {
     commonFooter
  },
  props:{
      clazzId:Number,
      courseId:Number
  },
  methods: {
    init(){

      var scoreList = []

      courseScoreList({
        courseId:this.courseId,
        clazzId:this.studentInfo.clazzId
      }).then(res=>{

        console.log("lllist",res.data.data)
        // scoreList = res.data
        this.scoreList = res.data.data
      })

      // //判断是否登录如果没有登录则去登录
      // if(!this.isLogin){
      //   this.$router.push({ path: '/user/login' })
      //   return;
      // }
      // let id = this.$route.query.id;
      // exampaperRecordById(id).then(res=>{
      //   this.examInfo = res.data.data
      // })
    },
    getTime(s){
      //计算分钟
      //算法：将秒数除以60，然后下舍入，既得到分钟数
      var h;
      h  =   Math.floor(s/60);
      //计算秒
      //算法：取得秒%60的余数，既得到秒数
      s  =   s%60;
      //将变量转换为字符串
      h    +=    '';
      s    +=    '';
      //如果只有一位数，前面增加一个0
      h  =   (h.length==1)?'0'+h:h;
      s  =   (s.length==1)?'0'+s:s;
      return h+'分'+s+"秒";
    },
    toErrorList(){
        this.$router.push({ path: "/uc/examRecord", query:{active:2}});
    },
    toIndex(){
      this.$router.push({ path: "/exam/examIndex" });
    },
    toAnswer(){
      this.$router.push({ path: "/exam/examAnswer",query:{id:this.examInfo.epId} });
    }
  },
  mounted: function () {
    this.init()
  },
  computed: {
    ...mapGetters(["studentInfo","isLogin"]),
  },
}
</script>

<style scoped>

</style>
