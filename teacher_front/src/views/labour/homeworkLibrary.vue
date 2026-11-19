<template>
  <basic-container>
    <div class="homework-library__toolbar">
      <el-input
        v-model="keyword"
        clearable
        placeholder="按作业名称搜索"
        style="width: 280px"
        @keyup.enter.native="search"
        @clear="search"
      >
        <el-button slot="append" icon="el-icon-search" @click="search">搜索</el-button>
      </el-input>
    </div>

    <el-table v-loading="loading" :data="rows" border>
      <el-table-column prop="name" label="作业名称" min-width="180" />
      <el-table-column prop="fileName" label="附件" min-width="180">
        <template slot-scope="scope">
          <el-link v-if="scope.row.fileUrl" :href="scope.row.fileUrl" target="_blank" type="primary">
            {{ scope.row.fileName || "查看附件" }}
          </el-link>
          <span v-else>—</span>
        </template>
      </el-table-column>
      <el-table-column prop="courseId" label="课程编号" width="110" />
      <el-table-column prop="kpointId" label="小节编号" width="110" />
      <el-table-column prop="sort" label="排序" width="90" />
      <el-table-column prop="createTime" label="创建时间" min-width="170" />
    </el-table>

    <el-pagination
      class="homework-library__pagination"
      :current-page="page.currentPage"
      :page-size="page.pageSize"
      :total="page.total"
      layout="total, prev, pager, next, sizes"
      @current-change="changePage"
      @size-change="changeSize"
    />
  </basic-container>
</template>

<script>
import { getList } from "@/api/edu/courseHomework";

export default {
  name: "HomeworkLibrary",
  data() {
    return {
      keyword: "",
      loading: false,
      rows: [],
      page: { currentPage: 1, pageSize: 10, total: 0 },
    };
  },
  created() {
    this.load();
  },
  methods: {
    search() {
      this.page.currentPage = 1;
      this.load();
    },
    changePage(currentPage) {
      this.page.currentPage = currentPage;
      this.load();
    },
    changeSize(pageSize) {
      this.page.pageSize = pageSize;
      this.page.currentPage = 1;
      this.load();
    },
    load() {
      this.loading = true;
      getList(this.page.currentPage, this.page.pageSize, { name: this.keyword })
        .then((res) => {
          const result = res.data && res.data.data;
          this.rows = (result && result.records) || [];
          this.page.total = (result && result.total) || 0;
        })
        .finally(() => {
          this.loading = false;
        });
    },
  },
};
</script>

<style scoped>
.homework-library__toolbar {
  margin-bottom: 16px;
}

.homework-library__pagination {
  margin-top: 16px;
  text-align: right;
}
</style>
