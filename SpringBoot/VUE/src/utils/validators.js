// 表单验证工具函数

// 验证邮箱格式
export const validateEmail = (email) => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return emailRegex.test(email)
}

// 验证电话号码格式
export const validatePhone = (phone) => {
  const phoneRegex = /^1[3-9]\d{9}$/
  return phoneRegex.test(phone)
}

// 验证用户名（3-20个字符，只能包含字母、数字、下划线）
export const validateUsername = (username) => {
  const usernameRegex = /^[a-zA-Z0-9_]{3,20}$/
  return usernameRegex.test(username)
}

// 验证密码（至少6个字符）
export const validatePassword = (password) => {
  return password && password.length >= 6
}

// 验证身高（100-250cm）
export const validateHeight = (height) => {
  const h = parseFloat(height)
  return h >= 100 && h <= 250
}

// 验证体重（20-200kg）
export const validateWeight = (weight) => {
  const w = parseFloat(weight)
  return w >= 20 && w <= 200
}

// 验证心率（30-200次/分）
export const validateHeartRate = (rate) => {
  const r = parseInt(rate)
  return r >= 30 && r <= 200
}

// 验证年龄（1-150岁）
export const validateAge = (age) => {
  const a = parseInt(age)
  return a >= 1 && a <= 150
}

// 验证日期格式（YYYY-MM-DD）
export const validateDateFormat = (date) => {
  const dateRegex = /^\d{4}-\d{2}-\d{2}$/
  return dateRegex.test(date)
}

// 验证必填字段
export const validateRequired = (value) => {
  return value !== null && value !== undefined && value !== ''
}

export default {
  validateEmail,
  validatePhone,
  validateUsername,
  validatePassword,
  validateHeight,
  validateWeight,
  validateHeartRate,
  validateAge,
  validateDateFormat,
  validateRequired
}
