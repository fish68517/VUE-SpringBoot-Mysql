import instance from './axios.js'

/**
 * Get user list with pagination and search
 * @param {number} page - Page number (default 1)
 * @param {number} pageSize - Page size (default 10)
 * @param {string} search - Search keyword for username or email
 * @returns {Promise} API response with user list
 */
export const getUserList = (page = 1, pageSize = 10, search = '') => {
  return instance.get('/admin/users', {
    params: {
      page,
      pageSize,
      search: search || undefined
    }
  })
}

/**
 * Get user details by id
 * @param {number} id - User id
 * @returns {Promise} API response with user details
 */
export const getUserById = (id) => {
  return instance.get(`/admin/users/${id}`)
}

/**
 * Update user information
 * @param {number} id - User id
 * @param {object} userData - User data to update
 * @returns {Promise} API response
 */
export const updateUser = (id, userData) => {
  return instance.put(`/admin/users/${id}`, userData)
}

/**
 * Delete user by id
 * @param {number} id - User id
 * @returns {Promise} API response
 */
export const deleteUser = (id) => {
  return instance.delete(`/admin/users/${id}`)
}
