import request from '../utils/request';

/**
 * User registration
 * @param {Object} data - Registration data
 * @param {string} data.username - Username
 * @param {string} data.password - Password
 * @param {string} data.role - User role (user, coach, admin)
 * @returns {Promise} Registration response
 */
export function register(data) {
  return request({
    url: '/api/auth/register',
    method: 'post',
    data
  });
}

/**
 * User login
 * @param {Object} data - Login credentials
 * @param {string} data.username - Username
 * @param {string} data.password - Password
 * @returns {Promise} Login response with token and user info
 */
export function login(data) {
  return request({
    url: '/api/auth/login',
    method: 'post',
    data
  });
}

/**
 * User logout
 * @returns {Promise} Logout response
 */
export function logout() {
  return request({
    url: '/api/auth/logout',
    method: 'post'
  });
}
