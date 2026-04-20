<template>
  <header class="header">
    <div class="header-content">
      <div class="logo-section">
        <h1 class="logo">旅行记忆</h1>
      </div>

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

      <div class="user-section">
        <el-dropdown @command="handleCommand">
          <el-button type="primary" link class="account-btn">
            <el-icon><User /></el-icon>
            <span>{{ accountLabel }}</span>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item v-if="false" command="profile">
                <el-icon><UserFilled /></el-icon>
                个人中心
              </el-dropdown-item>
              <el-dropdown-item v-if="userStore.isAdmin" command="admin">
                <el-icon><Setting /></el-icon>
                管理后台
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
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Calendar,
  DocumentCopy,
  HomeFilled,
  Setting,
  Share,
  SwitchButton,
  User,
  UserFilled
} from '@element-plus/icons-vue'
import { useUserStore } from '../stores/userStore'

const router = useRouter()
const userStore = useUserStore()

const accountLabel = computed(() => userStore.user?.username || '我的账户')

const handleCommand = (command) => {
  if (command === 'profile') {
    router.push('/profile')
    return
  }

  if (command === 'admin') {
    router.push('/admin')
    return
  }

  if (command === 'logout') {
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

.account-btn {
  color: white !important;
  font-size: 15px;
  font-weight: 600;
}

@media (max-width: 992px) {
  .nav-menu {
    gap: 20px;
  }
}

@media (max-width: 768px) {
  .header-content {
    padding: 0 15px;
    height: auto;
    flex-direction: column;
    padding-top: 12px;
    padding-bottom: 12px;
  }

  .nav-menu {
    order: 3;
    width: 100%;
    justify-content: center;
    margin-top: 10px;
    gap: 15px;
  }

  .nav-link span {
    display: none;
  }

  .user-section {
    position: absolute;
    right: 15px;
    top: 18px;
  }
}
</style>
