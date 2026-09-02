<template>
  <div class="waterlog-detail-modal">
    <div class="modal-header">
      <div class="header-title">道路风险点详情</div>
      <button class="close-btn" @click="handleClose">×</button>
    </div>

    <div class="modal-body">
      <!-- 基础信息板块 -->
      <div class="base-info-section">
        <div class="section-title">基础信息</div>

        <!-- 风险点名称高亮栏 -->
        <div class="risk-name-bar">
          <span class="risk-name-text">{{ getName() }}</span>
          <button class="monitor-btn" @click="handleOpenMonitor">查看监控</button>
        </div>

        <div class="info-grid">
          <div class="info-row">
            <div class="info-item">
              <span class="label">风险等级：</span>
              <span class="value">{{ getPointLevel() }}</span>
            </div>
            <div class="info-item">
              <span class="label">所属分区：</span>
              <span class="value">{{ getCounty() }}</span>
            </div>
            <div class="info-item">
              <span class="label">风险点类型：</span>
              <span class="value">{{ getRiskName() }}</span>
            </div>
          </div>

          <div class="info-row">
            <div class="info-item">
              <span class="label">阈值(mm/2h)：</span>
              <span class="value">{{ riskData?.threshold ?? '-' }}</span>
            </div>
            <div class="info-item">
              <span class="label">所属区县：</span>
              <span class="value">{{ getCounty() }}</span>
            </div>
            <div class="info-item">
              <span class="label">场景类型：</span>
              <span class="value">{{ riskData?.pointRiskType || '-' }}</span>
            </div>
          </div>

          <div class="info-row">
            <div class="info-item">
              <span class="label">行业主管部门：</span>
              <span class="value">{{ riskData?.unit || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="label">所属街道：</span>
              <span class="value">{{ riskData?.town || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="label">场景子类型：</span>
              <span class="value">{{ riskData?.pointRiskType || '-' }}</span>
            </div>
          </div>

          <div class="info-row">
            <div class="info-item">
              <span class="label">所属社区：</span>
              <span class="value">{{ getCommunity() }}</span>
            </div>
          </div>
        </div>

        <!-- 负责人卡片区域 -->
        <div class="person-card-section">
          <div v-for="card in contactCards" :key="card.title" class="person-card">
            <div class="card-title">{{ card.title }}</div>
            <div class="card-content">
              <div class="person-name">{{ card.name }}</div>
              <div class="person-phone">
                <span class="phone-text">{{ card.phone }}</span>
                <img class="phone-icon" src="../img/Frame-1.png" alt="" />
              </div>
            </div>
          </div>
        </div>

        <!-- 定级原因 -->
        <div class="reason-section">
          <div class="reason-item">
            <span class="reason-label">风险点等级定级原因：</span>
            <span class="reason-text">{{ riskData?.gradeReason || '-' }}</span>
          </div>
        </div>
      </div>

      <!-- 图表区域 -->
      <div class="chart-section-wrapper">
        <div class="chart-section">
          <div class="section-title">降雨量分析图</div>
          <div class="chart-content" ref="rainfallChart"></div>
        </div>
        <div class="chart-section">
          <div class="section-title">水位分析图</div>
          <div class="chart-content" ref="waterLevelChart"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, nextTick } from 'vue'
import axios from 'axios'

const props = defineProps({
  id: {
    type: String,
    default: ''
  },
  name: {
    type: String,
    default: ''
  },
  bus: {
    type: Object,
    default: null
  }
})

const sourceName = 'BzWaterKpiDia'

const eventBus = {
  emit(event, data) {
    if (!props.bus) return
    props.bus.emit(event, { data, source: sourceName })
  }
}

const emit = defineEmits(['close'])
const echarts = window.echarts

const WATERLOG_BASE_URL = 'http://23.99.16.179:11001/api/boot/system/waterlog'

// 接口返回的风险点位数据
const riskData = ref(null)
const loading = ref(false)

// 调用接口2：获取风险点位及关联摄像头信息
const fetchRiskDetail = async () => {
  loading.value = true
  try {
    const res = await axios.get(`${WATERLOG_BASE_URL}/risk/carema`, {
      params: { pointId: props.id }
    })
    const data = res?.data?.data
    if (data) {
      riskData.value = Array.isArray(data) ? data[0] : data
    }
  } catch (error) {
    console.error('获取风险点位详情失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取展示值
const getName = () => riskData.value?.name || props.name || '-'
const getCounty = () => riskData.value?.county || '-'
const getCommunity = () => riskData.value?.community || '-'
const getRiskName = () => riskData.value?.riskName || '-'
const getPointLevel = () => riskData.value?.pointLevel || '-'
const parseResponsible = (raw) => {
  if (!raw) return { name: '-', phone: '-' }
  try {
    const obj = JSON.parse(raw)
    return { name: obj.dutyPersonName || '-', phone: obj.dutyPersonPhone || '-' }
  } catch (e) {
    return { name: '-', phone: '-' }
  }
}

const contactCards = computed(() => {
  const d = riskData.value || {}
  const cspsflaq = parseResponsible(d.cspsflaqResponsible)
  const csnlzl = parseResponsible(d.csnlzlResponsible)
  const sdzd = parseResponsible(d.sdzdResponsible)
  const ghdw = parseResponsible(d.ghdwResponsible)
  return [
    { title: '市级负责人', name: cspsflaq.name, phone: cspsflaq.phone },
    { title: '区级负责人', name: csnlzl.name, phone: csnlzl.phone },
    { title: '镇街负责人', name: sdzd.name, phone: sdzd.phone },
    { title: '管护负责人', name: ghdw.name, phone: ghdw.phone }
  ]
})

// 查看监控：从 devicesInfo 中提取 cameraId，触发事件总线打开视频弹窗
const handleOpenMonitor = () => {
  const raw = riskData.value?.devicesInfo
  if (!raw) return
  try {
    const devices = typeof raw === 'string' ? JSON.parse(raw) : raw
    if (!Array.isArray(devices)) return
    const cameraIds = devices.map(d => d.code).filter(Boolean)
    if (cameraIds.length > 0) {
      console.log('查看监控-传递的cameraIds:', cameraIds)
      eventBus.emit('YZVideoPop:openPop', cameraIds)
    }
  } catch (e) {
    console.error('解析devicesInfo失败:', e)
  }
}

let rainfallChartInstance = null
let waterLevelChartInstance = null

const rainfallChart = ref(null)
const waterLevelChart = ref(null)

// 降雨量模拟数据（6条）
const rainfallData = ref([
  { time: '11:22', value: 25 },
  { time: '11:32', value: 35 },
  { time: '11:42', value: 33 },
  { time: '11:52', value: 30 },
  { time: '12:02', value: 15 },
  { time: '12:12', value: 42 }
])

// 水位模拟数据（4条）
const waterLevelData = ref([
  { time: '11:22', value: 12 },
  { time: '11:42', value: 16 },
  { time: '12:02', value: 20 },
  { time: '12:22', value: 25 }
])

// 获取风险点关联降雨量数据
const fetchRainfallData = async () => {
  try {
    const res = await axios.get(`${WATERLOG_BASE_URL}/risk/point/rain`, {
      params: { pointId: props.id }
    })
    const list = res?.data?.data
    if (Array.isArray(list) && list.length > 0) {
      rainfallData.value = list.map(item => ({
        time: item.create_time ? item.create_time.substring(11, 16) : '',
        value: Number(item.rian) || 0
      }))
      updateRainfallChart()
    }
    // 接口无数据或调用失败则保持静态数据
  } catch (error) {
    console.error('获取降雨量数据失败:', error)
  }
}

/**
 * 初始化降雨量图表
 */
function initRainfallChart() {
  if (!rainfallChart.value || !echarts) return
  if (rainfallChartInstance) {
    rainfallChartInstance.dispose()
  }
  rainfallChartInstance = echarts.init(rainfallChart.value)
  updateRainfallChart()
}

/**
 * 更新降雨量图表
 */
function updateRainfallChart() {
  if (!rainfallChartInstance) return

  const xAxisData = rainfallData.value.map(item => item.time)
  const seriesData = rainfallData.value.map(item => item.value)

  const option = {
    grid: {
      left: 40,
      right: 10,
      top: 10,
      bottom: 20
    },
    xAxis: {
      type: 'category',
      data: xAxisData,
      axisLine: {
        lineStyle: { color: '#2B7BBD' }
      },
      axisLabel: {
        color: '#8EC9FF',
        fontSize: 10,
        interval: 0
      },
      splitLine: { show: false }
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: {
        color: '#8EC9FF',
        fontSize: 10
      },
      splitLine: {
        lineStyle: {
          color: 'rgba(43, 123, 189, 0.3)',
          type: 'dashed'
        }
      }
    },
    series: [
      {
        data: seriesData,
        type: 'bar',
        barWidth: 12,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#45C8FF' },
            { offset: 1, color: '#025899' }
          ]),
          borderRadius: [2, 2, 0, 0]
        }
      }
    ]
  }

  rainfallChartInstance.setOption(option, true)
}

/**
 * 初始化水位图表
 */
function initWaterLevelChart() {
  if (!waterLevelChart.value || !echarts) return
  if (waterLevelChartInstance) {
    waterLevelChartInstance.dispose()
  }
  waterLevelChartInstance = echarts.init(waterLevelChart.value)
  updateWaterLevelChart()
}

/**
 * 更新水位图表
 */
function updateWaterLevelChart() {
  if (!waterLevelChartInstance) return

  const xAxisData = waterLevelData.value.map(item => item.time)
  const seriesData = waterLevelData.value.map(item => item.value)

  const option = {
    grid: {
      left: 40,
      right: 10,
      top: 10,
      bottom: 20
    },
    xAxis: {
      type: 'category',
      data: xAxisData,
      axisLine: {
        lineStyle: { color: '#2B7BBD' }
      },
      axisLabel: {
        color: '#8EC9FF',
        fontSize: 10,
        interval: 0
      },
      splitLine: { show: false }
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: {
        color: '#8EC9FF',
        fontSize: 10
      },
      splitLine: {
        lineStyle: {
          color: 'rgba(43, 123, 189, 0.3)',
          type: 'dashed'
        }
      }
    },
    series: [
      {
        data: seriesData,
        type: 'bar',
        barWidth: 20,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#45C8FF' },
            { offset: 1, color: '#025899' }
          ]),
          borderRadius: [2, 2, 0, 0]
        }
      }
    ]
  }

  waterLevelChartInstance.setOption(option, true)
}

/**
 * 自适应
 */
function handleResize() {
  rainfallChartInstance?.resize()
  waterLevelChartInstance?.resize()
}

function handleClose() {
  rainfallChartInstance?.dispose()
  rainfallChartInstance = null
  waterLevelChartInstance?.dispose()
  waterLevelChartInstance = null
  emit('close')
}

onMounted(() => {
  fetchRiskDetail()
  fetchRainfallData()
  nextTick(() => {
    initRainfallChart()
    initWaterLevelChart()
    window.addEventListener('resize', handleResize)
  })
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  rainfallChartInstance?.dispose()
  rainfallChartInstance = null
  waterLevelChartInstance?.dispose()
  waterLevelChartInstance = null
})
</script>

<script>
export default {
  name: "WaterlogRiskDetail"
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

.waterlog-detail-modal {
  font-family: 'AlibabaPuHuiTi', sans-serif;
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  z-index: 1001;
  width: 904px;
  height: 700px;
  display: flex;
  flex-direction: column;
  background:
    url('../img/Group-2136640490-1.png') top center / 100% 60px no-repeat,
    url('../img/Rectangle-346242153.png') center / 100% 100% no-repeat;
  border: 1px solid rgba(37, 134, 255, 0.25);
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.45);
  color: #b8d9ff;
  font-size: 14px;
  overflow: hidden;
  pointer-events: auto;
}

.modal-header {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  flex-shrink: 0;

  .header-title {
    background: linear-gradient(180deg, #FFFFFF 0%, #5FBCFF 100%);
    -webkit-background-clip: text;
    background-clip: text;
    color: transparent;
    font-size: 22px;
    font-weight: 700;
    text-shadow: 0 0 12px rgba(95, 188, 255, 0.3);
  }

  .close-btn {
    position: absolute;
    right: 20px;
    top: 50%;
    transform: translateY(-50%);
    width: 32px;
    height: 32px;
    border: 1px solid rgba(69, 200, 255, 0.3);
    background: rgba(16, 64, 126, 0.4);
    color: #7AA7CF;
    font-size: 20px;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;

    &:hover {
      color: #ffffff;
      border-color: rgba(69, 200, 255, 0.6);
      background: rgba(24, 92, 179, 0.6);
    }
  }
}

.modal-body {
  flex: 1;
  padding: 20px 32px 32px 32px;
  overflow-y: auto;
  overflow-x: hidden;

  &::-webkit-scrollbar {
    display: none;
  }
}

/* 基础信息板块 */
.base-info-section {
  width: 840px;
  height: 447px;
  background: url('../img/xxbg.png') center / 100% 100% no-repeat;
  border: 1px solid rgba(4, 188, 250, 0.2);
  padding: 0;

  .section-title {
    height: 30px;
    line-height: 30px;
    font-size: 16px;
    color: #8EC9FF;
    font-weight: 500;
    padding-left: 20px;
    background: url('../img/titleBg.png') left center / cover no-repeat;
  }

  /* 风险点名称高亮栏 */
  .risk-name-bar {
    height: 32px;
    line-height: 32px;
    margin: 12px 20px 16px 20px;
    padding-left: 8px;
    background: url('../img/addressBg.png') left center / contain no-repeat;
    position: relative;
    display: flex;
    align-items: center;

    .risk-name-text {
      color: #d2ecff;
      font-size: 16px;
      font-weight: 500;
    }

    .monitor-btn {
      margin-left: auto;
      padding: 0 12px;
      height: 24px;
      line-height: 24px;
      border: 1px solid rgba(69, 200, 255, 0.4);
      background: rgba(16, 64, 126, 0.6);
      color: #8EC9FF;
      font-size: 12px;
      cursor: pointer;
      flex-shrink: 0;

      &:hover {
        color: #fff;
        border-color: rgba(69, 200, 255, 0.7);
        background: rgba(24, 92, 179, 0.7);
      }
    }
  }

  .info-grid {
    padding: 0 20px 0 20px;

    .info-row {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      gap: 8px;
      margin-bottom: 10px;

      .info-item {
        line-height: 28px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;

        .label {
          color: #8EC9FF;
        }

        .value {
          color: #CFE8FF;
          font-family: 'D-DIN', 'AlibabaPuHuiTi', sans-serif;
        }
      }
    }

    .info-row:last-child {
      grid-template-columns: repeat(1, 1fr);
    }
  }

  /* 负责人卡片区域 */
  .person-card-section {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 12px;
    padding: 12px 20px 12px 20px;

    .person-card {
      border: 1px solid rgba(4, 188, 250, 0.3);
      background: rgba(0, 42, 82, 0.4);

      .card-title {
          height: 41px;
          line-height: 41px;
          text-align: center;
          color: #d2ecff;
          font-size: 15px;
          font-weight: 500;
          background: url('../img/Rectangle-346242169.png') left center / cover no-repeat;
          border-bottom: 1px solid rgba(4, 188, 250, 0.2);
        }

      .card-content {
        padding: 10px;
        text-align: center;

        .person-name {
          color: #b8e6ff;
          font-size: 16px;
          font-weight: 500;
          margin-bottom: 8px;
        }

        .person-phone {
          display: flex;
          align-items: center;
          justify-content: center;
          gap: 6px;

          .phone-text {
            color: #3de68c;
            font-family: 'D-DIN', sans-serif;
            font-size: 14px;
          }

          .phone-icon {
            width: 16px;
            height: 16px;
            vertical-align: middle;
            filter: brightness(1.2);
          }
        }
      }
    }
  }

  /* 定级原因 */
  .reason-section {
    margin: 0 20px 16px 20px;
    padding: 0 12px;
    background: linear-gradient(90deg, rgba(4, 188, 250, 0.1) 0%, rgba(4, 84, 203, 0.05) 100%);
    border-left: 4px solid #04bcfa;

    .reason-item {
      line-height: 36px;
      display: flex;

      .reason-label {
        flex-shrink: 0;
        color: #8EC9FF;
      }

      .reason-text {
        flex: 1;
        color: #87f0ff;
        font-weight: 500;
      }
    }
  }
}

/* 图表区域 */
.chart-section-wrapper {
  display: flex;
  gap: 16px;
  margin-top: 16px;

  .chart-section {
    width: 412px;
    height: 125px;
    background: url('../img/xxbg.png') center / 100% 100% no-repeat;
    border: 1px solid rgba(4, 188, 250, 0.2);
    padding: 0;

    .section-title {
      height: 30px;
      line-height: 30px;
      font-size: 16px;
      color: #8EC9FF;
      font-weight: 500;
      padding-left: 20px;
      background: url('../img/titleBg.png') left center / cover no-repeat;
    }

    .chart-content {
      height: calc(100% - 30px);
      width: 100%;
    }
  }
}
</style>
