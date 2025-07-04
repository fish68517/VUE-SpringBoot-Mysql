import { series } from './../Charts/Bars/RotationRankingBarChart/config'
import { option } from './../Charts/Pies/RoseChart/config'
/*
 * @Author: kaix
 * @Date: 2023-12-15 14:47:58
 * @LastEditTime: 2024-12-11 15:53:17
 * @LastEditors: wangcong
 * @Description: 全局注入的 echarts 的 监听钩子 给每个组件使用 从index.vue 注入
 * @todo 处理 echarts 组件的 监听 事件
 */

import { toRefs, onMounted, computed, watch } from 'vue'
import cloneDeep from 'loadsh/cloneDeep'
import merge from 'lodash/merge'
import defaults from 'lodash/defaults'
import pick from 'lodash/pick'
import { useEvent } from '@/package/hooks/useEvent.hook'
import { useChartsAxisSetting } from '@/package/hooks/useChartsAxisSetting.hook'
import { useYAxisWatch } from '@/package/hooks/useYAxisWatch.hook'
import { useTooltipWatch } from '@/package/hooks/useTooltipWatch.hook'
import { useDataZoomWatch } from '@/package/hooks/useDataZoomWatch.hook'
import { useLoopAnimationWatch } from '@/package/hooks/useLoopAnimation.hook'
import isNil from 'lodash/isNil'
/**
 * @description: 事件的全局处理 echarts只需处理渲染完成和数据变更
 * @param {*} chartRef
 * @param {*} emit
 * @param {*} chartConfig
 * @return {*}
 */
const eventWatchFn = (chartRef, emit, chartConfig) => {
  const { dataset } = toRefs(chartConfig.option)
  const { useRendered, useDataRendered } = useEvent()
  onMounted(() => {
    if (chartRef.value) {
      const chartInstance = chartRef.value.chart
      // 渲染完成事件 悬浮，点击事件也会触发钩子函数 因此使用后注销
      const emitFinished = () => {
        emit('finishedFn')
      }
      // 首次渲染钩子
      useRendered(chartInstance, emitFinished)
      // 数据改变渲染完成钩子
      useDataRendered(chartInstance, emitFinished, dataset)
    }

  })
}

/**
 * @description: 全局配色的监听处理
 * @param {*} chartConfig
 * @param {*} themeColor
 * @return {*}
 */
const colorThemeWatchFn = (chartConfig, themeColor) => {
  const finalThemeColor = computed(() => {
    return chartConfig.newAttr.themeColor.useGlobalColor ? themeColor : chartConfig.newAttr.themeColor.selfTheme
  })

  watch(
    () => finalThemeColor.value,
    val => {
      if (chartConfig.newAttr.themeColor.useGlobalColor) {
        chartConfig.option.color = cloneDeep(val.color)
      } else {
        if (!chartConfig.option.useSeriesColor) {
          chartConfig.option.color = cloneDeep(val.color)
        }
      }
    },
    { immediate: true, deep: true }
  )
  return finalThemeColor
}

/**
 * @description: 全量数据的监听处理
 * @param {*} chartConfig
 * @return {*}
 */
const datasetParamsMapsFn = (chartConfig, ins) => {
  // 兼容老的组件 没有 这些设置的情况
  if (!chartConfig.option.allDatas) {
    chartConfig.option.allDatas = []
  }
  if (!chartConfig.option.datasetParamsMaps) {
    chartConfig.option.datasetParamsMaps = { interactiveParams: null, dataKey: null }
  } else {
    if (!Object.prototype.hasOwnProperty.call(chartConfig.option.datasetParamsMaps, 'interactiveParams')) {
      chartConfig.option.datasetParamsMaps.interactiveParams = null
    }
    if (!Object.prototype.hasOwnProperty.call(chartConfig.option.datasetParamsMaps, 'dataKey')) {
      chartConfig.option.datasetParamsMaps.interactiveParams = null
    }
  }

  watch(
    () => chartConfig.option.datasetParamsMaps,
    val => {
      const { interactiveParams, dataKey } = val
      const data = chartConfig.option.allDatas.find(item => item.key === interactiveParams || item.key === dataKey)
      try {
        console.log('%c🚀', data)
        if (data && data.value) {
          chartConfig.option.dataset = data.value
        }
        // todo 不知道哪里的问题 tooltip变成数组了 给他还原一下
        if (Array.isArray(chartConfig.option.tooltip)) {
          chartConfig.option.tooltip = chartConfig.option.tooltip[0]
        }
        console.log(
          '%c🎆🎆🎆🎆🎆图表实例的option:🎆🎆🎆🎆🎆',
          'color: red;font-size: 24px',
          ins.value.chart.getOption()
        )
      } catch (e) {
        console.log(e)
        console.log('没有找到对应的数据')
      }
    },
    { deep: true }
  )
}

/**
 * @description: X轴的全局监听处理
 * @param {*} chartConfig
 * @return {*}
 */
const XAxisFn = chartConfig => {
  const { xAxis } = chartConfig.option
  if (xAxis) {
    const { watchXaxisLabel } = useChartsAxisSetting(chartConfig)
    watchXaxisLabel()
  }
}

/**
 * @description: Y轴的全局监听处理
 * @param {*} chartConfig
 * @return {*}
 */
const YAxisFn = chartConfig => {
  const { yAxis } = chartConfig.option
  if (yAxis) {
    const { yAxisFn } = useYAxisWatch()
    yAxisFn(chartConfig)
  }
}

/**
 * @description: tootip的全局监听处理
 * @param {*} chartConfig
 * @return {*}
 */
const ToolTipFn = (chartConfig, chartRef, type?: 'h' | 'v', use: boolean = true) => {
  const { tooltip } = chartConfig.option
  if (tooltip) {
    const { toolTipFn } = useTooltipWatch()
    toolTipFn(chartConfig, chartRef, type, use)
  }
}

const DataZoomFn = (chartConfig, chartRef) => {
  // useDataZoom 是否自定义了 dataZoom的逻辑处理
  const { dataZoom, useDataZoom = true } = chartConfig.option
  if (dataZoom && useDataZoom) {
    const { dataZoomFn } = useDataZoomWatch()
    dataZoomFn(chartConfig, chartRef)
  }
}

/**
 * @description: 循环动画的全局监听处理
 * @param {*} chartConfig
 * @return {*}
 */
const LoopAnimationFn = (chartConfig, chartRef) => {
  const { animationLoop } = chartConfig.option
  if (animationLoop) {
    const { loopAnimationFn } = useLoopAnimationWatch()
    loopAnimationFn(chartConfig, chartRef)
  }
}

/**
 * @description: 合并 属性
 * @param {*} T
 * @param {*} U
 * @return {*} includes 包含的属性
 */
export const mergeOption = <T, U>(option: T, defaultOption: U, includes: string[]) => {
  if (includes.length === 0) {
    // return (option = merge({}, defaultOption, option))
    return defaults(option, defaultOption)
  }
  // return (option = merge({}, pick(defaultOption, includes), option))
  return defaults(option, pick(defaultOption, includes))
}

/**
 * @description: 全局的 监听钩子 写钩子之前应加上判断
 * todo 1. 事件的钩子 2. 全局配色的钩子 3.全局数据的钩子 4. 全局的监听钩子 5. 自定义的公用钩子
 * @return {*}
 */
type OrtherConfig = {
  type?: 'h' | 'v'
}
const globalWatch = (chartRef, emit, chartConfig, _defaultOption, themeColor, ortherConfig?: OrtherConfig, useYAxisFn: boolean = true) => {
  const defaultOption = cloneDeep(_defaultOption)
  const { type = 'v' } = ortherConfig ?? {}
  // 合并 原来属性和默认属性 针对默认项的情况处理
  const option = computed(() => {
    // console.log(chartConfig.option, 'chartConfig.option')
    return mergeOption(chartConfig.option, defaultOption, [])
  })
  // 事件的钩子
  eventWatchFn(chartRef, emit, chartConfig)

  //   全量数据的钩子
  datasetParamsMapsFn(chartConfig, chartRef)
  // X轴的钩子
  XAxisFn(chartConfig)
  useYAxisFn && YAxisFn(chartConfig)
  ToolTipFn(chartConfig, chartRef, type)
  watch(
    () => chartConfig.option.animationLoop,
    val => {
      LoopAnimationFn(chartConfig, chartRef)
    },
    { deep: true, immediate: true }
  )
  // 针对 柱状图和折线图
  if (!isNil(defaultOption.dataZoom) && isNil(option.value.dataZoom)) {
    option.value.dataZoom = defaultOption.dataZoom
    option.value.showDataZoom = defaultOption.showDataZoom
  }
  DataZoomFn(chartConfig, chartRef)

  // 全局配色的钩子
  const finalThemeColor = colorThemeWatchFn(chartConfig, themeColor)

  // 需要返回的变量，放在这
  return {
    finalThemeColor,
    option,
    ToolTipFn
  }
}

export const useEchartsWatch = () => {
  return {
    globalWatch
  }
}
