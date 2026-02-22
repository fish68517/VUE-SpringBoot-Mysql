<template>
  <el-button
    :type="type"
    :size="size"
    :disabled="disabled"
    :loading="loading"
    @click="$emit('click')"
  >
    <slot></slot>
  </el-button>
</template>

<script setup>
defineProps({
  type: {
    type: String,
    default: 'primary',
  },
  size: {
    type: String,
    default: 'default',
  },
  disabled: {
    type: Boolean,
    default: false,
  },
  loading: {
    type: Boolean,
    default: false,
  },
})

defineEmits(['click'])
</script>
