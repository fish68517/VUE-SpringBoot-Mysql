<template>
  <el-container class="admin-layout">
    <el-header class="admin-header">
      <div class="logo">系统管理后台</div>
      <div class="user-actions">
        <el-tag type="danger" effect="dark" class="role-tag">管理员</el-tag>
    
      </div>
    </el-header>

    <el-container class="admin-body">
      <el-aside width="200px" class="admin-sidebar">
        <el-menu
          :default-active="route.path"
          class="el-menu-vertical"
          router
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409eff"
        >
          <el-menu-item index="/admin/users">
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/products">
            <span>农资产品管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/orders">
            <span>订单管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/warnings">
            <span>预警信息管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <el-main class="admin-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const goToFrontend = () => {
  router.push('/home')
}
</script>

<style scoped>
.admin-layout {
  height: 100vh; /* 撑满整个屏幕可视高度 */
  display: flex;
  flex-direction: column;
}

.admin-header {
  background-color: #242f42;
  color: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  height: 60px;
}

.admin-header .logo {
  font-size: 20px;
  font-weight: bold;
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 15px;
}

.admin-body {
  flex: 1; /* 占据剩下的所有高度 */
  overflow: hidden;
}

.admin-sidebar {
  background-color: #304156;
}

.el-menu-vertical {
  border-right: none; /* 去除菜单默认的右边框，更美观 */
  height: 100%;
}

.admin-main {
  background-color: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}
</style>