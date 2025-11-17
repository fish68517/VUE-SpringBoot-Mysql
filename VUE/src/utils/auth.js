// Token storage key
const TOKEN_KEY = 'auth_token';
const USER_INFO_KEY = 'user_info';

/**
 * Get authentication token from localStorage
 * @returns {string|null} Token or null if not found
 */
export function getToken() {
  return localStorage.getItem(TOKEN_KEY);
}

/**
 * Store authentication token in localStorage
 * @param {string} token - Authentication token
 */
export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token);
}

/**
 * Remove authentication token from localStorage
 */
export function removeToken() {
  localStorage.removeItem(TOKEN_KEY);
}

/**
 * Get user information from localStorage
 * @returns {Object|null} User object or null if not found
 */
export function getUserInfo() {
  const userInfo = localStorage.getItem(USER_INFO_KEY);
  if (userInfo) {
    try {
      return JSON.parse(userInfo);
    } catch (error) {
      console.error('Failed to parse user info:', error);
      return null;
    }
  }
  return null;
}

/**
 * Store user information in localStorage
 * @param {Object} user - User object
 */
export function setUserInfo(user) {
  localStorage.setItem(USER_INFO_KEY, JSON.stringify(user));
}

/**
 * Clear all authentication data from localStorage
 */
export function clearAuth() {
  removeToken();
  localStorage.removeItem(USER_INFO_KEY);
}
