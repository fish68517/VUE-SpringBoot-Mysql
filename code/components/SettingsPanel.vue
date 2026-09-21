<template>
  <view class="toolbar">
    <button class="button" @click="go('advancedSettings')">门户、通知与快照</button>
    <button class="button secondary" @click="go('inspectionConfig')">巡检配置</button>
    <button class="button secondary" @click="go('mapConfig')">图层与审核配置</button>
  </view>
  <view class="columns">
    <view class="panel">
      <view class="panel-title">业务场景</view>
      <view class="notice">
        切换场景会恢复所选场景初始数据，并清除本机工单、巡检修改和操作记录。
      </view>
      <view
        v-for="s in dictionary.scenarios"
        :key="s.id"
        :class="['scenario-card', { selected: scenario === s.id }]"
        @click="scenario = s.id"
      >
        <view>
          <text class="list-item-title">{{ s.name }}</text>
          <text v-if="demo.state.scenarioId === s.id" class="demo-badge">当前场景</text>
        </view>
        <text class="subtle">{{ s.description }}</text>
      </view>
      <button v-if="demo.has('reset')" class="button danger" :disabled="busy" @click="reset">
        重置并加载场景
      </button>
      <text v-else class="subtle">当前角色只能查看设置，重置由管理员执行。</text>
      <view class="divider" />
      <view class="panel-title">
        回放时钟
        <text class="subtle">{{ formatTime(demo.state.simulationTime) }}</text>
      </view>
      <view class="detail-actions">
        <button class="button secondary" @click="demo.playing ? demo.pause() : demo.play()">
          {{ demo.playing ? '暂停回放' : '启动回放时钟' }}
        </button>
      </view>
      <text class="subtle block" style="margin-top: 12px">
        每 5 秒推进 1 分钟；不生成随机新告警。离开设置页或应用后台时暂停。
      </text>
    </view>
    <view class="stack">
      <view class="panel">
        <view class="panel-title">当前账号</view>
        <view class="detail-grid">
          <view>
            <text class="detail-label">姓名</text>
            {{ demo.user?.displayName }}
          </view>
          <view>
            <text class="detail-label">角色</text>
            {{ roles.find((r) => r.id === demo.user?.roleId)?.name }}
          </view>
          <view class="form-wide">
            <text class="detail-label">可见区域</text>
            {{ demo.user?.regionIds.map(regionName).join('、') }}
          </view>
        </view>
        <button class="button secondary" @click="demo.logout">退出并切换账号</button>
      </view>
      <view class="panel">
        <view class="panel-title">版本与范围</view>
        <view class="list-item">
          <text class="detail-label">数据版本</text>
          {{ demo.state.version }}
        </view>
        <view class="list-item">
          <text class="detail-label">本机修订</text>
          {{ demo.state.revision }}
        </view>
        <view class="list-item">
          <text class="detail-label">存储方式</text>
          当前设备独立保存
        </view>
        <text class="subtle block" style="margin-top: 16px">
          门户、通知及快照请从本页顶部进入；所有设置仅保存在当前设备。
        </text>
      </view>
    </view>
  </view>
</template>
<script setup lang="ts">
import { go } from '../navigation/routeMap'
import { ref } from 'vue'
import { useDemo } from '../stores/demo'
import { dictionary, roles } from '../repositories/seed'
import { regionName, confirm } from '../domain/presentation'
import { formatTime } from '../domain/metrics'
const demo = useDemo(),
  scenario = ref(demo.state.scenarioId),
  busy = ref(false)
async function reset() {
  if (busy.value) return
  busy.value = true
  if (!(await confirm('恢复初始数据', '这会清除当前设备的业务修改，其他设备不受影响。确认继续？'))) {
    busy.value = false
    return
  }
  demo.pause()
  const r = await demo.run((e) => e.reset(scenario.value))
  busy.value = false
  if (r.success) {
    demo.filters = {}
    demo.selection = 'FAC-001'
    uni.showToast({ title: '场景已重置', icon: 'success' })
  }
}
</script>
<style scoped>
.scenario-card {
  padding: 20px;
  border: 1px solid #e0e9f1;
  border-radius: 6px;
  margin-bottom: 16px;
  cursor: pointer;
}
.scenario-card > view {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}
.scenario-card.selected {
  border-color: #3695e9;
  background: #f2f8ff;
}
</style>
