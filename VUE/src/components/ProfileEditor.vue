<template>
  <div class="profile-editor">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="120px"
      @submit.prevent="handleSubmit"
    >
      <el-form-item label="用户名" prop="username">
        <div class="form-field-wrapper">
          <el-input
            v-model="formData.username"
            placeholder="请输入用户名"
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

      <el-form-item label="头像地址" prop="avatarUrl">
        <div class="form-field-wrapper">
          <el-input
            v-model="formData.avatarUrl"
            placeholder="请输入头像链接"
            maxlength="255"
            @blur="validateField('avatarUrl')"
          />
          <div v-if="hasFieldError('avatarUrl')" class="field-error">
            <el-icon><CircleCloseFilled /></el-icon>
            <span>{{ getFieldError('avatarUrl') }}</span>
          </div>
          <div v-else-if="formData.avatarUrl" class="field-hint">
            请输入有效的图片链接
          </div>
        </div>
      </el-form-item>

      <el-form-item label="个人简介" prop="bio">
        <div class="form-field-wrapper">
          <el-input
            v-model="formData.bio"
            type="textarea"
            placeholder="介绍一下自己吧"
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
          保存修改
        </el-button>
        <el-button @click="handleCancel">取消</el-button>
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
    { min: 1, max: 50, message: '用户名长度需在 1 到 50 个字符之间', trigger: 'blur' }
  ],
  avatarUrl: [
    { max: 255, message: '头像链接不能超过 255 个字符', trigger: 'blur' }
  ],
  bio: [
    { max: 500, message: '个人简介不能超过 500 个字符', trigger: 'blur' }
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

    ElMessage.success('个人资料更新成功')
    emit('update', response.data)
  } catch (error) {
    const errorMessage = error.message || '更新个人资料失败'
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
