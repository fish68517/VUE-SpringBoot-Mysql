<template>
  <div class="dashboard-container" v-loading="loading">
    <div class="dashboard-header">
      <div class="header-left">
        <h2>数据仪表盘</h2>
        <span class="refresh-time">最后更新时间：{{ lastUpdateTime }}</span>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="loadData" :icon="Refresh">刷新数据</el-button>
        <el-button type="success" @click="handleExport" :icon="Download">导出报表</el-button>
      </div>
    </div>

    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card countdown-card">
          <div class="stat-icon">
            <el-icon><Timer /></el-icon>
          </div>
          <div class="stat-info">
            <div class="label">距离音乐节开幕</div>
            <div class="value">{{ stats.festivalCountdown || 0 }} <span class="unit">天</span></div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card shadow="hover" class="stat-card weather-card">
          <div class="stat-icon">
            <el-icon><Sunny /></el-icon> </div>
          <div class="stat-info">
            <div class="label">现场天气</div>
            <div class="value">{{ stats.weather?.temp || '--' }} <span class="unit">℃</span></div>
            <div class="sub-text">{{ stats.weather?.condition || '暂无数据' }}</div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card shadow="hover" class="stat-card order-card">
          <div class="stat-icon">
            <el-icon><ShoppingCart /></el-icon>
          </div>
          <div class="stat-info">
            <div class="label">今日订单量</div>
            <div class="value">{{ stats.todayOrderCount || 0 }}</div>
            <div class="sub-text">
              周同比 {{ calculateGrowth(stats.weekOrderCount) }}%
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card shadow="hover" class="stat-card task-card">
          <div class="stat-icon">
            <el-icon><Camera /></el-icon>
          </div>
          <div class="stat-info">
            <div class="label">打卡任务总数</div>
            <div class="value">{{ stats.taskStats?.total || 0 }}</div>
            <div class="sub-text">
              通过率 {{ (stats.taskStats?.passRate * 100).toFixed(1) }}%
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="16">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>用户注册增长趋势 (近30天)</span>
            </div>
          </template>
          <div ref="userGrowthChartRef" class="chart-container"></div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>分区售票状态</span>
            </div>
          </template>
          <div ref="ticketChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>周边商品销量 TOP10</span>
            </div>
          </template>
          <div ref="productChartRef" class="chart-container"></div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>订单周期分布</span>
            </div>
          </template>
          <div ref="orderPieChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh, Download, Timer, Sunny, ShoppingCart, Camera } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { dashboardApi, type DashboardStats } from '@/api/dashboard' // 确保你创建了 api/dashboard.ts
import { formatDate } from '@/utils/date'

// 状态定义
const loading = ref(false)
const lastUpdateTime = ref('')
const stats = ref<Partial<DashboardStats>>({})

// ECharts DOM 引用
const userGrowthChartRef = ref<HTMLElement | null>(null)
const ticketChartRef = ref<HTMLElement | null>(null)
const productChartRef = ref<HTMLElement | null>(null)
const orderPieChartRef = ref<HTMLElement | null>(null)

// Chart 实例
let userChart: echarts.ECharts | null = null
let ticketChart: echarts.ECharts | null = null
let productChart: echarts.ECharts | null = null
let orderPieChart: echarts.ECharts | null = null

// 辅助函数：计算简单增长率（模拟）
const calculateGrowth = (val: number | undefined) => {
  if (!val) return 0
  return Math.floor(Math.random() * 20) - 5 // 模拟数据
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await dashboardApi.getStats()
    // 假设后端返回的数据结构是 { code: 200, data: { ... } }
    // 如果直接返回 data，请根据 request.ts 的封装调整
    const data = (res as any).data || res // 兼容性处理
    
    stats.value = data
    lastUpdateTime.value = formatDate(new Date(), 'YYYY-MM-DD HH:mm:ss')
    
    // 数据更新后渲染图表
    await nextTick()
    renderCharts()
  } catch (error) {
    console.error(error)
    ElMessage.error('获取仪表盘数据失败')
  } finally {
    loading.value = false
  }
}

// 导出报表
const handleExport = async () => {
  try {
    const blob = await dashboardApi.exportReport()
    // 创建下载链接
    const url = window.URL.createObjectURL(new Blob([blob as any]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', `dashboard_report_${Date.now()}.png`) // 假设是图片
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    ElMessage.success('报表导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

// 渲染图表逻辑
const renderCharts = () => {
  initUserGrowthChart()
  initTicketChart()
  initProductChart()
  initOrderPieChart()
}

// 1. 用户增长图表 (Line)
const initUserGrowthChart = () => {
  if (!userGrowthChartRef.value) return
  if (userChart) userChart.dispose()
  
  userChart = echarts.init(userGrowthChartRef.value)
  const data = stats.value.userGrowth || []
  
  userChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: data.map(item => item.date)
    },
    yAxis: { type: 'value' },
    series: [
      {
        name: '新增用户',
        type: 'line',
        smooth: true,
        data: data.map(item => item.count),
        itemStyle: { color: '#409eff' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64,158,255,0.5)' },
            { offset: 1, color: 'rgba(64,158,255,0.01)' }
          ])
        }
      }
    ]
  })
}

// 2. 票务销售状态 (Bar - 类似热力展示)
const initTicketChart = () => {
  if (!ticketChartRef.value) return
  if (ticketChart) ticketChart.dispose()
  
  ticketChart = echarts.init(ticketChartRef.value)
  const data = stats.value.ticketHeatmap || []
  
  ticketChart.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      formatter: (params: any) => {
        const item = data[params[0].dataIndex]
        return `${item.zoneName}<br/>售出率: ${(item.soldRate * 100).toFixed(1)}%<br/>剩余: ${item.remaining}张`
      }
    },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: data.map(item => item.zoneName) },
    yAxis: { type: 'value', max: 100, name: '售出率(%)' },
    series: [
      {
        data: data.map(item => (item.soldRate * 100).toFixed(1)),
        type: 'bar',
        showBackground: true,
        backgroundStyle: { color: 'rgba(180, 180, 180, 0.2)' },
        itemStyle: {
          color: (params: any) => {
            // 根据售出率变色，模拟热力图效果
            const val = params.value
            if (val > 90) return '#F56C6C' // 红色 - 紧俏
            if (val > 60) return '#E6A23C' // 黄色 - 火热
            return '#67C23A' // 绿色 - 充足
          }
        }
      }
    ]
  })
}

// 3. 商品销量 TOP10 (Horizontal Bar)
const initProductChart = () => {
  if (!productChartRef.value) return
  if (productChart) productChart.dispose()
  
  productChart = echarts.init(productChartRef.value)
  const data = stats.value.topProducts || []
  // 倒序排列，让Top1在最上面
  const sortedData = [...data].reverse()

  productChart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'value' },
    yAxis: { type: 'category', data: sortedData.map(item => item.name) },
    series: [
      {
        name: '销量',
        type: 'bar',
        data: sortedData.map(item => item.sales),
        itemStyle: { color: '#8e44ad' },
        label: { show: true, position: 'right' }
      }
    ]
  })
}

// 4. 订单周期分布 (Pie - 模拟数据，API中用 week/month counts)
const initOrderPieChart = () => {
  if (!orderPieChartRef.value) return
  if (orderPieChart) orderPieChart.dispose()
  
  orderPieChart = echarts.init(orderPieChartRef.value)
  const today = stats.value.todayOrderCount || 0
  const week = stats.value.weekOrderCount || 0
  const month = stats.value.monthOrderCount || 0

  orderPieChart.setOption({
    tooltip: { trigger: 'item' },
    legend: { top: '5%', left: 'center' },
    series: [
      {
        name: '订单分布',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: { show: false, position: 'center' },
        emphasis: {
          label: { show: true, fontSize: 20, fontWeight: 'bold' }
        },
        data: [
          { value: today, name: '今日订单' },
          { value: week - today, name: '本周其他' }, // 简单估算
          { value: month - week, name: '本月其他' },
        ]
      }
    ]
  })
}

// 监听窗口大小变化
const handleResize = () => {
  userChart?.resize()
  ticketChart?.resize()
  productChart?.resize()
  orderPieChart?.resize()
}

onMounted(() => {
  loadData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  userChart?.dispose()
  ticketChart?.dispose()
  productChart?.dispose()
  orderPieChart?.dispose()
})
</script>

<style scoped>
.dashboard-container {
  padding: 0;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.05);
}

.header-left h2 {
  margin: 0;
  font-size: 20px;
  display: inline-block;
  margin-right: 15px;
}

.refresh-time {
  color: #909399;
  font-size: 13px;
}

.stat-cards {
  margin-bottom: 20px;
}

.stat-card {
  height: 100%;
}

.stat-card :deep(.el-card__body) {
  display: flex;
  align-items: center;
  padding: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-size: 30px;
  color: white;
}

.countdown-card .stat-icon { background: linear-gradient(135deg, #ff9a9e, #fad0c4); }
.weather-card .stat-icon { background: linear-gradient(135deg, #a18cd1, #fbc2eb); }
.order-card .stat-icon { background: linear-gradient(135deg, #84fab0, #8fd3f4); }
.task-card .stat-icon { background: linear-gradient(135deg, #fccb90, #d57eeb); }

.stat-info .label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 5px;
}

.stat-info .value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-info .unit {
  font-size: 14px;
  font-weight: normal;
}

.stat-info .sub-text {
  font-size: 12px;
  color: #909399;
}

.chart-row {
  margin-bottom: 20px;
}

.chart-card {
  height: 400px;
}

.chart-container {
  height: 320px;
  width: 100%;
}
</style>