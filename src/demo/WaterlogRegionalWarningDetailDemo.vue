<template>
  <div class="regional-warning-detail-demo">
    <WaterlogRegionalWarningDetail @close="handleClose" />
  </div>
</template>

<script setup lang="ts">
import WaterlogRegionalWarningDetail from '@/package/BZAQ/BzWaterlogDia/components/WaterlogRegionalWarningDetail.vue'

function handleClose() {
  window.history.back()
}
</script>

<script lang="ts">
export default {
  name: 'WaterlogRegionalWarningDetailDemo'
}
</script>

<style lang="scss" scoped>
.regional-warning-detail-demo {
  position: relative;
  width: 100vw;
  height: 100vh;
  min-width: 1646px;
  min-height: 812px;
  overflow: auto;
  background: #050914;
}
</style>
