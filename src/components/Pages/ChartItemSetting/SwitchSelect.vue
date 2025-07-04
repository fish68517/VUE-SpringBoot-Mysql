<template>
  <n-space>
    <n-text>{{ label }}</n-text>
    <n-switch v-model:value="open" />
  </n-space>
</template>

<script setup lang="ts">
interface Props {
  label: string
  open?: boolean
}

withDefaults(defineProps<Props>(), {
  open: false
})
</script>

<style lang="scss" scoped>
.n-space {
  justify-content: space-between !important;
  align-items: center;
}
.n-text {
  font-size: 12px;
  color: #808792;
}
</style>
