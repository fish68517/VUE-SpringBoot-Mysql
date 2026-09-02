<template>
  <div class="fire-risk-point-modal">
    <div class="modal-header">
      <div class="header-title">{{ title }}</div>
      <button class="close-btn" @click="handleClose">×</button>
    </div>
    <div class="modal-content">
      <n-data-table
        class="fire-table"
        :columns="columns"
        :data="tableData"
        :scroll-x="tableScrollX"
        flex-height
        :bordered="false"
        :single-line="false"
        size="small"
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

    <div v-if="previewVisible" class="image-preview-mask" @click="closeImagePreview">
      <div class="image-preview-modal" @click.stop>
        <button class="image-preview-close" @click="closeImagePreview">×</button>
        <div v-if="previewLoading" class="image-preview-empty">加载中...</div>
        <img v-else-if="previewImageUrl" class="image-preview-img" :src="previewImageUrl" alt="小流域图片预览" />
        <div v-else class="image-preview-empty">暂无图片</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { h, onBeforeUnmount, onMounted, ref } from 'vue'
import { NDataTable, NPagination } from 'naive-ui'
import axios from 'axios'
import { BASE_URL } from '../config'
const emit = defineEmits(['close'])

const title = '小流域'
const currentPage = ref(1)
const totalPage = ref(1)
const pageSize = 10
const tableData = ref([])
const previewVisible = ref(false)
const previewImageUrl = ref('')
const previewLoading = ref(false)
const previewLoadingFileId = ref('')
let previewRequestId = 0

const columns = [
  {
    title: '序号',
    key: 'index',
    width: 80,
    render(_row, rowIndex) {
      return h('span', {}, (currentPage.value - 1) * pageSize + rowIndex + 1)
    }
  },
  {
    title: '操作',
    key: 'actions',
    width: 140,
    render(row) {
      return h(
        'button',
        {
          class: 'detail-btn',
          disabled: previewLoading.value,
          onClick: () => handleViewDetail(row)
        },
        previewLoadingFileId.value === row?.fileId ? '加载中...' : '查看详情'
      )
    }
  },
  {
    title: '区县编码',
    key: 'city',
    width: 180,
    ellipsis: { tooltip: true }
  },
  {
    title: '创建时间',
    key: 'createdTs',
    width: 220,
    ellipsis: { tooltip: true }
  },
  {
    title: '最后修改时间',
    key: 'lastModifiedTs',
    width: 220,
    ellipsis: { tooltip: true }
  },
]

// 表格固定横向滚动宽度，列多时通过横向滚动完整展示。
const tableScrollX = 1600

const getQueryParams = () => ({
  Index: currentPage.value,
  size: pageSize
})

const getResponsePage = data => {
  const pageData = data?.data || {}
  const list = pageData.content || pageData.records || []
  const total = Number(pageData.page?.total ?? pageData.total ?? list.length)

  return {
    records: Array.isArray(list) ? list : [],
    pages: Math.max(1, Math.ceil(total / pageSize))
  }
}

const requestSmallWatershedList = async () => {
  const api = '/api/boot/system/land/landRainXlyImagePage'
  const res = await axios.get(`${BASE_URL}${api}`, {
    params: getQueryParams()
  })
  return getResponsePage(res?.data)
}

const revokePreviewImageUrl = () => {
  if (previewImageUrl.value) {
    if (previewImageUrl.value.startsWith('blob:')) {
      URL.revokeObjectURL(previewImageUrl.value)
    }
    previewImageUrl.value = ''
  }
}

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

const requestPreviewFile = async fileId => {
  const api = '/api/boot/system/land/previewFile'
  const res = await axios.get(`${BASE_URL}${api}`, {
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

const getSmallWatershedList = async () => {
  try {
    const pageData = await requestSmallWatershedList()
    totalPage.value = pageData.pages || 1
    tableData.value = pageData.records
  } catch (error) {
    console.error('获取小流域列表失败:', error)
    tableData.value = []
    totalPage.value = 1
  }
}

function handleClose() {
  emit('close')
}

function handlePageChange(page) {
  currentPage.value = page
  getSmallWatershedList()
}

async function handleViewDetail(row) {
  revokePreviewImageUrl()
  const requestId = ++previewRequestId
  previewVisible.value = true

  if (!row?.fileId) {
    return
  }

  try {
    previewLoading.value = true
    previewLoadingFileId.value = row.fileId
    const imageUrl = await requestPreviewFile(row.fileId)
    if (requestId !== previewRequestId) {
      if (imageUrl?.startsWith('blob:')) URL.revokeObjectURL(imageUrl)
      return
    }
    previewImageUrl.value = imageUrl
  } catch (error) {
    console.error('获取小流域图片预览失败:', error)
    revokePreviewImageUrl()
  } finally {
    if (requestId === previewRequestId) {
      previewLoading.value = false
      previewLoadingFileId.value = ''
    }
  }
}

function closeImagePreview() {
  previewRequestId += 1
  previewVisible.value = false
  previewLoading.value = false
  previewLoadingFileId.value = ''
  revokePreviewImageUrl()
}

onMounted(() => {
  getSmallWatershedList()
})

onBeforeUnmount(() => {
  revokePreviewImageUrl()
})
</script>

<script>
export default {
  name: 'GeoSmallWatershedList'
}
</script>

<style lang="scss" scoped>
@import './geo-table-scroll.scss';
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

.fire-risk-point-modal {
  font-family: 'AlibabaPuHuiTi', sans-serif;
  position: absolute;
  transform: translate(-50%, -50%);
  z-index: 1001;
  width: 1200px;
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

.modal-content {
  flex: 1;
  min-height: 0;
  display: flex;
  padding: 12px 32px 8px;
  overflow: hidden;
}

:deep(.fire-table) {
  flex: 1;
  height: 100%;
  min-height: 0;

  .n-data-table-wrapper {
    background: transparent;
  }

  .n-data-table-table {
    background: transparent;
    font-size: 14px;
  }

  .n-data-table-th {
    height: 40px;
    padding: 0 12px;
    color: #8bbce6;
    font-weight: 500;
    background: #0b437c;
    border: none;
  }

  .n-data-table-td {
    height: 40px;
    padding: 0 12px;
    color: #d7e9ff;
    font-family: 'D-DIN', 'AlibabaPuHuiTi', sans-serif;
    background: #0a325f;
    border: none;
  }

  .n-data-table-tr:nth-child(even) .n-data-table-td {
    background: #19406b;
  }

  .n-data-table-tr:hover .n-data-table-td {
    background: rgba(29, 107, 202, 0.3);
  }
}

:deep(.detail-btn) {
  min-width: 72px;
  height: 26px;
  border: 1px solid rgba(83, 174, 255, 0.45);
  background: rgba(16, 64, 126, 0.45);
  color: #aed5ff;
  font-size: 13px;
  cursor: pointer;

  &:hover {
    color: #ffffff;
    border-color: rgba(83, 174, 255, 0.75);
    background: rgba(24, 92, 179, 0.65);
  }

  &:disabled {
    cursor: not-allowed;
    opacity: 0.72;
  }
}

.image-preview-mask {
  position: fixed;
  inset: 0;
  z-index: 2000;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.72);
  pointer-events: auto;
}

.image-preview-modal {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  padding: 40px;
  background: rgba(3, 19, 54, 0.96);
  border: 1px solid rgba(83, 174, 255, 0.45);
  box-shadow: 0 16px 54px rgba(0, 0, 0, 0.55);
}

.image-preview-close {
  position: absolute;
  right: 10px;
  top: 10px;
  width: 32px;
  height: 32px;
  border: 1px solid rgba(83, 174, 255, 0.55);
  background: rgba(16, 64, 126, 0.9);
  color: #ffffff;
  font-size: 20px;
  cursor: pointer;
}

.image-preview-img {
  display: block;
  width: calc(100% - 28px);
  height: calc(100% - 28px);
  object-fit: contain;
}

.image-preview-empty {
  width: 420px;
  height: 240px;
  margin:0 auto;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #aed5ff;
  font-size: 16px;
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

  :deep(.n-pagination-item),
  :deep(.n-pagination-button) {
    min-width: 32px;
    height: 32px;
    border: 1px solid rgba(83, 174, 255, 0.35);
    background: rgba(16, 64, 126, 0.4);
    color: #aed5ff;
    font-size: 14px;
    border-radius: 2px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
  }

  :deep(.n-pagination-item:hover),
  :deep(.n-pagination-button:hover) {
    color: #ffffff;
    border-color: rgba(83, 174, 255, 0.6);
    background: rgba(24, 92, 179, 0.6);
  }

  :deep(.n-pagination-item--active) {
    background: rgba(37, 134, 255, 0.8) !important;
    border-color: rgba(37, 134, 255, 0.9) !important;
    color: #ffffff !important;
  }
}
</style>
