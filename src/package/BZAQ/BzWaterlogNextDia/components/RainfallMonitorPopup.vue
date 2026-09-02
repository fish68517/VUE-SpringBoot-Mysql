<template>
  <div class="rainfall-monitor-popup">
    <div class="popup-header">
      <span class="popup-title">降雨监测</span>
      <button class="popup-close" @click="$emit('close')">×</button>
    </div>

    <div class="popup-body">
      <div class="search-bar">
        <div class="search-item">
          <input type="date" v-model="selectedDate" class="date-input" />
          <span v-if="selectedDate" class="clear-btn" @click="selectedDate = ''">×</span>
        </div>
        <div class="search-item">
          <input type="text" v-model="searchName" class="search-input" placeholder="请输入雨量站名称" />
          <span v-if="searchName" class="clear-btn" @click="searchName = ''">×</span>
        </div>
        <button class="search-btn">搜索</button>
      </div>

      <div class="station-grid">
        <div class="station-card" v-for="(station, index) in stations" :key="index">
          <div class="station-card__content">
            <div class="station-card__header">
              <span class="station-card__name">{{ station.name }}</span>
              <span class="station-card__label">最大降雨量:</span>
              <span class="station-card__value">{{ station.maxRainfall }}</span>
            </div>
            <div class="station-card__progress">
              <div class="progress-segments">
                <span
                  v-for="i in 10"
                  :key="i"
                  class="progress-segment"
                  :class="{ active: i <= station.progressSegments }"
                ></span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="pagination">
        <span class="pagination__info">共 5 页</span>
        <div class="pagination__nav">
          <button class="pagination__btn pagination__btn--prev">‹</button>
          <button class="pagination__btn pagination__btn--active">1</button>
          <button class="pagination__btn">2</button>
          <button class="pagination__btn">3</button>
          <button class="pagination__btn">4</button>
          <button class="pagination__btn">5</button>
          <button class="pagination__btn pagination__btn--next">›</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

defineEmits(['close'])

const selectedDate = ref('')
const searchName = ref('')

const stations = ref([
  { name: '酉阳米旺', maxRainfall: '1.60mm', progressSegments: 6 },
  { name: '酉阳米旺', maxRainfall: '1.60mm', progressSegments: 6 },
  { name: '酉阳米旺', maxRainfall: '1.60mm', progressSegments: 6 },
  { name: '酉阳米旺', maxRainfall: '1.60mm', progressSegments: 6 },
  { name: '酉阳米旺', maxRainfall: '1.60mm', progressSegments: 6 },
  { name: '酉阳米旺', maxRainfall: '1.60mm', progressSegments: 6 },
  { name: '酉阳米旺', maxRainfall: '1.60mm', progressSegments: 6 },
  { name: '酉阳米旺', maxRainfall: '1.60mm', progressSegments: 6 },
  { name: '酉阳米旺', maxRainfall: '1.60mm', progressSegments: 6 }
])
</script>

<script lang="ts">
export default {
  name: "RainfallMonitorPopup",
  version: "1.0.0"
}
</script>

<style lang="scss" scoped>
.rainfall-monitor-popup {
  width: 1406px;
  height: 553px;
  background: url('../img/mainbg.png') center/cover no-repeat;
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
  background: url('../img/diatitlebg.png') center/cover no-repeat;
  border-bottom: 1px solid rgba(33, 145, 244, 0.3);
  position: relative;
  height: 56px;
}

.popup-title {
  font-size: 20px;
  font-weight: 700;
  color: #fff;
  letter-spacing: 2px;
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
}

.popup-close {
  width: 28px;
  height: 28px;
  border: none;
  background: transparent;
  color: #fff;
  font-size: 24px;
  cursor: pointer;
  border-radius: 3px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  line-height: 1;

  &:hover {
    color: #ff6b6b;
  }
}

.popup-body {
  padding: 24px 36px;
  height: calc(100% - 56px);
  display: flex;
  flex-direction: column;
}

.search-bar {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
  align-items: center;
}

.search-item {
  position: relative;
  display: inline-flex;
  align-items: center;
}

.clear-btn {
  position: absolute;
  right: 8px;
  top: 50%;
  transform: translateY(-50%);
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
  font-size: 12px;
  line-height: 18px;
  text-align: center;
  cursor: pointer;
  z-index: 2;
  transition: all 0.2s ease;

  &:hover {
    background: rgba(255, 107, 107, 0.8);
  }
}

.date-input {
  width: 280px;
  height: 36px;
  padding: 0 32px 0 12px;
  background: rgba(9, 49, 100, 0.7);
  border: 1px solid rgba(33, 145, 244, 0.6);
  border-radius: 4px;
  color: #fff;
  font-size: 14px;
  outline: none;
  transition: all 0.3s ease;
  box-shadow: 0 0 6px rgba(33, 145, 244, 0.12) inset;
  box-sizing: border-box;

  &::-webkit-calendar-picker-indicator {
    filter: invert(1);
    cursor: pointer;
    opacity: 0.8;

    &:hover {
      opacity: 1;
    }
  }

  &::-webkit-datetime-edit-text,
  &::-webkit-datetime-edit-month-field,
  &::-webkit-datetime-edit-day-field,
  &::-webkit-datetime-edit-year-field {
    color: rgba(139, 188, 230, 0.8);
  }

  &::-webkit-clear-button {
    display: none;
  }

  &:focus {
    border-color: rgba(33, 145, 244, 0.9);
    box-shadow: 0 0 10px rgba(33, 145, 244, 0.3);
  }
}

.search-input {
  width: 280px;
  height: 36px;
  padding: 0 32px 0 12px;
  background: rgba(9, 49, 100, 0.7);
  border: 1px solid rgba(33, 145, 244, 0.6);
  border-radius: 4px;
  color: #fff;
  font-size: 14px;
  outline: none;
  transition: all 0.3s ease;
  box-shadow: 0 0 6px rgba(33, 145, 244, 0.12) inset;
  box-sizing: border-box;

  &::placeholder {
    color: rgba(139, 188, 230, 0.8);
  }

  &:focus {
    border-color: rgba(33, 145, 244, 0.9);
    box-shadow: 0 0 10px rgba(33, 145, 244, 0.3);
  }
}

.search-btn {
  height: 36px;
  padding: 0 24px;
  background: linear-gradient(180deg, rgba(9, 81, 156, 0.9) 0%, rgba(4, 42, 85, 0.95) 100%);
  border: 1px solid rgba(33, 145, 244, 0.8);
  border-radius: 4px;
  color: #fff;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 0 10px rgba(33, 145, 244, 0.4), inset 0 0 10px rgba(255, 255, 255, 0.1);

  &:hover {
    background: linear-gradient(180deg, rgba(12, 92, 176, 0.95) 0%, rgba(5, 55, 110, 1) 100%);
    box-shadow: 0 0 14px rgba(33, 145, 244, 0.6), inset 0 0 10px rgba(255, 255, 255, 0.15);
  }
}

.station-grid {
  display: grid;
  grid-template-columns: repeat(3, 426px);
  gap: 16px;
  flex: 1;
  overflow-y: auto;
  margin-bottom: 16px;
  justify-content: space-between;
}

.station-card {
  width: 426px;
  height: 84px;
  background: url('../img/itembg.png') 100% 100% / contain no-repeat;
  border: none;
  border-radius: 8px;
  padding: 12px 20px 12px 72px;
  display: flex;
  align-items: center;
  transition: all 0.3s ease;
  box-sizing: border-box;

  &:hover {
    filter: brightness(1.1);
  }
}

.station-card__content {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.station-card__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.station-card__name {
  font-size: 15px;
  font-weight: 600;
  color: #fff;
  flex: 1;
  border-left: 3px solid #04bcfa;
  padding-left: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 120px;
}

.station-card__label {
  font-size: 13px;
  color: #8bbce6;
  white-space: nowrap;
}

.station-card__value {
  font-size: 16px;
  font-weight: 700;
  color: #47dea2;
  white-space: nowrap;
}

.station-card__progress {
  width: 100%;
  padding-left: 8px;
}

.progress-segments {
  display: flex;
  gap: 2px;
  width: 100%;
  height: 10px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 5px;
  padding: 2px;
}

.progress-segment {
  flex: 1;
  border-radius: 2px;
  background: transparent;
  transition: all 0.3s ease;

  &.active {
    background: #04bcfa;
  }
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  padding-top: 12px;
  border-top: 1px solid rgba(33, 145, 244, 0.2);
}

.pagination__info {
  font-size: 13px;
  color: #8bbce6;
}

.pagination__nav {
  display: flex;
  gap: 8px;
}

.pagination__btn {
  width: 32px;
  height: 32px;
  border: 1px solid rgba(33, 145, 244, 0.4);
  background: rgba(0, 40, 80, 0.5);
  color: #8bbce6;
  font-size: 13px;
  cursor: pointer;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;

  &:hover {
    background: rgba(33, 145, 244, 0.3);
    color: #fff;
  }

  &--active {
    background: #04bcfa;
    color: #fff;
    border-color: #04bcfa;
  }
}
</style>
