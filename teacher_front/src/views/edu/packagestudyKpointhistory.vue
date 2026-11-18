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
        <el-button type="text" @click="openKpointStudyStudentHistory(scope.row)"
          >查看课程详情</el-button
        >
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
    <el-dialog
      title="查看章节全部学员详情"
      width="80%"
      :visible.sync="coursekpointstudyStudenthistory"
      append-to-body
    >
      <coursekpointstudentstudy
        v-if="coursekpointstudyStudenthistory"
        :courseId="courseIds"
        :courseType="courseType"
        :kpointCourseId="kpointCourseId"
      ></coursekpointstudentstudy>
    </el-dialog>
  </basic-container>
</template>

<script>

import {
    getMyCourseList,getMyClazzList
  } from "@/api/edu/course";
  

import {
  getList,
  getDetail,
  add,
  update,
  remove,
} from "@/api/edu/packagestudyKpointhistory";
import { mapGetters } from "vuex";
import coursekpointstudentstudy from "./coursestudyKpointhistory";
export default {
  data() {
    return {
      form: {},
      query: { courseId: this.courseId },
      loading: true,
      kpointId: 0,
      courseIds: 0,
      popeType: "",
      clazzId:"",
        clazzName:"",
        classes:[],
      courseType: "",
      kpointCourseId: 0,
      page: {
        pageSize: 10,
        currentPage: 1,
        total: 0,
      },
      coursekpointstudyStudenthistory: false,
      selectionList: [],
      option: {
        tip: false,
        border: true,
        align: "center",
        index: false,
        addBtn: false,
        viewBtn: true,
        selection: false,
        defaultExpandAll: true,
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
            label: "班级",
            prop: "clazzName",
          },
          {
            label: "课程名称",
            prop: "courseName",
          },
          // {
          //   label: "课程类型",
          //   prop: "popeType",
          //   formatter: function (row, value) {
          //     if (value == 1) {
          //       return "录播";
          //     } else if (value == 2) {
          //       return "直播";
          //     }
          //   },
          // },
          {
            label: "学习总次数",
            prop: "playercount",
            formatter: function (row, value) {
              if (value != -1) {
                return value + "次";
              } else {
                return "";
              }
            },
          },
          {
            label: "课程总时长",
            prop: "videoTime",
          },
          {
            label: "应学习总人数",
            prop: "studyPope",
          },
        ],
      },
      data: [],
    };
  },
  computed: {
    ...mapGetters(["permission"]),
    permissionList() {
      return {
        addBtn: this.vaildData(this.permission.coursestudyhistory_add, false),
        viewBtn: this.vaildData(this.permission.coursestudyhistory_view, false),
        delBtn: this.vaildData(
          this.permission.coursestudyhistory_delete,
          false
        ),
        editBtn: this.vaildData(this.permission.coursestudyhistory_edit, false),
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
        });
      }
    },
  methods: {
    async getMyCourses(){
        return new Promise((resolve,reject)=>{
            getMyCourseList().then(res=>{

                console.log("getMyCourseList",res.data.data)
                resolve(res.data.data)
            })
        })
      },
    openKpointStudyStudentHistory(row) {
      this.courseIds = row.courseId;
      this.courseType = "PACKAGE";
      this.kpointCourseId = row.kpointCourseId;
      this.coursekpointstudyStudenthistory = true;
    },
    rowSave(row, loading, done) {
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
    handleDelete() {
      if (this.selectionList.length === 0) {
        this.$message.warning("请选择至少一条数据");
        return;
      }
      this.$confirm("确定将选择数据删除?", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          return remove(this.ids);
        })
        .then(() => {
          this.onLoad(this.page);
          this.$message({
            type: "success",
            message: "操作成功!",
          });
          this.$refs.crud.toggleSelection();
        });
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
      this.query = { courseId: this.courseId };
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
    onLoad(page, params = { courseId: this.courseId }) {
      this.loading = true;
      getList(
        page.currentPage,
        page.pageSize,
        Object.assign(params, this.query)
      ).then((res) => {
        const data = res.data.data;
        /*this.page.total = data.total;*/
        this.data = data.records;
        this.loading = false;
        this.selectionClear();
      });
    },
  },
  components: {
    coursekpointstudentstudy: coursekpointstudentstudy,
  },
};
</script>

<style>
</style>
