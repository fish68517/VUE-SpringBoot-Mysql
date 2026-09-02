<template>
  <div class="risk-device-point" :class="{ 'risk-device-point--single': isSingleMode }" :style="riskDeviceStyle">
    <n-checkbox-group
      :value="selectedLayerKeys"
      class="legend-panel"
      @update:value="handleLegendChange"
    >
      <n-checkbox
        v-for="item in legendItems"
        :key="item.key"
        class="legend-item"
        :value="item.key"
        :title="item.label"
      >
        <img class="legend-icon" :src="item.legendIcon || item.mapIcon || item.icon" alt="" />
        <span class="legend-name">{{ item.label }}</span>
      </n-checkbox>
    </n-checkbox-group>

    <section class="panel panel--risk">
      <div class="panel-title" @click="openRiskPointList">风险点位</div>
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
          <span class="stat-value" :class="item.valueClass">{{ formatStatValue(item) }}</span>
        </button>
      </div>
    </section>

    <section class="panel panel--device">
      <div class="panel-title" @click="openPerceptionDeviceList">感知方式</div>
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
          <span class="stat-value" :class="item.valueClass">{{ formatStatValue(item) }}</span>
        </button>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, onMounted, onUnmounted } from 'vue'
import axios from 'axios'
import { NCheckbox, NCheckboxGroup } from 'naive-ui'
import statHiddenDanger from './img/stat_hidden_danger.png'
import statSmallWatershed from './img/stat_small_watershed.png'
import statOldRetainingWall from './img/stat_old_retaining_wall.png'
import statOldSlope from './img/stat_old_slope.png'
import statOldHouse from './img/stat_old_house.png'
import statOldWall from './img/stat_old_wall.png'
import statCliffTrail from './img/stat_cliff_trail.png'
import statGreenSpace from './img/stat_green_space.png'
import statDeformation from './img/stat_deformation.png'
import statDisplacement from './img/stat_displacement.png'
import statRainfall from './img/stat_rainfall.png'
import statCamera from './img/stat_camera.png'
import statGridMember from './img/stat_grid_member.png'
import legendHiddenDanger from './img/legend_hidden_danger.png'
import legendSmallWatershed from './img/legend_small_watershed.png'
import legendOldRetainingWall from './img/legend_old_retaining_wall.png'
import legendOldSlope from './img/legend_old_slope.png'
import legendOldHouse from './img/legend_old_house.png'
import legendOldWall from './img/legend_old_wall.png'
import legendCliffTrail from './img/legend_cliff_trail.png'
import legendGreenSpace from './img/legend_green_space.png'
import legendDeformation from './img/legend_deformation.png'
import legendDisplacement from './img/legend_displacement.png'
import legendRainfall from './img/legend_rainfall.png'
import legendGridMember from './img/legend_grid_member.png'
import mapHiddenDanger from './img/hidden_danger_map.png'
import mapSmallWatershed from './img/small_watershed_map.png'
import mapOldRetainingWall from './img/old_retaining_wall_map.png'
import mapOldSlope from './img/old_slope_map.png'
import mapOldHouse from './img/old_house_map.png'
import mapOldWall from './img/old_wall_map.png'
import mapCliffTrail from './img/cliff_trail_map.png'
import redMapOldRetainingWall from './img/red_old_retaining_wall_map.png'
import redMapOldSlope from './img/red_old_slope_map.png'
import redMapOldHouse from './img/red_old_house_map.png'
import redMapOldWall from './img/red_old_wall_map.png'
import redMapCliffTrail from './img/red_cliff_trail_map.png'
import mapGreenSpace from './img/green_space_map.png'
import mapDeformation from './img/deformation_map.png'
import mapDisplacement from './img/displacement_map.png'
import mapRainfall from './img/rainfall_map.png'
import mapCamera from './img/camera_map.png'
import mapGridMember from './img/grid_member_map.png'
import legendCamera from './img/camera_map2.png'
import geoDeviceAlarmIcon from './img/geo-device-alarm-point.png'
import { greenParkExactFences } from './greenParkFences.js'

type LayerKind = 'risk' | 'device'
type StatItem = {
  key: string
  kind: LayerKind
  label: string
  value: number | string
  num?: number
  icon: string
  legendIcon?: string
  mapIcon?: string
  riskLocType?: string
  valueClass?: string
}
type PointItem = {
  id?: string | number
  pointId?: string | number
  name?: string
  disasterName?: string
  deviceName?: string
  deviceCode?: string
  devId?: string
  devName?: string
  devType?: number
  devTypeName?: string
  deviceNum?: string
  deviceNo?: string
  dvcode?: string
  dvname?: string
  dvjxcode?: string
  modelname?: string
  enfullname?: string
  mountimage?: string
  listImage?: string
  mountAddress?: string
  street?: string
  handleNum?: number
  type?: string
  lng?: string | number
  lat?: string | number
  longitude?: string | number
  latitude?: string | number
  wgs84Longitude?: string | number
  wgs84Latitude?: string | number
  lngWgs84?: string | number
  latWgs84?: string | number
  lon?: string | number
  mapIcon?: string
  personnelId?: string | number
  fullName?: string
  headUrl?: string
  telephone?: string
  unifiedcode?: string
  cityCode?: string
  townCode?: string
  disasterCode?: string
  level?: string
  issatellitenetwork?: number
  __subTypeKey?: string
  __subTypeLabel?: string
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

const sourceName = 'BzGeoRiskDevice'
const eventCallbackMap = new Map<string, Map<Function, Function>>()
const eventBus = {
  on: (event, callback) => {
    const wrappedCallback = ({ source, data }) => {
      if (source === sourceName) return
      callback(data)
    }
    if (!eventCallbackMap.has(event)) eventCallbackMap.set(event, new Map())
    eventCallbackMap.get(event)?.set(callback, wrappedCallback)
    props.bus?.on(event, wrappedCallback)
  },
  off: (event, callback) => {
    const wrappedCallback = eventCallbackMap.get(event)?.get(callback)
    props.bus?.off(event, wrappedCallback || callback)
    eventCallbackMap.get(event)?.delete(callback)
  },
  emit: (event, data) => {
    props.bus?.emit(event, { data, source: sourceName })
  }
}

const isSingleMode = computed(() => props.chartConfig?.option?.containerMode === 'single')
const riskDeviceStyle = computed(() => ({
  '--risk-device-width': isSingleMode.value ? '736px' : '1620px'
}))

const GEO_BASE_URL = 'http://23.99.16.179:11001/api/boot/system/geo'
const OLD_BASE_URL = 'http://23.99.16.179:11001/api/boot/system/old'
const LAND_BASE_URL = 'http://23.99.16.179:11001/api/boot/system'
const TEST_URL = 'http://192.168.112.129:11001/api/boot/system'
const CHECK_LEGEND_EVENT = 'CHECK_GEO_LEGEND'
const TOGGLE_GEO_DEVICE_ALARM_EVENT = 'TOGGLE_GEO_DEVICE_ALARM_LAYER'
const CLOSE_GEO_DEVICE_ALARM_EVENT = 'CLOSE_GEO_DEVICE_ALARM_LAYER'
const selectedLayerKeys = ref<string[]>([])
const pointLayer: Record<string, Cesium.Entity> = {}
const polygonLayer: Record<string, Cesium.Entity> = {}
const geoDeviceAlarmLayer: Record<string, Cesium.Entity> = {}
const layerRequestVersions: Record<string, number> = {}
let shouldShowGeoDeviceAlarm = false
let geoDeviceAlarmRequestVersion = 0
let clickHandler: Cesium.ScreenSpaceEventHandler | null = null
const layerApiMap: Record<string, string> = {
  'risk-hidden-danger': '/land/landGeoHazardPointAll',
  'risk-small-watershed': '/list/small-watershed',
  'device-deformation': '/land/landDevicePoint',
  'device-displacement': '/land/landDevicePoint',
  'device-rainfall': '/land/landDevicePoint',
  'device-camera': '/land/landDevicePoint',
  'device-grid-member': '/land/landQcqfPersonnel'
}

const monitoringDeviceTypeMap: Record<string, string> = {
  'device-deformation': '形变',
  'device-displacement': '位移',
  'device-rainfall': '雨量计',
  'device-camera': '裂缝检测仪'
}

const greenParkPoints = [
  { id: 'eling', name: '鹅岭公园', lng: 106.533842, lat: 29.551261 },
  { id: 'hutouyan', name: '虎头岩公园', lng: 106.510624, lat: 29.557418 },
  { id: 'fotuguan', name: '佛图关公园', lng: 106.517273, lat: 29.554082 },
  { id: 'renmin', name: '人民公园', lng: 106.562431, lat: 29.557844 },
  { id: 'hongyan', name: '红岩公园', lng: 106.497836, lat: 29.552753 },
  { id: 'tiandihu', name: '天地湖公园', lng: 106.504372, lat: 29.550167 },
  { id: 'shanhu', name: '珊瑚公园', lng: 106.554726, lat: 29.549425 },
  { id: 'binjiang', name: '滨江公园', lng: 106.547183, lat: 29.547604 }
]

const greenParkInfoMap: Record<string, any> = {
  eling: { name: '鹅岭公园', deviceCode: '50010300106001000165', manager: '陈俊', address: '鹅岭正街176号' },
  fotuguan: { name: '佛图关公园', deviceCode: '50010300106001000152', manager: '封逆', address: '长江一路' },
  renmin: { name: '人民公园', manager: '刘素芳', address: '人民路' },
  hongyan: { name: '红岩公园', deviceCode: '50010300106001000184', manager: '肖镇', address: '红岩村' },
  tiandihu: { name: '天地湖公园', deviceCode: '50010300056001023000', manager: '肖镇', address: '天地湖' },
  hutouyan: { name: '虎头岩公园', deviceCode: '50010300106001000135', manager: '刘东', address: '虎头岩' },
  shanhu: { name: '珊瑚公园', deviceCode: '50010300056004000918', manager: '梁雪岗', address: '珊瑚路' },
  binjiang: { name: '滨江公园', deviceCode: '50010300056001025502', manager: '梁雪岗', address: '滨江路' }
}

const riskStats = ref<StatItem[]>([
  { key: 'risk-hidden-danger', kind: 'risk', label: '地灾隐患点', value: 15, icon: statHiddenDanger, legendIcon: legendHiddenDanger, mapIcon: mapHiddenDanger },
  { key: 'risk-small-watershed', kind: 'risk', label: '小流域', value: 22, icon: statSmallWatershed, legendIcon: legendSmallWatershed, mapIcon: mapSmallWatershed },
  { key: 'risk-four-old-trail', kind: 'risk', label: '四老一步道', value: 0, icon: statOldRetainingWall, legendIcon: legendOldRetainingWall, mapIcon: mapOldRetainingWall },
  { key: 'risk-green-space', kind: 'risk', label: '绿地', value: 8, icon: statGreenSpace, legendIcon: legendGreenSpace, mapIcon: mapGreenSpace }
])

// 四老一步道子类型定义（用于撒点时按子类型区分图标）
const fourOldTrailSubTypes = [
  { key: 'risk-old-retaining-wall', label: '老堡坎', riskLocType: 'retaining_wall', mapIcon: mapOldRetainingWall, disasterMapIcon: redMapOldRetainingWall },
  { key: 'risk-old-slope', label: '老边坡', riskLocType: 'slope', mapIcon: mapOldSlope, disasterMapIcon: redMapOldSlope },
  { key: 'risk-old-house', label: '老房屋', riskLocType: 'old_house', mapIcon: mapOldHouse, disasterMapIcon: redMapOldHouse },
  { key: 'risk-old-wall', label: '老围墙', riskLocType: 'wall', mapIcon: mapOldWall, disasterMapIcon: redMapOldWall },
  { key: 'risk-cliff-trail', label: '邻崖步道', riskLocType: 'trail', mapIcon: mapCliffTrail, disasterMapIcon: redMapCliffTrail }
]
const fourOldTrailSubTypeMap = fourOldTrailSubTypes.reduce((map, item) => {
  map[item.riskLocType] = item
  return map
}, {} as Record<string, typeof fourOldTrailSubTypes[number]>)

const deviceStats = ref<StatItem[]>([
  { key: 'device-deformation', kind: 'device', label: '形变', value: 19, icon: statDeformation, legendIcon: legendDeformation, mapIcon: mapDeformation },
  { key: 'device-displacement', kind: 'device', label: '位移', value: 22, icon: statDisplacement, legendIcon: legendDisplacement, mapIcon: mapDisplacement },
  { key: 'device-rainfall', kind: 'device', label: '雨量计', value: 22, icon: statRainfall, legendIcon: legendRainfall, mapIcon: mapRainfall },
  { key: 'device-camera', kind: 'device', label: '裂缝检测仪', value: 0, icon: statCamera, legendIcon: legendCamera, mapIcon: mapCamera },
  { key: 'device-grid-member', kind: 'device', label: '四重网格员', value: 32, icon: statGridMember, legendIcon: legendGridMember, mapIcon: mapGridMember }
])

const legendItems = computed(() => [...riskStats.value, ...deviceStats.value])

const getPointLng = (item: PointItem) => Number(item.wgs84Longitude ?? item.lngWgs84 ?? item.longitude ?? item.lng ?? item.lon)
const getPointLat = (item: PointItem) => Number(item.wgs84Latitude ?? item.latWgs84 ?? item.latitude ?? item.lat)

const isGeoDisasterPoint = (point: any) => String(point?.isGeoDisaster ?? '').trim() === '是'

const getPropertyValue = (value: any) => {
  if (value && typeof value.getValue === 'function') {
    return value.getValue(typeof Cesium !== 'undefined' ? Cesium.JulianDate.now() : undefined)
  }
  return value
}

const getEntityProperties = (entity: any) => {
  const properties = entity?.properties
  if (!properties) return {}
  return getPropertyValue(properties) || properties
}

const ensureMapClickHandler = () => {
  if (clickHandler || !window.viewer || typeof Cesium === 'undefined') return

  clickHandler = new Cesium.ScreenSpaceEventHandler(window.viewer.scene.canvas)
  clickHandler.setInputAction((movement: any) => {
    const picked = window.viewer?.scene?.pick(movement.position)
    const properties = getEntityProperties(picked?.id)
    const pointType = getPropertyValue(properties.type)
    const layerKey = getPropertyValue(properties.layerKey)
    const data = getPropertyValue(properties.data) || {}

    if (pointType === 'riskDevicePolygon') {
      if (layerKey === 'risk-green-space') {
        openGreenParkVideo(data)
        return
      }

      eventBus.emit('OPEN_GEO_KPI_DIA', {
        diaName: 'geo-point-detail',
        data,
        pointType: 'riskArea',
        showTrend: false
      })
      return
    }

    if (pointType === 'geoDeviceAlarmPoint') {
      eventBus.emit('OPEN_GEO_KPI_DIA', {
        diaName: 'geo-point-detail',
        data,
        pointType: 'geoWarnAlarm',
        showTrend: true
      })
      return
    }

    if (pointType !== 'riskDevicePoint') return

    if (monitoringDeviceTypeMap[layerKey]) {
      eventBus.emit('OPEN_GEO_KPI_DIA', {
        diaName: 'geo-point-detail',
        data,
        pointType: 'alarm',
        showTrend: true
      })
      return
    }

    if (layerKey === 'risk-hidden-danger') {
      const token = window.localStorage.getItem('dcqc-tk') || ''
      const rawUrl = data?.addressUrl || ''
      if (rawUrl) {
        const separator = rawUrl.includes('?') ? '&' : '?'
        eventBus.emit('OPEN_IFRAME_DIA', {
          url: token ? `${rawUrl}${separator}dcqc-tk=${token}` : rawUrl,
          pointName: data?.name || '',
          title: data?.name || '地灾隐患点详情',
          type: 'geo-disaster-detail',
          headerBg: 'xiaofangtitle',
          closeGeoPopup: true,
          sourceDiaName: 'risk-hidden-danger'
        })
      } else {
        eventBus.emit('OPEN_GEO_KPI_DIA', {
          diaName: 'geo-point-detail',
          data,
          pointType: 'hazard',
          showTrend: false
        })
      }
      return
    }

    if (layerKey === 'risk-four-old-trail') {
      const subTypeLabel = getPropertyValue(properties.__subTypeLabel) || '四老一步道'
      eventBus.emit('OPEN_GEO_KPI_DIA', {
        diaName: 'geo-old-risk-point-detail',
        data,
        layerName: subTypeLabel
      })
      return
    }

    const riskLocItem = riskStats.value.find(item => item.key === layerKey && item.riskLocType)
    if (riskLocItem) {
      eventBus.emit('OPEN_GEO_KPI_DIA', {
        diaName: 'geo-old-risk-point-detail',
        data,
        layerName: riskLocItem.label
      })
      return
    }

    if (layerKey === 'device-grid-member') {
      eventBus.emit('OPEN_GEO_KPI_DIA', {
        diaName: 'geo-grid-member-detail',
        data
      })
    }
  }, Cesium.ScreenSpaceEventType.LEFT_CLICK)
}

function openGreenParkVideo(data: any) {
  const park = greenParkInfoMap[data?.id] || data || {}
  if (!park.deviceCode) {
    console.warn(`${park.name || data?.name || '绿地公园'} 缺少 deviceCode，无法查看摄像头`)
    return
  }

  eventBus.emit('YZVideoPop:openPop', {
    '设备编码': park.deviceCode,
    '所属区县': '渝中区',
    '感知类型': '绿地防火',
    '设备名称': park.name || data?.name || '-',
    '所属单位': park.manager || '-',
    '设备风险点': park.address || '-',
    '设备状态': '启用',
    deviceIds: [park.deviceCode]
  })
}

const clearPointLayer = () => {
  Object.keys(pointLayer).forEach(key => {
    const entity = pointLayer[key]
    if (entity) {
      window.viewer?.entities?.remove(entity)
    }
    delete pointLayer[key]
  })
}

const clearPolygonLayer = () => {
  Object.keys(polygonLayer).forEach(key => {
    const entity = polygonLayer[key]
    if (entity) {
      window.viewer?.entities?.remove(entity)
    }
    delete polygonLayer[key]
  })
}

/** 移除 BzGeo 设备告警入口创建的全部告警点实体。 */
const clearGeoDeviceAlarmLayer = () => {
  Object.keys(geoDeviceAlarmLayer).forEach(key => {
    const entity = geoDeviceAlarmLayer[key]
    if (entity) {
      window.viewer?.entities?.remove(entity)
    }
    delete geoDeviceAlarmLayer[key]
  })
}

/** 关闭设备告警点图层，并使尚未完成的请求失效。 */
const closeGeoDeviceAlarmLayer = () => {
  shouldShowGeoDeviceAlarm = false
  geoDeviceAlarmRequestVersion += 1
  clearGeoDeviceAlarmLayer()
}

/** 从设备告警接口返回中提取今日设备告警点位数组。 */
const getGeoDeviceAlarmRecords = (responseData: any) => {
  const result = responseData?.data ?? responseData
  if (Array.isArray(result?.todayDeviceAlarm)) return result.todayDeviceAlarm
  if (Array.isArray(result)) return result
  return result?.content || result?.records || result?.list || result?.rows || []
}

/** 请求地灾设备告警数据，并在 Cesium 地图创建告警点。 */
const renderGeoDeviceAlarmLayer = async () => {
  clearGeoDeviceAlarmLayer()
  if (!window.viewer || typeof Cesium === 'undefined') return
  const requestVersion = ++geoDeviceAlarmRequestVersion

  try {
    const res = await axios.get(`${LAND_BASE_URL}/land/geoWarn`)
    const list = getGeoDeviceAlarmRecords(res?.data)
    if (!Array.isArray(list)) return
    if (!shouldShowGeoDeviceAlarm || requestVersion !== geoDeviceAlarmRequestVersion) return

    list.forEach((point, index) => {
      const lng = getPointLng(point)
      const lat = getPointLat(point)
      if (!Number.isFinite(lng) || !Number.isFinite(lat)) return

      const key = `geo_device_alarm_${point.deviceId ?? point.devId ?? point.id ?? point.deviceCode ?? 'point'}_${index}`
      geoDeviceAlarmLayer[key] = window.viewer.entities.add({
        position: Cesium.Cartesian3.fromDegrees(lng, lat, 360),
        billboard: {
          image: geoDeviceAlarmIcon,
          width: 15,
          height: 15,
          verticalOrigin: Cesium.VerticalOrigin.BOTTOM,
          disableDepthTestDistance: Number.POSITIVE_INFINITY
        },
        properties: new Cesium.PropertyBag({
          type: 'geoDeviceAlarmPoint',
          data: point
        })
      })
    })
    ensureMapClickHandler()
  } catch (error) {
    console.error('获取地灾设备告警点位失败:', error)
    clearGeoDeviceAlarmLayer()
  }
}

/** 根据 BzGeo 的触发事件切换或选中设备告警点图层显隐。 */
const toggleGeoDeviceAlarmLayer = () => {
  shouldShowGeoDeviceAlarm = !shouldShowGeoDeviceAlarm
  if (!shouldShowGeoDeviceAlarm) {
    geoDeviceAlarmRequestVersion += 1
    clearGeoDeviceAlarmLayer()
    return
  }
  renderGeoDeviceAlarmLayer()
}

/**
 * 清理组件创建的全部地图资源。
 * 卸载时先递增请求版本，使尚未完成的异步请求失效，再移除点、面实体和点击监听。
 */
/** 判断 Cesium 实体是否为本组件创建的点、面或设备告警图层实体。 */
const isOwnedMapEntity = (entity: any) => {
  const properties = getEntityProperties(entity)
  const type = getPropertyValue(properties.type)
  return ['riskDevicePoint', 'riskDevicePolygon', 'geoDeviceAlarmPoint'].includes(type)
}

/** 兜底移除本组件创建但可能未被图层字典记录到的 Cesium 实体。 */
const removeOwnedEntitiesFromViewer = () => {
  const entities = window.viewer?.entities
  const values = entities?.values
  if (!entities || !Array.isArray(values)) return

  values
    .filter(isOwnedMapEntity)
    .forEach(entity => entities.remove(entity))
}

const clearAllMapLayers = () => {
  legendItems.value.forEach(item => {
    layerRequestVersions[item.key] = (layerRequestVersions[item.key] || 0) + 1
  })
  geoDeviceAlarmRequestVersion += 1
  shouldShowGeoDeviceAlarm = false
  selectedLayerKeys.value = []
  clearPointLayer()
  clearPolygonLayer()
  clearGeoDeviceAlarmLayer()
  removeOwnedEntitiesFromViewer()

  if (clickHandler) {
    clickHandler.destroy()
    clickHandler = null
  }
}

const removeLayerByKey = (layerKey: string) => {
  layerRequestVersions[layerKey] = (layerRequestVersions[layerKey] || 0) + 1
  console.log(`[removeLayerByKey] 开始清除图层: ${layerKey}`)
  console.log(`[removeLayerByKey] 当前 pointLayer keys:`, Object.keys(pointLayer))
  console.log(`[removeLayerByKey] 当前 polygonLayer keys:`, Object.keys(polygonLayer))
  // 清除点图层
  Object.keys(pointLayer).forEach(key => {
    if (!key.startsWith(`${layerKey}_`)) return

    const entity = pointLayer[key]
    if (entity) {
      window.viewer?.entities?.remove(entity)
      console.log(`[removeLayerByKey] 已移除点图层 entity: ${key}`)
    }
    delete pointLayer[key]
  })
  // 清除面图层
  Object.keys(polygonLayer).forEach(key => {
    if (!key.startsWith(`${layerKey}_`)) return

    const entity = polygonLayer[key]
    if (entity) {
      window.viewer?.entities?.remove(entity)
      console.log(`[removeLayerByKey] 已移除面图层 entity: ${key}`)
    }
    delete polygonLayer[key]
  })
}

/** 解析 coordinates 数组为 Cesium 多边形坐标数组 */
const parseCoordinatesToPositions = (coordinates: { lng: number; lat: number }[]): Cesium.Cartesian3[] | null => {
  if (!Array.isArray(coordinates) || coordinates.length < 3) return null
  return coordinates.map(c => Cesium.Cartesian3.fromDegrees(Number(c.lng), Number(c.lat), 360))
}

/**
 * 根据经纬度和半径生成圆形多边形坐标
 * @param centerLng 中心经度
 * @param centerLat 中心纬度
 * @param radiusMeter 半径（米）
 * @param sides 边数，默认32边
 */
const createCirclePolygon = (centerLng: number, centerLat: number, radiusMeter: number, sides = 32) => {
  const positions = []
  const earthRadius = 6378137 // 地球半径，米
  const radLat = (centerLat * Math.PI) / 180
  const radLng = (centerLng * Math.PI) / 180
  const distance = radiusMeter / earthRadius

  for (let i = 0; i <= sides; i++) {
    const theta = (i * 2 * Math.PI) / sides
    const lat = Math.asin(Math.sin(radLat) * Math.cos(distance) + Math.cos(radLat) * Math.sin(distance) * Math.cos(theta))
    const lng = radLng + Math.atan2(Math.sin(theta) * Math.sin(distance) * Math.cos(radLat), Math.cos(distance) - Math.sin(radLat) * Math.sin(lat))
    positions.push({
      lng: (lng * 180) / Math.PI,
      lat: (lat * 180) / Math.PI
    })
  }
  return positions
}

/** 解析 geom JSON 字符串为 Cesium 多边形坐标数组 */
const parseGeomToPositions = (geomStr: string): Cesium.Cartesian3[] | null => {
  try {
    const feature = JSON.parse(geomStr)
    if (feature?.geometry?.type === 'Polygon') {
      const coords = feature.geometry.coordinates[0]
      if (Array.isArray(coords) && coords.length >= 3) {
        return coords.map((c: number[]) => Cesium.Cartesian3.fromDegrees(c[0], c[1], 200))
      }
    }
    return null
  } catch {
    return null
  }
}

const getResponseList = (data: any, item?: StatItem) => {
  const result = data?.data ?? data
  const mapIcon = item?.mapIcon || item?.icon

  let list: PointItem[] = []

  if (Array.isArray(result)) list = result
  if (Array.isArray(result?.content)) list = result.content
  if (Array.isArray(result?.records)) list = result.records
  if (Array.isArray(result?.list)) list = result.list
  if (Array.isArray(result?.rows)) list = result.rows

  return list.map(item => ({
    ...item,
    name: item.name ?? item.disasterName ?? item.deviceName ?? item.devName ?? item.dvname ?? item.fullName,
    deviceName: item.deviceName ?? item.devName ?? item.dvname ?? item.fullName,
    deviceCode: item.deviceCode ?? item.devId ?? item.deviceNum ?? item.deviceNo ?? item.dvcode ?? item.personnelId,
    lng: item.lng ?? item.wgs84Longitude ?? item.longitude ?? item.lon,
    lat: item.lat ?? item.wgs84Latitude ?? item.latitude,
    mapIcon
  }))
}

const lengthValueKeys = new Set(['risk-hidden-danger', 'risk-small-watershed'])

const updateStatValue = (key: string, value: number) => {
  const stat = legendItems.value.find(item => item.key === key)
  if (stat) {
    stat.value = value
  }
}

/**
 * 获取风险点位统计数量，并更新 riskStats。
 * 统计值统一以 landRisk 接口为准，不使用点位列表长度覆盖。
 */
const updateRiskStatValues = async () => {
  const fieldKeyMap: Record<string, string> = {
    disasterHiddenPoint: 'risk-hidden-danger',
    smallWatershed: 'risk-small-watershed'
  }
  try {
    const res = await axios.get(`${LAND_BASE_URL}/land/landRisk`)
    const data = res?.data?.data ?? res?.data ?? {}
    Object.entries(fieldKeyMap).forEach(([field, key]) => {
      const value = Number(data?.[field])
      if (Number.isFinite(value)) updateStatValue(key, value)
    })
  } catch (error) {
    console.error('获取地灾风险统计数量失败:', error)
  }

  await updateOldRiskStatValues()
  updateStatValue('risk-green-space', greenParkPoints.length)
}

const updateOldRiskStatValues = async () => {
  try {
    const totalCount = await getRiskPointCount()
    updateStatValue('risk-four-old-trail', totalCount)
  } catch (error) {
    console.error('获取四老一步道风险点数量失败:', error)
    updateStatValue('risk-four-old-trail', 0)
  }
}

/**
 * 获取感知设备统计数量，并更新 deviceStats 的 value。
 * 四重网格员 value 表示总人数，num 表示具有地图图层的人数。
 */
const updateDeviceStatValues = async () => {
  const fieldKeyMap: Record<string, string> = {
    deformation: 'device-deformation',
    displacement: 'device-displacement',
    rainGauge: 'device-rainfall',
    crackMonitor: 'device-camera',
    fourGridWorker: 'device-grid-member'
  }
  try {
    const res = await axios.get(`${LAND_BASE_URL}/land/landDevice`)
    const data = res?.data?.data ?? res?.data ?? {}
    Object.entries(fieldKeyMap).forEach(([field, key]) => {
      const value = Number(data?.[field])
      if (Number.isFinite(value)) updateStatValue(key, value)
    })
  } catch (error) {
    console.error('获取地灾感知设备统计数量失败:', error)
  }
}

/**
 * 格式化统计数量。
 * 存在 num 时展示“全部数量 (可落图数量)”，否则只展示原 value。
 */
const formatStatValue = (item: StatItem) => {
  return item.num === undefined ? item.value : `${item.value} (${item.num})`
}

/**
 * 获取四重网格员可落图点位数量。
 * gridPersonnelPoint 表示具有地图坐标、可以生成图层的网格员数量。
 */
const updateGridPersonnelPointValue = async () => {
  try {
    const res = await axios.get(`${LAND_BASE_URL}/land/gridPatrol`)
    const data = res?.data?.data ?? res?.data ?? {}
    const num = Number(data?.gridPersonnelPoint)
    const stat = deviceStats.value.find(item => item.key === 'device-grid-member')
    if (stat) stat.num = Number.isFinite(num) ? num : 0
  } catch (error) {
    console.error('获取四重网格员图层数量失败:', error)
    const stat = deviceStats.value.find(item => item.key === 'device-grid-member')
    if (stat) stat.num = 0
  }
}

/**
 * 请求地灾监测设备点。
 * 形变、位移、雨量计和裂缝检测仪使用同一接口，通过 deviceType 区分。
 */
const getMonitoringDevicePoints = async (item: StatItem) => {
  const deviceType = monitoringDeviceTypeMap[item.key]
  const res = await axios.post(`${LAND_BASE_URL}/land/landDevicePoint`, {
    deviceType
  })
  return getResponseList(res?.data, item)
}

const getRiskLocPoints = async () => {
  const res = await axios.get(`${OLD_BASE_URL}/risk/loc`)
  return getResponseList(res?.data)
}

const getRiskPointCount = async () => {
  const res = await axios.get(`${OLD_BASE_URL}/risk/point`)
  const value = Number(res?.data?.data ?? res?.data ?? 0)
  return Number.isFinite(value) ? value : 0
}

/** 根据图层配置请求对应的点或面数据。 */
const getLayerApiPoints = async (item: StatItem) => {
  if (item.key === 'risk-green-space') {
    updateStatValue(item.key, greenParkPoints.length)
    return {
      __circlePointData: greenParkPoints
    }
  }

  // 四老一步道：接口不传 type，按返回 riskType 匹配对应图层图标。
  if (item.key === 'risk-four-old-trail') {
    const list = await getRiskLocPoints()
    const allPoints = list
      .map(point => {
        const subType = fourOldTrailSubTypeMap[String(point?.riskType || '')]
        if (!subType) return null
        return {
          ...point,
          mapIcon: isGeoDisasterPoint(point) ? subType.disasterMapIcon : subType.mapIcon,
          __subTypeKey: subType.key,
          __subTypeLabel: subType.label
        }
      })
      .filter(Boolean)
    return allPoints
  }

  const api = layerApiMap[item.key]
  if (!api) return []

  try {
    if (monitoringDeviceTypeMap[item.key]) {
      return await getMonitoringDevicePoints(item)
    }

    const res = item.key === 'device-grid-member'
      ? await axios.get(`${LAND_BASE_URL}${api}`)
      : item.key === 'risk-hidden-danger'
      ? await axios.get(`${LAND_BASE_URL}${api}`, {
        params: {
          Index: 1,
          size: 9999
        }
      })
      : await axios.post(`${GEO_BASE_URL}${api}`, {
        pageNumber: 1,
        pageSize: 9999
      })
    return getResponseList(res?.data, item)
  } catch (error) {
    console.error('获取风险点/设备点位失败:', error)
    return []
  }
}

const getLayerPoints = async (item: StatItem) => {
  return getLayerApiPoints(item)
}

const addPointLayer = async (item: StatItem) => {
  removeLayerByKey(item.key)
  const requestVersion = layerRequestVersions[item.key]
  const layerData: any = await getLayerPoints(item)
  if (
    layerRequestVersions[item.key] !== requestVersion
    || !selectedLayerKeys.value.includes(item.key)
  ) return

  // 面图层必须在异步请求有效且图例仍选中时才允许渲染。
  if (layerData?.__polygonData !== undefined) {
    addPolygonLayerFromData(layerData.__polygonData, item)
    return
  }

  // 绿地圆形围栏图层
  if (layerData?.__circlePointData !== undefined && item.key === 'risk-green-space') {
    addGreenSpaceCircleLayer(layerData.__circlePointData, item)
    return
  }

  if (!window.viewer || typeof Cesium === 'undefined' || !Array.isArray(layerData)) return

  layerData.forEach((point, index) => {
    const lng = getPointLng(point)
    const lat = getPointLat(point)

    if (!Number.isFinite(lng) || !Number.isFinite(lat)) return

    const key = `${item.key}_${point.pointId ?? point.id ?? point.devId ?? point.deviceCode ?? point.dvcode ?? 'point'}_${index}`
    const entity = window.viewer.entities.add({
      position: Cesium.Cartesian3.fromDegrees(lng, lat, 360),
      billboard: {
        image: point.mapIcon || item.mapIcon || item.icon,
        width: 15,
        height: 15,
        verticalOrigin: Cesium.VerticalOrigin.BOTTOM,
        disableDepthTestDistance: Number.POSITIVE_INFINITY
      },
      properties: new Cesium.PropertyBag({
        type: 'riskDevicePoint',
        layerKey: item.key,
        layerName: item.label,
        __subTypeKey: point.__subTypeKey,
        __subTypeLabel: point.__subTypeLabel,
        data: point
      })
    })

    pointLayer[key] = entity
  })
}

/** 从数据渲染绿地圆形围栏图层 */
const addGreenSpaceCircleLayer = (data: any[], item: StatItem) => {
  if (!window.viewer || typeof Cesium === 'undefined') {
    console.warn(`[${item.label}] viewer不可用，无法渲染圆形围栏图层`)
    return []
  }
  if (!Array.isArray(data)) {
    console.warn(`[${item.label}] 数据不是数组，无法渲染圆形围栏图层:`, data)
    return []
  }

  removeLayerByKey(item.key)

  let renderCount = 0
  data.forEach((row, index) => {
    const lng = getPointLng(row)
    const lat = getPointLat(row)
    if (!Number.isFinite(lng) || !Number.isFinite(lat)) return

    // 只渲染鹅岭公园和虎头岩公园的电子围栏，其他公园不渲染
    const exactFence = greenParkExactFences[row?.id]
    if (!exactFence) return
    const positions = parseCoordinatesToPositions(exactFence)
    if (!positions) return

    // 计算围栏中心点坐标
    let sumLng = 0, sumLat = 0
    exactFence.forEach(c => { sumLng += c.lng; sumLat += c.lat })
    const centerLng = sumLng / exactFence.length
    const centerLat = sumLat / exactFence.length

    const id = row?.id ?? index
    const key = `${item.key}_circle_${id}`

    // 渲染圆形电子围栏
    const polygonEntity = window.viewer.entities.add({
      name: row?.name ?? `${item.label}-${index}`,
      polygon: {
        hierarchy: new Cesium.PolygonHierarchy(positions),
        material: Cesium.Color.fromCssColorString('rgba(54, 255, 139, 0.28)'),
        outline: true,
        outlineColor: Cesium.Color.fromCssColorString('rgba(54, 255, 139, 0.9)'),
        outlineWidth: 2,
        height: 360,
        perPositionHeight: true
      },
      properties: new Cesium.PropertyBag({
        type: 'riskDevicePolygon',
        layerKey: item.key,
        layerName: item.label,
        data: row
      })
    })

    // 渲染公园名称标签（跟随地图缩放）
    const labelEntity = window.viewer.entities.add({
      position: Cesium.Cartesian3.fromDegrees(centerLng, centerLat, 370),
      label: {
        text: row?.name ?? '',
        font: 'bold 24px sans-serif',
        fillColor: Cesium.Color.fromCssColorString('#ffffff'),
        style: Cesium.LabelStyle.FILL,
        verticalOrigin: Cesium.VerticalOrigin.CENTER,
        horizontalOrigin: Cesium.HorizontalOrigin.CENTER,
        disableDepthTestDistance: Number.POSITIVE_INFINITY,
        // 跟随地图缩放：近距离放大，远距离缩小
        scaleByDistance: new Cesium.NearFarScalar(500, 1.2, 5000, 0.5)
      }
    })

    polygonLayer[`${key}_polygon`] = polygonEntity
    polygonLayer[`${key}_label`] = labelEntity
    renderCount++
  })
  console.log(`[${item.label}] 圆形围栏图层渲染完成，共 ${renderCount} 个围栏`)
  return []
}

/** 从数据渲染面图层 */
const addPolygonLayerFromData = (data: any[], item: StatItem) => {
  if (!window.viewer || typeof Cesium === 'undefined') {
    console.warn(`[${item.label}] viewer不可用，无法渲染面图层`)
    return []
  }
  if (!Array.isArray(data)) {
    console.warn(`[${item.label}] 数据不是数组，无法渲染面图层:`, data)
    return []
  }

  removeLayerByKey(item.key)

  let renderCount = 0
  data.forEach((row, index) => {
    const coordinates = row?.coordinates
    if (!coordinates) return

    const positions = parseCoordinatesToPositions(coordinates)
    if (!positions) {
      console.warn(`[${item.label}] 第${index}项 coordinates 解析失败:`, coordinates)
      return
    }

    const id = row?.id ?? row?.OBJECTID ?? index
    const key = `${item.key}_polygon_${id}`

    const entity = window.viewer.entities.add({
      name: row?.DLWZ ?? row?.name ?? `${item.label}-${index}`,
      polygon: {
        hierarchy: new Cesium.PolygonHierarchy(positions),
        material: new Cesium.ColorMaterialProperty(
          item.key === 'risk-green-space'
            ? Cesium.Color.fromCssColorString('rgba(54, 255, 139, 0.28)')
            : Cesium.Color.fromCssColorString('rgba(255, 155, 53, 0.25)')
        ),
        outline: true,
        outlineColor: item.key === 'risk-green-space'
          ? Cesium.Color.fromCssColorString('rgba(54, 255, 139, 0.9)')
          : Cesium.Color.fromCssColorString('rgba(255, 155, 53, 0.8)'),
        outlineWidth: 2,
        height: 360,
        perPositionHeight: true
      },
      properties: new Cesium.PropertyBag({
        type: 'riskDevicePolygon',
        layerKey: item.key,
        layerName: item.label,
        data: row
      })
    })

    polygonLayer[key] = entity
    renderCount++
  })
  console.log(`[${item.label}] 面图层渲染完成，共 ${renderCount} 个多边形`)
  return []
}

const handleLegendChange = async (keys: string[]) => {
  const prevKeys = selectedLayerKeys.value
  const addedKeys = keys.filter(key => !prevKeys.includes(key))
  const removedKeys = prevKeys.filter(key => !keys.includes(key))
  console.log(`[handleLegendChange] prev:`, prevKeys, `new:`, keys, `removed:`, removedKeys, `added:`, addedKeys)

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
  eventBus.emit('OPEN_GEO_KPI_DIA', {
    diaName: 'geo-risk-point-list'
  })
}

const openPerceptionDeviceList = () => {
  eventBus.emit('OPEN_GEO_KPI_DIA', {
    diaName: 'geo-perception-device-list'
  })
}

const toggleLayer = async (key: string) => {
  const newKeys = selectedLayerKeys.value.includes(key)
    ? selectedLayerKeys.value.filter(selectedKey => selectedKey !== key)
    : [...selectedLayerKeys.value, key]

  await handleLegendChange(newKeys)
}

onMounted(() => {
  ensureMapClickHandler()
  updateRiskStatValues()
  updateDeviceStatValues()
  updateGridPersonnelPointValue()
})

onUnmounted(() => {
  eventBus.off(TOGGLE_GEO_DEVICE_ALARM_EVENT, toggleGeoDeviceAlarmLayer)
  eventBus.off(CLOSE_GEO_DEVICE_ALARM_EVENT, closeGeoDeviceAlarmLayer)
  clearAllMapLayers()
  eventBus.off(CHECK_LEGEND_EVENT, handleCheckLegend)
})

/**
 * 监听外部图层切换事件。
 * toggle 会同步切换图层显隐与图例勾选状态，select 只负责选中并落图。
 */
const handleCheckLegend = async (data: { key: string; action?: 'select' | 'toggle' }) => {
  const key = data?.key
  if (!key || !legendItems.value.find(item => item.key === key)) return

  if (data.action === 'toggle') {
    await toggleLayer(key)
    return
  }

  // select 模式下已选中的图层不重复请求和渲染。
  if (selectedLayerKeys.value.includes(key)) return

  const newKeys = [...selectedLayerKeys.value, key]
  await handleLegendChange(newKeys)
}

onMounted(() => {
  eventBus.on(CHECK_LEGEND_EVENT, handleCheckLegend)
  eventBus.on(TOGGLE_GEO_DEVICE_ALARM_EVENT, toggleGeoDeviceAlarmLayer)
  eventBus.on(CLOSE_GEO_DEVICE_ALARM_EVENT, closeGeoDeviceAlarmLayer)
})
</script>

<script lang="ts">
export default {
  name: 'BzGeoRiskDevice',
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
  pointer-events: auto;
  border: 1px solid rgba(59, 179, 255, 0.78);
  background:
    linear-gradient(180deg, rgba(12, 111, 181, 0.72) 0%, rgba(3, 67, 126, 0.88) 100%),
    radial-gradient(circle at 20% 0%, rgba(72, 211, 255, 0.35), transparent 50%);
  box-shadow: inset 0 0 16px rgba(56, 178, 255, 0.24), 0 0 8px rgba(35, 153, 255, 0.18);
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
  top:225px;
  min-width: 0;
  height: auto;
}

.panel-title {
  height: 50px;
  padding-left: 40px;
  box-sizing: border-box;
  background: url('./img/bg_second_title.png') left center / 100% 100% no-repeat;
  color: #f2fbff;
  font-family: 'UISDCBiaoTiHei', 'Microsoft YaHei', sans-serif;
  font-size: 24px;
  font-weight: 700;
  line-height: 50px;
  letter-spacing: 0;
  text-shadow: 0 0 10px rgba(92, 217, 255, 0.72);
  background-size: 100% auto;
  cursor: pointer;
  pointer-events: auto;
}

.stat-row {
  margin-top: 10px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  justify-content: flex-start;
  align-content: start;
}

.stat-card {
  position: relative;
  flex: 0 0 calc((100% - 30px) / 4);
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

.panel--device {
  .stat-card {
    flex-basis: calc((100% - 40px) / 5);
  }
}

.panel--risk {
  .stat-card {
    flex-basis: calc((100% - 30px) / 4);
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
  .panel-title {
    height: 32px;
    line-height: 32px;
    background-size: auto;
  }

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
    flex-basis: calc((100% - 20px) / 3);
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
</style>
