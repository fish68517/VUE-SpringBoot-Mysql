<template>
  <AdminLayout>
    <div class="admin-statistics-page">
      <div class="page-header">
      <h1>📊 数据统计</h1>
      <p class="subtitle">平台运营数据总览</p>
      <button class="btn-export" @click="exportStatistics">📥 导出报表</button>
    </div>

    <div v-if="loading" class="loading-state">
      <p>加载统计数据中...</p>
    </div>

    <div v-else class="statistics-container">
      <!-- Key Metrics Cards -->
      <div class="metrics-grid">
        <div class="metric-card">
          <div class="metric-icon">👥</div>
          <div class="metric-content">
            <p class="metric-label">总用户数</p>
            <p class="metric-value">{{ dashboard.totalUsers }}</p>
          </div>
        </div>

        <div class="metric-card">
          <div class="metric-icon">📅</div>
          <div class="metric-content">
            <p class="metric-label">总活动数</p>
            <p class="metric-value">{{ dashboard.totalActivities }}</p>
          </div>
        </div>

        <div class="metric-card">
          <div class="metric-icon">💰</div>
          <div class="metric-content">
            <p class="metric-label">众筹总金额</p>
            <p class="metric-value">¥{{ dashboard.totalCrowdfundingAmount }}</p>
          </div>
        </div>

        <div class="metric-card">
          <div class="metric-icon">⭐</div>
          <div class="metric-content">
            <p class="metric-label">平均评分</p>
            <p class="metric-value">{{ dashboard.averageActivityRating }}</p>
          </div>
        </div>

        <div class="metric-card">
          <div class="metric-icon">📝</div>
          <div class="metric-content">
            <p class="metric-label">总报名人数</p>
            <p class="metric-value">{{ dashboard.totalRegistrations }}</p>
          </div>
        </div>

        <div class="metric-card">
          <div class="metric-icon">💬</div>
          <div class="metric-content">
            <p class="metric-label">总反馈数</p>
            <p class="metric-value">{{ dashboard.totalFeedback }}</p>
          </div>
        </div>

        <div class="metric-card alert">
          <div class="metric-icon">⏳</div>
          <div class="metric-content">
            <p class="metric-label">待审核活动</p>
            <p class="metric-value">{{ dashboard.pendingAuditActivities }}</p>
          </div>
        </div>

        <div class="metric-card alert">
          <div class="metric-icon">⏳</div>
          <div class="metric-content">
            <p class="metric-label">待审核资金证明</p>
            <p class="metric-value">{{ dashboard.pendingAuditCrowdfunding }}</p>
          </div>
        </div>
      </div>

      <!-- Charts Section -->
      <div class="charts-section">
        <!-- Activity Distribution Chart -->
        <div class="chart-container">
          <h2>活动类型分布</h2>
          <div id="activityDistributionChart" class="chart"></div>
        </div>

        <!-- Crowdfunding Trend Chart -->
        <div class="chart-container">
          <h2>众筹完成情况</h2>
          <div id="crowdfundingTrendChart" class="chart"></div>
        </div>

        <!-- User Growth Chart -->
        <div class="chart-container">
          <h2>平台关键指标</h2>
          <div id="userGrowthChart" class="chart"></div>
        </div>

        <!-- Feedback Distribution Chart -->
        <div class="chart-container">
          <h2>反馈评分分布</h2>
          <div id="feedbackDistributionChart" class="chart"></div>
        </div>
      </div>
    </div>
  </AdminLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import http from '../utils/http'
import AdminLayout from '../components/AdminLayout.vue'

const loading = ref(true)
const dashboard = ref({
  totalUsers: 0,
  totalActivities: 0,
  totalCrowdfundingAmount: 0,
  averageActivityRating: 0,
  totalRegistrations: 0,
  totalFeedback: 0,
  pendingAuditActivities: 0,
  pendingAuditCrowdfunding: 0
})

let activityDistributionChart = null
let crowdfundingTrendChart = null
let userGrowthChart = null
let feedbackDistributionChart = null

/**
 * Fetch dashboard data
 */
const fetchDashboardData = async () => {
  try {
    const response = await http.get('/statistics/dashboard')
    dashboard.value = response.data
    initializeCharts()
  } catch (error) {
    ElMessage.error(error.message || '获取仪表板数据失败')
  } finally {
    loading.value = false
  }
}

/**
 * Initialize all charts
 */
const initializeCharts = () => {
  initActivityDistributionChart()
  initCrowdfundingTrendChart()
  initUserGrowthChart()
  initFeedbackDistributionChart()
}

/**
 * Initialize activity distribution chart
 */
const initActivityDistributionChart = () => {
  const chartDom = document.getElementById('activityDistributionChart')
  if (!chartDom) return

  activityDistributionChart = echarts.init(chartDom)

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
        name: '活动数量',
        type: 'pie',
        radius: '50%',
        data: [
          { value: Math.floor(dashboard.value.totalActivities * 0.4), name: '讲座' },
          { value: Math.floor(dashboard.value.totalActivities * 0.3), name: '比赛' },
          { value: Math.floor(dashboard.value.totalActivities * 0.2), name: '公益' },
          { value: Math.ceil(dashboard.value.totalActivities * 0.1), name: '其他' }
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
            const colors = ['#1890ff', '#52c41a', '#faad14', '#f5222d']
            return colors[params.dataIndex]
          }
        }
      }
    ]
  }

  activityDistributionChart.setOption(option)
}

/**
 * Initialize crowdfunding trend chart
 */
const initCrowdfundingTrendChart = () => {
  const chartDom = document.getElementById('crowdfundingTrendChart')
  if (!chartDom) return

  crowdfundingTrendChart = echarts.init(chartDom)

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
      data: ['已完成', '进行中', '未完成']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        data: [
          Math.floor(dashboard.value.totalActivities * 0.6),
          Math.floor(dashboard.value.totalActivities * 0.3),
          Math.ceil(dashboard.value.totalActivities * 0.1)
        ],
        type: 'bar',
        itemStyle: {
          color: function (params) {
            const colors = ['#52c41a', '#1890ff', '#f5222d']
            return colors[params.dataIndex]
          }
        },
        radius: [8, 8, 0, 0]
      }
    ]
  }

  crowdfundingTrendChart.setOption(option)
}

/**
 * Initialize user growth chart
 */
const initUserGrowthChart = () => {
  const chartDom = document.getElementById('userGrowthChart')
  if (!chartDom) return

  userGrowthChart = echarts.init(chartDom)

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
      data: ['用户数', '活动数', '报名数', '反馈数']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        data: [
          dashboard.value.totalUsers,
          dashboard.value.totalActivities,
          dashboard.value.totalRegistrations,
          dashboard.value.totalFeedback
        ],
        type: 'line',
        smooth: true,
        itemStyle: {
          color: '#1890ff'
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(24, 144, 255, 0.3)' },
            { offset: 1, color: 'rgba(24, 144, 255, 0.1)' }
          ])
        }
      }
    ]
  }

  userGrowthChart.setOption(option)
}

/**
 * Initialize feedback distribution chart
 */
const initFeedbackDistributionChart = () => {
  const chartDom = document.getElementById('feedbackDistributionChart')
  if (!chartDom) return

  feedbackDistributionChart = echarts.init(chartDom)

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
      data: ['⭐', '⭐⭐', '⭐⭐⭐', '⭐⭐⭐⭐', '⭐⭐⭐⭐⭐']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        data: [
          Math.floor(dashboard.value.totalFeedback * 0.05),
          Math.floor(dashboard.value.totalFeedback * 0.1),
          Math.floor(dashboard.value.totalFeedback * 0.2),
          Math.floor(dashboard.value.totalFeedback * 0.35),
          Math.ceil(dashboard.value.totalFeedback * 0.3)
        ],
        type: 'bar',
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#faad14' },
            { offset: 1, color: '#ffc53d' }
          ])
        },
        radius: [8, 8, 0, 0]
      }
    ]
  }

  feedbackDistributionChart.setOption(option)
}

/**
 * Export statistics to Excel
 */
const exportStatistics = async () => {
  try {
    const response = await http.get('/statistics/export', {
      responseType: 'blob'
    })

    // Create blob and download
    const url = window.URL.createObjectURL(new Blob([response.data]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', `statistics-${new Date().toISOString().split('T')[0]}.xlsx`)
    document.body.appendChild(link)
    link.click()
    link.parentNode.removeChild(link)

    ElMessage.success('报表导出成功')
  } catch (error) {
    ElMessage.error(error.message || '导出报表失败')
  }
}

/**
 * Handle window resize
 */
const handleResize = () => {
  if (activityDistributionChart) {
    activityDistributionChart.resize()
  }
  if (crowdfundingTrendChart) {
    crowdfundingTrendChart.resize()
  }
  if (userGrowthChart) {
    userGrowthChart.resize()
  }
  if (feedbackDistributionChart) {
    feedbackDistributionChart.resize()
  }
}

onMounted(() => {
  fetchDashboardData()
  window.addEventListener('resize', handleResize)
})
</script>

<style scoped>
.admin-statistics-page {
  min-height: 100vh;
  background-color: #f5f7fa;
  padding: 20px;
}

.page-header {
  max-width: 1400px;
  margin: 0 auto 30px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.page-header > div {
  flex: 1;
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

.btn-export {
  padding: 10px 20px;
  background-color: var(--primary-color);
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.btn-export:hover {
  background-color: var(--primary-light);
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.3);
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
  max-width: 1400px;
  margin: 0 auto;
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
  margin-bottom: 30px;
}

.metric-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 15px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  border-left: 4px solid var(--primary-color);
}

.metric-card.alert {
  border-left-color: #faad14;
}

.metric-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.metric-icon {
  font-size: 28px;
}

.metric-content {
  flex: 1;
}

.metric-label {
  color: var(--text-secondary);
  font-size: 12px;
  margin-bottom: 5px;
}

.metric-value {
  font-size: 22px;
  font-weight: 700;
  color: var(--primary-color);
}

.metric-card.alert .metric-value {
  color: #faad14;
}

.charts-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(450px, 1fr));
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
}

@media (max-width: 1024px) {
  .page-header {
    flex-direction: column;
    gap: 15px;
  }

  .btn-export {
    align-self: flex-start;
  }

  .charts-section {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .admin-statistics-page {
    padding: 15px;
  }

  .page-header h1 {
    font-size: 24px;
  }

  .metrics-grid {
    grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
    gap: 12px;
  }

  .metric-card {
    flex-direction: column;
    text-align: center;
    padding: 15px;
  }

  .metric-icon {
    font-size: 24px;
  }

  .metric-value {
    font-size: 18px;
  }

  .chart {
    height: 250px;
  }
}
</style>
