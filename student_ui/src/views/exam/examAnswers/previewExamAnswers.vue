<template>
  <div>
    <div class="main">
      <div class="container">
        <preview-exam-answer-content
          ref="content"
          :examInfo="examInfo"
        ></preview-exam-answer-content>
       <!-- <exam-answer-right :examInfo="examInfo" @go="goto"></exam-answer-right>  -->
        <div class="clear"></div>
      </div>
    </div>

  </div>
</template>

<script>

import previewExamAnswerContent from "@/views/exam/examAnswers/previewExamAnswerContent";
import examAnswerRight from "@/views/exam/examAnswers/examAnswerRight";
import { examDetail,  ws } from "@/api";


export default {
  name: "courseList",
  data() {
    return {
      headNav: "exam/examIndex",
      exSuspendShow: true,
      examInfo: {},
      favList: [],
    };
  },
  components: {
   
    previewExamAnswerContent,
    examAnswerRight,
  },
  methods: {
    responseCallback(frame) {
      console.log("收到消息：" + frame.body);

      //eventBus.$emit("get-progress", frame.body);
      //this.$store.commit('setDeviceConfig', JSON.parse(frame.body))
    },
    connect(currentUser) {
      var headers = {};
    
    },
    onConnected(frame) {
      console.log("连接ws成功: " + frame);
      // var userInfo = this.getStore("userInfo");
      // var currentUser = JSON.parse(userInfo);
      // var topicSubscription = client.subscribe(
      //   "/topic/subscribe/import/progress/" + currentUser.id,
      //   this.responseCallback
      // );
    },
    onFailed(e) {
      console.log("onFailed", e);
    },
    goto(i) {
      this.$refs.content.go(i);
    },
    init() {
      // let timer = setInterval(() => {
      //   console.log("开始自动刷新(每5秒获取数据)...", new Date());
      // }, 5000);

     // console.log("timer1:", timer);

      // 监听浏览器切换事件
      // document.addEventListener("visibilitychange", () => {
      //   // 离开当前tab标签(不管是切到其它tab，还是最小化)
      //   if (document.visibilityState === "hidden") {
      //     //alert("离开当前页面将会判为作弊，请慎重！");
      //     eventBus.$emit("forceSubmitExam");
      //     console.log("离开当前tab:停止自动刷新,clear timer:", timer);
      //     clearInterval(timer);
      //     timer = -1;
      //   } else {
      //     // 回到当前tab标签
      //     console.log("回到当前tab:");
      //     //延时2秒后，恢复自动刷新
      //     setTimeout(() => {
      //       //判断上1次的timer是否已被清除
      //       if (timer < 0) {
      //         timer = setInterval(() => {
      //           console.log("恢复自动刷新(每5秒获取数据)...", new Date());
      //         }, 5000);
      //         console.log("timer2:", timer);
      //       }
      //     }, 2000);
      //   }
      // });
      console.log("gggggggggggggggg")
      //判断是否登录如果没有登录则去登录
    
      let that = this;
      console.log("hhhhhhhhhhhh")
      let id = this.$route.query.id;
      examDetail(id).then((res) => {
        let examInfo = res.data.data;
       // let param = {};
       // favoritesqstListAll(param).then((res) => {
         // let favArr = res.data.data;
          let num = 1;
          examInfo.list.forEach((j) => {
            j.questionArr.forEach((e) => {
              e.userFavor = false;
              e.signFlag = false;
              e.num = num;
              num++;
              if (e.qstType == 1 || e.qstType == 2) {
                e.optionList = JSON.parse(e.optionList);
              }
              //判断如果是填空题添加填空数组
              if (e.qstType == 4) {
                e.answerArr = [];
              }
              //处理收藏
              // for (let i = 0; i < favArr.length; i++) {
              //   if (e.qstId == favArr[i].qstId) {
              //     e.userFavor = true;
              //     break;
              //   }
              // }
            });

            console.log("hhhhhhhhhhhh")

          });
          console.log("hhhhhhhhhhhh111")
          that.examInfo = examInfo;
          console.log("123",that.examInfo);
       // });
      });
    },
  },
  mounted: function () {
    this.init();
    //this.connect(this.studentInfo);
  },
  computed: {
  //  ...mapGetters(["studentInfo", "isLogin", "userInfo"]),
  },
};
</script>

<style scoped>
</style>
