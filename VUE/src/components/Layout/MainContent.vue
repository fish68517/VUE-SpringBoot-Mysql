<template>
  <el-main class="main-content">
    <slot></slot>
  </el-main>
</template>

<script setup>
</script>

<style scoped>
.main-content {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background-color: #ffffff;
}
</style>
