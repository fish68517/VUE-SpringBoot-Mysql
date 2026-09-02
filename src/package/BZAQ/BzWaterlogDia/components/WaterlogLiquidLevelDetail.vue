<template>
  <div class="waterlog-liquid-level-detail">
    <div class="modal-header">
      <div class="header-title">{{ title }}</div>
      <button class="close-btn" @click="handleClose"></button>
    </div>

    <div class="modal-body">
      <div class="device-info-grid">
        <div class="info-item" v-for="item in deviceInfoTop" :key="item.label">
          <span class="label">{{ item.label }}：</span>
          <span class="value">{{ item.value }}</span>
        </div>
      </div>

      <div class="chart-panel">
        <div class="chart-title"><span>设备信息</span></div>
        <div class="chart-container">
          <div class="chart-unit">单位 <span class="unit-value">cm</span></div>
          <div class="chart-y-axis">
            <div class="y-tick"><span>80</span></div>
            <div class="y-tick"><span>40</span></div>
            <div class="y-tick"><span>0</span></div>
          </div>
          <div class="chart-area">
            <div class="grid-lines">
              <div class="grid-line"></div>
              <div class="grid-line"></div>
              <div class="grid-line"></div>
            </div>
            <div class="bars">
              <div class="bar" v-for="(val, idx) in chartData" :key="idx" :style="{ height: val + '%' }"></div>
            </div>
            <div class="x-axis">
              <div class="x-tick" v-for="(time, idx) in chartTimes" :key="idx">{{ time }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue'

const props = defineProps({
  title: {
    type: String,
    default: '液位计监测详情'
  }
})

const emit = defineEmits(['close'])

const deviceInfoTop = [
  { label: '设备名称', value: '地埋液位计' },
  { label: '设备编码', value: '50010301321203923910' },
  { label: '设备SN码', value: '-' },
  { label: '设备状态', value: '在线' },
  { label: '设备来源', value: 'IRS' },
  { label: '液位值', value: '0.00cm' },
  { label: '设备位置', value: '重庆市渝中区朝天门街道陕西路社区千厮门隧道' },
  { label: '经度', value: '106.58359' },
  { label: '纬度', value: '106.58359' }
]

const chartData = [20, 30, 35, 28, 10, 45, 42, 43, 44, 8]
const chartTimes = [
  '2026-06-01 11:22:22',
  '2026-06-01 11:22:22',
  '2026-06-01 11:22:22'
]

function handleClose() {
  emit('close')
}
</script>

<script>
export default {
  name: 'WaterlogLiquidLevelDetail'
}
</script>

<style lang="scss" scoped>
@font-face {
  font-family: 'Alibaba PuHuiTi 2.0';
  src: url('../font/Alibaba_PuHuiTi_2.0_55_Regular_55_Regular.ttf') format('truetype');
  font-weight: 400;
  font-style: normal;
}

@font-face {
  font-family: 'Alibaba PuHuiTi 2.0';
  src: url('../font/Alibaba_PuHuiTi_2.0_75_SemiBold_75_SemiBold.ttf') format('truetype');
  font-weight: 600;
  font-style: normal;
}

.waterlog-liquid-level-detail {
  font-family: 'Alibaba PuHuiTi 2.0', sans-serif;
  position: absolute;
  left: 50%;
  top: 50%;
  z-index: 1001;
  width: 992px;
  height: 464px;
  transform: translate(-50%, -50%);
  color: #d8ecff;
  background: url('../img/Rectangle-346242153.png') no-repeat center / 100% 100%;
  border: 1px solid #a6ceff4d;
  box-shadow: 0 16px 56px rgba(0, 0, 0, 0.55);
  overflow: hidden;
  pointer-events: auto;
}

.modal-header {
  position: relative;
  width: calc(100% - 8px);
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background:
    linear-gradient(90deg, #0b3b7000 0.2%, #2e6bdc59 50.04%, #0c2d5c00 99.87%),
    url('../img/waterlog_title_bg.png') no-repeat center / 100% 100%;
}

.header-title {
  text-align: center;
  font-family: "Alibaba PuHuiTi 2.0", sans-serif;
  font-size: 20px;
  font-style: normal;
  font-weight: 500;
  line-height: normal;
  background: linear-gradient(0deg, #5FBCFF 1.4%, #FFF 58.67%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.close-btn {
  position: absolute;
  right: 20px;
  top: 8px;
  width: 34px;
  height: 34px;
  border: none;
  background: url('../img/close.png') no-repeat center / 100% 100%;
  cursor: pointer;
}

.modal-body {
  height: calc(100% - 48px);
  padding: 30px 32px 34px;
  box-sizing: border-box;
}

.device-info-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px 24px;
  margin-bottom: 24px;
}

.info-item {
  display: flex;
  align-items: flex-start;
  font-family: "Alibaba PuHuiTi 2.0", sans-serif;
  font-size: 14px;
  font-style: normal;
  font-weight: 500;
  line-height: 20px;
}

.info-item .label {
  flex-shrink: 0;
  color: #749dc0;
  min-width: 88px;
}

.info-item .value {
  flex: 1;
  color: #d8ecff;
  word-break: break-all;
}

.chart-panel {
  width: 928px;
  height: 193px;
  background: url('../img/bg_left_top01@2x.png') no-repeat center / 100% 100%;
  box-sizing: border-box;
}

.chart-title {
  width: 928px;
  height: 30px;
  display: flex;
  align-items: center;
  padding-left: 32px; /* Adjusted padding as background image might have decoration */
  background: url('../img/liquid_device_title.png') no-repeat center / 100% 100%;
  box-sizing: border-box;
}

.chart-title span {
  font-family: "Alibaba PuHuiTi 2.0", sans-serif;
  font-size: 14px;
  font-style: normal;
  font-weight: 500;
  line-height: 14px;
  background: linear-gradient(0deg, #5FBCFF 1.4%, #FFF 58.67%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.chart-container {
  height: 163px;
  position: relative;
  width: 100%;
}

.chart-unit {
  position: absolute;
  left: 20px;
  top: 10px;
  font-size: 12px;
  color: #749dc0;

  .unit-value {
    color: #3ce8ff;
  }
}

.chart-y-axis {
  position: absolute;
  left: 10px;
  top: 35px;
  bottom: 25px;
  width: 40px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.y-tick {
  font-size: 12px;
  color: #749dc0;
  text-align: right;
  padding-right: 8px;
}

.chart-area {
  position: absolute;
  left: 55px;
  top: 35px;
  right: 20px;
  bottom: 25px;
  border-bottom: 1px solid rgba(116, 157, 192, 0.3);
}

.grid-lines {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.grid-line {
  width: 100%;
  height: 1px;
  border-top: 1px dashed rgba(116, 157, 192, 0.3);
}

.bars {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: flex-end;
  justify-content: space-evenly;
  padding: 0 10px;
}

.bar {
  width: 24px;
  background: linear-gradient(180deg, #3ce8ff 0%, rgba(60, 232, 255, 0.2) 100%);
  border-radius: 2px 2px 0 0;
  transition: height 0.3s ease;
}

.x-axis {
  position: absolute;
  left: 0;
  right: 0;
  bottom: -22px;
  display: flex;
  justify-content: space-between;
}

.x-tick {
  font-size: 12px;
  color: #d8ecff;
  white-space: nowrap;
}
</style>
