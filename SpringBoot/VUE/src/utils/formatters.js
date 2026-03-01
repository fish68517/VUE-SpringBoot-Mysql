// 数据格式化工具函数

// 格式化日期（YYYY-MM-DD HH:mm:ss）
export const formatDateTime = (date) => {
  if (!date) return ''
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hours = String(d.getHours()).padStart(2, '0')
  const minutes = String(d.getMinutes()).padStart(2, '0')
  const seconds = String(d.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

// 格式化日期（YYYY-MM-DD）
export const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

// 格式化时间（HH:mm:ss）
export const formatTime = (date) => {
  if (!date) return ''
  const d = new Date(date)
  const hours = String(d.getHours()).padStart(2, '0')
  const minutes = String(d.getMinutes()).padStart(2, '0')
  const seconds = String(d.getSeconds()).padStart(2, '0')
  return `${hours}:${minutes}:${seconds}`
}

// 格式化数字（保留指定小数位）
export const formatNumber = (num, decimals = 2) => {
  if (num === null || num === undefined) return ''
  return parseFloat(num).toFixed(decimals)
}

// 格式化性别
export const formatGender = (gender) => {
  const genderMap = {
    'M': '男',
    'F': '女',
    'O': '其他',
    'MALE': '男',
    'FEMALE': '女',
    'OTHER': '其他'
  }
  return genderMap[gender] || gender
}

// 格式化用户角色
export const formatRole = (role) => {
  const roleMap = {
    'USER': '普通用户',
    'DOCTOR': '医师',
    'ADMIN': '管理员'
  }
  return roleMap[role] || role
}

// 格式化状态
export const formatStatus = (status) => {
  const statusMap = {
    'ACTIVE': '活跃',
    'INACTIVE': '禁用',
    'PENDING': '待处理',
    'COMPLETED': '已完成',
    'ANSWERED': '已回答'
  }
  return statusMap[status] || status
}

// 格式化血压（收缩压/舒张压）
export const formatBloodPressure = (systolic, diastolic) => {
  if (!systolic || !diastolic) return ''
  return `${systolic}/${diastolic}`
}

// 格式化字节大小
export const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return Math.round((bytes / Math.pow(k, i)) * 100) / 100 + ' ' + sizes[i]
}

// 截断文本
export const truncateText = (text, length = 50) => {
  if (!text) return ''
  return text.length > length ? text.substring(0, length) + '...' : text
}

export default {
  formatDateTime,
  formatDate,
  formatTime,
  formatNumber,
  formatGender,
  formatRole,
  formatStatus,
  formatBloodPressure,
  formatFileSize,
  truncateText
}
