import request from './request'

export const festivalApi = {
  getFestivalInfo: () => request.get('/festival/info'),
  getFestivalSchedule: () => request.get('/festival/schedule'),
  getArtists: () => request.get('/artists'),
  getArtistById: (id: number) => request.get(`/artists/${id}`),
  getLocalBands: () => request.get('/artists/local-bands'),
  getWeather: () => request.get('/weather'),
  getTransport: () => request.get('/transport'),
}
