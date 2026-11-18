<template>
  <div>
    <common-header></common-header>
    <div class="main">
      <banner></banner>
      <main-context></main-context>
      <saveplatform></saveplatform>
      <news></news>
      <div class="clear"></div>
    </div>
    <common-footer></common-footer>
  </div>
</template>

<script>
import MainContext from "./mainContext.vue";
import commonHeader from "@/views/common/header";
import commonFooter from "@/views/common/footer";
import hotCourse from "./hotCourse.vue";
import hotArticle from "./hotArticle.vue";
import teacherList from "./teacherList.vue";
import banner from "./banner.vue";
import Saveplatform from "./saveplatform.vue";
import News from "./news.vue";
export default {
  name: "Login",
  data() {
    return {
      isReg: false,
      name: "",
      password: "",
      repeat: ""
    };
  },
  components: {
    commonHeader,
    commonFooter,
    banner,
    MainContext,
    Saveplatform,
    News
},
  methods: {}
};
</script>

<style scoped>
.main {
  background: white;
}
</style>
