<template>
  <div class="popup-container" :style="containerStyle">
    <div v-if="showMask" class="popup-mask"></div>

    <GeoRiskPointList
      v-if="activePopup === 'geo-risk-point-list'"
      :style="popupStyle"
      @close="closeAllPopup"
      @open-small-watershed-detail="openSmallWatershedImageDetail"
    />
    <GeoPerceptionDeviceList
      v-if="activePopup === 'geo-perception-device-list'"
      :style="popupStyle"
      @close="closeAllPopup"
    />
    <GeoGridMemberDetail
      v-if="activePopup === 'geo-grid-member-detail'"
      :style="popupStyle"
      :detail="popupData"
      @close="closeAllPopup"
    />
    <GeoOldRiskPointDetail
      v-if="activePopup === 'geo-old-risk-point-detail'"
      :style="popupStyle"
      :detail="popupData"
      :layer-name="popupPayload.layerName"
      @close="closeAllPopup"
    />
    <GeoKeySlopeUnitDetail
      v-if="activePopup === 'geo-key-slope-unit-detail'"
      :style="popupStyle"
      :detail="popupData"
      @close="closeAllPopup"
    />
    <GeoPointDetail
      v-if="activePopup === 'geo-point-detail'"
      :style="popupStyle"
      :detail="popupData"
      :show-trend="Boolean(popupPayload.showTrend)"
      :point-type="popupPayload.pointType"
      @close="closeAllPopup"
    />
    <GeoSmallWatershedImageDetail
      v-if="smallWatershedImageVisible"
      :detail="smallWatershedImageDetail"
      @close="closeSmallWatershedImageDetail"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import GeoRiskPointList from './components/GeoRiskPointList.vue'
import GeoPerceptionDeviceList from './components/GeoPerceptionDeviceList.vue'
import GeoGridMemberDetail from './components/GeoGridMemberDetail.vue'
import GeoOldRiskPointDetail from './components/GeoOldRiskPointDetail.vue'
import GeoKeySlopeUnitDetail from './components/GeoKeySlopeUnitDetail.vue'
import GeoPointDetail from './components/GeoPointDetail.vue'
import GeoSmallWatershedImageDetail from './components/GeoSmallWatershedImageDetail.vue'

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

const sourceName = 'BzGeoKpiDia'
const OPEN_GEO_EVENT = 'OPEN_GEO_KPI_DIA'
const LEGACY_OPEN_EVENT = 'OPEN_DIA'
const CLOSE_EVENT = 'CLOSE_DIA'
const eventCallbackMap = new Map<string, Map<Function, Function>>()

const eventBus = {
  on(event, callback) {
    if (!props.bus) return
    const wrappedCallback = ({ source, data }) => {
      if (source === sourceName) return
      callback(data)
    }
    if (!eventCallbackMap.has(event)) eventCallbackMap.set(event, new Map())
    eventCallbackMap.get(event)?.set(callback, wrappedCallback)
    props.bus.on(event, wrappedCallback)
  },
  off(event, callback) {
    if (!props.bus) return
    const wrappedCallback = eventCallbackMap.get(event)?.get(callback)
    props.bus.off(event, wrappedCallback || callback)
    eventCallbackMap.get(event)?.delete(callback)
  },
  emit(event, data) {
    if (!props.bus) return
    props.bus.emit(event, { data, source: sourceName })
  }
}

const activePopup = ref('')
const popupData = ref({})
const popupPayload = ref({})
const showMask = ref(false)
const smallWatershedImageVisible = ref(false)
const smallWatershedImageDetail = ref({})

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
  'geo-risk-point-list',
  'geo-perception-device-list',
  'geo-grid-member-detail',
  'geo-old-risk-point-detail',
  'geo-key-slope-unit-detail',
  'geo-point-detail',
  'geo-small-watershed-image-detail'
]

function openPopup(payload) {
  if (!payload || !payload.diaName) return
  if (payload.diaName === 'geo-small-watershed-image-detail') {
    openSmallWatershedImageDetail(payload.data || payload.detail || payload)
    return
  }
  if (allowedPopups.includes(payload.diaName)) {
    popupPayload.value = payload
    popupData.value = payload.data || payload
    activePopup.value = payload.diaName
    showMask.value = true
  }
}

function openSmallWatershedImageDetail(detail = {}) {
  smallWatershedImageDetail.value = detail
  smallWatershedImageVisible.value = true
}

function closeSmallWatershedImageDetail() {
  smallWatershedImageVisible.value = false
  smallWatershedImageDetail.value = {}
}

function closeAllPopup() {
  activePopup.value = null
  popupData.value = {}
  popupPayload.value = {}
  showMask.value = false
  closeSmallWatershedImageDetail()
}

onMounted(() => {
  eventBus.on(OPEN_GEO_EVENT, openPopup)
  eventBus.on(LEGACY_OPEN_EVENT, openPopup)
  eventBus.on(CLOSE_EVENT, closeAllPopup)
})

onBeforeUnmount(() => {
  eventBus.off(OPEN_GEO_EVENT, openPopup)
  eventBus.off(LEGACY_OPEN_EVENT, openPopup)
  eventBus.off(CLOSE_EVENT, closeAllPopup)
})
</script>

<script lang="ts">
export default {
  name: 'BzGeoKpiDia',
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
