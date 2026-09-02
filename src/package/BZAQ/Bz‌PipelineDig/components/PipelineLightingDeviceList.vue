<template>
  <PipelineListModal
    :title="title"
    :columns="columns"
    :rows="activeRows"
    :scroll-x="1120"
    :show-filters="false"
    :tabs="tabs"
    :active-tab="activeTab"
    @update:active-tab="activeTab = $event"
    @open-detail="emit('open-detail', $event)"
    @close="emit('close')"
  />
</template>

<script setup lang="ts">
import { computed, h, onMounted, ref, watch } from 'vue'
import axios from 'axios'
import PipelineListModal from './PipelineListModal.vue'

const emit = defineEmits(['close', 'open-detail'])
const props = defineProps({
  title: {
    type: String,
    default: '照明设备'
  },
  payload: {
    type: Object,
    default: () => ({})
  }
})
const title = computed(() => props.title || '照明设备')
const isWarning = computed(() => props.payload?.isWarning === true)
const BASE_URL = 'http://23.99.16.179:11001'

const activeTab = ref('water-level')

const tabs = [
  { label: '路面水位监测', value: 'water-level', count: 20 },
  { label: '井盖状态监测', value: 'manhole', count: 150 },
  { label: '杆体倾斜监测', value: 'pole-tilt', count: 401 }
]

const columns = [
  { title: '设备名称', key: 'deviceName', width: 180 },
  {
    title: '设备编码',
    key: 'deviceCode',
    width: 190,
    render(row: any) {
      return h('span', { class: 'link-cell' }, row.deviceCode)
    }
  },
  { title: '设备类型', key: 'deviceType', width: 150 },
  { title: '安装地址', key: 'installAddress', width: 150 },
  { title: '安装时间', key: 'installTime', width: 170 },
  { title: '经度', key: 'longitude', width: 130 },
  { title: '纬度', key: 'latitude', width: 130 }
]

const deviceTypeMap: Record<string, string> = {
  'water-level': '路面水位监测',
  manhole: '井盖状态监测',
  'pole-tilt': '杆体倾斜'
}

const rows = ref<any[]>([])
const activeRows = computed(() => rows.value)

function getResponseList(responseData: any) {
  const data = responseData?.data ?? responseData
  if (Array.isArray(data)) return data
  if (Array.isArray(data?.content)) return data.content
  if (Array.isArray(data?.records)) return data.records
  if (Array.isArray(data?.list)) return data.list
  if (Array.isArray(data?.rows)) return data.rows
  return []
}

function normalizeRow(row: any, index: number) {
  const detailType = activeTab.value
  const deviceName = row.deviceName || row.name || '--'
  const deviceCode = row.deviceCode || row.deviceId || row.platformCode || row.code || '--'
  const installAddress = row.installAddress || row.detailAddress || row.address || row.location || '--'

  return {
    ...row,
    key: row.id || deviceCode || index,
    deviceName,
    deviceCode,
    deviceId: row.deviceId || deviceCode,
    deviceType: row.deviceType || deviceTypeMap[detailType] || '--',
    installAddress,
    deviceAddress: installAddress,
    installTime: row.installTime || '--',
    longitude: row.longitude ?? row.lng ?? '--',
    latitude: row.latitude ?? row.lat ?? '--',
    detailType,
    status: row.status || row.deviceStatus || row.networkStatus || (isWarning.value ? '处置中' : '已完成')
  }
}

async function fetchRows() {
  try {
    const res = await axios.post(`${BASE_URL}/api/boot/system/pipeline/lightingDevice`, {
      index: 1,
      size: 1000,
      isWarning: isWarning.value,
      deviceType: deviceTypeMap[activeTab.value] || ''
    })
    rows.value = getResponseList(res?.data).map(normalizeRow)
  } catch (error) {
    console.error('获取照明设备列表失败', error)
    rows.value = []
  }
}

watch(() => [props.payload?.isWarning, activeTab.value], fetchRows)

onMounted(fetchRows)
</script>

<script lang="ts">
export default {
  name: 'PipelineLightingDeviceList'
}
</script>
