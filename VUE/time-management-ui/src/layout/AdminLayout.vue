<template>
  <div class="common-layout">
    <el-container>
      <el-aside width="200px">
        <AdminSideBar />
      </el-aside>
      
      <el-container>
        <el-header>
          <Header />
        </el-header>
        
        <el-main>
          <router-view></router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import AdminSideBar from '../components/AdminSideBar.vue';
import Header from '../components/Header.vue'; // 复用之前的 Header
</script>

<style scoped>
.common-layout, .el-container {
  height: 100vh;
}
.el-header {
  background: var(--app-header-bg);
  color: var(--app-text-color);
  line-height: 60px;
  border-bottom: 1px solid var(--app-border-color);
}
.el-aside {
  background: var(--app-aside-bg);
}
.el-main {
  background: var(--app-page-bg);
  color: var(--app-text-color);
}
</style>
