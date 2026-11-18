<template>
  <basic-container>
    <avue-crud
      :option="option"
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
      @on-load="onLoad"
    >
      <template slot-scope="scope" slot="menu">
         <el-button
          v-if="scope.row.completionStatus < 3"
          type="text"
          icon="el-icon-notebook-2"
          size="small"
          @click="editCourseKpoint(scope.row, 3)"
          >课程完结</el-button
        >
       <!-- <el-button
          v-if="scope.row.completionStatus == 3"
          type="text"
          icon="el-icon-notebook-2"
          size="small"
          @click="editCourseKpoint(scope.row, 4)"
          >学期完结</el-button
        > -->
      </template>

      <template slot-scope="scope" slot="menu">
         <!-- <el-button
        
          type="text"
          icon="el-icon-notebook-2"
          size="small"
          @click="editCourseKpoint(scope.row, 3)"
          >课程完结</el-button
        >
        <el-button
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
        <el-button type="text" size="small" icon="el-icon-delete" @click="rowDel(scope.row)" >删除</el-button> -->
      </template>


      <!-- <template slot-scope="scope" slot="mainTeacherIdForm">
        <el-tag v-if="tags.length == 1" closable @close="handleClose(tag)" v-for="tag in tags" :key="tag"
          :disable-transitions="false">{{ tag.name }}</el-tag>
        <el-button type="primary" icon="el-icon-circle-plus-outline" size="small" v-if="tags.length == 0"
          @click.stop="handleShowTeacher(scope.row)">选择班主任
        </el-button>
      </template> -->

      <template slot="teachers" slot-scope="scope">
        {{scope.row.teachers}}
        <el-button type="primary" size="mini"  @click="showTeacherPicker(scope.row)">选择老师</el-button>
      </template>

      <template slot="menuLeft">
        <el-button
          type="primary"
          size="small"
          icon="el-icon-plus"
          @click="handleAddCourse"
          >新增
        </el-button>
        <!-- <el-button type="primary" size="small" @click="handleStartCourse"
          >全部开始
        </el-button>-->
        <!-- <el-button type="danger" size="small" @click="handleEndCourse"
          >学期完结
        </el-button>  -->
      </template>
    </avue-crud>
    <el-dialog
      title="班级课程添加"
      width="80%"
      :visible.sync="dialogCourseVisible"
      @close="dialogCourse"
      append-to-body
    >
      <addcourse @adddata="adddata" v-if="addcourseFlag"></addcourse>
    </el-dialog>

    <el-dialog title="讲师列表" width="70%" :visible.sync="dialogTeacherVisible" append-to-body>
      <teacher :multiple="true" v-if="dialogTeacherVisible" @datasubmit="teacherdataSubmit"></teacher>
    </el-dialog>

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
  endCoursePackages,
  completeCoursePackage,
  coursePage,
  syncStudentsCourse
} from "@/api/edu/coursepackage";
import { mapGetters } from "vuex";
import addcourse from "./addcourse";
import teacher from "./teacher";
export default {
  data() {
    return {
      addcourseFlag: false,
      dialogCourseVisible: false,
      form: {},
      selectCourse:{},
      query: { parentCourseId: this.courseId },
      loading: true,
      page: {
        pageSize: 10,
        currentPage: 1,
        total: 0,
      },
      dialogTeacherVisible:false,
      selectionList: [],
      option: {
        tip: false,
        border: true,
        align: "center",
        index: false,
        viewBtn: false,
        addBtn: false,
        editBtn:false,
        selection: false,
        editBtnText: "修改",
        column: [
          {
            label: "课程名称",
            prop: "courseName",
            addDisplay: false,
            editDisplay: false,
          },
          // {
          //   label: "课程类型",
          //   prop: "sellType",
          //   row: true,
          //   addDisplay: false,
          //   editDisplay: false,
          //   dataType: "string",
          //   dicUrl: "/api/crazy-system/dict/dictionary?code=course_type",
          //   props: {
          //     label: "dictValue",
          //     value: "dictKey",
          //   },
          // },
          {
            label: "教师",
            prop: "teachers",
            addDisplay: false,
            editDisplay: false,
            slot:true,
          },
          // {
          //     label: "选择直播房间",
          //     prop: "chooseVideo",
          //     formslot:true,
          //     display: false,
          //     span: 24,
          //     hide: true,
          //   },
          {
            label: "排序",
            prop: "sort",
            type: "number",
          },
          
          
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
          //   label: "同步学员数",
          //   prop: "joinStudentNum",
          //   type:"number"
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
    showTeacherPicker(e){
      console.log("showTeacherPicker",e)
      this.selectCourse =  e
      this.dialogTeacherVisible = true
    },
    teacherdataSubmit(selection) {
      var that = this
      console.log("teacherdataSubmit",selection)
      // this.form.mainTeacherId = selection.id;
      // this.tags[0] = { name: selection.name };
      var ids = []
      var names = []
      selection.map(item=>{
        ids.push(item.id)
        names.push(item.name)
      })
      this.selectCourse.teacherIds = ids.join(",")
      this.selectCourse.teachers = names.join(",")
      update(this.selectCourse).then(res=>{
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
          parentCourseId: this.courseId,
          courseId: ele.id,
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
      row.parentCourseId = this.courseId;

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
      this.query.parentCourseId =  this.courseId
      coursePage(
        Object.assign(params, this.query)
      ).then((res) => {
        const data = res.data.data;
        this.page.total = data.length;
        this.data = data;
        this.loading = false;
        this.selectionClear();
      });
    },
  },
  components: {
    addcourse: addcourse,
    teacher:teacher
  },
};
</script>

<style>
</style>
