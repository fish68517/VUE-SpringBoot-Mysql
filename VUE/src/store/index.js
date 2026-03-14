import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAdminStore = defineStore('admin', () => {
  const token = ref(localStorage.getItem('adminToken') || '')
  const adminInfo = ref(JSON.parse(localStorage.getItem('adminInfo') || '{}'))

  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('adminToken', newToken)
  }

  const setAdminInfo = (info) => {
    adminInfo.value = info
    localStorage.setItem('adminInfo', JSON.stringify(info))
  }

  const logout = () => {
    token.value = ''
    adminInfo.value = {}
    localStorage.removeItem('adminToken')
    localStorage.removeItem('adminInfo')
  }

  const isAuthenticated = () => {
    return !!token.value
  }

  return {
    token,
    adminInfo,
    setToken,
    setAdminInfo,
    logout,
    isAuthenticated
  }
})

export const useUserStore = defineStore('user', () => {
  const users = ref([])
  const total = ref(0)

  const setUsers = (data) => {
    users.value = data
  }

  const setTotal = (count) => {
    total.value = count
  }

  return {
    users,
    total,
    setUsers,
    setTotal
  }
})

export const useCommunityStore = defineStore('community', () => {
  const posts = ref([])
  const total = ref(0)

  const setPosts = (data) => {
    posts.value = data
  }

  const setTotal = (count) => {
    total.value = count
  }

  return {
    posts,
    total,
    setPosts,
    setTotal
  }
})
