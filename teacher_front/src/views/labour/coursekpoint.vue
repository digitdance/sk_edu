<template>
  <basic-container>
    <avue-crud :option="option" :table-loading="loading" :data="data" :page="page" :permission="permissionList"
      :before-open="beforeOpen" :upload-before="uploadBefore" v-model="form" ref="crud" @row-update="rowUpdate"
      @row-save="rowSave" @row-del="rowDel" @search-change="searchChange" @search-reset="searchReset"
      @selection-change="selectionChange" @current-change="currentChange" @size-change="sizeChange"
      @refresh-change="refreshChange" @on-load="onLoad" @sortable-change="sortableChange">
      <template slot="menuLeft">
        <el-button type="primary" size="small" icon="el-icon-plus" @click="showMkDialo">教学模块
        </el-button>
        <el-button type="primary" size="small" icon="el-icon-plus" @click="showKpointDialo">章节目录
        </el-button>
        <el-button type="danger" size="small" icon="el-icon-delete" plain v-if="permission.coursekpoint_delete"
          @click="handleDelete">删 除
        </el-button>
      </template>
      <template slot-scope="scope" slot="chooseVideoForm">
        <el-tag v-if="form.videoUrl != ''" @close="closeVideo" closable>
          {{ videoName }}
        </el-tag>
        <el-button type="primary" v-if="form.videoUrl == ''" @click="dialogVideoVisible = true">选择视频</el-button>
      </template>

      <template slot-scope="scope" slot="chooseDocumentForm">
        <div>
          <el-tag v-if="form.documentUrl" @close="closeDocument" closable>
            {{ documentName }}

          </el-tag>
          <el-button type="primary" v-if="!form.documentUrl" @click="chooseDocument">选择资料</el-button>
        </div>
      </template>

      <!-- <template slot-scope="scope" slot="chooseDocumentForm">
        <div>
          <el-tag v-if="form.documentUrl" @close="closeDocument" closable>
            {{ documentName }}
          </el-tag>
          <el-button type="primary" v-if="!form.documentUrl" @click="chooseDocument">选择资料</el-button>
        </div>
      </template> -->
      <template slot-scope="scope" slot="name">
        <div style="display: inline-block;">

          <span v-if="!scope.row.children">{{ scope.row.name }}</span>
          <span v-else style="font-weight: bold;font-size: 16px;">{{ scope.row.name }}</span>
          <div style="display: inline-block;">
            <el-tooltip v-if="!scope.row.children && scope.row.videoUrl" :content="getVideoName(scope.row.videoUrl)"
              style="margin-right: 10px;">
              <i class="icon-t el-icon-video-camera" type="primary" style="font-size: 22px;color:brown;"></i>
            </el-tooltip>
            <el-tooltip v-if="!scope.row.children && scope.row.documentUrl" style="margin-right: 10px;"
              :content="getVideoName(scope.row.documentUrl)">
              <i class="el-icon-s-help" style="font-size: 22px;color: aqua;"></i>
            </el-tooltip>

            <!-- <el-tooltip  v-if="course.type=='common'&&scope.row.isLock==0&&scope.row.parentId != '0'" style="margin-right: 10px;" content="未锁">
              <i class="el-icon-lock" style="font-size: 22px;color: aqua;"></i>
            </el-tooltip> -->
            <!-- <el-tooltip  v-if="course.type=='common'&&scope.row.isLock==1&&scope.row.parentId != '0'" style="margin-right: 10px;" content="已锁">
              <i class="el-icon-lock" style="font-size: 22px;color: aqua;"></i>
            </el-tooltip> -->

            <!-- <el-tooltip  v-if="course.type=='exam'&&scope.row.isLock==0&&scope.row.parentId != '0'" style="margin-right: 10px;" content="未锁">
                <i class="el-icon-lock" style="font-size: 22px;color: aqua;"></i>
              </el-tooltip> -->
            <!-- <el-tooltip  v-if="course.type=='exam'&&scope.row.isLock==1&&scope.row.parentId != '0'" style="margin-right: 10px;" content="已锁">
              <i class="el-icon-lock" style="font-size: 22px;color: aqua;"></i>
            </el-tooltip> -->

            <el-badge v-if="!scope.row.children && scope.row.kqstCount > 0" :value="scope.row.kqstCount"
              style="margin-right: 10px;">
              <el-tooltip content="练习">
                <i class="el-icon-s-management" style="font-size: 20px;"></i>
              </el-tooltip>
            </el-badge>

            <el-badge v-if="!scope.row.children && scope.row.shicaoCount > 0" :value="scope.row.shicaoCount"
              style="margin-right: 10px;">
              <el-tooltip content="实训">
                <i class="el-icon-s-promotion" style="font-size: 20px;"></i>
              </el-tooltip>
            </el-badge>

            <el-badge v-if="scope.row.kmaterialCount > 0" :value="scope.row.kmaterialCount">
              <el-tooltip>
                <i class="el-icon-s-cooperation" style="font-size: 20px;"></i>
                <div slot="content">
                  <div :key="index" v-for="(item, index) in scope.row.courseMaterials"
                    style="position: relative;border-bottom: 1px solid #999;">
                    {{ index + 1 }}、{{ item.name }}
                    <div style="right: 10px;display: inline-block;display: flex;">
                      <el-button type="text" size="small" icon="el-icon-view"
                        @click="previewFile(item.fileUrl)">预览</el-button>
                      <el-button type="text" size="small" icon="el-icon-delete"
                        @click="fileDel(scope.row, index)">删除</el-button>
                    </div>
                  </div>
                </div>
              </el-tooltip>
            </el-badge>
          </div>



        </div>

      </template>

      <template slot-scope="scope" slot="menu">
        <!-- <el-button type="text" icon="el-icon-edit" size="small" v-if="scope.row.kpointType == '0'"
          @click.stop="handleEdit12(scope.row, scope.index)">查看
        </el-button> -->
        <el-button v-show="course.createBy == userInfo.userId" type="text" icon="el-icon-edit" size="small"
          v-if="scope.row.kpointType == '0'" @click.stop="handleEdit2(scope.row, scope.index)">编辑
        </el-button>
        <el-button v-show="course.createBy == userInfo.userId" type="text" icon="el-icon-edit" size="small"
          v-if="scope.row.kpointType == '1'" @click.stop="handleEdit1(scope.row, scope.index)">编辑
        </el-button>
        <el-button v-show="course.createBy == userInfo.userId" type="text" icon="el-icon-delete" size="small"
          @click.stop="handleDel(scope.row, scope.index)">删除
        </el-button>
        <el-button type="text" v-show="course.createBy == userInfo.userId" icon="el-icon-circle-plus-outline"
          size="small" v-if="scope.row.kpointType == '0'" @click.stop="handleAdd(scope.row, scope.index)">新增子项
        </el-button>
        <el-button v-if="scope.row.kpointType != '-1'" type="text" icon="el-icon-notebook-2" size="small" @click="openMaterial(scope.row)">章节资料
        </el-button>

        <el-button v-if="scope.row.kpointType != '-1'" type="text" icon="el-icon-view" size="small" @click="toPreview(scope.row)">预览章节</el-button>

        <el-button type="text" v-show="course.createBy == userInfo.userId" icon="el-icon-circle-plus-outline"
          size="small" v-if="scope.row.kpointType == '-1'" @click.stop="showKpointDialo(scope.row, scope.index)">新增章节
        </el-button>
        <!-- 
        <el-button type="text" icon="el-icon-unlock" v-if="course.type == 'common'&&scope.row.isLock==1&&scope.row.parentId != '0'" size="small" @click="unLockKpoint(scope.row,0)">解锁
        </el-button> -->

        <el-button type="text" icon="el-icon-notebook-1" size="small"
          v-if="scope.row.parentId != '0' && scope.row.kpointType == '1'" @click="openHomework(scope.row)">作业
        </el-button>
        <!-- <el-button
          type="text"
          icon="el-icon-notebook-1"
          size="small"
          v-if="scope.row.parentId != '0' && scope.row.kpointType == '1'"
          @click="openTask(scope.row)"
          >实训任务
        </el-button> -->
        <!-- <el-button type="text" icon="el-icon-notebook-1" size="small"
          v-if="scope.row.parentId != '0' && scope.row.kpointType == '1'" @click="openQuestion(scope.row, '5')">实训任务
        </el-button> -->
        <el-button type="text" icon="el-icon-notebook-1" size="small"
          v-if="scope.row.parentId != '0' && scope.row.kpointType == '1'" @click="openQuestion(scope.row, '4')">练习题
        </el-button>
        <el-button type="text" icon="el-icon-notebook-1" size="small"
          v-if="scope.row.parentId != '0' && scope.row.kpointType == '1'" @click="openQuestion(scope.row, '3')">实训任务
        </el-button>
        <!-- <el-button type="text" icon="el-icon-notebook-1" size="small"
          v-if="scope.row.parentId != '0' && scope.row.kpointType == '1'" @click="aiDesign(scope.row, '3')">AI课程设计
        </el-button> -->
      </template>
      <template slot-scope="scope" slot="videoTypeForm">
        <el-select v-model="scope.row.videoType" @change="change" placeholder="请选择">
          <el-option v-for="item in supplierList" :key="item.dictKey" :label="item.dictValue" :value="item.dictKey">
          </el-option>
        </el-select>
      </template>
      <template slot-scope="scope" slot="teacherIdForm">
        <el-tag v-if="tags.length == 1" closable @close="handleClose(tag)" v-for="tag in tags" :key="tag"
          :disable-transitions="false">{{ tag.name }}</el-tag>
        <el-button type="primary" icon="el-icon-circle-plus-outline" size="small" v-if="tags.length == 0"
          @click.stop="handleShowTeacher(scope.row)">选择讲师
        </el-button>
      </template>
    </avue-crud>
    <el-dialog title="添加章节目录" :modal="true" :visible.sync="dialogFormVisible" append-to-body>
      <el-form :model="form">

        

        <el-form-item v-if="selectMk" label="模块" :label-width="formLabelWidth">
         {{ selectMk.name }}
        </el-form-item>
        <el-form-item label="目录名称" :label-width="formLabelWidth">
          <el-input v-model="name" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="addKponint">确 定</el-button>
      </div>
    </el-dialog>
    <el-dialog title="添加教学模块" :modal="true" :visible.sync="dialogMkFormVisible" append-to-body>
      <el-form :model="mkForm">
        <el-form-item label="模块名称" :label-width="formLabelWidth">
          <el-input v-model="name" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogMkFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitMk">确 定</el-button>
      </div>
    </el-dialog>
    <el-dialog title="讲师列表" width="80%" :visible.sync="dialogTeacherVisible" append-to-body>
      <teacher v-if="teaFlag" @datasubmit="dataSubmit"></teacher>
    </el-dialog>
    <el-dialog title="视频列表" width="80%" :visible.sync="dialogVideoVisible" append-to-body>
      <choose-videosource v-if="dialogVideoVisible" @videodata="videodata"></choose-videosource>
    </el-dialog>
    <el-dialog title="章节资料" width="80%" :visible.sync="dialogDataVisible" append-to-body>
      <!-- <courseMaterial :materialIds="materialIds" :kpointId="kpointId" :action="chooseKpointType" @documentdata="kpointDocumentdata" >
      </courseMaterial> -->

      <courseMaterialChoose :action="'choose'" @documentdata="kpointDocumentdata" :kpointId="kpointId">
      </courseMaterialChoose>

    </el-dialog>
    <el-dialog title="资料管理" width="80%" :visible.sync="dialogDataChooseVisible" append-to-body>
      <courseMaterialChoose :action="'choose'" @documentdata="documentdata">
      </courseMaterialChoose>
    </el-dialog>
    <el-dialog title="实训任务管理" width="80%" :visible.sync="dialogTaskVisible" append-to-body>
      <courseTask :platform="0" :kpointId="kpointId" :courseId="courseId"></courseTask>
    </el-dialog>

    <el-dialog class="el-scrollbar__wrap" :title="composeExamTitle" width="80%" :visible.sync="dialogQuestionVisible"
      append-to-body>
      <composeExampaper :exampaperArr="exampaperArr" :exam="kpoint" :examType="examType" @saveExam="saveKpointQuestion">
      </composeExampaper>
      <span slot="footer" class="dialog-footer">
        <el-button type="warning" @click="selectExampaper">选择试卷</el-button>
        <el-button type="primary" @click="saveKpointQuestion">保 存</el-button>
        <el-button @click="dialogQuestionVisible = false">取 消</el-button>
      </span>
    </el-dialog>

    <el-dialog
      title="考试选择"
      width="80%"
      :visible.sync="dialogCourseKpointExamVisible"
      @close="closedialogCourseKpointExam"
      append-to-body
    >
      <addexam @adddata="adddata" :limit="1" v-if="dialogCourseKpointExamVisible"></addexam>
    </el-dialog>


    <el-dialog class="el-scrollbar__wrap" :title="shicaoTitle" width="95%" :visible.sync="dialogShicaoVisible"
      append-to-body>
      <shicao :exampaperArr="exampaperArr" :paperId="paperId" :kpoint="kpoint" :examType="examType"
        @saveExam="saveKpointQuestion">
      </shicao>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="addShicaoExampaper">保 存</el-button>
        <el-button @click="dialogShicaoVisible = false">取 消</el-button>
      </span>
    </el-dialog>

    <el-dialog title="文件预览" width="80%" :visible.sync="dialogPreviewVisible" append-to-body>
      <iframe style="width: 100%; height: 600px" allowfullscreen="true" id="previewer" src="">
      </iframe>
    </el-dialog>
    <el-dialog title="作业管理" width="80%" :visible.sync="dialogHomeworkVisible" append-to-body>
      <courseHomework :kpointId="kpointId" :courseId="courseId"></courseHomework>
    </el-dialog>
  </basic-container>
</template>

<script>
import {
  getList,
  getDetail,
  add,
  update,
  updateMaterials,
  updateSort,
  unLock,
  remove,
  getSupplier,
  addMk,
  getUserDetail,
} from "@/api/edu/coursekpoint";

import {
  setKpointExam
} from "@/api/edu/coursepackageExam";
import Sortable from 'sortablejs'

import { add2 as addPaper, saveQuestion } from "@/api/exam/exampaper";
import { getDetailByIdVarchar } from "@/api/edu/videosource";
import { getDetail as getResourceDetail } from "@/api/resource";
import { mapGetters } from "vuex";
import teacher from "./teacher";
import courseMaterial from "./courseMaterial";
import courseHomework from "./courseHomework";
import courseTask from "./courseTask";
import chooseVideosource from "./chooseVideosource";
import composeExampaper from "./composeExampaper";
import courseMaterialChoose from "../resource/courseMaterialChoose";
import { getList as getQuestions } from "@/api/exam/question";
import shicao from "../edu/shicao";
import addexam from "../edu/addexam";
// import shicao from "../edu/shicaoChoose";
export default {
  data() {
    return {
      videoName: "",
      materialIds: "",
      selectRow: {},
      selectMk:null,
      dataBack:[],
      shicaoTitle: "",
      editAction: "edit",
      dialogCourseKpointExamVisible:false,
      paperId: "",
      dialogDataChooseVisible: false,
      dialogVideoVisible: false,
      dialogDataVisible: false,
      dialogPreviewVisible: false,
      dialogHomeworkVisible: false,
      composeExamTitle: "",
      dialogTaskVisible: false,
      examType: '4',
      dialogQuestionVisible: false,
      dialogShicaoVisible: false,
      teaFlag: false,
      tags: [],
      kpointId: "",
      kpoint: undefined,
      paper: {},
      exampaperArr: [],
      supplierList: [
        {
          dictValue: "阿里云点播",
          dictKey: "aliyunvod",
        },
        {
          dictValue: "视频云点播",
          dictKey: "baijiayunvod",
        } /*,{
          dictValue:"保利威点播",
          dictKey:"polyvvod"
        }*/,
        {
          dictValue: "其他",
          dictKey: "othervod",
        },
      ],
      name: "",
      formLabelWidth: "120px",
      dialogTeacherVisible: false,
      paperId: 0,
      dialogFormVisible: false,
      dialogMkFormVisible: false,
      form: {},
      mkForm:{},
      query: { courseId: this.courseId },
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
        index: false,
        addBtn: false,
        editBtn: false,
        delBtn: false,
        viewBtn: false,
        selection: false,
        sortable: true,
        labelWidth: 150,
        defaultExpandAll: true,
        columnBtn: false,
        column: [
          {
            label: "选择视频",
            prop: "chooseVideo",
            formslot: true,
            span: 24,
            hide: true,
          },
          {
            label: "课件",
            prop: "chooseDocument",
            formslot: true,
            span: 24,
            hide: true,
          },
          // {
          //   label: "选择资料",
          //   prop: "chooseDocument",
          //   formslot: true,
          //   span: 24,
          //   hide: true,
          // },
          {
            label: "名称",
            prop: "name",
            span: 24,
            slot: true,
            rules: [
              {
                required: true,
                message: "请输入节点名称",
                trigger: "blur",
              },
            ],
          },
          {
            label: "视频来源",
            prop: "videoType",
            hide: true,
            display: false,
            span: 24,
            value: "",
            dataType: "string",
            formslot: true,
          },
          {
            label: "播放地址",
            prop: "videoUrl",
            span: 24,
            hide: true,
            display: false,
          },
          // {
          //   label: "远程播放地址",
          //   prop: "remoteVideoUrl",
          //   span: 24,
          //   hide: true,
          // },
          {
            label: "文档地址",
            prop: "documentUrl",
            display: false,
            span: 24,
            hide: true,
          },
          {
            label: "学习时长(格式00:00:00)",
            prop: "videoTime",
            span: 24,
            hide: true,
            rules: [
              {
                required: false,
                message: "请输入学习时长(格式00:00:00)",
                trigger: "blur",
              },
            ],
          },
          {
            label: "章节详情",
            prop: "content",
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
          // {
          //   label: "打卡要求",
          //   prop: "context",
          //   type: "textarea",
          //   span: 24,
          //   hide: true,
          //   rules: [
          //     {
          //       required: true,
          //       message: "请输入打卡要求",
          //       trigger: "blur",
          //     },
          //   ],
          // },
          // {
          //   label: "积分",
          //   prop: "integral",
          //   type: "number",
          //   width: "80px",
          //   value: 1,
          //   span: 12,
          //   rules: [
          //     {
          //       required: true,
          //       message: "请输入章节积分",
          //       trigger: "blur",
          //     },
          //   ],
          // },
          {
            label: "上级类型",
            prop: "parentId",
            hide: true,
            addDisplay: false,
            editDisplay: false,
            type: "select",
            span: 24,
            dicUrl: "/api/edu/coursekpoint/tree",
            props: {
              label: "title",
            },
          },
          {
            label: "课程章节类型",
            prop: "kpointType",
            hide: true,
            span: 24,
            addDisplay: false,
            editDisplay: false,
          },
          {
            label: "排序",
            prop: "sort",
            type: "number",
            value: 1,
            span: 24,
            rules: [
              {
                required: true,
                message: "请输入显示排序",
                trigger: "blur",
              },
            ],
          },
          /*{
              label: "是否可以试听",
              prop: "isFree",
              type: "radio",
              hide: true,
              value: 2,
              span: 24,
              dataType: "number",
              props: {
                label: "dictValue",
                value: "dictKey"
              },
              dicUrl: "/api/crazy-system/dict/dictionary?code=yes_no",
            },*/
          // {
          //   label: "讲师",
          //   prop: "teacherId",
          //   hide: true,
          //   span: 24,
          //   formslot: true,
          // },
          /*{
              label: "video视频 ",
              prop: "fileType",
              hide: true,
              type: "select",
              value: "1",
              dataType: "string",
              props: {
                label: "dictValue",
                value: "dictKey"
              },
              dicUrl: "/api/crazy-system/dict/dictionary?code=file_type",
              rules: [{
                required: true,
                message: "请输入video视频 ",
                trigger: "blur"
              }]
            },*/
        ],
      },
      data: [],
    };
  },
  computed: {
    ...mapGetters(["permission", "userInfo"]),
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
    course: Object,
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
    sortableChange(e,f,g){
      this.loading = true;
      console.log("sortableChange",e,f)
      console.log("sortableChange g",g)

      var all = []
      
      var dataList = JSON.parse(JSON.stringify(this.dataBack))
      console.log(dataList)
      dataList.map(item=>{
        if(item){
          all.push(item)
          try{
            console.log(item)
            if(item.children){
              item.children.map(item2=>{
                all.push(item2)
                console.log("2=>"+item.children)
                if(item2.children){
                  item2.children.map(item3=>{
                    all.push(item3)
                  })
                }
              })
            }
          }catch(e){
            console.log(e)
          }
        }
      })
      all.map((item,index)=>{
        item.sort = index
      })

      all[e].sort=f
      all[f].sort=e

      var updates = []
      all.map((item,index)=>{
        updates.push({
          id:item.id,
          sort:item.sort
        })
      })
      // var min1 = e
      // var max1 = f
      // if(e>f){
      //   min1 = f
      //   max1 = e
      // }
      // all.map((item,idex)=>{
      //   if(idex>=min1 && idex<max1){
      //     item.sort -=1
      //   }
      //   if(idex>=max1){
      //     item.sort +=1
      //   }
      // })


      console.log("all",all)
      // var row = all[e]
      // console.log("row",row)
      // row.sort = f

      updateSort(updates).then(res=>{
        this.data = []
        this.onLoad(this.page);
      })
    },
    adddata(selectionList) {
      this.dialogCourseKpointExamVisible = false;
      let list = [];
      // selectionList.forEach((ele) => {
      //   list.push({
      //     clazzId: this.kpoint.courseId,
      //     exampaperId: ele.id,
      //     kpointId:this.kpoint.id,
      //     customExampaperName: ele.name,
      //   });
      // });
      var ele = selectionList[0]
      var data = {
          courseId: this.kpoint.courseId,
          exampaperId: ele.id,
          kpointId:this.kpoint.id,
          //customExampaperName: ele.name,
      }
      setKpointExam(data, this.courseId).then(
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
    fileDel(row, index) {
      var that = this
      this.$confirm("确定将选择数据删除?", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          //return remove(row.id);
          row.courseMaterials.splice(index, 1)
          var ids = []
          row.courseMaterials.map(item => {
            ids.push(item.id)
          })
          row.materialIds = ids.join(",")
          return updateMaterials(row)
        })
        .then(() => {
          this.onLoad(this.page);
          this.$message({
            type: "success",
            message: "操作成功!",
          });
        });
    },
    previewFile(fileUrl) {
      this.dialogPreviewVisible = true
      this.$nextTick(e => {
        var previewer = document.getElementById("previewer");
        console.log("documentUrl", fileUrl);
        var iframeSrc = "http://139.9.162.79:8012/onlinePreview?url=" + encodeURIComponent(Base64.encode(fileUrl));

        console.log("iframeSrc", previewer, iframeSrc)
        //previewer.height = window.innerHeight;
        previewer.setAttribute("src", iframeSrc);
      })

      //window.open(row.fileUrl);
    },
    selectExampaper(){
        this.dialogCourseKpointExamVisible = true
        this.dialogQuestionVisible  = false
    },
    closedialogCourseKpointExam(e){
        console.log("closedialogCourseKpointExam",e)
    },
    addShicaoExampaper(paperId, questions) {
      this.dialogShicaoVisible = false
      this.onLoad(this.page)
      return
      getQuestions(1, 10000, {
        kpointId: this.kpoint.id,
        qstType: "6"
      }).then(res => {

        console.log("getQuestions", res)
        //score: questions.length,
        var questions = res.data.data.records

        questions.map(item => {
          item.qstId = item.id
          item.paperId = this.paperId
          item.id = null
        })
        var exampaperArr = []
        let data = {
          name: "实训题",
          score: questions.length,
          num: questions.length,
          traffic: "0",
          type: "6",
          questionArr: questions,
          paperId: this.paperId,
          title: "",
        }
        exampaperArr.push(data);
        saveQuestion(exampaperArr).then((res) => {
          this.$message({
            type: "success",
            message: "保存成功",
          });
          this.dialogShicaoVisible = false
        });
      })


    },
    toPreview(e) {
      window.open(`http://100.86.128.18:8282/course/previewCourseInfo/` + e.courseId+"?kpontId="+e.id);
    },
    UrlDecode(str) {
      var ret = "";
      for (var i = 0; i < str.length; i++) {
        var chr = str.charAt(i);
        if (chr == "+") {
          ret += " ";
        } else if (chr == "%") {
          var asc = str.substring(i + 1, i + 3);
          if (parseInt("0x" + asc) > 0x7f) {
            ret += asc2str(parseInt("0x" + asc + str.substring(i + 4, i + 6)));
            i += 5;
          } else {
            ret += asc2str(parseInt("0x" + asc));
            i += 2;
          }
        } else {
          ret += chr;
        }
      }
      return ret;
    },
    getVideoName(e) {
      var fName = decodeURIComponent(e)
      if (fName.indexOf("_") > -1) {
        fName = fName.split("_")[1]
      }
      var indexStart = fName.lastIndexOf("/")
      return fName.substring(indexStart + 1)
    },
    unLockKpoint(row, isLock) {
      row.isLock = isLock
      unLock(row).then(res => {
        this.$message({
          type: "success",
          message: "操作成功",
        });
      })
    },
    closeDocument() {
      this.form.documentUrl = "";
      //this.form.videoTime = "00:00";
      this.documentName = "";
    },
    chooseDocument() {
      this.dialogDataChooseVisible = true;
    },
    kpointDocumentdata(ids) {

      console.log("kpointDocumentdata", ids)
      var hasSelected = false
      this.selectRow.courseMaterials.map(item => {
        if (item.id == ids.id) {
          hasSelected = true
        }
      })
      if (!hasSelected) {
        this.selectRow.courseMaterials.push(ids)
      }

      var sids = []
      this.selectRow.courseMaterials.map(item => {
        sids.push(item.id)
      })
      this.selectRow.materialIds = sids.join(",")
      updateMaterials(this.selectRow).then(
        () => {
          this.dialogDataVisible = false;
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

      return
      // this.selectRow.materialIds = ids.join(",")
      // updateMaterials(this.selectRow).then(
      //   () => {
      //     this.onLoad(this.page);
      //     this.$message({
      //       type: "success",
      //       message: "操作成功!",
      //     });
      //   },
      //   (error) => {

      //     console.log(error);
      //   }
      // );
      //this.dialogDataChooseVisible = false;
    },
    documentdata(row) {
      console.log("documentdata", row);
      //this.form.name = row.name;
      this.form.materialId = row.id;
      this.form.pageCount = row.totalPage;
      this.documentName = row.name;
      this.form.documentUrl = row.fileUrl;

      this.dialogDataChooseVisible = false;
    },
    saveKpointQuestion(e) {
      console.log("saveKpointQuestion", this.exampaperArr);

      saveQuestion(this.exampaperArr,this.paperId).then((res) => {
        // this.exampaperArr = [];
        // this.dialogQuestionVisible = false;
        // this.onLoad(this.page);
      });
    },
    closeVideo() {
      this.form.videoUrl = "";
      this.form.videoTime = "00:00";
      this.videoName = "";
    },
    videodata(row) {
      console.log("videodata", row);
      this.form.name = row.name;
      this.videoName = row.name;
      this.form.videoUrl = row.idVarchar;
      let vtype = row.videoType;
      if (row.videoType == "1") {
        vtype = "aliyunvod";
      }
      if (row.videoType == "2") {
        vtype = "baijiayunvod";
      }
      this.form.videoType = vtype;

      let value = row.videoDuration;
      let timeStr = null;
      let hour;
      let minute;
      let second = 0;
      if (value <= 0) {
        console.log("1111");
        //return "00:00";
      } else {
        console.log("22222");
        minute = value / 60;
        second = value % 60;
        if (parseInt(minute) < 10) {
          if (second < 10) {
            timeStr = "0" + parseInt(minute) + ":0" + second;
          } else {
            timeStr = "0" + parseInt(minute) + ":" + second;
          }
        } else {
          if (second < 10) {
            timeStr = parseInt(minute) + ":0" + second;
          } else {
            timeStr = parseInt(minute) + ":" + second;
          }
        }
      }
      this.form.videoTime = timeStr;
      this.dialogVideoVisible = false;
    },
    handleClose(tag) {
      this.tags.splice(0, 1);
    },
    dataSubmit(selection) {
      this.form.teacherId = selection.id;
      this.tags[0] = { name: selection.name };
      this.dialogTeacherVisible = false;
      this.teaFlag = false;
    },
    handleShowTeacher(row) {
      this.dialogTeacherVisible = true;
      this.teaFlag = true;
    },
    uploadBefore(file, done, loading) {
      //视频为mp4格式时则可以上传
      if ("video/mp4" == file.type) {
        done();
      } else {
        this.$message({
          type: "error",
          message: "请上传mp4格式视频",
        });
        loading();
      }
    },
    change(value) {
      console.log(value);
      this.option.column.filter((item) => {
        if (item.prop === "videoUrl") {
          if (value == "aliyunvod") {
            item.label = "播放地址";
            item.prop = "videoUrl";
            item.type = "input";
            item.hide = true;
          }
          if (value == "baijiayunvod") {
            item.label = "播放地址";
            item.prop = "videoUrl";
            item.type = "input";
            item.hide = true;
          }
          if (value == "polyvvod") {
            item.label = "播放地址";
            item.prop = "videoUrl";
            item.type = "input";
            item.hide = true;
          }
          if (value == "othervod") {
            item.label = "视频地址";
            item.type = "input";
            item.prop = "videoUrl";
            item.hide = true;
          }
        }
      });
    },
    handleEdit1(row, index) {
      this.$refs.crud.value.parentId = row.id;
      this.$refs.crud.option.column.filter((item) => {
        item.editDisplay = true;
        if (item.prop === "parentId") {
          item.value = row.id;
          item.editDisplay = false;
        }
        if (item.prop === "kpointType") {
          item.value = 1;
          item.editDisplay = false;
        }
        if (item.prop === "videoUrl") {
          if (row.videoType == "aliyunvod") {
            item.label = "播放地址";
            item.prop = "videoUrl";
            item.type = "input";
            item.hide = true;
          }
          if (row.videoType == "baijiayunvod") {
            item.label = "播放地址";
            item.prop = "videoUrl";
            item.type = "input";
            item.hide = true;
          }
          if (row.videoType == "polyvvod") {
            item.label = "播放地址";
            item.prop = "videoUrl";
            item.type = "input";
            item.hide = true;
          }
          if (row.videoType == "othervod") {
            item.label = "视频地址";
            item.type = "input";
            item.prop = "videoUrl";
            item.hide = true;
          }
        }
      });
      getUserDetail(row.teacherId).then((res) => {
        if (res.data.msg != "暂无承载数据") {
          this.tags[0] = { name: res.data.data.name };
        }
      });

      this.$refs.crud.rowEdit(row, index);
    },
    handleEdit12(row, index) {
      this.$refs.crud.value.parentId = row.id;
      this.$refs.crud.option.column.filter((item) => {
        item.editDisplay = false;
        if (item.prop === "name") {
          item.editDisplay = true;
        }
        if (item.prop === "sort") {
          item.editDisplay = true;
        }
      });
      this.$refs.crud.rowView(row, index);
      this.editAction = "detail";
    },
    handleEdit2(row, index) {
      this.$refs.crud.value.parentId = row.id;
      this.$refs.crud.option.column.filter((item) => {
        item.editDisplay = false;
        if (item.prop === "name") {
          item.editDisplay = true;
        }
        if (item.prop === "sort") {
          item.editDisplay = true;
        }
      });
      this.$refs.crud.rowEdit(row, index);
    },
    handleDel(row) {
      this.$refs.crud.rowDel(row);
    },
    handleAdd(row) {
      this.$refs.crud.value.parentId = row.id;
      this.$refs.crud.option.column.filter((item) => {
        if (item.prop === "parentId") {
          item.value = row.id;
        }
        if (item.prop === "kpointType") {
          item.value = 1;
        }
      });
      this.$refs.crud.rowAdd();
    },
    openMaterial(row) {
      this.kpointId = row.id;
      this.action = "preview"
      this.selectRow = row
      console.log("openMaterial", row)
      this.dialogDataVisible = true;
      this.materialIds = row.materialIds
    },
    openHomework(row) {
      this.kpointId = row.id;
      this.dialogHomeworkVisible = true;
    },
    openTask(row) {
      this.kpointId = row.id;
      this.dialogTaskVisible = true;
    },
    // openPaper(exam) {
    //   this.exampaperArr = [];
    //   queryQuestion(exam.id).then((res) => {
    //     this.exampaperArr = res.data.data;
    //     this.dialogComposeVisible = true;
    //   });
    //   this.exam = exam;
    // },

    createPaper(row, callback) {
      var append = "课后习题"
      if (this.examType == 3) {
        append = "实训任务"
      }
      this.composeExamTitle = row.name + append
      var param = {
        assemblyMode: "manual",
        level: "simple",
        name: row.name + append,
        replyTime: 120,
        status: "normal",
        subjectId: 0,
        type: this.examType,
        info: "",
        courseId: row.courseId,
        kpointId: row.id,
      };
      addPaper(param).then((res) => {
        this.paper = res.data.data;
        if (callback) {
          callback(res.data);
        }
      });
    },
    aiDesign(row,type){
      window.open("http://100.86.128.18:8282/coursedesign?content=请设计《"+row.name+"》的教学课件")

    },
    openQuestion(row, type) {

      console.log("openQuestion", row)
      var that = this;
      that.examType = type
      row.examType  = type
      if (type == 3) {

        this.createPaper(row, (e) => {
          console.log("createPaper", e);
          row.paperId = e.data.id;
          //that.dialogShicaoVisible = true;
          that.dialogShicaoVisible = true;
          //that.examType = type
          that.shicaoTitle = row.name + "实训任务"
          that.exampaperArr = e.data.middles;
          that.kpoint = row;
          that.paperId = e.data.id;
          // that.addShicaoExampaper
        });

        // that.dialogShicaoVisible = true;
        // that.shicaoTitle = row.name+"实训任务"
        // that.kpoint = row;
      } else {
        this.createPaper(row, (e) => {
          console.log("createPaper", e);
          row.paperId = e.data.id;
          this.paperId = e.data.id;
          //that.dialogShicaoVisible = true;
          that.dialogQuestionVisible = true;
          //that.examType = type
          that.exampaperArr = e.data.middles;
          that.kpoint = row;
        });
      }
    },
    rowSave(row, loading, done) {
      let videoTimeminType1 = /^([0-9][0-9][0-9]):([0-5][0-9])$/;
      let videoTimeminType2 = /^([0-9][0-9]):([0-5][0-9])$/;
      let videoTime = row.videoTime;
      if (videoTime) {
        if (videoTime.length == 6) {
          let videoTimeT = videoTimeminType1.test(videoTime);
          // if (!videoTimeT) {
          //   this.$message({
          //     type: "warning",
          //     message:
          //       "时间格式不正确，例如xxx:xx或xx:xx或0x:xx 注意：冒号为英文下的",
          //   });
          //   done();
          //   return;
          // }
        } else if (videoTime.length == 5 && videoTime != '00:00') {
          console.log("videoTime==", videoTime)
          let videoTimeT = videoTimeminType2.test(videoTime);
          if (!videoTimeT) {
            // this.$message({
            //   type: "warning",
            //   message:
            //     "时间格式不正确，例如xxx:xx或xx:xx或0x:xx 注意：冒号为英文下的",
            // });
            // done();
            // return;
          }

          let videoTimeminType3 = /^([0-0][0-0]):([0-0][0-0])$/;
          let defaultTime = videoTimeminType3.test(videoTime);
          // if (defaultTime) {
          //   this.$message({
          //     type: "warning",
          //     message: "学习时长不能为空",
          //   });
          //   done();
          //   return;
          // }
        } else {
          // this.$message({
          //   type: "warning",
          //   message:
          //     "时间格式不正确，例如xxx:xx或xx:xx或0x:xx 注意：冒号为英文下的",
          // });
          // done();
          // return;
        }
      } else {
        row.videoTime = "00:00";
      }

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
      let parentId = row.parentId;
      if (parentId != 0) {
        let videoTimeminType1 = /^([0-9][0-9][0-9]):([0-5][0-9])$/;
        let videoTimeminType2 = /^([0-9][0-9]):([0-5][0-9])$/;
        let videoTime = row.videoTime;
        if (videoTime) {
          if (videoTime.length == 6) {
            let videoTimeT = videoTimeminType1.test(videoTime);
            // if (!videoTimeT) {
            //   this.$message({
            //     type: "warning",
            //     message:
            //       "时间格式不正确，例如xxx:xx或xx:xx或0x:xx 注意：冒号为英文下的",
            //   });
            //   done();
            //   return;
            // }
          } else if (videoTime.length == 5 && videoTime != '00:00') {
            let videoTimeT = videoTimeminType2.test(videoTime);
            // if (!videoTimeT) {
            //   this.$message({
            //     type: "warning",
            //     message:
            //       "时间格式不正确，例如xxx:xx或xx:xx或0x:xx 注意：冒号为英文下的",
            //   });
            //   done();
            //   return;
            // }

            let videoTimeminType3 = /^([0-0][0-0]):([0-0][0-0])$/;
            let defaultTime = videoTimeminType3.test(videoTime);
            if (defaultTime) {
              this.$message({
                type: "warning",
                message: "学习时长不能为空!",
              });
              done();
              return;
            }
          } else {
            // this.$message({
            //   type: "warning",
            //   message:
            //     "时间格式不正确，例如xxx:xx或xx:xx或0x:xx 注意：冒号为英文下的",
            // });
            // done();
            // return;
          }
        } else {
          row.videoTime = "00:00";
        }
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
    formatDuring(mss) {
      var second = Math.ceil(mss);
      var minute = 0;
      var hour = 0;
      if (second > 0) {
        minute = parseInt(second / 60);
        second = parseInt(second % 60);
        if (minute > 60) {
          hour = parseInt(minute / 60);
          minute = parseInt(minute % 60);
        }
      }
      var time = "";
      if (minute == 0) {
        time += "00:" + parseInt(second);
      } else {
        time += parseInt(second);
      }
      if (minute != 0) {
        if (minute > 9) {
          if (time == 0) {
            time = parseInt(minute) + ":" + time + "0";
          } else {
            time = parseInt(minute) + ":" + time;
          }
        } else {
          time = "0" + parseInt(minute) + ":" + time;
        }
      }
      if (hour > 0) {
        time = parseInt(hour) + ":" + time;
      }
      return time;
    },
    beforeOpen(done, type) {
      this.form.videoUrl = "";
      this.form.videoTime = "00:00";
      this.videoName = "";
      if (["edit", "view"].includes(type)) {
        getDetail(this.form.id).then((res) => {
          this.form = res.data.data;
          if (this.form.videoUrl !== "") {
            getDetailByIdVarchar(this.form.videoUrl).then(
              (res) => {
                console.log("getDetailByIdVarchar", res.data.data);
                this.videoName = res.data.data.name;
                this.form.videoTime = this.formatDuring(
                  res.data.data.videoDuration
                );

                if (typeof this.videoName == "undefined") {
                  this.form.videoUrl = "";
                  this.form.videoTime = "";
                  this.videoName = "";
                }
              },
              (error) => {
                this.form.videoUrl = "";
                this.form.videoTime = "";
                this.videoName = "";
              }
            );


            // this.documentName = row.name;
          }

          if (this.form.documentUrl) {
            getResourceDetail({
              fileUrl: this.form.documentUrl
            }).then(
              (res) => {
                console.log("getResourceDetail", res.data.data);
                this.documentName = res.data.data.name;
                this.$forceUpdate()
                // this.form.videoTime = this.formatDuring(
                //   res.data.data.videoDuration
                // );

                // if (typeof this.videoName == "undefined") {
                //   this.form.videoUrl = "";
                //   this.form.videoTime = "";
                //   this.videoName = "";
                // }
              },
              (error) => {
                this.form.documentUrl = "";
                //this.form.videoTime = "";
                this.documentName = "";
              }
            );
          }


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
        const data = res.data.data;

        // this.page.total = data.total;
        this.data = data;
        this.dataBack = data
        this.loading = false;
        this.selectionClear();
      });
    },
    refreshChange() {
      this.onLoad(this.page, this.query);
    },
    showKpointDialo(mk,index) {
      this.selectMk = mk
      this.dialogFormVisible = true;
    },
    showMkDialo() {
      this.dialogMkFormVisible = true;
    },
    addKponint() {
      let row = {
        courseId: this.courseId,
        name: this.name,
        parentId: this.selectMk?this.selectMk.id:"0",
        sort: "0",
        kpointType: "0",
      };
      add(row).then(
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
      this.name = "";
      this.dialogFormVisible = false;
    },
    submitMk() {
      let row = {
        courseId: this.courseId,
        name: this.name,
        parentId: "0",
        sort: "0",
        kpointType: "-1",//模块
      };
      add(row).then(
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
      this.name = "";
      this.dialogMkFormVisible = false;
    },
  },
  components: {
    teacher: teacher,
    courseHomework,
    courseTask,
    shicao,
    composeExampaper,
    addexam,
    chooseVideosource: chooseVideosource,
    courseMaterialChoose: courseMaterialChoose,
    courseMaterial: courseMaterial,
  },
  created: function () { },
};
</script>

<style scoped></style>
