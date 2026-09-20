<template>
  <view>
    <view class="toolbar">
      <button
        class="button secondary"
        @click="go('mapChanges', { id: pipeId || selectedId, kind: pipeId ? 'pipe' : 'facility' })"
      >
        点线编辑与纠错
      </button>
      <button class="button secondary" @click="go('mapConfig')">图层配置</button>
      <button class="button secondary" @click="go('mapAnalysis')">爆管分析</button>
      <button class="button secondary" @click="go('logs')">操作日志</button>
      <button class="button secondary" v-if="demo.has('export')" @click="exportFacilities">
        导出设施清单
      </button>
    </view>
    <view class="panel">
      <view class="toolbar">
        <input class="field search" v-model="filter.keyword" placeholder="搜索设施 / 管线名称或编号" />
        <SelectField v-model="filter.region" :options="regionOptions" />
        <SelectField v-model="filter.type" :options="typeOptions" />
        <SelectField
          v-model="filter.status"
          :options="[
            { value: '', label: '全部状态' },
            { value: 'alarm', label: '存在未关闭告警' },
            { value: 'normal', label: '正常设施' },
          ]"
        />
        <view class="spacer" />
        <text class="subtle">设施 {{ facilities.length }} · 管段 {{ mapPipes.length }}</text>
      </view>
      <view class="toolbar">
        <SelectField
          v-model="selectionMode"
          :options="[
            { value: 'pan', label: '浏览平移' },
            { value: 'box', label: '矩形框选' },
            { value: 'polygon', label: '多边形面选' },
          ]"
        />
        <button v-if="selectionMode === 'polygon'" class="button" @click="finishPolygon">完成面选</button>
        <button class="button secondary" @click="clearSelection">清除区域选择</button>
        <text class="subtle">
          区域已选 {{ selectedIds.length }} 个设施 / {{ mapPipes.length }} 条匹配管线
        </text>
      </view>
      <view class="columns">
        <view>
          <view class="map-container">
            <MapCanvas :active="active" :config="config" @select="select" />
            <view class="map-floating">
              <button
                v-for="t in themes"
                :key="t.id"
                :class="['button', 'compact', theme === t.id ? '' : 'secondary']"
                @click="theme = t.id"
              >
                {{ t.name }}
              </button>
              <button class="button secondary compact" @click="showPipes = !showPipes">
                {{ showPipes ? '隐藏管线' : '显示管线' }}
              </button>
              <button class="button secondary compact" @click="zoom = Math.min(3, zoom + 0.25)">＋</button>
              <button class="button secondary compact" @click="zoom = Math.max(0.5, zoom - 0.25)">－</button>
              <button class="button secondary compact" @click="resetMap">复位</button>
            </view>
            <view class="map-bottom">
              {{
                selectionMode === 'polygon'
                  ? '逐点点击构成多边形，再点击完成面选'
                  : selectionMode === 'box'
                    ? '拖出矩形进行框选'
                    : '拖动平移 · 点击点线查看详情'
              }}
            </view>
          </view>
        </view>
        <view>
          <view class="panel-title">{{ selectedPipe ? '管线详情' : '设施详情' }}</view>
          <template v-if="selectedPipe">
            <text class="list-item-title">{{ selectedPipe.name }}</text>
            <view class="detail-grid">
              <view>
                <text class="detail-label">管线编号</text>
                {{ selectedPipe.id }}
              </view>
              <view>
                <text class="detail-label">管径</text>
                {{ selectedPipe.diameterMm }} mm
              </view>
            </view>
            <view class="list-item">
              <text class="detail-label">起点设施</text>
              <text class="link" @click="select({ type: 'facility', id: selectedPipe.fromFacilityId })">
                {{ nameOf(selectedPipe.fromFacilityId) }} ↗
              </text>
            </view>
            <view class="list-item">
              <text class="detail-label">终点设施</text>
              <text class="link" @click="select({ type: 'facility', id: selectedPipe.toFacilityId })">
                {{ nameOf(selectedPipe.toFacilityId) }} ↗
              </text>
            </view>
          </template>
          <template v-else-if="selected">
            <text class="list-item-title">{{ selected.name }}</text>
            <text class="subtle">{{ selected.id }}</text>
            <view class="detail-grid">
              <view>
                <text class="detail-label">设施类型</text>
                {{ facilityType(selected.type) }}
              </view>
              <view>
                <text class="detail-label">所属区域</text>
                {{ regionName(selected.regionId) }}
              </view>
              <view>
                <text class="detail-label">DMA 分区</text>
                <text class="link" @click="go('dmaDetail', { id: selected.dmaId })">
                  {{ selected.dmaId }} ↗
                </text>
              </view>
              <view>
                <text class="detail-label">关联告警</text>
                {{ demo.alarms.filter((a) => a.facilityId === selected?.id && a.status !== 'closed').length }}
                条
              </view>
            </view>
            <view class="detail-actions">
              <button class="button compact" @click="go('facility', { id: selected.id })">设施详情</button>
              <button class="button secondary compact" @click="go('alarms', { facilityId: selected.id })">
                查看告警
              </button>
            </view>
          </template>
          <view v-else class="empty">请选择地图上的设施或管线</view>
          <view v-if="selected" class="list-item">
            <text class="detail-label">已发布图层字段</text>
            <text v-for="field in demo.state.phase2.layers.fields" :key="field" class="subtle">
              {{ fieldLabels[field] }}：{{ fieldValue(selected, field) }}
            </text>
          </view>
          <view class="divider" />
          <text class="subtle">本地示意坐标。区域选择后下方台账同步过滤；点线变更需六级审核生效。</text>
        </view>
      </view>
    </view>
    <view class="panel" style="margin-top: 20px">
      <view class="panel-title">
        设施台账
        <text class="subtle">与地图采用相同筛选条件</text>
      </view>
      <view class="table">
        <view class="table-row header facility-row">
          <text>设施编号</text>
          <text>设施名称</text>
          <text>设施类型</text>
          <text>区域</text>
        </view>
        <view
          v-for="f in pageRows"
          :key="f.id"
          class="table-row facility-row"
          @click="select({ type: 'facility', id: f.id })"
        >
          <text class="link">{{ f.id }}</text>
          <text>{{ f.name }}</text>
          <text>{{ facilityType(f.type) }}</text>
          <text>{{ regionName(f.regionId) }}</text>
        </view>
      </view>
      <view v-if="!facilities.length" class="empty">没有匹配的设施</view>
      <view v-if="polygon.length && selectionMode === 'pan'" class="notice">
        <text>匹配管线（点选查看）</text>
        <text
          v-for="p in mapPipes"
          :key="p.id"
          class="link block"
          @click="select({ type: 'pipe', id: p.id })"
        >
          {{ p.name }} · {{ p.diameterMm }} mm
        </text>
      </view>
      <view class="pagination">
        <text>共 {{ facilities.length }} 处</text>
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
</template>
<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import { useDemo } from '../stores/demo'
import { regions, dictionary } from '../repositories/seed'
import { regionName, facilityType, options } from '../domain/presentation'
import { go } from '../navigation/routeMap'
import MapCanvas from './MapCanvas.vue'
import { inside, intersects } from '../domain/phase2'
import { exportCsv } from '../platform/export'
import SelectField from './SelectField.vue'
const props = defineProps<{ query: Record<string, string>; active: boolean }>(),
  demo = useDemo(),
  filter = reactive(demo.filters.map || { keyword: '', region: '', type: '', status: '', page: 1 })
demo.filters.map = filter
const selectedId = ref(props.query.facilityId || demo.selection),
  pipeId = ref(props.query.pipeId || ''),
  theme = ref('dark'),
  showPipes = ref(true),
  zoom = ref(1),
  reset = ref(0)
const selectionMode = ref('pan'),
  polygon = ref<number[][]>([]),
  selectedIds = ref<string[]>([])
function finishPolygon() {
  if (polygon.value.length < 3) {
    uni.showToast({ title: '至少选择三个顶点', icon: 'none' })
    return
  }
  selectedIds.value = demo.facilities.filter((f) => inside(f.position, polygon.value)).map((f) => f.id)
  selectionMode.value = 'pan'
}
function clearSelection() {
  polygon.value = []
  selectedIds.value = []
  selectionMode.value = 'pan'
}
function exportFacilities() {
  exportCsv('设施台账', [
    ['编号', '名称', '类型', '区域'],
    ...facilities.value.map((f) => [f.id, f.name, f.type, regionName(f.regionId)]),
  ])
}
const fieldLabels: Record<string, string> = {
  name: '名称',
  type: '类型',
  regionId: '区域',
  dmaId: 'DMA',
  status: '状态',
  description: '说明',
}
const fieldValue = (f: any, key: string) =>
  key === 'regionId' ? regionName(f[key]) : key === 'type' ? facilityType(f[key]) : f[key]
const themes = [
  { id: 'dark', name: '深色底图' },
  { id: 'light', name: '浅色底图' },
  { id: 'network', name: '管网视图' },
]
const regionOptions = computed(() => [
    { value: '', label: '全部区域' },
    ...regions
      .filter((r) => demo.user?.regionIds.includes(r.id))
      .map((r) => ({ value: r.id, label: r.name })),
  ]),
  typeOptions = options(dictionary.facilityTypes, '全部设施类型')
const alarmIds = computed(
  () => new Set(demo.alarms.filter((a) => a.status !== 'closed').map((a) => a.facilityId)),
)
const facilities = computed(() =>
  demo.facilities.filter(
    (f) =>
      (!polygon.value.length || selectionMode.value !== 'pan' || selectedIds.value.includes(f.id)) &&
      (!filter.region || f.regionId === filter.region) &&
      (!filter.type || f.type === filter.type) &&
      (!props.query.dmaId || f.dmaId === props.query.dmaId) &&
      (!filter.status ||
        (filter.status === 'alarm' ? alarmIds.value.has(f.id) : !alarmIds.value.has(f.id))) &&
      (f.name + f.id).includes(filter.keyword),
  ),
)
const mapPipes = computed(() =>
  demo.state.phase2.pipes.filter(
    (p) =>
      demo.user?.regionIds.includes(p.regionId) &&
      (!polygon.value.length || selectionMode.value !== 'pan' || intersects(p.path, polygon.value)) &&
      (!filter.region || p.regionId === filter.region) &&
      (!filter.keyword ||
        (p.name + p.id).includes(filter.keyword) ||
        facilities.value.some((f) => f.id === p.fromFacilityId || f.id === p.toFacilityId)) &&
      (!filter.type || facilities.value.some((f) => f.id === p.fromFacilityId || f.id === p.toFacilityId)),
  ),
)
const config = computed(() => ({
  regions: theme.value === 'network' ? [] : regions.filter((r) => demo.user?.regionIds.includes(r.id)),
  facilities: facilities.value,
  pipes: mapPipes.value,
  alarmIds: [...alarmIds.value],
  selected: pipeId.value || selectedId.value,
  theme: theme.value,
  showPipes: showPipes.value,
  zoom: zoom.value,
  reset: reset.value,
  selectionMode: selectionMode.value,
  polygon: polygon.value,
  selectedIds: selectedIds.value,
}))
const selected = computed(() => facilities.value.find((f) => f.id === selectedId.value)),
  selectedPipe = computed(() => mapPipes.value.find((p) => p.id === pipeId.value)),
  nameOf = (id: string) => demo.facilities.find((f) => f.id === id)?.name || id
const pages = computed(() => Math.max(1, Math.ceil(facilities.value.length / 10))),
  pageRows = computed(() => facilities.value.slice((filter.page - 1) * 10, filter.page * 10))
watch(
  () => [filter.keyword, filter.region, filter.type, filter.status],
  () => (filter.page = 1),
)
function resetMap() {
  reset.value++
  zoom.value = 1
}
function select(v: { type: string; id: string; point?: number[]; polygon?: number[][] }) {
  if (v.type === 'polygonPoint') {
    polygon.value.push(v.point!)
    return
  }
  if (v.type === 'box') {
    polygon.value = v.polygon!
    finishPolygon()
    return
  }
  if (v.type === 'facility') {
    selectedId.value = v.id
    demo.selection = v.id
    pipeId.value = ''
  } else pipeId.value = v.id
}
</script>
