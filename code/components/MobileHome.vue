<template>
  <view class="mobile-workbench">
    <view class="workbench-welcome">
      <text class="greeting-title">{{ demo.user?.displayName }}，工作顺利</text>
      <text>{{ role }} · {{ regionsText }}</text>
      <text class="workbench-date">{{ formatTime(demo.state.simulationTime).slice(0, 10) }}</text>
    </view>
    <view class="workbench-metrics">
      <button @click="go('mobileTasks', { scope: 'mine', view: 'pending' })">
        <text>{{ pendingTasks.filter((t) => t.status === 'pending').length }}</text>
        待执行巡检
      </button>
      <button @click="go('mobileTasks', { view: 'history', scope: 'mine' })">
        <text>{{ completed.length }}</text>
        已完成巡检
      </button>
      <button @click="go('orders', { scope: guest ? '' : 'mine' })">
        <text>{{ myOrders.length }}</text>
        {{ guest ? '可见工单' : '我的工单' }}
      </button>
    </view>
    <view class="mobile-section">
      <view class="mobile-section-title">现场工作</view>
      <view class="workbench-actions">
        <button v-for="item in actions" :key="item.key" @click="go(item.key, item.params || {})">
          <text class="action-symbol">{{ item.icon }}</text>
          <text>{{ item.label }}</text>
        </button>
      </view>
    </view>
    <view class="mobile-section panel">
      <view class="mobile-section-title">
        <text>{{ guest ? '待执行巡检' : '我的待办巡检' }}</text>
        <button class="mobile-link" @click="go('mobileTasks')">全部 ›</button>
      </view>
      <view
        v-for="task in pendingTasks.slice(0, 3)"
        :key="task.id"
        class="workbench-task"
        @click="go('taskDetail', { id: task.id })"
      >
        <view>
          <text class="list-item-title">{{ task.name }}</text>
          <StatusTag :value="task.status" kind="task" />
        </view>
        <text class="subtle">
          {{ task.plannedAt }} · {{ task.checks.filter((c) => c.result).length }}/{{
            task.checks.length
          }}
          处已检查
        </text>
        <button
          class="mobile-link"
          @click.stop="go('mobileMap', { taskId: task.id, facilityId: task.checks[0]?.facilityId })"
        >
          查看任务地图 ›
        </button>
      </view>
      <view v-if="!pendingTasks.length" class="empty">暂无待办巡检，可在任务中查看历史记录。</view>
    </view>
    <view v-if="demo.has('dispatch')" class="notice mobile-section">
      当前为管理员。区域任务和配置入口位于“我的 → 管理工具”。
    </view>
    <view class="mobile-section panel">
      <view class="mobile-section-title">通知公告</view>
      <view
        v-for="notice in portal.announcements"
        :key="notice.id"
        class="list-item"
        @click="go('portalDetail', { type: 'notice', id: notice.id })"
      >
        <text class="list-item-title">{{ notice.title }}</text>
        <text class="subtle">{{ notice.date }}</text>
      </view>
    </view>
  </view>
</template>
<script setup lang="ts">
import { computed } from 'vue'
import { useDemo } from '../stores/demo'
import { roles, portal } from '../repositories/seed'
import { regionName } from '../domain/presentation'
import { formatTime } from '../domain/metrics'
import { go } from '../navigation/routeMap'
import StatusTag from './StatusTag.vue'
const demo = useDemo()
const guest = computed(() => demo.user?.roleId === 'guest')
const role = computed(() => roles.find((r) => r.id === demo.user?.roleId)?.name)
const regionsText = computed(() => demo.user?.regionIds.map(regionName).join('、'))
const tasks = computed(() => demo.tasks.filter((t) => guest.value || t.assigneeId === demo.user?.id))
const pendingTasks = computed(() => tasks.value.filter((t) => ['pending', 'processing'].includes(t.status)))
const completed = computed(() => tasks.value.filter((t) => t.status === 'completed'))
const myOrders = computed(() => demo.orders.filter((w) => guest.value || w.assigneeId === demo.user?.id))
const actions = computed(() => [
  { key: 'mobileTasks', label: '巡检任务', icon: '▤', params: { scope: 'mine' } },
  { key: 'mobileMap', label: '设施地图', icon: '◇' },
  ...(demo.has('write')
    ? [
        { key: 'mobileReport', label: '事件上报', icon: '＋' },
        { key: 'taskEdit', label: '新建巡检', icon: '⌁' },
      ]
    : []),
  { key: 'mobileTasks', label: '历史巡查', icon: '◷', params: { view: 'history', scope: 'mine' } },
  { key: 'inspectionReplay', label: '巡检轨迹', icon: '↝' },
  { key: 'alarms', label: '告警处理', icon: '◉' },
  { key: 'orders', label: '工单办理', icon: '▣', params: { scope: guest.value ? '' : 'mine' } },
])
</script>
<style scoped>
.workbench-welcome {
  background: linear-gradient(125deg, #096a84, #0b99a4);
  padding: 22px 20px;
  border-radius: 18px;
  color: white;
}
.workbench-welcome > text {
  display: block;
  font-size: 13px;
  line-height: 23px;
}
.workbench-welcome .greeting-title {
  font-size: 21px;
  margin-bottom: 10px;
  font-weight: 600;
}
.workbench-date {
  margin-top: 10px;
  opacity: 0.8;
}
.workbench-metrics {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
  margin-top: 12px;
}
.workbench-metrics button {
  background: white;
  border-radius: 12px;
  width: 100%;
  padding: 16px 4px;
  line-height: 23px;
  font-size: 12px;
  color: #5d7485;
}
.workbench-metrics button text {
  display: block;
  color: #13677f;
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 8px;
}
.workbench-actions {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
}
.workbench-actions button {
  width: 100%;
  padding: 14px 4px;
  background: white;
  border-radius: 12px;
  font-size: 13px;
  line-height: 22px;
  color: #29485c;
}
.action-symbol {
  display: block;
  font-size: 25px;
  color: #07869b;
  margin-bottom: 9px;
}
.workbench-task {
  padding: 14px 0;
  border-top: 1px solid #edf2f5;
}
.workbench-task > view {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  margin-bottom: 8px;
}
</style>
