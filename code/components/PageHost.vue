<template>
  <component :is="mobile ? MobileLayout : BusinessLayout" :title="pageTitle" :mode="mode" :section="section">
    <view v-if="denied" class="panel empty">当前账号无权访问此功能，请返回工作台。</view>
    <MobileHome v-else-if="mode === 'mobileHome'" />
    <MobileTasks v-else-if="mode === 'mobileTasks'" :query="query" />
    <MobileProfile v-else-if="mode === 'profile'" />
    <PortalPanel v-else-if="['portal', 'portalDetail'].includes(mode)" :mode="mode" :query="query" />
    <AlarmPanel v-else-if="['alarms', 'alarmDetail'].includes(mode)" :query="query" />
    <OrderPanel
      v-else-if="['orders', 'orderDetail', 'orderEdit'].includes(mode)"
      :mode="mode"
      :query="query"
    />
    <MapPanel
      v-else-if="['map', 'mobileMap'].includes(mode)"
      :query="query"
      :active="active"
      :mobile="mobile"
    />
    <DmaPanel v-else-if="['dma', 'dmaDetail'].includes(mode)" :mode="mode" :query="query" :active="active" />
    <TaskPanel
      v-else-if="['inspection', 'taskDetail', 'taskEdit', 'mobileTasks'].includes(mode)"
      :mode="mode"
      :query="query"
    />
    <DevicePanel
      v-else-if="['datahub', 'device', 'facility'].includes(mode)"
      :mode="mode"
      :query="query"
      :active="active"
    />
    <ReportsPanel v-else-if="mode === 'reports'" :active="active" />
    <SettingsPanel v-else-if="mode === 'settings'" />
    <MapManagement
      v-else-if="['mapChanges', 'mapConfig', 'mapAnalysis', 'logs'].includes(mode)"
      :mode="mode"
      :query="query"
      :active="active"
    />
    <InspectionTools
      v-else-if="['inspectionConfig', 'inspectionReplay'].includes(mode)"
      :mode="mode"
      :query="query"
      :active="active"
    />
    <OperationsPanel
      v-else-if="['energy', 'advancedSettings', 'mobileReport'].includes(mode)"
      :mode="mode"
      :query="query"
      :active="active"
    />
    <VideoPanel v-else-if="mode === 'video'" :active="active" />
    <view v-else class="empty">页面不存在</view>
  </component>
</template>
<script setup lang="ts">
import { computed } from 'vue'
import { useDemo } from '../stores/demo'
import MapManagement from './MapManagement.vue'
import InspectionTools from './InspectionTools.vue'
import OperationsPanel from './OperationsPanel.vue'
import VideoPanel from './VideoPanel.vue'
import { titles } from '../navigation/routeMap'
import BusinessLayout from '../layouts/BusinessLayout.vue'
import MobileLayout from '../layouts/MobileLayout.vue'
import MobileHome from './MobileHome.vue'
import MobileTasks from './MobileTasks.vue'
import MobileProfile from './MobileProfile.vue'
import { useMobileClient } from '../platform/client'
import PortalPanel from './PortalPanel.vue'
import AlarmPanel from './AlarmPanel.vue'
import OrderPanel from './OrderPanel.vue'
import MapPanel from './MapPanel.vue'
import DmaPanel from './DmaPanel.vue'
import TaskPanel from './TaskPanel.vue'
import DevicePanel from './DevicePanel.vue'
import ReportsPanel from './ReportsPanel.vue'
import SettingsPanel from './SettingsPanel.vue'
const props = defineProps<{ mode: string; query: Record<string, string>; active: boolean }>()
const mobile = useMobileClient()
const demo = useDemo()
const denied = computed(
  () =>
    mobile.value &&
    ((['mapConfig', 'inspectionConfig', 'advancedSettings'].includes(props.mode) && !demo.has('dispatch')) ||
      (['mapChanges', 'mobileReport', 'taskEdit', 'orderEdit'].includes(props.mode) && !demo.has('write'))),
)
const pageTitle = computed(
  () =>
    (mobile.value
      ? (
          { mapChanges: '点线纠错', inspectionReplay: '巡检轨迹', orders: '工单办理' } as Record<
            string,
            string
          >
        )[props.mode]
      : '') ||
    titles[props.mode] ||
    '智慧水务',
)
const section = computed(
  () =>
    (
      ({
        alarmDetail: 'alarms',
        orderDetail: 'orders',
        orderEdit: 'orders',
        dmaDetail: 'dma',
        taskDetail: 'inspection',
        taskEdit: 'inspection',
        device: 'datahub',
        facility: 'map',
        portalDetail: 'portal',
        mobileHome: 'portal',
        mobileTasks: 'inspection',
        mobileMap: 'map',
        profile: 'settings',
      }) as Record<string, string>
    )[props.mode] || props.mode,
)
</script>
