<template>
  <div class="organizer-statistics-page">
    <!-- Top Navigation -->
    <TopNavigation />

    <div class="page-header">
      <h1>📊 数据统计</h1>
      <p class="subtitle">查看您的活动数据和众筹统计</p>
    </div>

    <div v-if="loading" class="loading-state">
      <p>加载统计数据中...</p>
    </div>

    <div v-else class="statistics-container">
      <!-- Statistics Cards -->
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon">📈</div>
          <div class="stat-content">
            <p class="stat-label">总活动数</p>
            <p class="stat-value">{{ activityStats.totalActivities }}</p>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon">👥</div>
          <div class="stat-content">
            <p class="stat-label">总报名人数</p>
            <p class="stat-value">{{ activityStats.totalRegistrations }}</p>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon">💰</div>
          <div class="stat-content">
            <p class="stat-label">众筹总金额</p>
            <p class="stat-value">¥{{ crowdfundingStats.totalCrowdfundingAmount }}</p>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon">⭐</div>
          <div class="stat-content">
            <p class="stat-label">平均评分</p>
            <p class="stat-value">{{ activityStats.averageRating }}</p>
          </div>
        </div>
      </div>

      <!-- Charts Section -->
      <div class="charts-section">
        <!-- Activity Statistics Chart -->
        <div class="chart-container">
          <h2>活动统计</h2>
          <div id="activityChart" class="chart"></div>
          <div class="chart-details">
            <div class="detail-item">
              <span class="detail-label">平均每个活动报名人数:</span>
              <span class="detail-value">{{ activityStats.averageRegistrationsPerActivity.toFixed(1) }}</span>
            </div>
          </div>
        </div>

        <!-- Crowdfunding Statistics Chart -->
        <div class="chart-container">
          <h2>众筹统计</h2>
          <div id="crowdfundingChart" class="chart"></div>
          <div class="chart-details">
            <div class="detail-item">
              <span class="detail-label">众筹完成率:</span>
              <span class="detail-value">{{ crowdfundingStats.completionRate }}%</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">总支持人次:</span>
              <span class="detail-value">{{ crowdfundingStats.totalSupporters }}</span>
            </div>
          </div>
        </div>

        <!-- Feedback Statistics Chart -->
        <div class="chart-container">
          <h2>反馈统计</h2>
          <div id="feedbackChart" class="chart"></div>
          <div class="chart-details">
            <div class="detail-item">
              <span class="detail-label">总反馈数:</span>
              <span class="detail-value">{{ feedbackStats.totalFeedback }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">有反馈的活动数:</span>
              <span class="detail-value">{{ feedbackStats.activitiesWithFeedback }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import TopNavigation from '../components/TopNavigation.vue'
import http from '../utils/http'

const loading = ref(true)
const activityStats = ref({
  totalActivities: 0,
  totalRegistrations: 0,
  averageRegistrationsPerActivity: 0,
  averageRating: 0
})
const crowdfundingStats = ref({
  totalCrowdfundingAmount: 0,
  totalTargetAmount: 0,
  completionRate: 0,
  totalSupporters: 0,
  averageSupportPerActivity: 0
})
const feedbackStats = ref({
  totalFeedback: 0,
  averageRating: 0,
  activitiesWithFeedback: 0
})

let activityChartInstance = null
let crowdfundingChartInstance = null
let feedbackChartInstance = null

/**
 * Fetch all statistics data
 */
const fetchStatistics = async () => {
  try {
    const [activityRes, crowdfundingRes, feedbackRes] = await Promise.all([
      http.get('/statistics/activities'),
      http.get('/statistics/crowdfunding'),
      http.get('/statistics/feedback')
    ])

    activityStats.value = activityRes.data
    crowdfundingStats.value = crowdfundingRes.data
    feedbackStats.value = feedbackRes.data

    // Initialize charts after data is loaded
    initializeCharts()
  } catch (error) {
    ElMessage.error(error.message || '获取统计数据失败')
  } finally {
    loading.value = false
  }
}

/**
 * Initialize all charts
 */
const initializeCharts = () => {
  initActivityChart()
  initCrowdfundingChart()
  initFeedbackChart()
}

/**
 * Initialize activity statistics chart
 */
const initActivityChart = () => {
  const chartDom = document.getElementById('activityChart')
  if (!chartDom) return

  activityChartInstance = echarts.init(chartDom)

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '15%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: ['总活动数', '总报名人数', '平均评分']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        data: [
          activityStats.value.totalActivities,
          activityStats.value.totalRegistrations,
          activityStats.value.averageRating * 10 // Scale for visibility
        ],
        type: 'bar',
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#1890ff' },
            { offset: 1, color: '#69c0ff' }
          ])
        },
        radius: [8, 8, 0, 0]
      }
    ]
  }

  activityChartInstance.setOption(option)
}

/**
 * Initialize crowdfunding statistics chart
 */
const initCrowdfundingChart = () => {
  const chartDom = document.getElementById('crowdfundingChart')
  if (!chartDom) return

  crowdfundingChartInstance = echarts.init(chartDom)

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '众筹进度',
        type: 'pie',
        radius: '50%',
        data: [
          {
            value: crowdfundingStats.value.totalCrowdfundingAmount,
            name: '已筹金额'
          },
          {
            value: Math.max(0, crowdfundingStats.value.totalTargetAmount - crowdfundingStats.value.totalCrowdfundingAmount),
            name: '未筹金额'
          }
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        },
        itemStyle: {
          color: function (params) {
            const colors = ['#1890ff', '#f0f0f0']
            return colors[params.dataIndex]
          }
        }
      }
    ]
  }

  crowdfundingChartInstance.setOption(option)
}

/**
 * Initialize feedback statistics chart
 */
const initFeedbackChart = () => {
  const chartDom = document.getElementById('feedbackChart')
  if (!chartDom) return

  feedbackChartInstance = echarts.init(chartDom)

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '15%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: ['总反馈数', '有反馈活动数', '平均评分']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        data: [
          feedbackStats.value.totalFeedback,
          feedbackStats.value.activitiesWithFeedback,
          feedbackStats.value.averageRating * 10 // Scale for visibility
        ],
        type: 'line',
        smooth: true,
        itemStyle: {
          color: '#52c41a'
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(82, 196, 26, 0.3)' },
            { offset: 1, color: 'rgba(82, 196, 26, 0.1)' }
          ])
        }
      }
    ]
  }

  feedbackChartInstance.setOption(option)
}

/**
 * Handle window resize
 */
const handleResize = () => {
  if (activityChartInstance) {
    activityChartInstance.resize()
  }
  if (crowdfundingChartInstance) {
    crowdfundingChartInstance.resize()
  }
  if (feedbackChartInstance) {
    feedbackChartInstance.resize()
  }
}

onMounted(() => {
  fetchStatistics()
  window.addEventListener('resize', handleResize)
})
</script>

<style scoped>
.organizer-statistics-page {
  min-height: 100vh;
  background-color: #f5f7fa;
  padding: 20px;
}

.page-header {
  max-width: 1200px;
  margin: 0 auto 30px;
}

.page-header h1 {
  font-size: 28px;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.subtitle {
  color: var(--text-secondary);
  font-size: 14px;
}

.loading-state {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  color: var(--text-secondary);
  font-size: 16px;
}

.statistics-container {
  max-width: 1200px;
  margin: 0 auto;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 15px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.stat-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.stat-icon {
  font-size: 32px;
}

.stat-content {
  flex: 1;
}

.stat-label {
  color: var(--text-secondary);
  font-size: 13px;
  margin-bottom: 5px;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: var(--primary-color);
}

.charts-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 20px;
}

.chart-container {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.chart-container h2 {
  font-size: 16px;
  color: var(--text-primary);
  margin-bottom: 15px;
}

.chart {
  width: 100%;
  height: 300px;
  margin-bottom: 15px;
}

.chart-details {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding-top: 15px;
  border-top: 1px solid var(--border-color);
}

.detail-item {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
}

.detail-label {
  color: var(--text-secondary);
}

.detail-value {
  font-weight: 600;
  color: var(--primary-color);
}

@media (max-width: 768px) {
  .organizer-statistics-page {
    padding: 15px;
  }

  .page-header h1 {
    font-size: 24px;
  }

  .stats-grid {
    grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
    gap: 15px;
  }

  .stat-card {
    flex-direction: column;
    text-align: center;
  }

  .charts-section {
    grid-template-columns: 1fr;
  }

  .chart {
    height: 250px;
  }
}
</style>
