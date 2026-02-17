<template>
  <div class="admin-menu">
    <nav>
      <router-link to="/admin">仪表板</router-link>
      <router-link to="/admin/patterns">纹样管理</router-link>
      <router-link to="/admin/users">用户管理</router-link>
      <router-link to="/admin/review">内容审核</router-link>
      <router-link to="/admin/logs">日志管理</router-link>
      <router-link to="/admin/settings">参数设置</router-link>
      <router-link to="/admin/permissions">权限管理</router-link>
    </nav>
  </div>
</template>

<script setup>
</script>

<style scoped>
.admin-menu {
  background-color: #2b9ada;
  padding: 1rem;
  width: 200px;
  border-radius: 4px;
}

nav {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

a {
  padding: 0.5rem 1rem;
  text-decoration: none;
  color: #333;
  border-radius: 4px;
  transition: background-color 0.3s;
}

a:hover,
a.router-link-active {
  background-color: #ddd;
}
</style>
