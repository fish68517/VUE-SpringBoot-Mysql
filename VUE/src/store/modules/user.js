import { defineStore } from 'pinia'
import { getProfile, updateProfile as updateProfileApi } from '@/api/user'
import { getCheckInStats } from '@/api/checkin'

export const useUserStore = defineStore('user', {
  state: () => ({
    profile: null,
    checkInStats: {
      totalCount: 0,
      currentStreak: 0,
      longestStreak: 0
    }
  }),

  getters: {
    userProfile: (state) => state.profile,
    stats: (state) => state.checkInStats
  },

  actions: {
    async fetchProfile() {
      try {
        const response = await getProfile()
        this.profile = response
        return response
      } catch (error) {
        console.error('Failed to fetch profile:', error)
        throw error
      }
    },

    async updateProfile(data) {
      try {
        const response = await updateProfileApi(data)
        this.profile = { ...this.profile, ...response }
        return response
      } catch (error) {
        console.error('Failed to update profile:', error)
        throw error
      }
    },

    async fetchCheckInStats() {
      try {
        const response = await getCheckInStats()
        this.checkInStats = response
        return response
      } catch (error) {
        console.error('Failed to fetch check-in stats:', error)
        throw error
      }
    },

    clearUserData() {
      this.profile = null
      this.checkInStats = {
        totalCount: 0,
        currentStreak: 0,
        longestStreak: 0
      }
    }
  }
})
