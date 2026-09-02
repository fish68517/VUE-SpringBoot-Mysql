<template>
  <div class="waterlog-dia-bus-demo">
    <div class="toolbar">
      <n-select
        v-model:value="selectedDia"
        class="dia-select"
        :options="diaOptions"
        filterable
        placeholder="选择要打开的弹窗"
      />
      <n-button type="primary" @click="openSelectedDia">打开选中弹窗</n-button>
      <n-button @click="closeDia">关闭弹窗</n-button>
      <n-button type="success" @click="goHome">返回首页</n-button>
    </div>

    <div class="main-component-panel">
      <BzWaterlog
        :chart-config="waterlogConfig"
        :bus="eventBus"
        :public-param-list="[]"
      />
    </div>

    <div class="dia-layer">
      <BzWaterlogDia
        :chart-config="diaConfig"
        :bus="eventBus"
        :public-param-list="[]"
      />
      <BzWaterKpiDia
        :chart-config="diaConfig"
        :bus="eventBus"
        :public-param-list="[]"
      />
      <BzWaterlogNextDia
        :chart-config="diaConfig"
        :bus="eventBus"
        :public-param-list="[]"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import eventBus from '@/utils/bus'
import BzWaterlog from '@/package/BZAQ/BzWaterlog/index.vue'
import BzWaterlogDia from '@/package/BZAQ/BzWaterlogDia/index.vue'
import BzWaterKpiDia from '@/package/BZAQ/BzWaterKpiDia/index.vue'
import BzWaterlogNextDia from '@/package/BZAQ/BzWaterlogNextDia/index.vue'

const router = useRouter()
const diaConfig = ref({
  attr: {
    w: window.innerWidth,
    h: window.innerHeight
  }
})
const waterlogConfig = ref({
  attr: {
    w: 540,
    h: 910
  }
})
const selectedDia = ref('waterlog-point-warning-list')
const diaOptions = [
  { label: '告警列表', value: 'waterlog-alarm-list' },
  { label: '告警事件列表', value: 'waterlog-alarm-event-list' },
  { label: '设备视频', value: 'waterlog-device-video' },
  { label: '设备列表', value: 'waterlog-device-list' },
  { label: '设备详情', value: 'waterlog-device-detail' },
  { label: '液位计监测', value: 'waterlog-level-monitor' },
  { label: '液位计详情', value: 'waterlog-liquid-level-detail' },
  { label: '风险详情', value: 'waterlog-risk-detail' },
  { label: '风险点列表', value: 'waterlog-risk-point-list' },
  { label: '道路风险详情', value: 'waterlog-road-risk-detail' },
  { label: '告警详情', value: 'waterlog-alarm-detail' },
  { label: '区级预警', value: 'waterlog-regional-warning-detail' },
  { label: '点位预警详情', value: 'waterlog-point-warning-detail' },
  { label: '点位预警列表', value: 'waterlog-point-warning-list' },
  { label: '气象预警弹窗', value: 'weather-alert' },
  { label: '降雨监测弹窗', value: 'rainfall-monitor' },
  { label: '应急响应弹窗', value: 'emergency-response' },
  { label: '街道应急响应弹窗', value: 'emergency-response-street' },
  { label: '水位监测弹窗', value: 'water-level' }
]

function syncSize() {
  diaConfig.value.attr.w = window.innerWidth
  diaConfig.value.attr.h = window.innerHeight
}

function openDia(diaName: string) {
  eventBus.emit('CLOSE_DIA', { source: 'WaterlogDiaBusDemo', data: null })
  eventBus.emit('OPEN_DIA', {
    source: 'WaterlogDiaBusDemo',
    data: { diaName }
  })
}

function openSelectedDia() {
  openDia(selectedDia.value)
}

function closeDia() {
  eventBus.emit('CLOSE_DIA', { source: 'WaterlogDiaBusDemo', data: null })
}

function goHome() {
  router.push('/')
}

onMounted(() => {
  eventBus.clearCache('OPEN_DIA')
  eventBus.clearCache('CLOSE_DIA')
  syncSize()
  window.addEventListener('resize', syncSize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', syncSize)
})
</script>

<script lang="ts">
export default {
  name: 'WaterlogDiaBusDemo'
}
</script>

<style lang="scss" scoped>
.waterlog-dia-bus-demo {
  position: relative;
  width: 100vw;
  height: 100vh;
  overflow: auto;
  background: #050914;
}

.main-component-panel {
  position: relative;
  padding: 72px 24px 24px;
  min-width: 588px;
  min-height: 1006px;
}

.dia-layer {
  position: fixed;
  inset: 0;
  z-index: 1000;
  pointer-events: none;
}

.toolbar {
  position: fixed;
  left: 16px;
  top: 16px;
  z-index: 2000;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  align-items: center;
}

.dia-select {
  width: 240px;
}
</style>
