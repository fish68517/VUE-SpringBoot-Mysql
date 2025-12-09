<template>
  <el-menu
    active-text-color="#ffd04b"
    background-color="#1f2d3d"
    class="el-menu-vertical-demo"
    text-color="#bfcbd9"
    router
    :default-active="$route.path"
  >
    <h3 style="color: white; text-align: center; padding: 20px 0;">系统管理后台</h3>
    
    <el-menu-item index="/admin/dashboard">
      <el-icon><DataAnalysis /></el-icon>
      <span>数据统计</span>
    </el-menu-item>
    
    <el-menu-item index="/admin/users">
      <el-icon><User /></el-icon>
      <span>用户管理</span>
    </el-menu-item>
    
    <el-menu-item index="/admin/departments">
      <el-icon><OfficeBuilding /></el-icon>
      <span>部门管理</span>
    </el-menu-item>

    <el-menu-item index="/admin/admin-product-management">
      <el-icon><OfficeBuilding /></el-icon>
      <span>产品管理</span>
    </el-menu-item>

    <el-menu-item index="/admin/inbound-management">
      <el-icon><UploadFilled /></el-icon>
      <span>入库管理</span>
    </el-menu-item>

    <!-- ===== 新增出库管理菜单项 ===== -->
    <el-menu-item index="/admin/outbound-management">
      <el-icon><Download /></el-icon>
      <span>出库管理</span>
    </el-menu-item>
    <!-- ============================= -->

    <el-menu-item index="/admin/statistics">
      <el-icon><TrendCharts /></el-icon>
      <span>数据统计</span>
    </el-menu-item>

     <!-- ===== 新增日志菜单 ===== -->
    <el-menu-item index="/admin/system-logs">
      <el-icon><Monitor /></el-icon>
      <span>用户操作日志</span>
    </el-menu-item>
    <!-- ====================== -->

    
     <el-menu-item index="/admin/item-logs">
      <el-icon><Monitor /></el-icon>
      <span>商品操作日志</span>
    </el-menu-item>
    
  </el-menu>
</template>

<script setup>
import {
  DataAnalysis,
  User,
  OfficeBuilding,
} from '@element-plus/icons-vue'
</script>

<style scoped>
.el-menu {
  border-right: none;
}
</style>