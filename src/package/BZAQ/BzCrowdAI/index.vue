<template>
  <div class="bz-crowd-ai-wrapper">
    <!-- 机器人头像（右上角，点击弹出面板） -->
    <div class="robot-avatar" @click="showPanel = !showPanel">
      <img src="./img/机器人.png" alt="AI助手">
    </div>

    <!-- AI分析面板 -->
    <div class="ai-panel" v-if="showPanel">
      <div class="panel-inner">
        <!-- 左侧地点按钮组（垂直排列） -->
        <div class="place-buttons">
          <button 
            v-for="place in placeList" 
            :key="place.name"
            class="place-btn"
            :class="{ active: activePlace === place.name }"
            @click="changePlace(place.name)"
          >
            <span class="place-name">{{ place.name }}</span>
            <span class="place-percent">{{ place.percent || '0%' }}</span>
          </button>
        </div>

        <!-- 右侧文本区域 -->
        <div class="text-area">
          <div class="text-content" ref="textRef">{{ displayText }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import axios from 'axios'
import { ref, watch, nextTick, onMounted, onBeforeUnmount, defineProps } from 'vue'

const props = defineProps({
  chartConfig: {
    type: Object,
    required: true
  }
})

// 面板显示控制
const showPanel = ref(false)

// 地点列表
const placeList = ref([
  { name: '洪崖洞', percent: '0%' },
  { name: '解放碑', percent: '0%' },
  { name: '朝天门', percent: '0%' },
  { name: '山城巷', percent: '0%' },
  { name: '十八梯', percent: '0%' },
  { name: '李子坝', percent: '0%' },
  { name: '鹅岭栈桥', percent: '0%' },
  { name: '鹅岭二厂', percent: '0%' },
  { name: '时代天街', percent: '0%' }
])
const activePlace = ref('洪崖洞')

// 打字机相关
const fullText = ref('')
const displayText = ref('')
const textRef = ref<HTMLDivElement | null>(null)
let typingTimer: number | null = null
const typingSpeed = 15
let requestSerial = 0

// 清除打字机定时器和内容
function resetTyping() {
  if (typingTimer) {
    clearInterval(typingTimer)
    typingTimer = null
  }
  fullText.value = ''
  displayText.value = ''
}

function startTyping(text: string) {
  if (typingTimer) {
    clearInterval(typingTimer)
    typingTimer = null
  }

  displayText.value = ''
  let index = 0

  typingTimer = window.setInterval(async () => {
    displayText.value += text[index]
    index++

    await nextTick()
    if (textRef.value) {
      textRef.value.scrollTop = textRef.value.scrollHeight
    }

    if (index >= text.length) {
      clearInterval(typingTimer!)
      typingTimer = null
    }
  }, typingSpeed)
}

// 监听文本变化 → 重新打字
watch(
  fullText,
  (newText) => {
    if (!newText) return
    startTyping(newText.trimStart())
  }
)

let regionTime: number | null = null

// 请求AI数据，参数为当前选中的地点
function getAIdata() {
  // 清除之前的打字机，显示加载文字
  resetTyping()
  displayText.value = 'AI分析中...'

  const currentSerial = ++requestSerial

  axios({
    method: 'post',
    url: `http://23.210.227.34:23343/yzqzlzx/api/boot/system/common/ai/analysis?name=${activePlace.value}`,
    timeout: 50000
  })
    .then(res => {
      if (currentSerial !== requestSerial) return
      fullText.value = res.data?.data.replace(/[*#]/g, '') || '暂无该区域的分析建议'
    })
    .catch(err => {
      if (currentSerial !== requestSerial) return
      console.log(err, '接口错误')
      fullText.value = '暂无该区域的分析建议'
    })
}

// 切换地点
function changePlace(place: string) {
  if (activePlace.value === place) return
  activePlace.value = place
  getAIdata()
}

const BASE_URL = 'http://23.210.227.34:23343/yzqzlzx'

// 面板关闭时清除所有内容和定时器
watch(showPanel, async (val) => {
  if (val) {
    // 先获取拥堵百分比并排序，排序后会自动设置activePlace为第一个
    await fetchCongestionPercent()
    getAIdata()
  } else {
    resetTyping()
    requestSerial++ // 使进行中的请求失效
  }
})

// 获取各景点拥堵百分比
async function fetchCongestionPercent() {
  try {
    const res = await axios.post(`${BASE_URL}/api/boot/system/common/gdperson/list`)
    if (Array.isArray(res.data.data)) {
      const nameMap = new Map()
      res.data.data.forEach(item => {
        let percent = 0
        // 解析百分比，优先取percentage，再取congestionStatus
        if (item.percentage) {
          percent = parseFloat(String(item.percentage).replace('%', ''))
        } else if (item.congestionStatus && /^\d+(\.\d+)?%?$/.test(String(item.congestionStatus))) {
          percent = parseFloat(String(item.congestionStatus).replace('%', ''))
        }
        nameMap.set(item.scenicName, { percent, display: `${Math.round(percent)}%` })
      })
      // 更新placeList的百分比
      placeList.value = placeList.value.map(place => {
        const data = nameMap.get(place.name) || { percent: 0, display: '0%' }
        return { ...place, percent: data.display, rate: data.percent }
      })
      // 按百分比降序排序（参考BzCrowded组件）
      placeList.value = placeList.value.slice().sort((a, b) => (b.rate || 0) - (a.rate || 0))
      // 排序后取第一个地点调用AI接口
      activePlace.value = placeList.value[0].name
    }
  } catch (err) {
    console.error('获取拥堵百分比失败:', err)
  }
}

// 组件加载后：30分钟轮询
onMounted(() => {
  regionTime = window.setInterval(() => {
    if (showPanel.value) {
      fetchCongestionPercent()
      getAIdata()
    }
  }, 300000)
})

/* 组件销毁时统一清理 */
onBeforeUnmount(() => {
  if (typingTimer) {
    clearInterval(typingTimer)
  }
  if (regionTime) {
    clearInterval(regionTime)
  }
})
</script>



<script lang="ts">
export default {
  name: "BzCrowdAI",
  version: "1.0.0",
};
</script>

<style lang="scss" scoped>
// 最外层容器，占满父元素大小
.bz-crowd-ai-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: visible;
  pointer-events: none;
}

// 机器人头像（相对于外层容器右上角定位）
.robot-avatar {
  position: absolute;
  top: 10px;
  right: 20px;
  width: 84px;
  height: 114px;
  cursor: pointer;
  z-index: 999;
  pointer-events: auto;

  img {
    width: 100%;
    height: 100%;
    object-fit: contain;
  }
}

// AI分析面板（相对于外层容器右上角定位）
.ai-panel {
  position: absolute;
  top: 134px; // 头像高度114 + 间距20
  right: 20px;
  width: 471px;
  height: 341px;
  pointer-events: auto;
  background: url('./img/ai-panel-bg.png') no-repeat center center / cover;
  border: 1px solid rgba(0, 160, 255, 0.5);
  box-shadow: 0 0 30px rgba(0, 160, 255, 0.3);
  border-radius: 4px;
  padding: 24px 20px;
  box-sizing: border-box;
  z-index: 998;

  // 顶部科技感装饰角
  &::before, &::after {
    content: '';
    position: absolute;
    width: 8px;
    height: 8px;
    border: 2px solid #00a0ff;
    top: 0;
  }
  &::before { left: 0; border-right: none; border-bottom: none; }
  &::after { right: 0; border-left: none; border-bottom: none; }
}

// 面板内部布局（左右）
.panel-inner {
  display: flex;
  gap: 20px;
  height: 100%;
}

// 地点按钮组（垂直排列）
.place-buttons {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex-shrink: 0;
  justify-content: space-between;
}

.place-btn {
  display: flex;
  width: 110px;
  height: 24px;
  justify-content: space-between;
  align-items: center;
  padding: 0 8px;
  border: 1px solid #336FB0;
  background: linear-gradient(180deg, #074d7f17 0%, #0d9bff33 100%);
  color: #ffffffa6;
  text-align: left;
  font-size: 14px;
  font-weight: 400;
  white-space: nowrap;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;

  .place-percent {
    color: #00d4ff;
    font-weight: 600;
  }

  &.active {
    background: linear-gradient(180deg, #007ae333 0%, #007ae380 100%);
    color: #ffffff;
    text-shadow: 0 0 12px #0D9BFF, 0 0 4px #0D9BFF;
    font-weight: 600;

    .place-percent {
      color: #fff;
    }
  }

  &:hover {
    border-color: #0D9BFF;
    color: #fff;
  }
}

// 文本区域
.text-area {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  .text-label {
    font-size: 16px;
    color: #f8ea56;
    font-weight: 600;
    margin-bottom: 12px;
    flex-shrink: 0;
  }
  .text-content {
    flex: 1;
    overflow-y: auto;
    overflow-x: hidden;
    scrollbar-width: none;
    -ms-overflow-style: none;

    &::-webkit-scrollbar {
      display: none;
    }

    white-space: pre-wrap;
    word-break: break-word;
    overflow-wrap: anywhere;
    font-size: 14px;
    line-height: 1.8;
    color: #fff;
  }
}
</style>
