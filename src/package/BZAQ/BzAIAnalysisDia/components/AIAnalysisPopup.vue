<template>
  <div class="ai-analysis-popup">
    <div class="popup-header">
      <span class="popup-title">{{ title }}</span>
      <button class="popup-close" @click="$emit('close')">×</button>
    </div>
    
    <div class="popup-body">
      <div class="ai-section">
        <div class="ai-section-title">
          <span class="ai-icon">🤖</span>
          <span>AI分析</span>
        </div>
        
        <div class="ai-content">
          <div class="ai-item">
            <span class="ai-label">是否需要一键调度</span>
            <span class="ai-value ai-value-yes">{{ analysisData.dispatchSuggestion }}</span>
          </div>
          <div class="ai-item">
            <span class="ai-label">风险等级</span>
            <span class="ai-value ai-value-level">{{ analysisData.emergencyLevel }}</span>
          </div>
          

          
          <div class="ai-item ai-item-column">
            <span class="ai-label">处置建议</span>
            <span class="ai-value ai-value-desc">{{ analysisData.suggestion }}</span>
          </div>
        </div>
      </div>
    </div>
    
    <div class="popup-footer">
      <button class="dispatch-btn" @click="handleDispatch">一键调度</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'

const emit = defineEmits(['close', 'dispatch'])

const props = defineProps({
  title: {
    type: String,
    default: '渝中区南纪门街道凯旋路22号附9号居民楼后侧崩塌'
  },
  analysisData: {
    type: Object,
    default: () => ({
      emergencyLevel: '较重要',
      dispatchSuggestion: '是',
      suggestion: '此次风险较为重大，正在联系相关部门进行处理。'
    })
  }
})

const localData = reactive({
  emergencyLevel: props.analysisData?.emergencyLevel || '较重要',
  dispatchSuggestion: props.analysisData?.dispatchSuggestion || '是',
  suggestion: props.analysisData?.suggestion || '此次风险较为重大，正在联系相关部门进行处理。'
})

const handleDispatch = () => {
  emit('dispatch')
}
</script>

<script lang="ts">
export default {
  name: "AIAnalysisPopup",
  version: "1.0.0"
}
</script>

<style lang="scss" scoped>
.ai-analysis-popup {
  width: 312px;
  height: 294px;
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
  display: flex;
  flex-direction: column;
}

.popup-header {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 10px 36px;
  background: linear-gradient(90deg, rgba(17, 84, 139, 0.9) 0%, rgba(6, 47, 78, 0.8) 100%);
  border-bottom: 1px solid rgba(33, 145, 244, 0.3);
  position: relative;
}

.popup-title {
  font-size: 15px;
  font-weight: 700;
  color: #fff;
  text-align: center;
  line-height: 1.4;
  word-break: break-all;
}

.popup-close {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  width: 24px;
  height: 24px;
  border: none;
  background: transparent;
  color: #fff;
  font-size: 20px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;

  &:hover {
    color: #ff6b6b;
  }
}

.popup-body {
  flex: 1;
  padding: 14px 16px;
  overflow-y: auto;
}

.ai-section {
  width: 100%;
}

.ai-section-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 600;
  color: #47a8ff;
  margin-bottom: 12px;
}

.ai-icon {
  font-size: 16px;
}

.ai-content {
  background: rgba(71, 168, 255, 0.08);
  border: 1px solid rgba(71, 168, 255, 0.15);
  border-radius: 4px;
  padding: 12px 14px;
}

.ai-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  font-size: 13px;

  &:last-child {
    margin-bottom: 0;
  }
}

.ai-item-column {
  flex-direction: column;
  align-items: flex-start;
  gap: 6px;
}

.ai-label {
  color: #8bbce6;
  flex-shrink: 0;
}

.ai-value {
  color: #e8f8ff;
}

.ai-value-yes {
  color: #47a8ff;
  font-weight: 700;
}

.ai-value-level {
  color: #ffa502;
  font-weight: 700;
}

.ai-value-desc {
  color: #c4daef;
  line-height: 1.6;
  font-size: 12px;
}

.popup-footer {
  padding: 10px 16px;
  background: rgba(5, 33, 62, 0.5);
}

.dispatch-btn {
  width: 100%;
  padding: 10px 0;
  background: linear-gradient(180deg, #47a8ff 0%, #0454cb 100%);
  border: none;
  border-radius: 4px;
  color: #fff;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    opacity: 0.9;
    box-shadow: 0 4px 12px rgba(71, 168, 255, 0.4);
  }

  &:active {
    transform: scale(0.98);
  }
}

::-webkit-scrollbar {
  width: 6px;
}

::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.2);
}

::-webkit-scrollbar-thumb {
  background: rgba(33, 145, 244, 0.5);
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: rgba(33, 145, 244, 0.7);
}
</style>
