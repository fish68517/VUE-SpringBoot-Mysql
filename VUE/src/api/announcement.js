import client from './client'

export const announcementApi = {
  getAnnouncements: (params) => client.get('/announcements', { params }),
  getAnnouncementDetail: (id) => client.get(`/announcements/${id}`),
  createAnnouncement: (data) => client.post('/announcements', data),
  updateAnnouncement: (id, data) => client.put(`/announcements/${id}`, data),
  deleteAnnouncement: (id) => client.delete(`/announcements/${id}`)
}
