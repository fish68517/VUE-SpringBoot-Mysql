<template>
  <div class="bz-fire-dia-container" :style="containerStyle">
    <!-- 遮罩层（点击不关闭） -->
    <div v-if="showMask" class="modal-overlay"></div>

    <!-- 智管场景列表弹窗（居中） -->
    <FireSceneList
      v-if="activePopup === 'fire-scene-list'"
      :style="popupStyle"
      @close="closeAllPopup"
    />
    <!-- 消防通道保畅弹窗（居中） -->
    <FirePassageSmooth
      v-if="activePopup === 'fire-passage-smooth'"
      :style="popupStyle"
      @close="closeAllPopup"
    />
    <!-- 网格巡查隐患弹窗（居中） -->
    <GridInspectionHidden
      v-if="activePopup === 'grid-inspection-hidden'"
      :style="popupStyle"
      @close="closeAllPopup"
    />
    <!-- 近期动火作业隐患弹窗（居中） -->
    <FireHotWorkHidden
      v-if="activePopup === 'fire-hot-work-hidden'"
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
    <!-- 消防关键KPI弹窗（居中） -->
    <FireKpiList
      v-if="activePopup === 'fire-kpi-list'"
      :style="popupStyle"
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
import FireKpiList from './components/FireKpiList.vue'

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

// 事件总线封装
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
const activePopup = ref(null)

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

function openPopup(payload) {
  if (!payload || !payload.diaName) return
  activePopup.value = payload.diaName
  showMask.value = true
}

function closeAllPopup() {
  activePopup.value = null
  showMask.value = false
}

const OPEN_EVENT = 'OPEN_FIRE_DIA'
const CLOSE_EVENT = 'CLOSE_FIRE_DIA'

onMounted(() => {
  eventBus.on(OPEN_EVENT, openPopup)
  eventBus.on(CLOSE_EVENT, closeAllPopup)
})

onBeforeUnmount(() => {
  eventBus.off(OPEN_EVENT, openPopup)
  eventBus.off(CLOSE_EVENT, closeAllPopup)
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

.modal-overlay {
  position: fixed;
  inset: 0;
  z-index: 1000;
  background: rgba(0, 0, 0, 0.55);
  pointer-events: auto;
}
</style>
