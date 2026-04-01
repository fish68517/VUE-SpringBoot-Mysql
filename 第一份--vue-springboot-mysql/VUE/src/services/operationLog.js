import { userAPI } from './api'

/**
 * Operation logging service for tracking user activities
 */
export const operationLogService = {
  /**
   * Record a view operation
   */
  recordView: async (patternId, patternName) => {
    try {
      const userId = localStorage.getItem('userId')
      if (!userId) return

      await userAPI.recordOperation({
        userId,
        operationType: 'view',
        targetType: 'pattern',
        targetId: patternId,
        details: `浏览纹样: ${patternName}`
      })
    } catch (error) {
      console.error('Failed to record view operation:', error)
    }
  },

  /**
   * Record a search operation
   */
  recordSearch: async (keyword, resultCount) => {
    try {
      const userId = localStorage.getItem('userId')
      if (!userId) return

      await userAPI.recordOperation({
        userId,
        operationType: 'search',
        targetType: 'pattern',
        details: `搜索纹样: ${keyword} (找到 ${resultCount} 个结果)`
      })
    } catch (error) {
      console.error('Failed to record search operation:', error)
    }
  },

  /**
   * Record a download operation
   */
  recordDownload: async (patternId, patternName) => {
    try {
      const userId = localStorage.getItem('userId')
      if (!userId) return

      await userAPI.recordOperation({
        userId,
        operationType: 'download',
        targetType: 'pattern',
        targetId: patternId,
        details: `下载纹样: ${patternName}`
      })
    } catch (error) {
      console.error('Failed to record download operation:', error)
    }
  },

  /**
   * Record a collection operation
   */
  recordCollection: async (patternId, patternName, action = 'add') => {
    try {
      const userId = localStorage.getItem('userId')
      if (!userId) return

      const actionText = action === 'add' ? '收藏' : '取消收藏'
      await userAPI.recordOperation({
        userId,
        operationType: 'collection',
        targetType: 'pattern',
        targetId: patternId,
        details: `${actionText}纹样: ${patternName}`
      })
    } catch (error) {
      console.error('Failed to record collection operation:', error)
    }
  },

  /**
   * Record a comment operation
   */
  recordComment: async (patternId, patternName) => {
    try {
      const userId = localStorage.getItem('userId')
      if (!userId) return

      await userAPI.recordOperation({
        userId,
        operationType: 'comment',
        targetType: 'pattern',
        targetId: patternId,
        details: `在纹样 ${patternName} 上发布评论`
      })
    } catch (error) {
      console.error('Failed to record comment operation:', error)
    }
  },

  /**
   * Record a question operation
   */
  recordQuestion: async (patternId, patternName) => {
    try {
      const userId = localStorage.getItem('userId')
      if (!userId) return

      await userAPI.recordOperation({
        userId,
        operationType: 'question',
        targetType: 'pattern',
        targetId: patternId,
        details: `在纹样 ${patternName} 上发布提问`
      })
    } catch (error) {
      console.error('Failed to record question operation:', error)
    }
  }
}

export default operationLogService
