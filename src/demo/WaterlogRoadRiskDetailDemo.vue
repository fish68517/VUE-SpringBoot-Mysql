<template>
  <div class="waterlog-road-risk-detail-demo">
    <WaterlogRoadRiskDetail @close="handleClose" />
  </div>
</template>

<script setup lang="ts">
import WaterlogRoadRiskDetail from '@/package/BZAQ/BzWaterlogDia/components/WaterlogRoadRiskDetail.vue'

function handleClose() {
  window.history.back()
}
</script>

<script lang="ts">
export default {
  name: 'WaterlogRoadRiskDetailDemo'
}
</script>

<style lang="scss" scoped>
.waterlog-road-risk-detail-demo {
  position: relative;
  width: 100vw;
  height: 100vh;
  min-width: 1646px;
  min-height: 812px;
  overflow: auto;
  background: #050914;
}
</style>
