<template>
  <div class="user-layout">
    <!-- 顶部导航菜单栏 -->
    <el-header class="user-header">
      <div class="header-content">
        <div class="logo">
          <span class="logo-text">{{ $t('common.home') || '个人健康管理系统' }}</span>
        </div>
        
        <!-- 顶部菜单 -->
        <el-menu
          :default-active="activeMenu"
          mode="horizontal"
          class="top-menu"
          @select="handleMenuSelect"
        >
          <el-menu-item index="/user/profile">
            {{ $t('menu.personalInfo') || '个人信息' }}
          </el-menu-item>
          <el-menu-item index="/user/health-data">
            {{ $t('menu.healthData') || '健康数据' }}
          </el-menu-item>
          <el-menu-item index="/user/health-trend">
            {{ $t('menu.healthTrend') || '健康趋势' }}
          </el-menu-item>
          <el-menu-item index="/user/health-check">
            {{ $t('menu.healthCheck') || '常规检查' }}
          </el-menu-item>
          <el-menu-item index="/user/gender-health">
            {{ $t('menu.genderHealth') || '性别健康' }}
          </el-menu-item>
          <el-menu-item index="/user/consultation">
            {{ $t('menu.consultation') || '在线咨询' }}
          </el-menu-item>
          <el-menu-item index="/user/health-history">
            {{ $t('menu.healthHistory') || '健康历史' }}
          </el-menu-item>
        </el-menu>

        <!-- 用户操作区域 -->
        <div class="user-actions">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              {{ currentUser?.name || currentUser?.username }}
              <el-icon class="el-icon--right">
                <arrow-down />
              </el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">
                  {{ $t('common.logout') || '登出' }}
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </el-header>

    <!-- 内容区域 -->
    <el-main class="user-main">
      <router-view />
    </el-main>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authService } from '../../services/auth'
import { authAPI } from '../../services/api'

export default {
  name: 'UserLayout',
  setup() {
    const router = useRouter()
    const route = useRoute()
    const activeMenu = ref('/user/profile')
    const currentUser = computed(() => authService.getUser())

    // 初始化时设置活跃菜单
    onMounted(() => {
      updateActiveMenu()
    })

    // 监听路由变化，更新活跃菜单
    const updateActiveMenu = () => {
      const path = route.path
      if (path.startsWith('/user/')) {
        activeMenu.value = path
      }
    }

    // 菜单选择处理
    const handleMenuSelect = (index) => {
      router.push(index)
    }

    // 下拉菜单命令处理
    const handleCommand = async (command) => {
      if (command === 'logout') {
        try {
          // 调用登出API
          await authAPI.logout()
        } catch (error) {
          console.error('登出API调用失败:', error)
        } finally {
          // 清除认证信息
          authService.clearAuth()
          ElMessage.success('已登出')
          // 重定向到登录页面
          router.push('/login')
        }
      }
    }

    return {
      activeMenu,
      currentUser,
      handleMenuSelect,
      handleCommand,
      updateActiveMenu
    }
  }
}
</script>

<style scoped>
.user-layout {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f5f7fa;
}

.user-header {
  background-color: #ffffff;
  border-bottom: 1px solid #e4e7eb;
  padding: 0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.08);
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  padding: 0 20px;
  max-width: 100%;
}

.logo {
  display: flex;
  align-items: center;
  margin-right: 30px;
  min-width: 200px;
}

.logo-text {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.top-menu {
  flex: 1;
  border: none;
  background-color: transparent;
}

.top-menu :deep(.el-menu--horizontal) {
  border: none;
}

.top-menu :deep(.el-menu-item) {
  height: 60px;
  line-height: 60px;
  color: #606266;
  font-size: 14px;
}

.top-menu :deep(.el-menu-item:hover) {
  color: #409eff;
}

.top-menu :deep(.el-menu-item.is-active) {
  color: #409eff;
  border-bottom-color: #409eff;
}

.user-actions {
  display: flex;
  align-items: center;
  margin-left: 20px;
  min-width: 150px;
  justify-content: flex-end;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #606266;
  font-size: 14px;
  padding: 0 10px;
  border-radius: 4px;
  transition: all 0.3s;
}

.user-info:hover {
  background-color: #f5f7fa;
  color: #409eff;
}

.user-info .el-icon {
  margin-left: 5px;
}

.user-main {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-content {
    flex-wrap: wrap;
  }

  .logo {
    margin-right: 10px;
    min-width: auto;
  }

  .logo-text {
    font-size: 16px;
  }

  .top-menu :deep(.el-menu-item) {
    padding: 0 10px;
    font-size: 12px;
  }

  .user-actions {
    margin-left: 10px;
    min-width: auto;
  }

  .user-info {
    font-size: 12px;
    padding: 0 5px;
  }
}
</style>
