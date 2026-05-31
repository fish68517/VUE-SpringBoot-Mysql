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

        <el-form-item label="地点名称" prop="locationName">
          <el-input
            v-model="addForm.locationName"
            placeholder="例如：苏州平江路"
            maxlength="200"
            show-word-limit
            clearable
          />
        </el-form-item>

        <div class="coordinate-row">
          <el-form-item label="纬度" prop="latitude" class="coordinate-item">
            <el-input-number
              v-model="addForm.latitude"
              :min="-90"
              :max="90"
              :precision="6"
              :step="0.000001"
              controls-position="right"
              style="width: 100%"
            />
          </el-form-item>

          <el-form-item label="经度" prop="longitude" class="coordinate-item">
            <el-input-number
              v-model="addForm.longitude"
              :min="-180"
              :max="180"
              :precision="6"
              :step="0.000001"
              controls-position="right"
              style="width: 100%"
            />
          </el-form-item>
        </div>

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
const addDialogVisible = ref(false)
const addFormRef = ref(null)

const addForm = reactive({
  travelRecordId: null,
  locationName: '',
  latitude: null,
  longitude: null,
  visitDate: ''
})

const currentUserId = computed(() => userStore.user?.id ?? null)
const presetFootprintCount = computed(() => footprints.value.filter(item => item.source === 'preset').length)
const userFootprintCount = computed(() => footprints.value.filter(item => item.source === 'custom').length)

const validateLatitude = (_rule, value, callback) => {
  validateCoordinate(value, -90, 90, '纬度', callback)
}

const validateLongitude = (_rule, value, callback) => {
  validateCoordinate(value, -180, 180, '经度', callback)
}

const addRules = {
  travelRecordId: [{ required: true, message: '请选择旅行记录', trigger: 'change' }],
  locationName: [{ required: true, message: '请输入地点名称', trigger: 'blur' }],
  latitude: [{ validator: validateLatitude, trigger: 'change' }],
  longitude: [{ validator: validateLongitude, trigger: 'change' }]
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

    await footprintService.addFootprint({
      travelRecordId: addForm.travelRecordId,
      locationName: addForm.locationName.trim(),
      latitude: Number(addForm.latitude),
      longitude: Number(addForm.longitude),
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
  addForm.locationName = ''
  addForm.latitude = null
  addForm.longitude = null
  addForm.visitDate = ''
  addFormRef.value?.clearValidate()
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
    ElMessage.error('地图加载失败')
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

const validateCoordinate = (value, min, max, label, callback) => {
  if (value === null || value === undefined || value === '') {
    callback(new Error(`请输入${label}`))
    return
  }

  const numberValue = Number(value)
  if (Number.isNaN(numberValue) || numberValue < min || numberValue > max) {
    callback(new Error(`${label}范围为 ${min} 到 ${max}`))
    return
  }

  callback()
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
