import client from './client'

export const browsingHistoryApi = {
  getBrowsingHistory: (params) => client.get('/browsing-history', { params }),
  recordBrowsingHistory: (data) => client.post('/browsing-history', data)
}
