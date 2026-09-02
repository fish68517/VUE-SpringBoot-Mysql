<template>
  <div class="popup-container" :style="containerStyle">
    <div v-if="showMask" class="popup-mask"></div>

    <AIAnalysisPopup
      v-if="activePopup === 'ai-analysis'"
      :style="popupStyle"
      :title="currentData.title"
      :analysis-data="currentData.analysisData"
      @close="closeAllPopup"
      @dispatch="handleDispatch"
    />

    <DispatchPopup
      v-if="activePopup === 'dispatch'"
      :style="popupStyle"
      :risk-data="currentRiskData"
      @close="closeDispatchPopup"
      @confirm="handleDispatchConfirm"
    />

    <PdfPreviewPopup
      v-if="activePopup === 'pdf-preview'"
      :popup-style="popupStyle"
      :title="pdfPreviewTitle"
      :pdf-url="pdfPreviewUrl"
      @close="closeAllPopup"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive, onMounted, onBeforeUnmount } from 'vue'
import AIAnalysisPopup from './components/AIAnalysisPopup.vue'
import DispatchPopup from './components/DispatchPopup.vue'
import PdfPreviewPopup from './components/PdfPreviewPopup.vue'
import defaultPdfUrl from './pdf/risk-report.pdf'

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

const sourceName = 'BzAIAnalysisDia'
const OPEN_EVENT = 'OPEN_DIA'
const CLOSE_EVENT = 'CLOSE_DIA'
const RISK_CLICK_EVENT = 'BZ_RISK_CLICK'

const eventBus = {
  on(event, callback) {
    if (!props.bus) return
    props.bus.on(event, ({ source, data }) => {
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

const activePopup = ref<string | null>(null)
const showMask = ref(false)
const currentRiskData = ref<any>(null)
const pdfPreviewTitle = ref('PDF预览')
const pdfPreviewUrl = ref('')

const currentData = reactive({
  title: '渝中区南纪门街道凯旋路22号附9号居民楼后侧崩塌',
  analysisData: {
    emergencyLevel: '较重要',
    dispatchSuggestion: '是',
    suggestion: '此次风险较为重大，正在联系相关部门进行处理。'
  }
})

const containerStyle = computed(() => {
  const width = Number(props.chartConfig?.attr?.w)
  const height = Number(props.chartConfig?.attr?.h)
  return {
    width: Number.isFinite(width) && width > 0 ? width + 'px' : '100%',
    height: Number.isFinite(height) && height > 0 ? height + 'px' : '100%'
  }
})

const popupStyle = computed(() => ({
  left: '50%',
  top: '50%'
}))

const allowedPopups = [
  'ai-analysis',
  'pdf-preview',
  'dispatch'
]

function openPopup(payload) {
  if (!payload || !payload.diaName) return
  if (!allowedPopups.includes(payload.diaName)) return

  if (payload.diaName === 'ai-analysis') {
    if (payload.data) Object.assign(currentData, payload.data)
    currentRiskData.value = payload.data?.riskData || payload.data || currentRiskData.value
  }

  if (payload.diaName === 'pdf-preview') {
    pdfPreviewTitle.value = payload.title || payload.name || payload.data?.title || payload.data?.name || 'PDF预览'
    pdfPreviewUrl.value = payload.pdfUrl || payload.url || payload.data?.pdfUrl || payload.data?.url || defaultPdfUrl
  }

  if (payload.diaName === 'dispatch') {
    currentRiskData.value = payload.data || payload.riskData || currentRiskData.value
  }

  activePopup.value = payload.diaName
  showMask.value = true
}

function closeAllPopup() {
  activePopup.value = null
  showMask.value = false
  currentRiskData.value = null
  pdfPreviewUrl.value = ''
}

function handleRiskClick(data: any) {
  currentRiskData.value = data
  activePopup.value = 'dispatch'
  showMask.value = true
}

function handleDispatch() {
  activePopup.value = 'dispatch'
  showMask.value = true
}

function closeDispatchPopup() {
  activePopup.value = null
  currentRiskData.value = null
  showMask.value = false
}

function handleDispatchConfirm(data: { street: string; streetName: string }) {
  console.log('确认调度:', data)
  closeAllPopup()
}

onMounted(() => {
  eventBus.on(OPEN_EVENT, openPopup)
  eventBus.on(CLOSE_EVENT, closeAllPopup)
  eventBus.on(RISK_CLICK_EVENT, handleRiskClick)
})

onBeforeUnmount(() => {
  eventBus.off(OPEN_EVENT, openPopup)
  eventBus.off(CLOSE_EVENT, closeAllPopup)
  eventBus.off(RISK_CLICK_EVENT, handleRiskClick)
})
</script>

<script lang="ts">
export default {
  name: "BzAIAnalysisDia",
  version: "1.0.0"
}
</script>

<style lang="scss" scoped>
.popup-container {
  position: relative;
  overflow: visible;
  pointer-events: none;
}

.popup-mask {
  position: fixed;
  inset: 0;
  z-index: 1000;
  pointer-events: auto;
  background: rgba(0, 0, 0, 0.3);
}

.popup-container > :not(.popup-mask) {
  pointer-events: auto;
}
</style>
