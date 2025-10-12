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
  background-color: #B3C0D1;
  color: #333;
  line-height: 60px;
}
.el-aside {
  background-color: #1f2d3d; /* Admin主题色 */
}
.el-main {
  background-color: #f0f2f5; /* Admin背景色 */
}
</style>