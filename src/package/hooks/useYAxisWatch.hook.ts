/*
 * @Author: kaix
 * @Date: 2023-12-15 16:14:54
 * @LastEditTime: 2025-01-22 09:06:10
 * @LastEditors: wangcong
 * @Description:
 */
import { computed, watch, toRefs } from 'vue'
import { getUUID, getYInterval, isNumber } from '@/utils'

/**
 * @description: / 用 dimensions 指定了维度的顺序。直角坐标系中，如果 X 轴 type 为 category，
    // 默认把第一个维度映射到 X 轴上，后面维度映射到 Y 轴上。
 * @return {*}
 */
const yAxisFn = chartConfig => {
  // 处理 轴标签的分割个数
  const yAxis = chartConfig.option.yAxis
  // y轴增加标签数量、步长后，数据变换时需要监听修改最大值
  const dataset = computed(() => {
    return chartConfig.option.dataset
  })
  // 目前针对单个轴线
  if (!Array.isArray(yAxis)) {
    //   分割线默认值是5
    const splitNumber = computed(() => {
      return yAxis.splitNumber
    })
    const yAxisMax = computed(() => {
      return yAxis.max
    })
    const yAxisMin = computed(() => {
      return yAxis.min
    })
    // 显示标签数量
    const mySplitNumber = computed(() => {
      return yAxis.mySplitNumber
    })
    // 显示最大值
    const maxNum = computed(() => {
      return yAxis.maxNum
    })
    // 是否刻度自适应
    const isScaleSelfadaption = computed(() => {
      return yAxis?.isScaleSelfadaption?.length || 0
    })
    // 刻度模式，'1'数量强制,'2'步长强制
    const scaleType = computed(() => {
      return yAxis.scaleType
    })
    const minInterval = computed(() => {
      return yAxis.minInterval
    })
    //y轴步长，显示作用，与minInterval区分
    const myMinInterval = computed(() => {
      return yAxis.myMinInterval
    })

    // 这里使用了 flush: 'post' 保证一定能读取到 dataset
    watch(
      () => [
        splitNumber.value,
        yAxisMax.value,
        yAxisMin.value,
        mySplitNumber.value,
        maxNum.value,
        isScaleSelfadaption.value,
        scaleType.value,
        minInterval.value,
        myMinInterval.value,
      ],
      ([
        val,
        newYAxisMax,
        newYAxisMin,
        newMySplitNumber,
        newMaxNum,
        isfadaption,
        stype,
        newMinInterval,
        newMyMinInterval,
      ]) => {
        delete chartConfig.option.yAxis.splitNumber
        delete chartConfig.option.yAxis.interval
        if (isfadaption) { // 自适应
          chartConfig.key = getUUID()
        } else { // 设置
          if (stype === '1') {
            let min = Infinity
            let max = -Infinity
            if (newYAxisMin === 'dataMin') {
              const res = dataset.value.source.reduce((pre, item) => {
                const arr = Object.values(item).map(it => Number.isNaN(Number(it)) ? Infinity : Number(it))
                return pre > Math.min(...arr) ? Math.min(...arr) : pre
              }, min)
              min = res
            } else {
              min = newYAxisMin
            }
            if (newYAxisMax === 'dataMax') {
              const res = dataset.value.source.reduce((pre, item) => {
                const arr = Object.values(item).map(it => Number.isNaN(Number(it)) ? -Infinity : Number(it))
                return pre < Math.max(...arr) ? Math.max(...arr) : pre
              }, max)
              max = res
            } else {
              max = newYAxisMax
            }
            const count = newMySplitNumber - 1
            chartConfig.option.yAxis.interval = parseFloat(((max - min) / count).toFixed(1))
          } else {
            chartConfig.option.yAxis.interval = newMyMinInterval
          }
          
        }
        // if (isNumber(val)) {
        //   const { dimensions, source } = chartConfig.option.dataset
        //   const dataKeys = dimensions.filter((item, index) => index !== 0)
        //   let datas = []
        //   dataKeys.forEach(item => {
        //     datas = datas.concat(source.map(i => i[item]))
        //   })

        //   const { max, interval } = getYInterval(datas, val - 1, newMinInterval)

        //   if (chartConfig.option.yAxis.type === 'value') {
        //     if (newMaxNum === 'dataMax' || newMaxNum === null) {
        //       chartConfig.option.yAxis.max = max
        //     } else {
        //       chartConfig.option.yAxis.max = newMaxNum
        //     }
        //     if (!isfadaption) {
        //       if (stype === '1') {
        //         delete chartConfig.option.yAxis.interval
        //         chartConfig.option.yAxis.minInterval = 1
        //         if (isNumber(newMySplitNumber)) {
        //           chartConfig.option.yAxis.splitNumber = newMySplitNumber
        //           if (isNumber(newYAxisMax)) {
        //             chartConfig.option.yAxis.interval = Math.ceil((newYAxisMax - newYAxisMin) / (newMySplitNumber - 1))
        //           }
        //         } else {
        //           chartConfig.option.yAxis.splitNumber = 5
        //         }
        //         if (interval < (newYAxisMax - newYAxisMin) / (newMySplitNumber - 1)) {
        //           chartConfig.option.yAxis.interval = Math.ceil((newYAxisMax - newYAxisMin) / (newMySplitNumber - 1))
        //         }
        //       } else {
        //         chartConfig.option.yAxis.splitNumber = 5
        //         chartConfig.option.yAxis.minInterval = newMyMinInterval
        //         chartConfig.option.yAxis.interval = newMinInterval
        //       }
        //     } else {
        //       chartConfig.option.yAxis.minInterval = 1
        //       delete chartConfig.option.yAxis.interval
        //       chartConfig.option.yAxis.splitNumber = 6
        //       if (interval < (newYAxisMax - newYAxisMin) / 5) {
        //         chartConfig.option.yAxis.interval = Math.ceil((newYAxisMax - newYAxisMin) / 5)
        //       }
        //     }
        //   }
        // }
      },
      {
        flush: 'post',
        deep: true
      }
    )
  } else {
    const splitNumber = computed(() => {
      return [yAxis[0].splitNumber, yAxis[1].splitNumber]
    })
    const yAxisMax = computed(() => {
      return [yAxis[0].max, yAxis[1].max]
    })
    const yAxisMin = computed(() => {
      return [yAxis[0].min, yAxis[1].min]
    })
    // 显示标签数量
    const mySplitNumber = computed(() => {
      return [yAxis[0].mySplitNumber, yAxis[1].mySplitNumber]
    })
    // 显示最大值
    const maxNum = computed(() => {
      return [yAxis[0].maxNum, yAxis[1].maxNum]
    })
    // 是否刻度自适应
    const isScaleSelfadaption = computed(() => {
      return [yAxis[0].isScaleSelfadaption?.length || 0, yAxis[1].isScaleSelfadaption?.length || 0]
    })
    // 刻度模式，'1'数量强制,'2'步长强制
    const scaleType = computed(() => {
      return [yAxis[0].scaleType, yAxis[1].scaleType]
    })
    const minInterval = computed(() => {
      return [yAxis[0].minInterval, yAxis[1].minInterval]
    })
    //y轴步长，显示作用，与minInterval区分
    const myMinInterval = computed(() => {
      return [yAxis[0].myMinInterval, yAxis[1].myMinInterval]
    })

    // 这里使用了 flush: 'post' 保证一定能读取到 dataset
    watch(
      [
        splitNumber,
        yAxisMax,
        yAxisMin,
        mySplitNumber,
        maxNum,
        isScaleSelfadaption,
        scaleType,
        minInterval,
        myMinInterval,
        dataset
      ],
      ([
        val,
        newYAxisMax,
        newYAxisMin,
        newMySplitNumber,
        newMaxNum,
        isfadaption,
        stype,
        newMinInterval,
        newMyMinInterval,
        newDataset
      ]) => {
        // console.log('newMySplitNumber', newMySplitNumber)

        // console.log('change')

        const { dimensions, source } = chartConfig.option.dataset
        const dataKeys = dimensions.filter((item, index) => index !== 0)
        let datas = []
        dataKeys.forEach(item => {
          datas = datas.concat(source.map(i => i[item]))
        })
        yAxis.forEach((item, i) => {
          const { max, interval } = getYInterval(datas[i], val[i] - 1, newMinInterval[i])
          const { type } = item
          // console.log('type', type)
          if (type === 'value') {
            if (newMaxNum[i] === 'dataMax' || newMaxNum === null) {
              chartConfig.option.yAxis[i].max = max
            } else {
              chartConfig.option.yAxis[i].max = newMaxNum[i]
            }
            // if(newMaxNum && newMaxNum !== 'dataMax'){
            //   chartConfig.option.yAxis.max = newMaxNum
            // }
            if (!isfadaption[i]) {
              if (stype[i] === '1') {
                delete chartConfig.option.yAxis[i].interval
                chartConfig.option.yAxis[i].minInterval = 1
                if (isNumber(newMySplitNumber[i])) {
                  chartConfig.option.yAxis[i].splitNumber = newMySplitNumber[i]
                  if (isNumber(newYAxisMax[i])) {
                    chartConfig.option.yAxis[i].interval = Math.ceil(
                      (newYAxisMax[i] - newYAxisMin[i]) / (newMySplitNumber[i] - 1)
                    )
                  }
                } else {
                  chartConfig.option.yAxis[i].splitNumber = 5
                }
                if (interval < (newYAxisMax[i] - newYAxisMin[i]) / (newMySplitNumber[i] - 1)) {
                  chartConfig.option.yAxis[i].interval = Math.ceil(
                    (newYAxisMax[i] - newYAxisMin[i]) / (newMySplitNumber[i] - 1)
                  )
                }
              } else {
                chartConfig.option.yAxis[i].splitNumber = 5
                chartConfig.option.yAxis[i].minInterval = newMyMinInterval[i]
                chartConfig.option.yAxis[i].interval = newMinInterval[i]
              }
            } else {
              chartConfig.option.yAxis[i].minInterval = 1
              delete chartConfig.option.yAxis[i].interval
              chartConfig.option.yAxis[i].splitNumber = 6
              if (interval < (newYAxisMax[i] - newYAxisMin[i]) / 5) {
                chartConfig.option.yAxis[i].interval = Math.ceil((newYAxisMax[i] - newYAxisMin[i]) / 5)
              }
              // chartConfig.option.yAxis.interval = interval
              // chartConfig.option.yAxis.splitNumber = val
            }
          }
        })

        // console.log('max', max, interval)
      },
      {
        flush: 'post',
        deep: true
      }
    )
  }
}

export const useYAxisWatch = () => {
  return {
    yAxisFn
  }
}
