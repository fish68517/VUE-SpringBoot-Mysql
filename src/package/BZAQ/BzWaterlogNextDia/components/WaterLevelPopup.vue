<template>
  <div class="water-level-popup">
    <div class="popup-header">
      <span class="popup-title">弹子石朝天门</span>
      <button class="popup-close" @click="$emit('close')">×</button>
    </div>
    
    <div class="popup-body">
      <div class="tab-nav">
        <button 
          class="tab-nav__item" 
          :class="{ 'tab-nav__item--active': activeTab === 'key' }"
          @click="handleTabClick('key')"
        >
          关键信息
        </button>
        <button 
          class="tab-nav__item" 
          :class="{ 'tab-nav__item--active': activeTab === 'basic' }"
          @click="handleTabClick('basic')"
        >
          基本信息
        </button>
      </div>
      
      <div class="tab-content">
        <div v-if="activeTab === 'key'" class="key-info-panel">
          <div class="key-info-header">
            <div class="guarantee-water-level">
              <span class="guarantee-label">保证水位</span>
              <span class="guarantee-value">185m</span>
            </div>
            <div class="update-time">
              <span class="update-label">更新时间：</span>
              <span class="update-value">2026-06-01 01:00:00</span>
            </div>
          </div>
          
          <div class="water-level-info">
            <div class="info-item">
              <span class="info-item__label">所在河流</span>
              <span class="info-item__value info-item__value--highlight">长江</span>
            </div>
            <div class="info-item">
              <span class="info-item__label">水位</span>
              <span class="info-item__value info-item__value--primary">171.78m</span>
            </div>
            <div class="info-item">
              <span class="info-item__label">比警戒水位</span>
              <span class="info-item__value info-item__value--green">↑ 17.78m</span>
            </div>
            <div class="info-item">
              <span class="info-item__label">水势</span>
              <span class="info-item__value">落</span>
            </div>
            <div class="info-item">
              <span class="info-item__label">警戒水位</span>
              <span class="info-item__value info-item__value--primary">171.78m</span>
            </div>
          </div>
          
          <div class="water-chart">
            <div class="chart-y-axis">
              <span class="y-axis-label">185m</span>
              <span class="y-axis-label">160m</span>
              <span class="y-axis-label">100m</span>
              <span class="y-axis-label">80m</span>
              <span class="y-axis-label">60m</span>
              <span class="y-axis-label">40m</span>
              <span class="y-axis-label">20m</span>
              <span class="y-axis-label">0</span>
            </div>
            <div class="chart-area">
                <div class="chart-line chart-line--red" data-label="保证水位 185m"></div>
                <div class="chart-line chart-line--yellow" data-label="警戒水位 160m"></div>
                <div class="chart-line chart-line--blue" data-label="当前水位 160m"></div>
                <div class="chart-water-fill"></div>
              </div>
          </div>
        </div>
        
        <div v-if="activeTab === 'basic'" class="basic-info-panel">
          <div class="basic-info-grid">
            <div class="basic-info-item">
              <span class="basic-info-item__label">测站编码</span>
              <span class="basic-info-item__value">60704009</span>
            </div>
            <div class="basic-info-item">
              <span class="basic-info-item__label">测站名称</span>
              <span class="basic-info-item__value">大溪沟嘉陵</span>
            </div>
            <div class="basic-info-item">
              <span class="basic-info-item__label">河流名称</span>
              <span class="basic-info-item__value">后溪河</span>
            </div>
            <div class="basic-info-item">
              <span class="basic-info-item__label">测站编码</span>
              <span class="basic-info-item__value">-m</span>
            </div>
            <div class="basic-info-item">
              <span class="basic-info-item__label">保证水位</span>
              <span class="basic-info-item__value">-m</span>
            </div>
            <div class="basic-info-item">
              <span class="basic-info-item__label">经度</span>
              <span class="basic-info-item__value">106.546512</span>
            </div>
            <div class="basic-info-item">
              <span class="basic-info-item__label">纬度</span>
              <span class="basic-info-item__value">106.546512</span>
            </div>
            <div class="basic-info-item">
              <span class="basic-info-item__label">行政区域</span>
              <span class="basic-info-item__value">涪陵区</span>
            </div>
            <div class="basic-info-item">
              <span class="basic-info-item__label">水位</span>
              <span class="basic-info-item__value">170.08m</span>
            </div>
            <div class="basic-info-item">
              <span class="basic-info-item__label">水势</span>
              <span class="basic-info-item__value">-m</span>
            </div>
            <div class="basic-info-item">
              <span class="basic-info-item__label">监测时间</span>
              <span class="basic-info-item__value">2026-06-01 01:00:00</span>
            </div>
            <div class="basic-info-item">
              <span class="basic-info-item__label">站类</span>
              <span class="basic-info-item__value">--</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const emit = defineEmits(['close'])

const activeTab = ref('key')

const handleTabClick = (tabName: string) => {
  activeTab.value = tabName
}
</script>

<script lang="ts">
export default {
  name: "WaterLevelPopup",
  version: "1.0.0"
}
</script>

<style lang="scss" scoped>
.water-level-popup {
  width: 1096px;
  height: 547px;
  background: rgba(5, 33, 62, 0.95);
  border: 1px solid rgba(33, 145, 244, 0.5);
  border-radius: 6px;
  overflow: hidden;
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  z-index: 1001;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.5);
}

.popup-header {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  padding: 14px 16px;
  background: linear-gradient(90deg, rgba(17, 84, 139, 0.9) 0%, rgba(6, 47, 78, 0.8) 100%);
  border-bottom: 1px solid rgba(33, 145, 244, 0.3);
  position: relative;
}

.popup-title {
  font-size: 16px;
  font-weight: 700;
  color: #fff;
  letter-spacing: 4px;
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
}

.popup-close {
  width: 28px;
  height: 28px;
  border: none;
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
  font-size: 24px;
  cursor: pointer;
  border-radius: 3px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;

  &:hover {
    background: rgba(255, 255, 255, 0.2);
    color: #ff6b6b;
  }
}

.popup-body {
  padding: 16px;
  height: calc(100% - 56px);
  display: flex;
  flex-direction: column;
}

.tab-nav {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.tab-nav__item {
  padding: 8px 28px;
  background: rgba(0, 40, 80, 0.8);
  border: 1px solid rgba(33, 145, 244, 0.4);
  border-radius: 4px 4px 0 0;
  color: #8bbce6;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    border-color: rgba(33, 145, 244, 0.8);
    color: #fff;
  }

  &--active {
    background: rgba(33, 145, 244, 0.4);
    border-color: rgba(33, 145, 244, 0.8);
    color: #fff;
  }
}

.tab-content {
  flex: 1;
  overflow: hidden;
}

.key-info-panel {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.key-info-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 14px;
  background: rgba(0, 40, 80, 0.6);
  border: 1px solid rgba(33, 145, 244, 0.3);
  border-radius: 4px;
  margin-bottom: 12px;
}

.guarantee-water-level {
  display: flex;
  align-items: center;
}

.guarantee-label {
  font-size: 14px;
  color: #8bbce6;
  margin-right: 8px;
}

.guarantee-value {
  font-size: 20px;
  font-weight: 700;
  color: #ff4757;
}

.update-time {
  display: flex;
  align-items: center;
}

.update-label {
  font-size: 12px;
  color: #8bbce6;
}

.update-value {
  font-size: 12px;
  color: #e8f8ff;
}

.water-level-info {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
}

.info-item {
  flex: 1;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 12px;
  background: rgba(0, 40, 80, 0.6);
  border: 1px solid rgba(33, 145, 244, 0.3);
  border-radius: 4px;
  font-size: 13px;
}

.info-item__label {
  color: #8bbce6;
}

.info-item__value {
  color: #e8f8ff;
  font-weight: 600;

  &--highlight {
    color: #47a8ff;
  }

  &--primary {
    color: #47dea2;
  }

  &--green {
    color: #47dea2;
  }
}

.water-chart {
  flex: 1;
  display: flex;
  border: 1px solid rgba(33, 145, 244, 0.3);
  border-radius: 4px;
  overflow: hidden;
}

.chart-y-axis {
  width: 50px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 10px 0;
  background: rgba(0, 40, 80, 0.4);
  border-right: 1px solid rgba(33, 145, 244, 0.3);
}

.y-axis-label {
  font-size: 11px;
  color: #8bbce6;
  text-align: center;
}

.chart-area {
  flex: 1;
  position: relative;
  overflow: hidden;
  background: rgba(0, 40, 80, 0.4);
}

.chart-line {
  position: absolute;
  left: 0;
  right: 0;
  height: 1px;

  &--red {
    top: 15%;
    border-top: 2px dashed #ff4757;
  }

  &--yellow {
    top: 35%;
    border-top: 2px dashed #ffa502;
  }

  &--blue {
    top: 55%;
    border-top: 2px solid #47a8ff;
  }

  &::before {
    content: attr(data-label);
    position: absolute;
    left: 50%;
    transform: translateX(-50%);
    top: -20px;
    font-size: 11px;
    color: inherit;
    white-space: nowrap;
    font-weight: 600;
  }

  &--red::before {
    color: #ff4757;
  }

  &--yellow::before {
    color: #ffa502;
  }

  &--blue::before {
    color: #47a8ff;
  }
}

.line-dot {
  position: absolute;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: inherit;
}

.chart-water-fill {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  height: 60%;
  background: linear-gradient(180deg, rgba(33, 145, 244, 0.7) 0%, rgba(6, 123, 188, 0.9) 50%, rgba(6, 80, 120, 0.95) 100%);
  border-top: 2px solid rgba(67, 196, 255, 0.8);
}

.chart-water-fill::before {
  content: '';
  position: absolute;
  left: 0;
  right: 0;
  top: -10px;
  height: 20px;
  background: linear-gradient(180deg, rgba(67, 196, 255, 0.6) 0%, rgba(33, 145, 244, 0) 100%);
  border-radius: 50%;
  animation: wave 3s ease-in-out infinite;
}

@keyframes wave {
  0%, 100% {
    transform: translateX(0);
  }
  50% {
    transform: translateX(-5px);
  }
}

.basic-info-panel {
  height: 100%;
}

.basic-info-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
}

.basic-info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 12px;
  background: rgba(0, 40, 80, 0.6);
  border: 1px solid rgba(33, 145, 244, 0.3);
  border-radius: 4px;
  font-size: 13px;
}

.basic-info-item__label {
  color: #8bbce6;
}

.basic-info-item__value {
  color: #e8f8ff;
}
</style>