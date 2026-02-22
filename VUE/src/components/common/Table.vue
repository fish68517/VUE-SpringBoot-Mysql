<template>
  <div class="table-container">
    <el-table
      :data="data"
      stripe
      style="width: 100%"
      :default-sort="defaultSort"
      @sort-change="handleSortChange"
    >
      <el-table-column
        v-for="column in columns"
        :key="column.prop"
        :prop="column.prop"
        :label="column.label"
        :width="column.width"
        :sortable="column.sortable"
      />
      <el-table-column
        v-if="showActions"
        label="操作"
        width="200"
        fixed="right"
      >
        <template #default="{ row }">
          <el-button
            v-if="showEdit"
            type="primary"
            size="small"
            @click="$emit('edit', row)"
          >
            编辑
          </el-button>
          <el-button
            v-if="showDelete"
            type="danger"
            size="small"
            @click="$emit('delete', row)"
          >
            删除
          </el-button>
          <slot name="actions" :row="row"></slot>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      v-if="showPagination"
      :current-page="currentPage"
      :page-size="pageSize"
      :page-sizes="[10, 20, 50, 100]"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
      @current-page-change="$emit('page-change', $event)"
      @page-size-change="$emit('page-size-change', $event)"
    />
  </div>
</template>

<script setup>
defineProps({
  data: {
    type: Array,
    required: true,
  },
  columns: {
    type: Array,
    required: true,
  },
  showActions: {
    type: Boolean,
    default: true,
  },
  showEdit: {
    type: Boolean,
    default: true,
  },
  showDelete: {
    type: Boolean,
    default: true,
  },
  showPagination: {
    type: Boolean,
    default: true,
  },
  currentPage: {
    type: Number,
    default: 1,
  },
  pageSize: {
    type: Number,
    default: 10,
  },
  total: {
    type: Number,
    default: 0,
  },
  defaultSort: {
    type: Object,
    default: () => ({ prop: 'id', order: 'descending' }),
  },
})

defineEmits(['edit', 'delete', 'page-change', 'page-size-change', 'sort-change'])

const handleSortChange = (column) => {
  emit('sort-change', column)
}
</script>

<style scoped>
.table-container {
  width: 100%;
}
</style>
