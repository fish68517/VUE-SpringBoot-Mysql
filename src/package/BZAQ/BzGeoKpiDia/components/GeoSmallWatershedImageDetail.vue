<template>
  <div class="small-watershed-image-modal">
    <div class="modal-header">
      <div class="header-title">小流域预警详情</div>
      <button class="close-btn" @click="handleClose">×</button>
    </div>
    <div class="image-content">
      <div v-if="imageLoading" class="image-empty">加载中...</div>
      <img v-else-if="imageUrl" class="warning-image" :src="imageUrl" alt="小流域预警图片" />
      <div v-else class="image-empty">暂无图片</div>
    </div>
  </div>
</template>

<script setup>
import { onBeforeUnmount, ref, watch } from 'vue'
import axios from 'axios'

const props = defineProps({
  detail: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['close'])

const BASE_URL = 'http://23.99.16.179:11001'
const imageUrl = ref('')
const imageLoading = ref(false)
let imageRequestId = 0

const revokeImageUrl = () => {
  if (imageUrl.value?.startsWith('blob:')) {
    URL.revokeObjectURL(imageUrl.value)
  }
  imageUrl.value = ''
}

const getSmallWatershedFileId = () => {
  return props.detail?.fileId || props.detail?.fileID || props.detail?.file_id || props.detail?.imgFileId || props.detail?.pictureFileId || ''
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
  const res = await axios.get(`${BASE_URL}/api/boot/system/land/previewFile`, {
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

const loadSmallWatershedImage = async () => {
  const currentRequestId = ++imageRequestId
  revokeImageUrl()

  const fileId = getSmallWatershedFileId()
  if (!fileId || fileId === '--') {
    imageLoading.value = false
    return
  }

  try {
    imageLoading.value = true
    const url = await requestPreviewFile(fileId)
    if (currentRequestId !== imageRequestId) {
      if (url?.startsWith('blob:')) URL.revokeObjectURL(url)
      return
    }
    imageUrl.value = url
  } catch (error) {
    console.error('获取小流域预警图片失败:', error)
    revokeImageUrl()
  } finally {
    if (currentRequestId === imageRequestId) {
      imageLoading.value = false
    }
  }
}

function handleClose() {
  emit('close')
}

watch(
  () => props.detail,
  loadSmallWatershedImage,
  { immediate: true }
)

onBeforeUnmount(() => {
  imageRequestId += 1
  revokeImageUrl()
})
</script>

<script>
export default {
  name: 'GeoSmallWatershedImageDetail'
}
</script>

<style lang="scss" scoped>
@font-face {
  font-family: 'AlibabaPuHuiTi';
  src: url('../font/Alibaba_PuHuiTi_2.0_65_Medium_65_Medium.ttf') format('truetype');
  font-weight: normal;
  font-style: normal;
}

.small-watershed-image-modal {
  font-family: 'AlibabaPuHuiTi', sans-serif;
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  z-index: 1002;
  width: 720px;
  height: 520px;
  display: flex;
  flex-direction: column;
  background:
    url('../img/Group_2136640490.png') top center / 100% 48px no-repeat,
    linear-gradient(180deg, rgba(3, 19, 54, 0.95) 0%, rgba(2, 12, 36, 0.95) 100%);
  border: 1px solid rgba(37, 134, 255, 0.25);
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.45);
  color: #b8d9ff;
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
}

.close-btn:hover {
  color: #ffffff;
  border-color: rgba(83, 174, 255, 0.6);
  background: rgba(24, 92, 179, 0.6);
}

.image-content {
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

.warning-image {
  display: block;
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.image-empty {
  color: rgba(216, 236, 255, 0.72);
  font-size: 15px;
}
</style>
