<template>
  <aside class="admin-menu">
    <div class="brand">
      <div class="brand-title">管理后台</div>
      <div class="brand-subtitle">Naxi Pattern Platform</div>
    </div>

    <nav class="nav">
      <router-link to="/admin" class="nav-item">仪表板</router-link>
      <router-link to="/admin/patterns" class="nav-item">纹样管理</router-link>
      <router-link to="/admin/users" class="nav-item">用户管理</router-link>
      <router-link to="/admin/review" class="nav-item">内容审核</router-link>
      <!-- <router-link to="/admin/logs" class="nav-item">日志管理</router-link> -->
      <!-- <router-link to="/admin/settings" class="nav-item">参数设置</router-link> -->
      <router-link to="/admin/permissions" class="nav-item">权限管理</router-link>
    </nav>

    <div class="sidebar-footer">
      <span class="dot"></span>
      <span>管理员模式</span>
    </div>
  </aside>
</template>

<script setup>
</script>

<style scoped>
/* 侧边栏整体：与主内容形成“左侧深色 + 右侧浅色”的层次 */
.admin-menu {
  width: 260px;
  height: 100%;
  min-height: 1100px; /* 确保侧边栏高度始终覆盖视口 */
  padding: 16px 12px;
  box-sizing: border-box;

  background: linear-gradient(180deg, #1f6fb2 0%, #185a8f 100%);
  color: rgba(255, 255, 255, 0.92);

  /* 与主内容分隔更清晰 */
  border-right: 1px solid rgba(255, 255, 255, 0.12);
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.18);

  display: flex;
  flex-direction: column;
}

/* 顶部品牌区 */
.brand {
  padding: 6px 10px 14px 10px;
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

/* 导航 */
.nav {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 6px 6px 10px 6px;
}

/* 链接统一风格（不要用默认 a 标签样式，避免与背景冲突） */
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

/* hover：轻微高亮 */
.nav-item:hover {
  background: rgba(255, 255, 255, 0.12);
  transform: translateX(2px);
}

/* 激活态：白底强调，与主内容卡片更协调 */
.nav-item.router-link-active {
  background: rgba(255, 255, 255, 0.94);
  color: #134b76;
}

/* 底部状态区（占位，可放版本号/退出按钮等） */
.sidebar-footer {
  margin-top: auto;
  padding: 10px 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.14);
  font-size: 12px;
  opacity: 0.9;
  display: flex;
  align-items: center;
  gap: 8px;
}

.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #5ef28a;
  box-shadow: 0 0 0 3px rgba(94, 242, 138, 0.18);
}
</style>
