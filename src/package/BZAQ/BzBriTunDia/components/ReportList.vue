<template>
  <div class="report-list-popup">
    <div class="popup-header">
      <div class="title-bg">
        <h3 class="title">报告列表</h3>
      </div>
      <img class="close-btn" :src="closeIcon" alt="关闭" @click="$emit('close')" />
    </div>

    <div class="popup-body">
      <div class="tab-bar">
        <div
          v-for="(tab, index) in tabList"
          :key="tab"
          class="tab-item"
          :class="{ active: activeTab === index }"
          @click="handleTabChange(index)"
        >
          <span class="tab-text">{{ tab }}</span>
        </div>
      </div>

      <div class="search-bar">
        <div class="search-item">
          <label>设施选择：</label>
          <el-select
            v-model="searchForm.structType"
            class="search-select"
            popper-class="report-select-popper"
            placeholder="请选择"
            clearable
          >
            <el-option v-for="item in facilityOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </div>
        <div class="search-item">
          <label>时间选择：</label>
            <n-date-picker
              v-model:value="searchForm.date"
              class="search-date-picker"
              type="date"
              clearable
              placeholder="请选择日期"
              format="yy年MM月dd日"
            />
        </div>
        <div class="search-item search-btn-item">
          <button class="search-btn" @click="handleSearch">查询</button>
          <button class="search-btn reset-btn" @click="handleReset">重置</button>
        </div>
      </div>

      <div class="table-container">
          <n-data-table
            class="report-table"
            :columns="columns"
            :data="tableData"
            :bordered="false"
            :single-line="false"
            :max-height="350"
            size="small"
          />
      </div>

      <div class="pagination-wrapper">
        <n-pagination :page="pagination.current" :page-count="pagination.totalPage" :page-slot="5" @update:page="handlePageChange" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { h, ref, computed, onMounted, watch } from 'vue'
import { NDataTable, NPagination, NDatePicker, NConfigProvider, zhCN, dateZhCN } from 'naive-ui'
import { ElSelect, ElOption } from 'element-plus'
import 'element-plus/dist/index.css'
import axios from 'axios'
import closeIcon from '../img/close.png'

const props = defineProps({
  payload: {
    type: Object,
    default: () => ({})
  }
})

defineEmits<{
  (e: 'close'): void
}>()

const BASE_URL = 'http://23.99.16.179:11001/api/boot/system/bridge'

const tabList = ['日报', '周报', '月报']
const activeTab = ref(normalizeReportType(props.payload?.reportType) - 1)

const facilityOptions = [
  { label: '桥梁', value: 1 },
  { label: '隧道', value: 4 }
]

const searchForm = ref({
  structType: Number(props.payload?.structType) || null as number | null,
  date: null as number | null
})

const tableData = ref<any[]>([])

const pagination = ref({
  current: 1,
  totalPage: 1,
  size: 10,
  total: 0
})

function normalizeReportType(value: any) {
  const type = Number(value)
  return [1, 2, 3].includes(type) ? type : 1
}

const columns = computed(() => [
  {
    title: '序号',
    key: 'index',
    width: 70,
    align: 'center',
    render: (_row: any, index: number) => (pagination.value.current - 1) * pagination.value.size + index + 1
  },
  { title: '报告编号', key: 'reportId', width: 190, align: 'center', ellipsis: { tooltip: true } },
  { title: '报告名称', key: 'reportName', align: 'center', ellipsis: { tooltip: true } },
  { title: '创建时间', key: 'gmtCreate', width: 180, align: 'center', ellipsis: { tooltip: true } },
  {
    title: '状态',
    key: 'status',
    width: 120,
    align: 'center',
    render: (row: any) => {
      const isSuccess = /成功|完成|正常/.test(row.status || '')
      return h('span', {
        class: isSuccess ? 'status-tag status-tag--success' : 'status-tag status-tag--warning'
      }, row.status || '-')
    }
  },
  { title: '下载次数', key: 'downloadTimes', width: 110, align: 'center' },
  {
    title: '结构状态评估',
    key: 'statusAsse',
    width: 150,
    align: 'center',
    render: (row: any) => {
      const isNormal = /正常|良好/.test(row.statusAsse || '')
      return h('span', {
        class: isNormal ? 'assess-text assess-text--success' : 'assess-text assess-text--warning'
      }, row.statusAsse || '-')
    }
  },
  {
    title: '操作',
    key: 'action',
    width: 100,
    align: 'center',
    render(row: any) {
      return h('a', {
        class: row.reportUrl ? 'action-link' : 'action-link action-link--disabled',
        onClick: () => handleDownload(row)
      }, '下载')
    }
  }
])

function getPageContent(data: any) {
  return data?.content || data?.records || data?.list || data?.rows || data?.data || []
}

function getPageTotal(data: any) {
  return Number(data?.page?.total ?? data?.total ?? data?.totalElements ?? data?.count ?? 0) || 0
}

function formatDate(value: number | null) {
  if (!value) return undefined
  const date = new Date(value)
  const yy = String(date.getFullYear())
  const mm = String(date.getMonth() + 1).padStart(2, '0')
  const dd = String(date.getDate()).padStart(2, '0')
  return `${yy}年${mm}月${dd}日`
}

function normalizeRow(item: any) {
  return {
    reportId: item?.reportId || '-',
    reportName: item?.reportName || '-',
    gmtCreate: item?.gmtCreate || '-',
    status: item?.status || '-',
    downloadTimes: item?.downloadTimes ?? 0,
    statusAsse: item?.statusAsse || '-',
    reportUrl: item?.reportUrl || ''
  }
}

async function fetchReportList() {
  try {
    const res = await axios.get(`${BASE_URL}/report`, {
      params: {
        structType: searchForm.value.structType || undefined,
        type: activeTab.value + 1,
        time: formatDate(searchForm.value.date),
        pageIndex: pagination.value.current,
        pageSize: pagination.value.size
      }
    })
    const data = res?.data?.data || res?.data || {}
    const rows = getPageContent(data)
    const total = getPageTotal(data)
    tableData.value = Array.isArray(rows) ? rows.map(normalizeRow) : []
    pagination.value.total = total || tableData.value.length
    pagination.value.totalPage = Math.max(1, Math.ceil(pagination.value.total / pagination.value.size))
  } catch (err) {
    console.error('获取桥隧报告列表失败', err)
    tableData.value = []
    pagination.value.total = 0
    pagination.value.totalPage = 1
  }
}

function handlePageChange(page: number) {
  pagination.value.current = page
  fetchReportList()
}

function handleTabChange(index: number) {
  activeTab.value = index
  pagination.value.current = 1
  fetchReportList()
}

function handleSearch() {
  pagination.value.current = 1
  fetchReportList()
}

function handleReset() {
  searchForm.value = {
    structType: null,
    date: null
  }
  pagination.value.current = 1
  fetchReportList()
}

function handleDownload(row: any) {
  if (!row.reportUrl) return
  window.open(row.reportUrl, '_blank')
}

watch(
  () => props.payload?.structType,
  value => {
    searchForm.value.structType = Number(value) || null
    pagination.value.current = 1
    fetchReportList()
  }
)

watch(
  () => props.payload?.reportType,
  value => {
    activeTab.value = normalizeReportType(value) - 1
    pagination.value.current = 1
    fetchReportList()
  }
)

onMounted(() => {
  fetchReportList()
})
</script>

<style scoped lang="scss">
.report-list-popup {
  position: absolute;
  left: 50%;
  top: 50%;
  z-index: 1001;
  width: 1253px;
  height: 648px;
  transform: translate(-50%, -50%);
  background: url('../img/diaBg.png') no-repeat center / 100% 100%;
  color: #d8f5ff;
  font-family: Microsoft YaHei, Arial, sans-serif;
  box-sizing: border-box;
  padding: 0 20px 20px;
}

.popup-header {
  height: 64px;
  position: relative;
  display: flex;
  justify-content: center;
  align-items: flex-start;
}

.title-bg {
  width: 220px;
  height: 50px;
  background: url('../img/diaTitleBg.png') no-repeat center / 100% 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.title {
  margin: 0;
  font-size: 18px;
  color: #dff8ff;
  font-weight: 700;
  letter-spacing: 1px;
}

.close-btn {
  position: absolute;
  top: 14px;
  right: 0;
  width: 18px;
  height: 18px;
  cursor: pointer;
}

.popup-body {
  height: calc(100% - 64px);
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.tab-bar {
  height: 44px;
  display: flex;
  gap: 16px;
  align-items: center;
}

.tab-item {
  width: 92px;
  height: 32px;
  line-height: 32px;
  text-align: center;
  background: rgba(8, 78, 132, 0.7);
  border: 1px solid #116aa6;
  color: rgba(216, 245, 255, 0.75);
  cursor: pointer;
  border-radius: 3px;
}

.tab-item.active {
  color: #fff;
  background: linear-gradient(180deg, #00a6ff 0%, #0070c4 100%);
  border-color: #2bbcff;
  box-shadow: 0 0 12px rgba(0, 174, 255, 0.45);
}

.search-bar {
  height: 54px;
  display: flex;
  align-items: center;
  gap: 175px;
}

.search-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #d8f5ff;
}

.search-item label {
  flex: 0 0 auto;
  line-height: 32px;
  white-space: nowrap;
}

.search-select {
  width: 170px;
  flex: 0 0 170px;
}

.date-config-wrapper {
  display: flex;
  width: 180px;
  flex: 0 0 180px;
  min-width: 180px;
}

.search-date-picker {
  width: 100%;
  min-width: 180px;
}

.search-btn-item {
  gap: 12px;
}

.search-btn {
  min-width: 68px;
  height: 32px;
  border: 1px solid #1a8ed1;
  border-radius: 3px;
  background: linear-gradient(180deg, #0b78ba, #07538f);
  color: #fff;
  cursor: pointer;
}

.reset-btn {
  background: rgba(8, 70, 119, 0.85);
}

.table-container {
  flex: 1;
  min-height: 0;
  border: 1px solid rgba(22, 118, 177, 0.8);
  background: rgba(3, 31, 58, 0.78);
}

.pagination-wrapper {
  height: 54px;
  display: flex;
  justify-content: center;
  align-items: center;
}

:deep(.report-table) {
  --n-merged-th-color: #0b4c86;
  --n-merged-td-color: rgba(5, 34, 61, 0.94);
  --n-merged-td-color-hover: rgba(15, 88, 138, 0.92);
  --n-merged-border-color: rgba(34, 121, 177, 0.45);
  color: #fff;
}

:deep(.report-table .n-data-table-th) {
  height: 42px;
  color: #dff8ff;
  font-size: 16px;
  font-weight: 700;
  background: #0a4d86;
}

:deep(.report-table .n-data-table-td) {
  height: 38px;
  color: #f2fbff;
  font-size: 14px;
}

:deep(.report-table .n-data-table-tr:nth-child(even) .n-data-table-td) {
  background: rgba(5, 77, 116, 0.94);
}

:deep(.n-pagination .n-pagination-item) {
  color: #bfeeff;
  background: rgba(6, 65, 111, 0.78);
  border-color: rgba(36, 142, 204, 0.72);
}

:deep(.n-pagination .n-pagination-item--active) {
  color: #fff;
  background: #078feb;
}

:deep(.el-select__wrapper),
:deep(.n-date-picker .n-input) {
  min-height: 32px;
  height: 32px;
  background: rgba(6, 51, 92, 0.92);
  border: 1px solid #176ca7;
  box-shadow: none;
}

:deep(.el-select),
:deep(.n-date-picker) {
  width: 100%;
}

:deep(.n-input-wrapper) {
  height: 30px;
  line-height: 30px;
}

:deep(.n-input__input),
:deep(.n-input__input-el),
:deep(.n-input__placeholder) {
  height: 30px;
  line-height: 30px;
}

:deep(.el-select__placeholder),
:deep(.el-select__selected-item),
:deep(.n-input__input-el),
:deep(.n-base-selection-placeholder),
:deep(.n-input__placeholder) {
  color: #d8f5ff;
}

:deep(.n-input__border),
:deep(.n-input__state-border) {
  border-color: #176ca7;
}

:deep(.status-tag) {
  display: inline-flex;
  min-width: 48px;
  justify-content: center;
  padding: 2px 8px;
  border-radius: 2px;
}

:deep(.status-tag--success) {
  color: #38ff9d;
  border: 1px solid #38ff9d;
}

:deep(.status-tag--warning) {
  color: #ffbc00;
  border: 1px solid #ffbc00;
}

:deep(.assess-text--success) {
  color: #38ff9d;
}

:deep(.assess-text--warning) {
  color: #ff4d4f;
}

:deep(.action-link) {
  color: #04bcfa;
  cursor: pointer;
  text-decoration: none;
}

:deep(.action-link--disabled) {
  cursor: not-allowed;
  opacity: 0.5;
}
</style>
