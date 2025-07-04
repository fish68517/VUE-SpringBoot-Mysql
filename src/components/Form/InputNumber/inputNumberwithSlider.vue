<!--
 * @Author: wangcong
 * @Date: 2023-04-19 10:49:13
 * @LastEditTime: 2024-10-18 09:26:44
 * @LastEditors: wangcong
 * @Description: 
-->
<template>
  <n-form label-placement="left">
    <n-form-item :label="label">
      <n-slider v-model:value="_value" :min="min" :max="max" :step="step" />
      <n-input-number v-model:value="_value" :min="min" :max="max" :step="step">
        <template #minus-icon>
          <i class="iconfont icon-jianshao"></i>
        </template>
        <template #add-icon>
          <i class="iconfont icon-zengjia"></i>
        </template>
      </n-input-number>
    </n-form-item>
  </n-form>
</template>

<script setup lang="ts">
import isNil from "lodash/isNil";
import { computed } from 'vue'

interface Props {
  label?: string
  value: number
  min?: number
  max?: number
  step?: number
}

const props = withDefaults(defineProps<Props>(), {
  min: 0,
  max: 99999,
  step: 1
})

const emit = defineEmits<{
  (e: 'update:value', value: number): void
}>()

const _value = computed({
  get: () => props.value,
  set: value => {
    if(isNil(value)) _value.value = 0
    else emit('update:value', value)
  }
})
</script>

<style lang="scss" scoped>
@import '@/styles/pages/form.scss';
.n-form-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  :deep(.n-form-item-label) {
    width: 62px;
  }
}
.n-slider {
  width: 112px;
}
.n-input-number {
  width: 70px;
}
</style>
