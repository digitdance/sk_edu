<template>
  <div class="container">
    <div class="upload">

      
          <el-button
           size="mini"
            @click="choiceFile"
            type="primary"
            :disabled="uploadDisabled"
            >上传<i class="el-icon-upload el-icon--right"></i
          ></el-button>
          <input
           size="mini"
            ref="filElem"
            type="file"
            multiple
            v-show="false"
            id="fileUpload"
            @change="fileChange($event)"
          />
          
          <el-progress
            :percentage="authProgress"
            style="line-height: 35px"
            :status="status"
            :stroke-width="15"
          ></el-progress>
          <div style="font-size: 10px;">{{ fileName }}</div>
      
    </div>
  </div>
</template>
<script>
import axios from "axios";
import { mapGetters } from "vuex";
import {
  add
} from "@/api/edu/videosource";
import { getResumeUploadUrl, getUploadUrl } from "@/api/web/baijia";
import { baijiaUploader } from "@/util/baijiaUpload";
import { setStore, getStore } from "@/util/store";
import fileUploader from "@/components/common/fileUploader";
import cos_utils from "@/utils/cos_utils";
var resourceId = ""; //保存资源id
import SparkMD5 from "spark-md5";

export default {
  data() {
    return {
      baijiayunInfos: [], //视频云多文件信息
      baijiauploader: null,
      type: "1",
      timeout: "",
      partSize: "",
      parallel: "",
      retryCount: "",
      retryDuration: "",
      region: "cn-shanghai",
      userId: "",
      file: null,
      uploader_key: new Date().getTime(), //这个用来刷新组件--解决不刷新页面连续上传的缓存上传数据（注：每次上传时，强制这个值进行更改---根据自己的实际情况重新赋值）
      uploadOptions: {
        // headers: { "Content-Type": "multipart/form-data" },
        target: "/api/edu/videosource/chunkUpload", //SpringBoot后台接收文件夹数据的接口
        testChunks: false, //是否分片-不分片
      },
      authProgress: 0,
      uploadDisabled: false,
      resumeDisabled: true,
      pauseDisabled: true,
      uploader: null,
      statusText: "",
      status: "",
      videoId: "",
      options: [
        {
          value: "1",
          label: "阿里云",
        },
        {
          value: "2",
          label: "视频云",
        },
        {
          value: "3",
          label: "本地上传",
        },
      ],
    };
  },
  components: {
    fileUploader,
  },
  computed: {
    ...mapGetters(["website"]),
  },
  mounted: function () {
    const oScript = document.createElement("script");
    oScript.type = "text/javascript";
    oScript.src = "/aliyun-upload-sdk/lib/es6-promise.min.js";
    document.body.appendChild(oScript);
    const oScript2 = document.createElement("script");
    oScript2.type = "text/javascript";
    oScript2.src = "/aliyun-upload-sdk/lib/aliyun-oss-sdk-5.3.1.min.js";
    document.body.appendChild(oScript2);
    const oScript3 = document.createElement("script");
    oScript3.type = "text/javascript";
    oScript3.src = "/aliyun-upload-sdk/aliyun-upload-sdk-1.5.0.min.js";
    document.body.appendChild(oScript3);
    this.init();
  },
  props:{
    documentdata:Function,
    fileName:String,
  },
  methods: {
    init() {
      this.options = getStore({ name: "yunconfig" });
      this.baijiayun();
    },
    //上传视频格式限制
    beforeUploadVideo(file) {
    
      //获取学习时长
      let videoUrl = URL.createObjectURL(file);
      console.log("videoUrl", videoUrl);
      let audioElement = new Audio(videoUrl);
      audioElement.addEventListener("loadedmetadata", () => {
        console.log("3333333333");
        let duration = audioElement.duration; //时长为秒，小数，182.36
        this.addVideo(file, duration);
      });
      return false;
    },
    //上传视频接口
    addVideo(file, duration) {
      var that = this;
      let size = "";
      if (file.size < 0.1 * 1024) {
        //小于0.1KB，则转化成B
        size = file.size.toFixed(2) + "B";
      } else if (file.size < 0.1 * 1024 * 1024) {
        //小于0.1MB，则转化成KB
        size = (file.size / 1024).toFixed(2) + "KB";
      } else {
        size = (file.size / (1024 * 1024)).toFixed(2) + "MB";
      }
      //设置formData
      let formData = new FormData();
      formData.append("file", file);
      //在请求头设置上传进度条
      let config = {
        onUploadProgress: (progressEvent) => {
          var complete =
            ((progressEvent.loaded / progressEvent.total) * 100) | 0;
          this.authProgress = complete;
        },
        headers: {
          "Content-Type": "multipart/form-data",
        },
      };
      this.videoFlag = true;
      //调用后端上传视频的接口
      this.$http
        .post(`/api/edu/videosource/fileUpload`, formData, config)
        .then((res) => {
          this.videoFlag = false;

          if (res.data.code == 200) {
            let videoInfo = res.data.data;

            let row = {
              name: file.name,
              videoSize: size,
              videoStatus: "finish",
              idVarchar: videoInfo.url,
              videoLength: "",
              videoDuration: duration,
              videoType: 1,
              fileType: 1,
              initType: 1,
              status: 1,
            };
            add(row).then(
              () => {
                that.$emit("closeupload");
              },
              (error) => {
                window.console.log(error);
                that.$emit("failupload", "文件信息保存失败");
              }
            );
          } else {
            this.$message.error(res.message || "网络出了小差");
          }
        });
      return false;
    },

    choiceFile() {
      this.$refs.filElem.dispatchEvent(new MouseEvent("click"));
    },
    fileChange(e) {
      var files = e.target.files;
      if (this.type == "1") {
        // 遍历所选文件，逐个上传，实现批量上传
        for (var i = 0; i < files.length; i++) {
          this.txUpload(files[i]);
        }
      }
      // 清空 input 的值，保证下次可以选择相同的文件
      e.target.value = "";
    },
    stopFile() {
      this.baijiauploader.stopFile(0);
      this.status = "warning";
      this.resumeDisabled = false;
      this.pauseDisabled = true;
    },
    txUpload(fileObj) {
      this.userId = this.website.aliyunAccount;
      var title = fileObj.name;
      title = title.replace("(", "");
      title = title.replace(")", "");
      var file = new File([fileObj], title, {
        type: fileObj.type,
      });
      if (!file) {
        alert("请先选择需要上传的文件!");
        return;
      }
      var userData = '{"Vod":{}}';
      // if (this.uploader) {
      //   this.uploader.stopUpload();
         this.authProgress = 0;
         this.statusText = "";
      // }
      let that = this;

    
      let size = "";
      if (file.size < 0.1 * 1024) {
        //小于0.1KB，则转化成B
        size = file.size.toFixed(2) + "B";
      } else if (file.size < 0.1 * 1024 * 1024) {
        //小于0.1MB，则转化成KB
        size = (file.size / 1024).toFixed(2) + "KB";
      } else {
        size = (file.size / (1024 * 1024)).toFixed(2) + "MB";
      }
      //设置formData
   
     // that.uploader = this.createUploader();
      //that.uploader.addFile(that.file, null, null, null, userData);

      that.uploadDisabled = false;
      that.pauseDisabled = true;
      that.resumeDisabled = true;
      //添加文件成功开始上传
      //that.authUpload();
     var self = this
      cos_utils.sliceUploadFile(
        cos_utils.getConfig()
        ,'lecturer-files',file,(progress)=>{
            self.authProgress = progress.percent*100
            self.statusText = "文件上传中...";
           // param.onProgress({percent:progress.percent*100})
        },(data,err)=>{

          console.log("data=>",data)
            if(data!=undefined){
                if(data.statusCode == 200){
                    that.$emit("documentdata",{
                      fileUrl:data.Location,
                      fileName:file.name
                    });
                          // let row = {
                          //   name: this.file.name,
                          //   videoSize: size,
                          //   videoStatus: "finish",
                          //   idVarchar: data.Location,
                          //   videoLength: "",
                          //   videoDuration: duration,
                          //   videoType: 1,
                          //   fileType: 1,
                          //   initType: 1,
                          //   status: 1,
                          // };
                          // add(row).then(
                          //   () => {
                          //     that.$emit("closeupload");
                          //   },
                          //   (error) => {
                          //     window.console.log(error);
                          //     that.$emit("failupload", "文件信息保存失败");
                          //   }
                          // );
                       
                     
                    self.statusText = "文件上传完毕";
                  
                }else{
                  self.status = "exception";
                  self.statusText = "文件上传失败!";
                   // param.onError()
                    console.log(data.statusCode)
                }
            }else{
              self.status = "exception";
              self.statusText = "文件上传失败!";
               // param.onError()
                console.error(err)
            }
        })

    },
    aliyunUpload(e) {
      this.userId = this.website.aliyunAccount;
      // this.file = e.target.files[0]

      var title = e.target.files[0].name;
      title = title.replace("(", "");
      title = title.replace(")", "");
      this.file = new File([e.target.files[0]], title, {
        type: e.target.files[0].type,
      });
      if (!this.file) {
        alert("请先选择需要上传的文件!");
        return;
      }
      var userData = '{"Vod":{}}';
      if (this.uploader) {
        this.uploader.stopUpload();
        this.authProgress = 0;
        this.statusText = "";
      }
      let that = this;
      that.uploader = this.createUploader();
      that.uploader.addFile(that.file, null, null, null, userData);
      that.uploadDisabled = false;
      that.pauseDisabled = true;
      that.resumeDisabled = true;
      //添加文件成功开始上传
      that.authUpload();
    },
    authUpload() {
      // 然后调用 startUpload 方法, 开始上传
      if (this.uploader !== null) {
        this.uploader.startUpload();
        this.uploadDisabled = true;
        this.pauseDisabled = false;
      }
    },
    // 暂停上传
    pauseUpload() {
      if (this.uploader !== null) {
        this.status = "warning";
        this.uploader.stopUpload();
        this.resumeDisabled = false;
        this.pauseDisabled = true;
      }
    },
    // 恢复上传
    resumeUpload() {
      if (this.uploader !== null) {
        this.status = "";
        this.uploader.startUpload();
        this.resumeDisabled = true;
        this.pauseDisabled = false;
      }
    },
    computeMD5(file) {
      let that = this;
      let fileReader = new FileReader();
      let md5 = "";
      file.pause(); //先暂停，后续生成uniqueIdentifier
      fileReader.readAsArrayBuffer(file.file);

      fileReader.onload = (e) => {
        if (file.size != e.target.result.byteLength) {
          this.error("文件读取失败");
          return;
        }

        md5 = SparkMD5.hash(file.name); //业务需求以文件名作为加密
        file.uniqueIdentifier = md5;

        if (md5 != "") {
          file.resume(); //继续上传文件
        }
      };
      fileReader.onerror = function () {
        this.error("文件读取失败");
      };
    },
    //文件添加时回调，这里可以做格式校验，md5计算
    onFileAdded(file) {
      console.log("onFileAdded", file);
      let typeName = file.file.name;
      let reg = /\.zip$/;
      // if (!reg.test(typeName)) {
      //   file.ignored = true;
      //   this.$alert("上传文件格式不正确，请检查", "上传错误", {
      //     confirmButtonText: "确定",
      //     callback: (action) => {},
      //   });
      // } else {
      //   // 计算MD5;
      //   this.computeMD5(file);
      // }
    },
    onFileSuccess: function (rootFile, file, response, chunk) {
      console.log("onFileSuccess", file);
      this.beforeUploadVideo(file.file);
      //这里可以根据response（接口）返回的数据处理自己的实际问题（如：从response拿到后台返回的想要的数据进行组装并显示）
      //注，这里从文件夹每上传成功一个文件会调用一次这个方法
    },
    //文件上传的过程中会频繁调用该函数
    onFileProgress(rootFile, file, chunk) {
      console.log("onFileProgress", file);
    },

    //文件上传出错的回调
    onFileError(rootFile, file, msg, chunk) {},
    createUploader(type) {
      let self = this;
      let uploader = new AliyunUpload.Vod({
        timeout: self.timeout || 60000,
        partSize: self.partSize || 1048576,
        parallel: self.parallel || 5,
        retryCount: self.retryCount || 3,
        retryDuration: self.retryDuration || 2,
        region: self.region,
        userId: self.userId,
        // 添加文件成功
        addFileSuccess: function (uploadInfo) {
          self.uploadDisabled = false;
          self.resumeDisabled = false;
          self.statusText = "添加文件成功, 等待上传...";
          console.log("addFileSuccess: " + uploadInfo.file.name);
        },
        // 开始上传
        onUploadstarted: function (uploadInfo) {
          // 如果是 UploadAuth 上传方式, 需要调用 uploader.setUploadAuthAndAddress 方法
          // 如果是 UploadAuth 上传方式, 需要根据 uploadInfo.videoId是否有值，调用点播的不同接口获取uploadauth和uploadAddress
          // 如果 uploadInfo.videoId 有值，调用刷新视频上传凭证接口，否则调用创建视频上传凭证接口
          // 注意: 这里是测试 demo 所以直接调用了获取 UploadAuth 的测试接口, 用户在使用时需要判断 uploadInfo.videoId 存在与否从而调用 openApi
          // 如果 uploadInfo.videoId 存在, 调用 刷新视频上传凭证接口(https://help.aliyun.com/document_detail/55408.html)
          // 如果 uploadInfo.videoId 不存在,调用 获取视频上传地址和凭证接口(https://help.aliyun.com/document_detail/55407.html)
          if (!uploadInfo.videoId) {
            let createUrl =
              "/api/edu/videosource/createUploadVideo?videoType=1&name=" +
              uploadInfo.file.name +
              "&fileName=" +
              uploadInfo.file.name;
            axios.get(createUrl).then(({ data }) => {
              let uploadAuth = data.data.uploadAuth;
              let uploadAddress = data.data.uploadAddress;
              let videoId = data.data.videoId;
              uploader.setUploadAuthAndAddress(
                uploadInfo,
                uploadAuth,
                uploadAddress,
                videoId
              );
            });
            self.statusText = "文件开始上传...";
            console.log(
              "onUploadStarted:" +
                uploadInfo.file.name +
                ", endpoint:" +
                uploadInfo.endpoint +
                ", bucket:" +
                uploadInfo.bucket +
                ", object:" +
                uploadInfo.object
            );
          } else {
            // 如果videoId有值，根据videoId刷新上传凭证
            // https://help.aliyun.com/document_detail/55408.html?spm=a2c4g.11186623.6.630.BoYYcY
            let refreshUrl =
              "/api/edu/videosource/refreshUploadVideo?videoType=1&idVarchar=" +
              uploadInfo.videoId;
            axios.get(refreshUrl).then(({ data }) => {
              let uploadAuth = data.data.uploadAuth;
              let uploadAddress = data.data.uploadAddress;
              let videoId = data.data.videoId;
              uploader.setUploadAuthAndAddress(
                uploadInfo,
                uploadAuth,
                uploadAddress,
                videoId
              );
            });
          }
        },
        // 文件上传成功
        onUploadSucceed: function (uploadInfo) {
          console.log(
            "onUploadSucceed: " +
              uploadInfo.file.name +
              ", endpoint:" +
              uploadInfo.endpoint +
              ", bucket:" +
              uploadInfo.bucket +
              ", object:" +
              uploadInfo.object
          );
          self.status = "success";
          //文件上传成功后将数据保存到本地
          let row = {
            idVarchar: uploadInfo.videoId,
            videoType: "1",
          };
          setTimeout(function () {
            add(row).then(
              () => {
                self.$emit("closeupload");
              },
              (error) => {
                window.console.log(error);
                self.$emit("failupload", "文件信息保存失败");
              }
            );
            self.statusText = "文件上传成功!";
          }, 1000);
        },
        // 文件上传失败
        onUploadFailed: function (uploadInfo, code, message) {
          console.log(
            "onUploadFailed: file:" +
              uploadInfo.file.name +
              ",code:" +
              code +
              ", message:" +
              message
          );
          self.$emit("failupload", message);
          self.status = "exception";
          self.statusText = "文件上传失败!";
        },
        // 取消文件上传
        onUploadCanceled: function (uploadInfo, code, message) {
          console.log(
            "Canceled file: " +
              uploadInfo.file.name +
              ", code: " +
              code +
              ", message:" +
              message
          );
          self.statusText = "文件已暂停上传";
        },
        // 文件上传进度，单位：字节, 可以在这个函数中拿到上传进度并显示在页面上
        onUploadProgress: function (uploadInfo, totalSize, progress) {
          console.log(
            "onUploadProgress:file:" +
              uploadInfo.file.name +
              ", fileSize:" +
              totalSize +
              ", percent:" +
              Math.ceil(progress * 100) +
              "%"
          );
          let progressPercent = Math.ceil(progress * 100);
          self.authProgress = progressPercent;
          self.statusText = "文件上传中...";
        },
        // 上传凭证超时
        onUploadTokenExpired: function (uploadInfo) {
          // 上传大文件超时, 如果是上传方式一即根据 UploadAuth 上传时
          // 需要根据 uploadInfo.videoId 调用刷新视频上传凭证接口(https://help.aliyun.com/document_detail/55408.html)重新获取 UploadAuth
          // 然后调用 resumeUploadWithAuth 方法, 这里是测试接口, 所以我直接获取了 UploadAuth
          let refreshUrl =
            "/api/edu/videosource/refreshUploadVideo?videoType=1&idVarchar=" +
            uploadInfo.videoId;
          axios.get(refreshUrl).then(({ data }) => {
            let uploadAuth = data.data.uploadAuth;
            uploader.resumeUploadWithAuth(uploadAuth);
            console.log(
              "upload expired and resume upload with uploadauth " + uploadAuth
            );
          });
          self.status = "exception";
          self.statusText = "文件超时...";
          self.$emit("failupload", "文件超时...");
        },
        // 全部文件上传结束
        onUploadEnd: function (uploadInfo) {
          console.log("onUploadEnd: uploaded all the files");
          self.statusText = "文件上传完毕";
        },
      });
      return uploader;
    },
  },
};
</script>
<style type="text/css">
.bjy-desc {
  width: 600px;
  position: absolute;
  right: 0;
  border: 1px solid #ccc;
  padding: 10px;
}
.bjy-demo {
  width: 500px;
  float: left;
}
.bjy-demo .button {
  background-color: #37a4f5;
  display: inline-block;
  color: #fff;
  padding: 10px 15px;
  vertical-align: top;
}
.bjy-demo .upload-wrapper {
  position: relative;
  overflow: hidden;
}
.bjy-demo object,
.bjy-demo input[type="file"] {
  opacity: 0;
  filter: alpha(opacity=0);
  cursor: pointer;
  position: absolute;
  left: 0;
  top: 0;
  width: 110px;
  height: 100%;
  z-index: 2;
}
.item-list {
  font-size: 14px;
  color: #2e5580;
  padding: 10px;
  display: block;
  float: left;
  width: 100%;
}
.item-name {
  display: inline-block;
  margin-right: 20px;
}
.uploader-example {
  width: 90%;
  padding: 15px;
  margin: 40px auto 0;
  font-size: 12px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.4);
}

.uploader-example .uploader-btn {
  margin-right: 4px;
}

.uploader-example .uploader-list {
  max-height: 440px;
  overflow: auto;
  overflow-x: hidden;
  overflow-y: auto;
}
</style>
