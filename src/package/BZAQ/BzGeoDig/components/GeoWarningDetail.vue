<template>
  <div
    class="fire-risk-point-modal"
    :class="{ 'fire-risk-point-modal--image': isSmallWatershedDetail }"
  >
    <div class="modal-header">
      <div class="header-title">{{ title }}</div>
      <button class="close-btn" @click="handleClose">×</button>
    </div>
    <div
      v-if="isSmallWatershedDetail"
      class="image-content"
    >
      <div v-if="imageLoading" class="image-empty">加载中...</div>
      <img v-else-if="imageUrl" class="warning-image" :src="imageUrl" alt="小流域预警图片" />
      <div v-else class="image-empty">暂无图片</div>
    </div>
    <div v-else-if="isLabelValueDetail" class="modal-content">
      <div class="device-alarm-detail">
        <div
          v-for="item in labelValueRows"
          :key="item.key"
          class="device-alarm-detail__item"
        >
          <span class="device-alarm-detail__label">{{ item.label }}：</span>
          <span
            class="device-alarm-detail__value"
            :class="item.className"
          >
            {{ item.value }}
          </span>
        </div>
      </div>
    </div>
    <div v-else class="modal-content">
      <n-data-table
        class="fire-table"
        :columns="columns"
        :data="tableData"
        :scroll-x="scrollX"
        flex-height
        :bordered="false"
        :single-line="false"
        size="small"
      />
    </div>
  </div>
</template>

<script setup>
import { computed, h, onBeforeUnmount, ref, watch } from 'vue'
import { NDataTable } from 'naive-ui'
import axios from 'axios'
import { BASE_URL } from '../config'

const props = defineProps({
  detailType: {
    type: String,
    default: ''
  },
  detail: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['close'])

const warnLevelMap = {
  1: { label: '蓝色预警', className: 'warn-level--blue' },
  2: { label: '黄色预警', className: 'warn-level--yellow' },
  3: { label: '橙色预警', className: 'warn-level--orange' },
  4: { label: '红色预警', className: 'warn-level--red' }
}

const yesNoMap = {
  0: '否',
  1: '是'
}

const warnTypeMap = {
  0: '实时',
  1: '长期'
}

const statisticTypeMap = {
  0: '按处统计',
  1: '按点统计'
}

const getValue = key => {
  const value = props.detail?.[key]
  return value !== undefined && value !== null && value !== '' ? value : '--'
}

const getMappedValue = (map, key) => {
  const value = props.detail?.[key]
  return map[Number(value)] || getValue(key)
}

const renderWarnLevel = row => {
  const warnLevel = warnLevelMap[Number(row.warnLevel)]
  if (!warnLevel) return getValue('warnLevel')
  return h('span', { class: ['warn-level', warnLevel.className] }, warnLevel.label)
}

const title = computed(() => {
  const titleMap = {
    smallWatershed: '流域预警详情',
    geological: '今日地灾预警详情',
    deviceAlarm: '今日设备告警详情'
  }
  return titleMap[props.detailType] || '预警告警详情'
})

const isSmallWatershedDetail = computed(() => props.detailType === 'smallWatershed')
const isDeviceAlarmDetail = computed(() => props.detailType === 'deviceAlarm')
const isGeologicalDetail = computed(() => props.detailType === 'geological')
const isLabelValueDetail = computed(() => isDeviceAlarmDetail.value || isGeologicalDetail.value)
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

/** 使用小流域图片 id 查询文件流并转换为可展示的图片地址。 */
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

/** 加载小流域详情图片；非小流域详情不触发图片请求。 */
const loadSmallWatershedImage = async () => {
  const currentRequestId = ++imageRequestId
  revokeImageUrl()

  if (!isSmallWatershedDetail.value) {
    imageLoading.value = false
    return
  }

  const fileId = getSmallWatershedFileId()
  if (!fileId) {
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

const smallWatershedColumns = [
  { title: '时间', key: 'time', width: 180 },
  { title: '区县编码', key: 'city', width: 180 },
  { title: '图片ID', key: 'fileId', width: 260 }
]

const geologicalColumns = [
  { title: '时间', key: 'time', width: 160 },
  { title: '隐患点名称', key: 'disasterName', width: 180 },
  { title: '项目名称', key: 'projectName', width: 180 },
  {
    title: '预警类型',
    key: 'isLong',
    width: 100,
    render() {
      return getMappedValue(warnTypeMap, 'isLong')
    }
  },
  {
    title: '统计类型',
    key: 'isSpot',
    width: 110,
    render() {
      return getMappedValue(statisticTypeMap, 'isSpot')
    }
  },
  { title: '预警等级', key: 'warnLevel', width: 110, render: renderWarnLevel },
  { title: '创建时间', key: 'createdTs', width: 160 },
  { title: '最后修改时间', key: 'lastModifiedTs', width: 160 }
]

const deviceAlarmColumns = [
  { title: '时间', key: 'time', width: 160 },
  { title: '隐患点编码', key: 'unifiedcode', width: 140 },
  { title: '隐患点名称', key: 'disasterName', width: 180 },
  { title: '项目名称', key: 'projectName', width: 180 },
  { title: '区县编码', key: 'city', width: 120 },
  { title: '乡镇编码', key: 'town', width: 120 },
  {
    title: '是否为库区查询',
    key: 'isKq',
    width: 140,
    render() {
      return getMappedValue(yesNoMap, 'isKq')
    }
  },
  { title: '预警等级', key: 'warnLevel', width: 110, render: renderWarnLevel }
]

const getWarnLevelText = () => {
  return warnLevelMap[Number(props.detail?.warnLevel)]?.label || getValue('warnLevel')
}

const getWarnLevelClassName = () => {
  return warnLevelMap[Number(props.detail?.warnLevel)]?.className || ''
}

const deviceAlarmDetailRows = computed(() => [
  { label: '时间', key: 'time', value: getValue('time') },
  { label: '隐患点编码', key: 'unifiedcode', value: getValue('unifiedcode') },
  { label: '隐患点名称', key: 'disasterName', value: getValue('disasterName') },
  { label: '项目名称', key: 'projectName', value: getValue('projectName') },
  { label: '设备类型', key: 'deviceType', value: getValue('deviceType') },
  { label: '设备编号', key: 'deviceId', value: getValue('deviceId') },
  { label: '区县编码', key: 'city', value: getValue('city') },
  { label: '乡镇编码', key: 'town', value: getValue('town') },
  { label: '是否为库区查询', key: 'isKq', value: getMappedValue(yesNoMap, 'isKq') },
  {
    label: '预警等级',
    key: 'warnLevel',
    value: getWarnLevelText(),
    className: ['warn-level', getWarnLevelClassName()]
  },
  { label: '告警描述', key: 'msg', value: getValue('msg') }
])

const geologicalDetailRows = computed(() => [
  { label: '时间', key: 'time', value: getValue('time') },
  { label: '隐患点名称', key: 'disasterName', value: getValue('disasterName') },
  { label: '项目名称', key: 'projectName', value: getValue('projectName') },
  { label: '预警类型', key: 'isLong', value: getMappedValue(warnTypeMap, 'isLong') },
  { label: '统计类型', key: 'isSpot', value: getMappedValue(statisticTypeMap, 'isSpot') },
  {
    label: '预警等级',
    key: 'warnLevel',
    value: getWarnLevelText(),
    className: ['warn-level', getWarnLevelClassName()]
  },
  { label: '创建时间', key: 'createdTs', value: getValue('createdTs') },
  { label: '最后修改时间', key: 'lastModifiedTs', value: getValue('lastModifiedTs') }
])

const labelValueRows = computed(() => {
  if (isDeviceAlarmDetail.value) return deviceAlarmDetailRows.value
  if (isGeologicalDetail.value) return geologicalDetailRows.value
  return []
})

const columns = computed(() => {
  if (props.detailType === 'smallWatershed') return smallWatershedColumns
  if (props.detailType === 'deviceAlarm') return deviceAlarmColumns
  return geologicalColumns
})

// 表格固定横向滚动宽度，列多时通过横向滚动完整展示。
const scrollX = 1800

const tableData = computed(() => [{ ...props.detail, key: 'warning-detail' }])

function handleClose() {
  emit('close')
}

watch(
  () => [props.detailType, props.detail],
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
  name: 'GeoWarningDetail'
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
  width: 1300px;
  height: 540px;
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

.fire-risk-point-modal--image {
  width: 720px;
  height: 520px;
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

.device-alarm-detail {
  flex: 1;
  min-height: 0;
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  align-content: flex-start;
  column-gap: 48px;
  row-gap: 10px;
  padding: 14px 18px;
  overflow: auto;
  background: rgba(7, 50, 96, 0.52);
  border: 1px solid rgba(88, 162, 255, 0.16);
}

.device-alarm-detail__item {
  display: flex;
  align-items: flex-start;
  min-width: 0;
  color: #d7e9ff;
  font-size: 14px;
  line-height: 1.82;
}

.device-alarm-detail__label {
  flex: none;
  color: #80acd2;
}

.device-alarm-detail__value {
  min-width: 0;
  color: #e2f1ff;
  font-weight: 600;
  white-space: normal;
  overflow: visible;
  text-overflow: clip;
  overflow-wrap: anywhere;
  word-break: break-all;
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

  .n-data-table-tr:hover .n-data-table-td {
    background: rgba(29, 107, 202, 0.3);
  }
}

:deep(.warn-level) {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 72px;
  height: 24px;
  box-sizing: border-box;
  padding: 0 8px;
  border: 1px solid currentColor;
  border-radius: 2px;
  font-family: 'AlibabaPuHuiTi', sans-serif;
  font-size: 13px;
  font-weight: 500;
}

:deep(.warn-level--blue) {
  color: #35a8ff;
  background: rgba(53, 168, 255, 0.14);
}

:deep(.warn-level--yellow) {
  color: #ffd84d;
  background: rgba(255, 216, 77, 0.14);
}

:deep(.warn-level--orange) {
  color: #ff9f43;
  background: rgba(255, 159, 67, 0.14);
}

:deep(.warn-level--red) {
  color: #ff5b6b;
  background: rgba(255, 91, 107, 0.14);
}
</style>
