/*
 * @Author: kaix
 * @Date: 2023-03-17 10:00:53
 * @LastEditTime: 2023-11-21 10:21:18
 * @LastEditors: wangcong
 * @Description: 默认配置参数
 */
export const groupTitle = '分组'

// 图表初始配置(px)
export const chartInitConfig = {
  x: 50,
  y: 50,
  w: 500,
  h: 300,
  // 不建议动 offset
  offsetX: 0,
  offsetY: 0
}

export const chartInitNewAttrConfig = {
  sortStyle: '按原始数据',
    emptyHide: false,
    borderPosition: '顶部',
    themeColor: {
      useGlobalColor: true,
      selfTheme: {
        color: ['#04bcfa', '#0454cb', '#056ff1', '#47dea2', '#16b8d6', '#f1b736'],
        value: 'technology'
      }
    },
    xAxisLabelType: 'all',
    xAxisLabelCount: 5,
    showXaxisName: true,
    showYaxisName: true,
    legendPostion: '顶部居左',
    legendPadding: 0,
}

// 间距的最大值
export const spaceMax = 200
