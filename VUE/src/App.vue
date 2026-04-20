<template>
  <div id="app">
    <ErrorNotification />

    <template v-if="isAuthPage">
      <router-view />
    </template>

    <template v-else-if="isAdminPage">
      <AdminHeader />
      <div class="main-container">
        <AdminSidebar />
        <main class="main-content admin-content">
          <router-view />
        </main>
      </div>
    </template>

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
import AdminHeader from './components/AdminHeader.vue'
import AdminSidebar from './components/AdminSidebar.vue'
import ErrorNotification from './components/ErrorNotification.vue'
import Footer from './components/Footer.vue'
import Header from './components/Header.vue'
import Sidebar from './components/Sidebar.vue'
import './styles/ux-improvements.css'

const route = useRoute()

const isAuthPage = computed(() => ['/login', '/register'].includes(route.path))
const isAdminPage = computed(() => route.path.startsWith('/admin'))
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

.admin-content {
  background:
    radial-gradient(circle at top right, rgba(58, 124, 165, 0.12), transparent 32%),
    linear-gradient(180deg, #f4f8fb 0%, #eef3f7 100%);
}

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
