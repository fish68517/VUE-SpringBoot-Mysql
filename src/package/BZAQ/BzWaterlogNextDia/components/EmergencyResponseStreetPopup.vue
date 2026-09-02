<template>
  <div class="emergency-response-street-popup">
    <div class="popup-header">
      <span class="popup-title">应急响应（街道）</span>
      <button class="popup-close" @click="$emit('close')">×</button>
    </div>
    
    <div class="popup-body">      
      <div class="level-grid">
        <div class="level-column">
          <div class="level-column__header">
            <span class="level-column__label">Ⅰ级</span>
            <span class="level-column__count">({{ level1Streets.length }})</span>
          </div>
          <div class="level-column__body">
            <div class="street-card" v-for="(street, index) in level1Streets" :key="index" @click="openWaterLevel" style="cursor: pointer;">
              <div class="street-card__name-row">
                <span class="street-card__name">{{ street.name }}</span>
                <span class="street-card__status" :class="'street-card__status--' + street.status">{{ street.statusText }}</span>
              </div>
              <div class="street-card__info">
                <div class="street-info-item">
                  <span class="street-info-item__label">坐镇领导:</span>
                  <span class="street-info-item__value">{{ street.leader }}</span>
                </div>
                <div class="street-info-item street-info-item--multi-line">
                  <span class="street-info-item__label">应急预案:</span>
                  <span class="street-info-item__value">{{ street.plan }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <div class="level-column">
          <div class="level-column__header">
            <span class="level-column__label">Ⅱ级</span>
            <span class="level-column__count">({{ level2Streets.length }})</span>
          </div>
          <div class="level-column__body">
            <div class="street-card" v-for="(street, index) in level2Streets" :key="index" @click="openWaterLevel" style="cursor: pointer;">
              <div class="street-card__name-row">
                <span class="street-card__name">{{ street.name }}</span>
                <span class="street-card__status" :class="'street-card__status--' + street.status">{{ street.statusText }}</span>
              </div>
              <div class="street-card__info">
                <div class="street-info-item">
                  <span class="street-info-item__label">坐镇领导:</span>
                  <span class="street-info-item__value">{{ street.leader }}</span>
                </div>
                <div class="street-info-item street-info-item--multi-line">
                  <span class="street-info-item__label">应急预案:</span>
                  <span class="street-info-item__value">{{ street.plan }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <div class="level-column">
          <div class="level-column__header">
            <span class="level-column__label">Ⅲ级</span>
            <span class="level-column__count">(0)</span>
          </div>
        </div>
        
        <div class="level-column">
          <div class="level-column__header">
            <span class="level-column__label">Ⅳ级</span>
            <span class="level-column__count">(0)</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import eventBus from '@/utils/bus'

let emits = defineEmits(['close','upLevel'])

function openWaterLevel() {
  emits('upLevel')
}

const level1Streets = ref([
  {
    name: '上清寺街道',
    leader: '王建国',
    plan: '启动Ⅰ级应急响应，组织社区开展积水排查，转移低洼地带居民，设置临时安置点，协调排水抢险队伍及时处置积水点位。',
    status: 'responding',
    statusText: '响应中'
  },
  {
    name: '两路口街道',
    leader: '李明辉',
    plan: '启动Ⅰ级应急响应，对辖区内下穿道、地下车库等重点区域实施交通管制，安排专人值守，确保居民生命财产安全。',
    status: 'responding',
    statusText: '响应中'
  },
  {
    name: '菜园坝街道',
    leader: '张伟',
    plan: '启动Ⅰ级应急响应，对菜园坝立交及火车站周边区域进行重点监控，组织抢险排涝，协助转移受困群众。',
    status: 'ended',
    statusText: '已结束'
  }
])

const level2Streets = ref([
  {
    name: '大坪街道',
    leader: '陈志强',
    plan: '启动Ⅱ级应急响应，加强对辖区易涝点巡查，预置排水设备，做好应急转移准备。',
    status: 'responding',
    statusText: '响应中'
  }
])
</script>

<script lang="ts">
export default {
  name: "EmergencyResponseStreetPopup",
  version: "1.0.0"
}
</script>

<style lang="scss" scoped>
.emergency-response-street-popup {
  width: 1176px;
  height: 629px;
  background: rgba(5, 33, 62, 0.95);
  border: 1px solid rgba(33, 145, 244, 0.5);
  border-radius: 6px;
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  z-index: 1001;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
}

.popup-header {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  padding: 14px 16px;
  background: linear-gradient(90deg, rgba(17, 84, 139, 0.9) 0%, rgba(6, 47, 78, 0.8) 100%);
  border-bottom: 1px solid rgba(33, 145, 244, 0.3);
  position: relative;
  flex-shrink: 0;
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
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.level-grid {
  flex: 1;
  min-height: 0;
  display: flex;
  gap: 8px;
}

.level-column {
  flex: 1;
  min-height: 0;
  border: 1px dashed rgba(33, 145, 244, 0.3);
  border-radius: 4px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.level-column__header {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 10px 0;
  background: rgba(17, 84, 139, 0.7);
  border-bottom: 1px dashed rgba(33, 145, 244, 0.3);
  flex-shrink: 0;
}

.level-column__body {
  flex: 1;
  overflow-y: auto;
  scrollbar-width: none;
  &::-webkit-scrollbar {
    display: none;
  }
}

.level-column__label {
  font-size: 13px;
  font-weight: 600;
  color: #fff;
}

.level-column__count {
  font-size: 13px;
  color: #8bbce6;
  margin-left: 4px;
}

.street-card {
  margin: 10px;
  padding: 10px;
  background: rgba(33, 145, 244, 0.15);
  border: 1px solid rgba(33, 145, 244, 0.3);
  border-radius: 4px;

  &:not(:last-child) {
    margin-bottom: 8px;
  }
}

.street-card__name-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
  padding-bottom: 6px;
  border-bottom: 1px solid rgba(33, 145, 244, 0.3);
}

.street-card__name {
  font-size: 13px;
  font-weight: 600;
  color: #47a8ff;
}

.street-card__status {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 3px;
  font-weight: 600;

  &--responding {
    background: rgba(255, 165, 0, 0.25);
    color: #ffa500;
    border: 1px solid rgba(255, 165, 0, 0.5);
  }

  &--ended {
    background: rgba(76, 175, 80, 0.25);
    color: #4caf50;
    border: 1px solid rgba(76, 175, 80, 0.5);
  }
}

.street-card__info {
  font-size: 12px;
}

.street-info-item {
  display: flex;
  margin-bottom: 6px;

  &:last-child {
    margin-bottom: 0;
  }

  &--multi-line {
    flex-direction: column;
    .street-info-item__value {
      line-height: 1.5;
    }
  }
}

.street-info-item__label {
  color: #8bbce6;
  flex-shrink: 0;
}

.street-info-item__value {
  color: #e8f8ff;
  flex: 1;
}
</style>