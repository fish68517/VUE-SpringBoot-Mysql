import request from '../utils/request';

/**
 * Get all coaches
 * @returns {Promise} List of coaches
 */
export function getCoaches() {
  return request({
    url: '/api/coaches/list',
    method: 'get'
  });
}

/**
 * Get coach profile by ID
 * @param {number} id - Coach ID
 * @returns {Promise} Coach profile details
 */
export function getCoachById(id) {
  return request({
    url: `/api/coaches/${id}`,
    method: 'get'
  });
}

/**
 * Get current coach's students
 * @returns {Promise} List of students
 */
export function getMyStudents() {
  return request({
    url: '/api/coaches/students',
    method: 'get'
  });
}


/**
 * Get current coach's students
 * @returns {Promise} List of students
 */
export function getCoachStudentsById(coachId, studentId) {
  return request({
    url: `/api/coaches/coachId/${coachId}/students/${studentId}`,
    method: 'get'
  });
}

/**
 * Renew coach-student relationship
 * @param {Object} data - Renewal data { coachId, studentId, expireAt }
 * @returns {Promise} Response
 */
export function renewCoachStudent(data) {
  return request({
    url: '/api/coaches/renew',
    method: 'post',
    data
  });
}


/**
 * Add student to coach's roster
 * @param {number} userId - User ID to add as student
 * @returns {Promise} Add student response
 */
export function addStudent(userId) {
  return request({
    url: `/api/coaches/students/${userId}`,
    method: 'post'
  });
}

/**
 * Remove student from coach's roster
 * @param {number} userId - User ID to remove
 * @returns {Promise} Remove student response
 */
export function removeStudent(userId) {
  return request({
    url: `/api/coaches/students/${userId}`,
    method: 'delete'
  });
}

/**
 * Get student's check-in records
 * @param {number} studentId - Student user ID
 * @param {Object} params - Query parameters
 * @returns {Promise} Student's check-in history
 */
export function getStudentCheckIns(studentId, params) {
  return request({
    url: `/api/coaches/students/${studentId}/checkins`,
    method: 'get',
    params
  });
}

/**
 * Get student's diet records
 * @param {number} studentId - Student user ID
 * @param {Object} params - Query parameters
 * @param {string} params.startDate - Start date filter
 * @param {string} params.endDate - End date filter
 * @returns {Promise} Student's diet records
 */
export function getStudentDietRecords(studentId, params) {
  return request({
    url: `/api/coaches/students/${studentId}/diet-records`,
    method: 'get',
    params
  });
}

/**
 * Get student analytics
 * @param {number} studentId - Student user ID
 * @param {Object} params - Query parameters
 * @param {number} params.days - Number of days for analytics (default 30)
 * @returns {Promise} Student analytics data
 */
export function getStudentAnalytics(studentId, params) {
  return request({
    url: `/api/coaches/analytics/${studentId}`,
    method: 'get',
    params
  });
}
