<template>
  <div class="health-trend-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>{{ $t('healthData.trends') || '健康趋势分析' }}</h2>
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

        <div class="filter-group">
          <label>{{ $t('healthData.dataRange') || '健康指标' }}:</label>
          <el-select
            v-model="filterData.selectedMetrics"
            multiple
            :placeholder="$t('common.search') || '选择指标'"
            @change="handleMetricsChange"
          >
            <el-option label="身高 (cm)" value="height" />
            <el-option label="体重 (kg)" value="weight" />
            <el-option label="血压 (mmHg)" value="bloodPressure" />
            <el-option label="心率 (次/分)" value="heartRate" />
          </el-select>
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

    <!-- 统计信息卡片 -->
    <div v-if="!isLoading && trendData.length > 0" class="statistics-card">
      <div class="stat-item" v-for="stat in statistics" :key="stat.key">
        <div class="stat-label">{{ stat.label }}</div>
        <div class="stat-value">{{ stat.value }}</div>
        <div class="stat-unit">{{ stat.unit }}</div>
      </div>
    </div>

    <!-- 趋势图表卡片 -->
    <div v-if="!isLoading && trendData.length > 0" class="chart-card">
      <h3>{{ $t('healthData.trends') || '健康趋势图表' }}</h3>
      <div class="chart-container">
        <canvas ref="chartCanvas"></canvas>
      </div>
    </div>

    <!-- 数据表格 -->
    <div v-if="!isLoading && trendData.length > 0" class="data-table-card">
      <h3>{{ $t('healthData.recentData') || '详细数据' }}</h3>
      <el-table :data="trendData" stripe style="width: 100%">
        <el-table-column prop="recordedAt" :label="$t('healthData.recordDate') || '记录时间'" width="180" />
        <el-table-column 
          v-if="filterData.selectedMetrics.includes('height')"
          prop="height" 
          :label="$t('healthData.height') || '身高(cm)'" 
          width="120" 
        />
        <el-table-column 
          v-if="filterData.selectedMetrics.includes('weight')"
          prop="weight" 
          :label="$t('healthData.weight') || '体重(kg)'" 
          width="120" 
        />
        <el-table-column 
          v-if="filterData.selectedMetrics.includes('bloodPressure')"
          prop="bloodPressure" 
          :label="$t('healthData.bloodPressure') || '血压'" 
          width="120" 
        />
        <el-table-column 
          v-if="filterData.selectedMetrics.includes('heartRate')"
          prop="heartRate" 
          :label="$t('healthData.heartRate') || '心率(次/分)'" 
          width="120" 
        />
      </el-table>
    </div>

    <!-- 无数据提示 -->
    <div v-if="!isLoading && trendData.length === 0" class="no-data">
      <el-empty :description="$t('healthHistory.noRecords') || '暂无数据'" />
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
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import Chart from 'chart.js/auto'
import { healthDataAPI } from '../../services/api'
import { authService } from '../../services/auth'

export default {
  name: 'HealthTrend',
  setup() {
    const chartCanvas = ref(null)
    let chartInstance = null
    const isLoading = ref(true)
    const errorMessage = ref('')
    const trendData = ref([])

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
      endDate: defaultRange.end,
      selectedMetrics: ['weight', 'heartRate']
    })

    // 计算统计信息
    const statistics = computed(() => {
      if (trendData.value.length === 0) return []

      const stats = []

      if (filterData.selectedMetrics.includes('weight')) {
        const weights = trendData.value
          .map(d => d.weight)
          .filter(w => w !== null && w !== undefined)
        if (weights.length > 0) {
          const avgWeight = (weights.reduce((a, b) => a + b, 0) / weights.length).toFixed(2)
          const maxWeight = Math.max(...weights).toFixed(2)
          const minWeight = Math.min(...weights).toFixed(2)
          stats.push(
            { key: 'avgWeight', label: '平均体重', value: avgWeight, unit: 'kg' },
            { key: 'maxWeight', label: '最高体重', value: maxWeight, unit: 'kg' },
            { key: 'minWeight', label: '最低体重', value: minWeight, unit: 'kg' }
          )
        }
      }

      if (filterData.selectedMetrics.includes('heartRate')) {
        const heartRates = trendData.value
          .map(d => d.heartRate)
          .filter(h => h !== null && h !== undefined)
        if (heartRates.length > 0) {
          const avgHR = (heartRates.reduce((a, b) => a + b, 0) / heartRates.length).toFixed(0)
          const maxHR = Math.max(...heartRates)
          const minHR = Math.min(...heartRates)
          stats.push(
            { key: 'avgHR', label: '平均心率', value: avgHR, unit: '次/分' },
            { key: 'maxHR', label: '最高心率', value: maxHR, unit: '次/分' },
            { key: 'minHR', label: '最低心率', value: minHR, unit: '次/分' }
          )
        }
      }

      return stats
    })

    // 初始化图表
    const initChart = () => {
      if (!chartCanvas.value) return

      // 销毁旧图表
      if (chartInstance) {
        chartInstance.destroy()
      }

      // 准备图表数据
      const labels = trendData.value.map(d => d.recordedAt)
      const datasets = []

      const colors = {
        weight: { borderColor: '#409eff', backgroundColor: 'rgba(64, 158, 255, 0.1)' },
        height: { borderColor: '#67c23a', backgroundColor: 'rgba(103, 194, 58, 0.1)' },
        heartRate: { borderColor: '#f56c6c', backgroundColor: 'rgba(245, 108, 108, 0.1)' },
        bloodPressure: { borderColor: '#e6a23c', backgroundColor: 'rgba(230, 162, 60, 0.1)' }
      }

      if (filterData.selectedMetrics.includes('weight')) {
        datasets.push({
          label: '体重 (kg)',
          data: trendData.value.map(d => d.weight),
          borderColor: colors.weight.borderColor,
          backgroundColor: colors.weight.backgroundColor,
          borderWidth: 2,
          tension: 0.4,
          fill: true,
          yAxisID: 'y'
        })
      }

      if (filterData.selectedMetrics.includes('height')) {
        datasets.push({
          label: '身高 (cm)',
          data: trendData.value.map(d => d.height),
          borderColor: colors.height.borderColor,
          backgroundColor: colors.height.backgroundColor,
          borderWidth: 2,
          tension: 0.4,
          fill: true,
          yAxisID: 'y1'
        })
      }

      if (filterData.selectedMetrics.includes('heartRate')) {
        datasets.push({
          label: '心率 (次/分)',
          data: trendData.value.map(d => d.heartRate),
          borderColor: colors.heartRate.borderColor,
          backgroundColor: colors.heartRate.backgroundColor,
          borderWidth: 2,
          tension: 0.4,
          fill: true,
          yAxisID: 'y2'
        })
      }

      if (filterData.selectedMetrics.includes('bloodPressure')) {
        // 血压需要特殊处理（格式为 "120/80"）
        const systolicData = trendData.value.map(d => {
          if (d.bloodPressure && typeof d.bloodPressure === 'string') {
            const parts = d.bloodPressure.split('/')
            return parts.length > 0 ? parseInt(parts[0]) : null
          }
          return null
        })
        datasets.push({
          label: '血压收缩压 (mmHg)',
          data: systolicData,
          borderColor: colors.bloodPressure.borderColor,
          backgroundColor: colors.bloodPressure.backgroundColor,
          borderWidth: 2,
          tension: 0.4,
          fill: true,
          yAxisID: 'y3'
        })
      }

      // 创建新图表
      chartInstance = new Chart(chartCanvas.value, {
        type: 'line',
        data: {
          labels: labels,
          datasets: datasets
        },
        options: {
          responsive: true,
          maintainAspectRatio: true,
          interaction: {
            mode: 'index',
            intersect: false
          },
          plugins: {
            legend: {
              display: true,
              position: 'top'
            },
            title: {
              display: false
            }
          },
          scales: {
            x: {
              display: true,
              title: {
                display: true,
                text: '日期'
              }
            },
            y: {
              type: 'linear',
              display: filterData.selectedMetrics.includes('weight'),
              position: 'left',
              title: {
                display: filterData.selectedMetrics.includes('weight'),
                text: '体重 (kg)'
              }
            },
            y1: {
              type: 'linear',
              display: filterData.selectedMetrics.includes('height'),
              position: 'right',
              title: {
                display: filterData.selectedMetrics.includes('height'),
                text: '身高 (cm)'
              },
              grid: {
                drawOnChartArea: false
              }
            },
            y2: {
              type: 'linear',
              display: filterData.selectedMetrics.includes('heartRate'),
              position: 'right',
              title: {
                display: filterData.selectedMetrics.includes('heartRate'),
                text: '心率 (次/分)'
              },
              grid: {
                drawOnChartArea: false
              }
            },
            y3: {
              type: 'linear',
              display: filterData.selectedMetrics.includes('bloodPressure'),
              position: 'right',
              title: {
                display: filterData.selectedMetrics.includes('bloodPressure'),
                text: '血压 (mmHg)'
              },
              grid: {
                drawOnChartArea: false
              }
            }
          }
        }
      })
    }

    // 获取趋势数据
    const fetchTrendData = async () => {
      try {
        isLoading.value = true
        errorMessage.value = ''

        const currentUser = authService.getUser()
        if (!currentUser || !currentUser.id) {
          throw new Error('无法获取用户信息')
        }

        // 调用API获取数据
        const response = await healthDataAPI.getDataByRange(
          filterData.startDate,
          filterData.endDate
        )

        if (response && response.data) {
          // 按日期排序
          trendData.value = response.data.sort((a, b) => 
            new Date(a.recordedAt) - new Date(b.recordedAt)
          )
          
          // 初始化图表
          if (trendData.value.length > 0) {
            setTimeout(() => {
              initChart()
            }, 100)
          }
        }
      } catch (error) {
        console.error('获取趋势数据失败:', error)
        if (error.response && error.response.data) {
          errorMessage.value = error.response.data.message || '获取数据失败'
        } else if (error.message) {
          errorMessage.value = error.message
        } else {
          errorMessage.value = '获取趋势数据失败'
        }
        ElMessage.error(errorMessage.value)
      } finally {
        isLoading.value = false
      }
    }

    // 日期变化处理
    const handleDateChange = () => {
      // 验证日期范围
      if (filterData.startDate && filterData.endDate) {
        if (new Date(filterData.startDate) > new Date(filterData.endDate)) {
          ElMessage.warning('开始日期不能晚于结束日期')
          return
        }
      }
    }

    // 指标变化处理
    const handleMetricsChange = () => {
      if (filterData.selectedMetrics.length === 0) {
        ElMessage.warning('请至少选择一个健康指标')
        filterData.selectedMetrics = ['weight']
        return
      }
      
      if (trendData.value.length > 0) {
        initChart()
      }
    }

    // 查询按钮处理
    const handleRefresh = async () => {
      await fetchTrendData()
    }

    // 重置按钮处理
    const handleReset = () => {
      const defaultRange = getDefaultDateRange()
      filterData.startDate = defaultRange.start
      filterData.endDate = defaultRange.end
      filterData.selectedMetrics = ['weight', 'heartRate']
      trendData.value = []
      if (chartInstance) {
        chartInstance.destroy()
        chartInstance = null
      }
    }

    // 页面加载时获取数据
    onMounted(async () => {
      await fetchTrendData()
    })

    return {
      chartCanvas,
      isLoading,
      errorMessage,
      filterData,
      trendData,
      statistics,
      handleDateChange,
      handleMetricsChange,
      handleRefresh,
      handleReset
    }
  }
}
</script>

<style scoped>
.health-trend-container {
  max-width: 1200px;
  margin: 0 auto;
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

.statistics-card {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-item {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 20px;
  text-align: center;
  border-left: 4px solid #409eff;
}

.stat-label {
  color: #606266;
  font-size: 14px;
  margin-bottom: 10px;
}

.stat-value {
  color: #333;
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 5px;
}

.stat-unit {
  color: #909399;
  font-size: 12px;
}

.chart-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 30px;
  margin-bottom: 30px;
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
  height: 400px;
  width: 100%;
}

.chart-container canvas {
  max-height: 400px;
}

.data-table-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 30px;
  margin-bottom: 30px;
}

.data-table-card h3 {
  margin: 0 0 20px 0;
  color: #333;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 2px solid #409eff;
  padding-bottom: 10px;
}

.no-data {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 60px 20px;
  text-align: center;
}

.error-alert {
  margin-top: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .health-trend-container {
    padding: 10px;
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

  .statistics-card {
    grid-template-columns: 1fr;
  }

  .chart-card,
  .data-table-card {
    padding: 20px;
  }

  .chart-container {
    height: 300px;
  }

  .page-header h2 {
    font-size: 20px;
  }

  :deep(.el-date-picker) {
    width: 100%;
  }

  :deep(.el-select) {
    width: 100%;
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
}
</style>
