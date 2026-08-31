<script setup lang="ts">
defineProps<{ pageNum: number; pageSize: number; total: number }>()
const emit = defineEmits<{ change: [pageNum: number, pageSize: number] }>()
</script>

<template>
  <div class="pagination-wrap">
    <span>共 {{ total }} 条</span>
    <el-pagination
      background
      layout="prev, pager, next, sizes, jumper"
      :current-page="pageNum"
      :page-size="pageSize"
      :page-sizes="[10, 20, 50]"
      :total="total"
      @update:current-page="(page: number) => emit('change', page, pageSize)"
      @update:page-size="(size: number) => emit('change', 1, size)"
    />
  </div>
</template>

<style scoped lang="scss">
.pagination-wrap {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 20px;
  padding-top: 17px;
  color: #7e8794;
  font-size: 12px;
}
</style>
