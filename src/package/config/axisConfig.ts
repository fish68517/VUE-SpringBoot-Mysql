/*
 * @Author: kaix
 * @Date: 2023-04-19 10:36:40
 * @LastEditTime: 2023-04-19 10:36:44
 * @LastEditors: kaix
 * @Description: 
 */
export const axisConfig = {
    // X轴位置
    xposition: [
      {
        label: '顶部',
        value: 'top'
      },
      {
        label: '底部',
        value: 'bottom'
      }
    ],
    // Y轴位置
    yposition: [
      {
        label: '左侧',
        value: 'left'
      },
      {
        label: '右侧',
        value: 'right'
      }
    ],
    // 线条
    splitLint: {
      lineStyle: {
        type: [
          {
            label: '实线',
            value: 'solid'
          },
          {
            label: '虚线',
            value: 'dashed'
          },
          {
            label: '点线',
            value: 'dotted'
          }
        ]
      }
    },
    // 视觉映射
    visualMap: {
      orient: [
        {
          label: '竖直',
          value: 'vertical'
        },
        {
          label: '水平',
          value: 'horizontal'
        }
      ]
    }
  }