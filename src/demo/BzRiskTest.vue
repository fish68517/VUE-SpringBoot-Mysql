<template>
  <n-config-provider>
    <n-message-provider>
      <MessageContent />
      <div class="test-page">
        <div class="test-header">
          <h1>BzRisk 风险隐患组件测试</h1>
          <div class="test-controls">
            <button @click="refreshData">刷新数据</button>
          </div>
        </div>
        <div class="test-process">
          <BzProcess :chart-config="processConfig" :bus="eventBus"></BzProcess>
        </div>
        <div class="test-content">
          <BzRisk :chart-config="riskConfig" :bus="eventBus"></BzRisk>
          <BzFeedback :chart-config="feedbackConfig" :bus="eventBus"></BzFeedback>
          <BzAIAnalysisDia :chart-config="diaConfig" :bus="eventBus"></BzAIAnalysisDia>
          <BzOneEventPoint :chart-config="pointConfig" :bus="eventBus"></BzOneEventPoint>
        </div>
      </div>
    </n-message-provider>
  </n-config-provider>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import BzRisk from '@/package/BZAQ/BzRisk/index.vue'
import BzAIAnalysisDia from '@/package/BZAQ/BzAIAnalysisDia/index.vue'
import BzFeedback from '@/package/BZAQ/BzFeedback/index.vue'
import BzProcess from '@/package/BZAQ/BzProcess/index.vue'
import BzOneEventPoint from '@/package/BZAQ/BzOneEventPoint/index.vue'
import { MessageContent } from '@/plugins/MessageContent'

const riskConfig = reactive({
  attr: {
    w: 480,
    h: 915
  }
})

const feedbackConfig = reactive({
  attr: {
    w: 480,
    h: 915
  }
})

const processConfig = reactive({
  attr: {
    w: 530,
    h: 64
  }
})

const diaConfig = reactive({
  attr: {
    w: 400,
    h: 400
  }
})

const pointConfig = reactive({
  attr: {
    w: 1,
    h: 1
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

function refreshData() {
  console.log('刷新数据')
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

.test-process {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
  padding: 10px;
  background: rgba(71, 168, 255, 0.05);
  border: 1px solid rgba(71, 168, 255, 0.15);
  border-radius: 4px;
}

.test-content {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding: 20px;
  gap: 20px;
  position: relative;
}
</style>
