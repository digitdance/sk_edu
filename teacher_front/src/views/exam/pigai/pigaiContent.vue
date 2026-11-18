<template>

  <div class="co_l mt10">
    <div class="as_head as_head2">
      <span class="as_h_title">{{ recordInfo }}</span>
     
    </div>
   



  </div>
</template>

<script>
import {course, favoritesqstRemove, favoritesqstSave} from "@/api";
import {indexToString} from "@/util/common";
import {mapGetters} from "vuex";

export default {
  name: 'courseListContent',
  data() {
    return {
      courseList: [],
      current: 1,
      size: 10,
      sellType: "",
      level: "",
      examReccord:{},
      subjectId: "",
      subject1Id: "",
      pageList: [],
      pages: 0,
      page: {},
      levelFlag: false,
      orderBy: "default",
    }
  },
  components: {},
  methods: {
    textInput(qstmiddle){
      qstmiddle.useranswer = qstmiddle.answerArr.toString();
    },
    go(index){
      let anchor = this.$refs[index][0].offsetTop;
      this.$nextTick(() => {
        document.body.scrollTop = anchor;
      });
    },
    radioboxChange(qstmiddle,index){
      qstmiddle.useranswer = ''
      qstmiddle.useranswer = indexToString(index);
    },
    checkboxChange(qstmiddle,index){
      let answer = indexToString(index);
      let useranswer = qstmiddle.useranswer;
      let arr = useranswer.split(",");
      if(arr.indexOf(answer) == -1){
        arr.push(answer)
        arr.sort();
        let str = "";
        for (let i = 0; i < arr.length; i++) {
          if(i == 0){
            str += arr[i]
          }else{
            str += ","+arr[i]
          }
        }
        qstmiddle.useranswer = str
      }else{
        let str = ""
        for (let i = 0; i < arr.length; i++) {
          if(answer != arr[i]){
            str += arr[i]+","
          }
        }
        str = str.substr(0,str.length - 1)
        qstmiddle.useranswer = str
      }
      console.log(qstmiddle.useranswer)

    },
    check(index,useranswer){
      let arr = useranswer.split(",");
      if(arr.indexOf(indexToString(index)) !=-1){
        return true
      }else{
        return false;
      }
    },
    clickSignFalse(qstmiddle){
      qstmiddle.signFlag = false;
    },
    clickSignTrue(qstmiddle){
      qstmiddle.signFlag = true;
    },
    indexToString(str){
      return indexToString(str);
    },
    saveFav(qstmiddle){
      let param = {
        userId:this.studentInfo.id,
        qstId:qstmiddle.qstId
      }
      favoritesqstSave(param).then(res=>{
        qstmiddle.userFavor = true
        this.$layer.alert(
            "收藏成功！",
            {
              shade: true,
              title: "提示"
            },
            laeryid => {
              this.$layer.close(laeryid);
            }
        );
      })
    },
    removeFav(qstmiddle){
      let param = {
        userId:this.studentInfo.id,
        qstId:qstmiddle.qstId
      }
      favoritesqstRemove(param).then(res=>{
        qstmiddle.userFavor = false
        this.$layer.alert(
            "取消收藏！",
            {
              shade: true,
              title: "提示"
            },
            laeryid => {
              this.$layer.close(laeryid);
            }
        );
      })
    },
  },
  mounted: function () {
  },
  props:{
    examRecord: Object,
    recordInfo: Object
  },
  watch: {
    recordInfo: {
      deep: true,
      handler(newVal){

        console.log("recordInfo===>",this.recordInfo)
      }
    },
    examInfo: {
      deep: true,
      handler(newVal){
        console.log(this.examInfo)
      }
    },
  },
  computed: {
    ...mapGetters(["studentInfo"]),
  },
}
</script>

<style lang="scss" >

</style>
