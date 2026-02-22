import { defineStore } from 'pinia'
import { ref } from 'vue'
import { weatherAPI } from '@/api/weather'

export const useWeatherStore = defineStore('weather', () => {
  const weatherData = ref(null)
  const forecast = ref([])
  const history = ref([])
  const loading = ref(false)
  const error = ref(null)

  async function fetchCurrentWeather(region) {
    loading.value = true
    error.value = null
    try {
      const response = await weatherAPI.getCurrentWeather({ region })
      weatherData.value = response
    } catch (err) {
      error.value = err.message
    } finally {
      loading.value = false
    }
  }

  async function fetchForecast(region) {
    loading.value = true
    error.value = null
    try {
      const response = await weatherAPI.getForecast({ region })
      forecast.value = Array.isArray(response) ? response : []
    } catch (err) {
      error.value = err.message
    } finally {
      loading.value = false
    }
  }

  async function fetchHistory(region, startDate, endDate) {
    loading.value = true
    error.value = null
    try {
      const response = await weatherAPI.getHistory({ region, startDate, endDate })
      history.value = Array.isArray(response) ? response : []
    } catch (err) {
      error.value = err.message
    } finally {
      loading.value = false
    }
  }

  return {
    weatherData,
    forecast,
    history,
    loading,
    error,
    fetchCurrentWeather,
    fetchForecast,
    fetchHistory,
  }
})
