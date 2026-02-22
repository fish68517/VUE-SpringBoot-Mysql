import { ElMessage, ElMessageBox } from 'element-plus'

/**
 * 通知和消息提示的组合式函数
 */

/**
 * 显示成功消息
 * @param {string} message - 消息内容
 * @param {number} duration - 显示时长 (毫秒)
 */
export function useSuccessMessage(message, duration = 3000) {
  ElMessage.success({
    message,
    duration,
  })
}

/**
 * 显示错误消息
 * @param {string} message - 消息内容
 * @param {number} duration - 显示时长 (毫秒)
 */
export function useErrorMessage(message, duration = 3000) {
  ElMessage.error({
    message,
    duration,
  })
}

/**
 * 显示警告消息
 * @param {string} message - 消息内容
 * @param {number} duration - 显示时长 (毫秒)
 */
export function useWarningMessage(message, duration = 3000) {
  ElMessage.warning({
    message,
    duration,
  })
}

/**
 * 显示信息消息
 * @param {string} message - 消息内容
 * @param {number} duration - 显示时长 (毫秒)
 */
export function useInfoMessage(message, duration = 3000) {
  ElMessage.info({
    message,
    duration,
  })
}

/**
 * 显示确认对话框
 * @param {string} title - 标题
 * @param {string} message - 消息内容
 * @param {object} options - 其他选项
 * @returns {Promise} 确认结果
 */
export function useConfirm(title, message, options = {}) {
  return ElMessageBox.confirm(message, title, {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
    ...options,
  })
}

/**
 * 显示删除确认对话框
 * @param {string} itemName - 项目名称
 * @returns {Promise} 确认结果
 */
export function useDeleteConfirm(itemName = '该项目') {
  return useConfirm('删除确认', `确定要删除${itemName}吗？此操作不可撤销。`, {
    type: 'warning',
  })
}

/**
 * 显示加载消息
 * @param {string} message - 消息内容
 * @returns {function} 关闭函数
 */
export function useLoadingMessage(message = '加载中...') {
  const instance = ElMessage.loading({
    message,
    duration: 0,
  })
  return () => instance.close()
}
