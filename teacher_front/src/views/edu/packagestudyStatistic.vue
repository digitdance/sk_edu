<template>

  <div>
    <basic-container>
      <el-select @change="changeClazz" v-model="clazzId"  placeholder="请选择班级" style="width: 30%;margin-right: 50px;" >
                <el-option
                v-for="item in classes"
                :key="item.id"
                :label="item.courseName"
                :value="item.id">
                </el-option>
       </el-select>
      
       <el-date-picker
          v-model="startEnd"
          @change="selectStartEnd"
          type="datetimerange"
          range-separator="至"
          format="yyyy-MM-dd HH:mm:ss"
          value-format="yyyy-MM-dd HH:mm:ss"
          start-placeholder="开始日期"
          end-placeholder="结束日期">
        </el-date-picker>
    </basic-container>

    <basic-container >
     <div style="display: flex;flex-direction: row;">
      <div style="width: 24%;padding: 20px;background-color: #f8f9fb;margin-right: 5px;">
        <v-chart  theme="light" :options="onlineOption1" />
      </div>
      <div style="width: 24%;padding: 20px;margin-right: 5px;background-color: #f8f9fb;">
        <v-chart  theme="light" :options="onlineOption2" />
      </div>
      <div style="width: 24%;padding: 20px;margin-right: 5px;background-color: #f8f9fb;">
        <v-chart  theme="light" :options="onlineOption3" />
      </div>
      <div style="width: 24%;padding: 20px;background-color: #f8f9fb;">
        <v-chart  theme="light" :options="onlineOption4" />
      </div>
     </div>
    </basic-container>

    <div style="display: flex;flex-direction: row;">
      <div style="width: 49%;padding: 20px;background-color: #f8f9fb;margin-right: 5px;">
        <v-chart class="echartspie" theme="light" :options="pieOption1" />
      </div>
       <div style="width: 49%;padding: 20px;margin-right: 5px;background-color: #f8f9fb;">
        <v-chart class="echartspie" theme="light" :options="pieOption2" />
      </div>
      <!-- <div style="width: 24%;padding: 20px;margin-right: 5px;background-color: #f8f9fb;">
        <v-chart  theme="light" :options="onlineOption3" />
      </div>
      <div style="width: 24%;padding: 20px;background-color: #f8f9fb;">
        <v-chart  theme="light" :options="onlineOption4" />
      </div>  -->
     </div>
    

    <!-- <basic-container>
      <avue-data-rotate :option="option2"></avue-data-rotate>
    </basic-container> -->
   
    <basic-container>
    <avue-crud :option="option"
               :table-loading="loading"
               :data="data"
               :page="page"
               :permission="permissionList"
               :before-open="beforeOpen"
               v-model="form"
               ref="crud"
               @row-update="rowUpdate"
               @row-save="rowSave"
               @row-del="rowDel"
               @search-change="searchChange"
               @search-reset="searchReset"
               @selection-change="selectionChange"
               @current-change="currentChange"
               @size-change="sizeChange"
               @refresh-change="refreshChange"
               @on-load="onLoad">
      <template slot-scope="scope" slot="menu">
        <el-button type="text" @click="openKpointStudyStudentHistory(scope.row)" >查看课程详情</el-button>
      </template>

      <template slot-scope="scope" slot="clazzIdSearch">

    <el-select v-model="clazzId"  placeholder="请选择班级" style="width: 70%;" >
        <el-option
        v-for="item in classes"
        :key="item.id"
        :label="item.courseName"
        :value="item.id">
        </el-option>
    </el-select>
    </template>
    </avue-crud>
    <el-dialog title="查看章节全部学员详情" width="80%"  :visible.sync="coursekpointstudyStudenthistory" append-to-body>
      <coursekpointstudentstudy v-if="coursekpointstudyStudenthistory" :courseId="courseIds" :courseType="courseType" :kpointCourseId="kpointCourseId"></coursekpointstudentstudy>
    </el-dialog>
  </basic-container>
  </div>
  
</template>

<script>
// import * as echarts from 'echarts';
// var chartDom = document.getElementById('main');
// var myChart = echarts.init(chartDom);
// var option;

// prettier-ignore
// const data = [["2000-06-05", 116], ["2000-06-06", 129], ["2000-06-07", 135], ["2000-06-08", 86], ["2000-06-09", 73], ["2000-06-10", 85], ["2000-06-11", 73], ["2000-06-12", 68], ["2000-06-13", 92], ["2000-06-14", 130], ["2000-06-15", 245], ["2000-06-16", 139], ["2000-06-17", 115], ["2000-06-18", 111], ["2000-06-19", 309], ["2000-06-20", 206], ["2000-06-21", 137], ["2000-06-22", 128], ["2000-06-23", 85], ["2000-06-24", 94], ["2000-06-25", 71], ["2000-06-26", 106], ["2000-06-27", 84], ["2000-06-28", 93], ["2000-06-29", 85], ["2000-06-30", 73], ["2000-07-01", 83], ["2000-07-02", 125], ["2000-07-03", 107], ["2000-07-04", 82], ["2000-07-05", 44], ["2000-07-06", 72], ["2000-07-07", 106], ["2000-07-08", 107], ["2000-07-09", 66], ["2000-07-10", 91], ["2000-07-11", 92], ["2000-07-12", 113], ["2000-07-13", 107], ["2000-07-14", 131], ["2000-07-15", 111], ["2000-07-16", 64], ["2000-07-17", 69], ["2000-07-18", 88], ["2000-07-19", 77], ["2000-07-20", 83], ["2000-07-21", 111], ["2000-07-22", 57], ["2000-07-23", 55], ["2000-07-24", 60]];
// const dateList = data.map(function (item) {
//   return item[0];
// });
// const valueList = data.map(function (item) {
//   return item[1];
// });
import ECharts from "vue-echarts";
import "echarts/lib/chart/line";
import "echarts/lib/chart/pie";
import "echarts/lib/component/polar";
// 提示
import "echarts/lib/component/tooltip";
// 图例
import "echarts/lib/component/legend";
// 标题
import "echarts/lib/component/title";

import {
    getMyCourseList,
  } from "@/api/edu/course";
  import {
    getMyClazzList
    } from "@/api/edu/course";

  import {getList, getDetail, add, update, remove} from "@/api/edu/packagestudyKpointhistory";
  import {mapGetters} from "vuex";
  import  coursekpointstudentstudy from "./coursestudyKpointhistory";
  export default {

    data() {
      return {
        form: {},
        query: {courseId:this.courseId},
        loading: true,
        clazzId:"",
       // onlineOption1: {"loginDataList":[0,0,0,0,0,0,0],"data":["2022-03-22","2022-03-23","2022-03-24","2022-03-25","2022-03-26","2022-03-27","2022-03-28"],"viewDataList":[0,0,0,0,0,0,1]},
        onlineOption1: {},
        onlineOption2: {},
        onlineOption3: {},
        onlineOption4: {},

        pieOption1:{},
        pieOption2:{},
        startEnd:"",
        clazzName:"",
        kpointId:0,
        classes:[],
        courseIds:0,
        popeType:'',
        courseType:'',
        kpointCourseId:0,
        option1: {
          span: 6,
          data: [],
        },
        option2: {
          span: 8,
          data: [
            {
              click: function (item) {
                alert(JSON.stringify(item));
              },
              count: "150",
              title: "提现申请",
              icon: "el-icon-warning",
              color: "rgb(49, 180, 141)",
            },
            {
              click: function (item) {
                alert(JSON.stringify(item));
              },
              count: "5",
              title: "意见建议",
              icon: "el-icon-view",
              color: "#00a65a",
            },
            {
              click: function (item) {
                alert(JSON.stringify(item));
              },
              count: "44",
              title: "今日直播",
              icon: "el-icon-setting",
              color: "#f39c12",
            },
          ],
        },
        page: {
          pageSize: 10,
          currentPage: 1,
          total: 0
        },
        coursekpointstudyStudenthistory:false,
        selectionList: [],
        option: {
          tip: false,
          border: true,
          align: "center",
          index: false,
          addBtn:false,
          viewBtn: true,
          selection: false,
          defaultExpandAll:true,
          column: [
          // {
          //       label: "班级",
          //       prop: "clazzId",
          //       type: "select",
          //       searchLabelWidth: 110,
          //       span: 24,
              
          //       search: true,
          //       searchslot: true,
          //       props: {
          //           label: "courseName",
          //       },
          //       placeholder: "请选择班级",
          //       rules: [
          //           {
          //               required: true,
          //               message: "请选择班级",
          //               trigger: "blur",
          //           },
          //       ],
          //   },
          {
            label: "课程名称",
            prop: "courseName",
          },
            // {
            //   label: "课程类型",
            //   prop: "popeType",
            //   formatter: function (row,value) {
            //     if(value==1){
            //       return "录播";
            //     }else if(value==2){
            //       return "直播";
            //     }
            //   }
            // },
            {
              label: "播放总次数",
              prop: "playercount",
              formatter: function (row,value) {
                if(value!=-1){
                  return value+"次";
                }else {
                  return "";
                }
              }
            },
            {
              label: "课程总时长",
              prop: "videoTime",
            },
            {
              label: "应学习总人数",
              prop: "studyPope",
            },
          ]
        },
        data: []
      };
    },
    computed: {
      ...mapGetters(["permission"]),
      permissionList() {
        return {
          addBtn: this.vaildData(this.permission.coursestudyhistory_add, false),
          viewBtn: this.vaildData(this.permission.coursestudyhistory_view, false),
          delBtn: this.vaildData(this.permission.coursestudyhistory_delete, false),
          editBtn: this.vaildData(this.permission.coursestudyhistory_edit, false)
        };
      },
      ids() {
        let ids = [];
        this.selectionList.forEach(ele => {
          ids.push(ele.id);
        });
        return ids.join(",");
      }
    },
    props:{
      courseId: Number
    },
    watch: {
      courseId: {
        deep: true,
        handler(newVal){
          this.query.courseId = newVal;
          this.onLoad(this.page);
        }
      }
    },
    created: async function () {
      var _this = this;
      this.loading = true;

      this.courses = await this.getMyCourses()
      if(this.courses && this.courses.length>0){
        console.log("this.courses",this.courses)
        this.courseId = this.courses[0].id
        getMyClazzList(
              1,
              100,
              {}
          ).then((res) => {
              const data = res.data.data;
              _this.classes = data.records;
              _this.loading = false;
              _this.clazzId = _this.classes[0].id
              _this.clazzName = _this.classes[0].courseName
              //_this.studentScore()

              _this.$nextTick(e=>{

                _this.onlineOption1 =  {"title":{"text":"学习时长","subtext":""},"tooltip":{"trigger":"axis"},"xAxis":{"type":"category","boundaryGap":false,"data":["2026-11-10","2026-11-11","2026-11-12"]},"yAxis":{"type":"value","show":false},"series":[{"name":"登录人数","type":"line","smooth":true,"data":[0,2,2]}]}
                _this.onlineOption2 =  {"title":{"text":"学习人次","subtext":""},"tooltip":{"trigger":"axis"},"xAxis":{"type":"category","boundaryGap":false,"data":["2026-11-10","2026-11-11","2026-11-12"]},"yAxis":{"type":"value","show":false},"series":[{"name":"登录人数","type":"line","smooth":true,"data":[0,2,1]}]}
                _this.onlineOption3 =  {"title":{"text":"测验人次","subtext":""},"tooltip":{"trigger":"axis"},"xAxis":{"type":"category","boundaryGap":false,"data":["2026-11-10","2026-11-11","2026-11-12"]},"yAxis":{"type":"value","show":false},"series":[{"name":"登录人数","type":"line","smooth":true,"data":[0,0,0]}]}
                _this.onlineOption4 =  {"title":{"text":"实训人次","subtext":""},"tooltip":{"trigger":"axis"},"xAxis":{"type":"category","boundaryGap":false,"data":["2026-11-10","2026-11-11","2026-11-12"]},"yAxis":{"type":"value","show":false},"series":[{"name":"登录人数","type":"line","smooth":true,"data":[0,0,0]}]}
                _this.pieOption1 =  {
            title: {
              text: '发布课件',
              subtext: '',
              left: 'center'
            },
            tooltip: {
              trigger: 'item',
              formatter: '{a} <br/>{b}: {c} ({d}%)'
            },
            legend: {
              orient: 'vertical',
              left: 10,
              data: ['视频', '讲义', '课件', '资料']
            },
            series: [
              {
                name: '课程学习人数',
                type: 'pie',
                radius: ['50%', '70%'],
                avoidLabelOverlap: false,
                label: {
                  show: false,
                  position: 'center'
                },
                emphasis: {
                  label: {
                    show: true,
                    fontSize: '30',
                    fontWeight: 'bold'
                  }
                },
                labelLine: {
                  show: false
                },
                data: [
                  {value: 335, name: '数据1'},
                  {value: 310, name: '数据2'},
                  {value: 100, name: '数据3'},
                  {value: 135, name: '数据4'},
                  {value: 500, name: '数据5'}
                ]
              }
            ]
          }
                
          _this.pieOption2 =  {
            title: {
              text: '学习课件',
              subtext: '',
              left: 'center'
            },
            tooltip: {
              trigger: 'item',
              formatter: '{a} <br/>{b}: {c} ({d}%)'
            },
            legend: {
              orient: 'vertical',
              left: 10,
              data: ['', '']
            },
            series: [
              {
                name: '饼图名称',
                type: 'pie',
                radius: ['50%', '70%'],
                avoidLabelOverlap: false,
                label: {
                  show: false,
                  position: 'center'
                },
                emphasis: {
                  label: {
                    show: true,
                    fontSize: '30',
                    fontWeight: 'bold'
                  }
                },
                labelLine: {
                  show: false
                },
                data: [
                  {value: 100, name: '数据1'},
                  {value: 310, name: '数据2'},
                  {value: 234, name: '数据3'},
                  {value: 135, name: '数据4'},
                  {value: 499, name: '数据5'}
                ]
              }
            ]
          }


              })
        });
      }
    },
    methods: {
      
      changeClazz() {
          
      },
      selectStartEnd(){

      },
      async getMyCourses(){
        return new Promise((resolve,reject)=>{
            getMyCourseList().then(res=>{

                console.log("getMyCourseList",res.data.data)
                resolve(res.data.data)
            })
        })
      },
      openKpointStudyStudentHistory(row){
        this.courseIds=row.courseId;
        this.courseType="PACKAGE";
        this.kpointCourseId=row.kpointCourseId;
        this.coursekpointstudyStudenthistory=true;
      },
      rowSave(row, loading, done) {
        add(row).then(() => {
          loading();
          this.onLoad(this.page);
          this.$message({
            type: "success",
            message: "操作成功!"
          });
        }, error => {
          done();
          console.log(error);
        });
      },
      rowUpdate(row, index, loading, done) {
        update(row).then(() => {
          loading();
          this.onLoad(this.page);
          this.$message({
            type: "success",
            message: "操作成功!"
          });
        }, error => {
          done();
          console.log(error);
        });
      },
      rowDel(row) {
        this.$confirm("确定将选择数据删除?", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        })
          .then(() => {
            return remove(row.id);
          })
          .then(() => {
            this.onLoad(this.page);
            this.$message({
              type: "success",
              message: "操作成功!"
            });
          });
      },
      handleDelete() {
        if (this.selectionList.length === 0) {
          this.$message.warning("请选择至少一条数据");
          return;
        }
        this.$confirm("确定将选择数据删除?", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        })
          .then(() => {
            return remove(this.ids);
          })
          .then(() => {
            this.onLoad(this.page);
            this.$message({
              type: "success",
              message: "操作成功!"
            });
            this.$refs.crud.toggleSelection();
          });
      },
      beforeOpen(done, type) {
        if (["edit", "view"].includes(type)) {
          getDetail(this.form.id).then(res => {
            this.form = res.data.data;
          });
        }
        done();
      },
      searchReset() {
        this.query = {courseId:this.courseId};
        this.page.currentPage = 1;
        this.onLoad(this.page);
      },
      searchChange(params,done) {
        this.page.currentPage = 1;
        this.onLoad(this.page, params);
        done()
      },
      selectionChange(list) {
        this.selectionList = list;
      },
      selectionClear() {
        this.selectionList = [];
        this.$refs.crud.toggleSelection();
      },
      currentChange(currentPage){
        this.page.currentPage = currentPage;
      },
      sizeChange(pageSize){
        this.page.pageSize = pageSize;
      },
      refreshChange() {
        this.onLoad(this.page, this.query);
      },
      onLoad(page, params = {courseId:this.courseId}) {
        this.loading = true;
        getList(page.currentPage, page.pageSize, Object.assign(params, this.query)).then(res => {
          const data = res.data.data;
          /*this.page.total = data.total;*/
          this.data = data.records;
          this.loading = false;
          this.selectionClear();
        });
      }
    },
    components:{
      'coursekpointstudentstudy':coursekpointstudentstudy,
      "v-chart": ECharts,
    }
  };
</script>

<style scoped>

.el-divider--horizontal {
  margin: 12px 0 !important;
}

.el-font-size {
  font-size: 14px;
}
.echarts {
  width: 100%;
  height: 80px;
}

.echartspie {
  width: 100%;
  height: 200px;
}
</style>
