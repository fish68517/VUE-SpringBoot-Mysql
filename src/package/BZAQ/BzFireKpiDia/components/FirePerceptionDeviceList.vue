<template>
  <div class="fire-device-modal">
    <div class="modal-header">
      <div class="header-title">感知方式列表</div>
      <button class="close-btn" @click="handleClose">×</button>
    </div>

    <div class="query-bar">
      <n-input
        v-model:value="searchName"
        class="query-input"
        clearable
        placeholder="请输入感知方式名称"
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
        class="fire-table"
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
import { BASE_URL } from '../config'

const emit = defineEmits(['close', 'open-detail'])

const currentPage = ref(1)
const totalPage = ref(1)
const pageSize = 10
const tableData = ref([])
const searchName = ref('')
const selectedType = ref('device-water-pressure')

const deviceApiMap = {
  'device-water-pressure': '/list/location/water/detail',
  'device-life-channel': '/list/location/truck/detail',
  'device-fire-control': '/list/location/room/detail',
  'device-gas': '/list/location/gas/detail'
}

const deviceTypeOptions = [
  { label: '水压监测', value: 'device-water-pressure' },
  { label: '生命通道', value: 'device-life-channel' },
  { label: '消防控制室', value: 'device-fire-control' },
  { label: '燃气设备', value: 'device-gas' }
]

const deviceTypeLabelMap = {
  'device-water-pressure': '水压监测',
  'device-life-channel': '生命通道',
  'device-fire-control': '消防控制室',
  'device-gas': '燃气设备'
}

const columns = [
  {
    title: '序号',
    key: 'index',
    width: 80,
    render(_row, rowIndex) {
      return h('span', {}, (currentPage.value - 1) * pageSize + rowIndex + 1)
    }
  },
  { title: '感知方式名称', key: 'name', minWidth: 220, ellipsis: true },
  { title: '感知方式类型', key: 'type', width: 180, ellipsis: true }
]

const getResponsePage = data => {
  const result = data?.data ?? data
  const list = Array.isArray(result)
    ? result
    : result?.records || result?.list || result?.rows || []

  return {
    records: Array.isArray(list) ? list : [],
    pages: Number(result?.pages) || 1
  }
}

const getDeviceName = item => {
  return item.name || item.deviceName || item.dvname || item.deviceNum || item.deviceNo || item.dvcode || item.address || '-'
}

const getDeviceType = item => {
  return item.deviceTypeName || item.deviceType || item.typeName || item.type || item.categoryName || item.category || deviceTypeLabelMap[selectedType.value] || '-'
}

const getDeviceVideoUrl = item => {
  return item?.videoUrl || item?.streamUrl || item?.playUrl || item?.flv || item?.hls || item?.rtsp || item?.rtmp || ''
}

const getQueryParams = () => ({
  pageNumber: currentPage.value,
  pageSize,
  name: searchName.value || undefined
})

const getDeviceList = async () => {
  try {
    const api = deviceApiMap[selectedType.value] || deviceApiMap['device-water-pressure']
    const res = await axios.post(`${BASE_URL}${api}`, getQueryParams())
    const pageData = getResponsePage(res?.data)
    totalPage.value = pageData.pages || 1
    tableData.value = pageData.records.map((item, index) => ({
      key: item.id || item.deviceCode || item.deviceNum || item.deviceNo || item.dvcode || index,
      name: getDeviceName(item),
      type: getDeviceType(item),
      layerType: selectedType.value,
      raw: item
    }))
  } catch (error) {
    console.error('获取感知方式列表失败:', error)
    tableData.value = []
    totalPage.value = 1
  }
}

const getDetailPayload = row => {
  const item = row?.raw || row || {}

  if (row?.layerType === 'device-water-pressure') {
    return {
      diaName: 'fire-water-pressure-list',
      name: item.name || item.deviceName || row?.name || '',
      type: item.type || item.deviceType || '',
      buildName: item.buildName || ''
    }
  }

  if (row?.layerType === 'device-life-channel') {
    return {
      diaName: 'fire-life-channel-list',
      name: item.name || item.deviceName || row?.name || '',
      type: item.type || item.deviceType || '',
      deviceNum: item.deviceNum || item.deviceCode || '',
      videoUrl: getDeviceVideoUrl(item)
    }
  }

  if (row?.layerType === 'device-fire-control') {
    return {
      diaName: 'fire-control-room-list',
      name: item.name || item.deviceName || row?.name || '',
      type: item.type || item.deviceType || '',
      deviceNum: item.deviceNum || item.deviceCode || '',
      videoUrl: getDeviceVideoUrl(item)
    }
  }

  if (row?.layerType === 'device-gas') {
    return {
      diaName: 'fire-gas-device-list',
      name: item.deviceName || item.name || row?.name || '',
      type: item.deviceType || item.type || ''
    }
  }

  return null
}

const getRowProps = row => ({
  class: 'fire-table-row--clickable',
  onClick: () => {
    const payload = getDetailPayload(row)
    if (payload) emit('open-detail', payload)
  }
})

function handleClose() {
  emit('close')
}

function handleSearch() {
  currentPage.value = 1
  getDeviceList()
}

function handleReset() {
  searchName.value = ''
  selectedType.value = 'device-water-pressure'
  currentPage.value = 1
  getDeviceList()
}

function handleTypeChange() {
  currentPage.value = 1
  getDeviceList()
}

function handlePageChange(page) {
  currentPage.value = page
  getDeviceList()
}

onMounted(() => {
  getDeviceList()
})
</script>

<script>
export default {
  name: 'FirePerceptionDeviceList'
}
</script>

<style lang="scss" scoped>
@font-face {
  font-family: 'AlibabaPuHuiTi';
  src: url('../font/Alibaba_PuHuiTi_2.0_55_Regular_55_Regular.ttf') format('truetype');
  font-weight: normal;
  font-style: normal;
}

@font-face {
  font-family: 'D-DIN';
  src: url('../font/D-DIN.otf') format('opentype');
  font-weight: normal;
  font-style: normal;
}

.fire-device-modal {
  font-family: 'AlibabaPuHuiTi', sans-serif;
  position: absolute;
  transform: translate(-50%, -50%);
  z-index: 1001;
  width: 900px;
  height: 600px;
  display: flex;
  flex-direction: column;
  background:
    url('../img/Group_2136640490.png') top center / 100% 48px no-repeat,
    linear-gradient(180deg, rgba(3, 19, 54, 0.95) 0%, rgba(2, 12, 36, 0.95) 100%);
  border: 1px solid rgba(37, 134, 255, 0.25);
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.45);
  color: #b8d9ff;
  font-size: 14px;
  overflow: hidden;
  pointer-events: auto;

  &::before {
    content: '';
    position: absolute;
    inset: 0;
    background: url('../img/Rectangle_346242153.png') center / cover no-repeat;
    opacity: 0.25;
    z-index: -1;
  }
}

.modal-header {
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  flex-shrink: 0;
  padding: 0 32px;

  .header-title {
    color: #cfe8ff;
    font-size: 22px;
    font-weight: 700;
    text-shadow: 0 0 12px rgba(83, 174, 255, 0.5);
  }

  .close-btn {
    position: absolute;
    right: 20px;
    top: 50%;
    transform: translateY(-50%);
    width: 32px;
    height: 32px;
    border: 1px solid rgba(83, 174, 255, 0.3);
    background: rgba(16, 64, 126, 0.4);
    color: #aed5ff;
    font-size: 20px;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;

    &:hover {
      color: #ffffff;
      border-color: rgba(83, 174, 255, 0.6);
      background: rgba(24, 92, 179, 0.6);
    }
  }
}

.query-bar {
  height: 56px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 32px 0;
}

.query-input {
  width: 240px;
  background: #0d356b;
}

.query-select {
  width: 180px;
  background: #0d356b;
}

.query-select :deep(.n-base-selection),
.query-select :deep(.n-base-selection-label) {
  background: #0d356b;
}

.query-input,
.query-select {
  --query-control-bg: rgba(16, 64, 126, 0.45);
  --query-control-border: rgba(83, 174, 255, 0.35);
  --query-control-border-hover: rgba(83, 174, 255, 0.6);
}

.query-btn {
  min-width: 64px;
  border: 1px solid rgba(83, 174, 255, 0.35);
  background: rgba(16, 64, 126, 0.45);
  color: #aed5ff;
}

.modal-content {
  flex: 1;
  min-height: 0;
  padding: 16px 32px 0 32px;
  overflow-y: auto;
  scrollbar-width: none;
  -ms-overflow-style: none;
  &::-webkit-scrollbar {
    display: none;
  }
}

:deep(.fire-table) {
  height: 100%;

  .n-data-table-wrapper {
    background: transparent;
  }

  .n-data-table-table {
    background: transparent;
    font-size: 14px;
  }

  .n-data-table-th {
    height: 40px;
    padding: 0 12px;
    color: #8bbce6;
    font-weight: 500;
    background: #0b437c;
    border: none;
  }

  .n-data-table-td {
    height: 40px;
    padding: 0 12px;
    color: #d7e9ff;
    font-family: 'D-DIN', 'AlibabaPuHuiTi', sans-serif;
    background: #0a325f;
    border: none;
  }

  .n-data-table-tr:nth-child(even) .n-data-table-td {
    background: #19406b;
  }

  .n-data-table-tr:hover .n-data-table-td {
    background: rgba(29, 107, 202, 0.3);
  }

  .fire-table-row--clickable {
    cursor: pointer;
  }
}

.modal-footer {
  height: 60px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 32px;

  :deep(.n-pagination) {
    display: flex;
    align-items: center;
    gap: 6px;
  }

  :deep(.n-pagination-item),
  :deep(.n-pagination-button) {
    min-width: 32px;
    height: 32px;
    border: 1px solid rgba(83, 174, 255, 0.35);
    background: rgba(16, 64, 126, 0.4);
    color: #aed5ff;
    font-size: 14px;
    border-radius: 2px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
  }

  :deep(.n-pagination-item:hover),
  :deep(.n-pagination-button:hover) {
    color: #ffffff;
    border-color: rgba(83, 174, 255, 0.6);
    background: rgba(24, 92, 179, 0.6);
  }

  :deep(.n-pagination-item--active) {
    background: rgba(37, 134, 255, 0.8) !important;
    border-color: rgba(37, 134, 255, 0.9) !important;
    color: #ffffff !important;
  }
}
</style>
