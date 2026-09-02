<template>
  <div class="bz-waterlog">
    <!-- 顶部调度按钮行 -->
    <div class="top-dispatch-row">
      <span class="top-dispatch-btn" @click="openDispatchIframe('waterlog')">调度</span>
    </div>

    <div class="bz-waterlog__body">
    <aside class="side-tabs">
      <div class="side-tabs__col">
        <div class="side-tabs__item side-tabs__item--call">
          <span>叫应</span>
        </div>
        <div class="side-tabs__item side-tabs__item--response">
          <span>响应</span>
        </div>
        <div class="side-tabs__item side-tabs__item--disposal">
          <span>处置</span>
        </div>
      </div>
    </aside>

    <main class="content">
      <!-- 新结构 - 叫应板块 -->
      <section class="call-section">
        <!-- 轮次行 -->
        <div class="round-row">
          <div class="round-left">
            <span class="round-label">轮次</span>
            <select v-model="selectedRound" class="round-select">
              <option v-for="item in roundList" :key="item.no" :value="item.no">{{ item.label }}</option>
            </select>
          </div>
          <!-- <div class="round-right">上一轮时间 第13轮 7-1~7-10</div> -->
        </div>

        <!-- 临灾叫应+统计 容器 -->
        <div class="call-content">
          <!-- 临灾叫应行 -->
          <div class="call-row">
            <div class="call-left">
              <span class="call-label">临灾叫应</span>
              <select v-model="selectedCallPeriod" class="call-select">
                <option v-for="period in callPeriodList" :key="period" :value="period">第{{ period }}期</option>
              </select>
            </div>
          </div>

          <!-- 统计行 -->
          <div class="stats-row">
            <div class="stat-item" @click="openRegionalWarningDetail">
              <span class="stat-label">区级</span>
              <span class="stat-pending">{{ regionalPendingCount }}</span>
              <span class="stat-pending-label">未响应</span>
              <span class="stat-total">{{ regionalTotalCount }}</span>
              <span class="stat-total-label">总数</span>
            </div>
            <div class="stat-item" @click="openTownWarningDetail">
              <span class="stat-label">镇街</span>
              <span class="stat-pending">{{ townPendingCount }}</span>
              <span class="stat-pending-label">未响应</span>
              <span class="stat-total">{{ townTotalCount }}</span>
              <span class="stat-total-label">总数</span>
            </div>
          </div>
        </div>
      </section>

      <!-- 新结构 - 响应板块 -->
      <section class="resp-section">
        <div class="resp-content" @mouseenter="stopPointWarningScroll" @mouseleave="startPointWarningScroll">
          <!-- 左侧 -->
          <div class="resp-left">
            <!-- 第一行：标题 -->
            <div class="resp-title" @click="openPointWarningList">
              <span class="play-icon">▶</span>
              <span class="title-text">雨前巡守</span>
            </div>
            <!-- 第二行：未响应 + 总数 -->
            <div class="resp-stats">
              <div class="resp-pending-card">
                <span class="pending-left">未响应</span>
                <span class="pending-num">{{ pointWarningPendingCount }}</span>
              </div>
              <div class="resp-total-card">
                <span class="total-left">总数</span>
                <span class="total-num">{{ pointWarningTotalCount }}</span>
              </div>
            </div>
            <!-- 第三行：预警定位 + 状态 + 预警等级（轮播） -->
            <Transition name="carousel-fade" mode="out-in">
              <div class="resp-info-row" :key="pointWarningScrollIndex">
                <template v-if="pointWarningScrollData.length">
                  <span class="info-label">预警定位：</span>
                  <span class="info-value">{{ currentPointWarningRiskPoint?.name || currentPointWarning.name || '-' }}</span>
                  <span class="status-tag" :class="getWarningStatusClass(currentPointWarning.warningStatus)">{{ getWarningStatusText(currentPointWarning.warningStatus) }}</span>
                  <span class="info-label">预警等级：</span>
                  <span class="level-tag" :class="getLevelClass(currentPointWarning.warningLevel)">{{ getLevelText(currentPointWarning.warningLevel) }}</span>
                </template>
                <span v-else class="info-empty">暂无预警数据</span>
              </div>
            </Transition>
          </div>
          <!-- 右侧：图片+时间 -->
          <div class="resp-img-box" @click="openPointWarningVideo">
            <img :src="currentPointWarningImg" class="resp-img" alt="监控">
            <span class="img-time">{{ currentPointWarningTime }}</span>
          </div>
        </div>
      </section>

      <!-- 新结构 - 处置板块 -->
      <section class="resp-section">
        <div class="resp-content" @mouseenter="stopAlarmScroll" @mouseleave="startAlarmScroll">
          <!-- 左侧 -->
          <div class="resp-left">
            <!-- 第一行：标题 -->
            <div class="resp-title" @click="openAlarmEventList">
              <span class="play-icon">▶</span>
              <span class="title-text">积水处置</span>
            </div>
            <!-- 第二行：未响应 + 总数 -->
            <div class="resp-stats">
              <div class="resp-pending-card">
                <span class="pending-left">未响应</span>
                <span class="pending-num">{{ alarmWarningPendingCount }}</span>
              </div>
              <div class="resp-total-card">
                <span class="total-left">总数</span>
                <span class="total-num">{{ alarmWarningTotalCount }}</span>
              </div>
            </div>
            <!-- 第三行：预警定位 + 状态 + 预警等级（轮播） -->
            <Transition name="carousel-fade" mode="out-in">
              <div class="resp-info-row" :key="alarmScrollIndex">
                <template v-if="alarmScrollData.length">
                  <span class="info-label">预警定位：</span>
                  <span class="info-value">{{ currentAlarmRiskPoint?.name || currentAlarm.riskPoint || '-' }}</span>
                  <span class="status-tag" :class="getWarningStatusClass(currentAlarm.warningStatus)">{{ getWarningStatusText(currentAlarm.warningStatus) }}</span>
                  <span class="info-label">预警等级：</span>
                  <span class="level-tag" :class="getLevelClass(currentAlarm.warningLevel)">{{ getLevelText(currentAlarm.warningLevel) }}</span>
                </template>
                <span v-else class="info-empty">暂无预警数据</span>
              </div>
            </Transition>
          </div>
          <!-- 右侧：图片+时间 -->
          <div class="resp-img-box" @click="openAlarmVideo">
            <img :src="currentAlarmImg" class="resp-img" alt="监控">
            <span class="img-time">{{ currentAlarmTime }}</span>
          </div>
        </div>
      </section>

      <!-- 原有内容已注释
      <section class="weather-warning">
        <div class="section-header">
          <span class="section-header__icon">
            <img src="./font/icon_symbol_second.png" alt="">
          </span>
          <span class="section-header__title">气象预警信息</span>
          <span class="section-header__btn" @click="openDispatchIframe('weather')">调度</span>
        </div>
        <div class="warning-row" @click="openWeatherAlert" style="cursor: pointer;">
          <span class="warning-row__icon">
            <img src="./font/icon_forwarning_events.png" alt="">
          </span>
          <span class="warning-row__text">
            {{ weatherWarningText }}
            <span v-if="weatherWarningLevel" style="color: #FFBC00;">({{ weatherWarningLevel }})</span>
          </span>
          <span class="warning-row__date">{{ weatherWarningDate }}</span>
        </div>
      </section>

      <section class="rain-monitor">
        <div class="section-header">
          <div class="section-header-left">
            <span class="section-header__icon">
              <img src="./font/icon_symbol_second.png" alt="">
            </span>
            <span class="section-header__title">降雨监测</span>
            <select class="rain-select" v-model="selectedStation">
              <option value="fotuguan">重庆佛图关</option>
              <option value="jiefangbei">重庆解放碑</option>
              <option value="chaotianmen">重庆朝天门</option>
            </select>
          </div>
          <span class="section-header__btn" @click="openRainfallMonitor">查看详情</span>
        </div>
        <div class="rain-info-bar">
          <span class="rain-info-bar__label">
          <span class="section-header__icon">
            <img src="./font/icon_localtion.png" alt="">
          </span>
            重庆佛图关05.22
          </span>
          <span>
            <span class="rain-info-bar__title">日平均降雨量 </span>
            <span class="rain-info-bar__value">14.2mm</span>
          </span>
        </div>
        <v-chart class="rain-chart" :option="rainChartOption" autoresize />
      </section>

      <section class="waterlog-area" @mouseenter="stopRiskSlide" @mouseleave="startRiskSlide">
        <div class="area-tabs-row" style="display: flex;justify-content: space-between; align-items: center;">
            <span style="font-size: 12px;">
              <span style="background: linear-gradient(180deg, #ffffff 0%, #bffcff 42%, #1b8991 100%); -webkit-background-clip: text; -webkit-text-fill-color: transparent;">{{ areaWarningPeriodText || '2026年第13期' }}</span>
              <span style="color: #ffd039; background: rgba(255, 208, 57, 0.15); padding: 0 4px; border-radius: 2px; margin-left: 8px;">{{ areaWarningDateRange || '[7.6-7.8]' }}</span>
            </span>
            <span class="area-tabs-row__btn" @click="openDispatchIframe('waterlog')">调度</span>
        </div>
        <div class="area-tabs-row">
          <div class="area-tab" @click="openRegionalWarningDetail">
            <span class="area-tab__label">区级部门</span>
            <span class="area-tab__value">
              <span>{{ regionalCalledCount }}<span style="color: #8bbce6; font-size: 12px; margin-left: 1px;">(已叫应)</span></span>
              <span style="color: #38fff5;">/{{ regionalTotalCount }}<span style="color: #8bbce6; font-size: 12px; margin-left: 1px;">(总数)</span></span>
            </span>
          </div>
          <div class="area-tab" @click="openRegionalWarningDetail">
            <span class="area-tab__label">镇街</span>
             <span class="area-tab__value">
              <span>11<span style="color: #8bbce6; font-size: 12px; margin-left: 1px;">(已叫应)</span></span>
              <span style="color: #38fff5;">/11<span style="color: #8bbce6; font-size: 12px; margin-left: 1px;">(总数)</span></span>
            </span>
          </div>
        </div>
        <Transition name="carousel-fade" mode="out-in">
        <div class="waterlog-cards" :key="riskSlideIndex">
          <template v-if="riskPointsLoading">
            <div class="waterlog-card waterlog-card--loading" v-for="i in 3" :key="'loading-'+i">
              <div class="waterlog-card__img"><span class="loading-text">加载中...</span></div>
              <div class="waterlog-card__name">-</div>
            </div>
          </template>
          <template v-else-if="riskPoints.length">
            <div class="waterlog-card" v-for="point in currentRiskSlide" :key="point.id" @click="openRiskPointVideo(point)">
              <div class="waterlog-card__img"><img :src="getRiskPointImg(point)" alt="视频"></div>
              <div class="waterlog-card__name">{{ point.name || '-' }}</div>
            </div>
          </template>
          <template v-else>
            <div class="waterlog-card waterlog-card--empty">
              <div class="waterlog-card__img"><span class="empty-text">暂无风险点位</span></div>
              <div class="waterlog-card__name">-</div>
            </div>
          </template>
        </div>
        </Transition>
      </section>

      <section class="warning-disposal">
        <div class="disposal-tabs-row">
          <div class="disposal-tab disposal-tab--clickable" @click="openPointWarningList">
            <span class="disposal-tab__icon">◆</span>
            <span class="disposal-tab__label">雨前预警</span>
           <span class="area-tab__value">
              <span>{{ pointWarningPendingCount }}<span style="color: #8bbce6; font-size: 12px; margin-left: 1px;">(未处置)</span></span>
              <span style="color: #38fff5;">/{{ pointWarningTotalCount }}<span style="color: #8bbce6; font-size: 12px; margin-left: 1px;">(总数)</span></span>
            </span>
          </div>
          <div class="disposal-tab disposal-tab--clickable" @click="openAlarmEventList">
            <span class="disposal-tab__icon">◆</span>
            <span class="disposal-tab__label">积水预警</span>
           <span class="area-tab__value">
              <span>{{ alarmWarningPendingCount }}<span style="color: #8bbce6; font-size: 12px; margin-left: 1px;">(未处置)</span></span>
              <span style="color: #38fff5;">/{{ alarmWarningTotalCount }}<span style="color: #8bbce6; font-size: 12px; margin-left: 1px;">(总数)</span></span>
            </span>
          </div>
        </div>

        <div class="disposal-content">
          <div class="disposal-panel disposal-panel--clickable" @click="openPointWarningDetail" @mouseenter="stopPointWarningScroll" @mouseleave="startPointWarningScroll">
            <div class="disposal-panel__header">
              <span class="disposal-panel__title">雨前预警处置</span>
            </div>
            <div class="disposal-panel__body">
              <Transition name="carousel-fade" mode="out-in">
              <div v-if="pointWarningScrollData.length" :key="pointWarningScrollIndex">
                <div class="disposal-row-group">
                  <div class="disposal-row disposal-row--full">
                    <span class="disposal-row__label">预警定位:</span>
                    <span class="disposal-row__value">{{ currentPointWarning.name || '-' }}</span>
                  </div>
                </div>
                <div class="disposal-row-group">
                  <div class="disposal-row disposal-row--full">
                    <span class="disposal-row__label">预警时间段:</span>
                    <span class="disposal-row__value">{{ formatWarningPeriod(currentPointWarning.warningPeriodStart, currentPointWarning.warningPeriodEnd) }}</span>
                  </div>
                </div>
                <div class="disposal-row-group">
                  <div class="disposal-row">
                    <span class="disposal-row__label">预警等级:</span>
                    <span class="disposal-row__value level-tag" :class="getLevelClass(currentPointWarning.warningLevel)">{{ getLevelText(currentPointWarning.warningLevel) }}</span>
                  </div>
                  <div class="disposal-row">
                    <span class="disposal-row__label">降水量:</span>
                    <span class="disposal-row__value">{{ currentPointWarning.twoHourRain ? currentPointWarning.twoHourRain + 'mm' : '-' }}</span>
                  </div>
                  <div class="disposal-row">
                    <span class="disposal-row__label">预警阈值:</span>
                    <span class="disposal-row__value">{{ currentPointWarning.warningThreshold || '-' }}</span>
                  </div>
                </div>
              </div>
              </Transition>
              <div v-if="!pointWarningScrollData.length" class="disposal-empty">暂无未处置事件</div>
            </div>
          </div>

          <div class="disposal-panel disposal-panel--clickable" @click="openAlarmDetail" @mouseenter="stopAlarmScroll" @mouseleave="startAlarmScroll">
            <div class="disposal-panel__header">
              <span class="disposal-panel__title">积水事件处置</span>
            </div>
            <div class="disposal-panel__body">
              <Transition name="carousel-fade" mode="out-in">
              <div v-if="alarmScrollData.length" :key="alarmScrollIndex">
                <div class="disposal-row-group">
                  <div class="disposal-row">
                    <span class="disposal-row__label">积水点位:</span>
                    <span class="disposal-row__value">{{ currentAlarm.riskPoint || '-' }}</span>
                  </div>
                  <div class="disposal-row">
                    <span class="disposal-row__label">所属街道:</span>
                    <span class="disposal-row__value">{{ currentAlarm.town || '-' }}</span>
                  </div>
                </div>
                <div class="disposal-row-group">
                  <div class="disposal-row">
                    <span class="disposal-row__label">积水深度:</span>
                    <span class="disposal-row__value">{{ currentAlarm.liquidLevel || '0' }}cm</span>
                  </div>
                  <div class="disposal-row">
                    <span class="disposal-row__label">积水时间:</span>
                    <span class="disposal-row__value">{{ currentAlarm.duration || '0' }}分钟</span>
                  </div>
                </div>
              </div>
              </Transition>
              <div v-if="!alarmScrollData.length" class="disposal-empty">暂无未处置事件</div>
            </div>
          </div>
        </div>

        <div class="disposal-footer" style="display: flex;justify-content: space-between; align-items: center;">
          <span style="font-size: 12px;">
            <span style="background: linear-gradient(180deg, #ffffff 0%, #bffcff 42%, #1b8991 100%); -webkit-background-clip: text; -webkit-text-fill-color: transparent;">{{ areaWarningPeriodText || '2026年第13期' }}</span>
            <span style="color: #ffd039; background: rgba(255, 208, 57, 0.15); padding: 0 4px; border-radius: 2px; margin-left: 8px;">{{ areaWarningDateRange || '[7.6-7.8]' }}</span>
          </span>
          <span class="disposal-footer__btn" @click="openDispatchIframe('flood')">调度</span>
        </div>
      </section>

      <section class="flood-warning">
        <div class="warning-row">
          <span class="warning-row__icon">▲</span>
          <span class="warning-row__text">
            {{ weatherWarningText }}
            <span v-if="weatherWarningLevel" style="color: #FFBC00;">({{ weatherWarningLevel }})</span>
          </span>
          <span class="warning-row__date">{{ weatherWarningDate }}</span>
        </div>
      </section>

      <section class="response-status">
        <div class="section-header" style="height: 34px;line-height: 34px;margin-bottom: 12px;">
          <span class="section-header__icon">◆</span>
          <span class="section-header__title">镇街叫应情况</span>
          <span class="section-header__value">
              <span>11<span style="color: #8bbce6; font-size: 12px; margin-left: 1px;">(已叫应)</span></span>
              <span style="color: #38fff5;">/11<span style="color: #8bbce6; font-size: 12px; margin-left: 1px;">(总数)</span></span>
          </span>
        </div>
        <div class="response-item" @click="openEmergencyResponse" style="cursor: pointer;">
          <span class="response-item__label">区应急响应</span>
            <span class="response-item__label" style=" color: #ffffff;
              text-align: center;
              text-shadow: 0 0 12px #ff2e2e7a;
              font-size: 14px;
              font-style: normal;
              border: 1px solid #FF2E2E;
              font-weight: 55 Regular;
              line-height: normal;">
              一级响应
              </span>
          <span class="response-item__value">
            渝中区05-1419:00:00启动I级响应
          </span>
        </div>
        <div class="response-item">
          <span class="response-item__label">镇街应急响应</span>
          <span class="response-item__value" @click="openEmergencyResponseStreet" style="cursor: pointer;">I级0个 | </span>
          <span class="response-item__value" @click="openEmergencyResponseStreet" style="cursor: pointer;">II级6个 |</span>
          <span class="response-item__value" @click="openEmergencyResponseStreet" style="cursor: pointer;">III级6个 |</span>
          <span class="response-item__value" @click="openEmergencyResponseStreet" style="cursor: pointer;">IV级6个</span>
        </div>
        <div @click="openWaterLevel" style="cursor: pointer;">
          <div style="width: 100%;height: 100px;">
            <WaterlogMapPlaceholder />
          </div>
        </div>
      </section>
      -->
    </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch, onMounted, onBeforeUnmount } from 'vue'
import axios from 'axios'
import WaterlogMapPlaceholder from './components/WaterlogMapPlaceholder'

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

const sourceName = 'BzWaterlog'

const selectedStation = ref('fotuguan')
const selectedRound = ref('latest')
const selectedCallPeriod = ref('')
const callPeriodList = ref([])

// 气象预警
const WEATHER_BASE_URL = 'http://23.99.16.179:11001/api/boot/system/weather'
const weatherWarningData = ref([])
const weatherWarningText = computed(() => {
  if (!weatherWarningData.value.length) return '暂无预警信息'
  return weatherWarningData.value[0].bt || weatherWarningData.value[0].valueResult || '暂无预警信息'
})
const weatherWarningLevel = computed(() => {
  if (!weatherWarningData.value.length) return ''
  const d = weatherWarningData.value[0]
  const levelMap = { 'RED': 'I级/特别严重', 'ORANGE': 'II级/严重', 'YELLOW': 'III级/较重', 'BLUE': 'IV级/一般' }
  const codeMap = { '1': 'I级/特别严重', '2': 'II级/严重', '3': 'III级/较重', '4': 'IV级/一般' }
  return levelMap[d.xxdj] || codeMap[d.xxdjbm] || ''
})
const weatherWarningDate = computed(() => {
  if (!weatherWarningData.value.length) return ''
  const d = weatherWarningData.value[0]
  const dateStr = d.fbsj || d.kssj || d.statisticsTime || ''
  return dateStr ? dateStr.substring(0, 10) : ''
})

const fetchWeatherWarning = async () => {
  try {
    const res = await axios.get(`${WEATHER_BASE_URL}/warning`)
    const list = res?.data?.data
    if (Array.isArray(list) && list.length) {
      weatherWarningData.value = list
    }
  } catch (error) {
    console.error('获取气象预警失败:', error)
  }
}

let refreshTimer: any = null

async function refreshAllData() {
  // 先获取轮次，设置 selectedRound
  await fetchRoundList()
  // 轮次设置后，获取期数列表，设置 selectedCallPeriod
  await fetchCallPeriodList()
  // 参数就绪后，并行获取所有依赖数据
  await Promise.all([
    fetchWeatherWarning(),
    fetchRegionalWarningStats(),
    fetchTownWarningStats(),
    fetchPointWarningStats(),
    fetchAlarmWarningStats(),
    fetchRiskPoints(),
    fetchPointWarningScrollData(),
    fetchAlarmScrollData(),
    fetchAreaWarningPeriod()
  ])
}

onMounted(async () => {
  await refreshAllData()
  // 每3分钟刷新一次接口数据
  refreshTimer = setInterval(refreshAllData, 3 * 60 * 1000)

  // 监听点位预警详情弹窗的查看摄像头事件
  eventBus.on('pointWarningDetail:openVideo', (data) => {
    const pointId = data?.pointId
    if (!pointId) return
    const point = riskPoints.value.find((p: any) => p.id === pointId)
    if (point) {
      openRiskPointVideo(point)
    } else {
      console.warn('未匹配到 pointId 对应的摄像头点位:', pointId)
    }
  })

  // 监听积水告警详情弹窗的查看摄像头事件
  eventBus.on('alarmDetail:openVideo', (data) => {
    const riskPointId = data?.riskPointId
    if (!riskPointId) return
    const point = riskPoints.value.find((p: any) => p.id === riskPointId)
    if (point) {
      openRiskPointVideo(point)
    } else {
      console.warn('未匹配到 riskPointId 对应的摄像头点位:', riskPointId)
    }
  })
})

// 区域预警统计
const WATERLOG_BASE_URL = 'http://23.99.16.179:11001/api/boot/system/waterlog'
const regionalCalledCount = ref(0)
const regionalTotalCount = ref(0)
const regionalPendingCount = ref(0)

// 镇街叫应统计
const townCalledCount = ref(0)
const townTotalCount = ref(0)
const townPendingCount = ref(0)

// 轮次信息
const roundList = ref([])
const fetchRoundList = async () => {
  try {
    const res = await axios.get(`${WATERLOG_BASE_URL}/warning/round`)
    const list = res?.data?.data
    if (Array.isArray(list)) {
      roundList.value = list
      // 默认选择 no 最大的那条（no 为字符串，按字符串比较取最大）
      if (list.length > 0) {
        const maxItem = list.reduce((max, cur) => {
          return String(cur.no) > String(max.no) ? cur : max
        }, list[0])
        selectedRound.value = maxItem.no
      }
    }
  } catch (error) {
    console.error('获取轮次信息失败:', error)
  }
}

// 获取期数列表（利用轮次的 no 值）
const fetchCallPeriodList = async () => {
  const round = selectedRound.value
  if (round == null || round === 'latest') return
  try {
    const res = await axios.get(`${WATERLOG_BASE_URL}/list/qs`, {
      params: { round }
    })
    const list = res?.data?.data
    if (Array.isArray(list)) {
      callPeriodList.value = list
      // 默认选择最新的一期（数值最大的）
      if (list.length > 0) {
        const maxPeriod = list.reduce((max, cur) => {
          return Number(cur) > Number(max) ? cur : max
        }, list[0])
        selectedCallPeriod.value = maxPeriod
      }
    }
  } catch (error) {
    console.error('获取期数列表失败:', error)
  }
}

// 轮次变化时重新获取期数列表、雨前清掏/积水处置统计、轮播数据、内涝点位
watch(selectedRound, () => {
  fetchCallPeriodList()
  fetchPointWarningStats()
  fetchAlarmWarningStats()
  fetchPointWarningScrollData()
  fetchAlarmScrollData()
  fetchRiskPoints()
  fetchRegionalWarningStats()
  fetchTownWarningStats()
})

// 期数变化时重新获取区级和镇街统计数据
watch(selectedCallPeriod, () => {
  if (!selectedCallPeriod.value) return
  fetchRegionalWarningStats()
  fetchTownWarningStats()
})

// 区域预警期数信息
const areaWarningPeriodText = ref('')
const areaWarningDateRange = ref('')

const fetchAreaWarningPeriod = async () => {
  try {
    const res = await axios.get(`${WATERLOG_BASE_URL}/area/early/warning`, {
      params: { pageNumber: 1, pageSize: 1 }
    })
    const records = res?.data?.data?.records || []
    if (records.length) {
      const latest = records[0]
      const year = latest.warningPeriodStart ? latest.warningPeriodStart.substring(0, 4) : '2026'
      const periods = latest.periodsNumber || '-'
      areaWarningPeriodText.value = `${year}年第${periods}期`
      if (latest.warningPeriodStart && latest.warningPeriodEnd) {
        const startMonth = latest.warningPeriodStart.substring(5, 7).replace(/^0/, '')
        const startDay = latest.warningPeriodStart.substring(8, 10).replace(/^0/, '')
        const endMonth = latest.warningPeriodEnd.substring(5, 7).replace(/^0/, '')
        const endDay = latest.warningPeriodEnd.substring(8, 10).replace(/^0/, '')
        areaWarningDateRange.value = `[${startMonth}.${startDay}-${endMonth}.${endDay}]`
      }
    }
  } catch (error) {
    console.error('获取区域预警期数失败:', error)
  }
}

// 雨前预警统计
const pointWarningPendingCount = ref(0)
const pointWarningTotalCount = ref(0)

// 积水预警统计
const alarmWarningPendingCount = ref(0)
const alarmWarningTotalCount = ref(0)

const fetchRegionalWarningStats = async () => {

  if (!selectedCallPeriod.value){
      regionalCalledCount.value = 0
      regionalTotalCount.value = 0
      regionalPendingCount.value = 0
  }
  try {
    const params = { qs: selectedCallPeriod.value }
    const res = await axios.get(`${WATERLOG_BASE_URL}/three/response/statistics`, { params })
    const d = res?.data?.data
    if (d) {
      const townData = d['2'] || {}
      regionalCalledCount.value = townData.solved || 0
      regionalTotalCount.value = townData.total || 0
      regionalPendingCount.value = (townData.unsolve == null || townData.unsolve === undefined) ? 0 : townData.unsolve
    }
  } catch (error) {
    console.error('获取区域预警统计失败:', error)
  }
}

const fetchTownWarningStats = async () => {
  if (!selectedCallPeriod.value){
      townCalledCount.value = 0
      townTotalCount.value = 0
      townPendingCount.value = 0
     return
  }
  try {
    const params = { qs: selectedCallPeriod.value }
    const res = await axios.get(`${WATERLOG_BASE_URL}/three/response/statistics`, { params })
    const d = res?.data?.data
    if (d) {
      const townData = d['3'] || {}
      townCalledCount.value = townData.solved || 0
      townTotalCount.value = townData.total || 0
      townPendingCount.value = (townData.unsolve == null || townData.unsolve === undefined) ? 0 : townData.unsolve
    }
  } catch (error) {
    console.error('获取镇街预警统计失败:', error)
  }
}

const fetchPointWarningStats = async () => {
  try {
    const params = { round: selectedRound.value }
    const res = await axios.get(`${WATERLOG_BASE_URL}/dot/early/warning/statistics`, { params })
    const d = res?.data?.data
    if (d) {
      pointWarningPendingCount.value = d.unsolve || 0
      pointWarningTotalCount.value = d.total || 0
    }
  } catch (error) {
    console.error('获取雨前预警统计失败:', error)
  }
}

const fetchAlarmWarningStats = async () => {
  try {
    const params = { round: selectedRound.value }
    const res = await axios.get(`${WATERLOG_BASE_URL}/disposition/early/warning/statistics`, { params })
    const d = res?.data?.data
    if (d) {
      alarmWarningPendingCount.value = d.unsolve || 0
      alarmWarningTotalCount.value = d.total || 0
    }
  } catch (error) {
    console.error('获取积水预警统计失败:', error)
  }
}

// 内涝风险点位
const riskPoints = ref([])
const riskPointsLoading = ref(true)
const riskSlideIndex = ref(0)
let riskSlideTimer = null

// 预加载1~7图片
const riskPointImages = []
for (let i = 1; i <= 7; i++) {
  try {
    riskPointImages.push(require(`./img/${i}.png`))
  } catch (e) {
    // fallback
  }
}

// 根据点位id取模分配图片
function getRiskPointImg(point) {
  if (!riskPointImages.length) return ''
  const idx = point.id ? Math.abs(hashCode(String(point.id))) % riskPointImages.length : 0
  return riskPointImages[idx]
}

function hashCode(str) {
  let hash = 0
  for (let i = 0; i < str.length; i++) {
    hash = ((hash << 5) - hash) + str.charCodeAt(i)
    hash |= 0
  }
  return hash
}

// 当前轮播展示的3个点位
const currentRiskSlide = computed(() => {
  const list = riskPoints.value
  if (!list.length) return []
  const start = riskSlideIndex.value * 3
  return list.slice(start, start + 3)
})

// 总轮播页数
const riskSlideTotal = computed(() => Math.ceil(riskPoints.value.length / 3))

// 切换下一组
function nextRiskSlide() {
  if (riskSlideTotal.value <= 1) return
  riskSlideIndex.value = (riskSlideIndex.value + 1) % riskSlideTotal.value
}

// 启动轮播
function startRiskSlide() {
  stopRiskSlide()
  if (riskSlideTotal.value > 1) {
    riskSlideTimer = setInterval(nextRiskSlide, 5000)
  }
}

function stopRiskSlide() {
  if (riskSlideTimer) {
    clearInterval(riskSlideTimer)
    riskSlideTimer = null
  }
}

async function fetchRiskPoints() {
  riskPointsLoading.value = true
  try {
    const res = await axios.get(`${WATERLOG_BASE_URL}/risk/point`)
    const list = res?.data?.data
    if (Array.isArray(list)) {
      riskPoints.value = list
      riskSlideIndex.value = 0
      startRiskSlide()
    } else {
      riskPoints.value = []
    }
  } catch (error) {
    console.error('获取内涝风险点位失败:', error)
    riskPoints.value = []
  } finally {
    riskPointsLoading.value = false
  }
}

// 点击风险点位卡片 — 调用carema接口获取详情
async function openRiskPointVideo(point) {
  try {
    const res = await axios.get(`${WATERLOG_BASE_URL}/risk/carema`, {
      params: { pointId: point.id }
    })
    const data = res?.data?.data
    if (!data || !Array.isArray(data) || !data.length) {
      console.warn('风险点位摄像头接口返回为空')
      return
    }
    const info = data[0]

    // 从devicesInfo提取摄像头编码（参考WaterlogRiskDetail组件，提取code字段）
    const deviceIds = []
    try {
      const raw = info.devicesInfo
      if (raw) {
        const devices = typeof raw === 'string' ? JSON.parse(raw) : raw
        if (Array.isArray(devices)) {
          devices.forEach(item => {
            if (item.code) deviceIds.push(item.code)
          })
        }
      }
    } catch (e) {
      console.error('解析devicesInfo失败:', e)
    }

    // 组装中文键值对数据
    const payload = {
      '设备编码': info.id || '-',
      '所属区县': info.county || '-',
      '感知类型': point.pointRiskType || '-',
      '设备名称': info.name || '-',
      '所属单位': info.community || '-',
      '设备风险点': info.riskName || '-',
      '设备状态': point.pendingRemediation === '0' ? '启用' : point.pendingRemediation === '1' ? '禁用' : '-',
      deviceIds
    }

    console.log('点击风险点位传递的数据:', payload)
    eventBus.emit('YZVideoPop:openPop', payload)
  } catch (error) {
    console.error('获取风险点位摄像头信息失败:', error)
  }
}

// 点击雨前清掏图片盒子 — 通过 pointId 匹配风险点位后查看摄像头
function openPointWarningVideo() {
  const point = currentPointWarningRiskPoint.value
  if (!point) {
    console.warn('未匹配到对应的摄像头点位')
    return
  }
  openRiskPointVideo(point)
}

// ============ 雨前预警循环滚动（基于预警记录） ============
const pointWarningScrollIndex = ref(0)
let pointWarningScrollTimer: any = null

// 雨前预警记录
const pointWarningScrollData = ref<any[]>([])

// 当前轮播的预警记录
const currentPointWarning = computed(() => {
  const list = pointWarningScrollData.value
  if (!list.length) return {}
  return list[pointWarningScrollIndex.value % list.length] || {}
})

// 当前预警记录匹配的内涝点位（通过 pointId 匹配，用于展示图片/时间）
const currentPointWarningRiskPoint = computed(() => {
  const record = currentPointWarning.value
  if (!record || !record.pointId) return null
  return riskPoints.value.find((p: any) => p.id === record.pointId) || null
})

// 雨前预警状态映射
function getWarningStatusText(status: string) {
  const map: Record<string, string> = { '0': '待响应', '1': '响应中', '2': '已结束' }
  return map[status] || '待响应'
}

function getWarningStatusClass(status: string) {
  const map: Record<string, string> = { '0': 'pending', '1': 'responding', '2': 'success' }
  return map[status] || 'pending'
}

// 雨前预警轮播图片（基于匹配的内涝点位）
const currentPointWarningImg = computed(() => {
  const point = currentPointWarningRiskPoint.value
  if (point) return getRiskPointImg(point)
  return require('./img/1.png')
})

// 雨前预警轮播时间（基于匹配的内涝点位）
const currentPointWarningTime = computed(() => {
  const point = currentPointWarningRiskPoint.value
  if (!point) return '-'
  const time = point.createTime || point.updateTime || ''
  if (!time) return '-'
  return time.substring(5, 16).replace('T', ' ')
})

async function fetchPointWarningScrollData() {
  if (selectedRound.value == null || selectedRound.value === 'latest') return
  try {
    const params: any = { pageNumber: 1, pageSize: 100, round: selectedRound.value }
    const res = await axios.get(`${WATERLOG_BASE_URL}/dot/early/warning`, {
      params
    })
    // 兼容多种数据结构
    const records = res?.data?.records || res?.data?.data?.records || res?.data?.data || res?.data || []
    pointWarningScrollData.value = Array.isArray(records) ? records : []
    console.log('雨前预警滚动数据:', pointWarningScrollData.value)
    // 预警记录更新后重启轮播
    pointWarningScrollIndex.value = 0
    startPointWarningScroll()
  } catch (err) {
    console.error('获取雨前预警滚动数据失败:', err)
    pointWarningScrollData.value = []
    pointWarningScrollIndex.value = 0
    startPointWarningScroll()
  }
}

function startPointWarningScroll() {
  stopPointWarningScroll()
  if (pointWarningScrollData.value.length > 1) {
    pointWarningScrollTimer = setInterval(() => {
      pointWarningScrollIndex.value = (pointWarningScrollIndex.value + 1) % pointWarningScrollData.value.length
    }, 5000)
  }
}

function stopPointWarningScroll() {
  if (pointWarningScrollTimer) {
    clearInterval(pointWarningScrollTimer)
    pointWarningScrollTimer = null
  }
}

// ============ 积水预警循环滚动 ============
const alarmScrollData = ref<any[]>([])
const alarmScrollIndex = ref(0)
let alarmScrollTimer: any = null

const currentAlarm = computed(() => {
  const data = alarmScrollData.value
  if (!data.length) return {}
  return data[alarmScrollIndex.value % data.length] || {}
})

// 当前积水预警记录匹配的内涝点位（通过 riskPointId 匹配，用于展示图片/时间）
const currentAlarmRiskPoint = computed(() => {
  const record = currentAlarm.value
  if (!record || !record.riskPointId) return null
  return riskPoints.value.find((p: any) => p.id === record.riskPointId) || null
})

// 积水预警轮播图片（基于匹配的内涝点位）
const currentAlarmImg = computed(() => {
  const point = currentAlarmRiskPoint.value
  if (point) return getRiskPointImg(point)
  return require('./img/2.png')
})

// 积水预警轮播时间（基于匹配的内涝点位）
const currentAlarmTime = computed(() => {
  const point = currentAlarmRiskPoint.value
  if (!point) return '-'
  const time = point.createTime || point.updateTime || ''
  if (!time) return '-'
  return time.substring(5, 16).replace('T', ' ')
})

// 点击积水处置图片 — 通过 riskPointId 匹配风险点位后查看摄像头
function openAlarmVideo() {
  const point = currentAlarmRiskPoint.value
  if (!point) {
    console.warn('未匹配到对应的摄像头点位')
    return
  }
  openRiskPointVideo(point)
}

async function fetchAlarmScrollData() {
  try {
    const res = await axios.get(`${WATERLOG_BASE_URL}/disposition/early/warning`, {
      params: { pageNumber: 1, pageSize: 100,round: selectedRound.value }
    })
    const records = res?.data?.data?.records || []
    // 积水预警所有事件都展示
    alarmScrollData.value = records
    alarmScrollIndex.value = 0
    startAlarmScroll()
  } catch (err) {
    console.error('获取积水预警滚动数据失败:', err)
    alarmScrollData.value = []
  }
}

function startAlarmScroll() {
  stopAlarmScroll()
  if (alarmScrollData.value.length > 1) {
    alarmScrollTimer = setInterval(() => {
      alarmScrollIndex.value = (alarmScrollIndex.value + 1) % alarmScrollData.value.length
    }, 5000)
  }
}

function stopAlarmScroll() {
  if (alarmScrollTimer) {
    clearInterval(alarmScrollTimer)
    alarmScrollTimer = null
  }
}

// ============ 工具函数 ============
function formatWarningPeriod(start: string, end: string) {
  if (!start && !end) return '-'
  const s = start ? start.substring(0, 16) : ''
  const e = end ? end.substring(0, 16) : ''
  if (s && e) return s + ' 至 ' + e
  return s || e || '-'
}

function getLevelText(level: string) {
  const map: Record<string, string> = { '1': '一级', '2': '二级', '3': '三级', '4': '四级' }
  return map[level] || level || '-'
}

function getLevelClass(level: string) {
  const map: Record<string, string> = { '1': 'level-one', '2': 'level-two', '3': 'level-three', '4': 'level-four' }
  return map[level] || ''
}

const rainData = [
  { time: '04:00', height: 35 },
  { time: '05:00', height: 60 },
  { time: '06:00', height: 80 },
  { time: '07:00', height: 70 },
  { time: '08:00', height: 85 },
  { time: '09:00', height: 75 },
  { time: '10:00', height: 55 }
]

const rainChartOption = computed(() => ({
  grid: {
    left: 28,
    right: 0,
    top: 6,
    bottom: 22,
    containLabel: false
  },
  xAxis: {
    type: 'category',
    data: rainData.map(item => item.time),
    axisTick: {
      show: false
    },
    axisLine: {
      lineStyle: {
        color: 'rgba(139, 188, 230, 0.45)'
      }
    },
    axisLabel: {
      color: '#d6e8ff',
      fontSize: 12,
      interval: 0,
      fontFamily: 'BzDDIN, Arial, sans-serif'
    }
  },
  yAxis: {
    type: 'value',
    min: 0,
    max: 100,
    interval: 40,
    axisLabel: {
      show: true,
      color: '#8bbce6',
      fontSize: 12,
      fontFamily: 'BzDDIN, Arial, sans-serif'
    },
    axisTick: {
      show: false
    },
    axisLine: {
      show: false
    },
    splitLine: {
      lineStyle: {
        color: 'rgba(31, 117, 221, 0.3)'
      }
    }
  },
  series: [
    {
      type: 'bar',
      data: rainData.map(item => item.height),
      barWidth: 16,
      itemStyle: {
        borderRadius: [2, 2, 0, 0],
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            { offset: 0, color: '#04bcfa' },
            { offset: 1, color: '#0454cb' }
          ]
        }
      }
    }
  ],
  tooltip: {
    show: false
  }
}))

const dispatchIframeUrlMap = {
  weather: 'http://23.99.16.179:11001/api/boot/system/weather/token',
  waterlog: 'http://23.99.16.179:11001/api/boot/system/waterlog/token',
  flood: 'http://23.99.16.179:11001/api/boot/system/flood/token'
}

function getDispatchIframeUrl(data) {
  const result = data?.data ?? data
  if (typeof result === 'string') return result
  if (!result || typeof result !== 'object') return ''

  return result.url || result.iframeUrl || result.src || result.link || result.token || ''
}

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

function openRegionalWarningDetail() {
  eventBus.emit('CLOSE_DIA', null)
  eventBus.emit('OPEN_DIA', { diaName: 'waterlog-regional-warning-detail', qs: selectedCallPeriod.value, round: selectedRound.value })
}

// 镇街点击 — 与区级一致，打开同一个弹窗
function openTownWarningDetail() {
  openRegionalWarningDetail()
}

async function openDispatchIframe(type) {
  const requestUrl = dispatchIframeUrlMap[type]
  if (!requestUrl) return

  try {
    const res = await axios.get(requestUrl)
    const url = getDispatchIframeUrl(res?.data)
    if (!url) return

    eventBus.emit('CLOSE_DIA', null)
    eventBus.emit('OPEN_IFRAME_DIA', { url, type })
  } catch (error) {
    console.error('获取调度页面地址失败:', error)
  }
}

function openWaterLevel() {
  eventBus.emit('CLOSE_DIA', null)
  eventBus.emit('OPEN_DIA', { diaName: 'water-level' })
}

function openEmergencyResponseStreet() {
  eventBus.emit('CLOSE_DIA', null)
  eventBus.emit('OPEN_DIA', { diaName: 'emergency-response-street' })
}

function openEmergencyResponse() {
  eventBus.emit('CLOSE_DIA', null)
  eventBus.emit('OPEN_DIA', { diaName: 'emergency-response' })
}

function openWeatherAlert() {
  eventBus.emit('CLOSE_DIA', null)
  eventBus.emit('OPEN_DIA', { diaName: 'weather-alert' })
}

function openRainfallMonitor() {
  eventBus.emit('CLOSE_DIA', null)
  eventBus.emit('OPEN_DIA', { diaName: 'rainfall-monitor' })
}

function openDeviceDetail() {
  eventBus.emit('CLOSE_DIA', null)
  eventBus.emit('OPEN_DIA', { diaName: 'waterlog-device-detail' })
}

function openPointWarningList() {
  eventBus.emit('CLOSE_DIA', null)
  eventBus.emit('OPEN_DIA', { diaName: 'waterlog-point-warning-list', round: selectedRound.value })
}

function openPointWarningDetail() {
  eventBus.emit('CLOSE_DIA', null)
  eventBus.emit('OPEN_DIA', { diaName: 'waterlog-point-warning-detail', round: selectedRound.value })
}

function openAlarmEventList() {
  eventBus.emit('CLOSE_DIA', null)
  eventBus.emit('OPEN_DIA', { diaName: 'waterlog-alarm-event-list', round: selectedRound.value })
}

function openAlarmDetail() {
  eventBus.emit('CLOSE_DIA', null)
  eventBus.emit('OPEN_DIA', { diaName: 'waterlog-alarm-detail', round: selectedRound.value })
}

onBeforeUnmount(() => {
  stopRiskSlide()
  stopPointWarningScroll()
  stopAlarmScroll()
  eventBus.off('pointWarningDetail:openVideo')
  if (refreshTimer) {
    clearInterval(refreshTimer)
    refreshTimer = null
  }
})
</script>

<script lang="ts">
export default {
  name: "BzWaterlog",
  version: "1.0.1",
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

.bz-waterlog {
  width: 510px;
  height: 344px;
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
  overflow: hidden;
  // background: #061d35;
  font-family: 'BzAlibabaPuHuiTi', Microsoft YaHei, PingFang SC, Arial, sans-serif;
}

.bz-waterlog__body {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.side-tabs {
  width: 24px;
  height: 100%;
  flex-shrink: 0;
  display: flex;
  background: linear-gradient(180deg, #19809f 0%, #106b85 50%, #0b7453 100%);
}

.side-tabs__col {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  height: 100%;
  padding: 16px 0;
  box-sizing: border-box;
}

.side-tabs__item {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  writing-mode: vertical-rl;
  letter-spacing: 2px;
  font-size: 14px;
  font-weight: 500;
  color: #fff;
  position: relative;
}

.side-tabs__item--call {
  color: #8fffff;
  text-shadow: 0 0 4px rgba(143, 255, 255, 0.6);
}

.side-tabs__item--call::after,
.side-tabs__item--response::after {
  content: '';
  position: absolute;
  left: 50%;
  bottom: 50%;
  width: 70px;
  height: 75px;
  background: url('./img/jiantou.png') center / contain no-repeat;
  transform: translate(-50%, 100%);
  z-index: 2;
  pointer-events: none;
}

.side-tabs__item--response {
  color: #8fffff;
  text-shadow: 0 0 4px rgba(143, 255, 255, 0.6);
}

.side-tabs__item--disposal {
  color: #7cffb0;
  text-shadow: 0 0 4px rgba(124, 255, 176, 0.6);
}

.content {
  flex: 1;
  height: 100%;
  padding: 6px 8px;
  box-sizing: border-box;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.top-dispatch-row {
  display: flex;
  justify-content: flex-end;
  height: 24px;
  flex-shrink: 0;
  padding-right: 8px;
  align-items: center;
}

.top-dispatch-btn {
  padding: 0 8px;
  color: #fff;
  background: rgba(26, 120, 194, 0.8);
  border: 1px solid #3bb3ff;
  border-radius: 3px;
  font-size: 12px;
  cursor: pointer;
  line-height: 18px;
}

.content::-webkit-scrollbar {
  width: 0;
  height: 0;
}

/* 叫应板块 */
.call-section {
  width: 476px;
  height: 98px;
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-bottom: 4px;
}

.round-row {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 24px;
  flex-shrink: 0;
}

.round-left {
  display: flex;
  align-items: center;
  gap: 6px;
  white-space: nowrap;
}

.round-label {
  font-size: 16px;
  font-weight: 700;
  background: linear-gradient(180deg, #ffffff 0%, #8fffff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.round-select {
  height: 24px;
  width: 280px;
  padding: 0 28px 0 8px;
  border: 1px solid rgba(66, 184, 255, 0.9);
  border-radius: 4px;
  background: rgba(5, 33, 62, 0.8);
  color: #c9e6ff;
  font-size: 14px;
  cursor: pointer;
  outline: none;
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 24 24' fill='none' stroke='rgba(139, 188, 230, 0.9)' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpolyline points='6 9 12 15 18 9'%3E%3C/polyline%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 8px center;
  background-size: 12px;
  white-space: nowrap;
}

.round-select option {
  background-color: rgba(5, 33, 62, 0.98);
  color: #fff;
  padding: 6px 8px;
  font-size: 14px;
}

.round-right {
  font-size: 16px;
  color: #e8f8ff;
  white-space: nowrap;
}

/* 临灾叫应+统计 容器 */
.call-content {
  width: 476px;
  height: 80px;
  border: 1px solid rgba(66, 184, 255, 0.6);
  border-radius: 4px;
  padding: 6px 8px;
  box-sizing: border-box;
  background: url('./img/bg_left_top01.png') center / cover no-repeat;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

/* 临灾叫应行 */
.call-row {
  width: 100%;
  display: flex;
  align-items: center;
  height: 24px;
  flex-shrink: 0;
}

.call-left {
  display: flex;
  align-items: center;
  gap: 6px;
  white-space: nowrap;
}

.call-label {
  font-size: 16px;
  font-weight: 700;
  background: linear-gradient(180deg, #ffffff 0%, #8fffff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.call-select {
  height: 24px;
  width: 360px;
  padding: 0 28px 0 8px;
  border: 1px solid rgba(66, 184, 255, 0.9);
  border-radius: 4px;
  background: rgba(5, 33, 62, 0.8);
  color: #c9e6ff;
  font-size: 14px;
  cursor: pointer;
  outline: none;
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 24 24' fill='none' stroke='rgba(139, 188, 230, 0.9)' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpolyline points='6 9 12 15 18 9'%3E%3C/polyline%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 8px center;
  background-size: 12px;
  white-space: nowrap;
}

.call-select option {
  background-color: rgba(5, 33, 62, 0.98);
  color: #fff;
  padding: 6px 8px;
  font-size: 14px;
}

/* 统计行 */
.stats-row {
  flex: 1;
  width: 100%;
  display: flex;
  gap: 12px;
}

.stat-item {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 10px;
  background: rgba(10, 70, 112, 0.7);
  border-left: 3px solid #3bb3ff;
  border-radius: 2px;
  cursor: pointer;
}

.stat-label {
  font-size: 16px;
  font-weight: 700;
  background: linear-gradient(180deg, #ffffff 0%, #8fffff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin-right: 16px;
}

.stat-pending {
  font-size: 20px;
  font-weight: 700;
  color: #ffb74d;
  font-family: 'BzDDIN', Arial, sans-serif;
}

.stat-pending-label {
  font-size: 14px;
  color: #e8f8ff;
  margin-right: 12px;
}

.stat-total {
  font-size: 20px;
  font-weight: 700;
  background: linear-gradient(180deg, #ffffff 0%, #38fff5 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  font-family: 'BzDDIN', Arial, sans-serif;
}

.stat-total-label {
  font-size: 14px;
  color: #e8f8ff;
}

/* 响应板块 */
.resp-section {
  width: 476px;
  height: 98px;
}

.resp-content {
  width: 100%;
  height: 100%;
  border: 1px solid rgba(66, 184, 255, 0.6);
  border-radius: 4px;
  padding: 8px 10px;
  box-sizing: border-box;
  background: url('./img/bg_left_top02.png') center / cover no-repeat;
  display: flex;
  gap: 8px;
  overflow: visible;
}

/* 左侧 */
.resp-left {
  flex: 1;
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 0;
}

.resp-title {
  display: flex;
  align-items: center;
  gap: 6px;
  height: 28px;
  flex-shrink: 0;
  cursor: pointer;
}

.play-icon {
  color: #66ccff;
  font-size: 14px;
}

.title-text {
  font-size: 18px;
  font-weight: 700;
  background: linear-gradient(180deg, #ffffff 0%, #8fffff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.resp-stats {
  display: flex;
  gap: 8px;
  height: 28px;
  flex-shrink: 0;
}

.resp-pending-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 90px;
  height: 100%;
  padding: 0 6px;
  box-sizing: border-box;
  background: url('./img/bg_list_data.png') center / 100% 100% no-repeat;
}

.pending-left {
  font-size: 16px;
  color: #e8f8ff;
}

.pending-num {
  font-size: 20px;
  font-weight: 700;
  background: linear-gradient(180deg, #fff7d6 0%, #ff8a4d 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  font-family: 'BzDDIN', Arial, sans-serif;
}

.resp-total-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 90px;
  height: 100%;
  padding: 0 6px;
  box-sizing: border-box;
  background: url('./img/bg_list_data_1.png') center / 100% 100% no-repeat;
}

.total-left {
  font-size: 16px;
  color: #e8f8ff;
}

.total-num {
  font-size: 20px;
  font-weight: 700;
  background: linear-gradient(180deg, #ffffff 0%, #38fff5 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  font-family: 'BzDDIN', Arial, sans-serif;
}

/* 右侧图片 */
.resp-info-row {
  display: flex;
  align-items: center;
  gap: 2px;
  height: 22px;
  flex-shrink: 0;
  white-space: nowrap;
  overflow: visible;
}

.info-label {
  font-size: 12px;
  color: #c9e6ff;
  white-space: nowrap;
}

.info-value {
  font-size: 12px;
  color: #e8f8ff;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 120px;
}

.info-empty {
  font-size: 12px;
  color: #8bbce6;
}

.status-tag {
  padding: 0 4px;
  height: 18px;
  line-height: 18px;
  border: 1px solid #4caf50;
  color: #81c784;
  font-size: 12px;
  font-weight: 600;
  white-space: nowrap;
  display: inline-flex;
  align-items: center;
  justify-content: center;

  &.pending {
    border-color: #ffb74d;
    color: #ffb74d;
  }

  &.responding {
    border-color: #42a5f5;
    color: #64b5f6;
  }

  &.success {
    border-color: #4caf50;
    color: #81c784;
  }
}

.level-tag {
  padding: 0 4px;
  height: 18px;
  line-height: 18px;
  border: 1px solid #4caf50;
  color: #81c784;
  font-size: 12px;
  font-weight: 600;
  white-space: nowrap;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.resp-img-box {
  width: 120px;
  flex-shrink: 0;
  height: 100%;
  display: flex;
  flex-direction: column;
  border: 1px solid rgba(66, 184, 255, 0.5);
  border-radius: 2px;
  overflow: hidden;
  cursor: pointer;
}

.resp-img {
  width: 100%;
  flex: 1;
  object-fit: cover;
  min-height: 0;
}

.img-time {
  height: 20px;
  line-height: 20px;
  text-align: center;
  color: #fff;
  font-size: 12px;
  background: rgba(0,0,0,0.6);
  white-space: nowrap;
  flex-shrink: 0;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 28px;
  margin-bottom: 6px;
  padding: 0 8px;
  background: rgba(5, 33, 62, 0.6);
  border: 1px solid rgba(31, 117, 221, 0.5);
  border-radius: 3px;
}

.section-header-left {
  display: flex;
  align-items: center;
  gap: 6px;
}

.section-header__icon {
  color: #ffd039;
  font-size: 10px;
  margin-right: 6px;
}

.section-header__title {
  color: #e8f8ff;
  font-size: 14px;
  font-weight: 700;
  flex-shrink: 0;
}

.rain-select {
  width: 110px;
  height: 22px;
  padding: 0 6px;
  border: 1px solid rgba(31, 117, 221, 0.4);
  border-radius: 3px;
  background: rgba(5, 33, 62, 0.5);
  color: #fff;
  font-size: 12px;
  cursor: pointer;
  flex-shrink: 0;
  outline: none;
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='10' height='10' viewBox='0 0 24 24' fill='none' stroke='rgba(139, 188, 230, 0.6)' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpolyline points='6 9 12 15 18 9'%3E%3C/polyline%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 4px center;
  background-size: 10px;
  padding-right: 22px;
}

.rain-select:hover {
  border-color: rgba(31, 117, 221, 0.8);
  background-color: rgba(5, 33, 62, 0.8);
}

.rain-select:focus {
  border-color: #3bb3ff;
  box-shadow: 0 0 0 1px rgba(59, 179, 255, 0.2);
}

.rain-select option {
  background-color: rgba(5, 33, 62, 0.98);
  color: #fff;
  padding: 6px 8px;
}

.section-header__btn {
  padding: 0px 6px;
  color: #fff;
  background: rgba(26, 120, 194, 0.8);
  border: 1px solid #3bb3ff;
  border-radius: 3px;
  font-size: 12px;
  cursor: pointer;
}

.section-header__value {
  color: #ffd039;
  font-family: 'BzDDIN', Arial, sans-serif;
  font-size: 16px;
  font-weight: 800;
}

.weather-warning {
  margin-bottom: 4px;
}

.warning-row {
  display: flex;
  align-items: center;
  padding: 4px;
  background: rgba(5, 33, 62, 0.4);
  border: 1px solid rgba(31, 117, 221, 0.3);
  border-radius: 3px;
}

.warning-row__icon {
  color: #ff6b6b;
  font-size: 14px;
  margin-right: 8px;
}

.warning-row__text {
  flex: 1;
  color: #e8f8ff;
  font-size: 13px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.warning-row__date {
  color: #9bc9e8;
  font-size: 12px;
  margin-left: 8px;
}

.rain-monitor {
  margin-bottom: 4px;
}

.rain-info-bar {
  display: flex;
  justify-content: space-between;
  padding: 6px 8px;
  background: rgba(5, 33, 62, 0.4);
  border: 1px solid rgba(31, 117, 221, 0.3);
  border-radius: 3px;
  margin-bottom: 4px;
}

.rain-info-bar__label {
  color: #9bc9e8;
  font-size: 12px;
}

.rain-info-bar__title {
  color: #ffffff;
  font-size: 12px;
}

.rain-info-bar__value {
  color: #38fff5;
  font-size: 13px;
  font-weight: 700;
}

.rain-chart {
  height: 80px;
  width:450px;
  padding: 8px;
  background: rgba(5, 33, 62, 0.4);
  border: 1px solid rgba(31, 117, 221, 0.3);
  border-radius: 3px;
}

.waterlog-area {
  margin-bottom: 3px;
}

.area-tabs-row {
  display: flex;
  gap: 8px;
  margin-bottom: 3px;
}

.area-tab {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 2px 12px;
  background: rgba(5, 33, 62, 0.6);
  border: 1px solid rgba(33, 145, 244, 0.5);
  border-radius: 3px;
  cursor: pointer;
}

.area-tab__label {
  color: #dff4ff;
  font-size: 14px;
  font-weight: 700;
}

.area-tab__value {
  color: #ffd039;
  font-family: 'BzDDIN', Arial, sans-serif;
  font-size: 18px;
  font-weight: 800;
}

.area-tabs-row__btn {
  padding: 0px 6px;
  color: #fff;
  background: rgba(26, 120, 194, 0.8);
  border: 1px solid #3bb3ff;
  border-radius: 3px;
  font-size: 12px;
  cursor: pointer;
  align-self: center;
}

.waterlog-cards {
  display: flex;
  gap: 8px;
}

.waterlog-card {
  flex: 1;
  height: 94px;
  display: flex;
  flex-direction: column;
  background: rgba(10, 70, 112, 0.72);
  border: 1px solid rgba(84, 205, 255, 0.5);
  border-radius: 3px;
  overflow: hidden;
  cursor: pointer;
}

.waterlog-card__img {
  height: 74px;
  background: linear-gradient(180deg, rgba(4, 188, 250, 0.3) 0%, rgba(4, 84, 203, 0.2) 100%);
  overflow: hidden;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    display: block;
  }
}

.waterlog-card__name {
  height: 20px;
  line-height: 20px;
  padding: 0 6px;
  color: #fff;
  background: rgba(6, 47, 78, 0.8);
  font-size: 11px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.waterlog-card--loading .waterlog-card__img,
.waterlog-card--empty .waterlog-card__img {
  display: flex;
  align-items: center;
  justify-content: center;
}

.loading-text {
  color: #8bbce6;
  font-size: 11px;
  animation: blink 1.2s ease-in-out infinite;
}

.empty-text {
  color: #8bbce6;
  font-size: 11px;
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.3; }
}

.warning-disposal {
  margin-bottom: 3px;
}

.disposal-tabs-row {
  display: flex;
  gap: 8px;
  margin-bottom: 6px;
}

.disposal-tab {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 2px 12px;
  background: rgba(5, 33, 62, 0.6);
  border: 1px solid rgba(33, 145, 244, 0.5);
  border-radius: 3px;
}

.disposal-tab--clickable {
  cursor: pointer;
}

.disposal-tab__icon {
  color: #38fff5;
  font-size: 10px;
}

.disposal-tab__label {
  flex: 1;
  color: #dff4ff;
  font-size: 14px;
  font-weight: 700;
}

.disposal-tab__value {
  color: #ffd039;
  font-family: 'BzDDIN', Arial, sans-serif;
  font-size: 16px;
  font-weight: 800;
}

.disposal-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 4px;
}

.disposal-panel {
  background: rgba(5, 33, 62, 0.5);
  border: 1px solid rgba(33, 145, 244, 0.3);
  border-radius: 3px;
  overflow: hidden;
  display: flex;
  height: 74px;
  width: 100%;
}

.disposal-panel--clickable {
  cursor: pointer;
}

.disposal-panel__header {
  display: flex;
  align-items: center;
  padding: 2px 10px;
  color: #fff;
  background: linear-gradient(90deg, rgba(17, 84, 139, 0.8) 0%, rgba(6, 47, 78, 0.6) 100%);
  font-size: 13px;
  font-weight: 700;
}

.disposal-panel__icon {
  color: #ffd039;
  font-size: 10px;
  margin-right: 6px;
}

.disposal-panel__title {
  width: 30px;
  color: #bceeff;
  text-align: center;
  word-break: break-all;
  line-height: 18px;
  padding: 8px 2px;
}

.disposal-panel__body {
  flex: 1;
  padding: 8px 10px;
  width: calc(100% - 30px);
  display: flex;
  flex-direction: column;
  justify-content: center;
  overflow: hidden;
}

.disposal-row-group {
  display: flex;
  gap: 16px;
  margin-bottom: 6px;

  &:last-child {
    margin-bottom: 0;
  }
}

.disposal-row {
  flex: 1;
  display: flex;
  font-size: 12px;
  min-width: 0;
}

.disposal-row--full {
  flex: 1;
  width: 100%;
}

.disposal-row__label {
  color: #8bbce6;
  white-space: nowrap;
  width: 72px;
  flex-shrink: 0;
}

.disposal-row__value {
  color: #e8f8ff;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.level-tag {
  padding: 2px 10px;
  border-radius: 3px;
  font-weight: 700;
  font-size: 12px;
}

.level-four {
  color: #47dea2;
  background: rgba(71, 222, 162, 0.2);
  border: 1px solid #47dea2;
}

.level-three {
  color: #ffd039;
  background: rgba(255, 208, 57, 0.2);
  border: 1px solid #ffd039;
}

.level-two {
  color: #ff8c39;
  background: rgba(255, 140, 57, 0.2);
  border: 1px solid #ff8c39;
}

.level-one {
  color: #ff3939;
  background: rgba(255, 57, 57, 0.2);
  border: 1px solid #ff3939;
}

.disposal-empty {
  color: #8bbce6;
  font-size: 12px;
  text-align: center;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex: 1;
}

.disposal-footer {
  display: flex;
  justify-content: flex-end;
}

.disposal-footer__btn {
  padding: 0px 6px;
  color: #fff;
  background: linear-gradient(180deg, #1a78c2 0%, #0d5a96 100%);
  border: 1px solid #3bb3ff;
  border-radius: 4px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
}

.flood-warning {
  margin-bottom: 20px;
}

.response-status {
  margin-bottom: 4px;
}

.response-item {
  display: flex;
  justify-content: space-between;
  padding: 6px 10px;
  margin-bottom: 6px;
  background: rgba(5, 33, 62, 0.4);
  border: 1px solid rgba(33, 145, 244, 0.3);
  border-radius: 3px;
  font-size: 12px;

  &:last-of-type {
    margin-bottom: 10px;
  }
}

.response-item__label {
  color: #dff4ff;
  font-weight: 500;
}

.response-item__value {
  color: #e8f8ff;
}

.response-map {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
  padding: 10px;
  background: rgba(5, 33, 62, 0.4);
  border: 1px solid rgba(33, 145, 244, 0.3);
  border-radius: 3px;
}

.map-marker {
  display: flex;
  align-items: center;
  gap: 6px;
}

.map-marker__icon {
  color: #47dea2;
  font-size: 10px;
}

.map-marker__name {
  color: #e8f8ff;
  font-size: 11px;
}

.carousel-fade-enter-active,
.carousel-fade-leave-active {
  transition: transform 0.5s ease, opacity 0.5s ease;
}

.carousel-fade-enter-from {
  transform: translateY(-20px);
  opacity: 0;
}

.carousel-fade-leave-to {
  transform: translateY(20px);
  opacity: 0;
}
</style>
