<template>
  <n-space>
    <n-text>{{ label }}</n-text>
    <div class="right">
      <div class="sqare">
        <n-color-picker :default-value="color" @complete="handleConfirm" />
      </div>
      <n-input type="text" placeholder="请输入系列名" size="small" v-model:value="color"></n-input>
    </div>
  </n-space>
</template>

<script setup lang="ts">
import { ref } from 'vue'
const props = defineProps<{
  label: string
  defaultColor: string
}>()

const color = ref<string>(props.defaultColor)

const emit = defineEmits<{ (e: 'changeColor', color: string): void }>()

const handleConfirm = (val: string) => {
  color.value = val
  emit('changeColor', val)
}
</script>

<style lang="scss" scoped>
.n-space {
  align-items: center;
}
.n-text {
  display: inline-block;
  min-width: 70px;
  font-size: 12px;
  color: #808792;
}
.n-color-picker {
  width: 28px;
  height: 28px;
}
:deep(.n-color-picker-trigger__value) {
  display: none;
}
.right {
  width: 216px;
  display: flex;
  align-items: center;
  .sqare {
    width: 28px;
    height: 28px;
  }
}
</style>
