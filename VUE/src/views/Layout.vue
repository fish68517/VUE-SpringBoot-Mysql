<template>
  <el-container class="layout-container">
    <el-aside width="220px" class="aside">
      <div class="logo">
        <h3>{{ isAdmin ? '后台管理' : '自习室预约' }}</h3>
      </div>

      <el-menu
        :default-active="route.path"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <!-- 普通用户菜单 -->
        <template v-if="!isAdmin">
          <el-menu-item index="/home">
            <el-icon><House /></el-icon>
            <span>首页</span>
          </el-menu-item>

          <el-menu-item index="/reserve">
            <el-icon><Calendar /></el-icon>
            <span>座位预约</span>
          </el-menu-item>

          <el-menu-item index="/my-reservations">
            <el-icon><List /></el-icon>
            <span>我的预约</span>
          </el-menu-item>

          <el-menu-item index="/notice">
            <el-icon><Bell /></el-icon>
            <span>系统公告</span>
          </el-menu-item>

          <el-menu-item index="/ranking">
            <el-icon><Trophy /></el-icon>
            <span>学习排行</span>
          </el-menu-item>

          <el-menu-item index="/favorites">
            <el-icon><Star /></el-icon>
            <span>我的收藏</span>
          </el-menu-item>

          <el-menu-item index="/feedback">
            <el-icon><ChatDotRound /></el-icon>
            <span>意见反馈</span>
          </el-menu-item>

          <el-menu-item index="/profile">
            <el-icon><User /></el-icon>
            <span>个人中心</span>
          </el-menu-item>
        </template>

        <!-- 管理员菜单 -->
        <template v-else>
          <el-sub-menu index="/admin">
            <template #title>
              <el-icon><Setting /></el-icon>
              <span>管理后台</span>
            </template>
            <el-menu-item index="/admin/users">用户管理</el-menu-item>
            <el-menu-item index="/admin/seats">座位管理</el-menu-item>
            <el-menu-item index="/admin/reservations">预约管理</el-menu-item>
            <el-menu-item index="/admin/statistics">数据统计</el-menu-item>
            <el-menu-item index="/admin/notices">公告管理</el-menu-item>
            <el-menu-item index="/admin/violations">违规管理</el-menu-item>
            <el-menu-item index="/admin/feedbacks">反馈管理</el-menu-item>
            <el-menu-item index="/admin/settings">系统设置</el-menu-item>
          </el-sub-menu>
        </template>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-right">
          <span class="username">
            {{ userStore.user?.nickname || userStore.user?.username }}
          </span>

          <el-dropdown>
            <el-avatar :size="36">
              {{ (userStore.user?.nickname || userStore.user?.username || '').charAt(0) }}
            </el-avatar>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="router.push('/profile')">
                  个人中心
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 角色：1-管理员，0-普通用户
const isAdmin = computed(() => Number(userStore.user?.role) === 1)

const handleLogout = () => {
  userStore.logout()
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<style scoped>
.layout-container {
  min-height: 100vh;
}

.aside {
  background-color: #304156;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #263445;
}

.logo h3 {
  color: #fff;
  margin: 0;
}

.header {
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  justify-content: flex-end;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.username {
  color: #606266;
}

.main {
  background-color: #f0f2f5;
  padding: 28px;
}
</style>