<template>
  <div class="bz-house-kpi-open" @click="openHouseKpiList"></div>
</template>

<script setup lang="ts">
const props = defineProps({
  chartConfig: {
    type: Object,
    required: true
  },
  publicParamList: {
    type: Object,
    required: false,
    default: () => []
  },
  bus: {
    type: Object,
    required: false,
    default: null
  }
})

const sourceName = 'BZHouseKpiOpen'
const payload = {
  diaName: 'house-kpi-list'
}

const eventBus = {
  emit(event, data) {
    props.bus?.emit(event, { data, source: sourceName })
  }
}

function openHouseKpiList() {
  eventBus.emit('OPEN_HOUSE_KPI_LIST_DIA', payload)
}
</script>

<script lang="ts">
export default {
  name: 'BZHouseKpiOpen',
  version: '1.0.0'
}
</script>

<style lang="scss" scoped>
.bz-house-kpi-open {
  width: 100%;
  height: 100%;
  min-width: 120px;
  min-height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
  cursor: pointer;
}
</style>
