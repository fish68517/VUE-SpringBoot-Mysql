<template>
  <div class="warning-detail-popup">
    <!-- 顶部标题栏 -->
    <div class="popup-header">
      <h3 class="title">预警详情</h3>
      <img class="close-btn" :src="closeIcon" alt="关闭" @click="$emit('close')" />
    </div>

    <div class="warning-detail">
      <!-- 事件内容 -->
      <div class="detail-section">
        <div class="detail-section__title">事件内容</div>
        <div class="detail-grid">
          <div class="detail-field">
            <span class="detail-label">事件编码</span>
            <span class="detail-value">{{ detail.eventCode }}</span>
          </div>
          <div class="detail-field">
            <span class="detail-label">发生时间</span>
            <span class="detail-value">{{ detail.eventTime }}</span>
          </div>
          <div class="detail-field">
            <span class="detail-label">风险等级</span>
            <span class="detail-value">{{ detail.riskLevel }}</span>
          </div>
          <div class="detail-field detail-field--full">
            <span class="detail-label">事件内容</span>
            <span class="detail-value detail-value--content">{{ detail.eventContent }}</span>
          </div>
        </div>
      </div>

      <!-- 预警内容 -->
      <div class="detail-section">
        <div class="detail-section__title">预警内容</div>
        <div class="detail-grid">
          <div class="detail-field">
            <span class="detail-label">预警编号</span>
            <span class="detail-value">{{ detail.warningCode }}</span>
          </div>
          <div class="detail-field">
            <span class="detail-label">事件类型</span>
            <span class="detail-value">{{ detail.eventType }}</span>
          </div>
          <div class="detail-field">
            <span class="detail-label">处置状态</span>
            <span class="detail-value">{{ detail.handleStatus }}</span>
          </div>
          <div class="detail-field">
            <span class="detail-label">处理人</span>
            <span class="detail-value">{{ detail.handler }}</span>
          </div>
          <div class="detail-field">
            <span class="detail-label">处理时间</span>
            <span class="detail-value">{{ detail.handleTime }}</span>
          </div>
        </div>
      </div>

      <!-- 其他信息 -->
      <div class="detail-section">
        <div class="detail-section__title">其他信息</div>
        <div class="detail-grid detail-grid--images">
          <div class="detail-field">
            <span class="detail-label">预警信息</span>
            <div class="detail-image-list">
              <el-image
                v-for="(img, idx) in detail.warningImages"
                :key="'w' + idx"
                class="detail-image"
                :src="img"
                :preview-src-list="detail.warningImages"
                :initial-index="idx"
                fit="cover"
                preview-teleported
              />
            </div>
          </div>
          <div class="detail-field">
            <span class="detail-label">现场处置照片</span>
            <div class="detail-image-list">
              <el-image
                v-for="(img, idx) in detail.sceneImages"
                :key="'s' + idx"
                class="detail-image"
                :src="img"
                :preview-src-list="detail.sceneImages"
                :initial-index="idx"
                fit="cover"
                preview-teleported
              />
            </div>
          </div>
        </div>
      </div>

      <!-- 设备信息 -->
      <div class="detail-section">
        <div class="detail-section__title">设备信息</div>
        <div class="detail-grid">
          <div class="detail-field">
            <span class="detail-label">设备编码</span>
            <span class="detail-value">{{ detail.deviceCode }}</span>
          </div>
          <div class="detail-field">
            <span class="detail-label">设备名称</span>
            <span class="detail-value">{{ detail.deviceName }}</span>
          </div>
          <div class="detail-field">
            <span class="detail-label">所属区域(街道)</span>
            <span class="detail-value">{{ detail.deviceArea }}</span>
          </div>
          <div class="detail-field">
            <span class="detail-label">位置</span>
            <span class="detail-value">{{ detail.deviceLocation }}</span>
          </div>
          <div class="detail-field">
            <span class="detail-label">设备状态</span>
            <span class="detail-value">{{ detail.deviceStatus }}</span>
          </div>
          <div class="detail-field">
            <span class="detail-label">联系电话</span>
            <span class="detail-value">{{ detail.contactPhone }}</span>
          </div>
        </div>
      </div>

      <!-- 历史记录 -->
      <div class="detail-section">
        <div class="detail-section__title">历史记录</div>
        <div class="history-filter">
          <div class="filter-item">
            <span class="filter-label">时间</span>
            <el-date-picker
              v-model="historyQuery.timeRange"
              type="daterange"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
              :clearable="true"
              @change="handleHistorySearch"
            />
          </div>
          <button class="search-btn" @click="handleHistorySearch">查询</button>
          <button class="search-btn reset-btn" @click="handleHistoryReset">重置</button>
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
import { ref, onMounted, watch } from 'vue'
import { ElImage, ElDatePicker } from 'element-plus'
import { NDataTable, NPagination } from 'naive-ui'
import axios from 'axios'
import closeIcon from '../img/close.png'

const props = defineProps({
  detailData: {
    type: Object,
    default: () => ({})
  },
  id: {
    type: [String, Number],
    default: '4952'
  },
  houseType: {
    type: String,
    default: '商改住'
  }
})

defineEmits(['close'])

// 预警详情接口 - 按房屋类型区分
const WARNING_DETAIL_API_MAP = {
  '群租空间': 'http://23.99.16.86:11001/api/v1/system/smartRisk/group/page/detail',
  '商改住': 'http://23.99.16.86:11001/api/v1/system/smartRisk/sgz/warning/detail'
}
// 设备历史预警接口
const DEVICE_HISTORY_API = 'http://23.99.16.86:11001/api/v1/system/smartRisk/group/page/detailDevice'

const detail = ref({
  eventCode: '',
  eventTime: '',
  riskLevel: '',
  eventContent: '',
  warningCode: '',
  eventType: '',
  handleStatus: '',
  handler: '',
  handleTime: '',
  warningImages: [],
  sceneImages: [],
  deviceCode: '',
  deviceName: '',
  deviceArea: '',
  deviceLocation: '',
  deviceStatus: '',
  contactPhone: ''
})

async function fetchDetail(id) {
  const api = WARNING_DETAIL_API_MAP[props.houseType] || WARNING_DETAIL_API_MAP['群租空间']
  try {
    const res = await axios.get(api, { params: { id } })
    console.log(`预警详情接口返回结果(${props.houseType}):`, res)
    // 兼容取值 res.data.jsonResult.data 或 res.data.data
    const rawData = res.data?.jsonResult?.data || res.data?.data || {}
    detail.value = {
      eventCode: rawData.eventNo || '',
      eventTime: rawData.occurTime || '',
      riskLevel: rawData.riskLevelName || rawData.riskLevel || '',
      eventContent: rawData.description || '',
      warningCode: rawData.eventNo2 || '',
      eventType: rawData.eventTypeName || rawData.eventType || '',
      handleStatus: rawData.disposeName || rawData.disposeStatus || '',
      handler: rawData.disposeUserName || '',
      handleTime: rawData.disposeTime || '',
      warningImages: rawData.evidenceImagesList || (rawData.evidenceImages ? [rawData.evidenceImages] : []),
      sceneImages: rawData.disposeImagesList || (rawData.disposeImages ? [rawData.disposeImages] : []),
      deviceCode: rawData.deviceNo || '',
      deviceName: rawData.deviceName || '',
      deviceArea: rawData.houseAddress || '',
      deviceLocation: rawData.houseLocation || '',
      deviceStatus: rawData.deviceStatusName || rawData.deviceStatus || '',
      contactPhone: rawData.chargeUserPhone || ''
    }
  } catch (error) {
    console.error('预警详情接口请求失败:', error)
  }
}

onMounted(() => {
  fetchDetail(props.id)
})

watch(() => props.id, (newId) => {
  if (newId) fetchDetail(newId)
})

watch(() => props.houseType, () => {
  if (props.id) fetchDetail(props.id)
})

/* ===================== 历史记录 ===================== */
const historyQuery = ref({
  timeRange: []
})

const historyColumns = [
  { title: '预警编号', key: 'eventNo', align: 'center', ellipsis: { tooltip: true } },
  { title: '预警类型', key: 'eventTypeName', align: 'center', ellipsis: { tooltip: true } },
  { title: '风险等级', key: 'riskLevelName', align: 'center', ellipsis: { tooltip: true } },
  { title: '发生时间', key: 'occurTime', align: 'center', ellipsis: { tooltip: true } },
  { title: '处理时间', key: 'disposeTime', align: 'center', ellipsis: { tooltip: true } }
]

const historyData = ref([])
const historyPagination = ref({
  current: 1,
  totalPage: 1,
  size: 10,
  total: 0
})

// 根据时间段计算 startTime/endTime
function calcTimeRange() {
  const range = historyQuery.value.timeRange
  if (range && range.length === 2) {
    return {
      startTime: `${range[0]} 00:00:00`,
      endTime: `${range[1]} 23:59:59`
    }
  }
  return { startTime: '', endTime: '' }
}

async function fetchHistoryData() {
  if (!detail.value.deviceCode) return
  try {
    const { startTime, endTime } = calcTimeRange()
    const params = {
      index: historyPagination.value.current,
      size: historyPagination.value.size,
      deviceNo: detail.value.deviceCode,
      startTime: startTime || undefined,
      endTime: endTime || undefined
    }
    const res = await axios.post(DEVICE_HISTORY_API, params)
    console.log('设备历史预警接口返回结果:', res)
    const pageData = res.data?.data
    if (pageData) {
      historyData.value = pageData.content || []
      const page = pageData.page || {}
      historyPagination.value.total = page.total || 0
      historyPagination.value.totalPage = Math.ceil((page.total || 0) / historyPagination.value.size) || 1
    }
  } catch (error) {
    console.error('设备历史预警接口请求失败:', error)
  }
}

function handleHistorySearch() {
  historyPagination.value.current = 1
  fetchHistoryData()
}

function handleHistoryReset() {
  historyQuery.value.timeRange = []
  historyPagination.value.current = 1
  fetchHistoryData()
}

function handleHistoryPageChange(page) {
  historyPagination.value.current = page
  fetchHistoryData()
}

// 设备编码获取后自动查询历史记录
watch(() => detail.value.deviceCode, (newCode) => {
  if (newCode) {
    historyPagination.value.current = 1
    fetchHistoryData()
  }
})
</script>

<style lang="scss" scoped>
.warning-detail-popup {
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
}

.warning-detail {
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

    &--images {
      grid-template-columns: 1fr 1fr;
      gap: 16px 20px;
    }
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
      min-width: 70px;
    }

    .detail-value {
      font-size: 13px;
      color: #fff;
      word-break: break-all;
      flex: 1;

      &--content {
        line-height: 1.6;
        min-height: 60px;
        padding: 8px 12px;
        background: rgba(255, 255, 255, 0.05);
        border-radius: 4px;
        border: 1px solid rgba(255, 255, 255, 0.08);
      }
    }
  }

  .detail-image-list {
    display: flex;
    gap: 12px;
    flex-wrap: wrap;
  }

  .detail-image {
    width: 160px;
    height: 120px;
    border-radius: 4px;
    border: 1px solid rgba(0, 160, 255, 0.2);
    cursor: pointer;

    :deep(.el-image__inner) {
      border-radius: 4px;
    }
  }

  /* 历史记录 */
  .history-filter {
    display: flex;
    align-items: center;
    gap: 16px;
    margin-bottom: 12px;

    .filter-item {
      display: flex;
      align-items: center;
      gap: 8px;

      .filter-label {
        font-size: 13px;
        color: rgba(255, 255, 255, 0.6);
        white-space: nowrap;
      }

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

      :deep(.el-range-separator) {
        color: #f5fcff73 !important;
      }

      :deep(.el-range-input) {
        color: #f5fcff73 !important;
      }
    }

    .search-btn {
      padding: 5px 18px;
      font-size: 13px;
      color: #fff;
      background: rgba(4, 188, 250, 0.2);
      border: 1px solid rgba(4, 188, 250, 0.4);
      border-radius: 4px;
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        background: rgba(4, 188, 250, 0.35);
      }

      &.reset-btn {
        background: rgba(255, 255, 255, 0.08);
        border-color: rgba(255, 255, 255, 0.15);

        &:hover {
          background: rgba(255, 255, 255, 0.12);
        }
      }
    }
  }

  .history-table-wrapper {
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
</style>
