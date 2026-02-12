import instance from './axios'

/**
 * 获取岗位列表
 */
export const getPosts = (params) => {
  return instance.get('/posts', { params })
}

/**
 * 获取岗位详情
 */
export const getPostDetail = (id) => {
  return instance.get(`/posts/${id}`)
}
