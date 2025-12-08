// src/api/feedback.js

import request from '@/utils/request' // 导入我们封装好的 axios 实例

/**
 * @description 获取指定训练计划的所有反馈记录
 * @param {number | string} planId - 训练计划的 ID
 * @returns {Promise<Array>} 返回包含历史反馈对象的数组
 */
export function getFeedbackForPlan(planId) {
  // 发起 GET 请求
  return request({
    url: '/training-feedback', // 后端接口地址
    method: 'get',
    // 将 planId 作为 URL 查询参数传递，例如：/training-feedback?planId=123
    params: {
      planId
    }
  })
}

/**
 * @description 提交一条新的训练反馈
 * @param {object} data - 反馈数据对象
 * @param {number} data.planId - 关联的训练计划 ID
 * @param {string} data.feedbackDate - 反馈的日期 (格式: YYYY-MM-DD)
 * @param {string} data.feedbackText - 反馈的文本内容
 * @param {number} [data.rating] - 训练评分 (1-5), 可选
 * @returns {Promise<object>} 返回后端创建的新的反馈对象
 */
export function submitNewFeedback(data) {
  // 发起 POST 请求
  return request({
    url: '/training-feedback', // 后端接口地址
    method: 'post',
    // 将 data 对象作为请求体 (Request Body) 发送
    data
  })
}
