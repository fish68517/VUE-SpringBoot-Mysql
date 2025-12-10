<template>
  <div class="plan-create-container">
    <div class="plan-create-card">
      <h2>新建旅行计划</h2>
      <p class="tip">请填写以下信息，创建属于你的旅行计划</p>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="140px"
        @submit.prevent="handleCreatePlan"
      >
        <!-- 计划标题 -->
        <el-form-item label="计划标题" prop="title">
          <div class="form-field-wrapper">
            <el-input
              v-model="form.title"
              placeholder="例如：2025北海道赏樱之旅"
              clearable
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

        <!-- 目的地 -->
        <el-form-item label="目的地" prop="destination">
          <div class="form-field-wrapper">
            <el-input
              v-model="form.destination"
              placeholder="例如：日本·北海道、泰国·曼谷"
              clearable
              maxlength="200"
              show-word-limit
              @blur="validateField('destination')"
            />
            <div v-if="hasFieldError('destination')" class="field-error">
              <el-icon><CircleCloseFilled /></el-icon>
              <span>{{ getFieldError('destination') }}</span>
            </div>
          </div>
        </el-form-item>

        <!-- 开始日期 -->
        <el-form-item label="开始日期" prop="startDate">
          <div class="form-field-wrapper">
            <el-date-picker
              v-model="form.startDate"
              type="date"
              placeholder="选择出发日期"
              style="width: 100%"
              :disabled-date="disabledStartDate"
              @blur="validateField('startDate')"
            />
            <div v-if="hasFieldError('startDate')" class="field-error">
              <el-icon><CircleCloseFilled /></el-icon>
              <span>{{ getFieldError('startDate') }}</span>
            </div>
          </div>
        </el-form-item>

        <!-- 结束日期 -->
        <el-form-item label="结束日期" prop="endDate">
          <div class="form-field-wrapper">
            <el-date-picker
              v-model="form.endDate"
              type="date"
              placeholder="选择返程日期"
              style="width: 100%"
              :disabled-date="disabledEndDate"
              @blur="validateField('endDate')"
            />
            <div v-if="hasFieldError('endDate')" class="field-error">
              <el-icon><CircleCloseFilled /></el-icon>
              <span>{{ getFieldError('endDate') }}</span>
            </div>
          </div>
        </el-form-item>

        <!-- 预算 -->
        <el-form-item label="预算金额" prop="budget">
          <div class="form-field-wrapper">
            <el-input-number
              v-model="form.budget"
              :min="0"
              :step="100"
              :precision="0"
              placeholder="请输入预计预算（元）"
              style="width: 100%"
              controls-position="right"
            />
            <div v-if="hasFieldError('budget')" class="field-error">
              <el-icon><CircleCloseFilled /></el-icon>
              <span>{{ getFieldError('budget') }}</span>
            </div>
          </div>
        </el-form-item>

        <!-- 计划描述 -->
        <el-form-item label="计划描述" prop="description">
          <div class="form-field-wrapper">
            <el-input
              v-model="form.description"
              type="textarea"
              :rows="4"
              placeholder="简单描述这次旅行的亮点、想去的地方、期待的事～（可选）"
              maxlength="500"
              show-word-limit
            />
          </div>
        </el-form-item>

        <!-- 按钮区域 -->
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            @click="handleCreatePlan"
            :loading="loading"
            style="width: 100%; height: 48px; font-size: 16px"
          >
            立即创建计划
          </el-button>

          <el-button
            size="large"
            @click="handleCancel"
            style="width: 100%; margin-top: 12px; margin-left: 0"
          >
            取消返回
          </el-button>
        </el-form-item>
      </el-form>

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
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { CircleCloseFilled } from '@element-plus/icons-vue'
import { planService } from '../services/planService'
import { useUserStore } from '../stores/userStore'
import { useFormValidation } from '../composables/useFormValidation'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)
const errorMessage = ref('')

const form = reactive({
  title: '',
  destination: '',
  startDate: null,
  endDate: null,
  budget: null,
  description: ''
})

// 表单校验规则（全中文提示）
const rules = {
  title: [
    { required: true, message: '请输入计划标题', trigger: 'blur' },
    { min: 2, max: 200, message: '标题长度为2-200个字符', trigger: 'blur' }
  ],
  destination: [
    { required: true, message: '请输入目的地', trigger: 'blur' },
    { min: 2, max: 200, message: '目的地长度为2-200个字符', trigger: 'blur' }
  ],
  startDate: [
    { required: true, message: '请选择开始日期', trigger: 'change' }
  ],
  endDate: [
    { required: true, message: '请选择结束日期', trigger: 'change' },
    {
      validator: (rule, value, callback) => {
        if (form.startDate && value && value < form.startDate) {
          callback(new Error('结束日期不能早于开始日期'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ]
}

// 日期禁用逻辑：开始日期不能选今天之前，结束日期不能早于开始日期
const disabledStartDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7 // 不能选昨天及以前
}
const disabledEndDate = (time) => {
  if (!form.startDate) return false
  return time.getTime() < new Date(form.startDate).getTime()
}

// 表单校验组合式函数
const { validateField, getFieldError, hasFieldError } = useFormValidation(form, {
  title: { required: true, label: '计划标题', minLength: 2, maxLength: 200 },
  destination: { required: true, label: '目的地', minLength: 2, maxLength: 200 },
  startDate: { required: true, label: '开始日期' },
  endDate: { required: true, label: '结束日期' },
})

const handleCreatePlan = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    loading.value = true
    errorMessage.value = ''

    const planData = {
      title: form.title.trim(),
      destination: form.destination.trim(),
      startDate: form.startDate,
      endDate: form.endDate,
      budget: form.budget,
      description: form.description.trim()
    }

    const response = await planService.createTravelPlan(planData)

    ElMessage.success('旅行计划创建成功！')
    router.push(`/plans/${response.data.id}`) // 跳转到详情页
  } catch (err) {
    loading.value = false
    errorMessage.value = err.message || '创建失败，请稍后重试'
  }
}

const handleCancel = () => {
  router.back()
}
</script>

<style scoped>
.plan-create-container {
  max-width: 860px;
  margin: 20px auto;
  padding: 20px;
}

.plan-create-card {
  background: #ffffff;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.08);
}

.plan-create-card h2 {
  text-align: center;
  font-size: 26px;
  color: #303133;
  margin-bottom: 8px;
}

.tip {
  text-align: center;
  color: #909399;
  margin-bottom: 32px;
  font-size: 14px;
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
}

.field-error :deep(.el-icon) {
  font-size: 14px;
}
</style>