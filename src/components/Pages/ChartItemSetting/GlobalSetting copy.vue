<template>
  <n-divider style="margin: 6px 0 9px" v-if="title"></n-divider>

  <collapse-item v-if="title" name="标题" :expanded="title.show" :isControl="true">
    <template #header>
      <n-switch v-model:value="title.show"></n-switch>
    </template>

    <div class="wrap">
      <CustomInput label="文本" labelPlacement="left" v-model:value="title.text" placeholder="请输入标题名称" />

      <!-- 可能要注释的地方 13-33 -->
      <CustomInputSelect label="字体" v-model:value="title.textStyle.fontFamily" :options="fontFamilyOption" />

      <n-form label-placement="left">
        <n-form-item label="字号">
          <CustomInputNumber v-model:value="title.textStyle.fontSize" :min="1" :prefix="false" el-width="100%" />
        </n-form-item>
      </n-form>

      <CustomInputSelect
        label="样式"
        v-model:value="title.textStyle.fontWeight"
        :options="fontWeightOption"
        :sign="true"
      />

      <CustomColorPicker label-placement="left" label="颜色" v-model:value="title.textStyle.color" />

      <CustomInputSelect label="位置" v-model:value="titlePosition" :options="titlePositionOption" />

      <CustomInputNumberWithSlider label="上下间距" v-model:value="titleMarginTopBottom" />
      <!-- <CustomInputNumberWithSlider label="左右间距" v-model:value="gridOpion.topBottomPad" /> -->
    </div>
  </collapse-item>

  <slot name="individual-setting"></slot>
  <n-divider style="margin: 3px 0 9px" v-if="grid"></n-divider>
  <div class="wrap">
    <div class="subtitle">边距</div>
    <n-form label-placement="left">
      <n-form-item label="上边距">
        <CustomInputNumber v-model:value="grid.top" :prefix="false" el-width="100%" />
      </n-form-item>
    </n-form>
    <n-form label-placement="left">
      <n-form-item label="下边距">
        <CustomInputNumber v-model:value="grid.bottom" :prefix="false" el-width="100%" />
      </n-form-item>
    </n-form>
    <n-form label-placement="left">
      <n-form-item label="左边距">
        <CustomInputNumber v-model:value="grid.left" :prefix="false" el-width="100%" />
      </n-form-item>
    </n-form>
    <n-form label-placement="left">
      <n-form-item label="右边距">
        <CustomInputNumber v-model:value="grid.right" :prefix="false" el-width="100%" />
      </n-form-item>
    </n-form>
  </div>

  <n-divider style="margin: 3px 0 9px" v-if="grid"></n-divider>

  <collapse-item v-if="grid" name="数据系列">
    <div class="wrap">
      <CustomSwitch
        label="使用全局配色"
        v-model:value="newAttr.themeColor.useGlobalColor"
        label-color="#aaa"
        el-margin-bottom="10px"
      />
      <n-form label-placement="left" v-if="!newAttr.themeColor.useGlobalColor">
        <n-form-item label="配色方案">
          <ColorSelect @transTheme="changeTheme" />
        </n-form-item>
      </n-form>

      <n-divider n-divider style="margin: 6px 0 16px"></n-divider>

      <!-- <div class="series_group">
        <div class="subtitle control">
          <span>系列1</span>
          <div class="btns">
            <i class="iconfont icon-xinjianshujuyuan"></i>
            <i class="iconfont icon-yishanchuxiangmu"></i>
          </div>
        </div>
        <CustomInput
          label="系列名"
          labelPlacement="left"
          v-model:value="gridOpion.seriesName"
          placeholder="请输入系列名"
        />
        <CustomColorPicker label-placement="left" label="开始颜色" v-model:value="gridOpion.startColor" />
        <CustomColorPicker label-placement="left" label="结束颜色" v-model:value="gridOpion.endColor" />
        <CustomSwitch label="启用渐变色" v-model:value="gridOpion.emptyHide" el-margin-bottom="10px" /> -->

      <!-- </div> -->
    </div>
  </collapse-item>

  <n-divider style="margin: 9px 0" v-if="xAxis"></n-divider>

  <collapse-item
    v-if="xAxis"
    name="X轴设置"
    :expanded="xAxis.show"
    :isControl="true"
    divider-margin="margin: 9px 0 11px"
  >
    <template #header>
      <n-switch v-model:value="xAxis.show"></n-switch>
    </template>
    <div class="wrap">
      <CustomRadio label="x轴位置" v-model:value="xAxis.position" :options="selectxAxisPositionOption" />

      <n-divider n-divider style="margin: 1px 0 16px"></n-divider>

      <div class="subtitle">文本样式</div>
      <CustomInputSelect label="字体" v-model:value="xAxis.axisLabel.fontFamily" :options="selectTextFamilyOption" />

      <n-form label-placement="left">
        <n-form-item label="字号">
          <CustomInputNumber v-model:value="xAxis.axisLabel.fontSize" :min="1" :prefix="false" el-width="100%" />
        </n-form-item>
      </n-form>

      <!-- 可能要注释的地方 -->
      <CustomInputSelect
        label="样式"
        v-model:value="xAxis.axisLabel.fontWeight"
        :options="fontWeightOption"
        :sign="true"
      />

      <CustomColorPicker label-placement="left" label="颜色" v-model:value="xAxis.axisLabel.color" />

      <n-divider n-divider style="margin: 16px 0"></n-divider>

      <div class="subtitle">轴标签</div>
      <template v-if="xAxisType === 'category'">
        <CustomInputNumberWithSlider
          label="标签倾斜度"
          v-model:value="xAxis.axisLabel.rotate"
          :min="0"
          :max="360"
          :step="1"
        />
        <n-form label-placement="left" label-width="70">
          <n-form-item label="标签间隔">
            <CustomInputNumber v-model:value="xAxis.axisLabel.interval" :prefix="false" el-width="100%" />
          </n-form-item>
        </n-form>
        <LabelStyleRadio
          label="标签展示"
          v-model:value="newAttr.xAxisLabelType"
          v-model:number="newAttr.xAxisLabelCount"
        />
        <CustomInputNumberWithSlider label="与轴线距离" v-model:value="xAxis.axisLabel.margin" />
      </template>
      <template v-else-if="xAxisType === 'value'">
        <n-form label-placement="left">
          <n-form-item label="最小值">
            <CustomInputNumber v-model:value="xAxis.min" :min="0" :prefix="false" el-width="100%" />
          </n-form-item>
          <CustomInput v-model:value="xAxis.max" :min="0" el-width="100%" label="最大值" labelPlacement="left" />
          <n-form-item label="标签数量">
            <CustomInputNumber v-model:value="xAxis.splitNumber" :prefix="false" el-width="100%" />
          </n-form-item>
        </n-form>
      </template>

      <n-divider n-divider style="margin: 16px 0 10px"></n-divider>

      <CustomSwitch label="轴单位" v-model:value="newAttr.showXaxisName" label-color="#fff" el-margin-bottom="10px" />
      <div v-if="newAttr.showXaxisName">
        <CustomInput label="名称" labelPlacement="left" v-model:value="xAxis.name" placeholder="请输入单位" />
        <CustomColorPicker label-placement="left" label="颜色" v-model:value="xAxis.nameTextStyle.color" />
        <n-form label-placement="left">
          <n-form-item label="字号">
            <CustomInputNumber v-model:value="xAxis.nameTextStyle.fontSize" :prefix="false" :min="1" el-width="100%" />
          </n-form-item>
        </n-form>
      </div>

      <n-divider n-divider style="margin: 6px 0 10px"></n-divider>

      <CustomSwitch label="轴线" v-model:value="xAxis.axisLine.show" label-color="#fff" el-margin-bottom="10px" />
      <div v-if="xAxis.axisLine.show">
        <CustomColorPicker label-placement="left" label="颜色" v-model:value="xAxis.axisLine.lineStyle.color" />
        <n-form label-placement="left">
          <n-form-item label="粗细">
            <CustomInputNumber
              v-model:value="xAxis.axisLine.lineStyle.width"
              :prefix="false"
              :min="1"
              el-width="100%"
            />
          </n-form-item>
        </n-form>
      </div>

      <n-divider n-divider style="margin: 6px 0 10px"></n-divider>

      <CustomSwitch label="网格线" v-model:value="xAxis.splitLine.show" label-color="#fff" el-margin-bottom="10px" />
      <div v-if="xAxis.splitLine.show">
        <CustomColorPicker label-placement="left" label="颜色" v-model:value="xAxis.splitLine.lineStyle.color" />
        <CustomInputSelect
          label="类型"
          v-model:value="xAxis.splitLine.lineStyle.type"
          :options="selectSplitLineTypeOption"
        />
      </div>
    </div>

    <!-- <setting-item-box name="单位">
      <setting-item name="名称">
        <n-input v-model:value="xAxis.name" size="small"></n-input>
      </setting-item>
      <setting-item name="颜色">
        <n-color-picker size="small" v-model:value="xAxis.axisLabel.color"></n-color-picker>
      </setting-item>
      <setting-item name="大小">
        <n-input-number v-model:value="xAxis.axisLabel.fontSize" :min="1" size="small"></n-input-number>
      </setting-item>
      <setting-item name="偏移量">
        <n-input-number v-model:value="xAxis.nameGap" :min="5" size="small"></n-input-number>
      </setting-item>
    </setting-item-box>
    <setting-item-box name="标签">
      <setting-item name="展示">
        <n-space>
          <n-switch v-model:value="xAxis.axisLabel.show" size="small"></n-switch>
        </n-space>
      </setting-item>
      <setting-item name="颜色">
        <n-color-picker size="small" v-model:value="xAxis.axisLabel.color"></n-color-picker>
      </setting-item>
      <setting-item name="大小">
        <n-input-number v-model:value="xAxis.axisLabel.fontSize" :min="8" size="small"></n-input-number>
      </setting-item>
      <setting-item name="偏移量">
        <n-input-number v-model:value="xAxis.axisLabel.rotate" :min="-90" :max="90" size="small"></n-input-number>
      </setting-item>
    </setting-item-box>
    <setting-item-box name="轴线">
      <setting-item name="展示">
        <n-space>
          <n-switch v-model:value="xAxis.axisLine.show" size="small"></n-switch>
        </n-space>
      </setting-item>
      <setting-item name="颜色">
        <n-color-picker v-model:value="xAxis.axisLine.lineStyle.color" size="small"></n-color-picker>
      </setting-item>
      <setting-item name="粗细">
        <n-input-number v-model:value="xAxis.axisLine.lineStyle.width" :min="1" size="small"></n-input-number>
      </setting-item>
      <setting-item name="位置">
        <n-select v-model:value="xAxis.position" size="small" :options="axisConfig.xposition"></n-select>
      </setting-item>
      <setting-item name="对齐零">
        <n-space>
          <n-switch v-model:value="xAxis.axisLine.onZero" size="small"></n-switch>
        </n-space>
      </setting-item>
      <setting-item name="反向">
        <n-space>
          <n-switch v-model:value="xAxis.inverse" size="small"></n-switch>
        </n-space>
      </setting-item>
    </setting-item-box>
    <setting-item-box name="刻度">
      <setting-item name="展示">
        <n-space>
          <n-switch v-model:value="xAxis.axisTick.show" size="small"></n-switch>
        </n-space>
      </setting-item>
      <setting-item name="长度">
        <n-input-number v-model:value="xAxis.axisTick.length" :min="1" size="small"></n-input-number>
      </setting-item>
    </setting-item-box>
    <setting-item-box name="分割线">
      <setting-item name="展示">
        <n-space>
          <n-switch v-model:value="xAxis.splitLine.show" size="small"></n-switch>
        </n-space>
      </setting-item>
      <setting-item name="颜色">
        <n-color-picker v-model:value="xAxis.splitLine.lineStyle.color" size="small"></n-color-picker>
      </setting-item>
      <setting-item name="粗细">
        <n-input-number v-model:value="xAxis.splitLine.lineStyle.width" :min="1" size="small"></n-input-number>
      </setting-item>
      <setting-item name="类型">
        <n-select
          v-model:value="xAxis.splitLine.lineStyle.type"
          size="small"
          :options="axisConfig.splitLint.lineStyle.type"
        ></n-select>
      </setting-item>
    </setting-item-box> -->
  </collapse-item>

  <n-divider style="margin: 9px 0" v-if="yAxis"></n-divider>

  <collapse-item
    v-if="yAxis"
    name="Y轴设置"
    :expanded="yAxis.show"
    :isControl="true"
    divider-margin="margin: 9px 0 11px"
  >
    <template #header>
      <n-switch v-model:value="yAxis.show"></n-switch>
    </template>
    <div class="wrap">
      <CustomRadio label="y轴位置" v-model:value="yAxis.position" :options="selectyAxisPositionOption" />

      <n-divider n-divider style="margin: 1px 0 16px"></n-divider>

      <!-- 文本样式 -->
      <div class="subtitle">文本样式</div>
      <CustomInputSelect label="字体" v-model:value="yAxis.axisLabel.fontFamily" :options="selectTextFamilyOption" />

      <n-form label-placement="left">
        <n-form-item label="字号">
          <CustomInputNumber v-model:value="yAxis.axisLabel.fontSize" :min="1" :prefix="false" el-width="100%" />
        </n-form-item>
      </n-form>

      <!-- 可能要注释的地方 -->
      <CustomInputSelect
        label="样式"
        v-model:value="yAxis.axisLabel.fontWeight"
        :options="fontWeightOption"
        :sign="true"
      />

      <CustomColorPicker label-placement="left" label="颜色" v-model:value="yAxis.axisLabel.color" />

      <n-divider n-divider style="margin: 16px 0"></n-divider>

      <div class="subtitle">轴标签</div>
      <template v-if="yAxisType === 'value'">
        <n-form label-placement="left">
          <n-form-item label="最小值">
            <CustomInputNumber v-model:value="yAxis.min" :min="0" :prefix="false" el-width="100%" />
          </n-form-item>
          <CustomInput v-model:value="yAxis.max" :min="0" el-width="100%" label="最大值" labelPlacement="left" />
          <n-form-item label="标签数量">
            <CustomInputNumber v-model:value="yAxis.splitNumber" :prefix="false" el-width="100%" />
          </n-form-item>
        </n-form>
      </template>
      <template v-else-if="yAxisType === 'category'">
        <CustomInputNumberWithSlider
          label="标签倾斜度"
          v-model:value="yAxis.axisLabel.rotate"
          :min="0"
          :max="360"
          :step="1"
        />
        <n-form label-placement="left" label-width="70">
          <n-form-item label="标签间隔">
            <CustomInputNumber v-model:value="yAxis.axisLabel.interval" :prefix="false" el-width="100%" />
          </n-form-item>
        </n-form>
        <LabelStyleRadio
          label="标签展示"
          v-model:value="newAttr.xAxisLabelType"
          v-model:number="newAttr.xAxisLabelCount"
        />
        <CustomInputNumberWithSlider label="与轴线距离" v-model:value="yAxis.axisLabel.margin" />
      </template>

      <!-- <CustomInputSelect label="显示格式" v-model:value="yLabelVal" :options="yAxisLabelOption" /> -->

      <n-divider n-divider style="margin: 16px 0 10px"></n-divider>

      <CustomSwitch label="轴单位" v-model:value="newAttr.showYaxisName" label-color="#fff" el-margin-bottom="10px" />
      <div v-if="newAttr.showYaxisName">
        <CustomInput label="名称" labelPlacement="left" v-model:value="yAxis.name" placeholder="请输入单位" />
        <CustomColorPicker label-placement="left" label="颜色" v-model:value="yAxis.nameTextStyle.color" />
        <n-form label-placement="left">
          <n-form-item label="字号">
            <CustomInputNumber v-model:value="yAxis.nameTextStyle.fontSize" :prefix="false" :min="1" el-width="100%" />
          </n-form-item>
        </n-form>
      </div>

      <n-divider n-divider style="margin: 6px 0 10px"></n-divider>

      <CustomSwitch label="轴线" v-model:value="yAxis.axisLine.show" label-color="#fff" el-margin-bottom="10px" />
      <div v-if="yAxis.axisLine.show">
        <CustomColorPicker label-placement="left" label="颜色" v-model:value="yAxis.axisLine.lineStyle.color" />
        <n-form label-placement="left">
          <n-form-item label="粗细">
            <CustomInputNumber
              v-model:value="yAxis.axisLine.lineStyle.width"
              :prefix="false"
              :min="1"
              el-width="100%"
            />
          </n-form-item>
        </n-form>
      </div>

      <n-divider n-divider style="margin: 6px 0 10px"></n-divider>

      <CustomSwitch label="网格线" v-model:value="yAxis.splitLine.show" label-color="#fff" el-margin-bottom="10px" />
      <div v-if="yAxis.splitLine.show">
        <CustomColorPicker label-placement="left" label="颜色" v-model:value="yAxis.splitLine.lineStyle.color" />
        <CustomInputSelect
          label="类型"
          v-model:value="yAxis.splitLine.lineStyle.type"
          :options="selectSplitLineTypeOption"
        />
      </div>
    </div>
  </collapse-item>

  <n-divider style="margin: 9px 0" v-if="legend"></n-divider>

  <collapse-item
    v-if="legend"
    name="图例设置"
    :expanded="legend.show"
    :isControl="true"
    divider-margin="margin: 9px 0 11px"
  >
    <template #header>
      <n-switch v-model:value="legend.show"></n-switch>
    </template>
    <div class="wrap">
      <CustomRadio label="图例" v-model:value="legend.icon" :options="selectlegendStyleOption" />
      <CustomInputSelect label="位置" v-model:value="newAttr.legendPostion" :options="selectlegendPositionOption" />
      <CustomInputSelect label="排列方式" v-model:value="legend.orient" :options="selectlegendOrientOption" />
      <CustomInputNumberWithSlider label="图例间隔" v-model:value="legend.itemGap" />

      <!-- 可能要注释的地方 405-423 -->
      <n-divider style="margin: 16px 0"></n-divider>

      <div class="subtitle">文本样式</div>
      <CustomInputSelect label="字体" v-model:value="legend.textStyle.fontFamily" :options="selectTextFamilyOption" />

      <n-form label-placement="left">
        <n-form-item label="字号">
          <CustomInputNumber v-model:value="legend.textStyle.fontSize" :min="1" :prefix="false" el-width="100%" />
        </n-form-item>
      </n-form>

      <CustomInputSelect
        label="样式"
        v-model:value="legend.textStyle.fontWeight"
        :options="fontWeightOption"
        :sign="true"
      />

      <CustomColorPicker label-placement="left" label="颜色" v-model:value="legend.textStyle.color" />
    </div>
  </collapse-item>

  <n-divider style="margin: 9px 0"></n-divider>

  <collapse-item name="数值标记" :expanded="_sign" :isControl="true">
    <template #header>
      <n-switch v-model:value="_sign"></n-switch>
    </template>
  </collapse-item>

  <n-divider style="margin: 9px 0"></n-divider>

  <collapse-item name="基准标线" :expanded="_line" :isControl="true">
    <template #header>
      <n-switch v-model:value="_line"></n-switch>
    </template>
  </collapse-item>

  <!-- 可能要注释的地方 445 - 459 -->
  <n-divider style="margin: 9px 0" v-if="tooltip"></n-divider>

  <collapse-item v-if="tooltip" name="悬停Tooltip设置" :expanded="tooltip.show" :isControl="true">
    <template #header>
      <n-switch v-model:value="tooltip.show"></n-switch>
    </template>

    <div class="wrap">
      <n-form label-placement="left">
        <n-form-item label="字号">
          <CustomInputNumber v-model:value="tooltip.textStyle.fontSize" :min="1" :prefix="false" el-width="100%" />
        </n-form-item>
      </n-form>
    </div>
  </collapse-item>

  <n-divider style="margin: 6px 0 9px"></n-divider>

  <collapse-item v-if="visualMap" name="视觉映射">
    <template #header>
      <n-switch v-model:value="visualMap.show" size="small"></n-switch>
    </template>

    <setting-item-box name="范围">
      <setting-item name="最小值">
        <n-input-number v-model:value="visualMap.min" size="small"></n-input-number>
      </setting-item>
      <setting-item name="最大值">
        <n-input-number v-model:value="visualMap.max" size="small"></n-input-number>
      </setting-item>
    </setting-item-box>

    <setting-item-box name="颜色">
      <setting-item :name="`层级-${index + 1}`" v-for="(item, index) in visualMap.inRange.color" :key="index">
        <n-color-picker v-model:value="visualMap.inRange.color[index]" size="small"></n-color-picker>
      </setting-item>
    </setting-item-box>

    <setting-item-box name="控制块">
      <setting-item name="放置方向">
        <n-select v-model:value="visualMap.orient" size="small" :options="axisConfig.visualMap.orient"></n-select>
      </setting-item>
      <setting-item name="宽度">
        <n-input-number v-model:value="visualMap.itemWidth" :min="5" size="small"></n-input-number>
      </setting-item>
      <setting-item name="高度">
        <n-input-number v-model:value="visualMap.itemHeight" :min="5" size="small"></n-input-number>
      </setting-item>
      <setting-item name="反转">
        <n-space>
          <n-switch v-model:value="visualMap.inverse" size="small"></n-switch>
        </n-space>
      </setting-item>
      <setting-item name="拖拽组件实时更新">
        <n-space>
          <n-switch v-model:value="visualMap.realtime" size="small"></n-switch>
        </n-space>
      </setting-item>
    </setting-item-box>
    <global-setting-position :targetData="visualMap"></global-setting-position>
  </collapse-item>
</template>

<script setup lang="ts">
import { PropType, computed, reactive, ref, watch } from 'vue'
import { GlobalThemeJsonType } from '@/package/index.d'
import { axisConfig } from '@/package/config/axisConfig'
import {
  CustomInput,
  CustomInputNumber,
  CustomInputNumberWithSlider,
  CustomInputSelect,
  CustomColorPicker,
  CustomSwitch,
  CustomRadio,
  LabelStyleRadio
} from '@/components/Form'
import {
  CollapseItem,
  SettingItemBox,
  SettingItem,
  GlobalSettingPosition,
  ColorSetting,
  RadioSelectCustom
} from '@/components/Pages/ChartItemSetting'
import { ColorSelect } from './components'

const props = defineProps({
  optionData: {
    type: Object as PropType<GlobalThemeJsonType>,
    required: true
  },
  inChart: {
    type: Boolean,
    required: false,
    default: false
  },
  newAttr: {
    type: Object,
    default: () => {}
  }
})

// console.log('GlobalSetting收到的值', props.optionData)

const titlePosition = ref('顶部居左')
const titleMarginTopBottom = ref(40)

const title = computed(() => {
  return props.optionData.title
})

const xAxis = computed(() => {
  return props.optionData.xAxis
})

const xAxisType = computed(() => {
  return props.optionData.xAxis.type
})

const yAxis = computed(() => {
  return props.optionData.yAxis
})

const yAxisType = computed(() => {
  return props.optionData.yAxis.type
})

const legend = computed(() => {
  return props.optionData.legend
})

const grid = computed(() => {
  return props.optionData.grid
})

const tooltip = computed(() => {
  return props.optionData.tooltip
})

const series = computed(() => {
  return props.optionData.series
})

const visualMap = computed(() => {
  return props.optionData.visualMap
})

const selectSortOption = [
  { label: '按原始数据', value: '按原始数据' },
  { label: '按数据大小', value: '按数据大小' }
]
const selectxAxisPositionOption = [
  { label: '图表上方', value: 'top' },
  { label: '图表下方', value: 'bottom' }
]
const selectyAxisPositionOption = [
  { label: '图表左边', value: 'left' },
  { label: '图表右边', value: 'right' }
]
const selectTextFamilyOption = [
  { label: '微软雅黑', value: '微软雅黑' },
  { label: '宋体', value: '宋体' },
  { label: '黑体', value: '黑体' },
  { label: '庞门正道', value: 'PangMenZhengDao' },
  { label: 'DINPro', value: 'DINPro' }
]
const fontWeightOption = [
  { label: 'Normal', value: 'normal' },
  { label: 'Bold', value: 'bold' },
  { label: 'Bolder', value: 'bolder' },
  { label: 'Lighter', value: 'lighter' }
]
const selectTextStyleOption = [
  { label: 'Normal', value: 'normal' },
  { label: 'Italic', value: 'italic' },
  { label: 'Oblique', value: 'oblique' }
]
const selectyAxisMinOption = [
  { label: '默认', value: '默认' },
  { label: '10', value: '10' },
  { label: '100', value: '100' }
]
const selectyAxisMaxOption = [
  { label: '默认', value: '默认' },
  { label: '100', value: '100' },
  { label: '1000', value: '1000' }
]
const selectyAxisUnitOption = [
  { label: '默认', value: '默认' },
  { label: '元', value: '元' },
  { label: '人', value: '人' }
]
const selectlegendStyleOption = [
  { label: '圆形', value: 'circle' },
  { label: '方形', value: 'rect' }
]
const selectlegendPositionOption = [
  { label: '顶部居左', value: '顶部居左' },
  { label: '顶部居中', value: '顶部居中' },
  { label: '顶部居右', value: '顶部居右' },
  { label: '底部居中', value: '底部居中' },
  { label: '右边', value: '右边' },
  { label: '左边', value: '左边' }
]
const selectSplitLineTypeOption = [
  { label: '虚线', value: 'dashed' },
  { label: '实线', value: 'solid' },
  { label: '点', value: 'dotted' }
]

const fontFamilyOption = [
  { label: '微软雅黑', value: '微软雅黑' },
  { label: '宋体', value: '宋体' },
  { label: '黑体', value: '黑体' },
  { label: '庞门正道', value: 'PangMenZhengDao' },
  { label: 'DINPro', value: 'DINPro' }
]

const titlePositionOption = [
  { label: '顶部居左', value: '顶部居左' },
  { label: '顶部居中', value: '顶部居中' },
  { label: '顶部居右', value: '顶部居右' },
  { label: '中部居左', value: '中部居左' },
  { label: '居中', value: '居中' },
  { label: '中部居右', value: '中部居右' },
  { label: '底部居左', value: '底部居左' },
  { label: '底部居中', value: '底部居中' },
  { label: '底部居右', value: '底部居右' }
]

const selectlegendOrientOption = [
  { label: '水平排列', value: 'horizontal' },
  { label: '垂直排列', value: 'vertical' }
]

const yLabelVal = ref('默认')
const yLabelNum = ref(5)

const gridOpion = reactive({
  sort: '按原始数据',
  emptyHide: true,
  padGap: 0.5,
  groupGap: 0.5,
  radius: 5,
  topPad: 0.5,
  botPad: 0.5,
  leftPad: 0.5,
  topBottomPad: 25,
  leftRightPad: 25,
  seriesName: '',
  xAxis: {
    position: '图表下方',
    fontFamily: '宋体',
    fontSize: 12,
    fontStyle: 'Normal',
    label: {
      rotate: 0,
      gap: 0,
      withLabelGap: 8
    },
    labelLine: false,
    gridLine: false
  },
  yAxis: {
    position: '图表左边',
    fontFamily: '宋体',
    fontSize: 12,
    fontStyle: 'Normal',
    label: {
      rotate: 0,
      gap: 0,
      withLabelGap: 8
    },
    labelLine: false,
    gridLine: false,
    min: '默认',
    max: '默认',
    num: 5,
    unit: '默认'
  },
  legend: {
    type: '圆形',
    position: '底部居中'
  },
  fontFamily: '宋体',
  fontStyle: 'Normal',
  fontSize: 12,
  labelRotate: 0,
  labelGap: 0,
  withLabelGap: 8,
  startColor: '#00A0FF',
  endColor: '#006CFF',
  labelShowMode: 'all',
  labelCount: 5
})

const _sign = ref(false)
const _line = ref(false)
const _tooltip = ref(false)

watch(
  () => titleMarginTopBottom.value,
  val => {
    // 兼容旧图表，没有title属性不执行后面操作
    if (!title.value) return
    title.value.textStyle.lineHeight = val
  },
  { immediate: true }
)

watch(
  () => title.value,
  newVal => {
    if (newVal) {
      const _left = newVal.left
      const _top = newVal.top
      if (_left && !_top) {
        switch (_left) {
          case 'left':
            titlePosition.value = '顶部居左'
            break
          case 'center':
            titlePosition.value = '顶部居中'
            break
          case 'right':
            titlePosition.value = '顶部居右'
            break
        }
      } else if (!_left && _top) {
        switch (_top) {
          case 'top':
            titlePosition.value = '顶部居左'
            break
          case 'middle':
            titlePosition.value = '中部居左'
            break
          case 'bottom':
            titlePosition.value = '底部居左'
            break
        }
      } else {
        switch (`${_left}&${_top}`) {
          case 'left&top':
            titlePosition.value = '顶部居左'
            break
          case 'left&middle':
            titlePosition.value = '中部居左'
            break
          case 'left&bottom':
            titlePosition.value = '底部居左'
            break
          case 'center&top':
            titlePosition.value = '顶部居中'
            break
          case 'center&middle':
            titlePosition.value = '居中'
            break
          case 'center&bottom':
            titlePosition.value = '底部居中'
            break
          case 'right&top':
            titlePosition.value = '顶部居右'
            break
          case 'right&middle':
            titlePosition.value = '中部居右'
            break
          case 'right&bottom':
            titlePosition.value = '底部居右'
            break
        }
      }
    }
  },
  {
    deep: true,
    immediate: true
  }
)
watch(
  () => titlePosition.value,
  newVal => {
    switch (newVal) {
      case '顶部居左':
        title.value!.left = 'left'
        title.value!.top = 'top'
        break
      case '顶部居中':
        title.value!.left = 'center'
        title.value!.top = 'top'
        break
      case '顶部居右':
        title.value!.left = 'right'
        title.value!.top = 'top'
        break
      case '中部居左':
        title.value!.left = 'left'
        title.value!.top = 'middle'
        break
      case '居中':
        title.value!.left = 'center'
        title.value!.top = 'middle'
        break
      case '中部居右':
        title.value!.left = 'right'
        title.value!.top = 'middle'
        break
      case '底部居左':
        title.value!.left = 'left'
        title.value!.top = 'bottom'
        break
      case '底部居中':
        title.value!.left = 'center'
        title.value!.top = 'bottom'
        break
      case '底部居右':
        title.value!.left = 'right'
        title.value!.top = 'bottom'
        break
    }
  }
)

// 监听主题色
const changeTheme = (val: any) => {
  props.newAttr.themeColor.selfTheme.color = val.color
  props.newAttr.themeColor.selfTheme.value = val.val
}

// 监听标签展示
const allFormart = (v: any) => {
  // 全部展示
  return v
}
const wordSpceFormart = (num: number) => {
  // 超过num个字符换行
  function insertElementAtInterval(interval: number, array: Array<string>) {
    for (var i = interval; i < array.length; i += interval + 1) {
      array.splice(i, 0, '\n')
    }
    return array.join('')
  }
  return function (v: any) {
    let arr = v.split('')
    return insertElementAtInterval(num, arr)
  }
}
const ellispsFormart = (num: number) => {
  // 超过num个字符省略
  return function (v: any) {
    let arr = v.split('')
    let { length } = arr
    arr = arr.slice(0, num)
    if (length > num) {
      arr.push('...')
    }
    return arr.join('')
  }
}

watch(
  [() => props.newAttr.xAxisLabelType, () => props.newAttr.xAxisLabelCount],
  ([xAxisLabelType, xAxisLabelCount]) => {
    if (!xAxisLabelCount) return
    switch (xAxisLabelType) {
      case 'all': {
        if (xAxisType.value === 'category') {
          xAxis.value.axisLabel.formatter = allFormart
        } else if (yAxisType.value === 'category') {
          yAxis.value.axisLabel.formatter = allFormart
        }
        break
      }
      case 'wordspace': {
        if (xAxisType.value === 'category') {
          xAxis.value.axisLabel.formatter = wordSpceFormart(xAxisLabelCount)
        } else if (yAxisType.value === 'category') {
          yAxis.value.axisLabel.formatter = wordSpceFormart(xAxisLabelCount)
        }
        break
      }
      case 'ellisps': {
        if (xAxisType.value === 'category') {
          xAxis.value.axisLabel.formatter = ellispsFormart(xAxisLabelCount)
        } else if (yAxisType.value === 'category') {
          yAxis.value.axisLabel.formatter = ellispsFormart(xAxisLabelCount)
        }
        break
      }
    }
  },
  {
    immediate: true
  }
)

// 是否展示x轴单位
watch(
  () => props.newAttr.showXaxisName,
  val => {
    if (!val) {
      xAxis.value.name = ''
    }
  },
  {
    immediate: true
  }
)

// 图例位置
watch(
  () => props.newAttr.legendPostion,
  val => {
    switch (val) {
      case '顶部居左': {
        legend.value.top = 'top'
        legend.value.left = 'left'
        break
      }
      case '顶部居中': {
        legend.value.top = 'top'
        legend.value.left = 'middle'
        break
      }
      case '顶部居右': {
        legend.value.top = 'top'
        legend.value.left = 'right'
        break
      }
      case '底部居中': {
        legend.value.top = 'bottom'
        legend.value.left = 'middle'
        break
      }
      case '右边': {
        legend.value.top = 'center'
        legend.value.left = 'right'
        break
      }
      case '左边': {
        legend.value.top = 'center'
        legend.value.left = 'left'
        break
      }
    }
  },
  {
    immediate: true
  }
)
</script>

<style lang="scss" scoped>
@import '@/styles/pages/form.scss';
.font {
  display: flex;
  align-items: center;
}
.icon {
  width: 28px;
  height: 28px;
  background: #0e1216 #212b40;
  border: 1px solid $primary-color;
}
.n-space {
  padding: 0 20px 0 10px;
  margin-bottom: 15px;
  align-items: center;
  &.flex_between {
    justify-content: space-between !important;
  }
  &.flex_start {
    align-items: flex-start;
  }
  .n-text {
    display: inline-block;
    min-width: 70px;
    font-size: 12px;
    color: #808792;
  }
  .n-select,
  .n-input {
    width: 216px;
  }
  .n-slider {
    width: 115px;
  }
  .n-input-number {
    &.small {
      max-width: 85px;
    }
  }
  .n-color-picker {
    width: 28px;
    height: 28px;
  }
  :deep(.n-color-picker-trigger__value) {
    display: none;
  }
  .right {
    width: 216px;
    display: flex;
    align-items: center;
    .sqare {
      width: 28px;
      height: 28px;
    }
  }
}
.wrap {
  width: 100%;
  padding: 0 20px;
  box-sizing: border-box;
  .subtitle {
    height: 17px;
    font-size: 12px;
    font-family: PingFangSC-Medium, PingFang SC;
    font-weight: 500;
    color: #ffffff;
    line-height: 17px;
    margin-bottom: 10px;
    &.control {
      display: flex;
      justify-content: space-between;
      .btns {
        display: flex;
        gap: 10px;
        i {
          font-size: 16px;
          cursor: pointer;
        }
      }
    }
  }
}
h6 {
  padding-left: 10px;
  margin-bottom: 15px;
}

.btn {
  width: 100%;
  display: grid;
  place-items: center;
  margin-top: 20px;
  .add_series {
    color: #fff;
    &:hover {
      color: #fff;
    }
  }
}

.series_group {
  .tit {
    display: flex;
    justify-content: space-between;
    padding-right: 20px;
    i {
      cursor: pointer;
    }
  }
}
</style>
