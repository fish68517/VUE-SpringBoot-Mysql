<template>
  <div class="bz-crowded">
    <!-- 顶部预警栏 -->

    <div class="panel-title">
      <span></span>
      <div class="diaodu">调度</div>
    </div>

    <div class="warn-bar">
      <span class="status">处置中</span>
      <span class="title">洪崖洞人流超限</span>
      <span class="time">2026-6-5 15:00:00</span>
    </div>

    <!-- 卡片容器 -->
    <div class="card-container">
      <!-- 客流卡片 -->
      <div v-for="card in cardList" :key="card.name" class="crowd-card" :style="getCardStyle(card.rate, card.congestionStatus)">
        <div class="card-title">{{ card.name }}</div>
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
import { h, ref, onMounted } from 'vue'
import axios from 'axios'
import { NCarousel, NDataTable, NImage } from 'naive-ui'

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

// 通用接口基础地址
const BASE_URL = 'http://23.210.227.34:23343/yzqzlzx/'

// 卡片数据
const cardList = ref([
  { name: '解放碑', rate: 0, realTimeFlow: 0, congestionStatus: '', eventPending: 0, eventTotal: 0, railSmooth: 0, railSlow: 0, railCongestion: 0, roadSmooth: 0, roadSlow: 0, roadCongestion: 0 },
  { name: '洪崖洞', rate: 0, realTimeFlow: 0, congestionStatus: '', eventPending: 0, eventTotal: 0, railSmooth: 0, railSlow: 0, railCongestion: 0, roadSmooth: 0, roadSlow: 0, roadCongestion: 0 },
  { name: '朝天门', rate: 0, realTimeFlow: 0, congestionStatus: '', eventPending: 0, eventTotal: 0, railSmooth: 0, railSlow: 0, railCongestion: 0, roadSmooth: 0, roadSlow: 0, roadCongestion: 0 },
  { name: '鹅岭栈桥', rate: 0, realTimeFlow: 0, congestionStatus: '', eventPending: 0, eventTotal: 0, railSmooth: 0, railSlow: 0, railCongestion: 0, roadSmooth: 0, roadSlow: 0, roadCongestion: 0 },
  { name: '鹅岭二厂', rate: 0, realTimeFlow: 0, congestionStatus: '', eventPending: 0, eventTotal: 0, railSmooth: 0, railSlow: 0, railCongestion: 0, roadSmooth: 0, roadSlow: 0, roadCongestion: 0 },
  { name: '李子坝', rate: 0, realTimeFlow: 0, congestionStatus: '', eventPending: 0, eventTotal: 0, railSmooth: 0, railSlow: 0, railCongestion: 0, roadSmooth: 0, roadSlow: 0, roadCongestion: 0 },
  { name: '十八梯', rate: 0, realTimeFlow: 0, congestionStatus: '', eventPending: 0, eventTotal: 0, railSmooth: 0, railSlow: 0, railCongestion: 0, roadSmooth: 0, roadSlow: 0, roadCongestion: 0 },
  { name: '山城巷', rate: 0, realTimeFlow: 0, congestionStatus: '', eventPending: 0, eventTotal: 0, railSmooth: 0, railSlow: 0, railCongestion: 0, roadSmooth: 0, roadSlow: 0, roadCongestion: 0 },
  { name: '时代天街', rate: 0, realTimeFlow: 0, congestionStatus: '', eventPending: 0, eventTotal: 0, railSmooth: 0, railSlow: 0, railCongestion: 0, roadSmooth: 0, roadSlow: 0, roadCongestion: 0 }
])

// 获取事件统计数据
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
      var nameMap = new Map()
      res.data.data.forEach(function(item) { nameMap.set(item.scenicName, item) })
      var nextCardList = cardList.value.map(function(card) {
        var person = nameMap.get(card.name)
        if (person) {
          var rateValue = parsePercentValue(person.congestionStatus)
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
      // 按百分比降序排序
      cardList.value = nextCardList.sort(function(a, b) { return b.rate - a.rate })
    }
  } catch (err) {
    console.error('获取客流通用数据失败:', err)
  }
}

function sortCardList() {
  cardList.value = cardList.value.slice().sort(function(a, b) { return b.rate - a.rate })
}

onMounted(function() {
  fetchPersonData()
  fetchEventData()
  fetchRailwayData()
  fetchRoadData()
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
  if (congestionStatus === '畅通' || (!congestionStatus && rate < 50)) return {}
  var color = congestionStatus ? getCongestionColor(congestionStatus) : getRateColor(rate)
  return {
    boxShadow: '0 0 12px ' + color + '80, 0 0 24px ' + color + '40'
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
  width: 250px;
  height: 204px;
  background: url('./img/cardBg.png') center / 100% 100% no-repeat;
  border: 1px solid rgba(73, 255, 255, 0.2);
  padding: 8px 8px 0 8px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
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
.warn-bar {
  width: 508px;
  height: 32px;
  background: url('./img/warnBg.png') center / 100% 100% no-repeat;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  box-sizing: border-box;
  margin: 0 auto;

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
  }

  .time {
    font-size: 16px;
    font-family: 'BzDDIN', sans-serif;
    color: #a7d8ff;
  }
}

.panel-title{
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 8px;
  height: 32px;
  background: url('./img/warnBg');
}
</style>
