<template>
  <div class="container">
    <div class="course-tool-bar clearfix">
      <div class="tool-left l">
        <a href="javascript:void(0)" @click="toOrderBy('default')" :class="orderBy=='default' ? 'active' : ''" class="moco-change-smalle-btn">默认</a>
        <a href="javascript:void(0)" @click="toOrderBy('update')" :class="orderBy=='update' ? 'active' : ''" class="moco-change-smalle-btn">最新</a>
      </div>
    </div>
    <div class="course-list">
      <div class="mt20">
        <div class="clearfix">
          <div class="list_box" v-for="(exam,index) in examList" :key="index">
            <div class=" list_box2" >
              <span class="l_b2_font" v-for="paperType in paperTypeList" :key="paperType.id">{{exam.type == paperType.id?paperType.title:''}}</span>
              <p class="li_title">{{exam.middleName}}</p>
              <!-- <p class="li_title">{{exam.name}}</p> -->
              <div class="l_box">
                <span class="l_box_w">题数：{{exam.qstCount}}题</span>
              </div>
              <div class="l_box"> 
                <span class="l_box_w" >{{exam.clazzName}}</span>
              </div>
              <div class="l_box" v-for="(subject,index) in examSubjectList" :key="index">
                <span class="l_box_w txtOf" style="width: 110%" v-if="exam.subjectId == subject.id">分类：{{subject.subjectName}}</span>
              </div>
              <div class="l_box">
                <span class="l_box_w">分数：{{exam.score}}分</span>
              </div>
              <div class="l_box">
                <span class="l_box_w">限时：{{exam.replyTime}}分钟</span>
              </div>
              <div class="l_time1">
                随时可参加考试
              </div>
              <div class="l_btn_comm l_btn1" @click="goExam(exam)">
                参加考试
              </div>
            </div>
          </div>
          <div class="clear"></div>
        </div>

      </div>
      <page-component :page="page" @goto="goto" v-show="examList.length > 0" ></page-component>
      <div class="nodata" v-if="examList.length == 0">
        <p><i class="imv2-error_c"></i></p>
        <p>暂无数据</p>
      </div>
    </div>

  </div>
</template>

<script>
  import {exam,clazzExamPage,getExamSubjectList} from "@/api";
  import pageComponent from '@/views/common/pageComponent'
  import { mapGetters } from "vuex";

  export default {
    name: 'examListContent',
    data() {
      return {
        examList:[],
        examSubjectList:[],
        current:1,
        size:8,
        sellType:"",
        level:"",
        subjectId:null,
        subject1Id:"",
        pageList:[],
        pages:0,
        page: {
        pageSize: 10,
        currentPage: 1,
        total: 0,
      },
      query:{},
        levelFlag:false,
        orderBy:"default",
      }
    },
    components: {
      pageComponent
    },
    methods: {
      goExam(exam){
        if(!this.isLogin){
          this.$router.push({ path: '/user/login' })
          return
        }
        let routeData = this.$router.resolve({ path: "/exam/examInfo",query:{id:exam.id,middleId:exam.middleId} });
        window.location.href=routeData.href
      },
      goto(i){
        this.current = i
        this.selectExamList();
      },
      toOrderBy(orderBy1){//排序
          this.orderBy = orderBy1;
          this.selectExamList();
      },
      selectExamList(page, params = {}) {
        this.subjectId=this.$route.query.subjectId;
         console.log("🚀 ~ file: examListContent.vue ~ line 101 ~ selectExamList ~ this.subjectId", this.subjectId)
         this.query.subjectId = this.subjectId;
         this.query.examType = this.$route.query.examType;
         
        clazzExamPage(
          page.currentPage,
          page.pageSize,
          Object.assign(params, this.query)
        ).then((res) => {
            this.examList = res.data.data.records;
            console.log("🚀 ~ file: examListContent.vue ~ line 107 ~ ).then ~ this.examList", this.examList)
            this.page = res.data.data
            this.pages = res.data.data.pages
            this.computePage(res.data.data.pages)
          })
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
      },
      getExamSubject(){
        getExamSubjectList().then(res =>{
          this.examSubjectList = res.data;
          console.log(this.examSubjectList);
        })
      },
    },

    mounted:function() {
      this.selectExamList(this.page);
      this.getExamSubject();
    },
    computed: {
      ...mapGetters(["website","navigation","userInfo","studentInfo","isLogin"]),
    },
    props:{
      paperTypeList: Object,
      examType:Number
    },
    watch: {
      paperTypeList: {
        deep: true,
        handler(newVal){
          console.log(newVal)
        }
      },
      examType: {
        deep: true,
        immediate:true,
        handler(newVal){
          console.log(newVal)
        }
      },
    },
  }
</script>

<style scoped>

</style>
