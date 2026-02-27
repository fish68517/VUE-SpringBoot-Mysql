import request from './request'

export function getPublicSettings() {
  return request.get('/setting/public')
}

export function getSettingsList() {
  return request.get('/setting/list')
}

export function updateSetting(key, value) {
  return request.put(`/setting/update?key=${key}&value=${value}`)
}

export function batchUpdateSettings(settings) {
  return request.put('/setting/batch', settings)
}
