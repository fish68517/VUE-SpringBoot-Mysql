<template>
  <div class="bz-green-fire-dia-container" :style="containerStyle">
    <div v-if="visible" class="modal-overlay" @click="closePopup"></div>
    <div v-if="visible" class="green-fire-dia" :style="popupStyle">
      <div class="dia-header">
        <span class="dia-title">{{ activePark.name }}</span>
        <button class="close-btn" type="button" @click="closePopup">×</button>
      </div>

      <div class="dia-body">
        <!-- 当天预警情况 -->
        <div class="warn-section">
          <div class="warn-section__head">
            <span class="warn-summary">当天预警情况：{{ todayUnresponded }}/{{ todayTotal }}</span>
          </div>
          <div class="warn-card-list">
            <div v-for="(item, idx) in todayWarnings" :key="'today_' + idx" class="warn-card">
              <div class="warn-card__head">
                <span class="warn-card__ai"><img src="./img/AI.png" alt="" />AI分析</span>
                <span class="warn-card__time">{{ item.time }}</span>
              </div>
              <p class="warn-card__content">{{ item.content }}</p>
              <div class="warn-card__foot">
                <button class="event-btn" type="button" @click="openEventProcess(item)">事件单流程</button>
              </div>
            </div>
          </div>
        </div>

        <!-- 历史预警情况 -->
        <div class="warn-section">
          <div class="warn-section__head">
            <span class="warn-summary">历史预警情况：{{ historyUnresponded }}/{{ historyTotal }}</span>
            <n-config-provider :locale="zhCN" :date-locale="dateZhCN" class="date-config-wrapper">
              <n-date-picker
                v-model:value="historyDate"
                class="history-date-picker"
                type="date"
                clearable
                placeholder="选择日期"
                format="yyyy-MM-dd"
              />
            </n-config-provider>
          </div>
          <div class="warn-card-list">
            <div v-for="(item, idx) in historyWarnings" :key="'history_' + idx" class="warn-card">
              <div class="warn-card__head">
                <span class="warn-card__ai"><img src="./img/AI.png" alt="" />AI分析</span>
                <span class="warn-card__time">{{ item.time }}</span>
              </div>
              <p class="warn-card__content">{{ item.content }}</p>
              <div class="warn-card__foot">
                <button class="event-btn" type="button" @click="openEventProcess(item)">事件单流程</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { NDatePicker, NConfigProvider, zhCN, dateZhCN } from 'naive-ui'

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

const sourceName = 'BzGreenFireDia'
const OPEN_EVENT = 'OPEN_GREEN_FIRE_DIA'
const CLOSE_EVENT = 'CLOSE_DIA'

const defaultPark = {
  name: '鹅岭公园',
  imageKey: 'eling',
  deviceCode: '50010300106001000165',
  area: '6.5',
  manager: '陈俊',
  phone: '13452847983',
  address: '鹅岭正街176号',
  warningLevel: '高风险'
}

const visible = ref(false)
const activePark = ref({ ...defaultPark })
const historyDate = ref<number | null>(null)
const eventCallbackMap = new Map<string, Map<Function, Function>>()

// 当天预警静态数据
const todayUnresponded = ref(0)
const todayTotal = ref(2)
const todayWarnings = ref([
  {
    time: '2026-08-11 14:30:00',
    content: '当前因持续高温少雨、植被干燥等原因，渝中区鹅岭公园存在橙色高等级绿地火灾风险！'
  },
  {
    time: '2026-08-11 09:15:00',
    content: '当前因持续高温少雨、植被干燥等原因，渝中区鹅岭公园存在橙色高等级绿地火灾风险！'
  }
])

// 历史预警静态数据
const historyUnresponded = ref(1)
const historyTotal = ref(3)
const historyWarnings = ref([
  {
    time: '2026-08-10 16:20:00',
    content: '当前因持续高温少雨、植被干燥等原因，渝中区鹅岭公园存在橙色高等级绿地火灾风险！'
  },
  {
    time: '2026-08-09 11:05:00',
    content: '当前因持续高温少雨、植被干燥等原因，渝中区鹅岭公园存在橙色高等级绿地火灾风险！'
  },
  {
    time: '2026-08-08 08:45:00',
    content: '当前因持续高温少雨、植被干燥等原因，渝中区鹅岭公园存在橙色高等级绿地火灾风险！'
  }
])

const eventBus = {
  on(event, callback) {
    if (!props.bus) return
    const wrappedCallback = ({ source, data }) => {
      if (source === sourceName) return
      callback(data)
    }
    if (!eventCallbackMap.has(event)) eventCallbackMap.set(event, new Map())
    eventCallbackMap.get(event)?.set(callback, wrappedCallback)
    props.bus.on(event, wrappedCallback)
  },
  off(event, callback) {
    if (!props.bus) return
    const wrappedCallback = eventCallbackMap.get(event)?.get(callback)
    props.bus.off(event, wrappedCallback || callback)
    eventCallbackMap.get(event)?.delete(callback)
  },
  emit(event, data) {
    if (!props.bus) return
    props.bus.emit(event, { data, source: sourceName })
  }
}

function openPopup(payload) {
  activePark.value = {
    ...defaultPark,
    ...(payload?.park || payload || {})
  }
  visible.value = true
}

function closePopup() {
  visible.value = false
}

function openEventProcess(item) {
  eventBus.emit('OPEN_EVENT_PROCESS', { park: activePark.value, item })
}

const containerStyle = computed(() => {
  const width = Number(props.chartConfig?.attr?.w)
  const height = Number(props.chartConfig?.attr?.h)
  return {
    width: Number.isFinite(width) && width > 0 ? `${width}px` : '100%',
    height: Number.isFinite(height) && height > 0 ? `${height}px` : '100%'
  }
})

const popupStyle = computed(() => ({
  left: '50%',
  top: '50%'
}))

onMounted(() => {
  eventBus.on(OPEN_EVENT, openPopup)
  eventBus.on(CLOSE_EVENT, closePopup)
})

onBeforeUnmount(() => {
  eventBus.off(OPEN_EVENT, openPopup)
  eventBus.off(CLOSE_EVENT, closePopup)
})
</script>

<script lang="ts">
export default {
  name: 'BzGreenFireDia',
  version: '1.0.0'
}
</script>

<style lang="scss" scoped>
@font-face {
  font-family: 'BZGreenFireDiaPuHuiTi';
  src: url('../BZThreeGeo/font/Alibaba_PuHuiTi_2.0_55_Regular_55_Regular.ttf') format('truetype');
  font-weight: 400;
  font-style: normal;
  font-display: swap;
}

.bz-green-fire-dia-container {
  position: relative;
  overflow: visible;
  pointer-events: none;
}

.modal-overlay {
  position: fixed;
  inset: 0;
  z-index: 1000;
  background: rgba(0, 0, 0, 0.35);
  pointer-events: auto;
}

.green-fire-dia {
  position: absolute;
  z-index: 1001;
  transform: translate(-50%, -50%);
  box-sizing: border-box;
  width: 720px;
  height: 560px;
  color: #d9f7ff;
  background: #041f3c;
  border: 1px solid rgba(39, 146, 225, 0.68);
  box-shadow: 0 0 18px rgba(30, 135, 217, 0.35);
  font-family: 'BZGreenFireDiaPuHuiTi', Microsoft YaHei, Arial, sans-serif;
  pointer-events: auto;
  display: flex;
  flex-direction: column;
}

.dia-header {
  position: relative;
  flex-shrink: 0;
  height: 40px;
  display: flex;
  align-items: center;
  padding: 0 16px;
  border-bottom: 1px solid rgba(39, 146, 225, 0.4);
}

.dia-title {
  color: #fff;
  font-size: 18px;
  font-weight: 700;
  letter-spacing: 1px;
}

.close-btn {
  position: absolute;
  right: 8px;
  top: 50%;
  transform: translateY(-50%);
  width: 22px;
  height: 22px;
  border: 1px solid rgba(95, 188, 255, 0.45);
  color: #d9f7ff;
  background: rgba(10, 66, 126, 0.72);
  cursor: pointer;
  line-height: 18px;
}

.dia-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 12px 16px;
  overflow: hidden;
  height: calc(100% - 40px);
}

.warn-section {
  height: calc(50% - 6px);
  display: flex;
  flex-direction: column;
  padding: 8px;
  background: linear-gradient(100deg, rgba(7, 66, 125, 0.72), rgba(4, 35, 82, 0.78));
  border: 1px solid rgba(39, 146, 225, 0.4);
  overflow: hidden;
  position: relative;
}

.warn-section__head {
  flex-shrink: 0;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
  gap: 16px;
  overflow: visible;
  z-index: 10;
  position: relative;
}

.warn-summary {
  color: #5feaff;
  font-size: 14px;
  font-weight: 700;
  white-space: nowrap;
  flex-shrink: 0;
}

.date-config-wrapper {
  flex-shrink: 0;
  display: inline-block;
  height: 32px;
  max-height: 32px;
  overflow: visible;
  z-index: 10;
  position: relative;
}

/* 只限制输入框高度，弹出面板高度正常 */
.history-date-picker {
  width: 150px !important;
  display: inline-block !important;
  height: 32px !important;
  max-height: 32px !important;
  overflow: visible;

  :deep(.n-input) {
    background: rgba(7, 66, 125, 0.72) !important;
    border: 1px solid rgba(39, 146, 225, 0.55) !important;
    border-radius: 4px !important;
    height: 32px !important;
    max-height: 32px !important;
    box-shadow: none !important;
  }

  :deep(.n-input:hover) {
    border-color: rgba(135, 230, 255, 0.95) !important;
  }

  :deep(.n-input--focus) {
    border-color: rgba(95, 188, 255, 0.6) !important;
  }

  :deep(.n-input__input-el) {
    color: #d9f7ff !important;
    font-size: 12px !important;
    background: transparent !important;
  }

  :deep(.n-input__placeholder) {
    color: #b8d9ff !important;
    font-size: 12px !important;
  }

  :deep(.n-base-suffix) {
    color: #b8d9ff !important;
  }
}

/* 修复日期弹出面板高度问题，强制不限制 */
:deep(.n-date-picker .n-picker-panel) {
  max-height: unset !important;
  height: auto !important;
  z-index: 10000 !important;
}

.warn-card-list {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
  overflow-y: auto;
  padding-right: 4px;
  position: relative;
  z-index: 1;
}

/* 隐藏滚动条 */
.warn-card-list::-webkit-scrollbar {
  width: 0;
  height: 0;
}

.warn-card {
  padding: 8px 10px;
  border: 1px solid rgba(39, 146, 225, 0.55);
  border-radius: 2px;
  background: linear-gradient(180deg, rgba(6, 95, 169, 0.76), rgba(4, 66, 130, 0.76));
}

.warn-card__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 4px;
}

.warn-card__ai {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #fff;
  font-size: 13px;
  font-weight: 700;
}

.warn-card__ai img {
  width: 16px;
  height: 16px;
}

.warn-card__time {
  color: #b8d9ff;
  font-size: 12px;
  white-space: nowrap;
}

.warn-card__content {
  margin: 0;
  color: #d9f7ff;
  font-size: 12px;
  font-weight: 700;
  line-height: 1.55;
  overflow-wrap: anywhere;
  word-break: break-all;
}

.warn-card__foot {
  display: flex;
  justify-content: flex-end;
  margin-top: 6px;
}

.event-btn {
  height: 26px;
  padding: 0 12px;
  border: 1px solid rgba(95, 188, 255, 0.6);
  border-radius: 2px;
  color: #d9f7ff;
  background: rgba(10, 66, 126, 0.72);
  font-size: 12px;
  cursor: pointer;
  outline: none;
  transition: border-color 0.18s ease, box-shadow 0.18s ease;

  &:hover {
    border-color: rgba(135, 230, 255, 0.95);
    box-shadow: 0 0 8px rgba(35, 153, 255, 0.35);
  }

  &:active {
    background: rgba(72, 211, 255, 0.85);
    color: #041f3c;
  }
}
</style>
