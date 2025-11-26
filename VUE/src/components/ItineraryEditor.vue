<template>
  <div class="itinerary-editor">
    <!-- Add/Edit form -->
    <el-card v-if="showForm" class="form-card">
      <template #header>
        <div class="card-header">
          <span>{{ isEditing ? 'Edit Itinerary Item' : 'Add Itinerary Item' }}</span>
          <el-button text @click="handleCancel">
            <el-icon><Close /></el-icon>
          </el-button>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="120px"
        @submit.prevent="handleSubmit"
      >
        <el-form-item label="Date" prop="itemDate">
          <div class="form-field-wrapper">
            <el-date-picker
              v-model="formData.itemDate"
              type="date"
              placeholder="Select date"
              :disabled-date="disabledDate"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              @blur="validateField('itemDate')"
            />
            <div v-if="hasFieldError('itemDate')" class="field-error">
              <el-icon><CircleCloseFilled /></el-icon>
              <span>{{ getFieldError('itemDate') }}</span>
            </div>
          </div>
        </el-form-item>

        <el-form-item label="Type" prop="itemType">
          <div class="form-field-wrapper">
            <el-select
              v-model="formData.itemType"
              placeholder="Select type"
              @blur="validateField('itemType')"
            >
              <el-option label="Attraction" value="Attraction" />
              <el-option label="Accommodation" value="Accommodation" />
              <el-option label="Transportation" value="Transportation" />
              <el-option label="Dining" value="Dining" />
            </el-select>
            <div v-if="hasFieldError('itemType')" class="field-error">
              <el-icon><CircleCloseFilled /></el-icon>
              <span>{{ getFieldError('itemType') }}</span>
            </div>
          </div>
        </el-form-item>

        <el-form-item label="Title" prop="title">
          <div class="form-field-wrapper">
            <el-input
              v-model="formData.title"
              placeholder="Enter title"
              maxlength="200"
              show-word-limit
              @blur="validateField('title')"
            />
            <div v-if="hasFieldError('title')" class="field-error">
              <el-icon><CircleCloseFilled /></el-icon>
              <span>{{ getFieldError('title') }}</span>
            </div>
          </div>
        </el-form-item>

        <el-form-item label="Description" prop="description">
          <div class="form-field-wrapper">
            <el-input
              v-model="formData.description"
              type="textarea"
              placeholder="Enter description"
              maxlength="1000"
              show-word-limit
              :rows="3"
              @blur="validateField('description')"
            />
            <div v-if="hasFieldError('description')" class="field-error">
              <el-icon><CircleCloseFilled /></el-icon>
              <span>{{ getFieldError('description') }}</span>
            </div>
          </div>
        </el-form-item>

        <el-form-item label="Location" prop="location">
          <div class="form-field-wrapper">
            <el-input
              v-model="formData.location"
              placeholder="Enter location"
              maxlength="200"
              show-word-limit
              @blur="validateField('location')"
            />
            <div v-if="hasFieldError('location')" class="field-error">
              <el-icon><CircleCloseFilled /></el-icon>
              <span>{{ getFieldError('location') }}</span>
            </div>
          </div>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            {{ isEditing ? 'Update' : 'Add' }}
          </el-button>
          <el-button @click="handleCancel">Cancel</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- Items list -->
    <div v-if="!showForm && items.length > 0" class="items-list">
      <div
        v-for="item in sortedItems"
        :key="item.id"
        class="item-card"
      >
        <div class="item-date">
          {{ formatDate(item.itemDate) }}
        </div>
        <div class="item-content">
          <div class="item-header">
            <h4>{{ item.title }}</h4>
            <el-tag :type="getItemTypeTag(item.itemType)">{{ item.itemType }}</el-tag>
          </div>
          <p v-if="item.description" class="item-description">{{ item.description }}</p>
          <p v-if="item.location" class="item-location">
            <el-icon><Location /></el-icon>
            {{ item.location }}
          </p>
        </div>
        <div class="item-actions">
          <el-button type="primary" text size="small" @click="handleEdit(item)">
            Edit
          </el-button>
          <el-button type="danger" text size="small" @click="handleDelete(item.id)">
            Delete
          </el-button>
        </div>
      </div>
    </div>

    <!-- Empty state -->
    <el-empty
      v-if="!showForm && items.length === 0"
      description="No itinerary items yet"
    />
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Close, Location, CircleCloseFilled } from '@element-plus/icons-vue'
import { itineraryService } from '../services/itineraryService'
import { useFormValidation } from '../composables/useFormValidation'

const props = defineProps({
  planId: {
    type: Number,
    required: true
  },
  planStartDate: {
    type: String,
    required: true
  },
  planEndDate: {
    type: String,
    required: true
  }
})

const emit = defineEmits(['item-added', 'item-updated', 'item-deleted'])

const formRef = ref(null)
const items = ref([])
const showForm = ref(false)
const isEditing = ref(false)
const submitting = ref(false)
const editingItemId = ref(null)

const formData = ref({
  itemDate: '',
  itemType: '',
  title: '',
  description: '',
  location: ''
})

const formRules = {
  itemDate: [
    { required: true, message: 'Date is required', trigger: 'change' }
  ],
  itemType: [
    { required: true, message: 'Type is required', trigger: 'change' }
  ],
  title: [
    { required: true, message: 'Title is required', trigger: 'blur' },
    { min: 1, max: 200, message: 'Title must be between 1 and 200 characters', trigger: 'blur' }
  ]
}

// Use form validation composable
const {
  validateField,
  getFieldError,
  hasFieldError
} = useFormValidation(formData, {
  itemDate: { required: true },
  itemType: { required: true },
  title: { required: true, minLength: 1, maxLength: 200 },
  description: { maxLength: 1000 },
  location: { maxLength: 200 }
})

const sortedItems = computed(() => {
  return [...items.value].sort((a, b) => {
    return new Date(a.itemDate) - new Date(b.itemDate)
  })
})

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
 * Get tag type for item type
 */
const getItemTypeTag = (itemType) => {
  const typeMap = {
    'Attraction': 'success',
    'Accommodation': 'info',
    'Transportation': 'warning',
    'Dining': 'danger'
  }
  return typeMap[itemType] || 'info'
}

/**
 * Disable dates outside plan range
 */
const disabledDate = (date) => {
  const planStart = new Date(props.planStartDate)
  const planEnd = new Date(props.planEndDate)
  return date < planStart || date > planEnd
}

/**
 * Load itinerary items from API
 */
const loadItems = async () => {
  try {
    const response = await itineraryService.getItineraryItems(props.planId)
    if (response.data) {
      items.value = response.data
    }
  } catch (error) {
    ElMessage.error(error.message || 'Failed to load itinerary items')
  }
}

/**
 * Show add form
 */
const showAddForm = () => {
  isEditing.value = false
  editingItemId.value = null
  resetForm()
  showForm.value = true
}

/**
 * Show edit form
 */
const handleEdit = (item) => {
  isEditing.value = true
  editingItemId.value = item.id
  formData.value = {
    itemDate: item.itemDate,
    itemType: item.itemType,
    title: item.title,
    description: item.description || '',
    location: item.location || ''
  }
  showForm.value = true
}

/**
 * Cancel form
 */
const handleCancel = () => {
  showForm.value = false
  resetForm()
}

/**
 * Reset form data
 */
const resetForm = () => {
  formData.value = {
    itemDate: '',
    itemType: '',
    title: '',
    description: '',
    location: ''
  }
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

/**
 * Submit form
 */
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()

    submitting.value = true

    if (isEditing.value) {
      // Update existing item
      const response = await itineraryService.updateItineraryItem(
        props.planId,
        editingItemId.value,
        formData.value
      )
      if (response.data) {
        ElMessage.success('Itinerary item updated successfully')
        emit('item-updated', response.data)
      }
    } else {
      // Create new item
      const response = await itineraryService.createItineraryItem(
        props.planId,
        formData.value
      )
      if (response.data) {
        ElMessage.success('Itinerary item added successfully')
        emit('item-added', response.data)
      }
    }

    // Reload items
    await loadItems()
    showForm.value = false
    resetForm()
  } catch (error) {
    ElMessage.error(error.message || 'Failed to save itinerary item')
  } finally {
    submitting.value = false
  }
}

/**
 * Delete itinerary item
 */
const handleDelete = async (itemId) => {
  try {
    await ElMessageBox.confirm(
      'Are you sure you want to delete this itinerary item?',
      'Warning',
      {
        confirmButtonText: 'Delete',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }
    )

    await itineraryService.deleteItineraryItem(props.planId, itemId)
    ElMessage.success('Itinerary item deleted successfully')
    emit('item-deleted', itemId)

    // Reload items
    await loadItems()
  } catch (error) {
    if (error.message !== 'cancel') {
      ElMessage.error(error.message || 'Failed to delete itinerary item')
    }
  }
}

// Expose methods for parent component
defineExpose({
  showAddForm,
  loadItems
})

// Load items on mount
watch(() => props.planId, () => {
  loadItems()
}, { immediate: true })
</script>

<style scoped>
.itinerary-editor {
  width: 100%;
}

.form-card {
  margin-bottom: 20px;
  background: #f9f9f9;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.items-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.item-card {
  display: flex;
  gap: 15px;
  padding: 15px;
  background: #f9f9f9;
  border-radius: 6px;
  border-left: 4px solid #409eff;
  transition: all 0.3s ease;
}

.item-card:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
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

:deep(.el-form) {
  max-width: 600px;
}

:deep(.el-form-item) {
  margin-bottom: 20px;
}

:deep(.el-input),
:deep(.el-select),
:deep(.el-date-picker) {
  width: 100%;
}

.form-field-wrapper {
  position: relative;
}

.field-error {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 6px;
  font-size: 12px;
  color: #f56c6c;
  line-height: 1;
}

.field-error :deep(.el-icon) {
  font-size: 14px;
  flex-shrink: 0;
}
</style>
