<template>
  <el-container class="layout-container">
    <!-- Mobile overlay -->
    <div 
      v-if="isMobile && !appStore.isSidebarCollapsed" 
      class="sidebar-overlay"
      @click="appStore.toggleSidebar"
    ></div>
    
    <el-aside 
      :width="sidebarWidth" 
      :class="['layout-aside', 'layout-sidebar', { 'is-open': !appStore.isSidebarCollapsed, 'is-collapsed': appStore.isSidebarCollapsed }]"
    >
      <Sidebar />
    </el-aside>
    
    <el-container class="layout-main-container">
      <el-header class="layout-header">
        <Header />
      </el-header>
      
      <el-main class="layout-main">
        <slot />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, ref, onMounted, onUnmounted } from 'vue'
import { useAppStore } from '@/store/modules/app'
import Header from './Header.vue'
import Sidebar from './Sidebar.vue'

const appStore = useAppStore()
const isMobile = ref(false)

const sidebarWidth = computed(() => {
  return appStore.isSidebarCollapsed ? '64px' : '200px'
})

const checkMobile = () => {
  isMobile.value = window.innerWidth < 768
  // Auto-collapse sidebar on mobile
  if (isMobile.value && !appStore.isSidebarCollapsed) {
    appStore.toggleSidebar()
  }
}

onMounted(() => {
  checkMobile()
  window.addEventListener('resize', checkMobile)
})

onUnmounted(() => {
  window.removeEventListener('resize', checkMobile)
})
</script>

<style scoped>
.layout-container {
  min-height: 100vh;
  width: 100%;
}

.layout-aside {
  background-color: #304156;
  transition: width 0.3s ease, left 0.3s ease;
  overflow-x: hidden;
}

.layout-main-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.layout-header {
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  padding: 0;
  height: var(--header-height, 60px);
  display: flex;
  align-items: center;
}

.layout-main {
  background-color: var(--color-bg-light, #f0f2f5);
  padding: var(--spacing-lg, 20px);
  flex: 1;
  overflow-y: auto;
}

.sidebar-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 999;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

/* Desktop layout */
@media (min-width: 768px) {
  .layout-aside {
    position: fixed;
    left: 0;
    top: 0;
    bottom: 0;
    z-index: 100;
  }
  
  .layout-main-container {
    margin-left: 200px;
    transition: margin-left 0.3s ease;
  }
  
  .layout-aside.is-collapsed + .layout-main-container {
    margin-left: 64px;
  }
}

/* Mobile layout */
@media (max-width: 767px) {
  .layout-aside {
    position: fixed;
    left: -100%;
    top: 0;
    bottom: 0;
    z-index: 1000;
    width: 240px !important;
  }
  
  .layout-aside.is-open {
    left: 0;
  }
  
  .layout-main-container {
    margin-left: 0;
  }
  
  .layout-main {
    padding: var(--spacing-md, 15px);
  }
}
</style>
