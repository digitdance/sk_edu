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
      <template slot="menuLeft">
        <el-button type="primary" size="small" @click="openStudent"
          >指定
        </el-button>
      </template>
      <template slot="menu" slot-scope="scope">
        <el-button  type="danger" size="small" @click="handleDelete(scope.row)"
          >移除
        </el-button>
      </template>
    </avue-crud>

    <el-dialog title="学生列表" width="80%" :visible.sync="dialogStudentVisible" append-to-body>
      <studentAssignClazz @datasubmit="dataSubmit" :coursePackageId="coursePackageId" v-if="dialogStudentVisible"></studentAssignClazz>
    </el-dialog>
  </basic-container>
</template>

<script>
import { getStudentList, getDetail, add, update, remove,removeStudentClazz } from "@/api/user/student";
import { mapGetters } from "vuex";

import studentAssignClazz from "./studentAssignClazz";
import {
  give
} from "@/api/edu/course";
const moment = require('moment');
export default {
  components: {
    studentAssignClazz: studentAssignClazz
  },
  data() {
    return {
      form: {},
      query: {},
      loading: true,
      dialogStudentVisible: false,
      page: {
        pageSize: 10,
        currentPage: 1,
        total: 0,
      },
      selectionList: [],
      option: {
        tip: false,
        border: true,
        align: "center",
        index: false,
        viewBtn: true,
        addBtn: false,
        menu:true,
        selection: true,
        column: [
          {
            label: "ID",
            prop: "id",
          },
          {
            label: "手机",
            prop: "mobile",
            search: true,
            rules: [
              {
                required: true,
                message: "请输入手机",
                trigger: "blur",
              },
            ],
          },
          {
            label: "邮箱",
            prop: "email",
            search: true,
            rules: [
              {
                required: true,
                message: "请输入邮箱",
                trigger: "blur",
              },
            ],
          },

          {
            label: "学号",
            prop: "userName",
            search: true,
            rules: [
              {
                required: true,
                message: "请输入账号",
                trigger: "blur",
              },
            ],
          },
          {
            label: "昵称",
            prop: "showName",
            search: true,
            rules: [
              {
                required: true,
                message: "请输入昵称",
                trigger: "blur",
              },
            ],
          },
          {
            label: "性别",
            prop: "sex",
            type: "radio",
            dataType: "number",
            value: 1,
            props: {
              label: "dictValue",
              value: "dictKey",
            },
            dicUrl: "/api/crazy-system/dict/dictionary?code=sex",
          },
          {
            label: "真实姓名",
            prop: "realName",
            search: true,
          },
          {
            label: "身份证号",
            prop: "idCardNo",
          },
          // {
          //   label: "加入时间",
          //   prop: "courseAuthTime"
          // },
          {
            label: "有效时间",
            prop: "courseAuthTime"
          },
          {
            label: "创建人",
            prop: "creater",
            type: "select",
            span: 24,
            search: true,
            multiple: false,
            props: {
              label: "name",
              value: "userName",
            },
            dicUrl: "/api/user/teacher/all",
            rules: [
              {
                required: true,
                message: "请选择创建人",
                trigger: "blur",
              },
            ],
          },

          // {
          //   label: "学校",
          //   prop: "schoolName",
          // },
          /*{
              label: "是否可用",
              prop: "isAvalible",
              type: "radio",
              dataType: "number",
              value: 2,
              props: {
                label: "dictValue",
                value: "dictKey"
              },
              dicUrl: "/api/crazy-system/dict/dictionary?code=yes_no",
            },*/
        ],
      },
      data: [],
    };
  },
  props:{
    coursePackageId:{
      type:Number,
      default:0
    }
  },
  computed: {
    ...mapGetters(["permission"]),
    permissionList() {
      return {
        addBtn: this.vaildData(this.permission.teacher_add, false),
        viewBtn: this.vaildData(this.permission.teacher_view, false),
        delBtn: this.vaildData(this.permission.teacher_delete, false),
        editBtn: this.vaildData(this.permission.teacher_edit, false),
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
  methods: {
    dataSubmit(selection) {
      let userIds = "";
      selection.forEach((ele) => {
        userIds += ele.id + ",";
      });
      userIds = userIds.substring(0, userIds.length - 1);
      give(userIds, this.coursePackageId).then(
        () => {
          this.dialogStudentVisible = false;
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
    openStudent(row) {
      this.courseId = row.id;
      this.dialogStudentVisible = true;
    },
    checkTimeBefore(row){
      if(moment().isBefore(moment(row.courseAuthTime))){
          return true
      }else{
        return false
      }
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
    handleSelect() {
      if (this.selectionList.length === 0) {
        this.$message.warning("请选择至少一条数据");
        return;
      }
      this.$emit("datasubmit", this.selectionList);
    },
    handleDelete(row) {
      row.courseId = this.coursePackageId
      this.$confirm("确定将该学生移出课程吗?", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          row.courseAuthTime=""
          return removeStudentClazz(row);
        })
        .then(() => {
          this.onLoad(this.page);
          this.$message({
            type: "success",
            message: "操作成功!",
          });
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
      this.query = {};
      this.page.currentPage = 1;
      this.onLoad(this.page);
    },
    searchChange(params, done) {
      this.query = params;
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
      this.query.courseId=this.coursePackageId
      getStudentList(
        page.currentPage,
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
  },
};
</script>

<style>
</style>
