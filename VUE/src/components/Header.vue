<template>
  <div class="header">
    <div class="header-left">
      <h1 class="page-title">{{ pageTitle }}</h1>
    </div>

    <div class="header-right">
      <div class="admin-info">
        <span class="admin-name">{{ adminName }}</span>
        <el-dropdown @command="handleCommand">
          <el-button type="primary" link>
            <el-icon><User /></el-icon>
            <span>{{ adminName }}</span>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="logout">
                <el-icon><SwitchButton /></el-icon>
                <span>退出登录</span>
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAdminStore } from '../store/index.js'
import { showSuccess } from '../utils/notification.js'
import { User, SwitchButton } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const adminStore = useAdminStore()

const pageTitle = computed(() => {
  const titles = {
    '/dashboard': '仪表板',
    '/users': '用户管理',
    '/community': '社区管理',
    '/fitness-plans': '健身计划',
    '/exercise-data': '运动数据',
    '/image-upload': '图片上传'
  }
  return titles[route.path] || '管理系统'
})

const adminName = computed(() => {
  return adminStore.adminInfo?.username || '管理员'
})

const handleCommand = (command) => {
  if (command === 'logout') {
    adminStore.logout()
    showSuccess('已安全退出登录', '退出成功')
    setTimeout(() => {
      router.push('/login')
    }, 500)
  }
}
</script>

<style scoped>
.header {
  height: 60px;
  background: #fff;
  border-bottom: 1px solid #e0e0e0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}

.header-left {
  flex: 1;
}

.page-title {
  font-size: 20px;
  font-weight: 700;
  color: #333;
  margin: 0;
  letter-spacing: -0.5px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.admin-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.admin-name {
  color: #666;
  font-size: 14px;
  font-weight: 500;
}

.header-right :deep(.el-button) {
  padding: 0;
  font-weight: 500;
  transition: all 0.3s ease;
}

.header-right :deep(.el-button:hover) {
  color: #1890ff;
}

.header-right :deep(.el-icon) {
  margin-right: 5px;
}

:deep(.el-dropdown-menu) {
  border-radius: 6px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
}

:deep(.el-dropdown-menu__item) {
  padding: 10px 16px;
  font-size: 14px;
  transition: all 0.3s ease;
}

:deep(.el-dropdown-menu__item:hover) {
  background-color: #f5f7fa;
  color: #1890ff;
}
</style>
