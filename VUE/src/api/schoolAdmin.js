import instance from './axios'

/**
 * 获取待认证学生列表
 */
export const getPendingStudents = () => {
  return instance.get('/users/pending-students')
}

/**
 * 审批学生认证
 */
export const approveStudent = (userId, data) => {
  return instance.put(`/users/${userId}/approve`, data)
}

/**
 * 驳回学生认证
 */
export const rejectStudent = (userId, data) => {
  return instance.put(`/users/${userId}/reject`, data)
}

/**
 * 获取学校审批的申请列表
 */
export const getSchoolApplications = () => {
  return instance.get('/applications/school-review')
}

/**
 * 学校审批申请
 */
export const schoolReviewApplication = (applicationId, data) => {
  return instance.put(`/applications/${applicationId}/school-review`, data)
}

/**
 * 获取学校数据统计
 */
export const getSchoolStatistics = () => {
  return instance.get('/statistics/school')
}
