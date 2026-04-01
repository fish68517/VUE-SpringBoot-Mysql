<template>
  <div v-if="totalPages > 1" class="pagination">
    <div class="pagination-actions">
      <button
        class="pagination-btn"
        :disabled="currentPage === 1"
        @click="goToPage(1)"
      >
        {{ copy.first }}
      </button>

      <button
        class="pagination-btn"
        :disabled="currentPage === 1"
        @click="goToPage(currentPage - 1)"
      >
        {{ copy.previous }}
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
      >
        {{ copy.next }}
      </button>

      <button
        class="pagination-btn"
        :disabled="currentPage === totalPages"
        @click="goToPage(totalPages)"
      >
        {{ copy.last }}
      </button>
    </div>

    <div class="pagination-info">
      <span>{{ copy.pageLabel }} {{ currentPage }} / {{ totalPages }}</span>
      <span>{{ copy.totalLabel }} {{ totalItems }}</span>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const copy = {
  first: '首页',
  previous: '上一页',
  next: '下一页',
  last: '末页',
  pageLabel: '第',
  totalLabel: '共',
}

const props = defineProps({
  currentPage: {
    type: Number,
    required: true,
    default: 1,
  },
  totalPages: {
    type: Number,
    required: true,
    default: 1,
  },
  totalItems: {
    type: Number,
    required: true,
    default: 0,
  },
  maxVisiblePages: {
    type: Number,
    default: 5,
  },
})

const emit = defineEmits(['update:currentPage', 'change'])

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

  for (let page = start; page <= end; page += 1) {
    pages.push(page)
  }

  return pages
})

const goToPage = (page) => {
  if (page < 1 || page > props.totalPages || page === props.currentPage) {
    return
  }

  emit('update:currentPage', page)
  emit('change', page)
}
</script>

<style scoped>
.pagination {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--spacing-md);
  flex-wrap: wrap;
  padding: 18px 20px;
  border-radius: 18px;
  background: linear-gradient(180deg, #ffffff, #f8fbff);
  border: 1px solid #e6edf5;
  box-shadow: 0 12px 26px rgba(15, 59, 105, 0.06);
}

.pagination-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.pagination-btn,
.page-btn {
  min-width: 42px;
  padding: 10px 14px;
  border: 1px solid #dbe4ef;
  border-radius: 12px;
  background: #ffffff;
  color: var(--text-primary);
  font-size: 14px;
  font-weight: 600;
  transition: transform 0.2s ease, border-color 0.2s ease, box-shadow 0.2s ease, color 0.2s ease;
}

.pagination-btn:hover:not(:disabled),
.page-btn:hover:not(.active) {
  transform: translateY(-1px);
  border-color: rgba(0, 102, 204, 0.35);
  color: var(--primary-color);
  box-shadow: 0 8px 18px rgba(0, 102, 204, 0.08);
}

.pagination-btn:disabled {
  opacity: 0.45;
  cursor: not-allowed;
  box-shadow: none;
}

.page-numbers {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.page-btn.active {
  border-color: transparent;
  background: linear-gradient(135deg, var(--primary-color), var(--primary-light));
  color: #ffffff;
  box-shadow: 0 10px 18px rgba(0, 102, 204, 0.18);
}

.pagination-info {
  display: flex;
  align-items: center;
  gap: 16px;
  color: var(--text-secondary);
  font-size: 14px;
  font-weight: 500;
  flex-wrap: wrap;
}

@media (max-width: 768px) {
  .pagination {
    padding: 16px;
    border-radius: 16px;
  }

  .pagination-actions,
  .pagination-info {
    width: 100%;
  }

  .pagination-info {
    justify-content: space-between;
  }

  .pagination-btn,
  .page-btn {
    padding: 9px 12px;
    border-radius: 10px;
    font-size: 13px;
  }
}
</style>
