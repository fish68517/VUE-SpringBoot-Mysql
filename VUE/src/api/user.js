import request from '../utils/request';

/**
 * Get current user profile
 * @returns {Promise} User profile data
 */
export function getProfile() {
  return request({
    url: '/api/users/profile',
    method: 'get'
  });
}

/**
 * Update user profile
 * @param {Object} data - Profile update data
 * @param {string} data.avatar - Avatar URL
 * @param {string} data.gender - Gender
 * @param {string} data.intro - Introduction
 * @returns {Promise} Updated profile data
 */
export function updateProfile(data) {
  return request({
    url: '/api/users/profile',
    method: 'put',
    data
  });
}

/**
 * Upload user avatar
 * @param {File} file - Avatar image file
 * @returns {Promise} Upload response with avatar URL
 */
export function uploadAvatar(file) {
  const formData = new FormData();
  formData.append('file', file);
  
  return request({
    url: '/api/users/avatar',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
}
