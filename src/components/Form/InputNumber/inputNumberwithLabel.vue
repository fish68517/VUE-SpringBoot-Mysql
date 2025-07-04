<!--
 * @Author: kaix
 * @Date: 2023-08-10 17:13:46
 * @LastEditTime: 2024-01-19 12:20:21
 * @LastEditors: zouying
 * @Description: 
-->
<template>
  <n-form label-placement="left">
    <n-form-item :label="label">
      <CustomInputNumber v-model:value="_value" :prefixVal="prefixVal" :prefix="prefixs" :suffixVal="suffix" :min="min" :max="max" :step="step" el-width="100%"/>
    </n-form-item>
  </n-form>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import { CustomInputNumber } from './index'
import { prefix } from 'naive-ui/es/_utils/cssr'

const props = defineProps({
  label: {
    type: String,
    default: ''
  },
  value: {
    type: [Number, Object]
  },
  min: {
    type: Number,
    default: 0
  },
  max: {
    type: Number,
    default: null
  },
  step: {
    type: Number,
    default: 1
  },
  prefixVal: {
    type: String,
    default: null
  },
  suffix: {
    type: String,
    default: null
  },
  prefixs: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits<{
  (e: 'update:value', val: number): void
}>()

const _value = computed({
  get: () => props.value ?  props.value as number : 0,
  set: val => {
    const v = val ? val : 0
    emit('update:value', Number(v))
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
  :deep(.n-input-number-suffix){
  display: flex;
  }
}
.n-slider {
  width: 112px;
}
</style>
