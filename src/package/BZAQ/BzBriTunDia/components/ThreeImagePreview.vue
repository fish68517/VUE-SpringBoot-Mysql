<template>
  <div class="three-image-preview-popup">
    <div class="popup-header">
      <div class="title-bg">
        <h3 class="title">{{ previewTitle }}</h3>
      </div>
      <img class="close-btn" :src="closeIcon" alt="关闭" @click="$emit('close')" />
    </div>

    <div class="popup-body">
      <div class="image-stage">
        <img class="preview-image" :src="previewImage" alt="3D图片预览" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import closeIcon from '../img/close.png'
import defaultImage from '../img/example.png'

const props = defineProps<{
  payload?: Record<string, any>
}>()

defineEmits<{
  (e: 'close'): void
}>()

const previewTitle = computed(() => props.payload?.title || props.payload?.name || '3D图片预览')
const previewImage = computed(() => (
  props.payload?.imageUrl ||
  props.payload?.imgUrl ||
  props.payload?.url ||
  props.payload?.src ||
  defaultImage
))
</script>

<script lang="ts">
export default {
  name: 'ThreeImagePreview'
}
</script>

<style lang="scss" scoped>
.three-image-preview-popup {
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

.image-stage {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(4, 111, 241, 0.45);
  background:
    radial-gradient(circle at center, rgba(25, 116, 210, 0.2), rgba(1, 20, 52, 0.78) 58%),
    rgba(1, 20, 52, 0.72);
  overflow: hidden;
}

.preview-image {
  max-width: calc(100% - 32px);
  max-height: calc(100% - 32px);
  object-fit: contain;
  box-shadow: 0 0 22px rgba(4, 188, 250, 0.28);
}
</style>
