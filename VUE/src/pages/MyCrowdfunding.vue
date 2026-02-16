<template>
  <div class="my-crowdfunding-page">
    <!-- Top Navigation -->
    <TopNavigation />

    <!-- Header -->
    <div class="page-header">
      <h1>我的众筹</h1>
      <p>查看您支持的所有众筹项目</p>
    </div>

    <!-- Filter Bar -->
    <div class="filter-bar">
      <div class="filter-container">
        <!-- Sort By -->
        <div class="filter-group">
          <label>排序方式:</label>
          <select v-model="sortBy" @change="handleFilterChange">
            <option value="recent">最新支持</option>
            <option value="amount">按金额</option>
          </select>
        </div>

        <!-- Reset Button -->
        <button @click="handleReset" class="btn-reset">重置筛选</button>
      </div>
    </div>

    <!-- Crowdfunding Supports List -->
    <div class="supports-container">
      <div v-if="supports.length === 0" class="empty-state">
        <p>您还没有支持任何众筹项目</p>
        <button @click="goToActivityList" class="btn-explore">浏览活动</button>
      </div>

      <div v-else class="supports-list">
        <div
          v-for="support in supports"
          :key="support.id"
          class="support-item"
        >
          <!-- Activity Cover -->
          <div class="activity-cover">
            <img
              :src="support.activity?.coverUrl || 'https://via.placeholder.com/200x150?text=No+Image'"
              :alt="support.activity?.title"
              class="cover-image"
            />
            <div class="status-badge" :class="getStatusClass(support.activity?.status)">
              {{ getActivityStatusLabel(support.activity?.status) }}
            </div>
          </div>

          <!-- Support Details -->
          <div class="support-details">
            <h3 class="activity-title">{{ support.activity?.title }}</h3>

            <div class="support-meta">
              <div class="meta-item">
                <span class="meta-label">💰 支持金额:</span>
                <span class="meta-value amount">¥{{ formatAmount(support.amount) }}</span>
              </div>

              <div class="meta-item">
                <span class="meta-label">📅 支持时间:</span>
                <span class="meta-value">{{ formatDateTime(support.createdAt) }}</span>
              </div>

              <div class="meta-item">
                <span class="meta-label">💳 支付状态:</span>
                <span class="meta-value" :class="getPaymentStatusClass(support.paymentStatus)">
                  {{ getPaymentStatusLabel(support.paymentStatus) }}
                </span>
              </div>

              <div class="meta-item">
                <span class="meta-label">🎯 活动时间:</span>
                <span class="meta-value">{{ formatDateTime(support.activity?.startTime) }}</span>
              </div>

              <div class="meta-item">
                <span class="meta-label">📍 活动地点:</span>
                <span class="meta-value">{{ support.activity?.location || '未指定' }}</span>
              </div>
            </div>

            <!-- Crowdfunding Progress -->
            <div class="progress-section">
              <div class="progress-item">
                <div class="progress-label">
                  <span>众筹进度</span>
                  <span class="progress-percentage">{{ getCrowdfundingPercentage(support.activity) }}%</span>
                </div>
                <div class="progress-bar">
                  <div
                    class="progress-fill"
                    :style="{ width: getCrowdfundingPercentage(support.activity) + '%' }"
                  ></div>
                </div>
                <div class="progress-info">
                  已筹 ¥{{ formatAmount(support.activity?.crowdfundingAmount) }} / 目标 ¥{{ formatAmount(support.activity?.crowdfundingTarget) }}
                </div>
              </div>
            </div>
          </div>

          <!-- Actions -->
          <div class="support-actions">
            <button
              @click="goToActivityDetail(support.activity?.id)"
              class="btn-view-detail"
            >
              查看活动
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Pagination -->
    <div v-if="totalPages > 1" class="pagination">
      <button
        :disabled="currentPage === 0"
        @click="previousPage"
        class="btn-pagination"
      >
        上一页
      </button>

      <div class="page-info">
        第 {{ currentPage + 1 }} / {{ totalPages }} 页
      </div>

      <button
        :disabled="currentPage >= totalPages - 1"
        @click="nextPage"
        class="btn-pagination"
      >
        下一页
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import TopNavigation from '../components/TopNavigation.vue'
import http from '../utils/http'

const router = useRouter()

// State
const supports = ref([])
const currentPage = ref(0)
const pageSize = ref(10)
const totalPages = ref(1)

// Filters
const sortBy = ref('recent')

/**
 * Fetch user's crowdfunding supports
 */
const fetchMyCrowdfunding = async () => {
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value
    }

    const response = await http.get('/crowdfunding/my', { params })
    const pageData = response.data

    supports.value = pageData.content || []
    currentPage.value = pageData.number || 0
    pageSize.value = pageData.size || 10
    totalPages.value = pageData.totalPages || 1

    // Sort if needed
    if (sortBy.value === 'amount') {
      supports.value.sort((a, b) => b.amount - a.amount)
    }
  } catch (error) {
    ElMessage.error(error.message || '获取众筹支持列表失败')
  }
}

/**
 * Handle filter change
 */
const handleFilterChange = () => {
  currentPage.value = 0
  fetchMyCrowdfunding()
}

/**
 * Handle reset filters
 */
const handleReset = () => {
  sortBy.value = 'recent'
  currentPage.value = 0
  fetchMyCrowdfunding()
}

/**
 * Go to activity detail page
 */
const goToActivityDetail = (activityId) => {
  router.push(`/activity/${activityId}`)
}

/**
 * Go to activity list page
 */
const goToActivityList = () => {
  router.push('/activities')
}

/**
 * Get activity status label
 */
const getActivityStatusLabel = (status) => {
  const statusMap = {
    DRAFT: '草稿',
    PENDING_AUDIT: '待审核',
    APPROVED: '已批准',
    REJECTED: '已驳回',
    ONGOING: '进行中',
    ENDED: '已结束',
    ARCHIVED: '已归档'
  }
  return statusMap[status] || status || '未知'
}

/**
 * Get activity status class
 */
const getStatusClass = (status) => {
  const classMap = {
    ONGOING: 'status-ongoing',
    ENDED: 'status-ended',
    ARCHIVED: 'status-archived'
  }
  return classMap[status] || 'status-default'
}

/**
 * Get payment status label
 */
const getPaymentStatusLabel = (status) => {
  const statusMap = {
    PENDING: '待支付',
    COMPLETED: '已支付',
    FAILED: '支付失败'
  }
  return statusMap[status] || status || '未知'
}

/**
 * Get payment status class
 */
const getPaymentStatusClass = (status) => {
  const classMap = {
    PENDING: 'status-pending',
    COMPLETED: 'status-completed',
    FAILED: 'status-failed'
  }
  return classMap[status] || 'status-default'
}

/**
 * Format amount
 */
const formatAmount = (amount) => {
  if (!amount) return '0.00'
  return parseFloat(amount).toFixed(2)
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
 * Get crowdfunding percentage
 */
const getCrowdfundingPercentage = (activity) => {
  if (!activity || !activity.crowdfundingTarget || activity.crowdfundingTarget === 0) {
    return 0
  }
  return Math.round((activity.crowdfundingAmount || 0) / activity.crowdfundingTarget * 100)
}

/**
 * Previous page
 */
const previousPage = () => {
  if (currentPage.value > 0) {
    currentPage.value--
    fetchMyCrowdfunding()
  }
}

/**
 * Next page
 */
const nextPage = () => {
  if (currentPage.value < totalPages.value - 1) {
    currentPage.value++
    fetchMyCrowdfunding()
  }
}

onMounted(() => {
  fetchMyCrowdfunding()
})
</script>

<style scoped>
.my-crowdfunding-page {
  min-height: 100vh;
  background-color: #f5f7fa;
  padding: 20px;
}

/* Page Header */
.page-header {
  background: white;
  border-radius: 8px;
  padding: 30px;
  margin-bottom: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.page-header h1 {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 10px 0;
}

.page-header p {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0;
}

/* Filter Bar */
.filter-bar {
  background: white;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.filter-container {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  align-items: center;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-group label {
  font-weight: 500;
  color: var(--text-primary);
  white-space: nowrap;
}

.filter-group select {
  padding: 8px 10px;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  font-size: 14px;
}

.filter-group select:focus {
  outline: none;
  border-color: var(--primary-color);
}

.btn-reset {
  padding: 8px 16px;
  background-color: #f0f0f0;
  color: var(--text-primary);
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s ease;
}

.btn-reset:hover {
  background-color: #e0e0e0;
}

/* Supports Container */
.supports-container {
  margin-bottom: 30px;
}

.empty-state {
  background: white;
  border-radius: 8px;
  padding: 60px 20px;
  text-align: center;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.empty-state p {
  color: var(--text-secondary);
  font-size: 16px;
  margin-bottom: 20px;
}

.btn-explore {
  padding: 10px 24px;
  background-color: var(--primary-color);
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s ease;
}

.btn-explore:hover {
  background-color: var(--primary-light);
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.3);
}

/* Supports List */
.supports-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.support-item {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  display: grid;
  grid-template-columns: 200px 1fr auto;
  gap: 20px;
  padding: 20px;
  transition: all 0.3s ease;
}

.support-item:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.activity-cover {
  position: relative;
  width: 200px;
  height: 150px;
  border-radius: 8px;
  overflow: hidden;
  background-color: #f0f0f0;
}

.cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.status-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  color: white;
}

.status-ongoing {
  background-color: var(--primary-color);
}

.status-ended {
  background-color: #f5222d;
}

.status-archived {
  background-color: #999;
}

.status-default {
  background-color: #1890ff;
}

/* Support Details */
.support-details {
  flex: 1;
}

.activity-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 15px 0;
}

.support-meta {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
  margin-bottom: 15px;
  font-size: 13px;
}

.meta-item {
  display: flex;
  align-items: center;
  color: var(--text-secondary);
}

.meta-label {
  margin-right: 6px;
  font-weight: 500;
  white-space: nowrap;
}

.meta-value {
  color: var(--text-primary);
  word-break: break-word;
}

.meta-value.amount {
  color: var(--primary-color);
  font-weight: 600;
  font-size: 14px;
}

.status-pending {
  color: #faad14;
  font-weight: 600;
}

.status-completed {
  color: #52c41a;
  font-weight: 600;
}

.status-failed {
  color: #f5222d;
  font-weight: 600;
}

/* Progress Section */
.progress-section {
  margin-top: 15px;
}

.progress-item {
  margin-bottom: 8px;
}

.progress-label {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  margin-bottom: 4px;
  color: var(--text-secondary);
}

.progress-percentage {
  font-weight: 600;
  color: var(--primary-color);
}

.progress-bar {
  width: 100%;
  height: 6px;
  background-color: #f0f0f0;
  border-radius: 3px;
  overflow: hidden;
  margin-bottom: 4px;
}

.progress-fill {
  height: 100%;
  background-color: var(--primary-color);
  border-radius: 3px;
  transition: width 0.3s ease;
}

.progress-info {
  font-size: 12px;
  color: var(--text-secondary);
}

/* Support Actions */
.support-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
  justify-content: center;
  min-width: 120px;
}

.btn-view-detail {
  padding: 8px 16px;
  background-color: var(--primary-color);
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 500;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.btn-view-detail:hover {
  background-color: var(--primary-light);
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.3);
}

/* Pagination */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
  margin-top: 30px;
}

.btn-pagination {
  padding: 10px 20px;
  background-color: white;
  color: var(--primary-color);
  border: 1px solid var(--primary-color);
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s ease;
}

.btn-pagination:hover:not(:disabled) {
  background-color: var(--primary-color);
  color: white;
}

.btn-pagination:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  color: var(--text-secondary);
  font-size: 14px;
}

/* Responsive */
@media (max-width: 1024px) {
  .support-item {
    grid-template-columns: 150px 1fr auto;
  }

  .activity-cover {
    width: 150px;
    height: 120px;
  }

  .support-meta {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .support-item {
    grid-template-columns: 1fr;
    gap: 15px;
  }

  .activity-cover {
    width: 100%;
    height: 200px;
  }

  .support-actions {
    flex-direction: row;
    gap: 10px;
  }

  .btn-view-detail {
    flex: 1;
  }

  .support-meta {
    grid-template-columns: 1fr;
  }
}
</style>
