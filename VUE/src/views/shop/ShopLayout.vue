<template>
  <div class="shop-layout">
    <div class="shop-sidebar">
      <div class="shop-logo">
        <h2>店家中心</h2>
      </div>
      <nav class="shop-nav">
        <router-link to="/shop/dashboard" class="nav-item" :class="{ active: isActive('/shop/dashboard') }">
          <span class="icon">📊</span>
          <span>店铺首页</span>
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
      <div class="shop-footer">
        <button @click="logout" class="btn-logout">退出登录</button>
      </div>
    </div>
    <div class="shop-content">
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
.shop-layout {
  display: flex;
  min-height: 100vh;
  background: #f5f5f5;
}

.shop-sidebar {
  width: 250px;
  background: #2c3e50;
  color: white;
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
}

.shop-logo {
  padding: 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.shop-logo h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
}

.shop-nav {
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
  background: rgba(102, 126, 234, 0.2);
  color: #667eea;
  border-left-color: #667eea;
}

.nav-item .icon {
  margin-right: 12px;
  font-size: 18px;
}

.shop-footer {
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

.shop-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

@media (max-width: 768px) {
  .shop-layout {
    flex-direction: column;
  }

  .shop-sidebar {
    width: 100%;
    flex-direction: row;
    align-items: center;
  }

  .shop-logo {
    border-bottom: none;
    border-right: 1px solid rgba(255, 255, 255, 0.1);
    padding: 10px 20px;
  }

  .shop-nav {
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
    border-bottom-color: #667eea;
  }

  .shop-footer {
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
