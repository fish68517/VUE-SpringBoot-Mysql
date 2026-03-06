<template>
  <div class="pagination">
    <button
      class="pagination-btn"
      :disabled="currentPage === 1"
      @click="goToPage(currentPage - 1)"
    >
      上一页
    </button>

    <div class="pagination-info">
      第 {{ currentPage }} / {{ totalPages }} 页
    </div>

    <button
      class="pagination-btn"
      :disabled="currentPage === totalPages"
      @click="goToPage(currentPage + 1)"
    >
      下一页
    </button>

    <div class="pagination-jump">
      <input
        v-model.number="jumpPage"
        type="number"
        :min="1"
        :max="totalPages"
        @keyup.enter="goToPage(jumpPage)"
        placeholder="跳转"
      />
      <button @click="goToPage(jumpPage)">跳转</button>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits, ref, computed } from "vue";

const props = defineProps({
  total: {
    type: Number,
    required: true
  },
  pageSize: {
    type: Number,
    default: 10
  },
  currentPage: {
    type: Number,
    default: 1
  }
});

const emit = defineEmits(["page-change"]);

const jumpPage = ref(props.currentPage);

const totalPages = computed(() => {
  return Math.ceil(props.total / props.pageSize);
});

const goToPage = (page) => {
  if (page < 1 || page > totalPages.value) {
    return;
  }
  jumpPage.value = page;
  emit("page-change", page);
};
</script>

<style scoped>
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 12px;
  margin: 20px 0;
  padding: 20px;
}

.pagination-btn {
  padding: 8px 16px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.pagination-btn:hover:not(:disabled) {
  background: #f5f5f5;
  border-color: #999;
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination-info {
  font-size: 14px;
  color: #666;
  min-width: 100px;
  text-align: center;
}

.pagination-jump {
  display: flex;
  gap: 8px;
}

.pagination-jump input {
  width: 60px;
  padding: 6px 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.pagination-jump button {
  padding: 6px 12px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.pagination-jump button:hover {
  background: #f5f5f5;
  border-color: #999;
}
</style>
