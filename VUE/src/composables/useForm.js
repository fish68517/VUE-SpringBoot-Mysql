import { ref, reactive } from 'vue'

/**
 * 表单管理的组合式函数
 */
export function useForm(initialValues = {}) {
  const formData = reactive({ ...initialValues })
  const errors = reactive({})
  const touched = reactive({})
  const isSubmitting = ref(false)

  /**
   * 重置表单
   */
  function resetForm() {
    Object.keys(formData).forEach((key) => {
      formData[key] = initialValues[key] || ''
    })
    Object.keys(errors).forEach((key) => {
      delete errors[key]
    })
    Object.keys(touched).forEach((key) => {
      delete touched[key]
    })
  }

  /**
   * 设置字段值
   * @param {string} field - 字段名
   * @param {any} value - 字段值
   */
  function setFieldValue(field, value) {
    formData[field] = value
  }

  /**
   * 设置字段错误
   * @param {string} field - 字段名
   * @param {string} error - 错误信息
   */
  function setFieldError(field, error) {
    if (error) {
      errors[field] = error
    } else {
      delete errors[field]
    }
  }

  /**
   * 标记字段为已触碰
   * @param {string} field - 字段名
   */
  function setFieldTouched(field) {
    touched[field] = true
  }

  /**
   * 获取字段值
   * @param {string} field - 字段名
   * @returns {any} 字段值
   */
  function getFieldValue(field) {
    return formData[field]
  }

  /**
   * 获取字段错误
   * @param {string} field - 字段名
   * @returns {string} 错误信息
   */
  function getFieldError(field) {
    return errors[field] || ''
  }

  /**
   * 检查字段是否有错误
   * @param {string} field - 字段名
   * @returns {boolean} 是否有错误
   */
  function hasError(field) {
    return !!errors[field]
  }

  /**
   * 检查字段是否已触碰
   * @param {string} field - 字段名
   * @returns {boolean} 是否已触碰
   */
  function isTouched(field) {
    return !!touched[field]
  }

  /**
   * 检查表单是否有效
   * @returns {boolean} 是否有效
   */
  function isValid() {
    return Object.keys(errors).length === 0
  }

  /**
   * 获取表单数据
   * @returns {object} 表单数据
   */
  function getFormData() {
    return { ...formData }
  }

  /**
   * 设置提交状态
   * @param {boolean} status - 提交状态
   */
  function setSubmitting(status) {
    isSubmitting.value = status
  }

  return {
    formData,
    errors,
    touched,
    isSubmitting,
    resetForm,
    setFieldValue,
    setFieldError,
    setFieldTouched,
    getFieldValue,
    getFieldError,
    hasError,
    isTouched,
    isValid,
    getFormData,
    setSubmitting,
  }
}
