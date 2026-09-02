<template>
  <div class="facility-monitor-detail">
    <div class="popup-header">
      <div class="title-bg">
        <h3 class="title">设施监测详情</h3>
      </div>
      <img class="close-btn" :src="closeIcon" alt="关闭" @click="$emit('close')" />
    </div>

    <div class="popup-body">
      <div class="info-section">
        <div class="info-img">
          <img :src="detailInfo.picUrl || itemImg" alt="设施图片" />
        </div>
        <div class="info-content">
          <div class="info-row title-row">
            <h4 class="info-title">{{ detailInfo.structName }}</h4>
            <div class="tags">
              <span class="tag">桥梁概况</span>
              <span class="tag">评估报告</span>
            </div>
          </div>
          <div class="info-row status-row">
            <div class="status-item">
              <span class="label">结构状态：</span>
              <span class="status-dot green"></span>
              <span class="value green">{{ detailInfo.evalLevelDesc }}</span>
            </div>
            <div class="status-item">
              <span class="label">技术状况等级：</span>
              <span class="value green">{{ detailInfo.statusLevel }}</span>
              <span class="score">（{{ detailInfo.statusScore }}）</span>
            </div>
          </div>
          <div class="info-row param-row">
            <span class="label">监测参数：</span>
            <span class="value">{{ detailInfo.monitorParamTexts }}</span>
          </div>
        </div>
      </div>

      <div class="tab-bar">
        <div
          v-for="(tab, index) in tabList"
          :key="tab.name"
          class="tab-item"
          :class="{ active: activeTab === index }"
          @click="handleTabChange(index)"
        >
          <span class="tab-text">{{ tab.name }}（{{ tab.count }}）</span>
        </div>
      </div>

      <div class="tab-content">
        <div class="filter-bar">
          <div class="filter-item">
            <span class="filter-label">监测测点：</span>
            <select v-model="selectedStation" class="station-select" @change="fetchPointWarnStatus">
              <option v-for="item in stationOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
            </select>
          </div>
          <div class="filter-item">
            <span class="filter-label">时间选择：</span>
            <div class="time-tabs">
              <div
                v-for="(time, index) in timeOptions"
                :key="time"
                class="time-tab"
                :class="{ active: activeTime === index }"
                @click="handleTimeChange(index)"
              >
                <span class="time-text">{{ time }}</span>
              </div>
            </div>
          </div>
        </div>

        <div class="status-threshold">
          <div class="threshold-item">
            <img class="threshold-icon" :src="warnStatusIcon" alt="" />
            <div class="threshold-info">
              <div class="threshold-title">正常状态</div>
              <div class="threshold-value green">{{ warnStatusInfo.status }}</div>
            </div>
          </div>
          <div class="threshold-item">
            <img class="threshold-icon" :src="blueYzIcon" alt="" />
            <div class="threshold-info">
              <div class="threshold-title">蓝色阈值</div>
              <div class="threshold-value blue">{{ formatThreshold(warnStatusInfo.lowerLimit0, warnStatusInfo.upperLimit0) }}</div>
            </div>
          </div>
          <div class="threshold-item">
            <img class="threshold-icon" :src="yellowYzIcon" alt="" />
            <div class="threshold-info">
              <div class="threshold-title">黄色阈值</div>
              <div class="threshold-value yellow">{{ formatThreshold(warnStatusInfo.lowerLimit1, warnStatusInfo.upperLimit1) }}</div>
            </div>
          </div>
          <div class="threshold-item">
            <img class="threshold-icon" :src="redYzIcon" alt="" />
            <div class="threshold-info">
              <div class="threshold-title">红色阈值</div>
              <div class="threshold-value red">{{ formatThreshold(warnStatusInfo.lowerLimit2, warnStatusInfo.upperLimit2) }}</div>
            </div>
          </div>
        </div>

        <div class="chart-row">
          <div class="data-table">
            <n-data-table
              class="facility-table"
              :columns="tableColumns"
              :data="tableData"
              :bordered="false"
              :single-line="false"
              :max-height="236"
              size="small"
            />
          </div>
          <div class="chart-container">
            <img class="chart-bg" :src="echartBg" alt="" />
            <v-chart style="width:863px;height: 294px;" :key="chartRenderKey" class="echart" :option="chartOption" autoresize />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, h, onMounted, ref, watch } from 'vue'
import { NDataTable } from 'naive-ui'
import VChart from 'vue-echarts'
import axios from 'axios'
import closeIcon from '../img/close.png'
import itemImg from '../img/item.png'
import warnStatusIcon from '../img/warnStatus1.png'
import blueYzIcon from '../img/blueYz.png'
import yellowYzIcon from '../img/yellowYz.png'
import redYzIcon from '../img/redYz.png'
import echartBg from '../img/echartBg.png'

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

const detailInfo = ref({
  structName: '-',
  evalLevelDesc: '-',
  statusLevel: '-',
  statusScore: '-',
  monitorParamTexts: '-',
  monitorParams: '',
  prjId: '',
  picUrl: ''
})

const tabList = ref<any[]>([
  { name: '应变监测', count: 12 },
  { name: '主梁竖向位移', count: 8 },
  { name: '支座位移', count: 6 },
  { name: '梁体振动', count: 4 },
  { name: '电压电流', count: 10 },
  { name: '梁体倾斜', count: 5 }
])

const activeTab = ref(0)
const selectedStation = ref('all')
const activeTime = ref(0)
const stationOptions = ref<any[]>([
  { label: '全部站点', value: 'all' },
])
const timeOptions = ['今日', '本周', '本月', '近三月', '近半年']
const timeIntervals = [1, 7, 30, 90, 180]

const warnStatusInfo = ref({
  status: '正常',
  measureValueName: '',
  unit: '',
  upperLimit0: '',
  lowerLimit0: '',
  upperLimit1: '',
  lowerLimit1: '',
  upperLimit2: '',
  lowerLimit2: '',
  startTime: '',
  endTime: ''
})

const warnColorMap: Record<string, string> = {
  green: '#00ff94',
  blue: '#5fbcff',
  yellow: '#ffd039',
  red: '#ff6b6b'
}

const tableColumns = computed(() => [
  {
    title: '序号',
    key: 'index',
    width: 70,
    align: 'center',
    render: (_row: any, index: number) => index + 1
  },
  { title: '监测时间', key: 'time', width: 80, align: 'center' },
  {
    title: '预警',
    key: 'warn',
    width: 60,
    align: 'center',
    render: (row: any) => h('span', {
      style: {
        color: warnColorMap[row.warnLevel] || '#fff',
        fontSize: '14px'
      }
    }, row.warnText)
  },
  {
    title: chartLegendName.value,
    key: 'avg',
    align: 'center',
    render: (row: any) => h('span', { class: 'avg-cell' }, [
      h('span', row.avg),
      row.avgTrend
        ? h('span', { class: `avg-trend avg-trend--${row.avgTrend}` }, [
          row.avgTrend === 'up' ? '↑' : '↓',
          h('span', { class: 'avg-diff' }, `（${row.avgDiffText}）`)
        ])
        : null
    ])
  }
])

const tableData = ref<any[]>([])

const chartRows = ref<any[]>([])
const chartRenderKey = ref(0)
const chartLegendName = computed(() => {
  const name = warnStatusInfo.value.measureValueName || '监测值'
  const unit = warnStatusInfo.value.unit
  return unit ? `${name}（${unit}）` : name
})
const chartSeriesNames = computed(() => {
  const unit = warnStatusInfo.value.unit ? `（${warnStatusInfo.value.unit}）` : ''
  return {
    min: `最小值${unit}`,
    max: `最大值${unit}`
  }
})

const thresholdLineConfig = [
  { key: 'lowerLimit0', name: '蓝色阈值下限', legendName: '蓝色阈值', color: '#5fbcff' },
  { key: 'upperLimit0', name: '蓝色阈值上限', legendName: '蓝色阈值', color: '#5fbcff' },
  { key: 'lowerLimit1', name: '黄色阈值下限', legendName: '黄色阈值', color: '#ffd039' },
  { key: 'upperLimit1', name: '黄色阈值上限', legendName: '黄色阈值', color: '#ffd039' },
  { key: 'lowerLimit2', name: '红色阈值下限', legendName: '红色阈值', color: '#ff6b6b' },
  { key: 'upperLimit2', name: '红色阈值上限', legendName: '红色阈值', color: '#ff6b6b' }
]

const thresholdMarkLineSeries = computed(() => {
  const used = new Set<string>()
  const groups = thresholdLineConfig.reduce((result, item) => {
      const rawValue = (warnStatusInfo.value as any)[item.key]
      const value = Number(rawValue)
      if (!Number.isFinite(value)) return result

      const uniqueKey = `${item.key}-${value}`
      if (used.has(uniqueKey)) return result
      used.add(uniqueKey)

      if (!result[item.legendName]) {
        result[item.legendName] = {
          color: item.color,
          data: []
        }
      }

      result[item.legendName].data.push({
        name: item.name,
        yAxis: value,
        lineStyle: {
          color: item.color,
          width: 1.5,
          type: 'dashed'
        },
        label: {
          show: true,
          color: item.color,
          fontSize: 12,
          formatter: `${item.name}: ${rawValue}${warnStatusInfo.value.unit || ''}`
        }
      })

      return result
    }, {} as Record<string, { color: string; data: any[] }>)

  return Object.entries(groups).map(([name, config]) => ({
    name,
    data: [],
    type: 'line',
    symbol: 'none',
    lineStyle: { color: config.color, width: 1.5, type: 'dashed' },
    itemStyle: { color: config.color },
    tooltip: { show: false },
    markLine: {
      silent: true,
      symbol: 'none',
      data: config.data
    }
  }))
})

const thresholdTooltipRows = computed(() => {
  return thresholdLineConfig
    .map((item) => {
      const rawValue = (warnStatusInfo.value as any)[item.key]
      const value = Number(rawValue)
      if (!Number.isFinite(value)) return null
      return {
        name: item.name,
        value: rawValue,
        color: item.color
      }
    })
    .filter(Boolean)
})

const defaultChartRows = computed(() => {
  const labelPrefix = timeOptions[activeTime.value] || '默认'
  return Array.from({ length: 6 }, (_, index) => ({
    time: `${labelPrefix}${index + 1}`,
    min: 0,
    max: 0
  }))
})

const effectiveChartRows = computed(() => {
  return chartRows.value.length ? chartRows.value : defaultChartRows.value
})

function normalizeChartValue(value: any) {
  const numberValue = Number(value)
  if (!Number.isFinite(numberValue)) return NaN
  const roundedValue = Math.round(numberValue)
  if (roundedValue === 0 && numberValue !== 0) {
    return Number(numberValue.toFixed(2))
  }
  return roundedValue
}

const yAxisRange = computed(() => {
  const thresholdValues = thresholdLineConfig
    .map(item => Number((warnStatusInfo.value as any)[item.key]))
    .filter(Number.isFinite)
  const dataValues = effectiveChartRows.value
    .flatMap(item => [Number(item.min), Number(item.max)])
    .filter(Number.isFinite)
  const values = [...thresholdValues, ...dataValues]
  if (!values.length) {
    return {}
  }

  const minValue = Math.min(...values)
  const maxValue = Math.max(...values)
  if (minValue === maxValue) {
    const padding = Math.max(Math.abs(minValue) * 0.2, 1)
    return {
      min: Math.floor(minValue - padding),
      max: Math.ceil(maxValue + padding)
    }
  }

  const padding = Math.max((maxValue - minValue) * 0.12, 1)
  return {
    min: Math.floor(minValue - padding),
    max: Math.ceil(maxValue + padding)
  }
})

const chartOption = computed(() => ({
  grid: { left: 50, right: 20, top: 54, bottom: 30 },
  tooltip: {
    trigger: 'axis',
    formatter: (params: any) => {
      const list = Array.isArray(params) ? params : [params]
      const title = list[0]?.axisValueLabel || list[0]?.name || ''
      const valueRows = list
        .filter((item: any) => item?.seriesType !== 'line' || item?.data !== undefined)
        .filter((item: any) => item?.value !== undefined && item?.value !== null && item?.value !== '')
        .map((item: any) => {
          const color = item?.color || '#5fbcff'
          return `<div style="display:flex;align-items:center;gap:6px;">
            <span style="display:inline-block;width:8px;height:8px;border-radius:50%;background:${color};"></span>
            <span>${item.seriesName}: ${item.value}</span>
          </div>`
        })

      const thresholdRows = thresholdTooltipRows.value.map((item: any) => {
        return `<div style="display:flex;align-items:center;gap:6px;">
          <span style="display:inline-block;width:14px;height:0;border-top:1px dashed ${item.color};"></span>
          <span>${item.name}: ${item.value}${warnStatusInfo.value.unit || ''}</span>
        </div>`
      })

      return `<div>
        <div style="margin-bottom:6px;color:#fff;">${title}</div>
        ${valueRows.join('')}
        ${thresholdRows.length ? '<div style="height:1px;background:rgba(160,200,255,.25);margin:6px 0;"></div>' : ''}
        ${thresholdRows.join('')}
      </div>`
    }
  },
  legend: {
    type: 'scroll',
    top: 0,
    left: 'center',
    width: '92%',
    itemWidth: 16,
    itemHeight: 8,
    textStyle: { color: '#a0c8ff', fontSize: 12 },
    data: [
      chartSeriesNames.value.min,
      chartSeriesNames.value.max,
      ...thresholdMarkLineSeries.value.map(item => item.name)
    ]
  },
  xAxis: {
    type: 'category',
    data: effectiveChartRows.value.map(item => item.time),
    axisLine: { lineStyle: { color: '#5fbcff' } },
    axisLabel: { color: '#a0c8ff', fontSize: 12 }
  },
  yAxis: {
    type: 'value',
    ...yAxisRange.value,
    axisLine: { lineStyle: { color: '#5fbcff' } },
    axisLabel: { color: '#a0c8ff', fontSize: 12 },
    splitLine: { lineStyle: { color: 'rgba(95, 188, 255, 0.15)' } }
  },
  series: [{
    name: chartSeriesNames.value.min,
    data: effectiveChartRows.value.map(item => item.min),
    type: 'line',
    smooth: true,
    symbol: 'circle',
    symbolSize: 6,
    lineStyle: { color: '#5fbcff', width: 2 },
    itemStyle: { color: '#5fbcff' },
    areaStyle: {
      color: {
        type: 'linear',
        x: 0,
        y: 0,
        x2: 0,
        y2: 1,
        colorStops: [
          { offset: 0, color: 'rgba(95, 188, 255, 0.3)' },
          { offset: 1, color: 'rgba(95, 188, 255, 0)' }
        ]
      }
    }
  }, {
    name: chartSeriesNames.value.max,
    data: effectiveChartRows.value.map(item => item.max),
    type: 'line',
    smooth: true,
    symbol: 'circle',
    symbolSize: 6,
    lineStyle: { color: '#ffbc00', width: 2 },
    itemStyle: { color: '#ffbc00' },
    areaStyle: {
      color: {
        type: 'linear',
        x: 0,
        y: 0,
        x2: 0,
        y2: 1,
        colorStops: [
          { offset: 0, color: 'rgba(255, 188, 0, 0.18)' },
          { offset: 1, color: 'rgba(255, 188, 0, 0)' }
        ]
      }
    }
  }, ...thresholdMarkLineSeries.value]
}))

function normalizeDetail(data: any) {
  const fallbackName = props.payload?.structName || '-'
  return {
    structName: data?.structName || fallbackName,
    evalLevelDesc: data?.evalLevelDesc || '-',
    statusLevel: data?.statusLevel || '-',
    statusScore: data?.statusScore || '-',
    monitorParamTexts: data?.monitorParamTexts || '-',
    monitorParams: data?.monitorParams || '',
    prjId: data?.prjId || data?.projectId || props.payload?.prjId || '',
    picUrl: props.payload?.picUrl || ''
  }
}

function syncMonitorParams(params: any) {
  const list = parseMonitorParams(params)
  if (!list.length) return
  tabList.value = list.map((item: any) => {
    if (typeof item === 'string') {
      return {
        name: item,
        count: 0,
        dictMonitorParam: item || null
      }
    }
    const dictMonitorParam = item?.dictMonitorParam ?? item?.value ?? item?.code ?? item?.id
    return {
      name: item?.dictMonitorParamName || item?.name || item?.paramName || item?.label || item?.text || '-',
      count: Number(item?.count) || 0,
      dictMonitorParam: dictMonitorParam !== undefined && dictMonitorParam !== null && dictMonitorParam !== ''
        ? String(dictMonitorParam)
        : null
    }
  })
  activeTab.value = 0
}

function parseMonitorParams(params: any) {
  if (Array.isArray(params)) return params
  if (typeof params !== 'string') return []

  const text = params
    .trim()
    .replace(/^\uFEFF/, '')
  if (!text) return []

  const protectLongNumbers = (value: string) => {
    return value.replace(/(\\?"(?:dictMonitorParam|structId|prjId|projectId|pointId)\\?"\s*:\s*)(\d{16,})/g, '$1"$2"')
  }

  const parseJsonArray = (value: string) => {
    let current: any = protectLongNumbers(value)
    for (let index = 0; index < 3; index += 1) {
      if (Array.isArray(current)) return current
      if (typeof current !== 'string') return []
      const normalized = current.trim()
      if (!normalized) return []
      try {
        current = JSON.parse(protectLongNumbers(normalized))
      } catch (error) {
        if (!normalized.includes('\\"')) throw error
        current = JSON.parse(protectLongNumbers(normalized.replace(/\\"/g, '"')))
      }
    }
    return Array.isArray(current) ? current : []
  }

  try {
    const parsed = parseJsonArray(text)
    if (parsed.length) return parsed
  } catch (err) {
    const arrayStart = text.indexOf('[')
    const arrayEnd = text.lastIndexOf(']')
    if (arrayStart >= 0 && arrayEnd > arrayStart) {
      try {
        const parsed = parseJsonArray(text.slice(arrayStart, arrayEnd + 1))
        if (parsed.length) return parsed
      } catch (innerErr) {
        console.warn('解析监测参数对象数组失败，按文本兜底处理', innerErr)
      }
    } else {
      console.warn('解析监测参数对象数组失败，按文本兜底处理', err)
    }
  }

  return text
    .split(/[,，]/)
    .map(item => item.trim())
    .filter(Boolean)
}

async function fetchPointSelect() {
  const structId = props.payload?.structId
  const dictMonitorParam = (tabList.value[activeTab.value] as any)?.dictMonitorParam
  if (!structId || !dictMonitorParam) {
    fetchPointWarnStatus()
    return
  }

  try {
    const res = await axios.get(`${BASE_URL}/point/select`, {
      params: {
        structId,
        dictMonitorParam
      }
    })
    const data = res?.data?.data || res?.data || []
    const list = Array.isArray(data) ? data : []
    if (!list.length) {
      fetchPointWarnStatus()
      return
    }

    stationOptions.value = [
      { label: '全部测点', value: 'all' },
      ...list.map((item: any) => ({
        label: item?.label || item?.desc || item?.value || '-',
        value: item?.value || '',
        checked: Boolean(item?.checked),
        type: item?.type || '',
        desc: item?.desc || '',
        structId: item?.structId,
        prjId: item?.prjId || item?.projectId,
        dictMonitorParam: item?.dictMonitorParam,
        dictMonitorParamName: item?.dictMonitorParamName
      })).filter((item: any) => item.value)
    ]
    const checkedItem = stationOptions.value.find((item: any) => item.checked)
    selectedStation.value = checkedItem?.value || stationOptions.value[1]?.value || 'all'
    fetchPointWarnStatus()
  } catch (err) {
    console.error('获取设施监测测点下拉失败', err)
  }
}

function handleTabChange(index: number) {
  activeTab.value = index
  selectedStation.value = 'all'
  stationOptions.value = [{ label: '全部测点', value: 'all' }]
  fetchPointSelect()
}

function handleTimeChange(index: number) {
  activeTime.value = index
  fetchPointWarnStatus()
}

function formatThreshold(lower: string, upper: string) {
  const unit = warnStatusInfo.value.unit || ''
  if (!lower && !upper) return '~/~'
  return `${lower || '~'}/${upper || '~'}${unit}`
}

function refreshChart() {
  chartRenderKey.value += 1
}

async function fetchPointWarnStatus() {
  const pointId = selectedStation.value
  const MonitorParam = (tabList.value[activeTab.value] as any)?.dictMonitorParam
  if (!pointId || pointId === 'all' || !MonitorParam) {
    chartRows.value = []
    tableData.value = []
    refreshChart()
    return
  }

  try {
    const res = await axios.get(`${BASE_URL}/prjStructReal/pointWarnStatus`, {
      params: {
        pointId,
        timeInterval: timeIntervals[activeTime.value] || 1,
        MonitorParam
      }
    })
    const data = res?.data?.data || res?.data || {}
    warnStatusInfo.value = {
      status: data?.status || '-',
      measureValueName: data?.measureValueName || '',
      unit: data?.unit || '',
      upperLimit0: data?.upperLimit0 || '',
      lowerLimit0: data?.lowerLimit0 || '',
      upperLimit1: data?.upperLimit1 || '',
      lowerLimit1: data?.lowerLimit1 || '',
      upperLimit2: data?.upperLimit2 || '',
      lowerLimit2: data?.lowerLimit2 || '',
      startTime: data?.startTime || '',
      endTime: data?.endTime || ''
    }
    refreshChart()
    fetchStatisticPointMonitorDetail(data)
  } catch (err) {
    console.error('获取测点预警状态失败', err)
    chartRows.value = []
    tableData.value = []
    refreshChart()
  }
}

function getSelectedPointOption() {
  return stationOptions.value.find((item: any) => String(item.value) === String(selectedStation.value)) || {}
}

function getCurrentPrjId() {
  const pointOption: any = getSelectedPointOption()
  return pointOption?.prjId
    || pointOption?.projectId
    || pointOption?.prjID
    || pointOption?.projectID
    || detailInfo.value.prjId
    || props.payload?.prjId
    || props.payload?.projectId
    || pointOption?.structId
    || props.payload?.structId
}

async function fetchStatisticPointMonitorDetail() {
  const pointId = selectedStation.value
  const dictMonitorParam = (tabList.value[activeTab.value] as any)?.dictMonitorParam
  const prjId = getCurrentPrjId()
  if (!pointId || pointId === 'all' || !dictMonitorParam) {
    tableData.value = []
    chartRows.value = []
    refreshChart()
    return
  }

  try {
    const rows = await fetchStatisticRowsLoop({
      pointId,
      dictMonitorParam,
      prjId
    })
    tableData.value = withAvgTrend(rows)
    chartRows.value = rows
      .map(item => {
        const min = normalizeChartValue(item.min)
        const max = normalizeChartValue(item.max)
        return {
          time: item.time,
          min,
          max
        }
      })
      .filter(item => item.time && Number.isFinite(item.min) && Number.isFinite(item.max))
    refreshChart()
  } catch (err) {
    console.error('获取测点统计平均值失败', err)
    tableData.value = []
    chartRows.value = []
    refreshChart()
  }
}

async function fetchStatisticRowsLoop(params: { pointId: any; dictMonitorParam: any; prjId?: any }) {
  const rows: any[] = []
  let startTime = ''
  let endTime = ''
  let shouldContinue = true
  let guard = 0

  while (shouldContinue) {
    guard += 1
    const res = await axios.get(`${BASE_URL}/prjStructReal/statisticPointMonitorDetail`, {
      params: {
        structId: props.payload?.structId || undefined,
        dictMonitorParam: params.dictMonitorParam,
        pointId: params.pointId,
        prjId: params.prjId || undefined,
        startTime: startTime || undefined,
        endTime: endTime || undefined,
        timeInterval: timeIntervals[activeTime.value] || 1
      }
    })
    const data = res?.data?.data || res?.data || []
    const currentRows = normalizeStatisticRows(data)
    rows.push(...currentRows)

    const cursorSource = currentRows[currentRows.length - 1] || data
    shouldContinue = Boolean(cursorSource?.nextContinue)
    startTime = cursorSource?.nextStartTime || ''
    endTime = cursorSource?.nextEndTime || ''

    if (shouldContinue && (!startTime || !endTime)) break
  }

  return rows
}

function normalizeStatisticRows(data: any) {
  const rows = Array.isArray(data)
    ? data
    : data?.records || data?.list || data?.rows || data?.content || data?.data || (data?.pointId || data?.avg ? [data] : [])

  if (!Array.isArray(rows)) return []
  return rows.map((item: any) => ({
    pointId: item?.pointId || '',
    time: item?.time || '-',
    avg: item?.avg ?? '-',
    max: item?.max ?? '',
    min: item?.min ?? '',
    stdDev: item?.stdDev ?? '',
    variance: item?.variance ?? '',
    rms: item?.rms ?? '',
    range: item?.range ?? '',
    absCum: item?.absCum ?? '',
    squareSum: item?.squareSum ?? '',
    formulaValName: item?.formulaValName || '',
    measureParamName: item?.measureParamName || '',
    measureParamUnit: item?.measureParamUnit || '',
    measureParamId: item?.measureParamId || '',
    monitorCode: item?.monitorCode || '',
    paramAccuracy: item?.paramAccuracy || '',
    groupCount: item?.groupCount || '',
    alarm: item?.alarm || '0',
    warnLevel: String(item?.alarm) === '1' ? 'red' : 'green',
    warnText: String(item?.alarm) === '1' ? '报警' : '不报警',
    nextStartTime: item?.nextStartTime || '',
    nextEndTime: item?.nextEndTime || '',
    nextContinue: Boolean(item?.nextContinue)
  }))
}

function withAvgTrend(rows: any[]) {
  return rows.map((item, index) => {
    const current = Number(item.avg)
    const prev = Number(rows[index - 1]?.avg)
    if (index === 0 || !Number.isFinite(current) || !Number.isFinite(prev) || current === prev) {
      return {
        ...item,
        avgTrend: '',
        avgDiffText: ''
      }
    }
    return {
      ...item,
      avgTrend: current > prev ? 'up' : 'down',
      avgDiffText: formatAvgDiff(Math.abs(current - prev))
    }
  })
}

function formatAvgDiff(value: number) {
  const fixed = value.toFixed(2)
  return fixed.replace(/\.?0+$/, '')
}

function normalizeWarnChartRows(data: any) {
  const rows = Array.isArray(data)
    ? data
    : data?.records || data?.list || data?.rows || data?.trendData || data?.dataList || data?.values || []

  if (!Array.isArray(rows)) return []
  return rows
    .map((item: any) => {
      const time = item?.time || item?.monitorTime || item?.collectTime || item?.dataTime || item?.startTime || item?.createTime || ''
      const value = Number(item?.value ?? item?.measureValue ?? item?.avgValue ?? item?.monitorValue ?? item?.dataValue)
      return {
        time,
        value
      }
    })
    .filter(item => item.time && Number.isFinite(item.value))
}

function normalizeWarnLevel(status: any) {
  const text = String(status || '')
  if (text.includes('红')) return 'red'
  if (text.includes('黄')) return 'yellow'
  if (text.includes('蓝')) return 'blue'
  return 'green'
}

async function fetchMonitorDetail() {
  const structId = props.payload?.structId
  if (!structId) {
    detailInfo.value = normalizeDetail({})
    return
  }
  try {
    const res = await axios.get(`${BASE_URL}/monitor/detail`, {
      params: { structId }
    })
    const data = res?.data?.data || res?.data || {}
    detailInfo.value = normalizeDetail(data)
    syncMonitorParams(data?.monitorParams || data?.monitorParamTexts)
    fetchPointSelect()
  } catch (err) {
    console.error('获取设施监测详情失败', err)
    detailInfo.value = normalizeDetail({})
  }
}

watch(
  () => props.payload?.structId,
  () => {
    fetchMonitorDetail()
  }
)

watch(selectedStation, () => {
  fetchPointWarnStatus()
})

onMounted(() => {
  fetchMonitorDetail()
})
</script>

<script lang="ts">
export default {
  name: 'FacilityMonitorDetail',
  version: '1.0.0'
}
</script>

<style lang="scss" scoped>
.facility-monitor-detail {
  width: 1432px;
  height: 677px;
  background: url('../img/diaBg.png') no-repeat center / 100% 100%;
  transform: translate(-50%, -50%);
  position: relative;
  color: #fff;
  font-family: 'Microsoft YaHei';
  pointer-events: auto;
}

.popup-header {
  height: 50px;
  position: relative;
}

.title-bg {
  background: url('../img/diaTitleBg.png') no-repeat center / 100% 100%;
  width: 100%;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.title {
  text-align: center;
  font-family: 'Alibaba PuHuiTi 2.0';
  font-size: 20px;
  font-weight: 500;
  background: linear-gradient(0deg, #5fbcff 1.4%, #fff 58.67%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin: 0;
}

.close-btn {
  width: 40px;
  height: 40px;
  cursor: pointer;
  position: absolute;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
}

.popup-body {
  width: 100%;
  height: calc(100% - 50px);
  padding: 12px 25px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
}

.info-section {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 16px;
}

.info-img {
  width: 181px;
  height: 94px;
  flex-shrink: 0;
}

.info-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.info-content {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  gap: 12px;
}

.info-row {
  display: flex;
  align-items: center;
  gap: 16px;
}

.info-title {
  font-size: 16px;
  color: #fff;
  font-weight: 500;
  margin: 0;
  max-width: 520px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.tags {
  display: flex;
  gap: 12px;
  flex-shrink: 0;
}

.tag {
  width: 64px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #5fbcff;
  color: #5fbcff;
  font-size: 14px;
  border-radius: 2px;
}

.status-item {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-right: 32px;
}

.label {
  font-size: 16px;
  color: #a0c8ff;
  flex-shrink: 0;
}

.status-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.status-dot.green {
  background: #00ff94;
}

.value {
  font-size: 14px;
  color: #fff;
  min-width: 0;
}

.value.green {
  color: #00ff94;
}

.score {
  font-size: 14px;
  color: #fff;
}

.param-row .value {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.tab-bar {
  display: flex;
  align-items: center;
  height: 40px;
  gap: 4px;
}

.tab-item {
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 16px;
  cursor: pointer;
  white-space: nowrap;
  border-bottom: 2px solid transparent;
  box-sizing: border-box;
}

.tab-text {
  font-size: 16px;
  color: #8bbce6;
  transition: color 0.2s;
}

.tab-item:hover .tab-text {
  color: #fff;
}

.tab-item.active {
  border-bottom-color: #5fbcff;
}

.tab-item.active .tab-text {
  font-weight: 500;
  background: linear-gradient(0deg, #5fbcff 1.4%, #fff 58.67%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.tab-content {
  width: 100%;
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  margin-top: 10px;
}

.filter-bar {
  display: flex;
  align-items: center;
  gap: 32px;
  padding: 10px 0;
}

.filter-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.filter-label {
  font-size: 16px;
  color: #fff;
  white-space: nowrap;
}

.station-select {
  width: 240px;
  height: 36px;
  padding: 0 28px 0 8px;
  border: 1px solid rgba(66, 184, 255, 0.9);
  border-radius: 4px;
  background: rgba(5, 33, 62, 0.8);
  color: #fff;
  font-size: 14px;
  outline: none;
}

.time-tabs {
  display: flex;
  gap: 8px;
}

.time-tab {
  min-width: 68px;
  height: 30px;
  padding: 0 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(95, 188, 255, 0.5);
  color: #8bbce6;
  cursor: pointer;
}

.time-tab.active {
  color: #fff;
  background: rgba(4, 188, 250, 0.25);
  border-color: #04bcfa;
}

.status-threshold {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  margin: 8px 0 14px;
}

.threshold-item {
  display: flex;
  align-items: center;
  gap: 10px;
  height: 62px;
  padding: 0 12px;
  box-sizing: border-box;
  background: rgba(6, 64, 110, 0.45);
  border: 1px solid rgba(95, 188, 255, 0.28);
}

.threshold-icon {
  width: 32px;
  height: 32px;
}

.threshold-title {
  font-size: 14px;
  color: #a0c8ff;
}

.threshold-value {
  margin-top: 4px;
  font-size: 16px;
  font-weight: 700;
}

.threshold-value.green {
  color: #00ff94;
}

.threshold-value.blue {
  color: #5fbcff;
}

.threshold-value.yellow {
  color: #ffd039;
}

.threshold-value.red {
  color: #ff6b6b;
}

.chart-row {
  flex: 1;
  min-height: 0;
  display: grid;
  grid-template-columns: 500px 1fr;
  gap: 16px;
}

.data-table,
.chart-container {
  min-height: 0;
  position: relative;
  background: rgba(3, 25, 48, 0.65);
  border: 1px solid rgba(95, 188, 255, 0.28);
}

.chart-bg {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  opacity: 0.28;
}

.echart {
  position: relative;
  width: 100%;
  height: 100%;
}

:deep(.facility-table) {
  --n-merged-th-color: rgba(4, 111, 241, 0.22);
  --n-merged-td-color: transparent;
  --n-merged-td-color-hover: rgba(4, 111, 241, 0.15);
  --n-merged-border-color: rgba(95, 188, 255, 0.18);
  color: #d7e9ff;
}

:deep(.facility-table .n-data-table-th) {
  color: #8bbce6;
}

:deep(.facility-table .n-data-table-td) {
  color: #d7e9ff;
}

:deep(.avg-cell) {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  min-width: 82px;
  color: #d7e9ff;
}

:deep(.avg-trend) {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  font-weight: 700;
}

:deep(.avg-trend--up) {
  color: #ff4d4f;
}

:deep(.avg-trend--down) {
  color: #16d879;
}

:deep(.avg-diff) {
  color: #d7e9ff;
  font-weight: 400;
}
</style>
