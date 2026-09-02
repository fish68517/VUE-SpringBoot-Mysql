<template>
  <div class="bz-crowded" :class="{ 'anim-ready': isAnimReady, 'big-crowd': isBigCrowd }">
    <!-- 顶部预警栏 -->

    <div v-if="!isBigCrowd" class="panel-title">
      <span></span>
      <div class="diaodu" @click="openIframeByPoint('调度')">调度</div>
    </div>

    <div v-if="!isBigCrowd" class="warn-row" @mouseenter="isHovering = true" @mouseleave="isHovering = false">
      <n-carousel
        class="warn-carousel"
        :autoplay="!isHovering"
        :show-dots="false"
        :show-arrow="false"
      >
        <div
          v-for="item in warnList"
          :key="item.key"
          class="warn-bar"
          :class="{ 'warn-bar--disabled': !hasWarningData }"
          @click="hasWarningData && handleWarningEventRowClick(item)"
        >
          <span v-if="hasWarningData" class="status">{{ item.status }}</span>
          <n-tooltip placement="bottom" :delay="500">
            <template #trigger>
              <span class="title">{{ item.title }}</span>
            </template>
            {{ item.title }}
          </n-tooltip>
          <span class="time">{{ item.time }}</span>
        </div>
      </n-carousel>
      <div class="expand-btn" @click="toggleEventList">
        <span>{{ showEventList ? '收起' : '展开' }}</span>
      </div>
    </div>

    <!-- 事件列表面板 -->
    <div v-if="showEventList" class="event-list-panel">
      <div class="event-list-header">
        <span class="col-status">状态</span>
        <span class="col-title">事件标题</span>
        <span class="col-time">时间</span>
      </div>
      <div class="event-list-body">
        <div
          v-for="item in paginatedEventList"
          :key="item.key"
          class="event-list-row"
          :class="{ 'event-list-row--disabled': !hasWarningData }"
          @click="hasWarningData && handleWarningEventRowClick(item)"
        >
          <span class="col-status">{{ item.status }}</span>
          <span class="col-title">{{ item.title }}</span>
          <span class="col-time">{{ item.time }}</span>
        </div>
        <div v-if="!hasWarningData" class="event-list-empty">暂无待处置事件</div>
      </div>
      <div v-if="hasWarningData" class="event-list-pagination">
        <span class="page-btn" :class="{ disabled: eventListPage <= 1 }" @click="changeEventPage(-1)">上一页</span>
        <span class="page-info">{{ eventListPage }} / {{ totalPages }}</span>
        <span class="page-btn" :class="{ disabled: eventListPage >= totalPages }" @click="changeEventPage(1)">下一页</span>
      </div>
    </div>

    <!-- 卡片容器 -->
    <div class="card-container">
      <!-- 排序切换按钮 -->
      <button v-if="!isBigCrowd" class="sort-switch-btn" @click="toggleSortOrder" title="切换排序方式">
        <span class="sort-icon">{{ sortOrder === 'desc' ? '↓' : '↑' }}</span>
      </button>
      <!-- 客流卡片 -->
      <div
        v-for="card in cardList"
        :key="card.name"
        class="crowd-card"
        :style="getCardStyle(card.rate, card.congestionStatus)"
        @click="openIframeByPoint(card.name)"
      >
        <div class="card-title" :class="{ 'card-title--white': isBigCrowd }">{{ card.name }}</div>
        <div class="card-header">
          <div class="rate" :style="getRateStyle(card.rate, card.congestionStatus)">{{ card.rate }}%</div>
          <div class="status" :style="getStatusStyle(card.rate, card.congestionStatus)">{{ card.congestionStatus || (card.rate < 50 ? '畅通' : card.rate < 80 ? '临界' : '超限') }}</div>
        </div>
        <div class="progress-bar">
          <div class="progress-fill" :style="getProgressStyle(card.rate, card.congestionStatus)"></div>
          <div class="progress-dot" :style="getDotStyle(card.rate, card.congestionStatus)"></div>
        </div>
        <div class="data-grid">
          <div class="data-item">
            <span class="label">客流</span>
            <span class="value"><span class="num">{{ card.realTimeFlow }}</span><span class="unit">人</span></span>
          </div>
          <div class="data-item">
            <span class="label">轨道</span>
            <span class="value"><span class="num cyan">{{ card.railSmooth }}</span>/<span class="num cyan">{{ card.railSlow }}</span>/<span class="num cyan">{{ card.railCongestion }}</span></span>
          </div>
          <div class="data-item">
            <span class="label">交通</span>
            <span class="value"><span class="num orange">{{ card.roadCongestion }}</span>/<span class="num yellow">{{ card.roadSlow }}</span>/<span class="num cyan">{{ card.roadSmooth }}</span></span>
          </div>
          <div class="data-item">
            <span class="label">事件</span>
            <span class="value"><span class="num orange2">{{ card.eventPending }}</span>/<span class="num yellow">{{ card.eventTotal }}</span></span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { h, ref, onMounted, onBeforeUnmount, computed } from 'vue'
import axios from 'axios'
import { NCarousel, NDataTable, NImage, NTooltip } from 'naive-ui'

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

const sourceName = 'BzCrowded'

// 是否大客流模式
const isBigCrowd = computed(() => props.chartConfig?.option?.isBigCrowd === true)

// 通用接口基础地址
const BASE_URL = 'http://23.210.227.34:23343/yzqzlzx'
const UNDISPOSED_EVENT_URL = 'http://23.210.227.34:23343/yzqzlzx/api/boot/system/common/event/undisposed/list'
1
const iframeUrlMap = {
  '调度': 'http://23.210.227.34:23343/yitu/#/chart/publish-pass/MjAwMDA5NzI5NTk0Njg0NjIxMA==/111111/MjAwMTExMzcwODQ2NDQ0NzQ4OQ==/MTc2NTcyMDk2OTMxOA==',
  '洪崖洞': 'http://23.210.227.34:23343/yitu/#/chart/publish-pass/MjAwMDA5NzI5NTk0Njg0NjIxMA==/111111/MjAwMDA5NzI5NjE3MzMzODYyOA==/MTc2NTcyMDk2OTMxOA==',
  '鹅岭栈桥': 'http://23.210.227.34:23343/yitu/#/chart/publish-pass/MjA0ODMyOTM0NTAzMTgyNzQ1OA==/111111/MjA0ODU4MzcwMjg0ODU3NzUzOA==/MTc3NzI2NTkxMzg5MA==',
  '解放碑': 'http://23.210.227.34:23343/yitu/#/chart/publish-pass/MjA0ODMyOTM0NTAzMTgyNzQ1OA==/111111/MjA3OTQ1NTE5NTAzOTg3MDk3OA==/MTc3NzI2NTkxMzg5MA==',
  '朝天门': 'http://23.210.227.34:23343/yitu/#/chart/publish-pass/MjA0ODMyOTM0NTAzMTgyNzQ1OA==/111111/MjA4MDEzMTA4OTEwODE2MDUxNA==/MTc3NzI2NTkxMzg5MA==',
  '鹅岭二厂': 'http://23.210.227.34:23343/yitu/#/chart/publish-pass/MjA0ODMyOTM0NTAzMTgyNzQ1OA==/111111/MjA4MDEzODMyNzI1MzYwNjQwMg==/MTc3NzI2NTkxMzg5MA==',
  '李子坝': 'http://23.210.227.34:23343/yitu/#/chart/publish-pass/MjA0ODMyOTM0NTAzMTgyNzQ1OA==/111111/MjA4MDEzODM2NjcwMTAzNTUyMg==/MTc3NzI2NTkxMzg5MA==',
  '十八梯': 'http://23.210.227.34:23343/yitu/#/chart/publish-pass/MjA0ODMyOTM0NTAzMTgyNzQ1OA==/111111/MjA4MDEzODM5NDY4MTIzNzUwNg==/MTc3NzI2NTkxMzg5MA==',
  '时代天街': 'http://23.210.227.34:23343/yitu/#/chart/publish-pass/MjA0ODMyOTM0NTAzMTgyNzQ1OA==/111111/MjA4MDEzODQxNjkwMjY2MDA5Nw==/MTc3NzI2NTkxMzg5MA==',
  '山城巷': 'http://23.210.227.34:23343/yitu/#/chart/publish-pass/MjA0ODMyOTM0NTAzMTgyNzQ1OA==/111111/MjA4MDEzODQzODk1NjMxMDUzMA==/MTc3NzI2NTkxMzg5MA=='
}

const isAnimReady = ref(false) // 控制流光动画在数据加载完毕后统一启动

const warningEventDetailLoading = ref(false)
const isHovering = ref(false)

// 排序方式：desc降序（从高到低），asc升序（从低到高）
const sortOrder = ref<'desc' | 'asc'>('desc')

// 卡片数据
const cardList = ref([
  { name: '解放碑', rate: 0, realTimeFlow: 0, congestionStatus: '', eventPending: 0, eventTotal: 0, railSmooth: 0, railSlow: 0, railCongestion: 0, roadSmooth: 0, roadSlow: 0, roadCongestion: 0 },
  { name: '朝天门', rate: 0, realTimeFlow: 0, congestionStatus: '', eventPending: 0, eventTotal: 0, railSmooth: 0, railSlow: 0, railCongestion: 0, roadSmooth: 0, roadSlow: 0, roadCongestion: 0 },
  { name: '洪崖洞', rate: 0, realTimeFlow: 0, congestionStatus: '', eventPending: 0, eventTotal: 0, railSmooth: 0, railSlow: 0, railCongestion: 0, roadSmooth: 0, roadSlow: 0, roadCongestion: 0 },
  { name: '鹅岭栈桥', rate: 0, realTimeFlow: 0, congestionStatus: '', eventPending: 0, eventTotal: 0, railSmooth: 0, railSlow: 0, railCongestion: 0, roadSmooth: 0, roadSlow: 0, roadCongestion: 0 },
  { name: '十八梯', rate: 0, realTimeFlow: 0, congestionStatus: '', eventPending: 0, eventTotal: 0, railSmooth: 0, railSlow: 0, railCongestion: 0, roadSmooth: 0, roadSlow: 0, roadCongestion: 0 },
  { name: '李子坝', rate: 0, realTimeFlow: 0, congestionStatus: '', eventPending: 0, eventTotal: 0, railSmooth: 0, railSlow: 0, railCongestion: 0, roadSmooth: 0, roadSlow: 0, roadCongestion: 0 },
  { name: '山城巷', rate: 0, realTimeFlow: 0, congestionStatus: '', eventPending: 0, eventTotal: 0, railSmooth: 0, railSlow: 0, railCongestion: 0, roadSmooth: 0, roadSlow: 0, roadCongestion: 0 },
  { name: '鹅岭二厂', rate: 0, realTimeFlow: 0, congestionStatus: '', eventPending: 0, eventTotal: 0, railSmooth: 0, railSlow: 0, railCongestion: 0, roadSmooth: 0, roadSlow: 0, roadCongestion: 0 },
  { name: '时代天街', rate: 0, realTimeFlow: 0, congestionStatus: '', eventPending: 0, eventTotal: 0, railSmooth: 0, railSlow: 0, railCongestion: 0, roadSmooth: 0, roadSlow: 0, roadCongestion: 0 }
])

// 获取事件统计数据
const defaultWarnList = [
  { key: 'default_warn_1', status: '处置中', title: '暂无待处置事件', time: '--' }
]
const warnList = ref([...defaultWarnList])

function getResponseRecords(data) {
  const result = data?.data ?? data
  const list = Array.isArray(result)
    ? result
    : result?.records || result?.list || result?.rows || []
  return Array.isArray(list) ? list : []
}

function getWarnStatus(item) {
  return item.eventStatusDesc
}

function getWarnTitle(item) {
  return item.eventTitle
}

function getWarnTime(item) {
  return item.happenTime
}

async function fetchUndisposedEvents() {
  try {
    const res = await axios.post(UNDISPOSED_EVENT_URL)
    const records = getResponseRecords(res?.data)
    warnList.value = records.length
      ? records.map((item, index) => ({
        key: item.eventNum,
        status: getWarnStatus(item),
        title: getWarnTitle(item),
        time: getWarnTime(item)
      }))
      : [...defaultWarnList]
  } catch (err) {
    console.error('获取未处置事件失败:', err)
    warnList.value = [...defaultWarnList]
  }
}

async function fetchEventData() {
  try {
    const res = await axios.post(BASE_URL + '/api/boot/system/common/event/statistics/list')
    console.log(res,'xxxxx')
    if (Array.isArray(res.data.data)) {
      var nameMap = new Map()
      res.data.data.forEach(function(item) { nameMap.set(item.name, item) })
      cardList.value = cardList.value.map(function(card) {
        var event = nameMap.get(card.name)
        return {
          ...card,
          eventPending: event ? event.pendingCount : 0,
          eventTotal: event ? event.totalCount : 0
        }
      })
    }
  } catch (err) {
    console.error('获取事件统计数据失败:', err)
  }
}

// 获取轨道数据
async function fetchRailwayData() {
  try {
    const res = await axios.post(BASE_URL + '/api/boot/system/home/railway/quantity/list')
    if (Array.isArray(res.data.data)) {
      var nameMap = new Map()
      res.data.data.forEach(function(item) { nameMap.set(item.name, item) })
      cardList.value = cardList.value.map(function(card) {
        var rail = nameMap.get(card.name)
        if (rail && rail.quantity) {
          return {
            ...card,
            railSmooth: rail.quantity['畅通'] || 0,
            railSlow: rail.quantity['缓行'] || 0,
            railCongestion: rail.quantity['拥堵'] || 0
          }
        }
        return card
      })
    }
  } catch (err) {
    console.error('获取轨道数据失败:', err)
  }
}

// 获取交通数据
async function fetchRoadData() {
  try {
    const res = await axios.post(BASE_URL + '/api/boot/system/common/road/count/list')
    if (Array.isArray(res.data.data)) {
      var nameMap = new Map()
      res.data.data.forEach(function(item) { nameMap.set(item.name, item) })
      cardList.value = cardList.value.map(function(card) {
        var road = nameMap.get(card.name)
        if (road && road.count) {
          return {
            ...card,
            roadSmooth: road.count['畅通'] || 0,
            roadSlow: road.count['缓慢'] || 0,
            roadCongestion: road.count['拥堵'] || 0
          }
        }
        return card
      })
    }
  } catch (err) {
    console.error('获取交通数据失败:', err)
  }
}

// 获取客流通用数据（客流、百分比、状态标签）
function parsePercentValue(value) {
  if (value === null || value === undefined || value === '') return null
  var percentValue = parseFloat(String(value).replace('%', '').trim())
  return Number.isFinite(percentValue) ? percentValue : null
}

function isPercentLike(value) {
  if (value === null || value === undefined || value === '') return false
  return /^-?\d+(\.\d+)?\s*%?$/.test(String(value).trim())
}

async function fetchPersonData() {
  try {
    const res = await axios.post(BASE_URL + '/api/boot/system/common/gdperson/list')
    if (Array.isArray(res.data.data)) {
      const nameMap = new Map()
      res.data.data.forEach(function(item) { nameMap.set(item.scenicName, item) })
      // 鹅岭栈桥走单独接口，先从通用数据中移除
      nameMap.delete('鹅岭栈桥')
      const nextCardList = cardList.value.map(function(card) {
        const person = nameMap.get(card.name)
        if (person) {
          let rateValue = parsePercentValue(person.congestionStatus)
          if (rateValue === null) rateValue = parsePercentValue(person.percentage)
          if (rateValue === null) rateValue = 0
          return {
            ...card,
            rate: rateValue,
            realTimeFlow: person.realTimeFlow || 0,
            congestionStatus: isPercentLike(person.congestionStatus) ? '' : (person.congestionStatus || '')
          }
        }
        return card
      })
      // 按百分比排序
      cardList.value = nextCardList.sort(function(a, b) {
        if (isBigCrowd.value) return bigCrowdOrder.indexOf(a.name) - bigCrowdOrder.indexOf(b.name)
        return sortOrder.value === 'desc' ? b.rate - a.rate : a.rate - b.rate
      })
    }
  } catch (err) {
    console.error('获取客流通用数据失败:', err)
  }
  // 鹅岭栈桥单独获取客流数据
  await fetchElzqPersonData()
}

async function fetchElzqPersonData() {
  try {
    const res = await axios.post(BASE_URL + '/api/boot/system/elzq/person')
    const data = res.data.data
    if (data) {
      let rateValue = parsePercentValue(data.congestionStatus)
      if (rateValue === null) rateValue = parsePercentValue(data.percentage)
      if (rateValue === null) rateValue = 0
      cardList.value = cardList.value.map(function(card) {
        if (card.name !== '鹅岭栈桥') return card
        return {
          ...card,
          rate: rateValue,
          realTimeFlow: data.realTimeFlow || 0,
          congestionStatus: isPercentLike(data.congestionStatus) ? '' : (data.congestionStatus || '')
        }
      })
      cardList.value = cardList.value.slice().sort(function(a, b) {
        if (isBigCrowd.value) return bigCrowdOrder.indexOf(a.name) - bigCrowdOrder.indexOf(b.name)
        return sortOrder.value === 'desc' ? b.rate - a.rate : a.rate - b.rate
      })
    }
  } catch (err) {
    console.error('获取鹅岭栈桥客流数据失败:', err)
  }
}

// 大客流模式固定排序
const bigCrowdOrder = ['解放碑', '朝天门', '洪崖洞', '鹅岭栈桥', '十八梯', '李子坝', '山城巷', '鹅岭二厂', '时代天街']

function sortByBigCrowdOrder() {
  cardList.value = cardList.value.slice().sort(function(a, b) {
    return bigCrowdOrder.indexOf(a.name) - bigCrowdOrder.indexOf(b.name)
  })
}

function sortCardList() {
  if (isBigCrowd.value) {
    sortByBigCrowdOrder()
    return
  }
  cardList.value = cardList.value.slice().sort(function(a, b) {
    return sortOrder.value === 'desc' ? b.rate - a.rate : a.rate - b.rate
  })
}

// 切换排序方式
function toggleSortOrder() {
  sortOrder.value = sortOrder.value === 'desc' ? 'asc' : 'desc'
  sortCardList()
}

let refreshTimer: any = null

async function refreshAllData() {
  await Promise.all([
    fetchUndisposedEvents(),
    fetchPersonData(),
    fetchEventData(),
    fetchRailwayData(),
    fetchRoadData()
  ])
}

onMounted(async function() {
  await refreshAllData()
  // 所有数据加载并排序完成后，统一启动流光动画，确保9张卡片完全同步
  isAnimReady.value = true
  // 每3分钟刷新一次接口数据
  refreshTimer = setInterval(refreshAllData, 3 * 60 * 1000)
})

onBeforeUnmount(() => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
    refreshTimer = null
  }
})

// 根据拥堵状态获取颜色
function getCongestionColor(status) {
  if (status === '严重拥堵') return '#F84444'
  if (status === '拥堵' || status === '较拥堵') return '#F68337'
  return '#12DD60'
}

// 根据百分比获取颜色（降级方案）
function getRateColor(rate) {
  if (rate <= 0) return '#12DD60'
  if (rate < 50) return '#12DD60'
  if (rate < 80) return '#F68337'
  return '#F84444'
}

function getProgressStyle(rate, congestionStatus) {
  var color = congestionStatus ? getCongestionColor(congestionStatus) : getRateColor(rate)
  return {
    width: Math.min(rate, 100) + '%',
    background: 'linear-gradient(90deg, #ffffff 0%, ' + color + ' 100%)'
  }
}

function getDotStyle(rate, congestionStatus) {
  var color = congestionStatus ? getCongestionColor(congestionStatus) : getRateColor(rate)
  return {
    left: Math.min(rate, 100) + '%',
    background: color,
    boxShadow: '0 0 10px ' + color + '80'
  }
}

function getRateStyle(rate, congestionStatus) {
  var color = congestionStatus ? getCongestionColor(congestionStatus) : getRateColor(rate)
  return {
    background: 'linear-gradient(180deg, #ffffff 0%, ' + color + ' 100%)',
    '-webkit-background-clip': 'text',
    backgroundClip: 'text',
    color: 'transparent'
  }
}

function getStatusStyle(rate, congestionStatus) {
  var color = congestionStatus ? getCongestionColor(congestionStatus) : getRateColor(rate)
  return {
    border: '1px solid ' + color,
    background: color + '1A',
    color: '#ffffff'
  }
}

function getCardStyle(rate, congestionStatus) {
  var color = congestionStatus ? getCongestionColor(congestionStatus) : getRateColor(rate)
  return {
    '--trail-color': color,
    boxShadow: '0 0 6px ' + color + '80, 0 0 12px ' + color + '40'
  }
}

// 事件总线定义，方便组件间通信，本地暂时不能触发，只能上传测试环境测试,注意emit中的source是组件名称，方便区分事件来源
const eventBus = {
  on: (event, callback) => {
    props.bus?.on(event, ({ source, data }) => {
      console.log('🚀 ~ eventBus on:', event, source, data)
      if (source === sourceName) return
      callback(data)
    })
  },
  off: (event, callback) => {
    props.bus?.off(event, callback)
  },
  emit: (event, data) => {
    console.log('🚀 ~ eventBus emit:', event, data)
    props.bus?.emit(event, { data, source: sourceName })
  }
}

// 事件列表展开/分页
const showEventList = ref(false)
const eventListPage = ref(1)
const eventListPageSize = ref(5)

const hasWarningData = computed(() => {
  return warnList.value.length > 0 && !String(warnList.value[0]?.key).startsWith('default_warn')
})

const totalPages = computed(() => {
  return Math.max(1, Math.ceil(warnList.value.length / eventListPageSize.value))
})

const paginatedEventList = computed(() => {
  if (!hasWarningData.value) return []
  const start = (eventListPage.value - 1) * eventListPageSize.value
  return warnList.value.slice(start, start + eventListPageSize.value)
})

function toggleEventList() {
  showEventList.value = !showEventList.value
  if (showEventList.value) {
    eventListPage.value = 1
  }
}

function changeEventPage(delta) {
  const newPage = eventListPage.value + delta
  if (newPage >= 1 && newPage <= totalPages.value) {
    eventListPage.value = newPage
  }
}

function openIframeByPoint(pointName) {
  const url = iframeUrlMap[pointName]
  if (!url) return
  eventBus.emit('OPEN_IFRAME_DIA', { url, pointName })
}

const handleWarningEventRowClick = async item => {
  const eventNumber = item?.key
  if (!eventNumber || warningEventDetailLoading.value) return
  try {
    warningEventDetailLoading.value = true
    const res = await axios({
      method: 'post',
      url: 'http://23.210.227.34:23343/yztapi/cockpit/component/event/getEventList',
      data: {
        regionCode: ['500103'],
        eventStatus: ['10', '97', '99'],
        flag: '1',
        type: null,
        keyword: String(eventNumber),
        pageNum: 1,
        pageSize: 10,
        sortOrder: 'DESC',
        sortField: 'happen_time'
      },
      timeout: 50000
    })
    const records =
      res?.data?.resultBody?.page?.records ||
      res?.resultBody?.page?.records ||
      []
    const flowId = Array.isArray(records) && records.length ? records[0]?.flowId : ''
    const eventData = {
        flowId,
        eventNum: String(eventNumber)
    }
    warningEventDetailLoading.value = false
    eventBus.emit('EventDetailModal:eventData', eventData)
  } catch (err) {
    console.error('获取事件详情失败:', err)
  } finally {
    warningEventDetailLoading.value = false
  }
}
</script>



<script lang="ts">
export default {
  name: "BzCrowded",
  version: "1.0.0",
};
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

/* 卡片容器 */
.card-container {
  position: relative;
  margin-top: 10px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  flex: 1;
  overflow-y: auto;
  align-content: flex-start;
  justify-content: center;
  scrollbar-width: none;
  -ms-overflow-style: none;

  &::-webkit-scrollbar {
    display: none;
  }

  &::after {
    content: '';
    width: 250px;
  }
}

.bz-crowded {
  width: 540px;
  height: 928px;
  box-sizing: border-box;
  overflow: hidden;
  font-family: 'BzAlibabaPuHuiTi', Microsoft YaHei, PingFang SC, Arial, sans-serif;
  display: flex;
  flex-direction: column;
}

/* 客流卡片 */
.crowd-card {
  --trail-color: #12DD60;
  width: 250px;
  height: 200px;
  position: relative;
  isolation: isolate;
  overflow: hidden;
  background: url('./img/cardBg.png') center / 100% 100% no-repeat;
  border: 1px solid color-mix(in srgb, var(--trail-color) 25%, transparent);
  box-shadow: none !important;
  padding: 8px 8px 0 8px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  cursor: pointer;

  &::before {
    content: '';
    position: absolute;
    z-index: 1;
    inset: -1px;
    padding: 4px;
    background: conic-gradient(
      from var(--trail-angle),
      transparent 0deg,
      transparent 285deg,
      color-mix(in srgb, var(--trail-color) 25%, transparent) 305deg,
      var(--trail-color) 338deg,
      #ffffff 346deg,
      var(--trail-color) 352deg,
      transparent 360deg
    );
    -webkit-mask:
      linear-gradient(#000 0 0) content-box,
      linear-gradient(#000 0 0);
    -webkit-mask-composite: xor;
    mask:
      linear-gradient(#000 0 0) content-box,
      linear-gradient(#000 0 0);
    mask-composite: exclude;
    pointer-events: none;
  }

  // 所有数据加载完毕后统一启动流光动画，确保9张卡片完全同步
  .anim-ready &::before {
    animation: crowd-card-trail 9s linear infinite;
  }

  > * {
    position: relative;
    z-index: 2;
  }
}

@property --trail-angle {
  syntax: '<angle>';
  initial-value: 0deg;
  inherits: false;
}

@keyframes crowd-card-trail {
  to {
    --trail-angle: 360deg;
  }
}

.card-title {
  width: 234px;
  height: 36px;
  line-height: 36px;
  background: url('./img/titleBg.png') center / 100% 100% no-repeat;
  text-align: center;
  font-size: 20px;
  font-weight: 700;
  color: #d2ecff;
  margin: 0 auto 10px auto;
  flex-shrink: 0;
}

.card-title--white {
  color: #ffffff;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 8px;
  margin-bottom: 8px;
  flex-shrink: 0;

  .rate {
    font-size: 20px;
    font-weight: 700;
    font-family: 'BzDDIN', sans-serif;
  }

  .status {
    width: 60px;
    height: 26px;
    line-height: 26px;
    text-align: center;
    color: #ffffff;
    font-size: 13px;
    font-weight: 500;
  }
}

.progress-bar {
  width: calc(100% - 16px);
  height: 10px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 5px;
  position: relative;
  margin: 0 auto 12px auto;
  flex-shrink: 0;

  .progress-fill {
    height: 100%;
    background: linear-gradient(90deg, #ffffff 0%, #49FFFF 100%);
    border-radius: 5px;
    position: absolute;
    left: 0;
    top: 0;
  }

  .progress-dot {
    position: absolute;
    left: 59.3%;
    top: 50%;
    transform: translate(-50%, -50%);
    width: 20px;
    height: 20px;
    background: #49FFFF;
    border-radius: 50%;
    box-shadow: 0 0 10px rgba(73, 255, 255, 0.6);
  }
}

.data-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 8px;
  // padding: 0 8px;
  flex: 1;

  .data-item {
    width: 113px;
    height: 32px;
    line-height: 32px;
    background: url('./img/KPIbg.png') center / 100% 100% no-repeat;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 4px 0 10px;
    box-sizing: border-box;

    .label {
      font-size: 16px;
      color: #c7ecff;
      white-space: nowrap;
    }

    .value {
      font-family: 'BzDDIN', sans-serif;
      font-weight: 700;
      white-space: nowrap;
      color: #ffffff;

      .num {
        font-size: 16px;
        font-family: 'BzDDIN', sans-serif;
        font-style: normal;
        font-weight: 700;
      }
      .unit {
        font-size: 14px;
        font-family: 'BzAlibabaPuHuiTi', sans-serif;
      }
      .cyan { color: #49FFFF; }
      .orange { color: #ff6b49; }
      .orange2 { color: #ff9849; }
      .yellow { color: #ffd149; }
    }
  }
}

/* 顶部预警栏 */
.warn-row {
  position: relative;
  width: 508px;
  height: 32px;
  margin: 0 auto;
}

/* 顶部预警栏 */
.warn-carousel {
  width: 508px;
  height: 32px;
  margin: 0;

  :deep(.n-carousel__slides),
  :deep(.n-carousel__slide) {
    height: 32px;
  }
}

.warn-bar {
  width: 508px;
  height: 32px;
  background: url('./img/warnBg.png') center / 100% 100% no-repeat;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 72px 0 16px;
  box-sizing: border-box;
  cursor: pointer;

  &--disabled {
    cursor: default;
  }

  .status {
    font-size: 16px;
    font-weight: 700;
    color: #ffffff;
    background: rgba(73, 255, 255, 0.3);
    padding: 2px 0;
    border-radius: 2px;
    border: 1px solid rgba(73, 255, 255, 0.6);
    text-align: center;
  }

  .title {
    flex: 1;
    font-size: 16px;
    font-weight: 500;
    color: #c7ecff;
    margin-left: 16px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .time {
    font-size: 16px;
    font-family: 'BzDDIN', sans-serif;
    color: #a7d8ff;
  }
}

.expand-btn {
  position: absolute;
  right: 6px;
  top: 50%;
  transform: translateY(-50%);
  display: flex;
  align-items: center;
  justify-content: center;
  width: 52px;
  height: 24px;
  background: url('./img/bg_button_deal.png') center / 100% 100% no-repeat;
  cursor: pointer;
  flex-shrink: 0;
  font-size: 13px;
  color: #ffffff;
  font-weight: 500;
  z-index: 2;
}

/* 事件列表面板 */
.event-list-panel {
  width: 508px;
  margin: 4px auto 0;
  background: rgba(0, 20, 50, 0.85);
  border: 1px solid rgba(73, 255, 255, 0.2);
  border-radius: 4px;
  overflow: hidden;
  flex-shrink: 0;
}

.event-list-header {
  display: flex;
  align-items: center;
  height: 32px;
  padding: 0 12px;
  background: rgba(73, 255, 255, 0.1);
  border-bottom: 1px solid rgba(73, 255, 255, 0.15);
  font-size: 14px;
  font-weight: 700;
  color: #49ffff;
}

.event-list-body {
  max-height: 200px;
  overflow-y: auto;
  scrollbar-width: none;
  -ms-overflow-style: none;

  &::-webkit-scrollbar {
    display: none;
  }
}

.event-list-row {
  display: flex;
  align-items: center;
  height: 34px;
  padding: 0 12px;
  border-bottom: 1px solid rgba(73, 255, 255, 0.06);
  cursor: pointer;
  transition: background 0.15s;

  &--disabled {
    cursor: default;
    pointer-events: none;
  }

  &:hover {
    background: rgba(73, 255, 255, 0.08);
  }

  &:last-child {
    border-bottom: none;
  }
}

.event-list-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 60px;
  color: rgba(167, 216, 255, 0.6);
  font-size: 14px;
}

.col-status {
  width: 64px;
  font-size: 13px;
  font-weight: 700;
  color: #ffffff;
  flex-shrink: 0;
}

.col-title {
  flex: 1;
  font-size: 13px;
  color: #c7ecff;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin: 0 12px;
}

.col-time {
  font-size: 13px;
  font-family: 'BzDDIN', sans-serif;
  color: #a7d8ff;
  flex-shrink: 0;
}

.event-list-pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  height: 36px;
  border-top: 1px solid rgba(73, 255, 255, 0.12);
  background: rgba(73, 255, 255, 0.04);
}

.page-btn {
  font-size: 12px;
  color: #49ffff;
  cursor: pointer;
  user-select: none;

  &.disabled {
    color: rgba(73, 255, 255, 0.25);
    cursor: default;
    pointer-events: none;
  }
}

.page-info {
  font-size: 12px;
  font-family: 'BzDDIN', sans-serif;
  color: #a7d8ff;
}

.panel-title{
  width: 508px;
  height: 32px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 16px;
  margin: 0 16px;
  height: 32px;
  margin-bottom: 2px;
  background: url('./img/warnBg.png');
  .diaodu{
    color: #fff;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 41px;
    height: 24px;
    background: url('./img/bg_button_deal.png');
    cursor: pointer;
  }
}

/* 排序切换按钮 */
.sort-switch-btn {
  position: absolute;
  top: -4px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 10;
  width: 24px;
  height: 24px;
  background: linear-gradient(135deg, rgba(16, 64, 126, 0.8), rgba(24, 92, 179, 0.8));
  border: 1px solid rgba(83, 174, 255, 0.5);
  border-radius: 4px;
  color: #aed5ff;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
}

.sort-switch-btn:hover {
  background: linear-gradient(135deg, rgba(24, 92, 179, 0.9), rgba(30, 110, 200, 0.9));
  border-color: rgba(83, 174, 255, 0.8);
  color: #ffffff;
}

.sort-icon {
  font-size: 14px;
  line-height: 1;
}

/* 大客流模式 */
.big-crowd {
  width: 612px;

  .card-container {
    margin-top: 0;
    gap: 12px;
    justify-content: flex-start;

    &::after {
      width: 292px;
    }
  }

  .crowd-card {
    width: 292px;
    height: 230px;
    padding: 10px 10px 0 10px;
  }

  .card-title {
    width: 272px;
    height: 40px;
    line-height: 40px;
    font-size: 22px;
    margin-bottom: 12px;
  }

  .card-header {
    padding: 0 10px;
    margin-bottom: 10px;

    .rate {
      font-size: 24px;
    }

    .status {
      width: 68px;
      height: 28px;
      line-height: 28px;
      font-size: 14px;
    }
  }

  .progress-bar {
    width: calc(100% - 20px);
    height: 12px;
    margin-bottom: 14px;

    .progress-dot {
      width: 22px;
      height: 22px;
    }
  }

  .data-grid {
    gap: 10px;

    .data-item {
      width: 131px;
      height: 38px;
      line-height: 38px;
      padding: 0 4px 0 12px;

      .label {
        font-size: 18px;
      }

      .value {
        .num {
          font-size: 18px;
        }
        .unit {
          font-size: 15px;
        }
      }
    }
  }

  .sort-switch-btn {
    top: -2px;
    width: 28px;
    height: 28px;
    font-size: 16px;
  }
}
</style>
