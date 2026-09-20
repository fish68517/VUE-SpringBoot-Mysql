<template>
  <view v-if="mode === 'energy'" class="panel">
    <view class="toolbar">
      <SelectField v-model="deviceId" :options="devices.map((d) => ({ value: d.id, label: d.name }))" />
      <picker mode="date" :value="date" @change="date = $event.detail.value">
        <view class="select-field">{{ date }}</view>
      </picker>
      <SelectField
        v-model="period"
        :options="[
          { value: 'day', label: '日报' },
          { value: 'month', label: '月报' },
        ]"
      />
      <button class="button secondary" @click="go('reports')">完整报表</button>
    </view>
    <template v-if="device">
      <view class="kpi-grid">
        <view class="kpi">
          <text class="kpi-label">运行时长</text>
          <text class="kpi-number">{{ hours.toFixed(2) }}</text>
          <text class="subtle">小时 · 北京时间区间</text>
        </view>
        <view class="kpi">
          <text class="kpi-label">有效能耗</text>
          <text class="kpi-number">{{ consumption.kwh?.toFixed(2) ?? '—' }}</text>
          <text class="subtle">kWh · 有效 {{ consumption.hours }} 小时</text>
        </view>
        <view class="kpi">
          <text class="kpi-label">采样间隔</text>
          <text class="kpi-number">{{ device.samplingIntervalSec }}</text>
          <text class="subtle">秒 · 演示时间驱动</text>
        </view>
        <view class="kpi">
          <text class="kpi-label">终端状态</text>
          <text class="kpi-number">{{ device.status === 'online' ? '在线' : '离线' }}</text>
        </view>
      </view>
      <view v-if="demo.has('write')" class="toolbar">
        <input class="field" v-model="interval" type="number" placeholder="采样间隔 1—300 秒" />
        <SelectField
          v-model="on"
          :options="[
            { value: 'on', label: '模拟开机' },
            { value: 'off', label: '模拟关机' },
          ]"
        />
        <button class="button" @click="saveDevice">保存模拟设置</button>
        <text class="subtle">操作时间 {{ formatTime(demo.state.simulationTime) }}</text>
      </view>
      <view class="panel-title">运行时长分布（小时）</view>
      <ChartView
        :labels="runtimeRows.map((r) => r.label)"
        :values="runtimeRows.map((r) => r.value)"
        :active="active"
        name="h"
      />
      <view class="panel-title">节能方案对比</view>
      <SelectField
        v-model="planId"
        :options="demo.state.phase2.energyPlans.map((p) => ({ value: p.id, label: p.name }))"
      />
      <view v-if="plan" class="notice">
        {{ plan.name }}：基准有效能耗 × {{ plan.factor }} =
        {{
          consumption.kwh === null ? '无可比数据' : (consumption.kwh * plan.factor).toFixed(2) + ' kWh'
        }}。压力约束 {{ plan.minPressure }}—{{ plan.maxPressure }}
        MPa。仅为预设比较，不用压力单独推算能耗，不构成实际调度指令。
      </view>
      <view class="panel-title">启停事件</view>
      <view v-for="(e, i) in events" :key="i" class="list-item">
        {{ formatTime(e.time) }} · {{ e.on ? '开机' : '关机' }}
      </view>
      <view class="notice">
        优先累计电表差值；缺电表时对相邻功率样本积分。超过一小时的缺测间隔不补值，负电表跳变不计入。无功率样本的终端能耗显示“—”。
      </view>
    </template>
    <view v-else class="empty">当前区域没有设备</view>
  </view>
  <view v-else-if="mode === 'mobileReport'" class="panel">
    <view class="panel-title">移动事件上报</view>
    <SelectField
      v-model="facilityId"
      :options="demo.facilities.map((f) => ({ value: f.id, label: f.name }))"
    />
    <input class="field" v-model="title" placeholder="事件标题（必填）" />
    <textarea class="field" v-model="description" placeholder="事件描述（必填）" />
    <text class="subtle">上报时间：{{ formatTime(demo.state.simulationTime) }} · 本机模拟时钟</text>
    <button v-if="demo.has('write')" class="button" @click="report">上报并建立工单</button>
    <view class="toolbar">
      <button class="button secondary" @click="go('mobileMap', { facilityId })">地图定位</button>
      <button class="button secondary" @click="go('mapChanges', { id: facilityId })">设施纠错</button>
      <button class="button secondary" @click="go('taskEdit')">新建本人任务</button>
    </view>
  </view>
  <view v-else class="stack">
    <view class="equal-columns">
      <view class="panel">
        <view class="panel-title">我的门户卡片</view>
        <view class="list-item" v-for="(id, i) in cards" :key="id">
          <view class="toolbar">
            <text>{{ titles[id] }}</text>
            <button class="button secondary compact" :disabled="i === 0" @click="moveCard(i)">上移</button>
            <button class="button secondary compact" @click="cards.splice(i, 1)">隐藏</button>
          </view>
        </view>
        <view class="inline-options">
          <view
            class="choice"
            v-for="id in cardOptions.filter((id) => !cards.includes(id))"
            :key="id"
            @click="cards.push(id)"
          >
            ＋ {{ titles[id] }}
          </view>
        </view>
        <button class="button" @click="saveCards">保存门户布局</button>
      </view>
      <view class="panel">
        <view class="panel-title">模拟通知设置</view>
        <view class="inline-options">
          <view :class="['choice', { active: notice.sms }]" @click="notice.sms = !notice.sms">
            短信 {{ notice.sms ? '✓' : '' }}
          </view>
          <view :class="['choice', { active: notice.email }]" @click="notice.email = !notice.email">
            邮件 {{ notice.email ? '✓' : '' }}
          </view>
          <view
            v-for="l in levels"
            :key="l.id"
            :class="['choice', { active: notice.levels.includes(l.id) }]"
            @click="toggleLevel(l.id)"
          >
            {{ l.name }}
          </view>
        </view>
        <button v-if="demo.has('dispatch')" class="button" @click="saveNotices">保存通知配置</button>
        <SelectField
          v-model="alarmId"
          :options="demo.alarms.map((a) => ({ value: a.id, label: a.title + ' ' + a.id }))"
        />
        <button v-if="demo.has('write')" class="button secondary" @click="notify">生成模拟通知记录</button>
        <view class="notice">每次只生成已开启渠道且匹配级别的本地记录，不发送真实短信或邮件。</view>
        <view v-for="n in notifications.slice(-10).reverse()" :key="n.id" class="list-item">
          {{ n.alarmId }} · {{ n.channel === 'sms' ? '短信' : '邮件' }} · 仅模拟发送
        </view>
      </view>
    </view>
    <view class="panel">
      <view class="panel-title">本地快照迁移</view>
      <view class="notice">
        包含本地业务状态与配置，不含账号、密码和登录会话。导入替换当前设备的演示状态；两个设备不会自动同步。先导出备份，再导入同版本快照。
      </view>
      <template v-if="demo.has('reset')">
        <view class="toolbar">
          <button class="button" @click="exportSnapshot">导出 JSON 快照</button>
          <button class="button secondary" @click="readSnapshot">读取快照文件</button>
        </view>
        <view v-if="snapshotFile" class="notice">
          已读取快照文件 {{ Math.ceil(snapshotFile.length / 1024) }} KB，点击下方按钮校验并导入。
          <text class="link" @click="snapshotFile = ''">清除文件</text>
        </view>
        <textarea
          v-if="!snapshotFile"
          class="field snapshot-input"
          v-model="snapshot"
          :maxlength="-1"
          placeholder="也可以粘贴同版本 JSON 快照"
        />
        <button class="button danger" @click="importSnapshot">校验并导入快照</button>
      </template>
      <view v-else class="empty">完整快照包含所有区域，仅管理员可导出或导入</view>
    </view>
  </view>
</template>
<script setup lang="ts">
import { computed, ref, reactive, watch } from 'vue'
import { useDemo } from '../stores/demo'
import { clone } from '../repositories/seed'
import { energy, runtime } from '../domain/phase2'
import { formatTime } from '../domain/metrics'
import { confirm } from '../domain/presentation'
import { go, titles } from '../navigation/routeMap'
import { downloadText, chooseText } from '../platform/files'
import SelectField from './SelectField.vue'
import ChartView from './ChartView.vue'
const props = defineProps<{ mode: string; query: Record<string, string>; active: boolean }>(),
  demo = useDemo(),
  devices = computed(() =>
    demo.state.phase2.devices.filter((d) => demo.facilities.some((f) => f.id === d.facilityId)),
  ),
  deviceId = ref(
    props.query.deviceId || devices.value.find((d) => d.ratedPowerKw > 0)?.id || devices.value[0]?.id || '',
  ),
  device = computed(() => devices.value.find((d) => d.id === deviceId.value)),
  date = ref('2026-09-20'),
  period = ref('day'),
  interval = ref('60'),
  on = ref('on'),
  planId = ref('PLAN-2'),
  plan = computed(() => demo.state.phase2.energyPlans.find((p) => p.id === planId.value))
watch(
  device,
  (d) => {
    interval.value = String(d?.samplingIntervalSec || 60)
    on.value = d?.status === 'online' ? 'on' : 'off'
  },
  { immediate: true },
)
const range = computed(() => {
    const from = Date.parse(
      (period.value === 'day' ? date.value : date.value.slice(0, 7) + '-01') + 'T00:00:00+08:00',
    )
    const dt = new Date(from + 8 * 3600000)
    const to =
      period.value === 'day'
        ? from + 86400000
        : Date.UTC(dt.getUTCFullYear(), dt.getUTCMonth() + 1, 1) - 8 * 3600000
    return { from, to }
  }),
  hours = computed(() =>
    runtime(
      demo.state.phase2.deviceEvents,
      deviceId.value,
      range.value.from,
      Math.min(range.value.to, Date.parse(demo.state.simulationTime)),
    ),
  ),
  consumption = computed(() =>
    energy(demo.state.phase2.power, deviceId.value, range.value.from, range.value.to),
  ),
  events = computed(() =>
    demo.state.phase2.deviceEvents
      .filter(
        (e) =>
          e.deviceId === deviceId.value &&
          Date.parse(e.time) >= range.value.from &&
          Date.parse(e.time) < range.value.to,
      )
      .slice()
      .sort((a, b) => Date.parse(b.time) - Date.parse(a.time)),
  ),
  runtimeRows = computed(() => {
    const step = period.value === 'day' ? 3600000 : 86400000
    const out = []
    for (
      let t = range.value.from;
      t < Math.min(range.value.to, Date.parse(demo.state.simulationTime));
      t += step
    )
      out.push({
        label: new Date(t + 8 * 3600000)
          .toISOString()
          .slice(period.value === 'day' ? 11 : 5, period.value === 'day' ? 16 : 10),
        value: runtime(
          demo.state.phase2.deviceEvents,
          deviceId.value,
          t,
          Math.min(t + step, range.value.to, Date.parse(demo.state.simulationTime)),
        ),
      })
    return out
  })
async function saveDevice() {
  const r = await demo.run((e) => e.deviceSetting(deviceId.value, Number(interval.value), on.value === 'on'))
  if (r.success) uni.showToast({ title: '模拟设置已保存', icon: 'none' })
}
const facilityId = ref(props.query.facilityId || demo.facilities[0]?.id || ''),
  title = ref(''),
  description = ref('')
async function report() {
  const r = await demo.run((e) =>
    e.createOrder({
      title: title.value,
      type: 'repair',
      facilityId: facilityId.value,
      assigneeId: demo.user?.roleId === 'operator' ? demo.user.id : '',
      description: description.value,
    }),
  )
  if (r.success) go('orderDetail', { id: r.data }, true)
}
const cardOptions = ['alarms', 'orders', 'inspection', 'map', 'video', 'energy', 'mobileReport'],
  cards = ref([...(demo.state.phase2.portalCards[demo.user?.id || ''] || cardOptions.slice(0, 4))]),
  notice = reactive(clone(demo.state.phase2.noticeConfig)),
  levels = [
    { id: 'urgent', name: '紧急' },
    { id: 'important', name: '重要' },
    { id: 'normal', name: '一般' },
  ],
  alarmId = ref(demo.alarms[0]?.id || ''),
  notifications = computed(() =>
    demo.state.notifications.filter((n) => demo.alarms.some((a) => a.id === n.alarmId)),
  )
function moveCard(i: number) {
  const v = cards.value.splice(i, 1)[0]
  cards.value.splice(i - 1, 0, v)
}
function toggleLevel(id: string) {
  notice.levels = notice.levels.includes(id) ? notice.levels.filter((v) => v !== id) : [...notice.levels, id]
}
async function saveCards() {
  const r = await demo.run((e) => e.cards(cards.value))
  if (r.success) uni.showToast({ title: '门户已保存' })
}
async function saveNotices() {
  await demo.run((e) => e.notices(notice))
}
async function notify() {
  await demo.run((e) => e.notify(alarmId.value))
}
const snapshot = ref(''),
  snapshotFile = ref('')
async function exportSnapshot() {
  const r = await demo.run((e) => e.exportSnapshot())
  if (r.success) await downloadText('智慧供水快照.json', r.data, 'application/json')
}
async function readSnapshot() {
  try {
    snapshotFile.value = await chooseText()
  } catch (e) {
    uni.showToast({ title: e instanceof Error ? e.message : '读取失败', icon: 'none' })
  }
}
async function importSnapshot() {
  if (!(await confirm('导入本地快照', '将替换当前设备全部演示业务状态，请确认已导出备份。'))) return
  const r = await demo.run((e) => e.importSnapshot(snapshotFile.value || snapshot.value))
  if (r.success) {
    snapshot.value = ''
    snapshotFile.value = ''
    uni.showToast({ title: '快照已导入' })
  }
}
</script>
<style scoped>
.snapshot-input {
  height: 180px;
  margin: 16px 0;
}
</style>
