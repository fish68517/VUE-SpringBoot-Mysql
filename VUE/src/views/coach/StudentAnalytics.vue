<template>
  <Layout>
    <div class="student-analytics">
      <div class="page-header">
        <h2>Student Analytics</h2>
      </div>

      <!-- Filters -->
      <el-card class="filter-card">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12">
            <el-form-item label="Select Student">
              <el-select
                v-model="selectedStudentId"
                placeholder="Select a student"
                style="width: 100%"
                @change="fetchAnalytics"
              >
                <el-option
                  v-for="student in students"
                  :key="student.id"
                  :label="student.username"
                  :value="student.id"
                >
                  <div class="student-option">
                    <el-avatar :src="student.avatar" :size="30">
                      {{ student.username.charAt(0).toUpperCase() }}
                    </el-avatar>
                    <span>{{ student.username }}</span>
                  </div>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="Time Period">
              <el-select
                v-model="timePeriod"
                placeholder="Select time period"
                style="width: 100%"
                @change="fetchAnalytics"
              >
                <el-option label="Last 7 Days" :value="7" />
                <el-option label="Last 30 Days" :value="30" />
                <el-option label="Last 90 Days" :value="90" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-card>

      <!-- Loading State -->
      <div v-if="loading" class="loading-container">
        <el-icon class="is-loading" :size="40"><Loading /></el-icon>
      </div>

      <!-- No Student Selected -->
      <div v-else-if="!selectedStudentId" class="empty-state">
        <el-empty description="Please select a student to view analytics" />
      </div>

      <!-- Analytics Content -->
      <div v-else-if="analytics">
        <!-- Insufficient Data Message -->
        <el-alert
          v-if="!hasEnoughData"
          type="info"
          :closable="false"
          class="info-alert"
        >
          <template #title>
            Insufficient data available for the selected period. Analytics may be limited.
          </template>
        </el-alert>

        <!-- Statistics Cards -->
        <el-row :gutter="20" class="stats-row">
          <el-col :xs="24" :sm="12" :md="6">
            <el-card class="stat-card">
              <div class="stat-content">
                <el-icon class="stat-icon" :size="35" color="#409EFF">
                  <Calendar />
                </el-icon>
                <div class="stat-info">
                  <div class="stat-value">{{ analytics.totalCheckIns || 0 }}</div>
                  <div class="stat-label">Total Check-ins</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :xs="24" :sm="12" :md="6">
            <el-card class="stat-card">
              <div class="stat-content">
                <el-icon class="stat-icon" :size="35" color="#67C23A">
                  <TrendCharts />
                </el-icon>
                <div class="stat-info">
                  <div class="stat-value">{{ analytics.checkInFrequency || 0 }}%</div>
                  <div class="stat-label">Check-in Frequency</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :xs="24" :sm="12" :md="6">
            <el-card class="stat-card">
              <div class="stat-content">
                <el-icon class="stat-icon" :size="35" color="#E6A23C">
                  <Food />
                </el-icon>
                <div class="stat-info">
                  <div class="stat-value">{{ analytics.averageCalories || 0 }}</div>
                  <div class="stat-label">Avg Calories/Day</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :xs="24" :sm="12" :md="6">
            <el-card class="stat-card">
              <div class="stat-content">
                <el-icon class="stat-icon" :size="35" color="#F56C6C">
                  <Sunny />
                </el-icon>
                <div class="stat-info">
                  <div class="stat-value">{{ analytics.activeDays || 0 }}</div>
                  <div class="stat-label">Active Days</div>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <!-- Plan Completion Rate -->
        <el-card class="section-card">
          <template #header>
            <span>Training Plan Completion Rate</span>
          </template>
          <div class="completion-rate">
            <el-progress
              :percentage="analytics.planCompletionRate || 0"
              :color="getProgressColor(analytics.planCompletionRate)"
              :stroke-width="20"
            />
            <p class="completion-text">
              {{ analytics.planCompletionRate || 0 }}% of training plans completed
            </p>
          </div>
        </el-card>

        <!-- Charts -->
        <el-card class="section-card">
          <template #header>
            <span>Activity Trends</span>
          </template>
          <ProgressChart
            :check-in-data="chartData.checkIns"
            :calorie-data="chartData.calories"
            :loading="loading"
          />
        </el-card>
      </div>
    </div>
  </Layout>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { Calendar, TrendCharts, Food, Sunny, Loading } from '@element-plus/icons-vue'
import Layout from '@/components/common/Layout.vue'
import ProgressChart from '@/components/charts/ProgressChart.vue'
import { getMyStudents, getStudentAnalytics, getStudentCheckIns, getStudentDietRecords } from '@/api/coach'
import { showError } from '@/utils/feedback'

const students = ref([])
const selectedStudentId = ref(null)
const timePeriod = ref(30)
const loading = ref(false)
const analytics = ref(null)

const chartData = reactive({
  checkIns: [],
  calories: []
})

const hasEnoughData = computed(() => {
  if (!analytics.value) return false
  return analytics.value.totalCheckIns > 0 || analytics.value.activeDays > 0
})

const fetchStudents = async () => {
  try {
    const response = await getMyStudents()
    students.value = response
    
    // Auto-select first student if available
    if (students.value.length > 0 && !selectedStudentId.value) {
      selectedStudentId.value = students.value[0].id
      await fetchAnalytics()
    }
  } catch (error) {
    showError('Failed to load students')
    console.error('Fetch students error:', error)
  }
}

const fetchAnalytics = async () => {
  if (!selectedStudentId.value) return

  loading.value = true
  try {
    // Fetch analytics data
    const analyticsResponse = await getStudentAnalytics(selectedStudentId.value, timePeriod.value)
    analytics.value = analyticsResponse

    // Fetch check-in data for chart
    const checkInsResponse = await getStudentCheckIns(selectedStudentId.value)
    processCheckInData(checkInsResponse)

    // Fetch diet records for chart
    const endDate = new Date()
    const startDate = new Date()
    startDate.setDate(startDate.getDate() - timePeriod.value)
    
    const dietResponse = await getStudentDietRecords(selectedStudentId.value, {
      startDate: startDate.toISOString().split('T')[0],
      endDate: endDate.toISOString().split('T')[0]
    })
    processDietData(dietResponse)
  } catch (error) {
    showError('Failed to load analytics')
    console.error('Fetch analytics error:', error)
    analytics.value = null
  } finally {
    loading.value = false
  }
}

const processCheckInData = (checkIns) => {
  // Group check-ins by date and count
  const dateMap = new Map()
  
  checkIns.forEach(checkIn => {
    const date = new Date(checkIn.checkDate).toLocaleDateString('en-US', { 
      month: 'short', 
      day: 'numeric' 
    })
    dateMap.set(date, (dateMap.get(date) || 0) + 1)
  })

  // Convert to array and sort by date
  chartData.checkIns = Array.from(dateMap.entries())
    .map(([date, count]) => ({ date, count }))
    .slice(-timePeriod.value) // Limit to time period
}

const processDietData = (dietRecords) => {
  // Group diet records by date and sum calories
  const dateMap = new Map()
  
  dietRecords.forEach(record => {
    const date = new Date(record.mealDate).toLocaleDateString('en-US', { 
      month: 'short', 
      day: 'numeric' 
    })
    dateMap.set(date, (dateMap.get(date) || 0) + (record.calories || 0))
  })

  // Convert to array
  chartData.calories = Array.from(dateMap.entries())
    .map(([date, calories]) => ({ date, calories }))
    .slice(-timePeriod.value) // Limit to time period
}

const getProgressColor = (percentage) => {
  if (percentage >= 80) return '#67C23A'
  if (percentage >= 50) return '#E6A23C'
  return '#F56C6C'
}

onMounted(() => {
  fetchStudents()
})
</script>

<style scoped>
.student-analytics {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  color: #303133;
}

.filter-card {
  margin-bottom: 20px;
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 80px;
  color: #409EFF;
}

.empty-state {
  padding: 60px;
}

.info-alert {
  margin-bottom: 20px;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  margin-bottom: 20px;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 15px;
}

.stat-icon {
  flex-shrink: 0;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  line-height: 1;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 13px;
  color: #909399;
}

.section-card {
  margin-bottom: 20px;
}

.completion-rate {
  padding: 20px;
}

.completion-text {
  text-align: center;
  margin-top: 15px;
  color: #606266;
  font-size: 14px;
}

.student-option {
  display: flex;
  align-items: center;
  gap: 10px;
}

@media (max-width: 768px) {
  .student-analytics {
    padding: 10px;
  }

  .stat-value {
    font-size: 24px;
  }
}
</style>
