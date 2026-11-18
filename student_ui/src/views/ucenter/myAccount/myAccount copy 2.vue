<template>
  <div class="u-container">
    <div class="right-container l">

      <div class="right-title">
        <h2>我的成绩</h2>
        <ul>
          
          <li v-bind:class="{'action' :'1' == states}">
            <router-link :to="{path:'/uc/myOrder',query:{states:2}}">数据预览</router-link>
          </li>
          <li v-bind:class="{'action' :'2' == states}">
            <router-link :to="{path:'/uc/myOrder',query:{states:3}}">综合得分</router-link>
          </li>
        </ul>
      </div>
      <div class="balance-container">
        <div class="title-box">
          <div>
            <p class="title">综合得分</p>
            <!-- <button @click="chongzhi()" class="recharge-btn js-recharge-btn">立即充值</button> -->
          </div>
          <div>
            <p class="charge" style="left:0px">{{totalScore.score}}</p>
            <p class="charge-tip"></p>
          </div>
        </div>
        <div class="list-box" v-if="scoreList.length > 0">
          <table>
            <thead>
            <tr>
              <td width="154px">项目</td>
              <td width="94px">评分</td>
            </tr>
            </thead>
            <tbody>
            <tr v-for="score in scoreList" :key="score.id">
              <td>{{score.title}}</td>
              <td>{{score.score}}</td>
            </tr>
            </tbody>
          </table>
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


  export default {
    name: 'myAccount',
    data() {
      return {
        userAccount:{},
        myScores:[],
        states:"1",
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
    methods: {
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
      pageComponent
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
