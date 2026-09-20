<template>
  <view class="toolbar">
    <button class="button secondary" @click="go('map')">地图</button>
    <button class="button secondary" @click="go('mapChanges')">点线变更与审核</button>
    <button class="button secondary" @click="go('mapConfig')">图层配置</button>
    <button class="button secondary" @click="go('mapAnalysis')">爆管分析</button>
    <button class="button secondary" @click="go('logs')">操作日志</button>
  </view>
  <view v-if="mode === 'mapChanges'" class="equal-columns">
    <view class="panel">
      <view class="panel-title">{{ rejectedId ? '修改驳回申请' : '点线变更申请' }}</view>
      <view class="notice">有效台账在第六级审核通过后更新。设施坐标使用本地 1000 × 600 示意空间。</view>
      <view class="form-grid">
        <SelectField
          v-model="kind"
          :options="[
            { value: 'facility', label: '点设施' },
            { value: 'pipe', label: '管线' },
          ]"
        />
        <SelectField
          v-model="action"
          :options="[
            { value: 'edit', label: '编辑' },
            { value: 'add', label: '新增' },
            { value: 'delete', label: '删除' },
          ]"
        />
        <SelectField
          v-if="action !== 'add'"
          v-model="target"
          :options="objects.map((o) => ({ value: o.id, label: o.name }))"
        />
        <view v-else class="form-field">
          <text class="field-label">唯一编号</text>
          <input class="field" v-model="form.id" placeholder="唯一编号" />
        </view>
        <view class="form-field">
          <text class="field-label">名称</text>
          <input class="field" v-model="form.name" placeholder="名称" />
        </view>
        <SelectField v-model="form.regionId" :options="regionOptions" />
        <template v-if="kind === 'facility'">
          <SelectField
            v-model="form.type"
            :options="[
              { value: 'sensor', label: '传感器' },
              { value: 'valve', label: '阀门' },
              { value: 'pump', label: '水泵' },
              { value: 'meter', label: '水表' },
            ]"
          />
          <SelectField
            v-model="form.dmaId"
            :options="
              demo.state.phase2.dmas
                .filter((d) => d.regionId === form.regionId)
                .map((d) => ({ value: d.id, label: d.name }))
            "
          />
          <view class="form-field">
            <text class="field-label">X 坐标</text>
            <input class="field" v-model="x" type="digit" placeholder="X 坐标" />
          </view>
          <view class="form-field">
            <text class="field-label">Y 坐标</text>
            <input class="field" v-model="y" type="digit" placeholder="Y 坐标" />
          </view>
          <textarea class="field form-wide" v-model="form.description" placeholder="设施说明 / 纠错内容" />
        </template>
        <template v-else>
          <SelectField v-model="form.fromFacilityId" :options="facilityOptions" />
          <SelectField v-model="form.toFacilityId" :options="facilityOptions" />
          <view class="form-field">
            <text class="field-label">管径 mm</text>
            <input class="field" v-model="diameter" type="number" placeholder="管径 mm" />
          </view>
          <textarea
            class="field form-wide"
            v-model="pathText"
            placeholder="路径坐标，例如 90,100;120,100;120,150"
          />
        </template>
      </view>
      <button v-if="demo.has('write')" class="button" @click="submit">提交六级审核</button>
      <view class="notice" v-if="message">{{ message }}</view>
    </view>
    <view class="panel">
      <view class="panel-title">
        申请记录
        <text class="subtle">{{ changes.length }} 条</text>
      </view>
      <SelectField
        v-model="changeId"
        :options="[
          { value: '', label: '选择申请' },
          ...changes.map((c) => ({ value: c.id, label: c.id + ' · ' + c.proposed.name })),
        ]"
      />
      <template v-if="change">
        <view class="detail-grid">
          <view>
            状态：{{
              change.status === 'effective'
                ? '已生效'
                : change.status === 'rejected'
                  ? '已驳回'
                  : '第 ' + change.stage + ' 级待审'
            }}
          </view>
          <view>申请人：{{ personName(change.createdBy) }}</view>
        </view>
        <text class="content-text">
          变更前：{{ change.before?.name || '无' }} → {{ change.proposed.name }} / {{ change.action }}
        </text>
        <view class="timeline">
          <view class="timeline-item" v-for="(h, i) in change.history" :key="i">
            {{ h.text }}
            <text class="timeline-time">{{ h.actor }} · {{ formatTime(h.time) }}</text>
          </view>
        </view>
        <template v-if="change.status === 'pending' && demo.has('review')">
          <textarea class="field" v-model="reason" placeholder="审核意见（必填）" />
          <view class="toolbar">
            <button class="button" @click="review(true)">通过第 {{ change.stage }} 级</button>
            <button class="button danger" @click="review(false)">驳回</button>
          </view>
        </template>
        <button
          v-if="change.status === 'rejected' && change.createdBy === demo.user?.id"
          class="button"
          @click="revise"
        >
          修改并重新提交
        </button>
      </template>
      <view v-else class="empty">提交变更后可查看逐级审核记录</view>
    </view>
  </view>
  <view v-else-if="mode === 'mapConfig'" class="equal-columns">
    <view class="panel">
      <view class="panel-title">图层字段</view>
      <view class="inline-options">
        <view
          v-for="f in fields"
          :key="f.id"
          :class="['choice', { active: selectedFields.includes(f.id) }]"
          @click="toggleField(f.id)"
        >
          {{ f.name }}
        </view>
      </view>
      <view class="toolbar">
        <button v-if="demo.has('dispatch')" class="button" @click="layer('draft')">保存草稿</button>
        <button v-if="demo.has('dispatch')" class="button secondary" @click="layer('sync')">模拟同步</button>
        <button v-if="demo.has('dispatch')" class="button" @click="layer('publish')">模拟发布</button>
      </view>
      <text class="subtle">
        发布字段：{{ demo.state.phase2.layers.fields.join('、') }}
        <br />
        同步：{{ formatTime(demo.state.phase2.layers.syncedAt) }}
        <br />
        发布：{{ formatTime(demo.state.phase2.layers.publishedAt) }}
      </text>
      <view class="notice">仅本机配置状态；不会请求 GIS 或云服务。</view>
    </view>
    <view class="panel">
      <view class="panel-title">六级审核人员配置</view>
      <view v-for="(_, i) in reviewers" :key="i" class="form-field">
        <text>第 {{ i + 1 }} 级</text>
        <SelectField v-model="reviewers[i]" :options="admins" />
      </view>
      <button v-if="demo.has('review')" class="button" @click="saveReviewers">保存审核配置</button>
      <text class="subtle">
        当前演示管理员可承担六个节点；每级仍需分别提交意见，已有申请保留提交时的人员配置。
      </text>
    </view>
  </view>
  <view v-else-if="mode === 'mapAnalysis'" class="panel">
    <view class="panel-title">预设爆管影响分析</view>
    <SelectField v-model="burstId" :options="bursts.map((b) => ({ value: b.id, label: b.name }))" />
    <template v-if="burst">
      <view class="notice">固定演示影响关系，非真实水力计算。预计影响 {{ burst.households }} 户。</view>
      <view class="map-container"><MapCanvas :active="active" :config="burstMap" /></view>
      <view class="toolbar">
        <button class="button" @click="go('map', { pipeId: burst.pipeId })">定位爆管管线</button>
        <button class="button secondary" v-if="demo.has('export')" @click="exportBurst">导出影响清单</button>
      </view>
      <view class="panel-title">建议关闭阀门</view>
      <view v-for="id in burst.valveIds" :key="id" class="list-item link" @click="go('facility', { id })">
        {{ nameOf(id) }} ↗
      </view>
      <view class="panel-title">影响设施</view>
      <view v-for="id in burst.facilityIds" :key="id" class="list-item link" @click="go('facility', { id })">
        {{ nameOf(id) }} ↗
      </view>
    </template>
  </view>
  <view v-else class="panel">
    <view class="toolbar">
      <input class="field search" v-model="search" placeholder="筛选操作人或内容" />
      <button class="button" v-if="demo.has('export')" @click="exportLogs">导出日志</button>
    </view>
    <view v-for="(h, i) in logs.slice().reverse().slice(0, 200)" :key="i" class="list-item">
      {{ h.text }}
      <text class="subtle">{{ h.actor }} · {{ formatTime(h.time) }}</text>
    </view>
    <view v-if="!logs.length" class="empty">暂无操作日志</view>
    <text class="subtle">显示最近 200 条，导出包含全部筛选结果。</text>
  </view>
</template>
<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { useDemo } from '../stores/demo'
import { regions, users, clone } from '../repositories/seed'
import { go } from '../navigation/routeMap'
import { personName } from '../domain/presentation'
import { formatTime } from '../domain/metrics'
import { exportCsv } from '../platform/export'
import SelectField from './SelectField.vue'
import MapCanvas from './MapCanvas.vue'
import type { Change } from '../domain/phase2'
const props = defineProps<{ mode: string; query: Record<string, string>; active: boolean }>(),
  demo = useDemo()
const kind = ref<Change['kind']>(props.query.kind === 'pipe' ? 'pipe' : 'facility'),
  action = ref<Change['action']>('edit'),
  target = ref(props.query.id || demo.facilities[0]?.id || ''),
  form = ref<any>({}),
  x = ref('0'),
  y = ref('0'),
  diameter = ref('200'),
  pathText = ref(''),
  message = ref(''),
  rejectedId = ref('')
const objects = computed(() =>
    kind.value === 'facility'
      ? demo.facilities
      : demo.state.phase2.pipes.filter((p) => demo.user?.regionIds.includes(p.regionId)),
  ),
  regionOptions = computed(() =>
    regions.filter((r) => demo.user?.regionIds.includes(r.id)).map((r) => ({ value: r.id, label: r.name })),
  ),
  facilityOptions = computed(() => demo.facilities.map((f) => ({ value: f.id, label: f.name })))
function load(v: any) {
  form.value = clone(
    v || {
      id: '',
      name: '',
      type: 'sensor',
      regionId: regionOptions.value[0]?.value,
      dmaId: demo.state.phase2.dmas[0]?.id,
      position: [100, 100],
      status: 'normal',
      deviceId: null,
      valveState: 'open',
      description: '',
      fromFacilityId: demo.facilities[0]?.id,
      toFacilityId: demo.facilities[1]?.id,
      path: [],
      diameterMm: 200,
    },
  )
  x.value = String(form.value.position?.[0] || 100)
  y.value = String(form.value.position?.[1] || 100)
  diameter.value = String(form.value.diameterMm || 200)
  pathText.value = (form.value.path || []).map((p: number[]) => p.join(',')).join(';')
}
watch(
  [kind, action, target],
  () => load(action.value === 'add' ? null : objects.value.find((o) => o.id === target.value)),
  { immediate: true },
)
watch(kind, () => {
  target.value = objects.value[0]?.id || ''
  rejectedId.value = ''
})
const changes = computed(() =>
    demo.state.phase2.changes
      .filter((c) => demo.user?.roleId === 'admin' || c.createdBy === demo.user?.id)
      .slice()
      .reverse(),
  ),
  changeId = ref(''),
  change = computed(() => changes.value.find((c) => c.id === changeId.value)),
  reason = ref('')
async function submit() {
  const data = clone(form.value)
  if (kind.value === 'facility') {
    data.position = [Number(x.value), Number(y.value)]
  } else {
    data.diameterMm = Number(diameter.value)
    data.path = pathText.value
      .split(';')
      .filter(Boolean)
      .map((p) => p.split(',').map(Number))
    if (!data.path.length)
      data.path = [
        demo.facilities.find((f) => f.id === data.fromFacilityId)?.position,
        demo.facilities.find((f) => f.id === data.toFacilityId)?.position,
      ]
  }
  if (kind.value === 'pipe' && data.path.length >= 2) {
    data.path[0] = demo.facilities.find((f) => f.id === data.fromFacilityId)?.position
    data.path[data.path.length - 1] = demo.facilities.find((f) => f.id === data.toFacilityId)?.position
  }
  const r = await demo.run((e) => e.propose(kind.value, action.value, data, rejectedId.value))
  if (r.success) {
    changeId.value = r.data
    message.value = '申请已提交，有效台账尚未改变'
    rejectedId.value = ''
  }
}
async function review(approve: boolean) {
  if (!change.value) return
  const r = await demo.run((e) => e.review(change.value!.id, change.value!.stage, approve, reason.value))
  if (r.success) reason.value = ''
}
async function revise() {
  const c = change.value!
  kind.value = c.kind
  action.value = c.action
  target.value = c.targetId
  await Promise.resolve()
  load(c.proposed)
  rejectedId.value = c.id
}
const fields = [
    { id: 'name', name: '名称' },
    { id: 'type', name: '类型' },
    { id: 'regionId', name: '区域' },
    { id: 'dmaId', name: 'DMA' },
    { id: 'status', name: '状态' },
    { id: 'description', name: '说明' },
  ],
  selectedFields = ref([...demo.state.phase2.layers.draftFields]),
  reviewers = ref([...demo.state.phase2.reviewers]),
  admins = users.filter((u) => u.roleId === 'admin').map((u) => ({ value: u.id, label: u.displayName }))
function toggleField(id: string) {
  selectedFields.value = selectedFields.value.includes(id)
    ? selectedFields.value.filter((v) => v !== id)
    : [...selectedFields.value, id]
}
async function layer(a: 'draft' | 'sync' | 'publish') {
  const r = await demo.run((e) => e.layer(a, selectedFields.value))
  if (r.success) uni.showToast({ title: '本地配置已更新', icon: 'none' })
}
async function saveReviewers() {
  await demo.run((e) => e.configureReviewers(reviewers.value))
}
const bursts = computed(() =>
    demo.state.phase2.bursts.filter((b) => demo.facilities.some((f) => b.facilityIds.includes(f.id))),
  ),
  burstId = ref(bursts.value[0]?.id || ''),
  burst = computed(() => bursts.value.find((b) => b.id === burstId.value)),
  nameOf = (id: string) => demo.facilities.find((f) => f.id === id)?.name || id
const burstMap = computed(() => ({
  regions: regions.filter((r) => demo.user?.regionIds.includes(r.id)),
  facilities: demo.facilities,
  pipes: demo.state.phase2.pipes.filter((p) => demo.user?.regionIds.includes(p.regionId)),
  alarmIds: burst.value?.facilityIds,
  selected: burst.value?.pipeId,
  theme: 'dark',
}))
function exportBurst() {
  if (burst.value)
    exportCsv('爆管影响清单', [
      ['场景', '设施', '建议关阀'],
      ...burst.value.facilityIds.map((id) => [
        burst.value!.name,
        nameOf(id),
        burst.value!.valveIds.includes(id) ? '是' : '否',
      ]),
    ])
}
const search = ref(''),
  logs = computed(() =>
    demo.state.logs.filter(
      (h) =>
        (demo.user?.roleId === 'admin' || h.actor === demo.user?.displayName) &&
        (h.actor + h.text).includes(search.value),
    ),
  )
function exportLogs() {
  exportCsv('操作日志', [['时间', '操作人', '内容'], ...logs.value.map((h) => [h.time, h.actor, h.text])])
}
</script>
