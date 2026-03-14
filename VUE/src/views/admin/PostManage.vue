<template>
  <div class="post-manage">
    <h1>社区管理</h1>

    <div class="filter-section">
      <el-button type="primary" @click="fetchPostList">刷新</el-button>
    </div>

    <el-table :data="postList" stripe style="width: 100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="帖子标题" min-width="220" show-overflow-tooltip />
      <el-table-column prop="username" label="作者" width="120" />
      <el-table-column prop="views" label="浏览数" width="90" />
      <el-table-column prop="likes" label="点赞数" width="90" />
      <el-table-column label="创建时间" width="180">
        <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button type="info" size="small" @click="viewPost(row)">查看</el-button>
          <el-button type="danger" size="small" @click="handleDeletePost(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @change="fetchPostList"
      />
    </div>

    <el-dialog v-model="detailDialogVisible" title="帖子详情" width="700px">
      <div v-if="selectedPost" class="post-detail">
        <p><b>标题：</b>{{ selectedPost.title }}</p>
        <p><b>作者：</b>{{ selectedPost.username || '-' }}</p>
        <p><b>发布时间：</b>{{ formatDate(selectedPost.createTime) }}</p>
        <p><b>内容：</b></p>
        <div class="content">{{ selectedPost.content }}</div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import * as adminApi from "@/api/admin";

const postList = ref([]);
const loading = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const detailDialogVisible = ref(false);
const selectedPost = ref(null);

const fetchPostList = async () => {
  loading.value = true;
  try {
    const response = await adminApi.getPostList({
      page: currentPage.value - 1,
      size: pageSize.value
    });
    postList.value = response.content || [];
    total.value = response.totalElements || 0;
  } catch (error) {
    console.error(error);
    ElMessage.error("获取帖子列表失败");
  } finally {
    loading.value = false;
  }
};

const viewPost = (row) => {
  selectedPost.value = row;
  detailDialogVisible.value = true;
};

const handleDeletePost = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确认删除帖子「${row.title}」吗？`,
      "警告",
      {
        confirmButtonText: "确认",
        cancelButtonText: "取消",
        type: "warning"
      }
    );

    await adminApi.deletePost(row.id);
    ElMessage.success("帖子已删除");

    if (selectedPost.value?.id === row.id) {
      detailDialogVisible.value = false;
      selectedPost.value = null;
    }

    await fetchPostList();
  } catch (error) {
    if (error !== "cancel") {
      console.error(error);
      ElMessage.error("删除帖子失败");
    }
  }
};

const formatDate = (dateString) => {
  if (!dateString) return "-";
  return new Date(dateString).toLocaleString("zh-CN");
};

onMounted(() => {
  fetchPostList();
});
</script>

<style scoped>
.post-manage {
  background: white;
  border-radius: 8px;
  padding: 20px;
}

h1 {
  margin: 0 0 20px;
  font-size: 24px;
  color: #333;
}

.filter-section {
  margin-bottom: 20px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.content {
  white-space: pre-wrap;
  line-height: 1.6;
  color: #666;
}
</style>
