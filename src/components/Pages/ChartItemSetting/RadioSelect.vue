<template>
  <n-space>
    <n-text>{{ label }}</n-text>
    <n-radio-group v-model:value="defaultSelector" name="radiogroup">
      <n-radio v-for="item in selectOptions" :key="item.value" :value="item.value">
        {{ item.label }}
      </n-radio>
    </n-radio-group>
  </n-space>
</template>

<script setup lang="ts">
interface Options {
  value: string
  label: string
}
defineProps<{
  label: string
  defaultSelector: string
  selectOptions: Options[]
}>()
</script>

<style lang="scss" scoped>
.n-space {
  align-items: flex-start;
}
.n-text {
  display: inline-block;
  width: 70px;
  font-size: 12px;
}
.n-radio-group {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
</style>
