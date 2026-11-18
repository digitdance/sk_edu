<template>
  <div>
   <!-- 底部：现代简洁风格 -->
<footer class="bg-primary text-white py-10">
    <div class="max-w-7xl mx-auto px-4">
        <div class="grid grid-cols-1 md:grid-cols-4 gap-8 mb-8">
            <!-- 品牌信息 -->
            <div class="col-span-1 md:col-span-1">
                <div class="flex items-center gap-3 mb-4">
                    <div class="w-10 h-10 rounded-lg bg-white flex items-center justify-center text-secondary font-bold">T</div>
                    <span class="text-xl font-bold text-white">TradeNest</span>
                </div>
                <p class="text-white/70 text-sm mb-4">数智营销产业学院教育平台</p>

            </div>
            
            <!-- 快速链接 -->
            <div class="col-span-1">
                <h4 class="text-white font-semibold mb-4">快速链接</h4>
                <ul class="space-y-2 text-sm">
                    <!-- <li><a href="#home" class="text-white/70 hover:text-white transition-colors">首页</a></li>
                    <li><a href="课程广场.html" class="text-white/70 hover:text-white transition-colors">课程广场</a></li>
                    <li><a href="我的课程.html" class="text-white/70 hover:text-white transition-colors">我的课程</a></li>
                    <li><a href="智能课程工坊.html" class="text-white/70 hover:text-white transition-colors">智能课程工坊</a></li> -->
                </ul>
            </div>
            
            <!-- 帮助中心 -->
            <div class="col-span-1">
                <h4 class="text-white font-semibold mb-4">帮助中心</h4>
                <ul class="space-y-2 text-sm">
                    <li><a href="#" class="text-white/70 hover:text-white transition-colors">使用指南</a></li>
                    <li><a href="#" class="text-white/70 hover:text-white transition-colors">常见问题</a></li>
                    <li><a href="#" class="text-white/70 hover:text-white transition-colors">联系客服</a></li>
                    <li><a href="#" class="text-white/70 hover:text-white transition-colors">意见反馈</a></li>
                </ul>
            </div>
            
            <!-- 联系我们 -->
            <div class="col-span-1">
                <h4 class="text-white font-semibold mb-4">联系我们</h4>
                <ul class="space-y-2 text-sm">
                    <li class="flex items-start gap-2">
                        <i class="fa-solid fa-map-marker-alt text-white/70 mt-1"></i>
                        <span class="text-white/70"></span>
                    </li>
                    <li class="flex items-center gap-2">
                        <i class="fa-solid fa-phone text-white/70"></i>
                        <span class="text-white/70"></span>
                    </li>
                    <li class="flex items-center gap-2">
                        <i class="fa-solid fa-envelope text-white/70"></i>
                        <span class="text-white/70"></span>
                    </li>
                </ul>
            </div>
        </div>
        
        <!-- 版权信息 -->
        <div class="border-t border-white/10 pt-6 flex flex-col md:flex-row justify-between items-center text-sm text-white/50">
            <p>© 2026 TradeNest 数智营销产业学院教育平台 版权所有</p>
            <div class="flex gap-4 mt-4 md:mt-0">
                <a href="#" class="hover:text-white transition-colors">隐私声明</a>
                <a href="#" class="hover:text-white transition-colors">服务条款</a>
                <a href="#" class="hover:text-white transition-colors">网站备案：ICP备XXXXXXX号</a>
            </div>
        </div>
    </div>
</footer>

<!-- 回到顶部按钮 -->
<button id="back-to-top" class="fixed bottom-8 right-8 w-12 h-12 rounded-full bg-secondary text-white flex items-center justify-center shadow-lg hover:bg-secondary/90 transition-all opacity-0 invisible">
    <i class="fa-solid fa-arrow-up"></i>
</button>

  </div>
</template>
<script>
import { mapGetters } from "vuex";
import { validatenull } from "@/util/validate";
import Vue from "vue";
import VueAppend from "vue-append";
Vue.use(VueAppend);


    
export default {
  name: "pageFooter",
  data() {
    return {
      showTip: false,
      ticket: 60,
      intv: null,
      noticeVisable: false,
      visible: true,
      censusCodeString: "",
      visibilityHeight: 100,
      backPosition: 0,
      interval: null,
    };
  },
  methods: {
    tanhide() {
      if (this.ticket < 5) {
        this.showTip = false;
      }
    },
    init() {
      var that = this;
      that.intv = setInterval(() => {
        if (that.ticket == 0) {
          clearInterval(that.intv);
          that.intv = null;
        } else {
          that.ticket--;
        }
      }, 1000);
      //判断如果website 为空则更新数据
      if (validatenull(this.bottomLink)) {
        this.$store.dispatch("getBottomLink");
      }
      //判断如果website 为空则更新数据
      if (validatenull(this.friendLink)) {
        this.$store.dispatch("getFriendLink");
      }
      //加载统计代码
      this.censusCodeString = this.website.censusCodeString;
    },

    backToTop() {
      let distanceY =
        window.pageYOffset ||
        document.documentElement.scrollTop ||
        document.body.scrollTop;
      let i = 0;
      let that = this;
      this.interval = setInterval(() => {
        let next = Math.floor(
          this.easeInOutQuad(10 * i, distanceY, -distanceY, 500)
        );
        if (next <= that.backPosition) {
          window.scrollTo(0, that.backPosition);
          clearInterval(this.interval);
        } else {
          document.documentElement.scrollTop = document.body.scrollTop = next;
        }
        i++;
      }, 17);
    },
    easeInOutQuad(t, b, c, d) {
      // 判断当前时间是否总在总时间的一半以内，是的话执行缓入函数，否则的话执行缓出函数
      if ((t /= d / 2) < 1) {
        return (c / 2) * t * t + b;
      } else {
        // 将总长度设置为一半，并且时间从当前开始递减，对图像进行垂直向上平移
        return (-c / 2) * (--t * (t - 2) - 1) + b;
      }
    },
    toAi(){
      console.log("this.userInfo",this.userInfo)
        if(this.userInfo.account){
          console.log("this.userInfo",this.userInfo)
          window.open('http://edu_demo.digitdance.cn/ai/#/login?username='+this.userInfo.account)
        }else{
          this.$router.push({ path: '/user/login' })
          return
        }
    },
  },
  mounted: function () {
    this.init();
    console.log("userInfo==>",this.userInfo)
  },
  computed: {
    ...mapGetters(["website", "bottomLink", "friendLink","userInfo"]),
  },
};
</script>

<style scoped>
.cc {
  display: flex;
  justify-content: center;
  position: absolute;
  bottom: 20px;
  width: 100%;
}
.graycolor {
  background-color: gray;
}
</style>
