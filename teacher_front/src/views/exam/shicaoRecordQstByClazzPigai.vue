<template>
  <basic-container>
    <avue-crud :option="option"
               :table-loading="loading"
               :data="data"
               :page="page"
               :permission="permissionList"
             
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
               @on-load="onLoad">
      <template slot="menuLeft">
        <!-- <el-button type="danger"
                   size="small"
                   icon="el-icon-delete"
                   plain
                   v-if="permission.questionrecord_delete"
                   @click="handleDelete">删 除
        </el-button> -->

        <el-button type="warning"
                   size="small"
                   plain
                   icon="el-icon-download"
                   @click="handleExport">导出
        </el-button>

      </template>
      <template slot="menu" slot-scope="scope">
        <el-button type="danger"
                   size="small"
                   icon="el-icon-edit"
                   plain
                   @click="pigai(scope.row)">批 改
        </el-button>
      </template>
      <!-- <template slot-scope="scope" slot="qst_contentForm">
        <div>
          <div v-html="scope.row.qst_content">
          </div>
        </div>
      </template>   -->
      <template slot-scope="scope" slot="user_nameForm">
        <div>
          <div v-html="scope.row.user_name">
          </div>
        </div>
      </template>  
      <template slot-scope="scope" slot="addtimeForm">
        <div>
          <div v-html="scope.row.addtime">
          </div>
        </div>
      </template> 
      <template slot-scope="scope" slot="qst_contentForm">
        <div>
          
          <div style="max-height: 50px;overflow: hidden;" v-html="scope.row.qst_content">
          </div>
        </div>
      </template>  
      <template slot-scope="scope" slot="scoreForm">
        <div>
          <div v-html="scope.row.score">
          </div>
        </div>
      </template>  
      
      <template slot-scope="scope" slot="useranswerForm">
        <div>
          <div v-html="scope.row.useranswer.content">
          </div>
          <div >
            <a href="javascript:void()" @click="download(scope.row.useranswer)">下载附件</a>
          </div>
        </div>
      </template>  
    </avue-crud>


    <el-dialog  title="批 改"  :visible.sync="dialogPigaiVisible"  style="padding-top: 0px" append-to-body>
      <div style="height: 280px;" >
         
              <el-form :model="pwdForm">
                <el-form-item label="作答内容" label-width="120px">
                  <div v-if="dialogPigaiVisible&&recordInfo.useranswer.content" v-html="recordInfo.useranswer.content">
                  </div>
                </el-form-item>
                <el-form-item label="附件" label-width="120px">
                  <a v-if="dialogPigaiVisible&&recordInfo.useranswer.attachment" :href="recordInfo.useranswer.attachment" target="_blank" style="co" >
                          <em class="imv2-folder type "></em>
                          附件下载
                  </a>
                </el-form-item>
                <el-form-item label="分值" label-width="120px">
                  <el-input
                    v-model="score"
                    placeholder="请输入分值"
                    style="width: 70%"
                  ></el-input>
                </el-form-item>

                <el-form-item  label-width="120px">

                  <el-button @click="dialogPigaiVisible = false">取 消</el-button>
                  <el-button type="primary" @click="replyScore()">确 定</el-button>
               
                </el-form-item>
              </el-form>
             
      </div>
    </el-dialog>

  </basic-container>
</template>

<script>
  import {getUniqList, getDetail, add, update, remove,updateResult} from "@/api/exam/questionrecord";
  import {mapGetters} from "vuex";
  import pigaiContent from '@/views/exam/pigai/pigaiContent'
  import examAnalysisContent from '@/views/exam/pigai/examAnalysisContent'
  export default {
    components: {
     examAnalysisContent,pigaiContent
    },
    data() {
      return {
        form: {},
        examRecord:{},
        recordInfo:{},
        score:0,
        examInfo:{},
        query: {qstType:6,},
        dialogRecordVisible:false,
        loading: true,
        page: {
          pageSize: 10,
          currentPage: 1,
          total: 0,
          
        },
        dialogPigaiVisible:false,
        selectionList: [],
        option: {
          height: 'auto',
          calcHeight: 80,
          searchShow: true,
         // header:false,
          searchMenuSpan: 6,
          tip: false,
          border: true,
          index: false,
          viewBtn: false,
          editBtn: false,
          addBtn: false,
          delBtn:false,
          selection: false,
          menu:true,
          columnBtn:false,
          refreshBtn:false,
          column: [
            {
              label: "编号",
              prop: "qst_id",
              hide:false,
              addDisplay: false,
              editDisplay: false,
              search :true,
              rules: [{
                required: true,
                message: "请输入编号",
                trigger: "blur"
              }]
            },
            {
              label: "课程",
              prop: "course_id",
              type: "select",
              search: true,
              dicUrl: "/api/edu/course/allCourses?sellType=1",
              props: {
                label: "courseName"
              },
              rules: [{
                required: true,
                message: "请选择课程",
                trigger: "blur"
              }]
            },
            {
              label: "任务",
              prop: "qst_id",
              type: "select",
              search: true,
              dicUrl: "/api/exam/question/all?qstType=6",
              props: {
                label: "qstContentShort",
                value: "id"
              },
              rules: [{
                required: true,
                message: "请选择任务",
                trigger: "blur"
              }]
            },
            // {
            //   label: "题型",
            //   prop: "qst_type",
            //   editDisplay:false,
            //   type: "select",
            //   readonly:true,
            //   dataType: "number",
            //   dicUrl: "/api/crazy-system/dict/dictionary?code=exam_question_type",
            //   props: {
            //     label: "dictValue",
            //     value: "dictKey"
            //   },
            //   rules: [{
            //     required: true,
            //     message: "请输入题型",
            //     trigger: "blur"
            //   }]
            // },
          
            {
              label: "学号",
              prop: "user_name",
              formslot:true,
              readonly:true,
              rules: [{
                required: true,
                message: "请输入学号",
                trigger: "blur"
              }]
            },
            // {
            //   label:"最高分",
            //   prop: "score",
            //   formslot:true,
            //   readonly:true,
            //   rules: [{
            //     required: true,
            //     message: "请输入最高分",
            //     trigger: "blur"
            //   }]
            // },
            {
              label:"评分",
              prop: "give_score",
              type:"number",
              rules: [{
                required: true,
                message: "请输入评分",
                trigger: "blur"
              }]
            },
            {
              label: "提交时间",
              prop: "addtime",
              formslot:true,
              readonly:true,
              rules: [{
                required: true,
                message: "请输入提交时间",
                trigger: "blur"
              }]
            },
            {
              label: "批改时间",
              prop: "pigai_time",
              editDisplay:false,
              
              rules: [{
                required: true,
                message: "请输入批改时间",
                trigger: "blur"
              }]
            },
            {
              label: "批改人",
              prop: "pigai_user",
              editDisplay:false,
              rules: [{
                required: true,
                message: "请输入批改人",
                trigger: "blur"
              }]
            },
            {
              label: "回答",
              prop: "useranswer",
              formslot:true,
              row: true,
              hide:true,
              span:24,
              rules: [{
                required: true,
                message: "请输入回答",
                trigger: "blur"
              }]
            },
            // {
            //   label: "正确率",
            //   prop: "accuracy",
            //   formatter: function (row,s) {
            //     return Math.round(row.rightCount/row.count * 10000)/100+"%";
            //   },
            //   rules: [{
            //     required: true,
            //     message: "请输入正确率",
            //     trigger: "blur"
            //   }]
            // },
          ]
        },
        data: []
      };
    },
    computed: {
      ...mapGetters(["permission"]),
      permissionList() {
        return {
        //  addBtn: this.vaildData(this.permission.questionrecord_add, false),
          //viewBtn: this.vaildData(this.permission.questionrecord_view, false),
        //  delBtn: this.vaildData(this.permission.questionrecord_delete, false),
        //  editBtn: this.vaildData(this.permission.questionrecord_edit, false)
        };
      },
      // ids() {
      //   let ids = [];
      //   this.selectionList.forEach(ele => {
      //     ids.push(ele.id);
      //   });
      //   return ids.join(",");
      // }
    },
    methods: {
      replyScore(){
          updateResult(
            {
              id:this.recordInfo.id,
              userScore:this.score
            }
        ).then(res=>{
            this.$message({
              type: "success",
              message: "提交成功!"
            });
            this.dialogPigaiVisible = false
          })
      },
      pigai(e){
        this.dialogPigaiVisible = true
        this.recordInfo = e
        console.log("pigai start",e)
         getDetail(e.id).then(res => {
          console.log("pigai end",res.data)
            if(res.data.data.useranswer){
              res.data.data.useranswer = JSON.parse(res.data.data.useranswer)
            }
            this.recordInfo = res.data.data;
           
          });

       
      },
      download(row){
        console.log("row",row.attachment)
        if(row.attachment){
          window.open(row.attachment)
        }else{
          this.$message({
            type: "warn",
            message: "暂无附件!"
          });
        }
        
      },
      openRecord(row){
        console.log("openRecord",row)
        this.dialogRecordVisible = true
      },
      rowSave(row, done, loading) {
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
        if(row.giveScore&&row.giveScore>row.score){
          this.$message({
            type: "error",
            message: "评分不得高于最高分"
          });
          done();
          return
        }
        if(row.useranswer){
          row.useranswer = JSON.stringify(row.useranswer)
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
            return remove(row.id);
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
        console.log("this.form",this.form)
        if (["edit", "view"].includes(type)) {
          // getDetail(this.form.id).then(res => {

          //   if(res.data.data.useranswer){
          //     //res.data.data.useranswer = JSON.parse(res.data.data.useranswer)
          //   }
          //   this.form = res.data.data;
            
          // });
        }
        done();
      },
      searchReset() {
        this.query = {};
        this.page.currentPage = 1;
        this.onLoad(this.page);
      },
      searchChange(params,done) {
        this.query = params;
        this.page.currentPage = 1;
        this.onLoad(this.page, params);
        done()
      },
      selectionChange(list) {
        this.selectionList = list;
      },
      selectionClear() {
        this.selectionList = [];
        this.$refs.crud.toggleSelection();
      },
      currentChange(currentPage){
        this.page.currentPage = currentPage;
      },
      sizeChange(pageSize){
        this.page.pageSize = pageSize;
      },
      onLoad(page, params = {}) {
        this.loading = true;
        this.query.clazzId = this.clazzId
        this.query.cusId = this.studentId
        this.query.qstType = 6
        getUniqList(page.currentPage, page.pageSize, Object.assign(params, this.query)).then(res => {
          const data = res.data.data;
          this.page.total = data.total;
          this.data = data.records;
          this.loading = false;
          this.selectionClear();
        });
      }
    },
    props:{
      clazzId: Number,
      studentId:Number
    },
    watch: {
      exam: {
        deep: true,
        handler(newVal){
          console.log(newVal)
        }
      }
    },
  };
</script>

<style>
</style>
