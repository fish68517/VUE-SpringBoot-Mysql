import { defineConfig } from 'vite';
import uni from '@dcloudio/vite-plugin-uni';
export default defineConfig({
  plugins: [(uni.default || uni)()],
  server: { host: '127.0.0.1' },
  build: { target: 'es2022' },
  css: { preprocessorOptions: { scss: { silenceDeprecations: ['legacy-js-api'] } } }
});
