<template>
  <div class="demo-container">
    <button class="test-btn" @click="show = true">打开设备详情列表</button>
    <WaterlogDeviceList v-if="show" @close="show = false" />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import WaterlogDeviceList from '@/package/BZAQ/BzWaterlogDia/components/WaterlogDeviceList.vue'

const show = ref(true)
</script>

<style scoped>
.demo-container {
  width: 100vw;
  height: 100vh;
  background: #000;
  display: flex;
  align-items: center;
  justify-content: center;
}
.test-btn {
  position: absolute;
  top: 20px;
  left: 20px;
  padding: 10px 20px;
  z-index: 9999;
}
</style>
