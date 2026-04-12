<template>
  <div class="plan-list-container">
    <div class="plan-list-header">
      <h1>我的旅行计划</h1>
      <el-button type="primary" size="large" @click="handleCreatePlan">
        <el-icon><Plus /></el-icon>
        新建旅行计划
      </el-button>
    </div>

    <!-- 加载中 -->
    <el-skeleton v-if="loading" :rows="6" animated style="padding: 40px" />

    <!-- 空状态（超级温馨） -->
    <el-empty
      v-else-if="plans.length === 0"
      description="你还没有创建任何旅行计划哦～"
      style="padding: 100px 0"
    >
      <template #image>
        <el-icon :size="80" color="#dcdfe6"><Calendar /></el-icon>
      </template>
      <el-button type="primary" size="large" @click="handleCreatePlan">
        去规划第一场旅行
      </el-button>
    </el-empty>

    <!-- 计划列表 -->
    <div v-else class="plans-grid">
      <div
        v-for="plan in plans"
        :key="plan.id"
        class="plan-card"
        @click="handleViewPlan(plan.id)"
      >
        <div class="plan-card-header">
          <h3 class="title">{{ plan.title }}</h3>
          <el-tag type="success" size="large" effect="light">
            {{ plan.destination }}
          </el-tag>
        </div>

        <div class="plan-card-body">
          <!-- 旅行时间 -->
          <div class="plan-info">
            <span class="info-label">旅行时间</span>
            <span class="info-value highlight">
              {{ formatDateRange(plan.startDate, plan.endDate) }}
            </span>
          </div>

          <!-- 预算 -->
          <div class="plan-info">
            <span class="info-label">预算金额</span>
            <span class="info-value money">
              {{ plan.budget ? `¥${plan.budget.toLocaleString()}` : '未设置预算' }}
            </span>
          </div>

          <!-- 计划描述 -->
          <div class="plan-info" v-if="plan.description">
            <span class="info-label">计划简介</span>
            <p class="description">{{ plan.description }}</p>
          </div>

          <!-- 创建时间 -->
          <div class="plan-info">
            <span class="info-label">创建时间</span>
            <span class="info-value subtle">{{ formatDateTime(plan.createdAt) }}</span>
          </div>
        </div>

        <div class="plan-card-footer">
          <el-button type="primary" text size="large" @click.stop="handleViewPlan(plan.id)">
            查看详情
          </el-button>
          <el-button type="danger" text size="large" @click.stop="handleDeletePlan(plan.id)">
            删除计划
          </el-button>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="total > 0" class="pagination-container">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[6, 12, 24]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        background
        @current-change="handlePageChange"
        @size-change="handlePageSizeChange"
      />
    </div>

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
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Calendar } from '@element-plus/icons-vue'
import { planService } from '../services/planService'
import { useUserStore } from '../stores/userStore'

const router = useRouter()
const userStore = useUserStore()

const plans = ref([])
const loading = ref(true)
const errorMessage = ref('')
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

onMounted(() => {
  loadPlans()
})

const loadPlans = async () => {
  try {
    loading.value = true
    errorMessage.value = ''

    if (!userStore.isAuthenticated) {
      errorMessage.value = '请先登录后查看旅行计划'
      return
    }

    const response = await planService.getUserTravelPlans(currentPage.value - 1, pageSize.value)

    if (response?.data?.content) {
      plans.value = response.data.content
      total.value = response.data.totalElements || 0
    } else {
      plans.value = []
      total.value = 0
    }
  } catch (err) {
    errorMessage.value = '加载旅行计划失败，请刷新重试'
    plans.value = []
  } finally {
    loading.value = false
  }
}

const handlePageChange = () => loadPlans()
const handlePageSizeChange = () => {
  currentPage.value = 1
  loadPlans()
}

const handleCreatePlan = () => router.push('/plans/create')
const handleViewPlan = (id) => router.push(`/plans/${id}`)

const handleDeletePlan = async (id) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这个旅行计划吗？删除后将无法恢复！',
      '警告',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await planService.deleteTravelPlan(id)
    ElMessage.success('旅行计划已删除')
    loadPlans()
  } catch (err) {
    if (err !== 'cancel') {
      ElMessage.error('删除失败，请重试')
    }
  }
}

// 中文友好日期格式
const formatDateRange = (start, end) => {
  if (!start || !end) return '日期待定'
  const s = new Date(start).toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' })
  const e = new Date(end).toLocaleDateString('zh-CN', { month: 'short', day: 'numeric', year: 'numeric' })
  return `${s} ~ ${e}`
}

const formatDateTime = (datetime) => {
  if (!datetime) return '未知时间'
  return new Date(datetime).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  }).replace(/\//g, '-')
}
</script>

<style scoped>
.plan-list-container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 30px 20px;
}

.plan-list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
  flex-wrap: wrap;
  gap: 16px;
}

.plan-list-header h1 {
  margin: 0;
  font-size: 28px;
  color: #303133;
  font-weight: 600;
}

.plans-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: 28px;
  margin-bottom: 40px;
}

.plan-card {
  background: #ffffff;
  border-radius: 18px;
  box-shadow: 0 6px 24px rgba(0,0,0,0.08);
  overflow: hidden;
  cursor: pointer;
  transition: all 0.4s ease;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.plan-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 16px 40px rgba(0,0,0,0.15);
}

.plan-card-header {
  padding: 24px 24px 20px;
  background: linear-gradient(120deg, #fdfbfb 0%, #ebedf0 100%);
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
}

.title {
  margin: 0;
  font-size: 19px;
  font-weight: 600;
  color: #303133;
  line-height: 1.4;
  flex: 1;
}

.plan-card-body {
  padding: 0 24px 20px;
  flex: 1;
}

.plan-info {
  margin: 16px 0;
}

.info-label {
  display: block;
  font-size: 13px;
  color: #909399;
  margin-bottom: 6px;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.info-value {
  font-size: 16px;
  color: #303133;
  font-weight: 500;
}

.info-value.highlight {
  color: #409eff;
  font-weight: 600;
  font-size: 18px;
}

.info-value.money {
  color: #67c23a;
  font-weight: 600;
}

.info-value.subtle {
  color: #909399;
  font-size: 14px;
}

.description {
  margin: 12px 0 0;
  color: #606266;
  line-height: 1.7;

  font-size: 14.5px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.plan-card-footer {
  padding: 18px 24px;
  border-top: 1px dashed #ebeef5;
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  background: #fafafa;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 50px;
}

/* 手机端适配 */
@media (max-width: 768px) {
  .plan-list-header {
    flex-direction: column;
    align-items: stretch;
  }
  .plans-grid {
    grid-template-columns: 1fr;
  }
}
</style>