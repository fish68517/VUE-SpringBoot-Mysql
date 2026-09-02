<template>
  <PipelineListModal
    :title="title"
    :columns="columns"
    :rows="rows"
    :scroll-x="1240"
    :show-filters="false"
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
    default: '燃气设备'
  },
  payload: {
    type: Object,
    default: () => ({})
  }
})

const title = computed(() => props.title || '燃气设备')
const isWarning = computed(() => props.payload?.isWarning === true)
const BASE_URL = 'http://23.99.16.179:11001'

const columns = [
  { title: '管网公司', key: 'company', width: 160 },
  { title: '位置标识', key: 'locationCode', width: 110 },
  { title: '安装方式', key: 'installType', width: 110 },
  { title: '状态标记', key: 'statusMark', width: 100 },
  { title: '安装时间', key: 'installTime', width: 150 },
  { title: '设备名称', key: 'deviceName', width: 160 },
  {
    title: '设备ID',
    key: 'deviceId',
    width: 150,
    render(row: any) {
      return h('span', { class: 'link-cell' }, row.deviceId)
    }
  },
  { title: '街道', key: 'street', width: 110 },
  { title: '安装位置', key: 'installPosition', width: 240 }
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
  const deviceId = row.deviceId || row.deviceCode || row.platformCode || '--'
  const installPosition = row.installPosition || row.installLocation || row.installAddress || row.deviceAddress || '--'

  return {
    ...row,
    key: row.id || deviceId || index,
    company: row.company || row.pipelineCompany || '--',
    locationCode: row.locationCode || row.customerId || row.installId || '--',
    installType: row.installType || '--',
    statusMark: row.statusMark || row.status || '正常',
    installTime: row.installTime || '--',
    deviceName: row.deviceName || row.vibrationMonitorName || '--',
    deviceId,
    deviceCode: row.deviceCode || deviceId,
    street: row.street || '--',
    installPosition,
    installAddress: installPosition,
    deviceAddress: installPosition,
    detailType: 'gas-leak',
    status: row.status || row.deviceStatus || row.networkStatus || (isWarning.value ? '处置中' : '已完成')
  }
}

async function fetchRows() {
  try {
    const res = await axios.post(`${BASE_URL}/api/boot/system/pipeline/appliance`, {
      index: 1,
      size: 1000,
      isWarning: isWarning.value,
      warningType: isWarning.value ? '2' : ''
    })
    rows.value = getResponseList(res?.data).map(normalizeRow)
  } catch (error) {
    console.error('获取燃气设备列表失败', error)
    rows.value = []
  }
}

watch(() => props.payload?.isWarning, fetchRows)

onMounted(fetchRows)
</script>

<script lang="ts">
export default {
  name: 'PipelineGasLeakList'
}
</script>
