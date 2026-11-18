<template>
  <div>
    <common-header :headNav="headNav"></common-header>
    <common-breadcrumb ></common-breadcrumb>
    <div class="main">
      <router-view/>
    </div>
    <common-footer></common-footer>
  </div>
</template>

<script>
import commonHeader from '@/views/common/header'
import commonFooter from '@/views/common/footer'
import commonBreadcrumb from '@/views/common/breadcrumb'

export default {
  name: 'articlei',
  data () {
    return {
      subjectIdLevel1:"",
      subjectIdLevel2:"",
      type:"",
      headNav:"/article/list"
    }
  },components:{
    commonHeader,commonFooter,commonBreadcrumb
  },
  methods: {
  }
}
</script>

<style scoped>

</style>
