<template>
  <div class="data-statistics-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>{{ $t('admin.dataStatistics') || '数据统计与分析' }}</h2>
    </div>

    <!-- 加载状态 -->
    <el-skeleton v-if="isLoading" :rows="8" animated />

    <!-- 筛选条件卡片 -->
    <div v-else class="filter-card">
      <div class="filter-section">
        <div class="filter-group">
          <label>{{ $t('healthData.startDate') || '开始日期' }}:</label>
          <el-date-picker
            v-model="filterData.startDate"
            type="date"
            :placeholder="$t('healthData.startDate') || '开始日期'"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            @change="handleDateChange"
          />
        </div>

        <div class="filter-group">
          <label>{{ $t('healthData.endDate') || '结束日期' }}:</label>
          <el-date-picker
            v-model="filterData.endDate"
            type="date"
            :placeholder="$t('healthData.endDate') || '结束日期'"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            @change="handleDateChange"
          />
        </div>

        <div class="filter-actions">
          <el-button type="primary" @click="handleRefresh" :loading="isLoading">
            {{ $t('common.search') || '查询' }}
          </el-button>
          <el-button @click="handleReset">
            {{ $t('common.reset') || '重置' }}
          </el-button>
        </div>
      </div>
    </div>

    <!-- 统计数据卡片网格 -->
    <div v-if="!isLoading" class="statistics-grid">
      <!-- 用户统计卡片 -->
      <div class="stat-card user-stat">
        <div class="stat-header">
          <span class="stat-title">{{ $t('admin.userStatistics') || '用户统计' }}</span>
          <el-icon class="stat-icon"><User /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-item">
            <div class="stat-label">总用户数</div>
            <div class="stat-value">{{ statistics['总用户数'] || 0 }}</div>
          </div>
          <div class="stat-item">
            <div class="stat-label">普通用户</div>
            <div class="stat-value">{{ statistics['普通用户数'] || 0 }}</div>
          </div>
          <div class="stat-item">
            <div class="stat-label">医师</div>
            <div class="stat-value">{{ statistics['医师数'] || 0 }}</div>
          </div>
          <div class="stat-item">
            <div class="stat-label">管理员</div>
            <div class="stat-value">{{ statistics['管理员数'] || 0 }}</div>
          </div>
        </div>
      </div>

      <!-- 用户状态统计卡片 -->
      <div class="stat-card status-stat">
        <div class="stat-header">
          <span class="stat-title">{{ $t('admin.userStatus') || '用户状态' }}</span>
          <el-icon class="stat-icon"><CircleCheck /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-item">
            <div class="stat-label">活跃用户</div>
            <div class="stat-value active">{{ statistics['活跃用户数'] || 0 }}</div>
          </div>
          <div class="stat-item">
            <div class="stat-label">禁用用户</div>
            <div class="stat-value inactive">{{ statistics['禁用用户数'] || 0 }}</div>
          </div>
        </div>
      </div>

      <!-- 健康数据统计卡片 -->
      <div class="stat-card health-stat">
        <div class="stat-header">
          <span class="stat-title">{{ $t('admin.healthDataStatistics') || '健康数据统计' }}</span>
          <el-icon class="stat-icon"><DataAnalysis /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-item">
            <div class="stat-label">总数据条数</div>
            <div class="stat-value">{{ statistics['总健康数据条数'] || 0 }}</div>
          </div>
          <div class="stat-item">
            <div class="stat-label">常规检查</div>
            <div class="stat-value">{{ statistics['常规检查数据条数'] || 0 }}</div>
          </div>
          <div class="stat-item">
            <div class="stat-label">性别健康</div>
            <div class="stat-value">{{ statistics['性别健康数据条数'] || 0 }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 图表卡片 -->
    <div v-if="!isLoading" class="chart-section">
      <!-- 用户分布饼图 -->
      <div class="chart-card">
        <h3>{{ $t('admin.userDistribution') || '用户角色分布' }}</h3>
        <div class="chart-container">
          <canvas ref="userDistributionChart"></canvas>
        </div>
      </div>

      <!-- 健康数据分布饼图 -->
      <div class="chart-card">
        <h3>{{ $t('admin.healthDataDistribution') || '健康数据类型分布' }}</h3>
        <div class="chart-container">
          <canvas ref="healthDataDistributionChart"></canvas>
        </div>
      </div>

      <!-- 用户状态分布饼图 -->
      <div class="chart-card">
        <h3>{{ $t('admin.userStatusDistribution') || '用户状态分布' }}</h3>
        <div class="chart-container">
          <canvas ref="userStatusChart"></canvas>
        </div>
      </div>
    </div>

    <!-- 详细统计表格 -->
    <div v-if="!isLoading" class="detail-table-card">
      <h3>{{ $t('admin.detailedStatistics') || '详细统计数据' }}</h3>
      <el-table :data="statisticsTableData" stripe style="width: 100%">
        <el-table-column prop="key" :label="$t('admin.statisticsItem') || '统计项目'" width="200" />
        <el-table-column prop="value" :label="$t('admin.statisticsValue') || '数值'" width="150" />
        <el-table-column prop="description" :label="$t('admin.description') || '描述'" />
      </el-table>
    </div>

    <!-- 错误提示 -->
    <el-alert
      v-if="errorMessage"
      :title="errorMessage"
      type="error"
      :closable="true"
      @close="errorMessage = ''"
      class="error-alert"
    />

    <!-- 成功提示 -->
    <el-alert
      v-if="successMessage"
      :title="successMessage"
      type="success"
      :closable="true"
      @close="successMessage = ''"
      class="success-alert"
    />
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import Chart from 'chart.js/auto'
import { User, CircleCheck, DataAnalysis } from '@element-plus/icons-vue'
import { adminAPI } from '../../services/api'

export default {
  name: 'DataStatistics',
  components: {
    User,
    CircleCheck,
    DataAnalysis
  },
  setup() {
    const userDistributionChart = ref(null)
    const healthDataDistributionChart = ref(null)
    const userStatusChart = ref(null)

    let userDistributionChartInstance = null
    let healthDataDistributionChartInstance = null
    let userStatusChartInstance = null

    const isLoading = ref(true)
    const errorMessage = ref('')
    const successMessage = ref('')
    const statistics = ref({})

    // 默认日期范围（最近30天）
    const getDefaultDateRange = () => {
      const endDate = new Date()
      const startDate = new Date(endDate.getTime() - 30 * 24 * 60 * 60 * 1000)
      return {
        start: startDate.toISOString().split('T')[0],
        end: endDate.toISOString().split('T')[0]
      }
    }

    const defaultRange = getDefaultDateRange()

    const filterData = reactive({
      startDate: defaultRange.start,
      endDate: defaultRange.end
    })

    // 统计表格数据
    const statisticsTableData = computed(() => {
      const data = []
      for (const [key, value] of Object.entries(statistics.value)) {
        data.push({
          key: key,
          value: value,
          description: getStatisticsDescription(key)
        })
      }
      return data
    })

    // 获取统计项目描述
    const getStatisticsDescription = (key) => {
      const descriptions = {
        '总用户数': '系统中所有用户的总数',
        '普通用户数': '角色为普通用户的用户数',
        '医师数': '角色为医师的用户数',
        '管理员数': '角色为管理员的用户数',
        '活跃用户数': '状态为活跃的用户数',
        '禁用用户数': '状态为禁用的用户数',
        '总健康数据条数': '系统中所有健康数据的总条数',
        '常规检查数据条数': '常规检查类型的健康数据条数',
        '性别健康数据条数': '性别健康类型的健康数据条数'
      }
      return descriptions[key] || '-'
    }

    // 初始化用户分布饼图
    const initUserDistributionChart = () => {
      if (!userDistributionChart.value) return

      if (userDistributionChartInstance) {
        userDistributionChartInstance.destroy()
      }

      const userCount = statistics.value['普通用户数'] || 0
      const doctorCount = statistics.value['医师数'] || 0
      const adminCount = statistics.value['管理员数'] || 0

      userDistributionChartInstance = new Chart(userDistributionChart.value, {
        type: 'doughnut',
        data: {
          labels: ['普通用户', '医师', '管理员'],
          datasets: [{
            data: [userCount, doctorCount, adminCount],
            backgroundColor: [
              'rgba(64, 158, 255, 0.8)',
              'rgba(230, 162, 60, 0.8)',
              'rgba(245, 108, 108, 0.8)'
            ],
            borderColor: [
              '#409eff',
              '#e6a23c',
              '#f56c6c'
            ],
            borderWidth: 2
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: true,
          plugins: {
            legend: {
              position: 'bottom'
            }
          }
        }
      })
    }

    // 初始化健康数据分布饼图
    const initHealthDataDistributionChart = () => {
      if (!healthDataDistributionChart.value) return

      if (healthDataDistributionChartInstance) {
        healthDataDistributionChartInstance.destroy()
      }

      const routineCount = statistics.value['常规检查数据条数'] || 0
      const genderCount = statistics.value['性别健康数据条数'] || 0

      healthDataDistributionChartInstance = new Chart(healthDataDistributionChart.value, {
        type: 'doughnut',
        data: {
          labels: ['常规检查', '性别健康'],
          datasets: [{
            data: [routineCount, genderCount],
            backgroundColor: [
              'rgba(103, 194, 58, 0.8)',
              'rgba(144, 147, 153, 0.8)'
            ],
            borderColor: [
              '#67c23a',
              '#909399'
            ],
            borderWidth: 2
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: true,
          plugins: {
            legend: {
              position: 'bottom'
            }
          }
        }
      })
    }

    // 初始化用户状态饼图
    const initUserStatusChart = () => {
      if (!userStatusChart.value) return

      if (userStatusChartInstance) {
        userStatusChartInstance.destroy()
      }

      const activeCount = statistics.value['活跃用户数'] || 0
      const inactiveCount = statistics.value['禁用用户数'] || 0

      userStatusChartInstance = new Chart(userStatusChart.value, {
        type: 'doughnut',
        data: {
          labels: ['活跃', '禁用'],
          datasets: [{
            data: [activeCount, inactiveCount],
            backgroundColor: [
              'rgba(103, 194, 58, 0.8)',
              'rgba(245, 108, 108, 0.8)'
            ],
            borderColor: [
              '#67c23a',
              '#f56c6c'
            ],
            borderWidth: 2
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: true,
          plugins: {
            legend: {
              position: 'bottom'
            }
          }
        }
      })
    }

    // 获取统计数据
    const fetchStatistics = async () => {
      try {
        isLoading.value = true
        errorMessage.value = ''
        successMessage.value = ''

        const response = await adminAPI.getStatistics()

        if (response && response.data) {
          statistics.value = response.data

          // 初始化所有图表
          setTimeout(() => {
            initUserDistributionChart()
            initHealthDataDistributionChart()
            initUserStatusChart()
          }, 100)

          successMessage.value = '数据统计加载成功'
        }
      } catch (error) {
        console.error('获取统计数据失败:', error)
        if (error.response && error.response.data) {
          errorMessage.value = error.response.data.message || '获取统计数据失败'
        } else if (error.message) {
          errorMessage.value = error.message
        } else {
          errorMessage.value = '获取统计数据失败'
        }
        ElMessage.error(errorMessage.value)
      } finally {
        isLoading.value = false
      }
    }

    // 日期变化处理
    const handleDateChange = () => {
      if (filterData.startDate && filterData.endDate) {
        if (new Date(filterData.startDate) > new Date(filterData.endDate)) {
          ElMessage.warning('开始日期不能晚于结束日期')
          return
        }
      }
    }

    // 查询按钮处理
    const handleRefresh = async () => {
      await fetchStatistics()
    }

    // 重置按钮处理
    const handleReset = () => {
      const defaultRange = getDefaultDateRange()
      filterData.startDate = defaultRange.start
      filterData.endDate = defaultRange.end
    }

    // 页面加载时获取数据
    onMounted(async () => {
      await fetchStatistics()
    })

    return {
      userDistributionChart,
      healthDataDistributionChart,
      userStatusChart,
      isLoading,
      errorMessage,
      successMessage,
      filterData,
      statistics,
      statisticsTableData,
      handleDateChange,
      handleRefresh,
      handleReset
    }
  }
}
</script>

<style scoped>
.data-statistics-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 30px;
  border-bottom: 2px solid #409eff;
  padding-bottom: 10px;
}

.page-header h2 {
  margin: 0;
  color: #333;
  font-size: 24px;
  font-weight: bold;
}

.filter-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 20px;
  margin-bottom: 30px;
}

.filter-section {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  align-items: flex-end;
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex: 1;
  min-width: 200px;
}

.filter-group label {
  font-weight: 500;
  color: #333;
  font-size: 14px;
}

.filter-actions {
  display: flex;
  gap: 10px;
  flex: 1;
  min-width: 200px;
  justify-content: flex-start;
}

.statistics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 20px;
  border-top: 4px solid #409eff;
}

.stat-card.user-stat {
  border-top-color: #409eff;
}

.stat-card.status-stat {
  border-top-color: #67c23a;
}

.stat-card.health-stat {
  border-top-color: #e6a23c;
}

.stat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e4e7eb;
}

.stat-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.stat-icon {
  font-size: 24px;
  color: #409eff;
}

.stat-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(100px, 1fr));
  gap: 15px;
}

.stat-item {
  text-align: center;
  padding: 10px;
  background: #f5f7fa;
  border-radius: 4px;
}

.stat-label {
  font-size: 12px;
  color: #606266;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
}

.stat-value.active {
  color: #67c23a;
}

.stat-value.inactive {
  color: #f56c6c;
}

.chart-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.chart-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 30px;
}

.chart-card h3 {
  margin: 0 0 20px 0;
  color: #333;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 2px solid #409eff;
  padding-bottom: 10px;
}

.chart-container {
  position: relative;
  height: 300px;
  width: 100%;
}

.chart-container canvas {
  max-height: 300px;
}

.detail-table-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 30px;
  margin-bottom: 30px;
}

.detail-table-card h3 {
  margin: 0 0 20px 0;
  color: #333;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 2px solid #409eff;
  padding-bottom: 10px;
}

.error-alert {
  margin-top: 20px;
}

.success-alert {
  margin-top: 20px;
}

:deep(.el-table) {
  font-size: 14px;
}

:deep(.el-table__header th) {
  background-color: #f5f7fa;
  font-weight: bold;
}

:deep(.el-table__body tr:hover > td) {
  background-color: #f5f7fa;
}

:deep(.el-date-picker) {
  width: 100%;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .data-statistics-container {
    padding: 10px;
  }

  .page-header h2 {
    font-size: 20px;
  }

  .filter-section {
    flex-direction: column;
    gap: 15px;
  }

  .filter-group {
    min-width: 100%;
  }

  .filter-actions {
    min-width: 100%;
    justify-content: space-between;
  }

  .statistics-grid {
    grid-template-columns: 1fr;
  }

  .chart-section {
    grid-template-columns: 1fr;
  }

  .chart-card {
    padding: 20px;
  }

  .chart-container {
    height: 250px;
  }

  .detail-table-card {
    padding: 20px;
  }

  :deep(.el-table) {
    font-size: 12px;
  }

  :deep(.el-table__header th) {
    padding: 8px 0;
  }

  :deep(.el-table__body td) {
    padding: 8px 0;
  }

  .stat-content {
    grid-template-columns: 1fr 1fr;
  }
}
</style>
