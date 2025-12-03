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
  width: 100%; /* 使用 100% 避免 100vw 可能导致的滚动条 */
  display: flex; /* 显式声明 flex */
  overflow: hidden; /* 防止整体溢出 */
}

.layout-aside {
  background-color: #304156;
  color: white;
  height: 100%;
  transition: width 0.3s; /* 添加过渡动画 */
  flex-shrink: 0; /* 防止侧边栏被挤压 */
  overflow-x: hidden;
}

/* 核心修复样式 */
.layout-main-container {
  flex: 1; /* 关键：让它自动填满剩余宽度 */
  display: flex;
  flex-direction: column;
  min-width: 0; /* 关键：防止 flex 子元素内容过宽导致溢出 */
  height: 100vh;
  overflow: hidden;
}

.layout-header {
  padding: 0; /* 关键：移除 Element 默认 padding，由 Header 组件内部控制 */
  height: 60px; /* 固定高度 */
  border-bottom: 1px solid #e6e6e6;
  background-color: #fff;
}

.layout-main {
  background-color: #f5f7fa;
  padding: 20px;
  flex: 1; /* 占满垂直剩余空间 */
  overflow-y: auto; /* 内容滚动 */
}
</style>