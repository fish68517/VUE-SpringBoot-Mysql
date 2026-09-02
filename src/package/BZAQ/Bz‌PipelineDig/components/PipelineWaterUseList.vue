<template>
  <PipelineListModal
    :title="title" :columns="columns" :rows="rows" :scroll-x="1100" :show-filters="false" @open-detail="emit('open-detail', $event)"
    @close="emit('close')" />
</template>

<script setup lang="ts">
import { computed, h, onMounted, ref, watch } from 'vue'
import axios from 'axios'
import PipelineListModal from './PipelineListModal.vue'

const emit = defineEmits(['close', 'open-detail'])
const props = defineProps({
  title: {
    type: String,
    default: '用水监测'
  },
  payload: {
    type: Object,
    default: () => ({})
  }
})
const title = computed(() => props.title || '用水监测')
const isWarning = computed(() => props.payload?.isWarning === true)
const BASE_URL = 'http://23.99.16.179:11001'

const columns = [
  { title: '设备名称', key: 'deviceName', width: 170 },
  {
    title: '设备编码',
    key: 'deviceCode',
    width: 190,
    render(row: any) {
      return h('span', { class: 'link-cell' }, row.deviceCode)
    }
  },
  { title: '设施名', key: 'facilityName', width: 140 },
  { title: '安装地址', key: 'installAddress', width: 160 },
  { title: '单位名称', key: 'unitName', width: 140 },
  { title: '单位编码', key: 'unitCode', width: 120 },
  { title: '经度', key: 'longitude', width: 150 },
  { title: '纬度', key: 'latitude', width: 150 }
]

const rows = ref<any[]>([])

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
  const deviceName = row.deviceName || row.name || '--'
  const deviceCode = row.deviceCode || row.deviceUniqueCode || row.platformCode || row.code || '--'
  const installAddress = row.installAddress || row.detailAddress || row.address || row.location || '--'
  const unitName = row.unitName || row.dataSourceUnit || row.maintenanceUnit || row.constructionUnitType || '--'
  const unitCode = row.unitCode || row.dataSourceDepartment || row.applicationCode || '--'

  return {
    ...row,
    key: row.id || deviceCode || index,
    deviceName,
    deviceCode,
    facilityName: row.facilityName || row.applicationName || row.projectName || '--',
    installAddress,
    unitName,
    unitCode,
    longitude: row.longitude ?? row.lng ?? '--',
    latitude: row.latitude ?? row.lat ?? '--',
    deviceType: row.deviceType || deviceName,
    deviceAddress: installAddress,
    status: row.status || row.deviceStatus || row.networkStatus || (isWarning.value ? '处置中' : '已完成')
  }
}

async function fetchRows() {
  try {
    const res = await axios.post(`${BASE_URL}/api/boot/system/pipeline/waterMonitor`, {
      index: 1,
      size: 1000,
      isWarning: isWarning.value
    })
    rows.value = getResponseList(res?.data).map(normalizeRow)
  } catch (error) {
    console.error('获取用水监测列表失败', error)
    rows.value = []
  }
}

watch(() => props.payload?.isWarning, fetchRows)

onMounted(fetchRows)
</script>

<script lang="ts">
export default {
  name: 'PipelineWaterUseList'
}
</script>
