<template>
  <div class="bz-fire">
    <aside class="side-bar">
      <div class="side-bar__item side-bar__item--fire">
        <span class="side-bar__item-text">灭</span>
        <span>火</span>
      </div>
      <div class="side-bar__item side-bar__item--prevention">
        <span>防</span>
        <span>火</span>
      </div>
    </aside>

    <main class="content">
      <section class="module fire-system">
        <div class="module__header">
          <div class="module__header-title">消防系统“接”</div>
          <div class="diaodu" @click="handleToIfrme">调度</div>
        </div>
        <div class="fire-system__content">
          <div
            class="fire-system__main-card"
            :class="mainCardStatusClass"
            @click="openFireDialog('fire-scene-list', 1)"
          >
            <div class="fire-system__main-head">
              <img class="fire-system__main-img" src="./img/fire.png" alt="" />
              <div class="fire-system__main-panel">
                <div class="fire-system__main-title">火灾扑救</div>
                <div class="fire-system__status" v-if="fireFightTaskOngoingCount > 0">{{ fireFightTaskStatus }}</div>
              </div>
            </div>
            <div class="fire-system__main-stats">
              <div class="fire-system__main-row">
                <span>处置中</span>
                <strong>{{ fireFightTaskOngoingCount }}</strong>
              </div>
              <div class="fire-system__main-row">
                <span>今日总数</span>
                <strong class="line-color">{{ fireFightTaskCount }}</strong>
              </div>
            </div>
          </div>
          <div class="fire-system__side-list">
            <div class="fire-system__side-item" @click="openFireDialog('fire-scene-list', 3)">
              <div class="fire-system__side-title">
                <span class="fire-system__dot"></span>
                <span class="fire-system__side-label">火灾舆情</span>
              </div>
              <div class="fire-system__side-value">
                <strong>{{ firePublicOpinionOngoingCount }}</strong>
                <span>/{{ fireOpinionCount }}</span>
              </div>
            </div>
            <div class="fire-system__side-item" @click="openFireDialog('fire-scene-list', 4)">
              <div class="fire-system__side-title">
                <span class="fire-system__dot"></span>
                <span class="fire-system__side-label">抢险救援</span>
              </div>
              <div class="fire-system__side-value">
                <strong>{{ rescueMissionOngoingCount }}</strong>
                <span>/{{ rescueMissionCount }}</span>
              </div>
            </div>
            <div class="fire-system__side-item" @click="openFireDialog('fire-scene-list', 5)">
              <div class="fire-system__side-title">
                <span class="fire-system__dot"></span>
                <span class="fire-system__side-label">社会救助</span>
              </div>
              <div class="fire-system__side-value">
                <strong>{{ socialAssistanceOngoingCount }}</strong>
                <span>/{{ socialAssistanceCount }}</span>
              </div>
            </div>
          </div>
        </div>
      </section>

      <section class="module iot-perception">
        <div class="module__header">
          <div class="module__header-title">物联感知“推”</div>
        </div>
        <div class="iot-perception__grid">
          <div class="iot-perception__item" v-for="item in iotStats" :key="item.label">
            <div class="iot-perception__label" @click="handleIotStatClick(item)">
              <span class="iot-perception__icon"></span>
              <span class="iot-perception__label-text">{{ item.label }}</span>
            </div>
            <div class="iot-perception__value">
              <span class="highlight">{{ item.current }}</span
              >/ <span class="line-color"></span>{{ item.total }}
            </div>
          </div>
        </div>
        <div class="iot-perception__warning">
          <div class="iot-perception__warning-title">
            <span class="arrow"></span>
            预警现场
          </div>
          <n-carousel
            class="iot-perception__warning-carousel"
            autoplay
            :interval="5000"
            :slides-per-view="3"
            :show-dots="false"
            :show-arrow="false"
          >
            <div
              v-for="item in warningSites"
              :key="item.key"
              class="iot-perception__warning-item"
            >
              <img
                v-if="item.previewUrl"
                class="iot-perception__warning-video"
                :src="item.previewUrl"
                @click.stop="openFireVideoDialog(item)"
                alt=""
              />
              <div v-else class="iot-perception__warning-empty">加载失败</div>
              <div class="iot-perception__warning-name" @click.stop="openFireVideoDialog(item)">{{ item.name }}</div>
            </div>
          </n-carousel>
        </div>
      </section>

      <section class="module management-patrol" :class="{ 'anim-ready': isAnimReady }">
        <div class="module__header">
          <div class="module__header-title">管理单位“巡”</div>
        </div>
        <div class="patrol-summary">
          <div
            class="patrol-summary__card"
            :style="{ '--trail-color': isAnimReady ? getProgressColor(calcPercent(doubleRandomCorrectionNum, doubleRandomUnCorrectionNum)) : '#12DD60' }"
          >
            <div class="patrol-summary__title" @click="openFireDialog('fire-radom')">双随机<br>隐患</div>
            <div class="patrol-summary__content">
              <div class="patrol-summary__row">
                <span>未完成</span>
                <strong>{{ doubleRandomUnCorrectionNum }}</strong>
              </div>
              <div class="patrol-summary__row">
                <span>已完成</span>
                <strong class="line-color">{{ doubleRandomCorrectionNum }}</strong>
              </div>
              <div class="progress-bar">
                <div
                  class="progress-bar__fill"
                  :style="{
                    width: calcPercent(doubleRandomCorrectionNum, doubleRandomUnCorrectionNum) + '%',
                    background: getProgressColor(calcPercent(doubleRandomCorrectionNum, doubleRandomUnCorrectionNum))
                  }"
                ></div>
              </div>
            </div>
          </div>
          <!-- 暂时保留：网格巡查隐患
          <div class="patrol-summary__card">
            <div class="patrol-summary__title" @click="openFireDialog('grid-inspection-hidden')">网格巡查隐患</div>
          </div>
          -->
          <!-- 暂时保留：近期动火作业隐患
          <div class="patrol-summary__card">
            <div class="patrol-summary__title" @click="openFireDialog('fire-work-hidden')">近期动火作业隐患</div>
          </div>
          -->
        </div>
        <div class="management-patrol__grid">
          <div class="management-patrol__card">
            <div class="management-patrol__card-title" @click="openFireDialog('fire-patrol-missing')">
              <span class="dot dot--yellow"></span>
              近期防火巡查不到位
            </div>
            <div class="management-patrol__chart">
              <v-chart style="height: 120px;width: 230px;" :option="chart1Option" v-on="$attrs" autoresize />
            </div>
            <div class="management-patrol__card-stats">
              <div class="management-patrol__stat">
                <span class="label">已检查</span>
                <span class="value value--cyan">{{ checkedCount }}</span>
              </div>
              <div class="management-patrol__stat">
                <span class="label">未检查</span>
                <span class="value value--yellow">{{ notCheckCount }}</span>
              </div>
            </div>
          </div>
          <div class="management-patrol__card">
            <div class="management-patrol__card-title" @click="openFireDialog('fire-maintenance-missing')">
              <span class="dot dot--yellow"></span>
              近期未开展消防设施维保
            </div>
            <div class="management-patrol__chart">
              <v-chart style="height: 120px;width: 230px;" :option="chart2Option" v-on="$attrs" autoresize />
            </div>
            <div class="management-patrol__card-stats">
              <div class="management-patrol__stat">
                <span class="label" >已检查</span>
                <span class="value value--cyan">{{ maintCheckedCount }}</span>
              </div>
              <div class="management-patrol__stat">
                <span class="label">未检查</span>
                <span class="value value--yellow">{{ notMaintCount }}</span>
              </div>
            </div>
          </div>
        </div>
      </section>
    </main>
  </div>

</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { NCarousel } from 'naive-ui'
import axios from 'axios'
import firePreview1 from './img/fire1.png'
import firePreview2 from './img/fire2.png'
import firePreview3 from './img/fire3.png'
import firePreview4 from './img/fire4.png'
import firePreview5 from './img/fire5.png'
import firePreview6 from './img/fire6.png'

declare const echarts: any

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

const sourceName = 'BzFire'
const fireTruckIframeUrl = 'http://183.71.240.211:12434/fire-truck'
// 响应式统计数据
const fireFightTaskCount = ref(0) // 火灾扑救数
const fireOpinionCount = ref(0) // 火灾舆情数
const rescueMissionCount = ref(0) // 抢险救援数
const socialAssistanceCount = ref(0) // 社会救助数
const fireFightTaskOngoingCount = ref(0) // 火灾扑救-处置中数量
const fireFightTaskOngoingList = ref([]) // 火灾扑救-进行中警情列表
const fireFightTaskStatus = computed(() => fireFightTaskOngoingList.value?.[0]?.jqzt || '接警中')
const firePublicOpinionOngoingCount = ref(0) // 火灾舆情-进行中数量
const rescueMissionOngoingCount = ref(0) // 抢险救援-进行中数量
const socialAssistanceOngoingCount = ref(0) // 社会救助-进行中数量

// 物联感知统计数据
const iotStats =ref([
  { label: '消防车保障', current: 0, total: 0,key:'fire-passage-smooth' },
  { label: '消防水管监管', current: 0, total: 0,key:'fire-water' },
  { label: '消控室监测', current: 0, total: 0,key:'fire-cotrol' },
  // { label: '重点点位燃气泄露监测', current: 0, total: 0,key:'fire-gas' },
  // { label: '公共重点区域化粪池监管', current: 0, total: 0,key:'fire-septic' }
])

// 网格群众数据
const hiddenDangerUnCorrectionNum = ref(0) // 未整改
const hiddenDangerCorrectionNum = ref(0) // 已整改
const doubleRandomUnCorrectionNum = ref(0) // 未完成
const doubleRandomCorrectionNum = ref(0) // 已完成
const hotWorkCorrectionNum = ref(0) // 动火作业隐患已完成
const hotWorkUnCorrectionNum = ref(0) //动火作业未完成

// 管理单位数据
const checkRate = ref('0') // 巡查完成率
const notCheckCount = ref(0) // 未检查数量
const checkedCount = ref(0) // 已检查数量（接口字段 checkCount）
const maintRate = ref('0') // 维保完成率
const notMaintCount = ref(23) // 未维保数量
const maintCheckedCount = ref(0) // 已维保数量（接口字段 mainCount）
const isAnimReady = ref(false) // 数据加载完成后统一启动流光动画

const getRateNumber = (rate: string | number) => {
  const value = Number.parseFloat(String(rate).replace('%', ''))
  if (!Number.isFinite(value)) return 0
  return Math.max(0, Math.min(100, value))
}

const getDoneCount = (notDoneCount: number, rate: string | number) => {
  const currentRate = getRateNumber(rate)
  const notDone = Number(notDoneCount) || 0
  if (currentRate <= 0) return 0
  if (currentRate >= 100) return notDone === 0 ? 0 : notDone
  return Math.round((notDone * currentRate) / (100 - currentRate))
}

const chart1Option = computed(() => getSemiRingOption(checkRate.value, '#35fff4'))
const chart2Option = computed(() => getSemiRingOption(maintRate.value, '#ffb800'))
const mainCardStatusClass = computed(() => {
  const count = Number(fireFightTaskOngoingCount.value) || 0
  if (count === 0) return 'fire-system__main-card--green'
  if (count <= 3) return 'fire-system__main-card--orange'
  return 'fire-system__main-card--red'
})

// 预警现场数据
const defaultWarningSites = [
  { key: 'warning_placeholder_1', name: '生命通道', deviceNum: '', previewUrl: '' },
  { key: 'warning_placeholder_2', name: '生命通道', deviceNum: '', previewUrl: '' },
  { key: 'warning_placeholder_3', name: '生命通道', deviceNum: '', previewUrl: '' },
]
const warningSites = ref([...defaultWarningSites])
const warningPreviewMap: Record<string, string> = {
  0: firePreview1,
  1: firePreview2,
  2: firePreview3,
  3: firePreview4,
  4: firePreview5,
  5: firePreview6
}

// 接口基础地址
const BASE_URL = 'http://23.210.227.34:23343/ywtg/api/boot/system/fire'
// const BASE_URL = 'http://192.168.112.165:11001/api/boot/system/fire'
const VIDEO_STREAM_URL = 'http://23.210.227.34:23343/ywtg/api/boot/system/fire/video/stream'
const SAFE_STATISTICS_INTERVAL = 3 * 60 * 1000
let safeStatisticsTimer: ReturnType<typeof setInterval> | null = null

const getChartSeriesData = chartData => {
  if (!chartData) return []
  const list = Array.isArray(chartData)
    ? chartData
    : chartData.data || chartData.records || chartData.list || chartData.rows || []

  if (!Array.isArray(list)) return []

  return list.map(item => {
    if (typeof item === 'number') return item
    if (typeof item === 'string') return Number(item.replace('%', '')) || 0
    const value = item.yAxis ?? item.value ?? item.count ?? item.num ?? item.total ?? item.y ?? 0
    return Number(String(value).replace('%', '')) || 0
  })
}

const getChartXAxisData = (...chartList) => {
  for (const chartData of chartList) {
    const list = Array.isArray(chartData)
      ? chartData
      : chartData?.xAxis || chartData?.xAxisData || chartData?.months || chartData?.data || chartData?.records || []

    if (!Array.isArray(list)) continue

    const xAxisData = list.map((item, index) => {
      if (typeof item === 'string') return item
      if (typeof item === 'number') return `${item}月`
      return item.xAxis || item.month || item.name || item.label || item.x || `${index + 1}月`
    })

    if (xAxisData.length) return xAxisData
  }

  return []
}

const updateChartOption = (option, lastYearChart, thisYearChart) => {
  const lastYearData = getChartSeriesData(lastYearChart)
  const thisYearData = getChartSeriesData(thisYearChart)
  const xAxisData = getChartXAxisData(thisYearChart, lastYearChart)
  const maxValue = Math.max(...lastYearData, ...thisYearData, 0)

  if (xAxisData.length) {
    option.xAxis.data = xAxisData
  }
  if (lastYearData.length) {
    option.series[0].data = lastYearData
  }
  if (thisYearData.length) {
    option.series[1].data = thisYearData
  }
  if (maxValue > 80) {
    option.yAxis.max = Math.ceil(maxValue / 10) * 10
  }
}

const getResponsePageRecords = data => {
  const result = data?.data ?? data
  const list = Array.isArray(result)
    ? result
    : result?.records || result?.list || result?.rows || []
  return Array.isArray(list) ? list : []
}

const getVideoStreamUrl = data => {
  const result = data?.data ?? data
  if (typeof result === 'string') return result
  if (!result || typeof result !== 'object') return ''
  return result.url || result.streamUrl || result.playUrl || result.videoUrl || result.flv || result.hls || ''
}

const getFireTruckWarningSites = async () => {
  try {
    const res = await axios.post(`${BASE_URL}/list/fireTruck`, {
      pageNumber: 1,
      pageSize: 6
    })
    const records = getResponsePageRecords(res?.data)
    console.log('获取生命通道点位数据:', records)
    if (!records.length) {
      warningSites.value = [...defaultWarningSites]
      return
    }

    const sites = records.map((item, index) => {
      const deviceNum = item.deviceNum
      const id = String(item.id ?? '')
      return {
        key: item.id || deviceNum || `warning_site_${index}`,
        name:  item.address,
        deviceNum,
        previewUrl: warningPreviewMap[index] || warningPreviewMap[0]
      }
    })
    warningSites.value = sites
  } catch (error) {
    warningSites.value = [...defaultWarningSites]
  }
}

// 查询统计数据
const getSafeStatistics = async () => {
  try {
    const res = await axios.get(`${BASE_URL}/safeStatistics`)
    console.log(res.data)
    
    if (res.data.code === '000000') {
      const data = res.data.data

      // 消防系统「接」模块数据
      fireFightTaskCount.value = data.fireFightTaskCount || 0
      fireOpinionCount.value = data.fireOpinionCount || 0
      rescueMissionCount.value = data.rescueMissionCount || 0
      socialAssistanceCount.value = data.socialAssistanceCount || 0
      fireFightTaskOngoingCount.value = data.fireFightTaskOngoingCount || 0
      fireFightTaskOngoingList.value = Array.isArray(data.fireFightTaskOngoingList)
        ? data.fireFightTaskOngoingList
        : []
      firePublicOpinionOngoingCount.value = data.firePublicOpinionOngoingCount || 0
      rescueMissionOngoingCount.value = data.rescueMissionOngoingCount || 0
      socialAssistanceOngoingCount.value = data.socialAssistanceOngoingCount || 0

      // 物联感知「推」模块数据
      iotStats.value = [
        { label: '消防车保障', current: data.fireTruckOccupyWarnCount || 0, total: data.fireTruckOccupyTotal || 0,key:'fire-passage-smooth' },
        { label: '消防水管监管', current: data.fireWaterGjCount || 0, total: data.fireWaterTotal || 0,key:'fire-water' },
        { label: '消控室监测', current: data.fireControlRoomGjCount || 0, total: data.fireControlRoomTotal || 0,key:'fire-cotrol' },
      ]

      // 网格群众「报」模块数据
      hiddenDangerUnCorrectionNum.value = data.hiddenDangerUnCorrectionNum || 0
      hiddenDangerCorrectionNum.value = data.hiddenDangerCorrectionNum || 0
      doubleRandomUnCorrectionNum.value = data.doubleRandomUnCorrectionNum || 0
      doubleRandomCorrectionNum.value = data.doubleRandomCorrectionNum || 0
      hotWorkCorrectionNum.value = data.hotWorkCorrectionNum || 0
      hotWorkUnCorrectionNum.value = data.hotWorkUnCorrectionNum || 0

      // 管理单位「巡」模块数据，处理百分比格式，保留整数去掉百分号
      checkRate.value = data.checkRate ? Math.round(parseFloat(data.checkRate)).toString() : '0'
      notCheckCount.value = data.notCheckCount || 0
      checkedCount.value = data.checkCount || 0
      maintRate.value = data.maintRate ? Math.round(parseFloat(data.maintRate)).toString() : '0'
      notMaintCount.value = data.notMaintCount || 0
      maintCheckedCount.value = data.mainCount || 0
    } else {
      console.error('获取统计数据失败：' + res.data.message)
    }
  } catch (err) {
    console.error('请求统计接口失败', err)
  }
}

// 根据完成率计算进度条颜色
// ≥90% 绿色, 60-90% 橙色, <60% 红色
function getProgressColor(rate: number): string {
  if (rate >= 90) return '#12DD60'
  if (rate >= 60) return '#F68337'
  return '#F84444'
}

// 计算完成百分比的工具函数
function calcPercent(numerator: number, denominator: number): number {
  return numerator + denominator > 0
    ? Math.min(100, Math.round((numerator / (numerator + denominator)) * 100))
    : 0
}

function getSemiRingOption(rate: string | number, activeColor: string) {
  const value = getRateNumber(rate)
  const restValue = Math.max(0, 100 - value)
  const activeGradient = new echarts.graphic.LinearGradient(0, 0, 1, 0, [
    { offset: 0, color: activeColor === '#ffb800' ? '#ffffff' : '#35fff4' },
    { offset: 0.42, color: activeColor },
    { offset: 1, color: activeColor === '#ffb800' ? '#ffb800' : '#0c66bd' }
  ])

  return {
    backgroundColor: 'transparent',
    animation: true,
    series: [
      {
        type: 'pie',
        radius: [92, 104],
        center: ['50%', '92%'],
        startAngle: 180,
        clockwise: true,
        silent: true,
        label: { show: false },
        labelLine: { show: false },
        data: [
          {
            value: 100,
            itemStyle: {
              color: '#0c58aa'
            }
          },
          {
            value: 100,
            itemStyle: {
              color: 'transparent'
            }
          }
        ]
      },
      {
        type: 'pie',
        radius: [92, 104],
        center: ['50%', '92%'],
        startAngle: 180,
        clockwise: true,
        label: { show: false },
        labelLine: { show: false },
        data: [
          {
            value,
            itemStyle: {
              color: activeGradient
            }
          },
          {
            value: restValue,
            itemStyle: {
              color: 'transparent'
            }
          },
          {
            value: 100,
            itemStyle: {
              color: 'transparent'
            }
          }
        ]
      },
      {
        type: 'gauge',
        radius: 86,
        center: ['50%', '92%'],
        startAngle: 180,
        endAngle: 0,
        min: 0,
        max: 100,
        splitNumber: 4,
        axisLine: {
          lineStyle: {
            width: 1,
            color: [[1, 'rgba(48, 137, 219, 0.42)']]
          }
        },
        axisTick: {
          show: true,
          splitNumber: 2,
          lineStyle: {
            color: 'rgba(70, 151, 229, 0.42)',
            width: 1
          }
        },
        splitLine: {
          length: 7,
          lineStyle: {
            color: 'rgba(70, 151, 229, 0.42)',
            width: 1
          }
        },
        axisLabel: { show: false },
        pointer: { show: false },
        anchor: { show: false },
        detail: { show: false },
        title: { show: false }
      }
    ],
    graphic: [
      {
        type: 'text',
        left: 'center',
        top: '48%',
        style: {
          text: `${value}%`,
          fill: '#35fff4',
          fontSize: 18,
          textAlign: 'center',
          textShadowBlur: 8,
          textShadowColor: 'rgba(53, 255, 244, 0.72)'
        }
      },
      {
        type: 'text',
        left: 'center',
        top: '78%',
        style: {
          text: '完成率',
          fill: '#cbe9ff',
          fontSize: 14,
          fontWeight: 600,
          textAlign: 'center'
        }
      }
    ]
  }
}

// 图表通用配置 
function getChartOption (data: number[]) {
  return {
    backgroundColor: 'transparent',
    grid: {
      left: '0%',
      right: '0%',
      top: '18%',
      bottom: '4%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: ['1月', '2月', '3月', '4月'],
      axisLine: {
        show: true,
        lineStyle: {
          color: 'rgba(139, 188, 230, 0.3)'
        }
      },
      axisLabel: {
        color: '#8bbce6',
        fontSize: 11
      },
      axisTick: {
        show: false
      }
    },
    yAxis: {
      type: 'value',
      min: 0,
      max: 80,
      splitNumber: 3,
      splitLine: {
        show: true,
        lineStyle: {
          color: 'rgba(139, 188, 230, 0.2)',
          type: 'dashed'
        }
      },
      axisLine: {
        show: false
      },
      axisLabel: {
        color: '#8bbce6',
        fontSize: 11
      },
      axisTick: {
        show: false
      }
    },
    legend: {
      data: ['去年', '今年'],
      right: '18%',
      top: -2,
      itemWidth: 10,
      itemHeight: 6,
      textStyle: {
        color: '#8bbce6',
        fontSize: 10
      }
    },
    graphic: {
      type: 'text',
      left:0,
      top: 2,
      style: {
        text: '单位：%',
        fill: '#8bbce6',
        fontSize: 8
      }
    },
    series: [
      {
        name: '去年',
        type: 'bar',
        data: [60, 55, 40, 10],
        barWidth: 10,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0.732, color: '#0085ff' },
            { offset: 1, color: '#0085ff26' }
          ]),
          borderRadius: [2, 2, 0, 0]
        }
      },
      {
        name: '今年',
        type: 'bar',
        data: data,
        barWidth: 10,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0.7, color: '#35FFF4' },
            { offset: 1, color: '#35fff41a' }
          ]),
          borderRadius: [2, 2, 0, 0]
        }
      }
    ]
  }
}

onMounted(async () => {
  await Promise.all([
    getSafeStatistics(),
    getFireTruckWarningSites()
  ])
  // 所有数据加载完成后统一启动流光动画，确保3张卡片完全同步
  isAnimReady.value = true
  safeStatisticsTimer = setInterval(getSafeStatistics, SAFE_STATISTICS_INTERVAL)
})

onUnmounted(() => {
  if (safeStatisticsTimer) {
    clearInterval(safeStatisticsTimer)
    safeStatisticsTimer = null
  }
})

// 事件总线定义，使用弹窗组件暴露的eventBus
const eventBus = {
  on(event, callback) {
    if (!props.bus) return
    props.bus.on(event, ({ source, data }) => {
      if (source === sourceName) return
      callback(data)
    })
  },
  off(event, callback) {
    if (!props.bus) return
    props.bus.off(event, callback)
  },
  emit(event, data) {
    if (!props.bus) return
    props.bus.emit(event, { data, source: sourceName })
  }
}

// 打开弹窗
function openFireDialog(diaName: string, type?: number) {
  console.log('open')
  closeFireDialog()
  const data = { diaName, type: type || null }
  // console.log('data',data)
  eventBus.emit('OPEN_DIA', data)
}

function openFireTruckIframe() {
  closeFireDialog()
  eventBus.emit('OPEN_IFRAME_DIA', { url: fireTruckIframeUrl })
}

function handleIotStatClick(item: any) {
  if (item?.key === 'fire-passage-smooth') {
    openFireTruckIframe()
    return
  }
  openFireDialog(item.key)
}

async function openFireVideoDialog(item: any) {
  if (!item?.deviceNum) return
  try {
    const streamRes = await axios.post(VIDEO_STREAM_URL, { deviceNum: item.deviceNum })
    const videoUrl = getVideoStreamUrl(streamRes?.data)
    if (!videoUrl) return
    closeFireDialog()
    eventBus.emit('OPEN_DIA', {
      diaName: 'fire-video-player',
      name: item.name || '',
      videoUrl
    })
  } catch (error) {
    console.error('获取生命通道视频流失败:', item.deviceNum, error)
  }
}

function handleToIfrme(eventName: string, data: any) {
  closeFireDialog()
  eventBus.emit('OPEN_IFRAME_DIA', { url:'http://183.71.240.211:12431/firewatch' })
}

// 关闭弹窗
function closeFireDialog() {
  eventBus.emit('CLOSE_DIA', {})
}
</script>

<script lang="ts">
export default {
  name: 'BzFire',
  version: '1.0.1'
}
</script>

<style lang="scss" scoped>
@font-face {
  font-family: 'BzAlibabaPuHuiTi';
  src: url('./font/Alibaba_PuHuiTi_2.0_55_Regular_55_Regular.ttf') format('truetype');
  font-weight: 400;
  font-style: normal;
  font-display: swap;
}

@font-face {
  font-family: 'BzAlibabaPuHuiTi';
  src: url('./font/Alibaba_PuHuiTi_2.0_75_SemiBold_75_SemiBold.ttf') format('truetype');
  font-weight: 700;
  font-style: normal;
  font-display: swap;
}

@font-face {
  font-family: 'BzDDIN';
  src: url('./font/D-DIN.otf') format('opentype');
  font-weight: 400;
  font-style: normal;
  font-display: swap;
}

@font-face {
  font-family: 'BzDDIN';
  src: url('./font/D-DIN-Bold.otf') format('opentype');
  font-weight: 700 900;
  font-style: normal;
  font-display: swap;
}

@font-face {
  font-family: 'UISDC';
  src: url('./font/UISDC-BiaoTiHei.ttf') format('opentype');
  font-weight: 700 900;
  font-style: normal;
  font-display: swap;
}

*{
  font-family: 'BzAlibabaPuHuiTi';
}

.bz-fire {
  width: 540px;
  height: 916px;
  display: flex;
  box-sizing: border-box;
  overflow: hidden;
  color: #e8f8ff;
  // background: #061d35;
  font-family: 'BzAlibabaPuHuiTi', Microsoft YaHei, PingFang SC, Arial, sans-serif;
}

.side-bar {
  width: 24px;
  flex: 0 0 24px;
  display: flex;
  flex-direction: column;

  &__item {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 24px;
    font-size: 16px;
    font-weight: 700;
    line-height: 20px;
    letter-spacing: 2px;
    color: #cfe8ff;
  }

  &__item--fire {
    height: 260px;
    background: #156a7a;
  }

  &__item--prevention {
    flex: 1;
    background: #054180;
  }
}

.content {
  width: 516px;
  height: 100%;
  box-sizing: border-box;
  padding: 0 8px 8px;
}

.module {
  width: 100%;
  box-sizing: border-box;
  margin-bottom: 8px;
  border: 0;
  background: transparent;

  &__header {
    height: 32px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 8px 0 42px;
    background: url('./img/bg_second_title.png') no-repeat center center;
    background-size: 100% 100%;
    position: relative;
  }

  &__header-title {
    height: 32px;
    line-height: 32px;
    font-family: 'UISDC';
    font-size: 22px;
    font-weight: 700;
    letter-spacing: 1px;
    color: #fff;
    text-shadow: 0 0 8px rgba(96, 165, 250, 0.95);
  }
}

.fire-system {
  &__content {
    padding: 17px 8px 0;
  }

  &__main-card {
    height: 126px;
    box-sizing: border-box;
    padding: 10px 10px 8px;
    cursor: pointer;
    background: linear-gradient(180deg, rgba(8, 45, 95, 0.82), rgba(7, 31, 69, 0.9));
    border: 2px solid rgba(211, 117, 71, 0.78);
    box-shadow: inset 0 0 18px rgba(0, 145, 255, 0.3);
    margin-bottom: 16px;
  }

  &__main-card--green {
    border-color: rgba(18, 221, 96, 0.86);
  }

  &__main-card--orange {
    animation: fire-main-card-orange-breath 4s ease-in-out infinite;
  }

  &__main-card--red {
    animation: fire-main-card-red-breath 4s ease-in-out infinite;
  }

  &__main-head {
    height: 58px;
    display: flex;
    align-items: center;
    gap: 8px;
  }

  &__main-img {
    width: 52px;
    height: 56px;
    object-fit: contain;
    flex: 0 0 52px;
  }

  &__main-panel {
    flex: 1;
    min-width: 0;
    height: 34px;
    display: flex;
    align-items: center;
    padding: 0 5px 0 14px;
    background: url('./img/bg_long.png') no-repeat center center;
    background-size: 100% 100%;
  }

  &__main-title {
    flex: 1;
    min-width: 0;
    font-size: 17px;
    line-height: 34px;
    font-weight: 700;
    color: #ffe764;
    text-shadow: 0 0 8px rgba(255, 213, 76, 0.8);
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  &__status {
    width: 54px;
    height: 24px;
    line-height: 24px;
    flex: 0 0 54px;
    text-align: center;
    font-size: 13px;
    font-weight: 700;
    color: #075786;
    background: linear-gradient(180deg, #d8fbff 0%, #50d8ff 100%);
    border: 1px solid rgba(203, 250, 255, 0.9);
    box-shadow: inset 0 0 7px rgba(255, 255, 255, 0.76), 0 0 8px rgba(79, 216, 255, 0.46);
  }

  &__main-stats {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 8px;
    margin-top: 8px;
  }

  &__main-row {
    height: 36px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 12px;
    background: url('./img/bg_list_blue.png') no-repeat center center;
    background-size: 100% 100%;

    span {
      color: #c8eaff;
      font-size: 15px;
      font-weight: 700;
    }

    strong {
      font-family: 'BzDDIN';
      font-size: 20px;
      color: #ffcf35;
      text-shadow: 0 0 8px rgba(255, 207, 53, 0.7);
    }
  }

  &__side-list {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 8px;
    margin: 8px 0;
  }

  &__side-item {
    position: relative;
    height: 78px;
    box-sizing: border-box;
    padding: 0;
    display: block;
    cursor: pointer;
    background: rgba(5, 31, 67, 0.74);
    border: 1px solid rgba(57, 124, 191, 0.45);
    box-shadow: inset 0 0 12px rgba(0, 122, 255, 0.16);
    overflow: hidden;

    &::before {
      content: '';
      position: absolute;
      left: 0;
      right: 0;
      top: 0;
      height: 36px;
      background: url('./img/Rectangle_346242169.png') no-repeat center center;
      background-size: 100% 100%;
      pointer-events: none;
    }

    strong {
      font-family: 'BzDDIN';
      font-size: 24px;
      line-height: 40px;
      color: #ffd037;
      text-shadow: 0 0 8px rgba(251, 191, 36, 0.72);
    }

    .fire-system__side-value span {
      margin-left: 2px;
      font-size: 16px;
      line-height: 40px;
      color: #5feaff;
      font-family: 'BzDDIN';
      font-weight: 700;
    }
  }

  &__side-title {
    position: relative;
    z-index: 1;
    width: 100%;
    height: 36px;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0 6px;
    box-sizing: border-box;
    overflow: hidden;
  }

  &__side-value {
    position: relative;
    z-index: 1;
    width: 100%;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    box-sizing: border-box;
  }

  &__dot {
    width: 12px;
    height: 12px;
    margin-right: 6px;
    flex: 0 0 12px;
    border-radius: 50%;
    background: #12dd60;
    box-shadow: 0 0 8px rgba(18, 221, 96, 0.85);
  }

  &__side-label {
    display: block;
    max-width: calc(100% - 18px);
    min-width: 0;
    font-size: 15px;
    line-height: 36px;
    color: #d9f7ff;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

.iot-perception {
  &__grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 8px;
    padding:17px 8px;
  }

  &__item {
    position: relative;
    height: 70px;
    box-sizing: border-box;
    padding: 7px 8px 6px 48px;
    cursor: pointer;
    background: url('./img/bg_list_data7.png') no-repeat center center;
    background-size: 100% 100%;
    display: grid;
    grid-template-rows: 25px 26px;
    align-items: center;
  }

  &__label {
    grid-row: 1;
    min-width: 0;
    height: 25px;
    display: block;
    align-items: center;
    color: #d9f7ff;
    font-size: 13px;
    line-height: 25px;
    font-weight: 700;
    overflow: hidden;
  }

  &__icon {
    position: absolute;
    left: 8px;
    top: 50%;
    transform: translateY(-50%);
    width: 30px;
    height: 30px;
    background: url('./img/group1.png') no-repeat center center;
    background-size: 100% 100%;
  }
  
  &__label-text {
    min-width: 0;
    flex: 1;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  &__item:nth-child(2) &__icon {
    background-image: url('./img/group2.png');
  }

  &__item:nth-child(3) &__icon {
    background-image: url('./img/group3.png');
  }

  &__value {
    grid-row: 2;
    align-self: center;
    justify-self: start;
    margin-top: 0;
    text-align: left;
    color: #5feaff;
    font-family: 'BzDDIN';
    font-size: 16px;
    font-weight: 700;

    .highlight {
      color: #ffd037;
      font-size: 22px;
      text-shadow: 0 0 8px rgba(251, 191, 36, 0.72);
    }
  }

  &__warning {
    padding: 0 8px 8px;
  }

  &__warning-title {
    height: 24px;
    display: flex;
    align-items: center;
    color: #d9f7ff;
    font-size: 15px;
    font-weight: 700;
    margin-bottom: 8px;

    .arrow {
      width: 0;
      height: 0;
      margin-right: 6px;
      border-top: 5px solid transparent;
      border-bottom: 5px solid transparent;
      border-left: 7px solid #add8fd;
    }
  }

  &__warning-carousel {
    width: 100%;
    height: 110px;

    :deep(.n-carousel__slides),
    :deep(.n-carousel__slide) {
      height: 110px;
    }

    :deep(.n-carousel__slide) {
      box-sizing: border-box;
      padding-right: 8px;
    }
  }

  &__warning-item {
    height: 110px;
    box-sizing: border-box;
    overflow: hidden;
    border: 1px solid rgba(56, 189, 248, 0.48);
    background: rgba(5, 31, 67, 0.74);
  }

  &__warning-video,
  &__warning-empty {
    width: 100%;
    height: 80px;
    display: block;
    object-fit: cover;
    background: rgba(4, 20, 42, 0.78);
    cursor: pointer;
  }

  &__warning-empty {
    display: flex;
    align-items: center;
    justify-content: center;
    color: rgba(191, 219, 254, 0.72);
    font-size: 12px;
  }

  &__warning-name {
    height: 28px;
    line-height: 28px;
    padding: 0 6px;
    color: #d9f7ff;
    font-size: 12px;
    background: rgba(7, 50, 100, 0.92);
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    cursor: pointer;
    text-align: center;
  }
}

.patrol-summary {
  padding:17px 8px 8px 8px;
}

.patrol-summary__card {
  --trail-color: #12dd60;
  display: grid;
  grid-template-columns: 76px 1fr;
  gap: 8px;
  min-height: 76px;
}

.patrol-summary__title {
  height: 80px;
  box-sizing: border-box;
  padding: 13px 10px 0;
  color: #d9f7ff;
  font-size: 15px;
  line-height: 18px;
  font-weight: 700;
  text-align: center;
  cursor: pointer;
  background: url('./img/bg-card.png') no-repeat center center;
}

.patrol-summary__content {
  min-width: 0;
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-template-rows: 32px 12px;
  gap: 18px 8px;
  padding-top: 8px;
}

.patrol-summary__row {
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 12px;
  margin-bottom: 0;
  background: url('./img/bg_list_data5.png') no-repeat center center;
  background-size: 100% 100%;

  span {
    color: #d7efff;
    font-size: 15px;
    font-weight: 700;
  }

  strong {
    color: #ffd037;
    font-size: 20px;
    font-family: 'BzDDIN';
    text-shadow: 0 0 8px rgba(251, 191, 36, 0.72);
  }
}

.progress-bar {
  position: relative;
  grid-column: 1 / -1;
  align-self: center;
  width: 100%;
  height: 16px;
  background: rgba(8, 71, 126, 0.72);
  border: 1px solid rgba(19, 119, 207, 0.82);
  border-radius: 8px;
  overflow: hidden;
  padding: 1px;
  box-sizing: border-box;

  &__fill {
    position: absolute;
    top:50%;
    transform: translateY(-50%);
    height: 10px;
    border-radius: 8px;
    transition: width 0.3s, background 0.3s;
  }
}

.management-patrol {
  &__grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 8px;
    padding: 0 8px 0;
  }

  &__card {
    height: 200px;
    min-width: 0;
    box-sizing: border-box;
    border: 1px solid rgba(0, 123, 255, 0.72);
    background: rgba(5, 33, 76, 0.78);
    box-shadow: inset 0 0 14px rgba(0, 144, 255, 0.22);
    overflow: hidden;
  }

  &__card-title {
    height: 32px;
    display: flex;
    align-items: center;
    padding: 0 8px;
    color: #e8f7ff;
    font-size: 14px;
    font-weight: 700;
    background: linear-gradient(90deg, rgba(79, 96, 111, 0.72) 0%, rgba(48, 72, 94, 0.82) 100%);
    cursor: pointer;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;

    .dot {
      width: 4px;
      height: 20px;
      flex: 0 0 4px;
      margin-right: 7px;

      &--yellow {
        background: #ffd037;
        box-shadow: 0 0 6px rgba(251, 191, 36, 0.7);
      }
    }
  }

  &__chart {
    height: 120px;
    margin: 2px 4px 0;

    > div {
      width: 100%;
      height: 100%;
    }
  }

  &__card-stats {
    height: 44px;
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 6px;
    padding: 0 4px 4px;
    box-sizing: border-box;
  }

  &__stat {
    min-width: 0;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 8px;
    border: 1px solid rgba(0, 126, 255, 0.7);
    background: linear-gradient(180deg, rgba(11, 69, 132, 0.78), rgba(6, 47, 101, 0.84));

    .label {
      color: #cbe9ff;
      font-size: 14px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .value {
      font-family: 'BzDDIN';
      font-size: 18px;
      font-weight: 700;
    }

    .value--cyan {
      color: #5feaff;
      text-shadow: 0 0 6px rgba(34, 211, 238, 0.6);
    }

    .value--yellow {
      color: #ffd037;
      text-shadow: 0 0 6px rgba(251, 191, 36, 0.6);
    }
  }
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

.line-color {
  color: #5feaff !important;
}

@property --trail-angle {
  syntax: '<angle>';
  initial-value: 0deg;
  inherits: false;
}

@keyframes grid-card-trail {
  to {
    --trail-angle: 360deg;
  }
}

@keyframes fire-main-card-orange-breath {
  0%,
  100% {
    border-color: rgba(248, 128, 53, 0.72);
    box-shadow:
      inset 0 0 16px rgba(0, 145, 255, 0.25),
      0 0 4px rgba(248, 128, 53, 0.25);
  }

  50% {
    border-color: rgba(255, 214, 87, 1);
    box-shadow:
      inset 0 0 20px rgba(0, 185, 255, 0.34),
      0 0 12px rgba(255, 193, 58, 0.72),
      0 0 22px rgba(255, 132, 42, 0.38);
  }
}

@keyframes fire-main-card-red-breath {
  0%,
  100% {
    border-color: rgba(248, 68, 68, 0.72);
    box-shadow:
      inset 0 0 16px rgba(0, 145, 255, 0.25),
      0 0 4px rgba(248, 68, 68, 0.28);
  }

  50% {
    border-color: rgba(255, 62, 62, 1);
    box-shadow:
      inset 0 0 20px rgba(0, 185, 255, 0.28),
      0 0 12px rgba(255, 62, 62, 0.8),
      0 0 24px rgba(255, 0, 0, 0.45);
  }
}
</style>
