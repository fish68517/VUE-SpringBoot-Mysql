<template>
  <div id="app" class="app-container">
    <!-- 登录/注册页不显示侧边栏 -->
    <Sidebar v-if="showLayout" />

    <div class="main-wrapper">
      <main class="main-content">
        <router-view />
      </main>

      <!-- 登录/注册页不显示页脚 -->
      <!-- <Footer v-if="showLayout" /> -->
    </div>

    <Toast ref="toastRef" />
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'

import Sidebar from './components/Sidebar.vue'
import Footer from './components/Footer.vue'
import Toast from './components/Toast.vue'
import { useToast } from './utils/useToast'

const route = useRoute()

/**
 * 是否展示“主布局”（Sidebar + Footer）
 * 这里按 path 判断最稳：不依赖路由 name 是否重复
 * 你当前路由可能存在 '/'、'/login'、'/register'
 */
const showLayout = computed(() => {
  const hidePaths = ['/', '/login', '/register']
  return !hidePaths.includes(route.path)
})

// Toast 注入逻辑保持不变
const toastRef = ref(null)
const { setToastInstance } = useToast()

onMounted(() => {
  if (toastRef.value) {
    setToastInstance(toastRef.value)
  }
})
</script>

<style scoped>
/* App 容器样式已在 layout.css 中定义 */
</style>
