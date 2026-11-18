<template>
  <basic-container>
    <div>

       <el-form :model="tableData">
          <el-form-item label="测验分占比(%)：" label-width="120px" >
            <el-input type="number" v-model="tableData.t_exam" placeholder="请输入测验分占比"  style="width: 70%;" ></el-input>
          </el-form-item>
          <el-form-item label="课堂分占比(%)：" label-width="120px" >
            <el-input type="number"  v-model="tableData.t_kpoint_study" placeholder="请输入课堂分占比" style="width: 70%;" ></el-input>
          </el-form-item>
          <el-form-item label="实训分占比(%)：" label-width="120px" >
            <el-input type="number"  v-model="tableData.t_shixun" placeholder="请输入实训分占比" style="width: 70%;" ></el-input>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="updateSetting">确 定</el-button>
         
        </div>
    </div>
  </basic-container>
</template>

<script>

import {
  getClazzScoreSetting,
  saveClazzScoreSetting,
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
      tableData: {},
      genderMap: {
        male: '男',
        female: '女'
      }
    }
  },
  props: {
    visible: Boolean,
    clazzId: Number
  },
  watch: {
    visible: {
      deep: true,
      immediate:true,
      handler(newVal) {
        //this.query.kpointId = newVal;
        if(newVal){
          this.onLoad();
        }
        
      },
    }
  },
  methods: {
    onLoad(){
      var that = this
      getClazzScoreSetting({clazzId:this.clazzId}).then(res=>{
          
          
          var records = res.data.data
          console.log("setting==>",records)
          if(!records.t_exam){
            records = {
                "t_exam": 30,
                "t_kpoint_study":30,
                "t_shixun": 40
            }
          }

          this.tableData = records

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
    updateSetting() {
      if(!this.tableData['t_exam']||!this.tableData['t_kpoint_study']||!this.tableData['t_shixun']){
        this.$message.error('存在空数据')
        return
      }
      if((parseFloat(this.tableData['t_exam'])+parseFloat(this.tableData['t_kpoint_study'])+parseFloat(this.tableData['t_shixun']))!=100){
        this.$message.error('占比相加不等于100')
        return
      }
      saveClazzScoreSetting({
        id:this.clazzId,
        scoreSetting:JSON.stringify(this.tableData)
      }).then(res=>{
        console.log("save res",res)
          //if(res.success){
            this.$message.success('设置成功')
            this.$emit("close",this.tableData)
         // }
      })
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
