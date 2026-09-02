<template>
  <div
    class="waterlog-map"
    @mousedown="handleMouseDown"
    @wheel.prevent="handleWheel"
  >
    <div class="tile-viewport">
      <div class="tile-stage" :style="stageStyle">
        <template v-for="tile in tiles" :key="tile.key">
          <img
            class="map-tile base-tile"
            :src="tile.baseSrc"
            :style="tile.style"
            alt=""
            draggable="false"
          />
          <img
            class="map-tile label-tile"
            :src="tile.labelSrc"
            :style="tile.style"
            alt=""
            draggable="false"
          />
        </template>
      </div>
    </div>
    <div class="map-vignette"></div>
  </div>
</template>

<script setup>
import { computed, reactive, onBeforeUnmount } from 'vue'

const props = defineProps({
  initialScale: {
    type: Number,
    default: 0.185
  },
  initialX: {
    type: Number,
    default: null
  },
  initialY: {
    type: Number,
    default: null
  },
  maxScale: {
    type: Number,
    default: 0.65
  },
  minScale: {
    type: Number,
    default: 0.12
  }
})

const tileConfig = {
  z: 17,
  minX: 104304,
  maxX: 104344,
  minY: 54256,
  maxY: 54275,
  tileSize: 256,
  basePath: '/map/output-yuzhong-z17-bbox/chongqing-yuzhong-z17-bbox-z17-17.lcNAnifvR',
  labelPath: '/map/output-yuzhong-z17-label-bbox/chongqing-yuzhong-z17-label-bbox-z17-17.N2Fp4ifDg'
}

const cols = tileConfig.maxX - tileConfig.minX + 1
const rows = tileConfig.maxY - tileConfig.minY + 1
const stageWidth = cols * tileConfig.tileSize
const stageHeight = rows * tileConfig.tileSize
const jiefangbeiOffset = {
  x: -3840.39,
  y: 1332.24
}

const viewState = reactive({
  scale: props.initialScale,
  x: props.initialX ?? Number((jiefangbeiOffset.x * props.initialScale).toFixed(2)),
  y: props.initialY ?? Number((jiefangbeiOffset.y * props.initialScale).toFixed(2)),
  dragging: false,
  startX: 0,
  startY: 0,
  originX: 0,
  originY: 0
})

const stageStyle = computed(() => ({
  width: `${stageWidth}px`,
  height: `${stageHeight}px`,
  transform: `translate(calc(-50% + ${viewState.x}px), calc(-50% + ${viewState.y}px)) scale(${viewState.scale})`
}))

function handleMouseDown(event) {
  if (event.button !== 0) return
  viewState.dragging = true
  viewState.startX = event.clientX
  viewState.startY = event.clientY
  viewState.originX = viewState.x
  viewState.originY = viewState.y
  window.addEventListener('mousemove', handleMouseMove)
  window.addEventListener('mouseup', handleMouseUp)
}

function handleMouseMove(event) {
  if (!viewState.dragging) return
  viewState.x = viewState.originX + event.clientX - viewState.startX
  viewState.y = viewState.originY + event.clientY - viewState.startY
}

function handleMouseUp() {
  viewState.dragging = false
  window.removeEventListener('mousemove', handleMouseMove)
  window.removeEventListener('mouseup', handleMouseUp)
}

function handleWheel(event) {
  const delta = event.deltaY > 0 ? -0.02 : 0.02
  const nextScale = Math.min(props.maxScale, Math.max(props.minScale, viewState.scale + delta))
  viewState.scale = Number(nextScale.toFixed(3))
}

const tiles = computed(() => {
  const list = []
  for (let x = tileConfig.minX; x <= tileConfig.maxX; x += 1) {
    for (let y = tileConfig.minY; y <= tileConfig.maxY; y += 1) {
      list.push({
        key: `${x}-${y}`,
        baseSrc: `${tileConfig.basePath}/${tileConfig.z}/${x}/${y}.jpg`,
        labelSrc: `${tileConfig.labelPath}/${tileConfig.z}/${x}/${y}.png`,
        style: {
          left: `${(x - tileConfig.minX) * tileConfig.tileSize}px`,
          top: `${(y - tileConfig.minY) * tileConfig.tileSize}px`
        }
      })
    }
  }
  return list
})

onBeforeUnmount(() => {
  window.removeEventListener('mousemove', handleMouseMove)
  window.removeEventListener('mouseup', handleMouseUp)
})
</script>

<script>
export default {
  name: 'WaterlogMapPlaceholder'
}
</script>

<style lang="scss" scoped>
.waterlog-map {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
  background: #071426;
  cursor: grab;

  &:active {
    cursor: grabbing;
  }
}

.tile-viewport {
  position: absolute;
  inset: 0;
  overflow: hidden;
}

.tile-stage {
  position: absolute;
  left: 50%;
  top: 52%;
  transform-origin: center center;
}

.map-tile {
  position: absolute;
  width: 256px;
  height: 256px;
  user-select: none;
  pointer-events: none;
}

.base-tile {
  z-index: 1;
}

.label-tile {
  z-index: 2;
}

.map-vignette {
  position: absolute;
  inset: 0;
  z-index: 3;
  pointer-events: none;
  background: radial-gradient(53.51% 54.05% at 50.03% 50%, rgba(3, 14, 37, 0) 0%, rgba(3, 14, 37, 0.78) 100%);
}
</style>
