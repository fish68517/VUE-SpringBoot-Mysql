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

<!-- Layout.vue 的建议样式 -->
<style scoped>
.layout-container {
  height: 100vh;
  width: 100vw; /* 确保容器占满视窗宽度 */
}

.el-aside {
  background-color: #304156;
  color: white;
  /* width: 200px;  <-- 侧边栏宽度通常是固定的 */
}

.el-header {
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  /* padding: 0 20px; */
}

.el-main {
  background-color: #f5f7fa; /* 灰色背景 */
  padding: 20px; /* 给内容留一点边距，但不要太大 */
  height: 100%;
  overflow-y: auto; /* 内容过多时只在 main 区域滚动 */
}
</style>