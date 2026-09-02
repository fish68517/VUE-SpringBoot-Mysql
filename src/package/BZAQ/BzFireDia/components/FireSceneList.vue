<template>
  <div class="fire-scene-modal">
    <div class="modal-header">
      <div class="header-title">智管场景</div>
      <button class="close-btn" @click="handleClose">×</button>
    </div>

    <div class="filter-bar">
      <div class="filter-item">
        <span class="filter-label">街道</span>
        <n-select v-model:value="selectedStreet" class="filter-select" :options="streetOptions" :theme-overrides="selectThemeOverrides" size="small" />
      </div>
      <div class="filter-item">
        <span class="filter-label">警情类型</span>
        <n-select v-model:value="selectedType" class="filter-select" :options="typeOptions" :theme-overrides="selectThemeOverrides" size="small" />
      </div>
      <div class="filter-item">
        <span class="filter-label">案件状态</span>
        <n-select v-model:value="selectedStatus" class="filter-select" :options="statusOptions" :theme-overrides="selectThemeOverrides" size="small" />
      </div>
      <div class="filter-item filter-item--range">
        <span class="filter-label">时间范围</span>
        <n-date-picker
          v-model:value="timeRange"
          class="filter-date-range"
          type="datetimerange"
          :start-placeholder="dateRangeStartPlaceholder"
          :end-placeholder="dateRangeEndPlaceholder"
          :theme-overrides="datePickerThemeOverrides"
          size="small"
          clearable
        />
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
        :bordered="false"
        :single-line="false"
        size="small"
        :scroll-x="tableScrollX"
        :max-height="tableMaxHeight"
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
import { ref, computed, h, watch } from 'vue'
import { NPagination, NDataTable, NSelect, NDatePicker, NButton } from 'naive-ui'
import axios from 'axios'
import { BASE_URL } from '../config'

const emit = defineEmits(['close'])
const props = defineProps({
  type: {
    type: Number,
    default: 1
  }
})

const currentPage = ref(1)
const totalPage = ref(0)
const total = ref(0)
const pageSize = 10
const tableScrollX = 1800
const tableMaxHeight = 420
const allData = ref([])
const selectedStreet = ref('')
const selectedType = ref(props.type)
const selectedStatus = ref('')
const timeRange = ref(null)
const dateRangeStartPlaceholder = '开始时间'
const dateRangeEndPlaceholder = '结束时间'

// 警情类型映射
const typeMap = {
  1: '火灾扑救',
  2: '重大火情',
  3: '火灾舆情',
  4: '抢险救援',
  5: '社会救助'
}

const streetOptions = [
  { label: '全部', value: '' },
  { label: '七星岗街道', value: '七星岗街道' },
  { label: '解放碑街道', value: '解放碑街道' },
  { label: '两路口街道', value: '两路口街道' },
  { label: '上清寺街道', value: '上清寺街道' },
  { label: '菜园坝街道', value: '菜园坝街道' },
  { label: '南纪门街道', value: '南纪门街道' },
  { label: '朝天门街道', value: '朝天门街道' },
  { label: '大溪沟街道', value: '大溪沟街道' },
  { label: '大坪街道', value: '大坪街道' },
  { label: '化龙桥街道', value: '化龙桥街道' },
  { label: '石油路街道', value: '石油路街道' }
]

const typeOptions = Object.entries(typeMap).map(([value, label]) => ({
  label,
  value: Number(value)
}))

const statusOptions = [
  { label: '全部', value: '' },
  { label: '归档', value: '归档' },
  { label: '归队', value: '归队' },
  { label: '出动', value: '出动' },
  { label: '结案', value: '结案' }
]
const controlTheme = {
  heightSmall: '28px',
  fontSizeSmall: '12px',
  borderRadius: '2px',
  color: 'rgba(16, 64, 126, 0.42)',
  colorActive: 'rgba(16, 64, 126, 0.42)',
  colorFocus: 'rgba(16, 64, 126, 0.42)',
  textColor: '#d7e9ff',
  placeholderColor: 'rgba(184, 217, 255, 0.55)',
  border: '1px solid rgba(83, 174, 255, 0.32)',
  borderHover: '1px solid rgba(83, 174, 255, 0.32)',
  borderActive: '1px solid rgba(83, 174, 255, 0.32)',
  borderFocus: '1px solid rgba(83, 174, 255, 0.32)',
  boxShadowActive: 'none',
  boxShadowFocus: 'none'
}

const selectThemeOverrides = {
  peers: {
    InternalSelection: {
      ...controlTheme,
      arrowColor: 'rgba(184, 217, 255, 0.55)'
    }
  }
}

const datePickerThemeOverrides = {
  peers: {
    Input: {
      ...controlTheme,
      iconColor: 'rgba(184, 217, 255, 0.55)',
      suffixTextColor: 'rgba(184, 217, 255, 0.55)',
      paddingSmall: '0 8px'
    }
  }
}

const pad = value => String(value).padStart(2, '0')

const formatDateTime = value => {
  if (!value) return undefined
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return undefined

  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`
}

const getTodayRange = () => {
  const start = new Date()
  start.setHours(0, 0, 0, 0)
  const end = new Date(start)
  end.setDate(end.getDate() + 1)

  return [start.getTime(), end.getTime()]
}

const resetFilters = (keepType = true) => {
  const [todayStart, todayEnd] = getTodayRange()
  selectedStreet.value = ''
  selectedStatus.value = ''
  timeRange.value = [todayStart, todayEnd]
  if (!keepType) {
    selectedType.value = props.type
  }
}

const columns = [
  {
    title: '序号',
    key: 'index',
    width: 46,
    render(row, rowIndex) {
      return h('span', {}, rowIndex + 1)
    }
  },
  { title: '乡镇街道', key: 'town', width: 100 },
  { title: '警情类型', key: 'type', width: 80 },
  { title: '案发地址', key: 'address', width: 300 },
  { title: '案件状态', key: 'status', width: 74 },
  { title: '立案时间', key: 'registerTime', width: 140 },
  { title: '出动时间', key: 'dispatchTime', width: 140 },
  { title: '结案时间', key: 'closeTime', width: 140 }
]
// 鑾峰彇鐏儏鍒楄〃鏁版嵁
const getFireList = async () => {
  try {
    const res = await axios.post(`${BASE_URL}/list/fires`, {
      pageNumber: currentPage.value,
      pageSize: pageSize,
      jqType: selectedType.value,
      street: selectedStreet.value || undefined,
      jqzt: selectedStatus.value || undefined,
      beginTime: formatDateTime(timeRange.value?.[0]),
      endTime: formatDateTime(timeRange.value?.[1])
    })
    if (res.data.code === '000000') {
      const pageData = res.data.data
      totalPage.value = pageData.pages || 0
      total.value = pageData.total || pageData.records?.length || 0
      allData.value = pageData.records.map(item => ({
        id: item.id,
        town: item.street || '未知街道',
        type: typeMap[selectedType.value],
        address: item.ddmc || '未知地址',
        status: item.jqzt || '未知状态',
        registerTime: item.lasj || '-',
        dispatchTime: item.cdsj || '-',
        closeTime: item.jasj || '-'
      }))
    } else {
      console.error('获取火情列表失败：' + res.data.message)
    }
  } catch (err) {
    console.error('请求火情列表接口失败', err)
  }
}

const tableData = computed(() => {
  return allData.value
})

function handleClose() {
  emit('close')
}

function handlePageChange(page) {
  currentPage.value = page
  getFireList()
}

function handleSearch() {
  currentPage.value = 1
  getFireList()
}

function handleReset() {
  resetFilters(true)
  currentPage.value = 1
  getFireList()
}

watch(() => props.type, () => {
  selectedType.value = props.type
  resetFilters(true)
}, { immediate: true })

watch([selectedStreet, selectedType, selectedStatus, timeRange], () => {
  currentPage.value = 1
  getFireList()
}, { deep: true, immediate: true })
</script>

<script>
export default {
  name: "FireSceneList"
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

.fire-scene-modal {
  font-family: 'AlibabaPuHuiTi', sans-serif;
  position: absolute;
  transform: translate(-50%, -50%);
  z-index: 1001;
  width: 1356px;
  height: 630px;
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

.filter-bar {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 32px 0;
  box-sizing: border-box;
}

.filter-item {
  display: flex;
  align-items: center;
  gap: 6px;
  min-width: 0;
}

.filter-item--time {
  display: none;
}

.filter-item--range {
  flex-shrink: 0;

  &::before {
    content: '时间范围';
    flex-shrink: 0;
    color: #8BBCE6;
    font-size: 13px;
    white-space: nowrap;
  }

  .filter-label {
    display: none;
  }
}

.filter-label {
  flex-shrink: 0;
  color: #8BBCE6;
  font-size: 13px;
  white-space: nowrap;
}

.filter-select {
  width: 130px;
}

.filter-date-range {
  width: 360px;
}

.filter-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-total {
  margin-left: auto;
  color: #8BBCE6;
  font-size: 13px;
  white-space: nowrap;
}

.filter-select :deep(.n-base-selection),
.filter-date-range :deep(.n-input) {
  min-height: 28px !important;
  height: 28px !important;
  background: rgba(16, 64, 126, 0.42) !important;
  border: 1px solid rgba(83, 174, 255, 0.32) !important;
  border-radius: 2px !important;
  box-sizing: border-box;
}

.filter-select :deep(.n-base-selection-label),
.filter-date-range :deep(.n-input-wrapper) {
  min-height: 26px !important;
  height: 26px !important;
}

.filter-select :deep(.n-base-selection-label),
.filter-select :deep(.n-base-selection-input),
.filter-select :deep(.n-base-selection-placeholder),
.filter-date-range :deep(.n-input__input),
.filter-date-range :deep(.n-input__input-el) {
  height: 26px !important;
  line-height: 26px !important;
  color: #d7e9ff !important;
  font-size: 12px !important;
}

.filter-select :deep(.n-base-selection-placeholder),
.filter-date-range :deep(.n-input__placeholder),
.filter-date-range :deep(.n-date-picker-icon),
.filter-date-range :deep(.n-input__suffix) {
  color: rgba(184, 217, 255, 0.55) !important;
}

.filter-select :deep(.n-base-selection__border),
.filter-select :deep(.n-base-selection__state-border),
.filter-date-range :deep(.n-input__border),
.filter-date-range :deep(.n-input__state-border) {
  border: none !important;
  box-shadow: none !important;
}

.filter-date-range :deep(.n-date-picker) {
  width: 100%;
}

.filter-date-range :deep(.n-date-picker-separator) {
  color: rgba(184, 217, 255, 0.65) !important;
  line-height: 26px !important;
}

.filter-date-range :deep(.n-input__suffix) {
  margin-left: 4px;
}

.filter-actions :deep(.n-button) {
  min-width: 56px;
  border-radius: 2px;
}

.modal-content {
  flex: 1;
  min-height: 0;
  padding: 12px 32px 0 32px;
  overflow: hidden;
}

// NDataTable 鏍峰紡瑕嗙洊
:deep(.fire-table) {
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
    padding: 0 4px;
    color: #8BBCE6;
    font-weight: 500;
    background: #0B437C;
    border: none;
  }

  .n-data-table-td {
    height: 40px;
    max-height: 40px;
    overflow: hidden;
    padding: 6px 4px;
    color: #d7e9ff;
    font-family: 'D-DIN', 'AlibabaPuHuiTi', sans-serif;
    background: #0A325F;
    border: none;
  }

  .n-data-table-tr:nth-child(even) .n-data-table-td {
    background: #19406B;
  }

  .n-data-table-tr:hover .n-data-table-td {
    background: rgba(29, 107, 202, 0.3);
  }

  .n-data-table-th:first-child,
  .n-data-table-th:last-child,
  .n-data-table-td:first-child,
  .n-data-table-td:last-child {
    border: none;
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

  :deep(.n-pagination-item) {
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

  :deep(.n-pagination-item:hover) {
    color: #ffffff;
    border-color: rgba(83, 174, 255, 0.6);
    background: rgba(24, 92, 179, 0.6);
  }

  :deep(.n-pagination-item--active) {
    background: rgba(37, 134, 255, 0.8) !important;
    border-color: rgba(37, 134, 255, 0.9) !important;
    color: #ffffff !important;
  }

  :deep(.n-pagination-item--disabled) {
    opacity: 0.4;
    cursor: not-allowed;
    pointer-events: none;
  }

  :deep(.n-pagination-quick__arrow) {
    color: #aed5ff;
  }

  :deep(.n-pagination-button) {
    min-width: 32px;
    height: 32px;
    border: 1px solid rgba(83, 174, 255, 0.35);
    background: rgba(16, 64, 126, 0.4);
    color: #aed5ff;
    border-radius: 2px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
  }

  :deep(.n-pagination-button:hover) {
    color: #ffffff;
    border-color: rgba(83, 174, 255, 0.6);
    background: rgba(24, 92, 179, 0.6);
  }

  :deep(.n-pagination-button[disabled]) {
    opacity: 0.4;
    cursor: not-allowed;
  }
}
</style>
