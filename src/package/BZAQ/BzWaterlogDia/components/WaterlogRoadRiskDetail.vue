<template>
  <div class="waterlog-road-risk-detail">
    <div class="modal-header">
      <div class="header-title">{{ title }}</div>
      <button class="close-btn" @click="handleClose"></button>
    </div>

    <div class="modal-body">
      <!-- 基础信息 -->
      <div class="section-panel basic-info-panel">
        <div class="section-title"><span class="section-title-text">基础信息</span></div>
        <div class="section-content">
          <div class="location-title">
            <span class="location-text">渝中区化龙桥街道关岳庙下方（人行天桥段）</span>
            <div class="arrows"></div>
          </div>

          <div class="info-grid">
            <div class="info-item" v-for="item in basicInfo" :key="item.label">
              <span class="label">{{ item.label }}：</span>
              <span class="value">{{ item.value }}</span>
            </div>
          </div>

          <div class="contact-cards">
            <div class="contact-card" v-for="card in contactCards" :key="card.title">
              <div class="card-title"><span class="title-text">{{ card.title }}</span></div>
              <div class="card-name">{{ card.name }}</div>
              <div class="card-phone">
                {{ card.phone }} <i class="phone-icon"></i>
              </div>
            </div>
          </div>

          <div class="reason-bar">
            <i class="arrow-icon"></i>
            <span class="reason-label">风险点等级定级原因：</span>
            <span class="reason-text">属于积水内涝风险重点区域</span>
          </div>
        </div>
      </div>

      <!-- 视频监控 -->
      <div class="section-panel video-info-panel">
        <div class="section-title"><span class="section-title-text">视频监控</span></div>
        <div class="section-content">
          <div class="video-container">
            <div class="video-overlay-time">2026-06-02 16:01:47</div>
            <div class="video-overlay-watermark">重庆市住建委排水中心<br>宇视君 严禁泄密</div>
            <div class="video-overlay-location">固 枪 G0 &nbsp;&nbsp;&nbsp;&nbsp; 重庆 渝中 03 李子坝正街人行天桥楼梯</div>
            <video
              class="video-placeholder"
              controls
              poster="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg=="
            >
              <source src="#" type="video/mp4">
              您的浏览器不支持视频播放
            </video>
          </div>
        </div>
      </div>

      <!-- 分析图 -->
      <div class="charts-row">
        <div class="section-panel chart-info-panel half">
          <div class="section-title"><span class="section-title-text">降雨量分析图</span></div>
          <div class="section-content">
             <div class="chart-container">
                <div class="chart-area left-chart-area">
                  <div class="grid-lines">
                    <div class="grid-line"></div>
                    <div class="grid-line"></div>
                    <div class="grid-line"></div>
                  </div>
                  <div class="bars">
                    <div class="bar" style="height: 5%"></div>
                    <div class="bar orange" style="height: 60%"></div>
                    <div class="bar" style="height: 5%"></div>
                    <div class="bar" style="height: 5%"></div>
                    <div class="bar" style="height: 5%"></div>
                    <div class="bar" style="height: 5%"></div>
                  </div>
                  <div class="x-axis">
                    <div class="x-tick">05-23 04:00</div>
                    <div class="x-tick">05:00</div>
                    <div class="x-tick">06:00</div>
                    <div class="x-tick">07:00</div>
                    <div class="x-tick">06:00</div>
                    <div class="x-tick">07:00</div>
                  </div>
                </div>
             </div>
          </div>
        </div>
        <div class="section-panel chart-info-panel half">
          <div class="section-title"><span class="section-title-text">水位分析图</span></div>
          <div class="section-content">
            <div class="chart-container">
              <div class="chart-unit">单位 <span class="unit-value">cm</span></div>
              <div class="chart-y-axis">
                <div class="y-tick"><span>80</span></div>
                <div class="y-tick"><span>40</span></div>
                <div class="y-tick"><span>0</span></div>
              </div>
              <div class="chart-area right-chart-area">
                <div class="grid-lines">
                  <div class="grid-line"></div>
                  <div class="grid-line"></div>
                  <div class="grid-line"></div>
                </div>
                <div class="bars">
                  <div class="bar" style="height: 40%"></div>
                  <div class="bar" style="height: 60%"></div>
                  <div class="bar" style="height: 70%"></div>
                  <div class="bar" style="height: 80%"></div>
                </div>
                <div class="x-axis">
                  <div class="x-tick">05-23 04:00</div>
                  <div class="x-tick">05:00</div>
                  <div class="x-tick">06:00</div>
                  <div class="x-tick">07:00</div>
                </div>
              </div>
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
    default: '道路风险点详情'
  }
})

const emit = defineEmits(['close'])

const basicInfo = [
  { label: '风险等级', value: '地埋液位计' },
  { label: '所属分区', value: '中心城区' },
  { label: '风险点类型', value: '城市积水风险' },
  { label: '阈值(mm/2h)', value: '地埋液位计' },
  { label: '所属区县', value: '渝中区' },
  { label: '场景类型', value: '道路' },
  { label: '行业主管部门', value: '住房城乡建设委员会' },
  { label: '所属街道', value: '化龙桥街道' },
  { label: '场景子类型', value: '桥梁' },
  { label: '所属社区', value: '佳宝路社区' }
]

const contactCards = [
  { title: '市级负责人', name: '刘某某', phone: '138****9999' },
  { title: '区级负责人', name: '刘某某', phone: '138****9999' },
  { title: '镇街负责人', name: '刘某某', phone: '138****9999' },
  { title: '管护负责人', name: '刘某某', phone: '138****9999' }
]

function handleClose() {
  emit('close')
}
</script>

<script>
export default {
  name: 'WaterlogRoadRiskDetail'
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

.waterlog-road-risk-detail {
  font-family: 'Alibaba PuHuiTi 2.0', sans-serif;
  position: absolute;
  left: 50%;
  top: 50%;
  z-index: 1001;
  width: 904px;
  height: 1080px;
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
  font-weight: 500;
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
  padding: 24px 32px;
  box-sizing: border-box;
  overflow-y: auto;
}

.section-panel {
  background: rgba(14, 55, 115, 0.4);
  border: 1px solid rgba(166, 206, 255, 0.1);
  margin-bottom: 20px;
}

.basic-info-panel {
  width: 840px;
  height: 447px;
  box-sizing: border-box;
  background: url('../img/bg_left_top01@2x.png') no-repeat center / 100% 100%, linear-gradient(90deg, rgba(27, 79, 151, 0.5) 0%, rgba(27, 79, 151, 0.08) 116.99%);
  border: none !important;
}

.video-info-panel {
  width: 840px;
  height: 284px;
  box-sizing: border-box;
  background: url('../img/bg_left_top01@2x.png') no-repeat center / 100% 100%, linear-gradient(90deg, rgba(27, 79, 151, 0.5) 0%, rgba(27, 79, 151, 0.08) 116.99%);
  border: none !important;
}

.chart-info-panel {
  box-sizing: border-box;
  background: url('../img/bg_left_top01@2x.png') no-repeat center / 100% 100%, linear-gradient(90deg, rgba(27, 79, 151, 0.5) 0%, rgba(27, 79, 151, 0.08) 116.99%);
  border: none !important;
}

.section-title {
  height: 36px;
  line-height: 36px;
  padding-left: 16px;
  background: url('../img/liquid_device_title.png') no-repeat center / 100% 100%;
  position: relative;
}

.section-title-text {
  font-family: "Alibaba PuHuiTi 2.0";
  font-size: 14px;
  font-style: normal;
  font-weight: 500;
  line-height: 36px;
  background: linear-gradient(0deg, #5FBCFF 1.4%, #FFF 58.67%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  display: inline-block;
}

.section-content {
  padding: 12px 20px;
}

.location-title {
  display: inline-block;
  position: relative;
  color: #fff;
  font-size: 16px;
  margin-bottom: 12px;
  background: url('../img/waterlog_center_listbg.png') no-repeat center / 100% 100%;
  padding: 6px 60px 6px 14px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px 24px;
}

.info-item {
  display: flex;
  align-items: flex-start;
  font-size: 14px;
}

.info-item .label {
  color: #749dc0;
  min-width: 110px;
  flex-shrink: 0;
}

.info-item .value {
  color: #fff;
  flex: 1;
  word-break: break-all;
}

.contact-cards {
  display: flex;
  justify-content: space-between;
  margin: 16px 0;
}

.contact-card {
  width: 190px;
  height: 125px;
  box-sizing: border-box;
  border: 1px solid rgba(116, 157, 192, 0.5);
  background: rgba(24, 88, 163, 0.15);
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  align-items: center;
}

.card-title {
  margin: 0 auto 12px auto;
  background: url('../img/Rectangle-346242169.png') no-repeat center / 100% 100%;
  width: 140px;
  height: 32px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.title-text {
  text-align: center;
  font-family: "Alibaba PuHuiTi 2.0";
  font-size: 18px;
  font-style: normal;
  font-weight: bold;
  line-height: normal;
  background: linear-gradient(0deg, #5FBCFF 1.4%, #FFF 58.67%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.card-name {
  text-align: center;
  font-feature-settings: 'liga' off, 'clig' off;
  font-family: "Alibaba PuHuiTi 2.0";
  font-size: 16px;
  font-style: normal;
  font-weight: bold;
  line-height: normal;
  letter-spacing: 0.32px;
  margin-bottom: 10px;
  background: linear-gradient(0deg, #5FBCFF 1.4%, #FFF 58.67%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.card-phone {
  color: #12dd60;
  font-feature-settings: 'liga' off, 'clig' off;
  font-family: "Alibaba PuHuiTi 2.0";
  font-size: 16px;
  font-style: normal;
  font-weight: normal;
  line-height: normal;
  letter-spacing: 0.32px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.phone-icon {
  width: 14px;
  height: 14px;
  display: inline-block;
  background: url('../img/phone.png') no-repeat center / cover;
}

.reason-bar {
  background: url('../img/shadow_bg.png') no-repeat center / 100% 100%;
  padding: 10px 14px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.arrow-icon {
  width: 20px;
  height: 20px;
  display: inline-block;
  background: url('../img/arrow.png') no-repeat center / 100% 100%;
}

.reason-label {
  color: #749dc0;
  font-family: "Alibaba PuHuiTi 2.0";
  font-size: 16px;
  font-style: normal;
  font-weight: 500;
  line-height: 20px;
}

.reason-text {
  font-feature-settings: 'liga' off, 'clig' off;
  font-family: "Alibaba PuHuiTi 2.0";
  font-size: 16px;
  font-style: normal;
  font-weight: bold;
  line-height: 24px;
  letter-spacing: 0.32px;
  background: linear-gradient(0deg, #5FBCFF 1.4%, #FFF 58.67%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.video-container {
  width: 100%;
  height: 224px;
  background: #000;
  position: relative;
  border: 1px solid rgba(62, 160, 255, 0.3);
}

.video-placeholder {
  width: 100%;
  height: 100%;
  object-fit: cover;
  background: #000;
}

.video-overlay-time {
  position: absolute;
  right: 20px;
  top: 16px;
  z-index: 5;
  color: #fff;
  font-family: "Alibaba PuHuiTi 2.0", sans-serif;
  font-size: 14px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.8);
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
  left: 20px;
  bottom: 20px;
  z-index: 5;
  color: #ffffff;
  font-family: "Alibaba PuHuiTi 2.0", sans-serif;
  font-size: 14px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.8);
}

.charts-row {
  display: flex;
  gap: 20px;
}

.half {
  flex: 1;
  margin-bottom: 0;
}

.chart-container {
  height: 150px;
  position: relative;
  width: 100%;
}

.chart-unit {
  position: absolute;
  left: 10px;
  top: 0px;
  font-size: 12px;
  color: #749dc0;
}

.chart-unit .unit-value {
  color: #3ce8ff;
}

.chart-y-axis {
  position: absolute;
  left: 0px;
  top: 20px;
  bottom: 25px;
  width: 30px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.y-tick {
  font-size: 12px;
  color: #749dc0;
  text-align: right;
  padding-right: 4px;
}

.chart-area {
  position: absolute;
  top: 20px;
  right: 10px;
  bottom: 25px;
  border-bottom: 1px solid rgba(116, 157, 192, 0.3);
}

.left-chart-area {
  left: 20px;
}

.right-chart-area {
  left: 45px;
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
  transition: height 0.3s ease;
}

.bar.orange {
  background: linear-gradient(180deg, #ff9f3e 0%, rgba(255, 159, 62, 0.2) 100%);
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
