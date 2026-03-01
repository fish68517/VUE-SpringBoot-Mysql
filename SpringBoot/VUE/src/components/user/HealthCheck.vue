<template>
  <div class="health-check-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>{{ $t('healthCheck.routineCheck') || '常规健康检查' }}</h2>
    </div>

    <!-- 加载状态 -->
    <el-skeleton v-if="isLoading" :rows="8" animated />

    <!-- 健康检查表单 -->
    <div v-else class="form-card">
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="120px"
        @submit.prevent="handleSubmit"
      >
        <!-- 检查项目 - 身高 -->
        <el-form-item :label="$t('healthData.height') || '身高(cm)'" prop="height">
          <el-input-number
            v-model="formData.height"
            :min="50"
            :max="250"
            :step="0.1"
            :placeholder="$t('healthData.height') || '身高(cm)'"
            controls-position="right"
          />
          <span class="unit-label">cm</span>
        </el-form-item>

        <!-- 检查项目 - 体重 -->
        <el-form-item :label="$t('healthData.weight') || '体重(kg)'" prop="weight">
          <el-input-number
            v-model="formData.weight"
            :min="20"
            :max="300"
            :step="0.1"
            :placeholder="$t('healthData.weight') || '体重(kg)'"
            controls-position="right"
          />
          <span class="unit-label">kg</span>
        </el-form-item>

        <!-- 检查项目 - 血压 -->
        <el-form-item :label="$t('healthData.bloodPressure') || '血压'" prop="bloodPressure">
          <el-input
            v-model="formData.bloodPressure"
            :placeholder="$t('healthData.bloodPressure') || '血压 (例如: 120/80)'"
            clearable
          />
          <span class="unit-label">mmHg</span>
        </el-form-item>

        <!-- 检查项目 - 心率 -->
        <el-form-item :label="$t('healthData.heartRate') || '心率(次/分)'" prop="heartRate">
          <el-input-number
            v-model="formData.heartRate"
            :min="30"
            :max="200"
            :step="1"
            :placeholder="$t('healthData.heartRate') || '心率(次/分)'"
            controls-position="right"
          />
          <span class="unit-label">次/分</span>
        </el-form-item>

        <!-- 检查项目 - 检查结果备注 -->
        <el-form-item :label="$t('healthCheck.checkResults') || '检查结果'" prop="checkResults">
          <el-input
            v-model="formData.checkResults"
            type="textarea"
            :rows="3"
            :placeholder="$t('healthCheck.checkResults') || '请记录检查结果和医师建议（可选）'"
            clearable
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <!-- 检查日期 -->
        <el-form-item :label="$t('healthCheck.checkDate') || '检查日期'" prop="recordedAt">
          <el-date-picker
            v-model="formData.recordedAt"
            type="datetime"
            :placeholder="$t('healthCheck.checkDate') || '选择检查日期'"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>

        <!-- 操作按钮 -->
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="isSubmitting">
            {{ $t('common.submit') || '提交' }}
          </el-button>
          <el-button @click="resetForm">
            {{ $t('common.reset') || '重置' }}
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 错误提示 -->
      <el-alert
        v-if="errorMessage"
        :title="errorMessage"
        type="error"
        :closable="true"
        @close="errorMessage = ''"
        class="error-alert"
      />

      <!-- 成功提示 -->
      <el-alert
        v-if="successMessage"
        :title="successMessage"
        type="success"
        :closable="true"
        @close="successMessage = ''"
        class="success-alert"
      />

      <!-- 数据范围提示 -->
      <el-alert
        title="检查数据范围提示"
        type="info"
        :closable="false"
        class="info-alert"
      >
        <template #default>
          <div class="range-tips">
            <p><strong>身高:</strong> 50-250 cm</p>
            <p><strong>体重:</strong> 20-300 kg</p>
            <p><strong>心率:</strong> 30-200 次/分</p>
            <p><strong>血压:</strong> 格式为 收缩压/舒张压 (例如: 120/80)</p>
          </div>
        </template>
      </el-alert>
    </div>

    <!-- 最近检查记录 -->
    <div v-if="!isLoading && recentChecks.length > 0" class="recent-checks-card">
      <h3>{{ $t('healthCheck.routineCheck') || '最近检查记录' }}</h3>
      <el-table :data="recentChecks" stripe style="width: 100%">
        <el-table-column prop="recordedAt" :label="$t('healthCheck.checkDate') || '检查日期'" width="180" />
        <el-table-column prop="height" :label="$t('healthData.height') || '身高(cm)'" width="100" />
        <el-table-column prop="weight" :label="$t('healthData.weight') || '体重(kg)'" width="100" />
        <el-table-column prop="bloodPressure" :label="$t('healthData.bloodPressure') || '血压'" width="100" />
        <el-table-column prop="heartRate" :label="$t('healthData.heartRate') || '心率(次/分)'" width="120" />
        <el-table-column prop="checkResults" :label="$t('healthCheck.checkResults') || '检查结果'" width="200" :show-overflow-tooltip="true" />
      </el-table>
    </div>

    <!-- 无记录提示 -->
    <div v-if="!isLoading && recentChecks.length === 0" class="no-records-card">
      <el-empty :description="$t('healthHistory.noRecords') || '暂无检查记录'" />
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { healthDataAPI } from '../../services/api'
import { authService } from '../../services/auth'

export default {
  name: 'HealthCheck',
  setup() {
    const formRef = ref(null)
    const isLoading = ref(true)
    const isSubmitting = ref(false)
    const errorMessage = ref('')
    const successMessage = ref('')
    const recentChecks = ref([])

    const formData = reactive({
      height: null,
      weight: null,
      bloodPressure: '',
      heartRate: null,
      checkResults: '',
      recordedAt: new Date().toISOString().slice(0, 19).replace('T', ' ')
    })

    // 表单验证规则
    const formRules = {
      height: [
        { required: false, message: '身高为可选项', trigger: 'blur' },
        { type: 'number', message: '身高必须是数字', trigger: 'blur' }
      ],
      weight: [
        { required: false, message: '体重为可选项', trigger: 'blur' },
        { type: 'number', message: '体重必须是数字', trigger: 'blur' }
      ],
      bloodPressure: [
        { required: false, message: '血压为可选项', trigger: 'blur' }
      ],
      heartRate: [
        { required: false, message: '心率为可选项', trigger: 'blur' },
        { type: 'number', message: '心率必须是数字', trigger: 'blur' }
      ],
      checkResults: [
        { required: false, message: '检查结果为可选项', trigger: 'blur' }
      ],
      recordedAt: [
        { required: false, message: '检查日期为可选项', trigger: 'blur' }
      ]
    }

    // 验证血压格式
    const validateBloodPressure = (bloodPressure) => {
      if (!bloodPressure || bloodPressure.trim() === '') {
        return true // 可选字段
      }
      // 血压格式: 120/80
      const bpRegex = /^\d{2,3}\/\d{2,3}$/
      return bpRegex.test(bloodPressure)
    }

    // 验证数据范围
    const validateDataRange = () => {
      const errors = []

      if (formData.height !== null && (formData.height < 50 || formData.height > 250)) {
        errors.push('身高必须在50-250cm之间')
      }

      if (formData.weight !== null && (formData.weight < 20 || formData.weight > 300)) {
        errors.push('体重必须在20-300kg之间')
      }

      if (formData.heartRate !== null && (formData.heartRate < 30 || formData.heartRate > 200)) {
        errors.push('心率必须在30-200次/分之间')
      }

      if (formData.bloodPressure && !validateBloodPressure(formData.bloodPressure)) {
        errors.push('血压格式不正确，请使用 收缩压/舒张压 格式（例如: 120/80）')
      }

      return errors
    }

    // 获取最近检查记录
    const fetchRecentChecks = async () => {
      try {
        const currentUser = authService.getUser()
        if (!currentUser || !currentUser.id) {
          return
        }

        const response = await healthDataAPI.getUserData()
        if (response && response.data) {
          // 过滤出ROUTINE类型的数据，只显示最近5条
          const routineData = response.data.filter(item => item.dataType === 'ROUTINE')
          recentChecks.value = routineData.slice(0, 5)
        }
      } catch (error) {
        console.error('获取检查记录失败:', error)
      }
    }

    // 提交表单
    const handleSubmit = async () => {
      if (!formRef.value) return

      try {
        // 验证数据范围
        const rangeErrors = validateDataRange()
        if (rangeErrors.length > 0) {
          errorMessage.value = rangeErrors.join('; ')
          ElMessage.error(rangeErrors[0])
          return
        }

        isSubmitting.value = true
        errorMessage.value = ''
        successMessage.value = ''

        // 获取当前用户ID
        const currentUser = authService.getUser()
        if (!currentUser || !currentUser.id) {
          throw new Error('无法获取用户信息')
        }

        // 构建提交请求
        const submitRequest = {
          userId: currentUser.id,
          height: formData.height,
          weight: formData.weight,
          bloodPressure: formData.bloodPressure || null,
          heartRate: formData.heartRate,
          dietRecord: formData.checkResults || null,
          exerciseRecord: null,
          dataType: 'ROUTINE',
          recordedAt: formData.recordedAt
        }

        // 调用API提交检查记录
        const response = await healthDataAPI.submitData(submitRequest)

        if (response && response.data) {
          successMessage.value = response.message || '检查记录已保存'
          ElMessage.success('检查记录已保存')
          
          // 重置表单
          resetForm()
          
          // 刷新最近记录
          await fetchRecentChecks()
        }
      } catch (error) {
        console.error('保存检查记录失败:', error)
        if (error.response && error.response.data) {
          errorMessage.value = error.response.data.message || '保存检查记录失败'
        } else if (error.message) {
          errorMessage.value = error.message
        } else {
          errorMessage.value = '保存检查记录失败'
        }
      } finally {
        isSubmitting.value = false
      }
    }

    // 重置表单
    const resetForm = () => {
      formData.height = null
      formData.weight = null
      formData.bloodPressure = ''
      formData.heartRate = null
      formData.checkResults = ''
      formData.recordedAt = new Date().toISOString().slice(0, 19).replace('T', ' ')
      
      if (formRef.value) {
        formRef.value.resetFields()
      }
    }

    // 页面加载时获取最近记录
    onMounted(async () => {
      try {
        await fetchRecentChecks()
      } finally {
        isLoading.value = false
      }
    })

    return {
      formRef,
      isLoading,
      isSubmitting,
      errorMessage,
      successMessage,
      formData,
      formRules,
      recentChecks,
      handleSubmit,
      resetForm
    }
  }
}
</script>

<style scoped>
.health-check-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  margin-bottom: 30px;
  border-bottom: 2px solid #409eff;
  padding-bottom: 10px;
}

.page-header h2 {
  margin: 0;
  color: #333;
  font-size: 24px;
  font-weight: bold;
}

.form-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 30px;
  margin-bottom: 30px;
}

.unit-label {
  margin-left: 10px;
  color: #909399;
  font-size: 14px;
}

.error-alert {
  margin-top: 20px;
}

.success-alert {
  margin-top: 20px;
}

.info-alert {
  margin-top: 20px;
}

.range-tips {
  padding: 10px 0;
}

.range-tips p {
  margin: 8px 0;
  font-size: 14px;
  color: #606266;
}

.range-tips strong {
  color: #333;
}

.recent-checks-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 30px;
  margin-bottom: 30px;
}

.recent-checks-card h3 {
  margin: 0 0 20px 0;
  color: #333;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 2px solid #409eff;
  padding-bottom: 10px;
}

.no-records-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 60px 30px;
  text-align: center;
}

:deep(.el-form-item) {
  margin-bottom: 20px;
}

:deep(.el-input__wrapper) {
  padding: 8px 12px;
}

:deep(.el-select) {
  width: 100%;
}

:deep(.el-date-picker) {
  width: 100%;
}

:deep(.el-table) {
  margin-top: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .health-check-container {
    padding: 10px;
  }

  .form-card {
    padding: 20px;
  }

  .recent-checks-card {
    padding: 20px;
  }

  .no-records-card {
    padding: 40px 20px;
  }

  .page-header h2 {
    font-size: 20px;
  }

  :deep(.el-form-item__label) {
    width: 100% !important;
    text-align: left;
    margin-bottom: 8px;
  }

  :deep(.el-form-item__content) {
    margin-left: 0 !important;
  }

  :deep(.el-table) {
    font-size: 12px;
  }

  :deep(.el-table__header th) {
    padding: 8px 0;
  }

  :deep(.el-table__body td) {
    padding: 8px 0;
  }
}
</style>
