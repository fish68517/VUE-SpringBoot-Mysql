<template>
    <n-form label-placement="left">
      <n-form-item :label="label">
        <n-checkbox-group v-model:value="_value" name="checkboxgroup">
          <n-checkbox v-for="item in options" :key="item.value" :value="item.value">
            {{ item.label }}
          </n-checkbox>
        </n-checkbox-group>
      </n-form-item>
    </n-form>
  </template>
  
  <script setup lang="ts">
  import { computed } from 'vue'
  import type { OptionsInter } from './index.d'
  
  interface Props {
    label: string
    value: string
    options: OptionsInter[]
  }
  
  const props = withDefaults(defineProps<Props>(), {})
  
  const emit = defineEmits<{
    (e: 'update:value', value: string): void
  }>()
  
  const _value = computed({
    get: () => props.value,
    set: newVal => {
      emit('update:value', newVal)
    }
  })
  </script>
  
  <style lang="scss" scoped>
  @import '@/styles/pages/form.scss';
  .n-checkbox-group {
    display: flex;
    gap: 12px 0;
    flex-wrap: wrap;
  }
  </style>
  