<template>
  <div class="popup-container" :style="containerStyle">
    <div v-if="showMask" class="popup-mask"></div>

    <WeatherAlertPopup
      v-if="activePopup === 'weather-alert'"
      @close="closeAllPopup"
    />
    <RainfallMonitorPopup
      v-if="activePopup === 'rainfall-monitor'"
      @close="closeAllPopup"
    />
    <EmergencyResponsePopup
      v-if="activePopup === 'emergency-response'"
      @close="closeAllPopup"
    />
    <EmergencyResponseStreetPopup
      v-if="activePopup === 'emergency-response-street' || activePopup === 'water-level'"
      @close="closeAllPopup"
      @upLevel="upLevelPop"
    />
    <WaterLevelPopup
      v-if="activePopup === 'water-level'"
      @close="closeWaterLevel"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import WeatherAlertPopup from './components/WeatherAlertPopup.vue'
import RainfallMonitorPopup from './components/RainfallMonitorPopup.vue'
import EmergencyResponsePopup from './components/EmergencyResponsePopup.vue'
import EmergencyResponseStreetPopup from './components/EmergencyResponseStreetPopup.vue'
import WaterLevelPopup from './components/WaterLevelPopup.vue'

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

const sourceName = 'BzWaterlogNextDia'
const OPEN_EVENT = 'OPEN_DIA'
const CLOSE_EVENT = 'CLOSE_DIA'

const eventBus = {
  on(event, callback) {
    if (!props.bus) return
    props.bus.on(event, ({ source, data }) => {
      // if (source === sourceName) return
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

const activePopup = ref(null)
const showMask = ref(false)

const containerStyle = computed(() => {
  const width = Number(props.chartConfig?.attr?.w)
  const height = Number(props.chartConfig?.attr?.h)
  return {
    width: Number.isFinite(width) && width > 0 ? width + 'px' : '100%',
    height: Number.isFinite(height) && height > 0 ? height + 'px' : '100%'
  }
})



const allowedPopups = [
  'weather-alert',
  'rainfall-monitor',
  'emergency-response',
  'emergency-response-street',
  'water-level'
]

function openPopup(payload) {
  if (!payload || !payload.diaName) return
  if (allowedPopups.includes(payload.diaName)) {
    activePopup.value = payload.diaName
    showMask.value = true
  }
}

function upLevelPop(data) { 
  activePopup.value = 'water-level'
}

function closeWaterLevel() {
  activePopup.value = 'emergency-response-street'
}

function closeAllPopup() {
  activePopup.value = null
  showMask.value = false
}

onMounted(() => {
  eventBus.on(OPEN_EVENT, openPopup)
  eventBus.on(CLOSE_EVENT, closeAllPopup)
})

onBeforeUnmount(() => {
  eventBus.off(OPEN_EVENT, openPopup)
  eventBus.off(CLOSE_EVENT, closeAllPopup)
})
</script>

<script lang="ts">
export default {
  name: "BzWaterlogNextDia",
  version: "1.0.0"
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