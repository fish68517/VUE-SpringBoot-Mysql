<template>
  <div class="plan-detail-container">
    <!-- Loading state -->
    <el-skeleton v-if="loading" :rows="10" animated />

    <!-- Plan detail content -->
    <div v-else-if="plan" class="plan-detail-content">
      <!-- Header with back button -->
      <div class="detail-header">
        <el-button type="primary" text @click="handleBack">
          <el-icon><ArrowLeft /></el-icon>
          Back to Plans
        </el-button>
        <div class="header-actions">
          <el-button type="primary" @click="handleEditPlan">Edit</el-button>
          <el-button type="danger" @click="handleDeletePlan">Delete</el-button>
        </div>
      </div>

      <!-- Plan information -->
      <div class="plan-info-card">
        <div class="plan-header">
          <h1>{{ plan.title }}</h1>
          <el-tag type="info" size="large">{{ plan.destination }}</el-tag>
        </div>

        <div class="plan-details-grid">
          <div class="detail-item">
            <span class="detail-label">Duration</span>
            <span class="detail-value">
              {{ formatDate(plan.startDate) }} - {{ formatDate(plan.endDate) }}
            </span>
          </div>

          <div class="detail-item">
            <span class="detail-label">Budget</span>
            <span class="detail-value">
              {{ plan.budget ? `$${plan.budget.toFixed(2)}` : 'Not set' }}
            </span>
          </div>

          <div class="detail-item">
            <span class="detail-label">Days</span>
            <span class="detail-value">{{ calculateDays() }} days</span>
          </div>

          <div class="detail-item">
            <span class="detail-label">Created</span>
            <span class="detail-value">{{ formatDateTime(plan.createdAt) }}</span>
          </div>
        </div>

        <div v-if="plan.description" class="description-section">
          <h3>Description</h3>
          <p>{{ plan.description }}</p>
        </div>
      </div>

      <!-- Itinerary items section -->
      <div class="itinerary-section">
        <div class="section-header">
          <h2>Itinerary</h2>
          <el-button type="primary" size="small" @click="handleAddItinerary">
            <el-icon><Plus /></el-icon>
            Add Item
          </el-button>
        </div>

        <!-- Itinerary Editor Component -->
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

    <!-- Not found state -->
    <el-empty
      v-else
      description="Travel plan not found"
      style="margin-top: 40px"
    >
      <el-button type="primary" @click="handleBack">Back to Plans</el-button>
    </el-empty>

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
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, Plus, Location } from '@element-plus/icons-vue'
import { planService } from '../services/planService'
import { useUserStore } from '../stores/userStore'
import ItineraryEditor from '../components/ItineraryEditor.vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const plan = ref(null)
const loading = ref(false)
const errorMessage = ref('')
const itineraryEditorRef = ref(null)

const planId = computed(() => parseInt(route.params.id))

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
 * Calculate number of days in the plan
 */
const calculateDays = () => {
  if (!plan.value || !plan.value.startDate || !plan.value.endDate) return 0
  const start = new Date(plan.value.startDate)
  const end = new Date(plan.value.endDate)
  return Math.ceil((end - start) / (1000 * 60 * 60 * 24)) + 1
}



/**
 * Load travel plan details from API
 */
const loadPlanDetails = async () => {
  try {
    loading.value = true
    errorMessage.value = ''

    // Check if user is authenticated
    if (!userStore.isAuthenticated) {
      errorMessage.value = 'You must be logged in to view plan details'
      return
    }

    // Fetch plan details
    const response = await planService.getTravelPlanById(planId.value)

    if (response.data) {
      plan.value = response.data
    }
  } catch (error) {
    errorMessage.value = error.message || 'Failed to load travel plan'
    plan.value = null
  } finally {
    loading.value = false
  }
}

/**
 * Navigate back to plans list
 */
const handleBack = () => {
  router.push('/plans')
}

/**
 * Navigate to edit plan page
 */
const handleEditPlan = () => {
  router.push(`/plans/${planId.value}/edit`)
}

/**
 * Delete the travel plan
 */
const handleDeletePlan = async () => {
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
    await planService.deleteTravelPlan(planId.value)

    ElMessage.success('Travel plan deleted successfully')

    // Navigate back to plans list
    router.push('/plans')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || 'Failed to delete travel plan')
    }
  }
}

/**
 * Handle add itinerary item
 */
const handleAddItinerary = () => {
  if (itineraryEditorRef.value) {
    itineraryEditorRef.value.showAddForm()
  }
}

/**
 * Handle item added
 */
const handleItemAdded = (item) => {
  // Item was added successfully
}

/**
 * Handle item updated
 */
const handleItemUpdated = (item) => {
  // Item was updated successfully
}

/**
 * Handle item deleted
 */
const handleItemDeleted = (itemId) => {
  // Item was deleted successfully
}

// Load plan details on component mount
onMounted(() => {
  loadPlanDetails()
})
</script>

<style scoped>
.plan-detail-container {
  padding: 20px;
  max-width: 1000px;
  margin: 0 auto;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.plan-detail-content {
  animation: fadeIn 0.3s ease-in;
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

.plan-info-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 30px;
  margin-bottom: 30px;
}

.plan-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 20px;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.plan-header h1 {
  margin: 0;
  color: #333;
  flex: 1;
}

.plan-details-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.detail-item {
  display: flex;
  flex-direction: column;
}

.detail-label {
  font-weight: 600;
  color: #666;
  font-size: 12px;
  text-transform: uppercase;
  margin-bottom: 8px;
}

.detail-value {
  color: #333;
  font-size: 16px;
}

.description-section {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

.description-section h3 {
  margin: 0 0 10px 0;
  color: #333;
}

.description-section p {
  margin: 0;
  color: #666;
  line-height: 1.6;
}

.itinerary-section {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 30px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.section-header h2 {
  margin: 0;
  color: #333;
}

.itinerary-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.itinerary-item {
  display: flex;
  gap: 15px;
  padding: 15px;
  background: #f9f9f9;
  border-radius: 6px;
  border-left: 4px solid #409eff;
}

.item-date {
  min-width: 100px;
  font-weight: 600;
  color: #409eff;
  font-size: 14px;
}

.item-content {
  flex: 1;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.item-header h4 {
  margin: 0;
  color: #333;
  font-size: 15px;
}

.item-description {
  margin: 8px 0;
  color: #666;
  font-size: 13px;
  line-height: 1.5;
}

.item-location {
  margin: 8px 0 0 0;
  color: #999;
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 5px;
}

.item-actions {
  display: flex;
  gap: 8px;
  align-items: center;
}
</style>
