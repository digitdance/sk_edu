<template>
  <div>
    <div class="course-info-menu" style="    width: 100%;display: flex;flex-direction: row;justify-content: center;">
     

      <div class="w1">
        <ul class="course-menu1" style="z-index: 2;display: flex;width: 100%;">
          <li><a @click="showFlag = 0" class="moco-change-big-btn" v-bind:class="{'active':showFlag == 0}" href="javascript:void(0)">课程介绍</a></li>
          <li><a @click="showFlag = 1" class="moco-change-big-btn" v-bind:class="{'active':showFlag == 1}" id="learnOn" href="javascript:void(0)">课程章节</a></li>
          <li><a @click="showFlag = 3" class="moco-change-big-btn" v-bind:class="{'active':showFlag == 3}" id="learnOn" href="javascript:void(0)">课件资料</a></li>
        </ul>
      </div>
    </div>
    <div class="detail-path w" style="padding: 5px 0px;color: #666;" >
        <router-link :to="{path:'/course/courseList?courseType=1'}" class="js-column">所有课程</router-link>
        /
        <router-link :to="'#'"  class="js-column"> {{ courseInfo.courseName }}</router-link>
        <!-- <router-link :to="{path:'/article/list?subjectId='+subject.id}" target="_blank" class="js-column">{{subject.name}}</router-link> -->
      </div>

    <div class="course-info-main clearfix w" style="margin-top: 0px !important;" v-loading="loading">
      <div class="content-wrap clearfix"  >
        <div class="content" v-show="showFlag == 0">
          <!-- 课程简介 -->
          <div style="display: flex;justify-content: center;flex-direction: row;font-size: 16px;font-weight: bold;" >
            {{ courseInfo.courseName }}
          </div>
          <div style="display: flex;justify-content: center;flex-direction: row;">
            <!-- <div style="margin-right: 10px;">创建人:郭老师</div> -->
            <div style="margin-right: 10px;">学习人数:{{ courseInfo.pageBuycount }}</div>
            <div>浏览人数:{{ courseInfo.pageViewcount }}</div>
          </div>
          <div class="course-wrap"  style="min-height: 160px;display: flex;">
            <div style="width: 20%;">
              <img width="200" height="200" alt="" v-lazy="courseInfo.logo">
            </div>
            <div style="width: 80%;" v-html="courseInfo.title">
            </div>
          </div>
          <div class="course-wrap" v-html="courseInfo.context" style="min-height: 160px">
          </div>
          <!-- 课程简介 end -->
        </div>
        <div class="content" v-show="showFlag == 1">
          <!-- 课程章节 -->
          <div class="course-chapters">
            <div class="chapter course-wrap " v-for="courseKpoint in courseInfo.list" :key="courseKpoint.id">
              <!-- 章节标题 -->
              <h3>
                {{courseKpoint.name}}
              </h3>
              <!-- 章节标题 end -->
              <!-- 章节小节 -->
              <ul class="video">
                <li v-for="child in courseKpoint.children" :key="child.id" :class="(courseInfo.sellType == 2 && child.liveEndTime <= dateFormat('YYYY-mm-dd HH:MM:SS',new Date()))?'unclick':''">
                  <!-- 直播类型  start -->
                  <span v-if="courseInfo.sellType == 2" >
                    <!-- 百家云直播 start -->
                    <span v-if="child.videoType=='baijiayunlive'">
                      <!--直播结束时间之前-->
                      <a v-if="courseInfo.sellType == 2 && child.liveEndTime > dateFormat('YYYY-mm-dd HH:MM:SS',new Date())" @click="gotoPlay(child)" href="javascript:void(0)" class="J-media-item">
                        <i class="iconfont icon-zhibo type fl"></i>
                        <span class="fl">
                          <!-- 【直播】 -->
                        </span>
                        <span class="course-name">{{child.name}}</span>
                        <span class="fl">
                          <span style="color: #999;" v-if="child.liveEndTime <= dateFormat('YYYY-mm-dd HH:MM:SS',new Date())">（{{child.liveBeginTime|dataFormat('MM月dd日 hh:mm')}}-{{child.liveEndTime|dataFormat('hh:mm')}}  已结束）</span>
                          <span style="color: #999;" v-if="child.liveBeginTime > dateFormat('YYYY-mm-dd HH:MM:SS',new Date())">（{{child.liveBeginTime|dataFormat('MM月dd日 hh:mm')}}-{{child.liveEndTime|dataFormat('hh:mm')}} 未开始） </span>
                          <span style="color: #999;" v-if="child.liveBeginTime < dateFormat('YYYY-mm-dd HH:MM:SS',new Date())&&child.liveEndTime > dateFormat('YYYY-mm-dd HH:MM:SS',new Date())">（{{child.liveBeginTime|dataFormat('MM月dd日 hh:mm')}}-{{child.liveEndTime|dataFormat('hh:mm')}}）<span class="flag-orange js-expr-on">正在直播</span></span>
                        </span>
                        <i class="icon-tick-revert done" v-if="isLogin && child.complete==2"></i>
                        <i class="icon-half ing" v-if="isLogin && child.complete==1"></i>
                        <i class="icon-nolearn start" v-if="isLogin && child.complete==0"></i>
                      </a>
                      <!--直播结束时间之后，直播已经结束-->
                      <a v-if="courseInfo.sellType == 2 && child.liveEndTime <= dateFormat('YYYY-mm-dd HH:MM:SS',new Date())" @click="gotoPlay(child)" href="javascript:void(0)" class="J-media-item">
                        <i v-if="child.videoType=='baijiayunlive'&& child.openReplay==1" class="imv2-play_circle type fl"></i>
                        <i v-if="child.videoType=='baijiayunlive'&& child.openReplay==2" class="iconfont icon-zhibo type fl"></i>
                        <span v-if="child.videoType=='baijiayunlive'&& child.openReplay==1" class="fl">
                          【回放】
                        </span>
                        <span v-if="child.videoType=='baijiayunlive'&& child.openReplay==2" class="fl">
                          【直播】
                        </span>
                        <span v-if="child.videoType=='baijiayunlive'" class="course-name">{{child.name}}</span>

                        <span v-if="child.videoType=='baijiayunlive'&& child.openReplay==2" class="fl">
                          <span style="color: #999;" v-if="child.liveEndTime <= dateFormat('YYYY-mm-dd HH:MM:SS',new Date())">（{{child.liveBeginTime|dataFormat('MM月dd日 hh:mm')}}-{{child.liveEndTime|dataFormat('hh:mm')}}  已结束）</span>
                        </span>
                        <i class="icon-tick-revert done" v-if="isLogin && child.complete==2"></i>
                        <i class="icon-half ing" v-if="isLogin && child.complete==1"></i>
                        <i class="icon-nolearn start" v-if="isLogin && child.complete==0"></i>
                      </a>
                    </span>
                    <!-- 百家 end -->
                    <!-- 阿里云直播 start -->
                    <span v-if="child.videoType=='aliyunlive'">
                      <a @click="gotoPlay(child)" href="javascript:void(0)" class="J-media-item">
                        <i v-if="child.videoType=='aliyunlive'" class="iconfont icon-zhibo type fl"></i>
                        <span class="fl">
                          【直播】
                        </span>
                        <span class="course-name">{{child.name}}</span>
                        <span v-if="child.videoType=='aliyunlive'" class="fl">
                          <span style="color: #999;" v-if="child.liveEndTime <= dateFormat('YYYY-mm-dd HH:MM:SS',new Date())">（{{child.liveBeginTime|dataFormat('MM月dd日 hh:mm')}}-{{child.liveEndTime|dataFormat('hh:mm')}}  已结束）</span>
                          <span style="color: #999;" v-if="child.liveBeginTime > dateFormat('YYYY-mm-dd HH:MM:SS',new Date())">（{{child.liveBeginTime|dataFormat('MM月dd日 hh:mm')}}-{{child.liveEndTime|dataFormat('hh:mm')}} 未开始） </span>
                          <span style="color: #999;" v-if="child.liveBeginTime < dateFormat('YYYY-mm-dd HH:MM:SS',new Date())&&child.liveEndTime > dateFormat('YYYY-mm-dd HH:MM:SS',new Date())">（{{child.liveBeginTime|dataFormat('MM月dd日 hh:mm')}}-{{child.liveEndTime|dataFormat('hh:mm')}}）<span class="flag-orange js-expr-on">正在直播</span></span>
                        </span>
                        <i class="icon-tick-revert done" v-if="isLogin && child.complete==2"></i>
                        <i class="icon-half ing" v-if="isLogin && child.complete==1"></i>
                        <i class="icon-nolearn start" v-if="isLogin && child.complete==0"></i>
                      </a>
                    </span>
                    <!-- 阿里云直播 end -->
                    <!-- 阿里云直播回放 start -->
                    <span v-if="child.videoType=='aliyunReplay'">
                      <a @click="gotoPlay(child)" href="javascript:void(0)" class="J-media-item">
                        <i v-if="child.videoType=='aliyunReplay'" class="imv2-play_circle type fl"></i>
                        <span v-if="child.videoType=='aliyunReplay'" class="fl">
                          【回放】
                        </span>
                        <span class="course-name">{{child.name}}</span>
                        <i class="icon-tick-revert done" v-if="isLogin && child.complete==2"></i>
                        <i class="icon-half ing" v-if="isLogin && child.complete==1"></i>
                        <i class="icon-nolearn start" v-if="isLogin && child.complete==0"></i>
                      </a>
                    </span>
                    <!-- 阿里云直播回放 end -->
                    <!-- 其他类型直播 start -->
                    <span v-if="child.videoType=='otherlive'">
                      <a @click="gotoPlay(child)" href="javascript:void(0)" class="J-media-item">
                        <i class="iconfont icon-zhibo type fl"></i>
                        <span>
                          【直播】
                        </span>
                        <span class="course-name">{{child.name}}</span>
                        <span class="fl">
                          <span style="color: #999;" v-if="child.liveEndTime <= dateFormat('YYYY-mm-dd HH:MM:SS',new Date())">（{{child.liveBeginTime|dataFormat('MM月dd日 hh:mm')}}-{{child.liveEndTime|dataFormat('hh:mm')}}  已结束）</span>
                          <span style="color: #999;" v-if="child.liveBeginTime > dateFormat('YYYY-mm-dd HH:MM:SS',new Date())">（{{child.liveBeginTime|dataFormat('MM月dd日 hh:mm')}}-{{child.liveEndTime|dataFormat('hh:mm')}} 未开始） </span>
                          <span style="color: #999;" v-if="child.liveBeginTime < dateFormat('YYYY-mm-dd HH:MM:SS',new Date())&&child.liveEndTime > dateFormat('YYYY-mm-dd HH:MM:SS',new Date())">（{{child.liveBeginTime|dataFormat('MM月dd日 hh:mm')}}-{{child.liveEndTime|dataFormat('hh:mm')}}）<span class="flag-orange js-expr-on">正在直播</span></span>
                        </span>
                        <i class="icon-tick-revert done" v-if="isLogin && child.complete==2"></i>
                        <i class="icon-half ing" v-if="isLogin && child.complete==1"></i>
                        <i class="icon-nolearn start" v-if="isLogin && child.complete==0"></i>
                      </a>
                    </span>
                    <!-- 其他类型直播 end -->
                    <!-- iframe类型直播 start -->
                    <span v-if="child.videoType=='iframelive'">
                      <a @click="gotoPlay(child)" href="javascript:void(0)" class="J-media-item">
                        <i class="iconfont icon-zhibo type fl"></i>
                        <span class="fl">
                          【直播】
                        </span>
                        <span class="course-name">{{child.name}}</span>
                        <span class="fl">
                          <span style="color: #999;" v-if="child.liveEndTime <= dateFormat('YYYY-mm-dd HH:MM:SS',new Date())">（{{child.liveBeginTime|dataFormat('MM月dd日 hh:mm')}}-{{child.liveEndTime|dataFormat('hh:mm')}}  已结束）</span>
                          <span style="color: #999;" v-if="child.liveBeginTime > dateFormat('YYYY-mm-dd HH:MM:SS',new Date())">（{{child.liveBeginTime|dataFormat('MM月dd日 hh:mm')}}-{{child.liveEndTime|dataFormat('hh:mm')}} 未开始） </span>
                          <span style="color: #999;" v-if="child.liveBeginTime < dateFormat('YYYY-mm-dd HH:MM:SS',new Date())&&child.liveEndTime > dateFormat('YYYY-mm-dd HH:MM:SS',new Date())">（{{child.liveBeginTime|dataFormat('MM月dd日 hh:mm')}}-{{child.liveEndTime|dataFormat('hh:mm')}}）<span class="flag-orange js-expr-on">正在直播</span></span>
                        </span>
                        <i class="icon-tick-revert done" v-if="isLogin && child.complete==2"></i>
                        <i class="icon-half ing" v-if="isLogin && child.complete==1"></i>
                        <i class="icon-nolearn start" v-if="isLogin && child.complete==0"></i>
                      </a>
                    </span>
                    <!-- iframe类型直播 end -->
                  </span>
                  <!-- 直播类型  end -->
                  <!-- 点播类型  start -->
                  <span v-if="courseInfo.sellType == 1">
                    <a @click="gotoPlay(child)"  href="javascript:void(0)" class="J-media-item ">
                      <i v-if="courseInfo.videoUrl" class="imv2-play_circle type fl"></i>
                      <span class="fl">
                        <!-- 【点播】 -->
                      </span>
                      <span class="course-name">
                        {{child.name}}
                      </span>
                      <span class="fl">
                        (学习时长:{{child.videoTimeFmt}})
                      </span>
                      <!--<button class="r moco-btn moco-btn-red preview-btn">开始学习</button>-->
                      <i class="icon-tick-revert done" v-if="isLogin && child.complete==2"></i>
                      <i class="icon-half ing" v-if="isLogin && child.complete==1"></i>
                      <i class="icon-nolearn start" v-if="isLogin && child.complete==0"></i>
                    </a>
                  </span>
                  <!-- 点播类型  end -->
                </li>
              </ul>
              <!-- 章节小节 end -->
            </div>
          </div>
          <!-- 课程章节 end -->
        </div><!--content end-->

        <div  v-if="showFlag == 3">
          
          <kejianContent v-if="showFlag == 3" :isHave="isHave" :courseInfo="courseInfo"></kejianContent>
        </div>
        <!-- 评论列表  start -->
        <div class="content" v-show="showFlag == 2">
          

          <div class="evaluation-list">
            <div class=" evaluate" v-for="comment in commentList" :key="comment.id">
              <div class="evaluation-con">
                <a href="javascript:void(0)" class="img-box">
                  <img :src="comment.headImg" onerror="this.src='/static/img/user-avatar.png'" :alt="comment.showName">
                </a>
                <div class="content-box">
                  <div class="user-info clearfix">
                    <a href="javascript:void(0)" class="username">{{comment.showName}}</a>
                    <!-- <div class="star-box">
                      <i class="icon-star2 " v-bind:class="{'active':comment.commentScore >= 2}"></i>
                      <i class="icon-star2 " v-bind:class="{'active':comment.commentScore >= 4}"></i>
                      <i class="icon-star2 " v-bind:class="{'active':comment.commentScore >= 6}"></i>
                      <i class="icon-star2 " v-bind:class="{'active':comment.commentScore >= 8}"></i>
                      <i class="icon-star2 " v-bind:class="{'active':comment.commentScore >= 10}"></i>
                      <span class="ml10 fs12" style="color:#ffb200">{{comment.commentScore}}分</span>
                    </div>-->
                  </div> 
                  <!--user-info end-->
                  <p class="content1" v-html="comment.content"></p>
                  <div class="info clearfix">

                    <span class="time r">时间：{{comment.addtime}}</span>
                  </div>
                </div><!--content end-->
              </div><!--evaluation-con end-->
              <div class="do-reply" v-if="false">
                <div class="textarea-box">
                  <textarea name="teacher-reply" class="teacher-do-reply" placeholder="写下你对评价的回复..."></textarea>
                  <span class="num-tip"><em>0</em>/300</span>
                </div><!--textarea-box end-->
                <div class="ctrl clearfix">
                  <p class="errtip"></p>
                  <div class="btn-box">
                    <div class="moco-btn cancel">取消</div>
                    <div class="moco-btn moco-btn-red sure" data-comment-id="144753">回复</div>
                  </div>
                </div><!--ctrl end-->
              </div><!--do-reply end-->
            </div><!--evaluation end-->

            <page-component :page="page" @goto="goto" v-show="commentList.length >0" ></page-component>
            <!-- <div class="nodata" v-if="commentList.length == 0">
              <p><i class="imv2-error_c"></i></p>
              <p>还没有内容哦！</p>
            </div> -->
          </div>

          <div class="evaluate clearfix" v-show="commentFlag">
            <!-- <div class="evaluate-item clearfix">
              <div class="evaluate-title">
                评分：
              </div>
              <ul>
                <li>
                  <h3></h3>
                  <div id="cType1" data-field="内容实用" class="star-box js-star">
                    <i @mouseover="score = 2" class="icon-star2 " v-bind:class="{'active':score >= 2}"></i>
                    <i @mouseover="score = 4" class="icon-star2" v-bind:class="{'active':score >= 4}"></i>
                    <i @mouseover="score = 6" class="icon-star2 " v-bind:class="{'active':score >= 6}"></i>
                    <i @mouseover="score = 8" class="icon-star2 " v-bind:class="{'active':score >= 8}"></i>
                    <i @mouseover="score = 10" class="icon-star2 " v-bind:class="{'active':score == 10}"></i>
                    <span class="undis ml15" v-bind:class="{'disinline':score == 2}"><span style="color: #ffb200;padding-right: 5px">1星</span>差评！课程太差了，我要吐槽！</span>
                    <span class="undis ml15" v-bind:class="{'disinline':score == 4}"><span style="color: #ffb200;padding-right: 5px">2星</span>勉强中评！课程不满意</span>
                    <span class="undis ml15" v-bind:class="{'disinline':score == 6}"><span style="color: #ffb200;padding-right: 5px">3星</span>中评！课程一般</span>
                    <span class="undis ml15" v-bind:class="{'disinline':score == 8}"><span style="color: #ffb200;padding-right: 5px">4星</span>好评！课程还不错，继续保持！</span>
                    <span class="undis ml15" v-bind:class="{'disinline':score == 10}"><span style="color: #ffb200;padding-right: 5px">5星</span>好评！课程非常棒，点赞！</span>
                  </div>
                </li>
              </ul>
            </div> -->
            <div class="evaluate-item clearfix">
              <a href="javascript:void(0)" class="user-head l">
                <img :src="studentInfo.headImg" onerror="this.src='/static/img/user-avatar.png'" :alt="studentInfo.showName" class="js-textarea-head">
              </a>
              <div class="textarea-box">
                <textarea @keyup="computeWordCount()" maxlength="300" v-model="commentContent"  name="evaluation" id="evaluation" placeholder="输入内容..."></textarea>
                <span class="num-tip"><em>{{wordCount}}</em>/300</span>
              </div>
            </div><!--textarea-box end-->
            <div class="ctrl-box clearfix">
              <div @click="addComment()" class="moco-btn moco-btn-red moco-btn-lg release-evaluation r">提交</div>
              <div class="errtip l"></div>
            </div><!--ctrl-box end-->
          </div><!--evaluate end-->
          <div class="evaluate-succ" v-show="tipFlag">
            <i class="icon-tick2"></i>提交成功，感谢您对本课程的支持！
          </div><!--evaluate-succ end-->

        </div>
        <!-- 评论列表  end -->
        <div class="aside r" v-if="!isHave" style="margin-top: 0px !important;">
        <!-- <div class="aside r" v-if="!isHave" style="margin-top: 0px !important;"> -->
          <div class="course-wrap course-aside-info js-usercard-box">
            <div class="course-card-price">
              <!-- <div  class="price-box tac">
                <span class="price fs20" v-if="courseInfo.currentPrice != '0.00'">价格</span>
                <span class="price red fs20" v-if="courseInfo.currentPrice == '0.00'" >免费</span>
                <span class="price red fs20" v-if="courseInfo.currentPrice != '0.00'" >￥{{courseInfo.currentPrice}}</span>
                <span class="cost-price">￥{{courseInfo.sourcePrice}}</span>
              </div> -->
            </div>
            <div class="learn-btn mt10">
              <a @click="toPay(obj)" href="javascript:void(0)" class="moco-btn moco-btn-red moco-btn-lg J-learn-course" >加入学习</a> 
              <!-- <a @click="toPay(courseInfo)" href="javascript:void(0)" class="moco-btn moco-btn-red moco-btn-lg J-learn-course" v-if="!isHave">加入学习</a> -->
            </div>
            <!-- <div class="course-info-tip" v-html="courseInfo.title">
            </div> -->
          </div>

          <div id="coursechapterlistRight" style="display: block;margin-top: 20px" v-for="img in courseInfoImgList" :key="img.id">
            <div>
              <a v-if="img.linkAddress!= ''" :href="img.linkAddress" target="_blank"><img :src="img.imageUrl"></a>
              <a v-if="img.linkAddress== ''" href="javascript:void(0);" ><img :src="img.imageUrl"></a>
            </div>
            <div></div>
          </div>
          <div class="js-related-box">
            <div class="mb40 recom-course-list-box"><h4>相关课程</h4>
              <ul class="js-recom-course moco-aside-course clearfix">
                <li v-for="course in courseInfoList" :key="course.id">
                  <router-link :to="{path:'/course/courseInfo/'+course.id}" class="aside-course-name"
                               target="_blank">
                    <div class="aside-course-img"><img :src="course.logo" onerror="this.src='/static/img/course-default.png'"></div>
                  </router-link>
                  <div class="aside-course-content"><router-link :to="{path:'/course/courseInfo/'+course.id}" class="aside-course-name"
                                                                 target="_blank">{{course.courseName}}</router-link>
                    <p class="aside-course-people">
                      <i class="imv2-set-sns"></i>
                      <span v-if="course.bogusBuycount == 0">{{course.pageBuycount}}</span>
                      <span v-if="course.bogusBuycount != 0">{{course.bogusBuycount+course.pageBuycount}}</span>
                    </p>
                  </div>
                </li>
              </ul>
            </div>
          </div>
        </div>
        
      </div>
      <div class="clear"></div>
    </div>


    <el-dialog
      title="加入学习"
      width="30%"
      :visible.sync="dialogJoinVisible"
    >
   
      <el-form ref="joinForm" :model="joinForm" :rules="loginRules">
           
            <div class="lowin-group">
              <label>授课密码 </label>
              <el-input
                ref="password"
                v-model="joinForm.password"
                class="lowin-input"
                placeholder="授课密码"
                name="password"
                type="text"
                tabindex="1"
                auto-complete="on"
              />
            </div>
            <div  style="margin-top: 20px;">
              <el-button
              :loading="loading"
              type="primary"
              class="lowin-btn login-btn"
              @click.native.prevent="handleJoin"
              >加入</el-button>
            </div>
           
            
      </el-form>
    
    </el-dialog>


  </div>

</template>

<script>
  import { setStore, getStore } from '@/util/store'
  import {getBannerList,course,commentList,checkComment} from "@/api";
  import {checkCourse,checkPassword,createFreeCourseOrder,commentSubmit,createCourseOrder} from "@/api/user";
  import { mapGetters } from "vuex";
  import pageComponent from '@/views/common/pageComponent'
  
  import kejianContent from "./kejian";
  export default {
    name: 'courseInfoContent',

    data() {
      return {
        isHave:true,//是否拥有课程权限
        isToLook:false,
        obj:{},
        dialogJoinVisible:false,
        loginRules: {
          password: [
            { required: true, trigger: "blur"},
          ]
        },
        joinForm:{password:""},
        loading:false,
        courseInfoImgList:getStore({ name: 'courseInfoImgList' }) || [],
        courseInfoList:getStore({ name: 'courseInfoList' }) ||[],
        showFlag:0,
        commentList:[],
        commentCount:0,
        current:1,//评论分页
        size:20,
        page:{},
        score:0,
        commentContent:"",
        wordCount:0,
        commentFlag:false,
        tipFlag:false,
      }
    },
    methods: {
      handleJoin(){
        if(this.joinForm.password.length==0){
          this.dialogJoinVisible = false
          this.$layer.alert(
              "请输入选课密码",
              {
                shade: true,
                title: "提示"
              },
              laeryid => {
                this.$layer.close(laeryid);
              }
            );
            return;
        }else{

          this.joinForm.courseId = this.courseInfo.id;
          checkPassword(this.joinForm).then(res2=>{
            console.log("res2",res2)
            this.dialogJoinVisible = false
            this.isHave = true
            this.$layer.alert(
              "加入学习成功",
              {
                shade: true,
                title: "提示"
              },
              laeryid => {
                this.$layer.close(laeryid);
              }
            );
            //that.showFlag = 2
            // if(res.data.data){
            //   that.commentFlag = true
            // }else{
            //   that.commentFlag = false
            // }
          }).catch(data => { 
            this.dialogJoinVisible = false
            this.$layer.alert(
              data,
              {
                shade: true,
                title: "提示"
              },
              laeryid => {
                this.$layer.close(laeryid);
              }
            );

            console.log("error",data)
           })
        }
      },
      checkComment(){//点击评论按钮
        let that = this
        //如果没有登录是不能评论的
        if(!this.studentInfo){
          that.showFlag = 2
          return;
        }
        if(this.studentInfo){
          if(!this.studentInfo.id){
            that.showFlag = 2
            return;
          }
        }
        //检查是否已评论
        let params = {
          "courseId":this.courseInfo.id,
        }
        checkComment(params).then(res=>{
          that.showFlag = 2
          // if(res.data.data){
          //   that.commentFlag = true
          // }else{
          //   that.commentFlag = false
          // }
        })

        that.commentFlag = true

      },
      goto(i){//评论分页
        this.current = i
        //查询评论
        let params = {
          "current": this.current,
          "size": this.size,
          "otherId":this.courseInfo.id,
          "type":2
        }
        commentList(params).then(res=>{
          this.commentList = res.data.data.records
          this.page = res.data.data
          this.commentCount = res.data.data.total
        })
      },
      computeWordCount(){//评论输入数字回显方法
        this.wordCount = this.commentContent.length
      },
      toPay(courseInfo){//立即购买
        //未登录则跳转到登录页面
       
        //判断课程是否过期
        if(this.courseInfo.losetype == '0'){
          if(Date.parse(this.courseInfo.endTime) < new Date()){
            this.$layer.alert(
              "课程已过期，请学习其他课程",
              {
                shade: true,
                title: "提示"
              },
              laeryid => {
                this.$layer.close(laeryid);
              }
            );
            return;
          }
        }
       // this.dialogJoinVisible = true
        // this.$router.push({ path: '/course/confirmOrder/'+courseInfo.id })
        // let courseId = this.courseInfo.id;
        // let param = {
        //   courseId: courseId,
        //   payType: "",
        //   reqChannel:"web"
        // }
        // createCourseOrder(param).then(res =>{
        //   let order = res.data.data
        //   this.$router.push({ path: '/course/payCenter/'+order.id })
        // })
      },
      joinClazzCourse(){
        
      },
      gotoPlay(obj){
        //判断如果没有登录则前往登录
      
        
        this.$router.push({ path: '/uc/preview/'+obj.id })
      
      },
      openApp(enterClassroomClientURL){
        let that = this
        var userAgent = navigator.userAgent;
        var isOpera = userAgent.indexOf("Opera") > -1;
        if (userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1 && !isOpera || userAgent.indexOf("Firefox") > -1) {
          // IE和火狐用window.open打开
          // 调起客户端 5秒之后自动关闭调起窗口
          that.$layer.confirm(
            "请先<a href='https://www.baijiayun.com/classroomdown/' style='color: #00a0dc' target='_blank'>下载</a>直播客户端再进入直播",
            {
              title: "进入直播",
              btn: ["进入直播", "取消"],
            },
            layerid => {
              var client = window.open(enterClassroomClientURL);
              setTimeout(function () {
                if (client) {
                  client.close(); //关闭新打开的浏览器窗口，避免留下一个空白窗口
                }
              }, 5000);
              that.$layer.close(layerid);
            },
            layerid => {
              that.$layer.close(layerid);
            }
          )
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
            layerid => {
              a.click();
              that.$layer.close(layerid);
            },
            layerid => {
              that.$layer.close(layerid);
            }
          )
        }

      },
      dateFormat(fmt, date){
        let ret;
        const opt = {
          "Y+": date.getFullYear().toString(),        // 年
          "m+": (date.getMonth() + 1).toString(),     // 月
          "d+": date.getDate().toString(),            // 日
          "H+": date.getHours().toString(),           // 时
          "M+": date.getMinutes().toString(),         // 分
          "S+": date.getSeconds().toString()          // 秒
          // 有其他格式化字符需求可以继续添加，必须转化成字符串
        };
        for (let k in opt) {
          ret = new RegExp("(" + k + ")").exec(fmt);
          if (ret) {
            fmt = fmt.replace(ret[1], (ret[1].length == 1) ? (opt[k]) : (opt[k].padStart(ret[1].length, "0")))
          }
        }
        return fmt;
      },
      init(){
        let that = this
      },
    },
    props:{
      courseInfo: Object,
    },
    watch: {
      courseInfo: {
        deep: true,
        handler(newVal){
         
          //查询广告位
          if(this.courseInfoImgList.length == 0){
            getBannerList("2").then(res => {
              this.courseInfoImgList = res.data.data;
              setStore({ name: 'courseInfoImgList', content: res.data.data,type: 'session' })
            });
          }
          //查询相关课程
          if(this.courseInfoList.length == 0){
            course(1,5,"",1,newVal.subject.id,null).then(res =>{
              this.courseInfoList = res.data.data.records;
              setStore({ name: 'courseInfoList', content: res.data.data.records,type: 'session' })
             
            })
          }
          //查询评论
          let params = {
            "current": this.current,
            "size": this.size,
            "otherId":newVal.id,
            "type":2
          }
          commentList(params).then(res=>{
            this.commentList = res.data.data.records
            this.page = res.data.data
            this.commentCount = res.data.data.total
          })
          let klist = newVal.list
          if(klist.length >0){
            let child = klist[0].children
            if(child!=undefined&&child!=null){
              if(child.length > 0){
                this.obj = child[0]
              }
            }else {
              this.isToLook=true;
            }
          }
          // if(newVal.currentPrice == "0.00"){
          //   this.isHave = true;
          //   let param = {
          //     courseId:newVal.id,
          //   }
          //   if(this.isLogin){
          //     checkCourse(param).then(res =>{
          //       if(res.data.data){
          //         //this.showFlag = 1
          //       }
          //     })
          //   }

          //   return
          // }
          //判断用户是否拥有此课程  如果没有登录则不验证
         
        }
      }
    },
    mounted:function() {
      this.init()
    },
    computed: {
      ...mapGetters(["studentInfo","isLogin"]),
    },
    components: {
      pageComponent,kejianContent
    },
  }
</script>

<style scoped >
  .flag-orange{
    color: #e85308;
    border: 1px solid;
    padding: 1px;
    line-height: 1;
    font-size: 12px;
    border-radius: 2px;
    margin-left: 4px;
    display: inline-block;
    vertical-align: 0;
  }
  .js-column{
    color:#afadad!important;
    
  }
  
</style>
