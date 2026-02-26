<template>
  <el-header class="top-navigation">
    <div class="nav-content">
      <div class="nav-left">
        <h1 class="app-title" @click="handleTitleClick">广州旅游管理系统</h1>
        <!-- 顶部导航菜单（未登录时显示） -->
        <nav v-if="!isLoggedIn" class="top-menu">
          <router-link to="/attractions" class="menu-link">景点</router-link>
          <router-link to="/hotels" class="menu-link">酒店</router-link>
          <router-link to="/products" class="menu-link">商品</router-link>
          <router-link to="/routes" class="menu-link">路线</router-link>
        </nav>
      </div>

      <!-- 用户操作区 -->
      <div class="nav-right">
        <el-button 
          v-if="!isLoggedIn" 
          type="primary" 
          @click="handleLogin"
        >
          登录
        </el-button>
        <el-button 
          v-if="!isLoggedIn" 
          @click="handleRegister"
        >
          注册
        </el-button>
        <el-dropdown v-if="isLoggedIn" class="user-menu">
          <span class="user-info">
            <el-icon><user-filled /></el-icon>
            {{ username }}
            <el-icon class="el-icon--right"><arrow-down /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="handlePersonalCenter">
                <el-icon><user /></el-icon>
                个人中心
              </el-dropdown-item>
              <el-dropdown-item @click="handleProfile">
                <el-icon><user /></el-icon>
                个人资料
              </el-dropdown-item>
              <el-dropdown-item 
                v-if="isAdmin"
                @click="handleAdminPanel"
              >
                <el-icon><setting /></el-icon>
                管理员面板
              </el-dropdown-item>
              <el-dropdown-item divided @click="handleLogout">
                <el-icon><switch-button /></el-icon> 退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </el-header>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import {
  UserFilled,
  User,
  Setting,
  SwitchButton, // <--- 把 Logout 改成 SwitchButton
  ArrowDown
} from '@element-plus/icons-vue'

const router = useRouter()

const props = defineProps({
  isLoggedIn: {
    type: Boolean,
    default: false
  },
  username: {
    type: String,
    default: ''
  },
  userRole: {
    type: String,
    default: 'tourist'
  }
})

const emit = defineEmits(['logout'])

const isAdmin = computed(() => props.userRole === 'admin')

const handleTitleClick = () => {
  router.push('/')
}

const handleLogin = () => {
  router.push('/login')
}

const handleRegister = () => {
  router.push('/register')
}

const handleProfile = () => {
  router.push('/profile')
}

const handlePersonalCenter = () => {
  router.push('/personal-center')
}

const handleAdminPanel = () => {
  router.push('/admin/users')
}

const handleLogout = () => {
  emit('logout')
}
</script>

<style scoped>
.top-navigation {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  color: white;
  padding: 0 20px;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  height: 60px;
  flex-wrap: wrap;
}

.nav-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  gap: 10px;
}

.nav-left {
  display: flex;
  align-items: center;
  gap: 40px;
  min-width: 0;
  flex: 1;
}

.app-title {
  margin: 0;
  font-size: 24px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
  flex-shrink: 0;
}

.app-title:hover {
  transform: scale(1.05);
}

.top-menu {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.menu-link {
  color: white;
  text-decoration: none;
  font-size: 14px;
  padding: 8px 12px;
  border-radius: 4px;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.menu-link:hover {
  background-color: rgba(255, 255, 255, 0.2);
}

.menu-link.router-link-active {
  background-color: rgba(255, 255, 255, 0.3);
  font-weight: bold;
}

.nav-right {
  display: flex;
  gap: 10px;
  align-items: center;
  flex-shrink: 0;
}

.user-menu {
  cursor: pointer;
}

.user-info {
  color: white;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.user-info:hover {
  opacity: 0.8;
}

/* Tablet screens */
@media (max-width: 1024px) {
  .nav-left {
    gap: 20px;
  }

  .app-title {
    font-size: 20px;
  }

  .top-menu {
    gap: 15px;
  }

  .menu-link {
    font-size: 13px;
    padding: 6px 10px;
  }
}

/* Tablet and smaller */
@media (max-width: 768px) {
  .top-navigation {
    padding: 0 12px;
    height: auto;
    min-height: 50px;
  }

  .nav-left {
    gap: 15px;
    flex: 1;
  }

  .app-title {
    font-size: 18px;
  }

  .top-menu {
    gap: 8px;
    order: 3;
    width: 100%;
    margin-top: 8px;
  }

  .menu-link {
    font-size: 12px;
    padding: 5px 8px;
  }

  .nav-right {
    gap: 8px;
  }

  .user-info {
    font-size: 13px;
    gap: 6px;
  }
}

/* Mobile devices */
@media (max-width: 480px) {
  .top-navigation {
    padding: 0 10px;
    height: auto;
    min-height: 45px;
  }

  .nav-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .nav-left {
    width: 100%;
    gap: 10px;
    flex-direction: column;
  }

  .app-title {
    font-size: 16px;
    width: 100%;
  }

  .top-menu {
    display: none;
  }

  .nav-right {
    width: 100%;
    justify-content: flex-start;
  }

  .user-info {
    font-size: 12px;
    gap: 4px;
  }

  .user-info :deep(.el-icon) {
    font-size: 14px;
  }
}

/* Extra small devices */
@media (max-width: 360px) {
  .app-title {
    font-size: 14px;
  }

  .user-info {
    font-size: 11px;
  }
}
</style>
