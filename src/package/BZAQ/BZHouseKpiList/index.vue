<template>
  <div v-if="visible" class="house-kpi-modal" :style="containerStyle">
    <div class="modal-header">
      <div class="header-title">场所关键KPI</div>
      <button class="close-btn" type="button" @click="handleClose">×</button>
    </div>

    <div class="modal-content">
      <div class="summary-row">
        <div v-for="item in summaryCards" :key="item.key" class="summary-card">
          <div class="summary-icon">
            <img src="./img/icon_response_time.png" alt="" />
          </div>
          <div class="summary-label">{{ item.label }}</div>
          <div class="summary-value">
            <span>{{ item.rate }}</span>
            <em>{{ item.countText }}</em>
          </div>
        </div>
      </div>

      <div class="table-wrap">
        <n-data-table
          class="house-kpi-table"
          :columns="columns"
          :data="tableRows"
          :bordered="false"
          :flex-height="true"
          :single-line="false"
          size="small"
        />
      </div>

      <div class="total-row">
        <div class="total-cell total-cell--index"></div>
        <div class="total-cell total-cell--street">合计</div>
        <div class="total-cell">{{ totals.online }}</div>
        <div class="total-cell">{{ totals.group }}</div>
        <div class="total-cell">{{ totals.business }}</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, h, onBeforeUnmount, onMounted, ref } from 'vue'
import { NDataTable } from 'naive-ui'
import axios from 'axios'

const BASE_URL = 'http://23.99.16.86:11001'
const RATE_API = `${BASE_URL}/api/v1/system/management/allHouseManagement/rate`

type HouseTypeKey = 'online' | 'group' | 'business'

type SubHouse = {
  sourceFrom?: string
  from?: string
  managedCount?: number | string
  currentCount?: number | string
  applyingCount?: number | string
  unmanagedCount?: number | string
  managementRate?: number
}

const emit = defineEmits(['close'])
const props = defineProps({
  chartConfig: {
    type: Object,
    required: false,
    default: () => ({})
  },
  publicParamList: {
    type: Object,
    required: false,
    default: () => []
  },
  bus: {
    type: Object,
    required: false,
    default: null
  }
})

const sourceName = 'BZHouseKpiList'
const OPEN_EVENT = 'OPEN_HOUSE_KPI_LIST_DIA'
const CLOSE_EVENT = 'CLOSE_DIA'
const visible = ref(false)
const subHouseMap = ref<Record<HouseTypeKey, SubHouse>>({
  online: {},
  group: {},
  business: {}
})

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
  }
}

const houseTypeMeta = [
  { key: 'online' as HouseTypeKey, label: '网约房纳管率' },
  { key: 'group' as HouseTypeKey, label: '群租房纳管率' },
  { key: 'business' as HouseTypeKey, label: '商住改纳管率' }
]

const containerStyle = computed(() => {
  const width = Number(props.chartConfig?.attr?.w)
  const height = Number(props.chartConfig?.attr?.h)
  return {
    width: Number.isFinite(width) && width > 0 ? `${width}px` : '456px',
    height: Number.isFinite(height) && height > 0 ? `${height}px` : '620px'
  }
})

const summaryCards = computed(() => {
  return houseTypeMeta.map(item => {
    const data = subHouseMap.value[item.key] || {}
    const current = Number(data.managedCount ?? data.currentCount) || 0
    const applying = Number(data.applyingCount) || 0
    const unmanaged = Number(data.unmanagedCount) || 0
    return {
      ...item,
      rate: formatRateValue(data.managementRate, current, applying, unmanaged),
      countText: `${current}/${current + applying + unmanaged}`
    }
  })
})

const tableRows = [
  '七星岗街道',
  '解放碑街道',
  '两路口街道',
  '上清寺街道',
  '菜园坝街道',
  '南纪门街道',
  '朝天门街道',
  '大溪沟街道',
  '大坪街道',
  '化龙桥街道',
  '石油路街道'
].map((street, index) => ({
  key: street,
  index: index + 1,
  street,
  online: '26%↓10%',
  group: '26%↓10%',
  business: '26%↓10%'
}))

const totals = {
  online: '26%↑12.00%',
  group: '12%↑17.00%',
  business: '97%↑2.00%'
}

function formatRateValue(rate: any, managed: number, applying: number, unmanaged: number) {
  const numericRate = Number(rate)
  if (Number.isFinite(numericRate)) return `${(numericRate * 100).toFixed(2)}%`
  const total = managed + applying + unmanaged
  if (!total) return '0.00%'
  return `${((managed + applying) / total * 100).toFixed(2)}%`
}

function renderCell(value) {
  return h('span', { class: 'rate-cell' }, value || '-')
}

const columns = [
  {
    title: '序号',
    key: 'index',
    width: 48,
    align: 'center'
  },
  {
    title: '乡镇街道',
    key: 'street',
    width: 112,
    ellipsis: true
  },
  {
    title: '网约房纳管率',
    key: 'online',
    align: 'center',
    render(row) {
      return renderCell(row.online)
    }
  },
  {
    title: '群租房纳管率',
    key: 'group',
    align: 'center',
    render(row) {
      return renderCell(row.group)
    }
  },
  {
    title: '商住改纳管率',
    key: 'business',
    align: 'center',
    render(row) {
      return renderCell(row.business)
    }
  }
]

function getHouseTypeKey(from = ''): HouseTypeKey | null {
  const text = String(from).toLowerCase()
  if (/网约|online|wy/.test(text)) return 'online'
  if (/群租|群租空间|group|qz/.test(text)) return 'group'
  if (/商住|商改|biz|business|resi|sgz|c2r/.test(text)) return 'business'
  return null
}

async function fetchHouseRate() {
  try {
    const res = await axios.get(RATE_API)
    const data = res?.data?.data || {}
    const nextMap: Record<HouseTypeKey, SubHouse> = {
      online: {},
      group: {},
      business: {}
    }

    ;(data.subHouses || []).forEach((item: SubHouse) => {
      const type = getHouseTypeKey(item.sourceFrom || item.from)
      if (type) nextMap[type] = item
    })

    subHouseMap.value = nextMap
  } catch (err) {
    console.error('获取场所关键KPI纳管率失败', err)
  }
}

function handleClose() {
  visible.value = false
  emit('close')
}

function openByEvent(payload) {
  const params = payload?.params || {}
  const diaName = payload?.diaName || params.diaName
  if (diaName && diaName !== 'house-kpi-list') return
  visible.value = true
  fetchHouseRate()
}

function closeByEvent() {
  visible.value = false
}

onMounted(() => {
  fetchHouseRate()
  eventBus.on(OPEN_EVENT, openByEvent)
  eventBus.on(CLOSE_EVENT, closeByEvent)
})

onBeforeUnmount(() => {
  eventBus.off(OPEN_EVENT, openByEvent)
  eventBus.off(CLOSE_EVENT, closeByEvent)
})
</script>

<script lang="ts">
export default {
  name: 'BZHouseKpiList',
  version: '1.0.0'
}
</script>

<style lang="scss" scoped>
@font-face {
  font-family: 'HouseKpiRegular';
  src: url('../BZHouse/font/Alibaba_PuHuiTi_2.0_55_Regular_55_Regular.ttf') format('truetype');
  font-weight: normal;
  font-style: normal;
}

@font-face {
  font-family: 'HouseKpiDIN';
  src: url('../BZHouse/font/D-DIN.otf') format('opentype');
  font-weight: normal;
  font-style: normal;
}

.house-kpi-modal {
  position: relative;
  z-index: 1001;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  color: #b8d9ff;
  font-family: 'HouseKpiRegular', 'Microsoft YaHei', sans-serif;
  font-size: 14px;
  background:
    url('./img/Group_2136640490_1.png') top center / 100% 60px no-repeat,
    linear-gradient(180deg, rgba(3, 19, 54, 0.96) 0%, rgba(2, 12, 36, 0.96) 100%);
  border: 1px solid rgba(37, 134, 255, 0.25);
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.45);
  pointer-events: auto;
}

.house-kpi-modal::before {
  content: '';
  position: absolute;
  inset: 0;
  z-index: -1;
  background: url('./img/Rectangle_346242153_1.png') center / cover no-repeat;
  opacity: 0.25;
}

.modal-header {
  position: relative;
  height: 60px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 32px;
}

.header-title {
  color: #cfe8ff;
  font-size: 20px;
  font-weight: 700;
  text-shadow: 0 0 12px rgba(83, 174, 255, 0.5);
}

.close-btn {
  position: absolute;
  right: 20px;
  top: 50%;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  transform: translateY(-50%);
  border: 1px solid rgba(83, 174, 255, 0.3);
  background: rgba(16, 64, 126, 0.4);
  color: #aed5ff;
  font-size: 20px;
  cursor: pointer;
}

.close-btn:hover {
  color: #ffffff;
  border-color: rgba(83, 174, 255, 0.72);
  background: rgba(24, 92, 179, 0.62);
}

.modal-content {
  flex: 1;
  min-height: 0;
  padding: 20px 24px 0 24px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.summary-row {
  height: 112px;
  flex-shrink: 0;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 14px;
  align-items: center;
  padding: 0 18px;
  margin-bottom: 20px;
  background: rgba(11, 67, 124, 0.3);
  border: 1px solid rgba(37, 134, 255, 0.3);
  border-radius: 8px;
}

.summary-card {
  position: relative;
  height: 72px;
  min-width: 0;
  display: grid;
  grid-template-columns: 42px minmax(0, 1fr);
  grid-template-rows: 28px 30px;
  column-gap: 10px;
  align-items: center;
  padding: 0 12px;
  box-sizing: border-box;
  color: #b8d9ff;
  background: rgba(16, 64, 126, 0.32);
  border: 1px solid rgba(37, 134, 255, 0.28);
  border-radius: 4px;
}

.summary-icon {
  grid-row: 1 / 3;
  width: 42px;
  height: 42px;
}

.summary-icon img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.summary-label {
  min-width: 0;
  color: #b8d9ff;
  font-size: 14px;
  line-height: 20px;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.summary-value {
  display: flex;
  align-items: baseline;
  min-width: 0;
  gap: 8px;
  color: #b8d9ff;
  font-family: 'HouseKpiDIN', 'HouseKpiRegular', sans-serif;
  font-size: 14px;
  line-height: 26px;
}

.summary-value span {
  font-size: 24px;
  font-weight: 700;
  background: linear-gradient(180deg, #33f7ff 0%, #00f09e 100%);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

.summary-value em {
  color: #b8d9ff;
  font-size: 13px;
  font-style: normal;
}

.table-wrap {
  flex: 1;
  min-height: 0;
  overflow: hidden;
  margin-bottom: 0;
}

:deep(.house-kpi-table) {
  height: 100%;

  .n-data-table-wrapper,
  .n-data-table-base-table,
  .n-data-table-base-table-body,
  .n-data-table-table {
    background: transparent;
  }

  .n-data-table-wrapper,
  .n-data-table-base-table {
    height: 100%;
  }

  .n-data-table-base-table-body {
    overflow-y: auto;
    overflow-x: hidden;

    &::-webkit-scrollbar {
      width: 6px;
    }

    &::-webkit-scrollbar-track {
      background: rgba(8, 35, 74, 0.55);
      border-radius: 3px;
    }

    &::-webkit-scrollbar-thumb {
      background: rgba(64, 158, 255, 0.55);
      border-radius: 3px;

      &:hover {
        background: rgba(64, 158, 255, 0.8);
      }
    }
  }

  .n-data-table-table {
    table-layout: fixed;
    font-size: 14px;
  }

  .n-data-table-th {
    height: 40px;
    padding: 0 12px;
    color: #8bbce6;
    font-weight: 500;
    background: #0b437c;
    border: none;
    white-space: nowrap;
  }

  .n-data-table-td {
    height: 40px;
    padding: 0 12px;
    color: #d7e9ff;
    font-family: 'HouseKpiDIN', 'HouseKpiRegular', sans-serif;
    font-size: 14px;
    background: #0a3c70;
    border: none;
  }

  .n-data-table-tr:nth-child(even) .n-data-table-td {
    background: #19406b;
  }

  .n-data-table-tr:hover .n-data-table-td {
    background: rgba(29, 107, 202, 0.3);
  }
}

.rate-cell {
  display: inline-flex;
  align-items: baseline;
  gap: 0;
  color: #ffffff;
}

.total-row {
  height: 40px;
  flex-shrink: 0;
  display: grid;
  grid-template-columns: 48px 112px minmax(0, 1fr) minmax(0, 1fr) minmax(0, 1fr);
  background: #0b437c;
  margin-bottom: 20px;
}

.total-cell {
  min-width: 0;
  height: 40px;
  padding: 0 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  font-family: 'HouseKpiDIN', 'HouseKpiRegular', sans-serif;
  font-size: 14px;
  font-weight: 500;
  overflow: hidden;
  white-space: nowrap;
}

.total-cell--street {
  color: #cfe8ff;
  font-family: 'HouseKpiRegular', sans-serif;
}
</style>
