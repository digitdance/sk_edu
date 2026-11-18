<template>
  <div id="app">
    <router-view />
    <!-- <div class="foott">powered by 贵阳数字跳动科技有限公司</div> -->
  </div>
</template>

<script>
export default {
  name: "app",
  data() {
    return {};
  },
  watch: {},
  created() {
  },
  methods: {},
  computed: {}
};
</script>
<style lang="scss">
#app {
  width: 100%;
  height: 100%;
  overflow: hidden;
}
.el-table .cell{
  overflow: visible!important;
}

.avue-crud table td{
  overflow: hidden;
}
.foott{
    position: relative;
    float: right;
    width: 100%;
    height: 60px;
    line-height: 60px;
    bottom: 10px;
    text-align: center;
    color: #7c766f;
}
</style>
