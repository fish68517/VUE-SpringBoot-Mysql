<template>
  <div class="station-map-page">
    <section class="page-heading-panel compact-heading">
      <div>
        <p class="eyebrow">STATION MAP</p>
        <h1>充电站地图</h1>
        <p>通过区域、关键字、接口类型和运营状态筛选站点，地图与列表保持联动。</p>
      </div>
      <div class="heading-stat"><strong>{{ total }}</strong><span>个匹配站点</span></div>
    </section>

    <section class="map-workspace">
      <aside class="station-filter-panel">
        <div class="filter-title">
          <div><span>查询条件</span><small>最多显示 100 个站点</small></div>
          <el-button text type="primary" @click="resetFilters">重置</el-button>
        </div>

        <el-form :model="filters" label-position="top" @submit.prevent="loadStations">
          <el-form-item label="站点名称或地址">
            <el-input v-model="filters.keyword" placeholder="输入关键字" clearable @keyup.enter="loadStations" />
          </el-form-item>
          <el-form-item label="所属区域">
            <el-cascader
              v-model="filters.regionPath"
              :options="regionOptions"
              :props="cascaderProps"
              placeholder="全部区域"
              clearable
              style="width: 100%"
            />
          </el-form-item>
          <div class="filter-row">
            <el-form-item label="接口类型">
              <el-select v-model="filters.connectorType" placeholder="全部" clearable>
                <el-option label="直流快充" :value="0" />
                <el-option label="交流慢充" :value="1" />
              </el-select>
            </el-form-item>
            <el-form-item label="运营状态">
              <el-select v-model="filters.status" placeholder="全部" clearable>
                <el-option label="运营中" :value="1" />
                <el-option label="维护中" :value="2" />
                <el-option label="已停用" :value="0" />
              </el-select>
            </el-form-item>
          </div>
          <el-button type="primary" class="filter-submit" :loading="loading" @click="loadStations">查询站点</el-button>
        </el-form>

        <div class="station-list-heading">
          <strong>查询结果</strong>
          <span>{{ stations.length }} 条</span>
        </div>
        <div v-loading="loading" class="station-result-list">
          <el-empty v-if="!loading && stations.length === 0" description="没有找到匹配站点" :image-size="78" />
          <article
            v-for="station in stations"
            :key="station.id"
            :class="['station-result-card', { active: selectedStation?.id === station.id }]"
            @click="focusStation(station)"
          >
            <div class="station-result-head">
              <el-tag :type="station.status === 1 ? 'success' : station.status === 2 ? 'warning' : 'info'" size="small">
                {{ stationStatusText(station.status) }}
              </el-tag>
              <span>{{ station.regionName }}</span>
            </div>
            <h3>{{ station.stationName }}</h3>
            <p>{{ station.address }}</p>
            <div class="station-result-metrics">
              <span><strong>{{ station.freeCount }}</strong> 空闲</span>
              <span><strong>{{ station.usingCount }}</strong> 使用中</span>
              <span><strong>{{ station.pileCount }}</strong> 总桩</span>
            </div>
          </article>
        </div>
      </aside>

      <div class="map-stage">
        <div ref="mapContainer" class="amap-container" v-loading="mapLoading"></div>
        <el-alert
          v-if="mapError"
          class="map-error"
          :title="mapError"
          type="error"
          :closable="false"
          show-icon
        />
        <div v-if="selectedStation" class="selected-station-card">
          <button type="button" aria-label="关闭站点摘要" @click="selectedStation = null">×</button>
          <span>{{ selectedStation.regionName }} · {{ stationStatusText(selectedStation.status) }}</span>
          <h3>{{ selectedStation.stationName }}</h3>
          <p>{{ selectedStation.address }}</p>
          <div>
            <strong>{{ selectedStation.freeCount }}</strong><small> 空闲</small>
            <router-link :to="`/stations/${selectedStation.id}`">查看详情 →</router-link>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, reactive, ref, shallowRef } from 'vue'
import { ElMessage } from 'element-plus'
import { getRegionTree, getStations } from '../../api/station'
import { loadAmap } from '../../utils/amapLoader'

const mapContainer = ref()
const map = shallowRef()
const AMapObject = shallowRef()
const infoWindow = shallowRef()
const markers = new Map()
const stations = ref([])
const regionOptions = ref([])
const selectedStation = ref(null)
const loading = ref(false)
const mapLoading = ref(true)
const mapError = ref('')
const total = ref(0)

const filters = reactive({
  keyword: '',
  regionPath: [],
  connectorType: null,
  status: null
})

const cascaderProps = {
  value: 'id',
  label: 'regionName',
  children: 'children',
  checkStrictly: true,
  emitPath: true
}

const selectedRegionId = computed(() => {
  const path = filters.regionPath
  return Array.isArray(path) && path.length > 0 ? path[path.length - 1] : null
})

const loadRegions = async () => {
  regionOptions.value = await getRegionTree()
}

const initMap = async () => {
  mapLoading.value = true
  mapError.value = ''
  try {
    const AMap = await loadAmap()
    AMapObject.value = AMap
    map.value = new AMap.Map(mapContainer.value, {
      viewMode: '3D',
      zoom: 11,
      center: [120.699361, 27.994267],
      mapStyle: 'amap://styles/normal'
    })
    map.value.add(new AMap.ToolBar({ position: 'RT' }))
    map.value.add(new AMap.Scale())
    infoWindow.value = new AMap.InfoWindow({ offset: new AMap.Pixel(0, -30) })
    renderMarkers()
  } catch (error) {
    console.error('高德地图加载失败:', error)
    mapError.value = `高德地图加载失败：${error.message || '请检查网络和地图配置'}`
  } finally {
    mapLoading.value = false
  }
}

const loadStations = async () => {
  loading.value = true
  try {
    const result = await getStations({
      pageNum: 1,
      pageSize: 100,
      keyword: filters.keyword.trim() || undefined,
      regionId: selectedRegionId.value || undefined,
      connectorType: filters.connectorType ?? undefined,
      status: filters.status ?? undefined
    })
    stations.value = result.records || []
    total.value = result.total || 0
    selectedStation.value = null
    renderMarkers()
  } catch (error) {
    stations.value = []
    total.value = 0
    ElMessage.error(error.message || '站点数据加载失败')
  } finally {
    loading.value = false
  }
}

const renderMarkers = () => {
  if (!map.value || !AMapObject.value) return

  markers.clear()
  map.value.clearMap()
  if (stations.value.length === 0) {
    map.value.setZoomAndCenter(11, [120.699361, 27.994267])
    return
  }

  stations.value.forEach(station => {
    const marker = new AMapObject.value.Marker({
      position: [Number(station.longitude), Number(station.latitude)],
      title: station.stationName,
      label: {
        content: `<div class="amap-marker-label-inner">${escapeHtml(station.stationName)}</div>`,
        direction: 'top'
      }
    })
    marker.on('click', () => focusStation(station, marker))
    marker.setMap(map.value)
    markers.set(station.id, marker)
  })
  map.value.setFitView([...markers.values()], false, [70, 70, 70, 70], 13)
}

const focusStation = (station, marker = markers.get(station.id)) => {
  if (!map.value || !marker) return
  selectedStation.value = station
  map.value.setZoomAndCenter(15, [Number(station.longitude), Number(station.latitude)])
  infoWindow.value?.setContent(buildInfoWindow(station))
  infoWindow.value?.open(map.value, marker.getPosition())
}

const buildInfoWindow = station => `
  <div class="amap-info-card">
    <strong>${escapeHtml(station.stationName)}</strong>
    <p>${escapeHtml(station.address)}</p>
    <div><span>${station.freeCount} 个空闲</span><a href="/stations/${station.id}">查看详情</a></div>
  </div>
`

const resetFilters = () => {
  filters.keyword = ''
  filters.regionPath = []
  filters.connectorType = null
  filters.status = null
  loadStations()
}

const stationStatusText = status => ({ 0: '已停用', 1: '运营中', 2: '维护中' }[status] || '未知')

const escapeHtml = value => String(value ?? '')
  .replace(/&/g, '&amp;')
  .replace(/</g, '&lt;')
  .replace(/>/g, '&gt;')
  .replace(/"/g, '&quot;')
  .replace(/'/g, '&#39;')

onMounted(async () => {
  try {
    await Promise.all([loadRegions(), initMap()])
    await loadStations()
  } catch (error) {
    ElMessage.error(error.message || '页面初始化失败')
  }
})

onUnmounted(() => {
  infoWindow.value?.close()
  map.value?.destroy()
  markers.clear()
})
</script>
