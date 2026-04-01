<template>
  <aside class="admin-menu">
    <div class="brand">
      <div class="brand-title">管理后台</div>
      <div class="brand-subtitle">Naxi Pattern Platform</div>
    </div>

    <nav class="nav">
      <router-link to="/admin" class="nav-item">数据概览</router-link>
      <router-link to="/admin/patterns" class="nav-item">纹样管理</router-link>
      <router-link to="/admin/users" class="nav-item">用户管理</router-link>
      <router-link to="/admin/review" class="nav-item">内容审核</router-link>
      <router-link to="/admin/permissions" class="nav-item">日志操作</router-link>
    </nav>

    <div class="sidebar-footer">
      <div class="footer-status">
        <span class="dot"></span>
        <span>管理员模式</span>
      </div>
      <button class="logout-btn" @click="handleLogout">退出登录</button>
    </div>
  </aside>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../store'

const router = useRouter()
const userStore = useUserStore()

const handleLogout = () => {
  userStore.logout()
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<style scoped>
.admin-menu {
  width: 260px;
  height: 100%;
  min-height: 1100px;
  padding: 16px 12px;
  box-sizing: border-box;
  background: linear-gradient(180deg, #1f6fb2 0%, #185a8f 100%);
  color: rgba(255, 255, 255, 0.92);
  border-right: 1px solid rgba(255, 255, 255, 0.12);
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.18);
  display: flex;
  flex-direction: column;
}

.brand {
  padding: 6px 10px 14px;
  margin-bottom: 10px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.14);
}

.brand-title {
  font-size: 18px;
  font-weight: 700;
  letter-spacing: 0.5px;
  line-height: 1.2;
}

.brand-subtitle {
  margin-top: 6px;
  font-size: 12px;
  opacity: 0.8;
}

.nav {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 6px 6px 10px;
}

.nav-item {
  display: flex;
  align-items: center;
  padding: 10px 12px;
  border-radius: 10px;
  text-decoration: none;
  color: rgba(255, 255, 255, 0.92);
  font-size: 14px;
  font-weight: 600;
  transition: background-color 0.18s ease, transform 0.12s ease, color 0.18s ease;
}

.nav-item:hover {
  background: rgba(255, 255, 255, 0.12);
  transform: translateX(2px);
}

.nav-item.router-link-active {
  background: rgba(255, 255, 255, 0.94);
  color: #134b76;
}

.sidebar-footer {
  margin-top: auto;
  padding: 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.14);
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.footer-status {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  opacity: 0.9;
}

.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #5ef28a;
  box-shadow: 0 0 0 3px rgba(94, 242, 138, 0.18);
}

.logout-btn {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid rgba(255, 255, 255, 0.28);
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.12);
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s ease, transform 0.12s ease;
}

.logout-btn:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-1px);
}
</style>
