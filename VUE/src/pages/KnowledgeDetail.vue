<template>
  <div class="knowledge-detail-page">
    <!-- 返回按钮 -->
    <router-link to="/knowledge" class="back-link">← 返回知识列表</router-link>

    <!-- 加载状态 -->
    <div v-if="isLoading" class="loading-container">
      <div class="spinner"></div>
      <p>加载知识详情中...</p>
    </div>

    <!-- 知识详情内容 -->
    <div v-else-if="article" class="detail-container">
      <!-- 文章头部 -->
      <div class="article-header">
        <h1 class="article-title">{{ article.title }}</h1>
        <div class="article-meta">
          <span class="meta-item">
            <span class="meta-label">分类：</span>
            <span class="meta-value">{{ article.category }}</span>
          </span>
          <span class="meta-item">
            <span class="meta-label">作者：</span>
            <span class="meta-value">{{ article.author }}</span>
          </span>
          <span class="meta-item">
            <span class="meta-label">浏览：</span>
            <span class="meta-value">👁️ {{ article.viewCount }}</span>
          </span>
        </div>
      </div>

      <!-- 文章内容 -->
      <div class="article-content">
        <div class="content-text">{{ article.content }}</div>
      </div>

      <!-- 文章底部信息 -->
      <div class="article-footer">
        <div class="footer-info">
          <span class="info-item">发布时间：{{ formatDate(article.createdAt) }}</span>
          <span class="info-item">最后更新：{{ formatDate(article.updatedAt) }}</span>
        </div>
      </div>

      <!-- 相关知识推荐 -->
      <div class="related-section" v-if="false">
        <h2>相关知识推荐</h2>
        <div v-if="relatedArticles.length > 0" class="related-list">
          <router-link
            v-for="related in relatedArticles"
            :key="related.id"
            :to="`/knowledge/${related.id}`"
            class="related-card"
          >
            <h4>{{ related.title }}</h4>
            <p>{{ related.content.substring(0, 100) }}...</p>
          </router-link>
        </div>
        <div v-else class="no-related">
          <p>暂无相关知识</p>
        </div>
      </div>
    </div>

    <!-- 错误状态 -->
    <div v-else class="error-state">
      <div class="error-icon">⚠️</div>
      <p>知识内容不存在或已被删除</p>
      <router-link to="/knowledge" class="back-btn">返回知识列表</router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { KnowledgeService } from '../services'
import { useAuthStore } from '../stores/authStore'
import { useToast } from '../utils/useToast'

const route = useRoute()
const authStore = useAuthStore()

const article = ref(null)
const relatedArticles = ref([])
const isLoading = ref(false)

const { error } = useToast()

// 加载知识详情
const loadKnowledgeDetail = async () => {
  isLoading.value = true
  try {
    const knowledgeId = route.params.id
    const response = await KnowledgeService.getKnowledgeDetail(knowledgeId)
    article.value = response

    // 记录浏览
    const userId = authStore.user?.id
    await KnowledgeService.recordView(knowledgeId, userId)

    // 加载相关知识（同分类的其他文章）
    await loadRelatedArticles(article.value.category, knowledgeId)
  } catch (err) {
    console.error('Failed to load knowledge detail:', err)
    error('加载知识详情失败')
  } finally {
    isLoading.value = false
  }
}

// 加载相关知识
const loadRelatedArticles = async (category, currentId) => {
  try {
    const response = await KnowledgeService.getKnowledge({
      category: category,
      pageNum: 1,
      pageSize: 3,
    })
    // 过滤掉当前文章
    relatedArticles.value = (response.items || []).filter(
      (article) => article.id !== currentId
    )
  } catch (err) {
    console.error('Failed to load related articles:', err)
  }
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '未知'
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  })
}

onMounted(() => {
  loadKnowledgeDetail()
})
</script>

<style scoped>
.knowledge-detail-page {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
  padding: var(--spacing-lg);
  max-width: 900px;
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

/* 文章头部 */
.article-header {
  background-color: var(--bg-primary);
  padding: var(--spacing-xl);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-md);
  border-left: 4px solid var(--primary-color);
}

.article-title {
  font-size: var(--font-size-2xl);
  color: var(--text-primary);
  margin: 0 0 var(--spacing-lg) 0;
  font-weight: 700;
  line-height: 1.4;
}

.article-meta {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-lg);
  font-size: var(--font-size-sm);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.meta-label {
  font-weight: 600;
  color: var(--text-secondary);
}

.meta-value {
  color: var(--text-primary);
}

/* 文章内容 */
.article-content {
  background-color: var(--bg-primary);
  padding: var(--spacing-xl);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-md);
}

.content-text {
  color: var(--text-secondary);
  line-height: 1.8;
  font-size: var(--font-size-base);
  white-space: pre-wrap;
  word-break: break-word;
}

/* 文章底部 */
.article-footer {
  background-color: var(--bg-primary);
  padding: var(--spacing-lg);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-sm);
}

.footer-info {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-lg);
  font-size: var(--font-size-sm);
  color: var(--text-light);
}

.info-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

/* 相关知识推荐 */
.related-section {
  background-color: var(--bg-primary);
  padding: var(--spacing-xl);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-md);
}

.related-section h2 {
  margin: 0 0 var(--spacing-lg) 0;
  color: var(--text-primary);
  font-size: var(--font-size-xl);
  font-weight: 700;
}

.related-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: var(--spacing-lg);
}

.related-card {
  padding: var(--spacing-lg);
  background-color: var(--bg-secondary);
  border-radius: var(--border-radius-md);
  border-left: 3px solid var(--primary-light);
  text-decoration: none;
  color: var(--text-primary);
  transition: all 0.3s ease;
  cursor: pointer;
}

.related-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-md);
  border-left-color: var(--primary-color);
}

.related-card h4 {
  margin: 0 0 var(--spacing-sm) 0;
  font-size: var(--font-size-base);
  font-weight: 600;
  color: var(--text-primary);
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.related-card p {
  margin: 0;
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.no-related {
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
  .knowledge-detail-page {
    gap: var(--spacing-lg);
    padding: var(--spacing-md);
  }

  .article-title {
    font-size: var(--font-size-xl);
  }

  .article-meta {
    gap: var(--spacing-md);
    font-size: var(--font-size-xs);
  }

  .article-header,
  .article-content,
  .article-footer,
  .related-section {
    padding: var(--spacing-lg);
  }

  .related-list {
    grid-template-columns: 1fr;
  }

  .content-text {
    font-size: var(--font-size-sm);
  }
}
</style>
