<template>
  <basic-container>
    <div>
      <el-table :data="tableData" border style="width: 100%">
        <!-- 姓名列（文本输入） -->

        <el-table-column
          v-for="(it,index) in stuList" :key="index"
          :prop="it"
          :label="it"
          width="180">
          <template #default="{ row }">
              <el-input v-if="row.editing&&it=='rate'" v-model="row['权重']" />
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

      <!-- 新增行按钮 -->
      <div class="add-row">
        <el-button type="primary" @click="handleAdd">新增行</el-button>
      </div>

    </div>
  </basic-container>
</template>

<script>

import {
  pinfenStatistic,
  getDetail,
  add,
  update,
  remove,
} from "@/api/edu/coursestudyhistory";
import {
  getStudentsClazz,
} from "@/api/user/student";

  export default {
  name: 'EditableTable',
  data() {
    return {
      stuList:[],
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
    onLoad(){
      var that = this
      pinfenStatistic(1).then(res=>{
          console.log("pinfenStatistic",res.data)
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
                 records[key]["分值"] = (ss*base).toFixed(2)
              })
              records[key]['分项'] = that.itemMap[key]
              
              scoList.push(records[key])
            });
            stuList.push("分值")
            this.stuList = stuList
            this.tableData = scoList

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
      Object.assign(row, row.editData)
      row.editing = false
      delete row.editData
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
