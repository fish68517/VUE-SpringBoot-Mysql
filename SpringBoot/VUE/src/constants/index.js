// 系统常量定义

// 用户角色
export const USER_ROLES = {
  USER: 'USER',
  DOCTOR: 'DOCTOR',
  ADMIN: 'ADMIN'
}

// 用户状态
export const USER_STATUS = {
  ACTIVE: 'ACTIVE',
  INACTIVE: 'INACTIVE'
}

// 咨询状态
export const CONSULTATION_STATUS = {
  PENDING: 'PENDING',
  ANSWERED: 'ANSWERED'
}

// 性别
export const GENDER = {
  MALE: 'M',
  FEMALE: 'F',
  OTHER: 'O'
}

// 健康数据类型
export const HEALTH_DATA_TYPE = {
  ROUTINE: 'ROUTINE',
  GENDER_SPECIFIC: 'GENDER_SPECIFIC'
}

// 分页大小
export const PAGE_SIZE = 10

// API 超时时间（毫秒）
export const API_TIMEOUT = 10000

// 本地存储键
export const STORAGE_KEYS = {
  TOKEN: 'token',
  USER: 'user',
  ROLE: 'role',
  LANGUAGE: 'language'
}

// 路由路径
export const ROUTES = {
  LOGIN: '/login',
  REGISTER: '/register',
  USER_HOME: '/user/profile',
  DOCTOR_HOME: '/doctor/patients',
  ADMIN_HOME: '/admin/users'
}

// 错误代码
export const ERROR_CODES = {
  UNAUTHORIZED: 401,
  FORBIDDEN: 403,
  NOT_FOUND: 404,
  SERVER_ERROR: 500,
  NETWORK_ERROR: 'NETWORK_ERROR'
}

// 成功代码
export const SUCCESS_CODE = 200

export default {
  USER_ROLES,
  USER_STATUS,
  CONSULTATION_STATUS,
  GENDER,
  HEALTH_DATA_TYPE,
  PAGE_SIZE,
  API_TIMEOUT,
  STORAGE_KEYS,
  ROUTES,
  ERROR_CODES,
  SUCCESS_CODE
}
