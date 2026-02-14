<template>
  <div id="app" class="app-container">
    <Sidebar />
    <div class="main-wrapper">
      <main class="main-content">
        <router-view />
      </main>
      <Footer />
    </div>
    <Toast ref="toastRef" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import Sidebar from './components/Sidebar.vue'
import Footer from './components/Footer.vue'
import Toast from './components/Toast.vue'
import { useToast } from './utils/useToast'

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
