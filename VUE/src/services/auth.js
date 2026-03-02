/**
 * 认证服务 - 管理用户认证状态和信息存储
 * 
 * 功能：
 * - 用户登录状态管理
 * - 用户信息存储和检索
 * - 登出功能
 * - 本地存储管理（localStorage）
 */

// 存储键常量
const STORAGE_KEYS = {
  TOKEN: 'token',
  USER: 'user',
  ROLE: 'role',
  USER_ID: 'userId'
}

/**
 * 认证服务对象
 * 提供认证状态管理的核心功能
 */
export const authService = {
  /**
   * 设置认证信息
   * @param {string} token - 认证令牌
   * @param {object} user - 用户信息对象
   * @param {string} role - 用户角色 (USER, DOCTOR, ADMIN)
   */
  setAuth(token, user, role) {
    // 保存令牌到 localStorage
    localStorage.setItem(STORAGE_KEYS.TOKEN, token)
    
    // 保存用户信息到 localStorage
    localStorage.setItem(STORAGE_KEYS.USER, JSON.stringify(user))
    
    // 保存用户角色到 localStorage
    localStorage.setItem(STORAGE_KEYS.ROLE, role)
    
    // 保存用户ID到 localStorage
    if (user && user.id) {
      localStorage.setItem(STORAGE_KEYS.USER_ID, user.id)
    }
  },

  /**
   * 获取认证状态
   * @returns {object} 包含认证状态的对象
   */
  getAuthState() {
    const token = localStorage.getItem(STORAGE_KEYS.TOKEN)
    const userJson = localStorage.getItem(STORAGE_KEYS.USER)
    const role = localStorage.getItem(STORAGE_KEYS.ROLE)
    const userId = localStorage.getItem(STORAGE_KEYS.USER_ID)

    return {
      isAuthenticated: !!token,
      token: token || null,
      user: userJson ? JSON.parse(userJson) : null,
      role: role || null,
      userId: userId ? parseInt(userId) : null
    }
  },

  /**
   * 获取当前用户信息
   * @returns {object|null} 用户信息对象或 null
   */
  getCurrentUser() {
    const userJson = localStorage.getItem(STORAGE_KEYS.USER)
    return userJson ? JSON.parse(userJson) : null
  },

  /**
   * 获取当前用户信息（别名方法，用于兼容现有代码）
   * @returns {object|null} 用户信息对象或 null
   */
  getUser() {
    return this.getCurrentUser()
  },

  /**
   * 获取当前用户ID
   * @returns {number|null} 用户ID或 null
   */
  getCurrentUserId() {
    const userId = localStorage.getItem(STORAGE_KEYS.USER_ID)
    return userId ? parseInt(userId) : null
  },

  /**
   * 获取当前用户角色
   * @returns {string|null} 用户角色或 null
   */
  getCurrentRole() {
    return localStorage.getItem(STORAGE_KEYS.ROLE)
  },

  /**
   * 获取当前用户角色（别名方法，用于兼容现有代码）
   * @returns {string|null} 用户角色或 null
   */
  getUserRole() {
    return this.getCurrentRole()
  },

  /**
   * 获取认证令牌
   * @returns {string|null} 认证令牌或 null
   */
  getToken() {
    return localStorage.getItem(STORAGE_KEYS.TOKEN)
  },

  /**
   * 检查用户是否已认证
   * @returns {boolean} 是否已认证
   */
  isAuthenticated() {
    return !!localStorage.getItem(STORAGE_KEYS.TOKEN)
  },

  /**
   * 检查用户是否具有特定角色
   * @param {string} role - 要检查的角色
   * @returns {boolean} 用户是否具有该角色
   */
  hasRole(role) {
    return localStorage.getItem(STORAGE_KEYS.ROLE) === role
  },

  /**
   * 更新用户信息
   * @param {object} updatedUser - 更新后的用户信息
   */
  updateUser(updatedUser) {
    localStorage.setItem(STORAGE_KEYS.USER, JSON.stringify(updatedUser))
    
    // 如果用户ID有变化，也更新用户ID
    if (updatedUser && updatedUser.id) {
      localStorage.setItem(STORAGE_KEYS.USER_ID, updatedUser.id)
    }
  },

  /**
   * 登出 - 清除所有认证信息
   */
  logout() {
    // 清除所有存储的认证信息
    localStorage.removeItem(STORAGE_KEYS.TOKEN)
    localStorage.removeItem(STORAGE_KEYS.USER)
    localStorage.removeItem(STORAGE_KEYS.ROLE)
    localStorage.removeItem(STORAGE_KEYS.USER_ID)
  },

  /**
   * 清除认证信息（用于401错误处理）
   */
  clearAuth() {
    this.logout()
  }
}

export default authService
