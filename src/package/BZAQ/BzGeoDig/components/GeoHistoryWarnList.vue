<template>
  <div class="fire-risk-point-modal">
    <n-config-provider class="history-config-provider" :locale="zhCN" :date-locale="dateZhCN">
      <div class="modal-header">
        <div class="header-title">{{ title }}</div>
        <button class="close-btn" @click="handleClose">×</button>
      </div>
      <div class="filter-bar">
        <div class="filter-item">
          <span class="filter-label">预警类型</span>
          <n-select
            v-model:value="selectedType"
            class="filter-select"
            :options="typeOptions"
            :theme-overrides="selectThemeOverrides"
            size="small"
            @update:value="handleFilter"
          />
        </div>
        <div class="filter-item">
          <span class="filter-label">预警时间</span>
          <n-date-picker
            v-model:value="selectedDate"
            class="filter-date"
            type="date"
            placeholder="选择日期"
            size="small"
            clearable
            @update:value="handleFilter"
          />
        </div>
        <div class="filter-total">共 {{ total }} 条</div>
      </div>
      <div class="modal-content">
        <div class="card-list">
          <div v-for="(item, index) in warnList" :key="item.id ?? item.fileId ?? index" class="warn-card">
            <div class="warn-card__title">{{ isWatershedMode ? `渝中区流域${item.warningLevel || ''}` : item.title }}</div>
            <div class="warn-card__bottom">
              <span class="warn-card__time">{{ isWatershedMode ? item.time : item.createdTime }}</span>
              <button class="detail-btn" @click="handleDetail(item)">详情</button>
            </div>
          </div>
          <div v-if="warnList.length === 0 && !loading" class="empty-tip">暂无数据</div>
          <div v-if="loading" class="empty-tip">加载中...</div>
        </div>
      </div>
      <!-- 分页 -->
      <div class="pagination-bar">
        <n-pagination
          v-model:page="pageIndex"
          :page-count="totalPages"
          :page-size="pageSize"
          :item-count="total"
          show-quick-jumper
          size="small"
          @update:page="handlePageChange"
        />
      </div>
    </n-config-provider>

    <!-- 预警详情小弹框 -->
    <div v-if="detailVisible" class="warn-detail-popup" :class="{ 'warn-detail-popup--image': isWatershedMode }" @click.stop>
      <div class="warn-detail-popup__header">
        <span class="warn-detail-popup__title">{{ isWatershedMode ? '流域预警详情' : detailData.title }}</span>
        <button class="warn-detail-popup__close" @click="closeDetail">×</button>
      </div>
      <div class="warn-detail-popup__body">
        <template v-if="isWatershedMode">
          <div v-if="imageLoading" class="image-empty">加载中...</div>
          <img v-else-if="imageUrl" class="warning-image" :src="imageUrl" alt="流域预警图片" />
          <div v-else class="image-empty">暂无图片</div>
        </template>
        <template v-else>
          <div class="warn-detail-popup__time">发布时间：{{ detailData.createdTime }}</div>
          <div class="warn-detail-popup__content">{{ detailData.content }}</div>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { NSelect, NDatePicker, NConfigProvider, NPagination, zhCN, dateZhCN } from 'naive-ui'
import axios from 'axios'
import { BASE_URL as HOST_BASE } from '../config'

const emit = defineEmits(['close'])

const title = '历史预警'

const WEATHER_WARN_URL = `${HOST_BASE}/api/boot/system/weather/warning/history`
const WATERSHED_WARN_URL = `${HOST_BASE}/api/boot/system/land/warningAnalysis/history`
const PREVIEW_FILE_URL = `${HOST_BASE}/api/boot/system/land/previewFile`

const isWatershedMode = computed(() => selectedType.value === 36)

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
  borderFocus: '1px solid rgba(83, 174, 255, 0.32)'
}

const selectThemeOverrides = {
  peers: {
    InternalSelection: {
      ...controlTheme,
      arrowColor: 'rgba(184, 217, 255, 0.55)'
    }
  }
}

// 预警类型选项，value对应接口info_type_id：21暴雨，27高温，24大风，35大雾，32雷电，36流域
const typeOptions = [
  { label: '全部', value: null },
  { label: '暴雨预警', value: 21 },
  { label: '高温预警', value: 27 },
  { label: '大风预警', value: 24 },
  { label: '大雾预警', value: 35 },
  { label: '雷电预警', value: 32 },
  { label: '流域预警', value: 36 }
]

const selectedType = ref(null)
const selectedDate = ref(null)

const warnList = ref([])
const loading = ref(false)
const pageIndex = ref(1)
const pageSize = ref(10)
const total = ref(0)
const totalPages = ref(1)

function formatDate(timestamp) {
  if (!timestamp) return null
  const d = new Date(timestamp)
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}

async function fetchWarnList() {
  loading.value = true
  try {
    if (isWatershedMode.value) {
      await fetchWatershedWarnList()
      return
    }
    const params = {
      pageIndex: pageIndex.value,
      pageSize: pageSize.value
    }
    if (selectedType.value != null) {
      params.type = selectedType.value
    }
    const dateStr = formatDate(selectedDate.value)
    if (dateStr) {
      params.time = dateStr
    }
    const res = await axios.get(WEATHER_WARN_URL, { params })
    const data = res?.data?.data ?? res?.data ?? {}
    warnList.value = data?.records ?? []
    total.value = data?.total ?? 0
    totalPages.value = data?.pages ?? 1
  } catch (error) {
    console.error('获取历史预警数据失败:', error)
    warnList.value = []
    total.value = 0
    totalPages.value = 1
  } finally {
    loading.value = false
  }
}

async function fetchWatershedWarnList() {
  try {
    const res = await axios.get(WATERSHED_WARN_URL)
    const list = res?.data?.data ?? []
    const all = Array.isArray(list) ? list : []
    // 前端分页
    total.value = all.length
    totalPages.value = Math.max(1, Math.ceil(all.length / pageSize.value))
    const start = (pageIndex.value - 1) * pageSize.value
    warnList.value = all.slice(start, start + pageSize.value)
  } catch (error) {
    console.error('获取流域历史预警数据失败:', error)
    warnList.value = []
    total.value = 0
    totalPages.value = 1
  }
}

function handleFilter() {
  pageIndex.value = 1
  fetchWarnList()
}

function handlePageChange(page) {
  pageIndex.value = page
  fetchWarnList()
}

const detailVisible = ref(false)
const detailData = ref({ title: '', createdTime: '', content: '' })
const imageUrl = ref('')
const imageLoading = ref(false)

const getBase64MimeType = base64 => {
  if (base64.startsWith('/9j/')) return 'image/jpeg'
  if (base64.startsWith('iVBOR')) return 'image/png'
  if (base64.startsWith('R0lGOD')) return 'image/gif'
  if (base64.startsWith('UklGR')) return 'image/webp'
  return 'image/png'
}

const getPreviewPayload = value => {
  if (value == null) return ''
  if (typeof value !== 'string') return value
  const text = value.trim()
  if (!text) return ''
  try {
    const result = JSON.parse(text)
    return result?.data ?? result?.url ?? result?.fileUrl ?? result?.previewUrl ?? result
  } catch (_error) {
    return text
  }
}

const base64ToBlobUrl = value => {
  const text = String(value || '').trim()
  if (!text) return ''
  if (text.startsWith('data:image/')) return text
  const base64 = text.replace(/^data:image\/\w+;base64,/, '').replace(/\s/g, '')
  if (!/^[A-Za-z0-9+/]+={0,2}$/.test(base64)) return ''
  const byteCharacters = window.atob(base64)
  const byteArrays = []
  const sliceSize = 1024
  for (let offset = 0; offset < byteCharacters.length; offset += sliceSize) {
    const slice = byteCharacters.slice(offset, offset + sliceSize)
    const byteNumbers = new Array(slice.length)
    for (let i = 0; i < slice.length; i += 1) {
      byteNumbers[i] = slice.charCodeAt(i)
    }
    byteArrays.push(new Uint8Array(byteNumbers))
  }
  return URL.createObjectURL(new Blob(byteArrays, { type: getBase64MimeType(base64) }))
}

const blobToImageUrl = async blob => {
  if (!blob?.size) return ''
  if (blob.type?.startsWith('image/')) {
    return URL.createObjectURL(blob)
  }
  const text = await blob.text()
  const payload = getPreviewPayload(text)
  if (payload instanceof Blob) {
    return blobToImageUrl(payload)
  }
  if (typeof payload === 'string') {
    return base64ToBlobUrl(payload)
  }
  return ''
}

const revokeImageUrl = () => {
  if (imageUrl.value?.startsWith('blob:')) {
    URL.revokeObjectURL(imageUrl.value)
  }
  imageUrl.value = ''
}

const requestPreviewFile = async fileId => {
  const res = await axios.get(PREVIEW_FILE_URL, {
    params: { fileId },
    responseType: 'blob'
  })
  const fileData = getPreviewPayload(res?.data?.data ?? res?.data ?? res)
  if (fileData instanceof Blob) {
    return blobToImageUrl(fileData)
  }
  if (fileData instanceof ArrayBuffer) {
    return fileData.byteLength ? URL.createObjectURL(new Blob([fileData])) : ''
  }
  if (typeof fileData === 'string') {
    return base64ToBlobUrl(fileData)
  }
  return ''
}

async function handleDetail(item) {
  if (isWatershedMode.value) {
    revokeImageUrl()
    detailData.value = { title: '', createdTime: '', content: '' }
    detailVisible.value = true
    const fileId = item?.fileId || item?.fileID || item?.file_id || ''
    if (!fileId) return
    imageLoading.value = true
    try {
      const url = await requestPreviewFile(fileId)
      imageUrl.value = url
    } catch (error) {
      console.error('获取流域预警图片失败:', error)
      revokeImageUrl()
    } finally {
      imageLoading.value = false
    }
    return
  }
  detailData.value = {
    title: item.title || '',
    createdTime: item.createdTime || '',
    content: item.content || ''
  }
  detailVisible.value = true
}

function closeDetail() {
  detailVisible.value = false
  revokeImageUrl()
}

function handleClose() {
  emit('close')
}

onMounted(() => {
  fetchWarnList()
})
</script>

<script>
export default {
  name: 'GeoHistoryWarnList'
}
</script>

<style lang="scss" scoped>
@font-face {
  font-family: 'AlibabaPuHuiTi';
  src: url('../font/Alibaba_PuHuiTi_2.0_55_Regular_55_Regular.ttf') format('truetype');
  font-weight: normal;
  font-style: normal;
}

.fire-risk-point-modal {
  font-family: 'AlibabaPuHuiTi', sans-serif;
  position: absolute;
  transform: translate(-50%, -50%);
  z-index: 1001;
  width: 800px;
  height: 600px;
  display: flex;
  flex-direction: column;
  background:
    url('../img/Group_2136640490.png') top center / 100% 48px no-repeat,
    linear-gradient(180deg, rgba(3, 19, 54, 0.95) 0%, rgba(2, 12, 36, 0.95) 100%);
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
    background: url('../img/Rectangle_346242153.png') center / cover no-repeat;
    opacity: 0.25;
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
      color: #ffffff;
      border-color: rgba(83, 174, 255, 0.6);
      background: rgba(24, 92, 179, 0.6);
    }
  }
}

.filter-select {
  width: 180px;
}

.filter-select :deep(.n-base-selection) {
  min-height: 28px !important;
  height: 28px !important;
  background: rgba(16, 64, 126, 0.42) !important;
  border: 1px solid rgba(83, 174, 255, 0.32) !important;
  border-radius: 2px !important;
  box-sizing: border-box;
}

.filter-select :deep(.n-base-selection-label) {
  height: 26px !important;
  line-height: 26px !important;
}

.filter-select :deep(.n-base-selection-input),
.filter-select :deep(.n-base-selection-placeholder) {
  color: #d7e9ff !important;
  font-size: 12px !important;
}

.history-config-provider {
  display: flex;
  flex-direction: column;
  width: 100%;
  flex: 1;
  min-height: 0;
}

.filter-bar {
  display: flex;
  align-items: center;
  gap: 14px;
  flex-shrink: 0;
  min-height: 42px;
  padding: 10px 32px 0;
  box-sizing: border-box;
}

.filter-item {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.filter-label {
  color: #8bbce6;
  font-size: 13px;
  white-space: nowrap;
}

.filter-total {
  color: #8bbce6;
  font-size: 13px;
  white-space: nowrap;
  margin-left: auto;
}

.filter-date {
  width: 160px;
}

.filter-date :deep(.n-input) {
  min-height: 28px !important;
  height: 28px !important;
  background: rgba(16, 64, 126, 0.42) !important;
  border: 1px solid rgba(83, 174, 255, 0.32) !important;
  border-radius: 2px !important;
  box-sizing: border-box;
}

.filter-date :deep(.n-input-wrapper) {
  height: 26px !important;
  line-height: 26px !important;
}

.filter-date :deep(.n-input__input-el),
.filter-date :deep(.n-input__placeholder) {
  color: #d7e9ff !important;
  font-size: 12px !important;
}

.filter-date :deep(.n-input:hover),
.filter-date :deep(.n-input.n-input--focus) {
  background: rgba(16, 64, 126, 0.42) !important;
  border-color: rgba(83, 174, 255, 0.32) !important;
  box-shadow: none !important;
}

.modal-content {
  flex: 1;
  min-height: 0;
  display: flex;
  padding: 12px 32px 0;
  overflow: hidden;
}

.card-list {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding-bottom: 10px;
  scrollbar-width: none;
  -ms-overflow-style: none;

  &::-webkit-scrollbar {
    display: none;
  }
}

.warn-card {
  flex-shrink: 0;
  padding: 12px 16px;
  background: rgba(10, 50, 95, 0.6);
  border: 1px solid rgba(83, 174, 255, 0.2);
  border-radius: 4px;
  transition: background 0.2s, border-color 0.2s;

  &:hover {
    background: rgba(16, 64, 126, 0.6);
    border-color: rgba(83, 174, 255, 0.4);
  }

  &__title {
    color: #d7e9ff;
    font-size: 14px;
    font-weight: 500;
    line-height: 1.5;
    margin-bottom: 8px;
  }

  &__bottom {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  &__time {
    color: rgba(184, 217, 255, 0.7);
    font-size: 13px;
  }
}

.detail-btn {
  min-width: 56px;
  height: 26px;
  border: 1px solid rgba(83, 174, 255, 0.45);
  background: rgba(16, 64, 126, 0.45);
  color: #aed5ff;
  font-size: 13px;
  cursor: pointer;
  border-radius: 2px;
  display: flex;
  align-items: center;
  justify-content: center;

  &:hover {
    color: #ffffff;
    border-color: rgba(83, 174, 255, 0.75);
    background: rgba(24, 92, 179, 0.65);
  }
}

.empty-tip {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: rgba(184, 217, 255, 0.5);
  font-size: 14px;
}

.pagination-bar {
  flex-shrink: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 8px 32px 12px;
  box-sizing: border-box;
}

.pagination-bar :deep(.n-pagination) {
  --n-item-text-color: #b8d9ff;
  --n-item-text-hover-color: #ffffff;
  --n-item-text-active-color: #5feaff;
  --n-item-text-pressed-color: #5feaff;
  --n-button-icon-color: #b8d9ff;
  --n-button-icon-hover-color: #ffffff;
  --n-item-border: 1px solid rgba(83, 174, 255, 0.3);
  --n-item-border-hover: 1px solid rgba(83, 174, 255, 0.6);
  --n-item-border-active: 1px solid rgba(83, 174, 255, 0.6);
  --n-item-border-focus: 1px solid rgba(83, 174, 255, 0.6);
  --n-item-color: rgba(16, 64, 126, 0.42);
  --n-item-color-hover: rgba(24, 92, 179, 0.6);
  --n-item-color-active: rgba(24, 92, 179, 0.6);
  --n-item-color-focus: rgba(24, 92, 179, 0.6);
}

.warn-detail-popup {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 1002;
  width: 420px;
  background: linear-gradient(180deg, rgba(3, 19, 54, 0.98) 0%, rgba(2, 12, 36, 0.98) 100%);
  border: 1px solid rgba(83, 174, 255, 0.4);
  border-radius: 6px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.5);
  pointer-events: auto;

  &__header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 14px 18px;
    border-bottom: 1px solid rgba(83, 174, 255, 0.2);
  }

  &__title {
    color: #cfe8ff;
    font-size: 16px;
    font-weight: 600;
    text-shadow: 0 0 8px rgba(83, 174, 255, 0.4);
  }

  &__close {
    width: 26px;
    height: 26px;
    border: 1px solid rgba(83, 174, 255, 0.3);
    background: rgba(16, 64, 126, 0.4);
    color: #aed5ff;
    font-size: 18px;
    cursor: pointer;
    border-radius: 2px;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;

    &:hover {
      color: #ffffff;
      border-color: rgba(83, 174, 255, 0.6);
      background: rgba(24, 92, 179, 0.6);
    }
  }

  &__body {
    padding: 16px 18px;
  }

  &__time {
    color: rgba(184, 217, 255, 0.75);
    font-size: 13px;
    margin-bottom: 12px;
  }

  &__content {
    color: #d7e9ff;
    font-size: 14px;
    line-height: 1.8;
  }
}

.warn-detail-popup--image {
  width: 720px;
  height: 520px;
  display: flex;
  flex-direction: column;

  .warn-detail-popup__body {
    flex: 1;
    min-height: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;
    padding: 16px;
    box-sizing: border-box;
  }
}

.warning-image {
  display: block;
  max-width: 100%;
  max-height: 100%;
  width: auto;
  height: auto;
  object-fit: contain;
}

.image-empty {
  color: rgba(216, 236, 255, 0.72);
  font-size: 15px;
}
</style>
