<template>
  <div class="bz-geo">
    <aside class="side-tabs">
      <div
        v-for="item in sideMenus"
        :key="item.label"
        class="side-tabs__item"
        :class="item.className"
      >
        {{ item.label }}
      </div>
    </aside>

    <main class="content">
      <section class="grid-patrol">
        <div class="patrol-left">
          <div class="danger-row is-clickable" @click="openGeoPopup('geo-‌disaster-list')">
            <img src="./img/text-low-bg.png" alt="" />
            <div class="danger-info">
              <div class="danger-label">地灾隐患点</div>
              <div class="danger-value danger-value--yellow">{{ gridPatrolStats.landGeoHazardPoint }}</div>
            </div>
          </div>
          <div class="danger-row is-clickable" @click="openGeoPopup('geo-‌grid-person-list')">
            <img src="./img/stat-card-round.png" alt="" />
            <div class="danger-info">
              <div class="danger-label">四重网格员</div>
              <div class="danger-value">{{ gridPatrolStats.gridPersonnel }}<span class="danger-value__unit">({{ gridPatrolStats.gridPersonnelPoint }})</span></div>
            </div>
          </div>
        </div>
        <span class="section-header__btn" @click="openDispatchIframe">调度</span>
        <div class="patrol-cards">
          <div
            v-for="item in gridStats"
            :key="item.label"
            class="patrol-card"
            :class="{ 'is-clickable': item.label === '本周巡查点位' }"
            @click="openGridPatrolStat(item)"
          >
            <img :src="item.icon" alt="" />
            <div class="patrol-card__label">{{ item.label }}</div>
            <div class="patrol-card__value">
              {{ item.value }}<span>{{ item.unit }}</span>
            </div>
          </div>
        </div>
      </section>

      <section class="disaster-warning">
        <div class="warning-metrics">
          <div class="warning-device-group">
            <div class="warning-alarm-card is-clickable" @click="openGeoPopup('geo-‌video-risk-list')">
              <div class="warning-alarm-card__num">
                {{ warningStats[0].value }}<span class="danger-value__unit warning-alarm-card__num">({{ warningStats[0].num }})</span><span>起</span>
              </div>
              <div class="warning-alarm-card__base"></div>
              <div class="warning-alarm-card__label">{{ warningStats[0].label }}</div>
            </div>
            <div class="warning-device-bars">
              <div
                v-for="(item, index) in warningStats.slice(1, 3)"
                :key="item.label"
                class="warning-device-bar is-clickable"
                :class="index === 0 ? 'warning-device-bar--deformation' : 'warning-device-bar--displacement'"
                @click="toggleWarningDeviceLayer(index)"
              >
                <span class="warning-device-bar__label">{{ item.label }}</span>
                <span class="warning-device-bar__value">{{ item.value }}<span class="danger-value__unit">({{ item.num }})</span><em>起</em></span>
              </div>
            </div>
          </div>

          <div
            v-for="item in warningStats.slice(3)"
            :key="item.label"
            class="warning-metric is-clickable"
            @click="openWarningList(item.label)"
          >
            <div class="warning-metric__num">
              {{ item.value }}<span>起</span>
            </div>
            <div class="warning-metric__base"></div>
            <div class="warning-metric__label"><span>{{ item.label }}</span></div>
          </div>
        </div>

        <div class="warning-message-list">
          <n-carousel
            v-if="warningWarnMessages.length > 1"
            class="warning-message-carousel"
            autoplay
            :show-dots="false"
            :show-arrow="false"
          >
            <div
              v-for="item in warningWarnMessages"
              :key="item.key"
              class="warning-message is-clickable"
              @click="openWarningMessageDetail(item)"
            >
              <span :class="['warning-message__tag', item.type]">{{ item.tag }}</span>
              <span class="warning-message__text">
                <span class="warning-message__name">{{ item.text }}</span>
                <em v-if="item.levelName" :class="['warning-message__level', item.levelClass]">{{ item.levelName }}</em>
              </span>
              <span class="warning-message__date">{{ item.date }}</span>
            </div>
          </n-carousel>
          <div
            v-else-if="warningWarnMessages.length === 1"
            class="warning-message is-clickable"
            @click="openWarningMessageDetail(warningWarnMessages[0])"
          >
            <span :class="['warning-message__tag', warningWarnMessages[0].type]">{{ warningWarnMessages[0].tag }}</span>
            <span class="warning-message__text">
              <span class="warning-message__name">{{ warningWarnMessages[0].text }}</span>
              <em v-if="warningWarnMessages[0].levelName" :class="['warning-message__level', warningWarnMessages[0].levelClass]">{{ warningWarnMessages[0].levelName }}</em>
            </span>
            <span class="warning-message__date">{{ warningWarnMessages[0].date }}</span>
          </div>
          <n-carousel
            v-if="warningAlarmMessages.length > 1"
            class="warning-message-carousel"
            autoplay
            :show-dots="false"
            :show-arrow="false"
          >
            <div
              v-for="item in warningAlarmMessages"
              :key="item.key"
              class="warning-message is-clickable"
              @click="openWarningMessageDetail(item)"
            >
              <span :class="['warning-message__tag', item.type]">{{ item.tag }}</span>
              <span class="warning-message__text">
                <span class="warning-message__name">{{ item.text }}</span>
                <em v-if="item.levelName" :class="['warning-message__level', item.levelClass]">{{ item.levelName }}</em>
              </span>
              <span class="warning-message__date">{{ item.date }}</span>
            </div>
          </n-carousel>
          <div
            v-else-if="warningAlarmMessages.length === 1"
            class="warning-message is-clickable"
            @click="openWarningMessageDetail(warningAlarmMessages[0])"
          >
            <span :class="['warning-message__tag', warningAlarmMessages[0].type]">{{ warningAlarmMessages[0].tag }}</span>
            <span class="warning-message__text">
              <span class="warning-message__name">{{ warningAlarmMessages[0].text }}</span>
              <em v-if="warningAlarmMessages[0].levelName" :class="['warning-message__level', warningAlarmMessages[0].levelClass]">{{ warningAlarmMessages[0].levelName }}</em>
            </span>
            <span class="warning-message__date">{{ warningAlarmMessages[0].date }}</span>
          </div>
        </div>
      </section>

      <section class="disaster-disposal">
        <div class="disposal-metrics">
          <div
            v-for="item in disasterTabs"
            :key="item.label"
            class="disposal-metric is-clickable"
            @click="openDisasterDangerList(item.type)"
          >
            <span class="disposal-metric__label">{{ item.label }}</span>
            <span class="disposal-metric__value">
              <strong>{{ item.value }}</strong><em>/{{ item.total }}</em>
            </span>
          </div>
        </div>
        <n-carousel
          v-if="dangerRowGroups.length"
          class="disposal-table-carousel"
          :show-dots="false"
          :show-arrow="false"
          autoplay
          :interval="5000"
        >
          <n-data-table
            v-for="(group, index) in dangerRowGroups"
            :key="index"
            class="disposal-table"
            :columns="dangerColumns"
            :data="group"
            :pagination="false"
            :bordered="false"
            :max-height="154"
            size="small"
          />
        </n-carousel>
        <div v-else class="disposal-empty disposal-empty--table">暂无处置事件</div>
        <div class="disposal-title">处置情况</div>
        <n-carousel
          v-if="handleCardGroups.length"
          class="disposal-carousel"
          :show-dots="false"
          :show-arrow="false"
          autoplay
          :interval="5000"
        >
          <div
            v-for="(group, groupIndex) in handleCardGroups"
            :key="groupIndex"
            class="disposal-slide"
          >
            <div
              v-for="item in group"
              :key="item.key"
              class="disposal-card"
              @click="openDisposalImagePreview(item)"
            >
              <div class="disposal-card__image">
                <div v-if="item.imageLoading" class="disposal-card__loading">
                  <span class="disposal-card__spinner"></span>
                  <span>加载中...</span>
                </div>
                <n-image
                  v-else-if="item.imageUrl"
                  class="disposal-card__preview"
                  :src="item.imageUrl"
                  object-fit="cover"
                  preview-disabled
                />
                <div v-else class="disposal-card__empty">暂无图片</div>
              </div>
              <div class="disposal-card__text">{{ item.name }}</div>
            </div>
          </div>
        </n-carousel>
        <div v-else class="disposal-empty">暂无处置事件</div>
      </section>

      <section class="project-governance">
        <div
          v-for="item in projectStats"
          :key="item.label"
          class="project-governance__item is-clickable"
          @click="openProjectList(item.type)"
        >
          <div class="project-governance__value">
            <strong>{{ item.value }}</strong><span>个</span>
          </div>
          <div class="project-governance__label">{{ item.label }}</div>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, h, onMounted, onUnmounted, ref } from 'vue'
import { NCarousel, NDataTable, NImage } from 'naive-ui'
import axios from 'axios'

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

const sourceName = 'BzGeo'
const OPEN_GEO_EVENT = 'OPEN_GEO_DIA'
const OPEN_IFRAME_EVENT = 'OPEN_IFRAME_DIA'
const CHECK_LEGEND_EVENT = 'CHECK_GEO_LEGEND'
const dangerIcon = require('./img/icon_dangers_blue.png')
const patrolIcon = require('./img/icon-danger-patrol.png')
const finishIcon = require('./img/icon-danger-grid.png')
// const BASE_URL = 'http://192.168.112.150:11001'
const BASE_URL = 'http://23.99.16.179:11001'

const sideMenus = [
  { label: '网格巡查', className: 'side-tabs__item--patrol' },
  { label: '地灾预警', className: 'side-tabs__item--warning' },
  { label: '临灾处置', className: 'side-tabs__item--dispose' },
  { label: '工程治理', className: 'side-tabs__item--project' }
]
const gridPatrolStats = ref({
  landGeoHazardPoint: 0,
  gridPersonnel: 0,
  inspectionPoint:0,
  inspectionCompleted: 0,
  inspectionCompletionRate: 0,
  gridPersonnelPoint: 0
})
const gridStats = ref([
  { label: '本周巡查点位', value: 0, unit: '个', icon: dangerIcon },
  { label: '已完成巡查', value: 0, unit: '个', icon: finishIcon },
  { label: '巡查完成率', value: 0, unit: '%', icon: patrolIcon }
])
const warningStats = ref([
  { label: '设备告警', value: 0, num: 0 },
  { label: '形变', value: 0, num: 0 },
  { label: '位移', value: 0, num: 0 },
  { label: '地灾预警', value: 0 },
  { label: '小流域预警', value: 0 }
])
const defaultWarningWarnMessages = [
  { key: 'default_warn_message', tag: '预警', type: 'warn', text: '本日无预警', date: '--' }
]
const defaultWarningAlarmMessages = [
  { key: 'default_alarm_message', tag: '告警', type: 'alarm', text: '本日无告警', date: '--' }
]
const warningWarnMessages = ref([...defaultWarningWarnMessages])
const warningAlarmMessages = ref([...defaultWarningAlarmMessages])
const disasterTabs = ref([
  { label: '灾情', value: 0, total: 0, type: 0 },
  { label: '险情', value: 0, total: 0, type: 1 }
])
const dangerRows = ref([])
const emergencyRows = ref([])
const disposalImageMap = ref<Record<string, { loading: boolean; url: string }>>({})
let disposalImageRequestId = 0
const dangerColumns = [
  { title: '序号', key: 'id', width: 44, align: 'center' },
  { title: '灾险情名称', key: 'name', width: 116 },
  { title: '发生事件', key: 'event', width: 152 },
  {
    title: '灾险情等级',
    key: 'level',
    width: 88,
    render(row) {
      return h('span', { class: 'danger-level' }, row.level)
    }
  },
  { title: '发生时间', key: 'time', width: 76 }
]
const projectStats = ref([
  { label: '项目总数', value: 0 },
  { label: '正在实施', value: 0, type: 1 },
  { label: '已完成', value: 0, type: 2 }
])

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

const openVideoList = (list) =>{
  console.log('list',list)
  eventBus.emit('YZVideoPop:openPop',list)
}

const getResponseData = data => data?.data ?? data ?? {}


const getWarnLevelName = level => {
  const levelMap = {
    '1': '蓝色预警',
    '2': '黄色预警',
    '3': '橙色预警',
    '4': '红色预警'
  }
  return levelMap[String(level)] || ''
}

const getWarnLevelClass = level => {
  const classMap = {
    '1': 'level-blue',
    '2': 'level-yellow',
    '3': 'level-orange',
    '4': 'level-red'
  }
  return classMap[String(level)] || ''
}

const getWarnMessageTime = item => item.time || item.happenTime || item.warnTime || item.createTime || '--'

const toWarningMessage = (item, index, config) => ({
  key: item.eventNum || item.unifiedcode || item.id || `${config.keyPrefix || config.type}_${index}`,
  tag: config.tag,
  type: config.type,
  text: config.getText ? config.getText(item) : item.disasterName || '未知隐患点',
  levelName: config.showLevel === false ? '' : getWarnLevelName(item.warnLevel),
  levelClass: config.showLevel === false ? '' : getWarnLevelClass(item.warnLevel),
  date: getWarnMessageTime(item),
  detail: item,
  detailType: config.detailType,
  detailDiaName: config.detailDiaName
})

const toSmallWatershedWarningMessage = (item, index) => toWarningMessage(item, index, {
  tag: '预警',
  type: 'warn',
  keyPrefix: 'small_watershed_warn',
  getText: () => '小流域预警',
  showLevel: false,
  detailType: 'smallWatershed',
  detailDiaName: 'geo-small-watershed-warning-detail'
})

const getArrayData = value => Array.isArray(value) ? value : []

// 将列表按指定数量分组；空列表返回空数组，避免空状态被 [[]] 误判为有数据。
const chunkList = (list, size) => {
  if (!Array.isArray(list) || !list.length) return []
  const chunks = []
  for (let i = 0; i < list.length; i += size) {
    chunks.push(list.slice(i, i + size))
  }
  return chunks
}

const getEmergencyType = item => {
  const type = item.type ?? item.eventType ?? item.disasterType ?? item.category ?? item.zxqlx
  if (type === 0 || type === '0' || String(type).includes('灾')) return 0
  if (type === 1 || type === '1' || String(type).includes('险')) return 1
  return undefined
}

const getEmergencyLabel = item => {
  const type = getEmergencyType(item)
  return disasterTabs.value.find(tab => tab.type === type)?.label || disasterTabs.value[0]?.label || ''
}

const getEmergencyName = item => item.address || item.hazardname || item.name || item.disasterName || item.pointName || '--'

const getEmergencyFileId = item => item.fileId || item.fileID || item.file_id || item.imgFileId || item.pictureFileId || ''

const revokeDisposalImageUrls = () => {
  Object.values(disposalImageMap.value).forEach(item => {
    if (item?.url?.startsWith('blob:')) {
      URL.revokeObjectURL(item.url)
    }
  })
  disposalImageMap.value = {}
}

const getBase64MimeType = base64 => {
  if (base64.startsWith('/9j/')) return 'image/jpeg'
  if (base64.startsWith('iVBOR')) return 'image/png'
  if (base64.startsWith('R0lGOD')) return 'image/gif'
  if (base64.startsWith('UklGR')) return 'image/webp'
  return 'image/png'
}

const getPreviewPayload = value => {
  if (value == null) return ''
  if (typeof value !== 'string') return value

  const text = value.trim()
  if (!text) return ''

  try {
    const result = JSON.parse(text)
    return result?.data ?? result?.url ?? result?.fileUrl ?? result?.previewUrl ?? result
  } catch (_error) {
    return text
  }
}

const base64ToBlobUrl = value => {
  const text = String(value || '').trim()
  if (!text) return ''
  if (text.startsWith('data:image/')) return text

  const base64 = text.replace(/^data:image\/\w+;base64,/, '').replace(/\s/g, '')
  if (!/^[A-Za-z0-9+/]+={0,2}$/.test(base64)) return ''

  const byteCharacters = window.atob(base64)
  const byteArrays = []
  const sliceSize = 1024

  for (let offset = 0; offset < byteCharacters.length; offset += sliceSize) {
    const slice = byteCharacters.slice(offset, offset + sliceSize)
    const byteNumbers = new Array(slice.length)

    for (let i = 0; i < slice.length; i += 1) {
      byteNumbers[i] = slice.charCodeAt(i)
    }

    byteArrays.push(new Uint8Array(byteNumbers))
  }

  return URL.createObjectURL(new Blob(byteArrays, { type: getBase64MimeType(base64) }))
}

const blobToImageUrl = async blob => {
  if (!blob?.size) return ''

  if (blob.type?.startsWith('image/')) {
    return URL.createObjectURL(blob)
  }

  const text = await blob.text()
  const payload = getPreviewPayload(text)

  if (payload instanceof Blob) {
    return blobToImageUrl(payload)
  }

  if (typeof payload === 'string') {
    return base64ToBlobUrl(payload)
  }

  return ''
}

const requestPreviewFile = async fileId => {
  const api = '/api/boot/system/land/previewFile'
  const res = await axios.get(`${BASE_URL}${api}`, {
    params: { fileId },
    responseType: 'blob'
  })
  const fileData = getPreviewPayload(res?.data?.data ?? res?.data ?? res)

  if (fileData instanceof Blob) {
    return blobToImageUrl(fileData)
  }

  if (fileData instanceof ArrayBuffer) {
    return fileData.byteLength ? URL.createObjectURL(new Blob([fileData])) : ''
  }

  if (typeof fileData === 'string') {
    return base64ToBlobUrl(fileData)
  }

  return ''
}

const setDisposalImageState = (fileId, state) => {
  disposalImageMap.value = {
    ...disposalImageMap.value,
    [fileId]: {
      ...(disposalImageMap.value[fileId] || {}),
      ...state
    }
  }
}

const loadDisposalCardImages = async list => {
  const fileIds = Array.from(new Set(list.map(getEmergencyFileId).filter(Boolean)))
  const requestId = ++disposalImageRequestId

  revokeDisposalImageUrls()

  if (!fileIds.length) return

  disposalImageMap.value = fileIds.reduce((map, fileId) => {
    map[fileId] = { loading: true, url: '' }
    return map
  }, {})

  await Promise.allSettled(fileIds.map(async fileId => {
    try {
      const imageUrl = await requestPreviewFile(fileId)
      if (requestId !== disposalImageRequestId) {
        if (imageUrl?.startsWith('blob:')) URL.revokeObjectURL(imageUrl)
        return
      }
      setDisposalImageState(fileId, { loading: false, url: imageUrl || '' })
    } catch (error) {
      console.error('获取处置情况图片失败:', error)
      if (requestId === disposalImageRequestId) {
        setDisposalImageState(fileId, { loading: false, url: '' })
      }
    }
  }))
}

const dangerRowGroups = computed(() => chunkList(dangerRows.value, 3))

const handleCardGroups = computed(() => {
  const cards = emergencyRows.value.map((item, index) => {
    const fileId = getEmergencyFileId(item)
    const imageState = fileId ? disposalImageMap.value[fileId] : null

    return {
      key: item.id || item.pkid || item.eventNum || `${getEmergencyName(item)}_${index}`,
      label: getEmergencyLabel(item),
      name: getEmergencyName(item),
      fileId,
      imageUrl: imageState?.url || '',
      imageLoading: Boolean(imageState?.loading),
      detail: item
    }
  })

  return chunkList(cards, 3)
})

function openDisposalImagePreview(item) {
  openGeoPopup('geo-image-preview', {
    fileId: item.fileId,
    title: item.name,
    label: item.label,
    detail: item.detail || {}
  })
}

function openGeoPopup(diaName, params = {}) {
  // 点击地灾隐患点时切换图层：首次勾选并落图，再次取消勾选并清除落点。
  if (diaName === 'geo-‌disaster-list') {
    eventBus.emit(CHECK_LEGEND_EVENT, {
      key: 'risk-hidden-danger',
      action: 'toggle'
    })
    return
  }
  if (diaName === 'geo-‌grid-person-list') {
    eventBus.emit(CHECK_LEGEND_EVENT, {
      key: 'device-grid-member',
      action: 'toggle'
    })
    return
  }
  // 点击设备告警时，切换设备点位图层显隐
  if (diaName === 'geo-‌video-risk-list') {
    eventBus.emit('TOGGLE_GEO_DEVICE_ALARM_LAYER', {})
    return
  }
  eventBus.emit(OPEN_GEO_EVENT, {
    diaName,
    params
  })
}

function openWarningList(label) {
  const diaNameMap = {
    // 地灾预警: 'geo-‌earthquake-warning-list',
    小流域预警: 'geo-small-watershed-list'
  }
  const diaName = diaNameMap[label]
  if (!diaName) return
  openGeoPopup(diaName)
}

/** 打开本周巡查点位列表；其他巡查统计卡片暂不响应点击。 */
function openGridPatrolStat(item) {
  if (item?.label !== '本周巡查点位') return
  openGeoPopup('geo-grid-patrol-point-list')
}

/**
 * 切换形变或位移监测点图层。
 * index 为 0 时对应形变监测，为 1 时对应位移监测；
 * BzGeoRiskDevice 收到 toggle 后会同步图层显隐和图例勾选状态。
 */
function toggleWarningDeviceLayer(index) {
  const layerKeys = ['device-deformation', 'device-displacement']
  const key = layerKeys[index]
  if (!key) return

  eventBus.emit(CHECK_LEGEND_EVENT, {
    key,
    action: 'toggle'
  })
}

function openWarningMessageDetail(item) {
  if (!item?.detailDiaName) return
  openGeoPopup(item.detailDiaName, {
    detailType: item.detailType,
    detail: item.detail || {}
  })
}

function openDisasterDangerList(type) {
  openGeoPopup('geo-disaster-danger-list', { type })
}

function openProjectList(type) {
  const params = type === undefined ? {} : { type }
  openGeoPopup('geo-‌project-list', params)
}

async function openDispatchIframe() {
  try {
    // const res = await axios.get(`${BASE_URL}/api/boot/system/land/getLoginToken`)
    // const token = res.data.data
    const token = window.localStorage.getItem('dcqc-tk')
    const url = `http://23.213.34.1:8006/reservoir/#/login?dcqc-tk=${token}&adcode=500103`
    eventBus.emit(OPEN_IFRAME_EVENT, { url, type: 'geo-dispatch' })
  } catch (error) {
    console.error('获取地灾调度登录token失败:', error)
  }
}

const toDangerRow = (item, index) => ({
  id: index + 1,
  name: item.address || '--',
  event: item.hazardname || '--',
  level: item.pkiddLevel || '--',
  time: item.happentime || '--'
})

async function fetchGridPatrol() {
  try {
    const res = await axios.get(`${BASE_URL}/api/boot/system/land/gridPatrol`)
    const data = getResponseData(res?.data)
    gridPatrolStats.value = {
      landGeoHazardPoint: Number(data.landGeoHazardPoint) || 0,
      gridPersonnel: Number(data.gridPersonnel) || 0,
      inspectionPoint: Number(data.inspectionPoint) || 0,
      inspectionCompleted: Number(data.inspectionCompleted) || 0,
      inspectionCompletionRate: Number(data.inspectionCompletionRate).toFixed(2) || 0,
      gridPersonnelPoint: Number(data.gridPersonnelPoint) || 0
    }
    gridStats.value = [
      { label: '本周巡查点位', value: gridPatrolStats.value.inspectionPoint, unit: '个', icon: dangerIcon },
      { label: '已完成巡查', value: gridPatrolStats.value.inspectionCompleted, unit: '个', icon: finishIcon },
      { label: '巡查完成率', value: gridPatrolStats.value.inspectionCompletionRate, unit: '%', icon: patrolIcon }
    ]
  } catch (error) {
    console.error('获取网格巡查数据失败:', error)
  }
}

async function fetchGeoWarn() {
  try {
    const res = await axios.get(`${BASE_URL}/api/boot/system/land/geoWarn`)
    const data = getResponseData(res?.data)
    warningStats.value = [
      { label: '设备告警', value: Number(data.deviceAlarm) || 0, num: Number(data.todayDeviceAlarmPoint) || 0 },
      { label: '形变', value: Number(data.deformation) || 0, num: Number(data.todayDeformationPoint) || 0 },
      { label: '位移', value: Number(data.displacement) || 0, num: Number(data.todayDisplacementPoint) || 0 },
      { label: '地灾预警', value: Number(data.geologicalDisasterWarning) || 0 },
      { label: '小流域预警', value: Number(data.smallWatershedWarning) || 0 }
    ]
    const alarmMessages = getArrayData(data.todayDeviceAlarm)
    const geologicalWarningMessages = [
      ...getArrayData(data.todayGeologicalDisasterWarning)
    ]
    const smallWatershedWarningMessages = getArrayData(data.todaySmallWatershedWarning)
    const warnMessages = [
      ...geologicalWarningMessages.map((item, index) => toWarningMessage(item, index, {
        tag: '预警',
        type: 'warn',
        keyPrefix: 'geo_disaster_warn',
        detailType: 'geological',
        detailDiaName: 'geo-geological-warning-detail'
      })),
      ...smallWatershedWarningMessages.map((item, index) => toSmallWatershedWarningMessage(item, index))
    ]
    warningAlarmMessages.value = alarmMessages.length
      ? alarmMessages.map((item, index) => toWarningMessage(item, index, {
        tag: '告警',
        type: 'alarm',
        keyPrefix: 'device_alarm',
        detailType: 'deviceAlarm',
        detailDiaName: 'geo-device-alarm-detail'
      }))
      : [...defaultWarningAlarmMessages]
    warningWarnMessages.value = warnMessages.length
      ? warnMessages
      : [...defaultWarningWarnMessages]
  } catch (error) {
    console.error('获取地灾预警数据失败:', error)
  }
}

async function fetchEngineering() {
  try {
    const res = await axios.get(`${BASE_URL}/api/boot/system/land/engineering`)
    const data = getResponseData(res?.data)
    projectStats.value = [
      { label: '项目总数', value: Number(data.projectAll) || 0 },
      { label: '正在实施', value: Number(data.projectProgress) || 0, type: 1 },
      { label: '已完成', value: Number(data.projectComplete) || 0, type: 2 }
    ]
  } catch (error) {
    console.error('获取工程治理数据失败:', error)
  }
}

async function fetchEmergency() {
  try {
    const res = await axios.get(`${BASE_URL}/api/boot/system/land/emergency`)
    const data = getResponseData(res?.data)
    disasterTabs.value = [
      { label: '灾情', value: Number(data.disasterComplete) || 0, total: Number(data.disasterAll) || 0, type: 0 },
      { label: '险情', value: Number(data.dangerComplete) || 0, total: Number(data.dangerAll) || 0, type: 1 }
    ]
    const list = getArrayData(data.list)
    emergencyRows.value = list
    dangerRows.value = list.map(toDangerRow)
    loadDisposalCardImages(list)
  } catch (error) {
    console.error('获取临灾处置数据失败:', error)
  }
}

onMounted(() => {
  fetchGridPatrol()
  fetchGeoWarn()
  fetchEmergency()
  fetchEngineering()
})

onUnmounted(() => {
  eventBus.emit('CLOSE_GEO_DEVICE_ALARM_LAYER', {})
  disposalImageRequestId += 1
  revokeDisposalImageUrls()
})
</script>



<script lang="ts">
export default {
  name: "BzGeoLog",
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

.bz-geo {
  width: 540px;
  height: 928px;
  display: flex;
  box-sizing: border-box;
  overflow: hidden;
  color: #e8f8ff;
  // background: #061d35;
  font-family: 'BzAlibabaPuHuiTi', Microsoft YaHei, PingFang SC, Arial, sans-serif;
}

.side-tabs {
  width: 24px;
  flex-shrink: 0;
  display: grid;
  grid-template-rows: 139px 279px 370px 114px;
}

.side-tabs__item {
  display: flex;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
  padding: 0 4px;
  color: #e7fcff;
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  font-size: 16px;
  font-weight: 700;
  line-height: 1.28;
  text-align: center;
  writing-mode: vertical-rl;
  letter-spacing: 5px;
}

.side-tabs__item--patrol {
  background:
    linear-gradient(180deg, #ffffff 0%, #bffcff 42%, #1b8991 100%) text,
    linear-gradient(180deg, #1b8991 0%, #116f85 100%) border-box;
}

.side-tabs__item--warning {
  background:
    linear-gradient(180deg, #ffffff 0%, #fff0a8 42%, #806f14 100%) text,
    linear-gradient(180deg, #806f14 0%, #665f13 100%) border-box;
}

.side-tabs__item--dispose {
  background:
    linear-gradient(180deg, #ffffff 0%, #d9ffe9 42%, #0a7b57 100%) text,
    linear-gradient(180deg, #0a7b57 0%, #066b4e 100%) border-box;
}

.side-tabs__item--project {
  background:
    linear-gradient(180deg, #ffffff 0%, #bfe7ff 42%, #075d9e 100%) text,
    linear-gradient(180deg, #075d9e 0%, #064b86 100%) border-box;
}

.content {
  width: 516px;
  height: 100%;
  box-sizing: border-box;
}

.is-clickable {
  cursor: pointer;
}

.is-clickable:hover {
  filter: brightness(1.12);
}

.grid-patrol {
  position: relative;
  width: 476px;
  height: 139px;
  margin-left: 20px;
  box-sizing: border-box;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  background:
    linear-gradient(90deg, rgba(4, 23, 48, 0.34), rgba(4, 31, 58, 0.18)),
    url('./img/grid-patrol-bg.png') center no-repeat;
  border: 1px solid rgba(31, 117, 221, 0.75);
  border-radius: 3px;
}

.patrol-left {
  flex-shrink: 0;
  display: grid;
  grid-template-rows: repeat(2, 1fr);
  align-items: center;
}

.danger-row {
  min-width: 0;
  display: flex;
  align-items: center;

  img {
    width: 73px;
    height: 72px;
    flex-shrink: 0;
  }
}

.danger-info {
  min-width: 0;
}

.danger-label {
  color: #f0f8ff;
  font-size: 14px;
  font-weight: 700;
  line-height: 20px;
  white-space: nowrap;
  cursor: pointer;
}

.danger-value {
  margin-top: 3px;
  color: #35fff5;
  background: linear-gradient(180deg, #35fff5 0%, #35fff5 48%, #ffffff 100%);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  font-family: 'BzDDIN', Arial, sans-serif;
  font-size: 24px;
  font-weight: 800;
  line-height: 1;
}

.danger-value--yellow {
  color: #ffd039;
  background: linear-gradient(180deg, #ffd039 0%, #ffd039 48%, #ffffff 100%);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}

.patrol-cards {
  position: relative;
  top:10px;
  width: 326px;
  height: 103px;
  display: flex;
  align-items: center;
  justify-content: space-evenly;
  flex-shrink: 0;
}

.patrol-card {
  width: 102px;
  height: 103px;
  flex-shrink: 0;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: center;
  background: url('./img/alarm-circle-bg.png') center no-repeat;

  img {
    width: 30px;
    height: 30px;
    margin: 0 0 6px 9px;
  }
}

.patrol-card__label {
  margin-left: 12px;
  color: #edf7ff;
  background: linear-gradient(180deg, #5fbcff 0%, #ffffff 100%);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  font-size: 14px;
  font-weight: 700;
  line-height: 20px;
  white-space: nowrap;
}

.patrol-card__value {
  margin: 6px 0 0 12px;
  color: #38fff5;
  background: linear-gradient(180deg, #38fff5 0%, #38fff5 48%, #ffffff 100%);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  font-family: 'BzDDIN', Arial, sans-serif;
  font-size: 28px;
  font-weight: 800;
  line-height: 1;

  span {
    margin-left: 2px;
    color: #a9d9ff;
    background: none;
    -webkit-text-fill-color: #a9d9ff;
    font-family: 'BzAlibabaPuHuiTi', Microsoft YaHei, sans-serif;
    font-size: 14px;
    font-weight: 700;
  }
}

.disaster-warning {
  width: 476px;
  margin-left: 20px;
  box-sizing: border-box;
}

.warning-metrics {
  width: 476px;
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.warning-device-group {
  width: 270px;
  height: 200px;
  box-sizing: border-box;
  display: flex;
  align-items: center;
  gap: 10px;
  background: url('./img/ba-weiyi.png') center no-repeat;
}

.warning-alarm-card {
  width: 96px;
  height: 108px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.warning-alarm-card__num {
  position: relative;
  z-index: 1;
  color: #ffd039 !important;
  background: linear-gradient(180deg, #ffd039 0%, #ffd039 48%, #ffffff 100%);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  font-family: 'BzDDIN', Arial, sans-serif;
  font-size: 28px;
  font-weight: 800;
  line-height: 1;

  span {
    margin-left: 3px;
    color: #8bbce6;
    background: none;
    -webkit-text-fill-color: #8bbce6;
    font-family: 'BzAlibabaPuHuiTi', Microsoft YaHei, sans-serif;
    font-size: 13px;
    font-weight: 700;
  }
}

.warning-alarm-card__base {
  width: 88px;
  height: 54px;
  margin-top: -6px;
  background: url('./img/project-card-bg.png') center / 88px auto no-repeat;
  flex-shrink: 0;
}

.warning-alarm-card__label {
  width: 86px;
  height: 30px;
  margin-top: 2px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  background: url('./img/warning-label-bg.png') center no-repeat;
  font-size: 14px;
  font-weight: 700;
  white-space: nowrap;
}

.warning-device-bars {
  flex: 1;
  min-width: 0;
  height: 82px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.warning-device-bar {
  height: 34px;
  box-sizing: border-box;
  padding: 0 10px 0 14px;
  display: flex;
  align-items: center;
  background-size: auto;
  background-repeat: no-repeat;
}

.warning-device-bar--deformation {
  background-image: url('./img/beijing.png');
}

.warning-device-bar--displacement {
  background-image: url('./img/beijing2.png');
}

.warning-device-bar__label {
  position: relative;
  top: 6px;
  left:10px;
  color: #dff4ff;
}

.warning-device-bar__value {
  position: relative;
  top: 4px;
  margin-left: auto;
  color: #38fff5;
  font-family: 'BzDDIN', Arial, sans-serif;
  font-size: 22px;
  font-weight: 800;
  line-height: 1;

  em {
    margin-left: 2px;
    color: #8bbce6;
    font-family: 'BzAlibabaPuHuiTi', Microsoft YaHei, sans-serif;
    font-size: 13px;
    font-style: normal;
    font-weight: 700;
  }
}

.warning-metric {
  height: 200px;
  width: 94px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  align-items: center;
  background: url('./img/frame-tall.png') center no-repeat;
  background-size: 94px 200px;
}

.warning-metric__num {
  position: relative;
  top:10px;
  margin-top: 20px;
  color: #ffd039;
  background: linear-gradient(180deg, #ffd039 0%, #ffd039 48%, #ffffff 100%);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  font-family: 'BzDDIN', Arial, sans-serif;
  font-size: 32px;
  font-weight: 800;
  line-height: 1;

  span {
    margin-left: 3px;
    color: #8bbce6;
    background: none;
    -webkit-text-fill-color: #8bbce6;
    font-family: 'BzAlibabaPuHuiTi', Microsoft YaHei, sans-serif;
    font-size: 14px;
    font-weight: 700;
  }
}

.warning-metric__base {
  width: 88px;
  height: 63px;
  margin-top: -16px;
  background: url('./img/project-card-bg.png') center / 88px auto no-repeat;
  flex-shrink: 0;
}

.warning-metric__label {
  width: 86px;
  height: 30px;
  margin-top: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: url('./img/warning-label-bg.png') center no-repeat;
  font-size: 14px;
  font-weight: 700;
  white-space: nowrap;

  span {
    color: #ffffff;
    background: linear-gradient(180deg, #ffffff 0%, #5fbcff 100%);
    -webkit-background-clip: text;
    background-clip: text;
    -webkit-text-fill-color: transparent;
  }
}

.warning-message-list {
  width: 476px;
  display: flex;
  flex-direction: column;
  gap: 7px;
}

.warning-message-carousel {
  width: 476px;
  height: 36px;

  :deep(.n-carousel__slides),
  :deep(.n-carousel__slide) {
    height: 36px;
  }
}

.warning-message {
  width: 476px;
  height: 36px;
  box-sizing: border-box;
  display: grid;
  grid-template-columns: 52px 1fr 112px;
  align-items: center;
  background: url('./img/warning-row-bg.png') center no-repeat;
  border: 1px solid rgba(33, 145, 244, 0.76);
  color: #e7f8ff;
  font-size: 14px;
}

.warning-message__tag {
  width: 34px;
  height: 22px;
  margin-left: 10px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: #ffe66a;
  border: 1px solid #ffe66a;
  font-size: 14px;
  font-weight: 700;

  &.alarm {
    color: #ff6d9c;
    border-color: #ff6d9c;
  }
}

.warning-message__text {
  min-width: 0;
  display: flex;
  align-items: center;
  gap: 8px;
  overflow: hidden;
  white-space: nowrap;
}

.warning-message__name {
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
}

.warning-message__level {
  flex-shrink: 0;
  font-size: 12px;
  font-style: normal;
  font-weight: 700;

  &.level-blue {
    color: #2ea8ff;
  }

  &.level-yellow {
    color: #ffe66a;
  }

  &.level-orange {
    color: #ff9f43;
  }

  &.level-red {
    color: #ff5b6b;
  }
}

.warning-message__date {
  color: #9bc9e8;
  text-align: center;
}

.disaster-disposal {
  width: 476px;
  height: 384px;
  margin-left: 20px;
  margin-top: 8px;
  box-sizing: border-box;
}

.disposal-metrics {
  width: 476px;
  height: 36px;
  display: flex;
  justify-content: space-between;
}

.disposal-metric {
  width: 232px;
  height: 36px;
  box-sizing: border-box;
  display: flex;
  align-items: center;
  padding: 0 12px;
  background: url('./img/bg_list_data.png') center / 232px 36px no-repeat;
  border: 1px solid rgba(33, 145, 244, 0.56);
}

.disposal-metric__label {
  color: #dff4ff;
  font-size: 16px;
  font-weight: 700;
}

.disposal-metric__value {
  margin-left: auto;
  display: inline-flex;
  align-items: baseline;
  color: #7fd6ff;

  strong {
    color: #ffd039;
    background: linear-gradient(180deg, #ffd039 0%, #ffd039 48%, #ffffff 100%);
    -webkit-background-clip: text;
    background-clip: text;
    -webkit-text-fill-color: transparent;
    font-family: 'BzDDIN', Arial, sans-serif;
    font-size: 24px;
    font-weight: 800;
    line-height: 1;
  }

  em {
    margin-left: 2px;
    color: #8bbce6;
    font-family: 'BzDDIN', Arial, sans-serif;
    font-size: 16px;
    font-style: normal;
    font-weight: 700;
  }
}

.disposal-table-carousel {
  width: 476px;
  height: 204px;
  margin-top: 8px;
  overflow: hidden;

  :deep(.n-carousel__slides),
  :deep(.n-carousel__slide) {
    height: 204px;
  }
}

.disposal-table {
  width: 476px;
  height: 204px;
  overflow: hidden;

  :deep(.n-data-table-wrapper) {
    background: rgba(5, 33, 62, 0.36);
  }

  :deep(.n-data-table-base-table-body) {
    max-height: 154px !important;
  }

  :deep(.n-data-table-table) {
    background: transparent;
    font-size: 14px;
  }

  :deep(.n-data-table-th) {
    height: 50px;
    padding: 0 6px;
    color: #8bbce6;
    background: rgba(17, 84, 139, 0.7);
    border: 0;
    font-size: 14px;
    font-weight: 500;
  }

  :deep(.n-data-table-td) {
    height: 50px;
    padding: 3px 6px;
    color: #e8f8ff;
    background: rgba(14, 63, 106, 0.38);
    border: 0;
    font-size: 14px;
    line-height: 18px;
    vertical-align: middle;
  }

  :deep(.n-data-table-td:first-child),
  :deep(.n-data-table-td:last-child) {
    font-family: 'BzDDIN', Arial, sans-serif;
  }

  :deep(.n-data-table-tr:nth-child(even) .n-data-table-td) {
    background: rgba(23, 78, 120, 0.5);
  }

  :deep(.n-scrollbar-rail--vertical) {
    width: 4px;
  }

  :deep(.n-scrollbar-rail__scrollbar) {
    background: rgba(71, 202, 255, 0.68);
  }
}

:deep(.danger-level) {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 40px;
  height: 24px;
  box-sizing: border-box;
  color: #ddffe7;
  background: rgba(23, 148, 68, 0.82);
  border: 1px solid #35ff6c;
  font-size: 14px;
}

.disposal-title {
  height: 24px;
  margin-top: 8px;
  display: flex;
  align-items: center;
  color: #bceeff;
  font-size: 16px;
  font-weight: 700;

  &::before {
    content: '';
    width: 0;
    height: 0;
    margin-right: 6px;
    border-top: 5px solid transparent;
    border-bottom: 5px solid transparent;
    border-left: 7px solid #8edbff;
  }
}

.disposal-carousel {
  width: 476px;
  height: 82px;
  margin-top: 4px;
}

.disposal-empty {
  width: 476px;
  height: 82px;
  margin-top: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: rgba(216, 236, 255, 0.72);
  font-size: 14px;
  background: rgba(10, 70, 112, 0.36);
  border: 1px solid rgba(84, 205, 255, 0.28);
  box-sizing: border-box;
}

.disposal-empty--table {
  height: 204px;
  margin-top: 8px;
}

.disposal-slide {
  width: 476px;
  height: 82px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.disposal-card {
  position: relative;
  width: 152px;
  height: 78px;
  box-sizing: border-box;
  padding: 4px;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  background: rgba(10, 70, 112, 0.72);
  border: 1px solid rgba(84, 205, 255, 0.65);
  box-shadow: inset 0 0 12px rgba(73, 188, 255, 0.18);
  cursor: pointer;
  overflow: hidden;
}

.disposal-card__image {
  position: relative;
  width: 100%;
  flex: 1;
  min-height: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  color: rgba(216, 236, 255, 0.78);
  background: rgba(0, 21, 54, 0.36);
}

.disposal-card__preview {
  width: 100%;
  height: 100%;

  :deep(img) {
    width: 100%;
    height: 100%;
    display: block;
  }
}

.disposal-card__loading,
.disposal-card__empty {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
  font-size: 12px;
}

.disposal-card__spinner {
  width: 12px;
  height: 12px;
  border: 2px solid rgba(142, 219, 255, 0.32);
  border-top-color: #8edbff;
  border-radius: 50%;
  animation: disposal-card-loading 0.8s linear infinite;
}

@keyframes disposal-card-loading {
  to {
    transform: rotate(360deg);
  }
}

.disposal-card__text {
  height: 20px;
  padding-left: 6px;
  padding-right: 6px;
  color: #ffffff;
  background: rgba(6, 47, 78, 0.76);
  font-size: 12px;
  line-height: 20px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.disposal-card__tag {
  position: absolute;
  left: 8px;
  top: 8px;
  z-index: 1;
  height: 20px;
  padding: 0 8px;
  color: #ffffff;
  background: rgba(0, 122, 255, 0.78);
  border: 1px solid rgba(142, 219, 255, 0.72);
  font-size: 12px;
  line-height: 18px;
}

.project-governance {
  width: 476px;
  height: 101px;
  margin-left: 20px;
  box-sizing: border-box;
  display: flex;
  align-items: stretch;
  justify-content: space-between;
}

.project-governance__item {
  width: 157px;
  height: 101px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: space-around;
}

.project-governance__value {
  position: relative;
  width: 157px;
  height: 53px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: url('./img/frame-thumb.png') center no-repeat;
}

.project-governance__value strong {
  color: #38fff5;
  background: linear-gradient(180deg, #38fff5 0%, #38fff5 48%, #ffffff 100%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  font-family: 'BzDDIN', Arial, sans-serif;
  font-size: 24px;
  font-weight: 800;
  line-height: 1;
}

.project-governance__value span {
  margin-left: 2px;
  color: #8bbce6;
  font-family: 'BzAlibabaPuHuiTi', Microsoft YaHei, sans-serif;
  font-size: 14px;
  font-weight: 700;
}

.project-governance__label {
  margin-top: 0;
  margin-bottom: 0;
  color: #eaf8ff;
  font-size: 16px;
  line-height: 20px;
  white-space: nowrap;

  &::before,
  &::after {
    content: '';
    display: inline-block;
    width: 0;
    height: 0;
    margin: 0 8px 2px;
    border-top: 5px solid transparent;
    border-bottom: 5px solid transparent;
  }

  &::before {
    border-left: 7px solid #ffe37a;
  }

  &::after {
    border-right: 7px solid #ffe37a;
  }
}
.section-header__btn{
  position: absolute;
  right: 0;
  top:0;
  padding: 0px 6px;
  color: #fff;
  background: rgba(26, 120, 194, 0.8);
  border: 1px solid #3bb3ff;
  border-radius: 3px;
  font-size: 12px;
  cursor: pointer;
  align-self: center;
}
.danger-value__unit{
  font-size: 14px !important;
}

.warning-alarm-card__num .danger-value__unit {
  color: #ffd039 !important;
  -webkit-text-fill-color: #ffd039 !important;
}

</style>
