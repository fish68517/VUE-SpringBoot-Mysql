<template>
  <div class="risk-warning-popup">
    <!-- 顶部标题栏 -->
    <div class="popup-header">
      <h3 class="title">风险预警</h3>
      <img class="close-btn" :src="closeIcon" alt="关闭" @click="$emit('close')" />
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar">
      <div class="search-item">
        <label>预警内容：</label>
        <el-input v-model="searchForm.warningContent" class="search-input" placeholder="请输入" />
      </div>
      <div class="search-item">
        <label>企业名称：</label>
        <el-input v-model="searchForm.companyName" class="search-input" placeholder="请输入" />
      </div>
      <div class="search-item">
        <label>风险等级：</label>
        <el-select v-model="searchForm.riskLevel" class="search-select" popper-class="risk-warning-select-popper" placeholder="请选择">
          <el-option
            v-for="item in riskLevelOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </div>
      <div class="search-item">
        <label>巡查时间：</label>
        <n-config-provider :locale="zhCN" :date-locale="dateZhCN" class="date-config-wrapper">
          <n-date-picker
            v-model:value="searchForm.inspectionTime"
            class="search-date-picker"
            type="daterange"
            clearable
            :separator="'至'"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="yyyy-MM-dd"
            :is-date-disabled="() => false"
          />
        </n-config-provider>
      </div>
      <div class="search-buttons">
        <button class="search-btn" @click="handleSearch">查询</button>
        <button class="reset-btn" @click="handleReset">重置</button>
      </div>
    </div>

    <!-- 表格 -->
    <div class="table-container">
      <n-data-table
        class="risk-warning-table"
        :columns="columns"
        :data="tableData"
        :bordered="false"
        :single-line="false"
        size="small"
      />
    </div>

    <!-- 分页 -->
    <div class="pagination-wrapper">
      <n-pagination
        :page="pagination.current"
        :page-count="pagination.totalPage"
        :page-slot="5"
        @update:page="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { h, ref, onMounted } from 'vue'
import { NDataTable, NPagination, NDatePicker, NConfigProvider, zhCN, dateZhCN } from 'naive-ui'
import { ElInput, ElSelect, ElOption } from 'element-plus'
import 'element-plus/dist/index.css'
import axios from 'axios'
import closeIcon from '../img/close.png'

const props = defineProps({
  bus: {
    type: Object,
    default: null
  }
})

const emits = defineEmits(['close', 'show-risk-warning-detail'])

// 风险等级下拉选项（接口传数字：1-低 2-中 3-高）
const riskLevelOptions = [
  { label: '高', value: '3' },
  { label: '中', value: '2' },
  { label: '低', value: '1' }
]

// 风险等级数字转中文
const riskLevelTextMap = { '1': '低', '2': '中', '3': '高' }

// 搜索表单
const searchForm = ref({
  warningContent: '',
  companyName: '',
  riskLevel: '',
  inspectionTime: null
})

// 分页
const pagination = ref({
  current: 1,
  size: 10,
  total: 0,
  totalPage: 1
})

// 表格数据
const tableData = ref([])
const loading = ref(false)

// 接口地址
const API_URL = 'http://23.210.227.34:23343/ywtg/api/boot/system/resiComm/bizToResiHouseRiskAlertPage'

// 查询列表
async function fetchList() {
  loading.value = true
  try {
    const params = {
      index: pagination.value.current,
      size: pagination.value.size,
      alertContent: searchForm.value.warningContent || '',
      enterpriseName: searchForm.value.companyName || '',
      riskLevel: searchForm.value.riskLevel || '',
      startTime: '',
      endTime: '',
      sourceId: ''
    }
    // 处理时间段
    const timeRange = searchForm.value.inspectionTime
    if (timeRange && Array.isArray(timeRange) && timeRange.length === 2) {
      params.startTime = formatTimestamp(timeRange[0])
      params.endTime = formatTimestamp(timeRange[1])
    }
    const res = await axios.post(API_URL, params)
    console.log('商改住风险预警分页查询返回:', res)
    const data = res.data?.data || {}
    const content = data.content || []
    tableData.value = content.map(item => ({
      eventId: item.eventId || '',
      warningCode: item.alertCode || '--',
      warningContent: item.alertContent || '--',
      riskLevel: riskLevelTextMap[item.riskLevel] || item.riskLevel || '--',
      eventTime: item.occurTime || '--',
      companyName: item.enterpriseName || '--',
      disposeTime: item.disposeTime || '--',
      sourceId: item.sourceId || ''
    }))
    const page = data.page || {}
    pagination.value.total = page.total || 0
    pagination.value.totalPage = Math.ceil((page.total || 0) / pagination.value.size) || 1
  } catch (error) {
    console.error('商改住风险预警分页查询失败:', error)
  } finally {
    loading.value = false
  }
}

// 时间戳格式化为 yyyy-MM-dd HH:mm:ss
function formatTimestamp(ts) {
  if (!ts) return ''
  const d = new Date(ts)
  const pad = n => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}

function handlePageChange(page) {
  pagination.value.current = page
  fetchList()
}

// 搜索
function handleSearch() {
  pagination.value.current = 1
  fetchList()
}

// 重置
function handleReset() {
  searchForm.value = {
    warningContent: '',
    companyName: '',
    riskLevel: '',
    inspectionTime: null
  }
  pagination.value.current = 1
  fetchList()
}

onMounted(() => {
  fetchList()
})

// 操作列
const actionColumn = {
  title: '操作',
  key: 'action',
  width: 122,
  align: 'center',
  render(row) {
    return h('a', {
      style: { color: '#04bcfa', cursor: 'pointer', textDecoration: 'none' },
      onClick: () => showRiskWarningDetail(row)
    }, '详情')
  }
}

// 风险预警详情 - 通过alertCode查询flowId，触发事件总线
const EVENT_DETAIL_API = 'http://23.210.227.34:23343/yztapi/cockpit/component/event/getEventList'

function showRiskWarningDetail(row) {
  const alertCode = row.warningCode || ''
  if (!alertCode || alertCode === '--') return
  axios.post(EVENT_DETAIL_API, {
    regionCode: ['500103'],
    eventStatus: ['10', '97', '99'],
    flag: '1',
    type: null,
    keyword: alertCode,
    pageNum: 1,
    pageSize: 10,
    sortOrder: 'DESC',
    sortField: 'happen_time'
  }).then(response => {
    const responseData = response?.data?.resultBody?.page
    const detailList = Array.isArray(responseData?.records) ? responseData.records : []
    const detailItem = detailList[0] || {}
    if (!detailItem.flowId) {
      console.error('查询flowId失败: flowId为空')
      return
    }
    // 触发事件总线
    props.bus?.emit?.('EventDetailModal:eventData', {
      data: { flowId: detailItem.flowId, eventNum: alertCode },
      source: 'HouseRiskWarning'
    })
  }).catch(error => {
    console.error('查询flowId失败:', error)
  })
}

// 表格列定义
const columns = [
  { title: '预警编号', key: 'warningCode', align: 'center', width: 140, ellipsis: { tooltip: true } },
  { title: '预警内容', key: 'warningContent', align: 'center', ellipsis: { tooltip: true } },
  { title: '风险等级', key: 'riskLevel', align: 'center', width: 100, ellipsis: { tooltip: true } },
  { title: '发生时间', key: 'eventTime', align: 'center', width: 180, ellipsis: { tooltip: true } },
  { title: '企业名称', key: 'companyName', align: 'center', ellipsis: { tooltip: true } },
  actionColumn
]
</script>

<style lang="scss" scoped>
.risk-warning-popup {
  width: 1284px;
  height: 756px;
  background: url('../img/diaBg.png') no-repeat center / 100% 100%;
  transform: translate(-50%, -50%);
  position: relative;
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

  // 搜索栏
  .search-bar {
    display: flex;
    flex-wrap: wrap;
    row-gap: 16px;
    column-gap: 24px;
    padding: 15px 25px;
    margin-top: 10px;

    .search-item {
      display: flex;
      align-items: center;
      gap: 8px;
      justify-content: flex-start;
      flex-shrink: 0;

      label {
        white-space: nowrap;
        color: #ffffff;
        font-size: 14px;
        font-weight: 400;
        font-family: "Alibaba PuHuiTi 2.0", "Microsoft YaHei", sans-serif;
        line-height: 22px;
      }
    }

    .search-input,
    .search-select {
      width: 240px;
    }

    .search-date-picker {
      width: 280px;
    }

    .date-config-wrapper {
      display: contents;
    }

    .search-buttons {
      display: flex;
      align-items: center;
      gap: 8px;
    }

    .search-btn {
      width: 70px;
      height: 32px;
      border: 1px solid #04bcfa;
      border-radius: 4px;
      background: rgba(4, 188, 250, 0.15);
      color: #04bcfa;
      font-size: 14px;
      cursor: pointer;
      font-family: "Alibaba PuHuiTi 2.0", "Microsoft YaHei", sans-serif;

      &:hover {
        background: rgba(4, 188, 250, 0.3);
      }
    }

    .reset-btn {
      width: 70px;
      height: 32px;
      border: 1px solid #ffffff3d;
      border-radius: 4px;
      background: transparent;
      color: #f5fcff73;
      font-size: 14px;
      cursor: pointer;
      font-family: "Alibaba PuHuiTi 2.0", "Microsoft YaHei", sans-serif;

      &:hover {
        background: rgba(255, 255, 255, 0.08);
      }
    }
  }

  // 表格容器
  .table-container {
    padding: 0 25px;
    margin-top: 8px;
    height: calc(100% - 250px);
  }

  // 表头分类
  .table-header {
    display: flex;
    height: 32px;
    line-height: 32px;
    font-size: 16px;
    font-weight: bold;

    .header-left {
      flex: 1;
      text-align: center;
      background: url('../img/矩形.png') no-repeat center / 100% 100%;
    }

    .header-right {
      width: 122px;
      height: 32px;
      text-align: center;
      background: url('../img/back.png') no-repeat center / 100% 100%;
    }
  }

  // 分页容器
  .pagination-wrapper {
    display: flex;
    justify-content: center;
    padding: 10px 0;
  }
}

// Element Plus 输入框样式覆写
.search-input {
  :deep(.el-input__wrapper) {
    background: transparent !important;
    border: 1px solid #ffffff3d !important;
    border-radius: 4px !important;
    height: 32px;
    box-shadow: none !important;
  }

  :deep(.el-input__wrapper:hover) {
    border-color: #ffffff5e !important;
  }

  :deep(.el-input__wrapper.is-focus) {
    border-color: #ffffff5e !important;
  }

  :deep(.el-input__inner) {
    color: #f5fcff73 !important;
    font-size: 14px;
    font-weight: 400;
    font-family: "Alibaba PuHuiTi 2.0", "Microsoft YaHei", sans-serif;
    height: 32px;
    line-height: 32px;
  }

  :deep(.el-input__inner::placeholder) {
    color: #f5fcff73 !important;
  }
}

// Element Plus 下拉框样式覆写
.search-select {
  :deep(.el-select__wrapper),
  :deep(.el-select__wrapper.is-hovering),
  :deep(.el-select__wrapper.is-focused) {
    background: transparent !important;
    border: 1px solid #ffffff3d !important;
    border-radius: 4px !important;
    height: 32px;
    box-shadow: none !important;
  }

  :deep(.el-select__wrapper:hover) {
    border-color: #ffffff5e !important;
  }

  :deep(.el-select__wrapper.is-focused) {
    border-color: #ffffff5e !important;
  }

  :deep(.el-select__placeholder),
  :deep(.el-select__selected-item) {
    color: #f5fcff73 !important;
    font-size: 14px;
    font-weight: 400;
    font-family: "Alibaba PuHuiTi 2.0", "Microsoft YaHei", sans-serif;
  }
}

// NaiveUI 日期选择器样式覆写
.search-date-picker {
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

// NaiveUI 表格主题覆写
:deep(.risk-warning-table) {
  .n-data-table-wrapper {
    background: transparent;
  }

  .n-data-table-table {
    background: transparent;
    font-size: 14px;
  }

  .n-data-table-th {
    height: 42px;
    padding: 0 12px;
    color: #8bbce6;
    font-weight: 500;
    background: rgba(4, 111, 241, 0.2);
    border: none;
  }

  .n-data-table-td {
    height: 42px;
    padding: 0 12px;
    color: #d7e9ff;
    background: transparent;
    border: none;
  }

  .n-data-table-tr:nth-child(even) .n-data-table-td {
    background: rgba(4, 84, 203, 0.08);
  }

  .n-data-table-tr:hover .n-data-table-td {
    background: rgba(4, 111, 241, 0.15);
  }

  .n-scrollbar-rail--vertical {
    width: 6px;
  }

  .n-scrollbar-rail__scrollbar {
    background: rgba(71, 202, 255, 0.68);
  }
}

// NaiveUI 分页主题覆写
.pagination-wrapper {
  :deep(.n-pagination) {
    display: flex;
    align-items: center;
    gap: 6px;
  }

  :deep(.n-pagination-item),
  :deep(.n-pagination-button) {
    min-width: 32px;
    height: 32px;
    border: 1px solid rgba(4, 111, 241, 0.4);
    background: rgba(4, 84, 203, 0.2);
    color: #d7e9ff;
    font-size: 14px;
    border-radius: 4px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
  }

  :deep(.n-pagination-item:hover),
  :deep(.n-pagination-button:hover) {
    color: #fff;
    border-color: #046ff1;
    background: rgba(4, 111, 241, 0.4);
  }

  :deep(.n-pagination-item--active) {
    background: #046ff1 !important;
    border-color: #046ff1 !important;
    color: #fff !important;
  }
}
</style>

<style lang="scss">
// 下拉选项面板样式（popper 渲染在 body 上，不能用 scoped）
.risk-warning-select-popper {
  background: rgba(4, 28, 72, 0.95) !important;
  border: 1px solid rgba(4, 111, 241, 0.4) !important;

  .el-select-dropdown__item {
    color: #d7e9ff;
    font-size: 14px;
    font-family: "Alibaba PuHuiTi 2.0", "Microsoft YaHei", sans-serif;
    background: transparent;
  }

  .el-select-dropdown__item.hover,
  .el-select-dropdown__item:hover {
    background: rgba(4, 111, 241, 0.3) !important;
    color: #fff;
  }

  .el-select-dropdown__item.selected {
    color: #04bcfa;
    font-weight: bold;
  }

  .el-popper__arrow::before {
    background: rgba(4, 28, 72, 0.95) !important;
    border-color: rgba(4, 111, 241, 0.4) !important;
  }
}
</style>
