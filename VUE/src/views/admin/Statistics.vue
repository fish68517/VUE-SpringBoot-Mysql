<template>
  <div class="statistics">
    <h1>数据统计</h1>
    
    <!-- Statistics Cards -->
    <el-row :gutter="20" style="margin-bottom: 30px">
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
            <div class="stat-value">{{ userActivityStats.totalUsers }}</div>
            <div class="stat-label">总用户数</div>
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

    <!-- Tabs for detailed statistics -->
    <el-tabs>
      <!-- Submission Statistics Tab -->
      <el-tab-pane label="投稿统计">
        <div class="tab-content">
          <el-button type="primary" @click="loadSubmissionStats" :loading="submissionLoading" style="margin-bottom: 20px">刷新</el-button>
          
          <el-row :gutter="20" style="margin-bottom: 30px">
            <el-col :xs="24" :sm="12">
              <el-card>
                <template #header>
                  <div class="card-header">
                    <span>投稿状态分布</span>
                  </div>
                </template>
                <el-table :data="submissionStatusData" stripe>
                  <el-table-column prop="status" label="状态" width="120" />
                  <el-table-column prop="count" label="数量" width="100" />
                  <el-table-column prop="percentage" label="占比" />
                </el-table>
              </el-card>
            </el-col>
            <el-col :xs="24" :sm="12">
              <el-card>
                <template #header>
                  <div class="card-header">
                    <span>栏目投稿分布</span>
                  </div>
                </template>
                <el-table :data="submissionByCategoryData" stripe>
                  <el-table-column prop="category" label="栏目" />
                  <el-table-column prop="count" label="投稿数" width="100" />
                </el-table>
              </el-card>
            </el-col>
          </el-row>
        </div>
      </el-tab-pane>

      <!-- Approval Rate Statistics Tab -->
      <el-tab-pane label="通过率统计">
        <div class="tab-content">
          <el-button type="primary" @click="loadApprovalRateStats" :loading="approvalRateLoading" style="margin-bottom: 20px">刷新</el-button>
          
          <el-row :gutter="20">
            <el-col :xs="24" :sm="12">
              <el-card>
                <template #header>
                  <div class="card-header">
                    <span>总体通过率</span>
                  </div>
                </template>
                <div class="approval-rate-summary">
                  <div class="rate-item">
                    <span class="label">已审核稿件：</span>
                    <span class="value">{{ approvalRateStats.totalReviewed }}</span>
                  </div>
                  <div class="rate-item">
                    <span class="label">已录用：</span>
                    <span class="value accepted">{{ approvalRateStats.acceptedCount }}</span>
                  </div>
                  <div class="rate-item">
                    <span class="label">已驳回：</span>
                    <span class="value rejected">{{ approvalRateStats.rejectedCount }}</span>
                  </div>
                  <div class="rate-item highlight">
                    <span class="label">通过率：</span>
                    <span class="value">{{ approvalRateStats.approvalRate }}%</span>
                  </div>
                </div>
              </el-card>
            </el-col>
            <el-col :xs="24" :sm="12">
              <el-card>
                <template #header>
                  <div class="card-header">
                    <span>栏目通过率</span>
                  </div>
                </template>
                <el-table :data="approvalRateByCategoryData" stripe>
                  <el-table-column prop="category" label="栏目" />
                  <el-table-column prop="rate" label="通过率" width="100" />
                </el-table>
              </el-card>
            </el-col>
          </el-row>
        </div>
      </el-tab-pane>

      <!-- User Activity Statistics Tab -->
      <el-tab-pane label="用户活跃度">
        <div class="tab-content">
          <el-button type="primary" @click="loadUserActivityStats" :loading="userActivityLoading" style="margin-bottom: 20px">刷新</el-button>
          
          <el-row :gutter="20">
            <el-col :xs="24" :sm="12">
              <el-card>
                <template #header>
                  <div class="card-header">
                    <span>用户角色分布</span>
                  </div>
                </template>
                <div class="user-role-summary">
                  <div class="role-item">
                    <span class="label">作者：</span>
                    <span class="value">{{ userActivityStats.authorCount }}</span>
                  </div>
                  <div class="role-item">
                    <span class="label">编辑：</span>
                    <span class="value">{{ userActivityStats.editorCount }}</span>
                  </div>
                  <div class="role-item">
                    <span class="label">审稿人：</span>
                    <span class="value">{{ userActivityStats.reviewerCount }}</span>
                  </div>
                  <div class="role-item">
                    <span class="label">管理员：</span>
                    <span class="value">{{ userActivityStats.adminCount }}</span>
                  </div>
                </div>
              </el-card>
            </el-col>
            <el-col :xs="24" :sm="12">
              <el-card>
                <template #header>
                  <div class="card-header">
                    <span>用户状态统计</span>
                  </div>
                </template>
                <div class="user-status-summary">
                  <div class="status-item">
                    <span class="label">活跃用户：</span>
                    <span class="value active">{{ userActivityStats.activeUsers }}</span>
                  </div>
                  <div class="status-item">
                    <span class="label">待审核用户：</span>
                    <span class="value pending">{{ userActivityStats.pendingUsers }}</span>
                  </div>
                  <div class="status-item">
                    <span class="label">审稿人接受率：</span>
                    <span class="value">{{ userActivityStats.reviewerAcceptanceRate }}%</span>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>
      </el-tab-pane>

      <!-- Comprehensive Report Tab -->
      <el-tab-pane label="综合报表">
        <div class="tab-content">
          <el-button type="primary" @click="generateReport" :loading="reportLoading" style="margin-bottom: 20px">生成报表</el-button>
          <el-button @click="downloadReport" :disabled="!reportGenerated" style="margin-bottom: 20px">下载报表</el-button>
          
          <el-card v-if="reportGenerated">
            <template #header>
              <div class="card-header">
                <span>报表信息</span>
              </div>
            </template>
            <div class="report-info">
              <div class="info-item">
                <span class="label">生成时间：</span>
                <span class="value">{{ formatDate(comprehensiveReport.generatedAt) }}</span>
              </div>
              <div class="info-item">
                <span class="label">总投稿数：</span>
                <span class="value">{{ comprehensiveReport.submissionStats?.totalSubmissions || 0 }}</span>
              </div>
              <div class="info-item">
                <span class="label">总用户数：</span>
                <span class="value">{{ comprehensiveReport.userActivityStats?.totalUsers || 0 }}</span>
              </div>
              <div class="info-item">
                <span class="label">总体通过率：</span>
                <span class="value">{{ comprehensiveReport.approvalRateStats?.approvalRate || 0 }}%</span>
              </div>
            </div>
          </el-card>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { statisticsService } from '../../services/statisticsService'

const submissionLoading = ref(false)
const approvalRateLoading = ref(false)
const userActivityLoading = ref(false)
const reportLoading = ref(false)
const reportGenerated = ref(false)

const submissionStats = ref({
  totalSubmissions: 0,
  submitted: 0,
  underReview: 0,
  revisionRequired: 0,
  accepted: 0,
  rejected: 0,
  submissionsByCategory: {}
})

const approvalRateStats = ref({
  totalReviewed: 0,
  acceptedCount: 0,
  rejectedCount: 0,
  approvalRate: 0,
  approvalRateByCategory: {}
})

const userActivityStats = ref({
  authorCount: 0,
  editorCount: 0,
  reviewerCount: 0,
  adminCount: 0,
  totalUsers: 0,
  activeUsers: 0,
  pendingUsers: 0,
  reviewerAcceptanceRate: 0
})

const comprehensiveReport = ref({
  generatedAt: null,
  submissionStats: null,
  approvalRateStats: null,
  userActivityStats: null
})

// Computed properties for table data
const submissionStatusData = computed(() => {
  const total = submissionStats.value.totalSubmissions || 1
  return [
    { status: '已提交', count: submissionStats.value.submitted, percentage: ((submissionStats.value.submitted / total) * 100).toFixed(2) + '%' },
    { status: '审稿中', count: submissionStats.value.underReview, percentage: ((submissionStats.value.underReview / total) * 100).toFixed(2) + '%' },
    { status: '需修改', count: submissionStats.value.revisionRequired, percentage: ((submissionStats.value.revisionRequired / total) * 100).toFixed(2) + '%' },
    { status: '已录用', count: submissionStats.value.accepted, percentage: ((submissionStats.value.accepted / total) * 100).toFixed(2) + '%' },
    { status: '已驳回', count: submissionStats.value.rejected, percentage: ((submissionStats.value.rejected / total) * 100).toFixed(2) + '%' }
  ]
})

const submissionByCategoryData = computed(() => {
  return Object.entries(submissionStats.value.submissionsByCategory || {}).map(([category, count]) => ({
    category,
    count
  }))
})

const approvalRateByCategoryData = computed(() => {
  return Object.entries(approvalRateStats.value.approvalRateByCategory || {}).map(([category, rate]) => ({
    category,
    rate: rate.toFixed(2) + '%'
  }))
})

// Load statistics on component mount
onMounted(async () => {
  await loadSubmissionStats()
  await loadApprovalRateStats()
  await loadUserActivityStats()
})

// Load submission statistics
const loadSubmissionStats = async () => {
  submissionLoading.value = true
  try {
    const response = await statisticsService.getSubmissionStats()
    if (response.data.code === 200) {
      submissionStats.value = response.data.data
    } else {
      ElMessage.error(response.data.message || '加载投稿统计失败')
    }
  } catch (error) {
    ElMessage.error('加载投稿统计失败')
    console.error(error)
  } finally {
    submissionLoading.value = false
  }
}

// Load approval rate statistics
const loadApprovalRateStats = async () => {
  approvalRateLoading.value = true
  try {
    const response = await statisticsService.getApprovalRateStats()
    if (response.data.code === 200) {
      approvalRateStats.value = response.data.data
    } else {
      ElMessage.error(response.data.message || '加载通过率统计失败')
    }
  } catch (error) {
    ElMessage.error('加载通过率统计失败')
    console.error(error)
  } finally {
    approvalRateLoading.value = false
  }
}

// Load user activity statistics
const loadUserActivityStats = async () => {
  userActivityLoading.value = true
  try {
    const response = await statisticsService.getUserActivityStats()
    if (response.data.code === 200) {
      userActivityStats.value = response.data.data
    } else {
      ElMessage.error(response.data.message || '加载用户活跃度统计失败')
    }
  } catch (error) {
    ElMessage.error('加载用户活跃度统计失败')
    console.error(error)
  } finally {
    userActivityLoading.value = false
  }
}

// Generate comprehensive report
const generateReport = async () => {
  reportLoading.value = true
  try {
    const response = await statisticsService.generateReport()
    if (response.data.code === 200) {
      comprehensiveReport.value = response.data.data
      reportGenerated.value = true
      ElMessage.success('报表生成成功')
    } else {
      ElMessage.error(response.data.message || '生成报表失败')
    }
  } catch (error) {
    ElMessage.error('生成报表失败')
    console.error(error)
  } finally {
    reportLoading.value = false
  }
}

// Download report
const downloadReport = () => {
  if (!reportGenerated.value) {
    ElMessage.warning('请先生成报表')
    return
  }
  
  // Create a JSON file and download it
  const dataStr = JSON.stringify(comprehensiveReport.value, null, 2)
  const dataBlob = new Blob([dataStr], { type: 'application/json' })
  const url = URL.createObjectURL(dataBlob)
  const link = document.createElement('a')
  link.href = url
  link.download = `statistics-report-${new Date().getTime()}.json`
  link.click()
  URL.revokeObjectURL(url)
  ElMessage.success('报表已下载')
}

// Format date
const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}
</script>

<style scoped>
.statistics {
  padding: 20px;
}

.statistics h1 {
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

.tab-content {
  padding: 20px 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.approval-rate-summary,
.user-role-summary,
.user-status-summary,
.report-info {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.rate-item,
.role-item,
.status-item,
.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  border-bottom: 1px solid #eee;
}

.rate-item:last-child,
.role-item:last-child,
.status-item:last-child,
.info-item:last-child {
  border-bottom: none;
}

.rate-item.highlight {
  background-color: #f0f9ff;
  border-radius: 4px;
  padding: 15px;
  font-weight: bold;
}

.label {
  color: #666;
  font-weight: 500;
}

.value {
  color: #333;
  font-weight: bold;
  font-size: 16px;
}

.value.accepted {
  color: #67c23a;
}

.value.rejected {
  color: #f56c6c;
}

.value.active {
  color: #409eff;
}

.value.pending {
  color: #e6a23c;
}
</style>
