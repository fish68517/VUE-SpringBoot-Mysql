<template>
  <div class="popup-container" :style="containerStyle">
    <!-- 遮罩层（点击不关闭） -->
    <div v-if="showMask" class="popup-mask"></div>

    <!-- 子弹窗 -->
    <WaterlogAlarmList
      v-if="activePopup === 'waterlog-alarm-list'"
      :style="popupStyle"
      @close="closeAllPopup"
    />
    <WaterlogDeviceVideo
      v-if="activePopup === 'waterlog-device-video'"
      :style="popupStyle"
      @close="closeAllPopup"
    />
    <WaterlogDeviceDetail
      v-if="activePopup === 'waterlog-device-detail'"
      :style="popupStyle"
      @close="closeAllPopup"
    />
    <WaterlogLevelMonitor
      v-if="activePopup === 'waterlog-level-monitor'"
      :style="popupStyle"
      @close="closeAllPopup"
    />
    <WaterlogLiquidLevelDetail
      v-if="activePopup === 'waterlog-liquid-level-detail'"
      :style="popupStyle"
      @close="closeAllPopup"
    />
    <WaterlogRiskDetail
      v-if="activePopup === 'waterlog-risk-detail'"
      :style="popupStyle"
      @close="closeAllPopup"
    />
    <WaterlogRoadRiskDetail
      v-if="activePopup === 'waterlog-road-risk-detail'"
      :style="popupStyle"
      @close="closeAllPopup"
    />
    <WaterlogAlarmEventList
      v-if="activePopup === 'waterlog-alarm-event-list' || activePopup === 'waterlog-alarm-detail'"
      :style="popupStyle"
      :round="alarmRound"
      @close="closeAllPopup"
      @detail="openAlarmDetail"
    />
    <WaterlogAlarmDetail
      v-if="activePopup === 'waterlog-alarm-detail'"
      :style="popupStyle"
      :row-data="alarmData"
      :bus="props.bus"
      :round="alarmRound"
      @close="activePopup = 'waterlog-alarm-event-list'"
    />
    <WaterlogRegionalWarningDetail
      v-if="activePopup === 'waterlog-regional-warning-detail'"
      :style="popupStyle"
      :bus="props.bus"
      :qs="regionalRound"
      :round="regionalRoundVal"
      @close="closeAllPopup"
    />
    <WaterlogPointWarningList
      v-if="activePopup === 'waterlog-point-warning-list' || activePopup === 'waterlog-point-warning-detail'"
      :style="popupStyle"
      :round="pointRound"
      @close="closeAllPopup"
      @detail="openPointWarningDetail"
    />
    <WaterlogPointWarningDetail
      v-if="activePopup === 'waterlog-point-warning-detail'"
      :style="popupStyle"
      :row-data="pointWarningData"
      :bus="props.bus"
      :round="pointRound"
      @close="activePopup = 'waterlog-point-warning-list'"
    />
    <WorkOrderDetail
      v-if="showWorkOrderDetail"
      :style="popupStyle"
      :row-data="workOrderData"
      @close="closeWorkOrderDetail"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import WaterlogAlarmList from './components/WaterlogAlarmList.vue'
import WaterlogAlarmEventList from './components/WaterlogAlarmEventList.vue'
import WaterlogDeviceVideo from './components/WaterlogDeviceVideo.vue'
import WaterlogDeviceDetail from './components/WaterlogDeviceDetail.vue'
import WaterlogLevelMonitor from './components/WaterlogLevelMonitor.vue'
import WaterlogLiquidLevelDetail from './components/WaterlogLiquidLevelDetail.vue'
import WaterlogRiskDetail from './components/WaterlogRiskDetail.vue'
import WaterlogRoadRiskDetail from './components/WaterlogRoadRiskDetail.vue'
import WaterlogAlarmDetail from './components/WaterlogAlarmDetail.vue'
import WaterlogRegionalWarningDetail from './components/WaterlogRegionalWarningDetail.vue'
import WaterlogPointWarningDetail from './components/WaterlogPointWarningDetail.vue'
import WaterlogPointWarningList from './components/WaterlogPointWarningList.vue'
import WorkOrderDetail from './components/WorkOrderDetail.vue'

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

const sourceName = 'BzWaterlogDia'
const OPEN_EVENT = 'OPEN_WATER_DIA'
const LEGACY_OPEN_EVENT = 'OPEN_DIA'
const CLOSE_EVENT = 'CLOSE_DIA'

// 事件总线封装
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

// 当前激活的弹窗
const activePopup = ref(null)
const showMask = ref(false)
const pointWarningData = ref({})
const alarmData = ref({})
const workOrderData = ref({})
const showWorkOrderDetail = ref(false)
const previousPopup = ref(null)
const regionalRound = ref(null)
const regionalRoundVal = ref(null)
const pointRound = ref(null)
const alarmRound = ref(null)

// 容器宽高跟随低代码平台拖动的 attr.w / attr.h（computed 确保响应式更新）
const containerStyle = computed(() => {
  const width = Number(props.chartConfig?.attr?.w)
  const height = Number(props.chartConfig?.attr?.h)
  return {
    width: Number.isFinite(width) && width > 0 ? width + 'px' : '100%',
    height: Number.isFinite(height) && height > 0 ? height + 'px' : '100%'
  }
})

// 弹窗居中定位（computed 确保响应式）
const popupStyle = computed(() => ({
  left: '50%',
  top: '50%'
}))

const allowedPopups = [
  'waterlog-alarm-list',
  'waterlog-alarm-event-list',
  'waterlog-device-video',
  'waterlog-device-detail',
  'waterlog-level-monitor',
  'waterlog-liquid-level-detail',
  'waterlog-risk-detail',
  'waterlog-road-risk-detail',
  'waterlog-alarm-detail',
  'waterlog-regional-warning-detail',
  'waterlog-point-warning-detail',
  'waterlog-point-warning-list',
  'work-order-detail'
]

// 打开弹窗
function openPopup(payload) {
  if (!payload || !payload.diaName) return
  if (allowedPopups.includes(payload.diaName)) {
    activePopup.value = payload.diaName
    showMask.value = true
    if (payload.diaName === 'waterlog-regional-warning-detail') {
      if (payload.qs !== undefined) regionalRound.value = payload.qs
      if (payload.round !== undefined) regionalRoundVal.value = payload.round
    }
    if (payload.diaName === 'waterlog-point-warning-list' && payload.round !== undefined) {
      pointRound.value = payload.round
    }
    if (payload.diaName === 'waterlog-alarm-event-list' && payload.round !== undefined) {
      alarmRound.value = payload.round
    }
  }
}

// 关闭所有弹窗
function closeAllPopup() {
  activePopup.value = null
  showMask.value = false
  showWorkOrderDetail.value = false
}

function openPointWarningDetail(data) {
  pointWarningData.value = data || {}
  activePopup.value = 'waterlog-point-warning-detail'
  showMask.value = true
}

function openAlarmDetail(data) {
  alarmData.value = data || {}
  activePopup.value = 'waterlog-alarm-detail'
  showMask.value = true
}

function openWorkOrderDetail(data) {
  workOrderData.value = data || {}
  showWorkOrderDetail.value = true
}

function closeWorkOrderDetail() {
  showWorkOrderDetail.value = false
}

function openDiaAfterClose(diaName) {
  const payload = { diaName }
  eventBus.emit(CLOSE_EVENT, null)
  closeAllPopup()
  eventBus.emit(OPEN_EVENT, payload)
  openPopup(payload)
}

const WORK_ORDER_EVENT = 'OPEN_WORK_ORDER_DETAIL'

onMounted(() => {
  eventBus.on(OPEN_EVENT, openPopup)
  eventBus.on(LEGACY_OPEN_EVENT, openPopup)
  eventBus.on(CLOSE_EVENT, closeAllPopup)
  eventBus.on(WORK_ORDER_EVENT, openWorkOrderDetail)
})

onBeforeUnmount(() => {
  eventBus.off(OPEN_EVENT, openPopup)
  eventBus.off(LEGACY_OPEN_EVENT, openPopup)
  eventBus.off(CLOSE_EVENT, closeAllPopup)
  eventBus.off(WORK_ORDER_EVENT, openWorkOrderDetail)
})
</script>

<script lang="ts">
export default {
  name: "BzWaterlogDia",
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
