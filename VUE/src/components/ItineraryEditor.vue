<template>
  <div class="itinerary-editor">
    <!-- 添加/编辑表单 -->
    <el-card v-if="showForm" class="form-card">
      <template #header>
        <div class="card-header">
          <span>{{ isEditing ? '编辑行程安排' : '新增行程安排' }}</span>
          <el-button text @click="handleCancel">
            <el-icon><Close /></el-icon>
          </el-button>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-position="top"
        class="itinerary-form"
        @submit.prevent="handleSubmit"
      >
        <el-form-item label="日期" prop="itemDate">
          <div class="form-field-wrapper">
            <el-date-picker
              v-model="formData.itemDate"
              type="date"
              placeholder="请选择日期"
              :disabled-date="disabledDate"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              style="width: 100%"
              @blur="validateField('itemDate')"
            />
            <div v-if="hasFieldError('itemDate')" class="field-error">
              <el-icon><CircleCloseFilled /></el-icon>
              <span>{{ getFieldError('itemDate') }}</span>
            </div>
          </div>
        </el-form-item>

        <el-form-item label="类型" prop="itemType">
          <div class="form-field-wrapper">
            <el-select
              v-model="formData.itemType"
              placeholder="请选择类型"
              style="width: 100%"
              @blur="validateField('itemType')"
            >
              <el-option label="景点" value="Attraction" />
              <el-option label="住宿" value="Accommodation" />
              <el-option label="交通" value="Transportation" />
              <el-option label="餐饮" value="Dining" />
            </el-select>
            <div v-if="hasFieldError('itemType')" class="field-error">
              <el-icon><CircleCloseFilled /></el-icon>
              <span>{{ getFieldError('itemType') }}</span>
            </div>
          </div>
        </el-form-item>

        <el-form-item label="标题" prop="title">
          <div class="form-field-wrapper">
            <el-input
              v-model="formData.title"
              placeholder="请输入行程标题"
              maxlength="200"
              show-word-limit
              style="width: 100%"
              @blur="validateField('title')"
            />
            <div v-if="hasFieldError('title')" class="field-error">
              <el-icon><CircleCloseFilled /></el-icon>
              <span>{{ getFieldError('title') }}</span>
            </div>
          </div>
        </el-form-item>

        <el-form-item label="详细安排" prop="description">
          <div class="form-field-wrapper">
            <el-input
              v-model="formData.description"
              type="textarea"
              placeholder="请输入当天的详细安排、注意事项或路线说明"
              maxlength="1000"
              show-word-limit
              :rows="6"
              resize="vertical"
              style="width: 100%"
              @blur="validateField('description')"
            />
            <div v-if="hasFieldError('description')" class="field-error">
              <el-icon><CircleCloseFilled /></el-icon>
              <span>{{ getFieldError('description') }}</span>
            </div>
          </div>
        </el-form-item>

        <el-form-item label="地点" prop="location">
          <div class="form-field-wrapper">
            <el-input
              v-model="formData.location"
              placeholder="请输入地点"
              maxlength="200"
              show-word-limit
              style="width: 100%"
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
            {{ isEditing ? '保存修改' : '添加行程' }}
          </el-button>
          <el-button @click="handleCancel">取消</el-button>
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
            <el-tag :type="getItemTypeTag(item.itemType)">{{ formatItemType(item.itemType) }}</el-tag>
          </div>
          <p v-if="item.description" class="item-description">{{ item.description }}</p>
          <p v-if="item.location" class="item-location">
            <el-icon><Location /></el-icon>
            {{ item.location }}
          </p>
        </div>
        <div class="item-actions">
          <el-button type="primary" text size="small" @click="handleEdit(item)">
            编辑
          </el-button>
          <el-button type="danger" text size="small" @click="handleDelete(item.id)">
            删除
          </el-button>
        </div>
      </div>
    </div>

    <!-- Empty state -->
    <el-empty
      v-if="!showForm && items.length === 0"
      description="还没有详细行程安排"
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
    { required: true, message: '请选择日期', trigger: 'change' }
  ],
  itemType: [
    { required: true, message: '请选择类型', trigger: 'change' }
  ],
  title: [
    { required: true, message: '请输入标题', trigger: 'blur' },
    { min: 1, max: 200, message: '标题长度需在 1 到 200 个字符之间', trigger: 'blur' }
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
  if (!date) return '未设置'
  return new Date(date).toLocaleDateString('zh-CN', {
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
    Attraction: 'success',
    Accommodation: 'info',
    Transportation: 'warning',
    Dining: 'danger'
  }
  return typeMap[itemType] || 'info'
}

const formatItemType = (itemType) => {
  const typeLabelMap = {
    Attraction: '景点',
    Accommodation: '住宿',
    Transportation: '交通',
    Dining: '餐饮'
  }
  return typeLabelMap[itemType] || itemType || '未分类'
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
    ElMessage.error(error.message || '加载行程安排失败')
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
        ElMessage.success('行程安排更新成功')
        emit('item-updated', response.data)
      }
    } else {
      // Create new item
      const response = await itineraryService.createItineraryItem(
        props.planId,
        formData.value
      )
      if (response.data) {
        ElMessage.success('行程安排添加成功')
        emit('item-added', response.data)
      }
    }

    // Reload items
    await loadItems()
    showForm.value = false
    resetForm()
  } catch (error) {
    ElMessage.error(error.message || '保存行程安排失败')
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
      '确定要删除这条行程安排吗？',
      '提示',
      {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await itineraryService.deleteItineraryItem(props.planId, itemId)
    ElMessage.success('行程安排已删除')
    emit('item-deleted', itemId)

    // Reload items
    await loadItems()
  } catch (error) {
    if (error.message !== 'cancel') {
      ElMessage.error(error.message || '删除行程安排失败')
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

.itinerary-form {
  width: 100%;
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
  max-width: none;
  width: 100%;
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
  width: 100%;
  max-width: 960px;
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

.itinerary-form :deep(.el-form-item__content) {
  display: flex;
  flex-direction: column;
  align-items: stretch;
  width: 100%;
  margin-left: 0 !important;
}

.itinerary-form :deep(.el-input),
.itinerary-form :deep(.el-select),
.itinerary-form :deep(.el-date-editor),
.itinerary-form :deep(.el-textarea),
.itinerary-form :deep(.el-textarea__inner) {
  width: 100% !important;
}

.itinerary-form :deep(.el-textarea__inner) {
  min-height: 220px;
  line-height: 1.8;
}
</style>
