<template>
  <div class="u-container">
    <div class="right-container l">

      <div class="flex-1">
                <!-- 选项卡模块 -->
                <div class="bg-white rounded-2xl shadow-soft p-4 mb-8">
                    <div class="flex border-b border-gray/20">
                        <button id="tab-data-preview" v-bind:class="{'text-secondary' :'1' == states}" @click="states='1'" class="px-4 py-3 font-medium border-b-2 border-secondary">数据预览</button>
                        <button id="tab-score" v-bind:class="{'text-secondary' :'2' == states}" @click="states='2'" class="px-4 py-3 font-medium hover:text-secondary transition-colors text-gray">综合得分</button>
                    </div>
                </div>
                <!-- 数据预览区域 -->
                <div id="data-preview" v-if="states==1" class="bg-white rounded-2xl shadow-soft p-4 mb-6" style="display: block;">
                    <!-- 数据预览统计 - 由后端根据用户学习记录动态生成 -->
                    <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <div class="flex items-center justify-between bg-gradient rounded-xl p-4 transition-all hover:shadow-hover">
                            <div>
                                <p class="text-sm text-white/90 mb-1">完成学习</p>
                                <p id="completed-studies" class="text-3xl font-bold text-white">{{ countList.count1 }}</p>
                            </div>
                            <div class="w-12 h-12 rounded-full bg-white/20 flex items-center justify-center">
                                <i class="fa-solid fa-book-open text-white"></i>
                            </div>
                        </div>
                        <div class="flex items-center justify-between bg-gradient rounded-xl p-4 transition-all hover:shadow-hover">
                            <div>
                                <p class="text-sm text-white/90 mb-1">完成答题</p>
                                <p id="completed-questions" class="text-3xl font-bold text-white">{{ countList.count2 }}</p>
                            </div>
                            <div class="w-12 h-12 rounded-full bg-white/20 flex items-center justify-center">
                                <i class="fa-solid fa-pen-to-square text-white"></i>
                            </div>
                        </div>
                    </div>
                </div>
                
                <!-- 学习进度模块 -->
                <div v-if="states==1"  id="progress-modules" class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6" style="display: grid;">
                    <!-- 学习完成进度 - 由后端根据用户学习记录动态生成 -->
                    <div class="bg-white rounded-2xl shadow-soft p-4 card-hover">
                        <div class="flex justify-between items-center mb-4">
                            <h3 class="font-semibold">学习完成进度</h3>
                            <div class="flex items-center gap-2">
                                <span class="w-2 h-2 rounded-full bg-secondary"></span>
                                <span class="text-sm text-gray">已完成</span>
                            </div>
                        </div>
                        <div class="flex justify-center mb-4">
                          <el-progress type="circle" :stroke-width="30" :percentage="taskPer" :width="160"></el-progress>
                        </div>
                        <div class="flex justify-between text-center">
                            <div>
                                <p id="completed-study-count" class="text-2xl font-bold">{{ taskComplete }}</p>
                                <p class="text-sm text-gray">完成学习</p>
                            </div>
                            <div>
                                <p id="total-study-count" class="text-2xl font-bold">{{ kpointNum }}</p>
                                <p class="text-sm text-gray">学习总数</p>
                            </div>
                        </div>
                    </div>
                    
                    <!-- 考试完成进度 - 由后端根据用户考试记录动态生成 -->
                    <div class="bg-white rounded-2xl shadow-soft p-4 card-hover">
                        <div class="flex justify-between items-center mb-4">
                            <h3 class="font-semibold">考试完成进度</h3>
                            <div class="flex items-center gap-2">
                                <span class="w-2 h-2 rounded-full bg-secondary"></span>
                                <span class="text-sm text-gray">已完成</span>
                            </div>
                        </div>
                        <div class="flex justify-center mb-4">
                          <el-progress type="circle" :stroke-width="30" :percentage="examPer" :width="160"></el-progress>
                        </div>
                        <div class="flex justify-between text-center">
                            <div>
                                <p id="completed-exam-count" class="text-2xl font-bold">{{ examPaperCompleteCount }}</p>
                                <p class="text-sm text-gray">完成考试</p>
                            </div>
                            <div>
                                <p id="total-exam-count" class="text-2xl font-bold">{{ examPaperCount }}</p>
                                <p class="text-sm text-gray">试卷总数</p>
                            </div>
                        </div>
                    </div>
                    
                    <!-- 学习画像 - 由后端根据用户学习行为数据动态生成 -->
                    <div class="bg-white rounded-2xl shadow-soft p-4 card-hover">
                        <div class="flex justify-between items-center mb-4">
                            <h3 class="font-semibold">学习画像</h3>
                        </div>
                        <div class="flex justify-center items-center h-48">
                          <div id="main_5" style="width: 300px;height: 300px;padding: 3px;"></div>
                        </div>
                    </div>
                </div>
                
                <!-- 综合成绩模块 -->
                <div v-if="states==2" id="score-module" class="bg-white rounded-2xl shadow-soft p-4 card-hover" >
                    <div class="bg-secondary text-white p-4 rounded-xl mb-4 flex items-center justify-between">
                        <div>
                            <h2 class="text-lg font-semibold">综合得分</h2>
                            <p class="text-sm text-white/90">基于课堂、考试和实训的综合评估</p>
                        </div>
                        <!-- 综合得分 - 由后端根据各分项得分加权计算生成 -->
                        <p id="comprehensive-score" class="text-4xl font-bold">0</p>
                    </div>

                    <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-4">
                        <div class="bg-secondary/5 rounded-xl p-4 text-center" v-for="score in scoreList" :key="score.id">
                            <p class="text-sm text-gray mb-1">{{score.title}}</p>
                            <!-- 综合课堂得分 - 由后端根据课堂表现、考勤、作业等计算生成 -->
                            <p id="class-score" class="text-2xl font-bold text-secondary">{{score.score}}</p>
                        </div>
                        
                    </div>

                    
                      
                    <!-- 动态评估状态提示 - 由后端根据用户成绩状态返回不同提示信息 -->
                    <!-- 可根据以下场景动态切换：
                         1. 成绩未完成评估："您的成绩正在评估中，请继续完成课程任务"
                         2. 成绩已更新："您的成绩已全部更新"
                         3. 无成绩记录："尚未开始学习，请进入课程广场选择课程"
                         4. 部分完成："部分成绩已完成评估，剩余内容将在完成后更新"
                    -->
                    <!-- <div id="evaluation-status" v-if="scoreList.length==0" class="text-sm text-gray text-center">
                        <p>您的成绩正在评估中，请继续完成课程任务</p>
                    </div> -->
                </div>
            </div>
    </div>

  </div>
</template>

<script>



  import { mapGetters } from "vuex";
  import {getUserAccount,getUserAccountHistory,createOrder,checkOrder,getwechatPay} from "@/api/user";
  import pageComponent from '@/views/common/pageComponent'
  import {myScoreList} from '@/api'
  import { getMyData } from "@/api";
  import echarts from "echarts";
  import "echarts-liquidfill";
  import ECharts from "vue-echarts";


  export default {
    name: 'myAccount',
    data() {
      return {
        shopNum:0,
      taskComplete: 0,
      states:"1",
      links:[],
      taskAll: 0,
      current:1,
      pageNotice: {
        pageSize: 3,
        currentPage: 1,
        total: 0,
      },
      taskPer: 0,
      examPaperCompleteCount:0,
      kpointNum:0,
      examPaperCount:0,
      examComplete: 0,
      msgList:[
        {
            title:"开学通知",
            createTime:"2026-01-01",
            summary:"2026学年开学通知"
          }
      ],
      examAll: 0,
      examPer: 0,
      countList: {},
      notices: [],
        userAccount:{},
        myScores:[],
       
        scoreList:[],
        userAccountHistory:[],
        moneyType:1,
        payType:0,
        money:"",
        itemMap:{
          t_exam:"考试综合",
          exam:"考试",
          kpoint_study:"课堂",
          shixun:"实训",
          t_shixun:"实训综合",
          t_kpoint_study:"课堂综合"
        },
        totalScore:{},
        payFlag:false,
        wechatPay:false,
        waitpay:false,
        order:{},
        orderCss:false,
        orderMsg:"请在支付页面继续完成支付",
        current:1,
        size:10,
        page:{},
        timeInterval:{},
        wechatPayUrl:"",

      }
    },
    created() {
    var that = this;
   
    this.$nextTick(() => {
      that.loadData();
      that.radar('main_5', 'AA', [[23, 43, 12, 32, 45]])
    });
   
  },
    methods: {
      initWater() {
      let _this = this;

      this.taskAll = this.countList.count5;
      this.taskComplete = this.countList.count1;

      this.examPaperCompleteCount = this.countList.count2;
      this.examPaperCount = this.countList.count4;
      this.kpointNum = this.countList.count3;

      if (this.countList.count1 == 0) {
        this.taskPer = 0;
      } else {
        this.taskPer = (
          (this.countList.count1 / this.countList.count3) *
          100
        ).toFixed(0);
      }

      this.examAll = this.countList.count6;
      this.examComplete = this.countList.count4;
      if (this.countList.count4 == 0) {
        this.examPer = 0;
      } else {
        this.examPer = (
          (this.countList.count5 / this.countList.count4) *
          100
        ).toFixed(0);
      }

      // var option = {
      //   title: {
      //     text: "实训学习完成率",
      //     left: "center",
      //   },
      //   tooltip: {
      //     trigger: "item",
      //   },
      //   legend: {
      //     orient: "vertical",
      //     left: "left",
      //   },
      //   color: ["#fc8251", "#5470c6", "yellow", "blue", "purple"],
      //   series: [
      //     {
      //       name: "",
      //       type: "pie",
      //       radius: "70%",
      //       data: [
      //         { value: Number(this.countList.count1), name: "已完成" },
      //         {
      //           value:
      //             Number(this.countList.count5) - Number(this.countList.count1),
      //           name: "未完成",
      //         },
      //       ],
      //       emphasis: {
      //         itemStyle: {
      //           shadowBlur: 10,
      //           shadowOffsetX: 0,
      //           shadowColor: "rgba(0, 0, 0, 0.5)",
      //           normal: {
      //             color: function (colors) {
      //               var colorList = [
      //                 "#fc8251",
      //                 "#5470c6",
      //                 "#91cd77",
      //                 "#ef6567",
      //                 "#f9c956",
      //                 "#75bedc",
      //               ];
      //               return colorList[colors.dataIndex];
      //             },
      //           },
      //         },
      //       },
      //     },
      //   ],
      // };

      // var option2 = {
      //   title: {
      //     text: "考试完成率",
      //     left: "center",
      //   },
      //   tooltip: {
      //     trigger: "item",
      //   },
      //   legend: {
      //     orient: "vertical",
      //     left: "left",
      //   },
      //   color: ["#91cd77", "#ef6567", "yellow", "blue", "purple"],
      //   series: [
      //     {
      //       name: "",
      //       type: "pie",
      //       radius: "70%",
      //       data: [
      //         { value: Number(this.countList.count7), name: "已完成" },
      //         {
      //           value:
      //             Number(this.countList.count6) - Number(this.countList.count7),
      //           name: "未完成",
      //         },
      //       ],
      //       emphasis: {
      //         itemStyle: {
      //           shadowBlur: 10,
      //           shadowOffsetX: 0,
      //           shadowColor: "rgba(0, 0, 0, 0.5)",
      //           normal: {
      //             color: function (colors) {
      //               var colorList = [
      //                 "#fc8251",
      //                 "#5470c6",
      //                 "#91cd77",
      //                 "#ef6567",
      //                 "#f9c956",
      //                 "#75bedc",
      //               ];
      //               return colorList[colors.dataIndex];
      //             },
      //           },
      //         },
      //       },
      //     },
      //   ],
      // };

      // let myChart1 = echarts.init(document.getElementById("main1"));
      // myChart1.setOption(option);

      // let myChart2 = echarts.init(document.getElementById("main2"));
      // myChart2.setOption(option2);

      this.$forceUpdate();
    },
    radar(main, nam, dat) {
      var echart = echarts.init(document.getElementById(main));
      var option = {
        grid: {
          containLabel: true,
          top: "10%",
          left: '3%',
          right: '3%',
          bottom: '3%'
        },
        title: {
          text: ''
        },
        tooltip: {
          trigger: 'item',
        },
        radar: {
          indicator: [
            { name: '认知', max: 50 },
            { name: '实训', max: 50 },
            { name: '潜力', max: 50 },
            { name: '判断', max: 50 },
            { name: '执行', max: 50 },
          ]
        },
        series: {
          type: 'radar',
          name: nam,
          data: dat
        }
      };
      echart.setOption(option);
    },
      loadData() {
   
        getMyData().then((res) => {
          const data = res.data.data;
          this.countList = data;
          this.initWater();
        });

      },
      goto(i){
        this.current = i
        let param= {
          "current":this.current,
          "size":this.size,
        }
        getUserAccountHistory(param).then(res =>{
          this.page = res.data.data
          this.userAccountHistory = res.data.data.records
        })
      },
      chongzhi(){
        this.money = ""
        this.moneyType = 1

        //设置支付默认
        if(this.website.aliFlag == '1') {
          this.payType = 1
        }else{
          if(this.website.wechatFlag == '1'){
            this.payType = 2
          }
        }
        this.payFlag = true;

      },
      closeWechat(){
        this.wechatPay = false
        clearInterval(this.timeInterval)
      },
      topay(){
        let money = 0
        switch (this.moneyType) {
          case 1:
            money = 300;
            break
          case 2:
            money = 500;
            break
          case 3:
            money = 1000;
            break
          case 4:
            money = this.money;
            break
        }
        let that = this
        //支付宝支付
        if(this.payType == 1){
          let param = {
            orderType:"ACCOUNT",
            money:money,
            payType: this.payType,
            reqChannel:"web"
          }
          createOrder(param).then(res =>{
            this.order = res.data.data
            this.waitpay = true
            window.open(this.baseUrl + "/front/aliPay/pcPay?orderId="+res.data.data.id);
          })
        }
        //微信支付
        if(this.payType == 2){
          let param = {
            orderType:"ACCOUNT",
            money:money,
            payType: this.payType,
            reqChannel:"web"
          }
          createOrder(param).then(res =>{
            this.order = res.data.data
            //获取微信支付二维码信息
            getwechatPay({orderId:this.order.id}).then(res=>{
              if(res.data.code == 200){
                this.wechatPayUrl = res.data.data
                this.wechatPay = true
              }
            })
            that.timeInterval = setInterval(function () {
              let params = {
                orderId:that.order.id
              }
              checkOrder(params).then(res=>{
                that.wechatPay = false;
                that.payOk = true
                that.payFlag = false
                that.waitpay = false
                that.init()
                clearInterval(that.timeInterval)
                // alert("支付成功")
                // this.$router.push({ path: '/uc/myCourse'})
              },error =>{
                console.log("未支付成功，请在支付页面继续完成支付！")
              })
            },1000)
          })
        }
      },
      checko(){
        let params = {
          orderId:this.order.id
        }
        checkOrder(params).then(res=>{
          this.payFlag = false
          this.waitpay = false
          this.init()
        },error =>{
          this.orderCss = true
          this.orderMsg = "未支付成功，请在支付页面继续完成支付！"
        })
      },
      clickMoney(moneyType){
        this.moneyType = moneyType
      },
      clickPayType(payType){
        this.payType = payType;
      },
      init(){
        console.log("this.studentInfo",this.studentInfo)
        var list = []
        myScoreList({
          clazzId:this.studentInfo.clazzId
        }).then(res=>{
            console.log("myScoreList",res)
            this.scoreList = res.data.data.records
            this.totalScore = res.data.data.last[0]
        })

       // this.loadData()
        // if (typeof(this.studentInfo.id) == "undefined"){
        //   return;
        // }
        // getUserAccount().then(res =>{
        //   // this.userAccount = res.data.data;
        //   let accountList = res.data.data.records
        //   if(accountList.length >0){
        //     this.userAccount = accountList[0];
        //   }
        // })
        // let param= {
        //   "current":this.current,
        //   "size":this.size,
        // }
        // getUserAccountHistory(param).then(res =>{
        //   this.page = res.data.data
        //   this.userAccountHistory = res.data.data.records
        // })
      }
    },
    mounted:function() {
      this.init();
    },
    computed: {
      ...mapGetters(["studentInfo","website"]),
    },
    components: {
      pageComponent,"v-chart": ECharts
    },
    watch: {
      studentInfo: function (val, oldVal) {
        this.init();
      }
    },
  }
</script>

<style scoped>
  .wechat-modal {
    width: 320px;
    background-color: #fff;
    text-align: center;
    padding: 36px 0 32px;
    border-radius: 12px;
  }
  .wechat-modal .modal {
    position: relative;
  }
  .wechat-modal .price {
    font-size: 16px;
    color: #545c63;
    line-height: 24px;
    font-weight: 700;
  }
  .wechat-modal .qrcode {
    width: 180px;
    height: 180px;
    margin: 12px auto;
  }
  .wechat-modal p {
    font-size: 12px;
    color: #545c63;
    line-height: 18px;
  }
  .wechat-modal .restart-btn {
    font-size: 14px;
    color: #9199a1;
    line-height: 24px;
    margin-top: 24px;
    cursor: pointer;
  }
</style>
