<template>
  <div class="popup-container" :style="containerStyle">
    <!-- 遮罩层（点击不关闭） -->
    <div v-if="showMask" class="popup-mask"></div>

    <!-- 房屋列表弹窗 -->
    <HouseList
      v-if="activePopup === 'house-list'"
      :style="popupStyle"
      :house-type="houseListType"
      :bus="props.bus"
      @close="closeAllPopup"
      @show-inspection-detail="openInspectionDetail"
    />

    <!-- 住宿人员列表弹窗 -->
    <HousePersonList
      v-if="activePopup === 'house-person-list'"
      :style="popupStyle"
      :house-type="housePersonListType"
      :bus="props.bus"
      @close="closeAllPopup"
      @show-inspection-detail="openInspectionDetail"
    />

    <!-- 风险感知弹窗 -->
    <HouseRiskPerception
      v-if="activePopup === 'house-risk-perception'"
      :style="popupStyle"
      :chart-config="props.chartConfig"
      :public-param-list="props.publicParamList"
      :bus="props.bus"
      :house-type="houseRiskPerceptionType"
      @close="closeAllPopup"
      @show-warning-detail="openWarningDetail"
    />

    <!-- 巡查弹窗 -->
    <HouseInspection
      v-if="activePopup === 'house-inspection'"
      :style="popupStyle"
      @close="closeAllPopup"
      @show-inspection-detail="openInspectionDetail"
    />

    <!-- 风险预警弹窗 -->
    <HouseRiskWarning
      v-if="activePopup === 'house-risk-warning'"
      :style="popupStyle"
      :bus="props.bus"
      @close="closeAllPopup"
    />

    <!-- 预警详情弹窗 -->
    <WarningDetail
      v-if="activePopup === 'warning-detail'"
      :style="popupStyle"
      :detail-data="warningDetailData"
      :id="warningDetailId"
      :house-type="warningDetailHouseType"
      @close="closeAllPopup"
    />

    <!-- 巡查详情弹窗（独立显示，关闭只关闭自身） -->
    <InspectionDetail
      v-if="inspectionDetailVisible"
      :style="popupStyle"
      :detail-data="inspectionDetailData"
      @close="closeInspectionDetail"
    />

    <!-- 四色赋码弹窗 -->
    <HouseColorCode
      v-if="activePopup === 'house-color-code'"
      :style="popupStyle"
      :house-type="houseColorCodeType"
      :bus="props.bus"
      @close="closeAllPopup"
    />

    <!-- 网格巡查隐患弹窗 -->
    <HouseGridInspectionHidden
      v-if="activePopup === 'house-grid-inspection-hidden'"
      :style="popupStyle"
      @close="closeAllPopup"
    />

    <!-- 近期动火作业隐患弹窗 -->
    <HouseFireHotWorkHidden
      v-if="activePopup === 'house-fire-work-hidden'"
      :style="popupStyle"
      @close="closeAllPopup"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import HouseList from './components/HouseList.vue'
import HousePersonList from './components/HousePersonList.vue'
import HouseRiskPerception from './components/HouseRiskPerception.vue'
import HouseInspection from './components/HouseInspection.vue'
import HouseRiskWarning from './components/HouseRiskWarning.vue'
import WarningDetail from './components/WarningDetail.vue'
import InspectionDetail from './components/InspectionDetail.vue'
import HouseColorCode from './components/HouseColorCode.vue'
import HouseGridInspectionHidden from './components/HouseGridInspectionHidden.vue'
import HouseFireHotWorkHidden from './components/HouseFireHotWorkHidden.vue'

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

const sourceName = 'BZHouseDia'
const OPEN_EVENT = 'OPEN_HOUSE_DIA'
const LEGACY_OPEN_EVENT = 'OPEN_DIA'
const CLOSE_EVENT = 'CLOSE_HOUSE_DIA'

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
const houseListType = ref('网约房')
const housePersonListType = ref('网约房')
const houseColorCodeType = ref('网约房')
const houseRiskPerceptionType = ref('网约房')
const warningDetailId = ref('4952')
const warningDetailHouseType = ref('群租空间')

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
  'house-list',
  'house-person-list',
  'house-risk-perception',
  'house-inspection',
  'house-risk-warning',
  'warning-detail',
  'house-color-code',
  'house-grid-inspection-hidden',
  'house-fire-work-hidden'
]

// 打开弹窗
function openPopup(payload) {
  if (!payload || !payload.diaName) return
  if (payload.diaName === 'inspection-detail') {
    inspectionDetailData.value = payload.detailData || {}
    inspectionDetailVisible.value = true
    showMask.value = true
    return
  }
  if (allowedPopups.includes(payload.diaName)) {
    // 预警详情弹窗需要携带 detailData
    if (payload.diaName === 'warning-detail') {
      warningDetailData.value = payload.detailData || {}
      warningDetailId.value = payload.id || payload.detailData?.id || '4952'
      warningDetailHouseType.value = payload.houseType || payload.detailData?.houseType || '群租空间'
    }
    if (payload.diaName === 'house-list') {
      houseListType.value = payload.houseType || '网约房'
    }
    if (payload.diaName === 'house-person-list') {
      housePersonListType.value = payload.houseType || '网约房'
    }
    if (payload.diaName === 'house-color-code') {
      houseColorCodeType.value = payload.houseType || '网约房'
    }
    if (payload.diaName === 'house-risk-perception') {
      houseRiskPerceptionType.value = payload.houseType || '网约房'
    }
    activePopup.value = payload.diaName
    showMask.value = true
  }
}

// 关闭所有弹窗
function closeAllPopup() {
  activePopup.value = null
  showMask.value = false
  inspectionDetailVisible.value = false
  riskWarningDetailVisible.value = false
}

// 预警详情弹窗
const warningDetailData = ref({})

function openWarningDetail(detail) {
  warningDetailData.value = detail
  activePopup.value = 'warning-detail'
}

// 巡查详情弹窗（独立于列表弹窗，关闭只关闭自身）
const inspectionDetailVisible = ref(false)
const inspectionDetailData = ref({})

function openInspectionDetail(detail) {
  inspectionDetailData.value = detail
  inspectionDetailVisible.value = true
  showMask.value = true
}

function closeInspectionDetail() {
  inspectionDetailVisible.value = false
  if (!activePopup.value && !riskWarningDetailVisible.value) {
    showMask.value = false
  }
}

function openDiaAfterClose(diaName) {
  const payload = { diaName }
  eventBus.emit(CLOSE_EVENT, null)
  closeAllPopup()
  eventBus.emit(OPEN_EVENT, payload)
  openPopup(payload)
}

onMounted(() => {
  eventBus.on(OPEN_EVENT, openPopup)
  eventBus.on(LEGACY_OPEN_EVENT, openPopup)
  eventBus.on(CLOSE_EVENT, closeAllPopup)
})

onBeforeUnmount(() => {
  eventBus.off(OPEN_EVENT, openPopup)
  eventBus.off(LEGACY_OPEN_EVENT, openPopup)
  eventBus.off(CLOSE_EVENT, closeAllPopup)
})
</script>

<script lang="ts">
export default {
  name: "BZHouseDia",
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
  z-index: 1001;
  pointer-events: auto;
}
</style>
