import { defineConfig } from 'vite'
import uni from '@dcloudio/vite-plugin-uni'
export default defineConfig({
  plugins: [
    uni(),
    {
      name: 'water-local-data',
      enforce: 'pre',
      transform(source, id) {
        // App service is compiled by a nested Vite build. Remove its unused
        // provider expression before either build substitutes injected values.
        if (/[/\\]uni-cloud[/\\]dist[/\\]/.test(id)) {
          return { code: source.replace(/process\.env\.UNI_CLOUD_PROVIDER/g, JSON.stringify('')), map: null }
        }
      },
      configResolved(config) {
        // HBuilderX injects its associated space after the config file loads.
        // This project uses local repositories, so do not ship unused cloud credentials.
        process.env.UNI_CLOUD_SPACES = ''
        process.env.UNI_CLOUD_PROVIDER = ''
        config.define['process.env.UNI_CLOUD_PROVIDER'] = JSON.stringify('')
        console.info('[water] Build uses local business data; cloud space configuration excluded.')
      },
    },
  ],
  server: { host: '127.0.0.1', port: 5173, strictPort: true },
  resolve: { alias: { '@': process.cwd() } },
  build: { sourcemap: false },
})
