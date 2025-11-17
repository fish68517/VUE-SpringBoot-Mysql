<template>
  <div class="diet-records-container">
    <el-card class="header-card">
      <div class="header-content">
        <h2>Diet Records</h2>
        <div class="header-actions">
          <el-date-picker
            v-model="selectedDate"
            type="date"
            placeholder="Select date"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            @change="handleDateChange"
            class="date-picker"
          />
          <el-button type="primary" @click="showAddDialog">
            <el-icon><Plus /></el-icon>
            Add Record
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- Daily Summary Card -->
    <el-card class="summary-card">
      <div class="summary-content">
        <div class="summary-icon">
          <el-icon><Food /></el-icon>
        </div>
        <div class="summary-info">
          <div class="summary-label">Total Calories Today</div>
          <div class="summary-value">{{ dailySummary.totalCalories || 0 }} kcal</div>
          <div class="summary-detail">{{ dailySummary.mealCount || 0 }} meals recorded</div>
        </div>
      </div>
    </el-card>

    <!-- Diet Records List -->
    <el-card class="records-card">
      <div v-if="loading" class="loading-container">
        <el-icon class="is-loading"><Loading /></el-icon>
        <span>Loading records...</span>
      </div>

      <div v-else-if="groupedRecords.length === 0" class="empty-state">
        <el-empty description="No diet records for this date">
          <el-button type="primary" @click="showAddDialog">Add Your First Record</el-button>
        </el-empty>
      </div>

      <div v-else class="records-list">
        <div
          v-for="group in groupedRecords"
          :key="group.mealType"
          class="meal-group"
        >
          <div class="meal-header">
            <el-icon class="meal-icon">
              <component :is="getMealIcon(group.mealType)" />
            </el-icon>
            <h3>{{ formatMealType(group.mealType) }}</h3>
            <span class="meal-calories">{{ group.totalCalories }} kcal</span>
          </div>

          <div class="meal-records">
            <div
              v-for="record in group.records"
              :key="record.id"
              class="record-item"
            >
              <div class="record-content">
                <div class="record-foods">{{ record.foodItems }}</div>
                <div class="record-meta">
                  <span class="record-calories">{{ record.calories }} kcal</span>
                  <span class="record-time">{{ formatTime(record.createdAt) }}</span>
                </div>
              </div>
              <div class="record-actions">
                <el-button
                  type="primary"
                  size="small"
                  text
                  @click="handleEdit(record)"
                >
                  <el-icon><Edit /></el-icon>
                </el-button>
                <el-button
                  type="danger"
                  size="small"
                  text
                  @click="handleDelete(record)"
                >
                  <el-icon><Delete /></el-icon>
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-card>

    <!-- Add/Edit Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEditing ? 'Edit Diet Record' : 'Add Diet Record'"
      width="500px"
      @close="resetForm"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="Meal Type" prop="mealType">
          <el-select v-model="formData.mealType" placeholder="Select meal type">
            <el-option label="Breakfast" value="breakfast" />
            <el-option label="Lunch" value="lunch" />
            <el-option label="Dinner" value="dinner" />
            <el-option label="Snack" value="snack" />
          </el-select>
        </el-form-item>

        <el-form-item label="Food Items" prop="foodItems">
          <el-input
            v-model="formData.foodItems"
            type="textarea"
            :rows="4"
            placeholder="Enter food items (e.g., Chicken breast, Brown rice, Broccoli)"
          />
        </el-form-item>

        <el-form-item label="Calories" prop="calories">
          <el-input-number
            v-model="formData.calories"
            :min="0"
            :max="10000"
            placeholder="Enter calories"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="Meal Date" prop="mealDate">
          <el-date-picker
            v-model="formData.mealDate"
            type="date"
            placeholder="Select date"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">Cancel</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          {{ isEditing ? 'Update' : 'Save' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import {
  Plus,
  Food,
  Loading,
  Edit,
  Delete,
  Sunrise,
  Sunny,
  Moon,
  Coffee
} from '@element-plus/icons-vue'
import {
  getDietRecords,
  createDietRecord,
  updateDietRecord,
  deleteDietRecord,
  getDailySummary
} from '@/api/diet'
import { showSuccess, showError, handleDelete, handleFormSubmit } from '@/utils/feedback'

const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const isEditing = ref(false)
const selectedDate = ref(new Date().toISOString().split('T')[0])
const records = ref([])
const dailySummary = ref({
  totalCalories: 0,
  mealCount: 0
})

const formRef = ref(null)
const formData = ref({
  id: null,
  mealType: '',
  foodItems: '',
  calories: null,
  mealDate: new Date().toISOString().split('T')[0]
})

const formRules = {
  mealType: [
    { required: true, message: 'Please select meal type', trigger: 'change' }
  ],
  foodItems: [
    { required: true, message: 'Please enter food items', trigger: 'blur' },
    { min: 2, max: 500, message: 'Length should be 2 to 500 characters', trigger: 'blur' }
  ],
  calories: [
    { required: true, message: 'Please enter calories', trigger: 'blur' },
    { type: 'number', min: 0, message: 'Calories must be positive', trigger: 'blur' }
  ],
  mealDate: [
    { required: true, message: 'Please select meal date', trigger: 'change' }
  ]
}

const groupedRecords = computed(() => {
  const groups = {}
  
  records.value.forEach(record => {
    const mealType = record.mealType
    if (!groups[mealType]) {
      groups[mealType] = {
        mealType,
        records: [],
        totalCalories: 0
      }
    }
    groups[mealType].records.push(record)
    groups[mealType].totalCalories += record.calories || 0
  })

  // Sort by meal type order
  const mealOrder = ['breakfast', 'lunch', 'dinner', 'snack']
  return Object.values(groups).sort((a, b) => {
    return mealOrder.indexOf(a.mealType) - mealOrder.indexOf(b.mealType)
  })
})

const getMealIcon = (mealType) => {
  const icons = {
    breakfast: Sunrise,
    lunch: Sunny,
    dinner: Moon,
    snack: Coffee
  }
  return icons[mealType] || Food
}

const formatMealType = (mealType) => {
  return mealType.charAt(0).toUpperCase() + mealType.slice(1)
}

const formatTime = (dateTime) => {
  if (!dateTime) return ''
  const date = new Date(dateTime)
  return date.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' })
}

const handleDateChange = () => {
  fetchRecords()
  fetchDailySummary()
}

const showAddDialog = () => {
  isEditing.value = false
  formData.value = {
    id: null,
    mealType: '',
    foodItems: '',
    calories: null,
    mealDate: selectedDate.value
  }
  dialogVisible.value = true
}

const handleEdit = (record) => {
  isEditing.value = true
  formData.value = {
    id: record.id,
    mealType: record.mealType,
    foodItems: record.foodItems,
    calories: record.calories,
    mealDate: record.mealDate
  }
  dialogVisible.value = true
}

const handleDelete = async (record) => {
  try {
    await handleDelete(
      async () => {
        await deleteDietRecord(record.id)
        await fetchRecords()
        await fetchDailySummary()
      },
      'this diet record',
      {
        successMessage: 'Diet record deleted successfully',
        errorMessage: 'Failed to delete diet record'
      }
    )
  } catch (error) {
    // Error already handled
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return

  submitting.value = true
  try {
    await handleFormSubmit(
      formRef.value,
      async () => {
        if (isEditing.value) {
          await updateDietRecord(formData.value.id, formData.value)
        } else {
          await createDietRecord(formData.value)
        }
        
        dialogVisible.value = false
        await fetchRecords()
        await fetchDailySummary()
      },
      {
        successMessage: isEditing.value ? 'Diet record updated successfully' : 'Diet record added successfully',
        errorMessage: 'Failed to save diet record',
        validationMessage: 'Please fill in all required fields'
      }
    )
  } catch (error) {
    // Error already handled
  } finally {
    submitting.value = false
  }
}

const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  formData.value = {
    id: null,
    mealType: '',
    foodItems: '',
    calories: null,
    mealDate: selectedDate.value
  }
}

const fetchRecords = async () => {
  loading.value = true
  try {
    const response = await getDietRecords({
      startDate: selectedDate.value,
      endDate: selectedDate.value,
      page: 0,
      size: 100
    })
    records.value = response.content || response || []
  } catch (error) {
    // Error already handled by request interceptor
    console.error('Failed to fetch diet records:', error)
    records.value = []
  } finally {
    loading.value = false
  }
}

const fetchDailySummary = async () => {
  try {
    const response = await getDailySummary(selectedDate.value)
    dailySummary.value = response
  } catch (error) {
    console.error('Failed to fetch daily summary:', error)
    dailySummary.value = { totalCalories: 0, mealCount: 0 }
  }
}

onMounted(async () => {
  await Promise.all([
    fetchRecords(),
    fetchDailySummary()
  ])
})
</script>

<style scoped>
.diet-records-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.header-card {
  margin-bottom: 20px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 15px;
}

.header-content h2 {
  margin: 0;
  font-size: 24px;
  color: #303133;
}

.header-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

.date-picker {
  width: 200px;
}

.summary-card {
  margin-bottom: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.summary-card :deep(.el-card__body) {
  padding: 30px;
}

.summary-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.summary-icon {
  width: 80px;
  height: 80px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40px;
}

.summary-info {
  flex: 1;
}

.summary-label {
  font-size: 14px;
  opacity: 0.9;
  margin-bottom: 5px;
}

.summary-value {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 5px;
}

.summary-detail {
  font-size: 14px;
  opacity: 0.8;
}

.records-card {
  min-height: 400px;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: #909399;
  gap: 10px;
}

.loading-container .el-icon {
  font-size: 40px;
}

.empty-state {
  padding: 40px 20px;
}

.records-list {
  display: flex;
  flex-direction: column;
  gap: 25px;
}

.meal-group {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  overflow: hidden;
}

.meal-header {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 15px 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #ffffff 100%);
  border-bottom: 1px solid #ebeef5;
}

.meal-icon {
  font-size: 24px;
  color: #409eff;
}

.meal-header h3 {
  margin: 0;
  font-size: 18px;
  color: #303133;
  flex: 1;
}

.meal-calories {
  font-size: 16px;
  font-weight: 600;
  color: #409eff;
}

.meal-records {
  padding: 10px;
}

.record-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  background: white;
  border-radius: 6px;
  margin-bottom: 10px;
  transition: all 0.3s ease;
}

.record-item:last-child {
  margin-bottom: 0;
}

.record-item:hover {
  background: #f5f7fa;
  transform: translateX(5px);
}

.record-content {
  flex: 1;
}

.record-foods {
  font-size: 15px;
  color: #303133;
  margin-bottom: 8px;
  line-height: 1.5;
}

.record-meta {
  display: flex;
  gap: 15px;
  font-size: 13px;
  color: #909399;
}

.record-calories {
  font-weight: 600;
  color: #409eff;
}

.record-actions {
  display: flex;
  gap: 5px;
}

@media (max-width: 768px) {
  .diet-records-container {
    padding: 10px;
  }

  .header-content {
    flex-direction: column;
    align-items: stretch;
  }

  .header-actions {
    flex-direction: column;
  }

  .date-picker {
    width: 100%;
  }

  .summary-content {
    flex-direction: column;
    text-align: center;
  }

  .summary-value {
    font-size: 28px;
  }

  .meal-header {
    padding: 12px 15px;
  }

  .meal-header h3 {
    font-size: 16px;
  }

  .record-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .record-actions {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>
