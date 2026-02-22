/**
 * 用户类型常量
 */
export const USER_TYPES = {
  FARMER: 'farmer',
  MERCHANT: 'merchant',
  ADMIN: 'admin',
}

export const USER_TYPE_LABELS = {
  farmer: '农户',
  merchant: '商家',
  admin: '管理员',
}

/**
 * 用户状态常量
 */
export const USER_STATUS = {
  ACTIVE: 'active',
  INACTIVE: 'inactive',
  LOCKED: 'locked',
}

export const USER_STATUS_LABELS = {
  active: '活跃',
  inactive: '不活跃',
  locked: '锁定',
}

/**
 * 预警等级常量
 */
export const WARNING_SEVERITY = {
  LOW: 'low',
  MEDIUM: 'medium',
  HIGH: 'high',
  CRITICAL: 'critical',
}

export const WARNING_SEVERITY_LABELS = {
  low: '低',
  medium: '中',
  high: '高',
  critical: '严重',
}

/**
 * 预警状态常量
 */
export const WARNING_STATUS = {
  ACTIVE: 'active',
  EXPIRED: 'expired',
  CANCELLED: 'cancelled',
}

export const WARNING_STATUS_LABELS = {
  active: '活跃',
  expired: '已过期',
  cancelled: '已取消',
}

/**
 * 订单状态常量
 */
export const ORDER_STATUS = {
  PENDING: 'pending',
  PAID: 'paid',
  SHIPPED: 'shipped',
  DELIVERED: 'delivered',
  CANCELLED: 'cancelled',
}

export const ORDER_STATUS_LABELS = {
  pending: '待支付',
  paid: '已支付',
  shipped: '已发货',
  delivered: '已送达',
  cancelled: '已取消',
}

/**
 * 推荐状态常量
 */
export const RECOMMENDATION_STATUS = {
  PENDING: 'pending',
  ACCEPTED: 'accepted',
  REJECTED: 'rejected',
}

export const RECOMMENDATION_STATUS_LABELS = {
  pending: '待处理',
  accepted: '已接受',
  rejected: '已拒绝',
}

/**
 * 产品分类常量
 */
export const PRODUCT_CATEGORIES = {
  FERTILIZER: 'fertilizer',
  PESTICIDE: 'pesticide',
  SEED: 'seed',
  EQUIPMENT: 'equipment',
  OTHER: 'other',
}

export const PRODUCT_CATEGORY_LABELS = {
  fertilizer: '肥料',
  pesticide: '农药',
  seed: '种子',
  equipment: '农具',
  other: '其他',
}

/**
 * 作物生育期常量
 */
export const GROWTH_STAGES = {
  GERMINATION: 'germination',
  SEEDLING: 'seedling',
  VEGETATIVE: 'vegetative',
  FLOWERING: 'flowering',
  FRUITING: 'fruiting',
  MATURITY: 'maturity',
}

export const GROWTH_STAGE_LABELS = {
  germination: '萌芽期',
  seedling: '苗期',
  vegetative: '营养生长期',
  flowering: '开花期',
  fruiting: '结果期',
  maturity: '成熟期',
}

/**
 * API响应码常量
 */
export const API_CODES = {
  SUCCESS: 200,
  CREATED: 201,
  BAD_REQUEST: 400,
  UNAUTHORIZED: 401,
  FORBIDDEN: 403,
  NOT_FOUND: 404,
  CONFLICT: 409,
  SERVER_ERROR: 500,
}

/**
 * 分页常量
 */
export const PAGINATION = {
  DEFAULT_PAGE_SIZE: 10,
  PAGE_SIZES: [10, 20, 50, 100],
}

/**
 * 时间常量 (毫秒)
 */
export const TIME_CONSTANTS = {
  SECOND: 1000,
  MINUTE: 60 * 1000,
  HOUR: 60 * 60 * 1000,
  DAY: 24 * 60 * 60 * 1000,
}

/**
 * 消息类型常量
 */
export const MESSAGE_TYPES = {
  SUCCESS: 'success',
  ERROR: 'error',
  WARNING: 'warning',
  INFO: 'info',
}
