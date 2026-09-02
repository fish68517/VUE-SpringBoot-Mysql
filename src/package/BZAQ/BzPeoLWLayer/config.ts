/*
 * @Author: kaix
 * @Date: 2023-03-22 10:25:24
 * @LastEditTime: 2025-02-20 16:21:18
 * @LastEditors: wangcong
 * @Description:
 */
import { PublicConfigClass, echartOptionProfixHandle } from '@/package/public/index'
import { BzPeoLWLayer } from './index'
import { CreateComponentType } from '@/package/index.d'
import cloneDeep from 'lodash/cloneDeep'
import dataJson from './data.json'
import { useLoadOption } from '@/package/config/useChartOption'
import { chartInitNewAttrConfig } from '@/package/config/const'
const { useTitle, useGrid, useXAxis, useYAxis, useLegend, useTooltip, useColor, useDataZoom } = useLoadOption()

export const option = {
  // animation: true,
  // color: useColor([{
  //   colorStops: [
  //     { offset: 0, color: "#316EF4" },
  //     { offset: 1, color: "#4C42DB" },
  //   ],
  //   type: "linear",
  //   x: 0,
  //   x2: 0,
  //   y: 0,
  //   y2: 1
  // }]),
  // grid: useGrid(),
  // title: useTitle(),
  // tooltip: useTooltip(),
  // xAxis: useXAxis({ data: [] }),
  // yAxis: useYAxis(),
  // legend: useLegend(),
  // dataZoom: useDataZoom(),
  // // 是否展示 dataZoom
  // showDataZoom: false,
  // dataset: { ...dataJson },
  // series: [seriesItem],
  // useSeriesColor: false,
  // allDatas: [],
  // datasetParamsMaps: {
  //   interactiveParams: ''
  // }
}


export default class Config extends PublicConfigClass implements CreateComponentType {
  public key = BzPeoLWLayer.key
  public chartConfig = cloneDeep(BzPeoLWLayer)
  public attr = { ...BzPeoLWLayer, w: 100, h: 100, zIndex: -1 }
  public option = cloneDeep(option)
  // 图表配置项
  // public option = echartOptionProfixHandle(option, includes)
  // public newAttr = {
  //   ...chartInitNewAttrConfig,
  //   themeColor: {
  //     useGlobalColor: false,
  //     selfTheme: {
  //       color: ['#04bcfa', '#0454cb', '#056ff1', '#47dea2', '#16b8d6', '#f1b736'],
  //       value: 'technology'
  //     }
  //   }
  // }
}
