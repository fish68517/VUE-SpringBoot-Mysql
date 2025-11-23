import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
    plugins: [
        vue(),
    ],
    resolve: {
        alias: {
            // 关键修改：使用 URL 和 import.meta.url 来替代 __dirname
            '@': fileURLToPath(new URL('./src', import.meta.url))
        }
    }
})