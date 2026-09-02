<template>
  <div class="bz-fire-kpi-dia-container" :style="containerStyle">
    <!-- <n-select
      v-model:value="previewPopup"
      class="popup-preview-select"
      :options="previewPopupOptions"
      clearable
      placeholder="选择点图层弹窗预览"
      @update:value="handlePreviewPopupChange"
    /> -->

    <div v-if="showMask" class="modal-overlay" @click="closeAllPopup"></div>

    <!-- 列表层弹窗 -->
    <FireRiskPointList
      v-if="activePopup === 'fire-risk-point-list'"
      :key="riskPointListKey"
      :style="popupStyle"
      :risk-type="riskPointType"
      :risk-label="riskPointLabel"
      @open-detail="openDetailPopup"
      @close="closeAllPopup"
    />
    <FirePerceptionDeviceList
      v-if="activePopup === 'fire-perception-device-list'"
      :key="perceptionDeviceListKey"
      :style="popupStyle"
      @open-detail="openDetailPopup"
      @close="closeAllPopup"
    />
    <FireKpiList
      v-if="activePopup === 'fire-kpi-list'"
      :style="popupStyle"
      @close="closeAllPopup"
    />

    <!-- 详情层弹窗（覆盖在列表之上） -->
    <FireHighBuildingList
      v-if="detailPopup === 'fire-high-building-list'"
      :key="highBuildingListKey"
      :style="popupStyle"
      :name="highBuildingName"
      :fxdj="highBuildingRiskLevel"
      @close="closeDetailPopup"
    />
    <FireNineSmallList
      v-if="detailPopup === 'fire-nine-small-list'"
      :key="nineSmallListKey"
      :style="popupStyle"
      :name="nineSmallName"
      :type="nineSmallType"
      @close="closeDetailPopup"
    />
    <FireSepticTankList
      v-if="detailPopup === 'fire-septic-tank-list'"
      :key="septicTankListKey"
      :style="popupStyle"
      :name="septicTankName"
      :type="septicTankType"
      @close="closeDetailPopup"
    />
    <FireWaterPressureList
      v-if="detailPopup === 'fire-water-pressure-list'"
      :key="waterPressureListKey"
      :style="popupStyle"
      :name="waterPressureName"
      :build-name="waterPressureBuildName"
      :type="waterPressureType"
      @close="closeDetailPopup"
    />
    <FireLifeChannelList
      v-if="detailPopup === 'fire-life-channel-list'"
      :key="lifeChannelListKey"
      :style="popupStyle"
      :name="lifeChannelName"
      :type="lifeChannelType"
      :device-num="lifeChannelDeviceNum"
      :video-url="lifeChannelVideoUrl"
      @close="closeDetailPopup"
    />
    <FireControlRoomList
      v-if="detailPopup === 'fire-control-room-list'"
      :key="controlRoomListKey"
      :style="popupStyle"
      :name="controlRoomName"
      :type="controlRoomType"
      :device-num="controlRoomDeviceNum"
      :video-url="controlRoomVideoUrl"
      @close="closeDetailPopup"
    />
    <FireGasDeviceList
      v-if="detailPopup === 'fire-gas-device-list'"
      :key="gasDeviceListKey"
      :style="popupStyle"
      :name="gasDeviceName"
      :type="gasDeviceType"
      @close="closeDetailPopup"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { NSelect } from 'naive-ui'
import FireKpiList from './components/FireKpiList.vue'
import FireRiskPointList from './components/FireRiskPointList.vue'
import FirePerceptionDeviceList from './components/FirePerceptionDeviceList.vue'
import FireHighBuildingList from './components/FireHighBuildingList.vue'
import FireNineSmallList from './components/FireNineSmallList.vue'
import FireSepticTankList from './components/FireSepticTankList.vue'
import FireWaterPressureList from './components/FireWaterPressureList.vue'
import FireLifeChannelList from './components/FireLifeChannelList.vue'
import FireControlRoomList from './components/FireControlRoomList.vue'
import FireGasDeviceList from './components/FireGasDeviceList.vue'

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

const sourceName = 'BzFireKpiDia'
const OPEN_FIRE_KPI_EVENT = 'OPEN_FIRE_KPI_DIA'
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
  }
}

const showMask = ref(false)
const previewPopup = ref<string | null>(null)
const activePopup = ref<string | null>('')  // 列表层弹窗
const detailPopup = ref<string | null>(null) // 详情层弹窗（覆盖在列表之上）
const riskPointListKey = ref(0)
const perceptionDeviceListKey = ref(0)
const highBuildingListKey = ref(0)
const nineSmallListKey = ref(0)
const septicTankListKey = ref(0)
const waterPressureListKey = ref(0)
const lifeChannelListKey = ref(0)
const controlRoomListKey = ref(0)
const gasDeviceListKey = ref(0)
const riskPointType = ref('all')
const riskPointLabel = ref('风险点')
const highBuildingName = ref('')
const highBuildingRiskLevel = ref('')
const nineSmallName = ref('')
const nineSmallType = ref('')
const septicTankName = ref('')
const septicTankType = ref('')
const waterPressureName = ref('')
const waterPressureType = ref('')
const waterPressureBuildName = ref('')
const lifeChannelName = ref('')
const lifeChannelType = ref('')
const lifeChannelDeviceNum = ref('')
const lifeChannelVideoUrl = ref('')
const controlRoomName = ref('')
const controlRoomType = ref('')
const controlRoomDeviceNum = ref('')
const controlRoomVideoUrl = ref('')
const gasDeviceName = ref('')
const gasDeviceType = ref('')

const previewPopupOptions = [
  { label: '高层建筑', value: 'fire-high-building-list' },
  { label: '九小场所', value: 'fire-nine-small-list' },
  { label: '化粪池', value: 'fire-septic-tank-list' },
  { label: '水压监测', value: 'fire-water-pressure-list' },
  { label: '生命通道', value: 'fire-life-channel-list' },
  { label: '消防控制室', value: 'fire-control-room-list' },
  { label: '燃气设备', value: 'fire-gas-device-list' }
]

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

const listPopups = [
  'fire-kpi-list',
  'fire-risk-point-list',
  'fire-perception-device-list'
]

const detailPopups = [
  'fire-high-building-list',
  'fire-nine-small-list',
  'fire-septic-tank-list',
  'fire-water-pressure-list',
  'fire-life-channel-list',
  'fire-control-room-list',
  'fire-gas-device-list'
]

const allowedPopups = [...listPopups, ...detailPopups]

// 打开列表层弹窗（由事件总线触发）
function openPopup(payload) {
  if (!payload || !payload.diaName) return
  if (!allowedPopups.includes(payload.diaName)) return

  if (detailPopups.includes(payload.diaName)) {
    activePopup.value = null
    openDetailPopup(payload)
    showMask.value = true
    return
  }

  const params = payload.params || {}
  if (payload.diaName === 'fire-risk-point-list') {
    riskPointType.value = payload.riskType || params.riskType || 'all'
    riskPointLabel.value = payload.riskLabel || params.riskLabel || '风险点'
    riskPointListKey.value += 1
  }
  if (payload.diaName === 'fire-perception-device-list') {
    perceptionDeviceListKey.value += 1
  }

  activePopup.value = payload.diaName
  detailPopup.value = null
  showMask.value = true
}

// 打开详情层弹窗（由列表项点击触发，不关闭列表）
function openDetailPopup(payload) {
  if (!payload || !payload.diaName) return
  if (!allowedPopups.includes(payload.diaName)) return

  const params = payload.params || {}
  if (payload.diaName === 'fire-high-building-list') {
    highBuildingName.value = payload.name || params.name || ''
    highBuildingRiskLevel.value = payload.fxdj || params.fxdj || ''
    highBuildingListKey.value += 1
  }
  if (payload.diaName === 'fire-nine-small-list') {
    nineSmallName.value = payload.name || params.name || ''
    nineSmallType.value = payload.type || params.type || ''
    nineSmallListKey.value += 1
  }
  if (payload.diaName === 'fire-septic-tank-list') {
    septicTankName.value = payload.name || params.name || ''
    septicTankType.value = payload.type || params.type || ''
    septicTankListKey.value += 1
  }
  if (payload.diaName === 'fire-water-pressure-list') {
    waterPressureName.value = payload.name || params.name || ''
    waterPressureType.value = payload.type || params.type || ''
    waterPressureBuildName.value = payload.buildName || params.buildName || ''
    waterPressureListKey.value += 1
  }
  if (payload.diaName === 'fire-life-channel-list') {
    lifeChannelName.value = payload.name || params.name || ''
    lifeChannelType.value = payload.type || params.type || ''
    lifeChannelDeviceNum.value = payload.deviceNum || params.deviceNum || ''
    lifeChannelVideoUrl.value = payload.videoUrl || params.videoUrl || ''
    lifeChannelListKey.value += 1
  }
  if (payload.diaName === 'fire-control-room-list') {
    controlRoomName.value = payload.name || params.name || ''
    controlRoomType.value = payload.type || params.type || ''
    controlRoomDeviceNum.value = payload.deviceNum || params.deviceNum || ''
    controlRoomVideoUrl.value = payload.videoUrl || params.videoUrl || ''
    controlRoomListKey.value += 1
  }
  if (payload.diaName === 'fire-gas-device-list') {
    gasDeviceName.value = payload.name || params.name || 'BA2402PBJG11840'
    gasDeviceType.value = payload.type || params.type || ''
    gasDeviceListKey.value += 1
  }

  detailPopup.value = payload.diaName
}

// 关闭详情层弹窗（列表层保持显示）
function closeDetailPopup() {
  detailPopup.value = null
  if (!activePopup.value) {
    showMask.value = false
  }
}

// 关闭所有弹窗
function closeAllPopup() {
  activePopup.value = null
  detailPopup.value = null
  previewPopup.value = null
  showMask.value = false
}

function handlePreviewPopupChange(value) {
  if (!value) {
    closeAllPopup()
    return
  }

  openPopup({ diaName: value })
}

onMounted(() => {
  eventBus.on(OPEN_FIRE_KPI_EVENT, openPopup)
  eventBus.on(CLOSE_EVENT, closeAllPopup)
})

onBeforeUnmount(() => {
  eventBus.off(OPEN_FIRE_KPI_EVENT, openPopup)
  eventBus.off(CLOSE_EVENT, closeAllPopup)
})
</script>

<script lang="ts">
export default {
  name: 'BzFireKpiDia',
  version: '1.0.0'
}
</script>

<style lang="scss" scoped>
.bz-fire-kpi-dia-container {
  position: relative;
  overflow: visible;
  pointer-events: none;
}

.popup-preview-select {
  position: absolute;
  top: 16px;
  left: 16px;
  z-index: 1002;
  width: 220px;
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
