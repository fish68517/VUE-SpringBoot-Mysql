<template>
  <Layout>
    <div class="coach-dashboard">
      <h2>Coach Dashboard</h2>
      
      <!-- Statistics Cards -->
      <el-row :gutter="20" class="stats-row">
        <el-col :xs="24" :sm="8">
          <el-card class="stat-card">
            <div class="stat-content">
              <el-icon class="stat-icon" :size="40" color="#409EFF">
                <User />
              </el-icon>
              <div class="stat-info">
                <div class="stat-value">{{ stats.totalStudents }}</div>
                <div class="stat-label">Total Students</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="8">
          <el-card class="stat-card">
            <div class="stat-content">
              <el-icon class="stat-icon" :size="40" color="#67C23A">
                <Document />
              </el-icon>
              <div class="stat-info">
                <div class="stat-value">{{ stats.totalContent }}</div>
                <div class="stat-label">Total Content</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="8">
          <el-card class="stat-card">
            <div class="stat-content">
              <el-icon class="stat-icon" :size="40" color="#E6A23C">
                <Calendar />
              </el-icon>
              <div class="stat-info">
                <div class="stat-value">{{ stats.totalPlans }}</div>
                <div class="stat-label">Training Plans</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- Quick Actions -->
      <el-card class="section-card">
        <template #header>
          <span>Quick Actions</span>
        </template>
        <div class="quick-actions">
          <el-button type="primary" @click="goToAddStudent">
            <el-icon><UserFilled /></el-icon>
            Add Student
          </el-button>
          <el-button type="success" @click="goToCreatePlan">
            <el-icon><Plus /></el-icon>
            Create Training Plan
          </el-button>
          <el-button type="warning" @click="goToCreateContent">
            <el-icon><Edit /></el-icon>
            Create Content
          </el-button>
        </div>
      </el-card>

      <!-- Recent Students -->
      <el-card class="section-card">
        <template #header>
          <div class="card-header">
            <span>Recent Students</span>
            <el-button text type="primary" @click="goToStudentList">View All</el-button>
          </div>
        </template>
        <div v-if="loading" class="loading-container">
          <el-icon class="is-loading"><Loading /></el-icon>
        </div>
        <div v-else-if="recentStudents.length === 0" class="empty-state">
          <el-empty description="No students yet">
            <el-button type="primary" @click="goToAddStudent">Add Your First Student</el-button>
          </el-empty>
        </div>
        <el-table v-else :data="recentStudents" style="width: 100%">
          <el-table-column label="Student" min-width="200">
            <template #default="{ row }">
              <div class="student-info">
                <el-avatar :src="row.avatar" :size="40">
                  {{ row.username.charAt(0).toUpperCase() }}
                </el-avatar>
                <span class="student-name">{{ row.username }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="intro" label="Introduction" min-width="250" show-overflow-tooltip />
          <el-table-column label="Joined" width="150">
            <template #default="{ row }">
              {{ formatDate(row.createdAt) }}
            </template>
          </el-table-column>
          <el-table-column label="Actions" width="200">
            <template #default="{ row }">
              <el-button size="small" @click="viewStudentDetails(row.id)">View Details</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <!-- Recent Training Plans -->
      <el-card class="section-card">
        <template #header>
          <div class="card-header">
            <span>Recent Training Plans</span>
            <el-button text type="primary" @click="goToCreatePlan">Create New</el-button>
          </div>
        </template>
        <div v-if="loadingPlans" class="loading-container">
          <el-icon class="is-loading"><Loading /></el-icon>
        </div>
        <div v-else-if="recentPlans.length === 0" class="empty-state">
          <el-empty description="No training plans yet">
            <el-button type="primary" @click="goToCreatePlan">Create Your First Plan</el-button>
          </el-empty>
        </div>
        <el-table v-else :data="recentPlans" style="width: 100%">
          <el-table-column prop="name" label="Plan Name" min-width="200" />
          <el-table-column label="Student" width="150">
            <template #default="{ row }">
              {{ row.studentName }}
            </template>
          </el-table-column>
          <el-table-column label="Duration" width="200">
            <template #default="{ row }">
              {{ formatDate(row.startDate) }} - {{ formatDate(row.endDate) }}
            </template>
          </el-table-column>
          <el-table-column label="Status" width="120">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
  </Layout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { User, Document, Calendar, UserFilled, Plus, Edit, Loading } from '@element-plus/icons-vue'
import Layout from '@/components/common/Layout.vue'
import { getMyStudents } from '@/api/coach'
import { getTrainingPlans } from '@/api/training'
import { getResources } from '@/api/resource'
import { showError } from '@/utils/feedback'

const router = useRouter()

const stats = ref({
  totalStudents: 0,
  totalContent: 0,
  totalPlans: 0
})

const recentStudents = ref([])
const recentPlans = ref([])
const loading = ref(false)
const loadingPlans = ref(false)

const fetchDashboardData = async () => {
  loading.value = true
  loadingPlans.value = true
  
  try {
    // Fetch students
    const studentsResponse = await getMyStudents()
    recentStudents.value = studentsResponse.slice(0, 5) // Show only 5 most recent
    stats.value.totalStudents = studentsResponse.length

    // Fetch training plans
    const plansResponse = await getTrainingPlans()
    recentPlans.value = plansResponse.slice(0, 5) // Show only 5 most recent
    stats.value.totalPlans = plansResponse.length

    // Fetch content created by coach
    const contentResponse = await getResources({ creatorId: 'current' })
    stats.value.totalContent = contentResponse.total || contentResponse.length || 0
  } catch (error) {
    showError('Failed to load dashboard data')
    console.error('Dashboard error:', error)
  } finally {
    loading.value = false
    loadingPlans.value = false
  }
}

const formatDate = (dateString) => {
  if (!dateString) return 'N/A'
  const date = new Date(dateString)
  return date.toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' })
}

const getStatusType = (status) => {
  const statusMap = {
    active: 'success',
    completed: 'info',
    cancelled: 'danger'
  }
  return statusMap[status?.toLowerCase()] || 'info'
}

const goToAddStudent = () => {
  router.push('/coach/students')
}

const goToCreatePlan = () => {
  router.push('/coach/training-plans/create')
}

const goToCreateContent = () => {
  router.push('/coach/content')
}

const goToStudentList = () => {
  router.push('/coach/students')
}

const viewStudentDetails = (studentId) => {
  router.push(`/coach/students/${studentId}`)
}

onMounted(() => {
  fetchDashboardData()
})
</script>

<style scoped>
/* 核心修改：移除 max-width 和 margin: 0 auto */
.dashboard-container {
  /* padding: 20px;  <-- 如果 Layout.vue 的 el-main 已经有 padding，这里可以去掉或减少 */
  width: 100%;
}

.header-section {
  margin-bottom: 24px;
}

.dashboard-title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin: 0;
  text-align: left; /* 标题左对齐 */
}

/* 统计卡片样式优化 */
.stat-card {
  height: 100%;
  transition: all 0.3s;
}

.stat-content {
  display: flex;
  align-items: center;
  padding: 10px;
}

.stat-icon {
  padding: 10px;
  background-color: #f0f9eb;
  border-radius: 8px;
  margin-right: 16px;
}
/* 不同颜色的背景微调 */
.el-col:nth-child(1) .stat-icon { background-color: #ecf5ff; }
.el-col:nth-child(2) .stat-icon { background-color: #f0f9eb; }
.el-col:nth-child(3) .stat-icon { background-color: #fdf6ec; }

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}

/* 快捷操作按钮区域 */
.action-buttons {
  display: flex;
  gap: 15px;
  justify-content: flex-start; /* 按钮左对齐 */
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 学生列表样式 */
.student-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #EBEEF5;
}

.student-item:last-child {
  border-bottom: none;
}

.student-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #409EFF;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  margin-right: 12px;
}

.student-info {
  flex: 1;
}

.student-name {
  font-weight: 500;
  font-size: 14px;
  color: #303133;
}

.student-date {
  font-size: 12px;
  color: #909399;
}
</style>

