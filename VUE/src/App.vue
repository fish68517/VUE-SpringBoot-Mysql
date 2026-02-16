<template>
  <div id="app">
    <router-view />
    <Toast ref="toastRef" />
    <LoadingSpinner ref="loadingRef" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import Toast from './components/Toast.vue'
import LoadingSpinner from './components/LoadingSpinner.vue'

const toastRef = ref(null)
const loadingRef = ref(null)

// Expose loading and toast globally
onMounted(() => {
  window.$loading = {
    show: (text) => loadingRef.value?.show(text),
    hide: () => loadingRef.value?.hide()
  }
})
</script>

<style scoped>
#app {
  width: 100%;
  height: 100%;
}
</style>
