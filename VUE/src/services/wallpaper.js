import axios from 'axios'

const WALLPAPER_API_BASE = (import.meta.env.VITE_WALLPAPER_API_URL || 'http://localhost:8001').replace(/\/+$/, '')

const wallpaperApi = axios.create({
  baseURL: WALLPAPER_API_BASE,
  timeout: 120000
})

export const resolveWallpaperUrl = (path) => {
  if (!path) return ''
  if (/^https?:\/\//i.test(path)) return path
  return `${WALLPAPER_API_BASE}${path.startsWith('/') ? '' : '/'}${path}`
}

export const wallpaperAPI = {
  listFormulas: () => wallpaperApi.get('/api/wallpaper/formulas').then((res) => res.data),
  generate: (formulaKey) =>
    wallpaperApi
      .post('/api/wallpaper/generate', {
        formula_key: formulaKey
      })
      .then((res) => res.data)
}

