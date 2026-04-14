<template>
  <div class="my-training-plans">
    <div class="page-header">
      <el-button class="back-btn" text @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
        返回
      </el-button>
      <h1>我的训练计划</h1>
      <p class="subtitle">查看、追踪并反馈您的专属训练计划</p>
    </div>

    <el-tabs v-model="activeTab" class="custom-tabs">
      <el-tab-pane label="训练计划" name="plans">
        <div class="filter-section">
          <el-radio-group v-model="statusFilter" @change="handleFilterChange">
            <el-radio-button label="all">全部</el-radio-button>
            <el-radio-button label="active">进行中</el-radio-button>
            <el-radio-button label="completed">已完成</el-radio-button>
          </el-radio-group>
        </div>

        <div v-if="loading" class="loading-container">
          <el-skeleton :rows="3" animated />
        </div>
        <div v-else-if="filteredPlans.length > 0" class="plans-list">
          <PlanCard
            v-for="plan in filteredPlans"
            :key="plan.id"
            :plan="plan"
            @click="handlePlanClick(plan)"
          />
        </div>
        <div v-else class="empty-state">
          <el-empty :description="emptyStateMessage">
            <template #image>
              <el-icon :size="100" color="#909399"><Document /></el-icon>
            </template>
            <el-button type="primary" @click="handleContactCoach">寻找教练</el-button>
          </el-empty>
        </div>
      </el-tab-pane>

      <el-tab-pane label="训练反馈" name="feedback">
        <div v-if="loading" class="loading-container">
          <el-skeleton :rows="3" animated />
        </div>
        <div v-else-if="plans.length > 0" class="plans-list">
          <p class="feedback-prompt">请选择一个训练计划以查看或提交反馈。</p>
          <PlanCard
            v-for="plan in plans"
            :key="plan.id"
            :plan="plan"
            @click="handleFeedbackClick(plan)"
          />
        </div>
        <div v-else class="empty-state">
          <el-empty description="暂无任何训练计划，无法提交反馈。">
            <template #image>
              <el-icon :size="100" color="#909399"><Document /></el-icon>
            </template>
          </el-empty>
        </div>
      </el-tab-pane>
    </el-tabs>

    <el-dialog
      v-model="dialogVisible"
      :title="selectedPlan?.name"
      width="960px"
      :close-on-click-modal="false"
      class="training-plan-dialog"
    >
      <div v-if="selectedPlan" class="plan-detail">
        <div class="detail-section">
          <h3>计划信息</h3>
          <div class="info-grid">
            <div class="info-row">
              <span class="label">教练：</span>
              <span class="value">{{ selectedPlan.coachName }}</span>
            </div>
            <div class="info-row">
              <span class="label">状态：</span>
              <el-tag :type="getStatusType(selectedPlan.status)" size="small">
                {{ getStatusText(selectedPlan.status) }}
              </el-tag>
            </div>
            <div class="info-row">
              <span class="label">开始日期：</span>
              <span class="value">{{ selectedPlan.startDate }}</span>
            </div>
            <div class="info-row">
              <span class="label">结束日期：</span>
              <span class="value">{{ selectedPlan.endDate }}</span>
            </div>
          </div>
        </div>

        <div class="detail-section">
          <h3>计划详情</h3>
          <p class="description">{{ selectedPlan.description || '暂无描述' }}</p>
        </div>

        <div v-if="selectedPlan.videoUrl" class="detail-section">
          <h3>训练视频</h3>
          <div class="video-box">
            <div class="video-name">{{ selectedPlan.videoName || '训练视频' }}</div>
            <video :src="selectedPlan.videoUrl" controls class="plan-video-player" />
          </div>
        </div>

        <div class="detail-section">
          <h3>训练动作</h3>
          <ExerciseList :exercises-data="selectedPlan.exercises" />
        </div>
      </div>

      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <TrainingFeedbackDialog
      v-if="selectedPlanForFeedback"
      v-model:visible="feedbackDialogVisible"
      :plan="selectedPlanForFeedback"
    />
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft, Document } from '@element-plus/icons-vue'
import { getTrainingPlans } from '../../api/training'
import PlanCard from '../../components/training/PlanCard.vue'
import ExerciseList from '../../components/training/ExerciseList.vue'
import TrainingFeedbackDialog from '../../components/training/TrainingFeedbackDialog.vue'
import { showError } from '@/utils/feedback'

const router = useRouter()

const activeTab = ref('plans')
const loading = ref(false)
const plans = ref([])
const statusFilter = ref('all')
const dialogVisible = ref(false)
const selectedPlan = ref(null)
const feedbackDialogVisible = ref(false)
const selectedPlanForFeedback = ref(null)

const filteredPlans = computed(() => {
  if (statusFilter.value === 'all') {
    return plans.value
  }
  return plans.value.filter((plan) => plan.status === statusFilter.value)
})

const emptyStateMessage = computed(() => {
  if (statusFilter.value === 'all') {
    return '暂无训练计划，联系教练为您制定专属计划吧！'
  }

  const statusMap = {
    active: '进行中',
    completed: '已完成'
  }
  const statusText = statusMap[statusFilter.value] || statusFilter.value
  return `暂无${statusText}的训练计划。`
})

const goBack = () => {
  router.back()
}

const fetchPlans = async () => {
  loading.value = true
  try {
    const response = await getTrainingPlans()
    plans.value = response.content || response || []
  } catch (error) {
    console.error('获取训练计划失败:', error)
    showError('获取训练计划失败')
  } finally {
    loading.value = false
  }
}

const handleFilterChange = () => {
  // filteredPlans handles filtering reactively
}

const handlePlanClick = (plan) => {
  selectedPlan.value = plan
  dialogVisible.value = true
}

const handleFeedbackClick = (plan) => {
  selectedPlanForFeedback.value = plan
  feedbackDialogVisible.value = true
}

const handleContactCoach = () => {
  router.push('/home')
}

const getStatusType = (status) => {
  const statusMap = {
    active: 'success',
    completed: 'info',
    cancelled: 'danger'
  }
  return statusMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    active: '进行中',
    completed: '已完成',
    cancelled: '已取消'
  }
  return textMap[status] || status
}

onMounted(() => {
  fetchPlans()
})
</script>

<style scoped>
.my-training-plans {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  margin: 0 0 8px 0;
  font-size: 28px;
  font-weight: 600;
  color: #303133;
}

.subtitle {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.filter-section {
  margin-bottom: 24px;
  padding: 16px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.loading-container {
  padding: 24px;
}

.plans-list {
  display: grid;
  gap: 16px;
}

.feedback-prompt {
  margin: 0;
  color: #606266;
}

.empty-state {
  padding: 60px 24px;
  text-align: center;
}

.empty-state .el-button {
  margin-top: 16px;
}

.plan-detail {
  max-height: 72vh;
  overflow-y: auto;
}

.detail-section {
  margin-bottom: 24px;
}

.detail-section:last-child {
  margin-bottom: 0;
}

.detail-section h3 {
  margin: 0 0 16px 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  border-bottom: 2px solid #409eff;
  padding-bottom: 8px;
}

.info-grid {
  display: grid;
  gap: 12px;
}

.info-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.info-row .label {
  font-weight: 600;
  color: #606266;
  min-width: 100px;
}

.info-row .value {
  color: #303133;
}

.description {
  margin: 0;
  color: #606266;
  line-height: 1.6;
  white-space: pre-wrap;
}

.video-box {
  padding: 12px;
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  background-color: #f8fafc;
}

.video-name {
  margin-bottom: 12px;
  color: #303133;
  font-weight: 500;
  word-break: break-all;
}

.plan-video-player {
  width: 100%;
  max-height: 560px;
  border-radius: 8px;
  background-color: #000;
}

.training-plan-dialog :deep(.el-dialog__body) {
  padding-top: 12px;
}

@media (max-width: 768px) {
  .my-training-plans {
    padding: 16px;
  }

  .page-header h1 {
    font-size: 24px;
  }

  .filter-section {
    padding: 12px;
  }

  .plan-detail {
    max-height: 68vh;
  }

  .plan-video-player {
    max-height: 320px;
  }
}
</style>
