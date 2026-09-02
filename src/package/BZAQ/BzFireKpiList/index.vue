<template>
  <div v-if="visible" class="fire-kpi-modal">
    <div class="modal-header">
      <div class="header-title">消防关键KPI</div>
      <button class="close-btn" @click="handleClose">×</button>
    </div>

    <div class="modal-content">
      <div class="stat-card">
        <div class="stat-icon">
          <img src="./img/icon_response_time.png" alt="火情统计" />
        </div>
        <div class="stat-info">
          <div class="stat-title">火情总数（2026）</div>
          <div class="stat-value">
            <span class="value-num">{{ totalFireCount }}</span>
            <span class="value-unit">起</span>
            <span class="value-trend down" v-if="Number(totalFireTrend) < 0">▼{{ Math.abs(totalFireTrend) }}%</span>
            <span class="value-trend up" v-else>▲{{ totalFireTrend }}%</span>
          </div>
        </div>
        <!-- <div class="stat-year">
          <n-select
            v-model:value="selectedYear"
            class="year-select"
            :options="yearOptions"
            :bordered="false"
            size="small"
          />
        </div> -->
      </div>

      <div class="table-container">
        <n-data-table
          class="kpi-table"
          :columns="columns"
          :data="tableData"
          :bordered="false"
          :flex-height="true"
          :single-line="false"
          size="small"
        />
      </div>

      <div class="total-footer">
        <div class="total-row">
          <div class="total-cell total-cell--index"></div>
          <div class="total-cell total-cell--town">合计</div>
          <div class="total-cell">
            {{ calcTotal.fireTotal }}
            <span class="trend down" v-if="Number(totalFireTrend) < 0">▼{{ Math.abs(totalFireTrend) }}%</span>
            <span class="trend up" v-else>▲{{ totalFireTrend }}%</span>
          </div>
          <div class="total-cell">
            {{ calcTotal.lossTotal }}
            <span class="trend down" v-if="Number(totalLossTrend) < 0">▼{{ Math.abs(totalLossTrend) }}%</span>
            <span class="trend up" v-else>▲{{ totalLossTrend }}%</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onBeforeUnmount, onMounted, watch, computed, h } from 'vue'
import { NDataTable, NSelect } from 'naive-ui'
import axios from 'axios'
import { BASE_URL } from './config'
import yzqGeoJson from './yzq-new.json'

const emit = defineEmits(['close'])
const props = defineProps({
  chartConfig: {
    type: Object,
    required: false,
    default: () => ({})
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

const sourceName = 'BzFireKpiList'
const OPEN_FIRE_KPI_EVENT = 'OPEN_FIRE_KPI_LIST_DIA'
const CLOSE_EVENT = 'CLOSE_DIA'
const visible = ref(false)

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

const selectedYear = ref(2026)
const totalFireCount = ref(0)
const totalFireTrend = ref(0)
const totalLossTrend = ref(0)
const tableData = ref([])
const firePolygonLayer: Record<string, any> = {}
let fireHoverHandler: any = null
let fireHoverLabel: any = null

const yearOptions = [
  { label: '2026', value: 2026 },
  { label: '2025', value: 2025 },
  { label: '2024', value: 2024 }
]

const renderTrend = value => {
  const trendValue = Number(value) || 0
  const isDown = trendValue < 0
  return h('span', { class: ['trend', isDown ? 'down' : 'up'] }, `${isDown ? '▼' : '▲'}${Math.abs(trendValue)}%`)
}

const renderHeader = (label, showSort = false) => {
  const children = [h('span', label)]
  if (showSort) {
    children.push(h('span', { class: 'sort-icon' }, '▼'))
  }
  children.push(h('span', { class: 'comparison' }, '（同比）'))
  return h('span', { class: 'table-header-title' }, children)
}

/** 按火情总数获取镇街表格和面图层颜色。 */
const getFireCountColor = count => {
  const value = Number(count) || 0
  if (value > 50) return '#ff4d4f'
  if (value > 20) return '#ff8c1a'
  if (value > 5) return '#ffd039'
  if (value >= 1) return '#20d16f'
  return '#5f8fb8'
}

/** 渲染火情总数，按数量区间赋色。 */
const renderFireCount = row => {
  return h('span', { style: { color: getFireCountColor(row.fireCount), fontWeight: 700 } }, [
    h('span', row.fireCount),
    renderTrend(row.fireTrend)
  ])
}

const columns = computed(() => [
  {
    title: '序号',
    key: 'index',
    width: 60,
    render(_row, rowIndex) {
      return rowIndex + 1
    }
  },
  { title: '乡镇街道', key: 'town', width: 120, ellipsis: true },
  {
    title: () => renderHeader('火情总数', true),
    key: 'fireCount',
    render(row) {
      return renderFireCount(row)
    }
  },
  {
    title: () => renderHeader('财产损失'),
    key: 'loss',
    render(row) {
      return h('span', [
        h('span', row.loss),
        renderTrend(row.lossTrend)
      ])
    }
  }
])

const calcTotal = computed(() => {
  const fireTotal = tableData.value.reduce((total, item) => total + (Number(item.fireCount) || 0), 0)
  const lossTotal = tableData.value.reduce((total, item) => total + (Number(item.lossAmount) || 0), 0)

  return {
    fireTotal,
    lossTotal: `${lossTotal}万`
  }
})

/** 解析 GeoJSON Polygon/MultiPolygon 为 Cesium 面坐标集合。 */
const getPolygonRings = feature => {
  const geometry = feature?.geometry
  if (!geometry?.coordinates) return []
  if (geometry.type === 'Polygon') return geometry.coordinates
  if (geometry.type === 'MultiPolygon') return geometry.coordinates.flat()
  return []
}

/** 判断坐标是否在国内，国内 GCJ-02 才需要转换为 WGS-84。 */
const isOutOfChina = (lng, lat) => {
  return lng < 72.004 || lng > 137.8347 || lat < 0.8293 || lat > 55.8271
}

const transformLat = (lng, lat) => {
  let ret = -100.0 + 2.0 * lng + 3.0 * lat + 0.2 * lat * lat + 0.1 * lng * lat + 0.2 * Math.sqrt(Math.abs(lng))
  ret += (20.0 * Math.sin(6.0 * lng * Math.PI) + 20.0 * Math.sin(2.0 * lng * Math.PI)) * 2.0 / 3.0
  ret += (20.0 * Math.sin(lat * Math.PI) + 40.0 * Math.sin(lat / 3.0 * Math.PI)) * 2.0 / 3.0
  ret += (160.0 * Math.sin(lat / 12.0 * Math.PI) + 320 * Math.sin(lat * Math.PI / 30.0)) * 2.0 / 3.0
  return ret
}

const transformLng = (lng, lat) => {
  let ret = 300.0 + lng + 2.0 * lat + 0.1 * lng * lng + 0.1 * lng * lat + 0.1 * Math.sqrt(Math.abs(lng))
  ret += (20.0 * Math.sin(6.0 * lng * Math.PI) + 20.0 * Math.sin(2.0 * lng * Math.PI)) * 2.0 / 3.0
  ret += (20.0 * Math.sin(lng * Math.PI) + 40.0 * Math.sin(lng / 3.0 * Math.PI)) * 2.0 / 3.0
  ret += (150.0 * Math.sin(lng / 12.0 * Math.PI) + 300.0 * Math.sin(lng / 30.0 * Math.PI)) * 2.0 / 3.0
  return ret
}

/** 将 yzq-new.json 中的 GCJ-02 坐标转换为 Cesium 使用的 WGS-84 坐标。 */
const gcj02ToWgs84 = (lng, lat) => {
  if (!Number.isFinite(lng) || !Number.isFinite(lat) || isOutOfChina(lng, lat)) return [lng, lat]
  const a = 6378245.0
  const ee = 0.00669342162296594323
  let dLat = transformLat(lng - 105.0, lat - 35.0)
  let dLng = transformLng(lng - 105.0, lat - 35.0)
  const radLat = lat / 180.0 * Math.PI
  let magic = Math.sin(radLat)
  magic = 1 - ee * magic * magic
  const sqrtMagic = Math.sqrt(magic)
  dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * Math.PI)
  dLng = (dLng * 180.0) / (a / sqrtMagic * Math.cos(radLat) * Math.PI)
  const mgLat = lat + dLat
  const mgLng = lng + dLng
  return [lng * 2 - mgLng, lat * 2 - mgLat]
}

/** 移除消防 KPI 镇街面图层。 */
const clearFirePolygonLayer = () => {
  Object.keys(firePolygonLayer).forEach(key => {
    const entity = firePolygonLayer[key]
    if (entity) window.viewer?.entities?.remove(entity)
    delete firePolygonLayer[key]
  })
}

/** 读取 Cesium PropertyBag 中的属性原始值。 */
const getPropertyValue = value => {
  if (value && typeof value.getValue === 'function') {
    return value.getValue(typeof Cesium !== 'undefined' ? Cesium.JulianDate.now() : undefined)
  }
  return value
}

/** 隐藏镇街面图层悬浮提示。 */
const hideFireHoverLabel = () => {
  if (fireHoverLabel) {
    fireHoverLabel.show = false
  }
}

/** 显示镇街面图层悬浮提示。 */
const showFireHoverLabel = (cartesian, townName, fireCount) => {
  if (!window.viewer || typeof Cesium === 'undefined' || !cartesian) return
  const text = `${townName || '-'}\n火情数量：${Number(fireCount) || 0}起`

  if (!fireHoverLabel) {
    fireHoverLabel = window.viewer.entities.add({
      position: cartesian,
      label: {
        text,
        font: '14px Microsoft YaHei',
        fillColor: Cesium.Color.WHITE,
        showBackground: true,
        backgroundColor: Cesium.Color.fromCssColorString('rgba(5, 24, 52, 0.82)'),
        backgroundPadding: new Cesium.Cartesian2(10, 6),
        pixelOffset: new Cesium.Cartesian2(0, -18),
        verticalOrigin: Cesium.VerticalOrigin.BOTTOM,
        disableDepthTestDistance: Number.POSITIVE_INFINITY
      },
      properties: new Cesium.PropertyBag({
        type: 'fireKpiTownHoverLabel'
      })
    })
    return
  }

  fireHoverLabel.position = cartesian
  fireHoverLabel.label.text = text
  fireHoverLabel.show = true
}

/** 监听鼠标悬浮镇街面图层，展示街道名称和火情数量。 */
const ensureFireHoverHandler = () => {
  if (fireHoverHandler || !window.viewer || typeof Cesium === 'undefined') return

  fireHoverHandler = new Cesium.ScreenSpaceEventHandler(window.viewer.scene.canvas)
  fireHoverHandler.setInputAction(movement => {
    const picked = window.viewer?.scene?.pick(movement.endPosition)
    const properties = picked?.id?.properties
    const type = getPropertyValue(properties?.type)
    if (type !== 'fireKpiTownPolygon') {
      hideFireHoverLabel()
      return
    }

    const townName = getPropertyValue(properties?.townName)
    const fireCount = getPropertyValue(properties?.fireCount)
    const cartesian = window.viewer?.scene?.pickPosition?.(movement.endPosition)
      || window.viewer?.camera?.pickEllipsoid(movement.endPosition, window.viewer.scene.globe.ellipsoid)
    showFireHoverLabel(cartesian, townName, fireCount)
  }, Cesium.ScreenSpaceEventType.MOUSE_MOVE)
}

/** 清理镇街面图层悬浮提示和鼠标监听。 */
const clearFireHoverHandler = () => {
  if (fireHoverHandler) {
    fireHoverHandler.destroy()
    fireHoverHandler = null
  }
  if (fireHoverLabel) {
    window.viewer?.entities?.remove(fireHoverLabel)
    fireHoverLabel = null
  }
}

/** 根据镇街火情总数渲染半透明面图层，避免完全遮盖底图。 */
const renderFirePolygonLayer = () => {
  clearFirePolygonLayer()
  if (!window.viewer || typeof Cesium === 'undefined') return

  const countMap = tableData.value.reduce((map, row) => {
    map[row.town] = Number(row.fireCount) || 0
    return map
  }, {})

  ;(yzqGeoJson.features || []).forEach((feature, featureIndex) => {
    const townName = feature?.properties?.name
    const fireCount = countMap[townName] || 0
    const color = getFireCountColor(fireCount)

    getPolygonRings(feature).forEach((ring, ringIndex) => {
      const positions = ring
        .map(point => {
          const [lng, lat] = gcj02ToWgs84(Number(point[0]), Number(point[1]))
          return Cesium.Cartesian3.fromDegrees(lng, lat, 260)
        })
        .filter(Boolean)
      if (positions.length < 3) return

      const key = `${townName || featureIndex}_${ringIndex}`
      firePolygonLayer[key] = window.viewer.entities.add({
        name: townName,
        polygon: {
          hierarchy: new Cesium.PolygonHierarchy(positions),
          material: Cesium.Color.fromCssColorString(color).withAlpha(0.26),
          outline: true,
          outlineColor: Cesium.Color.fromCssColorString(color).withAlpha(0.85),
          outlineWidth: 2,
          height: 260,
          perPositionHeight: true
        },
        properties: new Cesium.PropertyBag({
          type: 'fireKpiTownPolygon',
          townName,
          fireCount
        })
      })
    })
  })
  ensureFireHoverHandler()
}

const getFireKpiData = async () => {
  try {
    const res = await axios.post(`${BASE_URL}/list/reviewAndImprove`, {
      year: selectedYear.value
    })
    if (res.data.code === '000000') {
      const data = res.data.data || {}
      totalFireCount.value = data.fireTotalCount || 0
      totalFireTrend.value = data.fireTotalCountYoY || 0
      totalLossTrend.value = data.propertyLossAmountYoY || 0
      tableData.value = (data.streetList || []).map((item, index) => ({
        key: item.streetName || index,
        town: item.streetName || '-',
        fireCount: item.fireTotalCount || 0,
        fireTrend: item.fireTotalCountYoY || 0,
        lossAmount: item.propertyLossAmount || 0,
        loss: `${item.propertyLossAmount || 0}万`,
        lossTrend: item.propertyLossAmountYoY || 0
      }))
      renderFirePolygonLayer()
    } else {
      console.error('获取消防KPI数据失败：' + res.data.message)
    }
  } catch (err) {
    console.error('请求消防KPI接口失败', err)
  }
}

function handleClose() {
  visible.value = false
  clearFirePolygonLayer()
  clearFireHoverHandler()
  emit('close')
}

function openByEvent(payload) {
  const params = payload?.params || {}
  const diaName = payload?.diaName || params.diaName

  if (diaName !== 'fire-kpi-list') return

  visible.value = true
  getFireKpiData()
}

function closeByEvent() {
    visible.value = false
    clearFirePolygonLayer()
    clearFireHoverHandler()
}

watch(selectedYear, () => {
  if (visible.value) {
    getFireKpiData()
  }
})

onMounted(() => {
  eventBus.on(OPEN_FIRE_KPI_EVENT, openByEvent)
  eventBus.on(CLOSE_EVENT, closeByEvent)
})

onBeforeUnmount(() => {
  clearFirePolygonLayer()
  clearFireHoverHandler()
  eventBus.off(OPEN_FIRE_KPI_EVENT, openByEvent)
  eventBus.off(CLOSE_EVENT, closeByEvent)
})
</script>

<script lang="ts">
export default {
  name: 'BzFireKpiList',
  version: '1.0.0'
}
</script>

<style lang="scss" scoped>
@font-face {
  font-family: 'AlibabaPuHuiTi';
  src: url('./font/Alibaba_PuHuiTi_2.0_55_Regular_55_Regular.ttf') format('truetype');
  font-weight: normal;
  font-style: normal;
}

@font-face {
  font-family: 'D-DIN';
  src: url('./font/D-DIN.otf') format('opentype');
  font-weight: normal;
  font-style: normal;
}

.fire-kpi-modal {
  font-family: 'AlibabaPuHuiTi', sans-serif;
  position: relative;
  z-index: 1001;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  background:
    url('./img/Group_2136640490_1.png') top center / 100% 60px no-repeat,
    linear-gradient(180deg, rgba(3, 19, 54, 0.95) 0%, rgba(2, 12, 36, 0.95) 100%);
  border: 1px solid rgba(37, 134, 255, 0.25);
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.45);
  color: #b8d9ff;
  font-size: 14px;
  overflow: hidden;
  pointer-events: auto;

  &::before {
    content: '';
    position: absolute;
    inset: 0;
    background: url('./img/Rectangle_346242153_1.png') center / cover no-repeat;
    opacity: 0.25;
    z-index: -1;
  }
}

.modal-header {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  flex-shrink: 0;
  padding: 0 32px;

  .header-title {
    color: #cfe8ff;
    font-size: 20px;
    font-weight: 700;
    text-shadow: 0 0 12px rgba(83, 174, 255, 0.5);
  }

  .close-btn {
    position: absolute;
    right: 20px;
    top: 50%;
    transform: translateY(-50%);
    width: 32px;
    height: 32px;
    border: 1px solid rgba(83, 174, 255, 0.3);
    background: rgba(16, 64, 126, 0.4);
    color: #aed5ff;
    font-size: 20px;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;

    &:hover {
      color: #ffffff;
      border-color: rgba(83, 174, 255, 0.6);
      background: rgba(24, 92, 179, 0.6);
    }
  }
}

.modal-content {
  flex: 1;
  min-height: 0;
  padding: 20px 24px 0 24px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.stat-card {
  display: flex;
  align-items: center;
  height: 112px;
  padding: 0 24px;
  margin-bottom: 20px;
  background: rgba(11, 67, 124, 0.3);
  border: 1px solid rgba(37, 134, 255, 0.3);
  border-radius: 8px;

  .stat-icon {
    width: 64px;
    height: 64px;
    margin-right: 16px;
    display: flex;
    align-items: center;
    justify-content: center;

    img {
      width: 100%;
      height: 100%;
    }
  }

  .stat-info {
    flex: 1;

    .stat-title {
      font-size: 16px;
      color: #b8d9ff;
      margin-bottom: 8px;
    }

    .stat-value {
      display: flex;
      align-items: baseline;

      .value-num {
        font-size: 28px;
        font-weight: 700;
        font-family: 'D-DIN', 'AlibabaPuHuiTi', sans-serif;
        background: linear-gradient(180deg, #33f7ff 0%, #00f09e 100%);
        -webkit-background-clip: text;
        background-clip: text;
        color: transparent;
      }

      .value-unit {
        font-size: 14px;
        color: #b8d9ff;
        margin: 0 8px;
      }

      .value-trend {
        font-size: 14px;
        font-family: 'D-DIN', 'AlibabaPuHuiTi', sans-serif;

        &.down {
          color: #00f09e;
        }

        &.up {
          color: #ff4d4f;
        }
      }
    }
  }

  .stat-year {
    width: 100px;
  }
}

.year-select {
  width: 100px;

  :deep(.n-base-selection) {
    min-height: 40px;
    background: rgba(16, 64, 126, 0.6);
    border: 1px solid rgba(37, 134, 255, 0.4);
    border-radius: 4px;
  }

  :deep(.n-base-selection-label) {
    height: 38px;
    background: transparent;
  }

  :deep(.n-base-selection-input),
  :deep(.n-base-selection-placeholder),
  :deep(.n-base-selection__state-border),
  :deep(.n-base-selection__border) {
    color: #cfe8ff;
  }

  :deep(.n-base-selection__border),
  :deep(.n-base-selection__state-border) {
    border: none;
    box-shadow: none;
  }
}

.table-container {
  flex: 1;
  min-height: 0;
  overflow: hidden;
  margin-bottom: 0;
}

:deep(.kpi-table) {
  height: 100%;

  .n-data-table-wrapper,
  .n-data-table-base-table,
  .n-data-table-base-table-body,
  .n-data-table-table {
    background: transparent;
  }

  .n-data-table-wrapper,
  .n-data-table-base-table {
    height: 100%;
  }

  .n-data-table-base-table-body {
    overflow-y: auto;
    overflow-x: hidden;

    &::-webkit-scrollbar {
      width: 6px;
    }

    &::-webkit-scrollbar-track {
      background: rgba(8, 35, 74, 0.55);
      border-radius: 3px;
    }

    &::-webkit-scrollbar-thumb {
      background: rgba(64, 158, 255, 0.55);
      border-radius: 3px;

      &:hover {
        background: rgba(64, 158, 255, 0.8);
      }
    }
  }

  .n-data-table-table {
    table-layout: fixed;
    font-size: 14px;
  }

  .n-data-table-th {
    height: 40px;
    padding: 0 12px;
    color: #8bbce6;
    font-weight: 500;
    background: #0b437c;
    border: none;
    white-space: nowrap;
  }

  .n-data-table-td {
    height: 40px;
    padding: 0 12px;
    color: #d7e9ff;
    font-size: 14px;
    font-family: 'D-DIN', 'AlibabaPuHuiTi', sans-serif;
    background: #0a325f;
    border: none;
  }

  .n-data-table-tr:nth-child(even) .n-data-table-td {
    background: #19406b;
  }

  .n-data-table-tr:hover .n-data-table-td {
    background: rgba(29, 107, 202, 0.3);
  }

  .sort-icon {
    color: #00f09e;
    margin: 0 4px;
  }

  .comparison {
    color: #66a3d9;
    margin-left: 4px;
  }
}

.trend {
  margin-left: 8px;
  font-family: 'D-DIN', 'AlibabaPuHuiTi', sans-serif;

  &.down {
    color: #00f09e;
  }

  &.up {
    color: #ff4d4f;
  }
}

.total-footer {
  height: 40px;
  background: #0b437c;
  margin-bottom: 20px;
}

.total-row {
  display: grid;
  grid-template-columns: 60px 120px minmax(0, 1fr) minmax(0, 1fr);
  height: 40px;
}

.total-cell {
  min-width: 0;
  height: 40px;
  padding: 0 12px;
  display: flex;
  align-items: center;
  color: #cfe8ff;
  font-weight: 500;
  font-size: 14px;
  font-family: 'D-DIN', 'AlibabaPuHuiTi', sans-serif;
}
</style>
