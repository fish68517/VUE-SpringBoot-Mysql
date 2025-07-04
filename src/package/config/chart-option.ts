/**
 * 导出 chart 默认属性配置
 */
export const defaultOption: any = {
  title: {
    text: '',
    subtext: '',
    show: false,
    textStyle: {
      color: '#D78400',
      fontSize: 12,
      fontWeight: 'bold',
      fontFamily: '微软雅黑',
      lineHeight: 40
    },
    subtextStyle: {
      show: true,
      color: '#fff',
      fontSize: 14
    },
    left: 'left',
    top: 'top'
  },
  grid: {
    left: '30',
    right: '30',
    bottom: '30',
    top: '30',
    containLabel: true
  },
  xAxis: {
    name: '',
    nameGap: 1,
    nameTextStyle: {
      color: '#C2DDFC',
      fontSize: 12,
      padding: 10
    },
    type: 'category',
    axisTick: {
      show: true
    },
    axisLine: {
      show: true,
      lineStyle: {
        color: '#C6C6C6',
        width: 1
      }
    },
    axisLabel: {
      fontFamily: '微软雅黑',
      show: true,
      rotate: 0,
      interval: 0,
      fontSize: 12,
      color: '#C2DDFC',
      padding: [4, 0, 0, 0],
      margin: 8,
      formatter: (value: any) => {
        return value
      }
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
  },
  tooltip: {
    show: true,
    trigger: 'axis',
    axisPointer: {
      type: 'line',
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
    extraCssText: 'box-shadow: 0 0 5px rgba(0,0,0,0.3)'
  },
  yAxis: {
    name: '',
    nameTextStyle: {
      color: '#C2DDFC',
      fontSize: 12,
      align: 'center',
      padding: [0, 35, 0, 0]
    },
    type: 'value',
    triggerEvent: true,
    splitLine: {
      show: true,
      lineStyle: {
        color: 'rgba(255, 255, 255, 0.15)',
        type: 'dashed'
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
      fontSize: 12,
      color: '#C2DDFC',
      fontFamily: '微软雅黑'
    },
    min: 0,
    max: 'dataMax',
    splitNumber: 5
  },
  legend: {
    show: false,
    left: 'left',
    top: 'top',
    icon: 'circle',
    itemGap: 16,
    itemWidth: 10,
    itemHeight: 10,
    orient: 'horizontal',
    textStyle: {
      color: '#a8aab0',
      fontStyle: 'normal',
      fontFamily: '微软雅黑',
      fontSize: 12
    }
  },
  // 内置主题配色系列
  theme: 3,
  // 是否开启toolTip轮播
  isToolTipInterval: false,
  // toolTip轮播时间
  toolTipIntervalTime: 5000,
  // 是否开启轮播
  isSeriesScorll: true,
  // 轮播的间隔时间
  scorllTimes: 1500,
  // 超过多少的数量轮播
  dataZoomNum: 4
}
