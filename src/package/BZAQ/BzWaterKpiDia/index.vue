<template>
  <div class="popup-container" :style="containerStyle">
    <div v-if="showMask" class="popup-mask"></div>

    <WaterlogPerceptionDeviceList
      v-if="activePopup === 'waterlog-perception-device-list'"
      :style="popupStyle"
      @close="closeAllPopup"
      @detail="openWaterlogDetail"
    />
    <WaterlogRiskPointList
      v-if="activePopup === 'waterlog-risk-point-list'"
      :style="popupStyle"
      @close="closeAllPopup"
    />
    <WaterlogRiskDetail
      v-if="activePopup === 'waterlog-risk-detail'"
      :style="popupStyle"
      :id="popupData?.id || ''"
      :name="popupData?.name || ''"
      :bus="props.bus"
      @close="closeAllPopup"
    />
    <StationWaterLevelDia
      v-if="activePopup === 'station-water-level'"
      :style="popupStyle"
      :station-name="popupData?.stationName || '朝天门'"
      @close="closeAllPopup"
    />
    <WaterlogPredictionDia
      v-if="activePopup === 'water-prediction'"
      :style="popupStyle"
      @close="closeAllPopup"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import WaterlogPerceptionDeviceList from './components/WaterlogPerceptionDeviceList.vue'
import WaterlogRiskPointList from './components/WaterlogRiskPointList.vue'
import WaterlogRiskDetail from './components/WaterlogRiskDetail.vue'
import StationWaterLevelDia from './components/StationWaterLevelDia.vue'
import WaterlogPredictionDia from './components/WaterlogPredictionDia.vue'

const props = defineProps({
  chartConfig: {
    type: Object,
    required: true
  },
  publicParamList: {
    type: Object,
    default: () => []
  },
  bus: {
    type: Object,
    default: null
  }
})

const sourceName = 'BzWaterKpiDia'
const OPEN_WATER_KPI_EVENT = 'OPEN_WATER_KPI_DIA'
const OPEN_WATER_DIA_EVENT = 'OPEN_WATER_DIA'
const LEGACY_OPEN_EVENT = 'OPEN_DIA'
const CLOSE_EVENT = 'CLOSE_DIA'

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

const activePopup = ref("")
const popupData = ref(true)
const showMask = ref(false)

const containerStyle = computed(() => {
  const width = Number(props.chartConfig?.attr?.w)
  const height = Number(props.chartConfig?.attr?.h)
  return {
    width: Number.isFinite(width) && width > 0 ? width + 'px' : '100%',
    height: Number.isFinite(height) && height > 0 ? height + 'px' : '100%'
  }
})

const popupStyle = computed(() => ({
  left: '50%',
  top: '50%'
}))

const allowedPopups = [
  'waterlog-device-list',
  'waterlog-perception-device-list',
  'waterlog-risk-point-list',
  'waterlog-risk-detail',
  'station-water-level',
  'water-prediction'
]

function openPopup(payload) {
  if (!payload || !payload.diaName) return
  if (allowedPopups.includes(payload.diaName)) {
    activePopup.value = payload.diaName
    popupData.value = payload
    showMask.value = true
  }
}

function closeAllPopup() {
  activePopup.value = null
  popupData.value = null
  showMask.value = false
}

function openWaterlogDetail(diaName) {
  if (!diaName) return
  const payload = { diaName }
  eventBus.emit(CLOSE_EVENT, null)
  closeAllPopup()
  eventBus.emit(OPEN_WATER_DIA_EVENT, payload)
}

onMounted(() => {
  eventBus.on(OPEN_WATER_KPI_EVENT, openPopup)
  eventBus.on(LEGACY_OPEN_EVENT, openPopup)
  eventBus.on(CLOSE_EVENT, closeAllPopup)
})

onBeforeUnmount(() => {
  eventBus.off(OPEN_WATER_KPI_EVENT, openPopup)
  eventBus.off(LEGACY_OPEN_EVENT, openPopup)
  eventBus.off(CLOSE_EVENT, closeAllPopup)
})
</script>

<script lang="ts">
export default {
  name: 'BzWaterKpiDia',
  version: '1.0.0'
}
</script>

<style lang="scss" scoped>
.popup-container {
  position: absolute;
  left: 0;
  top: 0;
  overflow: visible;
  pointer-events: none;
}

.popup-mask {
  position: absolute;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  z-index: 1000;
  pointer-events: auto;
}

.popup-container > :not(.popup-mask) {
  pointer-events: auto;
}
</style>
