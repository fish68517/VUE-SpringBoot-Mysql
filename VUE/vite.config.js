import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      // 将所有以 /images 开头的请求转发到后端
      '/images': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        // 确保路径保持不变，直接发给后端
        rewrite: (path) => path 
      }
    }
  },
  build: {
    outDir: 'dist',
    sourcemap: false
  }
})
