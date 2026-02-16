import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
  server: {
    port: 5173,
    open: true,
    proxy: {
      // 你原有的 API 代理应该在这里...
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      // 🌟 新增图片代理规则：凡是请求 /images 开头的，都转发给 SpringBoot
      '/images': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      }
    }
  },
})
