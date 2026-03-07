<template>
  <div class="dashboard">
    <h1>编辑首页</h1>
    
    <el-row :gutter="20">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-value">{{ stats.submitted }}</div>
            <div class="stat-label">待初审</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-value">{{ stats.underReview }}</div>
            <div class="stat-label">审稿中</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-value">{{ stats.revisionRequired }}</div>
            <div class="stat-label">待修改/待编辑</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-value">{{ stats.accepted }}</div>
            <div class="stat-label">已录用/已发布</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>最近 7 天新增投稿趋势</span>
            </div>
          </template>
          <div ref="chartRef" style="height: 400px; width: 100%;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts' // 引入 echarts
import api from '@/services/api'   // 使用统一的带 Token 的 api 实例

// 统计数据
const stats = ref({
  submitted: 0,
  underReview: 0,
  revisionRequired: 0,
  accepted: 0
})

// 图表相关的 ref 和实例
const chartRef = ref(null)
let myChart = null

onMounted(() => {
  loadDashboardData()
  // 监听窗口大小变化，让图表自适应缩放
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  if (myChart) {
    myChart.dispose()
  }
})

const handleResize = () => {
  if (myChart) myChart.resize()
}

// 加载数据
const loadDashboardData = async () => {
  try {
    // 假设使用该接口获取平台上的所有稿件 
    // (如果后端提供了更细致的 dashboard 统计接口，只需将这里替换即可)
    const response = await api.get('/api/manuscripts') 
    
    if (response.code === 200) {
      const manuscripts = response.data || []
      
      // 1. 根据 Manuscript 模型计算状态统计数量
      stats.value.submitted = manuscripts.filter(m => m.status === 'SUBMITTED').length
      stats.value.underReview = manuscripts.filter(m => m.status === 'UNDER_REVIEW').length
      stats.value.revisionRequired = manuscripts.filter(m => m.status === 'REVISION_REQUIRED').length
      stats.value.accepted = manuscripts.filter(m => m.status === 'ACCEPTED').length

      // 2. 渲染最近 7 天的数据曲线图
      renderChart(manuscripts)
    }
  } catch (error) {
    ElMessage.error('加载编辑面板数据失败')
    console.error(error)
  }
}

// 渲染曲线图
const renderChart = (manuscripts) => {
  if (!chartRef.value) return
  
  if (!myChart) {
    myChart = echarts.init(chartRef.value)
  }

  // 构建过去 7 天的日期数组 (例如: '03-01', '03-02')
  const dates = []
  const counts = []
  
  for (let i = 6; i >= 0; i--) {
    const d = new Date()
    d.setDate(d.getDate() - i)
    // 格式化为 MM-DD
    const dateStr = `${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
    dates.push(dateStr)
    counts.push(0) // 初始数量设为 0
  }

  // 遍历所有稿件，如果在最近 7 天内投稿，则将当天数量 +1
  manuscripts.forEach(m => {
    // 根据 Manuscript 模型，投稿日期字段为 submissionDate
    if (!m.submissionDate) return 
    
    const subDate = new Date(m.submissionDate)
    const dateStr = `${String(subDate.getMonth() + 1).padStart(2, '0')}-${String(subDate.getDate()).padStart(2, '0')}`
    
    const index = dates.indexOf(dateStr)
    if (index !== -1) {
      counts[index]++
    }
  })

  // ECharts 曲线图配置
  const option = {
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.9)',
      padding: [10, 15],
      formatter: '{b} <br/> 投稿数量: <b>{c}</b> 篇'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dates,
      axisLine: { lineStyle: { color: '#E0E6F1' } },
      axisLabel: { color: '#666' }
    },
    yAxis: {
      type: 'value',
      minInterval: 1, // 确保坐标轴刻度是整数（不能出现半篇稿件）
      splitLine: { lineStyle: { type: 'dashed', color: '#E0E6F1' } }
    },
    series: [
      {
        name: '投稿数',
        type: 'line',
        data: counts,
        smooth: true, // 开启平滑曲线
        symbol: 'circle',
        symbolSize: 8,
        itemStyle: {
          color: '#409EFF',
          borderColor: '#fff',
          borderWidth: 2
        },
        areaStyle: {
          // 曲线下方的渐变色填充区域
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64,158,255,0.3)' },
            { offset: 1, color: 'rgba(64,158,255,0.05)' }
          ])
        }
      }
    ]
  }

  myChart.setOption(option)
}
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.dashboard h1 {
  margin-bottom: 20px;
  color: #333;
}

.stat-card {
  text-align: center;
  transition: all 0.3s;
  border-radius: 8px;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-content {
  padding: 20px 0;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 10px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.chart-card {
  border-radius: 8px;
}
.card-header {
  font-weight: bold;
  color: #333;
}
</style>