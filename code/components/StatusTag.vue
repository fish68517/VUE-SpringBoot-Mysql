<template>
  <text :class="['status-tag', tone]">{{ label }}</text>
</template>
<script setup lang="ts">
import { computed } from 'vue'
import { dictionary } from '../repositories/seed'
const props = defineProps<{ value: string; kind?: string }>()
const label = computed(() => {
  const all =
    props.kind === 'alarm'
      ? dictionary.alarmStatuses
      : props.kind === 'order'
        ? dictionary.orderStatuses
        : props.kind === 'task'
          ? dictionary.taskStatuses
          : dictionary.levels
  return (all as Record<string, string>)[props.value] || props.value
})
const tone = computed(() =>
  ['urgent', 'important', 'normal'].includes(props.value)
    ? props.value
    : ['completed', 'closed'].includes(props.value)
      ? 'success'
      : ['processing', 'review'].includes(props.value)
        ? 'important'
        : 'normal',
)
</script>
