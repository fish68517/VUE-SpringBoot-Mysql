import axios from 'axios'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000
})

request.interceptors.response.use(
  response => {
    const body = response.data
    if (body?.code !== 200) {
      return Promise.reject(new Error(body?.message || '请求失败'))
    }
    return body.data
  },
  error => Promise.reject(new Error(error.response?.data?.message || error.message || '网络请求失败'))
)

export default request
