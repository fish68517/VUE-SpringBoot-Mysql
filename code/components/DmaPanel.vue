<template>
  <view v-if="mode === 'dma'">
    <view class="notice">
      同一统计期间比较入口、转供与分表水量。所有分区为本地示意数据，点击分区可联查地图和告警。
    </view>
    <view class="dma-grid">
      <view v-for="d in visible" :key="d.id" class="panel dma-card" @click="go('dmaDetail', { id: d.id })">
        <view class="panel-title">
          <text>{{ d.name }}</text>
          <StatusTag :value="dmaMetrics(d).lossRate! > d.threshold ? 'important' : 'normal'" />
        </view>
        <text class="detail-label">演示漏损率</text>
        <view class="dma-main">{{ percent(dmaMetrics(d).lossRate) }}</view>
        <view class="list-item-meta">
          <text>净供水量 {{ dmaMetrics(d).net.toLocaleString() }} m³</text>
          <text>详情 ↗</text>
        </view>
        <view class="dma-bar">
          <view
            class="dma-fill"
            :style="{
              width: Math.min(100, (dmaMetrics(d).lossRate || 0) * 500) + '%',
            }"
          />
        </view>
      </view>
    </view>
  </view>
  <view v-else-if="selected">
    <view class="notice">
      {{ selected.name }} · 统计期间 {{ selected.period }} · 单位 m³。总分表比率＝分表合计 / 总表进水量。
    </view>
    <view class="kpi-grid">
      <view v-for="item in metrics" :key="item.label" class="kpi">
        <text class="kpi-label">{{ item.label }}</text>
        <text class="kpi-number">{{ item.value }}</text>
        <text class="kpi-note">{{ item.note }}</text>
      </view>
    </view>
    <view class="columns">
      <view class="panel">
        <view class="panel-title">
          计量构成
          <text>m³</text>
        </view>
        <ChartView
          :active="active"
          :labels="['入口进水', '出口转供', '分表水量', '合理未计量', '演示漏损']"
          :values="[
            selected.inlet,
            selected.transfer,
            selected.subMeter,
            selected.unmetered,
            dmaMetrics(selected).loss,
          ]"
          name="m³"
        />
        <view class="detail-actions">
          <button class="button" @click="go('map', { dmaId: selected.id })">地图定位</button>
          <button class="button secondary" @click="go('alarms', { dmaId: selected.id })">
            关联告警 {{ alarms.length }} 条
          </button>
          <button
            v-if="demo.has('write')"
            class="button secondary"
            @click="go('orderEdit', { facilityId: relatedFacility, type: 'leak' })"
          >
            新建检漏工单
          </button>
        </view>
      </view>
      <view class="panel">
        <view class="panel-title">阈值与历史</view>
        <view v-if="demo.has('dispatch')" class="toolbar">
          <input class="field" type="digit" v-model="threshold" placeholder="漏损阈值 %" />
          <button class="button" @click="saveThreshold">保存阈值 %</button>
        </view>
        <view class="toolbar">
          <picker mode="date" :value="historyStart" @change="historyStart = $event.detail.value">
            <view class="select-field">{{ historyStart }}</view>
          </picker>
          <picker mode="date" :value="historyEnd" @change="historyEnd = $event.detail.value">
            <view class="select-field">{{ historyEnd }}</view>
          </picker>
        </view>
        <ChartView
          v-if="history.length"
          :labels="history.map((h) => h.label)"
          :values="history.map((h) => h.value)"
          :active="active"
          name="漏损率 %"
        />
        <view v-else class="empty">所选区间无数据</view>
        <view class="panel-title">口径与范围</view>
        <view class="list-item">
          <text class="detail-label">漏损量</text>
          <text class="content-text">净供水 − 分表用水 − 合理未计量</text>
        </view>
        <view class="list-item">
          <text class="detail-label">产销差量</text>
          {{ selected.produced - selected.sold }} m³
        </view>
        <view class="list-item">
          <text class="detail-label">产销差率</text>
          {{ percent(dmaMetrics(selected).salesGapRate) }}
        </view>
        <view class="list-item">
          <text class="detail-label">服务住户（模拟）</text>
          {{ selected.households }} 户
        </view>
        <text class="subtle block" style="margin-top: 20px">
          历史曲线采用本地每日计量样例，缺失日期不补值；净供水不大于零的日期不绘制漏损率。
        </text>
      </view>
    </view>
  </view>
  <view v-else class="panel empty">分区不存在或无权访问</view>
</template>
<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import SelectField from './SelectField.vue'
import { useDemo } from '../stores/demo'
import { dmaMetrics, percent } from '../domain/metrics'
import { go } from '../navigation/routeMap'
import StatusTag from './StatusTag.vue'
import ChartView from './ChartView.vue'
const props = defineProps<{ mode: string; query: Record<string, string>; active: boolean }>(),
  demo = useDemo()
const visible = computed(() =>
    demo.state.phase2.dmas.filter((d) => demo.user?.regionIds.includes(d.regionId)),
  ),
  selected = computed(() => visible.value.find((d) => d.id === props.query.id)),
  alarms = computed(() => demo.alarms.filter((a) => a.dmaId === selected.value?.id))
const threshold = ref('12'),
  historyStart = ref('2026-09-01'),
  historyEnd = ref('2026-09-20')
watch(selected, (d) => (threshold.value = String((d?.threshold || 0) * 100)), { immediate: true })
const history = computed(() =>
  demo.state.phase2.dmaHistory
    .filter(
      (h) => h.dmaId === selected.value?.id && h.date >= historyStart.value && h.date <= historyEnd.value && h.inlet > h.transfer,
    )
    .map((h) => ({
      label: h.date,
      value:
        Math.round(
          ((h.inlet - h.transfer - h.subMeter - h.unmetered) / (h.inlet - h.transfer)) * 10000,
        ) / 100,
    })),
)
async function saveThreshold() {
  const r = await demo.run((e) => e.setThreshold(selected.value!.id, Number(threshold.value) / 100))
  if (r.success) uni.showToast({ title: '阈值已更新' })
}
const relatedFacility = computed(() => demo.facilities.find((f) => f.dmaId === selected.value?.id)?.id)
const metrics = computed(() => {
  if (!selected.value) return []
  const m = dmaMetrics(selected.value)
  return [
    { label: '净供水量', value: m.net.toLocaleString(), note: 'm³' },
    { label: '演示漏损量', value: m.loss.toLocaleString(), note: 'm³' },
    { label: '漏损率', value: percent(m.lossRate), note: '漏损 / 净供水' },
    { label: '总分表比率', value: percent(m.ratio), note: '分表 / 总表' },
  ]
})
</script>
