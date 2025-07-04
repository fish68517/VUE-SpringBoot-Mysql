/*
 * @Author: kaix
 * @Date: 2023-12-15 16:14:54
 * @LastEditTime: 2024-07-08 10:04:21
 * @LastEditors: kaix
 * @Description:
 */
import { toRefs, watch, nextTick } from 'vue'

const dataZoomFn = (chartConfig, chartRef) => {
  // console.log('chartConfig', chartConfig)
  const dataZoomInstance = chartConfig.option.dataZoom[0]
  if (dataZoomInstance) {
    const { isOpenInterval, intervalTime, endValue, scrollNum } = toRefs(dataZoomInstance)
    let timer = -1
    // const endNum = endValue.value
    const length = chartConfig.option.dataset?.source?.length ?? 0
    const reset = (interval = true) => {
      if (interval) {
        chartConfig.option.dataZoom[0].isOpenInterval = false
      }
      chartConfig.option.dataZoom[0].startValue = 0
      chartConfig.option.dataZoom[0].endValue = scrollNum.value - 1
    }
    reset(false)

    const timerFun = () => {
      // 每次向后滚动一个，最后一个从头开始；
      if (chartConfig.option.dataZoom[0].endValue === length - 1) {
        timer !== -1 && clearInterval(timer)
        chartConfig.option.dataZoom[0].startValue = 0
        chartConfig.option.dataZoom[0].endValue = scrollNum.value - 1
        timer = setInterval(timerFun, intervalTime.value as number)
        // 为了保持
      } else {
        chartConfig.option.dataZoom[0].endValue += 1
        chartConfig.option.dataZoom[0].startValue += 1
      }
    }

    nextTick(() => {
      setTimeout(() => {
        // 组件实例
        const chartInstance = chartRef.value?.chart
        // 监听组件事件
        chartInstance.on('mouseover', () => {
          if (isOpenInterval.value) {
            timer !== -1 && clearInterval(timer)
          }
        })
        chartInstance.on('globalout', () => {
          if (isOpenInterval.value) {
            timer !== -1 && clearInterval(timer)
            timer = setInterval(timerFun, intervalTime.value as number)
          }
        })
      }, 20);
    })

    watch(
      () => chartConfig.option.showDataZoom,
      val => {
        if (val) {
          dataZoomInstance.show = true
          delete chartConfig.option.dataZoom[0].end
        } else {
          dataZoomInstance.show = false
          timer !== -1 && clearInterval(timer)
          reset()
          chartConfig.option.dataZoom[0].end = 100
        }
      },
      {
        immediate: true
      }
    )
    watch(
      () => [isOpenInterval.value, intervalTime.value],
      ([open, times]) => {
        timer !== -1 && clearInterval(timer)
        if (open) {
          dataZoomInstance.show = false
          timer = setInterval(timerFun, times as number)
        } else {
          // 是否开启 dataZoom
          if (chartConfig.option.showDataZoom) {
            dataZoomInstance.show = true
          } else {
            dataZoomInstance.show = false
          }
          reset()
        }
      },
      { immediate: true }
    )
    watch(
      () => scrollNum.value,
      nums => {
        timer !== -1 && clearInterval(timer)
        chartConfig.option.dataZoom[0].startValue = 0
        chartConfig.option.dataZoom[0].endValue = nums - 1
        if (isOpenInterval.value) {
          timer = setInterval(timerFun, intervalTime.value as number)
        }
      },
      { immediate: true }
    )
  }
}

export const useDataZoomWatch = () => {
  return {
    dataZoomFn
  }
}
