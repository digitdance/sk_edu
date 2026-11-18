<template>
  <div class="articleleft bg-fff">
      <div class="articleleft_tab">
        <div class="articleleft_title">
          班级通知
        </div>
        <!-- 
          <div class="articleleft_tablist">
            <div class="" v-bind:class="{'active' :0 == activeIndex}"><a href="javascript:void(0)" @click="clickSubject('')">全部</a></div>
            <div class="" v-bind:class="{'active' :subject.id == activeIndex}" v-for="(subject) in subjectList" :key="subject.id">
              <a href="javascript:void(0)" @click="clickSubject(subject.id)">{{subject.subjectName}}</a>
            </div>
          </div> 
        -->
      </div>
      <div class="tab-content">
        <div class=article_card v-for="(article) in msgList" :key="article.id">

      
          <div class="article_card_right">
            <div class="article_card_right_title">
              {{article.title}}
            </div>
            <div class="article_card_right_describe" style="font-size: 14px;">
              日期：{{article.createTime}}
            </div>
            <div class="article_card_right_describe" >
              {{article.summary}}
            </div>
          </div>
         
        </div>
        <page-component :page="page" @goto="goto" v-show="msgList.length != 0"></page-component>
        <div class="nodata" v-if="msgList.length == 0">
          <p><i class="imv2-error_c"></i></p>
          <p>暂无数据</p>
        </div>
      </div>
  </div>
</template>
<script>
 import {articleList, cmsSubject,msgReceiveUpdateState,msgreceiveList} from "@/api";
// import {msgreceiveList,msgReceiveUpdateState,removeMsgreceive} from "@/api";
 import { setStore, getStore } from '@/util/store'
 import pageComponent from '@/views/common/pageComponent'
 import { mapGetters } from "vuex";
 export default {
    name: 'articleSubjectLeft',
    data() {
      return {
        articleList:[
          {
            title:"开学通知",
            createTime:"2026-01-01",
            summary:"2026学年开学通知"
          }
        ],
        msgList:[
        {
            title:"开学通知",
            createTime:"2026-01-01",
            summary:"2026学年开学通知"
          }
        ],
        subjectId:0,
        subjectList:getStore({ name: 'subjectList' }) || [],
        activeIndex:'',
        page:{},
      }
    },
    components: {
      pageComponent
    },
    mounted:function() {
      // this.init();
      // let subjectId = this.$route.query.subjectId;

      // if(typeof(subjectId) == "undefined"){
      //   subjectId = ""
      // }
      // this.clickSubject(subjectId)
      this.getMsgList()
    },
    computed: {
      ...mapGetters(["studentInfo"]),
    },
    methods: {
      getMsgList(current,size){
        if (typeof(this.studentInfo.id) == "undefined"){
          return;
        }
        //更新站内信状态
        msgReceiveUpdateState(this.studentInfo.id).then(res =>{
          this.$store.dispatch("updateLetterCount", this.studentInfo)
        })
        let params = {
          "current":current,
          "size":size,
          "receivingCusid":this.studentInfo.id
        }
        msgreceiveList(params).then(res =>{
          this.page = res.data.data
          this.msgList = res.data.data.records

        })
      },
      init(){
        let subjectId = "";
        if(this.subjectList.length == 0){
          cmsSubject().then(res => {
            this.subjectList = res.data.data;
            setStore({ name: 'subjectList', content: res.data.data,type: 'session' })
          });
        }
        if(this.subjectList.length >0){
          subjectId = this.subjectList[0].id
          this.$emit('subjectData',"")
        }
      },
      clickSubject(subjectId){
        this.activeIndex = subjectId
        this.subjectId = subjectId;
        this.getArticleList(1,12);
      },
      getArticleList(current,size){
        articleList(current,size,this.subjectId).then(res =>{
          this.page = res.data.data
          //this.articleList = res.data.data.records
        })
      },
      goto(i){
        this.getArticleList(i,12);
      },
    },
  }
</script>

<style scoped>
body, html {
  background-color: #fff;
}
.article_card{  width: 100%;  padding: 15px;  display: -webkit-flex;  display: block!important;  -webkit-justify-content: space-around;  justify-content: space-around;  margin-top: 10px;}


.article_card_right{height: 95px;width: 100%;display: -webkit-flex;display: flex;-webkit-flex-direction: column;flex-direction: column;}
.article_card_right_title{cursor: pointer;margin-top: 3px;font-size: 20px;height: 28px;-webkit-flex-shrink: 0;flex-shrink: 0;line-height: 28px;font-weight: 600;color: #2a2a2a;margin-bottom: 16px;overflow: hidden;white-space: nowrap;text-overflow: ellipsis;}
.article_card_right_describe{width: 534px;height: 60px;-webkit-flex-grow: 1;flex-grow: 1;font-weight: 400;color: #707478;font-size: 16px;overflow: hidden;text-overflow: ellipsis;display: -webkit-box;-webkit-box-orient: vertical;-webkit-line-clamp: 4;}
.article_card_right_bottom{webkit-flex-shrink: 0;flex-shrink: 0;}
.article_card_right_type{font-weight: 500;color: #93999f;font-size: 12px;}
.article_card_right_date{font-size: 12px;font-weight: 400;color: #93999f;margin-left: 30px;}
</style>
