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
    default: '地下管网气体设备'
  },
  payload: {
    type: Object,
    default: () => ({})
  }
})

const title = computed(() => props.title || '地下管网气体设备')
const isWarning = computed(() => props.payload?.isWarning === true)
const BASE_URL = 'http://23.99.16.179:11001'

const columns = [
  { title: '设备名称', key: 'deviceName', width: 220 },
  {
    title: '设备编码',
    key: 'deviceCode',
    width: 110,
    render(row: any) {
      return h('span', { class: 'link-cell' }, row.deviceCode)
    }
  },
  { title: '设备类型', key: 'deviceType', width: 150 },
  { title: '安装地址', key: 'installAddress', width: 220 },
  { title: '安装时间', key: 'installTime', width: 120 },
  { title: '所属街道', key: 'street', width: 130 },
  { title: '建设项目名称', key: 'projectName', width: 180 },
  { title: '终端所属部门', key: 'department', width: 190 }
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
  const deviceCode = row.deviceCode || row.deviceId || row.platformCode || row.code || '--'
  const installAddress = row.installAddress || row.deviceAddress || row.detailAddress || row.address || row.location || '--'

  return {
    ...row,
    key: row.id || deviceCode || index,
    deviceName,
    deviceCode,
    deviceType: row.deviceType || '--',
    installAddress,
    deviceAddress: installAddress,
    installTime: row.installTime || '--',
    street: row.street || '--',
    projectName: row.projectName || row.affiliationProject || '--',
    department: row.department || row.affiliationUnit || '--',
    detailType: 'gas-network',
    status: row.status || row.deviceStatus || row.networkStatus || (isWarning.value ? '处置中' : '已完成')
  }
}

async function fetchRows() {
  try {
    const res = await axios.post(`${BASE_URL}/api/boot/system/pipeline/gasMonitor`, {
      index: 1,
      size: 1000,
      isWarning: isWarning.value
    })
    rows.value = getResponseList(res?.data).map(normalizeRow)
  } catch (error) {
    console.error('获取地下管网气体监测列表失败', error)
    rows.value = []
  }
}

watch(() => props.payload?.isWarning, fetchRows)

onMounted(fetchRows)
</script>

<script lang="ts">
export default {
  name: 'PipelineGasNetworkList'
}
</script>
