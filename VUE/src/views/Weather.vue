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
            @change="handleSearch"
          />
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
    const response = await weatherAPI.getCurrentWeather({
      region: filters.value.region,
      page: pagination.value.current,
      size: pagination.value.size,
    })
    weatherData.value = response.data.records || []
    pagination.value.total = response.data.total || 0
  } catch (error) {
    ElMessage.error('获取气象数据失败')
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
