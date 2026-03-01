// 简体中文翻译文件
export default {
  // 通用
  common: {
    submit: '提交',
    cancel: '取消',
    save: '保存',
    delete: '删除',
    edit: '编辑',
    add: '添加',
    search: '搜索',
    reset: '重置',
    loading: '加载中...',
    success: '成功',
    error: '错误',
    warning: '警告',
    info: '信息',
    confirm: '确认',
    back: '返回',
    logout: '登出',
    home: '首页',
    user: '用户',
    noAccount: '没有账户？',
    hasAccount: '已有账户？',
    clear: '清空',
    noData: '暂无数据',
    createdAt: '创建时间',
    dateRange: '日期范围',
    id: 'ID',
    view: '查看',
    action: '操作'
  },

  // 认证相关
  auth: {
    login: '登录',
    register: '注册',
    username: '用户名',
    password: '密码',
    email: '邮箱',
    confirmPassword: '确认密码',
    loginSuccess: '登录成功',
    registerSuccess: '注册成功',
    loginFailed: '登录失败',
    registerFailed: '注册失败',
    invalidCredentials: '用户名或密码错误',
    userExists: '用户已存在',
    passwordMismatch: '两次输入的密码不一致',
    userRole: '用户角色',
    doctorLicense: '医师执业证号',
    doctorRegister: '医师注册',
    adminLogin: '管理员登录'
  },

  // 用户相关
  user: {
    profile: '个人信息',
    name: '姓名',
    age: '年龄',
    gender: '性别',
    phone: '电话',
    address: '地址',
    updateProfile: '更新个人信息',
    profileUpdated: '个人信息已更新',
    male: '男',
    female: '女',
    other: '其他'
  },

  // 健康数据相关
  healthData: {
    submitData: '提交健康数据',
    height: '身高(cm)',
    weight: '体重(kg)',
    bloodPressure: '血压',
    heartRate: '心率(次/分)',
    dietRecord: '饮食记录',
    exerciseRecord: '运动记录',
    dataSubmitted: '健康数据已提交',
    viewTrends: '查看趋势',
    trends: '健康趋势',
    dataRange: '数据范围',
    startDate: '开始日期',
    endDate: '结束日期',
    recordDate: '记录时间',
    recentData: '最近提交的数据',
    healthData: '健康数据',
    dataType: '数据类型'
  },

  // 健康检查相关
  healthCheck: {
    routineCheck: '常规健康检查',
    checkItems: '检查项目',
    checkResults: '检查结果',
    checkDate: '检查日期',
    recordSaved: '检查记录已保存'
  },

  // 性别健康相关
  genderHealth: {
    genderHealth: '性别健康',
    menstrualCycle: '月经周期',
    menstrualDays: '月经天数',
    lastMenstrualDate: '最后月经日期',
    pregnancyStatus: '妊娠状态',
    menstrualSymptoms: '月经症状',
    prostateHealth: '前列腺健康',
    sexualFunction: '性功能状态',
    notes: '备注',
    recentRecords: '最近记录',
    privacyNotice: '此信息为隐私数据，仅您和您的医师可见',
    dataSaved: '性别健康数据已保存'
  },

  // 咨询相关
  consultation: {
    consultation: '在线咨询',
    submitQuestion: '提交问题',
    question: '问题',
    answer: '回答',
    consultationHistory: '咨询历史',
    status: '状态',
    pending: '待回答',
    answered: '已回答',
    questionSubmitted: '问题已提交',
    answerReceived: '已收到回答'
  },

  // 健康历史相关
  healthHistory: {
    healthHistory: '健康历史',
    recordDate: '记录日期',
    recordType: '记录类型',
    recordDetails: '记录详情',
    noRecords: '暂无记录'
  },

  // 医师相关
  doctor: {
    patients: '患者列表',
    patientRecord: '患者档案',
    patientInfo: '患者信息',
    healthData: '健康数据',
    dataReview: '数据审核',
    selectPatient: '选择患者',
    reviewOpinion: '审核意见',
    existingFeedback: '已有反馈',
    feedback: '反馈',
    healthAdvice: '健康建议',
    adviceContent: '建议内容',
    recommendation: '推荐',
    adviceSent: '建议已发送'
  },

  // 管理员相关
  admin: {
    userManagement: '用户管理',
    doctorManagement: '医师管理',
    dataStatistics: '数据统计',
    auditLogs: '审计日志',
    allUsers: '所有用户',
    allDoctors: '所有医师',
    userRole: '用户角色',
    status: '状态',
    active: '活跃',
    inactive: '禁用',
    roleUpdated: '角色已更新',
    userDisabled: '用户已禁用',
    doctorDeleted: '医师已删除',
    action: '操作',
    timestamp: '时间戳',
    details: '详情',
    licenseNumber: '执业证号',
    specialization: '专科',
    hospital: '医院',
    verified: '认证状态',
    unverified: '未认证',
    editDoctorInfo: '编辑医师信息',
    userStatistics: '用户统计',
    userStatus: '用户状态',
    healthDataStatistics: '健康数据统计',
    userDistribution: '用户角色分布',
    healthDataDistribution: '健康数据类型分布',
    userStatusDistribution: '用户状态分布',
    detailedStatistics: '详细统计数据',
    statisticsItem: '统计项目',
    statisticsValue: '数值',
    description: '描述',
    auditLogDetails: '审计日志详情',
    resource: '资源'
  },

  // 错误消息
  errors: {
    networkError: '网络错误',
    serverError: '服务器错误',
    unauthorized: '未授权',
    forbidden: '禁止访问',
    notFound: '未找到',
    validationError: '验证错误',
    requiredField: '此字段为必填项',
    invalidEmail: '邮箱格式不正确',
    invalidPhone: '电话号码格式不正确',
    passwordTooShort: '密码长度至少为6位'
  },

  // 导航菜单
  menu: {
    personalInfo: '个人信息',
    healthData: '健康数据',
    healthTrend: '健康趋势',
    healthCheck: '常规检查',
    genderHealth: '性别健康',
    consultation: '在线咨询',
    healthHistory: '健康历史',
    patientList: '患者列表',
    patientRecord: '患者档案',
    dataReview: '数据审核',
    healthAdvice: '健康建议',
    userManagement: '用户管理',
    doctorManagement: '医师管理',
    dataStatistics: '数据统计',
    auditLogs: '审计日志'
  }
}
