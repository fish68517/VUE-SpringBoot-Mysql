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
// ⚠️ 极其重要：务必使用封装好的 api，不要用原生 axios，否则会报 Token 缺失和外键错误
import api from '@/services/api'

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
    // ⚠️ 修复了原来 /api/api/ 的双重路径错误，并使用带 Token 的 api 实例请求
    const response = await api.get('/api/reviewers/tasksAll')
    if (response.code === 200) {
      const allTasks = response.data || []
      tasks.value = allTasks
      
      // 1. 按照新规则计算统计数据
      stats.value.total = allTasks.length
      stats.value.pending = allTasks.filter(t => t.status === 'PENDING').length
      stats.value.accepted = allTasks.filter(t => t.status === 'ACCEPTED').length
      // 核心修改：既不是 PENDING 也不是 ACCEPTED 的，全部视为已完成
      stats.value.completed = allTasks.filter(t => t.status !== 'PENDING' && t.status !== 'ACCEPTED').length
      
      // 2. 筛选最近 7 天的任务
      const sevenDaysAgo = new Date()
      sevenDaysAgo.setDate(sevenDaysAgo.getDate() - 7)
      
      let recent7DaysTasks = allTasks.filter(t => {
        if (!t.createdAt) return false
        // 将任务的创建时间与 7 天前的时间进行对比
        return new Date(t.createdAt) >= sevenDaysAgo
      })
      
      // (可选) 按照时间倒序排列，让最新的任务在最前面
      recent7DaysTasks.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
      
      recentTasks.value = recent7DaysTasks
    }
  } catch (error) {
    ElMessage.error(error.message || '加载数据失败')
    console.error(error)
  }
}

// 动态获取状态文字（匹配新规则）
const getStatusLabel = (status) => {
  if (status === 'PENDING') return '待审稿'
  if (status === 'ACCEPTED') return '进行中'
  return '已完成' // 其他所有状态都显示为已完成
}

// 动态获取状态标签颜色（匹配新规则）
const getStatusType = (status) => {
  if (status === 'PENDING') return 'warning'  // 黄色
  if (status === 'ACCEPTED') return 'primary' // 蓝色
  return 'success' // 其他所有状态均为已完成（绿色）
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
