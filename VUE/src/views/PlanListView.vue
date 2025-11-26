<template>
  <div class="plan-list-container">
    <div class="plan-list-header">
      <h1>Travel Plans</h1>
      <el-button type="primary" @click="handleCreatePlan">
        <el-icon><Plus /></el-icon>
        Create New Plan
      </el-button>
    </div>

    <!-- Loading state -->
    <el-skeleton v-if="loading" :rows="5" animated />

    <!-- Empty state -->
    <el-empty
      v-else-if="plans.length === 0"
      description="No travel plans yet"
      style="margin-top: 40px"
    >
      <el-button type="primary" @click="handleCreatePlan">Create First Plan</el-button>
    </el-empty>

    <!-- Plans list -->
    <div v-else class="plans-grid">
      <div
        v-for="plan in plans"
        :key="plan.id"
        class="plan-card"
        @click="handleViewPlan(plan.id)"
      >
        <div class="plan-card-header">
          <h3>{{ plan.title }}</h3>
          <el-tag type="info">{{ plan.destination }}</el-tag>
        </div>

        <div class="plan-card-body">
          <div class="plan-info">
            <span class="info-label">Duration:</span>
            <span class="info-value">
              {{ formatDate(plan.startDate) }} - {{ formatDate(plan.endDate) }}
            </span>
          </div>

          <div class="plan-info">
            <span class="info-label">Budget:</span>
            <span class="info-value">
              {{ plan.budget ? `$${plan.budget.toFixed(2)}` : 'Not set' }}
            </span>
          </div>

          <div class="plan-info">
            <span class="info-label">Description:</span>
            <p class="description">{{ plan.description || 'No description' }}</p>
          </div>

          <div class="plan-info">
            <span class="info-label">Created:</span>
            <span class="info-value">{{ formatDateTime(plan.createdAt) }}</span>
          </div>
        </div>

        <div class="plan-card-footer">
          <el-button
            type="primary"
            text
            size="small"
            @click.stop="handleViewPlan(plan.id)"
          >
            View Details
          </el-button>
          <el-button
            type="danger"
            text
            size="small"
            @click.stop="handleDeletePlan(plan.id)"
          >
            Delete
          </el-button>
        </div>
      </div>
    </div>

    <!-- Pagination -->
    <div v-if="totalPages > 1" class="pagination-container">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="handlePageChange"
      />
    </div>

    <!-- Error message -->
    <el-alert
      v-if="errorMessage"
      :title="errorMessage"
      type="error"
      closable
      @close="errorMessage = ''"
      style="margin-top: 20px"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { planService } from '../services/planService'
import { useUserStore } from '../stores/userStore'

const router = useRouter()
const userStore = useUserStore()

const plans = ref([])
const loading = ref(false)
const errorMessage = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

/**
 * Format date to readable string
 */
const formatDate = (date) => {
  if (!date) return 'N/A'
  return new Date(date).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}

/**
 * Format datetime to readable string
 */
const formatDateTime = (datetime) => {
  if (!datetime) return 'N/A'
  return new Date(datetime).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

/**
 * Load travel plans from API
 */
const loadPlans = async () => {
  try {
    loading.value = true
    errorMessage.value = ''

    // Check if user is authenticated
    if (!userStore.isAuthenticated) {
      errorMessage.value = 'You must be logged in to view plans'
      return
    }

    // Fetch plans with pagination
    const response = await planService.getUserTravelPlans(currentPage.value - 1, pageSize.value)

    // Extract data from response
    if (response.data && response.data.content) {
      plans.value = response.data.content
      total.value = response.data.totalElements || 0
    } else {
      plans.value = []
      total.value = 0
    }
  } catch (error) {
    errorMessage.value = error.message || 'Failed to load travel plans'
    plans.value = []
  } finally {
    loading.value = false
  }
}

/**
 * Handle page change
 */
const handlePageChange = () => {
  loadPlans()
}

/**
 * Navigate to create plan page
 */
const handleCreatePlan = () => {
  router.push('/plans/create')
}

/**
 * Navigate to plan detail page
 */
const handleViewPlan = (planId) => {
  router.push(`/plans/${planId}`)
}

/**
 * Delete a travel plan
 */
const handleDeletePlan = async (planId) => {
  try {
    await ElMessageBox.confirm(
      'Are you sure you want to delete this plan? This action cannot be undone.',
      'Warning',
      {
        confirmButtonText: 'Delete',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }
    )

    // Call API to delete plan
    await planService.deleteTravelPlan(planId)

    ElMessage.success('Travel plan deleted successfully')

    // Reload plans
    loadPlans()
  } catch (error) {
    if (error.message !== 'cancel') {
      ElMessage.error(error.message || 'Failed to delete travel plan')
    }
  }
}

// Load plans on component mount
onMounted(() => {
  loadPlans()
})
</script>

<style scoped>
.plan-list-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.plan-list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.plan-list-header h1 {
  margin: 0;
  color: #333;
}

.plans-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.plan-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
}

.plan-card:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.plan-card-header {
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 10px;
}

.plan-card-header h3 {
  margin: 0;
  color: #333;
  font-size: 16px;
  flex: 1;
  word-break: break-word;
}

.plan-card-body {
  padding: 16px;
  flex: 1;
}

.plan-info {
  margin-bottom: 12px;
  display: flex;
  flex-direction: column;
}

.info-label {
  font-weight: 600;
  color: #666;
  font-size: 12px;
  text-transform: uppercase;
  margin-bottom: 4px;
}

.info-value {
  color: #333;
  font-size: 14px;
}

.description {
  margin: 0;
  color: #666;
  font-size: 13px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.plan-card-footer {
  padding: 12px 16px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}
</style>
