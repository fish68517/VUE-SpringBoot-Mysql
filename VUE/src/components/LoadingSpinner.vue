<template>
  <div v-if="visible" class="loading-overlay">
    <div class="loading-overlay-content">
      <div class="loading-spinner"></div>
      <div v-if="text" class="loading-overlay-text">{{ text }}</div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const visible = ref(false)
const text = ref('')

const show = (loadingText = '') => {
  text.value = loadingText
  visible.value = true
}

const hide = () => {
  visible.value = false
  text.value = ''
}

defineExpose({
  show,
  hide,
  visible
})
</script>

<style scoped>
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  animation: fadeIn 0.3s ease-in-out;
}

.loading-overlay-content {
  background-color: var(--bg-secondary);
  padding: var(--spacing-xl);
  border-radius: var(--radius-lg);
  text-align: center;
  box-shadow: var(--shadow-xl);
  animation: slideIn 0.3s ease-in-out;
}

.loading-spinner {
  display: inline-block;
  width: 40px;
  height: 40px;
  border: 4px solid rgba(24, 144, 255, 0.3);
  border-top-color: var(--primary-color);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

.loading-overlay-text {
  margin-top: var(--spacing-md);
  color: var(--text-primary);
  font-size: var(--font-size-md);
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes slideIn {
  from {
    transform: translateY(-10px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}
</style>
