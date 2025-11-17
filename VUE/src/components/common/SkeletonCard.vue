<template>
  <div class="skeleton-card">
    <div class="skeleton skeleton-image"></div>
    <div class="skeleton-content">
      <div class="skeleton skeleton-title"></div>
      <div class="skeleton skeleton-text"></div>
      <div class="skeleton skeleton-text" style="width: 80%;"></div>
      <div class="skeleton-footer">
        <div class="skeleton skeleton-avatar"></div>
        <div class="skeleton skeleton-text" style="width: 100px;"></div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.skeleton-card {
  background: var(--color-bg-base, #fff);
  border-radius: var(--border-radius-md, 8px);
  overflow: hidden;
  box-shadow: var(--shadow-base, 0 1px 3px 0 rgba(0, 0, 0, 0.1));
}

.skeleton-content {
  padding: var(--spacing-md, 16px);
}

.skeleton-footer {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm, 8px);
  margin-top: var(--spacing-md, 16px);
}

.skeleton {
  background: linear-gradient(
    90deg,
    var(--color-bg-light, #f5f7fa) 25%,
    var(--color-border-lighter, #ebeef5) 50%,
    var(--color-bg-light, #f5f7fa) 75%
  );
  background-size: 1000px 100%;
  animation: shimmer 2s infinite;
  border-radius: var(--border-radius-base, 4px);
}

.skeleton-image {
  width: 100%;
  height: 200px;
  border-radius: 0;
}

.skeleton-title {
  height: 24px;
  width: 70%;
  margin-bottom: var(--spacing-md, 12px);
}

.skeleton-text {
  height: 16px;
  margin-bottom: var(--spacing-sm, 8px);
}

.skeleton-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  flex-shrink: 0;
}

@keyframes shimmer {
  0% {
    background-position: -1000px 0;
  }
  100% {
    background-position: 1000px 0;
  }
}
</style>
