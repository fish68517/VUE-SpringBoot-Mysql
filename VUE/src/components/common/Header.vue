<template>
  <div class="header-container">
    <div class="header-left">
      <el-button 
        :icon="Fold" 
        circle 
        @click="toggleSidebar"
        class="sidebar-toggle mobile-menu-toggle"
      />
      <div class="logo-section">
       
        <span class="app-name">小鲨鱼健身</span>
      </div>
    </div>

    <div class="header-center">
      <SearchBar @search="handleSearch" />
    </div>

    <div class="header-right">
      <el-button :icon="Bell" circle class="notification-btn" />
      
      <el-dropdown @command="handleCommand" class="user-dropdown">
        <div class="user-info">
          <el-avatar :src="userAvatar" :size="36">
            <el-icon><User /></el-icon>
          </el-avatar>
          <span class="username">{{ username }}</span>
          <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">
              <el-icon><User /></el-icon>
              个人资料
            </el-dropdown-item>
            <el-dropdown-item divided command="logout">
              <el-icon><SwitchButton /></el-icon>
              退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/store/modules/auth'
import { useAppStore } from '@/store/modules/app'
import { 
  Fold, 
  Bell, 
  User, 
  ArrowDown, 
  SwitchButton 
} from '@element-plus/icons-vue'
import SearchBar from './SearchBar.vue'
import { showSuccess, showConfirm } from '@/utils/feedback'

const router = useRouter()
const authStore = useAuthStore()
const appStore = useAppStore()

const username = computed(() => authStore.currentUser?.username || '用户')
const userAvatar = computed(() => authStore.currentUser?.avatar || '')

const toggleSidebar = () => {
  appStore.toggleSidebar()
}

const handleSearch = (searchData) => {
  router.push({
    path: '/search',
    query: {
      q: searchData.query,
      type: searchData.type
    }
  })
}

const handleCommand = async (command) => {
  if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'logout') {
    try {
      await showConfirm(
        'Are you sure you want to logout?',
        'Confirm Logout',
        {
          confirmText: 'Logout',
          cancelText: 'Cancel',
          type: 'warning'
        }
      )
      
      authStore.logout()
      showSuccess('Logged out successfully')
      router.push('/login')
    } catch {
      // User cancelled
    }
  }
}
</script>

<style scoped>
.header-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  height: 100%;
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.sidebar-toggle {
  border: none;
}

.logo-section {
  display: flex;
  align-items: center;
  gap: 10px;
}

.logo {
  height: 32px;
  width: auto;
}

.app-name {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.header-center {
  flex: 1;
  max-width: 600px;
  margin: 0 20px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.notification-btn {
  border: none;
}

.user-dropdown {
  cursor: pointer;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 5px 10px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f5f7fa;
}

.username {
  font-size: 14px;
  color: #303133;
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.dropdown-icon {
  font-size: 12px;
  color: #909399;
}

/* Responsive design */
@media (max-width: 767px) {
  .header-container {
    padding: 0 var(--spacing-sm, 10px);
  }
  
  .header-center {
    display: none;
  }
  
  .app-name {
    font-size: var(--font-size-base, 16px);
  }
  
  .username {
    display: none;
  }
  
  .mobile-menu-toggle {
    display: block !important;
  }
  
  .header-right {
    gap: var(--spacing-sm, 8px);
  }
}

@media (min-width: 768px) and (max-width: 1023px) {
  .header-center {
    max-width: 400px;
  }
}

@media (min-width: 1024px) {
  .header-center {
    max-width: 600px;
  }
}
</style>
