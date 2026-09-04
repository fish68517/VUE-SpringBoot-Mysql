<template>
  <div class="hot-work-screen">
    <div class="screen-ornament" aria-hidden="true">
      <span></span><i></i><span></span>
    </div>

    <div class="screen-user">
      <span class="screen-user__person">♟</span>
      <span>李红岩</span>
      <span class="screen-user__status"></span>
    </div>

    <header class="hero-title">
      <button type="button" class="hero-arrow" aria-label="上一个模块">‹</button>
      <div class="hero-title__content">
        <strong>生产安全</strong>
        <span>住商互改</span>
      </div>
      <button type="button" class="hero-arrow hero-arrow--right" aria-label="下一个模块">›</button>
    </header>

    <section v-for="house in houseSections" :key="house.name" class="house-card">
      <ModuleTitle :title="house.name" />
      <div class="dual-label">
        <span>› 纳管情况</span>
        <span>› 重点人员</span>
      </div>
      <div class="stat-grid stat-grid--four">
        <div v-for="item in house.stats" :key="item.label" class="stat-chip" :class="`stat-chip--${item.tone}`">
          <span>{{ item.label }}</span><b>{{ item.value }}</b>
        </div>
      </div>
      <div class="minor-label">› 重点事件</div>
      <div class="risk-grid">
        <div v-for="item in house.risks" :key="item.label" class="risk-chip">
          <span>{{ item.label }}</span><b>{{ item.current }}<i>/{{ item.total }}</i></b>
        </div>
      </div>
      <div class="code-row">
        <span class="code-row__title">› 四色评价</span>
        <div v-for="item in colorCodes" :key="item.label" class="code-chip" :class="`code-chip--${item.tone}`">
          <span>{{ item.label }}</span><b>{{ item.value }} <i>/ 99.99%</i></b>
        </div>
      </div>
      <div class="photo-grid">
        <article v-for="(photo, index) in house.photos" :key="index" class="photo-card">
          <img :src="placeholderImg" alt="房屋巡查现场" />
          <p>{{ photo }}</p>
        </article>
      </div>
    </section>

    <div class="main-divider"><span>动火动焊</span></div>

    <section class="safety-panel">
      <ModuleTitle title="网格巡查隐患" />
      <div class="summary-grid summary-grid--two">
        <div><span>未整改</span><b class="number number--amber">47<small>件</small></b></div>
        <div><span>已整改</span><b class="number number--green">43971<small>件</small></b></div>
      </div>
      <ProgressBar :value="99.89" label="整改进度" tail="43971 / 44018" />

      <div class="inspection-detail">
        <div class="bar-card">
          <div class="bar-card__head"><b class="number number--amber">554<small>件</small></b><span>新增隐患</span><time>2026-08-29 ~ 09-01</time></div>
          <div class="bars" aria-label="近四日新增隐患柱状图">
            <div v-for="bar in inspectionBars" :key="bar.date" class="bar-item">
              <b>{{ bar.value }}</b><i :style="{ height: bar.height + '%' }"></i><span>{{ bar.date }}</span>
            </div>
          </div>
        </div>
        <div class="event-card">
          <h4>高频隐患点</h4>
          <ul>
            <li v-for="item in dangerPoints" :key="item.name"><span>{{ item.name }}</span><b>{{ item.level }}</b></li>
          </ul>
        </div>
      </div>
    </section>

    <section class="safety-panel">
      <ModuleTitle title="近期动火作业隐患" />
      <div class="summary-grid summary-grid--three">
        <div><span>近7天作业隐患</span><b class="number number--amber">7<small>起</small></b></div>
        <div><span>未完成</span><b class="number number--green">0<small>件</small></b></div>
        <div><span>关闭中</span><b class="number number--cyan">0<small>件</small></b></div>
      </div>
      <div class="summary-grid summary-grid--three work-total">
        <div><span>隐患总数</span><b class="number number--amber">471<small>件</small></b></div>
        <div><span>未完成</span><b class="number number--green">0<small>件</small></b></div>
        <div><span>已结案</span><b class="number number--cyan">471<small>件</small></b></div>
      </div>
      <ProgressBar :value="100" label="处置进度" tail="471 / 471 · 全部结案" />
      <div class="work-footer">
        <div class="efficiency-card">
          <span>处置时效</span><b>42<small>分钟</small></b><em>平均处置时长</em>
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
</template>

<script setup lang="ts">
import { defineComponent, h } from 'vue'
import placeholderImg from '../img/zhanwei.png'

const ModuleTitle = defineComponent({
  props: { title: { type: String, required: true } },
  setup(props) {
    return () => h('div', { class: 'module-title' }, [h('i'), h('strong', props.title)])
  }
})

const ProgressBar = defineComponent({
  props: {
    value: { type: Number, required: true },
    label: { type: String, required: true },
    tail: { type: String, required: true }
  },
  setup(props) {
    return () => h('div', { class: 'progress-block' }, [
      h('div', { class: 'progress-track' }, h('i', { style: { width: `${props.value}%` } })),
      h('div', { class: 'progress-meta' }, [h('span', props.label), h('span', props.tail)])
    ])
  }
})

const colorCodes = [
  { label: '绿码', value: 242, tone: 'green' },
  { label: '蓝码', value: 242, tone: 'blue' },
  { label: '黄码', value: 242, tone: 'yellow' },
  { label: '红码', value: 242, tone: 'red' }
]

const houseSections = [
  {
    name: '网约房',
    stats: [
      { label: '纳管中', value: 17, tone: 'cyan' },
      { label: '已纳管', value: 17, tone: 'green' },
      { label: '未成年', value: 17, tone: 'cyan' },
      { label: '重点关注', value: 17, tone: 'amber' }
    ],
    risks: [
      { label: '风险感知', current: 11, total: 11 },
      { label: '房屋风险', current: 11, total: 11 },
      { label: '人员风险', current: 11, total: 11 }
    ],
    photos: ['渝中区解放碑街道外滩...', '渝中区菜园坝街道光明...', '渝中区朝天门街道来福...']
  },
  {
    name: '群租空间',
    stats: [
      { label: '纳管中', value: 17, tone: 'cyan' },
      { label: '已纳管', value: 17, tone: 'green' },
      { label: '未成年', value: 17, tone: 'cyan' },
      { label: '重点关注', value: 17, tone: 'amber' }
    ],
    risks: [
      { label: '风险感知', current: 11, total: 11 },
      { label: '房屋风险', current: 11, total: 11 },
      { label: '人员风险', current: 11, total: 11 }
    ],
    photos: ['渝中区解放碑街道外滩...', '渝中区菜园坝街道光明...', '渝中区朝天门街道来福...']
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
  { name: '设备区域安全隐患', level: '多发' },
  { name: '疏散通道堆放杂物', level: '多发' }
]

const eventSources = [
  { name: '群众上报', value: 362 },
  { name: '网格员巡查', value: 58 },
  { name: '物联感知', value: 31 },
  { name: '部门移送', value: 20 }
]
</script>

<script lang="ts">
export default { name: 'BZHouseHotWorkTriple' }
</script>

<style lang="scss" scoped>
@font-face {
  font-family: 'HotWorkDIN';
  src: url('../font/D-DIN-Bold.otf') format('opentype');
  font-display: swap;
}

@font-face {
  font-family: 'HotWorkTitle';
  src: url('../font/Alibaba_PuHuiTi_2.0_75_SemiBold_75_SemiBold.ttf') format('truetype');
  font-display: swap;
}

.hot-work-screen {
  --cyan: #31dcff;
  --green: #20edbd;
  --amber: #ffbd2f;
  box-sizing: border-box;
  width: 100%;
  height: 100%;
  min-height: 0;
  padding: 6px 10px 14px;
  overflow-x: hidden;
  overflow-y: auto;
  color: #d9f3ff;
  background:
    linear-gradient(90deg, rgba(27, 143, 255, 0.18) 0, transparent 16px, transparent calc(100% - 16px), rgba(27, 143, 255, 0.18) 100%),
    radial-gradient(circle at 50% 0, rgba(15, 106, 196, 0.2), transparent 34%),
    linear-gradient(180deg, #02142f, #031b3b 42%, #021329);
  border: 1px solid rgba(44, 153, 255, 0.58);
  box-shadow: inset 0 0 30px rgba(0, 86, 180, 0.2);
  font-family: 'HotWorkTitle', 'Microsoft YaHei', sans-serif;
  scrollbar-width: thin;
  scrollbar-color: rgba(48, 190, 255, 0.72) rgba(4, 33, 70, 0.74);
}

.hot-work-screen::-webkit-scrollbar { width: 4px; }
.hot-work-screen::-webkit-scrollbar-track { background: rgba(4, 33, 70, 0.74); }
.hot-work-screen::-webkit-scrollbar-thumb { background: linear-gradient(#43ddff, #116cc7); border-radius: 4px; }

.screen-ornament {
  display: grid;
  grid-template-columns: 1fr 72px 1fr;
  align-items: center;
  gap: 8px;
  height: 18px;
  opacity: 0.88;
}

.screen-ornament span { height: 2px; background: linear-gradient(90deg, transparent, #1189f5); box-shadow: 0 0 5px #087ce4; }
.screen-ornament span:last-child { transform: scaleX(-1); }
.screen-ornament i { height: 5px; border-top: 1px solid #138cea; border-bottom: 1px solid rgba(25, 146, 241, 0.35); transform: skewX(-35deg); }

.screen-user {
  height: 23px;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 7px;
  padding-right: 14px;
  color: #b9dfff;
  font-size: 12px;
}

.screen-user__person { color: #ecf9ff; text-shadow: 0 0 7px #5bc7ff; }
.screen-user__status { width: 13px; height: 13px; border-radius: 50%; background: radial-gradient(circle at 36% 34%, #8df1ff 0 12%, #167fd7 44%, #073d83 70%); box-shadow: 0 0 8px #168df2; }

.hero-title {
  display: grid;
  grid-template-columns: 31px 1fr 31px;
  align-items: center;
  height: 58px;
  margin-bottom: 3px;
}

.hero-title__content { text-align: center; background: linear-gradient(90deg, transparent, rgba(4, 85, 157, 0.88) 18%, rgba(6, 96, 177, 0.88) 82%, transparent); }
.hero-title__content strong { display: block; height: 29px; font-size: 20px; line-height: 29px; color: #e8f8ff; text-shadow: 0 0 12px #37c7ff; letter-spacing: 2px; }
.hero-title__content span { display: block; height: 25px; margin: 0 8%; font-size: 15px; line-height: 25px; background: url('../img/bg_title.png') center / 100% 100% no-repeat; color: #fff; }

.hero-arrow {
  width: 27px;
  height: 38px;
  padding: 0;
  border: 0;
  border-left: 3px solid #12bdff;
  background: linear-gradient(90deg, rgba(13, 106, 182, 0.55), transparent);
  color: #ffbd38;
  font-size: 28px;
  line-height: 34px;
  cursor: pointer;
  text-shadow: 0 0 8px #ff9e20;
}
.hero-arrow--right { transform: scaleX(-1); }

.house-card, .safety-panel { padding: 0 8px 8px; background: rgba(3, 37, 78, 0.36); }
.house-card + .house-card { margin-top: 4px; }

.module-title {
  position: relative;
  display: flex;
  align-items: center;
  height: 29px;
  margin: 0 -2px 4px;
  padding-left: 32px;
  overflow: hidden;
  background: linear-gradient(90deg, rgba(5, 105, 183, 0.88), rgba(4, 75, 139, 0.54) 76%, transparent);
  color: #f0fbff;
  font-size: 18px;
  font-style: italic;
  letter-spacing: 2px;
  text-shadow: 0 0 8px #32baff;
}

.module-title i { position: absolute; left: 4px; width: 24px; height: 24px; border-radius: 50%; background: radial-gradient(circle at 30% 40%, #74eaff 0 8%, #0889cf 35%, #063f89 65%, transparent 70%); box-shadow: 0 0 8px #12aaff; }
.module-title i::after { content: ''; position: absolute; inset: 5px; border-radius: 50%; border: 1px solid rgba(154, 240, 255, 0.74); }

.dual-label { display: grid; grid-template-columns: 1fr 1fr; gap: 8px; height: 20px; color: #d7f2ff; font-size: 14px; line-height: 20px; }
.minor-label { height: 18px; font-size: 13px; line-height: 18px; color: #cce9ff; }

.stat-grid { display: grid; gap: 5px; }
.stat-grid--four { grid-template-columns: repeat(4, minmax(0, 1fr)); }
.stat-chip, .risk-chip {
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-width: 0;
  height: 24px;
  padding: 0 5px;
  background: linear-gradient(90deg, rgba(3, 74, 142, 0.8), rgba(6, 44, 91, 0.65));
  border-left: 2px solid #25c8ff;
  font-size: 13px;
}
.stat-chip span, .risk-chip span { white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.stat-chip b, .risk-chip b { font-family: 'HotWorkDIN'; color: var(--cyan); font-size: 15px; text-shadow: 0 0 7px currentColor; }
.stat-chip--green { border-color: var(--green); }
.stat-chip--green b { color: var(--green); }
.stat-chip--amber { border-color: var(--amber); }
.stat-chip--amber b { color: var(--amber); }

.risk-grid { display: grid; grid-template-columns: repeat(3, minmax(0, 1fr)); gap: 5px; }
.risk-chip { border: 1px solid rgba(22, 132, 224, 0.75); background: repeating-linear-gradient(112deg, rgba(9, 83, 150, 0.52) 0 2px, rgba(4, 54, 112, 0.5) 2px 5px); }
.risk-chip b { color: #ffd038; }
.risk-chip b i { color: #4df1ff; font: inherit; }

.code-row { display: grid; grid-template-columns: 103px repeat(2, 1fr); gap: 3px; margin-top: 4px; padding: 3px; border: 1px solid rgba(30, 126, 221, 0.8); background: rgba(3, 54, 110, 0.72); }
.code-row__title { grid-row: span 2; display: flex; align-items: center; justify-content: center; color: #e4f5ff; font-size: 14px; }
.code-chip { display: flex; justify-content: space-between; min-width: 0; height: 20px; padding: 0 5px; line-height: 20px; font-size: 12px; background: linear-gradient(90deg, rgba(11, 87, 152, 0.7), rgba(5, 48, 99, 0.4)); border-left: 2px solid currentColor; }
.code-chip b { font-family: 'HotWorkDIN'; font-size: 12px; }
.code-chip i { color: #72dff5; font: inherit; }
.code-chip--green { color: #30ef9d; }
.code-chip--blue { color: #36b6ff; }
.code-chip--yellow { color: #ffd22c; }
.code-chip--red { color: #ff6173; }

.photo-grid { display: grid; grid-template-columns: repeat(3, minmax(0, 1fr)); gap: 5px; margin-top: 4px; }
.photo-card { min-width: 0; border: 1px solid rgba(28, 125, 215, 0.8); background: #052853; overflow: hidden; }
.photo-card img { display: block; width: 100%; height: 45px; object-fit: cover; filter: saturate(0.82) brightness(0.88); }
.photo-card p { height: 17px; margin: 0; padding: 0 4px; overflow: hidden; color: #bfe9ff; font-size: 9px; line-height: 17px; white-space: nowrap; text-overflow: ellipsis; }

.main-divider { position: relative; display: flex; align-items: center; justify-content: center; height: 50px; margin: 0 -10px; overflow: hidden; background: linear-gradient(180deg, rgba(1, 12, 33, 0.86), rgba(3, 44, 85, 0.68), rgba(1, 15, 36, 0.86)); border-top: 1px solid rgba(16, 106, 196, 0.75); border-bottom: 1px solid rgba(16, 106, 196, 0.75); }
.main-divider::before, .main-divider::after { content: ''; position: absolute; top: 12px; width: 42%; height: 24px; border-top: 2px solid #0a75ce; border-radius: 0 60% 0 0; box-shadow: 0 -5px 14px rgba(18, 124, 216, 0.45); }
.main-divider::before { left: 0; transform: skewX(25deg); }
.main-divider::after { right: 0; transform: scaleX(-1) skewX(25deg); }
.main-divider span { position: relative; z-index: 1; min-width: 170px; text-align: center; color: #e7f8ff; font-size: 20px; line-height: 42px; letter-spacing: 2px; text-shadow: 0 0 10px #34c5ff; }

.safety-panel { padding-top: 5px; border: 1px solid rgba(18, 88, 166, 0.52); border-top: 0; }
.safety-panel + .safety-panel { margin-top: 6px; }
.summary-grid { display: grid; gap: 3px; margin: 5px 4px; }
.summary-grid--two { grid-template-columns: 1fr 1fr; }
.summary-grid--three { grid-template-columns: repeat(3, 1fr); }
.summary-grid > div { display: flex; align-items: flex-end; justify-content: space-between; min-width: 0; min-height: 31px; padding: 3px 9px; border-left: 2px solid #1fc5fa; background: linear-gradient(90deg, rgba(5, 75, 141, 0.62), rgba(3, 42, 89, 0.26)); box-sizing: border-box; }
.summary-grid span { min-width: 0; overflow: hidden; color: #b9def6; font-size: 12px; white-space: nowrap; text-overflow: ellipsis; }
.number { font-family: 'HotWorkDIN'; font-size: 22px; line-height: 1; text-shadow: 0 0 7px currentColor; }
.number small { margin-left: 2px; font-family: 'HotWorkTitle'; font-size: 9px; font-weight: 400; }
.number--amber { color: var(--amber); }
.number--green { color: var(--green); }
.number--cyan { color: var(--cyan); }

.progress-block { margin: 4px; }
.progress-track { height: 7px; overflow: hidden; border-radius: 5px; background: rgba(3, 30, 64, 0.92); box-shadow: inset 0 0 4px rgba(0, 0, 0, 0.7); }
.progress-track i { display: block; height: 100%; border-radius: inherit; background: linear-gradient(90deg, #16d79e, #35f4c3); box-shadow: 0 0 8px #21e8bd; }
.progress-meta { display: flex; justify-content: space-between; margin-top: 3px; color: #557ea7; font-size: 9px; }

.inspection-detail { display: grid; grid-template-columns: 1fr 1fr; gap: 7px; margin: 7px 4px 2px; }
.bar-card, .event-card, .efficiency-card, .source-card { min-width: 0; padding: 7px; border: 1px solid rgba(25, 98, 175, 0.56); background: rgba(2, 28, 62, 0.68); }
.bar-card__head { display: grid; grid-template-columns: auto 1fr; align-items: end; column-gap: 5px; }
.bar-card__head > span { color: #658fb6; font-size: 10px; }
.bar-card__head time { grid-column: 1 / -1; margin-top: 4px; padding-top: 4px; border-top: 1px solid rgba(24, 100, 178, 0.42); color: #7ba7c9; font-size: 9px; text-align: center; }
.bars { display: flex; align-items: flex-end; justify-content: space-around; height: 76px; margin-top: 5px; }
.bar-item { display: grid; grid-template-rows: 13px 48px 13px; justify-items: center; align-items: end; width: 19%; color: #6995bd; font-size: 8px; }
.bar-item b { color: #8bc8fb; font: 9px 'HotWorkDIN'; }
.bar-item i { width: 17px; max-height: 45px; min-height: 8px; background: linear-gradient(180deg, #43caff, #1168d0); box-shadow: 0 0 5px rgba(36, 171, 255, 0.64); }
.event-card h4 { margin: 0 0 6px; color: #79a8cc; font-size: 10px; font-weight: 400; }
.event-card ul, .source-card ul { margin: 0; padding: 0; list-style: none; }
.event-card li, .source-card li { display: flex; justify-content: space-between; gap: 6px; min-width: 0; height: 18px; color: #92bbd9; font-size: 9px; line-height: 18px; }
.event-card li span, .source-card li span { overflow: hidden; white-space: nowrap; text-overflow: ellipsis; }
.event-card li b { color: #ffc532; white-space: nowrap; }

.work-total { margin-top: 9px; }
.work-footer { display: grid; grid-template-columns: 1fr 1fr; gap: 7px; margin: 7px 4px 2px; }
.efficiency-card { display: grid; grid-template-columns: 1fr auto; align-items: center; }
.efficiency-card span, .source-card__head span { color: #78a6c9; font-size: 10px; }
.efficiency-card b { grid-row: span 2; font: 27px 'HotWorkDIN'; color: var(--green); text-shadow: 0 0 8px rgba(32, 237, 189, 0.58); }
.efficiency-card b small { margin-left: 3px; color: #88b4d4; font: 9px 'HotWorkTitle'; }
.efficiency-card em { color: #648cab; font-size: 9px; font-style: normal; }
.source-card__head { display: flex; justify-content: space-between; padding-bottom: 3px; margin-bottom: 2px; border-bottom: 1px solid rgba(23, 100, 178, 0.46); }
.source-card__head b { color: #7fb2d5; font-size: 9px; }
.source-card li b { color: #d8edfa; white-space: nowrap; }

@media (max-width: 430px) {
  .hot-work-screen { padding-right: 7px; padding-left: 7px; }
  .stat-chip, .risk-chip { padding: 0 3px; font-size: 11px; }
  .code-row { grid-template-columns: 84px repeat(2, 1fr); }
  .code-chip { font-size: 10px; }
}
</style>
