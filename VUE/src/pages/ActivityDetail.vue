<template>
  <div class="activity-detail-page">
    <!-- Top Navigation -->
    <TopNavigation />

    <!-- Registration Form Modal -->
    <RegistrationForm
      v-if="showRegistrationForm"
      :activity="activity"
      @close="showRegistrationForm = false"
      @success="handleRegistrationSuccess"
    />

    <div v-if="loading" class="loading-state">
      <p>加载中...</p>
    </div>

    <div v-else-if="activity" class="activity-detail-container">
      <!-- Back Button -->
      <button @click="goBack" class="btn-back">← 返回列表</button>

      <!-- Activity Header -->
      <div class="activity-header">
        <img
          :src="activity.coverUrl || 'https://via.placeholder.com/800x400?text=No+Image'"
          :alt="activity.title"
          class="cover-image"
        />
        <div class="header-overlay">
          <h1>{{ activity.title }}</h1>
          <div class="header-meta">
            <span class="type-badge">{{ getActivityTypeLabel(activity.type) }}</span>
            <span class="status-badge">{{ getStatusLabel(activity.status) }}</span>
          </div>
        </div>
      </div>

      <!-- Activity Content -->
      <div class="activity-content">
        <!-- Main Info -->
        <div class="main-section">
          <div class="info-grid">
            <div class="info-item">
              <span class="info-label">📅 开始时间</span>
              <span class="info-value">{{ formatDateTime(activity.startTime) }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">📅 结束时间</span>
              <span class="info-value">{{ formatDateTime(activity.endTime) }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">📍 地点</span>
              <span class="info-value">{{ activity.location || '未指定' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">👥 参与人数限制</span>
              <span class="info-value">{{ activity.maxParticipants || '无限制' }}</span>
            </div>
          </div>

          <!-- Description -->
          <div class="description-section">
            <h2>活动描述</h2>
            <p>{{ activity.description || '暂无描述' }}</p>
          </div>

          <!-- Feedback Section -->
          <div class="feedback-section">
            <h2>活动反馈</h2>
            <div v-if="feedbackLoading" class="loading">
              <p>加载反馈中...</p>
            </div>
            <div v-else-if="feedbackList.length === 0" class="empty-state">
              <p>暂无反馈</p>
            </div>
            <div v-else class="feedback-list">
              <div v-for="feedback in feedbackList" :key="feedback.id" class="feedback-item">
                <div class="feedback-header">
                  <div class="user-info">
                    <span class="user-name">{{ feedback.user?.nickname || '用户' }}</span>
                    <span class="feedback-date">{{ formatDate(feedback.createdAt) }}</span>
                  </div>
                  <div class="rating-display">
                    <span v-for="i in 5" :key="i" class="star" :class="{ filled: i <= feedback.rating }">★</span>
                  </div>
                </div>
                <div class="feedback-content">
                  {{ feedback.content }}
                </div>
                <div v-if="feedback.replyContent" class="feedback-reply">
                  <div class="reply-header">
                    <strong>组织者回复：</strong>
                  </div>
                  <div class="reply-content">
                    {{ feedback.replyContent }}
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Progress Bars -->
          <div class="progress-section">
            <h2>活动进度</h2>
            
            <!-- Registration Progress -->
            <div class="progress-item">
              <div class="progress-label">
                <span>报名进度</span>
                <span class="progress-percentage">{{ registrationPercentage }}%</span>
              </div>
              <div class="progress-bar">
                <div
                  class="progress-fill registration-fill"
                  :style="{ width: registrationPercentage + '%' }"
                ></div>
              </div>
            </div>

            <!-- Crowdfunding Progress -->
            <div v-if="activity.enableCrowdfunding" class="progress-item">
              <div class="progress-label">
                <span>众筹进度</span>
                <span class="progress-percentage">{{ crowdfundingPercentage }}%</span>
              </div>
              <div class="progress-bar">
                <div
                  class="progress-fill crowdfunding-fill"
                  :style="{ width: crowdfundingPercentage + '%' }"
                ></div>
              </div>
            </div>
          </div>
        </div>

        <!-- Sidebar -->
        <div class="sidebar">
          <!-- Registration Card -->
          <div class="card">
            <h3>报名信息</h3>
            <div class="card-content">
              <p class="card-stat">
                <span class="stat-label">已报名:</span>
                <span class="stat-value">{{ registrationCount }} / {{ activity.maxParticipants || '无限制' }}</span>
              </p>
              <button class="btn-register" @click="openRegistrationForm">立刻报名</button>
            </div>
          </div>

          <!-- Crowdfunding Card -->
          <div v-if="activity.enableCrowdfunding" class="card">
            <h3>众筹信息</h3>
            <div class="card-content">
              <p class="card-stat">
                <span class="stat-label">目标金额:</span>
                <span class="stat-value">¥{{ activity.crowdfundingTarget }}</span>
              </p>
              <p class="card-stat">
                <span class="stat-label">已筹金额:</span>
                <span class="stat-value">¥{{ crowdfundingAmount }}</span>
              </p>
              <button class="btn-support" @click="goToCrowdfundingPayment">支持众筹</button>
            </div>
          </div>

          <!-- Crowdfunding Tiers Card -->
          <div v-if="activity.enableCrowdfunding && crowdfundingTiers.length > 0" class="card">
            <h3>众筹档位</h3>
            <div class="card-content">
              <div v-for="tier in crowdfundingTiers" :key="tier.id" class="tier-item">
                <div class="tier-header">
                  <span class="tier-name">{{ tier.name }}</span>
                  <span class="tier-amount">¥{{ tier.amount }}</span>
                </div>
                <p v-if="tier.description" class="tier-description">{{ tier.description }}</p>
              </div>
            </div>
          </div>

          <!-- Organizer Card -->
          <div class="card">
            <h3>组织者</h3>
            <div class="card-content">
              <p class="organizer-name">{{ organizerName }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-else class="error-state">
      <p>活动不存在</p>
      <button @click="goBack" class="btn-back">返回列表</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import TopNavigation from '../components/TopNavigation.vue'
import RegistrationForm from '../components/RegistrationForm.vue'
import http from '../utils/http'

const router = useRouter()
const route = useRoute()

const activity = ref(null)
const loading = ref(true)
const showRegistrationForm = ref(false)
const crowdfundingTiers = ref([])
const feedbackList = ref([])
const feedbackLoading = ref(false)
let refreshInterval = null

/**
 * Fetch activity details
 */
const fetchActivityDetail = async () => {
  try {
    const activityId = route.params.id
    const response = await http.get(`/activities/${activityId}`)
    activity.value = response.data
    
    // Fetch crowdfunding tiers if crowdfunding is enabled
    if (activity.value.enableCrowdfunding) {
      await fetchCrowdfundingTiers(activityId)
    }

    // Fetch feedback
    await fetchActivityFeedback(activityId)
  } catch (error) {
    ElMessage.error(error.message || '获取活动详情失败')
    if (loading.value) {
      loading.value = false
    }
  }
}

/**
 * Fetch crowdfunding tiers
 */
const fetchCrowdfundingTiers = async (activityId) => {
  try {
    const response = await http.get(`/crowdfunding/tiers/${activityId}`)
    crowdfundingTiers.value = response.data
  } catch (error) {
    console.error('Failed to fetch crowdfunding tiers:', error)
    crowdfundingTiers.value = []
  }
}

/**
 * Fetch feedback for activity
 */
const fetchActivityFeedback = async (activityId) => {
  feedbackLoading.value = true
  try {
    const response = await http.get(`/feedback/activity/${activityId}`, {
      params: {
        page: 0,
        size: 5
      }
    })
    feedbackList.value = response.data.content || []
  } catch (error) {
    console.error('Failed to fetch feedback:', error)
    feedbackList.value = []
  } finally {
    feedbackLoading.value = false
  }
}

/**
 * Get activity type label
 */
const getActivityTypeLabel = (type) => {
  const typeMap = {
    lecture: '讲座',
    competition: '比赛',
    charity: '公益',
    other: '其他'
  }
  return typeMap[type] || type || '活动'
}

/**
 * Get status label
 */
const getStatusLabel = (status) => {
  const statusMap = {
    DRAFT: '草稿',
    PENDING_AUDIT: '待审核',
    APPROVED: '已批准',
    REJECTED: '已驳回',
    ONGOING: '进行中',
    ENDED: '已结束',
    ARCHIVED: '已归档'
  }
  return statusMap[status] || status
}

/**
 * Format datetime
 */
const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  const date = new Date(dateTime)
  return date.toLocaleDateString('zh-CN') + ' ' + date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

/**
 * Format date
 */
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

/**
 * Get registration percentage
 */
const registrationPercentage = computed(() => {
  if (!activity.value || !activity.value.maxParticipants || activity.value.maxParticipants === 0) {
    return 0
  }
  return Math.round((activity.value.registrationCount || 0) / activity.value.maxParticipants * 100)
})

/**
 * Get crowdfunding percentage
 */
const crowdfundingPercentage = computed(() => {
  if (!activity.value || !activity.value.crowdfundingTarget || activity.value.crowdfundingTarget === 0) {
    return 0
  }
  return Math.round((activity.value.crowdfundingAmount || 0) / activity.value.crowdfundingTarget * 100)
})

/**
 * Get registration count
 */
const registrationCount = computed(() => {
  return activity.value?.registrationCount || 0
})

/**
 * Get crowdfunding amount
 */
const crowdfundingAmount = computed(() => {
  return activity.value?.crowdfundingAmount || 0
})

/**
 * Get organizer name
 */
const organizerName = computed(() => {
  return activity.value?.organizerName || '未知'
})

/**
 * Go back to activity list
 */
const goBack = () => {
  router.push('/activities')
}

/**
 * Go to crowdfunding payment page
 */
const goToCrowdfundingPayment = () => {
  router.push({
    name: 'CrowdfundingPayment',
    params: { activityId: activity.value.id }
  })
}

/**
 * Open registration form
 */
const openRegistrationForm = () => {
  showRegistrationForm.value = true
}

/**
 * Handle registration success
 */
const handleRegistrationSuccess = () => {
  showRegistrationForm.value = false
  // Refresh activity details to update registration count
  fetchActivityDetail()
}

/**
 * Start auto-refresh timer (every 30 seconds)
 */
const startAutoRefresh = () => {
  refreshInterval = setInterval(() => {
    fetchActivityDetail()
  }, 30000) // 30 seconds
}

/**
 * Stop auto-refresh timer
 */
const stopAutoRefresh = () => {
  if (refreshInterval) {
    clearInterval(refreshInterval)
    refreshInterval = null
  }
}

onMounted(async () => {
  await fetchActivityDetail()
  loading.value = false
  startAutoRefresh()
})

onUnmounted(() => {
  stopAutoRefresh()
})
</script>

<style scoped>
.activity-detail-page {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.loading-state,
.error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  color: var(--text-secondary);
}

.btn-back {
  position: absolute;
  top: 20px;
  left: 20px;
  padding: 8px 16px;
  background-color: rgba(255, 255, 255, 0.9);
  color: var(--primary-color);
  border: 1px solid var(--primary-color);
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
  z-index: 10;
  transition: all 0.3s ease;
}

.btn-back:hover {
  background-color: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.activity-header {
  position: relative;
  width: 100%;
  height: 400px;
  overflow: hidden;
}

.cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.header-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.8), transparent);
  padding: 40px 20px 20px;
  color: white;
}

.header-overlay h1 {
  font-size: 32px;
  margin-bottom: 10px;
}

.header-meta {
  display: flex;
  gap: 10px;
}

.type-badge,
.status-badge {
  display: inline-block;
  padding: 4px 12px;
  background-color: var(--primary-color);
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.activity-content {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: 20px;
  padding: 30px 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.main-section {
  background: white;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.info-item {
  display: flex;
  flex-direction: column;
}

.info-label {
  font-weight: 600;
  color: var(--text-secondary);
  margin-bottom: 5px;
  font-size: 13px;
}

.info-value {
  color: var(--text-primary);
  font-size: 15px;
}

.description-section {
  margin-bottom: 30px;
  padding-bottom: 30px;
  border-bottom: 1px solid var(--border-color);
}

.description-section h2 {
  font-size: 18px;
  margin-bottom: 15px;
  color: var(--text-primary);
}

.description-section p {
  line-height: 1.6;
  color: var(--text-secondary);
}

.feedback-section {
  margin-bottom: 30px;
  padding-bottom: 30px;
  border-bottom: 1px solid var(--border-color);
}

.feedback-section h2 {
  font-size: 18px;
  margin-bottom: 15px;
  color: var(--text-primary);
}

.feedback-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.feedback-item {
  padding: 16px;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  transition: box-shadow 0.3s ease;
}

.feedback-item:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.feedback-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.user-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.user-name {
  font-weight: 600;
  color: var(--text-primary);
  font-size: 14px;
}

.feedback-date {
  color: var(--text-secondary);
  font-size: 12px;
}

.rating-display {
  display: flex;
  gap: 2px;
}

.star {
  color: #ddd;
  font-size: 14px;
}

.star.filled {
  color: var(--primary-color);
}

.feedback-content {
  color: var(--text-primary);
  font-size: 13px;
  line-height: 1.6;
  margin-bottom: 12px;
  word-break: break-word;
}

.feedback-reply {
  background-color: #f5f7fa;
  padding: 12px;
  border-radius: 4px;
  border-left: 3px solid var(--primary-color);
}

.reply-header {
  color: var(--text-primary);
  font-size: 12px;
  margin-bottom: 8px;
}

.reply-content {
  color: var(--text-primary);
  font-size: 13px;
  line-height: 1.6;
  word-break: break-word;
}

.loading,
.empty-state {
  padding: 20px;
  text-align: center;
  color: var(--text-secondary);
  font-size: 14px;
}

.progress-section h2 {
  font-size: 18px;
  margin-bottom: 15px;
  color: var(--text-primary);
}

.progress-item {
  margin-bottom: 20px;
}

.progress-label {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  margin-bottom: 8px;
  color: var(--text-secondary);
}

.progress-percentage {
  font-weight: 600;
  color: var(--primary-color);
}

.progress-bar {
  width: 100%;
  height: 8px;
  background-color: #f0f0f0;
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  border-radius: 4px;
  transition: width 0.3s ease;
}

.registration-fill {
  background-color: var(--primary-color);
}

.crowdfunding-fill {
  background-color: #52c41a;
}

.sidebar {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.card h3 {
  font-size: 16px;
  margin-bottom: 15px;
  color: var(--text-primary);
}

.card-content {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.card-stat {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  margin-bottom: 10px;
}

.stat-label {
  color: var(--text-secondary);
}

.stat-value {
  font-weight: 600;
  color: var(--text-primary);
}

.organizer-name {
  color: var(--text-primary);
  font-weight: 500;
}

.btn-register,
.btn-support {
  padding: 10px 16px;
  background-color: var(--primary-color);
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s ease;
  margin-top: 10px;
}

.btn-register:hover,
.btn-support:hover {
  background-color: var(--primary-light);
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.3);
}

.tier-item {
  padding: 12px;
  background-color: #f9f9f9;
  border-radius: 4px;
  margin-bottom: 10px;
  border-left: 3px solid var(--primary-color);
}

.tier-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 5px;
}

.tier-name {
  font-weight: 600;
  color: var(--text-primary);
  font-size: 14px;
}

.tier-amount {
  font-weight: 700;
  color: var(--primary-color);
  font-size: 16px;
}

.tier-description {
  font-size: 12px;
  color: var(--text-secondary);
  margin: 5px 0 0 0;
}

@media (max-width: 768px) {
  .activity-content {
    grid-template-columns: 1fr;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }

  .header-overlay h1 {
    font-size: 24px;
  }

  .main-section {
    padding: 20px;
  }
}
</style>
