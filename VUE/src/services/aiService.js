import api from './api'

export const aiService = {
  generateDiary(data) {
    return api.post('/ai/diary/generate', data)
  }
}
