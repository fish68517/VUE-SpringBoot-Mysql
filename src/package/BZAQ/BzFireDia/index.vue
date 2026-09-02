<template>
  <div class="bz-fire-dia-container" :style="containerStyle">
    <!-- <div class="debug-switcher">
      <select v-model="debugSelection" @change="handleDebugChange">
        <option value="" disabled>选择调试组件</option>
        <option
          v-for="item in debugOptions"
          :key="item.value"
          :value="item.value"
        >
          {{ item.label }}
        </option>
      </select>
    </div> -->

    <!-- 遮罩层（点击不关闭） -->
    <div v-if="showMask" class="modal-overlay"></div>

    <!-- 智管场景列表弹窗（居中） -->
    <FireSceneList
      v-if="activePopup === 'fire-scene-list'"
      :key="sceneListKey"
      :style="popupStyle"
      @close="closeAllPopup"
      :type="type"
    />
    <!-- 消防通道保畅弹窗（居中） -->
    <FirePassageSmooth
      v-if="activePopup === 'fire-passage-smooth'"
      :style="popupStyle"
      @close="closeAllPopup"
    />
    <!-- 消防水管弹窗（居中） -->
    <FireWater
      v-if="activePopup === 'fire-water'"
      :style="popupStyle"
      @close="closeAllPopup"
    />
     <!-- 消控室监测弹窗（居中） -->
    <FireCotrol
      v-if="activePopup === 'fire-cotrol'"
      :style="popupStyle"
      @close="closeAllPopup"
    />
     <!-- 燃气监测弹窗（居中） -->
    <FireGas
      v-if="activePopup === 'fire-gas'"
      :style="popupStyle"
      @close="closeAllPopup"
    />
     <!-- 化粪池监测弹窗（居中） -->
    <FireSeptic
      v-if="activePopup === 'fire-septic'"
      :style="popupStyle"
      @close="closeAllPopup"
    />
    <!-- 网格巡查隐患弹窗（居中） -->
    <GridInspectionHidden
      v-if="activePopup === 'grid-inspection-hidden'"
      :style="popupStyle"
      @close="closeAllPopup"
    />
    <!-- 双随机弹窗（居中） -->
    <FireRadom
      v-if="activePopup === 'fire-radom'"
      :style="popupStyle"
      @close="closeAllPopup"
    />
     <!-- 近期动火巡查不到位弹窗（居中） -->
    <FireHotWorkHidden
      v-if="activePopup === 'fire-work-hidden'"
      :style="popupStyle"
      @close="closeAllPopup"
    />
    <!-- 近期防火巡查不到位弹窗（居中） -->
    <FirePatrolMissing
      v-if="activePopup === 'fire-patrol-missing'"
      :style="popupStyle"
      @close="closeAllPopup"
    />
    <!-- 近期未开展消防设施维保弹窗（居中） -->
    <FireMaintenanceMissing
      v-if="activePopup === 'fire-maintenance-missing'"
      :style="popupStyle"
      @close="closeAllPopup"
    />
    <!-- 责令立即整改的违法行为弹窗（居中） -->
    <FireRectifyIllegal
      v-if="activePopup === 'fire-rectify-illegal'"
      :style="popupStyle"
      @close="closeAllPopup"
    />
    <!-- 责令限期整改的消防安全违法行为弹窗（居中） -->
    <FirePunishIllegal
      v-if="activePopup === 'fire-punish-illegal'"
      :style="popupStyle"
      @close="closeAllPopup"
    />
    <FireVideoPlayer
      v-if="activePopup === 'fire-video-player'"
      :key="videoPopupKey"
      :style="popupStyle"
      :title="videoPopupName"
      :video-url="videoPopupUrl"
      @close="closeAllPopup"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import FireSceneList from './components/FireSceneList.vue'
import FirePassageSmooth from './components/FirePassageSmooth.vue'
import GridInspectionHidden from './components/GridInspectionHidden.vue'
import FireHotWorkHidden from './components/FireHotWorkHidden.vue'
import FirePatrolMissing from './components/FirePatrolMissing.vue'
import FireMaintenanceMissing from './components/FireMaintenanceMissing.vue'
import FireRectifyIllegal from './components/FireRectifyIllegal.vue'
import FirePunishIllegal from './components/FirePunishIllegal.vue'
import FireWater from './components/FireWater.vue'
import FireCotrol from './components/FireCotrol.vue';
import FireGas from './components/FireGas.vue';
import FireSeptic from './components/FireSeptic.vue';
import FireRadom from './components/FireRadom.vue';
import FireVideoPlayer from './components/FireVideoPlayer.vue';



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

const sourceName = 'BzFireDia'

// 事件总线封装（暴露给父组件使用）
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

// 弹窗控制
const showMask = ref(false)
const activePopup = ref<string | null>(null)
const type = ref(1)
const sceneListKey = ref(0)
const debugSelection = ref('')
const videoPopupName = ref('')
const videoPopupUrl = ref('')
const videoPopupKey = ref(0)

const debugOptions = [
  { label: '消控室监测', value: 'fire-cotrol', diaName: 'fire-cotrol' },
  { label: '消防通道保畅', value: 'fire-passage-smooth', diaName: 'fire-passage-smooth' },
  { label: '消防水管', value: 'fire-water', diaName: 'fire-water' },
  { label: '燃气监测', value: 'fire-gas', diaName: 'fire-gas' },
  { label: '化粪池监测', value: 'fire-septic', diaName: 'fire-septic' },
  { label: '网格巡查隐患', value: 'grid-inspection-hidden', diaName: 'grid-inspection-hidden' },
  { label: '双随机', value: 'fire-radom', diaName: 'fire-radom' },
  { label: '近期动火巡查不到位', value: 'fire-work-hidden', diaName: 'fire-work-hidden' },
  { label: '近期防火巡查不到位', value: 'fire-patrol-missing', diaName: 'fire-patrol-missing' },
  { label: '近期未开展消防设施维保', value: 'fire-maintenance-missing', diaName: 'fire-maintenance-missing' },
  { label: '责令立即整改违法行为', value: 'fire-rectify-illegal', diaName: 'fire-rectify-illegal' },
  { label: '责令限期整改违法行为', value: 'fire-punish-illegal', diaName: 'fire-punish-illegal' },
  { label: '智管场景-火灾扑救', value: 'fire-scene-list-1', diaName: 'fire-scene-list', type: 1 },
  { label: '智管场景-重大火情', value: 'fire-scene-list-2', diaName: 'fire-scene-list', type: 2 },
  { label: '智管场景-火灾舆情', value: 'fire-scene-list-3', diaName: 'fire-scene-list', type: 3 },
  { label: '智管场景-抢险救援', value: 'fire-scene-list-4', diaName: 'fire-scene-list', type: 4 },
  { label: '智管场景-社会救助', value: 'fire-scene-list-5', diaName: 'fire-scene-list', type: 5 }
]

// 容器宽高跟随低代码平台拖动的 attr.w / attr.h（computed 确保响应式更新）
const containerStyle = computed(() => {
  const width = Number(props.chartConfig?.attr?.w)
  const height = Number(props.chartConfig?.attr?.h)

  return {
    width: Number.isFinite(width) && width > 0 ? width + 'px' : '100%',
    height: Number.isFinite(height) && height > 0 ? height + 'px' : '100%'
  }
})

// 弹窗居中定位（computed 确保响应式）
const popupStyle = computed(() => ({
  left: '50%',
  top: '50%'
}))

const allowedPopups = [
  'fire-scene-list',
  'fire-passage-smooth',
  'fire-water',
  'fire-cotrol',
  'fire-gas',
  'fire-septic',
  'grid-inspection-hidden',
  'fire-radom',
  'fire-work-hidden',
  'fire-patrol-missing',
  'fire-maintenance-missing',
  'fire-rectify-illegal',
  'fire-punish-illegal',
  'fire-video-player'
]

function openPopup(payload) {
  if (!payload || !payload.diaName) return
  if (!allowedPopups.includes(payload.diaName)) return
  const params = payload.params || {}
  const nextType = Number(payload.type ?? params.type)
  if (payload.diaName === 'fire-scene-list') {
    type.value = Number.isFinite(nextType) && nextType > 0 ? nextType : 1
    sceneListKey.value += 1
  }
  if (payload.diaName === 'fire-video-player') {
    videoPopupName.value = payload.name || params.name || ''
    videoPopupUrl.value = payload.videoUrl || params.videoUrl || ''
    videoPopupKey.value += 1
  }
  activePopup.value = payload.diaName
  debugSelection.value = payload.diaName === 'fire-scene-list'
    ? `fire-scene-list-${type.value}`
    : payload.diaName
  showMask.value = true
}

function closeAllPopup() {
  activePopup.value = null
  showMask.value = false
}

function handleDebugChange() {
  const option = debugOptions.find(item => item.value === debugSelection.value)
  if (!option) return
  openPopup({
    diaName: option.diaName,
    type: option.type
  })
}

const OPEN_FIRE_EVENT = 'OPEN_FIRE_DIA'
const OPEN_EVENT = 'OPEN_DIA'
const CLOSE_EVENT = 'CLOSE_DIA'

onMounted(() => {
  eventBus.on(OPEN_EVENT, openPopup)
  eventBus.on(OPEN_FIRE_EVENT, openPopup)
  eventBus.on(CLOSE_EVENT, closeAllPopup)
})

onBeforeUnmount(() => {
  eventBus.off(OPEN_EVENT, openPopup)
  eventBus.off(OPEN_FIRE_EVENT, openPopup)
  eventBus.off(CLOSE_EVENT, closeAllPopup)
})

// 暴露eventBus给父组件
defineExpose({
  openPopup
})
</script>

<script lang="ts">
export default {
  name: "BzFireDia",
  version: "1.0.0"
}
</script>

<style lang="scss" scoped>
.bz-fire-dia-container {
  position: relative;
  overflow: visible;
  pointer-events: none;
}

.debug-switcher {
  position: absolute;
  top: 8px;
  right: 8px;
  z-index: 1200;
  pointer-events: auto;

  select {
    min-width: 220px;
    height: 32px;
    padding: 0 10px;
    border: 1px solid rgba(83, 174, 255, 0.45);
    background: rgba(3, 19, 54, 0.9);
    color: #d7e9ff;
    font-size: 14px;
    outline: none;
    cursor: pointer;
  }
}

.modal-overlay {
  position: fixed;
  inset: 0;
  z-index: 1000;
  pointer-events: auto;
}
</style>
