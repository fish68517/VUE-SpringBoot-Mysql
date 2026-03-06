import { ref } from 'vue'
import { ElMessage } from 'element-plus'

/**
 * Composable for managing loading states and operation feedback
 */
export function useLoading() {
  const loading = ref(false)
  const loadingText = ref('加载中...')

  /**
   * Start loading with optional text
   */
  const startLoading = (text = '加载中...') => {
    loading.value = true
    loadingText.value = text
  }

  /**
   * Stop loading
   */
  const stopLoading = () => {
    loading.value = false
    loadingText.value = '加载中...'
  }

  /**
   * Execute async operation with loading state
   */
  const executeWithLoading = async (asyncFn, options = {}) => {
    const {
      loadingMessage = '处理中...',
      successMessage = '操作成功',
      errorMessage = '操作失败',
      showSuccess = true,
      showError = true
    } = options

    startLoading(loadingMessage)

    try {
      const result = await asyncFn()
      
      if (showSuccess && successMessage) {
        ElMessage.success({
          message: successMessage,
          duration: 2000,
          showClose: true
        })
      }

      return {
        success: true,
        data: result,
        error: null
      }
    } catch (error) {
      const message = error.message || errorMessage

      if (showError && message) {
        ElMessage.error({
          message,
          duration: 3000,
          showClose: true
        })
      }

      return {
        success: false,
        data: null,
        error: error
      }
    } finally {
      stopLoading()
    }
  }

  /**
   * Show success message
   */
  const showSuccess = (message = '操作成功', duration = 2000) => {
    ElMessage.success({
      message,
      duration,
      showClose: true
    })
  }

  /**
   * Show error message
   */
  const showError = (message = '操作失败', duration = 3000) => {
    ElMessage.error({
      message,
      duration,
      showClose: true
    })
  }

  /**
   * Show warning message
   */
  const showWarning = (message = '警告', duration = 3000) => {
    ElMessage.warning({
      message,
      duration,
      showClose: true
    })
  }

  /**
   * Show info message
   */
  const showInfo = (message = '提示', duration = 2000) => {
    ElMessage.info({
      message,
      duration,
      showClose: true
    })
  }

  return {
    loading,
    loadingText,
    startLoading,
    stopLoading,
    executeWithLoading,
    showSuccess,
    showError,
    showWarning,
    showInfo
  }
}
