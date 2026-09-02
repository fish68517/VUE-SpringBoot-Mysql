<template>
  <div class="station-water-level-dia">
    <div class="modal-header">
      <div class="header-title">{{ stationName || '站点水位信息' }}</div>
      <button class="close-btn" @click="handleClose">×</button>
    </div>

    <div class="modal-body">
      <template v-if="loading">
        <div class="loading-text">加载中...</div>
      </template>
      <template v-else-if="hasData">
        <!-- 预警信息 -->
        <div class="warning-row">
          <span class="warning-label">预警信息：</span>
          <span class="warning-value" :class="{ 'warning-active': warningInfo }">{{ warningInfo || '无' }}</span>
        </div>

        <!-- 水位信息 -->
        <div class="level-row">
          <div class="level-item">
            <span class="level-label">实时水位</span>
            <span class="level-value">{{ realTimeLevel }}</span>
          </div>
          <div class="level-item">
            <span class="level-label">警戒水位</span>
            <span class="level-value">{{ warningLevel }}</span>
          </div>
          <div class="level-item">
            <span class="level-label">保证水位</span>
            <span class="level-value">{{ guaranteeLevel }}</span>
          </div>
        </div>

        <!-- 历史水位折线图 -->
        <div class="chart-title">历史水位趋势</div>
        <div class="chart-container" ref="chartRef"></div>
      </template>
      <template v-else>
        <div class="loading-text">暂无数据</div>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, nextTick, watch } from 'vue'
import axios from 'axios'

const HYDROLOGY_STATION_URL = 'http://23.210.227.34:23343/yzqzlzx/api/boot/system/common/getHydrologyStationInfo'

const props = defineProps({
  stationName: {
    type: String,
    default: '朝天门'
  }
})

const emit = defineEmits(['close'])

const loading = ref(true)
const hasData = ref(false)
const warningInfo = ref('')
const realTimeLevel = ref('--')
const warningLevel = ref('--')
const guaranteeLevel = ref('--')
const historyList = ref<any[]>([])

const chartRef = ref<HTMLElement | null>(null)
let chartInstance: any = null

const handleClose = () => {
  emit('close')
}

const fetchStationInfo = async (stnm: string) => {
  loading.value = true
  hasData.value = false
  try {
    const res = await axios.post(HYDROLOGY_STATION_URL, { stnm })
    const data = res.data?.data
    if (!data) {
      loading.value = false
      return
    }

    const stationDetail = data.stationDetail || {}
    const list = data.historyList || []
    const latest = list.length > 0 ? list[list.length - 1] : {}

    const rtLevel = latest.z ?? null
    const wLevel = stationDetail.ivhz ?? null
    const gLevel = stationDetail.grz ?? null

    let wInfo = ''
    if (rtLevel != null && gLevel != null && rtLevel >= gLevel) {
      wInfo = '超保证水位预警'
    } else if (rtLevel != null && wLevel != null && rtLevel >= wLevel) {
      wInfo = '超警戒水位预警'
    }

    warningInfo.value = wInfo
    realTimeLevel.value = rtLevel != null ? rtLevel.toFixed(2) : '--'
    warningLevel.value = wLevel != null ? wLevel.toFixed(2) : '--'
    guaranteeLevel.value = gLevel != null ? gLevel.toFixed(2) : '--'
    historyList.value = list

    loading.value = false
    hasData.value = list.length > 0

    if (hasData.value) {
      await nextTick()
      renderChart()
    }
  } catch (error) {
    console.error('获取站点水文信息失败:', error)
    loading.value = false
  }
}

const renderChart = () => {
  if (!chartRef.value || historyList.value.length === 0) return

  const echartsLib = (window as any).echarts
  if (!echartsLib) return

  if (chartInstance) {
    chartInstance.dispose()
  }
  chartInstance = echartsLib.init(chartRef.value)

  const data = historyList.value.map((item: any) => ({
    time: item.tm ? new Date(item.tm).toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }) : '',
    date: item.tm ? new Date(item.tm).toLocaleString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' }) : '',
    value: item.z || 0
  }))

  const values = data.map((d: any) => d.value)
  const dataMin = Math.min(...values)
  const dataMax = Math.max(...values)
  const yMin = dataMin - 10
  const yMax = dataMax + 10

  // 拐点趋势箭头
  const markPointData: any[] = []
  const arrowUp = 'path://M-4,-2 L0,-7 L4,-2 L1.5,-2 L1.5,5 L-1.5,5 L-1.5,-2 Z'
  const arrowDown = 'path://M-4,2 L0,7 L4,2 L1.5,2 L1.5,-5 L-1.5,-5 L-1.5,2 Z'
  for (let i = 1; i < data.length; i++) {
    const prev = data[i - 1].value
    const curr = data[i].value
    if (curr > prev) {
      markPointData.push({
        coord: [data[i].time, curr],
        symbol: arrowUp,
        symbolSize: 12,
        symbolOffset: [0, -14],
        itemStyle: { color: '#F84444' }
      })
    } else if (curr < prev) {
      markPointData.push({
        coord: [data[i].time, curr],
        symbol: arrowDown,
        symbolSize: 12,
        symbolOffset: [0, 14],
        itemStyle: { color: '#00FF88' }
      })
    }
  }

  const option = {
    grid: { left: 48, right: 20, top: 20, bottom: 28 },
    tooltip: {
      trigger: 'axis',
      formatter: (params: any) => {
        const p = Array.isArray(params) ? params[0] : params
        const d = data[p.dataIndex]
        let arrow = ''
        if (p.dataIndex > 0) {
          const prev = data[p.dataIndex - 1].value
          const diff = Math.abs(p.value - prev).toFixed(2)
          if (p.value > prev) arrow = ` <span style="color:#F84444">↑${diff}m</span>`
          else if (p.value < prev) arrow = ` <span style="color:#00FF88">↓${diff}m</span>`
        }
        return `${d.date}<br/>水位：${p.value}m${arrow}`
      }
    },
    xAxis: {
      type: 'category',
      data: data.map((d: any) => d.time),
      axisLine: { lineStyle: { color: '#2B7BBD' } },
      axisLabel: { color: '#8EC9FF', fontSize: 11, interval: 'auto' },
      splitLine: { show: false }
    },
    yAxis: {
      type: 'value',
      name: 'm',
      min: yMin,
      max: yMax,
      nameTextStyle: { color: '#8EC9FF', fontSize: 11 },
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: '#8EC9FF', fontSize: 11 },
      splitLine: { lineStyle: { color: 'rgba(43, 123, 189, 0.3)', type: 'dashed' } }
    },
    series: [{
      data: data.map((d: any) => d.value),
      type: 'line',
      smooth: true,
      symbolSize: 5,
      lineStyle: { color: '#45C8FF', width: 2 },
      itemStyle: { color: '#45C8FF' },
      areaStyle: {
        color: new echartsLib.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(69, 200, 255, 0.3)' },
          { offset: 1, color: 'rgba(69, 200, 255, 0)' }
        ])
      },
      markPoint: {
        data: markPointData,
        label: { show: false }
      }
    }]
  }

  chartInstance.setOption(option, true)
}

// 监听站点名称变化时重新加载
watch(() => props.stationName, (name) => {
  if (name) {
    fetchStationInfo(name)
  }
}, { immediate: false })

onMounted(() => {
  fetchStationInfo(props.stationName)
})

onBeforeUnmount(() => {
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
  }
})
</script>

<script lang="ts">
export default {
  name: 'StationWaterLevelDia',
  version: '1.0.0'
}
</script>

<style lang="scss" scoped>
@font-face {
  font-family: 'AlibabaPuHuiTi';
  src: url('../font/Alibaba_PuHuiTi_2.0_55_Regular_55_Regular.ttf') format('truetype');
  font-weight: normal;
  font-style: normal;
}

.station-water-level-dia {
  font-family: 'AlibabaPuHuiTi', sans-serif;
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  z-index: 1001;
  width: 600px;
  height: 420px;
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
  padding: 16px 24px 24px 24px;
  overflow-y: auto;
  overflow-x: hidden;
  display: flex;
  flex-direction: column;

  &::-webkit-scrollbar {
    display: none;
  }
}

.loading-text {
  text-align: center;
  padding: 40px 0;
  color: rgba(184, 217, 255, 0.6);
  font-size: 14px;
}

.warning-row {
  display: flex;
  align-items: center;
  margin-bottom: 12px;

  .warning-label {
    color: rgba(184, 217, 255, 0.7);
    font-size: 14px;
    flex-shrink: 0;
  }

  .warning-value {
    color: #5FEAFF;
    font-size: 14px;
    font-weight: 600;

    &.warning-active {
      color: #F84444;
      font-weight: 700;
    }
  }
}

.level-row {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 16px;

  .level-item {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 10px 0;
    background: rgba(43, 123, 189, 0.12);
    border: 1px solid rgba(69, 200, 255, 0.15);
    border-radius: 4px;

    .level-label {
      color: rgba(184, 217, 255, 0.7);
      font-size: 12px;
      margin-bottom: 4px;
    }

    .level-value {
      color: #ffffff;
      font-size: 18px;
      font-weight: 700;
      font-family: 'D-DIN', 'AlibabaPuHuiTi', sans-serif;
    }
  }
}

.chart-title {
  color: #8EC9FF;
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 6px;

  &::before {
    content: '▶';
    color: #5FBCFF;
    font-size: 10px;
  }
}

.chart-container {
  width: 100%;
  flex: 1;
  min-height: 180px;
}
</style>
