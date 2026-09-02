<template>
  <div class="pipeline-field-detail" :style="$attrs.style">
    <div class="modal-header">
      <div class="header-title">{{ detailTitle }}</div>
      <button class="close-btn" type="button" @click="$emit('close')">×</button>
    </div>

    <div class="modal-body">
      <div class="field-grid">
        <div v-for="item in fieldItems" :key="item.key" class="field-item">
          <div class="field-label">{{ item.label }}</div>
          <div class="field-value">{{ item.value }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

defineEmits(['close'])

const props = defineProps({
  point: {
    type: Object,
    default: () => ({})
  },
  title: {
    type: String,
    default: '管线详情'
  }
})

const labelMap: Record<string, string> = {
  id: '设备ID',
  dvcode: '设备编码',
  dvname: '设备名称',
  dvjxcode: '设备机型编码',
  modelname: '型号名称',
  enfullname: '安装单位全称',
  mountimage: '安装图片',
  listImage: '列表展示图片',
  mountAddress: '安装地址',
  lon: '经度',
  lat: '纬度',
  street: '街道名称',
  handleNum: '处理次数',
  gxsj: '更新时间',
  isWarning: '是否预警',
  layerName: '图层名称',
  deviceCode: '设备编码',
  deviceId: '设备ID',
  deviceName: '设备名称',
  deviceAddress: '设备地址',
  installAddress: '安装地址',
  longitude: '经度',
  latitude: '纬度',
  detailType: '详情类型'
}

const hiddenKeys = new Set(['key', '_raw','mountimage','listImage'])

function formatValue(value: any) {
  if (value === null || value === undefined || value === '') return '--'
  if (Array.isArray(value)) return value.length ? value.join('，') : '--'
  if (typeof value === 'object') {
    try {
      return JSON.stringify(value)
    } catch (error) {
      return String(value)
    }
  }
  if (typeof value === 'boolean') return value ? '是' : '否'
  return String(value)
}

function toLabel(key: string) {
  return labelMap[key] || key
}

const sourceData = computed(() => props.point?._raw || props.point || {})

const detailTitle = computed(() => {
  const point: any = props.point || {}
  const raw: any = props.point?._raw || {}
  return props.title || point.deviceName || raw.dvname || raw.deviceName || '管线详情'
})

const fieldItems = computed(() => {
  const source: any = sourceData.value
  return Object.keys(source)
    .filter(key => !hiddenKeys.has(key))
    .map(key => ({
      key,
      label: toLabel(key),
      value: formatValue(source[key])
    }))
})
</script>

<script lang="ts">
export default {
  name: 'PipelineFieldDetail'
}
</script>

<style lang="scss" scoped>
.pipeline-field-detail {
  position: absolute;
  transform: translate(-50%, -50%);
  z-index: 1001;
  width: 960px;
  height: 600px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  pointer-events: auto;
  color: #d8efff;
  background: linear-gradient(180deg, rgba(6, 36, 76, 0.98) 0%, rgba(2, 12, 36, 0.98) 100%);
  border: 1px solid rgba(37, 134, 255, 0.35);
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.48);
  font-family: 'Alibaba PuHuiTi 2.0', 'Microsoft YaHei', sans-serif;
}

.pipeline-field-detail::before {
  content: '';
  position: absolute;
  inset: 0;
  z-index: -1;
  background:
    linear-gradient(90deg, rgba(83, 174, 255, 0.08), transparent 26%, transparent 74%, rgba(83, 174, 255, 0.08)),
    radial-gradient(circle at 50% 0%, rgba(83, 174, 255, 0.18), transparent 36%);
}

.modal-header {
  height: 48px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  padding: 0 48px;
  background: linear-gradient(90deg, rgba(12, 73, 138, 0.55), rgba(4, 34, 76, 0.2), rgba(12, 73, 138, 0.55));
  border-bottom: 1px solid rgba(83, 174, 255, 0.22);
}

.header-title {
  color: #dff7ff;
  font-size: 20px;
  font-weight: 700;
  letter-spacing: 1px;
  text-shadow: 0 0 8px rgba(83, 214, 255, 0.8);
}

.close-btn {
  position: absolute;
  right: 18px;
  top: 10px;
  width: 28px;
  height: 28px;
  border: none;
  background: transparent;
  color: #dff7ff;
  font-size: 28px;
  line-height: 28px;
  cursor: pointer;
}

.modal-body {
  flex: 1;
  min-height: 0;
  padding: 18px 22px;
  overflow-y: auto;
  overflow-x: hidden;
  scrollbar-width: thin;
  scrollbar-color: rgba(61, 190, 255, 0.72) rgba(5, 42, 84, 0.62);
}

.modal-body::-webkit-scrollbar {
  width: 6px;
}

.modal-body::-webkit-scrollbar-track {
  background: rgba(5, 42, 84, 0.62);
}

.modal-body::-webkit-scrollbar-thumb {
  border-radius: 6px;
  background: linear-gradient(180deg, rgba(49, 190, 255, 0.9), rgba(27, 116, 217, 0.72));
}

.field-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px 12px;
}

.field-item {
  min-width: 0;
  display: grid;
  grid-template-columns: 138px minmax(0, 1fr);
  min-height: 40px;
  border: 1px solid rgba(56, 151, 255, 0.34);
  background: rgba(6, 53, 104, 0.58);
}

.field-label {
  display: flex;
  align-items: center;
  padding: 8px 10px;
  color: #90caff;
  font-size: 14px;
  font-weight: 700;
  background: rgba(7, 70, 130, 0.64);
  border-right: 1px solid rgba(56, 151, 255, 0.34);
}

.field-value {
  min-width: 0;
  display: flex;
  align-items: center;
  padding: 8px 10px;
  color: #e8f7ff;
  font-size: 14px;
  line-height: 20px;
  word-break: break-all;
  white-space: normal;
}
</style>
