<template>
  <basic-container>
    <avue-crud
      :option="option"
      :table-loading="loading"
      :data="data"
      :page="page"
      :permission="permissionList"
      :before-open="beforeOpen"
      :upload-before="uploadBefore"
      :upload-after="uploadAfter"
      :upload-preview="uploadPreview"
      v-model="form"
      ref="crud"
      @row-update="rowUpdate"
      @row-save="rowSave"
      @row-del="rowDel"
      @search-change="searchChange"
      @selection-change="selectionChange"
      @current-change="currentChange"
      @size-change="sizeChange"
      @refresh-change="refreshChange"
      @on-load="onLoad"
    >
      <template slot-scope="scope" slot="taskIdForm">
        <el-divider></el-divider>
        <el-select
          v-model="scope.row.taskId"
          remote
          filterable
          placeholder="请选择"
          :remote-method="remoteMethod"
          :loading="searching"
          reserve-keyword
        >
          <el-option
            v-for="item in tasks"
            :key="item.id"
            :label="item.title"
            :value="item.id"
          >
          </el-option>
        </el-select>
      </template>
      <template slot-scope="scope" slot="menu">
        <el-button
          type="text"
          size="small"
          icon="el-icon-view"
          @click="playVideo(scope.row)"
          >预览</el-button
        >
        <el-button
          type="text"
          size="small"
          icon="el-icon-edit"
          @click="handleEdit(scope.row, scope.index)"
          >编辑</el-button
        >
        <el-button
          type="text"
          size="small"
          icon="el-icon-delete"
          @click="rowDel(scope.row)"
          >删除</el-button
        >
      </template>
    </avue-crud>
  </basic-container>
</template>


<script>
//import { getList, add, update, remove } from "@/api/edu/courseHomework";
import { getList, add, update, remove } from "@/api/task/taskMiddle";
import { getList as getTaskList } from "@/api/task/task";

export default {
  data() {
    return {
      query: {
        kpointId: this.kpointId,
        courseId: this.courseId,
      },
      tasks: [],
      loading: true,
      searching: false,
      page: {
        pageSize: 10,
        currentPage: 1,
        total: 0,
      },
      form: {
        imgUrl: [],
      },
      data: [],
      option: {
        align: "center",
        menuAlign: "center",
        editBtn: false,
        delBtn: false,
        column: [
          // {
          //   label: "作业类型",
          //   prop: "homeworkType",
          //   type: "radio",
          //   row: true,
          //   hide: true,
          //   addDisplay: false,
          //   editDisplay: false,
          //   dataType: "string",
          //   value: "1",
          //   dicUrl: "/api/crazy-system/dict/dictionary?code=homework_type",
          //   props: {
          //     label: "dictValue",
          //     value: "dictKey",
          //   },
          //   rules: [
          //     {
          //       required: true,
          //       message: "请输入作业类型",
          //       trigger: "blur",
          //     },
          //   ],
          // },
          {
            label: "任务ID",
            prop: "taskId",
            row: false,
            formslot: true,
            search: false,
            span: 15,
            rules: [
              {
                required: true,
                message: "请输入任务",
                trigger: "blur",
              },
            ],
          },
          {
            label: "任务",
            prop: "taskName",
            search: false,
            addDisplay: false, //当前行数据在新增表单中是否可见
            editDisplay: false, //当前行数据在编辑表单中是否可见
            span: 15,
          },

          // {
          //   label: "作业详情",
          //   prop: "context",
          //   type: "ueditor",
          //   component: "avueUeditor",
          //   options: {
          //     //普通图片上传
          //     action: "/api/common/upload?path=course",
          //     props: {
          //       res: "data",
          //       url: "url",
          //     },
          //   },
          //   span: 24,
          //   hide: true,
          // },
          {
            label: "排序",
            prop: "sort",
            type: "number",
            value: 1,
            span: 15,
            rules: [
              {
                required: true,
                message: "请输入显示排序",
                trigger: "blur",
              },
            ],
          },
          {
            label: "分值",
            prop: "score",
            type: "number",
            value: 1,
            span: 15,
            rules: [
              {
                required: true,
                message: "请输入分值",
                trigger: "blur",
              },
            ],
          },
          {
            label: "作业提交",
            prop: "postHomework",
            type: "switch",
            value: 2,
            dicData: [
              {
                label: "否",
                value: 2,
              },
              {
                label: "是",
                value: 1,
              },
            ],
          },
          {
            label: "附件上传",
            prop: "attachments",
            type: "upload",
            span: 24,
            limit: 1,
            drag: true,
            showColumn: false,
            propsHttp: {
              res: "data",
              name: "fileName",
              url: "url",
            },
            action: "/api/edu/coursematerial/fileUpload",
          },
        ],
      },
    };
  },
  props: {
    kpointId: Number,
    courseId: Number,
    platform: {
      type: Number,
      default: "",
    },
  },
  watch: {
    kpointId: {
      deep: true,
      handler(newVal) {
        this.query.kpointId = newVal;
        this.onLoad(this.page);
      },
    },
    courseId: {
      deep: true,
      handler(newVal) {
        this.query.courseId = newVal;
        this.onLoad(this.page);
      },
    },
  },
  methods: {
    remoteMethod(e) {
      console.log("remoteMethod", e);
      var param = {
        title: e,
      };
      this.searching = true;
      getTaskList(1, 100, param).then((res) => {
        this.searching = false;
        this.tasks = res.data.data.records;
        console.log("getTaskList", res.data.data.records);
      });
    },
    selectionChange(list) {
      this.selectionList = list;
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
      let paramsNew = {
        kpointId: this.kpointId,
        courseId: this.courseId,
      };
      getList(
        page.currentPage,
        page.pageSize,
        Object.assign(paramsNew, this.query)
      ).then((res) => {
        const data = res.data.data;
        this.page.total = data.total;
        this.data = data.records;
        this.loading = false;
        this.selectionClear();
      });

      var platform = this.platform;
      getTaskList(1, 100, {
        //  platform: platform,
      }).then((res) => {
        this.tasks = res.data.data.records;
      });
    },
    selectionClear() {
      this.selectionList = [];
      this.$refs.crud.toggleSelection();
    },
    playVideo(row) {
      window.open(row.fileUrl);
    },
    searchChange(params, done) {
      let paramsNew = {
        kpointId: this.kpointId,
        kpointType: "2",
        name: params.name,
      };
      this.query = params;
      this.page.currentPage = 1;
      this.onLoad(this.page, paramsNew);
      done();
    },
    rowSave(row, loading, done) {
      let rowNew = {
        kpointId: this.kpointId,
        courseId: this.courseId,
        kpointType: "2",
        name: row.name,
        score: row.score,
        sort: row.sort,
        taskId: row.taskId,
      };
      if (row.attachments.length > 0) {
        rowNew.attachments = JSON.stringify(row.attachments);
      }
      add(rowNew).then(
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
      row = {
        id: this.form.id,
        name: row.name,
        content: row.content,
        score: row.score,
        taskId: row.taskId,
        sort: row.sort,
      };
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
    handleEdit(row, index) {
      this.$refs.crud.rowEdit(row, index);
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
    uploadPreview(file, column, done) {
      window.open(file.url);
    },
    uploadBefore(file, done, loading, column) {
      done(file);
    },
    uploadAfter(res, done, loading, column) {
      this.form.name = res.fileName;
      done();
    },
  },
};
</script>
