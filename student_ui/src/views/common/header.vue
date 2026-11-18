<template>
  <div>
    <header class="sticky top-0 z-50 bg-white shadow-soft">
    <div class="max-w-7xl mx-auto px-4 h-16 flex items-center justify-between">
        <!-- 品牌Logo + 名称 -->
        <div class="flex items-center gap-3">
            <div class="w-8 h-8 rounded-lg bg-secondary flex items-center justify-center text-white font-bold">T</div>
            <div>
                <span class="text-primary font-bold text-lg">TradeNest</span>
                <span class="text-gray text-sm ml-2">· 数智营销产业学院教育平台</span>
            </div>
        </div>

        <!-- 导航菜单 -->
        <nav class="flex items-center gap-8">
            <a v-for="(nav,index) in navigation" :href="nav.url" :key="index" :class="{'nav-link':true,'active':nav.url == headNav} ">{{nav.name}}</a>
            <!-- <a href="课程广场.html" class="nav-link">课程广场</a>
            <a href="我的课程.html" class="nav-link">我的课程</a>
            <a href="智能课程工坊.html" class="nav-link">智能课程工坊</a> -->
        </nav>

        <!-- 搜索 + 通知 + 头像 -->
        <div class="flex items-center gap-4">
            <div class="relative w-64 hidden md:block">
                <input type="text" placeholder="请输入关键字.." class="w-full pl-10 pr-4 py-2 rounded-lg border border-gray/20 focus:outline-none focus:ring-2 focus:ring-secondary/20 text-sm">
                <i class="fa-solid fa-search absolute left-3 top-1/2 -translate-y-1/2 text-gray"></i>
            </div>
            <!-- 通知图标 - 由后端提供未读通知数量 -->
            <button id="notification-btn" class="w-8 h-8 flex items-center justify-center text-gray hover:text-secondary transition-colors relative">
                                <i class="fa-solid fa-bell"></i>
                                <!-- 未读通知数量 - 由后端动态更新 -->
                                <span id="notification-count" class="absolute top-0 right-0 w-2 h-2 bg-danger rounded-full"></span>
                            </button>


                            <el-tooltip placement="top" v-if="studentInfo.showName">
                                <div slot="content">
                                  <div  style="display: flex;flex-direction: column;gap: 10px;width: 120px;" >
                                    <!-- <el-button size="small" plain style="width: 120px;left: 0px;" @click="logOut" > 
                                     <a :href="'/uc/myCourse'" target="_self" ><span class="user-center-icon icon-tick">我的课程</span></a>
                                    </el-button> -->
                                    <el-button size="small" plain style="width: 120px;" @click="logOut"> <span class="name text-ellipsis">安全退出</span></el-button>
                                  
                                  </div>
                                </div>
                                  <!-- <div  slot="reference"  class="w-8 h-8 rounded-full bg-secondary/10 flex items-center justify-center text-secondary font-medium" >
                                    <span class="name text-ellipsis">{{studentInfo.showName[0]}}</span>
                                  </div> -->
                                  <el-button> <span class="name text-ellipsis">{{studentInfo.showName[0]}}</span></el-button>
                              </el-tooltip>
        
                              <router-link v-else :to="{path:'/user/login'}" ><a href="javascript:void(0)" id="js-signin-btn">登录</a></router-link>

           
        </div>
    </div>
</header>
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
        searchCourseName:"",
      }
    },
    props:{
      headNav: {
        type: String,
        default: "/"
      },

    },
    methods: {
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
