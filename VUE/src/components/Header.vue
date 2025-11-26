<template>
  <header class="header">
    <div class="header-content">
      <div class="logo-section">
        <h1 class="logo">✈️ Travel Memory</h1>
      </div>

      <nav class="nav-menu">
        <router-link to="/dashboard" class="nav-link" active-class="active">
          <el-icon><HomeFilled /></el-icon>
          <span>Dashboard</span>
        </router-link>
        <router-link to="/records" class="nav-link" active-class="active">
          <el-icon><DocumentCopy /></el-icon>
          <span>Records</span>
        </router-link>
        <router-link to="/plans" class="nav-link" active-class="active">
          <el-icon><Calendar /></el-icon>
          <span>Plans</span>
        </router-link>
        <router-link to="/social" class="nav-link" active-class="active">
          <el-icon><Share /></el-icon>
          <span>Social</span>
        </router-link>
      </nav>

      <div class="user-section">
        <div class="user-info" v-if="userStore.user">
          <span class="username">{{ userStore.user.username }}</span>
        </div>
        <el-dropdown @command="handleCommand">
          <el-button type="primary" link>
            <el-icon><User /></el-icon>
            <span>Account</span>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">
                <el-icon><UserFilled /></el-icon>
                <span>Profile</span>
              </el-dropdown-item>
              <el-dropdown-item command="logout" divided>
                <el-icon><SwitchButton /></el-icon>
                <span>Logout</span>
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
    ElMessage.success('Logged out successfully')
    router.push('/login')
  }
}
</script>

<style scoped>
.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 0;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  height: 60px;
  max-width: 100%;
}

.logo-section {
  display: flex;
  align-items: center;
  min-width: 200px;
}

.logo {
  margin: 0;
  font-size: 20px;
  font-weight: bold;
  white-space: nowrap;
}

.nav-menu {
  display: flex;
  gap: 30px;
  flex: 1;
  justify-content: center;
  align-items: center;
}

.nav-link {
  color: white;
  text-decoration: none;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border-radius: 4px;
  transition: all 0.3s ease;
  font-size: 14px;
}

.nav-link:hover {
  background-color: rgba(255, 255, 255, 0.2);
}

.nav-link.active {
  background-color: rgba(255, 255, 255, 0.3);
  font-weight: bold;
}

.user-section {
  display: flex;
  align-items: center;
  gap: 15px;
  min-width: 200px;
  justify-content: flex-end;
}

.user-info {
  display: flex;
  align-items: center;
}

.username {
  font-size: 14px;
  margin-right: 10px;
}

/* Responsive design */
@media (max-width: 768px) {
  .header-content {
    flex-wrap: wrap;
    height: auto;
    padding: 10px 15px;
  }

  .logo-section {
    width: 100%;
    margin-bottom: 10px;
  }

  .logo {
    font-size: 18px;
  }

  .nav-menu {
    width: 100%;
    gap: 15px;
    margin-bottom: 10px;
  }

  .nav-link {
    font-size: 12px;
    padding: 6px 10px;
  }

  .nav-link span {
    display: none;
  }

  .user-section {
    width: 100%;
    justify-content: flex-end;
  }
}

@media (max-width: 480px) {
  .nav-menu {
    gap: 10px;
  }

  .logo {
    font-size: 16px;
  }
}
</style>
