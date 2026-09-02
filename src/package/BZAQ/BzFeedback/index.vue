<template>
  <div class="bz-feedback" :style="containerStyle">
    <!-- 状态1：隐患发现 -->
    <template v-if="status === 1">
      <div class="feedback-header">
        <div class="feedback-title">
          <span class="title-arrow">▶</span>
          <span class="title-text">隐患发现</span>
        </div>
        <div class="feedback-status">
          <span class="status-text">处置中</span>
        </div>
      </div>

      <div class="feedback-event">
        <div class="event-title event-title--s1">{{ eventInfoS1.title }}</div>
      </div>

      <div class="info-section">
        <div class="info-title">事件信息</div>
        <div class="info-grid">
          <div class="info-item">
            <span class="info-label">事件编号</span>
            <span class="info-divider">：</span>
            <span class="info-value">{{ eventInfoS1.id }}</span>
          </div>
          <div class="info-item info-item--right">
            <span class="info-label">风险等级</span>
            <span class="info-divider">：</span>
            <span class="info-value info-value--warning">{{ eventInfoS1.needLevel1 }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">发生时间</span>
            <span class="info-divider">：</span>
            <span class="info-value">{{ eventInfoS1.time }}</span>
          </div>
          <div class="info-item info-item--right">
            <span class="info-label">所属社区</span>
            <span class="info-divider">：</span>
            <span class="info-value">{{ eventInfoS1.community }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">所属街道</span>
            <span class="info-divider">：</span>
            <span class="info-value">{{ eventInfoS1.street }}</span>
          </div>
          <div class="info-item info-item--right">
            <span class="info-label">联系电话</span>
            <span class="info-divider">：</span>
            <span class="info-value">{{ eventInfoS1.phone }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">网格员名</span>
            <span class="info-divider">：</span>
            <span class="info-value">{{ eventInfoS1.gridPerson }}</span>
          </div>
          <div class="info-item info-item--right">
            <span class="info-label">风险类型</span>
            <span class="info-divider">：</span>
            <span class="info-value">{{ eventInfoS1.riskType }}</span>
          </div>
          <div class="info-item info-item--full">
            <span class="info-label">经纬度</span>
            <span class="info-divider">：</span>
            <span class="info-value">{{ eventInfoS1.lnglat }}</span>
          </div>
          <div class="info-item info-item--full">
            <span class="info-label">地址描述</span>
            <span class="info-divider">：</span>
            <span class="info-value">{{ eventInfoS1.address }}</span>
          </div>
        </div>
      </div>

      <div class="photo-section">
        <div class="info-title">现场照片</div>
        <div class="photo-big">
          <div class="photo-big__placeholder" :style="{ backgroundImage: `url(${eventInfoS1.photoImgs ? eventInfoS1.photoImgs : photoBg})` }">
            <span class="placeholder-icon">▶</span>
          </div>
        </div>
      </div>

      <div class="report-section">
        <div class="info-title">上报内容</div>
        <div class="report-body">
          <div class="report-ai"><span class="ai-tag">AI分析</span> {{ eventInfoS1.reportContent }}</div>
          <div class="report-row">
            <span class="report-label">是否需要一键调度</span>
            <span class="report-value">{{ eventInfoS1.dispatchSuggestion }}</span>
          </div>
          <div class="report-row">
            <span class="report-label">风险等级</span>
            <span class="report-value info-value--warning">{{ eventInfoS1.needLevel1 }}</span>
          </div>
          <div class="report-row">
            <span class="report-label">处置建议</span>
            <span class="report-value">{{ eventInfoS1.suggestion }}</span>
          </div>
        </div>
      </div>

      <div class="dispatch-btn" @click="handleDispatchClick">
        <span>一键调度</span>
      </div>
    </template>

    <!-- 状态2：流程推送 -->
    <template v-else-if="status === 2">
      <div class="feedback-header">
        <div class="feedback-title">
          <span class="title-arrow">▶</span>
          <span class="title-text">流程推送</span>
        </div>
        <div class="feedback-status">
          <span class="status-text">处置中</span>
        </div>
      </div>

      <div class="feedback-event">
        <div class="event-title event-title--s2">{{ eventInfoS2.title }}</div>
        <div class="event-id">事件编号：{{ eventInfoS2.id }}</div>
      </div>

      <div class="dept-section">
        <div class="info-title">处置部门进度</div>
        <div class="progress-bar">
          <div class="progress-bar__fill" :style="{ width: progressPercent + '%' }"></div>
          <div class="progress-bar__label">{{ respondedCount }}/{{ deptList.length }} 部门已响应</div>
        </div>
        <div class="dept-list">
          <div 
            class="dept-item" 
            v-for="(dept, index) in deptList" 
            :key="index"
          >
            <div class="dept-item__dot" :class="{ 'dot-done': dept.done, 'dot-pending': !dept.done }"></div>
            <div class="dept-item__info">
              <div class="dept-item__name">{{ dept.name }}</div>
              <div class="dept-item__time" v-if="dept.done">{{ dept.time }}</div>
              <div class="dept-item__time dept-item__time--pending" v-else>待响应</div>
            </div>
            <div class="dept-item__status" :class="{ 'status-done': dept.done, 'status-pending': !dept.done }">
              {{ dept.done ? dept.statusText : '待响应' }}
            </div>
          </div>
        </div>
      </div>

      <div class="dispatch-btn" @click="handleDispatchClick">
        <span>一键调度</span>
      </div>
    </template>

    <!-- 状态3：处置反馈（原有内容） -->
    <template v-else-if="status === 3">
      <div class="feedback-header">
        <div class="feedback-title">
          <span class="title-arrow">▶</span>
          <span class="title-text">处置反馈</span>
        </div>
        <div class="feedback-status">
          <span class="status-text">处置中</span>
        </div>
      </div>

      <div class="feedback-event">
        <div class="event-title">{{ eventInfo.title }}</div>
        <div class="event-id">事件编号：{{ eventInfo.id }}</div>
      </div>

      <div class="feedback-timeline">
        <div 
          class="timeline-item" 
          v-for="(item, index) in feedbackList" 
          :key="index"
        >
          <div class="timeline-content">
            <div class="timeline-header">
              <span class="timeline-title">{{ item.title }}</span>
              <span class="timeline-time">{{ item.time }}</span>
            </div>
            <div class="timeline-desc">{{ item.desc }}</div>
          </div>
        </div>
      </div>
      <div class="feedback-photos">
        <div class="photos-title">
          <span>反馈照片</span>
        </div>
        <div class="photos-list">
          <div 
            class="photos-item" 
            v-for="(photo, index) in photoList" 
            :key="index"
          >
            <div class="photo-placeholder">
              <span class="placeholder-icon">🖼</span>
            </div>
          </div>
        </div>
      </div>

      <div class="feedback-attachment" v-if="eventInfo.attachment">
        <div class="photos-title">
          <span>反馈附件</span>
        </div>
        <div class="attachment-list">
          <div class="attachment-item" @click="openPdfPreview(eventInfo.attachment)">
            <div class="attachment-icon">
              <span class="icon-file">📄</span>
            </div>
            <div class="attachment-info">
              <div class="attachment-name">{{ eventInfo.attachment.name }}</div>
              <div class="attachment-size">{{ eventInfo.attachment.size }}</div>
            </div>
            <div class="attachment-action">
              <span class="icon-download">预览</span>
            </div>
          </div>
        </div>
      </div>
    </template>

  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'

const photoBg = '/imgs/process/process1.png'

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

const sourceName = 'BzFeedback'

const DISPATCH_EVENT = 'BZ_RISK_CLICK'

const currentRiskData = ref<any>(null)

const status = ref(1)

const containerStyle = computed(() => {
  const width = Number(props.chartConfig?.attr?.w)
  const height = Number(props.chartConfig?.attr?.h)
  return {
    width: Number.isFinite(width) && width > 0 ? width + 'px' : '480px',
    height: Number.isFinite(height) && height > 0 ? height + 'px' : '915px'
  }
})

const eventInfo = ref({
  title: '渝中区两路口街道凯旋路22号附9号居民楼后侧崩塌',
  id: '5001032024014310002',
  image: '',
  attachment: {
    name: '风险排查表.pdf',
    size: '2.3MB'
  }
})

const photoList = ref([
  { caption: '现场崩塌全景' },
  { caption: '警戒区域设置' },
  { caption: '应急处置现场' }
])

const feedbackList = ref([
  {
    title: '应急局应急',
    time: '06-01 19:02',
    desc: '已完成现场风险排查，确认区域暂时稳定，建议立即建围墙并暂停交通流。'
  },
  {
    title: '消防救援局',
    time: '06-01 19:02',
    desc: '已安排人员到达设置警戒范围，疏导绕行通行人员，现场未发现被困人员。'
  },
  {
    title: '交通运输委',
    time: '06-01 19:02',
    desc: '已建议周边小区及停止进行影响，建议增设反光警示标识，暂不影响主干道路通行。'
  },
  {
    title: '城管局',
    time: '06-01 19:02',
    desc: '已联系排险力量暂置临时措施设施，确认通信信号和供电稳定。'
  }
])

const eventInfoS1 = ref({
  title: '渝中区南纪门街道凯旋路22号附9号居民楼后侧崩塌',
  id: '50010320260414310002',
  riskLevel: '小型',
  time: '2026年04-14 09:14:26',
  community: '临江门社区',
  street: '南纪门街道',
  phone: '130****4821',
  gridPerson: '周峻峰',
  riskType: '崩塌',
  lnglat: '',
  address: '渝中区南纪门街道凯旋路22号附9号后侧',
  reportContent: '网格巡查发现居民楼后侧局部崩塌，存在岩土体滑坡风险。',
  needLevel1: '是',
  dispatchSuggestion: '是',
  suggestion: '此次风险较为重大，正在联系相关部门进行处理。'
})

const eventInfoS2 = ref({
  title: '渝中区南纪门街道凯旋路22号附9号居民楼后侧崩塌',
  id: '50010320260513 11:05:02'
})

const deptList = ref([
  { name: '区应急局', time: '03-01 19:02', done: true, statusText: '已完成现场风险复核' },
  { name: '区消防救援局', time: '03-01 19:02', done: true, statusText: '已完成现场风险复核' },
  { name: '区交通运输委', time: '03-01 19:02', done: true, statusText: '已完成现场风险复核' },
  { name: '区经信委', time: '03-01 19:02', done: true, statusText: '已完成现场风险复核' },
  { name: '区城管局', time: '03-01 19:02', done: true, statusText: '已完成现场风险复核' },
  { name: '区住建局', time: '', done: false, statusText: '' },
  { name: '区规自局', time: '', done: false, statusText: '' }
])

const respondedCount = computed(() => {
  return deptList.value.filter(d => d.done).length
})

const progressPercent = computed(() => {
  return Math.round((respondedCount.value / deptList.value.length) * 100)
})

const RISK_CLICK_EVENT = 'BZ_RISK_CLICK'
const RISK_ANALYSIS_DATA_EVENT = 'BZ_RISK_ANALYSIS_DATA'
const PROCESS_STEP_EVENT = 'BZ_PROCESS_STEP_CLICK'

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

function handleRiskClick(data: any) {
  if (!data) return
  currentRiskData.value = data
  status.value = 1
  updateEventInfoS1(data)
  eventInfo.value = {
    title: data.title || '未知事件',
    id: generateEventId(data),
    image: '',
    attachment: {
      name: '风险排查表.pdf',
      size: '2.3MB'
    }
  }
  photoList.value = generatePhotoList(data)
  feedbackList.value = generateFeedbackList(data)
}

function updateEventInfoS1(data: any) {
  const baseInfo = {
    title: data.title || data.eventDesc || '未知事件',
    id: data.asEventNumber || data.id || generateEventId(data),
    riskLevel: data.referenceLevel || data.level || '小型',
    time: data.time || data.eventTime || '',
    community: data.sssq || data.eventCommunity || '',
    street: data.ssjd || data.eventStreet || '',
    phone: data.phone || data.eventPhone || '',
    gridPerson: data.gridPerson || data.eventGridPerson || '',
    riskType: data.eventType || data.type || '',
    lnglat: data.lnglat || data.eventLnglat || '',
    address: data.eventAddress || data.address || '',
    reportContent: data.eventDesc || data.reportContent || '',
    needLevel1: data.needLevel1 || '',
    dispatchSuggestion: data.dispatchSuggestion || '',
    suggestion: data.suggestion || '',
    photoImgs: data.photoImgs
  }
  eventInfoS1.value = baseInfo
}

function handleAnalysisData(data: any) {
  if (!data) return
  const { analysisData } = data
  if (!analysisData) return

  const s1 = eventInfoS1.value
  eventInfoS1.value = {
    ...s1,
    needLevel1: analysisData.emergencyLevel || s1.needLevel1,
    dispatchSuggestion: analysisData.dispatchSuggestion || s1.dispatchSuggestion,
    suggestion: analysisData.suggestion || s1.suggestion,
    reportContent: s1.reportContent || analysisData.suggestion || ''
  }
}

function handleDispatchClick() {
  const s1 = eventInfoS1.value
  const s2 = eventInfoS2.value
  const base: any = currentRiskData.value || {}
  const riskData = {
    ...base,
    source: base.source || '',
    eventType: base.eventType || s1.riskType || '',
    eventAddress: base.eventAddress || s1.address || '',
    eventDesc: base.eventDesc || s1.title || s2.title || '风险事件',
    handleStatus: base.handleStatus || '处置中',
    referenceLevel: base.referenceLevel || s1.riskLevel || '',
    coordinateDepartments: base.coordinateDepartments || '',
    asEventNumber: base.asEventNumber || s1.id || s2.id || '',
    title: s1.title || s2.title || base.title || '风险事件'
  }
  eventBus.emit('OPEN_DIA', {
    diaName: 'dispatch',
    data: riskData
  })
}

function openPdfPreview(file: any) {
  eventBus.emit('OPEN_DIA', {
    diaName: 'pdf-preview',
    title: file?.name || 'PDF预览',
    pdfUrl: file?.url || ''
  })
}

function updateEventInfoS2(data: any) {
  eventInfoS2.value = {
    title: data.title || eventInfoS2.value.title,
    id: data.id || eventInfoS2.value.id
  }
}

function generateEventId(data: any) {
  const timestamp = Date.now().toString().slice(-8)
  const titleHash = (data.title || 'unknown').length.toString().padStart(4, '0')
  return `500103${timestamp}${titleHash}`
}

function generatePhotoList(data: any) {
  const basePhotos = [
    { caption: '现场全景' },
    { caption: '警戒区域' },
    { caption: '处置现场' }
  ]
  if (data.title) {
    return basePhotos.map(p => ({
      caption: `${data.title.slice(0, 4)}${p.caption}`
    }))
  }
  return basePhotos
}

function generateFeedbackList(data: any) {
  const title = data.title || '该事件'
  const time = data.time || new Date().toLocaleString('zh-CN', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
  return [
    {
      title: '应急局',
      time,
      desc: `已完成${title}现场风险排查，确认区域暂时稳定，建议立即采取处置措施。`
    },
    {
      title: '消防救援局',
      time,
      desc: `已安排人员到达${title}设置警戒范围，疏导绕行通行人员，现场未发现被困人员。`
    },
    {
      title: '交通运输委',
      time,
      desc: `已建议${title}周边区域增设反光警示标识，暂不影响主干道路通行。`
    },
    {
      title: '城管局',
      time,
      desc: `已联系排险力量对${title}暂置临时措施设施，确认通信信号和供电稳定。`
    }
  ]
}

function handleProcessStep(data: any) {
  if (!data) return
  const { index, label } = data
  const time = new Date().toLocaleString('zh-CN', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })

  // 流程节点索引：0=隐患发现(status=1)，1=流程推送(status=2)，2=处置反馈(status=3)
  if (index === 0) {
    status.value = 1
    updateEventInfoS1({ title: `[${label}] ${eventInfo.value.title || '风险事件'}` })
  } else if (index === 1) {
    status.value = 2
    updateEventInfoS2({ title: `[${label}] ${eventInfo.value.title || '风险事件'}` })
  } else if (index === 2) {
    status.value = 3
  }

  eventInfo.value = {
    title: `[${label}] ${eventInfo.value.title || '风险事件'}`,
    id: generateEventIdByStep(index),
    image: '',
    attachment: {
      name: getAttachmentNameByStep(index),
      size: '2.3MB'
    }
  }

  photoList.value = generatePhotoListByStep(index)
  feedbackList.value = generateFeedbackListByStep(index, time)
}

function generateEventIdByStep(index: number) {
  const baseId = '500103'
  const timestamp = Date.now().toString().slice(-8)
  const stepSuffix = String(index + 1).padStart(4, '0')
  return `${baseId}${timestamp}${stepSuffix}`
}

function getAttachmentNameByStep(index: number) {
  const names = ['隐患排查报告.pdf', '流程推送单.pdf', '处置反馈表.pdf']
  return names[index] || '处置反馈表.pdf'
}

function generatePhotoListByStep(index: number) {
  const photoSets = [
    [{ caption: '隐患点全景' }, { caption: '隐患细节' }, { caption: '周边环境' }],
    [{ caption: '推送单截图' }, { caption: '接收确认' }, { caption: '流程记录' }],
    [{ caption: '处置现场' }, { caption: '处置结果' }, { caption: '验收照片' }]
  ]
  return photoSets[index] || photoSets[2]
}

function generateFeedbackListByStep(index: number, time: string) {
  const feedbackSets = [
    [
      { title: '排查单位', time, desc: '已完成现场隐患排查，确认隐患类型和等级，记录详细信息。' },
      { title: '鉴定机构', time, desc: '已完成隐患鉴定，出具初步鉴定报告，提出整改建议。' },
      { title: '街道办', time, desc: '已接收隐患报告，安排后续处置工作，制定整改方案。' }
    ],
    [
      { title: '应急局', time, desc: '已接收流程推送，确认处置部门和责任人，启动处置流程。' },
      { title: '消防救援局', time, desc: '已响应流程推送，调动应急力量，准备前往处置现场。' },
      { title: '交通运输委', time, desc: '已确认流程推送内容，协调相关部门配合处置工作。' }
    ],
    [
      { title: '应急局', time, desc: '已完成现场风险排查，确认区域暂时稳定，建议立即采取处置措施。' },
      { title: '消防救援局', time, desc: '已安排人员到达设置警戒范围，疏导绕行通行人员，现场未发现被困人员。' },
      { title: '交通运输委', time, desc: '已建议周边区域增设反光警示标识，暂不影响主干道路通行。' },
      { title: '城管局', time, desc: '已联系排险力量暂置临时措施设施，确认通信信号和供电稳定。' }
    ]
  ]
  return feedbackSets[index] || feedbackSets[2]
}

onMounted(() => {
  eventBus.on(RISK_CLICK_EVENT, handleRiskClick)
  eventBus.on(RISK_ANALYSIS_DATA_EVENT, handleAnalysisData)
  eventBus.on(PROCESS_STEP_EVENT, handleProcessStep)
  handleProcessStep({ index: 0, label: '隐患发现' })
})

onBeforeUnmount(() => {
  eventBus.off(RISK_CLICK_EVENT, handleRiskClick)
  eventBus.off(RISK_ANALYSIS_DATA_EVENT, handleAnalysisData)
  eventBus.off(PROCESS_STEP_EVENT, handleProcessStep)
})
</script>

<script lang="ts">
export default {
  name: "BzFeedback",
  version: "1.0.0"
}
</script>

<style lang="scss" scoped>
.bz-feedback {
  width: 480px;
  height: 915px;
  background: linear-gradient(180deg, rgb(5, 26, 58,0.2) 0%, rgb(8, 31, 62,0.5) 100%);
  border: 1px solid rgba(33, 145, 244, 0.2);
  padding: 14px;
  box-sizing: border-box;
  font-family: 'Microsoft YaHei', sans-serif;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.feedback-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.feedback-title {
  display: flex;
  align-items: center;
  
  .title-arrow {
    font-size: 6px;
    color: #d0e8fe;
    margin-right: 6px;
  }
  
  .title-text {
    font-size: 16px;
    font-weight: 600;
    color: #c7ecfe;
  }
}

.feedback-status {
  display: flex;
  align-items: center;
  padding: 3px 8px;
  background: rgba(255, 165, 2, 0.12);
  border-radius: 2px;
  border: 1px solid #bf8e15;
  
  .status-dot {
    width: 5px;
    height: 5px;
    background: #ffa502;
    border-radius: 50%;
    margin-right: 5px;
  }
  
  .status-text {
    font-size: 11px;
    color: #ffa502;
  }
}

.feedback-event {
  background: rgba(0, 40, 80, 0.3);
  padding: 10px;
  margin-bottom: 10px;
}

.feedback-photos {
  margin-bottom: 10px;
  
  .photos-title {
    display: flex;
    align-items: center;
    font-size: 13px;
    color: #fff;
    margin-bottom: 8px;
    
    .photos-icon {
      margin-right: 6px;
    }
  }
  
  .photos-list {
    display: flex;
    gap: 8px;
  }
  
  .photos-item {
    flex: 1;
    
    .photo-placeholder {
      width: 100%;
      height: 60px;
      background: rgba(71, 168, 255, 0.1);
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: 2px;
      
      .placeholder-icon {
        font-size: 24px;
        opacity: 0.5;
      }
    }
    
    .photo-caption {
      font-size: 11px;
      color: #8bbce6;
      margin-top: 4px;
      text-align: center;
    }
  }
}

.event-title {
  font-size: 20px;
  font-weight: 600;
  color: #c0dcf2;
  line-height: 1.4;
  margin-bottom: 4px;
}

.event-id {
  font-size: 12px;
  color: #8bbce6;
}

.feedback-timeline {
  flex: 1;
  overflow-y: auto;
  padding-right: 4px;
  
  &::-webkit-scrollbar {
    width: 3px;
  }
  
  &::-webkit-scrollbar-thumb {
    background: rgba(33, 145, 244, 0.3);
  }
  
  &::-webkit-scrollbar-track {
    background: transparent;
  }
}

.timeline-item {
  display: flex;
  position: relative;
  padding-bottom: 10px;
  
  &:last-child {
    .timeline-dot::after {
      display: none;
    }
  }
}

.timeline-dot {
  flex-shrink: 0;
  width: 10px;
  height: 10px;
  background: #47a8ff;
  border-radius: 50%;
  margin-right: 8px;
  margin-top: 8px;
  position: relative;
  z-index: 1;
  
  &::after {
    content: '';
    position: absolute;
    left: 4px;
    top: 12px;
    bottom: -10px;
    width: 2px;
    background: rgba(71, 168, 255, 0.3);
  }
}

.timeline-content {
  flex: 1;
  min-width: 0;
  background: rgba(71, 168, 255, 0.08);
  padding: 8px 10px;
  border-radius: 2px;
}

.timeline-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.timeline-title {
  font-size: 12px;
  font-weight: 500;
  color: #d8eaf9;
}

.timeline-time {
  font-size: 11px;
  color: #8bbce6;
}

.timeline-desc {
  font-size: 12px;
  color: #c4daef;
  line-height: 1.5;
}

.feedback-image {
  background: rgba(0, 40, 80, 0.3);
  padding: 8px;
  margin-top: 10px;
  margin-bottom: 10px;
  text-align: center;
  
  img {
    width: 100%;
    max-height: 120px;
    object-fit: cover;
  }
}

.feedback-attachment {
  margin-bottom: 10px;
  
  .photos-title {
    display: flex;
    align-items: center;
    font-size: 13px;
    color: #fff;
    margin-bottom: 8px;
  }
  
  .attachment-list {
    display: flex;
    gap: 8px;
  }
  
  .attachment-item {
    flex: 1;
    background: rgba(71, 168, 255, 0.08);
    padding: 8px 10px;
    display: flex;
    align-items: center;
    border-radius: 2px;
    cursor: pointer;

    &:hover {
      background: rgba(71, 168, 255, 0.16);
    }
  }
}

.attachment-icon {
  width: 32px;
  height: 32px;
  background: rgba(33, 145, 244, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 8px;
  
  .icon-file {
    font-size: 16px;
    color: #47a8ff;
  }
}

.attachment-info {
  flex: 1;
  min-width: 0;
}

.attachment-name {
  font-size: 12px;
  color: #fff;
  margin-bottom: 2px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.attachment-size {
  font-size: 11px;
  color: #8bbce6;
}

.attachment-action {
  .icon-download {
    font-size: 12px;
    color: #47a8ff;
    white-space: nowrap;
  }
}

/* ========== 状态1：隐患发现 样式 ========== */
.event-title--s1 {
  font-size: 18px;
  line-height: 1.4;
}

.info-section,
.photo-section,
.report-section {
  margin-bottom: 10px;
  overflow-y: auto;
  
  &::-webkit-scrollbar {
    width: 3px;
  }
  &::-webkit-scrollbar-thumb {
    background: rgba(33, 145, 244, 0.3);
  }
}

.info-title {
  font-size: 13px;
  color: #fff;
  margin-bottom: 8px;
  font-weight: 500;
}

.info-grid {
  display: flex;
  flex-wrap: wrap;
  background: rgba(71, 168, 255, 0.06);
  padding: 8px 10px;
  border-radius: 2px;
}

.info-item {
  width: 50%;
  display: flex;
  align-items: center;
  padding: 3px 0;
  font-size: 12px;
  box-sizing: border-box;

  &--right {
    padding-left: 8px;
  }

  &--full {
    width: 100%;
  }
}

.info-label {
  color: #8bbce6;
  flex-shrink: 0;
}

.info-divider {
  color: #8bbce6;
  flex-shrink: 0;
}

.info-value {
  color: #e8f8ff;
  flex: 1;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;

  &--warning {
    color: #ffa502;
  }
}

/* 现场照片 */
.photo-big {
  width: 100%;
  height: 130px;
  background: rgba(71, 168, 255, 0.08);
  border-radius: 2px;
  position: relative;
  overflow: hidden;

  &__placeholder {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
    background-size: cover;
    background-position: center;
    background-repeat: no-repeat;
    background-color: rgba(0, 40, 80, 0.5);
    background-blend-mode: overlay;

    .placeholder-icon {
      width: 36px;
      height: 36px;
      border-radius: 50%;
      background: rgba(0, 0, 0, 0.55);
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
      font-size: 14px;
      position: absolute;
      left: 12px;
      bottom: 12px;
    }
  }
}

/* 上报内容 */
.report-body {
  background: rgba(71, 168, 255, 0.06);
  padding: 10px;
  border-radius: 2px;
}

.report-ai {
  font-size: 12px;
  color: #c4daef;
  line-height: 1.5;
  margin-bottom: 8px;
  padding-bottom: 8px;
  border-bottom: 1px dashed rgba(71, 168, 255, 0.15);
}

.ai-tag {
  display: inline-block;
  padding: 1px 6px;
  background: linear-gradient(90deg, rgba(71, 168, 255, 0.25) 0%, rgba(71, 168, 255, 0.08) 100%);
  border: 1px solid rgba(71, 168, 255, 0.4);
  color: #47a8ff;
  border-radius: 2px;
  font-size: 11px;
  margin-right: 6px;
}

.report-row {
  display: flex;
  align-items: flex-start;
  padding: 4px 0;
  font-size: 12px;
}

.report-label {
  color: #8bbce6;
  flex-shrink: 0;
  min-width: 96px;
}

.report-value {
  color: #e8f8ff;
  flex: 1;
  line-height: 1.5;
}

/* 一键调度按钮 */
.dispatch-btn {
  margin-top: auto;
  height: 40px;
  background: linear-gradient(180deg, #47a8ff 0%, #0454cb 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 14px;
  cursor: pointer;
  border-radius: 2px;
  font-weight: 500;
  transition: opacity 0.2s;

  &:hover {
    opacity: 0.9;
  }
}

/* ========== 状态2：流程推送 样式 ========== */
.event-title--s2 {
  font-size: 18px;
  line-height: 1.4;
}

.dept-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.progress-bar {
  position: relative;
  height: 18px;
  background: #ffbc00;
  border-radius: 9px;
  margin-bottom: 12px;
  overflow: hidden;
}

.progress-bar__fill {
  position: absolute;
  top: 0;
  left: 0;
  height: 100%;
  background: linear-gradient(90deg, #30dd70 0%, #9afebf 100%);
  border-radius: 9px;
  transition: width 0.3s ease;
}

.progress-bar__label {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 11px;
  color: #e8f8ff;
  white-space: nowrap;
  z-index: 1;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.4);
}

.dept-list {
  flex: 1;
  overflow-y: auto;
  padding-right: 4px;

  &::-webkit-scrollbar {
    width: 3px;
  }
  &::-webkit-scrollbar-thumb {
    background: rgba(33, 145, 244, 0.3);
  }
}

.dept-item {
  display: flex;
  align-items: center;
  padding: 10px 8px;
  background: rgba(71, 168, 255, 0.05);
  border-radius: 2px;
  margin-bottom: 6px;
}

.dept-item__dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  margin-right: 10px;
  flex-shrink: 0;

  &.dot-done {
    background: #12dd60;
    box-shadow: 0 0 4px rgba(71, 168, 255, 0.6);
  }

  &.dot-pending {
    background: #ffbc00;
  }
}

.dept-item__info {
  flex: 1;
  min-width: 0;
}

.dept-item__name {
  font-size: 13px;
  color: #e8f8ff;
  margin-bottom: 2px;
}

.dept-item__time {
  font-size: 11px;
  color: #8bbce6;

  &--pending {
    color: #6b7d93;
  }
}

.dept-item__status {
  font-size: 11px;
  flex-shrink: 0;

  &.status-done {
    color: #47a8ff;
  }

  &.status-pending {
    color: #6b7d93;
  }
}
</style>
