<template>
  <el-header class="header">
    <div class="header-content">
      <h1 class="logo">广州旅游管理系统</h1>
      <div class="header-actions">
        <slot></slot>
      </div>
    </div>
  </el-header>
</template>

<script setup>
</script>

<style scoped>
.header {
  background-color: #409eff;
  color: white;
  padding: 0 20px;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.logo {
  margin: 0;
  font-size: 24px;
  font-weight: bold;
}

.header-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}
</style>
