<template>
  <div class="app-layout user-layout">
    <aside class="sidebar">
      <div class="logo-area">
        <h2>星落宠物</h2>
        <p>用户中心</p>
      </div>
      
      <nav class="nav-menu">
        <router-link to="/user/productList" class="nav-item" :class="{ active: isActive('/user/productList') }">
          <span class="icon">🏠</span>
          <span>商城首页</span>
        </router-link>
        
        <div class="nav-divider">个人管理</div>
        
        <router-link to="/user/profile" class="nav-item" :class="{ active: isActive('/user/profile') }">
          <span class="icon">👤</span>
          <span>个人信息</span>
        </router-link>
        
        <router-link to="/user/pets" class="nav-item" :class="{ active: isActive('/user/pets') }">
          <span class="icon">🐾</span>
          <span>我的宠物</span>
        </router-link>
        
        <router-link to="/user/cart" class="nav-item" :class="{ active: isActive('/user/cart') }">
          <span class="icon">🛒</span>
          <span>购物车</span>
        </router-link>
        
        <router-link to="/user/orders" class="nav-item" :class="{ active: isActive('/user/orders') }">
          <span class="icon">📋</span>
          <span>我的订单</span>
        </router-link>

        <div class="nav-divider">社区互动</div>

        <router-link to="/community" class="nav-item">
          <span class="icon">💬</span>
          <span>宠物社区</span>
        </router-link>
      </nav>

      <div class="sidebar-footer">
        <button @click="handleLogout" class="btn-logout">退出登录</button>
      </div>
    </aside>

    <main class="main-content">
      <div class="content-wrapper">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </main>
  </div>
</template>

<script setup>
import { onMounted, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useUserStore } from '@/store/userStore';
import { ElMessage } from 'element-plus';

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

// 1. 调试日志：组件加载
onMounted(() => {
  console.log('[UserLayout] Mounted. Current Route:', route.fullPath);
  console.log('[UserLayout] User Info:', userStore.userInfo);
});

// 2. 调试日志：路由变化监听
watch(() => route.path, (newPath, oldPath) => {
  console.log(`[UserLayout] Navigation: ${oldPath} -> ${newPath}`);
});

const isActive = (path) => {
  return route.path === path;
};

const handleLogout = () => {
  console.log('[UserLayout] User initiated logout');
  userStore.logout();
  ElMessage.success('已安全退出');
  router.push('/login');
};
</script>

<style scoped>
.app-layout {
  display: flex;
  min-height: 100vh;
  background-color: #f5f7fa;
}

.sidebar {
  width: 240px;
  background: white;
  box-shadow: 2px 0 8px rgba(0,0,0,0.05);
  display: flex;
  flex-direction: column;
  position: fixed;
  height: 100vh;
  left: 0;
  top: 0;
  z-index: 100;
}

.logo-area {
  padding: 24px;
  text-align: center;
  border-bottom: 1px solid #eee;
}

.logo-area h2 {
  margin: 0;
  color: #667eea;
  font-size: 20px;
}

.logo-area p {
  margin: 5px 0 0;
  color: #999;
  font-size: 12px;
}

.nav-menu {
  flex: 1;
  padding: 20px 0;
  overflow-y: auto;
}

.nav-divider {
  padding: 10px 24px 5px;
  color: #999;
  font-size: 12px;
  font-weight: 600;
}

.nav-item {
  display: flex;
  align-items: center;
  padding: 12px 24px;
  color: #555;
  text-decoration: none;
  transition: all 0.3s;
  border-left: 3px solid transparent;
}

.nav-item:hover {
  background-color: #f0f7ff;
  color: #667eea;
}

.nav-item.active {
  background-color: #eef2ff;
  color: #667eea;
  border-left-color: #667eea;
  font-weight: 500;
}

.nav-item .icon {
  margin-right: 12px;
  font-size: 18px;
  width: 20px;
  text-align: center;
}

.sidebar-footer {
  padding: 20px;
  border-top: 1px solid #eee;
}

.btn-logout {
  width: 100%;
  padding: 10px;
  border: 1px solid #fee2e2;
  background-color: #fff1f2;
  color: #ef4444;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-logout:hover {
  background-color: #fee2e2;
}

.main-content {
  flex: 1;
  margin-left: 240px; /* Offset for fixed sidebar */
  padding: 20px;
  min-width: 0; /* Prevent flex child overflow */
}

.content-wrapper {
  max-width: 1200px;
  margin: 0 auto;
}

/* 简单的淡入淡出动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>