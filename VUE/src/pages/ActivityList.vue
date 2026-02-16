<template>
  <div class="activity-list-page">
    <!-- Top Navigation -->
    <TopNavigation />

    <!-- Filter Bar -->
    <div class="filter-bar">
      <div class="filter-container">
        <!-- Search Input -->
        <div class="search-box">
          <input
            v-model="searchQuery"
            type="text"
            placeholder="搜索活动..."
            @keyup.enter="handleSearch"
          />
          <button @click="handleSearch" class="btn-search">搜索</button>
        </div>

        <!-- Type Filter -->
        <div class="filter-group">
          <label>活动类型:</label>
          <select v-model="selectedType" @change="handleFilterChange">
            <option value="">全部类型</option>
            <option value="lecture">讲座</option>
            <option value="competition">比赛</option>
            <option value="charity">公益</option>
            <option value="other">其他</option>
          </select>
        </div>

        <!-- Time Range Filter -->
        <div class="filter-group">
          <label>时间范围:</label>
          <input
            v-model="startDate"
            type="date"
            @change="handleFilterChange"
          />
          <span class="date-separator">至</span>
          <input
            v-model="endDate"
            type="date"
            @change="handleFilterChange"
          />
        </div>

        <!-- Sort By -->
        <div class="filter-group">
          <label>排序方式:</label>
          <select v-model="sortBy" @change="handleFilterChange">
            <option value="startTime">按时间</option>
            <option value="popularity">按热度</option>
            <option value="recent">最新发布</option>
          </select>
        </div>

        <!-- Reset Button -->
        <button @click="handleReset" class="btn-reset">重置筛选</button>
      </div>
    </div>

    <!-- Activity List -->
    <div class="activity-list-container">
      <div v-if="activities.length === 0" class="empty-state">
        <p>暂无活动</p>
      </div>

      <div v-else class="activity-grid">
        <div
          v-for="activity in activities"
          :key="activity.id"
          class="activity-card"
          @click="goToActivityDetail(activity.id)"
        >
          <!-- Cover Image -->
          <div class="activity-cover">
            <img
              :src="activity.coverUrl || 'https://via.placeholder.com/300x200?text=No+Image'"
              :alt="activity.title"
              class="cover-image"
            />
            <div class="activity-type-badge">{{ getActivityTypeLabel(activity.type) }}</div>
          </div>

          <!-- Activity Info -->
          <div class="activity-info">
            <h3 class="activity-title">{{ activity.title }}</h3>

            <div class="activity-meta">
              <div class="meta-item">
                <span class="meta-label">📅 时间:</span>
                <span class="meta-value">{{ formatDateTime(activity.startTime) }}</span>
              </div>

              <div class="meta-item">
                <span class="meta-label">📍 地点:</span>
                <span class="meta-value">{{ activity.location || '未指定' }}</span>
              </div>

              <div class="meta-item">
                <span class="meta-label">👥 报名人数:</span>
                <span class="meta-value">{{ activity.registrationCount || 0 }} / {{ activity.maxParticipants || '无限制' }}</span>
              </div>
            </div>

            <!-- Progress Bars -->
            <div class="progress-section">
              <!-- Registration Progress -->
              <div class="progress-item">
                <div class="progress-label">
                  <span>报名进度</span>
                  <span class="progress-percentage">{{ getRegistrationPercentage(activity) }}%</span>
                </div>
                <div class="progress-bar">
                  <div
                    class="progress-fill registration-fill"
                    :style="{ width: getRegistrationPercentage(activity) + '%' }"
                  ></div>
                </div>
              </div>

              <!-- Crowdfunding Progress (if enabled) -->
              <div v-if="activity.enableCrowdfunding" class="progress-item">
                <div class="progress-label">
                  <span>众筹进度</span>
                  <span class="progress-percentage">{{ getCrowdfundingPercentage(activity) }}%</span>
                </div>
                <div class="progress-bar">
                  <div
                    class="progress-fill crowdfunding-fill"
                    :style="{ width: getCrowdfundingPercentage(activity) + '%' }"
                  ></div>
                </div>
              </div>
            </div>

            <!-- Description -->
            <p class="activity-description">{{ truncateText(activity.description, 100) }}</p>

            <!-- Action Button -->
            <button class="btn-view-detail">查看详情 →</button>
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
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import TopNavigation from '../components/TopNavigation.vue'
import http from '../utils/http'

const router = useRouter()

// State
const activities = ref([])
const currentPage = ref(0)
const pageSize = ref(10)
const totalPages = ref(1)
const totalElements = ref(0)

// Filters
const searchQuery = ref('')
const selectedType = ref('')
const startDate = ref('')
const endDate = ref('')
const sortBy = ref('startTime')

// Auto-refresh
let refreshInterval = null

/**
 * Fetch activity list
 */
const fetchActivityList = async () => {
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      sortBy: sortBy.value
    }

    if (selectedType.value) {
      params.type = selectedType.value
    }

    if (startDate.value && endDate.value) {
      // Convert date to ISO datetime format
      params.startDate = new Date(startDate.value).toISOString()
      params.endDate = new Date(endDate.value).toISOString()
    }

    const response = await http.get('/activities', { params })
    const pageData = response.data

    activities.value = pageData.content || []
    currentPage.value = pageData.number || 0
    pageSize.value = pageData.size || 10
    totalPages.value = pageData.totalPages || 1
    totalElements.value = pageData.totalElements || 0
  } catch (error) {
    ElMessage.error(error.message || '获取活动列表失败')
  }
}

/**
 * Handle search
 */
const handleSearch = () => {
  currentPage.value = 0
  fetchActivityList()
}

/**
 * Handle filter change
 */
const handleFilterChange = () => {
  currentPage.value = 0
  stopAutoRefresh()
  fetchActivityList()
  startAutoRefresh()
}

/**
 * Handle reset filters
 */
const handleReset = () => {
  searchQuery.value = ''
  selectedType.value = ''
  startDate.value = ''
  endDate.value = ''
  sortBy.value = 'startTime'
  currentPage.value = 0
  fetchActivityList()
}

/**
 * Go to activity detail page
 */
const goToActivityDetail = (activityId) => {
  router.push(`/activity/${activityId}`)
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
  if (!activity.maxParticipants || activity.maxParticipants === 0) {
    return 0
  }
  return Math.round((activity.registrationCount || 0) / activity.maxParticipants * 100)
}

/**
 * Get crowdfunding percentage
 */
const getCrowdfundingPercentage = (activity) => {
  if (!activity.crowdfundingTarget || activity.crowdfundingTarget === 0) {
    return 0
  }
  return Math.round((activity.crowdfundingAmount || 0) / activity.crowdfundingTarget * 100)
}

/**
 * Truncate text
 */
const truncateText = (text, length) => {
  if (!text) return ''
  if (text.length > length) {
    return text.substring(0, length) + '...'
  }
  return text
}

/**
 * Previous page
 */
const previousPage = () => {
  if (currentPage.value > 0) {
    currentPage.value--
    fetchActivityList()
  }
}

/**
 * Next page
 */
const nextPage = () => {
  if (currentPage.value < totalPages.value - 1) {
    currentPage.value++
    fetchActivityList()
  }
}

/**
 * Start auto-refresh timer (every 30 seconds for popularity sorting)
 */
const startAutoRefresh = () => {
  // Only auto-refresh if sorting by popularity
  if (sortBy.value === 'popularity') {
    refreshInterval = setInterval(() => {
      fetchActivityList()
    }, 30000) // 30 seconds
  }
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

onMounted(() => {
  fetchActivityList()
  startAutoRefresh()
})

onUnmounted(() => {
  stopAutoRefresh()
})
</script>

<style scoped>
.activity-list-page {
  min-height: 100vh;
  background-color: #f5f7fa;
  padding: 20px;
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

.search-box {
  display: flex;
  gap: 10px;
  flex: 1;
  min-width: 250px;
}

.search-box input {
  flex: 1;
  padding: 10px 12px;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  font-size: 14px;
}

.search-box input:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

.btn-search {
  padding: 10px 20px;
  background-color: var(--primary-color);
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s ease;
}

.btn-search:hover {
  background-color: var(--primary-light);
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.3);
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

.filter-group select,
.filter-group input {
  padding: 8px 10px;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  font-size: 14px;
}

.filter-group select:focus,
.filter-group input:focus {
  outline: none;
  border-color: var(--primary-color);
}

.date-separator {
  color: var(--text-secondary);
  margin: 0 5px;
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

/* Activity List */
.activity-list-container {
  margin-bottom: 30px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: var(--text-secondary);
  font-size: 16px;
}

.activity-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.activity-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
  display: flex;
  flex-direction: column;
}

.activity-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.activity-cover {
  position: relative;
  width: 100%;
  height: 200px;
  overflow: hidden;
  background-color: #f0f0f0;
}

.cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.activity-card:hover .cover-image {
  transform: scale(1.05);
}

.activity-type-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  background-color: var(--primary-color);
  color: white;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.activity-info {
  padding: 15px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.activity-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 10px 0;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.activity-meta {
  margin-bottom: 12px;
  font-size: 13px;
}

.meta-item {
  display: flex;
  align-items: center;
  margin-bottom: 6px;
  color: var(--text-secondary);
}

.meta-label {
  margin-right: 6px;
  font-weight: 500;
}

.meta-value {
  color: var(--text-primary);
}

.progress-section {
  margin-bottom: 12px;
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

.activity-description {
  font-size: 13px;
  color: var(--text-secondary);
  margin: 0 0 12px 0;
  line-height: 1.4;
  flex: 1;
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
  align-self: flex-start;
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
  .activity-grid {
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  }
}

@media (max-width: 768px) {
  .filter-container {
    flex-direction: column;
  }

  .search-box {
    width: 100%;
  }

  .filter-group {
    width: 100%;
  }

  .filter-group select,
  .filter-group input {
    width: 100%;
  }

  .activity-grid {
    grid-template-columns: 1fr;
  }
}
</style>
