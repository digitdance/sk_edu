<template>
  <div class="u-container">
    <div class="setting-right-wrap wrap-boxes settings">

      <div class="formBox">
        <div id="setting-profile" class="setting-wrap setting-profile">
          <div class="common-title">
            留言评论
          </div>
          <div class="line"></div>
          <div class="">
            <div class="u-r-all-box">
              <div class="u-sys-news" v-if="msgList.length > 0">
                <ul class="u-sys-list">

                  <li id="del528" v-for="msg in msgList" :key="msg.id">
                    <div>
                      <section class="u-s-l-txt">
                        <span class="fsize13" style="font-weight: 700">{{ msg.content }}</span>
                        <span class="fsize14 c-999 f-fM ml20" v-html="msg.addtime"></span>
<!--                        <em class="wd-ico">-->
<!--                          <img src="https://wx.inxedu.com/static/inxweb/img/wd-bg-u.png">-->
<!--                        </em>-->
                         <span href="javascript:void (0)" class="exam_label" @click="remove(msg.id)"    title="删除">
                         {{ msg.course.courseName }}
                        </span> 
                      </section>
                      <!-- <section class="clearfix hLh30">
                        <span class="fl">
                          <tt class="fsize14 c-999 f-fM" v-html="msg.content"></tt>
                        </span>
                      </section> -->
                      <section class="clearfix ">
                        <div class="fl">
                         回复:<span class="fsize13 c-999 f-fM" v-for="reply in msg.replys" v-html="reply.content"></span>
                        </div>
                      </section>
                    </div>
                  </li>
                </ul>
              </div>
              <page-component :page="page" @goto="goto" v-show="msgList.length > 0"></page-component>
              <div class="nodata" v-if="msgList.length == 0">
                <p><i class="imv2-error_c"></i></p>
                <p>暂无数据</p>
              </div>
            </div>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script>
  import {commentPage,removeMsgreceive} from "@/api";
  import pageComponent from '@/views/common/pageComponent'
  import { mapGetters } from "vuex";


  export default {
    name: 'myCourse',
    data() {
      return {
        isReg: false,
        name: '',
        password: '',
        repeat: '',
        msgList:[],
        current:1,
        size:20,
        page:{},

      }
    },mounted:function() {
      this.getMsgList(this.current,this.size);
    },
    components: {
      pageComponent
    },
    methods: {
      goto(i){
        this.current = i
        this.getMsgList(i,this.size);
      },
      getMsgList(current,size){
        if (typeof(this.studentInfo.id) == "undefined"){
          return;
        }
        //更新站内信状态
        // msgReceiveUpdateState(this.studentInfo.id).then(res =>{
        //   this.$store.dispatch("updateLetterCount", this.studentInfo)
        // })
        let params = {
          "current":current,
          "size":size,
          type:2
        }
        commentPage(params).then(res =>{
          this.page = res.data.data
          this.msgList = res.data.data.records

        })
      },
    },
    computed: {
      ...mapGetters(["studentInfo"]),
    },
    watch: {
      studentInfo: function (val, oldVal) {
        this.getMsgList(this.current,this.size);
      }
    },

  }
</script>

<style scoped>

.exam_label {
  width: 75px;
  height: 21px;
  background: #0091ff;
  border-radius: 10px;
  text-align: center;
  line-height: 21px;
  font-size: 14px;
  color: #fff;
  font-weight: 500;
}
</style>
