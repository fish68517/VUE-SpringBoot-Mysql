<template>
  <div class="waterlog-alarm-modal">
    <div class="modal-header">
      <div class="header-title">告警事件详情</div>
      <!-- <button class="close-btn" @click="handleClose">×</button> -->
    </div>

    <div class="modal-body">
      <div class="tab-bar">
        <div class="tab active">基础信息</div>
        <div class="tab">处置信息</div>
        <div class="tab">部门协同</div>
      </div>

      <div class="content-layout">
        <aside class="left-panel">
          <div class="media-card">
            <div class="card-head">
              <span class="card-title">视频监控/现场照片</span>
              <button class="all-device-btn">所有设备</button>
            </div>
            <div class="card-content image-content">
               <div class="image-placeholder">
                 <img src="https://picsum.photos/seed/waterlog/400/200" alt="现场照片" class="camera-img" />
               </div>
            </div>
          </div>

          <div class="media-card chart-card">
            <div class="card-head">
              <span class="card-title">视频监控/现场照片</span>
            </div>
            <div class="card-content chart-content">
              <div class="rain-chart">
                <div class="chart-grid"></div>
                <div class="bar main"></div>
                <div class="chart-times">
                  <span>05-23 04:00</span>
                  <span>05:00</span>
                  <span>06:00</span>
                  <span>07:00</span>
                </div>
              </div>
            </div>
          </div>
        </aside>

        <main class="right-panel">
          <div class="info-grid">
            <div class="info-item" v-for="(item, index) in baseInfo" :key="index">
              <div class="label">{{ item.label }}</div>
              <div class="value-wrap">
                <span class="value" :class="{ 'is-phone': item.phone }">{{ item.value }}</span>
                <img v-if="item.phone" src="../img/phone.png" class="phone-icon" />
              </div>
            </div>
          </div>
        </main>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const emit = defineEmits(['close'])

const baseInfo = [
  { label: '定位名称', value: '沙坪坝陈家桥街道...' },
  { label: '所属区县', value: '沙坪坝区' },
  { label: '降雨阈值', value: '22mm/2h' },
  { label: '告警时间', value: '2026-05-03 06: 01: 23' },
  { label: '告警来源', value: '人工' },
  { label: '降雨量', value: '22mm' },
  { label: '积水深度', value: '10cm' },
  { label: '积水面积', value: '20m²' },
  { label: '是否断交', value: '否' },
  { label: '市级负责人', value: '成某 138****9999', phone: true },
  { label: '区级负责人', value: '成某 138****9999', phone: true },
  { label: '镇街负责人', value: '成某 138****9999', phone: true },
  { label: '管护负责人', value: '成某 138****9999', phone: true }
]

function handleClose() {
  emit('close')
}
</script>

<script>
export default {
  name: "WaterlogAlarmList"
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

.waterlog-alarm-modal {
  font-family: 'AlibabaPuHuiTi', sans-serif;
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  z-index: 1001;
  width: 1356px;
  height: 604px;
  display: flex;
  flex-direction: column;
  background:
    url('../img/Group-2136640490.png') top center / 100% 48px no-repeat,
    url('../img/Rectangle-346242153.png') center / 100% 100% no-repeat;
  color: #b8d9ff;
  font-size: 14px;
  pointer-events: auto;
}

.modal-header {
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  flex-shrink: 0;

  .header-title {
    color: #ffffff;
    font-size: 18px;
    font-weight: 700;
  }
}

.modal-body {
  flex: 1;
  padding: 20px 30px 30px;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.tab-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.tab {
  width: 98px;
  height: 36px;
  background: url('../img/waterlog_tab_noactive.png') center / 100% 100% no-repeat;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #79a1cc;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.tab.active {
  background: url('../img/waterlog_tab.png') center / 100% 100% no-repeat;
  color: #fff;
}

.content-layout {
  display: flex;
  gap: 20px;
  flex: 1;
  min-height: 0;
}

.left-panel {
  width: 320px;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.media-card {
  flex: 1;
  background: url('../img/waterlog_left_cardbg.png') center / 100% 100% no-repeat;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.card-head {
  height: 40px;
  background: url('../img/wterlog_left_card_title.png') center / 100% 100% no-repeat;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 10px;
}

.card-title {
  color: #fff;
  font-size: 14px;
  font-weight: bold;
}

.all-device-btn {
  width: 66px;
  height: 24px;
  background: url('../img/waterlog_alldevice_tab.png') center / 100% 100% no-repeat;
  border: none;
  color: #fff;
  font-size: 12px;
  cursor: pointer;
}

.card-content {
  flex: 1;
  padding: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 0;
}

.image-placeholder {
  width: 100%;
  height: 100%;
  border: 1px solid rgba(134, 194, 255, 0.22);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.camera-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.chart-card {
  flex: 0.8;
}

.rain-chart {
  position: relative;
  width: 100%;
  height: 100%;
}

.chart-grid {
  position: absolute;
  inset: 10px 10px 30px;
  background: repeating-linear-gradient(0deg, rgba(91, 167, 225, 0.25) 0 1px, transparent 1px 20px);
}

.bar {
  position: absolute;
  bottom: 30px;
  width: 15px;
  background: linear-gradient(180deg, #ff9a45, rgba(255, 154, 69, 0));
}

.bar.main {
  left: 50%;
  transform: translateX(-50%);
  height: 60%;
}

.chart-times {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #d8edff;
}

.right-panel {
  flex: 1;
  background: url('../img/waterlog_center_bg.png') center / 100% 100% no-repeat;
  padding: 30px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px 30px;
}

.info-item {
  height: 40px;
  background: url('../img/waterlog_center_listbg.png') center / 100% 100% no-repeat;
  display: flex;
  align-items: center;
  padding: 0 15px 0 25px; /* Added left padding for the arrow shape */
}

.label {
  width: 90px;
  color: #aed5ff;
  font-size: 14px;
}

.value-wrap {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 8px;
}

.value {
  color: #fff;
  font-size: 14px;
  font-family: 'D-DIN', 'AlibabaPuHuiTi', sans-serif;
}

.is-phone {
  color: #23ff8f;
}

.phone-icon {
  width: 16px;
  height: 16px;
  cursor: pointer;
}
</style>