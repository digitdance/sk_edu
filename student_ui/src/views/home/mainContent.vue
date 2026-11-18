<template>
  <div class="container">
    <div class="list">
      <div class="labels">
        <div class="line11"></div>
        数据预览
      </div>
      <div class="data_preview">
        <div class="data_preview_list">
          <div class="data_preview_num">{{ countList.count1 }}</div>
          <div class="data_preview_item">
            <image src="@/assets/img/task.png"></image>完成学习
          </div>
        </div>
        <div class="lines"></div>
        <div class="data_preview_list">
          <div class="data_preview_num">{{ countList.count2 }}</div>
          <div class="data_preview_item">
            <image src="@/assets/img/task.png"></image>完成答题
          </div>
        </div>
        <!-- <div class="lines"></div>
        <div class="data_preview_list">
          <div class="data_preview_num">{{ countList.count3 }}</div>
          <div class="data_preview_item">
            <img src="../../assets/img/task.png" />今日积分
          </div>
        </div>
        <div class="lines"></div>
        <div class="data_preview_list">
          <div class="data_preview_num">{{ countList.count4 }}</div>
          <div class="data_preview_item">
            <img src="../../assets/img/task.png" />今日排名
          </div>
        </div> -->
      </div>
    </div>

    <div class="list">
      <div class="labels">
        <div class="line11"></div>
        常用入口
      </div>
      <ul>
        <router-link to="/uc/myPackage?courseType=PACKAGE">
          <li>
            <div class="li">
              <image src="@/assets/img/my_task.png" alt=""></image>
            </div>
            <div>我的班级</div>
          </li>
        </router-link>
        <!-- <router-link :to="item.url" v-for="item in links">
          <li>
            <div class="li li3">
              <img :src="'../../assets/img/my_knowledge.png'" alt="" />
            </div>
            <div>{{ item.name }}</div>
          </li>
        </router-link> -->
        <!-- <router-link to="/course/wmCourseList">
          <li>
            <div class="li li3">
              <img src="../../assets/img/my_knowledge.png" alt="" />
            </div>
            <div>我的资源</div>
          </li>
        </router-link> -->
        <router-link to="/uc/myComment">
          <li>
            <div class="li li1">
              <img src="../../assets/img/my_questions.png" alt="" />
            </div>

            <div>我的提问</div>
          </li>
        </router-link>
        <router-link to="/course/map/227">
          <li @click="toAi">
            <div class="li li1">
              <img src="../../assets/img/my_map.png" alt="" />
            </div>
            <div>AI助教</div>
          </li>
        </router-link>
        <!-- <router-link to="/uc/myRaking">
          <li>
            <div class="li li2">
              <img src="../../assets/img/my_ranking.png" alt="" />
            </div>
            <div>我的排名</div>
          </li>
        </router-link>  -->
        <router-link :to="item.url" v-for="item in links">
          <li>
            <div class="li li3">
              <img :src="'../../assets/img/my_knowledge.png'" alt="" />
            </div>
            <div>{{ item.name }}</div>
          </li>
        </router-link>
        <!-- <router-link to="/course/courseTaskInfo2"> -->
        <!-- <li>
                                      <div class="li li2">
                                        <img src="../../assets/img/my_many.png" alt="" />
                                      </div>

                                      <div>其他更多</div>
                                    </li> -->
        <!-- </router-link> -->
      </ul>
    </div>

    <div class="list">
      <div class="labels">
        <div class="line11"></div>
        班级公告
      </div>
      <div style="display: flex;flex-direction: column;">

        <div class="title_list ts" style="min-height: 50px;padding: 5px 10px;border-top: 1px solid beige;"
          v-for="(article, i) in notices" :key="article.id">
          <div style="display: flex;flex-wrap: wrap;justify-content: space-between;">
            <div style="font-weight: 600;">{{ article.title }} </div>
            <div style="color: #666;font-size: 11px; width: 200px;">发布时间：{{ article.createTime }}</div>
          </div>
          <div style="color: #666;font-size: 12px;padding: 5px;">{{ article.content }}</div>
        </div>
        <page-component style="font-size: 12px;" :page="pageNotice" v-show="notices.length > 0"></page-component>
      </div>
    </div>

    <aside>
      <div class="left">
        <div class="left_item">
          <div class="labels">
            <div class="line11"></div>
            学习完成进度
          </div>
          <div class="round_list">
            <div class="round_item">
              <div class="round"></div>
              已完成
            </div>
            <div class="round_item round_item1">
              <div class="round round1"></div>
              总学习
            </div>
          </div>
        </div>
        <el-progress type="circle" :stroke-width="30" :percentage="taskPer" :width="160"></el-progress>
        <div class="progress_title">完成的学习的占比</div>
        <div class="data_preview">
          <div class="data_preview_list">
            <div class="data_preview_num">{{ taskComplete }}</div>
            <div class="data_preview_item">完成学习</div>
          </div>
          <div class="lines"></div>
          <div class="data_preview_list">
            <div class="data_preview_num">{{ kpointNum }}</div>
            <div class="data_preview_item">学习总数</div>
          </div>
        </div>
      </div>
      <div class="center">
        <div class="left_item">
          <div class="labels">
            <div class="line11"></div>
            考试完成进度
          </div>
          <div class="round_list">
            <div class="round_item">
              <div class="round"></div>
              已完成
            </div>
            <div class="round_item round_item1">
              <div class="round round1"></div>
              总学习
            </div>
          </div>
        </div>
        <el-progress type="circle" :stroke-width="30" :percentage="examPer" :width="160"></el-progress>
        <div class="progress_title">完成的学习的占比</div>
        <div class="data_preview">
          <div class="data_preview_list">
            <div class="data_preview_num">{{ examPaperCompleteCount }}</div>
            <div class="data_preview_item">完成考试</div>
          </div>
          <div class="lines"></div>
          <div class="data_preview_list">
            <div class="data_preview_num">{{ examPaperCount }}</div>
            <div class="data_preview_item">试卷总数</div>
          </div>
        </div>
      </div>
      <div class="right">
        <div class="labels">
          <div class="line11"></div>
          学习画像
        </div>

        <div id="main_5" style="width: 300px;height: 300px;padding: 3px;"></div>

        <!-- <el-row style="font-size: 10px;padding: 6px;color:gray">
            <el-col :span="12" style="text-align: left;"> 视频学习学时：0 小时</el-col>
            <el-col :span="12" style="text-align: left;"> 学习课程数： 12 课时</el-col>
            <el-col :span="12" style="text-align: left;"> 练习次数：5 次</el-col>
          </el-row> -->

      </div>
    </aside>
  </div>
</template>
<script>
import { articleList, cmsSubject, msgReceiveUpdateState, msgreceiveList, studentLinks } from "@/api";
import echarts from "echarts";
import "echarts-liquidfill";
import { getMyData } from "@/api";
import ECharts from "vue-echarts";
import { mapGetters } from "vuex";
import { getNoticeList, getBannerList } from "@/api";
import { getMyStoreNum } from "@/api/user";

import pageComponent from '@/views/common/pageComponent'


export default {
  name: "DataView",
  components: {
    "v-chart": ECharts, pageComponent
  },
  data() {
    return {
      shopNum: 0,
      taskComplete: 0,
      links: [],
      taskAll: 0,
      current: 1,
      pageNotice: {
        pageSize: 3,
        currentPage: 1,
        total: 0,
      },
      taskPer: 0,
      examPaperCompleteCount: 0,
      kpointNum: 0,
      examPaperCount: 0,
      examComplete: 0,
      msgList: [
        {
          title: "开学通知",
          createTime: "2026-01-01",
          summary: "2026学年开学通知"
        }
      ],
      examAll: 0,
      examPer: 0,
      countList: {},
      notices: [],
    };
  },
  computed: {
    ...mapGetters(["userInfo", "studentInfo", "isLogin"]),
  },
  created() {
    var that = this;
    console.log("studentInfo", this.studentInfo);
    this.$nextTick(() => {
      that.loadData();
      that.radar('main_5', 'AA', [[23, 43, 12, 32, 45]])
    });
    this.getMsgList()
  },
  filters: {
    length10(val) {
      if (val) {
        return val.substring(0, 10);
      }
    },
    titleFilter(val) {
      if (val) {
        if (val.length > 10) {
          return val.substring(0, 10) + "...";
        } else {
          return val.substring(0, 10);
        }
      }
    },
  },
  methods: {
    gotoNotice(i) {
      this.current = i
      this.selectExamList();
    },
    toAi() {
      console.log("this.userInfo", this.userInfo)
      if (this.userInfo.account) {
        console.log("this.userInfo", this.userInfo)
        window.open('http://edu_demo.digitdance.cn/ai/index.html#/login?username=' + this.userInfo.account)
      } else {
        this.$router.push({ path: '/user/login' })
        return
      }
    },
    getMsgList(current, size) {
      if (typeof (this.studentInfo.id) == "undefined") {


        return;
      }
      //更新站内信状态
      // msgReceiveUpdateState(this.studentInfo.id).then(res =>{
      //   this.$store.dispatch("updateLetterCount", this.studentInfo)
      // })
      let params = {
        "current": current,
        "size": size,
        "receivingCusid": this.studentInfo.id
      }
      msgreceiveList(params).then(res => {

        console.log("msgreceiveList", res)
        this.page = res.data.data
        this.msgList = res.data.data.records

      })
    },
    loadData() {
      studentLinks({
        type: "studentLink1",
        pageSize: 10
      }).then(res => {
        console.log("res", res)
        this.links = res.data.data
      })

      getMyData().then((res) => {
        const data = res.data.data;
        this.countList = data;
        this.initWater();
      });

      // getMyStoreNum().then(res=>{
      //   const data = res.data.data;
      //   this.shopNum = data
      //   //console.log("getMyStoreNum",datra)
      // })
      getNoticeList().then((res) => {
        console.log("getNoticeList", res)
        const data = res.data.data;
        this.notices = data;
        //
      });
    },

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
    }
  },
};
</script>

<style scoped lang="scss">
.text-center {
  text-align: center;
}

.line11 {
  width: 12px;
  height: 14px;
  background: #0091ff;
  border-radius: 0px 2px 2px 0px;
  margin-right: 10px;
}

.lines {
  width: 2px;
  height: 60px;
  background: #efefef;
  border-radius: 10px;
}

.labels {
  display: flex;
  align-items: center;
  padding-top: 20px;
}

.list {
  width: 90%;
  margin-top: 30px;
  margin-left: 50px;
  box-sizing: border-box;
  background: #ffffff;
  box-shadow: 0px 2px 6px 0px rgba(0, 0, 0, 0.05);
  border-radius: 10px;
}

.data_preview {
  display: flex;
  text-align: center;
  justify-content: space-evenly;
  margin-top: 6px;
  padding-bottom: 26px;

  .data_preview_list {
    .data_preview_num {
      font-size: 24px;
      color: #3d3d3d;
    }

    .data_preview_item {
      display: flex;
      align-items: center;
      margin-top: 10px;
      color: #666666;
      font-size: 14px;

      img {
        margin-right: 3px;
      }
    }
  }
}

ul {
  display: flex;
  justify-content: space-evenly;
  padding: 20px 0 25px;

  li {
    display: flex;
    flex-direction: column;
    align-items: center;
    cursor: pointer;

    .li {
      width: 50px;
      height: 50px;
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: 50%;
      background: #0091ff;
      margin-bottom: 10px;
    }

    .li1 {
      background: #46c3ae;
    }

    .li3 {
      background: #1bc949;
    }

    .li2 {
      background: #ff8578;
    }
  }
}

aside {
  display: flex;
  justify-content: space-between;
  margin-top: 30px;
  margin-left: 50px;
  padding-bottom: 40px;
  width: 90%;

  .round_list {
    margin-top: 20px;
    margin-right: 20px;

    .round_item1 {
      margin-top: 10px;
    }

    .round_item {
      display: flex;
      align-items: center;

      .round {
        width: 12px;
        height: 12px;
        background: #0091ff;
        border-radius: 50%;
        margin-right: 3px;
      }

      .round1 {
        background: #e5e9f2;
      }
    }
  }

  .el-progress {
    margin-left: 140px;
  }

  .progress_title {
    color: #666666;
    font-size: 14px;
    margin: 10px 0 20px 164px;
  }

  .left_item {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
  }

  .left {
    width: 390px;
    height: 380px;
    background: #ffffff;
    box-shadow: 0px 2px 6px 0px rgba(0, 0, 0, 0.05);
    border-radius: 10px;
  }

  .center {
    width: 390px;
    height: 380px;
    background: #ffffff;
    box-shadow: 0px 2px 6px 0px rgba(0, 0, 0, 0.05);
    border-radius: 10px;
  }

  .right {
    width: 260px;
    height: 380px;
    background: #ffffff;
    box-shadow: 0px 2px 6px 0px rgba(0, 0, 0, 0.05);
    border-radius: 10px;

    .ts {
      margin-top: 20px;
    }

    .title_list {
      margin-bottom: 12px;

      .time {
        margin-left: 48px;
        font-size: 14px;
      }

      .title_item {
        display: flex;
        align-items: center;

        .index {
          width: 18px;
          height: 18px;
          line-height: 18px;
          background: #0091ff;
          border-radius: 50%;
          color: #fff;
          opacity: 0.5;
          margin: 0 8px 0 22px;
          text-align: center;
          font-size: 12px;
        }

        .index0 {
          background: #46c3ae;
        }

        .index1 {
          background: #be2f1f;
        }

        .index2 {
          background: #ff8578;
        }
      }
    }
  }
}
</style>