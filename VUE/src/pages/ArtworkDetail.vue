<template>
  <div class="artwork-detail-page">
    <!-- 返回按钮 -->
    <router-link to="/artworks" class="back-link">← 返回作品列表</router-link>

    <!-- 加载状态 -->
    <div v-if="isLoading" class="loading-container">
      <div class="spinner"></div>
      <p>加载作品详情中...</p>
    </div>

    <!-- 作品详情内容 -->
    <div v-else-if="artwork" class="detail-container">
      <!-- 作品图片和基本信息 -->
      <div class="artwork-main">
        <div class="artwork-image-section">
          <img :src="artwork.imageUrl" :alt="artwork.title" class="artwork-image" />
          <button
            class="collect-btn"
            :class="{ collected: isCollected }"
            @click="toggleCollect"
            :disabled="!isLoggedIn"
            :title="isLoggedIn ? (isCollected ? '取消收藏' : '收藏作品') : '请先登录'"
          >
            {{ isCollected ? '❤️ 已收藏' : '🤍 收藏' }}
          </button>
        </div>

        <div class="artwork-info-section">
          <h1 class="artwork-title">{{ artwork.title }}</h1>

          <div class="artwork-meta">
            <div class="meta-item">
              <span class="meta-label">分类：</span>
              <span class="meta-value">{{ artwork.category }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">创作者：</span>
              <span class="meta-value">{{ artwork.creator }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">刺绣技法：</span>
              <span class="meta-value">{{ artwork.technique }}</span>
            </div>
          </div>

          <div class="artwork-stats">
            <div class="stat-item">
              <span class="stat-icon">👁️</span>
              <span class="stat-text">{{ artwork.viewCount }} 次浏览</span>
            </div>
            <div class="stat-item">
              <span class="stat-icon">❤️</span>
              <span class="stat-text">{{ artwork.collectCount }} 人收藏</span>
            </div>
          </div>

          <div class="artwork-description">
            <h3>作品描述</h3>
            <p>{{ artwork.description }}</p>
          </div>
        </div>
      </div>

      <!-- 评论区 -->
      <div class="comments-section">
        <h2>评论区</h2>

        <!-- 发布评论表单 -->
        <div v-if="isLoggedIn" class="comment-form">
          <textarea
            v-model="newComment"
            placeholder="分享你的想法..."
            class="comment-input"
            rows="4"
          ></textarea>
          <button
            @click="submitComment"
            class="submit-btn"
            :disabled="!newComment.trim() || isSubmittingComment"
          >
            {{ isSubmittingComment ? '发布中...' : '发布评论' }}
          </button>
        </div>

        <!-- 未登录提示 -->
        <div v-else class="login-prompt">
          <p>请 <router-link to="/login">登录</router-link> 后发表评论</p>
        </div>

        <!-- 评论列表 -->
        <div class="comments-list">
          <div v-if="comments.length > 0">
            <div
              v-for="comment in comments"
              :key="comment.id"
              class="comment-item"
            >
              <div class="comment-header">
                <span class="comment-author">{{ comment.userId }}</span>
                <span class="comment-date">{{ formatDate(comment.createdAt) }}</span>
              </div>
              <p class="comment-content">{{ comment.content }}</p>
            </div>
          </div>
          <div v-else class="no-comments">
            <p>暂无评论，来发表第一条评论吧！</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 错误状态 -->
    <div v-else class="error-state">
      <div class="error-icon">⚠️</div>
      <p>作品不存在或已被删除</p>
      <router-link to="/artworks" class="back-btn">返回作品列表</router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ArtworkService, CommentService } from '../services'
import { useAuthStore } from '../stores/authStore'
import { useToast } from '../utils/useToast'

const route = useRoute()
const authStore = useAuthStore()

const artwork = ref(null)
const comments = ref([])
const newComment = ref('')
const isLoading = ref(false)
const isSubmittingComment = ref(false)
const isCollected = ref(false)

const { success, error } = useToast()
const isLoggedIn = authStore.isLoggedIn

// 加载作品详情
const loadArtworkDetail = async () => {
  isLoading.value = true
  try {
    const artworkId = route.params.id
    const response = await ArtworkService.getArtworkDetail(artworkId)
    artwork.value = response.data

    // 记录浏览
    const userId = authStore.user?.id
    await ArtworkService.recordView(artworkId, userId)

    // 加载评论
    await loadComments(artworkId)
  } catch (err) {
    console.error('Failed to load artwork detail:', err)
    error('加载作品详情失败')
  } finally {
    isLoading.value = false
  }
}

// 加载评论列表
const loadComments = async (artworkId) => {
  try {
    const response = await CommentService.getComments({
      artworkId: artworkId,
      pageNum: 1,
      pageSize: 20,
    })
    comments.value = response.data.content || []
  } catch (err) {
    console.error('Failed to load comments:', err)
  }
}

// 提交评论
const submitComment = async () => {
  if (!newComment.value.trim()) {
    error('评论内容不能为空')
    return
  }

  isSubmittingComment.value = true
  try {
    const commentData = {
      content: newComment.value,
      userId: authStore.user.id,
      artworkId: artwork.value.id,
    }

    await CommentService.postComment(commentData)
    success('评论发布成功')
    newComment.value = ''

    // 重新加载评论列表
    await loadComments(artwork.value.id)
  } catch (err) {
    console.error('Failed to submit comment:', err)
    error('评论发布失败，请重试')
  } finally {
    isSubmittingComment.value = false
  }
}

// 切换收藏状态
const toggleCollect = async () => {
  if (!isLoggedIn) {
    error('请先登录')
    return
  }

  try {
    if (isCollected.value) {
      // 取消收藏
      await ArtworkService.uncollectArtwork(artwork.value.id, authStore.user.id)
      isCollected.value = false
      success('已取消收藏')
    } else {
      // 收藏
      await ArtworkService.collectArtwork(artwork.value.id, authStore.user.id)
      isCollected.value = true
      success('收藏成功')
    }

    // 重新加载作品详情以更新收藏数
    await loadArtworkDetail()
  } catch (err) {
    console.error('Failed to toggle collect:', err)
    error('操作失败，请重试')
  }
}

// 格式化日期
const formatDate = (dateString) => {
  const date = new Date(dateString)
  const now = new Date()
  const diffTime = Math.abs(now - date)
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))

  if (diffDays === 0) {
    return '今天'
  } else if (diffDays === 1) {
    return '昨天'
  } else if (diffDays < 7) {
    return `${diffDays}天前`
  } else {
    return date.toLocaleDateString('zh-CN')
  }
}

onMounted(() => {
  loadArtworkDetail()
})
</script>

<style scoped>
.artwork-detail-page {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
  padding: var(--spacing-lg);
  max-width: 1200px;
  margin: 0 auto;
}

/* 返回链接 */
.back-link {
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-sm);
  color: var(--primary-color);
  text-decoration: none;
  font-weight: 500;
  transition: all 0.3s ease;
  width: fit-content;
}

.back-link:hover {
  gap: var(--spacing-md);
}

/* 加载状态 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-2xl);
  gap: var(--spacing-lg);
}

.spinner {
  width: 50px;
  height: 50px;
  border: 4px solid var(--border-color);
  border-top-color: var(--primary-color);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading-container p {
  color: var(--text-secondary);
  font-size: var(--font-size-lg);
}

/* 详情容器 */
.detail-container {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
}

/* 作品主要内容 */
.artwork-main {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--spacing-xl);
  background-color: var(--bg-primary);
  padding: var(--spacing-xl);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-md);
}

.artwork-image-section {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.artwork-image {
  width: 100%;
  height: auto;
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-md);
  object-fit: cover;
}

.collect-btn {
  padding: var(--spacing-md) var(--spacing-lg);
  background-color: var(--bg-secondary);
  color: var(--text-primary);
  border: 2px solid var(--border-color);
  border-radius: var(--border-radius-md);
  font-size: var(--font-size-lg);
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.collect-btn:hover:not(:disabled) {
  background-color: var(--primary-light);
  color: white;
  border-color: var(--primary-light);
}

.collect-btn.collected {
  background-color: var(--primary-color);
  color: white;
  border-color: var(--primary-color);
}

.collect-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.artwork-info-section {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.artwork-title {
  font-size: var(--font-size-2xl);
  color: var(--text-primary);
  margin: 0;
  font-weight: 700;
}

.artwork-meta {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
  padding: var(--spacing-lg);
  background-color: var(--bg-secondary);
  border-radius: var(--border-radius-md);
}

.meta-item {
  display: flex;
  gap: var(--spacing-md);
}

.meta-label {
  font-weight: 600;
  color: var(--text-secondary);
  min-width: 80px;
}

.meta-value {
  color: var(--text-primary);
}

.artwork-stats {
  display: flex;
  gap: var(--spacing-lg);
}

.stat-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-md) var(--spacing-lg);
  background: linear-gradient(135deg, var(--primary-light) 0%, var(--primary-color) 100%);
  color: white;
  border-radius: var(--border-radius-md);
  font-weight: 600;
}

.stat-icon {
  font-size: 20px;
}

.artwork-description {
  padding: var(--spacing-lg);
  background-color: var(--bg-secondary);
  border-radius: var(--border-radius-md);
}

.artwork-description h3 {
  margin: 0 0 var(--spacing-md) 0;
  color: var(--text-primary);
  font-weight: 600;
}

.artwork-description p {
  margin: 0;
  color: var(--text-secondary);
  line-height: 1.8;
}

/* 评论区 */
.comments-section {
  background-color: var(--bg-primary);
  padding: var(--spacing-xl);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-md);
}

.comments-section h2 {
  margin: 0 0 var(--spacing-lg) 0;
  color: var(--text-primary);
  font-size: var(--font-size-xl);
  font-weight: 700;
}

/* 评论表单 */
.comment-form {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-xl);
  padding: var(--spacing-lg);
  background-color: var(--bg-secondary);
  border-radius: var(--border-radius-md);
}

.comment-input {
  padding: var(--spacing-md);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-md);
  font-family: inherit;
  font-size: var(--font-size-base);
  color: var(--text-primary);
  background-color: var(--bg-primary);
  resize: vertical;
}

.comment-input:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(0, 102, 204, 0.1);
}

.submit-btn {
  padding: var(--spacing-md) var(--spacing-lg);
  background-color: var(--primary-color);
  color: white;
  border: none;
  border-radius: var(--border-radius-md);
  font-size: var(--font-size-base);
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.submit-btn:hover:not(:disabled) {
  background-color: var(--primary-light);
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 登录提示 */
.login-prompt {
  padding: var(--spacing-lg);
  background-color: var(--bg-secondary);
  border-radius: var(--border-radius-md);
  text-align: center;
  margin-bottom: var(--spacing-xl);
}

.login-prompt p {
  margin: 0;
  color: var(--text-secondary);
}

.login-prompt a {
  color: var(--primary-color);
  text-decoration: none;
  font-weight: 600;
}

.login-prompt a:hover {
  text-decoration: underline;
}

/* 评论列表 */
.comments-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.comment-item {
  padding: var(--spacing-lg);
  background-color: var(--bg-secondary);
  border-radius: var(--border-radius-md);
  border-left: 4px solid var(--primary-color);
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-md);
}

.comment-author {
  font-weight: 600;
  color: var(--text-primary);
}

.comment-date {
  font-size: var(--font-size-sm);
  color: var(--text-light);
}

.comment-content {
  margin: 0;
  color: var(--text-secondary);
  line-height: 1.6;
}

.no-comments {
  padding: var(--spacing-lg);
  text-align: center;
  color: var(--text-light);
}

/* 错误状态 */
.error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-2xl);
  gap: var(--spacing-lg);
  background-color: var(--bg-primary);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-sm);
}

.error-icon {
  font-size: 60px;
}

.error-state p {
  font-size: var(--font-size-lg);
  color: var(--text-secondary);
  margin: 0;
}

.back-btn {
  padding: var(--spacing-md) var(--spacing-lg);
  background-color: var(--primary-color);
  color: white;
  border-radius: var(--border-radius-md);
  text-decoration: none;
  font-weight: 600;
  transition: all 0.3s ease;
}

.back-btn:hover {
  background-color: var(--primary-light);
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .artwork-detail-page {
    gap: var(--spacing-lg);
    padding: var(--spacing-md);
  }

  .artwork-main {
    grid-template-columns: 1fr;
    gap: var(--spacing-lg);
    padding: var(--spacing-lg);
  }

  .artwork-title {
    font-size: var(--font-size-xl);
  }

  .artwork-stats {
    flex-direction: column;
  }

  .stat-item {
    justify-content: center;
  }

  .comments-section {
    padding: var(--spacing-lg);
  }

  .comment-form {
    padding: var(--spacing-md);
  }
}
</style>
