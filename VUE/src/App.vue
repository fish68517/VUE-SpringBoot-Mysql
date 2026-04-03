<template>
  <div id="app" class="app-container" :class="{ 'app-container--top-nav': showTopNavLayout }">
    <Sidebar v-if="showSidebar" />

    <div class="main-wrapper">
      <AutoTopNav v-if="showTopNavLayout" />

      <main class="main-content">
        <router-view />
      </main>

      <Toast ref="toastRef" />
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'

import AutoTopNav from './components/AutoTopNav.vue'
import Sidebar from './components/Sidebar.vue'
import Toast from './components/Toast.vue'
import { useToast } from './utils/useToast'

const route = useRoute()

const isAdminRoute = computed(() => route.path.startsWith('/admin'))
const showSidebar = computed(() => isAdminRoute.value)
const showTopNavLayout = computed(() => !isAdminRoute.value)

const toastRef = ref(null)
const { setToastInstance } = useToast()

onMounted(() => {
  if (toastRef.value) {
    setToastInstance(toastRef.value)
  }
})
</script>

<style scoped>
.app-container--top-nav {
  display: block;
  min-height: 100vh;
}
</style>
