<template>
  <transition name="slide-fade">
    <div v-if="visible" :class="['form-feedback', `form-feedback--${type}`]">
      <div class="feedback-content">
        <i :class="['feedback-icon', iconClass]"></i>
        <div class="feedback-text">
          <p class="feedback-title">{{ title }}</p>
          <p v-if="message" class="feedback-message">{{ message }}</p>
        </div>
        <button class="feedback-close" @click="close">
          <i class="el-icon-close"></i>
        </button>
      </div>
    </div>
  </transition>
</template>

<script setup>
import { defineProps, defineEmits, computed } from 'vue'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  type: {
    type: String,
    enum: ['success', 'error', 'warning', 'info'],
    default: 'info'
  },
  title: {
    type: String,
    default: ''
  },
  message: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['close'])

const iconClass = computed(() => {
  const icons = {
    success: 'el-icon-success',
    error: 'el-icon-circle-close',
    warning: 'el-icon-warning',
    info: 'el-icon-info'
  }
  return icons[props.type] || icons.info
})

const close = () => {
  emit('close')
}
</script>

<style scoped>
.form-feedback {
  padding: 12px 16px;
  border-radius: 4px;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  animation: slideIn 0.3s ease-out;
}

.form-feedback--success {
  background-color: #f0f9ff;
  border: 1px solid #c6e2ff;
  color: #0a66cc;
}

.form-feedback--error {
  background-color: #fef0f0;
  border: 1px solid #fde2e2;
  color: #f56c6c;
}

.form-feedback--warning {
  background-color: #fdf6ec;
  border: 1px solid #f5dab1;
  color: #e6a23c;
}

.form-feedback--info {
  background-color: #f4f4f5;
  border: 1px solid #ebeef5;
  color: #909399;
}

.feedback-content {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
}

.feedback-icon {
  font-size: 18px;
  flex-shrink: 0;
}

.feedback-text {
  flex: 1;
}

.feedback-title {
  margin: 0;
  font-weight: 500;
  font-size: 14px;
}

.feedback-message {
  margin: 4px 0 0 0;
  font-size: 12px;
  opacity: 0.8;
}

.feedback-close {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 16px;
  padding: 0;
  flex-shrink: 0;
  opacity: 0.6;
  transition: opacity 0.2s;
}

.feedback-close:hover {
  opacity: 1;
}

.slide-fade-enter-active,
.slide-fade-leave-active {
  transition: all 0.3s ease;
}

.slide-fade-enter-from {
  transform: translateX(-10px);
  opacity: 0;
}

.slide-fade-leave-to {
  transform: translateX(-10px);
  opacity: 0;
}

@keyframes slideIn {
  from {
    transform: translateX(-10px);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}
</style>
