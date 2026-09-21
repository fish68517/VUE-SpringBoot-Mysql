<template>
  <view class="business-layout">
    <view class="sidebar">
      <view class="brand" @click="go('dashboard', {}, true)">
        <text class="brand-mark">≈</text>
        <view>
          <text class="brand-title">郑州智慧供水</text>
          <text class="brand-sub">SMART WATER</text>
        </view>
      </view>
      <text class="nav-caption">运行与业务</text>
      <view
        v-for="item in navigation"
        :key="item.key"
        :class="['nav-item', { active: section === item.key }]"
        @click="go(item.key, {}, true)"
      >
        <text class="nav-icon">{{ item.icon }}</text>
        {{ item.label }}
        <text v-if="item.key === 'alarms'" class="nav-count">{{ demo.metrics.alarms }}</text>
      </view>
      <view class="phase-note">
        <text class="phase-dot" />
        供水业务管理
        <text class="muted block">审核、巡检、设备与报表</text>
      </view>
    </view>
    <view class="workspace">
      <view class="topbar">
        <view class="breadcrumb">
          <text class="muted" @click="go('portal', {}, true)">智慧水务平台</text>
          <text class="muted">/</text>
          <text>{{ title }}</text>
        </view>
        <view class="top-actions">
          <text class="account-name">{{ demo.user?.displayName }}</text>
          <button class="text-button" @click="demo.logout">退出</button>
        </view>
      </view>
      <view class="page-heading">
        <view>
          <text class="page-eyebrow">ZHENGZHOU SMART WATER</text>
          <text class="page-title">{{ title }}</text>
        </view>
        <view class="heading-actions">
          <button class="button secondary compact" @click="back">返回</button>
          <button class="button secondary compact" @click="go('dashboard', {}, true)">↗ 返回大屏</button>
        </view>
      </view>
      <view v-if="demo.warning" class="notice warning">
        {{ demo.warning }}
        <text v-if="demo.has('reset')" class="link" @click="go('settings')">前往系统设置</text>
      </view>
      <view class="page-body"><slot /></view>
      <view class="page-footer">
        郑州城市智慧供水管理系统
        <text>版本 · {{ demo.state.version }}</text>
      </view>
    </view>
    <view class="mobile-tabs">
      <view @click="go('mobileHome', {}, true)">
        ⌂
        <text>工作台</text>
      </view>
      <view @click="go('mobileTasks', {}, true)">
        ▤
        <text>任务</text>
      </view>
      <view @click="go('mobileMap', {}, true)">
        ◇
        <text>地图</text>
      </view>
      <view @click="go('profile', {}, true)">
        ○
        <text>我的</text>
      </view>
    </view>
  </view>
</template>
<script setup lang="ts">
import { useDemo } from '../stores/demo'
import { go, back } from '../navigation/routeMap'
defineProps<{ title: string; section: string }>()
const demo = useDemo()
const navigation = [
  { key: 'portal', label: '工作门户', icon: '⌂' },
  { key: 'dashboard', label: '管理驾驶舱', icon: '▦' },
  { key: 'map', label: '管控一张图', icon: '◇' },
  { key: 'alarms', label: '报警中心', icon: '◉' },
  { key: 'orders', label: '工单中心', icon: '▤' },
  { key: 'dma', label: 'DMA 分区计量', icon: '◷' },
  { key: 'inspection', label: '管网巡检', icon: '⌁' },
  { key: 'datahub', label: '设备监测', icon: '▥' },
  { key: 'reports', label: '统计报表', icon: '▧' },
  { key: 'video', label: '视频中心', icon: '▷' },
  { key: 'energy', label: '能耗与运行', icon: 'ϟ' },
  { key: 'settings', label: '系统设置', icon: '⚙' },
]
</script>
