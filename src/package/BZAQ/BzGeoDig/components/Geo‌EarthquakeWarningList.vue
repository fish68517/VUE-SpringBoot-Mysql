<template>
  <div class="fire-risk-point-modal">
    <div class="modal-header">
      <div class="header-title">{{ title }}</div>
      <button class="close-btn" @click="handleClose">×</button>
    </div>
    <!-- <div class="query-bar">
      <n-input
        v-model:value="searchName"
        class="query-input"
        clearable
        placeholder="请输入地灾预警名称"
        @keyup.enter="handleSearch"
      />
      <n-button class="query-btn" size="small" @click="handleSearch">查询</n-button>
      <n-button class="query-btn" size="small" @click="handleReset">重置</n-button>
    </div> -->
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
        :page-count="totalPage"
        :page-slot="5"
        @update:page="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { h, ref, watch ,onMounted} from 'vue'
import { NButton, NDataTable, NInput, NPagination, NSelect } from 'naive-ui'
import axios from 'axios'
import { BASE_URL } from '../config'

const emit = defineEmits(['close'])

const currentPage = ref(1)
const totalPage = ref(1)
const pageSize = 10
const tableData = ref([])
const searchName = ref('')

const warnTypeMap = {
  0: '实时',
  1: '长期'
}

const statisticTypeMap = {
  0: '按处统计',
  1: '按点统计'
}

const warnLevelMap = {
  1: { label: '蓝色预警', className: 'warn-level--blue' },
  2: { label: '黄色预警', className: 'warn-level--yellow' },
  3: { label: '橙色预警', className: 'warn-level--orange' },
  4: { label: '红色预警', className: 'warn-level--red' }
}

const getMappedValue = (map, value) => map[Number(value)] || value || '--'

const title ='地灾预警'
const columns = [
  {
    title: '序号',
    key: 'index',
    width: 80,
    render(_row, rowIndex) {
      return h('span', {}, (currentPage.value - 1) * pageSize + rowIndex + 1)
    }
  },
  { title: '隐患点名称', key: 'disasterName', width: 260, ellipsis: { tooltip: true } },
  { title: '隐患点编码', key: 'unifiedcode', width: 220, ellipsis: { tooltip: true } },
  { title: '项目名称', key: 'projectName', width: 260, ellipsis: { tooltip: true } },
  {
    title: '预警类型',
    key: 'isLong',
    width: 120,
    ellipsis: { tooltip: true },
    render(row) {
      return getMappedValue(warnTypeMap, row.isLong)
    }
  },
  {
    title: '统计类型',
    key: 'isSpot',
    width: 120,
    ellipsis: { tooltip: true },
    render(row) {
      return getMappedValue(statisticTypeMap, row.isSpot)
    }
  },
  {
    title: '预警等级',
    key: 'warnLevel',
    width: 110,
    render(row) {
      const warnLevel = warnLevelMap[Number(row.warnLevel)]
      if (!warnLevel) return row.warnLevel || '--'
      return h('span', { class: ['warn-level', warnLevel.className] }, warnLevel.label)
    }
  },
  { title: '创建时间', key: 'createdTs', width: 220 },
  { title: '最后修改时间', key: 'lastModifiedTs', width: 220 },
  { title: '预警描述', key: 'msg', width: 360, ellipsis: { tooltip: true } },
  { title: '区县编码', key: 'city', width: 160, ellipsis: { tooltip: true } },
  { title: '乡镇编码', key: 'town', width: 160, ellipsis: { tooltip: true } },
]

// 表格固定横向滚动宽度，列多时通过横向滚动完整展示。
const tableScrollX = 2400

const getQueryParams = () => ({
  Index: currentPage.value,
  size:pageSize,
  // name: searchName.value || undefined
})

const requestRiskList = async () => {
  let api = "/api/boot/system/land/landWarnAlertListPage"
  const res = await axios.get(`${BASE_URL}${api}`, { 
    params: getQueryParams() 
  })
  return getResponsePage(res?.data)
}

const getResponsePage = data => {
  const list = data.data.content
  return {
    records: Array.isArray(list) ? list : [],
    pages: Math.ceil(Number(data.data.page.total) / pageSize) 
  }
}

const getRiskPointList = async () => {
  try {
    const pageData = await requestRiskList()
    totalPage.value = pageData.pages || 1
    tableData.value = pageData.records
  } catch (error) {
    console.error('获取地灾预警列表失败:', error)
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
  currentPage.value = 1
  getRiskPointList()
}

function handlePageChange(page) {
  currentPage.value = page
  getRiskPointList()
}

onMounted(()=>{
  getRiskPointList()
})

</script>

<script>
export default {
  name: 'Geo‌EarthquakeWarningList'
}
</script>

<style lang="scss" scoped>
@import './geo-table-scroll.scss';
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


.query-btn {
  min-width: 64px;
  border: 1px solid rgba(83, 174, 255, 0.35);
  background: rgba(16, 64, 126, 0.45);
  color: #aed5ff;
}

.modal-content {
  flex: 1;
  min-height: 0;
  display: flex;
  padding: 12px 32px 18px;
  overflow: hidden;
}

:deep(.fire-table) {
  flex: 1;
  height: 100%;
  min-height: 0;

  .n-data-table-wrapper {
    background: transparent;
  }

  .n-data-table-base-table,
  .n-data-table-base-table-body {
    min-width: 0;
    min-height: 0;
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

:deep(.warn-level) {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 72px;
  height: 24px;
  box-sizing: border-box;
  padding: 0 8px;
  border: 1px solid currentColor;
  border-radius: 2px;
  font-family: 'AlibabaPuHuiTi', sans-serif;
  font-size: 13px;
  font-weight: 500;
}

:deep(.warn-level--blue) {
  color: #35a8ff;
  background: rgba(53, 168, 255, 0.14);
}

:deep(.warn-level--yellow) {
  color: #ffd84d;
  background: rgba(255, 216, 77, 0.14);
}

:deep(.warn-level--orange) {
  color: #ff9f43;
  background: rgba(255, 159, 67, 0.14);
}

:deep(.warn-level--red) {
  color: #ff5b6b;
  background: rgba(255, 91, 107, 0.14);
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
