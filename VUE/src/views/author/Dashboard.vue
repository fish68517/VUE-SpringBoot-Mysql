<template>
  <div class="dashboard">
    <h1>作者首页</h1>
    <el-row :gutter="20">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-value">{{ stats.total }}</div>
            <div class="stat-label">已投稿</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-value">{{ stats.underReview }}</div>
            <div class="stat-label">审核中</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-value">{{ stats.accepted }}</div>
            <div class="stat-label">已录用</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-value">{{ stats.rejected }}</div>
            <div class="stat-label">已驳回</div>
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
        <el-button type="primary" @click="goToSubmit">提交新稿件</el-button>
        <el-button @click="goToManuscripts">查看我的稿件</el-button>
        <el-button @click="goToMessages">查看消息</el-button>
        <el-button @click="goToProfile">个人信息</el-button>
      </el-space>
    </el-card>

    <!-- Recent Manuscripts -->
    <el-card class="recent-card">
      <template #header>
        <div class="card-header">
          <span>最近投稿</span>
          <el-button link type="primary" @click="goToManuscripts">查看全部</el-button>
        </div>
      </template>

      <el-table :data="recentManuscripts" stripe v-if="recentManuscripts.length > 0">
        <el-table-column prop="id" label="稿件ID" width="100" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
         <el-table-column label="投稿日期" width="150">
          <template #default="{ row }">
            {{ formatDate(row.submissionDate) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="viewManuscript(row.id)">
              查看
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-else description="暂无稿件" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { manuscriptService } from '@/services/manuscriptService'

const router = useRouter()
const manuscripts = ref([])
const stats = ref({
  total: 0,
  underReview: 0,
  accepted: 0,
  rejected: 0
})

const statusMap = {
  'DRAFT': '草稿',
  'SUBMITTED': '已投稿',
  'UNDER_REVIEW': '审核中',
  'REVISION_REQUIRED': '需要修改',
  'ACCEPTED': '已录用',
  'REJECTED': '已驳回'
}

const statusTypeMap = {
  'DRAFT': 'info',
  'SUBMITTED': 'warning',
  'UNDER_REVIEW': 'warning',
  'REVISION_REQUIRED': 'warning',
  'ACCEPTED': 'success',
  'REJECTED': 'danger'
}

const recentManuscripts = ref([])

onMounted(() => {
  loadDashboardData()
})

const loadDashboardData = async () => {
  try {
    const response = await manuscriptService.getAuthorManuscripts()
    manuscripts.value = response.data || []
    
    // Calculate statistics
    stats.value.total = manuscripts.value.length
    stats.value.underReview = manuscripts.value.filter(m => m.status === 'UNDER_REVIEW').length
    stats.value.accepted = manuscripts.value.filter(m => m.status === 'ACCEPTED').length
    stats.value.rejected = manuscripts.value.filter(m => m.status === 'REJECTED').length
    
    // Get recent manuscripts (last 5)
    recentManuscripts.value = manuscripts.value.slice(0, 5)
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

const goToSubmit = () => {
  router.push('/author/submit-manuscript')
}

const goToManuscripts = () => {
  router.push('/author/manuscripts')
}

const goToMessages = () => {
  router.push('/author/messages')
}

const goToProfile = () => {
  router.push('/author/profile')
}

const viewManuscript = (id) => {
  router.push(`/author/manuscript-detail/${id}`)
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
