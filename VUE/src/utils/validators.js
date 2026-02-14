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
 * 验证用户名格式
 * @param {string} username - 用户名
 * @returns {boolean} 是否有效
 */
export function isValidUsername(username) {
  // 用户名长度 3-20，只能包含字母、数字、下划线
  const usernameRegex = /^[a-zA-Z0-9_]{3,20}$/
  return usernameRegex.test(username)
}

/**
 * 验证密码强度
 * @param {string} password - 密码
 * @returns {boolean} 是否有效
 */
export function isValidPassword(password) {
  // 密码长度至少 6 位
  return password && password.length >= 6
}

/**
 * 验证手机号
 * @param {string} phone - 手机号
 * @returns {boolean} 是否有效
 */
export function isValidPhone(phone) {
  const phoneRegex = /^1[3-9]\d{9}$/
  return phoneRegex.test(phone)
}

/**
 * 验证 URL 格式
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
 * 验证非空
 * @param {any} value - 值
 * @returns {boolean} 是否非空
 */
export function isNotEmpty(value) {
  return value !== null && value !== undefined && value !== ''
}
