<template>
  <n-space>
    <n-text>{{ label }}</n-text>
    <n-input type="text" :placeholder="placeholder" size="small" clearable v-model:value="val"></n-input>
  </n-space>
</template>

<script setup lang="ts">
defineProps<{
  label: string
  placeholder: string
  val: string
}>()
</script>

<style lang="scss" scoped>
.n-space {
  align-items: center;
}
.n-text {
  display: inline-block;
  min-width: 70px;
  font-size: 12px;
}
.n-input:not(.n-input--autosize) {
  width: 216px;
}
</style>
