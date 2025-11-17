import request from '../utils/request';

/**
 * Upload image file
 * @param {File} file - Image file to upload
 * @returns {Promise} Upload response with file URL
 */
export function uploadImage(file) {
  const formData = new FormData();
  formData.append('file', file);
  
  return request({
    url: '/api/upload/image',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
}

/**
 * Upload video file
 * @param {File} file - Video file to upload
 * @returns {Promise} Upload response with file URL
 */
export function uploadVideo(file) {
  const formData = new FormData();
  formData.append('file', file);
  
  return request({
    url: '/api/upload/video',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
}
