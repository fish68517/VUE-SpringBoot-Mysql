<template>
  <div class="dashboard-container">
    <h2>医院数据看板</h2>
    
    <!-- 统计卡片 -->
    <div class="statistics-grid">
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-value">{{ statistics.totalPosts }}</div>
          <div class="stat-label">岗位发布数</div>
        </div>
      </el-card>
      
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-value">{{ statistics.totalApplications }}</div>
          <div class="stat-label">投递数</div>
        </div>
      </el-card>
      
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-value">{{ statistics.approvedApplications }}</div>
          <div class="stat-label">录用数</div>
        </div>
      </el-card>
      
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-value">{{ statistics.ongoingInternships }}</div>
          <div class="stat-label">实习中人数</div>
        </div>
      </el-card>
      
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-value">{{ statistics.completedInternships }}</div>
          <div class="stat-label">已完成人数</div>
        </div>
      </el-card>
      
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-value">{{ statistics.rejectedApplications }}</div>
          <div class="stat-label">驳回数</div>
        </div>
      </el-card>
    </div>
    
    <!-- 图表区域 -->
    <div class="charts-grid">
      <!-- 申请状态分布 -->
      <el-card class="chart-card">
        <template #header>
          <div class="card-header">
            <span>申请状态分布</span>
            <el-button type="primary" size="small" @click="loadStatistics">刷新</el-button>
          </div>
        </template>
        <div id="statusChart" style="width: 100%; height: 300px;"></div>
      </el-card>
      
      <!-- 实习进度 -->
      <el-card class="chart-card">
        <template #header>
          <div class="card-header">
            <span>实习进度</span>
          </div>
        </template>
        <div id="progressChart" style="width: 100%; height: 300px;"></div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { getHospitalStatistics } from '@/api/hospitalAdmin'

const statistics = ref({
  totalPosts: 0,
  totalApplications: 0,
  approvedApplications: 0,
  ongoingInternships: 0,
  completedInternships: 0,
  rejectedApplications: 0
})

const loadStatistics = async () => {
  try {
    const response = await getHospitalStatistics()
    statistics.value = response.data || statistics.value
    await nextTick()
    renderCharts()
  } catch (error) {
    ElMessage.error('加载统计数据失败: ' + (error.response?.data?.message || error.message))
  }
}

const renderCharts = () => {
  // 申请状态分布图
  const statusChartDom = document.getElementById('statusChart')
  if (statusChartDom && window.echarts) {
    const statusChart = window.echarts.init(statusChartDom)
    const statusOption = {
      tooltip: {
        trigger: 'item'
      },
      legend: {
        orient: 'vertical',
        left: 'left'
      },
      series: [
        {
          name: '申请状态',
          type: 'pie',
          radius: '50%',
          data: [
            { value: statistics.value.approvedApplications, name: '已录用' },
            { value: statistics.value.rejectedApplications, name: '已驳回' },
            { value: statistics.value.totalApplications - statistics.value.approvedApplications - statistics.value.rejectedApplications, name: '待审批' }
          ],
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }
      ]
    }
    statusChart.setOption(statusOption)
  }
  
  // 实习进度图
  const progressChartDom = document.getElementById('progressChart')
  if (progressChartDom && window.echarts) {
    const progressChart = window.echarts.init(progressChartDom)
    const progressOption = {
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        }
      },
      xAxis: {
        type: 'category',
        data: ['实习中', '已完成']
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          data: [statistics.value.ongoingInternships, statistics.value.completedInternships],
          type: 'bar',
          itemStyle: {
            color: '#f56c6c'
          }
        }
      ]
    }
    progressChart.setOption(progressOption)
  }
}

onMounted(() => {
  // 加载ECharts库
  if (!window.echarts) {
    const script = document.createElement('script')
    script.src = 'https://cdn.jsdelivr.net/npm/echarts@5/dist/echarts.min.js'
    script.onload = () => {
      loadStatistics()
    }
    document.head.appendChild(script)
  } else {
    loadStatistics()
  }
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.statistics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.stat-card:hover {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.stat-content {
  padding: 20px 0;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #f56c6c;
  margin-bottom: 10px;
}

.stat-label {
  font-size: 14px;
  color: #606266;
}

.charts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 20px;
}

.chart-card {
  min-height: 400px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
