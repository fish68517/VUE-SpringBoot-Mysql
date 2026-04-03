<template>
  <div class="home-page">
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
              <router-link :to="carousel.link" class="carousel-btn">{{ copy.learnMore }}</router-link>
            </div>
          </div>
        </div>

        <button class="carousel-btn-prev" @click="prevCarousel" :aria-label="copy.prevSlide">
          &#8249;
        </button>
        <button class="carousel-btn-next" @click="nextCarousel" :aria-label="copy.nextSlide">
          &#8250;
        </button>

        <div class="carousel-indicators">
          <button
            v-for="(_, index) in carousels"
            :key="index"
            class="indicator"
            :class="{ active: currentCarouselIndex === index }"
            @click="currentCarouselIndex = index"
            :aria-label="`${copy.carouselLabel} ${index + 1}`"
          />
        </div>
      </div>
    </section>

    <section class="features-section">
      <div class="section-header">
        <h2>{{ copy.featuresTitle }}</h2>
        <p>{{ copy.featuresSubtitle }}</p>
      </div>

      <div class="features-grid">
        <router-link
          v-for="feature in decoratedFeatures"
          :key="feature.id"
          :to="feature.link"
          class="feature-card"
        >
          <div class="feature-media">
            <img :src="feature.assetIcon" :alt="feature.name" />
          </div>
          <div class="feature-text">
            <span class="feature-kicker">{{ copy.featureEntry }}</span>
            <h3>{{ feature.name }}</h3>
            <p>{{ feature.description }}</p>
            <span class="feature-link">{{ copy.enterNow }}</span>
          </div>
        </router-link>
      </div>
    </section>

    <section class="statistics-section">
      <div class="section-header">
        <h2>{{ copy.statsTitle }}</h2>
        <p>{{ copy.statsSubtitle }}</p>
      </div>
      <div class="statistics-grid">
        <div class="stat-card">
          <div class="stat-icon">U</div>
          <div class="stat-content">
            <div class="stat-value">{{ statistics.totalUsers || 0 }}</div>
            <div class="stat-label">{{ copy.totalUsers }}</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">A</div>
          <div class="stat-content">
            <div class="stat-value">{{ statistics.totalArtworks || 0 }}</div>
            <div class="stat-label">{{ copy.totalArtworks }}</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">K</div>
          <div class="stat-content">
            <div class="stat-value">{{ statistics.totalKnowledge || 0 }}</div>
            <div class="stat-label">{{ copy.totalKnowledge }}</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">V</div>
          <div class="stat-content">
            <div class="stat-value">{{ formatNumber(statistics.totalVisits || 0) }}</div>
            <div class="stat-label">{{ copy.totalVisits }}</div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue'
import featureArtworks from '../assets/home/feature-artworks.svg'
import featureKnowledge from '../assets/home/feature-knowledge.svg'
import featureCommunity from '../assets/home/feature-community.svg'
import featureUser from '../assets/home/feature-user.svg'
import { HomeService } from '../services'
import { useToast } from '../utils/useToast'

const copy = {
  learnMore: '\u4e86\u89e3\u66f4\u591a',
  prevSlide: '\u4e0a\u4e00\u5f20',
  nextSlide: '\u4e0b\u4e00\u5f20',
  carouselLabel: '\u8f6e\u64ad',
  featuresTitle: '\u6838\u5fc3\u529f\u80fd',
  featuresSubtitle: '\u4ece\u9996\u9875\u76f4\u8fbe\u5e73\u53f0\u4e3b\u8981\u7248\u5757\uff0c\u66f4\u5feb\u627e\u5230\u4f60\u60f3\u770b\u7684\u5185\u5bb9\u3002',
  featureEntry: '\u529f\u80fd\u5165\u53e3',
  enterNow: '\u7acb\u5373\u8fdb\u5165',
  statsTitle: '\u5e73\u53f0\u6570\u636e',
  statsSubtitle: '\u5b9e\u65f6\u4e86\u89e3\u5e73\u53f0\u5f53\u524d\u7684\u5185\u5bb9\u4e0e\u8bbf\u95ee\u60c5\u51b5\u3002',
  totalUsers: '\u6ce8\u518c\u7528\u6237',
  totalArtworks: '\u4f5c\u54c1\u603b\u6570',
  totalKnowledge: '\u77e5\u8bc6\u6587\u7ae0',
  totalVisits: '\u603b\u6d4f\u89c8\u91cf',
  homeLoadError: '\u52a0\u8f7d\u9996\u9875\u6570\u636e\u5931\u8d25',
}

const featureAssetMap = {
  '/artworks': featureArtworks,
  '/knowledge': featureKnowledge,
  '/community': featureCommunity,
  '/user': featureUser,
  '\u523a\u7ee3\u5c55\u793a': featureArtworks,
  '\u77e5\u8bc6\u79d1\u666e': featureKnowledge,
  '\u4e92\u52a8\u4ea4\u6d41': featureCommunity,
  '\u7528\u6237\u4e2d\u5fc3': featureUser,
}

const carousels = ref([])
const features = ref([])
const statistics = ref({})
const currentCarouselIndex = ref(0)
const carouselInterval = ref(null)

const { error } = useToast()

const decoratedFeatures = computed(() => {
  return features.value.map((feature) => ({
    ...feature,
    assetIcon: featureAssetMap[feature.link] || featureAssetMap[feature.name] || featureArtworks,
  }))
})

const loadHomeData = async () => {
  try {
    const [carouselRes, featuresRes, statisticsRes] = await Promise.all([
      HomeService.getCarousel(),
      HomeService.getFeatures(),
      HomeService.getStatistics(),
    ])

    carousels.value = carouselRes || []
    features.value = featuresRes || []
    statistics.value = statisticsRes || {}
  } catch (loadError) {
    console.error('Failed to load home data:', loadError)
    error(copy.homeLoadError)
  }
}

const nextCarousel = () => {
  if (carousels.value.length === 0) {
    return
  }

  currentCarouselIndex.value = (currentCarouselIndex.value + 1) % carousels.value.length
}

const prevCarousel = () => {
  if (carousels.value.length === 0) {
    return
  }

  currentCarouselIndex.value =
    (currentCarouselIndex.value - 1 + carousels.value.length) % carousels.value.length
}

const startAutoCarousel = () => {
  if (carousels.value.length > 1) {
    carouselInterval.value = setInterval(() => {
      nextCarousel()
    }, 5000)
  }
}

const stopAutoCarousel = () => {
  if (carouselInterval.value) {
    clearInterval(carouselInterval.value)
    carouselInterval.value = null
  }
}

const formatNumber = (num) => {
  if (!num) {
    return '0'
  }

  if (num >= 1000000) {
    return `${(num / 1000000).toFixed(1)}M`
  }

  if (num >= 1000) {
    return `${(num / 1000).toFixed(1)}K`
  }

  return num.toString()
}
onMounted(async () => {
  await loadHomeData()
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

.carousel-section {
  width: 100%;
}

.carousel-container {
  position: relative;
  width: 100%;
  height: 400px;
  border-radius: 22px;
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
  background: linear-gradient(90deg, rgba(0, 0, 0, 0.64) 0%, rgba(0, 0, 0, 0.12) 52%, rgba(0, 0, 0, 0) 100%);
  z-index: 2;
  color: white;
}

.carousel-content h2 {
  font-size: 36px;
  margin-bottom: var(--spacing-md);
  font-weight: 700;
}

.carousel-content p {
  font-size: var(--font-size-lg);
  margin-bottom: var(--spacing-lg);
  max-width: 540px;
  line-height: 1.75;
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

.carousel-btn-prev,
.carousel-btn-next {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  z-index: 3;
  width: 50px;
  height: 50px;
  background-color: rgba(0, 0, 0, 0.45);
  color: white;
  border: none;
  border-radius: 50%;
  font-size: 34px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.carousel-btn-prev:hover,
.carousel-btn-next:hover {
  background-color: rgba(0, 0, 0, 0.75);
}

.carousel-btn-prev {
  left: var(--spacing-lg);
}

.carousel-btn-next {
  right: var(--spacing-lg);
}

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

.features-section,
.statistics-section {
  width: 100%;
}

.section-header {
  text-align: center;
  margin-bottom: var(--spacing-xl);
}

.section-header h2 {
  font-size: 32px;
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
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: var(--spacing-lg);
}

.feature-card {
  display: flex;
  flex-direction: column;
  gap: 18px;
  padding: 20px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(247, 250, 255, 0.98));
  border-radius: 24px;
  box-shadow: 0 16px 36px rgba(10, 56, 103, 0.08);
  text-decoration: none;
  color: var(--text-primary);
  transition: all 0.3s ease;
  border: 1px solid #e4edf8;
}

.feature-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 24px 44px rgba(10, 56, 103, 0.12);
  border-color: rgba(0, 102, 204, 0.22);
}

.feature-media {
  width: 100%;
  height: 180px;
  border-radius: 18px;
  overflow: hidden;
  background: linear-gradient(135deg, #eef6ff 0%, #f8fbff 100%);
}

.feature-media img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.feature-text {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.feature-kicker {
  display: inline-flex;
  align-items: center;
  width: fit-content;
  padding: 6px 10px;
  border-radius: 999px;
  background: rgba(0, 102, 204, 0.08);
  color: var(--primary-color);
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.05em;
}

.feature-card h3 {
  font-size: 24px;
  margin: 0;
  font-weight: 700;
}

.feature-card p {
  font-size: 15px;
  color: var(--text-secondary);
  line-height: 1.8;
  margin: 0;
}

.feature-link {
  color: var(--primary-color);
  font-size: 14px;
  font-weight: 700;
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
  border-radius: 22px;
  color: white;
  box-shadow: var(--shadow-md);
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
}

.stat-icon {
  font-size: 28px;
  min-width: 60px;
  height: 60px;
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  background: rgba(255, 255, 255, 0.16);
  font-weight: 800;
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

@media (max-width: 768px) {
  .home-page {
    gap: var(--spacing-lg);
    padding: var(--spacing-md);
  }

  .carousel-container {
    height: 260px;
  }

  .carousel-content {
    padding: var(--spacing-lg);
  }

  .carousel-content h2 {
    font-size: 28px;
  }

  .carousel-content p {
    font-size: var(--font-size-base);
  }

  .carousel-btn-prev,
  .carousel-btn-next {
    width: 40px;
    height: 40px;
    font-size: 28px;
  }

  .section-header h2 {
    font-size: 28px;
  }

  .features-grid,
  .statistics-grid {
    grid-template-columns: 1fr;
  }

  .feature-media {
    height: 160px;
  }

  .stat-card {
    flex-direction: row;
  }
}
</style>
