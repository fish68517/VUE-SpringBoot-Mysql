/**
 * 本地存储工具类
 */

/**
 * 设置本地存储
 * @param {string} key - 键
 * @param {any} value - 值
 */
export function setStorage(key, value) {
  try {
    localStorage.setItem(key, JSON.stringify(value))
  } catch (error) {
    console.error('Failed to set storage:', error)
  }
}

/**
 * 获取本地存储
 * @param {string} key - 键
 * @param {any} defaultValue - 默认值
 * @returns {any} 存储的值
 */
export function getStorage(key, defaultValue = null) {
  try {
    const value = localStorage.getItem(key)
    return value ? JSON.parse(value) : defaultValue
  } catch (error) {
    console.error('Failed to get storage:', error)
    return defaultValue
  }
}

/**
 * 删除本地存储
 * @param {string} key - 键
 */
export function removeStorage(key) {
  try {
    localStorage.removeItem(key)
  } catch (error) {
    console.error('Failed to remove storage:', error)
  }
}

/**
 * 清空本地存储
 */
export function clearStorage() {
  try {
    localStorage.clear()
  } catch (error) {
    console.error('Failed to clear storage:', error)
  }
}

/**
 * 会话存储工具
 */

/**
 * 设置会话存储
 * @param {string} key - 键
 * @param {any} value - 值
 */
export function setSessionStorage(key, value) {
  try {
    sessionStorage.setItem(key, JSON.stringify(value))
  } catch (error) {
    console.error('Failed to set session storage:', error)
  }
}

/**
 * 获取会话存储
 * @param {string} key - 键
 * @param {any} defaultValue - 默认值
 * @returns {any} 存储的值
 */
export function getSessionStorage(key, defaultValue = null) {
  try {
    const value = sessionStorage.getItem(key)
    return value ? JSON.parse(value) : defaultValue
  } catch (error) {
    console.error('Failed to get session storage:', error)
    return defaultValue
  }
}

/**
 * 删除会话存储
 * @param {string} key - 键
 */
export function removeSessionStorage(key) {
  try {
    sessionStorage.removeItem(key)
  } catch (error) {
    console.error('Failed to remove session storage:', error)
  }
}

/**
 * 清空会话存储
 */
export function clearSessionStorage() {
  try {
    sessionStorage.clear()
  } catch (error) {
    console.error('Failed to clear session storage:', error)
  }
}
