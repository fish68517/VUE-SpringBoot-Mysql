<template>
  <div class="risk-perception-popup">
    <!-- 顶部标题栏 -->
    <div class="popup-header">
      <h3 class="title">风险感知</h3>
      <img class="close-btn" :src="closeIcon" alt="关闭" @click="$emit('close')" />
    </div>

    <!-- 内容区域：左右结构 -->
    <div class="content-wrapper">
      <!-- 左侧 -->
      <div class="content-left">
        <!-- 饼图 -->
        <div class="chart-section">
          <v-chart class="pie-chart" :option="pieOption" autoresize />
        </div>

        <!-- 预警完结率 -->
        <div class="rate-section">
          <div class="rate-title">预警完结率</div>
          <div class="rate-list">
            <div
              v-for="(item, index) in riskRates"
              :key="item.name"
              class="rate-item"
              :class="{ 'rate-item--active': item.name === activeRiskName }"
              @click="handleRiskRateClick(item)"
            >
              <span class="rate-name" :style="{ color: pieColors[index % pieColors.length] }">{{ item.name }}</span>
              <span class="rate-value">{{ item.rate }}%</span>
            </div>
          </div>
        </div>

        <!-- 事件列表（搬运自 YZEventBox） -->
        <div class="event-section">
          <div class="yz-event-box">
            <div v-if="eventDetailLoading" class="event-detail-loading">
              <span class="event-detail-loading__spinner"></span>
              <span>正在查询事件详情</span>
            </div>

            <div class="event-box__filter">
              <n-select
                v-model:value="selectedTitleNameType"
                class="event-type-select"
                :options="titleNameTypeOptions"
                clearable
                placeholder="请选择"
                @update:value="handleTitleNameTypeChange"
              />
            </div>

            <div class="event-box__header">
              <div class="event-box__title">事件报警列表</div>
              <div class="event-box__count">{{ totalCount }}件</div>
            </div>

            <div class="event-box__body">
              <div
                v-for="(item, index) in pagedEventList"
                :key="item.id"
                class="event-row"
                :class="{ 'event-row--flash': flashEventNumber === item.eventNumber }"
                @click="handleRowClick(item)"
              >
                <div class="event-row__index">{{ (currentPage - 1) * pageSize + index + 1 }}</div>
                <div class="event-row__main">
                  <div class="event-row__title-line">
                    <span class="event-row__dot"></span>
                    <span class="event-row__title" :title="item.title">{{ item.title }}</span>
                  </div>
                  <div class="event-row__address" :title="item.address">
                    <span class="event-row__location-icon"></span>
                    <span>{{ item.address }}</span>
                  </div>
                </div>
                <div class="event-row__time">
                  <div>{{ item.date }}</div>
                  <div>{{ item.time }}</div>
                </div>
                <div class="event-row__action">
                  <span
                    class="event-row__status"
                    :class="{ 'event-row__status--finished': item.status === '已完成' }"
                  >
                    {{ item.status }}
                  </span>
                </div>
              </div>
            </div>

            <div class="event-box__pager">
              <button type="button" class="pager-btn pager-btn--arrow" @click="changePage(currentPage - 1)">‹</button>
              <button
                v-for="page in visiblePages"
                :key="page"
                type="button"
                class="pager-btn"
                :class="{ 'pager-btn--active': page === currentPage, 'pager-btn--ellipsis': page === '...' }"
                @click="changePage(page)"
              >
                {{ page }}
              </button>
              <button type="button" class="pager-btn pager-btn--arrow" @click="changePage(currentPage + 1)">›</button>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧（搬运自 AITalk） -->
      <div class="content-right">
        <div class="AIText">
          <div v-if="showRiskTitle" class="AIText__title">{{ riskTitle }}</div>
          <div ref="aiTextRef" class="AIText__content">{{ aiDisplayText }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onBeforeUnmount, nextTick, defineProps, defineEmits } from 'vue'
import axios from 'axios'
import { NSelect } from 'naive-ui'
import closeIcon from '../img/close.png'

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
  },
  houseType: {
    type: String,
    default: '群租空间'
  }
})

const emits = defineEmits(['close', 'useBaseApi', 'showWarningDetail'])

/* ===================== 左侧：饼图 ===================== */
const pieChartRef = ref(null)
let pieChart = null

// 重点事件统计接口 - 按房屋类型区分
const EVENT_STAT_API_MAP = {
  '网约房': 'http://23.99.16.179:11001/api/boot/system/resiComm/onlineHouseEventStatusDetail',
  '群租空间': 'http://23.99.16.179:11001/api/boot/system/resiComm/groupHouseEventStatusDetail',
  '商改住': 'http://23.99.16.179:11001/api/boot/system/resiComm/bizToResiHouseEventStatus'
}

// 风险分类数据（与饼图、预警完结率共用）
const riskCategories = ref([])

// 饼图颜色
const pieColors = ['#ff6b6b', '#4ecdc4', '#ffe66d', '#a78bfa', '#f97316', '#06d6a0', '#118ab2', '#ef476f', '#7209b7', '#fbbf24']

const pieOption = computed(() => ({
  tooltip: {
    trigger: 'item',
    formatter: '{b}: {c} ({d}%)'
  },
  legend: {
    type: 'scroll',
    orient: 'vertical',
    right: 0,
    top: 'center',
    width: 170,
    itemWidth: 10,
    itemHeight: 10,
    itemGap: 20,
    scrollDataIndex: 0,
    pageButtonItemGap: 5,
    pageButtonPosition: 'end',
    pageFormatter: '{current}/{total}',
    pageTextStyle: { color: '#d7e9ff', fontSize: 10 },
    formatter: (name) => {
      const item = riskCategories.value.find(d => d.name === name)
      const value = item ? item.value : ''
      const maxLen = 4
      const displayName = name.length > maxLen ? name.slice(0, maxLen) + '…' : name
      return `${displayName}  ${value}`
    },
    textStyle: {
      color: '#d7e9ff',
      fontSize: 12
    }
  },
  series: [
    {
      type: 'pie',
      radius: '70%',
      center: ['25%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderColor: 'transparent',
        borderWidth: 0
      },
      label: { show: false },
      labelLine: { show: false },
      data: riskCategories.value.map((item, index) => ({
        name: item.name,
        value: item.value,
        itemStyle: {
          color: pieColors[index % pieColors.length]
        }
      }))
    }
  ]
}))

/* ===================== 左侧：预警完结率 ===================== */
const riskRates = ref([])

const activeRiskName = ref('')

// 获取重点事件统计数据
async function fetchEventStat() {
  const api = EVENT_STAT_API_MAP[props.houseType] || EVENT_STAT_API_MAP['网约房']
  try {
    const res = await axios.post(api, {})
    console.log(`重点事件统计接口返回结果(${props.houseType}):`, res)
    const rawData = res.data?.data

    if (props.houseType === '商改住') {
      // 商改住返回单个对象：inspectionCount/inspectionTotal/riskAwarenessCount/riskAwarenessTotal
      const d = rawData || {}
      const items = [
        { name: '巡查上报', total: d.inspectionTotal || 0, unhandled: d.inspectionCount || 0 },
        { name: '风险感知', total: d.riskAwarenessTotal || 0, unhandled: d.riskAwarenessCount || 0 }
      ]
      riskCategories.value = items.map(item => ({ name: item.name, value: item.total }))
      riskRates.value = items.map(item => ({
        name: item.name,
        rate: item.total > 0 ? Math.round(((item.total - item.unhandled) / item.total) * 100) : 0
      }))
    } else {
      // 网约房/群租空间返回数组：title/number/notWjNumber/wjNumber/wjPercent
      const list = Array.isArray(rawData) ? rawData : []
      riskCategories.value = list.map(item => ({ name: item.title || '--', value: item.number || 0 }))
      riskRates.value = list.map(item => ({
        name: item.title || '--',
        rate: Math.round(item.wjPercent || 0)
      }))
    }

    activeRiskName.value = riskRates.value[0]?.name || ''
  } catch (error) {
    console.error('重点事件统计接口请求失败:', error)
  }
}

/* ===================== 左侧：事件列表 ===================== */
// 事件列表接口 - 按房屋类型区分
const EVENT_LIST_API_MAP = {
  '网约房': 'http://23.99.16.179:11001/api/boot/system/resiComm/onlineHouseEventStatusPage',
  '群租空间': 'http://23.99.16.179:11001/api/boot/system/resiComm/groupHouseEventStatusPage'
}

const EVENT_DETAIL_API = 'http://23.210.227.34:23343/yztapi/cockpit/component/event/getEventList'

// titleNameType 下拉选项
const titleNameTypeOptions = [
  { label: '房屋风险', value: '房屋风险' },
  { label: '人员风险', value: '人员风险' }
]
const selectedTitleNameType = ref(null)

const eventList = ref([])
const totalCount = ref(0)
const flashEventNumber = ref('')
const eventDetailLoading = ref(false)
let flashTimer = null

const currentPage = ref(1)
const pageSize = ref(10)

const totalPage = computed(() => Math.max(1, Math.ceil(totalCount.value / pageSize.value)))
const pagedEventList = computed(() => eventList.value)

const visiblePages = computed(() => {
  if (totalPage.value <= 5) {
    return Array.from({ length: totalPage.value }, (_, index) => index + 1)
  }
  if (currentPage.value <= 3) {
    return [1, 2, 3, 4, '...', totalPage.value]
  }
  if (currentPage.value >= totalPage.value - 2) {
    return [1, '...', totalPage.value - 3, totalPage.value - 2, totalPage.value - 1, totalPage.value]
  }
  return [1, '...', currentPage.value - 1, currentPage.value, currentPage.value + 1, '...', totalPage.value]
})

function changePage(page) {
  if (page === '...') return
  const nextPage = Number(page)
  if (!nextPage || nextPage < 1 || nextPage > totalPage.value) return
  currentPage.value = nextPage
  fetchEventList()
}

function formatDateTime(value) {
  if (!value) {
    return { date: '--', time: '--' }
  }
  const dateObj = new Date(value)
  if (Number.isNaN(dateObj.getTime())) {
    const rawText = String(value)
    const [date = rawText, time = '--'] = rawText.split(' ')
    return { date, time }
  }
  const pad = (num) => String(num).padStart(2, '0')
  const date = `${dateObj.getFullYear()}-${pad(dateObj.getMonth() + 1)}-${pad(dateObj.getDate())}`
  const time = `${pad(dateObj.getHours())}:${pad(dateObj.getMinutes())}:${pad(dateObj.getSeconds())}`
  return { date, time }
}

function normalizeEventItem(item, index) {
  const title = item.eventTitle || '--'
  const address = item.eventLocation || '--'
  const status = item.eventStatus || '--'
  const timeInfo = formatDateTime(item.eventTime)
  return {
    id: String(item.id || item.eventId || item.uuid || `${title}-${index}`),
    title,
    address,
    date: timeInfo.date,
    time: timeInfo.time,
    status,
    eventNumber: item.eventNumber || '--'
  }
}

function handleTitleNameTypeChange(value) {
  selectedTitleNameType.value = value || null
  currentPage.value = 1
  fetchEventList()
}

function emitEventDetail(payload) {
  const eventData = {
    data: payload,
    source: 'YZEventBox'
  }
  const busInstance = props.bus
  busInstance?.emit?.('EventDetailModal:eventData', eventData)
}

function handleRowClick(item) {
  if (eventDetailLoading.value) return
  if (!item.eventNumber || item.eventNumber === '--') return

  flashEventNumber.value = item.eventNumber
  if (flashTimer) clearTimeout(flashTimer)
  flashTimer = setTimeout(() => {
    flashEventNumber.value = ''
    flashTimer = null
  }, 220)

  eventDetailLoading.value = true
  axios.post(EVENT_DETAIL_API, {
    regionCode: ['500103'],
    eventStatus: ['10', '97', '99'],
    flag: '1',
    type: null,
    keyword: item.eventNumber,
    pageNum: 1,
    pageSize: 10,
    sortOrder: 'DESC',
    sortField: 'happen_time'
  }).then(response => {
    const responseData = response?.data?.resultBody?.page
    const detailList = Array.isArray(responseData?.records) ? responseData.records : []
    const detailItem = detailList[0] || {}
    if (!detailItem.flowId) {
      console.error('fetch event detail failed: flowId is empty')
      return
    }
    emitEventDetail({ flowId: detailItem.flowId, eventNum: item.eventNumber })
  }).catch(error => {
    console.error('fetch event detail failed:', error)
  }).finally(() => {
    eventDetailLoading.value = false
  })
}

async function fetchEventList() {
  const api = EVENT_LIST_API_MAP[props.houseType]
  if (!api) return
  try {
    const params = {
      index: currentPage.value,
      size: pageSize.value,
      titleNameType: selectedTitleNameType.value || ''
    }
    const res = await axios.post(api, params)
    console.log(`事件列表接口返回结果(${props.houseType}):`, res)
    const responseData = res.data?.data || {}
    const pageData = responseData.page || {}
    const rawList = Array.isArray(responseData.content) ? responseData.content : []
    eventList.value = rawList.map((item, index) => normalizeEventItem(item, index))
    totalCount.value = Number(pageData.total || rawList.length || 0)
  } catch (error) {
    console.error('事件列表接口请求失败:', error)
    eventList.value = []
    totalCount.value = 0
  }
}

/* ===================== 预警详情弹窗（通过事件触发独立弹窗） ===================== */
const placeholderImg1 = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' + encodeURIComponent('monitoring camera photo of electric bike charging in residential building corridor, dimly lit, safety hazard') + '&image_size=landscape_4_3'
const placeholderImg2 = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' + encodeURIComponent('fire safety inspection scene photo, electric bike parked in hallway, warning sign visible') + '&image_size=landscape_4_3'

function showWarningDetail(item) {
  const detail = {
    eventCode: item.eventNumber || '--',
    eventTime: `${item.date || ''} ${item.time || ''}`,
    riskLevel: '高',
    eventContent: `${item.title || ''}：监测到${activeRiskName.value}风险事件，事件地址为${item.address || '未知'}，已通知相关责任人前往现场核实处置。该事件涉及消防安全隐患，需尽快完成现场核查并提交处置结果。`,
    warningCode: 'YJ' + (item.eventNumber || '000000'),
    eventType: activeRiskName.value,
    handleStatus: item.status || '待处理',
    handler: '张三',
    handleTime: '2026-07-28 14:30:00',
    warningImages: [placeholderImg1],
    sceneImages: [placeholderImg1, placeholderImg2],
    deviceCode: 'DEV-' + (item.eventNumber || '000000'),
    deviceName: '智能感知摄像头',
    deviceArea: '某某街道',
    deviceLocation: item.address || '--',
    deviceStatus: '在线',
    contactPhone: '138****8888'
  }
  emits('showWarningDetail', detail)
}

/* ===================== 右侧：AI 打字机（搬运自 AITalk） ===================== */
const aiFullText = ref('')
const aiDisplayText = ref('')
const aiTextRef = ref(null)
let typingTimer = null
const typingSpeed = 15
const analyzingText = '正在分析中...'

// 风险标题（跟随选中分类）
const riskTitle = computed(() => {
  const text = activeRiskName.value.trim()
  if (!text) return ''
  return text.endsWith('风险') ? text : `${text}风险`
})
const showRiskTitle = ref(true)

// 静态 AI 分析内容（按风险分类）
const staticAiTextMap = {
  '电动车入户': `【电动车入户风险分析报告】

一、风险概述
近期监测到辖区内电动车入户充电现象频发，存在较大消防安全隐患。电动车电池在室内充电过程中，因电池老化、短路、过充等原因极易引发火灾，且火势蔓延迅速，释放有毒气体，严重威胁居民生命财产安全。

二、风险等级：高
- 本月累计发现电动车入户事件 35 起
- 较上月环比上升 12%
- 涉及小区 8 个，楼栋 23 栋

三、主要风险点
1. 电池热失控风险：锂电池在充电过程中可能发生热失控，引发自燃
2. 充电线路隐患：私拉乱接电线、使用非标充电器，易导致短路
3. 疏散通道堵塞：电动车停放楼道，堵塞消防疏散通道
4. 夜间充电无人监管：多数火灾发生在夜间充电时段

四、已采取措施
- 社区宣传 12 次，覆盖居民 2000+ 人
- 楼道张贴警示标识 156 处
- 联合物业开展夜间巡查 8 次
- 建设集中充电桩 5 处，可容纳 200 辆

五、建议
1. 加快集中充电桩建设，满足居民充电需求
2. 推广智能充电设施，具备过载保护、自动断电功能
3. 加强日常巡查，建立电动车入户台账
4. 开展消防安全演练，提升居民应急能力
5. 对屡教不改的住户进行联合执法处罚`,

  '垃圾堆放': `【垃圾堆放风险分析报告】

一、风险概述
辖区内部分小区存在垃圾乱堆乱放现象，尤其建筑垃圾和生活垃圾混堆，不仅影响环境卫生，更存在消防安全隐患。

二、风险等级：中
- 本月累计发现垃圾堆放事件 28 起
- 涉及小区 6 个，点位 42 处

三、主要风险点
1. 建筑垃圾长期堆放未清运，堵塞消防通道
2. 生活垃圾未分类投放，异味扰民
3. 装修垃圾中含易燃材料，存在火灾隐患
4. 垃圾堆放点周边蚊虫滋生，影响居民健康

四、已采取措施
- 设置建筑垃圾临时堆放点 8 处
- 联合环卫部门集中清运 15 次
- 张贴垃圾分类宣传标识 200 处

五、建议
1. 建立建筑垃圾预约清运机制
2. 完善垃圾分类设施配置
3. 加强日常巡查，对违规堆放及时处置
4. 建立长效管理机制，明确责任主体`,

  '人货混居': `【人货混居风险分析报告】

一、风险概述
辖区内部分出租房存在人货混居现象，经营场所与居住空间未做物理隔离，存在严重安全隐患。

二、风险等级：高
- 本月累计发现人货混居事件 22 起
- 涉及场所 15 处

三、主要风险点
1. 储存货物多为易燃可燃物品，火灾荷载大
2. 居住与经营合一，疏散困难
3. 电气线路私拉乱接，用电负荷超标
4. 未配备消防设施器材，初期火灾无法扑救

四、已采取措施
- 联合消防开展排查整治 6 次
- 督促整改完成 10 处
- 发放安全隐患告知书 30 份

五、建议
1. 严格实行经营与居住分离
2. 配备必要消防设施器材
3. 规范电气线路敷设
4. 加强租户安全教育培训
5. 建立定期回查机制防止反弹`,

  '消防通道堵塞': `【消防通道堵塞风险分析报告】

一、风险概述
部分小区存在私家车占用消防通道、楼道堆放杂物等问题，严重影响消防救援效率。

二、风险等级：高
- 本月累计发现消防通道堵塞事件 15 起
- 涉及小区 5 个

三、主要风险点
1. 私家车违规停放占用消防登高面
2. 楼道堆放鞋柜、杂物等占用疏散通道
3. 电动自行车停放楼道阻碍通行
4. 部分消防通道标识不清或缺失

四、已采取措施
- 施划消防通道禁停标线 12 处
- 清理楼道杂物 86 处
- 安装违停抓拍设备 3 套
- 张贴禁止占用消防通道警示标识 100 处

五、建议
1. 完善消防通道标识标线
2. 加大违停执法处罚力度
3. 推广智能违停监测系统
4. 定期开展楼道杂物清理行动
5. 加强居民消防安全宣传`,

  '违规用电': `【违规用电风险分析报告】

一、风险概述
辖区内存在私拉乱接电线、超负荷用电、使用大功率电器等违规用电行为，易引发电气火灾。

二、风险等级：中
- 本月累计发现违规用电事件 10 起
- 涉及住户 18 户

三、主要风险点
1. 私拉乱接电线，线路老化短路风险高
2. 超负荷使用大功率电器，电表过载
3. 使用非标插座排插，接触不良发热
4. 电器长时间通电无人看管

四、已采取措施
- 上门安全用电检查 45 户
- 更换老化线路 12 处
- 发放安全用电宣传手册 500 份

五、建议
1. 定期开展用电安全检查
2. 推广智能限流保护装置
3. 加强租户用电安全宣传
4. 对违规用电行为依法处置`
}

function startTyping(text) {
  if (typingTimer) {
    clearInterval(typingTimer)
    typingTimer = null
  }
  aiDisplayText.value = ''
  let index = 0
  typingTimer = window.setInterval(async () => {
    aiDisplayText.value += text[index]
    index++
    await nextTick()
    if (aiTextRef.value) {
      aiTextRef.value.scrollTop = aiTextRef.value.scrollHeight
    }
    if (index >= text.length) {
      clearInterval(typingTimer)
      typingTimer = null
    }
  }, typingSpeed)
}

function stopTyping() {
  if (typingTimer) {
    clearInterval(typingTimer)
    typingTimer = null
  }
}

function waitNextTask() {
  return new Promise(resolve => {
    window.setTimeout(resolve, 0)
  })
}

function getEventConfigList(eventName) {
  const eventConfig = props.chartConfig?.option?.eventConfig
  if (!Array.isArray(eventConfig)) return []
  return eventConfig.filter(item => item.customEvent === eventName)
}

function syncEventDataset() {
  const option = props.chartConfig?.option
  if (!option?.dataset?.source?.[0]) return
  option.dataset.source[0].aiText = aiFullText.value || aiDisplayText.value || ''
  option.dataset.source[0].riskType = riskTitle.value || ''
}

async function runEventList(eventName) {
  syncEventDataset()
  const eventList = getEventConfigList(eventName)
  const updateStatusEvents = eventList.filter(item => item.action === 'updateStatus')
  const showHideEvents = eventList.filter(item => item.action === 'showHide')
  const otherEvents = eventList.filter(item => !['updateStatus', 'showHide'].includes(item.action))

  updateStatusEvents.forEach(item => { handleEvent(item) })
  if (updateStatusEvents.length && showHideEvents.length) {
    await nextTick()
    await waitNextTask()
  }
  showHideEvents.forEach(item => { handleEvent(item) })
  otherEvents.forEach(item => { handleEvent(item) })
}

function handleEvent(item) {
  if (!item || !item.action) return
  switch (item.action) {
    case 'showMessage': {
      const { messageType, message } = item
      if (window.$message && messageType) {
        window.$message[messageType](message || '')
      }
      break
    }
    case 'updatePublcParam': {
      const { exportParam, bindParams } = item
      const source = props.chartConfig?.option?.dataset?.source?.[0] || {}
      emits('useBaseApi', {
        type: 'publicParamChange',
        data: {
          [bindParams]: source[exportParam] || ''
        }
      })
      break
    }
    case 'showHide': {
      const { componentIds, showHideType } = item
      emits('useBaseApi', {
        type: 'showHide',
        data: { componentIds, showHideType }
      })
      break
    }
    case 'jumpPage': {
      emits('useBaseApi', {
        type: 'jumpPage',
        data: item.pageId
      })
      break
    }
    case 'openLink': {
      const { link, openType } = item
      emits('useBaseApi', {
        type: 'openLink',
        data: { link, openType }
      })
      break
    }
    case 'updateStatus': {
      const { statusManageId, statusId } = item
      emits('useBaseApi', {
        type: 'updateStatus',
        data: { statusManageId, statusId }
      })
      break
    }
    case 'updateComponentData': {
      const { componentIds, data } = item
      emits('useBaseApi', {
        type: 'updateComponentData',
        data: { componentIds, data }
      })
      break
    }
    case 'dataExport': {
      const { componentId, data, exportType, fileName } = item
      emits('useBaseApi', {
        type: 'dataExport',
        data: { componentId, data, exportType, fileName }
      })
      break
    }
    case 'editOption': {
      const { componentId, component } = item
      emits('useBaseApi', {
        type: 'editOption',
        data: { componentId, component }
      })
      break
    }
  }
}

function getAIdata(riskName) {
  const name = riskName || activeRiskName.value
  stopTyping()
  aiDisplayText.value = analyzingText
  const text = staticAiTextMap[name] || `【${name}风险分析报告】\n\n暂无相关分析内容`
  aiFullText.value = text
}

function handleRiskRateClick(item) {
  if (activeRiskName.value === item.name) return
  activeRiskName.value = item.name
  getAIdata(item.name)
}

watch(aiFullText, (newText) => {
  if (!newText) return
  startTyping(newText.trimStart())
}, { immediate: true })

/* ===================== 生命周期 ===================== */
onMounted(() => {
  fetchEventStat()
  fetchEventList()
  nextTick(() => {
    initPieChart()
  })
  getAIdata()
})

watch(() => props.houseType, () => {
  fetchEventStat()
  currentPage.value = 1
  selectedTitleNameType.value = null
  fetchEventList()
})

onBeforeUnmount(() => {
  stopTyping()
  if (flashTimer) {
    clearTimeout(flashTimer)
    flashTimer = null
  }
})
</script>

<style lang="scss" scoped>
.risk-perception-popup {
  width: 1284px;
  height: 756px;
  background: url('../img/diaBg.png') no-repeat center / 100% 100%;
  transform: translate(-50%, -50%);
  position: relative;
  padding: 10px 25px;
  color: #fff;
  font-family: 'Microsoft YaHei';

  // 顶部标题栏
  .popup-header {
    height: 50px;
    line-height: 50px;
    padding: 0 25px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    transform: translateY(10px);

    .title {
      font-size: 26px;
      font-weight: bold;
      margin: 0;
      color: #fff;
    }

    .close-btn {
      width: 40px;
      height: 40px;
      cursor: pointer;
    }
  }

  // 内容区域
  .content-wrapper {
    display: flex;
    padding: 15px 25px;
    height: calc(100% - 80px);
    gap: 16px;

    // 左侧
    .content-left {
      width: 360px;
      flex-shrink: 0;
      display: flex;
      flex-direction: column;
      gap: 12px;
      background: rgba(4, 28, 72, 0.4);
      border-radius: 6px;
      padding: 12px;

      // 饼图
      .chart-section {
        height: 220px;
        flex-shrink: 0;

        .pie-chart {
          width: 100%;
          height: 100%;
        }
      }

      // 预警完结率
      .rate-section {
        flex-shrink: 0;
        max-height: 120px;
        padding: 4px 0;
        display: flex;
        flex-direction: column;

        .rate-title {
          font-size: 16px;
          font-weight: bold;
          color: #8bbce6;
          margin-bottom: 6px;
          padding-left: 8px;
          border-left: 3px solid #29d8ff;
          flex-shrink: 0;
        }

        .rate-list {
          display: grid;
          grid-template-columns: 1fr 1fr;
          gap: 4px 8px;
          overflow-y: auto;
          flex: 1;
          min-height: 0;
          scrollbar-width: none;
          -ms-overflow-style: none;

          &::-webkit-scrollbar {
            display: none;
            width: 0;
          }
        }

        .rate-item {
          display: flex;
          align-items: center;
          justify-content: space-between;
          gap: 6px;
          cursor: pointer;
          padding: 2px 6px;
          border-radius: 4px;
          transition: background 0.2s;

          &:hover {
            background: rgba(4, 188, 250, 0.1);
          }

          &--active {
            background: rgba(4, 188, 250, 0.15);
          }

          .rate-name {
            font-size: 13px;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
          }

          .rate-value {
            font-size: 13px;
            color: #47dea2;
            flex-shrink: 0;
            font-weight: bold;
          }
        }
      }

      // 事件列表区域
      .event-section {
        flex: 1;
        min-height: 0;
        overflow: hidden;
        border-radius: 4px;
        display: flex;
        flex-direction: column;
      }
    }

    // 右侧
    .content-right {
      flex: 1;
      display: flex;
      flex-direction: column;
      background: rgba(4, 28, 72, 0.4);
      border-radius: 6px;
      padding: 16px;
      min-width: 0;
    }
  }
}

/* ===================== YZEventBox 样式（搬运） ===================== */
.yz-event-box {
  position: relative;
  display: flex;
  flex-direction: column;
  flex: 1;
  min-width: 0;
  min-height: 0;
  padding: 10px 10px 8px;
  box-sizing: border-box;
  overflow: hidden;
  color: #dff7ff;
  background: linear-gradient(180deg, rgba(13, 56, 92, 0.96), rgba(7, 42, 73, 0.96));
}

.event-detail-loading {
  position: absolute;
  inset: 0;
  z-index: 10;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  color: #dff7ff;
  font-size: 14px;
  background: rgba(5, 39, 65, 0.78);
  backdrop-filter: blur(2px);
}

.event-detail-loading__spinner {
  width: 20px;
  height: 20px;
  border: 2px solid rgba(113, 210, 255, 0.28);
  border-top-color: #69d8ff;
  border-radius: 50%;
  animation: event-detail-loading-spin 0.8s linear infinite;
}

@keyframes event-detail-loading-spin {
  to {
    transform: rotate(360deg);
  }
}

.event-box__header {
  flex: 0 0 26px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-right: 14px;
}

.event-box__title {
  position: relative;
  padding-left: 8px;
  color: #bfefff;
  font-size: 13px;
  font-weight: 700;
  line-height: 1;

  &::before {
    content: '';
    position: absolute;
    left: 0;
    top: 50%;
    width: 3px;
    height: 14px;
    transform: translateY(-50%);
    background: #29d8ff;
    box-shadow: 0 0 10px rgba(41, 216, 255, 0.6);
  }
}

.event-box__count {
  color: #d8f8ff;
  font-size: 12px;
  font-weight: 700;
}

.event-box__filter {
  flex: 0 0 28px;
  width: 100%;
  min-width: 0;
  margin-bottom: 5px;
}

.event-type-select {
  width: 100%;

  :deep(.n-base-selection) {
    --n-height: 24px !important;
    --n-border: 1px solid rgba(65, 174, 225, 0.5) !important;
    --n-border-hover: 1px solid rgba(92, 214, 255, 0.82) !important;
    --n-border-active: 1px solid rgba(92, 214, 255, 0.92) !important;
    --n-box-shadow-active: 0 0 8px rgba(35, 188, 255, 0.18) !important;
    --n-color: rgba(9, 57, 92, 0.82) !important;
    --n-color-active: rgba(9, 57, 92, 0.92) !important;
    --n-text-color: #dff7ff !important;
    --n-placeholder-color: rgba(190, 229, 247, 0.62) !important;
  }
}

.event-box__body {
  flex: 1 1 auto;
  min-height: 0;
  display: flex;
  flex-direction: column;
  gap: 5px;
  overflow-y: auto;
  padding-right: 6px;
  scrollbar-width: thin;
  scrollbar-color: rgba(92, 164, 197, 0.8) transparent;

  &::-webkit-scrollbar {
    width: 6px;
  }

  &::-webkit-scrollbar-thumb {
    border-radius: 999px;
    background: rgba(92, 164, 197, 0.8);
  }

  &::-webkit-scrollbar-track {
    background: transparent;
  }
}

.event-row {
  flex: 1 1 0;
  min-height: 58px;
  display: grid;
  grid-template-columns: 26px minmax(0, 1fr) 68px 48px;
  align-items: center;
  gap: 6px;
  min-width: 0;
  padding: 5px 4px 5px 0;
  box-sizing: border-box;
  border: 1px solid rgba(53, 136, 189, 0.42);
  background: rgba(10, 67, 109, 0.48);
  cursor: pointer;
  transition: all 0.2s ease;
}

.event-row:hover {
  border-color: rgba(76, 196, 255, 0.72);
  background: rgba(14, 86, 139, 0.56);
}

.event-row--flash {
  border-color: rgba(53, 214, 255, 0.95);
  background: linear-gradient(90deg, rgba(19, 105, 168, 0.72), rgba(12, 71, 116, 0.82));
  box-shadow: inset 0 0 0 1px rgba(111, 237, 255, 0.35);
}

.event-row__index {
  color: #ccecff;
  font-size: 13px;
  text-align: center;
}

.event-row__main {
  min-width: 0;
}

.event-row__title-line,
.event-row__address {
  display: flex;
  align-items: center;
  min-width: 0;
}

.event-row__title-line {
  gap: 8px;
}

.event-row__dot {
  flex: 0 0 auto;
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #e57476;
  box-shadow: 0 0 8px rgba(229, 116, 118, 0.65);
}

.event-row__title {
  min-width: 0;
  overflow: hidden;
  color: #dff8ff;
  font-size: 12px;
  font-weight: 700;
  line-height: 1.5;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.event-row__address {
  gap: 5px;
  margin-top: 3px;
  color: rgba(205, 230, 244, 0.72);
  font-size: 11px;
  line-height: 1.4;

  span:last-child {
    min-width: 0;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
  }
}

.event-row__location-icon {
  flex: 0 0 auto;
  width: 11px;
  height: 11px;
  border: 1px solid rgba(184, 225, 245, 0.8);
  border-radius: 50%;
  box-sizing: border-box;
  position: relative;

  &::after {
    content: '';
    position: absolute;
    left: 50%;
    top: 50%;
    width: 3px;
    height: 3px;
    border-radius: 50%;
    background: rgba(184, 225, 245, 0.9);
    transform: translate(-50%, -50%);
  }
}

.event-row__time {
  color: #d5f4ff;
  font-size: 11px;
  line-height: 1.45;
  text-align: center;
  white-space: nowrap;
}

.event-row__action {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.event-row__status {
  width: 42px;
  height: 17px;
  border: none;
  border-radius: 2px;
  color: #9ff7ff;
  font-size: 11px;
  line-height: 17px;
  text-align: center;
  background: rgba(41, 166, 170, 0.5);
}

.event-row__status--finished {
  color: #b8ffce;
  background: rgba(40, 180, 91, 0.62);
}

.event-box__pager {
  flex: 0 0 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 9px;
  padding-top: 5px;
}

.pager-btn {
  min-width: 12px;
  height: 20px;
  padding: 0;
  border: none;
  color: #ffffff;
  font-size: 14px;
  line-height: 20px;
  text-align: center;
  background: transparent;
  cursor: pointer;
  font-family: inherit;
}

.pager-btn--active {
  color: #76e8ff;
  font-weight: 700;
}

.pager-btn--arrow {
  color: #60dbff;
  font-size: 24px;
  font-weight: 300;
  line-height: 18px;
}

.pager-btn--ellipsis {
  cursor: default;
}

/* ===================== AITalk 样式（搬运） ===================== */
.AIText {
  flex: 1;
  scrollbar-width: none;
  -ms-overflow-style: none;
  display: flex;
  flex-direction: column;
  padding: 8px;
  box-sizing: border-box;
  white-space: pre-wrap;
  word-break: break-word;
  overflow-wrap: anywhere;
  overflow: hidden !important;
  overflow-x: hidden;
  font-size: 20px;
  line-height: 1.6;
  color: #fff;
  cursor: pointer;
}

.AIText__title {
  z-index: 1;
  flex: 0 0 auto;
  padding: 0 2px 6px;
  color: #f8ea56;
  font-size: 22px;
  line-height: 1.35;
  font-weight: 700;
  text-align: left;
}

.AIText__content {
  flex: 1 1 auto;
  min-height: 0;
  overflow-y: auto;
  overflow-x: hidden;
  scrollbar-width: none;
  -ms-overflow-style: none;

  &::-webkit-scrollbar {
    display: none;
    width: 0;
  }
}

:deep(.n-card > .n-card-header .n-card-header__main) {
  color: #FFF !important;
}
</style>
