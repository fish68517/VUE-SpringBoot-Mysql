<template>
  <div id="app">
    <Header />
    <main class="main-content">
      <router-view />
    </main>
    <Footer />
    <Loading />
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import Header from './components/Header.vue'
import Footer from './components/Footer.vue'
import Loading from './components/Loading.vue'
import { useAuth } from './composables/useAuth'

const { restoreSession } = useAuth()

// Restore session from localStorage on app initialization
onMounted(() => {
  restoreSession()
})
</script>

<style scoped>
#app {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.main-content {
  flex: 1;
}
</style>
