/**
 * Storage utility for managing localStorage
 */

const PREFIX = 'naxi_'

/**
 * Set item in localStorage
 * @param {string} key - Storage key
 * @param {any} value - Value to store
 */
export const setStorage = (key, value) => {
  try {
    const serialized = JSON.stringify(value)
    localStorage.setItem(PREFIX + key, serialized)
  } catch (error) {
    console.error('Error setting storage:', error)
  }
}

/**
 * Get item from localStorage
 * @param {string} key - Storage key
 * @returns {any} Stored value or null
 */
export const getStorage = (key) => {
  try {
    const item = localStorage.getItem(PREFIX + key)
    return item ? JSON.parse(item) : null
  } catch (error) {
    console.error('Error getting storage:', error)
    return null
  }
}

/**
 * Remove item from localStorage
 * @param {string} key - Storage key
 */
export const removeStorage = (key) => {
  try {
    localStorage.removeItem(PREFIX + key)
  } catch (error) {
    console.error('Error removing storage:', error)
  }
}

/**
 * Clear all items from localStorage with prefix
 */
export const clearStorage = () => {
  try {
    const keys = Object.keys(localStorage)
    keys.forEach(key => {
      if (key.startsWith(PREFIX)) {
        localStorage.removeItem(key)
      }
    })
  } catch (error) {
    console.error('Error clearing storage:', error)
  }
}

/**
 * Get all items from localStorage with prefix
 * @returns {object} All stored items
 */
export const getAllStorage = () => {
  try {
    const items = {}
    const keys = Object.keys(localStorage)
    keys.forEach(key => {
      if (key.startsWith(PREFIX)) {
        const cleanKey = key.replace(PREFIX, '')
        items[cleanKey] = getStorage(cleanKey)
      }
    })
    return items
  } catch (error) {
    console.error('Error getting all storage:', error)
    return {}
  }
}
