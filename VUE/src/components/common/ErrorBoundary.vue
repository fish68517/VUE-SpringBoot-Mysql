<template>
  <div v-if="hasError" class="error-boundary">
    <el-result
      icon="error"
      title="Oops! Something went wrong"
      :sub-title="errorMessage"
    >
      <template #extra>
        <el-button type="primary" @click="handleReset">Try Again</el-button>
        <el-button @click="handleGoHome">Go Home</el-button>
      </template>
    </el-result>
  </div>
  <slot v-else></slot>
</template>

<script setup>
import { ref, onErrorCaptured } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getUserInfo } from '@/utils/auth'

const router = useRouter()
const hasError = ref(false)
const errorMessage = ref('')

// Capture errors from child components
onErrorCaptured((err, instance, info) => {
  console.error('Error captured by ErrorBoundary:', err, info)
  
  hasError.value = true
  errorMessage.value = err.message || 'An unexpected error occurred. Please try again.'
  
  // Log error details for debugging
  if (import.meta.env.DEV) {
    console.error('Component:', instance)
    console.error('Error info:', info)
  }
  
  // Show error notification
  ElMessage.error({
    message: 'An error occurred while rendering the component',
    duration: 3000
  })
  
  // Prevent error from propagating further
  return false
})

const handleReset = () => {
  hasError.value = false
  errorMessage.value = ''
  // Reload the current route
  router.go(0)
}

const handleGoHome = () => {
  hasError.value = false
  errorMessage.value = ''
  
  const userInfo = getUserInfo()
  if (userInfo) {
    const role = userInfo.role
    if (role === 'admin') {
      router.push('/admin/dashboard')
    } else if (role === 'coach') {
      router.push('/coach/dashboard')
    } else {
      router.push('/home')
    }
  } else {
    router.push('/login')
  }
}
</script>

<style scoped>
.error-boundary {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
  padding: 20px;
}

@media (max-width: 768px) {
  .error-boundary {
    min-height: 300px;
    padding: 10px;
  }
}
</style>
