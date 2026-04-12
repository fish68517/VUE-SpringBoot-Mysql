<template>
  <div class="footprints-container">
    <el-card class="control-panel" shadow="hover">
      <div class="header">
        <h2>我的旅行足迹</h2>
        <el-tag type="success">已记录足迹：{{ footprints.length }} 个</el-tag>
      </div>
    </el-card>

    <div id="container" class="map-container"></div>
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref, shallowRef } from 'vue'
import AMapLoader from '@amap/amap-jsapi-loader'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../stores/userStore'
import {
  getPresetFootprintsByUserId
} from '../mock/presetFootprints'

const userStore = useUserStore()
const map = shallowRef(null)
const AMapObj = shallowRef(null)
const footprints = ref([])

const currentUserId = computed(() => userStore.user?.id ?? null)

const fetchFootprints = async () => {
  if (!currentUserId.value) {
    footprints.value = []
    ElMessage.warning('当前未获取到登录用户 ID，无法匹配预置足迹数据')
    return
  }

  footprints.value = getPresetFootprintsByUserId(currentUserId.value)
  console.log('当前用户匹配到的足迹数据:', {
    userId: currentUserId.value,
    footprints: footprints.value
  })

  if (footprints.value.length === 0) {
    ElMessage.info(`当前用户 ID ${currentUserId.value} 暂无预置足迹，请在预置数据中新增`)
  }
}

const initMap = () => {
  window._AMapSecurityConfig = {
    securityJsCode: '76805393edb2f03827a55eafa36fc6d2'
  }

  AMapLoader.load({
    key: 'e2706bc1e334def5699349076d5f6d58',
    version: '2.0',
    plugins: ['AMap.ToolBar', 'AMap.Scale', 'AMap.Marker', 'AMap.InfoWindow']
  }).then((AMap) => {
    AMapObj.value = AMap
    map.value = new AMap.Map('container', {
      viewMode: '3D',
      zoom: 5,
      center: getDefaultCenter()
    })

    map.value.add(new AMap.ToolBar())
    map.value.add(new AMap.Scale())
    renderMarkers()
  }).catch(e => {
    console.error('地图加载失败:', e)
  })
}

const getDefaultCenter = () => {
  if (footprints.value.length > 0) {
    const firstFootprint = footprints.value[0]
    return [firstFootprint.longitude, firstFootprint.latitude]
  }
  return [120.699361, 27.994267]
}

const renderMarkers = () => {
  if (!map.value || !AMapObj.value) return

  map.value.clearMap()

  if (footprints.value.length === 0) {
    map.value.setCenter(getDefaultCenter())
    map.value.setZoom(5)
    return
  }

  const sortedFootprints = [...footprints.value].sort((a, b) => {
    if (!a.visit_date) return 1
    if (!b.visit_date) return -1
    return new Date(a.visit_date) - new Date(b.visit_date)
  })

  sortedFootprints.forEach(item => {
    const marker = new AMapObj.value.Marker({
      position: [item.longitude, item.latitude],
      title: item.location_name,
      map: map.value
    })

    const infoContent = `
  <div style="padding:5px; min-width: 200px;">
    <h4 style="margin:0 0 5px 0;">📍 ${item.location_name}</h4>
    <p style="margin:5px 0; font-size:13px; color:#666;">
      📅 打卡日期: ${item.visit_date}
    </p>
  </div>
`

    const infoWindow = new AMapObj.value.InfoWindow({
      content: infoContent,
      offset: new AMapObj.value.Pixel(0, -30)
    })

    marker.on('click', () => {
      infoWindow.open(map.value, marker.getPosition())
    })
  })

  map.value.setFitView()
}

onMounted(async () => {
  await fetchFootprints()
  initMap()
})

onUnmounted(() => {
  if (map.value) {
    map.value.destroy()
  }
})
</script>

<style scoped>
.footprints-container {
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.control-panel {
  flex-shrink: 0; /* 防止卡片被压缩 */
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.map-container {
  flex-grow: 1; /* 填满剩余空间 */
  width: 100%;
  min-height: 500px; /* 最小高度 */
  border-radius: 8px;
  border: 1px solid #dcdfe6;
  overflow: hidden;
}

@media (max-width: 768px) {
  .header {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
