/**
 * 格式化日期
 * @param {Date|string} date - 日期对象或字符串
 * @param {string} format - 格式字符串 (默认: 'YYYY-MM-DD HH:mm:ss')
 * @returns {string} 格式化后的日期字符串
 */
export function formatDate(date, format = 'YYYY-MM-DD HH:mm:ss') {
  if (!date) return ''
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hours = String(d.getHours()).padStart(2, '0')
  const minutes = String(d.getMinutes()).padStart(2, '0')
  const seconds = String(d.getSeconds()).padStart(2, '0')

  return format
    .replace('YYYY', year)
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
    .replace('ss', seconds)
}

/**
 * 格式化货币
 * @param {number} value - 数值
 * @param {string} currency - 货币符号 (默认: '¥')
 * @returns {string} 格式化后的货币字符串
 */
export function formatCurrency(value, currency = '¥') {
  if (value === null || value === undefined) return ''
  return `${currency}${parseFloat(value).toFixed(2)}`
}

/**
 * 格式化百分比
 * @param {number} value - 数值 (0-1 或 0-100)
 * @param {number} decimals - 小数位数 (默认: 2)
 * @returns {string} 格式化后的百分比字符串
 */
export function formatPercent(value, decimals = 2) {
  if (value === null || value === undefined) return ''
  const percent = value > 1 ? value : value * 100
  return `${percent.toFixed(decimals)}%`
}

/**
 * 格式化文件大小
 * @param {number} bytes - 字节数
 * @returns {string} 格式化后的文件大小
 */
export function formatFileSize(bytes) {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return Math.round((bytes / Math.pow(k, i)) * 100) / 100 + ' ' + sizes[i]
}

/**
 * 格式化电话号码
 * @param {string} phone - 电话号码
 * @returns {string} 格式化后的电话号码
 */
export function formatPhone(phone) {
  if (!phone) return ''
  const cleaned = phone.replace(/\D/g, '')
  if (cleaned.length === 11) {
    return `${cleaned.slice(0, 3)}-${cleaned.slice(3, 7)}-${cleaned.slice(7)}`
  }
  return phone
}
