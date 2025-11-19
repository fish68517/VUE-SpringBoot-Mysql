<template>
  <div class="post-list-page">
    <div class="container">
      <div class="header-section">
        <h1 class="page-title">宠物社区</h1>
        <router-link to="/community/create" class="btn-create-post">
          发布帖子
        </router-link>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading">加载中...</div>

      <!-- 空状态 -->
      <div v-else-if="posts.length === 0" class="empty-state">
        <p>暂无帖子</p>
        <router-link to="/community/create" class="btn-create-post-empty">
          发布第一个帖子
        </router-link>
      </div>

      <!-- 帖子列表 -->
      <div v-else class="posts-list">
        <div
          v-for="post in posts"
          :key="post.id"
          class="post-card"
          @click="goToDetail(post.id)"
        >
          <!-- 帖子头部 -->
          <div class="post-header">
            <div class="user-info">
              <img :src="post.userAvatar || '/default-avatar.png'" :alt="post.userName" class="user-avatar" />
              <div class="user-details">
                <p class="user-name">{{ post.userName }}</p>
                <p class="post-time">{{ formatDate(post.createTime) }}</p>
              </div>
            </div>
            <button
              v-if="isOwnPost(post.userId)"
              @click.stop="deletePostAction(post.id)"
              class="btn-delete"
            >
              删除
            </button>
          </div>

          <!-- 帖子内容 -->
          <div class="post-content">
            <h3 class="post-title">{{ post.title }}</h3>
            <p class="post-text">{{ truncateText(post.content, 150) }}</p>
            <div v-if="post.images && post.images.length > 0" class="post-images">
              <img
                v-for="(image, index) in post.images.slice(0, 3)"
                :key="index"
                :src="image"
                :alt="post.title"
                class="post-image"
              />
              <div v-if="post.images.length > 3" class="image-more">
                +{{ post.images.length - 3 }}
              </div>
            </div>
          </div>

          <!-- 帖子统计 -->
          <div class="post-stats">
            <span class="stat-item">
              <i class="icon">👁</i>
              {{ post.views || 0 }}
            </span>
            <span class="stat-item">
              <i class="icon">💬</i>
              {{ post.replyCount || 0 }}
            </span>
            <span class="stat-item">
              <i class="icon">❤</i>
              {{ post.likes || 0 }}
            </span>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div v-if="posts.length > 0" class="pagination-section">
        <Pagination
          :total="total"
          :page-size="pageSize"
          :current-page="currentPage"
          @page-change="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import { useUserStore } from "@/store/userStore";
import { getPostList, deletePost } from "@/api/community";
import Pagination from "@/components/Pagination.vue";

const router = useRouter();
const userStore = useUserStore();

const posts = ref([]);
const loading = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

const loadPosts = async () => {
  loading.value = true;
  try {
    const response = await getPostList({
      page: currentPage.value,
      pageSize: pageSize.value
    });
    posts.value = response.content || [];
    total.value = response.totalElements || 0;
  } catch (error) {
    console.error("加载帖子失败:", error);
    ElMessage.error("加载帖子失败");
  } finally {
    loading.value = false;
  }
};

const handlePageChange = (page) => {
  currentPage.value = page;
  loadPosts();
};

const goToDetail = (postId) => {
  router.push(`/community/post/${postId}`);
};

const isOwnPost = (userId) => {
  return userStore.isLogin && userStore.userInfo?.id === userId;
};

const deletePostAction = async (postId) => {
  try {
    await ElMessageBox.confirm("确定要删除这个帖子吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning"
    });

    await deletePost(postId);
    ElMessage.success("帖子已删除");
    loadPosts();
  } catch (error) {
    if (error !== "cancel") {
      console.error("删除帖子失败:", error);
      ElMessage.error("删除帖子失败");
    }
  }
};

const truncateText = (text, length) => {
  if (!text) return "";
  return text.length > length ? text.substring(0, length) + "..." : text;
};

const formatDate = (dateString) => {
  if (!dateString) return "";
  const date = new Date(dateString);
  const now = new Date();
  const diff = now - date;
  const minutes = Math.floor(diff / 60000);
  const hours = Math.floor(diff / 3600000);
  const days = Math.floor(diff / 86400000);

  if (minutes < 1) return "刚刚";
  if (minutes < 60) return `${minutes}分钟前`;
  if (hours < 24) return `${hours}小时前`;
  if (days < 7) return `${days}天前`;

  return date.toLocaleDateString("zh-CN");
};

onMounted(() => {
  loadPosts();
});
</script>

<style scoped>
.post-list-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 20px 0;
}

.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 20px;
}

.header-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.page-title {
  margin: 0;
  font-size: 28px;
  color: #333;
  font-weight: 600;
}

.btn-create-post {
  padding: 10px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  text-decoration: none;
  border-radius: 4px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.btn-create-post:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.loading,
.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 8px;
  color: #999;
  font-size: 16px;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.btn-create-post-empty {
  display: inline-block;
  padding: 10px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  text-decoration: none;
  border-radius: 4px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.btn-create-post-empty:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.posts-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.post-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;
}

.post-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.user-info {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  background: #f0f0f0;
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.user-name {
  margin: 0;
  font-size: 14px;
  color: #333;
  font-weight: 600;
}

.post-time {
  margin: 0;
  font-size: 12px;
  color: #999;
}

.btn-delete {
  padding: 6px 12px;
  background: #ff6b6b;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-delete:hover {
  background: #ff5252;
}

.post-content {
  margin-bottom: 15px;
}

.post-title {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #333;
  font-weight: 600;
}

.post-text {
  margin: 0 0 12px 0;
  font-size: 14px;
  color: #666;
  line-height: 1.6;
}

.post-images {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  gap: 8px;
  margin-top: 12px;
}

.post-image {
  width: 100%;
  height: 100px;
  border-radius: 4px;
  object-fit: cover;
  background: #f0f0f0;
}

.image-more {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100px;
  background: rgba(0, 0, 0, 0.5);
  color: white;
  font-size: 14px;
  font-weight: 600;
  border-radius: 4px;
}

.post-stats {
  display: flex;
  gap: 20px;
  padding-top: 12px;
  border-top: 1px solid #eee;
  font-size: 12px;
  color: #999;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.icon {
  font-style: normal;
}

.pagination-section {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

@media (max-width: 768px) {
  .header-section {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }

  .post-card {
    padding: 15px;
  }

  .post-images {
    grid-template-columns: repeat(2, 1fr);
  }

  .post-stats {
    gap: 15px;
  }
}
</style>
