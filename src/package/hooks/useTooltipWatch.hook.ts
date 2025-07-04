/*
 * @Author: kaix
 * @Date: 2023-12-15 16:14:54
 * @LastEditTime: 2025-07-04 14:20:48
 * @LastEditors: kaix
 * @Description:
 */
import isObject from 'lodash/isObject'
import { computed, watch, ref, watchEffect } from 'vue'

/**
 * @description:
 * @param {*} chartConfig
 * @return {*}
 */
const toolTipFn = (chartConfig, chartRef, type: 'h' | 'v' = 'v', use: boolean = true) => {
  if (use) {
    const tooltip = chartConfig.option.tooltip
    const intervalTimer = ref(null)
    const dataIndex = ref(0)
    // 图表的 类型 h 代表横向 v  代表纵向
    // console.log('type', type)
    const yAxis = type === 'v' ? chartConfig.option.yAxis : chartConfig.option.xAxis
    // 轮播动画
    watch(
      () => [tooltip.isToolTipInterval, tooltip.toolTipIntervalTime, tooltip.show],
      ([isInterval, intervalTime, show]) => {
        if (isInterval && show) {
          const length = chartConfig.option.dataset.source.length ?? 0
          const intervalTooltip = () => {
            intervalTimer.value = setInterval(() => {
              chartRef.value?.chart.dispatchAction({
                type: 'downplay',
                seriesIndex: 0 //serieIndex的索引值   可以触发多个
              })
              chartRef.value?.chart.dispatchAction({
                type: 'highlight',
                seriesIndex: 0,
                dataIndex: dataIndex.value
              })
              chartRef.value?.chart.dispatchAction({
                type: 'showTip',
                seriesIndex: 0,
                dataIndex: dataIndex.value
              })
              dataIndex.value = (dataIndex.value + 1) % length
              // if (dataIndex.value === length - 1) {
              //   dataIndex.value = 0
              // } else {
              //   dataIndex.value++
              // }
            }, intervalTime)
          }
          intervalTimer && clearInterval(intervalTimer.value)
          intervalTooltip()
          watchEffect(() => {
            chartRef.value?.chart.on('mouseover', () => {
              intervalTimer && clearInterval(intervalTimer.value)
              intervalTimer.value = 0
            })
            chartRef.value?.chart.on('mouseout', () => {
              !intervalTimer.value && intervalTooltip()
            })
          })
        } else {
          intervalTimer && clearInterval(intervalTimer.value)
          intervalTimer.value = 0
          dataIndex.value = 0
          chartRef.value?.chart.dispatchAction({
            type: 'downplay',
            seriesIndex: 0 //serieIndex的索引值   可以触发多个
          })
          chartRef.value?.chart.dispatchAction({
            type: 'showTip',
            seriesIndex: 0,
            dataIndex: -1
          })
        }
      },
      {
        immediate: true
      }
    )
    // 提示文字的 单位
    watch(
      () => [tooltip.showUnit, tooltip.isNearYAxis, tooltip.unit, yAxis],
      ([show, isNearYAxis, unit]) => {
        if (!yAxis) {
          // 没有Y轴的图表就不执行
          return
        }
        if (show) {
          // 根据轴的单位展示 单位
          if (isNearYAxis) {
            // const [name, name2] = Array.isArray(yAxis) ? yAxis.map(it => it.name) : [yAxis.name]
            const yAxisNames = Array.isArray(yAxis) ? yAxis.map(it => it.name) : [yAxis.name]
            /**
             * @description: 这块的逻辑需要改进 to
             * todo 应该需要匹配每个轴的单位
             * @param {number} value
             * @return {*}
             */
            // chartConfig.option.tooltip.valueFormatter = (value: number | string): string => {
            //   return `${value} ${name}`
            // }

            const getValue = (data, seriesName) => {
              if (isObject(data)) {
                // 如果是对象，先尝试获取 seriesName 对应的值
                if (data[seriesName] !== undefined) {
                  return data[seriesName];
                }
                // 如果没有对应的值，则返回 value 属性
                return data.value !== undefined ? data.value : '-';
              }
              // 如果不是对象，直接返回数据值
              return data !== undefined ? data : '-';
            };

            chartConfig.option.tooltip.formatter = function (params) {
              // console.log(params, 'params');

              if (params.seriesType === 'pie') {
                let relVal = params.name
                const { marker, seriesName, data, color } = params
                const dot =
                  typeof color === 'string'
                    ? marker
                    : `<span style="display:inline-block;margin-right:4px;border-radius:10px;width:10px;height:10px;background-image:linear-gradient(${type === 'h' ? '90deg,' : ''
                    }${color.colorStops[0].color},${color.colorStops[1].color});"></span>`
                relVal = relVal + '<br/>' + dot + seriesName + '<span style="margin-left: 20px"></span>' + getValue(data, seriesName)

                return relVal
              } else {
                let relVal = params[0]?.name ?? ''
                for (let i = 0, l = params.length; i < l; i++) {
                  const { marker, seriesName, data, color } = params[i]
                  const dot =
                    typeof color === 'string'
                      ? marker
                      : `<span style="display:inline-block;margin-right:4px;border-radius:10px;width:10px;height:10px;background-image:linear-gradient(${type === 'h' ? '90deg,' : ''
                      }${color.colorStops[0].color},${color.colorStops[1].color});"></span>`
                  relVal =
                    relVal +
                    '<br/>' +
                    dot +
                    seriesName +
                    '<span style="margin-left: 20px"></span>' +
                    getValue(data, seriesName) +
                    yAxisNames[chartConfig.option.series[i]?.yAxisIndex ?? 0]
                }

                return relVal
              }
            }
          } else {
            // chartConfig.option.tooltip.valueFormatter = (value: number | string): string => {
            //   return `${value} ${unit}`
            // }

            const getValue = (data, seriesName) => {
              if (isObject(data)) {
                // 如果是对象，先尝试获取 seriesName 对应的值
                if (data[seriesName] !== undefined) {
                  return data[seriesName];
                }
                // 如果没有对应的值，则返回 value 属性
                return data.value !== undefined ? data.value : '-';
              }
              // 如果不是对象，直接返回数据值
              return data !== undefined ? data : '-';
            };

            chartConfig.option.tooltip.formatter = function (params) {
              if (params.seriesType === 'pie') {
                let relVal = params.name
                const { marker, seriesName, data, color } = params
                const dot =
                  typeof color === 'string'
                    ? marker
                    : `<span style="display:inline-block;margin-right:4px;border-radius:10px;width:10px;height:10px;background-image:linear-gradient(${type === 'h' ? '90deg,' : ''
                    }${color.colorStops[0].color},${color.colorStops[1].color});"></span>`
                relVal =
                  relVal + '<br/>' + dot + seriesName + '<span style="margin-left: 20px"></span>' + getValue(data, seriesName) + unit

                return relVal
              } else {
                let relVal = params[0]?.name ?? ''
                for (let i = 0, l = params.length; i < l; i++) {
                  const { marker, seriesName, data, color } = params[i]
                  const dot =
                    typeof color === 'string'
                      ? marker
                      : `<span style="display:inline-block;margin-right:4px;border-radius:10px;width:10px;height:10px;background-image:linear-gradient(${type === 'h' ? '90deg,' : ''
                      }${color.colorStops[0].color},${color.colorStops[1].color});"></span>`
                  relVal =
                    relVal +
                    '<br/>' +
                    dot +
                    seriesName +
                    '<span style="margin-left: 20px"></span>' +
                    getValue(data, seriesName) +
                    unit
                }
                return relVal
              }
            }
          }
        } else {
          // chartConfig.option.tooltip.valueFormatter = (value: number | string): string => {
          //   return `${value}`
          // }

          const getValue = (data, seriesName) => {
            if (isObject(data)) {
              // 如果是对象，先尝试获取 seriesName 对应的值
              if (data[seriesName] !== undefined) {
                return data[seriesName];
              }
              // 如果没有对应的值，则返回 value 属性
              return data.value !== undefined ? data.value : '-';
            }
            // 如果不是对象，直接返回数据值
            return data !== undefined ? data : '-';
          };

          chartConfig.option.tooltip.formatter = function (params) {
            if (params.seriesType === 'pie') {
              let relVal = params.name
              const { marker, seriesName, data, color } = params
              const dot =
                typeof color === 'string'
                  ? marker
                  : `<span style="display:inline-block;margin-right:4px;border-radius:10px;width:10px;height:10px;background-image:linear-gradient(${type === 'h' ? '90deg,' : ''
                  }${color.colorStops[0].color},${color.colorStops[1].color});"></span>`
              relVal = relVal + '<br/>' + dot + seriesName + '<span style="margin-left: 20px"></span>' + getValue(data, seriesName)

              return relVal
            } else {
              let relVal = params[0]?.name ?? ''
              for (let i = 0, l = params.length; i < l; i++) {
                const { marker, seriesName, data, color } = params[i]
                const dot =
                  typeof color === 'string'
                    ? marker
                    : `<span style="display:inline-block;margin-right:4px;border-radius:10px;width:10px;height:10px;background-image:linear-gradient(${type === 'h' ? '90deg,' : ''
                    }${color.colorStops[0].color},${color.colorStops[1].color});"></span>`
                relVal =
                  relVal +
                  '<br/>' +
                  dot +
                  seriesName +
                  '<span style="margin-left: 20px"></span>' +
                  getValue(data, seriesName)
              }
              return relVal
            }
          }
        }
      },
      {
        immediate: true,
        deep: true
      }
    )
  }
}

export const useTooltipWatch = () => {
  return {
    toolTipFn
  }
}
