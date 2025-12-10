<template>
  <div class="plan-edit-container">
    <div class="plan-edit-card">
      <h2>编辑旅行计划</h2>
      <p class="tip">修改以下信息后点击保存，即可更新你的旅行计划</p>

      <!-- 加载中 -->
      <el-skeleton v-if="loading" :rows="12" animated style="padding: 40px" />

      <!-- 编辑表单 -->
      <el-form
        v-else
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="140px"
        @submit.prevent="handleUpdatePlan"
      >
        <!-- 计划标题 -->
        <el-form-item label="计划标题" prop="title">
          <el-input
            v-model="form.title"
            placeholder="例如：2025北海道赏樱之旅"
            clearable
            maxlength="200"
            show-word-limit
          />
        </el-form-item>

        <!-- 目的地 -->
        <el-form-item label="目的地" prop="destination">
          <el-input
            v-model="form.destination"
            placeholder="例如：日本·北海道、泰国·曼谷"
            clearable
            maxlength="200"
            show-word-limit
          />
        </el-form-item>

        <!-- 开始日期 -->
        <el-form-item label="开始日期" prop="startDate">
          <el-date-picker
            v-model="form.startDate"
            type="date"
            placeholder="选择出发日期"
            style="width: 100%"
            :disabled-date="time => time.getTime() < Date.now() - 8.64e7"
          />
        </el-form-item>

        <!-- 结束日期 -->
        <el-form-item label="结束日期" prop="endDate">
          <el-date-picker
            v-model="form.endDate"
            type="date"
            placeholder="选择返程日期"
            style="width: 100%"
            :disabled-date="time => form.startDate && time.getTime() < new Date(form.startDate).getTime()"
          />
        </el-form-item>

        <!-- 预算金额 -->
        <el-form-item label="预算金额" prop="budget">
          <el-input-number
            v-model="form.budget"
            :min="0"
            :step="500"
            :precision="0"
            placeholder="请输入预计预算（元）"
            style="width: 100%"
            controls-position="right"
          />
        </el-form-item>

        <!-- 计划描述 -->
        <el-form-item label="计划描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="5"
            placeholder="简单说说这次旅行的亮点、想去的地方、期待的事～（可选）"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <!-- 操作按钮 -->
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            @click="handleUpdatePlan"
            :loading="submitting"
            style="width: 100%; height: 52px; font-size: 16px"
          >
            保存修改
          </el-button>

          <el-button
            size="large"
            @click="handleCancel"
            style="width: 100%; margin-top: 14px; margin-left: 0"
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
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { planService } from '../services/planService'
import { useUserStore } from '../stores/userStore'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(true)
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

// 全中文校验规则
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

// 加载计划详情
const loadPlanDetails = async () => {
  try {
    loading.value = true
    errorMessage.value = ''

    if (!userStore.isAuthenticated) {
      errorMessage.value = '请先登录后再编辑计划'
      router.push('/login')
      return
    }

    const response = await planService.getTravelPlanById(planId)
    const data = response.data

    // 填充表单
    form.title = data.title
    form.destination = data.destination
    form.startDate = new Date(data.startDate)
    form.endDate = new Date(data.endDate)
    form.budget = data.budget
    form.description = data.description || ''
  } catch (err) {
    errorMessage.value = err.message || '加载计划失败，请刷新重试'
  } finally {
    loading.value = false
  }
}

// 保存修改
const handleUpdatePlan = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    submitting.value = true
    errorMessage.value = ''

    const planData = {
      title: form.title.trim(),
      destination: form.destination.trim(),
      startDate: form.startDate,
      endDate: form.endDate,
      budget: form.budget,
      description: form.description.trim()
    }

    await planService.updateTravelPlan(planId, planData)

    ElMessage.success('旅行计划已成功更新！')
    router.push(`/plans/${planId}`)
  } catch (err) {
    submitting.value = false
    errorMessage.value = err.message || '保存失败，请稍后重试'
  }
}

const handleCancel = () => {
  router.back()
}

onMounted(() => {
  loadPlanDetails()
})
</script>

<style scoped>
.plan-edit-container {
  max-width: 860px;
  margin: 20px auto;
  padding: 20px;
}

.plan-edit-card {
  background: #ffffff;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.08);
}

.plan-edit-card h2 {
  text-align: center;
  font-size: 26px;
  color: #303133;
  margin-bottom: 8px;
}

.tip {
  text-align: center;
  color: #909399;
  margin-bottom: 36px;
  font-size: 14px;
}
</style>