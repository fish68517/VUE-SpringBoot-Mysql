<template>
  <div class="artworks-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>作品展示</h1>
      <p>欣赏壮族刺绣的精美作品</p>
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
      <p>加载作品中...</p>
    </div>

    <!-- 作品列表（网格布局） -->
    <div v-else-if="artworks.length > 0" class="artworks-container">
      <div class="artworks-grid">
        <router-link
          v-for="artwork in artworks"
          :key="artwork.id"
          :to="`/artworks/${artwork.id}`"
          class="artwork-card"
        >
          <div class="artwork-image-wrapper">
            <img :src="artwork.imageUrl" :alt="artwork.title" class="artwork-image" />
            <div class="artwork-overlay">
              <span class="view-btn">查看详情</span>
            </div>
          </div>
          <div class="artwork-info">
            <h3 class="artwork-title">{{ artwork.title }}</h3>
            <p class="artwork-category">{{ artwork.category }}</p>
            <div class="artwork-stats">
              <span class="stat">👁️ {{ artwork.viewCount }}</span>
              <span class="stat">❤️ {{ artwork.collectCount }}</span>
            </div>
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
      <div class="empty-icon">🎨</div>
      <p>暂无作品</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Pagination } from '../components'
import { ArtworkService } from '../services'
import { useToast } from '../utils/useToast'

const artworks = ref([])
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
    const response = await ArtworkService.getCategories()
    categories.value = response.data || []
  } catch (err) {
    console.error('Failed to load categories:', err)
  }
}

// 加载作品列表
const loadArtworks = async () => {
  isLoading.value = true
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: 12,
    }

    // 如果选择了分类，添加到参数中
    if (selectedCategory.value) {
      params.category = selectedCategory.value
    }

    const response = await ArtworkService.getArtworks(params)
    const data = response.data

    artworks.value = data.content || []
    totalPages.value = data.totalPages || 1
    totalItems.value = data.total || 0
  } catch (err) {
    console.error('Failed to load artworks:', err)
    error('加载作品失败，请重试')
  } finally {
    isLoading.value = false
  }
}

// 选择分类
const selectCategory = (category) => {
  selectedCategory.value = category
  currentPage.value = 1 // 重置到第一页
  loadArtworks()
}

// 处理分页变化
const handlePageChange = (newPage) => {
  currentPage.value = newPage
  loadArtworks()
  // 滚动到顶部
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(() => {
  loadCategories()
  loadArtworks()
})
</script>

<style scoped>
.artworks-page {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
  padding: var(--spacing-lg);
  max-width: 1400px;
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

/* 作品网格 */
.artworks-container {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
}

.artworks-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: var(--spacing-lg);
}

.artwork-card {
  display: flex;
  flex-direction: column;
  background-color: var(--bg-primary);
  border-radius: var(--border-radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-md);
  text-decoration: none;
  color: var(--text-primary);
  transition: all 0.3s ease;
  cursor: pointer;
}

.artwork-card:hover {
  transform: translateY(-8px);
  box-shadow: var(--shadow-lg);
}

.artwork-image-wrapper {
  position: relative;
  width: 100%;
  height: 200px;
  overflow: hidden;
  background-color: var(--bg-secondary);
}

.artwork-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.artwork-card:hover .artwork-image {
  transform: scale(1.05);
}

.artwork-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.artwork-card:hover .artwork-overlay {
  opacity: 1;
}

.view-btn {
  padding: var(--spacing-md) var(--spacing-lg);
  background-color: var(--primary-color);
  color: white;
  border-radius: var(--border-radius-md);
  font-weight: 600;
  font-size: var(--font-size-base);
}

.artwork-info {
  padding: var(--spacing-lg);
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
  flex: 1;
}

.artwork-title {
  font-size: var(--font-size-lg);
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.artwork-category {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  margin: 0;
}

.artwork-stats {
  display: flex;
  gap: var(--spacing-lg);
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
}

.stat {
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
  .artworks-page {
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

  .artworks-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    gap: var(--spacing-md);
  }

  .artwork-image-wrapper {
    height: 150px;
  }

  .artwork-info {
    padding: var(--spacing-md);
    gap: var(--spacing-sm);
  }

  .artwork-title {
    font-size: var(--font-size-base);
  }
}
</style>
