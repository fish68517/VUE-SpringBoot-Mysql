<template>
  <div class="bz-waterlog-risk-point-open" @click="openRiskPointList">
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

const sourceName = 'BzWaterlogRiskOpen'
const payload = {
  diaName: 'waterlog-risk-point-list'
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

function openRiskPointList() {
  eventBus.emit('OPEN_WATER_DIA', payload)
}
</script>

<script lang="ts">
export default {
  name: 'BzWaterlogRiskOpen',
  version: '1.0.0'
}
</script>

<style lang="scss" scoped>
.bz-waterlog-risk-point-open {
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

.open-btn {
  min-width: 132px;
  height: 40px;
  padding: 0 18px;
  border: 1px solid rgba(66, 170, 255, 0.72);
  border-radius: 4px;
  background: linear-gradient(180deg, rgba(31, 121, 218, 0.92), rgba(5, 63, 137, 0.92));
  box-shadow: 0 0 14px rgba(49, 142, 255, 0.38), inset 0 0 12px rgba(132, 213, 255, 0.18);
  color: #eaf7ff;
  font-size: 16px;
  font-weight: 600;
  line-height: 38px;
  cursor: pointer;
  transition: border-color 0.18s ease, box-shadow 0.18s ease, transform 0.18s ease;

  &:hover {
    border-color: rgba(111, 208, 255, 0.95);
    box-shadow: 0 0 20px rgba(49, 175, 255, 0.55), inset 0 0 14px rgba(132, 213, 255, 0.24);
    transform: translateY(-1px);
  }

  &:active {
    transform: translateY(0);
  }
}
</style>
