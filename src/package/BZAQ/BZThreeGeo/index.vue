<template>
  <div class="bz-three-geo" :class="{ 'bz-three-geo--no-bg': !showBackground }" :style="containerStyle">
    <div class="dispatch-row">
      <button class="dispatch-btn" type="button" @click="openDispatchIframe">调度</button>
    </div>
    <section class="geo-block geo-block--danger">
      <div class="danger-main">
        <div class="danger-title">危岩滑坡</div>
        <div class="danger-stat-grid">
          <div class="danger-stat">
            <div class="danger-stat__value danger-stat__value--clickable" @click.stop="checkGeoHiddenDangerLayer">{{ geoStats.landGeoHazardPoint }}</div>
            <div class="danger-stat__base"></div>
            <div class="danger-stat__label danger-stat__label--clickable" @click="openGeoDisasterList">地灾隐患点</div>
          </div>
          <div class="danger-stat">
            <div class="danger-stat__value danger-stat__value--clickable" @click.stop="checkGeoGridMemberLayer">{{ geoStats.gridPersonnel }}</div>
            <div class="danger-stat__base"></div>
            <div class="danger-stat__label danger-stat__label--clickable" @click="openGeoGridPersonList">四重网格员</div>
          </div>
          <div class="danger-stat danger-stat--orange">
            <div class="danger-stat__value danger-stat__value--clickable" @click.stop="checkGeoDeviceAlarmLayer">{{ geoStats.todayDeviceAlarmPoint }}</div>
            <div class="danger-stat__base"></div>
            <div class="danger-stat__label danger-stat__label--clickable" @click="openGeoVideoRiskList">今日设备告警</div>
          </div>
          <div class="danger-stat danger-stat--clickable" @click="openGeoGridPatrolPointList">
            <div class="danger-stat__value">{{ geoStats.inspectionCompletionRate }}%</div>
            <div class="danger-stat__base"></div>
            <div class="danger-stat__label">本周巡查点位完成率</div>
          </div>
        </div>
      </div>
    </section>

    <section class="geo-block geo-block--path">
      <div class="path-main">
        <div class="path-title">四老一步道</div>
        <div class="path-stat-row">
          <div class="path-stat-card path-stat-card--clickable" @click="checkFourOldTrailLayer">
            <span>风险隐患点总数</span>
            <strong>{{ geoStats.riskPoint }}</strong>
          </div>
          <div class="path-stat-card">
            <span>“一键报险”上报问题数</span>
            <strong>0/11</strong>
          </div>
        </div>
        <div class="path-table-wrap">
          <div class="path-table-scroll" :class="{ 'is-scroll': pathRows.length > 2 }">
            <div
              v-for="row in pathScrollRows"
              :key="row.scrollKey"
              class="path-table-row"
            >
              <div class="path-table-cell path-table-cell--time">{{ row.time }}</div>
              <div class="path-table-cell path-table-cell--content">{{ row.content }}</div>
              <div class="path-table-cell path-table-cell--status">{{ row.status }}</div>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
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

const sourceName = 'BZThreeGeo'
const OPEN_GEO_EVENT = 'OPEN_GEO_DIA'
const OPEN_IFRAME_EVENT = 'OPEN_IFRAME_DIA'
const TOGGLE_GEO_DEVICE_ALARM_EVENT = 'TOGGLE_GEO_DEVICE_ALARM_LAYER'
const CHECK_GEO_LEGEND_EVENT = 'CHECK_GEO_LEGEND'
const BASE_URL = 'http://23.99.16.179:11001'
const OLD_BASE_URL = `${BASE_URL}/api/boot/system/old`

const geoStats = ref({
  landGeoHazardPoint: 0,
  gridPersonnel: 0,
  deviceAlarm: 0,
  todayDeviceAlarmPoint: 0,
  inspectionPoint: 0,
  inspectionCompletionRate: 0,
  uninspected: 0,
  riskPoint: 0,
  projectProgress: 0,
  projectAll: 0
})

const containerStyle = computed(() => {
  const width = Number(props.chartConfig?.attr?.w)
  const height = Number(props.chartConfig?.attr?.h)
  return {
    width: Number.isFinite(width) && width > 0 ? `${width}px` : '540px',
    height: Number.isFinite(height) && height > 0 ? `${height + 28}px` : '282px'
  }
})

const showBackground = computed(() => props.chartConfig?.option?.showBackground !== 'hide')

const eventBus = {
  emit(event, data) {
    props.bus?.emit(event, { data, source: sourceName })
  }
}

const getResponseData = data => data?.data ?? data ?? {}

const pathRows = [
  {
    id: 1,
    time: '2026-08-19 17:21:49',
    content: '桂花园路桂花大厦停车场旁边的围墙有裂痕',
    status: '处置中'
  },
  {
    id: 2,
    time: '-',
    content: '暂无数据',
    status: '-'
  }
]

const pathScrollRows = computed(() => {
  const rows = pathRows.map(row => ({ ...row, scrollKey: String(row.id) }))
  if (rows.length <= 2) return rows

  return [...pathRows, ...pathRows].map((row, index) => ({
    ...row,
    scrollKey: `${row.id}_${index}`
  }))
})

const formatRate = value => {
  const num = Number(value)
  if (!Number.isFinite(num)) return 0
  return Number.isInteger(num) ? num : num.toFixed(2)
}

async function fetchGeoData() {
  try {
    const [gridRes, warnRes, engineeringRes, riskPointRes] = await Promise.all([
      axios.get(`${BASE_URL}/api/boot/system/land/gridPatrol`),
      axios.get(`${BASE_URL}/api/boot/system/land/geoWarn`),
      axios.get(`${BASE_URL}/api/boot/system/land/engineering`),
      axios.get(`${OLD_BASE_URL}/risk/point`)
    ])

    const gridData = getResponseData(gridRes?.data)
    const warnData = getResponseData(warnRes?.data)
    const engineeringData = getResponseData(engineeringRes?.data)
    const riskPointData = getResponseData(riskPointRes?.data)
    const inspectionPoint = Number(gridData.inspectionPoint) || 0
    const inspectionCompleted = Number(gridData.inspectionCompleted) || 0
    geoStats.value = {
      landGeoHazardPoint: Number(gridData.landGeoHazardPoint) || 0,
      gridPersonnel: Number(gridData.gridPersonnel) || 0,
      deviceAlarm: Number(warnData.deviceAlarm) || 0,
      todayDeviceAlarmPoint: Number(gridRes.alarmNumber) || 0,
      inspectionPoint,
      inspectionCompletionRate: formatRate(gridData.inspectionCompletionRate),
      uninspected: Math.max(inspectionPoint - inspectionCompleted, 0),
      riskPoint: Number(riskPointData) || 0,
      projectProgress: Number(engineeringData.projectProgress) || 0,
      projectAll: Number(engineeringData.projectAll) || 0
    }
  } catch (error) {
    console.error('获取三类地灾数据失败:', error)
  }
}

function openDispatchIframe() {
  try {
    const token = window.localStorage.getItem('dcqc-tk')
    const url = `http://23.213.34.1:8006/reservoir/#/login?dcqc-tk=${token}&adcode=500103`
    eventBus.emit(OPEN_IFRAME_EVENT, { url, type: 'geo-dispatch' })
  } catch (error) {
    console.error('获取地灾调度登录token失败:', error)
  }
}

function openGeoDisasterList() {
  eventBus.emit(OPEN_GEO_EVENT, { diaName: 'geo-‌disaster-list' })
}

function openGeoGridPersonList() {
  eventBus.emit(OPEN_GEO_EVENT, { diaName: 'geo-‌grid-person-list' })
}

function openGeoGridPatrolPointList() {
  eventBus.emit(OPEN_GEO_EVENT, { diaName: 'geo-grid-patrol-point-list' })
}

function openGeoVideoRiskList() {
  eventBus.emit(OPEN_GEO_EVENT, { diaName: 'geo-‌video-risk-list', params: { isToday: '1' } })
}

function checkGeoHiddenDangerLayer() {
  eventBus.emit(CHECK_GEO_LEGEND_EVENT, {
    key: 'risk-hidden-danger',
    action: 'toggle'
  })
}

function checkGeoDeviceAlarmLayer() {
  eventBus.emit(TOGGLE_GEO_DEVICE_ALARM_EVENT, {})
}

function checkGeoGridMemberLayer() {
  eventBus.emit(CHECK_GEO_LEGEND_EVENT, {
    key: 'device-grid-member',
    action: 'toggle'
  })
}

function checkFourOldTrailLayer() {
  eventBus.emit(CHECK_GEO_LEGEND_EVENT, {
    key: 'risk-four-old-trail',
    action: 'toggle'
  })
}

onMounted(fetchGeoData)
</script>

<script lang="ts">
export default {
  name: 'BZThreeGeo',
  version: '1.0.0'
}
</script>

<style lang="scss" scoped>
@font-face {
  font-family: 'BZThreeGeoPuHuiTi';
  src: url('./font/Alibaba_PuHuiTi_2.0_55_Regular_55_Regular.ttf') format('truetype');
  font-weight: 400;
  font-style: normal;
  font-display: swap;
}

@font-face {
  font-family: 'BZThreeGeoDIN';
  src: url('./font/D-DIN-Bold.otf') format('opentype');
  font-weight: 700;
  font-style: normal;
  font-display: swap;
}
@font-face {
  font-family: 'YSBTHei';
  src: url('./font/UISDC-BiaoTiHei.ttf') format('opentype');
  font-weight: 700;
  font-style: normal;
  font-display: swap;
}

.bz-three-geo {
  padding: 0 12px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  gap: 4px;
  overflow: hidden;
  color: #d9f7ff;
  font-family: 'BZThreeGeoPuHuiTi', Microsoft YaHei, Arial, sans-serif;
}

.bz-three-geo--no-bg {
  background: transparent;
}

.dispatch-row {
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  flex: 0 0 24px;
}

.dispatch-btn {
  width: 44px;
  height: 24px;
  padding: 0;
  border: 1px solid rgba(64, 175, 255, 0.7);
  color: #dff8ff;
  font-size: 13px;
  font-weight: 700;
  background: linear-gradient(180deg, rgba(13, 86, 152, 0.94), rgba(5, 48, 105, 0.94));
  box-shadow: inset 0 0 10px rgba(73, 197, 255, 0.24);
  cursor: pointer;
}

.geo-block {
  flex: 0 0 auto;
  min-height: 0;
  display: grid;
  grid-template-columns: 24px 1fr;
  gap: 4px;
}

.geo-block--danger {
  grid-template-columns: 1fr;
  height: 100px;
}

.geo-block--path {
  grid-template-columns: 1fr;
  height: 160px;
}

.side-title {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 1px;
  color: #fff2d6;
  font-size: 15px;
  font-weight: 700;
  line-height: 1;
  background: linear-gradient(180deg, #532f2c, #7b3c1e);
}

.side-title--path {
  color: #d9f7ff;
  background: linear-gradient(180deg, #078194, #075b8e);
}

.danger-main {
  min-width: 0;
  display: grid;
  grid-template-rows: 34px 62px;
  gap: 4px;
  align-content: start;
}

.danger-title {
  height: 32px;
  box-sizing: border-box;
  padding-left: 36px;
  padding-right: 4px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #f7fbff;
  font-family: 'YSBTHei';
  font-size: 24px;
  font-weight: 700;
  letter-spacing: 0;
  text-shadow: 0 0 8px rgba(90, 235, 255, 0.62);
  background-image: url('./img/bg_second_title.png');
  background-size: 100% 100%;
  background-repeat: no-repeat;
}

.danger-stat-grid {
  min-width: 0;
  display: grid;
  grid-template-columns: 1fr 1fr 1.08fr 1.36fr;
  gap: 8px;
  align-items: end;
  padding: 0 4px;
  box-sizing: border-box;
}

.danger-stat {
  position: relative;
  height: 62px;
  min-width: 0;
  display: grid;
  grid-template-rows: 37px 1fr;
  justify-items: center;
  cursor: default;
}

.danger-stat--clickable {
  cursor: pointer;
}

.danger-stat--clickable:hover {
  filter: brightness(1.15);
}

.danger-stat__value--clickable,
.danger-stat__label--clickable {
  cursor: pointer;
}

.danger-stat__value--clickable:hover,
.danger-stat__label--clickable:hover {
  filter: brightness(1.18);
}

.danger-stat__value {
  position: relative;
  z-index: 1;
  width: 86px;
  height: 38px;
  line-height: 33px;
  color: #5ff6ff;
  font-family: 'BZThreeGeoDIN', Arial, sans-serif;
  font-size: 20px;
  text-align: center;
  text-shadow: 0 0 10px rgba(51, 236, 255, 0.88);
}

.danger-stat--orange .danger-stat__value {
  color: #ffaf72;
  text-shadow: 0 0 10px rgba(255, 150, 75, 0.78);
}

.danger-stat__base {
  position: absolute;
  left: 50%;
  width: 64px;
  height: 47px;
  transform: translateX(-50%);
  background-image: url('./img/item-bottom.png');
  background-size: 100% 100%;
  background-repeat: no-repeat;
}

.danger-stat__label {
  width: 100%;
  height: 22px;
  line-height: 22px;
  box-sizing: border-box;
  padding: 0 4px;
  color: #d8eefb;
  font-size: 14px;
  font-weight: 700;
  text-align: center;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  background: url('./img/bottom-bg.png') no-repeat center / 100% 100%;
  margin-top: 10px;
}

.path-main {
  min-width: 0;
  height: 100%;
  display: grid;
  grid-template-rows: 34px 36px 1fr;
  gap: 4px;
  margin-top: 4px;
}

.path-title {
  height: 32px;
  box-sizing: border-box;
  padding-left: 36px;
  min-width: 0;
  display: flex;
  align-items: center;
  color: #f7fbff;
  font-family: 'YSBTHei';
  font-size: 24px;
  font-weight: 700;
  letter-spacing: 0;
  text-shadow: 0 0 8px rgba(90, 235, 255, 0.62);
  background-image: url('./img/bg_second_title.png');
  background-size: 100% 100%;
  background-repeat: no-repeat;
}

.path-stat-row {
  min-width: 0;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.path-stat-card {
  min-width: 0;
  height: 32px;
  padding: 0 12px;
  box-sizing: border-box;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-image: url('./img/bgList.png');
  background-size: 100% 100%;
  background-repeat: no-repeat;
}

.path-stat-card--clickable {
  cursor: pointer;
}

.path-stat-card--clickable:hover {
  filter: brightness(1.14);
}

.path-stat-card span {
  min-width: 0;
  color: #d8eefb;
  font-size: 16px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.path-stat-card strong {
  flex: 0 0 auto;
  color: #5ff6ff;
  font-family: 'BZThreeGeoDIN', Arial, sans-serif;
  font-size: 18px;
  line-height: 1;
  text-shadow: 0 0 8px rgba(51, 236, 255, 0.85);
}

.path-table-wrap {
  width: 100%;
  height: 72px;
  overflow: hidden;
  border-top: 1px solid rgba(34, 131, 206, 0.72);
  border-left: 1px solid rgba(34, 131, 206, 0.32);
  border-right: 1px solid rgba(34, 131, 206, 0.32);
  background: rgba(6, 46, 94, 0.42);
}

.path-table-scroll {
  width: 100%;
}

.path-table-scroll.is-scroll {
  animation: pathTableScroll 8s linear infinite;
}

.path-table-wrap:hover .path-table-scroll.is-scroll {
  animation-play-state: paused;
}

.path-table-row {
  height: 36px;
  display: grid;
  grid-template-columns: 130px minmax(0, 1fr) 92px;
  background: rgba(6, 49, 96, 0.74);
}

.path-table-row:nth-child(even) {
  background: rgba(6, 64, 121, 0.74);
}

.path-table-cell {
  min-width: 0;
  height: 36px;
  line-height: 36px;
  padding: 0 14px;
  box-sizing: border-box;
  color: #8bbce6;
  font-size: 13px;
  border-right: 1px solid rgba(40, 125, 197, 0.52);
  border-bottom: 1px solid rgba(40, 125, 197, 0.52);
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.path-table-cell:last-child {
  border-right: 0;
}

.path-table-cell--content {
  color: #9ac8eb;
}

.path-table-cell--status {
  color: #00e887;
  font-size: 13px;
  font-weight: 700;
  text-align: center;
}

@keyframes pathTableScroll {
  0%,
  28% {
    transform: translateY(0);
  }

  100% {
    transform: translateY(-50%);
  }
}

.title-row {
  height: 18px;
  line-height: 18px;
  padding-left: 13px;
  box-sizing: border-box;
  position: relative;
  color: #d9f7ff;
  font-size: 15px;
  font-weight: 700;
}

.title-row--clickable {
  cursor: pointer;
}

.title-row--clickable:hover {
  color: #5ff6ff;
  text-shadow: 0 0 8px rgba(51, 236, 255, 0.7);
}

.title-row::before {
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

.risk-panel {
  height: 68px;
  border: 1px solid rgba(255, 188, 0, 0.2);
  background: linear-gradient(90deg, rgba(4, 72, 135, 0.25), rgba(3, 31, 68, 0.18));
}

.risk-title {
  height: 31px;
  line-height: 31px;
  color: #fff1a9;
  font-size: 17px;
  font-weight: 700;
  text-align: center;
  border-bottom: 1px solid rgba(255, 188, 0, 0.42);
}

.risk-value {
  height: 35px;
  line-height: 35px;
  color: #ffbc00;
  font-family: 'BZThreeGeoDIN', Arial, sans-serif;
  font-size: 16px;
  font-weight: 700;
  text-align: center;
}

.path-detail {
  height: 28px;
  min-width: 0;
  display: flex;
  align-items: center;
  gap: 6px;
}

.path-detail__title {
  flex: 0 0 auto;
  color: #ffbc00;
  font-size: 14px;
  font-weight: 700;
}

.path-detail__desc {
  min-width: 0;
  color: #27baff;
  font-size: 12px;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.risk-level {
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  min-width: 0;
  color: #8bbce6;
  font-size: 12px;
}

.risk-level strong {
  width: 22px;
  height: 18px;
  line-height: 18px;
  color: #fff;
  font-size: 12px;
  text-align: center;
  background: rgba(255, 57, 57,0.5);
}

.risk-image {
  height: 56px;
  width: 88px;
  display: block;
  object-fit: cover;
  border: 1px solid rgba(31, 117, 221, 0.55);
  box-shadow: inset 0 0 12px rgba(0, 0, 0, 0.45);
}
</style>
