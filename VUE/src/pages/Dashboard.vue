<template>
  <div class="dashboard-layout">
    <Sidebar />

    <div class="main-content">
      <Header />

      <div class="content-area">
        <div class="dashboard-container">
          <div class="welcome-section">
            <h2>欢迎回来，{{ adminName }}</h2>
            <p>这是管理员系统仪表板</p>
          </div>

          <div class="stats-grid" v-loading="loading">
            <div class="stat-card">
              <div class="stat-icon users-icon">
                <el-icon><User /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-label">用户总数</div>
                <div class="stat-value">{{ stats.totalUsers }}</div>
              </div>
            </div>

            <div class="stat-card">
              <div class="stat-icon posts-icon">
                <el-icon><ChatDotSquare /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-label">社区帖子</div>
                <div class="stat-value">{{ stats.totalPosts }}</div>
              </div>
            </div>

            <div class="stat-card">
              <div class="stat-icon plans-icon">
                <el-icon><Basketball /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-label">健身计划</div>
                <div class="stat-value">{{ stats.totalPlans }}</div>
              </div>
            </div>

            <div class="stat-card">
              <div class="stat-icon data-icon">
                <el-icon><DataAnalysis /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-label">运动记录</div>
                <div class="stat-value">{{ stats.totalExerciseData }}</div>
              </div>
            </div>
          </div>

          <div class="quick-actions">
            <h3>快速操作</h3>
            <div class="action-buttons">
              <el-button type="primary" @click="navigateTo('/users')">
                <el-icon><User /></el-icon>
                <span>管理用户</span>
              </el-button>
              <el-button type="primary" @click="navigateTo('/community')">
                <el-icon><ChatDotSquare /></el-icon>
                <span>审核社区</span>
              </el-button>
              <el-button type="primary" @click="navigateTo('/fitness-plans')">
                <el-icon><Basketball /></el-icon>
                <span>管理计划</span>
              </el-button>
              <el-button type="primary" @click="navigateTo('/exercise-data')">
                <el-icon><DataAnalysis /></el-icon>
                <span>查看数据</span>
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAdminStore } from '../store/index.js'
import Sidebar from '../components/Sidebar.vue'
import Header from '../components/Header.vue'
import { getUserList } from '../api/user.js'
import { getPostList } from '../api/community.js'
import { getFitnessPlanList } from '../api/fitnessPlan.js'
import { getExerciseDataList } from '../api/exerciseData.js'
import { showError } from '../utils/notification.js'
import {
  User,
  ChatDotSquare,
  Basketball,
  DataAnalysis
} from '@element-plus/icons-vue'

const router = useRouter()
const adminStore = useAdminStore()
const loading = ref(false)

const stats = ref({
  totalUsers: 0,
  totalPosts: 0,
  totalPlans: 0,
  totalExerciseData: 0
})

const adminName = computed(() => {
  return adminStore.adminInfo?.username || '管理员'
})

const navigateTo = (path) => {
  router.push(path)
}

const safeTotal = (response) => {
  if (!response || response.code !== 200 || !response.data) {
    return 0
  }
  return Number(response.data.total || 0)
}

const loadDashboardStats = async () => {
  loading.value = true
  try {
    const [userRes, postRes, planRes, exerciseRes] = await Promise.all([
      getUserList(1, 1),
      getPostList(1, 1),
      getFitnessPlanList(1, 1),
      getExerciseDataList(1, 1)
    ])

    stats.value = {
      totalUsers: safeTotal(userRes),
      totalPosts: safeTotal(postRes),
      totalPlans: safeTotal(planRes),
      totalExerciseData: safeTotal(exerciseRes)
    }
  } catch (error) {
    console.error('Failed to load dashboard stats:', error)
    showError('加载看板数据失败，请检查后端服务', '加载失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadDashboardStats()
})
</script>

<style scoped>
.dashboard-layout {
  display: flex;
  height: 100vh;
  background: #f5f7fa;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.content-area {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.dashboard-container {
  max-width: 1200px;
  margin: 0 auto;
  animation: fadeIn 0.5s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.welcome-section {
  margin-bottom: 30px;
}

.welcome-section h2 {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin: 0 0 10px 0;
  letter-spacing: -0.5px;
}

.welcome-section p {
  font-size: 14px;
  color: #999;
  margin: 0;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 15px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  cursor: pointer;
  animation: slideUp 0.5s ease;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.stat-card:nth-child(1) {
  animation-delay: 0.1s;
}

.stat-card:nth-child(2) {
  animation-delay: 0.2s;
}

.stat-card:nth-child(3) {
  animation-delay: 0.3s;
}

.stat-card:nth-child(4) {
  animation-delay: 0.4s;
}

.stat-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
  transform: translateY(-4px);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  flex-shrink: 0;
}

.users-icon {
  background: #e6f7ff;
  color: #1890ff;
}

.posts-icon {
  background: #f6ffed;
  color: #52c41a;
}

.plans-icon {
  background: #fff7e6;
  color: #fa8c16;
}

.data-icon {
  background: #f9f0ff;
  color: #722ed1;
}

.stat-content {
  flex: 1;
}

.stat-label {
  font-size: 12px;
  color: #999;
  margin-bottom: 5px;
  font-weight: 500;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #333;
}

.quick-actions {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  animation: slideUp 0.5s ease 0.5s both;
}

.quick-actions h3 {
  font-size: 18px;
  font-weight: 700;
  color: #333;
  margin: 0 0 15px 0;
}

.action-buttons {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.action-buttons :deep(.el-button) {
  flex: 1;
  min-width: 150px;
  height: 40px;
  border-radius: 6px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.action-buttons :deep(.el-button:hover) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
}
</style>
