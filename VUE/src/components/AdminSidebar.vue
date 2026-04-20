<template>
  <aside class="admin-sidebar">
    <router-link to="/admin" class="menu-item" active-class="active">
      <span>后台首页</span>
    </router-link>
    <router-link to="/admin/users" class="menu-item" active-class="active">
      <span>用户管理</span>
    </router-link>
    <router-link to="/admin/travels" class="menu-item" active-class="active">
      <span>旅行记忆管理</span>
    </router-link>
    <router-link to="/admin/plans" class="menu-item" active-class="active">
      <span>旅行计划管理</span>
    </router-link>
    <router-link to="/admin/social" class="menu-item" active-class="active">
      <span>社交数据管理</span>
    </router-link>
  </aside>
</template>

<style scoped>
.admin-sidebar {
  width: 240px;
  background: #123043;
  padding: 24px 16px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.menu-item {
  color: rgba(255, 255, 255, 0.82);
  text-decoration: none;
  padding: 14px 16px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.04);
  transition: 0.25s ease;
}

.menu-item:hover,
.menu-item.active {
  color: #fff;
  background: linear-gradient(135deg, rgba(90, 164, 214, 0.35), rgba(57, 109, 149, 0.45));
}

@media (max-width: 768px) {
  .admin-sidebar {
    width: 100%;
    flex-direction: row;
    overflow-x: auto;
  }

  .menu-item {
    white-space: nowrap;
  }
}
</style>
