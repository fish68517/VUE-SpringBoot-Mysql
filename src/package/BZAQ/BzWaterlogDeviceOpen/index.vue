<template>
  <div class="bz-waterlog-device-open" @click="openDeviceList">
  </div>
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

const sourceName = 'BzWaterlogDeviceOpen'
const payload = {
  diaName: 'waterlog-device-list'
}

const eventBus = {
  on(event, callback) {
    if (!props.bus) return
    props.bus.on(event, ({ source, data }) => {
      if (source === sourceName) return
      callback(data)
    })
  },
  off(event, callback) {
    if (!props.bus) return
    props.bus.off(event, callback)
  },
  emit(event, data) {
    if (!props.bus) return
    props.bus.emit(event, { data, source: sourceName })
  }
}

function openDeviceList() {
  eventBus.emit('OPEN_WATER_DIA', payload)
}
</script>

<script lang="ts">
export default {
  name: 'BzWaterlogDeviceOpen',
  version: '1.0.0'
}
</script>

<style lang="scss" scoped>
.bz-waterlog-device-open {
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
