<template>
  <div class="fire-risk-point-modal">
    <div class="modal-header">
      <div class="header-title">{{ title }}</div>
      <button class="close-btn" @click="handleClose">×</button>
    </div>
    <div class="query-bar">
      <n-select
        v-model:value="isTodayValue"
        class="filter-select"
        :options="isTodayOptions"
        size="small"
        placeholder="是否当日"
        @update:value="handleSearch"
      />
      <n-button class="query-btn" size="small" @click="handleSearch">查询</n-button>
      <n-button class="query-btn" size="small" @click="handleReset">重置</n-button>
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
        :page-count="totalPage"
        :page-slot="5"
        @update:page="handlePageChange"
      />
    </div>

    <div v-if="detailVisible" class="device-detail-overlay">
      <div class="device-detail-modal">
        <header class="device-detail-header">
          <span>设备告警详情</span>
          <button type="button" class="device-detail-close" @click="closeDetail">×</button>
        </header>
        <div class="device-detail-body">
          <section class="device-detail-left">
            <div class="alarm-card">
              <div class="section-tab">告警信息</div>
              <div class="alarm-message">{{ alarmMessage }}</div>
            </div>
            <div class="section-title">告警曲线</div>
            <div class="chart-card">
              <v-chart class="trend-chart" :option="trendChartOption" :loading="curveLoading" autoresize />
            </div>
          </section>
          <section class="device-detail-right">
            <div class="disposal-title">
              <span>处置情况</span>
              <strong>未完成</strong>
              <em>/处置完成</em>
            </div>
            <div class="disposal-card">
              <div v-if="disposalLoading" class="disposal-empty">加载中...</div>
              <div v-else-if="!disposalList.length" class="disposal-empty">暂无处置记录</div>
              <template v-else>
                <div v-for="item in disposalList" :key="item.key" class="disposal-item">
                  <div class="disposal-time">{{ item.time }}</div>
                  <div><span>处置人：</span>{{ item.person }}</div>
                  <div><span>处置内容：</span>{{ item.content }}</div>
                  <div><span>处置结果：</span>{{ item.result }}</div>
                </div>
              </template>
            </div>
          </section>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, h, ref, onMounted, watch } from 'vue'
import { NButton, NDataTable, NPagination, NSelect } from 'naive-ui'
import axios from 'axios'
import { BASE_URL } from '../config'

const emit = defineEmits(['close'])
const props = defineProps({
  isToday: {
    type: String,
    default: '0'
  }
})

const currentPage = ref(1)
const totalPage = ref(1)
const pageSize = 10
const tableData = ref([])
const isTodayValue = ref(props.isToday === '1' ? '1' : '0')
const detailVisible = ref(false)
const currentDetail = ref({})
const curveLoading = ref(false)
const curveData = ref([])
const disposalLoading = ref(false)
const disposalList = ref([])
let curveRequestVersion = 0
let disposalRequestVersion = 0

const warnLevelMap = {
  1: { label: '蓝色预警', className: 'warn-level--blue' },
  2: { label: '黄色预警', className: 'warn-level--yellow' },
  3: { label: '橙色预警', className: 'warn-level--orange' },
  4: { label: '红色预警', className: 'warn-level--red' }
}

const title ='设备告警'
const isTodayOptions = [
  { label: '否', value: '0' },
  { label: '是', value: '1' }
]

const columns = [
  {
    title: '序号',
    key: 'index',
    width: 80,
    render(_row, rowIndex) {
      return h('span', {}, (currentPage.value - 1) * pageSize + rowIndex + 1)
    }
  },
  { title: '隐患点名称', key: 'disasterName', width: 160 },
  { title: '隐患点编码', key: 'unifiedcode', width: 180 },
  { title: '项目名称', key: 'projectName', width: 160 },
  {
    title: '预警等级',
    key: 'warnLevel',
    width: 140,
    render(row) {
      const warnLevel = warnLevelMap[Number(row.warnLevel)]
      if (!warnLevel) return row.warnLevel || '--'
      return h('span', { class: ['warn-level', warnLevel.className] }, warnLevel.label)
    }
  },
  { title: '开始时间', key: 'startTime', width: 160 },
  { title: '结束时间', key: 'endTime', width: 160 },
  {
    title: '操作',
    key: 'action',
    width: 120,
    fixed: 'right',
    render(row) {
      return h(
        NButton,
        {
          text: true,
          class: 'detail-btn',
          onClick: () => openDetail(row)
        },
        { default: () => '查看详情' }
      )
    }
  }
]

// 表格固定横向滚动宽度，列多时通过横向滚动完整展示。
const tableScrollX = 1920

const getQueryParams = () => ({
  Index: currentPage.value,
  size:pageSize,
  isToday: isTodayValue.value,
  // name: searchName.value || undefined
})

const requestRiskList = async () => {
  let api = "/api/boot/system/land/landWarnAmountListPage"
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
    console.error('获取设备告警列表失败:', error)
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
  currentPage.value = 1
  isTodayValue.value = '0'
  getRiskPointList()
}

function handlePageChange(page) {
  currentPage.value = page
  getRiskPointList()
}

const value = (...keys) => keys.map(key => currentDetail.value?.[key]).find(item => item !== undefined && item !== null && item !== '')
const field = (...keys) => String(value(...keys) ?? '-')

const alarmMessage = computed(() => field('msg', 'message', 'warnContent', 'content'))

function formatWarnLevel(warnLevel) {
  const item = warnLevelMap[Number(warnLevel)]
  return item?.label || String(warnLevel ?? '-')
}

function openDetail(row) {
  currentDetail.value = { ...row }
  detailVisible.value = true
  requestDeviceCurve()
  requestDisposalList()
}

function closeDetail() {
  detailVisible.value = false
  currentDetail.value = {}
  curveData.value = []
  disposalList.value = []
  disposalLoading.value = false
  disposalRequestVersion += 1
}

const curveFieldOptions = [
  { key: 'rainfallIntensityNum', label: '雨量强度(mm)' },
  { key: 'yl', label: '压力值(KN)' },
  { key: 'nowXNum', label: '当前X轴角度' },
  { key: 'nowYNum', label: '当前Y轴角度' },
  { key: 'nowZNum', label: '当前Z轴角度' },
  { key: 'nowPullLineNum', label: '当前拉线值(mm)' },
  { key: 'speedXNum', label: 'X轴速度' },
  { key: 'speedYNum', label: 'Y轴速度' },
  { key: 'speedZNum', label: 'Z轴速度' },
  { key: 'shiftXNum', label: 'X轴位移' },
  { key: 'shiftYNum', label: 'Y轴位移' },
  { key: 'shiftZNum', label: 'Z轴位移' },
  { key: 'angleXNum', label: 'X轴角度' },
  { key: 'angleYNum', label: 'Y轴角度' },
  { key: 'angleZNum', label: 'Z轴角度' },
  { key: 'displacementNum', label: '合位移量' },
  { key: 'xdisplacementNum', label: 'X方向合位移量' },
  { key: 'ydisplacementNum', label: 'Y方向合位移量' }
]

const currentDayHours = Array.from(
  { length: 24 },
  (_, hour) => `${String(hour).padStart(2, '0')}:00`
)

const getCurveNumber = rawValue => {
  if (rawValue === null || rawValue === undefined || rawValue === '') return null
  const number = Number(rawValue)
  return Number.isFinite(number) ? number : null
}

const deviceTypeText = computed(() => String(
  value('deviceType', 'devTypeName', 'deviceTypeName', 'devType') || ''
).toLowerCase())
const isCrackMonitor = computed(() => /裂缝|crack|lf|拉线/.test(deviceTypeText.value))
const isDisplacementMonitor = computed(() => /位移|displacement|wy|gnss|倾斜/.test(deviceTypeText.value))
const isDeformationMonitor = computed(() => /形变|deformation|应力|stress/.test(deviceTypeText.value))
const isRainfallMonitor = computed(() => /雨量|rain|rainfall|yl/.test(deviceTypeText.value))

const curveTimes = computed(() => {
  if (isCrackMonitor.value) return currentDayHours
  return curveData.value.map((item, index) => (
    item.date || item.recordTime || item.sendTime || item.warnTime || `${index + 1}`
  ))
})

const curveSeries = computed(() => {
  if (isCrackMonitor.value) {
    const hourlyData = Array(24).fill(null)
    curveData.value.forEach((item, index) => {
      const number = getCurveNumber(item?.now_pull_line_num ?? item?.nowPullLineNum)
      if (number === null) return
      const time = item?.date || item?.recordTime || item?.sendTime || item?.warnTime
      const date = time ? new Date(time) : null
      const hour = date && !Number.isNaN(date.getTime()) ? date.getHours() : index % 24
      hourlyData[hour] = number
    })
    return [{
      name: '拉线位移(毫米)',
      type: 'line',
      smooth: false,
      connectNulls: true,
      showSymbol: false,
      data: hourlyData,
      lineStyle: { width: 2, color: '#a99cff' },
      itemStyle: { color: '#a99cff' }
    }]
  }

  if (isDisplacementMonitor.value) {
    const displacementFields = [
      {
        name: '合位移量(毫米)',
        getValue: item => {
          const x = getCurveNumber(item.shift_x_num ?? item.shiftXNum)
          const y = getCurveNumber(item.shift_y_num ?? item.shiftYNum)
          const z = getCurveNumber(item.shift_z_num ?? item.shiftZNum)
          return x !== null && y !== null && z !== null ? Math.sqrt(x * x + y * y + z * z) : null
        }
      },
      {
        name: 'X方向合位移量(毫米)',
        getValue: item => {
          const x = getCurveNumber(item.shift_x_num ?? item.shiftXNum)
          const y = getCurveNumber(item.shift_y_num ?? item.shiftYNum)
          return x !== null && y !== null ? Math.sqrt(x * x + y * y) : null
        }
      },
      {
        name: 'Y方向合位移量(毫米)',
        getValue: item => {
          const y = getCurveNumber(item.shift_y_num ?? item.shiftYNum)
          const z = getCurveNumber(item.shift_z_num ?? item.shiftZNum)
          return y !== null && z !== null ? Math.sqrt(y * y + z * z) : null
        }
      },
      { name: 'X方向位移量(毫米)', getValue: item => getCurveNumber(item.shift_x_num ?? item.shiftXNum) },
      { name: 'Y方向位移量(毫米)', getValue: item => getCurveNumber(item.shift_y_num ?? item.shiftYNum) },
      { name: 'Z方向位移量(毫米)', getValue: item => getCurveNumber(item.shift_z_num ?? item.shiftZNum) }
    ]
    const colors = ['#a99cff', '#4cafff', '#5cd6c0', '#ffb85c', '#ff7d95', '#7d91ff']
    return displacementFields.map((option, index) => ({
      name: option.name,
      type: 'line',
      smooth: true,
      connectNulls: true,
      showSymbol: false,
      data: curveData.value.map(option.getValue),
      lineStyle: { width: 2, color: colors[index] },
      itemStyle: { color: colors[index] }
    }))
  }

  if (isDeformationMonitor.value) {
    return [{
      name: '应力值(KN)',
      type: 'line',
      smooth: true,
      connectNulls: true,
      showSymbol: false,
      data: curveData.value.map(item => getCurveNumber(item?.yl)),
      lineStyle: { width: 2, color: '#a99cff' },
      itemStyle: { color: '#a99cff' }
    }]
  }

  if (isRainfallMonitor.value) {
    return [{
      name: '雨量强度(mm)',
      type: 'bar',
      barMaxWidth: 18,
      data: curveData.value.map(item => getCurveNumber(item?.rainfall_intensity_num ?? item?.rainfallIntensityNum)),
      itemStyle: { color: '#369dff', borderRadius: [2, 2, 0, 0] }
    }]
  }

  return curveFieldOptions.filter(option => curveData.value.some(item => (
    getCurveNumber(item?.[option.key]) !== null
  ))).map((option, index) => ({
    name: option.label,
    type: 'line',
    smooth: true,
    connectNulls: true,
    showSymbol: false,
    data: curveData.value.map(item => getCurveNumber(item?.[option.key])),
    lineStyle: {
      width: 2,
      color: ['#a99cff', '#43c8ff', '#52d89c', '#ffbf69', '#ff7895'][index % 5]
    }
  }))
})

const trendChartOption = computed(() => ({
  animationDuration: 700,
  grid: { left: 42, right: 18, top: 34, bottom: 28 },
  tooltip: {
    trigger: 'axis',
    backgroundColor: 'rgba(255, 255, 255, .96)',
    borderColor: '#d7e2ee',
    textStyle: { color: '#5c6773', fontSize: 12 },
    axisPointer: { type: 'line', lineStyle: { color: '#aec9de' } }
  },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: curveTimes.value,
    axisLine: { lineStyle: { color: '#bdd6e9' } },
    axisTick: { show: false },
    axisLabel: { color: '#7eb5df', fontSize: 10, hideOverlap: true },
    splitLine: { show: true, lineStyle: { color: '#e2edf5' } }
  },
  yAxis: {
    type: 'value',
    scale: true,
    name: isCrackMonitor.value
      ? '拉线位移(毫米)'
      : isDisplacementMonitor.value
      ? '位移量(毫米)'
      : isDeformationMonitor.value
      ? '应力值(KN)'
      : isRainfallMonitor.value
      ? '雨量强度(mm)'
      : '',
    nameTextStyle: { color: '#3aa5ff', fontSize: 10 },
    axisLine: { show: false },
    axisTick: { show: false },
    axisLabel: { color: '#72afe0', fontSize: 10 },
    splitLine: { lineStyle: { color: '#dceaf4' } }
  },
  legend: {
    top: 0,
    right: 0,
    textStyle: { color: '#54708a', fontSize: 10 }
  },
  series: curveSeries.value
}))

const formatCurveDateTime = date => {
  const pad = number => String(number).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`
}

const requestDeviceCurve = async () => {
  const deviceId = value('devId', 'deviceId', 'deviceCode')
  if (!deviceId) {
    curveData.value = []
    return
  }

  const requestVersion = ++curveRequestVersion
  const now = new Date()
  const startDate = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 0, 0, 0)
  const endDate = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 23, 59, 59)
  curveLoading.value = true
  try {
    const res = await axios.post(
      `${BASE_URL}/api/boot/system/land/landDeviceCurve`,
      {
        deviceId,
        startTime: formatCurveDateTime(startDate),
        endTime: formatCurveDateTime(endDate)
      }
    )
    if (requestVersion !== curveRequestVersion) return
    const data = res?.data?.data ?? res?.data
    curveData.value = Array.isArray(data) ? data : []
  } catch (error) {
    if (requestVersion === curveRequestVersion) curveData.value = []
    console.error('获取设备告警曲线失败:', error)
  } finally {
    if (requestVersion === curveRequestVersion) curveLoading.value = false
  }
}

const getDisposalRecordId = () => value(
  'thirdPartyId',
  'thirdpartyId',
  'thirdId',
  'recordId',
  'id',
  'eventId'
)

const requestDisposalList = async () => {
  const recordId = getDisposalRecordId()
  const requestVersion = ++disposalRequestVersion
  disposalList.value = []

  if (!recordId) {
    disposalLoading.value = false
    return
  }

  disposalLoading.value = true
  try {
    const res = await axios.post(`${BASE_URL}/api/boot/system/land/processNodeList`, { recordId })
    if (requestVersion !== disposalRequestVersion) return
    const data = res?.data?.data ?? res?.data
    const list = Array.isArray(data) ? data : []
    disposalList.value = list.map((item, index) => ({
      key: `${item.recordId || recordId}_${item.processNode || index}_${item.handleTime || index}`,
      time: item.handleTime || '-',
      person: item.handler || '-',
      content: item.processNode || '-',
      result: item.processResult || '-'
    }))
  } catch (error) {
    if (requestVersion === disposalRequestVersion) disposalList.value = []
    console.error('获取设备告警处置情况失败:', error)
  } finally {
    if (requestVersion === disposalRequestVersion) disposalLoading.value = false
  }
}

onMounted(()=>{
  getRiskPointList()
})

watch(() => props.isToday, value => {
  isTodayValue.value = value === '1' ? '1' : '0'
  currentPage.value = 1
  getRiskPointList()
})

</script>

<script>
export default {
  name: 'GeoVideoRiskList'
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

.query-select,
.filter-select {
  width: 180px;
  background: #0d356b;
}

.query-select :deep(.n-base-selection),
.query-select :deep(.n-base-selection-label),
.filter-select :deep(.n-base-selection),
.filter-select :deep(.n-base-selection-label) {
  background: #0d356b;
}

.filter-select :deep(.n-base-selection-input),
.filter-select :deep(.n-base-selection-placeholder),
.filter-select :deep(.n-base-selection-label) {
  color: #cfe8ff;
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

  .detail-btn {
    color: #d7e9ff;
    font-size: 13px;

    &:not(.n-button--disabled):hover {
      color: #5ff6ff;
    }
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

.device-detail-overlay {
  position: absolute;
  inset: 0;
  z-index: 1100;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 11, 28, 0.48);
}

.device-detail-modal {
  width: 960px;
  height: 760px;
  overflow: hidden;
  color: #d8ebff;
  background: #0f3f70;
  box-shadow: 0 14px 42px rgba(0, 0, 0, 0.5);
}

.device-detail-header {
  position: relative;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 14px;
  font-weight: 700;
  background: #123f70;
  border-bottom: 1px solid rgba(45, 139, 224, 0.35);
}

.device-detail-close {
  position: absolute;
  right: 12px;
  top: 5px;
  border: 0;
  color: #fff;
  background: transparent;
  font-size: 34px;
  line-height: 34px;
  cursor: pointer;
}

.device-detail-body {
  height: calc(100% - 44px);
  box-sizing: border-box;
  display: grid;
  grid-template-columns: 1fr 264px;
  gap: 24px;
  padding: 26px 16px 16px;
}

.device-detail-left,
.device-detail-right {
  min-width: 0;
  min-height: 0;
}

.alarm-card {
  position: relative;
  min-height: 198px;
  max-height: 198px;
  box-sizing: border-box;
  padding: 42px 14px 14px;
  overflow: auto;
  background:
    linear-gradient(100deg, rgba(15, 82, 150, 0.72), rgba(5, 48, 94, 0.78)),
    url('../img/Rectangle_346242153.png') center / cover no-repeat;
}

.alarm-card::-webkit-scrollbar,
.disposal-card::-webkit-scrollbar {
  width: 7px;
  height: 7px;
}

.alarm-card::-webkit-scrollbar-track,
.disposal-card::-webkit-scrollbar-track {
  background: rgba(3, 31, 68, 0.28);
  border-radius: 8px;
}

.alarm-card::-webkit-scrollbar-thumb,
.disposal-card::-webkit-scrollbar-thumb {
  border-radius: 8px;
  background: linear-gradient(180deg, rgba(95, 246, 255, 0.78), rgba(29, 139, 224, 0.68));
  box-shadow: inset 0 0 0 1px rgba(188, 236, 255, 0.22);
}

.alarm-card::-webkit-scrollbar-thumb:hover,
.disposal-card::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(180deg, rgba(130, 250, 255, 0.95), rgba(53, 168, 255, 0.88));
}

.alarm-card,
.disposal-card {
  scrollbar-width: thin;
  scrollbar-color: rgba(95, 246, 255, 0.78) rgba(3, 31, 68, 0.28);
}

.section-tab {
  position: absolute;
  left: 14px;
  top: 0;
  width: 116px;
  height: 30px;
  line-height: 30px;
  padding-left: 14px;
  box-sizing: border-box;
  color: #5ff6ff;
  font-size: 14px;
  font-weight: 700;
  background: linear-gradient(120deg, rgba(8, 111, 196, 0.95), rgba(5, 75, 145, 0.95) 76%, transparent 77%);
}

.alarm-message {
  color: #fff;
  font-size: 15px;
  font-weight: 700;
  line-height: 1.55;
  white-space: normal;
  overflow-wrap: anywhere;
  word-break: break-all;
}

.section-title {
  margin: 40px 0 12px;
  color: #fff;
  font-size: 15px;
  font-weight: 700;
}

.chart-card {
  height: 400px;
  background: #fff;
  overflow: hidden;
}

.trend-chart {
  width: 100%;
  height: 100%;
  background: linear-gradient(#fff, #fbfcff);
}

.disposal-title {
  height: 32px;
  display: flex;
  align-items: center;
  color: #fff;
  font-size: 16px;
  font-weight: 700;
}

.disposal-title span {
  margin-right: auto;
}

.disposal-title strong {
  color: #ff1f2f;
  font-size: 15px;
  font-style: normal;
}

.disposal-title em {
  color: #178450;
  font-size: 15px;
  font-style: normal;
}

.disposal-card {
  height: 642px;
  box-sizing: border-box;
  padding: 9px 12px;
  overflow: auto;
  color: #FFF;
  font-size: 10px;
  line-height: 1.7;
  background:
    linear-gradient(100deg, rgba(15, 82, 150, 0.72), rgba(5, 48, 94, 0.78)),
    url('../img/Rectangle_346242153.png') center / cover no-repeat;
}

.disposal-item + .disposal-item {
  margin-top: 7px;
}

.disposal-empty {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: rgba(216, 235, 255, 0.72);
  font-size: 13px;
}

.disposal-time {
  color: #fff;
}

.disposal-item span {
  color: #3486d3;
}
</style>
