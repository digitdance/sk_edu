<template>
  <div>
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item v-for ='(item, index) in navPath' :to="item.path" :key='index'>{{item.name}}</el-breadcrumb-item>
    </el-breadcrumb>
  </div>
</template>

<script>
  import { mapGetters } from "vuex";
  import { validatenull } from '@/util/validate';
  import {getToken} from '@/util/auth'

  export default {
    name: 'pageHeader',
    data () {
      return {
        navPath:"",
        searchCourseName:"",
      }
    },
    props:{

    },
    created() {
    	this.getRouterBran(this.$route.matched.pop())
    },
    watch:{
      $router () {
          this.getRouterBran(this.$route.matched.pop())
      }
    },
    methods: {
      getRouterBran () {
        //$route.matched属性 包含当前路由的所有 嵌套路径片段 的路由记录
        // 当 URL 为 /foo/bar，$route.matched 将会是一个包含从上到下的所有嵌套路由对象 (副本)，比如上面路由为二级路由，
        
        // 那么$route.matched的值为[{foo路由对象}，{bar路由对象}]
        //当url地址为/foo
        // 那么$route.matched的值为[{foo路由对象}]
        
        //当url地址为/home
        // 那么$route.matched的值为[{home路由对象}]
        
        let matche = this.$route.matched()
        //所以 我这里以 /foo/bar为列
        // 得到嵌套的路由对象记录，筛选排除一些路由，条件自定
        // 我这里是筛选排除不带name 的路由，自己决定
        matche = matche.filter(item => item.name)

        
        this.navPath = matche
      },
      checkUrl(url){
        let s = url
        if(url.indexOf("?") >0){
          s = url.substr(0,url.indexOf("?"));
        }
        return s;
      },
      search(){
        this.$router.push({ path: '/course/courseList',query: {courseType:"0",searchCourseName:this.searchCourseName} })
      },
      init(){
        let courseName = this.$route.query.searchCourseName;
        this.searchCourseName = courseName
        //判断如果website 为空则更新数据
        if (validatenull(this.website)){
          this.$store.dispatch("getWebsiteConfig")
        }
        //判断如果navigation 为空则更新数据
        if (validatenull(this.websiteNavigate)){
          this.$store.dispatch("getWebsiteNavigate")
        }
      },
      logOut(){
        this.$store.dispatch("LogOut").then(res=>{
          this.$router.push({ name: 'index' }).catch(data => {  });
        })
      }
    },
    mounted:function() {
      this.init();
    },
    computed: {
      ...mapGetters(["website","navigation","userInfo","studentInfo","isLogin","letterCount"]),
    }
  }
</script>

<style scoped>
  .header{
    width:100%;
    height:72px;
    border-bottom: 1px solid #ebeced;
    box-shadow: 0 1px 8px 0 hsla(0,0%,71.8%,.5);
    position: relative;
    z-index: 9;
    height: 70px;
  }

</style>
