<template>
  <div class="pattern-detail">
    <!-- Header -->
    <Header />

    <!-- Back Navigation -->
    <div class="back-nav">
      <button @click="goBack" class="back-btn">
        <span class="icon">←</span> 返回列表
      </button>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="5" animated />
    </div>

    <!-- Pattern Detail Content -->
    <div v-else-if="pattern" class="detail-container">
      <!-- Main Content -->
      <div class="detail-main">
        <!-- Image Section -->
        <div class="image-section">
          <img :src="pattern.imageUrl" :alt="pattern.name" class="pattern-image" />
          <div class="image-actions">
            <button 
              v-if="isLoggedIn"
              :class="['collect-btn', { collected: isCollected }]"
              @click="toggleCollection"
              :disabled="collectingLoading"
            >
              <span class="icon">♥</span>
              {{ isCollected ? '已收藏' : '收藏' }}
            </button>
            <button 
              class="download-btn"
              @click="downloadPattern"
              :disabled="downloadLoading"
            >
              <span class="icon">⬇</span>
              {{ downloadLoading ? '下载中...' : '下载' }}
            </button>
          </div>
        </div>

        <!-- Info Section -->
        <div class="info-section">
          <!-- Basic Info -->
          <div class="basic-info">
            <h1>{{ pattern.name }}</h1>
            <div class="meta-info">
              <span class="category">{{ pattern.category }}</span>
              <span class="stats">
                <span>👁 {{ pattern.viewCount || 0 }} 次浏览</span>
                <span>⬇ {{ pattern.downloadCount || 0 }} 次下载</span>
                <span>♥ {{ pattern.collectionCount || 0 }} 次收藏</span>
              </span>
            </div>
          </div>

          <!-- Cultural Background -->
          <div class="section">
            <h2>文化背景</h2>
            <p class="content">{{ pattern.culturalBackground || '暂无信息' }}</p>
          </div>

          <!-- Application Scenarios -->
          <div class="section">
            <h2>应用场景</h2>
            <p class="content">{{ pattern.applicationScenarios || '暂无信息' }}</p>
          </div>

          <!-- Description -->
          <div v-if="pattern.description" class="section">
            <h2>纹样描述</h2>
            <p class="content">{{ pattern.description }}</p>
          </div>
        </div>
      </div>

      <!-- Sidebar -->
      <div class="detail-sidebar">
        <!-- Login Prompt -->
        <div v-if="!isLoggedIn" class="login-prompt">
          <p>登录后可以收藏和评论</p>
          <router-link to="/login" class="login-link">立即登录</router-link>
        </div>

        <!-- Quick Stats -->
        <div class="quick-stats">
          <div class="stat-item">
            <div class="stat-value">{{ pattern.viewCount || 0 }}</div>
            <div class="stat-label">浏览</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ pattern.downloadCount || 0 }}</div>
            <div class="stat-label">下载</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ pattern.collectionCount || 0 }}</div>
            <div class="stat-label">收藏</div>
          </div>
        </div>
      </div>
    </div>

    <!-- Error State -->
    <div v-else class="error-container">
      <p>纹样不存在或加载失败</p>
      <button @click="goBack" class="back-btn">返回列表</button>
    </div>

    <!-- Footer -->
    <Footer />

    <!-- Comments Section -->
    <div v-if="pattern" class="comments-section">
      <CommentList :patternId="pattern.id" :patternName="pattern.name" />
    </div>

    <!-- Questions Section -->
    <div v-if="pattern" class="questions-section">
      <QuestionList :patternId="pattern.id" :patternName="pattern.name" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../store'
import { patternAPI, collectionAPI } from '../services/api'
import { operationLogService } from '../services/operationLog'
import { ElMessage } from 'element-plus'
import Header from '../components/Header.vue'
import Footer from '../components/Footer.vue'
import CommentList from '../components/CommentList.vue'
import QuestionList from '../components/QuestionList.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// State
const pattern = ref(null)
const loading = ref(false)
const isCollected = ref(false)
const collectingLoading = ref(false)
const downloadLoading = ref(false)

// Computed
const isLoggedIn = computed(() => userStore.isLoggedIn)

// Methods
const fetchPattern = async () => {
  loading.value = true
  try {
    const patternId = route.params.id
    const response = await patternAPI.getPattern(patternId)
    pattern.value = response.data || response
    
    // Record view operation
    if (isLoggedIn.value && pattern.value) {
      operationLogService.recordView(pattern.value.id, pattern.value.name)
    }
    
    // Check if collected
    if (isLoggedIn.value) {
      checkIfCollected()
    }
  } catch (error) {
    console.error('Failed to fetch pattern:', error)
    ElMessage.error('加载纹样详情失败')
  } finally {
    loading.value = false
  }
}

const checkIfCollected = async () => {
  try {
    const collections = await collectionAPI.getCollections({
      patternId: pattern.value.id
    })
    isCollected.value = collections && collections.length > 0
  } catch (error) {
    console.error('Failed to check collection status:', error)
  }
}

const toggleCollection = async () => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  collectingLoading.value = true
  try {
    if (isCollected.value) {
      // Delete collection
      const collections = await collectionAPI.getCollections({
        patternId: pattern.value.id
      })
      if (collections && collections.length > 0) {
        await collectionAPI.deleteCollection(collections[0].id)
        isCollected.value = false
        pattern.value.collectionCount = Math.max(0, (pattern.value.collectionCount || 1) - 1)
        ElMessage.success('已取消收藏')
        
        // Record collection operation
        operationLogService.recordCollection(pattern.value.id, pattern.value.name, 'remove')
      }
    } else {
      // Add collection
      await collectionAPI.addCollection({
        patternId: pattern.value.id
      })
      isCollected.value = true
      pattern.value.collectionCount = (pattern.value.collectionCount || 0) + 1
      ElMessage.success('收藏成功')
      
      // Record collection operation
      operationLogService.recordCollection(pattern.value.id, pattern.value.name, 'add')
    }
  } catch (error) {
    console.error('Failed to toggle collection:', error)
    ElMessage.error('操作失败，请重试')
  } finally {
    collectingLoading.value = false
  }
}

const downloadPattern = async () => {
  downloadLoading.value = true
  try {
    const response = await patternAPI.downloadPattern(pattern.value.id)
    
    // If response contains a download URL
    if (response && response.downloadUrl) {
      window.open(response.downloadUrl, '_blank')
    } else if (response && response.data && response.data.downloadUrl) {
      window.open(response.data.downloadUrl, '_blank')
    } else if (pattern.value.downloadUrl) {
      window.open(pattern.value.downloadUrl, '_blank')
    } else {
      ElMessage.error('下载链接不可用')
    }
    
    // Update download count
    pattern.value.downloadCount = (pattern.value.downloadCount || 0) + 1
    ElMessage.success('下载开始')
    
    // Record download operation
    if (isLoggedIn.value) {
      operationLogService.recordDownload(pattern.value.id, pattern.value.name)
    }
  } catch (error) {
    console.error('Failed to download pattern:', error)
    ElMessage.error('下载失败，请重试')
  } finally {
    downloadLoading.value = false
  }
}

const goBack = () => {
  router.back()
}

// Lifecycle
onMounted(() => {
  fetchPattern()
})
</script>

<style scoped>
.pattern-detail {
  min-height: 100vh;
  background-color: #f5f5f5;
  display: flex;
  flex-direction: column;
}

/* Back Navigation */
.back-nav {
  background-color: white;
  padding: 1rem;
  border-bottom: 1px solid #eee;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
}

.back-btn {
  padding: 0.5rem 1rem;
  background-color: #f0f0f0;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  color: #333;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.back-btn:hover {
  background-color: #e0e0e0;
  border-color: #999;
}

.back-btn .icon {
  font-size: 1.2rem;
}

/* Loading Container */
.loading-container {
  flex: 1;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
  padding: 2rem 1rem;
}

/* Detail Container */
.detail-container {
  flex: 1;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
  padding: 2rem 1rem;
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: 2rem;
}

/* Main Content */
.detail-main {
  background-color: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* Image Section */
.image-section {
  position: relative;
  background-color: #f9f9f9;
  padding: 2rem;
  text-align: center;
}

.pattern-image {
  max-width: 100%;
  max-height: 500px;
  object-fit: contain;
  border-radius: 4px;
  margin-bottom: 1.5rem;
}

.image-actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
  flex-wrap: wrap;
}

.collect-btn,
.download-btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  white-space: nowrap;
}

.collect-btn {
  background-color: #f0f0f0;
  color: #333;
  border: 1px solid #ddd;
}

.collect-btn:hover:not(:disabled) {
  background-color: #e0e0e0;
  border-color: #999;
}

.collect-btn.collected {
  background-color: #ff6b6b;
  color: white;
  border-color: #ff6b6b;
}

.collect-btn.collected:hover:not(:disabled) {
  background-color: #ff5252;
  border-color: #ff5252;
}

.download-btn {
  background-color: #667eea;
  color: white;
}

.download-btn:hover:not(:disabled) {
  background-color: #5568d3;
}

.collect-btn:disabled,
.download-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.collect-btn .icon,
.download-btn .icon {
  font-size: 1.2rem;
}

/* Info Section */
.info-section {
  padding: 2rem;
}

.basic-info {
  margin-bottom: 2rem;
  border-bottom: 1px solid #eee;
  padding-bottom: 1.5rem;
}

.basic-info h1 {
  font-size: 2rem;
  color: #333;
  margin: 0 0 1rem 0;
}

.meta-info {
  display: flex;
  align-items: center;
  gap: 1.5rem;
  flex-wrap: wrap;
}

.category {
  display: inline-block;
  background-color: #667eea;
  color: white;
  padding: 0.4rem 0.8rem;
  border-radius: 20px;
  font-size: 0.9rem;
}

.stats {
  display: flex;
  gap: 1.5rem;
  font-size: 0.95rem;
  color: #666;
}

.stats span {
  display: flex;
  align-items: center;
  gap: 0.3rem;
}

/* Sections */
.section {
  margin-bottom: 2rem;
}

.section h2 {
  font-size: 1.3rem;
  color: #333;
  margin: 0 0 1rem 0;
  border-bottom: 2px solid #667eea;
  padding-bottom: 0.5rem;
}

.section .content {
  color: #555;
  line-height: 1.8;
  margin: 0;
  white-space: pre-wrap;
  word-break: break-word;
}

/* Sidebar */
.detail-sidebar {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.login-prompt {
  background-color: white;
  border-radius: 8px;
  padding: 1.5rem;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.login-prompt p {
  color: #666;
  margin: 0 0 1rem 0;
}

.login-link {
  display: inline-block;
  background-color: #667eea;
  color: white;
  padding: 0.75rem 1.5rem;
  border-radius: 4px;
  text-decoration: none;
  transition: background-color 0.3s;
}

.login-link:hover {
  background-color: #5568d3;
}

.quick-stats {
  background-color: white;
  border-radius: 8px;
  padding: 1.5rem;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 1.8rem;
  font-weight: bold;
  color: #667eea;
  margin-bottom: 0.5rem;
}

.stat-label {
  font-size: 0.9rem;
  color: #666;
}

/* Error Container */
.error-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 1.5rem;
  padding: 2rem;
}

.error-container p {
  font-size: 1.1rem;
  color: #666;
}

/* Responsive Design */
@media (max-width: 1199px) {
  .detail-container {
    grid-template-columns: 1fr 280px;
    gap: 1.5rem;
    padding: 1.5rem 1rem;
  }

  .image-section {
    padding: 1.5rem;
  }

  .pattern-image {
    max-height: 450px;
  }

  .info-section {
    padding: 1.5rem;
  }

  .basic-info h1 {
    font-size: 1.8rem;
  }

  .section h2 {
    font-size: 1.2rem;
  }

  .quick-stats {
    padding: 1.25rem;
  }

  .stat-value {
    font-size: 1.6rem;
  }
}

@media (max-width: 991px) {
  .detail-container {
    grid-template-columns: 1fr;
    gap: 1.25rem;
    padding: 1.25rem 1rem;
  }

  .detail-sidebar {
    display: flex;
    flex-direction: row;
    gap: 1rem;
  }

  .login-prompt {
    flex: 1;
  }

  .quick-stats {
    flex: 1;
  }

  .back-nav {
    padding: 0.75rem;
  }

  .back-btn {
    padding: 0.45rem 0.9rem;
    font-size: 0.95rem;
  }

  .image-section {
    padding: 1.25rem;
  }

  .pattern-image {
    max-height: 400px;
    margin-bottom: 1.25rem;
  }

  .image-actions {
    gap: 0.75rem;
  }

  .collect-btn,
  .download-btn {
    padding: 0.65rem 1.25rem;
    font-size: 0.95rem;
  }

  .info-section {
    padding: 1.25rem;
  }

  .basic-info {
    margin-bottom: 1.5rem;
    padding-bottom: 1.25rem;
  }

  .basic-info h1 {
    font-size: 1.6rem;
    margin: 0 0 0.75rem 0;
  }

  .meta-info {
    gap: 1.25rem;
  }

  .stats {
    gap: 1rem;
    font-size: 0.9rem;
  }

  .section {
    margin-bottom: 1.5rem;
  }

  .section h2 {
    font-size: 1.15rem;
    margin: 0 0 0.75rem 0;
  }

  .section .content {
    font-size: 0.95rem;
  }

  .quick-stats {
    padding: 1.25rem;
    grid-template-columns: repeat(3, 1fr);
  }

  .stat-value {
    font-size: 1.5rem;
  }

  .stat-label {
    font-size: 0.85rem;
  }
}

@media (max-width: 767px) {
  .pattern-detail {
    padding: 0;
  }

  .back-nav {
    padding: 0.5rem;
  }

  .back-btn {
    padding: 0.4rem 0.8rem;
    font-size: 0.9rem;
  }

  .detail-container {
    grid-template-columns: 1fr;
    gap: 1rem;
    padding: 1rem;
  }

  .detail-sidebar {
    flex-direction: column;
    gap: 1rem;
  }

  .image-section {
    padding: 1rem;
  }

  .pattern-image {
    max-height: 300px;
    margin-bottom: 1rem;
  }

  .image-actions {
    gap: 0.5rem;
    flex-wrap: wrap;
  }

  .collect-btn,
  .download-btn {
    padding: 0.6rem 1rem;
    font-size: 0.9rem;
    flex: 1;
    min-width: 120px;
  }

  .info-section {
    padding: 1rem;
  }

  .basic-info {
    margin-bottom: 1.25rem;
    padding-bottom: 1rem;
  }

  .basic-info h1 {
    font-size: 1.4rem;
    margin: 0 0 0.6rem 0;
  }

  .meta-info {
    gap: 0.75rem;
    flex-wrap: wrap;
  }

  .category {
    padding: 0.35rem 0.7rem;
    font-size: 0.85rem;
  }

  .stats {
    gap: 0.75rem;
    font-size: 0.85rem;
  }

  .stats span {
    gap: 0.2rem;
  }

  .section {
    margin-bottom: 1.25rem;
  }

  .section h2 {
    font-size: 1.05rem;
    margin: 0 0 0.6rem 0;
  }

  .section .content {
    font-size: 0.9rem;
    line-height: 1.6;
  }

  .login-prompt {
    padding: 1.25rem;
  }

  .login-prompt p {
    margin: 0 0 0.75rem 0;
    font-size: 0.9rem;
  }

  .login-link {
    padding: 0.6rem 1.25rem;
    font-size: 0.9rem;
  }

  .quick-stats {
    padding: 1.25rem;
    grid-template-columns: repeat(3, 1fr);
    gap: 0.75rem;
  }

  .stat-value {
    font-size: 1.4rem;
  }

  .stat-label {
    font-size: 0.8rem;
  }

  .comments-section {
    padding: 0 0.75rem 1.5rem 0.75rem;
  }

  .questions-section {
    padding: 0 0.75rem 1.5rem 0.75rem;
  }
}

@media (max-width: 479px) {
  .back-nav {
    padding: 0.4rem;
  }

  .back-btn {
    padding: 0.35rem 0.7rem;
    font-size: 0.8rem;
  }

  .detail-container {
    gap: 0.75rem;
    padding: 0.75rem;
  }

  .image-section {
    padding: 0.75rem;
  }

  .pattern-image {
    max-height: 200px;
    margin-bottom: 0.75rem;
  }

  .image-actions {
    gap: 0.4rem;
  }

  .collect-btn,
  .download-btn {
    padding: 0.5rem 0.8rem;
    font-size: 0.8rem;
    min-width: 100px;
  }

  .collect-btn .icon,
  .download-btn .icon {
    font-size: 1rem;
  }

  .info-section {
    padding: 0.75rem;
  }

  .basic-info {
    margin-bottom: 1rem;
    padding-bottom: 0.75rem;
  }

  .basic-info h1 {
    font-size: 1.2rem;
    margin: 0 0 0.5rem 0;
  }

  .meta-info {
    gap: 0.5rem;
  }

  .category {
    padding: 0.3rem 0.6rem;
    font-size: 0.75rem;
  }

  .stats {
    gap: 0.5rem;
    font-size: 0.75rem;
    flex-direction: column;
    align-items: flex-start;
  }

  .stats span {
    gap: 0.2rem;
  }

  .section {
    margin-bottom: 1rem;
  }

  .section h2 {
    font-size: 0.95rem;
    margin: 0 0 0.5rem 0;
  }

  .section .content {
    font-size: 0.85rem;
    line-height: 1.5;
  }

  .login-prompt {
    padding: 1rem;
  }

  .login-prompt p {
    margin: 0 0 0.6rem 0;
    font-size: 0.85rem;
  }

  .login-link {
    padding: 0.5rem 1rem;
    font-size: 0.8rem;
  }

  .quick-stats {
    padding: 1rem;
    grid-template-columns: repeat(3, 1fr);
    gap: 0.5rem;
  }

  .stat-value {
    font-size: 1.2rem;
  }

  .stat-label {
    font-size: 0.7rem;
  }

  .comments-section {
    padding: 0 0.5rem 1rem 0.5rem;
  }

  .questions-section {
    padding: 0 0.5rem 1rem 0.5rem;
  }
}
</style>
