<template>
  <div class="bz-pipeline" :class="{ 'bz-pipeline--no-bg': !showBackground }" :style="containerStyle">
    <!-- <div class="pipeline-toolbar">
      <div class="diaodu" @click="openDispatchIframe">调度</div>
    </div> -->

    <section class="pipeline-section pipeline-section--cover">
      <aside class="side-title">
        <span>监</span><span>测</span><span>覆</span><span>盖</span>
      </aside>
      <div class="cover-grid">
        <div
          v-for="item in coverItems"
          :key="item.label"
          class="cover-card"
        >
          <div class="cover-card__label">{{ item.label }}</div>
          <div class="cover-card__value">
            <strong>{{ item.value }}</strong><span>{{ item.unit }}</span>
          </div>
        </div>
      </div>
    </section>

    <section class="pipeline-section pipeline-section--warn">
      <aside class="side-title">
        <span>重</span><span>点</span><span>监</span><span>测</span>
      </aside>
      <div class="warn-content">
        <div class="risk-grid">
          <div
            v-for="(item, index) in riskItems"
            :key="item.label"
            class="risk-badge"
            :class="{ 'risk-badge--active': index === activeLineChartIndex }"
            @click="switchLineChart(index)"
          >
            <div class="risk-badge__top">
              <span>{{ item.label }}</span>
              <em @click.stop="openRiskList(item, false)">{{ item.done }}{{ item.total }}</em>
            </div>
            <div class="risk-badge__bottom">
              <span>预警数</span>
              <strong @click.stop="openRiskList(item, true)">{{ item.warnCount }}</strong><em>个</em>
            </div>
          </div>
        </div>

        <div class="line-panel" @click="openCurrentLineList">
          <v-chart
            :key="currentLineChart.key"
            class="line-chart"
            :option="lineChartOption"
            autoresize
          />
        </div>

        <div class="water-monitor">
          <div class="water-monitor__head">
            <div class="water-monitor__title" @click="openWaterStationList">
              <span>水质监测站</span><strong>{{ waterStationTotal }}</strong><em>个</em>
            </div>
            <div class="water-monitor__warn" @click="openWaterStationList">
              <span>预警数</span><strong>{{ waterStationWarnCount }}</strong><em>个</em>
            </div>
          </div>
          <div class="radar-grid">
            <div class="radar-track" :style="radarTrackStyle">
              <div
                v-for="(item, index) in visibleRadarItems"
                :key="`${item.value}-${index}`"
                class="radar-card"
              >
                <div class="radar-card__title">{{ item.label }}</div>
                <v-chart class="radar-chart" :option="getRadarOption(item)" autoresize />
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { LineChart, RadarChart } from 'echarts/charts'
import { GridComponent, LegendComponent, RadarComponent, TooltipComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import axios from 'axios'

use([LineChart, RadarChart, GridComponent, LegendComponent, RadarComponent, TooltipComponent, CanvasRenderer])

const BASE_URL = 'http://23.99.16.179:11001'

const props = defineProps({
  chartConfig: {
    type: Object,
    required: true
  },
  publicParamList: {
    type: Object,
    required: false,
    default: () => []
  },
  bus: {
    type: Object,
    required: false
  }
})

const sourceName = 'Bz‌Pipeline'
const eventBus = {
  emit(event: string, data: any) {
    if (!props.bus) return
    props.bus.emit(event, { data, source: sourceName })
  }
}

const PIPELINE_DEVICE_SELECT_EVENT = 'BzPipelineDevice:selectLayers'
const DEFAULT_DISPATCH_URL = ''

const coverItems = [
  { label: '燃气管线', value: 536.62, unit: 'KM', warn: true },
  { label: '供水管线', value: 340.01, unit: 'KM' },
  { label: '排水管线', value: 877.19, unit: 'KM' },
  { label: '电力管线', value: 11.84, unit: 'KM' },
  { label: '通信管线', value: 369.35, unit: 'KM' },
  { label: '广播电视管线', value: 141.67, unit: 'KM' },
  { label: '照明管线', value: 313.93, unit: 'KM' },
  { label: '交通信号管线', value: 133.28, unit: 'KM' }
]

const coverageStats = ref({
  gasDeviceCount: 45,
  septicTankMonitorCount: 45,
  medicalWastewaterMonitorCount: 45,
  riverOutletMonitorCount: 45,
  waterQualityStationCount: 45
})

const warningStats = ref({
  gasLeakTodayCount: 5,
  gasLeakCurrentCount: 0,
  septicTankTodayCount: 5,
  septicTankCurrentCount: 0,
  medicalWastewaterTodayCount: 5,
  medicalWastewaterCurrentCount: 0,
  riverOutletTodayCount: 5,
  riverOutletCurrentCount: 0,
  waterQualityStationTodayCount: 0,
  waterQualityStationCurrentCount: 0
})

const riskItems = computed(() => [
  {
    label: '燃气泄漏',
    done: coverageStats.value.gasDeviceCount,
    total: '个',
    // warnCount: 0,
    warnCount: warningStats.value.gasLeakTodayCount,
    layerKey: 'gas',
    diaName: 'pipeline-gas-leak-list',
    listType: 'gas'
  },
  {
    label: '化粪池',
    done: coverageStats.value.septicTankMonitorCount,
    total: '个',
    // warnCount: warningStats.value.septicTankTodayCount,
    warnCount: 0,
    layerKey: 'septic-tank',
    diaName: 'pipeline-septic-tank-list',
    listType: 'septic'
  },
  {
    label: '医疗污水',
    done: coverageStats.value.medicalWastewaterMonitorCount,
    total: '个',
    warnCount: warningStats.value.medicalWastewaterTodayCount,
    layerKey: 'medical',
    diaName: 'pipeline-medical-sewage-list',
    listType: 'medical'
  },
  {
    label: '排江口水质',
    done: coverageStats.value.riverOutletMonitorCount,
    total: '个',
    warnCount: warningStats.value.riverOutletTodayCount,
    layerKey: 'outfall',
    diaName: 'pipeline-outfall-water-list',
    listType: 'outfall'
  }
])

const waterStationTotal = computed(() => coverageStats.value.waterQualityStationCount)
const waterStationWarnCount = computed(() => warningStats.value.waterQualityStationTodayCount || warningStats.value.waterQualityStationCurrentCount || 0)

const radarItems = [
  {
    label: '地表水小型自动监测站',
    value: 'YZQ-SZ-002',
    deviceCode: 'YZQ-SZ-002',
    deviceType: '地表水小型自动监测站（嘉陵江流域朝天门站）',
    deviceAddress: '重庆市渝中区朝天门街道朝千路社区朝天门6号码头',
    radarValue: [38, 86, 72, 78, 76]
  },
  {
    label: '地表水小型自动监测站',
    value: 'YZQ-SZ-003',
    deviceCode: 'YZQ-SZ-003',
    deviceType: '地表水小型自动监测站（嘉陵江洪崖洞站）',
    deviceAddress: '重庆市渝中区解放碑街道嘉陵江滨江路洪崖洞段',
    radarValue: [42, 82, 68, 74, 70]
  },
  {
    label: '地表水小型自动监测站',
    value: 'YZQ-SZ-004',
    deviceCode: 'YZQ-SZ-004',
    deviceType: '地表水小型自动监测站（嘉陵江化龙桥站）',
    deviceAddress: '重庆市渝中区化龙桥街道嘉陵江滨江路化龙桥段',
    radarValue: [50, 76, 62, 72, 84]
  },
  {
    label: '地表水小型自动监测站',
    value: 'YZQ-SZ-005',
    deviceCode: 'YZQ-SZ-005',
    deviceType: '地表水小型自动监测站（长江流域黄沙溪站）',
    deviceAddress: '重庆市渝中区菜园坝街道平安街社区重庆市环卫三所',
    radarValue: [44, 80, 74, 66, 78]
  }
]

const radarPage = ref(0)
const radarTransitionEnabled = ref(true)
let radarTimer: ReturnType<typeof window.setInterval> | null = null
let radarResetTimer: ReturnType<typeof window.setTimeout> | null = null
const radarCardWidth = 202
const radarCardGap = 7
const visibleRadarItems = computed(() => {
  return [
    ...radarItems,
    ...radarItems
  ]
})
const radarTrackStyle = computed(() => {
  return {
    transform: `translateX(-${radarPage.value * (radarCardWidth + radarCardGap)}px)`,
    transition: radarTransitionEnabled.value ? 'transform 0.65s ease-in-out' : 'none'
  }
})

const chartTimes = [
  '04:00',
  '05:00',
  '06:00',
  '07:00',
  '08:00',
  '09:00',
  '10:00',
  '11:00'
]

const lineCharts = [
  {
    title: '甲烷浓度值',
    key: 'gas-leak',
    diaName: 'pipeline-gas-leak-list',
    listType: 'gas',
    layerKey: 'gas',
    xData: chartTimes,
    data: [52, 68, 76, 64, 36, 88, 82, 84]
  },
  {
    title: '化粪池',
    key: 'septic-tank',
    diaName: 'pipeline-septic-tank-list',
    listType: 'septic',
    layerKey: 'septic-tank',
    xData: chartTimes,
    data: [45, 58, 72, 54, 40, 79, 70, 88]
  },
  {
    title: '医疗污水',
    key: 'medical-sewage',
    diaName: 'pipeline-medical-sewage-list',
    listType: 'medical',
    layerKey: 'medical',
    xData: chartTimes,
    data: [48, 66, 70, 60, 42, 85, 76, 91]
  },
  {
    title: '排江口水质',
    key: 'outfall-water',
    diaName: 'pipeline-outfall-water-list',
    listType: 'outfall',
    layerKey: 'outfall',
    xData: chartTimes,
    data: [50, 62, 80, 56, 38, 74, 83, 87]
  }
]

const activeLineChartIndex = ref(0)
const gasWarningCharts = ref<any[]>([])
const gasWarningChartIndex = ref(0)
let gasWarningChartTimer: ReturnType<typeof window.setInterval> | null = null

const currentLineChart = computed(() => {
  if (activeLineChartIndex.value === 0 && gasWarningCharts.value.length) {
    return gasWarningCharts.value[gasWarningChartIndex.value % gasWarningCharts.value.length]
  }
  return lineCharts[activeLineChartIndex.value]
})

const currentLineChartMax = computed(() => {
  const values = (currentLineChart.value?.data || [])
    .map((item: any) => Number(item))
    .filter(Number.isFinite)
  if (!values.length) return 100
  const maxValue = Math.max(...values)
  if (maxValue <= 1) return 1
  return Math.ceil(maxValue * 1.2)
})

const showThreeXAxisTicks = (index: number, xData: any[]) => {
  const length = xData.length
  if (length <= 3) return true
  const middleIndex = Math.floor((length - 1) / 2)
  return index === 0 || index === middleIndex || index === length - 1
}

const lineChartOption = computed(() => {
  const chart = currentLineChart.value
  return {
    animation: false,
    color: ['#088BFF'],
    tooltip: {
      trigger: 'axis',
      confine: true,
      backgroundColor: 'rgba(4, 24, 48, 0.92)',
      borderColor: 'rgba(95, 246, 255, 0.45)',
      textStyle: {
        color: '#e8f8ff',
        fontSize: 10
      }
    },
    grid: {
      left: 34,
      right: 8,
      top: 6,
      bottom: 20,
      containLabel: false
    },
    xAxis: {
      type: 'category',
      boundaryGap: true,
      data: chart.xData,
      axisLine: {
        lineStyle: {
          color: 'rgba(139, 188, 230, 0.72)'
        }
      },
      axisTick: {
        show: true,
        alignWithLabel: true,
        lineStyle: {
          color: 'rgba(139, 188, 230, 0.55)'
        }
      },
      axisLabel: {
        color: '#c8eaff',
        fontSize: 14,
        margin: 5,
        hideOverlap: true
      }
    },
    yAxis: {
      type: 'value',
      min: 0,
      max: currentLineChartMax.value,
      splitNumber: 2,
      interval: currentLineChartMax.value / 2,
      splitLine: {
        lineStyle: {
          color: 'rgba(185, 215, 235, 0.48)',
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
        color: '#c8eaff',
        fontSize: 12,
        margin: 4
      }
    },
    series: [
      {
        name: chart.title,
        type: 'line',
        data: chart.data,
        symbol: 'none',
        smooth: 0.85,
        lineStyle: {
          width: 2,
          color: '#088BFF'
        },
        itemStyle: {
          color: '#088BFF',
          borderColor: '#d9f7ff',
          borderWidth: 1
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(8, 139, 255, 0.35)' },
              { offset: 1, color: 'rgba(8, 139, 255, 0)' }
            ]
          }
        }
      }
    ]
  }
})

function getRadarOption(item: any) {
  return {
    animation: false,
    color: ['#62a8ff'],
    tooltip: {
      show: false
    },
    radar: {
      center: ['50%', '50%'],
      radius: 38,
      startAngle: 90,
      splitNumber: 5,
      shape: 'polygon',
      nameGap: 5,
      axisName: {
        color: 'rgba(172, 207, 238, 0.72)',
        fontSize: 10,
        lineHeight: 12
      },
      indicator: [
        { name: 'pH值', max: 100 },
        { name: '溶解氧\n浓度', max: 100 },
        { name: '氨氮浓度', max: 100 },
        { name: '总磷浓度', max: 100 },
        { name: '高锰酸\n盐指数', max: 100 }
      ],
      axisLine: {
        lineStyle: {
          color: 'rgba(75, 140, 210, 0.72)'
        }
      },
      splitLine: {
        lineStyle: {
          color: 'rgba(75, 140, 210, 0.72)'
        }
      },
      splitArea: {
        show: false
      }
    },
    series: [
      {
        type: 'radar',
        symbol: 'circle',
        symbolSize: 3,
        lineStyle: {
          width: 2,
          color: '#62a8ff'
        },
        itemStyle: {
          color: '#77b7ff',
          borderColor: '#d9f7ff',
          borderWidth: 1
        },
        areaStyle: {
          color: 'rgba(52, 137, 255, 0.24)'
        },
        data: [
          {
            value: item.radarValue
          }
        ]
      }
    ]
  }
}

const containerStyle = computed(() => {
  const width = Number(props.chartConfig?.attr?.w)
  const height = Number(props.chartConfig?.attr?.h)
  return {
    width: Number.isFinite(width) && width > 0 ? `${width}px` : '540px',
    height: Number.isFinite(height) && height > 0 ? `${height}px` : '440px'
  }
})

const showBackground = computed(() => props.chartConfig?.option?.showBackground !== 'hide')

function toNumber(value: any, fallback = 0) {
  const numberValue = Number(value)
  return Number.isFinite(numberValue) ? numberValue : fallback
}

function getPageContent(data: any) {
  return data?.content || data?.records || data?.list || data?.rows || data?.data || []
}

function formatGasChartTime(value: any) {
  const text = String(value || '')
  if (!text) return ''
  const date = new Date(text.replace(/-/g, '/'))
  if (!Number.isNaN(date.getTime())) {
    const hour = String(date.getHours()).padStart(2, '0')
    const minute = String(date.getMinutes()).padStart(2, '0')
    return `${hour}:${minute}`
  }
  return text.length > 16 ? text.slice(11, 16) : text
}

function normalizeGasWarningChart(detail: any, fallback: any) {
  const realtimeData = Array.isArray(detail?.realtimeData) ? detail.realtimeData : []
  const rows = realtimeData
    .map((item: any) => ({
      time: item?.timeStamp || item?.createTime || '',
      value: Number(item?.concentration)
    }))
    .filter(item => item.time && Number.isFinite(item.value))
    .sort((a, b) => new Date(a.time).getTime() - new Date(b.time).getTime())

  if (!rows.length) return null

  const title = '甲烷浓度值'
  return {
    title,
    key: `gas-warning-${detail?.id || fallback?.id || fallback?.deviceId || title}`,
    diaName: 'pipeline-gas-leak-list',
    listType: 'gas',
    layerKey: 'gas',
    xData: rows.map(item => formatGasChartTime(item.time)),
    data: rows.map(item => item.value)
  }
}

function resetGasWarningChartTimer() {
  if (gasWarningChartTimer) {
    window.clearInterval(gasWarningChartTimer)
    gasWarningChartTimer = null
  }
  if (gasWarningCharts.value.length <= 1) return
  gasWarningChartTimer = window.setInterval(() => {
    gasWarningChartIndex.value = (gasWarningChartIndex.value + 1) % gasWarningCharts.value.length
  }, 5000)
}

async function fetchGasWarningCharts() {
  try {
    const listRes = await axios.post(`${BASE_URL}/api/boot/system/pipeline/appliance`, {
      index: 1,
      size: 50,
      isWarning: true,
      warningType: '2'
    })
    const listData = listRes?.data?.data || {}
    const warningList = getPageContent(listData)
    const ids = Array.from(new Set(
      warningList
        .map((item: any) => item?.id)
        .filter(Boolean)
    ))

    if (!ids.length) {
      gasWarningCharts.value = []
      gasWarningChartIndex.value = 0
      resetGasWarningChartTimer()
      return
    }

    const detailResults = await Promise.allSettled(ids.map(id => axios.post(`${BASE_URL}/api/boot/system/pipeline/applianceDetail`, {
      id,
      startTime: '',
      endTime: ''
    })))
    const charts = detailResults
      .map((result, index) => {
        if (result.status !== 'fulfilled') return null
        const detail = result.value?.data?.data || {}
        return normalizeGasWarningChart(detail, warningList[index])
      })
      .filter(Boolean)

    gasWarningCharts.value = charts
    gasWarningChartIndex.value = 0
    resetGasWarningChartTimer()
  } catch (error) {
    console.error('获取燃气预警折线图数据失败', error)
    gasWarningCharts.value = []
    gasWarningChartIndex.value = 0
    resetGasWarningChartTimer()
  }
}

async function fetchPipelineStats() {
  try {
    const [coverageRes, warningRes, septicRes] = await Promise.all([
      axios.get(`${BASE_URL}/api/boot/system/pipeline/coverage`),
      axios.get(`${BASE_URL}/api/boot/system/pipeline/warning`),
      axios.get(`${BASE_URL}/api/boot/system/fire/pipeline/sta`)
    ])
    const coverageData = coverageRes?.data?.data || {}
    const warningData = warningRes?.data?.data || {}
    const septicData = septicRes?.data?.data || {}

    coverageStats.value = {
      ...coverageStats.value,
      gasDeviceCount: toNumber(coverageData.gasDeviceCount, coverageStats.value.gasDeviceCount),
      septicTankMonitorCount: toNumber(septicData.total, coverageStats.value.septicTankMonitorCount),
      medicalWastewaterMonitorCount: toNumber(coverageData.medicalWastewaterMonitorCount, coverageStats.value.medicalWastewaterMonitorCount),
      riverOutletMonitorCount: toNumber(coverageData.riverOutletMonitorCount, coverageStats.value.riverOutletMonitorCount),
      waterQualityStationCount: toNumber(coverageData.waterQualityStationCount, coverageStats.value.waterQualityStationCount)
    }

    warningStats.value = {
      ...warningStats.value,
      gasLeakTodayCount: toNumber(warningData.gasLeakTodayCount, warningStats.value.gasLeakTodayCount),
      gasLeakCurrentCount: toNumber(warningData.gasLeakCurrentCount, warningStats.value.gasLeakCurrentCount),
      septicTankTodayCount: toNumber(septicData.warning, warningStats.value.septicTankTodayCount),
      septicTankCurrentCount: toNumber(septicData.warning, warningStats.value.septicTankCurrentCount),
      medicalWastewaterTodayCount: toNumber(warningData.medicalWastewaterTodayCount, warningStats.value.medicalWastewaterTodayCount),
      medicalWastewaterCurrentCount: toNumber(warningData.medicalWastewaterCurrentCount, warningStats.value.medicalWastewaterCurrentCount),
      riverOutletTodayCount: toNumber(warningData.riverOutletTodayCount, warningStats.value.riverOutletTodayCount),
      riverOutletCurrentCount: toNumber(warningData.riverOutletCurrentCount, warningStats.value.riverOutletCurrentCount),
      waterQualityStationTodayCount: toNumber(
        warningData.waterQualityStationTodayCount ?? warningData.waterStationTodayCount ?? warningData.waterQualityTodayCount,
        warningStats.value.waterQualityStationTodayCount
      ),
      waterQualityStationCurrentCount: toNumber(
        warningData.waterQualityStationCurrentCount ?? warningData.waterStationCurrentCount ?? warningData.waterQualityCurrentCount,
        warningStats.value.waterQualityStationCurrentCount
      )
    }
  } catch (error) {
    console.error('获取管线统计数据失败', error)
  }
}

function openCurrentLineList() {
  const chart = currentLineChart.value
  eventBus.emit('OPEN_PIPELINE_DIA', {
    diaName: chart.diaName,
    listType: chart.listType,
    title: `${chart.title}预警`,
    isWarning: true
  })
}

function openRiskList(item: any, isWarning: boolean) {
  eventBus.emit('OPEN_PIPELINE_DIA', {
    diaName: item.diaName,
    listType: item.listType,
    title: isWarning ? `${item.label}预警` : item.label,
    isWarning
  })
}

function switchLineChart(index: number) {
  activeLineChartIndex.value = index
  const layerKey = lineCharts[index]?.layerKey
  if (layerKey) {
    eventBus.emit(PIPELINE_DEVICE_SELECT_EVENT, { keys: [layerKey] })
  }
}

function openWaterStationList() {
  eventBus.emit('OPEN_PIPELINE_DIA', {
    diaName: 'pipeline-water-station-list',
    title: '水质监测站指标',
    isWarning: true
  })
}

function openDispatchIframe() {
  const url = props.chartConfig?.option?.dispatchUrl || DEFAULT_DISPATCH_URL
  if (!url) {
    console.warn('请先配置管线调度地址')
    return
  }
  eventBus.emit('OPEN_IFRAME_DIA', { url, type: 'pipeline' })
}

onMounted(() => {
  fetchPipelineStats()
  fetchGasWarningCharts()
  if (radarItems.length > 1) {
    radarTimer = window.setInterval(() => {
      radarTransitionEnabled.value = true
      radarPage.value += 1
      if (radarPage.value >= radarItems.length) {
        if (radarResetTimer) window.clearTimeout(radarResetTimer)
        radarResetTimer = window.setTimeout(() => {
          radarTransitionEnabled.value = false
          radarPage.value = 0
          window.requestAnimationFrame(() => {
            radarTransitionEnabled.value = true
          })
        }, 700)
      }
    }, 5000)
  }
})

onUnmounted(() => {
  if (radarTimer) {
    window.clearInterval(radarTimer)
    radarTimer = null
  }
  if (radarResetTimer) {
    window.clearTimeout(radarResetTimer)
    radarResetTimer = null
  }
  if (gasWarningChartTimer) {
    window.clearInterval(gasWarningChartTimer)
    gasWarningChartTimer = null
  }
})
</script>

<script lang="ts">
export default {
  name: 'Bz‌Pipeline',
  version: '1.0.0'
}
</script>

<style lang="scss" scoped>
@font-face {
  font-family: 'BzPipelinePuHuiTi';
  src: url('./font/Alibaba_PuHuiTi_2.0_55_Regular_55_Regular.ttf') format('truetype');
  font-weight: 400;
  font-style: normal;
  font-display: swap;
}

@font-face {
  font-family: 'BzPipelineDIN';
  src: url('./font/D-DIN-Bold.otf') format('opentype');
  font-weight: 700;
  font-style: normal;
  font-display: swap;
}

.bz-pipeline {
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 0;
  overflow: hidden;
  color: #d9f7ff;
  background: #042040;
  font-family: 'BzPipelinePuHuiTi', Microsoft YaHei, Arial, sans-serif;
  padding-right: 8px;
}

.bz-pipeline--no-bg {
  background: transparent;
}

.pipeline-toolbar {
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  min-width: 0;
}

.diaodu {
  width: 41px;
  height: 24px;
  flex: 0 0 41px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #dff8ff;
  font-size: 13px;
  background: url('./img/bg_button_deal.png') no-repeat center center;
  background-size: 100% 100%;
  cursor: pointer;
}

.pipeline-section {
  display: grid;
  grid-template-columns: 24px 1fr;
  gap: 7px;
  min-width: 0;
}

.pipeline-section--cover {
  height: 79px;
}

.pipeline-section--warn {
  height: 326px;
}

.side-title {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 100%;
  color: #d9f7ff;
  font-size: 15px;
  font-weight: 700;
  line-height: 1.05;
  background: linear-gradient(180deg, #075c92 0%, #06467e 100%);
  text-shadow: 0 0 8px rgba(74, 194, 255, 0.55);
}

.pipeline-section--cover .side-title {
  height: 100%;
}

.cover-grid {
  min-width: 0;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  grid-template-rows: repeat(2, 36px);
  gap: 4px;
  box-sizing: border-box;
}

.cover-card {
  position: relative;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-width: 0;
  height: 36px;
  padding: 2px 5px 3px;
  text-align: center;
  background: url('./img/fgBg.png') center / 100% 100% no-repeat;
}

.cover-card--warn {
  background-image: url('./img/fgBg.png');
}

.cover-card__label {
  max-width: 100%;
  overflow: hidden;
  color: #dbefff;
  font-size: 10px;
  line-height: 13px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.cover-card__value {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #91dfff;
  font-family: 'BzPipelineDIN', Arial, sans-serif;
  line-height: 18px;
}

.cover-card__value strong {
  color: #5ff6ff;
  font-size: 18px;
}

.cover-card--warn .cover-card__value strong {
  color: #5ff6ff;
}

.cover-card__value span {
  margin-left: 3px;
  color: #bedfff;
  font-size: 10px;
}

.warn-content {
  min-width: 0;
  display: grid;
  grid-template-rows: 91px 70px 1fr;
  gap: 7px;
}

.risk-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 7px;
}

.risk-badge {
  box-sizing: border-box;
  display: grid;
  grid-template-rows: 42px 42px;
  gap: 2px;
  min-width: 0;
  padding: 0;
  color: #d9f7ff;
  font-size: 10px;
  font-weight: 700;
  cursor: pointer;
  overflow: hidden;
}

.risk-badge__top,
.risk-badge__bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-width: 0;
  background: url('./img/newJc.png') center / 100% 100% no-repeat;
  padding: 0 5px;
  line-height: 17px;
}

.risk-badge--active {
  border: 1px solid rgba(95, 246, 255, 0.68);
  box-shadow: inset 0 0 10px rgba(37, 134, 255, 0.34), 0 0 8px rgba(95, 246, 255, 0.22);
  background: url('./img/newActiveBg.png') center / 100% 100% no-repeat;
}

.risk-badge span {
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.risk-badge strong {
  margin-left: auto;
  color: #f78336;
  font-family: 'BzPipelineDIN', Arial, sans-serif;
  font-size: 13px;
  cursor: pointer;
}

.risk-badge em {
  color: #5ff6ff;
  font-family: 'BzPipelineDIN', Arial, sans-serif;
  font-size: 10px;
  font-style: normal;
  cursor: pointer;
}

.line-panel {
  position: relative;
  height: 70px;
  overflow: hidden;
  background: linear-gradient(135deg, rgba(4, 42, 86, 0.55), rgba(3, 25, 58, 0.35));
  cursor: pointer;
}

.line-chart {
  position: absolute;
  left: 0;
  right: 0;
  top: 0;
  width: 100%;
  height: 70px;
  min-height: 70px;
}

.water-monitor {
  min-width: 0;
  min-height: 0;
}

.water-monitor__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 24px;
  margin-bottom: 4px;
  background: url('./img/lang.png') center / 100% 100% no-repeat;
}

.water-monitor__title,
.water-monitor__warn {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  min-width: 0;
  padding:0 4px 0px 0 ;
  color: #d9f7ff;
  font-size: 14px;
  font-weight: 700;
  line-height: 24px;
  cursor: pointer;
}

.water-monitor__title::before {
  content: '';
  width: 14px;
  height: 13px;
  background:url('./img/icon_symbol.png') center / 100% 100% no-repeat;
}

.water-monitor__title strong,
.water-monitor__warn strong {
  color: #ffbc00;
  font-family: 'BzPipelineDIN', Arial, sans-serif;
  font-size: 15px;
}

.water-monitor__title em,
.water-monitor__warn em {
  color: #5ff6ff;
  font-style: normal;
}

.radar-grid {
  position: relative;
  height: calc(100% - 28px);
  min-height: 0;
  overflow: hidden;
}

.radar-track {
  display: flex;
  gap: 7px;
  height: 100%;
  min-width: 0;
}

.radar-card {
  position: relative;
  flex: 0 0 202px;
  width: 202px;
  min-height: 0;
  overflow: hidden;
  border: 1px solid rgba(45, 137, 218, 0.65);
  background: url("./img/bg_list_width.png") center / 100% 100% no-repeat;
}

.radar-card__title {
  position: absolute;
  left: 7px;
  top: 5px;
  right: 7px;
  z-index: 1;
  color: #d9f7ff;
  font-size: 12px;
  line-height: 16px;
}

.radar-chart {
  position: absolute;
  left: 50%;
  top: 24px;
  width: 200px;
  height: 112px;
  transform: translateX(-50%);
}

</style>
