import apiClient from './api'

export default {
  // 获取投票列表
  getVotes(params) {
    return apiClient.get('/votes', { params })
  },

  // 获取投票详情
  getVoteDetail(id) {
    return apiClient.get(`/votes/${id}`)
  },

  // 获取投票统计信息
  getVoteStatistics(id) {
    return apiClient.get(`/votes/${id}/statistics`)
  },

  // 提交投票
  submitVote(id, voteData) {
    return apiClient.post(`/votes/${id}/vote`, voteData)
  },
}
