<template>
  <div class="waterlog-level-modal">
    <div class="modal-header">
      <div class="header-title">液位计监测详情</div>
      <button class="close-btn" @click="handleClose">×</button>
    </div>

    <!-- 上方设备信息 -->
    <div class="top-info">
      <div class="info-item" v-for="item in topInfoList" :key="item.key">
        <span class="label">{{ item.label }}：</span>
        <span class="value">{{ item.value }}</span>
      </div>
    </div>

    <!-- 下方图表区域 -->
    <div class="chart-section">
      <div class="section-title">设备信息</div>
      <div class="chart-container" ref="chartRef"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'

const emit = defineEmits(['close'])
const echarts = window.echarts
let chartInstance = null
const chartRef = ref(null)

// 上方设备信息
const topInfoList = ref([
  { key: 'name', label: '设备名称', value: '地埋液位计' },
  { key: 'code', label: '设备编码', value: '50010301321203923910' },
  { key: 'sn', label: '设备SN码', value: '-' },
  { key: 'status', label: '设备状态', value: '在线' },
  { key: 'source', label: '设备来源', value: 'IRS' },
  { key: 'level', label: '液位值', value: '0.00cm' },
  { key: 'address', label: '设备位置', value: '重庆市渝中区朝天门街道陕西路社区千厮门隧道' },
  { key: 'longitude', label: '经度', value: '106.58359' },
  { key: 'latitude', label: '纬度', value: '106.58359' }
])

// 图表静态数据
const chartData = ref([
  { time: '2026-06-01 11:22:22', value: 25 },
  { time: '2026-06-01 11:22:22', value: 35 },
  { time: '2026-06-01 11:22:22', value: 33 },
  { time: '2026-06-01 11:22:22', value: 30 },
  { time: '2026-06-01 11:22:22', value: 15 },
  { time: '2026-06-01 11:22:22', value: 42 },
  { time: '2026-06-01 11:22:22', value: 40 },
  { time: '2026-06-01 11:22:22', value: 38 },
  { time: '2026-06-01 11:22:22', value: 41 }
])

/**
 * 初始化图表
 */
function initChart() {
  if (!chartRef.value || !echarts) return
  if (chartInstance) {
    chartInstance.dispose()
  }
  chartInstance = echarts.init(chartRef.value)
  updateChart()
}

/**
 * 更新图表数据
 */
function updateChart() {
  if (!chartInstance) return
  
  const xAxisData = chartData.value.map(item => item.time)
  const seriesData = chartData.value.map(item => item.value)

  const option = {
    grid: {
      left: 50,
      right: 30,
      top: 20,
      bottom: 35
    },
    xAxis: {
      type: 'category',
      data: xAxisData,
      axisLine: {
        lineStyle: {
          color: '#2B7BBD'
        }
      },
      axisLabel: {
        color: '#8EC9FF',
        fontSize: 11,
        interval: 4,
        formatter: (value) => value.slice(5, 16)
      },
      splitLine: {
        show: false
      }
    },
    yAxis: {
      type: 'value',
      name: '单位 cm',
      nameLocation: 'end',
      nameGap: 5,
      nameTextStyle: {
        color: '#50E3C2',
        fontSize: 12
      },
      axisLine: {
        show: false
      },
      axisTick: {
        show: false
      },
      axisLabel: {
        color: '#8EC9FF',
        fontSize: 12
      },
      splitLine: {
        lineStyle: {
          color: 'rgba(43, 123, 189, 0.3)',
          type: 'dashed'
        }
      },
      max: 80,
      interval: 40
    },
    series: [
      {
        data: seriesData,
        type: 'bar',
        barWidth: 24,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#45C8FF' },
            { offset: 1, color: '#025899' }
          ]),
          borderRadius: [2, 2, 0, 0]
        }
      }
    ]
  }

  chartInstance.setOption(option, true)
}

/**
 * 图表自适应
 */
function handleChartResize() {
  chartInstance?.resize()
}

/**
 * 关闭弹窗
 */
function handleClose() {
  // 关闭时销毁图表
  chartInstance?.dispose()
  chartInstance = null
  emit('close')
}

onMounted(() => {
  nextTick(() => {
    initChart()
    window.addEventListener('resize', handleChartResize)
  })
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleChartResize)
  chartInstance?.dispose()
  chartInstance = null
})
</script>

<script>
export default {
  name: "WaterlogLevelMonitor"
}
</script>

<style lang="scss" scoped>
@font-face {
  font-family: 'AlibabaPuHuiTi';
  src: url('../font/Alibaba_PuHuiTi_2.0_55_Regular_55_Regular.ttf') format('truetype');
  font-weight: normal;
  font-style: normal;
}

@font-face {
  font-family: 'D-DIN';
  src: url('../font/D-DIN.otf') format('opentype');
  font-weight: normal;
  font-style: normal;
}

.waterlog-level-modal {
  font-family: 'AlibabaPuHuiTi', sans-serif;
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  z-index: 1001;
  width: 992px;
  height: 464px;
  display: flex;
  flex-direction: column;
  background:
    url('../img/Group-2136640490-1.png') top center / 100% 60px no-repeat,
    url('../img/Rectangle-346242153.png') center / 100% 100% no-repeat;
  border: 1px solid rgba(37, 134, 255, 0.25);
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.45);
  color: #b8d9ff;
  font-size: 14px;
  overflow: hidden;
  pointer-events: auto;
}

.modal-header {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  flex-shrink: 0;
  padding: 0 32px;

  .header-title {
    background: linear-gradient(180deg, #FFFFFF 0%, #5FBCFF 100%);
    -webkit-background-clip: text;
    background-clip: text;
    color: transparent;
    font-size: 22px;
    font-weight: 700;
    text-shadow: 0 0 12px rgba(95, 188, 255, 0.3);
  }

  .close-btn {
    position: absolute;
    right: 20px;
    top: 50%;
    transform: translateY(-50%);
    width: 32px;
    height: 32px;
    border: 1px solid rgba(69, 200, 255, 0.3);
    background: rgba(16, 64, 126, 0.4);
    color: #7AA7CF;
    font-size: 20px;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;

    &:hover {
      color: #ffffff;
      border-color: rgba(69, 200, 255, 0.6);
      background: rgba(24, 92, 179, 0.6);
    }
  }
}

/* 上方设备信息 */
.top-info {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  padding: 12px 32px 10px 32px;
  margin-bottom: 15px;
  gap: 8px 40px;

  .info-item {
    line-height: 28px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;

    .label {
      color: #8EC9FF;
    }

    .value {
      color: #CFE8FF;
      font-family: 'D-DIN', 'AlibabaPuHuiTi', sans-serif;
    }
  }
}

/* 下方图表区域 */
.chart-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 0 32px 24px 32px;

  .section-title {
    width: 928px;
    height: 40px;
    line-height: 40px;
    font-size: 16px;
    color: #8EC9FF;
    font-weight: 500;
    padding-left: 12px;
    background: url('../img/title.png') left center / cover no-repeat;
  }

  .chart-container {
    width: 928px;
    height: 193px;
    background: url('../img/backg.png') center / 100% 100% no-repeat;
  }
}
</style>