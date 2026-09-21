<template>
  <view class="mobile-task-list">
    <view class="panel">
      <view class="mobile-section-title">
        <text>{{ scopeTitle }}</text>
        <button v-if="demo.has('write')" class="mobile-link" @click="go('taskEdit')">＋ 新建</button>
      </view>
      <view v-if="demo.has('dispatch')" class="inline-options">
        <button :class="['button', scope === 'mine' ? '' : 'secondary']" @click="scope = 'mine'">
          本人任务
        </button>
        <button :class="['button', scope === 'region' ? '' : 'secondary']" @click="scope = 'region'">
          区域任务
        </button>
      </view>
      <view class="task-summary">
        <view>
          <text>{{ taskList.length }}</text>
          任务总数
        </view>
        <view>
          <text>{{ completed }}</text>
          已完成
        </view>
        <view>
          <text>{{ completion }}%</text>
          完成率
        </view>
      </view>
      <input class="field" v-model="keyword" placeholder="搜索任务名称 / 编号" />
    </view>
    <view class="task-buckets">
      <button
        v-for="item in buckets"
        :key="item.key"
        :class="{ selected: bucket === item.key }"
        @click="bucket = item.key"
      >
        {{ item.label }}
      </button>
    </view>
    <view v-for="task in filtered" :key="task.id" class="panel task-card">
      <view class="panel-title">
        <text>{{ task.name }}</text>
        <StatusTag :value="task.status" kind="task" />
      </view>
      <text class="subtle">{{ task.id }} · {{ regionName(task.regionId) }}</text>
      <text class="subtle">{{ personName(task.assigneeId) }} · 计划 {{ task.plannedAt }}</text>
      <view class="task-progress"><view :style="{ width: progress(task) + '%' }" /></view>
      <text class="subtle">
        已检查 {{ task.checks.filter((c) => c.result).length }} / {{ task.checks.length }} 处设施
      </text>
      <view class="detail-actions">
        <button class="button" @click="go('taskDetail', { id: task.id })">
          {{ bucket === 'history' || !demo.has('write') ? '查看记录' : '查看并处理' }}
        </button>
        <button
          class="button secondary"
          @click="go('mobileMap', { taskId: task.id, facilityId: task.checks[0]?.facilityId })"
        >
          任务地图
        </button>
        <button
          v-if="bucket === 'history'"
          class="button secondary"
          @click="go('inspectionReplay', { id: task.id })"
        >
          查看轨迹
        </button>
      </view>
    </view>
    <view v-if="!filtered.length" class="panel empty">
      {{ keyword ? '没有匹配的巡检任务' : '当前分类暂无任务' }}
    </view>
  </view>
</template>
<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import type { Task } from '../domain/types'
import { useDemo } from '../stores/demo'
import { go } from '../navigation/routeMap'
import { regionName, personName } from '../domain/presentation'
import StatusTag from './StatusTag.vue'
const props = defineProps<{ query: Record<string, string> }>()
const demo = useDemo()
const saved = demo.filters.mobileTaskList || {}
const keyword = ref(saved.keyword || '')
const bucket = ref(
  ['pending', 'processing', 'history'].includes(props.query.view)
    ? props.query.view
    : saved.bucket || 'pending',
)
const scope = ref(
  props.query.scope === 'mine'
    ? 'mine'
    : demo.has('dispatch') && props.query.scope === 'region'
      ? 'region'
      : saved.scope || 'mine',
)
const buckets = [
  { key: 'pending', label: '待执行' },
  { key: 'processing', label: '进行中' },
  { key: 'history', label: '历史巡查' },
]
const taskList = computed(() =>
  demo.tasks.filter(
    (t) =>
      demo.user?.roleId === 'guest' ||
      (demo.has('dispatch') && scope.value === 'region') ||
      t.assigneeId === demo.user?.id,
  ),
)
const scopeTitle = computed(() =>
  demo.user?.roleId === 'guest'
    ? '可见巡检任务'
    : demo.has('dispatch') && scope.value === 'region'
      ? '区域巡检任务'
      : '我的巡检任务',
)
const completed = computed(() => taskList.value.filter((t) => t.status === 'completed').length)
const completion = computed(() => {
  const valid = taskList.value.filter((t) => t.status !== 'cancelled').length
  return valid ? Math.round((completed.value / valid) * 100) : 0
})
const filtered = computed(() =>
  taskList.value
    .filter(
      (t) =>
        (bucket.value === 'history'
          ? ['completed', 'cancelled'].includes(t.status)
          : t.status === bucket.value) && (t.name + t.id).includes(keyword.value),
    )
    .slice()
    .reverse(),
)
const progress = (task: Task) =>
  task.checks.length ? Math.round((task.checks.filter((c) => c.result).length / task.checks.length) * 100) : 0
watch([keyword, bucket, scope], () => {
  demo.filters.mobileTaskList = { keyword: keyword.value, bucket: bucket.value, scope: scope.value }
})
</script>
<style scoped>
.task-summary {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  text-align: center;
  margin: 18px 0;
  color: #718694;
  font-size: 12px;
}
.task-summary text {
  display: block;
  font-size: 25px;
  color: #17697e;
  font-weight: 600;
  margin-bottom: 8px;
}
.task-buckets {
  display: flex;
  gap: 8px;
  margin: 16px 0;
}
.task-buckets button {
  flex: 1;
  min-height: 44px;
  margin: 0;
  padding: 8px 4px;
  line-height: 26px;
  font-size: 14px;
  background: white;
  color: #627b8a;
  border-radius: 10px;
}
.task-buckets button.selected {
  background: #087f95;
  color: white;
}
.task-card {
  margin-bottom: 14px;
}
.task-card .subtle {
  display: block;
  margin-top: 8px;
}
.task-progress {
  height: 6px;
  border-radius: 4px;
  background: #e7eff3;
  margin-top: 16px;
  overflow: hidden;
}
.task-progress > view {
  height: 100%;
  background: #1aa2aa;
}
</style>
