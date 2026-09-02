<template>
  <div class="bz-risk" :style="containerStyle">
    <div class="component-title">
      <span class="title-arrow">▶</span>
      <span class="title-text">值班信息</span>
    </div>
    
    <div class="stats-row">
      <div 
        class="stat-item" 
        v-for="(item, index) in statsList" 
        :key="index"
      >
        <div class="stat-top">
          <div class="stat-top-bg" :style="{ backgroundImage: `url(${countBg})` }"></div>
          <div class="stat-value" :class="{ 'stat-value--highlight': item.highlight }">
            {{ item.value }}<span class="stat-unit">人</span>
          </div>
        </div>
        <div class="stat-bottom">
          <div class="stat-bottom-bg" :style="{ backgroundImage: `url(${labelBg})` }"></div>
          <div class="stat-label">{{ item.label }}</div>
        </div>
      </div>
    </div>
    
    <div class="risk-list-header">
      <div class="risk-list-title">
        <span class="title-arrow">▶</span>
        <span class="title-text">风险列表</span>
      </div>
      <div class="risk-count">
        共 <span class="count-num">{{ countMap.all }}</span> 条
      </div>
    </div>
    
    <div class="tab-nav">
      <div 
        class="tab-item" 
        :class="{ 'tab-item--active': activeTab === 'processing' }"
        @click="handleTabClick('processing')"
      >处置中（{{ countMap.processing }}）</div>
      <div 
        class="tab-item" 
        :class="{ 'tab-item--active': activeTab === 'completed' }"
        @click="handleTabClick('completed')"
      >已完成（{{ countMap.completed }}）</div>
      <div 
        class="tab-item" 
        :class="{ 'tab-item--active': activeTab === 'all' }"
        @click="handleTabClick('all')"
      >全部（{{ countMap.all }}）</div>
    </div>
    
    <div class="risk-list">
      <div 
        class="risk-item" 
        :class="{
          'risk-item--active': selectedIndex === index,
          'risk-item--geo-disaster': isGeoDisasterRisk(item)
        }"
        v-for="(item, index) in filteredList" 
        :key="index"
        @click="handleRiskClick(item, index)"
      >
        <div class="risk-item__top">
          <span class="risk-item__title">{{ item.title }}</span>
          <span class="risk-item__status" :class="'status--' + item.statusType">
            {{ item.status }}
          </span>
        </div>
        <div class="risk-item__bottom">
          <span class="risk-item__time">{{ item.time }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, nextTick, onMounted, onBeforeUnmount } from 'vue'
import axios from 'axios'
import labelBg from './img/back-name.png'
import countBg from './img/back-number.png'

const props = defineProps({
  chartConfig: {
    type: Object,
    required: true
  },
  publicParamList: {
    type: Object,
    default: () => []
  },
  bus: {
    type: Object,
    default: null
  }
})

const sourceName = 'BzRisk'

const RISK_CLICK_EVENT = 'BZ_RISK_CLICK'
const RISK_ANALYSIS_DATA_EVENT = 'BZ_RISK_ANALYSIS_DATA'

// 背景图URL

const containerStyle = computed(() => {
  const width = Number(props.chartConfig?.attr?.w)
  const height = Number(props.chartConfig?.attr?.h)
  return {
    width: Number.isFinite(width) && width > 0 ? width + 'px' : '480px',
    height: Number.isFinite(height) && height > 0 ? height + 'px' : '915px'
  }
})

const statsList = ref([
  { label: '区领导', value: '31', highlight: true },
  { label: '区政府办', value: '41', highlight: false },
  { label: '部门', value: '0', highlight: false },
  { label: '街道', value: '0', highlight: false },
  { label: '三支力量', value: '19', highlight: false }
])

const activeTab = ref('processing')
const selectedIndex = ref(-1)

const BASE_URL = 'http://23.99.16.179:11001/api/boot/system/risk'

const riskList = ref<any[]>([])
const GEO_DISASTER_EVENT_ID = 'SHYZDK20260819172149032961'

const countMap = ref({
  processing: 0,
  completed: 0,
  all: 0
})

const filteredList = computed(() => {
  if (activeTab.value === 'all') return riskList.value
  return riskList.value.filter(item => item.tabType === activeTab.value)
})

const getRiskEventId = (item: any) => String(item?.asEventNumber ?? item?.eventNo ?? item?.id ?? '').trim()
const isGeoDisasterRisk = (item: any) => {
  const fakeFlag = String(item?.fake_flag ?? item?.fakeFlag ?? '').trim()
  return fakeFlag === '地质灾害' || getRiskEventId(item) === GEO_DISASTER_EVENT_ID
}

// 获取风险列表（请求全部数据，前端按状态分类）
const getRiskList = async () => {
  try {
    const res = await axios.get(`${BASE_URL}/lists`, { params: { type: 0 } })
    if (res.data.code === '000000') {
      const list = res.data.data || []
      riskList.value = list.map((item: any) => {
        const isCompleted = item.handleStatus === '已办结'
        return {
          ...item,
          title: item.eventDesc || item.eventType || '未知事件',
          time: item.eventTime || '',
          status: item.handleStatus || '处置中',
          statusType: isCompleted ? 'completed' : 'processing',
          tabType: isCompleted ? 'completed' : 'processing'
        }
      })
      countMap.value.all = riskList.value.length
      countMap.value.processing = riskList.value.filter(item => item.tabType === 'processing').length
      countMap.value.completed = riskList.value.filter(item => item.tabType === 'completed').length
    } else {
      console.error('获取风险列表失败：' + res.data.message)
      riskList.value = []
      countMap.value = { processing: 0, completed: 0, all: 0 }
    }
  } catch (err) {
    console.error('请求风险列表接口失败', err)
    riskList.value = []
    countMap.value = { processing: 0, completed: 0, all: 0 }
  }
}

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

const getRiskTitle = item => item?.title || item?.name || item?.eventTitle || item?.riskName || '风险隐患'

const RESCUE_OPINION_URL = 'http://23.99.16.179:11001/api/boot/system/rescue/opinion'

async function fetchRescueOpinion(item: any) {
  try {
    const params = {
      source: item?.source || '',
      eventType: item?.eventType || '',
      eventAddress: item?.eventAddress || '',
      eventDesc: item?.eventDesc || '',
      handleStatus: item?.handleStatus || '',
      referenceLevel: item?.referenceLevel || '',
      coordinateDepartments: item?.coordinateDepartments || ''
    }
    const res = await axios.post(RESCUE_OPINION_URL, params)
    if (res.data.code === '000000') {
      return res.data.data || {}
    } else {
      console.error('获取AI分析失败：' + res.data.message)
      return {}
    }
  } catch (err) {
    console.error('请求AI分析接口失败', err)
    return {}
  }
}

async function openRiskAnalysis(item) {
  eventBus.emit('OPEN_DIA', {
    diaName: 'ai-analysis',
    data: {
      title: getRiskTitle(item),
      riskData: item,
      analysisData: {
        emergencyLevel: '分析中...',
        dispatchSuggestion: '分析中...',
        suggestion: '正在获取AI分析结果，请稍候...'
      }
    }
  })

  const opinion = await fetchRescueOpinion(item)

  const analysisData = {
    emergencyLevel: opinion['紧急状况'] || '较重要',
    dispatchSuggestion: opinion['智能调度'] || '否',
    suggestion: opinion['处置建议'] || '暂无处置建议'
  }

  eventBus.emit('OPEN_DIA', {
    diaName: 'ai-analysis',
    data: {
      title: getRiskTitle(item),
      riskData: item,
      analysisData
    }
  })

  eventBus.emit(RISK_ANALYSIS_DATA_EVENT, {
    riskData: item,
    analysisData
  })
}

function resetProcess() {
  eventBus.emit('BZ_PROCESS_RESET', { index: 0 })
}
let warningEventDetailLoading = false
function handleWarningEventRowClick(item) {
  const eventNumber = item?.asEventNumber
  if (!eventNumber || warningEventDetailLoading) return
  warningEventDetailLoading = true
  axios({
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
  }).then(res => {
    const records =
      res?.data?.resultBody?.page?.records ||
      res?.resultBody?.page?.records ||
      []
    const flowId = Array.isArray(records) && records.length ? records[0]?.flowId : ''
    const eventData = {
        flowId,
        eventNum: String(eventNumber)
    }
    eventBus.emit('EventDetailModal:eventData', eventData)
  }).catch(err => {
    console.error('获取事件详情失败:', err)
  }).finally(() => {
    warningEventDetailLoading = false
  })
}
function selectRiskItem(item, index) {
  if (!item) return
  selectedIndex.value = index
  resetProcess()
  eventBus.emit(RISK_CLICK_EVENT, item)
  openRiskAnalysis(item)
}

function handleRiskClick(item, index) {
  selectRiskItem(item, index)
}

function handleTabClick(tab) {
  activeTab.value = tab
  selectedIndex.value = -1
  eventBus.emit('BZ_RISK_TAB_CHANGE', { tab })
}

watch(filteredList, list => {
  nextTick(() => {
    if (!list.length) {
      selectedIndex.value = -1
      return
    }
    selectRiskItem(list[0], 0)
  })
})

onMounted(() => {
  getRiskList()
})
</script>

<script lang="ts">
export default {
  name: "BzRisk",
  version: "1.0.0"
}
</script>

<style lang="scss" scoped>
.bz-risk {
  width: 480px;
  height: 915px;
  background: linear-gradient(180deg, rgb(5, 26, 58,0.2) 0%, rgb(8, 31, 62,0.5) 100%);
  border: 1px solid rgba(33, 145, 244, 0.3);
  border-radius: 4px;
  padding: 14px;
  box-sizing: border-box;
  font-family: 'Alibaba PuHuiTi', 'Microsoft YaHei', sans-serif;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.component-title {
  display: flex;
  align-items: center;
  margin-bottom: 14px;
  
  .title-arrow {
    font-size: 6px;
    color: #c2e2ff;
    margin-right: 6px;
  }
  
  .title-text {
    font-size: 16px;
    font-weight: 600;
    color: #fff;
  }
}

.stats-row {
  display: flex;
  justify-content: space-between;
  gap: 4px;
}

.stat-item {
  height: 110px;
  flex: 1;
  text-align: center;
  position: relative;
  padding: 0;
  background: transparent;
  border: none;
  display: flex;
  flex-direction: column;
  align-items: center;
  background: url('./img/item-bg.png');
}

.stat-top {
  position: relative;
  width: 100%;
  height: 50px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.stat-top-bg {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 50px;
  height: 50px;
  background-repeat: no-repeat;
  background-position: center;
  background-size: 100% 100%;
  pointer-events: none;
}

.stat-bottom {
  position: relative;
  width: 100%;
  padding: 0 8px;
  height: 24px;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 4px;
}

.stat-bottom-bg {
  position: absolute;
  top: 0;
  left: 50%;
  margin: 0 auto;
  width: 52px;
  height: 100%;
  background-repeat: no-repeat;
  background-position: center;
  background-size: 100% 100%;
  pointer-events: none;
  transform: translateX(-50%);
}

.stat-value {
  position: relative;
  z-index: 1;
  font-size: 18px;
  font-weight: 700;
  color: #00c9ff;
  line-height: 1;
  margin-bottom: 30px;
  
  .stat-unit {
    font-size: 11px;
    font-weight: 400;
    margin-left: 1px;
    color: #8bbce6;
  }
}

.stat-label {
  position: relative;
  z-index: 1;
  font-size: 11px;
  color: #c4daef;
  white-space: nowrap;
}

.risk-list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  margin-top: 24px;
}

.risk-list-title {
  display: flex;
  align-items: center;
  
  .title-arrow {
    font-size: 6px;
    color: #c2e2ff;
    margin-right: 6px;
  }
  
  .title-text {
    font-size: 14px;
    font-weight: 600;
    color: #fff;
  }
}

.risk-count {
  font-size: 12px;
  color: #8bbce6;
  
  .count-num {
    color: #ffa502;
    font-weight: 700;
    margin: 0 2px;
  }
}

.tab-nav {
  display: flex;
  gap: 0;
  margin-bottom: 10px;
  border-bottom: 1px solid rgba(33, 145, 244, 0.15);
}

.tab-item {
  padding: 6px 12px;
  font-size: 12px;
  color: #8bbce6;
  cursor: pointer;
  position: relative;
  
  &:hover {
    color: #fff;
  }
  
  &--active {
    color: #fff;
    font-weight: 600;
    
    &::after {
      content: '';
      position: absolute;
      left: 50%;
      bottom: -1px;
      transform: translateX(-50%);
      width: 18px;
      height: 2px;
      background: #47a8ff;
    }
  }
}

.risk-list {
  flex: 1;
  overflow-y: auto;
  
  &::-webkit-scrollbar {
    width: 4px;
  }
  
  &::-webkit-scrollbar-thumb {
    background: rgba(33, 145, 244, 0.3);
    border-radius: 2px;
  }
  
  &::-webkit-scrollbar-track {
    background: transparent;
  }
}

.risk-item {
  padding: 10px 12px;
  background: rgba(0, 40, 80, 0.35);
  border: 1px solid rgba(33, 145, 244, 0.15);
  border-radius: 2px;
  margin-bottom: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  
  &:hover {
    border-color: rgba(33, 145, 244, 0.4);
    background: rgba(0, 40, 80, 0.55);
  }
  
  &--active {
    border-color: #47a8ff;
    background: rgba(71, 168, 255, 0.2);
    box-shadow: 0 0 8px rgba(71, 168, 255, 0.4);
    
    .risk-item__title {
      color: #47a8ff;
    }
  }

  &--geo-disaster {
    background: rgba(255, 160, 51, 0.3);
    border-color: rgba(255, 160, 51, 0.65);

    &:hover {
      background: rgba(255, 160, 51, 0.38);
      border-color: rgba(255, 160, 51, 0.8);
    }
  }

  &--geo-disaster.risk-item--active {
    background: rgba(255, 160, 51, 0.3);
    border-color: #ffa033;
    box-shadow: 0 0 8px rgba(255, 160, 51, 0.45);
  }
}

.risk-item__top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 6px;
}

.risk-item__title {
  font-size: 12px;
  color: #e8f8ff;
  line-height: 1.4;
  flex: 1;
  margin-right: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.risk-item__status {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 10px;
  flex-shrink: 0;
  
  &.status--processing {
    background: rgba(255, 165, 2, 0.2);
    color: #ffa502;
    border: 1px solid rgba(255, 165, 2, 0.4);
  }
  
  &.status--completed {
    background: rgba(71, 222, 162, 0.2);
    color: #47dea2;
    border: 1px solid rgba(71, 222, 162, 0.4);
  }
}

.risk-item__bottom {
  display: flex;
  align-items: center;
}

.risk-item__time {
  font-size: 11px;
  color: #8bbce6;
}
</style>
