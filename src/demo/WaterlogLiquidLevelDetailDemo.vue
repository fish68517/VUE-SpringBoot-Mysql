<template>
  <div class="waterlog-liquid-level-detail-demo">
    <WaterlogLiquidLevelDetail @close="handleClose" />
  </div>
</template>

<script setup lang="ts">
import WaterlogLiquidLevelDetail from '@/package/BZAQ/BzWaterlogDia/components/WaterlogLiquidLevelDetail.vue'

function handleClose() {
  window.history.back()
}
</script>

<script lang="ts">
export default {
  name: 'WaterlogLiquidLevelDetailDemo'
}
</script>

<style lang="scss" scoped>
.waterlog-liquid-level-detail-demo {
  position: relative;
  width: 100vw;
  height: 100vh;
  min-width: 1646px;
  min-height: 812px;
  overflow: auto;
  background: #050914;
}
</style>
