<template>
  <view v-if="mode === 'datahub'">
    <view class="notice">点击设备可设置采样间隔、启停状态，并查看运行时长与能耗。</view>
    <view class="kpi-grid">
      <view class="kpi">
        <text class="kpi-label">设备</text>
        <text class="kpi-number">{{ visibleDevices.length }}</text>
        <text class="kpi-note">当前可见区域</text>
      </view>
      <view class="kpi">
        <text class="kpi-label">在线设备</text>
        <text class="kpi-number">{{ visibleDevices.filter((d) => d.status === 'online').length }}</text>
      </view>
      <view class="kpi">
        <text class="kpi-label">离线设备</text>
        <text class="kpi-number">{{ visibleDevices.filter((d) => d.status === 'offline').length }}</text>
      </view>
      <view class="kpi">
        <text class="kpi-label">规划接入规模</text>
        <text class="kpi-number">50,000</text>
        <text class="kpi-note">设备容量规划</text>
      </view>
    </view>
    <view class="panel">
      <view class="table">
        <view class="table-row header facility-row">
          <text>设备编号</text>
          <text>设备名称</text>
          <text>运行状态</text>
          <text>详情</text>
        </view>
        <view
          v-for="d in visibleDevices"
          :key="d.id"
          class="table-row facility-row"
          @click="go('device', { deviceId: d.id })"
        >
          <text>{{ d.id }}</text>
          <text>{{ d.name }}</text>
          <text>{{ d.status === 'online' ? '在线' : '离线' }}</text>
          <text class="link">查看曲线 ↗</text>
        </view>
      </view>
    </view>
  </view>
  <view v-else-if="facility">
    <view class="columns">
      <view class="stack">
        <view class="panel">
          <view class="panel-title">
            {{ facility.name }}
            <text class="subtle">{{ facility.id }}</text>
          </view>
          <view class="detail-grid">
            <view>
              <text class="detail-label">设施类型</text>
              {{ facilityType(facility.type) }}
            </view>
            <view>
              <text class="detail-label">所属区域</text>
              {{ regionName(facility.regionId) }}
            </view>
            <view>
              <text class="detail-label">所属 DMA</text>
              <text class="link" @click="go('dmaDetail', { id: facility.dmaId })">
                {{ facility.dmaId }} ↗
              </text>
            </view>
            <view>
              <text class="detail-label">示意坐标</text>
              {{ facility.position.map((v) => v.toFixed(1)).join(' / ') }}
            </view>
            <view>
              <text class="detail-label">设备状态</text>
              {{ device ? (device.status === 'online' ? '在线' : '离线') : '未关联设备' }}
            </view>
            <view>
              <text class="detail-label">采样间隔</text>
              {{ device ? device.samplingIntervalSec + ' 秒' : '—' }}
            </view>
          </view>
          <text class="subtle">{{ facility.description }}</text>
          <view class="detail-actions">
            <button class="button" v-if="device" @click="go('energy', { deviceId: device.id })">
              采样、启停与能耗
            </button>
            <button
              class="button secondary"
              v-if="facility.type === 'valve' && demo.has('write')"
              @click="toggleValve"
            >
              阀门：{{ facility.valveState === 'open' ? '开启' : '关闭' }}
            </button>
            <button
              v-if="demo.has('write')"
              class="button secondary"
              @click="go('mapChanges', { id: facility.id })"
            >
              纠错与编辑
            </button>
            <button class="button" @click="go('map', { facilityId: facility.id })">地图定位</button>
            <button class="button secondary" @click="go('alarms', { facilityId: facility.id })">
              关联告警
            </button>
            <button
              v-if="demo.has('write')"
              class="button secondary"
              @click="go('orderEdit', { facilityId: facility.id })"
            >
              新建工单
            </button>
          </view>
        </view>
        <view class="panel">
          <view class="panel-title">
            压力趋势
            <text class="subtle">2026-09-20 · 全天记录</text>
          </view>
          <ChartView
            v-if="series"
            :active="active"
            :labels="series.points.map((p) => p.time.slice(11, 16))"
            :values="series.points.map((p) => p.value)"
            name="MPa"
          />
          <view v-else class="empty">该设施没有配置压力数据</view>
          <view class="panel-title">
            采样回放
            <text class="subtle">最近 {{ samples.length }} 个样本</text>
          </view>
          <view class="toolbar">
            <button class="button" @click="demo.playing ? demo.pause() : demo.play()">
              {{ demo.playing ? '暂停回放' : '播放采样' }}
            </button>
            <button class="button secondary" @click="go('energy', { deviceId: device?.id })">配置间隔</button>
          </view>
          <ChartView
            v-if="samples.length"
            :active="active"
            :labels="samples.map((s) => s.time.slice(11, 19))"
            :values="samples.map((s) => s.value)"
            name="MPa · 采样值"
          />
          <view v-else class="empty">播放后按配置间隔采样；离线设备不生成新样本</view>
        </view>
      </view>
      <view class="panel">
        <view class="panel-title">关联业务</view>
        <view v-for="a in alarms" :key="a.id" class="list-item" @click="go('alarmDetail', { id: a.id })">
          <text class="list-item-title">{{ a.title }}</text>
          <StatusTag :value="a.status" kind="alarm" />
        </view>
        <view
          v-for="w in orders.slice(0, 5)"
          :key="w.id"
          class="list-item"
          @click="go('orderDetail', { id: w.id })"
        >
          <text class="list-item-title">{{ w.title }}</text>
          <StatusTag :value="w.status" kind="order" />
        </view>
        <view v-if="!alarms.length && !orders.length" class="empty">暂无关联业务</view>
      </view>
    </view>
  </view>
  <view v-else class="panel empty">设施或设备不存在，或当前账号无权访问</view>
</template>
<script setup lang="ts">
import { computed } from 'vue'
import { confirm } from '../domain/presentation'
import { useDemo } from '../stores/demo'
import { pressureSeries } from '../repositories/seed'
import { facilityType, regionName } from '../domain/presentation'
import { go } from '../navigation/routeMap'
import ChartView from './ChartView.vue'
import StatusTag from './StatusTag.vue'
const props = defineProps<{ mode: string; query: Record<string, string>; active: boolean }>(),
  demo = useDemo()
const visibleDevices = computed(() =>
  demo.state.phase2.devices.filter((d) => demo.facilities.some((f) => f.id === d.facilityId)),
)
const facility = computed(() =>
    demo.facilities.find((f) =>
      props.mode === 'device' ? f.deviceId === props.query.deviceId : f.id === props.query.id,
    ),
  ),
  device = computed(() => visibleDevices.value.find((d) => d.facilityId === facility.value?.id)),
  series = computed(() => pressureSeries.find((s) => s.entityId === facility.value?.id)),
  alarms = computed(() => demo.alarms.filter((a) => a.facilityId === facility.value?.id)),
  orders = computed(() => demo.orders.filter((w) => w.facilityId === facility.value?.id))
const samples = computed(() =>
  demo.state.phase2.samples.filter((s) => s.deviceId === device.value?.id).slice(-120),
)
async function toggleValve() {
  if (!facility.value || !(await confirm('阀门控制', '确认切换阀门状态？'))) return
  await demo.run((e) => e.toggleValve(facility.value!.id))
}
</script>
