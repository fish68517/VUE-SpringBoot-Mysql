<!--
 * @Author: kaix
 * @Date: 2025-07-04 11:31:30
 * @LastEditTime: 2025-07-04 11:52:00
 * @LastEditors: kaix
 * @Description: 应用根组件
-->
<template>
  <router-view />
</template>

<script lang="ts" setup>
import { onMounted } from 'vue'

onMounted(() => {
  console.log('App 组件已挂载')
})
</script>

<style lang="scss">
#app {
  width: 100%;
  height: 100%;
  min-height: 100vh;
}

.n-config-provider {
  width: 100%;
  height: 100vh !important;
}

* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

body {
  width: 100%;
  height: 100%;
  overflow: hidden;
}
</style> 