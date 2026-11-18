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
      <template slot-scope="{ row }" slot="deptId">
        <el-tag>{{ row.deptName }}</el-tag>
      </template>
      <template slot-scope="{ row }" slot="subjectId">
        <el-tag>{{ row.subjectName }}</el-tag>
      </template>


      <template slot="menu" slot-scope="scope">
      
        <!-- <el-button
                type="text"
                size="small"
                icon="el-icon-edit"
                plain
                @click="showPwd(scope.row)"
                >修改密码
              </el-button> -->
        
      </template>

      <template slot="menuLeft">
        <el-button
          type="danger"
          size="small"
          icon="el-icon-delete"
          plain
          v-if="permission.teacher_delete"
          @click="handleDelete"
          >删 除
        </el-button>
      
        
      </template>
    </avue-crud>


    <el-dialog
      title="重置密码"
      width="30%"
      :visible.sync="dialogPwdFormVisible"
    >
      <el-form :model="pwdForm">
        <el-form-item label="新密码：" label-width="120px">
          <el-input
            v-model="pwdForm.password"
            placeholder="请输入密码"
            style="width: 70%"
            show-password
          ></el-input>
        </el-form-item>
        <el-form-item label="确认密码：" label-width="120px">
          <el-input
            v-model="pwdForm.passwords"
            placeholder="请在一次输入密码"
            style="width: 70%"
            show-password
          ></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="setPwd">确 定</el-button>
        <el-button @click="dialogPwdFormVisible = false">取 消</el-button>
      </div>
    </el-dialog>

  </basic-container>
</template>

<script>
import { getList, getDetail, add, update, remove } from "@/api/user/teacher";
import { mapGetters } from "vuex";
import { getDeptTree } from "@/api/system/dept";
import website from "@/config/website";
export default {
  data() {
    return {
      form: {
        tenantId: "000000",
      },
      query: {
        role: "6",
      },
      loading: true,
      page: {
        pageSize: 10,
        currentPage: 1,
        total: 0,
      },
      pwdForm: {
        teacherId: "",
        password: "",
        passwords: "",
      },
      dialogPwdFormVisible: false,
      selectionList: [],
      option: {
        tip: false,
        border: true,
        align: "center",
        index: false,
        viewBtn: false,
        selection: true,
        column: [
          {
            label: "ID",
            prop: "id",
            addDisplay: false,
            viewDisplay: false,
            editDisplay: false,
          },
          {
            label: "教师名称",
            prop: "name",
            search: true,
            span: 24,
            rules: [
              {
                required: true,
                message: "请输入教师名称",
                trigger: "blur",
              },
            ],
          },
          {
            label: "电话",
            prop: "mobile",
            search: true,
            span: 24,
            rules: [
              {
                required: true,
                message: "请输入教师电话",
                trigger: "blur",
              },
            ],
          },
          {
            label: "教师资历",
            prop: "education",
            type: "textarea",
            span: 24,
            rules: [
              {
                required: true,
                message: "请输入教师资历",
                trigger: "blur",
              },
            ],
          },

          // {
          //   label: "学校",
          //   prop: "deptId",
          //   type: "tree",
          //   multiple: false,
          //   addDisplay: false,
          //   dicData: [],
          //   props: {
          //     label: "title",
          //   },
          //   slot: true,
          //   checkStrictly: true,
          // },
          {
            label: "登录用户",
            prop: "userName",
            span: 12,
            rules: [
              {
                required: true,
                message: "请输入登录用户",
                trigger: "blur",
              },
            ],
          },
          {
            label: "登录密码",
            prop: "password",
            type:"password",
            span: 12,
            rules: [
              {
                required: true,
                message: "请输入登录密码",
                trigger: "blur",
              },
            ],
          },
          {
            label: "教师头衔",
            prop: "teaTitle",
            type: "select",
            dataType: "number",
            search: false,
            span: 24,
            props: {
              label: "dictValue",
              value: "dictKey",
            },
            dicUrl: "/api/crazy-system/dict/dictionary?code=tea_title",
            rules: [
              {
                required: true,
                message: "请输入教师头衔",
                trigger: "blur",
              },
            ],
          },
          {
            label: "头像",
            prop: "headImg",
            type: "upload",
            dataType: "string",
            span: 24,
            //action: "/api/common/upload/size?path=teacher&width=200&height=200",
            action: "/api/uploadOss?path=teacher&width=200&height=200",
            tip: "请上传 200*200(长X宽)像素 的图片",
            propsHttp: {
              res: "data",
            },
            listType: "picture-img",
            rules: [
              {
                required: true,
                message: "请输入头像",
                trigger: "blur",
              },
            ],
          },
          {
            label: "所属租户",
            prop: "tenantId",
            type: "tree",
            dicUrl: "/api/crazy-system/tenant/select",
            props: {
              label: "tenantName",
              value: "tenantId",
            },
            hide: !website.tenantMode,
            addDisplay: website.tenantMode,
            editDisplay: website.tenantMode,
            viewDisplay: website.tenantMode,
            search: false,
            rules: [
              {
                required: true,
                message: "请输入所属租户",
                trigger: "click",
              },
            ],
          },
          // {
          //   label: "来源",
          //   prop: "hasIm",
          //   type: "radio",
          //   span: 24,
          //   value: "0",
          //   dicData: [
          //     {
          //       label: "否",
          //       value: "0",
          //     },
          //     {
          //       label: "是",
          //       value: "1",
          //     },
          //   ],
          // },
          // {
          //   label: "年级课",
          //   prop: "subjectId",
          //   search: true,
          //   type: "select",
          //   span: 24,
          //   props: {
          //     label: "subjectName",
          //     value: "id",
          //   },
          //   dicUrl: "/api//edu/subject/subjectList?parentId=993&type=course",
          //   rules: [
          //     {
          //       required: true,
          //       message: "请输入专业id",
          //       trigger: "blur",
          //     },
          //   ],
          // },
          {
            label: "排序",
            prop: "sort",
            type: "number",
            value: 0,
          },
          {
            label: "教师简介",
            prop: "profile",
            type: "ueditor",
            component: "avueUeditor",
            hide: true,
            options: {
              //普通图片上传
              action: "/api/common/upload?path=teacher",
              props: {
                res: "data",
                url: "url",
              },
            },
            span: 24,
            rules: [
              {
                required: true,
                message: "请输入教师简介",
                trigger: "blur",
              },
            ],
          },
          {
            label: "创建时间",
            prop: "createTime",
            editDisplay: false,
            addDisplay: false,
            rules: [
              {
                required: true,
                message: "请输入创建时间",
                trigger: "blur",
              },
            ],
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
  watch: {
    "form.tenantId"() {
      console.log("form.tenantId change", this.form.tenantId);
      // if (this.form.tenantId !== "") {
      getDeptTree("000000").then((res) => {
        const column = this.findObject(this.option.column, "deptId");
        column.dicData = res.data.data;
      });
      //  }
    },
  },
  methods: {
    showPwd(row) {
      this.dialogPwdFormVisible = true;
      this.pwdForm.teacherId = row.id;
      this.pwdForm.password = "";
      this.pwdForm.passwords = "";
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
