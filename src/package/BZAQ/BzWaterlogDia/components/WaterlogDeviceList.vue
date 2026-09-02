<template>
  <div class="modal-container">
    <div class="modal-header">
      <div class="header-title">{{ title }}</div>
      <button class="close-btn" @click="handleClose"></button>
    </div>
    <div class="modal-body">
      <div class="table-container">
        <n-data-table
          class="device-table"
          :columns="columns"
          :data="tableData"
          :bordered="false"
          :single-line="false"
          size="small"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { h, ref } from 'vue'
import { NDataTable } from 'naive-ui'

const emit = defineEmits(['close'])

const title = ref('设备详情')
const tableData = ref([
  { location: '渝中区石油路街道煤建新村', type: '摄像头', status: '在线' },
  { location: '渝中区石油路街道煤建新村普天小院', type: '液位计', status: '在线' },
  { location: '渝中区解放碑街道华庭锦园', type: '鹰眼', status: '离线' }
])

const columns = [
  {
    title: '序号',
    key: 'index',
    width: 80,
    align: 'center',
    render(row, rowIndex) {
      return h('span', {}, rowIndex + 1)
    }
  },
  {
    title: '设备位置',
    key: 'location',
    align: 'center',
    ellipsis: true
  },
  {
    title: '类型',
    key: 'type',
    width: 140,
    align: 'center'
  },
  {
    title: '状态',
    key: 'status',
    width: 100,
    align: 'center',
    render(row) {
      return h(
        'span',
        { class: 'status-cell' },
        [
          h('span', {
            class: ['status-dot', row.status === '在线' ? 'online' : 'offline']
          }),
          row.status
        ]
      )
    }
  }
]

function handleClose() {
  emit('close')
}
</script>

<style lang="scss" scoped>
@font-face {
  font-family: 'AlibabaPuHuiTi';
  src: url('../font/Alibaba_PuHuiTi_2.0_55_Regular_55_Regular.ttf') format('truetype');
  font-weight: normal;
  font-style: normal;
}

@font-face {
  font-family: 'D-DIN';
  src: url('../font/D-DIN.otf') format('opentype');
  font-weight: normal;
  font-style: normal;
}

.modal-container {
  font-family: 'AlibabaPuHuiTi', sans-serif;
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  z-index: 1001;
  width: 904px;
  height: 604px;
  display: flex;
  flex-direction: column;
  background:
    url('../img/Group-2136640490.png') top center / 100% 48px no-repeat,
    url('../img/Rectangle-346242153.png') center / 100% 100% no-repeat;
  color: #b8d9ff;
  font-size: 14px;
  pointer-events: auto;
}

.modal-header {
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  flex-shrink: 0;

  .header-title {
    color: #ffffff;
    font-size: 18px;
    font-weight: 700;
  }

  .close-btn {
    position: absolute;
    right: 16px;
    top: 50%;
    transform: translateY(-50%);
    width: 14px;
    height: 14px;
    background: url('../img/close.png') no-repeat center / cover;
    border: none;
    cursor: pointer;
  }
}

.modal-body {
  flex: 1;
  padding: 20px 30px 30px;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.table-container {
  flex: 1;
  overflow: hidden;

  :deep(.device-table) {
    height: 100%;
    font-size: 14px;

    .n-data-table-wrapper {
      background: transparent;
    }

    .n-data-table-table {
      background: transparent;
      font-size: 14px;
    }

    .n-data-table-th,
    .n-data-table-td {
      padding: 12px 16px;
      white-space: nowrap;
      border-right: none;
    }

    .n-data-table-th {
      color: #749dc0;
      font-weight: 500;
      background: rgba(24, 88, 163, 0.2);
      border-bottom: 1px solid rgba(166, 206, 255, 0.15);
      border-top: 1px solid rgba(166, 206, 255, 0.15);
    }

    .n-data-table-td {
      color: #d8ecff;
      border-bottom: 1px solid rgba(166, 206, 255, 0.05);
      background: transparent;
    }

    .n-data-table-tr:nth-child(even) .n-data-table-td {
      background: rgba(255, 255, 255, 0.02);
    }

    .n-data-table-tr:hover .n-data-table-td {
      background: rgba(255, 255, 255, 0.05);
    }
  }
}

.status-cell {
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.status-dot {
  display: inline-block;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  margin-right: 6px;

  &.online {
    background: #1cf582;
  }

  &.offline {
    background: #888888;
  }
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 16px;
  font-size: 14px;
  color: #749dc0;

  .total {
    margin-right: 8px;
  }

  .page-btn {
    width: 28px;
    height: 28px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: transparent;
    border: 1px solid rgba(166, 206, 255, 0.3);
    color: #749dc0;
    border-radius: 3px;
    cursor: pointer;
    font-family: 'AlibabaPuHuiTi', sans-serif;
    padding: 0;

    &.active {
      background: #1676ea;
      color: #ffffff;
      border-color: #1676ea;
    }

    &:hover:not(.active) {
      border-color: #1676ea;
      color: #1676ea;
    }
  }
}
</style>
