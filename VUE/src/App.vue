<template>
  <div id="app">
    <!-- Global error notification component -->
    <ErrorNotification />

    <!-- Login/Register pages don't show layout -->
    <template v-if="isAuthPage">
      <router-view />
    </template>

    <!-- Main layout for authenticated pages -->
    <template v-else>
      <Header />
      <div class="main-container">
        <Sidebar />
        <main class="main-content">
          <router-view />
        </main>
      </div>
      <Footer />
    </template>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import Header from './components/Header.vue'
import Sidebar from './components/Sidebar.vue'
import Footer from './components/Footer.vue'
import ErrorNotification from './components/ErrorNotification.vue'
import './styles/ux-improvements.css'

const route = useRoute()

const isAuthPage = computed(() => {
  return route.path === '/login' || route.path === '/register'
})
</script>

<style scoped>
#app {
  width: 100%;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-container {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.main-content {
  flex: 1;
  overflow-y: auto;
  background-color: #f5f7fa;
  padding: 20px;
}

/* Responsive design */
@media (max-width: 768px) {
  .main-container {
    flex-direction: column;
  }

  .main-content {
    padding: 15px;
  }
}

@media (max-width: 480px) {
  .main-content {
    padding: 10px;
  }
}
</style>
