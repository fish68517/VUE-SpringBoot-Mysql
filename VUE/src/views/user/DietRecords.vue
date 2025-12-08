
<template>
  <div class="diet-records-container">
    <el-card class="header-card">
      <div class="header-content">
        <h2>饮食记录</h2>
        <div class="header-actions">
          <el-date-picker
            v-model="selectedDate"
            type="date"
            placeholder="选择日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            @change="handleDateChange"
            class="date-picker"
          />
          <el-button type="primary" @click="showAddDialog">
            <el-icon><Plus /></el-icon>
            添加记录
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 每日摘要卡片 (Daily Summary Card) -->
    <el-card class="summary-card">
      <div class="summary-content">
        <div class="summary-icon">
          <el-icon><Food /></el-icon>
        </div>
        <div class="summary-info">
          <div class="summary-label">今日总摄入</div>
          <div class="summary-value">{{ dailySummary.totalCalories || 0 }} 千卡</div>
          <div class="summary-detail">已记录 {{ dailySummary.mealCount || 0 }} 次用餐</div>
        </div>
      </div>
    </el-card>

    <!-- 饮食记录列表 (Diet Records List) -->
    <el-card class="records-card">
      <div v-if="loading" class="loading-container">
        <el-icon class="is-loading"><Loading /></el-icon>
        <span>正在加载记录...</span>
      </div>

      <div v-else-if="groupedRecords.length === 0" class="empty-state">
        <el-empty description="该日期暂无饮食记录">
          <el-button type="primary" @click="showAddDialog">添加第一条记录</el-button>
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
            <!-- 使用 formatMealType 显示中文餐点名称 -->
            <h3>{{ formatMealType(group.mealType) }}</h3>
            <span class="meal-calories">{{ group.totalCalories }} 千卡</span>
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
                  <span class="record-calories">{{ record.calories }} 千卡</span>
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

    <!-- 添加/编辑 弹窗 (Add/Edit Dialog) -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEditing ? '编辑饮食记录' : '添加饮食记录'"
      width="500px"
      @close="resetForm"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="用餐类型" prop="mealType">
          <el-select v-model="formData.mealType" placeholder="请选择用餐类型">
            <el-option label="早餐" value="breakfast" />
            <el-option label="午餐" value="lunch" />
            <el-option label="晚餐" value="dinner" />
            <el-option label="加餐/零食" value="snack" />
          </el-select>
        </el-form-item>

        <el-form-item label="食物内容" prop="foodItems">
          <el-input
            v-model="formData.foodItems"
            type="textarea"
            :rows="4"
            placeholder="请输入食物名称 (例如：鸡胸肉、糙米饭、西兰花)"
          />
        </el-form-item>

        <el-form-item label="热量 (千卡)" prop="calories">
          <el-input-number
            v-model="formData.calories"
            :min="0"
            :max="10000"
            placeholder="请输入热量"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="用餐日期" prop="mealDate">
          <el-date-picker
            v-model="formData.mealDate"
            type="date"
            placeholder="选择日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          {{ isEditing ? '更新' : '保存' }}
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
import { showSuccess, showError, handleDelete as utilHandleDelete, handleFormSubmit } from '@/utils/feedback'

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
    { required: true, message: '请选择用餐类型', trigger: 'change' }
  ],
  foodItems: [
    { required: true, message: '请输入食物内容', trigger: 'blur' },
    { min: 2, max: 500, message: '长度限制在 2 到 500 个字符', trigger: 'blur' }
  ],
  calories: [
    { required: true, message: '请输入热量', trigger: 'blur' },
    { type: 'number', min: 0, message: '热量必须为正数', trigger: 'blur' }
  ],
  mealDate: [
    { required: true, message: '请选择用餐日期', trigger: 'change' }
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

// 汉化后的餐点名称格式化函数
const formatMealType = (mealType) => {
  const map = {
    breakfast: '早餐',
    lunch: '午餐',
    dinner: '晚餐',
    snack: '加餐/零食'
  }
  return map[mealType] || mealType
}

const formatTime = (dateTime) => {
  if (!dateTime) return ''
  const date = new Date(dateTime)
  return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
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

// 这里的 handleDelete 重命名为本地函数，避免与 import 冲突
const handleDelete = async (record) => {
  try {
    await utilHandleDelete(
      async () => {
        await deleteDietRecord(record.id)
        await fetchRecords()
        await fetchDailySummary()
      },
      '这条饮食记录', // 这里的文本会被传入 confirm 弹窗
      {
        successMessage: '删除记录成功',
        errorMessage: '删除记录失败'
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
        successMessage: isEditing.value ? '更新记录成功' : '添加记录成功',
        errorMessage: '保存记录失败',
        validationMessage: '请填写所有必填项'
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
/* 样式保持不变 */
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