<template>
  <div class="pdf-preview-popup" :style="popupStyle">
    <div class="pdf-preview-header">
      <span class="pdf-preview-title">{{ title || 'PDF预览' }}</span>
      <button class="pdf-preview-close" type="button" @click="$emit('close')">×</button>
    </div>
    <div class="pdf-preview-body">
      <iframe
        v-if="pdfUrl"
        class="pdf-preview-frame"
        :src="pdfUrl"
        title="PDF预览"
      ></iframe>
      <div v-else class="pdf-preview-empty">暂无可预览文件</div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps({
  title: {
    type: String,
    default: 'PDF预览'
  },
  pdfUrl: {
    type: String,
    default: ''
  },
  popupStyle: {
    type: Object,
    default: () => ({})
  }
})

defineEmits(['close'])
</script>

<script lang="ts">
export default {
  name: 'PdfPreviewPopup',
  version: '1.0.0'
}
</script>

<style lang="scss" scoped>
.pdf-preview-popup {
  position: absolute;
  z-index: 1001;
  width: 1200px;
  height: 960px;
  max-width: calc(100vw - 40px);
  max-height: calc(100vh - 40px);
  transform: translate(-50%, -50%);
  display: flex;
  flex-direction: column;
  pointer-events: auto;
  background: rgba(5, 28, 58, 0.98);
  border: 1px solid rgba(33, 145, 244, 0.55);
  box-shadow: 0 0 24px rgba(0, 123, 255, 0.25);
}

.pdf-preview-header {
  height: 48px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  color: #e8f8ff;
  font-size: 18px;
  font-weight: 600;
  background: linear-gradient(90deg, rgba(10, 64, 126, 0.95), rgba(8, 42, 82, 0.95));
  border-bottom: 1px solid rgba(33, 145, 244, 0.35);
}

.pdf-preview-title {
  max-width: calc(100% - 96px);
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.pdf-preview-close {
  position: absolute;
  right: 16px;
  top: 50%;
  transform: translateY(-50%);
  width: 28px;
  height: 28px;
  border: 0;
  background: transparent;
  color: #fff;
  font-size: 24px;
  line-height: 1;
  cursor: pointer;
}

.pdf-preview-body {
  flex: 1;
  overflow: auto;
  background: #10233d;

  &::-webkit-scrollbar {
    width: 8px;
    height: 8px;
  }

  &::-webkit-scrollbar-thumb {
    background: rgba(71, 168, 255, 0.45);
    border-radius: 8px;
  }

  &::-webkit-scrollbar-track {
    background: rgba(255, 255, 255, 0.08);
  }
}

.pdf-preview-frame {
  width: 100%;
  min-height: 1200px;
  border: 0;
  display: block;
  background: #fff;
}

.pdf-preview-empty {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #8bbce6;
  font-size: 16px;
}
</style>
