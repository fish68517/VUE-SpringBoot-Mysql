<template>
  <div class="weather-alert-popup">
    <div class="popup-header">
      <span class="popup-title">气象预警</span>
      <button class="popup-close" @click="$emit('close')">×</button>
    </div>
    
    <div class="popup-body">
      <div class="alert-item">
        <span class="alert-item__label">预警发布单位:</span>
        <span class="alert-item__value">市气象局</span>
      </div>
      
      <div class="alert-item">
        <span class="alert-item__label">预警区域:</span>
        <span class="alert-item__value">渝中区</span>
      </div>
      
      <div class="alert-item">
        <span class="alert-item__label">预警区域:</span>
        <span class="alert-item__value">沙坪坝区、渝中区</span>
      </div>
      
      <div class="alert-item">
        <span class="alert-item__label">预警名称:</span>
        <span class="alert-item__value">重庆市气象台发布暴雨橙色预警[Ⅲ级/较重]</span>
      </div>
      
      <div class="alert-item">
        <span class="alert-item__label">预警时段:</span>
        <span class="alert-item__value">2025-11-11 14:23:31 至 2025-11-12 14:23:31</span>
      </div>
      
      <div class="alert-item">
        <span class="alert-item__label">风险等级:</span>
        <span class="alert-item__value">
          <span class="level-tag level-severe">严重</span>
        </span>
      </div>
      
      <div class="alert-item">
        <span class="alert-item__label">预警级别:</span>
        <span class="alert-item__value">
          <span class="level-tag level-orange">Ⅱ级</span>
        </span>
      </div>
      
      <div class="alert-item alert-item--content">
        <span class="alert-item__label">预警内容:</span>
        <span class="alert-item__value">
          这是预警内容这是预警内容这是预警内容，这是预警内容这是预警内容这是预警内容，这是预警内容这是预警内容这是预警内容这是预警内容，这是预警内容这是预警内容这是预警内容这是预警内容这是预警内容
        </span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineEmits(['close'])
</script>

<script lang="ts">
export default {
  name: "WeatherAlertPopup",
  version: "1.0.0"
}
</script>

<style lang="scss" scoped>
.weather-alert-popup {
  width: 452px;
  height: 380px;
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
  padding: 12px 16px;
  background: linear-gradient(90deg, rgba(17, 84, 139, 0.9) 0%, rgba(6, 47, 78, 0.8) 100%);
  border-bottom: 1px solid rgba(33, 145, 244, 0.3);
  position: relative;
}

.popup-title {
  font-size: 16px;
  font-weight: 700;
  color: #fff;
  letter-spacing: 2px;
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
}

.popup-close {
  width: 24px;
  height: 24px;
  border: none;
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
  font-size: 20px;
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
  padding: 14px 16px;
  overflow-y: auto;
  height: calc(100% - 48px);
}

.alert-item {
  display: flex;
  margin-bottom: 10px;
  font-size: 13px;

  &:last-child {
    margin-bottom: 0;
  }

  &--content {
    flex-direction: column;
    .alert-item__label {
      margin-bottom: 4px;
    }
    .alert-item__value {
      line-height: 1.6;
    }
  }
}

.alert-item__label {
  width: 100px;
  flex-shrink: 0;
  color: #8bbce6;
}

.alert-item__value {
  flex: 1;
  color: #e8f8ff;
}

.level-tag {
  padding: 2px 10px;
  border-radius: 3px;
  font-weight: 700;
  font-size: 12px;
}

.level-severe {
  color: #ff4757;
  background: rgba(255, 71, 87, 0.2);
  border: 1px solid #ff4757;
}

.level-orange {
  color: #ffa502;
  background: rgba(255, 165, 2, 0.2);
  border: 1px solid #ffa502;
}

::-webkit-scrollbar {
  width: 6px;
}

::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.2);
}

::-webkit-scrollbar-thumb {
  background: rgba(33, 145, 244, 0.5);
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: rgba(33, 145, 244, 0.7);
}
</style>