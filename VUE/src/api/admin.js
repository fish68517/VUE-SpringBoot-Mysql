import instance from './axios'

/**
 * 获取所有组织
 */
export const getAllOrganizations = () => {
  return instance.get('/organizations')
}

/**
 * 创建组织
 */
export const createOrganization = (data) => {
  return instance.post('/organizations', data)
}

/**
 * 更新组织信息
 */
export const updateOrganization = (id, data) => {
  return instance.put(`/organizations/${id}`, data)
}

/**
 * 获取组织内的用户列表
 */
export const getOrganizationUsers = (organizationId) => {
  return instance.get(`/organizations/${organizationId}/users`)
}

/**
 * 获取所有待认证用户
 */
export const getPendingUsers = () => {
  return instance.get('/users/pending-auth')
}

/**
 * 审批用户认证
 */
export const approveUser = (userId, data) => {
  return instance.put(`/users/${userId}/approve-auth`, data)
}

/**
 * 驳回用户认证
 */
export const rejectUser = (userId, data) => {
  return instance.put(`/users/${userId}/reject-auth`, data)
}
