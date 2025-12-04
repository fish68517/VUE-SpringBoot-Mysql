<template>
  <header class="header">
    <div class="logo">沈阳音乐节</div>
    <nav class="nav">
      <router-link to="/">首页</router-link>
      <router-link to="/shop">商城</router-link>
      <router-link to="/login">登录</router-link>
    </nav>
  </header>
</template>

<script setup lang="ts">
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background-color: #f5f5f5;
  border-bottom: 1px solid #ddd;
}

.logo {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.nav {
  display: flex;
  gap: 20px;
}

.nav a {
  text-decoration: none;
  color: #666;
  transition: color 0.3s;
}

.nav a:hover {
  color: #333;
}

.nav a.router-link-active {
  color: #409eff;
  font-weight: bold;
}
</style>
