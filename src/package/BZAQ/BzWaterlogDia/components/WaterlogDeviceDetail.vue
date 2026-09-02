<template>
  <div class="waterlog-device-detail">
    <div class="modal-header">
      <div class="header-title">{{ title }}</div>
      <button class="close-btn" @click="handleClose"></button>
    </div>

    <div class="modal-body">
      <aside class="info-panel">
        <div class="panel-title">设备信息</div>
        <div
          v-for="item in deviceInfo"
          :key="item.label"
          class="info-row"
        >
          <span class="label">{{ item.label }}：</span>
          <span class="value" :title="item.value">{{ item.value }}</span>
        </div>
      </aside>

      <main class="video-panel">
        <div class="video-overlay-time">{{ videoTime }}</div>
        <div class="video-overlay-watermark">重庆市住建委排水中心<br>宇视君 严禁泄密</div>
        <div class="video-overlay-location">{{ videoLocation }}</div>
        <video
          class="video-placeholder"
          controls
          poster="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg=="
        >
          <source src="#" type="video/mp4">
          您的浏览器不支持视频播放
        </video>
      </main>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue'

const props = defineProps({
  title: {
    type: String,
    default: '设备名称-牛角沱板凳桥立交'
  }
})

const emit = defineEmits(['close'])

const deviceInfo = [
  { label: '设备编码', value: '50010301321203923910' },
  { label: '所属区县', value: '渝中区' },
  { label: '感知类型', value: '摄像头' },
  { label: '设备名称', value: '牛角沱板凳桥立交' },
  { label: '所属单位', value: '重庆市渝中区住房和城乡建设委员会' },
  { label: '设备风险点', value: '渝中区上清寺街道上清寺路牛角沱立交' },
  { label: '设备状态', value: '-' }
]

const videoTime = '2026年06月02日 星期二 16:00:12'
const videoLocation = '牛角沱立交（富家园小区）'

function handleClose() {
  emit('close')
}
</script>

<script>
export default {
  name: 'WaterlogDeviceDetail'
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

.waterlog-device-detail {
  font-family: 'Alibaba PuHuiTi 2.0', sans-serif;
  position: absolute;
  left: 50%;
  top: 50%;
  z-index: 1001;
  width: 1159px;
  height: 592px;
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
  width: 100%;
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
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 18px;
  height: calc(100% - 48px);
  padding: 32px 36px 34px;
  box-sizing: border-box;
}

.info-panel {
  width: 100%;
  height: 100%;
  padding: 0;
  box-sizing: border-box;
  background: url('../img/bg_left_top01.png') no-repeat center / 100% 100%;
  overflow: hidden;
}

.panel-title {
  height: 32px;
  display: flex;
  align-items: center;
  padding-left: 16px;
  margin-bottom: 18px;
  font-family: "Alibaba PuHuiTi 2.0", sans-serif;
  font-size: 14px;
  font-style: normal;
  font-weight: 500;
  line-height: 14px;
  background: url('../img/bg_list_data-1.png') left center / cover no-repeat;
  color: #8EC9FF;
}

.info-row {
  display: flex;
  align-items: flex-start;
  padding: 0 16px;
  min-height: 24px;
  line-height: 20px;
  margin-bottom: 13px;
  font-family: "Alibaba PuHuiTi 2.0", sans-serif;
  font-size: 14px;
  font-style: normal;
  font-weight: 500;
}

.label {
  flex-shrink: 0;
  width: 88px;
  color: #749dc0;
}

.value {
  flex: 1;
  color: #ffffff;
  min-width: 0;
  word-break: break-all;
  overflow-wrap: break-word;
}

.video-panel {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
  background: #1a2a35;
}

.video-placeholder {
  width: 100%;
  height: 100%;
  object-fit: cover;
  background: #000;
}

.video-overlay-time {
  position: absolute;
  left: 20px;
  top: 16px;
  z-index: 5;
  color: #333333;
  font-family: "Alibaba PuHuiTi 2.0", sans-serif;
  font-size: 16px;
  font-weight: 600;
  text-shadow: 0 1px 2px rgba(255, 255, 255, 0.6);
}

.video-overlay-watermark {
  position: absolute;
  right: 200px;
  top: 50%;
  z-index: 5;
  transform: rotate(-25deg);
  color: rgba(255, 255, 255, 0.25);
  font-family: "Alibaba PuHuiTi 2.0", sans-serif;
  font-size: 18px;
  font-weight: 600;
  line-height: 1.6;
  text-align: center;
  pointer-events: none;
}

.video-overlay-location {
  position: absolute;
  right: 20px;
  bottom: 20px;
  z-index: 5;
  color: #ffffff;
  font-family: "Alibaba PuHuiTi 2.0", sans-serif;
  font-size: 14px;
  font-weight: 500;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.8);
}
</style>
