<template>
  <div class="risk-device-point" :class="{ 'risk-device-point--single': isSingleMode, 'risk-device-point--flood': isFloodMode }" :style="riskDeviceStyle">
    <!-- 内涝模式 -->
    <template v-if="!isFloodMode">
    <n-checkbox-group
      :value="selectedLayerKeys"
      class="legend-panel"
      @update:value="handleLegendChange"
    >
    <div
        v-for="item in legendItems"
        :key="item.key">
      <n-checkbox
        v-if="getItemMapIcon(item)"
        class="legend-item"
        :value="item.key"
        :title="item.label"
      >
        <img class="legend-icon" :src="getItemMapIcon(item)" alt="" />
        <span class="legend-name">{{ item.label }}</span>
      </n-checkbox>
    </div>
    </n-checkbox-group>
    <section class="panel">
      <div class="panel-title panel-title--clickable" @click="openRiskPointList">风险点</div>
      <div class="stat-row">
        <button
          v-for="item in riskStats"
          :key="item.key"
          class="stat-card"
          :title="item.label"
          type="button"
        >
          <img class="stat-icon" :src="item.icon" alt="" />
          <span class="stat-label" :title="item.label">{{ item.label }}</span>
          <span class="stat-value" :class="item.valueClass">{{ item.value }}</span>
        </button>
      </div>
    </section>

    <section class="panel">
      <div class="panel-title panel-title--clickable" @click="openPerceptionDeviceList">感知设备</div>
      <div class="stat-row">
        <button
          v-for="item in deviceStats"
          :key="item.key"
          class="stat-card"
          :title="item.label"
          type="button"
        >
          <img class="stat-icon" :src="item.icon" alt="" />
          <span class="stat-label" :title="item.label">{{ item.label }}</span>
          <span class="stat-value" :class="item.valueClass">{{ item.value }}</span>
        </button>
      </div>
    </section>
    </template>

    <!-- 防汛模式 -->
    <template v-else>
      <div class="flood-action-bar">
        <button class="flood-action-btn" :class="{ 'flood-action-btn--active': floodMapShown }" type="button" @click="openFloodMap">防汛图</button>
        <button class="flood-action-btn" type="button" @click="openWaterPrediction">沙盘推演</button>
        <!-- <button class="flood-action-btn" type="button">应急力量</button> -->
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch, onUnmounted, onMounted } from 'vue'
import axios from 'axios'
import { NCheckbox, NCheckboxGroup } from 'naive-ui'
import iconRiskTotal from './img/icon_risk_total.png'
import iconFloodTotal from './img/icon_flood_total.png'
import iconFloodAreaTotal from './img/icon_flood_area_total.png'
import iconEquipmentTotal from './img/icon_equipment_total.png'
import iconCameraTotal from './img/icon_camera_total.png'
import iconGaugeTotal from './img/icon_gauge_total.png'
import iconOthersTotal from './img/icon_others_total.png'
import iconFloodRiskMap from './img/hongzaifengxian.png'
import iconWaterlogRiskMap from './img/neilaofengxian.png'
import iconCameraMap from './img/shexiangtou.png'
import iconGaugeMap from './img/yeweijin.png'
import iconOtherMap from './img/qita.png'
import riverGreenIcon from '../BzWarning/img/shuihuan_hd_lv.png'
import riverYellowIcon from '../BzWarning/img/shuihuan_hd_huang.png'
import riverRedIcon from '../BzWarning/img/shuihuan_hd_hong.png'

type LayerKind = 'risk' | 'device'
type StatItem = {
  key: string
  kind: LayerKind
  label: string
  value: number
  icon: string
  valueClass?: string
  mapIcon?:string
}
type PointItem = {
  id?: string | number
  name?: string
  deviceName?: string
  deviceCode?: string
  type?: string
  lng?: string | number
  lat?: string | number
  longitude?: string | number
  latitude?: string | number
  lngWgs84?: string | number
  latWgs84?: string | number
  lon?: string | number
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

const sourceName = 'BzWaterRiskDevice'
const OPEN_WATER_KPI_EVENT = 'OPEN_WATER_KPI_DIA'

const eventBus = {
  emit(event, data) {
    if (!props.bus) return
    props.bus.emit(event, { data, source: sourceName })
  }
}

const isSingleMode = computed(() => props.chartConfig?.option?.containerMode === 'single')
const isFloodMode = computed(() => props.chartConfig?.option?.mode === 'flood')
const riskDeviceStyle = computed(() => ({
  '--risk-device-width': isSingleMode.value ? '736px' : '1208px'
}))

const allRoadDeviceURL = 'http://23.210.227.34:23343/yzqzlzx/api/boot/system/roadDevice/queryAllRoadDevice'
const WATERLOG_BASE_URL = 'http://23.99.16.179:11001/api/boot/system/waterlog'
const ALL_STATION_URL = 'http://23.210.227.34:23343/yzqzlzx/api/boot/system/common/getAllHydrologyStationInfo'
const RIVER_LINE_POINTS_URL = 'http://23.210.227.34:23343/yzqzlzx/api/boot/system/common/getAllRiverLinePoints'
const selectedLayerKeys = ref<string[]>([])
const pointLayer: Record<string, Cesium.Entity> = {}
const floodPointEntities: Cesium.Entity[] = []
const floodLineEntities: Cesium.Entity[] = []
const floodLabelEntities: Cesium.Entity[] = []
const floodMapShown = ref(false)
let clickHandler: Cesium.ScreenSpaceEventHandler | null = null
let cameraChangedHandler: (() => void) | null = null

const riskStats = ref<StatItem[]>([
  { key: 'risk-all', kind: 'risk', label: '风险点总数', value: 0, icon: iconRiskTotal },
  { key: 'risk-flood', kind: 'risk', label: '洪灾风险', value: 0, icon: iconFloodTotal, valueClass: 'danger', mapIcon: './img/hongzaifengxian.png' },
  { key: 'risk-waterlog', kind: 'risk', label: '内涝风险', value: 0, icon: iconFloodAreaTotal, valueClass: 'warning', mapIcon: './img/neilaofengxian.png' }
])

const deviceStats = ref<StatItem[]>([
  { key: 'device-all', kind: 'device', label: '设备总数', value: 67, icon: iconEquipmentTotal },
  { key: 'device-camera', kind: 'device', label: '摄像头', value: 53, icon: iconCameraTotal, mapIcon: './img/shexiangtou.png' },
  { key: 'device-gauge', kind: 'device', label: '液位计', value: 33, icon: iconGaugeTotal, mapIcon: './img/yeweijin.png' },
  { key: 'device-other', kind: 'device', label: '水文站', value: 15, icon: iconOthersTotal, mapIcon: './img/qita.png' }
])

const legendItems = computed(() => [...riskStats.value, ...deviceStats.value])
const mapIconMap: Record<string, string> = {
  './img/hongzaifengxian.png': iconFloodRiskMap,
  './img/neilaofengxian.png': iconWaterlogRiskMap,
  './img/shexiangtou.png': iconCameraMap,
  './img/yeweijin.png': iconGaugeMap,
  './img/qita.png': iconOtherMap
}

const getItemMapIcon = (item: StatItem) => {
  if (!item.mapIcon) return ''
  return mapIconMap[item.mapIcon] || item.mapIcon
}

const fallbackRiskPoints: Record<string, PointItem[]> = {
  'risk-all': [
    { id: 'risk_1', name: '解放碑洪灾风险点', type: '洪灾风险', lng: 106.5772, lat: 29.557 },
    { id: 'risk_2', name: '朝天门内涝风险点', type: '内涝风险', lng: 106.5879, lat: 29.5647 }
  ],
  'risk-flood': [
    { id: 'flood_1', name: '洪崖洞洪灾风险点', type: '洪灾风险', lng: 106.5811, lat: 29.5626 },
    { id: 'flood_2', name: '朝天门洪灾风险点', type: '洪灾风险', lng: 106.5881, lat: 29.5639 }
  ],
  'risk-waterlog': [
    { id: 'waterlog_1', name: '两路口内涝风险点', type: '内涝风险', lng: 106.5482, lat: 29.5526 },
    { id: 'waterlog_2', name: '解放碑内涝风险点', type: '内涝风险', lng: 106.5764, lat: 29.5582 }
  ]
}

const getPointLng = (item: PointItem) => Number(item.lngWgs84 ?? item.longitude ?? item.lng ?? item.lon)
const getPointLat = (item: PointItem) => Number(item.latWgs84 ?? item.latitude ?? item.lat)

const clearPointLayer = () => {
  Object.keys(pointLayer).forEach(key => {
    const entity = pointLayer[key]
    if (entity) {
      window.viewer?.entities?.remove(entity)
    }
    delete pointLayer[key]
  })
}

const removeLayerByKey = (layerKey: string) => {
  Object.keys(pointLayer).forEach(key => {
    if (!key.startsWith(`${layerKey}_`)) return

    const entity = pointLayer[key]
    if (entity) {
      window.viewer?.entities?.remove(entity)
    }
    delete pointLayer[key]
  })
}

const getDevicePoints = async (key: string) => {
  try {
    const res = await axios.post(allRoadDeviceURL)
    const list = res?.data?.data || []
    if (!Array.isArray(list)) return []

    if (key === 'device-camera') {
      return list.filter(item => String(item.deviceTypeName || item.deviceType || item.type || '').includes('摄像'))
    }

    if (key === 'device-gauge') {
      return list.filter(item => String(item.deviceTypeName || item.deviceType || item.type || '').includes('液位'))
    }

    if (key === 'device-other') {
      return list.filter(item => {
        const type = String(item.deviceTypeName || item.deviceType || item.type || '')
        return !type.includes('摄像') && !type.includes('液位')
      })
    }

    return list
  } catch (error) {
    console.error('获取设备点位失败:', error)
    return []
  }
}

const getWaterlogRiskPoints = async () => {
  try {
    const res = await axios.get(`${WATERLOG_BASE_URL}/risk/point`)
    const list = res?.data?.data
    if (!Array.isArray(list)) return []
    return list
  } catch (error) {
    console.error('获取内涝风险点位失败:', error)
    return []
  }
}

const getHydrologyStationPoints = async () => {
  try {
    const res = await axios.post(ALL_STATION_URL)
    const list = res.data?.data?.stations || []
    if (!Array.isArray(list)) return []
    return list
  } catch (error) {
    console.error('获取水文站点位失败:', error)
    return []
  }
}

const getLayerPoints = async (item: StatItem) => {
  if (item.key === 'risk-waterlog') {
    return getWaterlogRiskPoints()
  }
  if (item.key === 'device-other') {
    return getHydrologyStationPoints()
  }
  if (item.kind === 'risk') {
    return fallbackRiskPoints[item.key] || []
  }
  return getDevicePoints(item.key)
}

const getPropertyValue = (value: any) => {
  if (value && typeof value.getValue === 'function') {
    return value.getValue(typeof Cesium !== 'undefined' ? Cesium.JulianDate.now() : undefined)
  }
  return value
}

const getEntityProperties = (entity: any) => {
  const properties = entity?.properties
  if (!properties) return {}
  const propertyValue = getPropertyValue(properties)
  return propertyValue || properties
}

const openWaterlogRiskDetail = (point: any) => {
  eventBus.emit(OPEN_WATER_KPI_EVENT, {
    diaName: 'waterlog-risk-detail',
    id: point?.id || '',
    name: point?.name || ''
  })
}

const ensureMapClickHandler = () => {
  if (clickHandler || !window.viewer || typeof Cesium === 'undefined') return

  clickHandler = new Cesium.ScreenSpaceEventHandler(window.viewer.scene.canvas)
  clickHandler.setInputAction((movement: any) => {
    const picked = window.viewer?.scene?.pick(movement.position)
    const entity = picked?.id
    if (!entity) return

    const properties = getEntityProperties(entity)
    const pointType = getPropertyValue(properties.type)
    const layerKey = getPropertyValue(properties.layerKey)
    const data = getPropertyValue(properties.data)

    if (pointType === 'floodStation') {
      const stnm = getPropertyValue(properties.stnm)
      eventBus.emit(OPEN_WATER_KPI_EVENT, {
        diaName: 'station-water-level',
        stationName: stnm || ''
      })
      return
    }

    if (pointType !== 'riskDevicePoint') return

    if (layerKey === 'risk-waterlog') {
      openWaterlogRiskDetail(data || {})
    }
  }, Cesium.ScreenSpaceEventType.LEFT_CLICK)
}

const addPointLayer = async (item: StatItem) => {
  removeLayerByKey(item.key)
  if (!window.viewer || typeof Cesium === 'undefined') return
  ensureMapClickHandler()

  const list = await getLayerPoints(item)
  const icon = getItemMapIcon(item) || item.icon

  list.forEach((point, index) => {
    const lng = getPointLng(point)
    const lat = getPointLat(point)

    if (!Number.isFinite(lng) || !Number.isFinite(lat)) return

    const key = `${item.key}_${point.id ?? point.deviceCode ?? index}`
    const entity = window.viewer.entities.add({
      position: Cesium.Cartesian3.fromDegrees(lng, lat, 360),
      billboard: {
        image: icon,
        width: 15,
        height: 15,
        verticalOrigin: Cesium.VerticalOrigin.BOTTOM,
        disableDepthTestDistance: Number.POSITIVE_INFINITY
      },
      properties: new Cesium.PropertyBag({
        type: 'riskDevicePoint',
        layerKey: item.key,
        layerName: item.label,
        data: point
      })
    })

    pointLayer[key] = entity
  })
}

const handleLegendChange = async (keys: string[]) => {
  const prevKeys = selectedLayerKeys.value
  const addedKeys = keys.filter(key => !prevKeys.includes(key))
  const removedKeys = prevKeys.filter(key => !keys.includes(key))

  selectedLayerKeys.value = keys

  removedKeys.forEach(removeLayerByKey)

  for (const key of addedKeys) {
    const item = legendItems.value.find(legendItem => legendItem.key === key)
    if (item) {
      await addPointLayer(item)
    }
  }
}

const openRiskPointList = () => {
  eventBus.emit(OPEN_WATER_KPI_EVENT, {
    diaName: 'waterlog-risk-point-list'
  })
}

const openPerceptionDeviceList = () => {
  eventBus.emit(OPEN_WATER_KPI_EVENT, {
    diaName: 'waterlog-perception-device-list'
  })
}

const clearFloodLayer = () => {
  if (!window.viewer) return
  if (cameraChangedHandler) {
    window.viewer.camera.changed.removeEventListener(cameraChangedHandler)
    cameraChangedHandler = null
  }
  floodPointEntities.forEach(e => window.viewer!.entities.remove(e))
  floodPointEntities.length = 0
  floodLabelEntities.forEach(e => window.viewer!.entities.remove(e))
  floodLabelEntities.length = 0
  floodLineEntities.forEach(e => window.viewer!.entities.remove(e))
  floodLineEntities.length = 0
}

const getStationIcon = (st: any) => {
  const z = Number(st.z ?? st.sw ?? -999)
  const wrz = Number(st.wrz ?? st.ivhz ?? -999)
  const grz = Number(st.grz ?? -999)
  if (wrz !== -999 && z >= wrz) return riverRedIcon
  if (grz !== -999 && z >= grz) return riverYellowIcon
  return riverGreenIcon
}

const JIALING_RIVER_STATIONS = ['鸭嘴', '东津沱', '北碚', '磁器口', '化龙桥', '大溪沟', '千厮门', '朝天门']
const YANGTZE_RIVER_STATIONS = ['泸州', '塔坪', '钓二嘴', '李家沱', '菜园坝', '玄坛庙', '朝天门']
const MERGED_RIVER_STATIONS = ['朝天门', '郭家沱']

const addFloodLayer = async () => {
  clearFloodLayer()
  if (!window.viewer || typeof Cesium === 'undefined') return
  ensureMapClickHandler()

  // 1. 获取站点水情综合信息，用于补齐当前水位、保证水位、警戒水位等字段
  let stationDetails: any[] = []
  try {
    const res = await axios.post(ALL_STATION_URL)
    stationDetails = res.data?.data?.stations || []
    if (!Array.isArray(stationDetails)) stationDetails = []
  } catch (error) {
    console.error('获取站点水情综合信息失败:', error)
    return
  }

  // 2. 获取河道走向点，station=true 为站点，station=false 为拐点
  let riverPoints: any[] = []
  try {
    const res = await axios.post(RIVER_LINE_POINTS_URL)
    riverPoints = res.data?.data?.points || []
    if (!Array.isArray(riverPoints)) riverPoints = []
  } catch (error) {
    console.error('获取河道走向点失败:', error)
    return
  }

  // 3. station=true 的点位全部撒出，水位数据优先使用站点详情接口
  const labelBgColor = Cesium.Color.fromCssColorString('rgba(4, 35, 82, 0.94)')
  const labelCommon = {
    font: 'bold 16px sans-serif',
    outlineColor: Cesium.Color.BLACK,
    outlineWidth: 2,
    style: Cesium.LabelStyle.FILL_AND_OUTLINE,
    verticalOrigin: Cesium.VerticalOrigin.BOTTOM,
    showBackground: true,
    backgroundColor: labelBgColor,
    backgroundPadding: new Cesium.Cartesian2(10, 6),
    disableDepthTestDistance: Number.POSITIVE_INFINITY,
    scale: 1.0
  }

  // 根据相机高度动态调整标签缩放
  const updateLabelScale = () => {
    if (!window.viewer) return
    const cameraHeight = window.viewer.camera.positionCartographic.height
    // 基准高度 5000m，scale=1.0；高度越大缩放越小，最小0.3
    const baseHeight = 5000
    const scale = Math.max(0.3, Math.min(1.0, baseHeight / cameraHeight))
    floodLabelEntities.forEach(entity => {
      if (entity.label) {
        entity.label.scale = new Cesium.ConstantProperty(scale)
      }
    })
  }

  riverPoints.forEach((pt: any) => {
    const lon = Number(pt.longitude)
    const lat = Number(pt.latitude)
    if (!Number.isFinite(lon) || !Number.isFinite(lat)) return

    // station=false 只作为河道拐点参与连线，不单独撒点
    if (!pt.station) return

    // station=true 点位按名称匹配水情详情，匹配不到时使用河道点自身数据
    const detail = stationDetails.find((s: any) => (s.stnm || s.name || '') === (pt.name || ''))
    const st = detail || pt
    const name = st.stnm || st.name || pt.name || '-'
    const pos = Cesium.Cartesian3.fromDegrees(lon, lat, 360)
    const icon = getStationIcon(st)

    // 绘制站点图标
    const pointEntity = window.viewer.entities.add({
      position: pos,
      billboard: {
        image: icon,
        width: 20,
        height: 20,
        verticalOrigin: Cesium.VerticalOrigin.BOTTOM,
        disableDepthTestDistance: Number.POSITIVE_INFINITY
      },
      properties: new Cesium.PropertyBag({
        type: 'floodStation',
        stnm: name
      })
    })
    floodPointEntities.push(pointEntity)

    const z = st.z ?? st.sw
    const grz = st.grz
    const wrz = st.wrz ?? st.ivhz
    const zStr = z != null && z !== '' ? `${z}m` : '-'
    const grzStr = grz != null && grz !== '' ? `${grz}m` : '-'
    const wrzStr = wrz != null && wrz !== '' ? `${wrz}m` : '-'

    // 站名（顶部，浅蓝白）
    floodLabelEntities.push(window.viewer.entities.add({
      position: pos,
      label: {
        ...labelCommon,
        text: name,
        fillColor: Cesium.Color.fromCssColorString('#dff6ff'),
        pixelOffset: new Cesium.Cartesian2(0, -88)
      }
    }))
    // 当前水位（青色）
    floodLabelEntities.push(window.viewer.entities.add({
      position: pos,
      label: {
        ...labelCommon,
        text: `当前水位: ${zStr}`,
        fillColor: Cesium.Color.fromCssColorString('#5FEAFF'),
        pixelOffset: new Cesium.Cartesian2(0, -68)
      }
    }))
    // 保证水位（红色）
    floodLabelEntities.push(window.viewer.entities.add({
      position: pos,
      label: {
        ...labelCommon,
        text: `保证水位: ${grzStr}`,
        fillColor: Cesium.Color.fromCssColorString('#F84444'),
        pixelOffset: new Cesium.Cartesian2(0, -48)
      }
    }))
    // 警戒水位（黄色）
    floodLabelEntities.push(window.viewer.entities.add({
      position: pos,
      label: {
        ...labelCommon,
        text: `警戒水位: ${wrzStr}`,
        fillColor: Cesium.Color.fromCssColorString('#ffe713'),
        pixelOffset: new Cesium.Cartesian2(0, -28)
      }
    }))
  })

  // 标签随地图缩放
  updateLabelScale()
  if (cameraChangedHandler) {
    window.viewer.camera.changed.removeEventListener(cameraChangedHandler)
  }
  cameraChangedHandler = updateLabelScale
  window.viewer.camera.changed.addEventListener(cameraChangedHandler)

  // 4. 分段连线：按嘉陵江、长江、汇合段构建路径，保留 station=false 拐点
  const dashColor = Cesium.Color.fromCssColorString('rgba(106, 169, 234, 0.78)')
  const flowColor = Cesium.Color.fromCssColorString('#5FD3FF')

  const getRiverPointPosition = (pt: any) => {
    const lon = Number(pt.longitude)
    const lat = Number(pt.latitude)
    if (!Number.isFinite(lon) || !Number.isFinite(lat)) return null
    return { lon, lat, position: Cesium.Cartesian3.fromDegrees(lon, lat, 360) }
  }

  const addLine = (linePositions: Cesium.Cartesian3[]) => {
    if (linePositions.length < 2) return

    floodLineEntities.push(window.viewer.entities.add({
      polyline: {
        positions: linePositions,
        width: 4,
        material: new Cesium.PolylineDashMaterialProperty({
          color: dashColor,
          dashLength: 12
        }),
        clampToGround: true
      }
    }))

    floodLineEntities.push(window.viewer.entities.add({
      polyline: {
        positions: linePositions,
        width: 6,
        material: new Cesium.PolylineGlowMaterialProperty({
          color: flowColor.withAlpha(0.9),
          glowPower: 0.2,
          taperPower: 1
        }),
        clampToGround: true
      }
    }))
  }

  const getRiverPointName = (pt: any) => String(pt?.name || pt?.stnm || '')
  const findStationIndex = (name: string, startIndex = 0) => {
    return riverPoints.findIndex((pt: any, index: number) => {
      if (index < startIndex || !pt.station) return false
      return getRiverPointName(pt).includes(name)
    })
  }
  const buildRiverLinePositions = (stationNames: string[]) => {
    const firstIndex = findStationIndex(stationNames[0])
    if (firstIndex < 0) return []

    const pathPoints: any[] = [riverPoints[firstIndex]]
    let fromIndex = firstIndex
    for (let i = 1; i < stationNames.length; i++) {
      const toIndex = findStationIndex(stationNames[i], fromIndex + 1)
      if (toIndex < 0) break
      pathPoints.push(...riverPoints.slice(fromIndex + 1, toIndex + 1))
      fromIndex = toIndex
    }

    return pathPoints
      .map(getRiverPointPosition)
      .filter(Boolean)
      .map((point: any) => point.position)
  }

  addLine(buildRiverLinePositions(JIALING_RIVER_STATIONS))
  addLine(buildRiverLinePositions(YANGTZE_RIVER_STATIONS))
  addLine(buildRiverLinePositions(MERGED_RIVER_STATIONS))
}

const openFloodMap = async () => {
  if (floodMapShown.value) {
    clearFloodLayer()
    floodMapShown.value = false
    return
  }
  await addFloodLayer()
  floodMapShown.value = true
}

const openEmergencyForce = () => {
  eventBus.emit(OPEN_WATER_KPI_EVENT, {
    diaName: 'emergency-force'
  })
}

const openWaterPrediction = () => {
  eventBus.emit(OPEN_WATER_KPI_EVENT, {
    diaName: 'water-prediction'
  })
}

// 模式切换时清理内涝/防汛图层状态，避免互相影响
watch(isFloodMode, (flood) => {
  if (flood) {
    // 切到防汛：清除地图点位、点击监听、勾选状态
    if (clickHandler) {
      clickHandler.destroy()
      clickHandler = null
    }
    clearPointLayer()
    selectedLayerKeys.value = []
  } else {
    // 切离防汛：清除防汛撒点图层
    clearFloodLayer()
    floodMapShown.value = false
  }
})

onMounted(async () => {
  try {
    const points = await getWaterlogRiskPoints()
    const count = points.length
    const target = riskStats.value.find(item => item.key === 'risk-waterlog')
    if (target) target.value = count
    // 风险总数 = 洪灾风险 + 内涝风险
    const total = riskStats.value
      .filter(item => item.key !== 'risk-all')
      .reduce((sum, item) => sum + item.value, 0)
    const totalItem = riskStats.value.find(item => item.key === 'risk-all')
    if (totalItem) totalItem.value = total
  } catch (error) {
    console.error('获取内涝风险点数量失败:', error)
  }
})

onUnmounted(() => {
  if (clickHandler) {
    clickHandler.destroy()
    clickHandler = null
  }
  clearPointLayer()
  clearFloodLayer()
  // 组件销毁后定位到渝中区并放大显示
  if (window.viewer && typeof Cesium !== 'undefined') {
    window.viewer.camera.flyTo({
      destination: Cesium.Cartesian3.fromDegrees(106.568, 29.553, 8000),
      duration: 1.5
    })
  }
})
</script>

<script lang="ts">
export default {
  name: 'BzWaterRiskDevice',
  version: '1.0.0'
}
</script>

<style lang="scss" scoped>
@font-face {
  font-family: 'UISDCBiaoTiHei';
  src: url('./font/UISDC-BiaoTiHei.ttf') format('truetype');
  font-weight: normal;
  font-style: normal;
}

@font-face {
  font-family: 'DDIN';
  src: url('./font/D-DIN.otf') format('opentype');
  font-weight: normal;
  font-style: normal;
}

@font-face {
  font-family: 'AlibabaPuHuiTiMedium';
  src: url('./font/Alibaba_PuHuiTi_2.0_65_Medium_65_Medium.ttf') format('truetype');
  font-weight: 500;
  font-style: normal;
}

.risk-device-point {
  position: relative;
  width: var(--risk-device-width, 1208px);
  min-height: 110px;
  height: auto;
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  column-gap: 12px;
  box-sizing: border-box;
  color: #dff7ff;
  font-family: 'AlibabaPuHuiTiMedium', Microsoft YaHei, sans-serif;
  pointer-events: none;
}

.legend-panel {
  position: absolute;
  top: 0;
  right: 0;
  z-index: 5;
  display: inline-flex;
  flex-direction: column;
  flex-wrap: nowrap;
  justify-content: flex-start;
  align-items: flex-start;
  gap: 4px;
  width: max-content;
  max-width: none;
  padding: 3px 6px;
  box-sizing: border-box;
  white-space: nowrap;
  background: url('./img/bg_left_top01.png');
  background-size: auto;
  pointer-events: auto;
}

.legend-item {
  flex: 0 0 auto;
  max-width: none;
  min-width: max-content;
  font-size: 12px;
  line-height: 16px;

  :deep(.n-checkbox) {
    align-items: center;
  }

  :deep(.n-checkbox-box) {
    width: 12px;
    height: 12px;
  }

  :deep(.n-checkbox__label) {
    display: flex;
    justify-content: center;
    align-items: center;
    min-width: max-content;
    padding-left: 4px;
    color: #dff7ff;
    font-size: 12px;
    line-height: 16px;
  }
}

.legend-icon {
  width: 14px;
  height: 14px;
  margin-right: 3px;
  object-fit: contain;
  vertical-align: top;
}

.legend-name {
  display: inline-block;
  max-width: none;
  min-width: max-content;
  overflow: visible;
  white-space: nowrap;
  vertical-align: top;
}

.panel {
  position: relative;
  top:170px;
  min-width: 0;
  height: auto;
}

.panel-title {
  font-family: 'UISDCBiaoTiHei';
  height: 32px;
  padding-left: 40px;
  box-sizing: border-box;
  background: url('./img/bg_second_title.png') left center / 100% 100% no-repeat;
  color: #f2fbff;
  font-family: 'UISDCBiaoTiHei', Microsoft YaHei, sans-serif;
  font-size: 24px;
  font-weight: 700;
  line-height: 32px;
  letter-spacing: 0;
  text-shadow: 0 0 10px rgba(92, 217, 255, 0.72);
  background-size:auto;
  pointer-events: auto;
}

.panel-title--clickable {
  cursor: pointer;
}

.stat-row {
  margin-top: 10px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  justify-content: flex-start;
  align-content: flex-start;
}

.stat-card {
  position: relative;
  flex: 1 1 0;
  height: 46px;
  min-width: 0;
  padding: 4px 10px 4px 30px;
  border: 1px solid rgba(59, 179, 255, 0.78);
  background:
    linear-gradient(180deg, rgba(12, 111, 181, 0.72) 0%, rgba(3, 67, 126, 0.88) 100%),
    radial-gradient(circle at 20% 0%, rgba(72, 211, 255, 0.35), transparent 50%);
  box-shadow: inset 0 0 16px rgba(56, 178, 255, 0.24), 0 0 8px rgba(35, 153, 255, 0.18);
  color: inherit;
  cursor: default;
  text-align: left;
  outline: none;
  transition: border-color 0.18s ease, box-shadow 0.18s ease;

  &:hover {
    border-color: rgba(59, 179, 255, 0.78);
    box-shadow: inset 0 0 16px rgba(56, 178, 255, 0.24), 0 0 8px rgba(35, 153, 255, 0.18);
  }
}

.stat-icon {
  position: absolute;
  left: 4px;
  top: 0px;
  width: 24px;
  height: 24px;
  object-fit: contain;
}

.stat-label {
  font-family: 'AlibabaPuHuiTiMedium';
  display: block;
  height: 18px;
  overflow: hidden;
  color: #cbefff;
  font-size: 14px;
  line-height: 18px;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.stat-value {
  display: block;
  height: 22px;
  color: #52f7ff;
  font-family: 'DDIN', Arial, sans-serif;
  font-size: 18px;
  font-weight: 700;
  line-height: 22px;

  &.danger {
    color: #ff4f5c;
  }

  &.warning {
    color: #ff9b35;
  }

  &.yellow {
    color: #ffe45c;
  }
}

.risk-device-point--single {
  .legend-panel {
    max-width: none;
    gap: 3px;
  }

  .legend-item {
    max-width: none;
    font-size: 12px;
  }

  .legend-icon {
    width: 12px;
    height: 12px;
  }

  .legend-name {
    max-width: none;
  }

  .stat-card {
    height: 46px;
    padding: 4px 4px 4px 18px;
  }

  .stat-icon {
    left: 3px;
    top: 5px;
    width: 12px;
    height: 12px;
  }

  .stat-label {
    height: 18px;
    overflow: hidden;
    font-size: 12px;
    line-height: 18px;
    white-space: nowrap;
    text-overflow: ellipsis;
  }

  .stat-value {
    height: auto;
    font-size: 16px;
    line-height: 20px;
  }
}

.flood-action-bar {
  position: relative;
  top: 0px;
  // left: 50%;
  // transform: translateX(-50%);
  display: flex;
  gap: 16px;
  width: max-content;
  pointer-events: auto;
}

.flood-action-btn {
  width: 214px;
  height: 46px;
  box-sizing: border-box;
  border: 1px solid rgba(59, 179, 255, 0.78);
  background:
    linear-gradient(180deg, rgba(12, 111, 181, 0.72) 0%, rgba(3, 67, 126, 0.88) 100%),
    radial-gradient(circle at 20% 0%, rgba(72, 211, 255, 0.35), transparent 50%);
  box-shadow: inset 0 0 16px rgba(56, 178, 255, 0.24), 0 0 8px rgba(35, 153, 255, 0.18);
  color: #f2fbff;
  font-family: 'UISDCBiaoTiHei', Microsoft YaHei, sans-serif;
  font-size: 22px;
  font-weight: 700;
  line-height: 46px;
  letter-spacing: 2px;
  cursor: pointer;
  outline: none;
  transition: border-color 0.18s ease, box-shadow 0.18s ease;

  &:hover {
    border-color: rgba(135, 230, 255, 0.95);
    box-shadow: inset 0 0 16px rgba(56, 178, 255, 0.36), 0 0 14px rgba(35, 153, 255, 0.35);
  }

  &:active {
    background:
      linear-gradient(180deg, rgba(72, 211, 255, 0.85) 0%, rgba(12, 111, 181, 0.92) 100%),
      radial-gradient(circle at 20% 0%, rgba(120, 240, 255, 0.5), transparent 50%);
    box-shadow: inset 0 0 20px rgba(56, 178, 255, 0.5), 0 0 18px rgba(35, 153, 255, 0.45);
  }
}

.flood-action-btn--active {
  background:
    linear-gradient(180deg, rgba(255, 228, 76, 0.92) 0%, rgba(255, 180, 20, 0.88) 100%),
    radial-gradient(circle at 20% 0%, rgba(255, 245, 150, 0.6), transparent 50%);
  border-color: rgba(255, 228, 76, 0.95);
  box-shadow: inset 0 0 16px rgba(255, 200, 40, 0.4), 0 0 14px rgba(255, 180, 20, 0.4);
  color: #f2fbff;
}

.risk-device-point--flood {
  display: block;
  min-height: auto;
  height: auto;

  &.risk-device-point--single .flood-action-btn {
    width: 214px;
    height: 55px;
    font-size: 20px;
    line-height: 55px;
  }
}
</style>
