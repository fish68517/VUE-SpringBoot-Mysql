import api from './api'

/**
 * Upload a file chunk
 * @param {number} travelRecordId - The travel record ID
 * @param {Blob} chunk - The file chunk
 * @param {number} chunkIndex - The chunk index
 * @param {number} totalChunks - The total number of chunks
 * @param {string} fileName - The original file name
 * @param {number} fileSize - The total file size
 * @returns {Promise} The response from the server
 */
export const uploadFileChunk = async (
  travelRecordId,
  chunk,
  chunkIndex,
  totalChunks,
  fileName,
  fileSize
) => {
  const formData = new FormData()
  formData.append('travelRecordId', travelRecordId)
  formData.append('file', chunk)
  formData.append('chunkIndex', chunkIndex)
  formData.append('totalChunks', totalChunks)
  formData.append('fileName', fileName)
  formData.append('fileSize', fileSize)

  return api.post('/files/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * Get all multimedia files for a travel record
 * @param {number} travelRecordId - The travel record ID
 * @returns {Promise} The response from the server
 */
export const getFilesByTravelRecord = async (travelRecordId) => {
  return api.get(`/files/travel/${travelRecordId}`)
}

/**
 * Delete a multimedia file
 * @param {number} fileId - The file ID
 * @returns {Promise} The response from the server
 */
export const deleteFile = async (fileId) => {
  return api.delete(`/files/${fileId}`)
}

export default {
  uploadFileChunk,
  getFilesByTravelRecord,
  deleteFile
}
