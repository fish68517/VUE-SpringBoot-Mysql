<template>
  <div class="sidebar-container">
    <el-menu
      :default-active="activeRoute"
      :collapse="isCollapsed"
      :unique-opened="true"
      background-color="#304156"
      text-color="#bfcbd9"
      active-text-color="#409eff"
      router
      class="sidebar-menu"
    >
      <!-- User Menu Items -->
      <template v-if="false">
        <el-menu-item index="/home">
          <el-icon><HomeFilled /></el-icon>
          <template #title>首页</template>
        </el-menu-item>

        <el-menu-item index="/resources">
          <el-icon><Reading /></el-icon>
          <template #title>健身资源</template>
        </el-menu-item>

        <el-menu-item index="/my-collections">
          <el-icon><Star /></el-icon>
          <template #title>我的收藏</template>
        </el-menu-item>

        <el-menu-item index="/training-plans">
          <el-icon><Calendar /></el-icon>
          <template #title>训练计划</template>
        </el-menu-item>

        <el-menu-item index="/community">
          <el-icon><ChatDotRound /></el-icon>
          <template #title>社区动态</template>
        </el-menu-item>

        <el-menu-item index="/checkin">
          <el-icon><CircleCheck /></el-icon>
          <template #title>每日打卡</template>
        </el-menu-item>

        <el-menu-item index="/diet">
          <el-icon><Food /></el-icon>
          <template #title>饮食记录</template>
        </el-menu-item>
      </template>

      <!-- Coach Menu Items -->
      <template v-if="isCoach">
        <el-divider class="menu-divider" />
        
        <el-sub-menu index="coach">
          <template #title>
            <el-icon><User /></el-icon>
            <span>教练中心</span>
          </template>
          
          <el-menu-item index="/coach/dashboard">
            <el-icon><DataAnalysis /></el-icon>
            <template #title>教练面板</template>
          </el-menu-item>

          <el-menu-item index="/coach/students">
            <el-icon><UserFilled /></el-icon>
            <template #title>学员管理</template>
          </el-menu-item>

          <el-menu-item index="/coach/training-plans/create">
            <el-icon><EditPen /></el-icon>
            <template #title>创建计划</template>
          </el-menu-item>

          <el-menu-item index="/coach/analytics">
            <el-icon><TrendCharts /></el-icon>
            <template #title>数据分析</template>
          </el-menu-item>

          <el-menu-item index="/coach/content">
            <el-icon><Document /></el-icon>
            <template #title>我的内容</template>
          </el-menu-item>
        </el-sub-menu>
      </template>

      <!-- Admin Menu Items -->
      <template v-if="isAdmin">
        <el-divider class="menu-divider" />
        
        <el-sub-menu index="admin">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>管理中心</span>
          </template>
          
          <el-menu-item index="/admin/dashboard">
            <el-icon><Odometer /></el-icon>
            <template #title>管理面板</template>
          </el-menu-item>

          <el-menu-item index="/admin/users">
            <el-icon><User /></el-icon>
            <template #title>用户管理</template>
          </el-menu-item>

          <el-menu-item index="/admin/resources">
            <el-icon><Files /></el-icon>
            <template #title>资源管理</template>
          </el-menu-item>

          <el-menu-item index="/admin/moderation">
            <el-icon><View /></el-icon>
            <template #title>内容审核</template>
          </el-menu-item>
        </el-sub-menu>
      </template>
    </el-menu>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/store/modules/auth'
import { useAppStore } from '@/store/modules/app'
import {
  HomeFilled,
  Reading,
  Star,
  Calendar,
  ChatDotRound,
  CircleCheck,
  Food,
  User,
  UserFilled,
  DataAnalysis,
  EditPen,
  TrendCharts,
  Document,
  Setting,
  Odometer,
  Files,
  View
} from '@element-plus/icons-vue'

const route = useRoute()
const authStore = useAuthStore()
const appStore = useAppStore()

const activeRoute = computed(() => route.path)
const isCollapsed = computed(() => appStore.isSidebarCollapsed)

const isRegularUser = computed(() => authStore.userRole === 'user')
const isCoach = computed(() => authStore.isCoach)
const isAdmin = computed(() => authStore.isAdmin)
</script>

<style scoped>
.sidebar-container {
  height: 100%;
  overflow-y: auto;
  overflow-x: hidden;
}

.sidebar-menu {
  border-right: none;
  height: 100%;
}

.menu-divider {
  margin: 10px 0;
  background-color: #434a54;
}

/* Custom scrollbar */
.sidebar-container::-webkit-scrollbar {
  width: 6px;
}

.sidebar-container::-webkit-scrollbar-thumb {
  background-color: #434a54;
  border-radius: 3px;
}

.sidebar-container::-webkit-scrollbar-track {
  background-color: #304156;
}

/* Collapsed state adjustments */
:deep(.el-menu--collapse) {
  width: 64px;
}

:deep(.el-menu-item),
:deep(.el-sub-menu__title) {
  height: 50px;
  line-height: 50px;
}

:deep(.el-menu-item.is-active) {
  background-color: #263445 !important;
}

:deep(.el-menu-item:hover),
:deep(.el-sub-menu__title:hover) {
  background-color: #263445 !important;
}

:deep(.el-divider--horizontal) {
  margin: 10px 0;
}
</style>
