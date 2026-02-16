import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import './styles/main.css'
import http from './utils/http'
import { applyTheme } from './styles/theme'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(ElementPlus)

// Register http client globally
app.config.globalProperties.$http = http

// Apply blue theme configuration
applyTheme()

app.mount('#app')
