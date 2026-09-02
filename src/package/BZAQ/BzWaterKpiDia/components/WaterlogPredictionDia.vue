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
        <div class="station-list">
          <template v-for="group in stationGroups" :key="group.title">
            <div class="river-group">
              <span class="river-title" :class="group.cls">{{ group.title }}</span>
            </div>
            <div
              v-for="s in group.items"
              :key="s.name"
              class="station-row"
              :class="{ 'no-threshold': s.warning === null || s.warning === undefined }"
            >
              <span class="dot" :style="{ background: markerColor(s) }"></span>
              <span class="name">{{ s.name }}</span>
              <span class="info">
                警戒 {{ fmtThr(s.warning) }} / 保证 {{ fmtThr(s.guarantee) }}<br>{{ s.dist }} km
              </span>
              <input
                v-model.number="levels[s.name]"
                :disabled="!isTrigger(s)"
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
          <button class="btn-clear" :disabled="stationLoading" @click="resetByLatestStationInfo">
            {{ stationLoading ? '刷新中...' : '重置' }}
          </button>
        </div>

        <div class="section-title">
          <span>告警结果</span>
        </div>
        <div class="result">
          <div v-if="!result" class="hint" style="padding:0;">
            输入某站水位后点击「计算预警」。<br>仅可输入一个站点，其他站点将自动锁定。
          </div>
          <template v-else>
            <div class="level-big" :style="{ color: resultColor }">
              {{ result.hasWarning ? result.levelName + '预警' : '当前无预警' }}
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
          注：化龙桥/大溪沟/千厮门/郭家沱/塔坪/钓二嘴 警戒与保证水位在原表中为「/」（无数据），地图以灰色标记，暂不参与触发。
        </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref, computed, nextTick } from 'vue'
import axios from 'axios'
import L from 'leaflet'
import * as echarts from 'echarts/core'
import { LineChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, LegendComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import { STATIONS, RIVERS, COLORS, LEVEL_COLOR } from './flood-data'
import type { Station } from './flood-data'
import { evaluateWarning, fmtHours } from './flood-engine'
import type { WarningResult } from './flood-engine'

echarts.use([LineChart, GridComponent, TooltipComponent, LegendComponent, CanvasRenderer])

const FLOOD_API = 'http://23.99.16.179:11001/api/boot/system/flood-engine'

const emit = defineEmits(['close'])

const BASE_URL = 'http://23.99.16.179:11001'
const STATION_LIST_API = `${BASE_URL}/api/boot/system/flood-engine/station/list`
const SIMULATE_API = `${BASE_URL}/api/boot/system/flood-engine/simulate/count`

const visible = ref(true)
const isClosing = ref(false)
let closeTimer: ReturnType<typeof setTimeout> | null = null

// ---- 地图 ----
const mapRef = ref<HTMLElement | null>(null)
let map: L.Map | null = null
const markers: Record<string, L.CircleMarker> = {}
const labelMarkers: Record<string, L.Marker> = {}
let warningPathLayers: L.Polyline[] = []
let pathAnimTimer: ReturnType<typeof setInterval> | null = null
let overlayMarker: L.Marker | null = null

// ---- 图表 ----
const chartRef = ref<HTMLElement | null>(null)
let waterChart: echarts.ECharts | null = null

// ---- 数据 ----
const stationList = ref<Station[]>(STATIONS.map(station => ({ ...station })))
const levels = ref<Record<string, number>>({})
const result = ref<WarningResult | null>(null)
const caojieFlow = ref<number | null>(null)
const rainCount = ref<number | null>(null)
const caojieDischarge = caojieFlow
const rainfall = rainCount
const simulateRise = ref<number | null>(null)
const simulateStationName = ref('')
const calculating = ref(false)
const stationLoading = ref(false)
const stationRealZ = ref<Record<string, number>>({})
const lockedStation = ref('')
let isComponentAlive = false

const resultColor = computed(() => {
  if (!result.value) return '#9E9E9E'
  return LEVEL_COLOR[result.value.levelName] || '#9E9E9E'
})

const chaotianmenLevel = computed(() => {
  const station = stationList.value.find(s => s.name === '朝天门')
  const level = station ? normalizeNumber(levels.value[station.name] ?? station.z) : null
  return level === null ? '-' : `${level.toFixed(2)} m`
})

const stationGroups = computed(() => [
  {
    title: '嘉陵江',
    cls: 'jialing',
    items: stationList.value.filter(s => s.river === '嘉陵江')
  },
  // {
  //   title: '长江',
  //   cls: 'yangtze',
  //   items: stationList.value.filter(s => s.river === '长江')
  // }
])

function isTrigger(s: Station): boolean {
  return s.name !== '朝天门' && s.name !== '郭家沱'
}

function markerColor(s: Station): string {
  if (s.name === '朝天门') return '#E53935'
  if (s.name === '郭家沱') return '#9E9E9E'
  if (s.warning === null || s.warning === undefined) return '#9E9E9E'
  return COLORS[s.river] || '#9E9E9E'
}

function fmtThr(v: number | null | undefined): string {
  return (v === null || v === undefined) ? '—' : String(v)
}

// ---- 接口：获取水文站点列表 ----
async function fetchStations() {
  try {
    const res = await axios.get(`${FLOOD_API}/station/list`)
    const list = res?.data?.data
    if (!Array.isArray(list)) return

    const zMap: Record<string, number> = {}
    list.forEach((item: any) => {
      if (item.name && item.z != null) {
        zMap[item.name] = item.z
      }
    })
    stationRealZ.value = zMap

    // 将实时水位填入输入框（作为初始值）
    STATIONS.forEach(s => {
      if (isTrigger(s) && zMap[s.name] != null) {
        levels.value[s.name] = zMap[s.name]
      }
    })
    updateChart()
  } catch (e) {
    console.error('[WaterlogPredictionDia] 获取站点列表失败:', e)
  }
}

// ---- 单站点输入锁定逻辑 ----
// 判断某个站点的输入框是否可编辑
function isInputDisabled(stationName: string): boolean {
  if (!lockedStation.value) return false
  return lockedStation.value !== stationName
}

// 输入时检查锁定状态
function onStationInput(stationName: string) {
  const v = levels.value[stationName] || 0
  if (v !== 0) {
    // 有值了，锁定其他站点
    lockedStation.value = stationName
  } else {
    // 清零了，解除锁定
    lockedStation.value = ''
  }
  updateChart()
}

// ---- 初始化 ----
function normalizeNumber(value: any): number | null {
  if (value === '' || value === null || value === undefined) return null
  const num = Number(value)
  return Number.isFinite(num) ? num : null
}

function normalizeThreshold(value: any): number | null {
  const num = normalizeNumber(value)
  return num === null || num < 0 ? null : num
}

function normalizeOptionalNumber(value: any): number | null {
  const num = normalizeNumber(value)
  return num === null ? null : num
}

function normalizeLevelName(level: any): string {
  const levelMap: Record<string, string> = {
    NONE: '无',
    BLUE: '蓝色',
    YELLOW: '黄色',
    RED: '红色'
  }
  const key = String(level || '').toUpperCase()
  return levelMap[key] || String(level || '无')
}

function normalizeServerWarningResult(serverResult: any): WarningResult {
  const levelName = normalizeLevelName(serverResult?.level ?? serverResult?.levelName)
  return {
    hasWarning: Boolean(serverResult?.hasWarning),
    level: levelName === '红色' ? 3 : levelName === '黄色' ? 2 : levelName === '蓝色' ? 1 : 0,
    levelName,
    velocity: normalizeNumber(serverResult?.velocity) ?? 0,
    nearestStation: String(serverResult?.nearestStation || ''),
    nearestDistanceKm: normalizeNumber(serverResult?.nearestDistanceKm) ?? 0,
    arrivalHours: normalizeNumber(serverResult?.arrivalHours) ?? 0,
    warningHours: normalizeNumber(serverResult?.warningHours) ?? 0,
    warningImmediate: Boolean(serverResult?.warningImmediate),
    reachedWarningStations: Array.isArray(serverResult?.reachedWarningStations) ? serverResult.reachedWarningStations : [],
    matchedRules: Array.isArray(serverResult?.matchedRules) ? serverResult.matchedRules : [],
    arrivalTime: fmtHours(normalizeNumber(serverResult?.arrivalHours) ?? 0),
    warningTime: serverResult?.warningImmediate ? '已不足1小时(立即)' : fmtHours(normalizeNumber(serverResult?.warningHours) ?? 0)
  }
}

function getResultRank(warningResult: WarningResult) {
  return typeof warningResult.level === 'number' ? warningResult.level : 0
}

function pickBetterSimulation(current: any | null, next: any) {
  if (!current) return next
  const currentResult = current.warningResult as WarningResult
  const nextResult = next.warningResult as WarningResult
  const currentRank = getResultRank(currentResult)
  const nextRank = getResultRank(nextResult)
  if (nextRank !== currentRank) return nextRank > currentRank ? next : current
  if (nextResult.hasWarning && currentResult.hasWarning) {
    return nextResult.arrivalHours < currentResult.arrivalHours ? next : current
  }
  return Math.abs(next.chaotianmenRise || 0) > Math.abs(current.chaotianmenRise || 0) ? next : current
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

function applyStationRows(rows: any[]) {
  const defaultStationMap = new Map(STATIONS.map(station => [station.name, station]))
  const nextStations: Station[] = []
  const usedNames = new Set<string>()

  rows.forEach(row => {
    const name = String(row?.name || '').trim()
    if (!name) return

    const fallback = defaultStationMap.get(name)
    const lon = normalizeNumber(row?.lon) ?? fallback?.lon
    const lat = normalizeNumber(row?.lat) ?? fallback?.lat
    const dist = normalizeNumber(row?.distToChaotianmen ?? row?.dist) ?? fallback?.dist ?? 0
    if (!Number.isFinite(lon) || !Number.isFinite(lat)) return

    const station: Station = {
      name,
      river: String(row?.river || fallback?.river || ''),
      lon: lon as number,
      lat: lat as number,
      dist,
      warning: normalizeThreshold(row?.warning ?? fallback?.warning),
      guarantee: normalizeThreshold(row?.guarantee ?? fallback?.guarantee),
      z: normalizeOptionalNumber(row?.z ?? fallback?.z)
    }

    nextStations.push(station)
    usedNames.add(name)

    const realLevel = normalizeNumber(row?.z)
    levels.value[name] = realLevel ?? levels.value[name] ?? 0
  })

  STATIONS.forEach(station => {
    if (usedNames.has(station.name)) return
    nextStations.push({ ...station })
  })

  if (nextStations.length) stationList.value = nextStations
}

async function fetchStationList() {
  if (stationLoading.value) return
  stationLoading.value = true

  try {
    const res = await axios.get(STATION_LIST_API)
    if (!isComponentAlive) return
    const rows = Array.isArray(res?.data?.data) ? res.data.data : []
    applyStationRows(rows)
    rebuildMap()
    updateChart()
    calc()
  } catch (error) {
    console.error('获取水文站点列表失败:', error)
  } finally {
    if (isComponentAlive) stationLoading.value = false
  }
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

async function simulateStation(station: Station) {
  const inputz = normalizeNumber(levels.value[station.name]) ?? 0
  const res = await axios.post(SIMULATE_API, {
    name: station.name,
    z: station.z ?? null,
    inputz,
    caojieFlow: normalizeOptionalNumber(caojieFlow.value),
    rainCount: normalizeOptionalNumber(rainCount.value)
  })
  const data = res?.data?.data || {}
  return {
    name: String(data?.name || station.name),
    chaotianmenRise: normalizeNumber(data?.chaotianmenRise) ?? 0,
    warningResult: normalizeServerWarningResult(data?.warningResult)
  }
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
  map = L.map(mapRef.value, { attributionControl: false }).setView([29.55, 106.35], 9)

  const jl = RIVERS.jialing.map(p => [p[1], p[0]] as [number, number])
  const yz = RIVERS.yangtze.map(p => [p[1], p[0]] as [number, number])

  L.polyline(jl, { color: '#2196F3', weight: 12, opacity: 0.12 }).addTo(map)
  L.polyline(jl, { color: '#2196F3', weight: 4, opacity: 0.9 }).addTo(map)
  L.polyline(jl, { color: '#e3f2fd', weight: 2, opacity: 0.85, className: 'river-flow' }).addTo(map)

  L.polyline(yz, { color: '#FF9800', weight: 12, opacity: 0.12 }).addTo(map)
  L.polyline(yz, { color: '#FF9800', weight: 4, opacity: 0.9 }).addTo(map)
  L.polyline(yz, { color: '#fff3e0', weight: 2, opacity: 0.85, className: 'river-flow' }).addTo(map)

  stationList.value.forEach(s => {
    const color = markerColor(s)
    const isBig = s.name === '朝天门'
    const m = L.circleMarker([s.lat, s.lon], {
      radius: isBig ? 11 : 7,
      color: '#fff',
      weight: 2,
      fillColor: color,
      fillOpacity: 0.95
    })
    m.bindPopup(popupHtml(s))
    m.addTo(map!)
    createStationLabel(s)
    markers[s.name] = m
  })

  const lats = stationList.value.map(s => s.lat)
  const lons = stationList.value.map(s => s.lon)
  map.fitBounds(
    [[Math.min(...lats), Math.min(...lons)], [Math.max(...lats), Math.max(...lons)]],
    { padding: [30, 30] }
  )
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
  simulateStationName.value = ''

  try {
    const triggerStations = stationList.value.filter(isTrigger)
    const simulationResults = await Promise.all(
      triggerStations.map(station =>
        simulateStation(station).catch(error => {
          console.error(`洪峰模拟计算失败: ${station.name}`, error)
          return null
        })
      )
    )
    if (!isComponentAlive) return

    const bestSimulation = simulationResults.reduce<any | null>((best, item) => {
      if (!item) return best
      return pickBetterSimulation(best, item)
    }, null)

    if (bestSimulation) {
      simulateRise.value = bestSimulation.chaotianmenRise
      simulateStationName.value = bestSimulation.name
      applyWarningResult(bestSimulation.warningResult, bestSimulation.name)
      return
    }

    applyWarningResult(getLocalWarningResult())
  } catch (error) {
    console.error('洪峰模拟计算失败:', error)
    applyWarningResult(getLocalWarningResult())
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

async function resetByLatestStationInfo() {
  caojieFlow.value = null
  rainCount.value = null
  simulateRise.value = null
  simulateStationName.value = ''
  result.value = null
  resetMarkers()
  drawWarningPath(null)
  hideMapOverlay()
  await fetchStationList()
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

  const glow = L.polyline([], { color: '#E53935', weight: 12, opacity: 0.18 }).addTo(map)
  const main = L.polyline([], { color: '#E53935', weight: 5, opacity: 0.95 }).addTo(map)
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
  updateChart()
}

// ---- Leaflet CSS 动态加载 & 图标修复 ----
let leafletCSSLink: HTMLLinkElement | null = null

function loadLeafletCSS() {
  // 检查是否已加载
  const existing = document.querySelector('link[href*="leaflet/dist/leaflet.css"]') as HTMLLinkElement | null
  if (existing) { leafletCSSLink = existing; return }
  leafletCSSLink = document.createElement('link')
  leafletCSSLink.rel = 'stylesheet'
  leafletCSSLink.href = 'https://unpkg.com/leaflet@1.9.4/dist/leaflet.css'
  document.head.appendChild(leafletCSSLink)
}

function fixLeafletIcons() {
  // 修复 Leaflet 默认 marker 图标路径
  delete (L.Icon.Default.prototype as any)._getIconUrl
  L.Icon.Default.mergeOptions({
    iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
    iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
    shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png'
  })
}

onMounted(() => {
  isComponentAlive = true
  // 初始化 levels
  stationList.value.forEach(s => {
    if (isTrigger(s)) levels.value[s.name] = 0
  })

  // 动态加载 Leaflet CSS（绕过 webpack css-loader 的 images 别名冲突）
  loadLeafletCSS()

  // 修复 Leaflet 默认图标路径
  fixLeafletIcons()

  nextTick(() => {
    initMap()
    initChart()
    fetchStationList()
  })

  // 获取水文站点实时数据
  fetchStations()

  // 暴露给 Leaflet popup 内的 onclick
  ;(window as any).applyFromPopup = applyFromPopup
})

onBeforeUnmount(() => {
  isComponentAlive = false
  if (closeTimer) clearTimeout(closeTimer)
  if (pathAnimTimer) clearInterval(pathAnimTimer)
  if (waterChart) { waterChart.dispose(); waterChart = null }
  if (map) { map.remove(); map = null }
  if (leafletCSSLink) { leafletCSSLink.remove(); leafletCSSLink = null }
  delete (window as any).applyFromPopup
})

defineExpose({ closePopup })
</script>

<script lang="ts">
export default {
  name: 'WaterlogPredictionDia',
  version: '2.0.0'
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
  width: 1700px;
  height: 1000px;
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
}

.map {
  position: absolute;
  inset: 0;
  background: radial-gradient(ellipse at 50% 38%, #14304a 0%, #0b1c2c 60%, #071019 100%);
}

@keyframes riverFlow { to { stroke-dashoffset: -20; } }
:deep(.river-flow) { stroke-dasharray: 7 13; animation: riverFlow 1.2s linear infinite; }

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
  + .result {
    margin-top: 8px;
  }
}

.station-list {
  height: 360px;
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

.river-group {
  margin-bottom: 6px;

  .river-title {
    font-size: 13px;
    font-weight: 600;
    color: #fff;
    padding: 4px 10px;
    border-radius: 4px;
    margin: 10px 0 6px;
    display: inline-block;

    &.jialing { background: #2196F3; }
    &.yangtze { background: #FF9800; }
  }
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

  .level-big { font-size: 30px; font-weight: 700; text-align: center; padding: 8px 0; }
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
  z-index: 500;
  line-height: 1.7;

  .lg { display: flex; align-items: center; gap: 6px; }
  .sw { width: 14px; height: 3px; display: inline-block; }
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
