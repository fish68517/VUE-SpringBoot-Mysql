<template>
  <div class="app-layout admin-layout">
    <aside class="sidebar">
      <div class="logo-area">
        <h2>系统管理后台</h2>
        <p>星落宠物</p>
      </div>
      <nav class="nav-menu">
        <router-link to="/admin/dashboard" class="nav-item" :class="{ active: isActive('/admin/dashboard') }">
          <span class="icon">📊</span>
          <span>数据看板</span>
        </router-link>
        <router-link to="/admin/users" class="nav-item" :class="{ active: isActive('/admin/users') }">
          <span class="icon">👥</span>
          <span>用户管理</span>
        </router-link>
        <router-link to="/admin/shops" class="nav-item" :class="{ active: isActive('/admin/shops') }">
          <span class="icon">🏪</span>
          <span>店铺管理</span>
        </router-link>
        <router-link to="/admin/products" class="nav-item" :class="{ active: isActive('/admin/products') }">
          <span class="icon">📦</span>
          <span>商品审核</span>
        </router-link>
        <router-link to="/admin/posts" class="nav-item" :class="{ active: isActive('/admin/posts') }">
          <span class="icon">💬</span>
          <span>社区管理</span>
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
  console.log('[AdminLayout] Admin logged in:', userStore.userInfo?.username);
});

watch(() => route.path, (newPath) => {
  console.log(`[AdminLayout] Route change: ${newPath}`);
});

const isActive = (path) => {
  return route.path.startsWith(path);
};

const logout = () => {
  console.log('[AdminLayout] Logout action');
  userStore.logout();
  ElMessage.success('管理员已退出');
  router.push("/login");
};
</script>

<style scoped>
/* Darker theme for Admin */
.app-layout { display: flex; min-height: 100vh; background-color: #f5f7fa; }
.sidebar { width: 240px; background: #1a1a2e; color: white; display: flex; flex-direction: column; position: fixed; height: 100vh; z-index: 100; }
.logo-area { padding: 24px; text-align: center; border-bottom: 1px solid rgba(255,255,255,0.1); }
.logo-area h2 { margin: 0; font-size: 18px; color: #fff; }
.logo-area p { margin: 5px 0 0; color: rgba(255,255,255,0.5); font-size: 12px; }
.nav-menu { flex: 1; padding: 20px 0; overflow-y: auto; }
.nav-item { display: flex; align-items: center; padding: 12px 24px; color: rgba(255,255,255,0.7); text-decoration: none; transition: all 0.3s; border-left: 3px solid transparent; }
.nav-item:hover { background-color: rgba(255,255,255,0.1); color: white; }
.nav-item.active { background-color: rgba(76, 175, 80, 0.2); color: #4caf50; border-left-color: #4caf50; }
.nav-item .icon { margin-right: 12px; font-size: 18px; }
.sidebar-footer { padding: 20px; border-top: 1px solid rgba(255,255,255,0.1); }
.btn-logout { width: 100%; padding: 10px; background: #e74c3c; color: white; border: none; border-radius: 4px; cursor: pointer; transition: all 0.3s; }
.btn-logout:hover { background: #c0392b; }
.main-content { flex: 1; margin-left: 240px; padding: 20px; }
.content-wrapper { max-width: 1200px; margin: 0 auto; }
</style>