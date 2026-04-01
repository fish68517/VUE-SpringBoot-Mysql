/**
 * Theme Management Utility
 * Handles light/dark mode switching and persistence
 */

const THEME_STORAGE_KEY = 'naxi-platform-theme'
const THEME_ATTRIBUTE = 'data-theme'

// Theme options
export const THEMES = {
  LIGHT: 'light',
  DARK: 'dark',
  AUTO: 'auto'
}

/**
 * Get the current theme
 * @returns {string} Current theme ('light' or 'dark')
 */
export function getCurrentTheme() {
  const html = document.documentElement
  return html.getAttribute(THEME_ATTRIBUTE) || THEMES.LIGHT
}

/**
 * Get the stored theme preference
 * @returns {string} Stored theme preference
 */
export function getStoredTheme() {
  return localStorage.getItem(THEME_STORAGE_KEY) || THEMES.AUTO
}

/**
 * Check if dark mode is preferred by system
 * @returns {boolean} True if dark mode is preferred
 */
export function isDarkModePreferred() {
  return window.matchMedia('(prefers-color-scheme: dark)').matches
}

/**
 * Set the theme
 * @param {string} theme - Theme to set ('light', 'dark', or 'auto')
 */
export function setTheme(theme) {
  const html = document.documentElement
  let actualTheme = theme

  if (theme === THEMES.AUTO) {
    actualTheme = isDarkModePreferred() ? THEMES.DARK : THEMES.LIGHT
  }

  html.setAttribute(THEME_ATTRIBUTE, actualTheme)
  localStorage.setItem(THEME_STORAGE_KEY, theme)

  // Dispatch custom event for theme change
  window.dispatchEvent(
    new CustomEvent('themechange', {
      detail: { theme: actualTheme, preference: theme }
    })
  )
}

/**
 * Initialize theme on app load
 */
export function initializeTheme() {
  const storedTheme = getStoredTheme()
  setTheme(storedTheme)

  // Listen for system theme changes
  const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)')
  mediaQuery.addEventListener('change', (e) => {
    const storedPreference = getStoredTheme()
    if (storedPreference === THEMES.AUTO) {
      setTheme(THEMES.AUTO)
    }
  })
}

/**
 * Toggle between light and dark themes
 */
export function toggleTheme() {
  const currentTheme = getCurrentTheme()
  const newTheme = currentTheme === THEMES.LIGHT ? THEMES.DARK : THEMES.LIGHT
  setTheme(newTheme)
}

/**
 * Get theme preference for UI display
 * @returns {string} Theme preference to display
 */
export function getThemePreference() {
  return getStoredTheme()
}

/**
 * Check if current theme is dark
 * @returns {boolean} True if current theme is dark
 */
export function isDarkTheme() {
  return getCurrentTheme() === THEMES.DARK
}

/**
 * Get CSS variable value
 * @param {string} variableName - CSS variable name (without --)
 * @returns {string} CSS variable value
 */
export function getCSSVariable(variableName) {
  return getComputedStyle(document.documentElement)
    .getPropertyValue(`--${variableName}`)
    .trim()
}

/**
 * Set CSS variable value
 * @param {string} variableName - CSS variable name (without --)
 * @param {string} value - CSS variable value
 */
export function setCSSVariable(variableName, value) {
  document.documentElement.style.setProperty(`--${variableName}`, value)
}

export default {
  getCurrentTheme,
  getStoredTheme,
  isDarkModePreferred,
  setTheme,
  initializeTheme,
  toggleTheme,
  getThemePreference,
  isDarkTheme,
  getCSSVariable,
  setCSSVariable,
  THEMES
}
