<template>
  <div class="toast-container" :class="positionClass">
    <transition-group name="toast" tag="div">
      <div
        v-for="toast in toasts"
        :key="toast.id"
        class="toast"
        :class="[`toast-${toast.type}`, { 'has-icon': toast.icon }]"
      >
        <span v-if="toast.icon" class="toast-icon">{{ toast.icon }}</span>
        <div class="toast-content">
          <p v-if="toast.title" class="toast-title">{{ toast.title }}</p>
          <p class="toast-message">{{ toast.message }}</p>
        </div>
        <button class="toast-close" @click="removeToast(toast.id)">×</button>
      </div>
    </transition-group>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const toasts = ref([])
let nextId = 0

const props = defineProps({
  position: {
    type: String,
    default: 'top-right',
    validator: (value) =>
      ['top-left', 'top-right', 'bottom-left', 'bottom-right', 'top-center', 'bottom-center'].includes(value)
  }
})

const positionClass = computed(() => `toast-${props.position}`)

const addToast = (message, type = 'info', options = {}) => {
  const id = nextId++
  const toast = {
    id,
    message,
    type,
    title: options.title,
    icon: options.icon,
    duration: options.duration !== undefined ? options.duration : 3000
  }

  toasts.value.push(toast)

  if (toast.duration > 0) {
    setTimeout(() => removeToast(id), toast.duration)
  }

  return id
}

const removeToast = (id) => {
  const index = toasts.value.findIndex((t) => t.id === id)
  if (index > -1) {
    toasts.value.splice(index, 1)
  }
}

const success = (message, options = {}) => {
  return addToast(message, 'success', {
    icon: '✓',
    ...options
  })
}

const error = (message, options = {}) => {
  return addToast(message, 'error', {
    icon: '✕',
    ...options
  })
}

const warning = (message, options = {}) => {
  return addToast(message, 'warning', {
    icon: '⚠',
    ...options
  })
}

const info = (message, options = {}) => {
  return addToast(message, 'info', {
    icon: 'ℹ',
    ...options
  })
}

defineExpose({
  addToast,
  removeToast,
  success,
  error,
  warning,
  info
})
</script>

<style scoped>
.toast-container {
  position: fixed;
  z-index: 9998;
  pointer-events: none;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.toast-top-left {
  top: var(--spacing-lg);
  left: var(--spacing-lg);
}

.toast-top-right {
  top: var(--spacing-lg);
  right: var(--spacing-lg);
}

.toast-bottom-left {
  bottom: var(--spacing-lg);
  left: var(--spacing-lg);
}

.toast-bottom-right {
  bottom: var(--spacing-lg);
  right: var(--spacing-lg);
}

.toast-top-center {
  top: var(--spacing-lg);
  left: 50%;
  transform: translateX(-50%);
}

.toast-bottom-center {
  bottom: var(--spacing-lg);
  left: 50%;
  transform: translateX(-50%);
}

.toast {
  display: flex;
  align-items: flex-start;
  gap: var(--spacing-md);
  padding: var(--spacing-md) var(--spacing-lg);
  background-color: var(--bg-primary);
  border-radius: var(--border-radius-md);
  box-shadow: var(--shadow-lg);
  pointer-events: auto;
  min-width: 300px;
  max-width: 500px;
  border-left: 4px solid var(--info-color);
  animation: slideIn 0.3s ease-out;
}

.toast-info {
  border-left-color: var(--info-color);
}

.toast-success {
  border-left-color: var(--success-color);
}

.toast-warning {
  border-left-color: var(--warning-color);
}

.toast-error {
  border-left-color: var(--error-color);
}

.toast-icon {
  font-size: var(--font-size-lg);
  font-weight: 600;
  min-width: 24px;
  text-align: center;
}

.toast-info .toast-icon {
  color: var(--info-color);
}

.toast-success .toast-icon {
  color: var(--success-color);
}

.toast-warning .toast-icon {
  color: var(--warning-color);
}

.toast-error .toast-icon {
  color: var(--error-color);
}

.toast-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

.toast-title {
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
  font-size: var(--font-size-base);
}

.toast-message {
  color: var(--text-secondary);
  margin: 0;
  font-size: var(--font-size-sm);
  line-height: 1.5;
}

.toast-close {
  background: none;
  border: none;
  color: var(--text-light);
  font-size: var(--font-size-xl);
  cursor: pointer;
  padding: 0;
  min-width: auto;
  transition: color 0.3s ease;
  align-self: flex-start;
}

.toast-close:hover {
  color: var(--text-primary);
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(100%);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.toast-enter-active,
.toast-leave-active {
  transition: all 0.3s ease;
}

.toast-enter-from {
  opacity: 0;
  transform: translateX(100%);
}

.toast-leave-to {
  opacity: 0;
  transform: translateX(100%);
}

@media (max-width: 768px) {
  .toast-container {
    gap: var(--spacing-sm);
  }

  .toast-top-left,
  .toast-top-right,
  .toast-bottom-left,
  .toast-bottom-right {
    left: var(--spacing-md) !important;
    right: var(--spacing-md) !important;
    top: auto !important;
    bottom: auto !important;
  }

  .toast-top-left,
  .toast-top-right {
    top: var(--spacing-md) !important;
  }

  .toast-bottom-left,
  .toast-bottom-right {
    bottom: var(--spacing-md) !important;
  }

  .toast {
    min-width: auto;
    max-width: 100%;
    padding: var(--spacing-md);
  }
}
</style>
