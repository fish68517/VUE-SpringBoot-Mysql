<template>
  <div class="skeleton-loader">
    <div v-for="i in count" :key="i" class="skeleton-item" :class="type">
      <div v-if="type === 'card'" class="skeleton-card">
        <div class="skeleton-header"></div>
        <div class="skeleton-body">
          <div class="skeleton-line"></div>
          <div class="skeleton-line short"></div>
          <div class="skeleton-line"></div>
        </div>
        <div class="skeleton-footer">
          <div class="skeleton-line short"></div>
        </div>
      </div>

      <div v-else-if="type === 'list'" class="skeleton-list-item">
        <div class="skeleton-avatar"></div>
        <div class="skeleton-content">
          <div class="skeleton-line"></div>
          <div class="skeleton-line short"></div>
        </div>
      </div>

      <div v-else-if="type === 'text'" class="skeleton-text">
        <div class="skeleton-line"></div>
        <div class="skeleton-line"></div>
        <div class="skeleton-line short"></div>
      </div>

      <div v-else class="skeleton-default">
        <div class="skeleton-line"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  count: {
    type: Number,
    default: 3
  },
  type: {
    type: String,
    enum: ['card', 'list', 'text', 'default'],
    default: 'card'
  }
})
</script>

<style scoped>
.skeleton-loader {
  display: grid;
  gap: 16px;
}

.skeleton-item {
  animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

/* Card skeleton */
.skeleton-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.skeleton-header {
  height: 120px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: shimmer 2s infinite;
}

.skeleton-body {
  padding: 16px;
}

.skeleton-footer {
  padding: 12px 16px;
  border-top: 1px solid #f0f0f0;
}

/* List item skeleton */
.skeleton-list-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.skeleton-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: shimmer 2s infinite;
  flex-shrink: 0;
}

.skeleton-content {
  flex: 1;
}

/* Text skeleton */
.skeleton-text {
  padding: 12px;
  background: #fff;
  border-radius: 8px;
}

/* Default skeleton */
.skeleton-default {
  padding: 12px;
  background: #fff;
  border-radius: 8px;
}

/* Skeleton line */
.skeleton-line {
  height: 12px;
  margin-bottom: 8px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: shimmer 2s infinite;
  border-radius: 4px;
}

.skeleton-line.short {
  width: 60%;
}

.skeleton-line:last-child {
  margin-bottom: 0;
}

@keyframes shimmer {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}

/* Grid layout for cards */
.skeleton-item.card {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
}

@media (max-width: 768px) {
  .skeleton-item.card {
    grid-template-columns: 1fr;
  }
}
</style>
