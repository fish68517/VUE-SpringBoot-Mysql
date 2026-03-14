import instance from './axios.js'

export const adminLogin = (username, password) => {
  return instance.post('/admin/login', {
    username,
    password
  })
}

export const adminLogout = () => {
  return instance.post('/admin/logout')
}
