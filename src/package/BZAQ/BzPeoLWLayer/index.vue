<template>
  <div class="button" style="display: flex;gap: 10px;">
    <div @click="showLine">
      <span>{{ isShowLine?'交通线路隐藏':'交通线路显示'}}</span>
    </div>
    <div @click="toggleCamera" style="display: flex;align-items: center;justify-content: center;gap: 6px;">
      <img src="./img/icon_camera_total.png" style="width: 18px;height: 18px;" />
      <span>{{ isShowCamera ? '交通摄像头隐藏' : '交通摄像头显示' }}</span>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, defineProps, reactive, onMounted, onUnmounted, toRefs, computed, PropType } from 'vue'
import type { GlobalParams, EventBus } from '@/package/index.d'
import axios from 'axios'
import { CreateComponentType } from '@/package/index.d'

const sourceName = 'BzPeoLWLayer'

const props = defineProps({
  chartConfig: {
    type: Object as PropType<CreateComponentType>,
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
const isDevelopment = process.env.NODE_ENV === 'development'
const dataMappingRef = ref()
const mapReady = ref(false)
const { w, h } = toRefs(props.chartConfig.attr)
// 容器样式
const containerStyle = computed(() => ({
  width: w.value + 'px',
  height: h.value + 'px'
}))

const mapLoaded = () => {
  mapReady.value = true
  console.log('---1111---')
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
const allRoadDevice_URL = 'http://23.210.227.34:23343/yzqzlzx/api/boot/system/roadDevice/queryAllRoadDevice'
const queryRoadDevice_URL = 'http://23.210.227.34:23343/yzqzlzx/api/boot/system/home/queryRoadDevice'
const line_URL = 'http://23.210.227.34:23343/yzqzlzx/api/boot/system/home/line'

// 回调事件对象
const eventListeners = {
  // 地图加载完成
  'CMap:yzt_map_ready': () => {
    mapReady.value = true
    console.log('🚀 ~ contextMenuKeys.value:', mapReady.value)
    // dataMappingRef.value?.setEnabledContextMenu(contextMenuKeys.value)
    // 获取全局配置的固定围栏
    // geoFences.value = props.globalParams.params.areaConfig.map(item => JSON.stringify(item.data))
  }
}
window.eventListeners = eventListeners

const getLineData = () => {
  return axios.post(line_URL).then(res => {
    console.log('🚀 ~ getLineData ~ res:', res)
    addLineData(res.data.data)
  })
}

interface YZLWLayer {
  roadName: string
}
const YZLWLayer: Record<string, Cesium.Entity> = {}

const buildLinePositions = (arr) => {
  const result = []
  for (let i = 0; i < arr.length; i += 2) {
    result.push(arr[i])
    result.push(arr[i + 1])
    result.push(360) // ⭐统一抬高 30m（关键）
  }
  return result
}
const addLineData = _data => {

  if (!_data || !_data.length) return;
  // 步骤1：清空YZLWLayer内部属性（替代 YZLWLayer = {}，避免const赋值报错）
  // 先移除viewer中旧的线实体，再删除对象属性
  Object.keys(YZLWLayer).forEach(roadName => {
    const oldEntity = YZLWLayer[roadName];
    if (oldEntity) {
      window.viewer.entities.remove(oldEntity); // 从viewer中移除旧实体
    }
    delete YZLWLayer[roadName]; // 删除对象中的旧引用
  });
  // 步骤2：重新添加实体（确保roadName唯一，避免覆盖）
  const roadNameSet = new Set(); // 检测重复roadName

  _data?.map(item => {
    // 处理重复roadName：自动生成唯一标识，避免覆盖
    let uniqueKey = item.roadName;
    if (roadNameSet.has(uniqueKey)) {
      console.warn(`重复的roadName：${uniqueKey}，自动生成唯一键`);
      uniqueKey = `${uniqueKey}_${Date.now()}_${Math.random().toString(36).slice(2)}`;
    }
    roadNameSet.add(uniqueKey);

    const line = window.viewer.entities.add({
      show: isShowLine.value,
      // polyline: {
      //   positions: Cesium.Cartesian3.fromDegreesArray(item.andLonList),
      //   width: 2,
      //   material: Cesium.Color.fromCssColorString(item.color || '#ffffff')
      // },
      polyline: {
        positions: Cesium.Cartesian3.fromDegreesArrayHeights(buildLinePositions(item.andLonList),),
        width: 2,
        material: Cesium.Color.fromCssColorString(item.color || '#ffffff')
      },
      // polyline: {
      //   positions: Cesium.Cartesian3.fromDegreesArray(item.andLonList),
      //   width: 2,
      //
      //   material: new Cesium.PolylineOutlineMaterialProperty({
      //     color: Cesium.Color.fromCssColorString(item.color || '#ffffff'),
      //     outlineWidth: 0,
      //     outlineColor: Cesium.Color.WHITE
      //   }),
      //
      //   // ⭐关键：关闭深度测试（核心）
      //   depthFailMaterial: new Cesium.PolylineOutlineMaterialProperty({
      //     color: Cesium.Color.fromCssColorString(item.color || '#ffffff'),
      //     outlineWidth: 0,
      //     outlineColor: Cesium.Color.WHITE
      //   })
      // },

      properties: new Cesium.PropertyBag({
        type: 'routeLine',
        road: item,          // ✅ 把路段对象放进去（完整业务数据）
        roadName: item.roadName
      })
    })
    YZLWLayer[uniqueKey] = line
  })
  console.log('遍历后的数据',YZLWLayer)
  initLineClickEvent()
}

let clickHandler: Cesium.ScreenSpaceEventHandler | null = null
let selectedLine: Cesium.Entity | null = null

const clearLineLayer = () => {
  Object.keys(YZLWLayer).forEach(roadName => {
    const entity = YZLWLayer[roadName]
    if (entity) {
      window.viewer.entities.remove(entity)
    }
    delete YZLWLayer[roadName]
  })
  selectedLine = null
}

const highlightLine = (entity: Cesium.Entity) => {
  if (!entity?.polyline) return

  const road = entity.properties?.road?.getValue(Cesium.JulianDate.now())

  const baseColor = Cesium.Color.fromCssColorString(road?.color || '#ffffff')

  entity.polyline.width = 10

  entity.polyline.material = new Cesium.PolylineOutlineMaterialProperty({
    color: baseColor,        // 保持原颜色
    outlineWidth: 4,
    outlineColor: Cesium.Color.WHITE
  })
}
const resetLineStyle = (entity: Cesium.Entity) => {
  if (!entity?.polyline) return

  const road = entity.properties?.road?.getValue(Cesium.JulianDate.now())

  entity.polyline.width = 2

  entity.polyline.material = Cesium.Color.fromCssColorString(
      road?.color || '#ffffff'
  )
}

function initLineClickEvent() {
  if (clickHandler) {
    clickHandler.destroy()
    clickHandler = null
  }

  clickHandler = new Cesium.ScreenSpaceEventHandler(window.viewer.scene.canvas)

  clickHandler.setInputAction((movement) => {
    const picked = window.viewer.scene.pick(movement.position)
    if (!Cesium.defined(picked) || !picked.id) return

    const entity = picked.id as Cesium.Entity
    const type = entity.properties?.type?.getValue?.(Cesium.JulianDate.now())

    // 点击线路
    if (type === 'routeLine') {
      const road = entity.properties?.road?.getValue(Cesium.JulianDate.now())
      console.log('点击到的路段业务数据:', road)
      // 先恢复之前选中的线
      if (selectedLine && selectedLine !== entity) {
        resetLineStyle(selectedLine)
      }
      // 高亮当前线
      highlightLine(entity)
      selectedLine = entity

      const newLonLatList = fillRoadPoints(road.andLonList, 25)
      const params = { lonLatList: newLonLatList }
      console.log('补点前:', road.andLonList)
      console.log('补点后:', newLonLatList)

      axios.post(queryRoadDevice_URL, params).then(res => {
        console.log('🚀 ~ 路段查询设备结果:', res.data.data)
        let roadSx = res?.data?.data?.map(item=>{
          return item.deviceCode
        })
        eventBus.emit('YZVideoPop:openPop',roadSx)
        // eventBus.emit('MatrixOfVideoPop:openPop',["50011400106001000238"] )
      })
      return
    }

    // 点击摄像头点位
    if (type === 'cameraPoint') {
      const device = entity.properties?.device?.getValue(Cesium.JulianDate.now())
      console.log('点击到的摄像头点位业务数据:', device)
      eventBus.emit('YZVideoPop:openPop',[device.deviceCode])
      // eventBus.emit('MatrixOfVideoPop:openPop',["50011400106001000238"] )
      return
    }
  }, Cesium.ScreenSpaceEventType.LEFT_CLICK)

  clickHandler.setInputAction((movement) => {
    const picked = window.viewer.scene.pick(movement.endPosition)

    if (!Cesium.defined(picked) || !picked.id) {
      window.viewer.canvas.style.cursor = 'default'
      return
    }

    const entity = picked.id as Cesium.Entity
    const type = entity.properties?.type?.getValue?.(Cesium.JulianDate.now())

    if (type === 'routeLine' || type === 'cameraPoint') {
      window.viewer.canvas.style.cursor = 'pointer'
    } else {
      window.viewer.canvas.style.cursor = 'default'
    }
  }, Cesium.ScreenSpaceEventType.MOUSE_MOVE)
}
const EARTH_RADIUS = 6378137 // 地球半径，单位：米

// 角度转弧度
const toRad = (deg: number) => (deg * Math.PI) / 180

// 计算两点球面距离（米）
const getDistance = (lng1: number, lat1: number, lng2: number, lat2: number) => {
  const radLat1 = toRad(lat1)
  const radLat2 = toRad(lat2)
  const deltaLat = radLat2 - radLat1
  const deltaLng = toRad(lng2 - lng1)

  const a =
      Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
      Math.cos(radLat1) * Math.cos(radLat2) * Math.sin(deltaLng / 2) * Math.sin(deltaLng / 2)

  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
  return EARTH_RADIUS * c
}
const fillRoadPoints = (lonLatList: (string | number)[], maxDistance = 25) => {
  if (!Array.isArray(lonLatList) || lonLatList.length < 4) return lonLatList

  const result: string[] = []

  for (let i = 0; i < lonLatList.length - 2; i += 2) {
    const lng1 = Number(lonLatList[i])
    const lat1 = Number(lonLatList[i + 1])
    const lng2 = Number(lonLatList[i + 2])
    const lat2 = Number(lonLatList[i + 3])

    if (
        !Number.isFinite(lng1) ||
        !Number.isFinite(lat1) ||
        !Number.isFinite(lng2) ||
        !Number.isFinite(lat2)
    ) {
      continue
    }

    // 先放入当前起点
    if (i === 0) {
      result.push(String(lng1), String(lat1))
    }

    const distance = getDistance(lng1, lat1, lng2, lat2)

    if (distance > maxDistance) {
      // 只插入一个中点
      const midLng = (lng1 + lng2) / 2
      const midLat = (lat1 + lat2) / 2
      result.push(String(midLng), String(midLat))
    }

    // 再放入终点
    result.push(String(lng2), String(lat2))
  }

  return result
}

// window.addEventListener('keydown', handleKeyDown)
const isShowLine = ref(false)
async function showLine() {
  isShowLine.value = !isShowLine.value
  if (isShowLine.value) {
    console.log('show Line!!!!', YZLWLayer)

    if (!Object.keys(YZLWLayer).length) {
      await getLineData()
    } else {
      Object.values(YZLWLayer).forEach(entity => {
        entity.show = true // 设为false即隐藏
      })
    }
  } else {
    Object.values(YZLWLayer).forEach(entity => {
      entity.show = false // 设为false即隐藏
    })
  }
}


// const showLine = () => {
//   Object.values(YZLWLayer).forEach(entity => {
//     entity.show = false // 设为false即隐藏
//   })
// }



//以下是渲染摄像头点位
const YZCameraLayer: Record<string, Cesium.Entity> = {}
const isShowCamera = ref(false) // 默认隐藏

const getCameraData = () => {
  return axios.post(allRoadDevice_URL).then(res => {
    const list = res?.data?.data || []
    console.log('🚀 ~ 摄像头点位数据:', list)
    addCameraData(list)
  })
}

const clearCameraLayer = () => {
  Object.keys(YZCameraLayer).forEach(key => {
    const entity = YZCameraLayer[key]
    if (entity) {
      window.viewer.entities.remove(entity)
    }
    delete YZCameraLayer[key]
  })
}

const addCameraData = (_data) => {
  clearCameraLayer()

  if (!_data || !_data.length) return

  const keySet = new Set()

  _data.forEach((item, index) => {
    const lng = Number(item.lngWgs84)
    const lat = Number(item.latWgs84)

    if (!Number.isFinite(lng) || !Number.isFinite(lat)) return

    let uniqueKey = String(item.id ?? `camera_${index}`)
    if (keySet.has(uniqueKey)) {
      uniqueKey = `${uniqueKey}_${Date.now()}_${Math.random().toString(36).slice(2)}`
    }
    keySet.add(uniqueKey)

    const entity = window.viewer.entities.add({
      position: Cesium.Cartesian3.fromDegrees(lng, lat,360),

      // 当前先用 point 渲染
      // point: {
      //   pixelSize: 12,
      //   color: Cesium.Color.fromCssColorString('#ff4d4f'),
      //   outlineColor: Cesium.Color.WHITE,
      //   outlineWidth: 2,
      //   disableDepthTestDistance: Number.POSITIVE_INFINITY
      // },

      // 后续如需 icon，打开下面即可
      billboard: {
        image: 'http://23.210.227.34:23343/yzqzlzx/yz-cyzx/2026/03/06/1772778862673.png',
        width: 28,
        height: 28,
        verticalOrigin: Cesium.VerticalOrigin.BOTTOM,
        disableDepthTestDistance: Number.POSITIVE_INFINITY
      },

      properties: new Cesium.PropertyBag({
        type: 'cameraPoint',
        device: item,
        id: item.id,
        deviceName: item.deviceName,
        deviceCode: item.deviceCode
      })
    })

    YZCameraLayer[uniqueKey] = entity
  })

  console.log('摄像头图层实体:', YZCameraLayer)
}

const toggleCamera = async () => {
  isShowCamera.value = !isShowCamera.value

  if (isShowCamera.value) {
    // 显示时才请求，保证数据最新
    await getCameraData()
  } else {
    // 隐藏时清空图层
    clearCameraLayer()
  }
}

onMounted(() => {
  getLineData()
  // initLineClickEvent()

  Object.keys(eventListeners).forEach(key => {
    eventBus.on(key, eventListeners[key])
  })
})

onUnmounted(() => {
  if (clickHandler) {
    clickHandler.destroy()
    clickHandler = null
  }

  clearCameraLayer()
  clearLineLayer()

  Object.keys(eventListeners).forEach(key => {
    eventBus.off(key, eventListeners[key])
  })

  mapReady.value = false
  console.log('🚀 ~ window.viewer:', window.viewer.scene)
})
</script>

<script lang="ts">
export default {
  name: 'BzPeoLWLayer',
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

.button {
  position: absolute;
  z-index: 200;
  font-family: 'AlibabaPuHuiTi', sans-serif;
  div{
    width: 180px;
    height: 40px;
    line-height: 40px;
    text-align: center;
    font-size: 16px;
    font-weight: 700;
    border-radius: 6px;
    cursor: pointer;
    user-select: none;
    transition: all 0.2s ease;
    /* 按钮背景图 */
    background: url('./img/bg_mid_bottom_data.png') center / 100% 100% no-repeat;

    /* 悬停效果 */
    &:hover {
      transform: translateY(-1px);
      box-shadow: 0 4px 10px rgba(0, 0, 0, 0.15);
    }

    /* 点击效果 */
    &:active {
      transform: translateY(0);
      box-shadow: none;
    }
  }

  span {
    /* 文字渐变 */
    background: linear-gradient(180deg, #FFFFFF 0%, #5FBCFF 100%);
    -webkit-background-clip: text;
    background-clip: text;
    color: transparent;
  }
}
</style>
