<template>
  <div v-if="visible || isClosing" class="bz-iframe-dia" :style="containerStyle">
    <div
      v-if="isClosing"
      class="event-shield"
      @pointerdown.stop.prevent
      @pointerup.stop.prevent
      @mousedown.stop.prevent
      @mouseup.stop.prevent
      @click.stop.prevent
    ></div>
    <button
      v-if="visible"
      class="close-btn"
      type="button"
      aria-label="关闭弹窗"
      @pointerdown.stop.prevent="closePopup"
      @pointerup.stop.prevent
      @mousedown.stop.prevent
      @mouseup.stop.prevent
      @click.stop.prevent
    ></button>
    <img
      v-if="isWaterlog"
      class="waterlog-top-banner"
      src="./img/顶部.png"
      alt=""
    />
    <div v-if="isGeoDisasterDetail" class="geo-detail-header">
      <img class="geo-detail-header__bg" src="./img/xiaofangtitle.png" alt="" />
      <div class="geo-detail-header__title">{{ pointName }}</div>
    </div>
    <iframe
      v-if="iframeUrl"
      class="iframe-content"
      :src="iframeUrl"
      frameborder="0"
      referrerpolicy="no-referrer"
      allowfullscreen
    ></iframe>
    <div v-else-if="visible" class="empty-tip">未传入页面地址</div>
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'

const props = defineProps({
  chartConfig: {
    type: Object,
    required: true
  },
  publicParamList: {
    type: Object,
    required: false,
    default: () => []
  },
  bus: {
    type: Object,
    required: false,
    default: null
  }
})

const sourceName = 'BzIframeDia'
const OPEN_EVENT = 'OPEN_IFRAME_DIA'
const CLOSE_EVENT = 'CLOSE_DIA'

const visible = ref(false)
const iframeUrl = ref('')
const isWaterlog = ref(false)
const isGeoDisasterDetail = ref(false)
const pointName = ref('')
const isClosing = ref(false)
let closeTimer: ReturnType<typeof setTimeout> | null = null

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

const containerStyle = computed(() => {
  const width = Number(props.chartConfig?.attr?.w)
  const height = Number(props.chartConfig?.attr?.h)
  return {
    width: Number.isFinite(width) && width > 0 ? width + 'px' : '100%',
    height: Number.isFinite(height) && height > 0 ? height + 'px' : '100%'
  }
})

function openPopup(payload) {
  if (closeTimer) {
    clearTimeout(closeTimer)
    closeTimer = null
  }
  isClosing.value = false
  console.log('参数',payload)
  iframeUrl.value = payload?.url || payload?.src || ''
  // 判断是否为waterlog调度弹窗
  isWaterlog.value = payload?.type === 'waterlog'
  isGeoDisasterDetail.value = payload?.type === 'geo-disaster-detail' || payload?.headerBg === 'xiaofangtitle'
  pointName.value = payload?.pointName || payload?.title || ''
  visible.value = true
}

function stopCloseEvent(event?: Event) {
  event?.stopPropagation?.()
  event?.preventDefault?.()
}

function closePopup(event?: Event) {
  stopCloseEvent(event)
  if (isClosing.value) return
  isClosing.value = true
  visible.value = false
  iframeUrl.value = ''
  isWaterlog.value = false
  isGeoDisasterDetail.value = false
  pointName.value = ''
  if (closeTimer) clearTimeout(closeTimer)
  closeTimer = setTimeout(() => {
    isClosing.value = false
    closeTimer = null
  }, 180)
}

onMounted(() => {
  eventBus.on(OPEN_EVENT, openPopup)
  eventBus.on(CLOSE_EVENT, closePopup)
})

onBeforeUnmount(() => {
  eventBus.off(OPEN_EVENT, openPopup)
  eventBus.off(CLOSE_EVENT, closePopup)
  if (closeTimer) clearTimeout(closeTimer)
})
</script>

<script lang="ts">
export default {
  name: 'BzIframeDia',
  version: '1.0.0'
}
</script>

<style lang="scss" scoped>
@font-face {
  font-family: 'UISDCBiaoTiHei';
  src: url('./font/UISDC-BiaoTiHei.ttf') format('truetype');
  font-weight: 400;
  font-style: normal;
  font-display: swap;
}

.bz-iframe-dia {
  position: absolute;
  left: 0;
  top: 0;
  z-index: 1001;
  overflow: hidden;
  pointer-events: auto;
}

.iframe-content {
  display: block;
  width: 100%;
  height: 100%;
  border: none;
  background: transparent;
}

.event-shield {
  position: absolute;
  inset: 0;
  z-index: 10000;
  background: transparent;
  pointer-events: auto;
}

.close-btn {
  position: absolute;
  right: 16px;
  top: 16px;
  z-index: 10001;
  width: 32px;
  height: 32px;
  padding: 0;
  border: 0;
  border-radius: 2px;
  background:url('./img/close.png') center / 14px 14px no-repeat;
  cursor: pointer;
  pointer-events: auto;

  &::before,
  &::after {
    content: '';
    position: absolute;
    left: 8px;
    top: 15px;
    width: 14px;
    height: 2px;
    background: #d8ecff;
  }

  &::before {
    transform: rotate(45deg);
  }

  &::after {
    transform: rotate(-45deg);
  }

  &:hover {
    border-color: rgba(111, 208, 255, 0.8);
  }
}

.empty-tip {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #b8d9ff;
  font-size: 16px;
}

.waterlog-top-banner {
  position: absolute;
  left: 50%;
  top: 0;
  z-index: 2;
  width: 1920px;
  height: 94px;
  transform: translateX(-50%);
  pointer-events: none;
}

.geo-detail-header {
  position: absolute;
  left: 50%;
  top: 0;
  z-index: 2;
  width: 1920px;
  height: 94px;
  transform: translateX(-50%);
  pointer-events: none;
}

.geo-detail-header__bg {
  display: block;
  width: 100%;
  height: 94px;
  object-fit: cover;
}

.geo-detail-header__title {
  position: absolute;
  left: 50%;
  top: 16px;
  transform: translateX(-50%);
  max-width: 960px;
  color: #e6f7ff;
  font-family: 'UISDCBiaoTiHei', Microsoft YaHei, sans-serif;
  font-size: 48px;
  font-weight: 400;
  line-height: 1;
  text-align: center;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  text-shadow: 0 0 12px rgba(82, 200, 255, 0.75);
}
</style>
