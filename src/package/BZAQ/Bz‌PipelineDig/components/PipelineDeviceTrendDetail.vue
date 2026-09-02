<template>
  <div class="device-trend-modal">
    <div class="modal-header">
      <div class="header-title">{{ detailTitle }}</div>
      <button class="close-btn" type="button" @click="$emit('close')">×</button>
    </div>

    <div class="modal-body">
      <section class="detail-section">
        <table class="info-table">
          <thead>
            <tr>
              <th>设备编号</th>
              <th>{{ middleHeader }}</th>
              <th>设备地址</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>{{ deviceCode }}</td>
              <td>{{ middleValue }}</td>
              <td>{{ deviceAddress }}</td>
            </tr>
          </tbody>
        </table>
      </section>

      <section v-for="item in normalizedCharts" :key="item.title" class="detail-section chart-section">
        <v-chart class="detail-chart" :option="getLineOption(item)" autoresize />
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import axios from 'axios'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { LineChart } from 'echarts/charts'
import { GridComponent, LegendComponent, TooltipComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'

use([LineChart, GridComponent, LegendComponent, TooltipComponent, CanvasRenderer])

const props = defineProps({
  point: {
    type: Object,
    default: () => ({})
  },
  popupType: {
    type: String,
    default: ''
  }
})

defineEmits(['close'])

const BASE_URL = 'http://23.99.16.179:11001'
const detailData = ref({})
const pointData = computed(() => ({ ...(props.point || {}), ...detailData.value }))
const type = computed(() => pointData.value.detailType || props.popupType)
const detailId = computed(() => props.point?.id || '')

function normalizeDeviceDetail(data: any) {
  if (!data) return {}
  return {
    ...data,
    label: data.deviceName || data.name,
    deviceType: data.type || data.deviceName,
    deviceAddress: data.installAddress || data.detailAddress || data.address
  }
}

function getDetailApi() {
  if (props.popupType === 'water-station') return '/api/boot/system/pipeline/waterQualityStationDetail'
  if (props.popupType === 'water-use') return '/api/boot/system/pipeline/waterMonitorDetail'
  if (props.popupType === 'outfall') return '/api/boot/system/pipeline/outfallMonitorDetail'
  if (props.popupType === 'medical') return '/api/boot/system/pipeline/medicalWastewaterMonitorDetail'
  if (['water-level', 'manhole', 'pole-tilt'].includes(props.popupType)) return '/api/boot/system/pipeline/lightingDeviceDetail'
  if (props.popupType === 'gas-network') return '/api/boot/system/pipeline/gasMonitorDetail'
  if (props.popupType === 'gas-leak') return '/api/boot/system/pipeline/applianceDetail'
  return ''
}

async function fetchDeviceDetail() {
  const api = getDetailApi()
  if (!api || !detailId.value) {
    detailData.value = {}
    return
  }

  try {
    const res = await axios.post(`${BASE_URL}${api}`, {
      id: detailId.value,
      startTime: '',
      endTime: ''
    })
    detailData.value = normalizeDeviceDetail(res?.data?.data || {})
  } catch (error) {
    console.error('获取管线设备详情失败', error)
    detailData.value = {}
  }
}

watch(() => [props.popupType, detailId.value], fetchDeviceDetail)

onMounted(fetchDeviceDetail)

const baseTimes = [
  '2025-02-21 02:11:53',
  '2025-05-29 18:15:49',
  '2025-06-01 02:13:20',
  '2025-06-03 14:19:59',
  '2025-06-05 22:28:57',
  '2025-06-08 18:26:10',
  '2025-06-11 02:12:51',
  '2025-06-13 10:12:59'
]

const gasTimes = [
  '2025-12-18 00:22:05',
  '2025-12-18 12:33:40',
  '2025-12-19 00:45:18',
  '2025-12-19 12:56:57',
  '2025-12-20 01:08:48',
  '2025-12-20 13:20:27'
]

const waterUseTimes = [
  '2025-01-23 14:53:53',
  '2025-03-30 23:43:16',
  '2025-07-01 00:03:57',
  '2025-09-25 04:33:10',
  '2025-12-05 00:12:25',
  '2026-02-12 01:45:24',
  '2026-04-17 00:21:29',
  '2026-07-03 23:40:59'
]

const detailTitle = computed(() => {
  if (type.value === 'water-level') return '路面监测'
  if (type.value === 'manhole') return '井盖状态监测'
  if (type.value === 'pole-tilt') return '路面监测'
  if (type.value === 'water-use') return '供水监测'
  return '趋势图'
})

const deviceCode = computed(() => {
  if (type.value === 'outfall') return pointData.value.deviceName || '--'
  return pointData.value.deviceId || pointData.value.deviceCode || '--'
})
const middleHeader = computed(() => {
  if (type.value === 'gas-leak') return '街道'
  if (type.value === 'manhole') return '设备名称'
  return '设备类型'
})
const middleValue = computed(() => {
  if (type.value === 'gas-leak') return pointData.value.street || '--'
  if (type.value === 'outfall') return pointData.value.deviceCode || '--'
  if (type.value === 'manhole') return pointData.value.deviceName || '--'
  return pointData.value.deviceType || pointData.value.deviceName || '--'
})
const deviceAddress = computed(() => pointData.value.installPosition || pointData.value.installAddress || pointData.value.deviceAddress || pointData.value.deviceName || '--')

const gasRealtimeChart = computed(() => {
  const list = Array.isArray(pointData.value.realtimeData) ? pointData.value.realtimeData : []
  if (!list.length) {
    return {
      times: gasTimes,
      data: [0, 0, 0, 0, 0, 0]
    }
  }

  const sortedList = [...list].sort((a, b) => {
    const prev = new Date(a.timeStamp || a.createTime || 0).getTime()
    const next = new Date(b.timeStamp || b.createTime || 0).getTime()
    return prev - next
  })

  return {
    times: sortedList.map(item => item.timeStamp || item.createTime || '--'),
    data: sortedList.map(item => Number(item.concentration) || 0)
  }
})

const commonDenseData = [0.24, 0.13, 0.23, 0.04, 0.23, 0.25, 0.12, 0.24]
const chartSections = computed(() => {
  const common = {
    times: baseTimes,
    color: '#00ff34',
    redData: []
  }

  switch (type.value) {
    case 'gas-leak':
      return [{ title: '甲烷浓度（0~0.8%vol）', interval: 0.2, times: gasRealtimeChart.value.times, data: gasRealtimeChart.value.data, color: '#79d2ff' }]
    case 'water-level':
      return [{ title: '路面积水水位',  interval: 0.2, ...common, data: [0, 0, 0, 0, 0, 0, 0, 0] }]
    case 'manhole':
      return [{ title: '井盖打开状态', interval: 0.2, ...common, data: [0, 0, 0, 0, 0, 0, 0, 0] }]
    case 'pole-tilt':
      return [
        { title: 'X轴平均角度',  interval: 0.2, ...common, data: [0, 0, 0, 0, 0, 0, 0, 0], color: '#00ff34' },
        { title: 'Y轴平均角度',  interval: 0.2, ...common, data: [0, 0, 0, 0, 0, 0, 0, 0], color: '#ffcb54' },
        { title: 'Z轴平均角度',  interval: 0.2, ...common, data: [0, 0, 0, 0, 0, 0, 0, 0], color: '#ff5b47', merge: true }
      ]
    case 'gas-network':
      return [
        { title: '甲烷浓度(0-0.8%vol)', interval: 0.05, ...common, data: commonDenseData },
        { title: '温度℃',  interval: 10, ...common, data: [20, 36, 14, 35, 18, 31, 24, 38] },
        { title: '排气泵工作状态', max: 1.2, interval: 0.2, ...common, data: [0, 1, 0, 1, 0, 1, 0, 1] }
      ]
    case 'outfall':
      return [
        { title: '氨氮含量（0~15mg/L）',  interval: 2, ...common, data: [14.2, 2.1, 2.3, 1.4, 1.1, 1.1, 1.1, 1.0] },
        { title: '固体悬浮物浓度（0~70mg/L）', interval: 20, ...common, data: [20, 64, 62, 61, 42, 60, 64, 62] },
        { title: 'PH值(6~9)',  interval: 2, ...common, data: [7.1, 7.1, 7.1, 7.1, 7.1, 7.1, 7.1, 7.1] },
        { title: '化学需氧量COD（0~100mg/L）', interval: 10, ...common, data: [48, 32, 44, 31, 45, 40, 36, 32] }
      ]
    case 'medical':
      return [{ title: '化学需氧量COD(0~250mg/L)', interval: 50, ...common, data: [42, 56, 62, 48, 52, 64, 74, 92], redData: [null, null, 160, null, null, 258, null, null] }]
    case 'water-station':
      return [
        { title: '氨氮浓度（0~0.5mg/L）',  interval: 0.1, ...common, data: [0.28, 0.39, 0.28, 0.01, 0.5, 0, 0.3, 0.38] },
        { title: '总磷浓度（0~0.1mg/L）',  interval: 0.1, ...common, data: [0.04, 0.1, 0.1, 0.08, 0.05, 0.28, 0, 0.02], redData: [null, null, null, null, null, 0.5, null, null] },
        { title: 'PH值（6~9）',  interval: 2, ...common, data: [8.1, 8.0, 8.15, 6.4, 8.2, 8.1, 8.3, 7.9] },
        { title: '高锰酸钾指数（0~4mg/L）', interval: 1, ...common, data: [2.4, 1.2, 0.4, 0.6, 0.8, 0.7, 4.0, 0.9], redData: [null, null, null, null, null, null, 5.4, null] }
      ]
    case 'water-use':
      return [{ title: '累计流量m³', interval: 1000000, times: waterUseTimes, data: [2200000, 2600000, 3200000, 4100000, 5000000, 5300000, 5350000, 5400000], color: '#ffcb54' }]
    default:
      return [{ title: '甲烷浓度（0~0.8%vol）', interval: 0.2, times: gasTimes, data: [0, 0, 0, 0, 0, 0], color: '#79d2ff' }]
  }
})

const normalizedCharts = computed(() => {
  if (type.value !== 'pole-tilt') return chartSections.value

  return [
    {
      ...chartSections.value[0],
      seriesList: chartSections.value.map(item => ({
        name: item.title,
        data: item.data,
        color: item.color
      }))
    }
  ]
})

function getLineOption(item) {
  const seriesList = item.seriesList || [
    {
      name: item.title,
      data: item.data,
      color: item.color || '#00ff34'
    }
  ]

  const series = seriesList.map(seriesItem => ({
    name: seriesItem.name,
    type: 'line',
    data: seriesItem.data,
    symbol: 'none',
    lineStyle: {
      width: 3,
      color: seriesItem.color
    },
    itemStyle: {
      color: seriesItem.color
    }
  }))

  if (item.redData?.length) {
    series.push({
      name: '超标',
      type: 'line',
      data: item.redData,
      symbol: 'none',
      connectNulls: true,
      lineStyle: {
        width: 3,
        color: '#ff2828'
      },
      itemStyle: {
        color: '#ff2828'
      },
      tooltip: {
        show: false
      }
    })
  }

  return {
    animation: false,
    backgroundColor: '#064581',
    color: seriesList.map(item => item.color),
    tooltip: {
      trigger: 'axis',
      confine: true,
      backgroundColor: 'rgba(4, 24, 48, 0.92)',
      borderColor: 'rgba(95, 246, 255, 0.45)',
      textStyle: {
        color: '#e8f8ff',
        fontSize: 12
      }
    },
    legend: {
      top: 12,
      left: 'center',
      itemWidth: 14,
      itemHeight: 3,
      textStyle: {
        color: '#d6ecff',
        fontSize: 13,
        fontWeight: 600
      },
      data: seriesList.map(item => item.name)
    },
    grid: {
      left: 60,
      right: 70,
      top: 48,
      bottom: 34
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: item.times,
      axisLine: {
        lineStyle: {
          color: 'rgba(139, 188, 230, 0.8)'
        }
      },
      axisTick: {
        show: true,
        lineStyle: {
          color: 'rgba(139, 188, 230, 0.55)'
        }
      },
      axisLabel: {
        color: '#f0f8ff',
        fontSize: 14,
        interval: 0,
        margin: 6,
        hideOverlap: true
      }
    },
    yAxis: {
      type: 'value',
      min: 0,
      // max: item.max,
      // interval: item.interval,
      splitLine: {
        lineStyle: {
          color: 'rgba(210, 232, 245, 0.62)',
          type: 'dashed'
        }
      },
      axisLine: {
        show: false
      },
      axisTick: {
        show: false
      },
      axisLabel: {
        color: '#f0f8ff',
        fontSize: 14,
        margin: 8
      }
    },
    series
  }
}
</script>

<script lang="ts">
export default {
  name: 'PipelineDeviceTrendDetail'
}
</script>

<style lang="scss" scoped>
.device-trend-modal {
  position: absolute;
  transform: translate(-50%, -50%);
  z-index: 1002;
  width: 1200px;
  height: 960px;
  display: flex;
  flex-direction: column;
  background: linear-gradient(180deg, rgba(6, 36, 76, 0.98) 0%, rgba(2, 12, 36, 0.98) 100%);
  border: 1px solid rgba(37, 134, 255, 0.25);
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.45);
  color: #d7e9ff;
  overflow: hidden;
  pointer-events: auto;
}

.modal-header {
  height: 48px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  padding: 0 56px;
  background: linear-gradient(90deg, rgba(12, 73, 138, 0.55), rgba(4, 34, 76, 0.2), rgba(12, 73, 138, 0.55));
  border-bottom: 1px solid rgba(83, 174, 255, 0.22);
}

.header-title {
  max-width: 100%;
  color: #cfe8ff;
  font-size: 22px;
  font-weight: 700;
  text-align: center;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
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
}

.modal-body {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  padding: 14px 24px 24px;
  box-sizing: border-box;

  &::-webkit-scrollbar {
    width: 8px;
  }

  &::-webkit-scrollbar-thumb {
    background: rgba(71, 168, 255, 0.48);
    border-radius: 8px;
  }

  &::-webkit-scrollbar-track {
    background: rgba(255, 255, 255, 0.08);
  }
}

.detail-section {
  margin-bottom: 16px;
}

.info-table {
  width: 100%;
  border-collapse: collapse;
  table-layout: fixed;
  background: #064581;
}

.info-table th,
.info-table td {
  color: #f0f8ff;
  text-align: center;
  font-size: 16px;
  font-weight: 700;
}

.info-table th {
  height: 52px;
}

.info-table td {
  height: 92px;
  padding: 0 12px;
  line-height: 1.5;
}

.info-table th:nth-child(1),
.info-table td:nth-child(1) {
  width: 27%;
}

.info-table th:nth-child(2),
.info-table td:nth-child(2) {
  width: 20%;
}

.chart-section {
  height: 238px;
}

.detail-chart {
  width: 100%;
  height: 100%;
}
</style>
