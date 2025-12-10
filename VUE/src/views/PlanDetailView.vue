<template>
  <div class="plan-detail-container">
    <!-- 加载中 -->
    <el-skeleton v-if="loading" :rows="12" animated style="padding: 40px" />

    <!-- 计划详情主体 -->
    <div v-else-if="plan" class="plan-detail-content">
      <!-- 顶部操作栏 -->
      <div class="detail-header">
        <el-button type="primary" text @click="handleBack" size="large">
          <el-icon><ArrowLeft /></el-icon>
          返回计划列表
        </el-button>

        <div class="header-actions">
          <el-button type="primary" @click="handleEditPlan" size="large">
            编辑计划
          </el-button>
          <el-button type="danger" @click="handleDeletePlan" size="large">
            删除计划
          </el-button>
        </div>
      </div>

      <!-- 计划基本信息卡片 -->
      <div class="plan-info-card">
        <div class="plan-header">
          <h1>{{ plan.title }}</h1>
          <el-tag type="success" size="large" effect="light">
            {{ plan.destination }}
          </el-tag>
        </div>

        <div class="plan-details-grid">
          <div class="detail-item">
            <span class="detail-label">旅行时间</span>
            <span class="detail-value">
              {{ formatDate(plan.startDate) }} 至 {{ formatDate(plan.endDate) }}
            </span>
          </div>

          <div class="detail-item">
            <span class="detail-label">共计天数</span>
            <span class="detail-value highlight">{{ calculateDays() }} 天</span>
          </div>

          <div class="detail-item">
            <span class="detail-label">预算金额</span>
            <span class="detail-value">
              {{ plan.budget ? `¥${plan.budget.toLocaleString()}` : '未设置' }}
            </span>
          </div>

          <div class="detail-item">
            <span class="detail-label">创建时间</span>
            <span class="detail-value">{{ formatDateTime(plan.createdAt) }}</span>
          </div>
        </div>

        <!-- 计划描述 -->
        <div v-if="plan.description" class="description-section">
          <h3>计划描述</h3>
          <p class="description-text">{{ plan.description }}</p>
        </div>
      </div>

      <!-- 行程安排区域 -->
      <div class="itinerary-section">
        <div class="section-header">
          <h2>详细行程安排</h2>
          <el-button type="primary" @click="handleAddItinerary" size="large">
            <el-icon><Plus /></el-icon>
            添加行程
          </el-button>
        </div>

        <!-- 行程编辑器组件（保持不变） -->
        <ItineraryEditor
          ref="itineraryEditorRef"
          :plan-id="planId"
          :plan-start-date="plan.startDate"
          :plan-end-date="plan.endDate"
          @item-added="handleItemAdded"
          @item-updated="handleItemUpdated"
          @item-deleted="handleItemDeleted"
        />
      </div>
    </div>

    <!-- 未找到计划 -->
    <el-empty v-else description="未找到该旅行计划" style="padding: 80px 0">
      <el-button type="primary" size="large" @click="handleBack">
        返回计划列表
      </el-button>
    </el-empty>

    <!-- 错误提示 -->
    <el-alert
      v-if="errorMessage"
      :title="errorMessage"
      type="error"
      closable
      @close="errorMessage = ''"
      style="margin-top: 24px"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, Plus } from '@element-plus/icons-vue'
import { planService } from '../services/planService'
import { useUserStore } from '../stores/userStore'
import ItineraryEditor from '../components/ItineraryEditor.vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const plan = ref(null)
const loading = ref(true)
const errorMessage = ref('')
const itineraryEditorRef = ref(null)

const planId = computed(() => parseInt(route.params.id))

// 格式化日期（中文友好）
const formatDate = (date) => {
  if (!date) return '未设置'
  return new Date(date).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'short'
  }).replace('星期', '周')
}

const formatDateTime = (datetime) => {
  if (!datetime) return '未知'
  return new Date(datetime).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  }).replace(/\//g, '-')
}

const calculateDays = () => {
  if (!plan.value?.startDate || !plan.value?.endDate) return 0
  const start = new Date(plan.value.startDate)
  const end = new Date(plan.value.endDate)
  const days = Math.ceil((end - start) / (1000 * 60 * 60 * 24)) + 1
  return days
}

// 加载计划详情
const loadPlanDetails = async () => {
  try {
    loading.value = true
    errorMessage.value = ''

    if (!userStore.isAuthenticated) {
      errorMessage.value = '请先登录后再查看计划详情'
      return
    }

    const response = await planService.getTravelPlanById(planId.value)
    plan.value = response.data
  } catch (err) {
    errorMessage.value = err.message || '加载失败，请刷新重试'
    plan.value = null
  } finally {
    loading.value = false
  }
}

const handleBack = () => router.push('/plans')

const handleEditPlan = () => router.push(`/plans/${planId.value}/edit`)

const handleDeletePlan = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这个旅行计划吗？删除后无法恢复！',
      '警告',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await planService.deleteTravelPlan(planId.value)
    ElMessage.success('计划已成功删除')
    router.push('/plans')
  } catch (err) {
    if (err !== 'cancel') {
      ElMessage.error(err.message || '删除失败')
    }
  }
}

const handleAddItinerary = () => {
  itineraryEditorRef.value?.showAddForm()
}

const handleItemAdded = () => ElMessage.success('行程添加成功')
const handleItemUpdated = () => ElMessage.success('行程更新成功')
const handleItemDeleted = () => ElMessage.success('行程已删除')

onMounted(() => {
  loadPlanDetails()
})
</script>

<style scoped>
.plan-detail-container {
  max-width: 1100px;
  margin: 0 auto;
  padding: 30px 20px;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
  flex-wrap: wrap;
  gap: 16px;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.plan-info-card {
  background: #ffffff;
  border-radius: 16px;
  padding: 36px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.08);
  margin-bottom: 32px;
}

.plan-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 20px;
  margin-bottom: 32px;
  padding-bottom: 24px;
  border-bottom: 2px dashed #ebeef5;
}

.plan-header h1 {
  margin: 0;
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  line-height: 1.3;
}

.plan-details-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 24px;
  margin: 24px 0;
}

.detail-item {
  background: #f8f9fb;
  padding: 16px;
  border-radius: 12px;
  border-left: 4px solid #409eff;
}

.detail-label {
  display: block;
  font-size: 13px;
  color: #909399;
  margin-bottom: 8px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.detail-value {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.detail-value.highlight {
  color: #409eff;
  font-size: 22px;
}

.description-section {
  margin-top: 28px;
  padding-top: 28px;
  border-top: 1px solid #ebeef5;
}

.description-section h3 {
  margin: 0 0 12px 0;
  color: #303133;
  font-size: 18px;
}

.description-text {
  color: #606266;
  line-height: 1.8;
  font-size: 15px;
  background: #f9f9f9;
  padding: 16px;
  border-radius: 8px;
}

.itinerary-section {
  background: #ffffff;
  border-radius: 16px;
  padding: 36px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.08);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 28px;
  padding-bottom: 16px;
  border-bottom: 1px solid #ebeef5;
}

.section-header h2 {
  margin: 0;
  font-size: 22px;
  color: #303133;
}
</style>