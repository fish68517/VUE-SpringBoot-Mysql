import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export interface UserProfile {
  userId?: string
  phone?: string
  nickname?: string
  avatar?: string
  email?: string
  realName?: string
  idNumber?: string
  isRealNameVerified?: boolean
  addresses?: Address[]
}

export interface Address {
  id?: string
  name: string
  phone: string
  province: string
  city: string
  district: string
  address: string
  isDefault: boolean
}

export const useUserStore = defineStore('user', () => {
  const token = ref<string>('')
  const userInfo = ref<UserProfile | null>(null)
  const isLoggedIn = computed(() => !!token.value)

  const setToken = (newToken: string) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  const setUserInfo = (info: UserProfile) => {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  const updateProfile = (updates: Partial<UserProfile>) => {
    if (userInfo.value) {
      userInfo.value = { ...userInfo.value, ...updates }
      localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
    }
  }

  const addAddress = (address: Address) => {
    if (!userInfo.value) {
      userInfo.value = {}
    }
    if (!userInfo.value.addresses) {
      userInfo.value.addresses = []
    }
    userInfo.value.addresses.push(address)
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
  }

  const updateAddress = (addressId: string, updates: Partial<Address>) => {
    if (userInfo.value?.addresses) {
      const index = userInfo.value.addresses.findIndex((a) => a.id === addressId)
      if (index !== -1) {
        userInfo.value.addresses[index] = { ...userInfo.value.addresses[index], ...updates }
        localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
      }
    }
  }

  const deleteAddress = (addressId: string) => {
    if (userInfo.value?.addresses) {
      userInfo.value.addresses = userInfo.value.addresses.filter((a) => a.id !== addressId)
      localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
    }
  }

  const logout = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  const loadFromStorage = () => {
    const storedToken = localStorage.getItem('token')
    const storedUserInfo = localStorage.getItem('userInfo')
    if (storedToken) {
      token.value = storedToken
    }
    if (storedUserInfo) {
      userInfo.value = JSON.parse(storedUserInfo)
    }
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    setToken,
    setUserInfo,
    updateProfile,
    addAddress,
    updateAddress,
    deleteAddress,
    logout,
    loadFromStorage,
  }
})
