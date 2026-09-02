<template>
  <div class="bz-bri-tun-dia-container" :style="containerStyle">
    <!-- 遮罩层（点击不关闭） -->
    <div v-if="showMask && activePopup" class="modal-overlay"></div>

    <!-- 设施监测详情弹窗 -->
    <FacilityMonitorDetail
      v-if="activePopup === 'facility-monitor-detail'"
      :style="popupStyle"
      :payload="popupPayload"
      @close="closeAllPopup"
    />

    <!-- 桥梁基础信息弹窗 -->
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

    <!-- 报告列表弹窗 -->
    <ReportList
      v-if="activePopup === 'report-list'"
      :style="popupStyle"
      :payload="popupPayload"
      @close="closeAllPopup"
    />

    <!-- 桥隧列表弹窗 -->
    <BridgeTunnelList
      v-if="activePopup === 'bridge-tunnel-list'"
      :style="popupStyle"
      :payload="popupPayload"
      @open-detail="openStackedPopup"
      @open-model-iframe="openModelIframe"
      @open-facility-detail="openStackedPopup"
      @close="closeAllPopup"
    />

    <!-- 桥隧列表上方叠加详情弹窗，关闭详情后保留列表 -->
    <FacilityMonitorDetail
      v-if="activePopup === 'bridge-tunnel-list' && stackedPopup === 'facility-monitor-detail'"
      :style="popupStyle"
      :payload="stackedPayload"
      @close="closeStackedPopup"
    />

    <!-- PDF预览弹窗 -->
    <PdfPreview
      v-if="activePopup === 'pdf-preview'"
      :style="popupStyle"
      :payload="popupPayload"
      @close="closeAllPopup"
    />

    <!-- 3D图片预览弹窗 -->
    <ThreeImagePreview
      v-if="activePopup === 'three-image-preview'"
      :style="popupStyle"
      :payload="popupPayload"
      @close="closeAllPopup"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import FacilityMonitorDetail from './components/FacilityMonitorDetail.vue'
import BridgeInfoDetail from './components/BridgeInfoDetail.vue'
import ReportList from './components/ReportList.vue'
import BridgeTunnelList from './components/BridgeTunnelList.vue'
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

const sourceName = 'BzBriTunDia'

// 事件总线封装（暴露给父组件使用）
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

// 弹窗控制
const showMask = ref(false)
const activePopup = ref<string | null>("")
const popupPayload = ref<Record<string, any>>({})
const stackedPopup = ref<string | null>(null)
const stackedPayload = ref<Record<string, any>>({})

// 容器宽高跟随低代码平台拖动的 attr.w / attr.h
const containerStyle = computed(() => {
  const width = Number(props.chartConfig?.attr?.w)
  const height = Number(props.chartConfig?.attr?.h)
  return {
    width: Number.isFinite(width) && width > 0 ? width + 'px' : '100%',
    height: Number.isFinite(height) && height > 0 ? height + 'px' : '100%'
  }
})

// 弹窗居中定位
const popupStyle = computed(() => ({
  left: '50%',
  top: '50%'
}))

const allowedPopups: string[] = [
  'facility-monitor-detail',
  'bridge-info-detail',
  'report-list',
  'bridge-tunnel-list',
  'pdf-preview',
  'pdf-preview-dialog',
  'three-image-preview',
  'three-d-image-preview',
  '3d-image-preview'
]

function openPopup(payload) {
  if (!payload || !payload.diaName) return
  const diaNameMap: Record<string, string> = {
    'pdf-preview-dialog': 'pdf-preview',
    'three-d-image-preview': 'three-image-preview',
    '3d-image-preview': 'three-image-preview'
  }
  const diaName = diaNameMap[payload.diaName] || payload.diaName
  if (!allowedPopups.includes(payload.diaName) && !allowedPopups.includes(diaName)) return
  closeStackedPopup()
  popupPayload.value = { ...payload, diaName }
  activePopup.value = diaName
  showMask.value = true
}

function openStackedPopup(payload) {
  if (!payload || !payload.diaName) return
  const diaNameMap: Record<string, string> = {
    'pdf-preview-dialog': 'pdf-preview',
    'three-d-image-preview': 'three-image-preview',
    '3d-image-preview': 'three-image-preview'
  }
  const diaName = diaNameMap[payload.diaName] || payload.diaName
  if (!allowedPopups.includes(payload.diaName) && !allowedPopups.includes(diaName)) return
  stackedPayload.value = { ...payload, diaName }
  stackedPopup.value = diaName
  showMask.value = true
}

function closeStackedPopup() {
  stackedPopup.value = null
  stackedPayload.value = {}
}

function openPreviewPopup(diaName: string) {
  openPopup({ diaName })
}

function openModelIframe(payload: Record<string, any>) {
  eventBus.emit('OPEN_IFRAME_DIA', payload)
}

function closeAllPopup() {
  activePopup.value = null
  popupPayload.value = {}
  closeStackedPopup()
  showMask.value = false
}

const OPEN_EVENT = 'OPEN_DIA'
const CLOSE_EVENT = 'CLOSE_DIA'

onMounted(() => {
  eventBus.on(OPEN_EVENT, openPopup)
  eventBus.on(CLOSE_EVENT, closeAllPopup)
})

onBeforeUnmount(() => {
  eventBus.off(OPEN_EVENT, openPopup)
  eventBus.off(CLOSE_EVENT, closeAllPopup)
})

// 暴露eventBus给父组件
defineExpose({
  openPopup
})
</script>

<script lang="ts">
export default {
  name: "BzBriTunDia",
  version: "1.0.0"
}
</script>

<style lang="scss" scoped>
.bz-bri-tun-dia-container {
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

.bz-bri-tun-dia-container > :not(.modal-overlay) {
  z-index: 1001;
  pointer-events: auto;
}
</style>
