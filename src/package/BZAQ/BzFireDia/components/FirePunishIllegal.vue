<template>
  <div class="fire-punish-illegal-modal">
    <div class="modal-header">
      <div class="header-title">责令限期整改的消防安全违法行为</div>
      <button class="close-btn" @click="handleClose">×</button>
    </div>

    <div class="modal-content">
      <div class="list-item" v-for="(item, index) in listData" :key="index">
        <span class="item-text">{{ item.text }}</span>
        <span class="item-count">
          <span class="count-left">{{ item.left }}</span>
          <span class="count-divider">/</span>
          <span class="count-right">{{ item.right }}</span>
        </span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const emit = defineEmits(['close'])

const listData = ref([
  { text: '未依法进行消防设计备案/竣工验收消防备案;', left: '11', right: '11' },
  { text: '消防设施、器材、消防安全标志配置、设置不符合标准，未保持完好有效;', left: '11', right: '11' },
  { text: '损坏/挪用/擅自拆除消防设施、器材;', left: '11', right: '11' },
  { text: '占用/堵塞/封闭疏散通道、安全出口;', left: '11', right: '11' }
])

function handleClose() {
  emit('close')
}
</script>

<script>
export default {
  name: "FirePunishIllegal"
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

.fire-punish-illegal-modal {
  font-family: 'AlibabaPuHuiTi', sans-serif;
  position: absolute;
  transform: translate(-50%, -50%);
  z-index: 1001;
  width: 664px;
  height: 480px;
  display: flex;
  flex-direction: column;
  background:
    url('../img/Group_2136640490.png') top center / 100% 60px no-repeat,
    linear-gradient(180deg, rgba(3, 19, 54, 0.95) 0%, rgba(2, 12, 36, 0.95) 100%);
  border: 1px solid rgba(37, 134, 255, 0.25);
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.45);
  color: #b8d9ff;
  font-size: 14px;
  overflow: hidden;
  pointer-events: auto;

  &::before {
    content: '';
    position: absolute;
    inset: 0;
    background: url('../img/Rectangle_346242153.png') center / cover no-repeat;
    opacity: 0.25;
    z-index: -1;
  }
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
    color: #cfe8ff;
    font-size: 20px;
    font-weight: 700;
    text-shadow: 0 0 12px rgba(83, 174, 255, 0.5);
  }

  .close-btn {
    position: absolute;
    right: 20px;
    top: 50%;
    transform: translateY(-50%);
    width: 32px;
    height: 32px;
    border: 1px solid rgba(83, 174, 255, 0.3);
    background: rgba(16, 64, 126, 0.4);
    color: #aed5ff;
    font-size: 20px;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;

    &:hover {
      color: #ffffff;
      border-color: rgba(83, 174, 255, 0.6);
      background: rgba(24, 92, 179, 0.6);
    }
  }
}

.modal-content {
  flex: 1;
  min-height: 0;
  padding: 32px 32px 0 32px;
  overflow: hidden;
}

.list-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 48px;
  padding: 0 16px;
  margin-bottom: 8px;
  background: transparent;

  &:nth-child(even) {
    background: #19406B;
  }

  .item-text {
    font-size: 16px;
    color: #d7e9ff;
    flex: 1;
    margin-right: 32px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .item-count {
    display: flex;
    align-items: center;
    font-size: 18px;
    font-weight: 700;
    font-family: 'D-DIN', 'AlibabaPuHuiTi', sans-serif;

    .count-left {
      background: linear-gradient(to bottom, #ffd959, #ffa023);
      -webkit-background-clip: text;
      background-clip: text;
      color: transparent;
    }

    .count-divider {
      margin: 0 4px;
      color: #8BBCE6;
    }

    .count-right {
      background: linear-gradient(to bottom, #53aeff, #2af5ff);
      -webkit-background-clip: text;
      background-clip: text;
      color: transparent;
    }
  }
}
</style>
