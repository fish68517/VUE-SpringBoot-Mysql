import axios from 'axios'

/**
 * Upload image file
 * @param {File} file - The image file to upload
 * @returns {Promise} API response with file path
 */
export const uploadImage = (file) => {
  const formData = new FormData()
  formData.append('file', file)

  return axios.post('http://localhost:8080/api/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    timeout: 30000
  })
}
