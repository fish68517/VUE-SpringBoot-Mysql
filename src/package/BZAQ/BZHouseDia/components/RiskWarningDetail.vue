<template>
  <div class="risk-warning-detail-popup">
    <!-- 顶部标题栏 -->
    <div class="popup-header">
      <h3 class="title">风险预警详情</h3>
      <img class="close-btn" :src="closeIcon" alt="关闭" @click="$emit('close')" />
    </div>

    <div class="risk-warning-detail">
      <!-- 预警信息 -->
      <div class="detail-section">
        <div class="detail-section__title">预警信息</div>
        <div class="detail-grid">
          <div class="detail-field">
            <span class="detail-label">预警编号</span>
            <span class="detail-value">{{ detail.warningCode }}</span>
          </div>
          <div class="detail-field">
            <span class="detail-label">发生时间</span>
            <span class="detail-value">{{ detail.eventTime }}</span>
          </div>
          <div class="detail-field">
            <span class="detail-label">风险来源</span>
            <span class="detail-value">{{ detail.riskSource }}</span>
          </div>
          <div class="detail-field">
            <span class="detail-label">风险等级</span>
            <span class="detail-value">{{ detail.riskLevel }}</span>
          </div>
          <div class="detail-field detail-field--full">
            <span class="detail-label">预警内容</span>
            <span class="detail-value detail-value--content">{{ detail.warningContent }}</span>
          </div>
          <div class="detail-field">
            <span class="detail-label">事件编号</span>
            <span class="detail-value">{{ detail.eventCode }}</span>
          </div>
          <div class="detail-field">
            <span class="detail-label">处理部门</span>
            <span class="detail-value">{{ detail.handleDept }}</span>
          </div>
          <div class="detail-field">
            <span class="detail-label">处理时间</span>
            <span class="detail-value">{{ detail.handleTime }}</span>
          </div>
          <div class="detail-field">
            <span class="detail-label">处置状态</span>
            <span class="detail-value">{{ detail.handleStatus }}</span>
          </div>
          <div class="detail-field">
            <span class="detail-label">企业名称</span>
            <span class="detail-value">{{ detail.companyName }}</span>
          </div>
          <div class="detail-field detail-field--full">
            <span class="detail-label">企业地址</span>
            <span class="detail-value">{{ detail.companyAddress }}</span>
          </div>
        </div>
      </div>

      <!-- 历史记录 -->
      <div class="detail-section">
        <div class="detail-section__title">历史记录</div>
        <div class="history-filter">
          <label>时间：</label>
          <n-config-provider :locale="zhCN" :date-locale="dateZhCN" class="date-config-wrapper">
            <n-date-picker
              v-model:value="historyTime"
              class="history-date-picker"
              type="daterange"
              clearable
              :separator="'至'"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              format="yyyy-MM-dd"
            />
          </n-config-provider>
        </div>
        <div class="history-table-wrapper">
          <n-data-table
            class="history-table"
            :columns="historyColumns"
            :data="historyData"
            :bordered="false"
            :single-line="false"
            size="small"
          />
        </div>
        <div class="history-pagination">
          <n-pagination
            :page="historyPagination.current"
            :page-count="historyPagination.totalPage"
            :page-slot="5"
            @update:page="handleHistoryPageChange"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { h, ref, watch } from 'vue'
import { NDataTable, NPagination, NDatePicker, NConfigProvider, zhCN, dateZhCN } from 'naive-ui'
import closeIcon from '../img/close.png'

const props = defineProps({
  detailData: {
    type: Object,
    default: () => ({})
  }
})

defineEmits(['close'])

const detail = ref({
  warningCode: '',
  eventTime: '',
  riskSource: '',
  riskLevel: '',
  warningContent: '',
  eventCode: '',
  handleDept: '',
  handleTime: '',
  handleStatus: '',
  companyName: '',
  companyAddress: ''
})

watch(() => props.detailData, (val) => {
  if (val) {
    detail.value = { ...detail.value, ...val }
  }
}, { immediate: true })

// 历史记录时间选择
const historyTime = ref(null)

// 历史记录分页
const historyPagination = ref({
  current: 1,
  totalPage: 3
})

function handleHistoryPageChange(page) {
  historyPagination.value.current = page
}

// 历史记录表格列
const historyColumns = [
  { title: '预警编号', key: 'warningCode', align: 'center', width: 140, ellipsis: { tooltip: true } },
  { title: '预警内容', key: 'warningContent', align: 'center', ellipsis: { tooltip: true } },
  { title: '风险等级', key: 'riskLevel', align: 'center', width: 100, ellipsis: { tooltip: true } },
  { title: '发生时间', key: 'eventTime', align: 'center', width: 180, ellipsis: { tooltip: true } },
  { title: '处理时间', key: 'handleTime', align: 'center', width: 180, ellipsis: { tooltip: true } }
]

// 历史记录模拟数据
const historyData = ref([
  { warningCode: 'YJ20260728001', warningContent: '电动车入户充电隐患', riskLevel: '高', eventTime: '2026-07-28 09:30:00', handleTime: '2026-07-28 10:15:00' },
  { warningCode: 'YJ20260727002', warningContent: '消防通道堵塞', riskLevel: '中', eventTime: '2026-07-27 14:20:00', handleTime: '2026-07-27 15:00:00' },
  { warningCode: 'YJ20260726003', warningContent: '私拉电线违规用电', riskLevel: '高', eventTime: '2026-07-26 11:00:00', handleTime: '2026-07-26 11:45:00' },
  { warningCode: 'YJ20260725004', warningContent: '垃圾堆放隐患', riskLevel: '低', eventTime: '2026-07-25 16:45:00', handleTime: '2026-07-25 17:30:00' },
  { warningCode: 'YJ20260724005', warningContent: '灭火器过期未更换', riskLevel: '中', eventTime: '2026-07-24 08:50:00', handleTime: '2026-07-24 09:30:00' }
])
</script>

<style lang="scss" scoped>
.risk-warning-detail-popup {
  width: 1284px;
  height: 756px;
  background: url('../img/diaBg.png') no-repeat center / 100% 100%;
  transform: translate(-50%, -50%);
  position: absolute;
  left: 50%;
  top: 50%;
  z-index: 1002;
  padding: 10px 25px;
  color: #fff;
  font-family: 'Microsoft YaHei';

  // 顶部标题栏
  .popup-header {
    height: 50px;
    line-height: 50px;
    padding: 0 25px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    transform: translateY(10px);

    .title {
      font-size: 26px;
      font-weight: bold;
      margin: 0;
      color: #fff;
    }

    .close-btn {
      width: 40px;
      height: 40px;
      cursor: pointer;
    }
  }
}

.risk-warning-detail {
  padding: 15px 25px;
  margin-top: 10px;
  height: calc(100% - 70px);
  overflow-y: auto;
  scrollbar-width: none;
  -ms-overflow-style: none;

  &::-webkit-scrollbar {
    display: none;
  }

  .detail-section {
    margin-bottom: 20px;

    &:last-child {
      margin-bottom: 0;
    }

    &__title {
      font-size: 14px;
      font-weight: 600;
      color: #04bcfa;
      margin-bottom: 12px;
      padding-left: 8px;
      border-left: 3px solid #04bcfa;
    }
  }

  .detail-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 12px 20px;
  }

  .detail-field {
    display: flex;
    align-items: flex-start;
    gap: 8px;

    &--full {
      grid-column: 1 / -1;
    }

    .detail-label {
      font-size: 13px;
      color: rgba(255, 255, 255, 0.5);
      white-space: nowrap;
      flex-shrink: 0;
      min-width: 80px;
    }

    .detail-value {
      font-size: 13px;
      color: #fff;
      word-break: break-all;
      flex: 1;

      &--content {
        line-height: 1.6;
        min-height: 40px;
        padding: 8px 12px;
        background: rgba(255, 255, 255, 0.05);
        border-radius: 4px;
        border: 1px solid rgba(255, 255, 255, 0.08);
      }
    }
  }

  .history-filter {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 12px;

    label {
      white-space: nowrap;
      color: #ffffff;
      font-size: 14px;
      font-weight: 400;
      line-height: 22px;
    }

    .date-config-wrapper {
      display: contents;
    }

    .history-date-picker {
      width: 280px;
    }
  }

  .history-table-wrapper {
    margin-bottom: 12px;

    :deep(.n-data-table) {
      background: transparent;
      color: #fff;

      .n-data-table-th {
        background: rgba(4, 188, 250, 0.15);
        color: #fff;
        font-size: 13px;
      }

      .n-data-table-td {
        background: transparent;
        color: rgba(255, 255, 255, 0.85);
        font-size: 13px;
      }

      .n-data-table-tr:hover .n-data-table-td {
        background: rgba(255, 255, 255, 0.05);
      }
    }
  }

  .history-pagination {
    display: flex;
    justify-content: center;
    padding: 8px 0;
  }
}

// 时间选择器样式
.history-date-picker {
  :deep(.n-input) {
    background: transparent !important;
    border: 1px solid #ffffff3d !important;
    border-radius: 4px !important;
    height: 32px;
    box-shadow: none !important;
  }

  :deep(.n-input:hover) {
    border-color: #ffffff5e !important;
  }

  :deep(.n-input--focus) {
    border-color: #ffffff5e !important;
  }

  :deep(.n-input__input-el),
  :deep(.n-input__textarea-el) {
    color: #f5fcff73 !important;
    font-size: 14px;
    font-family: "Alibaba PuHuiTi 2.0", "Microsoft YaHei", sans-serif;
    background: transparent !important;
  }

  :deep(.n-input__placeholder) {
    color: #f5fcff73 !important;
    font-size: 14px;
  }

  :deep(.n-input__separator) {
    color: #f5fcff73 !important;
    font-size: 14px;
  }

  :deep(.n-base-suffix) {
    color: #f5fcff73 !important;
  }
}
</style>
