import { createApp } from 'vue'
import './style.css'
import App from './App.vue'

// Import Element Plus
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

// Import Global Styles
import './assets/styles/variables.css'
import './assets/styles/global.css'
import './assets/styles/responsive.css'
import './assets/styles/animations.css'

// Import Router
import router from './router'

// Import Pinia
import pinia from './store'

const app = createApp(App)

// Register Element Plus
app.use(ElementPlus)

// Register all Element Plus icons
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// Use Router
app.use(router)

// Use Pinia
app.use(pinia)

app.mount('#app')
