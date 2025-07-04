/*
 * @Author: chenlong2
 * @Date: 2023-12-15 16:14:54
 * @LastEditTime: 2024-05-22 17:12:01
 * @LastEditors: chenlong2
 * @Description:
 */
import { computed, watch, toRefs } from 'vue'
import { cloneDeep } from 'loadsh'

/**
 * @description: 监听options上的animationLoop属性 通过计时器让组件重新加载实现动画播放效果
 * @return {*}
 */
const loopAnimationFn = (chartConfig, chartRef) => {
  console.log('chartConfig', chartConfig)
  let time
  watch(
    () => chartConfig.option.animationLoop,
    val => {
      // console.log('chart', chartRef)
      clearInterval(time)
      if (val.animationLoop) {
        // console.log('监听到loop变化')
        time = setInterval(() => {
          chartRef.value.clear()
          chartRef.value.setOption(chartConfig.option, true)
        }, val.cycleInterval)
      } else {
        clearInterval(time)
      }
    },
    { immediate: true, deep: true }
  )
}

export const useLoopAnimationWatch = () => {
  return {
    loopAnimationFn
  }
}
