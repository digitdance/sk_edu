<template>
  <basic-container>
    <el-tabs v-model="activeName" @tab-click="handleClick">
      <el-tab-pane label="电商平台配置" name="store_server">
        <sysConfig configType="store_server" ></sysConfig>
      </el-tab-pane>
      
    </el-tabs>
  </basic-container>
</template>

<script>
  import {mapGetters} from "vuex";
  import sysConfig from "../config/sysConfig";

  export default {
    data() {
      return {
        activeName: 'web',
      };
    },
    computed: {
      ...mapGetters(["permission"])
    },
    methods: {
      handleClick(tab, event) {
        this.activeName = tab.name;
      }
    },
    components:{
      'sysConfig':sysConfig,
    },
  };
</script>

<style>
</style>
