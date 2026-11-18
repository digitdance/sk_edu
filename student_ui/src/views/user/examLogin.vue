<template>
  <div v-wechat-title="examInfoTitle">
    <div class="main">
      <div class="container">
   

        <div class="tac n-ex-time-btn" id="permitExam" style="">

            <el-form ref="form" :model="form" label-width="80px">
  <el-form-item label="活动名称">
    <el-input v-model="form.name"></el-input>
  </el-form-item>
  <el-form-item label="活动区域">
    <el-select v-model="form.region" placeholder="请选择活动区域">
      <el-option label="区域一" value="shanghai"></el-option>
      <el-option label="区域二" value="beijing"></el-option>
    </el-select>
  </el-form-item>
  <el-form-item label="活动时间">
    <el-col :span="11">
      <el-date-picker type="date" placeholder="选择日期" v-model="form.date1" style="width: 100%;"></el-date-picker>
    </el-col>
    <el-col class="line" :span="2">-</el-col>
    <el-col :span="11">
      <el-time-picker placeholder="选择时间" v-model="form.date2" style="width: 100%;"></el-time-picker>
    </el-col>
  </el-form-item>
  <el-form-item label="即时配送">
    <el-switch v-model="form.delivery"></el-switch>
  </el-form-item>
  <el-form-item label="活动性质">
    <el-checkbox-group v-model="form.type">
      <el-checkbox label="美食/餐厅线上活动" name="type"></el-checkbox>
      <el-checkbox label="地推活动" name="type"></el-checkbox>
      <el-checkbox label="线下主题活动" name="type"></el-checkbox>
      <el-checkbox label="单纯品牌曝光" name="type"></el-checkbox>
    </el-checkbox-group>
  </el-form-item>
  <el-form-item label="特殊资源">
    <el-radio-group v-model="form.resource">
      <el-radio label="线上品牌商赞助"></el-radio>
      <el-radio label="线下场地免费"></el-radio>
    </el-radio-group>
  </el-form-item>
  <el-form-item label="活动形式">
    <el-input type="textarea" v-model="form.desc"></el-input>
  </el-form-item>
  <el-form-item>
    <el-button type="primary" @click="onSubmit">立即创建</el-button>
    <el-button>取消</el-button>
  </el-form-item>
</el-form>
          <a
            href="javascript:void(0)"
            @click="go"
            title="立即考试"
            class="f-fM cou-nocou-btn fsize16 "
            onfocus="this.blur()"
          >
            开始考试
          </a>
        </div>
      </div>
    </div>
    <div class="clear"></div>
  </div>
</template>

<script>
import { exampaperRecordById, examById } from "@/api";
import { mapGetters } from "vuex";
import { setStore, getStore, removeStore } from "@/util/store";

export default {
  name: "courseList",
  data() {
    return {
      examInfoTitle: "",
      headNav: "/exam/examIndex",
      exSuspendShow: true,
      examInfo: {}
    };
  },
  components: {
    // commonHeader, commonFooter
  },
  methods: {
  
  },
  mounted: function() {
 
  },
  computed: {
    ...mapGetters(["studentInfo", "isLogin"])
  }
};
</script>

<style scoped>
.n-ex-time-tx {
  width: 120px;
  height: 120px;
  display: block;
  margin: 20px auto 35px;
  overflow: hidden;
  border-radius: 50%;
}
.n-ex-time-tx img {
  display: block;
  width: 100%;
}
.n-ex-time-dl {
  margin: 0 auto;
  width: 30%;
}
.n-ex-time-dl dl {
  padding-left: 90px;
  position: relative;
  min-height: 30px;
  margin-bottom: 20px;
}
.n-ex-time-dl dl dt {
  position: absolute;
  top: 0;
  left: 0;
  text-align: left;
  line-height: 30px;
  font-size: 18px;
}
.n-ex-time-nub {
  font-size: 16px;
  line-height: 30px;
  font-weight: bold;
  margin-top: 40px;
}
.vam {
  vertical-align: middle;
}
.n-ex-time-nub .time {
  font-size: 26px;
  font-weight: bold;
}
.n-ex-time-btn a {
  border: 1px solid transparent;
  display: block;
  width: 150px;
  height: 40px;
  line-height: 40px;
  border-radius: 4px;
  text-align: center;
  margin: 0 auto;
  background: #0091ff;
  border: 1px solid #0091ff;
  box-shadow: 0 2px 15px 0 #b3d4ff;
  border-radius: 20px;
  color: #fff;
  text-decoration: none;
}
.n-ex-time-in-boy {
  padding-top: 20px;
  padding-bottom: 50px;
}
.n-ex-time-btn {
  margin-top: 50px;
}
</style>
