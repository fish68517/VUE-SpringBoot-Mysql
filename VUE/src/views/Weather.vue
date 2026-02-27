<template>
  <div class="weather-container">
    <Card title="气象数据">
      <el-row :gutter="20" class="filter-row">
        <el-col :xs="24" :sm="12" :md="8">
          <el-input
            v-model="filters.region"
            placeholder="请输入地区"
            @input="handleSearch"
          />
        </el-col>
        <el-col :xs="24" :sm="12" :md="8">
          <el-date-picker
            v-model="filters.date"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"  @change="handleSearch"
            clearable                  />
        </el-col>
        <el-col :xs="24" :sm="12" :md="8">
          <el-button type="primary" @click="handleSearch">查询</el-button>
        </el-col>
      </el-row>

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

    <Card title="气象预报">
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
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { weatherAPI } from '@/api/weather'
import Card from '@/components/common/Card.vue'
import Table from '@/components/common/Table.vue'

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

const columns = [
  { prop: 'region', label: '地区' },
  { prop: 'temperature', label: '温度(°C)' },
  { prop: 'humidity', label: '湿度(%)' },
  { prop: 'precipitation', label: '降水(mm)' },
  { prop: 'windSpeed', label: '风速(m/s)' },
  { prop: 'weatherCondition', label: '天气状况' },
  { prop: 'recordedAt', label: '记录时间' },
]

const handleSearch = async () => {
  try {
    // 构造请求参数
    const params = {
      region: filters.value.region
    }

    // 如果用户在前端选择了日期，我们将其转换为当天的起始和结束时间
    if (filters.value.date) {
      params.startTime = `${filters.value.date} 00:00:00`
      params.endTime = `${filters.value.date} 23:59:59`
    }

    // 重点：改为调用 queryWeather 接口
    const response = await weatherAPI.queryWeather(params)
    
    // 后端返回的 data 是一个数组 (List<WeatherData>)
    const allData = response || []
    
    // ---- 前端模拟分页逻辑 ----
    // 因为后端 query 接口没有做数据库层面的分页，返回的是全部 list
    pagination.value.total = allData.length
    
    const startIdx = (pagination.value.current - 1) * pagination.value.size
    const endIdx = startIdx + pagination.value.size
    
    // 切割当前页需要显示的数据
    weatherData.value = allData.slice(startIdx, endIdx)

  } catch (error) {
    console.error('获取气象数据失败', error)
    // ElMessage.error('获取数据失败或该地区暂无数据')
    weatherData.value = []
    pagination.value.total = 0
  }
}

const handlePageChange = (page) => {
  pagination.value.current = page
  handleSearch()
}

const handlePageSizeChange = (size) => {
  pagination.value.size = size
  pagination.value.current = 1
  handleSearch()
}

onMounted(async () => {
  try {
    const response = await weatherAPI.getForecast()
    forecastData.value = response.data || []
    handleSearch()
  } catch (error) {
    ElMessage.error('获取预报数据失败')
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
