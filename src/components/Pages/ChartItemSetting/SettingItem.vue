<!--
 * @Author: wangcong
 * @Date: 2023-04-13 10:40:55
 * @LastEditTime: 2023-04-18 15:24:43
 * @LastEditors: wangcong
 * @Description: 
-->
<template>
  <div class="go-setting-item" :style="{ width: width + 'px' }">
    <slot ></slot>
    <n-text class="name" depth="3">{{ name }}</n-text>
  </div>
</template>

<script setup lang="ts">
import { NText } from 'naive-ui';
defineProps({
  name: {
    type: String,
    required: false
  },
  width: {
    type: Number,
    required: false
  }
})
</script>

<style lang="scss" scoped>
.go-setting-item {
  display: flex;
  flex-direction: column;
  min-width: 110px;
  text-align: start;
  margin-bottom: 5px;
  .name {
    margin-top: 2px;
  }
}
</style>
