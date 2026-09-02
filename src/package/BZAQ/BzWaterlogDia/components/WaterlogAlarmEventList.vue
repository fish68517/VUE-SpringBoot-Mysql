<template>
  <div class="modal-container">
    <div class="modal-header">
      <div class="header-title">{{ title }}</div>
      <button class="close-btn" @click="handleClose"></button>
    </div>
    <div class="modal-body">
      <div class="table-container">
        <table>
          <thead>
            <tr>
              <th style="width: 80px;">序号</th>
              <th>点位名称</th>
              <th style="width: 120px;">所属区县</th>
              <th style="width: 140px;">所属镇街</th>
              <th style="width: 100px;">处置人数</th>
              <th style="width: 120px;">特种车辆</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(row, index) in tableData" :key="index" @click="handleDetail(row)">
              <td>{{ index + 1 }}</td>
              <td class="ellipsis-text" :title="row.riskPoint">{{ row.riskPoint }}</td>
              <td>{{ row.county }}</td>
              <td>{{ row.town }}</td>
              <td>{{ row.personnelNumber }}</td>
              <td>{{ row.specialVehiclesNumber }}</td>
            </tr>
          </tbody>
        </table>
      </div>
      <div class="pagination">
        <span class="total">共 {{ totalCount }} 条 / {{ totalPages }} 页</span>
        <button class="page-btn prev" :disabled="currentPage <= 1" @click="changePage(currentPage - 1)">&lt;</button>
        <button 
          v-for="(page, idx) in pages" 
          :key="idx"
          class="page-btn"
          :class="{ active: currentPage === page, ellipsis: page === '...' }"
          :disabled="page === '...'"
          @click="page !== '...' && changePage(page)"
        >
          {{ page }}
        </button>
        <button class="page-btn next" :disabled="currentPage >= totalPages" @click="changePage(currentPage + 1)">&gt;</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import axios from 'axios'

const WATERLOG_BASE_URL = 'http://23.99.16.179:11001/api/boot/system/waterlog'

const emit = defineEmits(['close', 'detail'])

const props = defineProps({
  round: {
    type: [String, Number],
    default: ''
  }
})

const title = ref('积水告警事件列表')
const tableData = ref([])
const statisticsData = ref({})

const currentPage = ref(1)
const pageSize = 10
const totalPages = ref(1)
const totalCount = ref(0)

const pages = computed(() => {
  const total = totalPages.value
  const cur = currentPage.value
  if (total <= 7) return Array.from({ length: total }, (_, i) => i + 1)
  const result = []
  result.push(1)
  if (cur > 4) result.push('...')
  const start = Math.max(2, cur - 1)
  const end = Math.min(total - 1, cur + 1)
  for (let i = start; i <= end; i++) result.push(i)
  if (cur < total - 3) result.push('...')
  result.push(total)
  return result
})

const fetchStatistics = async () => {
  try {
    const res = await axios.get(`${WATERLOG_BASE_URL}/disposition/early/warning/statistics`, {
      params: { round: props.round || undefined }
    })
    if (res?.data?.data) {
      statisticsData.value = res.data.data
    }
  } catch (error) {
    console.error('获取积水预警统计失败:', error)
  }
}

const fetchAlarmList = async () => {
  try {
    const res = await axios.get(`${WATERLOG_BASE_URL}/disposition/early/warning`, {
      params: { pageNumber: currentPage.value, pageSize, round: props.round || undefined }
    })
    const data = res?.data?.data
    if (data) {
      tableData.value = data.records || []
      totalPages.value = data.pages || 1
      totalCount.value = data.total || 0
    }
  } catch (error) {
    console.error('获取积水预警列表失败:', error)
  }
}

function handleClose() {
  emit('close')
}

function handleDetail(row) {
  emit('detail', row)
}

function changePage(page) {
  if (page < 1 || page > totalPages.value) return
  currentPage.value = page
  fetchAlarmList()
}

onMounted(() => {
  fetchAlarmList()
  fetchStatistics()
})

watch(() => props.round, () => {
  currentPage.value = 1
  fetchAlarmList()
  fetchStatistics()
})
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

.modal-container {
  font-family: 'AlibabaPuHuiTi', sans-serif;
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  z-index: 1001;
  width: 904px;
  height: 604px;
  display: flex;
  flex-direction: column;
  background:
    url('../img/Group-2136640490.png') top center / 100% 48px no-repeat,
    url('../img/Rectangle-346242153.png') center / 100% 100% no-repeat;
  color: #b8d9ff;
  font-size: 14px;
  pointer-events: auto;
}

.modal-header {
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  flex-shrink: 0;

  .header-title {
    color: #ffffff;
    font-size: 18px;
    font-weight: 700;
  }

  .close-btn {
    position: absolute;
    right: 16px;
    top: 50%;
    transform: translateY(-50%);
    width: 14px;
    height: 14px;
    background: url('../img/close.png') no-repeat center / cover;
    border: none;
    cursor: pointer;
  }
}

.modal-body {
  flex: 1;
  padding: 20px 30px 30px;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.table-container {
  flex: 1;
  overflow: auto;
  
  table {
    width: 100%;
    border-collapse: collapse;
    text-align: left;
    font-size: 14px;
    
    th, td {
      padding: 12px 16px;
      white-space: nowrap;
    }

    th {
      color: #749dc0;
      font-weight: 500;
      background: rgba(24, 88, 163, 0.2);
      border-bottom: 1px solid rgba(166, 206, 255, 0.15);
      border-top: 1px solid rgba(166, 206, 255, 0.15);
    }

    td {
      color: #d8ecff;
      border-bottom: 1px solid rgba(166, 206, 255, 0.05);
    }

    tr:nth-child(even) {
      background: rgba(255, 255, 255, 0.02);
    }
    
    tr:hover {
      background: rgba(255, 255, 255, 0.05);
      cursor: pointer;
    }
    
    .ellipsis-text {
      max-width: 250px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 16px;
  font-size: 14px;
  color: #749dc0;

  .total {
    margin-right: 8px;
  }

  .page-btn {
    width: 28px;
    height: 28px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: transparent;
    border: 1px solid rgba(166, 206, 255, 0.3);
    color: #749dc0;
    border-radius: 3px;
    cursor: pointer;
    font-family: 'AlibabaPuHuiTi', sans-serif;
    padding: 0;

    &.active {
      background: #1676ea;
      color: #ffffff;
      border-color: #1676ea;
    }

    &.ellipsis {
      border: none;
      cursor: default;
      color: #749dc0;
    }

    &:hover:not(.active):not(.ellipsis) {
      border-color: #1676ea;
      color: #1676ea;
    }
  }
}
</style>
