import { ref } from 'vue'
import { ElMessage } from 'element-plus'

/**
 * Composable for enhanced form validation with better error messages
 */
export function useFormValidation() {
  const validationRules = {
    username: [
      {
        required: true,
        message: '用户名不能为空',
        trigger: 'blur'
      },
      {
        min: 3,
        max: 20,
        message: '用户名长度应为3-20个字符',
        trigger: 'blur'
      },
      {
        pattern: /^[a-zA-Z0-9_-]+$/,
        message: '用户名只能包含字母、数字、下划线和连字符',
        trigger: 'blur'
      }
    ],
    email: [
      {
        required: true,
        message: '邮箱不能为空',
        trigger: 'blur'
      },
      {
        type: 'email',
        message: '请输入有效的邮箱地址',
        trigger: 'blur'
      }
    ],
    phone: [
      {
        pattern: /^1[3-9]\d{9}$/,
        message: '请输入有效的手机号码',
        trigger: 'blur'
      }
    ],
    password: [
      {
        required: true,
        message: '密码不能为空',
        trigger: 'blur'
      },
      {
        min: 6,
        max: 20,
        message: '密码长度应为6-20个字符',
        trigger: 'blur'
      }
    ],
    confirmPassword: [
      {
        required: true,
        message: '确认密码不能为空',
        trigger: 'blur'
      }
    ],
    title: [
      {
        required: true,
        message: '标题不能为空',
        trigger: 'blur'
      },
      {
        min: 5,
        max: 200,
        message: '标题长度应为5-200个字符',
        trigger: 'blur'
      }
    ],
    abstract: [
      {
        required: true,
        message: '摘要不能为空',
        trigger: 'blur'
      },
      {
        min: 50,
        max: 500,
        message: '摘要长度应为50-500个字符',
        trigger: 'blur'
      }
    ],
    content: [
      {
        required: true,
        message: '内容不能为空',
        trigger: 'blur'
      }
    ]
  }

  /**
   * Validate form and show error message
   */
  const validateForm = async (formRef) => {
    if (!formRef) {
      ElMessage.error('表单引用不存在')
      return false
    }

    try {
      await formRef.validate()
      return true
    } catch (error) {
      // Error message is shown by Element Plus automatically
      return false
    }
  }

  /**
   * Clear form validation errors
   */
  const clearValidation = (formRef) => {
    if (formRef) {
      formRef.clearValidate()
    }
  }

  /**
   * Reset form
   */
  const resetForm = (formRef) => {
    if (formRef) {
      formRef.resetFields()
    }
  }

  /**
   * Show validation error message
   */
  const showValidationError = (message) => {
    ElMessage.error({
      message,
      duration: 3000,
      showClose: true
    })
  }

  /**
   * Show validation success message
   */
  const showValidationSuccess = (message) => {
    ElMessage.success({
      message,
      duration: 2000,
      showClose: true
    })
  }

  return {
    validationRules,
    validateForm,
    clearValidation,
    resetForm,
    showValidationError,
    showValidationSuccess
  }
}
