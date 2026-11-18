
<template>
  <div>
    <basic-container>
      <avue-form
        :option="option"
        @submit="handleSubmit"
        v-model="form"
      ></avue-form>
      <div>
        <v-chart v-if="chartFlag" theme="light" :options="onlineOption1" />
      </div>
    </basic-container>
  </div>
</template>

<script>
import { mapGetters } from "vuex";
import ECharts from "vue-echarts";
import "echarts/lib/chart/line";
import "echarts/lib/component/polar";
// 提示
import "echarts/lib/component/tooltip";
// 图例
import "echarts/lib/component/legend";
// 标题
import "echarts/lib/component/title";
import { getDetail, getDataByDay } from "@/api/edu/userStatistics";

export default {
  name: "wel",
  components: {
    "v-chart": ECharts,
  },
  data() {
    return {
      search: {},
      chartFlag: false,
      height: "400px",
      msg: "Welcome to Your Vue.js App",
      onlineOption1: {},
      onlineOption: {
        title: {
          text: "劳动课互动统计",
          subtext: "人数",
        },
        tooltip: {
          trigger: "axis",
        },
        legend: {
          // "登录人数", "访问人数",
          data: ["提交作业人数"],
        },
        xAxis: {
          type: "category",
          boundaryGap: false,
          data: [],
        },

        yAxis: {
          type: "value",
        },
        series: [
          // {
          //   name: "登录人数",
          //   type: "line",
          //   smooth: true,
          //   data: [],
          // },
          // {
          //   name: "游客人数",
          //   type: "line",
          //   smooth: true,
          //   data: [],
          // },
          {
            name: "作业提交数",
            type: "line",
            smooth: true,
            data: [],
          },
        ],
      },
      option: {
        submitText: "搜索",
        submitIcon: "el-icon-search",
        column: [
          {
            label: "时间",
            prop: "dateStatis",
            type: "select",
            span: 6,
            hide: true,
            search: true,
            value: 1,
            dicData: [
              {
                label: "最近7天",
                value: 1,
              },
              {
                label: "最近一个月",
                value: 2,
              },
            ],
          },
          {
            label: "教育局",
            prop: "jyjId",
            type: "select",
            dataType: "string",
            dicUrl: "/api/web/statdata/jyjs",
            tip: "教育局",
            props: {
              label: "deptName",
              value: "id",
            },
          },
          {
            label: "学校",
            prop: "schoolId",
            type: "select",
            dataType: "string",
            dicUrl: "/api/web/statdata/schools",
            tip: "学校",
            props: {
              label: "deptName",
              value: "id",
            },
          },
          // {
          //   label: "学校",
          //   prop: "dateStatis",
          //   type: "select",
          //   span: 6,
          //   hide: true,
          //   search: true,
          //   value: 1,
          //   dicData: [
          //     {
          //       label: "最近7天",
          //       value: 1,
          //     },
          //     {
          //       label: "最近一个月",
          //       value: 2,
          //     },
          //   ],
          // },

          {
            label: "班级",
            prop: "classId",
            type: "select",
            dataType: "string",
            dicUrl: "/api/web/statdata/classes",
            tip: "学校",
            props: {
              label: "courseName",
              value: "id",
            },
          },

          // {
          //   label: "班级",
          //   prop: "dateStatis",
          //   type: "select",
          //   span: 6,
          //   hide: true,
          //   search: true,
          //   value: 1,
          //   dicData: [
          //     {
          //       label: "最近7天",
          //       value: 1,
          //     },
          //     {
          //       label: "最近一个月",
          //       value: 2,
          //     },
          //   ],
          // },
        ],
      },
    };
  },
  computed: {
    ...mapGetters(["userInfo"]),
  },

  created() {
    this.init();
  },
  methods: {
    init() {
      // if (this.form.dateStatis == "") {
      //   this.form.dateStatis = 1;
      // }
      // if (this.form.dateStatis == 1) {
      //   this.form.code = "statadminuserdata";
      // }
      // if (this.form.dateStatis == 2) {

      // }
      this.form.code = "statadminuserdata";
      this.form.days = 3;
      console.log("handleSubmit");
      getDataByDay(this.form).then((data) => {
        let json = data.data.data.data;
        let jsonObj = JSON.parse(json);

        this.onlineOption.xAxis.data = jsonObj.data;
        // this.onlineOption.series[0].data = jsonObj.loginDataList;
        // this.onlineOption.series[1].data = jsonObj.viewDataList;

        this.onlineOption.series[0].data = jsonObj.data2;

        console.log("onlineOption", this.onlineOption);

        console.log(this.onlineOption);
        this.chartFlag = true;
        this.onlineOption1 = this.onlineOption;
      });
      done();

      // getDetail("statadminuserdata").then((data) => {
      //   let json = data.data.data.data;
      //   let jsonObj = JSON.parse(json);

      //   this.onlineOption.xAxis.data = jsonObj.data;
      //   this.onlineOption.series[0].data = jsonObj.loginDataList;
      //   this.onlineOption.series[1].data = jsonObj.viewDataList;
      //   this.onlineOption.series[2].data = jsonObj.data2;

      //   console.log("onlineOption", this.onlineOption);
      //   this.chartFlag = true;
      //   this.onlineOption1 = this.onlineOption;
      // });
    },
    handleSubmit(form, done) {
      if (this.form.dateStatis == "") {
        this.form.dateStatis = 1;
      }
      if (this.form.dateStatis == 1) {
        this.form.code = "statadminuserdata";
      }
      if (this.form.dateStatis == 2) {
        this.form.code = "statadminuserdata";
      }
      this.form.days = 3;
      console.log("handleSubmit");
      getDataByDay(this.form).then((data) => {
        let json = data.data.data.data;
        let jsonObj = JSON.parse(json);

        this.onlineOption.xAxis.data = jsonObj.data;
        // this.onlineOption.series[0].data = jsonObj.loginDataList;
        // this.onlineOption.series[1].data = jsonObj.viewDataList;

        this.onlineOption.series[0].data = jsonObj.data2;

        console.log("onlineOption", this.onlineOption);

        console.log(this.onlineOption);
        this.chartFlag = true;
        this.onlineOption1 = this.onlineOption;
      });
      done();
    },
  },
};
</script>

<style>
.el-divider--horizontal {
  margin: 12px 0 !important;
}

.el-font-size {
  font-size: 14px;
}
.echarts {
  width: 100%;
  height: 400px;
}
</style>

