import request from './request'

export const getAdminDashboard = () => request.get('/admin/dashboard')
export const getAdminPage = (module, params) => request.get(`/admin/${module}`, { params })
export const getAdminItem = (module, id) => request.get(`/admin/${module}/${id}`)
export const createAdminItem = (module, data) => request.post(`/admin/${module}`, data)
export const updateAdminItem = (module, id, data) => request.put(`/admin/${module}/${id}`, data)
export const deleteAdminItem = (module, id) => request.delete(`/admin/${module}/${id}`)
export const runSimulation = () => request.post('/admin/simulation/run')
export const regenerateStatistics = (statDate) => request.post('/admin/statistics/regenerate', null, { params: { statDate } })
