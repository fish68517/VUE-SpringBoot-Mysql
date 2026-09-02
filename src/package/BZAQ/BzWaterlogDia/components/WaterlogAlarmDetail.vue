<template>
  <div class="waterlog-alarm-detail">
    <div class="modal-header">
      <div class="header-title">积水告警事件详情</div>
      <button class="close-btn" @click="handleClose"></button>
    </div>

    <!-- tab导航 -->
    <div class="tab-nav">
      <div
        class="tab-item"
        :class="{ active: activeTab === 'event' }"
        @click="activeTab = 'event'"
      >
        事件信息
      </div>
      <div
        class="tab-item"
        :class="{ active: activeTab === 'workOrder' }"
        @click="activeTab = 'workOrder'"
      >
        工单信息
      </div>
    </div>

    <div class="modal-body">
      <!-- 工单信息tab（默认） -->
      <div v-if="activeTab === 'workOrder'" class="content-layout">
        <!-- 左侧：基础信息 + 视频监控 -->
        <div class="left-panel">
          <!-- 基础信息 -->
          <div class="section-panel basic-info-panel">
            <div class="section-title"><span class="section-title-text">基础信息</span></div>
            <div class="section-content">
              <div class="location-title">
                <span class="location-text">{{ basicInfoLocation }}</span>
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

          <!-- 视频监控 -->
          <div class="section-panel video-info-panel">
            <div class="section-title"><span class="section-title-text">视频监控</span></div>
            <div class="section-content">
              <div class="video-container" @click="openVideo">
                <div class="video-overlay-time">{{ d.createTime || '-' }}</div>
                <div class="video-overlay-watermark">重庆市住建委排水中心<br>宇视君 严禁泄密</div>
                <div class="video-overlay-location">{{ d.riskPoint || '-' }}</div>
                <video
                  class="video-placeholder"
                  controls
                  poster="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg=="
                >
                  <source src="#" type="video/mp4">
                  您的浏览器不支持视频播放
                </video>
              </div>
            </div>
          </div>

          <!-- 工单信息：指标 + 表格 -->
          <div class="section-panel work-order-panel">
            <div class="section-title"><span class="section-title-text">工单信息</span></div>
            <div class="section-content">
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
                      >
                        <td>{{ ri + 1 }}</td>
                        <td>{{ row.name }}</td>
                        <td>{{ row.phone }}</td>
                        <td>{{ row.state }}</td>
                        <td>{{ row.riskPoint }}</td>
                        <td>{{ row.county }}</td>
                        <td>{{ row.town }}</td>
                        <td>{{ row.community }}</td>
                        <td>{{ row.personnelNumber }}</td>
                        <td>{{ row.specialVehiclesNumber }}</td>
                        <td>{{ row.pumpingEquipmentQuantity }}</td>
                        <td>{{ row.extractionCapacity }}</td>
                        <td>{{ row.disposalMatter }}</td>
                        <td>{{ row.createTime }}</td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 右侧：事件处置时间轴 -->
        <div class="right-panel">
          <div class="timeline-header">
            <span class="timeline-title-text">事件处置</span>
          </div>
          <div class="timeline">
            <div
              v-for="(item, idx) in eventFlow"
              :key="idx"
              class="timeline-item"
            >
              <div class="dot"></div>
              <div class="event-title">{{ item.title }}</div>
              <div class="event-card">
                <div class="event-content">{{ item.content }}</div>
                <div class="event-time">{{ item.time }}</div>
                <div v-if="item.photo" class="event-photo-placeholder"></div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 事件信息tab -->
      <div v-if="activeTab === 'event'" class="event-content-layout">
        <!-- 左侧：事件单详情 + 处置详情 -->
        <div class="event-left-panel">
          <!-- 事件单内容详情 -->
          <div class="section-panel event-info-panel">
            <div class="section-title">
              <span class="section-title-text">事件单内容详情</span>
            </div>
            <div class="section-content">
              <div class="event-info-grid">
                <div class="event-info-item">
                  <span class="label">事件单标题：</span>
                  <span class="value">{{ staticEventInfo.title }}</span>
                </div>
                <div class="event-info-item">
                  <span class="label">点位名称：</span>
                  <span class="value">{{ staticEventInfo.pointName }}</span>
                </div>
                <div class="event-info-item">
                  <span class="label">现场联系人：</span>
                  <span class="value">{{ staticEventInfo.contactPerson }}</span>
                </div>
                <div class="event-info-item">
                  <span class="label">联系电话：</span>
                  <span class="value">{{ staticEventInfo.contactPhone }}</span>
                </div>
                <div class="event-info-item">
                  <span class="label">事发地址：</span>
                  <span class="value">{{ staticEventInfo.address }}</span>
                </div>
                <div class="event-info-item">
                  <span class="label">事发时间：</span>
                  <span class="value">{{ staticEventInfo.eventTime }}</span>
                </div>
                <div class="event-info-item">
                  <span class="label">事件单编号：</span>
                  <span class="value">{{ staticEventInfo.eventNo }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 事件单处置详情 -->
          <div class="section-panel event-disposal-panel">
            <div class="section-title">
              <span class="section-title-text">事件单处置详情</span>
            </div>
            <div class="section-content">
              <div class="disposal-levels">
                <!-- 市级 -->
                <div class="disposal-level">
                  <div class="level-tag">市级</div>
                  <div class="level-cards">
                    <div class="disposal-card" v-for="card in staticDisposalInfo.city" :key="card.unit">
                      <div class="card-unit">{{ card.unit }}</div>
                      <div class="card-content">{{ card.content }}</div>
                      <div class="card-time">{{ card.time }}</div>
                    </div>
                  </div>
                </div>

                <!-- 区级 -->
                <div class="disposal-level">
                  <div class="level-tag">区级</div>
                  <div class="level-cards">
                    <div class="disposal-card" v-for="card in staticDisposalInfo.district" :key="card.unit">
                      <div class="card-unit">{{ card.unit }}</div>
                      <div class="card-content">{{ card.content }}</div>
                      <div class="card-time">{{ card.time }}</div>
                    </div>
                  </div>
                </div>

                <!-- 镇街 -->
                <div class="disposal-level">
                  <div class="level-tag">镇街</div>
                  <div class="level-cards">
                    <div class="disposal-card" v-for="card in staticDisposalInfo.street" :key="card.unit">
                      <div class="card-unit">{{ card.unit }}</div>
                      <div class="card-content">{{ card.content }}</div>
                      <div class="card-time">{{ card.time }}</div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 右侧：贯通流程时间轴 -->
        <div class="event-right-panel">
          <div class="timeline-header">
            <span class="timeline-title-text">贯通流程</span>
          </div>
          <div class="timeline">
            <div
              v-for="(item, idx) in staticFlowInfo"
              :key="idx"
              class="timeline-item"
            >
              <div class="dot"></div>
              <div class="event-card">
                <div class="event-node">{{ item.node }}</div>
                <div class="event-department">{{ item.department }}</div>
                <div class="event-time">{{ item.time }}</div>
              </div>
            </div>
          </div>
        </div>
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

const sourceName = 'WaterlogAlarmDetail'
const WATERLOG_BASE_URL = 'http://23.99.16.179:11001/api/boot/system/waterlog'

// tab切换，默认事件信息
const activeTab = ref('event')

// 静态事件单内容详情数据
const staticEventInfo = {
  title: '渝中区化龙桥街道红岩村天桥下十字路口',
  pointName: '渝中区化龙桥街道红岩村天桥下十字路口',
  contactPerson: '--',
  contactPhone: '--',
  address: '重庆市渝中区化龙桥街道瑞天路61号附1号',
  eventTime: '--',
  eventNo: 'SSSNLZ20260728182323041450'
}

// 静态事件单处置详情数据
const staticDisposalInfo = {
  city: [],
  district: [
    {
      unit: '渝中区住房城市建委',
      content: '积水告警事件在渝中区化龙桥街道渝中区化龙桥街道红岩村天桥下...',
      time: '2026-07-28 18:24:08'
    },
    {
      unit: '渝中区城管局',
      content: '积水告警事件在渝中区化龙桥街道渝中区化龙桥街道红岩村天桥下...',
      time: '2026-07-28 18:24:08'
    },
    {
      unit: '渝中区公安分局',
      content: '积水告警事件在渝中区化龙桥街道渝中区化龙桥街道红岩村天桥下...',
      time: '2026-07-28 18:24:08'
    }
  ],
  street: [
    {
      unit: '渝中区化龙桥街道',
      content: '积水告警事件在渝中区化龙桥街道渝中区化龙桥街道红岩村天桥下...',
      time: '2026-07-28 18:24:08'
    }
  ]
}

// 静态贯通流程时间轴数据
const staticFlowInfo = [
  {
    node: '',
    department: '重庆市数字化城市运行和治理中心',
    time: '2026-07-28 18:23:39'
  },
  {
    node: '执行节点：业务事项启动',
    department: '渝中区数字化城市运行和治理中心',
    time: '2026-07-28 18:23:40'
  },
  {
    node: '执行节点：业务事项启动',
    department: '渝中区城管局',
    time: '2026-07-28 18:23:41'
  },
  {
    node: '执行节点：业务事项启动',
    department: '化龙桥街道',
    time: '2026-07-28 18:23:41'
  },
  {
    node: '执行节点：业务事项启动',
    department: '重庆市公安局交通警察总队',
    time: '2026-07-28 18:23:41'
  },
  {
    node: '执行节点：业务事项启动',
    department: '渝中区公安分局',
    time: '2026-07-28 18:23:41'
  },
  {
    node: '执行节点：业务事项启动',
    department: '渝中区住房城市建委',
    time: '2026-07-28 18:23:41'
  }
]

const eventBus = {
  emit(event, data) {
    if (!props.bus) return
    props.bus.emit(event, { data, source: sourceName })
  }
}

const d = computed(() => props.rowData || {})

// 接口返回的积水点位详情数据
const pointDetail = ref(null)

// 调用接口获取积水点位信息
const fetchPointDetail = async () => {
  const pointId = d.value?.riskPointId
  if (!pointId) return
  try {
    const res = await axios.get(`${WATERLOG_BASE_URL}/disposition/point`, {
      params: { pointId }
    })
    const data = res?.data?.data
    if (data) {
      pointDetail.value = data
    }
  } catch (error) {
    console.error('获取积水点位详情失败:', error)
  }
}

onMounted(() => {
  fetchPointDetail()
  fetchWorkOrderDisponse()
})

const basicInfoLocation = computed(() => {
  const p = pointDetail.value || {}
  return p.pointName || d.value.riskPoint || '-'
})

const basicInfo = computed(() => {
  const p = pointDetail.value || {}
  const r = d.value
  return [
    { label: '点位名称', value: p.pointName || r.riskPoint || '-' },
    { label: '所属区县', value: p.county || r.county || '-' },
    { label: '降雨阈值', value: p.threshold || (r.warningThreshold ? `${r.warningThreshold}mm/2h` : '-') || '-' },
    { label: '告警时间', value: p.waterRetentionTime || r.createTime || '-' },
    { label: '告警来源', value: p.waterRetentionFrom || r.warningSource || '-' },
    { label: '降雨量', value: p.rainNum || (r.rainfall ? `${r.rainfall}mm` : '-') || '-' },
    { label: '积水深度', value: p.accumulatedWaterDepth || (r.liquidLevel ? `${r.liquidLevel}cm` : '-') || '-' },
    { label: '积水面积', value: p.accumulatedWaterArea || (r.waterArea ? `${r.waterArea}m²` : '-') || '-' },
    { label: '是否断交', value: p.ifDisconnection || r.isTrafficControl || '-' }
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
  const p = pointDetail.value || {}
  const cspsflaq = parseResponsible(p.cspsflaqResponsible)
  const csnlzl = parseResponsible(p.csnlzlResponsible)
  const sdzd = parseResponsible(p.sdzdResponsible)
  const ghdw = parseResponsible(p.ghdwResponsible)
  return [
    { title: '市级负责人', name: cspsflaq.name, phone: cspsflaq.phone },
    { title: '区级负责人', name: csnlzl.name, phone: csnlzl.phone },
    { title: '镇街负责人', name: sdzd.name, phone: sdzd.phone },
    { title: '管护责任人', name: ghdw.name, phone: ghdw.phone }
  ]
})

const eventFlow = [
  { title: '创建工单', content: '沙坪坝陈家桥街道民德路', time: '2026-05-03 06:01:23' },
  { title: '审核通过', content: '李雪健', time: '2026-05-03 06:05:12' },
  { title: '创建工单', content: '沙坪坝区，西部应急小组1', time: '2026-05-03 06:10:45' },
  { title: '确认到场', content: '沙坪坝陈家桥街道民德路', time: '2026-05-03 06:30:18', photo: true }
]

// 点击查看摄像头 — 通过事件总线通知主组件，由主组件匹配 riskPointId 后打开视频
function openVideo() {
  const riskPointId = d.value?.riskPointId
  if (!riskPointId) {
    console.warn('当前行数据缺少 riskPointId，无法查看摄像头')
    return
  }
  eventBus.emit('alarmDetail:openVideo', { riskPointId })
}

// 工单信息 - 五个指标
const workOrderMetrics = ref([
  { title: '应急抢险作业人员参与巡查处置', value: '0', unit: '人次' },
  { title: '特种车辆', value: '0', unit: '车次' },
  { title: '排涝设备', value: '0', unit: '台次' },
  { title: '抽排能力', value: '0', unit: 'm³/h' }
])

// 工单信息 - 表格列头
const workOrderColumns = [
  '序号',
  '处置人员',
  '联系电话',
  '状态',
  '风险点位',
  '所属区县',
  '所属街道',
  '所属社区',
  '人员数量',
  '特种车辆',
  '抽水设备',
  '抽水能力',
  '处置事项',
  '创建时间'
]

// 工单信息 - 表格数据
const workOrderList = ref([])

// 获取处置工单信息（### /work/order/disponse，入参 round）
const fetchWorkOrderDisponse = async () => {
  try {
    const params = {}
    if (props.round != null && props.round !== '') {
      params.round = props.round
    }
    const res = await axios.get(`${WATERLOG_BASE_URL}/work/order/disponse`, { params })
    const list = res?.data?.data || []
    // 汇总指标
    let personnelNumber = 0
    let specialVehiclesNumber = 0
    let pumpingEquipmentQuantity = 0
    let extractionCapacity = 0
    list.forEach(item => {
      personnelNumber += Number(item.personnelNumber) || 0
      specialVehiclesNumber += Number(item.specialVehiclesNumber) || 0
      pumpingEquipmentQuantity += Number(item.pumpingEquipmentQuantity) || 0
      extractionCapacity += Number(item.extractionCapacity) || 0
    })
    workOrderMetrics.value = [
      { title: '应急抢险作业人员参与巡查处置', value: String(personnelNumber), unit: '人次' },
      { title: '特种车辆', value: String(specialVehiclesNumber), unit: '车次' },
      { title: '排涝设备', value: String(pumpingEquipmentQuantity), unit: '台次' },
      { title: '抽排能力', value: String(extractionCapacity), unit: 'm³/h' }
    ]
    // 填充工单表格
    workOrderList.value = list.map((item) => ({
      name: item.name || '-',
      phone: item.phone || '-',
      state: item.state || '-',
      riskPoint: item.riskPoint || '-',
      county: item.county || '-',
      town: item.town || '-',
      community: item.community || '-',
      personnelNumber: String(item.personnelNumber ?? '-'),
      specialVehiclesNumber: String(item.specialVehiclesNumber ?? '-'),
      pumpingEquipmentQuantity: String(item.pumpingEquipmentQuantity ?? '-'),
      extractionCapacity: String(item.extractionCapacity ?? '-'),
      disposalMatter: item.disposalMatter || '-',
      createTime: item.createTime || '-'
    }))
  } catch (error) {
    console.error('获取处置工单信息失败:', error)
  }
}

function handleClose() {
  emit('close')
}
</script>

<script>
export default {
  name: 'WaterlogAlarmDetail'
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

.waterlog-alarm-detail {
  font-family: 'Alibaba PuHuiTi 2.0', sans-serif;
  position: absolute;
  left: 50%;
  top: 50%;
  z-index: 1001;
  width: 1300px;
  height: 880px;
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
  width: calc(100% - 8px);
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
  font-weight: 500;
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
  height: calc(100% - 96px);
  padding: 20px 24px;
  box-sizing: border-box;
  overflow: hidden;
}

/* tab导航样式 */
.tab-nav {
  height: 48px;
  padding: 0 24px;
  display: flex;
  align-items: center;
  gap: 24px;
  border-bottom: 1px solid rgba(62, 160, 255, 0.15);
}

.tab-item {
  position: relative;
  height: 32px;
  line-height: 32px;
  padding: 0 24px;
  font-size: 16px;
  font-weight: 500;
  color: #749dc0;
  cursor: pointer;
  transition: all 0.2s ease;
}

.tab-item.active {
  color: #5FBCFF;
  background: rgba(95, 188, 255, 0.1);
  border-radius: 4px;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: -8px;
  left: 50%;
  transform: translateX(-50%);
  width: 32px;
  height: 3px;
  background: linear-gradient(90deg, #5FBCFF, #FFF);
  border-radius: 2px;
}

.content-layout {
  display: flex;
  gap: 20px;
  height: 100%;
}

/* 左侧面板 */
.left-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
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
  min-width: 96px;
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
}

.video-placeholder {
  width: 100%;
  height: 100%;
  object-fit: cover;
  background: #000;
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

/* 右侧时间轴面板 */
.right-panel {
  width: 400px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  background: url('../img/bg_left_top01@2x.png') no-repeat center / 100% 100%;
  overflow: hidden;
}

.timeline-header {
  height: 36px;
  line-height: 36px;
  padding-left: 16px;
  background: url('../img/liquid_device_title.png') no-repeat center / 100% 100%;
  flex-shrink: 0;
}

.timeline-title-text {
  font-family: "Alibaba PuHuiTi 2.0";
  font-size: 14px;
  font-style: normal;
  font-weight: 500;
  line-height: 36px;
  background: linear-gradient(0deg, #5FBCFF 1.4%, #FFF 58.67%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.timeline {
  position: relative;
  flex: 1;
  padding: 16px 15px 20px 42px;
  box-sizing: border-box;
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

  &::before {
    content: '';
    position: absolute;
    left: 25px;
    top: 18px;
    bottom: 0;
    width: 4px;
    background: url('../img/waterlog_right_line.png') repeat-y center;
    background-size: 100% 100%;
  }
}

.timeline-item {
  position: relative;
  margin-bottom: 15px;
}

.dot {
  position: absolute;
  left: -23px;
  top: 2px;
  width: 16px;
  height: 16px;
  background: url('../img/waterlog_right_icon.png') no-repeat center;
  background-size: 100% 100%;
}

.event-title {
  margin-bottom: 10px;
  color: #ffffff;
  font-size: 15px;
  font-weight: 700;
}

.event-card {
  min-height: 82px;
  padding: 16px 16px 14px;
  box-sizing: border-box;
  background: url('../img/waterlog_right_cardbg.png') no-repeat center;
  background-size: 100% 100%;
  border: none;
}

.event-content {
  color: #ffffff;
  font-family: "Alibaba PuHuiTi 2.0", sans-serif;
  font-size: 20px;
  font-style: normal;
  font-weight: 500;
  line-height: normal;
}

.event-time {
  align-self: stretch;
  margin-top: 7px;
  color: #749dc0;
  font-family: "Alibaba PuHuiTi 2.0", sans-serif;
  font-size: 14px;
  font-style: normal;
  font-weight: 500;
  line-height: 20px;
}

.event-photo-placeholder {
  margin-top: 12px;
  width: 240px;
  height: 252px;
  background:
    linear-gradient(0deg, rgba(26, 36, 44, 0.75), rgba(26, 36, 44, 0.05)),
    repeating-linear-gradient(90deg, rgba(255, 255, 255, 0.12) 0 1px, transparent 1px 34px),
    linear-gradient(135deg, #8aa2b0, #1c2c35 58%, #101a21);
  border-top: 3px solid #33d846;
}

/* 事件信息tab样式 */
.event-content-layout {
  display: flex;
  gap: 20px;
  height: 100%;
}

.event-left-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
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

.event-info-panel, .event-disposal-panel {
  box-sizing: border-box;
  background: url('../img/bg_left_top01@2x.png') no-repeat center / 100% 100%, linear-gradient(90deg, rgba(27, 79, 151, 0.5) 0%, rgba(27, 79, 151, 0.08) 116.99%);
  border: none !important;
}

.event-info-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px 24px;
}

.event-info-item {
  display: flex;
  align-items: flex-start;
  font-size: 14px;
}

.event-info-item .label {
  color: #749dc0;
  min-width: 96px;
  flex-shrink: 0;
}

.event-info-item .value {
  color: #fff;
  flex: 1;
  word-break: break-all;
}

.disposal-levels {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.disposal-level {
  display: flex;
  gap: 16px;
  align-items: flex-start;
}

.level-tag {
  flex-shrink: 0;
  width: 48px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(180deg, rgba(95, 188, 255, 0.2) 0%, rgba(95, 188, 255, 0.05) 100%);
  border: 1px solid rgba(95, 188, 255, 0.3);
  border-radius: 4px;
  font-size: 18px;
  font-weight: 700;
  color: #5FBCFF;
  writing-mode: vertical-rl;
  letter-spacing: 4px;
}

.level-cards {
  flex: 1;
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.disposal-card {
  width: 320px;
  box-sizing: border-box;
  padding: 12px 16px;
  background: rgba(24, 88, 163, 0.15);
  border: 1px solid rgba(116, 157, 192, 0.5);
  border-radius: 4px;
}

.card-unit {
  font-size: 15px;
  font-weight: 700;
  color: #fff;
  margin-bottom: 8px;
}

.card-content {
  font-size: 13px;
  color: #d8ecff;
  line-height: 1.5;
  margin-bottom: 8px;
}

.card-time {
  font-size: 12px;
  color: #749dc0;
}

.event-right-panel {
  width: 400px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  background: url('../img/bg_left_top01@2x.png') no-repeat center / 100% 100%;
  overflow: hidden;
}

.event-node {
  color: #5FBCFF;
  font-size: 13px;
  margin-bottom: 4px;
}

.event-department {
  color: #ffffff;
  font-size: 14px;
  font-weight: 700;
  margin-bottom: 4px;
}

/* 工单信息样式 */
.work-order-panel {
  box-sizing: border-box;
  background: url('../img/bg_left_top01@2x.png') no-repeat center / 100% 100%, linear-gradient(90deg, rgba(27, 79, 151, 0.5) 0%, rgba(27, 79, 151, 0.08) 116.99%);
  border: none !important;
}

.work-order-metrics {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 8px;
  margin-bottom: 12px;
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

.work-order-table-wrap {
  overflow: hidden;
  border: 1px solid rgba(95, 188, 255, 0.3);
  display: flex;
  flex-direction: column;
  max-height: 300px;
}

.work-order-table-scroll {
  flex: 1;
  overflow-x: auto;
  overflow-y: auto;
  scrollbar-width: thin;
  scrollbar-color: rgba(95, 188, 255, 0.6) rgba(8, 53, 108, 0.3);

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
  }
}

.work-order-table {
  width: 100%;
  min-width: 1200px;
  border-collapse: collapse;
  font-size: 13px;
  table-layout: auto;

  th, td {
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
</style>
