<template>

  
  <!-- <CMapVue
    :data="{
      data: {
        矢量地图: true, // 矢量地图
        遥感影像: true, // 遥感影像
        电子影像: true, // 电子影像
        三维实景: true // 三维实景
      }
    }"
    @loaded="mapLoaded"
    :chartConfig="{
      ...chartConfig,
      option: {
        layer: {
          矢量地图: true, // 矢量地图
          遥感影像: true, // 遥感影像
          电子影像: true, // 电子影像
          三维实景: true // 三维实景
        },
        areaConfig: [],
        state: {
          scope: {
            // 默认行政区编码
            adcode: '500102',
            // 矩形范围
            bbox: [105.06, 36.06, 105.06, 36.06],
            // 中心点
            center: [106.56, 29.56],
            // 缩放
            // zoom: 10,
            // 俯仰角
            pitch: 0,
            // 倾斜角
            heading: 0,
            // 高度
            height: 6700,
            // 最大高度
            maxHeight: 1000000,
            // 最小高度
            minHeight: 1000,
            // 底图
            tile: '矢量地图',
            cesiumMapToken: '32697850912581',
            layerBg: false,
            mapTypeShow: true,
            control: true
          }
        }
      }
    }"
  ></CMapVue> -->
  <div
    class="device-select"
    :class="{ 'device-select--active': selectOpen, 'device-select--bz': isEssentialSafe }"
    @click="handleSelectTriggerClick"
  >
    <span class="device-select__label">感知设备</span>
    <span class="device-select__arrow"></span>

    <div
      v-show="selectOpen"
      class="device-select__dropdown"
      @click.stop
    >
      <div
        v-for="item in deviceTypeOptions"
        :key="item.value"
        class="device-select__option"
        :class="{ 'device-select__option--checked': isDeviceTypeSelected(item.value) }"
        @click="toggleDeviceType(item.value)"
      >
        <span class="device-select__checkbox"></span>
        <span class="device-select__option-text">{{ item.label }}</span>
      </div>
    </div>
  </div>

  
  <!-- <div v-show="popupVisible" class="station-popup-wrappers" :style="popupStyle">
    <div class="device-point-card">
      <div class="device-point-card__header">
        <span class="device-point-card__title">{{ currentStation?.popupTitle || '-' }}</span>
        <button type="button" class="device-point-card__close" @click="clickPopup">×</button>
      </div>

      <div class="device-point-card__body">
        <div class="device-point-card__row">
          <span class="device-point-card__label">点位名称：</span>
          <span class="device-point-card__value" :title="currentStation?.deviceStatus || '-'">{{ currentStation?.deviceStatus || '-' }}</span>
        </div>
        <div class="device-point-card__row">
          <span class="device-point-card__label">设备名称</span>
          <span class="device-point-card__value" :title="currentStation?.deviceName || '-'">{{ currentStation?.deviceName || '-' }}</span>
        </div>
        <div class="device-point-card__row">
          <span class="device-point-card__label">设备编号</span>
          <span class="device-point-card__value" :title="currentStation?.deviceNo || '-'">{{ currentStation?.deviceNo || '-' }}</span>
        </div>
        <div class="device-point-card__row">
          <span class="device-point-card__label">经纬度</span>
          <span class="device-point-card__value" :title="currentStation?.coordinateText || '-'">{{ currentStation?.coordinateText || '-' }}</span>
        </div>
      </div>
    </div>
  </div> -->
</template>

<script lang="ts" setup>
import { ref, defineProps, reactive, onMounted, onUnmounted, nextTick, provide, watch, computed } from 'vue'
// import { CMapVue } from '@/package/components/cmap/index'
import type { GlobalParams, EventBus } from '@/package/index.d'
import axios from 'axios'
import { NCard, NSpace, NButton } from 'naive-ui'
// import { BillboardGraphics } from 'cesium'

//表格模拟数据
const tableData = ref([
  {
    name: '张三',
    unit: '沙坪坝树人小学公交站',
    duty: '工作人员',
    phone: '138XXXX1234',
    id: '1001',
    remark: '无'
  },
  {
    name: '李四',
    unit: '蓝壁延辉段右干山体育',
    duty: '负责人',
    phone: '139XXXX5678',
    id: '', // 无ID
    remark: '暂无'
  },
  {
    name: '王五',
    unit: '两新江区人民医院',
    duty: '医护人员',
    phone: '137XXXX9012',
    id: '1003',
    remark: '需要协调'
  },
  {
    name: '赵六',
    unit: '李子坝正街（李子坝观景台）',
    duty: '管理员',
    phone: '136XXXX3456',
    id: null, // 无ID
    remark: '无'
  },
  {
    name: '孙七',
    unit: '绿地路（大园小学路段）',
    duty: '巡检员',
    phone: '135XXXX7890',
    id: '1005',
    remark: '正常'
  },
  {
    name: '孙七',
    unit: '绿地路（大园小学路段）',
    duty: '巡检员',
    phone: '135XXXX7890',
    id: '1005',
    remark: '正常'
  },
  {
    name: '孙七',
    unit: '绿地路（大园小学路段）',
    duty: '巡检员',
    phone: '135XXXX7890',
    id: '1005',
    remark: '正常'
  },
  {
    name: '孙七',
    unit: '绿地路（大园小学路段）',
    duty: '巡检员',
    phone: '135XXXX7890',
    id: '1005',
    remark: '正常'
  },
  {
    name: '孙七',
    unit: '绿地路（大园小学路段）',
    duty: '巡检员',
    phone: '135XXXX7890',
    id: '1005',
    remark: '正常'
  },
  {
    name: '孙七',
    unit: '绿地路（大园小学路段）',
    duty: '巡检员',
    phone: '135XXXX7890',
    id: '1005',
    remark: '正常'
  },
  {
    name: '孙七',
    unit: '绿地路（大园小学路段）',
    duty: '巡检员',
    phone: '135XXXX7890',
    id: '1005',
    remark: '正常'
  }
])
const currentStation = ref<any>(null)
const popupVisible = ref(false)
const popupHtml = ref('')
const popupStyle = ref<Record<string, string>>({})
let popupEntity: Cesium.Entity | null = null
const ZB_POINT_3D_HEIGHT = 210

interface YZZBInterface {
  id?: string
  locationName: string
  latWgs84: number
  lngWgs84: number
  iconUrl?: string
  deviceCode?: string
  deviceAddress?: string
  deviceStatus?: string
  deviceName?: string
  deviceNo?: string
  sourceFrom?: string
  popupTitle?: string
  coordinateText?: string
  raw?: any
}

const YZZBEntities: Record<string, Cesium.Entity> = {}
const DEVICE_POINT_ENTITY_TYPE = 'YZDevicePoint'
const HOUSE_DEVICE_OPEN_EVENT = 'yz-house-device-open'
const DEFAULT_DEVICE_ICON = '/api/api/download/2062440096825167874'
const DEVICE_ICON_MAP = {
  '2002': new URL('./img/烟感.png', import.meta.url).href,
  '2003': new URL('./img/电气火灾.png', import.meta.url).href,
  '2004': new URL('./img/门锁.png', import.meta.url).href,
  '2005': new URL('./img/燃气.png', import.meta.url).href,
  '2006': new URL('./img/一氧化碳.png', import.meta.url).href
}

function getZBPointPosition(station: YZZBInterface) {
  return Cesium.Cartesian3.fromDegrees(station.lngWgs84, station.latWgs84, ZB_POINT_3D_HEIGHT)
}

function addYZZBPoints(stations: YZZBInterface[]) {
  stations.forEach((station, index) => {
    const stationId = [
      station.raw?.deviceType,
      station.id || station.deviceCode || `${station.lngWgs84}-${station.latWgs84}`,
      index
    ].filter(Boolean).join('-')
    const iconUrl = station.iconUrl || DEFAULT_DEVICE_ICON
    const entity = window.viewer.entities.add({
      id: `device-${stationId}`,
      position: getZBPointPosition(station),
      billboard: {
        image: iconUrl,
        scale: 0.3,
        verticalOrigin: Cesium.VerticalOrigin.BOTTOM,
        heightReference: Cesium.HeightReference.CLAMP_TO_GROUND,
        disableDepthTestDistance: Number.POSITIVE_INFINITY
      },
      properties: {
        type: DEVICE_POINT_ENTITY_TYPE,
        stationData: station // ⭐ 把完整业务数据挂进去
      }
    })

    YZZBEntities[stationId] = entity
  })
}

let pointClickHandler: Cesium.ScreenSpaceEventHandler | null = null

function YZZBPointClickEvent() {
  if (pointClickHandler) return

  pointClickHandler = new Cesium.ScreenSpaceEventHandler(window.viewer.scene.canvas)

  pointClickHandler.setInputAction(movement => {
    const picked = window.viewer.scene.pick(movement.position)
    if (!Cesium.defined(picked) || !picked.id) return

    const entity = picked.id as Cesium.Entity
    const type = entity.properties?.type?.getValue()
    const isOwnEntity = Object.values(YZZBEntities).includes(entity)

    if (type === DEVICE_POINT_ENTITY_TYPE && isOwnEntity) {
      const station: YZZBInterface = entity.properties.stationData.getValue()
      console.log('点击站点信息：', station)
      console.log('点击站点实体：', station)
      currentStation.value = station
      // axios
      //   .post(`http://23.210.227.34:23343/yzqzlzx/api/boot/system/home/duty/info?name=${station.locationName}`)
      //   .then(res => {
      //     if (res.data.code !== '000000') return
      //     console.log('🚀 ~ getLineData ~ res:', res)
      //     tableData.value = res.data.data
      //打开对应监控
      clickPopup()
      openHouseDevicePopup(station)
      // })
    } else {
      clickPopup()
    }
  }, Cesium.ScreenSpaceEventType.LEFT_CLICK)
}

function normalizeHouseDevicePayload(station: YZZBInterface) {
  const raw = station?.raw || {}
  const deviceAddress = raw.deviceAddress || station?.deviceAddress || '-'
  const deviceNo = raw.deviceNo || station?.deviceNo || station?.deviceCode || ''

  return {
    ...raw,
    id: raw.id || station?.id || deviceNo,
    deviceCode: raw.deviceCode || deviceNo,
    deviceCodeNew: raw.deviceCodeNew || deviceNo,
    deviceNo,
    deviceName: raw.deviceName || station?.deviceName || '-',
    deviceType: raw.deviceType || '',
    deviceTypeName: raw.deviceTypeName || raw.deviceType || '-',
    deviceStatus: raw.deviceStatus || station?.deviceStatus || '',
    statusName: raw.statusName || raw.deviceStatus || station?.deviceStatus || '',
    installLocation: raw.installLocation || deviceAddress,
    location: raw.location || deviceAddress,
    address: raw.address || deviceAddress,
    deviceAddress,
    sourceFrom: raw.sourceFrom || station?.sourceFrom || '',
    longitude: raw.longitude || station?.lngWgs84 || '',
    latitude: raw.latitude || station?.latWgs84 || ''
  }
}

function openHouseDevicePopup(station: YZZBInterface) {
  const device = normalizeHouseDevicePayload(station)

  eventBus.emit(HOUSE_DEVICE_OPEN_EVENT, {
    device,
    deviceInfo: device,
    house: {},
    alarmList: device?.alarmList || device?.alarms || device?.eventList || device?.events || []
  })
}

function showStationDetail(station: YZZBInterface, entity: Cesium.Entity) {
  console.log(station, '点击站点信息。。。。。')
  popupEntity = entity

  popupVisible.value = true
  updatePopupPosition()
  bindPopupFollow()
}

const sourceName = 'YZDevicePoint'
const props = defineProps({
  chartConfig: {
    type: Object,
    required: true
  },
  // 全局参数
  globalParams: {
    type: Object as () => GlobalParams,
    required: false,
    default: () => ({
      params: reactive({
        yzt_tab: '',
        yzt_block: { class1: '', class2: '', class3: '' },
        yzt_map_ready: true,
        yzt_activeDispatch: {}
      })
    })
  },
  bus: {
    type: Object as () => EventBus,
    required: false
  }
})

// watch(
//   () => props.chartConfig.option,
//   (newVal, oldVal) => {
//     if (!mapReady.value) return

//     console.log('📌 chartConfig.option changed', newVal)
//     let areaName = newVal.ZbAddress || ''
//     reloadYZZBPoints(areaName)
//   },
//   {
//     deep: true
//   }
// )
// function reloadYZZBPoints(areaName) {
//   if (!window.viewer) return

//   // 1️⃣ 清理旧点位
//   destroyYZZBPoints()

//   // 2️⃣ 重新加载数据
//   loadYZZBData(areaName)

//   // 3️⃣ 重新绑定点击事件（只绑定一次也可以）
//   if (!pointClickHandler) {
//     YZZBPointClickEvent()
//   }
// }

const isDevelopment = process.env.NODE_ENV === 'development'
const mapReady = ref(false)

const mapLoaded = () => {
  mapReady.value = true
  viewer.scene.debugShowFramesPerSecond = isDevelopment
}

// 事件总线定义，方便组件间通信，本地暂时不能触发，只能上传测试环境测试,注意emit中的source是组件名称，方便区分事件来源
const eventBus = {
  on: (event: string, callback: (data: any) => void) => {
    props.bus?.on(event, ({ source, data }) => {
      console.log('🚀 ~ eventBus on:', event, source, data)
      if (source === sourceName) return
      callback(data)
    })
  },
  off: (event: string, callback: (eventData?: any) => void) => {
    props.bus?.off(event, callback)
  },
  emit: (event: string, data?: any) => {
    console.log('🚀 ~ eventBus emit:', event, data)
    props.bus?.emit(event, { data, source: sourceName })
  }
}

const normalizeDevicePoint = item => {
  const lng = Number(item?.longitude)
  const lat = Number(item?.latitude)
  const deviceType = String(item?.__deviceTypeCode || item?.deviceType || '')
  const deviceAddress = item?.deviceAddress || '-'
  const deviceName = item?.deviceName || '-'
  const deviceNo = item?.deviceNo || '-'
  const deviceStatus = item?.deviceStatus || '-'
  const sourceFrom = item?.sourceFrom || ''
  const popupTitle = sourceFrom ? `${deviceAddress}(${sourceFrom})` : deviceAddress

  if (!Number.isFinite(lng) || !Number.isFinite(lat)) return null

  return {
    id: item?.id || item?.deviceNo,
    locationName: deviceAddress || deviceName || item?.sourceFrom || '-',
    lngWgs84: lng,
    latWgs84: lat,
    deviceCode: deviceNo,
    deviceAddress,
    deviceStatus,
    deviceName,
    deviceNo,
    sourceFrom,
    popupTitle,
    coordinateText: `${item?.longitude || '-'},${item?.latitude || '-'}`,
    iconUrl: DEVICE_ICON_MAP[deviceType] || DEFAULT_DEVICE_ICON,
    raw: item
  }
}

const getDeviceListFromResponse = res => {
  const data = res?.data?.data ?? res?.data ?? {}
  if (Array.isArray(data)) return data
  if (Array.isArray(data?.content)) return data.content
  if (Array.isArray(data?.records)) return data.records
  if (Array.isArray(data?.list)) return data.list
  return []
}

const requestDevicePointList = deviceType =>
  axios({
    method: 'get',
    url: 'http://23.99.16.86:11001/api/v1/system/map/mapList/mapDeviceList',
    params: {
      deviceType
    },
    timeout: 50000
  }).then(res =>
    getDeviceListFromResponse(res).map(item => ({
      ...item,
      __deviceTypeCode: deviceType
    }))
  )

const loadYZZBData = async (deviceTypes = selectedDeviceTypes.value) => {
  if (!window.viewer || !Array.isArray(deviceTypes) || !deviceTypes.length) return
  const currentSerial = devicePointRequestSerial.value + 1
  devicePointRequestSerial.value = currentSerial

  try {
    const results = await Promise.all(
      deviceTypes.map(deviceType =>
        requestDevicePointList(deviceType).catch(err => {
          console.log(err, `设备点位接口错误: ${deviceType}`)
          return []
        })
      )
    )
    if (currentSerial !== devicePointRequestSerial.value) return

    const stations = results
      .flat()
      .map(normalizeDevicePoint)
      .filter(Boolean) as YZZBInterface[]

    addYZZBPoints(stations)
    YZZBPointClickEvent()
  } catch (err) {
    console.log(err, '设备点位接口错误')
  }
}

// 回调事件对象
const eventListeners = {
  // 地图加载完成
  'CMap:yzt_map_ready': () => {
    mapReady.value = true
    nextTick(() => {
      // loadYZZBData()
      console.log('🚀 ~ contextMenuKeys.value:', mapReady.value)
    })
  }
}
// window.eventListeners = eventListeners

// window.addEventListener('keydown', handleKeyDown)

const showZbPoint = ref(false)
const selectedDeviceTypes = ref<string[]>([])
const selectOpen = ref(false)
const devicePointRequestSerial = ref(0)
const isEssentialSafe = computed(() => !!props.chartConfig?.option?.isEssentialSafe)
const deviceTypeOptions = [
  { label: '烟感', value: '2002' },
  { label: '电气火灾', value: '2003' },
  { label: '智能门锁', value: '2004' },
  { label: '一氧化碳', value: '2006' }
]

function handleDeviceTypeChange(value) {
  const nextVisible = Array.isArray(value) && value.length > 0

  showZbPoint.value = nextVisible
  console.log('🚀 ~ handleDeviceTypeChange ~ selected:', value)
  devicePointRequestSerial.value += 1
  clickPopup()
  destroyYZZBPoints()

  if (!showZbPoint.value) {
    return
  } else {
    loadYZZBData(value)
  }
}

function isDeviceTypeSelected(value) {
  return selectedDeviceTypes.value.includes(value)
}

function toggleDeviceType(value) {
  const nextSelected = isDeviceTypeSelected(value)
    ? selectedDeviceTypes.value.filter(item => item !== value)
    : [...selectedDeviceTypes.value, value]

  selectedDeviceTypes.value = nextSelected
  handleDeviceTypeChange(nextSelected)
}

function handleSelectTriggerClick(event) {
  selectOpen.value = !selectOpen.value
}

function handleKeyDown() {
  showZbPoint.value = !showZbPoint.value
  console.log('🚀 ~ handleKeyDown ~ event:', showZbPoint.value)
  if (!showZbPoint.value) {
    // Object.values(YZZBEntities).forEach(entity => {
    //   entity.show = false // 设为false即隐藏
    // })
    clickPopup()
    destroyYZZBPoints()
    console.log('close Point!!!!', YZZBEntities)
  } else if (showZbPoint.value) {
    loadYZZBData()
  }
}
// 销毁值班点位
function destroyYZZBPoints() {
  if (!window.viewer) return
  // 1. 仅删除 YZZBEntities 缓存的实体（即 addYZZBPoints 创建的点位）
  Object.keys(YZZBEntities).forEach(id => {
    const entity = YZZBEntities[id]
    entity && window.viewer.entities.remove(entity)
  })
  // 2. 清空缓存（避免内存泄漏）
  Object.keys(YZZBEntities).forEach(key => delete YZZBEntities[key])
}

function destroyPointClickEvent() {
  if (!pointClickHandler) return

  pointClickHandler.destroy()
  pointClickHandler = null
}

// window.addEventListener('keydown', handleKeyDown)

// function handleKeyDown(event: KeyboardEvent) {
//   console.log('🚀 ~ handleKeyDown ~ event:', event.key)
//   if (event.key === '1') {
//     window.viewer?.entities.removeAll()
//   } else if (event.key === '3') {
//     loadYZZBData()
//   }
// }

// watch(
//   () => mapReady.value,
//   newVal => {
//     if (newVal) {
//       loadYZZBData()
//       YZZBPointClickEvent()
//       // initPointClickEvent()
//     }
//   },
//   { deep: true }
// )

// window.viewer.scene.postRender.addEventListener(() => {
//   updatePopupPosition()
// })

const showPoint = () => {
  Object.values(YZZBEntities).forEach(entity => {
    entity.show = false // 设为false即隐藏
  })
}

onMounted(() => {
  Object.keys(eventListeners).forEach(key => {
    eventBus.on(key, eventListeners[key])
  })
})

onUnmounted(() => {
  Object.keys(eventListeners).forEach(key => {
    eventBus.off(key, eventListeners[key])
  })
  clickPopup()
  destroyYZZBPoints()
  destroyPointClickEvent()
  mapReady.value = false
  console.log('🚀 ~ window.viewer:', window.viewer?.scene)
})

const handleDispatch = item => {
  console.log(`触发调度`, item)
  window.open(
    `taurusykz://taurusclient/action/avmeeting/conferenceCreateByIds?title=%E4%BC%9A%E8%AE%AE%E5%90%8D%E7%A7%B0&isVideoConference=false&calleeStaffIds=${item.ykzId}`
  )
  // 实际项目中可在这里编写调度的业务逻辑
}

function updatePopupPosition() {
  if (!popupEntity || !popupVisible.value) return
  if (!window.viewer) return

  const viewer = window.viewer

  // 1️⃣ 获取点位世界坐标
  const position = popupEntity.position?.getValue(
    viewer.clock.currentTime
  )
  if (!position) return

  // 2️⃣ 世界坐标 → viewport 坐标
  const windowPosition =
    Cesium.SceneTransforms.wgs84ToWindowCoordinates(
      viewer.scene,
      position
    )
  if (!windowPosition) return

  // 3️⃣ 找到 popup 的 offsetParent（关键）
  const popupEl =
    document.querySelector('.station-popup-wrappers') as HTMLElement
  if (!popupEl) return

  const offsetParent =
    popupEl.offsetParent as HTMLElement | null

  if (!offsetParent) return

  // 4️⃣ offsetParent 在 viewport 中的位置
  const parentRect = offsetParent.getBoundingClientRect()
  // 5️⃣ 计算“相对 offsetParent 的坐标”
  const left = windowPosition.x - parentRect.left
  const top = windowPosition.y - parentRect.top

  popupStyle.value = {
    left: `${left}px`,
    top: `${top}px`,
    transform: 'translate(-50%, calc(-100% - 18px))'
  }
}
let popupPostRenderHandler: (() => void) | null = null

function bindPopupFollow() {
  if (!window.viewer || popupPostRenderHandler) return

  popupPostRenderHandler = () => {
    updatePopupPosition()
  }

  window.viewer.scene.postRender.addEventListener(popupPostRenderHandler)
}
const clickPopup = () => {
  popupVisible.value = false

  if (popupPostRenderHandler && window.viewer) {
    window.viewer.scene.postRender.removeEventListener(
      popupPostRenderHandler
    )
    popupPostRenderHandler = null
  }
}
</script>

<script lang="ts">
export default {
  name: 'YZDevicePoint',
  version: '1.0.2'
}
</script>

<style lang="scss" scoped>
.station-popup-wrappers {
  position: absolute;
  width: 430px;
  pointer-events: none;
  z-index: 100000001;

  .device-point-card {
    width: 430px;
    overflow: hidden;
    color: #dff8ff;
    pointer-events: auto;
    position: relative;
    left: 50%;
    transform: translateX(-50%);
    background: linear-gradient(180deg, rgba(31, 55, 62, 0.98) 0%, rgba(36, 61, 68, 0.96) 100%);
    border: 1px solid rgba(120, 190, 205, 0.22);
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.34);
  }

  .device-point-card__header {
    position: relative;
    height: 38px;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0 34px;
    box-sizing: border-box;
    background: rgba(28, 48, 55, 0.98);
  }

  .device-point-card__title {
    min-width: 0;
    color: #ffffff;
    font-size: 17px;
    font-weight: 700;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .device-point-card__close {
    position: absolute;
    right: 9px;
    top: 5px;
    width: 24px;
    height: 24px;
    padding: 0;
    border: none;
    color: rgba(220, 236, 242, 0.82);
    font-size: 24px;
    line-height: 22px;
    background: transparent;
    cursor: pointer;
  }

  .device-point-card__body {
    padding: 10px 14px 14px;
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 8px 12px;
    background: linear-gradient(180deg, rgba(54, 85, 95, 0.78) 0%, rgba(39, 63, 70, 0.9) 100%);
  }

  .device-point-card__row {
    min-height: 46px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: flex-start;
    min-width: 0;
    padding: 6px 8px;
    box-sizing: border-box;
    border: 1px solid rgba(118, 163, 175, 0.16);
    background: rgba(35, 78, 92, 0.3);
  }

  .device-point-card__label {
    flex: 0 0 auto;
    color: #ffffff;
    font-size: 13px;
    font-weight: 700;
    line-height: 1.2;
    opacity: 0.88;
  }

  .device-point-card__row:nth-child(1) .device-point-card__label,
  .device-point-card__row:nth-child(2) .device-point-card__label {
    max-width: 100%;
    font-size: 0;
    display: flex;
    flex-direction: column;
  }

  .device-point-card__row:nth-child(1) .device-point-card__label::before {
    content: '设备状态';
    font-size: 13px;
  }

  .device-point-card__row:nth-child(2) .device-point-card__label::before {
    content: '设备名称';
    font-size: 13px;
  }

  .device-point-card__value {
    min-width: 0;
    max-width: 100%;
    margin-top: 4px;
    color: #7de6ff;
    font-size: 14px;
    font-weight: 700;
    line-height: 1.25;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

.station-popup {
  background: rgba(0, 0, 0, 0.75);
  color: #fff;
  padding: 8px 12px;
  border-radius: 6px;
  font-size: 14px;
}
.device-select {
  position: absolute;
  z-index: 100000000;
  width: 136px;
  height: 58px;
  background: url('./img/nochoose.png') center / 100% 100% no-repeat;
  cursor: pointer;
  user-select: none;
  transition: background 0.2s ease;
  text-align: center;
  line-height: 58px;

  &--active {
    background-image: url('./img/choosed.png');
  }

  &--bz {
    width: 190px;
    height: 46px;
    box-sizing: border-box;
    border: 1px solid rgba(59, 179, 255, 0.78);
    color: #f2fbff;
    line-height: 46px;
    background:
      linear-gradient(180deg, rgba(12, 111, 181, 0.72) 0%, rgba(3, 67, 126, 0.88) 100%),
      radial-gradient(circle at 20% 0%, rgba(72, 211, 255, 0.35), transparent 50%);
    box-shadow: inset 0 0 16px rgba(56, 178, 255, 0.24), 0 0 8px rgba(35, 153, 255, 0.18);
    transition: border-color 0.18s ease, box-shadow 0.18s ease;
  }

  &--bz:hover {
    border-color: rgba(135, 230, 255, 0.95);
    box-shadow: inset 0 0 16px rgba(56, 178, 255, 0.36), 0 0 14px rgba(35, 153, 255, 0.35);
  }

  &--bz &__arrow {
    right: 18px;
    left: auto;
    width: 0;
    height: 0;
    margin-left: 0;
    background: none;
    border-left: 6px solid transparent;
    border-right: 6px solid transparent;
    border-top: 8px solid rgba(242, 251, 255, 0.92);
  }

  &--bz.device-select--active {
    border-color: rgba(255, 228, 76, 0.95);
    background:
      linear-gradient(180deg, rgba(255, 228, 76, 0.92) 0%, rgba(255, 180, 20, 0.88) 100%),
      radial-gradient(circle at 20% 0%, rgba(255, 245, 150, 0.6), transparent 50%);
    box-shadow: inset 0 0 16px rgba(255, 200, 40, 0.4), 0 0 14px rgba(255, 180, 20, 0.4);
  }

  &--bz &__label {
    color: #f2fbff;
    font-family: 'UISDCBiaoTiHei', Microsoft YaHei, sans-serif;
    font-size: 22px;
    font-weight: 700;
    letter-spacing: 2px;
    text-shadow: none;
  }

  &--bz &__dropdown {
    bottom: 61px;
    width: 190px;
  }

  &__arrow {
    position: absolute;
    right: 12px;
    top: 50%;
    width: 16px;
    height: 10px;
    transform: translateY(-50%);
    pointer-events: none;
    background: url('./img/jt.png') center / 100% 100% no-repeat;
  }

  &__label {
    color: rgba(232, 249, 255, 0.96);
    font-size: 20px;
    font-weight: 600;
    line-height: 1;
    pointer-events: none;
    text-shadow: 0 0 8px rgba(58, 151, 255, 0.85);
  }

  &__dropdown {
    position: absolute;
    left: 0;
    bottom: 62px;
    width: 138px;
    height: 132px;
    padding: 10px 8px;
    box-sizing: border-box;
    border: 1px solid rgba(69, 193, 255, 0.42);
    background: url('./img/bg.png') center / 100% 100% no-repeat;
    box-shadow: 0 0 18px rgba(30, 151, 255, 0.28);
  }

  &__option {
    height: 28px;
    display: flex;
    align-items: center;
    gap: 12px;
    color: rgba(229, 248, 255, 0.92);
    font-size: 15px;
    font-weight: 700;
    line-height: 28px;
    cursor: pointer;
    text-shadow: 0 0 8px rgba(34, 121, 204, 0.72);
  }

  &__option:hover {
    color: #ffffff;
  }

  &__checkbox {
    width: 22px;
    height: 22px;
    flex: 0 0 auto;
    box-sizing: border-box;
    border: 2px solid rgba(47, 113, 177, 0.95);
    background: rgba(12, 38, 82, 0.38);
    position: relative;
  }

  &__option--checked &__checkbox {
    border-color: rgba(47, 113, 177, 0.95);
    background: rgba(12, 38, 82, 0.38);
  }

  &__option--checked &__checkbox::after {
    content: '';
    position: absolute;
    left: 4px;
    top: 4px;
    width: 10px;
    height: 10px;
    background: #ffffff;
  }

  &__option-text {
    min-width: 0;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
  }
}
.station-popup-card {
  position: absolute;      // ⭐ 关键
  z-index: 99999999;
  width: 300px;
  pointer-events: auto;
}
</style>
