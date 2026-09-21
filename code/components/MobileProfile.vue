<template>
  <view class="stack mobile-profile">
    <view class="panel">
      <view class="profile-avatar">{{ demo.user?.displayName.slice(0, 1) }}</view>
      <view class="panel-title">{{ demo.user?.displayName }}</view>
      <text class="subtle">{{ role }} · {{ demo.user?.username }}</text>
      <view class="divider" />
      <text class="detail-label">负责区域</text>
      <text class="content-text">{{ demo.user?.regionIds.map(regionName).join('、') }}</text>
    </view>
    <view class="panel">
      <view class="panel-title">个人工作</view>
      <button class="profile-item" @click="go('mobileTasks', { scope: 'mine' })">
        我的任务
        <text>›</text>
      </button>
      <button class="profile-item" @click="go('mobileTasks', { view: 'history', scope: 'mine' })">
        历史巡查
        <text>›</text>
      </button>
      <button class="profile-item" @click="go('inspectionReplay')">
        巡检轨迹
        <text>›</text>
      </button>
      <button v-if="demo.has('write')" class="profile-item" @click="go('mobileReport')">
        事件上报
        <text>›</text>
      </button>
    </view>
    <view v-if="demo.has('dispatch')" class="panel">
      <view class="panel-title">管理工具</view>
      <button class="profile-item" @click="go('mobileTasks', { scope: 'region' })">
        区域巡检与分派
        <text>›</text>
      </button>
      <button class="profile-item" @click="go('inspectionConfig')">
        巡检模板与路线
        <text>›</text>
      </button>
      <button class="profile-item" @click="go('mapConfig')">
        图层与审核配置
        <text>›</text>
      </button>
      <button class="profile-item" @click="go('advancedSettings')">
        通知与数据备份
        <text>›</text>
      </button>
    </view>
    <view class="panel">
      <view class="panel-title">关于应用</view>
      <text class="subtle">郑州智慧供水 · {{ app.versionName }}</text>
      <text class="subtle">数据版本：{{ demo.state.version }}</text>
      <text class="subtle">业务记录保存在当前设备。</text>
    </view>
    <button class="button secondary" @click="logout">退出登录</button>
  </view>
</template>
<script setup lang="ts">
import { computed } from 'vue'
import { useDemo } from '../stores/demo'
import { roles } from '../repositories/seed'
import { regionName, confirm } from '../domain/presentation'
import { go } from '../navigation/routeMap'
import app from '../manifest.json'
const demo = useDemo()
const role = computed(() => roles.find((r) => r.id === demo.user?.roleId)?.name)
async function logout() {
  if (await confirm('退出登录', '已保存的业务记录将保留在当前设备。确认退出？')) await demo.logout()
}
</script>
<style scoped>
.profile-avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 54px;
  height: 54px;
  background: #e5f4f6;
  color: #068298;
  border-radius: 18px;
  font-size: 25px;
  margin-bottom: 18px;
}
.profile-item {
  display: flex;
  justify-content: space-between;
  width: 100%;
  min-height: 50px;
  padding: 12px 0;
  background: white;
  border-bottom: 1px solid #edf2f5;
  font-size: 15px;
  line-height: 26px;
  text-align: left;
  color: #314f63;
  border-radius: 0;
}
.profile-item text {
  color: #91a5b1;
}
.mobile-profile .subtle {
  display: block;
  margin-top: 8px;
}
</style>
