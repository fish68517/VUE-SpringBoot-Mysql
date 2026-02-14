import { ref } from 'vue'

let toastInstance = null

export const useToast = () => {
  const setToastInstance = (instance) => {
    toastInstance = instance
  }

  const success = (message, options = {}) => {
    if (toastInstance) {
      return toastInstance.success(message, options)
    }
  }

  const error = (message, options = {}) => {
    if (toastInstance) {
      return toastInstance.error(message, options)
    }
  }

  const warning = (message, options = {}) => {
    if (toastInstance) {
      return toastInstance.warning(message, options)
    }
  }

  const info = (message, options = {}) => {
    if (toastInstance) {
      return toastInstance.info(message, options)
    }
  }

  return {
    setToastInstance,
    success,
    error,
    warning,
    info
  }
}
