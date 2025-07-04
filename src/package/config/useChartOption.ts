/*
 * @Author: kaix
 * @Date: 2023-07-25 10:00:39
 * @LastEditTime: 2025-03-18 15:20:38
 * @LastEditors: wangcong
 * @Description: echart 图表的默认配置项 通过hooks
 */
import merge from 'lodash/merge'

const useTitle = (title?) => {
  const _title = {
    show: false,
    text: '',
    subtext: '',
    textStyle: {
      color: '#D78400',
      fontWeight: 'bold',
      fontFamily: 'Microsoft YaHei',
      lineHeight: 40,
      rich: {
        title: {
          padding: [0, 0],
          fontSize: 24,
          fontWeight: 'normal',
          fontFamily: 'Microsoft YaHei'
        }
      }
    },
    subtextStyle: {
      show: true,
      color: '#fff',
      fontSize: 14
    },
    left: 'left',
    top: 'top',
    z: 999 // 保证标题位于画布最上层
  }
  return merge(_title, title)
}

const useColor = (color?) => {
  return color || ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de', '#3ba272', '#fc8452', '#9a60b4', '#ea7ccc']
}

const useGrid = (grid?) => {
  const _grid = {
    left: 5,
    right: 5,
    bottom: 5,
    top: 10,
    containLabel: true
  }
  return merge(_grid, grid)
}

const useXAxis = (xAxis?) => {
  const _xAxis = {
    show: true,
    name: '',
    nameGap: 10,
    position: 'bottom',
    nameTextStyle: {
      color: '#C2DDFC',
      fontSize: 12,
      padding: [0, 0, 0, 0],
      verticalAlign: 'top'
    },
    type: 'category',
    axisTick: {
      show: false
    },
    axisLine: {
      show: true,
      onZero: false,
      lineStyle: {
        color: '#136B6F',
        width: 1
      }
    },
    axisLabel: {
      fontFamily: 'DINPro',
      show: true,
      rotate: 0,
      interval: 0,
      fontSize: 14,
      color: '#0D8DA0',
      padding: [4, 0, 0, 0],
      margin: 8,
      formatter: (value: any) => {
        return value
      },
      fontWeight: 'bolder'
    },
    // 网格线
    splitLine: {
      show: false,
      lineStyle: {
        color: '#484753',
        width: 1,
        type: 'solid'
      }
    }
  }
  return merge(_xAxis, xAxis)
}

const useYAxis = (yAxis?) => {
  const _yAxis = {
    show: true,
    position: 'left',
    type: 'value',
    name: '',
    nameTextStyle: {
      color: '#C2DDFC',
      fontSize: 12,
      align: 'center',
      padding: [-10, 35, 0, 0]
    },
    triggerEvent: true,
    splitLine: {
      show: true,
      lineStyle: {
        color: 'rgba(18, 91, 95, 0.4)',
        type: 'solid'
      }
    },
    axisTick: {
      show: false
    },
    axisLine: {
      show: false,
      lineStyle: {
        color: '#C6C6C6',
        width: 1
      }
    },
    axisLabel: {
      show: true,
      fontSize: 14,
      color: '#0D8DA0',
      fontFamily: 'DINPro',
      fontWeight: 'bolder'
    },
    min: 0,
    max: 'dataMax',
    maxNum: 'dataMax', //y轴最大值，显示作用，与max区分
    splitNumber: 5,
    mySplitNumber: 5, //y轴标签数量，显示作用，与splitNumber区分
    isScaleSelfadaption: ['1'], //刻度自适应 ['1']自适应 [null]非自适应
    scaleType: '1', //刻度模式，'1'数量强制,'2'步长强制
    minInterval: 1,
    myMinInterval: 100 //y轴步长，显示作用，与minInterval区分
  }
  return merge(_yAxis, yAxis)
}

const useAnimation = (animation?) => {
  const _animation = {
    animationLoop: false,
    animationDuration: 1000,
    cycleInterval: 3000
  }
  return merge(_animation, animation)
}

const useTooltip = (tooltip?) => {
  const _tooltip = {
    show: true,
    // 是否展示单位 自定义属性
    showUnit: true,
    // 是否根据轴系列的单位显示单位
    isNearYAxis: true,
    unit: '',
    trigger: 'axis',
    axisPointer: {
      type: 'shadow',
      lineStyle: {
        color: '#fff'
      }
    },
    backgroundColor: 'rgba(0, 33, 59, 0.8)',
    borderColor: 'rgba(24, 174, 236, 1)',
    padding: [5, 10],
    textStyle: {
      fontSize: 12,
      color: '#fff'
    },
    extraCssText: 'box-shadow: 0 0 5px rgba(0,0,0,0.3)',
    // 是否开启toolTip轮播
    isToolTipInterval: false,
    // toolTip轮播时间
    toolTipIntervalTime: 5000
  }
  return merge(_tooltip, tooltip)
}

const useLegend = (legend?) => {
  const _legend = {
    shows: false,
    legendType: 'auto',
    show: false,
    align: 'left',
    left: 'left',
    top: 'top',
    icon: 'circle',
    itemGap: 16,
    itemWidth: 10,
    itemHeight: 10,
    padding: [0, 0],
    orient: 'horizontal',
    textStyle: {
      color: '#a8aab0',
      fontWeight: 'normal',
      fontFamily: '微软雅黑',
      fontSize: 12
    },
    formatter: {}
  }
  return merge(_legend, legend)
}

// 缩放滚动条
const useDataZoom = (dataZoom?) => {
  const _dataZoom = [

    // 滑动条
    {
      show: false, // 是否显示滑动条
      // 是否开启轮播
      isOpenInterval: false,
      intervalTime: 3000,
      scrollNum: 3,
      type: 'slider',
      height: 15,
      // 是否锁定选择区域（或叫做数据窗口）的大小。如果设置为 true 则锁定选择区域的大小，也就是说，只能平移，不能缩放。
      zoomLock: false,
      // 是否显示detail，即拖拽时候显示详细数值信息
      showDetail: true,
      brushSelect: false,
      backgroundColor: 'rgba(47,69,84,0)',
      // 滚动条颜色
      fillerColor: 'rgba(167,183,204,0.4)',
      borderColor: '#d2dbee',
      // 让 endValue 失效
      end: 100,
      startValue: 0, // 从头开始。
      endValue: 2 // 一次性展示5个。
    },
    {
      type: 'inside',
      zoomOnMouseWheel: false,
      moveOnMouseWheel: true
    },
  ]
  return merge(_dataZoom, dataZoom)
}

// option全局默认配置
export const useLoadOption = () => {
  return {
    useTitle,
    useAnimation,
    useGrid,
    useXAxis,
    useYAxis,
    useTooltip,
    useLegend,
    useColor,
    useDataZoom
  }
}
