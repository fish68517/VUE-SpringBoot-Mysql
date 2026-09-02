<template>
  <div class="fire-passage-modal">
    <div class="modal-header">
      <div class="header-title">化粪池详情</div>
      <button class="close-btn" @click="handleClose">×</button>
    </div>

    <div class="modal-content">
      <FirePointDetailInfo
        :columns="columns"
        :data="tableData"
      />
    </div>

    <!-- <div class="modal-footer">
      <n-pagination
        :page="currentPage"
        :item-count="total"
        :page-size="pageSize"
        :page-slot="5"
        @update:page="handlePageChange"
      />
    </div> -->
  </div>
</template>

<script setup>
import { h, onMounted, ref, watch } from 'vue'
import { NPagination } from 'naive-ui'
import axios from 'axios'
import { BASE_URL } from '../config'
import FirePointDetailInfo from './FirePointDetailInfo.vue'

const emit = defineEmits(['close'])
const props = defineProps({
  name: {
    type: String,
    default: ''
  },
  type: {
    type: String,
    default: ''
  }
})

const currentPage = ref(1)
const total = ref(0)
const pageSize = 10
const tableData = ref([])
const loading = ref(false)

const columns = [
  {
    title: '序号',
    key: 'index',
    width: 60,
    render(_row, rowIndex) {
      return h('span', {}, (currentPage.value - 1) * pageSize + rowIndex + 1)
    }
  },
  { title: '设备编码', key: 'dvcode', width: 160 },
  { title: '设备名称', key: 'dvname', minWidth: 220 },
  { title: '设备机型编码', key: 'dvjxcode', width: 160 },
  { title: '型号名称', key: 'modelname', width: 160 },
  { title: '安装单位全称', key: 'enfullname', minWidth: 240 },
  { title: '安装地址', key: 'mountAddress', minWidth: 260 },
  { title: '街道名称', key: 'street', width: 140 }
]

const getQueryParams = () => ({
  pageNumber: currentPage.value,
  pageSize,
  name: props.name || undefined
})

const getResponsePage = data => {
  const result = data?.data ?? data
  const records = result?.records || result?.content || result?.list || result?.rows || []
  const totalCount = Number(result?.total ?? result?.page?.total ?? records.length)

  return {
    records: Array.isArray(records) ? records : [],
    total: Number.isFinite(totalCount) ? totalCount : 0
  }
}

const getSepticTankList = async () => {
  loading.value = true
  try {
    const res = await axios.post(`${BASE_URL}/list/location/septic/detail`, getQueryParams())
    const pageData = getResponsePage(res?.data)
    tableData.value = pageData.records
    total.value = pageData.total
  } catch (err) {
    console.error('获取化粪池列表失败:', err)
    tableData.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function handleClose() {
  emit('close')
}

function handlePageChange(page) {
  currentPage.value = page
  getSepticTankList()
}

watch(() => [props.name, props.type], () => {
  currentPage.value = 1
  getSepticTankList()
})

onMounted(() => {
  getSepticTankList()
})
</script>

<script>
export default {
  name: 'FireSepticTankList'
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

.fire-passage-modal {
  font-family: 'AlibabaPuHuiTi', sans-serif;
  position: absolute;
  transform: translate(-50%, -50%);
  z-index: 1001;
  width: 576px;
  height: 400px;
  display: flex;
  flex-direction: column;
  background:
    url('../img/Group_2136640490.png') top center / 100% 60px no-repeat,
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
  height: 60px;
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
  padding: 16px 32px 24px 32px;
  overflow-y: auto;
  overflow-x: hidden;
  scrollbar-width: thin;
  scrollbar-color: rgba(75, 174, 255, 0.65) rgba(5, 26, 64, 0.35);

  &::-webkit-scrollbar {
    width: 8px;
  }

  &::-webkit-scrollbar-track {
    background: rgba(5, 26, 64, 0.35);
    border-radius: 8px;
  }

  &::-webkit-scrollbar-thumb {
    background: linear-gradient(180deg, rgba(75, 174, 255, 0.85), rgba(20, 96, 202, 0.62));
    border: 2px solid rgba(5, 26, 64, 0.35);
    border-radius: 8px;
  }

  &::-webkit-scrollbar-thumb:hover {
    background: linear-gradient(180deg, rgba(119, 205, 255, 0.95), rgba(42, 128, 236, 0.78));
  }
}

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
    min-height: 40px;
    padding: 6px 4px;
    color: #d7e9ff;
    font-family: 'D-DIN', 'AlibabaPuHuiTi', sans-serif;
    background: #0A325F;
    border: none;
    line-height: 18px;
    white-space: normal;
    word-break: break-all;
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

  :deep(.n-pagination-item--disabled),
  :deep(.n-pagination-button[disabled]) {
    opacity: 0.4;
    cursor: not-allowed;
    pointer-events: none;
  }
}
</style>
