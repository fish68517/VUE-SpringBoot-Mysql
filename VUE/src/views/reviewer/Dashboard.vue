<template>
  <div class="dashboard">
    <h1>审稿人首页</h1>
    <el-row :gutter="20">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-value">{{ stats.pending }}</div>
            <div class="stat-label">待审稿</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-value">{{ stats.accepted }}</div>
            <div class="stat-label">进行中</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-value">{{ stats.completed }}</div>
            <div class="stat-label">已完成</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-value">{{ stats.total }}</div>
            <div class="stat-label">总任务数</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Quick Actions -->
    <el-card class="quick-actions-card">
      <template #header>
        <div class="card-header">
          <span>快速操作</span>
        </div>
      </template>
      <el-space>
        <el-button type="primary" @click="goToTasks">查看审稿任务</el-button>
        <el-button @click="goToHistory">查看历史记录</el-button>
        <el-button @click="goToProfile">个人信息</el-button>
      </el-space>
    </el-card>

    <!-- Recent Tasks -->
    <el-card class="recent-card">
      <template #header>
        <div class="card-header">
          <span>最近任务</span>
          <el-button link type="primary" @click="goToTasks">查看全部</el-button>
        </div>
      </template>

      <el-table :data="recentTasks" stripe v-if="recentTasks.length > 0">
        <el-table-column prop="id" label="任务ID" width="100" />
        <el-table-column prop="manuscriptId" label="稿件ID" width="100" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="分配时间" width="150">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="goToTasks">
              查看
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-else description="暂无任务" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const router = useRouter()
const tasks = ref([])
const stats = ref({
  pending: 0,
  accepted: 0,
  completed: 0,
  total: 0
})

const statusMap = {
  'PENDING': '待审稿',
  'ACCEPTED': '进行中',
  'REJECTED': '已拒绝',
  'SUBMITTED': '已完成'
}

const statusTypeMap = {
  'PENDING': 'warning',
  'ACCEPTED': 'info',
  'REJECTED': 'danger',
  'SUBMITTED': 'success'
}

const recentTasks = ref([])

onMounted(() => {
  loadDashboardData()
})

const loadDashboardData = async () => {
  try {
    const response = await axios.get('/api/api/reviewers/tasks')
    if (response.data.code === 200) {
      tasks.value = response.data.data || []
      
      // Calculate statistics
      stats.value.total = tasks.value.length
      stats.value.pending = tasks.value.filter(t => t.status === 'PENDING').length
      stats.value.accepted = tasks.value.filter(t => t.status === 'ACCEPTED').length
      stats.value.completed = tasks.value.filter(t => t.status === 'SUBMITTED').length
      
      // Get recent tasks (last 5)
      recentTasks.value = tasks.value.slice(0, 5)
    }
  } catch (error) {
    ElMessage.error(error.message || '加载数据失败')
    console.error(error)
  }
}

const getStatusLabel = (status) => {
  return statusMap[status] || status
}

const getStatusType = (status) => {
  return statusTypeMap[status] || 'info'
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('zh-CN')
}

const goToTasks = () => {
  router.push('/reviewer/tasks')
}

const goToHistory = () => {
  router.push('/reviewer/history')
}

const goToProfile = () => {
  router.push('/reviewer/profile')
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
  cursor: pointer;
  transition: all 0.3s ease;
}

.stat-card:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
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

.quick-actions-card {
  margin: 20px 0;
}

.recent-card {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
