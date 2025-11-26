import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

import App from './App.vue'
import router from './router'
import { errorHandler } from './utils/errorHandler'

const app = createApp(App)
const pinia = createPinia()

// Register lazy loading directive with Intersection Observer
app.directive('lazy', {
  mounted(el) {
    const imageUrl = el.getAttribute('v-lazy')
    if (!imageUrl) return

    // Set placeholder
    el.classList.add('lazy-loading')
    el.style.backgroundColor = '#f0f0f0'

    // Use Intersection Observer for better performance
    const observer = new IntersectionObserver((entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          const img = new Image()
          img.onload = () => {
            el.src = imageUrl
            el.classList.remove('lazy-loading')
            el.classList.add('lazy-loaded')
            el.style.backgroundColor = 'transparent'
            observer.unobserve(el)
          }
          img.onerror = () => {
            el.src = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="100" height="100"%3E%3Crect fill="%23f0f0f0" width="100" height="100"/%3E%3Ctext x="50" y="50" text-anchor="middle" dy=".3em" fill="%23999" font-size="12"%3EFailed to load%3C/text%3E%3C/svg%3E'
            el.classList.remove('lazy-loading')
            el.classList.add('lazy-error')
            observer.unobserve(el)
          }
          img.src = imageUrl
        }
      })
    }, {
      rootMargin: '50px'
    })

    observer.observe(el)
  }
})

// Global error handler for Vue errors
app.config.errorHandler = (err, _instance, info) => {
  errorHandler.logError(err, `Vue Error: ${info}`)
  
  // Show user-friendly error message
  const { useErrorStore } = require('./stores/errorStore')
  const errorStore = useErrorStore()
  errorStore.showError('An unexpected error occurred. Please try again.')
}

// Global warning handler
app.config.warnHandler = (msg, _instance, _trace) => {
  // Only log warnings in development
  if (process.env.NODE_ENV === 'development') {
    console.warn('Vue warning:', msg)
  }
}

// Make error handler available globally
app.config.globalProperties.$errorHandler = errorHandler

app.use(pinia)
app.use(router)
app.use(ElementPlus)

app.mount('#app')
