<template>
  <div class="knowledge-page">
    <section class="hero-section">
      <div class="hero-main">
        <div class="hero-content">
          <span class="hero-kicker">{{ copy.heroKicker }}</span>
          <h1>{{ copy.title }}</h1>
          <p>{{ copy.subtitle }}</p>
        </div>

        <div class="hero-stats">
          <div class="hero-stat-card">
            <span class="hero-stat-label">{{ copy.totalArticles }}</span>
            <strong>{{ totalItems || knowledge.length }}</strong>
          </div>
          <div class="hero-stat-card">
            <span class="hero-stat-label">{{ copy.totalCategories }}</span>
            <strong>{{ categories.length }}</strong>
          </div>
          <div class="hero-stat-card">
            <span class="hero-stat-label">{{ copy.currentFilter }}</span>
            <strong>{{ currentCategoryName }}</strong>
          </div>
        </div>
      </div>

      <div class="hero-visual">
        <img :src="knowledgeHero" :alt="copy.title" class="hero-visual-image" />
      </div>
    </section>

    <section class="filter-panel">
      <div class="filter-header">
        <div>
          <h2>{{ copy.filterTitle }}</h2>
          <p>{{ copy.filterSubtitle }}</p>
        </div>
      </div>

      <div class="filter-chips">
        <button
          class="filter-btn"
          :class="{ active: selectedCategory === '' }"
          @click="selectCategory('')"
        >
          {{ copy.allCategories }}
        </button>

        <button
          v-for="cat in categories"
          :key="cat"
          class="filter-btn"
          :class="{ active: selectedCategory === cat }"
          @click="selectCategory(cat)"
        >
          {{ cat }}
        </button>
      </div>
    </section>

    <div v-if="isLoading" class="loading-container">
      <div class="spinner"></div>
      <p>{{ copy.loading }}</p>
    </div>

    <div v-else-if="knowledge.length > 0" class="knowledge-container">
      <div class="knowledge-list">
        <router-link
          v-for="article in knowledge"
          :key="article.id"
          :to="`/knowledge/${article.id}`"
          class="knowledge-card"
        >
          <div class="knowledge-card-top">
            <span class="knowledge-category">{{ article.category || copy.defaultCategory }}</span>
            <span class="knowledge-views">{{ copy.viewsPrefix }} {{ article.viewCount || 0 }}</span>
          </div>

          <div class="knowledge-main">
            <h3 class="knowledge-title">{{ article.title }}</h3>
            <p class="knowledge-excerpt">{{ getExcerpt(article.content) }}</p>
          </div>

          <div class="knowledge-footer">
            <div class="knowledge-meta">
              <span>{{ copy.authorPrefix }} {{ article.author || copy.defaultAuthor }}</span>
              <span class="meta-divider"></span>
              <span>{{ getReadingTime(article.content) }}</span>
            </div>
            <span class="knowledge-link">{{ copy.readMore }}</span>
          </div>
        </router-link>
      </div>

      <Pagination
        :current-page="currentPage"
        :total-pages="totalPages"
        :total-items="totalItems"
        @update:current-page="handlePageChange"
      />
    </div>

    <div v-else class="empty-state">
      <div class="empty-icon">{{ copy.emptyIcon }}</div>
      <h3>{{ copy.emptyTitle }}</h3>
      <p>{{ copy.emptyText }}</p>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { Pagination } from '../components'
import knowledgeHero from '../assets/Knowledge.png'
import { KnowledgeService } from '../services'
import { useToast } from '../utils/useToast'

const copy = {
  title: '\u77e5\u8bc6\u79d1\u666e',
  subtitle:
    '\u4ece\u5206\u7c7b\u77e5\u8bc6\u3001\u523a\u7ee3\u6280\u6cd5\u5230\u6587\u5316\u80cc\u666f\uff0c\u96c6\u4e2d\u4e86\u89e3\u6587\u5c71\u523a\u7ee3\u7684\u5173\u952e\u5185\u5bb9\u3002',
  heroKicker: '\u975e\u9057\u77e5\u8bc6\u9605\u8bfb',
  totalArticles: '\u6587\u7ae0\u603b\u6570',
  totalCategories: '\u5206\u7c7b\u6570\u91cf',
  currentFilter: '\u5f53\u524d\u7b5b\u9009',
  filterTitle: '\u5206\u7c7b\u7b5b\u9009',
  filterSubtitle:
    '\u6309\u4e3b\u9898\u5feb\u901f\u6d4f\u89c8\uff0c\u627e\u5230\u4f60\u60f3\u4e86\u89e3\u7684\u523a\u7ee3\u77e5\u8bc6\u3002',
  allCategories: '\u5168\u90e8',
  loading: '\u6b63\u5728\u52a0\u8f7d\u77e5\u8bc6\u5185\u5bb9...',
  defaultCategory: '\u672a\u5206\u7c7b',
  defaultAuthor: '\u672a\u77e5',
  authorPrefix: '\u4f5c\u8005',
  viewsPrefix: '\u6d4f\u89c8',
  readMore: '\u67e5\u770b\u8be6\u60c5',
  emptyIcon: '\u77e5',
  emptyTitle: '\u6682\u65e0\u77e5\u8bc6\u5185\u5bb9',
  emptyText: '\u53ef\u4ee5\u5207\u6362\u5206\u7c7b\u518d\u8bd5\u4e00\u4e0b\uff0c\u6216\u7a0d\u540e\u518d\u6765\u67e5\u770b\u3002',
  loadError: '\u52a0\u8f7d\u77e5\u8bc6\u5185\u5bb9\u5931\u8d25\uff0c\u8bf7\u91cd\u8bd5',
  allFilterName: '\u5168\u90e8',
  minutesReadSuffix: '\u5206\u949f\u9605\u8bfb',
}

const knowledge = ref([])
const categories = ref([])
const selectedCategory = ref('')
const currentPage = ref(1)
const totalPages = ref(1)
const totalItems = ref(0)
const isLoading = ref(false)

const { error } = useToast()

const currentCategoryName = computed(() => {
  return selectedCategory.value || copy.allFilterName
})

const loadCategories = async () => {
  try {
    const response = await KnowledgeService.getCategories()
    categories.value = response || []
  } catch (err) {
    console.error('Failed to load categories:', err)
  }
}

const loadKnowledge = async () => {
  isLoading.value = true

  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: 10,
    }

    if (selectedCategory.value) {
      params.category = selectedCategory.value
    }

    const data = await KnowledgeService.getKnowledge(params)
    knowledge.value = data.items || []
    totalPages.value = data.totalPages || 1
    totalItems.value = data.total || 0
  } catch (err) {
    console.error('Failed to load knowledge:', err)
    error(copy.loadError)
  } finally {
    isLoading.value = false
  }
}

const selectCategory = (category) => {
  selectedCategory.value = category
  currentPage.value = 1
  loadKnowledge()
}

const handlePageChange = (newPage) => {
  currentPage.value = newPage
  loadKnowledge()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const getExcerpt = (content) => {
  if (!content) {
    return ''
  }

  const cleanContent = String(content).replace(/\s+/g, ' ').trim()
  return cleanContent.length > 150 ? `${cleanContent.slice(0, 150)}...` : cleanContent
}

const getReadingTime = (content) => {
  const textLength = String(content || '').replace(/\s+/g, '').length
  const minutes = Math.max(1, Math.ceil(textLength / 220))
  return `${minutes} ${copy.minutesReadSuffix}`
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
  max-width: 1120px;
  margin: 0 auto;
  padding: var(--spacing-lg);
}

.hero-section {
  display: grid;
  grid-template-columns: minmax(0, 1.05fr) minmax(320px, 0.95fr);
  gap: var(--spacing-lg);
  padding: 32px;
  border-radius: 24px;
  background: linear-gradient(135deg, #edf5ff 0%, #e2eefb 52%, #d7e8f8 100%);
  box-shadow: 0 22px 48px rgba(0, 71, 141, 0.22);
}

.hero-main {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  gap: 24px;
}

.hero-content {
  display: flex;
  flex-direction: column;
  gap: 14px;
  color: #16365f;
}

.hero-kicker {
  display: inline-flex;
  align-items: center;
  width: fit-content;
  padding: 6px 12px;
  border-radius: 999px;
  background: rgba(0, 93, 185, 0.1);
  color: #005db9;
  font-size: var(--font-size-sm);
  font-weight: 700;
  letter-spacing: 0.04em;
}

.hero-content h1 {
  margin: 0;
  font-size: 40px;
  line-height: 1.2;
  font-weight: 800;
  color: #16365f;
}

.hero-content p {
  max-width: 620px;
  margin: 0;
  color: #48627c;
  font-size: 16px;
  line-height: 1.85;
}

.hero-stats {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.hero-stat-card {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 18px 20px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.72);
  border: 1px solid rgba(0, 93, 185, 0.12);
  color: #16365f;
  backdrop-filter: blur(8px);
}

.hero-stat-card strong {
  font-size: 28px;
  font-weight: 800;
}

.hero-stat-label {
  color: #60778e;
  font-size: var(--font-size-sm);
}

.hero-visual {
  position: relative;
  min-height: 320px;
  overflow: hidden;
  border-radius: 22px;
  background: linear-gradient(135deg, #bfd8f3 0%, #dcebf8 100%);
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.32);
}

.hero-visual-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.filter-panel {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
  padding: 24px;
  border-radius: 20px;
  background: var(--bg-primary);
  box-shadow: var(--shadow-md);
}

.filter-header h2 {
  margin: 0 0 8px;
  color: var(--text-primary);
  font-size: 22px;
}

.filter-header p {
  margin: 0;
  color: var(--text-secondary);
}

.filter-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.filter-btn {
  padding: 10px 16px;
  border: 1px solid #d7e2ef;
  border-radius: 999px;
  background: #f8fbff;
  color: #34506d;
  font-size: 15px;
  font-weight: 600;
}

.filter-btn:hover {
  border-color: var(--primary-color);
  color: var(--primary-color);
  background: rgba(0, 102, 204, 0.06);
}

.filter-btn.active {
  background: linear-gradient(135deg, var(--primary-color), var(--primary-light));
  border-color: transparent;
  color: #ffffff;
  box-shadow: 0 10px 20px rgba(0, 102, 204, 0.18);
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-lg);
  padding: calc(var(--spacing-xl) * 2);
}

.spinner {
  width: 48px;
  height: 48px;
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

.knowledge-container {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
}

.knowledge-list {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.knowledge-card {
  display: flex;
  flex-direction: column;
  gap: 18px;
  padding: 24px 26px;
  border-radius: 22px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(249, 251, 255, 0.98));
  border: 1px solid #e6edf5;
  color: var(--text-primary);
  text-decoration: none;
  box-shadow: 0 14px 32px rgba(15, 59, 105, 0.07);
  transition: transform 0.3s ease, box-shadow 0.3s ease, border-color 0.3s ease;
}

.knowledge-card:hover {
  transform: translateY(-4px);
  border-color: rgba(0, 102, 204, 0.26);
  box-shadow: 0 20px 38px rgba(15, 59, 105, 0.12);
}

.knowledge-card-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--spacing-md);
  flex-wrap: wrap;
}

.knowledge-category {
  display: inline-flex;
  align-items: center;
  padding: 8px 14px;
  border-radius: 999px;
  background: rgba(0, 102, 204, 0.08);
  color: var(--primary-color);
  font-size: 13px;
  font-weight: 700;
}

.knowledge-views {
  color: var(--text-light);
  font-size: 13px;
  font-weight: 600;
}

.knowledge-main {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.knowledge-title {
  margin: 0;
  color: var(--text-primary);
  font-size: 26px;
  line-height: 1.35;
  font-weight: 800;
}

.knowledge-excerpt {
  margin: 0;
  color: var(--text-secondary);
  line-height: 1.9;
  font-size: 15px;
}

.knowledge-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--spacing-md);
  flex-wrap: wrap;
  padding-top: 4px;
}

.knowledge-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  color: var(--text-light);
  font-size: 14px;
}

.meta-divider {
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background: #c3cada;
}

.knowledge-link {
  color: var(--primary-color);
  font-weight: 700;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 52px 24px;
  border-radius: 22px;
  background: var(--bg-primary);
  box-shadow: var(--shadow-sm);
  text-align: center;
}

.empty-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: rgba(0, 102, 204, 0.08);
  color: var(--primary-color);
  font-size: 24px;
  font-weight: 800;
}

.empty-state h3,
.empty-state p {
  margin: 0;
}

.empty-state p {
  color: var(--text-secondary);
}

@media (max-width: 900px) {
  .hero-section {
    grid-template-columns: 1fr;
  }

  .hero-stats {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .knowledge-page {
    gap: var(--spacing-lg);
    padding: var(--spacing-md);
  }

  .hero-section,
  .filter-panel,
  .knowledge-card {
    padding: 20px;
    border-radius: 18px;
  }

  .hero-content h1 {
    font-size: 32px;
  }

  .knowledge-title {
    font-size: 22px;
  }

  .knowledge-footer {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
