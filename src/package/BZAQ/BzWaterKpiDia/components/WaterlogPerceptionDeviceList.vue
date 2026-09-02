<template>
  <div class="waterlog-list-modal">
    <div class="modal-header">
      <div class="header-title">感知设备列表</div>
      <button class="close-btn" @click="handleClose">×</button>
    </div>

    <div class="query-bar">
      <n-input
        v-model:value="searchName"
        class="query-input"
        clearable
        placeholder="请输入感知设备名称"
        @keyup.enter="handleSearch"
      />
      <n-select
        v-model:value="selectedType"
        class="query-select"
        :options="deviceTypeOptions"
        :bordered="false"
        @update:value="handleTypeChange"
      />
      <n-button class="query-btn" size="small" @click="handleSearch">查询</n-button>
      <n-button class="query-btn" size="small" @click="handleReset">重置</n-button>
    </div>

    <div class="modal-content">
      <n-data-table
        class="waterlog-table"
        :columns="columns"
        :data="tableData"
        :bordered="false"
        :single-line="false"
        :row-props="getRowProps"
        size="small"
      />
    </div>

    <div class="modal-footer">
      <n-pagination
        :page="currentPage"
        :page-count="totalPage"
        :page-slot="5"
        @update:page="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { h, onMounted, ref } from 'vue'
import { NButton, NDataTable, NInput, NPagination, NSelect } from 'naive-ui'
import axios from 'axios'

const emit = defineEmits(['close', 'detail'])

const DEVICE_URL = 'http://23.210.227.34:23343/yzqzlzx/api/boot/system/roadDevice/queryAllRoadDevice'
const ALL_STATION_URL = 'http://23.210.227.34:23343/yzqzlzx/api/boot/system/common/getAllHydrologyStationInfo'
const currentPage = ref(1)
const totalPage = ref(1)
const pageSize = 10
const tableData = ref([])
const allRecords = ref([])
const searchName = ref('')
const selectedType = ref('device-camera')

const deviceTypeOptions = [
  { label: '摄像头', value: 'device-camera' },
  { label: '液位计', value: 'device-gauge' },
  { label: '水文站', value: 'device-other' }
]

const deviceTypeLabelMap = {
  'device-camera': '摄像头',
  'device-gauge': '液位计',
  'device-other': '水文站'
}

const deviceColumns = [
  {
    title: '序号',
    key: 'index',
    width: 80,
    render(_row, rowIndex) {
      return h('span', {}, (currentPage.value - 1) * pageSize + rowIndex + 1)
    }
  },
  { title: '感知设备名称', key: 'name', minWidth: 220, ellipsis: true },
  { title: '感知设备类型', key: 'type', width: 180, ellipsis: true },
  {
    title: '状态',
    key: 'status',
    width: 100,
    render(row) {
      return h(
        'span',
        { class: 'status-cell' },
        [
          h('span', {
            class: ['status-dot', row.status === '在线' ? 'online' : 'offline']
          }),
          row.status
        ]
      )
    }
  }
]

const stationColumns = [
  {
    title: '序号',
    key: 'index',
    width: 80,
    render(_row, rowIndex) {
      return h('span', {}, (currentPage.value - 1) * pageSize + rowIndex + 1)
    }
  },
  { title: '水文站名称', key: 'name', minWidth: 220, ellipsis: true },
  { title: '经度', key: 'lon', width: 160, ellipsis: true },
  { title: '纬度', key: 'lat', width: 160, ellipsis: true }
]

const columns = ref(deviceColumns)

const getResponseRecords = data => {
  const result = data?.data ?? data
  const list = Array.isArray(result)
    ? result
    : result?.records || result?.list || result?.rows || []
  return Array.isArray(list) ? list : []
}

const getDeviceName = item => {
  return String(item.name || item.deviceName || item.dvname || item.deviceNum || item.deviceNo || item.dvcode || item.address || '-')
}

const getRawDeviceType = item => {
  return String(item.deviceTypeName || item.deviceType || item.typeName || item.type || item.categoryName || item.category || '')
}

const getDeviceTypeKey = item => {
  const type = getRawDeviceType(item)
  if (type.includes('摄像') || type.toLowerCase().includes('camera')) return 'device-camera'
  if (type.includes('液位') || type.includes('水位') || type.toLowerCase().includes('gauge')) return 'device-gauge'
  return 'device-other'
}

const getDeviceType = item => {
  return deviceTypeLabelMap[getDeviceTypeKey(item)] || getRawDeviceType(item) || '-'
}

const getDeviceStatus = item => {
  const rawStatus = item.state ?? item.status ?? item.deviceStatus ?? item.onlineStatus ?? item.networkStatus ?? item.statusName ?? item.statusDesc
  const status = String(rawStatus ?? '').toLowerCase()
  if (status.includes('在线') || status.includes('online') || status === '1' || status === 'true') return '在线'
  if (status.includes('离线') || status.includes('offline') || status === '0' || status === 'false') return '离线'
  return '离线'
}

const updateTableData = () => {
  if (selectedType.value === 'device-other') {
    const filtered = allRecords.value.filter(item => {
      const name = String(item.stnm || item.name || '')
      const matchesName = !searchName.value || name.includes(searchName.value)
      return matchesName
    })
    totalPage.value = Math.max(1, Math.ceil(filtered.length / pageSize))
    const start = (currentPage.value - 1) * pageSize
    tableData.value = filtered.slice(start, start + pageSize).map((item, index) => ({
      key: item.id || item.stnm || index,
      name: String(item.stnm || item.name || '-'),
      lon: String(item.lon ?? item.lonWgs84 ?? item.longitude ?? item.lng ?? '-'),
      lat: String(item.lat ?? item.latWgs84 ?? item.latitude ?? '-'),
      raw: item
    }))
    return
  }
  const filtered = allRecords.value.filter(item => {
    const matchesType = getDeviceTypeKey(item) === selectedType.value
    const matchesName = !searchName.value || getDeviceName(item).includes(searchName.value)
    return matchesType && matchesName
  })
  totalPage.value = Math.max(1, Math.ceil(filtered.length / pageSize))
  const start = (currentPage.value - 1) * pageSize
  tableData.value = filtered.slice(start, start + pageSize).map((item, index) => ({
    key: item.id || item.deviceCode || item.deviceNum || item.deviceNo || item.dvcode || index,
    name: getDeviceName(item),
    type: getDeviceType(item),
    status: getDeviceStatus(item),
    typeKey: getDeviceTypeKey(item),
    raw: item
  }))
}

const getDeviceList = async () => {
  try {
    const res = await axios.post(DEVICE_URL)
    allRecords.value = getResponseRecords(res?.data)
    updateTableData()
  } catch (error) {
    console.error('获取感知设备列表失败:', error)
    allRecords.value = []
    tableData.value = []
    totalPage.value = 1
  }
}

const getStationList = async () => {
  try {
    const res = await axios.post(ALL_STATION_URL)
    allRecords.value = res.data?.data?.stations || []
    if (!Array.isArray(allRecords.value)) allRecords.value = []
    updateTableData()
  } catch (error) {
    console.error('获取水文站列表失败:', error)
    allRecords.value = []
    tableData.value = []
    totalPage.value = 1
  }
}

function handleClose() {
  emit('close')
}

function openDeviceDetail(row) {
  if (row.typeKey === 'device-camera') {
    emit('detail', 'waterlog-road-risk-detail')
  }
  if (row.typeKey === 'device-gauge') {
    emit('detail', 'waterlog-liquid-level-detail')
  }
}

function getRowProps(row) {
  const clickable = row.typeKey === 'device-camera' || row.typeKey === 'device-gauge'
  return {
    style: clickable ? 'cursor: pointer;' : '',
    onClick: () => {
      if (clickable) openDeviceDetail(row)
    }
  }
}

function handleSearch() {
  currentPage.value = 1
  updateTableData()
}

function handleReset() {
  searchName.value = ''
  selectedType.value = 'device-camera'
  currentPage.value = 1
  columns.value = deviceColumns
  getDeviceList()
}

function handleTypeChange() {
  currentPage.value = 1
  columns.value = selectedType.value === 'device-other' ? stationColumns : deviceColumns
  if (selectedType.value === 'device-other') {
    getStationList()
  } else {
    getDeviceList()
  }
}

function handlePageChange(page) {
  currentPage.value = page
  updateTableData()
}

onMounted(() => {
  getDeviceList()
})
</script>

<script>
export default {
  name: 'WaterlogPerceptionDeviceList'
}
</script>

<style lang="scss" scoped>
@import './waterlog-list-modal.scss';
</style>
