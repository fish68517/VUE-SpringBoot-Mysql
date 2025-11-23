<template>
  <div class="post-detail-page">
    <div class="container">
      <!-- 返回按钮 -->
      <router-link to="/user/community" class="btn-back">← 返回列表</router-link>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading">加载中...</div>

      <!-- 帖子不存在 -->
      <div v-else-if="!post" class="not-found">
        <p>帖子不存在或已被删除</p>
        <router-link to="/uers/community" class="btn-back-home">返回社区</router-link>
      </div>

      <!-- 帖子详情 -->
      <div v-else class="post-detail">
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
            v-if="isOwnPost"
            @click="deletePostAction"
            class="btn-delete"
          >
            删除
          </button>
        </div>

        <!-- 帖子内容 -->
        <div class="post-content">
          <h1 class="post-title">{{ post.title }}</h1>
          <p class="post-text">{{ post.content }}</p>
          <div v-if="post.images && post.images.length > 0" class="post-images">
            <img
              v-for="(image, index) in post.images"
              :key="index"
              :src="image"
              :alt="post.title"
              class="post-image"
            />
          </div>
        </div>

        <!-- 帖子统计和操作 -->
        <div class="post-actions">
          <div class="post-stats">
            <span class="stat-item">
              <i class="icon">👁</i>
              {{ post.views || 0 }}
            </span>
            <span class="stat-item">
              <i class="icon">💬</i>
              {{ replies.length }}
            </span>
            <span class="stat-item">
              <i class="icon">❤</i>
              {{ post.likes || 0 }}
            </span>
          </div>
          <button
            v-if="userStore.isLogin"
            @click="toggleLikeAction"
            class="btn-like"
            :class="{ liked: isLiked }"
          >
            {{ isLiked ? "已赞" : "赞" }}
          </button>
        </div>

        <!-- 评论区 -->
        <div class="comments-section">
          <h2 class="comments-title">评论 ({{ replies.length }})</h2>

          <!-- 发布评论表单 -->
          <div v-if="userStore.isLogin" class="comment-form">
            <textarea
              v-model="newComment"
              placeholder="发表评论..."
              class="comment-input"
              rows="3"
            ></textarea>
            <button
              @click="submitComment"
              class="btn-submit-comment"
              :disabled="!newComment.trim()"
            >
              发布评论
            </button>
          </div>
          <div v-else class="login-prompt">
            <p>请 <router-link to="/login">登录</router-link> 后发表评论</p>
          </div>

          <!-- 评论列表 -->
          <div v-if="replies.length === 0" class="no-comments">
            <p>暂无评论，来发表第一条评论吧</p>
          </div>
          <div v-else class="comments-list">
            <div v-for="reply in replies" :key="reply.id" class="comment-item">
              <img :src="reply.userAvatar || '/default-avatar.png'" :alt="reply.userName" class="comment-avatar" />
              <div class="comment-content">
                <div class="comment-header">
                  <p class="comment-user">{{ reply.userName }}</p>
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
import { ref, computed, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import { useUserStore } from "@/store/userStore";
import {
  getPostDetail,
  getPostReplies,
  createReply,
  deletePost,
  toggleLike
} from "@/api/community";

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

const post = ref(null);
const replies = ref([]);
const loading = ref(false);
const newComment = ref("");
const isLiked = ref(false);

const postId = computed(() => route.params.id);

const isOwnPost = computed(() => {
  return userStore.isLogin && userStore.userInfo?.id === post.value?.userId;
});

const loadPostDetail = async () => {
  loading.value = true;
  try {
    post.value = await getPostDetail(postId.value);

    if (typeof post.value.images === 'string') {
        try {
          post.value.images = JSON.parse(post.value.images);
        } catch (e) {
          console.error('解析图片失败', e);
          post.value.images = [];
        }
    }
  
    await loadReplies();
  } catch (error) {
    console.error("加载帖子详情失败:", error);
    ElMessage.error("加载帖子详情失败");
  } finally {
    loading.value = false;
  }
};

const loadReplies = async () => {
  try {
    replies.value = await getPostReplies(postId.value);
  } catch (error) {
    console.error("加载评论失败:", error);
  }
};

const submitComment = async () => {
  if (!newComment.value.trim()) {
    ElMessage.warning("请输入评论内容");
    return;
  }

  try {
    // 需要给 createReply 增加 UserId
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
    if (isLiked.value) {
      post.value.likes = (post.value.likes || 0) + 1;
    } else {
      post.value.likes = Math.max(0, (post.value.likes || 0) - 1);
    }
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
  const date = new Date(dateString);
  return date.toLocaleDateString("zh-CN") + " " + date.toLocaleTimeString("zh-CN");
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
  font-size: 14px;
  transition: all 0.3s ease;
}

.btn-back:hover {
  color: #764ba2;
}

.loading,
.not-found {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 8px;
  color: #999;
  font-size: 16px;
}

.not-found {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.btn-back-home {
  display: inline-block;
  padding: 10px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  text-decoration: none;
  border-radius: 4px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.btn-back-home:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
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
  align-items: flex-start;
}

.user-avatar {
  width: 50px;
  height: 50px;
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
  font-size: 15px;
  color: #333;
  font-weight: 600;
}

.post-time {
  margin: 0;
  font-size: 12px;
  color: #999;
}

.btn-delete {
  padding: 8px 16px;
  background: #ff6b6b;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-delete:hover {
  background: #ff5252;
}

.post-content {
  margin-bottom: 20px;
}

.post-title {
  margin: 0 0 15px 0;
  font-size: 24px;
  color: #333;
  font-weight: 600;
}

.post-text {
  margin: 0 0 15px 0;
  font-size: 15px;
  color: #666;
  line-height: 1.8;
  white-space: pre-wrap;
  word-break: break-word;
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
  background: #f0f0f0;
}

.post-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 0;
  border-top: 1px solid #eee;
  border-bottom: 1px solid #eee;
  margin-bottom: 30px;
}

.post-stats {
  display: flex;
  gap: 20px;
  font-size: 13px;
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

.btn-like {
  padding: 8px 16px;
  background: white;
  color: #667eea;
  border: 1px solid #667eea;
  border-radius: 4px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-like:hover {
  background: #667eea;
  color: white;
}

.btn-like.liked {
  background: #ff6b6b;
  color: white;
  border-color: #ff6b6b;
}

.comments-section {
  margin-top: 30px;
}

.comments-title {
  margin: 0 0 20px 0;
  font-size: 18px;
  color: #333;
  font-weight: 600;
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
  font-size: 14px;
  font-family: inherit;
  resize: vertical;
  margin-bottom: 10px;
}

.comment-input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
}

.btn-submit-comment {
  padding: 8px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-submit-comment:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.4);
}

.btn-submit-comment:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.login-prompt {
  padding: 15px;
  background: #f9f9f9;
  border-radius: 4px;
  text-align: center;
  color: #666;
  font-size: 14px;
}

.login-prompt a {
  color: #667eea;
  text-decoration: none;
  font-weight: 600;
}

.login-prompt a:hover {
  color: #764ba2;
}

.no-comments {
  text-align: center;
  padding: 30px 20px;
  color: #999;
  font-size: 14px;
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
  background: #f0f0f0;
  flex-shrink: 0;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.comment-user {
  margin: 0;
  font-size: 13px;
  color: #333;
  font-weight: 600;
}

.comment-time {
  margin: 0;
  font-size: 12px;
  color: #999;
}

.comment-text {
  margin: 0;
  font-size: 13px;
  color: #666;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
}

@media (max-width: 768px) {
  .post-detail {
    padding: 20px;
  }

  .post-header {
    flex-direction: column;
    gap: 15px;
  }

  .post-title {
    font-size: 20px;
  }

  .post-images {
    grid-template-columns: repeat(2, 1fr);
  }

  .post-actions {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }

  .comment-item {
    gap: 10px;
  }

  .comment-avatar {
    width: 35px;
    height: 35px;
  }
}
</style>
