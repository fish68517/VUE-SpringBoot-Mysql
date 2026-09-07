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
    <section class="single-hot-work">
      <div class="hot-work-title"><span>动火动焊</span></div>

      <div class="hot-work-grid">
        <section class="safety-panel" @click="openGridDanger">
          <div class="work-card__title"><i></i><strong>网格巡查隐患</strong></div>
          <div class="summary-grid summary-grid--two">
            <div><span>未整改</span><b class="number number--amber">47<small>件</small></b></div>
            <div><span>已整改</span><b class="number number--green">43971<small>件</small></b></div>
          </div>
          <div class="progress-block">
            <div class="progress-track"><i style="width: 99.89%"></i></div>
            <div class="progress-meta"><span>整改进度</span><span>43971 / 44018</span></div>
          </div>

          <div class="inspection-detail">
            <div class="bar-card">
              <div class="bar-card__head">
                <span>新增隐患</span><time>2026-08-29 ~ 09-01</time>
              </div>
              <div class="bar-card__summary">
                <b class="number number--amber">554<small>件</small></b>
                <span>日均新增 <strong>139</strong> 件</span>
              </div>
              <div class="bars" aria-label="近四日新增隐患柱状图">
                <div v-for="bar in inspectionBars" :key="bar.date" class="bar-item">
                  <b>{{ bar.value }}</b><i :style="{ height: `${bar.height}%` }"></i><span>{{ bar.date }}</span>
                </div>
              </div>
            </div>
            <div class="event-card">
              <h4>高频隐患点</h4>
              <ul>
                <li v-for="item in dangerPoints" :key="item.name">
                  <span>{{ item.name }}</span><b :class="{ 'event-level--many': item.level === '多发' }">{{ item.level }}</b>
                </li>
              </ul>
            </div>
          </div>
        </section>

        <section class="safety-panel" @click="openHotWorkDanger">
          <div class="work-card__title"><i></i><strong>近期动火作业隐患</strong></div>
          <div class="summary-grid summary-grid--three filing-grid">
            <div><span>近7天动火作业备案</span><b class="number number--amber">7<small>起</small></b></div>
            <div><span>发现隐患 · 已整改</span><b class="number number--green">0<small>件</small></b></div>
            <div><span>发现隐患 · 未完成</span><b class="number number--green">0<small>件</small></b></div>
          </div>
          <div class="summary-grid summary-grid--three work-total">
            <div><span>隐患总数</span><b class="number number--amber">471<small>件</small></b></div>
            <div><span>未完成</span><b class="number number--green">0<small>件</small></b></div>
            <div><span>已结案</span><b class="number number--green">471<small>件</small></b></div>
          </div>
          <div class="progress-block">
            <div class="progress-track"><i style="width: 100%"></i></div>
            <div class="progress-meta"><span>处置进度</span><span>471 / 471 · 全部结案</span></div>
          </div>
          <div class="work-footer">
            <div class="efficiency-card">
              <span>处置时效</span>
              <b>42<small>分钟</small></b>
              <em>平均处置时长</em>
            </div>
            <div class="source-card">
              <div class="source-card__head"><span>事件来源</span><b>合计 471 件</b></div>
              <ul>
                <li v-for="item in eventSources" :key="item.name"><span>{{ item.name }}</span><b>{{ item.value }} 件</b></li>
              </ul>
            </div>
          </div>
        </section>
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

const inspectionBars = [
  { date: '08-29', value: 132, height: 62 },
  { date: '08-30', value: 145, height: 82 },
  { date: '08-31', value: 138, height: 70 },
  { date: '09-01', value: 139, height: 74 }
]

const dangerPoints = [
  { name: '灭火器未放在醒目位置', level: '高发' },
  { name: '消防控制室锁闭', level: '高发' },
  { name: '设备设施安全隐患', level: '多发' },
  { name: '疏散通道堆放杂物', level: '多发' }
]

const eventSources = [
  { name: '群众上报', value: 362 },
  { name: '网格员巡查', value: 58 },
  { name: '物联感知', value: 31 },
  { name: '部门移送', value: 20 }
]

function openGridDanger() {
  emitDia('house-grid-inspection-hidden')
}

function openHotWorkDanger() {
  emitDia('house-fire-work-hidden')
}

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
// 新增样式仅作用于单屏动火动焊模块。
.single-hot-work {
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
  height: 28px;
  margin: 0 0 5px;
  padding-left: 31px;
  overflow: hidden;
  background: linear-gradient(90deg, rgba(5, 105, 183, 0.9), rgba(4, 75, 139, 0.56) 78%, transparent);
  color: #f0fbff;
  font-size: 17px;
  font-style: italic;
  letter-spacing: 1px;
  text-shadow: 0 0 8px #32baff;
}

.work-card__title i {
  position: absolute;
  left: 4px;
  width: 23px;
  height: 23px;
  border-radius: 50%;
  background: radial-gradient(circle at 30% 40%, #74eaff 0 8%, #0889cf 35%, #063f89 65%, transparent 70%);
  box-shadow: 0 0 8px #12aaff;
}

.work-card__title i::after { content: ''; position: absolute; inset: 5px; border: 1px solid rgba(154, 240, 255, 0.74); border-radius: 50%; }
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
</style>
