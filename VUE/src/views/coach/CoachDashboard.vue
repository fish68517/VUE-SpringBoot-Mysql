<template>
  <Layout>
    <div class="coach-dashboard">
      <h2>仪表板</h2>
      
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
                <div class="stat-label">学生总数</div>
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
                <div class="stat-label">内容总数</div>
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
                <div class="stat-label">训练计划</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- Quick Actions -->
      <el-card class="section-card">
        <template #header>
          <span>快捷控制</span>
        </template>
        <div class="quick-actions">
          <el-button type="primary" @click="goToAddStudent">
            <el-icon><UserFilled /></el-icon>
            添加学员
          </el-button>
          <el-button type="success" @click="goToCreatePlan">
            <el-icon><Plus /></el-icon>
            创建训练计划
          </el-button>
          <el-button type="warning" @click="goToCreateContent">
            <el-icon><Edit /></el-icon>
            创建内容
          </el-button>
          <el-button type="danger" @click="goToStudentFeedback">
            <el-icon><Edit /></el-icon>
            学员反馈
          </el-button>
        </div>
      </el-card>

      <!-- Recent Students -->
      <el-card class="section-card">
        <template #header>
          <div class="card-header">
            <span>最近学员</span>
            <el-button text type="primary" @click="goToStudentList">查看所有</el-button>
          </div>
        </template>
        <div v-if="loading" class="loading-container">
          <el-icon class="is-loading"><Loading /></el-icon>
        </div>
        <div v-else-if="recentStudents.length === 0" class="empty-state">
          <el-empty description="No students yet">
            <el-button type="primary" @click="goToAddStudent">添加您的第一位学员</el-button>
          </el-empty>
        </div>
        <el-table v-else :data="recentStudents" style="width: 100%">
          <el-table-column label="Student" min-width="200">
            <template #default="{ row }">
              <div class="student-info">
                <el-avatar :src="row.avatar" :size="40">
                  {{ row.student.username.charAt(0).toUpperCase() }}
                </el-avatar>
                <span class="student-name">{{ row.student.username }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="student.intro" label="简介" min-width="250" show-overflow-tooltip />
          <el-table-column label="加入时间" width="150">
            <template #default="{ row }">
              {{ formatDate(row.createdAt) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" v-if="false">
            <template #default="{ row }">
              <el-button size="small" @click="viewStudentDetails(row.id)">查看详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <!-- Recent Training Plans -->
      <el-card class="section-card">
        <template #header>
          <div class="card-header">
            <span>最近训练计划</span>
            <el-button text type="primary" @click="goToCreatePlan">创建新计划</el-button>
          </div>
        </template>
        <div v-if="loadingPlans" class="loading-container">
          <el-icon class="is-loading"><Loading /></el-icon>
        </div>
        <div v-else-if="recentPlans.length === 0" class="empty-state">
          <el-empty description="No training plans yet">
            <el-button type="primary" @click="goToCreatePlan">创建新计划</el-button>
          </el-empty>
        </div>
        <el-table v-else :data="recentPlans" style="width: 100%">
          <el-table-column prop="name" label="计划名称" min-width="200" />
          <el-table-column label="学员" width="150">
            <template #default="{ row }">
              {{ row.studentName }}
            </template>
          </el-table-column>
          <el-table-column label="持续时间" width="200">
            <template #default="{ row }">
              {{ formatDate(row.startDate) }} - {{ formatDate(row.endDate) }}
            </template>
          </el-table-column>
          <el-table-column label="状态" width="120">
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
import { getUserInfo } from '@/utils/auth'

const router = useRouter()

const goToStudentFeedback = () => {
  // 跳转到新页面
  router.push({ name: 'CoachStudentFeedback' })
}

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
    const userId = getUserInfo().userId
    const contentResponse = await getResources({ creatorId: userId })
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

const goToCreateCertificate = () => {
  router.push('/coach/certification')
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
/* 1. 确保整个应用占满屏幕，无滚动条 */
.app-wrapper {
  display: flex;
  width: 100%;
  height: 100vh;
  overflow: hidden;
  background-color: #f5f7fa;
}

/* 2. 左侧侧边栏样式 */
.sidebar-container {
  background-color: #304156;
  height: 100%;
  flex-shrink: 0; /* 禁止侧边栏被压缩 */
  transition: width 0.3s;
  overflow-x: hidden;
  overflow-y: auto;
  z-index: 1001;
}

/* 3. 右侧容器核心修复：flex: 1 让它自动占满剩余宽度 */
.main-container {
  width: 100%;
  display: flex;
  flex-direction: column;
  min-width: 0; /* 防止 flex 子元素内容过宽导致溢出 */
  height: 100%;
  position: relative;
}

/* 4. 头部样式 */
.fixed-header {
  height: 60px;
  background: #fff;
  border-bottom: 1px solid #dcdfe6;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);
  flex-shrink: 0; /* 防止头部被压缩 */
  z-index: 999;
}

/* 5. 内容区域样式 */
.app-main {
  flex: 1; /* 占据垂直方向剩余空间 */
  padding: 20px;
  overflow-y: auto; /* 内容过多时，只有这里滚动 */
  overflow-x: hidden;
  position: relative;
}

/* 移动端遮罩层 */
.sidebar-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1000;
}
</style>

