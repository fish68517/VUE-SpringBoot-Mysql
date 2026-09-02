<template>
  <div class="risk-device-point" :class="{ 'risk-device-point--single': isSingleMode }" :style="riskDeviceStyle">
    <n-checkbox-group
      :value="selectedLayerKeys"
      class="legend-panel"
      @update:value="handleLegendChange"
    >
      <template v-for="item in legendItems" :key="item.key">
        <n-checkbox
          class="legend-item"
          :value="item.key"
          :title="item.label"
        >
          <img class="legend-icon" :src="item.legendIcon || item.mapIcon || item.icon" alt="" />
          <span class="legend-name">{{ item.label }}</span>
        </n-checkbox>
        <!-- 高层建筑子图例：使用原生label避免n-checkbox-group干扰checked状态 -->
        <template v-if="item.key === 'risk-high-building' && selectedLayerKeys.includes('risk-high-building')">
          <label
            v-for="sub in highBuildingSubItems"
            :key="sub.key"
            class="legend-item legend-item--sub"
            :title="sub.label"
          >
            <input
              type="checkbox"
              class="legend-sub-checkbox"
              :checked="selectedSubKeys.includes(sub.key)"
              @change="(e: Event) => handleSubLegendChange(sub.key, (e.target as HTMLInputElement).checked)"
            />
            <img class="legend-icon" :src="sub.icon" alt="" />
            <span class="legend-name">{{ sub.label }}</span>
          </label>
        </template>
      </template>
    </n-checkbox-group>

    <section class="panel">
      <div class="panel-title" @click="openAllRiskPointList">风险点位</div>
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
          <span class="stat-value" :class="item.valueClass">{{ item.value }}</span>
        </button>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, onMounted, onUnmounted } from 'vue'
import axios from 'axios'
import { NCheckbox, NCheckboxGroup } from 'naive-ui'
import statHighBuilding from './img/stat_high_building.png'
import statNineSmall from './img/stat_nine_small.png'
import statGasMileage from './img/stat_gas_mileage.png'
import statSepticTank from './img/stat_septic_tank.png'
import statWaterPressure from './img/stat_water_pressure.png'
import statLifeChannel from './img/stat_life_channel.png'
import statFireControlRoom from './img/stat_fire_control_room.png'
import statGasDevice from './img/stat_gas_device.png'
import legendHighBuilding from './img/legend_high_building.png'
import legendNineSmall from './img/legend_nine_small.png'
import legendGasMileage from './img/legend_gas_mileage.png'
import legendSepticTank from './img/legend_septic_tank.png'
import legendWaterPressure from './img/legend_water_pressure.png'
import legendLifeChannel from './img/legend_life_channel.png'
import legendFireControlRoom from './img/legend_fire_control_room.png'
import legendGasDevice from './img/legend_gas_device.png'
import mapHighBuilding from './img/high_building_map.png'
import mapNineSmall from './img/nine_small_map.png'
import mapGasMileage from './img/gas_mileage_map.png'
import mapSepticTank from './img/septic_tank_map.png'
import mapWaterPressure from './img/water_pressure_map.png'
import mapLifeChannel from './img/life_channel_map.png'
import mapFireControlRoom from './img/fire_control_room_map.png'
import mapGasDevice from './img/gas_device_map.png'
import highBuildMore from './img/highBuildMore.png'
import highBuildNormal from './img/highBuildNormal.png'
import highBuildDanger from './img/highBuildDanger.png'

type LayerKind = 'risk' | 'device'
type StatItem = {
  key: string
  kind: LayerKind
  label: string
  value: number | string
  icon: string
  legendIcon?: string
  mapIcon?: string
  valueClass?: string
  renderType?: 'point' | 'line'
}
type PointItem = {
  buildName?: string
  id?: string | number
  name?: string
  deviceName?: string
  deviceCode?: string
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
  deviceType?: string
  gcjzmc?: string
  fxdj?: string
  lng?: string | number
  lat?: string | number
  longitude?: string | number
  latitude?: string | number
  lngWgs84?: string | number
  latWgs84?: string | number
  lon?: string | number
  color?: string
  roadName?: string
  buildName?: string
  videoUrl?: string
  streamUrl?: string
  playUrl?: string
  flv?: string
  hls?: string
  rtsp?: string
  rtmp?: string
  andLonList?: Array<string | number>
  lonLatList?: Array<string | number>
  lineList?: Array<any>
  path?: Array<any>
  geom?: Array<any>
  geometry?: Array<any>
  coordinates?: Array<any>
  points?: Array<any>
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

const sourceName = 'BzFireRiskDevice'
// 事件总线定义，方便组件间通信，本地暂时不能触发，只能上传测试环境测试,注意emit中的source是组件名称，方便区分事件来源
const eventBus = {
  on: (event, callback) => {
    props.bus?.on(event, ({ source, data }) => {
      console.log('🚀 ~ eventBus on:', event, source, data)
      if (source === sourceName) return
      callback(data)
    })
  },
  off: (event, callback) => {
    props.bus?.off(event, callback)
  },
  emit: (event, data) => {
    console.log('🚀 ~ eventBus emit:', event, data)
    props.bus?.emit(event, { data, source: sourceName })
  }
}

const isSingleMode = computed(() => props.chartConfig?.option?.containerMode === 'single')
const riskDeviceStyle = computed(() => ({
  '--risk-device-width': isSingleMode.value ? '736px' : '1208px'
}))

const BASE_URL = 'http://23.99.16.179:11001/api/boot/system/fire'
// const BASE_URL = 'http://192.168.112.165:11001/api/boot/system/fire'
const selectedLayerKeys = ref<string[]>([])
const pointLayer: Record<string, Cesium.Entity> = {}
const lineLayer: Record<string, Cesium.Entity> = {}
let clickHandler: Cesium.ScreenSpaceEventHandler | null = null
const layerApiMap: Record<string, string> = {
  'risk-high-building': '/list/location/build',
  'risk-nine-small': '/list/location/nine',
  'risk-gas-mileage': '/list/location/gas',
  'risk-septic-tank': '/list/location/septic',
  'device-water-pressure': '/list/location/water',
  'device-life-channel': '/list/location/truck',
  'device-fire-control': '/list/location/room',
  'device-gas': '/list/location/gas'
}

const layerApiMethodMap: Record<string, 'get' | 'post'> = {
  'risk-septic-tank': 'get',
  'device-water-pressure': 'get',
  'device-life-channel': 'get',
  'device-fire-control': 'get',
  'device-gas': 'get',
  'risk-high-building':'get',
  'risk-gas-mileage':'get',
  'risk-nine-small':'get'
}

const riskStats = ref<StatItem[]>([
  { key: 'risk-high-building', kind: 'risk', label: '高层建筑', value: 0, icon: statHighBuilding, legendIcon: legendHighBuilding, mapIcon: mapHighBuilding },
  { key: 'risk-nine-small', kind: 'risk', label: '九小场所', value: 0, icon: statNineSmall, legendIcon: legendNineSmall, mapIcon: mapNineSmall },
  { key: 'risk-gas-mileage', kind: 'risk', label: '燃气里程', value: '526Km', icon: statGasMileage, legendIcon: legendGasMileage, mapIcon: mapGasMileage, renderType: 'line' },
  { key: 'risk-septic-tank', kind: 'risk', label: '化粪池', value: 0, icon: statSepticTank, legendIcon: legendSepticTank, mapIcon: mapSepticTank }
])

const deviceStats = ref<StatItem[]>([
  { key: 'device-water-pressure', kind: 'device', label: '水压监测', value: 0, icon: statWaterPressure, legendIcon: legendWaterPressure, mapIcon: mapWaterPressure },
  { key: 'device-life-channel', kind: 'device', label: '生命通道', value: 0, icon: statLifeChannel, legendIcon: legendLifeChannel, mapIcon: mapLifeChannel },
  { key: 'device-fire-control', kind: 'device', label: '消防控制室', value: 0, icon: statFireControlRoom, legendIcon: legendFireControlRoom, mapIcon: mapFireControlRoom },
  { key: 'device-gas', kind: 'device', label: '燃气设备', value: 0, icon: statGasDevice, legendIcon: legendGasDevice, mapIcon: mapGasDevice }
])

const legendItems = computed(() => [...riskStats.value, ...deviceStats.value])

// 高层建筑子图例
const highBuildingSubItems = [
  { key: 'risk-high-building-low', label: '较低风险', icon: highBuildNormal, fxdj: '较低风险' },
  { key: 'risk-high-building-normal', label: '一般风险', icon: highBuildMore, fxdj: '一般风险' },
  { key: 'risk-high-building-high', label: '高风险', icon: highBuildDanger }
]
const selectedSubKeys = ref<string[]>(highBuildingSubItems.map(s => s.key))
// 高层建筑缓存原始数据
const highBuildingRawList = ref<PointItem[]>([])

const getPointLng = (item: PointItem) => Number(item.lngWgs84 ?? item.longitude ?? item.lng ?? item.lon)
const getPointLat = (item: PointItem) => Number(item.latWgs84 ?? item.latitude ?? item.lat)

// 根据fxdj获取子图例key
const getSubKeyByFxdj = (fxdj: string) => {
  if (fxdj === '较低风险') return 'risk-high-building-low'
  if (fxdj === '一般风险') return 'risk-high-building-normal'
  return 'risk-high-building-high'
}

// 根据fxdj获取图标
const getIconByFxdj = (fxdj: string) => {
  const subKey = getSubKeyByFxdj(fxdj)
  const sub = highBuildingSubItems.find(s => s.key === subKey)
  return sub ? sub.icon : highBuildDanger
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

const clearLineLayer = () => {
  Object.keys(lineLayer).forEach(key => {
    const entity = lineLayer[key]
    if (entity) {
      window.viewer?.entities?.remove(entity)
    }
    delete lineLayer[key]
  })
}

const clearLayer = () => {
  clearPointLayer()
  clearLineLayer()
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

  Object.keys(lineLayer).forEach(key => {
    if (!key.startsWith(`${layerKey}_`)) return

    const entity = lineLayer[key]
    if (entity) {
      window.viewer?.entities?.remove(entity)
    }
    delete lineLayer[key]
  })
}

const getResponseList = (data: any) => {
  const result = data?.data ?? data

  let list: PointItem[] = []

  if (Array.isArray(result)) list = result
  if (Array.isArray(result?.records)) list = result.records
  if (Array.isArray(result?.list)) list = result.list
  if (Array.isArray(result?.rows)) list = result.rows

  return list.map(item => ({
    ...item,
    name: item.name ?? item.gcjzmc ?? item.deviceName ?? item.dvname,
    deviceName: item.deviceName ?? item.dvname,
    deviceCode: item.deviceCode ?? item.deviceNum ?? item.deviceNo ?? item.dvcode,
    lng: item.lng ?? item.longitude ?? item.lon,
    lat: item.lat ?? item.latitude,
    buildName: item.buildName ?? item.gcjzmc ?? item.name
  }))
}

const getResponseTotal = (data: any, fallback = 0) => {
  const result = data?.data ?? data
  const total = Number(result?.total ?? result?.page?.total ?? data?.total ?? fallback)
  return Number.isFinite(total) ? total : fallback
}

const updateStatValue = (key: string, value: number) => {
  const stat = legendItems.value.find(item => item.key === key)
  if (stat) {
    stat.value = value
  }
}

const getLayerApiPoints = async (key: string) => {
  const api = layerApiMap[key]
  if (!api) return []

  try {
    const method = layerApiMethodMap[key] || 'post'
    const res = method === 'get'
      ? await axios.get(`${BASE_URL}${api}`)
      : await axios.post(`${BASE_URL}${api}`)
    const list = getResponseList(res?.data)
    updateStatValue(key, getResponseTotal(res?.data, list.length))
    return list
  } catch (error) {
    console.error('获取风险点/设备点位失败:', error)
    return []
  }
}

const getLayerPoints = async (item: StatItem) => {
  return getLayerApiPoints(item.key)
}

const updateAllStatValues = async () => {
  await Promise.all(legendItems.value.map(item => getLayerApiPoints(item.key)))
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

const openHighBuildingList = (point: PointItem) => {
  eventBus.emit('OPEN_FIRE_KPI_DIA', {
    diaName: 'fire-high-building-list',
    name: point?.gcjzmc || point?.name || '',
    fxdj: point?.fxdj || ''
  })
}

const openNineSmallList = (point: PointItem) => {
  eventBus.emit('OPEN_FIRE_KPI_DIA', {
    diaName: 'fire-nine-small-list',
    name: point?.name || '',
    type: point?.type || ''
  })
}

const openSepticTankList = (point: PointItem) => {
  eventBus.emit('OPEN_FIRE_KPI_DIA', {
    diaName: 'fire-septic-tank-list',
    name: point?.dvname || point?.name || '',
    type: point?.type || point?.modelname || ''
  })
}

const openWaterPressureList = (point: PointItem) => {
  eventBus.emit('OPEN_FIRE_KPI_DIA', {
    diaName: 'fire-water-pressure-list',
    name: point?.name || point?.deviceName || '',
    type: point?.type || point?.deviceType || '',
    buildName:point.buildName
  })
}

const getPointVideoUrl = (point: PointItem) => {
  return point?.videoUrl || point?.streamUrl || point?.playUrl || point?.flv || point?.hls || point?.rtsp || point?.rtmp || ''
}

const openLifeChannelList = (point: PointItem) => {
  eventBus.emit('OPEN_FIRE_KPI_DIA', {
    diaName: 'fire-life-channel-list',
    name: point?.name || point?.deviceName || '',
    type: point?.type || point?.deviceType || '',
    deviceNum: point?.deviceNum || point?.deviceCode || '',
    videoUrl: getPointVideoUrl(point)
  })
}

const openControlRoomList = (point: PointItem) => {
  eventBus.emit('OPEN_FIRE_KPI_DIA', {
    diaName: 'fire-control-room-list',
    name: point?.name || point?.deviceName || '',
    type: point?.type || point?.deviceType || '',
    deviceNum: point?.deviceNum || point?.deviceCode || '',
    videoUrl: getPointVideoUrl(point)
  })
}

const openGasDeviceList = (point: PointItem) => {
  eventBus.emit('OPEN_FIRE_KPI_DIA', {
    diaName: 'fire-gas-device-list',
    name: point?.deviceName || point?.name || '',
    type: point?.deviceType || point?.type || ''
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

    if (pointType !== 'riskDevicePoint') return

    if (layerKey === 'risk-high-building') {
      openHighBuildingList(data || {})
      return
    }

    if (layerKey === 'risk-nine-small') {
      openNineSmallList(data || {})
      return
    }

    if (layerKey === 'risk-septic-tank') {
      openSepticTankList(data || {})
      return
    }

    if (layerKey === 'device-water-pressure') {
      openWaterPressureList(data || {})
      return
    }

    if (layerKey === 'device-life-channel') {
      openLifeChannelList(data || {})
      return
    }

    if (layerKey === 'device-fire-control') {
      openControlRoomList(data || {})
      return
    }

    if (layerKey === 'device-gas') {
      openGasDeviceList(data || {})
    }
  }, Cesium.ScreenSpaceEventType.LEFT_CLICK)
}

const buildLinePositions = (arr: Array<string | number>) => {
  const result: number[] = []

  for (let i = 0; i < arr.length; i += 2) {
    const lng = Number(arr[i])
    const lat = Number(arr[i + 1])

    if (Number.isFinite(lng) && Number.isFinite(lat)) {
      result.push(lng, lat, 360)
    }
  }

  return result
}

const getLinePositionArray = (item: PointItem) => {
  const source = item.andLonList || item.lonLatList || item.lineList || item.path || item.geom || item.geometry || item.coordinates || item.points || []

  if (!Array.isArray(source)) return []
  if (!source.length) return []

  if (typeof source[0] === 'number' || typeof source[0] === 'string') {
    return buildLinePositions(source as Array<string | number>)
  }

  const result: number[] = []
  source.forEach(point => {
    const lng = Number(point?.lng ?? point?.longitude ?? point?.lon ?? point?.[0])
    const lat = Number(point?.lat ?? point?.latitude ?? point?.[1])

    if (Number.isFinite(lng) && Number.isFinite(lat)) {
      result.push(lng, lat, 360)
    }
  })

  return result
}

const addPointLayer = async (item: StatItem) => {
  removeLayerByKey(item.key)
  const list = await getLayerPoints(item)
  const defaultIcon = item.mapIcon || item.icon
  if (!window.viewer || typeof Cesium === 'undefined') return
  ensureMapClickHandler()

  // 高层建筑：缓存原始数据
  if (item.key === 'risk-high-building') {
    highBuildingRawList.value = list
  }

  list.forEach((point, index) => {
    const lng = getPointLng(point)
    const lat = getPointLat(point)

    if (!Number.isFinite(lng) || !Number.isFinite(lat)) return

    const icon = item.key === 'risk-high-building' ? getIconByFxdj(point.fxdj) : defaultIcon

    const key = `${item.key}_${point.id ?? point.deviceCode ?? point.dvcode ?? index}_${index}`
    const entity = window.viewer.entities.add({
      id: `BzFireRiskDevice_${key}`,
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

const addLineLayer = async (item: StatItem) => {
  removeLayerByKey(item.key)
  const list = await getLayerPoints(item)
  if (!window.viewer || typeof Cesium === 'undefined') return

  list.forEach((lineItem, index) => {
    const positions = getLinePositionArray(lineItem)
    if (positions.length < 6) return

    const key = `${item.key}_${lineItem.id ?? lineItem.roadName ?? lineItem.deviceCode ?? index}_${index}`
    const entity = window.viewer.entities.add({
      id: `BzFireRiskDevice_${key}`,
      polyline: {
        positions: Cesium.Cartesian3.fromDegreesArrayHeights(positions),
        width: 2,
        material: Cesium.Color.fromCssColorString(lineItem.color || '#52f7ff')
      },
      properties: new Cesium.PropertyBag({
        type: 'riskDeviceLine',
        layerKey: item.key,
        layerName: item.label,
        data: lineItem
      })
    })

    lineLayer[key] = entity
  })
}

const addLayerByItem = async (item: StatItem) => {
  if (item.renderType === 'line') {
    await addLineLayer(item)
    return
  }

  await addPointLayer(item)
}

const handleLegendChange = async (keys: string[]) => {
  const prevKeys = selectedLayerKeys.value
  const addedKeys = keys.filter(key => !prevKeys.includes(key))
  const removedKeys = prevKeys.filter(key => !keys.includes(key))

  selectedLayerKeys.value = keys

  // 取消勾选高层建筑：隐藏子类、重置子类勾选、清除缓存
  if (!keys.includes('risk-high-building') && prevKeys.includes('risk-high-building')) {
    selectedSubKeys.value = []
    highBuildingRawList.value = []
  }

  // 勾选高层建筑：子类默认全选
  if (keys.includes('risk-high-building') && !prevKeys.includes('risk-high-building')) {
    selectedSubKeys.value = highBuildingSubItems.map(s => s.key)
  }

  removedKeys.forEach(removeLayerByKey)

  for (const key of addedKeys) {
    const item = legendItems.value.find(legendItem => legendItem.key === key)
    if (item) {
      await addLayerByItem(item)
    }
  }
}

// 子图例勾选/取消勾选处理
const handleSubLegendChange = (subKey: string, checked: boolean) => {
  if (checked) {
    if (!selectedSubKeys.value.includes(subKey)) {
      selectedSubKeys.value = [...selectedSubKeys.value, subKey]
    }
  } else {
    selectedSubKeys.value = selectedSubKeys.value.filter(k => k !== subKey)
  }
  // 根据子图例选中状态重新渲染高层建筑撒点
  refreshHighBuildingPoints()
}

// 刷新高层建筑撒点：先清除再按子图例筛选重新添加
const refreshHighBuildingPoints = () => {
  if (!selectedLayerKeys.value.includes('risk-high-building')) return
  removeLayerByKey('risk-high-building')
  const list = highBuildingRawList.value
  if (!list.length) return
  if (!window.viewer || typeof Cesium === 'undefined') return
  ensureMapClickHandler()

  list.forEach((point, index) => {
    const lng = getPointLng(point)
    const lat = getPointLat(point)
    if (!Number.isFinite(lng) || !Number.isFinite(lat)) return

    // 子类筛选：只显示选中子类对应的点位
    const subKey = getSubKeyByFxdj(point.fxdj)
    if (!selectedSubKeys.value.includes(subKey)) return

    const icon = getIconByFxdj(point.fxdj)
    const key = `risk-high-building_${point.id ?? point.deviceCode ?? point.dvcode ?? index}_${index}`
    const entity = window.viewer.entities.add({
      id: `BzFireRiskDevice_${key}`,
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
        layerKey: 'risk-high-building',
        layerName: '高层建筑',
        data: point
      })
    })
    pointLayer[key] = entity
  })
}

const openAllRiskPointList = () => {
  eventBus.emit('OPEN_FIRE_KPI_DIA', {
    diaName: 'fire-risk-point-list',
    riskType: 'all',
    riskLabel: '风险点'
  })
}

const openPerceptionDeviceList = () => {
  eventBus.emit('OPEN_FIRE_KPI_DIA', {
    diaName: 'fire-perception-device-list'
  })
}

onMounted(() => {
  ensureMapClickHandler()
  updateAllStatValues()
})

onUnmounted(() => {
  if (clickHandler) {
    clickHandler.destroy()
    clickHandler = null
  }
  clearLayer()
})
</script>

<script lang="ts">
export default {
  name: 'BzFireRiskDevice',
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
  max-height: 170px;
  padding: 3px 6px;
  box-sizing: border-box;
  white-space: nowrap;
  overflow-y: auto;
  background: url('./img/bg_left_top01.png');
  pointer-events: auto;

  // 隐藏滚动条
  scrollbar-width: none;
  -ms-overflow-style: none;
  &::-webkit-scrollbar {
    display: none;
  }
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

.legend-name {
  display: inline-block;
  max-width: none;
  min-width: max-content;
  overflow: visible;
  white-space: nowrap;
  vertical-align: top;
}

.legend-icon {
  width: 14px;
  height: 14px;
  margin-right: 3px;
  object-fit: contain;
  vertical-align: top;
}

.legend-item--sub {
  padding-left: 18px;

  .legend-icon {
    width: 12px;
    height: 12px;
  }

  .legend-name {
    font-size: 11px;
  }
}

.legend-sub-checkbox {
  appearance: none;
  -webkit-appearance: none;
  width: 12px;
  height: 12px;
  margin: 0 4px 0 0;
  border: 1px solid rgba(59, 179, 255, 0.78);
  border-radius: 2px;
  background: transparent;
  vertical-align: top;
  cursor: pointer;
  position: relative;
  flex-shrink: 0;

  &:checked {
    background: #409eff;
    border-color: #409eff;

    &::after {
      content: '';
      position: absolute;
      left: 3px;
      top: 0px;
      width: 4px;
      height: 7px;
      border: solid #fff;
      border-width: 0 1.5px 1.5px 0;
      transform: rotate(45deg);
    }
  }
}

.panel {
  position: relative;
  top:170px;
  min-width: 0;
  height: auto;
}

.panel-title {
  height: 32px;
  padding-left: 40px;
  box-sizing: border-box;
  background: url('./img/bg_second_title.png') left center / 100% 100% no-repeat;
  color: #f2fbff;
  font-family: 'UISDCBiaoTiHei', 'Microsoft YaHei', sans-serif;
  font-size: 24px;
  font-weight: 700;
  line-height: 32px;
  letter-spacing: 0;
  text-shadow: 0 0 10px rgba(92, 217, 255, 0.72);
  background-size:auto;
  cursor: pointer;
  pointer-events: auto;
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
  transition: border-color 0.18s ease, box-shadow 0.18s ease, transform 0.18s ease;

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

  .legend-name {
    max-width: none;
  }

  .legend-icon {
    width: 12px;
    height: 12px;
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
</style>
