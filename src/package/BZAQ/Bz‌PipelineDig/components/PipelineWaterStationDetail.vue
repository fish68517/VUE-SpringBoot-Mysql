<template>
  <div class="water-detail-modal">
    <div class="modal-header">
      <div class="header-title">{{ pointData.label || pointData.name || '水质监测站详情' }}</div>
      <button class="close-btn" type="button" @click="$emit('close')">×</button>
    </div>

    <div class="modal-body">
      <section class="detail-section">
        <table class="info-table">
          <thead>
            <tr>
              <th>设备编号</th>
              <th>设备类型</th>
              <th>设备地址</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>{{ pointData.deviceCode || 'YZQ-SZ-002' }}</td>
              <td>{{ pointData.deviceType || '地表水小型自动监测站（嘉陵江流域朝天门站）' }}</td>
              <td>{{ pointData.deviceAddress || '重庆市渝中区朝天门街道朝千路社区朝天门6号码头' }}</td>
            </tr>
          </tbody>
        </table>
      </section>

      <section v-for="item in chartSections" :key="item.title" class="detail-section chart-section">
        <v-chart class="detail-chart" :option="getLineOption(item)" autoresize />
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import axios from 'axios'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { LineChart } from 'echarts/charts'
import { GridComponent, LegendComponent, TooltipComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'

use([LineChart, GridComponent, LegendComponent, TooltipComponent, CanvasRenderer])

const props = defineProps({
  point: {
    type: Object,
    default: () => ({})
  }
})

defineEmits(['close'])

const BASE_URL = 'http://23.99.16.179:11001'
const detailData = ref({})
const pointData = computed(() => ({ ...(props.point || {}), ...detailData.value }))
const detailId = computed(() => props.point?.id || '')

function normalizeDetail(data: any) {
  if (!data) return {}
  return {
    ...data,
    label: data.deviceName || data.name,
    deviceType: data.type || data.deviceName,
    deviceAddress: data.installAddress || data.detailAddress || data.address
  }
}

async function fetchDetail() {
  if (!detailId.value) {
    detailData.value = {}
    return
  }

  try {
    const res = await axios.post(`${BASE_URL}/api/boot/system/pipeline/waterQualityStationDetail`, {
      id: detailId.value,
      startTime: '',
      endTime: ''
    })
    detailData.value = normalizeDetail(res?.data?.data || {})
  } catch (error) {
    console.error('获取水质监测站详情失败', error)
    detailData.value = {}
  }
}

watch(detailId, fetchDetail)

onMounted(fetchDetail)

const baseTimes = [
  '2025-01-24 02:10:03',
  '2025-01-28 22:10:06',
  '2025-02-02 06:10:03',
  '2025-02-06 14:10:04',
  '2025-02-10 14:10:05',
  '2025-02-14 22:10:04',
  '2025-02-19 02:10:25',
  '2024-12-02 18:33:48'
]

const chartSections = [
  {
    title: '氨氮浓度（0~0.5mg/L）',
    max: 2,
    interval: 0.5,
    data: [0.4, 0.52, 0.46, 0.5, 0.55, 0.04, 0.03, 0.02],
    redData: [null, 1.65, null, null, null, null, null, null]
  },
  {
    title: '总磷浓度（0~0.1mg/L）',
    max: 1,
    interval: 0.2,
    data: [0.02, 0.04, 0.03, 0.03, 0.04, 0.03, 0.02, 0.03],
    redData: [null, 0.84, null, null, null, null, null, null]
  },
  {
    title: 'PH值（6~9）',
    max: 10,
    interval: 2,
    data: [8.0, 8.05, 8.1, 8.08, 8.12, 8.16, 8.22, 8.28],
    redData: []
  },
  {
    title: '高锰酸钾指数（0~4mg/L）',
    max: 10,
    interval: 2,
    data: [0, 2.1, 2.2, 2.15, 2.35, 2.05, 2.3, 2.6],
    redData: [null, 8.2, null, null, null, null, null, null]
  }
]

const getLineOption = item => ({
  animation: false,
  backgroundColor: '#064581',
  color: ['#00ff34', '#ff2828'],
  tooltip: {
    trigger: 'axis',
    confine: true,
    backgroundColor: 'rgba(4, 24, 48, 0.92)',
    borderColor: 'rgba(95, 246, 255, 0.45)',
    textStyle: {
      color: '#e8f8ff',
      fontSize: 12
    }
  },
  legend: {
    top: 12,
    left: 'center',
    itemWidth: 14,
    itemHeight: 3,
    textStyle: {
      color: '#d6ecff',
      fontSize: 13,
      fontWeight: 600
    },
    data: [item.title]
  },
  grid: {
    left: 60,
    right: 70,
    top: 48,
    bottom: 34
  },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: baseTimes,
    axisLine: {
      lineStyle: {
        color: 'rgba(139, 188, 230, 0.8)'
      }
    },
    axisTick: {
      show: true,
      lineStyle: {
        color: 'rgba(139, 188, 230, 0.55)'
      }
    },
    axisLabel: {
      color: '#f0f8ff',
      fontSize: 14,
      interval: 0,
      margin: 6,
      hideOverlap: true
    }
  },
  yAxis: {
    type: 'value',
    min: 0,
    max: item.max,
    interval: item.interval,
    splitLine: {
      lineStyle: {
        color: 'rgba(210, 232, 245, 0.62)',
        type: 'dashed'
      }
    },
    axisLine: {
      show: false
    },
    axisTick: {
      show: false
    },
    axisLabel: {
      color: '#f0f8ff',
      fontSize: 14,
      margin: 8
    }
  },
  series: [
    {
      name: item.title,
      type: 'line',
      data: item.data,
      symbol: 'none',
      lineStyle: {
        width: 3,
        color: '#00ff34'
      }
    },
    {
      name: '超标',
      type: 'line',
      data: item.redData,
      symbol: 'none',
      connectNulls: true,
      lineStyle: {
        width: 3,
        color: '#ff2828'
      },
      itemStyle: {
        color: '#ff2828'
      },
      tooltip: {
        show: false
      }
    }
  ]
})
</script>

<script lang="ts">
export default {
  name: 'PipelineWaterStationDetail'
}
</script>

<style lang="scss" scoped>
.water-detail-modal {
  position: absolute;
  transform: translate(-50%, -50%);
  z-index: 1001;
  width: 1200px;
  height: 960px;
  display: flex;
  flex-direction: column;
  background: linear-gradient(180deg, rgba(6, 36, 76, 0.98) 0%, rgba(2, 12, 36, 0.98) 100%);
  border: 1px solid rgba(37, 134, 255, 0.25);
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.45);
  color: #d7e9ff;
  overflow: hidden;
  pointer-events: auto;
}

.modal-header {
  height: 48px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  padding: 0 56px;
  background: linear-gradient(90deg, rgba(12, 73, 138, 0.55), rgba(4, 34, 76, 0.2), rgba(12, 73, 138, 0.55));
  border-bottom: 1px solid rgba(83, 174, 255, 0.22);
}

.header-title {
  max-width: 100%;
  color: #cfe8ff;
  font-size: 22px;
  font-weight: 700;
  text-align: center;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  text-shadow: 0 0 12px rgba(83, 174, 255, 0.5);
}

.close-btn {
  position: absolute;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
  width: 32px;
  height: 32px;
  border: 1px solid rgba(83, 174, 255, 0.3);
  background: rgba(16, 64, 126, 0.4);
  color: #aed5ff;
  font-size: 20px;
  cursor: pointer;
}

.modal-body {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  padding: 14px 24px 24px;
  box-sizing: border-box;

  &::-webkit-scrollbar {
    width: 8px;
  }

  &::-webkit-scrollbar-thumb {
    background: rgba(71, 168, 255, 0.48);
    border-radius: 8px;
  }

  &::-webkit-scrollbar-track {
    background: rgba(255, 255, 255, 0.08);
  }
}

.detail-section {
  margin-bottom: 16px;
}

.info-table {
  width: 100%;
  border-collapse: collapse;
  table-layout: fixed;
  background: #064581;
}

.info-table th,
.info-table td {
  color: #f0f8ff;
  text-align: center;
  font-size: 16px;
  font-weight: 700;
}

.info-table th {
  height: 52px;
}

.info-table td {
  height: 92px;
  padding: 0 12px;
  line-height: 1.5;
}

.info-table th:nth-child(1),
.info-table td:nth-child(1) {
  width: 27%;
}

.info-table th:nth-child(2),
.info-table td:nth-child(2) {
  width: 20%;
}

.chart-section {
  height: 238px;
}

.detail-chart {
  width: 100%;
  height: 100%;
}
</style>
