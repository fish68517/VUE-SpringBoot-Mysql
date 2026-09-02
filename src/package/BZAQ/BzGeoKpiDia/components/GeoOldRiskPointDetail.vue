<template>
  <div class="old-risk-point-detail">
    <header class="detail-header">
      <span>{{ title }}</span>
      <button type="button" class="close-btn" aria-label="关闭" @click="$emit('close')">×</button>
    </header>

    <main class="detail-body">
      <div class="info-grid">
        <div
          v-for="row in detailRows"
          :key="row.key"
          class="info-row"
        >
          <span class="row-label">{{ row.label }}：</span>
          <span class="row-value">{{ row.value }}</span>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  detail?: Record<string, any>
  layerName?: string
}>()
defineEmits(['close'])

const riskTypeTextMap: Record<string, string> = {
  slope: '老边坡',
  retaining_wall: '老堡坎',
  wall: '老围墙',
  old_house: '老房屋',
  trail: '邻崖步道',
  other: '其他'
}

const fields = [
  { key: 'riskType', label: '风险点类型' },
  { key: 'seqNo', label: '序号' },
  { key: 'street', label: '街道' },
  { key: 'location', label: '所在地' },
  { key: 'longitude', label: '经度' },
  { key: 'latitude', label: '纬度' },
  { key: 'hazardDesc', label: '隐患描述' },
  { key: 'mappedFlagTime', label: '是否落图及落图时间' },
  { key: 'ownerUnit', label: '权属单位' },
  { key: 'ownerContact', label: '权属单位联系人及电话' },
  { key: 'supervisorDept', label: '行业监管部门' },
  { key: 'supervisorContact', label: '行业监管部门联系人及电话' },
  { key: 'streetContact', label: '属地街道联系人及电话' },
  { key: 'existingProblem', label: '存在问题' },
  { key: 'measureSuggestion', label: '措施建议' },
  { key: 'isGeoDisaster', label: '是否属于地灾' },
  { key: 'hazardLevel', label: '隐患分级' },
  { key: 'riskLevel', label: '风险等级' },
  { key: 'remark', label: '备注' }
]

const getValue = (key: string) => {
  const value = props.detail?.[key]
  if (key === 'riskType') return riskTypeTextMap[String(value || '')] || value || props.layerName || '-'
  if (value === undefined || value === null || value === '') return '-'
  return String(value)
}

const title = computed(() => {
  const typeName = getValue('riskType')
  const location = getValue('location')
  return location && location !== '-' ? `${typeName}详情 - ${location}` : `${typeName}详情`
})

const detailRows = computed(() => fields.map(field => ({
  ...field,
  value: getValue(field.key)
})))
</script>

<style scoped lang="scss">
.old-risk-point-detail {
  position: absolute;
  transform: translate(-50%, -50%);
  z-index: 1001;
  width: 1000px;
  height: 800px;
  color: #d9ecff;
  overflow: hidden;
  font-family: "Microsoft YaHei", sans-serif;
  background: url('../img/Rectangle_346242153.png') no-repeat center/100% 100%;
}

.detail-header {
  position: relative;
  height: 54px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 58px;
  color: #b9dcff;
  font-size: 22px;
  font-weight: 700;
  text-align: center;
  background: linear-gradient(90deg, transparent, rgba(24, 105, 190, .45), transparent);
  border-bottom: 1px solid rgba(50, 144, 234, .35);
  box-sizing: border-box;
}

.close-btn {
  position: absolute;
  right: 14px;
  top: 8px;
  border: 0;
  background: transparent;
  color: #b9dcff;
  font-size: 32px;
  line-height: 32px;
  cursor: pointer;
}

.detail-body {
  height: calc(100% - 54px);
  padding: 20px 22px;
  box-sizing: border-box;
  overflow: auto;
  scrollbar-color: #297fb8 rgba(5, 44, 82, .5);
  scrollbar-width: thin;
}

.detail-body::-webkit-scrollbar {
  width: 8px;
}

.detail-body::-webkit-scrollbar-track {
  background: rgba(5, 44, 82, .45);
  border-radius: 8px;
}

.detail-body::-webkit-scrollbar-thumb {
  background: linear-gradient(180deg, #39a8f2, #14639d);
  border-radius: 8px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
}

.info-row {
  min-height: 42px;
  display: grid;
  grid-template-columns: 160px minmax(0, 1fr);
  align-items: start;
  padding: 10px 12px;
  background: linear-gradient(90deg, rgba(16, 92, 166, .55), rgba(4, 49, 95, .65));
  border: 1px solid rgba(50, 144, 234, .45);
  box-sizing: border-box;
  font-size: 15px;
  line-height: 1.5;
}

.row-label {
  color: #7faed4;
}

.row-value {
  min-width: 0;
  color: #e6f7ff;
  word-break: break-all;
  white-space: pre-wrap;
}

@media (max-width: 1100px) {
  .old-risk-point-detail {
    width: 92vw;
    height: 82vh;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }
}
</style>
