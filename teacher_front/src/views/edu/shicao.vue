<template>
  <basic-container>
    <avue-crud :option="option" :search.sync="search" :table-loading="loading" :data="data" :page="page"
      :permission="permissionList" :before-open="beforeOpen" v-model="form" ref="crud" @row-update="rowUpdate"
      @row-save="rowSave" @row-del="rowDel" @search-change="searchChange" @search-reset="searchReset"
      @selection-change="selectionChange" @current-change="currentChange" @size-change="sizeChange"
      @refresh-change="refreshChange" @on-load="onLoad">
      <template slot="menuLeft">


        <el-button type="primary" size="small" plain icon="el-icon-add" @click="showChoose">选择
        </el-button>


        <!-- <el-button type="danger"
                   size="small"
                   icon="el-icon-delete"
                   plain
                   v-if="permission.question_delete"
                   @click="handleDelete">删 除
        </el-button>
        <el-button type="success"
                   size="small"
                   plain
                   icon="el-icon-upload2"
                   @click="handleImport">导入
        </el-button> -->
      </template>
      <template slot="menu" slot-scope="scope">
        <!-- <el-button type="text" size="small" icon="el-icon-record" plain @click="openExamPigaiRecord(scope.row)">实训记录 -->
        </el-button>

      </template>
      <template slot-scope="scope" slot="subjectIdForm">
        <el-cascader placeholder="考题分类" :options="subjectExamTreeList" v-model="form.subjectId"
          :props="{ 'label': 'title', 'emitPath': false }" filterable></el-cascader>
      </template>
      <template slot-scope="scope" slot="qstContentSearch">
        <el-input type="text" v-model="search.qstContent" placeholder="试题题干"></el-input>
      </template>
      <template slot-scope="scope" slot="qstContentForm">
        <avue-ueditor v-model="form.qstContent" :options="ueditorOptions2" style="width:90%;"></avue-ueditor>
      </template>
      <template slot-scope="scope" slot="qstAnalyzeForm">
        <avue-ueditor v-model="form.qstAnalyze" :options="ueditorOptions2" style="width:90%;"></avue-ueditor>
      </template>
      <template slot-scope="scope" slot="evaluationStandardsForm">
        <avue-ueditor v-model="form.evaluationStandards" :options="ueditorOptions2" style="width:90%;"></avue-ueditor>
      </template>


      <template slot-scope="scope" slot="materialForm">
        <el-upload class="upload-demo" ref="upload" :action="`/api/uploadOss?crazy-auth=${token}`" :limit="1"
          :on-success="uploadSuccess" :file-list="fileList">
          <el-button slot="trigger" size="small" type="primary">本地上传<i
              class="el-icon-upload el-icon--right"></i></el-button>
        </el-upload>
      </template>


    </avue-crud>
    <el-dialog title="试题数据导入" append-to-body :visible.sync="excelBox" width="555px">
      <avue-form :option="excelOption" v-model="excelForm" :upload-after="uploadAfter">
        <template slot="excelTemplate">
          <el-button type="primary" @click="handleTemplate()">
            点击下载<i class="el-icon-download el-icon--right"></i>
          </el-button>
        </template>
      </avue-form>
      <el-row v-if="errorMsg != ''" style="top:-45px;left:28px">
        <div>错误信息：</div>
        <el-col :span="2"></el-col>
        <el-col :span="20" style="color: #f56c6c">
          <div
            style="margin:15px 0 0 -35px;padding-left: 19px;overflow: auto; height: 150px;width: 420px;border-radius:5px;border: #d9d9d9 1px dashed"
            v-html="errorMsg"></div>
        </el-col>
      </el-row>
    </el-dialog>


    <el-dialog title="实训记录" :fullscreen="true" :visible.sync="dialogRecordPigaiVisible"
      style="padding-top: 0px;width:100%" append-to-body>
      <exampaperrecordQstByIdPigai v-if="dialogRecordPigaiVisible" :qstId="qstId"></exampaperrecordQstByIdPigai>
    </el-dialog>

    <el-dialog title="任务选择" width="90%" :visible.sync="dialogChooseVisible" style="padding-top: 0px;width:100%"
      append-to-body>
      <shicaoChoose @choose="chooseChange" v-if="dialogChooseVisible" :qstId="qstId"></shicaoChoose>
    </el-dialog>

  </basic-container>
</template>

<script>
import { getToken } from "@/util/auth";
import exampaperrecordQstByIdPigai from "../exam/exampaperrecordQstByIdPigai";
import { getQuestionsByids, getDetail, add, update, remove, removeMiddle, subjectExamTreeList, questionTypeList, getShicaoList } from "@/api/exam/question";
import { mapGetters } from "vuex";
import { add2 as addPaper, saveQuestion } from "@/api/exam/exampaper";
import shicaoChoose from "./shicaoChoose";


let _this;

export default {
  components: {
    exampaperrecordQstByIdPigai: exampaperrecordQstByIdPigai, shicaoChoose
  },
  data() {
    var isAsrValue = (rule, value, callback) => {
      let checkeArrString = '';
      if (_this.form.qstType == 2) {
        for (let i = 0; i < _this.checkedArr.length; i++) {
          if (_this.checkedArr[i] != undefined && _this.checkedArr[i] != null && _this.checkedArr[i] != '') {
            if (i == 0) {
              checkeArrString += _this.checkedArr[i];
            } else {
              checkeArrString = checkeArrString + "," + _this.checkedArr[i];
            }
          }
        }
        if (checkeArrString === '') {
          callback(new Error("请选择答案"));
        } else {
          _this.form.isAsr = checkeArrString;
          callback();
        }
      }
      if (_this.form.qstType == 4) {
        for (let i = 0; i < _this.optionArr.length; i++) {
          if (_this.optionArr[i].value != undefined && _this.optionArr[i].value != null && _this.optionArr[i].value != '') {
            if (i == 0) {
              checkeArrString += _this.optionArr[i].value;
            } else {
              checkeArrString = checkeArrString + "," + _this.optionArr[i].value;
            }
          }
        }
        if (checkeArrString === '') {
          callback(new Error("请选择答案"));
        } else {
          _this.form.isAsr = checkeArrString;
          callback();
        }
      }
      if (_this.form.qstType == 1 || _this.form.qstType == 3) {
        if (value === '') {
          callback(new Error("请选择答案"));
        } else {
          callback();
        }
      }
      if (_this.form.qstType == 5) {
        callback();
      }

      if (_this.form.qstType == 6) {
        callback();
      }
    };
    var optionListValue = (rule, value, callback) => {
      let error = '';
      let y = 1;
      for (let i = 0; i < _this.optionArr.length; i++) {

        console.log("_this.optionArr", _this.optionArr)
        if (_this.optionArr[i].value == undefined || _this.optionArr[i].value == null || _this.optionArr[i].value === '') {
          error += "第" + y + "行选项为空。";
        } else {
          _this.optionArr[i].editor == "false";
        }
        y += 1;
      }
      if (error === '') {
        console.log("执行！！！")
        console.log(JSON.stringify(_this.optionArr));
        _this.form.optionList = JSON.stringify(_this.optionArr);
        callback();
      } else {
        console.log("出错")
        callback(new Error(error));
      }
    };
    return {
      qstContentFlag: false,
      fileList: [],
      materialUploadVisible: true,
      qstAnalyzeFlag: false,
      excelBox: false,
      errorMsg: "",
      dialogRecordPigaiVisible: false,
      qstId: 0,
      token: getToken(),
      questionTypeList: [],
      ueditorOptions: {
        //普通图片上传
        //action: "/api/common/upload?path=course",
        action: "/api/uploadOss?path=course",
        customConfig: {
        },
        props: {
          res: "data",
          url: 'url'
        },
      },
      ueditorOptions2: {
        //普通图片上传
        action: "/api/uploadOss?path=course",
        customConfig: {
        },
        props: {
          res: "data",
          url: 'url'
        },
      },
      isAsr: "",
      optionArr: [{
        editor: false,
        value: ""
      }, {
        value: "",
        editor: false,
      }],
      search: {},
      checkedArr: [],
      form: {},
      query: {},
      dialogChooseVisible: false,
      loading: true,
      page: {
        pageSize: 10,
        currentPage: 1,
        total: 0
      },
      selectionList: [],
      subjectExamTreeList: [],
      option: {
        searchShow: true,
        align: "center",
        tip: false,
        border: true,
        index: false,
        viewBtn: false,
        addBtn: false,
        editBtn: false,
        selection: false,
        columnBtn: true,
        labelWidth: 130,
        column: [
          // {
          //   label: "试题id",
          //   prop: "id",
          //   hide:false,
          //   addDisplay: false,
          //   editDisplay: false,
          //   search :true,
          //   rules: [{
          //     required: true,
          //     message: "请输入试题id",
          //     trigger: "blur"
          //   }]
          // },
          // {
          //   label: "考题分类",
          //   prop: "subjectId",
          //   type:"cascader",
          //   search:true,
          //   formslot:true,
          //   dicUrl: "/api/edu/subject/exam/tree",
          //   props: {
          //     label: "title"
          //   },
          //   tip:"试题考题分类只可以选择二级分类",
          //   rules: [{
          //     required: true,
          //     message: "请选择试题考题分类",
          //     trigger: "blur"
          //   }]
          // },
          {
            label: "任务描述",
            prop: "qstContent",
            type: "ueditor",
            component: "avueUeditor",
            showOverflowTooltip: true,
              overHidden: true,
            search: false,
            searchslot: true,
            formslot: true,
            options: {
              //普通图片上传
              action: "/api/uploadOss?path=course",
              props: {
                res: "data",
                url: 'url'
              },
            },
            rules: [{
              required: true,
              message: "请输入题干",
              trigger: "blur"
            }],
            span: 24,
          },
          // {
          //   label: "试题类型",
          //   prop: "qstType",
          //   type: "select",
          //   dataType: "number",
          //   search :true,
          //   formslot:true,
          //   span: 24,
          //   value:1,
          //   props: {
          //     label: "dictValue",
          //     value: "dictKey"
          //   },
          //   dicUrl: "/api/crazy-system/dict/dictionary?code=exam_question_type",
          //   rules: [{
          //     required: true,
          //     message: "请选择试题类型",
          //     trigger: "blur"
          //   }]
          // },
          // {
          //   label: "填空题类型",
          //   prop: "vacancyType",
          //   addDisplay: false,
          //   editDisplay: false,
          //   hide: true,
          //   rules: [{
          //     required: true,
          //     message: "请输入填空题类型",
          //     trigger: "blur"
          //   }]
          // },
          // {
          //   label: "选项",
          //   prop: "optionList",
          //   hide: true,
          //   formslot:true,
          //   display: true,
          //   span:24,
          //   rules: [{ validator: optionListValue, trigger: 'blur' }]
          // },
          // {
          //   label: "分析材料",
          //   prop: "material",
          //   hide: true,
          //   formslot: true,
          //   value: "",
          //   display: true,
          //   span: 24
          // },
          // {
          //   label: "正确答案",
          //   prop: "isAsr",
          //   formslot:true,
          //   span:24,
          //   rules: [{ validator: isAsrValue, trigger: 'blur' }]
          // },
          // {
          //   label: "难度",
          //   prop: "level",
          //   type: "select",
          //   dataType: "number",
          //   search: true,
          //   span: 24,
          //   value: 1,
          //   props: {
          //     label: "dictValue",
          //     value: "dictKey"
          //   },
          //   dicUrl: "/api/crazy-system/dict/dictionary?code=exam_degree",
          //   rules: [{
          //     required: true,
          //     message: "请选择试题难度",
          //     trigger: "blur"
          //   }]
          // },
          // {
          //   label: "排序",
          //   prop: "sort",
          //   value: 0,
          //   hide: true,
          //   addDisplay: false,
          //   editDisplay: false,
          //   rules: [{
          //     required: true,
          //     message: "请输入排序",
          //     trigger: "blur"
          //   }]
          // },
          // {
          //   label: "解析",
          //   prop: "qstAnalyze",
          //   hide: true,
          //   formslot: true,
          //   type: "ueditor",
          //   component: "avueUeditor",
          //   options: {
          //     //普通图片上传
          //     action: "/api/uploadOss?path=course",
          //     props: {
          //       res: "data",
          //       url: 'url'
          //     },
          //   },
          //   span: 24,
          // },
          // {
          //   label: "评分标准",
          //   prop: "evaluationStandards",
          //   hide: true,
          //   formslot: true,
          //   type: "ueditor",
          //   component: "avueUeditor",
          //   options: {
          //     //普通图片上传
          //     action: "/api/uploadOss?path=course",
          //     props: {
          //       res: "data",
          //       url: 'url'
          //     },
          //   },
          //   span: 24,
          // },
          // {
          //   label: "创建人",
          //   prop: "author",
          //   value: "",
          //   editDisplay: false,
          //   addDisplay: false,
          //   span: 24
          // },
          // {
          //   label: "创建时间",
          //   prop: "createTime",
          //   addDisplay: false,
          //   editDisplay: false,
          //   rules: [{
          //     required: true,
          //     message: "请输入添加时间",
          //     trigger: "blur"
          //   }]
          // },
          /*{
            label: "试题作者",
            prop: "author",
            hide:true,
            addDisplay: false,
            editDisplay: false,
          },*/
        ]
      },
      data: [],
      excelForm: {},
      excelOption: {
        submitBtn: false,
        emptyBtn: false,
        column: [
          {
            label: '模板上传',
            prop: 'excelFile',
            type: 'upload',
            drag: true,
            loadText: '模板上传中，请稍等',
            span: 24,
            showFileList: false,
            propsHttp: {
              res: 'data'
            },
            tip: '请上传 .xls,.xlsx 标准格式文件',
            action: "/api/exam/question/import-exam"
          },
          {
            label: '模板下载',
            prop: 'excelTemplate',
            formslot: true,
            span: 24,
          }
        ]
      }
    };
  },
  props: {
    kpoint: Object,
    paperId: Number
  },
  watch: {
    kpoint: {
      deep: true,
      immediate: true,
      handler(newVal) {
        console.log("materialIds change")
        //this.query.materialIds = newVal;
        // if(newVal){
        //   this.ids = newVal.split(",")
        // }
        this.onLoad(this.page);
      },
    },
  },
  computed: {
    ...mapGetters(["permission"]),
    permissionList() {
      return {
        addBtn: this.vaildData(this.permission.question_add, false),
        viewBtn: this.vaildData(this.permission.question_view, false),
        delBtn: this.vaildData(this.permission.question_delete, false),
        editBtn: this.vaildData(this.permission.question_edit, false)
      };
    },
    ids() {
      let ids = [];
      this.selectionList.forEach(ele => {
        ids.push(ele.id);
      });
      return ids.join(",");
    }
  },
  methods: {
    showChoose() {
      this.dialogChooseVisible = true
    },
    chooseChange(items) {
      console.log("items", items)
      var allQst = []
      items.map(item => {
        const find = this.data.find(question => item.id == question.qstId);
        if (!find) {
          allQst.push(item.id)
        }
      })
      this.data.map(item2 => {
        allQst.push(item2.qstId)
      })
      if (allQst.length == 0) {
        retuen
      }

      getQuestionsByids(allQst.join(",")).then(res => {
        console.log("getQuestions", res)
        //score: questions.length,
        var questions = res.data.data
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
          courseId: this.kpoint.courseId,
          kpointId: this.kpoint.id,
          traffic: "0",
          type: "6",
          questionArr: questions,
          paperId: this.paperId,
          title: "",
        }
        exampaperArr.push(data);
        saveQuestion(exampaperArr, this.paperId).then((res) => {
          this.$message({
            type: "success",
            message: "保存成功",
          });
          this.dialogChooseVisible = false
          this.onLoad(this.page)
        });
      })
    },
    openExamPigaiRecord(qst) {
      console.log("openExamPigaiRecord", qst)
      this.qstId = qst.id;
      this.dialogRecordPigaiVisible = true;
    },
    uploadSuccess(res) {
      var that = this
      console.log("uploadSuccess", res)
      this.$refs.crud.option.column.filter((item) => {
        if (item.prop == "material") {
          console.log("item==>", item)
          res.data.name = res.data.fileName
          console.log("set material value", res.data.url)
          that.fileList = [res.data]
          //item.value = res.data.url
          //that.$set(item.value,res.data.url)
        }
      })
    },
    qstContentBlur() {
      this.qstContentFlag = true
    },
    handleImport() {
      this.excelBox = true;
    },
    handleTemplate() {
      window.open(`static/template/批量生成试题模版.xlsx`);
      // window.open(`/api/user/student/export-template?crazy-auth=${getToken()}`);
    },
    refreshChange() {
      this.onLoad(this.page, this.query);
    },
    uploadAfter(res, done, loading, column) {
      if (Object.prototype.toString.call(res) == "[object Error]") {
        this.errorMsg = res.message
        done();
        return;
      }
      done();
      this.errorMsg = ""
      this.excelBox = false;
      this.refreshChange();
    },
    qstContentFocus() {
      this.qstContentFlag = false
    },
    qstAnalyzeBlur() {
      this.qstAnalyzeFlag = true
    },
    qstAnalyzeFocus() {
      this.qstAnalyzeFlag = false
    },
    changeQues(value) {

      console.log("changeQues", value)

      if (value == "3") {
        _this.form.isAsr = "A"
      }
      if (value == "2") {
        _this.checkedArr = ["A"]
      }
      if (value == "4" || value == "3" || value == "5" || value == "6") {
        var optionList = _this.findObject(_this.option.column, 'optionList')
        optionList.display = false
        _this.optionArr = [{
          value: "",
          editor: false,
        }]
      } else {
        _this.form.isAsr = "A"
        var optionList = _this.findObject(_this.option.column, 'optionList')
        optionList.display = true
        _this.optionArr = [{
          value: "",
          editor: false,
        }, {
          value: "",
          editor: false,
        }, {
          value: "",
          editor: false,
        }, {
          value: "",
          editor: false,
        }]
      }
    },
    openEditor(index) {
      this.optionArr.forEach(function (e) {
        e.editor = false
      })
      this.optionArr[index].editor = true
    },
    optionPlus() {
      this.optionArr.push({
        value: "",
        editor: false,
      })
    },
    optionMinus(index) {
      this.optionArr.splice(index, 1)
    },
    indexToString(index) {
      var charcode;
      return index.toString(26).split("").map(function (item, i) {
        charcode = item.charCodeAt(0);
        charcode += (charcode >= 97 ? 10 : 49);
        return String.fromCharCode(charcode)
      }).join("").toUpperCase();
    },
    rowSave(row, done, loading) {

      row.kpointId = this.kpoint.id
      row.courseId = this.kpoint.courseId
      row.qstType = 6
      console.log("rowSave", this.fileList, row)
      row.material = ""
      if (this.fileList.length > 0) {
        row.material = JSON.stringify(this.fileList)
      }

      add(row).then(() => {
        done();
        this.onLoad(this.page);
        this.$message({
          type: "success",
          message: "操作成功!"
        });
      }, error => {
        window.console.log(error);
        loading();
      });
    },
    rowUpdate(row, index, done, loading) {
      row.material = ""
      if (this.fileList.length > 0) {
        row.material = JSON.stringify(this.fileList)
      }
      update(row).then(() => {
        done();
        this.onLoad(this.page);
        this.$message({
          type: "success",
          message: "操作成功!"
        });
      }, error => {
        window.console.log(error);
        loading();
      });
    },
    rowDel(row) {
      this.$confirm("确定将选择数据删除?", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          return removeMiddle(row.id);
        })
        .then(() => {
          this.onLoad(this.page);
          this.$message({
            type: "success",
            message: "操作成功!"
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
        type: "warning"
      })
        .then(() => {
          return remove(this.ids);
        })
        .then(() => {
          this.onLoad(this.page);
          this.$message({
            type: "success",
            message: "操作成功!"
          });
          this.$refs.crud.toggleSelection();
        });
    },
    beforeOpen(done, type) {
      if (["edit", "view"].includes(type)) {
        getDetail(this.form.id).then(res => {
          this.form = res.data.data;
          if (res.data.data.qstType == 1 || res.data.data.qstType == 2) {
            _this.optionArr = JSON.parse(res.data.data.optionList);
          }
          if (res.data.data.qstType == 2) {
            _this.checkedArr = res.data.data.isAsr.split(",")
            // for(let i=0;i<res.data.data.isAsr.split(",").length;i++){
            //   _this.checkedArr = res.data.data.isAsr.split(",")[i]
            // }
          }
          if (res.data.data.qstType == 4) {
            console.log(res.data.data.isAsr.split(","));
            _this.optionArr = [];
            for (let i = 0; i < res.data.data.isAsr.split(",").length; i++) {
              _this.optionArr.push({
                value: res.data.data.isAsr.split(",")[i],
                editor: false,
              });
            }
          }
          let value = res.data.data.qstType
          if (value == "4" || value == "3" || value == "5" || value == "6") {
            console.log("121212")
            var optionList = _this.findObject(_this.option.column, 'optionList')
            optionList.display = false
          } else {
            var optionList = _this.findObject(_this.option.column, 'optionList')
            optionList.display = true
          }

          if (value == "5" || value == "6") {
            this.materialUploadVisible = true
          } else {
            this.materialUploadVisible = false
          }

          if (this.form.material && (value == "5" || value == "6")) {
            this.fileList = JSON.parse(this.form.material)
          }

        });
      }
      if (["add", "view"].includes(type)) {
        this.form = {};
        //this.changeQues(6)
      }
      console.log("22")
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
      done()
    },
    selectionChange(list) {
      this.selectionList = list;
    },
    refreshChange() {
      this.onLoad(this.page, this.query);
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
      this.query.qstType = 6
      this.query.kPointId = this.kpoint.id

      // if (typeof(this.query.subjectId) != "undefined"){
      //   this.query.subjectId = this.query.subjectId[1]
      // }
      getShicaoList(this.paperId).then(res => {
        const data = res.data.data;
        this.page.total = data.length;
        this.data = data;
        this.loading = false;
        this.selectionClear();
      });
    }
  },
  created: function () {
    _this = this;
    subjectExamTreeList().then(res => {
      this.subjectExamTreeList = res.data.data;
    })

    questionTypeList().then(res => {
      let questionTypeList = res.data.data
      questionTypeList.forEach(e => {
        e.dictKey = Number(e.dictKey)
      })
      this.questionTypeList = questionTypeList
    })
  },
};
</script>

<style></style>
