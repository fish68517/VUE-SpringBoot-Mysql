<template>
  <div class="doctor-layout">
    <!-- 侧边导航菜单栏 -->
    <el-aside class="doctor-aside">
      <div class="aside-header">
        <div class="logo">
          <span class="logo-text">{{ $t('common.home') || '个人健康管理系统' }}</span>
        </div>
      </div>

      <!-- 侧边菜单 -->
      <el-menu
        :default-active="activeMenu"
        class="side-menu"
        @select="handleMenuSelect"
      >
        <el-menu-item index="/doctor/patients">
          <span>{{ $t('menu.patientList') || '患者列表' }}</span>
        </el-menu-item>
        <el-menu-item index="/doctor/patient/:id">
          <span>{{ $t('menu.patientRecord') || '患者档案' }}</span>
        </el-menu-item>
        <el-menu-item index="/doctor/data-review">
          <span>{{ $t('menu.dataReview') || '数据审核' }}</span>
        </el-menu-item>
        <el-menu-item index="/doctor/health-advice">
          <span>{{ $t('menu.healthAdvice') || '健康建议' }}</span>
        </el-menu-item>
      </el-menu>

      <!-- 医师操作区域 -->
      <div class="aside-footer">
        <el-dropdown @command="handleCommand" class="doctor-dropdown">
          <span class="doctor-info">
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
    </el-aside>

    <!-- 主内容区域 -->
    <div class="doctor-main-wrapper">
      <el-header class="doctor-header">
        <div class="header-title">
          {{ getPageTitle() }}
        </div>
      </el-header>

      <el-main class="doctor-main">
        <router-view />
      </el-main>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authService } from '../../services/auth'
import { authAPI } from '../../services/api'

export default {
  name: 'DoctorLayout',
  setup() {
    const router = useRouter()
    const route = useRoute()
    const activeMenu = ref('/doctor/patients')
    const currentUser = computed(() => authService.getUser())

    // 页面标题映射
    const pageTitles = {
      '/doctor/patients': '患者列表',
      '/doctor/patient/:id': '患者档案',
      '/doctor/data-review': '数据审核',
      '/doctor/health-advice': '健康建议'
    }

    // 初始化时设置活跃菜单
    onMounted(() => {
      updateActiveMenu()
    })

    // 监听路由变化，更新活跃菜单
    watch(() => route.path, () => {
      updateActiveMenu()
    })

    // 更新活跃菜单
    const updateActiveMenu = () => {
      const path = route.path
      if (path.startsWith('/doctor/')) {
        // 处理动态路由 /doctor/patient/:id
        if (path.startsWith('/doctor/patient/')) {
          activeMenu.value = '/doctor/patient/:id'
        } else {
          activeMenu.value = path
        }
      }
    }

    // 获取页面标题
    const getPageTitle = () => {
      const path = route.path
      if (path.startsWith('/doctor/patient/')) {
        return pageTitles['/doctor/patient/:id']
      }
      return pageTitles[path] || '医师端'
    }

    // 菜单选择处理
    const handleMenuSelect = (index) => {
      // 不处理患者档案的直接导航，需要通过患者列表选择
      if (index === '/doctor/patient/:id') {
        return
      }
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
      getPageTitle
    }
  }
}
</script>

<style scoped>
.doctor-layout {
  display: flex;
  height: 100vh;
  background-color: #f5f7fa;
}

.doctor-aside {
  width: 250px;
  background-color: #ffffff;
  border-right: 1px solid #e4e7eb;
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 4px rgba(0, 0, 0, 0.08);
}

.aside-header {
  padding: 20px;
  border-bottom: 1px solid #e4e7eb;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo {
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-text {
  font-size: 14px;
  font-weight: bold;
  color: #333;
  text-align: center;
}

.side-menu {
  flex: 1;
  border: none;
  background-color: transparent;
  overflow-y: auto;
}

.side-menu :deep(.el-menu) {
  border: none;
}

.side-menu :deep(.el-menu-item) {
  height: 50px;
  line-height: 50px;
  color: #606266;
  font-size: 14px;
  padding: 0 20px;
}

.side-menu :deep(.el-menu-item:hover) {
  background-color: #f5f7fa;
  color: #409eff;
}

.side-menu :deep(.el-menu-item.is-active) {
  background-color: #e6f7ff;
  color: #409eff;
  border-left: 3px solid #409eff;
  padding-left: 17px;
}

.aside-footer {
  padding: 15px 20px;
  border-top: 1px solid #e4e7eb;
  display: flex;
  align-items: center;
  justify-content: center;
}

.doctor-dropdown {
  width: 100%;
}

.doctor-info {
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #606266;
  font-size: 14px;
  padding: 8px 12px;
  border-radius: 4px;
  transition: all 0.3s;
  width: 100%;
  text-align: center;
}

.doctor-info:hover {
  background-color: #f5f7fa;
  color: #409eff;
}

.doctor-info .el-icon {
  margin-left: 5px;
}

.doctor-main-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.doctor-header {
  background-color: #ffffff;
  border-bottom: 1px solid #e4e7eb;
  padding: 0 20px;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.08);
}

.header-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.doctor-main {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .doctor-aside {
    width: 200px;
  }

  .aside-header {
    padding: 15px;
  }

  .logo-text {
    font-size: 12px;
  }

  .side-menu :deep(.el-menu-item) {
    height: 45px;
    line-height: 45px;
    font-size: 12px;
    padding: 0 15px;
  }

  .side-menu :deep(.el-menu-item.is-active) {
    padding-left: 12px;
  }

  .doctor-header {
    padding: 0 15px;
  }

  .header-title {
    font-size: 16px;
  }

  .doctor-main {
    padding: 15px;
  }
}
</style>
