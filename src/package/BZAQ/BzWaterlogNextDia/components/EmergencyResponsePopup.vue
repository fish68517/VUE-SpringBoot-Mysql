<template>
  <div class="emergency-response-popup">
    <div class="popup-header">
      <span class="popup-title">应急响应（市区）</span>
      <button class="popup-close" @click="$emit('close')">×</button>
    </div>
    
    <div class="popup-body">
      <div class="response-banner">
        <span class="response-banner__icon">✦</span>
        <span class="response-banner__text">启动IV级防汛应急响应</span>
      </div>
      
      <div class="response-item">
        <span class="response-item__label">应急预案:</span>
        <span class="response-item__value">渝中区防汛抗旱应急预案</span>
      </div>
      
      <div class="response-item">
        <span class="response-item__label">预警等级:</span>
        <span class="response-item__value">
          <span class="level-tag level-orange">Ⅱ级</span>
        </span>
      </div>
      
      <div class="response-item">
        <span class="response-item__label">预警时段:</span>
        <span class="response-item__value">2025-11-11 14:23:31</span>
      </div>
      
      <div class="response-item">
        <span class="response-item__label">启动对象:</span>
        <span class="response-item__value">两路口街道、大坪街道、菜园坝街道、上清寺街道</span>
      </div>
      
      <div class="response-item">
        <span class="response-item__label">坐镇领导:</span>
        <span class="response-item__value">-</span>
      </div>
      
      <div class="response-item">
        <span class="response-item__label">坐镇领导:</span>
        <span class="response-item__value">-</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineEmits(['close'])
</script>

<script lang="ts">
export default {
  name: "EmergencyResponsePopup",
  version: "1.0.0"
}
</script>

<style lang="scss" scoped>
.emergency-response-popup {
  width: 500px;
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
}

.response-banner {
  display: flex;
  align-items: center;
  padding: 12px 14px;
  background: linear-gradient(90deg, rgba(71, 222, 162, 0.2) 0%, rgba(6, 123, 87, 0.2) 100%);
  border: 1px solid rgba(71, 222, 162, 0.4);
  border-radius: 4px;
  margin-bottom: 16px;
}

.response-banner__icon {
  color: #47dea2;
  font-size: 16px;
  margin-right: 8px;
}

.response-banner__text {
  font-size: 14px;
  font-weight: 600;
  color: #47dea2;
}

.response-item {
  display: flex;
  margin-bottom: 12px;
  font-size: 13px;

  &:last-child {
    margin-bottom: 0;
  }
}

.response-item__label {
  width: 80px;
  flex-shrink: 0;
  color: #8bbce6;
}

.response-item__value {
  flex: 1;
  color: #e8f8ff;
}

.level-tag {
  padding: 2px 12px;
  border-radius: 3px;
  font-weight: 700;
  font-size: 12px;
}

.level-orange {
  color: #ffa502;
  background: rgba(255, 165, 2, 0.2);
  border: 1px solid #ffa502;
}
</style>