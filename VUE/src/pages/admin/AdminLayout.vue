<template>
  <el-container class="admin-layout">
    <el-aside width="240px" class="admin-aside">
      <div class="admin-logo">
        <h2>音乐节后台</h2>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="admin-menu"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
        router
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><Odometer /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        
        <el-menu-item index="/admin/users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>

        <el-menu-item index="/admin/tickets">
          <el-icon><Ticket /></el-icon>
          <span>票务管理</span>
        </el-menu-item>

        <el-menu-item index="/admin/products">
          <el-icon><Goods /></el-icon>
          <span>商品管理</span>
        </el-menu-item>

        <el-menu-item index="/admin/orders">
          <el-icon><List /></el-icon>
          <span>商品订单</span>
        </el-menu-item>

        <el-menu-item index="/admin/content">
          <el-icon><Document /></el-icon>
          <span>内容管理</span>
        </el-menu-item>

        <el-menu-item index="/admin/tasks">
          <el-icon><MapLocation /></el-icon>
          <span>打卡任务管理</span>
        </el-menu-item>

        <el-menu-item index="/admin/points">
          <el-icon><Present /></el-icon>
          <span>积分商城管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="admin-header">
        <div class="header-left">
          <span>欢迎回来，管理员</span>
        </div>
        <div class="header-right">
          <el-button type="danger" plain size="small" @click="handleLogout">退出登录</el-button>
        </div>
      </el-header>

      <el-main class="admin-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import {
  Odometer, User, Ticket, Goods, List, 
  Document, MapLocation, Present
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.admin-layout {
  height: 100vh;
  width: 100vw;
}

.admin-aside {
  background-color: #304156;
  color: white;
  display: flex;
  flex-direction: column;
}

.admin-logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #2b2f3a;
}

.admin-logo h2 {
  margin: 0;
  font-size: 20px;
  color: white;
}

.admin-menu {
  border-right: none;
  flex: 1;
}

.admin-header {
  background-color: white;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 60px;
}

.admin-main {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>