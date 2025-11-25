没问题！这是 “学员数据分析” (Student Analytics) 页面的完整汉化版本。

主要修改包括：

界面文本：Student Analytics -> 学员数据分析, Select Student -> 选择学员, Time Period -> 时间范围 等。

选项翻译：Last 7 Days -> 最近 7 天 等。

统计卡片：Total Check-ins -> 累计打卡, Avg Calories/Day -> 日均热量, Active Days -> 活跃天数 等。

图表标题：Activity Trends -> 活动趋势, Training Plan Completion Rate -> 训练计划完成率。

空状态与提示：Insufficient data... -> 当前时段数据不足... 等。

日期格式：图表数据的日期格式化为 zh-CN。

请复制以下代码覆盖：

code
Html
play_circle
download
content_copy
expand_less
<template>
  <Layout>
    <div class="student-analytics">
      <div class="page-header">
        <h2>学员数据分析</h2>
      </div>

      <!-- 筛选区域 (Filters) -->
      <el-card class="filter-card">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12">
            <el-form-item label="选择学员">
              <el-select
                v-model="selectedStudentId"
                placeholder="请选择学员"
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
            <el-form-item label="时间范围">
              <el-select
                v-model="timePeriod"
                placeholder="请选择时间范围"
                style="width: 100%"
                @change="fetchAnalytics"
              >
                <el-option label="最近 7 天" :value="7" />
                <el-option label="最近 30 天" :value="30" />
                <el-option label="最近 90 天" :value="90" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-card>

      <!-- 加载状态 (Loading State) -->
      <div v-if="loading" class="loading-container">
        <el-icon class="is-loading" :size="40"><Loading /></el-icon>
      </div>

      <!-- 未选择学员 (No Student Selected) -->
      <div v-else-if="!selectedStudentId" class="empty-state">
        <el-empty description="请选择一位学员以查看分析数据" />
      </div>

      <!-- 分析内容 (Analytics Content) -->
      <div v-else-if="analytics">
        <!-- 数据不足提示 (Insufficient Data Message) -->
        <el-alert
          v-if="!hasEnoughData"
          type="info"
          :closable="false"
          class="info-alert"
        >
          <template #title>
            所选时间段内数据不足，分析结果可能受限。
          </template>
        </el-alert>

        <!-- 统计卡片 (Statistics Cards) -->
        <el-row :gutter="20" class="stats-row">
          <el-col :xs="24" :sm="12" :md="6">
            <el-card class="stat-card">
              <div class="stat-content">
                <el-icon class="stat-icon" :size="35" color="#409EFF">
                  <Calendar />
                </el-icon>
                <div class="stat-info">
                  <div class="stat-value">{{ analytics.totalCheckIns || 0 }}</div>
                  <div class="stat-label">累计打卡次数</div>
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
                  <div class="stat-label">打卡频率</div>
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
                  <div class="stat-label">日均摄入热量 (kcal)</div>
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
                  <div class="stat-label">活跃天数</div>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <!-- 计划完成率 (Plan Completion Rate) -->
        <el-card class="section-card">
          <template #header>
            <span>训练计划完成率</span>
          </template>
          <div class="completion-rate">
            <el-progress
              :percentage="analytics.planCompletionRate || 0"
              :color="getProgressColor(analytics.planCompletionRate)"
              :stroke-width="20"
            />
            <p class="completion-text">
              已完成 {{ analytics.planCompletionRate || 0 }}% 的训练计划
            </p>
          </div>
        </el-card>

        <!-- 图表 (Charts) -->
        <el-card class="section-card">
          <template #header>
            <span>活动趋势</span>
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
    showError('加载学员列表失败')
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
    showError('加载分析数据失败')
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
    // 改为中文日期格式，如：11月25日
    const date = new Date(checkIn.checkDate).toLocaleDateString('zh-CN', { 
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
    // 改为中文日期格式
    const date = new Date(record.mealDate).toLocaleDateString('zh-CN', { 
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
/* 样式保持不变 */
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