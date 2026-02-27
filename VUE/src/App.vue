<template>
  <div id="app">
    <el-container>
      <el-header>
        <div class="header-content">
          <h1>气象+农资一体化服务平台</h1>
          <nav class="nav-menu" v-if="showNav">
            <router-link to="/home">首页</router-link>
            <router-link to="/weather">气象数据</router-link>
            <router-link to="/warnings">预警信息</router-link>
            <router-link to="/products">农资产品</router-link>
            <router-link to="/orders">我的订单</router-link>
            <router-link to="/cart">购物车</router-link>
            <router-link to="/login" v-if="!isLoggedIn">登录</router-link>
            <router-link to="/profile" v-if="isLoggedIn">个人中心</router-link>
          </nav>
        </div>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
      <el-footer>
        <p>&copy; 2026 气象+农资一体化服务平台. All rights reserved.</p>
      </el-footer>
    </el-container>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router' // 引入 useRoute
import { useAuthStore } from '@/stores/auth'

const route = useRoute() // 获取当前路由信息
const authStore = useAuthStore()

const isLoggedIn = computed(() => authStore.isLoggedIn)

// 新增：判断当前路由是否为登录或注册页
const showNav = computed(() => {
  return route.path !== '/login' && route.path !== '/register'
})
</script>

<style scoped>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

.el-header {
  background-color: #409eff;
  color: white;
  padding: 0;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  padding: 0 20px;
}

.header-content h1 {
  margin: 0;
  font-size: 24px;
}

.nav-menu {
  display: flex;
  gap: 20px;
}

.nav-menu a {
  color: white;
  text-decoration: none;
  transition: opacity 0.3s;
}

.nav-menu a:hover {
  opacity: 0.8;
}

.nav-menu a.router-link-active {
  border-bottom: 2px solid white;
  padding-bottom: 5px;
}

.el-footer {
  background-color: #f5f7fa;
  color: #606266;
  text-align: center;
  padding: 20px;
}
</style>