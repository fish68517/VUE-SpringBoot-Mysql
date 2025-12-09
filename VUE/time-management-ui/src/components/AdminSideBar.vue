<template>
  <el-menu
    active-text-color="#ffd04b"
    background-color="#1f2d3d"
    class="el-menu-vertical-demo"
    text-color="#bfcbd9"
    router
    :default-active="$route.path"
  >
    <h3 style="color: white; text-align: center; padding: 20px 0;">管理后台</h3>
    
    <el-menu-item index="/admin/dashboard">
      <el-icon><DataAnalysis /></el-icon>
      <span>数据统计</span>
    </el-menu-item>
    
    <el-menu-item index="/admin/users">
      <el-icon><User /></el-icon>
      <span>用户审核</span>
    </el-menu-item>
    
    <el-menu-item index="/admin/incentives">
      <el-icon><Medal /></el-icon>
      <span>激励配置</span>
    </el-menu-item>
    
    <el-menu-item index="/admin/resources">
      <el-icon><Collection /></el-icon>
      <span>资源维护</span>
    </el-menu-item>

      <el-menu-item index="/admin/habits">
    <el-icon><Calendar /></el-icon>
    <span>习惯监管</span>
  </el-menu-item>

  <el-menu-item index="/admin/focus">
  <el-icon><Timer /></el-icon>
  <span>专注监管</span>
</el-menu-item>


<el-menu-item index="/admin/user-achievements">
  <el-icon><Medal /></el-icon> <span>成就颁发</span>
</el-menu-item>

<el-menu-item index="/admin/interactions">
  <el-icon><Star /></el-icon>
  <span>交互监控</span>
</el-menu-item>
      
  </el-menu>
</template>

<script setup>
import {
  DataAnalysis,
  User,
  Medal,
  Collection,
} from '@element-plus/icons-vue'
</script>

<style scoped>
.el-menu {
  border-right: none;
}
</style>