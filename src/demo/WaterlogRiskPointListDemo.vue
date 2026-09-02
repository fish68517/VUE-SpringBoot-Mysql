<template>
  <div class="demo-container">
    <button class="test-btn" @click="show = true">打开风险点列表</button>
    <WaterlogRiskPointList v-if="show" @close="show = false" />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import WaterlogRiskPointList from '@/package/BZAQ/BzWaterKpiDia/components/WaterlogRiskPointList.vue'

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
