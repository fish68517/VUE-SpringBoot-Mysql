import instance from './axios'

/**
 * 获取医院岗位列表
 */
export const getHospitalPosts = (params) => {
  return instance.get('/posts/hospital', { params })
}

/**
 * 创建岗位
 */
export const createPost = (data) => {
  return instance.post('/posts', data)
}

/**
 * 更新岗位
 */
export const updatePost = (id, data) => {
  return instance.put(`/posts/${id}`, data)
}

/**
 * 上下架岗位
 */
export const updatePostStatus = (id, data) => {
  return instance.put(`/posts/${id}/status`, data)
}

/**
 * 获取医院审批的申请列表
 */
export const getHospitalApplications = () => {
  return instance.get('/applications/hospital-review')
}

/**
 * 医院审批申请
 */
export const hospitalReviewApplication = (applicationId, data) => {
  return instance.put(`/applications/${applicationId}/hospital-review`, data)
}

/**
 * 获取医院数据统计
 */
export const getHospitalStatistics = () => {
  return instance.get('/statistics/hospital')
}
