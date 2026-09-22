<template>
  <view class="screen">
    <view class="screen-header">
      <view class="screen-wordmark">
        ≈
        <text>智慧水务</text>
      </view>
      <view class="screen-title">
        城市智慧供水
        <text>SMART WATER OPERATION CENTER</text>
      </view>
      <view class="screen-meta">
        <text>数据时间 · {{ formatTime(demo.state.simulationTime) }}</text>
        <view @click="go('portal', {}, true)">业务工作台 ↗</view>
      </view>
    </view>
    <view class="screen-toolbar">
      <view>
        <text class="live-dot" />
        城市供水运行态势
        <text class="screen-muted">/ {{ currentScenario }}</text>
      </view>
      <view class="screen-tools">
        <button class="button dark compact" @click="demo.playing ? demo.pause() : demo.play()">
          {{ demo.playing ? 'Ⅱ 暂停回放' : '▷ 播放回放' }}
        </button>
        <button class="button dark compact" @click="go('settings')">场景设置</button>
        <button class="button dark compact" @click="demo.logout">退出</button>
      </view>
    </view>
    <view class="screen-kpis">
      <view v-for="item in kpis" :key="item.label" @click="go(item.page)">
        <text>{{ item.label }}</text>
        <view>
          {{ item.value }}
          <text>{{ item.unit }}</text>
        </view>
        <text class="screen-kpi-note">{{ item.note }} ↗</text>
      </view>
    </view>
    <view class="screen-main">
      <view class="screen-side stack">
        <view class="screen-panel">
          <view class="screen-panel-title">
            供水结构分析
            <text>今日</text>
          </view>
          <view class="supply-number">
            {{ volume.toLocaleString() }}
            <text>m³</text>
          </view>
          <text class="screen-muted">区域居民用水量</text>
          <view v-for="r in regionUsage" :key="r.name" class="screen-rank">
            <view>
              {{ r.name }}
              <text>{{ r.value.toLocaleString() }}</text>
            </view>
            <view class="rank-track"><view :style="{ width: (r.value / maxUsage) * 100 + '%' }" /></view>
          </view>
        </view>
        <view class="screen-panel">
          <view class="screen-panel-title">
            重点设备压力
            <text>MPa</text>
          </view>
          <ChartView v-if="series" :active="active" :labels="labels" :values="values" dark name="MPa" />
          <view v-else class="empty">该设施无压力数据</view>
          <text class="screen-muted">选择区域或设施，联动查看压力曲线</text>
        </view>
      </view>
      <view class="screen-map-area">
        <DashboardMap :active="active && !!demo.user" :config="mapConfig" @select="select" />
        <view class="map-title-float">
          <text class="screen-muted">● 管网动态总览</text>
          <view>郑州供水一张图</view>
          <text>设施 {{ demo.facilities.length }} 处 · 区域运行总览</text>
        </view>
        <view class="map-actions-float">
          <button class="button dark compact" @click="reset++">定位复位</button>
          <button class="button dark compact" @click="mapTheme = mapTheme === 'dark' ? 'light' : 'dark'">
            {{ mapTheme === 'dark' ? '标准底图' : '深色底图' }}
          </button>
          <button class="button dark compact" @click="go('map', { facilityId: demo.selection })">
            管网台账 ↗
          </button>
        </view>
        <view class="screen-map-card" v-if="selected">
          <SelectField v-model="facilitySelection" :options="facilityOptions" />
          <view>
            <text class="live-dot" />
            {{ selected.name }}
          </view>
          <text class="screen-muted">{{ selected.id }} · {{ facilityType(selected.type) }}</text>
          <view class="screen-card-actions">
            <button class="button dark compact" @click="go('facility', { id: selected.id })">设施详情</button>
            <button class="button dark compact" @click="go('alarms', { facilityId: selected.id })">
              关联告警 ↗
            </button>
          </view>
        </view>
        <view class="screen-legend">
          <text>区域汇总标记 · 非设施定位</text>
        </view>
      </view>
      <view class="screen-side stack">
        <view class="screen-panel">
          <view class="screen-panel-title">
            告警动态
            <text @click="go('alarms')">全部 ↗</text>
          </view>
          <view class="alarm-summary">
            <view>
              {{ demo.metrics.urgent }}
              <text>紧急告警</text>
            </view>
            <view>
              {{ demo.metrics.alarms }}
              <text>待闭环告警</text>
            </view>
          </view>
          <view
            v-for="a in activeAlarms.slice(0, 5)"
            :key="a.id"
            class="screen-alarm"
            @click="go('alarms', { id: a.id })"
          >
            <view>
              <text :class="['level-dot', a.level]" />
              {{ a.title }}
              <text class="arrow">↗</text>
            </view>
            <text>{{ nameOf(a.facilityId) }} · {{ formatTime(a.occurredAt).slice(11, 16) }}</text>
          </view>
          <view v-if="!activeAlarms.length" class="empty">当前场景无未关闭告警</view>
        </view>
        <view class="screen-panel">
          <view class="screen-panel-title">
            DMA 漏损关注
            <text @click="go('dma')">分区 ↗</text>
          </view>
          <view
            v-for="d in visibleDmas.slice(0, 4)"
            :key="d.id"
            class="screen-dma"
            @click="go('dmaDetail', { id: d.id })"
          >
            <view>
              {{ d.name }}
              <text>{{ percent(dmaMetrics(d).lossRate) }}</text>
            </view>
            <view class="rank-track">
              <view
                :style="{
                  width: Math.min(100, (dmaMetrics(d).lossRate || 0) * 500) + '%',
                  background: '#e9b349',
                }"
              />
            </view>
          </view>
        </view>
      </view>
    </view>
    <view class="screen-footer">
      <text>业务场景：{{ currentScenario }} · 业务数据仅保存于当前设备</text>
      <text>供水安全 / 精细计量 / 协同处置</text>
    </view>
  </view>
</template>
<script setup lang="ts">
import { computed, ref } from 'vue'
import { onLoad, onShow, onHide, onUnload } from '@dcloudio/uni-app'
import { useDemo } from '../../stores/demo'
import { regions, pressureSeries, usage, dictionary } from '../../repositories/seed'
import { dmaMetrics, percent, formatTime, usageFor } from '../../domain/metrics'
import { go, urlFor } from '../../navigation/routeMap'
import { isMobileClient } from '../../platform/client'
import DashboardMap from '../../components/DashboardMap.vue'
import SelectField from '../../components/SelectField.vue'
import ChartView from '../../components/ChartView.vue'
const demo = useDemo(),
  active = ref(true),
  reset = ref(0),
  mapTheme = ref('dark')
onLoad(() => {
  if (isMobileClient()) {
    go('mobileHome', {}, true)
    return
  }
  if (!demo.user) uni.reLaunch({ url: urlFor('login', { redirect: urlFor('dashboard') }) })
})
onShow(() => (active.value = true))
onHide(() => {
  active.value = false
  demo.pause()
})
onUnload(() => {
  active.value = false
  demo.pause()
})
const activeAlarms = computed(() => demo.alarms.filter((a) => a.status !== 'closed'))
const visibleDmas = computed(() =>
  demo.state.phase2.dmas.filter((d) => demo.user?.regionIds.includes(d.regionId)),
)
const selected = computed(() => demo.facilities.find((f) => f.id === demo.selection) || demo.facilities[0])
const facilitySelection = computed({
  get: () => selected.value?.id || '',
  set: (id: string) => {
    demo.selection = id
  },
})
const facilityOptions = computed(() => demo.facilities.map((f) => ({ value: f.id, label: f.name })))
const nameOf = (id: string) => demo.facilities.find((f) => f.id === id)?.name || id
const facilityType = (id: string) => (dictionary.facilityTypes as Record<string, string>)[id]
const currentScenario = computed(() => dictionary.scenarios.find((s) => s.id === demo.state.scenarioId)?.name)
const volume = computed(() => usageFor(demo.user?.regionIds || [], '2026-09-20'))
const regionUsage = computed(() =>
  regions
    .filter((r) => demo.user?.regionIds.includes(r.id))
    .map((r) => ({
      name: r.name,
      value: usage.find((u) => u.date === '2026-09-20')!.regions.find((v) => v.regionId === r.id)!.volume,
    })),
)
const maxUsage = computed(() => Math.max(...regionUsage.value.map((v) => v.value), 1))
const kpis = computed(() => [
  {
    label: '今日居民用水',
    value: (volume.value / 10000).toFixed(2),
    unit: '万 m³',
    note: '统一计量口径',
    page: 'reports',
  },
  { label: '管网设施', value: demo.metrics.facilities, unit: '处', note: '设施台账', page: 'map' },
  { label: '待闭环告警', value: demo.metrics.alarms, unit: '条', note: '分级响应处理', page: 'alarms' },
  { label: '进行中工单', value: demo.metrics.orders, unit: '单', note: '任务协同处置', page: 'orders' },
  {
    label: '巡检完成率',
    value: percent(demo.metrics.completion),
    unit: '',
    note: '任务执行进度',
    page: 'inspection',
  },
])
const series = computed(() => pressureSeries.find((v) => v.entityId === selected.value?.id))
const labels = computed(() => (series.value?.points || []).map((p) => p.time.slice(11, 16))),
  values = computed(() => (series.value?.points || []).map((p) => p.value))
const mapConfig = computed(() => ({
  regions: regions
    .filter((r) => demo.user?.regionIds.includes(r.id))
    .map((r) => ({
      id: r.id,
      name: r.name,
      facilityCount: demo.facilities.filter((f) => f.regionId === r.id).length,
      alarmCount: activeAlarms.value.filter((a) =>
        demo.facilities.some((f) => f.id === a.facilityId && f.regionId === r.id),
      ).length,
    })),
  selectedRegion: selected.value?.regionId,
  theme: mapTheme.value,
  reset: reset.value,
}))
function select(v: { type: string; id: string }) {
  if (v.type === 'region') {
    const facilities = demo.facilities.filter((f) => f.regionId === v.id)
    const facility = facilities.find((f) => pressureSeries.some((s) => s.entityId === f.id)) || facilities[0]
    if (facility) demo.selection = facility.id
  }
}
</script>
<style scoped>
.screen {
  background: #081f33;
  min-height: 100vh;
  color: #d9eff9;
  padding: 0 24px;
  font-size: 12px;
}
.screen-header {
  height: 87px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #235170;
  position: relative;
  background: linear-gradient(90deg, transparent, #113b62, transparent);
}
.screen-wordmark {
  font-size: 34px;
  color: #51d7ef;
}
.screen-wordmark text {
  font-size: 13px;
  color: #8baec4;
  letter-spacing: 3px;
}
.screen-title {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  text-align: center;
  font-size: 27px;
  font-weight: 600;
  letter-spacing: 5px;
  white-space: nowrap;
}
.screen-title text {
  display: block;
  font-size: 8px;
  letter-spacing: 3px;
  color: #6196b9;
  margin-top: 10px;
}
.screen-meta {
  text-align: right;
  font-size: 10px;
  color: #719dbb;
  line-height: 2.4;
}
.screen-meta view {
  color: #c1deef;
  cursor: pointer;
}
.screen-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 65px;
  font-size: 13px;
}
.screen-muted {
  color: #6b96b4;
  font-size: 10px;
}
.live-dot {
  display: inline-block;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #51d7ef;
  margin-right: 9px;
  box-shadow: 0 0 9px #51d7ef;
}
.screen-tools {
  display: flex;
  gap: 8px;
}
.screen-kpis {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  border: 1px solid #194360;
  background: linear-gradient(180deg, #10304b, #0b253d);
  margin-bottom: 20px;
}
.screen-kpis > view {
  padding: 19px 25px;
  border-right: 1px solid #194360;
  cursor: pointer;
}
.screen-kpis > view:last-child {
  border: 0;
}
.screen-kpis > view > text {
  font-size: 11px;
  color: #91b7d0;
}
.screen-kpis > view > view {
  font-size: 31px;
  font-weight: 500;
  color: #6de4f7;
  letter-spacing: 1px;
  margin: 10px 0;
}
.screen-kpis > view > view > text {
  font-size: 10px;
  color: #668fae;
  margin-left: 7px;
}
.screen-kpis .screen-kpi-note {
  font-size: 9px;
  color: #567e9d;
}
.screen-main {
  display: grid;
  grid-template-columns: minmax(210px, 1fr) minmax(300px, 2.7fr) minmax(225px, 1fr);
  gap: 16px;
  min-height: 630px;
}
.screen-panel {
  border: 1px solid #194766;
  background: linear-gradient(150deg, rgba(18, 53, 79, 0.8), rgba(8, 32, 53, 0.9));
  padding: 18px 17px;
}
.screen-panel-title {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  font-weight: 600;
  letter-spacing: 1px;
  border-left: 2px solid #4dd7ef;
  padding-left: 10px;
  margin-bottom: 19px;
}
.screen-panel-title text {
  font-size: 9px;
  font-weight: 400;
  color: #719db9;
  cursor: pointer;
}
.supply-number {
  font-size: 29px;
  color: #e4f7ff;
  letter-spacing: 1px;
}
.supply-number text {
  font-size: 10px;
  color: #6493b1;
  margin-left: 7px;
}
.screen-rank {
  margin-top: 17px;
  font-size: 10px;
  color: #83acc5;
}
.screen-rank > view:first-child {
  display: flex;
  justify-content: space-between;
}
.rank-track {
  height: 3px;
  background: #153c55;
  margin-top: 8px;
}
.rank-track > view {
  height: 100%;
  background: linear-gradient(90deg, #195e83, #50cce5);
}
.screen-map-area {
  position: relative;
  min-height: 630px;
  border: 1px solid #173e58;
}
.map-title-float {
  position: absolute;
  left: 18px;
  top: 18px;
  border-left: 2px solid #51d7ef;
  background: #092337d9;
  padding: 12px 16px;
}
.map-title-float > view {
  font-size: 16px;
  font-weight: 600;
  margin: 7px 0;
}
.map-title-float > text:last-child {
  font-size: 9px;
  color: #648eac;
}
.map-actions-float {
  position: absolute;
  right: 12px;
  top: 18px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.screen-map-card {
  position: absolute;
  left: 20px;
  bottom: 62px;
  background: #0a2942ed;
  border: 1px solid #2d6889;
  padding: 16px;
  min-width: 220px;
  max-width: 300px;
}
.screen-map-card :deep(.select-field) {
  width: 100%;
  max-width: 280px;
  margin-bottom: 12px;
  color: #d9eff9;
  background: #12384e;
  border-color: #2d6889;
}
.map-title-float,
.map-actions-float,
.screen-map-card,
.screen-legend {
  z-index: 140;
}
.screen-map-card > view:first-child {
  margin-bottom: 10px;
}
.screen-card-actions {
  display: flex;
  gap: 8px;
  margin-top: 13px;
}
.screen-legend {
  position: absolute;
  right: 15px;
  bottom: 23px;
  display: flex;
  gap: 14px;
  color: #58bfdc;
  font-size: 9px;
}
.alarm-summary {
  display: flex;
  gap: 40px;
  font-size: 25px;
  color: #ffbe42;
  margin: 3px 0 20px;
}
.alarm-summary text {
  display: block;
  font-size: 9px;
  color: #658eab;
  margin-top: 7px;
}
.screen-alarm {
  padding: 14px 0;
  border-top: 1px solid #193e58;
  cursor: pointer;
}
.screen-alarm > view {
  font-size: 11px;
  display: flex;
  align-items: center;
}
.level-dot {
  display: inline-block;
  width: 5px;
  height: 5px;
  border-radius: 50%;
  background: #1685ef;
  margin-right: 8px;
}
.level-dot.urgent {
  background: #f55462;
}
.level-dot.important {
  background: #ffbe42;
}
.arrow {
  margin-left: auto;
  color: #5b839f;
}
.screen-alarm > text {
  font-size: 9px;
  color: #557e9c;
  display: block;
  margin: 7px 0 0 13px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.screen-dma {
  margin: 21px 0;
  cursor: pointer;
}
.screen-dma > view:first-child {
  display: flex;
  justify-content: space-between;
  color: #96b9d0;
  font-size: 10px;
}
.screen-dma text {
  color: #e4be63;
}
.screen-footer {
  padding: 21px 0;
  display: flex;
  justify-content: space-between;
  color: #3e6887;
  font-size: 9px;
  letter-spacing: 1px;
}
.screen-side {
  gap: 16px;
}
@media (min-width: 1600px) {
  .screen-main {
    min-height: 660px;
    grid-template-columns: minmax(240px, 1fr) minmax(600px, 3fr) minmax(250px, 1fr);
  }
  .screen-map-area {
    min-height: 660px;
  }
  .screen-panel {
    padding: 17px;
  }
  .screen-rank {
    margin-top: 14px;
  }
  .screen-alarm {
    padding: 12px 0;
  }
  .screen-title {
    font-size: 31px;
  }
  .screen-kpis > view {
    padding: 23px 30px;
  }
}
@media (max-width: 1000px) {
  .screen {
    padding: 0 12px;
  }
  .screen-title {
    font-size: 19px;
    position: static;
    transform: none;
  }
  .screen-wordmark {
    display: none;
  }
  .screen-meta > text {
    display: none;
  }
  .screen-main {
    grid-template-columns: 1fr;
  }
  .screen-map-area {
    grid-row: 1;
    min-height: 480px;
    height: 480px;
  }
  .screen-kpis {
    grid-template-columns: repeat(2, 1fr);
  }
  .screen-kpis > view {
    padding: 13px;
  }
  .screen-kpis > view:last-child {
    display: none;
  }
  .screen-kpis > view > view {
    font-size: 24px;
  }
  .screen-side {
    display: grid;
    grid-template-columns: 1fr 1fr;
  }
  .screen-footer > text:last-child {
    display: none;
  }
  .screen-toolbar {
    font-size: 11px;
  }
  .screen-muted {
    display: none;
  }
  .screen-toolbar .button {
    font-size: 10px;
    padding: 6px;
  }
  .screen-side {
    grid-template-columns: 1fr;
  }
  .screen-meta {
    font-size: 9px;
  }
}
</style>
