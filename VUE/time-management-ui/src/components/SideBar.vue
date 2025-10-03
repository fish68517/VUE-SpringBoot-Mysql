<template>
  <el-menu
    active-text-color="#ffd04b"
    background-color="#304156"
    class="el-menu-vertical-demo"
    default-active="1"
    text-color="#fff"
    router
  >
    <h3 style="color: white; text-align: center; padding: 20px 0;">自律引导系统</h3>
    
    <el-menu-item index="/task">
      <el-icon><icon-menu /></el-icon>
      <span>任务管理</span>
    </el-menu-item>
    
    <el-menu-item index="/habit">
      <el-icon><setting /></el-icon>
      <span>习惯养成</span>
    </el-menu-item>
    
    <el-menu-item index="/stats">
      <el-icon><document /></el-icon>
      <span>数据统计</span>
    </el-menu-item>
    
    </el-menu>
</template>

<script setup>
// 引入 Element Plus 的图标
import {
  Document,
  Menu as IconMenu,
  Setting,
} from '@element-plus/icons-vue'
</script>

<style>
.el-menu {
  border-right: none; /* 去掉菜单右边的边框 */
}
</style>
