<template>
  <div class="point-warning-list">
    <div class="modal-header">
      <div class="header-title">点位预警</div>
      <button class="close-btn" @click="handleClose"></button>
    </div>

    <div class="modal-body">
      <div class="tab-bar">
        <button
          v-for="tab in tabs"
          :key="tab.key"
          class="tab"
          :class="{ active: activeTab === tab.key }"
          @click="activeTab = tab.key"
        >
          {{ tab.label }}
          <span class="tab-count">{{ tab.count }}</span>
        </button>
      </div>

      <div class="table-wrap">
        <table class="warning-table">
          <thead>
            <tr>
              <th class="col-index">序号</th>
              <th>预警点位</th>
              <th class="col-level">预警等级</th>
              <th class="col-rain">降水量</th>
              <th class="col-threshold">预警阈值</th>
              <th class="col-time">预警时间段</th>
              <th class="col-action">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in currentRows" :key="row.id">
              <td class="col-index">{{ row.id }}</td>
              <td class="point-name">{{ row.pointName }}</td>
              <td class="col-level"><span class="level-tag">{{ row.level }}</span></td>
              <td class="col-rain">{{ row.rainfall }}</td>
              <td class="col-threshold">{{ row.threshold }}</td>
              <td class="col-time">{{ row.timeRange }}</td>
              <td class="col-action">
                <button class="detail-btn" @click="handleDetail(row)">详情</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="pagination">
        <span class="page-total">共 {{ totalPages }} 页</span>
        <button class="page-btn" :disabled="currentPage <= 1" @click="changePage(currentPage - 1)">&lt;</button>
        <button
          v-for="(page, idx) in pages"
          :key="idx"
          class="page-btn"
          :class="{ active: page === currentPage, ellipsis: page === '...' }"
          :disabled="page === '...'"
          @click="page !== '...' && changePage(page)"
        >
          {{ page }}
        </button>
        <button class="page-btn" :disabled="currentPage >= totalPages" @click="changePage(currentPage + 1)">&gt;</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch, onMounted } from 'vue'
import axios from 'axios'

const WATERLOG_BASE_URL = 'http://23.99.16.179:11001/api/boot/system/waterlog'

const props = defineProps({
  round: {
    type: [String, Number],
    default: null
  }
})

const emit = defineEmits(['close', 'detail'])

// 预警状态映射
const warningStatusMap = { '0': '待响应', '1': '响应中', '2': '已结束' }
const warningLevelMap = { '1': '一级', '2': '二级', '3': '三级', '4': '四级' }

// 分页
const currentPage = ref(1)
const pageSize = 10
const totalPages = ref(1)

// 接口返回的当前页数据
const tableData = ref([])

// tab统计数
const tabStats = ref({ total: 0, solved: 0, unsolve: 0 })

// tabs
const tabs = computed(() => {
  const pending = tabStats.value.unsolve || 0
  const responding = tableData.value.filter(d => d.warningStatus === '1').length
  const ended = tabStats.value.solved || 0
  const total = tabStats.value.total || 0
  return [
    { key: 'all', label: '全部', count: total },
    { key: '0', label: '待响应', count: pending },
    { key: '1', label: '响应中', count: responding },
    { key: '2', label: '已结束', count: ended }
  ]
})

const activeTab = ref('0')

// 当前页行数据映射
const currentRows = computed(() => {
  return tableData.value.map((row, index) => ({
    id: (currentPage.value - 1) * pageSize + index + 1,
    pointName: row.name || '-',
    level: warningLevelMap[row.warningLevel] || row.warningLevel || '-',
    rainfall: row.twoHourRain != null ? `${row.twoHourRain}mm` : '-',
    threshold: row.warningThreshold != null ? `${row.warningThreshold}mm` : '-',
    timeRange: formatPeriod(row.warningPeriodStart, row.warningPeriodEnd),
    rawData: row
  }))
})

// 切换 tab 时重置页码并重新请求
watch(activeTab, () => {
  currentPage.value = 1
  fetchWarningList()
})

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

function formatPeriod(start, end) {
  if (!start && !end) return '-'
  return `${start || ''} - ${end || ''}`
}

function changePage(page) {
  if (page < 1 || page > totalPages.value) return
  currentPage.value = page
  fetchWarningList()
}

// 获取雨前预警列表（服务端分页）
const fetchWarningList = async () => {
  try {
    const params = { pageNumber: currentPage.value, pageSize }
    if (activeTab.value !== 'all') {
      params.warningStatus = activeTab.value
    }
    if (props.round != null && props.round !== 'latest') {
      params.round = props.round
    }
    const res = await axios.get(`${WATERLOG_BASE_URL}/dot/early/warning`, { params })
    const data = res?.data?.data
    if (data) {
      tableData.value = data.records || []
      totalPages.value = data.pages || 1
    }
  } catch (error) {
    console.error('获取点位预警列表失败:', error)
  }
}

// 获取tab统计数据
const fetchWarningStats = async () => {
  try {
    const params = {}
    if (props.round != null && props.round !== 'latest') {
      params.round = props.round
    }
    const res = await axios.get(`${WATERLOG_BASE_URL}/dot/early/warning/statistics`, { params })
    const d = res?.data?.data
    if (d) {
      tabStats.value = { total: d.total || 0, solved: d.solved || 0, unsolve: d.unsolve || 0 }
    }
  } catch (error) {
    console.error('获取雨前预警统计失败:', error)
  }
}

function handleClose() {
  emit('close')
}

function handleDetail(row) {
  emit('detail', row.rawData)
}

onMounted(() => {
  fetchWarningStats()
  fetchWarningList()
})
</script>

<script>
export default {
  name: 'WaterlogPointWarningList'
}
</script>

<style lang="scss" scoped>
@font-face {
  font-family: 'Alibaba PuHuiTi 2.0';
  src: url('../font/Alibaba_PuHuiTi_2.0_55_Regular_55_Regular.ttf') format('truetype');
  font-weight: 400;
  font-style: normal;
}

@font-face {
  font-family: 'D-DIN';
  src: url('../font/D-DIN.otf') format('opentype');
  font-weight: normal;
  font-style: normal;
}

.point-warning-list {
  font-family: 'Alibaba PuHuiTi 2.0', sans-serif;
  position: absolute;
  left: 50%;
  top: 50%;
  z-index: 1001;
  width: 1156px;
  height: 812px;
  transform: translate(-50%, -50%);
  color: #d8ecff;
  background:
    radial-gradient(122.26% 78.82% at 0% 0%, rgba(20, 106, 255, 0.24) 0%, rgba(21, 106, 255, 0.14) 22.78%, rgba(14, 113, 255, 0.07) 51.23%, rgba(20, 106, 255, 0) 100%),
    #0A2B4A;
  border: 1px solid rgba(166, 206, 255, 0.3);
  box-shadow: 0 16px 56px rgba(0, 0, 0, 0.55);
  overflow: hidden;
  pointer-events: auto;
}

.modal-header {
  position: relative;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background:
    linear-gradient(90deg, #0b3b7000 0.2%, #2e6bdc59 50.04%, #0c2d5c00 99.87%),
    url('../img/waterlog_title_bg.png') no-repeat center / 100% 100%;
}

.header-title {
  text-align: center;
  font-size: 18px;
  font-weight: 500;
  background: linear-gradient(0deg, #5FBCFF 1.4%, #FFF 58.67%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.close-btn {
  position: absolute;
  right: 14px;
  top: 8px;
  width: 34px;
  height: 34px;
  border: none;
  background: url('../img/close.png') no-repeat center / 100% 100%;
  cursor: pointer;
}

.modal-body {
  position: relative;
  height: calc(100% - 48px);
  padding: 27px 30px 26px;
  box-sizing: border-box;
}

.tab-bar {
  display: flex;
  gap: 16px;
  height: 36px;
  margin-bottom: 18px;
}

.tab {
  width: 96px;
  height: 36px;
  border: none;
  color: #749dc0;
  background: url('../img/waterlog_tab_noactive.png') no-repeat center / 100% 100%;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 0.96px;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 4px;

  &.active {
    color: #ffffff;
    background: url('../img/waterlog_tab.png') no-repeat center / 100% 100%;
  }
}

.tab-count {
  font-family: 'D-DIN', sans-serif;
  font-size: 12px;
  color: #ffd039;
  font-weight: 700;
  text-shadow: 0 0 6px rgba(255, 208, 57, 0.5);

  .tab:not(.active) & {
    color: #a6cfff;
    text-shadow: none;
  }
}

.table-wrap {
  height: 607px;
  overflow: hidden;
}

.warning-table {
  width: 100%;
  border-collapse: collapse;
  table-layout: fixed;
  font-size: 14px;

  th {
    height: 38px;
    color: #8bb4da;
    font-weight: 400;
    text-align: left;
    background: #114f87;
  }

  td {
    height: 38px;
    color: #ffffff;
    background: rgba(6, 33, 65, 0.8);
    font-family: 'D-DIN', 'Alibaba PuHuiTi 2.0', sans-serif;
  }

  tbody tr:nth-child(even) td {
    background: rgba(28, 74, 118, 0.78);
  }

  th,
  td {
    padding: 0 10px;
    box-sizing: border-box;
  }
}

.col-index {
  width: 52px;
  text-align: center;
}

.col-level {
  width: 152px;
}

.col-rain,
.col-threshold {
  width: 152px;
}

.col-time {
  width: 230px;
}

.col-action {
  width: 76px;
  text-align: center;
}

.point-name {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.level-tag {
  display: inline-flex;
  height: 22px;
  padding: 0 6px;
  align-items: center;
  color: #ffffff;
  border: 1px solid #1cf582;
  background: rgba(20, 171, 93, 0.18);
  box-sizing: border-box;
}

.detail-btn {
  border: none;
  background: transparent;
  color: #00a2ff;
  font-size: 14px;
  cursor: pointer;
}

.pagination {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 7px;
}

.page-total {
  color: #c4ddf5;
  font-size: 12px;
  margin-right: 2px;
}

.page-btn {
  min-width: 22px;
  height: 22px;
  padding: 0 6px;
  color: #86b8e5;
  border: 1px solid rgba(33, 135, 229, 0.86);
  border-radius: 3px;
  background: rgba(11, 60, 111, 0.66);
  font-family: 'D-DIN', sans-serif;
  cursor: default;

  &.active {
    color: #ffffff;
    border-color: #159dff;
    background: #0f8ce8;
  }

  &.ellipsis {
    border: none;
    background: transparent;
    cursor: default;
  }
}
</style>
