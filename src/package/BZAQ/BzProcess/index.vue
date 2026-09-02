<template>
  <div class="bz-process" :style="containerStyle">
    <div class="process-steps">
      <template v-for="(step, index) in steps" :key="index">
        <div 
          class="step-item" 
          :class="{ 'step-active': step.active, 'step-done': step.done }"
          @click="handleStepClick(index)"
        >
          <div class="step-icon">
            <img :src="step.active ? iconListActive[index] : iconList[index]" :alt="step.label" />
          </div>
          <div class="step-label">{{ step.label }}</div>
        </div>
        <div v-if="index < steps.length - 1" class="step-line" :class="{ 'step-line-done': step.done, 'step-line-active': step.active }"></div>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import axios from 'axios'
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import processIcon1 from './img/process1.png'
import processIcon2 from './img/process2.png'
import processIcon3 from './img/process3.png'
import processIcon1Active from './img/process1-active.png'
import processIcon2Active from './img/process2-active.png'
import processIcon3Active from './img/process3-active.png'

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

const sourceName = 'BzProcess'

const containerStyle = computed(() => {
  const width = Number(props.chartConfig?.attr?.w)
  const height = Number(props.chartConfig?.attr?.h)
  return {
    width: Number.isFinite(width) && width > 0 ? width + 'px' : '530px',
    height: Number.isFinite(height) && height > 0 ? height + 'px' : '64px'
  }
})

const steps = ref([
  { label: '隐患发现', active: true, done: false },
  { label: '流程推送', active: false, done: false },
  { label: '处置反馈', active: false, done: false }
])

const iconList = [
  processIcon1,
  processIcon2,
  processIcon3
]

const iconListActive = [
  processIcon1Active,
  processIcon2Active,
  processIcon3Active
]

const PROCESS_STEP_EVENT = 'BZ_PROCESS_STEP_CLICK'
const PROCESS_RESET_EVENT = 'BZ_PROCESS_RESET'
const RISK_CLICK_EVENT = 'BZ_RISK_CLICK'

const currentRiskItem = ref<any>(null)

const setActiveStep = (index: number) => {
  steps.value.forEach((step, i) => {
    step.active = i === index
    step.done = i < index
  })
}

const emitProcessStep = (index: number) => {
  const step = steps.value[index]
  if (!step) return
  eventBus.emit(PROCESS_STEP_EVENT, {
    index,
    label: step.label,
    done: step.done,
    active: step.active
  })
}

const resetProcess = (data?: { index?: number }) => {
  const index = Number(data?.index ?? 0)
  const activeIndex = Number.isFinite(index) ? index : 0
  setActiveStep(activeIndex)
  emitProcessStep(activeIndex)
}
const getEventNumber = (item: any): string => {
  if (!item) return ''
  return item?.asEventNumber ?? item?.eventNum ?? item?.id ?? ''
}

const warningEventDetailLoading = ref(false)
const handleWarningEventRowClick = async (item: any) => {
  if (!item) {
    console.warn('BzProcess handleWarningEventRowClick: item is null, please click a risk item first')
    return
  }
  const eventNumber = getEventNumber(item)
  console.log('BzProcess handleWarningEventRowClick triggered, eventNumber:', eventNumber, 'item:', item)
  if (!eventNumber || warningEventDetailLoading.value) return
  try {
    warningEventDetailLoading.value = true
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
    warningEventDetailLoading.value = false
  }
}
const handleStepClick = (index: number) => {
    setActiveStep(index)
  if (index > 0) {
    handleWarningEventRowClick(currentRiskItem.value)
  } else {
    emitProcessStep(index)
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

function handleRiskClick(data: any) {
  currentRiskItem.value = data
}

onMounted(() => {
  eventBus.on(PROCESS_RESET_EVENT, resetProcess)
  eventBus.on(RISK_CLICK_EVENT, handleRiskClick)
  resetProcess({ index: 0 })
})

onBeforeUnmount(() => {
  eventBus.off(PROCESS_RESET_EVENT, resetProcess)
  eventBus.off(RISK_CLICK_EVENT, handleRiskClick)
})
</script>

<script lang="ts">
export default {
  name: "BzProcess",
  version: "1.0.0"
}
</script>

<style lang="scss" scoped>
.bz-process {
  width: 530px;
  height: 64px;
  display: flex;
  align-items: flex-start;
  padding: 0 20px;
  box-sizing: border-box;
  font-family: 'Microsoft YaHei', sans-serif;
}

.process-steps {
  display: flex;
  align-items: flex-start;
  width: 100%;
}

.step-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex-shrink: 0;
  cursor: pointer;
}

.step-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: contain;
  }
}

.step-label {
  margin-top: 6px;
  font-size: 12px;
  color: #8bbce6;
  white-space: nowrap;
  text-align: center;
}

.step-line {
  width: 180px;
  height: 2px;
  background: rgba(71, 168, 255, 0.3);
  margin-top: 17px;
  flex-shrink: 0;
}

.step-line.step-line-done {
  background: #47a8ff;
}

.step-line.step-line-active {
  background: #47a8ff;
}

.step-item.step-done {
}

.step-item.step-active {
  .step-label {
    color: #e7ecf0;
    font-weight: 600;
  }
}
</style>
