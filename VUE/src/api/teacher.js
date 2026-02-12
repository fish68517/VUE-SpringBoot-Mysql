import instance from './axios'

/**
 * 获取分配给老师的学生列表
 */
export const getStudentsByTeacher = (params) => {
  return instance.get('/teacher/students', { params })
}

/**
 * 获取学生详情
 */
export const getStudentDetail = (internshipId) => {
  return instance.get(`/teacher/students/${internshipId}`)
}

/**
 * 获取学生的任务列表
 */
export const getTasksByStudent = (internshipId, params) => {
  return instance.get(`/teacher/students/${internshipId}/tasks`, { params })
}

/**
 * 为学生创建任务
 */
export const createTask = (internshipId, data) => {
  return instance.post(`/teacher/students/${internshipId}/tasks`, data)
}

/**
 * 获取学生的周记列表
 */
export const getWeeklyReportsByStudent = (internshipId, params) => {
  return instance.get(`/teacher/students/${internshipId}/weekly-reports`, { params })
}

/**
 * 获取待批阅的周记列表
 */
export const getWeeklyReportsForTeacher = (params) => {
  return instance.get('/teacher/weekly-reports', { params })
}

/**
 * 批阅周记
 */
export const reviewWeeklyReport = (internshipId, reportId, data) => {
  return instance.put(`/internships/${internshipId}/weekly-reports/${reportId}/review`, data)
}

/**
 * 提交对学生的评价
 */
export const submitStudentEvaluation = (internshipId, data) => {
  return instance.post(`/internships/${internshipId}/evaluations`, data)
}

/**
 * 获取学生的评价历史
 */
export const getStudentEvaluationHistory = (internshipId, params) => {
  return instance.get(`/teacher/students/${internshipId}/evaluations`, { params })
}
