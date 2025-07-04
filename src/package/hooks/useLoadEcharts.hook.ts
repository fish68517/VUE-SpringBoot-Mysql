/*
 * @Author: kaix
 * @Date: 2023-03-13 16:41:05
 * @LastEditTime: 2023-11-17 10:28:09
 * @LastEditors: kaix
 * @Description:
 */
import { App } from 'vue'

// 引入 vue-echarts 二次封装

import * as echarts from 'echarts'
import ECharts from 'vue-echarts'
import componentConfig from '../config/componentConfig'

// import 'uno.css'
window['echarts'] = echarts

window['componentConfig'] = componentConfig

export const useLoadEcharts = (app: App) => {
  app.component('VChart', ECharts)
}
