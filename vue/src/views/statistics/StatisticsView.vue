<template>
  <div class="statistics-page">
    <section class="page-heading-panel compact-heading">
      <div>
        <p class="eyebrow">REGIONAL ANALYTICS</p>
        <h1>区域使用统计</h1>
        <p>对比各区域充电设施规模、当前使用率，并查看选定区域的七日变化趋势。</p>
      </div>
      <div class="heading-stat"><strong>{{ ranking.length }}</strong><span>个统计区域</span></div>
    </section>

    <section class="statistics-filter-bar">
      <div>
        <label>统计日期</label>
        <el-date-picker v-model="filters.statDate" type="date" value-format="YYYY-MM-DD" :clearable="false" />
      </div>
      <div>
        <label>趋势区域</label>
        <el-select v-model="filters.regionId" placeholder="请选择区域" style="width: 180px">
          <el-option v-for="item in ranking" :key="item.regionId" :label="item.regionName" :value="item.regionId" />
        </el-select>
      </div>
      <div>
        <label>趋势日期范围</label>
        <el-date-picker
          v-model="filters.dateRange"
          type="daterange"
          value-format="YYYY-MM-DD"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :clearable="false"
        />
      </div>
      <el-button type="primary" :loading="loading" @click="loadAllData">刷新统计</el-button>
    </section>

    <section class="statistics-summary-grid">
      <article><span>统计站点</span><strong>{{ totalOf('stationCount') }}</strong><small>个</small></article>
      <article><span>统计充电桩</span><strong>{{ totalOf('pileCount') }}</strong><small>个</small></article>
      <article><span>当前空闲</span><strong>{{ totalOf('freeCount') }}</strong><small>个</small></article>
      <article><span>当日充电量</span><strong>{{ totalEnergy }}</strong><small>kWh</small></article>
    </section>

    <section v-loading="loading" class="chart-grid">
      <article class="chart-card">
        <div class="card-section-title">
          <div><p class="eyebrow">USAGE RANKING</p><h2>区域使用率排行</h2></div>
          <span>{{ filters.statDate }}</span>
        </div>
        <div ref="barChartElement" class="chart-canvas"></div>
      </article>
      <article class="chart-card">
        <div class="card-section-title">
          <div><p class="eyebrow">SEVEN-DAY TREND</p><h2>{{ selectedRegionName }}使用趋势</h2></div>
          <span>{{ filters.dateRange?.[0] }} 至 {{ filters.dateRange?.[1] }}</span>
        </div>
        <div ref="lineChartElement" class="chart-canvas"></div>
      </article>
    </section>

    <section class="detail-card statistics-table-card">
      <div class="card-section-title">
        <div><p class="eyebrow">REGION DATA</p><h2>区域统计明细</h2></div>
        <span>使用率 = 使用中数量 ÷ 充电桩数量</span>
      </div>
      <el-table :data="ranking" stripe empty-text="该日期暂无统计数据" @row-click="selectRegion">
        <el-table-column type="index" label="排名" width="70" />
        <el-table-column prop="regionName" label="区域" min-width="120" />
        <el-table-column prop="stationCount" label="站点数" width="90" />
        <el-table-column prop="pileCount" label="充电桩" width="90" />
        <el-table-column prop="freeCount" label="空闲" width="80" />
        <el-table-column prop="usingCount" label="使用中" width="90" />
        <el-table-column prop="faultCount" label="故障" width="80" />
        <el-table-column prop="usageCount" label="使用次数" width="100" />
        <el-table-column label="充电量" width="120">
          <template #default="scope">{{ scope.row.energyKwh }} kWh</template>
        </el-table-column>
        <el-table-column label="使用率" min-width="170">
          <template #default="scope">
            <div class="rate-cell"><el-progress :percentage="Number(scope.row.usageRate)" :stroke-width="8" /><strong>{{ scope.row.usageRate }}%</strong></div>
          </template>
        </el-table-column>
      </el-table>
    </section>
  </div>
</template>

<script setup>
import { computed, nextTick, onMounted, onUnmounted, reactive, ref } from 'vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import { getRegionStatistics, getRegionTrend } from '../../api/statistics'

const today = new Date()
const startDay = new Date(today)
startDay.setDate(today.getDate() - 6)

const filters = reactive({
  statDate: formatDate(today),
  regionId: null,
  dateRange: [formatDate(startDay), formatDate(today)]
})

const ranking = ref([])
const trend = ref([])
const loading = ref(false)
const barChartElement = ref()
const lineChartElement = ref()
let barChart
let lineChart

const selectedRegionName = computed(() => ranking.value.find(item => item.regionId === filters.regionId)?.regionName || '')
const totalEnergy = computed(() => ranking.value.reduce((sum, item) => sum + Number(item.energyKwh || 0), 0).toFixed(2))
const totalOf = key => ranking.value.reduce((sum, item) => sum + Number(item[key] || 0), 0)

const loadAllData = async () => {
  loading.value = true
  try {
    ranking.value = await getRegionStatistics({ statDate: filters.statDate })
    if (!ranking.value.some(item => item.regionId === filters.regionId)) {
      filters.regionId = ranking.value[0]?.regionId || null
    }
    trend.value = filters.regionId
      ? await getRegionTrend({
          regionId: filters.regionId,
          startDate: filters.dateRange[0],
          endDate: filters.dateRange[1]
        })
      : []
    await nextTick()
    renderCharts()
  } catch (error) {
    ElMessage.error(error.message || '统计数据加载失败')
  } finally {
    loading.value = false
  }
}

const selectRegion = async row => {
  filters.regionId = row.regionId
  loading.value = true
  try {
    trend.value = await getRegionTrend({
      regionId: filters.regionId,
      startDate: filters.dateRange[0],
      endDate: filters.dateRange[1]
    })
    renderLineChart()
  } catch (error) {
    ElMessage.error(error.message || '趋势数据加载失败')
  } finally {
    loading.value = false
  }
}

const renderCharts = () => {
  if (!barChartElement.value || !lineChartElement.value) return
  barChart ||= echarts.init(barChartElement.value)
  lineChart ||= echarts.init(lineChartElement.value)
  barChart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: 50, right: 24, top: 28, bottom: 46 },
    xAxis: { type: 'category', data: ranking.value.map(item => item.regionName), axisLabel: { color: '#687580' } },
    yAxis: { type: 'value', name: '使用率 %', max: 100, splitLine: { lineStyle: { color: '#edf1f2' } } },
    series: [{
      type: 'bar',
      data: ranking.value.map(item => Number(item.usageRate)),
      barMaxWidth: 44,
      itemStyle: { color: '#10a674', borderRadius: [7, 7, 0, 0] },
      label: { show: true, position: 'top', formatter: '{c}%' }
    }]
  }, true)
  renderLineChart()
}

const renderLineChart = () => {
  if (!lineChartElement.value) return
  lineChart ||= echarts.init(lineChartElement.value)
  lineChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['使用率', '充电次数'], bottom: 0 },
    grid: { left: 50, right: 50, top: 28, bottom: 52 },
    xAxis: { type: 'category', data: trend.value.map(item => item.statDate.slice(5)), boundaryGap: false },
    yAxis: [
      { type: 'value', name: '使用率 %', max: 100, splitLine: { lineStyle: { color: '#edf1f2' } } },
      { type: 'value', name: '次数', splitLine: { show: false } }
    ],
    series: [
      {
        name: '使用率',
        type: 'line',
        smooth: true,
        data: trend.value.map(item => Number(item.usageRate)),
        lineStyle: { width: 3, color: '#10a674' },
        itemStyle: { color: '#10a674' },
        areaStyle: { color: 'rgba(16, 166, 116, 0.12)' }
      },
      {
        name: '充电次数',
        type: 'line',
        yAxisIndex: 1,
        smooth: true,
        data: trend.value.map(item => item.usageCount),
        lineStyle: { width: 2, color: '#4d78cc' },
        itemStyle: { color: '#4d78cc' }
      }
    ]
  }, true)
}

const handleResize = () => {
  barChart?.resize()
  lineChart?.resize()
}

function formatDate(date) {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

onMounted(() => {
  window.addEventListener('resize', handleResize)
  loadAllData()
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  barChart?.dispose()
  lineChart?.dispose()
})
</script>
