<!--
 * @Author: kaix
 * @Date: 2023-02-22 09:44:35
 * @LastEditTime: 2023-02-27 10:49:44
 * @LastEditors: kaix
 * @Description: 自定义图标库
-->
<template>
  <svg class="icon" aria-hidden="true" @click="emit('click', $event)">
    <use :xlink:href="`#icon-${icon}`" />
  </svg>
</template>

<script setup lang="ts">
import '@/assets/font/iconfont.js'

defineProps({
  icon: {
    type: String,
    required: true,
    default: ''
  },
  width: {
    type: Number,
    default: 20
  },
  height: {
    type: Number,
    default: 20
  }
})

const emit = defineEmits(['click'])
</script>

<style lang="scss" scoped>
.icon {
  display: inline-block;
  width: v-bind('width + "px"');
  height: v-bind('height + "px"');
  font-size: 16px;
  fill: currentColor;
  overflow: hidden;
  border: none;
  outline: none;
}
</style>
