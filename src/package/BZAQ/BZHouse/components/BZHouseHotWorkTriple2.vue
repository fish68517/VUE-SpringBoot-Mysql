<template>
  <div
    class="house-triple2"
    :class="{ 'house-triple2--transparent': !showBackground }"
    :style="containerStyle"
  >
    <div class="house-triple2__scroll">
      <section v-for="house in houseSections" :key="house.key" class="house-module">
        <button type="button" class="module-title" @click="openHouseList(house.name)">
          <i></i><span>{{ house.name }}</span>
        </button>

        <div class="dual-heading">
          <button type="button" @click="openHouseList(house.name)">› {{ house.manageLabel }}</button>
          <button type="button" @click="openHousePersonList(house.name)">› 重点人员</button>
        </div>

        <div class="status-grid">
          <div v-for="item in house.status" :key="item.label" class="status-chip" :class="`status-chip--${item.tone}`">
            <span>{{ item.label }}</span><b>{{ item.value }}</b>
          </div>
        </div>

        <button type="button" class="event-heading" @click="openHouseRisk(house.name)">› 重点事件</button>
        <div class="risk-grid" :class="{ 'risk-grid--two': house.risks.length === 2 }">
          <button v-for="item in house.risks" :key="item.label" type="button" class="risk-chip" @click="openHouseRisk(house.name)">
            <span>{{ item.label }}</span><b>{{ item.current }}<i>/{{ item.total }}</i></b>
          </button>
        </div>

        <button type="button" class="code-panel" @click="openColorCode(house.name)">
          <span class="code-panel__title">› 四色评价</span>
          <span class="code-grid">
            <span v-for="item in colorCodes" :key="item.label" class="code-chip" :class="`code-chip--${item.tone}`">
              <em>{{ item.label }}</em><b>{{ item.value }} <i>/ 99.99%</i></b>
            </span>
          </span>
        </button>

        <div class="photo-grid">
          <article v-for="(title, index) in house.photos" :key="index" class="photo-card">
            <img :src="placeholderImg" alt="房屋巡查现场" />
            <p>{{ title }}</p>
          </article>
        </div>
      </section>
    </div>

    <section class="hot-work-fixed">
      <div class="hot-work-title"><span>动火动焊</span></div>

      <div class="hot-work-grid">
        <button type="button" class="work-card" @click="openGridDanger">
          <div class="work-card__title"><i></i><span>网格巡查隐患</span><em>GRID INSPECTION</em></div>
          <div class="metric-row metric-row--two">
            <span><em>未整改</em><b class="amber">47<small>件</small></b></span>
            <span><em>已整改</em><b>43971<small>件</small></b></span>
          </div>
          <div class="progress"><i style="width: 99.89%"></i></div>
          <div class="progress-meta"><span>整改进度</span><span>43971 / 44018</span></div>
          <div class="work-card__footer">
            <span>近4日新增 <b class="amber">554</b> 件</span>
            <span>高频隐患 <b>4</b> 类</span>
          </div>
        </button>

        <button type="button" class="work-card" @click="openHotWorkDanger">
          <div class="work-card__title"><i></i><span>近期动火作业隐患</span><em>HOT WORK</em></div>
          <div class="metric-row metric-row--three">
            <span><em>隐患总数</em><b class="amber">471<small>件</small></b></span>
            <span><em>未完成</em><b>0<small>件</small></b></span>
            <span><em>已结案</em><b>471<small>件</small></b></span>
          </div>
          <div class="progress"><i style="width: 100%"></i></div>
          <div class="progress-meta"><span>处置进度</span><span>471 / 471 · 全部结案</span></div>
          <div class="work-card__footer">
            <span>近7天隐患 <b class="amber">7</b> 起</span>
            <span>平均处置 <b>42</b> 分钟</span>
          </div>
        </button>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import placeholderImg from '../img/zhanwei.png'

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

const commonPhotos = ['渝中区解放碑街道巡查...', '渝中区菜园坝街道巡查...', '渝中区朝天门街道巡查...']

const houseSections = [
  { key: 'online', name: '网约房', manageLabel: '纳管情况', status: commonStatus, risks: commonRisks, photos: commonPhotos },
  { key: 'group', name: '群租空间', manageLabel: '房屋情况', status: commonStatus, risks: commonRisks, photos: commonPhotos },
  {
    key: 'business',
    name: '商改住',
    manageLabel: '纳管情况',
    status: commonStatus,
    risks: [
      { label: '巡查上报', current: 11, total: 11 },
      { label: '风险感知', current: 11, total: 11 }
    ],
    photos: commonPhotos
  }
]

function emitDia(diaName: string, extra: Record<string, unknown> = {}) {
  props.bus?.emit('OPEN_HOUSE_DIA', {
    source: 'BZHouseHotWorkTriple2',
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

function openGridDanger() {
  emitDia('house-grid-inspection-hidden')
}

function openHotWorkDanger() {
  emitDia('house-fire-work-hidden')
}
</script>

<script lang="ts">
export default { name: 'BZHouseHotWorkTriple2' }
</script>

<style lang="scss" scoped>
@font-face {
  font-family: 'Triple2Text';
  src: url('../font/Alibaba_PuHuiTi_2.0_55_Regular_55_Regular.ttf') format('truetype');
  font-display: swap;
}

@font-face {
  font-family: 'Triple2DIN';
  src: url('../font/D-DIN-Bold.otf') format('opentype');
  font-display: swap;
}

.house-triple2 {
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
  font-family: 'Triple2Text', 'Microsoft YaHei', sans-serif;
}

.house-triple2--transparent { background: transparent; }

button {
  padding: 0;
  border: 0;
  color: inherit;
  background: none;
  font: inherit;
  text-align: left;
  cursor: pointer;
}

.house-triple2__scroll {
  flex: 1 1 auto;
  min-height: 0;
  overflow-x: hidden;
  overflow-y: auto;
  padding: 4px 7px 8px;
  scrollbar-width: thin;
  scrollbar-color: rgba(61, 190, 255, 0.8) rgba(5, 42, 84, 0.62);
}

.house-triple2__scroll::-webkit-scrollbar { width: 4px; }
.house-triple2__scroll::-webkit-scrollbar-track { background: rgba(5, 42, 84, 0.62); }
.house-triple2__scroll::-webkit-scrollbar-thumb { border-radius: 4px; background: linear-gradient(#40d8ff, #187bd9); }

.house-module { padding: 0 2px 9px; }
.house-module + .house-module { padding-top: 6px; border-top: 1px solid rgba(20, 110, 201, 0.34); }

.module-title {
  position: relative;
  display: flex;
  align-items: center;
  width: 100%;
  height: 31px;
  padding-left: 39px;
  overflow: hidden;
  background: url('../img/bg_second_title.png') center / 100% 100% no-repeat;
  color: #f2fbff;
  font-size: 20px;
  font-weight: 700;
  font-style: italic;
  letter-spacing: 1px;
  text-shadow: 0 0 9px #33baff;
}

.module-title i {
  position: absolute;
  left: 5px;
  width: 27px;
  height: 27px;
  border-radius: 50%;
  background: radial-gradient(circle at 30% 38%, #79ecff 0 8%, #098dce 36%, #063c82 66%, transparent 70%);
  box-shadow: 0 0 9px #16a9ff;
}

.dual-heading {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  height: 21px;
  margin-top: 5px;
}

.dual-heading button,
.event-heading {
  color: #daf2ff;
  font-size: 14px;
  line-height: 21px;
}

.event-heading { display: block; width: 100%; height: 20px; }

.status-grid,
.risk-grid {
  display: grid;
  gap: 6px;
}

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
  height: 24px;
  padding: 0 6px;
  overflow: hidden;
  border-left: 2px solid #2dc7ff;
  background: linear-gradient(90deg, rgba(4, 77, 146, 0.88), rgba(5, 40, 84, 0.48));
  font-size: 13px;
}

.status-chip span,
.risk-chip span { overflow: hidden; white-space: nowrap; text-overflow: ellipsis; }
.status-chip b,
.risk-chip b { flex: 0 0 auto; color: #52f0ff; font: 15px 'Triple2DIN'; text-shadow: 0 0 7px currentColor; }
.status-chip--green { border-color: #19df94; }
.status-chip--green b { color: #24efa3; }
.status-chip--amber { border-color: #f4a936; }
.status-chip--amber b { color: #ffbd43; }

.risk-chip { width: 100%; border: 1px solid rgba(27, 127, 220, 0.75); background: repeating-linear-gradient(112deg, rgba(8, 84, 153, 0.58) 0 2px, rgba(4, 52, 106, 0.54) 2px 5px); }
.risk-chip b { color: #ffd333; }
.risk-chip b i { color: #53edff; font: inherit; }

.code-panel {
  display: grid;
  grid-template-columns: 112px 1fr;
  gap: 5px;
  width: 100%;
  margin-top: 5px;
  padding: 4px;
  box-sizing: border-box;
  border: 1px solid rgba(24, 121, 218, 0.88);
  background: linear-gradient(90deg, rgba(4, 61, 125, 0.95), rgba(6, 49, 105, 0.58));
}

.code-panel__title { display: flex; align-items: center; justify-content: center; color: #e7f7ff; font-size: 16px; }
.code-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 3px; }
.code-chip { display: flex; justify-content: space-between; min-width: 0; height: 20px; padding: 0 6px; line-height: 20px; border-left: 2px solid currentColor; background: rgba(4, 67, 130, 0.44); font-size: 12px; }
.code-chip em { font-style: normal; }
.code-chip b { font: 12px 'Triple2DIN'; white-space: nowrap; }
.code-chip i { color: #66ddf7; font: inherit; }
.code-chip--green { color: #2fe99a; }
.code-chip--blue { color: #34b3ff; }
.code-chip--yellow { color: #ffd02c; }
.code-chip--red { color: #ff5d72; }

.photo-grid { display: grid; grid-template-columns: repeat(3, minmax(0, 1fr)); gap: 6px; margin-top: 5px; }
.photo-card { min-width: 0; overflow: hidden; border: 1px solid rgba(29, 129, 226, 0.78); background: #052853; }
.photo-card img { display: block; width: 100%; height: 51px; object-fit: cover; filter: saturate(0.84) brightness(0.9); }
.photo-card p { height: 18px; margin: 0; padding: 0 5px; overflow: hidden; color: #bfe8ff; font-size: 9px; line-height: 18px; white-space: nowrap; text-overflow: ellipsis; }

.hot-work-fixed {
  flex: 0 0 284px;
  box-sizing: border-box;
  padding: 0 8px 10px;
  background: linear-gradient(180deg, rgba(1, 14, 36, 0.98), rgba(3, 29, 62, 0.98));
  border-top: 1px solid rgba(16, 106, 196, 0.82);
  box-shadow: 0 -10px 24px rgba(0, 18, 47, 0.72);
}

.hot-work-title {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 47px;
  margin: 0 -8px 7px;
  overflow: hidden;
  background: url('../img/bg_title_long.png') center / 100% 100% no-repeat;
}

.hot-work-title span { color: #eefbff; font-size: 21px; font-weight: 700; letter-spacing: 2px; text-shadow: 0 0 11px #32c4ff; }

.hot-work-grid { display: grid; grid-template-columns: 1fr; gap: 7px; }

.work-card {
  box-sizing: border-box;
  width: 100%;
  min-height: 106px;
  padding: 6px 9px;
  border: 1px solid rgba(25, 119, 207, 0.82);
  background: linear-gradient(90deg, rgba(4, 48, 101, 0.94), rgba(3, 33, 74, 0.86));
  box-shadow: inset 0 0 15px rgba(16, 112, 206, 0.14);
}

.work-card:hover { filter: brightness(1.1); }

.work-card__title { display: flex; align-items: center; height: 21px; margin-bottom: 4px; background: linear-gradient(90deg, rgba(5, 96, 171, 0.76), transparent); }
.work-card__title i { width: 16px; height: 16px; margin: 0 6px 0 2px; border-radius: 50%; background: radial-gradient(circle, #6eebff 0 12%, #0985d0 42%, #053e85 70%); box-shadow: 0 0 7px #1ba7ff; }
.work-card__title span { font-size: 15px; font-weight: 700; }
.work-card__title em { margin-left: auto; padding-right: 5px; color: rgba(69, 178, 239, 0.54); font: 9px 'Triple2DIN'; letter-spacing: 1px; }

.metric-row { display: grid; gap: 5px; }
.metric-row--two { grid-template-columns: repeat(2, 1fr); }
.metric-row--three { grid-template-columns: repeat(3, 1fr); }
.metric-row > span { display: flex; align-items: flex-end; justify-content: space-between; min-width: 0; height: 25px; padding: 0 7px 2px; box-sizing: border-box; border-left: 2px solid #29c8f5; background: linear-gradient(90deg, rgba(5, 77, 145, 0.58), transparent); }
.metric-row em { color: #afd5ed; font-size: 11px; font-style: normal; white-space: nowrap; }
.metric-row b { color: #31efc0; font: 20px 'Triple2DIN'; text-shadow: 0 0 7px currentColor; }
.metric-row b.amber { color: #ffc33c; }
.metric-row small { margin-left: 2px; font: 8px 'Triple2Text'; }

.progress { height: 6px; margin-top: 5px; overflow: hidden; border-radius: 4px; background: rgba(2, 25, 55, 0.96); }
.progress i { display: block; height: 100%; border-radius: inherit; background: linear-gradient(90deg, #16d6a0, #34f3c1); box-shadow: 0 0 7px #20e8b9; }
.progress-meta { display: flex; justify-content: space-between; margin-top: 2px; color: #5f87a9; font-size: 8px; }
.work-card__footer { display: flex; justify-content: space-between; margin-top: 3px; padding-top: 3px; border-top: 1px solid rgba(26, 102, 175, 0.36); color: #75a5c7; font-size: 9px; }
.work-card__footer b { color: #31eeca; font-family: 'Triple2DIN'; }
.work-card__footer b.amber { color: #ffc13a; }

@media (max-width: 430px) {
  .status-chip, .risk-chip { padding: 0 3px; font-size: 11px; }
  .code-panel { grid-template-columns: 88px 1fr; }
  .code-chip { padding: 0 3px; font-size: 10px; }
  .hot-work-fixed { flex-basis: 278px; }
}
</style>
