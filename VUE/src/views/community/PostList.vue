<template>
  <div class="post-list-page">
    <div class="container">
      <div class="header-section">
        <h1 class="page-title">宠物社区</h1>
        <router-link to="/user/community/create" class="btn-create-post">发布帖子</router-link>
      </div>

      <div v-if="loading" class="loading">加载中...</div>

      <div v-else-if="posts.length === 0" class="empty-state">
        <p>暂无帖子</p>
        <router-link to="/user/community/create" class="btn-create-post-empty">发布第一个帖子</router-link>
      </div>

      <div v-else class="posts-list">
        <div v-for="post in posts" :key="post.id" class="post-card" @click="goToDetail(post.id)">
          <div class="post-header">
            <div class="user-info">
              <img
                :src="getAvatarUrl(post.userAvatar)"
                :alt="post.username || '用户'"
                class="user-avatar"
                @error="handleAvatarError"
              />
              <div class="user-details">
                <p class="user-name">{{ post.username || `用户${post.userId}` }}</p>
                <p class="post-time">{{ formatDate(post.createTime) }}</p>
              </div>
            </div>
            <button v-if="isOwnPost(post.userId)" @click.stop="deletePostAction(post.id)" class="btn-delete">删除</button>
          </div>

          <div class="post-content">
            <h3 class="post-title">{{ post.title }}</h3>
            <p class="post-text">{{ truncateText(post.content, 150) }}</p>

            <div v-if="post.imageList.length > 0" class="post-images">
              <img
                v-for="(image, index) in post.imageList.slice(0, 3)"
                :key="index"
                :src="getImageUrl(image)"
                :alt="post.title"
                @error="handlePostImageError"
                class="post-image"
              />
              <div v-if="post.imageList.length > 3" class="image-more">+{{ post.imageList.length - 3 }}</div>
            </div>
          </div>

          <div class="post-stats">
            <span class="stat-item">浏览 {{ post.views || 0 }}</span>
            <span class="stat-item">点赞 {{ post.likes || 0 }}</span>
          </div>
        </div>
      </div>

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
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import { useUserStore } from "@/store/userStore";
import { getPostList, deletePost } from "@/api/community";
import Pagination from "@/components/Pagination.vue";
import defaultPostImage from "@/assets/bg.jpg";

const router = useRouter();
const userStore = useUserStore();

const posts = ref([]);
const loading = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

const parseImages = (images) => {
  if (!images) return [];
  if (Array.isArray(images)) return images;
  if (typeof images === "string") {
    try {
      const parsed = JSON.parse(images);
      return Array.isArray(parsed) ? parsed : [];
    } catch {
      return [];
    }
  }
  return [];
};

const getAvatarUrl = (src) => {
  if (!src) return defaultPostImage;
  if (src.startsWith("http")) return src;
  return `http://localhost:8080/uploads/${src}`;
};

const getImageUrl = (src) => {
  if (!src) return defaultPostImage;
  if (src.startsWith("http")) return src;
  return `http://localhost:8080/uploads/${src}`;
};

const handlePostImageError = (event) => {
  const img = event?.target;
  if (!img || img.dataset.fallbackApplied === "1") return;
  img.dataset.fallbackApplied = "1";
  img.src = defaultPostImage;
};

const handleAvatarError = (event) => {
  const img = event?.target;
  if (!img || img.dataset.fallbackApplied === "1") return;
  img.dataset.fallbackApplied = "1";
  img.src = defaultPostImage;
};

const loadPosts = async () => {
  loading.value = true;
  try {
    const response = await getPostList({
      page: currentPage.value - 1,
      size: pageSize.value
    });

    const list = response.content || [];
    posts.value = list.map((post) => ({
      ...post,
      imageList: parseImages(post.images)
    }));
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
  router.push(`/user/community/post/${postId}`);
};

const isOwnPost = (userId) => {
  return userStore.isLogin && Number(userStore.userInfo?.id) === Number(userId);
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
    await loadPosts();
  } catch (error) {
    if (error !== "cancel") {
      console.error("删除帖子失败:", error);
      ElMessage.error("删除帖子失败");
    }
  }
};

const truncateText = (text, length) => {
  if (!text) return "";
  return text.length > length ? text.slice(0, length) + "..." : text;
};

const formatDate = (dateString) => {
  if (!dateString) return "";
  const date = new Date(dateString);
  return date.toLocaleString("zh-CN");
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

.btn-create-post,
.btn-create-post-empty {
  padding: 10px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  text-decoration: none;
  border-radius: 4px;
  font-weight: 600;
}

.loading,
.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 8px;
  color: #999;
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
  align-items: center;
}

.user-avatar {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  object-fit: cover;
  background: #f0f0f0;
}

.user-name {
  margin: 0;
  font-size: 14px;
  color: #333;
  font-weight: 600;
}

.post-time {
  margin: 4px 0 0;
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
}

.post-title {
  margin: 0 0 8px;
  font-size: 16px;
  color: #333;
}

.post-text {
  margin: 0;
  color: #666;
  line-height: 1.6;
}

.post-images {
  margin-top: 12px;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  gap: 8px;
}

.post-image {
  width: 100%;
  height: 100px;
  border-radius: 4px;
  object-fit: cover;
}

.image-more {
  height: 100px;
  border-radius: 4px;
  background: rgba(0, 0, 0, 0.45);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
}

.post-stats {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #eee;
  color: #777;
  font-size: 13px;
  display: flex;
  gap: 20px;
}

.pagination-section {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}
</style>
