<template>
  <header class="header">
    <div class="header-content">
      <!-- Logo -->
      <div class="logo-section">
        <h1 class="logo">Travel Memory</h1>
      </div>

      <!-- 主导航菜单 -->
      <nav class="nav-menu">
        <router-link to="/dashboard" class="nav-link" active-class="active">
          <el-icon><HomeFilled /></el-icon>
          <span>首页仪表盘</span>
        </router-link>
        <router-link to="/records" class="nav-link" active-class="active">
          <el-icon><DocumentCopy /></el-icon>
          <span>旅行记录</span>
        </router-link>
        <router-link to="/plans" class="nav-link" active-class="active">
          <el-icon><Calendar /></el-icon>
          <span>旅行计划</span>
        </router-link>
        <router-link to="/social" class="nav-link" active-class="active">
          <el-icon><Share /></el-icon>
          <span>社区分享</span>
        </router-link>
      </nav>

      <!-- 用户区域 -->
      <div class="user-section">
        <!-- 已登录显示用户名 -->
        <div class="user-info" v-if="userStore.user">
          <span class="username">Hi, {{ userStore.user.username }}</span>
        </div>

        <!-- 个人中心下拉菜单 -->
        <el-dropdown @command="handleCommand">
          <el-button type="primary" link class="account-btn">
            <el-icon><User /></el-icon>
            <span>我的账户</span>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">
                <el-icon><UserFilled /></el-icon>
                个人中心
              </el-dropdown-item>
              <el-dropdown-item command="logout" divided>
                <el-icon><SwitchButton /></el-icon>
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </header>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/userStore'
import { ElMessage } from 'element-plus'
import {
  HomeFilled,
  DocumentCopy,
  Calendar,
  Share,
  User,
  UserFilled,
  SwitchButton
} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const handleCommand = (command) => {
  if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'logout') {
    userStore.logout()
    ElMessage.success('退出登录成功')
    router.push('/login')
  }
}
</script>

<style scoped>
.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 30px;
  height: 64px;
  max-width: 100%;
}

.logo-section {
  display: flex;
  align-items: center;
}

.logo {
  margin: 0;
  font-size: 24px;
  font-weight: bold;
  letter-spacing: 1px;
}

.nav-menu {
  display: flex;
  gap: 40px;
  flex: 1;
  justify-content: center;
}

.nav-link {
  color: white;
  text-decoration: none;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border-radius: 8px;
  transition: all 0.3s ease;
  font-size: 15px;
  font-weight: 500;
}

.nav-link:hover {
  background-color: rgba(255, 255, 255, 0.25);
  transform: translateY(-1px);
}

.nav-link.active {
  background-color: rgba(255, 255, 255, 0.35);
  font-weight: bold;
}

.user-section {
  display: flex;
  align-items: center;
  gap: 20px;
}

.username {
  font-size: 15px;
  font-weight: 500;
}

.account-btn {
  color: white !important;
  font-size: 15px;
}

/* 移动端适配 */
@media (max-width: 992px) {
  .nav-menu {
    gap: 20px;
  }
  .nav-link span {
    font-size: 14px;
  }
}

@media (max-width: 768px) {
  .header-content {
    padding: 0 15px;
    height: auto;
    flex-direction: column;
    py: 12px;
  }

  .logo-section {
    margin-bottom: 8px;
  }

  .nav-menu {
    order: 3;
    width: 100%;
    justify-content: center;
    margin-top: 10px;
    gap: 15px;
  }

  .nav-link span {
    display: none; /* 小屏幕只显示图标 */
  }

  .user-section {
    position: absolute;
    right: 15px;
    top: 18px;
  }
}
</style>