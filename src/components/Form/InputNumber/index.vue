<template>
  <n-input-number v-model:value="_value" :min="min" :max="max" :step="step">
    <template #prefix v-if="prefix">
      <n-text depth="3">{{ prefixVal }}</n-text>
    </template>
    <template #suffix>
      <span class="unit">{{ suffixVal }}</span>
    </template>
    <template #minus-icon>
      <i class="iconfont icon-jianshao"></i>
    </template>
    <template #add-icon>
      <i class="iconfont icon-zengjia"></i>
    </template>
  </n-input-number>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  value: number
  min?: number
  max?: number
  step?: number
  prefix?: boolean
  prefixVal?: string
  elWidth?: string
  prefixMarginRight?: string
  suffixVal?: string
}

const props = withDefaults(defineProps<Props>(), {
  min: 0,
  max: 99999,
  step: 1,
  prefix: true,
  prefixVal: 'X',
  elWidth: '120px',
  prefixMarginRight: '20px',
  suffixVal: ''
})

const emit = defineEmits<{
  (e: 'update:value', val: number): void
}>()

const _value = computed({
  get: () => props.value,
  set: val => {
    emit('update:value', val)
  }
})
</script>

<style lang="scss" scoped>
@import '@/styles/pages/form.scss';

.n-input-number {
  width: v-bind('elWidth');
}
:deep(.n-input__prefix) {
  margin-right: v-bind('prefixMarginRight');
}
:deep(.n-input-number-suffix) {
  position: absolute;
  right: 8px;
  .unit {
    color: var(--n-label-text-color);
    display: inline-block;
  }
}
</style>
