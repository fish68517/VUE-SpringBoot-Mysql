<template>
  <div class="app-layout shop-layout">
    <aside class="sidebar">
      <div class="logo-area">
        <h2>店家管理中心</h2>
        <p>星落宠物商城</p>
      </div>
      <nav class="nav-menu">
        <router-link to="/shop/dashboard" class="nav-item" :class="{ active: isActive('/shop/dashboard') }">
          <span class="icon">📊</span>
          <span>店铺概览</span>
        </router-link>
        <router-link to="/shop/info" class="nav-item" :class="{ active: isActive('/shop/info') }">
          <span class="icon">🏪</span>
          <span>店铺信息</span>
        </router-link>
        <router-link to="/shop/products" class="nav-item" :class="{ active: isActive('/shop/products') }">
          <span class="icon">📦</span>
          <span>商品管理</span>
        </router-link>
        <router-link to="/shop/orders" class="nav-item" :class="{ active: isActive('/shop/orders') }">
          <span class="icon">📋</span>
          <span>订单管理</span>
        </router-link>
        <router-link to="/shop/coupons" class="nav-item" :class="{ active: isActive('/shop/coupons') }">
          <span class="icon">🎟️</span>
          <span>优惠券管理</span>
        </router-link>
      </nav>
      <div class="sidebar-footer">
        <button @click="logout" class="btn-logout">退出登录</button>
      </div>
    </aside>
    <main class="main-content">
      <div class="content-wrapper">
        <router-view />
      </div>
    </main>
  </div>
</template>

<script setup>
import { onMounted, watch } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useUserStore } from "@/store/userStore";
import { ElMessage } from "element-plus";

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

onMounted(() => {
  // 打印用户信息
  console.log('[ShopLayout] Mounting. UserInfo:', userStore.userInfo);
  console.log('[ShopLayout] Mounted. ShopID:', userStore.userInfo?.shopId);
});

watch(() => route.path, (newPath) => {
  console.log(`[ShopLayout] Navigating to: ${newPath}`);
});

const isActive = (path) => {
  // 简单的包含匹配，让子路由也能高亮 (如 /shop/products/edit/1)
  return route.path.startsWith(path);
};

const logout = () => {
  console.log('[ShopLayout] Logout clicked');
  userStore.logout();
  ElMessage.success('已退出店家后台');
  router.push("/login");
};
</script>

<style scoped>
/* 复用 UserLayout 的样式结构，保持一致性 */
.app-layout { display: flex; min-height: 100vh; background-color: #f5f7fa; }
.sidebar { width: 240px; background: #2c3e50; color: white; display: flex; flex-direction: column; position: fixed; height: 100vh; z-index: 100; }
.logo-area { padding: 24px; text-align: center; border-bottom: 1px solid rgba(255,255,255,0.1); }
.logo-area h2 { margin: 0; font-size: 18px; color: #fff; }
.logo-area p { margin: 5px 0 0; color: rgba(255,255,255,0.5); font-size: 12px; }
.nav-menu { flex: 1; padding: 20px 0; overflow-y: auto; }
.nav-item { display: flex; align-items: center; padding: 12px 24px; color: rgba(255,255,255,0.7); text-decoration: none; transition: all 0.3s; border-left: 3px solid transparent; }
.nav-item:hover { background-color: rgba(255,255,255,0.1); color: white; }
.nav-item.active { background-color: rgba(102, 126, 234, 0.2); color: #fff; border-left-color: #667eea; }
.nav-item .icon { margin-right: 12px; font-size: 18px; }
.sidebar-footer { padding: 20px; border-top: 1px solid rgba(255,255,255,0.1); }
.btn-logout { width: 100%; padding: 10px; background: #e74c3c; color: white; border: none; border-radius: 4px; cursor: pointer; transition: all 0.3s; }
.btn-logout:hover { background: #c0392b; }
.main-content { flex: 1; margin-left: 240px; padding: 20px; }
.content-wrapper { max-width: 1200px; margin: 0 auto; }
</style>