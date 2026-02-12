import instance from './axios'

/**
 * 用户登录
 */
export const login = (username, password) => {
  return instance.post('/users/login', {
    username,
    password,
  })
}

/**
 * 用户注册
 */
export const register = (data) => {
  return instance.post('/users/register', data)
}

/**
 * 获取个人信息
 */
export const getProfile = () => {
  return instance.get('/users/profile')
}

/**
 * 更新个人信息
 */
export const updateProfile = (data) => {
  return instance.put('/users/profile', data)
}

/**
 * 用户登出
 */
export const logout = () => {
  return instance.post('/users/logout')
}

/**
 * 获取所有组织
 */
export const getOrganizations = () => {
  return instance.get('/organizations')
}
