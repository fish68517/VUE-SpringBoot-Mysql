<template>
  <div class="bz-pipeline-dig-container" :style="containerStyle">
    <!-- <div class="preview-switcher">
      <n-select
        v-model:value="selectedPreview"
        class="preview-select"
        :options="previewOptions"
        clearable
        placeholder="选择弹窗预览"
        size="small"
        @update:value="handlePreviewChange"
      />
    </div> -->

    <div v-if="showMask" class="modal-overlay"></div>

    <component
      :is="activeListComponent"
      v-if="activeListComponent"
      :key="popupKey"
      :style="popupStyle"
      :title="activeTitle"
      :payload="activePayload"
      @open-detail="openDeviceDetail"
      @close="closeAllPopup"
    />

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

    <PipelineWaterStationDetail
      v-if="activePopup === waterDetailPopupName"
      :key="popupKey"
      :style="popupStyle"
      :point="activePoint"
      @close="closeAllPopup"
    />

    <PipelineGasNetworkDetail
      v-if="activePopup === gasNetworkDetailPopupName"
      :key="popupKey"
      :style="popupStyle"
      @close="closeAllPopup"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { NSelect } from 'naive-ui'
import PipelineDeviceTrendDetail from './components/PipelineDeviceTrendDetail.vue'
import PipelineFieldDetail from './components/PipelineFieldDetail.vue'
import PipelineGasLeakList from './components/PipelineGasLeakList.vue'
import PipelineGasNetworkList from './components/PipelineGasNetworkList.vue'
import PipelineGasNetworkDetail from './components/PipelineGasNetworkDetail.vue'
import PipelineLightingDeviceList from './components/PipelineLightingDeviceList.vue'
import PipelineMedicalSewageList from './components/PipelineMedicalSewageList.vue'
import PipelineOutfallWaterList from './components/PipelineOutfallWaterList.vue'
import PipelineSepticTankList from './components/PipelineSepticTankList.vue'
import PipelineWaterStationList from './components/PipelineWaterStationList.vue'
import PipelineWaterStationDetail from './components/PipelineWaterStationDetail.vue'
import PipelineWaterUseList from './components/PipelineWaterUseList.vue'

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

const sourceName = 'Bz‌PipelineDig'

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

const popupMap: Record<string, string> = {
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

const listComponentMap = {
  'pipeline-gas-leak-list': PipelineGasLeakList,
  'pipeline-lighting-device-list': PipelineLightingDeviceList,
  'pipeline-gas-network-list': PipelineGasNetworkList,
  'pipeline-septic-tank-list': PipelineSepticTankList,
  'pipeline-medical-sewage-list': PipelineMedicalSewageList,
  'pipeline-outfall-water-list': PipelineOutfallWaterList,
  'pipeline-water-station-list': PipelineWaterStationList,
  'pipeline-water-use-list': PipelineWaterUseList
}

const showMask = ref(false)
const activePopup = ref<string | null>(null)
const activePoint = ref({})
const activeTitle = ref('')
const activePayload = ref({})
const activeDetailPoint = ref({})
const activeDetailType = ref('')
const showDeviceDetail = ref(false)
const activeFieldPoint = ref({})
const showFieldDetail = ref(false)
const popupKey = ref(0)
const detailPopupKey = ref(0)
const fieldDetailKey = ref(0)
const selectedPreview = ref<string | null>(null)
const waterDetailPopupName = 'pipeline-water-station-detail'
const gasNetworkDetailPopupName = 'pipeline-gas-network-detail'

const previewOptions = [
  { label: '燃气泄漏列表', value: 'pipeline-gas-leak-list' },
  { label: '照明设备列表', value: 'pipeline-lighting-device-list' },
  { label: '地下管网气体设备', value: 'pipeline-gas-network-list' },
  { label: '化粪池列表', value: 'pipeline-septic-tank-list' },
  { label: '医疗污水列表', value: 'pipeline-medical-sewage-list' },
  { label: '排江口水质预警列表', value: 'pipeline-outfall-water-list' },
  { label: '水质监测站列表', value: 'pipeline-water-station-list' },
  { label: '用水监测列表', value: 'pipeline-water-use-list' },
  { label: '水质监测站详情', value: waterDetailPopupName },
  { label: '地下管网气体详情', value: gasNetworkDetailPopupName }
]

const activeListComponent = computed(() => {
  if (!activePopup.value || activePopup.value === waterDetailPopupName || activePopup.value === gasNetworkDetailPopupName) return null
  return listComponentMap[activePopup.value] || null
})

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
  return point.deviceName || point.dvname || point.name || '管线详情'
})

function openPopup(payload) {
  if (!payload?.diaName) return

  if (payload.diaName === waterDetailPopupName) {
    activePopup.value = payload.diaName
    selectedPreview.value = payload.diaName
    activePoint.value = payload.point || payload.data || {}
    activeTitle.value = payload.title || ''
    activePayload.value = payload
    popupKey.value += 1
    showMask.value = true
    return
  }

  if (payload.diaName === gasNetworkDetailPopupName) {
    activePopup.value = payload.diaName
    selectedPreview.value = payload.diaName
    activePoint.value = {}
    activeTitle.value = payload.title || ''
    activePayload.value = payload
    popupKey.value += 1
    showMask.value = true
    return
  }

  const nextPopup = popupMap[payload.diaName] || popupMap[payload.listType]
  if (!nextPopup) return

  activePopup.value = nextPopup
  selectedPreview.value = nextPopup
  activePoint.value = {}
  activeTitle.value = payload.title || ''
  activePayload.value = payload
  popupKey.value += 1
  showMask.value = true
}

function closeAllPopup() {
  activePopup.value = null
  activePoint.value = {}
  activeTitle.value = ''
  activePayload.value = {}
  closeDeviceDetail()
  closeFieldDetail()
  selectedPreview.value = null
  showMask.value = false
}

function getDetailType(row) {
  if (activePopup.value === 'pipeline-gas-leak-list') return 'gas-leak'
  if (activePopup.value === 'pipeline-lighting-device-list') return row.detailType || 'water-level'
  if (activePopup.value === 'pipeline-gas-network-list') return 'gas-network'
  if (activePopup.value === 'pipeline-medical-sewage-list') return 'medical'
  if (activePopup.value === 'pipeline-outfall-water-list') return 'outfall'
  if (activePopup.value === 'pipeline-water-station-list') return 'water-station'
  if (activePopup.value === 'pipeline-water-use-list') return 'water-use'
  return ''
}

function openDeviceDetail(row) {
  const detailType = row?.detailType || getDetailType(row)
  const detailPoint = {
    ...(row || {}),
    detailType
  }

  if (detailType === 'septic') {
    activeFieldPoint.value = detailPoint
    fieldDetailKey.value += 1
    showFieldDetail.value = true
    return
  }

  activeDetailPoint.value = detailPoint
  activeDetailType.value = detailType
  detailPopupKey.value += 1
  showDeviceDetail.value = true
}

function closeDeviceDetail() {
  showDeviceDetail.value = false
  activeDetailPoint.value = {}
  activeDetailType.value = ''
}

function closeFieldDetail() {
  showFieldDetail.value = false
  activeFieldPoint.value = {}
}

function handlePreviewChange(value: string | null) {
  if (!value) {
    closeAllPopup()
    return
  }

  openPopup({
    diaName: value,
    point: {
      label: '长江流域朝天门站',
      deviceCode: 'YZQ-SZ-002',
      deviceType: '地表水小型自动监测站（嘉陵江流域朝天门站）',
      deviceAddress: '重庆市渝中区朝天门街道朝千路社区朝天门6号码头'
    }
  })
}

const OPEN_EVENT = 'OPEN_DIA'
const OPEN_PIPELINE_EVENT = 'OPEN_PIPELINE_DIA'
const CLOSE_EVENT = 'CLOSE_DIA'

onMounted(() => {
  eventBus.on(OPEN_EVENT, openPopup)
  eventBus.on(OPEN_PIPELINE_EVENT, openPopup)
  eventBus.on(CLOSE_EVENT, closeAllPopup)
})

onBeforeUnmount(() => {
  eventBus.off(OPEN_EVENT, openPopup)
  eventBus.off(OPEN_PIPELINE_EVENT, openPopup)
  eventBus.off(CLOSE_EVENT, closeAllPopup)
})

defineExpose({
  openPopup
})
</script>

<script lang="ts">
export default {
  name: 'Bz‌PipelineDig',
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
