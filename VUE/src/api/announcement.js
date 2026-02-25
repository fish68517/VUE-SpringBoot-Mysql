import client from './client'

export const announcementApi = {
  // 游客端接口
  getAnnouncements: (params) => client.get('/announcements', { params }),
  getAnnouncementDetail: (id) => client.get(`/announcements/${id}`),
  
  // 管理员端接口
  createAnnouncement: (data) => client.post('/announcements/admin', data),
  updateAnnouncement: (id, data) => client.put(`/announcements/admin/${id}`, data),
  deleteAnnouncement: (id) => client.delete(`/announcements/admin/${id}`)
}
