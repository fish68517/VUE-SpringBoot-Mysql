<template>
  <view class="toolbar">
    <button class="button secondary" @click="go('inspection')">任务台账</button>
    <button v-if="!mobile || demo.has('dispatch')" class="button secondary" @click="go('inspectionConfig')">
      模板与路线
    </button>
    <button class="button secondary" @click="go('inspectionReplay')">人员轨迹与统计</button>
  </view>
  <view v-if="mode === 'inspectionConfig'" class="equal-columns">
    <view class="panel">
      <view class="panel-title">巡检模板</view>
      <view v-for="t in demo.state.phase2.templates" :key="t.id" class="list-item">
        {{ t.id }} · {{ t.name }}
        <text class="subtle">
          {{ t.fields.map((f) => f.label + (f.required ? ' *' : '')).join('、') || '基础结果与说明' }}
        </text>
      </view>
      <template v-if="demo.has('dispatch')">
        <view class="panel-title">新增模板</view>
        <input class="field" v-model="templateId" placeholder="模板编号，如 TPL-003" />
        <input class="field" v-model="templateName" placeholder="模板名称" />
        <view v-for="(f, i) in fields" :key="i" class="check-card">
          <input class="field" v-model="f.id" placeholder="字段编号，英文" />
          <input class="field" v-model="f.label" placeholder="字段名称" />
          <SelectField
            v-model="f.type"
            :options="[
              { value: 'text', label: '文本' },
              { value: 'number', label: '数值' },
              { value: 'choice', label: '单选' },
            ]"
          />
          <input
            v-if="f.type === 'choice'"
            class="field"
            :value="f.options.join('、')"
            @input="updateOptions(f, $event)"
            placeholder="选项，用顿号分隔"
          />
          <view class="toolbar">
            <view :class="['choice', { active: f.required }]" @click="f.required = !f.required">
              必填 {{ f.required ? '✓' : '' }}
            </view>
            <button class="button secondary compact" @click="fields.splice(i, 1)">移除字段</button>
          </view>
        </view>
        <view class="toolbar">
          <button class="button secondary" @click="addField">添加字段</button>
          <button class="button" @click="saveTemplate">保存模板</button>
        </view>
      </template>
      <view class="notice">
        所有模板均要求检查结果与说明；发布任务时复制模板，后续配置不会改变已发布任务。
      </view>
    </view>
    <view class="panel">
      <view class="panel-title">区域与巡检路线</view>
      <SelectField
        v-model="routeId"
        :options="[
          { value: '', label: '新增路线' },
          ...demo.state.phase2.routes.map((r) => ({ value: r.id, label: r.name })),
        ]"
      />
      <input class="field" v-model="routeCode" placeholder="路线编号" />
      <input class="field" v-model="routeName" placeholder="路线名称" />
      <SelectField v-model="regionId" :options="regions.map((r) => ({ value: r.id, label: r.name }))" />
      <view class="inline-options">
        <view
          v-for="f in regionFacilities"
          :key="f.id"
          :class="['choice', { active: facilityIds.includes(f.id) }]"
          @click="toggleFacility(f.id)"
        >
          {{ facilityIds.includes(f.id) ? '✓ ' : '' }}{{ f.name }}
        </view>
      </view>
      <text class="subtle">按点击顺序形成路线：{{ facilityIds.join(' → ') }}</text>
      <button v-if="demo.has('dispatch')" class="button" @click="saveRoute">保存路线与区域</button>
      <button class="button secondary" @click="go('taskEdit', { routeId })">按当前路线新建任务</button>
    </view>
  </view>
  <view v-else>
    <view class="kpi-grid">
      <view class="kpi">
        <text class="kpi-label">有效任务完成率</text>
        <text class="kpi-number">{{ percent(completion) }}</text>
        <text class="subtle">取消任务不进入分母</text>
      </view>
      <view class="kpi">
        <text class="kpi-label">轨迹里程</text>
        <text class="kpi-number">{{ total.km.toFixed(2) }}</text>
        <text class="subtle">km · 示意坐标 1 单位 = 10 m</text>
      </view>
      <view class="kpi">
        <text class="kpi-label">轨迹累计时长</text>
        <text class="kpi-number">{{ total.hours.toFixed(2) }}</text>
        <text class="subtle">小时 · 轨迹记录</text>
      </view>
      <view class="kpi">
        <text class="kpi-label">已检查设施项</text>
        <text class="kpi-number">{{ checked }}</text>
      </view>
    </view>
    <view class="panel">
      <view class="toolbar">
        <SelectField v-model="taskId" :options="demo.tasks.map((t) => ({ value: t.id, label: t.name }))" />
        <button class="button" :disabled="!track?.points.length" @click="togglePlay">
          {{ playing ? '暂停' : '播放轨迹' }}
        </button>
        <SelectField
          v-model="speed"
          :options="[
            { value: '1', label: '1 倍' },
            { value: '2', label: '2 倍' },
            { value: '4', label: '4 倍' },
          ]"
        />
        <button class="button secondary" @click="restart">回到起点</button>
        <button v-if="demo.has('export')" class="button secondary" @click="exportTasks">导出巡检统计</button>
      </view>
      <view class="map-container">
        <MapCanvas :config="mapConfig" :active="active" @select="selectPerson" />
      </view>
      <slider :value="cursor" :max="Math.max(1, (track?.points.length || 1) - 1)" :step="1" @change="seek" />
      <text class="subtle">
        {{ track ? formatTime(track.points[cursor]?.time || '') : '当前任务没有轨迹记录' }} · 执行人
        {{ personName(task?.assigneeId || '') }} · 巡检轨迹
      </text>
      <view class="panel-title">人员分布与工作量</view>
      <view v-for="u in personnel" :key="u.id" class="list-item" @click="choosePerson(u.id)">
        <text class="link">{{ u.name }} ↗</text>
        <text class="subtle">
          {{ u.tasks }} 个有效任务 · 完成 {{ u.completed }} · {{ u.km.toFixed(2) }} km ·
          {{ u.hours.toFixed(2) }} h
        </text>
      </view>
    </view>
  </view>
</template>
<script setup lang="ts">
import { computed, ref, watch, onUnmounted } from 'vue'
import { useMobileClient } from '../platform/client'
import { useDemo } from '../stores/demo'
import { regions, users } from '../repositories/seed'
import { trackStats } from '../domain/phase2'
import type { Field } from '../domain/phase2'
import { personName } from '../domain/presentation'
import { formatTime, percent } from '../domain/metrics'
import { go } from '../navigation/routeMap'
import { exportCsv } from '../platform/export'
import SelectField from './SelectField.vue'
import MapCanvas from './MapCanvas.vue'
const mobile = useMobileClient()
const props = defineProps<{ mode: string; query: Record<string, string>; active: boolean }>(),
  demo = useDemo(),
  templateId = ref(''),
  templateName = ref(''),
  fields = ref<Field[]>([])
function updateOptions(f: Field, e: any) {
  f.options = e.detail.value.split('、')
}
function addField() {
  fields.value.push({
    id: 'field' + (fields.value.length + 1),
    label: '',
    type: 'text',
    required: true,
    options: [],
  })
}
async function saveTemplate() {
  const r = await demo.run((e) =>
    e.saveTemplate({ id: templateId.value, name: templateName.value, fields: fields.value }),
  )
  if (r.success) {
    templateId.value = ''
    templateName.value = ''
    fields.value = []
    uni.showToast({ title: '模板已保存' })
  }
}
const routeId = ref(''),
  routeCode = ref(''),
  routeName = ref(''),
  regionId = ref(regions[0].id),
  facilityIds = ref<string[]>([]),
  regionFacilities = computed(() => demo.facilities.filter((f) => f.regionId === regionId.value))
watch(regionId, () => {
  facilityIds.value = facilityIds.value.filter((id) => regionFacilities.value.some((f) => f.id === id))
})
watch(routeId, (id) => {
  const r = demo.state.phase2.routes.find((r) => r.id === id)
  routeCode.value = r?.id || ''
  routeName.value = r?.name || ''
  regionId.value = r?.regionId || regions[0].id
  facilityIds.value = [...(r?.facilityIds || [])]
})
function toggleFacility(id: string) {
  facilityIds.value = facilityIds.value.includes(id)
    ? facilityIds.value.filter((v) => v !== id)
    : [...facilityIds.value, id]
}
async function saveRoute() {
  const r = await demo.run((e) =>
    e.saveRoute({
      id: routeCode.value,
      name: routeName.value,
      regionId: regionId.value,
      facilityIds: facilityIds.value,
    }),
  )
  if (r.success) {
    routeId.value = routeCode.value
    uni.showToast({ title: '路线已保存' })
  }
}
const taskId = ref(props.query.id || demo.tasks[0]?.id || ''),
  task = computed(() => demo.tasks.find((t) => t.id === taskId.value)),
  track = computed(() => demo.state.phase2.tracks.find((t) => t.taskId === task.value?.id)),
  cursor = ref(0),
  playing = ref(false),
  speed = ref('1')
let timer: ReturnType<typeof setInterval> | undefined
function pause() {
  if (timer) clearInterval(timer)
  timer = undefined
  playing.value = false
}
function togglePlay() {
  if (playing.value) {
    pause()
    return
  }
  if (!track.value?.points.length) return
  if (cursor.value >= track.value.points.length - 1) cursor.value = 0
  playing.value = true
  timer = setInterval(() => {
    cursor.value = Math.min(cursor.value + Number(speed.value), track.value!.points.length - 1)
    if (cursor.value >= track.value!.points.length - 1) pause()
  }, 500)
}
function restart() {
  pause()
  cursor.value = 0
}
function seek(e: any) {
  pause()
  cursor.value = e.detail.value
}
watch(taskId, restart)
watch(
  () => props.active,
  (v) => {
    if (!v) pause()
  },
)
onUnmounted(pause)
const valid = computed(() => demo.tasks.filter((t) => t.status !== 'cancelled')),
  completion = computed(() =>
    valid.value.length
      ? valid.value.filter((t) => t.status === 'completed').length / valid.value.length
      : null,
  ),
  checked = computed(() => valid.value.reduce((n, t) => n + t.checks.filter((c) => c.result).length, 0)),
  total = computed(() =>
    valid.value.reduce(
      (v, t) => {
        const x = trackStats(demo.state.phase2.tracks.find((r) => r.taskId === t.id))
        return { km: v.km + x.km, hours: v.hours + x.hours }
      },
      { km: 0, hours: 0 },
    ),
  )
const personnel = computed(() =>
  users
    .filter((u) => u.roleId === 'operator' && (demo.user?.roleId !== 'operator' || u.id === demo.user.id))
    .map((u) => {
      const ts = valid.value.filter((t) => t.assigneeId === u.id)
      const sums = ts.reduce(
        (v, t) => {
          const x = trackStats(demo.state.phase2.tracks.find((r) => r.taskId === t.id))
          return { km: v.km + x.km, hours: v.hours + x.hours }
        },
        { km: 0, hours: 0 },
      )
      return {
        id: u.id,
        name: u.displayName,
        tasks: ts.length,
        completed: ts.filter((t) => t.status === 'completed').length,
        ...sums,
      }
    }),
)
const mapConfig = computed(() => ({
  regions: regions.filter((r) => demo.user?.regionIds.includes(r.id)),
  facilities: demo.facilities,
  pipes: demo.state.phase2.pipes.filter((p) => demo.user?.regionIds.includes(p.regionId)),
  theme: 'dark',
  track: track.value?.points.slice(0, cursor.value + 1).map((p) => p.position) || [],
  people: personnel.value
    .map((u) => {
      const t = demo.tasks.find((t) => t.assigneeId === u.id),
        tr = demo.state.phase2.tracks.find((r) => r.taskId === t?.id)
      return { id: u.id, name: u.name, position: tr?.points[tr.points.length - 1]?.position }
    })
    .filter((u) => u.position),
}))
function choosePerson(id: string) {
  const t = demo.tasks.find((t) => t.assigneeId === id)
  if (t) taskId.value = t.id
}
function selectPerson(e: { type: string; id: string }) {
  if (e.type === 'person') choosePerson(e.id)
}
function exportTasks() {
  exportCsv('巡检统计', [
    ['任务', '状态', '执行人', '已检查', '里程 km', '时长 h'],
    ...demo.tasks.map((t) => {
      const s = trackStats(demo.state.phase2.tracks.find((r) => r.taskId === t.id))
      return [
        t.name,
        t.status,
        personName(t.assigneeId),
        t.checks.filter((c) => c.result).length,
        s.km.toFixed(2),
        s.hours.toFixed(2),
      ]
    }),
  ])
}
</script>
