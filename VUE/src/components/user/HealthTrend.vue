<template>
  <div class="health-trend-container">
    <div class="page-header">
      <h2>{{ $t('healthData.trends') || '健康趋势分析' }}</h2>
    </div>

    <el-skeleton v-if="isLoading" :rows="8" animated />

    <div v-else class="filter-card">
      <div class="filter-grid">
        <div class="filter-item">
          <label>{{ $t('healthData.startDate') || '开始日期' }}</label>
          <el-date-picker
            v-model="filterData.startDate"
            type="date"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </div>

        <div class="filter-item">
          <label>{{ $t('healthData.endDate') || '结束日期' }}</label>
          <el-date-picker
            v-model="filterData.endDate"
            type="date"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </div>

        <div class="filter-item metrics-selector">
          <label>{{ $t('healthData.dataRange') || '分析指标' }}</label>
          <el-select
            v-model="filterData.selectedMetrics"
            multiple
            collapse-tags
            collapse-tags-tooltip
            placeholder="请选择要分析的指标"
            @change="handleMetricsChange"
          >
            <el-option
              v-for="metric in metricOptions"
              :key="metric.value"
              :label="metric.label"
              :value="metric.value"
            />
          </el-select>
        </div>

        <div class="filter-actions">
          <el-button type="primary" :loading="isLoading" @click="handleRefresh">
            {{ $t('common.search') || '查询' }}
          </el-button>
          <el-button @click="handleReset">
            {{ $t('common.reset') || '重置' }}
          </el-button>
        </div>
      </div>
    </div>

    <div v-if="!isLoading && trendData.length > 0" class="statistics-card">
      <div v-for="stat in statistics" :key="stat.key" class="stat-item">
        <div class="stat-label">{{ stat.label }}</div>
        <div class="stat-value">{{ stat.value }}</div>
        <div class="stat-unit">{{ stat.unit }}</div>
      </div>
    </div>

    <div v-if="!isLoading && trendData.length > 0" class="chart-card">
      <div class="chart-header">
        <div>
          <h3>{{ $t('healthData.trends') || '趋势图表' }}</h3>
          <p>支持按已选指标导出趋势分析报告。</p>
        </div>
        <el-button type="primary" plain @click="handleExport">
          导出分析报告
        </el-button>
      </div>
      <div class="chart-container">
        <canvas ref="chartCanvas"></canvas>
      </div>
    </div>

    <div v-if="!isLoading && trendData.length > 0" class="table-card">
      <h3>{{ $t('healthData.recentData') || '趋势明细' }}</h3>
      <el-table :data="trendData" stripe style="width: 100%">
        <el-table-column prop="recordedAt" :label="$t('healthData.recordDate') || '记录时间'" width="180" />
        <el-table-column
          v-for="metric in selectedMetricOptions"
          :key="metric.value"
          :prop="metric.value"
          :label="metric.tableLabel"
          min-width="120"
        >
          <template #default="{ row }">
            {{ formatMetricValue(metric.value, row) }}
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div v-else-if="!isLoading" class="empty-card">
      <el-empty :description="$t('healthHistory.noRecords') || '当前时间范围内暂无数据'" />
    </div>

    <el-alert
      v-if="errorMessage"
      :title="errorMessage"
      type="error"
      :closable="true"
      @close="errorMessage = ''"
      class="feedback-alert"
    />
  </div>
</template>

<script>
import { computed, nextTick, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import Chart from 'chart.js/auto'
import { healthDataAPI } from '../../services/api'
import { authService } from '../../services/auth'

const METRIC_CONFIG = {
  height: { value: 'height', label: '身高', tableLabel: '身高(cm)', unit: 'cm', color: '#4caf50', decimals: 1 },
  weight: { value: 'weight', label: '体重', tableLabel: '体重(kg)', unit: 'kg', color: '#1976d2', decimals: 1 },
  bloodPressure: { value: 'bloodPressure', label: '血压', tableLabel: '血压(mmHg)', unit: 'mmHg', color: '#ff9800', decimals: 0 },
  heartRate: { value: 'heartRate', label: '心率', tableLabel: '心率(次/分)', unit: '次/分', color: '#ef5350', decimals: 0 },
  bodyTemperature: { value: 'bodyTemperature', label: '体温', tableLabel: '体温(°C)', unit: '°C', color: '#8e24aa', decimals: 1 },
  bloodOxygen: { value: 'bloodOxygen', label: '血氧', tableLabel: '血氧(%)', unit: '%', color: '#009688', decimals: 0 },
  bloodSugar: { value: 'bloodSugar', label: '血糖', tableLabel: '血糖(mmol/L)', unit: 'mmol/L', color: '#ff7043', decimals: 1 },
  sleepDuration: { value: 'sleepDuration', label: '睡眠时长', tableLabel: '睡眠时长(小时)', unit: '小时', color: '#455a64', decimals: 1 }
}

const metricOptions = Object.values(METRIC_CONFIG)

const escapeHtml = (value) =>
  String(value ?? '')
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;')

export default {
  name: 'HealthTrend',
  setup() {
    const chartCanvas = ref(null)
    const isLoading = ref(true)
    const errorMessage = ref('')
    const trendData = ref([])
    let chartInstance = null

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
      selectedMetrics: ['weight', 'heartRate', 'bodyTemperature']
    })

    const selectedMetricOptions = computed(() =>
      filterData.selectedMetrics.map((metric) => METRIC_CONFIG[metric]).filter(Boolean)
    )

    const parseMetricValue = (metricKey, record) => {
      const rawValue = record?.[metricKey]
      if (rawValue === null || rawValue === undefined || rawValue === '') {
        return null
      }

      if (metricKey === 'bloodPressure') {
        const [systolic] = String(rawValue).split('/')
        const numericValue = Number(systolic)
        return Number.isFinite(numericValue) ? numericValue : null
      }

      const numericValue = Number(rawValue)
      return Number.isFinite(numericValue) ? numericValue : null
    }

    const formatNumber = (value, decimals = 1) => {
      const numericValue = Number(value)
      if (!Number.isFinite(numericValue)) {
        return '-'
      }
      return numericValue.toFixed(decimals)
    }

    const formatMetricValue = (metricKey, row) => {
      const config = METRIC_CONFIG[metricKey]
      if (!config) {
        return row?.[metricKey] ?? '-'
      }
      if (metricKey === 'bloodPressure') {
        return row?.bloodPressure || '-'
      }
      return row?.[metricKey] === null || row?.[metricKey] === undefined ? '-' : formatNumber(row[metricKey], config.decimals)
    }

    const buildMetricStats = (metricKey) => {
      const config = METRIC_CONFIG[metricKey]
      const values = trendData.value
        .map((record) => parseMetricValue(metricKey, record))
        .filter((value) => value !== null)

      if (!values.length) {
        return []
      }

      const average = values.reduce((sum, value) => sum + value, 0) / values.length
      const max = Math.max(...values)
      const min = Math.min(...values)

      return [
        { key: `${metricKey}-avg`, label: `${config.label}平均值`, value: formatNumber(average, config.decimals), unit: config.unit },
        { key: `${metricKey}-max`, label: `${config.label}最高值`, value: formatNumber(max, config.decimals), unit: config.unit },
        { key: `${metricKey}-min`, label: `${config.label}最低值`, value: formatNumber(min, config.decimals), unit: config.unit }
      ]
    }

    const statistics = computed(() =>
      selectedMetricOptions.value.flatMap((metric) => buildMetricStats(metric.value))
    )

    const destroyChart = () => {
      if (chartInstance) {
        chartInstance.destroy()
        chartInstance = null
      }
    }

    const initChart = () => {
      if (!chartCanvas.value || trendData.value.length === 0 || selectedMetricOptions.value.length === 0) {
        return
      }

      destroyChart()

      const labels = trendData.value.map((item) => item.recordedAt)
      const datasets = []
      const scales = {
        x: {
          display: true,
          title: {
            display: true,
            text: '记录时间'
          }
        }
      }

      selectedMetricOptions.value.forEach((metric, index) => {
        const axisId = `y${index}`
        datasets.push({
          label: metric.tableLabel,
          data: trendData.value.map((item) => parseMetricValue(metric.value, item)),
          borderColor: metric.color,
          backgroundColor: `${metric.color}22`,
          borderWidth: 2,
          tension: 0.35,
          fill: false,
          spanGaps: true,
          yAxisID: axisId
        })

        scales[axisId] = {
          type: 'linear',
          display: true,
          position: index % 2 === 0 ? 'left' : 'right',
          title: {
            display: true,
            text: metric.tableLabel
          },
          grid: {
            drawOnChartArea: index === 0
          }
        }
      })

      chartInstance = new Chart(chartCanvas.value, {
        type: 'line',
        data: { labels, datasets },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          interaction: {
            mode: 'index',
            intersect: false
          },
          plugins: {
            legend: {
              display: true,
              position: 'top'
            }
          },
          scales
        }
      })
    }

    const fetchTrendData = async () => {
      try {
        isLoading.value = true
        errorMessage.value = ''

        const currentUser = authService.getUser()
        if (!currentUser?.id) {
          throw new Error('无法获取用户信息')
        }

        if (new Date(filterData.startDate) > new Date(filterData.endDate)) {
          throw new Error('开始日期不能晚于结束日期')
        }

        const response = await healthDataAPI.getDataByRange(filterData.startDate, filterData.endDate)
        trendData.value = (response?.data || [])
          .filter((item) => item.dataType !== 'GENDER_SPECIFIC')
          .sort((a, b) => new Date(a.recordedAt) - new Date(b.recordedAt))

        await nextTick()
        if (trendData.value.length > 0) {
          initChart()
        } else {
          destroyChart()
        }
      } catch (error) {
        console.error('Failed to fetch trend data:', error)
        errorMessage.value = error.response?.data?.message || error.message || '获取趋势数据失败'
        ElMessage.error(errorMessage.value)
      } finally {
        isLoading.value = false
      }
    }

    const handleMetricsChange = () => {
      if (filterData.selectedMetrics.length === 0) {
        filterData.selectedMetrics = ['weight']
        ElMessage.warning('至少选择一个分析指标')
      }

      if (trendData.value.length > 0) {
        nextTick(() => {
          initChart()
        })
      }
    }

    const handleRefresh = async () => {
      await fetchTrendData()
    }

    const handleReset = () => {
      const range = getDefaultDateRange()
      filterData.startDate = range.start
      filterData.endDate = range.end
      filterData.selectedMetrics = ['weight', 'heartRate', 'bodyTemperature']
      trendData.value = []
      errorMessage.value = ''
      destroyChart()
    }

    const buildSummaryText = () =>
      statistics.value.map((stat) => `${stat.label}：${stat.value} ${stat.unit}`).join('；')

    const handleExport = () => {
      if (!trendData.value.length) {
        ElMessage.warning('当前没有可导出的分析数据')
        return
      }

      const exportedAt = new Date().toLocaleString('zh-CN')
      const chartImage = chartCanvas.value ? chartCanvas.value.toDataURL('image/png') : ''
      const headerCells = ['记录时间', ...selectedMetricOptions.value.map((metric) => metric.tableLabel)]
        .map((header) => `<th>${escapeHtml(header)}</th>`)
        .join('')

      const rows = trendData.value
        .map((record) => {
          const cells = [
            `<td>${escapeHtml(record.recordedAt)}</td>`,
            ...selectedMetricOptions.value.map((metric) => `<td>${escapeHtml(formatMetricValue(metric.value, record))}</td>`)
          ]
          return `<tr>${cells.join('')}</tr>`
        })
        .join('')

      const statsHtml = statistics.value
        .map((stat) => `
          <div class="stat-card">
            <div class="stat-label">${escapeHtml(stat.label)}</div>
            <div class="stat-value">${escapeHtml(stat.value)}</div>
            <div class="stat-unit">${escapeHtml(stat.unit)}</div>
          </div>
        `)
        .join('')

      const reportHtml = `<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8" />
  <title>健康趋势分析报告</title>
  <style>
    body { font-family: "Microsoft YaHei", sans-serif; margin: 32px; color: #1f2d3d; }
    h1, h2, p { margin: 0 0 16px; }
    .meta { margin-bottom: 24px; line-height: 1.8; color: #606266; }
    .stats { display: grid; grid-template-columns: repeat(auto-fit, minmax(160px, 1fr)); gap: 16px; margin: 24px 0; }
    .stat-card { border: 1px solid #e8eef5; border-radius: 12px; padding: 16px; background: #f8fbff; }
    .stat-label { font-size: 14px; color: #606266; margin-bottom: 8px; }
    .stat-value { font-size: 24px; font-weight: 700; color: #1f2d3d; }
    .stat-unit { font-size: 12px; color: #909399; margin-top: 6px; }
    .chart img { width: 100%; border: 1px solid #e8eef5; border-radius: 12px; }
    table { width: 100%; border-collapse: collapse; margin-top: 20px; }
    th, td { border: 1px solid #e8eef5; padding: 10px 12px; text-align: left; font-size: 13px; }
    th { background: #f5f7fa; }
  </style>
</head>
<body>
  <h1>健康趋势分析报告</h1>
  <div class="meta">
    <div>导出时间：${escapeHtml(exportedAt)}</div>
    <div>分析时间范围：${escapeHtml(filterData.startDate)} 至 ${escapeHtml(filterData.endDate)}</div>
    <div>分析指标：${escapeHtml(selectedMetricOptions.value.map((metric) => metric.label).join('、'))}</div>
    <div>摘要：${escapeHtml(buildSummaryText())}</div>
  </div>
  <h2>指标统计</h2>
  <div class="stats">${statsHtml}</div>
  <h2>趋势图</h2>
  <div class="chart">${chartImage ? `<img src="${chartImage}" alt="trend chart" />` : ''}</div>
  <h2>明细数据</h2>
  <table>
    <thead>
      <tr>${headerCells}</tr>
    </thead>
    <tbody>${rows}</tbody>
  </table>
</body>
</html>`

      const blob = new Blob([reportHtml], { type: 'text/html;charset=utf-8' })
      const url = URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `health-trend-report-${filterData.startDate}-to-${filterData.endDate}.html`
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      URL.revokeObjectURL(url)
      ElMessage.success('分析报告已导出')
    }

    onMounted(async () => {
      await fetchTrendData()
    })

    onBeforeUnmount(() => {
      destroyChart()
    })

    return {
      chartCanvas,
      isLoading,
      errorMessage,
      trendData,
      filterData,
      metricOptions,
      selectedMetricOptions,
      statistics,
      formatMetricValue,
      handleMetricsChange,
      handleRefresh,
      handleReset,
      handleExport
    }
  }
}
</script>

<style scoped>
.health-trend-container {
  width: 100%;
  padding: 8px 0 24px;
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
  font-weight: 700;
}

.filter-card,
.statistics-card,
.chart-card,
.table-card,
.empty-card {
  background: #fff;
  border-radius: 16px;
  border: 1px solid #e8eef5;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.filter-card,
.chart-card,
.table-card,
.empty-card {
  padding: 28px 32px;
}

.statistics-card {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 16px;
  margin: 24px 0;
  padding: 24px;
}

.chart-card,
.table-card,
.empty-card {
  margin-top: 24px;
}

.filter-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 20px;
  align-items: end;
}

.filter-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.filter-item label {
  color: #606266;
  font-size: 14px;
}

.metrics-selector {
  grid-column: span 2;
}

.filter-actions {
  display: flex;
  gap: 12px;
}

.stat-item {
  min-height: 120px;
  padding: 20px;
  border-radius: 14px;
  background: linear-gradient(180deg, #f8fbff 0%, #eef5ff 100%);
  border: 1px solid #e8eef5;
}

.stat-label {
  color: #606266;
  font-size: 14px;
}

.stat-value {
  margin-top: 12px;
  color: #1f2d3d;
  font-size: 30px;
  font-weight: 700;
}

.stat-unit {
  margin-top: 8px;
  color: #909399;
  font-size: 13px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 20px;
  margin-bottom: 20px;
}

.chart-header h3,
.table-card h3 {
  margin: 0 0 6px;
  font-size: 20px;
  color: #1f2d3d;
}

.chart-header p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.chart-container {
  height: 420px;
}

.feedback-alert {
  margin-top: 20px;
}

@media (max-width: 1200px) {
  .filter-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .metrics-selector {
    grid-column: span 2;
  }
}

@media (max-width: 768px) {
  .filter-card,
  .chart-card,
  .table-card,
  .empty-card {
    padding: 20px;
  }

  .filter-grid,
  .statistics-card {
    grid-template-columns: 1fr;
  }

  .metrics-selector {
    grid-column: span 1;
  }

  .chart-header {
    flex-direction: column;
  }

  .chart-container {
    height: 320px;
  }
}
</style>
