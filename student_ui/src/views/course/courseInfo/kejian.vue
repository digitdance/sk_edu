<template>
  <div class="container1" style="display: flex;flex-direction: row;">
    <div class="left111" style="flex: 1;">
      <el-menu  :default-openeds="defaultOpeneds" :v-model="activeMenu" :default-active="activeKpointId" class="el-menu-vertical-demo" style="width: 100%;" >
        <el-submenu :index="courseKpoint.id" :key="courseKpoint.id" v-for="(courseKpoint, index) in courseInfo.list">
          <template slot="title">
            <span>{{ courseKpoint.name }}</span>
          </template>
          <el-menu-item-group>
            <el-menu-item  class="elmenu" style="overflow: visible;width: 280px;" @click="handleChange(child)" :key="subIndex" :index="courseKpoint.id+'-'+child.id" :title="child.name"
              :name="child.id" v-for="(child, subIndex) in courseKpoint.children">
              
              <p style="white-space: normal;line-height: 20px;">
                <!-- <i v-if="child.documentUrl" class="el-icon-document"></i> -->
                {{ child.name }}
              </p>
                 <div style="width: 100%;display: flex;justify-content: flex-end; height: 20px;align-items: flex-end;">
                  <!-- <el-tooltip content="课件" placement="top">
                    <i  :class="{'el-icon-search':true}" style="font-size: 10px;margin-right: 5px;">课件</i>
                  </el-tooltip>
                  <el-tooltip content="资料" placement="top">
                    <i :class="{'el-icon-paperclip':true}" style="font-size: 10px;margin-right: 5px;">资料</i>
                  </el-tooltip> -->
                  <el-tooltip content="去测验" placement="top">
                    <i :class="{'el-icon-magic-stick':true}" @click="toExam(child,'4')" style="font-size: 10px;margin-right: 5px;">测验</i>
                  </el-tooltip>
                  <!-- <el-tooltip content="去实训" placement="top">
                    <i :class="{'el-icon-s-promotion':true}" @click="toExam(child,'3')" style="font-size: 10px;">实训</i>
                  </el-tooltip> -->
                </div> 
            </el-menu-item>
          </el-menu-item-group>
        </el-submenu>
        <el-submenu>
          <template slot="title">
            <span>综合考试</span>
          </template>
          <el-menu-item-group>
            <el-menu-item  class="elmenu" style="overflow: visible;width: 280px;"  :key="subIndex" :index="exam.id" :title="exam.examName"
              :name="exam.id" v-for="(exam, subIndex) in others">
              
              <p style="white-space: normal;line-height: 20px;">
                {{ exam.examName }}
              </p>
                 <div style="width: 100%;display: flex;justify-content: flex-end; height: 20px;align-items: flex-end;">
                  <el-tooltip content="去考试" placement="top">
                    <i :class="{'el-icon-magic-stick':true}" @click="directToExam(exam,'4')" style="font-size: 10px;margin-right: 5px;">去考试</i>
                  </el-tooltip>
                </div> 
            </el-menu-item>
          </el-menu-item-group>
        </el-submenu>
      </el-menu>
    </div>
    <div class="right" style="flex: 2;" >
      <div>
        <div class="course_title">{{ selectKpoint.name }}</div>
      </div>
     <div style="display: flex;flex-direction: column;">
        <div>
          <el-tabs :tab-position="'top'" v-model="anctiveRight" style="height: 80px;">
            <el-tab-pane label="简介" name="about" v-if="selectKpoint.content"></el-tab-pane>
            <el-tab-pane label="微课" name="weike" v-if="selectKpoint.videoUrl"></el-tab-pane>
            <el-tab-pane label="课件" name="kejian" v-if="selectKpoint.documentUrl"></el-tab-pane>
            <el-tab-pane label="资料" name="material" v-if="materialList.length>0"></el-tab-pane>
            <!-- <el-tab-pane label="测验"></el-tab-pane>-->
            <el-tab-pane label="实训" name="shicao" v-if="shicaoList.length>0"></el-tab-pane> 
          </el-tabs>
        </div>
      <div>
          <div v-if="anctiveRight=='about'">
             
              <div class="course_content" v-html="selectKpoint.content"></div>
          </div>
          <div v-if="anctiveRight=='weike'">
              <local-player-content  class="course_content" v-if="selectKpoint.videoUrl" :courseKpoint="selectKpoint" :taocanId="taocanId"
              :times="times"></local-player-content>

          </div>
          <div v-if="anctiveRight=='kejian'">
            <div style="margin-top:20px" class="course_content" v-if="selectKpoint.documentUrl">
                <iframe allow="fullscreen" frameborder="0" style="width: 100%; min-height: 600px" :src="
              selectKpoint.documentUrl +
              '?ci-process=doc-preview&dstType=html&copyable=0&copyable=0'
                    "></iframe>
                </div>
                <div class="nodata" v-else>
                  <p><i class="imv2-error_c"></i></p>
                  <p>暂无课件</p>
            </div>  
          </div>
          <div v-if="anctiveRight=='material'" class="course_content" style="min-height: 600px">
            <div class="nodata" v-if="materialList.length == 0">
              <p><i class="imv2-error_c"></i></p>
              <p>暂无资料</p>
            </div>
            <div v-for="(material,index) in materialList" :key="index" style="display: flex;flex-wrap: wrap;">
             <div>
              <a :href="material.fileUrl" :download="material.fileName" target="_blank" style="" >
                {{ index+1 }}、  <em class="imv2-folder type " style="color: #969696;"></em>{{material.name}} <em class="imv2-arrow3_d type " style="color: #9f9f9f;font-size: 12px;">下载</em>
              </a>
             </div>
              <div>
               
              </div>
            </div>
          </div> 
          <div  v-if="anctiveRight=='shicao'"  class="course_content" style="min-height: 600px">
            <div class="nodata" v-if="shicaoList.length == 0">
              <p><i class="imv2-error_c"></i></p>
              <p>暂无实训任务</p>
            </div>
            <div v-for="(exam,index) in shicaoList" :key="index" class="rt_topic_con" style="    background-color: #f9f9f9;    padding: 10px;    border-radius: 10px;">
              <div class="float_left num_topic d_jump">{{ index+1 }}</div>
              <div style="    color: #333333;"  class="cm_t_title"   v-html="exam.qstContent"  >
                
              </div>
              <button style="color: #409EFF;padding: 5px;" class="go-buyCourse" @click="toExam(exam,3)"><i :class="{'el-icon-s-promotion':true}" @click="toExam(child,'3')" style="font-size: 10px;"></i>去实训</button>

              <div style="margin-top: 20px;">
                附件
                <div v-for="(material,examIndex) in exam.material" :key="index">
                  <a :href="material.url" :download="material.fileName" target="_blank" style="" >
                     <em class="imv2-folder type " style="color: #9f9f9f;font-size: 12px;"></em>{{material.name}}<em class="imv2-arrow3_d type " style="color: #9f9f9f;font-size: 12px;">下载</em>
              </a>
                </div>
              </div>
            </div>
          </div>
          <div  v-if="anctiveRight=='4'"  class="course_content" style="min-height: 600px">
            <div class="nodata" v-if="examList.length == 0">
              <p><i class="imv2-error_c"></i></p>
              <p>暂无任务</p>
            </div>
            <div v-for="(exam,index) in examList" :key="index">
              <div style="cursor: pointer;color: #969696;"  @click="toExam(exam)"  >
                  {{exam.name}}
              </div>
            </div>
          </div>
      </div>
     </div>
    </div>
  </div>
</template>

<script>
const localPlayerContent = () =>
  import("@/views/ucenter/player/localPlayerContent2.vue");

import { getStore } from "@/util/store";
import {
  commentList,
  checkComment,
  getMaterials,
  courseKpoint,
  examKOfPoint,
  examOfCourse,
} from "@/api";
import {
  getKpointQuestion
} from "@/api/exam";
import {
  createFreeCourseOrder,
  commentSubmit,
  createCourseOrder,
} from "@/api/user";

import { mapGetters } from "vuex";
import pageComponent from "@/views/common/pageComponent";
import {
  addCoursestudyhistory,
  myCoursePackage
} from "@/api/user";
export default {
  name: "kejian",
  
  data() {
    return {
      activeCont:"",
      anctiveRight:"",
      activeMenu:"",
   //   isHave: false, //是否拥有课程权限
      isToLook: false,
      obj: {},
      defaultOpeneds: ["1"],
      materialList:[],
      examList:[],
      others:[],
      shicaoList:[],
      activeKpointId: "1-1",
      times: 0,
      taocanId: "",
      courseInfoImgList: getStore({ name: "courseInfoImgList" }) || [],
      courseInfoList: getStore({ name: "courseInfoList" }) || [],
      showFlag: 0,
      commentList: [],
      selectKpoint: {},
      commentCount: 0,
      current: 1, //评论分页
      size: 20,
      page: {},
      score: 0,
      commentContent: "",
      wordCount: 0,
      commentFlag: false,
      tipFlag: false,
      videoImg: "",
      playStatus: "",
      muteStatus: "",
      isMute: true,
      isPlay: false,
      width: "900",
      height: "506",
      preload: "auto",
      controls: true,
      autoplay: true,
    };
  },
  methods: {
    directToExam(row){
      console.log("directToExam",row)
      if(!this.isLogin){
          this.$router.push({ path: '/user/login' })
          return
        }
        let routeData = this.$router.resolve({ path: "/exam/examInfo",query:{id:row.exampaperId,courseId:this.courseInfo.id} });
        window.open(routeData.href, '');
      
    },
    toExam(row,type){


      if(!this.isLogin){
          this.$router.push({ path: '/user/login' })
          return
        }
        //测试
        // this.$router.push({ path: '/pigai?direct=1' })
        // return;
        var kid =row.id
        if(type==3){
          kid =row.kpointId
        }
        examKOfPoint({
          kpointId:kid,
          examType:type
        }).then((res) => {
          if(res.data.data){
            var exam1 = res.data.data
            if(exam1.id){
              let routeData = this.$router.resolve({ path: "/exam/examInfo",query:{id:exam1.id,courseId:this.courseInfo.id} });
              window.open(routeData.href, '');
            }else{
              this.$layer.alert(
                "练习/任务不存在",
                {
                  shade: true,
                  title: "提示",
                },
                (laeryid) => {
                  this.$layer.close(laeryid);
                }
              );
            }
           
          }  
        });
    },
    toShicao(){

    },
    changeActiveCont(active){
        this.activeCont = active
    },
    goExam(exam){
        if(!this.isLogin){
          this.$router.push({ path: '/user/login' })
          return
        }
        console.log("goExam",exam)
        let routeData = this.$router.resolve({ path: "/exam/examInfo",query:{id:exam.id,courseId:this.courseInfo.id} });
        window.open(routeData.href, '');
      },
    getZongheExam(){
      var others = []
      examOfCourse({
        courseId:this.courseInfo.id
      }).then(res=>{
         console.log("getZongheExam1",res.data.data.records)
          res.data.data.records.map(item=>{
            if(item.kpointId==-1){
              others.push(item)
            }
          })
          this.others = others
          console.log("getZongheExam2",others)
      })
    },  
    handleChange(e) {
      if (!this.isHave) {
        this.$layer.alert(
          "您还未拥有课程",
          {
            shade: true,
            title: "提示",
          },
          (laeryid) => {
            this.$layer.close(laeryid);
          }
        );
        return;
      }

      this.selectKpoint = e;
      this.shicaoList=[]
      getKpointQuestion({
        kpointId:this.selectKpoint.id
      }).then((res) => {
          console.log("getKpointQuestion==>",res)
          res.data.data.map(item=>{
            if(item.material){
              item.material = JSON.parse(item.material)
            }
          })
          this.shicaoList = res.data.data;




      });
      console.log("handleChange==>", e);
     

      // this.$nextTick(e=>{
      //     var previewer = document.getElementById("previewer");
      //     console.log("documentUrl", this.selectKpoint.documentUrl);
      //     var iframeSrc ="http://139.9.162.79:8012/onlinePreview?url=" +encodeURIComponent(Base64.encode(this.selectKpoint.documentUrl));

      //     console.log("iframeSrc",previewer,iframeSrc)
      //     previewer.height = window.innerHeight;
      //     previewer.setAttribute("src", iframeSrc);
      // })
        
      this.materialList=[]
      if(e.materialIds){
        getMaterials(e.materialIds).then((res) => {
          console.log("materialInfo",res)
            this.materialList = res.data.data;
        });
      }
      
      this.$forceUpdate();
      
      if(this.selectKpoint.context){
        this.anctiveRight = "about"
      }else if(this.selectKpoint.videoUrl){
        this.anctiveRight = "weike"
      }else if(this.selectKpoint.documentUrl){
        this.anctiveRight = "kejian"
      }
     

    },
    checkComment() {
      //点击评论按钮
      let that = this;
      //如果没有登录是不能评论的
      if (!this.studentInfo) {
        that.showFlag = 2;
        return;
      }
      if (this.studentInfo) {
        if (!this.studentInfo.id) {
          that.showFlag = 2;
          return;
        }
      }
      //检查是否已评论
      let params = {
        courseId: this.courseInfo.id,
      };
      checkComment(params).then((res) => {
        that.showFlag = 2;
        if (res.data.data) {
          that.commentFlag = true;
        } else {
          that.commentFlag = false;
        }
      });
    },
    goto(i) {
      //评论分页
      this.current = i;
      //查询评论
      let params = {
        current: this.current,
        size: this.size,
        otherId: this.courseInfo.id,
        type: 2,
      };
      commentList(params).then((res) => {
        this.commentList = res.data.data.records;
        this.page = res.data.data;
        this.commentCount = res.data.data.total;
      });
    },
    computeWordCount() {
      //评论输入数字回显方法
      this.wordCount = this.commentContent.length;
    },
    toPay(courseInfo) {
      //立即购买
      //未登录则跳转到登录页面
      if (!this.isLogin) {
        this.$router.push({ path: "/user/login" });
        return;
      }
      //判断课程是否过期
      if (this.courseInfo.losetype == "0") {
        if (Date.parse(this.courseInfo.endTime) < new Date()) {
          this.$layer.alert(
            "课程已过期，请学习其他课程",
            {
              shade: true,
              title: "提示",
            },
            (laeryid) => {
              this.$layer.close(laeryid);
            }
          );
          return;
        }
      }
      // this.$router.push({ path: '/course/confirmOrder/'+courseInfo.id })
      let courseId = this.courseInfo.id;
      let param = {
        courseId: courseId,
        payType: "",
        reqChannel: "web",
      };
      createCourseOrder(param).then((res) => {
        let order = res.data.data;
        this.$router.push({ path: "/course/payCenter/" + order.id });
      });
    },
    gotoPlay(obj) {
      //判断如果没有登录则前往登录
      if (!this.isLogin) {
        this.$router.push({ path: "/user/login" });
        return;
      }
      if (this.isToLook) {
        this.$layer.alert(
          "当前课程并无可学习章节，请联系管理员！",
          {
            shade: true,
            title: "提示",
          },
          (laeryid) => {
            this.$layer.close(laeryid);
          }
        );
        return;
      }
      if (obj == null) {
        this.$layer.alert(
          "暂无课程",
          {
            shade: true,
            title: "提示",
          },
          (laeryid) => {
            this.$layer.close(laeryid);
          }
        );
        return;
      }
      //判断课程是否过期
      if (this.courseInfo.losetype == "0") {
        if (Date.parse(this.courseInfo.endTime) < new Date()) {
          this.$layer.alert(
            "课程已过期，请学习其他课程",
            {
              shade: true,
              title: "提示",
            },
            (laeryid) => {
              this.$layer.close(laeryid);
            }
          );
          return;
        }
      }
      if (!this.isHave) {
        this.$layer.alert(
          "您还未拥有课程",
          {
            shade: true,
            title: "提示",
          },
          (laeryid) => {
            this.$layer.close(laeryid);
          }
        );
        return;
      }

      //判断日期 如果是直播 则进行时间判断 如果是回放则不进行时间判断
      if (obj.videoType == "baijiayunlive" || obj.videoType == "iframelive") {
        //修改时间提前10分钟进场
        let d = new Date();
        let addDate = d.setMinutes(d.getMinutes() + 5);
        new Date(addDate);
        let ti = this.dateFormat("YYYY-mm-dd HH:MM:SS", new Date(addDate));
        if (obj.liveBeginTime > ti) {
          this.$layer.alert(
            "课程还未开始",
            {
              shade: true,
              title: "提示",
            },
            (laeryid) => {
              this.$layer.close(laeryid);
            }
          );
          return;
        }
        ti = this.dateFormat("YYYY-mm-dd HH:MM:SS", new Date());
        if (obj.liveEndTime < ti) {
          if (obj.openReplay == 2) {
            this.$layer.alert(
              "课程已结束",
              {
                shade: true,
                title: "提示",
              },
              (laeryid) => {
                this.$layer.close(laeryid);
              }
            );
            return;
          }
        }
      }

      //判断日期 如果是直播 则进行时间判断 如果是回放则不进行时间判断
      if (
        obj.videoType == "polyvlive" ||
        obj.videoType == "aliyunlive" ||
        obj.videoType == "otherlive"
      ) {
        //修改时间提前10分钟进场
        let d = new Date();
        let addDate = d.setMinutes(d.getMinutes() + 5);
        new Date(addDate);
        let ti = this.dateFormat("YYYY-mm-dd HH:MM:SS", new Date(addDate));
        if (obj.liveBeginTime > ti) {
          this.$layer.alert(
            "课程还未开始",
            {
              shade: true,
              title: "提示",
            },
            (laeryid) => {
              this.$layer.close(laeryid);
            }
          );
          return;
        }
        ti = this.dateFormat("YYYY-mm-dd HH:MM:SS", new Date());
        if (obj.liveEndTime < ti) {
          this.$layer.alert(
            "课程已结束",
            {
              shade: true,
              title: "提示",
            },
            (laeryid) => {
              this.$layer.close(laeryid);
            }
          );
          return;
        }
      }
      //判断当课程为免费时创建课程订单
      if (this.courseInfo.currentPrice == "0.00") {
        let param = {
          courseId: this.courseInfo.id,
          payType: "",
          reqChannel: "web",
        };
        createFreeCourseOrder(param).then((res) => {
          console.log(res);
        });
      }

      //判断如果为百家云则进行判断  是客户端打开还是web打开
      if (obj.videoType == "baijiayunlive") {
        if (obj.openType == "APP") {
          courseKpoint(obj.id).then((res) => {
            let courseKpoint = res.data.data;
            this.openApp(courseKpoint.params.clienturl);
          });
          return;
        }
      }
      if (obj.videoType == "iframelive") {
        window.open(obj.videoUrl);
        return;
      }
      this.$router.push({ path: "/uc/player/" + obj.id });
      /*if(obj.videoType == "baijiayunlive"){
          getDetail(obj.id,"student").then(res=>{
            window.open(res.data.data.params.url);
          })
        }else if(obj.videoType == "baijiayunReplay"){
          getDetail(obj.id,"student").then(res=>{
            window.open(res.data.data.params.url);
          })
        }else{
          this.$router.push({ path: '/uc/player/'+obj.id })
        }*/
    },
    openApp(enterClassroomClientURL) {
      let that = this;
      var userAgent = navigator.userAgent;
      var isOpera = userAgent.indexOf("Opera") > -1;
      if (
        (userAgent.indexOf("compatible") > -1 &&
          userAgent.indexOf("MSIE") > -1 &&
          !isOpera) ||
        userAgent.indexOf("Firefox") > -1
      ) {
        // IE和火狐用window.open打开
        // 调起客户端 5秒之后自动关闭调起窗口
        that.$layer.confirm(
          "请先<a href='https://www.baijiayun.com/classroomdown/' style='color: #00a0dc' target='_blank'>下载</a>直播客户端再进入直播",
          {
            title: "进入直播",
            btn: ["进入直播", "取消"],
          },
          (layerid) => {
            var client = window.open(enterClassroomClientURL);
            setTimeout(function () {
              if (client) {
                client.close(); //关闭新打开的浏览器窗口，避免留下一个空白窗口
              }
            }, 5000);
            that.$layer.close(layerid);
          },
          (layerid) => {
            that.$layer.close(layerid);
          }
        );
      } else {
        //其它浏览器使用模拟<a>标签`click`事件的形式调起
        var a = document.createElement("a");
        a.setAttribute("href", enterClassroomClientURL);
        document.body.appendChild(a);
        that.$layer.confirm(
          "请先<a href='https://www.baijiayun.com/classroomdown/' style='color: #00a0dc' target='_blank'>下载</a>直播客户端再进入直播",
          {
            title: "进入直播",
            btn: ["进入直播", "取消"],
          },
          (layerid) => {
            a.click();
            that.$layer.close(layerid);
          },
          (layerid) => {
            that.$layer.close(layerid);
          }
        );
      }
    },
    dateFormat(fmt, date) {
      let ret;
      const opt = {
        "Y+": date.getFullYear().toString(), // 年
        "m+": (date.getMonth() + 1).toString(), // 月
        "d+": date.getDate().toString(), // 日
        "H+": date.getHours().toString(), // 时
        "M+": date.getMinutes().toString(), // 分
        "S+": date.getSeconds().toString(), // 秒
        // 有其他格式化字符需求可以继续添加，必须转化成字符串
      };
      for (let k in opt) {
        ret = new RegExp("(" + k + ")").exec(fmt);
        if (ret) {
          fmt = fmt.replace(
            ret[1],
            ret[1].length == 1 ? opt[k] : opt[k].padStart(ret[1].length, "0")
          );
        }
      }
      return fmt;
    },
    getMyCoursePackage(courseId) {
      myCoursePackage({
        courseId: courseId,
      }).then((res) => {
        //this.selectKpoint = newVal.list[0].children[0];
        if (res.data.data.parentCourseId) {
          this.taocanId = res.data.data.parentCourseId;
        }
      });
    },
    addStudyhistory() {
      let that = this;

      let courseType = 1;
      let taocanId = that.taocanId;
      // if (taocanId != undefined && taocanId != "" && taocanId != 0) {
      //   courseType = 2;
      // }
      if (!that.selectKpoint.id) {
        return;
      }
      let param = {
        courseId: that.selectKpoint.courseId,
        kpointId: that.selectKpoint.id,
        courseName: that.selectKpoint.parentName,
        kpointName: that.selectKpoint.name,
        clazzId: this.studentInfo.clazzId,
      //  kpointCourseId: taocanId,
        courseType: courseType,
        complete: 1,
      };
      addCoursestudyhistory(param).then((res) => {
        console.log("创建学习记录完成", res);
      });
    },
    addComment() {
      //添加评论
      if (this.commentContent.length == 0) {
        this.$layer.alert(
          "请填写评论内容",
          {
            shade: true,
            title: "提示",
          },
          (laeryid) => {
            this.$layer.close(laeryid);
          }
        );
        return;
      }
      let that = this;
      let params = {
        parentId: 0,
        content: this.commentContent,
        otherId: this.courseInfo.id,
        praiseCount: 0,
        replyCount: 0,
        type: 2,
        roleType: "user",
        commentScore: this.score,
        addTime: new Date(),
      };
      commentSubmit(params).then((res) => {
        that.commentFlag = false;
        that.tipFlag = true;
        that.commentList = [];
        //清空评论进行查询
        setTimeout(function () {
          that.tipFlag = false;
          //查询评论
          let params = {
            current: that.current,
            size: that.size,
            otherId: that.courseInfo.id,
            type: 2,
          };
          commentList(params).then((res) => {
            that.commentList = res.data.data.records;
            that.page = res.data.data;
            that.commentCount = res.data.data.total;
          });
        }, 3000);
      });
    },
  },
  props: {
    courseInfo: Object,
    isHave: Boolean
  },
  watch: {
    courseInfo: {
      deep: true,
      immediate: true,
      handler(newVal) {
        console.log("courseInfo change")

        this.getZongheExam()

        var that = this
        if (newVal.list) {

          if(newVal.list.length>0){
            if( newVal.list[0].children.length>0){
              
              that.activeKpointId = newVal.list[0].id+'-'+newVal.list[0].children[0].id
              that.selectKpoint = newVal.list[0].children[0]
              that.defaultOpeneds = [newVal.list[0].id]
              console.log("that.activeKpointId",that.activeKpointId)
              console.log("that.selectKpoint",that.selectKpoint)
              that.handleChange( newVal.list[0].children[0])
            }
          }
          
          newVal.list.map(item => {
           
            item.children.map(item2 => {
              if (item2.id == this.activeKpointId) {
                // console.log("router123", this.activeKpointId)
              //  that.selectKpoint = item2
              }
            })
          })
          let klist = newVal.list;
          if (klist.length > 0) {
            let child = klist[0].children;
            if (child != undefined && child != null) {

              if (child.length > 0) {
                this.obj = child[0];
              }
            } else {
              this.isToLook = true;
            }
          }

        }


       
        //查询广告位
        this.getMyCoursePackage(newVal.id);

        if (this.courseInfoImgList.length == 0) {
          // getBannerList("2").then((res) => {
          //   this.courseInfoImgList = res.data.data;
          //   setStore({
          //     name: "courseInfoImgList",
          //     content: res.data.data,
          //     type: "session",
          //   });
          // });
        }
        //查询相关课程

        //查询评论


        if (newVal.currentPrice == "0.00") {
          this.isHave = true;
          let param = {
            courseId: newVal.id,
          };
          if (this.isLogin) {
          }

          return;
        }
        //判断用户是否拥有此课程  如果没有登录则不验证
        if (this.isLogin) {
          let param = {
            courseId: newVal.id,
          };
          // checkCourse(param).then((res) => {
          //   this.isHave = res.data.data;
          //   //如果已经拥有了这个课程则默认展示套餐班级
          //   if (this.isHave) {
          //     this.showFlag = 1;
          //   }
          // });
        }
      },
    },
    selectKpoint: {
      deep: true,
      immediate: true,
      handler(newVal) {
        if (newVal) {
          this.addStudyhistory();
        }
      },
    },
  },
  mounted: function () {


    if (this.$route.query.kpontId) {
      //this.activeKpointId = this.$route.query.kpontId

    }
    //this.init();
  },
  computed: {
    ...mapGetters(["studentInfo", "isLogin"]),
  },
  components: {
    pageComponent,
    localPlayerContent,
  },
};
</script>

<style scoped lang="scss">
.container {
  display: flex;
}

.left111 {
 
  background: #ffffff;
  box-shadow: 0px 2px 6px 0px rgba(0, 0, 0, 0.05);
  border-radius: 10px;
  margin-right: 20px;
  padding: 20px;
  box-sizing: border-box;

  .el-menu {
    border-right: none;
  }
}

.right {
  width: 1000px;
  background: #ffffff;
  box-shadow: 0px 2px 6px 0px rgba(0, 0, 0, 0.05);
  border-radius: 10px;
  padding: 0 50px;
  box-sizing: border-box;
  color: #3d3d3d;

  .course_title {
    font-size: 20px;
    text-align: center;
    margin: 30px 0;
  }

  .course_text {
    padding-top: 30px;
    margin: 50px 0 10px;
    border-top: 1px solid #efefef;
  }

  .course_content {
    width: 900px;
    font-size: 14px;
    line-height: 30px;
    padding-bottom: 20px;
  }
}
.elmenu{
  // overflow: hidden;
}
.el-menu-item{
  /* 类似flex纵向布局 */
    display: -webkit-box; 
    -webkit-box-orient: vertical;
    /* 盒子高度取（2）行，并自带省略号 */
    -webkit-line-clamp:2; 
    // overflow :hidden;
  }
  .cnt-active{
    color: brown!important;
  }
  /deep/ .el-submenu__title{
    font-weight: bold;
  }
</style>
