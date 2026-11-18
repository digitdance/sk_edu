<template>
  <div>
    <el-row :gutter="10" class="grid-row">
      <el-col :xs="2" :sm="3" :md="4" :lg="4" :xl="4" class="grid-empty">
        <div class="grid-empty"></div>
      </el-col>
      <el-col :xs="10" :sm="9" :md="8" :lg="8" :xl="8" class="grid-empty">
        <el-tabs v-model="activeName" @tab-click="handleClick">
          <el-tab-pane label="考试通知" name="second">
          <el-table :data="articleList" :show-header="false">
            <el-table-column prop="title" label="消息"> </el-table-column>
            <el-table-column prop="createTime" label="日期" :formatter="formatDate" min-width="25px">
            </el-table-column>
          </el-table>
        </el-tab-pane>
        </el-tabs>
      </el-col>
      <el-col :xs="2" :sm="3" :md="4" :lg="4" :xl="4" class="grid-empty">
        <div></div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import "swiper/css/swiper.css";
import { articleList} from "@/api";

export default {
  name: "banner",
  data() {
    return {
      banLst: [],
      activeName: "second",
      articleList:[],
      isReg: false,
      name: "",
      password: "",
      repeat: ""
    };
  },
  mounted: function() {
    this.init();
  },
  components: {},
  methods: {
    init(){
      this.getArticleList(1,3,371);
    },
    formatDate(row, column) {
      // 获取单元格数据
      let data = row[column.property];
      if(data == null) {
        return null
      }
      let dt = new Date(data);
      console.log(dt);
      return dt.getFullYear() + '-' + (dt.getMonth() + 1) + '-' + dt.getDate() + ' ';
    },
    getArticleList(current,size,subjectId){
      articleList(current,size,subjectId).then(res =>{
        this.articleList = res.data.data.records;
        console.log(this.articleList);
      })
    },
    handleClick(tab, event) {
      console.log(tab, event);
    }
  }
};
</script>

<style scoped lang="scss">
@import "../../../public/static/css/swiper/swiper.scss";

.grid-row {
  background-color: transparent;
  //   margin-top: 30px;
  height: 477px;
}

.grid-empty {
  margin-top: 2px;
  color: #fff;
}

.image-size {
  width: 619px;
  height: 367px;
  margin-top: 4px;
}

.grid-right {
  color: transparent;
  display: flex;
  // flex-flow: row wrap;
  align-content: flex-end;
}

.grid-left {
  color: transparent;
  text-align: left;
}
</style>
