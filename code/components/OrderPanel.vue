<template>
  <view v-if="mode === 'orders'">
    <view class="panel">
      <view class="toolbar">
        <SelectField v-model="filter.type" :options="options(dictionary.orderTypes, '全部工单类型')" />
        <SelectField v-model="filter.status" :options="options(dictionary.orderStatuses, '全部状态')" />
        <input class="field search" v-model="filter.keyword" placeholder="搜索工单编号 / 标题" />
        <view class="spacer" />
        <button v-if="demo.has('write')" class="button" @click="go('orderEdit')">＋ 新建工单</button>
      </view>
      <view class="table">
        <view class="table-row header order-row">
          <text>工单编号</text>
          <text>工单标题</text>
          <text>工单类型</text>
          <text>处理人</text>
          <text>处理状态</text>
        </view>
        <view
          v-for="w in rows"
          :key="w.id"
          class="table-row order-row"
          @click="go('orderDetail', { id: w.id })"
        >
          <text class="link">{{ w.id }}</text>
          <text>{{ w.title }}</text>
          <text>{{ orderType(w.type) }}</text>
          <text>{{ personName(w.assigneeId) }}</text>
          <StatusTag :value="w.status" kind="order" />
        </view>
      </view>
      <view v-if="!filtered.length" class="empty">没有符合条件的工单</view>
      <view class="pagination">
        <text>共 {{ filtered.length }} 条</text>
        <button class="button secondary compact" :disabled="filter.page <= 1" @click="filter.page--">
          上一页
        </button>
        <text>{{ filter.page }} / {{ pages }}</text>
        <button class="button secondary compact" :disabled="filter.page >= pages" @click="filter.page++">
          下一页
        </button>
      </view>
    </view>
  </view>
  <view v-else-if="mode === 'orderEdit'" class="panel">
    <view class="panel-title">
      填写工单信息
      <text class="subtle">保存于本机演示记录</text>
    </view>
    <view v-if="!demo.has('write')" class="empty">当前角色无权创建工单</view>
    <view v-else>
      <view class="form-grid">
        <view class="form-field">
          <text class="field-label">工单标题 *</text>
          <input class="field" v-model="form.title" placeholder="填写工单标题" />
        </view>
        <view class="form-field">
          <text class="field-label">工单类型 *</text>
          <SelectField v-model="form.type" :options="options(dictionary.orderTypes)" />
        </view>
        <view class="form-field">
          <text class="field-label">关联设施 *</text>
          <SelectField v-model="form.facilityId" :options="facilityOptions" />
        </view>
        <view class="form-field">
          <text class="field-label">处理人</text>
          <SelectField v-model="form.assigneeId" :options="assignees" />
        </view>
        <view class="form-field form-wide">
          <text class="field-label">处理说明 *</text>
          <textarea class="field" v-model="form.description" placeholder="填写现象、范围和处理要求" />
        </view>
      </view>
      <button class="button" :disabled="busy" @click="save">保存工单</button>
    </view>
  </view>
  <view v-else-if="order" class="columns">
    <view class="panel">
      <view class="panel-title">
        {{ order.title }}
        <StatusTag :value="order.status" kind="order" />
      </view>
      <view class="detail-grid">
        <view>
          <text class="detail-label">工单编号</text>
          <text class="detail-value">{{ order.id }}</text>
        </view>
        <view>
          <text class="detail-label">工单类型</text>
          <text class="detail-value">{{ orderType(order.type) }}</text>
        </view>
        <view>
          <text class="detail-label">执行人</text>
          <text class="detail-value">{{ personName(order.assigneeId) }}</text>
        </view>
        <view>
          <text class="detail-label">创建时间</text>
          <text class="detail-value">{{ formatTime(order.createdAt) }}</text>
        </view>
        <view>
          <text class="detail-label">关联设施</text>
          <text class="link" @click="go('facility', { id: order.facilityId })">
            {{ facilityName(order.facilityId) }} ↗
          </text>
        </view>
        <view v-if="order.sourceId">
          <text class="detail-label">业务来源</text>
          <text
            class="link"
            @click="go(order.sourceType === 'alarm' ? 'alarmDetail' : 'taskDetail', { id: order.sourceId })"
          >
            {{ order.sourceId }} ↗
          </text>
        </view>
      </view>
      <view class="divider" />
      <text class="field-label">处置要求</text>
      <text class="content-text">{{ order.description }}</text>
      <view v-if="order.result" style="margin-top: 22px">
        <text class="field-label">现场处理结果</text>
        <text class="content-text">{{ order.result }}</text>
      </view>
      <view class="divider" />
      <view v-if="canHandle && order.status !== 'completed'">
        <view v-if="order.status === 'pending'">
          <text class="field-label">分配处理人</text>
          <SelectField v-model="dispatchId" :options="detailAssignees" />
        </view>
        <view v-if="['processing', 'review'].includes(order.status)">
          <text class="field-label">
            {{ order.status === 'processing' ? '填写处理结果 *' : '复核意见（驳回必填）' }}
          </text>
          <textarea class="field" v-model="resultText" placeholder="填写处理记录或验收意见" />
        </view>
        <view class="detail-actions">
          <button
            v-if="order.status === 'pending' && demo.has('dispatch')"
            class="button"
            :disabled="busy"
            @click="transition('dispatch')"
          >
            派发工单
          </button>
          <button
            v-if="order.status === 'assigned'"
            class="button"
            :disabled="busy"
            @click="transition('start')"
          >
            接单并开始处理
          </button>
          <button
            v-if="order.status === 'processing'"
            class="button"
            :disabled="busy"
            @click="transition('submit')"
          >
            提交验收
          </button>
          <template v-if="order.status === 'review' && demo.has('review')">
            <button class="button" :disabled="busy" @click="transition('approve')">验收通过</button>
            <button class="button secondary" :disabled="busy" @click="transition('reject')">
              驳回重处理
            </button>
          </template>
        </view>
      </view>
      <view v-else class="notice">
        {{
          order.status === 'completed'
            ? '工单已完成，关联告警已按流程闭环。'
            : '当前为只读视图，请由有权限的执行人或管理员处理。'
        }}
      </view>
    </view>
    <view class="panel">
      <view class="panel-title">流转记录</view>
      <view class="timeline">
        <view v-for="(h, i) in order.steps" :key="i" class="timeline-item">
          {{ h.text }}
          <text class="timeline-time">{{ h.actor }} · {{ formatTime(h.time) }}</text>
        </view>
      </view>
    </view>
  </view>
  <view v-else class="panel empty">
    <text class="error-title">工单不存在或无权访问</text>
    <text class="link" @click="go('orders', {}, true)">返回工单中心</text>
  </view>
</template>
<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import { useDemo } from '../stores/demo'
import { dictionary, users } from '../repositories/seed'
import { go } from '../navigation/routeMap'
import { options, orderType, personName, confirm } from '../domain/presentation'
import { formatTime } from '../domain/metrics'
import StatusTag from './StatusTag.vue'
import SelectField from './SelectField.vue'
const props = defineProps<{ mode: string; query: Record<string, string> }>(),
  demo = useDemo(),
  busy = ref(false),
  resultText = ref(''),
  dispatchId = ref('')
const filter = reactive(demo.filters.orders || { type: '', status: '', keyword: '', page: 1 })
demo.filters.orders = filter
const form = reactive({
  title: '',
  type: props.query.type || 'repair',
  facilityId: props.query.facilityId || demo.facilities[0]?.id || '',
  assigneeId: '',
  description: '',
})
const order = computed(() => demo.orders.find((w) => w.id === props.query.id))
const canHandle = computed(
  () =>
    demo.has('write') &&
    !!order.value &&
    (demo.user?.roleId === 'admin' || order.value.assigneeId === demo.user?.id) &&
    !(order.value.status === 'review' && !demo.has('review')),
)
const facilityName = (id: string) => demo.facilities.find((f) => f.id === id)?.name || id
const filtered = computed(() =>
  demo.orders
    .filter(
      (w) =>
        (!filter.type || w.type === filter.type) &&
        (!filter.status || w.status === filter.status) &&
        (w.title + w.id).includes(filter.keyword),
    )
    .slice()
    .reverse(),
)
const pages = computed(() => Math.max(1, Math.ceil(filtered.value.length / 12))),
  rows = computed(() => filtered.value.slice((filter.page - 1) * 12, filter.page * 12))
watch(
  () => [filter.type, filter.status, filter.keyword],
  () => (filter.page = 1),
)
const facilityOptions = computed(() => demo.facilities.map((f) => ({ value: f.id, label: f.name })))
function operatorOptions(facilityId: string) {
  const regionId = demo.facilities.find((f) => f.id === facilityId)?.regionId
  return users
    .filter(
      (u) =>
        u.enabled &&
        u.roleId === 'operator' &&
        u.regionIds.includes(regionId || '') &&
        (demo.has('dispatch') || u.id === demo.user?.id),
    )
    .map((u) => ({ value: u.id, label: u.displayName }))
}
const assignees = computed(() => [{ value: '', label: '暂不分配' }, ...operatorOptions(form.facilityId)])
const detailAssignees = computed(() => [
  { value: '', label: '选择处理人' },
  ...operatorOptions(order.value?.facilityId || ''),
])
watch(
  () => form.facilityId,
  () => (form.assigneeId = ''),
)
async function save() {
  if (busy.value) return
  busy.value = true
  const r = await demo.run((e) => e.createOrder(form))
  busy.value = false
  if (r.success) go('orderDetail', { id: r.data }, true)
}
async function transition(action: string) {
  if (busy.value || !order.value) return
  busy.value = true
  const o = order.value
  if (action === 'approve' && !(await confirm('验收通过', '确认处理结果符合要求？通过后将关闭关联告警。'))) {
    busy.value = false
    return
  }
  const r = await demo.run((e) =>
    e.transitionOrder(o.id, o.status, action, action === 'dispatch' ? dispatchId.value : resultText.value),
  )
  busy.value = false
  if (r.success) {
    resultText.value = ''
    uni.showToast({ title: '操作已保存', icon: 'success' })
  }
}
</script>
