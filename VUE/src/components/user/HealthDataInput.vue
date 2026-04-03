<template>
  <div class="health-data-input-container">
    <div class="page-header">
      <h2>{{ $t('healthData.submitData') || '健康数据录入' }}</h2>
    </div>

    <el-skeleton v-if="isLoading" :rows="8" animated />

    <div v-else class="form-card">
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="120px"
        @submit.prevent="handleSubmit"
      >
        <div class="metrics-grid">
          <el-form-item :label="$t('healthData.height') || '身高'" prop="height">
            <div class="field-with-unit">
              <el-input-number v-model="formData.height" :min="50" :max="250" :step="0.1" controls-position="right" />
              <span class="unit-label">cm</span>
            </div>
          </el-form-item>

          <el-form-item :label="$t('healthData.weight') || '体重'" prop="weight">
            <div class="field-with-unit">
              <el-input-number v-model="formData.weight" :min="20" :max="300" :step="0.1" controls-position="right" />
              <span class="unit-label">kg</span>
            </div>
          </el-form-item>

          <el-form-item :label="$t('healthData.bloodPressure') || '血压'" prop="bloodPressure">
            <div class="field-with-unit">
              <el-input v-model="formData.bloodPressure" placeholder="例如：120/80" clearable />
              <span class="unit-label">mmHg</span>
            </div>
          </el-form-item>

          <el-form-item :label="$t('healthData.heartRate') || '心率'" prop="heartRate">
            <div class="field-with-unit">
              <el-input-number v-model="formData.heartRate" :min="30" :max="200" :step="1" controls-position="right" />
              <span class="unit-label">次/分</span>
            </div>
          </el-form-item>

          <el-form-item label="体温" prop="bodyTemperature">
            <div class="field-with-unit">
              <el-input-number v-model="formData.bodyTemperature" :min="34" :max="42" :step="0.1" controls-position="right" />
              <span class="unit-label">°C</span>
            </div>
          </el-form-item>

          <el-form-item label="血氧" prop="bloodOxygen">
            <div class="field-with-unit">
              <el-input-number v-model="formData.bloodOxygen" :min="70" :max="100" :step="1" controls-position="right" />
              <span class="unit-label">%</span>
            </div>
          </el-form-item>

          <el-form-item label="血糖" prop="bloodSugar">
            <div class="field-with-unit">
              <el-input-number v-model="formData.bloodSugar" :min="2" :max="30" :step="0.1" controls-position="right" />
              <span class="unit-label">mmol/L</span>
            </div>
          </el-form-item>

          <el-form-item label="睡眠时长" prop="sleepDuration">
            <div class="field-with-unit">
              <el-input-number v-model="formData.sleepDuration" :min="0" :max="24" :step="0.5" controls-position="right" />
              <span class="unit-label">小时</span>
            </div>
          </el-form-item>
        </div>

        <el-form-item :label="$t('healthData.dietRecord') || '饮食记录'" prop="dietRecord">
          <el-input v-model="formData.dietRecord" type="textarea" :rows="3" maxlength="500" show-word-limit />
        </el-form-item>

        <el-form-item :label="$t('healthData.exerciseRecord') || '运动记录'" prop="exerciseRecord">
          <el-input v-model="formData.exerciseRecord" type="textarea" :rows="3" maxlength="500" show-word-limit />
        </el-form-item>

        <el-form-item :label="$t('healthData.recordDate') || '记录时间'" prop="recordedAt">
          <el-date-picker
            v-model="formData.recordedAt"
            type="datetime"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="isSubmitting" @click="handleSubmit">
            {{ $t('common.submit') || '提交' }}
          </el-button>
          <el-button @click="resetForm">
            {{ $t('common.reset') || '重置' }}
          </el-button>
        </el-form-item>
      </el-form>

      <el-alert
        v-if="errorMessage"
        :title="errorMessage"
        type="error"
        :closable="true"
        @close="errorMessage = ''"
        class="feedback-alert"
      />
      <el-alert
        v-if="successMessage"
        :title="successMessage"
        type="success"
        :closable="true"
        @close="successMessage = ''"
        class="feedback-alert"
      />

      <el-alert title="指标参考范围" type="info" :closable="false" class="tips-alert">
        <template #default>
          <div class="range-tips">
            <p>身高：50 - 250 cm</p>
            <p>体重：20 - 300 kg</p>
            <p>心率：30 - 200 次/分</p>
            <p>体温：34 - 42 °C</p>
            <p>血氧：70 - 100 %</p>
            <p>血糖：2 - 30 mmol/L</p>
            <p>睡眠时长：0 - 24 小时</p>
            <p>血压格式：例如 120/80</p>
          </div>
        </template>
      </el-alert>
    </div>

    <div v-if="!isLoading && recentData.length > 0" class="recent-data-card">
      <h3>{{ $t('healthData.recentData') || '最近提交记录' }}</h3>
      <el-table :data="recentData" stripe style="width: 100%">
        <el-table-column prop="recordedAt" :label="$t('healthData.recordDate') || '记录时间'" width="180" />
        <el-table-column prop="height" label="身高(cm)" width="110" />
        <el-table-column prop="weight" label="体重(kg)" width="110" />
        <el-table-column prop="bloodPressure" label="血压" width="120" />
        <el-table-column prop="heartRate" label="心率" width="100" />
        <el-table-column prop="bodyTemperature" label="体温" width="100" />
        <el-table-column prop="bloodOxygen" label="血氧" width="100" />
        <el-table-column prop="bloodSugar" label="血糖" width="110" />
        <el-table-column prop="sleepDuration" label="睡眠时长" width="120" />
      </el-table>
    </div>
  </div>
</template>

<script>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { healthDataAPI } from '../../services/api'
import { authService } from '../../services/auth'

const createDefaultFormData = () => ({
  height: null,
  weight: null,
  bloodPressure: '',
  heartRate: null,
  bodyTemperature: null,
  bloodOxygen: null,
  bloodSugar: null,
  sleepDuration: null,
  dietRecord: '',
  exerciseRecord: '',
  recordedAt: new Date().toISOString().slice(0, 19).replace('T', ' ')
})

export default {
  name: 'HealthDataInput',
  setup() {
    const formRef = ref(null)
    const isLoading = ref(true)
    const isSubmitting = ref(false)
    const errorMessage = ref('')
    const successMessage = ref('')
    const recentData = ref([])

    const formData = reactive(createDefaultFormData())

    const numberRule = (message) => [{ type: 'number', message, trigger: 'blur' }]
    const formRules = {
      height: numberRule('请输入有效身高'),
      weight: numberRule('请输入有效体重'),
      heartRate: numberRule('请输入有效心率'),
      bodyTemperature: numberRule('请输入有效体温'),
      bloodOxygen: numberRule('请输入有效血氧'),
      bloodSugar: numberRule('请输入有效血糖'),
      sleepDuration: numberRule('请输入有效睡眠时长')
    }

    const validateBloodPressure = (value) => {
      if (!value) {
        return true
      }
      return /^\d{2,3}\/\d{2,3}$/.test(value.trim())
    }

    const validateDataRange = () => {
      const errors = []

      if (formData.height !== null && (formData.height < 50 || formData.height > 250)) errors.push('身高必须在 50-250 cm 之间')
      if (formData.weight !== null && (formData.weight < 20 || formData.weight > 300)) errors.push('体重必须在 20-300 kg 之间')
      if (formData.heartRate !== null && (formData.heartRate < 30 || formData.heartRate > 200)) errors.push('心率必须在 30-200 次/分之间')
      if (formData.bodyTemperature !== null && (formData.bodyTemperature < 34 || formData.bodyTemperature > 42)) errors.push('体温必须在 34-42 °C 之间')
      if (formData.bloodOxygen !== null && (formData.bloodOxygen < 70 || formData.bloodOxygen > 100)) errors.push('血氧必须在 70-100% 之间')
      if (formData.bloodSugar !== null && (formData.bloodSugar < 2 || formData.bloodSugar > 30)) errors.push('血糖必须在 2-30 mmol/L 之间')
      if (formData.sleepDuration !== null && (formData.sleepDuration < 0 || formData.sleepDuration > 24)) errors.push('睡眠时长必须在 0-24 小时之间')
      if (formData.bloodPressure && !validateBloodPressure(formData.bloodPressure)) errors.push('血压格式应为 120/80')

      return errors
    }

    const fetchRecentData = async () => {
      try {
        const currentUser = authService.getUser()
        if (!currentUser?.id) {
          return
        }

        const response = await healthDataAPI.getUserData()
        if (response?.data) {
          recentData.value = response.data
            .filter((item) => item.dataType !== 'HEALTH_CHECK' && item.dataType !== 'GENDER_SPECIFIC')
            .slice(0, 20)
        }
      } catch (error) {
        console.error('Failed to fetch routine data:', error)
      }
    }

    const resetForm = () => {
      Object.assign(formData, createDefaultFormData())
      errorMessage.value = ''
      successMessage.value = ''
      formRef.value?.clearValidate()
    }

    const handleSubmit = async () => {
      if (!formRef.value) {
        return
      }

      try {
        await formRef.value.validate()

        const rangeErrors = validateDataRange()
        if (rangeErrors.length > 0) {
          errorMessage.value = rangeErrors.join('；')
          ElMessage.error(rangeErrors[0])
          return
        }

        isSubmitting.value = true
        errorMessage.value = ''
        successMessage.value = ''

        const currentUser = authService.getUser()
        if (!currentUser?.id) {
          throw new Error('无法获取用户信息')
        }

        const payload = {
          userId: currentUser.id,
          height: formData.height,
          weight: formData.weight,
          bloodPressure: formData.bloodPressure || null,
          heartRate: formData.heartRate,
          bodyTemperature: formData.bodyTemperature,
          bloodOxygen: formData.bloodOxygen,
          bloodSugar: formData.bloodSugar,
          sleepDuration: formData.sleepDuration,
          dietRecord: formData.dietRecord || null,
          exerciseRecord: formData.exerciseRecord || null,
          dataType: 'ROUTINE',
          recordedAt: formData.recordedAt
        }

        await healthDataAPI.submitData(payload)
        successMessage.value = '健康数据已提交'
        ElMessage.success('健康数据已提交')
        resetForm()
        await fetchRecentData()
      } catch (error) {
        console.error('Failed to submit routine data:', error)
        errorMessage.value = error.response?.data?.message || error.message || '提交健康数据失败'
      } finally {
        isSubmitting.value = false
      }
    }

    onMounted(async () => {
      try {
        await fetchRecentData()
      } finally {
        isLoading.value = false
      }
    })

    return {
      formRef,
      formData,
      formRules,
      isLoading,
      isSubmitting,
      errorMessage,
      successMessage,
      recentData,
      handleSubmit,
      resetForm
    }
  }
}
</script>

<style scoped>
.health-data-input-container {
  width: 100%;
  padding: 8px 0 24px;
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
  font-weight: 700;
}

.form-card,
.recent-data-card {
  background: #fff;
  border-radius: 16px;
  border: 1px solid #e8eef5;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  padding: 36px;
}

.form-card {
  margin-bottom: 30px;
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0 24px;
}

.field-with-unit {
  display: flex;
  align-items: center;
  width: 100%;
}

.field-with-unit :deep(.el-input-number),
.field-with-unit :deep(.el-input) {
  flex: 1;
}

.unit-label {
  margin-left: 10px;
  color: #909399;
  white-space: nowrap;
}

.feedback-alert,
.tips-alert {
  margin-top: 20px;
}

.range-tips {
  line-height: 1.9;
}

.range-tips p {
  margin: 0;
}

.recent-data-card h3 {
  margin: 0 0 20px;
  font-size: 20px;
  color: #1f2d3d;
}

@media (max-width: 992px) {
  .metrics-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .form-card,
  .recent-data-card {
    padding: 20px;
  }
}
</style>
