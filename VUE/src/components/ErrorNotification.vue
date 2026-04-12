<template>
  <div class="error-notification-container">
    <transition-group name="notification" tag="div">
      <div
        v-for="notification in notifications"
        :key="notification.id"
        :class="['notification', `notification-${notification.type}`]"
      >
        <div class="notification-content">
          <span :class="['notification-icon', `icon-${notification.type}`]">
            <i :class="getIconClass(notification.type)"></i>
          </span>
          <span class="notification-message">{{ notification.message }}</span>
          <button
            class="notification-close"
            @click="removeNotification(notification.id)"
            aria-label="Close notification"
          >
            ×
          </button>
        </div>
        <div
          v-if="notification.duration > 0"
          class="notification-progress"
          :style="{ animationDuration: `${notification.duration}ms` }"
        ></div>
      </div>
    </transition-group>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useErrorStore } from '../stores/errorStore'

const errorStore = useErrorStore()

const notifications = computed(() => errorStore.notifications)

const removeNotification = (id) => {
  errorStore.removeNotification(id)
}

const getIconClass = (type) => {
  const iconMap = {
    success: 'el-icon-success',
    error: 'el-icon-circle-close',
    warning: 'el-icon-warning',
    info: 'el-icon-info'
  }
  return iconMap[type] || 'el-icon-info'
}
</script>

<style scoped>
.error-notification-container {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 9999;
  max-width: 400px;
  pointer-events: none;
}

.notification {
  margin-bottom: 12px;
  border-radius: 4px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
  overflow: hidden;
  pointer-events: auto;
  animation: slideIn 0.3s ease-out;
}

.notification-content {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  gap: 12px;
  min-height: 44px;
}

.notification-icon {
  flex-shrink: 0;
  font-size: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.notification-message {
  flex: 1;
  font-size: 14px;
  line-height: 1.5;
  word-break: break-word;
}

.notification-close {
  flex-shrink: 0;
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  padding: 0;
  margin: 0;
  opacity: 0.7;
  transition: opacity 0.2s;
}

.notification-close:hover {
  opacity: 1;
}

.notification-progress {
  height: 3px;
  animation: progress linear forwards;
}

/* Notification type styles */
.notification-success {
  background-color: #f0f9ff;
  border-left: 4px solid #67c23a;
}

.notification-success .notification-icon {
  color: #67c23a;
}

.notification-success .notification-close {
  color: #67c23a;
}

.notification-success .notification-progress {
  background-color: #67c23a;
}

.notification-error {
  background-color: #fef0f0;
  border-left: 4px solid #f56c6c;
}

.notification-error .notification-icon {
  color: #f56c6c;
}

.notification-error .notification-close {
  color: #f56c6c;
}

.notification-error .notification-progress {
  background-color: #f56c6c;
}

.notification-warning {
  background-color: #fdf6ec;
  border-left: 4px solid #e6a23c;
}

.notification-warning .notification-icon {
  color: #e6a23c;
}

.notification-warning .notification-close {
  color: #e6a23c;
}

.notification-warning .notification-progress {
  background-color: #e6a23c;
}

.notification-info {
  background-color: #f4f4f5;
  border-left: 4px solid #909399;
}

.notification-info .notification-icon {
  color: #909399;
}

.notification-info .notification-close {
  color: #909399;
}

.notification-info .notification-progress {
  background-color: #909399;
}

/* Animations */
@keyframes slideIn {
  from {
    transform: translateX(400px);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

@keyframes slideOut {
  from {
    transform: translateX(0);
    opacity: 1;
  }
  to {
    transform: translateX(400px);
    opacity: 0;
  }
}

@keyframes progress {
  from {
    width: 100%;
  }
  to {
    width: 0;
  }
}

.notification-enter-active {
  animation: slideIn 0.3s ease-out;
}

.notification-leave-active {
  animation: slideOut 0.3s ease-in;
}

/* Responsive design */
@media (max-width: 768px) {
  .error-notification-container {
    left: 12px;
    right: 12px;
    max-width: none;
    top: 12px;
  }

  .notification-content {
    padding: 10px 12px;
    min-height: 40px;
  }

  .notification-message {
    font-size: 13px;
  }
}
</style>
