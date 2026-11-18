<template>
  <basic-container>
    <div
      v-if="videosource.fileType&&videosource.fileType == 'video'"
      id="video-box"
      style="width: 100%; height: 500px"
    >
    <video
        ref="video"
        controls="controls"
        style="width: 100%; height: 500px"
      ></video>
    </div>
    <div
      v-else
      id="main"
      style="width: 100%; height: 500px"
    >
      <div class="bjc-player-wrapper">
        <iframe
                    style="width: 100%; height: 600px"
                    allowfullscreen="true"
                    id="previewer"
                    src=""
                  >
                  </iframe>
      </div>
    </div>
    
  </basic-container>
</template>

<script>
import { getDetail } from "@/api/edu/courseMaterial";

export default {
  data() {
    return {};
  },
  props: {
    videosource: Object,
  },
  watch: {
    videosource: {
      deep: true,
      handler(newVal) {
        this.init();
      },
    },
  },
  mounted: function () {
    const oScript = document.createElement("script");
    oScript.type = "text/javascript";
    oScript.src = "/static/js/aliyunPlayer/2.8.2/aliplayer-min.js";
    document.body.appendChild(oScript);
    const link = document.createElement("link");
    link.rel = "stylesheet";
    link.href =
      "//g.alicdn.com/de/prismplayer/2.8.2/skins/default/aliplayer-min.css";
    link.type = "text/css";
    document.body.appendChild(link);
    const bScript = document.createElement("script");
    bScript.type = "text/javascript";
    bScript.src =
      "//live-cdn.baijiayun.com/www-video-jssdk/0.0.7/playback/player.js";
    document.body.appendChild(bScript);
    const blink = document.createElement("link");
    blink.rel = "stylesheet";
    blink.href =
      "//live-cdn.baijiayun.com/www-video-jssdk/0.0.7/playback/player.css";
    blink.type = "text/css";
    document.body.appendChild(blink);



    this.init();
  },
  methods: {
    init() {
      let that = this;
      getDetail(this.videosource.id).then((res) => {
        let videosource = res.data.data;
        if (videosource.fileType == "video") {
          this.$refs.video.src = videosource.idVarchar;
          this.$refs.video.play();
        }else{

          this.$nextTick(e=>{
              var previewer = document.getElementById("previewer");
              console.log("documentUrl", videosource);
              var iframeSrc ="http://139.9.162.79:8012/onlinePreview?url=" +encodeURIComponent(Base64.encode(videosource.fileUrl));

              console.log("iframeSrc",previewer,iframeSrc)
              //previewer.height = window.innerHeight;
              previewer.setAttribute("src", iframeSrc);
          })

      //window.open(row.fileUrl);
   
        }
      });
    },
  },
};
</script>

<style>
.bjc-player-wrapper {
  width: 100%;
  height: 100%;
}
.bjc-player-wrapper .video-js {
  width: 100%;
  height: 100%;
}
</style>
