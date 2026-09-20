<template>
  <view>
    <view class="kpi-grid">
      <view class="kpi" @click="filter.level = ''">
        <text class="kpi-label">待闭环告警</text>
        <text class="kpi-number">{{ open.length }}</text>
        <text class="kpi-note">全部级别 · 当前可见区域</text>
        <text class="kpi-icon">◉</text>
      </view>
      <view v-for="l in ['urgent', 'important', 'normal']" :key="l" class="kpi" @click="filter.level = l">
        <text class="kpi-label">{{ levels[l] }}告警</text>
        <text
          class="kpi-number"
          :style="{ color: l === 'urgent' ? '#ef5061' : l === 'important' ? '#d5a21e' : '#2589df' }"
        >
          {{ open.filter((a) => a.level === l).length }}
        </text>
        <text class="kpi-note">点击筛选该级别</text>
      </view>
    </view>
    <view class="panel">
      <view class="toolbar">
        <SelectField v-model="filter.level" :options="options(levels, '全部级别')" />
        <SelectField v-model="filter.status" :options="options(statuses, '全部状态')" />
        <input class="field search" v-model="filter.keyword" placeholder="搜索告警标题 / 设备编号" />
        <button v-if="filter.facilityId" class="button secondary compact" @click="filter.facilityId = ''">
          清除设施筛选
        </button>
        <view class="spacer" />
        <button
          class="button danger"
          :disabled="!selected || !demo.has('write') || selected.status !== 'pending'"
          @click="showForm = true"
        >
          ▣ 紧急处理
        </button>
        <button v-if="demo.has('export')" class="button" @click="exportList">⇩ 批量导出</button>
      </view>
      <view class="columns">
        <view>
          <view class="panel-title">
            告警列表
            <text class="subtle">共 {{ filtered.length }} 条</text>
          </view>
          <view class="table-scroll">
            <view class="table">
              <view class="table-row header alarm-row">
                <text>告警级别</text>
                <text>告警标题</text>
                <text>告警设施</text>
                <text>所在区域</text>
                <text>处理状态</text>
                <text class="optional-col">处理人</text>
              </view>
              <view
                v-for="a in pageRows"
                :key="a.id"
                :class="['table-row', 'alarm-row', { selected: selectedId === a.id }]"
                @click="choose(a.id)"
              >
                <StatusTag :value="a.level" />
                <text>{{ a.title }}</text>
                <text>{{ facilityName(a.facilityId) }}</text>
                <text>{{ regionName(facility(a.facilityId)?.regionId || '') }}</text>
                <StatusTag :value="a.status" kind="alarm" />
                <text class="optional-col">{{ personName(a.assigneeId) }}</text>
              </view>
            </view>
          </view>
          <view v-if="!filtered.length" class="empty">
            没有符合条件的告警
            <text class="block link" @click="clear">清除筛选</text>
          </view>
          <view class="pagination">
            <text>第 {{ filter.page }} / {{ pages }} 页</text>
            <button class="button secondary compact" :disabled="filter.page <= 1" @click="filter.page--">
              上一页
            </button>
            <button class="button secondary compact" :disabled="filter.page >= pages" @click="filter.page++">
              下一页
            </button>
          </view>
        </view>
        <view class="alarm-details" v-if="selected">
          <view class="panel-title">
            告警详情
            <StatusTag :value="selected.level" />
          </view>
          <text class="list-item-title">{{ selected.title }}</text>
          <text class="subtle">{{ selected.id }} · {{ formatTime(selected.occurredAt) }}</text>
          <view class="detail-grid">
            <view>
              <text class="detail-label">设备编号</text>
              <text class="link" @click="go('facility', { id: selected.facilityId })">
                {{ selected.facilityId }} ↗
              </text>
            </view>
            <view>
              <text class="detail-label">当前状态</text>
              <StatusTag :value="selected.status" kind="alarm" />
            </view>
            <view>
              <text class="detail-label">触发时压力</text>
              <text class="detail-value">{{ selected.value.toFixed(3) }} {{ selected.unit }}</text>
            </view>
            <view>
              <text class="detail-label">处理人</text>
              <text class="detail-value">{{ personName(selected.assigneeId) }}</text>
            </view>
          </view>
          <view class="inline-options">
            <text class="link" @click="go('map', { facilityId: selected.facilityId })">地图定位 ↗</text>
            <text
              v-if="selected.workOrderId"
              class="link"
              @click="go('orderDetail', { id: selected.workOrderId })"
            >
              关联工单 {{ selected.workOrderId }} ↗
            </text>
          </view>
          <view class="divider" />
          <text class="list-item-title">处理历史</text>
          <view class="timeline">
            <view v-for="(h, i) in selected.history" :key="i" class="timeline-item">
              {{ h.text }}
              <text class="timeline-time">{{ h.actor }} · {{ formatTime(h.time) }}</text>
            </view>
          </view>
          <view v-if="demo.state.notifications.some((n) => n.alarmId === selected?.id)" class="notice">
            <text class="block">通知记录 · 仅模拟发送</text>
            <text
              v-for="n in demo.state.notifications.filter((n) => n.alarmId === selected?.id)"
              :key="n.id"
              class="block"
            >
              {{ n.channel === 'sms' ? '短信' : '邮件' }}：模拟发送，处理人
              {{ personName(selected.assigneeId) }}
            </text>
          </view>
          <view v-if="showForm && selected.status === 'pending'" class="handling-form">
            <text class="field-label">分配处理人</text>
            <SelectField v-model="assigneeId" :options="assignees" />
            <text class="field-label" style="margin-top: 15px">处置要求</text>
            <textarea class="field" v-model="description" placeholder="填写检查范围和处置要求" />
            <button
              class="button danger"
              style="margin-top: 14px; width: 100%"
              :disabled="busy"
              @click="create"
            >
              创建维修工单
            </button>
            <text class="subtle block" style="margin-top: 10px">创建后告警转为已受理，验收通过才关闭。</text>
          </view>
          <button
            v-else-if="selected.status === 'pending' && demo.has('write')"
            class="button"
            @click="showForm = true"
          >
            受理并分配
          </button>
        </view>
        <view v-else class="empty">{{ query.id ? '该告警不存在或无权访问' : '请选择一条告警查看详情' }}</view>
      </view>
    </view>
  </view>
</template>
<script setup lang="ts">
import { computed, ref, watch, reactive } from 'vue'
import { useDemo } from '../stores/demo'
import { dictionary, users } from '../repositories/seed'
import { regionName, personName, options } from '../domain/presentation'
import { formatTime } from '../domain/metrics'
import { go } from '../navigation/routeMap'
import { exportCsv } from '../platform/export'
import SelectField from './SelectField.vue'
import StatusTag from './StatusTag.vue'
const props = defineProps<{ query: Record<string, string> }>(),
  demo = useDemo()
const filter = reactive(
  demo.filters.alarms || { level: '', status: '', keyword: '', facilityId: '', page: 1 },
)
Object.assign(filter, {
  ...(props.query.level ? { level: props.query.level } : {}),
  ...(props.query.facilityId ? { facilityId: props.query.facilityId } : {}),
})
demo.filters.alarms = filter
const levels = dictionary.levels as Record<string, string>,
  statuses = dictionary.alarmStatuses as Record<string, string>
const selectedId = ref(props.query.id || ''),
  showForm = ref(false),
  busy = ref(false),
  assigneeId = ref(''),
  description = ref('')
const facility = (id: string) => demo.facilities.find((f) => f.id === id),
  facilityName = (id: string) => facility(id)?.name || id
const open = computed(() => demo.alarms.filter((a) => a.status !== 'closed'))
const filtered = computed(() =>
  demo.alarms.filter(
    (a) =>
      (!filter.level || a.level === filter.level) &&
      (!filter.status || a.status === filter.status) &&
      (!filter.facilityId || a.facilityId === filter.facilityId) &&
      (!props.query.dmaId || a.dmaId === props.query.dmaId) &&
      (a.title + facilityName(a.facilityId) + a.facilityId)
        .toLowerCase()
        .includes(filter.keyword.toLowerCase()),
  ),
)
const pages = computed(() => Math.max(1, Math.ceil(filtered.value.length / 10))),
  pageRows = computed(() => filtered.value.slice((filter.page - 1) * 10, filter.page * 10)),
  selected = computed(() => demo.alarms.find((a) => a.id === selectedId.value))
watch(
  () => [filter.level, filter.status, filter.keyword, filter.facilityId],
  () => (filter.page = 1),
)
watch(
  pageRows,
  (rows) => {
    if (!props.query.id && !rows.some((a) => a.id === selectedId.value)) choose(rows[0]?.id || '')
  },
  { immediate: true },
)
watch(
  selected,
  () => {
    const r = facility(selected.value?.facilityId || '')?.regionId
    const first = users.find((u) => u.roleId === 'operator' && u.regionIds.includes(r || ''))
    assigneeId.value = demo.user?.roleId === 'operator' ? demo.user.id : first?.id || ''
  },
  { immediate: true },
)
const assignees = computed(() => [
  { value: '', label: '选择处理人' },
  ...users
    .filter(
      (u) =>
        u.roleId === 'operator' &&
        u.regionIds.includes(facility(selected.value?.facilityId || '')?.regionId || '') &&
        (demo.has('dispatch') || u.id === demo.user?.id),
    )
    .map((u) => ({ value: u.id, label: u.displayName })),
])
function choose(id: string) {
  selectedId.value = id
  showForm.value = false
  description.value = ''
}
function clear() {
  Object.assign(filter, { level: '', status: '', keyword: '', facilityId: '', page: 1 })
}
async function create() {
  if (busy.value || !selected.value) return
  busy.value = true
  const r = await demo.run((e) => e.createFromAlarm(selected.value!.id, assigneeId.value, description.value))
  busy.value = false
  if (r.success) {
    showForm.value = false
    go('orderDetail', { id: r.data })
  }
}
function exportList() {
  if (!demo.has('export')) return
  exportCsv('告警清单', [
    ['告警ID', '标题', '设施', '级别', '状态', '处理人'],
    ...filtered.value.map((a) => [
      a.id,
      a.title,
      facilityName(a.facilityId),
      levels[a.level],
      statuses[a.status],
      personName(a.assigneeId),
    ]),
  ])
}
</script>
