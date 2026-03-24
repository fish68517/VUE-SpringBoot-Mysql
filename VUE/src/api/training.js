import request from '../utils/request';

/**
 * Get training plans
 * @param {Object} params - Query parameters
 * @param {number} params.page - Page number
 * @param {number} params.size - Page size
 * @returns {Promise} Training plans list
 */
export function getTrainingPlans(params) {
  return request({
    url: '/api/training-plans',
    method: 'get',
    params
  });
}

/**
 * Get training plan by ID
 * @param {number} id - Training plan ID
 * @returns {Promise} Training plan details
 */
export function getTrainingPlanById(id) {
  return request({
    url: `/api/training-plans/${id}`,
    method: 'get'
  });
}

/**
 * Create new training plan
 * @param {Object} data - Training plan data
 * @param {string} data.name - Plan name
 * @param {string} data.description - Plan description
 * @param {string} data.exercises - Exercises JSON string
 * @param {number} data.studentId - Student user ID
 * @param {string} data.startDate - Start date
 * @param {string} data.endDate - End date
 * @param {string} data.status - Plan status (active, completed, cancelled)
 * @returns {Promise} Created training plan
 */
export function createTrainingPlan(data) {
  return request({
    url: '/api/training-plans',
    method: 'post',
    data
  });
}

export function createTrainingPlanByCoachAndStudent(data) {
  return request({
    url: '/api/training-plans/coachAndStudent',
    method: 'post',
    data
  });
}


/**
 * Update training plan
 * @param {number} id - Training plan ID
 * @param {Object} data - Updated plan data
 * @returns {Promise} Updated training plan
 */
export function updateTrainingPlan(id, data) {
  return request({
    url: `/api/training-plans/${id}`,
    method: 'put',
    data
  });
}

/**
 * Delete training plan
 * @param {number} id - Training plan ID
 * @returns {Promise} Delete response
 */
export function deleteTrainingPlan(id) {
  return request({
    url: `/api/training-plans/${id}`,
    method: 'delete'
  });
}

// ==========================================
// APIs for coach/student feedback
// ==========================================

// Get feedback list for a student under current coach
export function getFeedbackListByStudent(params) {
  return request({
    url: '/training-feedback/list',
    method: 'get',
    params: {
      student_id: params.studentId,
      coach_id: params.coachId
    }
  })
}

// Coach replies to feedback
export function replyFeedback(data) {
  return request({
    url: '/training-feedback/reply',
    method: 'post',
    data: {
      id: data.id,
      coach_reply: data.coach_reply,
      coach_reply_image_urls: data.coach_reply_image_urls || '',
      coach_reply_video_urls: data.coach_reply_video_urls || '',
      coach_reply_document_urls: data.coach_reply_document_urls || ''
    }
  })
}

// Delete feedback
export function deleteFeedback(id) {
  return request({
    url: `/training-feedback/${id}`,
    method: 'delete'
  })
}
