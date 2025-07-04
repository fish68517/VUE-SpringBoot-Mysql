<!--
 * @Author: wangcong
 * @Date: 2023-08-16 08:41:09
 * @LastEditTime: 2025-07-04 14:23:42
 * @LastEditors: kaix
 * @Description: 
-->
<template>
  <v-chart ref="chartRef" v-on="$attrs" :option="option" autoresize />
</template>

<script setup lang="ts">
import { computed, onMounted, PropType, watch, nextTick, ref } from 'vue'
import config, { option as _defaultOption } from './config'
import cloneDeep from 'loadsh/cloneDeep'
import { useEchartsWatch } from '@/package/hooks/useEchartsWatch.hook'
import { getUUID } from '@/utils'

// GlobalParams的类型定义
interface GlobalParams {
  params: { [key: string]: any }
  setParam: (param: string, value: any) => void
  getParam: (param: string) => void
  removeParam: (param: string) => void
}

interface EventBus {
  on: (eventName: string, callback: (data: any) => void) => void
  emit: (eventName: string, data: any) => void
  off: (eventName: string, callback?: (data: any) => void) => void
}

const props = defineProps({
  themeColor: {
    type: Object,
    default: () => ({})
  },
  chartConfig: {
    type: Object as PropType<config>,
    default: () => ({})
  },
  globalParams: {
    type: Object as () => GlobalParams,
    required: false
  },
  bus: {
    type: Object as () => EventBus,
    required: false
  }
})

// 方案1：直接访问 store 的 params 对象（推荐）
const theme = computed(() => props.globalParams?.params?.theme)
watch(theme, (newTheme) => {
  console.log('globalParams: theme: newTheme', newTheme)
}, { immediate: true })

// 方案2：在watch中直接访问params
// watch(
//   () => props.globalParams?.params?.theme,
//   (newTheme) => {
//     console.log('globalParams: theme: newTheme', newTheme)
//   },
//   { immediate: true }
// )

const fn1 = (data) => {
  console.log('update:theme:fn1', data)
}
const fn2 = (data) => {
  console.log('update:theme:fn2', data)
}
props.bus?.on('update:theme', fn1)
props.bus?.on('update:theme', fn2)


const { globalWatch } = useEchartsWatch()

// 组件实例
const chartRef = ref(null)
// 自定义组件的事件 参考config.ts的定义
const emit = defineEmits<{
  // 加载完成
  (e: 'finishedFn'): void
}>()

// 柱顶数据
watch(
  () => props.chartConfig.option.series[0]?.label?.show,
  show => {
    if (show) {
      props.chartConfig.option.grid.top = 25
    } else {
      props.chartConfig.option.grid.top = 10
    }
  }
)

// 监听全局颜色
const finalThemeColor = computed(() => {
  return props.chartConfig.newAttr.themeColor.useGlobalColor
    ? props.themeColor
    : props.chartConfig.newAttr.themeColor.selfTheme
})

watch(
  () => finalThemeColor.value,
  val => {
    console.log(props.chartConfig, val, 'barCommon监听')
    if (props.chartConfig.newAttr.themeColor.useGlobalColor) {
      props.chartConfig.option.color = cloneDeep(val.color)
    } else {
      if (!props.chartConfig.option.useSeriesColor) {
        props.chartConfig.option.color = cloneDeep(val.color)
      }
    }
  },
  { immediate: true, deep: true }
)

// 排序方式 & 空值隐藏
const handleDataset = (dataset: any, sortType: string, emptyHide: boolean) => {
  const _dataset = cloneDeep(dataset)
  // 基础柱状图只有一个系列
  const keysArray: Array<string> = _dataset.dimensions.filter((it, i) => i === 1)
  const series = []

  if (keysArray.length > 0) {
    // 空值隐藏, （只隐藏值为空的系列）
    if (emptyHide) {
      _dataset.source = _dataset.source.filter(item => {
        const key = keysArray[0]
        return item[key] !== ''
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
    keysArray.forEach((key) => {
      const data = _dataset.source.map((item) => item[key])
      series.push({
        ...props.chartConfig.option.series[0],
        data,
        name: key,
      })
    })
  } else {
    series[0] = {
      ...props.chartConfig.option.series[0],
      data: [],
      name: ''
    }
  }

  const xAxisData = _dataset.source.map(item => item[_dataset.dimensions[0]])

  setTimeout(() => {
    props.chartConfig.option.xAxis.data = xAxisData;
    props.chartConfig.option.series = series;
  }, 10)
  setTimeout(() => {
    getItemStyle()
  }, 30)
}
watch(
  () => [props.chartConfig.option.dataset, props.chartConfig.newAttr.sortStyle, props.chartConfig.newAttr.emptyHide],
  ([dataset, sortType, emptyHide]) => {
    handleDataset(dataset, sortType as string, emptyHide as boolean)
  },
  { immediate: true, deep: true }
)
watch(
  () => props.chartConfig.option.dataset,
  val => {
    emit('finishedFn')
  },
  { deep: true }
)

const getItemStyle = () => {
  const { show, showOverBaseColor, useGradient, color, type } = props.chartConfig.option.series[0].markLineSetting
  const data = cloneDeep(props.chartConfig.option.series[0].data).map(item => {
    if (typeof item === 'number' || typeof item === 'string') {
      return item
    } else if (typeof item === 'object' && item.value) {
      return item.value
    }
  })
  if (!show) {
    props.chartConfig.option.series[0].data = data
    return
  } else {
    if (showOverBaseColor) {
      props.chartConfig.option.series[0].data = data.map((item, index) => {
        let markline = 0
        if (type === 'average') {
          const total = data.reduce((pre, v) => {return pre + (Number.isNaN(parseFloat(v)) ? 0 : parseFloat(v))}, 0)
          markline = data.length ? (total / data.length) : 0
        } else {
          markline = props.chartConfig.option.series[0].markLineSetting.value
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
            name: props.chartConfig.option.xAxis.data[index],
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
      props.chartConfig.option.series[0].data = data
    }
  }
}

// 全局监听钩子
const { option } = globalWatch(chartRef, emit, props.chartConfig, _defaultOption, props.themeColor)
</script>

<script lang="ts">
export default {
  name: "BarCommon",
  version: "2.5.2",
};
</script>
