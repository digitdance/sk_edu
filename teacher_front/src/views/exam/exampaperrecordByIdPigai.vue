<template>
  <basic-container>
    <avue-crud :option="option"
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
               @on-load="onLoad">
      <template slot="menuLeft">
        <!-- <el-button type="danger"
                   size="small"
                   icon="el-icon-delete"
                   plain
                   v-if="permission.questionrecord_delete"
                   @click="handleDelete">删 除
        </el-button> -->

       

      </template>

      <template slot-scope="scope" slot="qst_contentForm">
        <div>
          <div v-html="scope.row.qst_content">
          </div>
        </div>
      </template>  
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
      
          
          <div style="max-height: 50px;overflow: hidden;" v-html="scope.row.qst_content">
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
            <el-button
          type="text"
          size="small"
          icon="el-icon-view"
          @click="previewFile(scope.row)"
          >预览</el-button
        >
        <el-button
          type="text"
          size="small"
          icon="el-icon-view"
          @click="download(scope.row.useranswer)"
          >下载</el-button
        >
<!-- 
            <a href="javascript:void()" @click="download(scope.row.useranswer)">预览</a>
            <a href="javascript:void()" @click="download(scope.row.useranswer)">下载</a> -->
          </div>
        </div>
      </template>  



    </avue-crud>


  </basic-container>
</template>

<script>
  import {getList, getDetail, add, update, remove} from "@/api/exam/questionrecord";
  import {mapGetters} from "vuex";
  import examAnalysisContent from '@/views/exam/pigai/examAnalysisContent'
  export default {
    components: {
     examAnalysisContent
    },
    data() {
      return {
        form: {},
        examRecord:{},
        examInfo:{},
        query: {},
        dialogRecordVisible:false,
        loading: true,
        page: {
          pageSize: 10,
          currentPage: 1,
          total: 0
        },
        selectionList: [],
        option: {
          height: 'auto',
          calcHeight: 80,
          searchShow: false,
          header:false,
          searchMenuSpan: 6,
          tip: false,
          border: true,
          editBtnText:"批改",
          index: false,
          viewBtn: false,
          editBtn: true,
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
              label: "题型",
              prop: "qst_type",
              editDisplay:false,
              type: "select",
              readonly:true,
              dataType: "number",
              dicUrl: "/api/crazy-system/dict/dictionary?code=exam_question_type",
              props: {
                label: "dictValue",
                value: "dictKey"
              },
              rules: [{
                required: true,
                message: "请输入题型",
                trigger: "blur"
              }]
            },
            {
              label: "题目",
              prop: "qst_content",
              formslot:true,
              readonly:true,
              rules: [{
                required: true,
                message: "请输入题目",
                trigger: "blur"
              }]
            },
            {
              label: "用户",
              prop: "user_name",
              formslot:true,
              readonly:true,
              rules: [{
                required: true,
                message: "请输入用户",
                trigger: "blur"
              }]
            },
            {
              label:"最高分",
              prop: "score",
              formslot:true,
              readonly:true,
              rules: [{
                required: true,
                message: "请输入最高分",
                trigger: "blur"
              }]
            },
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
          addBtn: this.vaildData(this.permission.questionrecord_add, false),
          viewBtn: this.vaildData(this.permission.questionrecord_view, false),
          delBtn: this.vaildData(this.permission.questionrecord_delete, false),
        //  editBtn: this.vaildData(this.permission.questionrecord_edit, false)
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
      download(row){

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
        if (["edit", "view"].includes(type)) {
          getDetail(this.form.id).then(res => {

            if(res.data.data.useranswer){
              res.data.data.useranswer = JSON.parse(res.data.data.useranswer)
            }
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
        this.query.paperId = this.exam.id
        this.query.clazzId = this.exam.clazzId
        getList(page.currentPage, page.pageSize, Object.assign(params, this.query)).then(res => {
          const data = res.data.data;
          this.page.total = data.total;
          this.data = data.records;
          this.loading = false;
          this.selectionClear();
        });
      }
    },
    props:{
      exam: Object,
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
