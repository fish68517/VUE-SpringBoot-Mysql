<template>
  <div class="point-warning-list-demo">
    <WaterlogPointWarningList @close="handleClose" />
  </div>
</template>

<script setup lang="ts">
import WaterlogPointWarningList from '@/package/BZAQ/BzWaterlogDia/components/WaterlogPointWarningList.vue'

function handleClose() {
  window.history.back()
}
</script>

<script lang="ts">
export default {
  name: 'WaterlogPointWarningListDemo'
}
</script>

<style lang="scss" scoped>
.point-warning-list-demo {
  position: relative;
  width: 100vw;
  height: 100vh;
  min-width: 1156px;
  min-height: 812px;
  overflow: auto;
  background: #050914;
}
</style>
