<template>
  <PipelineListModal
    :title="title"
    :columns="columns"
    :rows="rows"
    :show-filters="false"
    :scroll-x="1320"
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
    default: '化粪池列表'
  },
  payload: {
    type: Object,
    default: () => ({})
  }
})
const title = computed(() => props.title || '化粪池列表')
const isWarning = computed(() => props.payload?.isWarning === true)
const BASE_URL = 'http://23.99.16.179:11001'

const columns = [
  {
    title: '设备编码',
    key: 'deviceCode',
    width: 170,
    render(row: any) {
      return h('span', { class: 'link-cell' }, row.deviceCode)
    }
  },
  { title: '设备名称', key: 'deviceName', width: 160 },
  { title: '型号名称', key: 'modelName', width: 140 },
  { title: '安装单位', key: 'installUnit', width: 220 },
  { title: '安装地址', key: 'installAddress', width: 260 },
  { title: '所属街道', key: 'street', width: 110 },
  { title: '经度', key: 'longitude', width: 110 },
  { title: '纬度', key: 'latitude', width: 110 },
  { title: '处理次数', key: 'handleNum', width: 100 },
  { title: '更新时间', key: 'updateTime', width: 160 }
]

const rows = ref<any[]>([])

function getResponseList(responseData: any) {
  const data = responseData?.data ?? responseData
  if (Array.isArray(data)) return data
  if (Array.isArray(data?.records)) return data.records
  if (Array.isArray(data?.content)) return data.content
  if (Array.isArray(data?.list)) return data.list
  if (Array.isArray(data?.rows)) return data.rows
  return []
}

function normalizeRow(row: any, index: number) {
  const deviceCode = row.dvcode || row.deviceCode || '--'
  const deviceName = row.dvname || row.deviceName || '--'
  const installAddress = row.mountAddress || row.installAddress || row.deviceAddress || '--'

  return {
    ...row,
    _raw: row,
    key: row.id || deviceCode || index,
    id: row.id,
    deviceCode,
    deviceId: row.id || deviceCode,
    deviceName,
    deviceType: row.modelname || row.dvjxcode || '化粪池监测',
    modelName: row.modelname || '--',
    installUnit: row.enfullname || '--',
    installAddress,
    deviceAddress: installAddress,
    street: row.street || '--',
    longitude: row.lon || row.longitude || '--',
    latitude: row.lat || row.latitude || '--',
    handleNum: row.handleNum ?? 0,
    updateTime: row.gxsj || '--',
    listImage: row.listImage || '',
    mountImage: row.mountimage || '',
    detailType: 'septic',
    status: Number(row.isWarning) === 1 ? '处置中' : '已完成'
  }
}

async function fetchRows() {
  try {
    const res = await axios.get(`${BASE_URL}/api/boot/system/fire/pipeline/list`, {
      params: {
        pageIndex: 1,
        pageSize: 1000,
        isWarning: isWarning.value ? 1 : 0
      }
    })
    rows.value = getResponseList(res?.data).map(normalizeRow)
  } catch (error) {
    console.error('获取化粪池列表失败', error)
    rows.value = []
  }
}

watch(() => props.payload?.isWarning, fetchRows)

onMounted(fetchRows)
</script>

<script lang="ts">
export default {
  name: 'PipelineSepticTankList'
}
</script>
