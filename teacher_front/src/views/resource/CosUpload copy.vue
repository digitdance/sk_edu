<template>
  <div class="container">
    <div class="setting">

    </div>

    <div class="upload">
      <el-form ref="form" label-width="80px">
        <el-form-item label="云">
          <el-select v-model="type" placeholder="请选择">
            <el-option
              v-for="item in options"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="上传文件" v-show="type == '3'">
          <el-upload
            class="upload-demo"
            ref="upload"
            :action="
              '/api/crazy-system/resource/fileUpload?crazy-auth=' + getToken()
            "
            :limit="1"
            :on-success="uploadSuccess"
            :file-list="fileList"
            :before-upload="beforeUploadVideo"
          >
            <el-button slot="trigger" size="small" type="primary"
              >上传<i class="el-icon-upload el-icon--right"></i
            ></el-button>
          </el-upload>
          <!--          <el-button type="primary" @click="submitUpload()">确定</el-button>-->
        </el-form-item>
        <!-- 
        <el-form-item label="上传文件" v-show="type == '1'">
          <el-button
            @click="choiceFile"
            type="primary"
            :disabled="uploadDisabled"
            >上传<i class="el-icon-upload el-icon--right"></i
          ></el-button>
          <input
            ref="filElem"
            type="file"
            v-show="false"
            id="fileUpload"
            @change="fileChange($event)"
          />
          <el-button
            type="primary"
            @click="pauseUpload"
            style="margin-left: 10px"
            :disabled="pauseDisabled"
            >停止上传<i class="el-icon-video-pause el-icon--right"></i
          ></el-button>
          <el-button
            type="primary"
            @click="resumeUpload"
            :disabled="resumeDisabled"
            >继续上传<i class="el-icon-video-play el-icon--right"></i
          ></el-button>
        </el-form-item> -->
        <el-form-item label="上传进度" v-show="type != 2">
          <el-progress
            :percentage="authProgress"
            style="line-height: 35px"
            :status="status"
            :stroke-width="15"
          ></el-progress>
        </el-form-item>
        <el-form-item label="上传进度" v-if="baijiayunInfos.length > 0">
          <div
            style="
              padding-left: 20px;
              overflow: auto;
              height: 230px;
              width: 85%;
              border-radius: 5px;
              border: #d9d9d9 1px dashed;
            "
          >
            <div class="bjy-demo" v-for="(item, index) in baijiayunInfos">
              {{ item.fileName }}
              <el-progress
                :percentage="item.authProgress"
                style="line-height: 30px"
                :status="item.status"
                :stroke-width="4"
              ></el-progress>
            </div>
          </div>
        </el-form-item>
      </el-form>

      <!--   <div>


          <label class="status">上传状态: <span>{{statusText}}</span></label>
        </div>
        <div >
         <button @click="authUpload" :disabled="uploadDisabled">开始上传</button>
          <button @click="pauseUpload" :disabled="pauseDisabled">暂停</button>
          <button :disabled="resumeDisabled" @click="resumeUpload">恢复上传</button>

      </div>-->
    </div>
  </div>
</template>
<script>
import axios from "axios";
import { mapGetters } from "vuex";
import { getList, getDetail, add, update, remove } from "@/api/system/resource";
import { getResumeUploadUrl, getUploadUrl } from "@/api/web/baijia";
import { baijiaUploader } from "@/util/baijiaUpload";
import { setStore, getStore } from "@/util/store";
import { getToken } from "@/util/auth";
export default {
  data() {
    return {
      baijiayunInfos: [], //视频云多文件信息
      baijiauploader: null,
      type: "3",
      timeout: "",
      partSize: "",
      parallel: "",
      retryCount: "",
      retryDuration: "",
      region: "cn-shanghai",
      userId: "",
      file: null,
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

        // {
        //   value: '2',
        //   label: '视频云'
        // },

        {
          value: "3",
          label: "本地上传",
        },
      ],
    };
  },
  computed: {
    ...mapGetters(["website"]),
  },
  mounted: function () {
    // const oScript = document.createElement("script");
    // oScript.type = "text/javascript";
    // oScript.src = "/admin/aliyun-upload-sdk/lib/es6-promise.min.js";
    // document.body.appendChild(oScript);
    // const oScript2 = document.createElement("script");
    // oScript2.type = "text/javascript";
    // oScript2.src = "/admin/aliyun-upload-sdk/lib/aliyun-oss-sdk-5.3.1.min.js";
    // document.body.appendChild(oScript2);
    // const oScript3 = document.createElement("script");
    // oScript3.type = "text/javascript";
    // oScript3.src = "/admin/aliyun-upload-sdk/aliyun-upload-sdk-1.5.0.min.js";
    // document.body.appendChild(oScript3);
    // this.init();
  },
  methods: {
    getToken,
    uploadSuccess(res, file) {
      console.log(res);
      console.log(file);
      add({
        fileName: file.name,
        url: file.response.data.url,
        filesize: file.size,
      }).then((res) => {
        console.log("保存成功");
      });
    },
    init() {
      //this.options = getStore({ name: "yunconfig" });
      //this.baijiayun();
    },
    //上传视频格式限制
    beforeUploadVideo(file) {
      // if (file.type != "video/mp4") {
      //   this.$message.warning("请上传mp4文件！");
      //   return false;
      // }
      // if (file.size >= 1073741824) {
      //   this.$message.warning("本地上传最大支持1GB  文件");
      //   return false;
      // }

      console.log("file", file);
      // //获取学习时长
      // let videoUrl = URL.createObjectURL(file);
      // let audioElement = new Audio(videoUrl);
      // audioElement.addEventListener("loadedmetadata", () => {
      //   let duration = audioElement.duration; //时长为秒，小数，182.36
      //   this.addVideo(file, duration);
      // });
      // return false;
    },
    //上传视频接口

    choiceFile() {
      this.$refs.filElem.dispatchEvent(new MouseEvent("click"));
    },
    fileChange(e) {
      if (this.type == "1") {
        this.aliyunUpload(e);
      }
    },
    stopFile() {
      this.baijiauploader.stopFile(0);
      this.status = "warning";
      this.resumeDisabled = false;
      this.pauseDisabled = true;
    },
    continueFile() {
      // 继续上传
      var data = this.baijiauploader.currentFiles[0];
      this.status = "";
      this.resumeDisabled = true;
      this.pauseDisabled = false;
      this.baijiauploader.resumeUpload(data);
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
</style>
