import type { StorageAdapter } from '../domain/types'
export const storage: StorageAdapter = {
  get: (key) => uni.getStorageSync(key) || null,
  set: (key, value) => uni.setStorageSync(key, value),
  remove: (key) => uni.removeStorageSync(key),
}
