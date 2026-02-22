import apiClient from './client'

export const weatherAPI = {
  getCurrentWeather(params) {
    return apiClient.get('/weather/current', { params })
  },

  getForecast(params) {
    return apiClient.get('/weather/forecast', { params })
  },

  getHistory(params) {
    return apiClient.get('/weather/history', { params })
  },
}
