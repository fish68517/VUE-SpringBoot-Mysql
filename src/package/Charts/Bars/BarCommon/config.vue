<template>
  <!-- Echarts 全局设置 -->
  <global-setting :optionData="optionData" :new-attr="newAttr" v-bind="$attrs">
    <template #style-setting>
      <n-divider style="margin: 9px 0 16px" />
      <collapse-item name="图形样式" :expanded="true">
        <div class="wrap" v-if="newAttr">
          <CustomInputSelect label="排序方式" v-model:value="newAttr.sortStyle" :options="selectSortOption" />
          <CustomSwitch label="空值隐藏" v-model:value="newAttr.emptyHide" elMarginBottom="10px" />
          <n-divider n-divider style="margin: 9px 0 16px" />

          <div class="subtitle">柱子样式</div>
          <InputNumberwithLabel label="柱外间距" :min="0" v-model:value="barCategoryGap" />
          <!-- <InputNumberwithLabel label="柱组内间距" :min="0" v-model:value="barGap" /> -->
          <InputNumberwithLabel label="圆角半径" :min="0" v-model:value="borderRadius" />
          <CustomInputSelect label="圆角位置" v-model:value="newAttr.borderPosition" :options="borderPositionOption" />

          <n-divider n-divider style="margin: 9px 0 16px" />

          <CustomSwitch label="柱子背景" labelColor="#fff" v-model:value="showBarBg" elMarginBottom="10px" />
          <!-- <CustomColorPicker
            v-if="showBarBg"
            label-placement="left"
            label="背景颜色"
            v-model:value="barBgColor"
          /> -->
          <NewColorPicker
            v-bind="$attrs"
            v-if="showBarBg"
            label-placement="left"
            label="背景颜色"
            v-model:value="barBgColor"
          />

          <n-divider n-divider style="margin: 9px 0 16px" />

          <CustomSwitch label="柱顶数据" labelColor="#fff" v-model:value="seriesLabelShow" el-margin-bottom="10px" />
          <template v-if="seriesLabelShow">
            <CustomInputSelect label="字体" v-model:value="seriesLabelFontFamily" :options="fontFamilyOption" />
            <InputNumberwithLabel label="字号" :min="1" v-model:value="seriesLabelFontSize" />
            <CustomInputSelect
              label="样式"
              v-model:value="seriesLabelFontWeight"
              :options="fontWeightOption"
              :sign="true"
            />
            <!-- <CustomColorPicker
              label-placement="left"
              label="文本颜色"
              v-model:value="seriesLabelFontColor"
            /> -->
            <NewColorPicker
              v-bind="$attrs"
              label-placement="left"
              label="文本颜色"
              v-model:value="seriesLabelFontColor"
            />
          </template>
        </div>
      </collapse-item>
    </template>

    <!-- <template #series-setting v-if="!newAttr.themeColor.useGlobalColor">
      <n-divider style="margin: 9px 0 16px" />
      <div class="wrap">
        <CustomSwitch
          label="开启系列配色"
          v-model:value="props.optionData.useSeriesColor"
          label-color="#fff"
          el-margin-bottom="10px"
        />
        <template v-if="props.optionData.useSeriesColor">
          <SeriesColors
            v-for="(item, index) in series"
            :key="index"
            v-model:color="optionData.color[index]"
            :series-index="index + 1"
            v-bind="$attrs"
          />
        </template>
      </div>
    </template> -->

    <template #other-setting>
      <collapse-item name="数值标记" :expanded="showMarkPoint" :isControl="true">
        <template #header>
          <n-switch v-model:value="showMarkPoint" />
        </template>
        <div class="wrap">
          <CustomSwitch label="标记最大值" v-model:value="series[0].markMax" elMarginBottom="10px" />
          <CustomSwitch label="标记最小值" v-model:value="series[0].markMin" elMarginBottom="10px" />
          <CustomInputSelect label="标记样式" v-model:value="symbolSetting.markSymbol" :options="symbols" />
          <InputNumberwithLabel label="标记宽度" :min="0" v-model:value="symbolSetting.symbolSize[0]" />
          <InputNumberwithLabel label="标记高度" :min="0" v-model:value="symbolSetting.symbolSize[1]" />
          <InputNumberwithLabel label="横向偏移" :min="-9999" v-model:value="symbolSetting.symbolOffset[0]" />
          <InputNumberwithLabel label="纵向偏移" :min="-9999" v-model:value="symbolSetting.symbolOffset[1]" />
          <InputNumberwithLabel label="标记字号" :min="1" v-model:value="symbolSetting.fontSize" />
        </div>
      </collapse-item>

      <n-divider style="margin: 9px 0"></n-divider>

      <collapse-item name="基准标线" :expanded="series[0].markLineSetting.show" :isControl="true">
        <template #header>
          <n-switch v-model:value="series[0].markLineSetting.show"></n-switch>
        </template>
        <div class="wrap">
          <CustomRadio label="标线类型" v-model:value="series[0].markLineSetting.type" :options="markLineOptions" />
          <InputNumberwithLabel
            v-if="series[0].markLineSetting.type === 'custom'"
            label="基线值"
            :min="0"
            v-model:value="series[0].markLineSetting.value"
          />
          <CustomInputSelect
            label="标线线型"
            v-model:value="optionData.series[0].markLine.lineStyle.type"
            :options="markLineLineStyleTypes"
          /> 
          <NewColorPicker
            v-bind="$attrs"
            label-placement="left"
            label="标线颜色"
            v-model:value="series[0].markLine.lineStyle.color"
          />
          <InputNumberwithLabel
            label="标线宽度"
            :min="1"
            v-model:value="series[0].markLine.lineStyle.width"
          />
          <CustomSwitch
            label="展示标线文本"
            v-model:value="series[0].markLine.label.show"
            elMarginBottom="10px"
          />
          <InputNumberwithLabel label="标线宽度" :min="1" v-model:value="series[0].markLine.lineStyle.width" />
          <CustomSwitch label="展示标线文本" v-model:value="series[0].markLine.label.show" elMarginBottom="10px" />
          <div v-if="series[0].markLine.label.show">
            <CustomInputSelect
              label="文本位置"
              v-model:value="series[0].markLine.label.position"
              :options="markLineLabelPositions"
            />
            <CustomInputSelect
              label="字体"
              v-model:value="series[0].markLine.label.fontFamily"
              :options="fontFamilyOption"
            />
            <InputNumberwithLabel label="字号" v-model:value="series[0].markLine.label.fontSize" />
            <CustomInputSelect
              label="样式"
              v-model:value="series[0].markLine.label.fontWeight"
              :options="fontWeightOption"
              :sign="true"
            />
            <NewColorPicker
              v-bind="$attrs"
              label-placement="left"
              label="颜色"
              v-model:value="series[0].markLine.label.color"
            />
            <!-- <InputNumberwithLabel
              label="上下偏移"
              :min="-99999"
              v-model:value="series[0].markLine.label.distance[1]"
            /> -->
            <InputNumberwithLabel
              label="左右偏移"
              :min="-99999"
              v-model:value="series[0].markLine.label.distance"
            />
          </div>
          <CustomSwitch
            label="超过标线变色"
            v-model:value="series[0].markLineSetting.showOverBaseColor"
            elMarginBottom="10px"
          />
          <div v-if="series[0].markLineSetting.showOverBaseColor">
            <CustomSwitch
              label="启用渐变色"
              v-model:value="series[0].markLineSetting.useGradient"
              elMarginBottom="10px"
            />
            <NewColorPicker
              v-bind="$attrs"
              label-placement="left"
              :label="series[0].markLineSetting.useGradient ? '开始颜色' : '柱子颜色'"
              v-model:value="series[0].markLineSetting.color[0]"
            />
            <NewColorPicker
              v-if="series[0].markLineSetting.useGradient"
              v-bind="$attrs"
              label-placement="left"
              label="结束颜色"
              v-model:value="series[0].markLineSetting.color[1]"
            />
          </div>
        </div>
      </collapse-item>
    </template>
  </global-setting>
</template>

<script setup lang="ts">
import { PropType, ref, computed, watch, reactive } from 'vue'
import { newAttrInter } from '@/package/index.d'
import cloneDeep from 'lodash/cloneDeep'
import { GlobalSetting, CollapseItem } from '@/components/Pages/ChartItemSetting'
import {
  fontFamilyOption,
  fontWeightOption,
  selectSortOption,
  symbols
} from '@/components/Pages/ChartItemSetting/globalSettings/config'
import {
  CustomInput,
  CustomInputNumber,
  CustomInputNumberWithSlider,
  CustomInputSelect,
  CustomColorPicker,
  NewColorPicker,
  CustomSwitch,
  CustomRadio,
  LabelStyleRadio,
  InputNumberwithLabel
} from '@/components/Form'
import { SeriesColor } from '@/components/Pages/ChartItemSetting/components/index'

const props = defineProps({
  optionData: {
    type: Object as PropType<any>,
    required: true
  },
  newAttr: {
    type: Object as PropType<newAttrInter>,
    required: true
  }
})
const series = computed(() => {
  return props.optionData.series
})

// 数值标记
const showMarkPoint = computed({
  get: () => props.optionData.series[0].showMarkPoint,
  set: val => {
    props.optionData.series[0].showMarkPoint = val
  }
})
const originSymbol = computed(() => {
  return props.optionData.series[0].markPoint
})
const symbolSetting = reactive({
  markSymbol: originSymbol.value?.markSymbol ?? 'pin',
  symbolSize: originSymbol.value?.symbolSize ?? [36, 36],
  symbolOffset: originSymbol.value?.symbolOffset ?? [0, 0],
  fontSize: originSymbol.value?.label?.fontSize ?? 12
})

const getMarkPoint = () => {
  const data = []
  if (props.optionData.series[0].markMax) {
    const max = Math.max(...series.value[0].data.map(item => parseFloat(item)))
    const indexArray = []
    series.value[0].data.forEach((item, index) => {
      if (item === max) {
        indexArray.push(index)
      }
    })
    indexArray.forEach(item => {
      data.push({
        value: max,
        xAxis: item,
        yAxis: max
      })
    })
  }
  if (props.optionData.series[0].markMin) {
    const min = Math.min(...series.value[0].data.map(item => parseFloat(item)))
    const indexArray = []
    series.value[0].data.forEach((item, index) => {
      if (item === min) {
        indexArray.push(index)
      }
    })
    indexArray.forEach(item => {
      data.push({
        value: min,
        xAxis: item,
        yAxis: min
      })
    })
  }
  return {
    data,
    symbol: symbolSetting.markSymbol,
    symbolSize: symbolSetting.symbolSize,
    symbolOffset: symbolSetting.symbolOffset,
    label: {
      fontSize: symbolSetting.fontSize
    }
  }
}

// 基准标线
const markLineOptions = [
  { label: '均值', value: 'average' },
  { label: '自定义基准值', value: 'custom' }
]
const markLineLabelPositions = [
  { label: "居左", value: "start" },
  // { label: "居中", value: "middle" },
  { label: "居右", value: "end" },
];
const markLineLineStyleTypes = [
  { label: '实线', value: 'solid' },
  { label: '虚线', value: 'dashed' }
]
const getMarkLine = () => {
  let data = []
  switch (props.optionData.series[0].markLineSetting.type) {
    case 'average': {
      data = [
        {
          name: '平均线',
          type: 'average'
        }
      ]
      break
    }
    case 'custom': {
      data = [
        {
          name: '自定义',
          yAxis: props.optionData.series[0].markLineSetting.value
        }
      ]
      break
    }
  }
  return {
    ...props.optionData.series[0].markLine,
    symbol: 'none',
    data
  }
}

// 排序方式 & 空值隐藏
const handleDataset = (dataset: any, sortType: string, emptyHide: boolean) => {
  const _dataset = cloneDeep(dataset)
  // 基础柱状图只有一个系列
  const keysArray: Array<string> = _dataset.dimensions.filter((it, i) => i === 1)
  const series = []

  // 空值隐藏
  if (emptyHide) {
    _dataset.source = _dataset.source.filter(item => {
      let flag = true
      keysArray.forEach(key => {
        if (parseInt(item[key]) !== 0) {
          flag = false
        }
      })
      return !flag
    })
  }

  // 排序方式
  if (sortType === 'sort') {
    _dataset.source.forEach(item => {
      item.total = 0
      keysArray.forEach(key => {
        item.total += Number(item[key])
      })
    })
    _dataset.source = _dataset.source.sort((a, b) => a.total - b.total)
  }

  // 组合数据
  keysArray.forEach(key => {
    const data = _dataset.source.map(item => item[key])
    series.push({
      ...props.optionData.series[0],
      data,
      name: key
    })
  })

  const xAxisData = _dataset.source.map(item => item[_dataset.dimensions[0]])
  props.optionData.xAxis.data = xAxisData

  // 标记数据
  if (showMarkPoint.value) {
    series[0].markPoint = getMarkPoint()
  } else {
    series[0].markPoint = {
      data: []
    }
  }
  // 基准标线
  if (series[0].markLineSetting.show) {
    series[0].markLine = getMarkLine()
  }

  props.optionData.series = series;
  setTimeout(() => {
    getItemStyle()
  }, 30)
};
watch(
  () => [props.optionData.dataset, props.newAttr.sortStyle, props.newAttr.emptyHide],
  ([dataset, sortType, emptyHide]) => {
    handleDataset(dataset, sortType as string, emptyHide as boolean)
  },
  { immediate: true, deep: true }
)

// 柱子背景
const showBarBg = ref(false)
const barBgColor = ref('#e9e9e90d')

// 柱组内间距
const barGap = ref(0)
// 柱外间距
const barCategoryGap = ref(60)
// 圆角半径
const borderRadius = ref(2)

// 柱顶数据是否展示
const seriesLabelShow = ref(true)
const seriesLabelFontFamily = ref('Microsoft YaHei')
const seriesLabelFontSize = ref(12)
const seriesLabelFontWeight = ref('normal')
const seriesLabelFontColor = ref('#ffffff')

// 监听 series：控制柱组内间距、柱外间距和圆角半径
watch(
  () => series.value,
  newVal => {
    const lastVal = newVal[newVal.length - 1]
    const { barGap: _barGap, barCategoryGap: _barCategoryGap, itemStyle } = lastVal
    const { borderPosition } = props.newAttr
    const gapValue = _barGap ? Number(_barGap.split('%')[0]) : null
    const categoryGapValue = _barCategoryGap ? Number(_barCategoryGap.split('%')[0]) : null
    const radiusValue = itemStyle.borderRadius
      ? borderPosition === '顶部'
        ? itemStyle.borderRadius[0]
        : itemStyle.borderRadius[2]
      : null

    if (gapValue !== null) {
      barGap.value = gapValue
    }

    if (categoryGapValue !== null) {
      barCategoryGap.value = categoryGapValue
    }

    if (radiusValue !== null) {
      borderRadius.value = radiusValue
    }

    seriesLabelShow.value = newVal[0]?.label?.show
    seriesLabelFontFamily.value = newVal[0]?.label?.fontFamily ?? 'Microsoft YaHei'
    // 字号
    seriesLabelFontSize.value = newVal[0]?.label?.fontSize
    // 样式
    seriesLabelFontWeight.value = newVal[0]?.label?.fontWeight ?? 'normal'
    // 文本颜色
    seriesLabelFontColor.value = newVal[0]?.label?.color

    // 柱图背景
    const { backgroundStyle, showBackground } = lastVal
    if (showBackground) {
      showBarBg.value = showBackground
    }
    if (backgroundStyle) {
      barBgColor.value = backgroundStyle.color
    }
  },
  {
    deep: true,
    immediate: true
  }
)
// 监听 柱组内间距、柱外间距和圆角半径：控制 series
watch(
  () => [barGap.value, barCategoryGap.value, borderRadius.value],
  newVal => {
    const _position = props.newAttr.borderPosition
    series.value[series.value.length - 1].barGap = `${newVal[0]}%`
    series.value[series.value.length - 1].barCategoryGap = `${newVal[1]}%`
    series.value.forEach((it: { itemStyle: { borderRadius: number[] } }) => {
      if (_position === '顶部') {
        it.itemStyle.borderRadius = [newVal[2], newVal[2], 0, 0]
      } else {
        it.itemStyle.borderRadius = [0, 0, newVal[2], newVal[2]]
      }
    })
  }
)
// 监听 圆角位置
watch(
  () => props.newAttr.borderPosition,
  newVal => {
    series.value.forEach((it: { itemStyle: { borderRadius: number[] } }) => {
      if (newVal === '顶部') {
        it.itemStyle.borderRadius = [borderRadius.value, borderRadius.value, 0, 0]
      } else {
        it.itemStyle.borderRadius = [0, 0, borderRadius.value, borderRadius.value]
      }
    })
  }
)

const borderPositionOption = [
  { label: '顶部', value: '顶部' },
  { label: '底部', value: '底部' }
]

watch(
  () => showBarBg.value,
  val => {
    if (val) {
      series.value.forEach((item: { showBackground: boolean; backgroundStyle: { color: string } }) => {
        item.showBackground = true
        item.backgroundStyle = { color: barBgColor.value }
      })
    } else {
      series.value.forEach((item: { showBackground: boolean }) => {
        item.showBackground = false
      })
    }
  }
)
watch(
  () => barBgColor.value,
  val => {
    series.value.forEach((item: { backgroundStyle: { color: string } }) => {
      item.backgroundStyle = { color: val }
    })
  }
)

// 监听柱顶数据是否展示
watch(
  () => [
    // 是否显示
    seriesLabelShow.value,
    // 字体
    seriesLabelFontFamily.value,
    // 字号
    seriesLabelFontSize.value,
    // 样式
    seriesLabelFontWeight.value,
    // 文本颜色
    seriesLabelFontColor.value
  ],
  ([show, fontFamily, fontSize, fontWeight, color]) => {
    series.value.forEach(it => {
      it.label.show = show as boolean
      it.label.fontFamily = fontFamily
      it.label.fontSize = fontSize
      it.label.fontWeight = fontWeight
      it.label.color = color
    })
  }
)

// 监听数值标记
watch(
  () => showMarkPoint.value,
  val => {
    if (val) {
      props.optionData.series[0].markPoint = getMarkPoint()
    } else {
      props.optionData.series[0].markPoint = {
        data: []
      }
    }
  },
  { immediate: true }
)
watch(
  () => [symbolSetting, series.value[0].markMax, series.value[0].markMin],
  () => {
    if (showMarkPoint.value) {
      props.optionData.series[0].markPoint = getMarkPoint()
    } else {
      props.optionData.series[0].markPoint = {
        data: []
      }
    }
  },
  { deep: true }
)

// 监听基准标线
watch(
  () => props.optionData.series[0].markLineSetting.show,
  val => {
    if (val) {
      props.optionData.series[0].markLine = getMarkLine()
    } else {
      props.optionData.series[0].markLine.data = []
    }
  },
  { immediate: true }
)
watch(
  () => series.value[0].markLineSetting,
  val => {
    if (val.show) {
      props.optionData.series[0].markLine = getMarkLine()
    }
    getItemStyle()
  },
  {
    deep: true
  }
)

const getItemStyle = () => {
  const { show, showOverBaseColor, useGradient, color, type } = props.optionData.series[0].markLineSetting
  const data = cloneDeep(props.optionData.series[0].data).map(item => {
    if (typeof item === 'number' || typeof item === 'string') {
      return item
    } else if (typeof item === 'object' && item.value) {
      return item.value
    }
  })
  
  if (!show) {
    props.optionData.series[0].data = data
    return
  } else {
    if (showOverBaseColor) {
      props.optionData.series[0].data = data.map((item, index) => {
        let markline = 0
        if (type === 'average') {
          const total = data.reduce((pre, v) => {return pre + (Number.isNaN(parseFloat(v)) ? 0 : parseFloat(v))}, 0)
          markline = data.length ? (total / data.length) : 0
        } else {
          markline = props.optionData.series[0].markLineSetting.value
        }
        if (item >= markline) {
          let itemColor = null
          if (useGradient) {
            itemColor = {
              colorStops:[
                {offset: 0, color: color[0]},
                {offset: 1, color: color[1]},
              ],
              type:"linear",
              x:0,
              x2: 0,
              y:0,
              y2:1
            }
          } else {
            itemColor = color[0]
          }
          return {
            name: props.optionData.xAxis.data[index],
            value: item,
            itemStyle: {
              color: itemColor
            }
          }
        } else {
          return {
            value: item
          }
        }
      })
    } else {
      props.optionData.series[0].data = data
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/styles/pages/form.scss';
.wrap {
  width: 100%;
  padding: 0 20px;
  box-sizing: border-box;
  .subtitle {
    height: 17px;
    font-size: 12px;
    font-family: PingFangSC-Medium, PingFang SC;
    font-weight: 500;
    // color: #ffffff;
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
</style>
