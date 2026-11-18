<template>
  <basic-container>
    <avue-crud
      :option="option"
      :table-loading="loading"
      :data="data"
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
      @on-load="onLoad"
    >
      <template slot="menuLeft">
        <el-button
          type="danger"
          size="small"
          icon="el-icon-delete"
          plain
          v-if="permission.subject_delete"
          @click="handleDelete"
          >删 除
        </el-button>
      </template>
      <template slot-scope="scope" slot="menu">
        <el-button
          type="text"
          icon="el-icon-circle-plus-outline"
          size="small"
          v-if="scope.row.parentId == 0"
          @click.stop="handleAdd(scope.row, scope.index)"
          >新增
        </el-button>
      </template>

      <template slot-scope="scope" slot="menu">
        <el-button
          type="text"
          icon="el-icon-notebook-2"
          size="small"
          @click="stduyhistorysimb(scope.row)"
          >发放记录</el-button
        >
      </template>
    </avue-crud>

    <el-dialog
      title="发放管理"
      width="80%"
      :visible.sync="dialogReceiveVisible"
    >
      <medalreceive :medalId="medalId"></medalreceive>
    </el-dialog>
  </basic-container>
</template>

<script>
import medalreceive from "./medalreceive";
import { getList, getDetail, add, update, remove } from "@/api/edu/medal";
import { mapGetters } from "vuex";
import website from "@/config/website";
export default {
  data() {
    return {
      form: {},
      query: {},
      medalId: "",
      loading: true,
      page: {
        pageSize: 10,
        currentPage: 1,
        total: 0,
      },
      selectionList: [],
      dialogReceiveVisible: false,
      option: {
        menuWidth: 350,
        tip: false,
        tree: true,
        border: true,
        index: false,
        viewBtn: true,
        selection: true,
        columnBtn: false,
        refreshBtn: false,
        column: [
          {
            label: "勋章名称",
            prop: "medalName",
            span: 24,
            rules: [
              {
                required: true,
                message: "请输入勋章名称",
                trigger: "blur",
              },
            ],
          },
          {
            label: "图片",
            prop: "logo",
            type: "upload",
            dataType: "string",
            span: 24,
            action: "/api/common/upload/size?path=medal&width=200&height=200",
            tip: "请上传 200*200(长X宽)像素 的图片",
            propsHttp: {
              res: "data",
            },
            listType: "picture-img",
            rules: [
              {
                required: true,
                message: "请输入图片",
                trigger: "blur",
              },
            ],
          },
          {
            label: "所属租户",
            prop: "tenantId",
            type: "tree",
            dicUrl: "/api/crazy-system/tenant/select",
            addDisplay: false,
            editDisplay: false,
            viewDisplay: website.tenantMode,
            span: 24,
            props: {
              label: "tenantName",
              value: "tenantId",
            },
            hide: !website.tenantMode,
            search: website.tenantMode,
            rules: [
              {
                required: true,
                message: "请输入所属租户",
                trigger: "click",
              },
            ],
          },
          // {
          //   label: "阶段",
          //   prop: "jieduan",
          //   type: "radio",
          //   search: true,
          //   dataType: "number",
          //   value: 1,
          //   row: true,
          //   dicUrl: "/api/crazy-system/dict/dictionary?code=grade_jieduan",
          //   props: {
          //     label: "dictValue",
          //     value: "dictKey",
          //   },
          // },
          // {
          //   label: "上级类型",
          //   prop: "parentId",
          //   hide: true,
          //   type: "tree",
          //   span: 24,
          //   addDisplay: false,
          //   editDisplay: false,
          //   value: 0,
          //   dicUrl: "/api/edu/subject/tree",
          //   props: {
          //     label: "title",
          //   },
          // },
          // {
          //   label: "专业类型",
          //   prop: "type",
          //   type: "select",
          //   hide: true,
          //   addDisplay: false,
          //   editDisplay: false,
          //   viewDisplay: false,
          //   value: "course",
          //   props: {
          //     label: "dictValue",
          //     value: "dictKey",
          //   },
          //   dicUrl: "/api/crazy-system/dict/dictionary?code=subject_type",
          // },
          {
            label: "排序字段",
            prop: "sort",
            value: "0",
            span: 24,
            type: "number",
            rules: [
              {
                required: true,
                message: "请输入排序字段",
                trigger: "blur",
              },
            ],
          },
        ],
      },
      data: [],
    };
  },
  components: {
    medalreceive: medalreceive,
  },
  computed: {
    ...mapGetters(["permission"]),
    permissionList() {
      return {
        // addBtn: this.vaildData(this.permission.subject_add, false),
        // viewBtn: this.vaildData(this.permission.subject_view, false),
        // delBtn: this.vaildData(this.permission.subject_delete, false),
        // editBtn: this.vaildData(this.permission.subject_edit, false),
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
    stduyhistorysimb(e) {
      this.medalId = e.id;
      this.dialogReceiveVisible = true;
    },
    handleAdd(row) {
      this.$refs.crud.value.parentId = row.id;
      this.$refs.crud.option.column.filter((item) => {
        // if (item.prop === "parentId") {
        //   item.value = row.id;
        // }
        if (item.prop === "medalName") {
          item.value = "";
        }
      });
      this.$refs.crud.rowAdd();
    },
    rowSave(row, loading, done) {
      //row.type = "course";
      add(row).then(
        () => {
          loading();
          this.onLoad(this.page);
          this.$refs.crud.option.column.filter((item) => {
            // if (item.prop === "parentId") {
            //   item.value = "";
            // }
          });
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
    onLoad(page, params = {}) {
      this.loading = true;
      getList(
        page.currentPage,
        page.pageSize,
        Object.assign(params, this.query)
      ).then((res) => {
        console.log("res.data", res.data);
        const data = res.data.data.records;
        // this.page.total = data.total;
        this.data = data;
        this.loading = false;
        this.selectionClear();
      });
    },
  },
};
</script>

<style>
</style>
