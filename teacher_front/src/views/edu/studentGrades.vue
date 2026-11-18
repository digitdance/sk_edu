<template>
    <basic-container>
      <avue-crud :option="option" :search.sync="search" :table-loading="loading" :data="data" :page="page"
        :permission="permissionList" :before-open="beforeOpen" v-model="form" ref="crud" @row-update="rowUpdate"
        @row-save="rowSave" @row-del="rowDel" @search-change="searchChange" @search-reset="searchReset"
        @selection-change="selectionChange" @current-change="currentChange" @size-change="sizeChange"
        @refresh-change="refreshChange" @on-load="onLoad">
        <template slot="menuLeft" slot-scope="scope">
           
            <el-button type="plain"
                    size="small"
                    icon="el-icon-publish"
                    plain
                    @click="handleMgrRule">管理规则
            </el-button>


            <el-button type="primary"
                   size="small"
                   icon="el-icon-publish"
                   plain
                   @click="handlePublish">确认并发布
            </el-button>
        <!-- <el-button type="danger"
                   size="small"
                   icon="el-icon-delete"
                   plain
                   v-if="permission.agentaccount_delete"
                   @click="handleDelete">删 除
        </el-button> -->

          <!-- <el-button type="text" size="small" icon="el-icon-record" plain @click="openExamPigaiRecord(scope.row)">
            导出
          </el-button>
          <el-button type="text" size="small" icon="el-icon-record" plain @click="openExamPigaiRecord(scope.row)">
            确认并发布
          </el-button> -->
        </template>


        <template slot="menu" slot-scope="scope">
            <el-button type="primary"
                    size="small"
                    icon="el-icon-publish"
                    plain
                    @click="openChengji(scope.row)">详情
            </el-button>
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

        <template slot-scope="scope" slot="courseIdSearch">

            <el-select v-model="courseId"  placeholder="请选择课程" style="width: 70%;" >
                <el-option
                v-for="item in courses"
                :key="item.id"
                :label="item.courseName"
                :value="item.id">
                </el-option>
            </el-select>
            </template>

      </avue-crud>
      <el-dialog title="规则设置"  :visible.sync="dialogstudentGradeRateSettingVisible"
        style="padding-top: 0px;width:100%" append-to-body>
        <studentGradeRateSetting @close="closeSetting" :visible="dialogstudentGradeRateSettingVisible" :clazzId="clazzId" ></studentGradeRateSetting>
      </el-dialog>


      <el-dialog title="成绩管理" width="80%" :visible.sync="dialogChengjiVisible" @close="dialogChengjiVisible == false">
      <el-tabs v-model="activeName" @tab-click="handleClick">
        <el-tab-pane label="随堂测验" name="first">
          <exampaperrecordByUser :studentId="studentId" examType="4"></exampaperrecordByUser>
        </el-tab-pane>
        <el-tab-pane label="实训任务批改" name="second">
          <shicaoRecordQstByClazzPigai :clazzId="clazzId" :studentId="studentId"></shicaoRecordQstByClazzPigai>
        </el-tab-pane>
        <el-tab-pane label="综合考试" name="third">
          <exampaperrecordByUser :studentId="studentId" examType="2"></exampaperrecordByUser>
        </el-tab-pane>
        <!-- <el-tab-pane label="总成绩" name="fourth">
          <chengjiStatistics :courseInfo="course"></chengjiStatistics>
        </el-tab-pane> -->
      </el-tabs>
    </el-dialog>


    </basic-container>
  </template>
  
  <script>
  import { getToken } from "@/util/auth";
  import exampaperrecordQstByIdPigai from "../exam/exampaperrecordQstByIdPigai";
  import { getList, getDetail, add, update, remove, subjectExamTreeList } from "@/api/exam/question";
  import { mapGetters } from "vuex";
  import {
    selectClazzStudentPage as selectClazzStudent,
  } from "@/api/user/student";

  import {
    getMyCourseList,
  } from "@/api/edu/course";
  
  import studentGradeRateSetting from "./studentGradeRateSetting";
  import {
    pinfenStatisticByStudent,getClazzScoreSetting
  } from "@/api/edu/coursestudyhistory";

  import {
    getMyClazzList
    } from "@/api/edu/course";

 import exampapers from "@/views/exam/exampapers";
 import exampaperrecordByUser from "@/views/exam/exampaperrecordByUser";
 
 import shicaoRecordQstByClazzPigai from "@/views/exam/shicaoRecordQstByClazzPigai";
  let _this;
  export default {
    components: {
        studentGradeRateSetting,shicaoRecordQstByClazzPigai,exampapers,exampaperrecordByUser
    },
    data() {
      return {
        qstContentFlag: false,
        dialogChengjiVisible:false,
        activeName:"first",
        studentId:null,
        clazzId:"",
        dialogstudentGradeRateSettingVisible:false,
        clazzName:"",
        fileList: [],
        materialUploadVisible: true,
        qstAnalyzeFlag: false,
        excelBox: false,
        errorMsg: "",
        dialogRecordPigaiVisible: false,
        qstId: 0,
        token: getToken(),
        questionTypeList: [],
        ueditorOptions: {
          //普通图片上传
          //action: "/api/common/upload?path=course",
          action: "/api/uploadOss?path=course",
          customConfig: {
          },
          props: {
            res: "data",
            url: 'url'
          },
        },
        ueditorOptions2: {
          //普通图片上传
          action: "/api/uploadOss?path=course",
          customConfig: {
          },
          props: {
            res: "data",
            url: 'url'
          },
        },
        classes:[],
        isAsr: "",
        optionArr: [{
          editor: false,
          value: ""
        }, {
          value: "",
          editor: false,
        }],
        search: {},
        checkedArr: [],
        form: {},
        query: {},
        loading: true,
        page: {
          pageSize: 10,
          currentPage: 1,
          total: 0
        },
        selectionList: [],
        subjectExamTreeList: [],
        option: {
          searchShow: true,
          align: "center",
          tip: false,
          border: true,
          index: false,
          viewBtn: false,
          addBtn: false,
          editBtn: false,
          delBtn: false,
          selection: false,
          columnBtn: true,
          labelWidth: 130,
          column: [
            {
                label: "班级",
                prop: "clazzId",
                type: "select",
                searchLabelWidth: 110,
                span: 24,
                hide:true,
                search: true,
                searchslot: true,
                props: {
                    label: "courseName",
                },
                placeholder: "请选择班级",
                rules: [
                    {
                        required: true,
                        message: "请选择班级",
                        trigger: "blur",
                    },
                ],
            },
            {
                label: "课程",
                prop: "courseId",
                type: "select",
                searchLabelWidth: 110,
                span: 24,
                hide:true,
                search: true,
                searchslot: true,
                props: {
                    label: "courseName",
                },
                placeholder: "请选择课程",
                rules: [
                    {
                        required: true,
                        message: "请选择课程",
                        trigger: "blur",
                    },
                ],
            },
            {
                label: "班级",
                prop: "clazzName",
                span: 24,
                
            },
            {
                label: "用户",
                prop: "showName",
                search: false,
                
            },
            {
                label: "测验成绩",
                prop: "examScore",
                formslot: true,
                value: "",
                display: true,
                span: 24
            },
            {
                label: "课堂成绩",
                prop: "kpointStudyScore",
                formslot: true,
                value: "",
                display: true,
                span: 24
            },
            {
                label: "实训成绩",
                prop: "shixunScore",
                formslot: true,
                value: "",
                display: true,
                span: 24
            },
            {
                label: "综合成绩",
                prop: "lastScore",
                formslot: true,
                value: "",
                display: true,
                span: 24
            },
            
          ]
        },
        data: [],
        excelForm: {},
        excelOption: {
          submitBtn: false,
          emptyBtn: false,
          column: [
            {
              label: '模板上传',
              prop: 'excelFile',
              type: 'upload',
              drag: true,
              loadText: '模板上传中，请稍等',
              span: 24,
              showFileList: false,
              propsHttp: {
                res: 'data'
              },
              tip: '请上传 .xls,.xlsx 标准格式文件',
              action: "/api/exam/question/import-exam"
            },
            {
              label: '模板下载',
              prop: 'excelTemplate',
              formslot: true,
              span: 24,
            }
          ]
        }
      };
    },
    props: {
      kpoint: Object
    },
    watch: {
      
    },
    computed: {
      ...mapGetters(["permission"]),
      permissionList() {
        return {
          addBtn: this.vaildData(this.permission.question_add, false),
          viewBtn: this.vaildData(this.permission.question_view, false),
          delBtn: this.vaildData(this.permission.question_delete, false),
          editBtn: this.vaildData(this.permission.question_edit, false)
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
    methods: {
      handleMgrRule(){
            this.dialogstudentGradeRateSettingVisible = true
      },
      closeSetting(e){
        console.log("closeSetting",e)
        this.dialogstudentGradeRateSettingVisible = false
        this.$refs.crud.option.column[4].label = '测验成绩('+e['t_exam']+'%)'
        this.$refs.crud.option.column[5].label = '课堂成绩('+e['t_kpoint_study']+'%)'
        this.$refs.crud.option.column[6].label = '实训成绩('+e['t_shixun']+'%)'
      },
      async getSetting(){
        var that = this
        if(this.clazzId){
          return new Promise((resolve,reject)=>{
            getClazzScoreSetting({clazzId:this.clazzId}).then(res=>{
                var records = res.data.data
                resolve(records)
                // console.log("setting==>",records)
                // if(!records.t_exam){
                //     records = {
                //         "t_exam": 30,
                //         "t_kpoint_study":30,
                //         "t_shixun": 40
                //     }
                // }
                // this.tableData = records
                // console.log("records=>",records)
            })
        })
        }
        
        
      },
      openChengji(row) {
        console.log("openChengji",row)
        this.studentId = row.id;
       // this.course = row;
         this.dialogChengjiVisible = true;
        //this.courseExamFlag = true;
      },
      handlePublish(){
        this.$confirm("是否确认发布学生成绩?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(() => {
        publishScore({
                clazzId:this.clazzId,
                courseId:this.courseId
            }).then(res=>{
               
            })
      });
      },
      async studentScore(){
            var that = this

            var settings = await this.getSetting()
            this.$refs.crud.option.column[4].label = '测验成绩('+settings['t_exam']+'%)'
            this.$refs.crud.option.column[5].label = '课堂成绩('+settings['t_kpoint_study']+'%)'
            this.$refs.crud.option.column[6].label = '实训成绩('+settings['t_shixun']+'%)'

            pinfenStatisticByStudent({
                clazzId:this.clazzId,
                courseId:this.courseId
            }).then(res=>{
                console.log("pinfenStatisticByStudent",res.data)
                res.data.data.map(item=>{
                    if(item.kpointStudyScore){
                        item.kpointStudyScore = ((Number(item.kpointStudyScore)/5)*Number(settings['t_kpoint_study'])).toFixed(1)
                    }else{
                        item.kpointStudyScore =0
                    }
                    if(item.shixunScore){
                        item.shixunScore = (Number(item.shixunScore)*Number(settings['t_shixun'])).toFixed(1)
                    }else{
                        item.shixunScore =0
                    }
                    if(item.examScore){
                        item.examScore = (Number(item.examScore)*Number(settings['t_exam'])).toFixed(1)
                    }else{
                        item.examScore =0
                    }
                    item.lastScore = Number(item.kpointStudyScore+item.shixunScore+item.examScore).toFixed(1)
                })
                this.data = res.data.data;
                var cjMap = {}
            })
      },
      searchReset() {
        this.query = {};
        this.page.currentPage = 1;
        this.onLoad(this.page);
      },
      searchChange(params, done) {
        this.query = params;
        this.page.currentPage = 1;
        this.studentScore()
        //this.onLoad(this.page, params);
        done()
      },
      selectionChange(list) {
        this.selectionList = list;
      },
      refreshChange() {
        this.onLoad(this.page, this.query);
      },
      selectionClear() {
        this.selectionList = [];
        this.$refs.crud.toggleSelection();
      },
      currentChange(currentPage) {
        this.page.currentPage = currentPage;
      },
      sizeChange(pageSize) {
        this.page.pageSize = pageSize;
      },
      async getMyCourses(){
        return new Promise((resolve,reject)=>{
            getMyCourseList().then(res=>{

                console.log("getMyCourseList",res.data.data)
                resolve(res.data.data)
            })
        })
      },
      onLoad(page, params = {}) {
        this.loading = true;
        this.query.qstType = 6
        //this.query.kPointId = this.kpoint.id
  
        // if (typeof(this.query.subjectId) != "undefined"){
        //   this.query.subjectId = this.query.subjectId[1]
        // }
        // getList(page.currentPage, page.pageSize, Object.assign(params, this.query)).then(res => {
        //   const data = res.data.data;
        //   this.page.total = data.total;
        //   this.data = data.records;
        //   this.loading = false;
        //   this.selectionClear();
        // });
        this.loading = false;
        this.selectionClear();
      }
    },
    created: async function () {
      _this = this;
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
              _this.studentScore()
        });
      }
    },
  };
  </script>
  
  <style></style>
  