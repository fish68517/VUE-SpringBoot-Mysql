<template>
  <n-config-provider>
    <n-message-provider>
      <MessageContent />
      <div class="test-page">
        <div class="test-header">
          <h1>BzAIAnalysisDia AI分析弹窗测试</h1>
          <div class="test-controls">
            <button @click="openPopup">打开AI分析弹窗</button>
            <button @click="closePopup">关闭弹窗</button>
          </div>
        </div>
        <div class="test-content">
          <div class="test-area">
            <BzAIAnalysisDia :chart-config="chartConfig" :bus="eventBus"></BzAIAnalysisDia>
          </div>
        </div>
      </div>
    </n-message-provider>
  </n-config-provider>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import BzAIAnalysisDia from '@/package/BZAQ/BzAIAnalysisDia/index.vue'
import { MessageContent } from '@/plugins/MessageContent'

const chartConfig = reactive({
  attr: {
    w: 800,
    h: 600
  }
})

const eventBus = {
  events: {},
  on(event, callback) {
    if (!this.events[event]) {
      this.events[event] = []
    }
    this.events[event].push(callback)
  },
  off(event, callback) {
    if (this.events[event]) {
      this.events[event] = this.events[event].filter(cb => cb !== callback)
    }
  },
  emit(event, data) {
    console.log('Event:', event, data)
    if (this.events[event]) {
      this.events[event].forEach(callback => callback(data))
    }
  }
}

const openPopup = () => {
  eventBus.emit('OPEN_DIA', {
    source: 'TestPage',
    data: {
      diaName: 'ai-analysis',
      data: {
        title: '渝中区南纪门街道凯旋路22号附9号居民楼后侧崩塌',
        analysisData: {
          needDispatch: '是',
          riskLevel: '小型',
          suggestion: '此次风险较为重大，正在联系相关部门进行处理。'
        }
      }
    }
  })
}

const closePopup = () => {
  eventBus.emit('CLOSE_DIA', {
    source: 'TestPage'
  })
}
</script>

<style lang="scss" scoped>
.test-page {
  min-height: 100vh;
  background: #0a1628;
  padding: 20px;
}

.test-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  
  h1 {
    color: #fff;
    font-size: 20px;
    margin: 0;
  }
}

.test-controls {
  display: flex;
  gap: 10px;
  
  button {
    padding: 8px 16px;
    background: linear-gradient(180deg, #47a8ff 0%, #0454cb 100%);
    border: none;
    border-radius: 4px;
    color: #fff;
    cursor: pointer;
    font-size: 14px;
    
    &:hover {
      opacity: 0.9;
    }
  }
}

.test-content {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding: 20px;
}

.test-area {
  width: 800px;
  height: 600px;
  background: rgba(5, 33, 62, 0.3);
  border: 1px dashed rgba(33, 145, 244, 0.3);
  position: relative;
}
</style>
