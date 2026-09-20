<template>
  <!-- #ifdef H5 -->
  <select class="select-field native-select" :value="modelValue" @change="changeNative">
    <option v-for="option in options" :key="option.value" :value="option.value">{{ option.label }}</option>
  </select>
  <!-- #endif -->
  <!-- #ifndef H5 -->
  <picker :range="options" range-key="label" :value="index" @change="change">
    <view class="select-field">
      {{ options[index]?.label || placeholder }}
      <text class="select-arrow">⌄</text>
    </view>
  </picker>
  <!-- #endif -->
</template>
<script setup lang="ts">
import { computed } from 'vue'
const props = defineProps<{
  modelValue: string
  options: { value: string; label: string }[]
  placeholder?: string
}>()
const emit = defineEmits(['update:modelValue'])
const index = computed(() =>
  Math.max(
    0,
    props.options.findIndex((v) => v.value === props.modelValue),
  ),
)
function change(e: any) {
  emit('update:modelValue', props.options[Number(e.detail.value)].value)
}
function changeNative(e: Event) {
  emit('update:modelValue', (e.target as HTMLSelectElement).value)
}
</script>
<style scoped>
.native-select {
  font: inherit;
  color: inherit;
  max-width: 100%;
  min-width: 0;
  background: #fff;
  cursor: pointer;
  box-sizing: border-box;
}
.native-select option {
  color: #243247;
}
</style>
