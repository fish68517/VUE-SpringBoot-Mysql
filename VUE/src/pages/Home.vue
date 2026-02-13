<template>
  <div class="home">
    <!-- Carousel Section -->
    <section class="carousel-section">
      <div class="carousel-container">
        <div class="carousel">
          <div class="carousel-item" v-if="carouselPatterns.length > 0">
            <img :src="carouselPatterns[currentSlide].imageUrl" :alt="carouselPatterns[currentSlide].name" />
            <div class="carousel-overlay">
              <h2>{{ carouselPatterns[currentSlide].name }}</h2>
              <p>{{ carouselPatterns[currentSlide].category }}</p>
            </div>
          </div>
          <div v-else class="carousel-placeholder">
            <p>加载中...</p>
          </div>
        </div>
        
        <!-- Carousel Controls -->
        <button class="carousel-btn prev" @click="previousSlide" v-if="carouselPatterns.length > 0">
          ❮
        </button>
        <button class="carousel-btn next" @click="nextSlide" v-if="carouselPatterns.length > 0">
          ❯
        </button>
        
        <!-- Carousel Indicators -->
        <div class="carousel-indicators" v-if="carouselPatterns.length > 0">
          <span 
            v-for="(_, index) in carouselPatterns" 
            :key="index"
            :class="{ active: index === currentSlide }"
            @click="currentSlide = index"
          ></span>
        </div>
      </div>

      <!-- Cultural Theme Slogans -->
      <div class="slogans">
        <div class="slogan">
          <h3>传承文化</h3>
          <p>纳西族纹样承载着深厚的文化内涵和历史记忆</p>
        </div>
        <div class="slogan">
          <h3>创意融合</h3>
          <p>将传统纹样与现代设计相结合，创造新的艺术形式</p>
        </div>
        <div class="slogan">
          <h3>知识共享</h3>
          <p>通过数字化展示，让更多人了解和欣赏纳西族文化</p>
        </div>
      </div>
    </section>

    <!-- Quick Navigation Section -->
    <section class="navigation-section">
      <h2>快速导航</h2>
      <div class="nav-grid">
        <router-link to="/patterns" class="nav-card">
          <div class="nav-icon">📚</div>
          <h3>纹样资源库</h3>
          <p>浏览四类纹样的完整资源库</p>
        </router-link>
        <router-link to="/science" class="nav-card">
          <div class="nav-icon">🎓</div>
          <h3>文化科普</h3>
          <p>深入了解纹样的文化背景和应用场景</p>
        </router-link>
        <router-link to="/works" class="nav-card">
          <div class="nav-icon">🎨</div>
          <h3>原创作品</h3>
          <p>欣赏用户创作的原创设计作品</p>
        </router-link>
        <router-link v-if="isLoggedIn" to="/user-center" class="nav-card">
          <div class="nav-icon">👤</div>
          <h3>个人中心</h3>
          <p>管理个人资料和收藏夹</p>
        </router-link>
        <router-link v-else to="/login" class="nav-card">
          <div class="nav-icon">🔐</div>
          <h3>登录注册</h3>
          <p>登录账号以使用更多功能</p>
        </router-link>
      </div>
    </section>

    <!-- Platform Introduction Section -->
    <section class="intro-section">
      <div class="intro-content">
        <h2>关于平台</h2>
        <p>
          纳西族纹样数字化展示平台是一个致力于传播和保护纳西族文化遗产的Web应用系统。
          平台以七星纹、东巴衍生纹、日月纹、云纹水纹四类核心纹样为研究对象，
          提供纹样资源库、文化科普、用户互动等功能。
        </p>
        
        <h3>平台特色</h3>
        <ul class="features">
          <li>
            <strong>完整的纹样资源库</strong>：展示四类纹样的高清素材和详细信息
          </li>
          <li>
            <strong>深度文化解读</strong>：提供纹样的起源、演变、文化内涵等科普内容
          </li>
          <li>
            <strong>用户互动功能</strong>：支持收藏、评论、提问等互动方式
          </li>
          <li>
            <strong>原创作品展示</strong>：用户可以上传基于纹样的创意设计作品
          </li>
          <li>
            <strong>响应式设计</strong>：支持桌面和移动设备的完美适配
          </li>
        </ul>

        <h3>使用指南</h3>
        <ol class="guide">
          <li>
            <strong>浏览纹样</strong>：进入"纹样资源库"，按类型或应用场景浏览纹样
          </li>
          <li>
            <strong>搜索纹样</strong>：使用搜索功能按名称或文化内涵查找纹样
          </li>
          <li>
            <strong>查看详情</strong>：点击纹样卡片查看高清图片和详细信息
          </li>
          <li>
            <strong>收藏纹样</strong>：登录后可以收藏感兴趣的纹样到个人收藏夹
          </li>
          <li>
            <strong>互动交流</strong>：在纹样详情页发布评论和提问，与其他用户交流
          </li>
          <li>
            <strong>上传作品</strong>：创建基于纹样的原创设计作品并分享给社区
          </li>
        </ol>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { patternAPI } from '../services/api'
import { useUserStore } from '../store'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const isLoggedIn = computed(() => userStore.isLoggedIn)

const carouselPatterns = ref([])
const currentSlide = ref(0)

// Fetch carousel patterns from API
const fetchCarouselPatterns = async () => {
  try {
    const response = await patternAPI.getPatterns({ 
      page: 0, 
      size: 4 
    })
    
    // Handle both direct array and paginated response
    if (Array.isArray(response)) {
      carouselPatterns.value = response
    } else if (response.data && Array.isArray(response.data)) {
      carouselPatterns.value = response.data
    } else if (response.content && Array.isArray(response.content)) {
      carouselPatterns.value = response.content
    }
    
    if (carouselPatterns.value.length === 0) {
      ElMessage.warning('暂无纹样数据')
    }
  } catch (error) {
    console.error('Failed to fetch carousel patterns:', error)
    ElMessage.error('加载纹样数据失败')
  }
}

// Carousel navigation
const nextSlide = () => {
  if (carouselPatterns.value.length > 0) {
    currentSlide.value = (currentSlide.value + 1) % carouselPatterns.value.length
  }
}

const previousSlide = () => {
  if (carouselPatterns.value.length > 0) {
    currentSlide.value = (currentSlide.value - 1 + carouselPatterns.value.length) % carouselPatterns.value.length
  }
}

// Auto-advance carousel every 5 seconds
onMounted(() => {
  fetchCarouselPatterns()
  
  const interval = setInterval(() => {
    if (carouselPatterns.value.length > 0) {
      nextSlide()
    }
  }, 5000)
  
  // Cleanup interval on component unmount
  return () => clearInterval(interval)
})
</script>

<style scoped>
.home {
  width: 100%;
}

/* Carousel Section */
.carousel-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 2rem 0;
}

.carousel-container {
  position: relative;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1rem;
}

.carousel {
  position: relative;
  width: 100%;
  height: 400px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
}

.carousel-item {
  position: relative;
  width: 100%;
  height: 100%;
}

.carousel-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.carousel-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.8), transparent);
  padding: 2rem;
  color: white;
}

.carousel-overlay h2 {
  margin: 0 0 0.5rem 0;
  font-size: 1.8rem;
}

.carousel-overlay p {
  margin: 0;
  font-size: 1rem;
  opacity: 0.9;
}

.carousel-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.1);
}

.carousel-btn {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background-color: rgba(255, 255, 255, 0.3);
  color: white;
  border: none;
  font-size: 2rem;
  padding: 1rem;
  cursor: pointer;
  border-radius: 4px;
  transition: background-color 0.3s;
  z-index: 10;
}

.carousel-btn:hover {
  background-color: rgba(255, 255, 255, 0.5);
}

.carousel-btn.prev {
  left: 1rem;
}

.carousel-btn.next {
  right: 1rem;
}

.carousel-indicators {
  display: flex;
  justify-content: center;
  gap: 0.5rem;
  margin-top: 1rem;
}

.carousel-indicators span {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.5);
  cursor: pointer;
  transition: background-color 0.3s;
}

.carousel-indicators span.active {
  background-color: white;
}

/* Slogans Section */
.slogans {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
  margin-top: 3rem;
  max-width: 1200px;
  margin-left: auto;
  margin-right: auto;
  padding: 0 1rem;
}

.slogan {
  text-align: center;
  padding: 1.5rem;
  background-color: rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  backdrop-filter: blur(10px);
}

.slogan h3 {
  margin: 0 0 0.5rem 0;
  font-size: 1.3rem;
}

.slogan p {
  margin: 0;
  font-size: 0.95rem;
  opacity: 0.9;
}

/* Navigation Section */
.navigation-section {
  padding: 3rem 1rem;
  max-width: 1200px;
  margin: 0 auto;
}

.navigation-section h2 {
  text-align: center;
  font-size: 2rem;
  margin-bottom: 2rem;
  color: #333;
}

.nav-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1.5rem;
}

.nav-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 2rem;
  background-color: #f5f5f5;
  border-radius: 12px;
  text-decoration: none;
  color: #333;
  transition: all 0.3s;
  border: 2px solid transparent;
}

.nav-card:hover {
  background-color: #fff;
  border-color: #667eea;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
  transform: translateY(-5px);
}

.nav-icon {
  font-size: 2.5rem;
  margin-bottom: 1rem;
}

.nav-card h3 {
  margin: 0 0 0.5rem 0;
  font-size: 1.1rem;
  text-align: center;
}

.nav-card p {
  margin: 0;
  font-size: 0.9rem;
  color: #666;
  text-align: center;
}

/* Introduction Section */
.intro-section {
  background-color: #f9f9f9;
  padding: 3rem 1rem;
}

.intro-content {
  max-width: 1200px;
  margin: 0 auto;
}

.intro-content h2 {
  font-size: 2rem;
  color: #333;
  margin-bottom: 1rem;
}

.intro-content > p {
  font-size: 1rem;
  line-height: 1.6;
  color: #666;
  margin-bottom: 2rem;
}

.intro-content h3 {
  font-size: 1.3rem;
  color: #333;
  margin-top: 2rem;
  margin-bottom: 1rem;
}

.features {
  list-style: none;
  padding: 0;
  margin: 0 0 2rem 0;
}

.features li {
  padding: 0.8rem 0;
  padding-left: 2rem;
  position: relative;
  color: #666;
  line-height: 1.6;
}

.features li:before {
  content: "✓";
  position: absolute;
  left: 0;
  color: #667eea;
  font-weight: bold;
  font-size: 1.2rem;
}

.guide {
  padding-left: 2rem;
  color: #666;
  line-height: 1.8;
}

.guide li {
  margin-bottom: 1rem;
}

.guide strong {
  color: #333;
}

/* Responsive Design */
@media (max-width: 1199px) {
  .carousel {
    height: 350px;
  }

  .carousel-overlay h2 {
    font-size: 1.5rem;
  }

  .carousel-overlay p {
    font-size: 0.95rem;
  }

  .slogans {
    gap: 1.5rem;
    margin-top: 2.5rem;
  }

  .nav-grid {
    grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  }

  .intro-content h2 {
    font-size: 1.8rem;
  }

  .intro-content h3 {
    font-size: 1.2rem;
  }
}

@media (max-width: 991px) {
  .carousel-section {
    padding: 1.5rem 0;
  }

  .carousel {
    height: 300px;
  }

  .carousel-overlay {
    padding: 1.5rem;
  }

  .carousel-overlay h2 {
    font-size: 1.3rem;
  }

  .carousel-overlay p {
    font-size: 0.9rem;
  }

  .carousel-btn {
    padding: 0.75rem;
    font-size: 1.3rem;
  }

  .slogans {
    gap: 1.25rem;
    margin-top: 2rem;
  }

  .slogan {
    padding: 1.25rem;
  }

  .slogan h3 {
    font-size: 1.2rem;
  }

  .slogan p {
    font-size: 0.9rem;
  }

  .navigation-section {
    padding: 2.5rem 1rem;
  }

  .navigation-section h2 {
    font-size: 1.8rem;
    margin-bottom: 1.5rem;
  }

  .nav-grid {
    grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
    gap: 1.25rem;
  }

  .nav-card {
    padding: 1.5rem;
  }

  .nav-icon {
    font-size: 2.2rem;
  }

  .nav-card h3 {
    font-size: 1rem;
  }

  .nav-card p {
    font-size: 0.85rem;
  }

  .intro-section {
    padding: 2.5rem 1rem;
  }

  .intro-content h2 {
    font-size: 1.7rem;
  }

  .intro-content h3 {
    font-size: 1.15rem;
  }

  .intro-content > p {
    font-size: 0.95rem;
  }
}

@media (max-width: 767px) {
  .carousel-section {
    padding: 1rem 0;
  }

  .carousel {
    height: 220px;
  }

  .carousel-overlay {
    padding: 1rem;
  }

  .carousel-overlay h2 {
    font-size: 1.1rem;
    margin: 0 0 0.3rem 0;
  }

  .carousel-overlay p {
    font-size: 0.8rem;
  }

  .carousel-btn {
    padding: 0.5rem;
    font-size: 1.2rem;
  }

  .carousel-indicators {
    gap: 0.4rem;
    margin-top: 0.75rem;
  }

  .carousel-indicators span {
    width: 10px;
    height: 10px;
  }

  .slogans {
    gap: 1rem;
    margin-top: 1.5rem;
  }

  .slogan {
    padding: 1rem;
  }

  .slogan h3 {
    font-size: 1.05rem;
    margin: 0 0 0.4rem 0;
  }

  .slogan p {
    font-size: 0.85rem;
    margin: 0;
  }

  .navigation-section {
    padding: 2rem 1rem;
  }

  .navigation-section h2 {
    font-size: 1.5rem;
    margin-bottom: 1.25rem;
  }

  .nav-grid {
    grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
    gap: 1rem;
  }

  .nav-card {
    padding: 1.25rem;
  }

  .nav-icon {
    font-size: 2rem;
    margin-bottom: 0.75rem;
  }

  .nav-card h3 {
    font-size: 0.95rem;
    margin: 0 0 0.4rem 0;
  }

  .nav-card p {
    font-size: 0.8rem;
    margin: 0;
  }

  .intro-section {
    padding: 2rem 1rem;
  }

  .intro-content h2 {
    font-size: 1.4rem;
    margin-bottom: 0.75rem;
  }

  .intro-content > p {
    font-size: 0.9rem;
    margin-bottom: 1.5rem;
  }

  .intro-content h3 {
    font-size: 1.05rem;
    margin-top: 1.5rem;
    margin-bottom: 0.75rem;
  }

  .features li {
    padding: 0.6rem 0;
    padding-left: 1.5rem;
    font-size: 0.9rem;
  }

  .guide {
    padding-left: 1.5rem;
    font-size: 0.9rem;
    line-height: 1.6;
  }

  .guide li {
    margin-bottom: 0.75rem;
  }
}

@media (max-width: 479px) {
  .carousel-section {
    padding: 0.75rem 0;
  }

  .carousel-container {
    padding: 0 0.5rem;
  }

  .carousel {
    height: 180px;
  }

  .carousel-overlay {
    padding: 0.75rem;
  }

  .carousel-overlay h2 {
    font-size: 1rem;
    margin: 0 0 0.2rem 0;
  }

  .carousel-overlay p {
    font-size: 0.75rem;
  }

  .carousel-btn {
    padding: 0.4rem;
    font-size: 1rem;
  }

  .carousel-btn.prev {
    left: 0.5rem;
  }

  .carousel-btn.next {
    right: 0.5rem;
  }

  .carousel-indicators {
    gap: 0.3rem;
    margin-top: 0.5rem;
  }

  .carousel-indicators span {
    width: 8px;
    height: 8px;
  }

  .slogans {
    gap: 0.75rem;
    margin-top: 1rem;
    padding: 0 0.5rem;
  }

  .slogan {
    padding: 0.75rem;
  }

  .slogan h3 {
    font-size: 0.95rem;
    margin: 0 0 0.3rem 0;
  }

  .slogan p {
    font-size: 0.8rem;
    margin: 0;
  }

  .navigation-section {
    padding: 1.5rem 0.5rem;
  }

  .navigation-section h2 {
    font-size: 1.3rem;
    margin-bottom: 1rem;
  }

  .nav-grid {
    grid-template-columns: 1fr;
    gap: 0.75rem;
  }

  .nav-card {
    padding: 1rem;
  }

  .nav-icon {
    font-size: 1.8rem;
    margin-bottom: 0.5rem;
  }

  .nav-card h3 {
    font-size: 0.9rem;
    margin: 0 0 0.3rem 0;
  }

  .nav-card p {
    font-size: 0.75rem;
    margin: 0;
  }

  .intro-section {
    padding: 1.5rem 0.5rem;
  }

  .intro-content h2 {
    font-size: 1.2rem;
    margin-bottom: 0.5rem;
  }

  .intro-content > p {
    font-size: 0.85rem;
    margin-bottom: 1rem;
  }

  .intro-content h3 {
    font-size: 0.95rem;
    margin-top: 1rem;
    margin-bottom: 0.5rem;
  }

  .features li {
    padding: 0.5rem 0;
    padding-left: 1.25rem;
    font-size: 0.85rem;
  }

  .features li:before {
    font-size: 1rem;
  }

  .guide {
    padding-left: 1.25rem;
    font-size: 0.85rem;
    line-height: 1.5;
  }

  .guide li {
    margin-bottom: 0.5rem;
  }
}
</style>
