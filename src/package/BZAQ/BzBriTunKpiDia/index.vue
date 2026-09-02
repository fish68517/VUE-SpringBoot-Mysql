<template>
  <div class="bz-bri-tun-kpi-dia-container" :style="containerStyle">
    <div v-if="showMask && activePopup" class="modal-overlay"></div>

    <BridgeInfoDetail
      v-if="activePopup === 'bridge-info-detail'"
      :style="popupStyle"
      :payload="popupPayload"
      @close="closeAllPopup"
      @open-pdf-preview="openPreviewPopup('pdf-preview')"
      @open-three-preview="openPreviewPopup('three-image-preview')"
      @open-model-iframe="openModelIframe"
      @open-facility-detail="openPopup"
    />

    <FacilityMonitorDetail
      v-if="activePopup === 'facility-monitor-detail'"
      :style="popupStyle"
      :payload="popupPayload"
      @close="closeAllPopup"
    />

    <PdfPreview
      v-if="activePopup === 'pdf-preview'"
      :style="popupStyle"
      :payload="popupPayload"
      @close="closeAllPopup"
    />

    <ThreeImagePreview
      v-if="activePopup === 'three-image-preview'"
      :style="popupStyle"
      :payload="popupPayload"
      @close="closeAllPopup"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import BridgeInfoDetail from './components/BridgeInfoDetail.vue'
import FacilityMonitorDetail from './components/FacilityMonitorDetail.vue'
import PdfPreview from './components/PdfPreview.vue'
import ThreeImagePreview from './components/ThreeImagePreview.vue'

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

const sourceName = 'BzBriTunKpiDia'
const OPEN_EVENT = 'OPEN_BRIDGE_TUNNEL_KPI_DIA'
const CLOSE_EVENT = 'CLOSE_DIA'

const showMask = ref(false)
const activePopup = ref<string | null>('')
const popupPayload = ref<Record<string, any>>({})

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
  'bridge-info-detail',
  'facility-monitor-detail',
  'pdf-preview',
  'pdf-preview-dialog',
  'three-image-preview',
  'three-d-image-preview',
  '3d-image-preview'
]

const eventBus = {
  on(event: string, callback: any) {
    props.bus?.on(event, callback)
  },
  off(event: string, callback: any) {
    props.bus?.off(event, callback)
  },
  emit(event: string, data: any) {
    props.bus?.emit(event, { data, source: sourceName })
  }
}

function normalizeDiaName(diaName: string) {
  const diaNameMap: Record<string, string> = {
    'pdf-preview-dialog': 'pdf-preview',
    'three-d-image-preview': 'three-image-preview',
    '3d-image-preview': 'three-image-preview'
  }
  return diaNameMap[diaName] || diaName
}

function openPopup(payload: any) {
  if (!payload || !payload.diaName) return
  const diaName = normalizeDiaName(payload.diaName)
  if (!allowedPopups.includes(payload.diaName) && !allowedPopups.includes(diaName)) return
  popupPayload.value = { ...payload, diaName }
  activePopup.value = diaName
  showMask.value = true
}

function openPreviewPopup(diaName: string) {
  openPopup({ ...popupPayload.value, diaName })
}

function openModelIframe(payload: Record<string, any>) {
  eventBus.emit('OPEN_IFRAME_DIA', payload)
}

function closeAllPopup() {
  activePopup.value = null
  popupPayload.value = {}
  showMask.value = false
}

let openListener: ((payload: any) => void) | null = null
let closeListener: (() => void) | null = null

onMounted(() => {
  openListener = ({ source, data }: any) => {
    if (source === sourceName) return
    openPopup(data)
  }
  closeListener = closeAllPopup
  eventBus.on(OPEN_EVENT, openListener)
  eventBus.on(CLOSE_EVENT, closeListener)
})

onBeforeUnmount(() => {
  if (openListener) eventBus.off(OPEN_EVENT, openListener)
  if (closeListener) eventBus.off(CLOSE_EVENT, closeListener)
})

defineExpose({
  openPopup
})
</script>

<script lang="ts">
export default {
  name: 'BzBriTunKpiDia',
  version: '1.0.0'
}
</script>

<style lang="scss" scoped>
.bz-bri-tun-kpi-dia-container {
  position: relative;
  overflow: visible;
  pointer-events: none;
}

.modal-overlay {
  position: absolute;
  left: 50%;
  top: 50%;
  width: 100%;
  height: 100%;
  transform: translate(-50%, -50%);
  z-index: 1000;
  pointer-events: auto;
}

.bz-bri-tun-kpi-dia-container > :not(.modal-overlay) {
  z-index: 1001;
  pointer-events: auto;
}
</style>
