<template>
  <view>
    <view class="panel">
      <view class="toolbar">
        <SelectField
          v-model="type"
          :options="[
            { value: 'usage', label: '居民用水量' },
            { value: 'pressure', label: '设施压力' },
            { value: 'energy', label: '设备能耗' },
            { value: 'runtime', label: '运行时长' },
            { value: 'inspection', label: '巡检业务' },
            { value: 'network', label: '管网变更' },
          ]"
        />
        <SelectField
          v-model="period"
          :options="[
            { value: 'day', label: '日报' },
            { value: 'month', label: '月报' },
          ]"
        />
        <picker mode="date" :value="date" @change="date = $event.detail.value">
          <view class="select-field">{{ period === 'day' ? date : date.slice(0, 7) }} ⌄</view>
        </picker>
        <SelectField v-if="type !== 'pressure'" v-model="regionId" :options="regionOptions" />
        <SelectField v-else v-model="facilityId" :options="facilityOptions" />
        <view class="spacer" />
        <button v-if="demo.has('export')" class="button" @click="exportData">⇩ 导出当前报表</button>
      </view>
      <view class="notice">
        {{ reportNote }}
        数据期间：2026 年 9 月；压力样本日期：9 月 20 日。
      </view>
      <view class="panel-title">
        {{ reportName }}{{ period === 'day' ? '日报' : '月报' }}
        <text class="subtle">{{ rows.length }} 个统计项 · {{ unit }}</text>
      </view>
      <ChartView
        v-if="rows.length"
        :active="active"
        :labels="rows.map((r) => r.label)"
        :values="rows.map((r) => r.value)"
        :name="unit"
      />
      <view v-else class="empty">该时段无数据</view>
      <view class="table" v-if="rows.length">
        <view class="table-row header report-row">
          <text>区域 / 时间</text>
          <text>指标数值</text>
          <text>单位</text>
          <text>统计口径</text>
        </view>
        <view v-for="r in rows" :key="r.label" class="table-row report-row">
          <text>{{ r.label }}</text>
          <text>{{ r.value.toLocaleString() }}</text>
          <text>{{ unit }}</text>
          <text>
            {{ reportName }}
          </text>
        </view>
      </view>
    </view>
    <view class="notice" style="margin-top: 20px">
      用水按区域合计；巡检按计划日期统计当前完成状态；管网按审核生效日期统计变更；能耗仅累计有效样本，运行时长截止当前业务时间。空时段不填造数据。
    </view>
  </view>
</template>
<script setup lang="ts">
import { ref, computed } from 'vue'
import { businessReport } from '../domain/reports'
import { useDemo } from '../stores/demo'
import { regions, pressureSeries, usage } from '../repositories/seed'
import { exportCsv } from '../platform/export'
import SelectField from './SelectField.vue'
import ChartView from './ChartView.vue'
defineProps<{ active: boolean }>()
const demo = useDemo(),
  type = ref('usage'),
  period = ref('day'),
  date = ref('2026-09-20'),
  regionId = ref(''),
  facilityId = ref(demo.facilities.find((f) => pressureSeries.some((s) => s.entityId === f.id))?.id || '')
const visibleRegions = computed(() => regions.filter((r) => demo.user?.regionIds.includes(r.id))),
  regionOptions = computed(() => [
    { value: '', label: '全部授权区域' },
    ...visibleRegions.value.map((r) => ({ value: r.id, label: r.name })),
  ]),
  facilityOptions = computed(() =>
    demo.facilities
      .filter((f) => pressureSeries.some((s) => s.entityId === f.id))
      .map((f) => ({ value: f.id, label: f.name })),
  ),
  unit = computed(
    () =>
      ({ usage: 'm³', pressure: 'MPa', energy: 'kWh', runtime: 'h', inspection: '项', network: '项' })[
        type.value
      ] || '',
  )
const reportName = computed(
  () =>
    ({
      usage: '用水量',
      pressure: '压力',
      energy: '设备能耗',
      runtime: '设备运行时长',
      inspection: '巡检业务',
      network: '管网变更',
    })[type.value] || '',
)
const reportNote = computed(() =>
  type.value === 'pressure'
    ? '压力按有效样本统计，不对缺失日期补零。'
    : type.value === 'network'
      ? '按六级审核最终生效时间统计新增、修改、删除；未生效申请不计入。'
      : '按选定日期、区域与有效记录统计。',
)
const rows = computed(() => {
  if (!['usage', 'pressure'].includes(type.value))
    return businessReport(
      demo.state,
      demo.user,
      type.value,
      date.value,
      period.value === 'month',
      regionId.value,
    )
  if (type.value === 'usage') {
    const source = usage.filter((r) =>
      period.value === 'day' ? r.date === date.value : r.date.startsWith(date.value.slice(0, 7)),
    )
    if (!source.length) return []
    const selected = visibleRegions.value.filter((r) => !regionId.value || r.id === regionId.value)
    return period.value === 'day'
      ? selected.map((r) => ({
          label: r.name,
          value: source[0].regions.find((v) => v.regionId === r.id)!.volume,
        }))
      : source.map((r) => ({
          label: r.date.slice(5),
          value: r.regions
            .filter((v) => selected.some((s) => s.id === v.regionId))
            .reduce((sum, v) => sum + v.volume, 0),
        }))
  }
  const points =
    pressureSeries
      .find((s) => s.entityId === facilityId.value)
      ?.points.filter((p) =>
        period.value === 'day' ? p.time.startsWith(date.value) : p.time.startsWith(date.value.slice(0, 7)),
      ) || []
  if (period.value === 'day') return points.map((p) => ({ label: p.time.slice(11, 16), value: p.value }))
  const days = [...new Set(points.map((p) => p.time.slice(0, 10)))]
  return days.map((day) => {
    const values = points.filter((p) => p.time.startsWith(day))
    return { label: day, value: Number((values.reduce((s, p) => s + p.value, 0) / values.length).toFixed(3)) }
  })
})
function exportData() {
  if (demo.has('export'))
    exportCsv('供水统计报表', [
      ['区域或时间', '数值', '单位'],
      ...rows.value.map((r) => [r.label, r.value, unit.value]),
    ])
}
</script>
<style scoped>
.report-row {
  grid-template-columns: 1fr 1fr 1fr 1.5fr;
}
</style>
