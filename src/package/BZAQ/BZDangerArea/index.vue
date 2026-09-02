<template>
  <div class="bz-danger-area" :style="containerStyle">
    <div class="area-list">
      <div
        v-for="item in areaList"
        :key="item.name"
        class="area-item"
        @click="handleAreaClick(item)"
      >
        <div class="status" :class="{ congestion: item.status === '拥堵' || item.status === '较拥堵' }">
          {{ item.status || '畅通' }}
        </div>
        <div class="base-img"></div>
        <div class="name">{{ item.name }}</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue'
import axios from 'axios'

const props = defineProps({
  chartConfig: {
    type: Object,
    required: true
  },
  publicParamList: {
    type: Object,
    required: false,
    default: () => []
  },
  bus: {
    type: Object,
    required: false
  }
})

const sourceName = 'BZDangerArea'
let fetchTimer = null

// 容器宽高跟随低代码平台拖动的 attr.w / attr.h
const containerStyle = computed(() => {
  const width = Number(props.chartConfig?.attr?.w)
  const height = Number(props.chartConfig?.attr?.h)
  return {
    width: Number.isFinite(width) && width > 0 ? width + 'px' : '100%',
    height: Number.isFinite(height) && height > 0 ? height + 'px' : 'auto'
  }
})

// 通用接口基础地址（和BzCrowded保持一致）
const BASE_URL = 'http://23.210.227.34:23343/yzqzlzx'

// 区域列表，匹配设计图顺序
const areaList = ref([
  { name: '洪崖洞', status: '畅通' },
  { name: '朝天门', status: '畅通' },
  { name: '解放碑', status: '畅通' },
  { name: '鹅岭栈道', status: '畅通' },
  { name: '十八梯', status: '畅通' },
  { name: '李子坝', status: '畅通' },
  { name: '山城巷', status: '畅通' },
  { name: '鹅岭二厂', status: '畅通' },
  { name: '时代天街', status: '畅通' }
])

// 获取拥堵数据（复用BzCrowded的客流接口）
async function fetchCongestionData() {
  try {
    const res = await axios.post(BASE_URL + '/api/boot/system/common/gdperson/list')
    if (Array.isArray(res.data.data)) {
      const nameMap = new Map()
      res.data.data.forEach(item => {
        nameMap.set(item.scenicName, item)
      })
      
      areaList.value = areaList.value.map(card => {
        const data = nameMap.get(card.name)
        if (data) {
          let status = '畅通'
          if (data.congestionStatus && !/^-?\d+(\.\d+)?\s*%?$/.test(data.congestionStatus.trim())) {
            status = data.congestionStatus
          } else {
            // 如果状态是百分比，按阈值判断
            const rate = parseFloat(String(data.congestionStatus || data.percentage || 0).replace('%', ''))
            status = rate >= 80 ? '拥堵' : rate >= 50 ? '缓慢' : '畅通'
          }
          return { ...card, status }
        }
        return card
      })
    }
  } catch (err) {
    console.error('获取拥堵数据失败:', err)
  }
}

onMounted(() => {
  fetchCongestionData()
  // 每3分钟轮询刷新数据
  fetchTimer = setInterval(fetchCongestionData, 3 * 60 * 1000)
})

onUnmounted(() => {
  // 组件销毁时清除定时器
  clearInterval(fetchTimer)
})

// 点击区域项，通过事件总线发送地点名称到 BzPeoHeatMap
function handleAreaClick(item) {
  eventBus.emit('BZDANGER_AREA_CLICK', item.name)
}

// 事件总线定义
const eventBus = {
  on: (event, callback) => {
    props.bus?.on(event, ({ source, data }) => {
      console.log('🚀 ~ eventBus on:', event, source, data)
      if (source === sourceName) return
      callback(data)
    })
  },
  off: (event, callback) => {
    props.bus?.off(event, callback)
  },
  emit: (event, data) => {
    console.log('🚀 ~ eventBus emit:', event, data)
    props.bus?.emit(event, { data, source: sourceName })
  }
}
</script>

<script lang="ts">
export default {
  name: "BZDangerArea",
  version: "1.0.0",
};
</script>

<style lang="scss" scoped>
.bz-danger-area {
  width: 943px;
  height: 110px;
  box-sizing: border-box;
  font-family: 'Alibaba PuHuiTi', Microsoft YaHei, PingFang SC, Arial, sans-serif;
  overflow-y: auto;
  overflow-x: hidden;
  scrollbar-width: none;
  -ms-overflow-style: none;

  &::-webkit-scrollbar {
    display: none;
  }
}

.area-list {
  display: flex;
  flex-wrap: wrap;
  width: 100%;
  justify-content: flex-start;
  align-items: flex-end;
  align-content: flex-start;
  gap: 12px;
}

.area-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  width: 90px;
  box-sizing: border-box;
  cursor: pointer;

  .status {
    font-size: 20px;
    font-weight: 700;
    background: linear-gradient(180deg, #9FFF70 0%, #00D25F 100%);
    -webkit-background-clip: text;
    background-clip: text;
    color: transparent;
    line-height: 1.2;
    flex-shrink: 0;

    &.congestion {
      background: linear-gradient(180deg, #FFE782 0%, #FF9036 100%);
      -webkit-background-clip: text;
      background-clip: text;
      color: transparent;
    }
  }

  .base-img {
    width: 88px;
    height: 36px;
    background: url('./img/底座.png') center center / 100% 100% no-repeat;
    flex-shrink: 0;
  }

  .name {
    font-size: 14px;
    font-weight: 700;
    color: #A9D8FF;
    line-height: 1.2;
    text-align: center;
    flex-shrink: 0;
  }
}
</style>
