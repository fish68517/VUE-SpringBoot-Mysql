<template>
  <el-aside class="sidebar-menu-container">
    <!-- 角色标识 -->
    <div class="sidebar-header">
      <span v-if="isAdmin" class="role-badge admin">管理员</span>
      <span v-else class="role-badge tourist">游客</span>
    </div>

    <!-- 菜单 -->
    <el-menu 
      :default-active="activeMenu"
      router
      class="sidebar-menu"
    >
      <!-- 管理员菜单 -->
      <template v-if="isAdmin">
        <el-menu-item index="/admin/users">
          <el-icon><user /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/attractions">
          <el-icon><location /></el-icon>
          <span>景点管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/hotels">
          <el-icon><office-building /></el-icon>
          <span>酒店管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/orders">
          <el-icon><shopping-cart /></el-icon>
          <span>订单管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/comments">
          <el-icon><chat-dot-square /></el-icon>
          <span>留言管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/announcements">
          <el-icon><bell /></el-icon>
          <span>公告管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/routes">
          <el-icon><map-location /></el-icon>
          <span>路线管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/products">
  <el-icon><bell /></el-icon>
  <span>商品管理</span>
</el-menu-item>
      </template>

      <!-- 游客菜单 -->
      <template v-else>
        <el-menu-item index="/">
          <el-icon><home-filled /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/attractions">
          <el-icon><location /></el-icon>
          <span>景点</span>
        </el-menu-item>
        <el-menu-item index="/hotels">
          <el-icon><office-building /></el-icon>
          <span>酒店</span>
        </el-menu-item>
        <el-menu-item index="/products">
          <el-icon><shopping-bag /></el-icon>
          <span>商品</span>
        </el-menu-item>
        <el-menu-item index="/routes">
          <el-icon><map-location /></el-icon>
          <span>推荐路线</span>
        </el-menu-item>
        <el-divider />
        <el-menu-item index="/personal-center">
          <el-icon><user /></el-icon>
          <span>个人中心</span>
        </el-menu-item>
        <el-menu-item index="/orders">
          <el-icon><shopping-cart /></el-icon>
          <span>我的订单</span>
        </el-menu-item>
        <!-- <el-menu-item index="/favorites">
          <el-icon><star-filled /></el-icon>
          <span>我的收藏</span>
        </el-menu-item> -->
      </template>
    </el-menu>
  </el-aside>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import {
  User,
  Location,
  OfficeBuilding, // <--- 把 Building 改成 OfficeBuilding
  ShoppingCart,
  ChatDotSquare,
  Bell,
  MapLocation,
  HomeFilled,
  ShoppingBag,
  StarFilled
} from '@element-plus/icons-vue'

const route = useRoute()

const props = defineProps({
  userRole: {
    type: String,
    default: 'tourist'
  }
})

const isAdmin = computed(() => props.userRole === 'admin')

const activeMenu = computed(() => route.path)
</script>

<style scoped>
.sidebar-menu-container {
  width: 220px;
  background-color: #f5f7fa;
  border-right: 1px solid #dcdfe6;
  overflow-y: auto;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
  transition: all 0.3s ease;
}

.sidebar-header {
  padding: 15px 20px;
  border-bottom: 1px solid #dcdfe6;
  display: flex;
  justify-content: center;
}

.role-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: bold;
  color: white;
}

.role-badge.admin {
  background-color: #f56c6c;
}

.role-badge.tourist {
  background-color: #409eff;
}

.sidebar-menu {
  border-right: none;
  padding: 10px 0;
  flex: 1;
}

.sidebar-menu :deep(.el-menu-item) {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 0 20px;
  height: 40px;
  line-height: 40px;
  font-size: 14px;
  transition: all 0.3s ease;
}

.sidebar-menu :deep(.el-menu-item:hover) {
  background-color: #e0e9ff;
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background-color: #409eff;
  color: white;
}

.sidebar-menu :deep(.el-divider) {
  margin: 10px 0;
}

/* Tablet screens */
@media (max-width: 1024px) {
  .sidebar-menu-container {
    width: 200px;
  }

  .sidebar-menu :deep(.el-menu-item) {
    padding: 0 15px;
    font-size: 13px;
  }
}

/* Tablet and smaller */
@media (max-width: 768px) {
  .sidebar-menu-container {
    width: 180px;
  }

  .sidebar-menu :deep(.el-menu-item) {
    padding: 0 12px;
    height: 38px;
    line-height: 38px;
    font-size: 12px;
    gap: 8px;
  }

  .sidebar-header {
    padding: 12px 15px;
  }

  .role-badge {
    font-size: 11px;
    padding: 3px 10px;
  }
}

/* Mobile devices */
@media (max-width: 480px) {
  .sidebar-menu-container {
    width: 150px;
  }

  .sidebar-menu :deep(.el-menu-item) {
    padding: 0 10px;
    height: 36px;
    line-height: 36px;
    font-size: 11px;
    gap: 6px;
  }

  .sidebar-menu :deep(.el-menu-item span) {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .sidebar-header {
    padding: 10px 12px;
  }

  .role-badge {
    font-size: 10px;
    padding: 2px 8px;
  }

  .sidebar-menu :deep(.el-icon) {
    font-size: 14px;
    flex-shrink: 0;
  }
}

/* Extra small devices */
@media (max-width: 360px) {
  .sidebar-menu-container {
    width: 130px;
  }

  .sidebar-menu :deep(.el-menu-item) {
    padding: 0 8px;
    height: 34px;
    line-height: 34px;
    font-size: 10px;
  }
}
</style>
