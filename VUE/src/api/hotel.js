import client from './client'

export const hotelApi = {
  getHotels: (params) => client.get('/hotels', { params }),
  getHotelDetail: (id) => client.get(`/hotels/${id}`),
  getDetail: (id, userId) => client.get(`/hotels/${id}`, { params: { userId } }),
  createHotel: (data) => client.post('/hotels', data),
  updateHotel: (id, data) => client.put(`/hotels/${id}`, data),
  deleteHotel: (id) => client.delete(`/hotels/${id}`),
  getHotelRooms: (hotelId) => client.get(`/hotels/${hotelId}/rooms`),
  addHotelRoom: (hotelId, data) => client.post(`/hotels/${hotelId}/rooms`, data),
  updateHotelRoom: (hotelId, roomId, data) => client.put(`/hotels/${hotelId}/rooms/${roomId}`, data),
  deleteHotelRoom: (hotelId, roomId) => client.delete(`/hotels/${hotelId}/rooms/${roomId}`)
}
