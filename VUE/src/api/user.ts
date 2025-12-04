import request from './request'

export const userApi = {
  register: (data: any) => request.post('/users/register', data),
  login: (data: any) => request.post('/users/login', data),
  sendSms: (phone: string) => request.post('/users/send-sms', { phone }),
  verifySms: (phone: string, code: string) => request.post('/users/verify-sms', { phone, code }),
  logout: () => request.post('/users/logout'),
  getProfile: () => request.get('/users/profile'),
  updateProfile: (data: any) => request.put('/users/profile', data),
  realNameAuth: (data: any) => request.post('/users/real-name', data),
  getRealNameStatus: () => request.get('/users/real-name'),
  uploadAvatar: (file: File) => {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/users/avatar', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
  },
}
