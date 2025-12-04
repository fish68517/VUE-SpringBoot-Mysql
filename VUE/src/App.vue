<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useUserStore } from './stores/user'

const userStore = useUserStore()

// 应用启动时从 localStorage 恢复用户信息
onMounted(() => {
  userStore.loadFromStorage()
})
</script>

<style scoped>
#app {
  width: 100%;
  height: 100%;
}
</style>
