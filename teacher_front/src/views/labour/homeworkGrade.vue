<template>
  <basic-container>
    <el-form inline>
      <el-form-item label="我的课程">
        <el-select
          v-model="courseId"
          filterable
          clearable
          :loading="loadingCourses"
          placeholder="请选择课程查看作业提交情况"
          style="width: 360px"
        >
          <el-option
            v-for="course in courses"
            :key="course.id"
            :label="course.name"
            :value="course.id"
          />
        </el-select>
      </el-form-item>
    </el-form>

    <course-homework-kpointhistory
      v-if="courseId"
      :course-id="courseId"
      course-type="COURSE"
      :kpoint-course-id="0"
    />
    <el-empty v-else description="选择课程后查看章节作业和批改情况" />
  </basic-container>
</template>

<script>
import { getList } from "@/api/edu/course";
import courseHomeworkKpointhistory from "@/views/labour/courseHomeworkKpointhistory";

export default {
  name: "HomeworkGrade",
  components: { courseHomeworkKpointhistory },
  data() {
    return {
      courses: [],
      courseId: "",
      loadingCourses: false,
    };
  },
  created() {
    this.loadCourses();
  },
  methods: {
    loadCourses() {
      this.loadingCourses = true;
      getList(1, 1000, { mine: 1 })
        .then((res) => {
          const page = res.data && res.data.data;
          this.courses = (page && page.records) || [];
        })
        .finally(() => {
          this.loadingCourses = false;
        });
    },
  },
};
</script>
