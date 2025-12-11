import axios from 'axios'

// 假设你的 userStore 定义在这里，根据实际路径修改
// 如果无法在非组件文件中使用 useUserStore，可以使用 localStorage 获取
import { useUserStore } from '../stores/userStore' 

const API_BASE_URL = 'http://localhost:8080/api/files'

export const fileService = {
  async uploadFile(travelRecordId, fileObj) {
    const formData = new FormData()
    
    formData.append('travelRecordId', travelRecordId)
    formData.append('file', fileObj)
    formData.append('chunkIndex', 0)
    formData.append('totalChunks', 1)
    formData.append('fileName', fileObj.name)
    formData.append('fileSize', fileObj.size)

    // --- 修复开始：获取 Token ---
    // 方法 A: 如果使用了 Pinia 且可以在此处调用
    const userStore = useUserStore()
    // 打印 token
    console.log('Token:', JSON.stringify(userStore))
    const token = userStore.token 
    
    // 方法 B: 如果方法 A 报错，直接从 localStorage 获取 (假设你存的是 'token')
    // const token = localStorage.getItem('token') 
    // --- 修复结束 ---

    return axios.post(`${API_BASE_URL}/upload`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
        // --- 关键修复：显式添加认证头 ---
        'Authorization': `Bearer ${token}` 
      }
    })
  }
}