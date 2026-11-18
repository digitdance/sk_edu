<template>
  <basic-container>
    <avue-crud :option="option" :table-loading="loading" :data="data" :page="page" :permission="permissionList"
      :before-open="beforeOpen" v-model="form" ref="crud" @row-update="rowUpdate" @row-save="rowSave" @row-del="rowDel"
      @search-change="searchChange" @search-reset="searchReset" @selection-change="selectionChange"
      @current-change="currentChange" @size-change="sizeChange" @refresh-change="refreshChange" @on-load="onLoad">
      <template slot-scope="scope" slot="menu">
        <el-button v-if="scope.row.completionStatus < 3" type="text" icon="el-icon-notebook-2" size="small"
          @click="editCourseKpoint(scope.row, 3)">课程完结</el-button>
        <!-- <el-button
          v-if="scope.row.completionStatus == 3"
          type="text"
          icon="el-icon-notebook-2"
          size="small"
          @click="editCourseKpoint(scope.row, 4)"
          >学期完结</el-button
        > -->
      </template>


      <template slot-scope="scope" slot="examNum">
        <el-button type="text" v-if="scope.row.examNum != -1" @click="showExamRecord(scope.row)">{{ scope.row.examNum
        }}人</el-button>
      </template>


      <template slot-scope="scope" slot="status">
        <el-switch :active-value="'1'" :inactive-value="'0'" el-switch active-text="进行中" inactive-text="未开始"
          @change="e => changeStatus(e, scope.row)" v-model="scope.row.status" active-color="#13ce66"
          inactive-color="#ff4949">
        </el-switch>
      </template>


      <template slot-scope="scope" slot="menu">
        <!-- <el-button icon="iconfont iconicon_doc" :size="size"  :type="type" @click="openExamRecord(scope.row)">考试记录</el-button>
        <el-button icon="el-icon-s-data" :size="size" :type="type" @click="openExamAnalysis(scope.row)">试卷分析</el-button>
        <el-button icon="el-icon-s-data" :size="size" :type="type" @click="openExamPigaiRecord(scope.row)">试卷批改</el-button> -->
        <!-- <el-button
        
          type="text"
          icon="el-icon-notebook-2"
          size="small"
          @click="editCourseKpoint(scope.row, 3)"
          >课程完结</el-button
        > -->
        <!-- <el-button
          type="text"
          icon="el-icon-notebook-1"
          size="small"
          @click="syncCourse2Students(scope.row)"
          >同步到学员</el-button> -->
        <!--  <el-button
          v-if="scope.row.completionStatus == 1||scope.row.completionStatus == ''"
          type="text"
          icon="el-icon-notebook-1"
          size="small"
          @click="editCourseKpoint(scope.row, 2)"
          >课程开始</el-button
        > -->
        <!-- <el-button type="text" size="small" icon="el-icon-view" @click="playVideo(scope.row)" >授课老师</el-button> -->
        <!-- <el-button type="text" size="small" icon="el-icon-edit" @click="handleEdit(scope.row,scope.index)" >编辑</el-button>
        <el-button type="text" size="small" icon="el-icon-delete" @click="rowDel(scope.row)" >删除</el-button>  -->
      </template>


      <!-- <template slot-scope="scope" slot="mainTeacherIdForm">
        <el-tag v-if="tags.length == 1" closable @close="handleClose(tag)" v-for="tag in tags" :key="tag"
          :disable-transitions="false">{{ tag.name }}</el-tag>
        <el-button type="primary" icon="el-icon-circle-plus-outline" size="small" v-if="tags.length == 0"
          @click.stop="handleShowTeacher(scope.row)">选择班主任
        </el-button>
      </template> -->

      <!-- <template slot="teachers" slot-scope="scope">
        {{scope.row.teachers}}
        <el-button type="primary" size="mini"  @click="showTeacherPicker(scope.row)">选择老师</el-button>
      </template> -->

      <template slot="menuLeft">
        <el-button type="primary" size="small" icon="el-icon-plus" @click="handleAddCourse">新增
        </el-button>
        <!-- <el-button type="primary" size="small" @click="handleStartCourse"
          >全部开始
        </el-button>-->
        <!-- <el-button type="danger" size="small" @click="handleEndCourse"
          >学期完结
        </el-button>  -->
      </template>
    </avue-crud>
    <el-dialog title="考试添加" width="80%" :visible.sync="dialogCourseVisible" @close="dialogCourse" append-to-body>
      <addexam @adddata="adddata" v-if="addcourseFlag"></addexam>
    </el-dialog>




    <!-- <el-dialog
      title="考试记录"
      width="80%"
      :visible.sync="clazzexampaperrecordVisible"
      append-to-body
    >
      <clazzexampaperrecord
        v-if="clazzexampaperrecordVisible"
        :clazzId="clazzId"
        :exampaperId="exampaperId"
      ></clazzexampaperrecord>
    </el-dialog> -->



    <!-- <el-dialog class="el-scrollbar__wrap" title="试卷分析" :fullscreen="true" :visible.sync="dialogAnalysisVisible"
      style="padding-top: 0px" append-to-body>
      <exampaperAnalysis v-if="dialogAnalysisVisible" :exam="exam" :clazzId="clazzId"></exampaperAnalysis>
    </el-dialog>
    <el-dialog class="el-scrollbar__wrap" title="考试记录" :fullscreen="true" :visible.sync="dialogRecordVisible"
      style="padding-top: 0px" append-to-body>
      <exampaperrecordById v-if="dialogRecordVisible" :exam="exam" :clazzId="clazzId"></exampaperrecordById>
    </el-dialog>
    <el-dialog class="el-scrollbar__wrap" title="试卷批改" :fullscreen="true" :visible.sync="dialogRecordPigaiVisible"
      style="padding-top: 0px" append-to-body>
      <exampaperrecordByIdPigai v-if="dialogRecordPigaiVisible" :exam="exam" :clazzId="clazzId"></exampaperrecordByIdPigai>
    </el-dialog> -->


  </basic-container>
</template>

<script>
import {
  getList,
  getDetail,
  add,
  update,
  remove,
  addList,
  startCoursePackages,
  updateStatus,
  endCoursePackages,
  completeCoursePackage,
  examPage,
  syncStudentsCourse
} from "@/api/edu/coursepackageExam";
import { mapGetters } from "vuex";
import addexam from "./addexam";

import clazzexampaperrecord from "../exam/clazzexampaperrecord";
import exampaperAnalysis from "../exam/exampaperAnalysis";
import exampaperrecordById from "../exam/exampaperrecordById";
import exampaperrecordByIdPigai from "../exam/exampaperrecordByIdPigai";


export default {
  components: {
    addexam: addexam,
    // clazzexampaperrecord:clazzexampaperrecord,
    // exampaperAnalysis: exampaperAnalysis,
    // exampaperrecordById: exampaperrecordById,
    // exampaperrecordByIdPigai:exampaperrecordByIdPigai
  },
  data() {
    return {
      dialogRecordVisible: false,
      exam: {},
      type: "text",
      dialogAnalysisVisible: false,
      dialogComposeVisible: false,
      //dialogAutoComposeVisible:false,
      dialogAutoComposeVisible: false,
      dialogRecordPigaiVisible: false,

      addcourseFlag: false,
      dialogCourseVisible: false,
      clazzexampaperrecordVisible: false,
      form: {},
      clazzId: "",
      exampaperId: "",
      selectCourse: {},
      query: {},
      loading: true,
      page: {
        pageSize: 10,
        currentPage: 1,
        total: 0,
      },
      dialogTeacherVisible: false,
      selectionList: [],
      option: {
        tip: false,
        border: true,
        align: "center",
        index: false,
        viewBtn: false,
        addBtn: false,
        editBtn: true,
        selection: false,
        editBtnText: "修改",
        column: [
          {
            label: "试卷名称",
            prop: "customExampaperName",
          },
          {
            label: "标准试卷",
            prop: "examName",
          },

          {
            label: "试卷类型",
            search: true,
            prop: "examType",
            addDisplay: false,
            editDisplay: false,
            type: "select",
            dicUrl: "/api/exam/papertype/dictionary",
            props: {
              label: "title",
              value: "id",
            },
            rules: [
              {
                required: true,
                message: "请输入试卷类型",
                trigger: "blur",
              },
            ],
          },
          // {
          //   label: "考题分类",
          //   prop: "examSubjectId",
          //   type: "cascader",
          //   addDisplay:false,
          //   editDisplay:false,
          //   search: true,
          //   formslot: true,
          //   dicUrl: "/api/edu/subject/exam/tree",
          //   props: {
          //     label: "title",
          //   },
          //   rules: [
          //     {
          //       required: true,
          //       message: "请选择考题分类",
          //       trigger: "blur",
          //     },
          //   ],
          // },
          // {
          //   label: "教师",
          //   prop: "teachers",
          //   addDisplay: false,
          //   editDisplay: false,
          //   slot:true,
          // },
          // {
          //     label: "选择直播房间",
          //     prop: "chooseVideo",
          //     formslot:true,
          //     display: false,
          //     span: 24,
          //     hide: true,
          //   },
          // {
          //   label: "排序",
          //   prop: "sort",
          //   type: "number",
          // },
          // {
          //     label: "状态",
          //     prop: "status",
          //     type: "string",
          //     addDisplay:false,
          //     editDisplay:false,
          //     slot:true,
          //     dicData:[
          //       {
          //         label:'未开始',
          //         value:'0'
          //       },{
          //         label:'进行中',
          //         value: '1'
          //       }],
          //     rules: [{
          //       required: false,
          //       message: "请输入0为默认完成，1为未考完",
          //       trigger: "blur"
          //     }]
          //   },

          // {
          //   label: "完结状态",
          //   prop: "completionStatus",
          //   formslot: true,
          //   dataType: "string",
          //   value: "1",
          //   row: true,
          //   dicUrl:
          //     "/api/crazy-system/dict/dictionary?code=course_complete_status",
          //   props: {
          //     label: "dictValue",
          //     value: "dictKey",
          //   },
          // },
          // {
          //   label: "完结时间",
          //   prop: "completionTime",
          // },
          // {
          //   label: "排序",
          //   prop: "sort",
          // },

          // {
          //   label: "考试人数",
          //   prop: "examNum",
          //   addDisplay:false,
          //   editDisplay:false,
          //   type:"number",
          //   slot:true
          // },
        ],
      },
      data: [],
    };
  },
  computed: {
    ...mapGetters(["permission"]),
    permissionList() {
      return {
        addBtn: this.vaildData(true, false),
        viewBtn: this.vaildData(true, false),
        delBtn: this.vaildData(true, false),
        editBtn: this.vaildData(true, false),
      };
    },
    ids() {
      let ids = [];
      this.selectionList.forEach((ele) => {
        ids.push(ele.id);
      });
      return ids.join(",");
    },
  },
  props: {
    courseId: Number,
    courseType: Number
  },
  watch: {
    courseId: {
      deep: true,
      handler(newVal) {
        this.query.courseId = newVal;
        this.onLoad(this.page);
      },
    },
  },
  methods: {
    openExamRecord(exam) {
      this.exam = {
        id: exam.exampaperId,
        clazzId: exam.clazzId
      };
      this.dialogRecordVisible = true;
    },
    openExamAnalysis(exam) {
      this.exam = {
        id: exam.exampaperId,
        clazzId: exam.clazzId
      };
      this.dialogAnalysisVisible = true;
    },
    openExamPigaiRecord(exam) {
      this.exam = {
        id: exam.exampaperId,
        clazzId: exam.clazzId
      };
      this.dialogRecordPigaiVisible = true;
    },
    showExamRecord(row) {
      this.clazzexampaperrecordVisible = true
      this.clazzId = row.clazzId
      this.exampaperId = row.exampaperId
    },
    changeStatus(e, row) {
      console.log("changeStatus", e)
      row.status = e
      updateStatus(row).then(
        () => {
          loading();
          this.onLoad(this.page);
          this.$message({
            type: "success",
            message: "操作成功!",
          });
        },
        (error) => {
          done();

          console.log(error);
        }
      );

    },
    showTeacherPicker(e) {
      console.log("showTeacherPicker", e)
      this.selectCourse = e
      this.dialogTeacherVisible = true
    },
    teacherdataSubmit(selection) {
      var that = this
      console.log("teacherdataSubmit", selection)
      // this.form.mainTeacherId = selection.id;
      // this.tags[0] = { name: selection.name };
      var ids = []
      var names = []
      selection.map(item => {
        ids.push(item.id)
        names.push(item.name)
      })
      this.selectCourse.teacherIds = ids.join(",")
      this.selectCourse.teachers = names.join(",")
      update(this.selectCourse).then(res => {
        that.onLoad(1);
        that.dialogTeacherVisible = false;
      })
      //this.selectCourse.teachers
    },
    editCourseKpoint(row, type) {
      if (type == 2) {
        this.$confirm("确定开始课程吗?", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        })
          .then(() => {
            return completeCoursePackage({
              id: row.id,
              completionStatus: type,
            });
          })
          .then(() => {
            this.onLoad(this.page);
            this.$message({
              type: "success",
              message: "操作成功!",
            });
            //this.$refs.crud.toggleSelection();
          });
      }
      if (type == 3) {
        this.$confirm("确定完结课程吗?", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        })
          .then(() => {
            return completeCoursePackage({
              id: row.id,
              completionStatus: type,
            });
          })
          .then(() => {
            this.onLoad(this.page);
            this.$message({
              type: "success",
              message: "操作成功!",
            });
            //this.$refs.crud.toggleSelection();
          });
      }
      if (type == 4) {
        this.$confirm("学期完结,当前所有学员将过期", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        })
          .then(() => {
            return completeCoursePackage({
              id: row.id,
              completionStatus: type,
            });
          })
          .then(() => {
            this.onLoad(this.page);
            this.$message({
              type: "success",
              message: "操作成功!",
            });
            //this.$refs.crud.toggleSelection();
          });
      }
    },
    dialogCourse() {
      this.addcourseFlag = false;
    },
    adddata(selectionList) {
      this.dialogCourseVisible = false;
      let list = [];
      selectionList.forEach((ele) => {
        list.push({
          courseId: this.courseId,
          exampaperId: ele.id,
          customExampaperName: ele.name,
        });
      });
      addList(list, this.courseId).then(
        () => {
          this.onLoad(this.page);
          this.$message({
            type: "success",
            message: "操作成功!",
          });
        },
        (error) => {
          console.log(error);
        }
      );
    },
    rowSave(row, loading, done) {
      row.courseId = this.courseId;

      add(row).then(
        () => {
          loading();
          this.onLoad(this.page);
          this.$message({
            type: "success",
            message: "操作成功!",
          });
        },
        (error) => {
          done();
          console.log(error);
        }
      );
    },
    rowUpdate(row, index, loading, done) {
      update(row).then(
        () => {
          loading();
          this.onLoad(this.page);
          this.$message({
            type: "success",
            message: "操作成功!",
          });
        },
        (error) => {
          done();
          console.log(error);
        }
      );
    },
    rowDel(row) {
      this.$confirm("确定将选择数据删除?", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          return remove(row.id);
        })
        .then(() => {
          this.onLoad(this.page);
          this.$message({
            type: "success",
            message: "操作成功!",
          });
        });
    },
    handleAddCourse() {
      this.dialogCourseVisible = true;
      this.addcourseFlag = true;
    },

    syncCourse2Students(row) {
      syncStudentsCourse({
        parentCourseId: this.courseId,
        courseId: row.courseId,
      }).then((res) => {
        this.onLoad(this.page);
        this.$message({
          type: "success",
          message: "操作成功!",
        });
      });
    },


    handleStartCourse() {
      startCoursePackages({
        parentCourseId: this.courseId,
      }).then((res) => {
        this.onLoad(this.page);
        this.$message({
          type: "success",
          message: "操作成功!",
        });
      });
    },
    handleEndCourse() {
      endCoursePackages({
        parentCourseId: this.courseId,
      }).then((res) => {
        this.onLoad(this.page);
        this.$message({
          type: "success",
          message: "操作成功!",
        });
      });

      this.dialogCourseVisible = true;
      this.addcourseFlag = true;
    },
    beforeOpen(done, type) {
      if (["edit", "view"].includes(type)) {
        getDetail(this.form.id).then((res) => {
          this.form = res.data.data;
        });
      }
      done();
    },
    searchReset() {
      this.page.currentPage = 1;
      this.onLoad(this.page);
    },
    searchChange(params, done) {
      this.page.currentPage = 1;
      this.onLoad(this.page, params);
      done();
    },
    selectionChange(list) {
      this.selectionList = list;
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
    refreshChange() {
      this.onLoad(this.page, this.query);
    },
    onLoad(page, params = {}) {
      this.loading = true;
      this.query.courseId = this.courseId
      getList(page.currentPage,
        page.pageSize,
        Object.assign(params, this.query)
      ).then((res) => {
        const data = res.data.data;
        this.page.total = data.total;
        this.data = data.records;
        this.loading = false;
        this.selectionClear();
      });
    },
  }
};
</script>

<style></style>
