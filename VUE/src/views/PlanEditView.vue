<template>
  <div class="plan-edit-container">
    <div class="plan-edit-card">
      <h2>Edit Travel Plan</h2>

      <!-- Loading state -->
      <el-skeleton v-if="loading" :rows="10" animated />

      <!-- Edit form -->
      <el-form
        v-else
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="140px"
        @submit.prevent="handleUpdatePlan"
      >
        <el-form-item label="Title" prop="title">
          <el-input
            v-model="form.title"
            placeholder="Enter plan title"
            clearable
            maxlength="200"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="Destination" prop="destination">
          <el-input
            v-model="form.destination"
            placeholder="Enter destination"
            clearable
            maxlength="200"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="Start Date" prop="startDate">
          <el-date-picker
            v-model="form.startDate"
            type="date"
            placeholder="Select start date"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="End Date" prop="endDate">
          <el-date-picker
            v-model="form.endDate"
            type="date"
            placeholder="Select end date"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="Budget" prop="budget">
          <el-input-number
            v-model="form.budget"
            placeholder="Enter budget amount"
            :min="0"
            :step="100"
            :precision="2"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="Description" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            placeholder="Enter a brief description"
            rows="3"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            @click="handleUpdatePlan"
            :loading="submitting"
            style="width: 100%"
          >
            Update Plan
          </el-button>
          <el-button
            @click="handleCancel"
            style="width: 100%; margin-top: 10px"
          >
            Cancel
          </el-button>
        </el-form-item>
      </el-form>

      <el-alert
        v-if="errorMessage"
        :title="errorMessage"
        type="error"
        closable
        @close="errorMessage = ''"
        style="margin-top: 20px"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { planService } from '../services/planService'
import { useUserStore } from '../stores/userStore'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)
const submitting = ref(false)
const errorMessage = ref('')

const planId = route.params.id

const form = reactive({
  title: '',
  destination: '',
  startDate: null,
  endDate: null,
  budget: null,
  description: ''
})

// Validation rules
const rules = {
  title: [
    { required: true, message: 'Title is required', trigger: 'blur' },
    { min: 1, max: 200, message: 'Title must be between 1 and 200 characters', trigger: 'blur' }
  ],
  destination: [
    { required: true, message: 'Destination is required', trigger: 'blur' },
    { min: 1, max: 200, message: 'Destination must be between 1 and 200 characters', trigger: 'blur' }
  ],
  startDate: [
    { required: true, message: 'Start date is required', trigger: 'change' }
  ],
  endDate: [
    { required: true, message: 'End date is required', trigger: 'change' },
    {
      validator: (rule, value, callback) => {
        if (form.startDate && value && value < form.startDate) {
          callback(new Error('End date must be after or equal to start date'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ],
  description: [
    { max: 500, message: 'Description must not exceed 500 characters', trigger: 'blur' }
  ]
}

/**
 * Load plan details from API
 */
const loadPlanDetails = async () => {
  try {
    loading.value = true
    errorMessage.value = ''

    // Check if user is authenticated
    if (!userStore.isAuthenticated) {
      errorMessage.value = 'You must be logged in to edit a plan'
      return
    }

    // Fetch plan details
    const response = await planService.getTravelPlanById(planId)

    if (response.data) {
      form.title = response.data.title
      form.destination = response.data.destination
      form.startDate = new Date(response.data.startDate)
      form.endDate = new Date(response.data.endDate)
      form.budget = response.data.budget
      form.description = response.data.description
    }
  } catch (error) {
    errorMessage.value = error.message || 'Failed to load travel plan'
  } finally {
    loading.value = false
  }
}

/**
 * Update the travel plan
 */
const handleUpdatePlan = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    
    // Check if user is authenticated
    if (!userStore.isAuthenticated) {
      errorMessage.value = 'You must be logged in to update a plan'
      return
    }

    submitting.value = true
    errorMessage.value = ''

    // Prepare data for API call
    const planData = {
      title: form.title,
      destination: form.destination,
      startDate: form.startDate,
      endDate: form.endDate,
      budget: form.budget,
      description: form.description
    }

    // Call API to update plan
    await planService.updateTravelPlan(planId, planData)

    ElMessage.success('Travel plan updated successfully!')
    
    // Redirect to plan detail page
    router.push(`/plans/${planId}`)
  } catch (error) {
    submitting.value = false
    
    if (error.message) {
      errorMessage.value = error.message
    } else if (error.errors && error.errors.length > 0) {
      errorMessage.value = error.errors[0].message
    } else {
      errorMessage.value = 'Failed to update travel plan. Please try again.'
    }
  }
}

/**
 * Cancel editing and go back
 */
const handleCancel = () => {
  router.back()
}

// Load plan details on component mount
onMounted(() => {
  loadPlanDetails()
})
</script>

<style scoped>
.plan-edit-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.plan-edit-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 30px;
}

.plan-edit-card h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}
</style>
