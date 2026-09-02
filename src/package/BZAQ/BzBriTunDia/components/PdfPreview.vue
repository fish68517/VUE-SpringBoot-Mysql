<template>
  <div class="pdf-preview-popup">
    <div class="popup-header">
      <div class="title-bg">
        <h3 class="title">{{ previewTitle }}</h3>
      </div>
      <img class="close-btn" :src="closeIcon" alt="关闭" @click="$emit('close')" />
    </div>

    <div class="popup-body">
      <iframe
        v-if="previewUrl"
        class="pdf-frame"
        :src="previewUrl"
        title="PDF预览"
      ></iframe>
      <div v-else class="preview-empty">暂无PDF文件</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import closeIcon from '../img/close.png'

const props = defineProps<{
  payload?: Record<string, any>
}>()

defineEmits<{
  (e: 'close'): void
}>()

const previewTitle = computed(() => props.payload?.title || props.payload?.name || 'PDF预览')
const previewUrl = computed(() => props.payload?.url || props.payload?.pdfUrl || props.payload?.src || '')
</script>

<script lang="ts">
export default {
  name: 'PdfPreview'
}
</script>

<style lang="scss" scoped>
.pdf-preview-popup {
  width: 1253px;
  height: 760px;
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  background: url('../img/diaBg.png') no-repeat center center;
  background-size: 100% 100%;
  display: flex;
  flex-direction: column;
  pointer-events: auto;
  color: #d8efff;
  font-family: 'Alibaba PuHuiTi 2.0', 'Microsoft YaHei', sans-serif;
}

.popup-header {
  width: 100%;
  height: 50px;
  position: relative;
  flex-shrink: 0;
}

.title-bg {
  width: 100%;
  height: 100%;
  background: url('../img/diaTitleBg.png') no-repeat center center;
  background-size: 100% 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.title {
  margin: 0;
  text-align: center;
  font-size: 20px;
  font-weight: 500;
  background: linear-gradient(0deg, #5FBCFF 1.4%, #FFF 58.67%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.close-btn {
  width: 24px;
  height: 24px;
  cursor: pointer;
  position: absolute;
  right: 18px;
  top: 50%;
  transform: translateY(-50%);
}

.popup-body {
  flex: 1;
  min-height: 0;
  box-sizing: border-box;
  padding: 18px 24px 24px;
}

.pdf-frame {
  width: 100%;
  height: 100%;
  border: 1px solid rgba(4, 111, 241, 0.45);
  background: rgba(1, 20, 52, 0.72);
}

.preview-empty {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #8bbce6;
  font-size: 18px;
  border: 1px solid rgba(4, 111, 241, 0.45);
  background: rgba(1, 20, 52, 0.72);
}
</style>
