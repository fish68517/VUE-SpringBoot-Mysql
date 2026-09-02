<template>
  <div class="fire-risk-point-modal">
    <div class="modal-header">
      <div class="header-title">本周巡查点位</div>
      <button class="close-btn" @click="handleClose">×</button>
    </div>
    <div class="filter-bar">
      <div class="filter-item">
        <span class="filter-label">街道</span>
        <n-select
          v-model:value="queryForm.townName"
          class="filter-select"
          :options="streetOptions"
          :theme-overrides="selectThemeOverrides"
          size="small"
        />
      </div>
      <div class="filter-item">
        <span class="filter-label">灾害类型</span>
        <n-select
          v-model:value="queryForm.type"
          class="filter-select"
          :options="disasterTypeOptions"
          :theme-overrides="selectThemeOverrides"
          size="small"
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
        :scroll-x="tableScrollX"
        flex-height
        :bordered="false"
        :single-line="true"
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
import { h, onMounted, ref } from 'vue'
import { NButton, NDataTable, NPagination, NSelect } from 'naive-ui'
import axios from 'axios'
import { BASE_URL } from '../config'

const emit = defineEmits(['close'])

const currentPage = ref(1)
const total = ref(0)
const pageSize = 10
const tableData = ref([])
const queryForm = ref({
  type: '',
  townName: '',
  isValid: ''
})

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

const disasterTypeOptions = [
  { label: '全部', value: '' },
  { label: '崩塌', value: '崩塌' },
  { label: '滑坡', value: '滑坡' },
  { label: '斜坡', value: '斜坡' },
  { label: '泥石流', value: '泥石流' }
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

const emptyText = value => {
  if (value === 0) return '0'
  return value || '-'
}

const getPatrolResultClass = value => {
  const text = String(value ?? '')
  if (text === '正常' || text === '1') return 'patrol-result patrol-result--normal'
  if (text === '异常' || text === '0') return 'patrol-result patrol-result--abnormal'
  return 'patrol-result'
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
  { title: '所在街道', key: 'townName', width: 150, ellipsis: { tooltip: true } },
  { title: '巡查结果', key: 'isNormal', width: 120, render(row) { return h('span', { class: getPatrolResultClass(row.isNormal) }, emptyText(row.isNormal)) } },
  { title: '隐患点编码', key: 'code', width: 180, ellipsis: { tooltip: true } },
  { title: '隐患点名称', key: 'name', width: 220, ellipsis: { tooltip: true } },
  { title: '具体位置', key: 'location', width: 260, ellipsis: { tooltip: true } },
  { title: '灾害类型', key: 'type', width: 120, ellipsis: { tooltip: true } },
  { title: '经度', key: 'longitude', width: 110 },
  { title: '纬度', key: 'latitude', width: 110 },
  { title: '是否巡查', key: 'isValid', width: 120, render(row) { return h('span', {}, emptyText(row.isValid)) } },
  { title: '巡查人', key: 'reportUserName', width: 140, ellipsis: { tooltip: true } },
  { title: '巡查时间', key: 'reportTime', width: 200, ellipsis: { tooltip: true } }
]

// 表格固定横向滚动宽度，列多时通过横向滚动完整展示。
const tableScrollX = 1810

const getQueryBody = () => ({
  index: currentPage.value,
  size: pageSize,
  type: queryForm.value.type || '',
  townName: queryForm.value.townName || '',
  isValid: queryForm.value.isValid || ''
})

const getResponsePage = data => {
  const pageData = data?.data || {}
  const list = pageData.content || pageData.records || []
  const totalValue = Number(pageData.page?.total ?? pageData.total ?? list.length)

  return {
    records: Array.isArray(list) ? list : [],
    total: Number.isFinite(totalValue) ? totalValue : 0
  }
}

/** 查询本周巡查点位分页数据。 */
const requestGridPatrolList = async () => {
  const api = '/api/boot/system/land/landGeoHazardPointPatrolPage'
  const res = await axios.post(`${BASE_URL}${api}`, getQueryBody())
  return getResponsePage(res?.data)
}

/** 加载本周巡查点位列表。 */
const getGridPatrolList = async () => {
  try {
    const pageData = await requestGridPatrolList()
    total.value = pageData.total || 0
    tableData.value = pageData.records
  } catch (error) {
    console.error('获取本周巡查点位列表失败:', error)
    tableData.value = []
    total.value = 0
  }
}

function handleClose() {
  emit('close')
}

function handlePageChange(page) {
  currentPage.value = page
  getGridPatrolList()
}

/** 按筛选条件重新查询第一页。 */
function handleSearch() {
  currentPage.value = 1
  getGridPatrolList()
}

/** 清空查询条件并重新加载列表。 */
function handleReset() {
  queryForm.value = {
    type: '',
    townName: '',
    isValid: ''
  }
  currentPage.value = 1
  getGridPatrolList()
}

onMounted(() => {
  getGridPatrolList()
})
</script>

<script>
export default {
  name: 'GeoGridPatrolPointList'
}
</script>

<style lang="scss" scoped>
@import './geo-filter-bar.scss';
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

.filter-select {
  width: 180px;
}

.filter-select :deep(.n-base-selection) {
  min-height: 28px !important;
  height: 28px !important;
  background: rgba(16, 64, 126, 0.42) !important;
  border: 1px solid rgba(83, 174, 255, 0.32) !important;
  border-radius: 2px !important;
  box-sizing: border-box;
}

.filter-select :deep(.n-base-selection-label) {
  height: 26px !important;
  line-height: 26px !important;
}

.filter-select :deep(.n-base-selection-input),
.filter-select :deep(.n-base-selection-placeholder) {
  color: #d7e9ff !important;
  font-size: 12px !important;
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

  .patrol-result {
    color: #d7e9ff;
    font-weight: 700;
  }

  .patrol-result--normal {
    color: #00e887 !important;
  }

  .patrol-result--abnormal {
    color: #ff4f5c !important;
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
