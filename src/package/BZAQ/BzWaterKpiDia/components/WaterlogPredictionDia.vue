<template>
  <div v-if="visible || isClosing" class="waterlog-prediction-dia">
    <div
      v-if="isClosing"
      class="event-shield"
      @pointerdown.stop.prevent
      @pointerup.stop.prevent
      @mousedown.stop.prevent
      @mouseup.stop.prevent
      @click.stop.prevent
    ></div>
    <div class="modal-header">
      <div class="header-titles">
        <div class="header-title">防汛预警系统</div>
        <div class="header-subtitle">嘉陵江 · 长江在朝天门交汇 — 洪水预警与到达时间计算</div>
      </div>
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
      >×</button>
    </div>

    <!-- 防汛预警内容区 -->
    <div v-if="visible" class="flood-main">
      <!-- 地图区 -->
      <div class="map-wrap">
        <div ref="mapRef" class="map"></div>
        <div class="water-grid" aria-hidden="true"></div>
        <canvas ref="flowCanvasRef" class="flow-canvas" aria-hidden="true"></canvas>
        <div class="flow-hud" aria-hidden="true">
          <div class="flow-hud__eyebrow">
            <span class="flow-hud__pulse"></span>
            水势动态推演
          </div>
          <div class="flow-hud__title">两江汇流态势</div>
          <div class="flow-hud__routes">
            <span><i class="jialing"></i>嘉陵江 → 朝天门</span>
            <span><i class="yangtze"></i>长江 → 朝天门</span>
          </div>
        </div>
        <div class="flow-status" aria-hidden="true">
          <span class="flow-status__wave"><i></i><i></i><i></i><i></i></span>
          <span>FLOWING</span>
          <b>双河道粒子流</b>
        </div>
        <button
          class="particle-toggle"
          type="button"
          :class="{ 'particle-toggle--active': particleEnabled }"
          :aria-pressed="particleEnabled"
          @click.stop="toggleParticleAnimation"
        >
          <span class="particle-toggle__switch" aria-hidden="true"><i></i></span>
          粒子动画
        </button>
        <div class="map-attribution">
          行政区：China-GeoData · 河网：
          <a href="https://www.openstreetmap.org/copyright" target="_blank" rel="noopener noreferrer">
            © OpenStreetMap contributors · ODbL
          </a>
        </div>
        <div class="legend">
          <div class="lg"><span class="sw" style="background:#2196F3;height:4px;"></span> 嘉陵江</div>
          <div class="lg"><span class="sw" style="background:#FF9800;height:4px;"></span> 长江</div>
          <div class="lg"><span class="sw" style="width:14px;height:14px;border-radius:50%;background:#E53935;"></span> 朝天门(预警站)</div>
          <div class="lg"><span class="sw" style="width:10px;height:10px;border-radius:50%;background:#2196F3;"></span> 嘉陵江站点</div>
          <div class="lg"><span class="sw" style="width:10px;height:10px;border-radius:50%;background:#FF9800;"></span> 长江站点</div>
          <div class="lg"><span class="sw" style="width:10px;height:10px;border-radius:50%;background:#9E9E9E;"></span> 无阈值站点</div>
        </div>
      </div>

      <!-- 侧边栏 -->
      <div class="sidebar">
        <div class="sidebar-scroll">
        <div class="section-title">
          <span>站点当前水位（米）</span>
        </div>
        <div class="river-tabs" role="tablist" aria-label="河流站点" :data-source="stationDataSource">
          <button
            class="river-tab river-tab--jialing is-active"
            type="button"
            role="tab"
            aria-selected="true"
          >嘉陵江</button>
          <span
            class="river-tab river-tab--yangtze"
            role="tab"
            aria-disabled="true"
            title="长江站点已合并显示在下方列表"
          >长江</span>
        </div>
        <div class="station-list">
          <div v-if="stationLoading" class="station-loading">站点数据加载中...</div>
          <template v-else>
            <div
              v-for="s in stationList"
              :key="s.name"
              class="station-row"
              :class="{ 'no-threshold': !hasThresholds(s) }"
            >
              <span class="dot" :style="{ background: markerColor(s) }"></span>
              <span class="name">{{ s.name }}</span>
              <span class="info">
                警戒 {{ fmtThr(s.warning) }} / 保证 {{ fmtThr(s.guarantee) }}<br>{{ s.dist }} km
              </span>
              <input
                v-model.number="levels[s.name]"
                :disabled="!isTrigger(s) || isInputDisabled(s.name)"
                type="number"
                step="0.1"
                @input="onStationInput(s.name)"
              />
            </div>
          </template>
        </div>

        <!-- 草街水库放水量 -->
        <div class="caojie-section">
          <h2>草街水库放水量（立方米）</h2>
          <div class="caojie-row">
            <span class="name">放水量</span>
            <input
              v-model.number="caojieDischarge"
              type="number"
              step="100"
              placeholder="m³"
            />
          </div>
        </div>

        <!-- 降雨量 -->
        <div class="rainfall-section">
          <h2>降雨量（mm）</h2>
          <div class="rainfall-row">
            <span class="name">降雨量</span>
            <input
              v-model.number="rainfall"
              type="number"
              step="0.1"
              placeholder="mm"
            />
          </div>
        </div>

        <div class="actions">
          <div class="simulate-options">
            <label>
              <span>草街放水量</span>
              <input v-model.number="caojieFlow" type="number" step="0.1" placeholder="m³/s" />
            </label>
            <label>
              <span>降雨量</span>
              <input v-model.number="rainCount" type="number" step="0.1" placeholder="mm" />
            </label>
          </div>
          <button class="btn-calc" :disabled="calculating" @click="calc">
            {{ calculating ? '计算中...' : '计算预警' }}
          </button>
          <button class="btn-clear" :disabled="calculating" @click="resetStaticData">重置</button>
        </div>

        <div class="section-title">
          <span>告警结果</span>
        </div>
        <div class="result">
          <div v-if="!result" class="hint" style="padding:0;">
            输入某站水位后点击「计算预警」。<br>仅可输入一个站点，其他站点将自动锁定。
          </div>
          <template v-else>
            <div
              class="level-big"
              :class="result.hasWarning ? 'level-big--warning' : 'level-big--safe'"
              :style="{ '--status-color': result.hasWarning ? resultColor : '#69eaff' }"
              role="status"
              :aria-live="result.hasWarning ? 'assertive' : 'polite'"
            >
              <span class="level-big__pulse" aria-hidden="true"></span>
              <span class="level-big__label">
                {{ result.hasWarning ? result.levelName + '预警' : '当前无预警' }}
              </span>
              <span class="level-big__signal" aria-hidden="true">
                <i></i><i></i><i></i><i></i>
              </span>
            </div>
            <div v-if="simulateRise !== null" class="kv">
              <span class="k">朝天门上涨</span>
              <span class="v">
                {{ simulateRise.toFixed(2) }} m
                <span v-if="simulateStationName">（{{ simulateStationName }}）</span>
              </span>
            </div>
            <div class="kv">
              <span class="k">朝天门当前水位</span>
              <span class="v">{{ chaotianmenLevel }}</span>
            </div>
            <div class="kv"><span class="k">流速</span><span class="v">{{ result.velocity }} km/h</span></div>
            <div class="kv"><span class="k">最近站点</span><span class="v">{{ result.nearestStation || '-' }}（{{ result.nearestDistanceKm }} km）</span></div>
            <div class="kv"><span class="k">预估到达时间</span><span class="v">{{ result.arrivalHours.toFixed(3) }} h（{{ result.arrivalTime }}）</span></div>
            <div v-if="result.hasWarning" class="kv"><span class="k">预警时间（减1h）</span><span class="v">{{ result.warningHours.toFixed(3) }} h（{{ result.warningTime }}）</span></div>
            <div class="kv">
              <span class="k">达警戒站点</span>
              <span class="v">{{ result.reachedWarningStations.length ? result.reachedWarningStations.join('、') : '无' }}</span>
            </div>
            <div v-if="result.matchedRules.length" class="rules">
              命中规则：
              <ul>
                <li v-for="(rule, i) in result.matchedRules" :key="i">{{ rule }}</li>
              </ul>
            </div>
          </template>
        </div>

        <!-- <div class="chart-box">
          <h3>水位剖面图（按距朝天门距离排序）</h3>
          <div ref="chartRef" class="chart-canvas"></div>
        </div> -->

        <div class="hint">
          <template v-if="unavailableStationNames.length">
            注：{{ unavailableStationNames.join('、') }} 暂缺完整警戒/保证水位，地图以灰色标记且不参与触发。<br>
          </template>
          朝天门为预警目标站，郭家沱为出境参考站，不作为上游触发输入。
        </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref, computed, nextTick } from 'vue'
import L from 'leaflet'
import * as echarts from 'echarts/core'
import { LineChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, LegendComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import { STATIONS, RIVERS, COLORS, LEVEL_COLOR } from './flood-data'
import type { Station } from './flood-data'
import { evaluateWarning, fmtHours } from './flood-engine'
import type { WarningResult } from './flood-engine'
import { loadHydrologyStations } from './waterlog-station.service'
import type { StationDataSource } from './waterlog-station.service'
import adminGeoJson from './map-data/waterlog-admin.gcj02.geojson'
import riverGeoJson from './map-data/waterlog-rivers.gcj02.geojson'

echarts.use([LineChart, GridComponent, TooltipComponent, LegendComponent, CanvasRenderer])

const emit = defineEmits(['close'])

const visible = ref(true)
const isClosing = ref(false)
let closeTimer: ReturnType<typeof setTimeout> | null = null

// ---- 地图 ----
const mapRef = ref<HTMLElement | null>(null)
const flowCanvasRef = ref<HTMLCanvasElement | null>(null)
let map: L.Map | null = null
const markers: Record<string, L.CircleMarker> = {}
const labelMarkers: Record<string, L.Marker> = {}
let warningPathLayers: L.Polyline[] = []
let pathAnimTimer: ReturnType<typeof setInterval> | null = null
let overlayMarker: L.Marker | null = null
let flowAnimationFrame: number | null = null
let mapResizeObserver: ResizeObserver | null = null
let mapResizeTimer: ReturnType<typeof setTimeout> | null = null
const particleEnabled = ref(true)

// 离线高德瓦片实际按 z/y/x.jpg 存放，Leaflet 查找键统一转换为 x/y。
const OFFLINE_TILE_CONFIG = {
  z: 10,
  minX: 811,
  maxX: 823,
  minY: 418,
  maxY: 426,
  tileSize: 256
} as const

type WebpackAsset = string | { default: string }

const offlineTileContext = require.context('../../../../assets/map/10', true, /\.jpg$/)
const offlineTileMap: Record<string, string> = {}

offlineTileContext.keys().forEach(key => {
  const match = key.match(/^\.\/(\d+)\/(\d+)\.jpg$/)
  if (!match) return
  const [, tileY, tileX] = match
  const asset = offlineTileContext(key) as WebpackAsset
  offlineTileMap[`${tileX}/${tileY}`] = typeof asset === 'string' ? asset : asset.default
})

function tileToLngLat(x: number, y: number, z: number) {
  const scale = Math.pow(2, z)
  const lng = x / scale * 360 - 180
  const lat = Math.atan(Math.sinh(Math.PI * (1 - 2 * y / scale))) * 180 / Math.PI
  return { lng, lat }
}

const offlineTileBounds = (() => {
  const northWest = tileToLngLat(OFFLINE_TILE_CONFIG.minX, OFFLINE_TILE_CONFIG.minY, OFFLINE_TILE_CONFIG.z)
  const southEast = tileToLngLat(OFFLINE_TILE_CONFIG.maxX + 1, OFFLINE_TILE_CONFIG.maxY + 1, OFFLINE_TILE_CONFIG.z)
  return L.latLngBounds([southEast.lat, northWest.lng], [northWest.lat, southEast.lng])
})()

function createOfflineTileLayer() {
  const layer = L.tileLayer('', {
    tileSize: OFFLINE_TILE_CONFIG.tileSize,
    minZoom: 8,
    maxZoom: 12,
    minNativeZoom: OFFLINE_TILE_CONFIG.z,
    maxNativeZoom: OFFLINE_TILE_CONFIG.z,
    bounds: offlineTileBounds,
    noWrap: true,
    keepBuffer: 2,
    updateWhenIdle: false,
    className: 'waterlog-offline-tile'
  })

  layer.getTileUrl = coords => offlineTileMap[`${coords.x}/${coords.y}`] || L.Util.emptyImageUrl
  return layer
}

function configureMapPanes() {
  if (!map) return
  const panes: Array<[string, number]> = [
    ['adminPane', 260],
    ['riverBasePane', 320],
    ['riverMainPane', 410],
    ['warningPane', 680],
  ]
  panes.forEach(([name, zIndex]) => {
    const pane = map!.getPane(name) || map!.createPane(name)
    pane.style.zIndex = String(zIndex)
    pane.style.pointerEvents = 'none'
  })
}

function addReferenceLayers() {
  if (!map) return
  const adminRenderer = L.canvas({ pane: 'adminPane', padding: 0.35 })
  const riverRenderer = L.canvas({ pane: 'riverBasePane', padding: 0.35 })

  L.geoJSON(adminGeoJson as any, {
    pane: 'adminPane',
    renderer: adminRenderer,
    interactive: false,
    style: feature => {
      const adcode = Number(feature?.properties?.adcode || 0)
      const fills = ['#0e3359', '#123d68', '#174871']
      return {
        pane: 'adminPane',
        renderer: adminRenderer,
        fillColor: fills[Math.abs(adcode) % fills.length],
        fillOpacity: 0.46,
        color: '#071f3d',
        weight: 0.8,
        opacity: 0.9,
      }
    },
  }).addTo(map)

  L.geoJSON(riverGeoJson as any, {
    pane: 'riverBasePane',
    renderer: riverRenderer,
    interactive: false,
    style: feature => {
      const rank = Number(feature?.properties?.rank || 3)
      if (rank === 1) return { pane: 'riverBasePane', renderer: riverRenderer, color: '#20aef7', weight: 2, opacity: 0.86 }
      if (rank === 2) return { pane: 'riverBasePane', renderer: riverRenderer, color: '#0f99ee', weight: 1.25, opacity: 0.72 }
      return { pane: 'riverBasePane', renderer: riverRenderer, color: '#087bcf', weight: 0.72, opacity: 0.5 }
    },
  }).addTo(map)
}
let lastFlowFrame = 0

type FlowRiverKey = 'jialing' | 'yangtze'
type FlowPoint = { x: number; y: number }
type FlowPath = {
  points: FlowPoint[]
  distances: number[]
  total: number
  color: string
}

const projectedFlowPaths: Partial<Record<FlowRiverKey, FlowPath>> = {}
const flowParticles = Array.from({ length: 46 }, (_, index) => ({
  river: (index % 5 < 3 ? 'jialing' : 'yangtze') as FlowRiverKey,
  phase: ((index * 37) % 101) / 101,
  speed: 18 + (index % 7) * 3.5,
  radius: 0.9 + (index % 4) * 0.42,
  alpha: 0.46 + (index % 5) * 0.1
}))

// ---- 图表 ----
const chartRef = ref<HTMLElement | null>(null)
let waterChart: echarts.ECharts | null = null

// ---- 数据 ----
const stationList = ref<Station[]>(STATIONS.map(station => ({ ...station })))
let loadedStationSnapshot: Station[] = STATIONS.map(station => ({ ...station }))
const stationLoading = ref(true)
const stationDataSource = ref<StationDataSource>('base')
const levels = ref<Record<string, number>>({})
const result = ref<WarningResult | null>(null)
const caojieFlow = ref<number | null>(null)
const rainCount = ref<number | null>(null)
const caojieDischarge = caojieFlow
const rainfall = rainCount
const simulateRise = ref<number | null>(null)
const simulateStationName = ref('')
const calculating = ref(false)
const lockedStation = ref('')
let isComponentAlive = false

const resultColor = computed(() => {
  if (!result.value) return '#9E9E9E'
  return LEVEL_COLOR[result.value.levelName] || '#9E9E9E'
})

const chaotianmenLevel = computed(() => {
  const station = stationList.value.find(s => s.name === '朝天门')
  const level = station ? normalizeNumber(station.z) : null
  return level === null ? '-' : `${level.toFixed(2)} m`
})

const unavailableStationNames = computed(() => stationList.value
  .filter(station => !hasThresholds(station))
  .map(station => station.name))

function hasThresholds(station: Station): boolean {
  return station.warning != null && station.guarantee != null
}

function isTrigger(s: Station): boolean {
  return s.name !== '朝天门' && s.name !== '郭家沱' && hasThresholds(s)
}

function markerColor(s: Station): string {
  if (s.name === '朝天门') return '#E53935'
  if (!hasThresholds(s)) return '#9E9E9E'
  return COLORS[s.river] || '#9E9E9E'
}

function fmtThr(v: number | null | undefined): string {
  return (v === null || v === undefined) ? '—' : String(v)
}

// ---- 单站点输入锁定逻辑 ----
// 判断某个站点的输入框是否可编辑
function isInputDisabled(stationName: string): boolean {
  if (!lockedStation.value) return false
  return lockedStation.value !== stationName
}

// 输入时检查锁定状态
function onStationInput(stationName: string) {
  const value = normalizeNumber(levels.value[stationName])
  if (value !== null && value !== 0) {
    // 有值了，锁定其他站点
    lockedStation.value = stationName
  } else {
    // 清零了，解除锁定
    lockedStation.value = ''
  }
  updateChart()
}

function cloneStations(stations: Station[]): Station[] {
  return stations.map(station => ({
    ...station,
    missingFields: station.missingFields ? [...station.missingFields] : undefined
  }))
}

function initializeStationLevels() {
  const nextLevels: Record<string, number> = {}
  stationList.value.forEach(station => {
    nextLevels[station.name] = 0
  })
  levels.value = nextLevels
  lockedStation.value = ''
}

// ---- 初始化 ----
function normalizeNumber(value: any): number | null {
  if (value === '' || value === null || value === undefined) return null
  const num = Number(value)
  return Number.isFinite(num) ? num : null
}

function escapeHtml(value: string) {
  return value.replace(/[&<>"']/g, char => ({
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
    '"': '&quot;',
    "'": '&#39;'
  }[char] || char))
}

function getLocalWarningResult() {
  const localResult = evaluateWarning(stationList.value, levels.value)
  localResult.arrivalTime = fmtHours(localResult.arrivalHours)
  localResult.warningTime = localResult.warningImmediate ? '已不足1小时(立即)' : fmtHours(localResult.warningHours)
  return localResult
}

function getNearestEstimateStation(preferredName?: string) {
  const triggerStations = stationList.value.filter(isTrigger)
  const preferred = preferredName ? triggerStations.find(s => s.name === preferredName) : null
  if (preferred) return preferred

  return triggerStations
    .filter(s => Number.isFinite(s.dist))
    .sort((a, b) => a.dist - b.dist)[0] || null
}

function getFallbackVelocity(level: number) {
  if (level === 3) return 7
  if (level === 2) return 5.5
  return 4
}

function completeEstimateInfo(warningResult: WarningResult, preferredName?: string): WarningResult {
  const nextResult = { ...warningResult }
  const station = getNearestEstimateStation(nextResult.nearestStation || preferredName)
  const velocity = normalizeNumber(nextResult.velocity) || getFallbackVelocity(nextResult.level)

  if (!nextResult.nearestStation && station) {
    nextResult.nearestStation = station.name
  }

  if ((!nextResult.nearestDistanceKm || nextResult.nearestDistanceKm <= 0) && station) {
    nextResult.nearestDistanceKm = station.dist
  }

  nextResult.velocity = velocity
  if (!nextResult.arrivalHours || nextResult.arrivalHours <= 0) {
    nextResult.arrivalHours = nextResult.nearestDistanceKm > 0 ? nextResult.nearestDistanceKm / velocity : 0
  }

  nextResult.warningImmediate = nextResult.arrivalHours < 1
  if (!nextResult.warningHours || nextResult.warningHours <= 0) {
    nextResult.warningHours = Math.max(0, nextResult.arrivalHours - 1)
  }
  nextResult.arrivalTime = fmtHours(nextResult.arrivalHours)
  nextResult.warningTime = nextResult.warningImmediate ? '已不足1小时(立即)' : fmtHours(nextResult.warningHours)
  return nextResult
}

function applyWarningResult(warningResult: WarningResult, preferredName?: string) {
  const nextResult = completeEstimateInfo(warningResult, preferredName)
  result.value = nextResult
  if (nextResult.reachedWarningStations) highlightReached(nextResult.reachedWarningStations)
  drawWarningPath(nextResult.hasWarning ? nextResult.nearestStation : null)
  showMapOverlay(nextResult)
  updateChart()
}

function hideMapOverlay() {
  if (!overlayMarker) return
  map?.removeLayer(overlayMarker)
  overlayMarker = null
}

function rebuildMap() {
  if (!map) return
  warningPathLayers.forEach(layer => map?.removeLayer(layer))
  warningPathLayers = []
  if (overlayMarker) {
    map.removeLayer(overlayMarker)
    overlayMarker = null
  }
  if (pathAnimTimer) {
    clearInterval(pathAnimTimer)
    pathAnimTimer = null
  }
  Object.keys(markers).forEach(key => delete markers[key])
  Object.keys(labelMarkers).forEach(key => delete labelMarkers[key])
  map.remove()
  map = null
  initMap()
}

function createStationLabel(s: Station) {
  if (!map) return
  const icon = L.divIcon({
    className: 'waterlog-station-label',
    html: `<span class="waterlog-station-label__text">${escapeHtml(s.name)}</span>`,
    iconSize: [0, 0],
    iconAnchor: [0, 0]
  })
  labelMarkers[s.name] = L.marker([s.lat, s.lon], {
    icon,
    interactive: false,
    keyboard: false,
    zIndexOffset: 500
  }).addTo(map)
}

function initMap() {
  if (!mapRef.value) return
  map = L.map(mapRef.value, {
    attributionControl: false,
    zoomControl: true,
    zoomAnimation: true,
    fadeAnimation: true,
    minZoom: 8,
    maxZoom: 12,
    maxBounds: offlineTileBounds,
    maxBoundsViscosity: 0.72
  }).setView([29.55, 106.35], 9)

  configureMapPanes()
  createOfflineTileLayer().addTo(map)
  addReferenceLayers()

  const jl = RIVERS.jialing.map(p => [p[1], p[0]] as [number, number])
  const yz = RIVERS.yangtze.map(p => [p[1], p[0]] as [number, number])

  addRiverLayers(jl, '#2196F3', '#9eeeff', 'jialing')
  addRiverLayers(yz, '#FF9800', '#fff0aa', 'yangtze')

  stationList.value.forEach(s => {
    const color = markerColor(s)
    const isBig = s.name === '朝天门'
    const m = L.circleMarker([s.lat, s.lon], {
      radius: isBig ? 11 : 7,
      color: '#fff',
      weight: 2,
      fillColor: color,
      fillOpacity: 0.95,
      className: isBig ? 'station-marker station-marker--warning' : 'station-marker'
    })
    m.bindPopup(popupHtml(s))
    m.addTo(map!)
    createStationLabel(s)
    markers[s.name] = m
  })

  fitMapToStations()
  map.on('move zoom resize', refreshFlowGeometry)

  // 弹窗由小尺寸 playground 容器向外溢出，Leaflet 首次读取到的尺寸可能偏小。
  // 连续两帧校正尺寸和视野，避免河网挤在地图右下角。
  requestAnimationFrame(() => {
    if (!map) return
    map.invalidateSize({ animate: false })
    fitMapToStations()
    requestAnimationFrame(refreshFlowGeometry)
  })
}

function addRiverLayers(
  points: [number, number][],
  color: string,
  highlight: string,
  riverClass: FlowRiverKey
) {
  if (!map) return
  L.polyline(points, {
    pane: 'riverMainPane',
    color,
    weight: 30,
    opacity: 0.08,
    interactive: false,
    className: `river-glow river-glow--${riverClass}`
  }).addTo(map)
  L.polyline(points, {
    pane: 'riverMainPane',
    color,
    weight: 14,
    opacity: 0.22,
    interactive: false,
    className: `river-bank river-bank--${riverClass}`
  }).addTo(map)
  L.polyline(points, {
    pane: 'riverMainPane',
    color,
    weight: 7,
    opacity: 0.9,
    interactive: false,
    className: `river-body river-body--${riverClass}`
  }).addTo(map)
  L.polyline(points, {
    pane: 'riverMainPane',
    color: highlight,
    weight: 2.8,
    opacity: 0.92,
    interactive: false,
    className: `river-current river-current--${riverClass}`
  }).addTo(map)
  L.polyline(points, {
    pane: 'riverMainPane',
    color: '#ffffff',
    weight: 3.6,
    opacity: 0.88,
    interactive: false,
    className: `river-spark river-spark--${riverClass}`
  }).addTo(map)
}

function fitMapToStations() {
  if (!map || !stationList.value.length) return
  const lats = stationList.value.map(s => s.lat)
  const lons = stationList.value.map(s => s.lon)
  map.fitBounds(
    [[Math.min(...lats), Math.min(...lons)], [Math.max(...lats), Math.max(...lons)]],
    { paddingTopLeft: [92, 100], paddingBottomRight: [92, 120], animate: false }
  )
}

function buildProjectedPath(river: [number, number][], color: string): FlowPath | null {
  if (!map) return null
  const points = river.map(([lon, lat]) => {
    const point = map!.latLngToContainerPoint([lat, lon])
    return { x: point.x, y: point.y }
  })
  if (points.length < 2) return null

  const distances = [0]
  let total = 0
  for (let index = 1; index < points.length; index++) {
    const dx = points[index].x - points[index - 1].x
    const dy = points[index].y - points[index - 1].y
    total += Math.sqrt(dx * dx + dy * dy)
    distances.push(total)
  }
  return { points, distances, total, color }
}

function refreshFlowGeometry() {
  const canvas = flowCanvasRef.value
  const host = mapRef.value
  if (!canvas || !host || !map) return

  const width = Math.max(1, host.clientWidth)
  const height = Math.max(1, host.clientHeight)
  const pixelRatio = Math.min(window.devicePixelRatio || 1, 1.5)
  canvas.width = Math.round(width * pixelRatio)
  canvas.height = Math.round(height * pixelRatio)
  canvas.style.width = `${width}px`
  canvas.style.height = `${height}px`

  const context = canvas.getContext('2d')
  context?.setTransform(pixelRatio, 0, 0, pixelRatio, 0, 0)
  projectedFlowPaths.jialing = buildProjectedPath(RIVERS.jialing, '#63dcff') || undefined
  projectedFlowPaths.yangtze = buildProjectedPath(RIVERS.yangtze, '#ffc45c') || undefined
}

function pointOnFlowPath(path: FlowPath, distance: number): FlowPoint {
  const target = ((distance % path.total) + path.total) % path.total
  let index = 1
  while (index < path.distances.length && path.distances[index] < target) index++
  const endIndex = Math.min(index, path.points.length - 1)
  const startIndex = Math.max(0, endIndex - 1)
  const startDistance = path.distances[startIndex]
  const segmentLength = Math.max(1, path.distances[endIndex] - startDistance)
  const ratio = (target - startDistance) / segmentLength
  const start = path.points[startIndex]
  const end = path.points[endIndex]
  return {
    x: start.x + (end.x - start.x) * ratio,
    y: start.y + (end.y - start.y) * ratio
  }
}

function drawFlowParticles(timestamp: number) {
  if (!particleEnabled.value) {
    flowAnimationFrame = null
    return
  }
  flowAnimationFrame = requestAnimationFrame(drawFlowParticles)
  if (timestamp - lastFlowFrame < 32) return
  lastFlowFrame = timestamp

  const canvas = flowCanvasRef.value
  const context = canvas?.getContext('2d')
  const host = mapRef.value
  if (!canvas || !context || !host) return

  context.clearRect(0, 0, host.clientWidth, host.clientHeight)
  context.save()
  context.globalCompositeOperation = 'lighter'

  flowParticles.forEach(particle => {
    const path = projectedFlowPaths[particle.river]
    if (!path || path.total <= 0) return
    const distance = particle.phase * path.total + timestamp * particle.speed / 1000
    const point = pointOnFlowPath(path, distance)
    const tail = pointOnFlowPath(path, distance - 13 - particle.radius * 2)
    const gradient = context.createLinearGradient(tail.x, tail.y, point.x, point.y)
    gradient.addColorStop(0, 'rgba(255,255,255,0)')
    gradient.addColorStop(1, path.color)
    context.beginPath()
    context.moveTo(tail.x, tail.y)
    context.lineTo(point.x, point.y)
    context.strokeStyle = gradient
    context.globalAlpha = particle.alpha
    context.lineWidth = particle.radius * 1.4
    context.shadowColor = path.color
    context.shadowBlur = 8
    context.stroke()

    context.beginPath()
    context.arc(point.x, point.y, particle.radius, 0, Math.PI * 2)
    context.fillStyle = '#ffffff'
    context.fill()
  })

  const chaotianmen = stationList.value.find(station => station.name === '朝天门')
  if (chaotianmen && map) {
    const center = map.latLngToContainerPoint([chaotianmen.lat, chaotianmen.lon])
    const pulse = (timestamp / 26) % 34
    context.globalAlpha = Math.max(0, 0.6 - pulse / 56)
    context.shadowBlur = 12
    context.strokeStyle = '#82f4ff'
    context.lineWidth = 1.5
    context.beginPath()
    context.arc(center.x, center.y, 12 + pulse, 0, Math.PI * 2)
    context.stroke()
  }
  context.restore()
}

function startFlowAnimation() {
  if (flowAnimationFrame !== null) cancelAnimationFrame(flowAnimationFrame)
  refreshFlowGeometry()
  if (!particleEnabled.value) {
    flowAnimationFrame = null
    return
  }
  flowAnimationFrame = requestAnimationFrame(drawFlowParticles)
}

function toggleParticleAnimation() {
  particleEnabled.value = !particleEnabled.value
  if (particleEnabled.value) {
    startFlowAnimation()
    return
  }
  if (flowAnimationFrame !== null) cancelAnimationFrame(flowAnimationFrame)
  flowAnimationFrame = null
  const canvas = flowCanvasRef.value
  const context = canvas?.getContext('2d')
  if (canvas && context) context.clearRect(0, 0, canvas.width, canvas.height)
}

function observeMapSize() {
  if (!mapRef.value || typeof ResizeObserver === 'undefined') return
  mapResizeObserver = new ResizeObserver(() => {
    if (mapResizeTimer) clearTimeout(mapResizeTimer)
    mapResizeTimer = setTimeout(() => {
      if (!map) return
      map.invalidateSize({ animate: false })
      fitMapToStations()
      refreshFlowGeometry()
    }, 80)
  })
  mapResizeObserver.observe(mapRef.value)
}

function popupHtml(s: Station): string {
  let html = `<b>${s.name}</b><br>
    流域：${s.river}<br>
    距朝天门：${s.dist} km<br>
    警戒水位：${fmtThr(s.warning)} m<br>
    保证水位：${fmtThr(s.guarantee)} m`
  if (isTrigger(s)) {
    html += `<br><div style="display:flex;gap:4px;align-items:center;margin-top:2px;">
      <input id="pop_${s.name}" type="number" step="0.1" value="${levels.value[s.name] ?? 0}" style="width:78px;padding:4px;border:1px solid #ccc;border-radius:3px;">
      <button onclick="applyFromPopup('${s.name}')" style="padding:4px 8px;border:none;border-radius:3px;background:#2196F3;color:#fff;cursor:pointer;">设置</button>
    </div>`
  }
  return html
}

// ---- 初始化图表 ----
function initChart() {
  if (!chartRef.value) return
  waterChart = echarts.init(chartRef.value)
  updateChart()
}

function updateChart() {
  if (!waterChart) return
  const trigger = stationList.value.filter(isTrigger).sort((a, b) => a.dist - b.dist)
  const labels = trigger.map(s => s.name)

  waterChart.setOption({
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(4, 20, 45, .92)',
      borderColor: 'rgba(90, 213, 255, .38)',
      textStyle: { color: '#eaf7ff' }
    },
    legend: {
      data: ['当前水位', '警戒水位', '保证水位'],
      textStyle: { fontSize: 11, color: '#b8d9ff' }
    },
    grid: { left: 40, right: 16, top: 30, bottom: 50 },
    xAxis: {
      type: 'category',
      data: labels,
      axisLabel: { fontSize: 10, rotate: 45, color: '#b8d9ff' },
      axisLine: { lineStyle: { color: 'rgba(126, 203, 255, .45)' } },
      axisTick: { lineStyle: { color: 'rgba(126, 203, 255, .35)' } }
    },
    yAxis: {
      type: 'value',
      name: '水位(m)',
      nameTextStyle: { fontSize: 11, color: '#b8d9ff' },
      axisLabel: { fontSize: 10, color: '#b8d9ff' },
      axisLine: { lineStyle: { color: 'rgba(126, 203, 255, .45)' } },
      splitLine: { lineStyle: { color: 'rgba(126, 203, 255, .14)' } }
    },
    series: [
      {
        name: '当前水位',
        type: 'line',
        data: trigger.map(s => levels.value[s.name] || 0),
        itemStyle: { color: '#E53935' },
        areaStyle: { color: 'rgba(229,57,53,.10)' },
        smooth: true,
        symbolSize: 6
      },
      {
        name: '警戒水位',
        type: 'line',
        data: trigger.map(s => s.warning == null ? null : s.warning),
        itemStyle: { color: '#FF9800' },
        lineStyle: { type: 'dashed' },
        smooth: true,
        symbolSize: 4,
        connectNulls: true
      },
      {
        name: '保证水位',
        type: 'line',
        data: trigger.map(s => s.guarantee == null ? null : s.guarantee),
        itemStyle: { color: '#9C27B0' },
        lineStyle: { type: [8, 4] },
        smooth: true,
        symbolSize: 4,
        connectNulls: true
      }
    ]
  })
}

// ---- 计算预警 ----
async function calc() {
  if (calculating.value) return
  calculating.value = true
  simulateRise.value = null
  simulateStationName.value = lockedStation.value

  try {
    await nextTick()
    if (!isComponentAlive) return
    applyWarningResult(getLocalWarningResult(), lockedStation.value || undefined)
  } finally {
    if (isComponentAlive) calculating.value = false
  }
}

function clearAll() {
  stationList.value.forEach(s => {
    if (isTrigger(s)) levels.value[s.name] = 0
  })
  caojieFlow.value = null
  rainCount.value = null
  simulateRise.value = null
  simulateStationName.value = ''
  result.value = null
  resetMarkers()
  drawWarningPath(null)
  hideMapOverlay()
  updateChart()
}

function resetStaticData() {
  stationList.value = cloneStations(loadedStationSnapshot)
  initializeStationLevels()
  caojieFlow.value = null
  rainCount.value = null
  simulateRise.value = null
  simulateStationName.value = ''
  result.value = null
  resetMarkers()
  drawWarningPath(null)
  hideMapOverlay()
  rebuildMap()
  updateChart()
}

function example(kind: string) {
  clearAll()
  const set: Record<string, Record<string, string>> = {
    '蓝': { '磁器口': '186.0' },
    '黄': { '鸭嘴': '207.0', '东津沱': '209.0' },
    '红': { '菜园坝': '187.0', '玄坛庙': '184.0' }
  }
  const data = set[kind] || {}
  Object.entries(data).forEach(([name, v]) => {
    levels.value[name] = parseFloat(v)
  })
  updateChart()
}

// ---- 地图标记操作 ----
function resetMarkers() {
  stationList.value.forEach(s => {
    const m = markers[s.name]
    if (!m) return
    const color = markerColor(s)
    m.setStyle({ fillColor: color, radius: s.name === '朝天门' ? 11 : 7 })
  })
}

function highlightReached(names: string[]) {
  resetMarkers()
  names.forEach(name => {
    const m = markers[name]
    if (m) m.setStyle({ fillColor: '#E53935', radius: 10, fillOpacity: 1 })
  })
}

function nearestIndex(coords: number[][], st: Station): number {
  let best = -1, bd = Infinity
  coords.forEach((c, i) => {
    const d = (c[0] - st.lon) ** 2 + (c[1] - st.lat) ** 2
    if (d < bd) { bd = d; best = i }
  })
  return best
}

function drawWarningPath(nearestName: string | null) {
  warningPathLayers.forEach(l => map?.removeLayer(l))
  warningPathLayers = []
  if (pathAnimTimer) { clearInterval(pathAnimTimer); pathAnimTimer = null }
  if (!nearestName) return

  const near = stationList.value.find(s => s.name === nearestName)
  const ctm = stationList.value.find(s => s.name === '朝天门')
  if (!near || !ctm || !map) return

  const river = near.river === '长江' ? RIVERS.yangtze : RIVERS.jialing
  const iNear = nearestIndex(river, near)
  const iCtm = nearestIndex(river, ctm)
  if (iNear < 0 || iCtm < 0) return

  const lo = Math.min(iNear, iCtm), hi = Math.max(iNear, iCtm)
  const seg = river.slice(lo, hi + 1).map(p => [p[1], p[0]] as [number, number])

  const glow = L.polyline([], { pane: 'warningPane', color: '#E53935', weight: 12, opacity: 0.18 }).addTo(map)
  const main = L.polyline([], { pane: 'warningPane', color: '#E53935', weight: 5, opacity: 0.95 }).addTo(map)
  warningPathLayers.push(glow, main)

  let i = 1
  const step = Math.max(60, Math.floor(6000 / seg.length))
  pathAnimTimer = setInterval(() => {
    const part = seg.slice(0, i + 1)
    glow.setLatLngs(part)
    main.setLatLngs(part)
    i++
    if (i >= seg.length) i = 1
  }, step)
}

function showMapOverlay(r: WarningResult) {
  if (overlayMarker) { map?.removeLayer(overlayMarker); overlayMarker = null }

  const ctm = stationList.value.find(s => s.name === '朝天门')
  if (!ctm || !map) return

  const lc = r.hasWarning ? (LEVEL_COLOR[r.levelName] || '#E53935') : '#7ecbff'
  const levelText = r.hasWarning ? `${r.levelName}预警` : '暂无预警'
  const warningTimeText = r.hasWarning ? r.warningTime : '暂无预警'
  const html = `<div class="map-overlay-card">
    <div style="font-size:11px;color:#999;letter-spacing:1px;">预警等级</div>
    <div class="ol-level" style="color:${lc}">${levelText}</div>
    <div class="ol-kv">预警时间：<b>${warningTimeText}</b></div>
    <div class="ol-kv">预估到达：${r.arrivalTime}</div>
    <div class="ol-kv">最近站点：${r.nearestStation || '-'}（${r.nearestDistanceKm} km）</div>
  </div>`
  const icon = L.divIcon({ className: 'map-overlay-icon', html, iconSize: [190, 120], iconAnchor: [95, 150] })

  // 创建自定义 pane，层级高于 tooltipPane(650) 和 popupPane(700)
  let warningPane = map.getPane('warningPane')
  if (!warningPane) {
    warningPane = map.createPane('warningPane')
    warningPane.style.zIndex = '800'
  }
  overlayMarker = L.marker([ctm.lat, ctm.lon], { icon, interactive: false, pane: 'warningPane' }).addTo(map)
}

// ---- 弹窗关闭 ----
function closePopup(event?: Event) {
  event?.stopPropagation?.()
  event?.preventDefault?.()
  if (isClosing.value) return
  isClosing.value = true
  visible.value = false
  if (closeTimer) clearTimeout(closeTimer)
  closeTimer = setTimeout(() => {
    isClosing.value = false
    closeTimer = null
    emit('close')
  }, 180)
}

// ---- 全局函数（供 popup 内 onclick 调用）----
function applyFromPopup(name: string) {
  const popInput = document.getElementById('pop_' + name) as HTMLInputElement | null
  if (popInput) {
    const v = parseFloat(popInput.value)
    levels.value[name] = isNaN(v) ? 0 : v
  }
  onStationInput(name)
}

onMounted(async () => {
  isComponentAlive = true
  if (window.matchMedia?.('(prefers-reduced-motion: reduce)').matches) {
    particleEnabled.value = false
  }
  const stationResult = await loadHydrologyStations()
  if (!isComponentAlive) return
  stationList.value = cloneStations(stationResult.stations)
  loadedStationSnapshot = cloneStations(stationResult.stations)
  stationDataSource.value = stationResult.source
  stationLoading.value = false
  initializeStationLevels()
  if (stationResult.error) {
    console.warn('水情站点数据加载失败，已保留本地基线站点:', stationResult.error)
  }

  await nextTick()
  if (!isComponentAlive) return
  initMap()
  initChart()
  observeMapSize()
  startFlowAnimation()

  // 暴露给 Leaflet popup 内的 onclick
  ;(window as any).applyFromPopup = applyFromPopup
})

onBeforeUnmount(() => {
  isComponentAlive = false
  if (closeTimer) clearTimeout(closeTimer)
  if (pathAnimTimer) clearInterval(pathAnimTimer)
  if (mapResizeTimer) clearTimeout(mapResizeTimer)
  if (flowAnimationFrame !== null) cancelAnimationFrame(flowAnimationFrame)
  mapResizeObserver?.disconnect()
  mapResizeObserver = null
  if (waterChart) { waterChart.dispose(); waterChart = null }
  if (map) { map.remove(); map = null }
  delete (window as any).applyFromPopup
})

defineExpose({ closePopup })
</script>

<script lang="ts">
export default {
  name: 'WaterlogPredictionDia',
  version: '2.2.0'
}
</script>

<style lang="scss" scoped>
@font-face {
  font-family: 'AlibabaPuHuiTi';
  src: url('../font/Alibaba_PuHuiTi_2.0_55_Regular_55_Regular.ttf') format('truetype');
  font-weight: normal;
  font-style: normal;
}

.waterlog-prediction-dia {
  font-family: 'AlibabaPuHuiTi', sans-serif;
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  z-index: 1001;
  width: min(1700px, calc(100vw - 16px));
  height: min(1000px, calc(100vh - 16px));
  display: flex;
  flex-direction: column;
  overflow: hidden;
  pointer-events: auto;
  background:
    url('../img/Group-2136640490-1.png') top center / 100% 60px no-repeat,
    url('../img/Rectangle-346242153.png') center / 100% 100% no-repeat;
  border: 1px solid rgba(37, 134, 255, 0.25);
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.45);
  color: #b8d9ff;
  font-size: 14px;
}

.modal-header {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  flex-shrink: 0;

  .header-titles {
    display: flex;
    align-items: baseline;
    gap: 12px;
  }

  .header-title {
    background: linear-gradient(180deg, #FFFFFF 0%, #5FBCFF 100%);
    -webkit-background-clip: text;
    background-clip: text;
    color: transparent;
    font-size: 22px;
    font-weight: 700;
    text-shadow: 0 0 12px rgba(95, 188, 255, 0.3);
    white-space: nowrap;
  }

  .header-subtitle {
    color: #8EC9FF;
    font-size: 14px;
    font-weight: 400;
    white-space: nowrap;
  }

  .close-btn {
    position: absolute;
    right: 20px;
    top: 50%;
    transform: translateY(-50%);
    width: 32px;
    height: 32px;
    border: 1px solid rgba(69, 200, 255, 0.3);
    background: rgba(16, 64, 126, 0.4);
    color: #7AA7CF;
    font-size: 20px;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    pointer-events: auto;

    &:hover {
      color: #ffffff;
      border-color: rgba(69, 200, 255, 0.6);
      background: rgba(24, 92, 179, 0.6);
    }
  }
}

/* 防汛预警内容区 */
.flood-main {
  flex: 1;
  display: flex;
  min-height: 0;
}

.map-wrap {
  flex: 1;
  position: relative;
  min-width: 0;
  overflow: hidden;
  isolation: isolate;
  background: #061726;
}

.map {
  position: absolute;
  inset: 0;
  z-index: 1;
  background:
    radial-gradient(circle at 76% 46%, rgba(17, 105, 144, .22) 0%, transparent 28%),
    radial-gradient(ellipse at 44% 38%, #123853 0%, #0a2438 52%, #05131f 100%);
}

.water-grid {
  position: absolute;
  inset: 0;
  z-index: 200;
  pointer-events: none;
  opacity: .1;
  background-image:
    linear-gradient(rgba(77, 193, 255, .07) 1px, transparent 1px),
    linear-gradient(90deg, rgba(77, 193, 255, .07) 1px, transparent 1px),
    radial-gradient(circle at center, transparent 25%, rgba(1, 10, 20, .35) 100%);
  background-size: 48px 48px, 48px 48px, 100% 100%;
  mask-image: linear-gradient(135deg, rgba(0, 0, 0, .15), #000 48%, rgba(0, 0, 0, .25));
}

.flow-canvas {
  position: absolute;
  inset: 0;
  z-index: 440;
  pointer-events: none;
}

.particle-toggle {
  position: absolute;
  right: 14px;
  top: 16px;
  z-index: 730;
  display: inline-flex;
  align-items: center;
  gap: 7px;
  padding: 7px 10px;
  border: 1px solid rgba(83, 187, 255, .34);
  border-radius: 3px;
  background: rgba(4, 25, 51, .86);
  box-shadow: inset 0 0 15px rgba(41, 157, 235, .08), 0 5px 16px rgba(0, 7, 20, .3);
  color: rgba(201, 231, 249, .78);
  font-size: 11px;
  cursor: pointer;
  backdrop-filter: blur(6px);

  &:hover,
  &.particle-toggle--active {
    border-color: rgba(80, 221, 255, .68);
    color: #e7fbff;
  }
}

.particle-toggle__switch {
  position: relative;
  width: 28px;
  height: 14px;
  border-radius: 8px;
  background: rgba(88, 115, 141, .56);
  box-shadow: inset 0 0 5px rgba(0, 0, 0, .55);

  i {
    position: absolute;
    left: 2px;
    top: 2px;
    width: 10px;
    height: 10px;
    border-radius: 50%;
    background: #b7c8d6;
    transition: transform .2s ease, background .2s ease, box-shadow .2s ease;
  }
}

.particle-toggle--active .particle-toggle__switch {
  background: rgba(17, 169, 226, .74);

  i {
    transform: translateX(14px);
    background: #ecfeff;
    box-shadow: 0 0 8px #63ecff;
  }
}

.map-attribution {
  position: absolute;
  right: 12px;
  bottom: 7px;
  z-index: 725;
  padding: 2px 6px;
  border-radius: 2px;
  background: rgba(2, 18, 37, .66);
  color: rgba(169, 204, 226, .62);
  font-size: 9px;
  line-height: 1.35;

  a {
    color: rgba(126, 213, 255, .78);
    text-decoration: none;
  }
}

.flow-hud {
  position: absolute;
  left: 22px;
  top: 22px;
  z-index: 720;
  width: 224px;
  padding: 14px 16px 13px;
  pointer-events: none;
  overflow: hidden;
  border: 1px solid rgba(92, 216, 255, .28);
  border-left: 3px solid #52d9ff;
  background: linear-gradient(115deg, rgba(5, 36, 64, .9), rgba(5, 24, 44, .62));
  box-shadow: inset 0 0 24px rgba(30, 168, 255, .08), 0 8px 28px rgba(0, 7, 18, .28);
  backdrop-filter: blur(8px);

  &::after {
    content: '';
    position: absolute;
    right: -24px;
    top: -32px;
    width: 90px;
    height: 90px;
    border: 1px solid rgba(82, 217, 255, .2);
    border-radius: 50%;
    box-shadow: 0 0 0 12px rgba(82, 217, 255, .025), 0 0 0 24px rgba(82, 217, 255, .018);
  }
}

.flow-hud__eyebrow {
  display: flex;
  align-items: center;
  gap: 7px;
  color: #6ee7ff;
  font-size: 10px;
  letter-spacing: 2px;
}

.flow-hud__pulse {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #6fffe9;
  box-shadow: 0 0 0 0 rgba(111, 255, 233, .62);
  animation: hudPulse 1.8s ease-out infinite;
}

.flow-hud__title {
  margin-top: 5px;
  color: #f2fbff;
  font-size: 19px;
  font-weight: 700;
  letter-spacing: 1px;
  text-shadow: 0 0 14px rgba(93, 218, 255, .35);
}

.flow-hud__routes {
  display: grid;
  grid-template-columns: 1fr;
  gap: 5px;
  margin-top: 10px;
  color: rgba(202, 235, 255, .74);
  font-size: 11px;

  span {
    display: flex;
    align-items: center;
    gap: 7px;
  }

  i {
    position: relative;
    width: 24px;
    height: 2px;
    overflow: hidden;
    background: currentColor;
    box-shadow: 0 0 7px currentColor;

    &::after {
      content: '';
      position: absolute;
      top: 0;
      left: -10px;
      width: 8px;
      height: 100%;
      background: #fff;
      animation: routeGlint 1.7s linear infinite;
    }

    &.jialing { color: #42cfff; }
    &.yangtze { color: #ffad35; }
  }
}

.flow-status {
  position: absolute;
  left: 22px;
  bottom: 24px;
  z-index: 720;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border: 1px solid rgba(70, 202, 255, .2);
  border-radius: 3px;
  background: rgba(4, 26, 48, .76);
  color: #5ee5ff;
  font-size: 9px;
  letter-spacing: 1.4px;
  pointer-events: none;
  backdrop-filter: blur(6px);

  b {
    color: rgba(215, 242, 255, .78);
    font-size: 11px;
    font-weight: 500;
    letter-spacing: 0;
  }
}

.flow-status__wave {
  display: flex;
  align-items: center;
  gap: 2px;
  height: 14px;

  i {
    width: 2px;
    height: 5px;
    background: #5ee5ff;
    box-shadow: 0 0 6px #5ee5ff;
    animation: flowEqualizer .9s ease-in-out infinite alternate;
  }

  i:nth-child(2) { animation-delay: -.6s; }
  i:nth-child(3) { animation-delay: -.3s; }
  i:nth-child(4) { animation-delay: -.75s; }
}

@keyframes riverCurrent {
  to { stroke-dashoffset: -76; }
}

@keyframes riverSpark {
  to { stroke-dashoffset: -92; }
}

@keyframes riverGlow {
  0%, 100% { opacity: .07; stroke-width: 28px; }
  50% { opacity: .14; stroke-width: 35px; }
}

@keyframes markerPulse {
  0%, 100% { filter: drop-shadow(0 0 3px rgba(255, 255, 255, .45)); }
  50% { filter: drop-shadow(0 0 10px rgba(85, 224, 255, .95)); }
}

@keyframes hudPulse {
  70% { box-shadow: 0 0 0 8px rgba(111, 255, 233, 0); }
  100% { box-shadow: 0 0 0 0 rgba(111, 255, 233, 0); }
}

@keyframes routeGlint {
  to { transform: translateX(38px); }
}

@keyframes flowEqualizer {
  to { height: 14px; opacity: .45; }
}

:deep(.river-glow) {
  animation: riverGlow 3.2s ease-in-out infinite;
  filter: drop-shadow(0 0 13px currentColor);
}

:deep(.river-bank) {
  stroke-linecap: round;
  filter: drop-shadow(0 0 8px currentColor);
}

:deep(.river-body) {
  stroke-linecap: round;
  filter: drop-shadow(0 0 4px currentColor);
}

:deep(.river-current) {
  stroke-linecap: round;
  stroke-dasharray: 22 9 5 12;
  animation: riverCurrent 2.15s linear infinite;
  filter: drop-shadow(0 0 4px currentColor);
}

:deep(.river-current--yangtze) {
  animation-duration: 2.55s;
}

:deep(.river-spark) {
  stroke-linecap: round;
  stroke-dasharray: 1 28;
  animation: riverSpark 1.65s linear infinite;
  filter: drop-shadow(0 0 6px #fff);
}

:deep(.river-spark--yangtze) {
  animation-duration: 1.95s;
}

:deep(.station-marker) {
  animation: markerPulse 2.4s ease-in-out infinite;
}

:deep(.station-marker--warning) {
  animation-duration: 1.25s;
}

:deep(.map-overlay-icon) {
  z-index: 1000 !important;
}
:deep(.map-overlay-card) {
  background: rgba(255,255,255,.96);
  padding: 10px 14px;
  border-radius: 10px;
  box-shadow: 0 6px 16px rgba(0,0,0,.4);
  min-width: 175px;
  white-space: nowrap;

  .ol-level { font-size: 26px; font-weight: 700; line-height: 1.1; }
  .ol-kv { font-size: 12px; color: #555; margin-top: 3px; }
}

.sidebar {
  width: 360px;
  background: linear-gradient(180deg, rgba(8, 38, 78, .96) 0%, rgba(5, 24, 52, .98) 100%);
  border-left: 1px solid rgba(66, 173, 255, .28);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  color: #d9efff;
  box-shadow: inset 1px 0 0 rgba(255, 255, 255, .06);
}

.sidebar-scroll {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  padding: 12px;

  &::-webkit-scrollbar {
    width: 6px;
  }

  &::-webkit-scrollbar-thumb {
    border-radius: 4px;
    background: rgba(83, 189, 255, .45);
  }

  &::-webkit-scrollbar-track {
    background: rgba(10, 45, 86, .45);
  }
}

.section-title {
  width: 100%;
  height: 38px;
  padding: 0 12px;
  border: 1px solid rgba(69, 200, 255, .3);
  border-radius: 4px;
  background: linear-gradient(90deg, rgba(18, 93, 168, .82), rgba(8, 45, 91, .58));
  color: #eaf7ff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 15px;
  font-weight: 700;
  box-shadow: inset 0 0 18px rgba(60, 177, 255, .12);

  + .station-list,
  + .river-tabs,
  + .result {
    margin-top: 8px;
  }
}

.station-list {
  height: 326px;
  overflow-y: auto;
  padding: 2px 0 10px;

  &::-webkit-scrollbar {
    width: 6px;
  }

  &::-webkit-scrollbar-thumb {
    border-radius: 4px;
    background: rgba(83, 189, 255, .45);
  }

  &::-webkit-scrollbar-track {
    background: rgba(10, 45, 86, .45);
  }
}

.river-tabs {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}

.river-tab {
  min-width: 72px;
  height: 29px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0 12px;
  border: 1px solid rgba(74, 189, 255, .38);
  border-radius: 4px;
  background: linear-gradient(180deg, rgba(27, 120, 205, .92), rgba(12, 76, 145, .92));
  box-shadow: inset 0 0 12px rgba(91, 211, 255, .12), 0 0 8px rgba(14, 111, 202, .16);
  font-family: inherit;
  font-size: 13px;
  font-weight: 700;
  line-height: 1;
  white-space: nowrap;
  color: #fff;
}

.river-tab--jialing.is-active {
  border-color: rgba(83, 205, 255, .76);
  background: linear-gradient(180deg, #2196f3, #1377c7);
}

.river-tab--yangtze {
  color: #ffb13b;
  text-shadow: 0 0 7px rgba(255, 152, 0, .5);
  cursor: default;
}

.station-loading {
  min-height: 72px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: rgba(184, 217, 255, .64);
  font-size: 12px;
}

.station-row {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 8px;
  border-radius: 4px;
  margin-bottom: 4px;
  background: rgba(10, 49, 96, .72);
  border: 1px solid rgba(82, 169, 255, .18);

  .dot { width: 10px; height: 10px; border-radius: 50%; flex-shrink: 0; }
  .name { width: 88px; font-size: 13px; font-weight: 600; flex-shrink: 0; color: #f2fbff; }
  .info { flex: 1; font-size: 11px; color: rgba(184, 217, 255, .72); line-height: 1.3; }

  input {
    width: 74px;
    padding: 5px 6px;
    border: 1px solid rgba(92, 188, 255, .38);
    border-radius: 4px;
    font-size: 13px;
    text-align: right;
    color: #eaf7ff;
    background: rgba(4, 20, 45, .78);

    &:focus {
      outline: none;
      border-color: #5ad5ff;
      box-shadow: 0 0 0 2px rgba(69, 200, 255, .14);
    }
  }

  .no-input { width: 74px; text-align: center; color: rgba(184, 217, 255, .46); }

  &.no-threshold { opacity: .65; }
  input:disabled { opacity: .4; cursor: not-allowed; }
}

.caojie-section {
  border-top: 1px solid rgba(69, 200, 255, 0.12);
  padding: 8px 12px;
  background: rgba(38, 166, 154, 0.08);

  h2 {
    font-size: 14px;
    color: #4DB6AC;
    margin: 0 0 6px;
    padding: 0;
    border: none;
    background: none;
  }

  .caojie-row {
    display: flex;
    align-items: center;
    gap: 8px;

    .name { width: 88px; font-size: 13px; font-weight: 500; flex-shrink: 0; color: #d2ecff; }

    input {
      flex: 1;
      padding: 6px 8px;
      border: 1px solid rgba(77, 182, 172, 0.3);
      border-radius: 4px;
      font-size: 13px;
      background: rgba(7, 28, 51, 0.6);
      color: #4DB6AC;

      &:focus { outline: none; border-color: #26A69A; }
      &::placeholder { color: rgba(184, 217, 255, 0.3); }
    }
  }
}

.rainfall-section {
  border-top: 1px solid rgba(69, 200, 255, 0.12);
  padding: 8px 12px;
  background: rgba(255, 152, 0, 0.06);

  h2 {
    font-size: 14px;
    color: #FFB74D;
    margin: 0 0 6px;
    padding: 0;
    border: none;
    background: none;
  }

  .rainfall-row {
    display: flex;
    align-items: center;
    gap: 8px;

    .name { width: 88px; font-size: 13px; font-weight: 500; flex-shrink: 0; color: #d2ecff; }

    input {
      flex: 1;
      padding: 6px 8px;
      border: 1px solid rgba(255, 183, 77, 0.3);
      border-radius: 4px;
      font-size: 13px;
      background: rgba(7, 28, 51, 0.6);
      color: #FFB74D;

      &:focus { outline: none; border-color: #FF9800; }
      &::placeholder { color: rgba(184, 217, 255, 0.3); }
    }
  }
}

.actions {
  padding: 10px 0 12px;
  border-top: 1px solid rgba(69, 200, 255, .18);
  display: flex;
  flex-wrap: wrap;
  gap: 8px;

  .simulate-options {
    width: 100%;
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 8px;

    label {
      display: flex;
      flex-direction: column;
      gap: 4px;
      font-size: 12px;
      color: rgba(184, 217, 255, .82);
    }

    input {
      width: 100%;
      padding: 7px 8px;
      border: 1px solid rgba(92, 188, 255, .38);
      border-radius: 4px;
      font-size: 13px;
      color: #eaf7ff;
      background: rgba(4, 20, 45, .78);

      &:focus {
        outline: none;
        border-color: #5ad5ff;
        box-shadow: 0 0 0 2px rgba(69, 200, 255, .14);
      }
    }
  }

  button {
    flex: 1;
    min-width: 72px;
    padding: 9px 0;
    border: none;
    border-radius: 4px;
    font-size: 14px;
    font-weight: 600;
    cursor: pointer;
    color: #fff;
    transition: .15s;
  }

  button:disabled {
    opacity: .65;
    cursor: not-allowed;
  }

  .btn-calc { background: #E53935; &:hover { filter: brightness(1.08); } }
  .btn-clear { background: #607D8B; }
}

.result {
  padding: 10px 0 12px;
  background: transparent;

  .level-big {
    --status-color: #69eaff;
    position: relative;
    isolation: isolate;
    min-height: 68px;
    margin-bottom: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;
    border: 1px solid color-mix(in srgb, var(--status-color) 58%, transparent);
    border-radius: 5px;
    color: var(--status-color);
    background:
      linear-gradient(90deg, transparent, color-mix(in srgb, var(--status-color) 13%, transparent), transparent),
      repeating-linear-gradient(135deg, rgba(67, 190, 255, .035) 0 7px, transparent 7px 14px),
      rgba(4, 25, 53, .72);
    box-shadow:
      inset 0 0 22px color-mix(in srgb, var(--status-color) 11%, transparent),
      0 0 13px color-mix(in srgb, var(--status-color) 15%, transparent);

    &::before {
      content: '';
      position: absolute;
      z-index: -1;
      top: -35%;
      bottom: -35%;
      left: -42%;
      width: 34%;
      transform: skewX(-20deg);
      background: linear-gradient(90deg, transparent, rgba(255, 255, 255, .32), transparent);
      animation: warningStatusSweep 2.7s ease-in-out infinite;
    }

    &::after {
      content: '';
      position: absolute;
      inset: 5px;
      z-index: -1;
      border: 1px solid color-mix(in srgb, var(--status-color) 21%, transparent);
      clip-path: polygon(0 0, 24px 0, 24px 1px, calc(100% - 24px) 1px, calc(100% - 24px) 0, 100% 0, 100% 100%, calc(100% - 24px) 100%, calc(100% - 24px) calc(100% - 1px), 24px calc(100% - 1px), 24px 100%, 0 100%);
      opacity: .9;
    }
  }

  .level-big__label {
    position: relative;
    z-index: 2;
    font-size: 28px;
    font-weight: 800;
    letter-spacing: 3px;
    text-align: center;
    text-shadow: 0 0 8px currentColor, 0 0 18px color-mix(in srgb, currentColor 55%, transparent);
  }

  .level-big__pulse {
    position: absolute;
    z-index: 1;
    left: 50%;
    top: 50%;
    width: 30px;
    height: 30px;
    margin: -15px 0 0 -15px;
    border: 1px solid currentColor;
    border-radius: 50%;
    opacity: 0;
    box-shadow: 0 0 12px currentColor;
  }

  .level-big__signal {
    position: absolute;
    right: 14px;
    bottom: 12px;
    z-index: 2;
    height: 18px;
    display: flex;
    align-items: flex-end;
    gap: 3px;

    i {
      display: block;
      width: 3px;
      height: 5px;
      border-radius: 2px 2px 0 0;
      background: currentColor;
      box-shadow: 0 0 6px currentColor;
      animation: warningSignal 1s ease-in-out infinite alternate;
    }

    i:nth-child(2) { animation-delay: -.75s; }
    i:nth-child(3) { animation-delay: -.5s; }
    i:nth-child(4) { animation-delay: -.25s; }
  }

  .level-big--safe {
    animation: warningSafeFrame 2.2s ease-in-out infinite;

    .level-big__label { animation: warningSafeText 2.2s ease-in-out infinite; }
    .level-big__pulse { animation: warningSafePulse 2.2s ease-out infinite; }
  }

  .level-big--warning {
    border-width: 2px;
    background:
      linear-gradient(90deg, transparent, color-mix(in srgb, var(--status-color) 24%, transparent), transparent),
      repeating-linear-gradient(135deg, color-mix(in srgb, var(--status-color) 7%, transparent) 0 7px, transparent 7px 14px),
      rgba(28, 12, 22, .86);
    animation: warningAlarmFrame .82s ease-in-out infinite;

    &::before { animation-duration: 1.05s; }
    .level-big__label { animation: warningAlarmText .82s ease-in-out infinite; }
    .level-big__pulse { animation: warningAlarmPulse 1.05s ease-out infinite; }
    .level-big__signal i { animation-duration: .55s; }
  }
  .kv {
    display: flex;
    justify-content: space-between;
    font-size: 13px;
    gap: 10px;
    padding: 6px 8px;
    margin-bottom: 4px;
    border-radius: 4px;
    border: 1px solid rgba(82, 169, 255, .14);
    background: rgba(9, 43, 88, .58);

    .k { color: rgba(184, 217, 255, .72); flex-shrink: 0; }
    .v { font-weight: 600; color: #f2fbff; text-align: right; }
  }
  .rules {
    margin-top: 8px;
    font-size: 12px;
    color: rgba(218, 239, 255, .88);
    padding: 8px;
    border-radius: 4px;
    background: rgba(9, 43, 88, .58);
    border: 1px solid rgba(82, 169, 255, .14);

    ul { margin: 4px 0 0; padding-left: 16px; }
    li { padding: 2px 0; }
  }
  .hint { font-size: 12px; color: rgba(184, 217, 255, .66); }
}

@keyframes warningStatusSweep {
  0% { left: -42%; opacity: 0; }
  18% { opacity: .8; }
  76%, 100% { left: 118%; opacity: 0; }
}

@keyframes warningSignal {
  to { height: 18px; opacity: .48; }
}

@keyframes warningSafeFrame {
  0%, 100% { box-shadow: inset 0 0 18px rgba(105, 234, 255, .08), 0 0 8px rgba(105, 234, 255, .12); }
  50% { box-shadow: inset 0 0 30px rgba(105, 234, 255, .2), 0 0 20px rgba(105, 234, 255, .32); }
}

@keyframes warningSafeText {
  0%, 100% { opacity: .78; transform: scale(1); text-shadow: 0 0 6px currentColor; }
  50% { opacity: 1; transform: scale(1.045); text-shadow: 0 0 10px currentColor, 0 0 22px currentColor; }
}

@keyframes warningSafePulse {
  0% { opacity: .7; transform: scale(.65); }
  78%, 100% { opacity: 0; transform: scale(8.5, 2.1); }
}

@keyframes warningAlarmFrame {
  0%, 100% { box-shadow: inset 0 0 22px color-mix(in srgb, var(--status-color) 17%, transparent), 0 0 9px color-mix(in srgb, var(--status-color) 28%, transparent); }
  50% { box-shadow: inset 0 0 42px color-mix(in srgb, var(--status-color) 34%, transparent), 0 0 27px color-mix(in srgb, var(--status-color) 68%, transparent); }
}

@keyframes warningAlarmText {
  0%, 100% { transform: scale(1); opacity: .86; }
  45% { transform: scale(1.075); opacity: 1; text-shadow: 0 0 12px currentColor, 0 0 30px currentColor; }
}

@keyframes warningAlarmPulse {
  0% { opacity: .92; transform: scale(.55); }
  75%, 100% { opacity: 0; transform: scale(9.2, 2.4); }
}

.chart-box {
  padding: 12px 0;
  border-top: 1px solid rgba(69, 200, 255, .18);
  background: transparent;

  h3 { font-size: 13px; margin: 0 0 6px; color: #d9efff; }
}

.chart-canvas {
  position: relative;
  height: 200px;
}

.chart-placeholder {
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;

  .placeholder-text {
    font-size: 14px;
    color: rgba(184, 217, 255, 0.3);
  }
}

.warning-panel {
  height: 200px;
  display: flex;
  flex-direction: column;
  background: rgba(43, 123, 189, 0.08);
  border: 1px solid rgba(69, 200, 255, 0.1);
  border-radius: 4px;
  padding: 12px 16px;

  .wp-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 10px;

    .wp-level {
      font-size: 15px;
      font-weight: 700;
    }

    .wp-station {
      font-size: 18px;
      font-weight: 600;
    }
  }

  .wp-body {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: center;
    gap: 8px;

    p {
      margin: 0;
      font-size: 14px;
      line-height: 1.5;
    }
  }
}

.hint {
  font-size: 12px;
  color: rgba(184, 217, 255, .62);
  padding: 8px 0 2px;
  border-top: 1px solid rgba(69, 200, 255, .18);
}

.legend {
  position: absolute;
  right: 12px;
  bottom: 28px;
  background: rgba(7, 28, 51, 0.85);
  padding: 8px 12px;
  border-radius: 4px;
  border: 1px solid rgba(69, 200, 255, 0.15);
  box-shadow: 0 2px 8px rgba(0,0,0,.3);
  font-size: 12px;
  color: #b8d9ff;
  z-index: 720;
  line-height: 1.7;

  .lg { display: flex; align-items: center; gap: 6px; }
  .sw { width: 14px; height: 3px; display: inline-block; }
}

@media (prefers-reduced-motion: reduce) {
  :deep(.river-glow),
  :deep(.river-current),
  :deep(.river-spark),
  :deep(.station-marker),
  .flow-hud__pulse,
  .flow-hud__routes i::after,
  .flow-status__wave i,
  .level-big,
  .level-big::before,
  .level-big__label,
  .level-big__pulse,
  .level-big__signal i {
    animation: none !important;
  }

  .flow-canvas { display: none; }
}

:deep(.waterlog-station-label) {
  width: 0 !important;
  height: 0 !important;
  margin: 0 !important;
  background: transparent;
  border: 0;
  pointer-events: none;
}

:deep(.waterlog-station-label__text) {
  position: absolute;
  left: 10px;
  top: -10px;
  display: inline-flex;
  align-items: center;
  height: 20px;
  padding: 0 6px;
  border-radius: 3px;
  background: rgba(255, 255, 255, .92);
  box-shadow: 0 1px 4px rgba(0, 0, 0, .25);
  color: #333;
  font-size: 12px;
  font-weight: 600;
  line-height: 20px;
  white-space: nowrap;
  transform: translateZ(0);
}

:deep(.leaflet-popup-content) {
  font-size: 13px;
  color: #333;
}

:deep(.leaflet-popup-content-wrapper) {
  background: rgba(255, 255, 255, 0.96);
  border-radius: 6px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.35);
}

/* 隐藏 number input 的 spinner 按钮 */
input[type="number"]::-webkit-inner-spin-button,
input[type="number"]::-webkit-outer-spin-button {
  -webkit-appearance: none;
  margin: 0;
}
input[type="number"] {
  -moz-appearance: textfield;
}

.event-shield {
  position: absolute;
  inset: 0;
  z-index: 10000;
  background: transparent;
  pointer-events: auto;
}
</style>

<!-- Leaflet 的项目内最小样式：避免线上 CDN，也避开工程 images 别名对官方 CSS 的 URL 解析冲突。 -->
<style lang="scss">
.waterlog-prediction-dia {
  .leaflet-pane,
  .leaflet-tile,
  .leaflet-marker-icon,
  .leaflet-pane > svg,
  .leaflet-pane > canvas,
  .leaflet-zoom-box,
  .leaflet-layer {
    position: absolute;
    left: 0;
    top: 0;
  }

  .leaflet-container {
    overflow: hidden;
    outline: 0;
    -webkit-tap-highlight-color: transparent;
    font-family: 'AlibabaPuHuiTi', sans-serif;
  }

  .leaflet-container .leaflet-overlay-pane svg {
    max-width: none !important;
    max-height: none !important;
  }

  .leaflet-tile {
    width: 256px;
    height: 256px;
    max-width: none !important;
    max-height: none !important;
    user-select: none;
    -webkit-user-drag: none;
  }

  .leaflet-layer.waterlog-offline-tile .leaflet-tile {
    opacity: .7;
    filter: invert(.82) sepia(.74) saturate(2.35) hue-rotate(158deg) brightness(.66) contrast(1.1);
  }

  .leaflet-pane { z-index: 400; }
  .leaflet-tile-pane { z-index: 200; }
  .leaflet-overlay-pane { z-index: 400; }
  .leaflet-shadow-pane { z-index: 500; }
  .leaflet-marker-pane { z-index: 600; }
  .leaflet-tooltip-pane { z-index: 650; }
  .leaflet-popup-pane { z-index: 700; }
  .leaflet-map-pane canvas { z-index: 100; }
  .leaflet-map-pane svg { z-index: 200; }

  .leaflet-zoom-animated { transform-origin: 0 0; }
  svg.leaflet-zoom-animated { will-change: transform; }
  .leaflet-container.leaflet-zoom-anim .leaflet-zoom-animated {
    transition: transform .25s cubic-bezier(0, 0, .25, 1);
  }

  .leaflet-marker-icon,
  .leaflet-pane > svg path { pointer-events: none; }
  .leaflet-marker-icon.leaflet-interactive,
  .leaflet-pane > svg path.leaflet-interactive { pointer-events: auto; }
  .leaflet-interactive { cursor: pointer; }
  .leaflet-grab { cursor: grab; }
  .leaflet-container.leaflet-dragging .leaflet-grab { cursor: grabbing; }

  .leaflet-control {
    position: relative;
    z-index: 800;
    float: left;
    clear: both;
    pointer-events: auto;
  }

  .leaflet-top,
  .leaflet-bottom {
    position: absolute;
    z-index: 1000;
    pointer-events: none;
  }

  .leaflet-top { top: 0; }
  .leaflet-bottom { bottom: 0; }
  .leaflet-left { left: 0; }
  .leaflet-right { right: 0; }
  .leaflet-top .leaflet-control { margin-top: 10px; }
  .leaflet-bottom .leaflet-control { margin-bottom: 10px; }
  .leaflet-left .leaflet-control { margin-left: 10px; }
  .leaflet-right .leaflet-control { margin-right: 10px; float: right; }

  .leaflet-bar {
    overflow: hidden;
    border: 1px solid rgba(86, 207, 255, .32);
    border-radius: 3px;
    box-shadow: 0 4px 14px rgba(0, 5, 14, .4);
  }

  .leaflet-bar a {
    display: block;
    width: 30px;
    height: 30px;
    border-bottom: 1px solid rgba(86, 207, 255, .24);
    background: rgba(5, 34, 59, .9);
    color: #a9ecff;
    font: bold 20px/30px 'Lucida Console', Monaco, monospace;
    text-align: center;
    text-decoration: none;
  }

  .leaflet-bar a:last-child { border-bottom: 0; }
  .leaflet-bar a:hover,
  .leaflet-bar a:focus { background: rgba(16, 83, 126, .95); color: #fff; }
  .leaflet-bar a.leaflet-disabled { cursor: default; opacity: .38; }

  .leaflet-fade-anim .leaflet-popup { opacity: 0; transition: opacity .2s linear; }
  .leaflet-fade-anim .leaflet-map-pane .leaflet-popup { opacity: 1; }
  .leaflet-popup { position: absolute; margin-bottom: 20px; text-align: center; }
  .leaflet-popup-content-wrapper { padding: 1px; text-align: left; }
  .leaflet-popup-content { margin: 13px 24px 13px 20px; line-height: 1.35; }
  .leaflet-popup-tip-container {
    position: absolute;
    left: 50%;
    width: 40px;
    height: 20px;
    margin-top: -1px;
    margin-left: -20px;
    overflow: hidden;
    pointer-events: none;
  }
  .leaflet-popup-tip {
    width: 17px;
    height: 17px;
    margin: -10px auto 0;
    padding: 1px;
    transform: rotate(45deg);
  }
  .leaflet-popup-content-wrapper,
  .leaflet-popup-tip { background: #fff; color: #333; box-shadow: 0 3px 14px rgba(0, 0, 0, .4); }
  .leaflet-container a.leaflet-popup-close-button {
    position: absolute;
    top: 0;
    right: 0;
    width: 24px;
    height: 24px;
    border: 0;
    background: transparent;
    color: #757575;
    font: 16px/24px Tahoma, Verdana, sans-serif;
    text-align: center;
    text-decoration: none;
  }
}
</style>
