<template>
  <!-- <div v-show="popupVisible" class="station-popup-wrappers" :style="popupStyle">
    <div class="station-card">
      <div class="card-header">
        <span class="title-text">
          {{ '点位信息' }}
        </span>
        <span class="close-btn" @click="clickPopup">×</span>
      </div>

      <div class="card-body">
        <div class="info-row">
          <label>单位：</label>
          <span>{{ currentStation?.unitName || '-' }}</span>
        </div>
        <div class="info-row">
          <label>点位名称：</label>
          <span>
            {{ currentStation?.locationName }}
          </span>
        </div>
        <div class="info-row">
          <label>联系人：</label>
          <span>{{ currentStation?.personName || '-' }}</span>
        </div>
        <div class="info-row">
          <label>联系电话：</label>
          <span>{{ currentStation?.personMobile || '-' }}</span>
        </div> 
      </div>

      <div class="card-footer">
        <button class="dispatch-btn" :disabled="!currentStation?.ykzId" @click="handleDispatch(currentStation)">
          调度
        </button>
      </div>
    </div>
  </div> -->

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
  <div class="button" :class="{ btnActive: showZbPoint, 'button--bz': isEssentialSafe }" @click="handleKeyDown">
    {{ showZbPoint ? '视频监控' : '视频监控' }}
  </div>
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
const ZB_POINT_3D_HEIGHT = 360

interface YZZBInterface {
  deviceName: string
  latitude: number
  longitude: number
  iconUrl: string
  deviceNo: string
  id?: string
}

const YZZBEntities: Record<string, Cesium.Entity> = {}

function getZBPointPosition(station: YZZBInterface) {
  return Cesium.Cartesian3.fromDegrees(station.longitude, station.latitude, ZB_POINT_3D_HEIGHT)
}

function addYZZBPoints(stations: YZZBInterface[]) {
  stations.forEach((station,index) => {
    console.log(station)
    // window.viewer.entities.add(YZZBEntities)
    const entity = window.viewer.entities.add({
      id: station.id,
      position: getZBPointPosition(station),
      billboard: {
        image: '/api/api/download/2062465554742038530',
        // image: station.iconUrl.replace('http://23.99.15.31:8001/', '/yzqzlzx/'),
        scale: 0.3,
        verticalOrigin: Cesium.VerticalOrigin.BOTTOM
      },
      label: {
        text: '',
        font: '14px sans-serif',
        style: Cesium.LabelStyle.FILL_AND_OUTLINE,
        outlineColor: Cesium.Color.BLACK,
        outlineWidth: 2,
        pixelOffset: new Cesium.Cartesian2(0, -28),
        disableDepthTestDistance: 1e9
      },
      properties: {
        type: 'stationPoint',
        stationData: station // ⭐ 把完整业务数据挂进去
      }
    })

    YZZBEntities[station.id] = entity
  })
}

let pointClickHandler: Cesium.ScreenSpaceEventHandler | null = null

function destroyPointClickHandler() {
  if (pointClickHandler) {
    pointClickHandler.destroy()
    pointClickHandler = null
  }
}

function YZZBPointClickEvent() {
  if (!window.viewer) return
  destroyPointClickHandler()
  pointClickHandler = new Cesium.ScreenSpaceEventHandler(window.viewer.scene.canvas)

  pointClickHandler.setInputAction(async movement => {
    const picked = window.viewer.scene.pick(movement.position)
    if (!Cesium.defined(picked) || !picked.id) return

    const entity = picked.id as Cesium.Entity
    const type = entity.properties?.type?.getValue()

    if (type === 'stationPoint') {
      const station: YZZBInterface = entity.properties.stationData.getValue()
      console.log('点击站点信息：', station)
      console.log('点击站点实体：', station)
      currentStation.value = station
      await emitEventDetail(station.deviceNo)
      // axios
      //   .post(`http://23.210.227.34:23343/yzqzlzx/api/boot/system/home/duty/info?name=${station.locationName}`)
      //   .then(res => {
      //     if (res.data.code !== '000000') return
      //     console.log('🚀 ~ getLineData ~ res:', res)
      //     tableData.value = res.data.data
      //打开对应监控
      showStationDetail(station, entity)
      // })
    }
  }, Cesium.ScreenSpaceEventType.LEFT_CLICK)
}

const emitEventDetail = async (deviceNo: string) => {
  if (!deviceNo) return
  try {
    props.bus?.emit('YZVideoPop:openPop', deviceNo ? [deviceNo] : [])
  } catch (error) {
    console.error('YZEventMapVideoPoint fetch event detail failed:', error)
  }
}

function showStationDetail(station: YZZBInterface, entity: Cesium.Entity) {
  console.log(station, '点击站点信息。。。。。')
  popupEntity = entity

  popupVisible.value = true
  updatePopupPosition()
  bindPopupFollow()
}

const sourceName = 'YZEventMapVideoPoint'
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

const loadYZZBData = () => {
  axios.get(`http://23.99.16.86:11001/api/v1/system/map/mapList/mapDeviceList?deviceType=2001`).then(res => {
    if (res.data.code !== '000000') return
    console.log('🚀 ~ getTable ~ res:', res)
    const stations: YZZBInterface[] = res.data.data
    stations.forEach((item,index)=>{
      stations[index].id = `station-${item.id}-${item.deviceNo}-${index}`
    })
    // const stations: YZZBInterface[] = [
    //   {
    //     locationName: '测试2',
    //     longitude: 108.8041967,
    //     latitude: 28.8420493,
    //     iconUrl: 'http://23.99.15.31:8001/yz-cyzx/2025/12/02/zd.png'
    //   },
    //   {
    //     locationName: '测试3',
    //     longitude: 106.6066249,
    //     latitude: 29.5646345,
    //     iconUrl: 'http://23.99.15.31:8001/yz-cyzx/2025/12/02/zd.png'
    //   }
    // ]
    addYZZBPoints(stations)
  })
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
const isEssentialSafe = computed(() => !!props.chartConfig?.option?.isEssentialSafe)
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
    YZZBPointClickEvent()
  }
}
// 销毁值班点位
function destroyYZZBPoints() {
  if (!window.viewer) return
  destroyPointClickHandler()
  // 1. 仅删除 YZZBEntities 缓存的实体（即 addYZZBPoints 创建的点位）
  Object.keys(YZZBEntities).forEach(id => {
    const entity = YZZBEntities[id]
    entity && window.viewer.entities.remove(entity)
  })
  // 2. 清空缓存（避免内存泄漏）
  Object.keys(YZZBEntities).forEach(key => delete YZZBEntities[key])
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
  destroyPointClickHandler()
  clickPopup()
  mapReady.value = false
  // console.log('🚀 ~ window.viewer:', window.viewer.scene)
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
  const position = popupEntity.position?.getValue(viewer.clock.currentTime)
  if (!position) return

  // 2️⃣ 世界坐标 → viewport 坐标
  const windowPosition = Cesium.SceneTransforms.wgs84ToWindowCoordinates(viewer.scene, position)
  if (!windowPosition) return

  // 3️⃣ 找到 popup 的 offsetParent（关键）
  const popupEl = document.querySelector('.station-popup-wrappers') as HTMLElement
  if (!popupEl) return

  const offsetParent = popupEl.offsetParent as HTMLElement | null

  if (!offsetParent) return

  // 4️⃣ offsetParent 在 viewport 中的位置
  const parentRect = offsetParent.getBoundingClientRect()

  // 5️⃣ 计算“相对 offsetParent 的坐标”
  const left = windowPosition.x - parentRect.left
  const top = windowPosition.y - parentRect.top

  popupStyle.value = {
    left: `${left}px`,
    top: `${top - 100}px`,
    transform: 'translate(-50%, -100%)'
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
    window.viewer.scene.postRender.removeEventListener(popupPostRenderHandler)
    popupPostRenderHandler = null
  }
}
</script>

<script lang="ts">
export default {
  name: 'YZEventMapVideoPoint',
  version: '1.0.2'
}
</script>

<style lang="scss" scoped>
.station-popup-wrappers {
  position: absolute;
  // left: 50%;
  // top: 50%;
  // transform: translate(-50%, -50%);
  width: 900px;
  pointer-events: none;
  z-index: 100;

  /* 关闭按钮：绝对定位靠右，避免影响标题居中 */
  .close-btn {
    // position: absolute;
    z-index: 100000000;
    right: 16px; /* 与单元格右侧保持间距 */
    color: #ffffff; /* 文字白色，匹配表格风格 */
    font-size: 18px;
    cursor: pointer;
    width: 24px;
    height: 24px;
    line-height: 24px;
    text-align: center;
    border-radius: 50%; /* 圆形按钮（可选） */
    background-color: rgba(255, 255, 255, 0.2); /* 半透明背景 */
    transition: background-color 0.2s;
  }

  /* 按钮hover效果（可选） */
  .close-btn:hover {
    background-color: rgba(255, 255, 255, 0.4);
  }

  /* 调度按钮默认样式（淡蓝色可点击） */
  .dispatch-btn {
    color: #87cefa;
    cursor: pointer;
    text-decoration: underline;
  }

  /* 无ID时的禁用样式（灰色不可点击） */
  .dispatch-btn.disabled {
    color: #cccccc;
    cursor: not-allowed;
    text-decoration: none;
  }
  /* 分页控件禁用状态样式（新增） */
  .table-pagination button:disabled {
    background: #6687ff;
    cursor: not-allowed;
  }

  .station-card {
    width: 320px;
    background: rgba(15, 46, 102, 0.95);
    border-radius: 8px;
    color: #fff;
    pointer-events: auto;
    position: relative;
    z-index: 9999999;
    left: 50%;
    transform: translateX(-50%);
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px 12px;
    background: #165dff;
    border-radius: 8px 8px 0 0;
    font-weight: 600;
  }

  .card-body {
    padding: 12px;
    font-size: 14px;
  }

  .info-row {
    margin-bottom: 8px;
    display: flex;
  }

  .info-row label {
    width: 80px;
    color: #cce0ff;
  }

  .card-footer {
    padding: 10px 12px;
    text-align: right;
  }

  .card-footer .dispatch-btn {
    background: #165dff;
    border: none;
    padding: 6px 14px;
    border-radius: 4px;
    color: #fff;
    cursor: pointer;
  }

  .card-footer .dispatch-btn:disabled {
    background: #888;
    cursor: not-allowed;
  }
}

.station-popup {
  background: rgba(0, 0, 0, 0.75);
  color: #fff;
  padding: 8px 12px;
  border-radius: 6px;
  font-size: 14px;
}
.button {
  font-size: 20px;
  font-weight: 600;
  position: absolute;
  z-index: 100000000;
  cursor: pointer;
  width: 136px;
  height: 58px;
  line-height: 58px;
  text-align: center;
  background: url('http://23.210.227.34:23343/api/api/download/2062360648492306434');
  background-size: 100%;
  transition: all 0.2s ease;
  border-radius: 6px;
  color: rgba(232, 249, 255, 0.96);
  &.btnActive {
    background: url('http://23.210.227.34:23343/api/api/download/2062360768885608450');
    background-size: 100%;
  }

  &--bz {
    width: 190px;
    height: 46px;
    box-sizing: border-box;
    border: 1px solid rgba(59, 179, 255, 0.78);
    border-radius: 0;
    color: #f2fbff;
    font-family: 'UISDCBiaoTiHei', Microsoft YaHei, sans-serif;
    font-size: 22px;
    font-weight: 700;
    line-height: 46px;
    letter-spacing: 2px;
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

  &--bz:active {
    background:
      linear-gradient(180deg, rgba(72, 211, 255, 0.85) 0%, rgba(12, 111, 181, 0.92) 100%),
      radial-gradient(circle at 20% 0%, rgba(120, 240, 255, 0.5), transparent 50%);
    box-shadow: inset 0 0 20px rgba(56, 178, 255, 0.5), 0 0 18px rgba(35, 153, 255, 0.45);
  }

  &--bz.btnActive {
    border-color: rgba(255, 228, 76, 0.95);
    background:
      linear-gradient(180deg, rgba(255, 228, 76, 0.92) 0%, rgba(255, 180, 20, 0.88) 100%),
      radial-gradient(circle at 20% 0%, rgba(255, 245, 150, 0.6), transparent 50%);
    box-shadow: inset 0 0 16px rgba(255, 200, 40, 0.4), 0 0 14px rgba(255, 180, 20, 0.4);
  }
}
.station-popup-card {
  position: absolute; // ⭐ 关键
  z-index: 99999999;
  width: 300px;
  pointer-events: auto;
}
</style>
