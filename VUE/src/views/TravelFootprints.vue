<template>
  <div class="footprints-container">
    <el-card class="control-panel" shadow="hover">
      <div class="header">
        <div class="title-group">
          <h2>我的旅行足迹</h2>
          <div class="stat-tags">
            <el-tag type="success">全部：{{ footprints.length }} 个</el-tag>
            <el-tag type="info">预置：{{ presetFootprintCount }} 个</el-tag>
            <el-tag type="warning">自主添加：{{ userFootprintCount }} 个</el-tag>
          </div>
        </div>

        <div class="header-actions">
          <el-button :loading="loading" @click="fetchFootprints">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
          <el-button type="primary" @click="handleOpenAddDialog">
            <el-icon><Plus /></el-icon>
            添加足迹
          </el-button>
        </div>
      </div>
    </el-card>

    <div id="container" v-loading="loading" class="map-container"></div>

    <el-dialog
      v-model="addDialogVisible"
      title="添加足迹"
      width="560px"
      destroy-on-close
      @closed="resetAddForm"
    >
      <el-alert
        v-if="travelRecords.length === 0 && !recordsLoading"
        title="请先创建一条旅行记录，再添加足迹点"
        type="warning"
        :closable="false"
        class="record-alert"
      />

      <el-form
        ref="addFormRef"
        :model="addForm"
        :rules="addRules"
        label-position="top"
        class="add-form"
      >
        <el-form-item label="关联旅行记录" prop="travelRecordId">
          <el-select
            v-model="addForm.travelRecordId"
            placeholder="请选择旅行记录"
            filterable
            :loading="recordsLoading"
            style="width: 100%"
          >
            <el-option
              v-for="record in travelRecords"
              :key="record.id"
              :label="formatRecordOption(record)"
              :value="record.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="旅行地址" prop="address">
          <el-input
            v-model="addForm.address"
            placeholder="例如：苏州平江路、北京故宫、上海外滩"
            maxlength="200"
            show-word-limit
            clearable
            @input="clearResolvedAddress"
            @keyup.enter="handleResolveAddress"
          >
            <template #append>
              <el-button :loading="geocoding" @click="handleResolveAddress">识别地址</el-button>
            </template>
          </el-input>
        </el-form-item>

        <el-alert
          v-if="resolvedLocation"
          :title="`已识别：${resolvedLocation.name}`"
          description="系统已自动获取定位信息，保存时会写入数据库"
          type="success"
          :closable="false"
          show-icon
          class="geocode-alert"
        />

        <el-form-item label="打卡日期" prop="visitDate">
          <el-date-picker
            v-model="addForm.visitDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择日期"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          :loading="submitting"
          :disabled="travelRecords.length === 0"
          @click="handleSubmitFootprint"
        >
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, reactive, ref, shallowRef } from 'vue'
import AMapLoader from '@amap/amap-jsapi-loader'
import { ElMessage } from 'element-plus'
import { Plus, Refresh } from '@element-plus/icons-vue'
import { useUserStore } from '../stores/userStore'
import { footprintService } from '../services/footprintService'
import { travelService } from '../services/travelService'
import { getPresetFootprintsByUserId } from '../mock/presetFootprints'

const userStore = useUserStore()
const map = shallowRef(null)
const AMapObj = shallowRef(null)
const footprints = ref([])
const travelRecords = ref([])
const loading = ref(false)
const recordsLoading = ref(false)
const submitting = ref(false)
const geocoding = ref(false)
const addDialogVisible = ref(false)
const addFormRef = ref(null)
let amapLoadPromise = null

const addForm = reactive({
  travelRecordId: null,
  address: '',
  visitDate: ''
})
const resolvedLocation = ref(null)

const currentUserId = computed(() => userStore.user?.id ?? null)
const presetFootprintCount = computed(() => footprints.value.filter(item => item.source === 'preset').length)
const userFootprintCount = computed(() => footprints.value.filter(item => item.source === 'custom').length)

const addRules = {
  travelRecordId: [{ required: true, message: '请选择旅行记录', trigger: 'change' }],
  address: [{ required: true, message: '请输入旅行地址', trigger: 'blur' }]
}

const fetchFootprints = async () => {
  if (!currentUserId.value) {
    footprints.value = []
    ElMessage.warning('当前未获取到登录用户 ID')
    renderMarkers()
    return
  }

  loading.value = true
  const presetFootprints = getPresetFootprintsByUserId(currentUserId.value)
    .map(item => normalizeFootprint(item, 'preset'))

  try {
    const response = await footprintService.getCurrentUserFootprints()
    const userFootprints = Array.isArray(response?.data)
      ? response.data.map(item => normalizeFootprint(item, 'custom'))
      : []

    footprints.value = [...presetFootprints, ...userFootprints]

    if (footprints.value.length === 0) {
      ElMessage.info('当前用户暂无足迹点')
    }
  } catch (error) {
    footprints.value = presetFootprints
    ElMessage.error(error.message || '加载自主添加足迹失败')
  } finally {
    loading.value = false
    renderMarkers()
  }
}

const loadTravelRecords = async () => {
  recordsLoading.value = true
  try {
    const response = await travelService.getUserTravelRecords(0, 100)
    travelRecords.value = response?.data?.content || []
    if (!addForm.travelRecordId && travelRecords.value.length === 1) {
      addForm.travelRecordId = travelRecords.value[0].id
    }
  } catch (error) {
    travelRecords.value = []
    ElMessage.error(error.message || '加载旅行记录失败')
  } finally {
    recordsLoading.value = false
  }
}

const handleOpenAddDialog = async () => {
  if (!currentUserId.value) {
    ElMessage.warning('请先登录')
    return
  }

  addDialogVisible.value = true
  await loadTravelRecords()
}

const handleSubmitFootprint = async () => {
  if (!addFormRef.value) return

  try {
    await addFormRef.value.validate()
    submitting.value = true
    const location = await resolveAddressCoordinates()
    if (!location) {
      return
    }

    await footprintService.addFootprint({
      travelRecordId: addForm.travelRecordId,
      locationName: location.name,
      latitude: location.latitude,
      longitude: location.longitude,
      visitDate: addForm.visitDate || null
    })

    ElMessage.success('足迹添加成功')
    addDialogVisible.value = false
    await fetchFootprints()
  } catch (error) {
    ElMessage.error(error.message || '添加足迹失败')
  } finally {
    submitting.value = false
  }
}

const resetAddForm = () => {
  addForm.travelRecordId = null
  addForm.address = ''
  addForm.visitDate = ''
  resolvedLocation.value = null
  addFormRef.value?.clearValidate()
}

const clearResolvedAddress = () => {
  resolvedLocation.value = null
}

const handleResolveAddress = async () => {
  if (!addForm.address.trim()) {
    ElMessage.warning('请输入旅行地址')
    return
  }

  await resolveAddressCoordinates(true)
}

const resolveAddressCoordinates = async (showSuccessMessage = false) => {
  const address = addForm.address.trim()
  if (!address) {
    ElMessage.warning('请输入旅行地址')
    return null
  }

  if (resolvedLocation.value?.query === address) {
    return resolvedLocation.value
  }

  try {
    geocoding.value = true
    const AMap = await loadAmapApi()
    const geocoder = new AMap.Geocoder()
    const result = await geocodeAddress(geocoder, address)

    if (!result) {
      ElMessage.warning('未识别到经纬度，请重新输入常见的旅行地址，例如城市名、景区名或具体地标')
      return null
    }

    resolvedLocation.value = result
    if (showSuccessMessage) {
      ElMessage.success('地址识别成功')
    }
    return result
  } catch (error) {
    console.error('地址识别失败:', error)
    ElMessage.error('地址识别失败，请稍后重试或输入更常见的旅行地址')
    return null
  } finally {
    geocoding.value = false
  }
}

const geocodeAddress = (geocoder, address) => new Promise((resolve) => {
  geocoder.getLocation(address, (status, result) => {
    const firstGeocode = result?.geocodes?.[0]
    const location = firstGeocode?.location
    const latitude = Number(location?.lat)
    const longitude = Number(location?.lng)

    if (status !== 'complete' || !firstGeocode || Number.isNaN(latitude) || Number.isNaN(longitude)) {
      resolve(null)
      return
    }

    resolve({
      query: address,
      name: firstGeocode.formattedAddress || firstGeocode.formatted_address || address,
      latitude,
      longitude
    })
  })
})

const initMap = () => {
  window._AMapSecurityConfig = {
    securityJsCode: '76805393edb2f03827a55eafa36fc6d2'
  }

  loadAmapApi().then((AMap) => {
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
    ElMessage.error('地图加载失败')
  })
}

const loadAmapApi = () => {
  window._AMapSecurityConfig = {
    securityJsCode: '76805393edb2f03827a55eafa36fc6d2'
  }

  if (!amapLoadPromise) {
    amapLoadPromise = AMapLoader.load({
      key: 'e2706bc1e334def5699349076d5f6d58',
      version: '2.0',
      plugins: ['AMap.ToolBar', 'AMap.Scale', 'AMap.Marker', 'AMap.InfoWindow', 'AMap.Geocoder']
    })
  }

  return amapLoadPromise
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
    if (!a.visitDate) return 1
    if (!b.visitDate) return -1
    return new Date(a.visitDate) - new Date(b.visitDate)
  })

  sortedFootprints.forEach(item => {
    const marker = new AMapObj.value.Marker({
      position: [item.longitude, item.latitude],
      title: item.locationName,
      map: map.value
    })

    const infoWindow = new AMapObj.value.InfoWindow({
      content: buildInfoWindowContent(item),
      offset: new AMapObj.value.Pixel(0, -30)
    })

    marker.on('click', () => {
      infoWindow.open(map.value, marker.getPosition())
    })
  })

  map.value.setFitView()
}

const normalizeFootprint = (item, source) => ({
  id: `${source}-${item.id}`,
  rawId: item.id,
  travelRecordId: item.travelRecordId ?? item.travel_record_id,
  locationName: item.locationName ?? item.location_name,
  latitude: Number(item.latitude),
  longitude: Number(item.longitude),
  visitDate: item.visitDate ?? item.visit_date,
  createdAt: item.createdAt ?? item.created_at,
  source
})

const buildInfoWindowContent = (item) => {
  const sourceText = item.source === 'preset' ? '预置足迹' : '自主添加'
  return `
    <div style="padding:5px; min-width: 200px;">
      <h4 style="margin:0 0 8px 0;">${escapeHtml(item.locationName)}</h4>
      <p style="margin:5px 0; font-size:13px; color:#666;">打卡日期：${escapeHtml(formatVisitDate(item.visitDate))}</p>
      <p style="margin:5px 0; font-size:13px; color:#666;">类型：${sourceText}</p>
    </div>
  `
}

const formatRecordOption = (record) => {
  const destination = record.destination ? ` - ${record.destination}` : ''
  return `${record.title}${destination}`
}

const formatVisitDate = (value) => value || '未填写'

const escapeHtml = (value) => String(value ?? '')
  .replace(/&/g, '&amp;')
  .replace(/</g, '&lt;')
  .replace(/>/g, '&gt;')
  .replace(/"/g, '&quot;')
  .replace(/'/g, '&#39;')

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
  flex-shrink: 0;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.title-group {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 14px;
}

.title-group h2 {
  margin: 0;
  color: #303133;
  font-size: 22px;
}

.stat-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.map-container {
  flex-grow: 1;
  width: 100%;
  min-height: 500px;
  border-radius: 8px;
  border: 1px solid #dcdfe6;
  overflow: hidden;
}

.record-alert {
  margin-bottom: 16px;
}

.geocode-alert {
  margin-bottom: 18px;
}

.add-form {
  margin-top: 4px;
}

.coordinate-row {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.coordinate-item {
  margin-bottom: 18px;
}

@media (max-width: 768px) {
  .header,
  .header-actions {
    flex-direction: column;
    align-items: stretch;
  }

  .title-group {
    align-items: flex-start;
  }

  .coordinate-row {
    grid-template-columns: 1fr;
    gap: 0;
  }
}
</style>
