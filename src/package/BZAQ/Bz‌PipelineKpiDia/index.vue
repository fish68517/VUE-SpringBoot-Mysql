<template>
  <div class="bz-pipeline-dig-container" :style="containerStyle">
    <div v-if="showMask" class="modal-overlay"></div>

    <PipelineDeviceTrendDetail
      v-if="showDeviceDetail"
      :key="detailPopupKey"
      :style="popupStyle"
      :point="activeDetailPoint"
      :popup-type="activeDetailType"
      @close="closeDeviceDetail"
    />

    <PipelineFieldDetail
      v-if="showFieldDetail"
      :key="fieldDetailKey"
      :style="popupStyle"
      :point="activeFieldPoint"
      :title="fieldDetailTitle"
      @close="closeFieldDetail"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import PipelineDeviceTrendDetail from './components/PipelineDeviceTrendDetail.vue'
import PipelineFieldDetail from './components/PipelineFieldDetail.vue'

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

const sourceName = 'Bz‌PipelineKpiDia'

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
  }
}

const detailTypeMap: Record<string, string> = {
  'pipeline-gas-leak-list': 'pipeline-gas-leak-list',
  'pipeline-lighting-device-list': 'pipeline-lighting-device-list',
  'pipeline-gas-network-list': 'pipeline-gas-network-list',
  'pipeline-septic-tank-list': 'pipeline-septic-tank-list',
  'pipeline-medical-sewage-list': 'pipeline-medical-sewage-list',
  'pipeline-outfall-water-list': 'pipeline-outfall-water-list',
  'pipeline-water-station-list': 'pipeline-water-station-list',
  'pipeline-water-use-list': 'pipeline-water-use-list',
  'gas-leak-list': 'pipeline-gas-leak-list',
  'lighting-device-list': 'pipeline-lighting-device-list',
  'gas-network-list': 'pipeline-gas-network-list',
  'septic-tank-list': 'pipeline-septic-tank-list',
  'medical-sewage-list': 'pipeline-medical-sewage-list',
  'medical-wastewater-list': 'pipeline-medical-sewage-list',
  medical: 'pipeline-medical-sewage-list',
  'outfall-water-list': 'pipeline-outfall-water-list',
  'water-station-list': 'pipeline-water-station-list',
  'water-use-list': 'pipeline-water-use-list'
}

const popupDetailTypeMap: Record<string, string> = {
  'pipeline-gas-leak-list': 'gas-leak',
  'pipeline-lighting-device-list': 'water-level',
  'pipeline-gas-network-list': 'gas-network',
  'pipeline-septic-tank-list': 'septic',
  'pipeline-medical-sewage-list': 'medical',
  'pipeline-outfall-water-list': 'outfall',
  'pipeline-water-station-list': 'water-station',
  'pipeline-water-use-list': 'water-use'
}

const showMask = ref(false)
const activeDetailPoint = ref({})
const activeDetailType = ref('')
const showDeviceDetail = ref(false)
const detailPopupKey = ref(0)
const activeFieldPoint = ref({})
const showFieldDetail = ref(false)
const fieldDetailKey = ref(0)

const containerStyle = computed(() => {
  const width = Number(props.chartConfig?.attr?.w)
  const height = Number(props.chartConfig?.attr?.h)

  return {
    width: Number.isFinite(width) && width > 0 ? `${width}px` : '100%',
    height: Number.isFinite(height) && height > 0 ? `${height}px` : '100%'
  }
})

const popupStyle = computed(() => ({
  left: '50%',
  top: '50%'
}))

const fieldDetailTitle = computed(() => {
  const point: any = activeFieldPoint.value || {}
  const raw: any = point._raw || {}
  return point.deviceName || raw.dvname || raw.deviceName || '化粪池详情'
})

function openPopup(payload) {
  if (!payload) return
  const point = payload.point || payload.data || {}
  const normalizedPopup = detailTypeMap[payload.diaName] || detailTypeMap[payload.listType] || payload.diaName
  const detailType = payload.detailType || point.detailType || popupDetailTypeMap[normalizedPopup] || ''
  openDeviceDetail({
    ...point,
    detailType
  })
}

function openDeviceDetail(row) {
  if (row?.detailType === 'septic') {
    activeFieldPoint.value = row || {}
    fieldDetailKey.value += 1
    showFieldDetail.value = true
    showMask.value = true
    return
  }

  activeDetailPoint.value = row || {}
  activeDetailType.value = row?.detailType || ''
  detailPopupKey.value += 1
  showDeviceDetail.value = true
  showMask.value = true
}

function closeDeviceDetail() {
  showDeviceDetail.value = false
  activeDetailPoint.value = {}
  activeDetailType.value = ''
  if (!showFieldDetail.value) showMask.value = false
}

function closeFieldDetail() {
  showFieldDetail.value = false
  activeFieldPoint.value = {}
  if (!showDeviceDetail.value) showMask.value = false
}

const OPEN_PIPELINE_EVENT = 'OPEN_PIPELINE_KPI_DIA'
const CLOSE_EVENT = 'CLOSE_DIA'

onMounted(() => {
  eventBus.on(OPEN_PIPELINE_EVENT, openPopup)
  eventBus.on(CLOSE_EVENT, closeAllDetail)
})

onBeforeUnmount(() => {
  eventBus.off(OPEN_PIPELINE_EVENT, openPopup)
  eventBus.off(CLOSE_EVENT, closeAllDetail)
})

function closeAllDetail() {
  closeDeviceDetail()
  closeFieldDetail()
}

defineExpose({
  openPopup
})
</script>

<script lang="ts">
export default {
  name: 'Bz‌PipelineKpiDia',
  version: '1.0.0'
}
</script>

<style lang="scss" scoped>
.bz-pipeline-dig-container {
  position: relative;
  overflow: visible;
  pointer-events: none;
}

.preview-switcher {
  position: absolute;
  left: 12px;
  top: 12px;
  z-index: 1002;
  width: 210px;
  pointer-events: auto;
}

.preview-select :deep(.n-base-selection) {
  min-height: 30px;
  height: 30px;
  background: rgba(8, 44, 86, 0.88);
  border-radius: 2px;
}

.preview-select :deep(.n-base-selection-label),
.preview-select :deep(.n-base-selection-input),
.preview-select :deep(.n-base-selection-placeholder) {
  height: 28px;
  line-height: 28px;
  color: #d7e9ff;
  font-size: 12px;
}

.preview-select :deep(.n-base-selection-placeholder) {
  color: rgba(184, 217, 255, 0.62);
}

.preview-select :deep(.n-base-selection__border),
.preview-select :deep(.n-base-selection__state-border) {
  border-color: rgba(83, 174, 255, 0.45);
}

.modal-overlay {
  position: fixed;
  inset: 0;
  z-index: 1000;
  pointer-events: auto;
}
</style>
