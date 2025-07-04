/*
 * @Author: kaix
 * @Date: 2023-03-22 10:25:24
 * @LastEditTime: 2025-02-20 16:21:18
 * @LastEditors: wangcong
 * @Description:
 */
import { PublicConfigClass, echartOptionProfixHandle } from '@/package/public/index'
import { BarCommon } from './index'
import { CreateComponentType } from '@/package/index.d'
import cloneDeep from 'lodash/cloneDeep'
import dataJson from './data.json'
import { useLoadOption } from '@/package/config/useChartOption'
import { chartInitNewAttrConfig } from '@/package/config/const'
import { getUUID } from '@/utils'
const { useTitle, useGrid, useXAxis, useYAxis, useLegend, useTooltip, useColor, useDataZoom } = useLoadOption()
export const includes = ['legend', 'xAxis', 'yAxis', 'grid']

// barCategoryGap(柱外间距) 会影响 barWidth
export const seriesItem = {
  type: 'bar',
  barGap: '0%',
  barCategoryGap: '75%',
  label: {
    show: false,
    position: 'top',
    color: '#fff',
    fontSize: 12
  },
  itemStyle: {
    borderRadius: [0, 0, 0, 0],
    color: ''
  },
  showBackground: false,
  backgroundStyle: {
    color: '#e9e9e90d'
  },
  showMarkPoint: false,
  markMax: true,
  markMin: true,
  markPoint: {
    data: []
  },
  markLineSetting: {
    show: false,
    type: 'average',
    value: 0,
    showOverBaseColor: false,
    useGradient: false,
    color: ['#316EF4', '#316EF4']
  },
  markLine: {
    data: [],
    label: {
      show: true,
      position: 'end',
      fontWeight: 'normal',
      fontFamily: 'Microsoft YaHei',
      fontSize: 12,
      color: '#fff',
      distance: -25
    },
    lineStyle: {
      width: 1,
      type: 'dashed',
      color: '#316EF4'
    }
  },
  data: []
}
export const option = {
  animation: true,
  color: useColor([{
    colorStops: [
      { offset: 0, color: "#316EF4" },
      { offset: 1, color: "#4C42DB" },
    ],
    type: "linear",
    x: 0,
    x2: 0,
    y: 0,
    y2: 1
  }]),
  grid: useGrid(),
  title: useTitle(),
  tooltip: useTooltip(),
  xAxis: useXAxis({ data: [] }),
  yAxis: useYAxis(),
  legend: useLegend(),
  dataZoom: useDataZoom(),
  // 是否展示 dataZoom
  showDataZoom: false,
  dataset: { ...dataJson },
  series: [seriesItem],
  useSeriesColor: false,
  allDatas: [],
  datasetParamsMaps: {
    interactiveParams: ''
  }
}

export default class Config extends PublicConfigClass implements CreateComponentType {
  public key = BarCommon.key
  public chartConfig = cloneDeep(BarCommon)
  // 图表配置项
  public option = echartOptionProfixHandle(option, includes)
  public newAttr = {
    ...chartInitNewAttrConfig,
    themeColor: {
      useGlobalColor: false,
      selfTheme: {
        color: ['#04bcfa', '#0454cb', '#056ff1', '#47dea2', '#16b8d6', '#f1b736'],
        value: 'technology'
      }
    }
  }
}
