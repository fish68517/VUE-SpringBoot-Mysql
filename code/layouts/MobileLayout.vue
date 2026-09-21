<template>
  <view class="mobile-shell">
    <view class="mobile-header">
      <button v-if="!primary" class="mobile-back" @click="back">‹ 返回</button>
      <view class="mobile-heading">
        <text class="mobile-brand">郑州智慧供水</text>
        <text class="page-title">{{ title }}</text>
      </view>
      <button v-if="!primary" class="mobile-home-link" @click="go('mobileHome', {}, true)">工作台</button>
      <text v-else class="mobile-user">{{ demo.user?.displayName }}</text>
    </view>
    <view class="page-body mobile-content">
      <view v-if="demo.warning" class="notice warning">{{ demo.warning }}</view>
      <slot />
    </view>
    <view class="mobile-bottom-nav">
      <button
        v-for="item in tabs"
        :key="item.key"
        :class="['mobile-nav-item', { selected: selected === item.key }]"
        :aria-label="item.label"
        @click="go(item.key, {}, true)"
      >
        <text class="mobile-nav-icon">{{ item.icon }}</text>
        <text>{{ item.label }}</text>
      </button>
    </view>
  </view>
</template>
<script setup lang="ts">
import { computed } from 'vue'
import { useDemo } from '../stores/demo'
import { go, back } from '../navigation/routeMap'
import { mobileTab } from '../navigation/clientPolicy'
const props = defineProps<{ title: string; mode: string }>()
const demo = useDemo()
const tabs = [
  { key: 'mobileHome', label: '工作台', icon: '⌂' },
  { key: 'mobileTasks', label: '任务', icon: '▤' },
  { key: 'mobileMap', label: '地图', icon: '◇' },
  { key: 'profile', label: '我的', icon: '○' },
]
const primary = computed(() => tabs.some((t) => t.key === props.mode))
const selected = computed(() => mobileTab(props.mode))
</script>
