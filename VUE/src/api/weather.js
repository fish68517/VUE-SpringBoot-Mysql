import apiClient from './client'

export const weatherAPI = {

  // 提取一个公共方法来获取用户的 region
  getUserRegion() {
    const userStr = localStorage.getItem('user')
    if (userStr) {
      try {
        const user = JSON.parse(userStr)
        // 假设后端的 LoginResponse 中包含了 region 字段
        return user.region
      } catch (e) {
        console.error('解析用户信息失败', e)
      }
    }
    return '' // 如果没有取到，返回空字符串，交由后端去拦截报错
  },

  getCurrentWeather(params = {}) {
    // 如果调用时没有传 region，则自动从 localStorage 中获取
    if (!params.region) {
      params.region = this.getUserRegion()
    }
    return apiClient.get('/weather/current', { params })
  },

  getForecast(params = {}) {
    // 同样给 forecast 接口自动加上 region 参数
    if (!params.region) {
      params.region = this.getUserRegion()
    }
    return apiClient.get('/weather/forecast', { params })
  },

  getHistory(params = {}) {
    // 同样给 history 接口自动加上 region 参数
    if (!params.region) {
      params.region = this.getUserRegion()
    }
    return apiClient.get('/weather/history', { params })
  },

  // 👇 新增这个方法，对接后端的 /api/weather/query 接口
  queryWeather(params = {}) {
    if (!params.region) params.region = this.getUserRegion()
    return apiClient.get('/weather/query', { params })
  }
}