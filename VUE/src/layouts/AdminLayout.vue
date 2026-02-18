<template>
  <div class="admin-layout">
    <el-container>
      <el-header class="header">
        <div class="header-content">
          <h1>医校联动实习平台 - 系统管理</h1>
          <div class="user-menu">
            <el-dropdown @command="handleCommand">
              <span class="el-dropdown-link">
                {{ userStore.user?.username }}
                <el-icon class="el-icon--right">
                  <arrow-down />
                </el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </el-header>
      <el-container>
        <el-aside width="200px" class="sidebar">
          <el-menu :default-active="activeMenu" router>
            <el-menu-item index="/admin/organization">
              <span>组织管理</span>
            </el-menu-item>
            <el-menu-item index="/admin/user-auth">
              <span>用户认证</span>
            </el-menu-item>
            <el-menu-item index="/admin/notifications">
              <span>通知中心</span>
            </el-menu-item>
          </el-menu>
        </el-aside>
        <el-main class="main-content">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/userStore'
import { ElMessage } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => router.currentRoute.value.path)

const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/login')
  }
}
</script>

<style scoped>
.admin-layout {
  height: 100vh;
}

.header {
  background-color: #909399;
  color: white;
  display: flex;
  align-items: center;
}

.header-content {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.header h1 {
  margin: 0;
  font-size: 24px;
}

.user-menu {
  color: white;
}

.el-dropdown-link {
  cursor: pointer;
  color: white;
  display: flex;
  align-items: center;
  gap: 5px;
}

.sidebar {
  background-color: #f5f7fa;
  border-right: 1px solid #dcdfe6;
  height: 1000px;
}

.main-content {
  padding: 20px;
}
</style>
