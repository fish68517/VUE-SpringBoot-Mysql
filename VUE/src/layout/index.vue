<template>
  <div class="common-layout">
    <el-container>
      <el-aside width="200px">
        <SideBar />
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
import SideBar from '../components/SideBar.vue';
import Header from '../components/Header.vue';
</script>

<style scoped>
.common-layout, .el-container {
  height: 100vh; /* 让布局占满整个屏幕高度 */
}

.el-header {
  background-color: #B3C0D1;
  color: #333;
  line-height: 60px;
}

.el-aside {
  background-color: #304156;
  color: #333;
}

.el-main {
  background-color: #E9EEF3;
  color: #333;
}
</style>