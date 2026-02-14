<template>
  <div v-if="isLoading" class="loading-overlay" :class="{ fullscreen }">
    <div class="loading-container">
      <div class="spinner"></div>
      <p v-if="message" class="loading-message">{{ message }}</p>
    </div>
  </div>
</template>

<script setup>
defineProps({
  isLoading: {
    type: Boolean,
    required: true,
    default: false
  },
  message: {
    type: String,
    default: '加载中...'
  },
  fullscreen: {
    type: Boolean,
    default: false
  }
})
</script>

<style scoped>
.loading-overlay {
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: rgba(0, 0, 0, 0.3);
  border-radius: var(--border-radius-md);
  padding: var(--spacing-xl);
  min-height: 200px;
}

.loading-overlay.fullscreen {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  width: 100%;
  height: 100%;
  z-index: 9999;
  border-radius: 0;
  padding: 0;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-md);
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid rgba(0, 102, 204, 0.2);
  border-top-color: var(--primary-color);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading-message {
  font-size: var(--font-size-base);
  color: var(--text-secondary);
  margin: 0;
}

@media (max-width: 768px) {
  .loading-overlay {
    padding: var(--spacing-lg);
    min-height: 150px;
  }

  .spinner {
    width: 32px;
    height: 32px;
    border-width: 3px;
  }

  .loading-message {
    font-size: var(--font-size-sm);
  }
}
</style>
