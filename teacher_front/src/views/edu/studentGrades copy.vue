<template>
    <basic-container>
      <div>
  

        <!-- 新增行按钮 -->
        <div class="add-row" style="margin-bottom: 20px;">
            <el-button type="primary" @click="exportExcel">打印</el-button>
        </div>
  
        <el-table :data="tableData" ref="elTable" border style="width: 100%">
          <!-- 姓名列（文本输入） -->
  
          <el-table-column
            v-for="(it,index) in stuList" :key="index"
            :prop="it"
            :label="it"
            width="180">
            <template #default="{ row }">
                <el-input  v-if="row.editing&&it=='权重'" v-model="row['权重']" />
                <!-- <span v-else-if="it!='editing'&&it!='权重'&&it!='分项'">{{ row['权重']*row[it] }}</span> -->
                <span v-else>{{ row[it] }}</span>
              </template>
          </el-table-column>
  
          <!-- <templete v-for="(it,index) in ['张','李']" :key="index">
            <el-table-column  :prop="it" label="姓名">
              <template #default="{ row }">
                <el-input v-if="row.editing" v-model="row.editData.name" />
                <span v-else>{{ row.name }}</span>
              </template>
            </el-table-column>
          </templete> -->
  
          <!-- 操作列 -->
          <el-table-column label="操作" width="180">
            <template #default="{ row, $index }">
              <template v-if="row.editing">
                <el-button size="small" @click="handleSave(row, $index)">保存</el-button>
                <el-button size="small" @click="handleCancel(row)">取消</el-button>
              </template>
              <template v-else>
                <el-button size="small" @click="handleEdit(row)">编辑</el-button>
                <el-button size="small" type="danger" @click="handleDelete($index)">删除</el-button>
              </template>
            </template>
          </el-table-column>
        </el-table>
  
      
  
      </div>
    </basic-container>
  </template>
  
  <script>
  
  import {
    pinfenStatisticByStudent,
    updateQuanzhong,
    getDetail,
    add,
    update,
    remove,
  } from "@/api/edu/coursestudyhistory";
  import {
    getStudentsClazz,
  } from "@/api/user/student";
  
  import * as XLSX from 'xlsx';
  
  import FileSaver from 'file-saver'
  
    export default {
    name: 'EditableTable',
    data() {
      return {
        stuList:[],
        tableDataBack:[],
        itemMap:{
          t_exam:"考试综合",
          exam:"考试",
          kpoint_study:"课堂",
          shixun:"实训",
          t_shixun:"实训综合",
          t_kpoint_study:"课堂综合"
        },
        tableData: [
          { 分项: 'ab', 张: 0, 李: 1,rate:"99",editing:false },
          { 分项: 'dd', 张: 0, 李: 1 ,rate:"88",editing:false}
        ],
        genderMap: {
          male: '男',
          female: '女'
        }
      }
    },
    watch: {
      clazzId: {
        deep: true,
        immediate:true,
        handler(newVal) {
          //this.query.kpointId = newVal;
          this.onLoad();
        },
      }
    },
    methods: {
      save(e){
        savePinfen()
      },
      changeRate(e){
        console.log("changeRate",e)
      },
      exportExcel() {
        // 从Table组件获取完整数据源
        const data = this.$refs.elTable.data
        console.log("data",data)
        // 处理表头数据（假设columns是Table的列配置）
        const headers = this.stuList.map(col => col)
        console.log("headers",headers)
        // 处理表格数据
        const exportData = data.map(row => {
          return headers.map((_, index) => {
            const prop = this.stuList[index]
            console.log("prop=>",prop)
            return row[prop]
          })
        })
        console.log("exportData",exportData)
        // 合并表头和数据
        const wsData = [headers, ...exportData]
        
        console.log("XLSX",XLSX)
        // 创建工作表
        const worksheet = XLSX.utils.aoa_to_sheet(wsData)
        
        // 创建工作簿并添加工作表
        const workbook = XLSX.utils.book_new()
        XLSX.utils.book_append_sheet(workbook, worksheet, 'Sheet1')
        
        // 导出文件
        const excelBuffer = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' })
        const blob = new Blob([excelBuffer], { type: 'application/octet-stream' })
        FileSaver.saveAs(blob, '成绩表.xlsx')
      },
      changeLast(){
        var scoreMap ={}
        this.tableDataBack.map((item,index)=>{
         // if
          Object.keys(item).forEach(function (key) {
              if(key=='分项'){
                scoreMap[key] = ""
              }else if(key=='权重'){
                scoreMap[key] = ""
              }else if(key=='editing'){
                scoreMap[key] = false
              }else{
                if(!scoreMap[key]){
                  scoreMap[key] = 0
                  scoreMap[key]+=(Number(item[key])*Number(item['权重']))
                }else{
                  scoreMap[key]+=(Number(item[key])*Number(item['权重']))
                }
              }
          })
          //
          console.log("scoreMap",scoreMap)
        })
        this.tableData[this.tableData.length-1] = scoreMap
      },
      onLoad(){
        var that = this
        pinfenStatisticByStudent(1).then(res=>{
            console.log("pinfenStatisticByStudent",res.data)
            var records = res.data.data
            getStudentsClazz({
              courseId:this.clazzId,
              pageSize:100000
            }).then(res=>{
              var students = res.data.data.records
              var stuList = ['分项','权重']
              students.map(st=>{
                stuList.push(st.realName)
              })
              var scoList = []
              var keys = Object.keys(records)
              console.log("keys",keys)
              var base =  (100/(keys.length)/100)
              var allScore = 0
              var allScoreMap={}
                Object.keys(records).forEach(function (key) {
                  var scores =  records[key]
                  students.map(stu=>{
                    var ss = 0;
                    records[key]["权重"] = base.toFixed(2)
                    if(scores[stu.id]){
                      records[key][stu.realName] = records[key][stu.id] 
                      ss = records[key][stu.id] 
                    }else{
                      records[key][stu.realName] = 0 
                    }
                    allScore+=Number(ss*base)
                    allScoreMap[stu.realName] = allScore
                    //records[key]["分值"] = (ss*base).toFixed(2)
                  })
                  records[key]['分项'] = that.itemMap[key]
                  records[key]['editing'] = false
                  // records[key]['rate'] = 0
                  scoList.push(records[key])
                });
                //console.log("allScoreMap",allScoreMap)
                allScoreMap['分项']='总分'
                allScoreMap['editing'] = false
                allScoreMap['权重'] = ""
                //records[key]['rate'] = 0
                console.log("scoList",scoList)
                scoList.push(allScoreMap)
                //stuList.push("分值")
                this.stuList = stuList
                //scoList.push(records[key])
                this.tableData = scoList
                this.tableDataBack = scoList
                //this.changeLast()
            })
            console.log("records=>",records)
        })
  
      },
      // 进入编辑模式
      handleEdit(row) {
        row.editData = { ...row }
        row.editing = true
      },
      // 保存数据
      handleSave(row, index) {
        console.log("handleSave",row)
  
        if(row['分项']=='考试综合'){
          updateQuanzhong({
            type:'t_exam',
            quanzhong:row['权重']
          })
        }
        if(row['分项']=='课堂综合'){
          updateQuanzhong({
            type:'t_exam',
            quanzhong:row['权重']
          })
        }
        if(row['分项']=='实训综合'){
          updateQuanzhong({
            type:'t_exam',
            quanzhong:row['权重']
          })
        }
  
        this.changeLast()
        //Object.assign(row, row.editData)
        row.editing = false
       // delete row.editData
        this.$message.success('保存成功')
      },
      // 取消编辑
      handleCancel(row) {
        row.editing = false
        delete row.editData
      },
      // 删除行
      handleDelete(index) {
        this.$confirm('确定要删除此行吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.tableData.splice(index, 1)
          this.$message.success('删除成功')
        })
      },
      // 新增行
      handleAdd() {
        const newRow = {
          id: Date.now(),
          name: '',
          age: 0,
          gender: 'male',
          editing: true,
          editData: {
            name: '',
            age: 0,
            gender: 'male'
          }
        }
        this.tableData.push(newRow)
      }
    }
  }
  </script>
  
  <style lang="scss">
    #app {
      width: 100%;
      height: 100%;
      overflow: visible;
    }
  </style>
  