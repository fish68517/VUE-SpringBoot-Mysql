<template>
  <div class="point-warning-detail">
    <div class="modal-header">
      <div class="header-title">点位预警详情</div>
      <button class="close-btn" @click="handleClose"></button>
    </div>

    <div class="modal-body">
      <div class="tab-nav">
        <button
          v-for="item in tabOptions"
          :key="item.value"
          class="tab-item"
          :class="{ active: activeInfoTab === item.value }"
          type="button"
          @click="activeInfoTab = item.value"
        >
          {{ item.label }}
        </button>
      </div>
      <div class="content-layout" :class="{ 'work-order-mode': activeInfoTab === 'workOrder' }">
        <!-- 左侧：事件信息 / 工单信息 -->
        <aside class="info-panel">
          <div v-if="activeInfoTab === 'event'" class="info-header">
            <span class="info-title">{{ infoTitle }}</span>
            <button class="three-level-btn" @click="handleThreeLevel">三级贯通</button>
          </div>
          <div class="info-content">
            <!-- 事件信息 -->
            <template v-if="activeInfoTab === 'event'">
              <div
                v-for="item in warningInfo"
                :key="item.label"
                class="info-row"
                :class="{ long: item.long, compact: item.compact }"
              >
                <span class="label">{{ item.label }}：</span>
                <span
                  class="value"
                  :class="{
                    badge: item.badge,
                    orange: item.orange,
                    nowrap: item.nowrap
                  }"
                >
                  <span :class="{ green: item.green }">{{ item.value }}</span>
                  <span
                    v-if="item.tag"
                    class="level-tag"
                    :class="{ orange: item.tagOrange }"
                  >
                    {{ item.tag }}
                  </span>
                </span>
              </div>
            </template>
            <!-- 工单信息 -->
            <template v-else>
              <!-- 上：五个指标 -->
              <div class="work-order-metrics">
                <div
                  v-for="m in workOrderMetrics"
                  :key="m.title"
                  class="metric-card"
                >
                  <div class="metric-card__title">{{ m.title }}</div>
                  <div class="metric-card__value">
                    <span class="metric-num">{{ m.value }}</span>
                    <span class="metric-unit">{{ m.unit }}</span>
                  </div>
                </div>
              </div>
              <!-- 下：工单表格 -->
              <div class="work-order-table-wrap">
                <div class="work-order-table-scroll">
                  <table class="work-order-table">
                    <thead>
                      <tr>
                        <th v-for="(h, i) in workOrderColumns" :key="i">{{ h }}</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr
                        v-for="(row, ri) in workOrderList"
                        :key="ri"
                        class="clickable-row"
                        @click="openWorkOrderDetail(row)"
                      >
                        <td>{{ ri + 1 }}</td>
                        <td>{{ row.orderNo }}</td>
                        <td>{{ row.orderType }}</td>
                        <td>{{ row.status }}</td>
                        <td>{{ row.pointName }}</td>
                        <td>{{ row.rescueStaff }}</td>
                        <td>{{ row.specialVehicle }}</td>
                        <td>{{ row.mannedVehicle }}</td>
                        <td>{{ row.drainageDevice }}</td>
                        <td>{{ row.pumpCapacity }}</td>
                        <td>{{ row.description }}</td>
                        <td>{{ row.startTime }}</td>
                        <td>{{ row.endTime }}</td>
                        <td>{{ row.creator }}</td>
                        <td>{{ row.createTime }}</td>
                        <td>{{ row.team }}</td>
                        <td>{{ row.teamLeader }}</td>
                        <td>{{ row.leaderPhone }}</td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </template>
          </div>
        </aside>

        <!-- 右侧：风险点内容（仅事件信息时显示） -->
        <main v-if="activeInfoTab === 'event'" class="detail-panel">
          <!-- 基础信息 -->
          <div class="section-panel basic-info-panel">
            <div class="section-title"><span class="section-title-text">基础信息</span></div>
            <div class="section-content">
              <div class="location-title">
                <span class="location-text">{{ riskDetail?.name || rowData.name || rowData.riskPoint || '-' }}</span>
                <div class="arrows"></div>
              </div>

              <div class="info-grid">
                <div class="info-item" v-for="item in basicInfo" :key="item.label">
                  <span class="label">{{ item.label }}：</span>
                  <span class="value">{{ item.value }}</span>
                </div>
              </div>

              <div class="contact-cards">
                <div class="contact-card" v-for="card in contactCards" :key="card.title">
                  <div class="card-title"><span class="title-text">{{ card.title }}</span></div>
                  <div class="card-name">{{ card.name }}</div>
                  <div class="card-phone">
                    {{ card.phone }} <i class="phone-icon"></i>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 视频监控（图片展示，点击查看摄像头） -->
          <div class="section-panel video-info-panel">
            <div class="section-title"><span class="section-title-text">视频监控</span></div>
            <div class="section-content">
              <div class="video-container" @click="openVideo">
                <img
                  v-if="videoImg"
                  :src="videoImg"
                  class="video-placeholder"
                  alt="监控"
                >
                <div v-else class="video-empty">暂无监控图片</div>
                <div class="video-overlay-time">{{ videoTime }}</div>
                <div class="video-overlay-location">{{ riskDetail?.name || rowData.name || rowData.riskPoint || '-' }}</div>
              </div>
            </div>
          </div>
        </main>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'

const props = defineProps({
  rowData: {
    type: Object,
    default: () => ({})
  },
  bus: {
    type: Object,
    default: () => null
  },
  round: {
    type: [String, Number],
    default: null
  }
})

const emit = defineEmits(['close'])

const sourceName = 'WaterlogPointWarningDetail'
const WATERLOG_BASE_URL = 'http://23.99.16.179:11001/api/boot/system/waterlog'

const activeInfoTab = ref('event')
const tabOptions = [
  { label: '事件信息', value: 'event' },
  { label: '工单信息', value: 'workOrder' }
]
const infoTitle = computed(() => activeInfoTab.value === 'event' ? '事件信息' : '工单信息')

const eventBus = {
  emit(event, data) {
    if (!props.bus) return
    props.bus.emit(event, { data, source: sourceName })
  }
}

const rowData = computed(() => props.rowData || {})

// 接口返回的风险点位详情数据
const riskDetail = ref(null)

// 调用接口获取风险点位及关联摄像头信息（与道路风险点详情弹窗一致）
const fetchRiskDetail = async () => {
  const pointId = rowData.value?.pointId
  if (!pointId) return
  try {
    const res = await axios.get(`${WATERLOG_BASE_URL}/risk/carema`, {
      params: { pointId }
    })
    const data = res?.data?.data
    if (data) {
      riskDetail.value = Array.isArray(data) ? data[0] : data
    }
  } catch (error) {
    console.error('获取风险点位详情失败:', error)
  }
}

onMounted(() => {
  fetchRiskDetail()
  fetchWorkOrderDot()
})

// 监控图片（优先使用接口数据中的图片字段，否则使用行数据中的图片字段）
const videoImg = computed(() => {
  const d = riskDetail.value || {}
  const r = rowData.value || {}
  return d.imgUrl || d.picture || d.photo || r.imgUrl || r.picture || r.photo || ''
})

// 监控时间
const videoTime = computed(() => {
  const d = rowData.value
  return d.warningPeriodStart || d.publishTime || d.createTime || '-'
})

// 点击查看摄像头 — 通过事件总线通知主组件，由主组件匹配 pointId 后打开视频
function openVideo() {
  const pointId = rowData.value?.pointId
  if (!pointId) {
    console.warn('当前行数据缺少 pointId，无法查看摄像头')
    return
  }
  eventBus.emit('pointWarningDetail:openVideo', { pointId })
}

// 三级贯通按钮
const threeLevelLoading = ref(false)
async function handleThreeLevel() {
  const d = props.rowData
  const eventNumber = d?.eventNum
  if (!eventNumber || threeLevelLoading.value) return
  try {
    threeLevelLoading.value = true
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
    eventBus.emit('EventDetailModal:eventData', eventData)
  } catch (err) {
    console.error('获取事件详情失败:', err)
  } finally {
    threeLevelLoading.value = false
  }
}

const warningLevelMap = { '1': '一级', '2': '二级', '3': '三级', '4': '四级' }
const warningStatusMap = { '1': '未响应', '2': '已响应', '3': '已结束' }

// 左侧工单信息
const warningInfo = computed(() => {
  const d = props.rowData
  if (!d || !Object.keys(d).length) return []
  const level = warningLevelMap[d.warningLevel] || d.warningLevel || '-'
  const status = warningStatusMap[d.warningStatus] || d.warningStatus || '-'
  const isResponded = d.warningStatus === '2'
  return [
    { label: '预警编码', value: d.code || '-', compact: true },
    { label: '预警点位', value: d.name || d.riskPoint || '-', compact: true },
    { label: '场景类型', value: d.riskPoint || '-', compact: true },
    { label: '预警状态', value: status, green: isResponded, tag: level, tagOrange: d.warningLevel === '1' || d.warningLevel === '2', compact: true },
    { label: '预警等级', value: level, badge: true, orange: d.warningLevel === '1' || d.warningLevel === '2', compact: true },
    { label: '未来降水量', value: d.nextPrecipitation ? `${d.nextPrecipitation}mm` : '-', compact: true },
    { label: '预警阈值', value: d.warningThreshold ? `${d.warningThreshold}mm` : '-', compact: true },
    { label: '是否演练', value: d.isDrill || '否', compact: true },
    { label: '预警依据', value: d.warningBasis || '-', compact: true },
    { label: '预警时段', value: formatPeriod(d.warningPeriodStart, d.warningPeriodEnd), nowrap: true, compact: true },
    { label: '发布人', value: d.publishUnit || '-', compact: true },
    { label: '发布时间', value: d.publishTime || '-', compact: true },
    { label: '预警内容', value: d.warningContent || '-', long: true }
  ]
})

// 右侧基础信息（优先使用接口返回的 riskDetail 数据，行数据兜底）
const basicInfo = computed(() => {
  const d = riskDetail.value || {}
  const r = rowData.value || {}
  return [
    { label: '风险等级', value: d.pointLevel || r.riskLevel || r.riskGrade || '-' },
    { label: '所属分区', value: d.county || r.area || r.region || '-' },
    { label: '风险点类型', value: d.riskName || r.riskPointType || r.riskType || '-' },
    { label: '阈值(mm/2h)', value: d.threshold != null ? `${d.threshold}` : (r.warningThreshold != null ? `${r.warningThreshold}mm` : '-') },
    { label: '所属区县', value: d.county || r.district || r.districtName || '-' },
    { label: '场景类型', value: d.pointRiskType || r.sceneType || r.riskPoint || '-' },
    { label: '行业主管部门', value: d.unit || r.department || r.manageDept || '-' },
    { label: '所属街道', value: d.town || r.street || r.streetName || '-' },
    { label: '场景子类型', value: d.pointRiskType || r.sceneSubType || r.subSceneType || '-' },
    { label: '所属社区', value: d.community || r.community || r.communityName || '-' }
  ]
})

const parseResponsible = (raw) => {
  if (!raw) return { name: '-', phone: '-' }
  try {
    const obj = JSON.parse(raw)
    return { name: obj.dutyPersonName || '-', phone: obj.dutyPersonPhone || '-' }
  } catch (e) {
    return { name: '-', phone: '-' }
  }
}

const contactCards = computed(() => {
  const d = riskDetail.value || {}
  const r = rowData.value || {}
  const cspsflaq = parseResponsible(d.cspsflaqResponsible)
  const csnlzl = parseResponsible(d.csnlzlResponsible)
  const sdzd = parseResponsible(d.sdzdResponsible)
  const ghdw = parseResponsible(d.ghdwResponsible)
  return [
    { title: '市级负责人', name: cspsflaq.name, phone: cspsflaq.phone },
    { title: '区级负责人', name: csnlzl.name, phone: csnlzl.phone },
    { title: '镇街负责人', name: sdzd.name, phone: sdzd.phone },
    { title: '管护负责人', name: ghdw.name, phone: ghdw.phone }
  ]
})

function formatPeriod(start, end) {
  if (!start && !end) return '-'
  return `${start || ''} - ${end || ''}`
}

// 工单信息 - 五个指标
const workOrderMetrics = ref([
  { title: '应急抢险作业人员参与巡查处置', value: '0', unit: '人次' },
  { title: '特种车辆', value: '0', unit: '车次' },
  { title: '载人车辆', value: '0', unit: '车次' },
  { title: '排涝设备', value: '0', unit: '台次' },
  { title: '抽排能力', value: '0', unit: 'm³/h' }
])

// 工单信息 - 表格列头
const workOrderColumns = [
  '序号',
  '工单编号',
  '工单类型',
  '状态',
  '点位名称',
  '应急抢险作业人员参与巡查处置',
  '特种车辆',
  '载人车辆',
  '排涝设备',
  '抽排能力',
  '工单描述',
  '开始时间',
  '结束时间',
  '创建人',
  '创建时间',
  '负责班组',
  '班组组长',
  '组长联系电话'
]

// 工单信息 - 表格数据
const workOrderList = ref([])

// 获取工单信息（### /work/order/dot，入参 round）
const fetchWorkOrderDot = async () => {
  try {
    const params = {}
    if (props.round != null && props.round !== '') {
      params.round = props.round
    }
    const res = await axios.get(`${WATERLOG_BASE_URL}/work/order/dot`, { params })
    const data = res?.data?.data || {}
    // 填充五个指标
    workOrderMetrics.value = [
      { title: '应急抢险作业人员参与巡查处置', value: String(data.personnelNumber ?? 0), unit: '人次' },
      { title: '特种车辆', value: String(data.specialVehiclesNumber ?? 0), unit: '车次' },
      { title: '载人车辆', value: String(data.mannedVehiclesNumber ?? 0), unit: '车次' },
      { title: '排涝设备', value: String(data.pumpingEquipmentQuantity ?? 0), unit: '台次' },
      { title: '抽排能力', value: String(data.extractionCapacity ?? 0), unit: 'm³/h' }
    ]
    // 填充工单表格
    const list = data.list || []
    workOrderList.value = list.map((item) => ({
      ...item,
      orderNo: item.code || '-',
      orderType: item.workType || '-',
      status: item.state || '-',
      pointName: item.riskPoint || '-',
      rescueStaff: String(item.personnelNumber ?? '-'),
      specialVehicle: String(item.specialVehiclesNumber ?? '-'),
      mannedVehicle: String(item.mannedVehiclesNumber ?? '-'),
      drainageDevice: String(item.pumpingEquipmentQuantity ?? '-'),
      pumpCapacity: String(item.extractionCapacity ?? '-'),
      description: item.workOrderDescription || item.describe || '-',
      startTime: item.executeDate || '-',
      endTime: item.warningTimeEnd || '-',
      creator: item.createPersonName || '-',
      createTime: item.createDate || '-',
      team: item.reTeamName || '-',
      teamLeader: item.teamLeader || '-',
      leaderPhone: item.teamLeaderPhone || '-'
    }))
  } catch (error) {
    console.error('获取点位预警工单信息失败:', error)
  }
}

function handleClose() {
  emit('close')
}

function openWorkOrderDetail(row) {
  eventBus.emit('OPEN_WORK_ORDER_DETAIL', row)
}
</script>

<script>
export default {
  name: 'WaterlogPointWarningDetail'
}
</script>

<style lang="scss" scoped>
@font-face {
  font-family: 'Alibaba PuHuiTi 2.0';
  src: url('../font/Alibaba_PuHuiTi_2.0_55_Regular_55_Regular.ttf') format('truetype');
  font-weight: 400;
  font-style: normal;
}

@font-face {
  font-family: 'Alibaba PuHuiTi 2.0';
  src: url('../font/Alibaba_PuHuiTi_2.0_75_SemiBold_75_SemiBold.ttf') format('truetype');
  font-weight: 600;
  font-style: normal;
}

.point-warning-detail {
  font-family: 'Alibaba PuHuiTi 2.0', sans-serif;
  position: absolute;
  left: 50%;
  top: 50%;
  z-index: 1001;
  width: 1646px;
  height: 812px;
  transform: translate(-50%, -50%);
  color: #d8ecff;
  background: url('../img/Rectangle-346242153.png') no-repeat center / 100% 100%;
  border: 1px solid #a6ceff4d;
  box-shadow: 0 16px 56px rgba(0, 0, 0, 0.55);
  overflow: hidden;
  pointer-events: auto;
}

.modal-header {
  position: relative;
  width: 1638px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background:
    linear-gradient(90deg, #0b3b7000 0.2%, #2e6bdc59 50.04%, #0c2d5c00 99.87%),
    url('../img/waterlog_title_bg.png') no-repeat center / 100% 100%;
}

.header-title {
  text-align: center;
  font-family: "Alibaba PuHuiTi 2.0", sans-serif;
  font-size: 20px;
  font-style: normal;
  font-weight: 500;
  line-height: normal;
  background: linear-gradient(0deg, #5FBCFF 1.4%, #FFF 58.67%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.close-btn {
  position: absolute;
  right: 20px;
  top: 8px;
  width: 34px;
  height: 34px;
  border: none;
  background: url('../img/close.png') no-repeat center / 100% 100%;
  cursor: pointer;
}

.modal-body {
  height: calc(100% - 48px);
  padding: 18px 36px 34px;
  box-sizing: border-box;
}

.tab-nav {
  height: 42px;
  display: flex;
  align-items: center;
  gap: 18px;
  margin-bottom: 14px;
  border-bottom: 1px solid rgba(62, 160, 255, 0.15);
}

.tab-item {
  position: relative;
  height: 30px;
  padding: 0 24px;
  border: none;
  color: #749dc0;
  background: transparent;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;

  &.active {
    color: #5FBCFF;
    background: rgba(95, 188, 255, 0.1);
    border-radius: 4px;

    &::after {
      content: '';
      position: absolute;
      bottom: -7px;
      left: 50%;
      width: 32px;
      height: 3px;
      border-radius: 2px;
      background: linear-gradient(90deg, #5FBCFF, #FFF);
      transform: translateX(-50%);
    }
  }
}

.content-layout {
  display: grid;
  grid-template-columns: 420px 1fr;
  gap: 18px;
  height: calc(100% - 56px);

  &.work-order-mode {
    grid-template-columns: 1fr;
    gap: 0;
  }
}

/* 左侧工单信息 */
.info-panel {
  width: 420px;
  height: 100%;
  padding: 16px 18px;
  box-sizing: border-box;
  background: url('../img/bg_left_top01@2x.png') no-repeat center / 100% 100%;
  overflow: hidden;
  display: flex;
  flex-direction: column;

  .work-order-mode & {
    width: 100%;
  }
}

.info-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.info-title {
  font-size: 16px;
  font-weight: 600;
  color: #ffffff;
  background: url('../img/waterlog_tab.png') no-repeat center;
  background-size: 100% 100%;
  padding: 4px 24px;
}

.three-level-btn {
  height: 30px;
  padding: 0 16px;
  color: #cde9ff;
  border: 1px solid rgba(62, 160, 255, 0.6);
  border-radius: 3px;
  background: rgba(24, 88, 163, 0.6);
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s;

  &:hover {
    background: rgba(24, 88, 163, 0.9);
    border-color: rgba(62, 160, 255, 0.9);
  }

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
}

.info-content {
  flex: 1;
  overflow-y: auto;
  scrollbar-width: none;
  -ms-overflow-style: none;
  display: flex;
  flex-direction: column;

  &::-webkit-scrollbar {
    display: none;
  }
}

// 工单信息 - 指标卡片
.work-order-metrics {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 8px;
  margin-bottom: 12px;
  flex-shrink: 0;
}

.metric-card {
  display: flex;
  flex-direction: column;
  justify-content: center;
  min-height: 56px;
  padding: 8px 10px;
  box-sizing: border-box;
  background: rgba(10, 45, 93, 0.8);
  border: 1px solid rgba(95, 188, 255, 0.3);
  border-radius: 3px;
}

.metric-card__title {
  font-size: 12px;
  font-weight: 500;
  color: #749dc0;
  line-height: 18px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 4px;
}

.metric-card__value {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.metric-num {
  font-size: 22px;
  font-weight: 600;
  color: #ffffff;
  line-height: 24px;
}

.metric-unit {
  font-size: 12px;
  color: #87e8ff;
  white-space: nowrap;
}

// 工单信息 - 表格
.work-order-table-wrap {
  flex: 1;
  overflow: hidden;
  border: 1px solid rgba(95, 188, 255, 0.3);
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.work-order-table-scroll {
  flex: 1;
  overflow-x: auto;
  overflow-y: auto;
  scrollbar-width: thin;
  scrollbar-color: rgba(95, 188, 255, 0.6) rgba(8, 53, 108, 0.3);
  -ms-overflow-style: auto;

  &::-webkit-scrollbar {
    height: 6px;
    width: 6px;
    display: block;
  }

  &::-webkit-scrollbar-track {
    background: rgba(8, 53, 108, 0.3);
    border-radius: 3px;
  }

  &::-webkit-scrollbar-thumb {
    background: linear-gradient(90deg, rgba(95, 188, 255, 0.6), rgba(135, 232, 255, 0.6));
    border-radius: 3px;
    border: 1px solid rgba(95, 188, 255, 0.2);
  }

  &::-webkit-scrollbar-thumb:hover {
    background: linear-gradient(90deg, rgba(95, 188, 255, 0.9), rgba(135, 232, 255, 0.9));
  }
}

.work-order-table {
  width: 100%;
  min-width: 1800px;
  border-collapse: collapse;
  font-size: 13px;
  table-layout: auto;

  th,
  td {
    padding: 0 8px;
    height: 36px;
    text-align: center;
    white-space: nowrap;
    border-right: 1px solid rgba(95, 188, 255, 0.3);
    border-bottom: 1px solid rgba(95, 188, 255, 0.15);
    box-sizing: border-box;

    &:last-child {
      border-right: none;
    }
  }

  th {
    background: #08356C;
    color: #87e8ff;
    font-size: 12px;
    font-weight: 500;
    position: sticky;
    top: 0;
    z-index: 1;
  }

  tbody tr {
    background: #0A2D5D;
    cursor: pointer;

    &:nth-child(even) {
      background: #0A3268;
    }

    &:hover {
      background: rgba(95, 188, 255, 0.15);
    }
  }

  td {
    color: #e0f0ff;
  }
}

.info-row {
  display: flex;
  align-items: flex-start;
  min-height: 24px;
  line-height: 20px;
  margin-bottom: 13px;
  font-family: "Alibaba PuHuiTi 2.0", sans-serif;
  font-size: 14px;
  font-style: normal;
  font-weight: 500;

  &.compact {
    margin-bottom: 10px;
  }

  &.long {
    margin-bottom: 0;

    .value {
      line-height: 22px;
      text-align: left;
      word-break: normal;
      overflow-wrap: anywhere;
    }
  }
}

.label {
  flex-shrink: 0;
  width: 88px;
  color: #749dc0;
}

.value {
  flex: 1;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  color: #ffffff;
  min-width: 0;
  word-break: normal;
  overflow-wrap: break-word;
}

.nowrap {
  white-space: nowrap;
  font-size: 13px;
}

.green {
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 1;
  overflow: hidden;
  color: #12dd60;
  text-overflow: ellipsis;
}

.badge {
  display: inline-flex;
  flex: initial;
  height: 22px;
  padding: 0 7px;
  align-items: center;
  color: #ffffff;
  border: 1px solid #1cf582;
  background: rgba(20, 171, 93, 0.18);
  box-sizing: border-box;

  &.orange {
    border-color: #ffb26d;
    background: rgba(211, 105, 35, 0.32);
  }
}

.level-tag {
  display: inline-flex;
  height: 22px;
  padding: 0 7px;
  align-items: center;
  color: #ffffff;
  border: 1px solid #1cf582;
  background: rgba(20, 171, 93, 0.18);
  box-sizing: border-box;

  &.orange {
    border-color: #ffb26d;
    background: rgba(211, 105, 35, 0.32);
  }
}

/* 右侧风险点内容 */
.detail-panel {
  width: 100%;
  height: 100%;
  padding: 16px;
  box-sizing: border-box;
  background: url('../img/bg_left_top01@2x.png') no-repeat center / 100% 100%;
  overflow-y: auto;

  &::-webkit-scrollbar {
    width: 4px;
  }
  &::-webkit-scrollbar-thumb {
    border-radius: 2px;
    background: rgba(13, 155, 255, 0.45);
  }
  &::-webkit-scrollbar-track {
    background: transparent;
  }
}

.section-panel {
  background: rgba(14, 55, 115, 0.4);
  border: 1px solid rgba(166, 206, 255, 0.1);
  margin-bottom: 20px;
}

.basic-info-panel {
  box-sizing: border-box;
  background: url('../img/bg_left_top01@2x.png') no-repeat center / 100% 100%, linear-gradient(90deg, rgba(27, 79, 151, 0.5) 0%, rgba(27, 79, 151, 0.08) 116.99%);
  border: none !important;
}

.video-info-panel {
  box-sizing: border-box;
  background: url('../img/bg_left_top01@2x.png') no-repeat center / 100% 100%, linear-gradient(90deg, rgba(27, 79, 151, 0.5) 0%, rgba(27, 79, 151, 0.08) 116.99%);
  border: none !important;
}

.section-title {
  height: 36px;
  line-height: 36px;
  padding-left: 16px;
  background: url('../img/liquid_device_title.png') no-repeat center / 100% 100%;
  position: relative;
}

.section-title-text {
  font-family: "Alibaba PuHuiTi 2.0";
  font-size: 14px;
  font-style: normal;
  font-weight: 500;
  line-height: 36px;
  background: linear-gradient(0deg, #5FBCFF 1.4%, #FFF 58.67%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  display: inline-block;
}

.section-content {
  padding: 12px 20px;
}

.location-title {
  display: inline-block;
  position: relative;
  color: #fff;
  font-size: 16px;
  margin-bottom: 12px;
  background: url('../img/waterlog_center_listbg.png') no-repeat center / 100% 100%;
  padding: 6px 60px 6px 14px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px 24px;
}

.info-item {
  display: flex;
  align-items: flex-start;
  font-size: 14px;
}

.info-item .label {
  color: #749dc0;
  min-width: 110px;
  flex-shrink: 0;
}

.info-item .value {
  color: #fff;
  flex: 1;
  word-break: break-all;
}

.contact-cards {
  display: flex;
  justify-content: space-between;
  margin: 16px 0;
}

.contact-card {
  width: 190px;
  height: 125px;
  box-sizing: border-box;
  border: 1px solid rgba(116, 157, 192, 0.5);
  background: rgba(24, 88, 163, 0.15);
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  align-items: center;
}

.card-title {
  margin: 0 auto 12px auto;
  background: url('../img/Rectangle-346242169.png') no-repeat center / 100% 100%;
  width: 140px;
  height: 32px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.title-text {
  text-align: center;
  font-family: "Alibaba PuHuiTi 2.0";
  font-size: 18px;
  font-style: normal;
  font-weight: bold;
  line-height: normal;
  background: linear-gradient(0deg, #5FBCFF 1.4%, #FFF 58.67%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.card-name {
  text-align: center;
  font-feature-settings: 'liga' off, 'clig' off;
  font-family: "Alibaba PuHuiTi 2.0";
  font-size: 16px;
  font-style: normal;
  font-weight: bold;
  line-height: normal;
  letter-spacing: 0.32px;
  margin-bottom: 10px;
  background: linear-gradient(0deg, #5FBCFF 1.4%, #FFF 58.67%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.card-phone {
  color: #12dd60;
  font-feature-settings: 'liga' off, 'clig' off;
  font-family: "Alibaba PuHuiTi 2.0";
  font-size: 16px;
  font-style: normal;
  font-weight: normal;
  line-height: normal;
  letter-spacing: 0.32px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.phone-icon {
  width: 14px;
  height: 14px;
  display: inline-block;
  background: url('../img/phone.png') no-repeat center / cover;
}

.video-container {
  width: 100%;
  height: 224px;
  background: #000;
  position: relative;
  border: 1px solid rgba(62, 160, 255, 0.3);
  cursor: pointer;
}

.video-placeholder {
  width: 100%;
  height: 100%;
  object-fit: cover;
  background: #000;
}

.video-empty {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #749dc0;
  font-size: 14px;
}

.video-overlay-time {
  position: absolute;
  right: 20px;
  top: 16px;
  z-index: 5;
  color: #fff;
  font-family: "Alibaba PuHuiTi 2.0", sans-serif;
  font-size: 14px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.8);
}

.video-overlay-watermark {
  position: absolute;
  right: 200px;
  top: 50%;
  z-index: 5;
  transform: rotate(-25deg);
  color: rgba(255, 255, 255, 0.25);
  font-family: "Alibaba PuHuiTi 2.0", sans-serif;
  font-size: 18px;
  font-weight: 600;
  line-height: 1.6;
  text-align: center;
  pointer-events: none;
}

.video-overlay-location {
  position: absolute;
  left: 20px;
  bottom: 20px;
  z-index: 5;
  color: #ffffff;
  font-family: "Alibaba PuHuiTi 2.0", sans-serif;
  font-size: 14px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.8);
}
</style>
