<template>
  <div class="pipeline-device-point" :style="containerStyle">
    <n-checkbox-group
      :value="selectedLayerKeys"
      class="legend-panel"
      @update:value="handleLegendChange"
    >
      <n-checkbox
        v-for="item in layerItems"
        :key="item.key"
        class="legend-item"
        :value="item.key"
        :title="item.label"
      >
        <img class="legend-icon" :src="item.legendIcon" alt="" />
        <span class="legend-name">{{ item.label }}</span>
      </n-checkbox>
    </n-checkbox-group>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { NCheckbox, NCheckboxGroup } from 'naive-ui'
import axios from 'axios'
import gasBlue from './img/pipeline_gas_device_blue.png'
import gasRed from './img/gas_red.png'
import lightingBlue from './img/pipeline_lighting_device_blue.png'
import lightingRed from './img/lighting_red.png'
import gasNetworkBlue from './img/pipeline_gas_monitor_blue.png'
import gasNetworkRed from './img/gas_network_red.png'
import septicTankBlue from './img/pipeline_septic_tank_blue.png'
import septicTankRed from './img/septic_tank_red.png'
import outfallBlue from './img/pipeline_outfall_monitor_blue.png'
import outfallRed from './img/outfall_red.png'
import medicalBlue from './img/pipeline_medical_wastewater_blue.png'
import medicalRed from './img/medical_wastewater_red.png'
import waterStationBlue from './img/pipeline_water_quality_station_blue.png'
import waterStationRed from './img/water_station_red.png'
import waterUseBlue from './img/pipeline_water_supply_monitor_blue.png'
import waterUseRed from './img/water_use_red.png'
import legendGas from './img/legend_pipeline_gas_device_blue.png'
import legendLighting from './img/legend_pipeline_lighting_device_blue.png'
import legendGasNetwork from './img/legend_pipeline_gas_monitor_blue.png'
import legendSepticTank from './img/legend_pipeline_septic_tank_blue.png'
import legendOutfall from './img/legend_pipeline_outfall_monitor_blue.png'
import legendMedical from './img/legend_pipeline_medical_wastewater_blue.png'
import legendWaterStation from './img/legend_pipeline_water_quality_station_blue.png'
import legendWaterUse from './img/legend_pipeline_water_supply_monitor_blue.png'

type LayerItem = {
  key: string
  label: string
  code: number
  diaName: string
  detailType: string
  legendIcon: string
  blueIcon: string
  redIcon: string
  points?: any[]
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

const sourceName = 'Bz‌PipelineDevice'
const BASE_URL = 'http://23.99.16.179:11001'
const SELECT_LAYERS_EVENT = 'BzPipelineDevice:selectLayers'
const selectedLayerKeys = ref<string[]>([])
const pointLayer: Record<string, Cesium.Entity> = {}
const layerPointsCache = new Map<string, any[]>()
const eventCallbackMap = new Map<string, Map<Function, Function>>()
let clickHandler: Cesium.ScreenSpaceEventHandler | null = null
let isComponentMounted = false

const eventBus = {
  emit(event: string, data: any) {
    props.bus?.emit(event, { data, source: sourceName })
  },
  on(event: string, callback: (data: any) => void) {
    const wrappedCallback = ({ data, source }: any) => {
      if (source === sourceName) return
      callback(data)
    }
    if (!eventCallbackMap.has(event)) eventCallbackMap.set(event, new Map())
    eventCallbackMap.get(event)?.set(callback, wrappedCallback)
    props.bus?.on?.(event, wrappedCallback)
  },
  off(event: string, callback: (data: any) => void) {
    const wrappedCallback = eventCallbackMap.get(event)?.get(callback)
    props.bus?.off?.(event, wrappedCallback || callback)
    eventCallbackMap.get(event)?.delete(callback)
  }
}

const containerStyle = computed(() => {
  const width = Number(props.chartConfig?.attr?.w)
  const height = Number(props.chartConfig?.attr?.h)
  return {
    width: Number.isFinite(width) && width > 0 ? `${width}px` : '188px',
    height: Number.isFinite(height) && height > 0 ? `${height}px` : '220px'
  }
})

const layerItems: LayerItem[] = [
  {
    key: 'gas',
    code: 1,
    label: '燃气设备',
    diaName: 'pipeline-gas-leak-list',
    detailType: 'gas-leak',
    legendIcon: legendGas,
    blueIcon: gasBlue,
    // redIcon: gasBlue,
    redIcon: gasRed,
    points: [
      { deviceId: '15353131862796072794688', street: '大坪街道', installPosition: '乡村基(大坪医院店)陆军特色医学中心', lng: 106.5205, lat: 29.5413, status: 0 },
      { deviceId: '15353131862821069904684', street: '南纪门街道', installPosition: '维新巷熊猫公馆', lng: 106.5732, lat: 29.5532, status: 1 }
    ]
  },
  {
    key: 'lighting',
    code: 3,
    label: '照明设备',
    diaName: 'pipeline-lighting-device-list',
    detailType: 'water-level',
    legendIcon: legendLighting,
    blueIcon: lightingBlue,
    redIcon: lightingRed,
    points: [
      { deviceCode: '10010915307499473801', deviceName: '新华路10号杆旁水位', deviceType: '路面水位监测', installAddress: '新华路', lng: 106.57353, lat: 29.556126, status: 0, detailType: 'water-level' },
      { deviceCode: '10010315307499469976', deviceName: '8号杆旁井盖', deviceType: '井盖状态监测', installAddress: '新华路', lng: 106.573496, lat: 29.555962, status: 1, detailType: 'manhole' },
      { deviceCode: '8040115473222115728', deviceName: 'R20杆体倾斜', deviceType: '杆体倾斜监测', installAddress: 'R20杆体倾斜', lng: 106.570515, lat: 29.56214, status: 0, detailType: 'pole-tilt' }
    ]
  },
  {
    key: 'gas-network',
    code: 2,
    label: '地下管网气体监测',
    diaName: 'pipeline-gas-network-list',
    detailType: 'gas-network',
    legendIcon: legendGasNetwork,
    blueIcon: gasNetworkBlue,
    redIcon: gasNetworkRed,
    points: [
      { deviceCode: '2087', deviceType: '地下管网气体监测', installAddress: '中山2路儿童医院对面天桥对面', lng: 106.557, lat: 29.555, status: 1 },
      { deviceCode: '2088', deviceType: '地下管网气体监测', installAddress: '重庆市渝中区民新街168号（新月湾公交站）', lng: 106.553, lat: 29.548, status: 0 }
    ]
  },
  {
    key: 'septic-tank',
    code: 0,
    label: '化粪池监测',
    diaName: 'pipeline-septic-tank-list',
    detailType: 'septic',
    legendIcon: legendSepticTank,
    blueIcon: septicTankBlue,
    // redIcon: septicTankRed,
    redIcon: septicTankBlue,
    points: [
      { deviceCode: 'HFC-001', deviceName: '化粪池监测点', deviceType: '化粪池监测', installAddress: '解放碑街道', lng: 106.576, lat: 29.557, status: 0 }
    ]
  },
  {
    key: 'outfall',
    code: 5,
    label: '沿江排口监测设备',
    diaName: 'pipeline-outfall-water-list',
    detailType: 'outfall',
    legendIcon: legendOutfall,
    blueIcon: outfallBlue,
    redIcon: outfallRed,
    points: [
      { deviceName: 'ZSaDN1pNb4d-雨水口-J-01', deviceCode: '雨水口-J-01', installAddress: '重庆市渝中区化龙桥街道雍江苑嘉陵江滨江路应急救援场桥下', lng: 106.507, lat: 29.56, status: 0 },
      { deviceName: 'ZSaDN1pNb4d-溢流口-C-15', deviceCode: '溢流口-C-15', installAddress: '重庆市渝中区南纪门街道长滨路156附1号民工之家', lng: 106.579, lat: 29.548, status: 1 }
    ]
  },
  {
    key: 'medical',
    code: 4,
    label: '医疗废水监测设备',
    diaName: 'pipeline-medical-sewage-list',
    detailType: 'medical',
    legendIcon: legendMedical,
    blueIcon: medicalBlue,
    redIcon: medicalRed,
    points: [
      { deviceCode: '756877100000002500004005', deviceName: '化学需氧量COD监测', installAddress: '重庆市渝中区两路口街道中山一路社区中山一路136号', lng: 106.551, lat: 29.555, status: 1 },
      { deviceCode: '756877100000002500004001', deviceName: '悬浮物监测', installAddress: '重庆市渝中区两路口街道枇杷山正街104号', lng: 106.553, lat: 29.552, status: 0 }
    ]
  },
  {
    key: 'water-station',
    code: 7,
    label: '水质监测站',
    diaName: 'pipeline-water-station-list',
    detailType: 'water-station',
    legendIcon: legendWaterStation,
    blueIcon: waterStationBlue,
    redIcon: waterStationRed,
    points: [
      { deviceCode: 'YZQ-SZ-004', deviceType: '地表水小型自动监测站（长江流域黄沙溪站）', deviceAddress: '重庆市渝中区菜园坝街道平安街社区重庆市环卫三所', lng: 106.525, lat: 29.545, status: 0 },
      { deviceCode: 'YZQ-SZ-002', deviceType: '地表水小型自动监测站（嘉陵江流域朝天门站）', deviceAddress: '重庆市渝中区朝天门街道朝千路社区朝天门6号码头', lng: 106.587, lat: 29.562, status: 1 }
    ]
  },
  {
    key: 'water-use',
    code: 8,
    label: '用水监测',
    diaName: 'pipeline-water-use-list',
    detailType: 'water-use',
    legendIcon: legendWaterUse,
    blueIcon: waterUseBlue,
    redIcon: waterUseRed,
    points: [
      { deviceCode: '08030100012010011306', deviceName: '低压1#流量计', deviceType: '低压1#流量计', installAddress: '低压管网片区', lng: 106.55788, lat: 29.565253, status: 0 },
      { deviceCode: '080310000020100006841', deviceName: '枇杷山液位计', deviceType: '枇杷山液位计', installAddress: '枇杷山监测点', lng: 106.55788, lat: 29.55389, status: 1 }
    ]
  }
]

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

function ensureMapClickHandler() {
  if (clickHandler || !window.viewer || typeof Cesium === 'undefined') return

  clickHandler = new Cesium.ScreenSpaceEventHandler(window.viewer.scene.canvas)
  clickHandler.setInputAction((movement: any) => {
    const picked = window.viewer?.scene?.pick(movement.position)
    const properties = getEntityProperties(picked?.id)
    const pointType = getPropertyValue(properties.type)
    if (pointType !== 'pipelineDevicePoint') return

    const data = getPropertyValue(properties.data) || {}
    const layerKey = getPropertyValue(properties.layerKey)
    const layer = layerItems.find(item => item.key === layerKey)
    if (!layer) return

    eventBus.emit('OPEN_PIPELINE_KPI_DIA', {
      diaName: layer.diaName,
      title: layer.label,
      point: {
        ...data,
        detailType: data.detailType || layer.detailType
      },
      detailType: data.detailType || layer.detailType
    })
  }, Cesium.ScreenSpaceEventType.LEFT_CLICK)

  clickHandler.setInputAction((movement: any) => {
    const picked = window.viewer?.scene?.pick(movement.endPosition)
    const properties = getEntityProperties(picked?.id)
    const pointType = getPropertyValue(properties.type)
    window.viewer.canvas.style.cursor = pointType === 'pipelineDevicePoint' ? 'pointer' : 'default'
  }, Cesium.ScreenSpaceEventType.MOUSE_MOVE)
}

function clearLayer(layerKey?: string) {
  Object.keys(pointLayer).forEach(key => {
    if (layerKey && !key.startsWith(`${layerKey}_`)) return
    const entity = pointLayer[key]
    if (entity) window.viewer?.entities?.remove(entity)
    delete pointLayer[key]
  })
}

async function fetchLayerPoints(item: LayerItem) {
  if (layerPointsCache.has(item.key)) return layerPointsCache.get(item.key) || []
  try {
    const res = item.key === 'septic-tank'
      ? await axios.get(`${BASE_URL}/api/boot/system/fire/pipeline/loc`)
      : await axios.post(`${BASE_URL}/api/boot/system/pipeline/footprint`, {
        code: item.code
      })
    const rows = Array.isArray(res?.data?.data) ? res.data.data : []
    layerPointsCache.set(item.key, rows)
    return rows
  } catch (error) {
    console.error('获取管线地图落点失败:', item.key, error)
    return item.points || []
  }
}

function renderLayer(item: LayerItem, points: any[]) {
  clearLayer(item.key)
  if (!isComponentMounted) return
  if (!window.viewer || typeof Cesium === 'undefined') return
  ensureMapClickHandler()

  points.forEach((point, index) => {
    const lng = Number(point.longitudeWgs84 ?? point.lng ?? point.longitude ?? point.lon)
    const lat = Number(point.latitudeWgs84 ?? point.lat ?? point.latitude)
    if (!Number.isFinite(lng) || !Number.isFinite(lat)) return

    const isWarning = point.isWarning === true || Number(point.isWarning) === 1
    const icon = isWarning ? item.redIcon : item.blueIcon
    const key = `${item.key}_${point.deviceId ?? point.deviceCode ?? point.id ?? index}_${index}`
    pointLayer[key] = window.viewer.entities.add({
      position: Cesium.Cartesian3.fromDegrees(lng, lat, 360),
      billboard: {
        image: icon,
        width: 20,
        height: 20,
        verticalOrigin: Cesium.VerticalOrigin.BOTTOM,
        disableDepthTestDistance: Number.POSITIVE_INFINITY,
        scaleByDistance: new Cesium.NearFarScalar(1.0e2, 1.5, 5.0e5, 0.5)
      },
      properties: new Cesium.PropertyBag({
        type: 'pipelineDevicePoint',
        layerKey: item.key,
        layerName: item.label,
        data: {
          ...point,
          layerName: item.label,
          deviceCode: point.deviceCode || point.dvcode || point.deviceId || point.id,
          deviceId: point.deviceId || point.id || point.dvcode,
          deviceName: point.deviceName || point.dvname,
          deviceAddress: point.deviceAddress || point.installAddress || point.mountAddress,
          installAddress: point.installAddress || point.mountAddress || point.deviceAddress,
          longitude: point.longitude ?? point.longitudeWgs84 ?? point.lon,
          latitude: point.latitude ?? point.latitudeWgs84 ?? point.lat,
          isWarning,
          _raw: point,
          detailType: point.detailType || item.detailType
        }
      })
    })
  })
}

function handleLegendChange(keys: string[]) {
  if (!isComponentMounted) return
  const previousKeys = selectedLayerKeys.value
  const nextKeys = Array.isArray(keys) ? keys : []
  const removedKeys = previousKeys.filter(key => !nextKeys.includes(key))
  const addedKeys = nextKeys.filter(key => !previousKeys.includes(key))

  selectedLayerKeys.value = nextKeys
  removedKeys.forEach(key => clearLayer(key))
  addedKeys.forEach(async key => {
    const item = layerItems.find(layer => layer.key === key)
    if (!item) return
    const points = await fetchLayerPoints(item)
    if (isComponentMounted && selectedLayerKeys.value.includes(key)) {
      renderLayer(item, points)
    }
  })
}

function handleExternalLayerSelect(payload: any) {
  const keys = Array.isArray(payload)
    ? payload
    : Array.isArray(payload?.keys)
      ? payload.keys
      : payload?.key
        ? [payload.key]
        : []
  handleLegendChange(keys)
}

onMounted(() => {
  isComponentMounted = true
  eventBus.on(SELECT_LAYERS_EVENT, handleExternalLayerSelect)
})

onUnmounted(() => {
  isComponentMounted = false
  selectedLayerKeys.value = []
  eventBus.off(SELECT_LAYERS_EVENT, handleExternalLayerSelect)
  clearLayer()
  if (clickHandler) {
    clickHandler.destroy()
    clickHandler = null
  }
})
</script>

<script lang="ts">
export default {
  name: 'Bz‌PipelineDevice',
  version: '1.0.0'
}
</script>

<style scoped lang="scss">
.pipeline-device-point {
  position: relative;
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
</style>
