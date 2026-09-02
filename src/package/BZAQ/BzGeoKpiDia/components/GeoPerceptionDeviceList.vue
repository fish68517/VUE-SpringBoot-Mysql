<template>
  <div class="geo-list-modal">
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
        class="geo-table"
        :columns="columns"
        :data="tableData"
        :scroll-x="scrollX"
        :max-height="470"
        :bordered="false"
        :single-line="false"
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
import { computed, h, onMounted, ref } from 'vue'
import { NButton, NDataTable, NInput, NPagination, NSelect } from 'naive-ui'
import axios from 'axios'

const emit = defineEmits(['close'])

const LAND_BASE_URL = 'http://23.99.16.179:11001/api/boot/system/land'
const currentPage = ref(1)
const totalPage = ref(1)
const pageSize = 10
const tableData = ref([])
const searchName = ref('')
const selectedType = ref('device-deformation')

const deviceTypeNameMap = {
  'device-deformation': '形变',
  'device-displacement': '位移',
  'device-rainfall': '雨量计',
  'device-camera': '裂缝监测仪'
}

const deviceTypeLabelMap = {
  'device-deformation': '形变',
  'device-displacement': '位移',
  'device-rainfall': '雨量计',
  'device-camera': '裂缝检测仪',
  'device-grid-member': '四重网格员'
}

const deviceTypeOptions = [
  { label: '形变', value: 'device-deformation' },
  { label: '位移', value: 'device-displacement' },
  { label: '雨量计', value: 'device-rainfall' },
  { label: '裂缝检测仪', value: 'device-camera' },
  { label: '四重网格员', value: 'device-grid-member' }
]

const indexColumn = {
  title: '序号',
  key: 'index',
  width: 80,
  render(_row, rowIndex) {
    return h('span', {}, (currentPage.value - 1) * pageSize + rowIndex + 1)
  }
}

const deviceColumns = [
  indexColumn,
  { title: '设备编号', key: 'devId', width: 180, ellipsis: { tooltip: true } },
  { title: '设备名称', key: 'devName', width: 220, ellipsis: { tooltip: true } },
  { title: '设备类型', key: 'devTypeName', width: 140, ellipsis: { tooltip: true } },
  { title: '隐患点名称', key: 'disasterName', width: 220, ellipsis: { tooltip: true } },
  { title: '隐患点编码', key: 'unifiedcode', width: 180, ellipsis: { tooltip: true } },
  { title: '区县编码', key: 'cityCode', width: 140, ellipsis: { tooltip: true } },
  { title: '乡镇编码', key: 'townCode', width: 140, ellipsis: { tooltip: true } }
]

const gridMemberColumns = [
  indexColumn,
  { title: '区县编码', key: 'city', width: 160, ellipsis: { tooltip: true } },
  { title: '全名/姓名', key: 'fullName', width: 180, ellipsis: { tooltip: true } },
  { title: '联系电话', key: 'telephone', width: 180, ellipsis: { tooltip: true } },
  {
    title: '类型',
    key: 'type',
    width: 180,
    ellipsis: { tooltip: true },
    render(row) {
      return getGridMemberTypeName(row.type)
    }
  }
]

const columns = computed(() => selectedType.value === 'device-grid-member' ? gridMemberColumns : deviceColumns)
const scrollX = computed(() => selectedType.value === 'device-grid-member' ? 780 : 1360)

const gridMemberTypeMap = {
  1: '地环站人员',
  2: '片区负责人',
  3: '驻守地质队员',
  4: '群测群防员'
}

const getGridMemberTypeName = value => gridMemberTypeMap[Number(value)] || value || '--'

const getResponseRecords = data => {
  const result = data?.data ?? data
  const list = Array.isArray(result)
    ? result
    : result?.content || result?.records || result?.list || result?.rows || []
  return Array.isArray(list) ? list : []
}

const getResponsePage = data => {
  const result = data?.data ?? data
  const total = Number(result?.page?.total ?? result?.total)
  return {
    records: getResponseRecords(data),
    pages: Number(result?.pages) || (total ? Math.ceil(total / pageSize) : 1)
  }
}

const getDeviceQueryParams = () => ({
  deviceType: deviceTypeNameMap[selectedType.value] || '',
  index: currentPage.value,
  size: pageSize
})

const getGridMemberQueryParams = () => ({
  index: currentPage.value,
  size: pageSize
})

const fetchByType = async type => {
  const res = type === 'device-grid-member'
    ? await axios.get(`${LAND_BASE_URL}/landGridPersonnelPage`, { params: getGridMemberQueryParams() })
    : await axios.post(`${LAND_BASE_URL}/landDevicePointPage`, getDeviceQueryParams())
  return getResponsePage(res?.data)
}

const toTableRows = records => {
  return records.map((item, index) => {
    const type = item.__deviceType || selectedType.value
    return {
      ...item,
      key: item.id || item.devId || item.deviceId || item.telephone || item.fullName || `${type}_${index}`,
      devId: item.devId || item.deviceId || '--',
      devName: item.devName || item.deviceName || '--',
      devTypeName: item.devTypeName || item.deviceType || deviceTypeLabelMap[selectedType.value] || '--',
      disasterName: item.disasterName || '--',
      unifiedcode: item.unifiedcode || item.unifiedCode || '--',
      cityCode: item.cityCode || item.city || '--',
      townCode: item.townCode || item.town || '--',
      city: item.city || '--',
      fullName: item.fullName || '--',
      telephone: item.telephone || '--',
      type: item.type ?? '--'
    }
  })
}

const getDeviceList = async () => {
  try {
    const pageData = await fetchByType(selectedType.value)
    totalPage.value = pageData.pages || 1
    tableData.value = toTableRows(pageData.records)
  } catch (error) {
    console.error('获取感知方式列表失败:', error)
    tableData.value = []
    totalPage.value = 1
  }
}

function handleClose() {
  emit('close')
}

function handleSearch() {
  currentPage.value = 1
  getDeviceList()
}

function handleReset() {
  searchName.value = ''
  selectedType.value = 'device-deformation'
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
  name: 'GeoPerceptionDeviceList'
}
</script>

<style lang="scss" scoped>
@import './geo-list-modal.scss';
</style>
