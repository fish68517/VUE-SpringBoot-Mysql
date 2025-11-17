<template>
  <div class="progress-chart">
    <div v-if="loading" class="loading-container">
      <el-icon class="is-loading"><Loading /></el-icon>
    </div>
    <div v-else-if="!hasData" class="empty-state">
      <el-empty description="No data available for the selected period" />
    </div>
    <div v-else>
      <!-- Check-in Trend Chart -->
      <div class="chart-section">
        <h4>Check-in Trend</h4>
        <div ref="checkInChartRef" class="chart-container"></div>
      </div>

      <!-- Calorie Intake Chart -->
      <div class="chart-section">
        <h4>Daily Calorie Intake</h4>
        <div ref="calorieChartRef" class="chart-container"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, nextTick } from 'vue'
import { Loading } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

const props = defineProps({
  checkInData: {
    type: Array,
    default: () => []
  },
  calorieData: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const checkInChartRef = ref(null)
const calorieChartRef = ref(null)
let checkInChart = null
let calorieChart = null

const hasData = ref(false)

const initCheckInChart = () => {
  if (!checkInChartRef.value) return

  checkInChart = echarts.init(checkInChartRef.value)

  const dates = props.checkInData.map(item => item.date)
  const values = props.checkInData.map(item => item.count)

  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: '{b}: {c} check-ins'
    },
    xAxis: {
      type: 'category',
      data: dates,
      axisLabel: {
        rotate: 45
      }
    },
    yAxis: {
      type: 'value',
      minInterval: 1
    },
    series: [
      {
        name: 'Check-ins',
        type: 'line',
        data: values,
        smooth: true,
        itemStyle: {
          color: '#409EFF'
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
              { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
            ]
          }
        }
      }
    ],
    grid: {
      left: '3%',
      right: '4%',
      bottom: '15%',
      containLabel: true
    }
  }

  checkInChart.setOption(option)
}

const initCalorieChart = () => {
  if (!calorieChartRef.value) return

  calorieChart = echarts.init(calorieChartRef.value)

  const dates = props.calorieData.map(item => item.date)
  const values = props.calorieData.map(item => item.calories)

  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: '{b}: {c} kcal'
    },
    xAxis: {
      type: 'category',
      data: dates,
      axisLabel: {
        rotate: 45
      }
    },
    yAxis: {
      type: 'value',
      name: 'Calories (kcal)'
    },
    series: [
      {
        name: 'Calories',
        type: 'bar',
        data: values,
        itemStyle: {
          color: '#67C23A'
        }
      }
    ],
    grid: {
      left: '3%',
      right: '4%',
      bottom: '15%',
      containLabel: true
    }
  }

  calorieChart.setOption(option)
}

const updateCharts = async () => {
  hasData.value = props.checkInData.length > 0 || props.calorieData.length > 0

  if (!hasData.value) return

  await nextTick()

  if (props.checkInData.length > 0) {
    initCheckInChart()
  }

  if (props.calorieData.length > 0) {
    initCalorieChart()
  }
}

const resizeCharts = () => {
  checkInChart?.resize()
  calorieChart?.resize()
}

watch(() => [props.checkInData, props.calorieData], () => {
  updateCharts()
}, { deep: true })

onMounted(() => {
  updateCharts()
  window.addEventListener('resize', resizeCharts)
})

// Cleanup
import { onBeforeUnmount } from 'vue'
onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeCharts)
  checkInChart?.dispose()
  calorieChart?.dispose()
})
</script>

<style scoped>
.progress-chart {
  width: 100%;
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 60px;
  color: #409EFF;
  font-size: 32px;
}

.empty-state {
  padding: 40px;
}

.chart-section {
  margin-bottom: 30px;
}

.chart-section h4 {
  margin: 0 0 15px 0;
  color: #303133;
  font-size: 16px;
  font-weight: 600;
}

.chart-container {
  width: 100%;
  height: 300px;
}
</style>
