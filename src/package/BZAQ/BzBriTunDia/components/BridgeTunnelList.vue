<template>
  <div class="bridge-tunnel-list-popup">
    <div class="popup-header">
      <div class="title-bg">
        <h3 class="title">{{ popupTitle }}</h3>
      </div>
      <img class="close-btn" :src="closeIcon" alt="关闭" @click="$emit('close')" />
    </div>

    <div class="popup-body">
      <div class="search-bar">
        <div class="search-item">
          <label>构筑物类型：</label>
          <el-select v-model="searchForm.structType" class="search-select" popper-class="bridge-tunnel-select-popper" placeholder="请选择" clearable>
            <el-option v-for="item in typeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </div>
        <div class="search-item">
          <label>构筑物名称：</label>
          <el-input v-model="searchForm.structName" class="search-input" placeholder="请输入" clearable @keyup.enter="handleSearch" />
        </div>
        <div class="search-item">
          <label>重点筛选：</label>
          <el-select v-model="searchForm.importantFlag" class="search-select" popper-class="bridge-tunnel-select-popper" placeholder="全部" clearable>
            <el-option v-for="item in importantOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </div>
        <div class="search-item search-btn-item">
          <button class="search-btn" @click="handleSearch">查询</button>
        </div>
      </div>

      <div v-if="pageData.length" class="card-grid">
        <!-- <article
          v-for="item in pageData"
          :key="item.id"
          class="bridge-card"
          :class="{ 'bridge-card--warning': item.hasWarning }"
          @click="openFacilityDetail(item)"
        > -->
        <article
          v-for="item in pageData"
          :key="item.id"
          class="bridge-card"
          :class="{ 'bridge-card--warning': item.hasWarning }"
        >
          <div class="bridge-redCard" v-if="item.importantFlag">重点观察</div>
          <div class="bridge-card__title">
            <span>{{ item.title }}</span>
          </div>
          <div class="bridge-card__content">
            <div class="bridge-card__info">
              <p><span>构筑物类型：</span>{{ item.type }}</p>
              <p><span>构筑物位置：</span>{{ item.location }}</p>
              <p><span>监测设备数量：</span>激活(<b>{{ item.activeDevice }}</b>) / 未激活(<em>{{ item.inactiveDevice }}</em>)</p>
              <p><span>开始监测时间：</span>{{ item.startDate }}</p>
              <p><span>技术状况等级：</span><i>{{ item.techLevel }}</i></p>
            </div>
            <img class="bridge-card__image" :src="item.picUrl || exampleImg" alt="" />
          </div>
          <div class="bridge-card__warn">
            <img class="bridge-card__warn-icon" :src="itemIcon" alt="" />
            <span>预警：</span>{{ item.warningText }}
          </div>
          <div class="bridge-card__actions">
            <button type="button" class="bridge-card__action-btn" @click="openModelIframe(item)">监测可视化</button>
            <button type="button" class="bridge-card__action-btn" @click="openFacilityDetail(item)">设施详情</button>
          </div>
        </article>
      </div>
      <div v-else class="empty-state">
        <span>暂无数据</span>
      </div>

      <div class="pagination-wrapper">
        <span class="total-text">共 {{ pagination.totalPage }} 页</span>
        <n-pagination :page="pagination.current" :page-count="pagination.totalPage" :page-slot="5" @update:page="handlePageChange" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { NPagination } from 'naive-ui'
import { ElInput, ElSelect, ElOption } from 'element-plus'
import 'element-plus/dist/index.css'
import axios from 'axios'
import closeIcon from '../img/close.png'
import exampleImg from '../img/example.png'
import itemIcon from '../img/itemIcon.png'

const props = defineProps({
  payload: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'open-detail', payload: Record<string, any>): void
  (e: 'open-model-iframe', payload: Record<string, any>): void
  (e: 'open-facility-detail', payload: Record<string, any>): void
}>()

const BASE_URL = 'http://23.99.16.179:11001/api/boot/system/bridge'

const typeOptions = [
  { label: '桥梁', value: 1 },
  { label: '隧道', value: 4 }
]

const importantOptions = [
  { label: '否', value: 0 },
  { label: '是', value: 1 }
]

const searchForm = ref({
  structType: Number(props.payload?.structType) || null as number | null,
  structName: '',
  importantFlag: normalizeImportantFlag(props.payload?.importantFlag)
})

const bridgeRows = ref<any[]>([])

const pagination = ref({
  current: 1,
  size: 6,
  total: 0,
  totalPage: 1
})

const pageData = computed(() => bridgeRows.value)
const popupTitle = computed(() => {
  if (String(searchForm.value.structType) === '1') return '桥梁列表'
  if (String(searchForm.value.structType) === '4') return '隧道列表'
  return '桥隧列表'
})

function getPageContent(data: any) {
  return data?.content || data?.records || data?.list || data?.rows || data?.data || []
}

function getPageTotal(data: any) {
  return Number(data?.page?.total ?? data?.total ?? data?.totalElements ?? data?.count ?? 0) || 0
}

function getResponseData(data: any) {
  return data?.data ?? data ?? {}
}

function normalizeStructType(type: any) {
  if (String(type) === '1') return '桥梁'
  if (String(type) === '4') return '隧道'
  return type || '-'
}

function normalizeImportantFlag(value: any): number | null {
  const flag = Number(value)
  return flag === 0 || flag === 1 ? flag : null
}

function normalizeRow(item: any, index: number) {
  const warningFlag = Number(item?.warningFlag) || 0
  return {
    id: item?.structId || `${pagination.value.current}-${index}`,
    structId: item?.structId,
    title: item?.structName || '-',
    type: normalizeStructType(item?.structType),
    location: item?.address || '-',
    activeDevice: item?.inUseDeviceCount ?? 0,
    inactiveDevice: item?.unUseDeviceCount ?? 0,
    startDate: item?.startTime || '-',
    techLevel: item?.statusLevel || '-',
    picUrl: item?.structSchematicDiagram?.url || '',
    importantFlag: item.importantFlag,
    hasWarning: warningFlag !== 0,
    warningText: warningFlag !== 0 ? '存在预警' : '暂无预警'
  }
}

async function fetchBridgeTunnelList() {
  try {
    const res = await axios.get(`${BASE_URL}/list`, {
      params: {
        structType: searchForm.value.structType || undefined,
        structName: searchForm.value.structName || undefined,
        importantFlag: searchForm.value.importantFlag === null ? undefined : searchForm.value.importantFlag,
        pageIndex: pagination.value.current,
        pageSize: pagination.value.size
      }
    })
    const data = res?.data?.data || res?.data || {}
    const rows = getPageContent(data)
    const total = getPageTotal(data)
    bridgeRows.value = Array.isArray(rows) ? rows.map(normalizeRow) : []
    pagination.value.total = total || bridgeRows.value.length
    pagination.value.totalPage = Math.max(1, Math.ceil(pagination.value.total / pagination.value.size))
  } catch (err) {
    console.error('获取桥隧列表失败', err)
    bridgeRows.value = []
    pagination.value.total = 0
    pagination.value.totalPage = 1
  }
}

function handlePageChange(page: number) {
  pagination.value.current = page
  fetchBridgeTunnelList()
}

function handleSearch() {
  pagination.value.current = 1
  fetchBridgeTunnelList()
}

function openFacilityDetail(item: any) {
  if (!item?.structId) return
  const payload = {
    diaName: 'facility-monitor-detail',
    structId: item.structId,
    structName: item.title,
    picUrl: item.picUrl
  }
  emit('open-facility-detail', payload)
}

async function openModelIframe(item: any) {
  if (!item?.structId) return
  try {
    const res = await axios.get(`${BASE_URL}/model`, {
      params: { structId: item.structId }
    })
    const url = getResponseData(res?.data)
    if (!url) {
      console.warn('模型地址为空，无法打开监测可视化')
      return
    }
    emit('open-model-iframe', {
      url,
      title: item.title,
      structId: item.structId
    })
  } catch (error) {
    console.error('获取桥隧模型地址失败:', error)
  }
}

watch(
  () => props.payload?.structType,
  value => {
    searchForm.value.structType = Number(value) || null
    pagination.value.current = 1
    fetchBridgeTunnelList()
  }
)

watch(
  () => props.payload?.importantFlag,
  value => {
    searchForm.value.importantFlag = normalizeImportantFlag(value)
    pagination.value.current = 1
    fetchBridgeTunnelList()
  }
)

onMounted(() => {
  fetchBridgeTunnelList()
})
</script>

<script lang="ts">
export default {
  name: 'BridgeTunnelList'
}
</script>

<style lang="scss" scoped>
.bridge-tunnel-list-popup {
  width: 1646px;
  height: 850px;
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  background: url('../img/diaBg.png') no-repeat center center;
  background-size: 100% 100%;
  display: flex;
  flex-direction: column;
  pointer-events: auto;
  color: #d8efff;
  font-family: 'Alibaba PuHuiTi 2.0', 'Microsoft YaHei', sans-serif;
}

.popup-header {
  width: 100%;
  height: 50px;
  position: relative;
  flex-shrink: 0;
}

.title-bg {
  width: 100%;
  height: 100%;
  background: url('../img/diaTitleBg.png') no-repeat center center;
  background-size: 100% 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.title {
  margin: 0;
  text-align: center;
  font-size: 20px;
  font-weight: 500;
  background: linear-gradient(0deg, #5fbcff 1.4%, #fff 58.67%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.close-btn {
  width: 24px;
  height: 24px;
  cursor: pointer;
  position: absolute;
  right: 18px;
  top: 50%;
  transform: translateY(-50%);
}

.popup-body {
  height: calc(100% - 50px);
  box-sizing: border-box;
  padding: 28px 34px 18px;
  display: flex;
  flex-direction: column;
}

.search-bar {
  height: 38px;
  display: flex;
  align-items: center;
  gap: 64px;
  flex-shrink: 0;
}

.search-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.search-item label {
  white-space: nowrap;
  color: #fff;
  font-size: 14px;
  line-height: 22px;
}

.search-select,
.search-input {
  width: 240px;
}

.search-btn {
  width: 70px;
  height: 32px;
  border: 1px solid #04bcfa;
  border-radius: 4px;
  background: rgba(4, 188, 250, 0.18);
  color: #fff;
  font-size: 14px;
  cursor: pointer;
}

.search-btn:hover {
  background: rgba(4, 188, 250, 0.34);
}

.card-grid {
  flex: 1;
  min-height: 0;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  grid-auto-rows: 1fr;
  gap: 22px 18px;
  padding-top: 20px;
}

.empty-state {
  flex: 1;
  min-height: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  padding-top: 20px;
  color: rgba(216, 239, 255, 0.72);
  font-size: 20px;
  letter-spacing: 0;
  text-shadow: 0 0 10px rgba(95, 246, 255, 0.28);
}

.empty-state span {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 160px;
  height: 48px;
  border: 1px solid rgba(45, 137, 218, 0.46);
  background: rgba(4, 42, 86, 0.35);
}

.bridge-card {
  position: relative;
  min-width: 0;
  box-sizing: border-box;
  padding: 18px 18px 16px;
  background: url('../img/item.png') center / 100% 100% no-repeat;
  border: 1px solid rgba(4, 111, 241, 0.58);
  overflow: hidden;
}
.bridge-redCard{
  position: absolute;
  right: 2px;
  top:4px;
  padding:2px 6px;
  color:#FFF;
  background: rgba(255, 0, 0,0.6);
}

.bridge-card--warning {
  border-color: #ff8a00;
  animation: warning-border-breath 1.8s ease-in-out infinite;
}

.bridge-card__title {
  height: 32px;
  display: flex;
  align-items: center;
  padding-left: 16px;
  box-sizing: border-box;
  color: #fff;
  font-size: 15px;
  font-weight: 700;
  background: url('../img/itemtitle.png') center / 100% 100% no-repeat;
}

.bridge-card__title span,
.bridge-card__info p,
.bridge-card__warn {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.bridge-card__content {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 172px;
  gap: 12px;
  margin-top: 18px;
}

.bridge-card__info {
  min-width: 0;
}

.bridge-card__info p {
  margin: 0 0 16px;
  color: #fff;
  font-size: 14px;
  line-height: 1;
}

.bridge-card__info span {
  color: #8bbce6;
}

.bridge-card__info b {
  color: #5ff6ff;
  font-style: normal;
}

.bridge-card__info em {
  color: #ff8a00;
  font-style: normal;
}

.bridge-card__info i {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 30px;
  height: 18px;
  color: #5ff6ff;
  font-style: normal;
  border: 1px solid rgba(95, 246, 255, 0.7);
  background: rgba(20, 116, 201, 0.28);
}

.bridge-card__image {
  width: 172px;
  height: 104px;
  object-fit: cover;
}

.bridge-card__warn {
  height: 40px;
  margin-top: 3px;
  display: flex;
  align-items: center;
  box-sizing: border-box;
  padding-right: 12px;
  color: #8bbce6;
  font-size: 14px;
  background: url('../img/itemwarn.png') center / 100% 100% no-repeat;
}

.bridge-card__warn-icon {
  width: 20px;
  height: 30px;
  margin-right: 6px;
  object-fit: contain;
  flex: 0 0 auto;
}

.bridge-card__warn span {
  color: #ffbc00;
  margin-right: 6px;
}

.bridge-card__actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  height: 30px;
  margin-top: 8px;
}

.bridge-card__action-btn {
  min-width: 96px;
  height: 30px;
  padding: 0 12px;
  color: #d9f7ff;
  font-size: 14px;
  line-height: 28px;
  border: 1px solid rgba(95, 246, 255, 0.72);
  border-radius: 2px;
  background: linear-gradient(180deg, rgba(9, 101, 180, 0.88), rgba(4, 60, 124, 0.82));
  box-shadow: inset 0 0 10px rgba(95, 246, 255, 0.16);
  cursor: pointer;
}

.bridge-card__action-btn:hover {
  color: #5ff6ff;
  border-color: rgba(95, 246, 255, 0.96);
  filter: brightness(1.12);
}

.pagination-wrapper {
  height: 34px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.total-text {
  color: #8bbce6;
  font-size: 13px;
}

.search-input :deep(.el-input__wrapper),
.search-select :deep(.el-select__wrapper) {
  background: transparent !important;
  border: 1px solid #ffffff3d !important;
  border-radius: 4px !important;
  height: 32px;
  box-shadow: none !important;
}

.search-input :deep(.el-input__inner),
.search-input :deep(.el-input__inner::placeholder),
.search-select :deep(.el-select__placeholder),
.search-select :deep(.el-select__selected-item) {
  color: #f5fcff73 !important;
  font-size: 14px;
}

.pagination-wrapper :deep(.n-pagination) {
  display: flex;
  align-items: center;
  gap: 6px;
}

.pagination-wrapper :deep(.n-pagination-item),
.pagination-wrapper :deep(.n-pagination-button) {
  min-width: 28px;
  height: 28px;
  border: 1px solid rgba(4, 111, 241, 0.4);
  background: rgba(4, 84, 203, 0.2);
  color: #d7e9ff;
  font-size: 13px;
  border-radius: 4px;
}

.pagination-wrapper :deep(.n-pagination-item--active) {
  background: #046ff1 !important;
  border-color: #046ff1 !important;
  color: #fff !important;
}

@keyframes warning-border-breath {
  0%,
  100% {
    box-shadow: 0 0 0 rgba(255, 138, 0, 0.12), inset 0 0 16px rgba(255, 138, 0, 0.04);
  }

  50% {
    box-shadow: 0 0 18px rgba(255, 138, 0, 0.76), inset 0 0 18px rgba(255, 138, 0, 0.18);
  }
}
</style>

<style lang="scss">
.bridge-tunnel-select-popper {
  background: rgba(4, 28, 72, 0.95) !important;
  border: 1px solid rgba(4, 111, 241, 0.4) !important;

  .el-select-dropdown__item {
    color: #d7e9ff;
    font-size: 14px;
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
