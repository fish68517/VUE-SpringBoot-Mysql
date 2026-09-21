<template>
  <view v-if="mode === 'inspection' || mode === 'mobileTasks'">
    <view class="toolbar">
      <button class="button secondary" @click="go('inspectionConfig')">模板与路线</button>
      <button class="button secondary" @click="go('inspectionReplay')">人员、轨迹与统计</button>
      <button class="button secondary" v-if="demo.has('export')" @click="exportTasks">导出任务</button>
    </view>
    <view class="kpi-grid">
      <view class="kpi">
        <text class="kpi-label">巡检任务</text>
        <text class="kpi-number">{{ demo.tasks.length }}</text>
        <text class="kpi-note">当前可见任务</text>
      </view>
      <view class="kpi">
        <text class="kpi-label">待执行</text>
        <text class="kpi-number">{{ demo.tasks.filter((t) => t.status === 'pending').length }}</text>
      </view>
      <view class="kpi">
        <text class="kpi-label">执行中</text>
        <text class="kpi-number">{{ demo.tasks.filter((t) => t.status === 'processing').length }}</text>
      </view>
      <view class="kpi">
        <text class="kpi-label">完成率</text>
        <text class="kpi-number">{{ percent(demo.metrics.completion) }}</text>
      </view>
    </view>
    <view class="panel">
      <view class="toolbar">
        <SelectField v-model="filter.status" :options="options(dictionary.taskStatuses, '全部状态')" />
        <input class="field search" v-model="filter.keyword" placeholder="搜索任务名称 / 编号" />
        <view class="spacer" />
        <button v-if="demo.has('write')" class="button" @click="go('taskEdit')">＋ 发布巡检任务</button>
      </view>
      <view class="table desktop-table">
        <view class="table-row header task-row">
          <text>任务编号</text>
          <text>任务名称</text>
          <text>执行人</text>
          <text>设施检查</text>
          <text>状态</text>
        </view>
        <view
          v-for="t in rows"
          :key="t.id"
          class="table-row task-row"
          @click="go('taskDetail', { id: t.id })"
        >
          <text class="link">{{ t.id }}</text>
          <text>{{ t.name }}</text>
          <text>{{ personName(t.assigneeId) }}</text>
          <text>{{ t.checks.filter((c) => c.result).length }} / {{ t.checks.length }}</text>
          <StatusTag :value="t.status" kind="task" />
        </view>
      </view>
      <view class="mobile-cards">
        <view v-for="t in rows" :key="t.id" class="list-item" @click="go('taskDetail', { id: t.id })">
          <text class="list-item-title">{{ t.name }}</text>
          <view class="list-item-meta">
            <text>{{ personName(t.assigneeId) }} · {{ t.plannedAt }}</text>
            <StatusTag :value="t.status" kind="task" />
          </view>
        </view>
      </view>
      <view v-if="!filtered.length" class="empty">没有符合条件的任务</view>
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
  <view v-else-if="mode === 'taskEdit'" class="panel">
    <view class="panel-title">发布巡检任务</view>
    <view v-if="!demo.has('write')" class="empty">当前账号不能发布任务</view>
    <view v-else>
      <view class="form-grid">
        <view class="form-field">
          <text class="field-label">挂接模板 *</text>
          <SelectField
            v-model="form.templateId"
            :options="demo.state.phase2.templates.map((t) => ({ value: t.id, label: t.name }))"
          />
        </view>
        <view class="form-field">
          <text class="field-label">预设路线</text>
          <SelectField
            v-model="form.routeId"
            :options="[
              { value: '', label: '自选设施' },
              ...demo.state.phase2.routes
                .filter((r) => demo.user?.regionIds.includes(r.regionId))
                .map((r) => ({ value: r.id, label: r.name })),
            ]"
          />
        </view>
        <view class="form-field">
          <text class="field-label">任务名称 *</text>
          <input class="field" v-model="form.name" placeholder="填写巡检任务名称" />
        </view>
        <view class="form-field">
          <text class="field-label">计划日期 *</text>
          <picker mode="date" :value="form.plannedAt" @change="form.plannedAt = $event.detail.value">
            <view class="select-field">{{ form.plannedAt }}</view>
          </picker>
        </view>
        <view class="form-field">
          <text class="field-label">巡检区域 *</text>
          <SelectField
            v-model="form.regionId"
            :options="
              regions
                .filter((r) => demo.user?.regionIds.includes(r.id))
                .map((r) => ({ value: r.id, label: r.name }))
            "
          />
        </view>
        <view class="form-field">
          <text class="field-label">执行人 *</text>
          <SelectField v-model="form.assigneeId" :options="assignees" />
        </view>
        <view class="form-field form-wide">
          <text class="field-label">选择设施（点击顺序即巡检顺序）*</text>
          <view class="inline-options">
            <view
              v-for="f in regionFacilities"
              :key="f.id"
              :class="['choice', { active: form.facilityIds.includes(f.id) }]"
              @click="toggle(f.id)"
            >
              {{ form.facilityIds.includes(f.id) ? '✓ ' : '' }}{{ f.name }}
            </view>
          </view>
        </view>
      </view>
      <button class="button" :disabled="busy" @click="create">发布任务</button>
    </view>
  </view>
  <view v-else-if="task">
    <view v-if="mobile" class="toolbar">
      <button
        class="button secondary"
        @click="go('mobileMap', { taskId: task.id, facilityId: task.checks[0]?.facilityId })"
      >
        任务设施地图
      </button>
      <button class="button secondary" @click="go('inspectionReplay', { id: task.id })">巡检轨迹</button>
    </view>
    <view class="panel" style="margin-bottom: 20px">
      <view class="panel-title">
        {{ task.name }}
        <StatusTag :value="task.status" kind="task" />
      </view>
      <view class="detail-grid">
        <view>
          <text class="detail-label">执行人</text>
          {{ personName(task.assigneeId) }}
        </view>
        <view>
          <text class="detail-label">计划日期</text>
          {{ task.plannedAt }}
        </view>
        <view>
          <text class="detail-label">检查进度</text>
          {{ task.checks.filter((c) => c.result).length }} / {{ task.checks.length }}
        </view>
        <view>
          <text class="detail-label">巡检区域</text>
          {{ regionName(task.regionId) }}
        </view>
      </view>
      <text class="subtle">
        基础模板：设施外观、周边环境与运行状态。异常检查项将生成关联工单，维修完成情况独立跟进。
      </text>
    </view>
    <view class="columns">
      <view class="panel">
        <view class="panel-title">设施检查清单</view>
        <view v-for="c in task.checks" :key="c.facilityId" class="check-card">
          <view class="panel-title">
            <text class="link" @click="go('facility', { id: c.facilityId })">
              {{ nameOf(c.facilityId) }} ↗
            </text>
            <text class="subtle">
              {{
                c.result === 'normal' ? '已检查 · 正常' : c.result === 'abnormal' ? '已上报 · 异常' : '待检查'
              }}
            </text>
          </view>
          <template v-if="editable">
            <view class="inline-options">
              <view
                :class="['choice', { active: edit[c.facilityId]?.result === 'normal' }]"
                @click="edit[c.facilityId].result = 'normal'"
              >
                正常
              </view>
              <view
                :class="['choice', { active: edit[c.facilityId]?.result === 'abnormal' }]"
                @click="edit[c.facilityId].result = 'abnormal'"
              >
                发现异常
              </view>
            </view>
            <textarea class="field" v-model="edit[c.facilityId].description" placeholder="检查说明（必填）" />
            <view class="inline-options" style="margin-top: 12px">
              <view
                :class="['choice', { active: edit[c.facilityId].attachmentId === 'ATT-001' }]"
                @click="edit[c.facilityId].attachmentId = edit[c.facilityId].attachmentId ? null : 'ATT-001'"
              >
                {{ edit[c.facilityId].attachmentId ? '✓ 已选择巡检附件' : '＋ 选择本地附件' }}
              </view>
            </view>
            <image
              v-if="edit[c.facilityId].attachmentId"
              class="attachment"
              src="/static/demo-assets/inspection.svg"
              mode="aspectFit"
            />
            <view v-for="f in task.template?.fields || []" :key="f.id" class="form-field">
              <text class="field-label">{{ f.label }} {{ f.required ? '*' : '' }}</text>
              <SelectField
                v-if="f.type === 'choice'"
                v-model="edit[c.facilityId].values![f.id]"
                :options="[{ value: '', label: '请选择' }, ...f.options.map((o) => ({ value: o, label: o }))]"
              />
              <input
                v-else
                class="field"
                v-model="edit[c.facilityId].values![f.id]"
                :type="f.type === 'number' ? 'digit' : 'text'"
              />
            </view>
            <view class="detail-actions">
              <button class="button compact" :disabled="busy" @click="saveCheck(c.facilityId)">
                保存检查项
              </button>
            </view>
          </template>
          <template v-else>
            <text class="content-text">{{ c.description || '暂无检查记录' }}</text>
            <text v-for="f in task.template?.fields || []" :key="f.id" class="subtle">
              {{ f.label }}：{{ c.values?.[f.id] || '—' }}
            </text>
            <image
              v-if="c.attachmentId"
              class="attachment"
              src="/static/demo-assets/inspection.svg"
              mode="aspectFit"
            />
          </template>
          <text
            v-if="c.workOrderId"
            class="link block"
            style="margin-top: 13px"
            @click="go('orderDetail', { id: c.workOrderId })"
          >
            关联工单 {{ c.workOrderId }} ↗
          </text>
        </view>
        <button v-if="editable" class="button" :disabled="busy" @click="complete">提交完成巡检</button>
      </view>
      <view class="panel">
        <view class="panel-title">任务记录</view>
        <view class="timeline">
          <view v-for="(h, i) in task.history" :key="i" class="timeline-item">
            {{ h.text }}
            <text class="timeline-time">{{ h.actor }} · {{ formatTime(h.time) }}</text>
          </view>
        </view>
        <button class="button secondary" @click="go('inspectionReplay', { id: task.id })">
          查看历史轨迹
        </button>
        <template v-if="demo.has('dispatch') && !['completed', 'cancelled'].includes(task.status)">
          <view class="panel-title">分派 / 改派</view>
          <SelectField
            v-model="reassignee"
            :options="
              users
                .filter((u) => u.roleId === 'operator' && u.regionIds.includes(task!.regionId))
                .map((u) => ({ value: u.id, label: u.displayName }))
            "
          />
          <textarea class="field" v-model="reassignReason" placeholder="改派或取消原因（必填）" />
          <view class="toolbar">
            <button class="button" @click="reassign">保存改派</button>
            <button class="button danger" @click="cancelTask">取消任务</button>
          </view>
        </template>
      </view>
    </view>
  </view>
  <view v-else class="panel empty">任务不存在或无权访问</view>
</template>
<script setup lang="ts">
import { computed, ref, reactive, watch, nextTick } from 'vue'
import { useMobileClient } from '../platform/client'
import { exportCsv } from '../platform/export'
import { confirm } from '../domain/presentation'
import { useDemo } from '../stores/demo'
import { dictionary, regions, users, clone } from '../repositories/seed'
import { options, personName, regionName } from '../domain/presentation'
import { percent, formatTime } from '../domain/metrics'
import { go } from '../navigation/routeMap'
import SelectField from './SelectField.vue'
import StatusTag from './StatusTag.vue'
const mobile = useMobileClient()
const props = defineProps<{ mode: string; query: Record<string, string> }>(),
  demo = useDemo(),
  busy = ref(false),
  edit = reactive<
    Record<
      string,
      { result: string; description: string; attachmentId: string | null; values?: Record<string, string> }
    >
  >({})
const filter = reactive(demo.filters.tasks || { status: '', keyword: '', page: 1 })
demo.filters.tasks = filter
const task = computed(() => demo.tasks.find((t) => t.id === props.query.id)),
  editable = computed(
    () =>
      !!task.value &&
      !['completed', 'cancelled'].includes(task.value.status) &&
      demo.has('write') &&
      (demo.user?.roleId === 'admin' || task.value.assigneeId === demo.user?.id),
  )
watch(
  task,
  (t) => {
    for (const c of t?.checks || [])
      if (!edit[c.facilityId]) edit[c.facilityId] = { ...clone(c), values: clone(c.values || {}) }
  },
  { immediate: true },
)
const filtered = computed(() =>
    demo.tasks
      .filter(
        (t) => (!filter.status || t.status === filter.status) && (t.name + t.id).includes(filter.keyword),
      )
      .slice()
      .reverse(),
  ),
  pages = computed(() => Math.max(1, Math.ceil(filtered.value.length / 10))),
  rows = computed(() => filtered.value.slice((filter.page - 1) * 10, filter.page * 10))
watch(
  () => [filter.status, filter.keyword],
  () => (filter.page = 1),
)
const form = reactive({
  name: '',
  regionId: demo.user?.regionIds[0] || regions[0].id,
  templateId: 'TPL-001',
  routeId: '',
  plannedAt: '2026-09-20',
  assigneeId: demo.user?.roleId === 'operator' ? demo.user.id : '',
  facilityIds: [] as string[],
})
const regionFacilities = computed(() => demo.facilities.filter((f) => f.regionId === form.regionId)),
  assignees = computed(() => [
    { value: '', label: '选择执行人' },
    ...users
      .filter(
        (u) =>
          u.roleId === 'operator' &&
          u.regionIds.includes(form.regionId) &&
          (demo.has('dispatch') || u.id === demo.user?.id),
      )
      .map((u) => ({ value: u.id, label: u.displayName })),
  ])
watch(
  () => form.regionId,
  () => {
    form.assigneeId = demo.user?.roleId === 'operator' ? demo.user.id : ''
    form.facilityIds = []
  },
)
const nameOf = (id: string) => demo.facilities.find((f) => f.id === id)?.name || id
function toggle(id: string) {
  form.routeId = ''
  form.facilityIds = form.facilityIds.includes(id)
    ? form.facilityIds.filter((v) => v !== id)
    : [...form.facilityIds, id]
}
async function create() {
  if (busy.value) return
  busy.value = true
  const r = await demo.run((e) => e.createTask(form))
  busy.value = false
  if (r.success) go('taskDetail', { id: r.data }, true)
}
async function saveCheck(id: string) {
  if (busy.value || !task.value) return
  busy.value = true
  const c = edit[id]
  const r = await demo.run((e) =>
    e.checkTask(task.value!.id, id, c.result, c.description, c.attachmentId, c.values),
  )
  busy.value = false
  if (r.success) uni.showToast({ title: r.data ? '异常工单已生成' : '检查项已保存', icon: 'none' })
}
async function complete() {
  if (busy.value || !task.value) return
  busy.value = true
  const r = await demo.run((e) => e.completeTask(task.value!.id))
  busy.value = false
  if (r.success) uni.showToast({ title: '巡检已完成', icon: 'success' })
}
watch(
  () => form.routeId,
  async (id) => {
    const r = demo.state.phase2.routes.find((r) => r.id === id)
    if (r) {
      form.regionId = r.regionId
      await nextTick()
      form.facilityIds = [...r.facilityIds]
    }
  },
)
if (props.query.routeId) form.routeId = props.query.routeId
const reassignee = ref(''),
  reassignReason = ref('')
async function reassign() {
  if (task.value)
    await demo.run((e) => e.reassignTask(task.value!.id, reassignee.value, reassignReason.value))
}
async function cancelTask() {
  if (task.value && (await confirm('取消巡检任务', '取消后保留历史与异常工单，并从完成率分母排除。')))
    await demo.run((e) => e.cancelTask(task.value!.id, reassignReason.value))
}
function exportTasks() {
  exportCsv('巡检任务', [
    ['编号', '名称', '执行人', '状态', '日期'],
    ...filtered.value.map((t) => [t.id, t.name, personName(t.assigneeId), t.status, t.plannedAt]),
  ])
}
</script>
