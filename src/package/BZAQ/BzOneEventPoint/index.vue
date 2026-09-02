<template>
  <div class="bz-one-event-point" :style="containerStyle"></div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'
import axios from 'axios'
import processingIcon from './img/event-processing.png'
import completedIcon from './img/event-completed.png'
import geoDisasterIcon from './img/event-red.png'

declare const Cesium: any

type EventStatus = 'processing' | 'completed'
type TabStatus = 'all' | EventStatus

interface EventPoint {
  id: string
  name: string
  lng: number
  lat: number
  status: EventStatus
  [key: string]: any
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

const sourceName = 'BzOneEventPoint'
const activeTab = ref<TabStatus>('processing')
const pointLayer: Record<string, any> = {}
let retryTimer: ReturnType<typeof setTimeout> | null = null
let riskTabListener: ((payload: any) => void) | null = null
let clickHandler: any = null

const containerStyle = computed(() => {
  const width = Number(props.chartConfig?.attr?.w)
  const height = Number(props.chartConfig?.attr?.h)
  return {
    width: Number.isFinite(width) && width > 0 ? `${width}px` : '1px',
    height: Number.isFinite(height) && height > 0 ? `${height}px` : '1px'
  }
})

const BASE_URL = 'http://23.99.16.179:11001/api/boot/system/risk'
const RESCUE_OPINION_URL = 'http://23.99.16.179:11001/api/boot/system/rescue/opinion'
const GEO_DISASTER_EVENT_ID = 'SHYZDK20260819172149032961'

const tabTypeMap: Record<string, number> = {
  all: 0,
  processing: 0,
  completed: 2
}

const eventPoints = ref<EventPoint[]>([])

const normalizeStatus = (status: any) => String(status ?? '').trim()
const isCompletedStatus = (status: any) => {
  const normalized = normalizeStatus(status)
  return normalized === '已办结' || normalized === '已完成'
}

const toCoordinate = (value: any) => {
  if (value == null) return Number.NaN
  return Number(String(value).trim())
}

const getPointLng = (item: any) => toCoordinate(
  item?.longitude
  ?? item?.lng
  ?? item?.lon
  ?? item?.Lon
  ?? item?.x
  ?? item?.wgs84Longitude
  ?? item?.lngWgs84
  ?? item?.longitudeWgs84
)

const getPointLat = (item: any) => toCoordinate(
  item?.latitude
  ?? item?.lat
  ?? item?.Lat
  ?? item?.y
  ?? item?.wgs84Latitude
  ?? item?.latWgs84
  ?? item?.latitudeWgs84
)

const getRiskList = async () => {
  try {
    const type = tabTypeMap[activeTab.value] ?? 0
    const res = await axios.get(`${BASE_URL}/lists`, { params: { type } })
    if (res.data.code === '000000') {
      const list = res.data.data || []
      eventPoints.value = list.map((item: any, index: number) => {
        const isCompleted = isCompletedStatus(item.handleStatus)
        const lng = getPointLng(item)
        const lat = getPointLat(item)
        return {
          ...item,
          id: item.asEventNumber || item.eventNo || item.id || `${activeTab.value}_${index}`,
          name: item.eventDesc || item.eventType || '未知事件',
          lng,
          lat,
          __pointIndex: index,
          status: isCompleted ? 'completed' : 'processing'
        }
      })
      drawPointLayer()
    } else {
      console.error('获取风险列表失败：' + res.data.message)
      eventPoints.value = []
      drawPointLayer()
    }
  } catch (err) {
    console.error('请求风险列表接口失败', err)
    eventPoints.value = []
    drawPointLayer()
  }
}

const fetchRescueOpinion = async (point: EventPoint) => {
  try {
    const params = {
      source: point?.source || '',
      eventType: point?.eventType || '',
      eventAddress: point?.eventAddress || '',
      eventDesc: point?.eventDesc || '',
      handleStatus: point?.handleStatus || '',
      referenceLevel: point?.referenceLevel || '',
      coordinateDepartments: point?.coordinateDepartments || ''
    }
    const res = await axios.post(RESCUE_OPINION_URL, params)
    if (res.data.code === '000000') {
      return res.data.data || {}
    } else {
      console.error('获取AI分析失败：' + res.data.message)
      return {}
    }
  } catch (err) {
    console.error('请求AI分析接口失败', err)
    return {}
  }
}

const getVisiblePoints = () => {
  if (activeTab.value === 'all') return eventPoints.value
  return eventPoints.value.filter(point => point.status === activeTab.value)
}

const getPointEventId = (point: EventPoint) => String(point?.asEventNumber ?? point?.eventNo ?? point?.id ?? '').trim()
const isGeoDisasterPoint = (point: EventPoint) => {
  const fakeFlag = String(point?.fake_flag ?? point?.fakeFlag ?? '').trim()
  return fakeFlag === '地质灾害' || getPointEventId(point) === GEO_DISASTER_EVENT_ID
}

const getPointIcon = (point: EventPoint) => {
  if (isGeoDisasterPoint(point)) return geoDisasterIcon
  return point.status === 'processing' ? processingIcon : completedIcon
}

const getPropertyValue = (value: any) => {
  if (value && typeof value.getValue === 'function') {
    return value.getValue()
  }
  return value
}

const getEntityProperties = (entity: any) => {
  const properties = entity?.properties
  if (!properties) return {}
  if (typeof properties.getValue === 'function') return properties.getValue()
  return properties
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

const addPoint = (point: EventPoint) => {
  if (!window.viewer || typeof Cesium === 'undefined') return
  if (!Number.isFinite(point.lng) || !Number.isFinite(point.lat)) return

  const key = `one-event-${point.status}_${point.id}_${point.__pointIndex ?? ''}`
  const entity = window.viewer.entities.add({
    id: `BzOneEventPoint_${key}`,
    position: Cesium.Cartesian3.fromDegrees(point.lng, point.lat, 360),
    billboard: {
      image: getPointIcon(point),
      width: 22,
      height: 22,
      verticalOrigin: Cesium.VerticalOrigin.BOTTOM,
      disableDepthTestDistance: Number.POSITIVE_INFINITY
    },
    properties: new Cesium.PropertyBag({
      type: 'oneEventPoint',
      layerKey: `one-event-${point.status}`,
      layerName: point.status === 'processing' ? '处置中' : '已完成',
      data: point
    })
  })

  pointLayer[key] = entity
}

const openAIAnalysis = async (point: EventPoint) => {
  props.bus?.emit('OPEN_DIA', {
    data: {
      diaName: 'ai-analysis',
      data: {
        title: point.name,
        riskData: point,
        analysisData: {
          emergencyLevel: '分析中...',
          dispatchSuggestion: '分析中...',
          suggestion: '正在获取AI分析结果，请稍候...'
        }
      }
    },
    source: sourceName
  })

  const opinion = await fetchRescueOpinion(point)

  props.bus?.emit('OPEN_DIA', {
    data: {
      diaName: 'ai-analysis',
      data: {
        title: point.name,
        riskData: point,
        analysisData: {
          emergencyLevel: opinion['紧急状况'] || '较重要',
          dispatchSuggestion: opinion['智能调度'] || '否',
          suggestion: opinion['处置建议'] || '暂无处置建议'
        }
      }
    },
    source: sourceName
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
    const data = getPropertyValue(properties.data)

    if (pointType !== 'oneEventPoint' || !data) return
    openAIAnalysis(data)
  }, Cesium.ScreenSpaceEventType.LEFT_CLICK)
}

const drawPointLayer = () => {
  if (!window.viewer || typeof Cesium === 'undefined') {
    retryTimer = setTimeout(drawPointLayer, 500)
    return
  }

  ensureMapClickHandler()
  clearPointLayer()
  getVisiblePoints().forEach(addPoint)
}

const normalizeTab = (data: any): TabStatus => {
  const tab = typeof data === 'string' ? data : data?.tab
  if (tab === 'processing' || tab === 'completed') return tab
  return 'all'
}

const handleRiskTabChange = (data: any) => {
  activeTab.value = normalizeTab(data)
  getRiskList()
}

onMounted(() => {
  riskTabListener = ({ source, data }) => {
    if (source === sourceName) return
    handleRiskTabChange(data)
  }
  props.bus?.on('BZ_RISK_TAB_CHANGE', riskTabListener)
  getRiskList()
})

onUnmounted(() => {
  if (retryTimer) {
    clearTimeout(retryTimer)
    retryTimer = null
  }
  if (riskTabListener) {
    props.bus?.off('BZ_RISK_TAB_CHANGE', riskTabListener)
    riskTabListener = null
  }
  if (clickHandler) {
    clickHandler.destroy()
    clickHandler = null
  }
  clearPointLayer()
})
</script>

<script lang="ts">
export default {
  name: 'BzOneEventPoint',
  version: '1.0.0'
}
</script>

<style lang="scss" scoped>
.bz-one-event-point {
  position: relative;
  pointer-events: none;
}
</style>
