<template>
  <div>
    <el-row :gutter="10" class="grid-row">
      <el-col :xs="2" :sm="3" :md="4" :lg="4" :xl="4" class="grid-empty">
        <div class="grid-empty"></div>
      </el-col>
      <el-col :xs="10" :sm="9" :md="8" :lg="8" :xl="8">
        <el-carousel
          arrow="never"
          autoplay="false"
          class="grid-right image-size"
        >
          <el-carousel-item
            v-for="banner in banLst"
            :key="banner.id"
            class="grid-right image-size"
          >
            <el-image
              :src="banner.imageUrl"
              :fit="scale - down"
              class="image-size"
            ></el-image>
          </el-carousel-item>
        </el-carousel>
      </el-col>
      <el-col :xs="10" :sm="9" :md="8" :lg="8" :xl="8" class="grid-empty">
        <el-tabs v-model="activeName" @tab-click="handleClick">
          <el-tab-pane label="新闻动态" name="first">
            <el-table
              :data="newsArticleLIst"
              :show-header="false"
              @row-click="routerPush"
            >
              <el-table-column prop="title" label="消息"></el-table-column>
              <el-table-column
                prop="createTime"
                label="日期"
                :formatter="formatDate"
                min-width="25px"
              >
              </el-table-column>
            </el-table>
          </el-tab-pane>

          <el-tab-pane label="考试通知" name="second">
            <el-table
              :data="articleList"
              :show-header="false"
              @row-click="routerPush"
            >
              <!--              <router-link :to="{path:'/article/info/'+articleList[row].id}" target="_blank" class="title">-->
              <el-table-column prop="title" label="消息"></el-table-column>
              <el-table-column
                prop="createTime"
                label="日期"
                :formatter="formatDate"
                min-width="25px"
              >
              </el-table-column>
              <!--              </router-link>-->
            </el-table>
          </el-tab-pane>

          <el-tab-pane label="政策法规" name="third">
            <el-table
              :data="policyArticleLIst"
              :show-header="false"
              @row-click="routerPush"
            >
              <el-table-column prop="title" label="消息"></el-table-column>
              <el-table-column
                prop="createTime"
                label="日期"
                :formatter="formatDate"
                min-width="25px"
              >
              </el-table-column>
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </el-col>
      <el-col :xs="2" :sm="3" :md="4" :lg="4" :xl="4" class="grid-empty">
        <div></div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import Swiper from "swiper";
import "swiper/css/swiper.css";
import { getBannerList } from "@/api";
import { articleList } from "@/api";

export default {
  name: "banner",
  data() {
    return {
      banLst: [],
      activeName: "second",
      articleList: [],
      policyArticleLIst: [],
      newsArticleLIst: [],
      isReg: false,
      name: "",
      password: "",
      repeat: ""
    };
  },
  mounted: function() {
    this.SwiperFun();
    this.init();
  },
  methods: {
    init() {
      this.getExamArticleList(1, 10, 371);
      this.getpolicyArticleList(1, 10, 372);
      this.getNesArticleList(1, 10, 373);
    },

    routerPush(row) {
      this.$router.push({ path: "/article/info/" + row.id });
    },
    formatDate(row, column) {
      // 获取单元格数据
      let data = row[column.property];
      if (data == null) {
        return null;
      }
      let dt = new Date(data);

      return (
        dt.getFullYear() + "-" + (dt.getMonth() + 1) + "-" + dt.getDate() + " "
      );
    },
    getExamArticleList(current, size, subjectId) {
      articleList(current, size, subjectId).then(res => {
        this.articleList = res.data.data.records;
      });
    },
    getpolicyArticleList(current, size, subjectId) {
      articleList(current, size, subjectId).then(res => {
        this.policyArticleLIst = res.data.data.records;
      });
    },
    getNesArticleList(current, size, subjectId) {
      articleList(current, size, subjectId).then(res => {
        this.newsArticleLIst = res.data.data.records;
      });
    },
    SwiperFun() {
      if (this.banLst.length == 0) {
        getBannerList("2").then(res => {
          this.banLst = res.data.data;
        });
      }
      new Swiper(".swiper-container", {
        // 如果需要分页器
        pagination: {
          el: ".swiper-pagination",
          clickable: true
        },
        autoplay: {
          delay: 5000,
          disableOnInteraction: false, // 取消鼠标操作后的轮播暂停【无操作轮播继续】【参考链接1】
          stopOnLastSlide: false // 在最后一页停止
        },
        loop: true,
        observer: true,
        observeParents: true,

        // 如果需要前进后退按钮
        navigation: {
          nextEl: ".swiper-button-next",
          prevEl: ".swiper-button-prev"
        },
        // 如果需要滚动条
        scrollbar: {
          el: ".swiper-scrollbar"
        }
      });
    },
    handleClick(tab, event) {
      console.log(tab, event);
    }
  }
};
</script>

<style scoped lang="scss">
@import "../../../public/static/css/swiper/swiper.scss";

.grid-row {
  background-color: transparent;
  margin-top: 30px;
}

.grid-empty {
  margin-top: 2px;
  color: #fff;
  margin-left: 89px;
}

.image-size {
  width: 619px;
  height: 367px;
  margin-top: 4px;
}

.grid-right {
  color: transparent;
  display: flex;
  // flex-flow: row wrap;
  align-content: flex-end;
}

.grid-left {
  color: transparent;
  text-align: left;
}

::v-deep .el-carousel--horizontal {
  /* overflow-x: hidden; */
  overflow: scroll;
  text-align: left;
  scrollbar-width: none; /* firefox */
  -ms-overflow-style: none; /* IE 10+ */
  &::-webkit-scrollbar {
    // chrome safari
    display: none;
  }
}
</style>
