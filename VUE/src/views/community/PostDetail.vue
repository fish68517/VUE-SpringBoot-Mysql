<template>
  <div class="post-detail-page">
    <div class="container">
      <router-link to="/user/community" class="btn-back">← 返回列表</router-link>

      <div v-if="loading" class="loading">加载中...</div>

      <div v-else-if="!post" class="not-found">
        <p>帖子不存在或已被删除</p>
        <router-link to="/user/community" class="btn-back-home">返回社区</router-link>
      </div>

      <div v-else class="post-detail">
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
          <button v-if="isOwnPost" @click="deletePostAction" class="btn-delete">删除</button>
        </div>

        <div class="post-content">
          <h1 class="post-title">{{ post.title }}</h1>
          <p class="post-text">{{ post.content }}</p>
          <div v-if="post.imageList.length > 0" class="post-images">
            <img
              v-for="(image, index) in post.imageList"
              :key="index"
              :src="getImageUrl(image)"
              :alt="post.title"
              class="post-image"
              @error="handlePostImageError"
            />
          </div>
        </div>

        <div class="post-actions">
          <div class="post-stats" v-if="false">
            <span class="stat-item">浏览 {{ post.views || 0 }}</span>
            <span class="stat-item">评论 {{ replies.length }}</span>
            <span class="stat-item">点赞 {{ post.likes || 0 }}</span>
          </div>
          <button

            v-if="false"
            @click="toggleLikeAction"
            class="btn-like"
            :class="{ liked: isLiked }"
          >
            {{ isLiked ? "已赞" : "赞" }}
          </button>
        </div>

        <div class="comments-section">
          <h2 class="comments-title">评论 ({{ replies.length }})</h2>

          <div v-if="userStore.isLogin" class="comment-form">
            <textarea
              v-model="newComment"
              placeholder="发表评论..."
              class="comment-input"
              rows="3"
            ></textarea>
            <button @click="submitComment" class="btn-submit-comment" :disabled="!newComment.trim()">
              发布评论
            </button>
          </div>
          <div v-else class="login-prompt">
            <p>请 <router-link to="/login">登录</router-link> 后发表评论</p>
          </div>

          <div v-if="replies.length === 0" class="no-comments">
            <p>暂无评论，来发表第一条评论吧</p>
          </div>
          <div v-else class="comments-list">
            <div v-for="reply in replies" :key="reply.id" class="comment-item">
              <img
                :src="getAvatarUrl(reply.userAvatar)"
                :alt="reply.username || '用户'"
                class="comment-avatar"
                @error="handleAvatarError"
              />
              <div class="comment-content">
                <div class="comment-header">
                  <p class="comment-user">{{ reply.username || `用户${reply.userId}` }}</p>
                  <p class="comment-time">{{ formatDate(reply.createTime) }}</p>
                </div>
                <p class="comment-text">{{ reply.content }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from "vue";
import { useRouter, useRoute } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import { useUserStore } from "@/store/userStore";
import { getPostDetail, getPostReplies, createReply, deletePost, toggleLike } from "@/api/community";
import defaultImage from "@/assets/bg.jpg";

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

const post = ref(null);
const replies = ref([]);
const loading = ref(false);
const newComment = ref("");
const isLiked = ref(false);

const postId = computed(() => route.params.id);
const isOwnPost = computed(() => userStore.isLogin && Number(userStore.userInfo?.id) === Number(post.value?.userId));

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
  if (!src) return defaultImage;
  if (src.startsWith("http")) return src;
  return `http://localhost:8080/uploads/${src}`;
};

const getImageUrl = (src) => {
  if (!src) return defaultImage;
  if (src.startsWith("http")) return src;
  return `http://localhost:8080/uploads/${src}`;
};

const handleAvatarError = (event) => {
  const img = event?.target;
  if (!img || img.dataset.fallbackApplied === "1") return;
  img.dataset.fallbackApplied = "1";
  img.src = defaultImage;
};

const handlePostImageError = (event) => {
  const img = event?.target;
  if (!img || img.dataset.fallbackApplied === "1") return;
  img.dataset.fallbackApplied = "1";
  img.src = defaultImage;
};

const loadReplies = async () => {
  try {
    replies.value = await getPostReplies(postId.value);
  } catch (error) {
    console.error("加载评论失败:", error);
  }
};

const loadPostDetail = async () => {
  loading.value = true;
  try {
    const detail = await getPostDetail(postId.value);
    post.value = {
      ...detail,
      imageList: parseImages(detail.images)
    };
    isLiked.value = Boolean(detail.isLiked);
    await loadReplies();
  } catch (error) {
    console.error("加载帖子详情失败:", error);
    ElMessage.error("加载帖子详情失败");
  } finally {
    loading.value = false;
  }
};

const submitComment = async () => {
  if (!newComment.value.trim()) {
    ElMessage.warning("请输入评论内容");
    return;
  }

  try {
    await createReply({
      postId: postId.value,
      content: newComment.value,
      userId: userStore.userInfo.id
    });
    ElMessage.success("评论发布成功");
    newComment.value = "";
    await loadReplies();
  } catch (error) {
    console.error("发布评论失败:", error);
    ElMessage.error("发布评论失败");
  }
};

const toggleLikeAction = async () => {
  try {
    await toggleLike(postId.value);
    isLiked.value = !isLiked.value;
    post.value.likes = isLiked.value
      ? (post.value.likes || 0) + 1
      : Math.max(0, (post.value.likes || 0) - 1);
  } catch (error) {
    console.error("点赞失败:", error);
    ElMessage.error("点赞失败");
  }
};

const deletePostAction = async () => {
  try {
    await ElMessageBox.confirm("确定要删除这个帖子吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning"
    });

    await deletePost(postId.value);
    ElMessage.success("帖子已删除");
    router.push("/user/community");
  } catch (error) {
    if (error !== "cancel") {
      console.error("删除帖子失败:", error);
      ElMessage.error("删除帖子失败");
    }
  }
};

const formatDate = (dateString) => {
  if (!dateString) return "";
  return new Date(dateString).toLocaleString("zh-CN");
};

onMounted(() => {
  loadPostDetail();
});
</script>

<style scoped>
.post-detail-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 20px 0;
}

.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 20px;
}

.btn-back {
  display: inline-block;
  margin-bottom: 20px;
  color: #667eea;
  text-decoration: none;
}

.loading,
.not-found {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 8px;
  color: #999;
}

.btn-back-home {
  display: inline-block;
  margin-top: 12px;
  padding: 10px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  text-decoration: none;
  border-radius: 4px;
}

.post-detail {
  background: white;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.user-info {
  display: flex;
  gap: 12px;
  align-items: center;
}

.user-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  object-fit: cover;
}

.user-name {
  margin: 0;
  font-size: 15px;
  color: #333;
  font-weight: 600;
}

.post-time {
  margin: 4px 0 0;
  font-size: 12px;
  color: #999;
}

.btn-delete {
  padding: 8px 16px;
  background: #ff6b6b;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.post-title {
  margin: 0 0 15px;
  font-size: 24px;
  color: #333;
}

.post-text {
  margin: 0;
  font-size: 15px;
  color: #666;
  line-height: 1.8;
  white-space: pre-wrap;
}

.post-images {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 12px;
  margin-top: 15px;
}

.post-image {
  width: 100%;
  height: 150px;
  border-radius: 4px;
  object-fit: cover;
}

.post-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 0;
  border-top: 1px solid #eee;
  border-bottom: 1px solid #eee;
  margin: 20px 0 30px;
}

.post-stats {
  display: flex;
  gap: 20px;
  color: #777;
  font-size: 13px;
}

.btn-like {
  padding: 8px 16px;
  background: white;
  color: #667eea;
  border: 1px solid #667eea;
  border-radius: 4px;
  cursor: pointer;
}

.btn-like.liked {
  background: #ff6b6b;
  color: white;
  border-color: #ff6b6b;
}

.comments-title {
  margin: 0 0 20px;
}

.comment-form {
  margin-bottom: 20px;
  padding: 15px;
  background: #f9f9f9;
  border-radius: 4px;
}

.comment-input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  margin-bottom: 10px;
  resize: vertical;
}

.btn-submit-comment {
  padding: 8px 20px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.btn-submit-comment:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.login-prompt,
.no-comments {
  padding: 15px;
  background: #f9f9f9;
  border-radius: 4px;
  color: #666;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.comment-item {
  display: flex;
  gap: 12px;
  padding: 15px;
  background: #f9f9f9;
  border-radius: 4px;
}

.comment-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.comment-user {
  margin: 0;
  font-size: 13px;
  font-weight: 600;
}

.comment-time {
  margin: 0;
  font-size: 12px;
  color: #999;
}

.comment-text {
  margin: 0;
  color: #666;
  line-height: 1.6;
  white-space: pre-wrap;
}
</style>
