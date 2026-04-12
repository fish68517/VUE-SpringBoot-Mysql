<template>
  <div class="form-example">
    <h2>Form Validation Example</h2>
    
    <el-form
      ref="formRef"
      :model="formData"
      :rules="validationRules"
      label-width="120px"
      @submit.prevent="handleSubmit"
    >
      <el-form-item label="Username" prop="username">
        <el-input
          v-model="formData.username"
          placeholder="Enter username"
          clearable
          @blur="validateField('username')"
        />
      </el-form-item>

      <el-form-item label="Email" prop="email">
        <el-input
          v-model="formData.email"
          type="email"
          placeholder="Enter email"
          clearable
          @blur="validateField('email')"
        />
      </el-form-item>

      <el-form-item label="Password" prop="password">
        <el-input
          v-model="formData.password"
          type="password"
          placeholder="Enter password"
          show-password
          @blur="validateField('password')"
        />
      </el-form-item>

      <el-form-item label="Confirm Password" prop="confirmPassword">
        <el-input
          v-model="formData.confirmPassword"
          type="password"
          placeholder="Confirm password"
          show-password
          @blur="validateField('confirmPassword')"
        />
      </el-form-item>

      <el-form-item>
        <el-button
          type="primary"
          @click="handleSubmit"
          :loading="isSubmitting"
          style="width: 100%"
        >
          Submit
        </el-button>
      </el-form-item>
    </el-form>

    <!-- Loading spinner example -->
    <LoadingSpinner v-if="isLoading" message="Processing..." />

    <!-- Skeleton loader example -->
    <SkeletonLoader v-if="showSkeleton" :count="3" type="card" />
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useFormValidation } from '../composables/useFormValidation'
import { useLoading } from '../composables/useLoading'
import { showSuccess, showError } from '../utils/notificationUtils'
import LoadingSpinner from './LoadingSpinner.vue'
import SkeletonLoader from './SkeletonLoader.vue'

const formRef = ref(null)
const { isLoading, startLoading, stopLoading } = useLoading()
const showSkeleton = ref(false)

const formData = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
})

const validationRules = {
  username: [
    { required: true, message: 'Username is required', trigger: 'blur' },
    { min: 3, max: 50, message: 'Username must be between 3 and 50 characters', trigger: 'blur' }
  ],
  email: [
    { required: true, message: 'Email is required', trigger: 'blur' },
    { type: 'email', message: 'Invalid email format', trigger: 'blur' }
  ],
  password: [
    { required: true, message: 'Password is required', trigger: 'blur' },
    { min: 8, max: 100, message: 'Password must be between 8 and 100 characters', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: 'Please confirm your password', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== formData.password) {
          callback(new Error('Passwords do not match'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const validateField = (fieldName) => {
  if (formRef.value) {
    formRef.value.validateField(fieldName)
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    startLoading()
    
    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    showSuccess('Form submitted successfully!')
    stopLoading()
  } catch (error) {
    showError('Please fix the form errors')
  }
}
</script>

<style scoped>
.form-example {
  max-width: 400px;
  margin: 0 auto;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.form-example h2 {
  text-align: center;
  margin-bottom: 20px;
  color: #333;
}
</style>
