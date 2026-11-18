<template>
  <div v-wechat-title="title">
    <!-- <common-header :headNav="headNav"></common-header> -->
    <div class="main">
      <course-infoheader v-if="headFlag" :courseInfo="courseInfo"></course-infoheader>
      <course-info-content-preview v-loading="loading" :courseInfo="courseInfo"></course-info-content-preview>
     
      <div class="clear"></div>
    </div>
  
  </div>
</template>

<script>

import {courseInfo,courseInfoSimple} from "@/api";
import commonHeader from '@/views/common/header'
import commonFooter from '@/views/common/footer'
import courseInfoheader from '@/views/course/courseInfo/courseInfoheader'
import courseInfoContent from '@/views/course/courseInfo/courseInfoContent'
import courseInfoContentPreview from '@/views/course/courseInfo/courseInfoContentPreview'
import courseInfoTaocanContent from '@/views/course/courseInfo/courseInfoTaocanContent'
import Store from "../../../store";
export default {
  name: 'courseList',
  data () {
    return {
      title:"",
      courseInfo:{},
      loading:false,
      headNav:"",
      headFlag:false
    }
  },components:{
    commonHeader,commonFooter,courseInfoheader,courseInfoContent,courseInfoTaocanContent,courseInfoContentPreview
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
  mounted:function() {
    this.init()
  },
}
</script>

<style scoped>

</style>
