import instance from './axios'

/**
 * 提交岗位申请
 */
export const createApplication = (data) => {
  return instance.post('/applications', data)
}

/**
 * 获取申请列表
 */
export const getApplications = (params) => {
  return instance.get('/applications', { params })
}

/**
 * 获取申请详情
 */
export const getApplicationDetail = (id) => {
  return instance.get(`/applications/${id}`)
}
