<template>
  <div class="gas-network-modal">
    <div class="modal-header">
      <div class="header-title">地下管网气体</div>
      <button class="close-btn" type="button" @click="emit('close')">×</button>
    </div>

    <div class="modal-body">
      <section class="panel panel--left">
        <div class="section-title">设备列表</div>
        <n-data-table
          class="pipeline-table"
          :columns="deviceColumns"
          :data="deviceRows"
          :bordered="false"
          :single-line="false"
          size="small"
          :row-class-name="deviceRowClassName"
        />
      </section>

      <section class="panel panel--right">
        <div class="section-title">实时监测</div>
        <div class="realtime-grid">
          <div class="realtime-card">
            <span>甲烷浓度：</span><strong>1250ppm</strong>
          </div>
          <div class="realtime-card">
            <span>温度：</span><strong>28.5℃</strong>
          </div>
        </div>

        <div class="section-title">数据趋势分析</div>
        <div class="chart-stack">
          <v-chart
            v-for="item in chartItems"
            :key="item.title"
            class="trend-chart"
            :option="getLineOption(item)"
            autoresize
          />
        </div>

        <div class="section-title">报警记录和处置工单</div>
        <n-data-table
          class="pipeline-table work-table"
          :columns="workColumns"
          :data="workRows"
          :bordered="false"
          :single-line="false"
          size="small"
        />
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { LineChart } from 'echarts/charts'
import { GridComponent, LegendComponent, TooltipComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import { NDataTable } from 'naive-ui'

use([LineChart, GridComponent, LegendComponent, TooltipComponent, CanvasRenderer])

const emit = defineEmits(['close'])

const deviceColumns = [
  { title: '序号', key: 'index', width: 70 },
  { title: '设备名称', key: 'name', minWidth: 210 },
  { title: '设备编码', key: 'code', width: 120 },
  { title: '所属街道', key: 'street', width: 150 }
]

const deviceRows = [
  { index: 1, name: '中山2路儿童医院对面天桥对面', code: '2087', street: '七星岗街道', active: true },
  { index: 2, name: '大黄路88号', code: '2089', street: '大坪街道', active: false }
]

const workColumns = [
  { title: '序号', key: 'index', width: 80 },
  { title: '报警时间', key: 'alarmTime', minWidth: 190 },
  { title: '处理时间', key: 'handleTime', minWidth: 190 }
]

const workRows = [
  { index: 1, alarmTime: '2025-01-27 23:36:10', handleTime: '2025-01-27 00:36:10' },
  { index: 2, alarmTime: '2025-01-27 23:36:10', handleTime: '2025-01-27 00:36:10' },
  { index: 3, alarmTime: '2025-01-27 23:36:10', handleTime: '2025-01-27 00:36:10' },
  { index: 4, alarmTime: '2025-01-27 23:36:10', handleTime: '2025-01-27 00:36:10' }
]

const xData = [
  '2025-01-27 23:36:19',
  '2025-02-06 20:10:13',
  '2025-02-15 14:39:20',
  '2025-02-18 02:33:42',
  '2025-03-01 23:35:24',
  '2025-04-07 08:23:52',
  '2025-05-09 16:03:03',
  '2025-04-24 23:43:24'
]

const chartItems = [
  {
    title: '甲烷浓度（0~0.8%vol）',
    max: 0.3,
    interval: 0.05,
    data: [0.02, 0.25, 0.24, 0.26, 0.23, 0.26, 0.24, 0.25]
  },
  {
    title: '温度℃',
    max: 50,
    interval: 10,
    data: [22, 31, 18, 36, 24, 27, 21, 34]
  },
  {
    title: '排气阀工作状态',
    max: 1.2,
    interval: 0.2,
    data: [1.05, 0.02, 0.02, 0.02, 1.05, 0.02, 1.05, 1.05]
  }
]

function deviceRowClassName(row) {
  return row.active ? 'is-active-row' : ''
}

function getLineOption(item) {
  return {
    animation: false,
    backgroundColor: '#064581',
    color: ['#00ff34'],
    tooltip: {
      trigger: 'axis',
      confine: true,
      backgroundColor: 'rgba(4, 24, 48, 0.92)',
      borderColor: 'rgba(95, 246, 255, 0.45)',
      textStyle: {
        color: '#e8f8ff',
        fontSize: 11
      }
    },
    legend: {
      top: 4,
      left: 'center',
      itemWidth: 12,
      itemHeight: 3,
      textStyle: {
        color: '#d6ecff',
        fontSize: 10,
        fontWeight: 600
      },
      data: [item.title]
    },
    grid: {
      left: 34,
      right: 18,
      top: 26,
      bottom: 20
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: xData,
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
        color: '#d7e9ff',
        fontSize: 8,
        interval: 0,
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
          color: 'rgba(210, 232, 245, 0.48)',
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
        color: '#d7e9ff',
        fontSize: 9
      }
    },
    series: [
      {
        name: item.title,
        type: 'line',
        data: item.data,
        symbol: 'none',
        lineStyle: {
          width: 2,
          color: '#00ff34'
        },
        itemStyle: {
          color: '#00ff34'
        }
      }
    ]
  }
}
</script>

<script lang="ts">
export default {
  name: 'PipelineGasNetworkDetail'
}
</script>

<style lang="scss" scoped>
.gas-network-modal {
  position: absolute;
  transform: translate(-50%, -50%);
  z-index: 1001;
  width: 1180px;
  height: 850px;
  display: flex;
  flex-direction: column;
  background: linear-gradient(180deg, rgba(6, 36, 76, 0.98) 0%, rgba(2, 12, 36, 0.98) 100%);
  border: 1px solid rgba(37, 134, 255, 0.25);
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.45);
  color: #b8d9ff;
  overflow: hidden;
  pointer-events: auto;

  &::before {
    content: '';
    position: absolute;
    inset: 0;
    background:
      linear-gradient(90deg, rgba(83, 174, 255, 0.08), transparent 26%, transparent 74%, rgba(83, 174, 255, 0.08)),
      radial-gradient(circle at 50% 0%, rgba(83, 174, 255, 0.18), transparent 36%);
    z-index: -1;
  }
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
  color: #cfe8ff;
  font-size: 22px;
  font-weight: 700;
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
  display: flex;
  align-items: center;
  justify-content: center;

  &:hover {
    color: #fff;
    border-color: rgba(83, 174, 255, 0.6);
    background: rgba(24, 92, 179, 0.6);
  }
}

.modal-body {
  flex: 1;
  min-height: 0;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  padding: 14px 18px 18px;
  box-sizing: border-box;
}

.panel {
  min-width: 0;
  min-height: 0;
  padding: 12px 16px;
  border: 1px solid rgba(83, 174, 255, 0.25);
  background: rgba(8, 44, 86, 0.48);
  box-sizing: border-box;
}

.panel--left {
  overflow: hidden;
}

.panel--right {
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.section-title {
  position: relative;
  height: 26px;
  padding-left: 28px;
  color: #d9f7ff;
  font-size: 18px;
  font-weight: 500;
  line-height: 26px;
}

.section-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  transform: translateY(-50%);
  background: #10bdf5;
  box-shadow: 0 0 8px rgba(16, 189, 245, 0.78);
}

.realtime-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
  margin: 8px 0 12px;
}

.realtime-card {
  height: 62px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #d7e9ff;
  font-size: 14px;
  background:#064581;
  border: 1px solid rgba(139, 188, 230, 0.44);
}

.realtime-card strong {
  color: #fff;
  font-weight: 500;
}

.chart-stack {
  display: grid;
  grid-template-rows: repeat(3, 1fr);
  gap: 10px;
  height: 360px;
  margin: 8px 0 12px;
}

.trend-chart {
  width: 100%;
  height: 100%;
  background: #064581;
}

.work-table {
  flex: 1;
  min-height: 0;
  margin-top: 8px;
}

:deep(.pipeline-table) {
  height: auto;

  .n-data-table-wrapper,
  .n-data-table-table {
    background: transparent;
  }

  .n-data-table-table {
    font-size: 14px;
  }

  .n-data-table-th {
    height: 40px;
    padding: 0 4px;
    color: #8bbce6;
    font-weight: 500;
    background: #0b437c;
    border: none;
  }

  .n-data-table-td {
    height: 40px;
    padding: 6px 4px;
    color: #d7e9ff;
    text-align: center;
    background: #0a325f;
    border: none;
  }

  .n-data-table-tr:nth-child(even) .n-data-table-td {
    background: #19406b;
  }

  .n-data-table-tr.is-active-row .n-data-table-td {
    color: #001a2a;
    background: #1fc4e8;
  }

  .n-data-table-tr:hover .n-data-table-td {
    background: rgba(29, 107, 202, 0.42);
  }

  .n-data-table-tr.is-active-row:hover .n-data-table-td {
    background: #1fc4e8;
  }
}
</style>
