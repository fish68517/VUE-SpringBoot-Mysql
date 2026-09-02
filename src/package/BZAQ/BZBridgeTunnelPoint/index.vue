<template>
  <div class="bridge-tunnel-point" :style="containerStyle">
    <button
      v-for="item in layerButtons"
      :key="item.key"
      class="layer-btn"
      :class="{ 'is-active': activeLayerKeys.includes(item.key) }"
      type="button"
      @click="toggleLayer(item)"
    >
      {{ item.label }}
    </button>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'
import axios from 'axios'
import bridgeIcon from './img/bridge.png'
import bridgeWarningIcon from './img/bridge-warning.png'
import tunnelIcon from './img/tunnel.png'
import tunnelWarningIcon from './img/tunnel-warning.png'

type LayerButton = {
  key: string
  label: string
  type: number
  icon: string
  warningIcon: string
}

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

const BASE_URL = 'http://23.99.16.179:11001/api/boot/system/bridge'
const sourceName = 'BZBridgeTunnelPoint'
const BRIDGE_TUNNEL_LAYER_TOGGLE_EVENT = 'BZ_BRIDGE_TUNNEL_LAYER_TOGGLE'
const BRIDGE_TUNNEL_KPI_OPEN_EVENT = 'OPEN_BRIDGE_TUNNEL_KPI_DIA'
const layerButtons: LayerButton[] = [
  { key: 'bridge', label: '桥梁', type: 1, icon: bridgeIcon, warningIcon: bridgeWarningIcon },
  { key: 'tunnel', label: '隧道', type: 4, icon: tunnelIcon, warningIcon: tunnelWarningIcon }
]

const activeLayerKeys = ref<string[]>([])
const pointLayer: Record<string, Cesium.Entity> = {}
let clickHandler: Cesium.ScreenSpaceEventHandler | null = null
let layerToggleListener: ((payload: any) => void) | null = null

const containerStyle = computed(() => {
  const width = Number(props.chartConfig?.attr?.w)
  const height = Number(props.chartConfig?.attr?.h)
  return {
    width: Number.isFinite(width) && width > 0 ? `${width}px` : '230px',
    height: Number.isFinite(height) && height > 0 ? `${height}px` : '42px'
  }
})

const eventBus = {
  emit(event: string, data: any) {
    props.bus?.emit(event, { data, source: sourceName })
  }
}

const isTripleScreen = computed(() => props.chartConfig?.option?.screenMode === 'triple')

function getPropertyValue(value: any) {
  if (value && typeof value.getValue === 'function') {
    return value.getValue(typeof Cesium !== 'undefined' ? Cesium.JulianDate.now() : undefined)
  }
  return value
}

function getEntityProperties(entity: any) {
  const properties = entity?.properties
  if (!properties) return {}
  return getPropertyValue(properties) || properties
}

function clearLayer(layerKey?: string) {
  Object.keys(pointLayer).forEach(key => {
    if (layerKey && !key.startsWith(`${layerKey}_`)) return
    const entity = pointLayer[key]
    if (entity) window.viewer?.entities?.remove(entity)
    delete pointLayer[key]
  })
}

function getResponseList(data: any) {
  const payload = data?.data ?? data
  if (Array.isArray(payload)) return payload
  if (Array.isArray(payload?.content)) return payload.content
  if (Array.isArray(payload?.records)) return payload.records
  if (Array.isArray(payload?.list)) return payload.list
  if (Array.isArray(payload?.rows)) return payload.rows
  return []
}

function isWarningPoint(point: any) {
  const value = point?.isWarning
  if (typeof value === 'boolean') return value
  return ['1', 'true', '是', 'yes'].includes(String(value ?? '').trim().toLowerCase())
}

function openPointDetail(data: any) {
  eventBus.emit(isTripleScreen.value ? BRIDGE_TUNNEL_KPI_OPEN_EVENT : 'OPEN_DIA', {
    diaName: 'bridge-info-detail',
    structId: data.structId,
    prjId: data.prjId,
    structName: data.structName,
    structType: data.structType === '隧道' ? 4 : data.structType === '桥梁' ? 1 : data.__structType,
    data
  })
}

function ensureMapClickHandler() {
  if (clickHandler || !window.viewer || typeof Cesium === 'undefined') return

  clickHandler = new Cesium.ScreenSpaceEventHandler(window.viewer.scene.canvas)
  clickHandler.setInputAction((movement: any) => {
    const picked = window.viewer?.scene?.pick(movement.position)
    const properties = getEntityProperties(picked?.id)
    const pointType = getPropertyValue(properties.type)
    if (pointType !== 'bridgeTunnelPoint') return

    const data = getPropertyValue(properties.data) || {}
    openPointDetail(data)
  }, Cesium.ScreenSpaceEventType.LEFT_CLICK)

  clickHandler.setInputAction((movement: any) => {
    const picked = window.viewer?.scene?.pick(movement.endPosition)
    const properties = getEntityProperties(picked?.id)
    const pointType = getPropertyValue(properties.type)
    window.viewer.canvas.style.cursor = pointType === 'bridgeTunnelPoint' ? 'pointer' : 'default'
  }, Cesium.ScreenSpaceEventType.MOUSE_MOVE)
}

async function renderLayer(item: LayerButton) {
  clearLayer(item.key)
  if (!window.viewer || typeof Cesium === 'undefined') return
  ensureMapClickHandler()

  try {
    const res = await axios.get(`${BASE_URL}/struct/loc`, {
      params: { type: item.type }
    })
    const list = getResponseList(res?.data)
    list.forEach((point: any, index: number) => {
      const lng = Number(point.lng ?? point.longitude)
      const lat = Number(point.lat ?? point.latitude)
      if (!Number.isFinite(lng) || !Number.isFinite(lat)) return

      const key = `${item.key}_${point.structId || index}`
      const icon = isWarningPoint(point) ? item.warningIcon : item.icon
      pointLayer[key] = window.viewer.entities.add({
        position: Cesium.Cartesian3.fromDegrees(lng, lat, 360),
        billboard: {
          image: icon,
          width: 28,
          height: 34,
          verticalOrigin: Cesium.VerticalOrigin.BOTTOM,
          disableDepthTestDistance: Number.POSITIVE_INFINITY
        },
        properties: new Cesium.PropertyBag({
          type: 'bridgeTunnelPoint',
          layerKey: item.key,
          data: {
            ...point,
            __structType: item.type
          }
        })
      })
    })
  } catch (error) {
    console.error(`获取${item.label}地图落点失败:`, error)
  }
}

async function toggleLayer(item: LayerButton) {
  if (activeLayerKeys.value.includes(item.key)) {
    activeLayerKeys.value = activeLayerKeys.value.filter(key => key !== item.key)
    clearLayer(item.key)
    return
  }

  activeLayerKeys.value = [...activeLayerKeys.value, item.key]
  await renderLayer(item)
}

function getLayerByPayload(payload: any) {
  const key = String(payload?.key || '').trim()
  const structType = Number(payload?.structType ?? payload?.type)
  return layerButtons.find(item => item.key === key || item.type === structType)
}

async function handleLayerToggle(payload: any) {
  const item = getLayerByPayload(payload)
  if (!item) return
  await toggleLayer(item)
}

onMounted(() => {
  layerToggleListener = ({ source, data }: any) => {
    if (source === sourceName) return
    handleLayerToggle(data)
  }
  props.bus?.on(BRIDGE_TUNNEL_LAYER_TOGGLE_EVENT, layerToggleListener)
})

onUnmounted(() => {
  if (layerToggleListener) {
    props.bus?.off(BRIDGE_TUNNEL_LAYER_TOGGLE_EVENT, layerToggleListener)
    layerToggleListener = null
  }
  clearLayer()
  if (clickHandler) {
    clickHandler.destroy()
    clickHandler = null
  }
})
</script>

<script lang="ts">
export default {
  name: 'BZBridgeTunnelPoint',
  version: '1.0.0'
}
</script>

<style scoped lang="scss">
.bridge-tunnel-point {
  display: flex;
  align-items: center;
  gap: 16px;
  pointer-events: auto;
}

.layer-btn {
  width: 214px;
  height: 46px;
  box-sizing: border-box;
  border: 1px solid rgba(59, 179, 255, 0.78);
  padding: 0;
  color: #f2fbff;
  font-family: 'UISDCBiaoTiHei', Microsoft YaHei, sans-serif;
  font-size: 22px;
  font-weight: 700;
  line-height: 46px;
  letter-spacing: 2px;
  background:
    linear-gradient(180deg, rgba(12, 111, 181, 0.72) 0%, rgba(3, 67, 126, 0.88) 100%),
    radial-gradient(circle at 20% 0%, rgba(72, 211, 255, 0.35), transparent 50%);
  box-shadow: inset 0 0 16px rgba(56, 178, 255, 0.24), 0 0 8px rgba(35, 153, 255, 0.18);
  cursor: pointer;
  outline: none;
  transition: border-color 0.18s ease, box-shadow 0.18s ease;
}

.layer-btn:hover {
  border-color: rgba(135, 230, 255, 0.95);
  box-shadow: inset 0 0 16px rgba(56, 178, 255, 0.36), 0 0 14px rgba(35, 153, 255, 0.35);
}

.layer-btn:active {
  background:
    linear-gradient(180deg, rgba(72, 211, 255, 0.85) 0%, rgba(12, 111, 181, 0.92) 100%),
    radial-gradient(circle at 20% 0%, rgba(120, 240, 255, 0.5), transparent 50%);
  box-shadow: inset 0 0 20px rgba(56, 178, 255, 0.5), 0 0 18px rgba(35, 153, 255, 0.45);
}

.layer-btn.is-active {
  color: #fff;
  border-color: rgba(255, 228, 76, 0.95);
  background:
    linear-gradient(180deg, rgba(255, 228, 76, 0.92) 0%, rgba(255, 180, 20, 0.88) 100%),
    radial-gradient(circle at 20% 0%, rgba(255, 245, 150, 0.6), transparent 50%);
  box-shadow: inset 0 0 16px rgba(255, 200, 40, 0.4), 0 0 14px rgba(255, 180, 20, 0.4);
}
</style>
