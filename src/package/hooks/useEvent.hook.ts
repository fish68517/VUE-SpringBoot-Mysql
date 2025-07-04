/*
 * @Author: kaix
 * @Date: 2023-09-18 18:51:57
 * @LastEditTime: 2024-11-15 15:24:34
 * @LastEditors: zouying
 * @Description:
 */
import { DetermineEnum } from '@/types/eventEnum'
import { EventCondition, EffectType, ChartEventType } from '@/types/event.d'
import { watch, onMounted } from 'vue'

// echarts的首次渲染完成
const useRendered = (chartInstance, emitFn: Function) => {
  chartInstance.on('finished', () => {
    chartInstance.off('finished')
    emitFn()
  })
}

// echarts的数据改变导致的重新渲染完成事件
const useDataRendered = (chartInstance, emitFn: Function, dataset: any) => {
  watch(
    () => dataset,
    () => {
      useRendered(chartInstance, emitFn)
    },
    {
      deep: true
    }
  )
}

// 普通组件的加载完成
const useCustomRendered = (emitFn: Function) => {
  onMounted(() => {
    emitFn()
  })
}

export const useEvent = () => {
  return {
    useRendered,
    useDataRendered,
    useCustomRendered
  }
}
