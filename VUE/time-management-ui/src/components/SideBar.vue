<template>
  <el-menu
    active-text-color="#ffd04b"
    background-color="#304156"
    class="el-menu-vertical-demo"
    text-color="#fff"
    router
    :default-active="$route.path"
  >
    <h3 style="color: white; text-align: center; padding: 20px 0;">自律引导系统</h3>
    
    <el-menu-item index="/dashboard">
      <el-icon><DataLine /></el-icon>
      <span>我的仪表盘</span>
    </el-menu-item>
    
    <el-menu-item index="/task">
      <el-icon><Checked /></el-icon>
      <span>任务管理</span>
    </el-menu-item>
    
    <el-menu-item index="/habit">
      <el-icon><Sunrise /></el-icon>
      <span>习惯养成</span>
    </el-menu-item>
    
    <el-menu-item index="/achievement">
      <el-icon><Trophy /></el-icon>
      <span>成就徽章</span>
    </el-menu-item>
    
  </el-menu>
</template>

<script setup>
import {
  DataLine,
  Checked,
  Sunrise,
  Trophy,
} from '@element-plus/icons-vue'
</script>

<style>
.el-menu {
  border-right: none;
}
</style>