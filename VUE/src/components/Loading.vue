<template>
  <div v-if="isLoading" class="loading-overlay">
    <div class="loading-spinner">
      <el-icon class="is-loading">
        <Loading />
      </el-icon>
      <p>{{ message || '加载中...' }}</p>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useGlobalStore } from '../store'
import { Loading } from '@element-plus/icons-vue'

const globalStore = useGlobalStore()

const isLoading = computed(() => globalStore.loading)
const message = computed(() => globalStore.message)
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
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.loading-spinner {
  background-color: white;
  border-radius: 8px;
  padding: 2rem;
  text-align: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.is-loading {
  font-size: 2rem;
  animation: spin 1s linear infinite;
}

p {
  margin-top: 1rem;
  color: #333;
  font-size: 1rem;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}
</style>
