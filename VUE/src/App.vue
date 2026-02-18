<template>
  <div id="app">
    <Header v-if="!isAdminPage" />
    
    <div :class="{ 'admin-container': isAdminPage }">
      <AdminMenu v-if="isAdminPage" />
      
      <main class="main-content">
        <router-view />
      </main>
    </div>

    <Footer v-if="!isAdminPage" />
    
    <Loading />
  </div>
</template>

<script setup>
import { onMounted, computed } from 'vue'
import { useRoute } from 'vue-router' // 引入 useRoute
import Header from './components/Header.vue'
import Footer from './components/Footer.vue'
import AdminMenu from './components/AdminMenu.vue' // 引入 AdminMenu
import Loading from './components/Loading.vue'
import { useAuth } from './composables/useAuth'

const { restoreSession } = useAuth()
const route = useRoute() // 获取当前路由实例

/**
 * 核心逻辑修改：
 * 根据你 index.js 的配置，管理员路径都以 /admin 开头。
 * 使用 route.path.startsWith('/admin') 比检查 name 更可靠，
 * 因为它能匹配所有子路由。
 */
const isAdminPage = computed(() => {
  return route.path && route.path.startsWith('/admin')
})

onMounted(() => {
  restoreSession()
})
</script>

<style scoped>
/* 重要：清除浏览器默认边距，确保后台左右布局能铺满视口 */
:global(html, body) {
  height: 100%;
  margin: 0;
  padding: 0;
}

#app {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

/* 管理员模式下的布局：左右结构 */
.admin-container {
  display: flex;
  flex: 1;
  min-height: 100vh;     /* 侧边栏垂直铺满 */
  background: #f5f7fa;   /* 让右侧内容区与侧边栏形成层次 */
}

/* 主内容区：减少左侧空隙，并保持可滚动 */
.main-content {
  flex: 1;
  overflow-y: auto;
  padding: 20px 24px;    /* 右侧内容与侧边栏间距变小 */
  box-sizing: border-box;
}

/* 让路由页面默认占满宽度（避免被某些页面 container 居中导致左侧空白过大） */
.main-content > * {
  width: 100%;
}

/* 若页面内部有通用容器类名，可按需取消最大宽度（不影响组件本身） */
:deep(.container),
:deep(.page-container) {
  max-width: none !important;
  margin-left: 0 !important;
  margin-right: 0 !important;
}
</style>