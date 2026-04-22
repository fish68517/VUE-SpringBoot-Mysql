<template>
  <div class="analytics-wrap">
    <div class="stat-grid">
      <div class="stat-card bg-blue">
        <h3>用户总数</h3>
        <p>{{ summary.userCount || 0 }}</p>
      </div>
      <div class="stat-card bg-green">
        <h3>设备总数</h3>
        <p>{{ summary.deviceCount || 0 }}</p>
      </div>
      <div class="stat-card bg-orange">
        <h3>巡检记录</h3>
        <p>{{ summary.recordCount || 0 }}</p>
      </div>
      <div class="stat-card bg-red">
        <h3>异常缺陷</h3>
        <p>{{ summary.defectCount || 0 }}</p>
      </div>
      <div class="stat-card bg-indigo">
        <h3>维修工单</h3>
        <p>{{ summary.repairOrderCount || 0 }}</p>
      </div>
      <div class="stat-card bg-teal">
        <h3>公告数量</h3>
        <p>{{ summary.noticeCount || 0 }}</p>
      </div>
    </div>

    <div class="grid-2" style="margin-top: 20px;">
      <section class="page-card chart-panel">
        <div class="toolbar">
          <div class="toolbar-title">近 7 日业务趋势</div>
        </div>
        <div ref="trendChartRef" class="chart-box"></div>
      </section>
      <section class="page-card chart-panel">
        <div class="toolbar">
          <div class="toolbar-title">任务状态分布</div>
        </div>
        <div ref="taskChartRef" class="chart-box"></div>
      </section>
    </div>

    <div class="grid-2" style="margin-top: 20px;">
      <section class="page-card chart-panel">
        <div class="toolbar">
          <div class="toolbar-title">缺陷等级分布</div>
        </div>
        <div ref="defectChartRef" class="chart-box"></div>
      </section>
      <section class="page-card chart-panel">
        <div class="toolbar">
          <div class="toolbar-title">问题设备 TOP5</div>
        </div>
        <div ref="deviceChartRef" class="chart-box"></div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import * as echarts from 'echarts'
import http from '../api/http'

const summary = ref({})
const trendChartRef = ref(null)
const taskChartRef = ref(null)
const defectChartRef = ref(null)
const deviceChartRef = ref(null)

let trendChart
let taskChart
let defectChart
let deviceChart

async function loadStatistics() {
  const data = await http.get('/api/statistics/overview')
  summary.value = data.summary || {}
  await nextTick()
  renderCharts(data)
}

function renderCharts(data) {
  destroyCharts()
  trendChart = echarts.init(trendChartRef.value)
  taskChart = echarts.init(taskChartRef.value)
  defectChart = echarts.init(defectChartRef.value)
  deviceChart = echarts.init(deviceChartRef.value)

  trendChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['巡检记录', '异常缺陷', '维修工单'] },
    xAxis: { type: 'category', data: data.days || [] },
    yAxis: { type: 'value' },
    series: [
      { name: '巡检记录', type: 'line', smooth: true, data: data.recordTrend || [] },
      { name: '异常缺陷', type: 'line', smooth: true, data: data.defectTrend || [] },
      { name: '维修工单', type: 'line', smooth: true, data: data.repairTrend || [] }
    ]
  })

  taskChart.setOption({
    tooltip: { trigger: 'item' },
    series: [
      {
        type: 'pie',
        radius: ['45%', '72%'],
        data: data.taskStatusStats || []
      }
    ]
  })

  defectChart.setOption({
    tooltip: { trigger: 'item' },
    series: [
      {
        type: 'pie',
        radius: '70%',
        data: data.defectLevelStats || []
      }
    ]
  })

  deviceChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: (data.topDevices || []).map(item => item.name),
      axisLabel: { interval: 0, rotate: 20 }
    },
    yAxis: { type: 'value' },
    series: [
      {
        type: 'bar',
        data: (data.topDevices || []).map(item => item.value),
        itemStyle: {
          color: '#1f7bff',
          borderRadius: [8, 8, 0, 0]
        }
      }
    ]
  })
}

function destroyCharts() {
  ;[trendChart, taskChart, defectChart, deviceChart].forEach(chart => {
    if (chart) {
      chart.dispose()
    }
  })
}

function handleResize() {
  ;[trendChart, taskChart, defectChart, deviceChart].forEach(chart => chart?.resize())
}

onMounted(() => {
  loadStatistics()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  destroyCharts()
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.chart-panel {
  padding: 24px;
}

.chart-box {
  width: 100%;
  height: 360px;
}
</style>
