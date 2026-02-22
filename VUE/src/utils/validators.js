/**
 * 验证邮箱格式
 * @param {string} email - 邮箱地址
 * @returns {boolean} 是否有效
 */
export function isValidEmail(email) {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return emailRegex.test(email)
}

/**
 * 验证电话号码格式
 * @param {string} phone - 电话号码
 * @returns {boolean} 是否有效
 */
export function isValidPhone(phone) {
  const phoneRegex = /^1[3-9]\d{9}$/
  return phoneRegex.test(phone)
}

/**
 * 验证密码强度
 * @param {string} password - 密码
 * @returns {object} 验证结果 { isValid, strength, message }
 */
export function validatePassword(password) {
  if (!password) {
    return { isValid: false, strength: 0, message: '密码不能为空' }
  }

  if (password.length < 6) {
    return { isValid: false, strength: 1, message: '密码长度至少6位' }
  }

  let strength = 1
  if (/[a-z]/.test(password)) strength++
  if (/[A-Z]/.test(password)) strength++
  if (/\d/.test(password)) strength++
  if (/[!@#$%^&*]/.test(password)) strength++

  const isValid = strength >= 2
  const strengthText = ['弱', '弱', '中', '强', '非常强'][strength]

  return {
    isValid,
    strength,
    message: isValid ? `密码强度: ${strengthText}` : '密码强度过弱',
  }
}

/**
 * 验证用户名格式
 * @param {string} username - 用户名
 * @returns {boolean} 是否有效
 */
export function isValidUsername(username) {
  const usernameRegex = /^[a-zA-Z0-9_]{3,20}$/
  return usernameRegex.test(username)
}

/**
 * 验证URL格式
 * @param {string} url - URL
 * @returns {boolean} 是否有效
 */
export function isValidUrl(url) {
  try {
    new URL(url)
    return true
  } catch {
    return false
  }
}

/**
 * 验证数字范围
 * @param {number} value - 数值
 * @param {number} min - 最小值
 * @param {number} max - 最大值
 * @returns {boolean} 是否在范围内
 */
export function isInRange(value, min, max) {
  return value >= min && value <= max
}

/**
 * 验证必填字段
 * @param {any} value - 值
 * @returns {boolean} 是否为空
 */
export function isRequired(value) {
  if (value === null || value === undefined) return false
  if (typeof value === 'string') return value.trim().length > 0
  if (Array.isArray(value)) return value.length > 0
  return true
}
