<template>
  <div class="knowledge-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>知识科普</h1>
      <p>学习壮族刺绣的非遗知识</p>
    </div>

    <!-- 分类筛选 -->
    <div class="filter-section">
      <div class="filter-group">
        <label>分类筛选：</label>
        <button
          v-for="cat in categories"
          :key="cat"
          class="filter-btn"
          :class="{ active: selectedCategory === cat }"
          @click="selectCategory(cat)"
        >
          {{ cat }}
        </button>
        <button
          class="filter-btn"
          :class="{ active: selectedCategory === '' }"
          @click="selectCategory('')"
        >
          全部
        </button>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="isLoading" class="loading-container">
      <div class="spinner"></div>
      <p>加载知识中...</p>
    </div>

    <!-- 知识列表 -->
    <div v-else-if="knowledge.length > 0" class="knowledge-container">
      <div class="knowledge-list">
        <router-link
          v-for="article in knowledge"
          :key="article.id"
          :to="`/knowledge/${article.id}`"
          class="knowledge-card"
        >
          <div class="knowledge-header">
            <h3 class="knowledge-title">{{ article.title }}</h3>
            <span class="knowledge-category">{{ article.category }}</span>
          </div>
          <p class="knowledge-excerpt">{{ article.content.substring(0, 150) }}...</p>
          <div class="knowledge-footer">
            <span class="knowledge-author">作者：{{ article.author }}</span>
            <span class="knowledge-views">👁️ {{ article.viewCount }}</span>
          </div>
        </router-link>
      </div>

      <!-- 分页 -->
      <Pagination
        :current-page="currentPage"
        :total-pages="totalPages"
        :total-items="totalItems"
        @update:current-page="handlePageChange"
      />
    </div>

    <!-- 空状态 -->
    <div v-else class="empty-state">
      <div class="empty-icon">📚</div>
      <p>暂无知识内容</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Pagination } from '../components'
import { KnowledgeService } from '../services'
import { useToast } from '../utils/useToast'

const knowledge = ref([])
const categories = ref([])
const selectedCategory = ref('')
const currentPage = ref(1)
const totalPages = ref(1)
const totalItems = ref(0)
const isLoading = ref(false)

const { error } = useToast()

// 加载分类列表
const loadCategories = async () => {
  try {
    const response = await KnowledgeService.getCategories()
    categories.value = response || []
  } catch (err) {
    console.error('Failed to load categories:', err)
  }
}

// 加载知识列表
const loadKnowledge = async () => {
  isLoading.value = true
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: 10,
    }

    // 如果选择了分类，添加到参数中
    if (selectedCategory.value) {
      params.category = selectedCategory.value
    }

    const response = await KnowledgeService.getKnowledge(params)
    const data = response

    knowledge.value = data.items || []
    totalPages.value = data.totalPages || 1
    totalItems.value = data.total || 0
  } catch (err) {
    console.error('Failed to load knowledge:', err)
    error('加载知识内容失败，请重试')
  } finally {
    isLoading.value = false
  }
}

// 选择分类
const selectCategory = (category) => {
  selectedCategory.value = category
  currentPage.value = 1 // 重置到第一页
  loadKnowledge()
}

// 处理分页变化
const handlePageChange = (newPage) => {
  currentPage.value = newPage
  loadKnowledge()
  // 滚动到顶部
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(() => {
  loadCategories()
  loadKnowledge()
})
</script>

<style scoped>
.knowledge-page {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
  padding: var(--spacing-lg);
  max-width: 1000px;
  margin: 0 auto;
}

/* 页面标题 */
.page-header {
  text-align: center;
  margin-bottom: var(--spacing-lg);
}

.page-header h1 {
  font-size: var(--font-size-2xl);
  color: var(--primary-color);
  margin-bottom: var(--spacing-sm);
  font-weight: 700;
}

.page-header p {
  font-size: var(--font-size-lg);
  color: var(--text-secondary);
}

/* 分类筛选 */
.filter-section {
  background-color: var(--bg-primary);
  padding: var(--spacing-lg);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-sm);
}

.filter-group {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  flex-wrap: wrap;
}

.filter-group label {
  font-weight: 600;
  color: var(--text-primary);
  white-space: nowrap;
}

.filter-btn {
  padding: var(--spacing-sm) var(--spacing-md);
  background-color: var(--bg-secondary);
  color: var(--text-primary);
  border: 2px solid var(--border-color);
  border-radius: var(--border-radius-md);
  cursor: pointer;
  font-size: var(--font-size-base);
  font-weight: 500;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.filter-btn:hover {
  border-color: var(--primary-color);
  color: var(--primary-color);
}

.filter-btn.active {
  background-color: var(--primary-color);
  color: white;
  border-color: var(--primary-color);
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

/* 知识列表 */
.knowledge-container {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
}

.knowledge-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.knowledge-card {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
  padding: var(--spacing-lg);
  background-color: var(--bg-primary);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-md);
  text-decoration: none;
  color: var(--text-primary);
  transition: all 0.3s ease;
  cursor: pointer;
  border-left: 4px solid var(--primary-color);
}

.knowledge-card:hover {
  transform: translateX(8px);
  box-shadow: var(--shadow-lg);
}

.knowledge-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: var(--spacing-md);
}

.knowledge-title {
  font-size: var(--font-size-lg);
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
  flex: 1;
  line-height: 1.4;
}

.knowledge-category {
  display: inline-block;
  padding: var(--spacing-xs) var(--spacing-sm);
  background-color: var(--primary-light);
  color: white;
  border-radius: var(--border-radius-sm);
  font-size: var(--font-size-sm);
  font-weight: 600;
  white-space: nowrap;
}

.knowledge-excerpt {
  margin: 0;
  color: var(--text-secondary);
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.knowledge-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: var(--font-size-sm);
  color: var(--text-light);
}

.knowledge-author {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
}

.knowledge-views {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
}

/* 空状态 */
.empty-state {
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

.empty-icon {
  font-size: 60px;
}

.empty-state p {
  font-size: var(--font-size-lg);
  color: var(--text-secondary);
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .knowledge-page {
    gap: var(--spacing-lg);
    padding: var(--spacing-md);
  }

  .page-header h1 {
    font-size: var(--font-size-xl);
  }

  .filter-group {
    gap: var(--spacing-sm);
  }

  .filter-btn {
    padding: var(--spacing-xs) var(--spacing-sm);
    font-size: var(--font-size-sm);
  }

  .knowledge-card {
    padding: var(--spacing-md);
  }

  .knowledge-header {
    flex-direction: column;
    gap: var(--spacing-sm);
  }

  .knowledge-title {
    font-size: var(--font-size-base);
  }

  .knowledge-footer {
    flex-direction: column;
    gap: var(--spacing-sm);
  }
}
</style>
