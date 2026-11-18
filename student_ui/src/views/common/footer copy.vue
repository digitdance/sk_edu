<template>
  <div>
    <div class="d_tips">
      <!--纠错弹出-->
      <div class="ex_mask">
        <div class="ex003">
          <p class="ex_title" style="color: red; font-weight: bold">考场纪律</p>
          <p class="ex_br"></p>
          <div class="ex_box">
            <p class="ex_b_title" v-html="website.examNotice"></p>
          </div>
        </div>
      </div>
    </div>

    <div id="J_GotoTop" class="elevator">
      <!-- <a :href="website.information" class="elevator-msg" target="_blank">
        <i class="icon-feedback"></i>
        <span class="">呼叫</span>
      </a> -->
      <a href="/help" class="elevator-faq" target="_blank">
        <i class="icon-ques"></i>
        <span class="">考场纪律</span>
      </a>

      <a
        href="javascript:void(0)"
        @click="backToTop"
        v-show="visible"
        class="elevator-top no-goto"
        id="backTop"
      >
        <i class="icon-up2"></i>
        <span class="">返回顶部</span>
      </a>
    </div>
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
      noticeVisable: false,
      visible: true,
      censusCodeString: "",
      visibilityHeight: 100,
      backPosition: 0,
      interval: null,
    };
  },
  methods: {
    init() {
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
  },
  mounted: function () {
    this.init();
  },
  computed: {
    ...mapGetters(["website", "bottomLink", "friendLink"]),
  },
};
</script>

<style scoped>
</style>
