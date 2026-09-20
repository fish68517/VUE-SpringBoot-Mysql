<template>
  <BusinessLayout :title="titles[mode] || '智慧水务'" :section="section">
    <PortalPanel v-if="['portal', 'portalDetail', 'mobileHome'].includes(mode)" :mode="mode" :query="query" />
    <AlarmPanel v-else-if="['alarms', 'alarmDetail'].includes(mode)" :query="query" />
    <OrderPanel
      v-else-if="['orders', 'orderDetail', 'orderEdit'].includes(mode)"
      :mode="mode"
      :query="query"
    />
    <MapPanel v-else-if="['map', 'mobileMap'].includes(mode)" :query="query" :active="active" />
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
    <SettingsPanel v-else-if="['settings', 'profile'].includes(mode)" />
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
  </BusinessLayout>
</template>
<script setup lang="ts">
import { computed } from 'vue'
import MapManagement from './MapManagement.vue'
import InspectionTools from './InspectionTools.vue'
import OperationsPanel from './OperationsPanel.vue'
import VideoPanel from './VideoPanel.vue'
import { titles } from '../navigation/routeMap'
import BusinessLayout from '../layouts/BusinessLayout.vue'
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
