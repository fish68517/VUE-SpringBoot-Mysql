<template>
  <div class="post-manage">
    <h1>社区管理</h1>
    
    <!-- 筛选 -->
    <div class="filter-section">
      <div class="filter-group">
        <el-button type="primary" @click="fetchPostList">刷新</el-button>
        <el-button @click="resetFilter">重置</el-button>
      </div>
    </div>

    <!-- 帖子列表表格 -->
    <div class="table-section">
      <el-table
        :data="postList"
        stripe
        style="width: 100%"
        v-loading="loading"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="帖子标题" width="200" show-overflow-tooltip />
        <el-table-column label="作者" width="120">
          <template #default="{ row }">
            {{ row.user?.username || "-" }}
          </template>
        </el-table-column>
        <el-table-column prop="views" label="浏览数" width="100" />
        <el-table-column prop="likes" label="点赞数" width="100" />
        <el-table-column label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button
              type="info"
              size="small"
              @click="handleViewPost(row)"
            >
              查看
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="handleDeletePost(row)"
            >
              删除帖子
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
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
    </div>

    <!-- 帖子详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="帖子详情" width="700px">
      <div v-if="selectedPost" class="post-detail">
        <div class="detail-item">
          <span class="label">帖子标题：</span>
          <span>{{ selectedPost.title }}</span>
        </div>
        <div class="detail-item">
          <span class="label">作者：</span>
          <span>{{ selectedPost.user?.username }}</span>
        </div>
        <div class="detail-item">
          <span class="label">发布时间：</span>
          <span>{{ formatDate(selectedPost.createTime) }}</span>
        </div>
        <div class="detail-item">
          <span class="label">浏览数：</span>
          <span>{{ selectedPost.views }}</span>
        </div>
        <div class="detail-item">
          <span class="label">点赞数：</span>
          <span>{{ selectedPost.likes }}</span>
        </div>
        <div class="detail-item">
          <span class="label">帖子内容：</span>
          <div class="content">{{ selectedPost.content }}</div>
        </div>

        <!-- 评论列表 -->
        <div class="replies-section">
          <h3>评论列表</h3>
          <div v-if="selectedPost.replies && selectedPost.replies.length > 0" class="replies-list">
            <div v-for="reply in selectedPost.replies" :key="reply.id" class="reply-item">
              <div class="reply-header">
                <span class="reply-author">{{ reply.user?.username }}</span>
                <span class="reply-time">{{ formatDate(reply.createTime) }}</span>
                <el-button
                  type="danger"
                  size="small"
                  @click="handleDeleteReply(reply)"
                >
                  删除
                </el-button>
              </div>
              <div class="reply-content">{{ reply.content }}</div>
            </div>
          </div>
          <div v-else class="no-replies">暂无评论</div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
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
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value
    };
    
    const response = await adminApi.getPostList(params);
    postList.value = response.data || [];
    total.value = response.total || 0;
  } catch (error) {
    ElMessage.error("获取帖子列表失败");
  } finally {
    loading.value = false;
  }
};

const resetFilter = () => {
  currentPage.value = 1;
  fetchPostList();
};

const handleViewPost = (row) => {
  selectedPost.value = row;
  detailDialogVisible.value = true;
};

const handleDeletePost = (row) => {
  ElMessageBox.confirm(
    `确定要删除帖子 "${row.title}" 吗？删除后将同时删除所有评论。`,
    "警告",
    {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning"
    }
  )
    .then(async () => {
      try {
        await adminApi.deletePost(row.id);
        ElMessage.success("帖子已删除");
        fetchPostList();
      } catch (error) {
        ElMessage.error("删除帖子失败");
      }
    })
    .catch(() => {
      // 取消操作
    });
};

const handleDeleteReply = (reply) => {
  ElMessageBox.confirm(
    "确定要删除该评论吗？",
    "警告",
    {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning"
    }
  )
    .then(async () => {
      try {
        await adminApi.deleteReply(reply.id);
        ElMessage.success("评论已删除");
        // 从列表中移除该评论
        const index = selectedPost.value.replies.findIndex(r => r.id === reply.id);
        if (index > -1) {
          selectedPost.value.replies.splice(index, 1);
        }
      } catch (error) {
        ElMessage.error("删除评论失败");
      }
    })
    .catch(() => {
      // 取消操作
    });
};

const formatDate = (dateString) => {
  if (!dateString) return "-";
  const date = new Date(dateString);
  return date.toLocaleString("zh-CN");
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
  margin: 0 0 20px 0;
  font-size: 24px;
  color: #333;
}

h3 {
  margin: 20px 0 15px 0;
  font-size: 16px;
  color: #333;
}

.filter-section {
  margin-bottom: 20px;
  padding: 15px;
  background: #f5f5f5;
  border-radius: 4px;
}

.filter-group {
  display: flex;
  gap: 10px;
  align-items: center;
}

.table-section {
  margin-top: 20px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.post-detail {
  padding: 20px 0;
}

.detail-item {
  display: flex;
  margin-bottom: 15px;
  align-items: flex-start;
}

.detail-item .label {
  width: 100px;
  font-weight: bold;
  color: #333;
  flex-shrink: 0;
}

.detail-item span {
  flex: 1;
  color: #666;
  word-break: break-all;
}

.detail-item .content {
  flex: 1;
  color: #666;
  word-break: break-all;
  white-space: pre-wrap;
  line-height: 1.6;
}

.replies-section {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.replies-list {
  max-height: 400px;
  overflow-y: auto;
}

.reply-item {
  padding: 15px;
  margin-bottom: 10px;
  background: #f9f9f9;
  border-radius: 4px;
}

.reply-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.reply-author {
  font-weight: bold;
  color: #333;
}

.reply-time {
  font-size: 12px;
  color: #999;
}

.reply-content {
  color: #666;
  word-break: break-all;
  white-space: pre-wrap;
  line-height: 1.6;
}

.no-replies {
  text-align: center;
  color: #999;
  padding: 20px;
}

@media (max-width: 768px) {
  .filter-group {
    flex-direction: column;
  }

  .detail-item {
    flex-direction: column;
  }

  .detail-item .label {
    width: auto;
    margin-bottom: 5px;
  }
}
</style>
