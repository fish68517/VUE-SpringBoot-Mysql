<template>
  <div class="bz-zh-warn" :class="{ 'single-mode': !isTripleScreen, 'waterlog-mode': isWaterlogMode }">
    <div v-if="!isWaterlogMode" class="top-btns">
      <div class="history-warn-btn" @click="openHistoryWarn">历史预警</div>
      <div class="dispatch-btn" @click="openDispatchIframe">调度</div>
    </div>
    <!-- 单屏返回按钮（左上角） -->
    <div v-if="!isTripleScreen && singlePanelVisible && !isWaterlogMode" class="back-btn" @click="backToLeft">返回</div>

    <!-- 左侧区域 -->
    <div class="left-area" v-show="isTripleScreen || !singlePanelVisible">
      <!-- 顶部行（已注释，不再展示） -->
      <!-- <div class="top-header">
        <div class="header-group">
          <span class="location-icon">📍</span>
          <span class="text">{{ weatherNow.county }}</span>
        </div>
        <div class="divider"></div>
        <div class="header-group">
          <span class="text">{{ todayDate }}</span>
        </div>
        <div class="divider"></div>
        <div class="header-group">
          <span class="text">{{ weatherNow.temperature }}℃</span>
        </div>
        <div class="divider"></div>
        <div class="header-group">
          <img src="./img/Frame.png" alt="风力" class="wind-icon">
          <span class="text">{{ weatherNow.windy }}</span>
        </div>
      </div> -->

      <!-- 预警盒子区域 -->
      <div class="warn-boxes">
        <div
          v-for="item in warnTypes"
          v-show="!isWaterlogMode || item.key === 'rain'"
          :key="item.key"
          class="warn-card"
          :class="['bg-' + item.levelClass, { active: currentWarn === item.key }]"
          @click="handleWarnClick(item.key)"
        >
          <div class="card-title">{{ item.title }}</div>
          <div class="card-level" :class="[item.levelClass, { 'card-level--heat': item.key === 'heat' }]">
            <template v-if="item.key === 'heat'">
              <span class="heat-temperature">{{ item.temperature || '--℃' }}</span>
              <span class="heat-warning">{{ item.level }}</span>
            </template>
            <template v-else>{{ item.level }}</template>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧区域 -->
    <div class="right-area" v-show="isTripleScreen || singlePanelVisible">
      <!-- 上方预警信息卡片 -->
      <div v-if="showWeatherHeaderInInfo" class="warn-info-card warn-info-card--weather">
        <div class="top-header top-header--plain">
          <div class="header-group">
            <span class="location-icon">📍</span>
            <span class="text">{{ weatherNow.county }}</span>
          </div>
          <div class="divider"></div>
          <div class="header-group">
            <span class="text">{{ todayDate }}</span>
          </div>
          <div class="divider"></div>
          <div class="header-group">
            <span class="text">{{ weatherNow.temperature }}℃</span>
          </div>
          <div class="divider"></div>
          <div class="header-group">
            <img src="./img/Frame.png" alt="风力" class="wind-icon">
            <span class="text">{{ weatherNow.windy }}</span>
          </div>
        </div>
      </div>
      <div v-else class="warn-info-card">
        <div class="warn-title">{{ currentWarnData.title }}</div>
        <div class="warn-content">{{ currentWarnData.content }}</div>
      </div>

      <!-- 统计表格 -->
      <div class="rain-stat">
        <div class="stat-title">
          <span class="arrow">▶</span>
          {{ currentWarnData.statTitle }}
        </div>
        <div class="stat-table" :class="{ 'stat-table--heat': currentWarn === 'heat' }">
          <div class="table-row header">
            <div class="table-cell first-col">{{ currentWarnData.tableFirstCol }}</div>
            <div
              v-for="(col, idx) in currentWarnData.columns"
              :key="idx"
              class="table-cell"
              v-html="col"
            ></div>
          </div>
          <div class="table-row">
            <div class="table-cell first-col">{{ currentWarnData.tableFirstColData }}</div>
            <div
              v-for="(num, idx) in currentWarnData.numbers"
              :key="idx"
              class="table-cell"
            >
              <span class="rain-num" :class="num.color">{{ num.value }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import axios from 'axios'
import { ref, reactive, computed, watch, onMounted, onBeforeUnmount, defineProps } from 'vue'

const props = defineProps({
  chartConfig: {
    type: Object,
    required: true
  },
  bus: {
    type: Object,
    required: false
  }
})

const sourceName = 'BzZhWarn'
const OPEN_GEO_EVENT = 'OPEN_GEO_DIA'
const BASE_URL = 'http://23.99.16.179:11001'
const DISPATCH_URL = 'http://23.211.113.5/tianzi/cqmet/dash/?countyCode=500103#/map/monitor-warning'

const eventBus = {
  emit(event, data) {
    props.bus?.emit(event, { data, source: sourceName })
  }
}
// 是否三连屏模式（默认 true）；关闭则为单屏 1920*1080
const isTripleScreen = computed(() => {
  return props.chartConfig?.option?.tripleScreen !== false
})

// 非三联屏下的内涝防汛模式：只展示降雨预警
const isWaterlogMode = computed(() => {
  return !isTripleScreen.value && props.chartConfig?.option?.waterlogMode === true
})

// 单屏下右侧面板是否可见（点击分类后显示，返回后隐藏）
const singlePanelVisible = ref(false)

const formatHeaderDate = (value?: string) => {
  const date = value ? new Date(value) : new Date()
  if (Number.isNaN(date.getTime())) {
    const fallback = new Date()
    return `${fallback.getMonth() + 1}.${fallback.getDate()}`
  }
  return `${date.getMonth() + 1}.${date.getDate()}`
}

// 实时天气数据
const weatherNow = ref({
  county: '渝中区',
  datetime: '',
  temperature: '--',
  windy: '--'
})

// 预警等级映射（英文大写 → 中文 + 样式类名）
const LEVEL_MAP: Record<string, { text: string; class: string }> = {
  RED: { text: '红色', class: 'red' },
  ORANGE: { text: '橙色', class: 'orange' },
  YELLOW: { text: '黄色', class: 'yellow' },
  BLUE: { text: '蓝色', class: 'blue' },
  GREEN: { text: '绿色', class: 'green' }
}

// API键名 → 组件键名 映射
const API_KEY_MAP: Record<string, string> = {
  rain: 'rain',
  hot: 'heat',
  wind: 'wind',
  fog: 'fog',
  linghtning: 'thunder'
}

// 组件键名 → API键名 映射（反向）
const COMPONENT_KEY_MAP: Record<string, string> = {
  rain: 'rain',
  heat: 'hot',
  wind: 'wind',
  fog: 'fog',
  thunder: 'linghtning'
}

// 类型名称兜底
const TYPE_NAME_FALLBACK: Record<string, string> = {
  rain: '降雨',
  heat: '高温',
  wind: '大风',
  fog: '大雾',
  thunder: '雷电'
}

// 存储API返回的预警数据
const weatherWarningData = ref<Record<string, any[]>>({})

// 预警类型列表
const warnTypes = ref([
  { key: 'rain', title: '降雨预警', level: '暂无预警', levelClass: 'blue' },
  { key: 'heat', title: '高温预警', level: '暂无预警', levelClass: 'blue', temperature: '--℃' },
  { key: 'wind', title: '大风预警', level: '暂无预警', levelClass: 'blue' },
  { key: 'fog', title: '大雾预警', level: '暂无预警', levelClass: 'blue' },
  { key: 'thunder', title: '雷电预警', level: '暂无预警', levelClass: 'blue' },
  { key: 'watershed', title: '流域预警', level: '暂无预警', levelClass: 'blue' }
])
const smallWatershedWarningDetails = ref([])

const isNoWarningText = value => !value || String(value).includes('暂无预警')

const allWarnNoWarning = computed(() => warnTypes.value.every(item => isNoWarningText(item.level)))

const waterlogRainNoWarning = computed(() => {
  const rainItem = warnTypes.value.find(item => item.key === 'rain')
  return isNoWarningText(rainItem?.level)
})

const showWeatherHeaderInInfo = computed(() => (
  (isTripleScreen.value && allWarnNoWarning.value)
  || (isWaterlogMode.value && waterlogRainNoWarning.value)
))

const todayDate = computed(() => formatHeaderDate(weatherNow.value.datetime))

// 当前选中的预警类型
const currentWarn = ref('rain')

function selectFirstActiveWarnInTripleScreen() {
  if (!isTripleScreen.value) return

  const currentItem = warnTypes.value.find(item => item.key === currentWarn.value)
  if (currentItem && !isNoWarningText(currentItem.level)) return

  const activeItem = warnTypes.value.find(item => !isNoWarningText(item.level))
  if (activeItem) {
    currentWarn.value = activeItem.key
  }
}

// 内涝防汛模式下，强制选中降雨并显示右侧面板；退出时重置为默认分类视图
watch(isWaterlogMode, (val) => {
  if (val) {
    currentWarn.value = 'rain'
    singlePanelVisible.value = true
  } else {
    singlePanelVisible.value = false
  }
}, { immediate: true })

const forecastStatConfig: Record<string, { element: string; levels: string[]; colors: string[] }> = {
  rain: {
    element: 'ER24',
    levels: ['特大暴雨', '大暴雨', '暴雨', '大雨', '中雨', '小雨', '无雨'],
    colors: ['red', 'orange', 'yellow', 'yellow', 'cyan', 'blue', 'blue']
  },
  wind: {
    element: 'windSpeed',
    levels: ['13级及以上', '11-12级', '9-10级', '7-8级', '6级及以下'],
    colors: ['red', 'orange', 'yellow', 'yellow', 'cyan']
  },
  fog: {
    element: 'VIS',
    levels: ['5级特强浓雾', '4级强浓雾', '3级浓雾', '2级大雾', '1级轻雾', '无雾'],
    colors: ['red', 'orange', 'yellow', 'yellow', 'cyan', 'cyan']
  }
}

// 各预警类型对应的数据
const warnDataMap: Record<string, any> = reactive({
  rain: {
    title: '暴雨黄色预警',
    content: '重庆市气象台2026年7月28日9时20分发布"暴雨黄色预警信号"，预计72小时后14日13:00-20:00，菜园坝、大坪、两路口、上清寺、大溪沟、南纪门等11个街道，日最高降雨量达到35mm。',
    statTitle: '未来24小时雨量统计',
    tableFirstCol: '等级',
    tableFirstColData: '数量',
    columns: ['特大暴雨<br>≥250', '大暴雨<br>100~249.9', '暴雨<br>50~99.9', '大雨<br>25~49.9', '中雨<br>10~24.9', '小雨<br>0~9.9', '无雨'],
    numbers: [
      { value: '/', color: 'red' },
      { value: '/', color: 'orange' },
      { value: '/', color: 'yellow' },
      { value: '/', color: 'yellow' },
      { value: '/', color: 'cyan' },
      { value: '/', color: 'blue' },
      { value: '/', color: 'blue' }
    ]
  },
  heat: {
    title: '高温红色预警',
    content: '重庆市气象台2026年7月28日9时50分发布"高温红色预警信号"，预计28日13:00-21:00，菜园坝、大坪、两路口、上清寺、大溪沟、南纪门、七星岗、解放碑、化龙桥、朝天门、石油路等11个街道，日最高气温将升至40°C以上。',
    statTitle: '未来七日天气预报',
    tableFirstCol: '日期',
    tableFirstColData: '温度',
    columns: ['/', '/', '/', '/', '/', '/', '/'],
    numbers: [
      { value: '/', color: 'cyan' },
      { value: '/', color: 'cyan' },
      { value: '/', color: 'cyan' },
      { value: '/', color: 'cyan' },
      { value: '/', color: 'cyan' },
      { value: '/', color: 'cyan' },
      { value: '/', color: 'cyan' }
    ]
  },
  wind: {
    title: '大风蓝色预警',
    content: '当前无大风预警，目前风力等级6级，未来24小时风力稳定。',
    statTitle: '未来24小时大风统计',
    tableFirstCol: '等级',
    tableFirstColData: '数量',
    columns: ['13级及以上<br>≥37.0', '11-12级<br>28.5~36.9', '9-10级<br>20.8~28.4', '7-8级<br>13.9~20.7', '6级及以下<br>≤13.8'],
    numbers: [
      { value: '/', color: 'red' },
      { value: '/', color: 'orange' },
      { value: '/', color: 'yellow' },
      { value: '/', color: 'yellow' },
      { value: '/', color: 'cyan' }
    ]
  },
  fog: {
    title: '大雾预警',
    content: '大雾红色预警',
    statTitle: '未来24小时能见度',
    tableFirstCol: '等级',
    tableFirstColData: '数量',
    columns: ['5级特强浓雾', '4级强浓雾', '3级浓雾', '2级大雾', '1级轻雾', '无雾'],
    numbers: [
      { value: '/', color: 'red' },
      { value: '/', color: 'orange' },
      { value: '/', color: 'yellow' },
      { value: '/', color: 'yellow' },
      { value: '/', color: 'cyan' },
      { value: '/', color: 'cyan' }
    ]
  },
  thunder: {
    title: '雷电预警',
    content: '雷电预警信号',
    statTitle: '24小时闪电统计',
    tableFirstCol: '时次',
    tableFirstColData: '次数',
    columns: ['过去1小时', '过去3小时', '过去6小时', '过去12小时', '过去24小时'],
    numbers: [
      { value: '/', color: 'cyan' },
      { value: '/', color: 'cyan' },
      { value: '/', color: 'cyan' },
      { value: '/', color: 'cyan' },
      { value: '/', color: 'cyan' }
    ]
  },
  watershed: {
    title: '流域预警',
    content: '暂无预警信息',
    statTitle: '流域预警信息',
    tableFirstCol: '状态',
    tableFirstColData: '当前',
    columns: ['暂无预警'],
    numbers: [
      { value: '暂无预警', color: 'blue' }
    ]
  }
})

// 当前预警数据
const currentWarnData = computed(() => {
  const key = currentWarn.value
  // 流域不走API
  if (key === 'watershed') return warnDataMap.watershed || {}

  const apiKey = COMPONENT_KEY_MAP[key]
  const records = weatherWarningData.value[apiKey] || []
  const record = records[0] || {}

  // 预警等级
  const levelInfo = record.infoTypeLevel ? LEVEL_MAP[record.infoTypeLevel] : null
  const levelText = levelInfo?.text || ''
  const levelClass = levelInfo?.class || 'blue'

  // 类型名称
  const typeName = record.infoTypeName || TYPE_NAME_FALLBACK[key] || ''

  // 标题：如"大风蓝色预警"
  const title = levelText ? `${typeName}${levelText}预警` : `${typeName}预警`

  // 内容
  const content = record.content || '暂无预警信息'

  // 保留原有统计表格数据
  const baseData = warnDataMap[key] || {}
  return {
    ...baseData,
    title,
    content
  }
})

const getResponseData = data => data?.data ?? data ?? {}
const getArrayData = value => Array.isArray(value) ? value : []

function getSmallWatershedLevelClass(level = '') {
  if (level.includes('红')) return 'red'
  if (level.includes('橙')) return 'orange'
  if (level.includes('黄')) return 'yellow'
  return 'blue'
}

function setSmallWatershedWarnText(warningLevel = '暂无预警') {
  const watershedItem = warnTypes.value.find(item => item.key === 'watershed')
  if (!watershedItem) return
  const levelClass = getSmallWatershedLevelClass(warningLevel)
  watershedItem.level = warningLevel
  watershedItem.levelClass = levelClass
  warnDataMap.watershed.content = warningLevel
  warnDataMap.watershed.columns = [warningLevel]
  warnDataMap.watershed.numbers = [
    { value: warningLevel, color: levelClass }
  ]
  selectFirstActiveWarnInTripleScreen()
}

async function updateSmallWatershedWarnType() {
  try {
    const res = await axios.get(`${BASE_URL}/api/boot/system/land/warningAnalysis`)
    const list = getArrayData(getResponseData(res?.data))
    const latest = list[list.length - 1] || {}
    setSmallWatershedWarnText(latest.warningLevel || '暂无预警')
  } catch (error) {
    console.error('获取流域预警图片分析结果失败:', error)
    setSmallWatershedWarnText('暂无预警')
  }
}

async function fetchGeoWarn() {
  try {
    const res = await axios.get(`${BASE_URL}/api/boot/system/land/geoWarn`)
    const data = getResponseData(res?.data)
    const detailList = getArrayData(data.todaySmallWatershedWarning)
    smallWatershedWarningDetails.value = detailList
    await updateSmallWatershedWarnType()
  } catch (error) {
    console.error('获取流域预警数据失败:', error)
    smallWatershedWarningDetails.value = []
    await updateSmallWatershedWarnType()
  }
}

function openSmallWatershedWarningDetail() {
  const detail = smallWatershedWarningDetails.value[0] || {}
  eventBus.emit(OPEN_GEO_EVENT, {
    diaName: 'geo-small-watershed-warning-detail',
    params: {
      detailType: 'smallWatershed',
      detail
    }
  })
}

function closeDialog() {
  eventBus.emit('CLOSE_DIA', {})
}

function openDispatchIframe() {
  closeDialog()
  eventBus.emit('OPEN_IFRAME_DIA', { url: DISPATCH_URL })
}

function openHistoryWarn() {
  closeDialog()
  eventBus.emit('OPEN_GEO_DIA', {
    diaName: 'geo-history-warn',
    params: {}
  })
}

function handleWarnClick(key) {
  if (key === 'watershed') {
    openSmallWatershedWarningDetail()
    return
  }
  currentWarn.value = key
  if (!isTripleScreen.value) {
    singlePanelVisible.value = true
  }
}

let warnTimer: number | null = null

onMounted(() => {
  fetchGeoWarn()
  fetchWeatherWarningRecord()
  fetchWeatherNow()
  fetchTempForecast()
  fetchWeatherElementForecast()
  fetchLightningForecast()
  // 3分钟轮询
  warnTimer = window.setInterval(() => {
    fetchGeoWarn()
    fetchWeatherWarningRecord()
    fetchWeatherNow()
    fetchTempForecast()
    fetchWeatherElementForecast()
    fetchLightningForecast()
  }, 180000)
})

onBeforeUnmount(() => {
  if (warnTimer) {
    clearInterval(warnTimer)
    warnTimer = null
  }
})

function updateHeatCardTemperature(temperature?: string | number) {
  const heatItem = warnTypes.value.find(item => item.key === 'heat')
  if (!heatItem) return
  const value = temperature != null && `${temperature}` !== 'null' ? `${temperature}`.trim() : ''
  if (!value) return
  heatItem.temperature = value.endsWith('℃') ? value : `${value}℃`
}

// 获取当前实时天气风力
async function fetchWeatherNow() {
  try {
    const res = await axios.get(`${BASE_URL}/api/boot/system/weather/weather/now`)
    const data = getResponseData(res?.data)
    if (!data) return

    weatherNow.value.county = data.county || '渝中区'
    weatherNow.value.datetime = data.datetime || ''
    weatherNow.value.temperature = data.temperature || '--'
    weatherNow.value.windy = data.windy || '--'
    updateHeatCardTemperature(data.temperature)
  } catch (e) {
    console.error('获取实时天气失败:', e)
  }
}

// 获取7日内温度预报
async function fetchTempForecast() {
  try {
    const res = await axios.get(`${BASE_URL}/api/boot/system/weather/temp/forecast`)
    const data = getArrayData(getResponseData(res?.data)).slice(0, 7)
    if (!data.length) return

    warnDataMap.heat.columns = data.map((item: { dayTime?: string }) => {
      const parts = (item.dayTime || '').split('-')
      return parts.length >= 3 ? `${Number(parts[1])}/${Number(parts[2])}` : '/'
    })
    warnDataMap.heat.numbers = data.map((item: { minValue?: string | number; maxValue?: string | number }) => {
      const min = item.minValue != null && `${item.minValue}` !== 'null' ? `${item.minValue}` : ''
      const max = item.maxValue != null && `${item.maxValue}` !== 'null' ? `${item.maxValue}` : ''
      let value = '/'
      if (min && max) {
        value = `${min}~${max}℃`
      } else if (min) {
        value = `${min}℃`
      } else if (max) {
        value = `${max}℃`
      }
      return {
        value,
        color: 'cyan'
      }
    })
  } catch (e) {
    console.error('获取7日温度预报失败:', e)
  }
}

// 获取未来24小时雨量、大风、能见度分级统计
async function fetchWeatherElementForecast() {
  try {
    const res = await axios.get(`${BASE_URL}/api/boot/system/weather/windy/forecast`)
    const data = getResponseData(res?.data)
    const elementStatistics = getArrayData(data.elementStatistics)
    if (!elementStatistics.length) return

    Object.entries(forecastStatConfig).forEach(([warnKey, config]) => {
      const stat = elementStatistics.find((item: any) => item?.element === config.element)
      const levelCounts = getArrayData(stat?.levelCounts)
      if (!levelCounts.length || !warnDataMap[warnKey]) return

      warnDataMap[warnKey].numbers = config.levels.map((level, index) => {
        const matched = levelCounts.find((item: any) => item?.level === level)
        return {
          value: String(matched?.count ?? 0),
          color: config.colors[index] || 'cyan'
        }
      })
    })
  } catch (e) {
    console.error('获取未来24小时气象要素统计失败:', e)
  }
}

// 获取24小时闪电统计
async function fetchLightningForecast() {
  try {
    const res = await axios.get(`${BASE_URL}/api/boot/system/weather/lightning/forecast`)
    const data = getArrayData(getResponseData(res?.data))
    if (!data.length) return

    const lightningTypes = ['1', '3', '6', '12', '24']
    warnDataMap.thunder.numbers = lightningTypes.map(type => {
      const matched = data.find((item: any) => String(item?.type) === type)
      return {
        value: String(matched?.num ?? 0),
        color: 'cyan'
      }
    })
  } catch (e) {
    console.error('获取24小时闪电统计失败:', e)
  }
}

// 气象预警记录接口
async function fetchWeatherWarningRecord() {
  try {
    const res = await axios.get(`${BASE_URL}/api/boot/system/weather/warning/record`)
    console.log('气象预警记录接口返回:', res)
    const data = res.data?.data || {}
    weatherWarningData.value = data

    // 更新左下角卡片的预警等级
    Object.entries(API_KEY_MAP).forEach(([apiKey, componentKey]) => {
      const records = data[apiKey]
      const warnItem = warnTypes.value.find(item => item.key === componentKey)
      if (!warnItem) return

      if (Array.isArray(records) && records.length > 0) {
        const record = records[0]
        const level = record.infoTypeLevel || ''
        const levelInfo = LEVEL_MAP[level] || { text: level, class: 'blue' }
        warnItem.level = levelInfo.text
        warnItem.levelClass = levelInfo.class
      } else {
        warnItem.level = '暂无预警'
        warnItem.levelClass = 'blue'
      }
    })
    updateHeatCardTemperature(weatherNow.value.temperature)
    selectFirstActiveWarnInTripleScreen()
  } catch (error) {
    console.error('获取气象预警记录失败:', error)
  }
}

// 单屏返回左侧
const backToLeft = () => {
  singlePanelVisible.value = false
}
</script>

<script lang="ts">
export default {
  name: "BzZhWarn",
  version: "1.0.0",
};
</script>

<style lang="scss" scoped>
.bz-zh-warn {
  width: 1070px;
  height: 252px;
  box-sizing: border-box;
  position: relative;
  overflow: hidden;
  display: grid;
  grid-template-columns: 428px 626px;
  grid-template-rows: 28px 224px;
  column-gap: 16px;
  color: #fff;
  font-family: 'BzAlibabaPuHuiTi', Microsoft YaHei, PingFang SC, Arial, sans-serif;

  // 单屏模式
  &.single-mode {
    width: 540px;
    height: 252px;
    grid-template-columns: 100%;
    grid-template-rows: 28px 224px;
    position: relative;

    // 内涝防汛模式：隐藏左侧，右侧全宽展示
    &.waterlog-mode {
      .left-area {
        display: none;
      }
      .right-area {
        grid-column: 1;
        grid-row: 2;
        width: 100%;
        margin-left: 0;
      }
      .warn-info-card {
        width: 100%;
      }
      .rain-stat {
        width: 100%;
      }
    }

    .left-area {
      grid-column: 1;
      grid-row: 2;
      width: 100%;
    }

    .top-header {
      width: 100% !important;
      min-width: 0 !important;
      max-width: 100% !important;
      justify-content: space-around;
      padding: 0 8px;
    }

    .warn-boxes {
      width: 100%;
      grid-template-columns: repeat(3, 1fr);
      grid-template-rows: repeat(2, 74px);
      justify-content: stretch;
      flex: 0 0 auto;
      height: 164px;
    }

    .warn-card {
      width: 100%;
      padding: 5px 0 10px;
      box-sizing: border-box;

      .card-title {
        margin-top: -4px;
      }

      .card-level {
        margin-top: 6px;
        margin-bottom: 0px;
      }
    }

    .right-area {
      grid-column: 1;
      grid-row: 2;
      width: 100%;
      margin-left: 0;
    }

    .warn-info-card {
      width: 100%;
      height: 52px;
      padding: 4px 12px;

      .warn-title {
        font-size: 14px;
        margin-bottom: 2px;
      }

      .warn-content {
        font-size: 12px;
        line-height: 1.3;
        max-height: 28px;
        overflow-y: auto;
      }
    }

    .rain-stat {
      width: 100%;
      height: 121px;
    }

    // 非三联屏下高温表格数据行文字缩小到10px
    .stat-table--heat .table-row:not(.header) .rain-num {
      font-size: 10px;
    }
  }
}

// 单屏返回按钮（左上角）
.back-btn {
  position: absolute;
  top: 32px;
  right: 4px;
  z-index: 10;
  padding: 2px 8px;
  font-size: 12px;
  color: #5FBCFF;
  cursor: pointer;
  border: 1px solid rgba(95, 188, 255, 0.4);
  border-radius: 4px;
  user-select: none;

  &:hover {
    background: rgba(95, 188, 255, 0.15);
  }
}

.top-btns {
  grid-column: 1 / -1;
  grid-row: 1;
  justify-self: end;
  align-self: start;
  z-index: 12;
  display: flex;
  align-items: center;
  gap: 6px;
}

.history-warn-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 64px;
  height: 24px;
  color: #fff;
  font-size: 14px;
  line-height: 1;
  background: url('./img/bg_button_deal.png') center / 100% 100% no-repeat;
  cursor: pointer;
  user-select: none;
}

.dispatch-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 24px;
  color: #fff;
  font-size: 14px;
  line-height: 1;
  background: url('./img/bg_button_deal.png') center / 100% 100% no-repeat;
  cursor: pointer;
  user-select: none;
}

// 左侧区域
.left-area {
  grid-column: 1;
  grid-row: 2;
  width: 428px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

// 顶部行
.top-header {
  width: 428px;
  height: 48px;
  background: url('./img/Rectangle_346242393.png') no-repeat center / cover;
  display: flex;
  align-items: center;
  padding: 0 16px;
  gap: 16px;
  border-radius: 4px;
  box-sizing: border-box;
  overflow: hidden;

  .header-group {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 6px;
    white-space: nowrap;
    flex: 0 0 auto;

    .location-icon {
      font-size: 22px;
      color: #bffcff;
      flex-shrink: 0;
    }

    .wind-icon {
      height: 22px;
      width: auto;
      flex-shrink: 0;
    }

    .text {
      font-size: 22px;
      font-style: normal;
      font-weight: 800;
      line-height: normal;
      text-align: center;
      font-family: "Alibaba PuHuiTi 2.0";
      background: linear-gradient(0deg, #5FBCFF 1.4%, #FFF 58.67%);
      background-clip: text;
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
    }
  }

  .divider {
    width: 2px;
    height: 24px;
    background: rgba(191, 252, 255, 0.3);
  }
}

// 预警盒子区域
.warn-boxes {
  width: 428px;
  flex: 1;
  display: grid;
  grid-template-columns: repeat(3, 132px);
  grid-template-rows: repeat(2, 1fr);
  justify-content: center;
  gap: 16px 16px;
}

.warn-card {
  width: 132px;
  height: 100%;
  background: url('./img/Group 2136641308.png') no-repeat center / 100% 100%;
  display: flex;
  cursor: pointer;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 0px 0 10px;
  border-radius: 4px;
  box-sizing: border-box;

  .card-title {
    margin-top: 5px;
    text-align: center;
    font-family: "Alibaba PuHuiTi 2.0"; 
    font-size: 16px; 
    font-style: normal; 
    font-weight: 800; 
    line-height: normal; 
    cursor: pointer;
    background: linear-gradient(0deg, #5FBCFF 1.4%, #FFF 58.67%); 
    background-clip: text; 
    -webkit-background-clip: text; 
    -webkit-text-fill-color: transparent;
  }

  .card-level {
    margin-top: auto;
    margin-bottom: 10px;
    color: var(--Font-Font-8, #FFBC00); 
    font-feature-settings: 'liga' off, 'clig' off; 
    font-family: "Alibaba PuHuiTi 2.0"; 
    font-size: 20px; 
    font-style: normal; 
    font-weight: 800; 
    line-height: normal; 
    letter-spacing: 0.48px;

    &.card-level--heat {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      gap: 2px;
      margin-bottom: 5px;
      font-size: 16px;
      line-height: 1.05;
    }

    .heat-warning,
    .heat-temperature {
      display: block;
    }

    .heat-temperature {
      font-size: 16px;
    }

    &.yellow {
      background: linear-gradient(180deg, #FFD700 0%, #FFF8C0 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
    }
    &.red {
      background: linear-gradient(180deg, #FF4444 0%, #FFCCCC 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
    }
    &.orange {
      background: linear-gradient(180deg, #FF9900 0%, #FFCC99 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
    }
    &.cyan {
      background: linear-gradient(180deg, #00FFFF 0%, #CCFFFF 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
    }
    &.blue {
      background: linear-gradient(180deg, #44AAFF 0%, #CCEEFF 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
    }
    &.green {
      background: linear-gradient(180deg, #00DD60 0%, #99EEBB 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
    }
  }
}

// 卡片背景根据预警等级动态切换
.warn-card.bg-yellow {
  background: url('./img/Group 2136641308.png') no-repeat center / 100% 100%;
}
.warn-card.bg-red {
  background: url('./img/Group_2136641308_1.png') no-repeat center / 100% 100%;
}
.warn-card.bg-blue {
  background: url('./img/Group_2136641308_3.png') no-repeat center / 100% 100%;
}
.warn-card.bg-orange {
  background: url('./img/Group_2136641308_1.png') no-repeat center / 100% 100%;
}
.warn-card.bg-cyan {
  background: url('./img/Group_2136641308_2.png') no-repeat center / 100% 100%;
}
.warn-card.bg-green {
  background: url('./img/Group_2136641308_3.png') no-repeat center / 100% 100%;
}

// 右侧区域
.right-area {
  grid-column: 2;
  grid-row: 2;
  width: 626px;
  margin-left: 0;
  display: flex;
  flex-direction: column;
  gap: 3px;
}

// 预警信息卡片
.warn-info-card {
  width: 626px;
  height: 110px;
  background: url('./img/newBg.png') no-repeat center / 100% 100%;
  border-radius: 4px;
  padding: 8px 12px;
  box-sizing: border-box;

  .warn-title {
    font-size: 20px;
    font-weight: 800;
    color: #FFD700;
    margin-bottom: 4px;
  }

  .warn-content {
    font-size: 14px;
    line-height: 1.4;
    color: #e0f0ff;
    max-height: 60px;
    overflow-y: auto;
    text-align: justify;
    padding-right: 4px;
    scrollbar-width: none;
    -ms-overflow-style: none;

    &::-webkit-scrollbar {
      display: none;
    }
  }
}

.warn-info-card--weather {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
}

.top-header.top-header--plain {
  width: 100%;
  height: 64px;
  background: none;
  border-radius: 0;
  padding: 0 12px;
  justify-content: space-around;
}

.single-mode .warn-info-card--weather {
  height: 52px;
}

.single-mode .top-header.top-header--plain {
  height: 52px;
}

// 雨量统计
.rain-stat {
  width: 626px;
  height: 111px;

  .stat-title {
    font-size: 16px;
    font-weight: 600;
    background: linear-gradient(0deg, #5FBCFF 1.4%, #FFF 58.67%); 
    background-clip: text; 
    -webkit-background-clip: text; 
    -webkit-text-fill-color: transparent;
    margin-bottom: 8px;
    display: flex;
    align-items: center;
    gap: 6px;

    .arrow {
      color: #5FBCFF;
      font-size: 14px;
    }
  }

  .stat-table {
    .table-row {
      display: grid;
      grid-template-columns: 80px repeat(v-bind('currentWarnData.columns.length'), 1fr);
      height: 40px;
      border: 1px solid rgba(95, 188, 255, 0.3);

      .table-cell {
        display: flex;
        align-items: center;
        justify-content: center;
        border-right: 1px solid rgba(95, 188, 255, 0.3);
        font-size: 14px;
        color: #e0f0ff;
        text-align: center;

        &:last-child {
          border-right: none;
        }

        &.first-col {
          font-size: 12px;
        }

        .rain-num {
          font-size: 20px;
          font-weight: 800;

          &.red {
            color: #FF4444;
          }
          &.orange {
            color: #FF9900;
          }
          &.yellow {
            color: #FFD700;
          }
          &.cyan {
            color: #00FFFF;
          }
          &.blue {
            color: #44AAFF;
          }
        }
      }

      &.header {
        background: #08356C;
        .table-cell {
          color: #87e8ff;
          font-size: 11px;
          font-weight: 500;
        }
      }

      &:not(.header) {
        background: #0A2D5D;

        .first-col {
          font-size: 14px;
          font-weight: 600;
        }
      }
    }
  }

  // 高温表格特殊样式：缩小数据行文字防止超出
  .stat-table--heat {
    .table-row.header .table-cell {
      font-size: 14px;
      font-weight: 600;
    }

    .table-row:not(.header) .rain-num {
      font-size: 11px;
      font-weight: 700;
      white-space: nowrap;
    }
  }
}
</style>
