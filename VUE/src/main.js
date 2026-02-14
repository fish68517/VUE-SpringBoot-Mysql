import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import { useAuthStore } from './stores/authStore'
import './styles/global.css'
import './styles/layout.css'
import './styles/components.css'
import './styles/responsive.css'
import './styles/theme.css'
import './styles/pages.css'

const app = createApp(App)

const pinia = createPinia()
app.use(pinia)

// 初始化认证状态
const authStore = useAuthStore()
authStore.loadUserFromStorage()

app.use(router)

app.mount('#app')
