<template>
  <div class="registration-form-modal">
    <div class="modal-overlay" @click="closeForm"></div>
    <div class="modal-content">
      <div class="modal-header">
        <h2>报名活动</h2>
        <button class="btn-close" @click="closeForm">×</button>
      </div>

      <form @submit.prevent="submitForm" class="form">
        <!-- Activity Title (Read-only) -->
        <div class="form-group">
          <label>活动名称</label>
          <input type="text" :value="activity.title" disabled class="form-input" />
        </div>

        <!-- Participant Name -->
        <div class="form-group">
          <label>姓名 <span class="required">*</span></label>
          <input
            v-model="formData.participantName"
            type="text"
            placeholder="请输入您的姓名"
            class="form-input"
            @blur="validateField('participantName')"
          />
          <span v-if="errors.participantName" class="error-message">{{ errors.participantName }}</span>
        </div>

        <!-- Contact Phone -->
        <div class="form-group">
          <label>联系方式 <span class="required">*</span></label>
          <input
            v-model="formData.contactPhone"
            type="tel"
            placeholder="请输入您的手机号"
            class="form-input"
            @blur="validateField('contactPhone')"
          />
          <span v-if="errors.contactPhone" class="error-message">{{ errors.contactPhone }}</span>
        </div>

        <!-- Remarks -->
        <div class="form-group">
          <label>备注</label>
          <textarea
            v-model="formData.remarks"
            placeholder="请输入您的备注信息（可选）"
            class="form-textarea"
            rows="4"
          ></textarea>
        </div>

        <!-- Form Actions -->
        <div class="form-actions">
          <button type="button" class="btn-cancel" @click="closeForm">取消</button>
          <button type="submit" class="btn-submit" :disabled="isSubmitting">
            {{ isSubmitting ? '提交中...' : '确认报名' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../utils/http'

const props = defineProps({
  activity: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['close', 'success'])

const formData = reactive({
  participantName: '',
  contactPhone: '',
  remarks: ''
})

const errors = reactive({
  participantName: '',
  contactPhone: ''
})

const isSubmitting = ref(false)

/**
 * Validate a single field
 */
const validateField = (fieldName) => {
  errors[fieldName] = ''

  if (fieldName === 'participantName') {
    if (!formData.participantName.trim()) {
      errors.participantName = '姓名不能为空'
    }
  } else if (fieldName === 'contactPhone') {
    const phoneRegex = /^1[3-9]\d{9}$/
    if (!formData.contactPhone.trim()) {
      errors.contactPhone = '手机号不能为空'
    } else if (!phoneRegex.test(formData.contactPhone)) {
      errors.contactPhone = '请输入有效的手机号'
    }
  }
}

/**
 * Validate all fields
 */
const validateForm = () => {
  validateField('participantName')
  validateField('contactPhone')

  return !errors.participantName && !errors.contactPhone
}

/**
 * Submit registration form
 */
const submitForm = async () => {
  if (!validateForm()) {
    ElMessage.error('请填写完整的表单信息')
    return
  }

  isSubmitting.value = true

  try {
    const registrationData = {
      activityId: props.activity.id,
      participantName: formData.participantName,
      contactPhone: formData.contactPhone,
      remarks: formData.remarks
    }

    await http.post('/registrations', registrationData)
    ElMessage.success('报名成功')
    emit('success')
    closeForm()
  } catch (error) {
    ElMessage.error(error.message || '报名失败，请重试')
  } finally {
    isSubmitting.value = false
  }
}

/**
 * Close registration form
 */
const closeForm = () => {
  emit('close')
}
</script>

<style scoped>
.registration-form-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
}

.modal-content {
  position: relative;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid var(--border-color);
}

.modal-header h2 {
  margin: 0;
  font-size: 18px;
  color: var(--text-primary);
}

.btn-close {
  background: none;
  border: none;
  font-size: 28px;
  color: var(--text-secondary);
  cursor: pointer;
  padding: 0;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: color 0.3s ease;
}

.btn-close:hover {
  color: var(--text-primary);
}

.form {
  padding: 20px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: var(--text-primary);
  font-size: 14px;
}

.required {
  color: #f56c6c;
}

.form-input,
.form-textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  font-size: 14px;
  font-family: inherit;
  transition: border-color 0.3s ease;
}

.form-input:focus,
.form-textarea:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.1);
}

.form-input:disabled {
  background-color: #f5f7fa;
  color: var(--text-secondary);
  cursor: not-allowed;
}

.form-textarea {
  resize: vertical;
  min-height: 100px;
}

.error-message {
  display: block;
  color: #f56c6c;
  font-size: 12px;
  margin-top: 4px;
}

.form-actions {
  display: flex;
  gap: 12px;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid var(--border-color);
}

.btn-cancel,
.btn-submit {
  flex: 1;
  padding: 10px 16px;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-cancel {
  background-color: #f5f7fa;
  color: var(--text-primary);
  border: 1px solid var(--border-color);
}

.btn-cancel:hover {
  background-color: #eff2f5;
}

.btn-submit {
  background-color: var(--primary-color);
  color: white;
}

.btn-submit:hover:not(:disabled) {
  background-color: var(--primary-light);
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.3);
}

.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

@media (max-width: 600px) {
  .modal-content {
    width: 95%;
    max-height: 95vh;
  }

  .form {
    padding: 16px;
  }

  .form-group {
    margin-bottom: 16px;
  }
}
</style>
