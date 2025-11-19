<template>
  <div class="admin-layout">
    <div class="admin-sidebar">
      <div class="admin-logo">
        <h2>管理中心</h2>
      </div>
      <nav class="admin-nav">
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
      <div class="admin-footer">
        <button @click="logout" class="btn-logout">退出登录</button>
      </div>
    </div>
    <div class="admin-content">
      <router-view />
    </div>
  </div>
</template>

<script setup>
import { useRouter, useRoute } from "vue-router";
import { useUserStore } from "@/store/userStore";

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

const isActive = (path) => {
  return route.path === path;
};

const logout = () => {
  userStore.logout();
  router.push("/login");
};
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
  background: #f5f5f5;
}

.admin-sidebar {
  width: 250px;
  background: #1a1a2e;
  color: white;
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
}

.admin-logo {
  padding: 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.admin-logo h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
}

.admin-nav {
  flex: 1;
  padding: 20px 0;
}

.nav-item {
  display: flex;
  align-items: center;
  padding: 12px 20px;
  color: rgba(255, 255, 255, 0.7);
  text-decoration: none;
  transition: all 0.3s ease;
  border-left: 3px solid transparent;
}

.nav-item:hover {
  background: rgba(255, 255, 255, 0.1);
  color: white;
}

.nav-item.active {
  background: rgba(76, 175, 80, 0.2);
  color: #4caf50;
  border-left-color: #4caf50;
}

.nav-item .icon {
  margin-right: 12px;
  font-size: 18px;
}

.admin-footer {
  padding: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.btn-logout {
  width: 100%;
  padding: 10px;
  background: #e74c3c;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.btn-logout:hover {
  background: #c0392b;
}

.admin-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

@media (max-width: 768px) {
  .admin-layout {
    flex-direction: column;
  }

  .admin-sidebar {
    width: 100%;
    flex-direction: row;
    align-items: center;
  }

  .admin-logo {
    border-bottom: none;
    border-right: 1px solid rgba(255, 255, 255, 0.1);
    padding: 10px 20px;
  }

  .admin-nav {
    flex: 1;
    display: flex;
    padding: 0;
  }

  .nav-item {
    padding: 10px 15px;
    border-left: none;
    border-bottom: 3px solid transparent;
  }

  .nav-item.active {
    border-left: none;
    border-bottom-color: #4caf50;
  }

  .admin-footer {
    padding: 10px 20px;
    border-top: none;
    border-left: 1px solid rgba(255, 255, 255, 0.1);
  }

  .btn-logout {
    width: auto;
    padding: 8px 16px;
  }
}
</style>
