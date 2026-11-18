<template>
  <div>
    <el-row :gutter="10" class="grid-row">
      <span class="grid-p">
        服务平台
      </span>
      <div
        clas="identification-platform"
        @click="skilsIndex"
        style="width: 322px;
    height: 260px;
    border: 2px solid #D9D9D9;
    margin-top: 60px;
    margin-left: 323px;
    float: left;"
      >
        <img src="/static/images/zyrd.jpg" style="width: 322px;height:185px" />
        <p
          style="
    font-size: initial;
    font-weight: 500;
    text-align: center;
    margin-top: 20px;"
        >
          职业技能认定平台
        </p>
      </div>
      <div
        clas="training-platform"
        style="width: 322px;
    height: 260px;
    border: 2px solid #D9D9D9;
    margin-left: 100px;
    margin-top: 60px;
    float: left;"
        @click="trainingIndex"
      >
        <img src="/static/images/px.jpg" style="width: 322px;height:185px" />
        <p
          style="
    font-size: initial;
    font-weight: 500;
    text-align: center;
    margin-top: 20px;"
        >
          培训平台
        </p>
      </div>
    </el-row>
  </div>
</template>

<script>
export default {
  name: "banner",
  data() {
    return {};
  },
  methods: {
    trainingIndex() {
      this.$router.push({ path: "/training/index" });
    },
    skilsIndex() {
      this.$router.push({ path: "/skils/index" });
      //  this.$router.push({ path: "/material"});
    }
  }
};
</script>

<style scoped lang="scss">
@import "../../../public/static/css/swiper/swiper.scss";

.grid-row {
  background-color: transparent;
  margin-top: 70px;
  width: 1920px;
  height: 500px;
}
.grid-p {
  margin-left: 323px;
  font-weight: 600;
  font-size: large;
  position: absolute;
}
.grid-empty {
  margin-top: 2px;
  color: #fff;
}
.identification-platform {
}
</style>
