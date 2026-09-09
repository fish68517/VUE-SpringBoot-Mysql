<template>
  <div
    class="bz-hf-and-welding"
    :class="{ 'bz-hf-and-welding--embedded': embedded }"
    :style="containerStyle"
  >
    <div class="hot-work-title"><span>动火动焊</span></div>

    <div class="hot-work-grid">
      <section class="safety-panel" @click="openGridDanger">
        <div class="work-card__title"><i></i><strong>网格巡查隐患</strong></div>
        <div class="summary-grid summary-grid--two">
          <div><span class="risk-label-text">未整改</span><b class="number number--amber">24<small>件</small></b></div>
          <div><span class="risk-label-text">已整改</span><b class="number number--green">4211<small>件</small></b></div>
        </div>
        <div class="progress-line">
          <span class="risk-label-text">整改进度</span>
          <div class="progress-track"><i style="width: 88%"></i></div>
          <b>4200 / 4211</b>
        </div>

        <div class="inspection-detail">
          <div class="bar-card">
            <div class="bar-card__head">
              <span class="risk-label-text">新增隐患</span><time>2026-08-29 ~ 09-01</time>
            </div>
            <div class="bar-card__summary">
              <b class="number number--cyan">123<small>件</small></b>
              <span>日均新增 <strong>24件 ↑</strong></span>
            </div>
            <div class="bars" aria-label="近四日新增隐患柱状图">
              <div v-for="bar in inspectionBars" :key="bar.date" class="bar-item">
                <b>{{ bar.value }}</b><i :style="{ height: `${bar.height}%` }"></i><span>{{ bar.date }}</span>
              </div>
            </div>
          </div>
          <div class="event-card">
            <h4 class="risk-label-text">高频隐患点</h4>
            <ul>
              <li v-for="item in dangerPoints" :key="item.name">
                <span class="risk-label-text">{{ item.name }}</span>
                <b :class="{ 'event-level--many': item.level === '多发' }">{{ item.level }}</b>
              </li>
            </ul>
          </div>
        </div>
      </section>

      <section class="safety-panel" @click="openHotWorkDanger">
        <div class="work-card__title"><i></i><strong>近期动火作业隐患</strong></div>
        <div class="work-orbs">
          <div v-for="item in hotWorkOverview" :key="item.label" class="work-orb-item">
            <div class="work-orb" :class="{ 'work-orb--amber': item.tone === 'amber' }">
              <b>{{ item.value }}</b><small>{{ item.unit }}</small>
            </div>
            <span>{{ item.label }}</span>
          </div>
        </div>
        <div class="progress-line progress-line--resolved">
          <span class="risk-label-text">处置进度</span>
          <div class="progress-track"><i style="width: 88%"></i></div>
          <b>471 / 471 全部销案</b>
        </div>
        <div class="work-footer">
          <div class="efficiency-card">
            <div class="efficiency-card__head"><span class="risk-label-text">处置时效</span></div>
            <div class="efficiency-card__summary">
              <b>12<small>分钟</small></b><span>平均处置时长 <strong>12分钟</strong></span>
            </div>
            <div class="bars bars--handling" aria-label="近四日处置时效柱状图">
              <div v-for="bar in handlingBars" :key="bar.date" class="bar-item">
                <b>{{ bar.value }}</b><i :style="{ height: `${bar.height}%` }"></i><span>{{ bar.date }}</span>
              </div>
            </div>
          </div>
          <div class="source-card">
            <div class="source-card__head"><span class="risk-label-text">事件来源</span><b>合计：720件</b></div>
            <ul>
              <li v-for="item in eventSources" :key="item.name">
                <span class="risk-label-text">{{ item.name }}</span><b>{{ item.value }} 件</b>
              </li>
            </ul>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps({
  chartConfig: { type: Object, default: () => ({}) },
  bus: { type: Object, default: null },
  embedded: { type: Boolean, default: false }
})

const containerStyle = computed(() => {
  if (props.embedded) return undefined
  const width = Number(props.chartConfig?.attr?.w)
  const height = Number(props.chartConfig?.attr?.h)
  return {
    width: Number.isFinite(width) && width > 0 ? `${width}px` : '500px',
    height: Number.isFinite(height) && height > 0 ? `${height}px` : '635px'
  }
})

const inspectionBars = [
  { date: '08-29', value: 213, height: 75 },
  { date: '08-30', value: 213, height: 75 },
  { date: '08-31', value: 124, height: 46 },
  { date: '09-01', value: 12, height: 25 }
]

const dangerPoints = [
  { name: '灭火器未放在显著位置', level: '高发' },
  { name: '消防设施被遮挡', level: '高发' },
  { name: '设施设备安全隐患', level: '多发' },
  { name: '动火现场监护缺失', level: '多发' }
]

const hotWorkOverview = [
  { label: '近7天动火作业报备', value: 42, unit: '起', tone: 'cyan' },
  { label: '发现隐患 · 已销案', value: 42, unit: '件', tone: 'cyan' },
  { label: '发现隐患 · 未完成', value: 42, unit: '件', tone: 'amber' }
]

const handlingBars = [
  { date: '08-29', value: 959, height: 82 },
  { date: '08-30', value: 741, height: 68 },
  { date: '08-31', value: 147, height: 37 },
  { date: '09-01', value: 112, height: 30 }
]

const eventSources = [
  { name: '群众上报', value: 720 },
  { name: '网格员巡查', value: 453 },
  { name: '物联感知', value: 320 },
  { name: '部门移送', value: 180 }
]

function emitDia(diaName: string) {
  props.bus?.emit('OPEN_HOUSE_DIA', {
    source: 'BzHFAndwelding',
    data: { diaName }
  })
}

function openGridDanger() {
  emitDia('house-grid-inspection-hidden')
}

function openHotWorkDanger() {
  emitDia('house-fire-work-hidden')
}
</script>

<script lang="ts">
export default { name: 'BzHFAndwelding', version: '1.0.0' }
</script>

<style lang="scss" scoped>
@font-face {
  font-family: 'Triple3Text';
  src: url('../BZHouse/font/Alibaba_PuHuiTi_2.0_55_Regular_55_Regular.ttf') format('truetype');
  font-display: swap;
}

@font-face {
  font-family: 'Triple3DIN';
  src: url('../BZHouse/font/D-DIN-Bold.otf') format('opentype');
  font-display: swap;
}

.bz-hf-and-welding {
  --hot-cyan: #31dcff;
  --hot-green: #20edbd;
  --hot-amber: #ffbd2f;
  box-sizing: border-box;
  min-width: 340px;
  min-height: 420px;
  padding: 0 10px 10px;
  overflow: hidden;
  color: #d9f3ff;
  background:
    linear-gradient(90deg, rgba(18, 117, 217, 0.15), transparent 24px, transparent calc(100% - 24px), rgba(18, 117, 217, 0.15)),
    linear-gradient(180deg, rgba(1, 13, 34, 0.99), rgba(3, 28, 60, 0.99));
  border: 1px solid rgba(24, 113, 202, 0.72);
  box-shadow: 0 -9px 22px rgba(0, 18, 47, 0.74), inset 0 0 24px rgba(9, 92, 176, 0.16);
  font-family: 'Triple3Text', 'Microsoft YaHei', sans-serif;
  line-height: 1.5;
}

.bz-hf-and-welding--embedded {
  flex: 0 0 635px;
  width: 100%;
  height: 635px;
  min-height: 0;
}

.hot-work-title {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 42px;
  margin: 0 -10px 7px;
  overflow: hidden;
  background:
    url('../BZHouse/img/bg_title_long.png') center / 100% 100% no-repeat,
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

.hot-work-grid { display: grid; grid-template-columns: 1fr; gap: 7px; }

.safety-panel {
  box-sizing: border-box;
  width: 100%;
  padding: 5px 8px 7px;
  border: 1px solid rgba(21, 104, 190, 0.68);
  background: linear-gradient(180deg, rgba(4, 43, 88, 0.7), rgba(2, 28, 61, 0.68));
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
  margin: 0 0 7px;
  padding-left: 39px;
  box-sizing: border-box;
  overflow: hidden;
  background: url('../BZHouse/img/bg_second_title.png') center / 100% 100% no-repeat;
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

.summary-grid { display: grid; gap: 7px; margin: 0 0 5px; }
.summary-grid--two { grid-template-columns: repeat(2, minmax(0, 1fr)); }
.summary-grid--three { grid-template-columns: repeat(3, minmax(0, 1fr)); }
.summary-grid > div {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  min-width: 0;
  min-height: 31px;
  padding: 3px 8px;
  box-sizing: border-box;
  border: 1px solid rgba(25, 102, 185, 0.52);
  border-left: 2px solid #21caff;
  background: repeating-linear-gradient(112deg, rgba(7, 79, 145, 0.62) 0 2px, rgba(4, 51, 105, 0.56) 2px 5px);
}

.number { color: var(--hot-cyan); font: 20px/1 'Triple3DIN'; text-shadow: 0 0 7px currentColor; }
.number small { margin-left: 2px; color: #84a8c5; font: 8px 'Triple3Text'; text-shadow: none; }
.number--amber { color: var(--hot-amber); }
.number--green { color: var(--hot-green); }
.number--cyan { color: var(--hot-cyan); }

.progress-line {
  display: grid;
  grid-template-columns: 72px minmax(80px, 1fr) 96px;
  align-items: center;
  gap: 8px;
  min-height: 26px;
  margin: 0 1px 6px;
}

.progress-track { height: 8px; overflow: hidden; background: rgba(64, 98, 135, 0.48); }
.progress-track i { display: block; height: 100%; background: linear-gradient(90deg, #149cf1, #2ce7e9); }
.progress-track i::after { content: ''; float: right; width: 2px; height: 12px; margin-top: -2px; background: #e8ffff; box-shadow: 0 0 5px #72ffff; }
.progress-line > b { color: #43edff; font: 15px 'Triple3DIN'; white-space: nowrap; text-align: right; text-shadow: 0 0 6px rgba(49, 220, 255, 0.55); }

.inspection-detail,
.work-footer { display: grid; grid-template-columns: 1fr 1fr; gap: 7px; }

.bar-card,
.event-card,
.efficiency-card,
.source-card { box-sizing: border-box; min-width: 0; border: 1px solid rgba(25, 98, 175, 0.64); background: rgba(2, 28, 62, 0.72); }
.bar-card,
.event-card { height: 126px; padding: 6px 8px; }
.bar-card__head { display: flex; align-items: center; justify-content: space-between; height: 17px; color: #658fb6; font-size: 8px; }
.bar-card__head span { margin-right: auto; }
.bar-card__head time { color: #7698b8; font-size: 8px; }
.bar-card__summary { display: flex; align-items: flex-end; justify-content: space-between; height: 27px; }
.bar-card__summary .number { font-size: 24px; }
.bar-card__summary span { color: #6489aa; font-size: 8px; }
.bar-card__summary strong { color: #ff6150; font-family: 'Triple3DIN'; }

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
  display: grid;
  grid-template-rows: 12px minmax(32px, 1fr) 12px;
  justify-items: center;
  align-items: end;
  row-gap: 2px;
  width: 100%;
  height: 100%;
  color: #6995bd;
  font-size: 8px;
}

.bar-item b { color: #8bc8fb; font: 9px 'Triple3DIN'; }
.bar-item i { width: 21px; max-height: 34px; min-height: 5px; background: linear-gradient(180deg, #35e1ef, #1175cf); box-shadow: 0 0 5px rgba(36, 171, 255, 0.72); }

.event-card h4 { margin: 0 0 6px; padding-left: 0; font-size: 10px; font-weight: 400; }
.event-card ul,
.source-card ul { margin: 0; padding: 0; list-style: none; }
.event-card li,
.source-card li { display: flex; justify-content: space-between; gap: 6px; min-width: 0; }
.event-card li { height: 21px; padding: 0 5px; background: linear-gradient(90deg, rgba(5, 80, 146, 0.6), rgba(3, 43, 91, 0.25)); font-size: 10px; line-height: 21px; }
.event-card li + li { margin-top: 2px; }
.event-card li span,
.source-card li span { overflow: hidden; white-space: nowrap; text-overflow: ellipsis; }
.event-card li b { flex: 0 0 auto; color: #ffc532; }
.event-card li b.event-level--many { color: #d8edf9; }

.work-orbs { display: grid; grid-template-columns: repeat(3, minmax(0, 1fr)); gap: 8px; margin: 0 4px 7px; }
.work-orb-item { min-width: 0; text-align: center; }
.work-orb-item > span { display: block; overflow: hidden; color: #b5d5e9; font-size: 9px; white-space: nowrap; text-overflow: ellipsis; }
.work-orb {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 64px;
  height: 47px;
  margin: 0 auto 2px;
  color: #48f0ff;
  background: url('./img/hot-work-overview-orb.png') center / 64px 47px no-repeat;
}
.work-orb b { font: 18px 'Triple3DIN'; text-shadow: 0 0 7px currentColor; }
.work-orb small { margin-left: 2px; color: #d2edf8; font-size: 8px; }
.work-orb--amber { color: #ffd33a; }

.progress-line--resolved {
  grid-template-columns: 72px 230px minmax(138px, 1fr);
}

.progress-line--resolved .progress-track {
  width: 230px;
}
.efficiency-card,
.source-card { height: 130px; padding: 7px 8px; }
.efficiency-card__head,
.source-card__head { height: 17px; }
.efficiency-card__summary { display: flex; align-items: flex-end; justify-content: space-between; height: 28px; }
.efficiency-card__summary b { color: #45f1ff; font: 24px 'Triple3DIN'; text-shadow: 0 0 8px rgba(49, 220, 255, 0.58); }
.efficiency-card__summary b small { margin-left: 2px; color: #8eb8d2; font: 9px 'Triple3Text'; }
.efficiency-card__summary span { color: #789db9; font-size: 8px; }
.efficiency-card__summary strong { color: #a8c9dc; }
.bars--handling { height: 64px; }
.source-card__head { display: flex; justify-content: space-between; margin: 0 0 4px; }
.source-card__head b { color: #8eb4cc; font-size: 9px; }
.source-card li { height: 16px; padding: 0 7px; background: linear-gradient(90deg, rgba(4, 81, 148, 0.64), rgba(3, 43, 91, 0.25)); font-size: 10px; line-height: 16px; }
.source-card li + li { margin-top: 2px; }
.source-card li b { flex: 0 0 auto; color: #65d9ff; font: 12px 'Triple3DIN'; }

.risk-label-text { color: #d9f3ff; font-family: 'Triple3Text', 'Microsoft YaHei', sans-serif; font-size: 13px; font-style: normal; font-weight: 400; text-shadow: none; }

@media (max-width: 430px) {
  .progress-line { grid-template-columns: 54px minmax(58px, 1fr) 82px; gap: 5px; }
  .progress-line--resolved { grid-template-columns: 54px minmax(50px, 1fr) 110px; }
  .progress-line--resolved .progress-track { width: 100%; }
  .progress-line > b { font-size: 12px; }
  .summary-grid > div { padding-right: 4px; padding-left: 4px; }
  .inspection-detail,
  .work-footer { gap: 4px; }
  .event-card li,
  .source-card li { padding-right: 3px; padding-left: 3px; font-size: 8px; }
}
</style>
