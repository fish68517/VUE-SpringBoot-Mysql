<template>
  <view :class="{ 'mobile-map': mobile }">
    <view v-if="mobile" class="toolbar mobile-map-tools">
      <button class="button secondary" @click="filtersOpen = !filtersOpen">
        筛选 {{ filtersOpen ? '收起' : '展开' }}
      </button>
      <button class="button secondary" @click="layersOpen = !layersOpen">图层</button>
      <button class="button secondary" @click="listOpen = !listOpen">结果 {{ facilities.length }}</button>
    </view>
    <view v-if="query.taskId" class="notice">
      {{ task ? '当前任务：' + task.name : '任务不存在或无权访问' }}
      <button v-if="task" class="mobile-link" @click="go('taskDetail', { id: task.id })">返回任务</button>
    </view>
    <view v-if="!mobile" class="toolbar">
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
      <view v-if="!mobile || filtersOpen" class="toolbar">
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
      <view v-if="!geographic" class="toolbar">
        <SelectField
          v-model="selectionMode"
          :options="[
            { value: 'pan', label: '浏览平移' },
            { value: 'box', label: '矩形框选' },
            ...(!mobile ? [{ value: 'polygon', label: '多边形面选' }] : []),
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
          <view v-if="geographic" class="subtle geo-summary">
            区域汇总 · {{ facilities.length }} 处设施 · 点击“结果”查看明细
          </view>
          <view :class="['map-container', { 'geo-map': geographic }]">
            <DashboardMap v-if="geographic" :active="active" :config="geoConfig" @select="selectRegion" />
            <MapCanvas v-else :active="active" :config="config" @select="select" />
            <view class="map-floating">
              <template v-if="!mobile || layersOpen">
                <button
                  v-for="t in themes"
                  :key="t.id"
                  :class="['button', 'compact', theme === t.id ? '' : 'secondary']"
                  @click="theme = t.id"
                >
                  {{ t.name }}
                </button>
                <button v-if="!geographic" class="button secondary compact" @click="showPipes = !showPipes">
                  {{ showPipes ? '隐藏管线' : '显示管线' }}
                </button>
              </template>
              <button class="button secondary compact" @click="changeZoom(1)">＋</button>
              <button class="button secondary compact" @click="changeZoom(-1)">－</button>
              <button class="button secondary compact" @click="resetMap">复位</button>
            </view>
            <view class="map-bottom">
              {{
                geographic
                  ? '高德地图 · 点击区域查看设施'
                  : selectionMode === 'polygon'
                    ? '逐点点击构成多边形，再点击完成面选'
                    : selectionMode === 'box'
                      ? '拖出矩形进行框选'
                      : '拖动平移 · 点击点线查看详情'
              }}
            </view>
          </view>
        </view>
        <view v-if="mobile && detailOpen" class="map-sheet-mask" @click="detailOpen = false" />
        <view v-if="!mobile || detailOpen" :class="{ 'map-sheet': mobile }">
          <view class="panel-title">
            {{ selectedPipe ? '管线详情' : '设施详情' }}
            <button v-if="mobile" class="mobile-link" @click="detailOpen = false">关闭</button>
          </view>
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
              <button
                v-if="mobile && demo.has('write')"
                class="button secondary"
                @click="go('mobileReport', { facilityId: selected.id })"
              >
                事件上报
              </button>
              <button
                v-if="mobile && demo.has('write')"
                class="button secondary"
                @click="go('mapChanges', { id: selected.id, kind: 'facility' })"
              >
                设施纠错
              </button>
            </view>
          </template>
          <view v-else class="empty">请选择地图上的设施或管线</view>
          <button
            v-if="mobile && selectedPipe && demo.has('write')"
            class="button secondary"
            @click="go('mapChanges', { id: selectedPipe.id, kind: 'pipe' })"
          >
            管线纠错
          </button>
          <view v-if="selected" class="list-item">
            <text class="detail-label">已发布图层字段</text>
            <text v-for="field in demo.state.phase2.layers.fields" :key="field" class="subtle">
              {{ fieldLabels[field] }}：{{ fieldValue(selected, field) }}
            </text>
          </view>
          <view class="divider" />
          <text class="subtle">
            {{
              geographic
                ? '地图按区域汇总展示，设施台账尚未配置真实经纬度。'
                : '本地示意坐标。区域选择后下方台账同步过滤；点线变更需六级审核生效。'
            }}
          </text>
        </view>
      </view>
    </view>
    <view v-if="!mobile || listOpen" class="panel" style="margin-top: 20px">
      <view class="panel-title">
        设施台账
        <text class="subtle">与地图采用相同筛选条件</text>
      </view>
      <view v-if="!mobile" class="table">
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
      <view v-else>
        <view
          v-for="f in pageRows"
          :key="f.id"
          class="list-item"
          @click="select({ type: 'facility', id: f.id })"
        >
          <text class="list-item-title">{{ f.name }} ›</text>
          <text class="subtle">{{ facilityType(f.type) }} · {{ regionName(f.regionId) }} · {{ f.id }}</text>
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
import { onBackPress } from '@dcloudio/uni-app'
import { useDemo } from '../stores/demo'
import { regions, dictionary } from '../repositories/seed'
import { regionName, facilityType, options } from '../domain/presentation'
import { go } from '../navigation/routeMap'
import MapCanvas from './MapCanvas.vue'
import DashboardMap from './DashboardMap.vue'
import { inside, intersects } from '../domain/phase2'
import { exportCsv } from '../platform/export'
import SelectField from './SelectField.vue'
const props = defineProps<{ query: Record<string, string>; active: boolean; mobile?: boolean }>(),
  demo = useDemo(),
  filterKey = props.query.taskId ? 'map-task:' + props.query.taskId : props.mobile ? 'mobileMap' : 'map',
  filter = reactive(demo.filters[filterKey] || { keyword: '', region: '', type: '', status: '', page: 1 })
demo.filters[filterKey] = filter
const filtersOpen = ref(false),
  layersOpen = ref(false),
  listOpen = ref(false),
  detailOpen = ref(false)
const task = computed(() => demo.tasks.find((t) => t.id === props.query.taskId))
const taskIds = computed(() => new Set(task.value?.checks.map((c) => c.facilityId) || []))
onBackPress((event) => {
  if (event.from === 'navigateBack') return false
  if (props.mobile && detailOpen.value) {
    detailOpen.value = false
    return true
  }
  return false
})
const selectedId = ref(props.query.facilityId || demo.selection),
  pipeId = ref(props.query.pipeId || ''),
  theme = ref(props.mobile ? 'light' : 'dark'),
  showPipes = ref(true),
  zoom = ref(1),
  reset = ref(0)
const geographic = computed(() => !!props.mobile && theme.value !== 'network')
const zoomStep = ref(0)
watch(theme, () => {
  if (props.mobile) clearSelection()
  zoomStep.value = 0
})
const selectionMode = ref('pan'),
  polygon = ref<number[][]>([]),
  selectedIds = ref<string[]>([])
function finishPolygon() {
  if (polygon.value.length < 3) {
    uni.showToast({ title: '至少选择三个顶点', icon: 'none' })
    return
  }
  selectedIds.value = facilities.value.filter((f) => inside(f.position, polygon.value)).map((f) => f.id)
  filter.page = 1
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
      (!props.query.taskId || taskIds.value.has(f.id)) &&
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
      (!props.query.taskId || (taskIds.value.has(p.fromFacilityId) && taskIds.value.has(p.toFacilityId))) &&
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
const geoConfig = computed(() => ({
  compact: true,
  regions: regions
    .filter((r) => facilities.value.some((f) => f.regionId === r.id))
    .map((r) => ({
      id: r.id,
      name: r.name,
      facilityCount: facilities.value.filter((f) => f.regionId === r.id).length,
      alarmCount: demo.alarms.filter(
        (a) =>
          a.status !== 'closed' && facilities.value.some((f) => f.id === a.facilityId && f.regionId === r.id),
      ).length,
    })),
  theme: theme.value,
  reset: reset.value,
  zoomStep: zoomStep.value,
}))
function selectRegion(event: { type: string; id: string }) {
  if (event.type !== 'region') return
  filter.region = event.id
  filter.page = 1
  listOpen.value = true
}
function changeZoom(direction: number) {
  if (geographic.value) zoomStep.value += direction
  else zoom.value = Math.max(0.5, Math.min(3, zoom.value + direction * 0.25))
}
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
  zoomStep.value = 0
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
  if (props.mobile) detailOpen.value = true
}
</script>
<style scoped>
.mobile-map .columns {
  display: block;
}
.mobile-map .map-container {
  height: 52vh;
  min-height: 320px;
  max-height: 620px;
}
.mobile-map .map-floating {
  max-width: calc(100% - 24px);
  flex-wrap: wrap;
}
.geo-summary {
  margin-bottom: 12px;
  font-size: 12px;
}
.geo-map .map-bottom {
  bottom: 38px;
  background: rgba(255, 255, 255, 0.85);
  color: #34596a;
  border-radius: 4px;
  padding: 4px 8px;
  pointer-events: none;
}
.map-sheet-mask {
  position: fixed;
  inset: 0;
  background: rgba(10, 30, 48, 0.28);
  z-index: 70;
}
.map-sheet {
  position: fixed;
  bottom: calc(70px + env(safe-area-inset-bottom));
  left: 10px;
  right: 10px;
  padding: 20px;
  max-height: 62vh;
  overflow-y: auto;
  background: white;
  border-radius: 20px;
  box-shadow: 0 8px 40px #14334633;
  z-index: 75;
}
.map-sheet .panel-title {
  display: flex;
  justify-content: space-between;
}
.map-sheet .list-item > .subtle {
  display: block;
  margin-top: 6px;
}
</style>
