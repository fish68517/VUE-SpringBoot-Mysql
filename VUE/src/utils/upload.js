import { ElMessage } from 'element-plus'
import http from '../api/http'

export async function uploadImageRequest(option) {
  const formData = new FormData()
  formData.append('file', option.file)
  try {
    const data = await http.post('/api/files/image', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    option.onSuccess(data)
    return data
  } catch (error) {
    ElMessage.error('图片上传失败')
    option.onError(error)
    return null
  }
}
