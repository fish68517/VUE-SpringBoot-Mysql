<template>
  <div class="dashboard">
    <h1>管理员首页</h1>
    <el-row :gutter="20">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-value">{{ userActivityStats.totalUsers }}</div>
            <div class="stat-label">总用户数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-value">{{ submissionStats.totalSubmissions }}</div>
            <div class="stat-label">总投稿数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-value">{{ approvalRateStats.approvalRate }}%</div>
            <div class="stat-label">通过率</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-value">{{ userActivityStats.activeUsers }}</div>
            <div class="stat-label">活跃用户</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :xs="24">
        <el-button 
          type="primary" 
          @click="navigateToStatistics"
          :loading="loading"
        >
          {{ loading ? '加载中...' : '查看详细统计' }}
        </el-button>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { statisticsService } from '../../services/statisticsService'
import { useLoading } from '../../composables/useLoading'

const router = useRouter()
const { loading, executeWithLoading } = useLoading()

const submissionStats = ref({
  totalSubmissions: 0
})

const approvalRateStats = ref({
  approvalRate: 0
})

const userActivityStats = ref({
  totalUsers: 0,
  activeUsers: 0
})

// Load statistics on component mount
onMounted(async () => {
  await loadStatistics()
})

// Load statistics
const loadStatistics = async () => {
  const result = await executeWithLoading(
    async () => {
      const [submissionRes, approvalRes, userActivityRes] = await Promise.all([
        statisticsService.getSubmissionStats(),
        statisticsService.getApprovalRateStats(),
        statisticsService.getUserActivityStats()
      ])

      if (submissionRes.data.code === 200) {
        submissionStats.value = submissionRes.data.data
      }
      if (approvalRes.data.code === 200) {
        approvalRateStats.value = approvalRes.data.data
      }
      if (userActivityRes.data.code === 200) {
        userActivityStats.value = userActivityRes.data.data
      }
    },
    {
      loadingMessage: '加载统计数据中...',
      successMessage: '',
      showSuccess: false,
      errorMessage: '加载统计数据失败'
    }
  )
}

// Navigate to statistics page
const navigateToStatistics = () => {
  router.push('/admin/statistics')
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
</style>
