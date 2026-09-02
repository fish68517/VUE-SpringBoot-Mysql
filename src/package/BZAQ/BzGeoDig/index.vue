<template>
  <div class="bz-fire-kpi-dia-container" :style="containerStyle">
    <!-- <n-select
      v-model:value="previewComponent"
      class="component-preview-select"
      :options="previewOptions"
      placeholder="选择预览组件"
      clearable
      @update:value="handlePreviewChange"
    /> -->
    <div v-if="showMask" class="modal-overlay"></div>

    <GeoRiskKpiPointList
      v-if="activePopup === 'geo-risk-point-list'"
      :key="riskPointListKey"
      :style="popupStyle"
      :risk-label="riskPointLabel"
      @close="closeAllPopup"
    />
    <Geo‌Disaster‌List
      v-if="activePopup === 'geo-‌disaster-list'"
      :style="popupStyle"
      @close="closeAllPopup"
      @open-iframe="openIframePopup"
    />
    <Geo‌EarthquakeWarningList
      v-if="activePopup === 'geo-‌earthquake-warning-list'"
      :style="popupStyle"
      @close="closeAllPopup"
    />
    <Geo‌GridPersonList
      v-if="activePopup === 'geo-‌grid-person-list'"
      :style="popupStyle"
      @close="closeAllPopup"
    />
    <GeoProjectList
      v-if="activePopup === 'geo-‌project-list'"
      :style="popupStyle"
      :type="popupParams.type"
      @close="closeAllPopup"
    />
    <GeoVideoRiskList
      v-if="activePopup === 'geo-‌video-risk-list'"
      :style="popupStyle"
      :is-today="popupParams.isToday"
      @close="closeAllPopup"
    />
    <GeoSmallWatershedList
      v-if="activePopup === 'geo-small-watershed-list'"
      :style="popupStyle"
      @close="closeAllPopup"
    />
    <GeoWaterList
      v-if="activePopup === 'geo-water-list'"
      :style="popupStyle"
      @close="closeAllPopup"
    />
    <GeoDisasterDangerList
      v-if="activePopup === 'geo-disaster-danger-list'"
      :style="popupStyle"
      :type="popupParams.type"
      @close="closeAllPopup"
    />
    <GeoGridPatrolPointList
      v-if="activePopup === 'geo-grid-patrol-point-list'"
      :style="popupStyle"
      @close="closeAllPopup"
    />
    <GeoImagePreview
      v-if="activePopup === 'geo-image-preview'"
      :style="popupStyle"
      :file-id="popupParams.fileId"
      :title="popupParams.title"
      :label="popupParams.label"
      @close="closeAllPopup"
    />
    <GeoHistoryWarnList
      v-if="activePopup === 'geo-history-warn'"
      :style="popupStyle"
      @close="closeAllPopup"
    />
    <GeoWarningDetail
      v-if="warningDetailPopups.includes(activePopup)"
      :style="popupStyle"
      :detail-type="popupParams.detailType"
      :detail="popupParams.detail"
      @close="closeAllPopup"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { NSelect } from 'naive-ui'
import FireKpiList from './components/FireKpiList.vue'
import GeoRiskKpiPointList from './components/GeoRiskKpiPointList.vue'
import Geo‌Disaster‌List from './components/Geo‌Disaster‌List.vue'
import Geo‌EarthquakeWarningList from './components/Geo‌EarthquakeWarningList.vue'
import Geo‌GridPersonList from './components/Geo‌GridPersonList.vue'
import GeoProjectList from './components/Geo‌ProjectList.vue'
import GeoVideoRiskList from './components/GeoVideoRiskList.vue'
import GeoSmallWatershedList from './components/GeoSmallWatershedList.vue'
import GeoWaterList from './components/Geo‌WaterList.vue'
import GeoDisasterDangerList from './components/GeoDisasterDangerList.vue'
import GeoGridPatrolPointList from './components/GeoGridPatrolPointList.vue'
import GeoWarningDetail from './components/GeoWarningDetail.vue'
import GeoImagePreview from './components/GeoImagePreview.vue'
import GeoHistoryWarnList from './components/GeoHistoryWarnList.vue'

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

const sourceName = 'BzGeoDig'
const OPEN_GEO_EVENT = 'OPEN_GEO_DIA'
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

const showMask = ref(false)
const activePopup = ref<string | null>('')
const popupParams = ref({})
const previewComponent = ref<string | null>('geo-disaster-danger-list')
const riskPointListKey = ref(0)
const perceptionDeviceListKey = ref(0)
const riskPointType = ref('all')
const riskPointLabel = ref('风险点')

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
  'geo-‌disaster-list',
  'geo-‌earthquake-warning-list',
  'geo-‌grid-person-list',
  'geo-‌project-list',
  'geo-‌video-risk-list',
  'geo-small-watershed-list',
  'geo-water-list',
  'geo-disaster-danger-list',
  'geo-grid-patrol-point-list',
  'geo-image-preview',
  'geo-history-warn',
  'geo-small-watershed-warning-detail',
  'geo-geological-warning-detail',
  'geo-device-alarm-detail'
]

const previewOptions = [
  { label: '风险点列表', value: 'geo-risk-point-list' },
  { label: '地灾隐患点列表', value: 'geo-‌disaster-list' },
  { label: '地灾预警列表', value: 'geo-‌earthquake-warning-list' },
  { label: '四重网格员列表', value: 'geo-‌grid-person-list' },
  { label: '项目列表', value: 'geo-‌project-list' },
  { label: '视频风险列表', value: 'geo-‌video-risk-list' },
  { label: '小流域列表', value: 'geo-small-watershed-list' },
  { label: '水文列表', value: 'geo-water-list' },
  { label: '灾险情列表', value: 'geo-disaster-danger-list' },
  { label: '本周巡查点位', value: 'geo-grid-patrol-point-list' },
  { label: '小流域预警详情', value: 'geo-small-watershed-warning-detail' },
  { label: '地灾预警详情', value: 'geo-geological-warning-detail' },
  { label: '设备告警详情', value: 'geo-device-alarm-detail' },
  { label: '历史预警', value: 'geo-history-warn' }
]

const warningDetailPopups = [
  'geo-small-watershed-warning-detail',
  'geo-geological-warning-detail',
  'geo-device-alarm-detail'
]

function openPopup(payload) {
  if (!payload || !payload.diaName) return
  if (!allowedPopups.includes(payload.diaName)) return

  const params = payload.params || {}
  previewComponent.value = null
  activePopup.value = payload.diaName
  popupParams.value = params
  showMask.value = true
}

function closeAllPopup() {
  activePopup.value = null
  popupParams.value = {}
  showMask.value = false
  previewComponent.value = null
}

function openIframePopup(payload) {
  eventBus.emit('OPEN_IFRAME_DIA', payload)
  if (payload?.closeGeoPopup && (!payload.sourceDiaName || activePopup.value === payload.sourceDiaName)) {
    closeAllPopup()
  }
}

/**
 * 切换需要预览的弹窗组件。
 * 预警详情会同步设置对应的 detailType，其余列表直接按弹窗名称展示。
 */
function handlePreviewChange(value: string | null) {
  if (!value) {
    closeAllPopup()
    return
  }
  const detailTypeMap: Record<string, string> = {
    'geo-small-watershed-warning-detail': 'smallWatershed',
    'geo-geological-warning-detail': 'geological',
    'geo-device-alarm-detail': 'deviceAlarm'
  }
  activePopup.value = value
  popupParams.value = detailTypeMap[value]
    ? { detailType: detailTypeMap[value], detail: {} }
    : {}
  showMask.value = true
}

onMounted(() => {
  eventBus.on(OPEN_GEO_EVENT, openPopup)
  eventBus.on(CLOSE_EVENT, closeAllPopup)
})

onBeforeUnmount(() => {
  eventBus.off(OPEN_GEO_EVENT, openPopup)
  eventBus.off(CLOSE_EVENT, closeAllPopup)
})
</script>

<script lang="ts">
export default {
  name: 'BzGeoDig',
  version: '1.0.0'
}
</script>

<style lang="scss" scoped>
.bz-fire-kpi-dia-container {
  position: relative;
  overflow: visible;
  pointer-events: none;
}

.component-preview-select {
  position: absolute;
  left: 16px;
  top: 16px;
  z-index: 1100;
  width: 210px;
  pointer-events: auto;
}

.modal-overlay {
  position: absolute;
  inset: 0;
  z-index: 1000;
  pointer-events: auto;
}

.bz-fire-kpi-dia-container > :not(.modal-overlay) {
  pointer-events: auto;
}
</style>
