<template>
  <div class="gender-health-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>{{ $t('genderHealth.genderHealth') || '性别健康' }}</h2>
    </div>

    <!-- 隐私保护提示 -->
    <el-alert
      :title="$t('genderHealth.privacyNotice') || '此信息为隐私数据，仅您和您的医师可见'"
      type="warning"
      :closable="false"
      class="privacy-alert"
    >
      <template #default>
        <div class="privacy-content">
          <el-icon class="privacy-icon"><warning-filled /></el-icon>
          <span>{{ $t('genderHealth.privacyNotice') || '此信息为隐私数据，仅您和您的医师可见' }}</span>
        </div>
      </template>
    </el-alert>

    <!-- 加载状态 -->
    <el-skeleton v-if="isLoading" :rows="8" animated />

    <!-- 性别健康数据采集表单 -->
    <div v-else class="form-card">
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="140px"
        @submit.prevent="handleSubmit"
      >
        <!-- 性别选择 -->
        <el-form-item :label="$t('user.gender') || '性别'" prop="gender">
          <el-select
            v-model="formData.gender"
            :placeholder="$t('user.gender') || '请选择性别'"
            clearable
          >
            <el-option label="男" value="MALE" />
            <el-option label="女" value="FEMALE" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>

        <!-- 女性特定数据 -->
        <template v-if="formData.gender === 'FEMALE'">
          <!-- 月经周期 -->
          <el-form-item :label="$t('genderHealth.menstrualCycle') || '月经周期(天)'" prop="menstrualCycle">
            <el-input-number
              v-model="formData.menstrualCycle"
              :min="15"
              :max="60"
              :step="1"
              :placeholder="$t('genderHealth.menstrualCycle') || '月经周期(天)'"
              controls-position="right"
            />
            <span class="unit-label">天</span>
          </el-form-item>

          <!-- 月经天数 -->
          <el-form-item :label="$t('genderHealth.menstrualDays') || '月经天数'" prop="menstrualDays">
            <el-input-number
              v-model="formData.menstrualDays"
              :min="1"
              :max="14"
              :step="1"
              :placeholder="$t('genderHealth.menstrualDays') || '月经天数'"
              controls-position="right"
            />
            <span class="unit-label">天</span>
          </el-form-item>

          <!-- 最后月经日期 -->
          <el-form-item :label="$t('genderHealth.lastMenstrualDate') || '最后月经日期'" prop="lastMenstrualDate">
            <el-date-picker
              v-model="formData.lastMenstrualDate"
              type="date"
              :placeholder="$t('genderHealth.lastMenstrualDate') || '选择最后月经日期'"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>

          <!-- 妊娠状态 -->
          <el-form-item :label="$t('genderHealth.pregnancyStatus') || '妊娠状态'" prop="pregnancyStatus">
            <el-select
              v-model="formData.pregnancyStatus"
              :placeholder="$t('genderHealth.pregnancyStatus') || '请选择妊娠状态'"
              clearable
            >
              <el-option label="未妊娠" value="NOT_PREGNANT" />
              <el-option label="妊娠中" value="PREGNANT" />
              <el-option label="哺乳期" value="LACTATING" />
            </el-select>
          </el-form-item>

          <!-- 月经症状 -->
          <el-form-item :label="$t('genderHealth.menstrualSymptoms') || '月经症状'" prop="menstrualSymptoms">
            <el-input
              v-model="formData.menstrualSymptoms"
              type="textarea"
              :rows="3"
              :placeholder="$t('genderHealth.menstrualSymptoms') || '请记录月经相关症状（可选）'"
              clearable
              maxlength="500"
              show-word-limit
            />
          </el-form-item>
        </template>

        <!-- 男性特定数据 -->
        <template v-if="formData.gender === 'MALE'">
          <!-- 前列腺健康 -->
          <el-form-item :label="$t('genderHealth.prostateHealth') || '前列腺健康'" prop="prostateHealth">
            <el-input
              v-model="formData.prostateHealth"
              type="textarea"
              :rows="3"
              :placeholder="$t('genderHealth.prostateHealth') || '请记录前列腺相关信息（可选）'"
              clearable
              maxlength="500"
              show-word-limit
            />
          </el-form-item>

          <!-- 性功能状态 -->
          <el-form-item :label="$t('genderHealth.sexualFunction') || '性功能状态'" prop="sexualFunction">
            <el-select
              v-model="formData.sexualFunction"
              :placeholder="$t('genderHealth.sexualFunction') || '请选择性功能状态'"
              clearable
            >
              <el-option label="正常" value="NORMAL" />
              <el-option label="异常" value="ABNORMAL" />
              <el-option label="需要咨询" value="NEED_CONSULTATION" />
            </el-select>
          </el-form-item>
        </template>

        <!-- 通用备注 -->
        <el-form-item :label="$t('genderHealth.notes') || '备注'" prop="notes">
          <el-input
            v-model="formData.notes"
            type="textarea"
            :rows="3"
            :placeholder="$t('genderHealth.notes') || '请记录其他相关信息（可选）'"
            clearable
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <!-- 记录时间 -->
        <el-form-item :label="$t('healthData.recordDate') || '记录时间'" prop="recordedAt">
          <el-date-picker
            v-model="formData.recordedAt"
            type="datetime"
            :placeholder="$t('healthData.recordDate') || '选择记录时间'"
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
    </div>

    <!-- 最近记录 -->
    <div v-if="!isLoading && recentRecords.length > 0" class="recent-records-card">
      <h3>{{ $t('genderHealth.recentRecords') || '最近记录' }}</h3>
      <el-table :data="recentRecords" stripe style="width: 100%">
        <el-table-column prop="recordedAt" :label="$t('healthData.recordDate') || '记录时间'" width="180" />
        <el-table-column prop="gender" :label="$t('user.gender') || '性别'" width="80" :formatter="formatGender" />
        <el-table-column prop="menstrualCycle" :label="$t('genderHealth.menstrualCycle') || '月经周期'" width="100" />
        <el-table-column prop="pregnancyStatus" :label="$t('genderHealth.pregnancyStatus') || '妊娠状态'" width="100" :formatter="formatPregnancyStatus" />
        <el-table-column prop="notes" :label="$t('genderHealth.notes') || '备注'" width="200" :show-overflow-tooltip="true" />
      </el-table>
    </div>

    <!-- 无记录提示 -->
    <div v-if="!isLoading && recentRecords.length === 0" class="no-records-card">
      <el-empty :description="$t('healthHistory.noRecords') || '暂无记录'" />
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { WarningFilled } from '@element-plus/icons-vue'
import { healthDataAPI } from '../../services/api'
import { authService } from '../../services/auth'

export default {
  name: 'GenderHealth',
  components: {
    WarningFilled
  },
  setup() {
    const formRef = ref(null)
    const isLoading = ref(true)
    const isSubmitting = ref(false)
    const errorMessage = ref('')
    const successMessage = ref('')
    const recentRecords = ref([])

    const formData = reactive({
      gender: '',
      menstrualCycle: null,
      menstrualDays: null,
      lastMenstrualDate: '',
      pregnancyStatus: '',
      menstrualSymptoms: '',
      prostateHealth: '',
      sexualFunction: '',
      notes: '',
      recordedAt: new Date().toISOString().slice(0, 19).replace('T', ' ')
    })

    // 表单验证规则
    const formRules = {
      gender: [
        { required: false, message: '性别为可选项', trigger: 'change' }
      ],
      menstrualCycle: [
        { required: false, message: '月经周期为可选项', trigger: 'blur' },
        { type: 'number', message: '月经周期必须是数字', trigger: 'blur' }
      ],
      menstrualDays: [
        { required: false, message: '月经天数为可选项', trigger: 'blur' },
        { type: 'number', message: '月经天数必须是数字', trigger: 'blur' }
      ],
      lastMenstrualDate: [
        { required: false, message: '最后月经日期为可选项', trigger: 'change' }
      ],
      pregnancyStatus: [
        { required: false, message: '妊娠状态为可选项', trigger: 'change' }
      ],
      menstrualSymptoms: [
        { required: false, message: '月经症状为可选项', trigger: 'blur' }
      ],
      prostateHealth: [
        { required: false, message: '前列腺健康为可选项', trigger: 'blur' }
      ],
      sexualFunction: [
        { required: false, message: '性功能状态为可选项', trigger: 'change' }
      ],
      notes: [
        { required: false, message: '备注为可选项', trigger: 'blur' }
      ],
      recordedAt: [
        { required: false, message: '记录时间为可选项', trigger: 'change' }
      ]
    }

    // 验证数据范围
    const validateDataRange = () => {
      const errors = []

      if (formData.menstrualCycle !== null && (formData.menstrualCycle < 15 || formData.menstrualCycle > 60)) {
        errors.push('月经周期必须在15-60天之间')
      }

      if (formData.menstrualDays !== null && (formData.menstrualDays < 1 || formData.menstrualDays > 14)) {
        errors.push('月经天数必须在1-14天之间')
      }

      return errors
    }

    // 格式化性别显示
    const formatGender = (row) => {
      const genderMap = {
        'MALE': '男',
        'FEMALE': '女',
        'OTHER': '其他'
      }
      return genderMap[row.gender] || row.gender
    }

    // 格式化妊娠状态显示
    const formatPregnancyStatus = (row) => {
      const statusMap = {
        'NOT_PREGNANT': '未妊娠',
        'PREGNANT': '妊娠中',
        'LACTATING': '哺乳期'
      }
      return statusMap[row.pregnancyStatus] || row.pregnancyStatus
    }

    // 获取最近记录
    const fetchRecentRecords = async () => {
      try {
        const currentUser = authService.getUser()
        if (!currentUser || !currentUser.id) {
          return
        }

        const response = await healthDataAPI.getUserData()
        if (response && response.data) {
          // 过滤出GENDER_SPECIFIC类型的数据，只显示最近5条
          const genderData = response.data.filter(item => item.dataType === 'GENDER_SPECIFIC')
          recentRecords.value = genderData.slice(0, 5)
        }
      } catch (error) {
        console.error('获取最近记录失败:', error)
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
          height: null,
          weight: null,
          bloodPressure: null,
          heartRate: null,
          dietRecord: formData.gender === 'FEMALE' 
            ? `月经周期:${formData.menstrualCycle},月经天数:${formData.menstrualDays},妊娠状态:${formData.pregnancyStatus},症状:${formData.menstrualSymptoms}`
            : `前列腺健康:${formData.prostateHealth},性功能:${formData.sexualFunction}`,
          exerciseRecord: formData.notes || null,
          dataType: 'GENDER_SPECIFIC',
          recordedAt: formData.recordedAt,
          // 额外的性别健康数据字段
          gender: formData.gender,
          menstrualCycle: formData.menstrualCycle,
          menstrualDays: formData.menstrualDays,
          lastMenstrualDate: formData.lastMenstrualDate,
          pregnancyStatus: formData.pregnancyStatus,
          menstrualSymptoms: formData.menstrualSymptoms,
          prostateHealth: formData.prostateHealth,
          sexualFunction: formData.sexualFunction
        }

        // 调用API提交性别健康数据
        const response = await healthDataAPI.submitData(submitRequest)

        if (response && response.data) {
          successMessage.value = response.message || '性别健康数据已保存'
          ElMessage.success('性别健康数据已保存')
          
          // 重置表单
          resetForm()
          
          // 刷新最近记录
          await fetchRecentRecords()
        }
      } catch (error) {
        console.error('保存性别健康数据失败:', error)
        if (error.response && error.response.data) {
          errorMessage.value = error.response.data.message || '保存性别健康数据失败'
        } else if (error.message) {
          errorMessage.value = error.message
        } else {
          errorMessage.value = '保存性别健康数据失败'
        }
      } finally {
        isSubmitting.value = false
      }
    }

    // 重置表单
    const resetForm = () => {
      formData.gender = ''
      formData.menstrualCycle = null
      formData.menstrualDays = null
      formData.lastMenstrualDate = ''
      formData.pregnancyStatus = ''
      formData.menstrualSymptoms = ''
      formData.prostateHealth = ''
      formData.sexualFunction = ''
      formData.notes = ''
      formData.recordedAt = new Date().toISOString().slice(0, 19).replace('T', ' ')
      
      if (formRef.value) {
        formRef.value.resetFields()
      }
    }

    // 页面加载时获取最近记录
    onMounted(async () => {
      try {
        await fetchRecentRecords()
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
      recentRecords,
      handleSubmit,
      resetForm,
      formatGender,
      formatPregnancyStatus
    }
  }
}
</script>

<style scoped>
.gender-health-container {
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

.privacy-alert {
  margin-bottom: 30px;
}

.privacy-content {
  display: flex;
  align-items: center;
  gap: 10px;
}

.privacy-icon {
  font-size: 18px;
  color: #e6a23c;
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

.recent-records-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 30px;
  margin-bottom: 30px;
}

.recent-records-card h3 {
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
  .gender-health-container {
    padding: 10px;
  }

  .form-card {
    padding: 20px;
  }

  .recent-records-card {
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
