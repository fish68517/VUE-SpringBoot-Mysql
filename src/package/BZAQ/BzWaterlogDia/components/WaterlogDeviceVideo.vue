<template>
  <div class="waterlog-device-video-modal">
    <div class="modal-header">
      <div class="header-title">{{ deviceInfo.name }}</div>
      <button class="close-btn" @click="handleClose">×</button>
    </div>

    <div class="modal-content">
      <!-- 左侧设备信息 -->
      <div class="left-panel">
        <div class="panel-title">设备信息</div>
        <ul class="info-list">
          <li v-for="item in infoList" :key="item.key">
            <span class="label">{{ item.label }}：</span>
            <span class="value">{{ item.value }}</span>
          </li>
        </ul>
      </div>

      <!-- 右侧视频 -->
      <div class="right-panel">
        <video
          class="video-placeholder"
          width="782"
          height="480"
          controls
          poster="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg=="
        >
          <source src="#" type="video/mp4">
          您的浏览器不支持视频播放
        </video>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const emit = defineEmits(['close'])

// 设备静态数据
const deviceInfo = ref({
  name: '设备名称-牛角沱板凳桥立交'
})

const infoList = ref([
  { key: 'code', label: '设备编码', value: '50010301321203923910' },
  { key: 'district', label: '所属区县', value: '渝中区' },
  { key: 'type', label: '感知类型', value: '摄像头' },
  { key: 'name', label: '设备名称', value: '牛角沱板凳桥立交' },
  { key: 'unit', label: '所属单位', value: '重庆市渝中区住房和城市建设...' },
  { key: 'point', label: '设备风险点', value: '渝中区上清寺街道上清寺路...' },
  { key: 'status', label: '设备状态', value: '-' }
])

function handleClose() {
  emit('close')
}
</script>

<script>
export default {
  name: "WaterlogDeviceVideo"
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

.waterlog-device-video-modal {
  font-family: 'AlibabaPuHuiTi', sans-serif;
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  z-index: 1001;
  width: 1159px;
  height: 592px;
  display: flex;
  flex-direction: column;
  background:
    url('../img/Group-2136640490-1.png') top center / 100% 60px no-repeat,
    url('../img/Rectangle-346242153.png') center / 100% 100% no-repeat;
  border: 1px solid rgba(37, 134, 255, 0.25);
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.45);
  color: #b8d9ff;
  font-size: 14px;
  overflow: hidden;
  pointer-events: auto;
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
    background: linear-gradient(180deg, #FFFFFF 0%, #5FBCFF 100%);
    -webkit-background-clip: text;
    background-clip: text;
    color: transparent;
    font-size: 22px;
    font-weight: 700;
    text-shadow: 0 0 12px rgba(95, 188, 255, 0.3);
  }

  .close-btn {
    position: absolute;
    right: 20px;
    top: 50%;
    transform: translateY(-50%);
    width: 32px;
    height: 32px;
    border: 1px solid rgba(69, 200, 255, 0.3);
    background: rgba(16, 64, 126, 0.4);
    color: #7AA7CF;
    font-size: 20px;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;

    &:hover {
      color: #ffffff;
      border-color: rgba(69, 200, 255, 0.6);
      background: rgba(24, 92, 179, 0.6);
    }
  }
}

.modal-content {
  flex: 1;
  min-height: 0;
  padding: 16px 32px 24px 32px;
  display: flex;
  gap: 24px;
  overflow: hidden;
}

/* 左侧设备信息面板 */
.left-panel {
  width: 300px;
  flex-shrink: 0;
  background: url('../img/bg_left_top01.png') center / 100% 100% no-repeat;
  padding: 16px 20px;
  position: relative;

  .panel-title {
    height: 32px;
    line-height: 32px;
    font-size: 16px;
    color: #8EC9FF;
    font-weight: 500;
    margin-bottom: 16px;
    padding-left: 8px;
    background: url('../img/bg_list_data-1.png') left center / cover no-repeat;
  }

  .info-list {
    list-style: none;
    padding: 0;
    margin: 0;

    li {
      line-height: 40px;
      display: flex;
      align-items: flex-start;
      color: #CFE8FF;
      font-size: 14px;

      .label {
        flex-shrink: 0;
        color: #8EC9FF;
      }

      .value {
        flex: 1;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }
  }
}

/* 右侧视频区域 */
.right-panel {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #000;
  border: 1px solid rgba(37, 134, 255, 0.25);

  .video-placeholder {
    object-fit: cover;
    background: #000;
  }
}
</style>