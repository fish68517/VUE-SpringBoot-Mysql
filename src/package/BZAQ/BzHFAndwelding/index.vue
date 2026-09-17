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
    width: Number.isFinite(width) && width > 0 ? `${width}px` : 'var(--hw-default-width)',
    height: Number.isFinite(height) && height > 0 ? `${height}px` : 'var(--hw-default-height)'
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

  /* ================= 产品调样区：只改冒号右侧的值 =================
   * 例：字号 20px 改为 22px；颜色 #f2fbff 改为 #ffffff。
   * 尺寸保留 px，颜色保留 #，行尾保留分号；一次改一项，保存后查看页面。
   * x/y 是微调，不会自动腾出空间。避免重叠请同时调间距、行高或卡片高度。
   * 本区对独立控件和三连屏嵌入控件都生效；下方选择器是绑定代码，无需修改。
   * 图片背景的图案和颜色保存在图片里，文字颜色参数不会改变图片本身。
   */
  /* 正文颜色：未整改、已整改、进度名称、隐患名称、事件来源等 */
  --hw-text-color: #d9f3ff;
  /* 正文大小；调大后如拥挤，请同时增加卡片高度和列表行高 */
  --hw-text-size: 13px;
  /* 一级标题「动火动焊」字号 */
  --hw-title-size: 21px;
  /* 一级标题文字颜色 */
  --hw-title-color: #eefbff;
  /* 一级标题字间距 */
  --hw-title-spacing: 2px;
  /* 一级标题背景条高度 */
  --hw-title-height: 42px;
  /* 二级标题「网格巡查隐患」「近期动火作业隐患」字号 */
  --hw-section-title-size: 20px;
  /* 二级标题文字颜色 */
  --hw-section-title-color: #f2fbff;
  /* 二级标题距左边的距离，需给圆形图标留空间 */
  --hw-section-title-indent: 39px;
  /* 二级标题倾斜：italic 为斜体，normal 为正体 */
  --hw-section-title-style: italic;
  /* 二级标题字间距 */
  --hw-section-title-spacing: 1px;
  /* 二级标题圆形图标距离左边的位置 */
  --hw-icon-left: 5px;
  /* 二级标题圆形图标直径 */
  --hw-icon-size: 27px;
  /* 二级标题背景条高度 */
  --hw-section-title-height: 31px;
  /* 二级标题下方间距 */
  --hw-section-title-bottom: 7px;
  /* 控件左右内边距 */
  --hw-outer-padding: 10px;
  /* 控件底部内边距 */
  --hw-outer-bottom: 10px;
  /* 一级标题与第一个模块的距离 */
  --hw-title-bottom: 7px;
  /* 两个大模块之间的垂直距离 */
  --hw-panel-gap: 7px;
  /* 大模块内边距：上、左右、下，三个数值依次对应 */
  --hw-panel-padding: 5px 8px 7px;
  /* 未整改、已整改等统计数字字号 */
  --hw-number-size: 20px;
  /* 统计数字后「件」的颜色 */
  --hw-unit-color: #84a8c5;
  /* 统计数字后「件」的字号 */
  --hw-unit-size: 8px;
  /* 进度名称占用宽度 */
  --hw-progress-label-width: 72px;
  /* 整改进度右侧数字占用宽度 */
  --hw-progress-result-width: 96px;
  /* 进度条粗细 */
  --hw-progress-height: 8px;
  /* 进度条未完成部分颜色 */
  --hw-progress-bg: rgba(64, 98, 135, 0.48);
  /* 进度条左端颜色 */
  --hw-progress-start: #149cf1;
  /* 进度条右端颜色 */
  --hw-progress-end: #2ce7e9;
  /* 进度条右边结果文字颜色 */
  --hw-progress-text-color: #43edff;
  /* 进度条右边结果字号 */
  --hw-progress-text-size: 15px;
  /* 左右卡片宽度比例：1fr 1fr 为平分，1.2fr 1fr 为左侧更宽 */
  --hw-detail-columns: 1fr 1fr;
  /* 左右卡片之间的间距 */
  --hw-detail-gap: 7px;
  /* 新增隐患/高频隐患点卡片高度 */
  --hw-inspection-height: 126px;
  /* 上排小卡片内边距：上下、左右 */
  --hw-inspection-padding: 6px 8px;
  /* 新增隐患右上角日期范围颜色 */
  --hw-date-color: #7698b8;
  /* 日期范围字号 */
  --hw-date-size: 8px;
  /* 新增隐患 123 的字号 */
  --hw-new-number-size: 24px;
  /* 日均新增说明文字颜色 */
  --hw-average-color: #6489aa;
  /* 日均新增说明字号 */
  --hw-average-size: 8px;
  /* 日均新增中红色增长数字颜色 */
  --hw-increase-color: #ff6150;
  /* 两张柱状图整体高度，包含数值和日期；调大时同步增加对应卡片高度 */
  --hw-chart-height: 64px;
  /* 柱状图数值行预留高度 */
  --hw-chart-value-row: 12px;
  /* 柱状图日期行预留高度 */
  --hw-chart-date-row: 12px;
  /* 柱状图数值、柱体、日期之间的垂直间距 */
  --hw-chart-row-gap: 2px;
  /* 柱状图下方日期颜色 */
  --hw-chart-date-color: #6995bd;
  /* 柱状图下方日期字号 */
  --hw-chart-date-size: 8px;
  /* 柱状图上方数值颜色 */
  --hw-chart-value-color: #8bc8fb;
  /* 柱状图上方数值字号 */
  --hw-chart-value-size: 9px;
  /* 柱体宽度 */
  --hw-bar-width: 21px;
  /* 柱体最大高度，需小于图表高度减去数值行、日期行及间距 */
  --hw-bar-max-height: 34px;
  /* 柱体顶部颜色 */
  --hw-bar-top: #35e1ef;
  /* 柱体底部颜色 */
  --hw-bar-bottom: #1175cf;
  /* 高频隐患列表每行高度 */
  --hw-danger-row-height: 21px;
  /* 高频隐患右侧「高发」颜色 */
  --hw-danger-high-color: #ffc532;
  /* 高频隐患右侧「多发」颜色 */
  --hw-danger-many-color: #d8edf9;
  /* 三个图标下方说明颜色 */
  --hw-orb-label-color: #b5d5e9;
  /* 三个图标下方说明字号 */
  --hw-orb-label-size: 9px;
  /* 三个统计图标各自的宽度 */
  --hw-orb-width: 64px;
  /* 三个统计图标各自的高度 */
  --hw-orb-height: 47px;
  /* 图标中青色数字颜色 */
  --hw-orb-color: #48f0ff;
  /* 图标中的 42 数字字号 */
  --hw-orb-number-size: 18px;
  /* 图标中「起/件」单位颜色 */
  --hw-orb-unit-color: #d2edf8;
  /* 图标中单位字号 */
  --hw-orb-unit-size: 8px;
  /* 未完成图标数字的黄色 */
  --hw-orb-amber: #ffd33a;
  /* 处置进度条长度；全部销案溢出时减小此值 */
  --hw-resolved-track-width: 230px;
  /* 处置结果文字预留宽度 */
  --hw-resolved-result-width: 138px;
  /* 处置时效/事件来源卡片高度 */
  --hw-footer-height: 130px;
  /* 下排小卡片内边距：上下、左右 */
  --hw-footer-padding: 7px 8px;
  /* 处置时效 12 数字颜色 */
  --hw-efficiency-color: #45f1ff;
  /* 处置时效 12 字号 */
  --hw-efficiency-size: 24px;
  /* 分钟单位颜色 */
  --hw-efficiency-unit-color: #8eb8d2;
  /* 分钟单位字号 */
  --hw-efficiency-unit-size: 9px;
  /* 平均处置时长说明颜色 */
  --hw-efficiency-note-color: #789db9;
  /* 平均处置时长说明字号 */
  --hw-efficiency-note-size: 8px;
  /* 平均处置时长中 12分钟 颜色 */
  --hw-efficiency-note-value-color: #a8c9dc;
  /* 事件来源右上角合计颜色 */
  --hw-source-total-color: #8eb4cc;
  /* 合计文字字号 */
  --hw-source-total-size: 9px;
  /* 事件来源每行高度 */
  --hw-source-row-height: 16px;
  /* 事件来源右侧数量颜色 */
  --hw-source-number-color: #65d9ff;
  /* 事件来源右侧数量字号 */
  --hw-source-number-size: 12px;
  /* 独立控件默认宽度；调试页面的组件宽度有值时，以页面设置为准 */
  --hw-default-width: 500px;
  /* 独立控件默认高度；调试页面的组件高度有值时，以页面设置为准 */
  --hw-default-height: 635px;
  /* 嵌入三连屏时的完整内容高度；外层滚动窗口的 317.5px 高度仍由房屋组件控制 */
  --hw-embedded-height: 635px;
  /* 一级标题左右微调：正数向右，负数向左，不挤动旁边元素 */
  --hw-title-x: 0px;
  /* 一级标题上下微调：正数向下，负数向上，不改变占位高度 */
  --hw-title-y: 0px;
  /* 二级标题文字左右微调：正数向右，负数向左，不挤动旁边元素 */
  --hw-section-title-x: 0px;
  /* 二级标题文字上下微调：正数向下，负数向上，不改变占位高度 */
  --hw-section-title-y: 0px;
  /* 正文标签左右微调：正数向右，负数向左，不挤动旁边元素 */
  --hw-text-x: 0px;
  /* 正文标签上下微调：正数向下，负数向上，不改变占位高度 */
  --hw-text-y: 0px;
  /* 统计数字左右微调：正数向右，负数向左，不挤动旁边元素 */
  --hw-number-x: 0px;
  /* 统计数字上下微调：正数向下，负数向上，不改变占位高度 */
  --hw-number-y: 0px;
  /* 统计图标左右微调：正数向右，负数向左，不挤动旁边元素 */
  --hw-orb-x: 0px;
  /* 统计图标上下微调：正数向下，负数向上，不改变占位高度 */
  --hw-orb-y: 0px;
  /* 柱状图左右微调：正数向右，负数向左，不挤动旁边元素 */
  --hw-chart-x: 0px;
  /* 柱状图上下微调：正数向下，负数向上，不改变占位高度 */
  --hw-chart-y: 0px;
  /* 三级标题「高频隐患点」字号（其余三级标题沿用正文字号） */
  --hw-small-title-size: 10px;
  /* 小屏进度结果字号，浏览器宽度不超过430px时生效 */
  --hw-compact-progress-size: 12px;
  /* 小屏列表字号，浏览器宽度不超过430px时生效 */
  --hw-compact-list-size: 8px;
  /* 控件允许的最小宽度 */
  --hw-min-width: 340px;
  /* 独立控件允许的最小高度 */
  --hw-min-height: 420px;
  /* 整体背景顶部颜色；rgba最后一项是透明度，0透明、1不透明 */
  --hw-background-top: rgba(1, 13, 34, 0.99);
  /* 整体背景底部颜色 */
  --hw-background-bottom: rgba(3, 28, 60, 0.99);
  /* 大模块背景顶部颜色 */
  --hw-panel-top: rgba(4, 43, 88, 0.7);
  /* 大模块背景底部颜色 */
  --hw-panel-bottom: rgba(2, 28, 61, 0.68);
  /* 四张小卡片背景颜色 */
  --hw-card-background: rgba(2, 28, 62, 0.72);
  /* 未整改/已整改统计格最小高度 */
  --hw-summary-height: 31px;
  /* 未整改/已整改统计格之间距离 */
  --hw-summary-gap: 7px;
  /* 进度行最小高度 */
  --hw-progress-row-height: 26px;
  /* 进度行下方间距 */
  --hw-progress-bottom: 6px;
  /* 三个统计图标的横向间距 */
  --hw-orb-gap: 8px;
  /* 三个统计图标与处置进度之间的间距 */
  --hw-orb-bottom: 7px;
  /* ================= 产品调样区结束 ================= */
  /* 青色统计数字 */
  --hot-cyan: #31dcff;
  /* 绿色统计数字 */
  --hot-green: #20edbd;
  /* 黄色统计数字 */
  --hot-amber: #ffbd2f;
  box-sizing: border-box;
  min-width: var(--hw-min-width);
  min-height: var(--hw-min-height);
  padding: 0 var(--hw-outer-padding) var(--hw-outer-bottom);
  overflow: hidden;
  color: var(--hw-text-color);
  background:
    linear-gradient(90deg, rgba(18, 117, 217, 0.15), transparent 24px, transparent calc(100% - 24px), rgba(18, 117, 217, 0.15)),
    linear-gradient(180deg, var(--hw-background-top), var(--hw-background-bottom));
  border: 1px solid rgba(24, 113, 202, 0.72);
  box-shadow: 0 -9px 22px rgba(0, 18, 47, 0.74), inset 0 0 24px rgba(9, 92, 176, 0.16);
  font-family: 'Triple3Text', 'Microsoft YaHei', sans-serif;
  line-height: 1.5;
}

.bz-hf-and-welding--embedded {
  flex: 0 0 var(--hw-embedded-height);
  width: 100%;
  height: var(--hw-embedded-height);
  min-height: 0;
}

.hot-work-title {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  height: var(--hw-title-height);
  margin: 0 calc(0px - var(--hw-outer-padding)) var(--hw-title-bottom);
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
.hot-work-title span { position: relative; z-index: 1; color: var(--hw-title-color); font-size: var(--hw-title-size); font-weight: 700; letter-spacing: var(--hw-title-spacing); text-shadow: 0 0 11px #32c4ff; }

.hot-work-grid { display: grid; grid-template-columns: 1fr; gap: var(--hw-panel-gap); }

.safety-panel {
  box-sizing: border-box;
  width: 100%;
  padding: var(--hw-panel-padding);
  border: 1px solid rgba(21, 104, 190, 0.68);
  background: linear-gradient(180deg, var(--hw-panel-top), var(--hw-panel-bottom));
  box-shadow: inset 0 0 16px rgba(16, 112, 206, 0.12);
  cursor: pointer;
}

.safety-panel:hover { filter: brightness(1.06); }

.work-card__title {
  position: relative;
  display: flex;
  align-items: center;
  width: 100%;
  height: var(--hw-section-title-height);
  margin: 0 0 var(--hw-section-title-bottom);
  padding-left: var(--hw-section-title-indent);
  box-sizing: border-box;
  overflow: hidden;
  background: url('../BZHouse/img/bg_second_title.png') center / 100% 100% no-repeat;
  color: var(--hw-section-title-color);
  font-size: var(--hw-section-title-size);
  font-weight: 700;
  font-style: var(--hw-section-title-style);
  letter-spacing: var(--hw-section-title-spacing);
  text-shadow: 0 0 9px #33baff;
}

.work-card__title i {
  position: absolute;
  left: var(--hw-icon-left);
  width: var(--hw-icon-size);
  height: var(--hw-icon-size);
  border-radius: 50%;
  background: radial-gradient(circle at 30% 38%, #79ecff 0 8%, #098dce 36%, #063c82 66%, transparent 70%);
  box-shadow: 0 0 9px #16a9ff;
}

.summary-grid { display: grid; gap: var(--hw-summary-gap); margin: 0 0 5px; }
.summary-grid--two { grid-template-columns: repeat(2, minmax(0, 1fr)); }
.summary-grid--three { grid-template-columns: repeat(3, minmax(0, 1fr)); }
.summary-grid > div {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  min-width: 0;
  min-height: var(--hw-summary-height);
  padding: 3px 8px;
  box-sizing: border-box;
  border: 1px solid rgba(25, 102, 185, 0.52);
  border-left: 2px solid #21caff;
  background: repeating-linear-gradient(112deg, rgba(7, 79, 145, 0.62) 0 2px, rgba(4, 51, 105, 0.56) 2px 5px);
}

.number { color: var(--hot-cyan); font: var(--hw-number-size)/1 'Triple3DIN'; text-shadow: 0 0 7px currentColor; }
.number small { margin-left: 2px; color: var(--hw-unit-color); font: var(--hw-unit-size) 'Triple3Text'; text-shadow: none; }
.number--amber { color: var(--hot-amber); }
.number--green { color: var(--hot-green); }
.number--cyan { color: var(--hot-cyan); }

.progress-line {
  display: grid;
  grid-template-columns: var(--hw-progress-label-width) minmax(80px, 1fr) var(--hw-progress-result-width);
  align-items: center;
  gap: 8px;
  min-height: var(--hw-progress-row-height);
  margin: 0 1px var(--hw-progress-bottom);
}

.progress-track { height: var(--hw-progress-height); overflow: hidden; background: var(--hw-progress-bg); }
.progress-track i { display: block; height: 100%; background: linear-gradient(90deg, var(--hw-progress-start), var(--hw-progress-end)); }
.progress-track i::after { content: ''; float: right; width: 2px; height: 12px; margin-top: -2px; background: #e8ffff; box-shadow: 0 0 5px #72ffff; }
.progress-line > b { color: var(--hw-progress-text-color); font: var(--hw-progress-text-size) 'Triple3DIN'; white-space: nowrap; text-align: right; text-shadow: 0 0 6px rgba(49, 220, 255, 0.55); }

.inspection-detail,
.work-footer { display: grid; grid-template-columns: var(--hw-detail-columns); gap: var(--hw-detail-gap); }

.bar-card,
.event-card,
.efficiency-card,
.source-card { box-sizing: border-box; min-width: 0; border: 1px solid rgba(25, 98, 175, 0.64); background: var(--hw-card-background); }
.bar-card,
.event-card { height: var(--hw-inspection-height); padding: var(--hw-inspection-padding); }
.bar-card__head { display: flex; align-items: center; justify-content: space-between; height: 17px; color: #658fb6; font-size: 8px; }
.bar-card__head span { margin-right: auto; }
.bar-card__head time { color: var(--hw-date-color); font-size: var(--hw-date-size); }
.bar-card__summary { display: flex; align-items: flex-end; justify-content: space-between; height: 27px; }
.bar-card__summary .number { font-size: var(--hw-new-number-size); }
.bar-card__summary span { color: var(--hw-average-color); font-size: var(--hw-average-size); }
.bar-card__summary strong { color: var(--hw-increase-color); font-family: 'Triple3DIN'; }

.bars {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  align-items: end;
  column-gap: 8px;
  height: var(--hw-chart-height);
  padding: 4px 5px 1px;
  box-sizing: border-box;
  border-top: 1px dotted rgba(44, 156, 224, 0.42);
}

.bar-item {
  display: grid;
  grid-template-rows: var(--hw-chart-value-row) minmax(32px, 1fr) var(--hw-chart-date-row);
  justify-items: center;
  align-items: end;
  row-gap: var(--hw-chart-row-gap);
  width: 100%;
  height: 100%;
  color: var(--hw-chart-date-color);
  font-size: var(--hw-chart-date-size);
}

.bar-item b { color: var(--hw-chart-value-color); font: var(--hw-chart-value-size) 'Triple3DIN'; }
.bar-item i { width: var(--hw-bar-width); max-height: var(--hw-bar-max-height); min-height: 5px; background: linear-gradient(180deg, var(--hw-bar-top), var(--hw-bar-bottom)); box-shadow: 0 0 5px rgba(36, 171, 255, 0.72); }

.event-card h4 { margin: 0 0 6px; padding-left: 0; font-size: var(--hw-small-title-size); font-weight: 400; }
.event-card ul,
.source-card ul { margin: 0; padding: 0; list-style: none; }
.event-card li,
.source-card li { display: flex; justify-content: space-between; gap: 6px; min-width: 0; }
.event-card li { height: var(--hw-danger-row-height); padding: 0 5px; background: linear-gradient(90deg, rgba(5, 80, 146, 0.6), rgba(3, 43, 91, 0.25)); font-size: 10px; line-height: var(--hw-danger-row-height); }
.event-card li + li { margin-top: 2px; }
.event-card li span,
.source-card li span { overflow: hidden; white-space: nowrap; text-overflow: ellipsis; }
.event-card li b { flex: 0 0 auto; color: var(--hw-danger-high-color); }
.event-card li b.event-level--many { color: var(--hw-danger-many-color); }

.work-orbs { display: grid; grid-template-columns: repeat(3, minmax(0, 1fr)); gap: var(--hw-orb-gap); margin: 0 4px var(--hw-orb-bottom); }
.work-orb-item { min-width: 0; text-align: center; }
.work-orb-item > span { display: block; overflow: hidden; color: var(--hw-orb-label-color); font-size: var(--hw-orb-label-size); white-space: nowrap; text-overflow: ellipsis; }
.work-orb {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  width: var(--hw-orb-width);
  height: var(--hw-orb-height);
  margin: 0 auto 2px;
  color: var(--hw-orb-color);
  background: url('./img/hot-work-overview-orb.png') center / var(--hw-orb-width) var(--hw-orb-height) no-repeat;
}
.work-orb b { font: var(--hw-orb-number-size) 'Triple3DIN'; text-shadow: 0 0 7px currentColor; }
.work-orb small { margin-left: 2px; color: var(--hw-orb-unit-color); font-size: var(--hw-orb-unit-size); }
.work-orb--amber { color: var(--hw-orb-amber); }

.progress-line--resolved {
  grid-template-columns: var(--hw-progress-label-width) var(--hw-resolved-track-width) minmax(var(--hw-resolved-result-width), 1fr);
}

.progress-line--resolved .progress-track {
  width: var(--hw-resolved-track-width);
}
.efficiency-card,
.source-card { height: var(--hw-footer-height); padding: var(--hw-footer-padding); }
.efficiency-card__head,
.source-card__head { height: 17px; }
.efficiency-card__summary { display: flex; align-items: flex-end; justify-content: space-between; height: 28px; }
.efficiency-card__summary b { color: var(--hw-efficiency-color); font: var(--hw-efficiency-size) 'Triple3DIN'; text-shadow: 0 0 8px rgba(49, 220, 255, 0.58); }
.efficiency-card__summary b small { margin-left: 2px; color: var(--hw-efficiency-unit-color); font: var(--hw-efficiency-unit-size) 'Triple3Text'; }
.efficiency-card__summary span { color: var(--hw-efficiency-note-color); font-size: var(--hw-efficiency-note-size); }
.efficiency-card__summary strong { color: var(--hw-efficiency-note-value-color); }
.bars--handling { height: var(--hw-chart-height); }
.source-card__head { display: flex; justify-content: space-between; margin: 0 0 4px; }
.source-card__head b { color: var(--hw-source-total-color); font-size: var(--hw-source-total-size); }
.source-card li { height: var(--hw-source-row-height); padding: 0 7px; background: linear-gradient(90deg, rgba(4, 81, 148, 0.64), rgba(3, 43, 91, 0.25)); font-size: 10px; line-height: var(--hw-source-row-height); }
.source-card li + li { margin-top: 2px; }
.source-card li b { flex: 0 0 auto; color: var(--hw-source-number-color); font: var(--hw-source-number-size) 'Triple3DIN'; }

.risk-label-text { color: var(--hw-text-color); font-family: 'Triple3Text', 'Microsoft YaHei', sans-serif; font-size: var(--hw-text-size); font-style: normal; font-weight: 400; text-shadow: none; }


/* 位置参数绑定：仅作视觉微调，不改变各模块的布局占位。 */
.hot-work-title span { transform: translate(var(--hw-title-x), var(--hw-title-y)); }
.work-card__title strong { transform: translate(var(--hw-section-title-x), var(--hw-section-title-y)); }
.risk-label-text { position: relative; left: var(--hw-text-x); top: var(--hw-text-y); }
.number { transform: translate(var(--hw-number-x), var(--hw-number-y)); }
.work-orb { transform: translate(var(--hw-orb-x), var(--hw-orb-y)); }
.bars { transform: translate(var(--hw-chart-x), var(--hw-chart-y)); }

/* 浏览器宽度 <= 430px 时保留原紧凑布局；如需调小屏字号，请修改这里。 */
@media (max-width: 430px) {
  .progress-line { grid-template-columns: 54px minmax(58px, 1fr) 82px; gap: 5px; }
  .progress-line--resolved { grid-template-columns: 54px minmax(50px, 1fr) 110px; }
  .progress-line--resolved .progress-track { width: 100%; }
  .progress-line > b { font-size: var(--hw-compact-progress-size); }
  .summary-grid > div { padding-right: 4px; padding-left: 4px; }
  .inspection-detail,
  .work-footer { gap: 4px; }
  .event-card li,
  .source-card li { padding-right: 3px; padding-left: 3px; font-size: var(--hw-compact-list-size); }
}
</style>
