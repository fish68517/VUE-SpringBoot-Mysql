<template>
  <div class="my-activities-page">
    <!-- Top Navigation -->
    <TopNavigation />

    <!-- Header -->
    <div class="page-header">
      <h1>我的活动</h1>
      <p>查看您已报名的所有活动</p>
    </div>

    <!-- Filter Bar -->
    <div class="filter-bar">
      <div class="filter-container">
        <!-- Status Filter -->
        <div class="filter-group">
          <label>活动状态:</label>
          <select v-model="selectedStatus" @change="handleFilterChange">
            <option value="">全部状态</option>
            <option value="ONGOING">进行中</option>
            <option value="ENDED">已结束</option>
            <option value="CANCELLED">已取消</option>
          </select>
        </div>

        <!-- Sort By -->
        <div class="filter-group">
          <label>排序方式:</label>
          <select v-model="sortBy" @change="handleFilterChange">
            <option value="recent">最新报名</option>
            <option value="startTime">按时间</option>
          </select>
        </div>

        <!-- Reset Button -->
        <button @click="handleReset" class="btn-reset">重置筛选</button>
      </div>
    </div>

    <!-- Activities List -->
    <div class="activities-container">
      <div v-if="registrations.length === 0" class="empty-state">
        <p>您还没有报名任何活动</p>
        <button @click="goToActivityList" class="btn-explore">浏览活动</button>
      </div>

      <div v-else class="activities-list">
        <div
          v-for="registration in registrations"
          :key="registration.id"
          class="activity-item"
        >
          <!-- Activity Cover -->
          <div class="activity-cover">
            <img
              :src="registration.activity?.coverUrl || 'https://via.placeholder.com/200x150?text=No+Image'"
              :alt="registration.activity?.title"
              class="cover-image"
            />
            <div class="status-badge" :class="getStatusClass(registration.activity?.status)">
              {{ getActivityStatusLabel(registration.activity?.status) }}
            </div>
          </div>

          <!-- Activity Details -->
          <div class="activity-details">
            <h3 class="activity-title">{{ registration.activity?.title }}</h3>

            <div class="activity-meta">
              <div class="meta-item">
                <span class="meta-label">📅 时间:</span>
                <span class="meta-value">{{ formatDateTime(registration.activity?.startTime) }}</span>
              </div>

              <div class="meta-item">
                <span class="meta-label">📍 地点:</span>
                <span class="meta-value">{{ registration.activity?.location || '未指定' }}</span>
              </div>

              <div class="meta-item">
                <span class="meta-label">👤 报名人:</span>
                <span class="meta-value">{{ registration.participantName }}</span>
              </div>

              <div class="meta-item">
                <span class="meta-label">📞 联系方式:</span>
                <span class="meta-value">{{ registration.contactPhone }}</span>
              </div>

              <div v-if="registration.remarks" class="meta-item">
                <span class="meta-label">📝 备注:</span>
                <span class="meta-value">{{ registration.remarks }}</span>
              </div>

              <div class="meta-item">
                <span class="meta-label">✅ 报名状态:</span>
                <span class="meta-value" :class="getRegistrationStatusClass(registration.status)">
                  {{ getRegistrationStatusLabel(registration.status) }}
                </span>
              </div>
            </div>

            <!-- Progress Bars -->
            <div class="progress-section">
              <!-- Registration Progress -->
              <div class="progress-item">
                <div class="progress-label">
                  <span>报名进度</span>
                  <span class="progress-percentage">{{ getRegistrationPercentage(registration.activity) }}%</span>
                </div>
                <div class="progress-bar">
                  <div
                    class="progress-fill registration-fill"
                    :style="{ width: getRegistrationPercentage(registration.activity) + '%' }"
                  ></div>
                </div>
              </div>

              <!-- Crowdfunding Progress (if enabled) -->
              <div v-if="registration.activity?.enableCrowdfunding" class="progress-item">
                <div class="progress-label">
                  <span>众筹进度</span>
                  <span class="progress-percentage">{{ getCrowdfundingPercentage(registration.activity) }}%</span>
                </div>
                <div class="progress-bar">
                  <div
                    class="progress-fill crowdfunding-fill"
                    :style="{ width: getCrowdfundingPercentage(registration.activity) + '%' }"
                  ></div>
                </div>
              </div>
            </div>
          </div>

          <!-- Actions -->
          <div class="activity-actions">
            <button
              @click="goToActivityDetail(registration.activity?.id)"
              class="btn-view-detail"
            >
              查看详情
            </button>

            <button
              v-if="canCancelRegistration(registration.activity)"
              @click="handleCancelRegistration(registration.id)"
              class="btn-cancel"
            >
              取消报名
            </button>

            <button
              v-else
              disabled
              class="btn-cancel disabled"
              title="已超过取消时间"
            >
              无法取消
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
import { ElMessage, ElMessageBox } from 'element-plus'
import TopNavigation from '../components/TopNavigation.vue'
import http from '../utils/http'

const router = useRouter()

// State
const registrations = ref([])
const currentPage = ref(0)
const pageSize = ref(10)
const totalPages = ref(1)

// Filters
const selectedStatus = ref('')
const sortBy = ref('recent')

/**
 * Fetch user's registrations
 */
const fetchMyRegistrations = async () => {
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value
    }

    const response = await http.get('/registrations/my', { params })
    const pageData = response.data

    registrations.value = pageData.content || []
    currentPage.value = pageData.number || 0
    pageSize.value = pageData.size || 10
    totalPages.value = pageData.totalPages || 1
  } catch (error) {
    ElMessage.error(error.message || '获取报名列表失败')
  }
}

/**
 * Handle filter change
 */
const handleFilterChange = () => {
  currentPage.value = 0
  fetchMyRegistrations()
}

/**
 * Handle reset filters
 */
const handleReset = () => {
  selectedStatus.value = ''
  sortBy.value = 'recent'
  currentPage.value = 0
  fetchMyRegistrations()
}

/**
 * Handle cancel registration
 */
const handleCancelRegistration = (registrationId) => {
  ElMessageBox.confirm(
    '确定要取消报名吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await http.delete(`/registrations/${registrationId}`)
      ElMessage.success('取消报名成功')
      fetchMyRegistrations()
    } catch (error) {
      ElMessage.error(error.message || '取消报名失败')
    }
  }).catch(() => {
    // User cancelled the action
  })
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
 * Get registration status label
 */
const getRegistrationStatusLabel = (status) => {
  const statusMap = {
    REGISTERED: '已报名',
    CANCELLED: '已取消'
  }
  return statusMap[status] || status || '未知'
}

/**
 * Get registration status class
 */
const getRegistrationStatusClass = (status) => {
  return status === 'REGISTERED' ? 'status-registered' : 'status-cancelled'
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
 * Get registration percentage
 */
const getRegistrationPercentage = (activity) => {
  if (!activity || !activity.maxParticipants || activity.maxParticipants === 0) {
    return 0
  }
  return Math.round((activity.registrationCount || 0) / activity.maxParticipants * 100)
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
 * Check if registration can be cancelled
 */
const canCancelRegistration = (activity) => {
  if (!activity || !activity.registrationDeadline) {
    return true
  }
  const deadline = new Date(activity.registrationDeadline)
  return new Date() < deadline
}

/**
 * Previous page
 */
const previousPage = () => {
  if (currentPage.value > 0) {
    currentPage.value--
    fetchMyRegistrations()
  }
}

/**
 * Next page
 */
const nextPage = () => {
  if (currentPage.value < totalPages.value - 1) {
    currentPage.value++
    fetchMyRegistrations()
  }
}

onMounted(() => {
  fetchMyRegistrations()
})
</script>

<style scoped>
.my-activities-page {
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

/* Activities Container */
.activities-container {
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

/* Activities List */
.activities-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.activity-item {
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

.activity-item:hover {
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

/* Activity Details */
.activity-details {
  flex: 1;
}

.activity-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 15px 0;
}

.activity-meta {
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

.status-registered {
  color: #52c41a;
  font-weight: 600;
}

.status-cancelled {
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
}

.progress-fill {
  height: 100%;
  border-radius: 3px;
  transition: width 0.3s ease;
}

.registration-fill {
  background-color: var(--primary-color);
}

.crowdfunding-fill {
  background-color: #52c41a;
}

/* Activity Actions */
.activity-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
  justify-content: center;
  min-width: 120px;
}

.btn-view-detail,
.btn-cancel {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 500;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.btn-view-detail {
  background-color: var(--primary-color);
  color: white;
}

.btn-view-detail:hover {
  background-color: var(--primary-light);
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.3);
}

.btn-cancel {
  background-color: #f5f5f5;
  color: var(--text-primary);
  border: 1px solid #d9d9d9;
}

.btn-cancel:hover:not(.disabled) {
  background-color: #f0f0f0;
  border-color: #b3b3b3;
}

.btn-cancel.disabled {
  opacity: 0.5;
  cursor: not-allowed;
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
  .activity-item {
    grid-template-columns: 150px 1fr auto;
  }

  .activity-cover {
    width: 150px;
    height: 120px;
  }

  .activity-meta {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .activity-item {
    grid-template-columns: 1fr;
    gap: 15px;
  }

  .activity-cover {
    width: 100%;
    height: 200px;
  }

  .activity-actions {
    flex-direction: row;
    gap: 10px;
  }

  .btn-view-detail,
  .btn-cancel {
    flex: 1;
  }

  .activity-meta {
    grid-template-columns: 1fr;
  }
}
</style>
