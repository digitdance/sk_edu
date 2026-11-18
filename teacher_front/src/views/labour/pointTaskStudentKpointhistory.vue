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
      <template slot="attachment" slot-scope="scope">
        <!-- <img :src="scope.rows.attachment" alt="缩略图" /> -->
        <a target="_blank" :href="scope.row.attachmentUrl">{{
          scope.row.attachment
        }}</a>
      </template>

      <template slot-scope="scope" slot="menu">
        <el-button type="text">评分</el-button>
      </template>

      <template slot="menuLeft">
        <el-button
          type="warning"
          size="small"
          plain
          icon="el-icon-download"
          @click="handleExport"
          >导出
        </el-button>
      </template>
    </avue-crud>
  </basic-container>
</template>

<script>
import {
  getList,
  getDetail,
  add,
  update,
  remove,
} from "@/api/task/courseTaskStudentKpointhistory";
import { mapGetters } from "vuex";
import { getToken } from "@/util/auth";
export default {
  data() {
    return {
      form: {},
      query: {
        courseId: this.courseId,
        popeType: this.popeType,
        kpointId: this.kpointId,
        courseType: this.courseType,
        kpointCourseId: this.kpointCourseId,
      },
      loading: true,
      page: {
        pageSize: 10,
        currentPage: 1,
        total: 0,
      },
      selectionList: [],
      option: {
        tip: false,
        tree: true,
        border: true,
        align: "center",
        index: false,
        addBtn: false,
        viewBtn: true,
        selection: false,
        menu: true,
        defaultExpandAll: true,
        column: [
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
            label: "账号",
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
            label: "真实姓名",
            prop: "realName",
            search: true,
            rules: [
              {
                required: true,
                message: "请输入真实姓名",
                trigger: "blur",
              },
            ],
          },
          {
            label: "任务名称",
            prop: "courseName",
          },

          {
            label: "是否完成任务",
            prop: "complete",
            dicData: [
              {
                label: "完成",
                value: "1",
              },
              {
                label: "未完成",
                value: "0",
              },
            ],
          },
          {
            label: "缩略图",
            prop: "attachment",
            type: "upload",
            clearable: true, //图片是否可被删除、替换
            loadText: " 图片上传中，请稍等 ",
            limit: 9,
            listType: "picture-card",
            span: 24,
            propsHttp: {
              res: "data",
            },
            tip: "上传缩略图，且不超过10M",
          },
          // {
          //   label: "操作",
          //   prop: "action",
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
        // addBtn: this.vaildData(this.permission.coursestudyhistory_add, false),
        // viewBtn: this.vaildData(this.permission.coursestudyhistory_view, false),
        // delBtn: this.vaildData(
        //   this.permission.coursestudyhistory_delete,
        //   false
        // ),
        // editBtn: this.vaildData(this.permission.coursestudyhistory_edit, false),
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
    popeType: String,
    kpointId: Number,
    courseType: String,
    kpointCourseId: Number,
  },
  watch: {
    courseId: {
      deep: true,
      handler(newVal) {
        this.query.courseId = newVal;
        this.onLoad(this.page);
      },
    },
    popeType: {
      deep: true,
      handler(newVal) {
        this.query.popeType = newVal;
        this.onLoad(this.page);
      },
    },
    courseType: {
      deep: true,
      handler(newVal) {
        this.query.courseType = newVal;
        this.onLoad(this.page);
      },
    },
    kpointCourseId: {
      deep: true,
      handler(newVal) {
        this.query.kpointCourseId = newVal;
        this.onLoad(this.page);
      },
    },
    kpointId: {
      deep: true,
      handler(newVal) {
        this.query.kpointId = newVal;
        this.onLoad(this.page);
      },
    },
  },
  methods: {
    handleExport() {
      this.$confirm("是否导出用户数据?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(() => {
        window.open(
          `/api/edu/coursestudyhistory/exportKpointIdUser?crazy-auth=${getToken()}` +
            `&courseId=` +
            this.courseId +
            `&popeType=` +
            this.popeType +
            `&kpointId=` +
            this.kpointId +
            `&courseType=` +
            this.courseType +
            `&kpointCourseId=` +
            this.kpointCourseId
        );
      });
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
      this.query = {
        courseId: this.courseId,
        popeType: this.popeType,
        kpointId: this.kpointId,
        courseType: this.courseType,
        kpointCourseId: this.kpointCourseId,
      };
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
    onLoad(
      page,
      params = {
        courseId: this.courseId,
        popeType: this.popeType,
        kpointId: this.kpointId,
        courseType: this.courseType,
        kpointCourseId: this.kpointCourseId,
      }
    ) {
      this.loading = true;
      getList(
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
