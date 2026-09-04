<template>
  <div
    ref="houseRootRef"
    class="bz-house"
    :class="{
      'bz-house--no-bg': !showBackground,
      'bz-house--triple': isTripleScreen
    }"
    :style="containerStyle"
    @wheel.capture="handleHouseScrollWheel"
  >
    <BZHouseHotWorkTriple v-if="isTripleScreen" />
    <div v-else class="house-inner">
      <div ref="houseScrollRef" class="house-scroll">
      <section class="house-block house-block--managed">
        <div class="block-content">
          <div class="block-title-bar" :class="{ 'block-title-bar--module': isTripleScreen }" @click="openHouseDispatch('online')"><span>网约房</span></div>

          <div class="dual-title">
            <div class="title-item data-badge--clickable" @click="openHouseList('网约房')">纳管情况</div>
            <div class="title-item title-item--clickable" @click="openHousePersonList('网约房')">重点人员</div>
          </div>

          <div class="stats-row">
            <div class="data-badge data-badge--blue">
              <span class="badge-label">纳管中</span>
              <strong>{{ onlineManagedStatus.managingCount }}</strong>
            </div>
            <div class="data-badge data-badge--green">
              <span class="badge-label">已纳管</span>
              <strong>{{ onlineManagedStatus.managedCount }}</strong>
            </div>
            <div class="data-badge data-badge--blue">
              <span class="badge-label">未成年</span>
              <strong>{{ onlineManagedStatus.minorCount }}</strong>
            </div>
            <div class="data-badge data-badge--orange">
              <span class="badge-label">重点关注</span>
              <strong>{{ onlineManagedStatus.focusCount }}</strong>
            </div>
          </div>

          <div class="title-item risk-badge--clickable" @click="openHouseRiskPerception('网约房')">重点事件</div>
          <div class="risk-row">
            <div class="risk-badge">
              <span>风险感知</span>
              <strong><em>{{ onlineEventStatus.riskAwarenessCount }}</em><i>/</i><b>{{ onlineEventStatus.riskAwarenessTotal }}</b></strong>
            </div>
            <div class="risk-badge">
              <span>房屋风险</span>
              <strong><em>{{ onlineEventStatus.houseRiskCount }}</em><i>/</i><b>{{ onlineEventStatus.houseRiskTotal }}</b></strong>
            </div>
            <div class="risk-badge">
              <span>人员风险</span>
              <strong><em>{{ onlineEventStatus.peopleRiskCount }}</em><i>/</i><b>{{ onlineEventStatus.peopleRiskTotal }}</b></strong>
            </div>
          </div>

          <div class="color-panel color-panel--clickable" @click="openHouseColorCode('网约房')">
            <div class="color-panel__title">四色评价</div>
            <div class="color-grid">
              <div class="color-badge color-badge--green">
                <span class="color-label">绿码</span>
                <strong><span class="color-total">{{ onlineFourColor.green.count }}</span><span class="color-separator">/</span><span class="color-rate">{{ onlineFourColor.green.rate }}</span><span class="color-unit">%</span></strong>
              </div>
              <div class="color-badge color-badge--blue">
                <span class="color-label">蓝码</span>
                <strong><span class="color-total">{{ onlineFourColor.blue.count }}</span><span class="color-separator">/</span><span class="color-rate">{{ onlineFourColor.blue.rate }}</span><span class="color-unit">%</span></strong>
              </div>
              <div class="color-badge color-badge--yellow">
                <span class="color-label">黄码</span>
                <strong><span class="color-total">{{ onlineFourColor.yellow.count }}</span><span class="color-separator">/</span><span class="color-rate">{{ onlineFourColor.yellow.rate }}</span><span class="color-unit">%</span></strong>
              </div>
              <div class="color-badge color-badge--red">
                <span class="color-label">红码</span>
                <strong><span class="color-total">{{ onlineFourColor.red.count }}</span><span class="color-separator">/</span><span class="color-rate">{{ onlineFourColor.red.rate }}</span><span class="color-unit">%</span></strong>
              </div>
            </div>
          </div>

          <div class="photo-row photo-row--empty">
            <div class="photo-empty">暂无事件</div>
          </div>
        </div>
      </section>

      <section class="house-block house-block--group">
        <div class="block-content">
          <div class="block-title-bar" :class="{ 'block-title-bar--module': isTripleScreen }" @click="openHouseDispatch('group')"><span>群租空间</span></div>

          <div class="dual-title">
            <div class="title-item data-badge--clickable" @click="openHouseList('群租空间')">房屋情况</div>
            <div class="title-item title-item--clickable" @click="openHousePersonList('群租空间')">重点人员</div>
          </div>

          <div class="stats-row">
            <div class="data-badge data-badge--blue">
              <span class="badge-label">纳管中</span>
              <strong>{{ groupManagedStatus.managingCount }}</strong>
            </div>
            <div class="data-badge data-badge--green">
              <span class="badge-label">已纳管</span>
              <strong>{{ groupManagedStatus.managedCount }}</strong>
            </div>
            <div class="data-badge data-badge--blue">
              <span class="badge-label">未成年</span>
              <strong>{{ groupManagedStatus.minorCount }}</strong>
            </div>
            <div class="data-badge data-badge--orange">
              <span class="badge-label">重点关注</span>
              <strong>{{ groupManagedStatus.focusCount }}</strong>
            </div>
          </div>

          <div class="title-item risk-badge--clickable" @click="openHouseRiskPerception('群租空间')">重点事件</div>
          <div class="risk-row">
            <div class="risk-badge">
              <span>风险感知</span>
              <strong><em>{{ groupEventStatus.riskAwarenessCount }}</em><i>/</i><b>{{ groupEventStatus.riskAwarenessTotal }}</b></strong>
            </div>
            <div class="risk-badge">
              <span>房屋风险</span>
              <strong><em>{{ groupEventStatus.houseRiskCount }}</em><i>/</i><b>{{ groupEventStatus.houseRiskTotal }}</b></strong>
            </div>
            <div class="risk-badge">
              <span>人员风险</span>
              <strong><em>{{ groupEventStatus.peopleRiskCount }}</em><i>/</i><b>{{ groupEventStatus.peopleRiskTotal }}</b></strong>
            </div>
          </div>

          <div class="color-panel color-panel--clickable" @click="openHouseColorCode('群租空间')">
            <div class="color-panel__title">四色评价</div>
            <div class="color-grid">
              <div class="color-badge color-badge--green">
                <span class="color-label">绿码</span>
                <strong><span class="color-total">242</span><span class="color-separator">/</span><span class="color-rate">99.99</span><span class="color-unit">%</span></strong>
              </div>
              <div class="color-badge color-badge--blue">
                <span class="color-label">蓝码</span>
                <strong><span class="color-total">242</span><span class="color-separator">/</span><span class="color-rate">99.99</span><span class="color-unit">%</span></strong>
              </div>
              <div class="color-badge color-badge--yellow">
                <span class="color-label">黄码</span>
                <strong><span class="color-total">242</span><span class="color-separator">/</span><span class="color-rate">99.99</span><span class="color-unit">%</span></strong>
              </div>
              <div class="color-badge color-badge--red">
                <span class="color-label">红码</span>
                <strong><span class="color-total">242</span><span class="color-separator">/</span><span class="color-rate">99.99</span><span class="color-unit">%</span></strong>
              </div>
            </div>
          </div>

          <n-carousel
            v-if="groupVideoSlides.length"
            class="photo-carousel"
            autoplay
            draggable
            :interval="5000"
            :show-dots="false"
            :show-arrow="false"
          >
            <div v-for="(slide, slideIndex) in groupVideoSlides" :key="slideIndex" class="photo-row photo-row--slide">
              <div
                v-for="item in slide"
                :key="item.id || item.eventNo || item.imgUrl"
                class="photo-card photo-card--clickable"
                @click="openWarningDetail(item, '群租空间')"
              >
                <img class="photo-card__image" :src="item.imgUrl || placeholderImg" alt="" />
                <div class="photo-card__title" :title="item.title">{{ item.title || '-' }}</div>
              </div>
            </div>
          </n-carousel>
          <div v-else class="photo-row photo-row--empty">
            <div class="photo-empty">暂无事件</div>
          </div>
        </div>
      </section>

      <section class="house-block house-block--business">
        <div class="block-content">
          <div class="block-title-bar" :class="{ 'block-title-bar--module': isTripleScreen }" @click="openHouseDispatch('business')"><span>商改住</span></div>

          <div class="dual-title">
            <div class="title-item data-badge--clickable" @click="openHouseList('商改住')">纳管情况</div>
            <div class="title-item title-item--clickable" @click="openHousePersonList('商改住')">重点人员</div>
          </div>

          <div class="stats-row">
            <div class="data-badge data-badge--blue">
              <span class="badge-label">纳管中</span>
              <strong>{{ bizManagedStatus.managingCount }}</strong>
            </div>
            <div class="data-badge data-badge--green">
              <span class="badge-label">已纳管</span>
              <strong>{{ bizManagedStatus.managedCount }}</strong>
            </div>
            <div class="data-badge data-badge--blue">
              <span class="badge-label">未成年</span>
              <strong>{{ bizManagedStatus.minorCount }}</strong>
            </div>
            <div class="data-badge data-badge--orange">
              <span class="badge-label">重点关注</span>
              <strong>{{ bizManagedStatus.focusCount }}</strong>
            </div>
          </div>

          <div class="title-item risk-badge--clickable" @click="openHouseRiskPerception('商改住')">重点事件</div>
          <div class="risk-row">
            <div class="risk-badge risk-badge--clickable" @click="openHouseInspectionDetail">
              <span>巡查上报</span>
              <strong><em>{{ bizEventStatus.inspectionCount }}</em><i>/</i><b>{{ bizEventStatus.inspectionTotal }}</b></strong>
            </div>
            <div class="risk-badge risk-badge--clickable" @click="openHouseRiskWarning">
              <span>风险感知</span>
              <strong><em>{{ bizEventStatus.riskAwarenessCount }}</em><i>/</i><b>{{ bizEventStatus.riskAwarenessTotal }}</b></strong>
            </div>
          </div>

          <div class="color-panel color-panel--clickable" @click="openHouseColorCode('商改住')">
            <div class="color-panel__title">四色评价</div>
            <div class="color-grid">
              <div class="color-badge color-badge--green">
                <span class="color-label">绿码</span>
                <strong><span class="color-total">{{ bizFourColor.green.count }}</span><span class="color-separator">/</span><span class="color-rate">{{ bizFourColor.green.rate }}</span><span class="color-unit">%</span></strong>
              </div>
              <div class="color-badge color-badge--blue">
                <span class="color-label">蓝码</span>
                <strong><span class="color-total">{{ bizFourColor.blue.count }}</span><span class="color-separator">/</span><span class="color-rate">{{ bizFourColor.blue.rate }}</span><span class="color-unit">%</span></strong>
              </div>
              <div class="color-badge color-badge--yellow">
                <span class="color-label">黄码</span>
                <strong><span class="color-total">{{ bizFourColor.yellow.count }}</span><span class="color-separator">/</span><span class="color-rate">{{ bizFourColor.yellow.rate }}</span><span class="color-unit">%</span></strong>
              </div>
              <div class="color-badge color-badge--red">
                <span class="color-label">红码</span>
                <strong><span class="color-total">{{ bizFourColor.red.count }}</span><span class="color-separator">/</span><span class="color-rate">{{ bizFourColor.red.rate }}</span><span class="color-unit">%</span></strong>
              </div>
            </div>
          </div>

          <n-carousel
            v-if="bizVideoSlides.length"
            class="photo-carousel"
            autoplay
            draggable
            :interval="5000"
            :show-dots="false"
            :show-arrow="false"
          >
            <div v-for="(slide, slideIndex) in bizVideoSlides" :key="slideIndex" class="photo-row photo-row--slide">
              <div
                v-for="item in slide"
                :key="item.id || item.eventNo || item.imgUrl"
                class="photo-card photo-card--clickable"
                @click="openWarningDetail(item, '商改住')"
              >
                <img class="photo-card__image" :src="item.imgUrl || placeholderImg" alt="" />
                <div class="photo-card__title" :title="item.title">{{ item.title || '-' }}</div>
              </div>
            </div>
          </n-carousel>
          <div v-else class="photo-row photo-row--empty">
            <div class="photo-empty">暂无事件</div>
          </div>
        </div>
      </section>

      <section class="access-row">
        <aside class="block-label block-label--small">
          <span>住</span>
          <span>改</span>
          <span>商</span>
        </aside>
        <div class="access-card">
          <img :src="frameIcon" alt="" />
          <span>正在接入中</span>
        </div>
        <aside class="block-label block-label--small">
          <span>住</span>
          <span>改</span>
          <span>仓</span>
        </aside>
        <div class="access-card">
          <img :src="frameIcon" alt="" />
          <span>正在接入中</span>
        </div>
      </section>
      </div>

      <section v-if="isTripleScreen" class="house-block house-block--production">
        <div class="block-content">
          <div class="block-title-bar"><span>重点生产环节</span></div>
          <div class="production-panel production-panel--visible">
            <div
              class="production-card"
              :style="{ '--trail-color': getProgressColor(calcPercent(hiddenDangerCorrectionNum, hiddenDangerUnCorrectionNum)) }"
            >
              <div class="production-card__title" @click="openHouseGridInspectionHidden">网格巡查<br>隐患</div>
              <div class="production-card__content">
                <div class="production-card__row">
                  <span>未整改</span>
                  <strong>{{ hiddenDangerUnCorrectionNum }}</strong>
                </div>
                <div class="production-card__row">
                  <span>已整改</span>
                  <strong class="line-color">{{ hiddenDangerCorrectionNum }}</strong>
                </div>
                <div class="production-progress">
                  <div
                    class="production-progress__fill"
                    :style="{
                      width: calcPercent(hiddenDangerCorrectionNum, hiddenDangerUnCorrectionNum) + '%',
                      background: getProgressColor(calcPercent(hiddenDangerCorrectionNum, hiddenDangerUnCorrectionNum))
                    }"
                  ></div>
                </div>
              </div>
            </div>
            <div
              class="production-card"
              :style="{ '--trail-color': getProgressColor(calcPercent(hotWorkCorrectionNum, hotWorkUnCorrectionNum)) }"
            >
              <div class="production-card__title" @click="openHouseFireHotWorkHidden">近期动火作业<br>隐患</div>
              <div class="production-card__content">
                <div class="production-card__row">
                  <span>未完成</span>
                  <strong>{{ hotWorkUnCorrectionNum }}</strong>
                </div>
                <div class="production-card__row">
                  <span>已完成</span>
                  <strong class="line-color">{{ hotWorkCorrectionNum }}</strong>
                </div>
                <div class="production-progress">
                  <div
                    class="production-progress__fill"
                    :style="{
                      width: calcPercent(hotWorkCorrectionNum, hotWorkUnCorrectionNum) + '%',
                      background: getProgressColor(calcPercent(hotWorkCorrectionNum, hotWorkUnCorrectionNum))
                    }"
                  ></div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { NCarousel } from 'naive-ui'
import axios from 'axios'
import frameIcon from './img/Frame.png'
import placeholderImg from './img/zhanwei.png'
import BZHouseHotWorkTriple from './components/BZHouseHotWorkTriple.vue'

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

const containerStyle = computed(() => {
  const width = Number(props.chartConfig?.attr?.w)
  const height = Number(props.chartConfig?.attr?.h)
  return {
    width: Number.isFinite(width) && width > 0 ? `${width}px` : '400px',
    height: Number.isFinite(height) && height > 0 ? `${height}px` : '928px'
  }
})

const showBackground = computed(() => props.chartConfig?.option?.showBackground !== 'hide')
const isTripleScreen = computed(() => props.chartConfig?.option?.screenMode === 'triple')
// 本次交付为离线静态预览，保留原接口函数但不在组件加载时发起远程请求。
const useStaticPreview = true
const houseRootRef = ref<HTMLElement | null>(null)
const houseScrollRef = ref<HTMLElement | null>(null)

const BZHOUSE_BASE_URL = 'http://23.210.227.34:23343/ywtg'
const ZSHG_BASE_URL = 'http://23.99.16.86:11001'
const FIRE_BASE_URL = 'http://23.210.227.34:23343/ywtg/api/boot/system/fire'
const onlineManagedStatus = ref({
  managingCount: 17,
  managedCount: 17,
  minorCount: 17,
  focusCount: 17
})
const onlineFourColor = ref({
  green: { count: 242, rate: '99.99' },
  blue: { count: 242, rate: '99.99' },
  yellow: { count: 242, rate: '99.99' },
  red: { count: 242, rate: '99.99' }
})
const onlineEventStatus = ref({
  riskAwarenessCount: 11,
  riskAwarenessTotal: 11,
  houseRiskCount: 11,
  houseRiskTotal: 11,
  peopleRiskCount: 11,
  peopleRiskTotal: 11
})
const groupManagedStatus = ref({
  managingCount: 17,
  managedCount: 17,
  minorCount: 17,
  focusCount: 17
})
const groupEventStatus = ref({
  riskAwarenessCount: 11,
  riskAwarenessTotal: 11,
  houseRiskCount: 11,
  houseRiskTotal: 11,
  peopleRiskCount: 11,
  peopleRiskTotal: 11
})
const groupVideoList = ref([])
function chunkVideoSlides(list) {
  const slides = []
  for (let index = 0; index < list.length; index += 3) {
    slides.push(list.slice(index, index + 3))
  }
  return slides
}
const groupVideoSlides = computed(() => chunkVideoSlides(groupVideoList.value))
const bizVideoList = ref([])
const bizVideoSlides = computed(() => chunkVideoSlides(bizVideoList.value))
const bizManagedStatus = ref({
  managingCount: 17,
  managedCount: 17,
  minorCount: 17,
  focusCount: 17
})
const bizEventStatus = ref({
  inspectionCount: 11,
  inspectionTotal: 11,
  riskAwarenessCount: 11,
  riskAwarenessTotal: 11
})
const bizFourColor = ref({
  green: { count: 242, rate: '99.99' },
  blue: { count: 242, rate: '99.99' },
  yellow: { count: 242, rate: '99.99' },
  red: { count: 242, rate: '99.99' }
})
const hiddenDangerUnCorrectionNum = ref(0)
const hiddenDangerCorrectionNum = ref(0)
const hotWorkUnCorrectionNum = ref(0)
const hotWorkCorrectionNum = ref(0)

function calcPercent(numerator: number, denominator: number): number {
  return numerator + denominator > 0
    ? Math.min(100, Math.round((numerator / (numerator + denominator)) * 100))
    : 0
}

// ≥90% 绿色, 60-90% 橙色, <60% 红色
function getProgressColor(rate: number): string {
  if (rate >= 90) return '#12DD60'
  if (rate >= 60) return '#F68337'
  return '#F84444'
}

function handleHouseScrollWheel(event: WheelEvent) {
  const scrollEl = isTripleScreen.value ? houseScrollRef.value : houseRootRef.value
  if (!scrollEl || scrollEl.scrollHeight <= scrollEl.clientHeight) return
  const delta = event.deltaMode === 1 ? event.deltaY * 16 : event.deltaY
  scrollEl.scrollTop += delta
  event.preventDefault()
  event.stopPropagation()
}

function normalizeRate(value: unknown) {
  if (value === null || value === undefined || value === '') return '0'
  return String(value).replace('%', '')
}

function parseImageList(value: unknown) {
  if (Array.isArray(value)) return value.filter(Boolean)
  if (typeof value !== 'string' || !value.trim()) return []
  try {
    const parsed = JSON.parse(value)
    if (Array.isArray(parsed)) return parsed.filter(Boolean)
  } catch (error) {
    // evidenceImages may also be a comma-separated string.
  }
  return value.split(',').map(item => item.trim()).filter(Boolean)
}

function normalizeVideoItem(item) {
  const evidenceImages = parseImageList(item?.evidenceImages)
  return {
    id: item?.id,
    eventNo: item?.eventNo || '',
    imgUrl: item?.imgUrl || evidenceImages[0] || '',
    title: item?.title || item?.eventSubtype || item?.location || '视频事件'
  }
}

function openWarningDetail(item, houseType: string) {
  if (!item?.id) return
  eventBus.emit(OPEN_HOUSE_EVENT, {
    diaName: 'warning-detail',
    id: item.id,
    houseType
  })
}

async function fetchBizFourColor() {
  try {
    const res = await axios.get(`${BZHOUSE_BASE_URL}/api/boot/system/resiComm/bizToResiHouseFourColor`)
    const data = res.data?.data || {}
    bizFourColor.value = {
      green: { count: data.greenCodeCount || 0, rate: normalizeRate(data.greenCodeRate) },
      blue: { count: data.blueCodeCount || 0, rate: normalizeRate(data.blueCodeRate) },
      yellow: { count: data.yellowCodeCount || 0, rate: normalizeRate(data.yellowCodeRate) },
      red: { count: data.redCodeCount || 0, rate: normalizeRate(data.redCodeRate) }
    }
  } catch (error) {
    console.error('商改住四色评价请求失败:', error)
  }
}

async function fetchOnlineFourColor() {
  try {
    const res = await axios.get(`${BZHOUSE_BASE_URL}/api/boot/system/resiComm/onlineHouseFourColor`)
    const data = res.data?.data || {}
    onlineFourColor.value = {
      green: { count: data.greenCodeCount || 0, rate: normalizeRate(data.greenCodeRate) },
      blue: { count: data.blueCodeCount || 0, rate: normalizeRate(data.blueCodeRate) },
      yellow: { count: data.yellowCodeCount || 0, rate: normalizeRate(data.yellowCodeRate) },
      red: { count: data.redCodeCount || 0, rate: normalizeRate(data.redCodeRate) }
    }
  } catch (error) {
    console.error('网约房四色评价请求失败:', error)
  }
}

async function fetchOnlineManagedStatus() {
  try {
    const res = await axios.get(`${BZHOUSE_BASE_URL}/api/boot/system/resiComm/onlineHouseManagedStatus`)
    const data = res.data?.data || {}
    onlineManagedStatus.value = {
      managingCount: data.managingCount || 0,
      managedCount: data.managedCount || 0,
      minorCount: data.minorCount || 0,
      focusCount: data.focusCount || 0
    }
  } catch (error) {
    console.error('网约房纳管情况请求失败:', error)
  }
}

async function fetchOnlineEventStatus() {
  try {
    const res = await axios.post(`${BZHOUSE_BASE_URL}/api/boot/system/resiComm/onlineHouseEventStatus`, {
      startTime: '',
      endTime: ''
    })
    const data = res.data?.data || {}
    onlineEventStatus.value = {
      riskAwarenessCount: data.riskAwarenessCount || 0,
      riskAwarenessTotal: data.riskAwarenessTotal || 0,
      houseRiskCount: data.houseRiskCount || 0,
      houseRiskTotal: data.houseRiskTotal || 0,
      peopleRiskCount: data.peopleRiskCount || 0,
      peopleRiskTotal: data.peopleRiskTotal || 0
    }
  } catch (error) {
    console.error('网约房重点事件请求失败:', error)
  }
}

async function fetchGroupEventStatus() {
  try {
    const res = await axios.post(`${BZHOUSE_BASE_URL}/api/boot/system/resiComm/groupHouseEventStatus`, {
      startTime: '',
      endTime: ''
    })
    const data = res.data?.data || {}
    groupEventStatus.value = {
      riskAwarenessCount: data.riskAwarenessCount || 0,
      riskAwarenessTotal: data.riskAwarenessTotal || 0,
      houseRiskCount: data.houseRiskCount || 0,
      houseRiskTotal: data.houseRiskTotal || 0,
      peopleRiskCount: data.peopleRiskCount || 0,
      peopleRiskTotal: data.peopleRiskTotal || 0
    }
  } catch (error) {
    console.error('群租空间重点事件请求失败:', error)
  }
}

async function fetchGroupVideos() {
  try {
    const res = await axios.get(`${ZSHG_BASE_URL}/api/v1/system/smartRisk/qzkjVideo`)
    const list = res.data?.data || []
    groupVideoList.value = Array.isArray(list) ? list.map(normalizeVideoItem) : []
  } catch (error) {
    console.error('群租空间视频事件请求失败:', error)
    groupVideoList.value = []
  }
}

async function fetchBizVideos() {
  try {
    const res = await axios.get(`${ZSHG_BASE_URL}/api/v1/system/smartRisk/sgzVideo`)
    const list = res.data?.data || []
    bizVideoList.value = Array.isArray(list) ? list.map(normalizeVideoItem) : []
  } catch (error) {
    console.error('商改住视频事件请求失败:', error)
    bizVideoList.value = []
  }
}

async function fetchBizManagedStatus() {
  try {
    const res = await axios.get(`${BZHOUSE_BASE_URL}/api/boot/system/resiComm/bizToResiHouseManagedStatus`)
    const data = res.data?.data || {}
    bizManagedStatus.value = {
      managingCount: data.managingCount || 0,
      managedCount: data.managedCount || 0,
      minorCount: data.minorCount || 0,
      focusCount: data.focusCount || 0
    }
  } catch (error) {
    console.error('商改住纳管情况请求失败:', error)
  }
}

async function fetchBizEventStatus() {
  try {
    const res = await axios.post(`${BZHOUSE_BASE_URL}/api/boot/system/resiComm/bizToResiHouseEventStatus`, {
      startTime: '',
      endTime: ''
    })
    const data = res.data?.data || {}
    bizEventStatus.value = {
      inspectionCount: data.inspectionCount || 0,
      inspectionTotal: data.inspectionTotal || 0,
      riskAwarenessCount: data.riskAwarenessCount || 0,
      riskAwarenessTotal: data.riskAwarenessTotal || 0
    }
  } catch (error) {
    console.error('商改住重点事件请求失败:', error)
  }
}

async function fetchGroupManagedStatus() {
  try {
    const res = await axios.get(`${BZHOUSE_BASE_URL}/api/boot/system/resiComm/groupHouseManagedStatus`)
    const data = res.data?.data || {}
    groupManagedStatus.value = {
      managingCount: data.managingCount || 0,
      managedCount: data.managedCount || 0,
      minorCount: data.minorCount || 0,
      focusCount: data.focusCount || 0
    }
  } catch (error) {
    console.error('群租空间纳管情况请求失败:', error)
  }
}

async function fetchFireProductionStats() {
  try {
    const res = await axios.get(`${FIRE_BASE_URL}/safeStatistics`)
    const data = res.data?.data || {}
    hiddenDangerUnCorrectionNum.value = data.hiddenDangerUnCorrectionNum || 0
    hiddenDangerCorrectionNum.value = data.hiddenDangerCorrectionNum || 0
    hotWorkUnCorrectionNum.value = data.hotWorkUnCorrectionNum || 0
    hotWorkCorrectionNum.value = data.hotWorkCorrectionNum || 0
  } catch (error) {
    console.error('重点生产环节统计请求失败:', error)
  }
}

const sourceName = 'BZHouse'
const OPEN_HOUSE_EVENT = 'OPEN_HOUSE_DIA'
const HOUSE_DISPATCH_URL_MAP = {
  online: 'http://23.99.208.134:8848/cq/supervision_platform_web#/dataScreen?uuid=e3e70682-c209-4cac-629f-6f82dc3540d6',
  group: 'http://23.99.16.77:9091/ykb-web/cockpit',
  business: 'http://23.99.16.161:9091/c2r-web/cockpit'
}

const eventBus = {
  emit(event, data) {
    props.bus?.emit(event, { data, source: sourceName })
  }
}

function closeDialog() {
  eventBus.emit('CLOSE_DIA', {})
}

function openHouseDispatch(type: keyof typeof HOUSE_DISPATCH_URL_MAP) {
  const url = HOUSE_DISPATCH_URL_MAP[type]
  if (!url) return
  closeDialog()
  eventBus.emit('OPEN_IFRAME_DIA', { url })
}

function openHouseColorCode(houseType: string) {
  eventBus.emit(OPEN_HOUSE_EVENT, {
    diaName: 'house-color-code',
    houseType
  })
}

function openHouseList(houseType: string) {
  eventBus.emit(OPEN_HOUSE_EVENT, {
    diaName: 'house-list',
    houseType
  })
}

function openHousePersonList(houseType: string) {
  eventBus.emit(OPEN_HOUSE_EVENT, {
    diaName: 'house-person-list',
    houseType
  })
}

function openHouseRiskPerception(houseType: string) {
  eventBus.emit(OPEN_HOUSE_EVENT, {
    diaName: 'house-risk-perception',
    houseType
  })
}

function openHouseRiskWarning() {
  eventBus.emit(OPEN_HOUSE_EVENT, {
    diaName: 'house-risk-warning'
  })
}

function openHouseInspectionDetail() {
  eventBus.emit(OPEN_HOUSE_EVENT, {
    diaName: 'house-inspection'
  })
}

function openHouseGridInspectionHidden() {
  eventBus.emit(OPEN_HOUSE_EVENT, {
    diaName: 'house-grid-inspection-hidden'
  })
}

function openHouseFireHotWorkHidden() {
  eventBus.emit(OPEN_HOUSE_EVENT, {
    diaName: 'house-fire-work-hidden'
  })
}

onMounted(() => {
  if (useStaticPreview || isTripleScreen.value) return
  fetchOnlineManagedStatus()
  fetchOnlineFourColor()
  fetchOnlineEventStatus()
  fetchGroupManagedStatus()
  fetchGroupEventStatus()
  fetchGroupVideos()
  fetchBizManagedStatus()
  fetchBizEventStatus()
  fetchBizFourColor()
  fetchBizVideos()
  fetchFireProductionStats()
})
</script>

<script lang="ts">
export default {
  name: 'BZHouse',
  version: '1.0.0'
}
</script>

<style lang="scss" scoped>
@font-face {
  font-family: 'BzHousePuHuiTi';
  src: url('./font/Alibaba_PuHuiTi_2.0_55_Regular_55_Regular.ttf') format('truetype');
  font-weight: 400;
  font-style: normal;
  font-display: swap;
}

@font-face {
  font-family: 'BzHousePuHuiTi';
  src: url('./font/Alibaba_PuHuiTi_2.0_75_SemiBold_75_SemiBold.ttf') format('truetype');
  font-weight: 700;
  font-style: normal;
  font-display: swap;
}

@font-face {
  font-family: 'BzHouseDIN';
  src: url('./font/D-DIN-Bold.otf') format('opentype');
  font-weight: 700;
  font-style: normal;
  font-display: swap;
}

@font-face {
  font-family: 'BzHouseTitle';
  src: url('./font/Alibaba_PuHuiTi_2.0_105_Heavy_105_Heavy.ttf') format('truetype');
  font-weight: 900;
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

.bz-house {
  box-sizing: border-box;
  overflow-y: auto;
  overflow-x: hidden;
  overscroll-behavior: contain;
  pointer-events: auto;
  touch-action: pan-y;
  scrollbar-width: none;
  -ms-overflow-style: none;
  color: #d8efff;
  background: #031a36;
  font-family: 'BzHousePuHuiTi', Microsoft YaHei, Arial, sans-serif;
}

.bz-house::-webkit-scrollbar {
  display: none;
}

.bz-house--triple {
  overflow: hidden;
}

.block-title-bar {
  width: 100%;
  height: 40px;
  box-sizing: border-box;
  display: flex;
  align-items: center;
  justify-content: center;
  background: url('./img/bg_title.png') center / 100% 100% no-repeat;
  font-family: 'BzHouseTitle', Microsoft YaHei, Arial, sans-serif;
  font-size: 20px;
  font-weight: 900;
  line-height: 32px;
  letter-spacing: 1.6px;
  margin-bottom: 12px !important;
}

.block-title-bar--module {
  height: 32px;
  justify-content: flex-start;
  padding: 0 8px 0 42px;
  margin-bottom: 6px;
  cursor: pointer;
  background: url('./img/bg_second_title.png') center / 100% 100% no-repeat;
  font-family: 'UISDC', 'BzHousePuHuiTi', Microsoft YaHei, Arial, sans-serif;
  font-size: 22px;
  letter-spacing: 1px;
}

.block-title-bar span {
  position: relative;
  top:-4px;
  background: linear-gradient(180deg, #FFFFFF 0%,#FFFFFF 40%, #31BEFF 100%);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  color: transparent;
}

.bz-house--triple .block-title-bar--module span {
  position: relative;
  top:3px;
  background: none;
  -webkit-background-clip: initial;
  background-clip: initial;
  -webkit-text-fill-color: #fff;
  color: #fff;
  text-shadow: 0 0 8px rgba(96, 165, 250, 0.95);
}

.bz-house--no-bg {
  background: transparent;
}

.house-inner {
  width: 100%;
  height: 100%;
  min-height: 0;
  padding: 0 0px 10px 0;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  gap: 5px;
  background:
    linear-gradient(90deg, rgba(0, 100, 207, 0.22), rgba(0, 14, 42, 0) 34px),
    linear-gradient(180deg, #03234a 0%, #041d3c 54%, #031936 100%);
  position: relative;
}

.bz-house--no-bg .house-inner {
  background: transparent;
}

.bz-house--no-bg .house-inner::after {
  display: none;
}

.house-inner::after {
  content: '';
  position: absolute;
  right: 6px;
  bottom: 4px;
  width: 92%;
  height: 1px;
  background: linear-gradient(90deg, rgba(13, 91, 174, 0), #0c7bea, rgba(13, 91, 174, 0));
}

.house-scroll {
  display: contents;
}

.house-block {
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
}

.house-block--managed,
.house-block--group {
  flex: 0 0 auto;
}

.house-block--business {
  flex: 0 0 auto;
}

.block-label {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 2px;
  color: #d7f1ff;
  font-size: 13px;
  font-weight: 700;
  line-height: 1.05;
  background: linear-gradient(180deg, #06539c, #06417f);
  border-right: 1px solid rgba(56, 166, 255, 0.65);
}

.block-label--clickable {
  cursor: pointer;
}

.block-label--clickable:hover {
  filter: brightness(1.15);
}

.block-label--small {
  width: 20px;
  flex: 0 0 20px;
}

.block-content {
  min-width: 0;
  height: 100%;
  padding: 3px 0 0;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  gap: 0;
}

.dual-title,
.stats-row,
.block-content > .title-item,
.risk-row,
.color-panel {
  margin-bottom: 6px;
}

.dual-title {
  height: 16px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  padding-right: 8px;
}

.title-item {
  font-family: 'BzHousePuHuiTi';
  height: 16px;
  line-height: 16px;
  padding-left: 21px;
  box-sizing: border-box;
  position: relative;
  font-size: 16px;
  color: #d9f7ff;
}

.title-item::before {
  content: '';
  position: absolute;
  left: 12px;
  top: 5px;
  width: 0;
  height: 0;
  border-top: 4px solid transparent;
  border-bottom: 4px solid transparent;
  border-left: 6px solid #9fe6ff;
  filter: drop-shadow(0 0 4px #28b8ff);
}

.title-item--clickable {
  cursor: pointer;
}

.title-item--clickable:hover {
  color: #5ff6ff;
  text-shadow: 0 0 8px rgba(51, 236, 255, 0.7);
}

.stats-row {
  height: 23px;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 8px;
  padding: 0 8px;
}

.data-badge,
.risk-badge,
.color-badge {
  min-width: 0;
  box-sizing: border-box;
  display: flex;
  align-items: center;
  justify-content: space-between;
  overflow: hidden;
  background-size: 100% 100%;
}

.data-badge {
  height: 23px;
  padding: 0 4px 0 7px;
  background-image: url('./img/bg_list_data.png');
  background-repeat: no-repeat;
}

.data-badge--green {
  background-image: url('./img/bg_list_data_1.png');
  background-repeat: no-repeat;
}

.data-badge--orange {
  background-image: url('./img/bg_list_data_3.png');
  background-repeat: no-repeat;
}

.badge-label {
  min-width: 0;
  color: #e2f4ff;
  font-size: 16px;
  white-space: nowrap;
}

.data-badge strong,
.risk-badge strong,
.color-badge strong {
  flex: 0 0 auto;
  font-family: 'BzHouseDIN', Arial, sans-serif;
  font-size: 14px;
  color: #5ff6ff;
  line-height: 1;
  text-shadow: 0 0 8px rgba(51, 236, 255, 0.85);
}

.data-badge--orange strong {
  color: #ffb357;
}
.data-badge--green strong {
  color: #12DD60;
}

.data-badge--clickable {
  cursor: pointer;
}

.data-badge--clickable:hover {
  filter: brightness(1.14);
}

.risk-row {
  height: 28px;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 6px;
  padding: 0 8px;
}

.house-block--business .risk-row {
  grid-template-columns: 1fr 1fr;
}

.risk-badge {
  height: 28px;
  padding: 0 6px;
  background-image: url('./img/bgList.png');
  background-repeat: no-repeat;
}

.risk-badge--clickable {
  cursor: pointer;
}

.risk-badge--clickable:hover {
  filter: brightness(1.14);
}

.risk-badge span {
  min-width: 0;
  color: #d7f0ff;
  font-size: 16px;
  white-space: nowrap;
}

.risk-badge strong,
.color-badge strong {
  font-size: 13px;
}

.risk-badge strong em,
.risk-badge strong i,
.risk-badge strong b {
  font: inherit;
  font-style: normal;
}

.risk-badge strong em {
  color: #ffbc00;
}

.risk-badge strong i {
  color: #8bbce6;
}

.risk-badge strong b {
  color: #5ff6ff;
}

.color-grid {
  min-width: 0;
  height: 100%;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 4px;
}

.color-panel {
  padding:4px;
  margin:0 8px;
  display: grid;
  grid-template-columns: 130px 1fr;
  border: 1px solid rgba(26, 118, 216, 0.88);
  background: linear-gradient(90deg, rgba(4, 61, 125, 0.95), rgba(6, 49, 105, 0.66));
}

.color-panel--clickable {
  cursor: pointer;
}

.color-panel--clickable:hover {
  filter: brightness(1.12);
}

.color-panel__title {
  display: flex;
  align-items: center;
  padding-left: 36px;
  box-sizing: border-box;
  position: relative;
  color: #d8f3ff;
  font-size: 20px;
  font-weight: 700;
}

.color-panel__title::before {
  content: '';
  position: absolute;
  left: 18px;
  top: 50%;
  transform: translateY(-50%);
  width: 0;
  height: 0;
  border-top: 6px solid transparent;
  border-bottom: 6px solid transparent;
  border-left: 9px solid #9fe6ff;
  filter: drop-shadow(0 0 5px #28b8ff);
}

.color-badge {
  height: 31px;
  padding: 0 8px 0 12px;
  border-width: 0 0 1px 1px;
}

.color-badge:nth-child(3),
.color-badge:nth-child(4) {
  border-bottom: 0;
}

.color-label {
  min-width: 54px;
  color: #e9f8ff;
  font-size: 16px;
  font-weight: 700;
  position: relative;
  text-shadow: 0 0 8px rgba(112, 194, 255, 0.62);
}

.color-badge--yellow strong {
  color: #ffd94b;
  text-shadow: 0 0 7px rgba(255, 195, 47, 0.78);
}

.color-badge--red strong {
  color: #ff6679;
  text-shadow: 0 0 7px rgba(255, 65, 87, 0.78);
}

.color-badge strong span {
  font: inherit;
}

.color-badge .color-separator {
  margin: 0 5px;
  color: #8bbce6;
}

.color-badge .color-rate {
  color: #5ff6ff;
}

.color-badge .color-unit {
  margin-left: 2px;
  color: #8bbce6;
}

.photo-row {
  flex: 1;
  min-height: 72px;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 6px;
}

.photo-carousel {
  flex: 1;
  min-height: 72px;
  overflow: hidden;
  margin:0 8px;

  :deep(.n-carousel__slides),
  :deep(.n-carousel__slide) {
    height: 100%;
  }
}

.photo-row--slide {
  height: 100%;
  min-height: 72px;
}

.photo-row--empty {
  grid-template-columns: 1fr;
}

.photo-empty {
  min-height: 72px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(29, 129, 226, 0.8);
  background: linear-gradient(180deg, rgba(8, 50, 98, 0.92), rgba(5, 35, 77, 0.94));
  color: rgba(216, 239, 255, 0.72);
  font-size: 14px;
  font-weight: 700;
  margin: 4px 8px;
}

.photo-card {
  min-width: 0;
  height: 100%;
  min-height: 47px;
  border: 1px solid rgba(29, 129, 226, 0.8);
  background: rgba(5, 41, 83, 0.88);
  overflow: hidden;
}

.photo-card--clickable {
  cursor: pointer;
}

.photo-card--clickable:hover {
  filter: brightness(1.12);
}

.photo-card__image {
  display: block;
  width: 100%;
  height: 72px;
  min-height: 30px;
  object-fit: cover;
}

.photo-card__title {
  height: 17px;
  line-height: 17px;
  padding: 0 8px;
  color: #d9f6ff;
  font-size: 10px;
  text-align: center;
  background: rgba(0, 55, 112, 0.92);
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.access-row {
  height: 61px;
  display: flex;
  gap: 4px;
  margin: 0 8px;
}

.access-card {
  flex: 1;
  min-width: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  border: 1px solid rgba(31, 117, 221, 0.46);
  background: linear-gradient(180deg, rgba(8, 50, 98, 0.92), rgba(5, 35, 77, 0.94));
}

.access-card img {
  width: 45px;
  height: 45px;
  opacity: 0.68;
}

.access-card span {
  color: rgba(118, 174, 219, 0.7);
  font-size: 16px;
  font-weight: 700;
}

.bz-house--triple .house-inner {
  gap: 8px;
  overflow: hidden;
}

.bz-house--triple .house-scroll {
  flex: 1 1 auto;
  height: 0;
  min-height: 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
  overflow-y: auto;
  overflow-x: hidden;
  overscroll-behavior: contain;
  pointer-events: auto;
  touch-action: pan-y;
  padding-right: 2px;
  scrollbar-width: thin;
  scrollbar-color: rgba(61, 190, 255, 0.72) rgba(5, 42, 84, 0.62);
}

.bz-house--triple .house-scroll::-webkit-scrollbar {
  width: 4px;
}

.bz-house--triple .house-scroll::-webkit-scrollbar-track {
  background: rgba(5, 42, 84, 0.62);
}

.bz-house--triple .house-scroll::-webkit-scrollbar-thumb {
  border-radius: 4px;
  background: linear-gradient(180deg, rgba(49, 190, 255, 0.9), rgba(27, 116, 217, 0.72));
}

.bz-house--triple .house-block--production {
  flex: 0 0 auto;
}

.bz-house--triple .house-block--business .block-title-bar {
  cursor: default;
}

.bz-house--triple .block-content {
  padding-top: 0;
}

.production-panel {
  display: none;
  flex-direction: column;
  gap: 8px;
}

.production-panel--visible {
  display: flex;
}

.production-card {
  --trail-color: #12dd60;
  display: grid;
  grid-template-columns: 76px 1fr;
  gap: 8px;
  min-height: 76px;
  padding: 8px;
  border: 1px solid rgba(19, 119, 207, 0.82);
  margin:0 8px;
}

.production-card__title {
  height: 72px;
  box-sizing: border-box;
  padding: 13px 10px 0;
  color: #d9f7ff;
  font-size: 15px;
  line-height: 18px;
  font-weight: 700;
  text-align: center;
  cursor: pointer;
  background: url('./img/bg-card.png') no-repeat center center;
  background-size: 100% 100%;
}

.production-card__title:hover {
  filter: brightness(1.15);
}

.production-card__content {
  min-width: 0;
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-template-rows: 32px 12px;
  gap: 18px 8px;
  padding-top: 8px;
}

.production-card__row {
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 12px;
  box-sizing: border-box;
  background: url('./img/fire_process_stat.png') center / 100% 100% no-repeat;
}

.production-card__row span {
  min-width: 0;
  color: #d7efff;
  font-size: 15px;
  font-weight: 700;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.production-card__row strong {
  color: #ffd037;
  font-size: 20px;
  font-family: 'BzHouseDIN';
  text-shadow: 0 0 8px rgba(251, 191, 36, 0.72);
}

.production-progress {
  position: relative;
  grid-column: 1 / -1;
  align-self: center;
  width: 100%;
  height: 16px;
  padding: 1px;
  box-sizing: border-box;
  overflow: hidden;
  border: 1px solid rgba(19, 119, 207, 0.82);
  border-radius: 8px;
  background: rgba(8, 71, 126, 0.72);
}

.production-progress__fill {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  height: 10px;
  border-radius: 8px;
  background: var(--trail-color);
  box-shadow: 0 0 8px rgba(22, 214, 220, 0.72);
  transition: width 0.3s;
}

.line-color {
  color: #5feaff !important;
}
.color-badge--green{
  background-image: url('./img/bg_list_data_4.png');
  background-repeat: no-repeat;
}
.color-badge--yellow{
  background-image: url('./img/bg_list_data_5.png');
  background-repeat: no-repeat;
}
.color-badge--red{
  background-image: url('./img/bg_list_data_7.png');
  background-repeat: no-repeat;
}
.color-badge--blue{
  background-image: url('./img/bg_list_data_6.png');
  background-repeat: no-repeat;
}
</style>
