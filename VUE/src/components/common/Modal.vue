<template>
  <el-dialog
    :model-value="modelValue"
    :title="title"
    :width="width"
    @update:model-value="$emit('update:modelValue', $event)"
  >
    <slot></slot>
    <template #footer>
      <el-button @click="$emit('update:modelValue', false)">取消</el-button>
      <el-button type="primary" @click="$emit('confirm')">确认</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
defineProps({
  modelValue: {
    type: Boolean,
    required: true,
  },
  title: {
    type: String,
    default: '对话框',
  },
  width: {
    type: String,
    default: '50%',
  },
})

defineEmits(['update:modelValue', 'confirm'])
</script>
