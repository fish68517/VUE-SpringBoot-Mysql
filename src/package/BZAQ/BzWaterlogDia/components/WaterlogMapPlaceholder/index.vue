<template>
  <div class="waterlog-map-wrapper">
    <div ref="mapContainer" class="waterlog-map"></div>
    <!-- loading -->
    <div v-if="loading" class="map-loading">
      <div class="loading-spinner"></div>
      <span class="loading-text">地图加载中...</span>
    </div>
    <!-- 渐变遮罩 -->
    <div class="map-vignette"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch, nextTick } from 'vue'
import L from 'leaflet'

const props = defineProps({
  markers: {
    type: Array,
    default: () => []
    // [{ lng: 106.xxx, lat: 29.xxx, name: 'xxx' }]
  }
})

const emit = defineEmits(['loaded'])

const mapContainer = ref(null)
const loading = ref(true)
let map = null
let markerLayer = null
let baseLayer = null
let labelLayer = null

// 瓦片配置
const tileConfig = {
  z: 17,
  minX: 104304,
  maxX: 104344,
  minY: 54256,
  maxY: 54275,
  tileSize: 256
}

// 计算瓦片范围对应的经纬度边界
function tileToLngLat(x, y, z) {
  const n = Math.pow(2, z)
  const lng = x / n * 360 - 180
  const latRad = Math.atan(Math.sinh(Math.PI * (1 - 2 * y / n)))
  const lat = latRad * 180 / Math.PI
  return { lng, lat }
}

const bounds = (() => {
  const sw = tileToLngLat(tileConfig.minX, tileConfig.maxY + 1, tileConfig.z)
  const ne = tileToLngLat(tileConfig.maxX + 1, tileConfig.minY, tileConfig.z)
  return L.latLngBounds([sw.lat, sw.lng], [ne.lat, ne.lng])
})()

// 渝中区中心点（解放碑附近）
const defaultCenter = [29.558, 106.577]

// 预加载所有瓦片（require.context 在编译时解析）
const baseTileContext = require.context(
  './img/output-yuzhong-z17-bbox/chongqing-yuzhong-z17-bbox-z17-17.lcNAnifvR/17',
  true,
  /\.jpg$/
)
const labelTileContext = require.context(
  './img/output-yuzhong-z17-label-bbox/chongqing-yuzhong-z17-label-bbox-z17-17.N2Fp4ifDg/17',
  true,
  /\.png$/
)

// 构建 { "104304/54256": url } 映射
const baseTileMap = {}
baseTileContext.keys().forEach(key => {
  // key like "./104304/54256.jpg"
  const match = key.match(/\.\/(\d+)\/(\d+)\.jpg/)
  if (match) {
    baseTileMap[`${match[1]}/${match[2]}`] = baseTileContext(key)
  }
})

const labelTileMap = {}
labelTileContext.keys().forEach(key => {
  const match = key.match(/\.\/(\d+)\/(\d+)\.png/)
  if (match) {
    labelTileMap[`${match[1]}/${match[2]}`] = labelTileContext(key)
  }
})

// 初始化地图
onMounted(() => {
  nextTick(() => {
    if (!mapContainer.value) return

    map = L.map(mapContainer.value, {
      center: defaultCenter,
      zoom: 17,
      minZoom: 17,
      maxZoom: 17,
      zoomControl: false,
      attributionControl: false,
      maxBounds: bounds.pad(1)
    })

    // 底图瓦片
    baseLayer = L.tileLayer('', {
      tileSize: tileConfig.tileSize,
      minZoom: 17,
      maxZoom: 17,
      bounds: bounds,
      noWrap: true
    })
    baseLayer._url = ''
    // 重写 getTileUrl 使用预加载的瓦片映射
    baseLayer.getTileUrl = function (coords) {
      const x = coords.x
      const y = coords.y
      const key = `${x}/${y}`
      return baseTileMap[key] || L.Util.emptyImageUrl
    }
    baseLayer.addTo(map)

    // 标注瓦片
    labelLayer = L.tileLayer('', {
      tileSize: tileConfig.tileSize,
      minZoom: 17,
      maxZoom: 17,
      bounds: bounds,
      noWrap: true,
      opacity: 0.8
    })
    labelLayer._url = ''
    labelLayer.getTileUrl = function (coords) {
      const x = coords.x
      const y = coords.y
      const key = `${x}/${y}`
      return labelTileMap[key] || L.Util.emptyImageUrl
    }
    labelLayer.addTo(map)

    // 标记图层
    markerLayer = L.layerGroup().addTo(map)

    // 监听瓦片加载完成
    let hasLoaded = false
    baseLayer.on('tileload', () => {
      if (!hasLoaded) {
        hasLoaded = true
        // 首张瓦片加载后延迟关闭loading
        setTimeout(() => {
          loading.value = false
          emit('loaded')
        }, 500)
      }
    })

    baseLayer.on('load', () => {
      loading.value = false
      emit('loaded')
    })

    // 保险：3秒后强制关闭loading
    setTimeout(() => {
      if (loading.value) {
        loading.value = false
        emit('loaded')
      }
    }, 3000)

    // 渲染标记点
    updateMarkers()
  })
})

// 更新标记点
function updateMarkers() {
  if (!map || !markerLayer) return
  markerLayer.clearLayers()

  if (props.markers && props.markers.length > 0) {
    props.markers.forEach(marker => {
      const icon = L.divIcon({
        className: 'custom-marker-icon',
        html: `<div class="marker-container">
          <div class="marker-dot"></div>
          <div class="marker-pulse"></div>
        </div>`,
        iconSize: [20, 20],
        iconAnchor: [10, 10]
      })
      const m = L.marker([marker.lat, marker.lng], { icon })
      if (marker.name) {
        m.bindTooltip(marker.name, {
          direction: 'top',
          offset: [0, -10],
          className: 'marker-tooltip'
        })
      }
      markerLayer.addLayer(m)
    })

    // 自动定位到第一个标记点
    const first = props.markers[0]
    map.setView([first.lat, first.lng], 17, { animate: true })
  }
}

// 监听 markers 变化
watch(() => props.markers, () => {
  updateMarkers()
}, { deep: true })

onBeforeUnmount(() => {
  if (map) {
    map.remove()
    map = null
  }
})
</script>

<script>
export default {
  name: 'WaterlogMapPlaceholder'
}
</script>

<style lang="scss" scoped>
/* Leaflet 核心样式（内联，避免 sass-loader 解析 leaflet.css 中的 url 报错） */
.waterlog-map :deep(.leaflet-container) {
  font-family: inherit;
  font-size: 12px;
  line-height: 1.5;
  background: #071426;
  -webkit-tap-highlight-color: transparent;
}

.waterlog-map :deep(.leaflet-tile-pane),
.waterlog-map :deep(.leaflet-overlay-pane),
.waterlog-map :deep(.leaflet-shadow-pane),
.waterlog-map :deep(.leaflet-marker-pane),
.waterlog-map :deep(.leaflet-tooltip-pane),
.waterlog-map :deep(.leaflet-popup-pane) {
  position: absolute;
  left: 0;
  top: 0;
}

.waterlog-map :deep(.leaflet-tile-pane) {
  z-index: 200;
}

.waterlog-map :deep(.leaflet-overlay-pane) {
  z-index: 400;
}

.waterlog-map :deep(.leaflet-shadow-pane) {
  z-index: 500;
}

.waterlog-map :deep(.leaflet-marker-pane) {
  z-index: 600;
}

.waterlog-map :deep(.leaflet-tooltip-pane) {
  z-index: 650;
}

.waterlog-map :deep(.leaflet-popup-pane) {
  z-index: 700;
}

.waterlog-map :deep(.leaflet-tile) {
  position: absolute;
  left: 0;
  top: 0;
  -webkit-user-select: none;
  -moz-user-select: none;
  user-select: none;
  -webkit-user-drag: none;
}

.waterlog-map :deep(.leaflet-tile-loaded) {
  visibility: inherit;
}

.waterlog-map :deep(.leaflet-tile-container) {
  position: absolute;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
}

.waterlog-map :deep(.leaflet-grab) {
  cursor: grab;
}

.waterlog-map :deep(.leaflet-grab:active) {
  cursor: grabbing;
}

.waterlog-map :deep(.leaflet-marker-icon) {
  position: absolute;
  left: 0;
  top: 0;
}

.waterlog-map :deep(.leaflet-tooltip) {
  position: absolute;
  padding: 6px;
  background-color: #fff;
  border: 1px solid #fff;
  border-radius: 3px;
  color: #222;
  white-space: nowrap;
  -webkit-user-select: none;
  -moz-user-select: none;
  user-select: none;
  pointer-events: none;
  box-shadow: 0 1px 3px rgba(0,0,0,0.4);
}

.waterlog-map :deep(.leaflet-tooltip-top:before) {
  position: absolute;
  pointer-events: none;
  border: 6px solid transparent;
  background: transparent;
  content: "";
  bottom: 0;
  left: 50%;
  margin-left: -6px;
  border-top-color: #fff;
}

.waterlog-map :deep(.leaflet-tooltip-bottom:before) {
  position: absolute;
  pointer-events: none;
  border: 6px solid transparent;
  background: transparent;
  content: "";
  top: 0;
  left: 50%;
  margin-left: -6px;
  border-bottom-color: #fff;
}

.waterlog-map :deep(.leaflet-tooltip-left:before) {
  position: absolute;
  pointer-events: none;
  border: 6px solid transparent;
  background: transparent;
  content: "";
  top: 50%;
  right: 0;
  margin-top: -6px;
  border-left-color: #fff;
}

.waterlog-map :deep(.leaflet-tooltip-right:before) {
  position: absolute;
  pointer-events: none;
  border: 6px solid transparent;
  background: transparent;
  content: "";
  top: 50%;
  left: 0;
  margin-top: -6px;
  border-right-color: #fff;
}

/* 组件样式 */
.waterlog-map-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
  border-radius: 3px;
  background: #071426;
}

.waterlog-map {
  width: 100%;
  height: 100%;
}

.map-vignette {
  position: absolute;
  inset: 0;
  z-index: 500;
  pointer-events: none;
  background: radial-gradient(53.51% 54.05% at 50.03% 50%, rgba(3, 14, 37, 0) 0%, rgba(3, 14, 37, 0.78) 100%);
}

.map-loading {
  position: absolute;
  inset: 0;
  z-index: 600;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: rgba(7, 20, 38, 0.85);
}

.loading-spinner {
  width: 36px;
  height: 36px;
  border: 3px solid rgba(62, 160, 255, 0.3);
  border-top-color: #3ea0ff;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.loading-text {
  margin-top: 12px;
  color: #749dc0;
  font-size: 14px;
}
</style>

<style>
/* 标记点样式（非scoped，因为是divIcon动态创建的DOM） */
.custom-marker-icon {
  background: none !important;
  border: none !important;
}

.marker-container {
  position: relative;
  width: 20px;
  height: 20px;
}

.marker-dot {
  position: absolute;
  left: 50%;
  top: 50%;
  width: 14px;
  height: 14px;
  transform: translate(-50%, -50%);
  background: #ff4444;
  border: 2px solid #ffffff;
  border-radius: 50%;
  box-shadow: 0 0 6px rgba(255, 68, 68, 0.8);
  z-index: 2;
}

.marker-pulse {
  position: absolute;
  left: 50%;
  top: 50%;
  width: 30px;
  height: 30px;
  transform: translate(-50%, -50%);
  border-radius: 50%;
  background: rgba(255, 68, 68, 0.3);
  animation: marker-pulse 2s infinite;
  z-index: 1;
}

@keyframes marker-pulse {
  0% {
    transform: translate(-50%, -50%) scale(0.5);
    opacity: 1;
  }
  100% {
    transform: translate(-50%, -50%) scale(2);
    opacity: 0;
  }
}

.marker-tooltip {
  background: rgba(24, 88, 163, 0.9) !important;
  border: 1px solid rgba(62, 160, 255, 0.82) !important;
  color: #ffffff !important;
  font-size: 12px !important;
  padding: 4px 8px !important;
  border-radius: 3px !important;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3) !important;
}

.marker-tooltip::before {
  border-top-color: rgba(24, 88, 163, 0.9) !important;
}
</style>
