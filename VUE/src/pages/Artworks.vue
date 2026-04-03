<template>
  <div class="artworks-page">
    <section class="hero-section">
      <div class="hero-content">
        <span class="hero-kicker">{{ copy.heroKicker }}</span>
        <h1>{{ copy.title }}</h1>
        <p>{{ copy.subtitle }}</p>

        <div class="hero-stats">
          <div class="hero-stat-card">
            <span>{{ copy.totalArtworks }}</span>
            <strong>{{ totalItems || artworks.length }}</strong>
          </div>
          <div class="hero-stat-card">
            <span>{{ copy.totalCategories }}</span>
            <strong>{{ categories.length }}</strong>
          </div>
          <div class="hero-stat-card">
            <span>{{ copy.currentFilter }}</span>
            <strong>{{ currentCategoryName }}</strong>
          </div>
        </div>
      </div>

      <div v-if="featuredArtwork" class="hero-feature-card">
        <div class="hero-feature-media">
          <img
            :src="artworksHeroImage"
            :alt="featuredArtwork.title"
            class="hero-feature-image"
          />
          <span class="hero-feature-badge">
            {{ featuredArtwork.category || copy.defaultCategory }}
          </span>
        </div>

        <div class="hero-feature-body">
          <span class="hero-feature-label">{{ copy.featuredLabel }}</span>
          <h2>{{ featuredArtwork.title }}</h2>
          <p>{{ getExcerpt(featuredArtwork.description, 90) }}</p>

          <div class="hero-feature-meta">
            <span>{{ copy.creatorPrefix }} {{ getArtworkCreator(featuredArtwork) }}</span>
            <span class="meta-divider"></span>
            <span>{{ copy.viewsPrefix }} {{ featuredArtwork.viewCount || 0 }}</span>
          </div>
        </div>
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
          v-for="category in categories"
          :key="category"
          class="filter-btn"
          :class="{ active: selectedCategory === category }"
          @click="selectCategory(category)"
        >
          {{ category }}
        </button>
      </div>
    </section>

    <div v-if="isLoading" class="loading-container">
      <div class="spinner"></div>
      <p>{{ copy.loading }}</p>
    </div>

    <div v-else-if="artworks.length > 0" class="artworks-container">
      <div class="artworks-grid">
        <router-link
          v-for="artwork in artworks"
          :key="artwork.id"
          :to="`/artworks/${artwork.id}`"
          class="artwork-card"
        >
          <div class="artwork-card-media">
            <img :src="artwork.imageUrl" :alt="artwork.title" class="artwork-image" />
            <div class="artwork-card-overlay">
              <span>{{ copy.viewDetail }}</span>
            </div>
          </div>

          <div class="artwork-card-body">
            <div class="artwork-card-top">
              <span class="artwork-category">{{ artwork.category || copy.defaultCategory }}</span>
              <span class="artwork-technique">{{ getArtworkTechnique(artwork) }}</span>
            </div>

            <h3 class="artwork-title">{{ artwork.title }}</h3>
            <p class="artwork-description">{{ getExcerpt(artwork.description, 120) }}</p>

            <div class="artwork-card-footer">
              <div class="artwork-meta">
                <span>{{ copy.creatorPrefix }} {{ getArtworkCreator(artwork) }}</span>
                <span class="meta-divider"></span>
                <span>{{ copy.viewsPrefix }} {{ artwork.viewCount || 0 }}</span>
                <span class="meta-divider"></span>
                <span>{{ copy.collectPrefix }} {{ artwork.collectCount || 0 }}</span>
              </div>

              <span class="artwork-link">{{ copy.viewDetail }}</span>
            </div>
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
import artworksHeroImage from '../assets/image.png'
import { Pagination } from '../components'
import { ArtworkService } from '../services'
import { useToast } from '../utils/useToast'

const copy = {
  title: '刺绣作品',
  subtitle: '集中浏览文山刺绣作品，按分类快速筛选，查看每件作品的创作者、技法与人气数据。',
  heroKicker: '作品展示',
  totalArtworks: '作品数量',
  totalCategories: '分类数量',
  currentFilter: '当前筛选',
  featuredLabel: '本页精选',
  filterTitle: '作品分类',
  filterSubtitle: '按题材或工艺浏览作品，快速找到你想查看的内容。',
  allCategories: '全部',
  loading: '正在加载作品列表...',
  creatorPrefix: '创作者',
  viewsPrefix: '浏览',
  collectPrefix: '收藏',
  viewDetail: '查看详情',
  defaultCategory: '未分类',
  defaultCreator: '未知创作者',
  defaultTechnique: '传统技法',
  allFilterName: '全部',
  emptyIcon: '绣',
  emptyTitle: '暂无作品',
  emptyText: '可以切换分类再试一下，或稍后回来查看最新作品。',
  loadError: '加载作品失败，请稍后重试',
}

const artworks = ref([])
const categories = ref([])
const selectedCategory = ref('')
const currentPage = ref(1)
const totalPages = ref(1)
const totalItems = ref(0)
const isLoading = ref(false)

const { error } = useToast()

const currentCategoryName = computed(() => selectedCategory.value || copy.allFilterName)
const featuredArtwork = computed(() => artworks.value[0] || null)

const loadCategories = async () => {
  try {
    const response = await ArtworkService.getCategories()
    categories.value = response || []
  } catch (err) {
    console.error('Failed to load categories:', err)
  }
}

const loadArtworks = async () => {
  isLoading.value = true

  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: 12,
    }

    if (selectedCategory.value) {
      params.category = selectedCategory.value
    }

    const data = await ArtworkService.getArtworks(params)
    artworks.value = data.items || []
    totalPages.value = data.totalPages || 1
    totalItems.value = data.total || 0
  } catch (err) {
    console.error('Failed to load artworks:', err)
    error(copy.loadError)
  } finally {
    isLoading.value = false
  }
}

const selectCategory = (category) => {
  selectedCategory.value = category
  currentPage.value = 1
  loadArtworks()
}

const handlePageChange = (newPage) => {
  currentPage.value = newPage
  loadArtworks()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const getArtworkCreator = (artwork) => {
  return artwork.creator || copy.defaultCreator
}

const getArtworkTechnique = (artwork) => {
  return artwork.technique || copy.defaultTechnique
}

const getExcerpt = (value, maxLength = 120) => {
  const content = String(value || '').replace(/\s+/g, ' ').trim()

  if (!content) {
    return ''
  }

  return content.length > maxLength ? `${content.slice(0, maxLength)}...` : content
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
  max-width: 1180px;
  margin: 0 auto;
  padding: var(--spacing-lg);
}

.hero-section {
  display: grid;
  grid-template-columns: minmax(0, 1.15fr) minmax(320px, 0.85fr);
  gap: var(--spacing-lg);
  padding: 32px;
  border-radius: 24px;
  background:
    radial-gradient(circle at top right, rgba(255, 255, 255, 0.16), transparent 28%),
    linear-gradient(135deg, #8f2d1e 0%, #b8472f 40%, #e58d49 100%);
  box-shadow: 0 24px 48px rgba(143, 45, 30, 0.22);
}

.hero-content {
  display: flex;
  flex-direction: column;
  gap: 14px;
  color: #ffffff;
}

.hero-kicker {
  display: inline-flex;
  align-items: center;
  width: fit-content;
  padding: 6px 12px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.16);
  font-size: 13px;
  font-weight: 700;
  letter-spacing: 0.04em;
}

.hero-content h1 {
  margin: 0;
  font-size: 42px;
  line-height: 1.15;
  font-weight: 800;
}

.hero-content p {
  max-width: 620px;
  margin: 0;
  font-size: 16px;
  line-height: 1.85;
  color: rgba(255, 255, 255, 0.88);
}

.hero-stats {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  margin-top: 8px;
}

.hero-stat-card {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 18px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.12);
  border: 1px solid rgba(255, 255, 255, 0.14);
  backdrop-filter: blur(10px);
}

.hero-stat-card span {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.76);
}

.hero-stat-card strong {
  font-size: 28px;
  font-weight: 800;
}

.hero-feature-card {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 18px;
  border-radius: 22px;
  background: rgba(255, 248, 243, 0.94);
  box-shadow: 0 18px 34px rgba(94, 21, 10, 0.18);
}

.hero-feature-media {
  position: relative;
  min-height: 240px;
  border-radius: 18px;
  overflow: hidden;
}

.hero-feature-image {
  width: 100%;
  height: 100%;
  min-height: 240px;
  object-fit: cover;
  display: block;
}

.hero-feature-badge {
  position: absolute;
  top: 14px;
  left: 14px;
  display: inline-flex;
  align-items: center;
  padding: 7px 12px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.9);
  color: #8f2d1e;
  font-size: 12px;
  font-weight: 700;
}

.hero-feature-body {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.hero-feature-label {
  color: #b8472f;
  font-size: 13px;
  font-weight: 700;
}

.hero-feature-body h2 {
  margin: 0;
  font-size: 24px;
  line-height: 1.35;
  color: #40211c;
}

.hero-feature-body p {
  margin: 0;
  color: #6b4e48;
  line-height: 1.8;
}

.hero-feature-meta,
.artwork-meta {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
  color: var(--text-light);
  font-size: 13px;
}

.meta-divider {
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background: #d0b6aa;
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
  font-size: 22px;
  color: var(--text-primary);
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
  border: 1px solid #ead8cf;
  border-radius: 999px;
  background: #fff7f3;
  color: #8f4b3d;
  font-size: 15px;
  font-weight: 600;
  transition: transform 0.2s ease, border-color 0.2s ease, background 0.2s ease, color 0.2s ease;
}

.filter-btn:hover {
  transform: translateY(-1px);
  border-color: #c86d52;
  color: #b8472f;
}

.filter-btn.active {
  border-color: transparent;
  background: linear-gradient(135deg, #b8472f, #e58d49);
  color: #ffffff;
  box-shadow: 0 10px 22px rgba(184, 71, 47, 0.18);
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

.artworks-container {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
}

.artworks-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(270px, 1fr));
  gap: 20px;
}

.artwork-card {
  display: flex;
  flex-direction: column;
  overflow: hidden;
  border-radius: 22px;
  background: linear-gradient(180deg, #ffffff, #fcf8f5);
  border: 1px solid #f0e3dc;
  color: var(--text-primary);
  text-decoration: none;
  box-shadow: 0 16px 32px rgba(77, 41, 29, 0.08);
  transition: transform 0.3s ease, box-shadow 0.3s ease, border-color 0.3s ease;
}

.artwork-card:hover {
  transform: translateY(-5px);
  border-color: rgba(184, 71, 47, 0.28);
  box-shadow: 0 22px 40px rgba(77, 41, 29, 0.14);
}

.artwork-card-media {
  position: relative;
  overflow: hidden;
  background: #f4ece6;
}

.artwork-image {
  width: 100%;
  height: 260px;
  object-fit: cover;
  display: block;
  transition: transform 0.35s ease;
}

.artwork-card:hover .artwork-image {
  transform: scale(1.05);
}

.artwork-card-overlay {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(180deg, rgba(48, 25, 18, 0.1), rgba(48, 25, 18, 0.62));
  color: #ffffff;
  font-weight: 700;
  letter-spacing: 0.02em;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.artwork-card:hover .artwork-card-overlay {
  opacity: 1;
}

.artwork-card-body {
  display: flex;
  flex-direction: column;
  gap: 14px;
  padding: 22px;
  min-height: 240px;
}

.artwork-card-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  flex-wrap: wrap;
}

.artwork-category,
.artwork-technique {
  display: inline-flex;
  align-items: center;
  padding: 7px 12px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 700;
}

.artwork-category {
  background: rgba(184, 71, 47, 0.1);
  color: #b8472f;
}

.artwork-technique {
  background: #fff4e8;
  color: #a35d25;
}

.artwork-title {
  margin: 0;
  font-size: 24px;
  line-height: 1.35;
  color: var(--text-primary);
}

.artwork-description {
  margin: 0;
  color: var(--text-secondary);
  line-height: 1.85;
  font-size: 15px;
}

.artwork-card-footer {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: var(--spacing-md);
  margin-top: auto;
}

.artwork-link {
  color: #b8472f;
  font-weight: 700;
  white-space: nowrap;
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
  background: rgba(184, 71, 47, 0.1);
  color: #b8472f;
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

@media (max-width: 960px) {
  .hero-section {
    grid-template-columns: 1fr;
  }

  .hero-stats {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .artworks-page {
    gap: var(--spacing-lg);
    padding: var(--spacing-md);
  }

  .hero-section,
  .filter-panel {
    padding: 20px;
    border-radius: 18px;
  }

  .hero-content h1 {
    font-size: 34px;
  }

  .artworks-grid {
    grid-template-columns: 1fr;
  }

  .artwork-card-body {
    padding: 18px;
    min-height: auto;
  }

  .artwork-title {
    font-size: 22px;
  }

  .artwork-card-footer {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
