<template>
  <div class="waterlog-alarm-detail-demo">
    <WaterlogAlarmDetail @close="handleClose" />
  </div>
</template>

<script setup lang="ts">
import WaterlogAlarmDetail from '@/package/BZAQ/BzWaterlogDia/components/WaterlogAlarmDetail.vue'

function handleClose() {
  window.history.back()
}
</script>

<script lang="ts">
export default {
  name: 'WaterlogAlarmDetailDemo'
}
</script>

<style lang="scss" scoped>
.waterlog-alarm-detail-demo {
  position: relative;
  width: 100vw;
  height: 100vh;
  min-width: 1838px;
  min-height: 949px;
  overflow: auto;
  background: #050914;
}
</style>
