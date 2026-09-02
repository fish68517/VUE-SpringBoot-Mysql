<template>
  <div class="pipeline-list-modal">
    <div class="modal-header">
      <div class="header-title">{{ title }}</div>
      <button class="close-btn" type="button" @click="emit('close')">×</button>
    </div>

    <div v-if="tabs.length" class="modal-tabs">
      <button
        v-for="tab in tabs"
        :key="tab.value"
        class="modal-tab"
        :class="{ 'modal-tab--active': tab.value === activeTab }"
        type="button"
        @click="emit('update:activeTab', tab.value)"
      >
        {{ tab.label }}<span v-if="tab.count !== undefined">（{{ tab.count }}）</span>
      </button>
    </div>

    <div v-if="showFilters" class="filter-bar">
      <div class="filter-item">
        <span class="filter-label">所属街道</span>
        <n-select
          v-model:value="selectedStreet"
          class="filter-select"
          :options="streetOptions"
          :theme-overrides="selectThemeOverrides"
          size="small"
        />
      </div>
      <div class="filter-item">
        <span class="filter-label">处置状态</span>
        <n-select
          v-model:value="selectedStatus"
          class="filter-select"
          :options="statusOptions"
          :theme-overrides="selectThemeOverrides"
          size="small"
        />
      </div>
      <div class="filter-actions">
        <n-button size="small" type="info" @click="handleSearch">查询</n-button>
        <n-button size="small" @click="handleReset">重置</n-button>
      </div>
      <div class="filter-total">共 {{ filteredData.length }} 条</div>
    </div>

    <div class="modal-content">
      <n-data-table
        class="pipeline-table"
        :columns="displayColumns"
        :data="tableData"
        :bordered="false"
        :single-line="false"
        size="small"
        :scroll-x="scrollX"
        :max-height="tableMaxHeight"
      />
    </div>

    <div class="modal-footer">
      <n-pagination
        :page="currentPage"
        :page-count="totalPage"
        :page-slot="5"
        @update:page="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, h, ref, watch } from 'vue'
import { NButton, NDataTable, NPagination, NSelect } from 'naive-ui'

const emit = defineEmits(['close', 'update:activeTab', 'open-detail'])
const props = defineProps({
  title: {
    type: String,
    required: true
  },
  columns: {
    type: Array,
    required: true
  },
  rows: {
    type: Array,
    required: true
  },
  scrollX: {
    type: Number,
    default: 1160
  },
  showFilters: {
    type: Boolean,
    default: true
  },
  tabs: {
    type: Array,
    default: () => []
  },
  activeTab: {
    type: String,
    default: ''
  },
  payload: {
    type: Object,
    default: () => ({})
  },
  detailKeys: {
    type: Array,
    default: () => ['deviceId', 'deviceCode']
  }
})

const pageSize = 10
const tableMaxHeight = 610
const currentPage = ref(1)
const selectedStreet = ref('')
const selectedStatus = ref('')

const streetOptions = [
  { label: '全部', value: '' },
  { label: '解放碑街道', value: '解放碑街道' },
  { label: '朝天门街道', value: '朝天门街道' },
  { label: '化龙桥街道', value: '化龙桥街道' },
  { label: '大坪街道', value: '大坪街道' },
  { label: '两路口街道', value: '两路口街道' },
  { label: '南纪门街道', value: '南纪门街道' },
  { label: '石油路街道', value: '石油路街道' }
]

const statusOptions = [
  { label: '全部', value: '' },
  { label: '处置中', value: '处置中' },
  { label: '已完成', value: '已完成' },
  { label: '未处置', value: '未处置' }
]

const controlTheme = {
  heightSmall: '28px',
  fontSizeSmall: '12px',
  borderRadius: '2px',
  color: 'rgba(16, 64, 126, 0.42)',
  colorActive: 'rgba(16, 64, 126, 0.42)',
  colorFocus: 'rgba(16, 64, 126, 0.42)',
  textColor: '#d7e9ff',
  placeholderColor: 'rgba(184, 217, 255, 0.55)',
  border: '1px solid rgba(83, 174, 255, 0.32)',
  borderHover: '1px solid rgba(83, 174, 255, 0.32)',
  borderActive: '1px solid rgba(83, 174, 255, 0.32)',
  borderFocus: '1px solid rgba(83, 174, 255, 0.32)',
  boxShadowActive: 'none',
  boxShadowFocus: 'none'
}

const selectThemeOverrides = {
  peers: {
    InternalSelection: {
      ...controlTheme,
      arrowColor: 'rgba(184, 217, 255, 0.55)'
    }
  }
}

const filteredData = computed(() => {
  return props.rows.filter((row: any) => {
    const matchStreet = !selectedStreet.value || row.street === selectedStreet.value
    const matchStatus = !selectedStatus.value || row.status === selectedStatus.value
    return matchStreet && matchStatus
  })
})

const totalPage = computed(() => Math.max(1, Math.ceil(filteredData.value.length / pageSize)))

const displayColumns = computed(() => {
  const detailKeys = props.detailKeys as string[]
  const columns = (props.columns as any[]).map(column => {
    if (!detailKeys.includes(column.key)) return column

    return {
      ...column,
      render(row: any) {
        return h(
          'button',
          {
            class: 'link-cell detail-link',
            type: 'button',
            onClick(event: MouseEvent) {
              event.stopPropagation()
              emit('open-detail', row)
            }
          },
          row[column.key] || '--'
        )
      }
    }
  })

  return [
    {
      title: '序号',
      key: '__index',
      width: 64,
      align: 'center',
      render(_row: any, rowIndex: number) {
        return h('span', {}, (currentPage.value - 1) * pageSize + rowIndex + 1)
      }
    },
    ...columns
  ]
})

const tableData = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredData.value.slice(start, start + pageSize)
})

function handleSearch() {
  currentPage.value = 1
}

function handleReset() {
  selectedStreet.value = ''
  selectedStatus.value = ''
  currentPage.value = 1
}

function handlePageChange(page: number) {
  currentPage.value = page
}

watch(() => props.rows, handleReset)
</script>

<script lang="ts">
export default {
  name: 'PipelineListModal'
}
</script>

<style lang="scss" scoped>
.pipeline-list-modal {
  position: absolute;
  transform: translate(-50%, -50%);
  z-index: 1001;
  width: 1356px;
  height: 830px;
  display: flex;
  flex-direction: column;
  background: linear-gradient(180deg, rgba(6, 36, 76, 0.98) 0%, rgba(2, 12, 36, 0.98) 100%);
  border: 1px solid rgba(37, 134, 255, 0.25);
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.45);
  color: #b8d9ff;
  font-size: 14px;
  overflow: hidden;
  pointer-events: auto;

  &::before {
    content: '';
    position: absolute;
    inset: 0;
    background:
      linear-gradient(90deg, rgba(83, 174, 255, 0.08), transparent 26%, transparent 74%, rgba(83, 174, 255, 0.08)),
      radial-gradient(circle at 50% 0%, rgba(83, 174, 255, 0.18), transparent 36%);
    z-index: -1;
  }
}

.modal-header {
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  flex-shrink: 0;
  padding: 0 32px;
  background: linear-gradient(90deg, rgba(12, 73, 138, 0.55), rgba(4, 34, 76, 0.2), rgba(12, 73, 138, 0.55));
  border-bottom: 1px solid rgba(83, 174, 255, 0.22);
}

.header-title {
  color: #cfe8ff;
  font-size: 22px;
  font-weight: 700;
  text-shadow: 0 0 12px rgba(83, 174, 255, 0.5);
}

.close-btn {
  position: absolute;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
  width: 32px;
  height: 32px;
  border: 1px solid rgba(83, 174, 255, 0.3);
  background: rgba(16, 64, 126, 0.4);
  color: #aed5ff;
  font-size: 20px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;

  &:hover {
    color: #fff;
    border-color: rgba(83, 174, 255, 0.6);
    background: rgba(24, 92, 179, 0.6);
  }
}

.filter-bar {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 32px 0;
  box-sizing: border-box;
}

.modal-tabs {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 18px 48px 0;
}

.modal-tab {
  min-width: 176px;
  height: 44px;
  padding: 0 18px;
  border: 1px solid rgba(139, 188, 230, 0.75);
  border-radius: 4px;
  background: rgba(255, 255, 255, 0.04);
  color: #d7e9ff;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.18s ease;
}

.modal-tab--active,
.modal-tab:hover {
  border-color: #19a4df;
  background: #19a4df;
  color: #fff;
}

.filter-item {
  display: flex;
  align-items: center;
  gap: 6px;
  min-width: 0;
}

.filter-label {
  flex-shrink: 0;
  color: #8bbce6;
  font-size: 13px;
  white-space: nowrap;
}

.filter-select {
  width: 150px;
}

.filter-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-total {
  margin-left: auto;
  color: #8bbce6;
  font-size: 13px;
  white-space: nowrap;
}

.filter-select :deep(.n-base-selection) {
  min-height: 28px !important;
  height: 28px !important;
  background: rgba(16, 64, 126, 0.42) !important;
  border: 1px solid rgba(83, 174, 255, 0.32) !important;
  border-radius: 2px !important;
  box-sizing: border-box;
}

.filter-select :deep(.n-base-selection-label),
.filter-select :deep(.n-base-selection-input),
.filter-select :deep(.n-base-selection-placeholder) {
  height: 26px !important;
  line-height: 26px !important;
  color: #d7e9ff !important;
  font-size: 12px !important;
}

.filter-select :deep(.n-base-selection-placeholder) {
  color: rgba(184, 217, 255, 0.55) !important;
}

.filter-select :deep(.n-base-selection__border),
.filter-select :deep(.n-base-selection__state-border) {
  border: none !important;
  box-shadow: none !important;
}

.filter-actions :deep(.n-button) {
  min-width: 56px;
  border-radius: 2px;
}

.modal-content {
  flex: 1;
  min-height: 0;
  padding: 12px 32px 0;
  overflow: hidden;
}

:deep(.pipeline-table) {
  height: 100%;
  min-height: 0;

  .n-data-table-wrapper,
  .n-data-table-table {
    background: transparent;
  }

  .n-data-table-base-table,
  .n-data-table-base-table-body {
    min-height: 0;
  }

  .n-scrollbar-rail {
    background: rgba(8, 53, 108, 0.28);
  }

  .n-scrollbar-rail__scrollbar {
    background: linear-gradient(180deg, rgba(95, 188, 255, 0.9), rgba(25, 118, 214, 0.85)) !important;
    border-radius: 999px;
  }

  .n-data-table-table {
    font-size: 14px;
  }

  .n-data-table-th {
    height: 40px;
    padding: 0 4px;
    color: #8bbce6;
    font-weight: 500;
    background: #0b437c;
    border: none;
  }

  .n-data-table-td {
    min-height: 40px;
    padding: 6px 4px;
    color: #d7e9ff;
    background: #0a325f;
    border: none;
    line-height: 1.35;
    white-space: normal;
    word-break: break-all;
  }

  .n-data-table-tr:nth-child(even) .n-data-table-td {
    background: #19406b;
  }

  .n-data-table-tr:hover .n-data-table-td {
    background: rgba(29, 107, 202, 0.3);
  }

  .link-cell {
    color: #d7e9ff;
    text-decoration: underline;
    text-underline-offset: 2px;
  }

  .detail-link {
    border: none;
    padding: 0;
    background: transparent;
    cursor: pointer;
    font: inherit;

    &:hover {
      color: #5ff6ff;
    }
  }
}

.modal-footer {
  height: 60px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 32px;

  :deep(.n-pagination) {
    display: flex;
    align-items: center;
    gap: 6px;
  }

  :deep(.n-pagination-item) {
    min-width: 32px;
    height: 32px;
    border: 1px solid rgba(83, 174, 255, 0.35);
    background: rgba(16, 64, 126, 0.4);
    color: #aed5ff;
    font-size: 14px;
    border-radius: 2px;
  }

  :deep(.n-pagination-item--active) {
    border-color: rgba(83, 174, 255, 0.85);
    background: rgba(24, 92, 179, 0.8);
    color: #fff;
  }
}
</style>
