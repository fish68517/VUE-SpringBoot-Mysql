<template>
  <div class="waterlog-list-modal">
    <div class="modal-header">
      <div class="header-title">风险点位列表</div>
      <button class="close-btn" @click="handleClose">×</button>
    </div>

    <div class="query-bar">
      <n-input
        v-model:value="searchName"
        class="query-input"
        clearable
        placeholder="请输入风险点名称"
        @keyup.enter="handleSearch"
      />
      <n-select
        v-model:value="selectedType"
        class="query-select"
        :options="riskTypeOptions"
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
import axios from 'axios'
import { NButton, NDataTable, NInput, NPagination, NSelect } from 'naive-ui'
const emit = defineEmits(['close'])

const WATERLOG_RISK_API = 'http://23.99.16.179:11001/api/boot/system/waterlog/risk/point'

const currentPage = ref(1)
const totalPage = ref(1)
const pageSize = 10
const tableData = ref([])
const searchName = ref('')
const selectedType = ref('risk-waterlog')

// 所有从接口获取的数据（未过滤）
const allRiskPoints = ref([])

const riskLabelMap = {
  'risk-flood': '洪灾风险',
  'risk-waterlog': '内涝风险'
}

const riskTypeOptions = [
  { label: '洪灾风险', value: 'risk-flood' },
  { label: '内涝风险', value: 'risk-waterlog' }
]

// 洪灾风险无静态数据
const fallbackFloodRows = []

const columns = [
  {
    title: '序号',
    key: 'index',
    width: 80,
    render(_row, rowIndex) {
      return h('span', {}, (currentPage.value - 1) * pageSize + rowIndex + 1)
    }
  },
  { title: '风险点名称', key: 'name', width: 220 },
  { title: '风险点类型', key: 'type', width: 180, ellipsis: true }
]

const getRiskName = item => {
  return String(item.name || item.riskName || item.deviceName || item.dvname || item.placeName || item.address || item.mountAddress || '-')
}

/** 获取内涝风险点位数据 */
const fetchWaterlogRiskPoints = async () => {
  try {
    const res = await axios.get(WATERLOG_RISK_API)
    const list = res?.data?.data
    if (Array.isArray(list)) {
      allRiskPoints.value = list
    } else {
      allRiskPoints.value = []
    }
  } catch (error) {
    console.error('获取内涝风险点位失败:', error)
    allRiskPoints.value = []
  }
}

/** 获取当前类型的原始数据 */
const getRawRecords = () => {
  if (selectedType.value === 'risk-waterlog') {
    return allRiskPoints.value
  }
  // 洪灾风险使用兜底数据
  return fallbackFloodRows
}

/** 过滤 + 分页 */
const getFilteredRecords = () => {
  const records = getRawRecords()
  const filtered = searchName.value
    ? records.filter(item => getRiskName(item).includes(searchName.value))
    : records
  const start = (currentPage.value - 1) * pageSize
  return {
    records: filtered.slice(start, start + pageSize),
    total: filtered.length,
    pages: Math.max(1, Math.ceil(filtered.length / pageSize))
  }
}

const toTableRows = records => {
  const typeName = riskLabelMap[selectedType.value] || '内涝风险'
  return records.map((item, index) => {
    return {
      key: item.id || item.deviceCode || item.deviceNum || item.dvcode || `${selectedType.value}_${index}`,
      name: getRiskName(item),
      type: typeName
    }
  })
}

const getRiskPointList = async () => {
  try {
    const pageData = getFilteredRecords()
    totalPage.value = pageData.pages || 1
    tableData.value = toTableRows(pageData.records)
  } catch (error) {
    console.error('获取风险点列表失败:', error)
    tableData.value = []
    totalPage.value = 1
  }
}

function handleClose() {
  emit('close')
}

function handleSearch() {
  currentPage.value = 1
  getRiskPointList()
}

function handleReset() {
  searchName.value = ''
  selectedType.value = 'risk-waterlog'
  currentPage.value = 1
  getRiskPointList()
}

async function handleTypeChange() {
  currentPage.value = 1
  // 切换到内涝风险时重新拉取接口数据
  if (selectedType.value === 'risk-waterlog') {
    await fetchWaterlogRiskPoints()
  }
  getRiskPointList()
}

function handlePageChange(page) {
  currentPage.value = page
  getRiskPointList()
}

onMounted(async () => {
  // 默认内涝风险，先拉取接口数据
  await fetchWaterlogRiskPoints()
  getRiskPointList()
})
</script>

<script>
export default {
  name: 'WaterlogRiskPointList'
}
</script>

<style lang="scss" scoped>
@import './waterlog-list-modal.scss';
</style>
