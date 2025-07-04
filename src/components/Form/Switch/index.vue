<!--
 * @Author: wangcong
 * @Date: 2023-04-19 10:49:13
 * @LastEditTime: 2024-06-27 16:26:48
 * @LastEditors: chenlong2
 * @Description: 
-->
<template>
  <n-form label-placement="left" label-width="auto">
    <n-form-item :label="label">
      <n-switch v-model:value="_value" />
    </n-form-item>
  </n-form>
</template>

<script setup lang="ts">
import { computed } from 'vue'
interface Props {
  label: string
  labelColor?: string
  value: boolean
  elMarginBottom?: string
}

const props = withDefaults(defineProps<Props>(), {
  labelColor: '#aaaaaa',
  elMarginBottom: '16px'
})

const emit = defineEmits<{
  (e: 'update:value', value: boolean): void
}>()

const _value = computed({
  get: () => props.value,
  set: value => {
    emit('update:value', value)
  }
})
</script>

<style lang="scss" scoped>
@import '@/styles/pages/form.scss';
.n-form-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: v-bind('elMarginBottom');
  :deep(.n-form-item-label) {
  }
  :deep(.n-form-item-blank) {
    width: auto;
  }
  :deep(.n-form-item-feedback-wrapper) {
    display: none;
  }
  :deep(.n-switch__rail){
    height: 20px;
  }
}
</style>
