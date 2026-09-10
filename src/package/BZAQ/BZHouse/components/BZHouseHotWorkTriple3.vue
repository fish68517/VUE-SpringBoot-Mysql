<template>
  <div
    :class="[
      isTripleScreen ? 'house-triple3' : 'house-single',
      { 'house-single--transparent': !showBackground }
    ]"
    :style="containerStyle"
  >
    <BZHouseHotWorkTriple2
      v-if="isTripleScreen"
      class="house-triple3__houses"
      :chart-config="props.chartConfig"
      :bus="props.bus"
    />

    <template v-else>
    <section v-for="house in houseSections" :key="house.key" class="single-module">
      <button type="button" class="single-module__title" @click="openHouseList(house.name)">
        <span>{{ house.name }}</span>
      </button>

      <div class="single-module__body">
        <div class="dual-heading">
          <button type="button" @click="openHouseList(house.name)">› {{ house.manageLabel }}</button>
          <button type="button" @click="openHousePersonList(house.name)">› 重点人员</button>
        </div>

        <div class="status-grid">
          <div
            v-for="item in house.status"
            :key="item.label"
            class="status-chip"
            :class="`status-chip--${item.tone}`"
          >
            <span>{{ item.label }}</span><b>{{ item.value }}</b>
          </div>
        </div>

        <button type="button" class="event-heading" @click="openHouseRisk(house.name)">› 重点事件</button>
        <div class="risk-grid" :class="{ 'risk-grid--two': house.risks.length === 2 }">
          <button
            v-for="item in house.risks"
            :key="item.label"
            type="button"
            class="risk-chip"
            @click="openHouseRisk(house.name)"
          >
            <span>{{ item.label }}</span><b>{{ item.current }}<i>/{{ item.total }}</i></b>
          </button>
        </div>

        <button type="button" class="code-panel" @click="openColorCode(house.name)">
          <span class="code-panel__title">› 四色评价</span>
          <span class="code-grid">
            <span
              v-for="item in colorCodes"
              :key="item.label"
              class="code-chip"
              :class="`code-chip--${item.tone}`"
            >
              <em>{{ item.label }}</em><b>{{ item.value }} <i>/ 99.99%</i></b>
            </span>
          </span>
        </button>

        <button type="button" class="event-empty" @click="openHouseRisk(house.name)">暂无事件</button>
      </div>
    </section>
    </template>

    <div v-if="isTripleScreen" class="house-triple3__hot-work-viewport">
      <BzHFAndwelding
        class="house-triple3__hot-work"
        :chart-config="props.chartConfig"
        :bus="props.bus"
        embedded
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import BZHouseHotWorkTriple2 from './BZHouseHotWorkTriple2.vue'
import BzHFAndwelding from '../../BzHFAndwelding/index.vue'

const props = defineProps({
  chartConfig: { type: Object, required: true },
  bus: { type: Object, default: null }
})

const containerStyle = computed(() => {
  const width = Number(props.chartConfig?.attr?.w)
  const height = Number(props.chartConfig?.attr?.h)
  return {
    width: Number.isFinite(width) && width > 0 ? `${width}px` : '620px',
    height: Number.isFinite(height) && height > 0 ? `${height}px` : '738px'
  }
})

const showBackground = computed(() => props.chartConfig?.option?.showBackground !== 'hide')
const isTripleScreen = computed(() => props.chartConfig?.option?.screenMode === 'triple')

const colorCodes = [
  { label: '绿码', value: 242, tone: 'green' },
  { label: '蓝码', value: 242, tone: 'blue' },
  { label: '黄码', value: 242, tone: 'yellow' },
  { label: '红码', value: 242, tone: 'red' }
]

const commonStatus = [
  { label: '纳管中', value: 17, tone: 'cyan' },
  { label: '已纳管', value: 17, tone: 'green' },
  { label: '未成年', value: 17, tone: 'cyan' },
  { label: '重点关注', value: 17, tone: 'amber' }
]

const commonRisks = [
  { label: '风险感知', current: 11, total: 11 },
  { label: '房屋风险', current: 11, total: 11 },
  { label: '人员风险', current: 11, total: 11 }
]

const houseSections = [
  { key: 'online', name: '网约房', manageLabel: '纳管情况', status: commonStatus, risks: commonRisks },
  { key: 'group', name: '群租空间', manageLabel: '房屋情况', status: commonStatus, risks: commonRisks },
  {
    key: 'business',
    name: '商改住',
    manageLabel: '纳管情况',
    status: commonStatus,
    risks: [
      { label: '巡查上报', current: 11, total: 11 },
      { label: '风险感知', current: 11, total: 11 }
    ]
  }
]

function emitDia(diaName: string, extra: Record<string, unknown> = {}) {
  props.bus?.emit('OPEN_HOUSE_DIA', {
    source: 'BZHouseHotWorkTriple3',
    data: { diaName, ...extra }
  })
}

function openHouseList(houseType: string) {
  emitDia('house-list', { houseType })
}

function openHousePersonList(houseType: string) {
  emitDia('house-person-list', { houseType })
}

function openHouseRisk(houseType: string) {
  emitDia('house-risk-perception', { houseType })
}

function openColorCode(houseType: string) {
  emitDia('house-color-code', { houseType })
}
</script>

<script lang="ts">
export default { name: 'BZHouseHotWorkTriple3' }
</script>

<style lang="scss" scoped>
@font-face {
  font-family: 'Triple3Text';
  src: url('../font/Alibaba_PuHuiTi_2.0_55_Regular_55_Regular.ttf') format('truetype');
  font-display: swap;
}

@font-face {
  font-family: 'Triple3DIN';
  src: url('../font/D-DIN-Bold.otf') format('opentype');
  font-display: swap;
}

.house-single {
  box-sizing: border-box;
  min-width: 340px;
  min-height: 420px;
  padding: 0 8px 10px;
  overflow-x: hidden;
  overflow-y: auto;
  color: #d9f3ff;
  background:
    linear-gradient(90deg, rgba(12, 112, 214, 0.14), transparent 25px, transparent calc(100% - 25px), rgba(12, 112, 214, 0.14)),
    radial-gradient(circle at 50% 0, rgba(13, 104, 194, 0.2), transparent 34%),
    linear-gradient(180deg, #03234a, #041d3c 54%, #031936);
  border: 1px solid rgba(31, 134, 229, 0.72);
  font-family: 'Triple3Text', 'Microsoft YaHei', sans-serif;
  scrollbar-width: thin;
  scrollbar-color: rgba(61, 190, 255, 0.8) rgba(5, 42, 84, 0.62);
}

.house-single--transparent { background: transparent; }
.house-single::-webkit-scrollbar { width: 4px; }
.house-single::-webkit-scrollbar-track { background: rgba(5, 42, 84, 0.62); }
.house-single::-webkit-scrollbar-thumb { border-radius: 4px; background: linear-gradient(#40d8ff, #187bd9); }

button {
  padding: 0;
  border: 0;
  color: inherit;
  background: none;
  font: inherit;
  text-align: left;
  cursor: pointer;
}

.single-module { padding-bottom: 8px; }
.single-module + .single-module { padding-top: 5px; border-top: 1px solid rgba(18, 105, 196, 0.3); }

.single-module__title {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  width: calc(100% + 16px);
  height: 49px;
  margin: 0 -8px 8px;
  overflow: hidden;
  background:
    url('../img/bg_title_long.png') center / 100% 100% no-repeat,
    linear-gradient(180deg, rgba(1, 16, 40, 0.92), rgba(5, 58, 111, 0.74), rgba(1, 16, 40, 0.92));
}

.single-module__title::before,
.single-module__title::after {
  content: '';
  position: absolute;
  top: 8px;
  width: 39%;
  height: 24px;
  border-top: 2px solid rgba(10, 117, 206, 0.88);
  border-radius: 0 60% 0 0;
  box-shadow: 0 -5px 14px rgba(18, 124, 216, 0.5);
}

.single-module__title::before { left: 0; transform: skewX(25deg); }
.single-module__title::after { right: 0; transform: scaleX(-1) skewX(25deg); }
.single-module__title span { position: relative; z-index: 1; color: #eefbff; font-size: 21px; font-weight: 700; letter-spacing: 2px; text-shadow: 0 0 11px #32c4ff; }
.single-module__body { padding: 0 2px; }

.dual-heading { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; height: 25px; }
.dual-heading button,
.event-heading { color: #daf2ff; font-size: 16px; line-height: 25px; }
.event-heading { display: block; width: 100%; height: 24px; }

.status-grid,
.risk-grid { display: grid; gap: 6px; }
.status-grid { grid-template-columns: repeat(4, minmax(0, 1fr)); }
.risk-grid { grid-template-columns: repeat(3, minmax(0, 1fr)); }
.risk-grid--two { grid-template-columns: repeat(2, minmax(0, 1fr)); }

.status-chip,
.risk-chip {
  box-sizing: border-box;
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-width: 0;
  height: 29px;
  padding: 0 7px;
  overflow: hidden;
  border-left: 2px solid #2dc7ff;
  background: linear-gradient(90deg, rgba(4, 77, 146, 0.9), rgba(5, 40, 84, 0.5));
  font-size: 15px;
}

.status-chip span,
.risk-chip span { overflow: hidden; white-space: nowrap; text-overflow: ellipsis; }
.status-chip b,
.risk-chip b { flex: 0 0 auto; color: #52f0ff; font: 17px 'Triple3DIN'; text-shadow: 0 0 7px currentColor; }
.status-chip--green { border-color: #19df94; }
.status-chip--green b { color: #24efa3; }
.status-chip--amber { border-color: #f4a936; }
.status-chip--amber b { color: #ffbd43; }

.risk-chip { width: 100%; border: 1px solid rgba(27, 127, 220, 0.75); background: repeating-linear-gradient(112deg, rgba(8, 84, 153, 0.62) 0 2px, rgba(4, 52, 106, 0.58) 2px 5px); }
.risk-chip b { color: #ffd333; }
.risk-chip b i { color: #53edff; font: inherit; }

.code-panel {
  display: grid;
  grid-template-columns: 170px 1fr;
  gap: 5px;
  width: 100%;
  margin-top: 7px;
  padding: 5px;
  box-sizing: border-box;
  border: 1px solid rgba(24, 121, 218, 0.9);
  background: linear-gradient(90deg, rgba(4, 61, 125, 0.96), rgba(6, 49, 105, 0.62));
}

.code-panel__title { display: flex; align-items: center; justify-content: center; color: #e7f7ff; font-size: 21px; }
.code-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 4px; }
.code-chip { display: flex; justify-content: space-between; min-width: 0; height: 25px; padding: 0 8px; line-height: 25px; border-left: 2px solid currentColor; background: rgba(4, 67, 130, 0.48); font-size: 14px; }
.code-chip em { font-style: normal; }
.code-chip b { font: 14px 'Triple3DIN'; white-space: nowrap; }
.code-chip i { color: #66ddf7; font: inherit; }
.code-chip--green { color: #2fe99a; }
.code-chip--blue { color: #34b3ff; }
.code-chip--yellow { color: #ffd02c; }
.code-chip--red { color: #ff5d72; }

.event-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 82px;
  margin-top: 6px;
  border: 1px solid rgba(27, 127, 220, 0.9);
  background: rgba(3, 43, 91, 0.45);
  color: #87b6da;
  font-size: 15px;
}

@media (max-width: 430px) {
  .single-module__title { height: 43px; }
  .single-module__title span { font-size: 18px; }
  .status-chip,
  .risk-chip { padding: 0 3px; font-size: 12px; }
  .status-chip b,
  .risk-chip b { font-size: 14px; }
  .code-panel { grid-template-columns: 118px 1fr; }
  .code-panel__title { font-size: 17px; }
  .code-chip { padding: 0 4px; font-size: 11px; }
  .code-chip b { font-size: 11px; }
}
// 单屏和三联屏共用的动火动焊模块。
.hot-work-panel {
  --hot-cyan: #31dcff;
  --hot-green: #20edbd;
  --hot-amber: #ffbd2f;
  margin: 6px -8px 0;
  line-height: 1.5;
  box-sizing: border-box;
  min-height: 0;
  padding: 0 10px 9px;
  overflow: visible;
  background:
    linear-gradient(90deg, rgba(18, 117, 217, 0.13), transparent 24px, transparent calc(100% - 24px), rgba(18, 117, 217, 0.13)),
    linear-gradient(180deg, rgba(1, 13, 34, 0.99), rgba(3, 28, 60, 0.99));
  border-top: 1px solid rgba(16, 106, 196, 0.82);
  box-shadow: 0 -10px 24px rgba(0, 18, 47, 0.76), inset 0 0 22px rgba(9, 92, 176, 0.14);
}

.hot-work-title {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 43px;
  margin: 0 -10px 5px;
  overflow: hidden;
  background:
    url('../img/bg_title_long.png') center / 100% 100% no-repeat,
    linear-gradient(180deg, rgba(0, 17, 43, 0.94), rgba(4, 54, 103, 0.76), rgba(0, 17, 43, 0.94));
}

.hot-work-title::before,
.hot-work-title::after {
  content: '';
  position: absolute;
  top: 8px;
  width: 39%;
  height: 20px;
  border-top: 2px solid rgba(10, 117, 206, 0.85);
  border-radius: 0 60% 0 0;
  box-shadow: 0 -4px 12px rgba(18, 124, 216, 0.48);
}

.hot-work-title::before { left: 0; transform: skewX(25deg); }
.hot-work-title::after { right: 0; transform: scaleX(-1) skewX(25deg); }
.hot-work-title span { position: relative; z-index: 1; color: #eefbff; font-size: 21px; font-weight: 700; letter-spacing: 2px; text-shadow: 0 0 11px #32c4ff; }

.hot-work-grid { display: grid; grid-template-columns: 1fr; gap: 6px; }

.safety-panel {
  box-sizing: border-box;
  width: 100%;
  padding: 5px 9px 6px;
  border: 1px solid rgba(18, 88, 166, 0.58);
  background: rgba(3, 37, 78, 0.54);
  box-shadow: inset 0 0 16px rgba(16, 112, 206, 0.12);
  cursor: pointer;
}

.safety-panel:hover { filter: brightness(1.06); }

.work-card__title {
  position: relative;
  display: flex;
  align-items: center;
  width: 100%;
  height: 31px;
  margin: 0 0 5px;
  padding-left: 39px;
  box-sizing: border-box;
  overflow: hidden;
  background: url('../img/bg_second_title.png') center / 100% 100% no-repeat;
  color: #f2fbff;
  font-size: 20px;
  font-weight: 700;
  font-style: italic;
  letter-spacing: 1px;
  text-shadow: 0 0 9px #33baff;
}

.work-card__title i {
  position: absolute;
  left: 5px;
  width: 27px;
  height: 27px;
  border-radius: 50%;
  background: radial-gradient(circle at 30% 38%, #79ecff 0 8%, #098dce 36%, #063c82 66%, transparent 70%);
  box-shadow: 0 0 9px #16a9ff;
}

.work-card__title strong { font-weight: 700; }

.summary-grid { display: grid; gap: 4px; margin: 0 5px 4px; }
.summary-grid--two { grid-template-columns: repeat(2, minmax(0, 1fr)); }
.summary-grid--three { grid-template-columns: repeat(3, minmax(0, 1fr)); }
.summary-grid > div {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  min-width: 0;
  min-height: 31px;
  padding: 2px 8px 3px;
  box-sizing: border-box;
  border-left: 2px solid #1fc5fa;
  background: linear-gradient(90deg, rgba(5, 75, 141, 0.64), rgba(3, 42, 89, 0.25));
}

.summary-grid span { min-width: 0; overflow: hidden; color: #86aac8; font-size: 10px; white-space: nowrap; text-overflow: ellipsis; }
.number { color: var(--hot-cyan); font: 20px/1 'Triple3DIN'; text-shadow: 0 0 7px currentColor; }
.number small { margin-left: 2px; color: #84a8c5; font: 8px 'Triple3Text'; text-shadow: none; }
.number--amber { color: var(--hot-amber); }
.number--green { color: var(--hot-green); }

.progress-block { margin: 1px 5px 4px; }
.progress-track { height: 6px; overflow: hidden; border-radius: 5px; background: rgba(3, 30, 64, 0.96); box-shadow: inset 0 0 4px rgba(0, 0, 0, 0.72); }
.progress-track i { display: block; height: 100%; border-radius: inherit; background: linear-gradient(90deg, #16d79e, #35f4c3); box-shadow: 0 0 8px #21e8bd; }
.progress-meta { display: flex; justify-content: space-between; margin-top: 2px; color: #557ea7; font-size: 8px; }

.inspection-detail { display: grid; grid-template-columns: 1fr 1fr; gap: 6px; margin: 0 5px; }
.bar-card,
.event-card,
.efficiency-card,
.source-card { box-sizing: border-box; min-width: 0; border: 1px solid rgba(25, 98, 175, 0.56); background: rgba(2, 28, 62, 0.72); }
.bar-card { height: 79px; padding: 5px 7px 3px; }
.bar-card__head { display: flex; align-items: center; justify-content: space-between; color: #658fb6; font-size: 8px; }
.bar-card__head::before { content: ''; width: 2px; height: 9px; margin-right: 5px; background: #18c9ff; box-shadow: 0 0 5px #18c9ff; }
.bar-card__head span { margin-right: auto; }
.bar-card__head time { color: #7698b8; font-size: 8px; }
.bar-card__summary { display: flex; align-items: flex-end; justify-content: space-between; height: 21px; }
.bar-card__summary span { color: #6489aa; font-size: 8px; }
.bar-card__summary strong { color: #7299ba; font-family: 'Triple3DIN'; }
.bars { display: flex; align-items: flex-end; justify-content: space-around; height: 42px; }
.bar-item { display: grid; grid-template-rows: 10px 23px 9px; justify-items: center; align-items: end; width: 20%; color: #6995bd; font-size: 7px; }
.bar-item b { color: #8bc8fb; font: 8px 'Triple3DIN'; }
.bar-item i { width: 19px; max-height: 23px; min-height: 5px; background: linear-gradient(180deg, #43caff, #1168d0); box-shadow: 0 0 5px rgba(36, 171, 255, 0.72); }
.event-card { height: 79px; padding: 5px 7px; }
.event-card h4 { position: relative; margin: 0 0 3px; padding-left: 8px; color: #78a4c7; font-size: 9px; font-weight: 400; }
.event-card h4::before { content: ''; position: absolute; left: 0; top: 1px; width: 2px; height: 9px; background: #18c9ff; box-shadow: 0 0 5px #18c9ff; }
.event-card ul,
.source-card ul { margin: 0; padding: 0; list-style: none; }
.event-card li,
.source-card li { display: flex; justify-content: space-between; gap: 6px; min-width: 0; height: 14px; color: #91b8d7; font-size: 8px; line-height: 14px; }
.event-card li span,
.source-card li span { overflow: hidden; white-space: nowrap; text-overflow: ellipsis; }
.event-card li b { flex: 0 0 auto; color: #ffc532; }
.event-card li b.event-level--many { color: #d8edf9; }

.filing-grid { margin-top: 1px; }
.filing-grid > div { display: grid; justify-items: center; min-height: 42px; padding: 4px 3px; border: 1px solid rgba(25, 98, 175, 0.5); background: rgba(2, 28, 62, 0.55); }
.filing-grid span { max-width: 100%; font-size: 9px; }
.filing-grid .number { font-size: 18px; }
.work-total { margin-top: 5px; }
.work-footer { display: grid; grid-template-columns: 1fr 1fr; gap: 6px; margin: 0 5px; }
.efficiency-card,
.source-card { height: 58px; padding: 6px 8px; }
.efficiency-card { display: grid; grid-template-columns: 1fr auto; align-items: center; }
.efficiency-card span,
.source-card__head span { position: relative; padding-left: 7px; color: #78a6c9; font-size: 9px; }
.efficiency-card span::before,
.source-card__head span::before { content: ''; position: absolute; left: 0; top: 1px; width: 2px; height: 8px; background: #18c9ff; }
.efficiency-card b { grid-row: span 2; color: var(--hot-green); font: 24px 'Triple3DIN'; text-shadow: 0 0 8px rgba(32, 237, 189, 0.58); }
.efficiency-card b small { margin-left: 3px; color: #88b4d4; font: 8px 'Triple3Text'; text-shadow: none; }
.efficiency-card em { color: #648cab; font-size: 8px; font-style: normal; }
.source-card__head { display: flex; justify-content: space-between; margin-bottom: 1px; padding-bottom: 2px; border-bottom: 1px solid rgba(23, 100, 178, 0.46); }
.source-card__head b { color: #7fb2d5; font-size: 8px; }
.source-card li { height: 10px; line-height: 10px; }
.source-card li b { flex: 0 0 auto; color: #d8edf9; }

.house-triple3 {
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  min-width: 340px;
  min-height: 420px;
  overflow: hidden;
  color: #d9f3ff;
  background:
    linear-gradient(90deg, rgba(0, 104, 210, 0.2), transparent 34px),
    linear-gradient(180deg, #03234a, #041d3c 54%, #031936);
  border: 1px solid rgba(31, 134, 229, 0.72);
  font-family: 'Triple3Text', 'Microsoft YaHei', sans-serif;
}

.house-triple3__houses {
  flex: 1 1 auto;
  width: 100% !important;
  height: auto !important;
  min-height: 0;
  border: 0;
}

// 仅三联屏启用：以原动火动焊高度的一半作为可视窗口，完整内容通过独立滚动条查看。
.house-triple3__hot-work-viewport {
  box-sizing: border-box;
  flex: 0 0 317.5px;
  width: 100%;
  height: 317.5px;
  min-height: 0;
  overflow-x: hidden;
  overflow-y: auto;
  scrollbar-color: rgba(55, 205, 255, 0.92) rgba(3, 40, 82, 0.82);
  scrollbar-width: thin;
}

.house-triple3__hot-work-viewport::-webkit-scrollbar { width: 7px; }
.house-triple3__hot-work-viewport::-webkit-scrollbar-track {
  background: rgba(3, 40, 82, 0.82);
  box-shadow: inset 0 0 5px rgba(0, 7, 24, 0.78);
}
.house-triple3__hot-work-viewport::-webkit-scrollbar-thumb {
  border: 1px solid rgba(80, 218, 255, 0.46);
  border-radius: 7px;
  background: linear-gradient(180deg, #42d9ff, #167ccf);
  box-shadow: 0 0 7px rgba(43, 190, 255, 0.72);
}

.house-triple3__hot-work {
  display: block;
  width: 100% !important;
}

.house-triple3 :deep(.hot-work-fixed) { display: none; }

.hot-work-panel {
  margin: 6px -8px 0;
  padding: 0 10px 10px;
  background:
    linear-gradient(90deg, rgba(18, 117, 217, 0.15), transparent 24px, transparent calc(100% - 24px), rgba(18, 117, 217, 0.15)),
    linear-gradient(180deg, rgba(1, 13, 34, 0.99), rgba(3, 28, 60, 0.99));
  border: 1px solid rgba(24, 113, 202, 0.72);
  box-shadow: 0 -9px 22px rgba(0, 18, 47, 0.74), inset 0 0 24px rgba(9, 92, 176, 0.16);
}

.hot-work-panel--fixed {
  flex: 0 0 635px;
  min-height: 0;
  margin: 0;
  overflow: hidden;
}

.hot-work-title {
  height: 42px;
  margin-bottom: 7px;
}

.hot-work-grid { gap: 7px; }

.safety-panel {
  padding: 5px 8px 7px;
  border-color: rgba(21, 104, 190, 0.68);
  background: linear-gradient(180deg, rgba(4, 43, 88, 0.7), rgba(2, 28, 61, 0.68));
}

.work-card__title {
  margin-bottom: 7px;
}

.summary-grid {
  gap: 7px;
  margin: 0 0 5px;
}

.summary-grid > div {
  min-height: 31px;
  padding: 3px 8px;
  border: 1px solid rgba(25, 102, 185, 0.52);
  border-left: 2px solid #21caff;
  background: repeating-linear-gradient(112deg, rgba(7, 79, 145, 0.62) 0 2px, rgba(4, 51, 105, 0.56) 2px 5px);
}

.summary-grid span { color: #b7d7ec; font-size: 11px; }
.number { font-size: 20px; }
.number--cyan { color: var(--hot-cyan); }

.progress-line {
  display: grid;
  grid-template-columns: 72px minmax(80px, 1fr) 96px;
  align-items: center;
  gap: 8px;
  min-height: 26px;
  margin: 0 1px 6px;
  color: #d2e9f8;
  font-size: 11px;
}

.progress-line .progress-track { height: 8px; margin: 0; border-radius: 0; background: rgba(64, 98, 135, 0.48); }
.progress-line .progress-track i { border-radius: 0; background: linear-gradient(90deg, #149cf1, #2ce7e9); box-shadow: none; }
.progress-line .progress-track i::after { content: ''; float: right; width: 2px; height: 12px; margin-top: -2px; background: #e8ffff; box-shadow: 0 0 5px #72ffff; }
.progress-line > b { color: #43edff; font: 15px 'Triple3DIN'; white-space: nowrap; text-align: right; text-shadow: 0 0 6px rgba(49, 220, 255, 0.55); }

.inspection-detail {
  gap: 7px;
  margin: 0;
}

.bar-card,
.event-card {
  height: 126px;
  padding: 6px 8px;
  border-color: rgba(25, 98, 175, 0.64);
}

.bar-card__head { height: 17px; font-size: 8px; }
.bar-card__summary { height: 27px; }
.bar-card__summary .number { font-size: 24px; }
.bar-card__summary strong { color: #ff6150; }
.bars {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  align-items: end;
  column-gap: 8px;
  height: 64px;
  padding: 4px 5px 1px;
  box-sizing: border-box;
  border-top: 1px dotted rgba(44, 156, 224, 0.42);
}
.bar-item {
  grid-template-rows: 12px minmax(32px, 1fr) 12px;
  row-gap: 2px;
  width: 100%;
  height: 100%;
  font-size: 8px;
}
.bar-item b { font-size: 9px; }
.bar-item i { width: 21px; max-height: 34px; background: linear-gradient(180deg, #35e1ef, #1175cf); }

.bar-card__head::before,
.event-card h4::before { display: none; }
.event-card h4 { margin-bottom: 6px; padding-left: 0; font-size: 10px; }
.event-card li { height: 21px; padding: 0 5px; background: linear-gradient(90deg, rgba(5, 80, 146, 0.6), rgba(3, 43, 91, 0.25)); font-size: 10px; line-height: 21px; }
.event-card li + li { margin-top: 2px; }

.work-orbs {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 8px;
  margin: 0 4px 7px;
}

.work-orb-item { min-width: 0; text-align: center; }
.work-orb-item > span { display: block; overflow: hidden; color: #b5d5e9; font-size: 9px; white-space: nowrap; text-overflow: ellipsis; }

.work-orb {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 40px;
  margin: 0 auto 2px;
  color: #48f0ff;
  background: radial-gradient(ellipse at center, rgba(20, 189, 225, 0.28), rgba(3, 46, 91, 0.84) 62%, transparent 66%);
  border-bottom: 2px solid rgba(40, 198, 237, 0.72);
  border-radius: 50%;
  box-shadow: 0 8px 8px rgba(0, 12, 33, 0.7), inset 0 -7px 9px rgba(24, 173, 218, 0.22);
}

.work-orb::after { content: ''; position: absolute; left: 4px; right: 4px; bottom: -5px; height: 4px; border-radius: 50%; background: rgba(34, 176, 220, 0.34); }
.work-orb b { font: 18px 'Triple3DIN'; text-shadow: 0 0 7px currentColor; }
.work-orb small { margin-left: 2px; color: #d2edf8; font-size: 8px; }
.work-orb--amber { color: #ffd33a; }

.work-total { margin-top: 0; }
.work-total > div { min-height: 34px; }

.work-footer {
  gap: 7px;
  margin: 0;
}

.efficiency-card,
.source-card {
  height: 130px;
  padding: 7px 8px;
  border-color: rgba(25, 98, 175, 0.64);
}

.efficiency-card { display: block; }
.efficiency-card__head,
.source-card__head { height: 17px; border-bottom: 0; }
.efficiency-card__head span,
.source-card__head span { color: #d0e8f5; font-size: 11px; }
.efficiency-card__summary { display: flex; align-items: flex-end; justify-content: space-between; height: 28px; }
.efficiency-card__summary b { color: #45f1ff; font: 24px 'Triple3DIN'; text-shadow: 0 0 8px rgba(49, 220, 255, 0.58); }
.efficiency-card__summary b small { margin-left: 2px; color: #8eb8d2; font: 9px 'Triple3Text'; }
.efficiency-card__summary span { color: #789db9; font-size: 8px; }
.efficiency-card__summary strong { color: #a8c9dc; }
.bars--handling { height: 64px; }

.source-card__head { display: flex; justify-content: space-between; margin: 0 0 4px; padding: 0; }
.source-card__head b { color: #8eb4cc; font-size: 9px; }
.source-card li { height: 16px; padding: 0 7px; background: linear-gradient(90deg, rgba(4, 81, 148, 0.64), rgba(3, 43, 91, 0.25)); font-size: 10px; line-height: 16px; }
.source-card li + li { margin-top: 2px; }
.source-card li b { color: #65d9ff; font: 12px 'Triple3DIN'; }

// 复用群租空间“风险感知”的文字规格，统一动火动焊指标标签。
.hot-work-panel .risk-label-text {
  color: #d9f3ff;
  font-family: 'Triple3Text', 'Microsoft YaHei', sans-serif;
  font-size: 13px;
  font-style: normal;
  font-weight: 400;
  text-shadow: none;
}

@media (max-width: 430px) {
  .hot-work-panel--fixed { flex-basis: 635px; }
  .progress-line { grid-template-columns: 54px minmax(58px, 1fr) 82px; gap: 5px; }
  .progress-line > b { font-size: 12px; }
  .summary-grid > div { padding-right: 4px; padding-left: 4px; }
  .inspection-detail,
  .work-footer { gap: 4px; }
  .event-card li,
  .source-card li { padding-right: 3px; padding-left: 3px; font-size: 8px; }
}
</style>
