/**
 * API响应验证工具
 * 用于验证后端API响应的数据一致性
 */

/**
 * 验证登录响应
 * @param {Object} response - 登录响应对象
 * @returns {boolean} 验证是否通过
 */
export function validateLoginResponse(response) {
  if (!response) return false
  
  const requiredFields = ['token', 'userId', 'username', 'userType']
  return requiredFields.every(field => field in response)
}

/**
 * 验证用户信息响应
 * @param {Object} user - 用户对象
 * @returns {boolean} 验证是否通过
 */
export function validateUserResponse(user) {
  if (!user) return false
  
  const requiredFields = ['id', 'username', 'email', 'userType']
  return requiredFields.every(field => field in user)
}

/**
 * 验证气象数据响应
 * @param {Object} weather - 气象数据对象
 * @returns {boolean} 验证是否通过
 */
export function validateWeatherResponse(weather) {
  if (!weather) return false
  
  const requiredFields = ['region', 'temperature', 'humidity']
  return requiredFields.every(field => field in weather)
}

/**
 * 验证预警响应
 * @param {Object} warning - 预警对象
 * @returns {boolean} 验证是否通过
 */
export function validateWarningResponse(warning) {
  if (!warning) return false
  
  const requiredFields = ['id', 'warningType', 'region', 'severity', 'status']
  return requiredFields.every(field => field in warning)
}

/**
 * 验证产品响应
 * @param {Object} product - 产品对象
 * @returns {boolean} 验证是否通过
 */
export function validateProductResponse(product) {
  if (!product) return false
  
  const requiredFields = ['id', 'productName', 'category', 'price', 'stock']
  return requiredFields.every(field => field in product)
}

/**
 * 验证订单响应
 * @param {Object} order - 订单对象
 * @returns {boolean} 验证是否通过
 */
export function validateOrderResponse(order) {
  if (!order) return false
  
  const requiredFields = ['id', 'orderNumber', 'userId', 'productId', 'quantity', 'totalPrice', 'status']
  return requiredFields.every(field => field in order)
}

/**
 * 验证推荐响应
 * @param {Object} recommendation - 推荐对象
 * @returns {boolean} 验证是否通过
 */
export function validateRecommendationResponse(recommendation) {
  if (!recommendation) return false
  
  const requiredFields = ['id', 'warningId', 'productId', 'userId', 'status']
  return requiredFields.every(field => field in recommendation)
}

/**
 * 验证列表响应
 * @param {Array} data - 列表数据
 * @returns {boolean} 验证是否通过
 */
export function validateListResponse(data) {
  return Array.isArray(data)
}

/**
 * 验证分析数据响应
 * @param {Object} stats - 统计数据对象
 * @returns {boolean} 验证是否通过
 */
export function validateAnalyticsResponse(stats) {
  if (!stats) return false
  
  // 分析数据可以是任何对象，只需验证不为空
  return Object.keys(stats).length > 0
}

/**
 * 验证响应数据类型
 * @param {*} data - 数据
 * @param {string} expectedType - 期望的类型 ('object', 'array', 'string', 'number', 'boolean')
 * @returns {boolean} 验证是否通过
 */
export function validateDataType(data, expectedType) {
  if (expectedType === 'array') {
    return Array.isArray(data)
  }
  
  if (expectedType === 'object') {
    return data !== null && typeof data === 'object' && !Array.isArray(data)
  }
  
  return typeof data === expectedType
}

/**
 * 验证数值范围
 * @param {number} value - 数值
 * @param {number} min - 最小值
 * @param {number} max - 最大值
 * @returns {boolean} 验证是否通过
 */
export function validateNumberRange(value, min, max) {
  return typeof value === 'number' && value >= min && value <= max
}

/**
 * 验证字符串长度
 * @param {string} str - 字符串
 * @param {number} minLength - 最小长度
 * @param {number} maxLength - 最大长度
 * @returns {boolean} 验证是否通过
 */
export function validateStringLength(str, minLength, maxLength) {
  if (typeof str !== 'string') return false
  return str.length >= minLength && str.length <= maxLength
}

/**
 * 验证邮箱格式
 * @param {string} email - 邮箱
 * @returns {boolean} 验证是否通过
 */
export function validateEmail(email) {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return emailRegex.test(email)
}

/**
 * 验证手机号格式
 * @param {string} phone - 手机号
 * @returns {boolean} 验证是否通过
 */
export function validatePhone(phone) {
  const phoneRegex = /^1[3-9]\d{9}$/
  return phoneRegex.test(phone)
}

/**
 * 验证日期格式
 * @param {string} dateStr - 日期字符串
 * @returns {boolean} 验证是否通过
 */
export function validateDateFormat(dateStr) {
  const date = new Date(dateStr)
  return date instanceof Date && !isNaN(date)
}

/**
 * 验证完整的API响应
 * @param {Object} response - 完整的API响应
 * @param {string} type - 响应类型
 * @returns {Object} 验证结果 { valid: boolean, errors: string[] }
 */
export function validateAPIResponse(response, type) {
  const errors = []
  
  // 验证基本结构
  if (!response) {
    errors.push('响应为空')
    return { valid: false, errors }
  }
  
  // 验证特定类型
  let isValid = true
  switch (type) {
    case 'login':
      isValid = validateLoginResponse(response)
      if (!isValid) errors.push('登录响应缺少必要字段')
      break
    case 'user':
      isValid = validateUserResponse(response)
      if (!isValid) errors.push('用户响应缺少必要字段')
      break
    case 'weather':
      isValid = validateWeatherResponse(response)
      if (!isValid) errors.push('气象数据响应缺少必要字段')
      break
    case 'warning':
      isValid = validateWarningResponse(response)
      if (!isValid) errors.push('预警响应缺少必要字段')
      break
    case 'product':
      isValid = validateProductResponse(response)
      if (!isValid) errors.push('产品响应缺少必要字段')
      break
    case 'order':
      isValid = validateOrderResponse(response)
      if (!isValid) errors.push('订单响应缺少必要字段')
      break
    case 'recommendation':
      isValid = validateRecommendationResponse(response)
      if (!isValid) errors.push('推荐响应缺少必要字段')
      break
    case 'list':
      isValid = validateListResponse(response)
      if (!isValid) errors.push('列表响应不是数组')
      break
    case 'analytics':
      isValid = validateAnalyticsResponse(response)
      if (!isValid) errors.push('分析数据响应为空')
      break
    default:
      errors.push(`未知的响应类型: ${type}`)
  }
  
  return { valid: isValid && errors.length === 0, errors }
}

/**
 * 记录API响应验证结果
 * @param {string} apiName - API名称
 * @param {Object} response - 响应对象
 * @param {string} type - 响应类型
 */
export function logAPIValidation(apiName, response, type) {
  const result = validateAPIResponse(response, type)
  
  if (result.valid) {
    console.log(`✓ ${apiName} 响应验证通过`)
  } else {
    console.warn(`✗ ${apiName} 响应验证失败:`, result.errors)
  }
  
  return result
}
