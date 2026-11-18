<template>
  <basic-container>
    <avue-crud :search.sync="search" :option="option" :table-loading="loading" :data="data" :page="page"
      :permission="permissionList" :before-open="beforeOpen" v-model="form" ref="crud" @row-update="rowUpdate"
      @row-save="rowSave" @row-del="rowDel" @search-change="searchChange" @search-reset="searchReset"
      @selection-change="selectionChange" @current-change="currentChange" @size-change="sizeChange"
      @refresh-change="refreshChange" @on-load="onLoad">
      <!-- <template slot-scope="{ row }" slot="deptId">
        <el-tag>{{ row.deptName }}</el-tag>
      </template> -->
      <template slot="menuLeft">
        <el-button type="danger" size="small" icon="el-icon-delete" plain v-if="permission.course_delete"
          @click="handleDelete">删 除
        </el-button>
        <el-button type="success" size="small" plain icon="el-icon-upload2" @click="handleImport">批量指定
        </el-button>
      </template>
      <template slot-scope="scope" slot="menu">
        <el-button type="text" icon="el-icon-edit" size="small"
          @click="handleEdit(scope.row, scope.index)">编辑</el-button>
        <el-button type="text" icon="el-icon-delete" size="small" @click="handleDel(scope.row)">删除</el-button>
        <!-- <el-button type="text" icon="el-icon-notebook-2" size="small"
          @click="openCourseKpoint(scope.row)">选课</el-button> -->

        <!-- <el-button icon="el-icon-s-data" :size="size" :type="type" @click="openExamPigaiRecord(row)">实训管理</el-button> -->


        <!-- <el-button type="text" icon="el-icon-notebook-2" size="small"
          @click="openExamRecord(scope.row)">考情管理</el-button> -->
        <!-- <el-button type="text" icon="el-icon-paperclip" size="small"
          @click="openExamPigaiRecord(scope.row)">实训记录</el-button> -->


        <!-- <el-button type="text" icon="el-icon-notebook-2" size="small"
          @click="openExampaperRecord(scope.row)">做题记录</el-button> -->

        <!-- <el-button type="text" icon="el-icon-paperclip" size="small"
        @click="openExamPigaiRecord(scope.row)">实训记录</el-button> -->
<!-- 
        <el-button type="text" icon="el-icon-paperclip" size="small" @click="openChengjiEam(scope.row)">成绩统计</el-button> -->


        <!-- <el-button type="text" icon="el-icon-notebook-2" size="small" @click="openStudent(scope.row)">班级成员</el-button> -->
        <!-- <el-button type="text" icon="el-icon-notebook-2" size="small" @click="openComment(scope.row)">评论管理</el-button> -->
        <!-- <el-button type="text" icon="el-icon-notebook-2" size="small" @click="openNotice(scope.row)">公告管理</el-button> -->
        <!-- <el-button type="text" icon="el-icon-notebook-2" size="small"
          @click="stduyhistorysimb(scope.row)">学习记录</el-button>
        <el-button type="text" icon="el-icon-notebook-2" size="small" @click="stduyUsersimb(scope.row)">学情统计</el-button> -->
          <!-- <el-button type="text" icon="el-icon-notebook-2" size="small" @click="openAllExamPigaiRecord(scope.row)">试卷批改</el-button>  -->
        <!--<el-button type="text" icon="el-icon-notebook-2" size="small" @click="openChengji(scope.row)">成绩管理</el-button> -->
      </template>
      <template slot-scope="scope" slot="losetypeForm">
        <el-select v-model="scope.row.losetype" @change="change" placeholder="请选择">
          <el-option v-for="item in losetypeList" :key="item.dictKey" :label="item.dictValue" :value="item.dictKey">
          </el-option>
        </el-select>
      </template>
      <template slot-scope="scope" slot="mainTeacherIdForm">
        <el-tag v-if="tags.length == 1" closable @close="handleClose(tag)" v-for="tag in tags" :key="tag"
          :disable-transitions="false">{{ tag.name }}</el-tag>
        <el-button type="primary" icon="el-icon-circle-plus-outline" size="small" v-if="tags.length == 0"
          @click.stop="handleShowTeacher(scope.row)">选择班主任
        </el-button>
      </template>
      <template slot-scope="scope" slot="subjectIdForm">
        <el-cascader placeholder="考题分类" :options="subjectTree" v-model="form.subjectId"
          :props="{ label: 'title', emitPath: false }" filterable></el-cascader>
      </template>
      <template slot-scope="scope" slot="subjectIdSearch">
        <el-cascader placeholder="考题分类" :options="subjectTree" v-model="search.subjectId"
          :props="{ label: 'title', emitPath: false }" filterable clearable></el-cascader>
      </template>
    </avue-crud>
    <el-dialog title="班级成员" width="80%" :visible.sync="dialogStudyUser" append-to-body>
      <courseTaoCanStudyStudenthistory v-if="dialogStudyUser" :popeType="popeType" :courseId="courseId"
        :courseType="courseType"></courseTaoCanStudyStudenthistory>
    </el-dialog>

    <!-- <el-dialog title="班级成员" width="80%" :visible.sync="dialogStudyUser" append-to-body>
      <courseTaoCanStudyStudenthistory v-if="dialogStudyUser" :popeType="popeType" :courseId="courseId"
        :courseType="courseType"></courseTaoCanStudyStudenthistory>
    </el-dialog> -->
    <el-dialog class="el-scrollbar__wrap" title="考情记录" :fullscreen="true" :visible.sync="dialogRecordVisible"
      style="padding-top: 0px" append-to-body>
      <exampapers v-if="dialogRecordVisible" :courseInfo="course"></exampapers>
    </el-dialog>

    <el-dialog title="学员管理" width="80%" :visible.sync="dialogStudyUser" append-to-body>
      <courseTaoCanStudyStudenthistory v-if="dialogStudyUser" :popeType="popeType" :courseId="courseId"
        :courseType="courseType"></courseTaoCanStudyStudenthistory>
    </el-dialog>
    <el-dialog title="班级课程管理" width="80%" :visible.sync="dialogKpointVisible" @close="dialogKpoint">
      <coursepackage :courseId="courseId" v-if="coursepackageFlag"></coursepackage>
    </el-dialog>
    <el-dialog title="班级成员" width="80%" :visible.sync="dialogStudentVisible">
      <student2 @datasubmit="dataSubmit" :coursePackageId="courseId" v-if="dialogStudentVisible"></student2>
    </el-dialog>

    <el-dialog title="班级考试管理" width="80%" :visible.sync="dialogExamVisible" @close="dialogExamVisible == false">
      <coursepackageExam :courseId="courseId" v-if="coursepackageExamFlag"></coursepackageExam>
    </el-dialog>

    <!-- <el-dialog title="班级成员" width="80%" :visible.sync="dialogStudentVisible" append-to-body>
      <courseTaoCanStudyStudent v-if="dialogStudentVisible" :popeType="popeType" :courseId="courseId"
        :courseType="courseType"></courseTaoCanStudyStudent>
    </el-dialog> -->

    <el-dialog title="学习记录" width="80%" :visible.sync="dialogStudyHistory" append-to-body>
      <studyhistory v-if="dialogStudyHistory" :courseId="courseId"></studyhistory>
    </el-dialog>
    <el-dialog title="讲师列表" width="80%" :visible.sync="dialogTeacherVisible" append-to-body>
      <teacher v-if="dialogTeacherVisible" @datasubmit="teacherdataSubmit"></teacher>
    </el-dialog>
    <el-dialog title="留言管理" width="80%" :visible.sync="dialogCommentVisible" append-to-body>
      <comment v-if="dialogCommentVisible" :isTop="true" :courseId="courseId"></comment>
    </el-dialog>
    <el-dialog title="公告管理" width="80%" :visible.sync="noticeVisible" append-to-body>
      <notice v-if="noticeVisible" :courseId="courseId"></notice>
    </el-dialog>

    <el-dialog title="实训记录" :fullscreen="true" :visible.sync="dialogRecordPigaiVisible"
      style="padding-top: 0px;width:100%" append-to-body>
      <shicaoRecordQstByClazzPigai v-if="dialogRecordPigaiVisible" :clazzId="clazzId"></shicaoRecordQstByClazzPigai>
    </el-dialog>

    <el-dialog title="试卷批改" :fullscreen="true" :visible.sync="dialogAllRecordPigaiVisible"
      style="padding-top: 0px;width:100%" append-to-body>
      <!-- <exampaperrecordByIdPigai v-if="dialogRecordPigaiVisible" :exam="exam"></exampaperrecordByIdPigai>
       -->
      <examRecordQstByClazzPigai v-if="dialogAllRecordPigaiVisible" :clazzId="clazzId"></examRecordQstByClazzPigai>
    </el-dialog>


    <el-dialog title="做题记录" :fullscreen="true" :visible.sync="dialogRecordPaperVisible"
      style="padding-top: 0px;width:100%" append-to-body>
      <exampaperrecord v-if="dialogRecordPaperVisible" :clazzId="clazzId"></exampaperrecord>
    </el-dialog>

    <el-dialog title="成绩统计" :fullscreen="true" :visible.sync="dialogOrderStatisticsVisible"
      style="padding-top: 0px;width:100%" append-to-body>
      <chengjiStatistics v-if="dialogOrderStatisticsVisible" :clazzId="clazzId"></chengjiStatistics>
    </el-dialog>

    <el-dialog title="成绩管理" width="80%" :visible.sync="dialogChengjiVisible" @close="dialogChengjiVisible == false">
      <el-tabs v-model="activeName" @tab-click="handleClick">
        <el-tab-pane label="随堂测验" name="first">
          <exampapers :courseInfo="course" examType="4"></exampapers>
        </el-tab-pane>
        <el-tab-pane label="实训任务批改" name="second">
          <shicaoRecordQstByClazzPigai :clazzId="clazzId"></shicaoRecordQstByClazzPigai>
        </el-tab-pane>
        <el-tab-pane label="综合考试" name="third">
          <exampapers :courseInfo="course" examType="2"></exampapers>
        </el-tab-pane>
        <el-tab-pane label="总成绩" name="fourth">
          <chengjiStatistics :courseInfo="course"></chengjiStatistics>
        </el-tab-pane>
      </el-tabs>
    </el-dialog>

    <el-dialog title="批量分配班级导入" append-to-body :visible.sync="excelBox" width="555px">
      <avue-form :option="excelOption" v-model="excelForm" :upload-after="uploadAfter">
        <template slot="excelTemplate">
          <el-button type="primary" @click="handleTemplate()">
            点击下载<i class="el-icon-download el-icon--right"></i>
          </el-button>
        </template>
      </avue-form>
      <el-row v-if="errorMsg != ''" style="top: -45px; left: 28px">
        <div>错误信息：</div>
        <el-col :span="2"></el-col>
        <el-col :span="20" style="color: #f56c6c">
          <div style="
                      margin: 15px 0 0 -35px;
                      padding-left: 19px;
                      overflow: auto;
                      height: 150px;
                      width: 420px;
                      border-radius: 5px;
                      border: #d9d9d9 1px dashed;
                    " v-html="errorMsg"></div>
        </el-col>
      </el-row>
    </el-dialog>

    <!-- <el-dialog title="消息发送" :visible.sync="noticeVisible" width="555px">
      <el-form :model="noticeForm">
        <el-form-item label="通知内容" label-width="140px" >
          <el-input type="textarea" v-model="noticeForm.content" placeholder="" style="width: 70%;" ></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="sendNotice">确 定</el-button>
        <el-button @click="noticeVisible = false" >取 消</el-button>
      </div>
    </el-dialog> -->

  </basic-container>
</template>

<script>
import {
  getMyClazzList,
  getDetail,
  add,
  update,
  remove,
  getEndTimeType,
  give,
  getTeacherDetail,
  subjectTree,
} from "@/api/edu/course";
import { mapGetters } from "vuex";
import coursepackage from "./coursepackage";
import coursepackageExam from "./coursepackageExam";
import student2 from "./student2";
import teacher from "./teacher";
import comment from "./comment";
import notice from "./notice";
import website from "@/config/website";
import { getDeptTree } from "@/api/system/dept";
import { add as addNotice } from "@/api/dept/notice";
import studyhistory from "./packagestudyKpointhistory";
import courseTaoCanStudyStudenthistory from "@/views/edu/courseTaoCanStudyStudenthistory";
import courseTaoCanStudyStudent from "@/views/edu/courseTaoCanStudyStudent";
import exampapers from "@/views/exam/exampapers";
import shicaoRecordQstByClazzPigai from "../exam/shicaoRecordQstByClazzPigai";
import exampaperrecord from "../exam/exampaperrecord";
import chengjiStatistics from "@/views/edu/chengjiStatistics";
import examRecordQstByClazzPigai from "../exam/examRecordQstByClazzPigai";

//https://fire.zhaoanyun.com/video/videoAlarmLog/alarm
export default {

  data() {
    return {
      errorMsg: "",
      courseExamFlag:true,
      dialogAllRecordPigaiVisible:false,
      dialogChengjiVisible:false,
      excelBox: false,
      search: {},
      course: {},
      noticeForm: {},
      dialogOrderStatisticsVisible: false,
      subjectTree: [],
      dialogExamVisible: false,
      dialogTeacherVisible: false,
      dialogCommentVisible: false,
      noticeVisible: false,
      dialogStudyHistory: false,
      dialogRecordVisible: false,
      clazzId: 0,
      dialogRecordPaperVisible: false,
      dialogRecordPigaiVisible: false,
      tags: [],
      dialogStudentVisible: false,
      dialogStudyUser: false,
      coursepackageFlag: false,
      losetypeList: [],
      courseId: "",
      dialogKpointVisible: false,
      form: {},
      qstId: 0,
      query: { sellType: "3" },
      loading: true,
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
        viewBtn: false,
        delBtn: false,
        editBtn: false,
        selection: true,
        labelWidth: 150,
        dialogClickModal: false,
        column: [
          {
            label: "ID",
            prop: "id",
            addDisplay: false,
            search: false,
            editDisplay: false,
          },
          {
            label: "班级名称",
            prop: "courseName",
            search: true,
            span: 24,
            rules: [
              {
                required: true,
                message: "请输入班级名称",
                trigger: "blur",
              },
            ],
          },
          // {
          //   label: "班级考题分类",
          //   prop: "subjectId",
          //   type: "cascader",
          //   tip: "班级分类只可以选择二级分类",
          //   placeholder: "请输入要选择的专业，支持搜索",
          //   filterable: true,
          //   searchLabelWidth: 110,
          //   span: 24,
          //   search: true,
          //   formslot: true,
          //   searchslot: true,
          //   dicUrl: "/api/edu/subject/tree",
          //   props: {
          //     label: "title",
          //   },
          //   rules: [
          //     {
          //       required: true,
          //       message: "请输入班级考题分类",
          //       trigger: "blur",
          //     },
          //   ],
          // },
          // {
          //   label: "班级原价格",
          //   prop: "sourcePrice",
          //   type: "number",
          //   tip: "只显示价格",
          //   precision: 2,
          //   rules: [
          //     {
          //       required: true,
          //       message: "请输入班级原价格",
          //       trigger: "blur",
          //     },
          //   ],
          // },
          // {
          //   label: "班级销售价格",
          //   prop: "currentPrice",
          //   type: "number",
          //   precision: 2,
          //   tip: "（实际支付价格）设置为0则可免费观看",
          //   rules: [
          //     {
          //       required: true,
          //       message: "请输入班级销售价格",
          //       trigger: "blur",
          //     },
          //   ],
          // },
          {
            label: "班级简介",
            prop: "title",
            type: "textarea",
            span: 24,
            hide: true,
            rules: [
              {
                required: true,
                message: "请输入班级简介",
                trigger: "blur",
              },
            ],
          },
          {
            label: "班级详情",
            prop: "context",
            type: "ueditor",
            component: "avueUeditor",
            options: {
              //普通图片上传
              // action: "/api/common/upload?path=course",
              action: "/api/uploadOss?path=course",
              props: {
                res: "data",
                url: "url",
              },
            },
            span: 24,
            hide: true,
          },
          {
            label: "图片",
            prop: "logo",
            hide: true,
            type: "upload",
            dataType: "string",
            span: 24,
            // action: "/api/common/upload/size?path=course&width=570&height=312",
            action: "/api/uploadOss?path=course",
            propsHttp: {
              res: "data",
            },
            listType: "picture-img",
            tip: "建议图片像素：570*312",
            rules: [
              {
                required: false,
                message: "请输入图片路径",
                trigger: "blur",
              },
            ],
          },
          // {
          //   label: "销售数量",
          //   addDisplay: false,
          //   editDisplay: false,
          //   prop: "pageBuycount",
          //   type: "number",
          // },
          // {
          //   label: "学习人数",
          //   prop: "bogusBuycount",
          //   hide: true,
          //   type: "number",
          // },
          // {
          //   label: "评论数",
          //   prop: "commentNum",
          //   addDisplay: false,
          //   editDisplay: false,
          //   type: "number",
          // },
          // {
          //   label: "学校",
          //   prop: "deptId",
          //   type: "tree",
          //   multiple: false,
          //   addDisplay: false,
          //   editDisplay: false,
          //   dicData: [],
          //   props: {
          //     label: "title",
          //   },
          //   slot: true,
          //   checkStrictly: true,
          // },
          // {
          //   label: "浏览数量",
          //   prop: "pageViewcount",
          //   addDisplay: false,
          //   editDisplay: false,
          //   type: "number",
          // },
          // {
          //   label: "假浏览量",
          //   prop: "bogusViewcount",
          //   hide: true,
          //   type: "number",
          // },
          {
            label: "有效期类型",
            prop: "losetype",
            formslot: true,
            addDisplay: false,
            hide: true,
            editDisplay: false,
            dataType: "string",
            value: "0",
            row: true,
            dicUrl: "/api/crazy-system/dict/dictionary?code=end_time_type",
            props: {
              label: "dictValue",
              value: "dictKey",
            },
          },
          {
            label: "班级有效期",
            prop: "loseTime",
            type: "number",
            hide: true,
            addDisplay: false,
            editDisplay: false,
            tip: "商品订单过期时间点",
            rules: [
              {
                required: true,
                message: "请输入班级有效期",
                trigger: "blur",
              },
            ],
          },
          {
            label: "有效结束时间",
            prop: "endTime",
            type: "datetime",
            format: "yyyy-MM-dd HH:mm:ss",
            valueFormat: "yyyy-MM-dd HH:mm:ss",
            rules: [
              {
                required: true,
                message: "请输入有效结束时间",
                trigger: "blur",
              },
            ],
          },
          {
            label: "排序",
            prop: "sort",
            type: "number",
            value: "1",
            rules: [
              {
                required: true,
                message: "请输入排序",
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
          //   label: "状态",
          //   prop: "status",
          //   type: "selection",
          //   value: "1",
          //   rules: [
          //     {
          //       required: true,
          //       message: "请选择状态",
          //       trigger: "blur",
          //     },
          //   ],
          // },
          {
            label: "班级类型",
            prop: "sellType",
            type: "radio",
            row: true,
            hide: true,
            addDisplay: false,
            editDisplay: false,
            dataType: "string",
            value: "1",
            dicUrl: "/api/crazy-system/dict/dictionary?code=course_type",
            props: {
              label: "dictValue",
              value: "dictKey",
            },
            rules: [
              {
                required: true,
                message: "请输入班级类型",
                trigger: "blur",
              },
            ],
          },
          {
            label: "班级状态",
            prop: "isAvaliable",
            type: "radio",
            search: true,
            dataType: "number",
            value: 1,
            row: true,
            dicUrl: "/api/crazy-system/dict/dictionary?code=course_status",
            props: {
              label: "dictValue",
              value: "dictKey",
            },
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
          //   label: "班主任",
          //   prop: "mainTeacherId",
          //   hide: true,
          //   span: 24,
          //   formslot: true,
          //   rules: [
          //     {
          //       required: true,
          //       message: "请选择班主任",
          //       trigger: "blur",
          //     },
          //   ],
          // },
          {
            label: "授课密码",
            prop: "password",
            span: 24,
            rules: [
              {
                required: false,
                message: "请选择授课密码",
                trigger: "blur",
              },
            ],
          },
          {
            label: "授课教师",
            prop: "teacherIds",
            type: "select",
            hide: true,
            span: 24,
            multiple: true,
            props: {
              label: "name",
              value: "id",
            },
            dicUrl: "/api/user/teacher/all",
            rules: [
              {
                required: true,
                message: "请选择讲师",
                trigger: "blur",
              },
            ],
          },
          {
            label: "创建时间",
            prop: "createTime",
            addDisplay: false,
            editDisplay: false,
            rules: [
              {
                required: false,
                message: "请输入添加时间",
                trigger: "blur",
              },
            ],
          },
        ],
      },
      data: [],
      excelForm: {},
      excelOption: {
        submitBtn: false,
        emptyBtn: false,
        column: [
          {
            label: "模板上传",
            prop: "excelFile",
            type: "upload",
            drag: true,
            loadText: "模板上传中，请稍等",
            span: 24,
            showFileList: false,
            propsHttp: {
              res: "data",
            },
            tip: "请上传 .xls,.xlsx 标准格式文件",
            action: "/api/edu/course/import-course-user",
          },
          {
            label: "模板下载",
            prop: "excelTemplate",
            formslot: true,
            span: 24,
          },
        ],
      },
    };
  },
  computed: {
    ...mapGetters(["permission"]),
    permissionList() {
      return {
        addBtn: this.vaildData(this.permission.course_add, false),
        viewBtn: this.vaildData(this.permission.course_view, false),
        delBtn: this.vaildData(this.permission.course_delete, false),
        editBtn: this.vaildData(this.permission.course_edit, false),
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
      if (this.form.tenantId !== "") {
        getDeptTree(this.form.tenantId).then((res) => {
          // const column = this.findObject(this.option.column, "deptId");
          // column.dicData = res.data.data;
        });
        // getRoleTree(this.form.tenantId).then((res) => {
        //   const column = this.findObject(this.option.column, "roleId");
        //   column.dicData = res.data.data;
        // });
        // getPostList(this.form.tenantId).then((res) => {
        //   const column = this.findObject(this.option.column, "postId");
        //   column.dicData = res.data.data;
        // });
      }
    },
  },
  methods: {
    openChengji(row) {
      this.courseId = row.id;
      this.course = row;
      this.dialogChengjiVisible = true;
      this.courseExamFlag = true;
    },
    openChengjiEam(e) {
      this.clazzId = e.clazzId
      this.dialogOrderStatisticsVisible = true
    },
    openExamPigaiRecord(qst) {
      console.log("openExamPigaiRecord", qst)
      this.clazzId = qst.id;
      this.dialogRecordPigaiVisible = true;
    },
    openAllExamPigaiRecord(qst) {
      console.log("openExamPigaiRecord", qst)
      this.clazzId = qst.id;
      this.dialogAllRecordPigaiVisible = true;
    },
    openExamRecord(e) {
      console.log("openExamRecord", e)
      this.course = e;
      this.dialogRecordVisible = true;
    },
    openExampaperRecord(e) {
      console.log("openExamRecord", e)
      //this.course = e;
      this.clazzId = e.id;
      this.dialogRecordPaperVisible = true;
    },
    submitNotice() {
      addNotice(this.noticeForm).then(res => {
        this.noticeVisible = false;
        this.$message({
          type: "success",
          message: "发送成功!",
        });
      })
    },
    openComment(row) {
      this.courseId = row.id;
      this.dialogCommentVisible = true;
    },
    stduyUsersimb(row) {
      this.courseId = row.id;
      this.courseType = "PACKAGE";
      this.kpointCouserId = 0;
      this.dialogStudyUser = true;
    },
    handleClose() {
      this.tags.splice(0, 1);
      this.form.mainTeacherId = null;
    },
    stduyhistorysimb(row) {
      this.courseId = row.id;
      this.dialogStudyHistory = true;
    },
    handleShowTeacher() {
      this.dialogTeacherVisible = true;
    },
    teacherdataSubmit(selection) {
      this.form.mainTeacherId = selection.id;
      this.tags[0] = { name: selection.name };
      this.dialogTeacherVisible = false;
    },
    dataSubmit(selection) {
      let userIds = "";
      selection.forEach((ele) => {
        userIds += ele.id + ",";
      });
      userIds = userIds.substring(0, userIds.length - 1);
      give(userIds, this.courseId).then(
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
    openNotice(row) {
      this.courseId = row.id;
      this.noticeVisible = true;
    },
    dialogKpoint() {
      this.coursepackageFlag = false;
    },
    handleEdit(row, index) {
      this.$refs.crud.option.column.filter((item) => {
        if (item.prop === "loseTime") {
          if (row.losetype == "0") {
            item.editDisplay = false;
            item.addDisplay = false;
          }
          if (row.losetype == "1") {
            item.editDisplay = true;
            item.addDisplay = true;
          }
        }
        if (item.prop === "endTime") {
          if (row.losetype == "0") {
            item.editDisplay = true;
            item.addDisplay = true;
          }
          if (row.losetype == "1") {
            item.editDisplay = false;
            item.addDisplay = false;
          }
        }
      });
      this.$refs.crud.rowEdit(row, index);
    },
    handleDel(row) {
      this.$refs.crud.rowDel(row);
    },
    change(value) {
      this.$refs.crud.option.column.filter((item) => {
        if (item.prop === "loseTime") {
          if (value == "0") {
            item.editDisplay = false;
            item.addDisplay = false;
          }
          if (value == "1") {
            item.editDisplay = true;
            item.addDisplay = true;
          }
        }
        if (item.prop === "endTime") {
          if (value == "0") {
            item.editDisplay = true;
            item.addDisplay = true;
          }
          if (value == "1") {
            item.editDisplay = false;
            item.addDisplay = false;
          }
        }
      });
    },
    openCourseKpoint(row) {
      this.courseId = row.id;
      this.dialogKpointVisible = true;
      this.coursepackageFlag = true;
    },
    openCoursepackageEam(row) {
      this.courseId = row.id;
      this.dialogExamVisible = true;
      this.coursepackageExamFlag = true;
    },
    rowSave(row, loading, done) {
      if (row.teacherIds) {
        row.teacherIds = row.teacherIds.join(",")
      }
      row.sellType = "3";
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
      if (row.teacherIds) {
        row.teacherIds = row.teacherIds.join(",")
      }
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
      console.log("beforeOpen", type)
      if (["edit", "view"].includes(type)) {
        getDetail(this.form.id).then((res) => {

          if (res.data.data.teacherIds) {
            res.data.data.teacherIds = res.data.data.teacherIds.split(",")
            res.data.data.teacherIds.map(item => {
              item = 131
            })
            console.log("res.data.data.teacherIds", res.data.data.teacherIds)
          }
          this.form = res.data.data;
        });
      }
      let that = this;
      if (this.form.mainTeacherId) {
        getTeacherDetail(this.form.mainTeacherId).then((res) => {
          this.tags = [];
          that.tags[0] = { name: res.data.data.name };
          console.log(that.tags);
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
    handleTemplate() {
      window.open(`static/template/批量分配班级模板.xlsx`);
      // window.open(`/api/user/student/export-template?crazy-auth=${getToken()}`);
    },
    handleImport() {
      this.excelBox = true;
    },
    uploadAfter(res, done, loading, column) {
      if (Object.prototype.toString.call(res) == "[object Error]") {
        this.errorMsg = res.message;
        done();
        return;
      }
      done();
      this.errorMsg = "";
      this.excelBox = false;
      this.refreshChange();
    },
    refreshChange() {
      this.onLoad(this.page, this.query);
    },
    onLoad(page, params = {}) {
      this.loading = true;
      getMyClazzList(
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

      getDeptTree(this.form.tenantId).then((res) => {
        // const column = this.findObject(this.option.column, "deptId");
        // column.dicData = res.data.data;
        // this.treeData = res.data.data;
      });
    },
  },
  components: {
    coursepackage: coursepackage,
    coursepackageExam: coursepackageExam,
    chengjiStatistics: chengjiStatistics,
    student2: student2,
    notice: notice,
    teacher: teacher,
    examRecordQstByClazzPigai:examRecordQstByClazzPigai,
    comment: comment,
    exampaperrecord: exampaperrecord,
    courseTaoCanStudyStudent: courseTaoCanStudyStudent,
    courseTaoCanStudyStudenthistory: courseTaoCanStudyStudenthistory,
    shicaoRecordQstByClazzPigai: shicaoRecordQstByClazzPigai,
    studyhistory: studyhistory,
    exampapers: exampapers
  },
  created: function () {
    getEndTimeType().then((res) => {
      this.losetypeList = res.data.data;
    });
    subjectTree().then((res) => {
      this.subjectTree = res.data.data;
    });
  },
};
</script>

<style></style>
