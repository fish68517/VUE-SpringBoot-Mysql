<template>
  <div class="point-warning-detail-demo">
    <WaterlogPointWarningDetail @close="handleClose" />
  </div>
</template>

<script setup lang="ts">
import WaterlogPointWarningDetail from '@/package/BZAQ/BzWaterlogDia/components/WaterlogPointWarningDetail.vue'

function handleClose() {
  window.history.back()
}
</script>

<script lang="ts">
export default {
  name: 'WaterlogPointWarningDetailDemo'
}
</script>

<style lang="scss" scoped>
.point-warning-detail-demo {
  position: relative;
  width: 100vw;
  height: 100vh;
  min-width: 1646px;
  min-height: 812px;
  overflow: auto;
  background: #050914;
}
</style>
