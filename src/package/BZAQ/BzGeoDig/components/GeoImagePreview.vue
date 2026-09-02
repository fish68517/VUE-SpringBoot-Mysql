<template>
  <div class="geo-image-preview">
    <div class="preview-header">
      <div class="preview-title">{{ title || '图片预览' }}</div>
      <button class="close-btn" type="button" @click="handleClose">x</button>
    </div>
    <div v-if="label" class="preview-label">{{ label }}</div>
    <div class="preview-body">
      <div v-if="loading" class="preview-empty">加载中...</div>
      <img v-else-if="imageUrl" class="preview-image" :src="imageUrl" alt="图片预览" />
      <div v-else class="preview-empty">暂无图片</div>
    </div>
  </div>
</template>

<script setup>
import { onBeforeUnmount, ref, watch } from 'vue'
import axios from 'axios'
import { BASE_URL } from '../config'

const props = defineProps({
  fileId: {
    type: [String, Number],
    default: ''
  },
  title: {
    type: String,
    default: ''
  },
  label: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['close'])

const imageUrl = ref('')
const loading = ref(false)
let requestId = 0

const revokeImageUrl = () => {
  if (imageUrl.value?.startsWith('blob:')) {
    URL.revokeObjectURL(imageUrl.value)
  }
  imageUrl.value = ''
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

const loadPreviewImage = async fileId => {
  const currentRequestId = ++requestId
  revokeImageUrl()

  if (!fileId) {
    loading.value = false
    return
  }

  try {
    loading.value = true
    const url = await requestPreviewFile(fileId)
    if (currentRequestId !== requestId) {
      if (url?.startsWith('blob:')) URL.revokeObjectURL(url)
      return
    }
    imageUrl.value = url
  } catch (error) {
    console.error('获取图片预览失败:', error)
    revokeImageUrl()
  } finally {
    if (currentRequestId === requestId) {
      loading.value = false
    }
  }
}

function handleClose() {
  emit('close')
}

watch(
  () => props.fileId,
  value => {
    loadPreviewImage(value)
  },
  { immediate: true }
)

onBeforeUnmount(() => {
  requestId += 1
  revokeImageUrl()
})
</script>

<script>
export default {
  name: 'GeoImagePreview'
}
</script>

<style lang="scss" scoped>
@font-face {
  font-family: 'AlibabaPuHuiTi';
  src: url('../font/Alibaba_PuHuiTi_2.0_55_Regular_55_Regular.ttf') format('truetype');
  font-weight: normal;
  font-style: normal;
}

.geo-image-preview {
  font-family: 'AlibabaPuHuiTi', sans-serif;
  position: absolute;
  transform: translate(-50%, -50%);
  z-index: 1001;
  width: 720px;
  height: 520px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  color: #d8ecff;
  background:
    url('../img/Group_2136640490.png') top center / 100% 48px no-repeat,
    linear-gradient(180deg, rgba(3, 19, 54, 0.96) 0%, rgba(2, 12, 36, 0.96) 100%);
  border: 1px solid rgba(37, 134, 255, 0.28);
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.45);
  pointer-events: auto;

  &::before {
    content: '';
    position: absolute;
    inset: 0;
    z-index: -1;
    background: url('../img/Rectangle_346242153.png') center / cover no-repeat;
    opacity: 0.24;
  }
}

.preview-header {
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 18px 0 24px;
}

.preview-title {
  max-width: 600px;
  overflow: hidden;
  font-size: 18px;
  font-weight: 600;
  color: #ffffff;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.close-btn {
  width: 28px;
  height: 28px;
  border: 0;
  background: transparent;
  color: #9cc9ff;
  cursor: pointer;
  font-size: 20px;
  line-height: 28px;

  &:hover {
    color: #ffffff;
  }
}

.preview-label {
  align-self: flex-start;
  margin: 12px 24px 0;
  padding: 2px 12px;
  color: #ffdd8a;
  font-size: 13px;
  line-height: 24px;
  background: rgba(255, 184, 61, 0.12);
  border: 1px solid rgba(255, 184, 61, 0.35);
}

.preview-body {
  flex: 1;
  min-height: 0;
  margin: 16px 24px 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: auto;
  background: rgba(0, 21, 54, 0.36);
  border: 1px solid rgba(88, 162, 255, 0.22);
}

.preview-image {
  display: block;
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.preview-empty {
  color: rgba(216, 236, 255, 0.72);
  font-size: 15px;
}
</style>
