import client from './client'

export const browsingHistoryApi = {
  /**
   * 记录浏览历史
   * @param {number} userId 用户ID
   * @param {string} targetType 目标类型（attraction、hotel、product）
   * @param {number} targetId 目标ID
   * @returns {Promise}
   */
  recordBrowsingHistory(userId, targetType, targetId) {
    return client.post('/browsing-history/record', null, {
      params: {
        userId,
        targetType,
        targetId
      }
    })
  },

  /**
   * 获取用户浏览历史（分页）
   * @param {number} userId 用户ID
   * @param {number} page 页码（从0开始）
   * @param {number} size 每页数量
   * @returns {Promise}
   */
  getBrowsingHistory(userId, page = 0, size = 10) {
    return client.get(`/browsing-history/user/${userId}`, {
      params: {
        page,
        size
      }
    })
  },

  /**
   * 获取用户指定类型的浏览历史
   * @param {number} userId 用户ID
   * @param {string} targetType 目标类型
   * @returns {Promise}
   */
  getBrowsingHistoryByType(userId, targetType) {
    return client.get(`/browsing-history/user/${userId}/type/${targetType}`)
  },

  /**
   * 获取用户所有浏览历史
   * @param {number} userId 用户ID
   * @returns {Promise}
   */
  getAllBrowsingHistory(userId) {
    return client.get(`/browsing-history/user/${userId}/all`)
  },

  /**
   * 统计用户浏览历史数量
   * @param {number} userId 用户ID
   * @returns {Promise}
   */
  countBrowsingHistory(userId) {
    return client.get(`/browsing-history/user/${userId}/count`)
  },

  /**
   * 统计用户指定类型的浏览历史数量
   * @param {number} userId 用户ID
   * @param {string} targetType 目标类型
   * @returns {Promise}
   */
  countBrowsingHistoryByType(userId, targetType) {
    return client.get(`/browsing-history/user/${userId}/type/${targetType}/count`)
  },

  /**
   * 删除用户指定类型的浏览历史
   * @param {number} userId 用户ID
   * @param {string} targetType 目标类型
   * @returns {Promise}
   */
  deleteByType(userId, targetType) {
    return client.delete(`/browsing-history/user/${userId}/type/${targetType}`)
  },

  /**
   * 删除用户所有浏览历史
   * @param {number} userId 用户ID
   * @returns {Promise}
   */
  deleteAll(userId) {
    return client.delete(`/browsing-history/user/${userId}/all`)
  }
}
