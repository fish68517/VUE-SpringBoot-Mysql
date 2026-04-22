<template>
  <div class="layout-shell">
    <aside class="sidebar">
      <div class="brand">
        <div class="brand-mark">PI</div>
        <div>
          <div class="brand-title">电网设备巡检系统</div>
          <div class="brand-subtitle">Spring Boot + Vue + MySQL</div>
        </div>
      </div>

      <el-menu
        router
        :default-active="$route.path"
        class="side-menu"
        background-color="transparent"
        text-color="#d9e5f2"
        active-text-color="#ffffff"
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataBoard /></el-icon>
          <span>工作台</span>
        </el-menu-item>
        <el-menu-item v-if="isAdmin" index="/users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item v-if="isAdmin" index="/devices">
          <el-icon><OfficeBuilding /></el-icon>
          <span>设备管理</span>
        </el-menu-item>
        <el-menu-item v-if="isAdmin" index="/standards">
          <el-icon><Memo /></el-icon>
          <span>巡检标准</span>
        </el-menu-item>
        <el-menu-item index="/tasks">
          <el-icon><Calendar /></el-icon>
          <span>{{ isInspector ? '我的巡检任务' : '巡检任务' }}</span>
        </el-menu-item>
        <el-menu-item index="/records">
          <el-icon><Document /></el-icon>
          <span>{{ isInspector ? '我的巡检记录' : '巡检记录' }}</span>
        </el-menu-item>
        <el-menu-item index="/defects">
          <el-icon><Warning /></el-icon>
          <span>异常缺陷</span>
        </el-menu-item>
        <el-menu-item index="/repair-orders">
          <el-icon><Tools /></el-icon>
          <span>{{ isMaintainer ? '我的维修工单' : '维修工单' }}</span>
        </el-menu-item>
        <el-menu-item index="/analytics">
          <el-icon><TrendCharts /></el-icon>
          <span>统计分析</span>
        </el-menu-item>
        <el-menu-item index="/notices">
          <el-icon><Bell /></el-icon>
          <span>公告管理</span>
        </el-menu-item>
        <el-menu-item v-if="isAdmin" index="/logs">
          <el-icon><Tickets /></el-icon>
          <span>操作日志</span>
        </el-menu-item>
      </el-menu>
    </aside>

    <main class="main-area">
      <header class="topbar page-card">
        <div>
          <div class="topbar-title">{{ pageTitle }}</div>
          <div class="topbar-subtitle">角色：{{ roleText }} | 当前用户：{{ authStore.user?.realName }}</div>
        </div>
        <div class="topbar-actions">
          <el-tag type="primary" effect="dark">{{ authStore.user?.username }}</el-tag>
          <el-button type="danger" plain @click="logout">退出登录</el-button>
        </div>
      </header>

      <section class="content-area">
        <router-view />
      </section>
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const roleTextMap = {
  ADMIN: '管理员',
  INSPECTOR: '巡检员',
  MAINTAINER: '维护员'
}

const pageTitleMap = {
  '/dashboard': '系统工作台',
  '/users': '用户管理',
  '/devices': '设备与分类管理',
  '/standards': '巡检标准管理',
  '/tasks': '巡检任务管理',
  '/records': '巡检记录',
  '/defects': '异常缺陷',
  '/repair-orders': '维修工单',
  '/analytics': '统计分析',
  '/notices': '公告管理',
  '/logs': '操作日志'
}

const isAdmin = computed(() => authStore.user?.role === 'ADMIN')
const isInspector = computed(() => authStore.user?.role === 'INSPECTOR')
const isMaintainer = computed(() => authStore.user?.role === 'MAINTAINER')
const roleText = computed(() => roleTextMap[authStore.user?.role] || '-')
const pageTitle = computed(() => pageTitleMap[route.path] || '电网设备巡检系统')

function logout() {
  authStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.layout-shell {
  display: grid;
  grid-template-columns: 280px 1fr;
  min-height: 100vh;
}

.sidebar {
  padding: 24px 20px;
  background:
    linear-gradient(180deg, rgba(6, 33, 61, 0.98) 0%, rgba(13, 66, 118, 0.96) 100%),
    linear-gradient(145deg, #0b396d, #092545);
  color: #fff;
}

.brand {
  display: flex;
  gap: 14px;
  align-items: center;
  padding: 8px 8px 24px;
}

.brand-mark {
  width: 50px;
  height: 50px;
  display: grid;
  place-items: center;
  border-radius: 16px;
  font-weight: 800;
  font-size: 20px;
  background: linear-gradient(135deg, #39c4ff, #1f7bff);
}

.brand-title {
  font-size: 18px;
  font-weight: 700;
}

.brand-subtitle {
  margin-top: 4px;
  font-size: 12px;
  color: rgba(217, 229, 242, 0.78);
}

.side-menu {
  border-right: none;
}

.side-menu :deep(.el-menu-item) {
  margin-bottom: 8px;
  border-radius: 14px;
}

.side-menu :deep(.is-active) {
  background: linear-gradient(135deg, rgba(56, 155, 255, 0.72), rgba(29, 111, 219, 0.82));
}

.main-area {
  padding: 22px;
}

.topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 18px 24px;
  margin-bottom: 20px;
}

.topbar-title {
  font-size: 24px;
  font-weight: 800;
  color: #18314d;
}

.topbar-subtitle {
  margin-top: 6px;
  color: #6682a0;
}

.topbar-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.content-area {
  min-height: calc(100vh - 128px);
}
</style>
