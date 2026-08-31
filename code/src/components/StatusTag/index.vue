<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{ status: string | number | boolean; label?: string }>()

const value = computed(() => String(props.status))
const info = computed(() => {
  if (props.label) return { text: props.label, type: 'info' as const }
  if (['1', 'true', '启用', '正常', '成功', '已办结', '已发送', '已读', '已处置'].includes(value.value)) {
    return { text: typeof props.status === 'number' ? '启用' : value.value, type: 'success' as const }
  }
  if (['0', 'false', '停用', '失败', '未处置'].includes(value.value)) {
    return { text: typeof props.status === 'number' ? '停用' : value.value, type: 'info' as const }
  }
  if (['紧急', '高', '异常'].includes(value.value)) return { text: value.value, type: 'danger' as const }
  if (['待受理', '待发送', '待处理'].includes(value.value)) return { text: value.value, type: 'warning' as const }
  return { text: value.value, type: 'primary' as const }
})
</script>

<template><el-tag :type="info.type" effect="light" round>{{ info.text }}</el-tag></template>
