<template>
  <div class="bz-bridge-tunnel" :class="{ 'bz-bridge-tunnel--no-bg': !showBackground }" :style="containerStyle">
    <div class="dispatch-row">
      <button class="dispatch-btn" type="button" @click="openDispatchIframe">调度</button>
    </div>
    <section class="section-row section-row--dispose">
      <aside class="side-title">
        <span>综</span><span>合</span><span>评</span><span>估</span>
      </aside>
      <div class="section-content">
        <div class="top-grid top-grid--three">
          <div class="group">
            <div class="group-title">昨日</div>
            <div class="stat-row">
              <div class="data-badge data-badge--orange"><span>异常</span><strong>{{ assessmentYesterday.errorCount }}</strong></div>
              <div class="data-badge data-badge--clickable" @click="openReportList(1)"><span>总数</span><strong>{{ assessmentYesterday.total }}</strong></div>
            </div>
            <div class="time-card" :class="{ 'card-warn': assessmentYesterday.errorMsg.length, 'time-card--center': !assessmentYesterday.errorMsg.length }">
              <div v-if="assessmentYesterday.errorMsg.length" class="time-card__scroll">
                <n-tooltip
                  v-for="(msg, index) in assessmentYesterday.errorMsg"
                  :key="`ass-yesterday-${index}`"
                  trigger="hover"
                  placement="top"
                >
                  <template #trigger>
                    <div class="time-card__item time-card__item--clickable">{{ msg }}</div>
                  </template>
                  <span class="time-card__tooltip">{{ msg }}</span>
                </n-tooltip>
              </div>
              <template v-else>本周监测数据未见异常</template>
            </div>
          </div>
          <div class="group">
            <div class="group-title">上周</div>
            <div class="stat-row">
              <div class="data-badge data-badge--orange"><span>异常</span><strong>{{ assessmentLastWeek.errorCount }}</strong></div>
              <div class="data-badge data-badge--clickable" @click="openReportList(2)"><span>总数</span><strong>{{ assessmentLastWeek.total }}</strong></div>
            </div>
            <div class="time-card" :class="{ 'card-warn': assessmentLastWeek.errorMsg.length, 'time-card--center': !assessmentLastWeek.errorMsg.length }">
              <div v-if="assessmentLastWeek.errorMsg.length" class="time-card__scroll">
                <n-tooltip
                  v-for="(msg, index) in assessmentLastWeek.errorMsg"
                  :key="`ass-lastweek-${index}`"
                  trigger="hover"
                  placement="top"
                >
                  <template #trigger>
                    <div class="time-card__item time-card__item--clickable">{{ msg }}</div>
                  </template>
                  <span class="time-card__tooltip">{{ msg }}</span>
                </n-tooltip>
              </div>
              <template v-else>本周监测数据未见异常</template>
            </div>
          </div>
          <div class="group">
            <div class="group-title">上月</div>
            <div class="stat-row">
              <div class="data-badge data-badge--orange"><span>异常</span><strong>{{ assessmentLastMonth.errorCount }}</strong></div>
              <div class="data-badge data-badge--clickable" @click="openReportList(3)"><span>总数</span><strong>{{ assessmentLastMonth.total }}</strong></div>
            </div>
            <div class="time-card" :class="{ 'card-warn': assessmentLastMonth.errorMsg.length, 'time-card--center': !assessmentLastMonth.errorMsg.length }">
              <div v-if="assessmentLastMonth.errorMsg.length" class="time-card__scroll">
                <n-tooltip
                  v-for="(msg, index) in assessmentLastMonth.errorMsg"
                  :key="`ass-lastmonth-${index}`"
                  trigger="hover"
                  placement="top"
                >
                  <template #trigger>
                    <div class="time-card__item time-card__item--clickable">{{ msg }}</div>
                  </template>
                  <span class="time-card__tooltip">{{ msg }}</span>
                </n-tooltip>
              </div>
              <template v-else>本周监测数据未见异常</template>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="section-row section-row--monitor">
      <aside class="side-title">
        <span>在</span><span>线</span><span>监</span><span>测</span>
      </aside>
      <div class="section-content">
        <div class="top-grid">
          <div class="group group--clickable" @click="openBridgeTunnelList(1)">
            <div class="group-title">桥梁监测</div>
            <div class="focus-kpi focus-kpi--clickable" @click.stop="openBridgeTunnelList(1, 1)">重点关注：{{ bridgeFocus }}</div>
            <div class="stat-row stat-row--clickable" @click.stop="handleMonitorStatClick(1)">
              <div class="data-badge data-badge--orange"><span>异常</span><strong>{{ bridgeMonitor.errorCount }}</strong></div>
              <div class="data-badge"><span>总数</span><strong>{{ bridgeMonitor.total }}</strong></div>
            </div>
            <div class="time-card" :class="{ 'card-warn': bridgeMonitor.errorMsg.length, 'time-card--center': !bridgeMonitor.errorMsg.length }">
              <div v-if="bridgeMonitor.errorMsg.length" class="time-card__scroll">
                <n-tooltip
                  v-for="(msg, index) in bridgeMonitor.errorMsg"
                  :key="`bridge-${index}`"
                  trigger="hover"
                  placement="top"
                >
                  <template #trigger>
                    <div class="time-card__item time-card__item--clickable">{{ msg }}</div>
                  </template>
                  <span class="time-card__tooltip">{{ msg }}</span>
                </n-tooltip>
              </div>
              <template v-else>无异常</template>
            </div>
          </div>
          <div class="group group--clickable" @click="openBridgeTunnelList(4)">
            <div class="group-title">隧道监测</div>
            <div class="focus-kpi focus-kpi--clickable" @click.stop="openBridgeTunnelList(4, 1)">重点关注：{{ tunnelFocus }}</div>
            <div class="stat-row stat-row--clickable" @click.stop="handleMonitorStatClick(4)">
              <div class="data-badge data-badge--orange"><span>异常</span><strong>{{ tunnelMonitor.errorCount }}</strong></div>
              <div class="data-badge"><span>总数</span><strong>{{ tunnelMonitor.total }}</strong></div>
            </div>
            <div class="time-card" :class="{ 'card-warn': tunnelMonitor.errorMsg.length, 'time-card--center': !tunnelMonitor.errorMsg.length }">
              <div v-if="tunnelMonitor.errorMsg.length" class="time-card__scroll">
                <n-tooltip
                  v-for="(msg, index) in tunnelMonitor.errorMsg"
                  :key="`tunnel-${index}`"
                  trigger="hover"
                  placement="top"
                >
                  <template #trigger>
                    <div class="time-card__item time-card__item--clickable">{{ msg }}</div>
                  </template>
                  <span class="time-card__tooltip">{{ msg }}</span>
                </n-tooltip>
              </div>
              <template v-else>无异常</template>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="section-row section-row--patrol">
      <aside class="side-title">
        <span>人</span><span>工</span><span>巡</span><span>查</span>
      </aside>
      <div class="section-content">
        <div class="patrol-grid">
          <div class="patrol-col">
            <div class="group-title">日常巡查</div>
            <div class="stat-row stat-row--wide">
              <div class="data-badge data-badge--orange"><span>未巡查</span><strong>0</strong></div>
              <div class="data-badge"><span>总数</span><strong>0</strong></div>
            </div>
          </div>
          <div class="patrol-col">
            <div class="group-title">专业巡查</div>
            <div class="stat-row stat-row--wide">
              <div class="data-badge data-badge--orange"><span>未处置</span><strong>0</strong></div>
              <div class="data-badge"><span>总数</span><strong>0</strong></div>
            </div>
          </div>
        </div>
        <div class="event-card" :class="{ 'event-card--empty': !patrolEvent }">
          <template v-if="patrolEvent">
            <img class="event-image" :src="patrolEvent.image" alt="" />
            <div class="event-info">
              <div class="event-title">
                <span>{{ patrolEvent.title }}</span>
                <em>{{ patrolEvent.location }}</em>
              </div>
              <div class="event-text">{{ patrolEvent.description }}</div>
              <div class="event-time">{{ patrolEvent.time }}</div>
            </div>
            <span class="status-tag">{{ patrolEvent.status }}</span>
          </template>
          <div v-else class="event-empty">
            <div class="event-empty__icon"></div>
            <div class="event-empty__text">暂无事件</div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import axios from 'axios'
import { NTooltip } from 'naive-ui'

const BASE_URL = 'http://23.99.16.179:11001/api/boot/system/bridge'

type MonitorData = {
  total: number
  errorCount: number
  errorMsg: string[]
}

type PatrolEvent = {
  image: string
  title: string
  location: string
  description: string
  time: string
  status: string
}

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

const emptyMonitor = (): MonitorData => ({
  total: 0,
  errorCount: 0,
  errorMsg: []
})

const bridgeMonitor = ref<MonitorData>({ total: 0, errorCount: 0, errorMsg: [] })
const tunnelMonitor = ref<MonitorData>({ total: 0, errorCount: 0, errorMsg: [] })
const bridgeFocus = ref(51)
const tunnelFocus = ref(13)
const assessmentYesterday = ref<MonitorData>({ total: 0, errorCount: 0, errorMsg: [] })
const assessmentLastWeek = ref<MonitorData>({ total: 0, errorCount: 0, errorMsg: [] })
const assessmentLastMonth = ref<MonitorData>({ total: 0, errorCount: 0, errorMsg: [] })
const patrolEvent = ref<PatrolEvent | null>(null)

const containerStyle = computed(() => {
  const width = Number(props.chartConfig?.attr?.w)
  const height = Number(props.chartConfig?.attr?.h)
  return {
    width: Number.isFinite(width) && width > 0 ? `${width}px` : '540px',
    height: Number.isFinite(height) && height > 0 ? `${height}px` : '428px'
  }
})

const showBackground = computed(() => props.chartConfig?.option?.showBackground !== 'hide')

const sourceName = 'BZBridgeTunnel'
const BRIDGE_TUNNEL_LAYER_TOGGLE_EVENT = 'BZ_BRIDGE_TUNNEL_LAYER_TOGGLE'
const eventBus = {
  emit(event: string, data: any) {
    props.bus?.emit(event, { data, source: sourceName })
  }
}

function normalizeMonitor(item: any): MonitorData {
  return {
    total: Number(item?.total) || 0,
    errorCount: Number(item?.errorCount) || 0,
    errorMsg: Array.isArray(item?.errorMsg) ? item.errorMsg.filter(Boolean).map(String) : []
  }
}

function normalizeReportEvaluation(item: any): MonitorData {
  return {
    total: Number(item?.totalCount) || 0,
    errorCount: Number(item?.abnormalCount) || 0,
    errorMsg: []
  }
}

async function fetchOnlineMonitor() {
  try {
    const res = await axios.get(`${BASE_URL}/online/monitor`)
    const responseData = res?.data?.data || res?.data || {}
    bridgeMonitor.value = normalizeMonitor(responseData.bridge)
    tunnelMonitor.value = normalizeMonitor(responseData.tunnel)
  } catch (err) {
    console.error('获取桥隧在线监测数据失败', err)
  }
}

async function fetchAssessment() {
  try {
    const [daily, weekly, monthly] = await Promise.all([
      axios.get(`${BASE_URL}/report/evaluation`, { params: { type: 1 } }),
      axios.get(`${BASE_URL}/report/evaluation`, { params: { type: 2 } }),
      axios.get(`${BASE_URL}/report/evaluation`, { params: { type: 3 } })
    ])
    assessmentYesterday.value = normalizeReportEvaluation(daily?.data?.data || daily?.data || {})
    assessmentLastWeek.value = normalizeReportEvaluation(weekly?.data?.data || weekly?.data || {})
    assessmentLastMonth.value = normalizeReportEvaluation(monthly?.data?.data || monthly?.data || {})
  } catch (err) {
    console.error('获取综合评估数据失败', err)
  }
}

function openBridgeTunnelList(structType: number, importantFlag?: number) {
  eventBus.emit('OPEN_DIA', {
    diaName: 'bridge-tunnel-list',
    structType,
    importantFlag
  })
}

function openReportList(reportType: number) {
  eventBus.emit('OPEN_DIA', {
    diaName: 'report-list',
    reportType
  })
}

function handleMonitorStatClick(structType: number) {
  openBridgeTunnelList(structType)
  eventBus.emit(BRIDGE_TUNNEL_LAYER_TOGGLE_EVENT, { structType })
}

function getStructNameFromMonitorMessage(message: string) {
  return String(message || '')
    .replace(/^\d{4}-\d{2}-\d{2}\s+\d{2}:\d{2}:\d{2}/, '')
    .replace(/设备.*$/, '')
    .replace(/告警.*$/, '')
    .trim()
}

function openMonitorErrorDetail(structType: number, message: string) {
  eventBus.emit('OPEN_DIA', {
    diaName: 'bridge-info-detail',
    structType,
    structName: getStructNameFromMonitorMessage(message),
    message
  })
}

async function openDispatchIframe() {
  try {
    const res = await axios.get(`${BASE_URL}/token`)
    const url = res?.data?.data
    if (!url) return
    eventBus.emit('OPEN_IFRAME_DIA', { url })
  } catch (err) {
    console.error('获取桥隧调度地址失败', err)
  }
}

onMounted(() => {
  fetchOnlineMonitor()
  fetchAssessment()
})
</script>

<script lang="ts">
export default {
  name: 'BZBridgeTunnel',
  version: '1.0.0'
}
</script>

<style lang="scss" scoped>
@font-face {
  font-family: 'BZBridgePuHuiTi';
  src: url('./font/Alibaba_PuHuiTi_2.0_55_Regular_55_Regular.ttf') format('truetype');
  font-weight: 400;
  font-style: normal;
  font-display: swap;
}

@font-face {
  font-family: 'BZBridgeDIN';
  src: url('./font/D-DIN-Bold.otf') format('opentype');
  font-weight: 700;
  font-style: normal;
  font-display: swap;
}

.bz-bridge-tunnel {
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  color: #d9f7ff;
  background: #041f3c;
  font-family: 'BZBridgePuHuiTi', Microsoft YaHei, Arial, sans-serif;
}

.bz-bridge-tunnel--no-bg {
  background: transparent;
}

.dispatch-row {
  flex: 0 0 24px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-bottom: 2px;
  padding-right: 2px;
  box-sizing: border-box;
}

.dispatch-btn {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 40px;
  height: 24px;
  padding: 0;
  border: none;
  background: url('./img/bg_button_deal.png') center / 100% 100% no-repeat;
  color: #d9f7ff;
  font-size: 13px;
  font-weight: 700;
  cursor: pointer;
}

.dispatch-btn:hover {
  filter: brightness(1.12);
}

.section-row {
  min-height: 0;
  display: grid;
  grid-template-columns: 24px 1fr;
  gap: 7px;
}

.section-row--monitor {
  height: 105px;
  margin-bottom: 8px;
}

.section-row--patrol {
  height: 180px;
  margin-bottom: 8px;
}

.section-row--patrol .section-content {
  display: grid;
  grid-template-rows: 54px minmax(0, 1fr);
  gap: 8px;
}

.section-row--dispose {
  height: 105px;
  margin-bottom: 8px;
}

.side-title {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 2px;
  color: #d9f7ff;
  font-size: 15px;
  font-weight: 700;
  line-height: 1;
  background: linear-gradient(180deg, #075b8e, #064985);
}

.section-content {
  min-width: 0;
  min-height: 0;
  height: 100%;
}

.top-grid,
.bottom-grid {
  min-width: 0;
  height: 100%;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

.top-grid--three {
  grid-template-columns: 1fr 1fr 1fr;
}

.group {
  position: relative;
  min-width: 0;
  min-height: 0;
}

.group--clickable {
  cursor: pointer;
}

.group--clickable:hover .group-title {
  color: #5ff6ff;
}

.section-row--monitor .group,
.section-row--dispose .group {
  height: 100%;
}

.group-title {
  position: relative;
  height: 19px;
  line-height: 19px;
  box-sizing: border-box;
  padding-left: 12px;
  color: #d9f7ff;
  font-size: 13px;
  font-weight: 700;
}

.group-title::before {
  content: '';
  position: absolute;
  left: 1px;
  top: 5px;
  width: 0;
  height: 0;
  border-top: 4px solid transparent;
  border-bottom: 4px solid transparent;
  border-left: 6px solid #9fe6ff;
  filter: drop-shadow(0 0 4px #28b8ff);
}

.focus-kpi {
  position: absolute;
  right: 0;
  top: 0;
  width: 94px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: url('./img/bg_kpi.png') center / 100% 100% no-repeat;
  color: #174C7A;
  font-size: 14px;
  font-weight: 700;
  line-height: 1;
  z-index: 1;
}

.focus-kpi--clickable {
  cursor: pointer;
}

.focus-kpi--clickable:hover {
  filter: brightness(1.12);
}

.stat-row {
  min-width: 0;
  margin-top: 5px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
}

.stat-row--clickable {
  cursor: pointer;
}

.stat-row--clickable:hover .data-badge {
  filter: brightness(1.12);
}

.stat-row--wide {
  width: 236px;
}

.data-badge,
.time-card {
  min-width: 0;
  height: 27px;
  box-sizing: border-box;
  background: url('./img/bg_left_top01.png') center / 100% 100% no-repeat;
}

.data-badge {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 8px;
}

.data-badge--orange {
  background-image: url('./img/bg_list_data_3.png');
}

.data-badge--clickable {
  cursor: pointer;
}

.data-badge--clickable:hover {
  filter: brightness(1.12);
}

.data-badge span {
  min-width: 0;
  color: #d9f7ff;
  font-size: 13px;
  font-weight: 700;
  white-space: nowrap;
}

.data-badge strong {
  flex: 0 0 auto;
  color: #5ff6ff;
  font-family: 'BZBridgeDIN', Arial, sans-serif;
  font-size: 18px;
  line-height: 1;
  text-shadow: 0 0 8px rgba(51, 236, 255, 0.85);
}

.data-badge--orange strong {
  color: #ffbc00;
  text-shadow: 0 0 8px rgba(255, 188, 0, 0.75);
}

.time-card {
  margin-top: 5px;
  padding: 0 8px;
  overflow: hidden;
  color: #d9f7ff;
  font-size: 13px;
  line-height: 27px;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.time-card--center {
  text-align: center;
}

.section-row--monitor .time-card,
.section-row--dispose .time-card {
  height: calc(100% - 56px);
  display: flex;
  align-items: center;
  line-height: 1.35;
}

.section-row--monitor .time-card--center,
.section-row--dispose .time-card--center {
  justify-content: center;
}

.section-row--monitor .time-card__scroll,
.section-row--dispose .time-card__scroll {
  width: 100%;
}

.time-card__scroll {
  height: 27px;
  animation: bridge-monitor-scroll 8s linear infinite;
}

.time-card__item {
  height: 27px;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.time-card__item--clickable {
  cursor: pointer;
}

.time-card__item--clickable:hover {
  color: #5ff6ff;
}

.time-card__tooltip {
  display: inline-block;
  max-width: 360px;
  line-height: 18px;
  white-space: normal;
  word-break: break-all;
}

.event-card {
  position: relative;
  min-width: 0;
  height: 100%;
  margin-top: 5px;
  display: grid;
  grid-template-columns: 101px 1fr;
  gap: 8px;
  box-sizing: border-box;
  padding: 6px;
  border: 1px solid rgba(29, 150, 255, 0.42);
  background: linear-gradient(90deg, rgba(5, 59, 119, 0.82), rgba(4, 35, 82, 0.58));
}

.event-card--empty {
  display: flex;
  align-items: center;
  justify-content: center;
}

.section-row--patrol .event-card {
  margin-top: 5px;
  height: calc(100% - 5px);
}

.event-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: rgba(217, 247, 255, 0.62);
  font-size: 13px;
}

.event-empty__icon {
  width: 42px;
  height: 30px;
  border: 1px solid rgba(95, 246, 255, 0.32);
  background:
    linear-gradient(90deg, rgba(95, 246, 255, 0.32) 0 12px, transparent 12px) 10px 9px / 22px 2px no-repeat,
    linear-gradient(90deg, rgba(95, 246, 255, 0.22) 0 18px, transparent 18px) 10px 17px / 24px 2px no-repeat,
    rgba(15, 80, 145, 0.28);
  box-shadow: inset 0 0 12px rgba(39, 186, 255, 0.16);
}

.event-empty__text {
  line-height: 18px;
}

.patrol-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

.patrol-col {
  min-width: 0;
}

.event-image {
  width: 101px;
  height: 100%;
  min-height: 72px;
  object-fit: cover;
  border: 1px solid rgba(31, 117, 221, 0.55);
}

.event-info {
  min-width: 0;
  padding-right: 47px;
}

.event-title {
  display: flex;
  align-items: center;
  gap: 7px;
  min-width: 0;
  margin-bottom: 8px;
}

.event-title span {
  flex: 0 0 auto;
  color: #d9f7ff;
  font-size: 13px;
  font-weight: 700;
}

.event-title em {
  position: relative;
  min-width: 0;
  padding-left: 13px;
  color: #27baff;
  font-size: 12px;
  font-style: normal;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.event-title em::before {
  content: '';
  position: absolute;
  left: 0;
  top: 2px;
  width: 9px;
  height: 12px;
  background: radial-gradient(circle at 50% 34%, #27baff 0 3px, transparent 4px), linear-gradient(45deg, transparent 45%, #27baff 46% 54%, transparent 55%);
}

.event-text,
.event-time {
  min-width: 0;
  color: #8bbce6;
  font-size: 12px;
  line-height: 17px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 8px;
}

.event-time {
  color: #d9f7ff;
}

.status-tag {
  position: absolute;
  right: 6px;
  top: 8px;
  height: 22px;
  line-height: 20px;
  box-sizing: border-box;
  padding: 0 7px;
  border: 1px solid #e6d76a;
  color: #fff6b3;
  font-size: 12px;
  font-weight: 700;
  background: rgba(74, 69, 23, 0.82);
}

.card-warn {
  color: #ffbc00;
}

@keyframes bridge-monitor-scroll {
  0%,
  24% {
    transform: translateY(0);
  }

  100% {
    transform: translateY(calc(-100% + 27px));
  }
}
</style>
