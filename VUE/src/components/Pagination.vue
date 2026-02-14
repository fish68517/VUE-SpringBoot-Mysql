<template>
  <div class="pagination" v-if="totalPages > 1">
    <button
      class="pagination-btn"
      :disabled="currentPage === 1"
      @click="goToPage(1)"
      title="首页"
    >
      «
    </button>

    <button
      class="pagination-btn"
      :disabled="currentPage === 1"
      @click="goToPage(currentPage - 1)"
      title="上一页"
    >
      ‹
    </button>

    <div class="page-numbers">
      <button
        v-for="page in visiblePages"
        :key="page"
        class="page-btn"
        :class="{ active: page === currentPage }"
        @click="goToPage(page)"
      >
        {{ page }}
      </button>
    </div>

    <button
      class="pagination-btn"
      :disabled="currentPage === totalPages"
      @click="goToPage(currentPage + 1)"
      title="下一页"
    >
      ›
    </button>

    <button
      class="pagination-btn"
      :disabled="currentPage === totalPages"
      @click="goToPage(totalPages)"
      title="末页"
    >
      »
    </button>

    <div class="pagination-info">
      <span>第 {{ currentPage }} / {{ totalPages }} 页</span>
      <span>共 {{ totalItems }} 条</span>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  currentPage: {
    type: Number,
    required: true,
    default: 1
  },
  totalPages: {
    type: Number,
    required: true,
    default: 1
  },
  totalItems: {
    type: Number,
    required: true,
    default: 0
  },
  maxVisiblePages: {
    type: Number,
    default: 5
  }
})

const emit = defineEmits(['update:currentPage'])

const visiblePages = computed(() => {
  const pages = []
  const maxVisible = props.maxVisiblePages
  const total = props.totalPages
  const current = props.currentPage

  let start = Math.max(1, current - Math.floor(maxVisible / 2))
  let end = Math.min(total, start + maxVisible - 1)

  if (end - start + 1 < maxVisible) {
    start = Math.max(1, end - maxVisible + 1)
  }

  for (let i = start; i <= end; i++) {
    pages.push(i)
  }

  return pages
})

const goToPage = (page) => {
  if (page >= 1 && page <= props.totalPages && page !== props.currentPage) {
    emit('update:currentPage', page)
  }
}
</script>

<style scoped>
.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-lg);
  background-color: var(--bg-primary);
  border-radius: var(--border-radius-md);
  box-shadow: var(--shadow-sm);
  flex-wrap: wrap;
}

.pagination-btn,
.page-btn {
  padding: var(--spacing-sm) var(--spacing-md);
  background-color: var(--bg-secondary);
  color: var(--text-primary);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-md);
  cursor: pointer;
  font-size: var(--font-size-base);
  font-weight: 500;
  transition: all 0.3s ease;
  min-width: 36px;
  text-align: center;
}

.pagination-btn:hover:not(:disabled),
.page-btn:hover:not(.active) {
  background-color: var(--primary-light);
  color: white;
  border-color: var(--primary-light);
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-btn.active {
  background-color: var(--primary-color);
  color: white;
  border-color: var(--primary-color);
  font-weight: 600;
}

.page-numbers {
  display: flex;
  gap: var(--spacing-xs);
}

.pagination-info {
  display: flex;
  gap: var(--spacing-md);
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  white-space: nowrap;
}

@media (max-width: 768px) {
  .pagination {
    gap: var(--spacing-xs);
    padding: var(--spacing-md);
  }

  .pagination-btn,
  .page-btn {
    padding: var(--spacing-xs) var(--spacing-sm);
    font-size: var(--font-size-sm);
    min-width: 32px;
  }

  .pagination-info {
    font-size: 11px;
    gap: var(--spacing-sm);
  }
}
</style>
