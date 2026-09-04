<template>
  <BZHouseHotWorkTriple2
    v-if="isTripleScreen"
    :chart-config="props.chartConfig"
    :bus="props.bus"
  />

  <div
    v-else
    class="house-single"
    :class="{ 'house-single--transparent': !showBackground }"
    :style="containerStyle"
  >
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
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import BZHouseHotWorkTriple2 from './BZHouseHotWorkTriple2.vue'

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
</style>
