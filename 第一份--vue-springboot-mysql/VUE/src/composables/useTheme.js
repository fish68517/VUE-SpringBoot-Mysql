/**
 * Vue Composable for Theme Management
 * Provides reactive theme state and methods for Vue components
 */

import { ref, computed, onMounted } from 'vue'
import {
  getCurrentTheme,
  getStoredTheme,
  setTheme,
  toggleTheme,
  isDarkTheme,
  THEMES
} from '../utils/theme'

export function useTheme() {
  const currentTheme = ref(getCurrentTheme())
  const themePreference = ref(getStoredTheme())

  const isDark = computed(() => currentTheme.value === THEMES.DARK)
  const isLight = computed(() => currentTheme.value === THEMES.LIGHT)
  const isAuto = computed(() => themePreference.value === THEMES.AUTO)

  const handleThemeChange = (event) => {
    currentTheme.value = event.detail.theme
    themePreference.value = event.detail.preference
  }

  const changeTheme = (theme) => {
    setTheme(theme)
    themePreference.value = theme
    currentTheme.value = theme === THEMES.AUTO ? (isDarkTheme() ? THEMES.DARK : THEMES.LIGHT) : theme
  }

  const toggle = () => {
    toggleTheme()
    currentTheme.value = getCurrentTheme()
    themePreference.value = getStoredTheme()
  }

  onMounted(() => {
    window.addEventListener('themechange', handleThemeChange)
  })

  return {
    currentTheme,
    themePreference,
    isDark,
    isLight,
    isAuto,
    changeTheme,
    toggle,
    THEMES
  }
}

export default useTheme
