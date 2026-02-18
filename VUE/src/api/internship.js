import instance from './axios'

/**
 * 获取实习记录列表
 */
export const getInternships = (params) => {
  return instance.get('/internships', { params })
}

/**
 * 获取实习记录详情
 */
export const getInternshipDetail = (id) => {
  return instance.get(`/internships/${id}`)
}

/**
 * 提交周记
 */
export const submitWeeklyReport = (internshipId, data) => {
  return instance.post(`/internships/${internshipId}/weekly-reports`, data)
}

/**
 * 获取周记列表
 */
export const getWeeklyReports = (internshipId, params) => {
  return instance.get(`/internships/${internshipId}/weekly-reports`, { params })
}

/**
 * 提交评价
 */
export const submitEvaluation = (internshipId, data) => {
  return instance.post(`/internships/${internshipId}/evaluations`, data)
}

/**
 * 获取评价列表
 */
export const getEvaluations = (internshipId, params) => {
  return instance.get(`/internships/${internshipId}/evaluations`, { params })
}
/**
 * 手动添加实习记录
 */
export const createInternship = (data) => {
  return instance.post('/internships', data)
}
