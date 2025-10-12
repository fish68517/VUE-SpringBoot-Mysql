<template>
  <el-menu
    active-text-color="#ffd04b"
    background-color="#304156"
    class="el-menu-vertical-demo"
    text-color="#fff"
    router
    :default-active="$route.path"
  >
    <h3 style="color: white; text-align: center; padding: 20px 0;">仓储管理系统</h3>
    
    <el-menu-item index="/dashboard">
      <el-icon><DataLine /></el-icon>
      <span>数据看板</span>
    </el-menu-item>
    
    <el-menu-item index="/products">
      <el-icon><Box /></el-icon>
      <span>产品管理</span>
    </el-menu-item>
    
    <el-menu-item index="/inventory">
      <el-icon><Coin /></el-icon>
      <span>库存查询</span>
    </el-menu-item>
    
    <el-menu-item index="/inbound-orders">
      <el-icon><DocumentAdd /></el-icon>
      <span>入库单管理</span>
    </el-menu-item>
    
  </el-menu>
</template>

<script setup>
import {
  DataLine,
  Box,
  Coin,
  DocumentAdd,
} from '@element-plus/icons-vue'
</script>

<style>
.el-menu {
  border-right: none;
}
</style>