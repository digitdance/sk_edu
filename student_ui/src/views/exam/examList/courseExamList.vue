<template>
  <div>
    <!-- <common-header :headNav="headNav"></common-header> -->

    <div class="main">
       <select-exam-subject @changeType="changeType" :paperTypeList="paperTypeList"></select-exam-subject>
       <exam-list-content :examType="examType" :courseId="courseId" :paperTypeList="paperTypeList"></exam-list-content>
       <div class="clear"></div>
    </div>

    <!-- <common-footer></common-footer> -->
  </div>
</template>

<script>
import commonHeader from '@/views/common/header'
import commonFooter from '@/views/common/footer'
import selectExamSubject from '@/views/exam/examList/selectExamSubject'
import examListContent from '@/views/exam/examList/courseExamListContent'
import {paperTypeList} from "@/api";
export default {
  name: 'courseExamList',
  data () {
    return {
      examType:"",
      headNav: "/exam/examIndex",
      paperTypeList:[]
    }
  },
  components:{
    // commonHeader, commonFooter, selectExamSubject,
    examListContent,selectExamSubject
  },
  methods: {
    changeType(e){
      this.examType = e
    }
  },
  props:{
      courseId:Number
    },
  mounted:function() {
    // let examType = this.$route.query.examType;
    // if(examType == 0){
    //   // this.headNav = "/exam/examList?examType=1";
    // }
    // if(examType == 1){
    //   // this.headNav = "/exam/examList?examType=1";
    // }
    // if(examType==2){
    //   // this.headNav = "/exam/examList?examType=2";
    // }
    // if(examType==3){
    //   // this.headNav = "/exam/examList?examType=3";
    // }
    //获取考试类型
    paperTypeList().then(res =>{
      this.paperTypeList = res.data.data
    })
  },
}
</script>

<style scoped>

</style>
