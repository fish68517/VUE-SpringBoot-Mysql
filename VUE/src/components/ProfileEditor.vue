<template>
  <div class="profile-editor">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="120px"
      @submit.prevent="handleSubmit"
    >
      <el-form-item label="Username" prop="username">
        <div class="form-field-wrapper">
          <el-input
            v-model="formData.username"
            placeholder="Enter your username"
            maxlength="50"
            show-word-limit
            @blur="validateField('username')"
          />
          <div v-if="hasFieldError('username')" class="field-error">
            <el-icon><CircleCloseFilled /></el-icon>
            <span>{{ getFieldError('username') }}</span>
          </div>
        </div>
      </el-form-item>

      <el-form-item label="Avatar URL" prop="avatarUrl">
        <div class="form-field-wrapper">
          <el-input
            v-model="formData.avatarUrl"
            placeholder="Enter avatar URL"
            maxlength="255"
            @blur="validateField('avatarUrl')"
          />
          <div v-if="hasFieldError('avatarUrl')" class="field-error">
            <el-icon><CircleCloseFilled /></el-icon>
            <span>{{ getFieldError('avatarUrl') }}</span>
          </div>
          <div v-else-if="formData.avatarUrl" class="field-hint">
            Enter a valid image URL
          </div>
        </div>
      </el-form-item>

      <el-form-item label="Bio" prop="bio">
        <div class="form-field-wrapper">
          <el-input
            v-model="formData.bio"
            type="textarea"
            placeholder="Enter your bio"
            maxlength="500"
            show-word-limit
            :rows="4"
            @blur="validateField('bio')"
          />
          <div v-if="hasFieldError('bio')" class="field-error">
            <el-icon><CircleCloseFilled /></el-icon>
            <span>{{ getFieldError('bio') }}</span>
          </div>
        </div>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="handleSubmit" :loading="isLoading">
          Save Changes
        </el-button>
        <el-button @click="handleCancel">Cancel</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { CircleCloseFilled } from '@element-plus/icons-vue'
import { userService } from '../services/userService'
import { useFormValidation } from '../composables/useFormValidation'

const props = defineProps({
  user: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['update', 'cancel'])

const formRef = ref(null)
const isLoading = ref(false)

const formData = reactive({
  username: '',
  avatarUrl: '',
  bio: ''
})

const rules = {
  username: [
    { min: 1, max: 50, message: 'Username must be between 1 and 50 characters', trigger: 'blur' }
  ],
  avatarUrl: [
    { max: 255, message: 'Avatar URL must not exceed 255 characters', trigger: 'blur' }
  ],
  bio: [
    { max: 500, message: 'Bio must not exceed 500 characters', trigger: 'blur' }
  ]
}

// Use form validation composable
const {
  validateField,
  getFieldError,
  hasFieldError
} = useFormValidation(formData, {
  username: { maxLength: 50 },
  avatarUrl: { type: 'url', maxLength: 255 },
  bio: { maxLength: 500 }
})

// Initialize form data when user prop changes
watch(
  () => props.user,
  (newUser) => {
    if (newUser) {
      formData.username = newUser.username || ''
      formData.avatarUrl = newUser.avatarUrl || ''
      formData.bio = newUser.bio || ''
    }
  },
  { immediate: true }
)

const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    isLoading.value = true

    const response = await userService.updateUser(props.user.id, {
      username: formData.username,
      avatarUrl: formData.avatarUrl,
      bio: formData.bio
    })

    ElMessage.success('Profile updated successfully')
    emit('update', response.data)
  } catch (error) {
    const errorMessage = error.message || 'Failed to update profile'
    ElMessage.error(errorMessage)
  } finally {
    isLoading.value = false
  }
}

const handleCancel = () => {
  emit('cancel')
}
</script>

<style scoped>
.profile-editor {
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
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

.field-hint {
  margin-top: 6px;
  font-size: 12px;
  color: #909399;
  line-height: 1.5;
}
</style>
