<template>
  <div class="weather-container">
    <Card title="气象数据">
      <el-row :gutter="20" class="filter-row">
        <el-col :xs="24" :sm="12" :md="8">
          <el-input
            v-model="filters.region"
            placeholder="请输入地区"
            @input="handleSearch"
            clearable
          />
        </el-col>
        <el-col :xs="24" :sm="12" :md="8">
          <el-date-picker
            v-model="filters.date"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
            @change="handleSearch"
            clearable
            style="width: 100%;"
          />
        </el-col>
        <el-col :xs="24" :sm="12" :md="8">
          <el-button type="primary" @click="handleSearch">查询</el-button>
        </el-col>
      </el-row>

      <div 
        ref="chartRef" 
        style="width: 100%; height: 350px; margin-bottom: 20px;" 
        v-show="hasChartData"
      ></div>
      <el-empty v-show="!hasChartData" description="暂无图表数据，请尝试更换地区或日期" style="height: 350px;" />

      <Table
        :data="weatherData"
        :columns="columns"
        :show-actions="false"
        :show-pagination="true"
        :current-page="pagination.current"
        :page-size="pagination.size"
        :total="pagination.total"
        @page-change="handlePageChange"
        @page-size-change="handlePageSizeChange"
      />
    </Card>

    <Card title="气象预报" v-if="false">
      <el-row :gutter="20">
        <el-col
          v-for="forecast in forecastData"
          :key="forecast.id"
          :xs="24"
          :sm="12"
          :md="8"
        >
          <div class="forecast-card">
            <h4>{{ forecast.date }}</h4>
            <p>温度: {{ forecast.temperature }}°C</p>
            <p>湿度: {{ forecast.humidity }}%</p>
            <p>降水: {{ forecast.precipitation }}mm</p>
            <p>风速: {{ forecast.windSpeed }}m/s</p>
          </div>
        </el-col>
      </el-row>
    </Card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { weatherAPI } from '@/api/weather'
import Card from '@/components/common/Card.vue'
import Table from '@/components/common/Table.vue'
import * as echarts from 'echarts' // 引入 echarts

// --- 状态变量 ---
const filters = ref({
  region: '',
  date: null,
})

const weatherData = ref([])
const forecastData = ref([])
const pagination = ref({
  current: 1,
  size: 10,
  total: 0,
})

// 图表相关变量
const chartRef = ref(null)
let chartInstance = null
const hasChartData = ref(false)

const columns = [
  { prop: 'region', label: '地区' },
  { prop: 'temperature', label: '温度(°C)' },
  { prop: 'humidity', label: '湿度(%)' },
  { prop: 'precipitation', label: '降水(mm)' },
  { prop: 'windSpeed', label: '风速(m/s)' },
  { prop: 'weatherCondition', label: '天气状况' },
  { prop: 'recordedAt', label: '记录时间' },
]

// --- 获取与处理数据 ---
const handleSearch = async () => {
  try {
    const params = {
      region: filters.value.region
    }

    if (filters.value.date) {
      params.startTime = `${filters.value.date} 00:00:00`
      params.endTime = `${filters.value.date} 23:59:59`
    }

    const response = await weatherAPI.queryWeather(params)
    const allData = response || []
    
    // 渲染图表 (传递所有查询到的数据，因为图表不需要分页)
    renderChart(allData)
    
    // 分页计算 (表格依然需要分页)
    pagination.value.total = allData.length
    const startIdx = (pagination.value.current - 1) * pagination.value.size
    const endIdx = startIdx + pagination.value.size
    
    weatherData.value = allData.slice(startIdx, endIdx)

  } catch (error) {
    console.error('获取气象数据失败', error)
    weatherData.value = []
    pagination.value.total = 0
    hasChartData.value = false
  }
}

// --- 绘制 ECharts 折线图 ---
const renderChart = (data) => {
  if (!data || data.length === 0) {
    hasChartData.value = false
    if (chartInstance) {
      chartInstance.clear()
    }
    return
  }

  hasChartData.value = true

  // 1. 将数据按时间升序排列 (保证折线图从左到右的时间顺序)
  const sortedData = [...data].sort((a, b) => new Date(a.recordedAt) - new Date(b.recordedAt))

  // 2. 提取 X轴 和 Y轴 的数据
  // 如果是同一天的数据，X轴截取 HH:mm 即可；如果是多天，可以截取 MM-DD HH:mm
  const xData = sortedData.map(item => {
    const dateObj = new Date(item.recordedAt)
    const month = String(dateObj.getMonth() + 1).padStart(2, '0')
    const day = String(dateObj.getDate()).padStart(2, '0')
    const hours = String(dateObj.getHours()).padStart(2, '0')
    const minutes = String(dateObj.getMinutes()).padStart(2, '0')
    return `${month}-${day} ${hours}:${minutes}`
  })
  const tempData = sortedData.map(item => item.temperature)
  const humidityData = sortedData.map(item => item.humidity)

  // 3. 确保 DOM 渲染完毕后初始化图表
  nextTick(() => {
    if (!chartInstance) {
      chartInstance = echarts.init(chartRef.value)
    }

    // 4. 配置项 (双 Y 轴折线图)
    const option = {
      tooltip: {
        trigger: 'axis',
        axisPointer: { type: 'cross' }
      },
      legend: {
        data: ['温度 (°C)', '湿度 (%)']
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: [
        {
          type: 'category',
          boundaryGap: false,
          data: xData
        }
      ],
      yAxis: [
        {
          type: 'value',
          name: '温度 (°C)',
          position: 'left',
          axisLabel: { formatter: '{value} °C' },
          splitLine: { show: true, lineStyle: { type: 'dashed' } }
        },
        {
          type: 'value',
          name: '湿度 (%)',
          position: 'right',
          axisLabel: { formatter: '{value} %' },
          splitLine: { show: false } // 隐藏第二个 Y轴 的网格线，防止杂乱
        }
      ],
      series: [
        {
          name: '温度 (°C)',
          type: 'line',
          yAxisIndex: 0,
          smooth: true, // 平滑曲线
          itemStyle: { color: '#f56c6c' },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(245, 108, 108, 0.3)' },
              { offset: 1, color: 'rgba(245, 108, 108, 0.1)' }
            ])
          },
          data: tempData
        },
        {
          name: '湿度 (%)',
          type: 'line',
          yAxisIndex: 1,
          smooth: true,
          itemStyle: { color: '#409eff' },
          data: humidityData
        }
      ]
    }

    chartInstance.setOption(option)
  })
}

// --- 分页与生命周期 ---
const handlePageChange = (page) => {
  pagination.value.current = page
  handleSearch() // 即使重新搜索，图表也会用全部数据重绘，保证折线图始终完整
}

const handlePageSizeChange = (size) => {
  pagination.value.size = size
  pagination.value.current = 1
  handleSearch()
}

// 监听窗口大小变化，让图表自适应缩放
const resizeChart = () => {
  if (chartInstance) {
    chartInstance.resize()
  }
}

onMounted(async () => {
  window.addEventListener('resize', resizeChart)
  try {
    const response = await weatherAPI.getForecast()
    forecastData.value = response || []
    handleSearch()
  } catch (error) {
    ElMessage.error('获取预报数据失败')
  }
})

onUnmounted(() => {
  window.removeEventListener('resize', resizeChart)
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
  }
})
</script>

<style scoped>
.weather-container {
  padding: 20px;
}

.filter-row {
  margin-bottom: 20px;
}

.forecast-card {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 15px;
  background-color: #f5f7fa;
}

.forecast-card h4 {
  margin: 0 0 10px 0;
  color: #409eff;
}

.forecast-card p {
  margin: 5px 0;
  font-size: 14px;
}
</style>