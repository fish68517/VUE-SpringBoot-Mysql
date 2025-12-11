import axios from 'axios'
import { useUserStore } from '../stores/userStore'

// 配置 API 基础路径
// 如果你的 application.properties 设置了 server.servlet.context-path=/api
// 或者是 Vite 代理了 /api，请保留 '/api/files'
// 如果直连后端且没有 context-path，请改为 'http://localhost:8080/files'
const API_BASE_URL = 'http://localhost:8080/api/files' 

export const fileService = {
  /**
   * 上传文件 (适配后端分片接口)
   * @param {number} travelRecordId - 旅行记录ID
   * @param {File} fileObj - 文件对象
   */
  async uploadFile(travelRecordId, fileObj) {
    const formData = new FormData()
    
    // 组装后端 @RequestParam 需要的参数
    formData.append('travelRecordId', travelRecordId)
    formData.append('file', fileObj)
    formData.append('chunkIndex', 0)       // 默认单片上传：第0片
    formData.append('totalChunks', 1)      // 默认单片上传：共1片
    formData.append('fileName', fileObj.name)
    formData.append('fileSize', fileObj.size)

    // 获取 Token
    const userStore = useUserStore()
    const token = userStore.token 

    // 发送请求
    return axios.post(`${API_BASE_URL}/upload`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
        'Authorization': `Bearer ${token}` // 必须手动携带 Token
      }
    })
  },

  /**
   * 获取某条记录的所有文件
   * @param {number} travelRecordId 
   */
  async getFilesByTravelRecord(travelRecordId) {
    const userStore = useUserStore()
    const token = userStore.token
    
    // 对应后端 @GetMapping("/travel/{travelRecordId}")
    return axios.get(`${API_BASE_URL}/travel/${travelRecordId}`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
  },

  /**
   * 删除文件
   * @param {number} fileId 
   */
  async deleteFile(fileId) {
    const userStore = useUserStore()
    const token = userStore.token

    // 对应后端 @DeleteMapping("/{fileId}")
    return axios.delete(`${API_BASE_URL}/${fileId}`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
  }
}