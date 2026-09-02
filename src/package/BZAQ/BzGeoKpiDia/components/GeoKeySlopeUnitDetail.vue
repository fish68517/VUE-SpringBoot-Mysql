<template>
  <div class="key-slope-detail">
    <header class="detail-header">
      <span>{{ title }}</span>
      <button type="button" class="close-btn" aria-label="关闭" @click="$emit('close')">×</button>
    </header>

    <section class="detail-card">
      <dl>
        <div v-for="item in detailRows" :key="item.key">
          <dt>{{ item.label }}：</dt>
          <dd :title="item.value">{{ item.value }}</dd>
        </div>
      </dl>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{ detail?: Record<string, any> }>()
defineEmits(['close'])

const keySlopeFieldRows = [
  { key: 'fqdm', label: '分区代码', keys: ['fqdm', 'FQDM'] },
  { key: 'fxdj', label: '风险等级', keys: ['fxdj', 'FXDJ'] },
  { key: 'dlwz', label: '地理位置', keys: ['dlwz', 'DLWZ'] },
  { key: 'fqmj', label: '分区面积', keys: ['fqmj', 'FQMJ'] },
  { key: 'fxtz', label: '风险特征', keys: ['fxtz', 'FXTZ'] },
  { key: 'tybh', label: '统一编号', keys: ['tybh', 'TYBH'] },
  { key: 'geojsonId', label: 'GeoJSON属性ID', keys: ['geojsonId', 'geojsonid', 'GEOJSONID'] },
  { key: 'qx', label: '区县', keys: ['qx', 'QX'] },
  { key: 'xz', label: '乡镇/街道', keys: ['xz', 'XZ'] },
  { key: 'citycode', label: '城市代码', keys: ['citycode', 'cityCode', 'CITYCODE'] },
  { key: 'areacode', label: '区域代码', keys: ['areacode', 'areaCode', 'AREACODE'] }
]

const stringifyValue = (value: any) => {
  if (value === undefined || value === null || value === '') return '-'
  if (typeof value === 'object') return JSON.stringify(value)
  return String(value)
}

/** 按重点斜坡单元接口字段顺序获取详情值，并兼容大小写字段名。 */
const getFieldValue = (detail: Record<string, any>, keys: string[]) => {
  const matchedKey = keys.find(key => detail?.[key] !== undefined && detail?.[key] !== null && detail?.[key] !== '')
  return matchedKey ? detail[matchedKey] : ''
}

/** 将重点斜坡单元接口字段整理成固定两列详情行。 */
const detailRows = computed(() => {
  const detail = props.detail || {}
  return keySlopeFieldRows.map(item => ({
    key: item.key,
    label: item.label,
    value: stringifyValue(getFieldValue(detail, item.keys))
  }))
})

const title = computed(() => {
  const detail = props.detail || {}
  return stringifyValue(detail.dlwz || detail.DLWZ || detail.tybh || detail.TYBH || '重点斜坡单元详情')
})
</script>

<style scoped lang="scss">
.key-slope-detail {
  position: absolute;
  transform: translate(-50%, -50%);
  z-index: 1001;
  width: 1000px;
  height: 500px;
  box-sizing: border-box;
  overflow: hidden;
  color: #d8ebff;
  font-family: "Microsoft YaHei", sans-serif;
  background: url('../img/Rectangle_346242153.png') no-repeat center/cover;
  background-size: 100% 100%;
}

.detail-header {
  position: relative;
  height: 54px;
  box-sizing: border-box;
  display: flex;
  align-items: center;
  padding: 0 52px 0 24px;
  color: #b9dfff;
  font-size: 22px;
  font-weight: 700;
  border-bottom: 1px solid rgba(97, 173, 255, .28);
}

.close-btn {
  position: absolute;
  right: 14px;
  top: 8px;
  border: 0;
  background: transparent;
  color: #b9dcff;
  font-size: 30px;
  line-height: 30px;
  cursor: pointer;
}

.detail-card {
  position: relative;
  height: 398px;
  margin: 24px;
  padding: 18px 24px;
  overflow: auto;
  background: rgba(8, 49, 90, .78);
  border: 1px solid rgba(42, 125, 204, .33);
  box-sizing: border-box;
}

dl {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  column-gap: 36px;
  row-gap: 5px;
  margin: 0;
  font-size: 15px;
  line-height: 1.82;
}

dl div {
  display: flex;
  min-width: 0;
}

dt {
  flex: none;
  color: #80acd2;
}

dd {
  min-width: 0;
  margin: 0;
  color: #f1f8ff;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
