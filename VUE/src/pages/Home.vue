<template>
  <div class="home-page">
    <!-- 轮播展示区 -->
    <section class="carousel-section">
      <div class="carousel-container">
        <div class="carousel-wrapper">
          <div
            v-for="(carousel, index) in carousels"
            :key="carousel.id"
            class="carousel-item"
            :class="{ active: currentCarouselIndex === index }"
          >
            <img :src="carousel.imageUrl" :alt="carousel.title" class="carousel-image" />
            <div class="carousel-content">
              <h2>{{ carousel.title }}</h2>
              <p>{{ carousel.description }}</p>
              <router-link :to="carousel.link" class="carousel-btn">了解更多</router-link>
            </div>
          </div>
        </div>

        <!-- 轮播控制按钮 -->
        <button class="carousel-btn-prev" @click="prevCarousel" aria-label="上一张">
          ❮
        </button>
        <button class="carousel-btn-next" @click="nextCarousel" aria-label="下一张">
          ❯
        </button>

        <!-- 轮播指示点 -->
        <div class="carousel-indicators">
          <button
            v-for="(_, index) in carousels"
            :key="index"
            class="indicator"
            :class="{ active: currentCarouselIndex === index }"
            @click="currentCarouselIndex = index"
            :aria-label="`轮播 ${index + 1}`"
          />
        </div>
      </div>
    </section>

    <!-- 核心功能导航 -->
    <section class="features-section">
      <div class="section-header">
        <h2>核心功能</h2>
        <p>快速访问平台主要功能</p>
      </div>
      <div class="features-grid">
        <router-link
          v-for="feature in features"
          :key="feature.id"
          :to="feature.link"
          class="feature-card"
        >
          <div class="feature-icon">
            <img :src="feature.iconUrl" :alt="feature.name" />
          </div>
          <h3>{{ feature.name }}</h3>
          <p>{{ feature.description }}</p>
        </router-link>
      </div>
    </section>

    <!-- 最新活动公告区 -->
    <section class="announcements-section">
      <div class="section-header">
        <h2>最新动态</h2>
        <p>了解平台最新活动和公告</p>
      </div>
      <div class="announcements-list">
        <div
          v-for="announcement in announcements"
          :key="announcement.id"
          class="announcement-item"
          :class="announcement.type"
        >
          <div class="announcement-badge">{{ announcement.type === 'announcement' ? '公告' : '活动' }}</div>
          <div class="announcement-content">
            <h3>{{ announcement.title }}</h3>
            <p>{{ announcement.content }}</p>
            <span class="announcement-date">{{ formatDate(announcement.createdAt) }}</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 平台数据统计区 -->
    <section class="statistics-section">
      <div class="section-header">
        <h2>平台数据</h2>
        <p>实时平台运营数据</p>
      </div>
      <div class="statistics-grid">
        <div class="stat-card">
          <div class="stat-icon">👥</div>
          <div class="stat-content">
            <div class="stat-value">{{ statistics.totalUsers || 0 }}</div>
            <div class="stat-label">注册用户</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">🎨</div>
          <div class="stat-content">
            <div class="stat-value">{{ statistics.totalArtworks || 0 }}</div>
            <div class="stat-label">作品总数</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">📚</div>
          <div class="stat-content">
            <div class="stat-value">{{ statistics.totalKnowledge || 0 }}</div>
            <div class="stat-label">知识文章</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">👁️</div>
          <div class="stat-content">
            <div class="stat-value">{{ formatNumber(statistics.totalViews || 0) }}</div>
            <div class="stat-label">总浏览量</div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { HomeService } from '../services'
import { useToast } from '../utils/useToast'

const carousels = ref([])
const features = ref([])
const announcements = ref([])
const statistics = ref({})
const currentCarouselIndex = ref(0)
const carouselInterval = ref(null)

const { showToast } = useToast()

// 加载首页数据
const loadHomeData = async () => {
  try {
    // 并行加载所有数据
    const [carouselRes, featuresRes, announcementsRes, statisticsRes] = await Promise.all([
      HomeService.getCarousel(),
      HomeService.getFeatures(),
      HomeService.getAnnouncements(),
      HomeService.getStatistics(),
    ])

    carousels.value = carouselRes.data || []
    features.value = featuresRes.data || []
    announcements.value = announcementsRes.data || []
    statistics.value = statisticsRes.data || {}
  } catch (error) {
    console.error('Failed to load home data:', error)
    showToast('加载首页数据失败', 'error')
  }
}

// 轮播控制
const nextCarousel = () => {
  currentCarouselIndex.value = (currentCarouselIndex.value + 1) % carousels.value.length
}

const prevCarousel = () => {
  currentCarouselIndex.value =
    (currentCarouselIndex.value - 1 + carousels.value.length) % carousels.value.length
}

// 自动轮播
const startAutoCarousel = () => {
  if (carousels.value.length > 1) {
    carouselInterval.value = setInterval(() => {
      nextCarousel()
    }, 5000) // 每5秒切换一次
  }
}

const stopAutoCarousel = () => {
  if (carouselInterval.value) {
    clearInterval(carouselInterval.value)
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

// 格式化数字
const formatNumber = (num) => {
  if (num >= 1000000) {
    return (num / 1000000).toFixed(1) + 'M'
  } else if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'K'
  }
  return num.toString()
}

onMounted(() => {
  loadHomeData()
  startAutoCarousel()
})

onUnmounted(() => {
  stopAutoCarousel()
})
</script>

<style scoped>
.home-page {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
  padding: var(--spacing-lg);
  max-width: 1400px;
  margin: 0 auto;
}

/* 轮播展示区 */
.carousel-section {
  width: 100%;
}

.carousel-container {
  position: relative;
  width: 100%;
  height: 400px;
  border-radius: var(--border-radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-lg);
  background-color: var(--bg-primary);
}

.carousel-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
}

.carousel-item {
  position: absolute;
  width: 100%;
  height: 100%;
  opacity: 0;
  transition: opacity 0.5s ease-in-out;
  display: flex;
  align-items: center;
}

.carousel-item.active {
  opacity: 1;
}

.carousel-image {
  position: absolute;
  width: 100%;
  height: 100%;
  object-fit: cover;
  z-index: 1;
}

.carousel-content {
  position: absolute;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: flex-start;
  padding: var(--spacing-xl);
  background: linear-gradient(90deg, rgba(0, 0, 0, 0.6) 0%, rgba(0, 0, 0, 0) 100%);
  z-index: 2;
  color: white;
}

.carousel-content h2 {
  font-size: var(--font-size-2xl);
  margin-bottom: var(--spacing-md);
  font-weight: 700;
}

.carousel-content p {
  font-size: var(--font-size-lg);
  margin-bottom: var(--spacing-lg);
  max-width: 500px;
  line-height: 1.6;
}

.carousel-content .carousel-btn {
  display: inline-block;
  padding: var(--spacing-md) var(--spacing-lg);
  background-color: var(--primary-color);
  color: white;
  border-radius: var(--border-radius-md);
  text-decoration: none;
  font-weight: 600;
  transition: all 0.3s ease;
}

.carousel-content .carousel-btn:hover {
  background-color: var(--primary-light);
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

/* 轮播控制按钮 */
.carousel-btn-prev,
.carousel-btn-next {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  z-index: 3;
  width: 50px;
  height: 50px;
  background-color: rgba(0, 0, 0, 0.5);
  color: white;
  border: none;
  border-radius: 50%;
  font-size: var(--font-size-xl);
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.carousel-btn-prev:hover,
.carousel-btn-next:hover {
  background-color: rgba(0, 0, 0, 0.8);
}

.carousel-btn-prev {
  left: var(--spacing-lg);
}

.carousel-btn-next {
  right: var(--spacing-lg);
}

/* 轮播指示点 */
.carousel-indicators {
  position: absolute;
  bottom: var(--spacing-lg);
  left: 50%;
  transform: translateX(-50%);
  z-index: 3;
  display: flex;
  gap: var(--spacing-md);
}

.indicator {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.5);
  border: none;
  cursor: pointer;
  transition: all 0.3s ease;
}

.indicator.active {
  background-color: white;
  width: 30px;
  border-radius: 6px;
}

/* 核心功能导航 */
.features-section {
  width: 100%;
}

.section-header {
  text-align: center;
  margin-bottom: var(--spacing-xl);
}

.section-header h2 {
  font-size: var(--font-size-2xl);
  color: var(--primary-color);
  margin-bottom: var(--spacing-sm);
  font-weight: 700;
}

.section-header p {
  font-size: var(--font-size-lg);
  color: var(--text-secondary);
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: var(--spacing-lg);
}

.feature-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: var(--spacing-lg);
  background-color: var(--bg-primary);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-md);
  text-decoration: none;
  color: var(--text-primary);
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.feature-card:hover {
  transform: translateY(-8px);
  box-shadow: var(--shadow-lg);
  border-color: var(--primary-color);
}

.feature-icon {
  width: 80px;
  height: 80px;
  margin-bottom: var(--spacing-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--primary-light) 0%, var(--primary-color) 100%);
  border-radius: var(--border-radius-lg);
}

.feature-icon img {
  width: 50px;
  height: 50px;
  object-fit: contain;
}

.feature-card h3 {
  font-size: var(--font-size-lg);
  margin-bottom: var(--spacing-sm);
  font-weight: 600;
}

.feature-card p {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  text-align: center;
  line-height: 1.6;
}

/* 最新活动公告区 */
.announcements-section {
  width: 100%;
}

.announcements-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.announcement-item {
  display: flex;
  gap: var(--spacing-lg);
  padding: var(--spacing-lg);
  background-color: var(--bg-primary);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-md);
  border-left: 4px solid var(--primary-color);
  transition: all 0.3s ease;
}

.announcement-item:hover {
  box-shadow: var(--shadow-lg);
  transform: translateX(4px);
}

.announcement-item.activity {
  border-left-color: var(--accent-color);
}

.announcement-badge {
  display: inline-block;
  padding: var(--spacing-sm) var(--spacing-md);
  background-color: var(--primary-color);
  color: white;
  border-radius: var(--border-radius-md);
  font-size: var(--font-size-sm);
  font-weight: 600;
  white-space: nowrap;
  height: fit-content;
}

.announcement-item.activity .announcement-badge {
  background-color: var(--accent-color);
}

.announcement-content {
  flex: 1;
}

.announcement-content h3 {
  font-size: var(--font-size-lg);
  margin-bottom: var(--spacing-sm);
  color: var(--text-primary);
  font-weight: 600;
}

.announcement-content p {
  font-size: var(--font-size-base);
  color: var(--text-secondary);
  margin-bottom: var(--spacing-sm);
  line-height: 1.6;
}

.announcement-date {
  font-size: var(--font-size-sm);
  color: var(--text-light);
}

/* 平台数据统计区 */
.statistics-section {
  width: 100%;
}

.statistics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: var(--spacing-lg);
}

.stat-card {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
  padding: var(--spacing-lg);
  background: linear-gradient(135deg, var(--primary-light) 0%, var(--primary-color) 100%);
  border-radius: var(--border-radius-lg);
  color: white;
  box-shadow: var(--shadow-md);
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
}

.stat-icon {
  font-size: 40px;
  min-width: 60px;
  text-align: center;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: var(--font-size-2xl);
  font-weight: 700;
  margin-bottom: var(--spacing-sm);
}

.stat-label {
  font-size: var(--font-size-sm);
  opacity: 0.9;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .home-page {
    gap: var(--spacing-lg);
    padding: var(--spacing-md);
  }

  .carousel-container {
    height: 250px;
  }

  .carousel-content {
    padding: var(--spacing-lg);
  }

  .carousel-content h2 {
    font-size: var(--font-size-xl);
  }

  .carousel-content p {
    font-size: var(--font-size-base);
  }

  .carousel-btn-prev,
  .carousel-btn-next {
    width: 40px;
    height: 40px;
    font-size: var(--font-size-lg);
  }

  .features-grid {
    grid-template-columns: 1fr;
  }

  .section-header h2 {
    font-size: var(--font-size-xl);
  }

  .statistics-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .stat-card {
    flex-direction: column;
    text-align: center;
  }

  .stat-icon {
    min-width: auto;
  }
}
</style>
