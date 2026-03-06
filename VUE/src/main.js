import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import './styles/global.css'
import './styles/layout.css'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'
import { vPermission } from './directives/vPermission'
import { vRole } from './directives/vRole'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(ElementPlus)

// Register permission directives
app.directive('permission', vPermission)
app.directive('role', vRole)

app.mount('#app')
