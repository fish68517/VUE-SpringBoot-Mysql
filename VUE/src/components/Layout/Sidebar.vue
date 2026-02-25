<template>
  <el-aside width="200px" class="sidebar">
    <el-menu 
      :default-active="activeMenu"
      router
      class="sidebar-menu"
    >
      <slot></slot>
    </el-menu>
  </el-aside>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const activeMenu = computed(() => route.path)
</script>

<style scoped>
.sidebar {
  background-color: #f5f7fa;
  border-right: 1px solid #dcdfe6;
  height: 100%;
}

.sidebar-menu {
  border-right: none;
}
</style>
