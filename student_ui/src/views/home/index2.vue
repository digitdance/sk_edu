<template>
  <div>
  
    <div class="article container clearfix">
      <div class="articlecontainer">

        <main-content></main-content>

        <!-- <left @subjectData="clickSubjectId"></left>
        <right ></right> -->
      </div>
    </div>
    <common-footer></common-footer>
  </div>
</template>

<script>
  import left from '@/views/home/left'
  import right from '@/views/home/right'
  import commonHeader from "@/views/common/header";
  import commonFooter from "@/views/common/footer";
  import mainContent from "./mainContent.vue";
  export default {
    name: 'index',
    data () {
      return {
        subjectId:"0"
      }
    },
    components:{
      commonHeader,
      commonFooter,
      mainContent,
      left,
      right
    },
    methods: {
      clickSubjectId(subjectId){
        this.subjectId = subjectId+""
      }
    },

  }
</script>

<style scoped>

body{
  background-color: #f9f9f9;
}
</style>
