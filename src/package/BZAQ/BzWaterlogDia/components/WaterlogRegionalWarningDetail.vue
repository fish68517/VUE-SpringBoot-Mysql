<template>
  <div class="regional-warning-detail">
    <div class="modal-header">
      <div class="header-title">{{ title }}</div>
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
                v-for="item in regionalWarningInfo"
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

        <!-- 右侧：响应情况（仅事件信息时显示） -->
        <main v-if="activeInfoTab === 'event'" class="detail-panel">
          <div class="response-section">
            <!-- 已响应 -->
            <div class="response-block">
              <div class="response-title">已响应</div>
              <div class="response-tables">
                <div class="table-group">
                  <div class="table-group-title">市级</div>
                  <div class="data-table">
                    <div class="table-row header">
                      <div class="table-cell">单位名称</div>
                      <div class="table-cell">预警状态</div>
                      <div class="table-cell">预警内容</div>
                    </div>
                    <div
                      v-for="(row, idx) in respondedCityList"
                      :key="idx"
                      class="table-row"
                    >
                      <div class="table-cell" :title="row.name">{{ row.name }}</div>
                      <div class="table-cell" :title="row.status">{{ row.status }}</div>
                      <div class="table-cell" :title="row.content">{{ row.content }}</div>
                    </div>
                  </div>
                </div>
                <div class="table-group">
                  <div class="table-group-title">区级</div>
                  <div class="data-table">
                    <div class="table-row header">
                      <div class="table-cell">单位名称</div>
                      <div class="table-cell">预警状态</div>
                      <div class="table-cell">预警内容</div>
                    </div>
                    <div
                      v-for="(row, idx) in respondedRegionList"
                      :key="idx"
                      class="table-row"
                    >
                      <div class="table-cell" :title="row.name">{{ row.name }}</div>
                      <div class="table-cell" :title="row.status">{{ row.status }}</div>
                      <div class="table-cell" :title="row.content">{{ row.content }}</div>
                    </div>
                  </div>
                </div>
                <div class="table-group">
                  <div class="table-group-title">镇街</div>
                  <div class="data-table">
                    <div class="table-row header">
                      <div class="table-cell">单位名称</div>
                      <div class="table-cell">预警状态</div>
                      <div class="table-cell">预警内容</div>
                    </div>
                    <div
                      v-for="(row, idx) in respondedTownList"
                      :key="idx"
                      class="table-row"
                    >
                      <div class="table-cell" :title="row.name">{{ row.name }}</div>
                      <div class="table-cell" :title="row.status">{{ row.status }}</div>
                      <div class="table-cell" :title="row.content">{{ row.content }}</div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- 未响应 -->
            <div class="response-block">
              <div class="response-title">未响应</div>
              <div class="response-tables">
                <div class="table-group">
                  <div class="table-group-title">市级</div>
                  <div class="data-table">
                    <div class="table-row header">
                      <div class="table-cell">单位名称</div>
                      <div class="table-cell">预警状态</div>
                    </div>
                    <div
                      v-for="(row, idx) in unrespondedCityList"
                      :key="idx"
                      class="table-row"
                    >
                      <div class="table-cell" :title="row.name">{{ row.name }}</div>
                      <div class="table-cell" :title="row.status">{{ row.status }}</div>
                    </div>
                  </div>
                </div>
                <div class="table-group">
                  <div class="table-group-title">区级</div>
                  <div class="data-table">
                    <div class="table-row header">
                      <div class="table-cell">单位名称</div>
                      <div class="table-cell">预警状态</div>
                    </div>
                    <div
                      v-for="(row, idx) in unrespondedRegionList"
                      :key="idx"
                      class="table-row"
                    >
                      <div class="table-cell" :title="row.name">{{ row.name }}</div>
                      <div class="table-cell" :title="row.status">{{ row.status }}</div>
                    </div>
                  </div>
                </div>
                <div class="table-group">
                  <div class="table-group-title">镇街</div>
                  <div class="data-table">
                    <div class="table-row header">
                      <div class="table-cell">单位名称</div>
                      <div class="table-cell">预警状态</div>
                    </div>
                    <div
                      v-for="(row, idx) in unrespondedTownList"
                      :key="idx"
                      class="table-row"
                    >
                      <div class="table-cell" :title="row.name">{{ row.name }}</div>
                      <div class="table-cell" :title="row.status">{{ row.status }}</div>
                    </div>
                  </div>
                </div>
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

const WATERLOG_BASE_URL = 'http://23.99.16.179:11001/api/boot/system/waterlog'

const props = defineProps({
  title: {
    type: String,
    default: '区域预警详情'
  },
  activeTabLabel: {
    type: String,
    default: '预警信息'
  },
  defaultTab: {
    type: String,
    default: '三级贯通'
  },
  rowData: {
    type: Object,
    default: null
  },
  bus: {
    type: Object,
    default: null
  },
  qs: {
    type: [String, Number],
    default: null
  },
  round: {
    type: [String, Number],
    default: null
  }
})

const emit = defineEmits(['close'])

const sourceName = 'WaterlogRegionalWarningDetail'
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

// 三级贯通按钮
const threeLevelLoading = ref(false)
async function handleThreeLevel() {
  const d = props.rowData || currentWarning.value
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

// 预警等级映射
const warningLevelMap = { '1': '一级', '2': '二级', '3': '三级', '4': '四级' }

// 预警详情数据（### 14 /warning/detail）
const warningDetailData = ref({})

// 三级贯通数据（### 15 /three/response）
const threeResponseData = ref({})

function formatPeriod(start, end) {
  if (!start && !end) return '-'
  return `${start || ''} - ${end || ''}`
}

// 工单信息：基于 ### 14 /warning/detail
const regionalWarningInfo = computed(() => {
  const d = warningDetailData.value
  if (!d || !Object.keys(d).length) return []
  const level = warningLevelMap[d.cyWaringLevel] || d.cyWaringLevel || '-'
  return [
    { label: '期数', value: d.qs ? `第${d.qs}期` : '-', compact: true },
    { label: '名称', value: d.mc || '-', compact: true },
    { label: '预警时段', value: formatPeriod(d.sdStart, d.sdEnd), nowrap: true, compact: true },
    { label: '时间', value: d.time || '-', compact: true },
    { label: '叫应状态', value: d.cyResState || '-', compact: true },
    { label: '叫应详情', value: d.cyWaringDetail || '-', compact: true },
    { label: '叫应等级', value: level, badge: true, orange: d.cyWaringLevel === '1' || d.cyWaringLevel === '2', compact: true },
    { label: '事件编号', value: d.eventNum || '-', compact: true },
    { label: '文件名称', value: d.filename || '-', long: true }
  ]
})

// 获取预警详情（### 14 /warning/detail）
const fetchWarningDetail = async () => {
  try {
    const params = {}
    if (props.qs != null && props.qs !== '') {
      params.qs = props.qs
    }
    const res = await axios.get(`${WATERLOG_BASE_URL}/warning/detail`, { params })
    const list = res?.data?.data || []
    if (Array.isArray(list) && list.length) {
      warningDetailData.value = list[0]
      return list[0]?.id || null
    }
    return null
  } catch (error) {
    console.error('获取预警详情失败:', error)
    return null
  }
}

// 获取三级贯通（### 15 /three/response）
const fetchThreeResponse = async (qs) => {
  try {
    const params = {}
    if (qs) {
      params.qs = qs
    }
    const res = await axios.get(`${WATERLOG_BASE_URL}/three/response`, { params })
    const data = res?.data?.data || {}
    threeResponseData.value = data
  } catch (error) {
    console.error('获取三级贯通失败:', error)
  }
}

// 三级贯通响应列表
// data key: "1"=市级, "2"=区县, "3"=镇街
// 接口返回数据均为已响应，未响应置空
const mapThreeLevel = (key, responded) => {
  if (!responded) return []
  const list = threeResponseData.value?.[key] || []
  return list.map(d => ({
    name: d.unitName || '-',
    status: '已响应',
    content: d.responseContent || d.dealContent || '-'
  }))
}

const respondedCityList = computed(() => mapThreeLevel('1', true))
const respondedRegionList = computed(() => mapThreeLevel('2', true))
const respondedTownList = computed(() => mapThreeLevel('3', true))
const unrespondedCityList = computed(() => mapThreeLevel('1', false))
const unrespondedRegionList = computed(() => mapThreeLevel('2', false))
const unrespondedTownList = computed(() => mapThreeLevel('3', false))

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

// 获取工单信息（### /work/order/area，入参 round）
const fetchWorkOrderArea = async () => {
  try {
    const params = {}
    if (props.round != null && props.round !== '') {
      params.round = props.round
    }
    const res = await axios.get(`${WATERLOG_BASE_URL}/work/order/area`, { params })
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
    workOrderList.value = list.map((item, index) => ({
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
    console.error('获取工单信息失败:', error)
  }
}

function handleClose() {
  emit('close')
}

function openWorkOrderDetail(row) {
  eventBus.emit('OPEN_WORK_ORDER_DETAIL', row)
}

onMounted(async () => {
  await fetchWarningDetail()
  await fetchThreeResponse(props.qs)
  await fetchWorkOrderArea()
})
</script>

<script>
export default {
  name: 'WaterlogRegionalWarningDetail'
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

.regional-warning-detail {
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

// 左侧工单信息
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

// 右侧响应情况
.detail-panel {
  width: 100%;
  height: 100%;
  padding: 16px;
  box-sizing: border-box;
  background: url('../img/bg_left_top01@2x.png') no-repeat center / 100% 100%;
  overflow: hidden;
}

.response-section {
  display: flex;
  gap: 16px;
  height: 100%;
}

.response-block {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.response-title {
  font-size: 16px;
  font-weight: 600;
  color: #ffffff;
  background: url('../img/waterlog_tab.png') no-repeat center;
  background-size: 100% 100%;
  padding: 4px 24px;
  margin-bottom: 12px;
  align-self: flex-start;
}

.response-tables {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
  overflow: hidden;
}

.table-group {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.table-group-title {
  font-size: 14px;
  font-weight: 500;
  color: #87e8ff;
  margin-bottom: 6px;
  padding-left: 8px;
  border-left: 3px solid #5FBCFF;
}

.data-table {
  flex: 1;
  overflow-y: auto;
  border: 1px solid rgba(95, 188, 255, 0.3);
  scrollbar-width: none;
  -ms-overflow-style: none;

  &::-webkit-scrollbar {
    display: none;
  }

  .table-row {
    display: flex;
    height: 36px;

    .table-cell {
      flex: 1;
      min-width: 0;
      border-right: 1px solid rgba(95, 188, 255, 0.3);
      font-size: 13px;
      color: #e0f0ff;
      padding: 0 4px;
      box-sizing: border-box;
      text-align: center;
      line-height: 36px;
      overflow: hidden;
      white-space: nowrap;
      text-overflow: ellipsis;

      &:last-child {
        border-right: none;
      }
    }

    &.header {
      background: #08356C;

      .table-cell {
        color: #87e8ff;
        font-size: 12px;
        font-weight: 500;
      }
    }

    &:not(.header) {
      background: #0A2D5D;
    }

    &:nth-child(even):not(.header) {
      background: #0A3268;
    }
  }
}
</style>
