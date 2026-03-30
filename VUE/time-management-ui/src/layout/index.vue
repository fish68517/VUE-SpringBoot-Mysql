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
.common-layout,
.el-container {
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
  color: var(--app-text-color);
}

.el-main {
  background: var(--app-page-bg);
  color: var(--app-text-color);
}
</style>
