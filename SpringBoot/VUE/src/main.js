import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import i18n from './i18n'

const app = createApp(App)

// 使用 Pinia 状态管理
app.use(createPinia())

// 使用 Vue Router
app.use(router)

// 使用 Element Plus UI 库，配置中文语言
app.use(ElementPlus, {
  locale: zhCn
})

// 全局属性 - 国际化
app.config.globalProperties.$t = i18n.t

app.mount('#app')
