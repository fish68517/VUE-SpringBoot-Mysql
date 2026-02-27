<template>
  <div class="admin-statistics">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #409eff">
              <el-icon><Calendar /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.todayReservations || 0 }}</div>
              <div class="stat-label">今日预约</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #67c23a">
              <el-icon><Check /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.todayCheckIns || 0 }}</div>
              <div class="stat-label">今日签到</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #e6a23c">
              <el-icon><DataAnalysis /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.monthReservations || 0 }}</div>
              <div class="stat-label">本月预约</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 20px">
      <template #header>
        <span>数据概览</span>
      </template>
      <div class="overview">
        <p>统计日期：{{ stats.date }}</p>
        <el-divider />
        <el-descriptions :column="2" border>
          <el-descriptions-item label="今日预约数">{{ stats.todayReservations || 0 }} 次</el-descriptions-item>
          <el-descriptions-item label="今日签到数">{{ stats.todayCheckIns || 0 }} 次</el-descriptions-item>
          <el-descriptions-item label="本月预约总数">{{ stats.monthReservations || 0 }} 次</el-descriptions-item>
          <el-descriptions-item label="签到率">
            {{ stats.todayReservations > 0
              ? ((stats.todayCheckIns / stats.todayReservations) * 100).toFixed(1)
              : 0 }}%
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getStatistics } from '../../api/reservation'

const stats = ref({})

const loadStats = async () => {
  const res = await getStatistics()
  stats.value = res.data
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.admin-statistics {
  max-width: 1000px;
  margin: 0 auto;
  text-align: center;
}

.stat-card {
  height: 120px;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 10px;
  justify-content: center;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 28px;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  color: #909399;
  margin-top: 5px;
}

.stat-info {
  text-align: center;
}

.overview {
  padding: 10px;
}
</style>
