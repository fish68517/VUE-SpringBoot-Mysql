<template>
  <div class="fire-random-modal">
    <div class="modal-header">
      <div class="header-title">双随机隐患整改</div>
      <button class="close-btn" @click="handleClose">×</button>
    </div>

    <div class="modal-content">
      <!-- 立即整改区域 -->
      <div class="section">
        <div class="section-title">责令立即改正的消防安全违法行为</div>
        <div class="list-container">
          <div v-if="immediatelyList.length === 0" class="empty-state">
            暂无数据
          </div>
          <div 
            v-else
            class="list-item" 
            v-for="(item, index) in immediatelyList" 
            :key="`immediately-${index}`"
            :class="{ 'even': index % 2 === 1 }"
          >
            <span class="item-name">{{ index + 1 }}. {{ item.doubleName }}</span>
            <span class="item-count">
              <span class="orange">{{ item.dangerZgCount || 0 }}</span> / {{ item.dangerFxCount || 0 }}
            </span>
          </div>
        </div>
      </div>

      <!-- 限期整改区域 -->
      <div class="section">
        <div class="section-title">责令限期改正的消防安全违法行为</div>
        <div class="list-container">
          <div v-if="limitList.length === 0" class="empty-state">
            暂无数据
          </div>
          <div 
            v-else
            class="list-item" 
            v-for="(item, index) in limitList" 
            :key="`limit-${index}`"
            :class="{ 'even': index % 2 === 1 }"
          >
            <span class="item-name">{{ index + 1 }}. {{ item.doubleName }}</span>
            <span class="item-count">
              <span class="orange">{{ item.dangerZgCount || 0 }}</span> / {{ item.dangerFxCount || 0 }}
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { BASE_URL } from '../config'

const emit = defineEmits(['close'])

const immediatelyList = ref([])
const limitList = ref([])
const getImmediatelyList = async () => {
  try {
    const res = await axios.post(`${BASE_URL}/list/double-random/immediately`, {
      pageNumber: 1,
      pageSize: 1000 // 一次性获取所有数据
    })
    if (res.data.code === '000000') {
      immediatelyList.value = res.data.data.records || []
    } else {
      console.error('获取立即整改隐患列表失败：' + res.data.message)
    }
  } catch (err) {
    console.error('请求立即整改接口失败', err)
  }
}

// 获取限期整改隐患列表
const getLimitList = async () => {
  try {
    const res = await axios.post(`${BASE_URL}/list/double-random/limit`, {
      pageNumber: 1,
      pageSize: 1000 // 一次性获取所有数据
    })
    if (res.data.code === '000000') {
      limitList.value = res.data.data.records || []
    } else {
      console.error('获取限期整改隐患列表失败：' + res.data.message)
    }
  } catch (err) {
    console.error('请求限期整改接口失败', err)
  }
}

function handleClose() {
  emit('close')
}

onMounted(() => {
  getImmediatelyList()
  getLimitList()
})
</script>

<script>
export default {
  name: "FireRadom"
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

.fire-random-modal {
  font-family: 'AlibabaPuHuiTi', sans-serif;
  position: absolute;
  transform: translate(-50%, -50%);
  z-index: 1001;
  width: 1576px;
  height: 700px;
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
    font-size: 22px;
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
  padding: 20px 32px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  gap: 20px;

  .section {
    flex: 1;
    display: flex;
    flex-direction: column;
    min-height: 0;

    .section-title {
      height: 48px;
      line-height: 48px;
      background: linear-gradient(90deg, rgba(24, 92, 179, 0.8) 0%, rgba(11, 67, 124, 0.8) 100%);
      color: #ffffff;
      font-size: 18px;
      font-weight: 600;
      text-align: center;
      border-radius: 4px 4px 0 0;
      margin-bottom: 4px;
    }

    .list-container {
        flex: 1;
        min-height: 0;
        overflow-y: auto;
        background: #07213F;
        border-radius: 0 0 4px 4px;
        padding: 4px;

        &::-webkit-scrollbar {
          width: 6px;
        }

        &::-webkit-scrollbar-track {
          background: rgba(16, 64, 126, 0.2);
          border-radius: 3px;
        }

        &::-webkit-scrollbar-thumb {
          background: rgba(37, 134, 255, 0.5);
          border-radius: 3px;

          &:hover {
            background: rgba(37, 134, 255, 0.7);
          }
        }

        .empty-state {
          height: 100%;
          display: flex;
          align-items: center;
          justify-content: center;
          color: #669acc;
          font-size: 16px;
        }

        .list-item {
          display: flex;
          align-items: center;
          justify-content: space-between;
          height: 48px;
          padding: 0 20px;
          background: #0A325F;
          margin-bottom: 4px;
          border-radius: 4px;

          &.even {
            background: #19406B;
          }

          &:hover {
            background: rgba(29, 107, 202, 0.3);
          }

          .item-name {
            color: #d7e9ff;
            font-size: 15px;
          }

          .item-count {
            font-family: 'D-DIN', sans-serif;
            font-size: 16px;
            color: #5ac8fa;

            .orange {
              color: #ff9500;
            }
          }
        }
      }
  }
}
</style>
