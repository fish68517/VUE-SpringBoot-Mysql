<template>
  <div class="fire-risk-point-modal">
    <div class="modal-header">
      <div class="header-title">{{ title }}</div>
      <button class="close-btn" @click="handleClose">×</button>
    </div>
    <div class="filter-bar">
      <div class="filter-item">
        <span class="filter-label">灾险情名称</span>
        <n-input v-model:value="keyword" class="filter-keyword" placeholder="请输入灾险情名称" clearable size="small" @keyup.enter="handleSearch" />
      </div>
      <div class="filter-item filter-item--range">
        <span class="filter-label">时间范围</span>
        <n-date-picker v-model:value="timeRange" class="filter-date-range" type="datetimerange" start-placeholder="开始时间" end-placeholder="结束时间" size="small" clearable />
      </div>
      <div class="filter-actions">
        <n-button size="small" type="info" @click="handleSearch">查询</n-button>
        <n-button size="small" @click="handleReset">重置</n-button>
      </div>
      <div class="filter-total">共 {{ total }} 条</div>
    </div>
    <div class="modal-content">
      <n-data-table
        class="fire-table"
        :columns="columns"
        :data="tableData"
        :scroll-x="tableScrollX"
        flex-height
        :bordered="false"
        :single-line="false"
        size="small"
      />
    </div>

    <div class="modal-footer">
      <n-pagination
        :page="currentPage"
        :item-count="total"
        :page-size="pageSize"
        :page-slot="5"
        @update:page="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { computed, h, onMounted, ref, watch } from 'vue'
import { NButton, NDataTable, NDatePicker, NInput, NPagination } from 'naive-ui'
import axios from 'axios'
import { BASE_URL } from '../config'

const props = defineProps({
  type: {
    type: [String, Number],
    default: undefined
  }
})

const emit = defineEmits(['close'])

const currentPage = ref(1)
const total = ref(0)
const pageSize = 10
const tableData = ref([])
const keyword = ref('')
const timeRange = ref(null)

/** 将时间戳格式化为接口使用的日期时间字符串。 */
const formatDateTime = value => {
  if (!value) return undefined
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return undefined
  const pad = number => String(number).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`
}

const title = computed(() => {
  if (Number(props.type) === 0) return '灾情列表'
  if (Number(props.type) === 1) return '险情列表'
  return '灾险情列表'
})

const columns = [
  {
    title: '序号',
    key: 'index',
    width: 80,
    render(_row, rowIndex) {
      return h('span', {}, (currentPage.value - 1) * pageSize + rowIndex + 1)
    }
  },
  { title: '灾险情名称', key: 'address', width: 360 },
  { title: '发生事件', key: 'hazardname', width: 360 },
  { title: '灾险情等级', key: 'pkiddLevel', width: 160 },
  { title: '发生时间', key: 'happentime', width: 220 }
]

// 表格固定横向滚动宽度，列多时通过横向滚动完整展示。
const tableScrollX = 1800

const getQueryParams = () => {
  const params = {
    Index: currentPage.value,
    size: pageSize,
    keyword: keyword.value || undefined,
    beginTime: formatDateTime(timeRange.value?.[0]),
    endTime: formatDateTime(timeRange.value?.[1])
  }

  if (props.type !== undefined && props.type !== null && props.type !== '') {
    params.type = props.type
  }

  return params
}

const getResponsePage = data => {
  const pageData = data?.data || {}
  const list = pageData.content || pageData.records || []
  const total = Number(pageData.page?.total ?? pageData.total ?? list.length)

  return {
    records: Array.isArray(list) ? list : [],
    pages: Math.max(1, Math.ceil(total / pageSize)),
    total
  }
}

const requestDisasterDangerList = async () => {
  const api = '/api/boot/system/land/landDisasterDangerListPage'
  const res = await axios.get(`${BASE_URL}${api}`, {
    params: getQueryParams()
  })
  return getResponsePage(res?.data)
}

const getDisasterDangerList = async () => {
  try {
    const pageData = await requestDisasterDangerList()
    total.value = pageData.total || 0
    tableData.value = pageData.records
  } catch (error) {
    console.error('获取灾险情列表失败:', error)
    tableData.value = []
    total.value = 0
    total.value = 0
  }
}

function handleClose() {
  emit('close')
}

function handlePageChange(page) {
  currentPage.value = page
  getDisasterDangerList()
}

/** 按关键字和时间范围重新查询第一页。 */
function handleSearch() {
  currentPage.value = 1
  getDisasterDangerList()
}

/** 清空查询条件并重新加载列表。 */
function handleReset() {
  keyword.value = ''
  timeRange.value = null
  currentPage.value = 1
  getDisasterDangerList()
}

watch(
  () => props.type,
  () => {
    currentPage.value = 1
    getDisasterDangerList()
  }
)

onMounted(() => {
  getDisasterDangerList()
})
</script>

<script>
export default {
  name: 'GeoDisasterDangerList'
}
</script>

<style lang="scss" scoped>
@import './geo-filter-bar.scss';
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

.fire-risk-point-modal {
  font-family: 'AlibabaPuHuiTi', sans-serif;
  position: absolute;
  transform: translate(-50%, -50%);
  z-index: 1001;
  width: 1200px;
  height: 620px;
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

.modal-content {
  flex: 1;
  min-height: 0;
  display: flex;
  padding: 12px 32px 8px;
  overflow: hidden;
}

:deep(.fire-table) {
  flex: 1;
  height: 100%;
  min-height: 0;

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
