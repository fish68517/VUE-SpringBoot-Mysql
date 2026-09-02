<template>
  <div class="fire-point-detail">
    <div v-if="!rows.length" class="detail-empty">暂无数据</div>
    <template v-else>
      <div
        v-for="(row, rowIndex) in rows"
        :key="getRowKey(row, rowIndex)"
        class="detail-record"
        :class="{
          'detail-record--single': detailItems.length <= singleColumnLimit,
          'detail-record--clickable': Boolean(rowClick),
          'detail-record--active': isActiveRow(row, rowIndex)
        }"
        @click="handleRowClick(row, rowIndex)"
      >
        <div
          v-for="item in detailItems"
          :key="item.key"
          class="detail-item"
        >
          <span class="detail-item__label">{{ item.title }}:</span>
          <span class="detail-item__value">{{ getDisplayValue(row, item, rowIndex) }}</span>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  columns: {
    type: Array,
    default: () => []
  },
  data: {
    type: Array,
    default: () => []
  },
  rowKey: {
    type: [String, Function],
    default: ''
  },
  rowClick: {
    type: Function,
    default: null
  },
  activeRowKey: {
    type: [String, Number],
    default: ''
  },
  singleColumnLimit: {
    type: Number,
    default: 8
  }
})

const rows = computed(() => Array.isArray(props.data) ? props.data : [])

const detailItems = computed(() => {
  return (Array.isArray(props.columns) ? props.columns : []).filter(column => {
    return column && column.key && column.key !== 'index' && column.title
  })
})

const getRowKey = (row, rowIndex) => {
  if (typeof props.rowKey === 'function') return props.rowKey(row, rowIndex)
  if (props.rowKey) return row?.[props.rowKey] ?? rowIndex

  return row?.id || row?.key || row?.deviceCode || row?.deviceNum || row?.deviceNo || row?.dvcode || rowIndex
}

const isActiveRow = (row, rowIndex) => {
  if (props.activeRowKey === '') return false

  return String(getRowKey(row, rowIndex)) === String(props.activeRowKey)
}

const handleRowClick = (row, rowIndex) => {
  if (typeof props.rowClick === 'function') {
    props.rowClick(row, rowIndex)
  }
}

const getPlainVNodeText = value => {
  if (!value || typeof value !== 'object') return value
  const children = value.children

  if (Array.isArray(children)) {
    return children.map(getPlainVNodeText).join('')
  }

  return children ?? ''
}

const getDisplayValue = (row, item, rowIndex) => {
  const rawValue = typeof item.render === 'function'
    ? getPlainVNodeText(item.render(row, rowIndex))
    : row?.[item.key]

  if (rawValue === null || rawValue === undefined || rawValue === '') return '-'

  return String(rawValue)
}
</script>

<script>
export default {
  name: 'FirePointDetailInfo'
}
</script>

<style lang="scss" scoped>
.fire-point-detail {
  width: 100%;
  box-sizing: border-box;
}

.detail-empty {
  height: 100%;
  min-height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #8bbce6;
  background: rgba(10, 50, 95, 0.68);
  border: 1px solid rgba(83, 174, 255, 0.18);
}

.detail-record {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px 16px;
  padding: 14px 16px;
  margin-bottom: 12px;
  box-sizing: border-box;

  &:last-child {
    margin-bottom: 0;
  }

  &--single {
    grid-template-columns: minmax(0, 1fr);
  }

  &--clickable {
    cursor: pointer;
  }

  &--clickable:hover,
  &--active {
    border-color: rgba(83, 174, 255, 0.45);
    background: rgba(37, 134, 255, 0.28);
  }
}

.detail-item {
  min-width: 0;
  display: flex;
  align-items: flex-start;
  gap: 8px;
  color: #d7e9ff;
  font-size: 14px;
  line-height: 20px;
}

.detail-item__label {
  width: 112px;
  flex-shrink: 0;
  color: #8bbce6;
  line-height: 20px;
  text-align: right;
}

.detail-item__value {
  flex: 1;
  min-width: 0;
  color: #e8f8ff;
  font-family: 'D-DIN', 'AlibabaPuHuiTi', sans-serif;
  line-height: 20px;
  white-space: normal;
  text-overflow: clip;
  word-break: break-all;
  overflow-wrap: anywhere;
  overflow: visible;
}

</style>
